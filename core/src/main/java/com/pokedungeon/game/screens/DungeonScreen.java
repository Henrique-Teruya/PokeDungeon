package com.pokedungeon.game.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
    private Texture texFloor, texWall, texDoor, texChest, texWater, texGrass;
    private Texture texWaterN, texWaterS, texWaterE, texWaterW, texWaterNW, texWaterNE, texWaterSW, texWaterSE, texWaterCenter;
    
    // Sprites do jogador por direção
    private Texture texPlayerDown, texPlayerLeft, texPlayerRight, texPlayerUp;
    private Texture texPlayerDownWalk1, texPlayerDownWalk2;
    private Texture texPlayerLeftWalk1, texPlayerLeftWalk2;
    private Texture texPlayerRightWalk1, texPlayerRightWalk2;
    private Texture texPlayerUpWalk1, texPlayerUpWalk2;
    private int currentDirection = 0; // 0=Baixo, 1=Esquerda, 2=Direita, 3=Cima
    private float stateTime = 0f;
    private boolean isMoving = false;

    // Movimentação e mapa
    private float playerX, playerY;
    private final int TILE = 32;
    private final int MAP_COLS = 13; // 13 * 32 = 416 px
    private final int MAP_ROWS = 7;  // 7 * 32 = 224 px

    private boolean[][] waterMap = new boolean[MAP_COLS][MAP_ROWS];

    private String statusMessage = "";

    // Sprites dos pokémons do time para a sidebar
    private HashMap<String, Texture> pokemonSprites;

    // Modo de seleção de time (TAB)
    private boolean selectingTeam = false;
    private int teamCursorIndex = 0;
    private int firstSwapIndex = -1;
    private float selectionCooldown = 0f;

    // Menu de pausa (ESC)
    private boolean paused = false;
    private int pauseCursor = 0;

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
        texWater = new Texture(Gdx.files.internal("tiles/water.png"));
        texGrass = new Texture(Gdx.files.internal("tiles/grass.png"));

        // Texturas de lago (Autotiling)
        texWaterCenter = new Texture(Gdx.files.internal("tiles/water_center.png"));
        texWaterN = new Texture(Gdx.files.internal("tiles/water_n.png"));
        texWaterS = new Texture(Gdx.files.internal("tiles/water_s.png"));
        texWaterE = new Texture(Gdx.files.internal("tiles/water_e.png"));
        texWaterW = new Texture(Gdx.files.internal("tiles/water_w.png"));
        texWaterNW = new Texture(Gdx.files.internal("tiles/water_nw.png"));
        texWaterNE = new Texture(Gdx.files.internal("tiles/water_ne.png"));
        texWaterSW = new Texture(Gdx.files.internal("tiles/water_sw.png"));
        texWaterSE = new Texture(Gdx.files.internal("tiles/water_se.png"));

        // Filtro Nearest para todos os tiles
        Texture[] allTiles = {
            texFloor, texWall, texDoor, texChest, texWater, texGrass,
            texWaterCenter, texWaterN, texWaterS, texWaterE, texWaterW,
            texWaterNW, texWaterNE, texWaterSW, texWaterSE
        };
        for (Texture t : allTiles) {
            t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

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

        pokemonSprites = new HashMap<>();
        for (Pokemon p : player.getTeam()) {
            String path = "sprites/" + p.getName().toLowerCase() + ".PNG";
            if (Gdx.files.internal(path).exists()) {
                Texture tex = new Texture(Gdx.files.internal(path));
                tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                pokemonSprites.put(p.getName(), tex);
            }
        }

        inventory = new Inventory();
        inventory.addItem(new Item("Poção", GameConstants.DEFAULT_POTION_HEAL));

        DungeonGraph graph = new DungeonGraph();

        Room entrance = new Room(0, "Entrada", "A entrada escura da dungeon.");
        entrance.setMapX(0); entrance.setMapY(0);
        
        Room corridor = new Room(1, "Corredor", "Um corredor longo e úmido.");
        corridor.setMapX(0); corridor.setMapY(1);
        
        Room treasure = new Room(2, "Sala do Tesouro", "Uma sala com um baú!");
        treasure.setMapX(1); treasure.setMapY(0);
        
        Room arena = new Room(3, "Arena", "Uma arena de batalha antiga.");
        arena.setMapX(0); arena.setMapY(2);
        
        Room bossRoom = new Room(4, "Sala do Chefe", "O covil do chefe!");
        bossRoom.setMapX(0); bossRoom.setMapY(3);

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
        
        // Carrega sprites individuais do jogador por direção
        texPlayerDown = new Texture(Gdx.files.internal("sprites/player/player_down.png"));
        texPlayerLeft = new Texture(Gdx.files.internal("sprites/player/player_left.png"));
        texPlayerRight = new Texture(Gdx.files.internal("sprites/player/player_right.png"));
        texPlayerUp = new Texture(Gdx.files.internal("sprites/player/player_up.png"));
        
        texPlayerDownWalk1 = new Texture(Gdx.files.internal("sprites/player/player_down_walk1.png"));
        texPlayerDownWalk2 = new Texture(Gdx.files.internal("sprites/player/player_down_walk2.png"));
        texPlayerLeftWalk1 = new Texture(Gdx.files.internal("sprites/player/player_left_walk1.png"));
        texPlayerLeftWalk2 = new Texture(Gdx.files.internal("sprites/player/player_left_walk2.png"));
        texPlayerRightWalk1 = new Texture(Gdx.files.internal("sprites/player/player_right_walk1.png"));
        texPlayerRightWalk2 = new Texture(Gdx.files.internal("sprites/player/player_right_walk2.png"));
        texPlayerUpWalk1 = new Texture(Gdx.files.internal("sprites/player/player_up_walk1.png"));
        texPlayerUpWalk2 = new Texture(Gdx.files.internal("sprites/player/player_up_walk2.png"));
        
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

        // Generate water map for this room
        for(int c=0; c<MAP_COLS; c++) {
            for(int r=0; r<MAP_ROWS; r++) {
                waterMap[c][r] = false;
            }
        }
        
        Random rand = new Random(current.getId() * 1000L);
        // Tentamos posicionar o lago até 10 vezes para não sobrepor portas ou baús
            for (int attempt = 0; attempt < 10; attempt++) {
                int lakeW = rand.nextInt(4) + 3; // 3 to 6 width
                int lakeH = rand.nextInt(3) + 2; // 2 to 4 height
                int lakeX = rand.nextInt(MAP_COLS - lakeW - 1) + 1;
                int lakeY = rand.nextInt(MAP_ROWS - lakeH - 1) + 1;
                
                boolean overlap = false;
                int checkX1 = lakeX - 1;
                int checkX2 = lakeX + lakeW;
                int checkY1 = lakeY - 1;
                int checkY2 = lakeY + lakeH;
                
                for (DoorPoint d : currentDoors) {
                    if (d.col >= checkX1 && d.col <= checkX2 && d.row >= checkY1 && d.row <= checkY2) {
                        overlap = true;
                        break;
                    }
                }
                if (chestCol != -1 && chestCol >= checkX1 && chestCol <= checkX2 && chestRow >= checkY1 && chestRow <= checkY2) {
                    overlap = true;
                }
                
                // Protege o centro para o spawn inicial
                int cx = MAP_COLS / 2;
                int cy = MAP_ROWS / 2;
                if (cx >= checkX1 && cx <= checkX2 && cy >= checkY1 && cy <= checkY2) {
                    overlap = true;
                }
                
                if (!overlap) {
                    for(int c = lakeX; c < lakeX + lakeW; c++) {
                        for(int r = lakeY; r < lakeY + lakeH; r++) {
                            waterMap[c][r] = true;
                        }
                    }
                    break; // Posicionado com sucesso
                }
            }
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        selectionCooldown -= delta;

        // Input global tratado antes do movimento/seleção para evitar dupla ativação
        if (paused) {
            handlePauseInput();
            stateTime = 0f;
        } else if (selectingTeam) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
                selectingTeam = false;
                firstSwapIndex = -1;
                selectionCooldown = 0.2f;
                return;
            }
            handleTeamSelectionInput();
            isMoving = false;
            stateTime = 0f;
        } else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                paused = true;
                pauseCursor = 0;
                return;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
                if (player.getTeam().size() >= 2) {
                    selectingTeam = true;
                    selectionCooldown = 0.2f;
                    return;
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
                game.setScreen(new PartyMenuScreen(game, this, player, inventory));
                return;
            }
            handleMovement(delta);
        }
        
        if (isMoving) {
            stateTime += delta;
        } else {
            stateTime = 0f;
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
                } else if (isWater(c, r)) {
                    batch.draw(getWaterTexture(c, r), px, py, TILE, TILE);
                } else if (isGrass(c, r)) {
                    batch.draw(texGrass, px, py, TILE, TILE);
                }
            }
        }
        
        for (DoorPoint d : currentDoors) {
            batch.draw(texDoor, offsetX + d.col * TILE, offsetY + d.row * TILE, TILE, TILE);
        }
        
        if (chestCol != -1 && chestRow != -1) {
            batch.draw(texChest, offsetX + chestCol * TILE, offsetY + chestRow * TILE, TILE, TILE);
        }
        
        // Desenha o sprite do jogador conforme a direção
        Texture playerTex = getPlayerTexture();
        float drawW = 32f;
        float drawH = 32f;
        float drawX = offsetX + playerX + (TILE - drawW) / 2f;
        float drawY = offsetY + playerY + (TILE - drawH) / 2f;
        
        batch.draw(playerTex, drawX, drawY, drawW, drawH);

        // ========== SIDEBAR DA EQUIPE ==========
        drawPartySidebar(batch, font);

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
        
        if (selectingTeam) {
            String msg = "Selecione 2 Pokemons p/ trocar [ENTER]";
            if (firstSwapIndex != -1) {
                msg = "Selecione o 2o Pokemon [ENTER]";
            }
            font.setColor(Color.ORANGE);
            font.draw(batch, msg, 5, 14);
            font.setColor(Color.GRAY);
            font.draw(batch, "[TAB] Sair", viewport.getWorldWidth() - 100, 14);
        } else {
            font.setColor(Color.LIGHT_GRAY);
            font.draw(batch, statusMessage, 5, 14);
            font.setColor(Color.GRAY);
            font.draw(batch, "[TAB] Menu | [ESC] Sair", viewport.getWorldWidth() - 165, 14);
        }

        drawMiniMap(batch, viewport);

        if (paused) {
            drawPauseMenu(batch, font, viewport);
        }

        batch.end();
    }

    private void drawPauseMenu(SpriteBatch batch, BitmapFont font, Viewport viewport) {
        float sw = viewport.getWorldWidth();
        float sh = viewport.getWorldHeight();

        // Fundo escuro
        batch.setColor(0, 0, 0, 0.8f);
        batch.draw(game.getPixelWhite(), 0, 0, sw, sh);
        batch.setColor(Color.WHITE);

        // Título
        font.setColor(Color.WHITE);
        font.draw(batch, "PAUSE", sw / 2 - 20, sh / 2 + 30);

        // Opções
        String[] options = { "RESUMIR", "SAIR" };
        float y = sh / 2;
        for (int i = 0; i < options.length; i++) {
            if (i == pauseCursor) {
                font.setColor(Color.YELLOW);
                font.draw(batch, "> " + options[i], sw / 2 - 30, y);
            } else {
                font.setColor(Color.LIGHT_GRAY);
                font.draw(batch, "  " + options[i], sw / 2 - 30, y);
            }
            y -= 16;
        }

        font.setColor(Color.GRAY);
        font.draw(batch, "[ENTER] Selecionar", sw / 2 - 55, y - 8);

        font.setColor(Color.WHITE);
        batch.setColor(Color.WHITE);
    }

    private void handlePauseInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            paused = false;
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            pauseCursor = (pauseCursor + 1) % 2;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            pauseCursor = (pauseCursor - 1 + 2) % 2;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (pauseCursor == 0) {
                paused = false;
            } else {
                game.setScreen(new MenuScreen(game));
                dispose();
            }
        }
    }

    private void drawMiniMap(SpriteBatch batch, Viewport viewport) {
        float sw = viewport.getWorldWidth();
        float sh = viewport.getWorldHeight();
        
        // Área do minimapa no canto superior direito
        float mapSize = 64f; // tamanho base da área de desenho do mapa
        float startX = sw - mapSize - 10;
        float startY = sh - mapSize - 10;
        
        // Fundo do minimapa
        batch.setColor(0, 0, 0, 0.6f);
        batch.draw(game.getPixelWhite(), startX - 5, startY - 5, mapSize + 10, mapSize + 10);
        batch.setColor(Color.WHITE);

        DungeonGraph graph = dungeonManager.getGraph();
        Room current = dungeonManager.getCurrentRoom();
        
        float cellSize = 8f; // Tamanho de cada bloco de sala
        float spacing = 16f; // Distância entre as salas
        
        // Vamos centralizar o minimapa na sala (0,0) ou na sala atual para que fique dinâmico?
        // Como o mapa é pequeno, usaremos um offset fixo baseado no (0,0) ficando na parte inferior esquerda do minimapa.
        float offsetX = startX + 16f;
        float offsetY = startY + 16f;
        
        // 1) Desenha as conexões primeiro (linhas)
        batch.setColor(Color.GRAY);
        for (Room r : graph.getAllRooms()) {
            if (r.isVisited()) {
                for (Room neighbor : graph.getNeighbors(r)) {
                    if (neighbor.isVisited()) {
                        float x1 = offsetX + r.getMapX() * spacing + cellSize / 2f;
                        float y1 = offsetY + r.getMapY() * spacing + cellSize / 2f;
                        float x2 = offsetX + neighbor.getMapX() * spacing + cellSize / 2f;
                        float y2 = offsetY + neighbor.getMapY() * spacing + cellSize / 2f;
                        
                        // Desenha uma linha usando o pixelWhite
                        // Para simplificar, como o mapa é ortogonal, só desenhamos retângulos
                        float minX = Math.min(x1, x2);
                        float maxX = Math.max(x1, x2);
                        float minY = Math.min(y1, y2);
                        float maxY = Math.max(y1, y2);
                        
                        if (minX == maxX) {
                            // Linha vertical
                            batch.draw(game.getPixelWhite(), minX - 1, minY, 2, maxY - minY);
                        } else {
                            // Linha horizontal
                            batch.draw(game.getPixelWhite(), minX, minY - 1, maxX - minX, 2);
                        }
                    }
                }
            }
        }
        
        // 2) Desenha as salas
        for (Room r : graph.getAllRooms()) {
            if (r.isVisited()) {
                float rx = offsetX + r.getMapX() * spacing;
                float ry = offsetY + r.getMapY() * spacing;
                
                if (r.equals(current)) {
                    batch.setColor(Color.CYAN); // Sala atual
                } else {
                    batch.setColor(Color.LIGHT_GRAY); // Sala visitada
                }
                
                batch.draw(game.getPixelWhite(), rx, ry, cellSize, cellSize);
            }
        }
        batch.setColor(Color.WHITE);
    }

    private Texture getPlayerTexture() {
        if (!isMoving) {
            switch (currentDirection) {
                case 0:  return texPlayerDown;
                case 1:  return texPlayerLeft;
                case 2:  return texPlayerRight;
                case 3:  return texPlayerUp;
                default: return texPlayerDown;
            }
        } else {
            // Alterna os sprites a cada 0.15 segundos para formar o ciclo de caminhada
            int frame = (int) (stateTime / 0.15f) % 4;
            switch (currentDirection) {
                case 0:
                    if (frame == 0) return texPlayerDownWalk1;
                    if (frame == 2) return texPlayerDownWalk2;
                    return texPlayerDown;
                case 1:
                    if (frame == 0) return texPlayerLeftWalk1;
                    if (frame == 2) return texPlayerLeftWalk2;
                    return texPlayerLeft;
                case 2:
                    if (frame == 0) return texPlayerRightWalk1;
                    if (frame == 2) return texPlayerRightWalk2;
                    return texPlayerRight;
                case 3:
                    if (frame == 0) return texPlayerUpWalk1;
                    if (frame == 2) return texPlayerUpWalk2;
                    return texPlayerUp;
                default:
                    return texPlayerDown;
            }
        }
    }

    private void drawPartySidebar(SpriteBatch batch, BitmapFont font) {
        float sx = 0;
        float sy = 20;
        float sw = 76;
        float sh = 190;

        float cy = sy + sh - 8;

        // Título
        font.setColor(selectingTeam ? Color.ORANGE : Color.YELLOW);
        font.draw(batch, "TIME", sx + 20, cy);
        cy -= 16;

        for (int i = 0; i < player.getTeam().size(); i++) {
            Pokemon p = player.getTeam().get(i);
            boolean isActive = (p == player.getActivePokemon());

            // Cursor de navegação no modo seleção
            if (selectingTeam && i == teamCursorIndex) {
                font.setColor(Color.YELLOW);
                font.draw(batch, ">", sx + 10, cy);
            }

            // Nome
            if (i == firstSwapIndex) {
                font.setColor(Color.ORANGE);
            } else if (p.isFainted()) {
                font.setColor(Color.RED);
            } else if (isActive) {
                font.setColor(Color.CYAN);
            } else {
                font.setColor(Color.WHITE);
            }
            String prefix = isActive ? ">" : " ";
            font.draw(batch, prefix + p.getName(), sx + 20, cy);

            // Indicador de primeiro selecionado
            if (i == firstSwapIndex) {
                font.setColor(Color.ORANGE);
                font.draw(batch, "<<", sx + sw - 15, cy);
            }

            // Sprite do Pokémon (14x14)
            Texture sprite = pokemonSprites.get(p.getName());
            if (sprite != null) {
                batch.draw(sprite, sx + 3, cy - 16, 14, 14);
            }

            // Barra de HP
            float barW = 48;
            float barH = 3;
            float hpRatio = (float) p.getHp() / p.getMaxHp();
            float barY = cy - 12;

            batch.setColor(Color.DARK_GRAY);
            batch.draw(game.getPixelWhite(), sx + 20, barY, barW, barH);
            batch.setColor(hpRatio > 0.5f ? Color.GREEN : hpRatio > 0.2f ? Color.YELLOW : Color.RED);
            batch.draw(game.getPixelWhite(), sx + 20, barY, barW * hpRatio, barH);
            batch.setColor(Color.WHITE);

            // Texto de HP
            font.setColor(Color.WHITE);
            font.draw(batch, p.getHp() + "/" + p.getMaxHp(), sx + 72, cy - 8);

            cy -= 24;
        }

        font.setColor(Color.WHITE);
        batch.setColor(Color.WHITE);
    }

    private void handleTeamSelectionInput() {
        if (selectionCooldown > 0) return;

        int maxIndex = player.getTeam().size();

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            teamCursorIndex = (teamCursorIndex + 1) % maxIndex;
            selectionCooldown = 0.2f;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            teamCursorIndex = (teamCursorIndex - 1 + maxIndex) % maxIndex;
            selectionCooldown = 0.2f;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (firstSwapIndex == -1) {
                firstSwapIndex = teamCursorIndex;
            } else if (firstSwapIndex == teamCursorIndex) {
                firstSwapIndex = -1;
            } else {
                ArrayList<Pokemon> team = player.getTeam();
                Pokemon a = team.get(firstSwapIndex);
                Pokemon b = team.get(teamCursorIndex);
                team.set(firstSwapIndex, b);
                team.set(teamCursorIndex, a);
                firstSwapIndex = -1;
                statusMessage = "Ordem trocada!";
            }
            selectionCooldown = 0.2f;
        }
    }

    private void handleMovement(float delta) {
        float nextX = playerX;
        float nextY = playerY;
        float speed = GameConstants.PLAYER_SPEED;

        isMoving = false;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            nextX -= speed * delta;
            currentDirection = 1;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            nextX += speed * delta;
            currentDirection = 2;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            nextY -= speed * delta;
            currentDirection = 0;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            nextY += speed * delta;
            currentDirection = 3;
            isMoving = true;
        }

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

    private boolean isWater(int c, int r) {
        if (c < 0 || c >= MAP_COLS || r < 0 || r >= MAP_ROWS) return false;
        if (isWall(c, r)) return false;
        return waterMap[c][r];
    }

    private boolean isGrass(int c, int r) {
        if (isWall(c, r) || isWater(c, r)) return false;
        int roomSeed = dungeonManager.getCurrentRoom().getId() * 100 + c * 10 + r;
        return roomSeed % 17 == 0;
    }

    private Texture getWaterTexture(int c, int r) {
        boolean n = isWater(c, r + 1);
        boolean s = isWater(c, r - 1);
        boolean w = isWater(c - 1, r);
        boolean e = isWater(c + 1, r);

        if (!n && !s && !w && !e) return texWater; // Bloco de água isolado
        
        // Cantos Externos (Onde NÃO tem água em duas direções adjacentes)
        if (!n && !w && e && s) return texWaterNW; // Canto Superior Esquerdo do lago
        if (!n && !e && w && s) return texWaterNE; // Canto Superior Direito
        if (!s && !w && e && n) return texWaterSW; // Canto Inferior Esquerdo
        if (!s && !e && w && n) return texWaterSE; // Canto Inferior Direito
        
        // Bordas (Onde NÃO tem água em apenas uma direção)
        if (!n) return texWaterN; // Borda Norte do lago
        if (!s) return texWaterS; // Borda Sul
        if (!w) return texWaterW; // Borda Oeste
        if (!e) return texWaterE; // Borda Leste
        
        return texWaterCenter; // Rodeado de água
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
                game.setScreen(new BattleScreen(game, this, player, active, target.getEnemy(), inventory));
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
        texWater.dispose();
        texGrass.dispose();
        texWaterCenter.dispose();
        texWaterN.dispose();
        texWaterS.dispose();
        texWaterE.dispose();
        texWaterW.dispose();
        texWaterNW.dispose();
        texWaterNE.dispose();
        texWaterSW.dispose();
        texWaterSE.dispose();
        texPlayerDown.dispose();
        texPlayerLeft.dispose();
        texPlayerRight.dispose();
        texPlayerUp.dispose();
        texPlayerDownWalk1.dispose();
        texPlayerDownWalk2.dispose();
        texPlayerLeftWalk1.dispose();
        texPlayerLeftWalk2.dispose();
        texPlayerRightWalk1.dispose();
        texPlayerRightWalk2.dispose();
        texPlayerUpWalk1.dispose();
        texPlayerUpWalk2.dispose();
        for (Texture t : pokemonSprites.values()) {
            t.dispose();
        }
    }
}
