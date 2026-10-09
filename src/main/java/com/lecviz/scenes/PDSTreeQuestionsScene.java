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
 * Standalone clip for slide 29 of the trees deck: "Some Questions?". The slide only asks; the answers here are ours
 * (a node with two parents, undirected edges, weights, several roots, parallel edges, a tree drawn upside down),
 * each shown on a small picture.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeQuestionsScene extends PDSTreeClipBase {

    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();

    @Override
    public void construct() {
        head = writeHeading("Some Questions?");
        pause(0.5);
        questions();
        parents();
        undirected();
        weights();
        roots();
        multi();
        flipped();
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void questions() {
        List<MObject> q = new ArrayList<>();
        String[] qs = {"What if a child node is common to two parents?", "Can the edge be undirected?",
                "Can the edges have weights?", "Can there be multiple roots?",
                "Can there be multiple edges between two nodes?", "Is it okay to draw a tree with root at the bottom?"};
        String[] kw = {"two parents", "undirected", "weights", "multiple roots", "multiple edges", "root at the bottom"};
        Color[] kc = {Colors.PINK, Colors.BLUE, Colors.GOLD, Colors.ORANGE, Colors.RED, Colors.GREEN};
        for (int i = 0; i < 6; i++) {
            q.addAll(question(i + 1, qs[i], -330 + 100 * i, 40, new String[]{kw[i]}, kc[i]));
            if (i == 0) q.add(subLine("Ancestry", -330 + 46, 30));
            pause(0.7);
        }
        pause(2.0);
        fadeOutAll(d(1.0), q);
        pause(0.4);
    }

    private void narr(String s, Color c) { sayAt(s, c, 0, 440, 34); }

    private void wrap() {
        unsay();
        fadeOutAll(d(0.9), mine);
        mine.clear();
        pause(0.3);
    }

    private void verdict(String text, Color c) {
        List<MObject> ans = chip(text, 0, 340, 760, 74, c, 36);
        List<Animation> a = new ArrayList<>();
        for (MObject m : ans) a.add(new DropIn(m, 40, 0, d(0.6)));
        playAll(a);
        mine.addAll(ans);
    }

    private GT show(GN root, double cx, double topY, boolean arrows, Color all) {
        GT t = new GT(root, cx, topY, 120, 70, 30, 30, arrows, all == null ? null : n -> all);
        t.build(0.3);
        mine.addAll(t.parts());
        return t;
    }

    // ── 1: a child with two parents ──────────────────────────────────

    private void parents() {
        mine.addAll(solutionHeader(1, "A child common to two parents", -400));
        GN m = g("Mother"), f = g("Father"), c = g("Child");
        GN gm = g("Mother", g("Child")), gf = g("Father", g("Child"));
        GT t1 = new GT(gm, -260, -230, 150, 60, 30, 28, true, null);
        GT t2 = new GT(gf, 260, -230, 150, 60, 30, 28, true, null);
        t1.build(0.3);
        t2.build(0.3);
        mine.addAll(t1.parts());
        mine.addAll(t2.parts());
        narr("Two separate parent-child pairs...", Colors.LIGHT_GRAY);
        pause(1.2);
        // the two children are the same person: draw the second arrow to the first child
        GN c1 = gm.kids.get(0);
        Link l = arrow(gf.x - 20, gf.y + 32, c1.x + 24, c1.y - 30, Colors.RED, 4);
        play(new DrawLink(l, d(1.0)));
        mine.add(l);
        List<Animation> a = new ArrayList<>();
        t1.paint(a, c1, Colors.RED, d(0.6));
        playAll(a);
        narr("...and one child shared by both: this node has two parents.", Colors.RED);
        pause(1.8);
        TextMob x = label("with the ancestry of a person, this is normal", 0, 150, 30, Colors.LIGHT_GRAY, false, false);
        play(new FadeIn(x, d(0.6)));
        mine.add(x);
        verdict("not a tree any more (a graph)", Colors.ORANGE);
        pause(3.0);
        wrap();
    }

    // ── 2: undirected edges ──────────────────────────────────────────

    private void undirected() {
        mine.addAll(solutionHeader(2, "Undirected edges", -400));
        GN r = g("A", g("B", g("D"), g("E")), g("C"));
        GT t = show(r, 0, -250, true, null);
        narr("Arrows from a parent to its children...", Colors.LIGHT_GRAY);
        pause(1.4);
        GN r2 = g("A", g("B", g("D"), g("E")), g("C"));
        GT plain = new GT(r2, 0, -250, 120, 70, 30, 30, false, null);
        mine.addAll(plain.parts());
        List<Animation> sw = new ArrayList<>();
        for (GN n : t.nodes) if (n.edge != null) sw.add(new FadeOut(n.edge, d(1.0)));
        for (GN n : plain.nodes) if (n.edge != null) sw.add(new DrawLinkAt(n.edge, 0, d(1.0)));
        playAll(sw);
        narr("...can be dropped: once a root is chosen, 'away from the root' tells the direction.", Colors.GREEN);
        pause(2.6);
        verdict("yes: undirected, connected, no loops", Colors.GREEN);
        pause(3.0);
        wrap();
    }

    // ── 3: weights ───────────────────────────────────────────────────

    private void weights() {
        mine.addAll(solutionHeader(3, "Weights on the edges", -400));
        GN r = g("A", g("B", g("D"), g("E")), g("C", g("F")));
        GT t = show(r, 0, -250, false, null);
        int[] w = {4, 7, 3, 5, 2};
        int k = 0;
        List<TextMob> tags = new ArrayList<>();
        List<Animation> a = new ArrayList<>();
        for (GN n : t.nodes) {
            if (n.parent == null) continue;
            TextMob tg = label(String.valueOf(w[k++]), (n.parent.x + n.x) / 2 + (n.x < n.parent.x ? -22 : 22), (n.parent.y + n.y) / 2, 28, Colors.GOLD, false, true);
            a.add(new FadeIn(tg, d(0.6)));
            tags.add(tg);
        }
        playAll(a);
        mine.addAll(tags);
        narr("An edge can carry a number: a distance, a cost, a time.", Colors.LIGHT_GRAY);
        pause(1.6);
        // the cost of a path is the sum of its weights
        GN leaf = at(r, 0, 1);
        List<Animation> p = new ArrayList<>();
        t.paintEdge(p, at(r, 0), Colors.GOLD, d(0.5));
        t.paintEdge(p, leaf, Colors.GOLD, d(0.5));
        t.paint(p, at(r, 0), Colors.GOLD, d(0.5));
        t.paint(p, leaf, Colors.GOLD, d(0.5));
        playAll(p);
        TextMob sum = label("A to E:  4 + 3 = 7", 0, 200, 40, Colors.GOLD, false, true);
        play(new FadeIn(sum, d(0.6)));
        mine.add(sum);
        narr("The length of a path is then the sum of the weights along it.", Colors.GOLD);
        verdict("yes: weighted trees", Colors.GREEN);
        pause(3.0);
        wrap();
    }

    // ── 4: several roots ─────────────────────────────────────────────

    private void roots() {
        mine.addAll(solutionHeader(4, "Several roots", -400));
        GN a = g("A", g("B"), g("C")), b = g("P", g("Q"), g("R", g("S")));
        GT t1 = new GT(a, -330, -240, 120, 70, 30, 30, true, null);
        GT t2 = new GT(b, 330, -240, 120, 70, 30, 30, true, null);
        t1.build(0.3);
        t2.build(0.3);
        mine.addAll(t1.parts());
        mine.addAll(t2.parts());
        narr("Two roots means two separate trees, not one.", Colors.LIGHT_GRAY);
        pause(1.2);
        RectMob b1 = subBox(a, Colors.TEAL, 60, 50, 50);
        RectMob b2 = subBox(b, Colors.PINK, 60, 50, 50);
        play(new FadeIn(b1, d(0.6)), new FadeIn(b2, d(0.6)));
        mine.add(b1);
        mine.add(b2);
        narr("Every node is reachable from THE root. A set of trees is called a forest.", Colors.ORANGE);
        verdict("no: one tree, one root (many trees: a forest)", Colors.ORANGE);
        pause(3.0);
        wrap();
    }

    // ── 5: parallel edges ────────────────────────────────────────────

    private void multi() {
        mine.addAll(solutionHeader(5, "Two edges between two nodes", -400));
        GN r = g("A", g("B", g("D")), g("C"));
        GT t = show(r, 0, -250, false, null);
        narr("Add a second edge between A and B.", Colors.LIGHT_GRAY);
        pause(1.0);
        GN b = at(r, 0);
        Link e2 = new Link(new double[]{r.x - 40, r.x - 100, b.x - 34}, new double[]{r.y + 26, (r.y + b.y) / 2 - 10, b.y - 20},
                Colors.RED, 4, false);
        add(e2);
        play(new DrawLink(e2, d(1.0)));
        mine.add(e2);
        List<Animation> a = new ArrayList<>();
        t.paint(a, r, Colors.RED, d(0.5));
        t.paint(a, b, Colors.RED, d(0.5));
        playAll(a);
        narr("Now there are two different paths from A to B: a loop. A tree has exactly one.", Colors.RED);
        verdict("no: it would make a cycle", Colors.RED);
        pause(3.0);
        wrap();
    }

    // ── 6: a tree with its root at the bottom ────────────────────────

    private void flipped() {
        mine.addAll(solutionHeader(6, "Root at the bottom", -400));
        GN r = g("A", g("B", g("D"), g("E")), g("C", g("F")));
        GT t = show(r, 0, -250, true, null);
        narr("Only the picture changes: it is the same tree.", Colors.LIGHT_GRAY);
        pause(1.4);
        double[][] xy = new double[t.nodes.size()][2];
        for (int i = 0; i < xy.length; i++) {
            xy[i][0] = t.nodes.get(i).x;
            xy[i][1] = -250 + 2 * 120 - (t.nodes.get(i).y + 250) + 0;
        }
        List<Animation> a = new ArrayList<>();
        t.relocate(a, xy, d(2.0));
        playAll(a);
        narr("Roots at the bottom, like a real tree: nothing in the definition depends on up or down.", Colors.GREEN);
        verdict("yes: it is only a way of drawing", Colors.GREEN);
        pause(3.2);
        wrap();
    }
}
