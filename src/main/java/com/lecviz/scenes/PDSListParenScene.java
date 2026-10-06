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
 * Standalone clip for slides 26-27 of the lists deck: balanced parentheses.
 *
 *   Slide 26  the problem, the valid and invalid inputs one line at a time, the classwork and the question
 *   Slide 27  the algorithm typed in with the slide's two "find a string to match this error" callouts, then
 *             run on strings: a valid one, ( [ ) ] { } (the first error: the top does not match), ( ( ( ) )
 *             (the second error: the stack is not empty at the end) and } } ) ( { { (a closer with an empty
 *             stack). The stack, the input and the highlighted line of the algorithm move together.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListParenScene extends PDSListClipBase {

    private static final double RX = 470;

    private StrokeTextMob head;
    private CodeBox code;
    private VStack stack;
    private final List<MObject> stage = new ArrayList<>();

    @Override
    public void construct() {
        head = writeHeading("Balanced Parentheses");
        pause(0.4);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "We want to check if parentheses are balanced or"));
        s.add(ln(3, "not."));
        s.add(ln(0, "Three types of parentheses: ( ), [ ] and { }"));
        s.add(ln(0, "Valid inputs:"));
        s.add(ln(1, "( [ ] [ { } ] )"));
        s.add(ln(1, "[ ] { } [ ] ( ) [ [ [ ] ] ]"));
        s.add(ln(0, "Invalid inputs:"));
        s.add(ln(1, "( ( ( ) )"));
        s.add(ln(1, "( [ ) ] { }"));
        s.add(ln(1, "} } ) ( { {"));
        List<List<MObject>> text = writeSlide(s, -340);

        // the classwork and the question sit to the right of the examples
        List<MObject> side = new ArrayList<>();
        String[][] rows = {
                {"Classwork:", " Use stack to design an", "-70"},
                {"", "algorithm to check for balanced", "-22"},
                {"", "parentheses.", "26"},
                {"Question:", " Can we design an", "120"},
                {"", "application of stack from its ADT", "168"},
                {"", "without knowing its implementation?", "216"}};
        for (String[] r : rows) {
            double y = Double.parseDouble(r[2]);
            double x = -130;
            if (!r[0].isEmpty()) {
                StrokeTextMob lead = stroke(r[0], x + strokeW(r[0], true, 34) / 2, y, 34, Colors.RED, true);
                play(new Write(lead, d(0.9)));
                side.add(lead);
                x += strokeW(r[0], true, 34);
            }
            StrokeTextMob t = strokeLeft(r[1], x, y, 34, Colors.WHITE);
            play(new Write(t, d(Math.max(1.0, r[1].length() * 0.05))));
            side.add(t);
        }
        pause(1.6);
        List<List<MObject>> all = new ArrayList<>(text);
        for (MObject m : side) {
            List<MObject> g = new ArrayList<>();
            g.add(m);
            all.add(g);
        }
        swipeAway(all);
        pause(0.4);

        algorithm();
        fadeOutAll(d(1.0), head);
        pause(0.4);
    }

    // ── the algorithm ────────────────────────────────────────────────

    private void algorithm() {
        code = new CodeBox(new String[]{
                "for each input symbol c",
                "  if (c is an open parenthesis) stack.push(c)",
                "  else if (c is a close parenthesis) {",
                "    if stack.top contains the matching open parenthesis",
                "      pop the element from stack",
                "    else error",
                "  }",
                "if (stack is empty)",
                "  // all good.",
                "else error"}, -930, -330, 24, 42);
        code.typeIn(5.0);
        stage.addAll(code.parts());
        TextMob src = mono("Source: parentheses.cpp", -620, 130, 24, Colors.MAROON);
        play(new FadeIn(src, d(0.5)));
        stage.add(src);
        play(new FadeIn(code.hl, d(0.4)));
        pause(0.5);

        // the slide's two callouts: find a string for each error
        List<Animation> co = new ArrayList<>();
        List<MObject> made = new ArrayList<>();
        callout(co, made, "Find a string to match", "this error.", 360, code.lineY(5), 440, 100,
                code.lineEndX(5) + 12, code.lineY(5));
        callout(co, made, "Find a string to match", "this error.", 360, code.lineY(9) + 20, 440, 100,
                code.lineEndX(9) + 12, code.lineY(9));
        playAll(co);
        stage.addAll(made);
        pause(2.4);
        List<Animation> gone = new ArrayList<>();
        for (MObject m : made) gone.add(new FadeOut(m, d(0.5)));
        playAll(gone);
        for (MObject m : made) remove(m);
        stage.removeAll(made);

        stack = new VStack(300, 450, 110, 54, 5, "top");
        stack.showBox();
        stack.showTop();
        stage.addAll(stack.parts());

        run("( [ ] [ { } ] )", 1.0);
        run("( [ ) ] { }", 0.9);
        run("( ( ( ) )", 0.9);
        run("} } ) ( { {", 0.7);

        fadeOutAll(d(0.7), stack.box, stack.topArrow, stack.topLab);
        String[] sm = {"Error 1: a closing symbol whose match is", "not on top (or the stack is empty).",
                "Error 2: the input ends but the stack still", "holds open symbols."};
        double[] sy = {120, 165, 240, 285};
        Color[] sc = {Colors.RED, Colors.RED, Colors.ORANGE, Colors.ORANGE};
        for (int i = 0; i < 4; i++) {
            StrokeTextMob t = stroke(sm[i], RX, sy[i], 34, sc[i], false);
            play(new Write(t, d(2.0)));
            stage.add(t);
        }
        pause(3.0);
        fadeOutAll(d(1.2), stage);
        stage.clear();
        unsay();
        pause(0.3);
    }

    private static boolean isOpen(String t) { return t.equals("(") || t.equals("[") || t.equals("{"); }

    private static String match(String c) { return c.equals(")") ? "(" : c.equals("]") ? "[" : "{"; }

    private static Color colorOf(String t) {
        return (t.equals("(") || t.equals(")")) ? Colors.TEAL : (t.equals("[") || t.equals("]")) ? Colors.GOLD : Colors.PINK;
    }

    private void line(int i, double sp) { play(code.moveHl(i, d(0.3 * sp))); }

    private void run(String input, double sp) {
        String[] toks = input.split(" ");
        int n = toks.length;
        Cell[] cell = new Cell[n];
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            cell[i] = tokCell(toks[i], RX + (i - (n - 1) / 2.0) * 66, -270, 56, 64, colorOf(toks[i]), 34);
            cell[i].fadeIn(in, 0.05 * i, d(0.4));
        }
        TextMob inLab = label("input:", 140, -270, 28, Colors.GRAY, false, true);
        in.add(new FadeIn(inLab, d(0.4)));
        playAll(in);
        pause(0.4 * sp);
        line(0, sp);

        boolean ok = true;
        String why = null;
        for (int i = 0; i < n && ok; i++) {
            String t = toks[i];
            List<Animation> hi = new ArrayList<>();
            cell[i].color(hi, Colors.ORANGE, d(0.25 * sp));
            playAll(hi);
            if (isOpen(t)) {
                line(1, sp);
                sayAt(t + " is an open parenthesis: push", Colors.LIGHT_GRAY, RX, -170, 34);
                Cell c = stack.push(t, colorOf(t));
                stage.addAll(c.parts());
                pause(0.25 * sp);
            } else {
                line(2, sp);
                sayAt(t + " is a close parenthesis: is " + match(t) + " on top?", Colors.LIGHT_GRAY, RX, -170, 34);
                pause(0.3 * sp);
                line(3, sp);
                boolean good = !stack.items.isEmpty() && stack.items.get(stack.items.size() - 1).text.getText().equals(match(t));
                if (good) {
                    sayAt("yes: pop it", Colors.GREEN, RX, -170, 34);
                    line(4, sp);
                    Cell p = stack.popAway();
                    stage.addAll(p.parts());
                    pause(0.2 * sp);
                } else {
                    why = stack.items.isEmpty() ? "the stack is empty: nothing to match " + t
                            : "the top is " + stack.items.get(stack.items.size() - 1).text.getText() + ", not " + match(t);
                    sayAt("no: " + why, Colors.RED, RX, -170, 32);
                    line(5, sp);
                    List<Animation> bad = new ArrayList<>();
                    cell[i].color(bad, Colors.RED, d(0.3));
                    playAll(bad);
                    ok = false;
                }
            }
            if (ok) {
                List<Animation> back = new ArrayList<>();
                cell[i].color(back, colorOf(t), d(0.2 * sp));
                playAll(back);
            }
        }
        if (ok) {
            line(7, sp);
            if (stack.items.isEmpty()) {
                sayAt("the stack is empty", Colors.GREEN, RX, -170, 34);
                line(8, sp);
            } else {
                why = "open symbols are still on the stack";
                sayAt("not empty: " + why, Colors.RED, RX, -170, 32);
                line(9, sp);
                ok = false;
            }
        }
        List<MObject> res = chip(ok ? "balanced" : "not balanced", RX, -80, 300, 64, ok ? Colors.GREEN : Colors.RED, 34);
        List<Animation> ra = new ArrayList<>();
        fade(ra, res, d(0.5));
        playAll(ra);
        pause(1.8 * sp);

        // clear for the next string
        List<Animation> out = new ArrayList<>();
        for (Cell c : cell) c.fadeOut(out, d(0.5));
        out.add(new FadeOut(inLab, d(0.5)));
        for (MObject m : res) out.add(new FadeOut(m, d(0.5)));
        while (!stack.items.isEmpty()) {
            Cell c = stack.items.remove(stack.items.size() - 1);
            c.fadeOut(out, d(0.5));
        }
        List<Animation> mt = new ArrayList<>();
        stack.moveTop(mt, d(0.5));
        out.addAll(mt);
        playAll(out);
        for (Cell c : cell) {
            remove(c.box);
            remove(c.text);
        }
        pause(0.3);
    }
}
