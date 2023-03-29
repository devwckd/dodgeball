package me.devwckd.dodgeball.listener;

import lombok.RequiredArgsConstructor;
import me.devwckd.dodgeball.edit_session.EditSession;
import me.devwckd.dodgeball.edit_session.EditSessionManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@RequiredArgsConstructor
public class EditSessionListener implements Listener {

    private final EditSessionManager editSessionManager;

    @EventHandler
    public void onCreatureSpawn(final @NotNull CreatureSpawnEvent event) {
        final EditSession editSession = editSessionManager.findByWorldName(Objects.requireNonNull(event.getLocation().getWorld()).getName());
        if(editSession == null) return;
        event.setCancelled(true);
    }

}
