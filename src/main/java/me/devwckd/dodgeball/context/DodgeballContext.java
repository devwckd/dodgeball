package me.devwckd.dodgeball.context;

import lombok.Data;
import me.devwckd.dodgeball.arena.Arena;
import me.devwckd.dodgeball.room.Room;
import org.bukkit.plugin.java.JavaPlugin;

@Data
public class DodgeballContext {

    private final Arena arena;
    private final Room room;

}
