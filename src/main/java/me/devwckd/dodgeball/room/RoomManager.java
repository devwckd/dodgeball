package me.devwckd.dodgeball.room;

import com.mongodb.MongoException;
import me.devwckd.dodgeball.arena.ArenaManager;
import me.devwckd.dodgeball.context.DodgeballContext;
import me.devwckd.dodgeball.exception.RoomException;
import me.devwckd.dodgeball.game.Game;
import me.devwckd.dodgeball.states.AbstractState;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RoomManager {

    private final JavaPlugin plugin;
    private final RoomCache roomCache;
    private final RoomDefinitionRepository roomDefinitionRepository;
    private final ArenaManager arenaManager;

    public RoomManager(
      final @NotNull JavaPlugin plugin,
      final @NotNull RoomCache roomCache,
      final @NotNull RoomDefinitionRepository roomDefinitionRepository,
      final @NotNull ArenaManager arenaManager
    ) {
        this.plugin = plugin;
        this.roomCache = roomCache;
        this.roomDefinitionRepository = roomDefinitionRepository;
        this.arenaManager = arenaManager;

        for (RoomDefinition roomDefinition : roomDefinitionRepository.find()) {
            final Room room;
            try {
                room = roomDefinition.toRoom(plugin, arenaManager);
            } catch (RoomException e) {
                plugin.getLogger().warning(e.getMessage());
                continue;
            }

            room.ensureWorldCreated(plugin);
            try {
                roomCache.insert(room);
            } catch (RoomException e) {
                throw new RuntimeException(e);
            }
            room.start();
        }
    }

    public void insert(final @NotNull Room room) throws RoomException {
        try {
            roomDefinitionRepository.insert(room);
        } catch (MongoException e) {
            throw new RoomException("A room with this id already exists.");
        }
        roomCache.insert(room);

        if (room.getWorld() != null) {
            room.ensureWorldCreated(plugin);
        }
    }

    public @Nullable Room findById(final @NotNull UUID id) throws RoomException {
        final Room room;
        if ((room = roomCache.findById(id)) != null) {
            return room;
        }

        final RoomDefinition roomDefinition;
        if ((roomDefinition = roomDefinitionRepository.findById(id)) != null) {
            return roomDefinition.toRoom(plugin, arenaManager);
        }

        return null;
    }


    public @Nullable Room findCachedById(final @NotNull UUID id) {
        return roomCache.findById(id);
    }

    public @Nullable Room findByWorldName(final @NotNull String worldName) {
        return roomCache.findByWorldName(worldName);
    }

    public @Nullable Room findByWorld(final @NotNull World world) {
        return roomCache.findByWorld(world);
    }

    public @Nullable Room findByPlayer(final @NotNull Player player) {
        return roomCache.findAll().stream().filter(room -> {
            final Game<DodgeballContext> game = room.getGame();
            if (!game.isStarted()) return false;
            if (game.getCurrentState() instanceof AbstractState abstractState) {
                return abstractState.hasJoined(player);
            }
            return false;
        }).findFirst().orElse(null);
    }

    public void deleteById(final @NotNull UUID id) {
        final Room room = roomCache.deleteById(id);
        if(room == null) return;

        if(room.getWorld() != null) {
            plugin.getServer().unloadWorld(room.getWorld(), false);
            room.getWorld().getWorldFolder().delete();
        }

        roomDefinitionRepository.deleteById(id);
    }

    public @NotNull List<Room> getSortedRoomList() {
        return roomCache.findAll().stream().sorted(
          (room1, room2) -> {
              final int result = room1.getStage().compareTo(room2.getStage());
              if(result == 0) {
                  return -Integer.compare(
                    room1.getCastedState(AbstractState.class).getPlayerCount(),
                    room2.getCastedState(AbstractState.class).getPlayerCount()
                  );
              }

              return result;
          }
        ).collect(Collectors.toList());
    }

}
