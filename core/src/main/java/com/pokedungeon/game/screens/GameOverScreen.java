package com.pokedungeon.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pokedungeon.game.Main;

/**
 * Tela de Game Over.
 */
public class GameOverScreen implements Screen {

    private Main game;
    private int floorReached;
    private float inputCooldown = 0f;

    public GameOverScreen(Main game, int floorReached) {
        this.game = game;
        this.floorReached = floorReached;
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        inputCooldown -= delta;

        ScreenUtils.clear(0.05f, 0.05f, 0.05f, 1f);

        SpriteBatch batch = game.getBatch();
        BitmapFont font = game.getFont();
        Viewport viewport = game.getViewport();

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        float sw = viewport.getWorldWidth();
        float sh = viewport.getWorldHeight();

        batch.begin();

        font.setColor(Color.RED);
        font.draw(batch, "GAME OVER", sw / 2 - 50, sh / 2 - 20);

        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "Andar: " + floorReached, sw / 2 - 30, sh / 2 + 10);

        font.setColor(Color.YELLOW);
        font.draw(batch, "[ENTER] Menu Principal", sw / 2 - 70, sh / 2 + 40);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && inputCooldown <= 0) {
            game.setScreen(new MenuScreen(game));
            inputCooldown = 0.3f;
        }

        batch.end();
    }

    @Override public void resize(int w, int h) { game.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
