package com.pokedungeon.game.utils;

/**
 * Constantes globais do jogo PokeDungeon.
 * Centraliza valores fixos para facilitar manutenção.
 */
public final class GameConstants {

    private GameConstants() {
        // Classe utilitária, não deve ser instanciada
    }

    // Dimensões da janela
    public static final int SCREEN_WIDTH = 640;
    public static final int SCREEN_HEIGHT = 480;

    // Tamanho do tile no mapa da dungeon
    public static final int TILE_SIZE = 32;

    // Limites do jogador
    public static final int MAX_TEAM_SIZE = 3;
    public static final int MAX_INVENTORY_SIZE = 10;

    // Valores padrão de batalha
    public static final int DEFAULT_POTION_HEAL = 20;

    // Velocidade de movimento (pixels por segundo)
    public static final float PLAYER_SPEED = 100f;

    // Título do jogo
    public static final String GAME_TITLE = "PokeDungeon";
}
