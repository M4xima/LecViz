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
 * Standalone clip for slides 18-19 of the arrays deck: insertion sort.
 *
 *   Slide 18  the text comes one line at a time in the pen-stroke style, then swipes off to
 *             the right; a short binary-search demo answers the slide's "O(n log n)? — but
 *             are we doing more work?" by finding the place in two probes and then still
 *             having to shift every card to make room
 *   Slide 19  the listing types in with its three callouts, then runs on playing cards: the
 *             key card lifts out of the hand, bigger cards shift right one by one, the key
 *             drops into its place, and the sorted prefix (the invariant) grows. A sorted
 *             run shows the best case, a reverse-sorted run the 0 + 1 + 2 + ... + n-1 worst
 *             case, and the slide's closing points sit next to the counts from the runs.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSInsertionSortScene extends PDSSortClipBase {

    private static final double CW = 100, CH = 136, PITCH = 124;
    private static final Color NEUTRAL_FILL = Colors.withAlpha(Colors.WHITE, 0.10);
    private static final Color NEUTRAL_STROKE = Colors.withAlpha(Colors.WHITE, 0.75);
    private static final Color SORTED_FILL = Colors.withAlpha(Colors.GREEN, 0.24);
    private static final Color KEY_FILL = Colors.withAlpha(Colors.ORANGE, 0.42);

    private StrokeTextMob head;
    private CodeBox cb;
    private TextMob readout, counters, verdict;
    private StrokeTextMob lesson;
    private double viewX = 430, cardY = 0;

    @Override
    public void construct() {
        slide18();
        binaryTeaser();
        slide19();
    }

    // ── slide 18: the text ───────────────────────────────────────────

    private void slide18() {
        head = writeHeading("Insertion Sort");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Consider ith element and insert it at its place w.r.t."));
        s.add(ln(3, "the first i elements."));
        s.add(ln(1, "Resembles insertion of a playing card."));
        s.add(ln(0, "Invariant: Keep the first i elements sorted.").kw("Invariant:", Colors.BLUE));
        s.add(ln(0, "Note: Insertion is in a sorted array.").kw("Note:", Colors.GOLD));
        s.add(ln(0, "Complexity: O(n log n)?"));
        s.add(ln(1, "Yes, binary search is O(log n)."));
        s.add(ln(2, "But are we doing more work?"));
        s.add(ln(1, "Best case, Worst case?"));
        s.add(ln(0, "Classwork: Write the code.").kw("Classwork", Colors.ORANGE));
        List<List<MObject>> groups = writeSlide(s, -365);
        List<MObject> headGroup = new ArrayList<>();
        headGroup.add(head);
        groups.add(0, headGroup);
        pause(0.9);
        swipeAway(groups);
        pause(0.5);
    }

    // ── cards ────────────────────────────────────────────────────────

    private final class Card {
        final RectMob rect;
        final TextMob big, corner;
        final int value;

        Card(int v, double x, double y) {
            value = v;
            rect = new RectMob(CW, CH).setCornerRadius(12);
            rect.setFillColor(NEUTRAL_FILL);
            rect.setStrokeColor(NEUTRAL_STROKE);
            rect.setStrokeWidth(2.5);
            rect.setPosition(x, y);
            rect.setOpacity(0);
            add(rect);
            big = label(String.valueOf(v), x, y + 6, 50, Colors.WHITE, false, true);
            corner = label(String.valueOf(v), x - CW / 2 + 22, y - CH / 2 + 24, 22, Colors.LIGHT_GRAY, false, true);
        }

        List<MObject> parts() {
            List<MObject> l = new ArrayList<>();
            l.add(rect);
            l.add(big);
            l.add(corner);
            return l;
        }

        void move(List<Animation> into, double x, double y, double bulge, double dur) {
            into.add(new ArcMove(rect, x, y, bulge, dur));
            into.add(new ArcMove(big, x, y + 6, bulge, dur));
            into.add(new ArcMove(corner, x - CW / 2 + 22, y - CH / 2 + 24, bulge, dur));
        }

        void paint(List<Animation> into, Color fill, Color stroke, double dur) {
            into.add(new ColorChange(rect, fill, dur));
            into.add(new ColorChange(rect, stroke, dur, ColorChange.Target.STROKE));
        }

        void fadeIn(List<Animation> into, double delay, double dur) {
            for (MObject m : parts()) into.add(new FadeInAt(m, delay, dur));
        }
    }

    private double slotX(int i, int n) { return viewX + (i - (n - 1) / 2.0) * PITCH; }

    /** Resizes a rectangle horizontally while moving its center. */
    private static final class ResizeX extends Animation {
        private final RectMob r;
        private final double tw, tcx;
        private double sw, scx;

        ResizeX(RectMob r, double tw, double tcx, double dur) {
            super(r, dur, Easing.EASE_IN_OUT);
            this.r = r;
            this.tw = tw;
            this.tcx = tcx;
        }

        @Override
        public void begin() {
            sw = r.getWidth();
            scx = r.getPosition().x();
        }

        @Override
        public void interpolate(double t) {
            r.setSize(sw + (tw - sw) * t, r.getHeight());
            r.setPosition(scx + (tcx - scx) * t, r.getPosition().y());
        }
    }

    // ── slide 18's question: binary search finds the place, shifting is still the cost ──

    private void binaryTeaser() {
        head = dropHeading("Insertion Sort");
        pause(0.4);
        viewX = 0;
        cardY = 40;
        int[] vals = {2, 4, 5, 6, 1};
        int n = vals.length;
        List<MObject> mine = new ArrayList<>();
        Card[] cards = new Card[n];
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            cards[i] = new Card(vals[i], slotX(i, n), cardY);
            cards[i].fadeIn(in, 0.12 * i, d(0.6));
            mine.addAll(cards[i].parts());
        }
        StrokeTextMob intro = stroke("The first i elements are already sorted — so binary search can find the place.",
                0, -330, 30, Colors.LIGHT_GRAY, false);
        mine.add(intro);
        play(new Write(intro, d(3.6)));
        playAll(in);
        List<Animation> tint = new ArrayList<>();
        for (int i = 0; i < 4; i++) cards[i].paint(tint, SORTED_FILL, Colors.GREEN, d(0.6));
        playAll(tint);
        pause(0.5);

        // the new element steps out of the row
        Card key = cards[4];
        List<Animation> lift = new ArrayList<>();
        key.move(lift, slotX(4, n), cardY - 150, 0, d(0.7));
        key.paint(lift, KEY_FILL, Colors.ORANGE, d(0.7));
        playAll(lift);

        TextMob ver = label("", 0, -190, 32, Colors.WHITE, false, true);
        ver.setFontFamily("Menlo");
        mine.add(ver);
        RectMob ring = new RectMob(CW + 24, CH + 24).setCornerRadius(16);
        ring.setFillColor(Colors.withAlpha(Colors.ORANGE, 0.1));
        ring.setStrokeColor(Colors.ORANGE);
        ring.setStrokeWidth(4);
        ring.setPosition(slotX(1, n), cardY);
        ring.setOpacity(0);
        add(ring);
        mine.add(ring);
        TextMob midLab = label("mid", slotX(1, n), cardY + CH / 2 + 38, 28, Colors.ORANGE, false, true);
        mine.add(midLab);
        ver.setText("1 < 4   →   look left");
        play(new FadeIn(ring, d(0.5)), new FadeIn(midLab, d(0.5)), new FadeIn(ver, d(0.5)));
        pause(1.5);
        ver.setText("1 < 2   →   look left");
        play(new MoveTo(ring, slotX(0, n), cardY, d(0.6)).setEasing(Easing.EASE_IN_OUT),
                new MoveTo(midLab, slotX(0, n), cardY + CH / 2 + 38, d(0.6)).setEasing(Easing.EASE_IN_OUT));
        pause(1.5);
        ver.setText("place found: slot 0   (2 probes)");
        ver.setFillColor(Colors.GREEN);
        pause(1.4);
        play(new FadeOut(ring, d(0.4)), new FadeOut(midLab, d(0.4)));

        // ...but the room still has to be made
        TextMob cnt = label("shifts: 0", 0, 200, 34, Colors.GOLD, false, true);
        mine.add(cnt);
        StrokeTextMob more = stroke("...but making room still means shifting every bigger card.", 0, 270, 28, Colors.WHITE, false);
        mine.add(more);
        play(new FadeIn(cnt, d(0.4)), new Write(more, d(2.6)));
        int shifts = 0;
        for (int k = 3; k >= 0; k--) {
            List<Animation> sh = new ArrayList<>();
            cards[k].move(sh, slotX(k + 1, n), cardY, 0, d(0.45));
            playAll(sh);
            shifts++;
            cnt.setText("shifts: " + shifts);
        }
        List<Animation> place = new ArrayList<>();
        key.move(place, slotX(0, n), cardY, 0, d(0.7));
        key.paint(place, SORTED_FILL, Colors.GREEN, d(0.7));
        playAll(place);
        pause(0.8);
        StrokeTextMob concl = stroke("Finding the place is O(log n), but the shifting is still up to O(i): we do more work.",
                0, 335, 28, Colors.ORANGE, false);
        mine.add(concl);
        play(new Write(concl, d(3.6)));
        pause(0.5);
        StrokeTextMob ask = stroke("So what are the best and the worst cases?", 0, 395, 30, Colors.WHITE, false);
        mine.add(ask);
        play(new Write(ask, d(2.2)));
        pause(2.2);
        fadeOutAll(d(0.9), mine);
        pause(0.3);
    }

    // ── slide 19: the code, run on playing cards ─────────────────────

    private void slide19() {
        pause(0.2);
        CodeBox box = new CodeBox(new String[]{
            "for (ii = 1 ; ii < N; ++ii) {",
            "   int key = arr[ii];",
            "   int jj = ii - 1;",
            "",
            "   while (jj >= 0 && key < arr[jj]) {",
            "      arr[jj + 1] = arr[jj];",
            "      --jj;",
            "   }",
            "   arr[jj + 1] = key;",
            "}"}, -860, -385, 26, 40);
        box.typeIn(4.2);
        List<MObject> stage = new ArrayList<>();
        List<Animation> co = new ArrayList<>();
        callout(co, stage, "ith element", null, 150, box.lineY(1), 280, 60, box.lineEndX(1) + 12, box.lineY(1));
        playAll(co);
        pause(0.7);
        co = new ArrayList<>();
        callout(co, stage, "Shift elements", "0 + 1 + 2 + ... n-1", 160, box.lineY(5) + 20, 300, 96, box.lineEndX(5) + 12, box.lineY(5));
        playAll(co);
        pause(0.7);
        co = new ArrayList<>();
        callout(co, stage, "At its place", null, 150, box.lineY(8), 280, 60, box.lineEndX(8) + 12, box.lineY(8));
        playAll(co);
        pause(2.2);
        fadeOutAll(d(0.7), stage);
        cb = box;

        // readouts, counters and the sorted-prefix marker shared by the three runs
        viewX = 430;
        cardY = 0;
        readout = label("", viewX, 322, 30, Colors.WHITE, false, true);
        readout.setFontFamily("Menlo");
        counters = label("", viewX, 374, 30, Colors.GOLD, false, true);
        verdict = label("", viewX, 268, 34, Colors.WHITE, false, true);
        play(new FadeIn(readout, d(0.4)), new FadeIn(counters, d(0.4)), new FadeIn(verdict, d(0.4)));

        int[] mixed = {5, 2, 4, 6, 1, 3};
        int[] a = runInsertion(mixed, new double[]{1.0, 0.7, 0.45}, 0);
        int[] sorted = {1, 2, 3, 4, 5, 6};
        int[] b = runInsertion(sorted, new double[]{0.35}, 1);
        int[] reverse = {6, 5, 4, 3, 2, 1};
        int[] c = runInsertion(reverse, new double[]{0.45, 0.3}, 2);

        // clear the demo, then the slide's closing points next to the counts
        List<MObject> demo = new ArrayList<>(cb.parts());
        demo.add(readout);
        demo.add(counters);
        demo.add(verdict);
        fadeOutAll(d(0.8), demo);
        pause(0.3);

        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Best case: Sorted:").tail("while loop is O(1)").kw("Best", Colors.GREEN));
        s.add(ln(0, "Worst case: Reverse sorted:").tail("O(n²)").kw("Worst", Colors.RED));
        List<List<MObject>> groups = writeSlide(s, -330);
        pause(0.5);

        String[][] rows = {
            {"Sorted (best case)", String.valueOf(b[0]), String.valueOf(b[1])},
            {"Mixed", String.valueOf(a[0]), String.valueOf(a[1])},
            {"Reverse sorted (worst case)", String.valueOf(c[0]), String.valueOf(c[1])},
        };
        Color[][] tint = {
            {null, Colors.TEAL, Colors.BLUE},
            {null, Colors.TEAL, Colors.GOLD},
            {null, Colors.TEAL, Colors.RED},
        };
        List<MObject> table = dropTable(new String[]{"Input", "Comparisons", "Shifts"}, rows,
                new double[]{460, 300, 260}, tint, -150);
        StrokeTextMob formula = stroke("Worst case shifts: 0 + 1 + 2 + ... + (n-1) = " + c[1] + " for n = 6, and O(n²) in general.",
                0, 170, 28, Colors.GOLD, false);
        play(new Write(formula, d(3.4)));
        pause(3.2);

        List<MObject> all = new ArrayList<>(table);
        all.add(formula);
        all.add(head);
        for (List<MObject> g : groups) all.addAll(g);
        fadeOutAll(1.5, all);
        pause(0.5);
    }

    /**
     * Runs the code in {@code cb} on a hand of cards; {@code sp[ii-1]} scales how long insertion
     * ii takes. {@code mode}: 0 = teach the invariant, 1 = sorted input, 2 = reverse-sorted input.
     * Returns {comparisons, shifts}.
     */
    private int[] runInsertion(int[] data, double[] sp, int mode) {
        int n = data.length;
        List<MObject> mine = new ArrayList<>();
        Card[] at = new Card[n];
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            at[i] = new Card(data[i], slotX(i, n), cardY);
            at[i].fadeIn(in, 0.1 * i, d(0.6));
            mine.addAll(at[i].parts());
        }
        double bandY = cardY + CH / 2 + 24;
        RectMob band = new RectMob(PITCH - 14, 12).setCornerRadius(6);
        band.setFillColor(Colors.withAlpha(Colors.GREEN, 0.5));
        band.setStrokeColor(Colors.GREEN);
        band.setPosition(slotX(0, n), bandY);
        band.setOpacity(0);
        add(band);
        TextMob bandLab = label("sorted", slotX(0, n), bandY + 30, 22, Colors.GREEN, false, true);
        mine.add(band);
        mine.add(bandLab);
        in.add(new FadeInAt(band, 0.2, d(0.6)));
        in.add(new FadeInAt(bandLab, 0.2, d(0.6)));
        Ptr pj = pointer("jj", slotX(0, n), bandY + 78, false, Colors.GOLD);
        mine.addAll(pj.parts());
        playAll(in);
        List<Animation> first = new ArrayList<>();
        at[0].paint(first, SORTED_FILL, Colors.GREEN, d(0.5));
        playAll(first);
        pause(0.5);

        int comps = 0, shifts = 0;
        counters.setText("comparisons: 0     shifts: 0");
        verdict.setText("");
        readout.setText("");
        boolean firstStep = true;

        for (int ii = 1; ii < n; ii++) {
            double s = sp[Math.min(ii - 1, sp.length - 1)];
            readout.setText(String.format("ii = %d", ii));
            List<Animation> top = new ArrayList<>();
            top.add(cb.moveHl(0, d(0.35 * s)));
            if (firstStep) top.add(new FadeIn(cb.hl, d(0.4)));
            playAll(top);
            if (mode == 0 && ii == 1) {
                lesson = stroke("Like a playing card: take the next card and insert it into the sorted hand.",
                        viewX, -290, 30, Colors.LIGHT_GRAY, false);
                play(new Write(lesson, d(3.0)));
                pause(0.8);
                play(new FadeOut(lesson, d(0.5)));
                remove(lesson);
                lesson = null;
            }
            pause(0.25 * s);

            // int key = arr[ii]   — the ith element steps out of the hand
            Card key = at[ii];
            readout.setText(String.format("ii = %d   key = %d", ii, key.value));
            List<Animation> lift = new ArrayList<>();
            lift.add(cb.moveHl(1, d(0.3 * s)));
            key.move(lift, slotX(ii, n), cardY - 150, 0, d(0.6 * s));
            key.paint(lift, KEY_FILL, Colors.ORANGE, d(0.6 * s));
            playAll(lift);
            verdict.setText("ith element:  key = " + key.value);
            verdict.setFillColor(Colors.ORANGE);
            pause(0.35 * s);

            // int jj = ii - 1
            int jj = ii - 1;
            readout.setText(String.format("ii = %d   key = %d   jj = %d", ii, key.value, jj));
            List<Animation> pin = new ArrayList<>();
            pin.add(cb.moveHl(2, d(0.3 * s)));
            pj.go(pin, slotX(jj, n), d(0.4 * s));
            if (firstStep) {
                pin.add(new FadeIn(pj.arrow, d(0.4)));
                pin.add(new FadeIn(pj.lab, d(0.4)));
                firstStep = false;
            }
            playAll(pin);
            pause(0.2 * s);

            while (true) {
                play(cb.moveHl(4, d(0.3 * s)));
                boolean shift;
                if (jj < 0) {
                    shift = false;
                    verdict.setText("jj < 0   →   stop");
                    verdict.setFillColor(Colors.LIGHT_GRAY);
                } else {
                    comps++;
                    shift = key.value < at[jj].value;
                    verdict.setText(key.value + " < " + at[jj].value + " ?   " + (shift ? "yes → shift" : "no → stop"));
                    verdict.setFillColor(shift ? Colors.ORANGE : Colors.LIGHT_GRAY);
                }
                counters.setText("comparisons: " + comps + "     shifts: " + shifts);
                pause(0.45 * s);
                if (!shift) break;

                // arr[jj + 1] = arr[jj]
                play(cb.moveHl(5, d(0.25 * s)));
                Card c = at[jj];
                List<Animation> sh = new ArrayList<>();
                c.move(sh, slotX(jj + 1, n), cardY, 0, d(0.55 * s));
                playAll(sh);
                at[jj + 1] = c;
                shifts++;
                counters.setText("comparisons: " + comps + "     shifts: " + shifts);

                // --jj
                play(cb.moveHl(6, d(0.25 * s)));
                jj--;
                readout.setText(String.format("ii = %d   key = %d   jj = %d", ii, key.value, jj));
                List<Animation> back = new ArrayList<>();
                pj.go(back, slotX(jj, n), d(0.35 * s));
                playAll(back);
                pause(0.12 * s);
            }

            // arr[jj + 1] = key   — it lands at its place; the sorted prefix grows
            play(cb.moveHl(8, d(0.3 * s)));
            verdict.setText("at its place:  slot " + (jj + 1));
            verdict.setFillColor(Colors.GREEN);
            List<Animation> land = new ArrayList<>();
            key.move(land, slotX(jj + 1, n), cardY, 0, d(0.65 * s));
            key.paint(land, SORTED_FILL, Colors.GREEN, d(0.65 * s));
            land.add(new ResizeX(band, (ii + 1) * PITCH - 14, slotX(0, n) + ii * PITCH / 2.0, d(0.6 * s)));
            land.add(new MoveTo(bandLab, slotX(0, n) + ii * PITCH / 2.0, bandY + 30, d(0.6 * s)).setEasing(Easing.EASE_IN_OUT));
            playAll(land);
            at[jj + 1] = key;
            pause(0.3 * s);
        }

        List<Animation> end = new ArrayList<>();
        end.add(new FadeOut(pj.arrow, d(0.4)));
        end.add(new FadeOut(pj.lab, d(0.4)));
        playAll(end);
        verdict.setText("");
        readout.setText("");
        String closing = mode == 0 ? "Invariant: the first i elements stay sorted, so after n - 1 insertions all of them are."
                : mode == 1 ? "Already sorted: the while loop stops at once, O(1) work per element."
                : "Reverse sorted: card ii shifts ii places, so " + shifts + " shifts in all.";
        lesson = stroke(closing, viewX, -290, 28, mode == 0 ? Colors.GREEN : Colors.ORANGE, false);
        play(new Write(lesson, d(3.2)));
        pause(1.8);
        play(new FadeOut(lesson, d(0.5)));
        remove(lesson);
        lesson = null;

        fadeOutAll(d(0.8), mine);
        pause(0.3);
        return new int[]{comps, shifts};
    }
}
