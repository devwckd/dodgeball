package me.devwckd.dodgeball.game;

import me.devwckd.dodgeball.game.Game;
import me.devwckd.dodgeball.game.StateResult;

public abstract class State<C> {

    Game<C> game;

    public StateResult<C> start(C context) {
        return StateResult.none();
    }

    public StateResult<C> update(C context) {
        return StateResult.none();
    }

    public StateResult<C> stop(C context) {
        return StateResult.none();
    }

    public Game<C> getGame() {
        if(game == null) throw new RuntimeException("Trying to get game on a non started state.");
        return game;
    }

}
