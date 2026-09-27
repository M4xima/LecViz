package com.lecviz.mobjects;

import com.lecviz.core.MObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CircleMob extends MObject {

    private double radius;

    public CircleMob(double radius) {
        this.radius = radius;
    }

    public double getRadius() { return radius; }
    public CircleMob setRadius(double r) { this.radius = r; return this; }

    @Override
    protected void draw(GraphicsContext gc) {
        if (fillColor != null && !fillColor.equals(Color.TRANSPARENT)) {
            gc.setFill(fillColor);
            gc.fillOval(-radius, -radius, 2 * radius, 2 * radius);
        }
        if (strokeColor != null && !strokeColor.equals(Color.TRANSPARENT)) {
            gc.setStroke(strokeColor);
            gc.setLineWidth(strokeWidth);
            gc.strokeOval(-radius, -radius, 2 * radius, 2 * radius);
        }
    }

    @Override
    public MObject copy() {
        CircleMob c = new CircleMob(radius);
        copyBaseProperties(c);
        return c;
    }
}
