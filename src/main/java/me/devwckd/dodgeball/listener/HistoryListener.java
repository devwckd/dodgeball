package me.devwckd.dodgeball.listener;

import lombok.RequiredArgsConstructor;
import me.devwckd.dodgeball.history.HistoryManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class HistoryListener implements Listener {

    private final HistoryManager historyManager;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPreLogin(final @NotNull PlayerLoginEvent event) {
        historyManager.loadCache(event.getPlayer());
    }

    @EventHandler
    public void onQuit(final @NotNull PlayerQuitEvent event) {
        historyManager.removeCache(event.getPlayer());
    }

}
