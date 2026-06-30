package com.pokedungeon.game;

import java.util.Random;

import com.pokedungeon.game.inventory.Inventory;
import com.pokedungeon.game.model.Item;
import com.pokedungeon.game.model.Player;
import com.pokedungeon.game.model.Pokemon;
import com.pokedungeon.game.utils.GameConstants;

public class GameSession {

    public enum Rarity { COMMON, UNCOMMON, RARE, VERY_RARE }

    private int currentFloor;
    private int battlesWon;
    private int battlesToCenter;
    private int pokeballs;
    private Player player;
    private Inventory inventory;
    private Random random;
    private boolean showPokemonCenter;
    private boolean gameOver;
    private String lastLootMessage;
    private Pokemon lastDefeatedEnemy;

    // Floor objectives
    private int objectiveBattles;
    private int objectiveCaptures;
    private int objectiveChests;
    private boolean objectiveBattlesDone;
    private boolean objectiveCapturesDone;
    private boolean objectiveChestsDone;
    private int totalCaptures;
    private int totalChests;

    public GameSession() {
        this.currentFloor = GameConstants.STARTING_FLOOR;
        this.battlesWon = 0;
        this.battlesToCenter = GameConstants.BATTLES_TO_CENTER;
        this.pokeballs = GameConstants.STARTING_POKEBALLS;
        this.inventory = new Inventory();
        this.random = new Random();
        this.showPokemonCenter = false;
        this.gameOver = false;
        this.player = new Player("Red");
        this.lastLootMessage = "";

        inventory.addItem(new Item("Poção", GameConstants.DEFAULT_POTION_HEAL));
        inventory.addItem(new Item("Poção", GameConstants.DEFAULT_POTION_HEAL));
    }

    public void onBattleWon(Pokemon defeatedEnemy) {
        battlesWon++;
        lastDefeatedEnemy = defeatedEnemy;

        // Distribute EXP to all living team Pokemon
        int expGain = defeatedEnemy.getLevel() * 3 + random.nextInt(5);
        for (Pokemon p : player.getTeam()) {
            if (!p.isFainted()) {
                p.gainExp(expGain);
            }
        }

        // Generate loot
        generateLoot();

        if (battlesWon >= battlesToCenter) {
            battlesWon = 0;
            showPokemonCenter = true;
        }
    }

    private void generateLoot() {
        float roll = random.nextFloat() * 100;
        if (roll < 50) {
            Item potion = new Item("Poção", GameConstants.DEFAULT_POTION_HEAL);
            if (inventory.addItem(potion)) {
                lastLootMessage = "Encontrou: Poção!";
            }
        } else if (roll < 70) {
            pokeballs++;
            lastLootMessage = "Encontrou: Pokébola!";
        } else if (roll < 85) {
            Item superPotion = new Item("Super Poção", 40);
            if (inventory.addItem(superPotion)) {
                lastLootMessage = "Encontrou: Super Poção!";
            }
        } else if (roll < 95) {
            pokeballs += 2;
            lastLootMessage = "Encontrou: 2 Pokébolas!";
        } else {
            Item megaPotion = new Item("Mega Poção", 80);
            if (inventory.addItem(megaPotion)) {
                lastLootMessage = "Encontrou: Mega Poção!";
            }
        }
    }

    public String getLastLootMessage() {
        return lastLootMessage;
    }

    public boolean shouldShowCenter() {
        return showPokemonCenter;
    }

    public void visitCenter() {
        for (Pokemon p : player.getTeam()) {
            p.fullHeal();
        }
        pokeballs += GameConstants.POKEBALLS_PER_CENTER;
        showPokemonCenter = false;
    }

    public void nextFloor() {
        currentFloor++;
        showPokemonCenter = false;
        battlesWon = 0;
    }

    public boolean tryCapture(Pokemon enemy) {
        if (pokeballs <= 0) return false;

        pokeballs--;
        float hpRatio = (float) enemy.getHp() / enemy.getMaxHp();

        // Higher base rate for easier capture
        float baseRate = 0.75f;
        float hpBonus = (1.0f - hpRatio) * 0.25f; // Low HP = easier
        float floorPenalty = 1.0f / (1.0f + currentFloor * 0.05f);

        float captureRate = (baseRate + hpBonus) * floorPenalty;
        captureRate = Math.min(0.95f, Math.max(0.15f, captureRate));

        return random.nextDouble() < captureRate;
    }

    public boolean addCapturedPokemon(Pokemon pokemon) {
        if (player.getTeam().size() < GameConstants.MAX_TEAM_SIZE) {
            return player.addPokemon(pokemon);
        }
        return false;
    }

    /**
     * Replace a Pokemon in the team with a new one.
     * Used when team is full and player wants to capture.
     */
    public boolean replacePokemon(int index, Pokemon newPokemon) {
        if (index < 0 || index >= player.getTeam().size()) return false;
        player.getTeam().set(index, newPokemon);
        return true;
    }

    public Pokemon generateEnemy() {
        Rarity rarity = rollRarity();
        return generateEnemyByRarity(rarity);
    }

    private Rarity rollRarity() {
        float roll = random.nextFloat() * 100;
        if (roll < 60) return Rarity.COMMON;
        if (roll < 85) return Rarity.UNCOMMON;
        if (roll < 97) return Rarity.RARE;
        return Rarity.VERY_RARE;
    }

    private Pokemon generateEnemyByRarity(Rarity rarity) {
        String name, type;
        int hpMult, dmgMult;

        switch (rarity) {
            case COMMON:
                name = rollName("Rattata", "Pidgey", "Caterpie", "Pidgey");
                type = rollType("Normal", "Normal", "Planta", "Normal");
                hpMult = 1; dmgMult = 1;
                break;
            case UNCOMMON:
                name = rollName("Zubat", "Geodude", "Bellsprout", "Ekans");
                type = rollType("Voador", "Pedra", "Planta", "Veneno");
                hpMult = 2; dmgMult = 2;
                break;
            case RARE:
                name = rollName("Growlithe", "Abra", "Scyther", "Pidgeotto");
                type = rollType("Fogo", "Psíquico", "Inseto", "Voador");
                hpMult = 3; dmgMult = 3;
                break;
            case VERY_RARE:
                name = rollName("Dratini", "Lapras", "Magneton", "Golbat");
                type = rollType("Dragão", "Água", "Elétrico", "Voador");
                hpMult = 4; dmgMult = 4;
                break;
            default:
                name = "Rattata"; type = "Normal";
                hpMult = 1; dmgMult = 1;
        }

        int level = GameConstants.STARTING_LEVEL + (currentFloor - 1) * 2 + random.nextInt(3);
        int baseHp = (40 + currentFloor * 10) * hpMult + random.nextInt(20);
        int baseDmg = (12 + currentFloor * 4) * dmgMult + random.nextInt(8);

        Pokemon enemy = new Pokemon(name, type, level, baseHp);
        enemy.addAttack("Ataque", baseDmg, type);
        if (random.nextBoolean()) {
            enemy.addAttack("Investida", baseDmg - 3, "Normal");
        }
        return enemy;
    }

    private String rollName(String... names) {
        return names[random.nextInt(names.length)];
    }

    private String rollType(String... types) {
        return types[random.nextInt(types.length)];
    }

    public Pokemon generateBoss() {
        String[] bossNames = {"Arcanine", "Alakazam", "Gyarados", "Dragonite", "Snorlax"};
        String[] bossTypes = {"Fogo", "Psíquico", "Água", "Dragão", "Normal"};
        int idx = random.nextInt(bossNames.length);

        int level = GameConstants.STARTING_LEVEL + currentFloor * 3;
        int baseHp = 120 + currentFloor * 25;
        int baseDmg = 30 + currentFloor * 8;

        Pokemon boss = new Pokemon(bossNames[idx], bossTypes[idx], level, baseHp);
        boss.addAttack("Ataque Forte", baseDmg, bossTypes[idx]);
        boss.addAttack("Investida", baseDmg - 5, "Normal");
        return boss;
    }

    public boolean isBossFloor() {
        return currentFloor % 5 == 0;
    }

    // --- Getters ---
    public int getCurrentFloor() { return currentFloor; }
    public int getBattlesWon() { return battlesWon; }
    public int getBattlesToCenter() { return battlesToCenter; }
    public int getPokeballs() { return pokeballs; }
    public void addPokeballs(int amount) { pokeballs += amount; }
    public Player getPlayer() { return player; }
    public Inventory getInventory() { return inventory; }
    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }
    public Random getRandom() { return random; }
    public Pokemon getLastDefeatedEnemy() { return lastDefeatedEnemy; }
}
