package dataClasses;

import game.Particle;
import static com.raylib.Jaylib.Vector2;

public class CollisionData {
    public Particle other;
    public boolean hit;

    public CollisionData(Particle o, boolean h) {
        other = o;
        hit = h;
    }

    public static Vector2 difference(float x1, float y1, float x2, float y2) {
        return new Vector2(x1 - x2, y1 - y2);
    }

    public static float mag(Vector2 a, Vector2 b) {
        return (float) Math.sqrt(Math.pow(a.x() - b.x(), 2) + Math.pow(a.y() - b.y(), 2));
    }
}
