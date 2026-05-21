package com.pokedungeon.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pokedungeon.game.screens.MenuScreen;
import com.pokedungeon.game.utils.GameConstants;
import com.pokedungeon.game.utils.TextureUtils;

/**
 * Classe principal do PokeDungeon.
 * Extende Game para gerenciar múltiplas telas (Screen).
 */
public class Main extends Game {

    private SpriteBatch batch;
    private BitmapFont font;
    private Viewport viewport;
    private Texture pixelWhite;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Viewport que escala a resolução virtual para a janela mantendo a proporção (GBA feel)
        viewport = new FitViewport(GameConstants.VIRTUAL_WIDTH, GameConstants.VIRTUAL_HEIGHT);

        // Carrega a fonte Press Start 2P usando o gerador Freetype
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PressStart2P-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 8; // Tamanho base pequeno para ficar crocante e pixelado
        parameter.color = Color.WHITE;
        font = generator.generateFont(parameter);
        generator.dispose();

        // Textura base de 1x1 para desenhar blocos de UI (placeholders coloridos)
        pixelWhite = TextureUtils.createColorTexture(Color.WHITE);

        // Inicia na tela do menu principal
        setScreen(new MenuScreen(this));
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        super.resize(width, height);
    }

    public SpriteBatch getBatch() { return batch; }
    public BitmapFont getFont() { return font; }
    public Viewport getViewport() { return viewport; }
    public Texture getPixelWhite() { return pixelWhite; }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        pixelWhite.dispose();
        if (getScreen() != null) {
            getScreen().dispose();
        }
    }
}
