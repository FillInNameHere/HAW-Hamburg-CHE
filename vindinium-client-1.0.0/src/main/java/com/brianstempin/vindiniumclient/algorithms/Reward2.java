package com.brianstempin.vindiniumclient.algorithms;

import com.brianstempin.vindiniumclient.bot.advanced.AdvancedGameState;

/**
 * Created by Henning Kahl on 15.04.2016.
 */
public class Reward2 implements IReward {
    private AdvancedGameState lastState;
    private AdvancedGameState currentState;

    public int reward(int modus, long state) {
        int reward = 0;

        if (lastState == null) {
            this.lastState = currentState;
            return 0;
        }

        int lastMines = lastState.getMe().getMineCount();
        int currentMines = currentState.getMe().getMineCount();

        reward += (currentMines - lastMines) * 70;

        int lastLife = lastState.getMe().getLife();
        int currentLife = currentState.getMe().getLife();

        reward += currentLife - lastLife - 20;

        return reward;
    }

    public AdvancedGameState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(AdvancedGameState currentState) {
        this.currentState = currentState;
    }
}
