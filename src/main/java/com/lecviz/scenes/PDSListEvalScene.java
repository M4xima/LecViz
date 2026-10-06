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
 * Standalone clip for slides 33-34 of the lists deck: postfix evaluation and prefix evaluation.
 *
 *   Slide 33  the question and the task one line at a time; then the slide's algorithm is typed in and
 *             run on 5 1 2 3 * - 4 + 6 * -: the symbols are read left to right, operands are pushed and
 *             every operator pops two values, applies itself to them and pushes the result (value 11)
 *   Slide 34  the same algorithm read right to left for prefix forms: * + 1 2 - 3 4 is evaluated step by
 *             step, and the values of the slide's five prefix expressions fill the table beside it
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListEvalScene extends PDSListClipBase {

    private static final double CALC_Y = 40;
    private double tx = 380;
    private double narrX = 380;

    private StrokeTextMob head;
    private CodeBox code;
    private VStack stack;
    private final List<MObject> stage = new ArrayList<>();

    @Override
    public void construct() {
        postfix();
        prefix();
    }

    private static boolean isOp(String t) { return t.equals("+") || t.equals("–") || t.equals("*") || t.equals("/"); }

    private static int apply(String op, int a, int b) {
        return switch (op) {
            case "+" -> a + b;
            case "–" -> a - b;
            case "*" -> a * b;
            default -> a / b;
        };
    }

    private void line(int i, double sp) { play(code.moveHl(i, d(0.3 * sp))); }

    // ── the generic run ──────────────────────────────────────────────

    /**
     * Evaluates {@code toks} with the stack. For postfix the symbols are read left to right and the first value
     * popped is the right operand; for prefix they are read right to left and the first value popped is the left one.
     */
    private int eval(String[] toks, boolean prefix, double sp, int firstOpLine) {
        int n = toks.length;
        Cell[] cell = new Cell[n];
        List<Animation> in = new ArrayList<>();
        double pitch = n > 8 ? 72 : 78;
        for (int i = 0; i < n; i++) {
            Color c = isOp(toks[i]) ? Colors.GOLD : Colors.BLUE;
            cell[i] = tokCell(toks[i], tx + (i - (n - 1) / 2.0) * pitch, -300, 62, 66, c, 36);
            cell[i].fadeIn(in, 0.05 * i, d(0.4));
        }
        playAll(in);
        stage.addAll(allParts(cell));
        pause(0.4 * sp);

        int[] order = new int[n];
        for (int i = 0; i < n; i++) order[i] = prefix ? n - 1 - i : i;
        int result = 0;
        for (int k = 0; k < n; k++) {
            int i = order[k];
            String t = toks[i];
            List<Animation> hi = new ArrayList<>();
            cell[i].color(hi, Colors.ORANGE, d(0.25 * sp));
            playAll(hi);
            if (!isOp(t)) {
                line(1, sp);
                sayAt(t + " is an operand: push it", Colors.LIGHT_GRAY, narrX, -190, 34);
                pause(0.15 * sp);
                line(2, sp);
                Cell c = stack.push(t, Colors.BLUE);
                stage.addAll(c.parts());
                pause(0.2 * sp);
            } else {
                line(firstOpLine, sp);
                sayAt(t + " is an operator: pop two values", Colors.LIGHT_GRAY, narrX, -190, 34);
                pause(0.2 * sp);
                line(firstOpLine + 1, sp);
                // the first popped value goes to the right for postfix, to the left for prefix
                double leftX = tx - 150, rightX = tx + 150;
                Cell first = stack.popTo(prefix ? leftX : rightX, CALC_Y);
                Cell second = stack.popTo(prefix ? rightX : leftX, CALC_Y);
                stage.addAll(first.parts());
                stage.addAll(second.parts());
                int a = Integer.parseInt(prefix ? first.text.getText() : second.text.getText());
                int b = Integer.parseInt(prefix ? second.text.getText() : first.text.getText());
                TextMob opT = label(t, tx, CALC_Y, 48, Colors.GOLD, false, true);
                play(new FadeIn(opT, d(0.3 * sp)));
                stage.add(opT);
                line(firstOpLine + 2, sp);
                int r = apply(t, a, b);
                String res = r < 0 ? "–" + (-r) : String.valueOf(r);
                String eq = (a < 0 ? "–" + (-a) : String.valueOf(a)) + " " + t + " "
                        + (b < 0 ? "–" + (-b) : String.valueOf(b)) + " = " + res;
                TextMob eqT = mono(eq, tx, CALC_Y + 90, 44, Colors.GREEN);
                play(new FadeIn(eqT, d(0.4 * sp)));
                stage.add(eqT);
                pause(0.5 * sp);
                line(firstOpLine + 3, sp);
                List<Animation> clr = new ArrayList<>();
                first.fadeOut(clr, d(0.4 * sp));
                second.fadeOut(clr, d(0.4 * sp));
                clr.add(new FadeOut(opT, d(0.4 * sp)));
                clr.add(new FadeOut(eqT, d(0.4 * sp)));
                playAll(clr);
                Cell c = stack.push(String.valueOf(r), Colors.GREEN);
                c.text.setFontSize(32);
                stage.addAll(c.parts());
                if (k == n - 1) result = r;
                pause(0.3 * sp);
                List<Animation> calm = new ArrayList<>();
                c.color(calm, Colors.BLUE, d(0.3));
                playAll(calm);
            }
            List<Animation> done = new ArrayList<>();
            cell[i].color(done, Colors.GRAY, d(0.2 * sp));
            playAll(done);
        }
        return result;
    }

    private List<MObject> allParts(Cell[] cells) {
        List<MObject> l = new ArrayList<>();
        for (Cell c : cells) l.addAll(c.parts());
        return l;
    }

    // ── slide 33 ─────────────────────────────────────────────────────

    private void postfix() {
        head = writeHeading("Postfix Evaluation");
        pause(0.4);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Find the value of 5 1 2 3 * – 4 + 6 * –.").kw("5 1 2 3 * – 4 + 6 * –", Colors.BLUE));
        s.add(ln(0, "Write a program to evaluate a postfix expression."));
        s.add(ln(1, "Assume digits, +, –, *, /."));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(1.2);
        swipeAway(text);
        pause(0.4);

        code = new CodeBox(new String[]{
                "For each symbol in the expression",
                "  If the symbol is an operand",
                "    Push its value to a stack",
                "  Else if the symbol is an operator",
                "    Pop two nodes from the stack",
                "    Apply the operator on them",
                "    Push result to the stack"}, -930, -330, 26, 46);
        code.typeIn(4.0);
        stage.addAll(code.parts());
        TextMob src = mono("Source: postfixeval.cpp", -620, 20, 24, Colors.MAROON);
        play(new FadeIn(src, d(0.5)));
        stage.add(src);
        play(new FadeIn(code.hl, d(0.4)));
        stack = new VStack(-300, 450, 130, 58, 6, "top");
        stack.showBox();
        stack.showTop();
        stage.addAll(stack.parts());
        pause(0.4);
        line(0, 1.0);

        String[] toks = {"5", "1", "2", "3", "*", "–", "4", "+", "6", "*", "–"};
        int v = eval(toks, false, 0.85, 3);
        sayAt("the symbols are used up: the value is on the stack", Colors.GREEN, narrX, -190, 34);
        List<MObject> res = chip("value = " + v, tx, 270, 360, 90, Colors.GREEN, 50);
        List<Animation> ra = new ArrayList<>();
        fade(ra, res, d(0.7));
        playAll(ra);
        stage.addAll(res);
        pause(2.6);
        fadeOutAll(d(1.0), stage);
        stage.clear();
        unsay();
        fadeOutAll(d(0.6), head);
        pause(0.3);
    }

    // ── slide 34 ─────────────────────────────────────────────────────

    private void prefix() {
        head = writeHeading("Prefix Evaluation");
        narrX = 290;
        tx = 300;
        pause(0.4);
        code = new CodeBox(new String[]{
                "For each symbol in the expression               ",
                "  If the symbol is an operand",
                "    Push its value to the stack",
                "  Else if the symbol is an operator",
                "    Pop two symbols from the stack",
                "    Apply the operator on them",
                "    Push result to the stack"}, -930, -330, 26, 46);
        code.typeIn(4.0);
        stage.addAll(code.parts());
        TextMob rl = monoLeft("right-to-left", code.lineEndX(0) + 10, code.lineY(0), 26, Colors.RED);
        play(new FadeIn(rl, d(0.6)));
        stage.add(rl);
        play(new FadeIn(code.hl, d(0.4)));
        stack = new VStack(-300, 450, 130, 58, 6, "top");
        stack.showBox();
        stack.showTop();
        stage.addAll(stack.parts());

        // the slide's five prefix expressions with their values
        String[] pre = {"* + 1 2 – 3 4", "+ 1 * 2 – 3 4", "- + 1 * 2 3 4", "- * + 1 2 3 4", "+ 1 - * 2 3 4"};
        String[] val = {"–3", "–1", "3", "5", "3"};
        TextMob th = label("Prefix", 725, -255, 30, Colors.WHITE, false, true);
        TextMob vh = label("value", 865, -255, 30, Colors.WHITE, false, true);
        RectMob hb = panel(725, -255, 240, 52, Colors.BLUE, 0.35);
        RectMob hv = panel(865, -255, 90, 52, Colors.BLUE, 0.35);
        List<Animation> ta = new ArrayList<>();
        for (MObject m : new MObject[]{hb, hv, th, vh}) ta.add(new FadeIn(m, d(0.5)));
        TextMob[] valT = new TextMob[5];
        RectMob[] rowB = new RectMob[5];
        for (int r = 0; r < 5; r++) {
            double y = -255 + 58 * (r + 1);
            rowB[r] = panel(725, y, 240, 52, Colors.GOLD, 0.2);
            RectMob vb = panel(865, y, 90, 52, Colors.WHITE, 0.06);
            TextMob pt = mono(pre[r], 725, y, 22, Colors.WHITE);
            valT[r] = label("", 865, y, 30, Colors.GREEN, false, true);
            ta.add(new FadeInAt(rowB[r], 0.12 * r, d(0.5)));
            ta.add(new FadeInAt(vb, 0.12 * r, d(0.5)));
            ta.add(new FadeInAt(pt, 0.12 * r, d(0.5)));
            stage.add(rowB[r]);
            stage.add(vb);
            stage.add(pt);
            valT[r].setOpacity(0);
        }
        playAll(ta);
        stage.add(hb);
        stage.add(hv);
        stage.add(th);
        stage.add(vh);
        TextMob hw = mono("Homework: code this up.", -620, 20, 24, Colors.MAROON);
        play(new FadeIn(hw, d(0.5)));
        stage.add(hw);
        pause(0.6);

        // the first expression, step by step
        List<Animation> pick = new ArrayList<>();
        pick.add(new ColorChange(rowB[0], Colors.withAlpha(Colors.ORANGE, 0.45), d(0.3)));
        playAll(pick);
        line(0, 1.0);
        String[] toks = pre[0].split(" ");
        for (int i = 0; i < toks.length; i++) if (toks[i].equals("-")) toks[i] = "–";
        int v = eval(toks, true, 0.9, 3);
        valT[0].setText(v < 0 ? "–" + (-v) : String.valueOf(v));
        play(new FadeIn(valT[0], d(0.5)));
        stage.add(valT[0]);
        sayAt("right to left: operands wait on the stack", Colors.GREEN, narrX - 30, -190, 28);
        pause(2.0);

        // the other four: the same algorithm gives their values
        for (int r = 1; r < 5; r++) {
            List<Animation> sel = new ArrayList<>();
            sel.add(new ColorChange(rowB[r], Colors.withAlpha(Colors.ORANGE, 0.45), d(0.25)));
            playAll(sel);
            pause(0.35);
            valT[r].setText(val[r]);
            play(new FadeIn(valT[r], d(0.4)));
            stage.add(valT[r]);
            List<Animation> back = new ArrayList<>();
            back.add(new ColorChange(rowB[r], Colors.withAlpha(Colors.GOLD, 0.2), d(0.25)));
            playAll(back);
            pause(0.2);
        }
        pause(2.4);
        fadeOutAll(d(1.2), stage);
        stage.clear();
        unsay();
        fadeOutAll(d(0.8), head);
        pause(0.4);
    }
}
