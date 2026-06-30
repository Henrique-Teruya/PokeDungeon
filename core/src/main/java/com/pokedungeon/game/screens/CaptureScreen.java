package com.pokedungeon.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pokedungeon.game.GameSession;
import com.pokedungeon.game.Main;
import com.pokedungeon.game.model.Pokemon;
import com.pokedungeon.game.utils.GameConstants;

public class CaptureScreen implements Screen {

    private Main game;
    private DungeonScreen dungeonScreen;
    private GameSession session;
    private Pokemon capturedPokemon;
    private boolean captured;
    private boolean decisionMade;
    private int selectedOption;
    private float inputCooldown = 0f;

    // Team replacement mode
    private boolean choosingReplacement;
    private int replacementIndex;

    public CaptureScreen(Main game, DungeonScreen dungeonScreen, GameSession session, Pokemon enemy) {
        this.game = game;
        this.dungeonScreen = dungeonScreen;
        this.session = session;
        this.capturedPokemon = enemy;
        this.captured = false;
        this.decisionMade = false;
        this.selectedOption = 0;
        this.choosingReplacement = false;
        this.replacementIndex = 0;
    }

    @Override
    public void show() {}

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

        if (choosingReplacement) {
            drawReplacementChoice(batch, font, sw, sh);
        } else if (!decisionMade) {
            drawCaptureChoice(batch, font, sw, sh);
        } else if (captured) {
            drawCaptured(batch, font, sw, sh);
        } else {
            drawFailed(batch, font, sw, sh);
        }

        batch.end();
    }

    private void drawCaptureChoice(SpriteBatch batch, BitmapFont font, float sw, float sh) {
        font.setColor(Color.YELLOW);
        font.draw(batch, capturedPokemon.getName() + " foi derrotado!", sw / 2 - 80, sh / 2 - 40);

        font.setColor(Color.WHITE);
        font.draw(batch, "Tipo: " + capturedPokemon.getType() + " Nv." + capturedPokemon.getLevel(), sw / 2 - 60, sh / 2 - 20);
        font.draw(batch, "Pokéballs: " + session.getPokeballs(), sw / 2 - 50, sh / 2);

        boolean teamFull = session.getPlayer().getTeam().size() >= GameConstants.MAX_TEAM_SIZE;
        String[] options;
        if (teamFull) {
            options = new String[]{"SUBSTITUIR [ENTER]", "NÃO [ENTER]"};
        } else {
            options = new String[]{"CAPTURAR [ENTER]", "NÃO [ENTER]"};
        }

        for (int i = 0; i < options.length; i++) {
            if (i == selectedOption) {
                font.setColor(Color.YELLOW);
                font.draw(batch, "> " + options[i], sw / 2 - 60, sh / 2 + 20 + i * 20);
            } else {
                font.setColor(Color.LIGHT_GRAY);
                font.draw(batch, "  " + options[i], sw / 2 - 60, sh / 2 + 20 + i * 20);
            }
        }
    }

    private void drawReplacementChoice(SpriteBatch batch, BitmapFont font, float sw, float sh) {
        font.setColor(Color.YELLOW);
        font.draw(batch, "Substituir qual Pokemon?", sw / 2 - 80, sh / 2 - 40);

        float y = sh / 2 - 10;
        for (int i = 0; i < session.getPlayer().getTeam().size(); i++) {
            Pokemon p = session.getPlayer().getTeam().get(i);
            if (i == replacementIndex) {
                font.setColor(Color.YELLOW);
                font.draw(batch, "> " + p.getName() + " HP:" + p.getHp() + "/" + p.getMaxHp(), sw / 2 - 80, y);
            } else {
                font.setColor(Color.LIGHT_GRAY);
                font.draw(batch, "  " + p.getName() + " HP:" + p.getHp() + "/" + p.getMaxHp(), sw / 2 - 80, y);
            }
            y += 18;
        }

        font.setColor(Color.GRAY);
        font.draw(batch, "[W/S] Navegar  [ENTER] Confirmar  [ESC] Cancelar", sw / 2 - 110, sh - 20);
    }

    private void drawCaptured(SpriteBatch batch, BitmapFont font, float sw, float sh) {
        font.setColor(Color.GREEN);
        font.draw(batch, capturedPokemon.getName() + " capturado!", sw / 2 - 60, sh / 2);
        font.setColor(Color.YELLOW);
        font.draw(batch, "[ENTER] Continuar", sw / 2 - 55, sh / 2 + 30);
    }

    private void drawFailed(SpriteBatch batch, BitmapFont font, float sw, float sh) {
        font.setColor(Color.RED);
        font.draw(batch, "Falhou na captura!", sw / 2 - 55, sh / 2);
        font.setColor(Color.YELLOW);
        font.draw(batch, "[ENTER] Continuar", sw / 2 - 55, sh / 2 + 30);
    }

    private void handleInput() {
        if (inputCooldown > 0) return;

        if (choosingReplacement) {
            handleReplacementInput();
        } else if (!decisionMade) {
            handleCaptureInput();
        } else {
            handleContinueInput();
        }
    }

    private void handleCaptureInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            selectedOption = (selectedOption + 1) % 2;
            inputCooldown = 0.2f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            selectedOption = (selectedOption - 1 + 2) % 2;
            inputCooldown = 0.2f;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (selectedOption == 0) {
                boolean teamFull = session.getPlayer().getTeam().size() >= GameConstants.MAX_TEAM_SIZE;
                if (teamFull) {
                    choosingReplacement = true;
                    replacementIndex = 0;
                } else {
                    attemptCapture();
                }
            } else {
                decisionMade = true;
                captured = false;
            }
            inputCooldown = 0.2f;
        }
    }

    private void handleReplacementInput() {
        int maxIndex = session.getPlayer().getTeam().size();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            choosingReplacement = false;
            decisionMade = true;
            captured = false;
            inputCooldown = 0.2f;
            return;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            replacementIndex = (replacementIndex + 1) % maxIndex;
            inputCooldown = 0.2f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            replacementIndex = (replacementIndex - 1 + maxIndex) % maxIndex;
            inputCooldown = 0.2f;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            attemptCaptureWithReplacement();
            inputCooldown = 0.2f;
        }
    }

    private void handleContinueInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            dungeonScreen.afterCapture();
            inputCooldown = 0.3f;
        }
    }

    private void attemptCapture() {
        decisionMade = true;
        captured = session.tryCapture(capturedPokemon);
        if (captured) {
            session.addCapturedPokemon(capturedPokemon);
        }
    }

    private void attemptCaptureWithReplacement() {
        decisionMade = true;
        captured = session.tryCapture(capturedPokemon);
        if (captured) {
            session.replacePokemon(replacementIndex, capturedPokemon);
        }
    }

    @Override public void resize(int w, int h) { game.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
