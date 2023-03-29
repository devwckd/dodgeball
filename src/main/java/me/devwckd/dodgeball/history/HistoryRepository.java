package me.devwckd.dodgeball.history;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Updates;
import me.devwckd.dodgeball.codec.HistoryEntryCodec;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HistoryRepository {

    private final MongoCollection<History> collection;

    public HistoryRepository(MongoClient client) {
        this.collection = client.getDatabase("dodgeball").getCollection("histories", History.class);
        this.collection.createIndex(Indexes.ascending("id"), new IndexOptions().unique(true));
    }

    public void insert(final @NotNull History history) {
        collection.insertOne(history);
    }

    public void deleteById(final @NotNull UUID id) {
        collection.deleteOne(Filters.eq("id", new BsonBinary(id)));
    }

    public @Nullable History findById(final @NotNull UUID id) {
        return collection.find(Filters.eq("id", new BsonBinary(id))).first();
    }

    public void insertHistoryEntry(final @NotNull UUID id, final @NotNull HistoryEntry historyEntry) {
        this.collection.findOneAndUpdate(Filters.eq("id", new BsonBinary(id)), Updates.push("entries", historyEntry));
    }

    /**
     * Transactions were removed because they are only supported on replica-sets.
     */
    public void insertHistoryEntryBatch(final @NotNull Map<UUID, List<HistoryEntry>> entriesById) {
            try {
                for (Map.Entry<UUID, List<HistoryEntry>> entry : entriesById.entrySet()) {
                    final List<BsonDocument> documents = entry.getValue().stream().map(historyEntry -> {
                        final BsonDocument document = new BsonDocument();
                        final BsonWriter writer = new BsonDocumentWriter(document);
                        HistoryEntryCodec.INSTANCE.encode(writer, historyEntry, EncoderContext.builder().build());
                        return document;
                    }).toList();

                    this.collection.findOneAndUpdate(Filters.eq("id", new BsonBinary(entry.getKey())), Updates.pushEach("entries", documents));
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
    }
}
