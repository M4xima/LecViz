package com.lecviz.mobjects;

import com.lecviz.core.MObject;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Vec2;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Renders LaTeX math formulas using JLaTeXMath — 3Blue1Brown style.
 *
 * Usage:
 *   LaTeXMob formula = new LaTeXMob("O(n^2)").setSize(40).setLatexColor(Colors.GOLD);
 *   play(new Write(formula, 1.0));
 */
public class LaTeXMob extends MObject {

    private String latex;
    private float size = 36;
    private Color latexColor = Colors.WHITE;
    private double visibleFraction = 1.0;

    // Cached rendered image
    private Image renderedImage;
    private boolean dirty = true;

    public LaTeXMob(String latex) {
        this.latex = latex;
        this.fillColor = Colors.WHITE;
        this.strokeColor = Color.TRANSPARENT;
    }

    public LaTeXMob setLatex(String latex) { this.latex = latex; dirty = true; return this; }
    public LaTeXMob setSize(float size) { this.size = size; dirty = true; return this; }
    public LaTeXMob setLatexColor(Color c) { this.latexColor = c; dirty = true; return this; }
    public LaTeXMob setVisibleFraction(double f) { this.visibleFraction = Math.max(0, Math.min(1, f)); return this; }
    public double getVisibleFraction() { return visibleFraction; }

    // Covariant return overrides for fluent chaining
    @Override public LaTeXMob setFillColor(Color c) { setLatexColor(c); return this; }
    @Override public LaTeXMob setStrokeColor(Color c) { super.setStrokeColor(c); return this; }
    @Override public LaTeXMob setPosition(Vec2 pos) { super.setPosition(pos); return this; }
    @Override public LaTeXMob setPosition(double x, double y) { super.setPosition(x, y); return this; }
    @Override public LaTeXMob setOpacity(double o) { super.setOpacity(o); return this; }
    @Override public LaTeXMob setScale(double s) { super.setScale(s); return this; }

    private void renderLatex() {
        if (!dirty && renderedImage != null) return;
        try {
            TeXFormula formula = new TeXFormula(latex);
            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, size);

            // Convert JavaFX Color to AWT Color
            java.awt.Color awtColor = new java.awt.Color(
                (float) latexColor.getRed(),
                (float) latexColor.getGreen(),
                (float) latexColor.getBlue(),
                (float) latexColor.getOpacity()
            );

            int w = icon.getIconWidth();
            int h = icon.getIconHeight();
            if (w <= 0) w = 1;
            if (h <= 0) h = 1;

            BufferedImage bImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = bImg.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            icon.setForeground(awtColor);
            icon.paintIcon(null, g2, 0, 0);
            g2.dispose();

            renderedImage = SwingFXUtils.toFXImage(bImg, null);
            dirty = false;
        } catch (Exception e) {
            System.err.println("[LaTeXMob] Error rendering: " + latex + " — " + e.getMessage());
        }
    }

    @Override
    protected void draw(GraphicsContext gc) {
        renderLatex();
        if (renderedImage == null) return;

        double imgW = renderedImage.getWidth();
        double imgH = renderedImage.getHeight();
        double drawX = -imgW / 2;
        double drawY = -imgH / 2;

        if (visibleFraction < 1.0) {
            // Left-to-right reveal (clip)
            double clipW = imgW * visibleFraction;
            gc.save();
            gc.beginPath();
            gc.rect(drawX, drawY, clipW, imgH);
            gc.clip();
            gc.drawImage(renderedImage, drawX, drawY);
            gc.restore();
        } else {
            gc.drawImage(renderedImage, drawX, drawY);
        }
    }

    @Override
    public MObject copy() {
        LaTeXMob c = new LaTeXMob(latex);
        c.setSize(size);
        c.setLatexColor(latexColor);
        c.setPosition(getPosition());
        c.setOpacity(getOpacity());
        c.setScale(getScale());
        return c;
    }
}
