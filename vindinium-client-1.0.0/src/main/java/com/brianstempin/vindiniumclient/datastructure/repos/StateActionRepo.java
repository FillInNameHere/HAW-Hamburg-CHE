package com.brianstempin.vindiniumclient.datastructure.repos;

import com.brianstempin.vindiniumclient.datastructure.models.StateAction;
import com.brianstempin.vindiniumclient.util.persistenceservice.EntityManagerServices;

import javax.persistence.EntityManager;

/**
 * Created by Christian on 09.04.2016.
 */
public class StateActionRepo {
    private EntityManager em = EntityManagerServices.getEntityManager();

    public StateActionRepo() {
    }

    public StateAction saveStateAction(StateAction state) {
        try {
            StateAction savedStateAction  = new StateAction();
            em.getTransaction().begin();
            savedStateAction = em.merge(state);
            em.getTransaction().commit();
            return savedStateAction;
        }
        catch (Exception e) {
            em.getTransaction().rollback();
        }
        return null;
    }

    public StateAction findState(long id) {
        try {
            em.getTransaction().begin();
            StateAction state = em.find(StateAction.class, id);
            em.getTransaction().commit();
            return state;
        }
        catch (Exception e) {
            em.getTransaction().rollback();
        }
        return null;
    }
}
