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
 * Standalone clip for the first half of slide 12 of the priority-queue deck: increaseKey, decreaseKey and remove.
 *
 *   - the three lines of the slide in the pen-stroke style, with the "Source: heap.cpp" note, then they swipe away
 *   - the heap 52 (39 12) (6 30 5 11) (3 1 29) as tree and array; increaseKey makes 5 into 45, which now beats its
 *     parent 12 and climbs one level; decreaseKey makes 39 into 4, which now loses to its children and sinks to a leaf
 *   - remove: the element is made the largest (its key becomes infinity), climbs to the root, and deleteMax takes it out
 *   - each operation ends with its cost, O(log N): one pass along one path of the tree
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSPQKeysScene extends PDSHeapClipBase {

    private final List<MObject> mine = new ArrayList<>();
    private HV h;
    private TextMob title;
    private final List<MObject> chips = new ArrayList<>();

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("Other Operations");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "increaseKey, decreaseKey").kw("increaseKey", Colors.GREEN).kw("decreaseKey", Colors.ORANGE));
        s.add(ln(0, "remove").kw("remove", Colors.RED));
        s.add(ln(0, "build").kw("build", Colors.TEAL));
        List<List<MObject>> text = writeSlide(s, -250);
        TextMob src = label("Source: heap.cpp", 520, -250, 28, Colors.GOLD, false, true);
        play(new FadeIn(src, d(0.7)));
        List<MObject> srcGroup = new ArrayList<>();
        srcGroup.add(src);
        text.add(srcGroup);
        pause(1.6);
        swipeAway(text);
        pause(0.4);

        h = new HV(-390, -345, 720, 125, 34, 30, 4, true, -820, 190, 72, 64, 13);
        h.build(new int[]{52, 39, 12, 6, 30, 5, 11, 3, 1, 29}, 0.25);
        h.showFrame(d(0.5));
        mine.addAll(h.parts());
        cmpNum = label("0", 450, 120, 84, Colors.WHITE, false, true);
        moveNum = label("0", 760, 120, 84, Colors.WHITE, false, true);
        TextMob c1 = label("comparisons", 450, 190, 28, Colors.GRAY, false, true);
        TextMob c2 = label("swaps", 760, 190, 28, Colors.GRAY, false, true);
        title = label("", 610, -250, 40, Colors.WHITE, false, true);
        play(new FadeIn(cmpNum, d(0.4)), new FadeIn(moveNum, d(0.4)), new FadeIn(c1, d(0.4)), new FadeIn(c2, d(0.4)));
        mine.add(cmpNum);
        mine.add(moveNum);
        mine.add(c1);
        mine.add(c2);
        mine.add(title);
        pause(0.6);

        increase();
        decrease();
        remove();

        unsay();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void heading(String text, Color c) {
        title.setText(text);
        title.setFillColor(c);
        play(new FadeIn(title, d(0.5)));
        resetCounts();
    }

    private void verdict(String text, Color c) {
        List<MObject> ch = chip(text, 610, -130, 560, 70, c, 34);
        List<Animation> an = new ArrayList<>();
        for (MObject m : ch) an.add(new DropIn(m, 40, 0, d(0.6)));
        playAll(an);
        mine.addAll(ch);
        chips.addAll(ch);
    }

    private void clearVerdict() {
        fadeOutAll(d(0.5), chips);
        mine.removeAll(chips);
        chips.clear();
        play(new FadeOut(title, d(0.4)));
    }

    // ── increaseKey ──────────────────────────────────────────────────

    private void increase() {
        heading("increaseKey(5 → 45)", Colors.GREEN);
        narr("increaseKey: the key of the element 5 (index 6) becomes 45.", Colors.LIGHT_GRAY);
        pause(1.0);
        El e = h.a[6];
        h.paint(e, NEW_C, d(0.3));
        h.setValue(e, 45);
        play(new ScaleTo(e.shape, 1.3, d(0.2)), new ScaleTo(e.text, 1.3, d(0.2)));
        play(new ScaleTo(e.shape, 1.0, d(0.2)), new ScaleTo(e.text, 1.0, d(0.2)));
        pause(0.5);
        narr("A larger key can only break the order with the parent above: percolate up.", Colors.GREEN);
        pause(1.6);
        int at = percolateUp(h, 6, 1.0, true);
        h.paint(h.a[at], NODE_C, d(0.4));
        pause(0.6);
        verdict("increaseKey: O(log N)", Colors.GREEN);
        pause(2.2);
        clearVerdict();
    }

    // ── decreaseKey ──────────────────────────────────────────────────

    private void decrease() {
        heading("decreaseKey(39 → 4)", Colors.ORANGE);
        narr("decreaseKey: the key of the element 39 (index 2) becomes 4.", Colors.LIGHT_GRAY);
        pause(1.0);
        El e = h.a[2];
        h.paint(e, NEW_C, d(0.3));
        h.setValue(e, 4);
        play(new ScaleTo(e.shape, 1.3, d(0.2)), new ScaleTo(e.text, 1.3, d(0.2)));
        play(new ScaleTo(e.shape, 1.0, d(0.2)), new ScaleTo(e.text, 1.0, d(0.2)));
        pause(0.5);
        narr("A smaller key can only break the order with the children below: percolate down.", Colors.ORANGE);
        pause(1.6);
        percolateDown(h, 2, 1.0, true);
        pause(0.6);
        verdict("decreaseKey: O(log N)", Colors.ORANGE);
        pause(2.2);
        clearVerdict();
    }

    // ── remove ───────────────────────────────────────────────────────

    private void remove() {
        heading("remove(30)", Colors.RED);
        narr("remove: take out any element. Here the element 30 (index 2).", Colors.LIGHT_GRAY);
        pause(1.2);
        El e = h.a[2];
        h.paint(e, NEW_C, d(0.3));
        narr("Make it the largest element of all: increaseKey to infinity.", Colors.GREEN);
        pause(0.8);
        h.setValue(e, 999);
        e.shown = "∞";
        e.text.setText("∞");
        if (e.cell != null) e.cell.text.setText("∞");
        play(new ScaleTo(e.shape, 1.3, d(0.2)), new ScaleTo(e.text, 1.3, d(0.2)));
        play(new ScaleTo(e.shape, 1.0, d(0.2)), new ScaleTo(e.text, 1.0, d(0.2)));
        pause(0.8);
        percolateUp(h, 2, 1.0, true);
        pause(0.6);
        narr("It is the root now: a plain deleteMax removes it.", Colors.GOLD);
        pause(1.4);
        heapDeleteMax(h, 0.8, true);
        pause(0.6);
        verdict("remove: O(log N)", Colors.RED);
        narr("remove = increaseKey to infinity + deleteMax: two passes along a path, so O(log N).", Colors.GOLD);
        pause(3.2);
        clearVerdict();
    }
}
