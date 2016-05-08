package com.brianstempin.vindiniumclient.algorithms;

import com.brianstempin.vindiniumclient.bot.BotUtils.BotAction;
import com.brianstempin.vindiniumclient.datastructure.models.GameStep;
import com.brianstempin.vindiniumclient.datastructure.models.State;
import com.brianstempin.vindiniumclient.datastructure.models.StateAction;
import com.brianstempin.vindiniumclient.datastructure.repos.GameLogRepo;
import com.brianstempin.vindiniumclient.datastructure.repos.GameStepRepo;
import com.brianstempin.vindiniumclient.datastructure.repos.StateRepo;
import com.brianstempin.vindiniumclient.util.vars.model.Vars;

import java.util.ArrayList;
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
    private double learningRate = 0.1;
    private double explorationFactor = 0.15;
    private boolean eval = false;
    private GameStep lastGameStep;
    private GameStep currentGameStep;
    private GameStepRepo gameStepRepo;

    private double discount = 1;

    private QLearning() {
    }

    public QLearning(StateRepo stateRepo, GameStepRepo gameStepRepo, Vars v) {
        this.stateRepo = stateRepo;
        this.gameStepRepo = gameStepRepo;
        this.learningRate = v.getLearningRate();
        this.explorationFactor = v.getExplorationFactor();
    }

    @Override
    public void initialize(State currentState) {
        this.currentState = currentState;
    }

    @Override
    public GameStep step(State currentState) {
        this.currentGameStep = gameStepRepo.saveGameStep(new GameStep());
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

            currentGameStep.setBestActionThen(stateAction.getAction());

            if (Math.random() < explorationFactor) {
                stateAction = this.currentState.getActions().get((int) (Math.random() * 3));
            }

            currentGameStep.setChosenAction(stateAction.getAction());
            currentGameStep.setState(this.currentState);
            this.lastState = this.currentState;
            this.lastStateActions = this.lastState.getActions();
            this.lastAction = stateAction;
            if (eval) evaluateLastStep();
            eval = true;
            this.lastGameStep = this.currentGameStep;
            return currentGameStep;
        } else {
            currentGameStep.setChosenAction(BotAction.FORTFAHREN);
            currentGameStep.setState(this.lastState);
            currentGameStep.setBestActionThen(BotAction.FORTFAHREN);
            return currentGameStep;
        }
    }

    private State initState(State state) {
        stateRepo.saveState(state);
        List<StateAction> actions = new ArrayList<>();
        BotAction[] values = BotAction.values();
        for (int i = 0, valuesLength = values.length - 1; i < valuesLength; i++) {
            BotAction b = values[i];
            StateAction sa = new StateAction();
            sa.setqValue(0.0);
            sa.setState(state);
            sa.setAction(b);
            actions.add(sa);
        }
        state.setActions(actions);
        state.setBestAction((int) (Math.random() * 3));
        return stateRepo.saveState(state);
    }

    private void evaluateLastStep() {
        int reward = Reward.reward(lastAction.getAction().ordinal(), lastState.getStateId());
        double oldQVal = lastAction.getqValue();
        double bestQValNow = currentState.getActions().get(currentState.getBestAction()).getqValue();
        double newQVal = oldQVal + learningRate * (reward + discount * bestQValNow - oldQVal);
        lastAction.setqValue(newQVal);
        lastGameStep.setOldQval(oldQVal);
        lastGameStep.setNewQval(newQVal);
        lastGameStep.setReward(reward);

        StateAction bestAction = lastStateActions.get(lastState.getBestAction());
        for (int i = 0, actionsSize = lastStateActions.size(); i < actionsSize; i++) {
            StateAction sa = lastStateActions.get(i);
            if (sa.getqValue() > bestAction.getqValue()) {
                lastState.setBestAction(i);
            }
        }
        gameStepRepo.saveGameStep(lastGameStep);
        stateRepo.saveState(lastState);
    }
}
