package com.lecviz.utils;

/**
 * Immutable 2D vector for positions, sizes, and directions.
 */
public record Vec2(double x, double y) {

    public static final Vec2 ZERO   = new Vec2(0, 0);
    public static final Vec2 UP     = new Vec2(0, -1);
    public static final Vec2 DOWN   = new Vec2(0, 1);
    public static final Vec2 LEFT   = new Vec2(-1, 0);
    public static final Vec2 RIGHT  = new Vec2(1, 0);
    public static final Vec2 CENTER = ZERO;

    public Vec2 add(Vec2 o)         { return new Vec2(x + o.x, y + o.y); }
    public Vec2 sub(Vec2 o)         { return new Vec2(x - o.x, y - o.y); }
    public Vec2 scale(double s)     { return new Vec2(x * s, y * s); }
    public Vec2 scale(double sx, double sy) { return new Vec2(x * sx, y * sy); }
    public double length()          { return Math.sqrt(x * x + y * y); }
    public Vec2 normalize()         { double l = length(); return l == 0 ? ZERO : scale(1 / l); }
    public double dot(Vec2 o)       { return x * o.x + y * o.y; }
    public double angleTo(Vec2 o)   { return Math.atan2(o.y - y, o.x - x); }
    public double distanceTo(Vec2 o){ return sub(o).length(); }

    public Vec2 lerp(Vec2 to, double t) {
        return new Vec2(x + (to.x - x) * t, y + (to.y - y) * t);
    }

    @Override
    public String toString() {
        return String.format("(%.1f, %.1f)", x, y);
    }
}
