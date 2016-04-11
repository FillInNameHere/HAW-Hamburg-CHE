package com.brianstempin.vindiniumclient.bot.simple;

import com.brianstempin.vindiniumclient.bot.BotMove;
import com.brianstempin.vindiniumclient.bot.BotUtils;
import com.brianstempin.vindiniumclient.dto.GameState;

import java.util.*;
import java.util.logging.Logger;

public class CHEBot implements SimpleBot {

    private Logger logger;

    public CHEBot() {
        logger = Logger.getLogger("CHEBot");
    }

    // Zustand
    long currState = 190109000;
    String stateStr = "190109000";
    long state = 190109000;

    // Eigene Position
    Vertex ownPosition = null;

    // Eigenes Gold (abstrahiert)
    public int ownGoldBiggerTwo = 0;

    // Eigenes Leben (abstrahiert) (0: 0-10, 1: 11-20, 2: 21-30, 3: 31-40, 4: 41-50, 5: 51-60, 6: 61-70, 7: 71-80, 8: 81-90, 9: 91-100)
    public int ownLife = 9;

    // Eigene Minenanzahl (abstrahiert) (0: 0, 1: 1-3, 2: 4-7, 3: 8-inf)
    public int ownMineCount = 0;

    // Sieg (abstrahiert)
    public int win = 0;

    // Nähester Gegner Entfernung (abstrahiert)
    public int closestPlayerDistanceBiggerFour = 0;

    // Eigene Minenanzahl (abstrahiert) (0: 0, 0: 1-3, 0: 4-7, 0: 8-inf)
    public int closestPlayerMineCount = 0;

    // Nähester Gegner Entfernung (abstrahiert) (0: 0-10, 1: 11-20, 2: 21-30, 3: 31-40, 4: 41-50, 5: 51-60, 6: 61-70, 7: 71-80, 8: 81-90, 9: 91-100)
    public int closestPlayerLife = 9;

    // SpielModus (0: Schenke; 1: Mine; 2: Kampf; 3: Stehen;)
    public int modus = 1;;

    // Zeitbereich (abstahiert) (0: Turn 0-299; 1: Turn 300-599; 2: Turn 600-899; 3: Turn 900-1199;)
    public int timeRange = 0;

    // Map-Informationen
    public int mapSize = 0;
    public int tavernCount = 0;
    public int tavernCountHelper = 0;
    public int globalMineCount = 0;
    public int globalMineCountHelper = 0;
    public int closestPlayerId = 0;

    // TeamPlay
    public boolean hero1IsTeamplayBot = false;
    public boolean hero2IsTeamplayBot = false;
    public boolean hero3IsTeamplayBot = false;
    public boolean hero4IsTeamplayBot = false;

    // Ranking
    public int ownInGameRanking = 1;
    public ArrayList sortGoldArray = new ArrayList();
    public Set distinctSortGoldArray = new HashSet();

    public int lastElement = 0;

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
            if (v.getPosition().getX() == hero.getPos().getX() && v.getPosition().getY() == hero.getPos().getY()){
                ownPosition = v;
            }

            // Überprüfe: Felder worauf etwas steht werden nicht einbezogen.
            if (v.getTileType().equals("##") || v.getTileType().equals("[]") || v.getTileType().startsWith("$")) {

                //Mapscan: TavernenAnzahl
                if (v.getTileType().equals("[]")){
                    tavernCountHelper++;
                }

                //Mapscan: MinenAnzahl
                if (v.getTileType().startsWith("$")){
                    globalMineCountHelper++;
                }

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

            //MapInfos besorgen
            mapSize = gameState.getGame().getBoard().getSize() * gameState.getGame().getBoard().getSize();
            globalMineCount = globalMineCountHelper;
            tavernCount = tavernCountHelper;
        }

        //Pfade suchen nächste(r) (Gegner, Schenke, Mine)
        List<Vertex> vertexes = doDijkstra(gameState.getGame().getBoard(), gameState.getHero());

        Vertex closestPub = null;
        Vertex closestPlayer = null;
        Vertex closestMine = null;

        for (Vertex v : vertexes) {
            //Bestimmung der nächsten Mine
            if (v.getTileType().startsWith("$") && v.getMinDistance() != 0 && v.getMinDistance() != Double.POSITIVE_INFINITY && (closestMine == null || closestMine.getMinDistance() > v.getMinDistance())) {
                if(!(v.getTileType().startsWith("$" + gameState.getHero().getId())) && !(v.getTileType().equals("$1") && hero1IsTeamplayBot) && !(v.getTileType().equals("$2") && hero2IsTeamplayBot) && !(v.getTileType().equals("$3") && hero3IsTeamplayBot) && !(v.getTileType().equals("$4") && hero4IsTeamplayBot)){
                    closestMine = v;
                }
            }

            //Bestimmung der nächsten Schenke
            if (v.getTileType().equals("[]") && v.getMinDistance() != Double.POSITIVE_INFINITY && (closestPub == null || closestPub.getMinDistance() > v.getMinDistance())) {
                closestPub = v;
            }

            //Bestimmung des nächsten Gegners
            if (v.getTileType().startsWith("@") && v.getMinDistance() != Double.POSITIVE_INFINITY && (closestPlayer == null || closestPlayer.getMinDistance() > v.getMinDistance())){
                if(!(v.getTileType().startsWith("@" + gameState.getHero().getId())) && !(v.getTileType().equals("@1") && hero1IsTeamplayBot) && !(v.getTileType().equals("@2") && hero2IsTeamplayBot) && !(v.getTileType().equals("@3") && hero3IsTeamplayBot) && !(v.getTileType().equals("@4") && hero4IsTeamplayBot)){
                    if (v.getTileType().equals("@1")){
                        closestPlayerId = 0;
                    }
                    if (v.getTileType().equals("@2")){
                        closestPlayerId = 1;
                    }
                    if (v.getTileType().equals("@3")){
                        closestPlayerId = 2;
                    }
                    if (v.getTileType().equals("@4")){
                        closestPlayerId = 3;
                    }
                    closestPlayer = v;
                }
            }
        }

        currState = state;

        // Nähester Gegner Entfernung (abstrahiert) > 4
        if (getPath(closestPlayer).get(lastElement).getMinDistance() > 4){
            closestPlayerDistanceBiggerFour = 1;
        } else {
            closestPlayerDistanceBiggerFour = 0;
        }

        // Eigenes Gold (abstrahiert) >= 2
        if (gameState.getHero().getGold() >= 2){
            ownGoldBiggerTwo = 1;
        } else {
            ownGoldBiggerTwo = 0;
        }

        // Eigene Minenanzahl (abstrahiert)
        if (gameState.getHero().getMineCount() == 0){
            ownMineCount = 0;
        }
        if ((gameState.getHero().getMineCount()) > 0 && (gameState.getHero().getMineCount() <= 3)){
            ownMineCount = 1;
        }
        if ((gameState.getHero().getMineCount()) > 3 && (gameState.getHero().getMineCount() <= 7)){
            ownMineCount = 2;
        }
        if (gameState.getHero().getMineCount() >= 8){
            ownMineCount = 3;
        }

        // Eigenes Leben (abstrahiert)
        if(gameState.getHero().getLife() <= 10){
            ownLife = 0;
        }
        if((gameState.getHero().getLife() > 10) && (gameState.getHero().getLife() >= 20)){
            ownLife = 1;
        }
        if((gameState.getHero().getLife() > 20) && (gameState.getHero().getLife() >= 30)){
            ownLife = 2;
        }
        if((gameState.getHero().getLife() > 30) && (gameState.getHero().getLife() >= 40)){
            ownLife = 3;
        }
        if((gameState.getHero().getLife() > 40) && (gameState.getHero().getLife() >= 50)){
            ownLife = 4;
        }
        if((gameState.getHero().getLife() > 50) && (gameState.getHero().getLife() >= 60)){
            ownLife = 5;
        }
        if((gameState.getHero().getLife() > 60) && (gameState.getHero().getLife() >= 70)){
            ownLife = 6;
        }
        if((gameState.getHero().getLife() > 70) && (gameState.getHero().getLife() >= 80)){
            ownLife = 7;
        }
        if((gameState.getHero().getLife() > 80) && (gameState.getHero().getLife() >= 90)){
            ownLife = 8;
        }
        if((gameState.getHero().getLife() > 90) && (gameState.getHero().getLife() >= 100)){
            ownLife = 9;
        }

        // Nähester Gegner Leben (abstrahiert)
        if(gameState.getGame().getHeroes().get(closestPlayerId).getLife() <= 10){
            closestPlayerLife = 0;
        }
        if((gameState.getGame().getHeroes().get(closestPlayerId).getLife() > 10) && (gameState.getGame().getHeroes().get(closestPlayerId).getLife() >= 20)){
            closestPlayerLife = 1;
        }
        if((gameState.getGame().getHeroes().get(closestPlayerId).getLife() > 20) && (gameState.getGame().getHeroes().get(closestPlayerId).getLife() >= 30)){
            closestPlayerLife = 2;
        }
        if((gameState.getGame().getHeroes().get(closestPlayerId).getLife() > 30) && (gameState.getGame().getHeroes().get(closestPlayerId).getLife() >= 40)){
            closestPlayerLife = 3;
        }
        if((gameState.getGame().getHeroes().get(closestPlayerId).getLife() > 40) && (gameState.getGame().getHeroes().get(closestPlayerId).getLife() >= 50)){
            closestPlayerLife = 4;
        }
        if((gameState.getGame().getHeroes().get(closestPlayerId).getLife() > 50) && (gameState.getGame().getHeroes().get(closestPlayerId).getLife() >= 60)){
            closestPlayerLife = 5;
        }
        if((gameState.getGame().getHeroes().get(closestPlayerId).getLife() > 60) && (gameState.getGame().getHeroes().get(closestPlayerId).getLife() >= 70)){
            closestPlayerLife = 6;
        }
        if((gameState.getGame().getHeroes().get(closestPlayerId).getLife() > 70) && (gameState.getGame().getHeroes().get(closestPlayerId).getLife() >= 80)){
            closestPlayerLife = 7;
        }
        if((gameState.getGame().getHeroes().get(closestPlayerId).getLife() > 80) && (gameState.getGame().getHeroes().get(closestPlayerId).getLife() >= 90)){
            closestPlayerLife = 8;
        }
        if((gameState.getGame().getHeroes().get(closestPlayerId).getLife() > 90) && (gameState.getGame().getHeroes().get(closestPlayerId).getLife() >= 100)) {
            closestPlayerLife = 9;
        }

        // Nähester Gegner Minenanzahl (abstrahiert)
        if (gameState.getGame().getHeroes().get(closestPlayerId).getMineCount() == 0){
            closestPlayerMineCount = 0;
        }
        if ((gameState.getGame().getHeroes().get(closestPlayerId).getMineCount()) > 0 && (gameState.getGame().getHeroes().get(closestPlayerId).getMineCount() <= 3)){
            closestPlayerMineCount = 1;
        }
        if ((gameState.getGame().getHeroes().get(closestPlayerId).getMineCount()) > 3 && (gameState.getGame().getHeroes().get(closestPlayerId).getMineCount() <= 7)){
            closestPlayerMineCount = 2;
        }
        if (gameState.getGame().getHeroes().get(closestPlayerId).getMineCount() >= 8){
            closestPlayerMineCount = 3;
        }

        // Spieldauer (abstrahiert)
        if ((gameState.getGame().getTurn() >= 300) && (gameState.getGame().getTurn() <= 599)){
            timeRange = 1;
        }
        if ((gameState.getGame().getTurn() >= 600) && (gameState.getGame().getTurn() <= 899)){
            timeRange = 2;
        }
        if ((gameState.getGame().getTurn() >= 900) && (gameState.getGame().getTurn() <= 1199)){
            timeRange = 3;
        }

        // Eigene Platzierung
        sortGoldArray.clear();
        distinctSortGoldArray.clear();
        for(int i = 0; i < 4; i++) {
            sortGoldArray.add(gameState.getGame().getHeroes().get(i).getGold());
        }
        distinctSortGoldArray.addAll(sortGoldArray);
        sortGoldArray.clear();
        sortGoldArray.addAll(distinctSortGoldArray);
        Collections.sort(sortGoldArray);
        Collections.reverse(sortGoldArray);
        for(int i = 0; i < sortGoldArray.size(); i++) {
            if (sortGoldArray.get(i).equals(gameState.getHero().getGold())){
                ownInGameRanking = i + 1;
            }
        }

        // Notfallmodus
        Vertex move = getPath(closestMine).get(0);
        lastElement = getPath(closestPlayer).size() - 1;

        // Schenke
        if (modus == 0) {
            move = getPath(closestPub).get(0);
        }

        // Mine
        if (modus == 1) {
            move = getPath(closestMine).get(0);
        }

        // Kampf
        if (modus == 2) {
            move = getPath(closestPlayer).get(0);
        }

        // Stehen
        if (modus == 3) {
            move = getPath(ownPosition).get(0);
        }

        // Sieg
        if (gameState.getGame().getTurn() >= 1196 && ownInGameRanking == 1) {
            win = 1;
        }

        stateStr = "" + ownInGameRanking + "" + ownLife + "" + ownMineCount + "" + closestPlayerDistanceBiggerFour + "" + closestPlayerMineCount + "" + closestPlayerLife + "" + timeRange + "" + ownGoldBiggerTwo + "" + win + "";
        state = Long.parseLong(stateStr);

        logger.info("State: " + state);

        return BotUtils.directionTowards(gameState.getHero().getPos(), move.getPosition());
    }

    @Override
    public void setup() {
        // No-op
    }

    @Override
    public void shutdown() {
        // No-op
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
