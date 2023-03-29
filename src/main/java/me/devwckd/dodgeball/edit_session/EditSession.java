package me.devwckd.dodgeball.edit_session;

import lombok.Data;
import me.devwckd.dodgeball.utils.Cuboid;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class EditSession {

    private final UUID editor;
    private final World world;

    private String id;
    private String displayName;

    private Location lobbySpawn;

    private Cuboid middleLine;

    private Location redTeamSpawn;
    private Location redBallSpawn;
    private Location blueTeamSpawn;
    private Location blueBallSpawn;

    private List<Location> middleBallSpawns;

    public void addBallSpawn(Location location) {
        if(middleBallSpawns == null) {
            middleBallSpawns = new ArrayList<>();
        }
        middleBallSpawns.add(location);
    }

}
