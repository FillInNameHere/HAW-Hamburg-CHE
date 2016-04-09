package com.brianstempin.vindiniumclient.datastructure.models;

import com.brianstempin.vindiniumclient.bot.BotMove;

import javax.persistence.*;

/**
 * Created by Christian on 09.04.2016.
 */
@Entity
@Table(name = "StateAction")
public class StateAction {
    @Id
    @GeneratedValue
    private int stateActionID;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "state")
    private State state;
    private String description;
    private BotMove action;
    private double qValue;
    private int used;
    private int explored;
    private int bestAction;

    public StateAction() {
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BotMove getAction() {
        return action;
    }

    public void setAction(BotMove action) {
        this.action = action;
    }

    public double getqValue() {
        return qValue;
    }

    public void setqValue(double qValue) {
        this.qValue = qValue;
    }

    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
    }

    public int getExplored() {
        return explored;
    }

    public void setExplored(int explored) {
        this.explored = explored;
    }

    public int getBestAction() {
        return bestAction;
    }

    public void setBestAction(int bestAction) {
        this.bestAction = bestAction;
    }
}
