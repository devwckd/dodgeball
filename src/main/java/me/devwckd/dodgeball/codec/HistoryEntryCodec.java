package me.devwckd.dodgeball.codec;

import me.devwckd.dodgeball.history.HistoryEntry;
import me.devwckd.dodgeball.team.Team;
import org.bson.BsonBinary;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class HistoryEntryCodec implements Codec<HistoryEntry> {

    public static HistoryEntryCodec INSTANCE = new HistoryEntryCodec();

    @Override
    public HistoryEntry decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        reader.readObjectId("_id");
        final HistoryEntry historyEntry = readHistoryEntry(reader);
        reader.readEndDocument();
        return historyEntry;
    }

    @Override
    public void encode(BsonWriter writer, HistoryEntry value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeObjectId("_id", new ObjectId());
        writeHistoryEntry(writer, value);
        writer.writeEndDocument();
    }

    @Override
    public Class<HistoryEntry> getEncoderClass() {
        return HistoryEntry.class;
    }

    private void writeHistoryEntry(final @NotNull BsonWriter writer, final @NotNull HistoryEntry historyEntry) {
        if(historyEntry instanceof HistoryEntry.KillEntry killEntry) {
            writer.writeString("type", "KILL");
            writer.writeDateTime("dateTime", killEntry.getDateTime());
            writer.writeBinaryData("killedId", new BsonBinary(killEntry.getKilledId()));
            writer.writeString("killedName", killEntry.getKilledName());
            writer.writeString("arenaName", killEntry.getArenaName());
            writer.writeString("team", killEntry.getTeam().name());
            return;
        }

        if(historyEntry instanceof HistoryEntry.DeathEntry deathEntry) {
            writer.writeString("type", "DEATH");
            writer.writeDateTime("dateTime", deathEntry.getDateTime());
            writer.writeBinaryData("killerId", new BsonBinary(deathEntry.getKillerId()));
            writer.writeString("killerName", deathEntry.getKillerName());
            writer.writeString("arenaName", deathEntry.getArenaName());
            writer.writeString("team", deathEntry.getTeam().name());
            return;
        }

        if(historyEntry instanceof HistoryEntry.PlayEntry playEntry) {
            writer.writeString("type", "PLAY");
            writer.writeDateTime("dateTime", playEntry.getDateTime());
            writer.writeString("arenaName", playEntry.getArenaName());
            writer.writeString("team", playEntry.getTeam().name());
            return;
        }

        if(historyEntry instanceof HistoryEntry.WinEntry winEntry) {
            writer.writeString("type", "WIN");
            writer.writeDateTime("dateTime", winEntry.getDateTime());
            writer.writeString("arenaName", winEntry.getArenaName());
            writer.writeString("team", winEntry.getTeam().name());
            return;
        }

        throw new IllegalStateException("HistoryEntry not recognized.");
    }

    private @NotNull HistoryEntry readHistoryEntry(final @NotNull BsonReader reader) {
        final String type = reader.readString("type");
        switch (type.toUpperCase()) {
            case "KILL" -> {
                final long dateTime = reader.readDateTime("dateTime");
                final UUID killedId = reader.readBinaryData("killedId").asUuid();
                final String killedName = reader.readString("killedName");
                final String arenaName = reader.readString("arenaName");
                final Team team = Team.valueOf(reader.readString("team").toUpperCase());
                return new HistoryEntry.KillEntry(dateTime, killedId, killedName, arenaName, team);
            }
            case "DEATH" -> {
                final long dateTime = reader.readDateTime("dateTime");
                final UUID killerId = reader.readBinaryData("killerId").asUuid();
                final String killerName = reader.readString("killerName");
                final String arenaName = reader.readString("arenaName");
                final Team team = Team.valueOf(reader.readString("team").toUpperCase());
                return new HistoryEntry.DeathEntry(dateTime, killerId, killerName, arenaName, team);
            }
            case "PLAY" -> {
                final long dateTime = reader.readDateTime("dateTime");
                final String arenaName = reader.readString("arenaName");
                final Team team = Team.valueOf(reader.readString("team").toUpperCase());
                return new HistoryEntry.PlayEntry(dateTime, arenaName, team);
            }
            case "WIN" -> {
                final long dateTime = reader.readDateTime("dateTime");
                final String arenaName = reader.readString("arenaName");
                final Team team = Team.valueOf(reader.readString("team").toUpperCase());
                return new HistoryEntry.WinEntry(dateTime, arenaName, team);
            }
            default -> throw new IllegalStateException("HistoryEntry not recognized.");
        }
    }
}
