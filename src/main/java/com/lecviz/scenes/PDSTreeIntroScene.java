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
 * Standalone clip for slides 1-2 of the trees deck: the title card and where trees show up.
 *
 *   Slide 1  "Trees" is written over a tree that grows node by node (root on top: computer scientists
 *            draw trees upside down), then the author line and date
 *   Slide 2  five cards, one after the other, each with its own small picture: manager-employee relation
 *            (org chart), Google Maps (routes fanning out from where you are), planetary hierarchy
 *            (universe down to Earth), modeling computation (a call tree) and expression evaluation
 *            (an expression tree)
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeIntroScene extends PDSTreeClipBase {

    @Override
    public void construct() {
        titleCard();
        useCases();
    }

    // ── slide 1 ──────────────────────────────────────────────────────

    private void titleCard() {
        List<MObject> mine = new ArrayList<>();
        StrokeTextMob title = stroke("Trees", 0, -330, 150, Colors.WHITE, true);
        play(new Write(title, d(2.4)));
        mine.add(title);
        pause(0.3);

        GN root = g("", g("", g(""), g("")), g("", g("")), g("", g(""), g(""), g("")));
        GT t = tree(root, 0, -130, 105, 26, 24, 1, false);
        t.build(0.25);
        mine.addAll(t.parts());
        pause(0.4);

        StrokeTextMob cap = stroke("Yes, we draw it upside down: the root is on top.", 0, 170, 36, Colors.LIGHT_GRAY, false);
        play(new Write(cap, d(2.6)));
        mine.add(cap);
        pause(0.5);

        StrokeTextMob name = stroke("Rupesh Nasre.", 0, 320, 56, Colors.WHITE, false);
        play(new Write(name, d(2.0)));
        TextMob date = label("August 2021", 0, 425, 30, Colors.GRAY, false, false);
        play(new FadeIn(date, d(0.8)));
        mine.add(name);
        mine.add(date);
        pause(2.4);
        fadeOutAll(1.2, mine);
        pause(0.3);
    }

    // ── slide 2 ──────────────────────────────────────────────────────

    private List<MObject> card(double cx, double cy, double w, double h, String title, Color c) {
        RectMob p = panel(cx, cy, w, h, c, 0.09);
        TextMob t = label(title, cx, cy - h / 2 + 36, 31, c, false, true);
        List<MObject> l = new ArrayList<>();
        l.add(p);
        l.add(t);
        return l;
    }

    private void showCard(List<MObject> card) {
        List<Animation> a = new ArrayList<>();
        for (MObject m : card) a.add(new FadeIn(m, d(0.6)));
        playAll(a);
    }

    private void useCases() {
        List<MObject> mine = new ArrayList<>();

        RectMob pillBg = new RectMob(230, 74).setCornerRadius(74);
        pillBg.setFillColor(Colors.withAlpha(Colors.ORANGE, 0.4));
        pillBg.setStrokeColor(Colors.ORANGE);
        pillBg.setStrokeWidth(3);
        pillBg.setPosition(0, -470);
        pillBg.setOpacity(0);
        add(pillBg);
        TextMob pillTxt = label("Trees", 0, -470, 46, Colors.WHITE, false, true);
        play(new DropIn(pillBg, 60, 0, d(0.7)), new DropIn(pillTxt, 60, 0, d(0.7)));
        mine.add(pillBg);
        mine.add(pillTxt);
        pause(0.3);

        double w = 570, h = 360, top = -225, bot = 190;

        // 1. manager - employee relation: an org chart
        double cx = -610, cy = top;
        List<MObject> c1 = card(cx, cy, w, h, "Manager-Employee Relation", Colors.TEAL);
        showCard(c1);
        GN org = g("", g("", g(""), g("")), g("", g(""), g(""), g("")), g("", g(""), g("")));
        GT ot = tree(org, cx, cy - 65, 88, 22, 18, 1, false);
        ot.build(0.2);
        TextMob oc = label("who reports to whom", cx, cy + 152, 26, Colors.LIGHT_GRAY, false, false);
        play(new FadeIn(oc, d(0.6)));
        mine.addAll(c1);
        mine.addAll(ot.parts());
        mine.add(oc);
        pause(0.6);

        // 2. google maps: routes fan out from where you are
        cx = 0;
        cy = top;
        List<MObject> c2 = card(cx, cy, w, h, "Google Maps", Colors.BLUE);
        showCard(c2);
        mine.addAll(c2);
        double[] vx = {cx - 215, cx - 110, cx - 10, cx + 95, cx + 205};
        double[] hy = {cy - 45, cy + 35, cy + 100};
        List<Animation> roads = new ArrayList<>();
        for (double x : vx) {
            LineMob l = new LineMob(x, cy - 90, x, cy + 130, Colors.withAlpha(Colors.WHITE, 0.2), 3);
            roads.add(new FadeIn(l, d(0.6)));
            mine.add(l);
        }
        for (double y : hy) {
            LineMob l = new LineMob(cx - 235, y, cx + 225, y, Colors.withAlpha(Colors.WHITE, 0.2), 3);
            roads.add(new FadeIn(l, d(0.6)));
            mine.add(l);
        }
        Link river = new Link(new double[]{cx - 235, cx - 60, cx + 60, cx + 225}, new double[]{cy + 75, cy + 60, cy + 95, cy + 70},
                Colors.withAlpha(Colors.BLUE, 0.28), 24, false);
        add(river);
        roads.add(new FadeIn(river, d(0.6)));
        mine.add(river);
        playAll(roads);
        double ox = cx - 110, oy = cy + 100, y1 = cy + 35;
        Link r0 = line(new double[]{ox, ox}, new double[]{oy, y1});
        Link r1 = line(new double[]{ox, cx - 215, cx - 215}, new double[]{y1, y1, cy - 45});
        Link r2 = line(new double[]{ox, cx + 95, cx + 95}, new double[]{y1, y1, cy - 45});
        Link r3 = line(new double[]{cx + 95, cx + 205, cx + 205}, new double[]{y1, y1, cy + 100});
        CircleMob you = dot(ox, oy, 13, Colors.BLUE, Colors.BLUE);
        CircleMob pa = dot(cx - 215, cy - 45, 11, Colors.RED, Colors.RED);
        CircleMob pb = dot(cx + 95, cy - 45, 11, Colors.RED, Colors.RED);
        CircleMob pc = dot(cx + 205, cy + 100, 11, Colors.RED, Colors.RED);
        play(new FadeIn(you, d(0.4)));
        play(new DrawLink(r0, d(0.6)));
        play(new DrawLink(r1, d(0.8)), new DrawLink(r2, d(0.8)));
        play(new FadeIn(pa, d(0.3)), new FadeIn(pb, d(0.3)), new DrawLink(r3, d(0.6)));
        play(new FadeIn(pc, d(0.3)));
        TextMob mc = label("routes fan out like a tree", cx, cy + 158, 26, Colors.LIGHT_GRAY, false, false);
        play(new FadeIn(mc, d(0.6)));
        mine.add(r0);
        mine.add(r1);
        mine.add(r2);
        mine.add(r3);
        mine.add(you);
        mine.add(pa);
        mine.add(pb);
        mine.add(pc);
        mine.add(mc);
        pause(0.6);

        // 3. planetary hierarchy: universe down to earth
        cx = 610;
        cy = top;
        List<MObject> c3 = card(cx, cy, w, h, "Planetary Hierarchy", Colors.PURPLE);
        showCard(c3);
        mine.addAll(c3);
        String[] lev = {"Universe", "Galaxy (Milky Way)", "Star (Sun)", "Solar system", "Planet: Earth"};
        Color[] lc = {Colors.PURPLE, Colors.BLUE, Colors.TEAL, Colors.GREEN, Colors.GOLD};
        List<Animation> drop = new ArrayList<>();
        for (int i = 0; i < lev.length; i++) {
            double bw = 400 - 52 * i;
            RectMob bar = panel(cx, cy - 82 + 52 * i, bw, 44, lc[i], 0.3);
            TextMob bt = label(lev[i], cx, cy - 82 + 52 * i, 25, Colors.WHITE, false, true);
            drop.add(new DropIn(bar, 40, 0.45 * i, d(0.6)));
            drop.add(new DropIn(bt, 40, 0.45 * i, d(0.6)));
            mine.add(bar);
            mine.add(bt);
        }
        playAll(drop);
        pause(0.7);

        // 4. modeling computation: a call tree
        cx = -300;
        cy = bot;
        List<MObject> c4 = card(cx, cy, w, h, "Modeling Computation", Colors.ORANGE);
        showCard(c4);
        mine.addAll(c4);
        GN calls = g("main", g("fn()", g("bar()"), g("foo()")), g("baz()"));
        GT ct = tree(calls, cx, cy - 65, 86, 26, 26, 25, true);
        ct.build(0.25);
        TextMob cc = label("who calls whom", cx, cy + 155, 26, Colors.LIGHT_GRAY, false, false);
        play(new FadeIn(cc, d(0.6)));
        mine.addAll(ct.parts());
        mine.add(cc);
        pause(0.6);

        // 5. expression evaluation: an expression tree
        cx = 300;
        cy = bot;
        List<MObject> c5 = card(cx, cy, w, h, "Expression Evaluation", Colors.GREEN);
        showCard(c5);
        mine.addAll(c5);
        GN ex = g("+", g("/", g("*", g("2"), g("3")), g("−", g("2"), g("1"))),
                g("*", g("5"), g("−", g("4"), g("1"))));
        GT et = tree(ex, cx, cy - 70, 60, 22, 17, 23, false);
        et.build(0.2);
        TextMob ec = label("2*3/(2-1)+5*(4-1)", cx, cy + 155, 26, Colors.LIGHT_GRAY, false, false);
        play(new FadeIn(ec, d(0.6)));
        mine.addAll(et.parts());
        mine.add(ec);
        pause(0.8);

        StrokeTextMob end = stroke("Hierarchies, routes, calls, expressions: all trees.", 0, 462, 40, Colors.GOLD, false);
        play(new Write(end, d(3.0)));
        mine.add(end);
        pause(2.8);
        fadeOutAll(d(1.2), mine);
        pause(0.3);
    }

    private Link line(double[] xs, double[] ys) {
        Link l = new Link(xs, ys, Colors.GOLD, 5, false);
        add(l);
        return l;
    }

    private CircleMob dot(double x, double y, double r, Color fill, Color stroke) {
        CircleMob c = new CircleMob(r);
        c.setFillColor(Colors.withAlpha(fill, 0.8));
        c.setStrokeColor(stroke);
        c.setStrokeWidth(2.5);
        c.setPosition(x, y);
        c.setOpacity(0);
        add(c);
        return c;
    }
}
