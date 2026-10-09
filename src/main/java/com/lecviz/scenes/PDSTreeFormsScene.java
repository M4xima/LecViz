package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Standalone clip for slides 24-25 of the trees deck: infix, prefix and postfix.
 *
 *   Slide 24  the table drops in with only the infix column filled; for each row the expression tree is built and
 *             walked twice: preorder writes the prefix form, postorder writes the postfix form, letter by letter
 *   Slide 25  the filled table, then the two observations: the operands A, B, C, D keep their order in every form;
 *             and the operators of a prefix form come in the opposite order to those of its postfix form (every
 *             operator keeps one color so the reversal can be seen)
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeFormsScene extends PDSTreeClipBase {

    private static final String[] INFIX = {"A + B * C + D", "(A + B) * (C + D)", "A * B + C * D", "A + B + C + D", "A * B * C + D"};
    private static final String[] PREFIX = {"++A*BCD", "*+AB+CD", "+*AB*CD", "+++ABCD", "+**ABCD"};
    private static final String[] POSTFIX = {"ABC*+D+", "AB+CD+*", "AB*CD*+", "AB+C+D+", "AB*C*D+"};
    private static final double[] COLW = {540, 520, 520};
    private static final double HEAD_Y = -390, ROW = 62;

    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();
    private final List<List<TextMob>> preChars = new ArrayList<>(), postChars = new ArrayList<>();
    private final List<List<GN>> preNodes = new ArrayList<>(), postNodes = new ArrayList<>();
    private double cw;

    @Override
    public void construct() {
        head = writeHeading("Infix, Prefix, Postfix");
        pause(0.5);
        cw = measure("MMMMMMMMMM", MONO, 32, false) / 10.0;
        String[][] rows = new String[5][3];
        Color[][] tint = new Color[5][3];
        for (int r = 0; r < 5; r++) {
            rows[r][0] = INFIX[r];
            rows[r][1] = "";
            rows[r][2] = "";
        }
        mine.addAll(dropTable(new String[]{"Infix", "Prefix", "Postfix"}, rows, COLW, tint, HEAD_Y));
        pause(1.0);
        sayAt("Write each expression as prefix and as postfix. Build its tree, then walk it twice.", Colors.LIGHT_GRAY, 0, 480, 34);
        pause(1.6);
        for (int r = 0; r < 5; r++) fillRow(r);
        unsay();
        pause(1.0);
        observations();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private double cellLeft(int c) {
        double total = 12;
        for (double w : COLW) total += w;
        double x = -total / 2;
        for (int i = 0; i < c; i++) x += COLW[i] + 6;
        return x;
    }

    private double rowY(int r) { return HEAD_Y + 68 * (r + 1); }

    private void fillRow(int r) {
        RectMob band = new RectMob(1592, 66).setCornerRadius(10);
        band.setFillColor(Colors.withAlpha(Colors.GOLD, 0.1));
        band.setStrokeColor(Colors.withAlpha(Colors.GOLD, 0.8));
        band.setStrokeWidth(2.4);
        band.setPosition(0, rowY(r));
        band.setOpacity(0);
        add(band);
        play(new FadeIn(band, d(0.4)));
        GN root = parseInfix(INFIX[r]);
        GT t = new GT(root, 0, 90, 66, 44, 22, 26, false, n -> n.leaf() ? Colors.BLUE : Colors.GOLD, true);
        t.build(r == 0 ? 0.3 : 0.12);
        double speed = r == 0 ? 0.4 : 0.2;
        // preorder writes the prefix form
        List<GN> pre = new ArrayList<>(), post = new ArrayList<>();
        preorderInto(root, pre);
        postorderInto(root, post);
        sayAt("Preorder (node first) gives the prefix form.", Colors.GREEN, 0, 480, 32);
        List<TextMob> pc = writeForm(t, pre, 1, r, speed);
        preChars.add(pc);
        preNodes.add(pre);
        sayAt("Postorder (node last) gives the postfix form.", Colors.ORANGE, 0, 480, 32);
        List<TextMob> qc = writeForm(t, post, 2, r, speed);
        postChars.add(qc);
        postNodes.add(post);
        pause(r == 0 ? 1.2 : 0.4);
        List<MObject> gone = new ArrayList<>(t.parts());
        gone.add(band);
        List<Animation> out = new ArrayList<>();
        for (MObject m : gone) out.add(new FadeOut(m, d(0.5)));
        playAll(out);
        for (MObject m : gone) remove(m);
    }

    private List<TextMob> writeForm(GT t, List<GN> order, int col, int r, double speed) {
        List<TextMob> chars = new ArrayList<>();
        for (int i = 0; i < order.size(); i++) {
            GN n = order.get(i);
            List<Animation> a = new ArrayList<>();
            t.paint(a, n, VISIT_C, d(0.2));
            TextMob ch = mono(n.name, cellLeft(col) + 40 + i * cw * 1.25, rowY(r), 32, Colors.WHITE);
            a.add(new FadeIn(ch, d(0.25)));
            playAll(a);
            chars.add(ch);
            mine.add(ch);
            pause(speed);
            List<Animation> b = new ArrayList<>();
            t.paint(b, n, n.leaf() ? Colors.BLUE : Colors.GOLD, d(0.2));
            playAll(b);
        }
        return chars;
    }

    // ── slide 25 ─────────────────────────────────────────────────────

    private void observations() {
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "The order of operands (A, B, C, D) remains the").kw("operands", Colors.GOLD));
        s.add(ln(3, "same in all the expressions."));
        s.add(ln(0, "Operators in prefix are in the opposite order").kw("opposite order", Colors.PINK));
        s.add(ln(3, "compared to their postfix versions."));
        List<List<MObject>> text = writeSlide(s.subList(0, 2), 110);
        // the operands: gold in every form
        List<MObject> bands = new ArrayList<>();
        List<Animation> a = new ArrayList<>();
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 2; c++) {
                List<TextMob> cs = c == 0 ? preChars.get(r) : postChars.get(r);
                List<GN> ns = c == 0 ? preNodes.get(r) : postNodes.get(r);
                for (int i = 0; i < cs.size(); i++)
                    if (ns.get(i).leaf()) a.add(new ColorChange(cs.get(i), Colors.GOLD, d(0.7)));
                    else a.add(new ColorChange(cs.get(i), Colors.withAlpha(Colors.LIGHT_GRAY, 0.45), d(0.7)));
            }
        }
        playAll(a);
        TextMob abcd = label("A  B  C  D", 0, 330, 56, Colors.GOLD, false, true);
        TextMob lab = label("same order in the infix, the prefix and the postfix form", 0, 400, 30, Colors.LIGHT_GRAY, false, false);
        play(new FadeIn(abcd, d(0.7)), new FadeIn(lab, d(0.7)));
        mine.add(abcd);
        mine.add(lab);
        pause(3.0);
        play(new FadeOut(abcd, d(0.5)), new FadeOut(lab, d(0.5)));
        swipeAway(text);
        pause(0.4);
        List<List<MObject>> text2 = writeSlide(s.subList(2, 4), 110);
        // the operators: each operator keeps one color in the prefix and the postfix form
        Color[] pal = {Colors.TEAL, Colors.ORANGE, Colors.PINK, Colors.GREEN, Colors.PURPLE};
        List<Animation> b = new ArrayList<>();
        for (int r = 0; r < 5; r++) {
            Map<GN, Color> map = new HashMap<>();
            int k = 0;
            for (GN n : preNodes.get(r)) if (!n.leaf()) map.put(n, pal[k++ % pal.length]);
            for (int c = 0; c < 2; c++) {
                List<TextMob> cs = c == 0 ? preChars.get(r) : postChars.get(r);
                List<GN> ns = c == 0 ? preNodes.get(r) : postNodes.get(r);
                for (int i = 0; i < cs.size(); i++) {
                    GN n = ns.get(i);
                    if (n.leaf()) b.add(new ColorChange(cs.get(i), Colors.withAlpha(Colors.LIGHT_GRAY, 0.45), d(0.7)));
                    else b.add(new ColorChange(cs.get(i), map.get(n), d(0.7)));
                }
            }
        }
        playAll(b);
        TextMob ex1 = label("row 1:   prefix + + *    postfix * + +", 0, 380, 36, Colors.WHITE, false, true);
        play(new FadeIn(ex1, d(0.7)));
        mine.add(ex1);
        pause(2.0);
        TextMob ex2 = label("the same operators, read in the opposite order", 0, 440, 30, Colors.LIGHT_GRAY, false, false);
        play(new FadeIn(ex2, d(0.7)));
        mine.add(ex2);
        pause(3.2);
        mine.addAll(text2.get(0));
        mine.addAll(text2.get(1));
    }
}
