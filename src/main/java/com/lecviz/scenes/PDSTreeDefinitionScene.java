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
 * Standalone clip for slide 4 of the trees deck: the definition of a tree.
 *
 *   - the slide's text, line by line, base case and recursive comments in gray
 *   - the recursive definition drawn: a root whose children each head a tree in itself (boxes around the
 *     subtrees, boxes inside boxes), and a leaf whose "zero children" are empty trees: the base case
 *   - the alternative definition: the edges turn into arrows, every node except one has exactly one
 *     parent (counted node by node), the one without a parent is the root; a node given a second parent
 *     is shown to break the rule
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeDefinitionScene extends PDSTreeClipBase {

    private StrokeTextMob head;

    @Override
    public void construct() {
        head = writeHeading("Definition");
        pause(0.5);
        slideText();
        recursive();
        directed();
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void slideText() {
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "A tree is a collection of nodes.").kw("tree", Colors.BLUE).kw("nodes", Colors.TEAL));
        s.add(ln(3, "It could be empty.").tail("// base case").gap(110));
        s.add(ln(0, "Otherwise, it contains a root node,").kw("root", Colors.MAROON));
        s.add(ln(3, "connected to zero or more (child) nodes,").kw("child", Colors.BLUE));
        s.add(ln(3, "each of which is a tree in itself!").tail("// recursive").gap(60));
        s.add(ln(0, "Alternatively, a tree is a collection of nodes and"));
        s.add(ln(3, "directed edges, such that each node except one").kw("directed", Colors.BLUE));
        s.add(ln(3, "has a single parent. The node without a parent").kw("parent", Colors.ORANGE));
        s.add(ln(3, "node is the root.").kw("root", Colors.MAROON));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(1.4);
        swipeAway(text);
        pause(0.4);
    }

    // ── the recursive definition ─────────────────────────────────────

    private GT t1;
    private final List<MObject> mine = new ArrayList<>();

    private void recursive() {
        GN root = smallTree();
        t1 = tree(root, 0, -285, 140, 130, 28, 30, false);
        t1.build(0.35);
        mine.addAll(t1.parts());
        pause(0.4);
        GN b = root.kids.get(0), c = root.kids.get(1), dd = root.kids.get(2);

        StrokeTextMob cap1 = stroke("Each child is the root of a tree in itself.", 0, 255, 42, Colors.WHITE, false);
        play(new Write(cap1, d(2.6)));
        mine.add(cap1);
        pause(0.3);
        GN[] subs = {b, c, dd};
        List<RectMob> boxes = new ArrayList<>();
        for (GN s : subs) {
            RectMob r = subBox(s, Colors.TEAL, 38, 38, 76);
            boxes.add(r);
            List<Animation> a = new ArrayList<>();
            a.add(new FadeIn(r, d(0.6)));
            t1.paint(a, s, Colors.TEAL, d(0.6));
            playAll(a);
            pause(0.35);
        }
        mine.addAll(boxes);
        TextMob[] tg3 = new TextMob[3];
        for (int i = 0; i < 3; i++) {
            RectMob bx = boxes.get(i);
            tg3[i] = label("tree", bx.getPosition().x(), bx.getPosition().y() + bx.getHeight() / 2 + 26, 28, Colors.TEAL, false, true);
        }
        TextMob tag = tg3[0], tag2 = tg3[1], tag3 = tg3[2];
        play(new FadeIn(tag, d(0.4)), new FadeIn(tag2, d(0.4)), new FadeIn(tag3, d(0.4)));
        mine.add(tag);
        mine.add(tag2);
        mine.add(tag3);
        pause(1.0);

        // and again, inside the first box
        play(new FadeOut(cap1, d(0.4)));
        StrokeTextMob cap2 = stroke("Look inside: B is a root with two trees below it.", 0, 255, 42, Colors.WHITE, false);
        play(new Write(cap2, d(2.8)));
        mine.add(cap2);
        List<RectMob> inner = new ArrayList<>();
        for (GN k : b.kids) {
            RectMob r = subBox(k, Colors.ORANGE, 30, 30, 30);
            inner.add(r);
        }
        List<Animation> a = new ArrayList<>();
        for (RectMob r : inner) a.add(new FadeIn(r, d(0.7)));
        t1.paint(a, b.kids.get(0), Colors.ORANGE, d(0.7));
        t1.paint(a, b.kids.get(1), Colors.ORANGE, d(0.7));
        playAll(a);
        mine.addAll(inner);
        pause(1.4);

        // zero children: empty trees, the base case
        play(new FadeOut(cap2, d(0.4)));
        StrokeTextMob cap3 = stroke("A leaf has zero children: only empty trees below it.", 0, 255, 42, Colors.WHITE, false);
        play(new Write(cap3, d(3.0)));
        mine.add(cap3);
        List<MObject> empties = new ArrayList<>();
        List<Animation> ea = new ArrayList<>();
        for (GN n : t1.nodes) {
            if (!n.leaf()) continue;
            TextMob e = label("∅", n.x, n.y + 50, 32, Colors.GREEN, false, true);
            ea.add(new FadeIn(e, d(0.6)));
            empties.add(e);
        }
        playAll(ea);
        mine.addAll(empties);
        pause(0.5);
        TextMob base = label("the empty tree: the base case", 0, 320, 30, Colors.GREEN, false, false);
        play(new FadeIn(base, d(0.6)));
        mine.add(base);
        pause(2.2);

        // clear the boxes before the next idea
        List<Animation> out = new ArrayList<>();
        for (MObject m : boxes) out.add(new FadeOut(m, d(0.8)));
        for (MObject m : inner) out.add(new FadeOut(m, d(0.8)));
        out.add(new FadeOut(tag, d(0.8)));
        out.add(new FadeOut(tag2, d(0.8)));
        out.add(new FadeOut(tag3, d(0.8)));
        for (MObject m : empties) out.add(new FadeOut(m, d(0.8)));
        out.add(new FadeOut(cap3, d(0.8)));
        out.add(new FadeOut(base, d(0.8)));
        for (GN n : t1.nodes) t1.paintDefault(out, n, d(0.8));
        playAll(out);
        pause(0.3);
    }

    // ── nodes and directed edges ─────────────────────────────────────

    private void directed() {
        GN root2 = smallTree();
        GT t2 = tree(root2, 0, -285, 140, 130, 28, 30, true);
        // the same picture, but every edge now has an arrowhead
        List<Animation> sw = new ArrayList<>();
        for (GN n : t1.nodes) if (n.edge != null) sw.add(new FadeOut(n.edge, d(0.6)));
        for (GN n : t2.nodes) if (n.edge != null) sw.add(new DrawLinkAt(n.edge, 0, d(1.0)));
        playAll(sw);
        mine.addAll(t2.parts());
        StrokeTextMob cap = stroke("Directed edges: each one points from a parent to its child.", 0, 255, 40, Colors.WHITE, false);
        play(new Write(cap, d(3.2)));
        mine.add(cap);
        pause(0.6);

        // count the parents of every node
        play(new FadeOut(cap, d(0.4)));
        StrokeTextMob cap2 = stroke("Every node except one has a single parent.", 0, 255, 40, Colors.WHITE, false);
        play(new Write(cap2, d(2.6)));
        mine.add(cap2);
        List<MObject> tags = new ArrayList<>();
        for (GN n : t1.nodes) {
            if (n.parent == null) continue;
            TextMob tg = label("1 parent", n.x, n.y + 54, 24, Colors.GOLD, false, true);
            List<Animation> a = new ArrayList<>();
            a.add(new FadeIn(tg, d(0.4)));
            t1.paint(a, n, Colors.GOLD, d(0.4));
            playAll(a);
            pause(0.12);
            List<Animation> back = new ArrayList<>();
            t1.paintDefault(back, n, d(0.4));
            playAll(back);
            tags.add(tg);
        }
        mine.addAll(tags);
        pause(0.4);
        GN r = t1.root;
        TextMob rt = label("no parent", r.x + 190, r.y - 12, 28, Colors.MAROON, false, true);
        List<Animation> ra = new ArrayList<>();
        ra.add(new FadeIn(rt, d(0.5)));
        t1.paint(ra, r, Colors.RED, d(0.5));
        playAll(ra);
        mine.add(rt);
        pause(0.4);
        TextMob rt2 = label("so this is the root", r.x + 190, r.y + 24, 24, Colors.LIGHT_GRAY, false, false);
        play(new FadeIn(rt2, d(0.5)));
        mine.add(rt2);
        List<Animation> back = new ArrayList<>();
        t1.paint(back, r, ROOT_C, d(0.5));
        playAll(back);
        pause(1.6);

        // what breaks the rule: a second parent
        play(new FadeOut(cap2, d(0.4)));
        StrokeTextMob cap3 = stroke("Give F a second parent and it is no longer a tree.", 0, 255, 40, Colors.ORANGE, false);
        play(new Write(cap3, d(3.0)));
        mine.add(cap3);
        GN cN = t1.root.kids.get(1), fN = t1.root.kids.get(0).kids.get(1);
        Link extra = arrow(cN.x - 12, cN.y + cN.hh, fN.x + 16, fN.y - fN.hh - 4, Colors.RED, 4);
        play(new DrawLink(extra, d(0.9)));
        List<Animation> bad = new ArrayList<>();
        t1.paint(bad, fN, Colors.RED, d(0.5));
        playAll(bad);
        TextMob two = label("2 parents", fN.x, fN.y + 56, 26, Colors.RED, false, true);
        play(new FadeIn(two, d(0.5)));
        pause(2.2);
        List<Animation> fin = new ArrayList<>();
        fin.add(new FadeOut(extra, d(0.8)));
        fin.add(new FadeOut(two, d(0.8)));
        t1.paintDefault(fin, fN, d(0.8));
        playAll(fin);
        remove(extra);
        remove(two);
        pause(0.3);
        fadeOutAll(d(1.2), mine);
        pause(0.3);
    }
}
