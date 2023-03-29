package me.devwckd.dodgeball.listener;

import lombok.RequiredArgsConstructor;
import me.devwckd.dodgeball.room.Room;
import me.devwckd.dodgeball.room.RoomManager;
import me.devwckd.dodgeball.states.AbstractState;
import me.devwckd.dodgeball.states.PlayingState;
import me.devwckd.dodgeball.team.Team;
import me.devwckd.dodgeball.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@RequiredArgsConstructor
public class RoomListener implements Listener {

    private final RoomManager roomManager;

    @EventHandler
    public void onQuit(final @NotNull PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final Room room = roomManager.findByPlayer(player);
        if (room == null) return;
        final AbstractState castedState = room.getCastedState(AbstractState.class);
        castedState.quit(player);
    }

    @EventHandler
    public void onDamage(final @NotNull EntityDamageEvent event) {
        final World world = event.getEntity().getWorld();
        final Room room = roomManager.findByWorld(world);
        if (room == null) return;
        event.setDamage(0);
        event.setCancelled(true);

        if (event.getEntity() instanceof Player player && event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            if (room.getGame().getCurrentState() instanceof PlayingState playingState) {
                final Team team = playingState.getTeam(player);
                if (team == Team.BLUE) {
                    player.teleport(room.getArena().getBlueTeamSpawn().toLocation(world));
                } else {
                    player.teleport(room.getArena().getRedTeamSpawn().toLocation(world));
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(final @NotNull ProjectileHitEvent event) {
        final World world = event.getEntity().getWorld();

        final Room room = roomManager.findByWorld(world);
        if (room == null) return;

        final Projectile entity = event.getEntity();
        final ProjectileSource shooter = entity.getShooter();
        if (!(shooter instanceof final Player player)) return;

        if (room.getGame().getCurrentState() instanceof PlayingState playingState) {
            final Entity hitEntity = event.getHitEntity();
            if (hitEntity instanceof final Player killed) {
                final Location location = killed.getLocation();
                playingState.kill(player, killed);
                ItemUtils.spawnSnowball(location);
                return;
            }

            final Block hitBlock = event.getHitBlock();
            if (event.getHitBlockFace() == BlockFace.UP) {
                ItemUtils.spawnSnowball(hitBlock.getLocation().add(0.5, 1, 0.5));
                return;
            }

            final Vector blueBallSpawn = room.getArena().getBlueBallSpawn();
            final Vector redBallSpawn = room.getArena().getRedBallSpawn();
            final double distanceFromBlue = blueBallSpawn.distanceSquared(hitBlock.getLocation().toVector());
            final double distanceFromRed = redBallSpawn.distanceSquared(hitBlock.getLocation().toVector());
            if (distanceFromBlue > distanceFromRed) {
                ItemUtils.spawnSnowball(redBallSpawn.toLocation(world));
            } else {
                ItemUtils.spawnSnowball(blueBallSpawn.toLocation(world));
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(final @NotNull CreatureSpawnEvent event) {
        final Room room = roomManager.findByWorld(Objects.requireNonNull(event.getLocation().getWorld()));
        if (room == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(final @NotNull FoodLevelChangeEvent event) {
        final Room room = roomManager.findByWorld(Objects.requireNonNull(event.getEntity().getWorld()));
        if (room == null) return;
        event.setCancelled(true);
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onPlayerMove(final @NotNull PlayerMoveEvent event) {
        final Room room = roomManager.findByWorld(Objects.requireNonNull(event.getTo().getWorld()));
        if (room == null) return;
        if (room.getGame().getCurrentState() instanceof PlayingState && room.getArena().getMiddleLine().intersects(event.getTo().toVector())) {
            event.setTo(event.getFrom());
        }
    }

    @EventHandler
    public void onRain(final @NotNull WeatherChangeEvent event) {
        final Room room = roomManager.findByWorld(Objects.requireNonNull(event.getWorld()));
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }

}
