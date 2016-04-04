package com.brianstempin.vindiniumclient.bot.simple;

import com.brianstempin.vindiniumclient.bot.BotMove;
import com.brianstempin.vindiniumclient.bot.BotUtils;
import com.brianstempin.vindiniumclient.dto.GameState;

import java.util.*;
import java.util.logging.Logger;

public class TestBot implements SimpleBot {

    private Logger logger;

    public TestBot() {
        logger = Logger.getLogger("testbot");
    }

    public boolean mineMode = false;
    public boolean tavernMode = false;
    public boolean fightMode = false;
    public boolean hero1IsTeamplayBot = false;
    public boolean hero2IsTeamplayBot = false;
    public boolean hero3IsTeamplayBot = false;
    public boolean hero4IsTeamplayBot = false;
    public int closestPlayerId = 0;
    public int lastElement = 0;
    public int mapSize = 0;
    public int tavernCount = 0;
    public int globalMineCount = 0;


    private List<Vertex> doDijkstra(GameState.Board board, GameState.Hero hero) {
        List<Vertex> vertexes = new LinkedList<Vertex>();
        Vertex me = null;

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
                me = v;
            }

            // Überprüfe: Felder worauf etwas steht werden nicht einbezogen.
            if (v.getTileType().equals("##") || v.getTileType().equals("[]") || v.getTileType().startsWith("$")) {

                //Mapscan: TavernenAnzahl
                if (v.getTileType().equals("[]")){
                    tavernCount++;
                }

                //Mapscan: MinenAnzahl
                if (v.getTileType().startsWith("$")){
                    globalMineCount++;
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
        vertexQueue.add(me);
        me.setMinDistance(0);

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
                    closestPlayer = v;
                }
            }
        }

        Vertex move = getPath(closestMine).get(0);
        lastElement = getPath(closestPlayer).size() - 1;

        // Zur nächsten Schenke!
        if ((gameState.getHero().getGold() >= 2) && (gameState.getHero().getLife() <= 37)) {
            move = getPath(closestPub).get(0);
            tavernMode = true;
            fightMode = false;
            mineMode = false;
            logger.info("Turn: " + gameState.getGame().getTurn() + "; HeroID: " + gameState.getHero().getId() +  "; Life: " + gameState.getHero().getLife() + "; Gold: " + gameState.getHero().getGold() + "; MineCount: " + gameState.getHero().getMineCount() + "; -> Ich muss mich heilen, bin auf dem Weg zur nächsten Schenke!");
        } else {
            tavernMode = false;
        }

        // Zur nächsten Mine!
        if ((tavernMode == false) && (fightMode == false) && (gameState.getHero().getLife() >= 38)) {
            move = getPath(closestMine).get(0);
            mineMode = true;
            logger.info("Turn: " + gameState.getGame().getTurn() + "; HeroID: " + gameState.getHero().getId() +  "; Life: " + gameState.getHero().getLife() + "; Gold: " + gameState.getHero().getGold() + "; MineCount: " + gameState.getHero().getMineCount() + "; -> Ich gehe zur nächsten Mine!");
        } else {
            mineMode = false;
        }

        // Auf in dem Kampf!
        if ((tavernMode == false)  && (gameState.getGame().getHeroes().get(closestPlayerId).getLife() < gameState.getHero().getLife()) && (getPath(closestPlayer).get(lastElement).getMinDistance() <= 3) && (gameState.getHero().getMineCount() <= 2) && (gameState.getHero().getLife() >= 38)){
            move = getPath(closestPlayer).get(0);
            fightMode = true;
            mineMode = false;
            logger.info("Turn: " + gameState.getGame().getTurn() + "; HeroID: " + gameState.getHero().getId() +  "; Life: " + gameState.getHero().getLife() + "; Gold: " + gameState.getHero().getGold() + "; MineCount: " + gameState.getHero().getMineCount() + "; -> Ich bring " + gameState.getGame().getHeroes().get(closestPlayerId).getName() + " um!");
        } else {
            fightMode = false;
        }

        //Spiel wurde beendet!
        if (gameState.getGame().getTurn() >= 1196) {
            mapSize = gameState.getGame().getBoard().getSize() * gameState.getGame().getBoard().getSize();

            //Teilen durch die Runden, da zur Zeit bei jedem Durchlauf gescannt wird.
            globalMineCount = globalMineCount / 300;
            tavernCount = tavernCount / 300;

            logger.info("Spiel wurde beendet! Mapinformations: MapSize: " + mapSize + "; GlobalMineCount: " + globalMineCount + "; TavernCount: " + tavernCount + ";");
        }

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
