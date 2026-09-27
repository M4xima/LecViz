package com.lecviz.mobjects;

import com.lecviz.core.MObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class RectMob extends MObject {

    private double width;
    private double height;
    private double cornerRadius = 0;

    public RectMob(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public RectMob setCornerRadius(double r) { this.cornerRadius = r; return this; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public RectMob setSize(double w, double h) { this.width = w; this.height = h; return this; }

    @Override
    protected void draw(GraphicsContext gc) {
        double x = -width / 2, y = -height / 2;

        if (fillColor != null && !fillColor.equals(Color.TRANSPARENT)) {
            gc.setFill(fillColor);
            if (cornerRadius > 0) gc.fillRoundRect(x, y, width, height, cornerRadius, cornerRadius);
            else gc.fillRect(x, y, width, height);
        }
        if (strokeColor != null && !strokeColor.equals(Color.TRANSPARENT)) {
            gc.setStroke(strokeColor);
            gc.setLineWidth(strokeWidth);
            if (cornerRadius > 0) gc.strokeRoundRect(x, y, width, height, cornerRadius, cornerRadius);
            else gc.strokeRect(x, y, width, height);
        }
    }

    @Override
    public MObject copy() {
        RectMob c = new RectMob(width, height);
        copyBaseProperties(c);
        c.cornerRadius = this.cornerRadius;
        return c;
    }
}
