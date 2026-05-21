package com.pokedungeon.game.battle;

import java.util.Stack;

/**
 * Registra o histórico de ações da batalha usando uma Stack.
 *
 * Estruturas de dados utilizadas:
 * - Stack<String> para empilhar mensagens de log (LIFO)
 *
 * Permite consultar as últimas ações realizadas na batalha.
 */
public class BattleLog {

    private Stack<String> actionHistory;

    /**
     * Cria um novo log de batalha vazio.
     */
    public BattleLog() {
        this.actionHistory = new Stack<>();
    }

    /**
     * Registra uma nova ação no histórico.
     * A ação mais recente fica no topo da pilha.
     *
     * @param message descrição da ação
     */
    public void log(String message) {
        actionHistory.push(message);
    }

    /**
     * Retorna a última ação registrada sem removê-la.
     *
     * @return última mensagem, ou "Nenhuma ação" se vazio
     */
    public String getLastAction() {
        if (actionHistory.isEmpty()) {
            return "Nenhuma ação";
        }
        return actionHistory.peek();
    }

    /**
     * Remove e retorna a última ação registrada.
     *
     * @return última mensagem removida, ou null se vazio
     */
    public String popLastAction() {
        if (actionHistory.isEmpty()) {
            return null;
        }
        return actionHistory.pop();
    }

    /**
     * @return a pilha completa do histórico
     */
    public Stack<String> getHistory() {
        return actionHistory;
    }

    /**
     * @return quantidade de ações registradas
     */
    public int getSize() {
        return actionHistory.size();
    }

    /**
     * Limpa todo o histórico.
     */
    public void clear() {
        actionHistory.clear();
    }
}
