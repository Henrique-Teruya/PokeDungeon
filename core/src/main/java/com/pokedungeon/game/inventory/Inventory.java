package com.pokedungeon.game.inventory;

import java.util.ArrayList;

import com.pokedungeon.game.model.Item;
import com.pokedungeon.game.model.Pokemon;
import com.pokedungeon.game.utils.GameConstants;

/**
 * Gerencia o inventário de itens do jogador.
 *
 * Estruturas de dados utilizadas:
 * - ArrayList<Item> para armazenar os itens
 *
 * Princípios de POO:
 * - Encapsulamento: lista privada com métodos de acesso
 * - Modularização: inventário separado do Player
 * - Reutilização: pode ser usado em diferentes contextos
 */
public class Inventory {

    private ArrayList<Item> items;

    /**
     * Cria um inventário vazio.
     */
    public Inventory() {
        this.items = new ArrayList<>();
    }

    /**
     * Adiciona um item ao inventário.
     * Respeita o limite máximo de itens.
     *
     * @param item item a ser adicionado
     * @return true se foi adicionado, false se o inventário está cheio
     */
    public boolean addItem(Item item) {
        if (items.size() >= GameConstants.MAX_INVENTORY_SIZE) {
            return false;
        }
        items.add(item);
        return true;
    }

    /**
     * Remove um item do inventário pelo índice.
     *
     * @param index índice do item
     * @return item removido, ou null se índice inválido
     */
    public Item removeItem(int index) {
        if (index < 0 || index >= items.size()) {
            return null;
        }
        return items.remove(index);
    }

    /**
     * Usa um item do inventário em um pokémon.
     * O item é removido após o uso.
     *
     * @param index  índice do item no inventário
     * @param target pokémon alvo
     * @return true se o item foi usado, false se índice inválido
     */
    public boolean useItem(int index, Pokemon target) {
        Item item = removeItem(index);
        if (item == null) {
            return false;
        }
        item.use(target);
        return true;
    }

    /**
     * Retorna o item no índice sem removê-lo.
     *
     * @param index índice do item
     * @return item na posição, ou null se inválido
     */
    public Item getItem(int index) {
        if (index < 0 || index >= items.size()) {
            return null;
        }
        return items.get(index);
    }

    /**
     * @return quantidade de itens no inventário
     */
    public int getSize() {
        return items.size();
    }

    /**
     * @return true se o inventário está vazio
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * @return lista de itens (ArrayList)
     */
    public ArrayList<Item> getItems() {
        return items;
    }
}
