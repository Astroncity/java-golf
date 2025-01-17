package game;

import java.io.BufferedReader;
import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import static com.raylib.Raylib.*;
import static com.raylib.Jaylib.Vector2;
import static com.raylib.Jaylib.WHITE;
import static com.raylib.Jaylib.BLUE;
import static com.raylib.Jaylib.Color;
import static com.raylib.Jaylib.Rectangle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;

class Cell {
    public int x;
    public int y;
    public boolean isWall = false;
    public boolean isStart = false;
    public boolean isEnd = false;
    public boolean isPath = false;

    public Cell(int x, int y, char c) {
        this.x = x;
        this.y = y;

        if (c == '#') {
            isWall = true;
        }
        if (c == 'E') {
            isEnd = true;
        }
        if (c == 'S') {
            isStart = true;
        }
    }

    public void draw() {
        Color color = Theme.Background;
        if (isWall)
            color = Theme.Aqua;
        if (isStart)
            color = Theme.Green;
        if (isEnd)
            color = Theme.Red;
        if (isPath)
            color = Theme.Purple;

        DrawRectangle(x * 60, y * 60, 60, 60, color);
    }
}

public class Main {
    public static int windowWidth = 1920;
    public static int windowHeight = 1080;
    public static int targetFPS = 60;
    public static Cell[][] grid = new Cell[18][32];
    public static String filePath = "/home/astro/projects/csa/java_pathfinder/grid.txt";

    public static void drawGrid() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j].draw();
            }
        }
    }

    public static void init() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            int r = 0;
            while ((line = reader.readLine()) != null) {
                char[] chars = {};
                chars = line.toCharArray();

                for (int c = 0; c < chars.length; c++) {
                    grid[r][c] = new Cell(c, r, chars[c]);
                }
                r++;
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void pathfind() {
        // todo
    }

    public static void main(String[] args) {

        InitWindow(windowWidth, windowHeight, "PathFinder");
        SetTargetFPS(targetFPS);
        SetTraceLogLevel(LOG_ERROR);

        init();
        while (!WindowShouldClose()) {
            BeginDrawing();
            ClearBackground(Theme.Background);
            drawGrid();

            EndDrawing();
        }
    }
}
