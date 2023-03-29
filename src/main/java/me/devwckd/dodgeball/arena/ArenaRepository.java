package me.devwckd.dodgeball.arena;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class ArenaRepository {

    private final @NotNull MongoCollection<Arena> collection;

    public ArenaRepository(final @NotNull MongoClient mongoClient) {
        this.collection = mongoClient.getDatabase("dodgeball").getCollection("arenas", Arena.class);
        this.collection.createIndex(Indexes.ascending("id"), new IndexOptions().unique(true));
    }

    public void insert(final @NotNull Arena arena) {
        collection.insertOne(arena);
    }

    public void deleteById(final @NotNull String id) {
        collection.deleteOne(Filters.regex("id", id, "i"));
    }

    public @Nullable Arena findById(final @NotNull String id) {
        return collection.find(Filters.regex("id", id, "i")).first();
    }

    public @NotNull Iterable<Arena> find() {
        return collection.find();
    }

}
