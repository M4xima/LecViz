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
 * Standalone clip for slide 26 of the trees deck: evaluating a postfix expression with a stack.
 *
 *   - the slide's two points, then the algorithm typed in as pseudo-code with a highlight bar
 *   - the expression 5 1 2 3 * - 4 + 6 * - as a row of tokens with a pointer walking over it; operands are
 *     pushed on the stack; an operator pops two values (the right operand comes off first), applies itself and
 *     pushes the result; the stack and the pseudo-code move together
 *   - the answer is 11
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeEvalScene extends PDSTreeClipBase {

    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();
    private CodeBox code;
    private VStack vs;
    private final Color[] pal = {Colors.TEAL, Colors.BLUE, Colors.PINK, Colors.GOLD, Colors.PURPLE, Colors.GREEN};
    private int made;

    @Override
    public void construct() {
        head = writeHeading("Evaluating postfix");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Find the value of 5 1 2 3 * - 4 + 6 * -.").kw("5 1 2 3 * - 4 + 6 * -", Colors.BLUE));
        s.add(ln(0, "Write a program to evaluate a postfix expression."));
        s.add(ln(1, "Assume digits, +, -, *, /."));
        List<List<MObject>> text = writeSlide(s, -250);
        pause(1.4);
        swipeAway(text);
        pause(0.4);

        code = new CodeBox(new String[]{
                "For each symbol in the expression",
                "  If the symbol is an operand",
                "    Push its value to a stack",
                "  Else if the symbol is an operator",
                "    Pop two nodes from the stack",
                "    Apply the operator on them",
                "    Push result to the stack"}, -930, -260, 24, 38);
        code.typeIn(4.0);
        mine.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.3)));
        TextMob src = label("Source: postfixeval.cpp", 740, -470, 26, Colors.GRAY, false, false);
        play(new FadeIn(src, d(0.6)));
        mine.add(src);

        String[] toks = {"5", "1", "2", "3", "*", "-", "4", "+", "6", "*", "-"};
        List<Cell> cells = new ArrayList<>();
        List<Animation> ta = new ArrayList<>();
        for (int i = 0; i < toks.length; i++) {
            boolean op = !Character.isDigit(toks[i].charAt(0));
            Cell c = tokCell(toks[i], -500 + i * 100, -370, 84, 66, op ? Colors.ORANGE : Colors.BLUE, 34);
            c.fadeIn(ta, 0.1 * i, d(0.4));
            cells.add(c);
            mine.addAll(c.parts());
        }
        playAll(ta);
        vs = new VStack(-100, 340, 200, 56, 5, "top", 1);
        vs.showBox();
        vs.showTop();
        mine.addAll(vs.parts());
        TextMob log = label("", 0, 0, 1, Colors.WHITE, false, false);
        pause(0.6);

        Ptr ptr = pointer("", -500, -325, false, Colors.GOLD);
        mine.addAll(ptr.parts());
        play(new FadeIn(ptr.arrow, d(0.4)));
        List<Integer> st = new ArrayList<>();
        for (int i = 0; i < toks.length; i++) {
            List<Animation> mv = new ArrayList<>();
            ptr.go(mv, -500 + i * 100, d(0.4));
            cells.get(i).color(mv, Colors.GOLD, d(0.3));
            playAll(mv);
            play(code.moveHl(0, d(0.25)));
            String tk = toks[i];
            if (Character.isDigit(tk.charAt(0))) {
                play(code.moveHl(1, d(0.25)));
                play(code.moveHl(2, d(0.25)));
                int v = tk.charAt(0) - '0';
                push(v);
                st.add(v);
                if (i == 0) sayAt("An operand: push its value.", Colors.LIGHT_GRAY, 0, 440, 34);
            } else {
                play(code.moveHl(3, d(0.25)));
                play(code.moveHl(4, d(0.3)));
                Cell cb = vs.popTo(330, -170 + 0);
                int b = st.remove(st.size() - 1);
                Cell ca = vs.popTo(330, -100);
                int a = st.remove(st.size() - 1);
                TextMob lb = label("b", 330 + 70, -170, 28, Colors.GRAY, false, true);
                TextMob la = label("a", 330 + 70, -100, 28, Colors.GRAY, false, true);
                play(new FadeIn(lb, d(0.3)), new FadeIn(la, d(0.3)));
                mine.add(lb);
                mine.add(la);
                if (i == 4) sayAt("An operator: pop two. The right operand (b) comes off the stack first.", Colors.ORANGE, 0, 440, 34);
                play(code.moveHl(5, d(0.3)));
                int r = apply(tk.charAt(0), a, b);
                TextMob eq = label(a + " " + tk + " " + b + " = " + r, 330, -20, 40, Colors.GOLD, false, true);
                play(new FadeIn(eq, d(0.5)));
                pause(0.7);
                List<Animation> f = new ArrayList<>();
                ca.fadeOut(f, d(0.4));
                cb.fadeOut(f, d(0.4));
                f.add(new FadeOut(la, d(0.4)));
                f.add(new FadeOut(lb, d(0.4)));
                f.add(new FadeOut(eq, d(0.4)));
                playAll(f);
                play(code.moveHl(6, d(0.3)));
                push(r);
                st.add(r);
            }
            List<Animation> dim = new ArrayList<>();
            cells.get(i).color(dim, Colors.GRAY, d(0.3));
            playAll(dim);
            pause(i < 5 ? 0.5 : 0.2);
        }
        play(code.moveHl(0, d(0.3)));
        sayAt("The expression is used up and one value is left: the answer.", Colors.GREEN, 0, 440, 34);
        List<MObject> ans = chip("value = " + st.get(0), 330, 130, 380, 80, Colors.GREEN, 42);
        List<Animation> aa = new ArrayList<>();
        for (MObject m : ans) aa.add(new DropIn(m, 40, 0, d(0.6)));
        playAll(aa);
        mine.addAll(ans);
        pause(3.4);
        unsay();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void push(int v) {
        vs.push(String.valueOf(v), pal[made++ % pal.length]);
        mine.addAll(vs.parts());
    }

    private int apply(char op, int a, int b) {
        return switch (op) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            default -> a / b;
        };
    }
}
