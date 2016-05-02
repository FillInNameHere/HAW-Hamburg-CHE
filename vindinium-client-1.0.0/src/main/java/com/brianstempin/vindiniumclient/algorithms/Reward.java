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
        int rewardKat1 = -200;
        int rewardKat2 = -75;
        int rewardKat3 = -25;
        int rewardKat4 = -2;
        int rewardKat5 = 13;
        int rewardKat6 = 50;
        int rewardKat7 = 75;

        // Rückwandlung long -> int
        String stateStr = Long.toString(state);

        Integer ownIngameRanking = Integer.parseInt(String.valueOf(stateStr.charAt(0)));

        Integer ownLife = Integer.parseInt(String.valueOf(stateStr.charAt(1)));

        Integer ownMineCount = Integer.parseInt(String.valueOf(stateStr.charAt(2)));

        Integer closestPlayerDistanceBiggerFour = Integer.parseInt(String.valueOf(stateStr.charAt(3)));

        Integer closestPlayerMineCount = Integer.parseInt(String.valueOf(stateStr.charAt(4)));

        Integer closestPlayerLife = Integer.parseInt(String.valueOf(stateStr.charAt(5)));

        Integer timeRange = Integer.parseInt(String.valueOf(stateStr.charAt(6)));

        Integer ownGoldBiggerTwo = Integer.parseInt(String.valueOf(stateStr.charAt(7)));
        
        //Modus 0 => Schenke, Modus 1 => Mine, Modus 2 => Kampf

        // Kategorie 1 (-200)
        if (modus == 0 && ownGoldBiggerTwo == 0) {
            reward += rewardKat1;
        }

        if (modus == 0 && ownLife == 9) {
            reward += rewardKat1;
        }

        if (modus == 1 && ownLife <= 1) {
            reward += rewardKat1;
        }

        if (modus == 2 && closestPlayerMineCount == 0) {
            reward += rewardKat1;
        }

        if (modus == 2 && closestPlayerDistanceBiggerFour == 1) {
            reward += rewardKat1;
        }


        // Kategorie 2 (-75)
        if (modus == 0 && ownLife >= 7 && ownLife <= 8) {
            reward += rewardKat2;
        }

        if (modus == 1 && ownLife == 2) {
            reward += rewardKat2;
        }

        if (modus == 2 && ownLife <= 1) {
            reward += rewardKat2;
        }

        if (ownIngameRanking >= 3 && ownIngameRanking <= 4) {
            reward += rewardKat2;
        }


        // Kategorie 3 (-25)
        if (modus == 0 && ownLife == 6) {
            reward += rewardKat3;
        }

        if (modus == 2 && ownMineCount == 3) {
            reward += rewardKat3;
        }

        if (modus == 2 && closestPlayerMineCount == 1) {
            reward += rewardKat3;
        }

        if (modus == 2 && closestPlayerLife > ownLife) {
            reward += rewardKat3;
        }

        if (ownIngameRanking == 2) {
            reward += rewardKat3;
        }


        // Kategorie 4 (-2)
        if (modus == 0 && ownLife == 5) {
            reward += rewardKat4;
        }

        if (modus == 2 && ownMineCount == 2) {
            reward += rewardKat4;
        }

        if (modus == 2 && closestPlayerLife == ownLife) {
            reward += rewardKat4;
        }


        // Kategorie 5 (+13)
        if (modus == 0 && ownLife >= 3 && ownLife <= 4) {
            reward += rewardKat5;
        }

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


        // Kategorie 6 (+50)
        if (modus == 0 && ownLife <= 2) {
            reward += rewardKat6;
        }

        if (modus == 1 && ownLife >= 3) {
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
