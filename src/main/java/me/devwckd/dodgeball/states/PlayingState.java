package me.devwckd.dodgeball.states;

import fr.mrmicky.fastboard.FastBoard;
import me.devwckd.dodgeball.context.DodgeballContext;
import me.devwckd.dodgeball.game.StateResult;
import me.devwckd.dodgeball.team.Team;
import me.devwckd.dodgeball.team.TeamMembers;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PlayingState extends AbstractState {

    private final Set<Player> spectators = new HashSet<>();
    private final Set<FastBoard> scoreboards = new HashSet<>();
    private final TeamMembers redTeamMembers = new TeamMembers(Team.RED);
    private final TeamMembers blueTeamMembers = new TeamMembers(Team.BLUE);
    private final Map<Player, Team> teamByPlayer = new HashMap<>();

    private int elapsed = 0;
    private TeamMembers winner = null;

    public PlayingState(final @NotNull Set<Player> players) {
        int index = 0;
        for (Player player : players) {
            final Team team = Team.values()[index];
            final TeamMembers teamMembers = getTeamMembers(team);
            teamMembers.addPlayer(player);
            teamByPlayer.put(player, team);
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
            if (team == Team.RED) {
                spawnLocation = context.getArena().getRedTeamSpawn().toLocation(world);
            } else {
                spawnLocation = context.getArena().getBlueTeamSpawn().toLocation(world);
            }
            player.teleport(spawnLocation);
        }

        context.getArena().getMiddleBallSpawns().stream().map(vector -> vector.toLocation(world))
          .forEach(location -> world.dropItem(location, new ItemStack(Material.SNOWBALL)));

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
        return teamByPlayer.containsKey(player);
    }

    @Override
    public int getPlayerCount() {
        return teamByPlayer.size();
    }

    @Override
    public Collection<Player> getPlayers() {
        return teamByPlayer.keySet();
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
                if(content == null) continue;
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
        final Team team = teamByPlayer.get(player);
        if (team == null) throw new RuntimeException(player.getName() + " doesn't have a team!");
        return team;
    }

    public void kill(Player killer, Player killed) {
        final Team killerTeam = getTeam(killer);
        final Team killedTeam = getTeam(killed);
        if (killerTeam == killedTeam) return;
        broadcastMessage("§c§l[⚔] " + killerTeam.getColor() + killer.getName() + " §7eliminated " + killedTeam.getColor() + killed.getName());
        quit(killed);
    }

}
