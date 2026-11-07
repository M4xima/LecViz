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
 * Standalone clip for slide 7 of the dictionary deck: Insert 6, 8, 2, 4, 5, 1, 3.
 *
 *   - the values wait in a row of chips at the top; the next one lights up gold
 *   - the first one becomes the root; every other value walks down from the root, comparing at each node
 *     (smaller: go left, larger: go right) until it reaches an empty place, and the new node hangs there
 *   - the tree is drawn in the places the finished tree will use, so each node simply appears where the walk ends
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSDictInsertScene extends PDSDictClipBase {

    private static final int[] ORDER = {6, 8, 2, 4, 5, 1, 3};
    private final List<MObject> mine = new ArrayList<>();
    private GT t;
    private GN root;

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("Insert");
        pause(0.5);

        root = bstOf(ORDER);
        t = bst(root, 0, -250, 125, 34, 36);
        mine.addAll(t.parts());

        List<List<MObject>> chips = new ArrayList<>();
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < ORDER.length; i++) {
            List<MObject> c = chip(String.valueOf(ORDER[i]), (i - 3) * 110, -385, 84, 56, Colors.BLUE, 30);
            chips.add(c);
            for (MObject m : c) in.add(new FadeInAt(m, 0.12 * i, d(0.5)));
            mine.addAll(c);
        }
        playAll(in);
        say("Insert 6, 8, 2, 4, 5, 1, 3 into an empty tree, one by one.", Colors.LIGHT_GRAY);
        pause(1.6);

        GN prev = null;
        for (int i = 0; i < ORDER.length; i++) {
            if (prev != null) {
                List<Animation> a = new ArrayList<>();
                plain(t, a, prev, d(0.35));
                playAll(a);
            }
            insert(i, chips.get(i));
            prev = bstFind(root, ORDER[i]);
        }
        List<Animation> a = new ArrayList<>();
        plain(t, a, prev, d(0.4));
        playAll(a);
        say("Every new value ended up as a leaf.", Colors.GREEN);
        pause(2.6);
        unsay();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void insert(int i, List<MObject> chip) {
        int v = ORDER[i];
        GN n = bstFind(root, v);
        boolean slow = i < 3;
        // the value's chip lights up
        List<Animation> a = new ArrayList<>();
        a.add(new ColorChange(chip.get(0), Colors.withAlpha(Colors.GOLD, 0.45), d(0.35)));
        a.add(new ColorChange(chip.get(0), Colors.GOLD, d(0.35), ColorChange.Target.STROKE));
        playAll(a);
        if (i == 0) {
            say("The first insertion forms the root.", Colors.LIGHT_GRAY);
            pause(0.8);
        } else {
            List<GN> path = bstPath(root, v);
            for (int k = 0; k < path.size() - 1; k++) {
                GN at = path.get(k);
                lit(t, at, VISIT_C, d(0.35));
                boolean left = v < val(at);
                say(v + (left ? " < " : " > ") + at.name + ": go " + (left ? "left." : "right."), Colors.LIGHT_GRAY);
                pause(slow ? 0.9 : 0.5);
            }
            GN parent = path.get(path.size() - 2);
            boolean left = n.slot == 0;
            say("The " + (left ? "left" : "right") + " of " + parent.name + " is empty: the new node hangs here.", Colors.GREEN);
            pause(slow ? 0.7 : 0.35);
        }
        List<Animation> s = new ArrayList<>();
        t.showNode(s, n, 0, d(0.6));
        playAll(s);
        lit(t, n, NEW_C, d(0.35));
        t.pop(n);
        List<Animation> done = new ArrayList<>();
        done.add(new ColorChange(chip.get(0), Colors.withAlpha(Colors.GRAY, 0.18), d(0.4)));
        done.add(new ColorChange(chip.get(0), Colors.withAlpha(Colors.GRAY, 0.6), d(0.4), ColorChange.Target.STROKE));
        // the nodes the walk lit go back to plain
        for (GN m : t.nodes) if (m != n) plain(t, done, m, d(0.4));
        playAll(done);
        pause(slow ? 0.7 : 0.3);
    }

    private void say(String s, Color c) { sayAt(s, c, 0, 446, 34); }
}
