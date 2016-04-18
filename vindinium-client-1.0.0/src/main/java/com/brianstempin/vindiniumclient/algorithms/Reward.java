package com.brianstempin.vindiniumclient.algorithms;

/**
 * Created by Henning Kahl on 15.04.2016.
 */
public class Reward {

    public static int reward(int modus, long state){
        // return reward
        int reward = 0;

        // timeRangeFaktor
        double timeRangeFaktor = 0.125;

        // Reward Kategorien (Single Point Of Control)
        int rewardKat1 = -75;
        int rewardKat2 = -50;
        int rewardKat3 = -20;
        int rewardKat4 = -1;
        int rewardKat5 = 8;
        int rewardKat6 = 25;
        int rewardKat7 = 75;


        // Rückwandlung long -> int
        String stateStr = Long.toString(state);

        Character ownInGameRankingChar = stateStr.charAt(0);
        Integer ownIngameRanking = (int) ownInGameRankingChar;

        Character ownLifeChar = stateStr.charAt(1);
        Integer ownLife = (int) ownLifeChar;

        Character ownMineCountChar = stateStr.charAt(2);
        Integer ownMineCount = (int) ownMineCountChar;

        Character closestPlayerDistanceBiggerFourChar = stateStr.charAt(3);
        Integer closestPlayerDistanceBiggerFour = (int) closestPlayerDistanceBiggerFourChar;

        Character closestPlayerMineCountChar = stateStr.charAt(4);
        Integer closestPlayerMineCount = (int) closestPlayerMineCountChar;

        Character closestPlayerLifeChar = stateStr.charAt(5);
        Integer closestPlayerLife = (int) closestPlayerLifeChar;

        Character timeRangeChar = stateStr.charAt(6);
        Integer timeRange = (int) timeRangeChar;

        Character ownGoldBiggerTwoChar = stateStr.charAt(7);
        Integer ownGoldBiggerTwo = (int) ownGoldBiggerTwoChar;


        // Kategorie 1 (-75)
        if (modus == 0 && ownGoldBiggerTwo == 0) {
            reward += rewardKat1;
        }

        if (modus == 2 && closestPlayerMineCount == 0) {
            reward += rewardKat1;
        }

        if (modus == 0 && ownLife == 9) {
            reward += rewardKat1;
        }


        // Kategorie 2 (-50)
        if (ownIngameRanking == 3) {
            reward += rewardKat2;
        }

        if (ownIngameRanking == 4) {
            reward += rewardKat2;
        }

        if (modus == 0 && ownLife >= 6 && ownLife <= 8) {
            reward += rewardKat2;
        }


        // Kategorie 3 (-20)
        if (modus == 1 && ownLife <= 2) {
            reward += rewardKat3;
        }

        if (ownIngameRanking == 2) {
            reward += rewardKat3;
        }

        if (modus == 0 && ownLife >= 4 && ownLife <= 5) {
            reward += rewardKat3;
        }

        if (modus == 2 && ownMineCount == 3) {
            reward += rewardKat3;
        }

        if (modus == 2 && closestPlayerDistanceBiggerFour == 1) {
            reward += rewardKat3;
        }

        if (modus == 2 && closestPlayerMineCount == 1) {
            reward += rewardKat3;
        }

        if (modus == 2 && closestPlayerLife > ownLife) {
            reward += rewardKat3;
        }


        // Kategorie 4 (-1)
        if (modus == 0 && ownLife >= 2 && ownLife <= 3) {
            reward += rewardKat4;
        }

        if (modus == 2 && ownMineCount == 2) {
            reward += rewardKat4;
        }

        if (modus == 2 && closestPlayerLife == ownLife) {
            reward += rewardKat4;
        }

        if (modus == 0) {
            reward += rewardKat4;
        }

        if (modus == 1) {
            reward += rewardKat4;
        }

        if (modus == 2) {
            reward += rewardKat4;
        }


        // Kategorie 5 (+8)
        if (modus == 2 && ownMineCount <= 1) {
            reward += rewardKat5;
        }

        if (modus == 2 && closestPlayerDistanceBiggerFour == 0) {
            reward += rewardKat5;
        }

        if (modus == 2 && closestPlayerMineCount >= 2) {
            reward += rewardKat5;
        }

        if (modus == 2 && closestPlayerLife < ownLife) {
            reward += rewardKat5;
        }


        // Kategorie 6 (+25)
        if (modus == 1 && ownLife >= 3) {
            reward += rewardKat6;
        }

        if (modus == 0 && ownLife <= 1) {
            reward += rewardKat6;
        }


        // Kategorie 7 (+75)
        if (ownIngameRanking == 1) {
            reward += rewardKat7;
        }

        // timeRangeFaktor
        reward = (int) (reward * (1 + (timeRange * timeRangeFaktor)));

        return reward;
    }

}
