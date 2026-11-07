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
 * Standalone clip for slide 5 of the dictionary deck: search in a BST.
 *
 *   - the slide's two functions type in; a highlight bar follows the line being executed
 *   - the tree on the right: every call lights the node it is looking at; a call stack below the tree grows
 *     by one frame per recursive call and shrinks as the true / false answers come back
 *   - two searches: 5, which is found, and 7, which runs off the tree into a NULL and is reported absent
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSDictSearchScene extends PDSDictClipBase {

    private final List<MObject> mine = new ArrayList<>();
    private CodeBox code;
    private GT t;
    private GN root;
    private CallStack cs;
    private final Color[] fc = {Colors.TEAL, Colors.BLUE, Colors.PINK, Colors.GOLD, Colors.PURPLE};
    private int calls;
    private final List<MObject> temp = new ArrayList<>();

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("Search");
        pause(0.5);
        code = new CodeBox(new String[]{
                "bool Tree::search(DataType data, PtrToNode rr) {",
                "    if (rr == NULL) return false;",
                "    if (data == rr->data) return true;",
                "    if (data < rr->data) return search(data, rr->left);",
                "    return search(data, rr->right);",
                "}",
                "bool Tree::search(DataType data) {",
                "    bool present = search(data, root);",
                "    if (present)",
                "        std::cout << data << \" present.\" << std::endl;",
                "    else",
                "        std::cout << data << \" NOT present.\" << std::endl;",
                "    return present;",
                "}"}, -930, -340, 22, 37);
        code.typeIn(7.0);
        mine.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.3)));
        TextMob src = label("Switch to bst.cpp.", 760, -470, 26, Colors.GRAY, false, false);
        play(new FadeIn(src, d(0.6)));
        mine.add(src);

        root = bstOf(6, 2, 8, 1, 4, 3, 5);
        t = bst(root, 560, -330, 100, 26, 28);
        t.build(0.25);
        mine.addAll(t.parts());
        cs = new CallStack(560, 110, 440, 46);
        cs.showTitle();
        mine.addAll(cs.all());
        pause(0.6);

        run(5);
        reset();
        run(7);
        reset();
        unsay();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void say(String s, Color c) { sayAt(s, c, 0, 446, 34); }

    private void hl(int line) { play(code.moveHl(line, d(calls < 5 ? 0.35 : 0.2))); }

    private void push(String text) {
        cs.push(text, fc[Math.min(cs.frames.size(), fc.length - 1)]);
        mine.addAll(cs.all());
    }

    private void pop(String ret, Color c) {
        cs.pop(ret, c);
        mine.addAll(cs.all());
    }

    private void run(int v) {
        say("search(" + v + ") asks the tree whether " + v + " is in it.", Colors.LIGHT_GRAY);
        hl(6);
        push("search(" + v + ")");
        hl(7);
        pause(0.4);
        boolean found = rec(v, root, null, 0);
        hl(8);
        TextMob out = mono(v + (found ? " present." : " NOT present."), 560, 380, 34, found ? Colors.GREEN : Colors.RED);
        hl(found ? 9 : 11);
        play(new FadeIn(out, d(0.5)));
        temp.add(out);
        hl(12);
        pop(found ? "true" : "false", found ? Colors.GREEN : Colors.RED);
        say(found ? v + " is in the tree." : v + " is not in the tree: the search fell off a leaf.", found ? Colors.GREEN : Colors.RED);
        pause(2.4);
    }

    private boolean rec(int v, GN n, GN parent, int slot) {
        calls++;
        push("search(" + v + ", " + (n == null ? "NULL" : n.name) + ")");
        hl(0);
        pause(0.2);
        if (n == null) {
            TextMob nul = label("NULL", parent.x + (slot == 0 ? -52 : 52), parent.y + 92, 26, Colors.GRAY, false, true);
            play(new FadeIn(nul, d(0.4)));
            temp.add(nul);
            hl(1);
            say("rr is NULL: there is nothing here, so the answer is false.", Colors.RED);
            pause(0.9);
            pop("false", Colors.RED);
            return false;
        }
        lit(t, n, VISIT_C, d(0.35));
        hl(1);
        pause(calls < 4 ? 0.4 : 0.2);
        hl(2);
        if (v == val(n)) {
            lit(t, n, FOUND_C, d(0.35));
            t.pop(n);
            say(v + " == " + n.name + ": found it, return true.", Colors.GREEN);
            pause(1.0);
            pop("true", Colors.GREEN);
            return true;
        }
        boolean goLeft = v < val(n);
        hl(goLeft ? 3 : 4);
        say(goLeft ? v + " < " + n.name + ": search the left subtree." : v + " > " + n.name + ": search the right subtree.", Colors.LIGHT_GRAY);
        pause(calls < 4 ? 0.8 : 0.4);
        boolean r = rec(v, child(n, goLeft ? 0 : 1), n, goLeft ? 0 : 1);
        hl(goLeft ? 3 : 4);
        pop(r ? "true" : "false", r ? Colors.GREEN : Colors.RED);
        return r;
    }

    private void reset() {
        List<Animation> a = new ArrayList<>();
        for (GN n : t.nodes) plain(t, a, n, d(0.4));
        for (MObject m : temp) a.add(new FadeOut(m, d(0.4)));
        playAll(a);
        for (MObject m : temp) remove(m);
        temp.clear();
        play(code.moveHl(0, d(0.3)));
        calls = 0;
        pause(0.4);
    }
}
