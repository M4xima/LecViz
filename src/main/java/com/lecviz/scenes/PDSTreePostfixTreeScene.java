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
 * Standalone clip for slide 27 of the trees deck: from a postfix expression to an expression tree.
 *
 *   - the slide's algorithm typed in as pseudo-code, the postfix string a b c * + d e * f + g * + as a row of tokens
 *   - a pointer walks over the tokens: an operand makes a node and pushes it on a stack; an operator pops two
 *     nodes, connects them under a new operator node and pushes that root. The tree grows at its final place on
 *     the right; the nodes that are on the stack right now (the roots of the pieces built so far) are gold
 *   - the result is the tree of the expression (a + b * c) + ((d * e + f) * g) from slide 21
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreePostfixTreeScene extends PDSTreeClipBase {

    private static final String POSTFIX = "abc*+de*f+g*+";

    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();
    private CodeBox code;
    private GT t;
    private VStack vs;
    private final java.util.Map<GN, Cell> cellOf = new java.util.HashMap<>();
    private final java.util.Map<GN, Color> baseOf = new java.util.HashMap<>();

    @Override
    public void construct() {
        head = writeHeading("Postfix to Expression Tree");
        pause(0.5);
        code = new CodeBox(new String[]{
                "For each symbol in the expression",
                "  If the symbol is an operand",
                "    Push its node to stack",
                "  Else if the symbol is an operator",
                "    Pop two nodes from the stack",
                "    Connect those to the operator",
                "    Push root of the tree to stack"}, -930, -260, 24, 38);
        code.typeIn(4.0);
        mine.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.3)));
        TextMob src = label("Source: postfix2tree.cpp", 740, -470, 26, Colors.GRAY, false, false);
        play(new FadeIn(src, d(0.6)));
        mine.add(src);

        GN root = parseInfix("(a + b * c) + ((d * e + f) * g)");
        List<GN> post = new ArrayList<>();
        postorderInto(root, post);
        t = new GT(root, 430, -250, 100, 30, 24, 26, false, n -> n.leaf() ? Colors.BLUE : Colors.ORANGE, true);
        mine.addAll(t.parts());
        for (GN n : t.nodes) baseOf.put(n, n.leaf() ? Colors.BLUE : Colors.ORANGE);

        List<Cell> toks = new ArrayList<>();
        List<Animation> ta = new ArrayList<>();
        for (int i = 0; i < POSTFIX.length(); i++) {
            String ch = String.valueOf(POSTFIX.charAt(i));
            boolean op = "+*".contains(ch);
            Cell c = tokCell(ch, -560 + i * 74, -380, 62, 58, op ? Colors.ORANGE : Colors.BLUE, 30);
            c.fadeIn(ta, 0.07 * i, d(0.4));
            toks.add(c);
            mine.addAll(c.parts());
        }
        playAll(ta);
        vs = new VStack(-640, 400, 150, 50, 4, "top", 1);
        vs.showBox();
        vs.showTop();
        mine.addAll(vs.parts());
        TextMob sl = label("stack of nodes", -640, 120, 26, Colors.GRAY, false, true);
        play(new FadeIn(sl, d(0.5)));
        mine.add(sl);
        pause(0.6);
        Ptr ptr = pointer("", -560, -335, false, Colors.GOLD);
        mine.addAll(ptr.parts());
        play(new FadeIn(ptr.arrow, d(0.4)));

        for (int i = 0; i < POSTFIX.length(); i++) {
            GN n = post.get(i);
            List<Animation> mv = new ArrayList<>();
            ptr.go(mv, -560 + i * 74, d(0.35));
            toks.get(i).color(mv, Colors.GOLD, d(0.3));
            playAll(mv);
            play(code.moveHl(0, d(0.22)));
            if (n.leaf()) {
                play(code.moveHl(1, d(0.22)));
                play(code.moveHl(2, d(0.25)));
                List<Animation> a = new ArrayList<>();
                // the edge to the parent waits until the parent is made
                a.add(new FadeInAt(n.shape, 0, d(0.5)));
                a.add(new FadeInAt(n.text, 0, d(0.5)));
                t.paint(a, n, Colors.GOLD, d(0.5));
                playAll(a);
                push(n);
                if (i == 0) sayAt("An operand becomes a node, and the node goes on the stack.", Colors.LIGHT_GRAY, 0, 488, 34);
            } else {
                play(code.moveHl(3, d(0.22)));
                play(code.moveHl(4, d(0.3)));
                GN right = rightOf(n), left = leftOf(n);
                Cell cr = vs.popTo(-380, 250);
                Cell cl = vs.popTo(-380, 310);
                if (i == 4) sayAt("An operator pops two nodes: the first popped is its right child.", Colors.ORANGE, 0, 488, 34);
                play(code.moveHl(5, d(0.3)));
                List<Animation> a = new ArrayList<>();
                a.add(new FadeInAt(n.shape, 0, d(0.5)));
                a.add(new FadeInAt(n.text, 0, d(0.5)));
                a.add(new DrawLinkAt(left.edge, 0.2, d(0.7)));
                a.add(new DrawLinkAt(right.edge, 0.2, d(0.7)));
                t.paint(a, left, baseOf.get(left), d(0.5));
                t.paint(a, right, baseOf.get(right), d(0.5));
                t.paint(a, n, Colors.GOLD, d(0.5));
                cl.fadeOut(a, d(0.5));
                cr.fadeOut(a, d(0.5));
                playAll(a);
                play(code.moveHl(6, d(0.3)));
                push(n);
            }
            List<Animation> dim = new ArrayList<>();
            toks.get(i).color(dim, Colors.GRAY, d(0.3));
            playAll(dim);
            pause(i < 6 ? 0.45 : 0.2);
        }
        play(code.moveHl(0, d(0.3)));
        List<Animation> fin = new ArrayList<>();
        t.paint(fin, root, ROOT_C, d(0.6));
        playAll(fin);
        sayAt("One node is left on the stack: the root of the whole expression tree.", Colors.GREEN, 0, 488, 34);
        pause(3.6);
        unsay();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void push(GN n) {
        Cell c = vs.push(n.name, baseOf.get(n) == Colors.BLUE ? Colors.BLUE : Colors.ORANGE);
        cellOf.put(n, c);
        mine.addAll(vs.parts());
    }
}
