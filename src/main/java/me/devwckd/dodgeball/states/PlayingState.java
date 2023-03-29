package me.devwckd.dodgeball.states;

import fr.mrmicky.fastboard.FastBoard;
import me.devwckd.dodgeball.context.DodgeballContext;
import me.devwckd.dodgeball.game.StateResult;
import me.devwckd.dodgeball.history.HistoryEntry;
import me.devwckd.dodgeball.team.Team;
import me.devwckd.dodgeball.team.TeamMembers;
import me.devwckd.dodgeball.utils.ItemUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class PlayingState extends AbstractState {

    private final Set<Player> spectators = new HashSet<>();
    private final Set<FastBoard> scoreboards = new HashSet<>();
    private final TeamMembers redTeamMembers = new TeamMembers(Team.RED);
    private final TeamMembers blueTeamMembers = new TeamMembers(Team.BLUE);
    private final Map<Player, Team> teamsByPlayer = new HashMap<>();

    private int elapsed = 0;
    private TeamMembers winner = null;

    public PlayingState(final @NotNull List<Player> players) {
        int index = 0;
        Collections.shuffle(players);
        for (Player player : players) {
            final Team team = Team.values()[index];
            final TeamMembers teamMembers = getTeamMembers(team);
            teamMembers.addPlayer(player);
            teamsByPlayer.put(player, team);
            index++;
            if (index >= Team.values().length) {
                index = 0;
            }
        }
        players.clear();
    }

    @Override
    public StateResult<DodgeballContext> start(final @NotNull DodgeballContext context) {
        if (getPlayerCount() < 2) {
            return StateResult.next(new WaitingState());
        }

        final World world = context.getRoom().getWorld();
        for (Player player : getPlayers()) {
            createScoreboard(player);

            final Location spawnLocation;
            final Team team = getPlayerTeamMembers(player).getTeam();
            final Vector blueTeamSpawn = context.getArena().getBlueTeamSpawn();
            final Vector redTeamSpawn = context.getArena().getRedTeamSpawn();
            if (team == Team.RED) {
                spawnLocation = redTeamSpawn.toLocation(world).setDirection(redTeamSpawn.subtract(blueTeamSpawn));
            } else {
                spawnLocation = blueTeamSpawn.toLocation(world).setDirection(blueTeamSpawn.subtract(redTeamSpawn));
            }
            player.teleport(spawnLocation);
        }

        spawnExtraBall(context);

        context.getArena().getMiddleBallSpawns().stream().map(vector -> vector.toLocation(world))
          .forEach(location -> world.dropItem(location, new ItemStack(Material.SNOWBALL)));

        applyPlayHistories();

        return StateResult.none();
    }

    @Override
    public StateResult<DodgeballContext> update(final @NotNull DodgeballContext context) {
        elapsed += 1;
        updateScoreboards();

        if (winner != null) {
            return StateResult.next(new EndingState(winner.getTeam(), winner.getPlayers(), spectators));
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
    public boolean hasJoined(final @NotNull Player player) {
        return teamsByPlayer.containsKey(player);
    }

    @Override
    public int getPlayerCount() {
        return teamsByPlayer.size();
    }

    @Override
    public Collection<Player> getPlayers() {
        return teamsByPlayer.keySet();
    }

    @Override
    public void quit(final @NotNull Player player) {
        player.getInventory().clear();
        final TeamMembers teamMembers = getPlayerTeamMembers(player);
        teamMembers.removePlayer(player);
        scoreboards.removeIf(scoreboard -> {
            if (scoreboard.getPlayer().equals(player)) {
                scoreboard.delete();
                return true;
            } else {
                return false;
            }
        });
        updateScoreboards();

        if (player.isOnline()) {
            player.setGameMode(GameMode.SPECTATOR);
            spectators.add(player);
            for (ItemStack content : player.getInventory().getContents()) {
                if (content == null) continue;
                player.getWorld().dropItem(player.getLocation(), content);
            }
            player.getInventory().clear();
        }

        checkWinner();
    }

    private void checkWinner() {
        if (blueTeamMembers.getPlayerCount() < 1) {
            winner = redTeamMembers;
        } else if (redTeamMembers.getPlayerCount() < 1) {
            winner = blueTeamMembers;
        }
    }

    @Override
    public void createScoreboard(final @NotNull Player player) {
        final FastBoard scoreboard = new FastBoard(player);
        scoreboard.updateTitle("§9§lDodgeball");
        scoreboard.updateLines(
          " ",
          " " + Team.RED.getDisplayName() + " » §f" + getTeamMembers(Team.RED).getPlayerCount(),
          " " + Team.BLUE.getDisplayName() + " » §f" + getTeamMembers(Team.BLUE).getPlayerCount(),
          " ",
          " §fTeam: " + getPlayerTeamMembers(player).getTeam().getDisplayName(),
          " §fElapsed: §e" + seconds(elapsed),
          " "
        );
        scoreboards.add(scoreboard);
    }


    @Override
    public void updateScoreboards() {
        scoreboards.forEach(scoreboard -> {
            scoreboard.updateLine(1, " " + Team.RED.getDisplayName() + " » §f" + getTeamMembers(Team.RED).getPlayerCount());
            scoreboard.updateLine(2, " " + Team.BLUE.getDisplayName() + " » §f" + getTeamMembers(Team.BLUE).getPlayerCount());
            scoreboard.updateLine(5, " §fElapsed: §e" + seconds(elapsed));
        });
    }

    private @NotNull TeamMembers getTeamMembers(final @NotNull Team team) {
        if (team == Team.RED) {
            return redTeamMembers;
        } else {
            return blueTeamMembers;
        }
    }

    private @NotNull TeamMembers getPlayerTeamMembers(final @NotNull Player player) {
        final Team team = getTeam(player);
        return getTeamMembers(team);
    }

    public @NotNull Team getTeam(final @NotNull Player player) {
        final Team team = teamsByPlayer.get(player);
        if (team == null) throw new RuntimeException(player.getName() + " doesn't have a team!");
        return team;
    }

    public void kill(Player killer, Player killed) {
        final Team killerTeam = getTeam(killer);
        final Team killedTeam = getTeam(killed);
        if (killerTeam == killedTeam) return;
        broadcastMessage("§c§l[⚔] " + killerTeam.getColor() + killer.getName() + " §7eliminated " + killedTeam.getColor() + killed.getName());
        applyKillDeathHistory(killer, killed);
        quit(killed);
    }

    private void applyPlayHistories() {
        final long now = System.currentTimeMillis();
        final DodgeballContext context = getGame().getContext();
        context.getHistoryManager().addEntryBatch(teamsByPlayer
          .entrySet()
          .stream()
          .map(entry -> Map.<UUID, List<HistoryEntry>>entry(entry.getKey().getUniqueId(), Collections.singletonList(new HistoryEntry.PlayEntry(now, context.getArena().getDisplayName(), entry.getValue()))))
          .collect(Collectors.<Map.Entry<UUID, List<HistoryEntry>>, UUID, List<HistoryEntry>>toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }

    private void applyKillDeathHistory(final @NotNull Player killer, final @NotNull Player killed) {
        final long now = System.currentTimeMillis();
        final DodgeballContext context = getGame().getContext();

        final Map<UUID, List<HistoryEntry>> map = new HashMap<>();
        map.put(killer.getUniqueId(), Collections.singletonList(new HistoryEntry.KillEntry(now, killed.getUniqueId(), killed.getName(), context.getArena().getDisplayName(), getTeam(killer))));
        map.put(killed.getUniqueId(), Collections.singletonList(new HistoryEntry.DeathEntry(now, killer.getUniqueId(), killer.getName(), context.getArena().getDisplayName(), getTeam(killed))));
        context.getHistoryManager().addEntryBatch(map);
    }

    private void spawnExtraBall(DodgeballContext context) {
        if(redTeamMembers.getPlayerCount() > blueTeamMembers.getPlayerCount()) {
            ItemUtils.spawnSnowball(context.getArena().getBlueBallSpawn().toLocation(context.getRoom().getWorld()));
            for (Player player : blueTeamMembers.getPlayers()) {
                player.sendMessage("§9§l[!] §7Your team received an extra ball for being in numeric disadvantage.");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            }
        }
        if(blueTeamMembers.getPlayerCount() > redTeamMembers.getPlayerCount()) {
            ItemUtils.spawnSnowball(context.getArena().getRedTeamSpawn().toLocation(context.getRoom().getWorld()));
            for (Player player : blueTeamMembers.getPlayers()) {
                player.sendMessage("§c§l[!] §7Your team received an extra ball for being in numeric disadvantage.");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            }
        }
    }

    public int getBlueTeamMemberCount() {
        return blueTeamMembers.getPlayerCount();
    }

    public int getRedTeamMemberCount() {
        return redTeamMembers.getPlayerCount();
    }

}
