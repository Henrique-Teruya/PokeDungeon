package com.pokedungeon.game.utils;

/**
 * Constantes globais do jogo PokeDungeon.
 */
public final class GameConstants {

    private GameConstants() {}

    // Dimensões
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;
    public static final int VIRTUAL_WIDTH = 426;
    public static final int VIRTUAL_HEIGHT = 240;
    public static final int TILE_SIZE = 32;

    // Limites
    public static final int MAX_TEAM_SIZE = 3;
    public static final int MAX_INVENTORY_SIZE = 20;

    // Batalha
    public static final int DEFAULT_POTION_HEAL = 20;
    public static final int BATTLES_TO_CENTER = 3;

    // Captura
    public static final int STARTING_POKEBALLS = 10;
    public static final int POKEBALLS_PER_CENTER = 5;
    public static final float BASE_CAPTURE_RATE = 0.4f;

    // Progressão
    public static final int STARTING_FLOOR = 1;
    public static final int STARTING_LEVEL = 5;
    public static final float LEVEL_HP_SCALE = 1.15f;
    public static final float LEVEL_DMG_SCALE = 1.12f;

    // Velocidade
    public static final float PLAYER_SPEED = 100f;

    // Título
    public static final String GAME_TITLE = "PokeDungeon";
}
