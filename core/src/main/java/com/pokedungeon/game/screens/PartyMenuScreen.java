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
import com.pokedungeon.game.Main;
import com.pokedungeon.game.inventory.Inventory;
import com.pokedungeon.game.model.Item;
import com.pokedungeon.game.model.Player;
import com.pokedungeon.game.model.Pokemon;

public class PartyMenuScreen implements Screen {

    private Main game;
    private DungeonScreen dungeonScreen;
    private Player player;
    private Inventory inventory;

    private Texture dialogBox;

    private enum MenuState {
        TAB_POKEMON,    // Navegando no time (ENTER define o ativo)
        TAB_INVENTORY,  // Navegando na mochila (ENTER seleciona o item)
        SELECT_TARGET   // Escolhendo em qual Pokémon usar o item selecionado
    }

    private MenuState state;
    private int selectedIndex = 0;
    private Item selectedItem = null;

    private float inputCooldown = 0.2f;

    public PartyMenuScreen(Main game, DungeonScreen dungeonScreen, Player player, Inventory inventory) {
        this.game = game;
        this.dungeonScreen = dungeonScreen;
        this.player = player;
        this.inventory = inventory;
        this.state = MenuState.TAB_POKEMON;

        dialogBox = new Texture(Gdx.files.internal("ui/barratexto.PNG"));
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        inputCooldown -= delta;
        handleInput();

        // Fundo azul escuro clássico de menu
        ScreenUtils.clear(0.1f, 0.2f, 0.4f, 1f);

        SpriteBatch batch = game.getBatch();
        BitmapFont font = game.getFont();
        Viewport viewport = game.getViewport();

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        float sw = viewport.getWorldWidth();
        float sh = viewport.getWorldHeight();

        batch.begin();

        // Título
        font.setColor(Color.WHITE);
        font.draw(batch, "Menu da Equipe e Mochila", 10, sh - 10);
        font.draw(batch, "[TAB] ou [ESC] para voltar à Dungeon", 10, 20);

        // --- COLUNA ESQUERDA: TIME POKÉMON ---
        float pkmX = 20;
        float pkmY = sh - 50;

        font.setColor(state == MenuState.TAB_POKEMON || state == MenuState.SELECT_TARGET ? Color.YELLOW : Color.LIGHT_GRAY);
        font.draw(batch, "TIME POKÉMON", pkmX, pkmY);
        pkmY -= 20;

        for (int i = 0; i < player.getTeam().size(); i++) {
            Pokemon p = player.getTeam().get(i);
            
            // Cursor
            if ((state == MenuState.TAB_POKEMON || state == MenuState.SELECT_TARGET) && selectedIndex == i) {
                font.setColor(Color.YELLOW);
                font.draw(batch, ">", pkmX, pkmY);
            }

            // Nome e status
            font.setColor(p.isFainted() ? Color.RED : Color.WHITE);
            String label = p.getName() + " HP: " + p.getHp() + "/" + p.getMaxHp();
            
            // Marca o pokémon ativo
            if (p == player.getActivePokemon()) {
                label += " [ATIVO]";
                font.setColor(Color.CYAN);
            }

            font.draw(batch, label, pkmX + 15, pkmY);

            // Barrinha de HP visual
            float barW = 100;
            float barH = 4;
            float hpRatio = (float) p.getHp() / p.getMaxHp();
            
            batch.setColor(Color.DARK_GRAY);
            batch.draw(game.getPixelWhite(), pkmX + 15, pkmY - 10, barW, barH);
            batch.setColor(hpRatio > 0.5f ? Color.GREEN : hpRatio > 0.2f ? Color.YELLOW : Color.RED);
            batch.draw(game.getPixelWhite(), pkmX + 15, pkmY - 10, barW * hpRatio, barH);
            batch.setColor(Color.WHITE);

            pkmY -= 30;
        }

        // --- COLUNA DIREITA: MOCHILA ---
        float invX = sw / 2 + 20;
        float invY = sh - 50;

        font.setColor(state == MenuState.TAB_INVENTORY ? Color.YELLOW : Color.LIGHT_GRAY);
        font.draw(batch, "MOCHILA", invX, invY);
        invY -= 20;

        if (inventory.isEmpty()) {
            font.setColor(Color.GRAY);
            font.draw(batch, "(Vazia)", invX + 15, invY);
        } else {
            for (int i = 0; i < inventory.getSize(); i++) {
                Item item = inventory.getItem(i);
                
                if (state == MenuState.TAB_INVENTORY && selectedIndex == i) {
                    font.setColor(Color.YELLOW);
                    font.draw(batch, ">", invX, invY);
                }

                font.setColor(Color.WHITE);
                font.draw(batch, item.getName() + " (Cura " + item.getHealAmount() + " HP)", invX + 15, invY);
                invY -= 20;
            }
        }

        // --- MENSAGEM DE AÇÃO ---
        float msgY = 60;
        font.setColor(Color.CYAN);
        if (state == MenuState.TAB_POKEMON) {
            font.draw(batch, "Pressione [ENTER] para definir o Pokémon como Ativo.", 10, msgY);
        } else if (state == MenuState.TAB_INVENTORY) {
            font.draw(batch, "Pressione [ENTER] para usar o item.", 10, msgY);
        } else if (state == MenuState.SELECT_TARGET) {
            font.setColor(Color.GREEN);
            font.draw(batch, "Usando " + selectedItem.getName() + "...\nSelecione em qual Pokémon usar [ENTER]", 10, msgY);
        }

        batch.end();
    }

    private void handleInput() {
        if (inputCooldown > 0) return;

        // Voltar para Dungeon
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.TAB) || Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            game.setScreen(dungeonScreen);
            return;
        }

        // Alternar entre abas
        if (state == MenuState.TAB_POKEMON || state == MenuState.TAB_INVENTORY) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
                if (state == MenuState.TAB_POKEMON && !inventory.isEmpty()) {
                    state = MenuState.TAB_INVENTORY;
                    selectedIndex = 0;
                    inputCooldown = 0.2f;
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
                if (state == MenuState.TAB_INVENTORY) {
                    state = MenuState.TAB_POKEMON;
                    selectedIndex = 0;
                    inputCooldown = 0.2f;
                }
            }
        }

        int maxIndex = 0;
        if (state == MenuState.TAB_POKEMON || state == MenuState.SELECT_TARGET) {
            maxIndex = player.getTeam().size();
        } else if (state == MenuState.TAB_INVENTORY) {
            maxIndex = inventory.getSize();
        }

        if (maxIndex > 0) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                selectedIndex = (selectedIndex + 1) % maxIndex;
                inputCooldown = 0.2f;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
                selectedIndex = (selectedIndex - 1 + maxIndex) % maxIndex;
                inputCooldown = 0.2f;
            }
        }

        // Ação principal
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (state == MenuState.TAB_POKEMON) {
                // Define ativo
                Pokemon selected = player.getTeam().get(selectedIndex);
                if (!selected.isFainted()) {
                    // Traz para a primeira posição (index 0) para ser o ativo principal
                    player.getTeam().remove(selected);
                    player.getTeam().add(0, selected);
                }
            } else if (state == MenuState.TAB_INVENTORY) {
                if (!inventory.isEmpty()) {
                    selectedItem = inventory.getItem(selectedIndex);
                    state = MenuState.SELECT_TARGET;
                    selectedIndex = 0;
                }
            } else if (state == MenuState.SELECT_TARGET) {
                Pokemon target = player.getTeam().get(selectedIndex);
                if (!target.isFainted() && target.getHp() < target.getMaxHp()) {
                    target.heal(selectedItem.getHealAmount());
                    inventory.removeItem(selectedItem); // Remove item usado
                    
                    state = MenuState.TAB_POKEMON; // Volta pra tela de time
                    selectedIndex = 0;
                }
            }
            inputCooldown = 0.2f;
        }
    }

    @Override public void resize(int w, int h) { game.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { dialogBox.dispose(); }
}
