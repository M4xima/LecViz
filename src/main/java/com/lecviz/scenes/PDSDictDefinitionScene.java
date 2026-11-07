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
 * Standalone clip for slide 3 of the dictionary deck: the definition of a binary search tree.
 *
 *   - the slide's lines, one at a time
 *   - the two trees of the slide side by side. On the left one every node is checked: the node lights up,
 *     its left subtree turns blue (must be smaller) and its right subtree orange (must be larger); at node 3
 *     the check fails because 4 hangs to the left of 3. On the right one every node passes.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSDictDefinitionScene extends PDSDictClipBase {

    private final List<MObject> mine = new ArrayList<>();

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("Definition");
        pause(0.5);
        slideText();
        trees();
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void slideText() {
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "A BST is a binary tree.").kw("binary tree", Colors.BLUE));
        s.add(ln(0, "If it is non-empty, the value at the root is larger than any").kw("value", Colors.BLUE));
        s.add(ln(3, "value in the left-subtree, and"));
        s.add(ln(0, "the value at the root is smaller than any value in the right-"));
        s.add(ln(3, "subtree."));
        s.add(ln(0, "The left and the right subtrees are BSTs."));
        s.add(ln(0, "Assumption: All values are unique.").kw("unique", Colors.GOLD));
        List<List<MObject>> text = writeSlide(s, -300);
        pause(1.4);
        swipeAway(text);
        pause(0.4);
    }

    private void trees() {
        GN bad = bin("6", bin("2", bin("1"), bin("3", bin("4"), bin("5"))), bin("8"));
        GN good = bstOf(6, 2, 8, 1, 4, 3, 5);
        GT tl = bst(bad, -440, -260, 120, 30, 30);
        GT tr = bst(good, 440, -260, 120, 30, 30);
        tl.build(0.25);
        tr.build(0.25);
        mine.addAll(tl.parts());
        mine.addAll(tr.parts());
        pause(0.6);

        sayLine("Check every node: smaller values on its left, larger values on its right.", Colors.LIGHT_GRAY);
        pause(1.2);
        check(tl, bad);
        check(tl, at(bad, 0));
        // node 3: the check fails
        GN three = at(bad, 0, 1);
        showSides(tl, three);
        sayLine("Node 3: its left subtree must hold only smaller values.", Colors.LIGHT_GRAY);
        pause(1.0);
        GN four = at(three, 0);
        List<Animation> a = new ArrayList<>();
        tl.paint(a, four, GONE_C, d(0.4));
        tl.paintEdge(a, four, GONE_C, d(0.4));
        playAll(a);
        tl.pop(four);
        tl.pop(four);
        List<MObject> why = chip("4 > 3, but 4 hangs to the left of 3", -440, 330, 600, 58, Colors.RED, 28);
        List<Animation> w = new ArrayList<>();
        fade(w, why, d(0.6));
        playAll(w);
        mine.addAll(why);
        TextMob nb = label("Not a BST", -440, 230, 46, Colors.RED, false, true);
        play(new FadeIn(nb, d(0.7)));
        mine.add(nb);
        sayLine("A single node in the wrong place breaks the property.", Colors.RED);
        pause(1.8);

        // the good tree: every node passes
        sayLine("In the right tree every node passes its check.", Colors.LIGHT_GRAY);
        List<GN> order = new ArrayList<>(tr.nodes);
        order.sort((x, y) -> x.depth != y.depth ? Integer.compare(x.depth, y.depth) : Double.compare(x.x, y.x));
        int dep = 0;
        List<Animation> wave = new ArrayList<>();
        for (GN n : order) {
            if (n.depth != dep) {
                playAll(wave);
                wave = new ArrayList<>();
                dep = n.depth;
            }
            tr.paint(wave, n, FOUND_C, d(0.45));
            tr.paintEdge(wave, n, FOUND_C, d(0.45));
        }
        playAll(wave);
        TextMob gb = label("BST", 440, 230, 46, Colors.GREEN, false, true);
        play(new FadeIn(gb, d(0.7)));
        mine.add(gb);
        pause(2.2);
        unsay();
        fadeOutAll(d(1.2), mine);
    }

    /** Lights node n, its left subtree blue and its right subtree orange; one narration line. */
    private void check(GT t, GN n) {
        showSides(t, n);
        sayLine(val(n) + ": everything on the left is smaller, everything on the right is larger.", Colors.LIGHT_GRAY);
        pause(1.6);
        List<Animation> a = new ArrayList<>();
        for (GN m : t.nodes) plain(t, a, m, d(0.35));
        playAll(a);
        pause(0.2);
    }

    private void showSides(GT t, GN n) {
        List<Animation> a = new ArrayList<>();
        t.paint(a, n, VISIT_C, d(0.4));
        GN l = leftOf(n), r = rightOf(n);
        if (l != null) {
            List<GN> sub = new ArrayList<>();
            preorderInto(l, sub);
            for (GN m : sub) t.paint(a, m, Colors.BLUE, d(0.4));
        }
        if (r != null) {
            List<GN> sub = new ArrayList<>();
            preorderInto(r, sub);
            for (GN m : sub) t.paint(a, m, Colors.ORANGE, d(0.4));
        }
        playAll(a);
    }

    private void sayLine(String s, Color c) { sayAt(s, c, 0, 440, 34); }
}
