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
import java.util.function.Function;

/**
 * Shared kit for the tree clips, on top of the list kit (nodes, links, stacks, call stacks, narration):
 *
 *   - GN / GT: a general tree (any number of children) that lays itself out (leaves evenly spaced, every
 *     parent centered over its children), draws as circles or name pills joined by edges, builds level by
 *     level, recolors node by node, tags nodes with small labels, moves and mirrors as one object
 *   - a few trees the lecture keeps coming back to (the A..N tree, the directory tree, a small seven node
 *     tree for code walk-throughs)
 *   - OutStrip: the "output so far" row of chips that traversals write into
 *   - badge / ripple helpers for the numbered question style
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public abstract class PDSTreeClipBase extends PDSListClipBase {

    // ── palette for node roles ───────────────────────────────────────

    protected static final Color ROOT_C = Colors.MAROON;     // the root
    protected static final Color INNER_C = Colors.ORANGE;    // internal nodes
    protected static final Color LEAF_C = Colors.BLUE;       // leaves
    protected static final Color VISIT_C = Colors.GOLD;      // being visited right now
    protected static final Color DONE_C = Colors.GREEN;      // already processed

    // ── tree description ─────────────────────────────────────────────

    protected static final class GN {
        final String name;
        final List<GN> kids = new ArrayList<>();
        GN parent;
        int depth;
        double x, y, hw, hh;
        Color color = LEAF_C;
        MObject shape;
        TextMob text;
        Link edge;

        GN(String name) { this.name = name; }

        boolean leaf() { return kids.isEmpty(); }
    }

    protected static GN g(String name, GN... kids) {
        GN n = new GN(name);
        for (GN k : kids) {
            k.parent = n;
            n.kids.add(k);
        }
        return n;
    }

    /** The node reached from {@code from} by following the given child indexes. */
    protected static GN at(GN from, int... path) {
        GN n = from;
        for (int i : path) n = n.kids.get(i);
        return n;
    }

    protected static void preorderInto(GN n, List<GN> out) {
        out.add(n);
        for (GN k : n.kids) preorderInto(k, out);
    }

    protected static void postorderInto(GN n, List<GN> out) {
        for (GN k : n.kids) postorderInto(k, out);
        out.add(n);
    }

    protected static int height(GN n) {
        int h = 0;
        for (GN k : n.kids) h = Math.max(h, 1 + height(k));
        return h;
    }

    // ── standard trees ───────────────────────────────────────────────

    /** A (B C D E F G); D (H I); F (J K L); J (M N) — the tree on the implementation slide. */
    protected static GN idTree() {
        return g("A", g("B"), g("C"), g("D", g("H"), g("I")), g("E"),
                g("F", g("J", g("M"), g("N")), g("K"), g("L")), g("G"));
    }

    /** A small tree for code walk-throughs: A (B (E F) C D (G)). */
    protected static GN smallTree() {
        return g("A", g("B", g("E"), g("F")), g("C"), g("D", g("G")));
    }

    /** The directory tree of the lecture (slides 11, 14, 15, 16). */
    protected static GN dirTree() {
        return g("/", g("home",
                g("somesh",
                        g("acad", g("1.c"), g("2.c"), g("3.c")),
                        g("intern", g("ibm", g("first", g("readme")), g("second", g("readme")), g("third", g("readme")))),
                        g("test.c")),
                g("saurabh", g("cv.pdf")),
                g("jk", g("cs1100"),
                        g("spw",
                                g("bintree", g("1.cpp"), g("2.cpp"), g("trees.pdf")),
                                g("searchtree", g("1.cpp"), g("2.cpp"), g("bst.pdf"))))));
    }

    // ── a drawn general tree ─────────────────────────────────────────

    protected final class GT {
        final GN root;
        final List<GN> nodes = new ArrayList<>();   // preorder
        final boolean pill, arrows;
        final double r, fs;
        double cx, topY, levelGap, slotGap;
        private double cursor;

        /**
         * @param colorOf  node color by role (null = leaves blue, internal orange, root maroon)
         */
        GT(GN root, double cx, double topY, double levelGap, double slotGap, double r, double fs,
           boolean arrows, Function<GN, Color> colorOf) {
            this.root = root;
            this.cx = cx;
            this.topY = topY;
            this.levelGap = levelGap;
            this.slotGap = slotGap;
            this.r = r;
            this.fs = fs;
            this.arrows = arrows;
            preorderInto(root, nodes);
            boolean anyLong = false;
            for (GN n : nodes) if (n.name.length() > 2) anyLong = true;
            pill = anyLong;
            assignDepth(root, 0);
            for (GN n : nodes) {
                double tw = measure(n.name, "SansSerif", fs, true);
                n.hh = r;
                n.hw = pill ? Math.max(r, (tw + 34) / 2) : r;
                n.color = colorOf != null ? colorOf.apply(n) : (n == root ? ROOT_C : n.leaf() ? LEAF_C : INNER_C);
            }
            cursor = 0;
            place(root, slotGap);
            double mn = Double.MAX_VALUE, mx = -Double.MAX_VALUE;
            for (GN n : nodes) {
                mn = Math.min(mn, n.x - n.hw);
                mx = Math.max(mx, n.x + n.hw);
            }
            double dx = cx - (mn + mx) / 2;
            for (GN n : nodes) {
                n.x += dx;
                n.y = topY + n.depth * levelGap;
            }
            for (GN n : nodes) makeViews(n);
            for (GN n : nodes) if (n.parent != null) n.edge = makeEdge(n.parent, n);
        }

        private void assignDepth(GN n, int d) {
            n.depth = d;
            for (GN k : n.kids) assignDepth(k, d + 1);
        }

        private void place(GN n, double slot) {
            if (n.kids.isEmpty()) {
                n.x = cursor + n.hw;
                cursor += 2 * n.hw + slot;
            } else {
                for (GN k : n.kids) place(k, slot);
                n.x = (n.kids.get(0).x + n.kids.get(n.kids.size() - 1).x) / 2;
            }
        }

        private void makeViews(GN n) {
            if (pill) {
                RectMob s = new RectMob(2 * n.hw, 2 * n.hh).setCornerRadius(2 * n.hh);
                s.setFillColor(Colors.withAlpha(n.color, 0.28));
                s.setStrokeColor(Colors.withAlpha(n.color, 0.95));
                s.setStrokeWidth(3);
                n.shape = s;
            } else {
                CircleMob s = new CircleMob(n.hh);
                s.setFillColor(Colors.withAlpha(n.color, 0.28));
                s.setStrokeColor(Colors.withAlpha(n.color, 0.95));
                s.setStrokeWidth(3);
                n.shape = s;
            }
            n.shape.setPosition(n.x, n.y);
            n.shape.setOpacity(0);
            add(n.shape);
            n.text = label(n.name, n.x, n.y, fs, Colors.WHITE, false, true);
        }

        double[][] route(GN p, GN c) {
            double sx = p.x, sy = p.y + p.hh, ex = c.x, ey = c.y - c.hh - (arrows ? 4 : 0);
            if (!pill) {
                double dx = c.x - p.x, dy = c.y - p.y, len = Math.hypot(dx, dy);
                double ux = dx / len, uy = dy / len;
                sx = p.x + ux * p.hh;
                sy = p.y + uy * p.hh;
                ex = c.x - ux * (c.hh + (arrows ? 4 : 0));
                ey = c.y - uy * (c.hh + (arrows ? 4 : 0));
            }
            return pts(sx, sy, ex, ey);
        }

        private Link makeEdge(GN p, GN c) {
            double[][] q = route(p, c);
            Link l = new Link(q[0], q[1], Colors.withAlpha(Colors.LIGHT_GRAY, 0.8), 3, arrows);
            add(l);
            return l;
        }

        List<MObject> parts() {
            List<MObject> l = new ArrayList<>();
            for (GN n : nodes) {
                l.add(n.shape);
                l.add(n.text);
                if (n.edge != null) l.add(n.edge);
            }
            return l;
        }

        /** Level by level: a level's nodes fade in while the edges leading to them are drawn. */
        void build(double perLevelPause) {
            int h = height(root);
            for (int dep = 0; dep <= h; dep++) {
                List<Animation> a = new ArrayList<>();
                for (GN n : nodes) {
                    if (n.depth != dep) continue;
                    a.add(new FadeInAt(n.shape, 0, d(0.5)));
                    a.add(new FadeInAt(n.text, 0, d(0.5)));
                    if (n.edge != null) a.add(new DrawLinkAt(n.edge, 0, d(0.5)));
                }
                playAll(a);
                pause(perLevelPause);
            }
        }

        /** Everything fades in together (edges drawn on). */
        void buildAll(double dur) {
            List<Animation> a = new ArrayList<>();
            showAnim(a, 0, dur);
            playAll(a);
        }

        void showAnim(List<Animation> a, double delay, double dur) {
            for (GN n : nodes) {
                a.add(new FadeInAt(n.shape, delay, dur));
                a.add(new FadeInAt(n.text, delay, dur));
                if (n.edge != null) a.add(new DrawLinkAt(n.edge, delay, dur));
            }
        }

        /** One node (and the edge leading to it) appears. */
        void showNode(List<Animation> a, GN n, double delay, double dur) {
            a.add(new FadeInAt(n.shape, delay, dur));
            a.add(new FadeInAt(n.text, delay, dur));
            if (n.edge != null) a.add(new DrawLinkAt(n.edge, delay, dur));
        }

        void hideNode(List<Animation> a, GN n, double dur) {
            a.add(new FadeOut(n.shape, dur));
            a.add(new FadeOut(n.text, dur));
            if (n.edge != null) a.add(new FadeOut(n.edge, dur));
        }

        /** Recolor a node (fill and outline). */
        void paint(List<Animation> a, GN n, Color c, double dur) {
            a.add(new ColorChange(n.shape, Colors.withAlpha(c, 0.42), dur));
            a.add(new ColorChange(n.shape, Colors.withAlpha(c, 0.98), dur, ColorChange.Target.STROKE));
            n.color = c;
        }

        void paintDefault(List<Animation> a, GN n, double dur) {
            paint(a, n, n == root ? ROOT_C : n.leaf() ? LEAF_C : INNER_C, dur);
        }

        void paintEdge(List<Animation> a, GN n, Color c, double dur) {
            if (n.edge != null) a.add(new ColorChange(n.edge, c, dur, ColorChange.Target.STROKE));
        }

        void paintNow(GN n, Color c, double dur) {
            List<Animation> a = new ArrayList<>();
            paint(a, n, c, dur);
            playAll(a);
        }

        /** A quick pop: the node grows a little and settles. */
        void pop(GN n) {
            play(new ScaleTo(n.shape, 1.22, d(0.18)), new ScaleTo(n.text, 1.22, d(0.18)));
            play(new ScaleTo(n.shape, 1.0, d(0.22)), new ScaleTo(n.text, 1.0, d(0.22)));
        }

        /** Small label beside a node (an order number, a size, a depth). Created hidden. */
        TextMob tag(GN n, String s, Color c, double size, double dx, double dy) {
            return label(s, n.x + dx, n.y + dy, size, c, false, true);
        }

        /** Moves every node and edge by (dx, dy). */
        void shiftAnim(List<Animation> a, double dx, double dy, double dur) {
            double[][] xy = new double[nodes.size()][2];
            for (int i = 0; i < nodes.size(); i++) {
                xy[i][0] = nodes.get(i).x + dx;
                xy[i][1] = nodes.get(i).y + dy;
            }
            relocate(a, xy, dur);
            cx += dx;
            topY += dy;
        }

        /** Left-right mirror about the tree's center line. */
        void mirrorAnim(List<Animation> a, double dur) {
            double[][] xy = new double[nodes.size()][2];
            for (int i = 0; i < nodes.size(); i++) {
                xy[i][0] = 2 * cx - nodes.get(i).x;
                xy[i][1] = nodes.get(i).y;
            }
            relocate(a, xy, dur);
        }

        /** Lays the tree out again for the current order of every node's children, and moves everything there. */
        void relayout(List<Animation> a, double dur) {
            cursor = 0;
            place(root, slotGap);
            double mn = Double.MAX_VALUE, mx = -Double.MAX_VALUE;
            for (GN n : nodes) {
                mn = Math.min(mn, n.x - n.hw);
                mx = Math.max(mx, n.x + n.hw);
            }
            double dx = cx - (mn + mx) / 2;
            double[][] xy = new double[nodes.size()][2];
            for (int i = 0; i < nodes.size(); i++) {
                xy[i][0] = nodes.get(i).x + dx;
                xy[i][1] = nodes.get(i).y;
            }
            relocate(a, xy, dur);
        }

        /** Moves node i to xy[i] (same order as {@code nodes}); edges follow. */
        void relocate(List<Animation> a, double[][] xy, double dur) {
            for (int i = 0; i < nodes.size(); i++) {
                GN n = nodes.get(i);
                a.add(new MoveTo(n.shape, xy[i][0], xy[i][1], dur).setEasing(Easing.EASE_IN_OUT));
                a.add(new MoveTo(n.text, xy[i][0], xy[i][1], dur).setEasing(Easing.EASE_IN_OUT));
            }
            double[] ox = new double[nodes.size()], oy = new double[nodes.size()];
            for (int i = 0; i < nodes.size(); i++) {
                ox[i] = nodes.get(i).x;
                oy[i] = nodes.get(i).y;
                nodes.get(i).x = xy[i][0];
                nodes.get(i).y = xy[i][1];
            }
            for (GN n : nodes) {
                if (n.edge == null) continue;
                double[][] q = route(n.parent, n);
                a.add(new LinkTo(n.edge, q[0], q[1], dur));
            }
        }
    }

    protected GT tree(GN root, double cx, double topY, double levelGap, double slotGap, double r, double fs, boolean arrows) {
        return new GT(root, cx, topY, levelGap, slotGap, r, fs, arrows, null);
    }

    // ── "output so far" strip ────────────────────────────────────────

    /** A row of chips that a traversal appends to, one name at a time. */
    protected final class OutStrip {
        final double y, h;
        final List<MObject> made = new ArrayList<>();
        final TextMob title;
        final Color color;
        double nextX;
        final double left;

        OutStrip(String name, double left, double y, double h, Color c) {
            this.left = left;
            this.y = y;
            this.h = h;
            this.color = c;
            title = label(name, left, y, 34, c, true, true);
            made.add(title);
            nextX = left + measure(name, "SansSerif", 34, true) + 26;
        }

        void showTitle() { play(new FadeIn(title, d(0.5))); }

        /** Appends a chip; the chip drops in from above. */
        TextMob add(String s) { return add(s, color); }

        TextMob add(String s, Color c) {
            double w = Math.max(h, measure(s, "SansSerif", h * 0.5, true) + 30);
            List<MObject> ch = chip(s, nextX + w / 2, y, w, h, c, h * 0.5);
            nextX += w + 10;
            List<Animation> a = new ArrayList<>();
            for (MObject m : ch) a.add(new DropIn(m, 40, 0, d(0.45)));
            playAll(a);
            made.addAll(ch);
            return (TextMob) ch.get(1);
        }

        void clear() {
            List<MObject> l = new ArrayList<>(made);
            l.remove(title);
            fadeOutAll(d(0.4), l);
            made.clear();
            made.add(title);
            nextX = left + measure(title.getText(), "SansSerif", 34, true) + 26;
        }

        List<MObject> all() { return made; }
    }

    // ── numbered badges and ripples ──────────────────────────────────

    protected static final class Ripple extends Animation {
        private final CircleMob c;
        private final double r0, r1;

        Ripple(CircleMob c, double r0, double r1, double dur) {
            super(c, dur, Easing.EASE_OUT);
            this.c = c;
            this.r0 = r0;
            this.r1 = r1;
        }

        @Override public void begin() { c.setRadius(r0); c.setOpacity(0.9); }

        @Override
        public void interpolate(double t) {
            c.setRadius(r0 + (r1 - r0) * t);
            c.setOpacity(0.9 * (1 - t));
        }
    }

    /** An expanding ring at (x, y) that fades away. */
    protected void ripple(double x, double y, double r0, double r1, Color c) {
        CircleMob ring = new CircleMob(r0);
        ring.setFillColor(Color.TRANSPARENT);
        ring.setStrokeColor(c);
        ring.setStrokeWidth(4);
        ring.setPosition(x, y);
        ring.setOpacity(0);
        add(ring);
        play(new Ripple(ring, r0, r1, d(0.7)));
        remove(ring);
    }

    /** A numbered badge (disc + number), created hidden. */
    protected List<MObject> badge(int n, double x, double y, double r, Color c) {
        CircleMob disc = new CircleMob(r);
        disc.setFillColor(Colors.withAlpha(c, 0.35));
        disc.setStrokeColor(c);
        disc.setStrokeWidth(3);
        disc.setPosition(x, y);
        disc.setOpacity(0);
        add(disc);
        TextMob t = label(String.valueOf(n), x, y, r * 1.1, Colors.WHITE, false, true);
        List<MObject> l = new ArrayList<>();
        l.add(disc);
        l.add(t);
        return l;
    }

    /** The badge scales in with a ripple. */
    protected void popBadge(List<MObject> b, double r, Color c) {
        for (MObject m : b) m.setScale(0.2);
        List<Animation> a = new ArrayList<>();
        for (MObject m : b) {
            a.add(new FadeIn(m, d(0.35)));
            a.add(new ScaleTo(m, 1.0, d(0.45)).setEasing(Easing.EASE_OUT));
        }
        playAll(a);
        ripple(b.get(0).getPosition().x(), b.get(0).getPosition().y(), r, r * 2.4, c);
    }

    /** A short bracket on the right of a stretch of lines, with a label beside it. */
    protected Link bracketRight(double x, double y1, double y2, Color c) {
        Link l = new Link(new double[]{x - 20, x, x, x - 20}, new double[]{y1, y1, y2, y2}, c, 3.2, false);
        add(l);
        return l;
    }

    /** A dashed-looking box outline built from short segments, hidden until faded in. */
    protected RectMob outline(double cx, double cy, double w, double h, Color c) {
        RectMob r = new RectMob(w, h).setCornerRadius(20);
        r.setFillColor(Colors.withAlpha(c, 0.07));
        r.setStrokeColor(Colors.withAlpha(c, 0.8));
        r.setStrokeWidth(2.4);
        r.setPosition(cx, cy);
        r.setOpacity(0);
        add(r);
        return r;
    }
    /** A rounded box around the whole subtree of n (pads in pixels), hidden until faded in. */
    protected RectMob subBox(GN n, Color c, double padX, double padTop, double padBottom) {
        List<GN> sub = new ArrayList<>();
        preorderInto(n, sub);
        double x0 = Double.MAX_VALUE, x1 = -Double.MAX_VALUE, y0 = Double.MAX_VALUE, y1 = -Double.MAX_VALUE;
        for (GN m : sub) {
            x0 = Math.min(x0, m.x - m.hw);
            x1 = Math.max(x1, m.x + m.hw);
            y0 = Math.min(y0, m.y - m.hh);
            y1 = Math.max(y1, m.y + m.hh);
        }
        return outline((x0 + x1) / 2, (y0 - padTop + y1 + padBottom) / 2, x1 - x0 + 2 * padX, y1 - y0 + padTop + padBottom, c);
    }
    // ── the numbered question / solution style ───────────────────────

    /**
     * One classwork question: its badge pops in with a ripple, the text is written pen-stroke, key words get
     * a highlighter band. Returns everything it made.
     */
    protected List<MObject> question(int num, String text, double y, double size, String[] kws, Color kwc) {
        List<MObject> made = new ArrayList<>();
        List<MObject> b = badge(num, -850, y, 28, Colors.GOLD);
        popBadge(b, 28, Colors.GOLD);
        made.addAll(b);
        double left = -790;
        StrokeTextMob t = strokeLeft(text, left, y, size, Colors.WHITE);
        play(new Write(t, d(Math.max(1.4, text.length() * 0.058))));
        made.add(t);
        for (String kw : kws) {
            int idx = text.indexOf(kw);
            double pre = strokeW(text.substring(0, idx), false, size);
            double kwW = strokeW(text.substring(0, idx + kw.length()), false, size) - pre;
            RectMob band = new RectMob(kwW + 14, size * 1.3).setCornerRadius(8);
            band.setFillColor(Colors.withAlpha(kwc, 0.34));
            band.setStrokeColor(Color.TRANSPARENT);
            band.setOpacity(0);
            band.setPosition(left + pre + kwW / 2, y);
            add(band);
            play(new GrowRight(band, left + pre - 7, y, kwW + 14, size * 1.3, d(0.6)));
            made.add(band);
        }
        return made;
    }

    /** A gray sub-line under a question; fades in. */
    protected TextMob subLine(String text, double y, double size) {
        TextMob t = label(text, -790, y, size, Colors.GRAY, true, false);
        play(new FadeIn(t, d(0.6)));
        return t;
    }

    /** The header of a solution section: a numbered badge and a bold pen-stroke title. Returns what it made. */
    protected List<MObject> solutionHeader(int num, String title, double y) {
        List<MObject> made = new ArrayList<>();
        List<MObject> b = badge(num, -850, y, 28, Colors.GOLD);
        popBadge(b, 28, Colors.GOLD);
        made.addAll(b);
        StrokeTextMob t = stroke(title, -790 + strokeW(title, true, 46) / 2, y, 46, Colors.WHITE, true);
        play(new Write(t, d(Math.max(1.4, title.length() * 0.07))));
        made.add(t);
        return made;
    }
}
