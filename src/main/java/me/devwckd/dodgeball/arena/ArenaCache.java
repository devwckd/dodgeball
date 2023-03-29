package me.devwckd.dodgeball.arena;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ArenaCache {

    private final @NotNull Map<String, Arena> arenasById = new HashMap<>();

    public void insert(final @NotNull Arena arena) {
        arenasById.put(arena.getId().toLowerCase(), arena);
    }

    public @Nullable Arena findById(final @NotNull String id) {
        return arenasById.get(id.toLowerCase());
    }

}
