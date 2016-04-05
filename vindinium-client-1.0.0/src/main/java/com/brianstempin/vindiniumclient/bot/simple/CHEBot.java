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

    // Eigene Position
    Vertex ownPosition = null;

    // Eigenes Gold (abstrahiert)
    public boolean ownGoldBiggerTwo = false;

    // Eigene Platzierung
    public int ownInGameRanking = 0;

    // SpielModus (0 = Schenke; 1 = Mine; 2 = Kampf; 3 = Stehen;)
    public int modus = 0;

    // Letzter Spielmodus (0 = Schenke; 1 = Mine; 2 = Kampf; 3 = Stehen;)
    public int lastModus = 0;


    // Zeitbereich (0 = Turn 0-299; 1 = Turn 300-599; 2 = Turn 600-899; 3 = Turn 900-1199;)
    public int timeRange = 0;

    // Map-Informationen
    public int mapSize = 0;
    public int tavernCount = 0;
    public int tavernCountHelper = 0;
    public int globalMineCount = 0;
    public int globalMineCountHelper = 0;

    // TeamPlay
    public boolean hero1IsTeamplayBot = false;
    public boolean hero2IsTeamplayBot = false;
    public boolean hero3IsTeamplayBot = false;
    public boolean hero4IsTeamplayBot = false;

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
                    closestPlayer = v;
                }
            }
        }

        // Eigenes Gold >= 2
        if (gameState.getHero().getGold() >= 2){
            ownGoldBiggerTwo = true;
        } else {
            ownGoldBiggerTwo = false;
        }

        // Spieldauer in vier Bereiche
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



        // HeroID: gameState.getHero().getId()
        // Turn: gameState.getGame().getTurn()
        // Life: gameState.getHero().getLife()
        // Gold: ownGoldBiggerTwo
        // Eigene Platzierung: ownInGameRanking
        // MineCount: gameState.getHero().getMineCount()
        // closestPlayerMineCount: gameState.getGame().getHeroes().get(closestPlayerId).getMineCount()
        // closestPlayerLife: gameState.getGame().getHeroes().get(closestPlayerId).getLife()
        // closestPlayerDistance: getPath(closestPlayer).get(lastElement).getMinDistance()
        // MapSize: mapSize
        // GlobalMineCount: globalMineCount
        // TavernCount: tavernCount
        // Spieldauer: timeRange
        // letzter Modus: lastModus <-- wichtig!: vor der nächste Aktion abfragen

        // Notfallmodus
        Vertex move = getPath(closestMine).get(0);

        // Schenke
        if (modus == 0) {
            move = getPath(closestPub).get(0);
            lastModus = 0;
        }

        // Mine
        if (modus == 1) {
            move = getPath(closestMine).get(0);
            lastModus = 1;
        }

        // Kampf
        if (modus == 2) {
            move = getPath(closestPlayer).get(0);
            lastModus = 2;
        }

        // Stehen
        if (modus == 3) {
            move = getPath(ownPosition).get(0);
            lastModus = 3;
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
