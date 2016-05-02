package com.brianstempin.vindiniumclient.datastructure.models;

import com.brianstempin.vindiniumclient.bot.BotUtils.BotAction;

import javax.persistence.*;

/**
 * Created by Eric on 23.04.2016.
 */
@Entity
@Table(name = "GameStep")
public class GameStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name = "gameLogId")
    private long gameStepId;
    @OneToOne
    private State state;
    @OneToOne(cascade = CascadeType.ALL)
    private GameLog gameLog;
    private BotAction chosenAction;
    private BotAction bestActionThen;
    private double oldQval;
    private double newQval;
    private int reward;


    public GameStep() {
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public BotAction getChosenAction() {
        return chosenAction;
    }

    public void setChosenAction(BotAction chosenAction) {
        this.chosenAction = chosenAction;
    }

    public BotAction getBestActionThen() {
        return bestActionThen;
    }

    public void setBestActionThen(BotAction bestActionThen) {
        this.bestActionThen = bestActionThen;
    }

    public double getNewQval() {
        return newQval;
    }

    public void setNewQval(double newQval) {
        this.newQval = newQval;
    }

    public double getOldQval() {
        return oldQval;
    }

    public void setOldQval(double oldQval) {
        this.oldQval = oldQval;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public GameLog getGameLog() {
        return gameLog;
    }

    public void setGameLog(GameLog gameLog) {
        this.gameLog = gameLog;
    }
}
