package com.pokedungeon.game.screens;

import java.util.ArrayList;
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
import com.pokedungeon.game.dungeon.DungeonGraph;
import com.pokedungeon.game.dungeon.DungeonManager;
import com.pokedungeon.game.dungeon.Room;
import com.pokedungeon.game.inventory.Inventory;
import com.pokedungeon.game.model.Item;
import com.pokedungeon.game.model.Player;
import com.pokedungeon.game.model.Pokemon;
import com.pokedungeon.game.utils.GameConstants;

/**
 * Tela de exploração da dungeon.
 * Estruturas: Grafo, Stack, ArrayList.
 */
public class DungeonScreen implements Screen {

    private Main game;
    private ShapeRenderer shapeRenderer;
    private GlyphLayout layout;
    private Player player;
    private Inventory inventory;
    private DungeonManager dungeonManager;
    private String statusMessage = "";
    private float inputCooldown = 0f;
    private static final float INPUT_DELAY = 0.25f;

    public DungeonScreen(Main game) {
        this.game = game;
        this.shapeRenderer = new ShapeRenderer();
        this.layout = new GlyphLayout();
        initializeGame();
    }

    private void initializeGame() {
        player = new Player("Red");

        Pokemon charmander = new Pokemon("Charmander", "Fogo", 80);
        charmander.addAttack("Lança-Chamas", 25);
        charmander.addAttack("Arranhão", 15);

        Pokemon squirtle = new Pokemon("Squirtle", "Água", 90);
        squirtle.addAttack("Jato d'Água", 22);
        squirtle.addAttack("Investida", 14);

        player.addPokemon(charmander);
        player.addPokemon(squirtle);

        inventory = new Inventory();
        inventory.addItem(new Item("Poção", GameConstants.DEFAULT_POTION_HEAL));
        inventory.addItem(new Item("Super Poção", 40));

        DungeonGraph graph = new DungeonGraph();

        Room entrance = new Room(0, "Entrada", "A entrada escura da dungeon.");
        Room corridor = new Room(1, "Corredor", "Um corredor longo e úmido.");
        Room treasure = new Room(2, "Sala do Tesouro", "Uma sala com um baú!");
        Room arena = new Room(3, "Arena", "Uma arena de batalha antiga.");
        Room bossRoom = new Room(4, "Sala do Chefe", "O covil do pokémon mais forte!");

        treasure.setItem(new Item("Poção Máxima", 60));

        Pokemon rattata = new Pokemon("Rattata", "Normal", 40);
        rattata.addAttack("Investida", 12);
        corridor.setEnemy(rattata);

        Pokemon zubat = new Pokemon("Zubat", "Voador", 50);
        zubat.addAttack("Mordida", 15);
        arena.setEnemy(zubat);

        Pokemon onix = new Pokemon("Onix", "Pedra", 100);
        onix.addAttack("Pedrada", 28);
        bossRoom.setEnemy(onix);

        graph.connect(entrance, corridor);
        graph.connect(entrance, treasure);
        graph.connect(corridor, arena);
        graph.connect(arena, bossRoom);

        dungeonManager = new DungeonManager(graph, entrance);
        statusMessage = "Bem-vindo à dungeon! Escolha uma sala.";
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        inputCooldown -= delta;
        handleInput();
        ScreenUtils.clear(0.08f, 0.08f, 0.12f, 1f);

        SpriteBatch batch = game.getBatch();
        BitmapFont font = game.getFont();
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        batch.begin();

        Room current = dungeonManager.getCurrentRoom();
        font.getData().setScale(1.6f);
        font.setColor(Color.YELLOW);
        font.draw(batch, current.getName(), 20, sh - 15);

        font.getData().setScale(1f);
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, current.getDescription(), 20, sh - 45);

        font.getData().setScale(1.2f);
        font.setColor(Color.WHITE);
        font.draw(batch, "Salas acessíveis:", 20, sh - 85);

        ArrayList<Room> neighbors = dungeonManager.getAvailableRooms();
        font.getData().setScale(1.1f);
        for (int i = 0; i < neighbors.size(); i++) {
            Room r = neighbors.get(i);
            String label = (i + 1) + ". " + r.getName();
            if (r.isVisited()) label += " (visitada)";
            if (r.hasEnemy()) { font.setColor(Color.RED); label += " [INIMIGO]"; }
            else if (r.hasItem()) { font.setColor(Color.GREEN); label += " [ITEM]"; }
            else { font.setColor(Color.WHITE); }
            font.draw(batch, label, 30, sh - 110 - (i * 25));
        }

        float infoY = 170;
        font.getData().setScale(1.1f);
        font.setColor(Color.CYAN);
        font.draw(batch, "--- Time ---", 20, infoY);
        infoY -= 25;
        font.setColor(Color.WHITE);
        for (Pokemon p : player.getTeam()) {
            String s = p.isFainted() ? " [DERROTADO]" : "";
            font.draw(batch, p.getName() + " HP:" + p.getHp() + "/" + p.getMaxHp() + s, 30, infoY);
            infoY -= 22;
        }

        infoY -= 10;
        font.setColor(Color.CYAN);
        font.draw(batch, "--- Inventário (" + inventory.getSize() + ") ---", 20, infoY);
        infoY -= 25;
        font.setColor(Color.WHITE);
        for (int i = 0; i < inventory.getSize(); i++) {
            font.draw(batch, "- " + inventory.getItem(i), 30, infoY);
            infoY -= 22;
        }

        font.getData().setScale(1.1f);
        font.setColor(Color.YELLOW);
        font.draw(batch, statusMessage, 20, 30);

        font.getData().setScale(0.85f);
        font.setColor(Color.GRAY);
        font.draw(batch, "[1-4] Ir para sala | [B] Voltar | [ESC] Menu", sw - 380, 25);

        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        batch.end();
    }

    private void handleInput() {
        if (inputCooldown > 0) return;
        ArrayList<Room> neighbors = dungeonManager.getAvailableRooms();
        for (int i = 0; i < neighbors.size() && i < 9; i++) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
                enterRoom(neighbors.get(i));
                inputCooldown = INPUT_DELAY;
                return;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            if (dungeonManager.goBack()) {
                statusMessage = "Voltou para: " + dungeonManager.getCurrentRoom().getName();
            } else {
                statusMessage = "Não há sala anterior!";
            }
            inputCooldown = INPUT_DELAY;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            dispose();
        }
    }

    private void enterRoom(Room target) {
        dungeonManager.moveTo(target);
        if (target.hasItem()) {
            Item collected = target.collectItem();
            if (inventory.addItem(collected)) {
                statusMessage = "Encontrou: " + collected.getName() + "!";
            } else {
                statusMessage = "Inventário cheio!";
            }
            return;
        }
        if (target.hasEnemy()) {
            Pokemon active = player.getActivePokemon();
            if (active != null) {
                game.setScreen(new BattleScreen(game, this, active, target.getEnemy(), inventory));
                return;
            } else {
                statusMessage = "Todos os pokémons derrotados!";
                return;
            }
        }
        statusMessage = "Entrou em: " + target.getName();
    }

    public Player getPlayer() { return player; }
    public Inventory getInventory() { return inventory; }

    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { shapeRenderer.dispose(); }
}
