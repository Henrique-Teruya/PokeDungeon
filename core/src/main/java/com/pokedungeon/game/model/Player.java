package com.pokedungeon.game.model;

import java.util.ArrayList;

import com.pokedungeon.game.utils.GameConstants;

/**
 * Representa o jogador no PokeDungeon.
 *
 * Estruturas de dados utilizadas:
 * - ArrayList<Pokemon> para o time de pokémons
 *
 * Princípios de POO:
 * - Encapsulamento: atributos privados com getters
 * - Modularização: inventário separado em outra classe (Fase 2)
 */
public class Player {

    private String name;
    private ArrayList<Pokemon> team; // time de pokémons do jogador
    private float x; // posição X no mapa
    private float y; // posição Y no mapa

    /**
     * Cria um novo jogador.
     *
     * @param name nome do jogador
     */
    public Player(String name) {
        this.name = name;
        this.team = new ArrayList<>();
        this.x = 0;
        this.y = 0;
    }

    /**
     * Adiciona um pokémon ao time.
     * Respeita o limite máximo definido em GameConstants.
     *
     * @param pokemon pokémon a ser adicionado
     * @return true se foi adicionado, false se o time está cheio
     */
    public boolean addPokemon(Pokemon pokemon) {
        if (team.size() >= GameConstants.MAX_TEAM_SIZE) {
            return false;
        }
        team.add(pokemon);
        return true;
    }

    /**
     * Retorna o primeiro pokémon vivo do time.
     *
     * @return pokémon ativo, ou null se todos estiverem derrotados
     */
    public Pokemon getActivePokemon() {
        for (Pokemon p : team) {
            if (!p.isFainted()) {
                return p;
            }
        }
        return null;
    }

    /**
     * Verifica se todos os pokémons do time estão derrotados.
     *
     * @return true se todos estão com HP <= 0
     */
    public boolean isDefeated() {
        return getActivePokemon() == null;
    }

    /**
     * Move o jogador para uma nova posição.
     *
     * @param dx deslocamento em X
     * @param dy deslocamento em Y
     */
    public void move(float dx, float dy) {
        this.x += dx;
        this.y += dy;
    }

    // --- Getters e Setters ---

    public String getName() {
        return name;
    }

    public ArrayList<Pokemon> getTeam() {
        return team;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return name + " - Time: " + team.size() + " pokémon(s)";
    }
}
