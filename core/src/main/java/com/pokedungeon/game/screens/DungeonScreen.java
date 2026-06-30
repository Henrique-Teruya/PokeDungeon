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
import com.pokedungeon.game.GameSession;
import com.pokedungeon.game.Main;
import com.pokedungeon.game.dungeon.DungeonGraph;
import com.pokedungeon.game.dungeon.DungeonManager;
import com.pokedungeon.game.dungeon.DungeonGenerator;
import com.pokedungeon.game.dungeon.Room;
import com.pokedungeon.game.model.Item;
import com.pokedungeon.game.model.Pokemon;
import com.pokedungeon.game.utils.GameConstants;

public class DungeonScreen implements Screen {

    private Main game;
    private GameSession session;
    private DungeonManager dungeonManager;

    // Texturas
    private Texture texFloor, texWall, texDoor, texChest, texWater, texGrass;
    private Texture texWaterN, texWaterS, texWaterE, texWaterW, texWaterNW, texWaterNE, texWaterSW, texWaterSE, texWaterCenter;

    // Sprites do jogador
    private Texture texPlayerDown, texPlayerLeft, texPlayerRight, texPlayerUp;
    private Texture texPlayerDownWalk1, texPlayerDownWalk2;
    private Texture texPlayerLeftWalk1, texPlayerLeftWalk2;
    private Texture texPlayerRightWalk1, texPlayerRightWalk2;
    private Texture texPlayerUpWalk1, texPlayerUpWalk2;
    private int currentDirection = 0;
    private float stateTime = 0f;
    private boolean isMoving = false;

    private float playerX, playerY;
    private final int TILE = 32;
    private final int MAP_COLS = 13;
    private final int MAP_ROWS = 7;

    private boolean[][] waterMap = new boolean[MAP_COLS][MAP_ROWS];
    private String statusMessage = "";

    private HashMap<String, Texture> pokemonSprites;

    // Team selection
    private boolean selectingTeam = false;
    private int teamCursorIndex = 0;
    private int firstSwapIndex = -1;
    private float selectionCooldown = 0f;

    // Pause
    private boolean paused = false;
    private int pauseCursor = 0;

    class DoorPoint {
        int col, row;
        Room target;
        DoorPoint(int c, int r, Room t) { this.col = c; this.row = r; this.target = t; }
    }
    private ArrayList<DoorPoint> currentDoors = new ArrayList<>();
    private int chestCol = -1, chestRow = -1;

    public DungeonScreen(Main game) {
        this.game = game;
        loadTextures();
        session = new GameSession();
        generateFloor();
    }

    public DungeonScreen(Main game, GameSession session) {
        this.game = game;
        this.session = session;
        loadTextures();
        generateFloor();
    }

    private void loadTextures() {
        texFloor = new Texture(Gdx.files.internal("tiles/floor.png"));
        texWall = new Texture(Gdx.files.internal("tiles/wall.png"));
        texDoor = new Texture(Gdx.files.internal("tiles/door.png"));
        texChest = new Texture(Gdx.files.internal("tiles/chest.png"));
        texWater = new Texture(Gdx.files.internal("tiles/water.png"));
        texGrass = new Texture(Gdx.files.internal("tiles/grass.png"));

        texWaterCenter = new Texture(Gdx.files.internal("tiles/water_center.png"));
        texWaterN = new Texture(Gdx.files.internal("tiles/water_n.png"));
        texWaterS = new Texture(Gdx.files.internal("tiles/water_s.png"));
        texWaterE = new Texture(Gdx.files.internal("tiles/water_e.png"));
        texWaterW = new Texture(Gdx.files.internal("tiles/water_w.png"));
        texWaterNW = new Texture(Gdx.files.internal("tiles/water_nw.png"));
        texWaterNE = new Texture(Gdx.files.internal("tiles/water_ne.png"));
        texWaterSW = new Texture(Gdx.files.internal("tiles/water_sw.png"));
        texWaterSE = new Texture(Gdx.files.internal("tiles/water_se.png"));

        Texture[] allTiles = {
            texFloor, texWall, texDoor, texChest, texWater, texGrass,
            texWaterCenter, texWaterN, texWaterS, texWaterE, texWaterW,
            texWaterNW, texWaterNE, texWaterSW, texWaterSE
        };
        for (Texture t : allTiles) {
            t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

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

        pokemonSprites = new HashMap<>();
        for (Pokemon p : session.getPlayer().getTeam()) {
            String path = "sprites/" + p.getName().toLowerCase() + ".PNG";
            if (Gdx.files.internal(path).exists()) {
                Texture tex = new Texture(Gdx.files.internal(path));
                tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                pokemonSprites.put(p.getName(), tex);
            }
        }
    }

    private void generateFloor() {
        DungeonGenerator generator = new DungeonGenerator(session.getRandom(), session.getCurrentFloor());
        DungeonGraph graph = generator.generate();

        ArrayList<Room> rooms = graph.getAllRooms();
        boolean hasEntrance = false;
        boolean isBossFloor = session.isBossFloor();

        for (Room room : rooms) {
            if (!hasEntrance) {
                hasEntrance = true;
                dungeonManager = new DungeonManager(graph, room);
                if (isBossFloor) {
                    statusMessage = "Andar " + session.getCurrentFloor() + " - BOSS!";
                } else {
                    statusMessage = "Andar " + session.getCurrentFloor() + " - Explore!";
                }
            } else {
                if (session.getRandom().nextFloat() < 0.5f) {
                    room.setEnemy(session.generateEnemy());
                } else if (session.getRandom().nextFloat() < 0.25f) {
                    room.setItem(new Item("Poção", GameConstants.DEFAULT_POTION_HEAL));
                } else if (session.getRandom().nextFloat() < 0.15f) {
                    // Random events
                    float eventRoll = session.getRandom().nextFloat();
                    if (eventRoll < 0.4f) {
                        room.setEvent(RoomEvent.MYSTERY_CHEST);
                        room.setItem(new Item("Super Poção", 40));
                        room.setName("Baú Misterioso");
                    } else if (eventRoll < 0.7f) {
                        room.setEvent(RoomEvent.HEALING_SPRING);
                        room.setName("Fonte de Cura");
                    } else {
                        room.setEvent(RoomEvent.RARE_POKEMON);
                        room.setEnemy(session.generateEnemy());
                        room.setName("Sala Secreta");
                    }
                }
            }
        }

        // Add boss to last room on boss floors
        if (isBossFloor && !rooms.isEmpty()) {
            Room lastRoom = rooms.get(rooms.size() - 1);
            lastRoom.setEnemy(session.generateBoss());
            lastRoom.setName("Sala do Boss");
        }

        if (!hasEntrance) {
            Room entrance = new Room(0, "Entrada", "A entrada escura da dungeon.");
            entrance.setMapX(0); entrance.setMapY(0);
            graph.addRoom(entrance);
            dungeonManager = new DungeonManager(graph, entrance);
        }

        lastEntryDirection = -1;
        buildRoomMap();
    }

    private int lastEntryDirection = -1;

    private void buildRoomMap() {
        currentDoors.clear();
        chestCol = -1; chestRow = -1;

        Room current = dungeonManager.getCurrentRoom();
        ArrayList<Room> neighbors = dungeonManager.getAvailableRooms();

        int[][] positions = {
            {MAP_COLS / 2, MAP_ROWS - 1},
            {MAP_COLS - 1, MAP_ROWS / 2},
            {MAP_COLS / 2, 0},
            {0, MAP_ROWS / 2}
        };

        for (Room neighbor : neighbors) {
            int dx = neighbor.getMapX() - current.getMapX();
            int dy = neighbor.getMapY() - current.getMapY();

            int direction = -1;
            if (dy > 0) direction = 0;
            else if (dx > 0) direction = 1;
            else if (dy < 0) direction = 2;
            else if (dx < 0) direction = 3;

            if (direction != -1) {
                currentDoors.add(new DoorPoint(positions[direction][0], positions[direction][1], neighbor));
            }
        }

        if (current.hasItem()) {
            chestCol = MAP_COLS / 2;
            chestRow = MAP_ROWS / 2;
        }

        if (lastEntryDirection == 0) {
            playerX = positions[0][0] * TILE;
            playerY = (positions[0][1] - 1) * TILE;
        } else if (lastEntryDirection == 1) {
            playerX = (positions[1][0] - 1) * TILE;
            playerY = positions[1][1] * TILE;
        } else if (lastEntryDirection == 2) {
            playerX = positions[2][0] * TILE;
            playerY = (positions[2][1] + 1) * TILE;
        } else if (lastEntryDirection == 3) {
            playerX = (positions[3][0] + 1) * TILE;
            playerY = positions[3][1] * TILE;
        } else {
            playerX = (MAP_COLS / 2) * TILE;
            playerY = (MAP_ROWS / 2) * TILE - 20;
        }

        for (int c = 0; c < MAP_COLS; c++) {
            for (int r = 0; r < MAP_ROWS; r++) {
                waterMap[c][r] = false;
            }
        }

        Random rand = new Random(current.getId() * 1000L);
        int numLakes = rand.nextInt(3) + 1;
        int lakesPlaced = 0;

        for (int l = 0; l < numLakes; l++) {
            for (int attempt = 0; attempt < 50; attempt++) {
                int lakeW = rand.nextInt(4) + 3;
                int lakeH = rand.nextInt(3) + 3;
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

                int cx = MAP_COLS / 2;
                int cy = MAP_ROWS / 2;
                if (cx >= checkX1 && cx <= checkX2 && cy >= checkY1 && cy <= checkY2) {
                    overlap = true;
                }

                if (!overlap) {
                    for (int c = lakeX; c < lakeX + lakeW; c++) {
                        for (int r = lakeY; r < lakeY + lakeH; r++) {
                            waterMap[c][r] = true;
                        }
                    }
                    lakesPlaced++;
                    break;
                }
            }
        }

        if (lakesPlaced == 0) {
            outer:
            for (int c = 2; c < MAP_COLS - 2; c++) {
                for (int r = 2; r < MAP_ROWS - 2; r++) {
                    if (!waterMap[c][r] && (c != MAP_COLS / 2 || r != MAP_ROWS / 2) && (c != chestCol || r != chestRow)) {
                        boolean doorNear = false;
                        for (DoorPoint d : currentDoors) {
                            if (Math.abs(d.col - c) <= 1 && Math.abs(d.row - r) <= 1) doorNear = true;
                        }
                        if (!doorNear) {
                            waterMap[c][r] = true;
                            waterMap[c + 1][r] = true;
                            break outer;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        if (session.isGameOver()) {
            game.setScreen(new GameOverScreen(game, session.getCurrentFloor()));
            dispose();
            return;
        }

        selectionCooldown -= delta;

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
                if (session.getPlayer().getTeam().size() >= 2) {
                    selectingTeam = true;
                    selectionCooldown = 0.2f;
                    return;
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
                game.setScreen(new PartyMenuScreen(game, this, session.getPlayer(), session.getInventory()));
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

        float offsetX = (viewport.getWorldWidth() - (MAP_COLS * TILE)) / 2f;
        float offsetY = (viewport.getWorldHeight() - (MAP_ROWS * TILE)) / 2f;

        batch.begin();

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

        Texture playerTex = getPlayerTexture();
        float drawW = 32f;
        float drawH = 32f;
        float drawX = offsetX + playerX + (TILE - drawW) / 2f;
        float drawY = offsetY + playerY + (TILE - drawH) / 2f;

        batch.draw(playerTex, drawX, drawY, drawW, drawH);

        drawPartySidebar(batch, font);

        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(game.getPixelWhite(), 0, viewport.getWorldHeight() - 30, viewport.getWorldWidth(), 30);
        batch.setColor(Color.WHITE);

        Room current = dungeonManager.getCurrentRoom();
        font.setColor(Color.YELLOW);
        font.draw(batch, current.getName(), 5, viewport.getWorldHeight() - 5);

        Pokemon active = session.getPlayer().getActivePokemon();
        font.setColor(Color.CYAN);
        font.draw(batch, "Andar " + session.getCurrentFloor(), 200, viewport.getWorldHeight() - 5);

        font.setColor(Color.WHITE);
        font.draw(batch, "Itens: " + session.getInventory().getSize(), 300, viewport.getWorldHeight() - 5);

        font.setColor(Color.ORANGE);
        font.draw(batch, "Bolas: " + session.getPokeballs(), 370, viewport.getWorldHeight() - 5);

        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(game.getPixelWhite(), 0, 0, viewport.getWorldWidth(), 20);
        batch.setColor(Color.WHITE);

        if (selectingTeam) {
            String msg = "Selecione 2 Pokemons p/ trocar [ENTER]";
            if (firstSwapIndex != -1) msg = "Selecione o 2o Pokemon [ENTER]";
            font.setColor(Color.ORANGE);
            font.draw(batch, msg, 5, 14);
            font.setColor(Color.GRAY);
            font.draw(batch, "[TAB] Sair", viewport.getWorldWidth() - 100, 14);
        } else {
            font.setColor(Color.LIGHT_GRAY);
            font.draw(batch, statusMessage, 5, 14);
            font.setColor(Color.GRAY);
            font.draw(batch, "[TAB] Menu | [ESC] Pausa | [M] Mochila", viewport.getWorldWidth() - 265, 14);
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

        batch.setColor(0, 0, 0, 0.8f);
        batch.draw(game.getPixelWhite(), 0, 0, sw, sh);
        batch.setColor(Color.WHITE);

        font.setColor(Color.WHITE);
        font.draw(batch, "PAUSE", sw / 2 - 20, sh / 2 + 30);

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
                dispose();
                game.setScreen(new MenuScreen(game));
            }
        }
    }

    private void drawMiniMap(SpriteBatch batch, Viewport viewport) {
        float sw = viewport.getWorldWidth();
        float sh = viewport.getWorldHeight();

        float mapSize = 64f;
        float startX = sw - mapSize - 10;
        float startY = sh - mapSize - 10;

        batch.setColor(0, 0, 0, 0.6f);
        batch.draw(game.getPixelWhite(), startX - 5, startY - 5, mapSize + 10, mapSize + 10);
        batch.setColor(Color.WHITE);

        DungeonGraph graph = dungeonManager.getGraph();
        Room current = dungeonManager.getCurrentRoom();

        float cellSize = 8f;
        float spacing = 16f;
        float offsetX = startX + 16f;
        float offsetY = startY + 16f;

        batch.setColor(Color.GRAY);
        for (Room r : graph.getAllRooms()) {
            if (r.isVisited()) {
                for (Room neighbor : graph.getNeighbors(r)) {
                    if (neighbor.isVisited()) {
                        float x1 = offsetX + r.getMapX() * spacing + cellSize / 2f;
                        float y1 = offsetY + r.getMapY() * spacing + cellSize / 2f;
                        float x2 = offsetX + neighbor.getMapX() * spacing + cellSize / 2f;
                        float y2 = offsetY + neighbor.getMapY() * spacing + cellSize / 2f;

                        float minX = Math.min(x1, x2);
                        float maxX = Math.max(x1, x2);
                        float minY = Math.min(y1, y2);
                        float maxY = Math.max(y1, y2);

                        if (minX == maxX) {
                            batch.draw(game.getPixelWhite(), minX - 1, minY, 2, maxY - minY);
                        } else {
                            batch.draw(game.getPixelWhite(), minX, minY - 1, maxX - minX, 2);
                        }
                    }
                }
            }
        }

        for (Room r : graph.getAllRooms()) {
            if (r.isVisited()) {
                float rx = offsetX + r.getMapX() * spacing;
                float ry = offsetY + r.getMapY() * spacing;

                batch.setColor(r.equals(current) ? Color.CYAN : Color.LIGHT_GRAY);
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

        font.setColor(selectingTeam ? Color.ORANGE : Color.YELLOW);
        font.draw(batch, "TIME", sx + 20, cy);
        cy -= 16;

        for (int i = 0; i < session.getPlayer().getTeam().size(); i++) {
            Pokemon p = session.getPlayer().getTeam().get(i);
            boolean isActive = (p == session.getPlayer().getActivePokemon());

            if (selectingTeam && i == teamCursorIndex) {
                font.setColor(Color.YELLOW);
                font.draw(batch, ">", sx + 10, cy);
            }

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

            if (i == firstSwapIndex) {
                font.setColor(Color.ORANGE);
                font.draw(batch, "<<", sx + sw - 15, cy);
            }

            Texture sprite = pokemonSprites.get(p.getName());
            if (sprite != null) {
                batch.draw(sprite, sx + 3, cy - 16, 14, 14);
            }

            float barW = 48;
            float barH = 3;
            float hpRatio = (float) p.getHp() / p.getMaxHp();
            float barY = cy - 12;

            batch.setColor(Color.DARK_GRAY);
            batch.draw(game.getPixelWhite(), sx + 20, barY, barW, barH);
            batch.setColor(hpRatio > 0.5f ? Color.GREEN : hpRatio > 0.2f ? Color.YELLOW : Color.RED);
            batch.draw(game.getPixelWhite(), sx + 20, barY, barW * hpRatio, barH);
            batch.setColor(Color.WHITE);

            font.setColor(Color.WHITE);
            font.draw(batch, p.getHp() + "/" + p.getMaxHp(), sx + 72, cy - 8);

            cy -= 24;
        }

        font.setColor(Color.WHITE);
        batch.setColor(Color.WHITE);
    }

    private void handleTeamSelectionInput() {
        if (selectionCooldown > 0) return;

        int maxIndex = session.getPlayer().getTeam().size();

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
                ArrayList<Pokemon> team = session.getPlayer().getTeam();
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

        int pCol = (int) ((playerX + TILE / 2) / TILE);
        int pRow = (int) ((playerY + TILE / 2) / TILE);

        for (DoorPoint d : currentDoors) {
            if (pCol == d.col && pRow == d.row) {
                int dirUsed = -1;
                if (d.row == MAP_ROWS - 1) dirUsed = 0;
                else if (d.col == MAP_COLS - 1) dirUsed = 1;
                else if (d.row == 0) dirUsed = 2;
                else if (d.col == 0) dirUsed = 3;

                enterRoom(d.target, dirUsed);
                return;
            }
        }

        if (pCol == chestCol && pRow == chestRow && dungeonManager.getCurrentRoom().hasItem()) {
            Item collected = dungeonManager.getCurrentRoom().collectItem();
            if (session.getInventory().addItem(collected)) {
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

        if (!n && !s && !w && !e) return texWater;

        if (!n && !w && e && s) return texWaterNW;
        if (!n && !e && w && s) return texWaterNE;
        if (!s && !w && e && n) return texWaterSW;
        if (!s && !e && w && n) return texWaterSE;

        if (!n) return texWaterN;
        if (!s) return texWaterS;
        if (!w) return texWaterW;
        if (!e) return texWaterE;

        return texWaterCenter;
    }

    private void enterRoom(Room target, int directionUsed) {
        Room oldRoom = dungeonManager.getCurrentRoom();
        Room prevRoom = dungeonManager.canGoBack() ? dungeonManager.getRoomHistory().peek() : null;

        if (target.equals(prevRoom)) {
            dungeonManager.goBack();
        } else {
            dungeonManager.moveTo(target);
        }

        int dx = oldRoom.getMapX() - target.getMapX();
        int dy = oldRoom.getMapY() - target.getMapY();
        if (dy < 0) lastEntryDirection = 2;
        else if (dx > 0) lastEntryDirection = 1;
        else if (dy > 0) lastEntryDirection = 0;
        else if (dx < 0) lastEntryDirection = 3;
        else lastEntryDirection = -1;

        buildRoomMap();

        // Handle random events
        if (target.getEvent() != RoomEvent.NONE) {
            handleRoomEvent(target);
            target.triggerEvent();
            return;
        }

        if (target.hasEnemy()) {
            Pokemon active = session.getPlayer().getActivePokemon();
            if (active != null) {
                Pokemon enemy = target.getEnemy();
                target.setEnemy(null);
                game.setScreen(new BattleScreen(game, this, session, active, enemy));
                return;
            } else {
                session.setGameOver(true);
                return;
            }
        }

        statusMessage = "Entrou em: " + target.getName();
    }

    private void handleRoomEvent(Room room) {
        switch (room.getEvent()) {
            case MYSTERY_CHEST:
                if (room.hasItem()) {
                    Item item = room.collectItem();
                    if (session.getInventory().addItem(item)) {
                        statusMessage = "Baú Misterioso! Encontrou: " + item.getName() + "!";
                    } else {
                        statusMessage = "Baú Misterioso! Mochila cheia!";
                    }
                } else {
                    statusMessage = "Baú Misterioso! Vazio...";
                }
                break;
            case HEALING_SPRING:
                for (Pokemon p : session.getPlayer().getTeam()) {
                    p.heal(15);
                }
                statusMessage = "Fonte de Cura! Time recuperou 15 HP!";
                break;
            case RARE_POKEMON:
                statusMessage = "Sala Secreta! Um Pokemon raro aparece!";
                if (room.hasEnemy()) {
                    Pokemon active = session.getPlayer().getActivePokemon();
                    if (active != null) {
                        Pokemon enemy = room.getEnemy();
                        room.setEnemy(null);
                        game.setScreen(new BattleScreen(game, this, session, active, enemy));
                        return;
                    }
                }
                break;
        }
    }

    public void onBattleWon(Pokemon defeatedEnemy) {
        // Build status message with loot and level-up info
        StringBuilder msg = new StringBuilder();
        msg.append("Venceu!");
        String lootMsg = session.getLastLootMessage();
        if (!lootMsg.isEmpty()) {
            msg.append(" ").append(lootMsg);
        }
        // Check for level ups
        for (Pokemon p : session.getPlayer().getTeam()) {
            if (p.getExp() >= p.getExpToNext()) {
                msg.append(" ").append(p.getName()).append(" Nv.").append(p.getLevel()).append("!");
            }
        }
        statusMessage = msg.toString();

        if (defeatedEnemy != null && defeatedEnemy.isFainted()) {
            game.setScreen(new CaptureScreen(game, this, session, defeatedEnemy));
            return;
        }
        if (session.shouldShowCenter()) {
            game.setScreen(new PokemonCenterScreen(game, this, session));
            return;
        }
        game.setScreen(this);
    }

    public void afterCapture() {
        if (session.shouldShowCenter()) {
            game.setScreen(new PokemonCenterScreen(game, this, session));
            return;
        }
        statusMessage = "Capture concluida!";
        game.setScreen(this);
    }

    public void afterCenter() {
        game.setScreen(new RewardChoiceScreen(game, this, session));
    }

    public void onRewardChosen() {
        session.nextFloor();
        generateFloor();
        statusMessage = "Andar " + session.getCurrentFloor() + "!";
        game.setScreen(this);
    }

    @Override
    public void resize(int w, int h) { game.getViewport().update(w, h, true); }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

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
