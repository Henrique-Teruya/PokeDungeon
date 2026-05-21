package com.pokedungeon.game.dungeon;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Gerencia a exploração da dungeon pelo jogador.
 *
 * Estruturas de dados utilizadas:
 * - Stack<Room> para histórico de navegação (voltar para sala anterior)
 * - ArrayList<Room> para lista de salas visitadas
 * - Grafo (DungeonGraph) para o mapa da dungeon
 *
 * Princípios de POO:
 * - Modularização: separa lógica de navegação do grafo
 * - Encapsulamento: estado de navegação é privado
 */
public class DungeonManager {

    private DungeonGraph graph;
    private Room currentRoom;
    private Stack<Room> roomHistory;       // pilha para voltar (backtrack)
    private ArrayList<Room> visitedRooms;  // lista de todas as salas visitadas

    /**
     * Cria o gerenciador de dungeon.
     *
     * @param graph     grafo da dungeon
     * @param startRoom sala inicial
     */
    public DungeonManager(DungeonGraph graph, Room startRoom) {
        this.graph = graph;
        this.currentRoom = startRoom;
        this.roomHistory = new Stack<>();
        this.visitedRooms = new ArrayList<>();

        // Marca a sala inicial como visitada
        startRoom.setVisited(true);
        visitedRooms.add(startRoom);
    }

    /**
     * Move o jogador para uma sala vizinha.
     * A sala atual é empilhada no histórico (Stack).
     * A nova sala é adicionada à lista de visitadas (ArrayList).
     *
     * @param nextRoom sala de destino
     * @return true se a movimentação foi possível
     */
    public boolean moveTo(Room nextRoom) {
        // Verifica se a sala destino é vizinha da atual
        if (!graph.areConnected(currentRoom, nextRoom)) {
            return false;
        }

        // Empilha a sala atual no histórico
        roomHistory.push(currentRoom);

        // Move para a próxima sala
        currentRoom = nextRoom;

        // Marca como visitada se ainda não foi
        if (!nextRoom.isVisited()) {
            nextRoom.setVisited(true);
            visitedRooms.add(nextRoom);
        }

        return true;
    }

    /**
     * Volta para a sala anterior usando a Stack.
     *
     * @return true se conseguiu voltar, false se não há histórico
     */
    public boolean goBack() {
        if (roomHistory.isEmpty()) {
            return false;
        }
        currentRoom = roomHistory.pop();
        return true;
    }

    /**
     * Retorna as salas vizinhas da sala atual.
     *
     * @return lista de salas acessíveis
     */
    public ArrayList<Room> getAvailableRooms() {
        return graph.getNeighbors(currentRoom);
    }

    // --- Getters ---

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public ArrayList<Room> getVisitedRooms() {
        return visitedRooms;
    }

    public Stack<Room> getRoomHistory() {
        return roomHistory;
    }

    public DungeonGraph getGraph() {
        return graph;
    }

    /**
     * @return true se há histórico para voltar
     */
    public boolean canGoBack() {
        return !roomHistory.isEmpty();
    }
}
