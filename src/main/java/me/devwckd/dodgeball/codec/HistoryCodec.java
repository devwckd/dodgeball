package me.devwckd.dodgeball.codec;

import me.devwckd.dodgeball.history.History;
import me.devwckd.dodgeball.history.HistoryEntry;
import me.devwckd.dodgeball.team.Team;
import org.bson.BsonBinary;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HistoryCodec implements Codec<History> {

    public static HistoryCodec INSTANCE = new HistoryCodec();

    @Override
    public History decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        reader.readObjectId("_id");
        final UUID id = reader.readBinaryData("id").asUuid();
        final String nickname = reader.readString("nickname");
        reader.readName("entries");
        reader.readStartArray();
        final List<HistoryEntry> entries = new ArrayList<>();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            entries.add(decoderContext.decodeWithChildContext(HistoryEntryCodec.INSTANCE, reader));
        }
        reader.readEndArray();
        reader.readEndDocument();
        final History history = new History(id, nickname);
        history.addAllEntries(entries);
        return history;
    }

    @Override
    public void encode(BsonWriter writer, History value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeObjectId("_id", new ObjectId());
        writer.writeBinaryData("id", new BsonBinary(value.getId()));
        writer.writeString("nickname", value.getNickname());
        writer.writeStartArray("entries");
        for (HistoryEntry entry : value.getEntries()) {
            encoderContext.encodeWithChildContext(HistoryEntryCodec.INSTANCE, writer, entry);
        }
        writer.writeEndArray();
        writer.writeEndDocument();
    }

    @Override
    public Class<History> getEncoderClass() {
        return History.class;
    }





}
