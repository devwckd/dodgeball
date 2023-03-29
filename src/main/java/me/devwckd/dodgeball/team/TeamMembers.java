package me.devwckd.dodgeball.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class TeamMembers {

    private final Team team;
    private final Set<Player> players = new HashSet<>();

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public int getPlayerCount() {
        return players.size();
    }

}
