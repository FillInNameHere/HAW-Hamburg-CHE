package com.brianstempin.vindiniumclient.datastructure.models;

import com.brianstempin.vindiniumclient.bot.BotUtils;

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
    private BotUtils.BotAction action;

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


    public BotUtils.BotAction getAction() { return action; }

    public void setAction(BotUtils.BotAction action) { this.action = action; }
}
