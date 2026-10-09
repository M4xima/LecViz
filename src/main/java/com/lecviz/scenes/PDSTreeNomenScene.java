package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Easing;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Standalone clip for slide 3 of the trees deck: nomenclature borrowed from real trees.
 *
 *   - the slide's list one line at a time (root, stem, branches, leaves, fruits, flowers), the brace that
 *     calls stem + branches "edges", and fruits and flowers struck out
 *   - a drawn tree (roots under the ground, a stem, branches, leaves) with each word pointing at its part;
 *     fruits and flowers shown struck out
 *   - the picture flips over: the same shape drawn the way computer scientists draw it, root on top, where
 *     the branches become thin edges and the leaves become leaf nodes
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeNomenScene extends PDSTreeClipBase {

    private static final Color BARK = Color.web("#B07A45");
    private static final Color LEAFY = Color.web("#5FA84E");

    private StrokeTextMob head;

    /** Animates the stroke width of a line. */
    private static final class Widen extends Animation {
        private final double to;
        private double from;

        Widen(MObject m, double to, double dur) {
            super(m, dur, Easing.EASE_IN_OUT);
            this.to = to;
        }

        @Override public void begin() { from = target.getStrokeWidth(); }
        @Override public void interpolate(double t) { target.setStrokeWidth(from + (to - from) * t); }
    }

    @Override
    public void construct() {
        head = writeHeading("Nomenclature");
        pause(0.5);
        words();
        picture();
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    // ── the slide's list ─────────────────────────────────────────────

    private void words() {
        String[] w = {"Root", "Stem", "Branches", "Leaves", "Fruits", "Flowers"};
        List<Spec> s = new ArrayList<>();
        for (String x : w) s.add(ln(0, x));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(0.4);

        // stem + branches are the edges
        double bx = -800 + strokeW("Branches", false, 40) + 40;
        double y1 = -330 + 78 * 1 - 28, y2 = -330 + 78 * 2 + 28;
        Link br = bracketRight(bx, y1, y2, Colors.GOLD);
        StrokeTextMob edges = strokeLeft("Edges", bx + 30, (y1 + y2) / 2, 48, Colors.GOLD);
        play(new DrawLink(br, d(0.7)));
        play(new Write(edges, d(1.2)));
        List<MObject> g1 = new ArrayList<>();
        g1.add(br);
        g1.add(edges);
        text.add(g1);
        pause(0.6);

        // fruits and flowers are crossed out
        for (int k = 4; k <= 5; k++) {
            double y = -330 + 78 * k;
            double wd = strokeW(w[k], false, 40);
            LineMob strike = new LineMob(-808, y + 2, -792 + wd, y + 2, Colors.withAlpha(Colors.RED, 0.95), 4.5);
            add(strike);
            play(new DrawLine(strike, d(0.55)));
            List<MObject> g = new ArrayList<>();
            g.add(strike);
            text.add(g);
            pause(0.3);
        }
        pause(1.4);
        swipeAway(text);
        pause(0.4);
    }

    // ── a real tree, then the computer scientist's tree ──────────────

    private Link bark(double x1, double y1, double x2, double y2, double w) {
        Link l = new Link(new double[]{x1, x2}, new double[]{y1, y2}, BARK, w, false);
        add(l);
        return l;
    }

    private CircleMob blob(double x, double y, double r, Color c) {
        CircleMob b = new CircleMob(r);
        b.setFillColor(Colors.withAlpha(c, 0.55));
        b.setStrokeColor(Colors.withAlpha(c, 0.9));
        b.setStrokeWidth(2.5);
        b.setPosition(x, y);
        b.setOpacity(0);
        add(b);
        return b;
    }

    private void picture() {
        List<MObject> mine = new ArrayList<>();
        GN rootN = g("", g("", g("", g(""), g("")), g(""), g("", g(""))));
        double topY = -250, gap = 150, cx = 150;
        GT t = tree(rootN, cx, topY, gap, 120, 22, 1, false);
        int maxD = 3;
        // the botanical drawing is the computer scientist's tree upside down
        double groundY = topY + maxD * gap;
        GN stemN = rootN.kids.get(0);
        GN a = stemN.kids.get(0), b = stemN.kids.get(1), c = stemN.kids.get(2);

        Link ground = new Link(new double[]{cx - 430, cx + 430}, new double[]{groundY, groundY}, Colors.withAlpha(Colors.GREEN, 0.7), 4, false);
        add(ground);
        List<Link> roots = new ArrayList<>();
        double[][] rt = {{-110, 80}, {-45, 105}, {30, 95}, {105, 70}, {-70, 40}};
        for (double[] r : rt) roots.add(bark(rootN.x, groundY, rootN.x + r[0], groundY + r[1], 5));
        List<Link> edges = new ArrayList<>();
        List<GN> eNodes = new ArrayList<>();
        for (GN n : t.nodes) {
            if (n.parent == null) continue;
            double yb = topY + (maxD - n.depth) * gap, yp = topY + (maxD - n.parent.depth) * gap;
            double wd = n.depth == 1 ? 24 : n.depth == 2 ? 14 : 9;
            Link l = bark(n.parent.x, yp, n.x, yb, wd);
            edges.add(l);
            eNodes.add(n);
        }
        List<CircleMob> blobs = new ArrayList<>();
        List<GN> leafNodes = new ArrayList<>();
        for (GN n : t.nodes) {
            if (!n.leaf()) continue;
            double yb = topY + (maxD - n.depth) * gap;
            blobs.add(blob(n.x, yb, 34, LEAFY));
            blobs.add(blob(n.x - 26, yb + 14, 20, LEAFY));
            blobs.add(blob(n.x + 26, yb + 14, 20, LEAFY));
            leafNodes.add(n);
        }

        play(new DrawLink(ground, d(0.8)));
        List<Animation> an = new ArrayList<>();
        for (int i = 0; i < roots.size(); i++) an.add(new DrawLinkAt(roots.get(i), 0.12 * i, d(0.7)));
        playAll(an);
        pause(0.2);
        an = new ArrayList<>();
        for (int i = 0; i < edges.size(); i++) an.add(new DrawLinkAt(edges.get(i), 0.35 * i, d(0.8)));
        playAll(an);
        an = new ArrayList<>();
        for (int i = 0; i < blobs.size(); i++) an.add(new FadeInAt(blobs.get(i), 0.05 * i, d(0.6)));
        playAll(an);
        mine.add(ground);
        mine.addAll(roots);
        mine.addAll(edges);
        mine.addAll(blobs);
        pause(0.5);

        // each word points at its part
        List<MObject> lab = new ArrayList<>();
        double lx = -520;
        labelPart("Root", lx, groundY + 70, rootN.x - 70, groundY + 55, Colors.GOLD, lab);
        double sy = (groundY + topY + (maxD - 1) * gap) / 2;
        labelPart("Stem", lx, sy + 20, rootN.x - 14, sy, Colors.GOLD, lab);
        double by = (topY + (maxD - 1) * gap + topY + (maxD - 2) * gap) / 2;
        labelPart("Branches", lx, by - 20, (stemN.x + a.x) / 2 - 8, by, Colors.GOLD, lab);
        double ly = topY + (maxD - 3) * gap;
        labelPart("Leaves", lx, ly + 10, a.x - 48, ly + 6, Colors.GOLD, lab);
        mine.addAll(lab);

        // fruits and flowers are not part of it
        TextMob fr = label("Fruits", 720, -110, 38, Colors.GRAY, false, true);
        TextMob fl = label("Flowers", 720, -40, 38, Colors.GRAY, false, true);
        play(new FadeIn(fr, d(0.5)), new FadeIn(fl, d(0.5)));
        LineMob s1 = new LineMob(720 - 55, -108, 720 + 55, -108, Colors.withAlpha(Colors.RED, 0.95), 4.5);
        LineMob s2 = new LineMob(720 - 75, -38, 720 + 75, -38, Colors.withAlpha(Colors.RED, 0.95), 4.5);
        add(s1);
        add(s2);
        play(new DrawLine(s1, d(0.5)));
        play(new DrawLine(s2, d(0.5)));
        TextMob note = label("not needed for us", 720, 25, 26, Colors.GRAY, false, false);
        play(new FadeIn(note, d(0.6)));
        mine.add(fr);
        mine.add(fl);
        mine.add(s1);
        mine.add(s2);
        mine.add(note);
        pause(1.6);

        // flip it over
        StrokeTextMob cap = stroke("Computer scientists draw it the other way up.", 0, 440, 40, Colors.ORANGE, false);
        play(new Write(cap, d(2.8)));
        mine.add(cap);
        pause(0.6);
        List<Animation> out = new ArrayList<>();
        for (MObject m : lab) out.add(new FadeOut(m, d(0.6)));
        out.add(new FadeOut(fr, d(0.6)));
        out.add(new FadeOut(fl, d(0.6)));
        out.add(new FadeOut(s1, d(0.6)));
        out.add(new FadeOut(s2, d(0.6)));
        out.add(new FadeOut(note, d(0.6)));
        out.add(new FadeOut(ground, d(0.8)));
        for (Link r : roots) out.add(new FadeOut(r, d(0.8)));
        playAll(out);
        List<Animation> flip = new ArrayList<>();
        for (int i = 0; i < edges.size(); i++) {
            GN n = eNodes.get(i);
            double[][] q = t.route(n.parent, n);
            flip.add(new LinkTo(edges.get(i), q[0], q[1], d(2.0)));
            flip.add(new Widen(edges.get(i), 3, d(2.0)));
            flip.add(new ColorChange(edges.get(i), Colors.withAlpha(Colors.LIGHT_GRAY, 0.8), d(2.0), ColorChange.Target.STROKE));
        }
        for (int i = 0; i < blobs.size(); i++) {
            GN n = leafNodes.get(i / 3);
            int k = i % 3;
            flip.add(new MoveTo(blobs.get(i), n.x, n.y, d(2.0)).setEasing(Easing.EASE_IN_OUT));
            flip.add(new ScaleTo(blobs.get(i), k == 0 ? 0.6 : 0.01, d(2.0)));
        }
        playAll(flip);
        // the nodes appear on top of what was there
        List<Animation> sw = new ArrayList<>();
        for (GN n : t.nodes) {
            sw.add(new FadeIn(n.shape, d(0.7)));
        }
        for (CircleMob bl : blobs) sw.add(new FadeOut(bl, d(0.7)));
        playAll(sw);
        mine.addAll(t.parts());
        pause(0.4);

        // the same words, now for the drawn tree
        List<MObject> lab2 = new ArrayList<>();
        labelPart("root", lx, -250, rootN.x - 28, -250, Colors.GOLD, lab2);
        labelPart("edges", lx, -90, (stemN.x + a.x) / 2 - 6, topY + 1.5 * gap, Colors.GOLD, lab2);
        labelPart("leaves", lx, 120, a.x - 28, topY + 3 * gap, Colors.GOLD, lab2);
        mine.addAll(lab2);
        pause(0.4);
        StrokeTextMob fin = stroke("Same parts, new picture.", 0, 330, 44, Colors.GREEN, false);
        play(new FadeOut(cap, d(0.5)));
        play(new Write(fin, d(2.0)));
        mine.add(fin);
        pause(2.6);
        fadeOutAll(d(1.2), mine);
        pause(0.3);
    }

    /** A word on the left with an arrow to the part it names. */
    private void labelPart(String word, double lx, double ly, double tx, double ty, Color c, List<MObject> made) {
        TextMob t = label(word, lx, ly, 40, c, false, true);
        double half = measure(word, "SansSerif", 40, true) / 2;
        Link l = arrow(lx + half + 14, ly, tx, ty, Colors.withAlpha(c, 0.9), 3);
        play(new FadeIn(t, d(0.4)), new DrawLink(l, d(0.7)));
        made.add(t);
        made.add(l);
        pause(0.3);
    }
}
