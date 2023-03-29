package me.devwckd.dodgeball.edit_session;

import lombok.RequiredArgsConstructor;
import me.devwckd.dodgeball.arena.Arena;
import me.devwckd.dodgeball.exception.EditSessionException;
import me.devwckd.dodgeball.generator.VoidGenerator;
import me.devwckd.dodgeball.utils.Cuboid;
import me.devwckd.dodgeball.utils.FileUtils;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EditSessionManager {

    private final JavaPlugin plugin;
    private final Map<UUID, EditSession> editSessionsByPlayerId = new HashMap<>();
    private final Map<String, EditSession> editSessionsByWorldName = new HashMap<>();

    private static <T> T requireNonNull(T object, String message) throws EditSessionException {
        if (object != null) return object;
        throw new EditSessionException(message);
    }

    private static <T extends Collection<?>> T requireNotEmpty(T collection, String message) throws EditSessionException {
        final T nonNullCollection = requireNonNull(collection, message);
        if (!nonNullCollection.isEmpty()) return collection;
        throw new EditSessionException(message);
    }

    public @Nullable EditSession findByPlayer(final @NotNull Player player) {
        return editSessionsByPlayerId.get(player.getUniqueId());
    }

    public @NotNull EditSession create(final @NotNull Player player) {
        final String worldName = "editsession-" + player.getName();

        World world;
        if ((world = plugin.getServer().getWorld(worldName)) == null) {
            world = plugin.getServer().createWorld(new WorldCreator(worldName).generator(VoidGenerator.INSTANCE));
        }
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setTime(2000);

        final EditSession editSession = new EditSession(player.getUniqueId(), world);
        editSessionsByPlayerId.put(player.getUniqueId(), editSession);
        editSessionsByWorldName.put(worldName.toLowerCase(), editSession);
        return editSession;
    }

    public @NotNull Arena finish(final @NotNull EditSession editSession) throws EditSessionException {
        final String id = requireNonNull(editSession.getId(), "You must set the arena's ID");
        final String displayName = editSession.getDisplayName() == null ? id : editSession.getDisplayName();
        final Location lobbySpawn = requireNonNull(editSession.getLobbySpawn(), "You must set the arena's lobby spawn");
        final Cuboid middleLine = requireNonNull(editSession.getMiddleLine(), "You must set the arena's middle line");
        final Location redTeamSpawn = requireNonNull(editSession.getRedTeamSpawn(), "You must set the arena's red team spawn");
        final Location redBallSpawn = requireNonNull(editSession.getRedBallSpawn(), "You must set the arena's red ball spawn");
        final Location blueTeamSpawn = requireNonNull(editSession.getBlueTeamSpawn(), "You must set the arena's blue team spawn");
        final Location blueBallSpawn = requireNonNull(editSession.getBlueBallSpawn(), "You must set the arena's blue ball spawn");
        final List<Location> middleBallSpawns = requireNotEmpty(editSession.getMiddleBallSpawns(), "You must set at least one middle ball spawn");

        final File worldFolder = editSession.getWorld().getWorldFolder();
        plugin.getServer().unloadWorld(editSession.getWorld(), true);

        final File mapsFolder = new File(plugin.getDataFolder(), "maps");
        final File mapFolder = new File(mapsFolder, id);
        if (!mapFolder.exists()) {
            mapFolder.mkdirs();
        }

        FileUtils.copyDir(worldFolder, mapFolder);
        new File(mapFolder, "uid.dat").delete();
        new File(mapFolder, "session.lock").delete();

        worldFolder.deleteOnExit();

        editSessionsByPlayerId.remove(editSession.getEditor());
        editSessionsByWorldName.remove(editSession.getWorld().getName().toLowerCase());

        return new Arena(
          id,
          displayName,
          lobbySpawn.toVector(),
          id,
          middleLine,
          redTeamSpawn.toVector(),
          redBallSpawn.toVector(),
          blueTeamSpawn.toVector(),
          blueBallSpawn.toVector(),
          middleBallSpawns.stream().map(Location::toVector).collect(Collectors.toList())
        );
    }

    public @Nullable EditSession findByWorldName(final @NotNull String worldName) {
        return editSessionsByWorldName.get(worldName.toLowerCase());
    }

}
