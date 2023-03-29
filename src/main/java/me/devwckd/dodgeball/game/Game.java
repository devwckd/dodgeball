package me.devwckd.dodgeball.game;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

@RequiredArgsConstructor
public class Game<C> {

    private final JavaPlugin plugin;

    private C context;
    private State<C> currentState;

    private BukkitTask stateUpdateTask;

    public void start(State<C> firstState, C context) {
        if(stateUpdateTask != null) {
            throw new IllegalStateException("Trying to start a Game that has already started.");
        }

        this.context = context;
        redefineCurrentState(firstState);
        stateUpdateTask = Bukkit.getScheduler().runTaskTimer(plugin, new StateUpdateTask(), 20L, 20L);
    }

    public void end() {
        if(stateUpdateTask == null) {
            throw new IllegalStateException("Trying to end a Game that hasn't started yet.");
        }

        stateUpdateTask.cancel();
        finishCurrentState();
        this.context = null;
        this.currentState = null;
    }

    public State<C> getCurrentState() {
        return currentState;
    }

    public C getContext() {
        return context;
    }

    public boolean isStarted() {
        return stateUpdateTask != null;
    }

    void finishCurrentState() {
        if (currentState == null) return;
        currentState.stop(context).apply(this);
    }

    void redefineCurrentState(State<C> state) {
        currentState = state;
        currentState.game = this;
        currentState.start(context).apply(this);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    class StateUpdateTask implements Runnable {
        @Override
        public void run() {
            currentState.update(context).apply(Game.this);
        }
    }

}
