package com.brianstempin.vindiniumclient.datastructure.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Christian on 08.04.2016.
 */
@Entity
@Table(name = "GameLog")
public class GameLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long gameLogId;
    private String gameURL;
    private int whoAmI;
    private boolean win;
    private int rounds;
    private int tavern;
    private int totalMineCount;
    private int deathByEnemy;
    private int deathByMine;
    private int kills;
    private Date startingTime;
    private boolean crashed;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<GameStep> gameSteps;

    @OneToOne(cascade = CascadeType.ALL)
    private Hero hero1;
    @OneToOne(cascade = CascadeType.ALL)
    private Hero hero2;
    @OneToOne(cascade = CascadeType.ALL)
    private Hero hero3;
    @OneToOne(cascade = CascadeType.ALL)
    private Hero hero4;

    private int reward;
    private int biggestReward;
    private int smallestReward;

    public GameLog() {
        this.gameURL = "";
        this.win = false;
        this.crashed = false;
        this.whoAmI = 5;
        this.rounds = 0;
        this.tavern = 0;
        this.deathByMine = 0;
        this.deathByEnemy = 0;
        this.kills = 0;
        this.startingTime = new Date();
        this.reward = 0;
        this.gameSteps = new ArrayList<>();
    }

    public String getGameURL() {
        return gameURL;
    }

    public void setGameURL(String gameURL) {
        this.gameURL = gameURL;
    }

    public int getWhoAmI() {
        return whoAmI;
    }

    public void setWhoAmI(int whoAmI) {
        this.whoAmI = whoAmI;
    }

    public boolean isWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public int getTavern() {
        return tavern;
    }

    public void setTavern(int tavern) {
        this.tavern = tavern;
    }

    public int getTotalMineCount() {
        return totalMineCount;
    }

    public void setTotalMineCount(int totalMineCount) {
        this.totalMineCount = totalMineCount;
    }

    public int getDeathByEnemy() {
        return deathByEnemy;
    }

    public void setDeathByEnemy(int deathByEnemy) {
        this.deathByEnemy = deathByEnemy;
    }

    public int getDeathByMine() {
        return deathByMine;
    }

    public void setDeathByMine(int deatbByMine) {
        this.deathByMine = deatbByMine;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public Date getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(Date startingTime) {
        this.startingTime = startingTime;
    }

    public boolean isCrashed() {
        return crashed;
    }

    public void setCrashed(boolean crashed) {
        this.crashed = crashed;
    }

    public Hero getHero1() {
        return hero1;
    }

    public void setHero1(Hero hero1) {
        this.hero1 = hero1;
    }

    public Hero getHero2() {
        return hero2;
    }

    public void setHero2(Hero hero2) {
        this.hero2 = hero2;
    }

    public Hero getHero3() {
        return hero3;
    }

    public void setHero3(Hero hero3) {
        this.hero3 = hero3;
    }

    public Hero getHero4() {
        return hero4;
    }

    public void setHero4(Hero hero4) {
        this.hero4 = hero4;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public int getBiggestReward() {
        return biggestReward;
    }

    public int getSmallestReward() {
        return smallestReward;
    }

    public List<GameStep> getGameSteps() {
        return gameSteps;
    }

    public void setGameSteps(List<GameStep> gameSteps) {
        this.gameSteps = gameSteps;
    }

    public void addGameStep(GameStep gameStep) {
        this.gameSteps.add(gameStep);
    }

    public void setBiggestReward(int biggestReward) {
        this.biggestReward = biggestReward;
    }

    public void setSmallestReward(int smallestReward) {
        this.smallestReward = smallestReward;
    }
}
