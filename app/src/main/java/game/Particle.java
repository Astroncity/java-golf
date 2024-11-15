package game;

import java.util.ArrayList;
import static com.raylib.Jaylib.Vector2;
import static com.raylib.Jaylib.Color;
import static com.raylib.Raylib.*;
import dataClasses.CollisionData;

public class Particle {
    private float x, y;
    private float rot; // NOTE: In degrees
    private Color color;
    private float speed;
    public boolean processed = false; //

    public static final float rad = 10;
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 450;
    private static final int ROTATION_ANGLE = 90;

    private static final ArrayList<Particle> particles = new ArrayList<>();

    public Particle(float x, float y, Color col, float rot, float speed) {
        this.x = x;
        this.y = y;
        this.rot = rot;
        this.speed = speed;
        this.color = col;
        particles.add(this);
    }

    public boolean collidedWall() {
        return (x - rad < 0 || x + rad > SCREEN_WIDTH || y - rad < 0 || y + rad > SCREEN_HEIGHT);
    }

    public Vector2 pos() {
        return new Vector2(x, y);
    }

    public void pos(Vector2 newPos) {
        this.x = newPos.x();
        this.y = newPos.y();
    }

    // INFO: Check for collisions with other particles
    public CollisionData collidedOther() {
        for (Particle p : particles) {
            if (p != this && CheckCollisionCircles(new Vector2(x, y), rad, new Vector2(p.x, p.y), rad)) {
                return new CollisionData(p, true);
            }
        }
        return new CollisionData(null, false);
    }

    // TODO: Find good rotation angle
    private void rotate() {
        rot = (rot + ROTATION_ANGLE) % 360;
    }

    private void handleParticleCollision(Particle other) {
        rotate();
        if (!processed && !other.processed) {
            float avgSpeed = (this.speed + other.speed) / 2;
            this.speed = avgSpeed;
            other.speed = avgSpeed;
            this.processed = true;
            other.processed = true;
        } else {
            this.processed = false;
            other.processed = false;
        }
    }

    private void updatePosition() {
        float frameTime = GetFrameTime();
        this.x += Math.cos(Math.toRadians(rot)) * speed * frameTime;
        this.y += Math.sin(Math.toRadians(rot)) * speed * frameTime;
    }

    private void draw() {
        DrawCircle((int) x, (int) y, rad, color);
    }

    public static void runParticles(Goal goal) {
        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);

            if (p.collidedWall()) {
                p.rotate();
            }

            CollisionData other = p.collidedOther();
            if (other.hit) {
                p.handleParticleCollision(other.other);
            }

            if (goal.score(new Vector2(p.x, p.y))) {
                particles.remove(i);
                i--;
                continue;
            }

            p.updatePosition();
            p.draw();
        }
    }

    public static void clearParticles() {
        particles.removeIf(t -> true);
    }
}
