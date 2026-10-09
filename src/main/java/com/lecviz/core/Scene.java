package com.lecviz.core;

import com.lecviz.utils.Colors;
import com.lecviz.utils.Easing;
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

    // Canvas size: 1080p landscape by default; a scene that wants another shape (for example a vertical 1080x1920
    // short) overrides canvasWidth() / canvasHeight(). The values are set for the duration of render().
    public static int WIDTH  = 1920;
    public static int HEIGHT = 1080;
    public static final int FPS    = 60;  // 60fps for smooth animations

    /** Seconds of video rendered so far; objects can read it for idle animation (blinking, breathing). */
    public static volatile double clock = 0;

    protected Canvas canvas;
    protected GraphicsContext gc;
    protected VideoRenderer videoRenderer;
    protected Color backgroundColor = Colors.BACKGROUND;

    // 3B1B style gradient background
    private boolean useGradientBackground = true;
    private Color gradientCenter = Color.web("#12141F");
    private Color gradientEdge = Colors.DARK_BG;

    private final List<MObject> mobjects = new ArrayList<>();
    private boolean recording = false;
    private String outputPath = "output.mp4";

    // --- Camera (pan/zoom) ---
    // (cameraX, cameraY) is the scene point centered on screen; cameraZoom
    // scales everything around that point. Defaults (0, 0, 1) match the
    // untransformed full-canvas framing every scene starts in.
    private double cameraX = 0, cameraY = 0, cameraZoom = 1.0;

    // --- Frame clock and sound log ---
    private long frameCount = 0;
    private final List<Sfx.Event> sfxEvents = new ArrayList<>();

    /** Canvas size of this scene; override both for a non-1080p-landscape video. */
    protected int canvasWidth() { return 1920; }
    protected int canvasHeight() { return 1080; }

    /** Target video bitrate in bits per second; busy pixel-art scenes can ask for less to keep files small. */
    protected int videoBitrate() { return 20_000_000; }

    /** Time of the next frame to be rendered, in seconds. */
    protected double now() { return frameCount / (double) FPS; }

    /** Logs a sound effect at the current time (see {@link Sfx}); {@code pan} runs from -1 (left) to 1 (right). */
    protected void sfx(String name, double a, double pan) {
        if (recording) sfxEvents.add(new Sfx.Event(now(), name, a, pan));
    }

    protected void sfx(String name) { sfx(name, 0, 0); }

    /** Like {@link #sfx(String, double, double)} but {@code delay} seconds from now (for sounds inside a running animation). */
    protected void sfxAt(double delay, String name, double a, double pan) {
        if (recording) sfxEvents.add(new Sfx.Event(now() + delay, name, a, pan));
    }

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

    /**
     * Animate the camera to focus on scene point (x, y) at the given zoom
     * level, holding the shot for the duration. This is what lets a scene
     * push in on a detail and pull back out for context instead of every
     * mobject living at a fixed 1:1 scale forever.
     */
    public void cameraTo(double x, double y, double zoom, double duration) {
        double startX = cameraX, startY = cameraY, startZoom = cameraZoom;
        int frames = Math.max(1, (int) (duration * FPS));
        for (int i = 1; i <= frames; i++) {
            double t = Easing.SMOOTH.applyAsDouble(i / (double) frames);
            cameraX = startX + (x - startX) * t;
            cameraY = startY + (y - startY) * t;
            cameraZoom = startZoom + (zoom - startZoom) * t;
            renderFrame();
        }
    }

    public void resetCamera(double duration) {
        cameraTo(0, 0, 1.0, duration);
    }

    /**
     * Like cameraTo, but drives the given animations on the same frame
     * loop so a camera pan/zoom and, say, an object fading out happen in
     * the same breath instead of one after the other.
     */
    public void cameraToWith(double x, double y, double zoom, double duration, Animation... animations) {
        double startX = cameraX, startY = cameraY, startZoom = cameraZoom;
        for (Animation a : animations) {
            if (!mobjects.contains(a.getTarget())) mobjects.add(a.getTarget());
            a.begin();
        }
        int frames = Math.max(1, (int) (duration * FPS));
        double dt = 1.0 / FPS;
        for (int i = 1; i <= frames; i++) {
            double t = Easing.SMOOTH.applyAsDouble(i / (double) frames);
            cameraX = startX + (x - startX) * t;
            cameraY = startY + (y - startY) * t;
            cameraZoom = startZoom + (zoom - startZoom) * t;
            for (Animation a : animations) {
                if (!a.isFinished()) a.update(dt);
            }
            renderFrame();
        }
    }

    // --- Rendering ---

    private void renderFrame() {
        clock = frameCount / (double) FPS;
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

        gc.save();
        gc.translate(WIDTH / 2.0, HEIGHT / 2.0);
        gc.scale(cameraZoom, cameraZoom);
        gc.translate(-cameraX - WIDTH / 2.0, -cameraY - HEIGHT / 2.0);
        for (MObject obj : mobjects) {
            obj.render(gc);
        }
        gc.restore();

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
        frameCount++;
    }

    // --- Execution ---

    public void render() {
        int oldW = WIDTH, oldH = HEIGHT;
        WIDTH = canvasWidth();
        HEIGHT = canvasHeight();
        canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();
        gc.setFont(Font.font("SansSerif", 24));

        try {
            videoRenderer = new VideoRenderer(WIDTH, HEIGHT, FPS, outputPath, videoBitrate());
            videoRenderer.start();
            recording = true;

            System.out.println("[Scene] Building scene: " + this.getClass().getSimpleName());
            construct();

            videoRenderer.stop();
            recording = false;
            if (!sfxEvents.isEmpty()) AudioMux.mux(outputPath, sfxEvents, frameCount / (double) FPS);
            System.out.println("[Scene] Done. Video saved to " + outputPath);

        } catch (Exception e) {
            System.err.println("[Scene] Rendering error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            WIDTH = oldW;
            HEIGHT = oldH;
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
