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
 * Standalone clip for slides 30-31 of the arrays deck: the summary
 * of data structures and "DSAP usage".
 *
 *   Slide 30  the list is written line by line in the pen-stroke style, then swipes off and the
 *             same list becomes a map: a card with a small picture for every structure, children
 *             hanging under their parents, and Array ticked off as this lecture's topic
 *   Slide 31  the four points come one by one, then each gets its picture: static data (an array
 *             is enough), dynamic data (nodes arriving and leaving), the problem dictating both
 *             algorithm and data structure, and algorithms using data structures as tools
 *   End       "made by Karthik and Tejaswi" end card
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSWrapUpScene extends PDSSortClipBase {

    private StrokeTextMob head;

    @Override
    public void construct() {
        summary();
        dsapUsage();
        madeBy();
    }

    // ── small drawing helpers (everything starts invisible) ──────────

    private RectMob rect(double x, double y, double w, double h, Color c, double fill) {
        RectMob r = new RectMob(w, h).setCornerRadius(6);
        r.setFillColor(Colors.withAlpha(c, fill));
        r.setStrokeColor(Colors.withAlpha(c, 0.85));
        r.setStrokeWidth(2.5);
        r.setPosition(x, y);
        r.setOpacity(0);
        add(r);
        return r;
    }

    private CircleMob circ(double x, double y, double rad, Color c, double fill) {
        CircleMob o = new CircleMob(rad);
        o.setFillColor(Colors.withAlpha(c, fill));
        o.setStrokeColor(Colors.withAlpha(c, 0.9));
        o.setStrokeWidth(2.5);
        o.setPosition(x, y);
        o.setOpacity(0);
        add(o);
        return o;
    }

    private LineMob line(double x1, double y1, double x2, double y2, Color c, double w) {
        LineMob l = new LineMob(x1, y1, x2, y2, c, w);
        add(l);
        return l;
    }

    private ArrowMob arrow(double x1, double y1, double x2, double y2, Color c) {
        ArrowMob a = new ArrowMob(x1, y1, x2, y2);
        a.setStrokeColor(c);
        a.setStrokeWidth(3);
        a.setHeadLength(10);
        a.setOpacity(0);
        add(a);
        return a;
    }

    private TextMob tiny(String s, double x, double y, double size, Color c) {
        return label(s, x, y, size, c, false, true);
    }

    // ── slide 30: the summary ────────────────────────────────────────

    private void summary() {
        head = writeHeading("Summary");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Array"));
        s.add(ln(0, "Linked List"));
        s.add(ln(1, "Stack"));
        s.add(ln(1, "Queue"));
        s.add(ln(0, "Tree"));
        s.add(ln(1, "Binary Tree"));
        s.add(ln(1, "Binary Search Tree"));
        s.add(ln(1, "Heap"));
        s.add(ln(1, "…"));
        s.add(ln(0, "Hash Table"));
        s.add(ln(0, "Graph"));
        List<List<MObject>> text = writeSlide(s, -370);
        pause(1.2);
        swipeAway(text);
        pause(0.4);

        List<MObject> mine = new ArrayList<>();
        List<Animation> in = new ArrayList<>();
        double y1 = -230, y2 = 40;
        String[] nameTop = {"Array", "Linked List", "Tree", "Hash Table", "Graph"};
        double[] xTop = {-740, -410, 160, 540, 800};
        String[] kindTop = {"array", "list", "tree", "hash", "graph"};
        Color[] colTop = {Colors.TEAL, Colors.GOLD, Colors.BLUE, Colors.PINK, Colors.PURPLE};
        String[] nameSub = {"Stack", "Queue", "Binary Tree", "Binary Search Tree", "Heap", "…"};
        double[] xSub = {-520, -300, -60, 160, 380, 590};
        String[] kindSub = {"stack", "queue", "btree", "bst", "heap", "dots"};
        Color[] colSub = {Colors.GOLD, Colors.GOLD, Colors.BLUE, Colors.BLUE, Colors.BLUE, Colors.GRAY};
        int[] parent = {1, 1, 2, 2, 2, 2};

        // row one, one card at a time
        List<RectMob> topCard = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<MObject> card = card(nameTop[i], kindTop[i], xTop[i], y1, 230, 190, colTop[i]);
            topCard.add((RectMob) card.get(0));
            double start = 0.55 * i;
            for (MObject m : card) in.add(new FadeInAt(m, start, d(0.6)));
            mine.addAll(card);
        }
        playAll(in);
        pause(0.5);
        in.clear();

        // the children hang under their parents
        List<Animation> kids = new ArrayList<>();
        for (int k = 0; k < 6; k++) {
            List<MObject> card = card(nameSub[k], kindSub[k], xSub[k], y2, k == 5 ? 120 : (k == 3 ? 220 : 190), 150, colSub[k]);
            double start = 0.35 * k;
            for (MObject m : card) kids.add(new FadeInAt(m, start + 0.3, d(0.6)));
            mine.addAll(card);
            double px = xTop[parent[k]];
            LineMob conn = line(px, y1 + 95, xSub[k], y2 - 75, Colors.withAlpha(Colors.LIGHT_GRAY, 0.6), 3);
            kids.add(new DrawLine(conn, d(0.7)));
            mine.add(conn);
        }
        playAll(kids);
        pause(0.8);

        // Array is this lecture's topic
        LineMob c1 = line(xTop[0] + 82, y1 - 72, xTop[0] + 98, y1 - 56, Colors.GREEN, 6);
        LineMob c2 = line(xTop[0] + 98, y1 - 56, xTop[0] + 128, y1 - 92, Colors.GREEN, 6);
        play(new DrawLine(c1, d(0.4)));
        play(new DrawLine(c2, d(0.4)));
        mine.add(c1);
        mine.add(c2);
        StrokeTextMob cap = stroke("Array: done — the rest of the course builds on it.", 0, 260, 32, Colors.GREEN, false);
        play(new Write(cap, d(2.6)));
        mine.add(cap);
        StrokeTextMob cap2 = stroke("Next: linked lists and the structures built on them, trees, hash tables, graphs …", 0, 320, 28, Colors.LIGHT_GRAY, false);
        play(new Write(cap2, d(3.2)));
        mine.add(cap2);
        pause(3.0);
        fadeOutAll(d(1.0), mine);
        fadeOutAll(d(0.6), head);
        pause(0.3);
    }

    /** A card with a small picture and its name; element 0 of the result is the card rectangle. */
    private List<MObject> card(String name, String kind, double cx, double cy, double w, double h, Color c) {
        List<MObject> made = new ArrayList<>();
        RectMob r = new RectMob(w, h).setCornerRadius(14);
        r.setFillColor(Colors.withAlpha(c, 0.1));
        r.setStrokeColor(Colors.withAlpha(c, 0.7));
        r.setStrokeWidth(2.5);
        r.setPosition(cx, cy);
        r.setOpacity(0);
        add(r);
        made.add(r);
        TextMob nm = label(name, cx, cy + h / 2 - 28, name.length() > 12 ? 22 : 26, Colors.WHITE, false, true);
        made.add(nm);
        double iy = cy - 14;
        switch (kind) {
            case "array" -> {
                for (int i = 0; i < 5; i++) made.add(rect(cx + (i - 2) * 36, iy, 32, 32, c, 0.35));
            }
            case "list" -> {
                for (int i = 0; i < 3; i++) made.add(rect(cx + (i - 1) * 70, iy, 40, 28, c, 0.35));
                for (int i = 0; i < 2; i++) made.add(arrow(cx + (i - 1) * 70 + 24, iy, cx + i * 70 - 24, iy, c));
            }
            case "stack" -> {
                for (int i = 0; i < 3; i++) made.add(rect(cx, iy + 20 - i * 24, 76, 20, c, 0.35));
                made.add(arrow(cx, iy - 52, cx, iy - 30, c));
            }
            case "queue" -> {
                for (int i = 0; i < 4; i++) made.add(rect(cx + (i - 1.5) * 34, iy, 30, 30, c, 0.35));
                made.add(arrow(cx - 92, iy, cx - 70, iy, c));
                made.add(arrow(cx + 70, iy, cx + 92, iy, c));
            }
            case "hash" -> {
                for (int i = 0; i < 4; i++) made.add(rect(cx + 28, iy - 36 + 24 * i, 56, 20, c, 0.35));
                made.add(arrow(cx - 60, iy - 24, cx - 6, iy - 36 + 48, c));
                made.add(arrow(cx - 60, iy + 20, cx - 6, iy - 12, c));
            }
            case "graph" -> {
                double[][] p = {{-50, -26}, {20, -40}, {64, 6}, {-8, 36}, {-60, 20}};
                int[][] e = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 0}, {1, 3}, {0, 2}};
                for (int[] ed : e) {
                    LineMob l = line(cx + p[ed[0]][0], iy + p[ed[0]][1], cx + p[ed[1]][0], iy + p[ed[1]][1], Colors.withAlpha(c, 0.7), 3);
                    l.setOpacity(0);
                    made.add(l);
                }
                for (double[] q : p) made.add(circ(cx + q[0], iy + q[1], 11, c, 0.4));
            }
            case "dots" -> {
                TextMob t = label("…", cx, iy, 52, Colors.GRAY, false, true);
                made.add(t);
            }
            default -> made.addAll(kind.equals("tree") ? treeIcon(kind, cx, iy, c, 1.0) : treeIcon(kind, cx, cy - 24, c, 0.8));
        }
        // lines and shapes that were created hidden should fade in with the card
        return made;
    }

    private List<MObject> treeIcon(String kind, double cx, double cy, Color c, double k) {
        List<MObject> made = new ArrayList<>();
        double[][] pos;
        int[][] edges;
        String[] vals = null;
        switch (kind) {
            case "tree" -> {
                pos = new double[][]{{0, -40}, {-62, 6}, {0, 6}, {62, 6}, {-82, 50}, {-42, 50}};
                edges = new int[][]{{0, 1}, {0, 2}, {0, 3}, {1, 4}, {1, 5}};
            }
            case "bst" -> {
                pos = new double[][]{{0, -40}, {-44, 4}, {44, 4}, {-70, 48}, {-18, 48}, {70, 48}};
                edges = new int[][]{{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 5}};
                vals = new String[]{"5", "3", "8", "1", "4", "9"};
            }
            case "heap" -> {
                pos = new double[][]{{0, -40}, {-44, 4}, {44, 4}, {-70, 48}, {-18, 48}, {18, 48}};
                edges = new int[][]{{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 5}};
                vals = new String[]{"9", "7", "8", "3", "5", "2"};
            }
            default -> {
                pos = new double[][]{{0, -40}, {-44, 4}, {44, 4}, {-66, 48}, {-22, 48}, {66, 48}};
                edges = new int[][]{{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 5}};
            }
        }
        for (double[] q : pos) { q[0] *= k; q[1] *= k; }
        for (int[] e : edges) {
            LineMob l = line(cx + pos[e[0]][0], cy + pos[e[0]][1], cx + pos[e[1]][0], cy + pos[e[1]][1], Colors.withAlpha(c, 0.7), 3);
            l.setOpacity(0);
            made.add(l);
        }
        for (int i = 0; i < pos.length; i++) {
            made.add(circ(cx + pos[i][0], cy + pos[i][1], 13 * k, c, 0.4));
            if (vals != null) made.add(label(vals[i], cx + pos[i][0], cy + pos[i][1], 15 * k, Colors.WHITE, false, true));
        }
        return made;
    }

    // ── slide 31: DSAP usage ─────────────────────────────────────────

    private void dsapUsage() {
        head = dropHeading("DSAP Usage");
        pause(0.4);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "In several applications, arrays (and matrices)"));
        s.add(ln(3, "suffice. The data is static."));
        s.add(ln(0, "Most of our data structures are designed for"));
        s.add(ln(3, "other cases: the data is dynamic."));
        s.add(ln(0, "Properties of the problem dictate both the"));
        s.add(ln(3, "algorithm and the associated data structures."));
        s.add(ln(0, "Algorithms often use data structures as tools."));
        List<List<MObject>> text = writeSlide(s, -365);
        pause(1.2);
        swipeAway(text);
        pause(0.4);

        staticData();
        dynamicData();
        problemDictates();
        usedAsTools();
        fadeOutAll(d(0.9), head);
        pause(0.3);
    }

    private StrokeTextMob caption(String text, Color c, double secs) {
        StrokeTextMob t = stroke(text, 0, -340, 32, c, false);
        play(new Write(t, d(secs)));
        return t;
    }

    private void staticData() {
        List<MObject> mine = new ArrayList<>();
        mine.add(caption("In several applications, arrays (and matrices) suffice: the data is static.", Colors.WHITE, 3.8));
        List<Animation> in = new ArrayList<>();
        int[] v = {12, 7, 31, 4, 19, 8, 25, 16};
        List<RectMob> cells = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            double x = (i - 3.5) * 124;
            RectMob b = rect(x, -60, 112, 84, Colors.TEAL, 0.28);
            TextMob t = label(String.valueOf(v[i]), x, -60, 34, Colors.WHITE, false, true);
            TextMob ix = label("a[" + i + "]", x, 6, 22, Colors.GRAY, false, false);
            in.add(new FadeInAt(b, 0.08 * i, d(0.5)));
            in.add(new FadeInAt(t, 0.08 * i, d(0.5)));
            in.add(new FadeInAt(ix, 0.08 * i, d(0.5)));
            cells.add(b);
            mine.add(b);
            mine.add(t);
            mine.add(ix);
        }
        playAll(in);
        RectMob ring = new RectMob(124, 96).setCornerRadius(12);
        ring.setFillColor(Color.TRANSPARENT);
        ring.setStrokeColor(Colors.GOLD);
        ring.setStrokeWidth(5);
        ring.setPosition(-3.5 * 124, -60);
        ring.setOpacity(0);
        add(ring);
        mine.add(ring);
        play(new FadeIn(ring, d(0.4)));
        int[] visit = {5, 1, 6, 3, 0, 4};
        for (int idx : visit) {
            play(new MoveTo(ring, (idx - 3.5) * 124, -60, d(0.45)).setEasing(Easing.EASE_IN_OUT));
            pause(0.12);
        }
        StrokeTextMob c1 = stroke("static data: no inserts, no deletes — and any element is one step away.", 0, 140, 30, Colors.TEAL, false);
        play(new Write(c1, d(3.4)));
        mine.add(c1);
        pause(2.4);
        fadeOutAll(d(0.8), mine);
        pause(0.2);
    }

    private void dynamicData() {
        List<MObject> mine = new ArrayList<>();
        mine.add(caption("Most of our data structures are designed for the other case: the data is dynamic.", Colors.WHITE, 4.0));
        int[] v = {7, 3, 9, 4, 12};
        double[] nx = {-400, -200, 0, 200, 400};
        RectMob[] nd = new RectMob[5];
        TextMob[] nt = new TextMob[5];
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            nd[i] = rect(nx[i], -40, 110, 70, Colors.GOLD, 0.28);
            nt[i] = label(String.valueOf(v[i]), nx[i], -40, 32, Colors.WHITE, false, true);
            if (i < 4) {
                in.add(new FadeInAt(nd[i], 0.1 * i, d(0.5)));
                in.add(new FadeInAt(nt[i], 0.1 * i, d(0.5)));
            }
            mine.add(nd[i]);
            mine.add(nt[i]);
        }
        ArrowMob[] ar = new ArrowMob[4];
        for (int i = 0; i < 4; i++) {
            ar[i] = arrow(nx[i] + 60, -40, nx[i + 1] - 62, -40, Colors.GOLD);
            mine.add(ar[i]);
        }
        playAll(in);
        List<Animation> drawn = new ArrayList<>();
        for (int i = 0; i < 3; i++) drawn.add(new DrawArrow(ar[i], d(0.6)));
        playAll(drawn);
        pause(0.5);

        // a new element arrives
        TextMob op1 = label("a new element arrives", 0, 140, 30, Colors.GREEN, false, true);
        play(new FadeIn(op1, d(0.5)));
        mine.add(op1);
        nd[4].setPosition(760, -230);
        nt[4].setPosition(760, -230);
        List<Animation> arrive = new ArrayList<>();
        arrive.add(new FadeIn(nd[4], d(0.4)));
        arrive.add(new FadeIn(nt[4], d(0.4)));
        arrive.add(new ArcMove(nd[4], nx[4], -40, 60, d(1.0)));
        arrive.add(new ArcMove(nt[4], nx[4], -40, 60, d(1.0)));
        playAll(arrive);
        play(new DrawArrow(ar[3], d(0.6)));
        pause(0.6);

        // one leaves; its neighbours are re-linked
        play(new FadeOut(op1, d(0.4)));
        remove(op1);
        TextMob op2 = label("another one leaves", 0, 140, 30, Colors.RED, false, true);
        play(new FadeIn(op2, d(0.5)));
        mine.add(op2);
        ArrowMob bridge = arrow(nx[0] + 60, -40, nx[2] - 62, -40, Colors.GREEN);
        mine.add(bridge);
        List<Animation> leave = new ArrayList<>();
        leave.add(new FadeOut(nd[1], d(0.6)));
        leave.add(new FadeOut(nt[1], d(0.6)));
        leave.add(new FadeOut(ar[0], d(0.5)));
        leave.add(new FadeOut(ar[1], d(0.5)));
        playAll(leave);
        play(new DrawArrow(bridge, d(0.7)));
        pause(0.8);
        StrokeTextMob c1 = stroke("dynamic data: elements keep arriving and leaving — lists, trees, hash tables ...", 0, 240, 30, Colors.GOLD, false);
        play(new Write(c1, d(3.8)));
        mine.add(c1);
        pause(2.4);
        fadeOutAll(d(0.8), mine);
        pause(0.2);
    }

    private void problemDictates() {
        List<MObject> mine = new ArrayList<>();
        mine.add(caption("Properties of the problem dictate both the algorithm and the data structure.", Colors.WHITE, 3.8));
        RectMob prob = rect(0, -190, 330, 80, Colors.BLUE, 0.3);
        TextMob pt = label("the problem", 0, -190, 34, Colors.WHITE, false, true);
        RectMob alg = rect(-360, 60, 330, 80, Colors.ORANGE, 0.3);
        TextMob at = label("algorithm", -360, 60, 34, Colors.WHITE, false, true);
        RectMob ds = rect(360, 60, 330, 80, Colors.TEAL, 0.3);
        TextMob dt = label("data structure", 360, 60, 34, Colors.WHITE, false, true);
        mine.addAll(List.of(prob, pt, alg, at, ds, dt));
        play(new FadeIn(prob, d(0.5)), new FadeIn(pt, d(0.5)));
        ArrowMob a1 = arrow(-70, -145, -300, 20, Colors.LIGHT_GRAY);
        ArrowMob a2 = arrow(70, -145, 300, 20, Colors.LIGHT_GRAY);
        mine.add(a1);
        mine.add(a2);
        play(new DrawArrow(a1, d(0.8)), new DrawArrow(a2, d(0.8)), new FadeIn(alg, d(0.6)), new FadeIn(at, d(0.6)),
                new FadeIn(ds, d(0.6)), new FadeIn(dt, d(0.6)));
        TextMob dict = label("dictates", 0, -60, 26, Colors.GRAY, false, true);
        play(new FadeIn(dict, d(0.5)));
        mine.add(dict);
        ArrowMob b1 = arrow(-190, 60, 190, 60, Colors.GOLD);
        ArrowMob b2 = arrow(190, 50, -190, 50, Colors.GOLD);
        mine.add(b1);
        mine.add(b2);
        play(new DrawArrow(b1, d(0.8)), new DrawArrow(b2, d(0.8)));
        StrokeTextMob e1 = stroke("search a sorted array  →  binary search on an array", 0, 190, 28, Colors.LIGHT_GRAY, false);
        play(new Write(e1, d(2.6)));
        StrokeTextMob e2 = stroke("always need the largest next  →  heapsort on a heap", 0, 245, 28, Colors.LIGHT_GRAY, false);
        play(new Write(e2, d(2.6)));
        mine.add(e1);
        mine.add(e2);
        pause(2.6);
        fadeOutAll(d(0.8), mine);
        pause(0.2);
    }

    private void usedAsTools() {
        List<MObject> mine = new ArrayList<>();
        mine.add(caption("Algorithms often use data structures as tools.", Colors.WHITE, 2.8));
        String[] algs = {"binary search", "heapsort", "bucket / counting / radix sort"};
        String[] tools = {"sorted array", "heap", "buckets"};
        Color[] cols = {Colors.TEAL, Colors.BLUE, Colors.PINK};
        for (int i = 0; i < 3; i++) {
            double y = -170 + 120 * i;
            double start = 0.9 * i;
            RectMob a = rect(-330, y, 430, 74, Colors.ORANGE, 0.28);
            TextMob at = label(algs[i], -330, y, 28, Colors.WHITE, false, true);
            RectMob t = rect(330, y, 330, 74, cols[i], 0.28);
            TextMob tt = label(tools[i], 330, y, 30, Colors.WHITE, false, true);
            ArrowMob link = arrow(-100, y, 150, y, Colors.LIGHT_GRAY);
            TextMob uses = label("uses", 25, y - 26, 22, Colors.GRAY, false, true);
            List<Animation> row = new ArrayList<>();
            row.add(new FadeInAt(a, start, d(0.6)));
            row.add(new FadeInAt(at, start, d(0.6)));
            row.add(new FadeInAt(t, start + 0.5, d(0.6)));
            row.add(new FadeInAt(tt, start + 0.5, d(0.6)));
            row.add(new FadeInAt(uses, start + 0.3, d(0.6)));
            playAll(row);
            play(new DrawArrow(link, d(0.6)));
            mine.addAll(List.of(a, at, t, tt, link, uses));
        }
        pause(2.6);
        fadeOutAll(d(0.8), mine);
        pause(0.2);
    }

    // ── end card ─────────────────────────────────────────────────────

    private void madeBy() {
        List<MObject> mine = new ArrayList<>();
        StrokeTextMob by = stroke("This video was made by", 0, -120, 38, Colors.LIGHT_GRAY, false);
        play(new Write(by, d(2.0)));
        mine.add(by);
        pause(0.2);

        // "Karthik & Tejaswi", written name by name, each in its own color
        double size = 104, gap = 34;
        double w1 = strokeW("Karthik", true, size), w2 = strokeW("&", true, size), w3 = strokeW("Tejaswi", true, size);
        double left = -(w1 + w2 + w3 + 2 * gap) / 2;
        StrokeTextMob n1 = stroke("Karthik", left + w1 / 2, 20, size, Colors.TEAL, true);
        StrokeTextMob amp = stroke("&", left + w1 + gap + w2 / 2, 20, size, Colors.WHITE, true);
        StrokeTextMob n2 = stroke("Tejaswi", left + w1 + w2 + 2 * gap + w3 / 2, 20, size, Colors.GOLD, true);
        play(new Write(n1, d(1.8)));
        play(new Write(amp, d(0.7)));
        play(new Write(n2, d(1.8)));
        mine.add(n1);
        mine.add(amp);
        mine.add(n2);

        // a row of memory cells underneath, one lighting up after another
        List<Animation> row = new ArrayList<>();
        int cells = 9;
        for (int i = 0; i < cells; i++) {
            Color c = i < 4 ? Colors.TEAL : (i == 4 ? Colors.WHITE : Colors.GOLD);
            RectMob r = rect((i - (cells - 1) / 2.0) * 62, 150, 52, 40, c, 0.3);
            row.add(new FadeInAt(r, 0.1 * i, d(0.5)));
            mine.add(r);
        }
        playAll(row);
        TextMob course = label("CS5013  ·  LecViz", 0, 250, 30, Colors.GRAY, false, false);
        play(new FadeIn(course, d(0.8)));
        mine.add(course);
        pause(3.2);
        fadeOutAll(d(1.4), mine);
        pause(0.4);
    }
}
