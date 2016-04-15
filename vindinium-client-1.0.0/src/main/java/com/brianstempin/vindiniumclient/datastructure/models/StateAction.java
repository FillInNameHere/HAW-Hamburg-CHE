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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int stateActionID;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "state")
    private State state;
    private double qValue;
    private int bestAction;

    public StateAction() {
    }

    public int getStateActionID() {
        return stateActionID;
    }

    public void setStateActionID(int stateActionID) {
        this.stateActionID = stateActionID;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public double getqValue() {
        return qValue;
    }

    public void setqValue(double qValue) {
        this.qValue = qValue;
    }

    public int getBestAction() {
        return bestAction;
    }

    public void setBestAction(int bestAction) {
        this.bestAction = bestAction;
    }
}
