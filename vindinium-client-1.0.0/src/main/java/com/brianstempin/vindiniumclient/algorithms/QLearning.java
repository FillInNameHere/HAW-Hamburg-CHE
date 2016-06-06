package com.brianstempin.vindiniumclient.algorithms;

import com.brianstempin.vindiniumclient.bot.BotUtils.BotAction;
import com.brianstempin.vindiniumclient.bot.advanced.AdvancedGameState;
import com.brianstempin.vindiniumclient.datastructure.models.GameStep;
import com.brianstempin.vindiniumclient.datastructure.models.State;
import com.brianstempin.vindiniumclient.datastructure.models.StateAction;
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
    private boolean advancedActions = false;
    private double discount = 1;
    private IReward rewarder;
    private int actionRange;

    private QLearning() {
    }

    public QLearning(StateRepo stateRepo, GameStepRepo gameStepRepo, Vars v) {
        this.rewarder = new Reward();
        this.stateRepo = stateRepo;
        this.gameStepRepo = gameStepRepo;
        this.learningRate = v.getLearningRate();
        this.explorationFactor = v.getExplorationFactor();
        this.actionRange = 3;
    }

    public QLearning(StateRepo stateRepo, GameStepRepo gameStepRepo, Vars v, boolean advancedActions, IReward rewarder) {
        this.stateRepo = stateRepo;
        this.gameStepRepo = gameStepRepo;
        this.learningRate = v.getLearningRate();
        this.explorationFactor = v.getExplorationFactor();
        this.advancedActions = advancedActions;
        this.rewarder = rewarder;
        this.actionRange = 5;
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
                stateAction = this.currentState.getActions().get(this.currentState.getBestAction()-1);
            } else {
                if(advancedActions){
                    this.currentState = initState(currentState, 6);
                } else {
                    this.currentState = initState(currentState, 4);
                }
                stateAction = this.currentState.getActions().get(this.currentState.getBestAction()-1);
            }

            currentGameStep.setBestActionThen(stateAction.getAction());

            if (Math.random() < explorationFactor) {
                stateAction = this.currentState.getActions().get((int) (Math.random() * actionRange));
            }

            currentGameStep.setChosenAction(stateAction.getAction());
            currentGameStep.setState(this.currentState);
            this.lastState = this.currentState;
            this.lastStateActions = this.lastState.getActions();
            this.lastAction = stateAction;
            this.lastGameStep = this.currentGameStep;
            if (eval) evaluateLastStep();
            eval = true;
            return currentGameStep;
        } else {
            currentGameStep.setChosenAction(BotAction.FORTFAHREN);
            currentGameStep.setState(this.lastState);
            currentGameStep.setBestActionThen(BotAction.FORTFAHREN);
            gameStepRepo.saveGameStep(currentGameStep);
            return currentGameStep;
        }
    }



    private State initState(State state, int amount) {
        stateRepo.saveState(state);
        List<StateAction> actions = new ArrayList<>();
        BotAction[] values = BotAction.values();
            for (int i = 1, valuesLength = amount; i < valuesLength; i++) {
            BotAction b = values[i];
            StateAction sa = new StateAction();
            sa.setqValue(0.0);
            sa.setState(state);
            sa.setAction(b);
            actions.add(sa);
        }
        state.setActions(actions);
        state.setBestAction((int) (Math.random() * actionRange)+1);
        return stateRepo.saveState(state);
    }

    private void evaluateLastStep() {
        int reward = rewarder.reward(lastAction.getAction().ordinal()-1, lastState.getStateId());
        double oldQVal = lastAction.getqValue();
        double bestQValNow = currentState.getActions().get(currentState.getBestAction()-1).getqValue();
        double newQVal = oldQVal + learningRate * (reward + discount * bestQValNow - oldQVal);
        lastAction.setqValue(newQVal);
        lastGameStep.setOldQval(oldQVal);
        lastGameStep.setNewQval(newQVal);
        lastGameStep.setReward(reward);

        StateAction bestAction = lastStateActions.get(lastState.getBestAction()-1);
        for (int i = 1, actionsSize = lastStateActions.size(); i < actionsSize; i++) {
            StateAction sa = lastStateActions.get(i);
            if (sa.getqValue() > bestAction.getqValue()) {
                lastState.setBestAction(i);
            }
        }
        lastGameStep = gameStepRepo.saveGameStep(lastGameStep);
        lastState = stateRepo.saveState(lastState);
    }

    @Override
    public GameStep advancedStep(State state, AdvancedGameState gameState){
        ((Reward2) rewarder).setCurrentState(gameState);
        return this.step(state);
    }
}
