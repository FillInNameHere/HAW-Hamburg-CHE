package com.brianstempin.vindiniumclient.datastructure.models;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Christian on 08.04.2016.
 */
@Entity
@Table(name = "GameLog")
public class GameLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String gameURL;
    private int whoAmI;
    private boolean win;
    private int rounds;
    private int tavern;
    private int mine;
    private int totalMineCount;
    private int deathByEnemy;
    private int deatbByMine;
    private int kills;
    private int blockedWay;
    private Date startingTime;
    private boolean crashed;

    @OneToOne(cascade = CascadeType.ALL)
    private Hero hero1;
    @OneToOne(cascade = CascadeType.ALL)
    private Hero hero2;
    @OneToOne(cascade = CascadeType.ALL)
    private Hero hero3;
    @OneToOne(cascade = CascadeType.ALL)
    private Hero hero4;

    private int reward;
    private int bigestReward;
    private int bigestRewardRound;
    private int smalestReward;
    private int smalestRewardRound;
    private int maxSteps;
    private int minSteps;
    private int steps;

    public GameLog() {
        this.gameURL ="";
        this.win = false;
        this.crashed = false;
        this.whoAmI = 5;
        this.rounds = 0;
        this.tavern = 0;
        this.mine = 0;
        this.deatbByMine = 0;
        this.deathByEnemy = 0;
        this.kills = 0;
        this.blockedWay = 0;
        this.startingTime = new Date();
        this.reward = 0;
        this.steps = 0;
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

    public int getMine() {
        return mine;
    }

    public void setMine(int mine) {
        this.mine = mine;
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

    public int getDeatbByMine() {
        return deatbByMine;
    }

    public void setDeatbByMine(int deatbByMine) {
        this.deatbByMine = deatbByMine;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getBlockedWay() {
        return blockedWay;
    }

    public void setBlockedWay(int blockedWay) {
        this.blockedWay = blockedWay;
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

    public int getBigestReward() {
        return bigestReward;
    }

    public void setBigestReward(int bigestReward) {
        this.bigestReward = bigestReward;
    }

    public int getBigestRewardRound() {
        return bigestRewardRound;
    }

    public void setBigestRewardRound(int bigestRewardRound) {
        this.bigestRewardRound = bigestRewardRound;
    }

    public int getSmalestReward() {
        return smalestReward;
    }

    public void setSmalestReward(int smalestReward) {
        this.smalestReward = smalestReward;
    }

    public int getSmalestRewardRound() {
        return smalestRewardRound;
    }

    public void setSmalestRewardRound(int smalestRewardRound) {
        this.smalestRewardRound = smalestRewardRound;
    }

    public int getMaxSteps() {
        return maxSteps;
    }

    public void setMaxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    public int getMinSteps() {
        return minSteps;
    }

    public void setMinSteps(int minSteps) {
        this.minSteps = minSteps;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
