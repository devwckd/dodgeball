package me.devwckd.dodgeball.states;

import org.bukkit.entity.Player;

public interface ScoreboardState {

    void updateScoreboards();
    void createScoreboard(Player player);

}
