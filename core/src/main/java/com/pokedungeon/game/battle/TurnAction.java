package com.pokedungeon.game.battle;

import com.pokedungeon.game.model.Item;
import com.pokedungeon.game.model.Pokemon;

/**
 * Representa uma ação em um turno de batalha.
 *
 * Princípios de POO:
 * - Polimorfismo: mesma classe representa ações diferentes (ATTACK vs USE_ITEM)
 * - Encapsulamento: dados da ação são imutáveis após criação
 */
public class TurnAction {

    /**
     * Tipos de ação possíveis em um turno.
     */
    public enum ActionType {
        ATTACK,
        USE_ITEM
    }

    private ActionType type;
    private Pokemon source;      // quem executa a ação
    private Pokemon target;      // quem recebe a ação
    private String attackName;   // nome do ataque (se ATTACK)
    private Item item;           // item usado (se USE_ITEM)

    /**
     * Cria uma ação de ataque.
     *
     * @param source     pokémon atacante
     * @param target     pokémon alvo
     * @param attackName nome do ataque
     * @return ação de ataque configurada
     */
    public static TurnAction attack(Pokemon source, Pokemon target, String attackName) {
        TurnAction action = new TurnAction();
        action.type = ActionType.ATTACK;
        action.source = source;
        action.target = target;
        action.attackName = attackName;
        return action;
    }

    /**
     * Cria uma ação de usar item.
     *
     * @param source pokémon que usará o item (quem recebe a cura)
     * @param item   item a ser usado
     * @return ação de uso de item configurada
     */
    public static TurnAction useItem(Pokemon source, Item item) {
        TurnAction action = new TurnAction();
        action.type = ActionType.USE_ITEM;
        action.source = source;
        action.target = source; // item é usado no próprio pokémon
        action.item = item;
        return action;
    }

    // --- Getters ---

    public ActionType getType() {
        return type;
    }

    public Pokemon getSource() {
        return source;
    }

    public Pokemon getTarget() {
        return target;
    }

    public String getAttackName() {
        return attackName;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public String toString() {
        if (type == ActionType.ATTACK) {
            return source.getName() + " usou " + attackName + " em " + target.getName();
        } else {
            return source.getName() + " usou " + item.getName();
        }
    }
}
