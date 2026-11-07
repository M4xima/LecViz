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
 * Kit for the graph-deck clips, on top of the hash kit (so it also has cells, chains, code boxes, chips, LaTeX):
 *
 *   - G: a small graph model (directed or not, optional weights) with sorted neighbour lists; every table, order
 *        and count shown in the clips is computed from it
 *   - GV: the picture of a G (circles and arrows, curved edges, weight labels); nodes light up, edges are traced
 *   - Grid: a rows x columns table of boxes (adjacency matrix, edge list, CSR arrays)
 *   - Seq: a queue or a stack of chips that grows and shrinks (BFS queue, DFS stack, visit order)
 *   - the helpers every scene needs: say, keep, clearMine, newHeading, tag, show, code
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public abstract class PDSGraphClipBase extends PDSHashClipBase {

    protected static final Color NODE_C = Colors.TEAL;
    protected static final Color QUEUED_C = Colors.GOLD;
    protected static final Color CURRENT_C = Colors.ORANGE;
    protected static final Color DONE_C = Colors.GREEN;

    // ── scene helpers (every graph scene uses them) ──────────────────

    protected final List<MObject> mine = new ArrayList<>();
    protected StrokeTextMob head;

    protected void say(String s, Color c) { sayAt(s, c, 0, 440, 34); }

    protected void keep(List<List<MObject>> g) { for (List<MObject> x : g) mine.addAll(x); }

    protected void clearMine() {
        unsay();
        fadeOutAll(d(1.0), mine);
        mine.clear();
        pause(0.3);
    }

    /** Remembers how many objects the scene holds now; dropSince(mark) fades out everything added after that. */
    protected int mark() { return mine.size(); }

    protected void dropSince(int m, double dur) {
        List<MObject> f = new ArrayList<>(mine.subList(m, mine.size()));
        fadeOutAll(dur, f);
        mine.removeAll(f);
    }

    protected void newHeading(String s) {
        if (head != null) fadeOutAll(d(0.8), head);
        head = dropHeading(s);
        pause(0.4);
    }

    protected TextMob tag(String s, double x, double y, double size, Color c, boolean left) {
        TextMob t = label(s, x, y, size, c, left, true);
        mine.add(t);
        play(new FadeIn(t, d(0.5)));
        return t;
    }

    protected LaTeXMob show(String src, double size, double x, double y) {
        LaTeXMob f = latex(src, size, x, y);
        mine.add(f);
        play(new FadeIn(f, d(0.8)));
        return f;
    }

    protected CodeBox code(String[] lines, double left, double top, double size, double pitch, double typeSecs) {
        CodeBox c = new CodeBox(lines, left, top, size, pitch);
        c.typeIn(typeSecs);
        mine.addAll(c.parts());
        play(new FadeIn(c.hl, d(0.3)));
        return c;
    }

    protected void textThenSwipe(List<Spec> s, double startY, double hold) {
        List<List<MObject>> t = writeSlide(s, startY);
        pause(hold);
        swipeAway(t);
        pause(0.4);
    }

    // ── the graph model ──────────────────────────────────────────────

    protected static final class G {
        final int n;
        final boolean directed;
        final List<int[]> e = new ArrayList<>();
        final List<Integer> wt = new ArrayList<>();
        final List<String> lab = new ArrayList<>();
        boolean weighted;

        G(int n, boolean directed) {
            this.n = n;
            this.directed = directed;
        }

        G add(int u, int v) { e.add(new int[]{u, v}); wt.add(1); lab.add(null); return this; }

        G add(int u, int v, int w) { e.add(new int[]{u, v}); wt.add(w); lab.add(null); weighted = true; return this; }

        /** An edge with a word on it (a relation such as "mother of"). */
        G add(int u, int v, String label) { e.add(new int[]{u, v}); wt.add(1); lab.add(label); weighted = true; return this; }

        /** Out-neighbours of u in increasing order (for an undirected graph: every neighbour). */
        List<Integer> nbrs(int u) {
            java.util.TreeSet<Integer> s = new java.util.TreeSet<>();
            for (int[] x : e) {
                if (x[0] == u) s.add(x[1]);
                if (!directed && x[1] == u) s.add(x[0]);
            }
            return new ArrayList<>(s);
        }

        int weight(int u, int v) {
            for (int k = 0; k < e.size(); k++) {
                int[] x = e.get(k);
                if (x[0] == u && x[1] == v) return wt.get(k);
                if (!directed && x[0] == v && x[1] == u) return wt.get(k);
            }
            return 0;
        }

        boolean has(int u, int v) {
            for (int[] x : e) if ((x[0] == u && x[1] == v) || (!directed && x[0] == v && x[1] == u)) return true;
            return false;
        }

        int edgeIndex(int u, int v) {
            for (int k = 0; k < e.size(); k++) {
                int[] x = e.get(k);
                if ((x[0] == u && x[1] == v) || (!directed && x[0] == v && x[1] == u)) return k;
            }
            return -1;
        }

        int deg(int u) { return nbrs(u).size(); }

        int[] indeg() {
            int[] in = new int[n];
            for (int[] x : e) in[x[1]]++;
            return in;
        }

        /** Every edge as a directed pair in the order of the lists (an undirected edge appears in both directions). */
        List<int[]> pairs() {
            List<int[]> p = new ArrayList<>();
            for (int u = 0; u < n; u++) for (int v : nbrs(u)) p.add(new int[]{u, v});
            return p;
        }
    }

    // ── the picture of a graph ───────────────────────────────────────

    protected final class GV {
        final G g;
        final double[] x, y;
        final double r, fs;
        final CircleMob[] sh;
        final TextMob[] tx;
        final Link[] ln;
        final TextMob[] wl;
        final double[] bend;
        final List<MObject> over = new ArrayList<>();
        final Color[] base;

        GV(G g, double[][] xy, double r, double fs, String[] labels, double[] bend, double[] woff) {
            this.g = g;
            this.r = r;
            this.fs = fs;
            this.bend = bend;
            x = new double[g.n];
            y = new double[g.n];
            sh = new CircleMob[g.n];
            tx = new TextMob[g.n];
            base = new Color[g.n];
            for (int i = 0; i < g.n; i++) {
                x[i] = xy[i][0];
                y[i] = xy[i][1];
                base[i] = NODE_C;
                CircleMob c = new CircleMob(r);
                c.setFillColor(Colors.withAlpha(NODE_C, 0.28));
                c.setStrokeColor(Colors.withAlpha(NODE_C, 0.95));
                c.setStrokeWidth(3);
                c.setPosition(x[i], y[i]);
                c.setOpacity(0);
                add(c);
                sh[i] = c;
                tx[i] = label(labels == null ? String.valueOf(i) : labels[i], x[i], y[i], fs, Colors.WHITE, false, true);
            }
            ln = new Link[g.e.size()];
            wl = new TextMob[g.e.size()];
            for (int k = 0; k < ln.length; k++) {
                int[] q = g.e.get(k);
                double b = bend == null ? 0 : bend[k];
                double[][] rt = route(q[0], q[1], b, g.directed);
                Link l = new Link(rt[0], rt[1], Colors.withAlpha(Colors.LIGHT_GRAY, 0.75), 3, g.directed);
                add(l);
                ln[k] = l;
                if (g.weighted) {
                    double off = woff == null ? 20 : woff[k];
                    int m = rt[0].length / 2;
                    double mx = (rt[0][m] + rt[0][(rt[0].length - 1) / 2]) / 2, my = (rt[1][m] + rt[1][(rt[1].length - 1) / 2]) / 2;
                    double dx = x[q[1]] - x[q[0]], dy = y[q[1]] - y[q[0]], len = Math.hypot(dx, dy);
                    mx += -dy / len * off;
                    my += dx / len * off;
                    String lt = g.lab.get(k);
                    wl[k] = label(lt != null ? lt : String.valueOf(g.wt.get(k)), mx, my, lt != null ? 24 : 26, lt != null ? Colors.GOLD : Colors.RED, false, true);
                }
            }
            mine.addAll(parts());
        }

        GV(G g, double[][] xy, double r, double fs) { this(g, xy, r, fs, null, null, null); }

        /** A route (xs, ys) from node u to node v, trimmed by the circles; bend > 0 curves it. */
        double[][] route(int u, int v, double b, boolean headRoom) {
            double gap = headRoom ? 5 : 0;
            if (b == 0) {
                double dx = x[v] - x[u], dy = y[v] - y[u], len = Math.hypot(dx, dy), ux = dx / len, uy = dy / len;
                return pts(x[u] + ux * r, y[u] + uy * r, x[v] - ux * (r + gap), y[v] - uy * (r + gap));
            }
            double dx = x[v] - x[u], dy = y[v] - y[u], len = Math.hypot(dx, dy);
            double cx = (x[u] + x[v]) / 2 + dy / len * b * 2, cy = (y[u] + y[v]) / 2 - dx / len * b * 2;
            double sx = cx - x[u], sy = cy - y[u], sl = Math.hypot(sx, sy);
            double ex = cx - x[v], ey = cy - y[v], el = Math.hypot(ex, ey);
            double p0x = x[u] + sx / sl * r, p0y = y[u] + sy / sl * r;
            double p2x = x[v] + ex / el * (r + gap), p2y = y[v] + ey / el * (r + gap);
            int steps = 16;
            double[] xs = new double[steps + 1], ys = new double[steps + 1];
            for (int i = 0; i <= steps; i++) {
                double t = i / (double) steps, a = (1 - t) * (1 - t), bb = 2 * t * (1 - t), c = t * t;
                xs[i] = a * p0x + bb * cx + c * p2x;
                ys[i] = a * p0y + bb * cy + c * p2y;
            }
            return new double[][]{xs, ys};
        }

        List<MObject> parts() {
            List<MObject> l = new ArrayList<>();
            for (int i = 0; i < g.n; i++) {
                l.add(sh[i]);
                l.add(tx[i]);
            }
            for (int k = 0; k < ln.length; k++) {
                l.add(ln[k]);
                if (wl[k] != null) l.add(wl[k]);
            }
            l.addAll(over);
            return l;
        }

        /** Nodes fade in, then the edges are drawn. */
        void build(double dur) {
            List<Animation> a = new ArrayList<>();
            for (int i = 0; i < g.n; i++) {
                a.add(new FadeIn(sh[i], dur));
                a.add(new FadeIn(tx[i], dur));
            }
            for (int k = 0; k < ln.length; k++) {
                a.add(new DrawLinkAt(ln[k], dur * 0.6, dur));
                if (wl[k] != null) a.add(new FadeIn(wl[k], dur));
            }
            playAll(a);
        }

        void buildNodes(double dur) {
            List<Animation> a = new ArrayList<>();
            for (int i = 0; i < g.n; i++) {
                a.add(new FadeIn(sh[i], dur));
                a.add(new FadeIn(tx[i], dur));
            }
            playAll(a);
        }

        void buildEdge(int k, double dur) {
            List<Animation> a = new ArrayList<>();
            a.add(new DrawLinkAt(ln[k], 0, dur));
            if (wl[k] != null) a.add(new FadeIn(wl[k], dur));
            playAll(a);
        }

        void nodeColor(List<Animation> a, int i, Color c, double dur) {
            a.add(new ColorChange(sh[i], Colors.withAlpha(c, 0.5), dur));
            a.add(new ColorChange(sh[i], c, dur, ColorChange.Target.STROKE));
        }

        void light(int i, Color c) {
            List<Animation> a = new ArrayList<>();
            nodeColor(a, i, c, d(0.35));
            playAll(a);
        }

        void lightAll(List<Integer> nodes, Color c) {
            List<Animation> a = new ArrayList<>();
            for (int i : nodes) nodeColor(a, i, c, d(0.35));
            playAll(a);
        }

        /** A coloured trace drawn over the edge between u and v (in the direction u to v). */
        Link trace(int u, int v, Color c) {
            int k = g.edgeIndex(u, v);
            double b = 0;
            if (k >= 0 && bend != null) b = g.e.get(k)[0] == u ? bend[k] : -bend[k];
            double[][] rt = route(u, v, b, g.directed);
            Link l = new Link(rt[0], rt[1], c, 6, g.directed);
            add(l);
            over.add(l);
            mine.add(l);
            playAll(java.util.Collections.singletonList(new DrawLinkAt(l, 0, d(0.45))));
            return l;
        }

        /** A small text beside a node (a distance, a degree, a tick). */
        TextMob note(int i, String s, Color c, double dx, double dy, double size) {
            TextMob t = label(s, x[i] + dx, y[i] + dy, size, c, false, true);
            over.add(t);
            mine.add(t);
            play(new FadeIn(t, d(0.35)));
            return t;
        }

        /** Takes a coloured trace away again (a step that is undone). */
        void untrace(Link l) {
            playAll(java.util.Collections.singletonList(new FadeOut(l, d(0.3))));
            remove(l);
            over.remove(l);
            mine.remove(l);
        }

        /** Like note(), but the text fades in together with other animations of the list. */
        TextMob noteIn(List<Animation> a, int i, String s, Color c, double dx, double dy, double size) {
            TextMob t = label(s, x[i] + dx, y[i] + dy, size, c, false, true);
            over.add(t);
            mine.add(t);
            a.add(new FadeIn(t, d(0.35)));
            return t;
        }

        /** Takes the node's note away and sets a new one. */
        void clearOver(double dur) {
            List<Animation> a = new ArrayList<>();
            for (MObject m : over) a.add(new FadeOut(m, dur));
            playAll(a);
            for (MObject m : over) remove(m);
            mine.removeAll(over);
            over.clear();
        }

        void resetColors() {
            List<Animation> a = new ArrayList<>();
            for (int i = 0; i < g.n; i++) nodeColor(a, i, base[i], d(0.4));
            playAll(a);
        }

        void dimEdge(int k, double alpha) {
            playAll(java.util.Collections.singletonList(new FadeOutTo(ln[k], alpha, d(0.5))));
        }
    }

    /** Fades an object to a given opacity (not all the way). */
    protected static final class FadeOutTo extends Animation {
        private final double to;
        private double from;

        FadeOutTo(MObject m, double to, double dur) {
            super(m, dur, Easing.EASE_IN_OUT);
            this.to = to;
        }

        @Override public void begin() { from = target.getOpacity(); }

        @Override public void interpolate(double t) { target.setOpacity(from + (to - from) * t); }
    }

    // ── a grid of boxes ──────────────────────────────────────────────

    protected final class Grid {
        final int rows, cols;
        final double left, top, cw, ch;
        final RectMob[][] box;
        final TextMob[][] txt;
        final List<MObject> extra = new ArrayList<>();

        /** vals[r][c] may be null (empty box); filled boxes fade in with the grid. */
        Grid(String[][] vals, double left, double top, double cw, double ch, Color c, double fs) {
            this.rows = vals.length;
            this.cols = vals[0].length;
            this.left = left;
            this.top = top;
            this.cw = cw;
            this.ch = ch;
            box = new RectMob[rows][cols];
            txt = new TextMob[rows][cols];
            for (int r = 0; r < rows; r++) {
                for (int k = 0; k < cols; k++) {
                    RectMob b = new RectMob(cw - 4, ch - 4).setCornerRadius(5);
                    b.setFillColor(Colors.withAlpha(c, 0.16));
                    b.setStrokeColor(Colors.withAlpha(c, 0.6));
                    b.setStrokeWidth(2);
                    b.setPosition(cx(k), cy(r));
                    b.setOpacity(0);
                    add(b);
                    box[r][k] = b;
                    String v = vals[r][k];
                    txt[r][k] = label(v == null ? "" : v, cx(k), cy(r), fs, Colors.WHITE, false, true);
                }
            }
            mine.addAll(parts());
        }

        double cx(int c) { return left + c * cw + cw / 2; }

        double cy(int r) { return top + r * ch + ch / 2; }

        List<MObject> parts() {
            List<MObject> l = new ArrayList<>(extra);
            for (int r = 0; r < rows; r++) for (int k = 0; k < cols; k++) {
                l.add(box[r][k]);
                l.add(txt[r][k]);
            }
            return l;
        }

        void show(List<Animation> a, double dur) {
            for (int r = 0; r < rows; r++) for (int k = 0; k < cols; k++) {
                a.add(new FadeIn(box[r][k], dur));
                if (!txt[r][k].getText().isEmpty()) a.add(new FadeIn(txt[r][k], dur));
            }
            for (MObject m : extra) a.add(new FadeIn(m, dur));
        }

        /** Writes a value into a box that was empty (it fades in). */
        void put(List<Animation> a, int r, int c, String v, double dur) {
            txt[r][c].setText(v);
            txt[r][c].setOpacity(0);
            a.add(new FadeIn(txt[r][c], dur));
        }

        /** Headers along the top (column labels) and the left (row labels). */
        void headers(String[] colH, String[] rowH, double size, Color c) {
            if (colH != null) for (int k = 0; k < cols; k++) {
                TextMob t = label(colH[k], cx(k), top - 22, size, c, false, true);
                extra.add(t);
                mine.add(t);
            }
            if (rowH != null) for (int r = 0; r < rows; r++) {
                TextMob t = label(rowH[r], left - 14, cy(r), size, c, false, true);
                t.setAlignment(javafx.scene.text.TextAlignment.RIGHT);
                t.setPosition(left - 14, cy(r));
                extra.add(t);
                mine.add(t);
            }
        }

        void light(List<Animation> a, int r, int c, Color col, double dur) {
            a.add(new ColorChange(box[r][c], Colors.withAlpha(col, 0.5), dur));
            a.add(new ColorChange(box[r][c], col, dur, ColorChange.Target.STROKE));
        }

        void lightRow(List<Animation> a, int r, Color col, double dur) {
            for (int k = 0; k < cols; k++) light(a, r, k, col, dur);
        }

        void lightCol(List<Animation> a, int c, Color col, double dur) {
            for (int r = 0; r < rows; r++) light(a, r, c, col, dur);
        }

        void plain(List<Animation> a, Color c, double dur) {
            for (int r = 0; r < rows; r++) for (int k = 0; k < cols; k++) {
                a.add(new ColorChange(box[r][k], Colors.withAlpha(c, 0.16), dur));
                a.add(new ColorChange(box[r][k], Colors.withAlpha(c, 0.6), dur, ColorChange.Target.STROKE));
            }
        }
    }

    // ── a queue or a stack of chips ──────────────────────────────────

    protected final class Seq {
        final double x0, y0, dx, dy;
        final List<List<MObject>> items = new ArrayList<>();
        final double w, h;
        final Color c;
        final double fs;
        final List<MObject> extra = new ArrayList<>();

        /** Item k sits at (x0 + k*dx, y0 + k*dy). */
        Seq(double x0, double y0, double dx, double dy, double w, double h, Color c, double fs) {
            this.x0 = x0;
            this.y0 = y0;
            this.dx = dx;
            this.dy = dy;
            this.w = w;
            this.h = h;
            this.c = c;
            this.fs = fs;
        }

        int size() { return items.size(); }

        boolean empty() { return items.isEmpty(); }

        double px(int k) { return x0 + k * dx; }

        double py(int k) { return y0 + k * dy; }

        List<MObject> parts() {
            List<MObject> l = new ArrayList<>(extra);
            for (List<MObject> it : items) l.addAll(it);
            return l;
        }

        /** A caption beside the sequence (kept for clearing). */
        TextMob caption(String s, double x, double y, double size, Color col, boolean left) {
            TextMob t = label(s, x, y, size, col, left, true);
            extra.add(t);
            mine.add(t);
            play(new FadeIn(t, d(0.4)));
            return t;
        }

        void push(String s) { push(s, c); }

        void push(String s, Color col) {
            int k = items.size();
            List<MObject> chip = chip(s, px(k), py(k), w, h, col, fs);
            List<Animation> a = new ArrayList<>();
            for (MObject m : chip) a.add(new DropIn(m, 50, 0, d(0.45)));
            playAll(a);
            items.add(chip);
            mine.addAll(chip);
        }

        /** Takes the first item out (queue) and slides the rest forward. */
        void popFront() {
            List<MObject> gone = items.remove(0);
            List<Animation> a = new ArrayList<>();
            for (MObject m : gone) a.add(new FadeOut(m, d(0.4)));
            for (int k = 0; k < items.size(); k++) for (MObject m : items.get(k))
                a.add(new MoveTo(m, px(k), py(k), d(0.45)).setEasing(Easing.EASE_IN_OUT));
            playAll(a);
            for (MObject m : gone) remove(m);
            mine.removeAll(gone);
        }

        /** Takes the last item out (stack). */
        void popBack() {
            List<MObject> gone = items.remove(items.size() - 1);
            List<Animation> a = new ArrayList<>();
            for (MObject m : gone) a.add(new FadeOut(m, d(0.4)));
            playAll(a);
            for (MObject m : gone) remove(m);
            mine.removeAll(gone);
        }
    }

    // ── layouts of the example graph used all over the deck ──────────

    /** The six-node graph of the deck: 0 1 2 on top, 3 4 5 below. */
    protected static G exampleGraph() {
        G g = new G(6, false);
        g.add(0, 3).add(0, 4).add(1, 3).add(1, 5).add(2, 4).add(4, 5);
        return g;
    }

    protected static double[][] exampleXY(double cx, double cy, double dx, double dy) {
        return new double[][]{{cx - dx, cy - dy / 2}, {cx, cy - dy / 2}, {cx + dx, cy - dy / 2},
                {cx - dx, cy + dy / 2}, {cx, cy + dy / 2}, {cx + dx, cy + dy / 2}};
    }

    protected static String join(List<Integer> l, String sep) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < l.size(); i++) {
            if (i > 0) sb.append(sep);
            sb.append(l.get(i));
        }
        return sb.toString();
    }

    protected static double[][] pts2(double[] xs, double[] ys) { return new double[][]{xs, ys}; }
}
