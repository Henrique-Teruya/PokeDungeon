package com.pokedungeon.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pokedungeon.game.screens.MenuScreen;

/**
 * Classe principal do PokeDungeon.
 * Extende Game para gerenciar múltiplas telas (Screen).
 *
 * Princípios de POO:
 * - Herança: extende Game do LibGDX
 * - Polimorfismo: cada tela implementa Screen de forma diferente
 */
public class Main extends Game {

    private SpriteBatch batch;
    private BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont(); // fonte padrão do LibGDX
        font.getData().setScale(1.5f);

        // Inicia na tela do menu principal
        setScreen(new MenuScreen(this));
    }

    /**
     * @return SpriteBatch compartilhado entre todas as telas
     */
    public SpriteBatch getBatch() {
        return batch;
    }

    /**
     * @return BitmapFont compartilhada entre todas as telas
     */
    public BitmapFont getFont() {
        return font;
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        if (getScreen() != null) {
            getScreen().dispose();
        }
    }
}
