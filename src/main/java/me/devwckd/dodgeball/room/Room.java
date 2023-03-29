package me.devwckd.dodgeball.room;

import lombok.Data;
import me.devwckd.dodgeball.arena.Arena;
import me.devwckd.dodgeball.context.DodgeballContext;
import me.devwckd.dodgeball.game.Game;
import me.devwckd.dodgeball.game.State;
import me.devwckd.dodgeball.states.EndingState;
import me.devwckd.dodgeball.states.PlayingState;
import me.devwckd.dodgeball.states.StartingState;
import me.devwckd.dodgeball.states.WaitingState;
import me.devwckd.dodgeball.utils.FileUtils;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.UUID;

@Data
public class Room {

    private final UUID id;
    private final Arena arena;
    private final Game<DodgeballContext> game;
    private World world;

    public void start() {
        if (game.isStarted()) {
            game.end();
        }
        game.start(new WaitingState(), new DodgeballContext(arena, this));
    }

    public void ensureWorldCreated(final @NotNull JavaPlugin plugin) {
        if (world != null) return;

        final File worldFile = new File(plugin.getDataFolder(), "../../" + id);
        if (!worldFile.exists()) {
            FileUtils.copyDir(new File(plugin.getDataFolder(), "/maps/" + arena.getMapFolderName()), worldFile);
        }

        world = plugin.getServer().createWorld(new WorldCreator(id.toString()));
    }

    public @NotNull RoomDefinition toDefinition() {
        return new RoomDefinition(id, arena.getId());
    }

    public @NotNull RoomStage getStage() {
        final State<DodgeballContext> state = game.getCurrentState();
        if (state instanceof WaitingState) {
            return RoomStage.WAITING;
        } else if (state instanceof StartingState) {
            return RoomStage.STARTING;
        } else if (state instanceof PlayingState) {
            return RoomStage.PLAYING;
        } else if (state instanceof EndingState) {
            return RoomStage.ENDING;
        } else {
            return RoomStage.UNKNOWN;
        }
    }

    public <T extends State<DodgeballContext>> @NotNull T getCastedState(Class<T> clazz) {
        final State<DodgeballContext> currentState = game.getCurrentState();
        if(!game.isStarted() || currentState == null) {
            throw new RuntimeException("Tried to get a casted state of a game that hasn't been started.");
        }
        if(!clazz.isAssignableFrom(currentState.getClass())) {
            throw new RuntimeException(currentState.getClass().getName() + " is not a subtype of " + clazz.getName() + ".");
        }
        return ((T) currentState);
    }

}
