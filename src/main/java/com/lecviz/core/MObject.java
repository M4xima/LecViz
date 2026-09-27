package com.lecviz.core;

import com.lecviz.utils.Vec2;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all mathematical/visual objects on screen.
 * Equivalent to Manim's Mobject.
 */
public abstract class MObject {

    protected Vec2 position = Vec2.ZERO;
    protected double opacity = 1.0;
    protected double scale = 1.0;
    protected double rotation = 0.0; // radians
    protected Color strokeColor = Color.WHITE;
    protected Color fillColor = Color.TRANSPARENT;
    protected double strokeWidth = 2.0;
    protected List<MObject> children = new ArrayList<>();

    // --- Position ---

    public MObject setPosition(Vec2 pos) { this.position = pos; return this; }
    public MObject setPosition(double x, double y) { this.position = new Vec2(x, y); return this; }
    public Vec2 getPosition() { return position; }

    public MObject moveTo(double x, double y) { return setPosition(x, y); }
    public MObject moveTo(Vec2 pos) { return setPosition(pos); }

    public MObject shift(double dx, double dy) {
        this.position = position.add(new Vec2(dx, dy));
        return this;
    }
    public MObject shift(Vec2 delta) { return shift(delta.x(), delta.y()); }

    // Anchor-relative positioning
    public MObject toEdge(Vec2 direction, double margin) {
        double x = position.x(), y = position.y();
        if (direction.x() != 0) x = direction.x() * (Scene.WIDTH / 2.0 - margin);
        if (direction.y() != 0) y = direction.y() * (Scene.HEIGHT / 2.0 - margin);
        return setPosition(x, y);
    }

    public MObject nextTo(MObject other, Vec2 direction, double buffer) {
        Vec2 oPos = other.getPosition();
        return setPosition(oPos.add(direction.scale(buffer)));
    }

    // --- Style ---

    public MObject setOpacity(double o) { this.opacity = Math.max(0, Math.min(1, o)); return this; }
    public double getOpacity() { return opacity; }

    public MObject setScale(double s) { this.scale = s; return this; }
    public double getScale() { return scale; }

    public MObject setRotation(double r) { this.rotation = r; return this; }
    public double getRotation() { return rotation; }

    public MObject setStrokeColor(Color c) { this.strokeColor = c; return this; }
    public Color getStrokeColor() { return strokeColor; }

    public MObject setFillColor(Color c) { this.fillColor = c; return this; }
    public Color getFillColor() { return fillColor; }

    public MObject setStrokeWidth(double w) { this.strokeWidth = w; return this; }
    public double getStrokeWidth() { return strokeWidth; }

    // --- Children ---

    public MObject add(MObject child) { children.add(child); return this; }
    public List<MObject> getChildren() { return children; }

    // --- Rendering ---

    /**
     * Render this object to the canvas. Called every frame.
     * Coordinate system: origin at center of canvas.
     */
    public void render(GraphicsContext gc) {
        if (opacity <= 0) return;

        gc.save();
        // Translate to scene-center-based coordinates
        gc.translate(Scene.WIDTH / 2.0 + position.x(), Scene.HEIGHT / 2.0 + position.y());
        gc.scale(scale, scale);
        gc.rotate(Math.toDegrees(rotation));
        gc.setGlobalAlpha(opacity);

        draw(gc);

        // Reset transform for children (they are positioned relative to parent)
        for (MObject child : children) {
            child.render(gc);
        }

        gc.restore();
    }

    /**
     * Override this to draw the specific shape/text.
     * Called with transform already applied.
     * Draw centered at (0,0).
     */
    protected abstract void draw(GraphicsContext gc);

    /**
     * Create a deep copy for animation interpolation.
     */
    public abstract MObject copy();

    protected void copyBaseProperties(MObject target) {
        target.position = this.position;
        target.opacity = this.opacity;
        target.scale = this.scale;
        target.rotation = this.rotation;
        target.strokeColor = this.strokeColor;
        target.fillColor = this.fillColor;
        target.strokeWidth = this.strokeWidth;
    }
}
