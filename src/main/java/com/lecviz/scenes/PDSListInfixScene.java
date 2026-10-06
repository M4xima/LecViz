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
 * Standalone clip for slides 35-36 of the lists deck: infix to postfix.
 *
 *   Slide 35  the task and the table of fully parenthesized infix expressions with their postfix forms
 *             (the prefix column is greyed out, as on the slide)
 *   Slide 36  the algorithm typed in with the slide's three colored blocks (closing parenthesis, operator,
 *             end of input), then run on (1 + (2 * 3)) - 4: operands go straight to the output, opening
 *             parentheses and operators wait on the stack, a closing parenthesis pops back to its opening
 *             one, an operator first pops everything of higher or equal priority, and at the end the stack
 *             is emptied: 1 2 3 * + 4 -
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListInfixScene extends PDSListClipBase {

    private static final double RX = 540, OUT_Y = -110;

    private StrokeTextMob head;
    private CodeBox code;
    private VStack stack;
    private final List<MObject> stage = new ArrayList<>();
    private final List<String> model = new ArrayList<>();
    private int printed = 0;

    @Override
    public void construct() {
        table();
        algorithm();
    }

    // ── slide 35 ─────────────────────────────────────────────────────

    private void table() {
        head = writeHeading("Infix to Postfix");
        pause(0.4);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Given an infix expression (with parentheses),"));
        s.add(ln(3, "convert it to a postfix form (without parentheses)."));
        List<List<MObject>> text = writeSlide(s, -380);
        String[][] rows = {
                {"((1 + 2) * (3 – 4))", "* + 1 2 – 3 4", "1 2 + 3 4 - *"},
                {"(1 + (2 * (3 – 4)))", "+ 1 * 2 – 3 4", "1 2 3 4 - * +"},
                {"((1 + (2 * 3)) – 4)", "- + 1 * 2 3 4", "1 2 3 * + 4 -"},
                {"(((1 + 2) * 3) – 4)", "- * + 1 2 3 4", "1 2 + 3 * 4 -"},
                {"(1 + ((2 * 3) - 4))", "+ 1 - * 2 3 4", "1 2 3 * 4 - +"}};
        Color[][] tint = new Color[5][3];
        for (int r = 0; r < 5; r++) tint[r][2] = Colors.PINK;
        List<MObject> table = dropTable(new String[]{"Infix", "Prefix", "Postfix"}, rows, new double[]{640, 520, 520}, tint, -210);
        // the prefix column is not what we are after: grey it out
        for (int r = 0; r <= 5; r++) table.get(2 * (r * 3 + 1) + 1).setFillColor(Colors.GRAY);
        pause(0.6);
        StrokeTextMob cap = stroke("Each infix expression becomes a postfix form with no parentheses left.", 0, 280, 38, Colors.ORANGE, false);
        play(new Write(cap, d(3.4)));
        pause(2.6);
        List<MObject> all = new ArrayList<>(table);
        all.add(cap);
        for (List<MObject> g : text) all.addAll(g);
        fadeOutAll(d(1.0), all);
        fadeOutAll(d(0.6), head);
        pause(0.3);
    }

    // ── slide 36 ─────────────────────────────────────────────────────

    private void block(int a, int b, Color c) {
        RectMob r = panel(code.centerX(), (code.lineY(a) + code.lineY(b)) / 2, code.width - 20, (b - a + 1) * 34 - 2, c, 0.16);
        r.setStrokeColor(Colors.withAlpha(c, 0.5));
        r.setStrokeWidth(1.5);
        play(new FadeIn(r, d(0.7)));
        stage.add(r);
    }

    private static int prio(String t) {
        return t.equals("*") || t.equals("/") ? 2 : (t.equals("+") || t.equals("–") ? 1 : 0);
    }

    private void line(int i, double sp) { play(code.moveHl(i, d(0.28 * sp))); }

    private void say(String text, Color c) { sayAt(text, c, RX, -265, 32); }

    private void algorithm() {
        code = new CodeBox(new String[]{
                "For each symbol in the expression",
                "  If the symbol is an operand",
                "    Print the symbol",
                "  Else if the symbol is an opening parenthesis",
                "    Push the symbol on stack",
                "  Else if the symbol is a closing parenthesis",
                "    Do {",
                "      Pop symbol from the stack",
                "      If symbol is not opening parenthesis",
                "        Print the symbol",
                "    } while symbol is not opening parenthesis",
                "  Else {         // symbol c is an operator",
                "    Peek symbol d from the stack",
                "    While symbol d has higher or equal priority than c",
                "      Print the symbol d",
                "      Pop symbol d from the stack",
                "    Push the symbol c on stack",
                "  }",
                "While stack is not empty {",
                "  Pop symbol from the stack",
                "  Print the symbol",
                "}",
                "Return postfix"}, -945, -445, 22, 34);
        code.typeIn(6.5);
        stage.addAll(code.parts());
        TextMob src = mono("Source: infix2postfix.cpp", -690, 385, 24, Colors.MAROON);
        play(new FadeIn(src, d(0.5)));
        stage.add(src);
        block(5, 10, Colors.GOLD);
        block(11, 17, Colors.GREEN);
        block(18, 21, Colors.BLUE);
        List<MObject> l1 = chip("closing parenthesis", 130, -445, 290, 50, Colors.GOLD, 24);
        List<MObject> l2 = chip("operator", 400, -445, 190, 50, Colors.GREEN, 24);
        List<MObject> l3 = chip("end of input", 620, -445, 220, 50, Colors.BLUE, 24);
        List<Animation> la = new ArrayList<>();
        fade(la, l1, d(0.5));
        fade(la, l2, d(0.7));
        fade(la, l3, d(0.9));
        playAll(la);
        stage.addAll(l1);
        stage.addAll(l2);
        stage.addAll(l3);
        play(new FadeIn(code.hl, d(0.4)));
        stack = new VStack(230, 470, 60, 56, 5, "top");
        stack.showBox();
        stack.showTop();
        stage.addAll(stack.parts());
        TextMob ol = label("postfix:", 130, OUT_Y, 30, Colors.GRAY, false, true);
        play(new FadeIn(ol, d(0.4)));
        stage.add(ol);
        pause(0.4);
        line(0, 1.0);

        String[] toks = {"(", "1", "+", "(", "2", "*", "3", ")", ")", "–", "4"};
        int n = toks.length;
        Cell[] cell = new Cell[n];
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Color c = toks[i].equals("(") || toks[i].equals(")") ? Colors.GRAY : (Character.isDigit(toks[i].charAt(0)) ? Colors.BLUE : Colors.GOLD);
            cell[i] = tokCell(toks[i], RX + (i - (n - 1) / 2.0) * 66, -350, 58, 62, c, 34);
            cell[i].fadeIn(in, 0.05 * i, d(0.4));
            stage.addAll(cell[i].parts());
        }
        playAll(in);
        pause(0.4);

        for (int i = 0; i < n; i++) {
            String t = toks[i];
            double sp = i < 3 ? 1.0 : 0.85;
            Color back = cell[i].box.getFillColor();
            List<Animation> hi = new ArrayList<>();
            cell[i].color(hi, Colors.ORANGE, d(0.25 * sp));
            playAll(hi);
            if (Character.isDigit(t.charAt(0))) {
                line(1, sp);
                say(t + " is an operand: print it", Colors.LIGHT_GRAY);
                line(2, sp);
                print(t, Colors.BLUE);
            } else if (t.equals("(")) {
                line(3, sp);
                say("( is an opening parenthesis: push it", Colors.LIGHT_GRAY);
                line(4, sp);
                pushSym(t);
            } else if (t.equals(")")) {
                line(5, sp);
                say(") is a closing parenthesis: pop down to the opening one", Colors.LIGHT_GRAY);
                line(6, sp);
                while (true) {
                    line(7, sp);
                    String top = model.get(model.size() - 1);
                    if (!top.equals("(")) {
                        line(8, sp);
                        say("pop " + top + ": not an opening parenthesis, print it", Colors.LIGHT_GRAY);
                        line(9, sp);
                        popPrint();
                        line(10, sp);
                    } else {
                        say("pop ( : it is not printed, and the loop stops", Colors.GREEN);
                        model.remove(model.size() - 1);
                        Cell gone = stack.popAway();
                        stage.addAll(gone.parts());
                        line(10, sp);
                        break;
                    }
                }
            } else {
                line(11, sp);
                say(t + " is an operator", Colors.LIGHT_GRAY);
                line(12, sp);
                line(13, sp);
                boolean any = false;
                while (!model.isEmpty() && prio(model.get(model.size() - 1)) >= prio(t)) {
                    String d = model.get(model.size() - 1);
                    say("d = " + d + " has higher or equal priority than " + t + ": print it", Colors.LIGHT_GRAY);
                    line(14, sp);
                    popPrint();
                    line(15, sp);
                    any = true;
                    line(13, sp);
                }
                if (!any) {
                    say(model.isEmpty() ? "the stack is empty: nothing to pop"
                            : "d = " + model.get(model.size() - 1) + " has lower priority than " + t + ": nothing to pop", Colors.LIGHT_GRAY);
                    pause(0.5 * sp);
                }
                line(16, sp);
                say("push " + t, Colors.LIGHT_GRAY);
                pushSym(t);
            }
            List<Animation> done = new ArrayList<>();
            cell[i].color(done, Colors.GRAY, d(0.2 * sp));
            playAll(done);
            pause(0.2 * sp);
        }

        line(18, 0.8);
        say("the symbols are used up: empty the stack", Colors.GREEN);
        while (!model.isEmpty()) {
            line(19, 0.8);
            line(20, 0.8);
            popPrint();
            line(18, 0.8);
        }
        line(22, 0.8);
        sayAt("Return postfix:  1 2 3 * + 4 -", Colors.GREEN, RX, -265, 38);
        pause(3.0);
        fadeOutAll(d(1.2), stage);
        stage.clear();
        unsay();
        pause(0.4);
    }

    private double outX() { return 330 + printed * 64; }

    private void print(String t, Color c) {
        Cell o = tokCell(t, outX(), OUT_Y, 60, 56, c, 32);
        List<Animation> a = new ArrayList<>();
        o.fadeIn(a, 0, d(0.5));
        playAll(a);
        stage.addAll(o.parts());
        printed++;
    }

    private void pushSym(String t) {
        Cell c = stack.push(t, t.equals("(") ? Colors.GRAY : Colors.GOLD);
        model.add(t);
        stage.addAll(c.parts());
    }

    private void popPrint() {
        model.remove(model.size() - 1);
        Cell c = stack.popTo(outX(), OUT_Y);
        stage.addAll(c.parts());
        printed++;
        pause(0.2);
    }
}
