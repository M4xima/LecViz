package com.lecviz.mobjects;

import com.lecviz.core.MObject;
import com.lecviz.core.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * A pixel-art "slime" character that carries a number above its head: one chunky pixel grid for the body, a face
 * drawn on top (eight different default personalities, plus the moods OOF, JOY and DIZZY), a soft shadow that stays
 * on the ground while the slime is lifted, breathing and blinking that run on their own from {@link Scene#clock},
 * and squash and stretch for jumps.
 *
 * The position is the centre of the body when it rests on the ground ({@link #setGround}); {@link #setLift} raises it.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PixelSlime extends MObject {

    public enum Mood { NORMAL, OOF, JOY, DIZZY }

    private static final String[] BODY = {
            "....OOOOOOOO....",
            "..OOBBBBBBBBOO..",
            ".OBBBBBBBBBBBBO.",
            ".OBHHBBBBBBBBBO.",
            "OBBHBBBBBBBBBBBO",
            "OBBBBBBBBBBBBBBO",
            "OBBBBBBBBBBBBBBO",
            "OBBBBBBBBBBBBBSO",
            "OBBBBBBBBBBBBSSO",
            "OSSSSSSSSSSSSSSO",
            ".OOSSSSSSSSSSOO.",
            "..OOOOOOOOOOOO.."};
    private static final String[] DIGITS = {
            "111101101101111", "010110010010111", "111001111100111", "111001111001111", "101101111001001",
            "111100111001111", "111100111101111", "111001001010010", "111101111101111", "111101111001111"};
    private static final Color INK = Color.web("#1b1b2b");
    private static final Color TONGUE = Color.web("#ff8fa3");

    private final Color base, light, shade, outline;
    private final int face;
    private final double ps, phase;
    private int value;
    private Mood mood = Mood.NORMAL;
    private Color labelColor = Color.WHITE;
    private double squashX = 1, squashY = 1, lift = 0, groundY = 0;
    private boolean labelShown = true;

    public PixelSlime(int value, int face, Color base, double pixel) {
        this.value = value;
        this.face = face;
        this.base = base;
        this.light = base.interpolate(Color.WHITE, 0.42);
        this.shade = base.interpolate(Color.BLACK, 0.22);
        this.outline = base.interpolate(Color.BLACK, 0.55);
        this.ps = pixel;
        this.phase = (value * 0.37 + face * 0.19) % 1.0;
        this.strokeColor = Color.TRANSPARENT;
    }

    public double getPixel() { return ps; }
    public int getValue() { return value; }
    public int getFace() { return face; }
    public Color getBase() { return base; }
    public double getLift() { return lift; }
    public double getGround() { return groundY; }
    public Mood getMood() { return mood; }

    public PixelSlime setMood(Mood m) { this.mood = m; return this; }
    public PixelSlime setLabelColor(Color c) { this.labelColor = c; return this; }
    public PixelSlime setLabelShown(boolean b) { this.labelShown = b; return this; }
    public PixelSlime setSquash(double sx, double sy) { this.squashX = sx; this.squashY = sy; return this; }

    /** The y of the body centre when resting on the ground. */
    public PixelSlime setGround(double gy) {
        this.groundY = gy;
        position = new com.lecviz.utils.Vec2(position.x(), gy - lift);
        return this;
    }

    public PixelSlime setLift(double l) {
        this.lift = l;
        position = new com.lecviz.utils.Vec2(position.x(), groundY - l);
        return this;
    }

    /** Places the slime at x with the given lift above its ground. */
    public PixelSlime place(double x, double l) {
        this.lift = l;
        position = new com.lecviz.utils.Vec2(x, groundY - l);
        return this;
    }

    // ── drawing ──────────────────────────────────────────────────────

    private void px(GraphicsContext gc, int r, int c, Color col) {
        gc.setFill(col);
        gc.fillRect((c - 8) * ps - 0.35, (r - 6) * ps - 0.35, ps + 0.7, ps + 0.7);
    }

    private void block(GraphicsContext gc, int r, int c, int h, int w, Color col) {
        for (int i = 0; i < h; i++)
            for (int j = 0; j < w; j++) px(gc, r + i, c + j, col);
    }

    @Override
    protected void draw(GraphicsContext gc) {
        double t = Scene.clock;
        // the shadow stays on the ground: it is drawn lift pixels below the body, and gets smaller and fainter as the slime rises
        double sh = Math.max(0.45, 1 - lift / 520.0);
        gc.setFill(Color.rgb(0, 0, 0, 0.42 * Math.max(0.3, 1 - lift / 420.0)));
        gc.fillOval(-7.4 * ps * sh, 6 * ps + lift - 0.25 * ps, 14.8 * ps * sh, 1.0 * ps);

        double breath = Math.sin(2 * Math.PI * (t / 1.7 + phase));
        double sy = squashY * (1 + 0.028 * breath);
        double sx = squashX * (1 - 0.014 * breath);
        gc.save();
        gc.translate(0, 6 * ps);
        gc.scale(sx, sy);
        gc.translate(0, -6 * ps);
        for (int r = 0; r < BODY.length; r++) {
            String row = BODY[r];
            for (int c = 0; c < row.length(); c++) {
                char ch = row.charAt(c);
                if (ch == '.') continue;
                px(gc, r, c, ch == 'B' ? base : ch == 'H' ? light : ch == 'S' ? shade : outline);
            }
        }
        boolean blink = ((t + phase * 3.1) % 3.3) < 0.11;
        drawFace(gc, blink);
        gc.restore();

        if (labelShown) drawLabel(gc, (6 * ps) * sy);
    }

    private void drawFace(GraphicsContext gc, boolean blink) {
        int f = face;
        Mood m = mood;
        if (m == Mood.OOF) {
            block(gc, 5, 4, 3, 2, INK);
            block(gc, 5, 10, 3, 2, INK);
            px(gc, 5, 4, Color.WHITE);
            px(gc, 5, 10, Color.WHITE);
            block(gc, 8, 7, 2, 2, INK);
            return;
        }
        if (m == Mood.JOY) {
            happyEyes(gc);
            block(gc, 8, 6, 1, 4, INK);
            block(gc, 9, 7, 1, 2, INK);
            block(gc, 9, 7, 1, 2, TONGUE);
            return;
        }
        if (m == Mood.DIZZY) {
            px(gc, 5, 4, INK); px(gc, 6, 5, INK); px(gc, 5, 5, INK); px(gc, 6, 4, INK);
            px(gc, 5, 10, INK); px(gc, 6, 11, INK); px(gc, 5, 11, INK); px(gc, 6, 10, INK);
            block(gc, 8, 6, 1, 4, INK);
            return;
        }
        switch (f) {
            case 0 -> {                       // content
                eyes(gc, blink);
                px(gc, 8, 6, INK); px(gc, 9, 7, INK); px(gc, 9, 8, INK); px(gc, 8, 9, INK);
            }
            case 1 -> {                       // meh
                eyes(gc, blink);
                block(gc, 8, 6, 1, 4, INK);
            }
            case 2 -> {                       // squinting grin
                happyEyes(gc);
                block(gc, 8, 6, 1, 4, INK);
                block(gc, 9, 7, 1, 2, INK);
            }
            case 3 -> {                       // sleepy
                block(gc, 6, 4, 1, 2, INK);
                block(gc, 6, 10, 1, 2, INK);
                block(gc, 8, 7, 1, 2, INK);
            }
            case 4 -> {                       // grumpy
                eyes(gc, blink);
                px(gc, 3, 3, INK); px(gc, 4, 4, INK); px(gc, 4, 5, INK);
                px(gc, 3, 12, INK); px(gc, 4, 11, INK); px(gc, 4, 10, INK);
                px(gc, 9, 6, INK); px(gc, 8, 7, INK); px(gc, 8, 8, INK); px(gc, 9, 9, INK);
            }
            case 5 -> {                       // wink with tongue
                block(gc, 5, 4, 2, 2, INK);
                block(gc, 6, 10, 1, 2, INK);
                px(gc, 8, 6, INK); px(gc, 9, 7, INK); px(gc, 9, 8, INK); px(gc, 8, 9, INK);
                block(gc, 10, 7, 1, 2, TONGUE);
            }
            case 6 -> {                       // surprised
                block(gc, 5, 4, 3, 2, INK);
                block(gc, 5, 10, 3, 2, INK);
                px(gc, 5, 4, Color.WHITE);
                px(gc, 5, 10, Color.WHITE);
                block(gc, 8, 7, 2, 2, INK);
            }
            default -> {                      // cool
                block(gc, 5, 3, 2, 4, INK);
                block(gc, 5, 9, 2, 4, INK);
                px(gc, 5, 7, INK); px(gc, 5, 8, INK);
                px(gc, 5, 4, Color.web("#8a8aa8")); px(gc, 5, 10, Color.web("#8a8aa8"));
                px(gc, 8, 6, INK); px(gc, 9, 7, INK); px(gc, 9, 8, INK); px(gc, 8, 9, INK);
            }
        }
    }

    private void eyes(GraphicsContext gc, boolean blink) {
        if (blink) {
            block(gc, 6, 4, 1, 2, INK);
            block(gc, 6, 10, 1, 2, INK);
        } else {
            block(gc, 5, 4, 2, 2, INK);
            block(gc, 5, 10, 2, 2, INK);
        }
    }

    private void happyEyes(GraphicsContext gc) {
        px(gc, 5, 5, INK); px(gc, 6, 4, INK); px(gc, 6, 6, INK);
        px(gc, 5, 10, INK); px(gc, 6, 9, INK); px(gc, 6, 11, INK);
    }

    private void drawLabel(GraphicsContext gc, double bodyHalf) {
        String s = String.valueOf(value);
        double dp = ps * 0.95;
        double w = s.length() * 4 * dp - dp;
        double top = -bodyHalf - 7 - 5 * dp;
        for (int pass = 0; pass < 2; pass++) {
            gc.setFill(pass == 0 ? Color.rgb(0, 0, 0, 0.55) : labelColor);
            double off = pass == 0 ? dp * 0.45 : 0;
            for (int k = 0; k < s.length(); k++) {
                String g = DIGITS[s.charAt(k) - '0'];
                for (int i = 0; i < 15; i++) {
                    if (g.charAt(i) != '1') continue;
                    double x = -w / 2 + k * 4 * dp + (i % 3) * dp + off;
                    double y = top + (i / 3) * dp + off;
                    gc.fillRect(x - 0.3, y - 0.3, dp + 0.6, dp + 0.6);
                }
            }
        }
    }

    @Override
    public MObject copy() {
        PixelSlime c = new PixelSlime(value, face, base, ps);
        copyBaseProperties(c);
        c.mood = mood;
        c.lift = lift;
        c.groundY = groundY;
        return c;
    }
}
