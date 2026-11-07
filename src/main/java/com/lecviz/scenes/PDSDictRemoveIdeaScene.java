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
 * Standalone clip for slide 12 of the dictionary deck: turning the two-children remove into a simpler one.
 *
 *   - the slide's lines, one at a time
 *   - remove(4) on the tree 4 (2 (1), 6 (5)): 4 has two children; the four values the slide names (5, 6, 2, 1) each
 *     light up in turn with what removing them would cost (a leaf, or one child)
 *   - "which is the best, in general?": the answer is the smallest value of the right subtree, found by going
 *     right once and then left as far as possible
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSDictRemoveIdeaScene extends PDSDictClipBase {

    private final List<MObject> mine = new ArrayList<>();

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("Remove(value)");
        pause(0.5);
        slideText();
        picture();
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void slideText() {
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "We would like to convert this complicated remove"));
        s.add(ln(3, "into another simpler remove."));
        s.add(ln(1, "That is, convert this case of two children into a case of"));
        s.add(ln(2, "one child or zero children."));
        s.add(ln(1, "For instance, remove(4) can be converted to remove(5)"));
        s.add(ln(2, "or remove(6) or remove(2) or remove(1)."));
        s.add(ln(1, "Which one would be the best, in general?").kw("best", Colors.BLUE));
        s.add(ln(0, "General strategy:"));
        s.add(ln(1, "Copy the smallest value from the right subtree here.").kw("Copy", Colors.BLUE));
        s.add(ln(1, "Recursively delete that smallest value."));
        List<List<MObject>> text = writeSlide(s, -380);
        pause(1.4);
        swipeAway(text);
        pause(0.4);
    }

    private void picture() {
        GN root = bstOf(4, 2, 6, 1, 5);
        GT t = bst(root, 0, -240, 150, 42, 44);
        t.build(0.3);
        mine.addAll(t.parts());
        GN four = root, two = bstFind(root, 2), one = bstFind(root, 1), six = bstFind(root, 6), five = bstFind(root, 5);
        pause(0.5);

        lit(t, four, GONE_C, d(0.4));
        sayLine("remove(4): the node has two children.", Colors.RED);
        pause(1.6);
        sayLine("Instead of removing 4 itself, copy another value into it and remove that one.", Colors.LIGHT_GRAY);
        pause(1.8);

        candidate(t, five, "5 is a leaf: removing it is easy.");
        candidate(t, six, "6 has one child: also easy.");
        candidate(t, two, "2 has one child: also easy.");
        candidate(t, one, "1 is a leaf: easy too.");

        sayLine("Which one is the best, in general?", Colors.GOLD);
        pause(2.4);
        sayLine("The one that keeps the order: the smallest value in the right subtree.", Colors.GREEN);
        pause(1.2);
        // the walk: right once, then left as far as possible
        List<Animation> reset = new ArrayList<>();
        plain(t, reset, two, d(0.3));
        plain(t, reset, one, d(0.3));
        plain(t, reset, five, d(0.3));
        plain(t, reset, six, d(0.3));
        playAll(reset);
        pause(0.3);
        sayLine("Go right once ...", Colors.LIGHT_GRAY);
        lit(t, six, VISIT_C, d(0.4));
        pause(1.0);
        sayLine("... then left as far as possible.", Colors.LIGHT_GRAY);
        lit(t, five, FOUND_C, d(0.4));
        t.pop(five);
        pause(1.0);
        sayLine("5 is the smallest value of the right subtree. 5 replaces 4, then the old 5 is deleted.", Colors.GREEN);
        pause(3.2);
        unsay();
        fadeOutAll(d(1.2), mine);
    }

    /** One of the values the slide names: it lights up with a note on how easy its own removal is. */
    private void candidate(GT t, GN n, String note) {
        lit(t, n, NEW_C, d(0.4));
        t.pop(n);
        sayLine(note, Colors.LIGHT_GRAY);
        pause(1.4);
        List<Animation> a = new ArrayList<>();
        plain(t, a, n, d(0.35));
        playAll(a);
    }

    private void sayLine(String s, Color c) { sayAt(s, c, 0, 400, 34); }
}
