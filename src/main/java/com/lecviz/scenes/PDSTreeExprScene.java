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
 * Standalone clip for slide 21 of the trees deck: expression trees.
 *
 *   - the expression (a + b * c) + ((d * e + f) * g) is written, then its tree builds level by level
 *   - every pair of parentheses in the text matches one subtree: a colored band behind the parenthesized text
 *     and a box around the subtree light up together
 *   - the parentheses fade out of the text: the tree needs none, its shape already says what is grouped, which
 *     leads to the slide's question: can the expression itself be written without parentheses?
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeExprScene extends PDSTreeClipBase {

    private static final String EXPR = "(a + b * c) + ((d * e + f) * g)";

    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();

    @Override
    public void construct() {
        head = writeHeading("Expression Trees");
        pause(0.5);
        double size = 44, cw = measure("MMMMMMMMMM", MONO, size, false) / 10.0;
        double left = -EXPR.length() * cw / 2;
        List<MObject> chars = new ArrayList<>();
        List<Animation> ia = new ArrayList<>();
        for (int i = 0; i < EXPR.length(); i++) {
            String ch = String.valueOf(EXPR.charAt(i));
            if (ch.equals(" ")) { chars.add(null); continue; }
            TextMob c = mono(ch, left + (i + 0.5) * cw, -390, size, "()".contains(ch) ? Colors.GOLD : Colors.WHITE);
            ia.add(new FadeInAt(c, 0.04 * i, d(0.4)));
            chars.add(c);
        }
        playAll(ia);
        for (MObject m : chars) if (m != null) mine.add(m);
        pause(0.6);

        GN root = parseInfix(EXPR);
        GT t = new GT(root, 0, -270, 100, 40, 26, 28, false, n -> n.leaf() ? Colors.BLUE : Colors.GOLD, true);
        t.build(0.4);
        mine.addAll(t.parts());
        pause(0.8);

        // each pair of parentheses is one subtree
        GN[] sub = {leftOf(root), rightOf(root), leftOf(rightOf(root))};
        String[] txt = {"(a + b * c)", "((d * e + f) * g)", "(d * e + f)"};
        Color[] col = {Colors.TEAL, Colors.ORANGE, Colors.PINK};
        sayAt("Each pair of parentheses groups one subtree.", Colors.LIGHT_GRAY, 0, 390, 34);
        for (int i = 0; i < 3; i++) {
            int at = EXPR.indexOf(txt[i]);
            double w = txt[i].length() * cw;
            RectMob band = new RectMob(w + 10, size * 1.3).setCornerRadius(10);
            band.setFillColor(Colors.withAlpha(col[i], 0.32));
            band.setStrokeColor(Colors.withAlpha(col[i], 0.9));
            band.setStrokeWidth(2);
            band.setOpacity(0);
            band.setPosition(left + at * cw + w / 2, -390);
            add(band);
            RectMob box = subBox(sub[i], col[i], 34, 34, 32);
            play(new FadeIn(band, d(0.6)), new FadeIn(box, d(0.6)));
            pause(1.5);
            if (i < 2) {
                play(new FadeOut(band, d(0.5)), new FadeOut(box, d(0.5)));
                remove(band);
                remove(box);
            } else {
                mine.add(band);
                mine.add(box);
            }
        }
        pause(0.8);

        // the parentheses go away
        sayAt("The tree needs none of them: its shape already says what goes with what.", Colors.GREEN, 0, 390, 34);
        List<Animation> out = new ArrayList<>();
        for (int i = 0; i < EXPR.length(); i++) {
            char ch = EXPR.charAt(i);
            if ((ch == '(' || ch == ')') && chars.get(i) != null) out.add(new FadeOut(chars.get(i), d(1.0)));
        }
        playAll(out);
        pause(2.2);
        unsay();
        StrokeTextMob q1 = stroke("Where did the parentheses go?", 0, 365, 42, Colors.WHITE, false);
        play(new Write(q1, d(2.0)));
        StrokeTextMob q2 = stroke("Can we write the expression itself in a way that no parentheses are required?", 0, 440, 36, Colors.GOLD, false);
        play(new Write(q2, d(4.2)));
        mine.add(q1);
        mine.add(q2);
        pause(3.2);
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }
}
