package com.lecviz.mobjects;

import com.lecviz.core.MObject;
import com.lecviz.utils.Colors;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

/**
 * Visual stack — vertical boxes with push/pop animations.
 */
public class StackMob extends MObject {

    private final List<String> items = new ArrayList<>();
    private double cellWidth = 100;
    private double cellHeight = 45;
    private int maxVisible = 8;
    private String label = "Stack";
    private int highlightTop = 0; // how many top items to highlight (0 = none)

    public StackMob(String... initial) {
        for (String s : initial) items.add(s);
        this.strokeColor = Colors.PURPLE;
        this.fillColor = Colors.withAlpha(Colors.PURPLE, 0.1);
    }

    public StackMob push(String val) { items.add(val); return this; }
    public String pop() { return items.isEmpty() ? null : items.remove(items.size() - 1); }
    public String peek() { return items.isEmpty() ? null : items.get(items.size() - 1); }
    public int size() { return items.size(); }
    public StackMob setLabel(String l) { this.label = l; return this; }
    public StackMob setHighlightTop(int n) { this.highlightTop = n; return this; }

    @Override
    protected void draw(GraphicsContext gc) {
        int count = Math.min(items.size(), maxVisible);
        double totalHeight = count * cellHeight;
        double startX = -cellWidth / 2;
        double bottomY = totalHeight / 2;

        // Label
        gc.setFont(Font.font("SansSerif", 18));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.TOP);
        gc.setFill(Colors.LIGHT_GRAY);
        gc.fillText(label, 0, bottomY + 10);

        // "TOP" indicator
        if (!items.isEmpty()) {
            gc.setFont(Font.font("Monospace", 14));
            gc.setFill(Colors.ORANGE);
            gc.setTextBaseline(VPos.CENTER);
            gc.setTextAlign(TextAlignment.LEFT);
            double topY = bottomY - count * cellHeight + cellHeight / 2;
            gc.fillText("← TOP", cellWidth / 2 + 10, topY);
        }

        // Draw cells bottom to top
        for (int i = 0; i < count; i++) {
            int dataIndex = items.size() - count + i;
            double y = bottomY - (count - i) * cellHeight;

            boolean isHighlighted = (count - i) <= highlightTop;
            Color bg = isHighlighted
                    ? Colors.withAlpha(Colors.YELLOW, 0.25)
                    : fillColor;

            gc.setFill(bg);
            gc.fillRect(startX, y, cellWidth, cellHeight);
            gc.setStroke(strokeColor);
            gc.setLineWidth(2);
            gc.strokeRect(startX, y, cellWidth, cellHeight);

            gc.setFont(Font.font("Monospace", 22));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.setFill(Colors.WHITE);
            gc.fillText(items.get(dataIndex), 0, y + cellHeight / 2);
        }
    }

    @Override
    public MObject copy() {
        StackMob c = new StackMob();
        copyBaseProperties(c);
        c.items.addAll(this.items);
        c.cellWidth = this.cellWidth;
        c.cellHeight = this.cellHeight;
        c.maxVisible = this.maxVisible;
        c.label = this.label;
        c.highlightTop = this.highlightTop;
        return c;
    }
}
