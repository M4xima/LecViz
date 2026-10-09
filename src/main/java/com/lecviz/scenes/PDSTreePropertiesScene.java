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
 * Standalone clip for slide 6 of the trees deck: properties of a tree (classwork style).
 *
 *   - all four questions first, one at a time (six circles drawn beside the first)
 *   - then one solution section per question, each with its own header:
 *       1 minimum number of edges: six lonely nodes joined edge by edge, pieces counted down 6, 5, ... 1
 *       2 maximum: a sixth edge always closes a loop
 *       3 N nodes: every node except the root owns one parent edge, so N - 1
 *       4 paths between two nodes: climb to the common ancestor and come down; there is only one way
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreePropertiesScene extends PDSTreeClipBase {

    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();

    @Override
    public void construct() {
        head = writeHeading("Properties");
        pause(0.5);
        questions();
        minimum();
        maximum();
        general();
        paths();
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    // ── all the questions first ──────────────────────────────────────

    private void questions() {
        List<MObject> q = new ArrayList<>();
        StrokeTextMob ctx = strokeLeft("A tree has six nodes.", -790, -355, 44, Colors.WHITE);
        play(new Write(ctx, d(1.8)));
        q.add(ctx);
        List<Animation> six = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            CircleMob c = new CircleMob(24);
            c.setFillColor(Colors.withAlpha(Colors.BLUE, 0.3));
            c.setStrokeColor(Colors.BLUE);
            c.setStrokeWidth(3);
            c.setPosition(500 + (i % 3) * 90, -385 + (i / 3) * 70);
            c.setOpacity(0);
            add(c);
            six.add(new FadeInAt(c, 0.12 * i, d(0.5)));
            q.add(c);
        }
        playAll(six);
        pause(0.5);
        q.addAll(question(1, "What is the minimum number of edges in the tree?", -230, 40, new String[]{"minimum"}, Colors.BLUE));
        pause(0.8);
        q.addAll(question(2, "What is the maximum?", -110, 40, new String[]{"maximum"}, Colors.ORANGE));
        pause(0.8);
        q.addAll(question(3, "Generalization for N nodes?", 10, 40, new String[]{"Generalization"}, Colors.GREEN));
        pause(0.8);
        q.addAll(question(4, "How many (undirected) paths exist between two nodes?", 130, 40, new String[]{"paths"}, Colors.PINK));
        pause(2.0);
        fadeOutAll(d(1.0), q);
        pause(0.4);
    }

    // ── shared stage for the solutions ───────────────────────────────

    private GT t;
    private GN rootN;

    private GT stage(boolean neutral, double cx) {
        rootN = g("A", g("B", g("D"), g("E")), g("C", g("F")));
        return new GT(rootN, cx, -250, 150, 150, 34, 34, false, neutral ? n -> Colors.BLUE : null);
    }

    private void nodesOnly(GT tr) {
        List<Animation> a = new ArrayList<>();
        int i = 0;
        for (GN n : tr.nodes) {
            a.add(new FadeInAt(n.shape, 0.15 * i, d(0.5)));
            a.add(new FadeInAt(n.text, 0.15 * i, d(0.5)));
            i++;
        }
        playAll(a);
    }

    private TextMob big(String s, double x, double y, Color c) {
        return label(s, x, y, 92, c, false, true);
    }

    private void clearStage() {
        fadeOutAll(d(0.9), mine);
        mine.clear();
        unsay();
        pause(0.3);
    }

    // ── 1: minimum ───────────────────────────────────────────────────

    private void minimum() {
        mine.addAll(solutionHeader(1, "Minimum number of edges", -380));
        t = stage(true, -250);
        nodesOnly(t);
        mine.addAll(t.parts());
        TextMob e0 = big("0", 560, -160, Colors.GOLD);
        TextMob ec = label("edges", 560, -85, 30, Colors.GRAY, false, true);
        TextMob p0 = big("6", 560, 40, Colors.TEAL);
        TextMob pc = label("separate pieces", 560, 115, 30, Colors.GRAY, false, true);
        play(new FadeIn(e0, d(0.5)), new FadeIn(ec, d(0.5)), new FadeIn(p0, d(0.5)), new FadeIn(pc, d(0.5)));
        mine.add(e0);
        mine.add(ec);
        mine.add(p0);
        mine.add(pc);
        sayAt("Six nodes, no edges: six pieces.", Colors.LIGHT_GRAY, 0, 400, 36);
        pause(1.2);
        sayAt("Every edge can join at most two pieces into one.", Colors.LIGHT_GRAY, 0, 400, 36);
        pause(0.8);
        GN[] order = {at(rootN, 0), at(rootN, 1), at(rootN, 0, 0), at(rootN, 0, 1), at(rootN, 1, 0)};
        for (int k = 0; k < order.length; k++) {
            GN n = order[k];
            List<Animation> a = new ArrayList<>();
            a.add(new DrawLinkAt(n.edge, 0, d(0.8)));
            playAll(a);
            e0.setText(String.valueOf(k + 1));
            p0.setText(String.valueOf(5 - k));
            if (k == 3) {
                List<Animation> red = new ArrayList<>();
                t.paint(red, at(rootN, 1, 0), Colors.RED, d(0.5));
                playAll(red);
                TextMob un = label("F is cut off", at(rootN, 1, 0).x, at(rootN, 1, 0).y + 66, 28, Colors.RED, false, true);
                play(new FadeIn(un, d(0.5)));
                mine.add(un);
                sayAt("Four edges leave two pieces: F cannot be reached.", Colors.RED, 0, 400, 36);
                pause(2.0);
                play(new FadeOut(un, d(0.4)));
            } else if (k == 4) {
                List<Animation> ok = new ArrayList<>();
                t.paint(ok, at(rootN, 1, 0), Colors.BLUE, d(0.5));
                playAll(ok);
                sayAt("The fifth edge joins the last piece: connected.", Colors.GREEN, 0, 400, 36);
            }
            pause(0.5);
        }
        pause(0.8);
        List<MObject> ans = chip("minimum = 5 edges", 560, 250, 440, 70, Colors.GREEN, 34);
        play(new FadeIn(ans.get(0), d(0.6)), new FadeIn(ans.get(1), d(0.6)));
        mine.addAll(ans);
        pause(2.4);
        clearStage();
    }

    // ── 2: maximum ───────────────────────────────────────────────────

    private void maximum() {
        mine.addAll(solutionHeader(2, "Maximum number of edges", -380));
        t = stage(true, -250);
        nodesOnly(t);
        List<Animation> a = new ArrayList<>();
        for (GN n : t.nodes) if (n.edge != null) a.add(new DrawLinkAt(n.edge, 0, d(0.8)));
        playAll(a);
        mine.addAll(t.parts());
        TextMob e0 = big("5", 560, -120, Colors.GOLD);
        TextMob ec = label("edges, all pieces joined", 560, -45, 28, Colors.GRAY, false, true);
        play(new FadeIn(e0, d(0.5)), new FadeIn(ec, d(0.5)));
        mine.add(e0);
        mine.add(ec);
        sayAt("Everything is already connected. Try one more edge.", Colors.LIGHT_GRAY, 0, 400, 36);
        pause(1.2);

        // D - E closes the loop B - D - E
        GN dN = at(rootN, 0, 0), eN = at(rootN, 0, 1), bN = at(rootN, 0);
        Link extra = arrow(dN.x + 34, dN.y, eN.x - 34, eN.y, Colors.RED, 5);
        play(new DrawLink(extra, d(0.8)));
        List<Animation> red = new ArrayList<>();
        t.paint(red, bN, Colors.RED, d(0.5));
        t.paint(red, dN, Colors.RED, d(0.5));
        t.paint(red, eN, Colors.RED, d(0.5));
        playAll(red);
        e0.setText("6");
        sayAt("D - E closes a loop B - D - E: now two paths join B and E.", Colors.RED, 0, 400, 36);
        pause(2.6);
        List<Animation> undo = new ArrayList<>();
        undo.add(new FadeOut(extra, d(0.6)));
        t.paint(undo, bN, INNER_C, d(0.6));
        t.paint(undo, dN, LEAF_C, d(0.6));
        t.paint(undo, eN, LEAF_C, d(0.6));
        playAll(undo);
        remove(extra);
        e0.setText("5");

        // D - F closes a longer loop D - B - A - C - F
        GN fN = at(rootN, 1, 0), cN = at(rootN, 1);
        Link extra2 = new Link(new double[]{dN.x, dN.x + 14, fN.x - 14, fN.x},
                new double[]{dN.y + 34, dN.y + 100, fN.y + 100, fN.y + 36}, Colors.RED, 5, true);
        add(extra2);
        play(new DrawLink(extra2, d(0.9)));
        List<Animation> red2 = new ArrayList<>();
        for (GN n : new GN[]{dN, bN, rootN, cN, fN}) t.paint(red2, n, Colors.RED, d(0.5));
        playAll(red2);
        e0.setText("6");
        sayAt("D - F: F would have two parents (C and D), and D - B - A - C - F is a loop.", Colors.RED, 0, 400, 34);
        pause(2.8);
        List<Animation> undo2 = new ArrayList<>();
        undo2.add(new FadeOut(extra2, d(0.6)));
        t.paint(undo2, rootN, ROOT_C, d(0.6));
        t.paint(undo2, bN, INNER_C, d(0.6));
        t.paint(undo2, cN, INNER_C, d(0.6));
        t.paint(undo2, dN, LEAF_C, d(0.6));
        t.paint(undo2, fN, LEAF_C, d(0.6));
        playAll(undo2);
        remove(extra2);
        e0.setText("5");
        List<MObject> ans = chip("maximum = 5 edges", 560, 250, 440, 70, Colors.GREEN, 34);
        sayAt("So the minimum and the maximum are the same: 5.", Colors.GREEN, 0, 400, 36);
        play(new FadeIn(ans.get(0), d(0.6)), new FadeIn(ans.get(1), d(0.6)));
        mine.addAll(ans);
        pause(2.4);
        clearStage();
    }

    // ── 3: N nodes ───────────────────────────────────────────────────

    private void general() {
        mine.addAll(solutionHeader(3, "Generalization for N nodes", -380));
        t = stage(false, -250);
        t.build(0.3);
        mine.addAll(t.parts());
        sayAt("Every node except the root has exactly one parent edge.", Colors.LIGHT_GRAY, 0, 400, 36);
        pause(0.8);
        TextMob cnt = big("0", 560, -120, Colors.GOLD);
        TextMob cc = label("parent edges counted", 560, -45, 28, Colors.GRAY, false, true);
        play(new FadeIn(cnt, d(0.5)), new FadeIn(cc, d(0.5)));
        mine.add(cnt);
        mine.add(cc);
        TextMob noPar = label("root: no parent edge", rootN.x + 215, rootN.y, 28, Colors.MAROON, false, true);
        play(new FadeIn(noPar, d(0.6)));
        mine.add(noPar);
        pause(0.8);
        int k = 0;
        for (GN n : t.nodes) {
            if (n.parent == null) continue;
            List<Animation> a = new ArrayList<>();
            t.paint(a, n, Colors.GOLD, d(0.4));
            t.paintEdge(a, n, Colors.GOLD, d(0.4));
            playAll(a);
            k++;
            cnt.setText(String.valueOf(k));
            pause(0.5);
        }
        pause(0.6);
        sayAt("N nodes, one of them the root: N - 1 edges.", Colors.GREEN, 0, 400, 38);
        LaTeXMob f = latex("\\text{edges} = N - 1", 62, 560, 90);
        play(new FadeIn(f, d(0.8)));
        mine.add(f);
        LaTeXMob f2 = latex("6 - 1 = 5", 54, 560, 175);
        play(new FadeIn(f2, d(0.8)));
        mine.add(f2);
        pause(2.6);
        clearStage();
    }

    // ── 4: paths ─────────────────────────────────────────────────────

    private void paths() {
        mine.addAll(solutionHeader(4, "Paths between two nodes", -380));
        t = stage(false, -250);
        t.build(0.3);
        mine.addAll(t.parts());
        TextMob cnt = big("0", 560, -120, Colors.PINK);
        TextMob cc = label("paths found", 560, -45, 28, Colors.GRAY, false, true);
        play(new FadeIn(cnt, d(0.5)), new FadeIn(cc, d(0.5)));
        mine.add(cnt);
        mine.add(cc);
        GN[][] pairs = {{at(rootN, 0, 0), at(rootN, 1, 0)}, {at(rootN, 0, 1), at(rootN, 1)}};
        for (GN[] pr : pairs) {
            GN u = pr[0], v = pr[1];
            List<GN> up = new ArrayList<>(), down = new ArrayList<>();
            for (GN n = u; n != null; n = n.parent) up.add(n);
            for (GN n = v; n != null; n = n.parent) down.add(n);
            GN lca = null;
            for (GN n : up) if (down.contains(n)) { lca = n; break; }
            List<GN> path = new ArrayList<>();
            for (GN n : up) { path.add(n); if (n == lca) break; }
            List<GN> tail = new ArrayList<>();
            for (GN n : down) { if (n == lca) break; tail.add(0, n); }
            path.addAll(tail);
            sayAt("From " + u.name + " to " + v.name + ": climb up to " + lca.name + ", then come down.", Colors.LIGHT_GRAY, 0, 400, 36);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < path.size(); i++) {
                GN n = path.get(i);
                List<Animation> a = new ArrayList<>();
                t.paint(a, n, Colors.PINK, d(0.4));
                if (i > 0) {
                    GN pv = path.get(i - 1);
                    t.paintEdge(a, pv.parent == n ? pv : n, Colors.PINK, d(0.4));
                }
                playAll(a);
                sb.append(i == 0 ? "" : " - ").append(n.name);
            }
            TextMob pl = label(sb.toString(), 560, 60, 40, Colors.PINK, false, true);
            play(new FadeIn(pl, d(0.5)));
            cnt.setText("1");
            pause(2.0);
            List<Animation> b = new ArrayList<>();
            b.add(new FadeOut(pl, d(0.5)));
            for (GN n : path) {
                t.paintDefault(b, n, d(0.5));
                t.paintEdge(b, n, Colors.withAlpha(Colors.LIGHT_GRAY, 0.8), d(0.5));
            }
            playAll(b);
            remove(pl);
            cnt.setText("0");
        }
        sayAt("A second path would have to leave the first one and rejoin it: a loop.", Colors.ORANGE, 0, 400, 34);
        pause(2.6);
        cnt.setText("1");
        List<MObject> ans = chip("exactly 1 path", 560, 160, 420, 70, Colors.GREEN, 34);
        play(new FadeIn(ans.get(0), d(0.6)), new FadeIn(ans.get(1), d(0.6)));
        mine.addAll(ans);
        pause(2.6);
        clearStage();
    }
}
