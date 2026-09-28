package com.lecviz.mobjects;

import com.lecviz.core.MObject;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Vec2;
import com.lecviz.utils.Vec3;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.*;

/**
 * A rotating grid of unit cubes rendered with a tiny hand-rolled 3D pipeline:
 * rotate (yaw then pitch) -> orthographic project -> backface-cull each of
 * the 6 faces per cube via its rotated normal -> shade with a fixed
 * camera-space light -> depth-sort every visible face (and its label, if
 * any) together and paint back-to-front.
 *
 * Rows (r) stack along the local Y axis (down, matching this engine's
 * screen convention where +Y is down), columns (c) along X, and depth (d)
 * along Z. A cube's on-screen position is its grid center plus a per-cube
 * offset — animating that offset while rotation eases back to (0,0) is
 * what lets a cuboid "unroll" into a flat memory strip (see FlattenCube3D).
 */
public class CubeGrid3D extends MObject {

    public record Idx(int r, int c, int d) {}

    private static final Vec3[] FACE_NORMAL = {
        new Vec3(1, 0, 0), new Vec3(-1, 0, 0),
        new Vec3(0, 1, 0), new Vec3(0, -1, 0),
        new Vec3(0, 0, 1), new Vec3(0, 0, -1),
    };
    // Each face's 4 corners, in perimeter order, as +-1 sign patterns (scaled by half-extent h).
    private static final double[][][] FACE_CORNER_SIGNS = {
        { {1,-1,-1}, {1,-1,1}, {1,1,1}, {1,1,-1} },     // +X
        { {-1,-1,-1}, {-1,1,-1}, {-1,1,1}, {-1,-1,1} }, // -X
        { {-1,1,-1}, {1,1,-1}, {1,1,1}, {-1,1,1} },     // +Y (bottom, screen-down)
        { {-1,-1,-1}, {-1,-1,1}, {1,-1,1}, {1,-1,-1} }, // -Y (top, screen-up) — label face
        { {-1,-1,1}, {1,-1,1}, {1,1,1}, {-1,1,1} },     // +Z
        { {-1,-1,-1}, {-1,1,-1}, {1,1,-1}, {1,-1,-1} }, // -Z
    };
    private static final int LABEL_FACE = 4; // +Z (front) — stays visible from a flat frontal view through a modest orbit
    private static final Vec3 LIGHT_DIR = unit(new Vec3(0.35, -0.55, 0.75));

    private final int rows, cols, depth;
    private final double unit;
    private final double half;

    private final Map<Idx, String> values = new HashMap<>();
    private final Map<Idx, Color> colorOverride = new HashMap<>();
    private final Map<Idx, Vec3> offsets = new HashMap<>();

    private double rotY, rotX;
    private Color baseColor = Colors.BLUE;
    private double labelFontSize = 20;
    private boolean showLabels = true;
    // Default cubes render see-through so highlighted ones read as "popping"
    // through the shell; a cube with a color override renders solid.
    private double baseAlpha = 0.4;
    private double highlightAlpha = 0.96;

    public CubeGrid3D(int rows, int cols, int depth, double unit) {
        this.rows = rows;
        this.cols = cols;
        this.depth = depth;
        this.unit = unit;
        this.half = unit * 0.43;
        this.strokeColor = Colors.withAlpha(Color.BLACK, 0.4);
    }

    private static Vec3 unit(Vec3 v) {
        double len = Math.sqrt(v.x() * v.x() + v.y() * v.y() + v.z() * v.z());
        return len == 0 ? Vec3.ZERO : v.scale(1 / len);
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getDepth() { return depth; }
    public double getUnit() { return unit; }

    public CubeGrid3D setValue(int r, int c, int d, String v) { values.put(new Idx(r, c, d), v); return this; }
    public String getValue(int r, int c, int d) { return values.get(new Idx(r, c, d)); }

    public CubeGrid3D setCubeColor(int r, int c, int d, Color c2) { colorOverride.put(new Idx(r, c, d), c2); return this; }
    public CubeGrid3D clearCubeColor(int r, int c, int d) { colorOverride.remove(new Idx(r, c, d)); return this; }

    public CubeGrid3D setOffset(Idx idx, Vec3 off) { offsets.put(idx, off); return this; }
    public CubeGrid3D setOffset(int r, int c, int d, Vec3 off) { return setOffset(new Idx(r, c, d), off); }
    public Vec3 getOffset(Idx idx) { return offsets.getOrDefault(idx, Vec3.ZERO); }
    public Vec3 getOffset(int r, int c, int d) { return getOffset(new Idx(r, c, d)); }

    public CubeGrid3D setRotation(double ry, double rx) { this.rotY = ry; this.rotX = rx; return this; }
    public double getRotY() { return rotY; }
    public double getRotX() { return rotX; }

    public CubeGrid3D setBaseColor(Color c) { this.baseColor = c; return this; }
    public CubeGrid3D setLabelFontSize(double s) { this.labelFontSize = s; return this; }
    public CubeGrid3D setShowLabels(boolean b) { this.showLabels = b; return this; }
    public CubeGrid3D setBaseAlpha(double a) { this.baseAlpha = a; return this; }
    public CubeGrid3D setHighlightAlpha(double a) { this.highlightAlpha = a; return this; }

    /** Center of cube (r, c, d) in local space, before any per-cube offset. */
    public Vec3 cubeCenter(int r, int c, int d) {
        double x = (c - (cols - 1) / 2.0) * unit;
        double y = (r - (rows - 1) / 2.0) * unit;
        double z = (d - (depth - 1) / 2.0) * unit;
        return new Vec3(x, y, z);
    }

    /**
     * Project a local-space point to this mobject's own 2D local coordinates
     * at the current rotation (ignoring depth) — useful for a caller who
     * needs to know where a particular cube appears on screen right now,
     * e.g. to point a camera at it (add this mobject's own scene position
     * and scale on top of the result).
     */
    public Vec2 projectLocal(Vec3 local) {
        double[] r = rotateOnly(local);
        return new Vec2(r[0], r[1]);
    }

    /** Rotate a local-space vector/point by the current (rotY, rotX) orbit — no translation, no scale. */
    private double[] rotateOnly(Vec3 p) {
        double cosY = Math.cos(rotY), sinY = Math.sin(rotY);
        double x1 = p.x() * cosY + p.z() * sinY;
        double z1 = -p.x() * sinY + p.z() * cosY;
        double cosX = Math.cos(rotX), sinX = Math.sin(rotX);
        double y1 = p.y() * cosX - z1 * sinX;
        double z2 = p.y() * sinX + z1 * cosX;
        return new double[]{x1, y1, z2};
    }

    private static double clamp01(double v) { return Math.max(0, Math.min(1, v)); }

    private static Color shade(Color c, double brightness, double alpha) {
        return Color.color(clamp01(c.getRed() * brightness), clamp01(c.getGreen() * brightness),
                clamp01(c.getBlue() * brightness), c.getOpacity() * alpha);
    }

    private static double dot(double[] a, Vec3 b) { return a[0] * b.x() + a[1] * b.y() + a[2] * b.z(); }

    private static final class DrawItem {
        final double depth;
        final double[] xs, ys;
        final Color fill;
        final boolean isText;
        final String label;
        final double tx, ty, fontSize;

        DrawItem(double depth, double[] xs, double[] ys, Color fill) {
            this.depth = depth; this.xs = xs; this.ys = ys; this.fill = fill;
            this.isText = false; this.label = null; this.tx = 0; this.ty = 0; this.fontSize = 0;
        }

        DrawItem(double depth, double tx, double ty, String label, Color fill, double fontSize) {
            this.depth = depth; this.xs = null; this.ys = null; this.fill = fill;
            this.isText = true; this.label = label; this.tx = tx; this.ty = ty; this.fontSize = fontSize;
        }
    }

    @Override
    protected void draw(GraphicsContext gc) {
        List<DrawItem> items = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                for (int d = 0; d < depth; d++) {
                    Idx idx = new Idx(r, c, d);
                    Vec3 center = cubeCenter(r, c, d).add(getOffset(idx));
                    boolean highlighted = colorOverride.containsKey(idx);
                    Color cubeBase = colorOverride.getOrDefault(idx, baseColor);
                    double alpha = highlighted ? highlightAlpha : baseAlpha;

                    for (int f = 0; f < 6; f++) {
                        double[] nRot = rotateOnly(FACE_NORMAL[f]);
                        if (nRot[2] <= 0.02) continue; // backface culled

                        double[] xs = new double[4];
                        double[] ys = new double[4];
                        double depthSum = 0;
                        for (int k = 0; k < 4; k++) {
                            double[] s = FACE_CORNER_SIGNS[f][k];
                            Vec3 corner = center.add(new Vec3(s[0] * half, s[1] * half, s[2] * half));
                            double[] pr = rotateOnly(corner);
                            xs[k] = pr[0];
                            ys[k] = pr[1];
                            depthSum += pr[2];
                        }
                        double faceDepth = depthSum / 4.0;
                        double brightness = clamp01(0.42 + 0.58 * Math.max(0, dot(nRot, LIGHT_DIR)));
                        items.add(new DrawItem(faceDepth, xs, ys, shade(cubeBase, brightness, alpha)));

                        if (f == LABEL_FACE && showLabels) {
                            String v = values.get(idx);
                            if (v != null) {
                                double cx = (xs[0] + xs[1] + xs[2] + xs[3]) / 4.0;
                                double cy = (ys[0] + ys[1] + ys[2] + ys[3]) / 4.0;
                                items.add(new DrawItem(faceDepth + 0.01, cx, cy, v, Colors.WHITE, labelFontSize));
                            }
                        }
                    }
                }
            }
        }

        items.sort(Comparator.comparingDouble(it -> it.depth));

        for (DrawItem it : items) {
            if (!it.isText) {
                gc.setFill(it.fill);
                gc.fillPolygon(it.xs, it.ys, 4);
                gc.setStroke(strokeColor);
                gc.setLineWidth(1.3);
                gc.strokePolygon(it.xs, it.ys, 4);
            } else {
                gc.setFont(Font.font("Monospace", FontWeight.BOLD, it.fontSize));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.setTextBaseline(VPos.CENTER);
                gc.setFill(it.fill);
                gc.fillText(it.label, it.tx, it.ty);
            }
        }
    }

    @Override
    public MObject copy() {
        CubeGrid3D c = new CubeGrid3D(rows, cols, depth, unit);
        copyBaseProperties(c);
        c.values.putAll(this.values);
        c.colorOverride.putAll(this.colorOverride);
        c.offsets.putAll(this.offsets);
        c.rotY = this.rotY;
        c.rotX = this.rotX;
        c.baseColor = this.baseColor;
        c.labelFontSize = this.labelFontSize;
        c.showLabels = this.showLabels;
        c.baseAlpha = this.baseAlpha;
        c.highlightAlpha = this.highlightAlpha;
        return c;
    }
}
