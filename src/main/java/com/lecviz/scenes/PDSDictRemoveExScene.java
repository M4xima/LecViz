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
 * Standalone clip for slide 15 of the dictionary deck: a worked removal, Remove(2).
 *
 *   - the tree 6 (2 (1, 5 (3 (-, 4))), 8): node 2 has two children
 *   - the smallest value of its right subtree is found (5, then left to 3); a copy of 3 flies into the node of 2
 *   - the original 3 has a right child, 4: it is cut out and 4 moves up into its place; the tree glides to its new shape
 *   - the values are read in order to show the result is still a BST
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSDictRemoveExScene extends PDSDictClipBase {

    private final List<MObject> mine = new ArrayList<>();

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("Remove(value)");
        pause(0.5);
        GN root = bstOf(6, 2, 8, 1, 5, 3, 4);
        GT t = bst(root, 0, -270, 105, 30, 32);
        t.build(0.3);
        mine.addAll(t.parts());
        GN two = bstFind(root, 2), five = bstFind(root, 5), three = bstFind(root, 3);
        TextMob title = label("Remove(2)", 0, -380, 44, Colors.BLUE, false, true);
        play(new FadeIn(title, d(0.6)));
        mine.add(title);
        pause(0.6);

        lit(t, root, VISIT_C, d(0.35));
        pause(0.3);
        lit(t, two, GONE_C, d(0.4));
        sayLine("Remove(2): search for 2, then look at its children. It has two.", Colors.RED);
        pause(1.8);
        sayLine("The smallest value of its right subtree: go right to 5, then left as far as possible.", Colors.LIGHT_GRAY);
        lit(t, five, VISIT_C, d(0.4));
        pause(0.7);
        lit(t, three, FOUND_C, d(0.4));
        t.pop(three);
        pause(1.2);
        sayLine("Copy 3 into the node that held 2.", Colors.LIGHT_GRAY);
        pause(0.6);
        copyInto(t, three, two);
        sayLine("Now there are two 3s. Recursively delete the original one.", Colors.GOLD);
        pause(1.6);
        lit(t, three, GONE_C, d(0.4));
        pause(0.8);
        sayLine("It has one child, 4: make 4 the child of 5, and remove the node.", Colors.LIGHT_GRAY);
        pause(1.2);
        cutOut(t, three);
        pause(0.6);
        sayLine("Remove(2) is done: only one node went away, and the tree is a BST.", Colors.GREEN);
        pause(1.0);

        OutStrip os = new OutStrip("inorder:", -900, 330, 56, Colors.GREEN);
        os.showTitle();
        mine.addAll(os.all());
        List<GN> order = new ArrayList<>();
        inorderInto(root, order);
        for (GN n : order) {
            os.add(n.name);
            mine.addAll(os.all());
        }
        sayLine("Read in order: still sorted.", Colors.GREEN);
        pause(3.0);
        unsay();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void sayLine(String s, Color c) { sayAt(s, c, 0, 430, 34); }
}
