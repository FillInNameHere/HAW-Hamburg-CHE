package com.brianstempin.vindiniumclient.algorithms;

import com.brianstempin.vindiniumclient.bot.BotUtils;
import com.brianstempin.vindiniumclient.bot.simple.SimpleBot;
import com.brianstempin.vindiniumclient.datastructure.models.State;

import static com.brianstempin.vindiniumclient.bot.BotUtils.*;

/**
 * Created by Eric on 09.04.2016.
 */
public interface ILearningAlgorithm {
    void initialize(State currentState);
    BotAction step(State currentState);
}
