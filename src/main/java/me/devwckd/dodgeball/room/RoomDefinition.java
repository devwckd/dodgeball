package me.devwckd.dodgeball.room;

import lombok.Data;
import me.devwckd.dodgeball.arena.Arena;
import me.devwckd.dodgeball.arena.ArenaManager;
import me.devwckd.dodgeball.exception.RoomException;
import me.devwckd.dodgeball.game.Game;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Data
public class RoomDefinition {

    private final UUID id;
    private final String arenaId;

    public @NotNull Room toRoom(final @NotNull JavaPlugin plugin, final @NotNull ArenaManager arenaManager) throws RoomException {
        final Arena arena = arenaManager.findById(arenaId);
        if(arena == null) {
            throw new RoomException("Room " + id + " uses an unknown arena ID.");
        }

        return new Room(id, arena, new Game<>(plugin));
    }

}
