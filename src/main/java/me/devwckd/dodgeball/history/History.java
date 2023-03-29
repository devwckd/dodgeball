package me.devwckd.dodgeball.history;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Data
public class History {

    private final UUID id;
    private final String nickname;
    private final List<HistoryEntry> entries = new ArrayList<>();

    private long totalKills = 0;
    private long totalDeaths = 0;
    private long totalGames = 0;
    private long totalWins = 0;

    private long updateNext = 0;

    public long getTotalKills() {
        final long now = System.currentTimeMillis();
        if(updateNext < now) {
            updateAll(now);
        }
        return totalKills;
    }

    public long getTotalDeaths() {
        final long now = System.currentTimeMillis();
        if(updateNext < now) {
            updateAll(now);
        }
        return totalDeaths;
    }

    public long getTotalGames() {
        final long now = System.currentTimeMillis();
        if(updateNext < now) {
            updateAll(now);
        }
        return totalGames;
    }

    public long getTotalWins() {
        final long now = System.currentTimeMillis();
        if(updateNext < now) {
            updateAll(now);
        }
        return totalWins;
    }

    public void updateAll(long now) {
        totalKills = 0;
        totalDeaths = 0;
        totalGames = 0;
        totalWins = 0;

        for (HistoryEntry entry : entries) {
            if(entry instanceof HistoryEntry.KillEntry) {
                totalKills += 1;
            } else if (entry instanceof HistoryEntry.DeathEntry) {
                totalDeaths += 1;
            }else if (entry instanceof HistoryEntry.PlayEntry) {
                totalGames += 1;
            }else if (entry instanceof HistoryEntry.WinEntry) {
                totalWins += 1;
            }
        }

        updateNext = now + 10000L;
    }

    public void addEntry(final @NotNull HistoryEntry historyEntry) {
        entries.add(historyEntry);
    }

    public void addAllEntries(final @NotNull Collection<HistoryEntry> historyEntries) {
        entries.addAll(historyEntries);
    }

}
