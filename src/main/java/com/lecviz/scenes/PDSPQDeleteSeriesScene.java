package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Standalone clip for slide 11 of the priority-queue deck: the classwork with five deleteMax calls in a row.
 *
 *   - the two rules of the slide in the pen-stroke style, then they swipe away
 *   - the heap 39 (30 31) (15 29 6 3) (4 7) as tree and array, and five deleteMax calls, each narrated while the
 *     hole percolates down; the first two are slow, the rest quicker; every tree is exactly the one on the slide
 *   - a strip collects the removed maximums: 39 31 30 29 15, largest first
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSPQDeleteSeriesScene extends PDSHeapClipBase {

    private final List<MObject> mine = new ArrayList<>();

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("Classwork: deleteMax");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "DeleteMax").kw("DeleteMax", Colors.GOLD));
        s.add(ln(1, "Remove the root."));
        s.add(ln(1, "Percolate the hole down, and finally replace it with the last element.").kw("finally", Colors.ORANGE));
        List<List<MObject>> text = writeSlide(s, -300);
        pause(1.6);
        swipeAway(text);
        pause(0.4);

        HV h = new HV(-390, -345, 720, 125, 34, 30, 4, true, -820, 190, 72, 64, 13);
        h.build(new int[]{39, 30, 31, 15, 29, 6, 3, 4, 7}, 0.25);
        h.showFrame(d(0.5));
        mine.addAll(h.parts());
        pause(0.5);

        OutStrip out = new OutStrip("removed:", 300, -250, 62, Colors.GOLD);
        out.showTitle();
        mine.addAll(out.all());
        cmpNum = label("0", 450, 120, 84, Colors.WHITE, false, true);
        moveNum = label("0", 760, 120, 84, Colors.WHITE, false, true);
        TextMob c1 = label("comparisons", 450, 190, 28, Colors.GRAY, false, true);
        TextMob c2 = label("moves", 760, 190, 28, Colors.GRAY, false, true);
        TextMob call = label("", 610, -140, 38, Colors.LIGHT_GRAY, false, true);
        play(new FadeIn(cmpNum, d(0.4)), new FadeIn(moveNum, d(0.4)), new FadeIn(c1, d(0.4)), new FadeIn(c2, d(0.4)));
        mine.add(cmpNum);
        mine.add(moveNum);
        mine.add(c1);
        mine.add(c2);
        mine.add(call);
        pause(0.5);

        for (int k = 1; k <= 5; k++) {
            call.setText("deleteMax  #" + k);
            play(new FadeIn(call, d(0.3)));
            resetCounts();
            int v = heapDeleteMax(h, k <= 2 ? 0.9 : 0.5, true);
            out.add(String.valueOf(v));
            mine.addAll(out.all());
            pause(k < 5 ? 0.7 : 1.0);
            if (k < 5) play(new FadeOut(call, d(0.25)));
        }
        narr("39, 31, 30, 29, 15: each deleteMax returns the current largest, so the values come out in decreasing order.", Colors.GREEN);
        pause(3.6);
        unsay();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }
}
