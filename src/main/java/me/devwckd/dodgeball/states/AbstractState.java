package me.devwckd.dodgeball.states;

import me.devwckd.dodgeball.context.DodgeballContext;
import me.devwckd.dodgeball.game.State;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class AbstractState extends State<DodgeballContext> implements ScoreboardState {

    public abstract boolean hasJoined(final @NotNull Player player);

    public abstract int getPlayerCount();

    public abstract Collection<Player> getPlayers();

    public abstract void quit(Player player);

    protected String seconds(int time) {
        return time + " " + (time == 1 ? "second" : "seconds");
    }

    protected void broadcastMessage(String message) {
        for (Player player : getPlayers()) {
            player.sendMessage(message);
        }
    }
}
