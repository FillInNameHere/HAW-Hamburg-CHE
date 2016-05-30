package com.brianstempin.vindiniumclient.algorithms;

/**
 * Created by Henning Kahl on 15.04.2016.
 */
public class Reward3 implements IReward{

    public int reward(int modus, long state){
        // return reward
        int reward = 0;

        // Reward Kategorien (Single Point Of Control)
        int rewardKat1 = 100;
        int rewardKat2 = 0;
        int rewardKat3 = -100;
        int rewardKat4 = -200;


        // Rückwandlung long -> int
        String stateStr = Long.toString(state);

        Integer ownIngameRanking = Integer.parseInt(String.valueOf(stateStr.charAt(0)));

        // Kategorie 1 (100)
        if (ownIngameRanking == 1) {
            reward = rewardKat1;
        }

        // Kategorie 2 (0)
        if (ownIngameRanking == 2) {
            reward = rewardKat2;
        }

        // Kategorie 3 (-100)
        if (ownIngameRanking == 3) {
            reward = rewardKat3;
        }

        // Kategorie 4 (-200)
        if (ownIngameRanking == 4) {
            reward = rewardKat4;
        }

        return reward;
    }
}
