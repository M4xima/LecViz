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
 * Standalone clip for slide 23 of the arrays deck: heapsort.
 *
 *   - the slide comes together line by line: the four lines in the pen-stroke style, each with its
 *     gray cost note beside it, the "2N space" line, the callout "Can we avoid the second array?"
 *     and the listing; then everything swipes off and the listing glides to the top right
 *   - the array is read as a complete binary tree; a heap is built (sift-downs with swaps along
 *     the tree), then N deleteMax steps move each maximum into a second array — the 2N-space
 *     version the slide asks about
 *   - the same sort then runs again with hide_back: each deleted maximum is swapped to the back of
 *     the same array and turns green, so no second array is needed
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSHeapSortScene extends PDSSortClipBase {

    private static final int[] DATA = {3, 9, 2, 8, 5, 7, 1};
    private static final int N = DATA.length;
    // tree positions of the 7 array slots, and the cell rows under / beside the tree
    private static final double[] NX = {-330, -460, -200, -525, -395, -265, -135};
    private static final double[] NY = {-250, -135, -135, -20, -20, -20, -20};
    private static final double CELL_Y = 100;

    private StrokeTextMob head;
    private CodeBox cb;
    private TextMob verdict, counters;
    private StrokeTextMob lesson;

    private double cellX(int p) { return -330 + (p - 3) * 84; }
    private double outX(int p) { return 430 + (p - 3) * 84; }

    @Override
    public void construct() {
        slideIntro();
        runDemo();
    }

    // ── the slide ────────────────────────────────────────────────────

    private void slideIntro() {
        head = writeHeading("Heapsort");
        pause(0.5);
        String[] lines = {"Given N elements,", "build a heap and", "then perform N deleteMax,", "store each element into an array."};
        String[] notes = {"N storage", "O(N) time", "O(N log N) time", "O(N) time and N space"};
        double[] ys = {-330, -255, -180, -105};
        List<List<MObject>> groups = new ArrayList<>();
        for (int k = 0; k < 4; k++) {
            StrokeTextMob t = strokeLeft(lines[k], -860, ys[k], 38, Colors.WHITE);
            TextMob note = label(notes[k], -120, ys[k], 26, Colors.GRAY, true, false);
            play(new Write(t, d(Math.max(1.3, lines[k].length() * 0.06))));
            play(new FadeIn(note, d(0.6)));
            List<MObject> g = new ArrayList<>();
            g.add(t);
            g.add(note);
            groups.add(g);
            pause(0.4);
        }
        LineMob sep = new LineMob(-120, -55, 250, -55, Colors.GRAY, 3);
        add(sep);
        TextMob twoN = label("O(N log N) time and 2N space", -120, 5, 26, Colors.GRAY, true, false);
        play(new DrawLine(sep, d(0.6)));
        play(new FadeIn(twoN, d(0.7)));
        List<MObject> g5 = new ArrayList<>();
        g5.add(sep);
        g5.add(twoN);
        groups.add(g5);
        pause(0.6);

        CodeBox box = new CodeBox(new String[]{
            "for (int ii = 0; ii < nelements; ++ii) {",
            "   h.hide_back(h.deleteMax());",
            "}",
            "h.printArray(nelements);"}, -860, 150, 24, 38);
        box.typeIn(3.0);
        TextMob src = label("Source: heap-sort.cpp", -860, 330, 24, Colors.LIGHT_GRAY, true, true);
        play(new FadeIn(src, d(0.5)));
        List<MObject> co = new ArrayList<>();
        List<Animation> anim = new ArrayList<>();
        callout(anim, co, "Can we avoid the", "second array?", 470, 105, 360, 100, 150, 20);
        playAll(anim);
        groups.add(co);
        pause(2.2);

        List<Animation> out = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++)
            for (MObject m : groups.get(i))
                if (m.getOpacity() > 0) out.add(new SwipeOut(m, 900, 0.1 * i * PACE, d(1.0)));
        if (src.getOpacity() > 0) out.add(new SwipeOut(src, 900, 0.1 * groups.size() * PACE, d(1.0)));
        box.shift(out, 980, -520, d(1.3));
        playAll(out);
        for (List<MObject> g : groups) for (MObject m : g) remove(m);
        remove(src);
        cb = box;
        pause(0.4);
    }

    // ── tokens: a value that lives both as a tree node and as an array cell ──

    private final class Tok {
        final int value;
        final CircleMob node;
        final TextMob nodeLbl;
        final RectMob cell;
        final TextMob cellLbl;

        Tok(int v, int pos) {
            value = v;
            Color c = valueColor(v, 1, 9);
            node = new CircleMob(30);
            node.setFillColor(Colors.withAlpha(c, 0.45));
            node.setStrokeColor(c);
            node.setStrokeWidth(3);
            node.setPosition(NX[pos], NY[pos]);
            node.setOpacity(0);
            add(node);
            nodeLbl = label(String.valueOf(v), NX[pos], NY[pos], 28, Colors.WHITE, false, true);
            cell = new RectMob(76, 60).setCornerRadius(8);
            cell.setFillColor(Colors.withAlpha(c, 0.35));
            cell.setStrokeColor(c);
            cell.setStrokeWidth(2.5);
            cell.setPosition(cellX(pos), CELL_Y);
            cell.setOpacity(0);
            add(cell);
            cellLbl = label(String.valueOf(v), cellX(pos), CELL_Y, 30, Colors.WHITE, false, true);
        }

        List<MObject> parts() {
            List<MObject> l = new ArrayList<>();
            l.add(node);
            l.add(nodeLbl);
            l.add(cell);
            l.add(cellLbl);
            return l;
        }

        void moveTo(List<Animation> into, int pos, double nodeBulge, double cellBulge, double dur) {
            into.add(new ArcMove(node, NX[pos], NY[pos], nodeBulge, dur));
            into.add(new ArcMove(nodeLbl, NX[pos], NY[pos], nodeBulge, dur));
            into.add(new ArcMove(cell, cellX(pos), CELL_Y, cellBulge, dur));
            into.add(new ArcMove(cellLbl, cellX(pos), CELL_Y, cellBulge, dur));
        }

        void tint(List<Animation> into, Color c, double dur) {
            into.add(new ColorChange(node, Colors.withAlpha(c, 0.5), dur));
            into.add(new ColorChange(node, c, dur, ColorChange.Target.STROKE));
        }

        void untint(List<Animation> into, double dur) {
            Color c = valueColor(value, 1, 9);
            tint(into, c, dur);
        }
    }

    // ── the demo ─────────────────────────────────────────────────────

    private Tok[] heap;
    private LineMob[] edge;
    private int size;
    private boolean inPlace;

    private void runDemo() {
        verdict = label("", 440, -185, 28, Colors.WHITE, false, true);
        counters = label("", 440, -135, 28, Colors.GOLD, false, true);
        play(new FadeIn(verdict, d(0.4)), new FadeIn(counters, d(0.4)));
        play(new FadeIn(cb.hl, d(0.4)));
        cb.setLine(0);

        // version 1: build the heap, then N deleteMax into a second array
        inPlace = false;
        List<MObject> stage1 = setUp(DATA, true);
        counters.setText("storage: N cells");
        StrokeTextMob c0 = stroke("The array is read as a complete binary tree.", -330, -330, 26, Colors.LIGHT_GRAY, false);
        play(new Write(c0, d(2.4)));
        pause(0.8);
        play(new FadeOut(c0, d(0.5)));
        remove(c0);
        buildHeap();
        pause(0.6);
        StrokeTextMob c1 = stroke("Now N deleteMax: each maximum goes into a second array.", -330, -330, 24, Colors.LIGHT_GRAY, false);
        play(new Write(c1, d(2.8)));
        stage1.add(c1);
        TextMob outLab = label("second array", 430, 40, 26, Colors.PINK, false, true);
        List<Animation> outIn = new ArrayList<>();
        outIn.add(new FadeIn(outLab, d(0.6)));
        List<RectMob> outCells = new ArrayList<>();
        for (int p = 0; p < N; p++) {
            RectMob oc = new RectMob(76, 60).setCornerRadius(8);
            oc.setFillColor(GHOST_FILL);
            oc.setStrokeColor(Colors.withAlpha(Colors.PINK, 0.6));
            oc.setStrokeWidth(2.5);
            oc.setPosition(outX(p), CELL_Y);
            oc.setOpacity(0);
            add(oc);
            outCells.add(oc);
            outIn.add(new FadeInAt(oc, 0.06 * p, d(0.5)));
            stage1.add(oc);
        }
        stage1.add(outLab);
        playAll(outIn);
        counters.setText("storage: N + N = 2N cells");
        pause(0.5);
        play(cb.moveHl(0, d(0.3)));
        for (int k = 0; k < N; k++) {
            double s = k == 0 ? 1.0 : (k == 1 ? 0.6 : 0.35);
            play(cb.moveHl(1, d(0.3 * s)));
            deleteMaxIntoSecondArray(k, s);
        }
        play(cb.moveHl(3, d(0.3)));
        verdict.setText("h.printArray: 1 2 3 5 7 8 9");
        verdict.setFillColor(Colors.GREEN);
        pause(1.0);
        StrokeTextMob cost = stroke("O(N) to build + N deleteMax at O(log N) each = O(N log N) time, but 2N space.",
                -100, 300, 26, Colors.ORANGE, false);
        play(new Write(cost, d(3.6)));
        stage1.add(cost);
        pause(1.0);
        StrokeTextMob ask = stroke("Can we avoid the second array?", -100, 360, 34, Colors.GOLD, false);
        play(new Write(ask, d(2.0)));
        stage1.add(ask);
        pause(1.6);
        StrokeTextMob yes = stroke("Yes — hide_back: the deleted maximum goes to the back of the same array.", -100, 420, 26, Colors.GREEN, false);
        play(new Write(yes, d(3.4)));
        stage1.add(yes);
        pause(2.0);
        fadeOutAll(d(0.9), stage1);
        verdict.setText("");
        pause(0.3);

        // version 2: the same sort, in place
        inPlace = true;
        int[] built = {9, 8, 7, 3, 5, 2, 1};
        List<MObject> stage2 = setUp(built, false);
        counters.setText("storage: N cells (in place)");
        StrokeTextMob c2 = stroke("hide_back: swap the maximum with the last element, then shrink the heap.",
                -330, -330, 24, Colors.LIGHT_GRAY, false);
        play(new Write(c2, d(3.4)));
        stage2.add(c2);
        pause(0.8);
        play(cb.moveHl(0, d(0.3)));
        for (int k = 0; k < N; k++) {
            double s = k == 0 ? 1.0 : (k == 1 ? 0.6 : 0.35);
            play(cb.moveHl(1, d(0.3 * s)));
            deleteMaxInPlace(k, s);
        }
        play(cb.moveHl(3, d(0.3)));
        verdict.setText("sorted in the same array: 1 2 3 5 7 8 9");
        verdict.setFillColor(Colors.GREEN);
        pause(1.0);
        StrokeTextMob fin = stroke("Still O(N log N) time — now with just N storage.", -100, 300, 30, Colors.ORANGE, false);
        play(new Write(fin, d(2.6)));
        stage2.add(fin);
        pause(2.8);

        List<MObject> all = new ArrayList<>(stage2);
        all.addAll(cb.parts());
        all.add(verdict);
        all.add(counters);
        all.add(head);
        fadeOutAll(1.5, all);
        pause(0.5);
    }

    /** Creates tokens, tree edges and array index labels for a fresh run; returns everything made. */
    private List<MObject> setUp(int[] vals, boolean showIdx) {
        List<MObject> made = new ArrayList<>();
        List<Animation> in = new ArrayList<>();
        edge = new LineMob[N];
        for (int p = 1; p < N; p++) {
            int par = (p - 1) / 2;
            edge[p] = new LineMob(NX[par], NY[par], NX[p], NY[p], Colors.withAlpha(Colors.WHITE, 0.35), 3);
            add(edge[p]);
            made.add(edge[p]);
            in.add(new FadeInAt(edge[p], 0.15 + 0.08 * p, d(0.5)));
        }
        heap = new Tok[N];
        for (int p = 0; p < N; p++) {
            heap[p] = new Tok(vals[p], p);
            for (MObject m : heap[p].parts()) {
                in.add(new FadeInAt(m, 0.1 * p, d(0.6)));
                made.add(m);
            }
            TextMob idx = label(String.valueOf(p), cellX(p), CELL_Y + 48, 22, Colors.GRAY, false, false);
            in.add(new FadeInAt(idx, 0.1 * p, d(0.6)));
            made.add(idx);
        }
        playAll(in);
        size = N;
        pause(0.5);
        return made;
    }

    private void swapPos(int i, int j, double dur) {
        Tok a = heap[i], b = heap[j];
        List<Animation> sw = new ArrayList<>();
        a.moveTo(sw, j, 40, 70, dur);
        b.moveTo(sw, i, 40, -30, dur);
        playAll(sw);
        heap[i] = b;
        heap[j] = a;
    }

    private void siftDown(int i, int sz, double s) {
        while (true) {
            int l = 2 * i + 1, r = 2 * i + 2, largest = i;
            if (l < sz && heap[l].value > heap[largest].value) largest = l;
            if (r < sz && heap[r].value > heap[largest].value) largest = r;
            List<Animation> look = new ArrayList<>();
            heap[i].tint(look, Colors.ORANGE, d(0.25 * s));
            if (l < sz) heap[l].tint(look, Colors.ORANGE, d(0.25 * s));
            if (r < sz) heap[r].tint(look, Colors.ORANGE, d(0.25 * s));
            playAll(look);
            if (l >= sz) verdict.setText(heap[i].value + " has no children: stop");
            else if (largest == i) verdict.setText(heap[i].value + " ≥ its children: stop");
            else verdict.setText(heap[i].value + " < " + heap[largest].value + ": swap with the "
                    + (largest == l ? "left" : "right") + " child");
            verdict.setFillColor(largest == i ? Colors.LIGHT_GRAY : Colors.ORANGE);
            pause(0.5 * s);
            List<Animation> calm = new ArrayList<>();
            heap[i].untint(calm, d(0.25 * s));
            if (l < sz) heap[l].untint(calm, d(0.25 * s));
            if (r < sz) heap[r].untint(calm, d(0.25 * s));
            playAll(calm);
            if (largest == i) break;
            swapPos(i, largest, d(0.7 * s));
            i = largest;
        }
    }

    private void buildHeap() {
        StrokeTextMob c = stroke("Build the heap: sift down from the last parent up to the root.", -330, -330, 24, Colors.LIGHT_GRAY, false);
        play(new Write(c, d(3.0)));
        for (int i = N / 2 - 1; i >= 0; i--) {
            double s = i == N / 2 - 1 ? 1.0 : 0.7;
            siftDown(i, N, s);
        }
        verdict.setText("a max-heap: every parent ≥ its children");
        verdict.setFillColor(Colors.GREEN);
        pause(1.0);
        play(new FadeOut(c, d(0.5)));
        remove(c);
    }

    /** Version 1: the root moves to the second array, the last leaf takes its place, then sift down. */
    private void deleteMaxIntoSecondArray(int k, double s) {
        Tok root = heap[0], last = heap[size - 1];
        int slot = N - 1 - k;
        verdict.setText("deleteMax = " + root.value + "   →   second array, slot " + slot);
        verdict.setFillColor(Colors.PINK);
        List<Animation> mv = new ArrayList<>();
        mv.add(new ArcMove(root.cell, outX(slot), CELL_Y, 90, d(0.9 * s)));
        mv.add(new ArcMove(root.cellLbl, outX(slot), CELL_Y, 90, d(0.9 * s)));
        mv.add(new FadeOut(root.node, d(0.6 * s)));
        mv.add(new FadeOut(root.nodeLbl, d(0.6 * s)));
        mv.add(new ColorChange(root.cell, Colors.withAlpha(Colors.PINK, 0.4), d(0.8 * s)));
        mv.add(new ColorChange(root.cell, Colors.PINK, d(0.8 * s), ColorChange.Target.STROKE));
        if (size > 1) {
            last.moveTo(mv, 0, 30, 40, d(0.9 * s));
            mv.add(new FadeOut(edge[size - 1], d(0.5 * s)));
        }
        playAll(mv);
        if (size > 1) heap[0] = last;
        heap[size - 1] = null;
        size--;
        pause(0.2 * s);
        if (size > 1) siftDown(0, size, s);
    }

    /** Version 2: swap the root with the last element, hide it at the back, shrink the heap. */
    private void deleteMaxInPlace(int k, double s) {
        Tok root = heap[0];
        int back = size - 1;
        verdict.setText("deleteMax = " + root.value + "   →   hide_back: slot " + back);
        verdict.setFillColor(Colors.GREEN);
        if (back > 0) swapPos(0, back, d(0.9 * s));
        Tok hidden = heap[back];
        List<Animation> hide = new ArrayList<>();
        hide.add(new FadeOut(hidden.node, d(0.5 * s)));
        hide.add(new FadeOut(hidden.nodeLbl, d(0.5 * s)));
        hide.add(new ColorChange(hidden.cell, FINAL_FILL, d(0.6 * s)));
        hide.add(new ColorChange(hidden.cell, FINAL_STROKE, d(0.6 * s), ColorChange.Target.STROKE));
        if (back > 0) hide.add(new FadeOut(edge[back], d(0.5 * s)));
        playAll(hide);
        size--;
        pause(0.2 * s);
        if (size > 1) siftDown(0, size, s);
    }
}
