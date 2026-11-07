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
 * Standalone clip for slide 14 of the dictionary deck: the general strategy for Remove(value), run on Remove(4).
 *
 *   - the strategy's two lines are written on the left; a highlight band sits on the line being carried out
 *   - the tree 4 (2 (1), 6 (5)): the smallest value of the right subtree (5) is found, a copy of it flies into the
 *     node holding 4, and then the original 5, now a leaf, is cut off; the tree glides to its new shape
 *   - at the end the values are read in order to show the result is still a BST
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSDictStrategyScene extends PDSDictClipBase {

    private final List<MObject> mine = new ArrayList<>();

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("Remove(value)");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "General strategy:"));
        s.add(ln(1, "Copy the smallest value in the right subtree here.").kw("Copy", Colors.BLUE));
        s.add(ln(1, "Recursively delete that smallest value.").kw("Recursively", Colors.TEAL));
        List<List<MObject>> text = writeSlide(s, -230);
        for (List<MObject> g : text) mine.addAll(g);
        pause(0.8);

        GN root = bstOf(4, 2, 6, 1, 5);
        GT t = bst(root, 560, -250, 130, 38, 40);
        t.build(0.3);
        mine.addAll(t.parts());
        TextMob title = label("Remove(4)", 560, -340, 44, Colors.BLUE, false, true);
        play(new FadeIn(title, d(0.6)));
        mine.add(title);
        pause(0.5);

        GN five = bstFind(root, 5), six = bstFind(root, 6);
        lit(t, root, GONE_C, d(0.4));
        sayLine("Remove(4): the node has two children.", Colors.RED);
        pause(1.4);

        RectMob band = panel(-370, -170, 840, 54, Colors.GOLD, 0.2);
        mine.add(band);
        play(new FadeIn(band, d(0.5)));
        sayLine("Step 1: find the smallest value in the right subtree: right once, then left.", Colors.LIGHT_GRAY);
        lit(t, six, VISIT_C, d(0.4));
        pause(0.6);
        lit(t, five, FOUND_C, d(0.4));
        t.pop(five);
        pause(1.0);
        sayLine("Copy that 5 into the node that held 4.", Colors.LIGHT_GRAY);
        pause(0.6);
        copyInto(t, five, root);
        sayLine("Now 5 is in the tree twice, but the tree is still ordered.", Colors.GOLD);
        pause(1.8);

        play(new MoveTo(band, -370, -110, d(0.6)).setEasing(Easing.EASE_IN_OUT));
        sayLine("Step 2: recursively delete the original 5 from the right subtree.", Colors.LIGHT_GRAY);
        lit(t, five, GONE_C, d(0.4));
        pause(1.2);
        sayLine("It is a leaf, so it is simply cut off.", Colors.LIGHT_GRAY);
        pause(0.8);
        cutOut(t, five);
        pause(0.6);
        sayLine("4 is gone, and the tree is a BST again.", Colors.GREEN);
        pause(1.0);

        OutStrip os = new OutStrip("inorder:", -900, 320, 56, Colors.GREEN);
        os.showTitle();
        mine.addAll(os.all());
        List<GN> order = new ArrayList<>();
        inorderInto(root, order);
        for (GN n : order) {
            os.add(n.name);
            mine.addAll(os.all());
        }
        sayLine("Read in order, the values are still sorted.", Colors.GREEN);
        pause(3.0);
        unsay();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void sayLine(String s, Color c) { sayAt(s, c, 0, 420, 34); }
}
