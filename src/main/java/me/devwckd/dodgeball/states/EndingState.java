package me.devwckd.dodgeball.states;

import fr.mrmicky.fastboard.FastBoard;
import lombok.RequiredArgsConstructor;
import me.devwckd.dodgeball.context.DodgeballContext;
import me.devwckd.dodgeball.game.StateResult;
import me.devwckd.dodgeball.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class EndingState extends AbstractState {

    private final Team team;
    private final Set<Player> winners;
    private final Set<Player> spectators;
    private final Set<FastBoard> scoreboards = new HashSet<>();
    private int time = 10;

    @Override
    public StateResult<DodgeballContext> start(DodgeballContext context) {
        applyWinHistories();
        for (Player winner : winners) {
            createScoreboard(winner);
        }
        for (Player spectator : spectators) {
            createScoreboard(spectator);
        }
        return StateResult.none();
    }

    @Override
    public StateResult<DodgeballContext> update(DodgeballContext context) {
        if (time < 1) {
            return StateResult.next(new WaitingState());
        }
        for (Player winner : winners) {
            final Firework firework = (Firework) winner.getWorld().spawnEntity(winner.getLocation(), EntityType.FIREWORK);
            final FireworkMeta fireworkMeta = firework.getFireworkMeta();
            fireworkMeta.addEffect(FireworkEffect.builder().trail(true).withColor(team == Team.RED ? Color.RED : Color.BLUE).build());
            firework.setFireworkMeta(fireworkMeta);
            firework.detonate();
            winner.sendTitle(team.getDisplayName(), team.getColor() + "WINNER", 0, 25, 10);
        }
        for (Player spectator : spectators) {
            spectator.sendTitle(team.getDisplayName(), team.getColor() + "WINNER", 0, 25, 10);
        }
        time -= 1;
        return StateResult.none();
    }

    @Override
    public StateResult<DodgeballContext> stop(DodgeballContext context) {
        winners.forEach(this::quit);
        spectators.forEach(this::quit);
        scoreboards.forEach(FastBoard::delete);
        scoreboards.clear();
        return StateResult.none();
    }

    @Override
    public void updateScoreboards() {
    }

    @Override
    public void createScoreboard(Player player) {
        final FastBoard scoreboard = new FastBoard(player);
        scoreboard.updateTitle("§9§lDodgeball");
        scoreboard.updateLines(
          " ",
          " §fWinner: " + team.getDisplayName(),
          " "
        );
        scoreboards.add(scoreboard);
    }

    @Override
    public boolean hasJoined(@NotNull Player player) {
        return winners.contains(player) || spectators.contains(player);
    }

    @Override
    public int getPlayerCount() {
        return winners.size() + spectators.size();
    }

    @Override
    public Collection<Player> getPlayers() {
        final Set<Player> players = new HashSet<>(winners);
        players.addAll(spectators);
        return players;
    }

    @Override
    public void quit(Player player) {
        player.getInventory().clear();
        final FileConfiguration config = getGame().getPlugin().getConfig();
        spectators.remove(player);
        if (winners.remove(player)) {
            for (String command : config.getStringList("winner-commands")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
            }
        }
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(config.getLocation("exit-location", Bukkit.getWorlds().get(0).getSpawnLocation()));
        player.resetTitle();
    }

    private void applyWinHistories() {
        final long now = System.currentTimeMillis();
        final DodgeballContext context = getGame().getContext();
        context.getHistoryManager().addEntryBatch(winners
          .stream()
          .map(player -> Map.<UUID, List<HistoryEntry>>entry(player.getUniqueId(), Collections.singletonList(new HistoryEntry.WinEntry(now, context.getArena().getDisplayName(), team))))
          .collect(Collectors.<Map.Entry<UUID, List<HistoryEntry>>, UUID, List<HistoryEntry>>toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }
}
