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
 * Standalone clips for slides 22 and 23 of the trees deck: inorder and postorder traversals of the expression
 * tree (a + b * c) + ((d * e + f) * g).
 *
 *   slide 22  preorder (NLR), postorder (LRN), inorder (LNR) one line at a time, the inorder code with a
 *             highlight bar, the walk on the tree writing a+b*c+d*e+f*g, and then the catch: the actual
 *             expression needed parentheses, and without them the same string reads as a different value
 *   slide 23  the same list with Postorder in bold, the postorder code, the walk writing abc*+de*f+g*+,
 *             "operator precedence encoded": no parentheses are needed
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeWalkScene extends PDSTreeClipBase {

    private static final String EXPR = "(a + b * c) + ((d * e + f) * g)";

    private final boolean inorder;
    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();
    private CodeBox code;
    private GT t;
    private OutStrip os;
    private int step;

    public PDSTreeWalkScene(boolean inorder) { this.inorder = inorder; }

    @Override
    public void construct() {
        head = writeHeading("Traversals");
        pause(0.5);
        bullets();
        walkAll();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void bullets() {
        List<Spec> s = new ArrayList<>();
        if (inorder) {
            s.add(ln(0, "preorder (NLR)").kw("NLR", Colors.GOLD));
            s.add(ln(0, "postorder (LRN)").kw("LRN", Colors.ORANGE));
            s.add(ln(0, "inorder (LNR)").kw("LNR", Colors.GREEN));
        } else {
            s.add(ln(0, "preorder"));
            s.add(ln(0, "Postorder").tail("(7.cpp)").gap(18).kw("Postorder", Colors.ORANGE));
            s.add(ln(0, "inorder"));
        }
        List<List<MObject>> text = writeSlide(s, -250);
        pause(1.4);
        swipeAway(text);
        pause(0.4);
    }

    private void hl(int line) { play(code.moveHl(line, d(step < 6 ? 0.28 : 0.15))); }

    private void narr(String s, Color c) { sayAt(s, c, 0, 440, 34); }

    private void walkAll() {
        String name = inorder ? "inorder" : "postorder";
        code = new CodeBox(inorder ? new String[]{
                "void Tree::inorder(PtrToNode rr) {",
                "    if (rr) {",
                "        inorder(rr->left);",
                "        rr->print();",
                "        inorder(rr->right);",
                "    }",
                "}",
                "void Tree::inorder() {",
                "    inorder(root);",
                "    std::cout << std::endl;",
                "}"} : new String[]{
                "void Tree::postorder(PtrToNode rr) {",
                "    if (rr) {",
                "        postorder(rr->left);",
                "        postorder(rr->right);",
                "        rr->print();",
                "    }",
                "}",
                "void Tree::postorder() {",
                "    postorder(root);",
                "    std::cout << std::endl;",
                "}"}, -930, -380, 24, 36);
        code.typeIn(6.0);
        mine.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.3)));
        GN root = parseInfix(EXPR);
        t = new GT(root, 400, -300, 95, 30, 24, 26, false, n -> n.leaf() ? Colors.BLUE : Colors.GOLD, true);
        t.build(0.3);
        mine.addAll(t.parts());
        os = new OutStrip("output:", -900, 230, 56, inorder ? Colors.GREEN : Colors.ORANGE);
        os.showTitle();
        mine.addAll(os.all());
        TextMob q = label("Find output of this code on this example tree.", -560, 140, 28, Colors.LIGHT_GRAY, false, false);
        play(new FadeIn(q, d(0.6)));
        mine.add(q);
        pause(0.6);
        narr(inorder ? "Left subtree, then the node, then the right subtree." : "Left subtree, then right subtree, then the node.", Colors.LIGHT_GRAY);
        pause(0.8);
        step = 0;
        walk(root);
        mine.addAll(os.all());
        hl(inorder ? 9 : 9);
        String out = joined(order(root), "");
        TextMob res = mono(out, -560, 320, 44, inorder ? Colors.RED : Colors.GOLD);
        play(new FadeIn(res, d(0.8)));
        mine.add(res);
        pause(1.4);
        if (inorder) afterInorder(root, out);
        else afterPostorder();
    }

    private List<GN> order(GN root) {
        List<GN> l = new ArrayList<>();
        if (inorder) inorderInto(root, l);
        else postorderInto(root, l);
        return l;
    }

    private void walk(GN n) {
        step++;
        hl(0);
        hl(1);
        List<Animation> a = new ArrayList<>();
        t.paint(a, n, VISIT_C, d(0.25));
        playAll(a);
        GN l = leftOf(n), r = rightOf(n);
        if (inorder) {
            if (l != null) { hl(2); walk(l); }
            hl(3);
            emit(n);
            if (r != null) { hl(4); walk(r); }
        } else {
            if (l != null) { hl(2); walk(l); }
            if (r != null) { hl(3); walk(r); }
            hl(4);
            emit(n);
        }
        List<Animation> b = new ArrayList<>();
        t.paint(b, n, DONE_C, d(0.25));
        playAll(b);
    }

    private void emit(GN n) {
        os.add(n.name);
        mine.addAll(os.all());
        pause(step < 6 ? 0.45 : 0.15);
    }

    private void afterInorder(GN root, String out) {
        TextMob act = label("Actual expression:  " + EXPR, -290, 395, 30, Colors.WHITE, false, true);
        play(new FadeIn(act, d(0.8)));
        mine.add(act);
        pause(1.8);
        narr("The parentheses are gone: the output alone does not say how to group.", Colors.ORANGE);
        pause(2.4);
        // the same string evaluated by precedence is a different number
        TextMob v1 = label("with a..g = 1..7:  the real expression = 189", 400, 140, 28, Colors.GREEN, false, true);
        TextMob v2 = label("the printed string read by precedence = 69", 400, 185, 28, Colors.RED, false, true);
        play(new FadeIn(v1, d(0.7)));
        pause(1.0);
        play(new FadeIn(v2, d(0.7)));
        mine.add(v1);
        mine.add(v2);
        narr("Inorder keeps the left-to-right order but loses the structure.", Colors.RED);
        pause(3.4);
        unsay();
    }

    private void afterPostorder() {
        TextMob note = label("Operator precedence encoded.", 400, 160, 36, Colors.GREEN, false, true);
        play(new FadeIn(note, d(0.8)));
        mine.add(note);
        narr("An operator comes right after its two operands: no parentheses are needed.", Colors.GREEN);
        pause(3.6);
        unsay();
    }
}
