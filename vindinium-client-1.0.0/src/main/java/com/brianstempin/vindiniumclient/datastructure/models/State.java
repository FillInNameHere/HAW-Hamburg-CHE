package com.brianstempin.vindiniumclient.datastructure.models;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Christian on 09.04.2016.
 */
@Entity
@Table(name = "State")
public class State {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int stateId;
    private String stateComment;
    @OneToMany(cascade = CascadeType.ALL)
    private List<StateAction> actions;
    private Date discovery;

    public State() {
    }

    public String getStateComment() {
        return stateComment;
    }

    public void setStateComment(String stateComment) {
        this.stateComment = stateComment;
    }

    public List<StateAction> getActions() {
        return actions;
    }

    public void setActions(List<StateAction> actions) {
        this.actions = actions;
    }

    public Date getDiscovery() {
        return discovery;
    }

    public void setDiscovery(Date discovery) {
        this.discovery = discovery;
    }
}
