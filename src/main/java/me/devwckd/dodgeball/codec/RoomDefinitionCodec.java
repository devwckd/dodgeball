package me.devwckd.dodgeball.codec;

import lombok.RequiredArgsConstructor;
import me.devwckd.dodgeball.DodgeballPlugin;
import me.devwckd.dodgeball.arena.Arena;
import me.devwckd.dodgeball.room.Room;
import me.devwckd.dodgeball.room.RoomDefinition;
import me.devwckd.dodgeball.room.RoomDefinitionRepository;
import org.bson.BsonBinary;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

import java.util.UUID;

@RequiredArgsConstructor
public class RoomDefinitionCodec implements Codec<RoomDefinition> {

    public static final RoomDefinitionCodec INSTANCE = new RoomDefinitionCodec();

    @Override
    public RoomDefinition decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        reader.readObjectId("_id");
        final UUID id = reader.readBinaryData("id").asUuid();
        final String arenaId = reader.readString("arenaId");
        reader.readEndDocument();
        return new RoomDefinition(id, arenaId);
    }

    @Override
    public void encode(BsonWriter writer, RoomDefinition value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeObjectId("_id", new ObjectId());
        writer.writeBinaryData("id", new BsonBinary(value.getId()));
        writer.writeString("arenaId", value.getArenaId());
        writer.writeEndDocument();
    }

    @Override
    public Class<RoomDefinition> getEncoderClass() {
        return RoomDefinition.class;
    }
}
