package me.devwckd.dodgeball.room;

import me.devwckd.dodgeball.exception.RoomException;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RoomCache {

    private final Map<UUID, Room> roomsById = new HashMap<>();
    private final Map<String, Room> roomsByWorldName = new HashMap<>();

    public void insert(final @NotNull Room room) throws RoomException {
        if (room.getWorld() == null) throw new RoomException("The room's world wasn't created yet");
        roomsById.put(room.getId(), room);
        roomsByWorldName.put(room.getWorld().getName(), room);
    }

    public @Nullable Room findById(final @NotNull UUID id) {
        return roomsById.get(id);
    }

    public @Nullable Room findByWorldName(final @NotNull String worldName) {
        return roomsByWorldName.get(worldName);
    }

    public @Nullable Room findByWorld(final @NotNull World world) {
        return roomsByWorldName.get(world.getName());
    }

    public Collection<Room> findAll() {
        return roomsById.values();
    }

    public @Nullable Room deleteById(final @NotNull UUID id) {
        final Room room = roomsById.remove(id);
        if (room != null) {
            roomsByWorldName.remove(room.getWorld().getName());
        }
        return room;
    }

}
