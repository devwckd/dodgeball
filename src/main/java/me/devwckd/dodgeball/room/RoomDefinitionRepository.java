package me.devwckd.dodgeball.room;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.BsonBinary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class RoomDefinitionRepository {

    private final MongoCollection<RoomDefinition> collection;

    public RoomDefinitionRepository(final @NotNull MongoClient mongoClient) {
        this.collection = mongoClient.getDatabase("dodgeball").getCollection("rooms", RoomDefinition.class);
        this.collection.createIndex(Indexes.ascending("id"), new IndexOptions().unique(true));
    }

    public void insert(final @NotNull RoomDefinition roomDefinition) {
        collection.insertOne(roomDefinition);
    }

    public void insert(final @NotNull Room room) {
        insert(room.toDefinition());
    }

    public void deleteById(final @NotNull UUID id) {
        collection.deleteOne(Filters.eq("id", new BsonBinary(id)));
    }

    public @Nullable RoomDefinition findById(final @NotNull UUID id) {
        return collection.find(Filters.eq("id", new BsonBinary(id))).first();
    }

    public @NotNull Iterable<RoomDefinition> find() {
        return collection.find();
    }

}
