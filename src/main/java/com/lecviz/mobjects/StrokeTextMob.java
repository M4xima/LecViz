package com.lecviz.mobjects;

import com.lecviz.core.MObject;
import com.lecviz.utils.Colors;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Text drawn by tracing each glyph's real vector outline and filling it in
 * behind the stroke, one character at a time — the "handwritten" reveal
 * 3Blue1Brown uses for titles, instead of whole characters popping in.
 * Outlines come from java.awt.Font (GlyphVector.getGlyphOutline), which is
 * the one place a real font-hinted path is available to us; everything
 * else about this mobject is plain JavaFX Canvas drawing.
 *
 * Pair with the Write animation: it drives setRevealFraction(0..1) exactly
 * like it already does for TextMob and LaTeXMob.
 */
public class StrokeTextMob extends MObject {

    private static final class Contour {
        final double[] xs, ys;
        final double length;

        Contour(List<double[]> pts) {
            xs = new double[pts.size()];
            ys = new double[pts.size()];
            double len = 0;
            for (int i = 0; i < pts.size(); i++) {
                xs[i] = pts.get(i)[0];
                ys[i] = pts.get(i)[1];
                if (i > 0) {
                    double dx = xs[i] - xs[i - 1], dy = ys[i] - ys[i - 1];
                    len += Math.sqrt(dx * dx + dy * dy);
                }
            }
            this.length = len;
        }
    }

    private static final class Glyph {
        final List<Contour> contours = new ArrayList<>();
        double totalLength;
    }

    private final List<Glyph> glyphs = new ArrayList<>();
    private double originX, originY;
    private double revealFraction = 1.0;
    private double strokeWidthPx = 2.4;

    public StrokeTextMob(String text, String fontFamily, boolean bold, double size) {
        this.fillColor = Colors.WHITE;
        this.strokeColor = Colors.WHITE;
        buildOutline(text, fontFamily, bold, size);
    }

    public StrokeTextMob setRevealFraction(double f) { this.revealFraction = Math.max(0, Math.min(1, f)); return this; }
    public double getRevealFraction() { return revealFraction; }
    public StrokeTextMob setGlyphStrokeWidth(double w) { this.strokeWidthPx = w; return this; }

    // Covariant overrides so fluent chaining keeps this type
    @Override public StrokeTextMob setFillColor(Color c) { super.setFillColor(c); return this; }
    @Override public StrokeTextMob setStrokeColor(Color c) { super.setStrokeColor(c); return this; }

    private void buildOutline(String text, String fontFamily, boolean bold, double size) {
        double emSize = 200.0;
        java.awt.Font awtFont = new java.awt.Font(fontFamily, bold ? java.awt.Font.BOLD : java.awt.Font.PLAIN, 1)
                .deriveFont((float) emSize);
        FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
        GlyphVector gv = awtFont.createGlyphVector(frc, text);
        double scale = size / emSize;

        for (int i = 0; i < gv.getNumGlyphs(); i++) {
            java.awt.Shape outline = gv.getGlyphOutline(i);
            Glyph g = new Glyph();
            PathIterator pi = outline.getPathIterator(null, 1.0);
            List<double[]> current = new ArrayList<>();
            double[] coords = new double[6];
            while (!pi.isDone()) {
                int type = pi.currentSegment(coords);
                if (type == PathIterator.SEG_MOVETO) {
                    if (current.size() > 1) g.contours.add(new Contour(current));
                    current = new ArrayList<>();
                    current.add(new double[]{coords[0], coords[1]});
                } else if (type == PathIterator.SEG_LINETO) {
                    current.add(new double[]{coords[0], coords[1]});
                } else if (type == PathIterator.SEG_CLOSE) {
                    if (!current.isEmpty()) current.add(new double[]{current.get(0)[0], current.get(0)[1]});
                    if (current.size() > 1) g.contours.add(new Contour(current));
                    current = new ArrayList<>();
                }
                pi.next();
            }
            if (current.size() > 1) g.contours.add(new Contour(current));
            for (Contour c : g.contours) g.totalLength += c.length;
            glyphs.add(g);
        }

        Rectangle2D bounds = gv.getLogicalBounds();
        this.originX = -bounds.getWidth() * scale / 2.0;
        this.originY = -(bounds.getY() + bounds.getHeight() / 2.0) * scale;
        this.glyphScale = scale;
    }

    private double glyphScale = 1.0;

    @Override
    protected void draw(GraphicsContext gc) {
        int n = glyphs.size();
        if (n == 0) return;
        double slot = 1.0 / n;

        for (int gi = 0; gi < n; gi++) {
            double sub = (revealFraction - gi * slot) / slot;
            sub = Math.max(0, Math.min(1, sub));
            if (sub <= 0) continue;
            drawGlyph(gc, glyphs.get(gi), sub);
        }
    }

    private void drawGlyph(GraphicsContext gc, Glyph g, double sub) {
        double strokeT = Math.min(1, sub / 0.65);
        double fillT = Math.max(0, Math.min(1, (sub - 0.5) / 0.5));

        if (fillT > 0 && !g.contours.isEmpty()) {
            gc.setFill(Colors.withAlpha(fillColor, fillT));
            gc.setFillRule(FillRule.EVEN_ODD);
            gc.beginPath();
            for (Contour c : g.contours) {
                gc.moveTo(px(c.xs[0]), py(c.ys[0]));
                for (int i = 1; i < c.xs.length; i++) gc.lineTo(px(c.xs[i]), py(c.ys[i]));
                gc.closePath();
            }
            gc.fill();
        }

        if (strokeT > 0 && strokeT < 1.0 && g.totalLength > 0) {
            gc.setStroke(strokeColor);
            gc.setLineWidth(strokeWidthPx);
            double budget = strokeT * g.totalLength;
            double used = 0;
            for (Contour c : g.contours) {
                if (used >= budget) break;
                double remaining = budget - used;
                drawPartialContour(gc, c, remaining);
                used += c.length;
            }
        }
    }

    private void drawPartialContour(GraphicsContext gc, Contour c, double budget) {
        double travelled = 0;
        gc.beginPath();
        gc.moveTo(px(c.xs[0]), py(c.ys[0]));
        for (int i = 1; i < c.xs.length; i++) {
            double dx = c.xs[i] - c.xs[i - 1], dy = c.ys[i] - c.ys[i - 1];
            double segLen = Math.sqrt(dx * dx + dy * dy);
            if (travelled + segLen <= budget) {
                gc.lineTo(px(c.xs[i]), py(c.ys[i]));
                travelled += segLen;
            } else {
                double t = segLen == 0 ? 0 : (budget - travelled) / segLen;
                gc.lineTo(px(c.xs[i - 1] + dx * t), py(c.ys[i - 1] + dy * t));
                break;
            }
        }
        gc.stroke();
    }

    private double px(double glyphX) { return glyphX * glyphScale + originX; }
    private double py(double glyphY) { return glyphY * glyphScale + originY; }

    @Override
    public MObject copy() {
        // Rebuilding outlines is unnecessary for how this mobject is used
        // (Write reads/writes revealFraction on the same instance), so
        // copy() just shares the immutable glyph data.
        StrokeTextMob c = new StrokeTextMob("", "SansSerif", false, 1);
        copyBaseProperties(c);
        c.glyphs.addAll(this.glyphs);
        c.originX = this.originX;
        c.originY = this.originY;
        c.glyphScale = this.glyphScale;
        c.revealFraction = this.revealFraction;
        c.strokeWidthPx = this.strokeWidthPx;
        return c;
    }
}
