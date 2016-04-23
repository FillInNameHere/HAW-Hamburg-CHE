package com.brianstempin.vindiniumclient.algorithms;

import com.brianstempin.vindiniumclient.datastructure.models.GameStep;
import com.brianstempin.vindiniumclient.datastructure.models.State;

import static com.brianstempin.vindiniumclient.bot.BotUtils.BotAction;

/**
 * Created by Eric on 09.04.2016.
 */
public interface ILearningAlgorithm {
    void initialize(State currentState);

    GameStep step(State currentState);
}
