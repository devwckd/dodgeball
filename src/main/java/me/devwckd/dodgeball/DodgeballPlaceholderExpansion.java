package me.devwckd.dodgeball;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.devwckd.dodgeball.context.DodgeballContext;
import me.devwckd.dodgeball.game.Game;
import me.devwckd.dodgeball.history.History;
import me.devwckd.dodgeball.history.HistoryManager;
import me.devwckd.dodgeball.room.Room;
import me.devwckd.dodgeball.room.RoomManager;
import me.devwckd.dodgeball.states.PlayingState;
import me.devwckd.dodgeball.team.Team;
import me.devwckd.dodgeball.utils.NumberUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class DodgeballPlaceholderExpansion extends PlaceholderExpansion {

    private final RoomManager roomManager;
    private final HistoryManager historyManager;

    @Override
    public @NotNull String getIdentifier() {
        return "dodgeball";
    }

    @Override
    public @NotNull String getAuthor() {
        return "devwckd";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.1";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (!offlinePlayer.isOnline() || offlinePlayer.getPlayer() == null) return null;
        final Player player = offlinePlayer.getPlayer();

        if (params.equals("team")) {
            final Room room = roomManager.findByPlayer(player);
            if (room == null) return null;

            final Game<DodgeballContext> game = room.getGame();
            if (game.getCurrentState() instanceof PlayingState playingState) {
                return playingState.getTeam(player).getDisplayName();
            }

            return null;
        }

        if (params.equals("blue_team_count")) {
            final Room room = roomManager.findByPlayer(player);
            if (room == null) return null;

            final Game<DodgeballContext> game = room.getGame();
            if (game.getCurrentState() instanceof PlayingState playingState) {
                return playingState.getBlueTeamMemberCount() + "";
            }

            return null;
        }

        if (params.equals("red_team_count")) {
            final Room room = roomManager.findByPlayer(player);
            if (room == null) return null;

            final Game<DodgeballContext> game = room.getGame();
            if (game.getCurrentState() instanceof PlayingState playingState) {
                return playingState.getRedTeamMemberCount() + "";
            }

            return null;
        }

        if (params.equals("team_count")) {
            final Room room = roomManager.findByPlayer(player);
            if (room == null) return null;

            final Game<DodgeballContext> game = room.getGame();
            if (game.getCurrentState() instanceof PlayingState playingState) {
                final Team team = playingState.getTeam(player);
                if (team == Team.RED) {
                    return playingState.getRedTeamMemberCount() + "";
                } else {
                    return playingState.getBlueTeamMemberCount() + "";
                }
            }

            return null;
        }

        if (params.equals("history_kills")) {
            final History history = historyManager.findCachedById(player.getUniqueId());
            if(history == null) return null;
            return history.getTotalWins() + "";
        }

        if (params.equals("history_deaths")) {
            final History history = historyManager.findCachedById(player.getUniqueId());
            if(history == null) return null;
            return history.getTotalDeaths() + "";
        }

        if (params.equals("history_wins")) {
            final History history = historyManager.findCachedById(player.getUniqueId());
            if(history == null) return null;
            return history.getTotalWins() + "";
        }

        if (params.equals("history_games")) {
            final History history = historyManager.findCachedById(player.getUniqueId());
            if(history == null) return null;
            return history.getTotalGames() + "";
        }

        if (params.equals("history_wr")) {
            final History history = historyManager.findCachedById(player.getUniqueId());
            if(history == null) return null;
            return NumberUtils.percentage(history.getTotalWins(), history.getTotalGames()) + "";
        }

        if (params.equals("history_kdr")) {
            final History history = historyManager.findCachedById(player.getUniqueId());
            if(history == null) return null;
            return NumberUtils.percentage(history.getTotalKills(), history.getTotalDeaths()) + "";
        }

        return null;
    }
}
