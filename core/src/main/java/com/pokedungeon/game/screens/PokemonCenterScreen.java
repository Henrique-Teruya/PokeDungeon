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
import com.pokedungeon.game.GameSession;
import com.pokedungeon.game.model.Pokemon;

/**
 * Centro Pokémon - cura o time e repõe Pokébolas.
 */
public class PokemonCenterScreen implements Screen {

    private Main game;
    private DungeonScreen dungeonScreen;
    private GameSession session;

    private float inputCooldown = 0f;
    private boolean healed = false;
    private float healProgress = 0f;
    private static final float HEAL_SPEED = 0.5f;

    public PokemonCenterScreen(Main game, DungeonScreen dungeonScreen, GameSession session) {
        this.game = game;
        this.dungeonScreen = dungeonScreen;
        this.session = session;
    }

    @Override
    public void show() {
        healed = false;
        healProgress = 0f;
    }

    @Override
    public void render(float delta) {
        inputCooldown -= delta;

        if (!healed) {
            healProgress += delta;
            if (healProgress >= HEAL_SPEED) {
                session.visitCenter();
                healed = true;
            }
        }

        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);

        SpriteBatch batch = game.getBatch();
        BitmapFont font = game.getFont();
        Viewport viewport = game.getViewport();

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        float sw = viewport.getWorldWidth();
        float sh = viewport.getWorldHeight();

        batch.begin();

        // Fundo do centro
        batch.setColor(0.9f, 0.9f, 0.95f, 1f);
        batch.draw(game.getPixelWhite(), 0, 0, sw, sh);
        batch.setColor(Color.WHITE);

        // Título
        font.setColor(Color.RED);
        font.draw(batch, "CENTRO POKEMON", sw / 2 - 60, sh - 40);

        if (!healed) {
            // Mensagem de cura em andamento
            font.setColor(Color.GREEN);
            font.draw(batch, "Curando Pokemons...", sw / 2 - 55, sh / 2);

            // Barra de progresso
            float barW = 200;
            float barH = 8;
            float barX = (sw - barW) / 2;
            float barY = sh / 2 + 20;

            batch.setColor(Color.DARK_GRAY);
            batch.draw(game.getPixelWhite(), barX, barY, barW, barH);
            batch.setColor(Color.GREEN);
            batch.draw(game.getPixelWhite(), barX, barY, barW * (healProgress / HEAL_SPEED), barH);
            batch.setColor(Color.WHITE);
        } else {
            // Time curado
            font.setColor(Color.GREEN);
            font.draw(batch, "Time curado!", sw / 2 - 45, sh / 2 + 10);

            font.setColor(Color.WHITE);
            font.draw(batch, "Pokéballs: " + session.getPokeballs(), sw / 2 - 40, sh / 2 - 10);

            // Lista do time
            float y = sh / 2 - 40;
            for (Pokemon p : session.getPlayer().getTeam()) {
                font.setColor(p.isFainted() ? Color.RED : Color.CYAN);
                font.draw(batch, p.getName() + " HP: " + p.getHp() + "/" + p.getMaxHp(), 20, y);
                y -= 16;
            }

            font.setColor(Color.YELLOW);
            font.draw(batch, "[ENTER] Continuar", sw / 2 - 55, 30);

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && inputCooldown <= 0) {
                dungeonScreen.afterCenter();
                inputCooldown = 0.3f;
            }
        }

        batch.end();
    }

    @Override public void resize(int w, int h) { game.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
