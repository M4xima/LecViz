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
 * Standalone clip for slide 7 of the priority-queue deck: insert.
 *
 *   - the slide's two lines in the pen-stroke style, then they swipe away
 *   - the MaxHeap of slide 5 appears as a tree and as its array; insert(40) is done step by step: the new element
 *     sits at the first empty leaf and percolates up, one comparison with its parent at a time (40 beats 30, beats
 *     39, stops under 52: the picture on the slide)
 *   - insert(60) then climbs all the way to the root, to show the worst case: as many swaps as the tree is high
 *   - counters tick once per comparison and once per move; the clip ends with O(log N)
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSPQInsertScene extends PDSHeapClipBase {

    private final List<MObject> mine = new ArrayList<>();

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("insert");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Insert at the first empty leaf.").kw("first empty leaf", Colors.GREEN));
        s.add(ln(0, "Percolate the element upward").kw("Percolate", Colors.ORANGE));
        List<List<MObject>> text = writeSlide(s, -260);
        pause(1.6);
        swipeAway(text);
        pause(0.4);

        HV h = new HV(-390, -345, 720, 125, 34, 30, 4, true, -820, 190, 72, 64, 13);
        h.build(new int[]{52, 39, 12, 6, 30, 5, 11, 3, 1, 29}, 0.25);
        h.showFrame(d(0.5));
        mine.addAll(h.parts());
        pause(0.5);

        // the rules and the counters on the right
        RectMob panel = panel(610, -190, 620, 250, Colors.TEAL, 0.08);
        TextMob r1 = label("1  insert at the first empty leaf", 330, -250, 30, Colors.GREEN, true, true);
        TextMob r2 = label("2  percolate the element upward", 330, -170, 30, Colors.ORANGE, true, true);
        TextMob r3 = label("swap while it is larger than its parent", 330, -105, 26, Colors.LIGHT_GRAY, true, false);
        play(new FadeIn(panel, d(0.5)), new FadeIn(r1, d(0.5)), new FadeIn(r2, d(0.5)), new FadeIn(r3, d(0.5)));
        cmpNum = label("0", 450, 120, 84, Colors.WHITE, false, true);
        moveNum = label("0", 760, 120, 84, Colors.WHITE, false, true);
        TextMob c1 = label("comparisons", 450, 190, 28, Colors.GRAY, false, true);
        TextMob c2 = label("swaps", 760, 190, 28, Colors.GRAY, false, true);
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

        // the example of the slide
        El e = heapInsert(h, 40, 1.0, true);
        h.paint(e, NODE_C, d(0.4));
        pause(1.2);

        // worst case: all the way up
        narr("Now insert(60): it is larger than everything, so it climbs all the way.", Colors.LIGHT_GRAY);
        pause(1.6);
        resetCounts();
        El f = heapInsert(h, 60, 0.7, true);
        h.paint(f, NODE_C, d(0.4));
        pause(0.8);
        narr("At most one swap per level: the tree has O(log N) levels, so insert is O(log N).", Colors.GOLD);
        List<MObject> chip = chip("insert: O(log N)", 610, -10, 440, 70, Colors.GOLD, 36);
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
