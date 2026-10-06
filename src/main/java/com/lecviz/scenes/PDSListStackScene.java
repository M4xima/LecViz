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
 * Standalone clip for slides 23-24 of the lists deck: the Stack ADT and List versus Stack.
 *
 *   Slide 23  the points one at a time; then a stack of plates: push drops a plate on top, pop lifts the
 *             top one away (what went in as A B C D comes out D C), an attempt to take a plate from the
 *             middle is refused, and the implementation sits behind a question mark: "we do not care yet"
 *   Slide 24  the List class and the Stack class typed side by side; the operations the stack does not
 *             have are struck through one by one (pop takes no argument, no search, no size, no print) and
 *             isEmpty is the one added
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListStackScene extends PDSListClipBase {

    private StrokeTextMob head;
    private StrokeTextMob cap;

    @Override
    public void construct() {
        stackAdt();
        listVersusStack();
    }

    private void caption(String text, Color c, double x, double y, double size) {
        StrokeTextMob n = stroke(text, x, y, size, c, false);
        if (cap != null) {
            play(new FadeOut(cap, d(0.3)));
            remove(cap);
        }
        play(new Write(n, d(Math.max(1.4, text.length() * 0.05))));
        cap = n;
    }

    private void dropCaption() {
        if (cap != null) {
            play(new FadeOut(cap, d(0.3)));
            remove(cap);
            cap = null;
        }
    }

    // ── slide 23 ─────────────────────────────────────────────────────

    private void stackAdt() {
        head = writeHeading("Stack ADT");
        pause(0.4);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Special List"));
        s.add(ln(0, "Operations restricted to one end."));
        s.add(ln(0, "Insert  -->  Push").kw("Push", Colors.TEAL));
        s.add(ln(0, "Remove  -->  Pop").kw("Pop", Colors.PINK));
        s.add(ln(0, "LIFO").kw("LIFO", Colors.GOLD));
        s.add(ln(0, "Cannot access arbitrary element."));
        s.add(ln(0, "Important: Since this is ADT, we do not care about").kw("Important", Colors.RED));
        s.add(ln(3, "the implementation yet."));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(1.2);
        swipeAway(text);
        pause(0.4);

        List<MObject> mine = new ArrayList<>();
        mine.add(head);
        VStack st = new VStack(-480, 360, 230, 58, 6, "top", -1);
        st.showBox();
        st.showTop();
        mine.addAll(st.parts());

        String[] ids = {"A", "B", "C", "D"};
        Color[] cols = {Colors.TEAL, Colors.BLUE, Colors.PINK, Colors.GOLD};
        TextMob inLab = label("in:", 300, -45, 34, Colors.GRAY, false, true);
        TextMob outLab = label("out:", 300, 140, 34, Colors.GRAY, false, true);
        play(new FadeIn(inLab, d(0.4)), new FadeIn(outLab, d(0.4)));
        mine.add(inLab);
        mine.add(outLab);

        // push A B C D
        caption("Insert --> Push: each new plate goes on top.", Colors.WHITE, 480, -300, 38);
        for (int i = 0; i < 4; i++) {
            List<MObject> call = chip("push(" + ids[i] + ")", 480, -190, 230, 58, cols[i], 30);
            List<Animation> a = new ArrayList<>();
            fade(a, call, d(0.35));
            playAll(a);
            Cell c = st.push(ids[i], cols[i]);
            mine.addAll(c.parts());
            Cell in = tokCell(ids[i], 420 + 110 * i, -45, 84, 62, cols[i], 34);
            List<Animation> ia = new ArrayList<>();
            in.fadeIn(ia, 0, d(0.5));
            playAll(ia);
            mine.addAll(in.parts());
            List<Animation> gone = new ArrayList<>();
            for (MObject m : call) gone.add(new FadeOut(m, d(0.3)));
            playAll(gone);
            for (MObject m : call) remove(m);
        }
        pause(0.8);

        // pop D, pop C
        caption("Remove --> Pop: the plate on top leaves first.", Colors.WHITE, 480, -300, 38);
        for (int i = 3; i >= 2; i--) {
            List<MObject> call = chip("pop()", 480, -190, 230, 58, Colors.PINK, 30);
            List<Animation> a = new ArrayList<>();
            fade(a, call, d(0.35));
            playAll(a);
            Cell c = st.popTo(520 + 240 * (3 - i), 140);
            mine.addAll(c.parts());
            List<Animation> gone = new ArrayList<>();
            for (MObject m : call) gone.add(new FadeOut(m, d(0.3)));
            playAll(gone);
            for (MObject m : call) remove(m);
        }
        pause(0.6);
        caption("LIFO: Last In, First Out. In: A B C D, out: D C ...", Colors.GOLD, 480, 270, 38);
        pause(2.0);

        // no access to the middle
        caption("Cannot access an arbitrary element.", Colors.WHITE, 480, -300, 38);
        Cell bottom = st.items.get(0);
        Link hand = arrow(150, bottom.y, st.cx + st.w / 2 + 6, bottom.y, Colors.RED, 4);
        TextMob want = mono("get(A) ?", 290, bottom.y - 40, 30, Colors.RED);
        play(new DrawLink(hand, d(0.8)), new FadeIn(want, d(0.8)));
        Link x1 = new Link(new double[]{st.cx - 50, st.cx + 50}, new double[]{bottom.y - 22, bottom.y + 22}, Colors.RED, 6, false);
        Link x2 = new Link(new double[]{st.cx - 50, st.cx + 50}, new double[]{bottom.y + 22, bottom.y - 22}, Colors.RED, 6, false);
        add(x1);
        add(x2);
        play(new DrawLink(x1, d(0.35)), new DrawLink(x2, d(0.35)));
        mine.add(hand);
        mine.add(want);
        mine.add(x1);
        mine.add(x2);
        pause(1.0);
        List<Animation> sw = new ArrayList<>();
        sw.add(new FadeOut(x1, d(0.4)));
        sw.add(new FadeOut(x2, d(0.4)));
        sw.add(new FadeOut(hand, d(0.4)));
        sw.add(new FadeOut(want, d(0.4)));
        playAll(sw);
        Cell topCell = st.top();
        Link ok = arrow(150, topCell.y, st.cx + st.w / 2 + 6, topCell.y, Colors.GREEN, 4);
        TextMob okLab = mono("pop() gives B", 300, topCell.y - 40, 30, Colors.GREEN);
        play(new DrawLink(ok, d(0.8)), new FadeIn(okLab, d(0.8)));
        mine.add(ok);
        mine.add(okLab);
        caption("Only the top plate can be taken.", Colors.GREEN, 480, -300, 38);
        pause(2.0);

        // the implementation is not our concern yet
        List<Animation> clear = new ArrayList<>();
        clear.add(new FadeOut(ok, d(0.5)));
        clear.add(new FadeOut(okLab, d(0.5)));
        playAll(clear);
        RectMob impl = panel(520, 280, 640, 200, Colors.GRAY, 0.1);
        TextMob q = label("?", 520, 262, 110, Colors.GRAY, false, true);
        TextMob implLab = label("implementation: array or linked list?", 520, 355, 30, Colors.LIGHT_GRAY, false, true);
        play(new FadeIn(impl, d(0.6)), new FadeIn(q, d(0.6)), new FadeIn(implLab, d(0.6)));
        mine.add(impl);
        mine.add(q);
        mine.add(implLab);
        caption("Important: it is an ADT, so we do not care about the implementation yet.", Colors.ORANGE, 300, -300, 34);
        pause(3.0);
        dropCaption();
        fadeOutAll(d(1.0), mine);
        pause(0.4);
    }

    // ── slide 24 ─────────────────────────────────────────────────────

    private void listVersusStack() {
        head = writeHeading("List versus Stack");
        pause(0.4);
        List<MObject> mine = new ArrayList<>();
        mine.add(head);
        CodeBox list = new CodeBox(new String[]{
                "class List {",
                "  void insert(Element);",
                "  void remove(Element);",
                "  bool search(Element);",
                "  int size();",
                "  void print();",
                "  ...",
                "};"}, -900, -330, 30, 60);
        CodeBox stack = new CodeBox(new String[]{
                "class Stack {",
                "  void push(Element);",
                "  Element pop(Element);",
                "  bool search(Element);",
                "  int size(); bool isEmpty();",
                "  void print();",
                "  ...",
                "};"}, -330, -330, 30, 60);
        stack.card.setFillColor(Colors.withAlpha(Colors.BLUE, 0.13));
        stack.card.setStrokeColor(Colors.withAlpha(Colors.BLUE, 0.6));
        list.typeIn(3.2);
        stack.typeIn(3.2);
        mine.addAll(list.parts());
        mine.addAll(stack.parts());
        pause(1.0);

        // the operations the stack does not have, struck through one by one
        play(new FadeIn(stack.hl, d(0.3)));
        int[][] strikes = {{2, 14, 7}, {3, 2, 21}, {4, 2, 11}, {5, 2, 13}};
        String[] why = {
                "pop takes no argument: it always takes the top",
                "no search: only the top is within reach",
                "no size: just ask whether it is empty",
                "no print: the stack cannot be walked through"};
        Color[] wc = {Colors.PINK, Colors.ORANGE, Colors.ORANGE, Colors.ORANGE};
        for (int k = 0; k < 4; k++) {
            int ln = strikes[k][0];
            play(stack.moveHl(ln, d(0.4)));
            LineMob st = stack.strikeAt(ln, strikes[k][1], strikes[k][2]);
            List<MObject> note = chip(why[k], 610, stack.lineY(ln), 560, 56, wc[k], 24);
            List<Animation> a = new ArrayList<>();
            a.add(new DrawLine(st, d(0.5)));
            fade(a, note, d(0.5));
            playAll(a);
            mine.add(st);
            mine.addAll(note);
            pause(0.9);
        }

        // isEmpty is the one new operation
        play(stack.moveHl(4, d(0.4)));
        List<MObject> add = chip("isEmpty() is added: is there a top at all?", 610, stack.lineY(6) + 10, 560, 56, Colors.GREEN, 24);
        List<Animation> a = new ArrayList<>();
        fade(a, add, d(0.6));
        playAll(a);
        mine.addAll(add);
        pause(1.4);

        StrokeTextMob cap1 = stroke("A stack only ever touches one end: push, pop and isEmpty are enough.", 0, 300, 40, Colors.WHITE, false);
        play(new Write(cap1, d(3.6)));
        mine.add(cap1);
        pause(3.0);
        fadeOutAll(d(1.2), mine);
        pause(0.4);
    }
}
