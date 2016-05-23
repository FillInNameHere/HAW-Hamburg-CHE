package com.brianstempin.vindiniumclient.algorithms;

import com.brianstempin.vindiniumclient.dto.GameState;

/**
 * Created by Henning Kahl on 15.04.2016.
 */
public class Reward2 implements IReward{
    private GameState lastState;
    private GameState currentState;

    public int reward(int modus, long state){
        int reward = 0;



        return reward;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(GameState currentState) {
        this.currentState = currentState;
    }
}
