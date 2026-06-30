package com.pokedungeon.game.screens;

import java.util.ArrayList;
import java.util.HashMap;

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
import com.pokedungeon.game.battle.BattleManager;
import com.pokedungeon.game.battle.TurnAction;
import com.pokedungeon.game.model.Pokemon;

public class BattleScreen implements Screen {

    private Main game;
    private DungeonScreen dungeonScreen;
    private BattleManager battleManager;
    private GameSession session;

    private Texture playerBackSprite;
    private Texture enemyFrontSprite;
    private Texture dialogBox;

    private HashMap<String, String> frontSpriteMap;
    private HashMap<String, String> backSpriteMap;
    private HashMap<String, Texture> teamBackSprites;

    private ArrayList<String> attackNames;

    private enum UIState { CHOOSE_ACTION, CHOOSE_SWITCH, PROCESSING, BATTLE_OVER }
    private UIState uiState;

    private int selectedIndex = 0;
    private float inputCooldown = 0f;
    private static final float INPUT_DELAY = 0.2f;

    public BattleScreen(Main game, DungeonScreen dungeonScreen,
                        GameSession session, Pokemon playerPokemon, Pokemon enemyPokemon) {
        this.game = game;
        this.dungeonScreen = dungeonScreen;
        this.session = session;
        this.battleManager = new BattleManager(playerPokemon, enemyPokemon);
        this.attackNames = new ArrayList<>(playerPokemon.getAttacks().keySet());
        this.uiState = UIState.CHOOSE_ACTION;

        frontSpriteMap = new HashMap<>();
        frontSpriteMap.put("Charmander", "sprites/charmander.PNG");
        frontSpriteMap.put("Squirtle", "sprites/squirtle.PNG");
        frontSpriteMap.put("Rattata", "sprites/rattata.PNG");
        frontSpriteMap.put("Zubat", "sprites/zubat.PNG");
        frontSpriteMap.put("Onix", "sprites/onyx.PNG");
        frontSpriteMap.put("Pidgey", "sprites/rattata.PNG");
        frontSpriteMap.put("Pidgeotto", "sprites/rattata.PNG");
        frontSpriteMap.put("Caterpie", "sprites/rattata.PNG");
        frontSpriteMap.put("Metapod", "sprites/rattata.PNG");
        frontSpriteMap.put("Geodude", "sprites/onyx.PNG");
        frontSpriteMap.put("Ekans", "sprites/zubat.PNG");

        backSpriteMap = new HashMap<>();
        backSpriteMap.put("Charmander", "sprites/charmandercostas.PNG");
        backSpriteMap.put("Squirtle", "sprites/squirtledecostas.PNG");

        teamBackSprites = new HashMap<>();
        for (Pokemon p : session.getPlayer().getTeam()) {
            String path = backSpriteMap.get(p.getName());
            if (path != null) {
                Texture tex = new Texture(Gdx.files.internal(path));
                tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                teamBackSprites.put(p.getName(), tex);
            }
        }

        playerBackSprite = teamBackSprites.get(playerPokemon.getName());

        String frontPath = frontSpriteMap.get(enemyPokemon.getName());
        if (frontPath != null) {
            enemyFrontSprite = new Texture(Gdx.files.internal(frontPath));
            enemyFrontSprite.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

        dialogBox = new Texture(Gdx.files.internal("ui/barratexto.PNG"));
        dialogBox.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        inputCooldown -= delta;
        handleInput();

        ScreenUtils.clear(0.58f, 0.76f, 0.48f, 1f);

        SpriteBatch batch = game.getBatch();
        BitmapFont font = game.getFont();
        Viewport viewport = game.getViewport();

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        float sw = viewport.getWorldWidth();
        float sh = viewport.getWorldHeight();

        Pokemon playerPkm = battleManager.getPlayerPokemon();
        Pokemon enemyPkm = battleManager.getEnemyPokemon();

        float dialogH = 80;
        float dialogY = 0;
        float battleAreaH = sh - dialogH;

        batch.begin();

        batch.setColor(Color.WHITE);

        // Enemy sprite
        float enemySpriteSize = 56;
        float enemyX = sw - enemySpriteSize - 50;
        float enemyY = dialogH + battleAreaH * 0.5f;
        if (enemyFrontSprite != null) {
            batch.draw(enemyFrontSprite, enemyX, enemyY, enemySpriteSize, enemySpriteSize);
        }

        // Player sprite
        float playerSpriteSize = 52;
        float playerX = 40;
        float playerY = dialogH + 10;
        if (playerBackSprite != null) {
            batch.draw(playerBackSprite, playerX, playerY, playerSpriteSize, playerSpriteSize);
        }

        // Enemy info box
        float infoBoxW = 140;
        float infoBoxH = 36;
        float enemyInfoX = 10;
        float enemyInfoY = sh - infoBoxH - 8;

        batch.setColor(0.95f, 0.93f, 0.85f, 1f);
        batch.draw(game.getPixelWhite(), enemyInfoX, enemyInfoY, infoBoxW, infoBoxH);
        batch.setColor(0.3f, 0.3f, 0.3f, 1f);
        batch.draw(game.getPixelWhite(), enemyInfoX, enemyInfoY, infoBoxW, 2);
        batch.setColor(Color.WHITE);

        font.setColor(Color.BLACK);
        font.draw(batch, enemyPkm.getName(), enemyInfoX + 6, enemyInfoY + infoBoxH - 6);

        float barW = 80;
        float barH = 5;
        float eBarX = enemyInfoX + 6;
        float eBarY = enemyInfoY + 8;
        float enemyHpRatio = (float) enemyPkm.getHp() / enemyPkm.getMaxHp();

        font.setColor(Color.ORANGE);
        font.draw(batch, "HP", eBarX, eBarY + barH + 2);

        batch.setColor(Color.DARK_GRAY);
        batch.draw(game.getPixelWhite(), eBarX + 20, eBarY, barW, barH);
        batch.setColor(getHpColor(enemyHpRatio));
        batch.draw(game.getPixelWhite(), eBarX + 20, eBarY, barW * enemyHpRatio, barH);
        batch.setColor(Color.WHITE);

        // Player info box
        float playerInfoX = sw - infoBoxW - 10;
        float playerInfoY = dialogH + battleAreaH * 0.12f;

        batch.setColor(0.95f, 0.93f, 0.85f, 1f);
        batch.draw(game.getPixelWhite(), playerInfoX, playerInfoY, infoBoxW, infoBoxH + 8);
        batch.setColor(0.3f, 0.3f, 0.3f, 1f);
        batch.draw(game.getPixelWhite(), playerInfoX, playerInfoY + infoBoxH + 6, infoBoxW, 2);
        batch.setColor(Color.WHITE);

        font.setColor(Color.BLACK);
        font.draw(batch, playerPkm.getName(), playerInfoX + 6, playerInfoY + infoBoxH + 2);

        float pBarX = playerInfoX + 6;
        float pBarY = playerInfoY + 16;
        float playerHpRatio = (float) playerPkm.getHp() / playerPkm.getMaxHp();

        font.setColor(Color.ORANGE);
        font.draw(batch, "HP", pBarX, pBarY + barH + 2);

        batch.setColor(Color.DARK_GRAY);
        batch.draw(game.getPixelWhite(), pBarX + 20, pBarY, barW, barH);
        batch.setColor(getHpColor(playerHpRatio));
        batch.draw(game.getPixelWhite(), pBarX + 20, pBarY, barW * playerHpRatio, barH);
        batch.setColor(Color.WHITE);

        font.setColor(Color.BLACK);
        font.draw(batch, playerPkm.getHp() + "/" + playerPkm.getMaxHp(), pBarX + 20, pBarY - 2);

        // Dialog box
        batch.draw(dialogBox, 0, dialogY, sw, dialogH);

        float textX = 16;
        float textY = dialogH - 16;

        switch (uiState) {
            case CHOOSE_ACTION:
                drawAttackMenu(batch, font, textX, textY, sw);
                break;
            case CHOOSE_SWITCH:
                drawSwitchMenu(batch, font, textX, textY);
                break;
            case PROCESSING:
                if (!battleManager.isBattleOver()) {
                    Pokemon current = battleManager.getPlayerPokemon();
                    if (current.isFainted()) {
                        Pokemon next = session.getPlayer().getActivePokemon();
                        if (next != null) {
                            battleManager.setPlayerPokemon(next);
                            battleManager.getBattleLog().log(next.getName() + " foi enviado!");
                            playerBackSprite = teamBackSprites.get(next.getName());
                            updateAttackNames();
                        } else {
                            battleManager.getBattleLog().log("Todos os Pokemon desmaiaram! Perdeu!");
                            battleManager.forceDefeat();
                        }
                    }
                }
                uiState = UIState.CHOOSE_ACTION;
                if (battleManager.isBattleOver()) uiState = UIState.BATTLE_OVER;
                break;
            case BATTLE_OVER:
                drawBattleOver(batch, font, textX, textY);
                break;

        }

        drawBattleLog(batch, font, 10, dialogH + battleAreaH * 0.42f);

        font.setColor(Color.WHITE);
        batch.end();
    }

    private void drawAttackMenu(SpriteBatch batch, BitmapFont font, float startX, float startY, float sw) {
        Pokemon p = battleManager.getPlayerPokemon();

        float col1X = startX;
        float col2X = sw / 2 + 10;
        float rowH = 18;

        for (int i = 0; i < attackNames.size(); i++) {
            String name = attackNames.get(i);
            float x = (i % 2 == 0) ? col1X : col2X;
            float y = startY - (i / 2) * rowH;

            if (i == selectedIndex) {
                font.setColor(Color.BLACK);
                font.draw(batch, "> " + name, x, y);
            } else {
                font.setColor(Color.DARK_GRAY);
                font.draw(batch, "  " + name, x, y);
            }
        }

        int trocarIndex = attackNames.size();
        int trocarRow = trocarIndex / 2;
        float trocarX = (trocarIndex % 2 == 0) ? col1X : col2X;
        float trocarY = startY - trocarRow * rowH;

        if (selectedIndex == trocarIndex) {
            font.setColor(Color.BLACK);
            font.draw(batch, "> TROCAR", trocarX, trocarY);
        } else {
            font.setColor(Color.DARK_GRAY);
            font.draw(batch, "  TROCAR", trocarX, trocarY);
        }
    }

    private void drawSwitchMenu(SpriteBatch batch, BitmapFont font, float startX, float startY) {
        font.setColor(Color.BLACK);
        font.draw(batch, "Para qual Pokemon?", startX, startY);
        float y = startY - 16;

        for (int i = 0; i < session.getPlayer().getTeam().size(); i++) {
            Pokemon p = session.getPlayer().getTeam().get(i);
            boolean isCurrent = (p == battleManager.getPlayerPokemon());

            if (i == selectedIndex) {
                font.setColor(Color.BLACK);
                font.draw(batch, ">", startX, y);
            }

            if (p.isFainted()) {
                font.setColor(Color.RED);
            } else if (isCurrent) {
                font.setColor(Color.LIGHT_GRAY);
            } else {
                font.setColor(Color.BLACK);
            }
            font.draw(batch, p.getName() + " HP:" + p.getHp() + "/" + p.getMaxHp(),
                startX + 15, y);

            if (isCurrent) {
                font.draw(batch, "(ATUAL)", startX + 140, y);
            }
            y -= 14;
        }

        font.setColor(Color.GRAY);
        font.draw(batch, "[ESC] Voltar", startX, y - 4);
    }

    private void drawBattleOver(SpriteBatch batch, BitmapFont font, float startX, float startY) {
        if (battleManager.isPlayerWinner()) {
            font.setColor(Color.BLACK);
            font.draw(batch, battleManager.getEnemyPokemon().getName() + " foi derrotado!", startX, startY);
            font.setColor(Color.DARK_GRAY);
            font.draw(batch, "[ENTER] Continuar", startX, startY - 20);
        } else {
            font.setColor(Color.RED);
            font.draw(batch, "Derrota!", startX, startY);
            font.setColor(Color.DARK_GRAY);
            font.draw(batch, "[ENTER] Game Over", startX, startY - 20);
        }
    }

    private Color getHpColor(float ratio) {
        if (ratio > 0.5f) return Color.GREEN;
        if (ratio > 0.2f) return Color.YELLOW;
        return Color.RED;
    }

    private void drawBattleLog(SpriteBatch batch, BitmapFont font, float x, float y) {
        var history = battleManager.getBattleLog().getHistory();
        if (history.isEmpty()) return;

        int start = Math.max(0, history.size() - 2);
        font.setColor(Color.WHITE);
        for (int i = history.size() - 1; i >= start; i--) {
            font.draw(batch, history.get(i), x, y);
            y -= 12;
        }
    }

    private void updateAttackNames() {
        this.attackNames = new ArrayList<>(battleManager.getPlayerPokemon().getAttacks().keySet());
    }

    private void handleInput() {
        if (inputCooldown > 0) return;

        switch (uiState) {
            case CHOOSE_ACTION: handleActionInput(); break;
            case CHOOSE_SWITCH: handleSwitchInput(); break;
            case BATTLE_OVER: handleBattleOverInput(); break;
            default: break;
        }
    }

    private void handleActionInput() {
        int maxIndex = attackNames.size();

        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            selectedIndex = (selectedIndex - 1 + maxIndex + 1) % (maxIndex + 1);
            inputCooldown = INPUT_DELAY;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            selectedIndex = (selectedIndex + 1) % (maxIndex + 1);
            inputCooldown = INPUT_DELAY;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (selectedIndex < attackNames.size()) {
                TurnAction playerAction = TurnAction.attack(
                    battleManager.getPlayerPokemon(),
                    battleManager.getEnemyPokemon(),
                    attackNames.get(selectedIndex));
                battleManager.enqueuePlayerAction(playerAction);
                battleManager.enqueueEnemyAction();
                battleManager.processTurn();
                uiState = UIState.PROCESSING;
            } else {
                selectedIndex = 0;
                uiState = UIState.CHOOSE_SWITCH;
            }
            inputCooldown = INPUT_DELAY;
        }

        for (int i = 0; i < attackNames.size() && i < 4; i++) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
                TurnAction action = TurnAction.attack(
                    battleManager.getPlayerPokemon(),
                    battleManager.getEnemyPokemon(),
                    attackNames.get(i));
                battleManager.enqueuePlayerAction(action);
                battleManager.enqueueEnemyAction();
                battleManager.processTurn();
                uiState = UIState.PROCESSING;
                inputCooldown = INPUT_DELAY;
                return;
            }
        }
    }

    private void handleSwitchInput() {
        int maxIndex = session.getPlayer().getTeam().size();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            selectedIndex = 0;
            uiState = UIState.CHOOSE_ACTION;
            inputCooldown = INPUT_DELAY;
            return;
        }

        if (maxIndex <= 1) {
            selectedIndex = 0;
            uiState = UIState.CHOOSE_ACTION;
            inputCooldown = INPUT_DELAY;
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            selectedIndex = (selectedIndex - 1 + maxIndex) % maxIndex;
            inputCooldown = INPUT_DELAY;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            selectedIndex = (selectedIndex + 1) % maxIndex;
            inputCooldown = INPUT_DELAY;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            Pokemon target = session.getPlayer().getTeam().get(selectedIndex);
            Pokemon current = battleManager.getPlayerPokemon();

            if (!target.isFainted() && target != current) {
                TurnAction switchAction = TurnAction.switchPkm(current, target);
                battleManager.enqueuePlayerAction(switchAction);
                battleManager.enqueueEnemyAction();
                battleManager.processTurn();
                playerBackSprite = teamBackSprites.get(target.getName());
                updateAttackNames();
                selectedIndex = 0;
                uiState = UIState.PROCESSING;
            }
            inputCooldown = INPUT_DELAY;
        }
    }

    private void handleBattleOverInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (battleManager.isPlayerWinner()) {
                Pokemon defeated = battleManager.getEnemyPokemon();
                session.onBattleWon(defeated);
                dungeonScreen.onBattleWon(defeated);
            } else {
                session.setGameOver(true);
                game.setScreen(dungeonScreen);
            }
            inputCooldown = INPUT_DELAY;
        }
    }

    @Override
    public void resize(int w, int h) { game.getViewport().update(w, h, true); }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (enemyFrontSprite != null) enemyFrontSprite.dispose();
        if (dialogBox != null) dialogBox.dispose();
        for (Texture t : teamBackSprites.values()) {
            t.dispose();
        }
    }
}
