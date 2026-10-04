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
 * Standalone clip for slide 25 of the arrays deck: merge sort.
 *
 *   - the slide text comes one line at a time in the pen-stroke style, then the listing types in
 *     under it and the text swipes off
 *   - the code runs on eight bars laid out as the recursion tree: every call to mergeSort splits
 *     its bars into two halves one level down (the first two calls slowly, the rest quicker),
 *     and every merge pulls the two sorted halves back up in order, one bar at a time
 *   - a counter tallies the elements copied (each element goes to a temporary array and back),
 *     which is the "array copying" the slide blames for merge sort being slow in practice
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSMergeSortScene extends PDSSortClipBase {

    private static final double CX = 300, PITCH = 76, GAP = 40, BASE0 = -170, DY = 145;
    private static final int[] DATA = {5, 2, 8, 1, 9, 3, 7, 4};
    // boundaries that are split apart at each depth of the recursion
    private static final int[][] SPLITS = {{}, {4}, {2, 4, 6}, {1, 2, 3, 4, 5, 6, 7}};

    private StrokeTextMob head;
    private CodeBox cb;
    private TextMob readout, verdict, counters;
    private Bars bars;
    private int copies = 0, calls = 0, merges = 0;

    @Override
    public void construct() {
        head = writeHeading("Merge Sort");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Divide-and-Conquer"));
        s.add(ln(1, "Divide the array into two halves"));
        s.add(ln(1, "Sort each array separately"));
        s.add(ln(1, "Merge the two sorted sequences"));
        s.add(ln(0, "Worst case complexity: O(n log n)"));
        s.add(ln(1, "Not efficient in practice due to array copying."));
        s.add(ln(0, "Classwork: Write the code.").kw("Classwork", Colors.RED));
        List<List<MObject>> text = writeSlide(s, -365);
        pause(0.5);

        CodeBox box = new CodeBox(new String[]{
            "void mergeSort(int start, int end) {",
            "   if (start < end) {",
            "      int mid = (start + end) / 2;",
            "      mergeSort(start, mid);",
            "      mergeSort(mid + 1, end);",
            "      merge(start, mid, end);",
            "   }",
            "}"}, -860, 110, 26, 40);
        box.typeIn(4.2);
        pause(1.2);

        // the text leaves, the listing glides up to the top left
        List<Animation> out = new ArrayList<>();
        for (int i = 0; i < text.size(); i++)
            for (MObject m : text.get(i))
                if (m.getOpacity() > 0) out.add(new SwipeOut(m, 900, 0.1 * i * PACE, d(1.0)));
        box.shift(out, 0, -495, d(1.2));
        playAll(out);
        for (List<MObject> g : text) for (MObject m : g) remove(m);
        cb = box;

        readout = label("", -560, 10, 28, Colors.WHITE, false, true);
        readout.setFontFamily("Menlo");
        verdict = label("", -560, 70, 32, Colors.WHITE, false, true);
        counters = label("", -560, 130, 28, Colors.GOLD, false, true);
        bars = new Bars(DATA, CX, BASE0, PITCH, 56, 11, 24, false);
        List<MObject> mine = new ArrayList<>(bars.parts());
        List<Animation> in = new ArrayList<>();
        bars.fadeIn(in, 0.08, d(0.6));
        in.add(new FadeIn(readout, d(0.4)));
        in.add(new FadeIn(verdict, d(0.4)));
        in.add(new FadeIn(counters, d(0.4)));
        playAll(in);
        counters.setText("elements copied: 0");
        pause(0.5);

        play(new FadeIn(cb.hl, d(0.4)));
        ms(0, DATA.length - 1, 0);

        List<Animation> fin = new ArrayList<>();
        for (SBar b : bars.at) bars.paintFinal(fin, b, d(0.6));
        playAll(fin);
        verdict.setText("sorted");
        verdict.setFillColor(Colors.GREEN);
        readout.setText("");
        pause(1.0);
        StrokeTextMob c1 = stroke("log n levels, and every level copies all n elements to a temporary array and back:",
                CX, 345, 26, Colors.LIGHT_GRAY, false);
        play(new Write(c1, d(3.4)));
        StrokeTextMob c2 = stroke("O(n log n) comparisons — but all that array copying is why it is slow in practice.",
                CX, 400, 26, Colors.ORANGE, false);
        play(new Write(c2, d(3.4)));
        mine.add(c1);
        mine.add(c2);
        pause(3.0);

        List<MObject> all = new ArrayList<>(mine);
        all.addAll(cb.parts());
        all.add(readout);
        all.add(verdict);
        all.add(counters);
        all.add(head);
        fadeOutAll(1.5, all);
        pause(0.5);
    }

    private int level(int depth) { return Math.min(depth, 3); }

    private double xOf(int depth, int idx) {
        int[] sp = SPLITS[level(depth)];
        int count = 0;
        for (int b : sp) if (b <= idx) count++;
        return CX + (idx - (DATA.length - 1) / 2.0) * PITCH + GAP * (count - sp.length / 2.0);
    }

    private double baseOf(int depth) { return BASE0 + DY * level(depth); }

    private void placeAt(List<Animation> into, SBar b, int depth, int idx, double bulge, double dur) {
        double x = xOf(depth, idx), base = baseOf(depth);
        into.add(new ArcMove(b.rect, x, base - b.h / 2, bulge, dur));
        into.add(new ArcMove(b.lbl, x, base - b.h - 24, bulge, dur));
    }

    private void ms(int start, int end, int depth) {
        double s = calls < 3 ? 0.9 : 0.5;
        calls++;
        readout.setText(String.format("mergeSort(%d, %d)", start, end));
        play(cb.moveHl(0, d(0.3 * s)));
        play(cb.moveHl(1, d(0.25 * s)));
        if (start >= end) {
            verdict.setText("one element: already sorted");
            verdict.setFillColor(Colors.LIGHT_GRAY);
            pause(0.35 * s);
            return;
        }
        int mid = (start + end) / 2;
        play(cb.moveHl(2, d(0.3 * s)));
        readout.setText(String.format("start = %d  end = %d  mid = %d", start, end, mid));
        verdict.setText("divide:  [" + start + ".." + mid + "]  |  [" + (mid + 1) + ".." + end + "]");
        verdict.setFillColor(Colors.TEAL);
        List<Animation> split = new ArrayList<>();
        for (int i = start; i <= end; i++) placeAt(split, bars.at[i], depth + 1, i, 0, d(0.75 * s));
        playAll(split);
        pause(0.2 * s);

        play(cb.moveHl(3, d(0.25 * s)));
        ms(start, mid, depth + 1);
        play(cb.moveHl(4, d(0.25 * s)));
        ms(mid + 1, end, depth + 1);
        play(cb.moveHl(5, d(0.3 * s)));
        merge(start, mid, end, depth);
    }

    private void merge(int start, int mid, int end, int depth) {
        double s = merges == 0 ? 1.0 : (merges == 1 ? 0.7 : 0.45);
        merges++;
        readout.setText(String.format("merge(%d, %d, %d)", start, mid, end));
        int len = end - start + 1;
        SBar[] left = new SBar[mid - start + 1], right = new SBar[end - mid];
        for (int i = 0; i < left.length; i++) left[i] = bars.at[start + i];
        for (int i = 0; i < right.length; i++) right[i] = bars.at[mid + 1 + i];
        SBar[] merged = new SBar[len];
        int i = 0, j = 0;
        for (int k = 0; k < len; k++) {
            boolean takeLeft = j >= right.length || (i < left.length && left[i].value <= right[j].value);
            SBar pick = takeLeft ? left[i] : right[j];
            List<Animation> look = new ArrayList<>();
            if (i < left.length) bars.paint(look, left[i], Colors.ORANGE, d(0.2 * s));
            if (j < right.length) bars.paint(look, right[j], Colors.ORANGE, d(0.2 * s));
            playAll(look);
            if (i < left.length && j < right.length) {
                verdict.setText(left[i].value + " ≤ " + right[j].value + " ?   take the " + (takeLeft ? "left" : "right") + " one");
            } else {
                verdict.setText("one side is empty: copy the rest");
            }
            verdict.setFillColor(Colors.ORANGE);
            pause(0.35 * s);
            List<Animation> mv = new ArrayList<>();
            placeAt(mv, pick, depth, start + k, takeLeft ? 40 : -40, d(0.6 * s));
            bars.paintOriginal(mv, pick, d(0.5 * s));
            playAll(mv);
            merged[k] = pick;
            if (takeLeft) i++; else j++;
            copies += 2;
            counters.setText("elements copied: " + copies);
        }
        List<Animation> calm = new ArrayList<>();
        for (SBar b : merged) bars.paintOriginal(calm, b, d(0.3 * s));
        playAll(calm);
        for (int k = 0; k < len; k++) bars.at[start + k] = merged[k];
        pause(0.2 * s);
    }
}
