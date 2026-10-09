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
 * Standalone clip for slide 9 of the trees deck: the learning outcomes.
 *
 *   - the four outcomes one line at a time
 *   - then four cards, each with a small picture of its outcome: applications of trees (org chart,
 *     file system, expression), constructing a tree and inserting a node, traversals (visit order),
 *     and complexity (how many steps)
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeOutcomesScene extends PDSTreeClipBase {

    private StrokeTextMob head;

    @Override
    public void construct() {
        head = writeHeading("Learning Outcomes");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Apply tree data structure in relevant applications.").kw("Apply", Colors.BLUE));
        s.add(ln(0, "Construct trees in C++ and perform operations").kw("Construct", Colors.GREEN));
        s.add(ln(3, "such as insert."));
        s.add(ln(0, "Perform traversals on trees.").kw("traversals", Colors.ORANGE));
        s.add(ln(0, "Analyze complexity of various operations.").kw("complexity", Colors.PINK));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(1.2);
        swipeAway(text);
        pause(0.4);
        cards();
        fadeOutAll(d(1.2), head);
        pause(0.3);
    }

    private void cards() {
        List<MObject> mine = new ArrayList<>();
        double[] cx = {-705, -235, 235, 705};
        String[] name = {"Apply", "Construct", "Traverse", "Analyze"};
        String[] sub = {"in relevant applications", "in C++, e.g. insert", "visit every node", "the cost of operations"};
        Color[] col = {Colors.BLUE, Colors.GREEN, Colors.ORANGE, Colors.PINK};
        for (int i = 0; i < 4; i++) {
            RectMob card = panel(cx[i], 20, 440, 760, col[i], 0.07);
            TextMob nm = label(name[i], cx[i], -300, 44, col[i], false, true);
            TextMob sb = label(sub[i], cx[i], -245, 26, Colors.LIGHT_GRAY, false, false);
            play(new FadeIn(card, d(0.6)), new FadeIn(nm, d(0.6)), new FadeIn(sb, d(0.6)));
            mine.add(card);
            mine.add(nm);
            mine.add(sb);
            if (i == 0) apply(cx[i], mine);
            if (i == 1) construct(cx[i], mine);
            if (i == 2) traverse(cx[i], mine);
            if (i == 3) analyze(cx[i], mine);
            pause(0.6);
        }
        pause(3.0);
        fadeOutAll(d(1.2), mine);
    }

    private void apply(double cx, List<MObject> mine) {
        String[] app = {"employees", "files and folders", "expressions", "routes and calls"};
        GT t = tree(g("", g("", g(""), g("")), g("", g(""))), cx, -160, 80, 34, 17, 1, false);
        t.build(0.2);
        mine.addAll(t.parts());
        for (int k = 0; k < app.length; k++) {
            List<MObject> ch = chip(app[k], cx, 80 + 78 * k, 360, 58, Colors.BLUE, 27);
            List<Animation> a = new ArrayList<>();
            for (MObject m : ch) a.add(new DropIn(m, 40, 0, d(0.6)));
            playAll(a);
            mine.addAll(ch);
            pause(0.2);
        }
    }

    private void construct(double cx, List<MObject> mine) {
        GN root = g("", g(""), g(""));
        GT t = tree(root, cx, -160, 90, 70, 20, 1, false);
        t.build(0.2);
        mine.addAll(t.parts());
        TextMob code = mono("parent->addChild(x);", cx, 110, 24, Colors.WHITE);
        play(new FadeIn(code, d(0.6)));
        mine.add(code);
        // a new node joins under the left child
        GN left = root.kids.get(0);
        CircleMob nn = new CircleMob(20);
        nn.setFillColor(Colors.withAlpha(Colors.GREEN, 0.5));
        nn.setStrokeColor(Colors.GREEN);
        nn.setStrokeWidth(3);
        nn.setPosition(left.x, left.y + 90);
        nn.setOpacity(0);
        add(nn);
        Link e = arrow(left.x, left.y + 20, left.x, left.y + 90 - 22, Colors.GREEN, 3);
        play(new FadeIn(nn, d(0.6)), new DrawLink(e, d(0.6)));
        mine.add(nn);
        mine.add(e);
        TextMob ins = label("insert", cx, 175, 34, Colors.GREEN, false, true);
        play(new FadeIn(ins, d(0.5)));
        mine.add(ins);
    }

    private void traverse(double cx, List<MObject> mine) {
        GN root = g("", g("", g(""), g("")), g("", g("")));
        GT t = tree(root, cx, -160, 85, 62, 20, 22, false);
        t.build(0.2);
        mine.addAll(t.parts());
        List<GN> order = new ArrayList<>();
        preorderInto(root, order);
        int k = 1;
        for (GN n : order) {
            List<Animation> a = new ArrayList<>();
            t.paint(a, n, Colors.GOLD, d(0.3));
            TextMob nl = label(String.valueOf(k++), n.x, n.y, 22, Colors.WHITE, false, true);
            a.add(new FadeIn(nl, d(0.3)));
            playAll(a);
            mine.add(nl);
            pause(0.2);
        }
        TextMob w = label("preorder, postorder,", cx, 80, 26, Colors.LIGHT_GRAY, false, false);
        TextMob w2 = label("level by level ...", cx, 115, 26, Colors.LIGHT_GRAY, false, false);
        play(new FadeIn(w, d(0.6)), new FadeIn(w2, d(0.6)));
        mine.add(w);
        mine.add(w2);
    }

    private void analyze(double cx, List<MObject> mine) {
        Steps st = new Steps(cx - 40, -120, 90);
        play(new FadeIn(st.num, d(0.4)), new FadeIn(st.cap, d(0.4)));
        mine.addAll(st.parts());
        for (int i = 0; i < 12; i++) {
            st.tick();
            pause(0.12);
        }
        String[] big = {"O(1)", "O(N)"};
        Color[] cc = {Colors.GREEN, Colors.ORANGE};
        for (int k = 0; k < 2; k++) {
            List<MObject> ch = chip(big[k], cx, 20 + 90 * k, 240, 66, cc[k], 36);
            List<Animation> a = new ArrayList<>();
            for (MObject m : ch) a.add(new DropIn(m, 40, 0, d(0.6)));
            playAll(a);
            mine.addAll(ch);
            pause(0.2);
        }
        TextMob w = label("how many steps as the", cx, 250, 26, Colors.LIGHT_GRAY, false, false);
        TextMob w2 = label("tree grows?", cx, 285, 26, Colors.LIGHT_GRAY, false, false);
        play(new FadeIn(w, d(0.6)), new FadeIn(w2, d(0.6)));
        mine.add(w);
        mine.add(w2);
    }
}
