package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Standalone clip for slide 10 of the dictionary deck: "For this BST, find three different insertion orders."
 *
 *   - the question appears (badge, pen-stroke text, highlighter on the key words) beside the slide's tree
 *   - each of the slide's three answers is replayed on the tree: a gold number lands on every node in the order
 *     it is inserted while the order types itself into a row on the left
 *   - the rule behind all of them (a parent always comes before its children) and how many orders follow it
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSDictOrderQuizScene extends PDSDictClipBase {

    private static final int[][] ORDERS = {
            {4, 2, 8, 1, 3, 6, 5},
            {4, 8, 2, 6, 1, 3, 5},
            {4, 2, 1, 3, 8, 6, 5}};

    private final List<MObject> mine = new ArrayList<>();

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("Insertion Orders");
        pause(0.5);
        List<MObject> q = question(1, "For this BST, find three different insertion orders.", -395, 38,
                new String[]{"three different insertion orders"}, Colors.GOLD);
        mine.addAll(q);
        pause(0.8);

        GN root = bstOf(4, 2, 8, 1, 3, 6, 5);
        GT t = bst(root, 430, -250, 120, 34, 36);
        t.build(0.25);
        mine.addAll(t.parts());
        pause(0.8);

        for (int k = 0; k < ORDERS.length; k++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ORDERS[k].length; i++) sb.append(i == 0 ? "" : ", ").append(ORDERS[k][i]);
            double y = -170 + k * 95;
            TextMob row = monoLeft(sb.toString(), -880, y, 44, Colors.WHITE);
            mine.add(row);
            List<TextMob> tags = new ArrayList<>();
            for (int i = 0; i < ORDERS[k].length; i++) {
                GN n = bstFind(root, ORDERS[k][i]);
                TextMob tag = t.tag(n, String.valueOf(i + 1), Colors.GOLD, 36, 50, -40);
                tags.add(tag);
                mine.add(tag);
            }
            sayAt("Order " + (k + 1) + ": number the nodes in the order they are inserted.", Colors.LIGHT_GRAY, 0, 446, 34);
            List<Animation> type = new ArrayList<>();
            type.add(new TypeAt(row, 0, d(0.45 * ORDERS[k].length)));
            for (int i = 0; i < tags.size(); i++) {
                GN n = bstFind(root, ORDERS[k][i]);
                type.add(new FadeInAt(tags.get(i), 0.45 * i, d(0.3)));
                type.add(new PulseAt(n, 0.45 * i));
            }
            playAll(type);
            pause(1.0);
            List<Animation> gone = new ArrayList<>();
            for (TextMob tag : tags) gone.add(new FadeOut(tag, d(0.4)));
            playAll(gone);
            pause(0.2);
        }
        TextMob dots = monoLeft("...", -880, -170 + 3 * 95, 44, Colors.GRAY);
        play(new FadeIn(dots, d(0.6)));
        mine.add(dots);
        pause(0.8);

        sayAt("All three build the same tree: a parent is always inserted before its children.", Colors.GREEN, 0, 446, 34);
        pause(2.2);
        List<MObject> many = chip("In fact 40 different orders build this tree.", -430, 235, 800, 62, Colors.GOLD, 32);
        List<Animation> m = new ArrayList<>();
        fade(m, many, d(0.7));
        playAll(m);
        mine.addAll(many);
        pause(3.0);
        unsay();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    /** A short pop on a node's circle and number, starting after a delay. */
    private final class PulseAt extends Animation {
        private final GN n;
        private final double delay;

        PulseAt(GN n, double delay) {
            super(n.shape, delay + 0.5, com.lecviz.utils.Easing.LINEAR);
            this.n = n;
            this.delay = delay;
        }

        @Override
        public void interpolate(double t) {
            double p = Math.max(0, Math.min(1, (t * duration - delay) / 0.5));
            double s = 1.0 + 0.28 * Math.sin(Math.PI * p);
            n.shape.setScale(s);
            n.text.setScale(s);
        }
    }
}
