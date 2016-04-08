package com.brianstempin.vindiniumclient.datastructure.models;

import javax.persistence.*;

/**
 * Created by Christian on 08.04.2016.
 */
@Entity
@Table
public class GameLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;

    public GameLog() {
    }

    public GameLog(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameLog gameLog = (GameLog) o;

        if (id != gameLog.id) return false;
        return !(name != null ? !name.equals(gameLog.name) : gameLog.name != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
