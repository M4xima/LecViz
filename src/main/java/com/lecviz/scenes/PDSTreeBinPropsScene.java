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
 * Standalone clips for slide 20 of the trees deck: properties of binary trees, in the classwork style
 * (two clips because of the length; the first shows all seven questions, the second recalls the last three).
 *
 *   part 1  max height N (a chain), min height about log2 N (levels filled completely), NULL pointers N + 1
 *           (2N slots, N - 1 of them used), min and max leaves
 *   part 2  max nodes of height H is 2^H - 1 (levels double), full nodes 0 .. about N/2 - 1, and why
 *           #full nodes + 1 == #leaves (a tree grown one node at a time keeps the equation true)
 *
 * The slide counts height in levels (a single node has height 1). Where the slide's answer is approximate or
 * differs from the exact value, the exact value is shown next to it.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeBinPropsScene extends PDSTreeClipBase {

    private final int part;
    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();
    private GT t;

    public PDSTreeBinPropsScene(int part) { this.part = part; }

    @Override
    public void construct() {
        head = writeHeading("Properties of Binary Trees");
        pause(0.5);
        if (part == 1) {
            questions();
            maxHeight();
            minHeight();
            nullPointers();
            leaves();
        } else {
            recall();
            maxNodes();
            fullNodes();
            proof();
        }
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    // ── the questions ────────────────────────────────────────────────

    private void questions() {
        List<MObject> q = new ArrayList<>();
        StrokeTextMob ctx = strokeLeft("For an N node binary tree (N > 0):", -850, -370, 40, Colors.WHITE);
        play(new Write(ctx, d(2.2)));
        q.add(ctx);
        String[] qs = {"What is the maximum height?", "What is the minimum height?", "How many NULL pointers?",
                "How many min/max leaves?",
                "What is the maximum number of nodes a binary tree of height H may have?",
                "Full nodes (nodes with two children): how many minimum, maximum?",
                "Show that #full nodes + 1 == #leaves in a non-empty binary tree."};
        String[] kw = {"maximum", "minimum", "NULL", "leaves", "maximum number of nodes", "Full nodes", "#full nodes + 1 == #leaves"};
        Color[] kc = {Colors.ORANGE, Colors.BLUE, Colors.RED, Colors.GREEN, Colors.GOLD, Colors.PINK, Colors.TEAL};
        for (int i = 0; i < qs.length; i++) {
            q.addAll(question(i + 1, qs[i], -270 + 92 * i, 36, new String[]{kw[i]}, kc[i]));
            pause(0.7);
        }
        pause(2.2);
        fadeOutAll(d(1.0), q);
        pause(0.4);
    }

    private void recall() {
        List<MObject> q = new ArrayList<>();
        String[] qs = {"What is the maximum number of nodes a binary tree of height H may have?",
                "Full nodes (nodes with two children): how many minimum, maximum?",
                "Show that #full nodes + 1 == #leaves in a non-empty binary tree."};
        String[] kw = {"maximum number of nodes", "Full nodes", "#full nodes + 1 == #leaves"};
        Color[] kc = {Colors.GOLD, Colors.PINK, Colors.TEAL};
        for (int i = 0; i < 3; i++) {
            q.addAll(question(5 + i, qs[i], -200 + 130 * i, 38, new String[]{kw[i]}, kc[i]));
            pause(0.7);
        }
        pause(1.6);
        fadeOutAll(d(1.0), q);
        pause(0.4);
    }

    // ── shared helpers ───────────────────────────────────────────────

    private GN complete(int n) {
        GN[] a = new GN[n + 1];
        for (int i = 1; i <= n; i++) a[i] = bin("");
        for (int i = 1; i <= n; i++) {
            GN l = 2 * i <= n ? a[2 * i] : null, r = 2 * i + 1 <= n ? a[2 * i + 1] : null;
            a[i] = bin("", l, r);
            if (l != null) a[2 * i] = l;
            if (r != null) a[2 * i + 1] = r;
        }
        return build(1, n);
    }

    private GN build(int i, int n) {
        if (i > n) return null;
        return bin("", build(2 * i, n), build(2 * i + 1, n));
    }

    private GN chainTree(int n) {
        GN cur = bin("");
        for (int i = 1; i < n; i++) cur = bin("", null, cur);
        return cur;
    }

    private GT show(GN root, double cx, double topY, double gap, double pitchGap, double r) {
        GT tr = new GT(root, cx, topY, gap, pitchGap, r, 1, false, null, true);
        tr.build(0.3);
        mine.addAll(tr.parts());
        return tr;
    }

    private TextMob big(String s, double x, double y, Color c) { return label(s, x, y, 92, c, false, true); }

    private TextMob cap(String s, double x, double y, Color c) { return label(s, x, y, 28, c, false, true); }

    private void narr(String s, Color c) { sayAt(s, c, 0, 440, 34); }

    private void fadeIn(MObject... ms) {
        List<Animation> a = new ArrayList<>();
        for (MObject m : ms) a.add(new FadeIn(m, d(0.5)));
        playAll(a);
        for (MObject m : ms) mine.add(m);
    }

    private void result(String text, double y, Color c) {
        List<MObject> ans = chip(text, 540, y, 640, 70, c, 32);
        List<Animation> a = new ArrayList<>();
        for (MObject m : ans) a.add(new DropIn(m, 40, 0, d(0.6)));
        playAll(a);
        mine.addAll(ans);
    }

    private void wipe() {
        unsay();
        fadeOutAll(d(0.9), mine);
        mine.clear();
        pause(0.3);
    }

    // ── 1: maximum height ────────────────────────────────────────────

    private void maxHeight() {
        mine.addAll(solutionHeader(1, "Maximum height", -400));
        narr("Height here counts levels: a single node has height 1.", Colors.LIGHT_GRAY);
        GN ch = chainTree(6);
        t = new GT(ch, -350, -290, 100, 56, 22, 1, false, null, true);
        TextMob cnt = big("0", 540, -150, Colors.ORANGE);
        TextMob cc = cap("levels", 540, -80, Colors.GRAY);
        fadeIn(cnt, cc);
        mine.addAll(t.parts());
        int k = 0;
        for (GN n : t.nodes) {
            List<Animation> a = new ArrayList<>();
            t.showNode(a, n, 0, d(0.5));
            playAll(a);
            k++;
            cnt.setText(String.valueOf(k));
            pause(0.45);
        }
        narr("Each level needs at least one node, so N nodes make at most N levels: a chain.", Colors.ORANGE);
        result("maximum height = N", 120, Colors.GREEN);
        pause(2.6);
        wipe();
    }

    // ── 2: minimum height ────────────────────────────────────────────

    private void minHeight() {
        mine.addAll(solutionHeader(2, "Minimum height", -400));
        narr("Fill every level completely before starting the next one. Say N = 10.", Colors.LIGHT_GRAY);
        t = show(complete(10), -350, -290, 105, 40, 22);
        pause(0.4);
        int[] cap = {1, 2, 4, 8}, got = {1, 2, 4, 3};
        List<MObject> rows = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            TextMob r = label("level " + (i + 1) + ":  room for " + cap[i] + ",  used " + got[i], 560, -250 + 70 * i, 32, Colors.WHITE, false, true);
            List<Animation> a = new ArrayList<>();
            a.add(new FadeIn(r, d(0.5)));
            for (GN n : t.nodes) if (n.depth == i) t.paint(a, n, Colors.GOLD, d(0.5));
            playAll(a);
            rows.add(r);
            mine.add(r);
            pause(0.6);
            List<Animation> b = new ArrayList<>();
            for (GN n : t.nodes) if (n.depth == i) t.paintDefault(b, n, d(0.5));
            playAll(b);
        }
        LaTeXMob f = latex("N \\leq 2^{H} - 1 \\;\\Rightarrow\\; H \\geq \\log_{2}(N+1)", 52, 560, 80);
        fadeIn(f);
        narr("H levels hold at most 2^H - 1 nodes, so the height cannot be below log2(N + 1).", Colors.ORANGE);
        result("minimum height ≈ log2(N)", 220, Colors.GREEN);
        pause(2.6);
        wipe();
    }

    // ── 3: NULL pointers ─────────────────────────────────────────────

    private void nullPointers() {
        mine.addAll(solutionHeader(3, "NULL pointers", -400));
        GN root = bin("A", bin("B", bin("D"), bin("E")), bin("C", null, bin("F")));
        t = new GT(root, -350, -290, 130, 80, 24, 26, false, null, true);
        t.build(0.3);
        mine.addAll(t.parts());
        narr("Every node has two pointer slots, left and right: 2N slots for N nodes.", Colors.LIGHT_GRAY);
        List<MObject> slots = new ArrayList<>();
        List<CircleMob> hollow = new ArrayList<>();
        int used = 0;
        List<Animation> a = new ArrayList<>();
        for (GN n : t.nodes) {
            for (int s = 0; s < 2; s++) {
                boolean taken = (s == 0 && leftOf(n) != null) || (s == 1 && rightOf(n) != null);
                CircleMob c = new CircleMob(8);
                c.setPosition(n.x + (s == 0 ? -20 : 20), n.y + 50);
                if (taken) {
                    c.setFillColor(Colors.GOLD);
                    c.setStrokeColor(Color.TRANSPARENT);
                    used++;
                } else {
                    c.setFillColor(Color.TRANSPARENT);
                    c.setStrokeColor(Colors.LIGHT_GRAY);
                    c.setStrokeWidth(2.2);
                    hollow.add(c);
                }
                c.setOpacity(0);
                add(c);
                slots.add(c);
                a.add(new FadeIn(c, d(0.8)));
            }
        }
        playAll(a);
        mine.addAll(slots);
        TextMob s1 = label("slots: 2N = " + 2 * t.nodes.size(), 560, -240, 36, Colors.WHITE, false, true);
        fadeIn(s1);
        pause(0.8);
        narr("A pointer is used exactly once for every edge: N - 1 of them (gold).", Colors.GOLD);
        TextMob s2 = label("used (edges): N - 1 = " + used, 560, -170, 36, Colors.GOLD, false, true);
        fadeIn(s2);
        pause(1.4);
        narr("The rest are NULL: 2N - (N - 1) = N + 1. Count the hollow ones.", Colors.LIGHT_GRAY);
        TextMob cnt = big("0", 560, -40, Colors.RED);
        TextMob cc = cap("NULL pointers", 560, 30, Colors.GRAY);
        fadeIn(cnt, cc);
        int k = 0;
        for (CircleMob c : hollow) {
            play(new ScaleTo(c, 1.8, d(0.15)));
            play(new ScaleTo(c, 1.0, d(0.15)));
            k++;
            cnt.setText(String.valueOf(k));
        }
        pause(0.5);
        result("NULL pointers = N + 1", 170, Colors.GREEN);
        pause(2.6);
        wipe();
    }

    // ── 4: leaves ────────────────────────────────────────────────────

    private void leaves() {
        mine.addAll(solutionHeader(4, "Minimum and maximum leaves", -400));
        GT c1 = show(chainTree(7), -560, -290, 90, 40, 20);
        GT c2 = show(complete(7), 300, -290, 120, 36, 20);
        TextMob l1 = cap("a chain: N = 7", -560, -340, Colors.LIGHT_GRAY);
        TextMob l2 = cap("a complete tree: N = 7", 300, -340, Colors.LIGHT_GRAY);
        fadeIn(l1, l2);
        pause(0.5);
        List<Animation> a = new ArrayList<>();
        for (GN n : c1.nodes) if (n.leaf()) c1.paint(a, n, Colors.GOLD, d(0.6));
        playAll(a);
        TextMob r1 = big("1", -560, 190, Colors.GOLD);
        TextMob r1c = cap("leaf", -560, 250, Colors.GRAY);
        fadeIn(r1, r1c);
        narr("A chain has a single leaf, at its end: the minimum is 1 (the slide's 0 is the empty tree).", Colors.GOLD);
        pause(2.2);
        List<Animation> b = new ArrayList<>();
        for (GN n : c2.nodes) if (n.leaf()) c2.paint(b, n, Colors.GREEN, d(0.6));
        playAll(b);
        TextMob r2 = big("4", 300, 190, Colors.GREEN);
        TextMob r2c = cap("leaves", 300, 250, Colors.GRAY);
        fadeIn(r2, r2c);
        narr("In a complete tree about half of the nodes are leaves: at most N/2, exactly ⌈N/2⌉.", Colors.GREEN);
        pause(3.0);
        wipe();
    }

    // ── part 2 ───────────────────────────────────────────────────────

    private void maxNodes() {
        mine.addAll(solutionHeader(5, "Maximum nodes for height H", -400));
        t = show(complete(15), -350, -290, 100, 24, 18);
        TextMob h = cap("H = 4", -350, -345, Colors.LIGHT_GRAY);
        fadeIn(h);
        int[] room = {1, 2, 4, 8};
        int sum = 0;
        TextMob tot = big("0", 560, 130, Colors.GOLD);
        TextMob tc = cap("nodes", 560, 200, Colors.GRAY);
        fadeIn(tot, tc);
        for (int i = 0; i < 4; i++) {
            TextMob r = label("level " + (i + 1) + ":  " + room[i], 560, -270 + 64 * i, 34, Colors.WHITE, false, true);
            List<Animation> a = new ArrayList<>();
            a.add(new FadeIn(r, d(0.5)));
            for (GN n : t.nodes) if (n.depth == i) t.paint(a, n, Colors.GOLD, d(0.5));
            playAll(a);
            mine.add(r);
            sum += room[i];
            tot.setText(String.valueOf(sum));
            pause(0.7);
        }
        narr("Every level can hold twice the one above: 1 + 2 + 4 + 8 = 15.", Colors.LIGHT_GRAY);
        LaTeXMob f = latex("1 + 2 + \\cdots + 2^{H-1} = 2^{H} - 1", 50, 560, 20);
        fadeIn(f);
        result("at most 2^H - 1 nodes", 290, Colors.GREEN);
        pause(2.6);
        wipe();
    }

    private Color kind(GN n) {
        int k = n.kids.size();
        return k == 2 ? Colors.GOLD : k == 1 ? Colors.TEAL : Colors.BLUE;
    }

    private void paintKinds(GT tr, double dur) {
        List<Animation> a = new ArrayList<>();
        for (GN n : tr.nodes) tr.paint(a, n, kind(n), dur);
        playAll(a);
    }

    private void fullNodes() {
        mine.addAll(solutionHeader(6, "Full nodes", -400));
        GT c1 = show(chainTree(7), -560, -290, 90, 40, 20);
        GT c2 = show(complete(7), 300, -290, 120, 36, 20);
        fadeIn(cap("a chain: N = 7", -560, -340, Colors.LIGHT_GRAY), cap("a complete tree: N = 7", 300, -340, Colors.LIGHT_GRAY));
        narr("A full node has two children (gold).", Colors.LIGHT_GRAY);
        pause(0.6);
        paintKinds(c1, d(0.6));
        TextMob r1 = big("0", -560, 190, Colors.GOLD);
        fadeIn(r1, cap("full nodes", -560, 250, Colors.GRAY));
        narr("A chain has none: the minimum is 0.", Colors.GOLD);
        pause(2.0);
        paintKinds(c2, d(0.6));
        TextMob r2 = big("3", 300, 190, Colors.GOLD);
        fadeIn(r2, cap("full nodes", 300, 250, Colors.GRAY));
        narr("Every full node adds a second child, so at most about N/2 - 1: here (7 - 1) / 2 = 3.", Colors.GREEN);
        pause(3.0);
        wipe();
    }

    private int leavesOf(List<GN> shown) {
        int k = 0;
        for (GN n : shown) if (n.kids.isEmpty()) k++;
        return k;
    }

    private int fullOf(List<GN> shown) {
        int k = 0;
        for (GN n : shown) if (shown.contains(leftOf(n)) && shown.contains(rightOf(n))) k++;
        return k;
    }

    private void proof() {
        mine.addAll(solutionHeader(7, "#full nodes + 1 = #leaves", -400));
        GN a = bin("A"), b = bin("B"), c = bin("C"), d0 = bin("D"), e = bin("E"), f = bin("F"), g0 = bin("G");
        GN full = bin("A", bin("B", bin("D"), bin("E")), bin("C", bin("F"), bin("G")));
        t = new GT(full, -350, -290, 130, 90, 24, 26, false, null, true);
        mine.addAll(t.parts());
        TextMob lc = big("0", 560, -250, Colors.GREEN);
        TextMob lcc = cap("leaves", 560, -180, Colors.GRAY);
        TextMob fc = big("0", 560, -60, Colors.GOLD);
        TextMob fcc = cap("full nodes + 1", 560, 10, Colors.GRAY);
        fadeIn(lc, lcc, fc, fcc);
        narr("Grow a tree one node at a time and watch both numbers.", Colors.LIGHT_GRAY);
        pause(0.8);
        String[] order = {"A", "B", "C", "D", "E", "F", "G"};
        String[] why = {"one node: it is a leaf, no full node", "A gets a first child: the leaf count stays 1",
                "A gets a second child: A is now full, a new leaf appears", "B gets a first child: still no change",
                "B gets a second child: B becomes full, one more leaf", "C gets a first child: no change",
                "C gets a second child: C becomes full, one more leaf"};
        List<GN> shown = new ArrayList<>();
        for (int i = 0; i < order.length; i++) {
            GN n = null;
            for (GN x : t.nodes) if (x.name.equals(order[i])) n = x;
            shown.add(n);
            List<Animation> an = new ArrayList<>();
            t.showNode(an, n, 0, d(0.6));
            playAll(an);
            lc.setText(String.valueOf(leavesOf(shown)));
            fc.setText(String.valueOf(fullOf(shown) + 1));
            narr(why[i], Colors.LIGHT_GRAY);
            pause(1.0);
        }
        narr("The two numbers never part: growing a leaf into a full node adds one of each.", Colors.GREEN);
        List<Animation> pk = new ArrayList<>();
        for (GN n : t.nodes) t.paint(pk, n, kind(n), d(0.6));
        playAll(pk);
        pause(2.2);
        // the counting argument
        wipe();
        mine.addAll(solutionHeader(7, "The same thing by counting", -400));
        LaTeXMob l1 = latex("N = n_0 + n_1 + n_2", 60, 0, -230);
        fadeIn(l1);
        TextMob t1 = cap("n0 leaves, n1 nodes with one child, n2 full nodes", 0, -160, Colors.GRAY);
        fadeIn(t1);
        pause(1.6);
        LaTeXMob l2 = latex("N - 1 = n_1 + 2\\,n_2", 60, 0, -60);
        fadeIn(l2);
        TextMob t2 = cap("every edge is one child of some node: N - 1 children in all", 0, 10, Colors.GRAY);
        fadeIn(t2);
        pause(1.8);
        LaTeXMob l3 = latex("1 = n_0 - n_2 \\;\\Rightarrow\\; n_0 = n_2 + 1", 60, 0, 120);
        fadeIn(l3);
        TextMob t3 = cap("subtract the second line from the first", 0, 190, Colors.GRAY);
        fadeIn(t3);
        pause(1.0);
        result("#leaves = #full nodes + 1", 330, Colors.GREEN);
        pause(3.0);
        wipe();
    }
}
