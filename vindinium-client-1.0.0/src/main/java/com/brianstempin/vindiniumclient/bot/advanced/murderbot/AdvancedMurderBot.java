package com.brianstempin.vindiniumclient.bot.advanced.murderbot;

import com.brianstempin.vindiniumclient.bot.BotMove;
import com.brianstempin.vindiniumclient.bot.advanced.AdvancedBot;
import com.brianstempin.vindiniumclient.bot.advanced.AdvancedGameState;
import com.brianstempin.vindiniumclient.bot.advanced.DijkstraResult;
import com.brianstempin.vindiniumclient.dto.GameState;

import java.util.Map;

import static com.brianstempin.vindiniumclient.bot.advanced.DijkstraResult.dijkstraSearch;

/**
 * An improvement upon com.brianstempin.vindiniumClient.bot.simple.MurderBot
 *
 * This class uses a built-in static method to perform the path search via Dijkstra and uses a simple version of
 * behavior trees to determine its next action.
 */
public class AdvancedMurderBot implements AdvancedBot {

    public static class GameContext {
        private final AdvancedGameState gameState;
        private final Map<GameState.Position, DijkstraResult> dijkstraResultMap;

        public GameContext(AdvancedGameState gameState, Map<GameState.Position, DijkstraResult> dijkstraResultMap) {
            this.gameState = gameState;
            this.dijkstraResultMap = dijkstraResultMap;
        }

        public AdvancedGameState getGameState() {
            return gameState;
        }

        public Map<GameState.Position, DijkstraResult> getDijkstraResultMap() {
            return dijkstraResultMap;
        }
    }



    private final Decision<GameContext, BotMove> decisioner;

    public AdvancedMurderBot() {

        // Chain decisioners together
        SquatDecisioner squatDecisioner = new SquatDecisioner();
        UnattendedMineDecisioner unattendedMineDecisioner = new UnattendedMineDecisioner(squatDecisioner);
        BotTargetingDecisioner botTargetingDecisioner = new BotTargetingDecisioner(unattendedMineDecisioner);
        EnRouteLootingDecisioner enRouteLootingDecisioner = new EnRouteLootingDecisioner(botTargetingDecisioner);

        HealDecisioner healDecisioner = new HealDecisioner();
        CombatOutcomeDecisioner combatOutcomeDecisioner = new CombatOutcomeDecisioner(botTargetingDecisioner,
                botTargetingDecisioner);
        CombatEngagementDecisioner combatEngagementDecisioner = new CombatEngagementDecisioner(combatOutcomeDecisioner,
                healDecisioner);
        BotWellnessDecisioner botWellnessDecisioner = new BotWellnessDecisioner(enRouteLootingDecisioner, combatEngagementDecisioner);

        this.decisioner = botWellnessDecisioner;

    }

    @Override
    public BotMove move(AdvancedGameState gameState) {

        Map<GameState.Position, DijkstraResult> dijkstraResultMap = dijkstraSearch(gameState);

        GameContext context = new GameContext(gameState, dijkstraResultMap);
        return this.decisioner.makeDecision(context);

    }

    @Override
    public void setup() {
        // No-op
    }

    @Override
    public void shutdown(String s) {
        // No-op
    }
}
