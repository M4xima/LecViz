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
 * Standalone clip for slide 4 of the dictionary deck: the BST ADT.
 *
 *   - the slide's class listing types in; a highlight bar visits each method while a small tree on the right
 *     shows what the method does: search walks down to a node, findMin goes all the way left, findMax all the
 *     way right, insert hangs a new leaf, remove takes a node out
 *   - the slide declares {@code void remove();} with no argument although the next slides remove a given
 *     value, so the line is corrected on screen: the "()" is struck out and the parameter written in
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSDictAdtScene extends PDSDictClipBase {

    private final List<MObject> mine = new ArrayList<>();
    private CodeBox code;
    private GT t;
    private GN root, seven;

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("BST ADT");
        pause(0.5);
        code = new CodeBox(new String[]{
                "class BST {",
                "    ...",
                "public:",
                "    ...",
                "    PtrToNode search(DataType element);",
                "    PtrToNode findMin();",
                "    PtrToNode findMax();",
                "    PtrToNode insert(DataType element);",
                "    void remove();",
                "};"}, -910, -290, 28, 46);
        code.typeIn(5.0);
        mine.addAll(code.parts());
        pause(0.6);

        root = bstOf(6, 2, 8, 1, 4, 3, 5, 7);
        seven = bstFind(root, 7);
        t = bst(root, 520, -310, 110, 26, 28);
        for (int dep = 0; dep <= height(root); dep++) {
            List<Animation> a = new ArrayList<>();
            for (GN n : t.nodes) if (n.depth == dep && n != seven) t.showNode(a, n, 0, d(0.5));
            playAll(a);
            pause(0.25);
        }
        mine.addAll(t.parts());
        play(new FadeIn(code.hl, d(0.3)));
        code.setLine(4);
        pause(0.4);

        method(4, "search: is this value in the tree?", Colors.BLUE, () -> walk(5));
        method(5, "findMin: keep going left.", Colors.TEAL, () -> walk(1));
        method(6, "findMax: keep going right.", Colors.PINK, () -> walk(8));
        method(7, "insert: the new value hangs as a leaf.", Colors.GREEN, this::insert7);
        method(8, "remove: takes a value out of the tree.", Colors.RED, this::remove7);

        // the slide's remove() has no argument
        sayAt("remove must be told which value to remove.", Colors.GOLD, 520, 150, 32);
        pause(1.2);
        int col = "    void remove();".indexOf("()");
        LineMob strike = code.strikeAt(8, col, 2);
        play(new DrawLine(strike, d(0.5)));
        pause(0.4);
        List<Animation> a = new ArrayList<>();
        code.rewrite(a, 8, "    void remove(DataType element);", d(0.6));
        a.add(new FadeOut(strike, d(0.6)));
        playAll(a);
        mine.addAll(code.parts());
        pause(2.2);
        unsay();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void method(int line, String caption, Color c, Runnable demo) {
        play(code.moveHl(line, d(0.4)));
        sayAt(caption, c, 520, 150, 34);
        pause(0.4);
        demo.run();
        pause(0.8);
    }

    /** Walks from the root to the node with this value, lighting the path, then lets it go. */
    private void walk(int v) {
        List<GN> path = bstPath(root, v);
        for (int i = 0; i < path.size(); i++) {
            boolean last = i == path.size() - 1;
            lit(t, path.get(i), last ? FOUND_C : VISIT_C, d(0.35));
            pause(0.2);
        }
        t.pop(path.get(path.size() - 1));
        pause(0.6);
        plainAll(t, d(0.4));
    }

    private void insert7() {
        List<GN> path = bstPath(root, 7);
        // 7 < 8 is the last comparison: the search ends where 7 is missing
        for (int i = 0; i < path.size() - 1; i++) {
            lit(t, path.get(i), VISIT_C, d(0.3));
            pause(0.15);
        }
        List<Animation> a = new ArrayList<>();
        t.showNode(a, seven, 0, d(0.6));
        playAll(a);
        lit(t, seven, NEW_C, d(0.4));
        t.pop(seven);
        pause(0.6);
        plainAll(t, d(0.4));
    }

    private void remove7() {
        lit(t, seven, GONE_C, d(0.4));
        pause(0.4);
        List<Animation> a = new ArrayList<>();
        t.hideNode(a, seven, d(0.6));
        playAll(a);
        pause(0.4);
    }
}
