package com.pokedungeon.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.pokedungeon.game.Main;

/**
 * Tela do menu principal.
 * O jogador pode escolher "Jogar" ou "Sair".
 *
 * Visual: inspirado no estilo retrô de Pokémon Red.
 */
public class MenuScreen implements Screen {

    private Main game;
    private ShapeRenderer shapeRenderer;
    private GlyphLayout layout;

    private String[] menuOptions = {"JOGAR", "SAIR"};
    private int selectedOption = 0;

    // Controle de input para evitar repetição rápida
    private float inputCooldown = 0f;
    private static final float INPUT_DELAY = 0.2f;

    public MenuScreen(Main game) {
        this.game = game;
        this.shapeRenderer = new ShapeRenderer();
        this.layout = new GlyphLayout();
    }

    @Override
    public void show() {
        selectedOption = 0;
    }

    @Override
    public void render(float delta) {
        inputCooldown -= delta;

        handleInput();

        // Fundo escuro estilo Pokémon Red
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);

        SpriteBatch batch = game.getBatch();
        BitmapFont font = game.getFont();

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // --- Desenha a caixa do menu ---
        float boxWidth = 300;
        float boxHeight = 200;
        float boxX = (screenWidth - boxWidth) / 2;
        float boxY = (screenHeight - boxHeight) / 2 - 40;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.15f, 0.15f, 0.22f, 1f);
        shapeRenderer.rect(boxX, boxY, boxWidth, boxHeight);
        shapeRenderer.end();

        // Borda da caixa
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(boxX, boxY, boxWidth, boxHeight);
        shapeRenderer.end();

        // --- Texto ---
        batch.begin();

        // Título
        font.getData().setScale(2f);
        font.setColor(Color.WHITE);
        layout.setText(font, "PokeDungeon");
        float titleX = (screenWidth - layout.width) / 2;
        float titleY = screenHeight - 80;
        font.draw(batch, "PokeDungeon", titleX, titleY);

        // Subtítulo
        font.getData().setScale(0.9f);
        font.setColor(0.7f, 0.7f, 0.7f, 1f);
        layout.setText(font, "Pressione ENTER para selecionar");
        float subX = (screenWidth - layout.width) / 2;
        font.draw(batch, "Pressione ENTER para selecionar", subX, titleY - 40);

        // Opções do menu
        font.getData().setScale(1.5f);
        for (int i = 0; i < menuOptions.length; i++) {
            if (i == selectedOption) {
                font.setColor(Color.YELLOW);
                String text = "> " + menuOptions[i];
                layout.setText(font, text);
                float optX = (screenWidth - layout.width) / 2;
                font.draw(batch, text, optX, boxY + boxHeight - 50 - (i * 60));
            } else {
                font.setColor(Color.WHITE);
                String text = "  " + menuOptions[i];
                layout.setText(font, text);
                float optX = (screenWidth - layout.width) / 2;
                font.draw(batch, text, optX, boxY + boxHeight - 50 - (i * 60));
            }
        }

        // Restaura escala da fonte
        font.getData().setScale(1.5f);
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
            selectOption();
        }
    }

    private void selectOption() {
        switch (selectedOption) {
            case 0: // JOGAR
                game.setScreen(new DungeonScreen(game));
                dispose();
                break;
            case 1: // SAIR
                Gdx.app.exit();
                break;
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
