package com.pokedungeon.game.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pokedungeon.game.Main;
import com.pokedungeon.game.dungeon.DungeonGraph;
import com.pokedungeon.game.dungeon.DungeonManager;
import com.pokedungeon.game.dungeon.Room;
import com.pokedungeon.game.inventory.Inventory;
import com.pokedungeon.game.model.Item;
import com.pokedungeon.game.model.Player;
import com.pokedungeon.game.model.Pokemon;
import com.pokedungeon.game.utils.GameConstants;

public class DungeonScreen implements Screen {

    private Main game;
    private Player player;
    private Inventory inventory;
    private DungeonManager dungeonManager;

    // Texturas visuais
    private Texture texFloor, texWall, texDoor, texChest, texPlayer;

    // Movimentação e mapa
    private float playerX, playerY;
    private final int TILE = 32;
    private final int MAP_COLS = 13; // 13 * 32 = 416 px
    private final int MAP_ROWS = 7;  // 7 * 32 = 224 px

    private String statusMessage = "";

    // Estrutura auxiliar para mapear portas na sala
    class DoorPoint {
        int col, row;
        Room target;
        DoorPoint(int c, int r, Room t) { this.col = c; this.row = r; this.target = t; }
    }
    private ArrayList<DoorPoint> currentDoors = new ArrayList<>();
    private int chestCol = -1, chestRow = -1;

    public DungeonScreen(Main game) {
        this.game = game;
        
        // Carrega assets
        texFloor = new Texture(Gdx.files.internal("tiles/floor.png"));
        texWall = new Texture(Gdx.files.internal("tiles/wall.png"));
        texDoor = new Texture(Gdx.files.internal("tiles/door.png"));
        texChest = new Texture(Gdx.files.internal("tiles/chest.png"));
        texPlayer = new Texture(Gdx.files.internal("sprites/personagem.PNG"));

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

        DungeonGraph graph = new DungeonGraph();

        Room entrance = new Room(0, "Entrada", "A entrada escura da dungeon.");
        Room corridor = new Room(1, "Corredor", "Um corredor longo e úmido.");
        Room treasure = new Room(2, "Sala do Tesouro", "Uma sala com um baú!");
        Room arena = new Room(3, "Arena", "Uma arena de batalha antiga.");
        Room bossRoom = new Room(4, "Sala do Chefe", "O covil do chefe!");

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
        statusMessage = "Explore a dungeon!";
        
        buildRoomMap();
    }

    // Direção de onde o jogador acabou de entrar na sala (-1 = primeira sala)
    // 0: Topo, 1: Direita, 2: Baixo, 3: Esquerda
    private int lastEntryDirection = -1;

    // Monta a sala baseada no Grafo (onde estão as conexões)
    private void buildRoomMap() {
        currentDoors.clear();
        chestCol = -1; chestRow = -1;
        
        Room current = dungeonManager.getCurrentRoom();
        ArrayList<Room> neighbors = dungeonManager.getAvailableRooms();
        
        // Posições possíveis das portas: 0=Topo, 1=Direita, 2=Baixo, 3=Esquerda
        int[][] positions = {
            {MAP_COLS / 2, MAP_ROWS - 1}, 
            {MAP_COLS - 1, MAP_ROWS / 2}, 
            {MAP_COLS / 2, 0},            
            {0, MAP_ROWS / 2}             
        };
        
        boolean[] usedPositions = new boolean[4];
        
        // Verifica se temos uma sala anterior no histórico
        Room prevRoom = null;
        if (dungeonManager.canGoBack()) {
            prevRoom = dungeonManager.getRoomHistory().peek();
        }
        
        // Se viemos de uma sala, a porta de "voltar" fica obrigatoriamente na direção por onde entramos
        if (prevRoom != null && lastEntryDirection != -1) {
            currentDoors.add(new DoorPoint(positions[lastEntryDirection][0], positions[lastEntryDirection][1], prevRoom));
            usedPositions[lastEntryDirection] = true;
        }
        
        // Distribui os outros caminhos nas portas que sobraram
        for (Room neighbor : neighbors) {
            if (neighbor.equals(prevRoom)) continue; // já colocamos a porta de voltar
            
            // Procura a primeira parede livre (Preferência: Topo > Direita > Baixo > Esq)
            for (int i = 0; i < 4; i++) {
                if (!usedPositions[i]) {
                    currentDoors.add(new DoorPoint(positions[i][0], positions[i][1], neighbor));
                    usedPositions[i] = true;
                    break;
                }
            }
        }
        
        if (current.hasItem()) {
            chestCol = MAP_COLS / 2;
            chestRow = MAP_ROWS / 2;
        }
        
        // Spawn do jogador de acordo com a porta que ele acabou de entrar
        if (lastEntryDirection == 0) { // Entrou pelo Topo
            playerX = positions[0][0] * TILE;
            playerY = (positions[0][1] - 1) * TILE;
        } else if (lastEntryDirection == 1) { // Entrou pela Direita
            playerX = (positions[1][0] - 1) * TILE;
            playerY = positions[1][1] * TILE;
        } else if (lastEntryDirection == 2) { // Entrou por Baixo
            playerX = positions[2][0] * TILE;
            playerY = (positions[2][1] + 1) * TILE;
        } else if (lastEntryDirection == 3) { // Entrou pela Esquerda
            playerX = (positions[3][0] + 1) * TILE;
            playerY = positions[3][1] * TILE;
        } else {
            // Primeira sala: nasce no meio
            playerX = (MAP_COLS / 2) * TILE;
            playerY = (MAP_ROWS / 2) * TILE - 20; 
        }
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        handleMovement(delta);
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            dispose();
            return;
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB) || Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            game.setScreen(new PartyMenuScreen(game, this, player, inventory));
            return;
        }

        ScreenUtils.clear(0, 0, 0, 1f);

        SpriteBatch batch = game.getBatch();
        BitmapFont font = game.getFont();
        Viewport viewport = game.getViewport();

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        
        // Offset para centralizar a sala de 416x224 na tela de 426x240
        float offsetX = (viewport.getWorldWidth() - (MAP_COLS * TILE)) / 2f;
        float offsetY = (viewport.getWorldHeight() - (MAP_ROWS * TILE)) / 2f;

        batch.begin();

        // ========== DESENHA O MAPA ==========
        for (int c = 0; c < MAP_COLS; c++) {
            for (int r = 0; r < MAP_ROWS; r++) {
                float px = offsetX + c * TILE;
                float py = offsetY + r * TILE;
                
                batch.draw(texFloor, px, py, TILE, TILE);
                
                if (isWall(c, r)) {
                    batch.draw(texWall, px, py, TILE, TILE);
                }
            }
        }
        
        for (DoorPoint d : currentDoors) {
            batch.draw(texDoor, offsetX + d.col * TILE, offsetY + d.row * TILE, TILE, TILE);
        }
        
        if (chestCol != -1 && chestRow != -1) {
            batch.draw(texChest, offsetX + chestCol * TILE, offsetY + chestRow * TILE, TILE, TILE);
        }
        
        batch.draw(texPlayer, offsetX + playerX, offsetY + playerY, TILE, TILE);

        // ========== DESENHA UI OVERLAY ==========
        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(game.getPixelWhite(), 0, viewport.getWorldHeight() - 30, viewport.getWorldWidth(), 30);
        batch.setColor(Color.WHITE);
        
        Room current = dungeonManager.getCurrentRoom();
        font.setColor(Color.YELLOW);
        font.draw(batch, current.getName(), 5, viewport.getWorldHeight() - 5);
        
        font.setColor(Color.CYAN);
        Pokemon active = player.getActivePokemon();
        String pName = (active != null) ? active.getName() : "Sem Pkm";
        font.draw(batch, "Pkm: " + pName, 120, viewport.getWorldHeight() - 5);
        
        font.setColor(Color.WHITE);
        font.draw(batch, "Itens: " + inventory.getSize(), 240, viewport.getWorldHeight() - 5);

        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(game.getPixelWhite(), 0, 0, viewport.getWorldWidth(), 20);
        batch.setColor(Color.WHITE);
        
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, statusMessage, 5, 14);

        font.setColor(Color.GRAY);
        font.draw(batch, "[TAB] Menu | [ESC] Sair", viewport.getWorldWidth() - 165, 14);

        batch.end();
    }

    private void handleMovement(float delta) {
        float nextX = playerX;
        float nextY = playerY;
        float speed = GameConstants.PLAYER_SPEED;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) nextX -= speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) nextX += speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) nextY -= speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) nextY += speed * delta;

        Rectangle pRect = new Rectangle(nextX + 6, nextY + 6, TILE - 12, TILE - 12);
        
        boolean canMove = true;
        
        int startCol = (int) (pRect.x / TILE);
        int endCol = (int) ((pRect.x + pRect.width) / TILE);
        int startRow = (int) (pRect.y / TILE);
        int endRow = (int) ((pRect.y + pRect.height) / TILE);

        for (int c = startCol; c <= endCol; c++) {
            for (int r = startRow; r <= endRow; r++) {
                if (isWall(c, r)) {
                    canMove = false;
                }
            }
        }

        if (canMove) {
            playerX = nextX;
            playerY = nextY;
        }

        int pCol = (int) ((playerX + TILE/2) / TILE);
        int pRow = (int) ((playerY + TILE/2) / TILE);

        // Verifica portas
        for (DoorPoint d : currentDoors) {
            if (pCol == d.col && pRow == d.row) {
                // Descobre a direção geométrica desta porta
                int dirUsed = -1;
                if (d.row == MAP_ROWS - 1) dirUsed = 0; // Saiu por Cima
                else if (d.col == MAP_COLS - 1) dirUsed = 1; // Saiu pela Direita
                else if (d.row == 0) dirUsed = 2; // Saiu por Baixo
                else if (d.col == 0) dirUsed = 3; // Saiu pela Esquerda
                
                enterRoom(d.target, dirUsed);
                return;
            }
        }

        if (pCol == chestCol && pRow == chestRow && dungeonManager.getCurrentRoom().hasItem()) {
            Item collected = dungeonManager.getCurrentRoom().collectItem();
            if (inventory.addItem(collected)) {
                statusMessage = "Pegou: " + collected.getName() + "!";
                chestCol = -1;
            } else {
                statusMessage = "Mochila cheia!";
            }
        }
    }
    
    private boolean isWall(int c, int r) {
        if (c < 0 || c >= MAP_COLS || r < 0 || r >= MAP_ROWS) return true;
        if (c == 0 || c == MAP_COLS - 1 || r == 0 || r == MAP_ROWS - 1) {
            for (DoorPoint d : currentDoors) {
                if (d.col == c && d.row == r) return false;
            }
            return true;
        }
        return false;
    }

    private void enterRoom(Room target, int directionUsed) {
        Room prevRoom = dungeonManager.canGoBack() ? dungeonManager.getRoomHistory().peek() : null;
        
        // Se a porta leva para a sala anterior, faz o pop na Stack (Backtrack)
        if (target.equals(prevRoom)) {
            dungeonManager.goBack();
        } else {
            // Se for uma sala nova, empurra pro Stack (MoveTo)
            dungeonManager.moveTo(target);
        }
        
        // Calcula onde a porta de retorno deve aparecer na nova sala
        if (directionUsed == 0) lastEntryDirection = 2;      // Saiu por Cima -> Entrou por Baixo na próxima
        else if (directionUsed == 1) lastEntryDirection = 3; // Saiu Direita -> Entrou Esquerda
        else if (directionUsed == 2) lastEntryDirection = 0; // Saiu Baixo -> Entrou Cima
        else if (directionUsed == 3) lastEntryDirection = 1; // Saiu Esquerda -> Entrou Direita
        
        buildRoomMap(); 
        
        if (target.hasEnemy()) {
            Pokemon active = player.getActivePokemon();
            if (active != null) {
                game.setScreen(new BattleScreen(game, this, active, target.getEnemy(), inventory));
                return;
            } else {
                statusMessage = "Seu time desmaiou. Fuja!";
                return;
            }
        }
        statusMessage = "Entrou em: " + target.getName();
    }

    @Override public void resize(int w, int h) { game.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        texFloor.dispose();
        texWall.dispose();
        texDoor.dispose();
        texChest.dispose();
        texPlayer.dispose();
    }
}
