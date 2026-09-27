package com.lecviz.mobjects;

import com.lecviz.core.MObject;
import com.lecviz.utils.Colors;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.HashMap;
import java.util.Map;

/**
 * Binary tree visualization.
 * Uses array-based storage: index 0 = root, left = 2i+1, right = 2i+2.
 */
public class TreeMob extends MObject {

    private String[] nodes;        // null = empty slot
    private double nodeRadius = 25;
    private double hSpacing = 60;  // horizontal spacing base
    private double vSpacing = 80;  // vertical spacing
    private Map<Integer, Color> nodeColors = new HashMap<>();
    private int highlightedNode = -1;

    public TreeMob(String... values) {
        this.nodes = values;
        this.strokeColor = Colors.GREEN;
    }

    public TreeMob(int[] values) {
        this.nodes = new String[values.length];
        for (int i = 0; i < values.length; i++) nodes[i] = String.valueOf(values[i]);
        this.strokeColor = Colors.GREEN;
    }

    public TreeMob setNodeValue(int index, String val) { nodes[index] = val; return this; }
    public TreeMob setNodeColor(int index, Color c) { nodeColors.put(index, c); return this; }
    public TreeMob highlightNode(int index) { highlightedNode = index; return this; }
    public TreeMob clearHighlight() { highlightedNode = -1; return this; }

    private int getDepth() {
        int depth = 0;
        int n = nodes.length;
        while (n > 0) { depth++; n = (n - 1) / 2; }
        return depth;
    }

    @Override
    protected void draw(GraphicsContext gc) {
        int depth = getDepth();
        drawNode(gc, 0, 0, -(depth - 1) * vSpacing / 2, depth);
    }

    private void drawNode(GraphicsContext gc, int index, double x, double y, int depth) {
        if (index >= nodes.length || nodes[index] == null) return;

        double spread = hSpacing * Math.pow(2, depth - 2);

        // Draw edges first
        int left = 2 * index + 1;
        int right = 2 * index + 2;

        if (left < nodes.length && nodes[left] != null) {
            double cx = x - spread;
            double cy = y + vSpacing;
            gc.setStroke(Colors.GRAY);
            gc.setLineWidth(2);
            gc.strokeLine(x, y + nodeRadius, cx, cy - nodeRadius);
            drawNode(gc, left, cx, cy, depth - 1);
        }
        if (right < nodes.length && nodes[right] != null) {
            double cx = x + spread;
            double cy = y + vSpacing;
            gc.setStroke(Colors.GRAY);
            gc.setLineWidth(2);
            gc.strokeLine(x, y + nodeRadius, cx, cy - nodeRadius);
            drawNode(gc, right, cx, cy, depth - 1);
        }

        // Draw node circle
        Color bg = nodeColors.getOrDefault(index, Colors.withAlpha(Colors.GREEN, 0.2));
        if (index == highlightedNode) bg = Colors.withAlpha(Colors.YELLOW, 0.4);

        gc.setFill(bg);
        gc.fillOval(x - nodeRadius, y - nodeRadius, 2 * nodeRadius, 2 * nodeRadius);
        gc.setStroke(strokeColor);
        gc.setLineWidth(2);
        gc.strokeOval(x - nodeRadius, y - nodeRadius, 2 * nodeRadius, 2 * nodeRadius);

        // Value
        gc.setFont(Font.font("Monospace", 20));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFill(Colors.WHITE);
        gc.fillText(nodes[index], x, y);
    }

    @Override
    public MObject copy() {
        TreeMob c = new TreeMob(java.util.Arrays.copyOf(nodes, nodes.length));
        copyBaseProperties(c);
        c.nodeRadius = this.nodeRadius;
        c.hSpacing = this.hSpacing;
        c.vSpacing = this.vSpacing;
        c.nodeColors = new HashMap<>(this.nodeColors);
        c.highlightedNode = this.highlightedNode;
        return c;
    }
}
