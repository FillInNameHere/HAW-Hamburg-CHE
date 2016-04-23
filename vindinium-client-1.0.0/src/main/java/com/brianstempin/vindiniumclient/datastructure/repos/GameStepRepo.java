package com.brianstempin.vindiniumclient.datastructure.repos;

import com.brianstempin.vindiniumclient.datastructure.models.GameLog;
import com.brianstempin.vindiniumclient.datastructure.models.GameStep;
import com.brianstempin.vindiniumclient.util.persistenceservice.EntityManagerServices;

import javax.persistence.EntityManager;

/**
 * Created by Eric on 23.04.2016.
 */
public class GameStepRepo {
    private EntityManager em = EntityManagerServices.getEntityManager();

    public GameStepRepo() {
    }

    public GameStep saveGameStep(GameStep gs) {
        try {
            GameStep savedGS  = new GameStep();
            em.getTransaction().begin();
            savedGS = em.merge(gs);
            em.getTransaction().commit();
            return savedGS;
        }
        catch (Exception e) {
            em.getTransaction().rollback();
        }
        return null;
    }

    public GameStep findGameStep(long id) {
        try {
            em.getTransaction().begin();
            GameStep gs = em.find(GameStep.class, id);
            em.getTransaction().commit();
            return gs;
        }
        catch (Exception e) {
            em.getTransaction().rollback();
        }
        return null;
    }
}
