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
 * Clips for slide 24 of the dictionary deck: the five exercises, then a solution for each (added material).
 * Part 1 (dict_24a): the slide's questions one at a time, then
 *     1 is it a BST?  the in-order values of a tree that is not a BST break the sorted order, those of a BST do not
 *     2 minimum height: insert the middle value first, then the middle of each half: binary search, drawn as a tree
 *     3 count the leaves: the recursive code with a counter that ticks at every leaf
 * Part 2 (dict_24b):
 *     4 level order: a queue; the front node is written, its children join the back
 *     5 reverse-sorted order: in-order with the sides swapped, with the code beside the tree
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSDictExercisesScene extends PDSDictClipBase {

    private final int part;
    private final List<MObject> mine = new ArrayList<>();

    public PDSDictExercisesScene(int part) { this.part = part; }

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("Exercises");
        pause(0.5);
        if (part == 1) {
            List<Spec> s = new ArrayList<>();
            s.add(ln(0, "Given a binary tree, find out if it is BST.").kw("BST", Colors.BLUE));
            s.add(ln(0, "Given an insertion sequence, how would you permute it to achieve the"));
            s.add(ln(3, "minimum height of the resultant BST?").kw("minimum height", Colors.GREEN));
            s.add(ln(1, "See if your answer has a resemblance with binary search in a sorted array."));
            s.add(ln(0, "Count the number of leaves in a BST.").kw("leaves", Colors.TEAL));
            s.add(ln(0, "Print a BST in a level-order (breadth-first) manner.").kw("level-order", Colors.ORANGE));
            s.add(ln(0, "Write a program to print values in a BST in reverse-sorted order.").kw("reverse-sorted", Colors.PINK));
            List<List<MObject>> text = writeSlide(s, -330);
            pause(2.0);
            swipeAway(text);
            pause(0.4);
            isBst();
            minHeight();
            countLeaves();
        } else {
            levelOrder();
            reverseOrder();
        }
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void section(int num, String title) { mine.addAll(solutionHeader(num, title, -400)); }

    private void clearAll() {
        unsay();
        fadeOutAll(d(1.0), mine);
        mine.clear();
        pause(0.3);
    }

    private void say(String s, Color c) { sayAt(s, c, 0, 440, 34); }

    // ── 1: is it a BST? ──────────────────────────────────────────────

    private void isBst() {
        section(1, "Is it a BST?");
        GN bad = bin("6", bin("2", bin("1"), bin("3", bin("4"), bin("5"))), bin("8"));
        GN good = bstOf(6, 2, 8, 1, 4, 3, 5);
        GT tl = bst(bad, -440, -300, 95, 26, 28);
        GT tr = bst(good, 440, -300, 95, 26, 28);
        tl.build(0.2);
        tr.build(0.2);
        mine.addAll(tl.parts());
        mine.addAll(tr.parts());
        OutStrip sl = new OutStrip("inorder:", -900, 190, 56, Colors.GREEN);
        OutStrip sr = new OutStrip("inorder:", 40, 190, 56, Colors.GREEN);
        sl.showTitle();
        sr.showTitle();
        mine.addAll(sl.all());
        mine.addAll(sr.all());
        say("Idea: a BST read in order comes out sorted. Read each tree in order and look.", Colors.LIGHT_GRAY);
        pause(1.8);
        List<GN> ol = new ArrayList<>(), or = new ArrayList<>();
        inorderInto(bad, ol);
        inorderInto(good, or);
        for (int i = 0; i < 7; i++) {
            List<Animation> a = new ArrayList<>();
            tl.paint(a, ol.get(i), VISIT_C, d(0.3));
            tr.paint(a, or.get(i), VISIT_C, d(0.3));
            playAll(a);
            boolean broken = i == 2 || i == 3;
            sl.add(ol.get(i).name, broken ? Colors.RED : Colors.GREEN);
            sr.add(or.get(i).name);
            mine.addAll(sl.all());
            mine.addAll(sr.all());
            List<Animation> b = new ArrayList<>();
            tl.paint(b, ol.get(i), broken ? GONE_C : DONE_C, d(0.3));
            tr.paint(b, or.get(i), DONE_C, d(0.3));
            playAll(b);
        }
        say("Left: 4 comes before 3, so the order breaks: not a BST. Right: 1 2 3 4 5 6 8 is sorted: a BST.", Colors.GOLD);
        pause(3.6);
        clearAll();
    }

    // ── 2: minimum height ────────────────────────────────────────────

    private void minHeight() {
        section(2, "Minimum height");
        int[] vals = {1, 2, 3, 4, 5, 6, 7};
        List<List<MObject>> chips = new ArrayList<>();
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            List<MObject> c = chip(String.valueOf(vals[i]), (i - 3) * 100, -320, 76, 56, Colors.BLUE, 30);
            chips.add(c);
            mine.addAll(c);
            for (MObject m : c) in.add(new FadeInAt(m, 0.1 * i, d(0.4)));
        }
        playAll(in);
        say("Sort the values. The tree is lowest when it is as even as possible.", Colors.LIGHT_GRAY);
        pause(1.6);
        GN root = bstOf(4, 2, 6, 1, 3, 5, 7);
        GT t = bst(root, 0, -180, 120, 32, 34);
        mine.addAll(t.parts());
        int[][] rounds = {{4}, {2, 6}, {1, 3, 5, 7}};
        String[] words = {"Insert the middle value first: it becomes the root.",
                "Then the middle of each half.", "Then the middle of each quarter."};
        for (int r = 0; r < rounds.length; r++) {
            say(words[r], Colors.LIGHT_GRAY);
            List<Animation> a = new ArrayList<>();
            for (int v : rounds[r]) {
                a.add(new ColorChange(chips.get(v - 1).get(0), Colors.withAlpha(Colors.GOLD, 0.45), d(0.4)));
                a.add(new ColorChange(chips.get(v - 1).get(0), Colors.GOLD, d(0.4), ColorChange.Target.STROKE));
                t.showNode(a, bstFind(root, v), 0.1, d(0.6));
            }
            playAll(a);
            for (int v : rounds[r]) t.pop(bstFind(root, v));
            pause(1.4);
        }
        say("Order 4, 2, 6, 1, 3, 5, 7 gives height 2, the least possible for 7 values.", Colors.GREEN);
        pause(2.2);
        say("It is exactly the order in which binary search probes a sorted array.", Colors.GOLD);
        pause(3.2);
        clearAll();
    }

    // ── 3: count the leaves ──────────────────────────────────────────

    private CodeBox code;

    private void countLeaves() {
        section(3, "Count the leaves");
        code = new CodeBox(new String[]{
                "int Tree::leaves(PtrToNode rr) {",
                "    if (rr == NULL) return 0;",
                "    if (!rr->left && !rr->right) return 1;",
                "    return leaves(rr->left) + leaves(rr->right);",
                "}"}, -930, -310, 24, 42);
        code.typeIn(4.0);
        mine.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.3)));
        GN root = bstOf(6, 2, 8, 1, 4, 3, 5);
        GT t = bst(root, 520, -300, 100, 26, 28);
        t.build(0.25);
        mine.addAll(t.parts());
        Steps st = new Steps(-560, 150, 110);
        List<Animation> sa = new ArrayList<>();
        fade(sa, st.parts(), d(0.5));
        playAll(sa);
        mine.addAll(st.parts());
        TextMob capt = label("leaves so far", -560, 250, 30, Colors.GRAY, false, false);
        play(new FadeIn(capt, d(0.5)));
        mine.add(capt);
        say("A node with no children is a leaf and counts 1; otherwise add the leaves of both subtrees.", Colors.LIGHT_GRAY);
        pause(1.8);
        List<GN> pre = new ArrayList<>();
        preorderInto(root, pre);
        for (GN n : pre) {
            lit(t, n, VISIT_C, d(0.3));
            if (n.leaf()) {
                play(code.moveHl(2, d(0.25)));
                lit(t, n, FOUND_C, d(0.3));
                st.tick();
                t.pop(n);
            } else {
                play(code.moveHl(3, d(0.25)));
            }
            pause(0.45);
        }
        say("4 leaves: 1, 3, 5 and 8.", Colors.GREEN);
        pause(3.0);
        clearAll();
    }

    // ── 4: level order ───────────────────────────────────────────────

    private void levelOrder() {
        section(4, "Level-order printing");
        GN root = bstOf(6, 2, 8, 1, 4, 3, 5);
        GT t = bst(root, 0, -300, 100, 26, 28);
        t.build(0.25);
        mine.addAll(t.parts());
        TextMob ql = label("queue:", -900, 200, 34, Colors.PURPLE, true, true);
        OutStrip os = new OutStrip("level order:", -900, 320, 56, Colors.GREEN);
        List<Animation> in = new ArrayList<>();
        in.add(new FadeIn(ql, d(0.5)));
        playAll(in);
        os.showTitle();
        mine.add(ql);
        mine.addAll(os.all());
        say("Use a queue: take the front node, write it, and put its children at the back.", Colors.LIGHT_GRAY);
        pause(2.0);

        List<GN> queue = new ArrayList<>();
        List<List<MObject>> qc = new ArrayList<>();
        enqueue(queue, qc, root);
        pause(0.8);
        int step = 0;
        while (!queue.isEmpty()) {
            GN n = queue.remove(0);
            List<MObject> front = qc.remove(0);
            List<Animation> a = new ArrayList<>();
            for (MObject m : front) a.add(new FadeOut(m, d(0.4)));
            for (int k = 0; k < qc.size(); k++)
                for (MObject m : qc.get(k)) a.add(new MoveTo(m, queueX(k), 200, d(0.5)).setEasing(Easing.EASE_IN_OUT));
            lit(t, n, VISIT_C, d(0.3));
            playAll(a);
            os.add(n.name);
            mine.addAll(os.all());
            List<Animation> b = new ArrayList<>();
            t.paint(b, n, DONE_C, d(0.3));
            t.paintEdge(b, n, DONE_C, d(0.3));
            playAll(b);
            for (GN c : n.kids) enqueue(queue, qc, c);
            pause(step < 2 ? 0.8 : 0.3);
            step++;
        }
        say("6 2 8 1 4 3 5: level by level, left to right.", Colors.GREEN);
        pause(3.0);
        clearAll();
    }

    private double queueX(int k) { return -700 + k * 96; }

    private void enqueue(List<GN> queue, List<List<MObject>> qc, GN n) {
        int k = queue.size();
        List<MObject> c = chip(n.name, queueX(k), 200, 76, 54, Colors.PURPLE, 30);
        queue.add(n);
        qc.add(c);
        mine.addAll(c);
        List<Animation> a = new ArrayList<>();
        fade(a, c, d(0.4));
        playAll(a);
    }

    // ── 5: reverse-sorted order ──────────────────────────────────────

    private OutStrip strip;
    private GT tree5;

    private void reverseOrder() {
        section(5, "Reverse-sorted order");
        code = new CodeBox(new String[]{
                "void Tree::reverse(PtrToNode rr) {",
                "    if (rr) {",
                "        reverse(rr->right);",
                "        std::cout << rr->data << \" \";",
                "        reverse(rr->left);",
                "    }",
                "}"}, -930, -310, 24, 42);
        code.typeIn(4.5);
        mine.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.3)));
        GN root = bstOf(6, 2, 8, 1, 4, 3, 5);
        tree5 = bst(root, 520, -300, 100, 26, 28);
        tree5.build(0.25);
        mine.addAll(tree5.parts());
        strip = new OutStrip("output:", -900, 320, 56, Colors.GREEN);
        strip.showTitle();
        mine.addAll(strip.all());
        say("In-order walks left, node, right. For the opposite order, walk right, node, left.", Colors.LIGHT_GRAY);
        pause(2.2);
        walkReverse(root);
        say("8 6 5 4 3 2 1: the values in reverse-sorted order.", Colors.GREEN);
        pause(3.0);
        clearAll();
    }

    private void walkReverse(GN n) {
        play(code.moveHl(1, d(0.2)));
        lit(tree5, n, VISIT_C, d(0.3));
        GN r = child(n, 1), l = child(n, 0);
        play(code.moveHl(2, d(0.25)));
        pause(0.2);
        if (r != null) walkReverse(r);
        play(code.moveHl(3, d(0.25)));
        strip.add(n.name);
        mine.addAll(strip.all());
        List<Animation> a = new ArrayList<>();
        tree5.paint(a, n, DONE_C, d(0.3));
        tree5.paintEdge(a, n, DONE_C, d(0.3));
        playAll(a);
        pause(0.3);
        play(code.moveHl(4, d(0.25)));
        if (l != null) walkReverse(l);
    }
}
