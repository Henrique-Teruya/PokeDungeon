package com.pokedungeon.game.model;

/**
 * Representa um item no jogo.
 */
public class Item {

    public enum ItemType {
        POTION,
        POKEBALL,
        REVIVE
    }

    private String name;
    private ItemType itemType;
    private int healAmount;

    public Item(String name, int healAmount) {
        this(name, ItemType.POTION, healAmount);
    }

    public Item(String name, ItemType itemType, int healAmount) {
        this.name = name;
        this.itemType = itemType;
        this.healAmount = healAmount;
    }

    public void use(Pokemon target) {
        target.heal(healAmount);
    }

    // --- Getters ---
    public String getName() { return name; }
    public ItemType getItemType() { return itemType; }
    public int getHealAmount() { return healAmount; }

    @Override
    public String toString() {
        return name + " (" + itemType + ")";
    }
}
