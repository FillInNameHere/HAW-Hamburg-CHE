package com.brianstempin.vindiniumclient.datastructure;

import com.brianstempin.vindiniumclient.datastructure.models.State;
import com.brianstempin.vindiniumclient.datastructure.models.StateAction;
import com.brianstempin.vindiniumclient.datastructure.repos.StateActionRepo;
import com.brianstempin.vindiniumclient.datastructure.repos.StateRepo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christian on 11.04.2016.
 */
public class DatastructureTest {
    private StateRepo stateRepo;
    private StateActionRepo stateActionRepo;

    @Before
    public void setup() {
        stateRepo = new StateRepo();
        stateActionRepo = new StateActionRepo();
    }

    @Test
    public void StateTest() {
        State state = new State();
        state.setStateId(123456L);
        state.setComment("TestState");

        List<StateAction> actions = new ArrayList<>();

        StateAction a1 = new StateAction();
        // Important!
        a1.setState(state);
        a1.setBestAction(1);
        a1.setqValue(9.3);
        actions.add(a1);

        StateAction a2 = new StateAction();
        // Important!
        a2.setState(state);
        a2.setBestAction(2);
        a2.setqValue(2.5);
        actions.add(a2);

        // Important!
        state.setActions(actions);

        this.stateRepo.saveState(state);
        this.stateActionRepo.saveStateAction(a1);
        this.stateActionRepo.saveStateAction(a2);
    }
}
