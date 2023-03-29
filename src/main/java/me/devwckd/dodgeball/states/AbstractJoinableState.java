package me.devwckd.dodgeball.states;

import me.devwckd.dodgeball.arena.Arena;
import me.devwckd.dodgeball.context.DodgeballContext;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractJoinableState extends AbstractState implements ScoreboardState {

    public @NotNull JoinResult join(final @NotNull Player player) {
        if(getPlayerCount() >= 20) return AbstractJoinableState.JoinResult.FULL;
        if(hasJoined(player)) return AbstractJoinableState.JoinResult.ALREADY_JOINED;
        broadcastMessage("§a§l[!] §7" + player.getName() + " joined.");

        getPlayers().add(player);

        final DodgeballContext context = getGame().getContext();
        final World world = context.getRoom().getWorld();
        final Arena arena = context.getArena();

        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setFoodLevel(20);
        player.teleport(arena.getLobbySpawn().toLocation(world));
        updateScoreboards();
        createScoreboard(player);

        return AbstractJoinableState.JoinResult.SUCCESS;
    }

    public enum JoinResult {
        SUCCESS,
        FULL,
        ALREADY_JOINED
    }

}
