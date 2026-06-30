package com.pokedungeon.game.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Representa um Pokémon no jogo.
 *
 * Estruturas de dados utilizadas:
 * - HashMap<String, Attack> para mapear nome do ataque -> dados do ataque
 *
 * Princípios de POO:
 * - Encapsulamento: atributos privados com getters
 * - Reutilização: mesma classe para pokémons do jogador e inimigos
 */
public class Pokemon {

    private String name;
    private String type;
    private int level;
    private int exp;
    private int expToNext;
    private int hp;
    private int maxHp;
    private HashMap<String, Attack> attacks;

    public Pokemon(String name, String type, int maxHp) {
        this(name, type, 5, maxHp);
    }

    public Pokemon(String name, String type, int level, int maxHp) {
        this.name = name;
        this.type = type;
        this.level = level;
        this.exp = 0;
        this.expToNext = level * 10; // Simple EXP curve
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attacks = new HashMap<>();
    }

    public void addAttack(String attackName, int damage) {
        addAttack(attackName, damage, type);
    }

    public void addAttack(String attackName, int damage, String attackType) {
        attacks.put(attackName, new Attack(attackName, damage, attackType));
    }

    public int getAttackDamage(String attackName) {
        Attack a = attacks.get(attackName);
        return a != null ? a.getDamage() : 0;
    }

    public String getAttackType(String attackName) {
        Attack a = attacks.get(attackName);
        return a != null ? a.getType() : "Normal";
    }

    public void takeDamage(int damage) {
        this.hp = Math.max(0, this.hp - damage);
    }

    public void heal(int amount) {
        this.hp = Math.min(maxHp, this.hp + amount);
    }

    public void fullHeal() {
        this.hp = this.maxHp;
    }

    public boolean isFainted() {
        return this.hp <= 0;
    }

    /**
     * Gains EXP and levels up if enough EXP is accumulated.
     * Returns true if the Pokemon leveled up.
     */
    public boolean gainExp(int amount) {
        this.exp += amount;
        while (this.exp >= this.expToNext) {
            this.exp -= this.expToNext;
            levelUp();
            return true;
        }
        return false;
    }

    private void levelUp() {
        this.level++;
        this.expToNext = this.level * 10;
        // Scale HP and attacks on level up
        this.maxHp += 5;
        this.hp = this.maxHp; // Full heal on level up
        for (String attackName : new ArrayList<>(attacks.keySet())) {
            Attack a = attacks.get(attackName);
            attacks.put(attackName, new Attack(a.getName(), a.getDamage() + 2, a.getType()));
        }
    }

    public int getExp() { return exp; }
    public int getExpToNext() { return expToNext; }

    // --- Getters ---

    public String getName() { return name; }
    public String getType() { return type; }
    public int getLevel() { return level; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public HashMap<String, Attack> getAttacks() { return attacks; }

    @Override
    public String toString() {
        return name + " [" + type + "] Nv." + level + " HP: " + hp + "/" + maxHp;
    }

    /**
     * Classe interna que representa um ataque com tipo.
     */
    public static class Attack {
        private String name;
        private int damage;
        private String type;

        public Attack(String name, int damage, String type) {
            this.name = name;
            this.damage = damage;
            this.type = type;
        }

        public String getName() { return name; }
        public int getDamage() { return damage; }
        public String getType() { return type; }
    }
}
