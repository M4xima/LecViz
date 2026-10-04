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
 * Standalone clip for slides 20-21 of the arrays deck: Shell sort.
 *
 *   Slide 20  the text comes one line at a time in the pen-stroke style, followed by the
 *             slide's table (input, then gap=5, gap=3, gap=1) dropping in row by row; all of
 *             it swipes off to the right. A demo then earns the table: the 13 numbers become
 *             bars, the elements a gap apart share a color, each such list is insertion-sorted
 *             with shifts of a whole gap, and the finished rows build the table again under the
 *             bars — with the shift count compared against plain insertion sort.
 *   Slide 21  the listing types in with its callouts and strike-throughs, the shift line is
 *             corrected to read from jj - gap, and the code runs on eight bars with the
 *             highlight band following the executing line.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSShellSortScene extends PDSSortClipBase {

    private static final int[] INPUT = {81, 94, 11, 96, 12, 35, 17, 95, 28, 58, 41, 75, 15};
    private static final int[][] TABLE = {
        {81, 94, 11, 96, 12, 35, 17, 95, 28, 58, 41, 75, 15},
        {35, 17, 11, 28, 12, 41, 75, 15, 96, 58, 81, 94, 95},
        {28, 12, 11, 35, 15, 41, 58, 17, 94, 75, 81, 96, 95},
        {11, 12, 15, 17, 28, 35, 41, 58, 75, 81, 94, 95, 96},
    };
    private static final String[] ROW_LABEL = {"Input", "gap=5", "gap=3", "gap=1"};

    private StrokeTextMob head;
    private CodeBox cb;
    private TextMob readout, verdict, counters;

    @Override
    public void construct() {
        slide20();
        concept();
        slide21();
    }

    // ── slide 20: the text and the table ─────────────────────────────

    private void slide20() {
        head = writeHeading("Shell Sort");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "The number of shiftings is too high in insertion sort."));
        s.add(ln(3, "This leads to high inefficiency."));
        s.add(ln(0, "Can we allow some perturbations initially and fix"));
        s.add(ln(3, "them later?"));
        s.add(ln(0, "Approach: Instead of comparing adjacent elements,").kw("Approach", Colors.RED));
        s.add(ln(3, "compare those that are some distance apart."));
        s.add(ln(1, "And then reduce the distance."));
        s.add(ln(1, "This sequence of distances is called increment sequence.").kw("increment sequence", Colors.RED));
        List<List<MObject>> groups = writeSlide(s, -375);
        pause(0.4);

        // the slide's table, one row after another
        List<MObject> table = new ArrayList<>();
        List<Animation> drop = new ArrayList<>();
        double lw = 130, cw = 96, gap = 6;
        double left = -(lw + gap + 13 * cw + 12 * gap) / 2;
        double[] ys = {190, 258, 326, 394};
        for (int r = 0; r < 4; r++) {
            double start = d(0.8) * r;
            double alpha = r == 0 ? 0.42 : (r % 2 == 1 ? 0.2 : 0.13);
            RectMob lb = rowBox(left + lw / 2, ys[r], lw, alpha);
            TextMob lt = label(ROW_LABEL[r], left + 20, ys[r], 28, Colors.WHITE, true, true);
            drop.add(new DropIn(lb, 70, start, d(0.9)));
            drop.add(new DropIn(lt, 70, start, d(0.9)));
            table.add(lb);
            table.add(lt);
            for (int c = 0; c < 13; c++) {
                double x = left + lw + gap + c * (cw + gap) + cw / 2;
                RectMob b = rowBox(x, ys[r], cw, alpha);
                TextMob t = label(String.valueOf(TABLE[r][c]), x, ys[r], 28, Colors.WHITE, false, r == 0);
                drop.add(new DropIn(b, 70, start, d(0.9)));
                drop.add(new DropIn(t, 70, start, d(0.9)));
                table.add(b);
                table.add(t);
            }
        }
        playAll(drop);
        pause(2.2);

        List<MObject> headGroup = new ArrayList<>();
        headGroup.add(head);
        groups.add(0, headGroup);
        groups.add(table);
        swipeAway(groups);
        pause(0.5);
    }

    private RectMob rowBox(double x, double y, double w, double alpha) {
        RectMob b = new RectMob(w, 60).setCornerRadius(8);
        b.setFillColor(Colors.withAlpha(Colors.BLUE, alpha));
        b.setStrokeColor(Colors.withAlpha(Colors.BLUE, Math.min(1.0, alpha + 0.35)));
        b.setPosition(x, y);
        b.setOpacity(0);
        add(b);
        return b;
    }

    // ── the demo that earns the table ────────────────────────────────

    private static int insertionShifts(int[] data) {
        int[] a = data.clone();
        int shifts = 0;
        for (int ii = 1; ii < a.length; ii++) {
            int key = a[ii], jj = ii - 1;
            while (jj >= 0 && key < a[jj]) { a[jj + 1] = a[jj]; jj--; shifts++; }
            a[jj + 1] = key;
        }
        return shifts;
    }

    private void concept() {
        head = dropHeading("Shell Sort");
        pause(0.3);
        int n = INPUT.length;
        Bars bars = new Bars(INPUT, 0, 120, 100, 72, 2.6, 24, true);
        List<MObject> mine = new ArrayList<>(bars.parts());
        List<Animation> in = new ArrayList<>();
        bars.fadeIn(in, 0.08, d(0.6));
        playAll(in);
        pause(0.4);

        TextMob counter = label("shifts: 0", 640, -300, 34, Colors.GOLD, false, true);
        TextMob gapTag = label("", 0, -335, 40, Colors.WHITE, false, true);
        play(new FadeIn(counter, d(0.4)), new FadeIn(gapTag, d(0.4)));
        mine.add(counter);
        mine.add(gapTag);

        int[] gaps = {5, 3, 1};
        double[] speeds = {0.8, 0.6, 0.35};
        Color[] groupColor = {Colors.GOLD, Colors.BLUE, Colors.GREEN, Colors.PINK, Colors.TEAL};
        int total = 0;
        StrokeTextMob cap = null;
        for (int gi = 0; gi < gaps.length; gi++) {
            int g = gaps[gi];
            double s = speeds[gi];
            if (cap != null) { play(new FadeOut(cap, d(0.4))); remove(cap); }
            cap = stroke(gi == 0 ? "Compare elements that are 5 apart, not adjacent ones."
                            : gi == 1 ? "Reduce the distance: now 3 apart."
                            : "Finally, distance 1 — a plain insertion sort on an almost sorted array.",
                    0, -395, 28, Colors.LIGHT_GRAY, false);
            gapTag.setText("gap = " + g);
            play(new Write(cap, d(2.4)));

            // elements a gap apart share a color: they form interleaved lists
            List<Animation> col = new ArrayList<>();
            for (int i = 0; i < n; i++) bars.paint(col, bars.at[i], g == 1 ? Colors.ORANGE : groupColor[i % g], d(0.6));
            playAll(col);
            pause(0.7);

            for (int ii = g; ii < n; ii++) {
                SBar key = bars.at[ii];
                List<Animation> lift = new ArrayList<>();
                bars.lift(lift, key, 90, d(0.35 * s));
                playAll(lift);
                int jj = ii;
                while (jj - g >= 0 && key.value < bars.at[jj - g].value) {
                    SBar mv = bars.at[jj - g];
                    List<Animation> sh = new ArrayList<>();
                    bars.place(sh, mv, jj, 0, d(0.5 * s));
                    playAll(sh);
                    bars.at[jj] = mv;
                    jj -= g;
                    total++;
                    counter.setText("shifts: " + total);
                }
                List<Animation> land = new ArrayList<>();
                bars.place(land, key, jj, 0, d(0.5 * s));
                playAll(land);
                bars.at[jj] = key;
                pause(0.1 * s);
            }

            // the pass is done: its row joins the table under the bars
            double rowY = 240 + 65 * gi;
            List<Animation> row = new ArrayList<>();
            TextMob rl = label("gap=" + g, -735, rowY, 26, Colors.LIGHT_GRAY, false, true);
            row.add(new FadeIn(rl, d(0.4)));
            mine.add(rl);
            for (int i = 0; i < n; i++) {
                TextMob t = label(String.valueOf(bars.at[i].value), bars.slotX(i), rowY, 28, Colors.WHITE, false, false);
                row.add(new FadeInAt(t, 0.05 * i, d(0.4)));
                mine.add(t);
            }
            List<Animation> back = new ArrayList<>();
            for (int i = 0; i < n; i++) bars.paintOriginal(back, bars.at[i], d(0.5));
            playAll(row);
            playAll(back);
            pause(0.8);
        }
        play(new FadeOut(cap, d(0.4)));
        remove(cap);

        List<Animation> fin = new ArrayList<>();
        for (int i = 0; i < n; i++) bars.paintFinal(fin, bars.at[i], d(0.6));
        playAll(fin);
        int plain = insertionShifts(INPUT);
        StrokeTextMob cmp = stroke("The rows match the slide: " + total + " shifts in all, against " + plain
                + " for plain insertion sort on the same numbers.", 0, -395, 28, Colors.ORANGE, false);
        play(new Write(cmp, d(4.0)));
        mine.add(cmp);
        pause(2.4);
        fadeOutAll(d(0.9), mine);
        pause(0.3);
    }

    // ── slide 21: the code ───────────────────────────────────────────

    private void slide21() {
        CodeBox box = new CodeBox(new String[]{
            "for (gap = N/2; gap; gap /= 2)",
            "   for (ii = ... ; ii < N; ++ii) {",
            "      int key = arr[ii];",
            "      int jj = ii - 1;",
            "",
            "      while (jj - gap >= 0 && key < arr[jj - gap]) {",
            "         arr[jj + 1] = arr[jj];",
            "         jj -= gap;",
            "      }",
            "      arr[jj + 1] = key;",
            "   }"}, -860, -385, 26, 40);
        box.typeIn(5.0);
        List<MObject> stage = new ArrayList<>();
        List<Animation> co = new ArrayList<>();
        callout(co, stage, "ith element", null, 270, box.lineY(1), 280, 60, box.lineEndX(1) + 12, box.lineY(1));
        playAll(co);
        pause(0.6);
        co = new ArrayList<>();
        callout(co, stage, "Shift elements", null, 270, box.lineY(6), 280, 60, box.lineEndX(6) + 12, box.lineY(6));
        playAll(co);
        pause(0.6);
        co = new ArrayList<>();
        callout(co, stage, "At its place", null, 270, box.lineY(9), 280, 60, box.lineEndX(9) + 12, box.lineY(9));
        playAll(co);
        pause(1.0);

        // the edits from the insertion-sort listing: two tokens go, the shift reads from jj - gap
        LineMob strike1 = box.strike(3, "- 1");
        LineMob strike2 = box.strike(9, "+ 1");
        play(new DrawLine(strike1, d(0.6)), new DrawLine(strike2, d(0.6)));
        stage.add(strike1);
        stage.add(strike2);
        pause(0.8);
        StrokeTextMob note = stroke("jj starts at ii, and the shift copies from jj - gap: the previous element of the same gap-list.",
                -400, 95, 24, Colors.LIGHT_GRAY, false);
        List<Animation> fix = new ArrayList<>();
        List<MObject> fresh = box.rewrite(fix, 6, "         arr[jj] = arr[jj - gap];", d(0.8));
        fix.add(new Write(note, d(3.4)));
        playAll(fix);
        stage.addAll(fresh);
        stage.add(note);
        pause(1.8);
        // the listing stays; the callouts and the note leave
        List<MObject> leaving = new ArrayList<>();
        for (MObject m : stage) if (!box.parts().contains(m) && m != strike1 && m != strike2 && !fresh.contains(m)) leaving.add(m);
        fadeOutAll(d(0.6), leaving);
        cb = box;

        readout = label("", 450, -230, 28, Colors.WHITE, false, true);
        readout.setFontFamily("Menlo");
        verdict = label("", 450, -180, 32, Colors.WHITE, false, true);
        counters = label("", 450, -130, 28, Colors.GOLD, false, true);
        play(new FadeIn(readout, d(0.4)), new FadeIn(verdict, d(0.4)), new FadeIn(counters, d(0.4)));
        runShell(new int[]{9, 3, 7, 1, 8, 2, 6, 4});

        List<MObject> demo = new ArrayList<>(cb.parts());
        demo.add(readout);
        demo.add(verdict);
        demo.add(counters);
        demo.add(strike1);
        demo.add(strike2);
        fadeOutAll(d(0.8), demo);
        pause(0.3);

        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Best case: Sorted:").tail("while loop is O(1)").kw("Best", Colors.GREEN));
        s.add(ln(0, "Worst case:").tail("O(n²)").kw("Worst", Colors.RED));
        List<List<MObject>> groups = writeSlide(s, -300);
        pause(2.6);
        List<MObject> all = new ArrayList<>();
        all.add(head);
        for (List<MObject> g : groups) all.addAll(g);
        fadeOutAll(1.5, all);
        pause(0.5);
    }

    /** Runs the listing on a small array: gaps N/2, N/4, ..., 1. */
    private void runShell(int[] data) {
        int n = data.length;
        Bars bars = new Bars(data, 450, 330, 90, 64, 26, 28, true);
        List<MObject> mine = new ArrayList<>();
        mine.addAll(bars.parts());
        List<Animation> in = new ArrayList<>();
        bars.fadeIn(in, 0.08, d(0.6));
        Ptr pj = pointer("jj", bars.slotX(0), 330 + 52, false, Colors.GOLD);
        mine.addAll(pj.parts());
        playAll(in);
        pause(0.5);

        int shifts = 0, comps = 0, pass = 0;
        boolean first = true;
        for (int g = n / 2; g > 0; g /= 2, pass++) {
            readout.setText("gap = " + g);
            List<Animation> top = new ArrayList<>();
            top.add(cb.moveHl(0, d(0.35)));
            if (first) top.add(new FadeIn(cb.hl, d(0.4)));
            playAll(top);
            pause(0.4);
            for (int ii = g, k = 0; ii < n; ii++, k++) {
                double s = pass == 0 ? (k < 2 ? 1.0 : 0.6) : (pass == 1 ? 0.5 : 0.35);
                readout.setText(String.format("gap = %d  ii = %d", g, ii));
                play(cb.moveHl(1, d(0.3 * s)));
                SBar key = bars.at[ii];
                List<Animation> lift = new ArrayList<>();
                lift.add(cb.moveHl(2, d(0.3 * s)));
                bars.lift(lift, key, 100, d(0.5 * s));
                bars.paint(lift, key, Colors.ORANGE, d(0.5 * s));
                playAll(lift);
                int jj = ii;
                verdict.setText("key = " + key.value);
                verdict.setFillColor(Colors.ORANGE);
                List<Animation> at = new ArrayList<>();
                at.add(cb.moveHl(3, d(0.3 * s)));
                pj.go(at, bars.slotX(jj), d(0.4 * s));
                if (first) {
                    at.add(new FadeIn(pj.arrow, d(0.4)));
                    at.add(new FadeIn(pj.lab, d(0.4)));
                    first = false;
                }
                playAll(at);
                readout.setText(String.format("gap = %d  ii = %d  jj = %d", g, ii, jj));
                pause(0.2 * s);
                while (true) {
                    play(cb.moveHl(5, d(0.3 * s)));
                    boolean shift = jj - g >= 0 && key.value < bars.at[jj - g].value;
                    if (jj - g >= 0) comps++;
                    verdict.setText(jj - g < 0 ? "jj - gap < 0   →   stop"
                            : key.value + " < " + bars.at[jj - g].value + " ?   " + (shift ? "yes → shift" : "no → stop"));
                    verdict.setFillColor(shift ? Colors.ORANGE : Colors.LIGHT_GRAY);
                    counters.setText("comparisons: " + comps + "     shifts: " + shifts);
                    pause(0.4 * s);
                    if (!shift) break;
                    play(cb.moveHl(6, d(0.25 * s)));
                    SBar mv = bars.at[jj - g];
                    List<Animation> sh = new ArrayList<>();
                    bars.place(sh, mv, jj, 0, d(0.55 * s));
                    playAll(sh);
                    bars.at[jj] = mv;
                    shifts++;
                    counters.setText("comparisons: " + comps + "     shifts: " + shifts);
                    play(cb.moveHl(7, d(0.25 * s)));
                    jj -= g;
                    readout.setText(String.format("gap = %d  ii = %d  jj = %d", g, ii, jj));
                    List<Animation> back = new ArrayList<>();
                    pj.go(back, bars.slotX(jj), d(0.35 * s));
                    playAll(back);
                }
                play(cb.moveHl(9, d(0.3 * s)));
                List<Animation> land = new ArrayList<>();
                bars.place(land, key, jj, 0, d(0.55 * s));
                bars.paintOriginal(land, key, d(0.55 * s));
                playAll(land);
                bars.at[jj] = key;
                pause(0.15 * s);
            }
        }
        List<Animation> end = new ArrayList<>();
        for (int i = 0; i < n; i++) bars.paintFinal(end, bars.at[i], d(0.6));
        end.add(new FadeOut(pj.arrow, d(0.4)));
        end.add(new FadeOut(pj.lab, d(0.4)));
        playAll(end);
        verdict.setText("sorted");
        verdict.setFillColor(Colors.GREEN);
        readout.setText("");
        pause(2.0);
        fadeOutAll(d(0.8), mine);
    }
}
