package com.brianstempin.vindiniumclient.datastructure.repos;

import com.brianstempin.vindiniumclient.datastructure.models.GameLog;
import com.brianstempin.vindiniumclient.util.persistenceservice.EntityManagerServices;

import javax.persistence.EntityManager;

/**
 * Created by Christian on 08.04.2016.
 */
public class GameLogRepo {
    private EntityManager em = EntityManagerServices.getEntityManager();

    public GameLogRepo() {
    }

    public GameLog saveGameLog(GameLog gl) {
        try {
            GameLog savedGL  = new GameLog();
            em.getTransaction().begin();
            savedGL = em.merge(gl);
            em.getTransaction().commit();
            return savedGL;
        }
        catch (Exception e) {
            em.getTransaction().rollback();
        }
        return null;
    }

    public GameLog findGameLog(long id) {
        try {
            em.getTransaction().begin();
            GameLog gl = em.find(GameLog.class, id);
            em.getTransaction().commit();
            return gl;
        }
        catch (Exception e) {
            em.getTransaction().rollback();
        }
        return null;
    }
}
