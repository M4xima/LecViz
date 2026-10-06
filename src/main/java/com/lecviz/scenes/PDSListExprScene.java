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
 * Standalone clip for slides 29-30 of the lists deck: expressions, and prefix and postfix forms.
 *
 *   Slide 29  the points one at a time; then 1 + 2 * 3 - 4 is read in the four ways the slide lists, each
 *             drawn as an expression tree that builds level by level and gives its own value
 *             (-3, -1, 3, 5): the infix string alone is ambiguous
 *   Slide 30  infix, postfix and prefix one line at a time; then the tree of (1 + (2 * 3)) - 4 is walked
 *             three ways and each walk writes its form: the operator in the middle (needs parentheses),
 *             after its operands (postfix) or before them (prefix); the slide's question is answered:
 *             the position of the operator already fixes the order, so no parentheses are needed
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListExprScene extends PDSListClipBase {

    private StrokeTextMob head;

    @Override
    public void construct() {
        expressions();
        forms();
    }

    // ── a tiny expression tree ───────────────────────────────────────

    private static final class EN {
        final String s;
        final EN l, r;
        int pos, depth;

        EN(String s) { this(s, null, null); }

        EN(String s, EN l, EN r) {
            this.s = s;
            this.l = l;
            this.r = r;
        }

        int value() {
            if (l == null) return Integer.parseInt(s);
            int a = l.value(), b = r.value();
            return switch (s) {
                case "+" -> a + b;
                case "−" -> a - b;
                default -> a * b;
            };
        }
    }

    private static EN n(String s) { return new EN(s); }

    private static EN op(String s, EN l, EN r) { return new EN(s, l, r); }

    private void layout(EN node, int depth, List<EN> order) {
        if (node == null) return;
        layout(node.l, depth + 1, order);
        node.pos = order.size();
        node.depth = depth;
        order.add(node);
        layout(node.r, depth + 1, order);
    }

    /** A drawn tree: nodes at their in-order position (so the tree reads left to right like the string). */
    private final class Tree {
        final EN root;
        final Map<EN, TNode> map = new HashMap<>();
        final List<EN> order = new ArrayList<>();
        final List<Link> edges = new ArrayList<>();
        final List<MObject> all = new ArrayList<>();

        Tree(EN root, double cx, double y0, double dx, double dy, double r) {
            this.root = root;
            layout(root, 0, order);
            int count = order.size();
            for (EN e : order) {
                boolean opNode = e.l != null;
                TNode t = new TNode(e.s, cx + (e.pos - (count - 1) / 2.0) * dx, y0 + e.depth * dy, r, opNode ? Colors.GOLD : Colors.BLUE);
                map.put(e, t);
                all.addAll(t.parts());
            }
            for (EN e : order) {
                if (e.l != null) {
                    edges.add(treeEdge(map.get(e), map.get(e.l)));
                    edges.add(treeEdge(map.get(e), map.get(e.r)));
                }
            }
            all.addAll(edges);
        }
    }

    private EN[] interpretations() {
        return new EN[]{
                op("*", op("+", n("1"), n("2")), op("−", n("3"), n("4"))),
                op("+", n("1"), op("*", n("2"), op("−", n("3"), n("4")))),
                op("−", op("+", n("1"), op("*", n("2"), n("3"))), n("4")),
                op("−", op("*", op("+", n("1"), n("2")), n("3")), n("4"))};
    }

    // ── slide 29 ─────────────────────────────────────────────────────

    private void expressions() {
        head = writeHeading("Expressions");
        pause(0.4);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "1 + 2 * 3 – 4"));
        s.add(ln(1, "Binary operators appear between the operands").kw("between", Colors.MAROON));
        s.add(ln(1, "Ambiguous without extra knowledge"));
        s.add(ln(2, "(1 + 2) * (3 – 4) OR"));
        s.add(ln(2, "1 + (2 * (3 – 4)) OR"));
        s.add(ln(2, "(1 + (2 * 3)) – 4 OR"));
        s.add(ln(2, "((1 + 2) * 3) – 4 ?"));
        s.add(ln(1, "Parentheses help disambiguate; domain knowledge"));
        s.add(ln(2, "helps disambiguate (operator precedence)."));
        s.add(ln(1, "Won't it be nice if expressions can be written in"));
        s.add(ln(2, "unambiguous manner?"));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(1.2);
        swipeAway(text);
        pause(0.4);

        List<MObject> mine = new ArrayList<>();
        mine.add(head);
        TextMob big = mono("1 + 2 * 3 – 4", 0, -395, 52, Colors.WHITE);
        play(new FadeIn(big, d(0.6)));
        mine.add(big);
        EN[] trees = interpretations();
        String[] cap = {"(1 + 2) * (3 – 4)", "1 + (2 * (3 – 4))", "(1 + (2 * 3)) – 4", "((1 + 2) * 3) – 4"};
        double[] cx = {-470, 470, -470, 470};
        double[] y0 = {-300, -300, 70, 70};
        for (int i = 0; i < 4; i++) {
            Tree t = new Tree(trees[i], cx[i], y0[i], 58, 70, 24);
            buildTree(t);
            mine.addAll(t.all);
            TextMob c = mono(cap[i] + "  =  " + trees[i].value(), cx[i], y0[i] + 3 * 70 + 52, 28, Colors.WHITE);
            play(new FadeIn(c, d(0.6)));
            mine.add(c);
            List<Animation> hi = new ArrayList<>();
            t.map.get(t.root).paint(hi, Colors.GREEN, d(0.4));
            playAll(hi);
            pause(0.7);
        }
        StrokeTextMob a1 = stroke("Four readings, four different values: infix alone is ambiguous.", 0, 395, 38, Colors.ORANGE, false);
        play(new Write(a1, d(3.2)));
        mine.add(a1);
        pause(2.8);
        fadeOutAll(d(1.0), mine);
        pause(0.3);
    }

    /** Level by level: a level's nodes fade in while the edges leading to them are drawn. */
    private void buildTree(Tree t) {
        int maxDepth = 0;
        for (EN e : t.order) maxDepth = Math.max(maxDepth, e.depth);
        for (int dep = 0; dep <= maxDepth; dep++) {
            List<Animation> a = new ArrayList<>();
            for (EN e : t.order) {
                if (e.depth != dep) continue;
                t.map.get(e).fadeIn(a, 0.0, d(0.5));
                if (e.depth > 0) {
                    for (EN p : t.order) {
                        if (p.l == e || p.r == e) {
                            Link ed = findEdge(t, p, e);
                            if (ed != null) a.add(new DrawLinkAt(ed, 0.0, d(0.5)));
                        }
                    }
                }
            }
            playAll(a);
            pause(0.12);
        }
    }

    private Link findEdge(Tree t, EN p, EN c) {
        int idx = 0;
        for (EN e : t.order) {
            if (e.l != null) {
                if (e == p) return t.edges.get(idx + (e.l == c ? 0 : 1));
                idx += 2;
            }
        }
        return null;
    }

    // ── slide 30 ─────────────────────────────────────────────────────

    private void forms() {
        head = writeHeading("Prefix and Postfix Forms");
        pause(0.4);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "1 + 2 * 3 – 4"));
        s.add(ln(1, "Binary operators appear between the operands.").kw("between", Colors.MAROON));
        s.add(ln(1, "Called as infix form.").kw("infix", Colors.MAROON));
        s.add(ln(0, "1 2 3 * + 4 -"));
        s.add(ln(1, "Binary operators appear after the operands.").kw("after", Colors.MAROON));
        s.add(ln(1, "Called as postfix form.").kw("postfix", Colors.MAROON));
        s.add(ln(0, "- + 1 * 2 3 4"));
        s.add(ln(1, "Binary operators appear before the operands.").kw("before", Colors.MAROON));
        s.add(ln(1, "Called as prefix form.").kw("prefix", Colors.MAROON));
        List<List<MObject>> text = writeSlide(s, -330);
        List<MObject> qbox = chip("How do these forms help resolve ambiguity?", 560, 120, 700, 110, Colors.ORANGE, 30);
        List<Animation> qa = new ArrayList<>();
        fade(qa, qbox, d(0.7));
        playAll(qa);
        pause(1.4);
        List<List<MObject>> all = new ArrayList<>(text);
        all.add(qbox);
        swipeAway(all);
        pause(0.4);

        // the tree of (1 + (2 * 3)) - 4, and its three readings
        List<MObject> mine = new ArrayList<>();
        mine.add(head);
        EN root = op("−", op("+", n("1"), op("*", n("2"), n("3"))), n("4"));
        Tree t = new Tree(root, -540, -300, 100, 95, 32);
        buildTree(t);
        mine.addAll(t.all);
        pause(0.6);

        CircleMob ring = new CircleMob(42);
        ring.setFillColor(Color.TRANSPARENT);
        ring.setStrokeColor(Colors.ORANGE);
        ring.setStrokeWidth(5);
        TNode rootNode = t.map.get(root);
        ring.setPosition(rootNode.x, rootNode.y);
        ring.setOpacity(0);
        add(ring);
        play(new FadeIn(ring, d(0.4)));
        mine.add(ring);

        // infix needs parentheses; postfix and prefix do not
        List<String[]> inf = new ArrayList<>();
        List<EN> infN = new ArrayList<>();
        walkInfix(root, inf, infN, true);
        List<String[]> post = new ArrayList<>();
        List<EN> postN = new ArrayList<>();
        walkPost(root, post, postN);
        List<String[]> pre = new ArrayList<>();
        List<EN> preN = new ArrayList<>();
        walkPre(root, pre, preN);

        TextMob l1 = label("infix (needs parentheses)", 300, -300, 28, Colors.TEAL, false, true);
        TextMob l2 = label("postfix: the operator after its operands", 390, -120, 28, Colors.PINK, false, true);
        TextMob l3 = label("prefix: the operator before its operands", 390, 60, 28, Colors.GOLD, false, true);
        play(new FadeIn(l1, d(0.4)));
        mine.add(l1);
        mine.add(l2);
        mine.add(l3);
        walk(t, ring, inf, infN, -40, -230, 0.7, mine);
        play(new FadeIn(l2, d(0.4)));
        walk(t, ring, post, postN, -40, -50, 0.9, mine);
        play(new FadeIn(l3, d(0.4)));
        walk(t, ring, pre, preN, -40, 130, 0.9, mine);
        List<Animation> off = new ArrayList<>();
        off.add(new FadeOut(ring, d(0.5)));
        playAll(off);
        pause(0.4);

        StrokeTextMob a1 = stroke("Where the operator sits already says what happens first,", 0, 330, 38, Colors.GREEN, false);
        StrokeTextMob a2 = stroke("so postfix and prefix need no parentheses at all.", 0, 390, 38, Colors.GREEN, false);
        play(new Write(a1, d(3.0)));
        play(new Write(a2, d(2.6)));
        mine.add(a1);
        mine.add(a2);
        pause(3.0);
        fadeOutAll(d(1.2), mine);
        pause(0.4);
    }

    private void walkInfix(EN e, List<String[]> out, List<EN> owners, boolean top) {
        if (e.l == null) {
            out.add(new String[]{e.s, "num"});
            owners.add(e);
            return;
        }
        out.add(new String[]{"(", "par"});
        owners.add(e);
        walkInfix(e.l, out, owners, false);
        out.add(new String[]{e.s, "op"});
        owners.add(e);
        walkInfix(e.r, out, owners, false);
        out.add(new String[]{")", "par"});
        owners.add(e);
    }

    private void walkPost(EN e, List<String[]> out, List<EN> owners) {
        if (e.l != null) {
            walkPost(e.l, out, owners);
            walkPost(e.r, out, owners);
        }
        out.add(new String[]{e.s, e.l == null ? "num" : "op"});
        owners.add(e);
    }

    private void walkPre(EN e, List<String[]> out, List<EN> owners) {
        out.add(new String[]{e.s, e.l == null ? "num" : "op"});
        owners.add(e);
        if (e.l != null) {
            walkPre(e.l, out, owners);
            walkPre(e.r, out, owners);
        }
    }

    /** The ring walks the tree in the order the tokens are written; each token appears in its row. */
    private void walk(Tree t, CircleMob ring, List<String[]> toks, List<EN> owners, double x0, double y, double sp, List<MObject> mine) {
        int n = toks.size();
        double pitch = n > 9 ? 60 : 66;
        for (int i = 0; i < n; i++) {
            String[] tk = toks.get(i);
            Color c = tk[1].equals("op") ? Colors.GOLD : tk[1].equals("num") ? Colors.BLUE : Colors.GRAY;
            Cell cell = tokCell(tk[0], x0 + 150 + i * pitch, y, pitch - 8, 62, c, 32);
            TNode node = t.map.get(owners.get(i));
            List<Animation> a = new ArrayList<>();
            a.add(new MoveTo(ring, node.x, node.y, d(0.4 * sp)).setEasing(com.lecviz.utils.Easing.EASE_IN_OUT));
            cell.fadeIn(a, 0.15 * sp, d(0.4 * sp));
            playAll(a);
            mine.addAll(cell.parts());
            pause(0.12 * sp);
        }
        pause(0.5);
    }
}
