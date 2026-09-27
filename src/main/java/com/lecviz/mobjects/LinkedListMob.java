package com.lecviz.mobjects;

import com.lecviz.core.MObject;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Vec2;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

/**
 * Visual linked list for PDS data structure animations.
 * Each node shows data + next pointer arrow.
 */
public class LinkedListMob extends MObject {

    public static class Node {
        public String data;
        public Color color;
        public boolean highlighted;

        public Node(String data) {
            this.data = data;
            this.color = Colors.withAlpha(Colors.TEAL, 0.15);
            this.highlighted = false;
        }
    }

    private final List<Node> nodes = new ArrayList<>();
    private double nodeWidth = 100;
    private double nodeHeight = 50;
    private double gap = 60; // gap between nodes (for arrows)
    private String headLabel = "head";

    public LinkedListMob(String... values) {
        for (String v : values) nodes.add(new Node(v));
        this.strokeColor = Colors.TEAL;
    }

    public LinkedListMob addNode(String data) { nodes.add(new Node(data)); return this; }
    public LinkedListMob insertNode(int index, String data) { nodes.add(index, new Node(data)); return this; }
    public LinkedListMob removeNode(int index) { nodes.remove(index); return this; }
    public LinkedListMob highlightNode(int i) { if (i >= 0 && i < nodes.size()) nodes.get(i).highlighted = true; return this; }
    public LinkedListMob unhighlightNode(int i) { if (i >= 0 && i < nodes.size()) nodes.get(i).highlighted = false; return this; }
    public LinkedListMob setHeadLabel(String l) { this.headLabel = l; return this; }
    public int getSize() { return nodes.size(); }

    @Override
    protected void draw(GraphicsContext gc) {
        double totalWidth = nodes.size() * (nodeWidth + gap) - gap;
        double startX = -totalWidth / 2;

        // Head pointer
        if (headLabel != null && !nodes.isEmpty()) {
            double hx = startX - 50;
            gc.setFont(Font.font("Monospace", 16));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.setFill(Colors.ORANGE);
            gc.fillText(headLabel, hx, 0);
            gc.setStroke(Colors.ORANGE);
            gc.setLineWidth(2);
            gc.strokeLine(hx + 25, 0, startX - 5, 0);
            // arrowhead
            gc.setFill(Colors.ORANGE);
            gc.fillPolygon(
                new double[]{startX - 5, startX - 15, startX - 15},
                new double[]{0, -6, 6}, 3
            );
        }

        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            double x = startX + i * (nodeWidth + gap);
            double y = -nodeHeight / 2;

            // Node background
            Color bg = node.highlighted
                    ? Colors.withAlpha(Colors.YELLOW, 0.3)
                    : node.color;
            gc.setFill(bg);
            gc.fillRoundRect(x, y, nodeWidth, nodeHeight, 8, 8);

            // Node border
            gc.setStroke(strokeColor);
            gc.setLineWidth(2);
            gc.strokeRoundRect(x, y, nodeWidth, nodeHeight, 8, 8);

            // Split: data | next
            double splitX = x + nodeWidth * 0.65;
            gc.strokeLine(splitX, y, splitX, y + nodeHeight);

            // Data text
            gc.setFont(Font.font("Monospace", 22));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.setFill(Colors.WHITE);
            gc.fillText(node.data, x + nodeWidth * 0.325, 0);

            // Next pointer arrow to next node
            if (i < nodes.size() - 1) {
                double arrowStartX = x + nodeWidth;
                double arrowEndX = startX + (i + 1) * (nodeWidth + gap);
                gc.setStroke(Colors.LIGHT_GRAY);
                gc.setLineWidth(2);
                gc.strokeLine(arrowStartX, 0, arrowEndX - 5, 0);
                gc.setFill(Colors.LIGHT_GRAY);
                gc.fillPolygon(
                    new double[]{arrowEndX - 5, arrowEndX - 15, arrowEndX - 15},
                    new double[]{0, -5, 5}, 3
                );
            } else {
                // NULL for last node
                gc.setFont(Font.font("Monospace", 12));
                gc.setFill(Colors.GRAY);
                double nullX = x + nodeWidth * 0.825;
                gc.fillText("NULL", nullX, 0);
            }
        }
    }

    @Override
    public MObject copy() {
        LinkedListMob c = new LinkedListMob();
        copyBaseProperties(c);
        for (Node n : nodes) {
            Node cn = new Node(n.data);
            cn.color = n.color;
            cn.highlighted = n.highlighted;
            c.nodes.add(cn);
        }
        c.nodeWidth = this.nodeWidth;
        c.nodeHeight = this.nodeHeight;
        c.gap = this.gap;
        c.headLabel = this.headLabel;
        return c;
    }
}
