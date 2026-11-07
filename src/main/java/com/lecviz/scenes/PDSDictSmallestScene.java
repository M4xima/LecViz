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
 * Standalone clip for slide 13 of the dictionary deck: guarantees on the smallest value of the right subtree.
 *
 *   - the slide's lines, one at a time
 *   - a tree where x = 10 is to be deleted: its right subtree is boxed, and y = 12, the smallest value there,
 *     is found by going right once and then left as far as possible
 *   - nothing lies between x and y: the values in sorted order, x and y side by side
 *   - y has no left child: a made-up left child 11 would be smaller than y and larger than x, which cannot be
 *   - y may have a right child (13), so taking y out is at most a one-child removal: 13 simply moves up
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSDictSmallestScene extends PDSDictClipBase {

    private final List<MObject> mine = new ArrayList<>();

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("Guarantees on the smallest value");
        pause(0.5);
        slideText();
        picture();
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void slideText() {
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "If x is the value to be deleted, and y is the"));
        s.add(ln(3, "smallest value in its right subtree,").kw("smallest", Colors.BLUE));
        s.add(ln(1, "There are no values in the BST between x and y."));
        s.add(ln(1, "The node with y value cannot have two children."));
        s.add(ln(1, "In fact, y cannot have a left child."));
        s.add(ln(1, "When y replaces x, after removing original y"));
        s.add(ln(2, "node, the BST structure is not affected."));
        s.add(ln(1, "Removal of a node with two children does not"));
        s.add(ln(2, "result in further removal of another node with"));
        s.add(ln(2, "two children."));
        List<List<MObject>> text = writeSlide(s, -350);
        pause(1.4);
        swipeAway(text);
        pause(0.4);
    }

    private void picture() {
        GN root = bstOf(10, 4, 20, 2, 6, 14, 25, 12, 17, 13);
        GT t = bst(root, 0, -290, 100, 24, 28);
        t.build(0.25);
        mine.addAll(t.parts());
        GN x = root, y = bstFind(root, 12), n20 = bstFind(root, 20), n14 = bstFind(root, 14), n13 = bstFind(root, 13);
        pause(0.5);

        lit(t, x, GONE_C, d(0.4));
        TextMob xl = t.tag(x, "x", Colors.RED, 34, -44, -30);
        play(new FadeIn(xl, d(0.4)));
        mine.add(xl);
        sayLine("Delete x = 10. It has two children, so we look at its right subtree.", Colors.LIGHT_GRAY);
        RectMob box = subBox(n20, Colors.ORANGE, 40, 40, 40);
        mine.add(box);
        play(new FadeIn(box, d(0.7)));
        pause(1.8);
        sayLine("y is its smallest value: go right once, then left as far as you can.", Colors.LIGHT_GRAY);
        lit(t, n20, VISIT_C, d(0.4));
        pause(0.6);
        lit(t, n14, VISIT_C, d(0.4));
        pause(0.6);
        lit(t, y, FOUND_C, d(0.4));
        TextMob yl = t.tag(y, "y", Colors.GREEN, 34, -44, -30);
        play(new FadeIn(yl, d(0.4)));
        mine.add(yl);
        t.pop(y);
        pause(1.6);

        // nothing between x and y
        sayLine("Nothing lies between x and y.", Colors.GOLD);
        int[] sorted = {2, 4, 6, 10, 12, 13, 14, 17, 20, 25};
        List<List<MObject>> chips = new ArrayList<>();
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < sorted.length; i++) {
            Color c = sorted[i] == 10 ? Colors.RED : sorted[i] == 12 ? Colors.GREEN : Colors.BLUE;
            List<MObject> ch = chip(String.valueOf(sorted[i]), (i - 4.5) * 84, 270, 68, 56, c, 28);
            chips.add(ch);
            mine.addAll(ch);
            for (MObject m : ch) in.add(new FadeInAt(m, 0.07 * i, d(0.4)));
        }
        playAll(in);
        pause(0.6);
        List<MObject> bracket = new ArrayList<>();
        double bx1 = (3 - 4.5) * 84, bx2 = (4 - 4.5) * 84;
        TextMob adj = label("next to each other", (bx1 + bx2) / 2, 335, 28, Colors.GOLD, false, true);
        mine.add(adj);
        List<Animation> pulse = new ArrayList<>();
        pulse.add(new FadeIn(adj, d(0.5)));
        playAll(pulse);
        for (int i = 0; i < 2; i++) {
            play(new ScaleTo(chips.get(3).get(0), 1.2, d(0.25)), new ScaleTo(chips.get(4).get(0), 1.2, d(0.25)),
                    new ScaleTo(chips.get(3).get(1), 1.2, d(0.25)), new ScaleTo(chips.get(4).get(1), 1.2, d(0.25)));
            play(new ScaleTo(chips.get(3).get(0), 1.0, d(0.25)), new ScaleTo(chips.get(4).get(0), 1.0, d(0.25)),
                    new ScaleTo(chips.get(3).get(1), 1.0, d(0.25)), new ScaleTo(chips.get(4).get(1), 1.0, d(0.25)));
        }
        pause(1.2);

        // y has no left child
        sayLine("If y had a left child, say 11, it would be smaller than y but still larger than x.", Colors.LIGHT_GRAY);
        CircleMob ghost = new CircleMob(24);
        ghost.setFillColor(Colors.withAlpha(Colors.RED, 0.25));
        ghost.setStrokeColor(Colors.RED);
        ghost.setStrokeWidth(3);
        ghost.setPosition(y.x - 62, y.y + 100);
        ghost.setOpacity(0);
        add(ghost);
        TextMob gt = label("11", y.x - 62, y.y + 100, 28, Colors.WHITE, false, true);
        mine.add(ghost);
        mine.add(gt);
        List<Animation> g = new ArrayList<>();
        g.add(new FadeIn(ghost, d(0.6)));
        g.add(new FadeIn(gt, d(0.6)));
        playAll(g);
        pause(1.6);
        sayLine("Then 11 would sit between x and y, but nothing does. So y has no left child.", Colors.RED);
        pause(2.4);
        List<Animation> gone = new ArrayList<>();
        gone.add(new FadeOut(ghost, d(0.6)));
        gone.add(new FadeOut(gt, d(0.6)));
        playAll(gone);

        // y may have a right child: it simply moves up
        sayLine("y may still have a right child, 13. Taking y out only lifts 13 into its place.", Colors.LIGHT_GRAY);
        lit(t, n13, NEW_C, d(0.4));
        t.pop(n13);
        pause(2.0);
        sayLine("So the old y is a leaf or has one child: no second two-children removal follows.", Colors.GREEN);
        pause(3.2);
        unsay();
        fadeOutAll(d(1.2), mine);
    }

    private void sayLine(String s, Color c) { sayAt(s, c, 0, 400, 32); }
}
