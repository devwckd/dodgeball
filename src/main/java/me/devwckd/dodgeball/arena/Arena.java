package me.devwckd.dodgeball.arena;

import lombok.Data;
import me.devwckd.dodgeball.utils.Cuboid;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.List;

@Data
public class Arena {

    private final String id;
    private final String displayName;

    private final Vector lobbySpawn;

    private final String mapFolderName;

    private final Cuboid middleLine;

    private final Vector redTeamSpawn;
    private final Vector redBallSpawn;
    private final Vector blueTeamSpawn;
    private final Vector blueBallSpawn;

    private final List<Vector> middleBallSpawns;

}
