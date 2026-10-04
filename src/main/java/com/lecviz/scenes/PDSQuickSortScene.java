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
 * Standalone clip for slide 24 of the arrays deck: quicksort.
 *
 *   - the slide text comes one line at a time in the pen-stroke style; the listing types in with
 *     its "Crucially decides the complexity" callout on the partition call
 *   - the text swipes off and a second listing shows what partition does; the code then runs on
 *     eight bars: the pivot is marked, j scans, the values smaller than the pivot are swapped to
 *     the left of i, the pivot lands at its final place, and the two sides are sorted the same way
 *     while everything outside the current range dims
 *   - a last picture shows why the callout is right: balanced splits give log n levels, lopsided
 *     splits give n levels
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSQuickSortScene extends PDSSortClipBase {

    private static final int[] DATA = {8, 5, 7, 4, 3, 1, 6, 2};
    private static final double BASE = 260;

    private StrokeTextMob head;
    private CodeBox cq, cp;       // quick() and partition()
    private TextMob readout, verdict, counters;
    private Bars bars;
    private RectMob rangeBand;
    private TextMob rangeLab;
    private boolean[] done;
    private int comps = 0, swaps = 0, partitions = 0;
    private Ptr pi, pj;
    private RectMob pivotRing;
    private TextMob pivotTag;
    private boolean pointersShown = false;

    @Override
    public void construct() {
        head = writeHeading("Quicksort");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Approach:"));
        s.add(ln(1, "Choose an arbitrary element (called pivot).").kw("pivot", Colors.RED));
        s.add(ln(1, "Place the pivot at its final place."));
        s.add(ln(1, "Make sure all the elements smaller than the pivot"));
        s.add(ln(2, "are to the left of it, and ... (called partitioning)").kw("partitioning", Colors.RED));
        s.add(ln(1, "Divide-and-conquer."));
        List<List<MObject>> text = writeSlide(s, -365);
        pause(0.5);

        CodeBox box = new CodeBox(new String[]{
            "void quick(int start, int end) {",
            "   if (start < end) {",
            "      int iipivot = partition(start, end);",
            "      quick(start, iipivot - 1);",
            "      quick(iipivot + 1, end);",
            "   }",
            "}"}, -860, 60, 26, 40);
        box.typeIn(3.8);
        List<MObject> stage = new ArrayList<>();
        List<Animation> co = new ArrayList<>();
        callout(co, stage, "Crucially decides", "the complexity.", 90, box.lineY(2) - 6, 300, 96, box.lineEndX(2) + 12, box.lineY(2));
        playAll(co);
        pause(1.8);

        // text and callout leave; quick() glides to the top left and partition() types in under it
        List<Animation> out = new ArrayList<>();
        for (int i = 0; i < text.size(); i++)
            for (MObject m : text.get(i))
                if (m.getOpacity() > 0) out.add(new SwipeOut(m, 900, 0.1 * i * PACE, d(1.0)));
        for (MObject m : stage) if (m.getOpacity() > 0) out.add(new FadeOut(m, d(0.6)));
        box.shift(out, 0, -445, d(1.2));
        playAll(out);
        for (List<MObject> g : text) for (MObject m : g) remove(m);
        for (MObject m : stage) remove(m);
        cq = box;

        cp = new CodeBox(new String[]{
            "int pivot = arr[end];",
            "int i = start - 1;",
            "for (j = start; j < end; ++j)",
            "   if (arr[j] < pivot) swap(++i, j);",
            "swap(i + 1, end);",
            "return i + 1;"}, -860, -50, 24, 38);
        cp.typeIn(3.0);
        pause(0.6);

        readout = label("", 440, -310, 28, Colors.WHITE, false, true);
        readout.setFontFamily("Menlo");
        verdict = label("", 440, -255, 32, Colors.WHITE, false, true);
        counters = label("", 440, -205, 28, Colors.GOLD, false, true);
        bars = new Bars(DATA, 440, BASE, 100, 70, 30, 30, true);
        done = new boolean[DATA.length];
        List<MObject> mine = new ArrayList<>(bars.parts());
        List<Animation> in = new ArrayList<>();
        bars.fadeIn(in, 0.1, d(0.6));
        in.add(new FadeIn(readout, d(0.4)));
        in.add(new FadeIn(verdict, d(0.4)));
        in.add(new FadeIn(counters, d(0.4)));
        pi = pointer("i", bars.slotX(0), BASE + 52, false, Colors.GOLD);
        pj = pointer("j", bars.slotX(0), BASE + 52, false, Colors.TEAL);
        mine.addAll(pi.parts());
        mine.addAll(pj.parts());
        rangeBand = new RectMob(8 * 100 - 14, 12).setCornerRadius(6);
        rangeBand.setFillColor(Colors.withAlpha(Colors.TEAL, 0.5));
        rangeBand.setStrokeColor(Colors.TEAL);
        rangeBand.setPosition(440, BASE + 150);
        rangeBand.setOpacity(0);
        add(rangeBand);
        rangeLab = label("", 440, BASE + 182, 26, Colors.TEAL, false, true);
        pivotRing = new RectMob(86, 100).setCornerRadius(12);
        pivotRing.setFillColor(Color.TRANSPARENT);
        pivotRing.setStrokeColor(Colors.GOLD);
        pivotRing.setStrokeWidth(4.5);
        pivotRing.setOpacity(0);
        add(pivotRing);
        pivotTag = label("pivot", 0, 0, 24, Colors.GOLD, false, true);
        mine.add(rangeBand);
        mine.add(rangeLab);
        mine.add(pivotRing);
        mine.add(pivotTag);
        playAll(in);
        counters.setText("comparisons: 0     swaps: 0");
        play(new FadeIn(cq.hl, d(0.4)));
        pause(0.4);

        quick(0, DATA.length - 1);

        List<Animation> fin = new ArrayList<>();
        for (SBar b : bars.at) bars.paintFinal(fin, b, d(0.6));
        fin.add(new FadeOut(rangeBand, d(0.4)));
        fin.add(new FadeOut(rangeLab, d(0.4)));
        playAll(fin);
        verdict.setText("sorted");
        verdict.setFillColor(Colors.GREEN);
        readout.setText("");
        pause(1.4);

        List<MObject> demo = new ArrayList<>(mine);
        demo.addAll(cq.parts());
        demo.addAll(cp.parts());
        demo.add(readout);
        demo.add(verdict);
        demo.add(counters);
        fadeOutAll(d(0.8), demo);
        pause(0.3);
        pivotMatters();
        fadeOutAll(1.5, head);
        pause(0.5);
    }

    // ── the recursion ────────────────────────────────────────────────

    /** Dims everything outside [start, end]; finished bars stay green. */
    private void focus(int start, int end, double s) {
        List<Animation> an = new ArrayList<>();
        for (int i = 0; i < bars.n; i++) {
            if (done[i]) continue;
            if (i >= start && i <= end) bars.paintOriginal(an, bars.at[i], d(0.35 * s));
            else bars.paint(an, bars.at[i], Colors.GRAY, d(0.35 * s));
        }
        if (end >= start) {
            double w = (end - start + 1) * bars.pitch - 14;
            double cx = (bars.slotX(start) + bars.slotX(end)) / 2;
            if (rangeBand.getOpacity() < 1) an.add(new FadeIn(rangeBand, d(0.4)));
            an.add(new ResizeX(rangeBand, w, cx, d(0.5 * s)));
            rangeLab.setText("quick(" + start + ", " + end + ")");
            an.add(new MoveTo(rangeLab, cx, BASE + 182, d(0.5 * s)).setEasing(Easing.EASE_IN_OUT));
            if (rangeLab.getOpacity() < 1) an.add(new FadeIn(rangeLab, d(0.4)));
        }
        playAll(an);
    }

    private void quick(int start, int end) {
        double s = partitions == 0 ? 1.0 : (partitions == 1 ? 0.6 : 0.4);
        readout.setText(String.format("quick(%d, %d)", start, end));
        play(cb().moveHl(0, d(0.3 * s)));
        play(cb().moveHl(1, d(0.25 * s)));
        if (start > end) {
            verdict.setText("empty range: nothing to do");
            verdict.setFillColor(Colors.LIGHT_GRAY);
            pause(0.3 * s);
            return;
        }
        if (start == end) {
            verdict.setText("one element: already in place");
            verdict.setFillColor(Colors.GREEN);
            focus(start, end, s);
            List<Animation> g = new ArrayList<>();
            bars.paintFinal(g, bars.at[start], d(0.4 * s));
            playAll(g);
            done[start] = true;
            pause(0.3 * s);
            return;
        }
        focus(start, end, s);
        play(cb().moveHl(2, d(0.3 * s)));
        int ip = partition(start, end, s);
        readout.setText(String.format("quick(%d, %d)   iipivot = %d", start, end, ip));
        play(cb().moveHl(3, d(0.3 * s)));
        quick(start, ip - 1);
        play(cb().moveHl(4, d(0.3 * s)));
        quick(ip + 1, end);
    }

    private CodeBox cb() { return cq; }

    private int partition(int start, int end, double s) {
        partitions++;
        SBar pivot = bars.at[end];
        // line 0: int pivot = arr[end]
        play(cp.moveHl(0, d(0.3 * s)));
        if (cp.hl.getOpacity() < 1) play(new FadeIn(cp.hl, d(0.3)));
        verdict.setText("pivot = " + pivot.value + "  (the last element)");
        verdict.setFillColor(Colors.GOLD);
        pivotRing.setSize(bars.bw + 16, pivot.h + 16);
        pivotRing.setPosition(bars.slotX(end), BASE - pivot.h / 2);
        pivotTag.setPosition(bars.slotX(end), BASE - pivot.h - 62);
        List<Animation> mark = new ArrayList<>();
        mark.add(new FadeIn(pivotRing, d(0.4)));
        mark.add(new FadeIn(pivotTag, d(0.4)));
        playAll(mark);
        pause(0.4 * s);

        // line 1: int i = start - 1
        int i = start - 1;
        play(cp.moveHl(1, d(0.3 * s)));
        List<Animation> ptr = new ArrayList<>();
        pi.go(ptr, bars.slotX(i), d(0.4 * s));
        pj.go(ptr, bars.slotX(start), d(0.4 * s));
        if (!pointersShown) {
            ptr.add(new FadeIn(pi.arrow, d(0.4)));
            ptr.add(new FadeIn(pi.lab, d(0.4)));
            ptr.add(new FadeIn(pj.arrow, d(0.4)));
            ptr.add(new FadeIn(pj.lab, d(0.4)));
            pointersShown = true;
        }
        playAll(ptr);
        readout.setText(String.format("start = %d  end = %d  i = %d", start, end, i));
        pause(0.2 * s);

        for (int j = start; j < end; j++) {
            List<Animation> scan = new ArrayList<>();
            scan.add(cp.moveHl(2, d(0.25 * s)));
            pj.go(scan, bars.slotX(j), d(0.35 * s));
            playAll(scan);
            play(cp.moveHl(3, d(0.25 * s)));
            comps++;
            boolean smaller = bars.at[j].value < pivot.value;
            verdict.setText(bars.at[j].value + " < " + pivot.value + " ?   " + (smaller ? "yes → goes left of i's boundary" : "no → stays right"));
            verdict.setFillColor(smaller ? Colors.TEAL : Colors.PINK);
            counters.setText("comparisons: " + comps + "     swaps: " + swaps);
            List<Animation> tint = new ArrayList<>();
            bars.paint(tint, bars.at[j], smaller ? Colors.TEAL : Colors.PINK, d(0.3 * s));
            playAll(tint);
            pause(0.4 * s);
            if (smaller) {
                i++;
                List<Animation> mv = new ArrayList<>();
                pi.go(mv, bars.slotX(i), d(0.4 * s));
                if (i != j) {
                    bars.swap(mv, Math.min(i, j), Math.max(i, j), d(0.75 * s));
                    swaps++;
                    verdict.setText("swap(" + i + ", " + j + ")");
                    verdict.setFillColor(Colors.ORANGE);
                }
                playAll(mv);
                counters.setText("comparisons: " + comps + "     swaps: " + swaps);
                readout.setText(String.format("start = %d  end = %d  i = %d", start, end, i));
            }
            pause(0.12 * s);
        }

        // line 4: swap(i + 1, end) — the pivot lands at its final place
        play(cp.moveHl(4, d(0.3 * s)));
        int spot = i + 1;
        List<Animation> land = new ArrayList<>();
        if (spot != end) {
            bars.swap(land, spot, end, d(0.85 * s));
            swaps++;
            verdict.setText("swap(" + spot + ", " + end + "):  the pivot goes to slot " + spot);
        } else {
            verdict.setText("the pivot is already in slot " + spot);
        }
        verdict.setFillColor(Colors.ORANGE);
        land.add(new FadeOut(pivotRing, d(0.5 * s)));
        land.add(new FadeOut(pivotTag, d(0.5 * s)));
        playAll(land);
        counters.setText("comparisons: " + comps + "     swaps: " + swaps);
        List<Animation> fin = new ArrayList<>();
        bars.paintFinal(fin, bars.at[spot], d(0.5 * s));
        playAll(fin);
        done[spot] = true;
        play(cp.moveHl(5, d(0.3 * s)));
        verdict.setText("return " + spot + ":  everything left is smaller, everything right is bigger");
        verdict.setFillColor(Colors.GREEN);
        pause(0.8 * s);
        return spot;
    }

    // ── why the pivot decides the complexity ─────────────────────────

    private void pivotMatters() {
        List<MObject> made = new ArrayList<>();
        List<Animation> drop = new ArrayList<>();
        StrokeTextMob title = stroke("Crucially, the pivot decides the complexity.", 0, -380, 36, Colors.WHITE, false);
        play(new Write(title, d(2.6)));
        made.add(title);

        // balanced: 1 -> 2 -> 4 equal parts; lopsided: n -> n-1 -> n-2 ...
        double wMax = 640, rowH = 54, top = -270;
        String[] names = {"balanced splits", "lopsided splits"};
        double[] cxs = {-430, 430};
        for (int side = 0; side < 2; side++) {
            TextMob nm = label(names[side], cxs[side], top - 40, 30, side == 0 ? Colors.GREEN : Colors.RED, false, true);
            made.add(nm);
            drop.add(new DropIn(nm, 60, 0, d(0.8)));
            int levels = side == 0 ? 4 : 8;
            for (int lv = 0; lv < levels; lv++) {
                double y = top + lv * (side == 0 ? rowH + 18 : rowH * 0.62);
                double start = d(0.35) * lv;
                if (side == 0) {
                    int parts = 1 << lv;
                    double w = (wMax - (parts - 1) * 10) / parts;
                    for (int p = 0; p < parts; p++) {
                        double x = cxs[side] - wMax / 2 + p * (w + 10) + w / 2;
                        RectMob r = bar(x, y, w, rowH * 0.5, Colors.GREEN);
                        drop.add(new DropIn(r, 50, start, d(0.7)));
                        made.add(r);
                    }
                } else {
                    double w = wMax * (levels - lv) / levels;
                    RectMob r = bar(cxs[side] - wMax / 2 + w / 2, y, w, rowH * 0.42, Colors.RED);
                    drop.add(new DropIn(r, 50, start, d(0.7)));
                    made.add(r);
                    if (lv > 0) {
                        RectMob one = bar(cxs[side] - wMax / 2 + wMax * (levels - lv + 1) / levels - 8, y, 14, rowH * 0.42, Colors.GRAY);
                        drop.add(new DropIn(one, 50, start, d(0.7)));
                        made.add(one);
                    }
                }
            }
        }
        playAll(drop);
        pause(0.6);
        TextMob l1 = label("about log n levels  →  O(n log n)", cxs[0], 260, 30, Colors.GREEN, false, true);
        TextMob l2 = label("about n levels  →  O(n²) in the worst case", cxs[1], 260, 30, Colors.RED, false, true);
        play(new FadeIn(l1, d(0.6)), new FadeIn(l2, d(0.6)));
        made.add(l1);
        made.add(l2);
        StrokeTextMob c = stroke("Our first pivot above split 1 | 6 — a lopsided start. A good pivot keeps the two sides balanced.",
                0, 360, 26, Colors.LIGHT_GRAY, false);
        play(new Write(c, d(3.6)));
        made.add(c);
        pause(3.0);
        fadeOutAll(d(0.9), made);
    }

    private RectMob bar(double x, double y, double w, double h, Color c) {
        RectMob r = new RectMob(w, h).setCornerRadius(5);
        r.setFillColor(Colors.withAlpha(c, 0.4));
        r.setStrokeColor(c);
        r.setStrokeWidth(2);
        r.setPosition(x, y);
        r.setOpacity(0);
        add(r);
        return r;
    }
}
