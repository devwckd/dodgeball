package me.devwckd.dodgeball.history;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.devwckd.dodgeball.team.Team;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public interface HistoryEntry {

    long getDateTime();

    @Data
    class KillEntry implements HistoryEntry {
        private final long dateTime;
        private final UUID killedId;
        private final String killedName;
        private final String arenaName;
        private final Team team;
    }

    @Data
    class DeathEntry implements HistoryEntry {
        private final long dateTime;
        private final UUID killerId;
        private final String killerName;
        private final String arenaName;
        private final Team team;
    }

    @Data
    class PlayEntry implements HistoryEntry {
        private final long dateTime;
        private final String arenaName;
        private final Team team;
    }

    @Data
    class WinEntry implements HistoryEntry {
        private final long dateTime;
        private final String arenaName;
        private final Team team;
    }

}
