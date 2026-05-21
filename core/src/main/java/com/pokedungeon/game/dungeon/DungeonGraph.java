package com.pokedungeon.game.dungeon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Representa o mapa da dungeon como um GRAFO.
 *
 * Estruturas de dados utilizadas:
 * - HashMap<Room, ArrayList<Room>> para lista de adjacência (GRAFO)
 *
 * O grafo é NÃO-DIRECIONADO: se sala A conecta à B, então B conecta à A.
 *
 * Princípios de POO:
 * - Encapsulamento: estrutura interna do grafo é privada
 * - Modularização: grafo separado da lógica de navegação
 */
public class DungeonGraph {

    // Lista de adjacência: cada sala mapeia para suas salas vizinhas
    private HashMap<Room, ArrayList<Room>> adjacencyMap;

    /**
     * Cria um grafo de dungeon vazio.
     */
    public DungeonGraph() {
        this.adjacencyMap = new HashMap<>();
    }

    /**
     * Adiciona uma sala ao grafo (sem conexões).
     *
     * @param room sala a ser adicionada
     */
    public void addRoom(Room room) {
        adjacencyMap.putIfAbsent(room, new ArrayList<>());
    }

    /**
     * Conecta duas salas (grafo não-direcionado).
     * Ambas as salas são adicionadas automaticamente se não existirem.
     *
     * @param roomA primeira sala
     * @param roomB segunda sala
     */
    public void connect(Room roomA, Room roomB) {
        addRoom(roomA);
        addRoom(roomB);

        // Evita conexões duplicadas
        if (!adjacencyMap.get(roomA).contains(roomB)) {
            adjacencyMap.get(roomA).add(roomB);
        }
        if (!adjacencyMap.get(roomB).contains(roomA)) {
            adjacencyMap.get(roomB).add(roomA);
        }
    }

    /**
     * Retorna as salas vizinhas de uma sala.
     *
     * @param room sala de referência
     * @return lista de salas conectadas, ou lista vazia se não existir
     */
    public ArrayList<Room> getNeighbors(Room room) {
        return adjacencyMap.getOrDefault(room, new ArrayList<>());
    }

    /**
     * Verifica se duas salas estão conectadas.
     *
     * @param roomA primeira sala
     * @param roomB segunda sala
     * @return true se as salas são vizinhas
     */
    public boolean areConnected(Room roomA, Room roomB) {
        List<Room> neighbors = adjacencyMap.get(roomA);
        return neighbors != null && neighbors.contains(roomB);
    }

    /**
     * @return todas as salas do grafo
     */
    public ArrayList<Room> getAllRooms() {
        return new ArrayList<>(adjacencyMap.keySet());
    }

    /**
     * @return quantidade de salas no grafo
     */
    public int getRoomCount() {
        return adjacencyMap.size();
    }
}
