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
 * Standalone clip for slide 23 of the dictionary deck: "Some Questions?"
 *
 *   - the slide's three questions, one at a time
 *   - then each one is answered with a small picture (the slide gives no answers, so these are added material):
 *       duplicates: a second 4 arrives; either the node keeps a count, or equal keys go to one side (the right)
 *       strings and other types: a BST of fruit names; "lemon" walks down by alphabetical order
 *       more pointers: a parent pointer for every node, and a subtree size on every node
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSDictQuestionsScene extends PDSDictClipBase {

    private final List<MObject> mine = new ArrayList<>();

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("Some Questions?");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "What if a BST has duplicates?").kw("duplicates", Colors.ORANGE));
        s.add(ln(0, "Can a BST node contain strings? Other types?").kw("strings", Colors.BLUE));
        s.add(ln(0, "Can I store more pointers in a node?").kw("more pointers", Colors.TEAL));
        List<List<MObject>> text = writeSlide(s, -250);
        pause(1.6);
        swipeAway(text);
        pause(0.4);

        duplicates();
        strings();
        pointers();
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void section(int num, String title) {
        mine.addAll(solutionHeader(num, title, -400));
    }

    private void clearAll() {
        unsay();
        fadeOutAll(d(1.0), mine);
        mine.clear();
        pause(0.3);
    }

    private void say(String s, Color c) { sayAt(s, c, 0, 440, 34); }

    // ── 1: duplicates ────────────────────────────────────────────────

    private void duplicates() {
        section(1, "Duplicates");
        GN root = bstOf(6, 2, 8, 1, 4, 4);
        GN four = bstFind(root, 4);
        GN dup = child(four, 1);
        GT t = bst(root, -300, -270, 110, 34, 36);
        for (int dep = 0; dep <= height(root); dep++) {
            List<Animation> a = new ArrayList<>();
            for (GN n : t.nodes) if (n.depth == dep && n != dup) t.showNode(a, n, 0, d(0.5));
            playAll(a);
            pause(0.2);
        }
        mine.addAll(t.parts());
        say("insert(4) again: the search walks down and finds a 4 already there.", Colors.LIGHT_GRAY);
        for (GN n : bstPath(root, 4)) {
            if (n == dup) break;
            lit(t, n, VISIT_C, d(0.35));
            pause(0.45);
        }
        lit(t, four, FOUND_C, d(0.35));
        t.pop(four);
        pause(1.2);
        say("A BST of unique values has no place for it. There are two common answers.", Colors.GOLD);
        pause(1.6);

        // answer A: a count
        TextMob cnt = t.tag(four, "x2", Colors.GOLD, 34, 48, -34);
        mine.add(cnt);
        play(new FadeIn(cnt, d(0.5)));
        t.pop(four);
        List<MObject> ca = chip("A: keep a count in the node", 430, -110, 700, 62, Colors.GOLD, 30);
        List<Animation> in = new ArrayList<>();
        fade(in, ca, d(0.7));
        playAll(in);
        mine.addAll(ca);
        say("A: the node stores how many times its value was inserted. The shape of the tree does not change.", Colors.LIGHT_GRAY);
        pause(3.0);

        // answer B: equal keys on one side
        List<Animation> sw = new ArrayList<>();
        sw.add(new FadeOut(cnt, d(0.4)));
        playAll(sw);
        List<Animation> a2 = new ArrayList<>();
        t.showNode(a2, dup, 0, d(0.6));
        playAll(a2);
        lit(t, dup, NEW_C, d(0.35));
        t.pop(dup);
        List<MObject> cb = chip("B: equal values go to the right", 430, 20, 700, 62, Colors.BLUE, 30);
        List<Animation> in2 = new ArrayList<>();
        fade(in2, cb, d(0.7));
        playAll(in2);
        mine.addAll(cb);
        say("B: change the rule to: left < node <= right. Then a second 4 simply hangs to the right of the first.", Colors.LIGHT_GRAY);
        pause(3.2);
        say("Either way, pick one rule and use it in search, insert and remove alike.", Colors.GREEN);
        pause(2.6);
        clearAll();
    }

    // ── 2: strings and other types ───────────────────────────────────

    private void strings() {
        section(2, "Strings and other types");
        GN lemon = bin("lemon");
        GN root = bin("kiwi",
                bin("fig", bin("apple"), bin("grape")),
                bin("pear", bin("mango", lemon, null), bin("plum")));
        GT t = new GT(root, 0, -270, 115, 40, 30, 28, false, n -> BST_C, false);
        for (int dep = 0; dep <= height(root); dep++) {
            List<Animation> a = new ArrayList<>();
            for (GN n : t.nodes) if (n.depth == dep && n != lemon) t.showNode(a, n, 0, d(0.5));
            playAll(a);
            pause(0.2);
        }
        mine.addAll(t.parts());
        say("Yes: any type works, as long as two values can be compared. Words compare alphabetically.", Colors.LIGHT_GRAY);
        pause(2.4);
        say("Insert \"lemon\": walk down, comparing words.", Colors.LIGHT_GRAY);
        pause(0.8);
        String[][] steps = {{"kiwi", "lemon > kiwi: go right."}, {"pear", "lemon < pear: go left."}, {"mango", "lemon < mango: go left."}};
        for (String[] st : steps) {
            GN n = null;
            for (GN m : t.nodes) if (m.name.equals(st[0])) n = m;
            lit(t, n, VISIT_C, d(0.35));
            say(st[1], Colors.LIGHT_GRAY);
            pause(1.1);
        }
        say("The left of mango is empty: lemon hangs there.", Colors.GREEN);
        List<Animation> a = new ArrayList<>();
        t.showNode(a, lemon, 0, d(0.6));
        playAll(a);
        lit(t, lemon, NEW_C, d(0.35));
        t.pop(lemon);
        pause(1.6);
        say("Numbers, words, records ordered by a key: the tree only needs a comparison.", Colors.GREEN);
        pause(2.8);
        clearAll();
    }

    // ── 3: more pointers ─────────────────────────────────────────────

    private void pointers() {
        section(3, "More pointers");
        GN root = bstOf(6, 2, 8, 1, 4);
        GT t = bst(root, 0, -270, 120, 36, 38);
        t.build(0.3);
        mine.addAll(t.parts());
        say("Yes: a node may carry any extra fields. Two useful ones:", Colors.LIGHT_GRAY);
        pause(1.6);

        // parent pointers: arrows from each child back up to its parent
        say("A parent pointer: from any node you can walk back up.", Colors.PINK);
        List<Link> ups = new ArrayList<>();
        List<Animation> in = new ArrayList<>();
        for (GN n : t.nodes) {
            if (n.parent == null) continue;
            double dx = n.parent.x - n.x, dy = n.parent.y - n.y, len = Math.hypot(dx, dy);
            double ux = dx / len, uy = dy / len;
            double px = -uy * 16, py = ux * 16;
            Link l = arrow(n.x + ux * 38 + px, n.y + uy * 38 + py, n.parent.x - ux * 42 + px, n.parent.y - uy * 42 + py,
                    Colors.PINK, 3.2);
            ups.add(l);
            mine.add(l);
            in.add(new DrawLinkAt(l, 0, d(0.8)));
        }
        playAll(in);
        pause(2.6);
        List<Animation> out = new ArrayList<>();
        for (Link l : ups) out.add(new FadeOut(l, d(0.6)));
        playAll(out);

        // subtree sizes
        say("A size field: how many nodes are in each subtree, to find the k-th smallest value.", Colors.GOLD);
        int[] sizes = {5, 3, 1, 1, 1};
        List<GN> pre = new ArrayList<>();
        preorderInto(root, pre);
        List<Animation> sa = new ArrayList<>();
        for (GN n : pre) {
            int sz = 0;
            List<GN> sub = new ArrayList<>();
            preorderInto(n, sub);
            sz = sub.size();
            TextMob tag = t.tag(n, String.valueOf(sz), Colors.GOLD, 36, 52, -36);
            mine.add(tag);
            sa.add(new FadeIn(tag, d(0.7)));
        }
        playAll(sa);
        pause(3.4);
        say("Extra fields cost memory and must be kept up to date by insert and remove.", Colors.LIGHT_GRAY);
        pause(2.8);
        clearAll();
    }
}
