package com.pokedungeon.game.screens;

import java.util.ArrayList;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.pokedungeon.game.Main;
import com.pokedungeon.game.battle.BattleManager;
import com.pokedungeon.game.battle.TurnAction;
import com.pokedungeon.game.inventory.Inventory;
import com.pokedungeon.game.model.Item;
import com.pokedungeon.game.model.Pokemon;

/**
 * Tela de batalha por turnos.
 *
 * Estruturas de dados utilizadas:
 * - Queue (BattleManager.turnQueue): fila de ações FIFO
 * - Stack (BattleLog.actionHistory): histórico de ações LIFO
 * - HashMap (Pokemon.attacks): ataques disponíveis
 * - ArrayList (Inventory.items): itens para usar
 *
 * Controles:
 * - [1-4] Selecionar ataque
 * - [I] Abrir inventário / usar item
 * - [ENTER] Continuar após fim da batalha
 */
public class BattleScreen implements Screen {

    private Main game;
    private DungeonScreen dungeonScreen;
    private ShapeRenderer shapeRenderer;

    private BattleManager battleManager;
    private Inventory inventory;

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
        this.shapeRenderer = new ShapeRenderer();
        this.inventory = inventory;

        this.battleManager = new BattleManager(playerPokemon, enemyPokemon);

        // Converte as chaves do HashMap de ataques em ArrayList para indexar
        this.attackNames = new ArrayList<>(playerPokemon.getAttacks().keySet());
        this.uiState = UIState.CHOOSE_ACTION;
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        inputCooldown -= delta;
        handleInput();

        ScreenUtils.clear(0.05f, 0.05f, 0.1f, 1f);

        SpriteBatch batch = game.getBatch();
        BitmapFont font = game.getFont();
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        Pokemon player = battleManager.getPlayerPokemon();
        Pokemon enemy = battleManager.getEnemyPokemon();

        // --- Barras de HP (ShapeRenderer) ---
        drawHpBars(sw, sh, player, enemy);

        batch.begin();

        // --- Pokémon inimigo (topo) ---
        font.getData().setScale(1.4f);
        font.setColor(Color.RED);
        font.draw(batch, enemy.getName() + " [" + enemy.getType() + "]", 20, sh - 20);
        font.getData().setScale(1.1f);
        font.setColor(Color.WHITE);
        font.draw(batch, "HP: " + enemy.getHp() + "/" + enemy.getMaxHp(), 20, sh - 50);

        // --- Pokémon do jogador (meio) ---
        font.getData().setScale(1.4f);
        font.setColor(Color.CYAN);
        font.draw(batch, player.getName() + " [" + player.getType() + "]", 20, sh - 120);
        font.getData().setScale(1.1f);
        font.setColor(Color.WHITE);
        font.draw(batch, "HP: " + player.getHp() + "/" + player.getMaxHp(), 20, sh - 150);

        // --- Área de ação (parte inferior) ---
        float actionY = sh - 200;

        switch (uiState) {
            case CHOOSE_ACTION:
                drawAttackMenu(batch, font, actionY);
                break;
            case CHOOSE_ITEM:
                drawItemMenu(batch, font, actionY);
                break;
            case PROCESSING:
                // Turno sendo processado, mostrar resultado
                uiState = UIState.CHOOSE_ACTION;
                if (battleManager.isBattleOver()) {
                    uiState = UIState.BATTLE_OVER;
                }
                break;
            case BATTLE_OVER:
                drawBattleOver(batch, font, actionY);
                break;
        }

        // --- Log de batalha (últimas 5 ações da Stack) ---
        drawBattleLog(batch, font);

        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        batch.end();
    }

    /**
     * Desenha o menu de ataques.
     */
    private void drawAttackMenu(SpriteBatch batch, BitmapFont font, float startY) {
        font.getData().setScale(1.2f);
        font.setColor(Color.YELLOW);
        font.draw(batch, "Escolha um ataque:", 20, startY);

        font.getData().setScale(1.1f);
        Pokemon p = battleManager.getPlayerPokemon();
        for (int i = 0; i < attackNames.size(); i++) {
            String name = attackNames.get(i);
            int dmg = p.getAttackDamage(name);
            if (i == selectedIndex) {
                font.setColor(Color.YELLOW);
                font.draw(batch, "> " + (i + 1) + ". " + name + " (Dano: " + dmg + ")",
                    30, startY - 30 - (i * 25));
            } else {
                font.setColor(Color.WHITE);
                font.draw(batch, "  " + (i + 1) + ". " + name + " (Dano: " + dmg + ")",
                    30, startY - 30 - (i * 25));
            }
        }

        // Opção de item
        float itemY = startY - 30 - (attackNames.size() * 25);
        if (selectedIndex == attackNames.size()) {
            font.setColor(Color.GREEN);
            font.draw(batch, "> [I] Usar Item", 30, itemY);
        } else {
            font.setColor(Color.GREEN);
            font.draw(batch, "  [I] Usar Item", 30, itemY);
        }
    }

    /**
     * Desenha o menu de itens do inventário.
     */
    private void drawItemMenu(SpriteBatch batch, BitmapFont font, float startY) {
        font.getData().setScale(1.2f);
        font.setColor(Color.GREEN);
        font.draw(batch, "Inventário - Escolha um item:", 20, startY);

        font.getData().setScale(1.1f);
        if (inventory.isEmpty()) {
            font.setColor(Color.GRAY);
            font.draw(batch, "Inventário vazio!", 30, startY - 30);
            font.setColor(Color.YELLOW);
            font.draw(batch, "[ESC] Voltar", 30, startY - 60);
        } else {
            for (int i = 0; i < inventory.getSize(); i++) {
                Item item = inventory.getItem(i);
                if (i == selectedIndex) {
                    font.setColor(Color.YELLOW);
                    font.draw(batch, "> " + (i + 1) + ". " + item, 30, startY - 30 - (i * 25));
                } else {
                    font.setColor(Color.WHITE);
                    font.draw(batch, "  " + (i + 1) + ". " + item, 30, startY - 30 - (i * 25));
                }
            }
            font.setColor(Color.GRAY);
            font.draw(batch, "[ESC] Voltar aos ataques", 30,
                startY - 30 - (inventory.getSize() * 25) - 10);
        }
    }

    /**
     * Desenha a tela de fim de batalha.
     */
    private void drawBattleOver(SpriteBatch batch, BitmapFont font, float startY) {
        font.getData().setScale(1.5f);
        if (battleManager.isPlayerWinner()) {
            font.setColor(Color.GREEN);
            font.draw(batch, "VITÓRIA!", 20, startY);
        } else {
            font.setColor(Color.RED);
            font.draw(batch, "DERROTA!", 20, startY);
        }
        font.getData().setScale(1.1f);
        font.setColor(Color.YELLOW);
        font.draw(batch, "[ENTER] Voltar à Dungeon", 20, startY - 40);
    }

    /**
     * Desenha as barras de HP como retângulos coloridos.
     */
    private void drawHpBars(float sw, float sh, Pokemon player, Pokemon enemy) {
        float barWidth = 200;
        float barHeight = 14;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Barra do inimigo
        float enemyHpRatio = (float) enemy.getHp() / enemy.getMaxHp();
        float enemyBarX = sw - barWidth - 30;
        float enemyBarY = sh - 50;
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(enemyBarX, enemyBarY, barWidth, barHeight);
        shapeRenderer.setColor(getHpColor(enemyHpRatio));
        shapeRenderer.rect(enemyBarX, enemyBarY, barWidth * enemyHpRatio, barHeight);

        // Barra do jogador
        float playerHpRatio = (float) player.getHp() / player.getMaxHp();
        float playerBarX = sw - barWidth - 30;
        float playerBarY = sh - 150;
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(playerBarX, playerBarY, barWidth, barHeight);
        shapeRenderer.setColor(getHpColor(playerHpRatio));
        shapeRenderer.rect(playerBarX, playerBarY, barWidth * playerHpRatio, barHeight);

        shapeRenderer.end();
    }

    /**
     * Retorna cor da barra de HP baseada na porcentagem.
     */
    private Color getHpColor(float ratio) {
        if (ratio > 0.5f) return Color.GREEN;
        if (ratio > 0.2f) return Color.YELLOW;
        return Color.RED;
    }

    /**
     * Desenha as últimas mensagens do log de batalha (Stack).
     */
    private void drawBattleLog(SpriteBatch batch, BitmapFont font) {
        Stack<String> history = battleManager.getBattleLog().getHistory();
        font.getData().setScale(0.9f);
        font.setColor(Color.LIGHT_GRAY);

        float logY = 95;
        font.setColor(Color.GRAY);
        font.draw(batch, "--- Log ---", 20, logY);
        logY -= 20;

        // Mostra as últimas 3 ações
        int start = Math.max(0, history.size() - 3);
        font.setColor(Color.LIGHT_GRAY);
        for (int i = history.size() - 1; i >= start; i--) {
            font.draw(batch, history.get(i), 20, logY);
            logY -= 18;
        }
    }

    private void handleInput() {
        if (inputCooldown > 0) return;

        switch (uiState) {
            case CHOOSE_ACTION:
                handleActionInput();
                break;
            case CHOOSE_ITEM:
                handleItemInput();
                break;
            case BATTLE_OVER:
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    game.setScreen(dungeonScreen);
                }
                break;
            default:
                break;
        }
    }

    private void handleActionInput() {
        int maxIndex = attackNames.size(); // ataques + opção de item

        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            selectedIndex = (selectedIndex - 1 + maxIndex + 1) % (maxIndex + 1);
            inputCooldown = INPUT_DELAY;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            selectedIndex = (selectedIndex + 1) % (maxIndex + 1);
            inputCooldown = INPUT_DELAY;
        }

        // Tecla I abre inventário
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            selectedIndex = 0;
            uiState = UIState.CHOOSE_ITEM;
            inputCooldown = INPUT_DELAY;
            return;
        }

        // ENTER confirma seleção
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (selectedIndex < attackNames.size()) {
                // Ataque selecionado
                String attackName = attackNames.get(selectedIndex);
                TurnAction playerAction = TurnAction.attack(
                    battleManager.getPlayerPokemon(),
                    battleManager.getEnemyPokemon(),
                    attackName);

                battleManager.enqueuePlayerAction(playerAction);
                battleManager.enqueueEnemyAction();
                battleManager.processTurn();
                uiState = UIState.PROCESSING;
            } else {
                // Ir para inventário
                selectedIndex = 0;
                uiState = UIState.CHOOSE_ITEM;
            }
            inputCooldown = INPUT_DELAY;
        }

        // Atalhos numéricos para ataques
        for (int i = 0; i < attackNames.size() && i < 4; i++) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
                String attackName = attackNames.get(i);
                TurnAction playerAction = TurnAction.attack(
                    battleManager.getPlayerPokemon(),
                    battleManager.getEnemyPokemon(),
                    attackName);

                battleManager.enqueuePlayerAction(playerAction);
                battleManager.enqueueEnemyAction();
                battleManager.processTurn();
                uiState = UIState.PROCESSING;
                inputCooldown = INPUT_DELAY;
                return;
            }
        }
    }

    private void handleItemInput() {
        // ESC volta para ataques
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
                TurnAction useAction = TurnAction.useItem(
                    battleManager.getPlayerPokemon(), item);
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

    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
