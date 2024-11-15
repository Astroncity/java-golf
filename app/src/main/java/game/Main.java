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

import dataClasses.*;
import java.util.function.Function;

class Goal {
    private final int radius;
    private Vector2 pos;
    private final Launcher launcher;
    private final String label = "[GOAL]";
    private boolean moving = false;
    private Vector2 target;
    private static final int bounds = 250;
    private float timer = 0;

    public Goal(Launcher l, int x, int y, int r) {
        pos = new Vector2(x, y);
        radius = r;
        launcher = l;
    }

    public static float lerp(float a, float b, float t) {
        return a + t * (b - a);
    }

    public static Vector2 lerpV2(Vector2 a, Vector2 b, float t) {
        return new Vector2(lerp(a.x(), b.x(), t), lerp(a.y(), b.y(), t));
    }

    public void draw() {
        DrawCircleV(pos, radius, Theme.Green);

        int xOffset = 25 + radius / 2;
        int yOffset = 35 + radius / 2;
        timer += GetFrameTime();

        DrawText(label, (int) pos.x() - xOffset, (int) pos.y() - yOffset, 20, Theme.Green);

        if (moving && timer >= 2) {
            target = new Vector2(pos.x() + GetRandomValue(-bounds, bounds), pos.y() + GetRandomValue(-bounds, bounds));
            while (!CheckCollisionPointRec(target, new Rectangle(0, 0, 800, 450))) {

                target = new Vector2(pos.x() + GetRandomValue(-bounds, bounds),
                        pos.y() + GetRandomValue(-bounds, bounds));
            }
            timer = 0;
        }

        if (moving && target != null) {
            pos = lerpV2(pos, target, GetFrameTime());
        }
    }

    public boolean score(Vector2 p) {
        if (CheckCollisionCircles(p, Particle.rad, pos, radius)) {
            launcher.increment();
            Main.timer = 10;
            pos = new Vector2(
                    GetRandomValue(radius, 800 - radius),
                    GetRandomValue(radius, 450 - radius));

            target = new Vector2(pos.x() + GetRandomValue(-bounds, bounds), pos.y() + GetRandomValue(-bounds, bounds));
            if (launcher.score() > Main.highscore && launcher.score() > 5) {
                moving = true;
            }

            return true;
        }
        return false;
    }
}

class Launcher {
    public int score = 0;

    public void launch(float mag, float ang) {
        Vector2 pos = new Vector2(GetMouseX(), GetMouseY());
        new Particle(pos.x(), pos.y(), Theme.Purple, ang, mag);
        pos.close();
    }

    public static LaunchData getLaunchData(Vector2 start) {
        Vector2 end = new Vector2(GetMouseX(), GetMouseY());

        float mag = CollisionData.mag(start, end);
        float ang = (float) Math.toDegrees(Math.atan2(end.y() - start.y(), end.x() - start.x()) + Math.PI);
        end.close();

        return new LaunchData(mag, ang);
    }

    public static float easeFunc(float x) {
        return (x * x) / 50 + x;
    }

    public int score() {
        return score;
    }

    public void increment() {
        score++;
    }
}

enum GAME_STATE {
    MENU,
    GAME,
    NAN
};

class Button {
    private int ox, oy;
    private int x, y;
    private String text;
    private Function<Integer, Integer> callback;
    private Color col;
    private float xlen, ylen;

    private int width;
    private int height;
    private static float rad = 0.2f;
    private static Font fn;
    private static float spacing = 4f;

    public Button(int x, int y, String text, Function<Integer, Integer> func) {
        ox = x;
        oy = y;
        this.x = x;
        this.y = y;
        this.text = text;
        callback = func;
        col = Theme.Dark2;
        if (fn == null) {
            fn = LoadFont("../../resources/VictorMonoNerdFontMono-Regular.ttf");
        }
        xlen = MeasureTextEx(fn, text, 50, spacing).x();
        ylen = MeasureTextEx(fn, text, 50, spacing).y();

        setSize();

    }

    public void update(String text) {
        this.text = text;
        xlen = MeasureTextEx(fn, text, 50, spacing).x();
        ylen = MeasureTextEx(fn, text, 50, spacing).y();
        setSize();
    }

    public String text() {
        return text;
    }

    public void pos(Vector2 pos) {
        x = (int) pos.x();
        y = (int) pos.y();
        ox = x;
        oy = y;
        setSize();
    }

    private void setSize() {
        /*
         * width = (int) (xlen * 1.2);
         * height = (int) (ylen * 1.3);
         * 
         * x = (int) (ox * 0.9);
         * y = (int) (oy * 0.8);
         */
        int offset = 20;
        width = (int) xlen + offset * 2;
        height = (int) ylen + offset;

        x = ox - offset;
        y = oy - offset / 2;
    }

    public Vector2 midpoint() {
        return new Vector2(width / 2, height / 2);
    }

    public void draw() {
        DrawRectangleRounded(new Rectangle(x - 1, y + 2, width + 2, height), rad, 3, Theme.Green);
        DrawRectangleRounded(new Rectangle(x, y, width, height), rad, 3, col);
        drawText();
        collide();

        if (IsKeyPressed(KEY_G)) {
            rad += 0.1;
            System.out.println(rad);
        }
    }

    private void drawText() {
        int offsety = (int) ((height - ylen) / 2);
        int offsetx = (int) ((width - xlen) / 2);

        DrawTextEx(fn, text, new Vector2(x + offsetx, y + offsety), 50, spacing, WHITE);
    }

    private void collide() {
        int mx = GetMouseX();
        int my = GetMouseY();

        if (mx > x && mx < x + width && my > y && my < y + height) {
            col = Theme.Dark3;
            if (IsMouseButtonPressed(0)) {
                callback.apply(0);
            }
        } else {
            col = Theme.Dark2;
        }
    }
}

public class Main {
    public static GAME_STATE state = GAME_STATE.MENU;
    public static int highscore = 0;
    public static double timer = 10;
    public static String savePath = "/home/astro/temp/golfSave.txt";
    public static Launcher launch;

    public static Color clone(Color c) {
        return new Color(c.r(), c.g(), c.b(), c.a());
    }

    public static int playButton(Integer a) {
        System.out.println("clicked");
        state = GAME_STATE.GAME;
        return 0;
    }

    public static void loadGame() {
        try (BufferedReader reader = new BufferedReader(new FileReader(savePath))) {
            String firstLine = reader.readLine();
            highscore = Integer.parseInt(firstLine);
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    public static void endGame() {
        System.out.println("Score: " + launch.score() + " Highscore: " + highscore);
        if (launch.score() > highscore) {
            highscore = launch.score();

            try (FileWriter writer = new java.io.FileWriter(savePath)) {
                writer.write(String.valueOf(highscore));
            } catch (IOException e) {
                System.err.println("Error writing to file: " + e.getMessage());
            }
        }
        launch.score = 0;
        timer = 10;
        state = GAME_STATE.MENU;

        Particle.clearParticles();
    }

    public static void main(String[] args) {
        final int windowWidth = 800;
        final int windowHeight = 450;
        final int targetFPS = 60;
        final int startScoreX = 380;
        final int startScoreY = 200;

        InitWindow(windowWidth, windowHeight, "[RAYLIB] Unit 2 & 5 Evd.");
        SetTargetFPS(targetFPS);
        SetTraceLogLevel(LOG_ERROR);

        launch = new Launcher();
        Goal goal = new Goal(launch, 100, 50, 20);
        Vector2 start = new Vector2(-1, -1);

        Button play = new Button(300, 200, "PLAY", Main::playButton);

        play.pos(new Vector2(windowWidth / 2 - play.midpoint().x() + 15, 200));
        loadGame();

        while (!WindowShouldClose()) {
            BeginDrawing();
            ClearBackground(Theme.Background);

            // alignment
            // DrawLineEx(new Vector2(0, 225), new Vector2(800, 225), 4, Theme.Red);
            // DrawLineEx(new Vector2(400, 0), new Vector2(400, 450), 4, Theme.Red);

            if (state == GAME_STATE.GAME) {
                if (timer <= 0) {
                    endGame();
                }
                drawScore(launch, startScoreX, startScoreY);
                timer -= GetFrameTime();
                DrawText("" + (int) timer, windowWidth / 2, 30, 30, Theme.Red);

                if (IsMouseButtonPressed(0)) {
                    if (start.x() == -1) {
                        start = new Vector2(GetMouseX(), GetMouseY());
                    }
                }

                if (start.x() != -1) {
                    drawLaunchLine(start);
                }

                if (IsMouseButtonReleased(0) && start.x() != -1) {
                    LaunchData ld = Launcher.getLaunchData(start);
                    launch.launch(Launcher.easeFunc(ld.mag), ld.ang);
                    start = new Vector2(-1, -1);
                }

                goal.draw();
                Particle.runParticles(goal);
            } else if (state == GAME_STATE.MENU) {
                play.draw();
                DrawText("Highscore: " + highscore, 250, 380, 45, Theme.Green);
                DrawText("Golf", windowWidth / 2 - MeasureText("Golf", 100) / 2, 40, 100,
                        new Color(Theme.Red.r(), Theme.Red.g(), Theme.Red.b(), 100));
                DrawText("Golf", windowWidth / 2 - MeasureText("Golf", 100) / 2, 43, 100, Theme.Red);
            }

            EndDrawing();
        }
    }

    private static void drawScore(Launcher launch, int x, int y) {
        Color textCol = clone(Theme.Blue);
        DrawText(String.valueOf(launch.score()), x, y, 60, textCol);
        textCol.a((byte) 120);
        DrawText(String.valueOf(launch.score()), x, y + 6, 60, textCol);
    }

    private static void drawLaunchLine(Vector2 start) {
        float dist = CollisionData.mag(new Vector2(GetMouseX(), GetMouseY()), start);

        Color lineCol = dist > 200 ? Theme.Red : dist > 100 ? Theme.Yellow : Theme.Blue;

        DrawLineEx(start, new Vector2(GetMouseX(), GetMouseY()), 5, lineCol);
        DrawText("Power: " + (int) dist + "(" + Launcher.easeFunc(dist) + ")", (int) start.x() + 10,
                (int) start.y() + 10, 20, lineCol);
    }
}
