package com.pokedungeon.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pokedungeon.game.GameSession;
import com.pokedungeon.game.Main;
import com.pokedungeon.game.model.Pokemon;
import com.pokedungeon.game.utils.GameConstants;

public class StarterChoiceScreen implements Screen {

    private Main game;
    private GameSession session;

    private String[] starterNames = {"Charmander", "Squirtle", "Bulbasaur", "Pikachu"};
    private String[] starterTypes = {"Fogo", "Água", "Planta", "Elétrico"};
    private String[] starterDescs = {
        "Ataque alto. Forte contra Planta.",
        "HP alto. Forte contra Fogo.",
        "Balanceado. Forte contra Água.",
        "Rápido. Forte contra Voador."
    };
    private int[] starterHPs = {80, 90, 85, 75};
    private int[] starterDMGs = {25, 22, 20, 28};

    private int selectedIndex = 0;
    private float inputCooldown = 0f;

    private Texture[] starterSprites;

    public StarterChoiceScreen(Main game, GameSession session) {
        this.game = game;
        this.session = session;

        starterSprites = new Texture[starterNames.length];
        for (int i = 0; i < starterNames.length; i++) {
            String path = "sprites/" + starterNames[i].toLowerCase() + ".PNG";
            if (Gdx.files.internal(path).exists()) {
                starterSprites[i] = new Texture(Gdx.files.internal(path));
                starterSprites[i].setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            }
        }
    }

    @Override
    public void show() {
        selectedIndex = 0;
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

        batch.begin();

        font.setColor(Color.YELLOW);
        font.draw(batch, "Escolha seu Pokemon!", sw / 2 - 80, sh - 20);

        float startY = sh / 2 - 30;
        float spacing = 28;

        for (int i = 0; i < starterNames.length; i++) {
            float y = startY + i * spacing;
            boolean selected = (i == selectedIndex);

            if (selected) {
                font.setColor(Color.YELLOW);
                font.draw(batch, "> " + starterNames[i], 20, y);
                font.setColor(Color.WHITE);
                font.draw(batch, "[" + starterTypes[i] + "] " + starterDescs[i], 120, y);
            } else {
                font.setColor(Color.LIGHT_GRAY);
                font.draw(batch, "  " + starterNames[i], 20, y);
                font.setColor(Color.GRAY);
                font.draw(batch, "[" + starterTypes[i] + "] " + starterDescs[i], 120, y);
            }

            if (starterSprites[i] != null && selected) {
                batch.draw(starterSprites[i], sw - 60, y - 16, 32, 32);
            }
        }

        font.setColor(Color.GRAY);
        font.draw(batch, "[W/S] Navegar  [ENTER] Escolher", 20, 20);

        batch.end();
    }

    private void handleInput() {
        if (inputCooldown > 0) return;

        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            selectedIndex = (selectedIndex - 1 + starterNames.length) % starterNames.length;
            inputCooldown = 0.2f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            selectedIndex = (selectedIndex + 1) % starterNames.length;
            inputCooldown = 0.2f;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            Pokemon starter = new Pokemon(starterNames[selectedIndex], starterTypes[selectedIndex],
                GameConstants.STARTING_LEVEL, starterHPs[selectedIndex]);
            starter.addAttack("Ataque", starterDMGs[selectedIndex], starterTypes[selectedIndex]);
            starter.addAttack("Investida", starterDMGs[selectedIndex] - 5, "Normal");

            session.getPlayer().addPokemon(starter);
            game.setScreen(new DungeonScreen(game, session));
            dispose();
        }
    }

    @Override public void resize(int w, int h) { game.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        for (Texture t : starterSprites) {
            if (t != null) t.dispose();
        }
    }
}
