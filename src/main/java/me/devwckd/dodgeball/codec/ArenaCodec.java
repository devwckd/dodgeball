package me.devwckd.dodgeball.codec;

import me.devwckd.dodgeball.arena.Arena;
import me.devwckd.dodgeball.utils.Cuboid;
import me.devwckd.dodgeball.utils.MongoUtils;
import org.bson.BsonArray;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.BsonArrayCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArenaCodec implements Codec<Arena> {

    public static ArenaCodec INSTANCE = new ArenaCodec();

    @Override
    public Arena decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        reader.readObjectId("_id");
        final String id = reader.readString("id");
        final String displayName = reader.readString("displayName");
        final Vector lobbySpawn = MongoUtils.stringToVector(reader.readString("lobbySpawn"));
        final String map = reader.readString("map");
        final Cuboid middleLine = MongoUtils.stringToCuboid(reader.readString("middleLine"));
        final Vector redTeamSpawn = MongoUtils.stringToVector(reader.readString("redTeamSpawn"));
        final Vector redBallSpawn = MongoUtils.stringToVector(reader.readString("redBallSpawn"));
        final Vector blueTeamSpawn = MongoUtils.stringToVector(reader.readString("blueTeamSpawn"));
        final Vector blueBallSpawn = MongoUtils.stringToVector(reader.readString("blueBallSpawn"));
        reader.readName("middleBallSpawns");
        reader.readStartArray();
        final List<Vector> middleBallSpawns = new ArrayList<>();
        while(reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            middleBallSpawns.add(MongoUtils.stringToVector(reader.readString()));
        }
        reader.readEndArray();
        reader.readEndDocument();
        return new Arena(
          id, displayName, lobbySpawn, map, middleLine, redTeamSpawn, redBallSpawn, blueTeamSpawn, blueBallSpawn, middleBallSpawns
        );
    }

    @Override
    public void encode(BsonWriter writer, Arena value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeObjectId("_id", new ObjectId());
        writer.writeString("id", value.getId());
        writer.writeString("displayName", value.getDisplayName());
        writer.writeString("lobbySpawn", MongoUtils.vectorToString(value.getLobbySpawn()));
        writer.writeString("map", value.getMapFolderName());
        writer.writeString("middleLine", MongoUtils.cuboidToString(value.getMiddleLine()));
        writer.writeString("redTeamSpawn", MongoUtils.vectorToString(value.getRedTeamSpawn()));
        writer.writeString("redBallSpawn", MongoUtils.vectorToString(value.getRedBallSpawn()));
        writer.writeString("blueTeamSpawn", MongoUtils.vectorToString(value.getBlueTeamSpawn()));
        writer.writeString("blueBallSpawn", MongoUtils.vectorToString(value.getBlueBallSpawn()));
        writer.writeStartArray("middleBallSpawns");
        for (Vector middleBallSpawn : value.getMiddleBallSpawns()) {
            writer.writeString(MongoUtils.vectorToString(middleBallSpawn));
        }
        writer.writeEndArray();
        writer.writeEndDocument();
    }

    @Override
    public Class<Arena> getEncoderClass() {
        return Arena.class;
    }

}
