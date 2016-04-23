package com.brianstempin.vindiniumclient.datastructure.models;

import com.brianstempin.vindiniumclient.bot.BotUtils;
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
    private BotAction chosenAction;
    private BotAction bestActionThen;

    public GameStep(){
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
}
