package com.lecviz.mobjects;

import com.lecviz.core.MObject;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Vec2;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import com.lecviz.utils.Vec2;

/**
 * A text object for titles, labels, and explanations.
 * Supports partial rendering (for Write animation).
 */
public class TextMob extends MObject {

    private String text;
    private String fontFamily = "SansSerif";
    private double fontSize = 36;
    private FontWeight fontWeight = FontWeight.NORMAL;
    private TextAlignment alignment = TextAlignment.CENTER;
    private double visibleFraction = 1.0; // for Write animation

    public TextMob(String text) {
        this.text = text;
        this.fillColor = Colors.WHITE;
        this.strokeColor = Color.TRANSPARENT;
    }

    public TextMob setText(String text) { this.text = text; return this; }
    public String getText() { return text; }

    public TextMob setFontSize(double size) { this.fontSize = size; return this; }
    public double getFontSize() { return fontSize; }

    public TextMob setFontFamily(String family) { this.fontFamily = family; return this; }
    public TextMob setBold() { this.fontWeight = FontWeight.BOLD; return this; }

    public TextMob setAlignment(TextAlignment a) { this.alignment = a; return this; }
    public TextMob setVisibleFraction(double f) { this.visibleFraction = Math.max(0, Math.min(1, f)); return this; }
    public double getVisibleFraction() { return visibleFraction; }

    // Covariant return overrides for fluent chaining
    @Override public TextMob setFillColor(Color c) { super.setFillColor(c); return this; }
    @Override public TextMob setStrokeColor(Color c) { super.setStrokeColor(c); return this; }
    @Override public TextMob setPosition(Vec2 pos) { super.setPosition(pos); return this; }
    @Override public TextMob setPosition(double x, double y) { super.setPosition(x, y); return this; }
    @Override public TextMob setOpacity(double o) { super.setOpacity(o); return this; }
    @Override public TextMob setScale(double s) { super.setScale(s); return this; }
    protected void draw(GraphicsContext gc) {
        Font font = Font.font(fontFamily, fontWeight, fontSize);
        gc.setFont(font);
        gc.setTextAlign(alignment);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFill(fillColor);

        if (visibleFraction >= 1.0) {
            gc.fillText(text, 0, 0);
        } else {
            // Partial text rendering for Write animation
            int visibleChars = (int) (text.length() * visibleFraction);
            String partial = text.substring(0, visibleChars);
            gc.fillText(partial, 0, 0);
        }
    }

    @Override
    public MObject copy() {
        TextMob c = new TextMob(this.text);
        copyBaseProperties(c);
        c.fontFamily = this.fontFamily;
        c.fontSize = this.fontSize;
        c.fontWeight = this.fontWeight;
        c.alignment = this.alignment;
        c.visibleFraction = this.visibleFraction;
        return c;
    }
}
