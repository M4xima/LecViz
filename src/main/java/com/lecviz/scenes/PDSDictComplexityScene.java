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
 * Standalone clip for slide 17 of the dictionary deck: time complexity of search, insert and remove.
 *
 *   - the slide's lines, one at a time
 *   - search: two bars, one grown for a balanced tree of 15 nodes (about 4 steps) and one for a chain of 15 (15 steps):
 *     the cost is the height, not log n
 *   - insert: a step counter ticks along the search path of a new value, then the leaf hangs: the same cost as a search
 *   - remove: two calls (find the node, then find the smallest value of its right subtree), but the two paths join into
 *     one path going down, so the counter stops at a number no bigger than the height + 1
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSDictComplexityScene extends PDSDictClipBase {

    private final List<MObject> mine = new ArrayList<>();

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("Time Complexity");
        pause(0.5);
        slideText();
        searchBars();
        insertCost();
        removeCost();
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void slideText() {
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Search").kw("Search", Colors.RED));
        s.add(ln(1, "One may get tempted to conclude it to be O(log n)."));
        s.add(ln(1, "But it is O(tree height), which could be O(n).").kw("O(tree height)", Colors.ORANGE));
        s.add(ln(0, "Insert").kw("Insert", Colors.RED));
        s.add(ln(1, "Same as that of search."));
        s.add(ln(0, "Remove").kw("Remove", Colors.RED));
        s.add(ln(1, "There may be two remove calls."));
        s.add(ln(1, "Still the complexity does not change. It is same as that of search."));
        s.add(ln(0, "All the complexities improve if the BST is").kw("improve", Colors.GREEN));
        s.add(ln(3, "height-balanced (AVL trees, Splay Trees, red-black trees, B trees, ...).").kw("height-balanced", Colors.TEAL));
        List<List<MObject>> text = writeSlide(s, -370);
        pause(1.6);
        swipeAway(text);
        pause(0.4);
    }

    private void say(String s, Color c) { sayAt(s, c, 0, 430, 34); }

    private void clearAll() {
        fadeOutAll(d(1.0), mine);
        mine.clear();
        pause(0.3);
    }

    // ── search: the cost is the height ───────────────────────────────

    private void searchBars() {
        StrokeTextMob cap = stroke("Search costs one step per level of the tree.", 0, -390, 42, Colors.WHITE, false);
        play(new Write(cap, d(2.4)));
        mine.add(cap);
        double x0 = -300, unit = 50;
        TextMob l1 = label("balanced BST, 15 nodes", -880, -120, 34, Colors.GREEN, true, true);
        TextMob l2 = label("chain, 15 nodes", -880, 60, 34, Colors.RED, true, true);
        List<Animation> in = new ArrayList<>();
        in.add(new FadeIn(l1, d(0.6)));
        in.add(new FadeIn(l2, d(0.6)));
        playAll(in);
        mine.add(l1);
        mine.add(l2);
        pause(0.6);

        // balanced: height 3, so at most 4 nodes on a path; chain: 15
        RectMob b1 = bar(x0, -120, 4 * unit, Colors.GREEN);
        RectMob b2 = bar(x0, 60, 15 * unit, Colors.RED);
        mine.add(b1);
        mine.add(b2);
        say("The most a search can look at is one node per level.", Colors.LIGHT_GRAY);
        play(new GrowRight(b1, x0, -120, 4 * unit, 56, d(1.0)));
        TextMob c1 = label("4 steps: about log n", x0 + 4 * unit + 40, -120, 32, Colors.GREEN, true, true);
        play(new FadeIn(c1, d(0.6)));
        mine.add(c1);
        pause(1.0);
        play(new GrowRight(b2, x0, 60, 15 * unit, 56, d(2.6)));
        TextMob c2 = label("15 steps: n", x0 + 15 * unit + 30, 60, 32, Colors.RED, true, true);
        play(new FadeIn(c2, d(0.6)));
        mine.add(c2);
        pause(1.2);
        say("So the cost is O(tree height), and the height can be anything from about log n up to n.", Colors.GOLD);
        pause(3.2);
        unsay();
        clearAll();
    }

    private RectMob bar(double x0, double y, double w, Color c) {
        RectMob r = new RectMob(w, 56).setCornerRadius(10);
        r.setFillColor(Colors.withAlpha(c, 0.4));
        r.setStrokeColor(Colors.withAlpha(c, 0.95));
        r.setStrokeWidth(2.4);
        r.setPosition(x0 + w / 2, y);
        r.setOpacity(0);
        add(r);
        return r;
    }

    // ── insert: the same walk as a search ────────────────────────────

    private void insertCost() {
        StrokeTextMob cap = stroke("Insert walks the same path as a search.", 0, -390, 42, Colors.WHITE, false);
        play(new Write(cap, d(2.2)));
        mine.add(cap);
        GN root = bstOf(50, 30, 70, 20, 40, 60, 80, 35, 45, 44);
        GN nn = bstFind(root, 44);
        GT t = bst(root, -250, -280, 105, 30, 32);
        for (int dep = 0; dep <= height(root); dep++) {
            List<Animation> a = new ArrayList<>();
            for (GN n : t.nodes) if (n.depth == dep && n != nn) t.showNode(a, n, 0, d(0.5));
            playAll(a);
            pause(0.2);
        }
        mine.addAll(t.parts());
        Steps st = new Steps(560, -100, 100);
        List<Animation> sa = new ArrayList<>();
        fade(sa, st.parts(), d(0.5));
        playAll(sa);
        mine.addAll(st.parts());
        say("insert(44): search for 44 ...", Colors.LIGHT_GRAY);
        pause(0.8);
        List<GN> path = bstPath(root, 44);
        for (int i = 0; i < path.size() - 1; i++) {
            lit(t, path.get(i), VISIT_C, d(0.35));
            st.tick();
            pause(0.55);
        }
        say("... it ends at an empty place, and the new leaf hangs there.", Colors.GREEN);
        List<Animation> s = new ArrayList<>();
        t.showNode(s, nn, 0, d(0.6));
        playAll(s);
        lit(t, nn, NEW_C, d(0.4));
        st.tick();
        t.pop(nn);
        pause(1.0);
        say("The cost is the length of the path: at most the height of the tree.", Colors.GOLD);
        pause(3.0);
        unsay();
        clearAll();
    }

    // ── remove: two calls, one path ──────────────────────────────────

    private void removeCost() {
        StrokeTextMob cap = stroke("Remove: two calls, still one path down.", 0, -390, 42, Colors.WHITE, false);
        play(new Write(cap, d(2.2)));
        mine.add(cap);
        GN root = bstOf(50, 30, 70, 20, 40, 60, 80, 35, 45);
        GT t = bst(root, -250, -280, 105, 30, 32);
        t.build(0.25);
        mine.addAll(t.parts());
        Steps st = new Steps(560, -100, 100);
        List<Animation> sa = new ArrayList<>();
        fade(sa, st.parts(), d(0.5));
        playAll(sa);
        mine.addAll(st.parts());
        GN thirty = bstFind(root, 30);
        say("remove(30), call 1: search for 30.", Colors.LIGHT_GRAY);
        pause(0.6);
        lit(t, root, VISIT_C, d(0.35));
        st.tick();
        pause(0.5);
        lit(t, thirty, GONE_C, d(0.35));
        st.tick();
        pause(1.0);
        say("30 has two children, so call 2 looks for the smallest value of its right subtree.", Colors.ORANGE);
        pause(1.2);
        GN y = bstFind(root, 40);
        lit(t, y, Colors.ORANGE, d(0.35));
        st.tick();
        pause(0.5);
        y = bstFind(root, 35);
        lit(t, y, FOUND_C, d(0.35));
        st.tick();
        t.pop(y);
        pause(1.2);
        say("Together the two calls followed one path from the root downward.", Colors.GOLD);
        pause(2.0);
        say("It cannot be longer than the height: remove costs O(tree height), like search.", Colors.GREEN);
        pause(3.0);
        unsay();
        clearAll();
    }
}
