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
 * Standalone clip for slide 37 of the lists deck: recursive formulations of infix, postfix and prefix.
 *
 *   - the base and inductive rules one line at a time
 *   - then all three forms are built from the same three operands, base first: each operand is a
 *     one-token expression; "+" joins 1 and 2 into a larger expression (a teal box around it, whose place
 *     in the row depends on the form); "*" joins that box and 3 into the whole expression (a gold box)
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListRecursiveFormScene extends PDSListClipBase {

    private StrokeTextMob head;
    private StrokeTextMob cap;

    @Override
    public void construct() {
        head = writeHeading("Recursive Formulations");
        pause(0.4);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Infix"));
        s.add(ln(1, "Base: Each operand is an infix expression."));
        s.add(ln(1, "Inductive: infix op infix is an infix expression.").kw("infix op infix", Colors.BLUE));
        s.add(ln(0, "Postfix"));
        s.add(ln(1, "Base: Each operand is a postfix expression."));
        s.add(ln(1, "Inductive: postfix postfix op").kw("postfix postfix op", Colors.BLUE));
        s.add(ln(0, "Prefix"));
        s.add(ln(1, "Base: Each operand is a prefix expression."));
        s.add(ln(1, "Inductive: op prefix prefix").kw("op prefix prefix", Colors.BLUE));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(1.4);
        swipeAway(text);
        pause(0.4);

        List<MObject> mine = new ArrayList<>();
        mine.add(head);
        double[] rowY = {-230, 0, 230};
        String[] names = {"infix", "postfix", "prefix"};
        String[] rules = {"infix op infix", "postfix postfix op", "op prefix prefix"};
        Color[] fc = {Colors.TEAL, Colors.PINK, Colors.GOLD};
        // slot layout per form: tokens of ((1 + 2) * 3)  /  (1 2 + 3 *)  /  (* + 1 2 3)
        String[][] tok = {{"1", "+", "2", "*", "3"}, {"1", "2", "+", "3", "*"}, {"*", "+", "1", "2", "3"}};
        int[][] innerSpan = {{0, 2}, {0, 2}, {1, 3}};
        double cx = 330, pitch = 130;
        List<Cell[]> cells = new ArrayList<>();
        List<Animation> labels = new ArrayList<>();
        for (int f = 0; f < 3; f++) {
            TextMob nm = label(names[f], -640, rowY[f] - 18, 46, fc[f], false, true);
            TextMob rl = mono(rules[f], -640, rowY[f] + 34, 26, Colors.LIGHT_GRAY);
            labels.add(new FadeIn(nm, d(0.6)));
            labels.add(new FadeIn(rl, d(0.6)));
            mine.add(nm);
            mine.add(rl);
        }
        playAll(labels);
        caption("Base: each operand is an expression by itself.", Colors.WHITE);
        double[][] boxes = new double[3][2];
        for (int f = 0; f < 3; f++) {
            Cell[] row = new Cell[5];
            for (int i = 0; i < 5; i++) {
                boolean op = tok[f][i].equals("+") || tok[f][i].equals("*");
                row[i] = tokCell(tok[f][i], cx + (i - 2) * pitch, rowY[f], 92, 82, op ? Colors.GOLD : Colors.BLUE, 42);
                mine.addAll(row[i].parts());
            }
            cells.add(row);
        }
        // the operands appear first
        List<Animation> ops = new ArrayList<>();
        for (int f = 0; f < 3; f++)
            for (int i = 0; i < 5; i++)
                if (!(tok[f][i].equals("+") || tok[f][i].equals("*"))) cells.get(f)[i].fadeIn(ops, 0.12 * i, d(0.5));
        playAll(ops);
        pause(1.2);

        // + joins the first two operands into a bigger expression
        caption("Inductive: an operator joins two expressions into a bigger one.", Colors.WHITE);
        List<Animation> plus = new ArrayList<>();
        List<MObject> innerBoxes = new ArrayList<>();
        for (int f = 0; f < 3; f++) {
            int plusIdx = f == 0 ? 1 : (f == 1 ? 2 : 1);
            cells.get(f)[plusIdx].fadeIn(plus, 0.1 * f, d(0.6));
            double a = cx + (innerSpan[f][0] - 2) * pitch, b = cx + (innerSpan[f][1] - 2) * pitch;
            RectMob inner = panel((a + b) / 2, rowY[f], b - a + 120, 118, Colors.TEAL, 0.14);
            plus.add(new FadeInAt(inner, 0.3 + 0.1 * f, d(0.7)));
            innerBoxes.add(inner);
            mine.add(inner);
        }
        playAll(plus);
        pause(1.6);

        // * joins that expression and 3
        List<Animation> star = new ArrayList<>();
        for (int f = 0; f < 3; f++) {
            int starIdx = f == 0 ? 3 : (f == 1 ? 4 : 0);
            cells.get(f)[starIdx].fadeIn(star, 0.1 * f, d(0.6));
            double a = cx - 2 * pitch, b = cx + 2 * pitch;
            RectMob outer = panel((a + b) / 2, rowY[f], b - a + 170, 150, Colors.GOLD, 0.1);
            star.add(new FadeInAt(outer, 0.3 + 0.1 * f, d(0.7)));
            mine.add(outer);
        }
        playAll(star);
        pause(1.2);
        caption("Same recursion, three forms: only the place of the operator changes.", Colors.GREEN);
        pause(3.0);
        dropCaption();
        fadeOutAll(d(1.2), mine);
        pause(0.4);
    }

    private void caption(String text, Color c) {
        StrokeTextMob n = stroke(text, 0, -385, 38, c, false);
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
}
