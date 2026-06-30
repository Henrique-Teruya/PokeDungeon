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
public enum RoomEvent { NONE, MYSTERY_CHEST, HEALING_SPRING, RARE_POKEMON }

public class Room {

    private int id;
    private String name;
    private String description;
    private Pokemon enemy;
    private Item item;
    private boolean visited;
    private RoomEvent event;
    private int mapX;
    private int mapY;

    public Room(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.enemy = null;
        this.item = null;
        this.visited = false;
        this.event = RoomEvent.NONE;
    }

    public boolean hasEnemy() {
        return enemy != null && !enemy.isFainted();
    }

    public boolean hasItem() {
        return item != null;
    }

    public Item collectItem() {
        Item collected = this.item;
        this.item = null;
        return collected;
    }

    public void triggerEvent() {
        event = RoomEvent.NONE;
    }

    public int getId() { return id; }
    public void setName(String name) { this.name = name; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Pokemon getEnemy() { return enemy; }
    public void setEnemy(Pokemon enemy) { this.enemy = enemy; }
    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }
    public boolean isVisited() { return visited; }
    public void setVisited(boolean visited) { this.visited = visited; }
    public int getMapX() { return mapX; }
    public void setMapX(int mapX) { this.mapX = mapX; }
    public int getMapY() { return mapY; }
    public void setMapY(int mapY) { this.mapY = mapY; }
    public RoomEvent getEvent() { return event; }
    public void setEvent(RoomEvent event) { this.event = event; }

    @Override public String toString() { return "[Sala " + id + "] " + name; }
    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return id == ((Room) obj).id;
    }
    @Override public int hashCode() { return Integer.hashCode(id); }
}
