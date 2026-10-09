package com.lecviz.mobjects;

import com.lecviz.core.MObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;

/**
 * A burst of square pixel particles that fly out from a point, fall under gravity and fade. Drive it by setting the
 * time with {@link #setTime(double)} (seconds since the burst).
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class Confetti extends MObject {

    private final double[] vx, vy, size, spin;
    private final Color[] col;
    private final double life, gravity;
    private double t = 0;

    public Confetti(int n, double speed, double life, double gravity, double minSize, double maxSize, long seed, Color... palette) {
        Random r = new Random(seed);
        vx = new double[n];
        vy = new double[n];
        size = new double[n];
        spin = new double[n];
        col = new Color[n];
        this.life = life;
        this.gravity = gravity;
        for (int i = 0; i < n; i++) {
            double a = r.nextDouble() * 2 * Math.PI;
            double v = speed * (0.35 + 0.65 * r.nextDouble());
            vx[i] = Math.cos(a) * v;
            vy[i] = Math.sin(a) * v - speed * 0.35;
            size[i] = minSize + r.nextDouble() * (maxSize - minSize);
            spin[i] = r.nextDouble() * 6;
            col[i] = palette[r.nextInt(palette.length)];
        }
        this.strokeColor = Color.TRANSPARENT;
    }

    public double getLife() { return life; }

    public Confetti setTime(double seconds) {
        this.t = seconds;
        return this;
    }

    @Override
    protected void draw(GraphicsContext gc) {
        if (t < 0 || t > life) return;
        double fade = Math.max(0, 1 - Math.pow(t / life, 2.2));
        for (int i = 0; i < vx.length; i++) {
            double x = vx[i] * t;
            double y = vy[i] * t + 0.5 * gravity * t * t;
            gc.setGlobalAlpha(Math.min(1, opacity) * fade);
            gc.setFill(col[i]);
            double s = size[i];
            gc.fillRect(Math.round(x / s) * s, Math.round(y / s) * s, s, s);
        }
        gc.setGlobalAlpha(opacity);
    }

    @Override
    public MObject copy() {
        Confetti c = new Confetti(vx.length, 100, life, gravity, 6, 10, 1, Color.WHITE);
        copyBaseProperties(c);
        return c;
    }
}
