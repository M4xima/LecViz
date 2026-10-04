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
 * Standalone clip for slide 28 of the arrays deck: counting sort.
 *
 *   - the slide text comes one line at a time in the pen-stroke style, then swipes off
 *   - the slide's table is then built row by row from its own numbers: every element of the
 *     original array drops into its bucket, the bucket sizes are counted, the prefix sum turns
 *     sizes into starting indexes, and the buckets are copied out to their places in the output
 *     array
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSCountingSortScene extends PDSSortClipBase {

    private static final int[] ORIG = {4, 1, 4, 9, 11, 7, 8, 1, 3, 4};
    // the slide draws ten buckets in increasing order; this is the bucket each value sits in
    private static final java.util.Map<Integer, Integer> BUCKET = java.util.Map.of(1, 0, 3, 2, 4, 3, 7, 4, 8, 6, 9, 8, 11, 9);
    private static final double[] ROW_Y = {-230, -110, 10, 130, 250};
    private static final double CELL_W = 108, CELL_H = 64, PITCH = 125;

    private StrokeTextMob head;
    private TextMob verdict;

    private double colX(int c) { return (c - 4.5) * PITCH; }

    @Override
    public void construct() {
        head = writeHeading("Counting Sort");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Bucketize elements."));
        s.add(ln(0, "Find count of elements in each bucket."));
        s.add(ln(0, "Perform prefix sum.").kw("prefix sum", Colors.RED));
        s.add(ln(0, "Copy elements from buckets to original array."));
        List<List<MObject>> text = writeSlide(s, -365);
        pause(1.2);
        swipeAway(text);
        pause(0.4);
        demo();
        fadeOutAll(1.5, head);
        pause(0.5);
    }

    private RectMob cellBox(double x, double y, Color c, double fillAlpha) {
        RectMob r = new RectMob(CELL_W, CELL_H).setCornerRadius(8);
        r.setFillColor(Colors.withAlpha(c, fillAlpha));
        r.setStrokeColor(Colors.withAlpha(c, 0.7));
        r.setStrokeWidth(2.5);
        r.setPosition(x, y);
        r.setOpacity(0);
        add(r);
        return r;
    }

    private void demo() {
        int n = ORIG.length;
        List<MObject> mine = new ArrayList<>();
        List<Animation> in = new ArrayList<>();
        verdict = label("", 0, -390, 34, Colors.WHITE, false, true);
        mine.add(verdict);
        in.add(new FadeIn(verdict, d(0.5)));

        String[] names = {"Original array", "Buckets", "Bucket sizes", "Starting index", "Output array"};
        RectMob[][] box = new RectMob[5][n];
        TextMob[][] txt = new TextMob[5][n];
        Color[] tint = {Colors.BLUE, Colors.TEAL, Colors.GOLD, Colors.PURPLE, Colors.GREEN};
        for (int r = 0; r < 5; r++) {
            TextMob nm = label(names[r], -890, ROW_Y[r], 28, Colors.LIGHT_GRAY, true, true);
            in.add(new FadeInAt(nm, 0.3 * r, d(0.5)));
            mine.add(nm);
            for (int c = 0; c < n; c++) {
                box[r][c] = cellBox(colX(c), ROW_Y[r], tint[r], r == 0 ? 0.28 : 0.1);
                txt[r][c] = label("", colX(c), ROW_Y[r], 30, Colors.WHITE, false, true);
                in.add(new FadeInAt(box[r][c], 0.3 * r + 0.03 * c, d(0.5)));
                in.add(new FadeInAt(txt[r][c], 0.3 * r + 0.03 * c, d(0.5)));
                mine.add(box[r][c]);
                mine.add(txt[r][c]);
            }
        }
        for (int c = 0; c < n; c++) txt[0][c].setText(String.valueOf(ORIG[c]));
        playAll(in);
        pause(0.8);

        // 1) bucketize: every element drops into its bucket
        verdict.setText("Bucketize: every element drops into its bucket");
        verdict.setFillColor(Colors.TEAL);
        pause(0.6);
        List<List<Integer>> inBucket = new ArrayList<>();
        for (int c = 0; c < n; c++) inBucket.add(new ArrayList<>());
        for (int i = 0; i < n; i++) {
            double s = i < 3 ? 1.0 : 0.45;
            int v = ORIG[i], b = BUCKET.get(v);
            RectMob chip = cellBox(colX(i), ROW_Y[0], Colors.BLUE, 0.5);
            chip.setOpacity(1);
            TextMob ct = label(String.valueOf(v), colX(i), ROW_Y[0], 30, Colors.WHITE, false, true);
            ct.setOpacity(1);
            List<Animation> fly = new ArrayList<>();
            fly.add(new ArcMove(chip, colX(b), ROW_Y[1], 60, d(0.8 * s)));
            fly.add(new ArcMove(ct, colX(b), ROW_Y[1], 60, d(0.8 * s)));
            fly.add(new ColorChange(txt[0][i], Colors.GRAY, d(0.4 * s)));
            playAll(fly);
            inBucket.get(b).add(v);
            StringBuilder sb = new StringBuilder();
            for (int k = 0; k < inBucket.get(b).size(); k++) sb.append(k == 0 ? "" : ", ").append(inBucket.get(b).get(k));
            txt[1][b].setText(sb.toString());
            txt[1][b].setOpacity(1);
            play(new FadeOut(chip, d(0.2 * s)), new FadeOut(ct, d(0.2 * s)));
            remove(chip);
            remove(ct);
            pause(0.1 * s);
        }
        pause(0.8);

        // 2) bucket sizes
        verdict.setText("Count the elements in each bucket");
        verdict.setFillColor(Colors.GOLD);
        pause(0.5);
        int[] size = new int[n];
        for (int c = 0; c < n; c++) {
            size[c] = inBucket.get(c).size();
            txt[2][c].setText(String.valueOf(size[c]));
            txt[2][c].setScale(0.6);
            List<Animation> pop = new ArrayList<>();
            pop.add(new FadeIn(txt[2][c], d(0.3)));
            pop.add(new ScaleTo(txt[2][c], 1.0, d(0.4)).setEasing(Easing.EASE_OUT));
            pop.add(new ColorChange(box[1][c], Colors.withAlpha(Colors.GOLD, 0.35), d(0.25)));
            playAll(pop);
            List<Animation> back = new ArrayList<>();
            back.add(new ColorChange(box[1][c], Colors.withAlpha(Colors.TEAL, 0.1), d(0.25)));
            playAll(back);
        }
        pause(0.8);

        // 3) prefix sum: starting index = sum of the sizes before
        verdict.setText("Prefix sum: start[c] = start[c-1] + size[c-1]");
        verdict.setFillColor(Colors.PURPLE);
        pause(0.8);
        int[] start = new int[n];
        for (int c = 0; c < n; c++) {
            start[c] = c == 0 ? 0 : start[c - 1] + size[c - 1];
            double s = c < 3 ? 1.0 : 0.5;
            List<Animation> look = new ArrayList<>();
            if (c > 0) {
                look.add(new ColorChange(box[2][c - 1], Colors.withAlpha(Colors.GOLD, 0.45), d(0.25 * s)));
                look.add(new ColorChange(box[3][c - 1], Colors.withAlpha(Colors.PURPLE, 0.45), d(0.25 * s)));
                verdict.setText("start[" + c + "] = " + start[c - 1] + " + " + size[c - 1] + " = " + start[c]);
            } else {
                verdict.setText("start[0] = 0");
            }
            playAll(look);
            txt[3][c].setText(String.valueOf(start[c]));
            txt[3][c].setScale(0.6);
            List<Animation> pop = new ArrayList<>();
            pop.add(new FadeIn(txt[3][c], d(0.3 * s)));
            pop.add(new ScaleTo(txt[3][c], 1.0, d(0.4 * s)).setEasing(Easing.EASE_OUT));
            playAll(pop);
            List<Animation> back = new ArrayList<>();
            if (c > 0) {
                back.add(new ColorChange(box[2][c - 1], Colors.withAlpha(Colors.GOLD, 0.1), d(0.25 * s)));
                back.add(new ColorChange(box[3][c - 1], Colors.withAlpha(Colors.PURPLE, 0.1), d(0.25 * s)));
            }
            playAll(back);
            pause(0.2 * s);
        }
        pause(0.8);

        // 4) copy every bucket to its place in the output array
        verdict.setText("Copy the buckets out, starting at their starting indexes");
        verdict.setFillColor(Colors.GREEN);
        pause(0.8);
        for (int c = 0; c < n; c++) {
            for (int k = 0; k < inBucket.get(c).size(); k++) {
                double s = c < 4 ? 0.9 : 0.5;
                int v = inBucket.get(c).get(k);
                int slot = start[c] + k;
                RectMob chip = cellBox(colX(c), ROW_Y[1], Colors.TEAL, 0.5);
                chip.setOpacity(1);
                TextMob ct = label(String.valueOf(v), colX(c), ROW_Y[1], 30, Colors.WHITE, false, true);
                ct.setOpacity(1);
                verdict.setText(v + "  →  output[" + start[c] + " + " + k + "] = output[" + slot + "]");
                List<Animation> fly = new ArrayList<>();
                fly.add(new ArcMove(chip, colX(slot), ROW_Y[4], 70, d(0.8 * s)));
                fly.add(new ArcMove(ct, colX(slot), ROW_Y[4], 70, d(0.8 * s)));
                fly.add(new ColorChange(chip, FINAL_FILL, d(0.7 * s)));
                fly.add(new ColorChange(chip, FINAL_STROKE, d(0.7 * s), ColorChange.Target.STROKE));
                playAll(fly);
                txt[4][slot].setText(String.valueOf(v));
                txt[4][slot].setOpacity(1);
                box[4][slot].setFillColor(FINAL_FILL);
                box[4][slot].setStrokeColor(FINAL_STROKE);
                remove(chip);
                remove(ct);
                pause(0.1 * s);
            }
        }
        verdict.setText("sorted: the output array");
        verdict.setFillColor(Colors.GREEN);
        pause(1.2);
        StrokeTextMob c1 = stroke("No comparisons between elements at all: count, prefix-sum, copy.", 0, 350, 30, Colors.ORANGE, false);
        play(new Write(c1, d(3.2)));
        mine.add(c1);
        pause(3.0);
        fadeOutAll(d(0.9), mine);
        pause(0.3);
    }
}
