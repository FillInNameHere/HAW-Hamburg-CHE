package com.brianstempin.vindiniumclient.bot.advanced;

import com.brianstempin.vindiniumclient.bot.BotMove;

public interface AdvancedBot {

    public BotMove move(AdvancedGameState gameState);

    /**
     * Called before the game is started
     */
    public void setup();

    /**
     * Called after the game
     * @param s
     */
    public void shutdown(String s);

}
