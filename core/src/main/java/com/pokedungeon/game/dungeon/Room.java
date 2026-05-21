package com.pokedungeon.game.dungeon;

import com.pokedungeon.game.model.Item;
import com.pokedungeon.game.model.Pokemon;

/**
 * Representa uma sala na dungeon.
 *
 * Cada sala possui um ID único, nome, e pode conter
 * um pokémon inimigo e/ou um item para o jogador coletar.
 *
 * Princípios de POO:
 * - Encapsulamento: atributos privados com getters/setters
 * - Reutilização: usada tanto no Grafo quanto no gerenciador
 */
public class Room {

    private int id;
    private String name;
    private String description;
    private Pokemon enemy;   // pokémon inimigo (pode ser null)
    private Item item;       // item na sala (pode ser null)
    private boolean visited;

    /**
     * Cria uma nova sala.
     *
     * @param id          identificador único
     * @param name        nome da sala
     * @param description descrição da sala
     */
    public Room(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.enemy = null;
        this.item = null;
        this.visited = false;
    }

    /**
     * @return true se a sala tem um inimigo vivo
     */
    public boolean hasEnemy() {
        return enemy != null && !enemy.isFainted();
    }

    /**
     * @return true se a sala tem um item para coletar
     */
    public boolean hasItem() {
        return item != null;
    }

    /**
     * Coleta o item da sala e retorna.
     * O item é removido da sala após a coleta.
     *
     * @return item coletado, ou null se não houver
     */
    public Item collectItem() {
        Item collected = this.item;
        this.item = null;
        return collected;
    }

    // --- Getters e Setters ---

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Pokemon getEnemy() {
        return enemy;
    }

    public void setEnemy(Pokemon enemy) {
        this.enemy = enemy;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    @Override
    public String toString() {
        return "[Sala " + id + "] " + name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Room room = (Room) obj;
        return id == room.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
