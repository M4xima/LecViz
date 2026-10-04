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
 * Standalone clip for slide 29 of the arrays deck: radix sort.
 *
 *   - the slide text comes one line at a time in the pen-stroke style, with the O(P * (N + B))
 *     callout beside the heading; the text then swipes off
 *   - the slide's ten numbers are written with three digits each; in every pass the digit being
 *     looked at lights up, each number drops into the bucket for that digit, and the buckets are
 *     read out left to right back into the row — units, then tens, then hundreds — exactly the
 *     rows of the slide
 *   - the slide's classwork (33, 453, 124, 225, 1023, 432, 2232) runs the same way in four passes
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSRadixSortScene extends PDSSortClipBase {

    private static final int[] DEMO = {64, 8, 216, 512, 27, 729, 0, 1, 343, 125};
    private static final int[] CLASSWORK = {33, 453, 124, 225, 1023, 432, 2232};
    private static final double ROW_Y = -290, BUCKET_TOP = -170, BUCKET_BOTTOM = 200;
    private static final String[] DIGIT_NAME = {"units", "tens", "hundreds", "thousands"};

    private StrokeTextMob head;
    private StrokeTextMob tag;
    private final List<MObject> callout = new ArrayList<>();

    @Override
    public void construct() {
        head = writeHeading("Radix Sort");
        pause(0.4);

        // O(P * (N + B)) and what the letters mean
        RectMob box = new RectMob(380, 150).setCornerRadius(16);
        box.setFillColor(Colors.withAlpha(Colors.GREEN, 0.26));
        box.setStrokeColor(Colors.GREEN);
        box.setStrokeWidth(2);
        box.setPosition(640, -400);
        box.setOpacity(0);
        add(box);
        LineMob leader = new LineMob(190, -470, 450, -415, Colors.withAlpha(Colors.GREEN, 0.9), 3);
        add(leader);
        TextMob c1 = label("O(P * (N + B))", 640, -445, 30, Colors.WHITE, false, true);
        TextMob c2 = label("P = passes", 640, -405, 24, Colors.LIGHT_GRAY, false, false);
        TextMob c3 = label("N = elements", 640, -375, 24, Colors.LIGHT_GRAY, false, false);
        TextMob c4 = label("B = buckets", 640, -345, 24, Colors.LIGHT_GRAY, false, false);
        play(new DrawLine(leader, d(0.6)), new FadeIn(box, d(0.6)), new FadeIn(c1, d(0.6)));
        callout.add(box);
        callout.add(leader);
        callout.add(c1);
        callout.add(c2);
        callout.add(c3);
        callout.add(c4);
        play(new FadeInAt(c2, 0, d(0.5)), new FadeInAt(c3, 0.4, d(0.5)), new FadeInAt(c4, 0.8, d(0.5)));
        pause(0.4);

        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Generalization of bucket sort."));
        s.add(ln(0, "Radix sort sorts using different digits."));
        s.add(ln(0, "At every step, elements are moved to buckets"));
        s.add(ln(3, "based on their ith digits, starting from the least"));
        s.add(ln(3, "significant digit."));
        s.add(ln(0, "Classwork: 33, 453, 124, 225, 1023, 432, 2232").kw("Classwork", Colors.RED));
        List<List<MObject>> text = writeSlide(s, -300);
        pause(1.2);
        swipeAway(text);
        pause(0.4);

        // the slide's example: 64, 8, 216, ... in three passes
        run(DEMO, 3, new double[]{1.0, 0.55, 0.4});

        // the classwork, solved the same way
        StrokeTextMob cw = stroke("Classwork: 33, 453, 124, 225, 1023, 432, 2232", 0, -385, 34, Colors.ORANGE, false);
        play(new Write(cw, d(2.8)));
        pause(0.8);
        play(new FadeOut(cw, d(0.5)));
        remove(cw);
        run(CLASSWORK, 4, new double[]{0.7, 0.45, 0.35, 0.35});

        List<MObject> all = new ArrayList<>(callout);
        all.add(head);
        fadeOutAll(1.5, all);
        pause(0.5);
    }

    // ── a number drawn digit by digit ────────────────────────────────

    private final class Chip {
        final int value, digits;
        final RectMob box;
        final TextMob[] dig;
        final double[] off;

        Chip(int v, int digits, double x, double y) {
            this.value = v;
            this.digits = digits;
            double w = digits * 24 + 34;
            box = new RectMob(w, 62).setCornerRadius(8);
            box.setFillColor(Colors.withAlpha(Colors.BLUE, 0.2));
            box.setStrokeColor(Colors.withAlpha(Colors.BLUE, 0.8));
            box.setStrokeWidth(2.5);
            box.setPosition(x, y);
            box.setOpacity(0);
            add(box);
            String str = String.format("%0" + digits + "d", v);
            int natural = String.valueOf(v).length();
            dig = new TextMob[digits];
            off = new double[digits];
            for (int i = 0; i < digits; i++) {
                off[i] = (i - (digits - 1) / 2.0) * 24;
                boolean padded = i < digits - natural;
                dig[i] = label(String.valueOf(str.charAt(i)), x + off[i], y, 32, padded ? Colors.GRAY : Colors.WHITE, false, true);
                dig[i].setFontFamily("Menlo");
            }
        }

        List<MObject> parts() {
            List<MObject> l = new ArrayList<>();
            l.add(box);
            for (TextMob t : dig) l.add(t);
            return l;
        }

        void moveTo(List<Animation> into, double x, double y, double bulge, double dur) {
            into.add(new ArcMove(box, x, y, bulge, dur));
            for (int i = 0; i < digits; i++) into.add(new ArcMove(dig[i], x + off[i], y, bulge, dur));
        }

        /** The digit p places from the right (0 = units) glows; the others settle back. */
        void focus(List<Animation> into, int p, double dur) {
            int natural = String.valueOf(value).length();
            for (int i = 0; i < digits; i++) {
                boolean padded = i < digits - natural;
                boolean on = i == digits - 1 - p;
                into.add(new ColorChange(dig[i], on ? Colors.GOLD : (padded ? Colors.GRAY : Colors.WHITE), dur));
            }
        }

        void unfocus(List<Animation> into, double dur) {
            focus(into, 99, dur);
        }
    }

    private double rowX(int slot, int n) {
        double pitch = Math.min(135, 1300.0 / n);
        return (slot - (n - 1) / 2.0) * pitch;
    }

    private double bucketX(int b) { return (b - 4.5) * 135; }

    private void run(int[] vals, int digits, double[] speeds) {
        int n = vals.length;
        List<MObject> mine = new ArrayList<>();
        List<Animation> in = new ArrayList<>();

        for (int b = 0; b < 10; b++) {
            RectMob bx = new RectMob(122, BUCKET_BOTTOM - BUCKET_TOP).setCornerRadius(12);
            bx.setFillColor(GHOST_FILL);
            bx.setStrokeColor(Colors.withAlpha(Colors.TEAL, 0.7));
            bx.setStrokeWidth(2.5);
            bx.setPosition(bucketX(b), (BUCKET_TOP + BUCKET_BOTTOM) / 2);
            bx.setOpacity(0);
            add(bx);
            TextMob lab = label(String.valueOf(b), bucketX(b), BUCKET_TOP - 28, 34, Colors.TEAL, false, true);
            in.add(new FadeInAt(bx, 0.05 * b, d(0.5)));
            in.add(new FadeInAt(lab, 0.05 * b, d(0.5)));
            mine.add(bx);
            mine.add(lab);
        }
        Chip[] chip = new Chip[n];
        int[] order = new int[n];          // order[slot] = which chip is in that row slot
        for (int i = 0; i < n; i++) {
            chip[i] = new Chip(vals[i], digits, rowX(i, n), ROW_Y);
            order[i] = i;
            for (MObject m : chip[i].parts()) {
                in.add(new FadeInAt(m, 0.08 * i, d(0.5)));
                mine.add(m);
            }
        }
        playAll(in);
        pause(0.6);

        tag = null;
        for (int p = 0; p < digits; p++) {
            double s = speeds[Math.min(p, speeds.length - 1)];
            if (tag != null) { play(new FadeOut(tag, d(0.4))); remove(tag); }
            tag = stroke("Pass " + (p + 1) + ": the " + DIGIT_NAME[p] + " digit", 0, -385, 34, Colors.WHITE, false);
            play(new Write(tag, d(1.6)));

            List<Animation> glow = new ArrayList<>();
            for (int i = 0; i < n; i++) chip[i].focus(glow, p, d(0.5));
            playAll(glow);
            pause(0.4 * s);

            // every number drops into the bucket of its digit, in the order it stands in the row
            List<List<Integer>> stack = new ArrayList<>();
            for (int b = 0; b < 10; b++) stack.add(new ArrayList<>());
            for (int slot = 0; slot < n; slot++) {
                int id = order[slot];
                int dgt = (int) ((chip[id].value / Math.pow(10, p)) % 10);
                int k = stack.get(dgt).size();
                double y = BUCKET_BOTTOM - 36 - k * 66;
                List<Animation> fly = new ArrayList<>();
                chip[id].moveTo(fly, bucketX(dgt), y, 70, d(0.7 * s));
                playAll(fly);
                stack.get(dgt).add(id);
                pause(0.06 * s);
            }
            pause(0.5 * s);

            // read the buckets left to right back into the row
            int slot = 0;
            for (int b = 0; b < 10; b++) {
                for (int id : stack.get(b)) {
                    List<Animation> up = new ArrayList<>();
                    chip[id].moveTo(up, rowX(slot, n), ROW_Y, 50, d(0.5 * s));
                    playAll(up);
                    order[slot] = id;
                    slot++;
                }
            }
            List<Animation> calm = new ArrayList<>();
            for (int i = 0; i < n; i++) chip[i].unfocus(calm, d(0.4));
            playAll(calm);
            pause(0.7 * s);
        }

        List<Animation> fin = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            fin.add(new ColorChange(chip[i].box, FINAL_FILL, d(0.6)));
            fin.add(new ColorChange(chip[i].box, FINAL_STROKE, d(0.6), ColorChange.Target.STROKE));
        }
        play(new FadeOut(tag, d(0.4)));
        remove(tag);
        playAll(fin);
        tag = stroke("Sorted after " + digits + " passes — one per digit.", 0, -385, 34, Colors.GREEN, false);
        play(new Write(tag, d(2.2)));
        pause(2.2);
        List<MObject> gone = new ArrayList<>(mine);
        gone.add(tag);
        fadeOutAll(d(0.9), gone);
        tag = null;
        pause(0.3);
    }
}
