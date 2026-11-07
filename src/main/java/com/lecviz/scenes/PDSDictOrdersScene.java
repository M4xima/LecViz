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
 * Standalone clip for slide 8 of the dictionary deck: four insertion orders of the same values.
 *
 *   - four trees grow side by side, each by its own insertion order, one value per step, and each ends with
 *     its height: the same values give a tree of height 3 or a chain of height 6
 *   - classwork "Perform inorder traversal on the last BST": the left chain is redrawn larger; a call stack
 *     deepens to seven frames going down, then every value is written into an output strip on the way back
 *     up: 1 2 3 4 5 6 8, the sorted order
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSDictOrdersScene extends PDSDictClipBase {

    private static final int[][] ORDERS = {
            {6, 8, 2, 4, 5, 1, 3},
            {4, 8, 2, 6, 5, 1, 3},
            {1, 2, 3, 4, 5, 6, 8},
            {8, 6, 5, 4, 3, 2, 1}};

    private final List<MObject> mine = new ArrayList<>();
    private GT big;
    private CallStack cs;
    private OutStrip os;
    private final Color[] fc = {Colors.TEAL, Colors.BLUE, Colors.PINK, Colors.GOLD, Colors.PURPLE, Colors.ORANGE, Colors.MAROON};
    private int calls;

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("Insertion order");
        pause(0.5);
        four();
        classwork(head);
    }

    // ── four trees from four orders ──────────────────────────────────

    private void four() {
        double[] cx = {-470, 470, -470, 470};
        double[] top = {-350, -350, 70, 70};
        GN[] roots = new GN[4];
        GT[] ts = new GT[4];
        List<Animation> in = new ArrayList<>();
        List<TextMob> titles = new ArrayList<>();
        for (int k = 0; k < 4; k++) {
            roots[k] = bstOf(ORDERS[k]);
            ts[k] = bst(roots[k], cx[k], top[k], 50, 19, 22);
            mine.addAll(ts[k].parts());
            StringBuilder sb = new StringBuilder("Insert ");
            for (int i = 0; i < ORDERS[k].length; i++) sb.append(i == 0 ? "" : ", ").append(ORDERS[k][i]);
            TextMob ti = label(sb.toString(), cx[k], top[k] - 52, 28, Colors.WHITE, false, true);
            titles.add(ti);
            mine.add(ti);
            in.add(new FadeInAt(ti, 0.2 * k, d(0.6)));
        }
        playAll(in);
        sayLine("The same seven values, inserted in four different orders.", Colors.LIGHT_GRAY);
        pause(1.4);
        for (int i = 0; i < 7; i++) {
            List<Animation> a = new ArrayList<>();
            for (int k = 0; k < 4; k++) {
                GN n = bstFind(roots[k], ORDERS[k][i]);
                ts[k].showNode(a, n, 0, d(0.55));
            }
            playAll(a);
            pause(i < 2 ? 0.6 : 0.35);
        }
        pause(0.6);
        sayLine("Different insertion orders may give different shapes.", Colors.LIGHT_GRAY);
        int[] hs = {3, 3, 6, 6};
        List<Animation> h = new ArrayList<>();
        for (int k = 0; k < 4; k++) {
            TextMob ht = label("height " + height(roots[k]), cx[k] + 330, top[k] - 52, 28, hs[k] == 3 ? Colors.GREEN : Colors.RED, false, true);
            mine.add(ht);
            h.add(new FadeInAt(ht, 0.25 * k, d(0.6)));
        }
        playAll(h);
        pause(2.8);
        unsay();
    }

    // ── classwork: inorder traversal of the last tree ────────────────

    private void classwork(StrokeTextMob head) {
        mine.add(head);
        fadeOutAll(d(1.0), mine);
        mine.clear();
        List<MObject> q = question(1, "Perform inorder traversal on the last BST.", -420, 40, new String[]{"inorder traversal"}, Colors.GOLD);
        mine.addAll(q);
        pause(0.8);

        GN root = bstOf(8, 6, 5, 4, 3, 2, 1);
        big = bst(root, 480, -330, 72, 26, 28);
        big.build(0.2);
        mine.addAll(big.parts());
        cs = new CallStack(-330, -250, 340, 44);
        cs.showTitle();
        mine.addAll(cs.all());
        os = new OutStrip("inorder:", -900, 330, 56, Colors.GREEN);
        os.showTitle();
        mine.addAll(os.all());
        pause(0.5);

        sayLine("Inorder: the left subtree, then the node, then the right subtree.", Colors.LIGHT_GRAY);
        pause(1.4);
        inorder(root);
        mine.addAll(os.all());
        sayLine("1 2 3 4 5 6 8: the values come out in sorted order.", Colors.GREEN);
        pause(3.0);
        unsay();
        fadeOutAll(d(1.2), mine);
        pause(0.3);
    }

    private void inorder(GN n) {
        calls++;
        cs.push("inorder(" + n.name + ")", fc[Math.min(cs.frames.size(), fc.length - 1)]);
        mine.addAll(cs.all());
        lit(big, n, VISIT_C, d(0.35));
        GN l = child(n, 0), r = child(n, 1);
        if (l != null) {
            if (calls <= 3) sayLine(n.name + " has a left child: go there before writing " + n.name + ".", Colors.LIGHT_GRAY);
            pause(calls <= 3 ? 0.8 : 0.3);
            inorder(l);
        } else {
            sayLine(n.name + " has no left child: it is the smallest value left, write it.", Colors.GREEN);
            pause(0.7);
        }
        os.add(n.name);
        mine.addAll(os.all());
        List<Animation> a = new ArrayList<>();
        big.paint(a, n, DONE_C, d(0.3));
        big.paintEdge(a, n, DONE_C, d(0.3));
        playAll(a);
        pause(calls <= 3 ? 0.6 : 0.25);
        if (r != null) inorder(r);
        cs.pop(null, null);
        mine.addAll(cs.all());
    }

    private void sayLine(String s, Color c) { sayAt(s, c, 0, 446, 34); }
}
