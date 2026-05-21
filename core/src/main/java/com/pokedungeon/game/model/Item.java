package com.pokedungeon.game.model;

/**
 * Representa um item no jogo.
 *
 * Itens podem curar HP dos pokémons.
 * Mantido simples propositalmente — foco acadêmico.
 *
 * Princípios de POO:
 * - Encapsulamento: atributos privados com getters
 */
public class Item {

    private String name;
    private int healAmount;

    /**
     * Cria um novo item.
     *
     * @param name       nome do item
     * @param healAmount quantidade de HP que o item cura
     */
    public Item(String name, int healAmount) {
        this.name = name;
        this.healAmount = healAmount;
    }

    /**
     * Usa o item em um pokémon, curando seu HP.
     *
     * @param target pokémon que receberá a cura
     */
    public void use(Pokemon target) {
        target.heal(healAmount);
    }

    // --- Getters ---

    public String getName() {
        return name;
    }

    public int getHealAmount() {
        return healAmount;
    }

    @Override
    public String toString() {
        return name + " (Cura: " + healAmount + " HP)";
    }
}
