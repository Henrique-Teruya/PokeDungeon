package com.pokedungeon.game.model;

import java.util.HashMap;

/**
 * Representa um Pokémon no jogo.
 *
 * Estruturas de dados utilizadas:
 * - HashMap<String, Integer> para mapear nome do ataque -> dano
 *
 * Princípios de POO:
 * - Encapsulamento: atributos privados com getters
 * - Reutilização: mesma classe para pokémons do jogador e inimigos
 */
public class Pokemon {

    private String name;
    private String type;
    private int hp;
    private int maxHp;
    private HashMap<String, Integer> attacks; // nome do ataque -> dano

    /**
     * Cria um novo Pokémon.
     *
     * @param name  nome do pokémon
     * @param type  tipo (Fogo, Água, Planta, Normal)
     * @param maxHp HP máximo
     */
    public Pokemon(String name, String type, int maxHp) {
        this.name = name;
        this.type = type;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attacks = new HashMap<>();
    }

    /**
     * Adiciona um ataque ao pokémon.
     * HashMap: nome do ataque -> valor do dano.
     *
     * @param attackName nome do ataque
     * @param damage     dano causado
     */
    public void addAttack(String attackName, int damage) {
        attacks.put(attackName, damage);
    }

    /**
     * Retorna o dano de um ataque específico.
     *
     * @param attackName nome do ataque
     * @return dano do ataque, ou 0 se não existir
     */
    public int getAttackDamage(String attackName) {
        return attacks.getOrDefault(attackName, 0);
    }

    /**
     * Aplica dano ao pokémon. HP não fica abaixo de 0.
     *
     * @param damage quantidade de dano
     */
    public void takeDamage(int damage) {
        this.hp = Math.max(0, this.hp - damage);
    }

    /**
     * Cura o pokémon. HP não ultrapassa o máximo.
     *
     * @param amount quantidade de cura
     */
    public void heal(int amount) {
        this.hp = Math.min(maxHp, this.hp + amount);
    }

    /**
     * Verifica se o pokémon está derrotado.
     *
     * @return true se HP <= 0
     */
    public boolean isFainted() {
        return this.hp <= 0;
    }

    // --- Getters ---

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public HashMap<String, Integer> getAttacks() {
        return attacks;
    }

    @Override
    public String toString() {
        return name + " [" + type + "] HP: " + hp + "/" + maxHp;
    }
}
