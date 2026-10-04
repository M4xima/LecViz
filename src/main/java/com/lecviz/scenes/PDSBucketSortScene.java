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
import java.util.function.IntUnaryOperator;

/**
 * Standalone clip for slide 27 of the arrays deck: bucket sort.
 *
 *   - the slide text comes one line at a time in the pen-stroke style, then swipes off
 *   - ten exam marks are hashed into five buckets (index = mark / 20) like sheets sorted into
 *     piles, every pile is insertion-sorted, and the piles are read out in increasing order
 *   - the slide's special case: with at least as many buckets as the largest value, every bucket
 *     holds equal values only and nothing needs sorting
 *   - and "unsuitable for arbitrary types": there is no index to compute for words or pairs
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSBucketSortScene extends PDSSortClipBase {

    private StrokeTextMob head;
    private TextMob verdict;

    private static final double CHIP_W = 88, CHIP_H = 46;
    private static final double BUCKET_TOP = -150, BUCKET_BOTTOM = 190;
    private static final double IN_Y = -290, OUT_Y = 320;

    @Override
    public void construct() {
        head = writeHeading("Bucket Sort");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Hash / index each element into a bucket."));
        s.add(ln(0, "Sort each bucket."));
        s.add(ln(1, "use other sorting algorithms such as insertion sort."));
        s.add(ln(0, "Output buckets in increasing order."));
        s.add(ln(0, "Special case when number of buckets >="));
        s.add(ln(3, "maximum element value."));
        s.add(ln(0, "Unsuitable for arbitrary types."));
        List<List<MObject>> text = writeSlide(s, -365);
        pause(1.2);
        swipeAway(text);
        pause(0.4);

        verdict = label("", 0, -390, 34, Colors.WHITE, false, true);
        play(new FadeIn(verdict, d(0.4)));

        // exam marks into five piles of width 20, then a pile for every value
        bucketRun(new int[]{78, 17, 39, 26, 72, 94, 21, 12, 23, 68}, 5,
                v -> v / 20,
                new String[]{"0 – 19", "20 – 39", "40 – 59", "60 – 79", "80 – 99"},
                "index = mark / 20", true);
        bucketRun(new int[]{3, 1, 4, 1, 5, 2, 5}, 6,
                v -> v,
                new String[]{"0", "1", "2", "3", "4", "5"},
                "index = value   (buckets ≥ max value)", false);
        arbitraryTypes();

        fadeOutAll(1.5, head, verdict);
        pause(0.5);
    }

    // ── one bucket sort run ──────────────────────────────────────────

    private RectMob chipBox(double x, double y, Color c) {
        RectMob r = new RectMob(CHIP_W, CHIP_H).setCornerRadius(6);
        r.setFillColor(Colors.withAlpha(Colors.WHITE, 0.14));
        r.setStrokeColor(Colors.withAlpha(c, 0.9));
        r.setStrokeWidth(2.5);
        r.setPosition(x, y);
        r.setOpacity(0);
        add(r);
        return r;
    }

    private void bucketRun(int[] vals, int nb, IntUnaryOperator index, String[] labels, String formula, boolean sortEach) {
        int n = vals.length;
        double bw = Math.min(250, 1500.0 / nb), pitchB = bw + 14;
        List<MObject> mine = new ArrayList<>();
        List<Animation> in = new ArrayList<>();

        TextMob form = label(formula, 0, -340, 30, Colors.GOLD, false, true);
        form.setFontFamily("Menlo");
        mine.add(form);
        in.add(new FadeIn(form, d(0.6)));

        // the buckets
        double[] bx = new double[nb];
        for (int b = 0; b < nb; b++) {
            bx[b] = (b - (nb - 1) / 2.0) * pitchB;
            RectMob box = new RectMob(bw, BUCKET_BOTTOM - BUCKET_TOP).setCornerRadius(12);
            box.setFillColor(GHOST_FILL);
            box.setStrokeColor(Colors.withAlpha(Colors.TEAL, 0.7));
            box.setStrokeWidth(2.5);
            box.setPosition(bx[b], (BUCKET_TOP + BUCKET_BOTTOM) / 2);
            box.setOpacity(0);
            add(box);
            TextMob lab = label(labels[b], bx[b], BUCKET_TOP - 26, 26, Colors.TEAL, false, true);
            in.add(new FadeInAt(box, 0.08 * b, d(0.5)));
            in.add(new FadeInAt(lab, 0.08 * b, d(0.5)));
            mine.add(box);
            mine.add(lab);
        }

        // the marks, as sheets in a row
        RectMob[] chip = new RectMob[n];
        TextMob[] num = new TextMob[n];
        double ix0 = -(n - 1) * 100 / 2.0;
        for (int i = 0; i < n; i++) {
            chip[i] = chipBox(ix0 + 100 * i, IN_Y, Colors.LIGHT_GRAY);
            num[i] = label(String.valueOf(vals[i]), ix0 + 100 * i, IN_Y, 28, Colors.WHITE, false, true);
            in.add(new FadeInAt(chip[i], 0.07 * i, d(0.5)));
            in.add(new FadeInAt(num[i], 0.07 * i, d(0.5)));
            mine.add(chip[i]);
            mine.add(num[i]);
        }
        playAll(in);
        pause(0.5);

        // 1) index every element into its bucket
        List<List<Integer>> inBucket = new ArrayList<>();
        for (int b = 0; b < nb; b++) inBucket.add(new ArrayList<>());
        int[] home = new int[n];
        for (int i = 0; i < n; i++) {
            double s = i < 2 ? 1.0 : 0.45;
            int b = index.applyAsInt(vals[i]);
            verdict.setText(formulaText(vals[i], b, formula));
            verdict.setFillColor(Colors.WHITE);
            int k = inBucket.get(b).size();
            double y = BUCKET_BOTTOM - 34 - k * (CHIP_H + 8);
            Color c = Colors.interpolate(Colors.BLUE, Colors.ORANGE, nb <= 1 ? 0 : b / (double) (nb - 1));
            List<Animation> fly = new ArrayList<>();
            fly.add(new ArcMove(chip[i], bx[b], y, 80, d(0.8 * s)));
            fly.add(new ArcMove(num[i], bx[b], y, 80, d(0.8 * s)));
            fly.add(new ColorChange(chip[i], c, d(0.6 * s), ColorChange.Target.STROKE));
            playAll(fly);
            inBucket.get(b).add(i);
            home[i] = b;
            pause(0.12 * s);
        }
        pause(0.6);

        // 2) sort each bucket (insertion sort, as the slide suggests)
        if (sortEach) {
            verdict.setText("sort each bucket — insertion sort will do");
            verdict.setFillColor(Colors.ORANGE);
            pause(0.8);
            for (int b = 0; b < nb; b++) {
                List<Integer> items = inBucket.get(b);
                if (items.size() < 2) continue;
                for (int a = 1; a < items.size(); a++) {
                    int j = a;
                    while (j > 0 && vals[items.get(j)] < vals[items.get(j - 1)]) {
                        int lo = items.get(j), hi = items.get(j - 1);
                        double yLo = chip[lo].getPosition().y(), yHi = chip[hi].getPosition().y();
                        List<Animation> sw = new ArrayList<>();
                        sw.add(new ArcMove(chip[lo], bx[b], yHi, 40, d(0.5)));
                        sw.add(new ArcMove(num[lo], bx[b], yHi, 40, d(0.5)));
                        sw.add(new ArcMove(chip[hi], bx[b], yLo, -40, d(0.5)));
                        sw.add(new ArcMove(num[hi], bx[b], yLo, -40, d(0.5)));
                        playAll(sw);
                        items.set(j, hi);
                        items.set(j - 1, lo);
                        j--;
                    }
                }
                // smallest at the bottom, so reading a pile from the bottom up is increasing
                pause(0.3);
            }
            pause(0.5);
        } else {
            verdict.setText("every bucket holds equal values only — nothing to sort");
            verdict.setFillColor(Colors.GREEN);
            pause(1.6);
        }

        // 3) output the buckets in increasing order
        verdict.setText("output the buckets in increasing order");
        verdict.setFillColor(Colors.GREEN);
        double ox0 = -(n - 1) * 100 / 2.0;
        int slot = 0;
        for (int b = 0; b < nb; b++) {
            List<Integer> items = inBucket.get(b);
            // bottom of the pile first
            List<Integer> bottomUp = new ArrayList<>(items);
            java.util.Collections.sort(bottomUp, (p, q) -> Double.compare(chip[q].getPosition().y(), chip[p].getPosition().y()));
            for (int id : bottomUp) {
                List<Animation> out = new ArrayList<>();
                out.add(new ArcMove(chip[id], ox0 + 100 * slot, OUT_Y, 60, d(0.45)));
                out.add(new ArcMove(num[id], ox0 + 100 * slot, OUT_Y, 60, d(0.45)));
                out.add(new ColorChange(chip[id], FINAL_FILL, d(0.4)));
                out.add(new ColorChange(chip[id], FINAL_STROKE, d(0.4), ColorChange.Target.STROKE));
                playAll(out);
                slot++;
            }
        }
        verdict.setText("sorted");
        pause(2.0);
        fadeOutAll(d(0.9), mine);
        pause(0.3);
    }

    private String formulaText(int v, int bucket, String formula) {
        if (formula.contains("/")) return v + " / 20 = " + bucket + "   →   bucket " + bucket;
        return v + "   →   bucket " + bucket;
    }

    // ── "unsuitable for arbitrary types" ─────────────────────────────

    private void arbitraryTypes() {
        List<MObject> mine = new ArrayList<>();
        verdict.setText("");
        TextMob form = label("index = mark / 20", 0, -300, 32, Colors.GOLD, false, true);
        form.setFontFamily("Menlo");
        mine.add(form);
        String[] items = {"apple", "Karthik", "(2, 5)", "blue", "B+"};
        List<Animation> in = new ArrayList<>();
        in.add(new FadeIn(form, d(0.6)));
        RectMob[] chip = new RectMob[items.length];
        TextMob[] txt = new TextMob[items.length];
        TextMob[] q = new TextMob[items.length];
        for (int i = 0; i < items.length; i++) {
            double x = (i - 2) * 250;
            chip[i] = new RectMob(190, 62).setCornerRadius(8);
            chip[i].setFillColor(Colors.withAlpha(Colors.WHITE, 0.12));
            chip[i].setStrokeColor(Colors.LIGHT_GRAY);
            chip[i].setStrokeWidth(2.5);
            chip[i].setPosition(x, -120);
            chip[i].setOpacity(0);
            add(chip[i]);
            txt[i] = label(items[i], x, -120, 30, Colors.WHITE, false, true);
            q[i] = label("bucket ?", x, 0, 30, Colors.RED, false, true);
            in.add(new FadeInAt(chip[i], 0.1 * i, d(0.5)));
            in.add(new FadeInAt(txt[i], 0.1 * i, d(0.5)));
            mine.add(chip[i]);
            mine.add(txt[i]);
            mine.add(q[i]);
        }
        playAll(in);
        pause(0.6);
        List<Animation> ask = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            q[i].setScale(0.6);
            ask.add(new FadeInAt(q[i], 0.15 * i, d(0.5)));
            ask.add(new ScaleTo(q[i], 1.0, d(0.7)).setEasing(Easing.EASE_OUT));
        }
        ask.add(new ColorChange(form, Colors.RED, d(0.6)));
        playAll(ask);
        StrokeTextMob c = stroke("Unsuitable for arbitrary types: there is no index to compute for words, pairs or grades.",
                0, 140, 30, Colors.ORANGE, false);
        play(new Write(c, d(3.8)));
        StrokeTextMob c2 = stroke("We need a value that maps to a bucket — and keeps the order of the buckets.",
                0, 210, 28, Colors.LIGHT_GRAY, false);
        play(new Write(c2, d(3.2)));
        mine.add(c);
        mine.add(c2);
        pause(2.8);
        fadeOutAll(d(0.9), mine);
        pause(0.3);
    }
}
