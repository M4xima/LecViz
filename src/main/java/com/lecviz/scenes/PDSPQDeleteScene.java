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
 * Standalone clip for slide 8 of the priority-queue deck: deleteMax.
 *
 *   - the slide's two lines in the pen-stroke style, then they swipe away
 *   - the MaxHeap of slide 5 as tree and array; deleteMax removes 52: the root becomes a gold hole, the hole
 *     percolates down (at every step the larger child moves up), and the last element, 29, fills it in the end;
 *     each comparison is narrated, the counters tick, and the result is the tree on the slide
 *   - the clip ends with the cost: one step per level, O(log N)
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSPQDeleteScene extends PDSHeapClipBase {

    private final List<MObject> mine = new ArrayList<>();

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("deleteMax");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Remove the root.").kw("root", Colors.GOLD));
        s.add(ln(0, "Percolate the hole down.").kw("hole", Colors.GOLD));
        List<List<MObject>> text = writeSlide(s, -260);
        pause(1.6);
        swipeAway(text);
        pause(0.4);

        HV h = new HV(-390, -345, 720, 125, 34, 30, 4, true, -820, 190, 72, 64, 13);
        h.build(new int[]{52, 39, 12, 6, 30, 5, 11, 3, 1, 29}, 0.25);
        h.showFrame(d(0.5));
        mine.addAll(h.parts());
        pause(0.5);

        RectMob panel = panel(610, -190, 620, 250, Colors.TEAL, 0.08);
        TextMob r1 = label("1  remove the root", 330, -250, 30, Colors.GOLD, true, true);
        TextMob r2 = label("2  percolate the hole down", 330, -170, 30, Colors.GOLD, true, true);
        TextMob r3 = label("larger child moves up; last element fills in", 330, -105, 24, Colors.LIGHT_GRAY, true, false);
        play(new FadeIn(panel, d(0.5)), new FadeIn(r1, d(0.5)), new FadeIn(r2, d(0.5)), new FadeIn(r3, d(0.5)));
        cmpNum = label("0", 450, 120, 84, Colors.WHITE, false, true);
        moveNum = label("0", 760, 120, 84, Colors.WHITE, false, true);
        TextMob c1 = label("comparisons", 450, 190, 28, Colors.GRAY, false, true);
        TextMob c2 = label("moves", 760, 190, 28, Colors.GRAY, false, true);
        play(new FadeIn(cmpNum, d(0.4)), new FadeIn(moveNum, d(0.4)), new FadeIn(c1, d(0.4)), new FadeIn(c2, d(0.4)));
        mine.add(panel);
        mine.add(r1);
        mine.add(r2);
        mine.add(r3);
        mine.add(cmpNum);
        mine.add(moveNum);
        mine.add(c1);
        mine.add(c2);
        pause(0.6);

        heapDeleteMax(h, 1.0, true);
        pause(1.0);
        narr("The result is the tree on the slide: still complete, and every parent is larger than its children.", Colors.GREEN);
        pause(2.6);
        narr("The hole goes down at most one level per step: deleteMax is O(log N).", Colors.GOLD);
        List<MObject> chip = chip("deleteMax: O(log N)", 610, -10, 480, 70, Colors.GOLD, 36);
        List<Animation> an = new ArrayList<>();
        for (MObject m : chip) an.add(new DropIn(m, 40, 0, d(0.6)));
        playAll(an);
        mine.addAll(chip);
        pause(3.2);
        unsay();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }
}
