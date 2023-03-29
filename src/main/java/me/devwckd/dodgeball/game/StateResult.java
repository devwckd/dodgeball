package me.devwckd.dodgeball.game;

import lombok.RequiredArgsConstructor;

public interface StateResult<C> {
    /**
     * Advances the game state to the next state.
     */
    static <C> StateResult<C> next(State<C> nextState) {
        return new NextStateResult<>(nextState);
    }

    /**
     * Ends the current game.
     */
    static <C> StateResult<C> end() {
        return new EndStateResult<>();
    }

    /**
     * Does nothing.
     */
    static <C> StateResult<C> none() {
        return new NoneStateResult<>();
    }

    void apply(Game<C> game);

    @RequiredArgsConstructor
    class NextStateResult<C> implements StateResult<C> {
        private final State<C> nextState;

        @Override
        public void apply(Game<C> game) {
            game.finishCurrentState();
            game.redefineCurrentState(nextState);
        }
    }

    class EndStateResult<C> implements StateResult<C> {
        @Override
        public void apply(Game<C> game) {
            game.end();
        }
    }

    class NoneStateResult<C> implements StateResult<C> {
        @Override
        public void apply(Game<C> game) {
        }
    }

}
