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
 * Standalone clip for slide 5 of the trees deck: more nomenclature.
 *
 *   - the slide's six lines one at a time
 *   - a gray tree takes its colors one rule at a time: the root (no parent), the leaves (no children),
 *     the internal nodes (everything else)
 *   - reachable from the root: a gold trail runs down to two nodes; the whole tree hangs from one
 *     "root" pointer; every node heads a subtree of its own (boxes around subtrees)
 *   - the slide's small trees: empty, one node, two nodes, and the two shapes of three nodes
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeRolesScene extends PDSTreeClipBase {

    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();

    @Override
    public void construct() {
        head = writeHeading("Nomenclature");
        pause(0.5);
        slideText();
        roles();
        reach();
        catalogue();
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void slideText() {
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Root has no parent.").kw("Root", Colors.MAROON));
        s.add(ln(0, "Leaves have no children.").kw("Leaves", Colors.BLUE));
        s.add(ln(0, "Non-leaves are internal nodes.").kw("internal nodes", Colors.ORANGE));
        s.add(ln(0, "Each node is reachable from the root.").kw("reachable", Colors.GREEN));
        s.add(ln(0, "The whole tree can be accessed via root."));
        s.add(ln(0, "Each node can be viewed as the root of its unique subtree.").kw("subtree", Colors.TEAL));
        List<List<MObject>> text = writeSlide(s, -320);
        pause(1.4);
        swipeAway(text);
        pause(0.4);
    }

    private GT t;
    private GN rootN;

    private void roles() {
        rootN = g("A", g("B", g("E"), g("F")), g("C", g("G", g("H"), g("I"), g("J"))), g("D"));
        t = new GT(rootN, -230, -280, 135, 80, 30, 30, true, n -> Colors.GRAY);
        t.build(0.3);
        mine.addAll(t.parts());
        pause(0.5);

        // Root has no parent
        TextMob l1 = label("root: no parent", rootN.x + 215, rootN.y, 32, ROOT_C, false, true);
        List<Animation> a = new ArrayList<>();
        t.paint(a, rootN, ROOT_C, d(0.7));
        a.add(new FadeIn(l1, d(0.7)));
        playAll(a);
        mine.add(l1);
        pause(1.2);

        // Leaves have no children
        List<GN> leaves = new ArrayList<>();
        List<Animation> b = new ArrayList<>();
        List<MObject> nulls = new ArrayList<>();
        for (GN n : t.nodes) {
            if (!n.leaf()) continue;
            leaves.add(n);
            t.paint(b, n, LEAF_C, d(0.7));
            TextMob e = label("∅", n.x, n.y + 56, 30, Colors.LIGHT_GRAY, false, true);
            b.add(new FadeIn(e, d(0.7)));
            nulls.add(e);
        }
        TextMob l2 = label("leaves: no children", 560, 90, 32, LEAF_C, false, true);
        b.add(new FadeIn(l2, d(0.7)));
        playAll(b);
        mine.addAll(nulls);
        mine.add(l2);
        pause(1.2);

        // everything else is internal
        List<Animation> c = new ArrayList<>();
        for (GN n : t.nodes) if (n != rootN && !n.leaf()) t.paint(c, n, INNER_C, d(0.7));
        TextMob l3 = label("internal nodes: the rest", 560, -30, 32, INNER_C, false, true);
        c.add(new FadeIn(l3, d(0.7)));
        playAll(c);
        mine.add(l3);
        pause(1.8);
        List<Animation> f = new ArrayList<>();
        for (MObject m : nulls) f.add(new FadeOut(m, d(0.6)));
        f.add(new FadeOut(l1, d(0.6)));
        f.add(new FadeOut(l2, d(0.6)));
        f.add(new FadeOut(l3, d(0.6)));
        playAll(f);
        pause(0.2);
    }

    /** A gold trail from the root down to a node, one edge at a time. */
    private void trail(GN to, Color c) {
        List<GN> path = new ArrayList<>();
        for (GN n = to; n != null; n = n.parent) path.add(0, n);
        for (int i = 1; i < path.size(); i++) {
            List<Animation> a = new ArrayList<>();
            t.paintEdge(a, path.get(i), c, d(0.45));
            t.paint(a, path.get(i), c, d(0.45));
            playAll(a);
        }
    }

    private void untrail(GN to) {
        List<GN> path = new ArrayList<>();
        for (GN n = to; n != null; n = n.parent) path.add(0, n);
        List<Animation> a = new ArrayList<>();
        for (int i = 1; i < path.size(); i++) {
            t.paintEdge(a, path.get(i), Colors.withAlpha(Colors.LIGHT_GRAY, 0.8), d(0.6));
            t.paintDefault(a, path.get(i), d(0.6));
        }
        playAll(a);
    }

    private void reach() {
        StrokeTextMob cap = stroke("Every node is reachable from the root.", 0, 380, 42, Colors.WHITE, false);
        play(new Write(cap, d(2.4)));
        GN h = at(rootN, 1, 0, 0), e = at(rootN, 0, 0);
        trail(h, Colors.GOLD);
        TextMob p1 = label("A → C → G → H", h.x + 190, h.y, 30, Colors.GOLD, false, true);
        play(new FadeIn(p1, d(0.5)));
        pause(1.0);
        untrail(h);
        play(new FadeOut(p1, d(0.4)));
        trail(e, Colors.GOLD);
        TextMob p2 = label("A → B → E", e.x - 190, e.y, 30, Colors.GOLD, false, true);
        play(new FadeIn(p2, d(0.5)));
        pause(1.0);
        untrail(e);
        play(new FadeOut(p2, d(0.4)), new FadeOut(cap, d(0.4)));
        remove(cap);

        // the whole tree via root
        StrokeTextMob cap2 = stroke("The whole tree hangs from one pointer: root.", 0, 380, 42, Colors.WHITE, false);
        play(new Write(cap2, d(2.4)));
        Ptr rp = pointer("root", rootN.x, rootN.y - rootN.hh - 6, true, Colors.GOLD);
        play(new FadeIn(rp.arrow, d(0.5)), new FadeIn(rp.lab, d(0.5)));
        mine.addAll(rp.parts());
        pause(2.0);
        play(new FadeOut(cap2, d(0.4)));
        remove(cap2);

        // every node heads a subtree of its own
        StrokeTextMob cap3 = stroke("Every node is the root of its own subtree.", 0, 380, 42, Colors.WHITE, false);
        play(new Write(cap3, d(2.4)));
        GN[] heads = {at(rootN, 1), at(rootN, 1, 0), at(rootN, 0), at(rootN, 2)};
        for (int i = 0; i < heads.length; i++) {
            GN n = heads[i];
            RectMob box = subBox(n, Colors.TEAL, 40, 42, 38);
            List<Animation> a = new ArrayList<>();
            a.add(new FadeIn(box, d(0.7)));
            Color old = n.color;
            t.paint(a, n, ROOT_C, d(0.7));
            playAll(a);
            TextMob lab = label(n.name + " heads a subtree", box.getPosition().x() + 0, box.getPosition().y() + ((RectMob) box).getHeight() / 2 + 28, 26, Colors.TEAL, false, true);
            play(new FadeIn(lab, d(0.4)));
            pause(i < 2 ? 1.5 : 1.0);
            List<Animation> b = new ArrayList<>();
            b.add(new FadeOut(box, d(0.6)));
            b.add(new FadeOut(lab, d(0.6)));
            t.paint(b, n, old, d(0.6));
            playAll(b);
            remove(box);
            remove(lab);
        }
        pause(0.4);
        mine.add(cap3);
        fadeOutAll(d(1.0), mine);
        mine.clear();
        pause(0.3);
    }

    // ── the slide's small trees ──────────────────────────────────────

    private void catalogue() {
        double lx = -820;
        String[] names = {"Empty Tree", "Tree with one node", "Tree with two nodes", "Trees with three nodes"};
        double[] ys = {-300, -150, 0, 190};
        List<MObject> lbls = new ArrayList<>();
        List<Animation> first = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            TextMob n = label(names[i], lx, ys[i], 38, Colors.WHITE, true, true);
            first.add(new FadeInAt(n, 1.2 * i, d(0.6)));
            lbls.add(n);
        }
        playAll(first);
        mine.addAll(lbls);

        // empty tree: nothing at all
        TextMob none = label("∅", 160, ys[0], 60, Colors.GREEN, false, true);
        TextMob nn = label("no nodes", 330, ys[0], 28, Colors.LIGHT_GRAY, true, false);
        play(new FadeIn(none, d(0.6)), new FadeIn(nn, d(0.6)));
        mine.add(none);
        mine.add(nn);
        pause(0.5);

        GT one = tree(g(""), 160, ys[1], 90, 60, 26, 1, false);
        one.buildAll(d(0.6));
        mine.addAll(one.parts());
        pause(0.5);

        GT two = tree(g("", g("")), 160, ys[2] - 40, 90, 60, 26, 1, false);
        two.build(0.3);
        mine.addAll(two.parts());
        pause(0.5);

        GT th1 = tree(g("", g(""), g("")), 100, ys[3] - 60, 90, 90, 26, 1, false);
        GT th2 = tree(g("", g("", g(""))), 420, ys[3] - 60, 90, 60, 26, 1, false);
        th1.build(0.3);
        th2.build(0.3);
        mine.addAll(th1.parts());
        mine.addAll(th2.parts());
        pause(0.4);
        TextMob rc = label("two shapes", 640, ys[3] + 10, 30, Colors.GOLD, true, true);
        play(new FadeIn(rc, d(0.6)));
        mine.add(rc);
        pause(2.4);
        fadeOutAll(d(1.2), mine);
        mine.clear();
        pause(0.3);
    }
}
