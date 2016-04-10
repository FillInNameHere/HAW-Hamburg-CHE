package com.brianstempin.vindiniumclient.algorithms;

import com.brianstempin.vindiniumclient.bot.BotUtils;
import com.brianstempin.vindiniumclient.bot.BotUtils.BotAction;
import com.brianstempin.vindiniumclient.bot.simple.SimpleBot;
import com.brianstempin.vindiniumclient.datastructure.models.State;
import com.brianstempin.vindiniumclient.datastructure.models.StateAction;
import com.brianstempin.vindiniumclient.datastructure.repos.StateActionRepo;
import com.brianstempin.vindiniumclient.datastructure.repos.StateRepo;

/**
 * Created by Eric on 09.04.2016.
 */
public class QLearning implements ILearningAlgorithm {

    private State currentState;
    private StateRepo stateRepo;
    private StateActionRepo stateActionRepo;

    private QLearning(){}

    public QLearning(StateRepo stateRepo, StateActionRepo stateActionRepo){
        this.stateRepo = stateRepo;
        this.stateActionRepo = stateActionRepo;
    }

    @Override
    public void initialize(State currentState) {
        this.currentState = currentState;
    }

    @Override
    public BotAction step(State currentState) {

        if(!currentState.equals(this.currentState)) {
            //Todo: wie finden? hash-code?
            //State state = stateRepo.getState(currentState);
            //StateAction stateAction = stateActionRepo.findStateAction(currentState);
            StateAction stateAction = new StateAction();
            State state = new State();

            /**
             * algorithm
             */
            return BotAction.IDLE;
        } else {
            return BotAction.FORTFAHREN;
        }
    }
}
