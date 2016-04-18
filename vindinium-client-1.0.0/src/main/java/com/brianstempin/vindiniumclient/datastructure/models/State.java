package com.brianstempin.vindiniumclient.datastructure.models;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Christian on 09.04.2016.
 */
@Entity
@Table(name = "State")
public class State {
    @Id
    @Column(nullable = false, unique = true)
    private long stateId;
    private String comment;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "state", cascade = CascadeType.ALL)
    private List<StateAction> actions;
    private int bestAction;

    public State() {
    }

    public long getStateId() {
        return stateId;
    }

    public void setStateId(long stateId) {
        this.stateId = stateId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<StateAction> getActions() {
        return actions;
    }

    public void setActions(List<StateAction> actions) {
        this.actions = actions;
    }

    public void setBestAction(int actionId) { this.bestAction = actionId; }

    public int getBestAction() { return bestAction; }
}
