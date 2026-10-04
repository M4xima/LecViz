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
 * Standalone clip for slide 22 of the arrays deck: selection sort.
 *
 *   - the slide text comes one line at a time in the pen-stroke style
 *   - the listing types in with its "Find min." brace, then the text swipes off
 *   - the code runs on bars: a ring follows the smallest value found so far while jj scans the
 *     rest, swap(iimin, ii) sends it to its final place, and the sorted prefix (the invariant)
 *     turns green
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSSelectionSortScene extends PDSSortClipBase {

    private StrokeTextMob head;
    private CodeBox cb;
    private TextMob readout, verdict, counters;

    @Override
    public void construct() {
        head = writeHeading("Selection Sort");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Approach: Choose the minimum element, and"));
        s.add(ln(3, "push it to its final place."));
        s.add(ln(0, "What is the invariant?"));
        s.add(ln(1, "First i elements are at their final places after i"));
        s.add(ln(2, "iterations."));
        s.add(ln(0, "Classwork: Write the code.").kw("Classwork", Colors.RED));
        List<List<MObject>> text = writeSlide(s, -365);
        pause(0.6);

        // the listing, with the brace around the inner loop that finds the minimum
        CodeBox box = new CodeBox(new String[]{
            "for (ii = 0 ; ii < N - 1; ++ii) {",
            "   int iimin = ii;",
            "",
            "   for (jj = ii + 1; jj < N; ++jj)",
            "      if (arr[jj] < arr[iimin])",
            "         iimin = jj;",
            "   swap(iimin, ii);",
            "}"}, -860, 40, 26, 40);
        box.typeIn(4.0);
        List<MObject> stage = new ArrayList<>();
        double bx = box.left + box.width + 22;
        double y1 = box.lineY(3) - 18, y2 = box.lineY(5) + 18, ym = (y1 + y2) / 2;
        List<LineMob> brace = new ArrayList<>();
        brace.add(new LineMob(bx - 10, y1, bx, y1 + 8, Colors.GREEN, 3));
        brace.add(new LineMob(bx, y1 + 8, bx, ym - 8, Colors.GREEN, 3));
        brace.add(new LineMob(bx, ym - 8, bx + 14, ym, Colors.GREEN, 3));
        brace.add(new LineMob(bx + 14, ym, bx, ym + 8, Colors.GREEN, 3));
        brace.add(new LineMob(bx, ym + 8, bx, y2 - 8, Colors.GREEN, 3));
        brace.add(new LineMob(bx, y2 - 8, bx - 10, y2, Colors.GREEN, 3));
        List<Animation> co = new ArrayList<>();
        for (LineMob l : brace) { add(l); co.add(new DrawLine(l, d(0.7))); stage.add(l); }
        callout(co, stage, "Find min.", null, bx + 140, ym, 190, 62, bx + 16, ym);
        playAll(co);
        pause(1.8);

        // the text leaves; the listing and its callout stay
        swipeAway(text);
        pause(0.4);
        cb = box;

        readout = label("", 430, -300, 28, Colors.WHITE, false, true);
        readout.setFontFamily("Menlo");
        verdict = label("", 430, -240, 32, Colors.WHITE, false, true);
        counters = label("", 430, -190, 28, Colors.GOLD, false, true);
        play(new FadeIn(readout, d(0.4)), new FadeIn(verdict, d(0.4)), new FadeIn(counters, d(0.4)));
        int[] stats = run(new int[]{5, 3, 6, 2, 4, 1});

        List<MObject> demo = new ArrayList<>(cb.parts());
        demo.add(readout);
        demo.add(verdict);
        demo.add(counters);
        demo.addAll(stage);
        fadeOutAll(d(0.8), demo);
        pause(0.3);

        StrokeTextMob c1 = stroke("Always (n-1) + (n-2) + ... + 1 comparisons — " + stats[0] + " here —",
                0, -120, 34, Colors.WHITE, false);
        play(new Write(c1, d(3.0)));
        StrokeTextMob c2 = stroke("but at most n - 1 swaps: " + stats[1] + " here. Each swap sends one value home.",
                0, -50, 34, Colors.ORANGE, false);
        play(new Write(c2, d(3.2)));
        pause(3.0);
        fadeOutAll(1.5, head, c1, c2);
        pause(0.5);
    }

    private int[] run(int[] data) {
        int n = data.length;
        Bars bars = new Bars(data, 430, 300, 100, 70, 30, 30, true);
        List<MObject> mine = new ArrayList<>(bars.parts());
        List<Animation> in = new ArrayList<>();
        bars.fadeIn(in, 0.1, d(0.6));
        Ptr pi = pointer("ii", bars.slotX(0), 300 + 52, false, Colors.GOLD);
        Ptr pj = pointer("jj", bars.slotX(1), 300 + 52, false, Colors.TEAL);
        mine.addAll(pi.parts());
        mine.addAll(pj.parts());
        RectMob ring = new RectMob(86, 100).setCornerRadius(12);
        ring.setFillColor(Color.TRANSPARENT);
        ring.setStrokeColor(Colors.PINK);
        ring.setStrokeWidth(4.5);
        ring.setOpacity(0);
        add(ring);
        TextMob minTag = label("iimin", 0, 0, 24, Colors.PINK, false, true);
        mine.add(ring);
        mine.add(minTag);
        playAll(in);
        pause(0.5);

        int comps = 0, swaps = 0;
        boolean first = true;
        StrokeTextMob lesson = null;
        for (int ii = 0; ii < n - 1; ii++) {
            double s = ii == 0 ? 1.0 : (ii == 1 ? 0.6 : 0.35);
            readout.setText(String.format("ii = %d", ii));
            List<Animation> top = new ArrayList<>();
            top.add(cb.moveHl(0, d(0.35 * s)));
            pi.go(top, bars.slotX(ii), d(0.4 * s));
            if (first) {
                top.add(new FadeIn(cb.hl, d(0.4)));
                top.add(new FadeIn(pi.arrow, d(0.4)));
                top.add(new FadeIn(pi.lab, d(0.4)));
            }
            playAll(top);
            if (lesson != null) { play(new FadeOut(lesson, d(0.4))); remove(lesson); lesson = null; }
            pause(0.3 * s);

            // int iimin = ii
            int iimin = ii;
            SBar cur = bars.at[iimin];
            ring.setSize(bars.bw + 16, cur.h + 16);
            ring.setPosition(bars.slotX(iimin), 300 - cur.h / 2);
            minTag.setPosition(bars.slotX(iimin), 300 - cur.h - 62);
            List<Animation> mk = new ArrayList<>();
            mk.add(cb.moveHl(1, d(0.3 * s)));
            mk.add(new FadeIn(ring, d(0.4 * s + 0.1)));
            mk.add(new FadeIn(minTag, d(0.4 * s + 0.1)));
            playAll(mk);
            readout.setText(String.format("ii = %d   iimin = %d", ii, iimin));
            pause(0.2 * s);

            for (int jj = ii + 1; jj < n; jj++) {
                List<Animation> scan = new ArrayList<>();
                scan.add(cb.moveHl(3, d(0.3 * s)));
                pj.go(scan, bars.slotX(jj), d(0.35 * s));
                if (first) {
                    scan.add(new FadeIn(pj.arrow, d(0.4)));
                    scan.add(new FadeIn(pj.lab, d(0.4)));
                    first = false;
                }
                playAll(scan);
                readout.setText(String.format("ii = %d  jj = %d  iimin = %d", ii, jj, iimin));
                play(cb.moveHl(4, d(0.25 * s)));
                comps++;
                boolean smaller = bars.at[jj].value < bars.at[iimin].value;
                verdict.setText(bars.at[jj].value + " < " + bars.at[iimin].value + " ?   " + (smaller ? "yes → new minimum" : "no"));
                verdict.setFillColor(smaller ? Colors.PINK : Colors.LIGHT_GRAY);
                counters.setText("comparisons: " + comps + "     swaps: " + swaps);
                pause(0.45 * s);
                if (smaller) {
                    play(cb.moveHl(5, d(0.25 * s)));
                    iimin = jj;
                    SBar nb = bars.at[iimin];
                    ring.setSize(bars.bw + 16, nb.h + 16);
                    List<Animation> mv = new ArrayList<>();
                    mv.add(new MoveTo(ring, bars.slotX(iimin), 300 - nb.h / 2, d(0.45 * s)).setEasing(Easing.EASE_IN_OUT));
                    mv.add(new MoveTo(minTag, bars.slotX(iimin), 300 - nb.h - 62, d(0.45 * s)).setEasing(Easing.EASE_IN_OUT));
                    playAll(mv);
                    readout.setText(String.format("ii = %d  jj = %d  iimin = %d", ii, jj, iimin));
                }
                pause(0.1 * s);
            }

            // swap(iimin, ii): the minimum goes to its final place
            play(cb.moveHl(6, d(0.3 * s)));
            List<Animation> sw = new ArrayList<>();
            if (iimin != ii) {
                verdict.setText("swap(" + iimin + ", " + ii + ")");
                verdict.setFillColor(Colors.ORANGE);
                bars.swap(sw, ii, iimin, d(0.8 * s));
                swaps++;
            } else {
                verdict.setText("iimin = ii: already in place");
                verdict.setFillColor(Colors.LIGHT_GRAY);
            }
            sw.add(new FadeOut(ring, d(0.5 * s)));
            sw.add(new FadeOut(minTag, d(0.5 * s)));
            playAll(sw);
            counters.setText("comparisons: " + comps + "     swaps: " + swaps);
            List<Animation> done = new ArrayList<>();
            bars.paintFinal(done, bars.at[ii], d(0.5 * s));
            playAll(done);
            if (ii < 2) {
                lesson = stroke("After iteration " + (ii + 1) + ": the first " + (ii + 1) + " element"
                        + (ii == 0 ? " is" : "s are") + " at their final place" + (ii == 0 ? "." : "s."),
                        430, -120, 28, Colors.GREEN, false);
                play(new Write(lesson, d(2.2)));
                pause(0.9);
            }
        }
        if (lesson != null) { play(new FadeOut(lesson, d(0.4))); remove(lesson); lesson = null; }
        List<Animation> end = new ArrayList<>();
        bars.paintFinal(end, bars.at[n - 1], d(0.5));
        end.add(new FadeOut(pi.arrow, d(0.4)));
        end.add(new FadeOut(pi.lab, d(0.4)));
        end.add(new FadeOut(pj.arrow, d(0.4)));
        end.add(new FadeOut(pj.lab, d(0.4)));
        playAll(end);
        verdict.setText("sorted");
        verdict.setFillColor(Colors.GREEN);
        readout.setText("");
        pause(2.0);
        fadeOutAll(d(0.8), mine);
        return new int[]{comps, swaps};
    }
}
