package com.pokedungeon.game.model;

import java.util.HashMap;

/**
 * Sistema de tipagem com efetividade entre tipos.
 *
 * Estruturas de dados utilizadas:
 * - HashMap<String, HashMap<String, Float>> para a tabela de efetividade
 *
 * A tabela mapeia tipoAtacante -> (tipoDefensor -> multiplicador)
 * 2.0 = super efetivo, 0.5 = pouco efetivo, 1.0 = normal
 */
public final class TypeChart {

    private HashMap<String, HashMap<String, Float>> chart;

    private TypeChart() {
        chart = new HashMap<>();
        buildChart();
    }

    private void buildChart() {
        put("Fogo", "Planta", 2.0f);
        put("Fogo", "Fogo", 0.5f);
        put("Fogo", "Água", 0.5f);
        put("Fogo", "Pedra", 2.0f);

        put("Água", "Fogo", 2.0f);
        put("Água", "Água", 0.5f);
        put("Água", "Planta", 0.5f);
        put("Água", "Terra", 2.0f);

        put("Planta", "Água", 2.0f);
        put("Planta", "Planta", 0.5f);
        put("Planta", "Fogo", 0.5f);
        put("Planta", "Terra", 2.0f);

        put("Elétrico", "Água", 2.0f);
        put("Elétrico", "Voador", 2.0f);
        put("Elétrico", "Elétrico", 0.5f);
        put("Elétrico", "Planta", 0.5f);
        put("Elétrico", "Terra", 0.0f);

        put("Pedra", "Fogo", 2.0f);
        put("Pedra", "Voador", 2.0f);
        put("Pedra", "Pedra", 0.5f);
        put("Pedra", "Planta", 0.5f);

        put("Terra", "Fogo", 2.0f);
        put("Terra", "Elétrico", 2.0f);
        put("Terra", "Pedra", 2.0f);
        put("Terra", "Planta", 0.5f);
        put("Terra", "Voador", 0.0f);

        put("Voador", "Planta", 2.0f);
        put("Voador", "Pedra", 0.5f);
        put("Voador", "Elétrico", 0.5f);
        put("Voador", "Terra", 0.0f);

        put("Normal", "Pedra", 0.5f);
        put("Normal", "Voador", 1.0f);

        put("Gelo", "Voador", 2.0f);
        put("Gelo", "Terra", 2.0f);
        put("Gelo", "Planta", 2.0f);
        put("Gelo", "Fogo", 0.5f);
        put("Gelo", "Água", 0.5f);
        put("Gelo", "Gelo", 0.5f);

        put("Sombrio", "Sombrio", 0.5f);
        put("Sombrio", "Fantasma", 2.0f);
        put("Sombrio", "Normal", 1.0f);

        put("Fantasma", "Normal", 0.0f);
        put("Fantasma", "Fantasma", 2.0f);
        put("Fantasma", "Sombrio", 0.5f);
    }

    private void put(String attacker, String defender, float multiplier) {
        chart.computeIfAbsent(attacker, k -> new HashMap<>());
        chart.get(attacker).put(defender, multiplier);
    }

    /**
     * Retorna o multiplicador de efetividade.
     *
     * @param attackerType tipo do atacante
     * @param defenderType tipo do defensor
     * @return multiplicador (1.0 se não houver relação especial)
     */
    public float getEffectiveness(String attackerType, String defenderType) {
        HashMap<String, Float> attacks = chart.get(attackerType);
        if (attacks == null) return 1.0f;
        return attacks.getOrDefault(defenderType, 1.0f);
    }

    private static final TypeChart INSTANCE = new TypeChart();

    public static TypeChart getInstance() {
        return INSTANCE;
    }
}
