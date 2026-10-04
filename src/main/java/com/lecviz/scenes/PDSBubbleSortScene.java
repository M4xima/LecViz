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
 * Standalone clip for slides 16-17 of the arrays deck: bubble sort.
 *
 *   Slide 16  the text comes one line at a time in the pen-stroke style, then swipes
 *             off to the right
 *   Slide 17  the two code listings type in with their callouts; the second one is then
 *             run on bars with a highlight band following the executing line, pointers
 *             under the pair being compared, swaps that arc over each other, and the
 *             numbers that reach their final place turning green. A reverse-sorted run
 *             follows an element that moves away from its final place, a sorted run
 *             shows the best case, and the slide's remaining points come one by one
 *             next to the comparisons/swaps counted in the three runs.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSBubbleSortScene extends PDSSortClipBase {

    private StrokeTextMob head;

    @Override
    public void construct() {
        slide16();
        slide17();
    }

    // ── slide 16: the text ───────────────────────────────────────────

    private void slide16() {
        head = writeHeading("Bubble Sort");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Compare adjacent values and swap, if required.").kw("adjacent", Colors.GOLD));
        s.add(ln(0, "How many times do we need to do it?"));
        s.add(ln(0, "What is the invariant?").kw("invariant", Colors.BLUE));
        s.add(ln(1, "After ith iteration, i largest numbers are at their final places."));
        s.add(ln(1, "An element may move away from its final position in the").kw("away", Colors.ORANGE));
        s.add(ln(2, "intermediate stages (e.g., check the 2nd element of a"));
        s.add(ln(2, "reverse-sorted array)."));
        s.add(ln(0, "Best case: Sorted sequence").kw("Best", Colors.GREEN));
        s.add(ln(0, "Worst case: Reverse sorted").tail("(n-1 + n-2 + ... + 1 + 0)").kw("Worst", Colors.RED));
        s.add(ln(0, "Classwork: Write the code.").kw("Classwork", Colors.ORANGE));
        List<List<MObject>> groups = writeSlide(s, -365);
        List<MObject> headGroup = new ArrayList<>();
        headGroup.add(head);
        groups.add(0, headGroup);
        pause(0.9);
        swipeAway(groups);
        pause(0.5);
    }

    // ── slide 17: the code, run on bars ──────────────────────────────

    private CodeBox cb;
    private TextMob readout, counters, verdict;
    private StrokeTextMob lesson;

    private static Color valueColor(int v, int n) {
        return Colors.interpolate(Colors.BLUE, Colors.ORANGE, n <= 1 ? 0 : (v - 1) / (double) (n - 1));
    }

    private final class Bar {
        final RectMob rect;
        final TextMob lbl;
        final int value;
        final double h;

        Bar(int value, int n, double x, double baseY, double w, double unit) {
            this.value = value;
            this.h = 40 + unit * value;
            Color c = valueColor(value, n);
            rect = new RectMob(w, h).setCornerRadius(8);
            rect.setFillColor(Colors.withAlpha(c, 0.42));
            rect.setStrokeColor(c);
            rect.setStrokeWidth(2.5);
            rect.setPosition(x, baseY - h / 2);
            rect.setOpacity(0);
            add(rect);
            lbl = label(String.valueOf(value), x, baseY - h - 24, 34, Colors.WHITE, false, true);
        }
    }

    private void slide17() {
        head = dropHeading("Bubble Sort");
        pause(0.4);

        // the two listings from the slide, each with its callout
        List<MObject> stage = new ArrayList<>();
        CodeBox box1 = new CodeBox(new String[]{
            "for (ii = 0; ii < N; ++ii)",
            "   for (jj = 0; jj < N - 1; ++jj)",
            "      if (arr[jj] > arr[jj + 1]) swap(jj, jj + 1);"}, -800, -370, 27, 44);
        box1.typeIn(3.2);
        stage.addAll(box1.parts());
        List<Animation> co = new ArrayList<>();
        callout(co, stage, "Not using ii", null, 560, -300, 300, 66, box1.lineEndX(2) + 12, box1.lineY(2));
        box1.setLine(1);
        co.add(new FadeIn(box1.hl, d(0.5)));
        playAll(co);
        StrokeTextMob c1 = stroke("The inner loop never looks at ii: every pass makes N - 1 comparisons, even over numbers already in place.",
                0, 45, 26, Colors.LIGHT_GRAY, false);
        play(new Write(c1, d(4.0)));
        stage.add(c1);
        pause(1.2);
        play(new FadeOut(box1.hl, d(0.4)));

        CodeBox box2 = new CodeBox(new String[]{
            "for (ii = 0; ii < N - 1; ++ii)",
            "   for (jj = 0; jj < N - ii - 1; ++jj)",
            "      if (arr[jj] > arr[jj + 1]) swap(jj, jj + 1);"}, -800, -190, 27, 44);
        box2.typeIn(3.2);
        stage.addAll(box2.parts());
        co = new ArrayList<>();
        callout(co, stage, "O(n²)", null, 560, -120, 300, 66, box2.lineEndX(2) + 12, box2.lineY(2));
        box2.setLine(1);
        co.add(new FadeIn(box2.hl, d(0.5)));
        playAll(co);
        StrokeTextMob c2 = stroke("Each pass stops before the sorted tail: (N-1) + (N-2) + ... + 1 comparisons, so O(n²) in all.",
                0, 110, 26, Colors.LIGHT_GRAY, false);
        play(new Write(c2, d(3.6)));
        stage.add(c2);
        pause(1.6);

        // box 1, the callouts and the captions leave; box 2 glides up to the middle of the top
        List<MObject> leaving = new ArrayList<>(stage);
        leaving.removeAll(box2.parts());
        List<Animation> clear = new ArrayList<>();
        for (MObject m : leaving) if (m.getOpacity() > 0) clear.add(new FadeOut(m, d(0.6)));
        box2.shift(clear, 800 - box2.width / 2, -185, d(1.1));
        clear.add(new FadeOut(box2.hl, d(0.4)));
        playAll(clear);
        for (MObject m : leaving) remove(m);
        cb = box2;
        pause(0.4);

        // readouts shared by the three runs
        readout = label("", 0, -212, 30, Colors.WHITE, false, true);
        readout.setFontFamily("Menlo");
        counters = label("", 0, -160, 30, Colors.GOLD, false, true);
        verdict = label("", 0, -92, 36, Colors.WHITE, false, true);
        play(new FadeIn(readout, d(0.4)), new FadeIn(counters, d(0.4)), new FadeIn(verdict, d(0.4)));

        // run A: a mixed array — how the loops work, how many passes, the invariant
        int[] mixed = {4, 6, 2, 5, 1, 3};
        int[] stats = runBubble(mixed, new double[]{1.0, 0.6, 0.34}, -1, true);
        int mixedSwaps = stats[1];
        // run B: reverse sorted — the 2nd element moves away from its final place; worst case
        int[] rev = {6, 5, 4, 3, 2, 1};
        int[] statsB = runBubble(rev, new double[]{0.55, 0.28}, 5, false);
        // run C: already sorted — best case
        int[] sorted = {1, 2, 3, 4, 5, 6};
        int[] statsC = runBubble(sorted, new double[]{0.3}, -1, false);

        // clear the demo, then slide 17's remaining points one by one
        List<MObject> demo = new ArrayList<>(cb.parts());
        demo.add(readout);
        demo.add(counters);
        demo.add(verdict);
        fadeOutAll(d(0.8), demo);
        pause(0.3);

        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Best case: Sorted sequence").kw("Best", Colors.GREEN));
        s.add(ln(0, "Worst case: Reverse sorted").tail("(n-1 + n-2 + ... + 1 + 0)").kw("Worst", Colors.RED));
        s.add(ln(0, "What do we measure?"));
        s.add(ln(1, "Number of comparisons"));
        s.add(ln(1, "Number of swaps").tail("(bounded by comparisons)"));
        s.add(ln(0, "Number of comparisons remains the same!"));
        List<List<MObject>> groups = writeSlide(s, -330);
        pause(0.5);

        // the three runs, side by side: comparisons never change, swaps do
        List<MObject> table = new ArrayList<>();
        List<Animation> drop = new ArrayList<>();
        double[] colW = {400, 300, 260};
        double gap = 6;
        double total = colW[0] + colW[1] + colW[2] + 2 * gap;
        double[] cx = new double[3];
        double x = -total / 2;
        for (int c = 0; c < 3; c++) {
            cx[c] = x + colW[c] / 2;
            x += colW[c] + gap;
        }
        String[] hdr = {"Input", "Comparisons", "Swaps"};
        String[][] rows = {
            {"Sorted (best case)", String.valueOf(statsC[0]), String.valueOf(statsC[1])},
            {"Mixed", String.valueOf(stats[0]), String.valueOf(mixedSwaps)},
            {"Reverse sorted (worst case)", String.valueOf(statsB[0]), String.valueOf(statsB[1])},
        };
        double[] rowY = {150, 218, 284, 350};
        Color[] swapTint = {Colors.BLUE, Colors.GOLD, Colors.RED};
        for (int r = 0; r < 4; r++) {
            double start = d(0.7) * r;
            for (int c = 0; c < 3; c++) {
                boolean header = r == 0;
                Color base = header ? Colors.BLUE : (c == 0 ? Colors.WHITE : c == 1 ? Colors.GREEN : swapTint[r - 1]);
                RectMob box = new RectMob(colW[c], 62).setCornerRadius(8);
                box.setFillColor(Colors.withAlpha(base, header ? 0.38 : (c == 0 ? 0.07 : 0.2)));
                box.setStrokeColor(Colors.withAlpha(base, header ? 1.0 : (c == 0 ? 0.35 : 0.7)));
                box.setPosition(cx[c], rowY[r]);
                box.setOpacity(0);
                add(box);
                String text = header ? hdr[c] : rows[r - 1][c];
                TextMob t = (c == 0)
                        ? label(text, cx[c] - colW[c] / 2 + 24, rowY[r], 28, Colors.WHITE, true, header)
                        : label(text, cx[c], rowY[r], c == 0 ? 28 : 32, Colors.WHITE, false, true);
                drop.add(new DropIn(box, 70, start, d(0.9)));
                drop.add(new DropIn(t, 70, start, d(0.9)));
                table.add(box);
                table.add(t);
            }
        }
        playAll(drop);
        RectMob same = new RectMob(colW[1] + 14, 3 * 66 + 66).setCornerRadius(12);
        same.setFillColor(Color.TRANSPARENT);
        same.setStrokeColor(Colors.GREEN);
        same.setStrokeWidth(4);
        same.setPosition(cx[1], (rowY[0] + rowY[3]) / 2);
        same.setOpacity(0);
        add(same);
        play(new FadeIn(same, d(0.6)));
        table.add(same);
        pause(3.2);

        List<MObject> all = new ArrayList<>(table);
        all.add(head);
        for (List<MObject> g : groups) all.addAll(g);
        fadeOutAll(1.5, all);
        pause(0.5);
    }

    /**
     * Runs the code in {@code cb} on a fresh set of bars; {@code sp[pass]} scales how long that
     * pass takes. {@code tracked} is the value of a bar to follow (or -1); {@code teach} adds the
     * slide's invariant sentence after the first passes. Returns {comparisons, swaps}.
     */
    private int[] runBubble(int[] data, double[] sp, int tracked, boolean teach) {
        int n = data.length;
        double baseY = 340, pitch = 140, bw = 100, unit = 34;
        double x0 = -(n - 1) * pitch / 2.0;
        java.util.function.IntToDoubleFunction slotX = i -> x0 + pitch * i;

        List<MObject> mine = new ArrayList<>();
        List<Animation> in = new ArrayList<>();
        Bar[] at = new Bar[n];
        for (int i = 0; i < n; i++) {
            at[i] = new Bar(data[i], n, slotX.applyAsDouble(i), baseY, bw, unit);
            in.add(new FadeInAt(at[i].rect, 0.1 * i, d(0.6)));
            in.add(new FadeInAt(at[i].lbl, 0.1 * i, d(0.6)));
            TextMob idx = label(String.valueOf(i), slotX.applyAsDouble(i), baseY + 30, 22, Colors.GRAY, false, false);
            in.add(new FadeInAt(idx, 0.1 * i, d(0.6)));
            mine.add(at[i].rect);
            mine.add(at[i].lbl);
            mine.add(idx);
        }
        Ptr pj = pointer("jj", slotX.applyAsDouble(0), baseY + 52, false, Colors.GOLD);
        Ptr pj1 = pointer("jj+1", slotX.applyAsDouble(1), baseY + 52, false, Colors.TEAL);
        mine.addAll(pj.parts());
        mine.addAll(pj1.parts());
        RectMob ring = new RectMob(pitch + bw + 24, 200).setCornerRadius(14);
        ring.setFillColor(Colors.withAlpha(Colors.ORANGE, 0.1));
        ring.setStrokeColor(Colors.ORANGE);
        ring.setStrokeWidth(4);
        ring.setOpacity(0);
        add(ring);
        mine.add(ring);

        // the tracked element and the slot where it belongs
        RectMob ghost = null;
        TextMob ghostLab = null;
        Bar trackedBar = null;
        if (tracked >= 0) {
            for (Bar b : at) if (b.value == tracked) trackedBar = b;
            int fin = tracked - 1;
            double gh = trackedBar.h + 14;
            ghost = new RectMob(bw + 14, gh).setCornerRadius(10);
            ghost.setFillColor(Color.TRANSPARENT);
            ghost.setStrokeColor(Colors.withAlpha(Colors.GOLD, 0.8));
            ghost.setStrokeWidth(3);
            ghost.setPosition(slotX.applyAsDouble(fin), baseY - trackedBar.h / 2);
            ghost.setOpacity(0);
            add(ghost);
            ghostLab = label("final place of " + tracked, slotX.applyAsDouble(fin), baseY - trackedBar.h - 64, 22, Colors.GOLD, false, true);
            mine.add(ghost);
            mine.add(ghostLab);
            in.add(new FadeInAt(ghost, 0.4, d(0.6)));
            in.add(new FadeInAt(ghostLab, 0.4, d(0.6)));
            in.add(new ColorChange(trackedBar.rect, Colors.GOLD, d(0.6), ColorChange.Target.STROKE));
            trackedBar.rect.setStrokeWidth(5);
        }
        playAll(in);
        pause(0.6);

        int comps = 0, swaps = 0;
        counters.setText("comparisons: 0     swaps: 0");
        readout.setText("");
        verdict.setText("");
        if (lesson != null) { remove(lesson); lesson = null; }
        boolean told = false;
        boolean firstStep = true;

        for (int ii = 0; ii < n - 1; ii++) {
            double s = sp[Math.min(ii, sp.length - 1)];
            readout.setText(String.format("ii = %d    jj = -", ii));
            List<Animation> top = new ArrayList<>();
            top.add(cb.moveHl(0, d(0.35 * s)));
            if (firstStep) top.add(new FadeIn(cb.hl, d(0.4)));
            playAll(top);
            if (lesson != null) { play(new FadeOut(lesson, d(0.4))); remove(lesson); lesson = null; }
            pause(0.3 * s);

            for (int jj = 0; jj < n - ii - 1; jj++) {
                Bar a = at[jj], b = at[jj + 1];
                readout.setText(String.format("ii = %d    jj = %d", ii, jj));
                double maxH = Math.max(a.h, b.h);
                double ringTop = baseY - maxH - 46, ringBot = baseY + 14;
                ring.setSize(pitch + bw + 24, ringBot - ringTop);
                double ringX = (slotX.applyAsDouble(jj) + slotX.applyAsDouble(jj + 1)) / 2;
                List<Animation> look = new ArrayList<>();
                look.add(cb.moveHl(1, d(0.3 * s)));
                pj.go(look, slotX.applyAsDouble(jj), d(0.35 * s));
                pj1.go(look, slotX.applyAsDouble(jj + 1), d(0.35 * s));
                look.add(new MoveTo(ring, ringX, (ringTop + ringBot) / 2, d(0.35 * s)).setEasing(Easing.EASE_IN_OUT));
                if (firstStep) {
                    look.add(new FadeIn(ring, d(0.4)));
                    look.add(new FadeIn(pj.arrow, d(0.4)));
                    look.add(new FadeIn(pj.lab, d(0.4)));
                    look.add(new FadeIn(pj1.arrow, d(0.4)));
                    look.add(new FadeIn(pj1.lab, d(0.4)));
                    firstStep = false;
                }
                playAll(look);

                play(cb.moveHl(2, d(0.25 * s)));
                comps++;
                boolean doSwap = a.value > b.value;
                counters.setText("comparisons: " + comps + "     swaps: " + swaps);
                verdict.setText(a.value + " > " + b.value + "   →   " + (doSwap ? "swap" : "no swap"));
                verdict.setFillColor(doSwap ? Colors.ORANGE : Colors.LIGHT_GRAY);
                pause(0.45 * s);

                if (doSwap) {
                    double xa = slotX.applyAsDouble(jj), xb = slotX.applyAsDouble(jj + 1);
                    double dur = d(0.75 * s);
                    List<Animation> sw = new ArrayList<>();
                    sw.add(new ArcMove(a.rect, xb, a.rect.getPosition().y(), 120, dur));
                    sw.add(new ArcMove(a.lbl, xb, a.lbl.getPosition().y(), 120, dur));
                    sw.add(new ArcMove(b.rect, xa, b.rect.getPosition().y(), 0, dur));
                    sw.add(new ArcMove(b.lbl, xa, b.lbl.getPosition().y(), 0, dur));
                    playAll(sw);
                    at[jj] = b;
                    at[jj + 1] = a;
                    swaps++;
                    counters.setText("comparisons: " + comps + "     swaps: " + swaps);

                    if (tracked >= 0 && !told && b.value == tracked) {
                        // the tracked element just stepped away from where it belongs
                        told = true;
                        lesson = stroke(tracked + " just moved away from its final place!", 0, -20, 30, Colors.GOLD, false);
                        play(new Write(lesson, d(1.8)));
                        pause(1.4);
                        play(new FadeOut(lesson, d(0.4)));
                        remove(lesson);
                        lesson = null;
                    }
                }
                pause(0.12 * s);
            }

            // the largest number of what's left has reached its final place
            Bar fin = at[n - 1 - ii];
            List<Animation> done = new ArrayList<>();
            done.add(new ColorChange(fin.rect, FINAL_FILL, d(0.5)));
            done.add(new ColorChange(fin.rect, FINAL_STROKE, d(0.5), ColorChange.Target.STROKE));
            if (fin == trackedBar && ghost != null) {
                done.add(new FadeOut(ghost, d(0.4)));
                done.add(new FadeOut(ghostLab, d(0.4)));
            }
            playAll(done);
            fin.rect.setStrokeWidth(2.5);
            if (teach && ii < 2) {
                lesson = stroke("After iteration " + (ii + 1) + ": the " + (ii + 1) + " largest number"
                        + (ii == 0 ? " is" : "s are") + " at their final places.", 0, -20, 30, Colors.GREEN, false);
                play(new Write(lesson, d(2.2)));
                pause(1.0);
            }
        }
        // one number is left: it must be in place too
        Bar last = at[0];
        List<Animation> lastDone = new ArrayList<>();
        lastDone.add(new ColorChange(last.rect, FINAL_FILL, d(0.5)));
        lastDone.add(new ColorChange(last.rect, FINAL_STROKE, d(0.5), ColorChange.Target.STROKE));
        lastDone.add(new FadeOut(ring, d(0.4)));
        lastDone.add(new FadeOut(pj.arrow, d(0.4)));
        lastDone.add(new FadeOut(pj.lab, d(0.4)));
        lastDone.add(new FadeOut(pj1.arrow, d(0.4)));
        lastDone.add(new FadeOut(pj1.lab, d(0.4)));
        if (lesson != null) lastDone.add(new FadeOut(lesson, d(0.4)));
        if (last == trackedBar && ghost != null && ghost.getOpacity() > 0) {
            lastDone.add(new FadeOut(ghost, d(0.4)));
            lastDone.add(new FadeOut(ghostLab, d(0.4)));
        }
        playAll(lastDone);
        if (lesson != null) { remove(lesson); lesson = null; }
        verdict.setText("");
        readout.setText("");

        String closing = teach ? "N - 1 = " + (n - 1) + " passes, and every number is in its final place."
                : (swaps == 0 ? "Already sorted: no swaps — yet every pair was still compared."
                              : "Every pass swapped everything it compared: " + comps + " comparisons, " + swaps + " swaps.");
        lesson = stroke(closing, 0, -20, 30, Colors.ORANGE, false);
        play(new Write(lesson, d(2.6)));
        pause(1.8);
        play(new FadeOut(lesson, d(0.5)));
        remove(lesson);
        lesson = null;

        fadeOutAll(d(0.8), mine);
        pause(0.3);
        return new int[]{comps, swaps};
    }
}
