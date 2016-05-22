package com.brianstempin.vindiniumclient.bot.advanced;

/**
 * Created by Eric on 21.05.2016.
 */

import com.brianstempin.vindiniumclient.dto.GameState;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Represents the result of a Dijkstra search for a given position
 */
public class DijkstraResult {
    private int distance;
    private GameState.Position previous;

    public DijkstraResult(int distance, GameState.Position previous) {
        this.distance = distance;
        this.previous = previous;
    }

    public int getDistance() {
        return distance;
    }

    public GameState.Position getPrevious() {
        return previous;
    }


    public static synchronized Map<GameState.Position, DijkstraResult> dijkstraSearch(AdvancedGameState gameState) {
        Map<GameState.Position, DijkstraResult> result = new HashMap<>();

        DijkstraResult startingResult = new DijkstraResult(0, null);
        Queue<GameState.Position> queue = new ArrayBlockingQueue<>(gameState.getBoardGraph().size());
        queue.add(gameState.getMe().getPos());
        result.put(gameState.getMe().getPos(), startingResult);

        while (!queue.isEmpty()) {
            GameState.Position currentPosition = queue.poll();
            DijkstraResult currentResult = result.get(currentPosition);
            Vertex currentVertex = gameState.getBoardGraph().get(currentPosition);

            // If there's a bot here, then this vertex goes nowhere
            if (gameState.getHeroesByPosition().containsKey(currentPosition)
                    && !currentPosition.equals(gameState.getMe().getPos()))
                continue;

            int distance = currentResult.getDistance() + 1;

            for (Vertex neighbor : currentVertex.getAdjacentVertices()) {
                DijkstraResult neighborResult = result.get(neighbor.getPosition());
                if (neighborResult == null) {
                    neighborResult = new DijkstraResult(distance, currentPosition);
                    result.put(neighbor.getPosition(), neighborResult);
                    queue.remove(neighbor.getPosition());
                    queue.add(neighbor.getPosition());
                } else if (neighborResult.distance > distance) {
                    DijkstraResult newNeighborResult = new DijkstraResult(distance, currentPosition);
                    result.put(neighbor.getPosition(), newNeighborResult);
                    queue.remove(neighbor.getPosition());
                    queue.add(neighbor.getPosition());
                }
            }
        }

        return result;
    }
}