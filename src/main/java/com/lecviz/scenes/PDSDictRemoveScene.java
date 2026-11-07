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
 * Standalone clip for slide 11 of the dictionary deck: Remove(value).
 *
 *   - the slide's four rules, one line at a time
 *   - the three cases on the slide's tree, side by side: Remove(3) (a leaf: it is cut from its parent),
 *     Remove(8) (one child: the child moves up into its place, the tree glides to its new shape) and
 *     Remove(4) (two children: a question mark, "scratch your head")
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSDictRemoveScene extends PDSDictClipBase {

    private final List<MObject> mine = new ArrayList<>();

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("Remove(value)");
        pause(0.5);
        slideText();
        cases();
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void slideText() {
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Search for the node n to be removed.").kw("node n", Colors.RED));
        s.add(ln(0, "If n is a leaf, remove n from its parent.").kw("leaf", Colors.TEAL));
        s.add(ln(0, "If n has one child c, make c the child of n's parent.").kw("one", Colors.BLUE));
        s.add(ln(0, "If n has two children, scratch your head.").kw("two", Colors.ORANGE));
        List<List<MObject>> text = writeSlide(s, -250);
        pause(1.4);
        swipeAway(text);
        pause(0.4);
    }

    private void cases() {
        double[] cx = {-560, 0, 560};
        int[] target = {3, 8, 4};
        GN[] roots = new GN[3];
        GT[] ts = new GT[3];
        List<Animation> in = new ArrayList<>();
        for (int k = 0; k < 3; k++) {
            roots[k] = bstOf(4, 2, 8, 1, 3, 6, 5);
            ts[k] = bst(roots[k], cx[k], -170, 85, 20, 23);
            mine.addAll(ts[k].parts());
            TextMob ti = label("Remove(" + target[k] + ")", cx[k], -290, 40, Colors.BLUE, false, true);
            mine.add(ti);
            in.add(new FadeInAt(ti, 0.2 * k, d(0.6)));
            ts[k].showAnim(in, 0.2 * k, d(0.8));
        }
        playAll(in);
        sayLine("The same tree, three different removals.", Colors.LIGHT_GRAY);
        pause(1.6);

        // Remove(3): a leaf
        GN n = bstFind(roots[0], 3);
        search(ts[0], roots[0], 3);
        sayLine("3 is a leaf: just cut it off from its parent.", Colors.GREEN);
        pause(1.0);
        removeNode(ts[0], n);
        pause(1.2);

        // Remove(8): one child
        n = bstFind(roots[1], 8);
        search(ts[1], roots[1], 8);
        sayLine("8 has one child, 6: make 6 the child of 8's parent.", Colors.GREEN);
        pause(1.2);
        removeNode(ts[1], n);
        pause(1.4);

        // Remove(4): two children
        n = bstFind(roots[2], 4);
        search(ts[2], roots[2], 4);
        sayLine("4 has two children: whichever one moves up, the other has nowhere to go.", Colors.ORANGE);
        TextMob q = label("?", n.x + 70, n.y - 40, 70, Colors.GOLD, false, true);
        mine.add(q);
        play(new FadeIn(q, d(0.5)));
        for (int i = 0; i < 2; i++) {
            play(new ScaleTo(q, 1.35, d(0.25)));
            play(new ScaleTo(q, 1.0, d(0.25)));
        }
        pause(0.8);
        sayLine("Scratch your head: this needs a trick, coming next.", Colors.GOLD);
        pause(3.0);
        unsay();
        fadeOutAll(d(1.2), mine);
    }

    /** Walks from the root to the node, lighting the path; the node itself turns red. */
    private void search(GT t, GN root, int v) {
        List<GN> path = bstPath(root, v);
        for (int i = 0; i < path.size(); i++) {
            boolean last = i == path.size() - 1;
            lit(t, path.get(i), last ? GONE_C : VISIT_C, d(0.35));
            pause(0.25);
        }
    }

    /** The node (at most one child) is cut out; its child moves up and the tree glides to its new shape. */
    private void removeNode(GT t, GN n) {
        List<Animation> a = new ArrayList<>();
        t.hideNode(a, n, d(0.6));
        splice(n);
        playAll(a);
        List<Animation> b = new ArrayList<>();
        bstRelayout(t, b, d(0.9));
        for (GN m : t.nodes) plain(t, b, m, d(0.5));
        playAll(b);
    }

    private void sayLine(String s, Color c) { sayAt(s, c, 0, 400, 34); }
}
