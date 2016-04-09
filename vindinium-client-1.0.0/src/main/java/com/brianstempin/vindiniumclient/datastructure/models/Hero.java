package com.brianstempin.vindiniumclient.datastructure.models;

import javax.persistence.*;

/**
 * Created by Christian on 09.04.2016.
 */
@Entity
@Table(name = "Hero")
public class Hero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private int gold;

    public Hero() {
        name = "";
        gold = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }
}
