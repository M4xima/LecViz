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
 * Standalone clip for slide 10 of the trees deck: implementing a tree whose nodes have any number of children.
 *
 *   - the slide's challenge in two lines, then the tree A..N that the slide draws
 *   - the C version: each struct line is typed and explained by a node record (data, firstChild,
 *     nextSibling); the tree redraws itself as first-child arrows (gold) down and next-sibling arrows (teal)
 *     across, with the other parent-to-child edges fading away
 *   - the C++ version: the same tree again with a vector of child pointers in every node
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeImplScene extends PDSTreeClipBase {

    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();
    private GT t;
    private GN root;

    @Override
    public void construct() {
        head = writeHeading("Implementation");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "A challenge is that the maximum number of").kw("maximum number of", Colors.ORANGE));
        s.add(ln(3, "children is unknown, and may vary dynamically.").kw("unknown", Colors.RED).kw("vary dynamically", Colors.ORANGE));
        List<List<MObject>> text = writeSlide(s, -300);
        pause(1.4);
        swipeAway(text);
        pause(0.4);

        root = idTree();
        t = new GT(root, 0, -330, 120, 44, 24, 26, true, null);
        t.build(0.35);
        mine.addAll(t.parts());
        StrokeTextMob q = stroke("How many child pointers should a node have?", 0, 250, 42, Colors.WHITE, false);
        play(new Write(q, d(2.6)));
        mine.add(q);
        pause(0.5);
        // nodes with different numbers of children
        GN a = root, f = at(root, 4), g0 = at(root, 5);
        String[] cnt = {"6 children", "3 children", "none"};
        GN[] who = {a, f, g0};
        List<MObject> tags = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            double[] off = i == 0 ? new double[]{0, -52} : i == 1 ? new double[]{46, -40} : new double[]{0, 50};
            TextMob tg = label(cnt[i], who[i].x + off[0], who[i].y + off[1], 24, Colors.ORANGE, false, true);
            play(new FadeIn(tg, d(0.5)));
            tags.add(tg);
            pause(0.7);
        }
        pause(1.0);
        fadeOutAll(d(0.6), tags);
        fadeOutAll(d(0.6), q);
        List<Animation> sh = new ArrayList<>();
        t.shiftAnim(sh, 400, 0, d(1.2));
        playAll(sh);
        cVersion();
        cppVersion();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private CircleMob dot(double x, double y, Color c) {
        CircleMob d = new CircleMob(8);
        d.setFillColor(c);
        d.setStrokeColor(Color.TRANSPARENT);
        d.setPosition(x, y);
        d.setOpacity(0);
        add(d);
        return d;
    }

    private RectMob cell(double x, double y, double w, double h, Color c) {
        RectMob r = panel(x, y, w, h, c, 0.16);
        return r;
    }

    // ── C: first child + next sibling ────────────────────────────────

    private void cVersion() {
        TextMob lang = label("C", -620, -400, 44, Colors.ORANGE, false, true);
        play(new FadeIn(lang, d(0.5)));
        mine.add(lang);
        CodeBox code = new CodeBox(new String[]{
                "typedef struct TreeNode *PtrToNode;",
                "",
                "struct TreeNode {",
                "    char data;",
                "    PtrToNode firstChild;",
                "    PtrToNode nextSibling;",
                "};"}, -930, -330, 24, 36);
        code.typeIn(4.0);
        mine.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.3)));
        pause(0.4);

        // a node record: data, firstChild, nextSibling
        double ry = 120, x0 = -800;
        RectMob c1 = cell(x0, ry, 150, 84, Colors.BLUE);
        RectMob c2 = cell(x0 + 160, ry, 150, 84, Colors.GOLD);
        RectMob c3 = cell(x0 + 320, ry, 150, 84, Colors.TEAL);
        GN dN = at(root, 2);
        TextMob dv = label("D", x0, ry, 40, Colors.WHITE, false, true);
        TextMob l1 = label("data", x0, ry + 64, 24, Colors.BLUE, false, true);
        TextMob l2 = label("firstChild", x0 + 160, ry + 64, 24, Colors.GOLD, false, true);
        TextMob l3 = label("nextSibling", x0 + 320, ry + 64, 24, Colors.TEAL, false, true);
        CircleMob p2 = dot(x0 + 160, ry, Colors.GOLD);
        CircleMob p3 = dot(x0 + 320, ry, Colors.TEAL);
        List<Animation> a = new ArrayList<>();
        for (MObject m : new MObject[]{c1, c2, c3, dv, l1, l2, l3}) a.add(new FadeIn(m, d(0.6)));
        playAll(a);
        mine.addAll(List.of(c1, c2, c3, dv, l1, l2, l3, p2, p3));
        sayAt("Every node keeps exactly two pointers, however many children it has.", Colors.LIGHT_GRAY, 0, 405, 32);
        pause(1.4);

        // firstChild: the node's first child
        play(code.moveHl(4, d(0.4)));
        GN h = dN.kids.get(0), e = at(root, 3);
        Link fc = arrow(x0 + 160, ry - 12, h.x - 22, h.y + 18, Colors.GOLD, 3.4);
        play(new FadeIn(p2, d(0.3)), new DrawLink(fc, d(1.0)));
        mine.add(fc);
        List<Animation> pa = new ArrayList<>();
        t.paint(pa, dN, Colors.GOLD, d(0.4));
        t.paint(pa, h, Colors.GOLD, d(0.4));
        playAll(pa);
        sayAt("firstChild: the node's first child (H).", Colors.GOLD, 0, 405, 32);
        pause(1.6);
        // nextSibling: the next child of the same parent
        play(code.moveHl(5, d(0.4)));
        Link ns = arrow(x0 + 320, ry - 12, e.x - 22, e.y + 18, Colors.TEAL, 3.4);
        play(new FadeIn(p3, d(0.3)), new DrawLink(ns, d(1.0)));
        mine.add(ns);
        List<Animation> pb = new ArrayList<>();
        t.paint(pb, e, Colors.TEAL, d(0.4));
        playAll(pb);
        sayAt("nextSibling: the next child of the same parent (E).", Colors.TEAL, 0, 405, 32);
        pause(1.8);
        List<Animation> clear = new ArrayList<>();
        clear.add(new FadeOut(fc, d(0.5)));
        clear.add(new FadeOut(ns, d(0.5)));
        t.paintDefault(clear, dN, d(0.5));
        t.paintDefault(clear, h, d(0.5));
        t.paintDefault(clear, e, d(0.5));
        playAll(clear);

        // the whole tree in this form
        sayAt("Redraw the whole tree this way: down for the first child, across for the siblings.", Colors.LIGHT_GRAY, 0, 405, 32);
        List<Animation> fade = new ArrayList<>();
        List<MObject> sib = new ArrayList<>();
        List<Animation> draw = new ArrayList<>();
        for (GN p : t.nodes) {
            for (int i = 0; i < p.kids.size(); i++) {
                GN c = p.kids.get(i);
                if (i == 0) {
                    t.paintEdge(fade, c, Colors.GOLD, d(0.8));
                } else {
                    fade.add(new FadeOut(c.edge, d(0.8)));
                    GN pv = p.kids.get(i - 1);
                    Link l = arrow(pv.x + 26, pv.y, c.x - 30, c.y, Colors.TEAL, 3.4);
                    sib.add(l);
                    draw.add(new DrawLinkAt(l, 0.5, d(0.9)));
                }
            }
        }
        playAll(fade);
        playAll(draw);
        mine.addAll(sib);
        TextMob k1 = label("firstChild", 640, 320, 28, Colors.GOLD, false, true);
        TextMob k2 = label("nextSibling", 840, 320, 28, Colors.TEAL, false, true);
        play(new FadeIn(k1, d(0.5)), new FadeIn(k2, d(0.5)));
        mine.add(k1);
        mine.add(k2);
        pause(3.2);

        // out with the C picture
        List<Animation> out = new ArrayList<>();
        for (MObject m : code.parts()) out.add(new FadeOut(m, d(0.8)));
        for (MObject m : List.of(c1, c2, c3, dv, l1, l2, l3, p2, p3, k1, k2, lang)) out.add(new FadeOut(m, d(0.8)));
        for (MObject m : sib) out.add(new FadeOut(m, d(0.8)));
        for (GN n : t.nodes) {
            if (n.edge != null) {
                if (n.parent.kids.indexOf(n) > 0) out.add(new FadeIn(n.edge, d(0.8)));
                t.paintEdge(out, n, Colors.withAlpha(Colors.LIGHT_GRAY, 0.8), d(0.8));
            }
        }
        playAll(out);
        for (MObject m : code.parts()) remove(m);
    }

    // ── C++: a vector of children ────────────────────────────────────

    private void cppVersion() {
        unsay();
        TextMob lang = label("C++", -620, -400, 44, Colors.ORANGE, false, true);
        play(new FadeIn(lang, d(0.5)));
        mine.add(lang);
        CodeBox code = new CodeBox(new String[]{
                "#include <vector>",
                "typedef struct TreeNode *PtrToNode;",
                "",
                "struct TreeNode {",
                "    char data;",
                "    std::vector<PtrToNode> children;",
                "};"}, -930, -330, 24, 36);
        code.typeIn(4.0);
        mine.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.3)));
        pause(0.4);
        play(code.moveHl(5, d(0.4)));

        // a node record: data and a vector of pointers
        double ry = 140, x0 = -800;
        GN fN = at(root, 2);
        RectMob c1 = cell(x0, ry, 110, 84, Colors.BLUE);
        TextMob dv = label("D", x0, ry, 40, Colors.WHITE, false, true);
        TextMob l1 = label("data", x0, ry + 64, 24, Colors.BLUE, false, true);
        int nk = fN.kids.size();
        double slotW = 100, pad = 16, left = x0 + 90;
        double vw = 2 * pad + nk * slotW + (nk - 1) * 10;
        RectMob vec = cell(left + vw / 2, ry, vw, 84, Colors.GOLD);
        List<MObject> slots = new ArrayList<>();
        List<Link> arrows = new ArrayList<>();
        for (int i = 0; i < nk; i++) {
            double sx = left + pad + slotW / 2 + i * (slotW + 10);
            RectMob sl = cell(sx, ry, slotW, 62, Colors.GOLD);
            CircleMob dt = dot(sx, ry, Colors.GOLD);
            slots.add(sl);
            slots.add(dt);
            GN c = fN.kids.get(i);
            Link l = arrow(sx, ry - 14, c.x - 6 + i * 12, c.y + 28, Colors.GOLD, 3.2);
            arrows.add(l);
        }
        TextMob l2 = label("children: a vector of pointers", left + vw / 2 + 40, ry + 64, 24, Colors.GOLD, false, true);
        List<Animation> a = new ArrayList<>();
        for (MObject m : new MObject[]{c1, dv, l1, vec, l2}) a.add(new FadeIn(m, d(0.6)));
        for (MObject m : slots) a.add(new FadeInAt(m, 0.4, d(0.6)));
        playAll(a);
        mine.addAll(List.of(c1, dv, l1, vec, l2));
        mine.addAll(slots);
        List<Animation> dr = new ArrayList<>();
        for (int i = 0; i < nk; i++) dr.add(new DrawLinkAt(arrows.get(i), 0.4 * i, d(0.8)));
        playAll(dr);
        mine.addAll(arrows);
        List<Animation> hi = new ArrayList<>();
        t.paint(hi, fN, Colors.GOLD, d(0.5));
        for (GN c : fN.kids) t.paint(hi, c, Colors.GOLD, d(0.5));
        playAll(hi);
        sayAt("One pointer per child, and the vector grows when a child is added: D has H and I.", Colors.LIGHT_GRAY, 0, 405, 32);
        pause(3.4);
        List<Animation> back = new ArrayList<>();
        t.paintDefault(back, fN, d(0.6));
        for (GN c : fN.kids) t.paintDefault(back, c, d(0.6));
        playAll(back);
        sayAt("Simple to use, at the price of a vector in every node.", Colors.ORANGE, 0, 405, 32);
        pause(2.6);
        unsay();
    }
}
