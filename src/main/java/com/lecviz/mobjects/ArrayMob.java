package com.lecviz.mobjects;

import com.lecviz.core.MObject;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Vec2;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.*;

/**
 * Visual representation of an array — the bread and butter of PDS animations.
 * Each cell is a box with a value, optional index label, and highlight color.
 */
public class ArrayMob extends MObject {

    private String[] values;
    private double cellWidth = 70;
    private double cellHeight = 50;
    private Map<Integer, Color> cellColors = new HashMap<>();
    private Set<Integer> highlighted = new HashSet<>();
    private boolean showIndices = true;
    private String label = null; // e.g. "arr[]"
    private int pointerIndex = -1; // arrow pointer (-1 = hidden)
    private String pointerLabel = "i";

    public ArrayMob(String... values) {
        this.values = values;
        this.strokeColor = Colors.BLUE;
        this.fillColor = Colors.withAlpha(Colors.BLUE, 0.1);
    }

    public ArrayMob(int[] intValues) {
        this.values = new String[intValues.length];
        for (int i = 0; i < intValues.length; i++) values[i] = String.valueOf(intValues[i]);
        this.strokeColor = Colors.BLUE;
        this.fillColor = Colors.withAlpha(Colors.BLUE, 0.1);
    }

    public ArrayMob setValue(int index, String val) { values[index] = val; return this; }
    public ArrayMob setValues(String... vals) { this.values = vals; return this; }
    public String getValue(int index) { return values[index]; }
    public int getLength() { return values.length; }

    public ArrayMob setCellColor(int index, Color c) { cellColors.put(index, c); return this; }
    public ArrayMob clearCellColors() { cellColors.clear(); return this; }

    public ArrayMob highlight(int index) { highlighted.add(index); return this; }
    public ArrayMob unhighlight(int index) { highlighted.remove(index); return this; }
    public ArrayMob clearHighlights() { highlighted.clear(); return this; }

    public ArrayMob setShowIndices(boolean b) { showIndices = b; return this; }
    public ArrayMob setLabel(String l) { label = l; return this; }
    public ArrayMob setCellSize(double w, double h) { cellWidth = w; cellHeight = h; return this; }

    public ArrayMob setPointer(int index, String label) {
        this.pointerIndex = index;
        this.pointerLabel = label;
        return this;
    }
    public ArrayMob hidePointer() { pointerIndex = -1; return this; }

    /**
     * Get the center position of a cell (relative to the array's position).
     */
    public Vec2 getCellCenter(int index) {
        double totalWidth = values.length * cellWidth;
        double x = -totalWidth / 2 + index * cellWidth + cellWidth / 2;
        return new Vec2(x, 0);
    }

    /**
     * Swap two values visually.
     */
    public ArrayMob swap(int i, int j) {
        String tmp = values[i];
        values[i] = values[j];
        values[j] = tmp;
        return this;
    }

    @Override
    protected void draw(GraphicsContext gc) {
        double totalWidth = values.length * cellWidth;
        double startX = -totalWidth / 2;

        // Label above the array
        if (label != null) {
            gc.setFont(Font.font("SansSerif", 20));
            gc.setTextAlign(TextAlignment.LEFT);
            gc.setTextBaseline(VPos.BOTTOM);
            gc.setFill(Colors.LIGHT_GRAY);
            gc.fillText(label, startX, -cellHeight / 2 - 8);
        }

        for (int i = 0; i < values.length; i++) {
            double x = startX + i * cellWidth;
            double y = -cellHeight / 2;

            // Cell background
            Color bg = cellColors.getOrDefault(i, fillColor);
            if (highlighted.contains(i)) {
                bg = Colors.withAlpha(Colors.YELLOW, 0.25);
            }
            gc.setFill(bg);
            gc.fillRect(x, y, cellWidth, cellHeight);

            // Cell border
            gc.setStroke(strokeColor);
            gc.setLineWidth(strokeWidth);
            gc.strokeRect(x, y, cellWidth, cellHeight);

            // Value text
            gc.setFont(Font.font("Monospace", 24));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.setFill(Colors.WHITE);
            gc.fillText(values[i], x + cellWidth / 2, y + cellHeight / 2);

            // Index label below
            if (showIndices) {
                gc.setFont(Font.font("SansSerif", 14));
                gc.setFill(Colors.GRAY);
                gc.fillText(String.valueOf(i), x + cellWidth / 2, y + cellHeight + 16);
            }
        }

        // Pointer arrow
        if (pointerIndex >= 0 && pointerIndex < values.length) {
            double px = startX + pointerIndex * cellWidth + cellWidth / 2;
            double py = -cellHeight / 2 - 20;

            gc.setStroke(Colors.RED);
            gc.setLineWidth(2);
            gc.strokeLine(px, py - 30, px, py);

            // Arrowhead
            gc.setFill(Colors.RED);
            gc.fillPolygon(
                new double[]{px, px - 6, px + 6},
                new double[]{py, py - 10, py - 10},
                3
            );

            // Pointer label
            gc.setFont(Font.font("SansSerif", 16));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.BOTTOM);
            gc.setFill(Colors.RED);
            gc.fillText(pointerLabel, px, py - 34);
        }
    }

    @Override
    public MObject copy() {
        ArrayMob c = new ArrayMob(Arrays.copyOf(values, values.length));
        copyBaseProperties(c);
        c.cellWidth = this.cellWidth;
        c.cellHeight = this.cellHeight;
        c.cellColors = new HashMap<>(this.cellColors);
        c.highlighted = new HashSet<>(this.highlighted);
        c.showIndices = this.showIndices;
        c.label = this.label;
        c.pointerIndex = this.pointerIndex;
        c.pointerLabel = this.pointerLabel;
        return c;
    }
}
