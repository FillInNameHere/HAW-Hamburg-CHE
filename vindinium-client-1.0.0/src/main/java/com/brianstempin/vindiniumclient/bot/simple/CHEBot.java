package com.brianstempin.vindiniumclient.bot.simple;

import com.brianstempin.vindiniumclient.algorithms.ILearningAlgorithm;
import com.brianstempin.vindiniumclient.algorithms.QLearning;
import com.brianstempin.vindiniumclient.bot.BotMove;
import com.brianstempin.vindiniumclient.bot.BotUtils;
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

public class CHEBot implements SimpleBot {

    private Logger logger;
    private GameLog gameLog;
    private ILearningAlgorithm learningAlgorithm;
    private GameLogRepo gameLogRepo;
    private GameStepRepo gameStepRepo;
    private GameStep gameStep;

    public CHEBot() {
        logger = Logger.getLogger("CHEBot");
        gameStepRepo = new GameStepRepo();
        VarServices vs = new VarServices();
        Vars v = vs.getVars();
        learningAlgorithm = new QLearning(new StateRepo(), gameStepRepo, v);
    }

    // Zustand
    long currState = 19010900;
    String stateStr = "19010900";
    long state = 19010900;

    // Eigene Position
    Vertex ownPosition = null;

    // Eigenes Gold (abstrahiert)
    public int ownGoldBiggerTwo = 0;

    // Eigenes Leben (abstrahiert) (0: 0-10, 1: 11-20, 2: 21-30, 3: 31-40, 4: 41-50, 5: 51-60, 6: 61-70, 7: 71-80, 8: 81-90, 9: 91-100)
    public int ownLife = 9;

    // Eigene Minenanzahl (abstrahiert) (0: 0, 1: 1-3, 2: 4-7, 3: 8-inf)
    public int ownMineCount = 0;

    // Nähester Gegner Entfernung (abstrahiert)
    public int closestPlayerDistanceBiggerFour = 0;

    // Eigene Minenanzahl (abstrahiert) (0: 0, 1: 1-3, 2: 4-7, 3: 8-inf)
    public int closestPlayerMineCount = 0;

    // Nähester Gegner Leben (abstrahiert) (0: 0-10, 1: 11-20, 2: 21-30, 3: 31-40, 4: 41-50, 5: 51-60, 6: 61-70, 7: 71-80, 8: 81-90, 9: 91-100)
    public int closestPlayerLife = 9;

    // SpielModus (0: Schenke, 1: Mine, 2: Kampf)
    public int modus = 1;

    // Zeitbereich (abstahiert) (0: 0-299, 1: 300-599, 2: 600-899, 3: 900-1199)
    public int timeRange = 0;

    // Map-Informationen
    public int closestPlayerId = 0;

    // TeamPlay
    public boolean hero1IsTeamplayBot = false;
    public boolean hero2IsTeamplayBot = false;
    public boolean hero3IsTeamplayBot = false;
    public boolean hero4IsTeamplayBot = false;

    // Ranking (abstrahiert) (1: 1, 2: 2, 3: 3, 4: 4)
    public int ownInGameRanking = 1;
    public ArrayList sortGoldArray = new ArrayList();
    public Set distinctSortGoldArray = new HashSet();

    private void doLearningAlgorithm() {
        State currentState = new State();
        currentState.setStateId(state);
        gameStep = learningAlgorithm.step(currentState);
        BotUtils.BotAction action = gameStep.getChosenAction();
        if (action.ordinal() < 3) {
            modus = action.ordinal();
        }

        gameLog.setReward(gameLog.getReward()+gameStep.getReward());

        if(gameStep.getReward() > gameLog.getBiggestReward()){
            gameLog.setBiggestReward(gameStep.getReward());
        } else if(gameStep.getReward() < gameLog.getSmallestReward()){
            gameLog.setSmallestReward(gameStep.getReward());
        }

        gameStep.setGameLog(this.gameLog);
    }

    private List<Vertex> doDijkstra(GameState.Board board, GameState.Hero hero) {
        List<Vertex> vertexes = new LinkedList<Vertex>();

        // Graph ohne Kanten erstellen
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                Vertex v = new Vertex();
                GameState.Position pos = new GameState.Position(row, col);
                int tileStart = row * board.getSize() * 2 + (col * 2);
                v.setTileType(board.getTiles().substring(tileStart, tileStart + 1 + 1));
                v.setPosition(pos);
                vertexes.add(v);
            }
        }

        // Kanten hinzufügen
        for (int i = 0; i < board.getSize() * board.getSize(); i++) {
            int row = i % (board.getSize());
            int col = i / board.getSize();

            Vertex v = vertexes.get(i);

            // Überprüfe: Sind wir auf diesem Feld?
            if (v.getPosition().getX() == hero.getPos().getX() && v.getPosition().getY() == hero.getPos().getY()) {
                ownPosition = v;
            }

            // Überprüfe: Felder worauf etwas steht werden nicht einbezogen.
            if (v.getTileType().equals("##") || v.getTileType().equals("[]") || v.getTileType().startsWith("$")) {
                continue;
            }

            for (int j = col - 1; j <= col + 1; j += 2) {
                if (j >= 0 && j < board.getSize()) {
                    Vertex adjacentV = vertexes.get(j * board.getSize() + row);
                    v.getAdjacencies().add(adjacentV);
                }
            }

            for (int j = row - 1; j <= row + 1; j += 2) {
                if (j >= 0 && j < board.getSize()) {
                    Vertex adjacentV = vertexes.get(col * board.getSize() + j);
                    v.getAdjacencies().add(adjacentV);
                }
            }
        }

        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
        vertexQueue.add(ownPosition);
        ownPosition.setMinDistance(0);

        while (!vertexQueue.isEmpty()) {
            Vertex v = vertexQueue.poll();
            double distance = v.getMinDistance() + 1;

            for (Vertex neighbor : v.getAdjacencies()) {
                if (distance < neighbor.getMinDistance()) {
                    neighbor.setMinDistance(distance);
                    neighbor.setPrevious(v);
                    vertexQueue.remove(neighbor);
                    vertexQueue.add(neighbor);
                }
            }
        }
        return vertexes;
    }

    private static List<Vertex> getPath(Vertex target) {
        List<Vertex> path = new LinkedList<Vertex>();

        path.add(target);
        Vertex next = target;
        while (next.getPrevious().getMinDistance() != 0) {
            path.add(next.getPrevious());
            next = next.getPrevious();
        }

        Collections.reverse(path);
        return path;
    }

    @Override
    public BotMove move(GameState gameState) {
        //Führe Lern-Algorithmus aus
        doLearningAlgorithm();
        this.gameStep.setTurn(gameState.getGame().getTurn());
        gameLog.setGameURL(gameState.getViewUrl());
        gameLog.setWhoAmI(gameState.getHero().getId());

        if ((gameState.getGame().getTurn() >= 4) && (gameState.getGame().getTurn() <= 7)) {
            if (gameState.getGame().getHeroes().get(0).getName().equals("HAW-Hamburg CHE")) {
                hero1IsTeamplayBot = true;
            }
            if (gameState.getGame().getHeroes().get(1).getName().equals("HAW-Hamburg CHE")) {
                hero2IsTeamplayBot = true;
            }
            if (gameState.getGame().getHeroes().get(2).getName().equals("HAW-Hamburg CHE")) {
                hero3IsTeamplayBot = true;
            }
            if (gameState.getGame().getHeroes().get(3).getName().equals("HAW-Hamburg CHE")) {
                hero4IsTeamplayBot = true;
            }
        }

        //Pfade suchen nächste(r) (Gegner, Schenke, Mine)
        List<Vertex> vertexes = doDijkstra(gameState.getGame().getBoard(), gameState.getHero());

        Vertex closestPub = null;
        Vertex closestPlayer = null;
        Vertex closestMine = null;

        for (Vertex v : vertexes) {
            //Bestimmung der nächsten Mine
            if (v.getTileType().startsWith("$") && v.getMinDistance() != 0 && v.getMinDistance() != Double.POSITIVE_INFINITY && (closestMine == null || closestMine.getMinDistance() > v.getMinDistance())) {
                if (!(v.getTileType().startsWith("$" + gameState.getHero().getId())) && !(v.getTileType().equals("$1") && hero1IsTeamplayBot) && !(v.getTileType().equals("$2") && hero2IsTeamplayBot) && !(v.getTileType().equals("$3") && hero3IsTeamplayBot) && !(v.getTileType().equals("$4") && hero4IsTeamplayBot)) {
                    closestMine = v;
                }
            }

            //Bestimmung der nächsten Schenke
            if (v.getTileType().equals("[]") && v.getMinDistance() != Double.POSITIVE_INFINITY && (closestPub == null || closestPub.getMinDistance() > v.getMinDistance())) {
                closestPub = v;
            }

            //Bestimmung des nächsten Gegners
            if (v.getTileType().startsWith("@") && v.getMinDistance() != Double.POSITIVE_INFINITY && (closestPlayer == null || closestPlayer.getMinDistance() > v.getMinDistance())) {
                if (!(v.getTileType().startsWith("@" + gameState.getHero().getId())) && !(v.getTileType().equals("@1") && hero1IsTeamplayBot) && !(v.getTileType().equals("@2") && hero2IsTeamplayBot) && !(v.getTileType().equals("@3") && hero3IsTeamplayBot) && !(v.getTileType().equals("@4") && hero4IsTeamplayBot)) {
                    if (v.getTileType().equals("@1")) {
                        closestPlayerId = 0;
                    }
                    if (v.getTileType().equals("@2")) {
                        closestPlayerId = 1;
                    }
                    if (v.getTileType().equals("@3")) {
                        closestPlayerId = 2;
                    }
                    if (v.getTileType().equals("@4")) {
                        closestPlayerId = 3;
                    }
                    closestPlayer = v;
                }
            }
        }

        currState = state;

        // Nähester Gegner Entfernung (abstrahiert) (> 4)
        if ((getPath(closestPlayer).size() - 1) > 4) {
            closestPlayerDistanceBiggerFour = 1;
        } else {
            closestPlayerDistanceBiggerFour = 0;
        }

        // Eigenes Gold (abstrahiert) (>= 2)
        if (gameState.getHero().getGold() >= 2) {
            ownGoldBiggerTwo = 1;
        } else {
            ownGoldBiggerTwo = 0;
        }

        // Eigene Minenanzahl (abstrahiert)
        if (gameState.getHero().getMineCount() == 0) {
            ownMineCount = 0;
        }
        if ((gameState.getHero().getMineCount()) > 0 && (gameState.getHero().getMineCount() <= 3)) {
            ownMineCount = 1;
        }
        if ((gameState.getHero().getMineCount()) > 3 && (gameState.getHero().getMineCount() <= 7)) {
            ownMineCount = 2;
        }
        if (gameState.getHero().getMineCount() >= 8) {
            ownMineCount = 3;
        }

        // Eigenes Leben (abstrahiert)
        if (gameState.getHero().getLife() == 0) {
            ownLife = 0;
        } else {
            ownLife = ((gameState.getHero().getLife() - 1) / 10);
        }

        // Nähester Gegner Leben (abstrahiert)
        if (gameState.getGame().getHeroes().get(closestPlayerId).getLife() == 0) {
            closestPlayerLife = 0;
        } else {
            closestPlayerLife = ((gameState.getGame().getHeroes().get(closestPlayerId).getLife() - 1) / 10);
        }

        // Nähester Gegner Minenanzahl (abstrahiert)
        if (gameState.getGame().getHeroes().get(closestPlayerId).getMineCount() == 0) {
            closestPlayerMineCount = 0;
        }
        if ((gameState.getGame().getHeroes().get(closestPlayerId).getMineCount()) > 0 && (gameState.getGame().getHeroes().get(closestPlayerId).getMineCount() <= 3)) {
            closestPlayerMineCount = 1;
        }
        if ((gameState.getGame().getHeroes().get(closestPlayerId).getMineCount()) > 3 && (gameState.getGame().getHeroes().get(closestPlayerId).getMineCount() <= 7)) {
            closestPlayerMineCount = 2;
        }
        if (gameState.getGame().getHeroes().get(closestPlayerId).getMineCount() >= 8) {
            closestPlayerMineCount = 3;
        }

        // Spieldauer (abstrahiert)
        if ((gameState.getGame().getTurn() >= 300) && (gameState.getGame().getTurn() <= 599)) {
            timeRange = 1;
        }
        if ((gameState.getGame().getTurn() >= 600) && (gameState.getGame().getTurn() <= 899)) {
            timeRange = 2;
        }
        if ((gameState.getGame().getTurn() >= 900) && (gameState.getGame().getTurn() <= 1199)) {
            timeRange = 3;
        }

        // Eigene Platzierung
        sortGoldArray.clear();
        distinctSortGoldArray.clear();
        for (int i = 0; i < 4; i++) {
            sortGoldArray.add(gameState.getGame().getHeroes().get(i).getGold());
        }
        distinctSortGoldArray.addAll(sortGoldArray);
        sortGoldArray.clear();
        sortGoldArray.addAll(distinctSortGoldArray);
        Collections.sort(sortGoldArray);
        Collections.reverse(sortGoldArray);
        for (int i = 0; i < sortGoldArray.size(); i++) {
            if (sortGoldArray.get(i).equals(gameState.getHero().getGold())) {
                ownInGameRanking = i + 1;
            }
        }

        // Notfallmodus
        Vertex move;
        BotMove botMove = null;

        // Schenke
        if (modus == 0) {
            move = getPath(closestPub).get(0);
            botMove = BotUtils.directionTowards(gameState.getHero().getPos(), move.getPosition());
        }

        // Kampf
        if (modus == 2) {
            // Sonderfall alle vier Spieler sind TeamPlayBots
            if (getPath(closestPlayer).get(0) == null) {
                modus = 1;
            } else {
                move = getPath(closestPlayer).get(0);
                botMove = BotUtils.directionTowards(gameState.getHero().getPos(), move.getPosition());
            }
        }

        // Mine
        if (modus == 1) {
            // Sonderfall Hero besitzt ale Minen
            if (getPath(closestMine).get(0) == null){
                botMove = BotMove.STAY;
            } else {
                move = getPath(closestMine).get(0);
                botMove = BotUtils.directionTowards(gameState.getHero().getPos(), move.getPosition());
            }
        }

        stateStr = "" + ownInGameRanking + "" + ownLife + "" + ownMineCount + "" + closestPlayerDistanceBiggerFour + "" + closestPlayerMineCount + "" + closestPlayerLife + "" + timeRange + "" + ownGoldBiggerTwo + "";
        state = Long.parseLong(stateStr);

        //logger.info("State: " + state + " (ownInGameRanking,ownLife,ownMineCount,closestPlayerDistanceBiggerFour,closestPlayerMineCount,closestPlayerLife,timeRange,ownGoldBiggerTwo)");
        logger.info("" + gameState.getGame().getTurn());

        gameLog = gameLogRepo.saveGameLog(gameLog);
        gameStepRepo.saveGameStep(gameStep);
        return botMove;
    }

    @Override
    public void setup(GameLogRepo gameLogRepo, GameLog gameLog) {
        logger.info("setting up bot...");
        this.gameLogRepo = gameLogRepo;
        this.gameLog = gameLog;
    }

    public void shutdown(String reason, GameState gameState) {
        logger.info("shutting down bot, reason: "+reason);
        gameLog.setRounds(gameState.getGame().getTurn());
        gameLog.setEndMessage(reason);

        if(!gameState.getHero().isCrashed()){
            gameLog.setCrashed(0);
        }

        if (gameState.getGame().isFinished()) {
            if(ownInGameRanking == 1){
                System.out.println("Me win!");
                gameLog.setWin(1);
            } else {
                System.out.println("Me no win!");
            }
        } else {
            gameLog.setCrashed(1);
        }
        gameLogRepo.saveGameLog(gameLog);
    }

    private static class Vertex implements Comparable<Vertex> {
        private String tileType;
        private List<Vertex> adjacencies;
        private double minDistance;
        private Vertex previous;
        private GameState.Position position;

        private Vertex() {
            this.minDistance = Double.POSITIVE_INFINITY;

            // Safe default size...we want to avoid resizing
            this.adjacencies = new ArrayList<Vertex>(50 * 50);
        }

        public GameState.Position getPosition() {
            return position;
        }

        public void setPosition(GameState.Position position) {
            this.position = position;
        }

        public String getTileType() {
            return tileType;
        }

        public void setTileType(String tileType) {
            this.tileType = tileType;
        }

        public List<Vertex> getAdjacencies() {
            return adjacencies;
        }

        public void setAdjacencies(List<Vertex> adjacencies) {
            this.adjacencies = adjacencies;
        }

        public double getMinDistance() {
            return minDistance;
        }

        public void setMinDistance(double minDistance) {
            this.minDistance = minDistance;
        }

        public Vertex getPrevious() {
            return previous;
        }

        public void setPrevious(Vertex previous) {
            this.previous = previous;
        }

        @Override
        public int compareTo(Vertex o) {
            return Double.compare(getMinDistance(), o.getMinDistance());
        }
    }
}
