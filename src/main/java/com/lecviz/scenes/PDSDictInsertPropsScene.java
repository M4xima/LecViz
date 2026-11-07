package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Easing;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Standalone clip for slide 9 of the dictionary deck: what insertion guarantees.
 *
 *   - the slide's lines, one at a time, then one picture for each claim:
 *   - different insertion orders may form the same tree: two orders grow two trees that end up identical
 *   - inorder traversal gives the same order whatever the structure: a balanced tree and a chain both write 1 2 3 4 5 6 8
 *   - insertions happen at leaves: a new node tries to sit on an edge (inside the tree), is refused, and is hung
 *     below the last node of its search path
 *   - the order decides the height: searching for 7 takes 3 steps in a height-2 tree and 7 in a chain
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSDictInsertPropsScene extends PDSDictClipBase {

    private final List<MObject> mine = new ArrayList<>();

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("Insert");
        pause(0.5);
        slideText();
        sameTree();
        sameInorder();
        atLeaves();
        heights();
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void slideText() {
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Insertion order may change the tree structure."));
        s.add(ln(1, "Different insertion orders may form the same tree."));
        s.add(ln(0, "Inorder traversal prints the values in exactly the"));
        s.add(ln(3, "same order, irrespective of the BST structure.").kw("same order", Colors.BLUE));
        s.add(ln(1, "This is also the sorted order."));
        s.add(ln(0, "The first insertion forms the root."));
        s.add(ln(0, "Insertions always happen at leaves."));
        s.add(ln(1, "New node cannot be added as an intermediate node."));
        s.add(ln(0, "Insertion order decides the tree height."));
        s.add(ln(1, "Tree height affects efficiency/complexity of operations."));
        List<List<MObject>> text = writeSlide(s, -360);
        pause(1.4);
        swipeAway(text);
        pause(0.4);
    }

    private StrokeTextMob caption(String s) {
        StrokeTextMob c = stroke(s, 0, -395, 40, Colors.WHITE, false);
        play(new Write(c, d(Math.max(1.6, s.length() * 0.055))));
        mine.add(c);
        return c;
    }

    private void clearAll() {
        fadeOutAll(d(1.0), mine);
        mine.clear();
        pause(0.3);
    }

    private void say(String s, Color c) { sayAt(s, c, 0, 446, 34); }

    // ── same tree from two orders ────────────────────────────────────

    private void sameTree() {
        caption("Different insertion orders may form the same tree.");
        int[] oa = {4, 2, 8, 1, 3, 6, 5};
        int[] ob = {4, 8, 2, 6, 1, 3, 5};
        GN ra = bstOf(oa), rb = bstOf(ob);
        GT ta = bst(ra, -440, -270, 90, 26, 28);
        GT tb = bst(rb, 440, -270, 90, 26, 28);
        mine.addAll(ta.parts());
        mine.addAll(tb.parts());
        TextMob la = mono("4, 2, 8, 1, 3, 6, 5", -440, 130, 32, Colors.LIGHT_GRAY);
        TextMob lb = mono("4, 8, 2, 6, 1, 3, 5", 440, 130, 32, Colors.LIGHT_GRAY);
        List<Animation> in = new ArrayList<>();
        in.add(new FadeIn(la, d(0.6)));
        in.add(new FadeIn(lb, d(0.6)));
        playAll(in);
        mine.add(la);
        mine.add(lb);
        pause(0.5);
        for (int i = 0; i < 7; i++) {
            List<Animation> a = new ArrayList<>();
            ta.showNode(a, bstFind(ra, oa[i]), 0, d(0.5));
            tb.showNode(a, bstFind(rb, ob[i]), 0, d(0.5));
            playAll(a);
            pause(i < 2 ? 0.55 : 0.3);
        }
        TextMob eq = label("=", 0, -110, 110, Colors.GOLD, false, true);
        mine.add(eq);
        List<Animation> e = new ArrayList<>();
        e.add(new FadeIn(eq, d(0.5)));
        for (GN n : ta.nodes) ta.paint(e, n, FOUND_C, d(0.6));
        for (GN n : tb.nodes) tb.paint(e, n, FOUND_C, d(0.6));
        playAll(e);
        say("Two different orders, one and the same tree.", Colors.GREEN);
        pause(2.6);
        unsay();
        clearAll();
    }

    // ── inorder is always sorted ─────────────────────────────────────

    private void sameInorder() {
        caption("Inorder traversal prints the same order for any structure.");
        GN ra = bstOf(6, 2, 8, 1, 4, 3, 5);
        GN rb = bstOf(1, 2, 3, 4, 5, 6, 8);
        GT ta = bst(ra, -440, -300, 64, 24, 26);
        GT tb = bst(rb, 440, -300, 64, 24, 26);
        ta.build(0.2);
        tb.build(0.15);
        mine.addAll(ta.parts());
        mine.addAll(tb.parts());
        OutStrip sa = new OutStrip("inorder:", -900, 300, 56, Colors.GREEN);
        OutStrip sb = new OutStrip("inorder:", 40, 300, 56, Colors.GREEN);
        sa.showTitle();
        sb.showTitle();
        mine.addAll(sa.all());
        mine.addAll(sb.all());
        pause(0.6);
        List<GN> oa = new ArrayList<>(), ob = new ArrayList<>();
        inorderInto(ra, oa);
        inorderInto(rb, ob);
        for (int i = 0; i < 7; i++) {
            List<Animation> a = new ArrayList<>();
            ta.paint(a, oa.get(i), VISIT_C, d(0.3));
            tb.paint(a, ob.get(i), VISIT_C, d(0.3));
            playAll(a);
            sa.add(oa.get(i).name);
            sb.add(ob.get(i).name);
            mine.addAll(sa.all());
            mine.addAll(sb.all());
            List<Animation> b = new ArrayList<>();
            ta.paint(b, oa.get(i), DONE_C, d(0.3));
            tb.paint(b, ob.get(i), DONE_C, d(0.3));
            playAll(b);
        }
        say("A BST of any shape gives 1 2 3 4 5 6 8: the sorted order.", Colors.GREEN);
        pause(2.8);
        unsay();
        clearAll();
    }

    // ── insertions happen at leaves ──────────────────────────────────

    private void atLeaves() {
        caption("Insertions always happen at leaves.");
        GN root = bstOf(6, 2, 8, 1, 4, 5);
        GN five = bstFind(root, 5);
        GT t = bst(root, 0, -290, 100, 30, 32);
        // show everything except the new value 5
        for (int dep = 0; dep <= height(root); dep++) {
            List<Animation> a = new ArrayList<>();
            for (GN n : t.nodes) if (n.depth == dep && n != five) t.showNode(a, n, 0, d(0.5));
            playAll(a);
            pause(0.2);
        }
        mine.addAll(t.parts());
        say("The first insertion formed the root; the rest hang below it.", Colors.LIGHT_GRAY);
        pause(1.6);
        say("Now insert 5. Could it sit between 2 and 4, inside the tree?", Colors.LIGHT_GRAY);
        GN two = bstFind(root, 2), four = bstFind(root, 4);
        double fx = five.x, fy = five.y;
        five.shape.setPosition(fx + 260, -330);
        five.text.setPosition(fx + 260, -330);
        List<Animation> in = new ArrayList<>();
        in.add(new FadeIn(five.shape, d(0.6)));
        in.add(new FadeIn(five.text, d(0.6)));
        playAll(in);
        t.paint(new ArrayList<>(), five, NEW_C, 0.01);
        lit(t, five, NEW_C, d(0.3));
        pause(0.6);
        double mx = (two.x + four.x) / 2 + 10, my = (two.y + four.y) / 2;
        List<Animation> go = new ArrayList<>();
        go.add(new MoveTo(five.shape, mx, my, d(0.9)).setEasing(Easing.EASE_IN_OUT));
        go.add(new MoveTo(five.text, mx, my, d(0.9)).setEasing(Easing.EASE_IN_OUT));
        playAll(go);
        List<Animation> bad = new ArrayList<>();
        t.paint(bad, five, GONE_C, d(0.3));
        playAll(bad);
        List<MObject> no = chip("No: a new node cannot be an intermediate node", 0, 330, 760, 60, Colors.RED, 30);
        List<Animation> nn = new ArrayList<>();
        fade(nn, no, d(0.6));
        playAll(nn);
        mine.addAll(no);
        pause(1.8);
        say("The search for 5 ends below 4: 5 > 6? no, 5 > 2, 5 > 4, and 4 has no right child.", Colors.LIGHT_GRAY);
        List<Animation> back = new ArrayList<>();
        back.add(new MoveTo(five.shape, fx, fy, d(1.0)).setEasing(Easing.EASE_IN_OUT));
        back.add(new MoveTo(five.text, fx, fy, d(1.0)).setEasing(Easing.EASE_IN_OUT));
        t.paint(back, five, NEW_C, d(0.5));
        for (MObject m : no) back.add(new FadeOut(m, d(0.6)));
        playAll(back);
        List<Animation> edge = new ArrayList<>();
        edge.add(new DrawLinkAt(five.edge, 0, d(0.6)));
        playAll(edge);
        t.pop(five);
        say("It becomes a leaf, the child of the last node on the search path.", Colors.GREEN);
        pause(2.6);
        unsay();
        clearAll();
    }

    // ── order decides the height ─────────────────────────────────────

    private void heights() {
        caption("Insertion order decides the tree height.");
        GN rp = bstOf(4, 2, 6, 1, 3, 5, 7);
        GN rc = bstOf(1, 2, 3, 4, 5, 6, 7);
        GT tp = bst(rp, -440, -280, 80, 26, 28);
        GT tc = bst(rc, 440, -280, 62, 26, 28);
        tp.build(0.2);
        tc.build(0.15);
        mine.addAll(tp.parts());
        mine.addAll(tc.parts());
        TextMob hp = label("height 2", -440, 130, 36, Colors.GREEN, false, true);
        TextMob hc = label("height 6", 440, 130, 36, Colors.RED, false, true);
        List<Animation> h = new ArrayList<>();
        h.add(new FadeIn(hp, d(0.6)));
        h.add(new FadeInAt(hc, 0.25, d(0.6)));
        playAll(h);
        mine.add(hp);
        mine.add(hc);
        pause(0.8);
        say("Search for 7 in both trees and count the nodes looked at.", Colors.LIGHT_GRAY);
        Steps sp = new Steps(-440, 230, 70);
        Steps sc = new Steps(440, 230, 70);
        List<Animation> sh = new ArrayList<>();
        fade(sh, sp.parts(), d(0.5));
        fade(sh, sc.parts(), d(0.5));
        playAll(sh);
        mine.addAll(sp.parts());
        mine.addAll(sc.parts());
        pause(0.5);
        List<GN> pa = bstPath(rp, 7), pc = bstPath(rc, 7);
        for (int i = 0; i < pc.size(); i++) {
            List<Animation> a = new ArrayList<>();
            if (i < pa.size()) {
                tp.paint(a, pa.get(i), i == pa.size() - 1 ? FOUND_C : VISIT_C, d(0.3));
                sp.tick();
            }
            tc.paint(a, pc.get(i), i == pc.size() - 1 ? FOUND_C : VISIT_C, d(0.3));
            sc.tick();
            playAll(a);
            pause(0.35);
        }
        say("Same seven values: 3 steps in one tree, 7 steps in the other.", Colors.GOLD);
        pause(3.0);
        unsay();
        clearAll();
    }
}
