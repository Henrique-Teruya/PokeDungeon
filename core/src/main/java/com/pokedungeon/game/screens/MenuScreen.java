package com.pokedungeon.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pokedungeon.game.Main;

public class MenuScreen implements Screen {

    private Main game;
    private GlyphLayout layout;
    private Texture personagemSprite;

    private String[] menuOptions = {"JOGAR", "SAIR"};
    private int selectedOption = 0;
    private float inputCooldown = 0f;
    private static final float INPUT_DELAY = 0.2f;

    public MenuScreen(Main game) {
        this.game = game;
        this.layout = new GlyphLayout();
        personagemSprite = new Texture(Gdx.files.internal("sprites/player/player_down.png"));
        personagemSprite.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    @Override
    public void show() {
        selectedOption = 0;
    }

    @Override
    public void render(float delta) {
        inputCooldown -= delta;
        handleInput();

        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);

        SpriteBatch batch = game.getBatch();
        BitmapFont font = game.getFont();
        Viewport viewport = game.getViewport();

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        float sw = viewport.getWorldWidth();
        float sh = viewport.getWorldHeight();

        // Tamanhos ajustados para resolução 426x240
        float boxWidth = 200;
        float boxHeight = 100;
        float boxX = (sw - boxWidth) / 2;
        float boxY = (sh - boxHeight) / 2 - 20;

        batch.begin();

        // Sprite do personagem centralizado acima do menu
        float spriteSize = 48;
        float spriteX = (sw - spriteSize) / 2;
        float spriteY = boxY + boxHeight + 10;
        batch.draw(personagemSprite, spriteX, spriteY, spriteSize, spriteSize);

        // Fundo da caixa (Usando placeholder branco)
        batch.setColor(0.15f, 0.15f, 0.22f, 1f);
        batch.draw(game.getPixelWhite(), boxX, boxY, boxWidth, boxHeight);

        // Borda da caixa (4 linhas)
        batch.setColor(Color.WHITE);
        batch.draw(game.getPixelWhite(), boxX, boxY, boxWidth, 2); // base
        batch.draw(game.getPixelWhite(), boxX, boxY + boxHeight - 2, boxWidth, 2); // topo
        batch.draw(game.getPixelWhite(), boxX, boxY, 2, boxHeight); // esq
        batch.draw(game.getPixelWhite(), boxX + boxWidth - 2, boxY, 2, boxHeight); // dir

        batch.setColor(Color.WHITE);

        // Título
        font.setColor(Color.WHITE);
        layout.setText(font, "PokeDungeon");
        font.draw(batch, "PokeDungeon", (sw - layout.width) / 2, sh - 30);

        // Subtítulo
        font.setColor(0.7f, 0.7f, 0.7f, 1f);
        layout.setText(font, "Aperte ENTER");
        font.draw(batch, "Aperte ENTER", (sw - layout.width) / 2, sh - 50);

        // Opções do menu
        for (int i = 0; i < menuOptions.length; i++) {
            if (i == selectedOption) {
                font.setColor(Color.YELLOW);
                String text = "> " + menuOptions[i];
                layout.setText(font, text);
                font.draw(batch, text, (sw - layout.width) / 2, boxY + boxHeight - 30 - (i * 25));
            } else {
                font.setColor(Color.WHITE);
                String text = "  " + menuOptions[i];
                layout.setText(font, text);
                font.draw(batch, text, (sw - layout.width) / 2, boxY + boxHeight - 30 - (i * 25));
            }
        }

        font.setColor(Color.WHITE);
        batch.end();
    }

    private void handleInput() {
        if (inputCooldown > 0) return;

        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            selectedOption = (selectedOption - 1 + menuOptions.length) % menuOptions.length;
            inputCooldown = INPUT_DELAY;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            selectedOption = (selectedOption + 1) % menuOptions.length;
            inputCooldown = INPUT_DELAY;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (selectedOption == 0) {
                game.setScreen(new DungeonScreen(game));
                dispose();
            } else {
                Gdx.app.exit();
            }
        }
    }

    @Override public void resize(int w, int h) { game.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { personagemSprite.dispose(); }
}
