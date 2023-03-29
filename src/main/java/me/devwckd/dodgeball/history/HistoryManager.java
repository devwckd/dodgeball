package me.devwckd.dodgeball.history;

import com.mongodb.MongoException;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
public class HistoryManager {

    private static final Executor HISTORY_EXECUTOR = Executors.newSingleThreadExecutor();

    private final HistoryCache historyCache;
    private final HistoryRepository historyRepository;

    public void loadCache(final @NotNull Player player) {
        HISTORY_EXECUTOR.execute(() -> {
            final UUID id = player.getUniqueId();
            History history = historyRepository.findById(id);
            if (history == null) {
                history = new History(id, player.getName());
                historyRepository.insert(history);
            }
            historyCache.insert(history);
        });
    }

    public void removeCache(final @NotNull Player player) {
        historyCache.deleteById(player.getUniqueId());
    }

    public void addEntry(final @NotNull UUID id, final @NotNull HistoryEntry historyEntry) {
        HISTORY_EXECUTOR.execute(() -> {
            try {
                historyRepository.insertHistoryEntry(id, historyEntry);
            } catch (MongoException exception) {
                exception.printStackTrace();
            }
            final History history = historyCache.findById(id);
            if (history != null) {
                history.addEntry(historyEntry);
            }
        });
    }

    public void addEntryBatch(final @NotNull Map<UUID, List<HistoryEntry>> entriesById) {
        HISTORY_EXECUTOR.execute(() -> {
            try {
                historyRepository.insertHistoryEntryBatch(entriesById);
            } catch (MongoException exception) {
                exception.printStackTrace();
            }

            for (Map.Entry<UUID, List<HistoryEntry>> entry : entriesById.entrySet()) {
                final History history = historyCache.findById(entry.getKey());
                if(history == null) continue;
                history.addAllEntries(entry.getValue());
            }
        });
    }

    public final @Nullable History findCachedById(final @NotNull UUID id) {
        return historyCache.findById(id);
    }

}
