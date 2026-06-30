package com.pokedungeon.game.battle;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.pokedungeon.game.model.Pokemon;
import com.pokedungeon.game.model.TypeChart;

/**
 * Gerencia a lógica de batalha por turnos.
 *
 * Estruturas de dados utilizadas:
 * - Queue<TurnAction> para a fila de turnos (FIFO)
 * - Stack (via BattleLog) para o histórico de ações
 * - TypeChart (HashMap) para efetividade de tipos
 *
 * Princípios de POO:
 * - Modularização: lógica de batalha separada da renderização
 * - Composição: usa BattleLog para histórico
 */
public class BattleManager {

    private Pokemon playerPokemon;
    private Pokemon enemyPokemon;
    private Queue<TurnAction> turnQueue;
    private BattleLog battleLog;
    private boolean battleOver;
    private boolean enemyDefeated;
    private Random random;

    public BattleManager(Pokemon playerPokemon, Pokemon enemyPokemon) {
        this.playerPokemon = playerPokemon;
        this.enemyPokemon = enemyPokemon;
        this.turnQueue = new LinkedList<>();
        this.battleLog = new BattleLog();
        this.battleOver = false;
        this.enemyDefeated = false;
        this.random = new Random();

        battleLog.log("Batalha iniciada! " + playerPokemon.getName()
            + " vs " + enemyPokemon.getName());
    }

    public void enqueuePlayerAction(TurnAction action) {
        turnQueue.add(action);
    }

    /**
     * Inimigo escolhe ataque com inteligência baseada em tipos.
     * Prefere ataques super efetivos, evita pouco efetivos.
     */
    public void enqueueEnemyAction() {
        if (enemyPokemon.getAttacks().isEmpty()) {
            return;
        }

        String bestAttack = null;
        float bestMultiplier = 0f;

        for (String attackName : enemyPokemon.getAttacks().keySet()) {
            String attackType = enemyPokemon.getAttackType(attackName);
            float multiplier = TypeChart.getInstance().getEffectiveness(attackType, playerPokemon.getType());
            if (multiplier > bestMultiplier) {
                bestMultiplier = multiplier;
                bestAttack = attackName;
            }
        }

        if (bestAttack == null) {
            bestAttack = enemyPokemon.getAttacks().keySet().iterator().next();
        }

        TurnAction enemyAction = TurnAction.attack(enemyPokemon, playerPokemon, bestAttack);
        turnQueue.add(enemyAction);
    }

    public void processTurn() {
        while (!turnQueue.isEmpty() && !battleOver) {
            TurnAction action = turnQueue.poll();
            executeAction(action);
            checkBattleEnd();
        }
    }

    private void executeAction(TurnAction action) {
        if (action.getSource().isFainted()) {
            return;
        }

        switch (action.getType()) {
            case ATTACK:
                Pokemon target = action.getTarget();
                if (action.getSource() == enemyPokemon) {
                    target = playerPokemon;
                }

                int baseDamage = action.getSource().getAttackDamage(action.getAttackName());
                String attackType = action.getSource().getAttackType(action.getAttackName());
                float typeMultiplier = TypeChart.getInstance().getEffectiveness(attackType, target.getType());

                int finalDamage = (int) Math.round(baseDamage * typeMultiplier);
                finalDamage = Math.max(1, finalDamage);

                // Pequna variação aleatória (±10%)
                finalDamage = (int) (finalDamage * (0.9 + random.nextDouble() * 0.2));

                target.takeDamage(finalDamage);

                String effectivenessMsg = "";
                if (typeMultiplier > 1.0f) {
                    effectivenessMsg = " É super efetivo!";
                } else if (typeMultiplier > 0 && typeMultiplier < 1.0f) {
                    effectivenessMsg = " Não é muito efetivo...";
                } else if (typeMultiplier == 0) {
                    battleLog.log(action.getSource().getName() + " usou " + action.getAttackName() + "! Não teve efeito!");
                    return;
                }

                String attackMsg = action.getSource().getName() + " usou "
                    + action.getAttackName() + "! Causou " + finalDamage + " de dano." + effectivenessMsg
                    + " " + target.getName() + " HP: " + target.getHp() + "/" + target.getMaxHp();
                battleLog.log(attackMsg);
                break;

            case USE_ITEM:
                action.getItem().use(action.getTarget());
                String itemMsg = action.getSource().getName() + " usou "
                    + action.getItem().getName() + "! HP: "
                    + action.getTarget().getHp() + "/" + action.getTarget().getMaxHp();
                battleLog.log(itemMsg);
                break;

            case SWITCH:
                playerPokemon = action.getSwitchTarget();
                String switchMsg = playerPokemon.getName() + " entrou na batalha!";
                battleLog.log(switchMsg);
                break;
        }
    }

    private void checkBattleEnd() {
        if (enemyPokemon.isFainted()) {
            battleLog.log(enemyPokemon.getName() + " foi derrotado! Você venceu!");
            battleOver = true;
            enemyDefeated = true;
        }
    }

    public void setPlayerPokemon(Pokemon pkm) {
        this.playerPokemon = pkm;
    }

    public void forceDefeat() {
        battleOver = true;
    }

    // --- Getters ---

    public boolean isBattleOver() { return battleOver; }
    public boolean isPlayerWinner() { return battleOver && enemyDefeated; }
    public Pokemon getPlayerPokemon() { return playerPokemon; }
    public Pokemon getEnemyPokemon() { return enemyPokemon; }
    public BattleLog getBattleLog() { return battleLog; }
    public Queue<TurnAction> getTurnQueue() { return turnQueue; }
}
