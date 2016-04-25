package com.brianstempin.vindiniumclient.algorithms;

import com.brianstempin.vindiniumclient.bot.BotUtils;
import com.brianstempin.vindiniumclient.bot.BotUtils.BotAction;
import com.brianstempin.vindiniumclient.datastructure.models.GameStep;
import com.brianstempin.vindiniumclient.datastructure.models.State;
import com.brianstempin.vindiniumclient.datastructure.models.StateAction;
import com.brianstempin.vindiniumclient.datastructure.repos.GameLogRepo;
import com.brianstempin.vindiniumclient.datastructure.repos.StateActionRepo;
import com.brianstempin.vindiniumclient.datastructure.repos.StateRepo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Eric on 09.04.2016.
 */
public class QLearning implements ILearningAlgorithm {

    private State currentState;
    private State lastState;
    private StateAction lastAction;
    private List<StateAction> lastStateActions;
    private StateRepo stateRepo;
    private GameLogRepo gameLogRepo;
    private double learningRate = 0.1;
    private double explorationFactor = 0.15;
    private boolean eval = false;
    private GameStep gameStep;

    private double discount = 1;

    private QLearning() {
    }

    public QLearning(StateRepo stateRepo) {
        this.stateRepo = stateRepo;
    }

    @Override
    public void initialize(State currentState) {
        this.currentState = currentState;
    }

    @Override
    public GameStep step(State currentState) {
        this.gameStep = new GameStep();
        if (this.currentState == null || currentState.getStateId() != this.currentState.getStateId()) {
            StateAction stateAction;
            State state = stateRepo.findState(currentState.getStateId());

            if (!(state == null)) {
                this.currentState = state;
                stateAction = this.currentState.getActions().get(this.currentState.getBestAction());
            } else {
                this.currentState = initState(currentState);
                stateAction = this.currentState.getActions().get(this.currentState.getBestAction());
            }

            gameStep.setBestActionThen(stateAction.getAction());

            if (Math.random() < explorationFactor) {
                stateAction = this.currentState.getActions().get((int) (Math.random() * 4));
            }

            gameStep.setChosenAction(stateAction.getAction());
            gameStep.setState(this.currentState);
            this.lastState = this.currentState;
            this.lastStateActions = this.lastState.getActions();
            this.lastAction = stateAction;
            if(eval) evaluateLastStep();
            eval = true;
            return gameStep;
        } else {
            gameStep.setChosenAction(BotAction.FORTFAHREN);
            gameStep.setState(this.lastState);
            gameStep.setBestActionThen(BotAction.FORTFAHREN);
            return gameStep;
        }
    }

    private State initState(State state) {
        stateRepo.saveState(state);
        List<StateAction> actions = new ArrayList<>();
        BotAction[] values = BotAction.values();
        for (int i = 0, valuesLength = values.length-1; i < valuesLength; i++) {
            BotAction b = values[i];
            StateAction sa = new StateAction();
            sa.setqValue(0.0);
            sa.setState(state);
            sa.setAction(b);
            actions.add(sa);
        }
        state.setActions(actions);
        state.setBestAction((int) (Math.random() * 4));
        return stateRepo.saveState(state);
    }

    private void evaluateLastStep() {
        int reward = Reward.reward(lastAction.getAction().ordinal(), lastState.getStateId());
        double oldQVal = lastAction.getqValue();
        double bestQValNow = currentState.getActions().get(currentState.getBestAction()).getqValue();
        double newQVal = oldQVal + learningRate * (reward + discount * bestQValNow - oldQVal);
        lastAction.setqValue(newQVal);
        gameStep.setOldQval(oldQVal);
        gameStep.setNewQval(newQVal);
        gameStep.setReward(reward);

        StateAction bestAction = lastStateActions.get(lastState.getBestAction());
        for (int i = 0, actionsSize = lastStateActions.size(); i < actionsSize; i++) {
            StateAction sa = lastStateActions.get(i);
            if (sa.getqValue() > bestAction.getqValue()) {
                lastState.setBestAction(i);
            }
        }
        stateRepo.saveState(lastState);
    }
}
