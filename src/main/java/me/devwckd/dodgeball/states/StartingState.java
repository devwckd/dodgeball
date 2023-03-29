package me.devwckd.dodgeball.states;

import fr.mrmicky.fastboard.FastBoard;
import lombok.RequiredArgsConstructor;
import me.devwckd.dodgeball.arena.Arena;
import me.devwckd.dodgeball.context.DodgeballContext;
import me.devwckd.dodgeball.game.StateResult;
import me.devwckd.dodgeball.game.State;
import me.devwckd.dodgeball.history.HistoryEntry;
import me.devwckd.dodgeball.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class StartingState extends AbstractJoinableState {

    private final List<Player> players;
    private final Set<FastBoard> scoreboards = new HashSet<>();
    private int timeRemaining = 30;

    @Override
    public StateResult<DodgeballContext> start(DodgeballContext context) {
        players.forEach(this::createScoreboard);
        return StateResult.none();
    }

    @Override
    public StateResult<DodgeballContext> update(DodgeballContext context) {
        if(getPlayerCount() < 2) {
            return StateResult.next(new WaitingState(players));
        }

        if(timeRemaining <= 0) {
            return StateResult.next(new PlayingState(players));
        }

        if(timeRemaining <= 5) {
            players.forEach(player -> {
                player.sendTitle("", "§a" + seconds(timeRemaining), 0, 20, 10);
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
            });
        }

        updateScoreboards();
        timeRemaining -= 1;
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

        broadcastMessage("§c[!] " + player.getName() + " quit.");
    }

    @Override
    public boolean hasJoined(final @NotNull Player player) {
        return players.contains(player);
    }

    @Override
    public void createScoreboard(Player player) {
        final FastBoard scoreboard = new FastBoard(player);
        scoreboard.updateTitle("§9§lDodgeball");
        scoreboard.updateLines(
          " ",
          " §fWelcome §e" + player.getName() + " ",
          " §fto Dodgeball! ",
          " ",
          " §fStarting in §a" + seconds(timeRemaining) + " ",
          " §e" + getPlayerCount() + "/20 §fplayers ",
          " "
        );
        scoreboards.add(scoreboard);
    }

    @Override
    public void updateScoreboards() {
        scoreboards.forEach(scoreboard -> {
            scoreboard.updateLine(4, " §fStarting in §a" + seconds(timeRemaining) + " ");
            scoreboard.updateLine(5, " §e" + getPlayerCount() + "/20 §fplayers ");
        });
    }
}
