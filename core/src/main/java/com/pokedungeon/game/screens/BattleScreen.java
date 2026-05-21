package com.pokedungeon.game.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pokedungeon.game.Main;
import com.pokedungeon.game.battle.BattleManager;
import com.pokedungeon.game.battle.TurnAction;
import com.pokedungeon.game.inventory.Inventory;
import com.pokedungeon.game.model.Item;
import com.pokedungeon.game.model.Pokemon;

/**
 * Tela de batalha por turnos com sprites e UI estilo Pokémon GBA.
 *
 * Estruturas de dados utilizadas:
 * - Queue (BattleManager.turnQueue): fila de ações FIFO
 * - Stack (BattleLog.actionHistory): histórico de ações LIFO
 * - HashMap (Pokemon.attacks + spriteMap): ataques e mapeamento de sprites
 * - ArrayList (Inventory.items): itens para usar
 */
public class BattleScreen implements Screen {

    private Main game;
    private DungeonScreen dungeonScreen;
    private BattleManager battleManager;
    private Inventory inventory;

    // Sprites carregados
    private Texture playerBackSprite;
    private Texture enemyFrontSprite;
    private Texture dialogBox;

    // HashMap para mapear nome do pokémon ao caminho do sprite
    private HashMap<String, String> frontSpriteMap;
    private HashMap<String, String> backSpriteMap;

    // Ataques do pokémon do jogador como lista para indexar
    private ArrayList<String> attackNames;

    // Estado da UI
    private enum UIState { CHOOSE_ACTION, CHOOSE_ITEM, PROCESSING, BATTLE_OVER }
    private UIState uiState;

    private int selectedIndex = 0;
    private float inputCooldown = 0f;
    private static final float INPUT_DELAY = 0.2f;

    public BattleScreen(Main game, DungeonScreen dungeonScreen,
                        Pokemon playerPokemon, Pokemon enemyPokemon,
                        Inventory inventory) {
        this.game = game;
        this.dungeonScreen = dungeonScreen;
        this.inventory = inventory;
        this.battleManager = new BattleManager(playerPokemon, enemyPokemon);
        this.attackNames = new ArrayList<>(playerPokemon.getAttacks().keySet());
        this.uiState = UIState.CHOOSE_ACTION;

        // Mapeamento de sprites (HashMap - estrutura de dados)
        frontSpriteMap = new HashMap<>();
        frontSpriteMap.put("Charmander", "sprites/charmander.PNG");
        frontSpriteMap.put("Squirtle", "sprites/squirtle.PNG");
        frontSpriteMap.put("Rattata", "sprites/rattata.PNG");
        frontSpriteMap.put("Zubat", "sprites/zubat.PNG");
        frontSpriteMap.put("Onix", "sprites/onyx.PNG");

        backSpriteMap = new HashMap<>();
        backSpriteMap.put("Charmander", "sprites/charmandercostas.PNG");
        backSpriteMap.put("Squirtle", "sprites/squirtledecostas.PNG");

        // Carrega sprites usando o HashMap
        String backPath = backSpriteMap.get(playerPokemon.getName());
        if (backPath != null) {
            playerBackSprite = new Texture(Gdx.files.internal(backPath));
            playerBackSprite.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

        String frontPath = frontSpriteMap.get(enemyPokemon.getName());
        if (frontPath != null) {
            enemyFrontSprite = new Texture(Gdx.files.internal(frontPath));
            enemyFrontSprite.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

        // Caixa de diálogo
        dialogBox = new Texture(Gdx.files.internal("ui/barratexto.PNG"));
        dialogBox.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        inputCooldown -= delta;
        handleInput();

        // Fundo verde claro estilo Pokémon (campo de batalha)
        ScreenUtils.clear(0.58f, 0.76f, 0.48f, 1f);

        SpriteBatch batch = game.getBatch();
        BitmapFont font = game.getFont();
        Viewport viewport = game.getViewport();

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        float sw = viewport.getWorldWidth();   // 426
        float sh = viewport.getWorldHeight();  // 240

        Pokemon playerPkm = battleManager.getPlayerPokemon();
        Pokemon enemyPkm = battleManager.getEnemyPokemon();

        // Altura da caixa de diálogo (proporção da barratexto original ~2:1)
        float dialogH = 80;
        float dialogY = 0;

        // Área de batalha = acima da caixa de diálogo
        float battleAreaH = sh - dialogH;

        batch.begin();

        // ========== CENÁRIO DA BATALHA ==========

        // Chão da arena (faixa escura na base da área de batalha)
        batch.setColor(0.45f, 0.62f, 0.35f, 1f);
        batch.draw(game.getPixelWhite(), 0, dialogH, sw, battleAreaH * 0.4f);
        batch.setColor(Color.WHITE);

        // --- Sprite do inimigo (topo-direita, 64x64 escalado para 56x56 na virtual) ---
        float enemySpriteSize = 56;
        float enemyX = sw - enemySpriteSize - 50;
        float enemyY = dialogH + battleAreaH * 0.5f;
        if (enemyFrontSprite != null) {
            batch.draw(enemyFrontSprite, enemyX, enemyY, enemySpriteSize, enemySpriteSize);
        }

        // --- Sprite do jogador (embaixo-esquerda, 32x32 escalado para 52x52) ---
        float playerSpriteSize = 52;
        float playerX = 40;
        float playerY = dialogH + 10;
        if (playerBackSprite != null) {
            batch.draw(playerBackSprite, playerX, playerY, playerSpriteSize, playerSpriteSize);
        }

        // ========== INFO BOX DO INIMIGO (topo-esquerda) ==========
        float infoBoxW = 140;
        float infoBoxH = 36;
        float enemyInfoX = 10;
        float enemyInfoY = sh - infoBoxH - 8;

        // Fundo da info box
        batch.setColor(0.95f, 0.93f, 0.85f, 1f);
        batch.draw(game.getPixelWhite(), enemyInfoX, enemyInfoY, infoBoxW, infoBoxH);
        // Borda inferior
        batch.setColor(0.3f, 0.3f, 0.3f, 1f);
        batch.draw(game.getPixelWhite(), enemyInfoX, enemyInfoY, infoBoxW, 2);
        batch.setColor(Color.WHITE);

        // Nome e HP do inimigo
        font.setColor(Color.BLACK);
        font.draw(batch, enemyPkm.getName(), enemyInfoX + 6, enemyInfoY + infoBoxH - 6);

        // Barra de HP do inimigo
        float barW = 80;
        float barH = 5;
        float eBarX = enemyInfoX + 6;
        float eBarY = enemyInfoY + 8;
        float enemyHpRatio = (float) enemyPkm.getHp() / enemyPkm.getMaxHp();

        // Label "HP"
        font.setColor(Color.ORANGE);
        font.draw(batch, "HP", eBarX, eBarY + barH + 2);

        // Fundo da barra
        batch.setColor(Color.DARK_GRAY);
        batch.draw(game.getPixelWhite(), eBarX + 20, eBarY, barW, barH);
        // Barra preenchida
        batch.setColor(getHpColor(enemyHpRatio));
        batch.draw(game.getPixelWhite(), eBarX + 20, eBarY, barW * enemyHpRatio, barH);
        batch.setColor(Color.WHITE);

        // ========== INFO BOX DO JOGADOR (embaixo-direita) ==========
        float playerInfoX = sw - infoBoxW - 10;
        float playerInfoY = dialogH + battleAreaH * 0.12f;

        // Fundo da info box
        batch.setColor(0.95f, 0.93f, 0.85f, 1f);
        batch.draw(game.getPixelWhite(), playerInfoX, playerInfoY, infoBoxW, infoBoxH + 8);
        // Borda superior
        batch.setColor(0.3f, 0.3f, 0.3f, 1f);
        batch.draw(game.getPixelWhite(), playerInfoX, playerInfoY + infoBoxH + 6, infoBoxW, 2);
        batch.setColor(Color.WHITE);

        // Nome e HP do jogador
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

        // HP numérico do jogador
        font.setColor(Color.BLACK);
        font.draw(batch, playerPkm.getHp() + "/" + playerPkm.getMaxHp(), pBarX + 20, pBarY - 2);

        // ========== CAIXA DE DIÁLOGO (parte inferior) ==========
        batch.draw(dialogBox, 0, dialogY, sw, dialogH);

        // Margem interna do texto na caixa de diálogo
        float textX = 16;
        float textY = dialogH - 16;

        switch (uiState) {
            case CHOOSE_ACTION:
                drawAttackMenu(batch, font, textX, textY, sw);
                break;
            case CHOOSE_ITEM:
                drawItemMenu(batch, font, textX, textY);
                break;
            case PROCESSING:
                uiState = UIState.CHOOSE_ACTION;
                if (battleManager.isBattleOver()) uiState = UIState.BATTLE_OVER;
                break;
            case BATTLE_OVER:
                drawBattleOver(batch, font, textX, textY);
                break;
        }

        // ========== LOG DE BATALHA (no cenário, acima da caixa) ==========
        drawBattleLog(batch, font, 10, dialogH + battleAreaH * 0.42f);

        font.setColor(Color.WHITE);
        batch.end();
    }

    private void drawAttackMenu(SpriteBatch batch, BitmapFont font, float startX, float startY, float sw) {
        Pokemon p = battleManager.getPlayerPokemon();

        // Layout em 2 colunas estilo Pokémon clássico
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

        // Opção de Item
        int itemRow = attackNames.size() / 2;
        float itemX = (attackNames.size() % 2 == 0) ? col1X : col2X;
        float itemY = startY - itemRow * rowH;

        if (selectedIndex == attackNames.size()) {
            font.setColor(Color.BLACK);
            font.draw(batch, "> MOCHILA", itemX, itemY);
        } else {
            font.setColor(Color.DARK_GRAY);
            font.draw(batch, "  MOCHILA", itemX, itemY);
        }
    }

    private void drawItemMenu(SpriteBatch batch, BitmapFont font, float startX, float startY) {
        if (inventory.isEmpty()) {
            font.setColor(Color.DARK_GRAY);
            font.draw(batch, "Mochila vazia!", startX, startY);
            font.setColor(Color.BLACK);
            font.draw(batch, "[ESC] Voltar", startX, startY - 18);
        } else {
            for (int i = 0; i < inventory.getSize(); i++) {
                Item item = inventory.getItem(i);
                float y = startY - (i * 14);
                if (i == selectedIndex) {
                    font.setColor(Color.BLACK);
                    font.draw(batch, "> " + item.getName(), startX, y);
                } else {
                    font.setColor(Color.DARK_GRAY);
                    font.draw(batch, "  " + item.getName(), startX, y);
                }
            }
            font.setColor(Color.GRAY);
            font.draw(batch, "[ESC] Voltar", startX + 160, startY);
        }
    }

    private void drawBattleOver(SpriteBatch batch, BitmapFont font, float startX, float startY) {
        if (battleManager.isPlayerWinner()) {
            font.setColor(Color.BLACK);
            font.draw(batch, battleManager.getEnemyPokemon().getName() + " foi derrotado!", startX, startY);
        } else {
            font.setColor(Color.RED);
            font.draw(batch, playerBackSprite != null ?
                battleManager.getPlayerPokemon().getName() + " desmaiou!" :
                "Derrota!", startX, startY);
        }
        font.setColor(Color.DARK_GRAY);
        font.draw(batch, "[ENTER] Voltar à Dungeon", startX, startY - 20);
    }

    private Color getHpColor(float ratio) {
        if (ratio > 0.5f) return Color.GREEN;
        if (ratio > 0.2f) return Color.YELLOW;
        return Color.RED;
    }

    private void drawBattleLog(SpriteBatch batch, BitmapFont font, float x, float y) {
        Stack<String> history = battleManager.getBattleLog().getHistory();
        if (history.isEmpty()) return;

        int start = Math.max(0, history.size() - 2);
        font.setColor(Color.WHITE);
        for (int i = history.size() - 1; i >= start; i--) {
            font.draw(batch, history.get(i), x, y);
            y -= 12;
        }
    }

    // ========== INPUT ==========

    private void handleInput() {
        if (inputCooldown > 0) return;

        switch (uiState) {
            case CHOOSE_ACTION: handleActionInput(); break;
            case CHOOSE_ITEM: handleItemInput(); break;
            case BATTLE_OVER:
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    game.setScreen(dungeonScreen);
                }
                break;
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            selectedIndex = 0;
            uiState = UIState.CHOOSE_ITEM;
            inputCooldown = INPUT_DELAY;
            return;
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
                uiState = UIState.CHOOSE_ITEM;
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

    private void handleItemInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            selectedIndex = 0;
            uiState = UIState.CHOOSE_ACTION;
            inputCooldown = INPUT_DELAY;
            return;
        }

        if (inventory.isEmpty()) return;

        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            selectedIndex = (selectedIndex - 1 + inventory.getSize()) % inventory.getSize();
            inputCooldown = INPUT_DELAY;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            selectedIndex = (selectedIndex + 1) % inventory.getSize();
            inputCooldown = INPUT_DELAY;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            Item item = inventory.getItem(selectedIndex);
            if (item != null) {
                TurnAction useAction = TurnAction.useItem(battleManager.getPlayerPokemon(), item);
                inventory.removeItem(selectedIndex);
                battleManager.enqueuePlayerAction(useAction);
                battleManager.enqueueEnemyAction();
                battleManager.processTurn();
                selectedIndex = 0;
                uiState = UIState.PROCESSING;
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
        if (playerBackSprite != null) playerBackSprite.dispose();
        if (enemyFrontSprite != null) enemyFrontSprite.dispose();
        if (dialogBox != null) dialogBox.dispose();
    }
}
