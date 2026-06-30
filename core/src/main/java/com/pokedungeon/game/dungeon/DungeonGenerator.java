package com.pokedungeon.game.dungeon;

import java.util.ArrayList;
import java.util.Random;

public class DungeonGenerator {

    private Random random;
    private int floor;

    public DungeonGenerator(Random random, int floor) {
        this.random = random;
        this.floor = floor;
    }

    public DungeonGraph generate() {
        DungeonGraph graph = new DungeonGraph();

        int numRooms = 5 + floor * 2;
        if (numRooms > 15) numRooms = 15;

        // Posiciona salas em grid espalhado, garantindo conectividade
        int gridW = 4;
        int gridH = 4;
        int[][] positions = new int[numRooms][2];

        for (int i = 0; i < numRooms; i++) {
            positions[i][0] = random.nextInt(gridW);
            positions[i][1] = random.nextInt(gridH);
            // Evita duplicatas
            for (int j = 0; j < i; j++) {
                if (positions[j][0] == positions[i][0] && positions[j][1] == positions[i][1]) {
                    positions[i][0] = random.nextInt(gridW);
                    positions[i][1] = random.nextInt(gridH);
                    j = -1; // restart check
                }
            }
        }

        ArrayList<Room> rooms = new ArrayList<>();
        for (int i = 0; i < numRooms; i++) {
            String name = generateRoomName(i);
            String desc = generateDescription(i);
            Room room = new Room(i, name, desc);
            room.setMapX(positions[i][0]);
            room.setMapY(positions[i][1]);
            rooms.add(room);
            graph.addRoom(room);
        }

        // Garante conectividade com vizinhos mais próximos
        for (int i = 1; i < numRooms; i++) {
            int bestIdx = -1;
            int bestDist = Integer.MAX_VALUE;
            for (int j = 0; j < i; j++) {
                int dx = Math.abs(positions[i][0] - positions[j][0]);
                int dy = Math.abs(positions[i][1] - positions[j][1]);
                int dist = dx + dy;
                if (dist < bestDist) {
                    bestDist = dist;
                    bestIdx = j;
                }
            }
            if (bestIdx != -1 && bestDist <= 2) {
                graph.connect(rooms.get(i), rooms.get(bestIdx));
            } else if (bestIdx != -1) {
                // Força conexão mesmo distante para garantir conectividade
                graph.connect(rooms.get(i), rooms.get(bestIdx));
            }
        }

        int extraConnections = Math.min(floor, 3);
        for (int i = 0; i < extraConnections; i++) {
            int a = random.nextInt(numRooms);
            int b = random.nextInt(numRooms);
            if (a != b) {
                graph.connect(rooms.get(a), rooms.get(b));
            }
        }

        return graph;
    }

    private String generateRoomName(int index) {
        String[] names = {
            "Entrada", "Corredor", "Sala Escura", "Caverna", "Túnel",
            "Arena", "Sala do Tesouro", "Passagem", "Antecâmara",
            "Sala Central", "Galeria", "Cripta", "Sala Secreta",
            "Templo", "Covil", "Sala dos Ossos", "Lugar Abandonado"
        };
        return names[index % names.length];
    }

    private String generateDescription(int index) {
        String[] descs = {
            "Uma sala escura e úmida.", "O chão é coberto de musgo.",
            "Você ouve sons estranhos.", "Uma passagem estreita.",
            "A sala parece antiga.", "Há marcas nas paredes.",
            "O ar é pesado aqui.", "Uma sala de batalha.",
            "Algo brilha no canto.", "O silêncio é ensurdecedor."
        };
        return descs[index % descs.length];
    }
}
