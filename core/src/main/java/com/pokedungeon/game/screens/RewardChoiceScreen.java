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
import com.pokedungeon.game.model.Item;
import com.pokedungeon.game.model.Pokemon;
import com.pokedungeon.game.utils.GameConstants;

public class RewardChoiceScreen implements Screen {

    private Main game;
    private DungeonScreen dungeonScreen;
    private GameSession session;

    private String[] rewardNames = {
        "+3 Pokébolas",
        "+2 Poções",
        "+15 HP Máx",
        "Curar Time",
        "+1 Super Poção"
    };
    private int selectedIndex = 0;
    private float inputCooldown = 0f;

    public RewardChoiceScreen(Main game, DungeonScreen dungeonScreen, GameSession session) {
        this.game = game;
        this.dungeonScreen = dungeonScreen;
        this.session = session;
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
        font.draw(batch, "Escolha sua recompensa!", sw / 2 - 90, sh - 20);

        float startY = sh / 2 - 40;
        float spacing = 22;

        for (int i = 0; i < rewardNames.length; i++) {
            float y = startY + i * spacing;
            if (i == selectedIndex) {
                font.setColor(Color.YELLOW);
                font.draw(batch, "> " + rewardNames[i], sw / 2 - 50, y);
            } else {
                font.setColor(Color.LIGHT_GRAY);
                font.draw(batch, "  " + rewardNames[i], sw / 2 - 50, y);
            }
        }

        font.setColor(Color.GRAY);
        font.draw(batch, "[W/S] Navegar  [ENTER] Escolher", sw / 2 - 80, 20);

        batch.end();
    }

    private void handleInput() {
        if (inputCooldown > 0) return;

        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            selectedIndex = (selectedIndex - 1 + rewardNames.length) % rewardNames.length;
            inputCooldown = 0.2f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            selectedIndex = (selectedIndex + 1) % rewardNames.length;
            inputCooldown = 0.2f;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            applyReward();
            dungeonScreen.onRewardChosen();
            inputCooldown = 0.3f;
        }
    }

    private void applyReward() {
        switch (selectedIndex) {
            case 0: // +3 Pokeballs
                session.addPokeballs(3);
                break;
            case 1: // +2 Potions
                session.getInventory().addItem(new Item("Poção", GameConstants.DEFAULT_POTION_HEAL));
                session.getInventory().addItem(new Item("Poção", GameConstants.DEFAULT_POTION_HEAL));
                break;
            case 2: // +15 Max HP to active
                Pokemon active = session.getPlayer().getActivePokemon();
                if (active != null) {
                    active.heal(15);
                }
                break;
            case 3: // Heal team
                for (Pokemon p : session.getPlayer().getTeam()) {
                    p.fullHeal();
                }
                break;
            case 4: // +1 Super Potion
                session.getInventory().addItem(new Item("Super Poção", 40));
                break;
        }
    }

    @Override public void resize(int w, int h) { game.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
