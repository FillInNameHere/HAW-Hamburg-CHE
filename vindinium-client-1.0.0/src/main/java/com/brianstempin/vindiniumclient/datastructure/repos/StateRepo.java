package com.brianstempin.vindiniumclient.datastructure.repos;

import com.brianstempin.vindiniumclient.datastructure.models.State;
import com.brianstempin.vindiniumclient.util.persistenceservice.EntityManagerServices;

import javax.persistence.EntityManager;

/**
 * Created by Christian on 09.04.2016.
 */
public class StateRepo {
    private EntityManager em = EntityManagerServices.getEntityManager();

    public StateRepo() {
    }

    public State saveState(State state) {
        try {
            State savedState  = new State();
            em.getTransaction().begin();
            savedState = em.merge(state);
            em.getTransaction().commit();
            return savedState;
        }
        catch (Exception e) {
            em.getTransaction().rollback();
        }
        return null;
    }

    public State findState(long id) {
        try {
            em.getTransaction().begin();
            State state = em.find(State.class, id);
            em.getTransaction().commit();
            return state;
        }
        catch (Exception e) {
            em.getTransaction().rollback();
        }
        return null;
    }
}
