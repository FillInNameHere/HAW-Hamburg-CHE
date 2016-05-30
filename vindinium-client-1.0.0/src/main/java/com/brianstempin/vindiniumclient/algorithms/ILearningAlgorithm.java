package com.brianstempin.vindiniumclient.algorithms;

import com.brianstempin.vindiniumclient.bot.advanced.AdvancedGameState;
import com.brianstempin.vindiniumclient.datastructure.models.GameStep;
import com.brianstempin.vindiniumclient.datastructure.models.State;

/**
 * Created by Eric on 09.04.2016.
 */
public interface ILearningAlgorithm {
    void initialize(State currentState);

    GameStep step(State currentState);

    GameStep advancedStep(State currentState, AdvancedGameState gameState);
}
