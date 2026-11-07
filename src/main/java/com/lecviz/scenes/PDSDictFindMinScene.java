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
 * Standalone clip for slide 6 of the dictionary deck: FindMin and the classwork "write iterative findMax".
 *
 *   - the recursive findmin types in; the tree on the right lights the node of every call while a call stack
 *     grows down the left spine and the answer 1 comes back up through every frame
 *   - the iterative version: a pointer named ptr glides down the left spine while the loop condition is checked
 *   - classwork: the question appears, then findMax is written the mirror way (right instead of left) and
 *     the pointer runs down the right spine to 8
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSDictFindMinScene extends PDSDictClipBase {

    private final List<MObject> mine = new ArrayList<>();
    private final List<MObject> temp = new ArrayList<>();
    private CodeBox code;
    private GT t;
    private GN root;
    private CallStack cs;
    private final Color[] fc = {Colors.TEAL, Colors.BLUE, Colors.PINK, Colors.GOLD, Colors.PURPLE};
    private int calls;

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("FindMin");
        pause(0.5);

        root = bstOf(6, 2, 8, 1, 4, 3, 5);
        t = bst(root, 560, -330, 100, 26, 28);
        t.build(0.25);
        mine.addAll(t.parts());

        recursive();
        iterative();
        classwork(head);

        unsay();
        mine.addAll(temp);
        fadeOutAll(d(1.2), mine);
        pause(0.3);
    }

    // ── recursive ────────────────────────────────────────────────────

    private void recursive() {
        code = new CodeBox(new String[]{
                "DataType Tree::findmin(PtrToNode rr) {",
                "    if (rr) {",
                "        if (rr->left) return findmin(rr->left);",
                "        return rr->data;",
                "    }",
                "    return -1;",
                "}",
                "DataType Tree::findmin() {",
                "    return findmin(root);",
                "}"}, -930, -340, 24, 40);
        code.typeIn(6.0);
        play(new FadeIn(code.hl, d(0.3)));
        cs = new CallStack(560, 110, 380, 46);
        cs.showTitle();
        mine.addAll(cs.all());
        pause(0.6);

        say("findmin() starts at the root and asks findmin(root).", Colors.LIGHT_GRAY);
        hl(7);
        push("findmin()");
        hl(8);
        pause(0.4);
        int ans = rec(root);
        hl(8);
        pop(String.valueOf(ans), Colors.GREEN);
        TextMob out = mono("min = " + ans, 560, 380, 36, Colors.GREEN);
        play(new FadeIn(out, d(0.5)));
        temp.add(out);
        say("The smallest value is at the end of the left spine.", Colors.GREEN);
        pause(2.6);
        clear(true);
    }

    private int rec(GN n) {
        calls++;
        push("findmin(" + n.name + ")");
        hl(0);
        lit(t, n, VISIT_C, d(0.35));
        hl(1);
        pause(calls < 3 ? 0.4 : 0.2);
        hl(2);
        GN l = child(n, 0);
        int r;
        if (l != null) {
            say(n.name + " has a left child, so a smaller value may be there.", Colors.LIGHT_GRAY);
            pause(0.7);
            r = rec(l);
            hl(2);
        } else {
            say(n.name + " has no left child: nothing is smaller, return " + n.name + ".", Colors.GREEN);
            lit(t, n, FOUND_C, d(0.35));
            t.pop(n);
            hl(3);
            pause(0.8);
            r = val(n);
        }
        pop(String.valueOf(r), Colors.GREEN);
        return r;
    }

    // ── iterative ────────────────────────────────────────────────────

    private void iterative() {
        unsay();
        code = new CodeBox(new String[]{
                "DataType Tree::findminiterative() {",
                "    PtrToNode ptr = root;",
                "    if (ptr) {",
                "        while (ptr->left) ptr = ptr->left;",
                "        return ptr->data;",
                "    }",
                "    return -1;",
                "}"}, -930, -340, 24, 40);
        code.typeIn(5.0);
        play(new FadeIn(code.hl, d(0.3)));
        say("The same walk without recursion: one pointer going left.", Colors.LIGHT_GRAY);
        pause(0.8);
        walk(false, "min");
        clear(false);
    }

    /** ptr = root, then follow left (or right) children until there are none; answer shown at the end. */
    private void walk(boolean right, String what) {
        String dir = right ? "right" : "left";
        hl(1);
        NodePtr p = new NodePtr("ptr", root, Colors.ORANGE);
        List<Animation> a = new ArrayList<>();
        p.show(a, d(0.5));
        playAll(a);
        temp.addAll(p.parts());
        lit(t, root, VISIT_C, d(0.35));
        pause(0.5);
        hl(2);
        pause(0.4);
        GN n = root;
        int k = 0;
        while (true) {
            hl(3);
            GN next = child(n, right ? 1 : 0);
            if (next == null) {
                say("ptr->" + dir + " is NULL: stop, ptr is on the " + (right ? "largest" : "smallest") + " value.", Colors.GREEN);
                pause(0.9);
                break;
            }
            say("ptr->" + dir + " exists: move ptr to " + next.name + ".", Colors.LIGHT_GRAY);
            pause(k < 2 ? 0.7 : 0.4);
            List<Animation> g = new ArrayList<>();
            p.go(g, next, d(0.5));
            playAll(g);
            lit(t, next, VISIT_C, d(0.35));
            n = next;
            k++;
        }
        lit(t, n, FOUND_C, d(0.35));
        t.pop(n);
        hl(4);
        TextMob out = mono(what + " = " + n.name, 560, 380, 36, Colors.GREEN);
        play(new FadeIn(out, d(0.5)));
        temp.add(out);
        pause(2.2);
    }

    // ── classwork ────────────────────────────────────────────────────

    private void classwork(StrokeTextMob head) {
        unsay();
        List<MObject> gone = new ArrayList<>(code.parts());
        gone.add(head);
        fadeOutAll(d(0.8), gone);
        List<MObject> q = question(1, "Write iterative findMax.", -440, 46, new String[]{"findMax"}, Colors.GOLD);
        mine.addAll(q);
        pause(1.0);
        TextMob hint = label("Mirror image of findMin: the largest value is on the right.", -790, -380, 30, Colors.GRAY, true, false);
        play(new FadeIn(hint, d(0.7)));
        mine.add(hint);
        pause(1.0);
        code = new CodeBox(new String[]{
                "DataType Tree::findmaxiterative() {",
                "    PtrToNode ptr = root;",
                "    if (ptr) {",
                "        while (ptr->right) ptr = ptr->right;",
                "        return ptr->data;",
                "    }",
                "    return -1;",
                "}"}, -930, -300, 24, 40);
        code.typeIn(5.0);
        play(new FadeIn(code.hl, d(0.3)));
        mine.addAll(code.parts());
        pause(0.8);
        walk(true, "max");
    }

    // ── helpers ──────────────────────────────────────────────────────

    private void say(String s, Color c) { sayAt(s, c, 0, 446, 34); }

    private void hl(int line) { play(code.moveHl(line, d(calls < 4 ? 0.35 : 0.2))); }

    private void push(String text) {
        cs.push(text, fc[Math.min(cs.frames.size(), fc.length - 1)]);
        mine.addAll(cs.all());
    }

    private void pop(String ret, Color c) {
        cs.pop(ret, c);
        mine.addAll(cs.all());
    }

    /** Back to a plain tree; the call stack title goes too when {@code stackToo}. */
    private void clear(boolean stackToo) {
        List<Animation> a = new ArrayList<>();
        for (GN n : t.nodes) plain(t, a, n, d(0.4));
        for (MObject m : temp) a.add(new FadeOut(m, d(0.4)));
        List<MObject> codeParts = new ArrayList<>(code.parts());
        for (MObject m : codeParts) a.add(new FadeOut(m, d(0.5)));
        if (stackToo) a.add(new FadeOut(cs.title, d(0.4)));
        playAll(a);
        for (MObject m : temp) remove(m);
        temp.clear();
        calls = 0;
        pause(0.4);
    }
}
