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
 * Standalone clip for slide 16 of the dictionary deck: the classwork sequence of insertions and removals.
 *
 *   - the five operations wait in a row of chips: insert(50), insert(40), remove(20), insert(46), remove(31)
 *   - they are carried out one by one on the slide's tree 60 (20 (10, 52 (31 (-, 44))), 87): an insertion walks down
 *     comparing and hangs a leaf; a removal with two children copies the smallest value of the right subtree
 *     into the node and cuts out the original; the tree makes room or closes the gap as it changes
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSDictClassworkScene extends PDSDictClipBase {

    private final List<MObject> mine = new ArrayList<>();
    private GT t;
    private GN root;
    private final List<List<MObject>> chips = new ArrayList<>();

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("Classwork");
        pause(0.5);
        root = bstOf(60, 20, 87, 10, 52, 31, 44);
        t = bst(root, 0, -290, 90, 26, 28);
        t.build(0.25);
        mine.addAll(t.parts());

        String[] ops = {"insert(50)", "insert(40)", "remove(20)", "insert(46)", "remove(31)"};
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < ops.length; i++) {
            List<MObject> c = chip(ops[i], (i - 2) * 230, -395, 208, 56, Colors.BLUE, 28);
            chips.add(c);
            mine.addAll(c);
            for (MObject m : c) in.add(new FadeInAt(m, 0.25 * i, d(0.6)));
        }
        playAll(in);
        say("Start from this tree and carry out the five operations in order.", Colors.LIGHT_GRAY);
        pause(2.4);

        for (int i = 0; i < ops.length; i++) {
            lightChip(i);
            int v = Integer.parseInt(ops[i].substring(ops[i].indexOf('(') + 1, ops[i].indexOf(')')));
            if (ops[i].startsWith("insert")) insert(v);
            else remove(v);
            pause(1.2);
        }
        say("Every operation kept the tree a BST.", Colors.GREEN);
        pause(3.0);
        unsay();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void lightChip(int i) {
        List<Animation> a = new ArrayList<>();
        for (int k = 0; k < chips.size(); k++) {
            if (k < i) {
                a.add(new ColorChange(chips.get(k).get(0), Colors.withAlpha(Colors.GRAY, 0.18), d(0.3)));
                a.add(new ColorChange(chips.get(k).get(0), Colors.withAlpha(Colors.GRAY, 0.6), d(0.3), ColorChange.Target.STROKE));
            } else if (k == i) {
                a.add(new ColorChange(chips.get(k).get(0), Colors.withAlpha(Colors.GOLD, 0.45), d(0.3)));
                a.add(new ColorChange(chips.get(k).get(0), Colors.GOLD, d(0.3), ColorChange.Target.STROKE));
            }
        }
        playAll(a);
    }

    private void insert(int v) {
        say("insert(" + v + "): walk down from the root, comparing.", Colors.LIGHT_GRAY);
        GN n = root, last = null;
        while (n != null) {
            lit(t, n, VISIT_C, d(0.35));
            boolean left = v < val(n);
            say(v + (left ? " < " : " > ") + n.name + ": go " + (left ? "left." : "right."), Colors.LIGHT_GRAY);
            pause(0.8);
            last = n;
            n = child(n, left ? 0 : 1);
        }
        int slot = v < val(last) ? 0 : 1;
        say("Empty place: the new node " + v + " hangs here, as a leaf.", Colors.GREEN);
        GN nn = addLeaf(t, last, slot, String.valueOf(v));
        pause(0.4);
        settle(t, nn);
    }

    private void remove(int v) {
        say("remove(" + v + "): search for it.", Colors.LIGHT_GRAY);
        List<GN> path = bstPath(root, v);
        for (int i = 0; i < path.size(); i++) {
            lit(t, path.get(i), i == path.size() - 1 ? GONE_C : VISIT_C, d(0.35));
            pause(0.5);
        }
        GN n = path.get(path.size() - 1);
        GN l = child(n, 0), r = child(n, 1);
        if (l != null && r != null) {
            say(n.name + " has two children: its right subtree's smallest value will take its place.", Colors.ORANGE);
            pause(1.4);
            GN y = r;
            lit(t, y, VISIT_C, d(0.35));
            pause(0.4);
            while (child(y, 0) != null) {
                y = child(y, 0);
                lit(t, y, VISIT_C, d(0.35));
                pause(0.4);
            }
            lit(t, y, FOUND_C, d(0.35));
            t.pop(y);
            say("The smallest value there is " + y.name + ". Copy it into the node of " + n.name + ".", Colors.LIGHT_GRAY);
            pause(1.0);
            copyInto(t, y, n);
            say("Now delete the original " + y.name + ".", Colors.GOLD);
            lit(t, y, GONE_C, d(0.4));
            pause(0.9);
            say(y.kids.isEmpty() ? y.name + " is a leaf: cut it off." : y.name + " has one child: it moves up into its place.", Colors.LIGHT_GRAY);
            pause(0.8);
            cutOut(t, y);
        } else {
            say(n.name + " has at most one child: cut it out.", Colors.LIGHT_GRAY);
            pause(0.9);
            cutOut(t, n);
        }
    }

    private void say(String s, Color c) { sayAt(s, c, 0, 440, 34); }
}
