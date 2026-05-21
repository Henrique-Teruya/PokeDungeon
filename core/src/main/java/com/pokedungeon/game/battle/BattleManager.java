package com.pokedungeon.game.battle;

import java.util.LinkedList;
import java.util.Queue;

import com.pokedungeon.game.model.Pokemon;

/**
 * Gerencia a lógica de batalha por turnos.
 *
 * Estruturas de dados utilizadas:
 * - Queue<TurnAction> para a fila de turnos (FIFO)
 * - Stack (via BattleLog) para o histórico de ações
 *
 * Princípios de POO:
 * - Modularização: lógica de batalha separada da renderização
 * - Composição: usa BattleLog para histórico
 */
public class BattleManager {

    private Pokemon playerPokemon;
    private Pokemon enemyPokemon;
    private Queue<TurnAction> turnQueue; // fila de ações do turno
    private BattleLog battleLog;
    private boolean battleOver;

    /**
     * Inicia uma nova batalha.
     *
     * @param playerPokemon pokémon do jogador
     * @param enemyPokemon  pokémon inimigo
     */
    public BattleManager(Pokemon playerPokemon, Pokemon enemyPokemon) {
        this.playerPokemon = playerPokemon;
        this.enemyPokemon = enemyPokemon;
        this.turnQueue = new LinkedList<>();
        this.battleLog = new BattleLog();
        this.battleOver = false;

        battleLog.log("Batalha iniciada! " + playerPokemon.getName()
            + " vs " + enemyPokemon.getName());
    }

    /**
     * Enfileira a ação do jogador na Queue.
     *
     * @param action ação escolhida pelo jogador
     */
    public void enqueuePlayerAction(TurnAction action) {
        turnQueue.add(action);
    }

    /**
     * Enfileira a ação automática do inimigo na Queue.
     * O inimigo escolhe o primeiro ataque disponível.
     */
    public void enqueueEnemyAction() {
        if (enemyPokemon.getAttacks().isEmpty()) {
            return;
        }
        // Inimigo usa o primeiro ataque disponível
        String attackName = enemyPokemon.getAttacks().keySet().iterator().next();
        TurnAction enemyAction = TurnAction.attack(enemyPokemon, playerPokemon, attackName);
        turnQueue.add(enemyAction);
    }

    /**
     * Processa todas as ações enfileiradas na Queue.
     * Cada ação é desenfileirada (FIFO) e executada.
     */
    public void processTurn() {
        while (!turnQueue.isEmpty() && !battleOver) {
            TurnAction action = turnQueue.poll(); // desenfileira (FIFO)
            executeAction(action);
            checkBattleEnd();
        }
    }

    /**
     * Executa uma ação individual.
     *
     * @param action ação a ser executada
     */
    private void executeAction(TurnAction action) {
        if (action.getSource().isFainted()) {
            return; // pokémon derrotado não age
        }

        switch (action.getType()) {
            case ATTACK:
                int damage = action.getSource().getAttackDamage(action.getAttackName());
                action.getTarget().takeDamage(damage);

                String attackMsg = action.getSource().getName() + " usou "
                    + action.getAttackName() + "! Causou " + damage + " de dano. "
                    + action.getTarget().getName() + " ficou com "
                    + action.getTarget().getHp() + " HP.";
                battleLog.log(attackMsg);
                break;

            case USE_ITEM:
                action.getItem().use(action.getTarget());

                String itemMsg = action.getSource().getName() + " usou "
                    + action.getItem().getName() + "! HP: "
                    + action.getTarget().getHp() + "/" + action.getTarget().getMaxHp();
                battleLog.log(itemMsg);
                break;
        }
    }

    /**
     * Verifica se a batalha acabou.
     */
    private void checkBattleEnd() {
        if (playerPokemon.isFainted()) {
            battleLog.log(playerPokemon.getName() + " foi derrotado! Você perdeu.");
            battleOver = true;
        } else if (enemyPokemon.isFainted()) {
            battleLog.log(enemyPokemon.getName() + " foi derrotado! Você venceu!");
            battleOver = true;
        }
    }

    // --- Getters ---

    public boolean isBattleOver() {
        return battleOver;
    }

    /**
     * @return true se o jogador venceu
     */
    public boolean isPlayerWinner() {
        return battleOver && enemyPokemon.isFainted();
    }

    public Pokemon getPlayerPokemon() {
        return playerPokemon;
    }

    public Pokemon getEnemyPokemon() {
        return enemyPokemon;
    }

    public BattleLog getBattleLog() {
        return battleLog;
    }

    public Queue<TurnAction> getTurnQueue() {
        return turnQueue;
    }
}
