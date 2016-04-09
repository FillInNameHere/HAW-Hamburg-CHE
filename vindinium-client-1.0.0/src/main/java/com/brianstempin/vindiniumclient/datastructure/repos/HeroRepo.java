package com.brianstempin.vindiniumclient.datastructure.repos;

import com.brianstempin.vindiniumclient.datastructure.models.Hero;
import com.brianstempin.vindiniumclient.util.persistenceservice.EntityManagerServices;

import javax.persistence.EntityManager;

/**
 * Created by Christian on 09.04.2016.
 */
public class HeroRepo {
    private EntityManager em = EntityManagerServices.getEntityManager();

    public HeroRepo() {
    }

    public Hero saveHero(Hero hero) {
        try {
            Hero savedHero  = new Hero();
            em.getTransaction().begin();
            savedHero = em.merge(hero);
            em.getTransaction().commit();
            return savedHero;
        }
        catch (Exception e) {
            em.getTransaction().rollback();
        }
        return null;
    }

    public Hero findHero(long id) {
        try {
            em.getTransaction().begin();
            Hero hero = em.find(Hero.class, id);
            em.getTransaction().commit();
            return hero;
        }
        catch (Exception e) {
            em.getTransaction().rollback();
        }
        return null;
    }
}
