package me.devwckd.dodgeball.states;

import com.google.common.collect.Iterators;
import fr.mrmicky.fastboard.FastBoard;
import me.devwckd.dodgeball.arena.Arena;
import me.devwckd.dodgeball.context.DodgeballContext;
import me.devwckd.dodgeball.game.StateResult;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class WaitingState extends AbstractJoinableState {

    private final Set<Player> players = new HashSet<>();
    private final Set<FastBoard> scoreboards = new HashSet<>();

    @Override
    public StateResult<DodgeballContext> start(DodgeballContext context) {
        for (Item item : context.getRoom().getWorld().getEntitiesByClass(Item.class)) {
            item.remove();
        }

        return StateResult.none();
    }

    @Override
    public StateResult<DodgeballContext> update(DodgeballContext context) {
        if(getPlayerCount() >= 2) {
            return StateResult.next(new StartingState(players));
        }

        return StateResult.none();
    }

    @Override
    public StateResult<DodgeballContext> stop(DodgeballContext context) {
        scoreboards.forEach(FastBoard::delete);
        scoreboards.clear();
        return StateResult.none();
    }

    @Override
    public int getPlayerCount() {
        return players.size();
    }

    @Override
    public Collection<Player> getPlayers() {
        return players;
    }

    @Override
    public void quit(Player player) {
        players.remove(player);
        scoreboards.removeIf(scoreboard -> {
            if(scoreboard.getPlayer().equals(player)) {
                scoreboard.delete();
                return true;
            }
            return false;
        });

        if(player.isOnline()) {
            player.teleport(Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation());
        }

        broadcastMessage("§c§l[!] §7" + player.getName() + " quit.");
    }

    @Override
    public boolean hasJoined(final @NotNull Player player) {
        return players.contains(player);
    }

    public void createScoreboard(Player player) {
        final FastBoard scoreboard = new FastBoard(player);
        scoreboard.updateTitle("§9§lDodgeball");
        scoreboard.updateLines(
          " ",
          " §fWelcome §e" + player.getName() + " ",
          " §fto Dodgeball! ",
          " ",
          " §cWaiting. ",
          " §e" + getPlayerCount() + "/20 §fplayers ",
          " "
        );
        scoreboards.add(scoreboard);
    }

    public void updateScoreboards() {
        scoreboards.forEach(scoreboard -> {
            scoreboard.updateLine(5, " §e" + getPlayerCount() + "/20 §fplayers ");
        });
    }

}
