package com.lecviz.core;

import com.lecviz.utils.Colors;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * The main scene class — 3Blue1Brown style.
 * Subclass this and override construct() to build your animation.
 *
 * Improvements over basic version:
 *   - 60fps smooth animations
 *   - Radial gradient background (3B1B signature look)
 *   - Antialiased rendering
 *   - Support for LaTeX formulas (via LaTeXMob)
 */
public abstract class Scene {

    // 1080p canvas
    public static final int WIDTH  = 1920;
    public static final int HEIGHT = 1080;
    public static final int FPS    = 60;  // 60fps for smooth animations

    protected Canvas canvas;
    protected GraphicsContext gc;
    protected VideoRenderer videoRenderer;
    protected Color backgroundColor = Colors.BACKGROUND;

    // 3B1B style gradient background
    private boolean useGradientBackground = true;
    private Color gradientCenter = Color.web("#232345");
    private Color gradientEdge = Colors.BACKGROUND;

    private final List<MObject> mobjects = new ArrayList<>();
    private boolean recording = false;
    private String outputPath = "output.mp4";

    // --- Setup ---

    public Scene() {}

    public Scene setOutputPath(String path) {
        this.outputPath = path;
        return this;
    }

    public Scene setBackgroundColor(Color c) {
        this.backgroundColor = c;
        return this;
    }

    public Scene setUseGradientBackground(boolean use) {
        this.useGradientBackground = use;
        return this;
    }

    public Scene setGradientColors(Color center, Color edge) {
        this.gradientCenter = center;
        this.gradientEdge = edge;
        return this;
    }

    /**
     * Override this method to define your animation sequence.
     */
    public abstract void construct();

    // --- Object management ---

    public void add(MObject... objects) {
        for (MObject obj : objects) {
            if (!mobjects.contains(obj)) {
                mobjects.add(obj);
            }
        }
    }

    public void remove(MObject... objects) {
        for (MObject obj : objects) {
            mobjects.remove(obj);
        }
    }

    public void clear() {
        mobjects.clear();
    }

    // --- Animation playback ---

    public void play(Animation animation) {
        if (!mobjects.contains(animation.getTarget())) {
            mobjects.add(animation.getTarget());
        }

        animation.begin();
        double dt = 1.0 / FPS;
        while (animation.update(dt)) {
            renderFrame();
        }
        renderFrame();
    }

    public void play(Animation... animations) {
        for (Animation a : animations) {
            if (!mobjects.contains(a.getTarget())) {
                mobjects.add(a.getTarget());
            }
            a.begin();
        }

        double dt = 1.0 / FPS;
        boolean anyRunning = true;
        while (anyRunning) {
            anyRunning = false;
            for (Animation a : animations) {
                if (!a.isFinished() && a.update(dt)) {
                    anyRunning = true;
                }
            }
            renderFrame();
        }
        renderFrame();
    }

    public void hold(double seconds) {
        int frames = (int) (seconds * FPS);
        for (int i = 0; i < frames; i++) {
            renderFrame();
        }
    }

    public void waitSeconds(double seconds) {
        hold(seconds);
    }

    // --- Rendering ---

    private void renderFrame() {
        if (useGradientBackground) {
            RadialGradient gradient = new RadialGradient(
                0, 0,
                WIDTH / 2.0, HEIGHT / 2.0,
                Math.max(WIDTH, HEIGHT) * 0.7,
                false, CycleMethod.NO_CYCLE,
                new Stop(0.0, gradientCenter),
                new Stop(1.0, gradientEdge)
            );
            gc.setFill(gradient);
        } else {
            gc.setFill(backgroundColor);
        }
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        for (MObject obj : mobjects) {
            obj.render(gc);
        }

        if (recording && videoRenderer != null) {
            try {
                WritableImage fxImage = new WritableImage(WIDTH, HEIGHT);
                canvas.snapshot(null, fxImage);
                BufferedImage bImg = SwingFXUtils.fromFXImage(fxImage, null);
                videoRenderer.captureFrame(bImg);
            } catch (Exception e) {
                System.err.println("[Scene] Frame capture error: " + e.getMessage());
            }
        }
    }

    // --- Execution ---

    public void render() {
        canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();
        gc.setFont(Font.font("SansSerif", 24));

        try {
            videoRenderer = new VideoRenderer(WIDTH, HEIGHT, FPS, outputPath);
            videoRenderer.start();
            recording = true;

            System.out.println("[Scene] Building scene: " + this.getClass().getSimpleName());
            construct();

            videoRenderer.stop();
            recording = false;
            System.out.println("[Scene] Done. Video saved to " + outputPath);

        } catch (Exception e) {
            System.err.println("[Scene] Rendering error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void preview(Canvas previewCanvas) {
        this.canvas = previewCanvas;
        this.gc = canvas.getGraphicsContext2D();
        gc.setFont(Font.font("SansSerif", 24));
        recording = false;
        construct();
    }
}
