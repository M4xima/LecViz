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
 * Shared kit for the priority-queue clips, on top of the tree kit:
 *
 *   - HV: a binary heap drawn two ways at once, as a tree (circles joined by edges) and as the array that stores
 *     it (boxed cells under the tree, indexes below them). Every element is one object that owns its circle and its
 *     array cell, so when it moves in the tree its cell moves in the array
 *   - the two heap operations, driven by a real model of the heap so every number on screen is computed:
 *     heapInsert (put the new element at the first empty leaf, percolate it up) and heapDeleteMax (remove the root,
 *     percolate the hole down, finally fill it with the last element), each narrating one comparison at a time
 *   - a gold "hole" that travels down the tree during deleteMax, and ghost slots for the places still empty
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public abstract class PDSHeapClipBase extends PDSTreeClipBase {

    // colors with a fixed meaning in every heap clip
    protected static final Color NODE_C = Colors.BLUE;       // an element at rest
    protected static final Color NEW_C = Colors.PINK;        // the element being inserted / the last element
    protected static final Color CMP_C = Colors.ORANGE;      // elements being compared right now
    protected static final Color OK_C = Colors.GREEN;        // in the right place
    protected static final Color BAD_C = Colors.RED;         // breaks a rule
    protected static final Color HOLE_C = Colors.GOLD;       // the hole

    /** Where the one-line narration of an operation sits. */
    protected static final double NARR_Y = 440;

    /** Optional counters that heapInsert / heapDeleteMax tick (comparisons, element moves). */
    protected TextMob cmpNum, moveNum;
    private int cmpCount, moveCount;

    protected void resetCounts() {
        cmpCount = 0;
        moveCount = 0;
        if (cmpNum != null) cmpNum.setText("0");
        if (moveNum != null) moveNum.setText("0");
    }

    private void tickCmp() { cmpCount++; if (cmpNum != null) cmpNum.setText(String.valueOf(cmpCount)); }

    private void tickMove() { moveCount++; if (moveNum != null) moveNum.setText(String.valueOf(moveCount)); }

    protected int comparisons() { return cmpCount; }

    protected int moves() { return moveCount; }

    protected void narr(String text, Color c) { sayAt(text, c, 0, NARR_Y, 32); }

    // ── one heap element: a circle in the tree and a cell in the array ──

    protected final class El {
        int v, idx;
        Color color;
        final CircleMob shape;
        final TextMob text;
        final Cell cell;

        El(int v, int idx, Color c, HV h) {
            this.v = v;
            this.idx = idx;
            this.color = c;
            shape = new CircleMob(h.r);
            shape.setFillColor(Colors.withAlpha(c, 0.3));
            shape.setStrokeColor(Colors.withAlpha(c, 0.95));
            shape.setStrokeWidth(3);
            shape.setPosition(h.px(idx), h.py(idx));
            shape.setOpacity(0);
            add(shape);
            text = label(String.valueOf(v), h.px(idx), h.py(idx), h.fs, Colors.WHITE, false, true);
            cell = h.arr ? new Cell(String.valueOf(v), h.ax(idx), h.ay, h.cw - 8, h.ch, c, h.fs * 0.8) : null;
        }

        List<MObject> parts() {
            List<MObject> l = new ArrayList<>();
            l.add(shape);
            l.add(text);
            if (cell != null) l.addAll(cell.parts());
            return l;
        }
    }

    /** The gold hole: an empty circle in the tree and an empty gold cell in the array. */
    protected final class Hole {
        final CircleMob shape;
        final Cell cell;
        int idx;

        Hole(int idx, HV h) {
            this.idx = idx;
            shape = new CircleMob(h.r);
            shape.setFillColor(Colors.withAlpha(HOLE_C, 0.55));
            shape.setStrokeColor(HOLE_C);
            shape.setStrokeWidth(3);
            shape.setPosition(h.px(idx), h.py(idx));
            shape.setOpacity(0);
            add(shape);
            cell = h.arr ? new Cell(" ", h.ax(idx), h.ay, h.cw - 8, h.ch, HOLE_C, h.fs * 0.8) : null;
        }

        List<MObject> parts() {
            List<MObject> l = new ArrayList<>();
            l.add(shape);
            if (cell != null) l.addAll(cell.parts());
            return l;
        }
    }

    // ── the heap view ────────────────────────────────────────────────

    protected final class HV {
        final double cx, topY, width, levelGap, r, fs;
        final int levels, maxIdx;
        final boolean arr;
        final double ax0, ay, cw, ch;
        final El[] a;
        final Link[] edge;
        final CircleMob[] ghost;
        final List<MObject> all = new ArrayList<>();
        final List<MObject> frame = new ArrayList<>();
        int n;
        boolean cellsOn = true;   // false: elements appear without their array cells (see flatten)

        /**
         * @param levels how many tree levels have room (positions 1 .. 2^levels - 1)
         * @param arr    draw the array strip too (cells start at x = ax0 + index * cw, centered on y = ay)
         * @param cols   how many index labels the strip shows (0 .. cols - 1)
         */
        HV(double cx, double topY, double width, double levelGap, double r, double fs, int levels,
           boolean arr, double ax0, double ay, double cw, double ch, int cols) {
            this.cx = cx;
            this.topY = topY;
            this.width = width;
            this.levelGap = levelGap;
            this.r = r;
            this.fs = fs;
            this.levels = levels;
            this.maxIdx = (1 << levels) - 1;
            this.arr = arr;
            this.ax0 = ax0;
            this.ay = ay;
            this.cw = cw;
            this.ch = ch;
            a = new El[maxIdx + 2];
            edge = new Link[maxIdx + 2];
            ghost = new CircleMob[maxIdx + 2];
            for (int i = 2; i <= maxIdx; i++) {
                double[][] q = route(i);
                Link l = new Link(q[0], q[1], Colors.withAlpha(Colors.LIGHT_GRAY, 0.8), 3, false);
                add(l);
                edge[i] = l;
                all.add(l);
            }
            if (arr) {
                Cell zero = new Cell(" ", ax(0), ay, cw - 8, ch, Colors.GRAY, fs * 0.8);
                frame.addAll(zero.parts());
                for (int i = 0; i < cols; i++) {
                    TextMob t = label(String.valueOf(i), ax(i), ay + ch / 2 + 24, 24, Colors.GRAY, false, false);
                    frame.add(t);
                }
                all.addAll(frame);
            }
        }

        double lv(int i) { return 31 - Integer.numberOfLeadingZeros(i); }

        double px(int i) {
            int lv = (int) lv(i);
            int first = 1 << lv;
            return cx + ((i - first + 0.5) / first - 0.5) * width;
        }

        double py(int i) { return topY + lv(i) * levelGap; }

        double ax(int i) { return ax0 + i * cw + cw / 2; }

        double[][] route(int c) {
            int p = c / 2;
            double dx = px(c) - px(p), dy = py(c) - py(p), len = Math.hypot(dx, dy);
            double ux = dx / len, uy = dy / len;
            return pts(px(p) + ux * r, py(p) + uy * r, px(c) - ux * r, py(c) - uy * r);
        }

        /** Creates the element for index i (hidden) and files it in the model. */
        El make(int v, int i, Color c) {
            El e = new El(v, i, c, this);
            a[i] = e;
            if (i > n) n = i;
            all.addAll(e.parts());
            return e;
        }

        Hole hole(int i) {
            Hole h = new Hole(i, this);
            all.addAll(h.parts());
            return h;
        }

        /** A faint circle where an element could go; created hidden. */
        CircleMob ghost(int i) {
            CircleMob g = new CircleMob(r);
            g.setFillColor(Colors.withAlpha(Colors.GRAY, 0.06));
            g.setStrokeColor(Colors.withAlpha(Colors.GRAY, 0.55));
            g.setStrokeWidth(2.4);
            g.setPosition(px(i), py(i));
            g.setOpacity(0);
            add(g);
            ghost[i] = g;
            all.add(g);
            return g;
        }

        void showAnim(List<Animation> an, El e, double delay, double dur) {
            an.add(new FadeInAt(e.shape, delay, dur));
            an.add(new FadeInAt(e.text, delay, dur));
            if (e.cell != null && cellsOn) e.cell.fadeIn(an, delay, dur);
            if (e.idx >= 2) an.add(new DrawLinkAt(edge[e.idx], delay, dur));
        }

        /** Fades the element out; the edge into its place stays unless {@code withEdge}. */
        void hideAnim(List<Animation> an, El e, boolean withEdge, double dur) {
            an.add(new FadeOut(e.shape, dur));
            an.add(new FadeOut(e.text, dur));
            if (e.cell != null) e.cell.fadeOut(an, dur);
            if (withEdge && e.idx >= 2) an.add(new EraseLink(edge[e.idx], dur));
        }

        /** Moves the element (circle and cell) to index j. The caller keeps {@code a[]} in step. */
        void moveAnim(List<Animation> an, El e, int j, double bulge, double dur) {
            if (bulge == 0) {
                an.add(new MoveTo(e.shape, px(j), py(j), dur).setEasing(Easing.EASE_IN_OUT));
                an.add(new MoveTo(e.text, px(j), py(j), dur).setEasing(Easing.EASE_IN_OUT));
            } else {
                an.add(new ArcMove(e.shape, px(j), py(j), bulge, dur));
                an.add(new ArcMove(e.text, px(j), py(j), bulge, dur));
            }
            if (e.cell != null) e.cell.moveTo(an, ax(j), ay, bulge == 0 ? 0 : bulge * 0.8, dur);
            e.idx = j;
        }

        void swapAnim(List<Animation> an, int i, int j, double dur) {
            El x = a[i], y = a[j];
            moveAnim(an, x, j, 34, dur);
            moveAnim(an, y, i, -34, dur);
            a[i] = y;
            a[j] = x;
        }

        void paintAnim(List<Animation> an, El e, Color c, double dur) {
            an.add(new ColorChange(e.shape, Colors.withAlpha(c, 0.42), dur));
            an.add(new ColorChange(e.shape, Colors.withAlpha(c, 0.98), dur, ColorChange.Target.STROKE));
            if (e.cell != null) e.cell.color(an, c, dur);
            e.color = c;
        }

        void paint(El e, Color c, double dur) {
            List<Animation> an = new ArrayList<>();
            paintAnim(an, e, c, dur);
            playAll(an);
        }

        void paintEdgeAnim(List<Animation> an, int i, Color c, double dur) {
            an.add(new ColorChange(edge[i], c, dur, ColorChange.Target.STROKE));
        }

        /** Changes the number an element shows (circle and cell). */
        void setValue(El e, int v) {
            e.v = v;
            e.text.setText(String.valueOf(v));
            if (e.cell != null) e.cell.text.setText(String.valueOf(v));
        }

        /** Shows the array frame: the unused cell 0 and the index numbers. */
        void showFrame(double dur) {
            List<Animation> an = new ArrayList<>();
            for (MObject m : frame) an.add(new FadeIn(m, dur));
            playAll(an);
        }

        /** Level by level, the elements given (0 = nothing at that index; vals[k] is index k + 1) fade in with their edges and cells. */
        void build(int[] vals, double perLevelPause) {
            List<El> made = new ArrayList<>();
            for (int k = 0; k < vals.length; k++) if (vals[k] != 0) made.add(make(vals[k], k + 1, NODE_C));
            for (int lv = 0; lv < levels; lv++) {
                List<Animation> an = new ArrayList<>();
                for (El e : made) if (lv(e.idx) == lv) showAnim(an, e, 0, d(0.5));
                if (an.isEmpty()) continue;
                playAll(an);
                pause(perLevelPause);
            }
        }

        /** The array cells drop in one after another, in index order: the tree, written level by level, is the array. */
        void flatten(double step, double dur) {
            List<Animation> an = new ArrayList<>();
            int k = 0;
            for (int i = 1; i <= maxIdx; i++) {
                if (a[i] == null || a[i].cell == null) continue;
                an.add(new DropIn(a[i].cell.box, 40, step * k, dur));
                an.add(new DropIn(a[i].cell.text, 40, step * k, dur));
                k++;
            }
            cellsOn = true;
            playAll(an);
        }

        /** All the objects this view made (to fade out at the end). */
        List<MObject> parts() { return all; }
    }

    // ── insert: first empty leaf, percolate up ───────────────────────

    /**
     * insert(v): the new element appears at the first empty leaf (index n + 1) and percolates up while it is larger than
     * its parent. {@code sp} scales the pauses (1 = relaxed, 0.5 = quick). Returns the element.
     */
    protected El heapInsert(HV h, int v, double sp, boolean explain) {
        int i = h.n + 1;
        El e = h.make(v, i, NEW_C);
        if (explain) narr("insert(" + v + "): put it at the first empty leaf, index " + i + ".", Colors.LIGHT_GRAY);
        List<Animation> an = new ArrayList<>();
        h.showAnim(an, e, 0, d(0.6));
        playAll(an);
        pause(0.5 * sp);
        while (i > 1) {
            int p = i / 2;
            El pe = h.a[p];
            tickCmp();
            List<Animation> f = new ArrayList<>();
            h.paintAnim(f, pe, CMP_C, d(0.25));
            playAll(f);
            if (explain) narr(v + " vs its parent " + pe.v + " (index " + i + " / 2 = " + p + "): " + (v > pe.v ? v + " is larger" : v + " is smaller"), Colors.ORANGE);
            pause(0.55 * sp);
            if (v > pe.v) {
                if (explain) narr(v + " > " + pe.v + ": swap them, and look at the next parent.", Colors.PINK);
                List<Animation> s = new ArrayList<>();
                h.swapAnim(s, i, p, d(0.6));
                h.paintAnim(s, pe, NODE_C, d(0.4));
                playAll(s);
                tickMove();
                i = p;
                pause(0.3 * sp);
            } else {
                h.paint(pe, NODE_C, d(0.25));
                if (explain) narr(v + " < " + pe.v + ": the parent is larger, so " + v + " stays. Heap order holds.", Colors.GREEN);
                break;
            }
        }
        if (i == 1 && explain) narr(v + " reached the root: nothing is above it, so it is the new maximum.", Colors.GREEN);
        h.paint(e, OK_C, d(0.4));
        pause(0.5 * sp);
        return e;
    }

    // ── deleteMax: remove the root, percolate the hole down, fill it with the last element ──

    /**
     * deleteMax: the root goes, the hole percolates down (the larger child moves up) for as long as the last element is
     * smaller than that child, and the last element finally fills the hole. Returns the removed value.
     */
    protected int heapDeleteMax(HV h, double sp, boolean explain) {
        int n = h.n;
        El root = h.a[1];
        int removed = root.v;
        if (explain) narr("deleteMax: the maximum, " + removed + ", is the root. Remove it.", Colors.LIGHT_GRAY);
        if (n == 1) {
            List<Animation> an = new ArrayList<>();
            h.hideAnim(an, root, false, d(0.6));
            playAll(an);
            h.a[1] = null;
            h.n = 0;
            return removed;
        }
        El last = h.a[n];
        Hole hole = h.hole(1);
        List<Animation> an = new ArrayList<>();
        h.hideAnim(an, root, false, d(0.6));
        an.add(new FadeIn(hole.shape, d(0.6)));
        if (hole.cell != null) hole.cell.fadeIn(an, 0, d(0.6));
        h.a[1] = null;
        playAll(an);
        pause(0.5 * sp);
        if (explain) narr("Take the last element, " + last.v + " (index " + n + "): it will fill the hole in the end.", Colors.PINK);
        h.paint(last, NEW_C, d(0.4));
        pause(0.6 * sp);
        int m = n - 1;
        int i = 1;
        while (2 * i <= m) {
            int c = 2 * i;
            if (c + 1 <= m) {
                tickCmp();
                List<Animation> f = new ArrayList<>();
                h.paintAnim(f, h.a[c], CMP_C, d(0.25));
                h.paintAnim(f, h.a[c + 1], CMP_C, d(0.25));
                playAll(f);
                int big = h.a[c + 1].v > h.a[c].v ? c + 1 : c;
                if (explain) narr("Children of the hole: " + h.a[c].v + " and " + h.a[c + 1].v + ". The larger is " + h.a[big].v + ".", Colors.ORANGE);
                pause(0.6 * sp);
                List<Animation> g = new ArrayList<>();
                h.paintAnim(g, h.a[c], NODE_C, d(0.2));
                h.paintAnim(g, h.a[c + 1], NODE_C, d(0.2));
                playAll(g);
                c = big;
            }
            El ce = h.a[c];
            tickCmp();
            if (last.v < ce.v) {
                if (explain) narr(last.v + " < " + ce.v + ": " + ce.v + " moves up into the hole, the hole moves down.", Colors.PINK);
                List<Animation> mv = new ArrayList<>();
                h.moveAnim(mv, ce, i, 0, d(0.65));
                mv.add(new MoveTo(hole.shape, h.px(c), h.py(c), d(0.65)).setEasing(Easing.EASE_IN_OUT));
                if (hole.cell != null) hole.cell.moveTo(mv, h.ax(c), h.ay, 0, d(0.65));
                playAll(mv);
                h.a[i] = ce;
                h.a[c] = null;
                hole.idx = c;
                tickMove();
                i = c;
                pause(0.35 * sp);
            } else {
                if (explain) narr(last.v + " > " + ce.v + ": the hole stops here.", Colors.GREEN);
                pause(0.5 * sp);
                break;
            }
        }
        if (explain && 2 * i > m) narr("The hole has no children left: it stops here.", Colors.GREEN);
        pause(0.4 * sp);
        if (explain) narr("Now " + last.v + " fills the hole at index " + i + ".", Colors.PINK);
        List<Animation> fin = new ArrayList<>();
        h.moveAnim(fin, last, i, 70, d(0.9));
        fin.add(new FadeOut(hole.shape, d(0.8)));
        if (hole.cell != null) hole.cell.fadeOut(fin, d(0.8));
        if (n >= 2) fin.add(new EraseLink(h.edge[n], d(0.6)));
        h.a[n] = null;
        h.a[i] = last;
        playAll(fin);
        tickMove();
        h.n = m;
        h.paint(last, OK_C, d(0.4));
        pause(0.5 * sp);
        return removed;
    }
}
