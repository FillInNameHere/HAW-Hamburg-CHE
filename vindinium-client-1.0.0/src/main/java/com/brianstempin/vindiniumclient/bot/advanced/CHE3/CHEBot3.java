package com.brianstempin.vindiniumclient.bot.advanced.CHE3;

/**
 * Created by Henning Kahl on 30.05.2016.
 */

import com.brianstempin.vindiniumclient.algorithms.ILearningAlgorithm;
import com.brianstempin.vindiniumclient.algorithms.QLearning;
import com.brianstempin.vindiniumclient.algorithms.Reward3;
import com.brianstempin.vindiniumclient.bot.BotMove;
import com.brianstempin.vindiniumclient.bot.BotUtils;
import com.brianstempin.vindiniumclient.bot.advanced.*;
import com.brianstempin.vindiniumclient.datastructure.models.GameLog;
import com.brianstempin.vindiniumclient.datastructure.models.GameStep;
import com.brianstempin.vindiniumclient.datastructure.models.State;
import com.brianstempin.vindiniumclient.datastructure.repos.GameLogRepo;
import com.brianstempin.vindiniumclient.datastructure.repos.GameStepRepo;
import com.brianstempin.vindiniumclient.datastructure.repos.StateRepo;
import com.brianstempin.vindiniumclient.dto.GameState;
import com.brianstempin.vindiniumclient.util.vars.VarServices;
import com.brianstempin.vindiniumclient.util.vars.model.Vars;

import java.util.*;
import java.util.logging.Logger;

public class CHEBot3 implements AdvancedBot {
    ILearningAlgorithm learningAlgorithm;
    private Vars vars;
    private GameStepRepo gameStepRepo;
    private StateRepo stateRepo;
    private AdvancedGameState gameState;
    private Map<GameState.Position, DijkstraResult> dijkstraResult;
    private GameState.Hero topEnemy;
    private GameState.Hero closestEnemy;
    private int closestEnemyDistance;
    private int topEnemyDistance;
    private GameStep gameStep;
    private int modus;
    private GameLog gameLog;
    private long stateId;
    private Logger logger;
    private GameLogRepo gameLogRepo;
    private int ownInGameRanking;

    @Override
    public BotMove move(AdvancedGameState gameState) {
        this.gameState = gameState;
        this.dijkstraResult = DijkstraResult.dijkstraSearch(gameState);
        GameState.Position target;
        BotMove move;
        observeEnemies();

        stateId = generateState();
        State state = new State();
        state.setStateId(stateId);
        if(gameLog.getGameURL().equals("")) gameLog.setGameURL(gameState.getGameViewURL());
        doLearningAlgorithm();

        this.gameStep.setTurn(gameState.getTurn());

        switch (modus) {
            case 1:
                target = getClosestPub();
                break;
            case 2:
                target = getClosestMine();
                break;
            case 3:
                target = getClosestEnemy();
                break;
            case 4:
                target = getClosestEnemyMine();
                break;
            case 5:
                target = getTopEnemy();
                break;
            default:
                target = gameState.getMe().getPos();
        }

        if(target == null) target = gameState.getMe().getPos();
        List<GameState.Position> path = getPath(target);

        if(path.size() <= 1){
            move = BotUtils.directionTowards(gameState.getMe().getPos(), path.get(0));
        } else {
            move = BotUtils.directionTowards(gameState.getMe().getPos(), path.get(1));
        }
        gameLog = gameLogRepo.saveGameLog(gameLog);
        gameStep = gameStepRepo.saveGameStep(gameStep);
        return move;
    }

    @Override
    public void setup() {
        this.gameStepRepo = new GameStepRepo();
        this.stateRepo = new StateRepo();
        VarServices vs = new VarServices();
        this.vars = vs.getVars();
        this.logger = Logger.getLogger("CHEBot3");
        this.learningAlgorithm = new QLearning(stateRepo, gameStepRepo, vars, true, new Reward3());
        this.gameLogRepo = new GameLogRepo();
        this.gameLog = gameLogRepo.saveGameLog(new GameLog());
    }

    @Override
    public void shutdown(String reason) {
        gameLog.setEndMessage(reason);
        logger.info("shutting down bot, reason: " + reason);
        gameLog.setRounds(gameState.getTurn());
        gameLog.setEndMessage(reason);

        if (!gameState.getMe().isCrashed()) {
            gameLog.setCrashed(0);
        }

        if (reason.equals("")) {
            gameLog.setCrashed(0);
            if (ownInGameRanking == 1) {
                gameLog.setWin(1);
            } else {
                gameLog.setWin(0);
            }
        }

        gameStep.setGameLog(gameLog);
        gameStepRepo.saveGameStep(gameStep);

    }

    private void doLearningAlgorithm() {
        State currentState = new State();
        currentState.setStateId(stateId);
        gameStep = learningAlgorithm.step(currentState);
        BotUtils.BotAction action = gameStep.getChosenAction();
        if (action != BotUtils.BotAction.FORTFAHREN) {
            modus = action.ordinal();
        }

        gameLog.setReward(gameLog.getReward() + gameStep.getReward());

        if (gameStep.getReward() > gameLog.getBiggestReward()) {
            gameLog.setBiggestReward(gameStep.getReward());
        } else if (gameStep.getReward() < gameLog.getSmallestReward()) {
            gameLog.setSmallestReward(gameStep.getReward());
        }

        gameStep.setGameLog(this.gameLog);
    }

    private void observeEnemies() {
        int closestDistance = Integer.MAX_VALUE;
        int topGold = -1;
        Iterator<Map.Entry<GameState.Position, GameState.Hero>> heroIterator = gameState.getHeroesByPosition().entrySet().iterator();
        Map.Entry<GameState.Position, GameState.Hero> next;
        while (heroIterator.hasNext()) {
            next = heroIterator.next();
            boolean topGoldReset = false;
            if (next.getValue() == gameState.getMe()) continue;
            if (next.getValue().getGold() > topGold) {
                this.topEnemy = next.getValue();
                topGoldReset = true;
            }
            try {
                int distance = dijkstraResult.get(next.getKey()).getDistance();
                if (topGoldReset) topEnemyDistance = distance;
                if (distance < closestDistance) {
                    this.closestEnemy = next.getValue();
                    closestDistance = distance;
                }
            } catch (NullPointerException e) {
                logger.info("nullpointer!" + e);
            }
        }
        closestEnemyDistance = closestDistance;
    }

    private long generateState() {
        Map<Integer, GameState.Hero> heroMap = gameState.getHeroesById();
        List<Integer> sortGoldArray = new ArrayList<>();
        List<Integer> distinctSortGoldArray = new ArrayList<>();
        ownInGameRanking = 4;
        int ownMinesPercent = getAbstractMinecount(gameState.getMe().getMineCount());
        int ownHealth = (gameState.getMe().getLife() - 1) / 10;
        int topEnemyMinesPercent = getAbstractMinecount(topEnemy.getMineCount());
        int topEnemyHealth = (topEnemy.getLife() - 1) / 10;
        int closestEnemyMinesPercent = getAbstractMinecount(closestEnemy.getMineCount());
        int closestEnemyHealth = (closestEnemy.getLife() - 1) / 10;
        int closestEnemyDistanceAbstract = closestEnemyDistance;
        int topEnemyDistanceAbstract = topEnemyDistance;


        if (closestEnemyDistance > 9) {
            closestEnemyDistanceAbstract = 0;
        }

        if (topEnemyDistance > 9) {
            topEnemyDistanceAbstract = 0;
        }

        sortGoldArray.clear();
        distinctSortGoldArray.clear();
        for (int i = 1; i <= 4; i++) {
            sortGoldArray.add(heroMap.get(i).getGold());
        }
        distinctSortGoldArray.addAll(sortGoldArray);
        sortGoldArray.clear();
        sortGoldArray.addAll(distinctSortGoldArray);
        Collections.sort(sortGoldArray);
        Collections.reverse(sortGoldArray);
        for (int i = 0; i < sortGoldArray.size(); i++) {
            if (sortGoldArray.get(i).equals(gameState.getMe().getGold())) {
                ownInGameRanking = i + 1;
            }
        }

        return Long.parseLong(
                "" + ownInGameRanking
                        + "" + ownHealth
                        + "" + ownMinesPercent
                        + "" + topEnemy.getId()
                        + "" + topEnemyHealth
                        + "" + topEnemyMinesPercent
                        + "" + topEnemyDistanceAbstract
                        + "" + closestEnemy.getId()
                        + "" + closestEnemyHealth
                        + "" + closestEnemyMinesPercent
                        + "" + closestEnemyDistanceAbstract);
    }

    private int getAbstractMinecount(int minecount) {
        if (minecount == 0) return 0;
        double minecountPercent = minecount / gameState.getMines().size();
        if (minecountPercent == 1) {
            return 5;
        } else if (minecountPercent < 1 && minecountPercent >= 0.75) {
            return 4;
        } else if (minecountPercent < 0.75 && minecountPercent >= 0.5) {
            return 3;
        } else if (minecountPercent < 0.5 && minecountPercent >= 0.25) {
            return 2;
        } else if (minecountPercent < 0.25) {
            return 1;
        }
        return 0;
    }


    private GameState.Position getClosestEnemyMine() {
        GameState.Position closestMine = null;
        int closestDistance = Integer.MAX_VALUE;
        Iterator<Map.Entry<GameState.Position, Mine>> mineIterator = gameState.getMines().entrySet().iterator();
        Map.Entry<GameState.Position, Mine> next;
        while (mineIterator.hasNext()) {
            next = mineIterator.next();
            if (next.getValue().getOwner() != null) {
                if (next.getValue().getOwner().getId() == gameState.getMe().getId()) {
                    continue;
                }
                try {
                    int distance = dijkstraResult.get(next.getKey()).getDistance();
                    if (distance < closestDistance) {
                        closestMine = next.getKey();
                        closestDistance = distance;
                    }
                } catch (NullPointerException e) {
                    logger.info("nullpointer!");
                }
            }
        }
        return closestMine;
    }


    private GameState.Position getClosestMine() {
        GameState.Position closestMine = null;
        int closestDistance = Integer.MAX_VALUE;
        Iterator<Map.Entry<GameState.Position, Mine>> mineIterator = gameState.getMines().entrySet().iterator();
        Map.Entry<GameState.Position, Mine> next;
        while (mineIterator.hasNext()) {
            next = mineIterator.next();
            if (next.getValue().getOwner() != null && next.getValue().getOwner().getId() == gameState.getMe().getId()) {
                continue;
            }
            try {
                int distance = dijkstraResult.get(next.getKey()).getDistance();
                if (distance < closestDistance) {
                    closestMine = next.getKey();
                    closestDistance = distance;
                }
            } catch (NullPointerException e) {
                logger.info("nullpointer!");
            }
        }
        return closestMine;
    }

    private GameState.Position getClosestPub() {
        GameState.Position closestPub = null;
        int closestDistance = Integer.MAX_VALUE;
        Iterator<Map.Entry<GameState.Position, Pub>> pubIterator = gameState.getPubs().entrySet().iterator();
        Map.Entry<GameState.Position, Pub> next;
        while (pubIterator.hasNext()) {
            next = pubIterator.next();
            try {
                int distance = dijkstraResult.get(next.getKey()).getDistance();
                if (distance < closestDistance) {
                    closestPub = next.getKey();
                    closestDistance = distance;
                }
            } catch (NullPointerException e) {
                logger.info("nullpointer!");
            }
        }
        return closestPub;
    }

    private GameState.Position getClosestEnemy() {
        return closestEnemy.getPos();
    }

    private GameState.Position getTopEnemy() {
        return topEnemy.getPos();
    }

    private List<GameState.Position> getPath(GameState.Position target) {
        List<GameState.Position> path = new LinkedList<>();

        path.add(target);
        DijkstraResult next = this.dijkstraResult.get(target);
        while (next.getDistance() != 0) {
            path.add(next.getPrevious());
            next = this.dijkstraResult.get(next.getPrevious());
        }

        Collections.reverse(path);
        return path;
    }

}
