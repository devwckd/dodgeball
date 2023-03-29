package me.devwckd.dodgeball.arena;

import com.mongodb.MongoException;
import me.devwckd.dodgeball.exception.ArenaException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArenaManager {

    private final @NotNull ArenaCache arenaCache;
    private final @NotNull ArenaRepository arenaRepository;

    public ArenaManager(final @NotNull ArenaCache arenaCache, final @NotNull ArenaRepository arenaRepository) {
        this.arenaCache = arenaCache;
        this.arenaRepository = arenaRepository;

        for (Arena arena : arenaRepository.find()) {
            arenaCache.insert(arena);
        }
    }

    public void insert(final @NotNull Arena arena) throws ArenaException {
        try {
            arenaRepository.insert(arena);
        } catch (MongoException ignored) {
            throw new ArenaException("An arena with this id already exists.");
        }

        arenaCache.insert(arena);
    }

    public void replace(final @NotNull Arena arena) {
        arenaRepository.deleteById(arena.getId());
        arenaRepository.insert(arena);
        arenaCache.insert(arena);
    }

    public @Nullable Arena findById(final @NotNull String id) {
        Arena arena;

        if ((arena = arenaCache.findById(id)) != null) {
            return arena;
        }
        if ((arena = arenaRepository.findById(id)) != null) {
            return arena;
        }

        return null;
    }

}
