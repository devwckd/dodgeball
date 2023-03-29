package me.devwckd.dodgeball.history;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HistoryCache {

    private final Map<UUID, History> histories = new ConcurrentHashMap<>();

    public @Nullable History findById(final @NotNull UUID id) {
        return histories.get(id);
    }

    public void insert(final @NotNull History history) {
        histories.put(history.getId(), history);
    }

    public void deleteById(final @NotNull UUID id) {
        histories.remove(id);
    }

}
