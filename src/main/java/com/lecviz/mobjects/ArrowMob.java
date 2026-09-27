package com.lecviz.mobjects;

import com.lecviz.core.MObject;
import com.lecviz.utils.Vec2;
import javafx.scene.canvas.GraphicsContext;

/**
 * A line segment with an arrowhead at the end.
 * Start and end are relative to the object's position.
 */
public class ArrowMob extends MObject {

    private Vec2 start;
    private Vec2 end;
    private double headLength = 15;
    private double headAngle = Math.toRadians(25);
    private double drawFraction = 1.0; // for animation

    public ArrowMob(Vec2 start, Vec2 end) {
        this.start = start;
        this.end = end;
    }

    public ArrowMob(double x1, double y1, double x2, double y2) {
        this(new Vec2(x1, y1), new Vec2(x2, y2));
    }

    public ArrowMob setDrawFraction(double f) { this.drawFraction = Math.max(0, Math.min(1, f)); return this; }
    public double getDrawFraction() { return drawFraction; }

    public ArrowMob setHeadLength(double l) { this.headLength = l; return this; }
    public Vec2 getStart() { return start; }
    public Vec2 getEnd() { return end; }

    @Override
    protected void draw(GraphicsContext gc) {
        gc.setStroke(strokeColor);
        gc.setLineWidth(strokeWidth);

        // Interpolated end for partial drawing
        Vec2 actualEnd = start.lerp(end, drawFraction);

        // Draw line
        gc.strokeLine(start.x(), start.y(), actualEnd.x(), actualEnd.y());

        // Draw arrowhead if mostly drawn
        if (drawFraction > 0.3) {
            double angle = start.angleTo(actualEnd);
            double ax1 = actualEnd.x() - headLength * Math.cos(angle - headAngle);
            double ay1 = actualEnd.y() - headLength * Math.sin(angle - headAngle);
            double ax2 = actualEnd.x() - headLength * Math.cos(angle + headAngle);
            double ay2 = actualEnd.y() - headLength * Math.sin(angle + headAngle);

            gc.setFill(strokeColor);
            gc.fillPolygon(
                new double[]{actualEnd.x(), ax1, ax2},
                new double[]{actualEnd.y(), ay1, ay2},
                3
            );
        }
    }

    @Override
    public MObject copy() {
        ArrowMob c = new ArrowMob(start, end);
        copyBaseProperties(c);
        c.headLength = this.headLength;
        c.headAngle = this.headAngle;
        c.drawFraction = this.drawFraction;
        return c;
    }
}
