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
 * Standalone clip for slide 4 of the priority-queue deck: the heap.
 *
 *   - the slide's lines in the pen-stroke style, then they swipe away
 *   - Structural property: a heap fills level by level, left to right (the places still free in the last level
 *     are shown as faint circles); two trees that break the rule (a hole in a middle level, a gap in the last level)
 *     are marked wrong; a tree growing level by level gives N = 1, 3, 7, 15 and its height, which is about log N
 *   - Heap order property: every parent is compared with its children, one family at a time; the opposite order
 *     is shown as a MinHeap
 *   - "NOT implemented using pointers": a node with two pointers is crossed out, and the tree turns into an array
 *     that is just the numbers written level by level
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSPQHeapScene extends PDSHeapClipBase {

    private final List<MObject> mine = new ArrayList<>();
    private StrokeTextMob head;
    private HV h;

    @Override
    public void construct() {
        head = writeHeading("Heap");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "By default, a binary heap.").kw("binary heap", Colors.BLUE));
        s.add(ln(0, "Structural Property: Complete tree").kw("Complete tree", Colors.GREEN));
        s.add(ln(1, "Only the last level may not be full"));
        s.add(ln(1, "The last level must have elements pushed to the left"));
        s.add(ln(1, "Has O(log N) height for N elements"));
        s.add(ln(0, "Heap Order Property: Child-key < Parent-key").kw("Child-key < Parent-key", Colors.ORANGE));
        s.add(ln(1, "Assumes unique elements"));
        s.add(ln(1, "Opposite property in MinHeap"));
        s.add(ln(0, "NOT implemented using pointers!").kw("NOT", Colors.RED));
        s.add(ln(0, "Implemented using arrays").kw("arrays", Colors.TEAL));
        List<List<MObject>> text = writeSlide(s, -340);
        pause(1.6);
        swipeAway(text);
        pause(0.4);

        structural();
        order();
        arrays();

        unsay();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private HV tree(int[] vals) {
        HV v = new HV(-330, -345, 760, 125, 34, 30, 4, false, 0, 0, 0, 0, 0);
        if (vals != null) v.build(vals, 0.2);
        mine.addAll(v.parts());
        return v;
    }

    private void retire(HV v) {
        fadeOutAll(d(0.8), v.parts());
        mine.removeAll(v.parts());
    }

    private List<MObject> drop(String text, Color c, double x, double y, double w) {
        List<MObject> ch = chip(text, x, y, w, 66, c, 30);
        List<Animation> an = new ArrayList<>();
        for (MObject m : ch) an.add(new DropIn(m, 40, 0, d(0.6)));
        playAll(an);
        mine.addAll(ch);
        return ch;
    }

    private TextMob tag(String text, Color c, double y) {
        TextMob t = label(text, 560, y, 30, c, false, true);
        play(new FadeIn(t, d(0.5)));
        mine.add(t);
        return t;
    }

    // ── structural property ──────────────────────────────────────────

    private void structural() {
        TextMob t1 = tag("Structural property: a complete tree", Colors.GREEN, -330);
        h = tree(null);
        for (int i = 11; i <= 15; i++) h.ghost(i);
        narr("A heap fills the tree level by level, and each level from left to right.", Colors.LIGHT_GRAY);
        pause(0.6);
        for (int i = 1; i <= 10; i++) {
            El e = h.make(i == 1 ? 52 : new int[]{0, 52, 39, 12, 6, 30, 5, 11, 3, 1, 29}[i], i, NODE_C);
            List<Animation> an = new ArrayList<>();
            h.showAnim(an, e, 0, d(0.45));
            if (i == 8) for (int g = 11; g <= 15; g++) an.add(new FadeIn(h.ghost[g], d(0.6)));
            playAll(an);
            pause(i < 4 ? 0.45 : 0.18);
            if (i == 1) narr("The root comes first.", Colors.LIGHT_GRAY);
            if (i == 3) narr("Then the second level, left to right.", Colors.LIGHT_GRAY);
            if (i == 7) narr("Then the third level, left to right.", Colors.LIGHT_GRAY);
            if (i == 10) narr("Only the last level may not be full, and its elements are pushed to the left.", Colors.GREEN);
        }
        List<MObject> ok = drop("✓  complete", Colors.GREEN, 560, -240, 280);
        pause(2.6);
        fadeOutAll(d(0.5), ok);
        mine.removeAll(ok);

        // wrong shapes
        retire(h);
        narr("A hole in a level that is not the last one is not allowed:", Colors.LIGHT_GRAY);
        h = tree(new int[]{52, 39, 12, 6, 30, 5, 0, 3, 1, 29});
        CircleMob gap = h.ghost(7);
        gap.setStrokeColor(Colors.withAlpha(Colors.RED, 0.95));
        play(new FadeIn(gap, d(0.5)));
        pause(1.0);
        narr("Index 7 is empty, but the level below it already has elements: not a complete tree.", Colors.RED);
        List<MObject> bad1 = drop("✗  a hole above the last level", Colors.RED, 560, -240, 520);
        pause(3.0);
        fadeOutAll(d(0.5), bad1);
        mine.removeAll(bad1);
        retire(h);
        narr("In the last level the elements must be pushed to the left:", Colors.LIGHT_GRAY);
        h = tree(new int[]{52, 39, 12, 6, 30, 5, 11, 3, 1, 0, 29});
        CircleMob gap2 = h.ghost(10);
        gap2.setStrokeColor(Colors.withAlpha(Colors.RED, 0.95));
        play(new FadeIn(gap2, d(0.5)));
        pause(1.0);
        narr("There is a gap at index 10 before 29: not a complete tree.", Colors.RED);
        List<MObject> bad2 = drop("✗  a gap in the last level", Colors.RED, 560, -240, 480);
        pause(3.0);
        fadeOutAll(d(0.5), bad2);
        mine.removeAll(bad2);
        retire(h);

        // height
        play(new FadeOut(t1, d(0.4)));
        mine.remove(t1);
        TextMob t2 = tag("Height: O(log N) for N elements", Colors.GREEN, -330);
        h = tree(null);
        int[] sizes = {1, 3, 7, 15};
        int k = 0;
        List<El> all = new ArrayList<>();
        for (int i = 1; i <= 15; i++) all.add(h.make(i, i, NODE_C));
        TextMob nLab = label("N = ", 440, -200, 54, Colors.WHITE, true, true);
        TextMob nNum = label("0", 560, -200, 54, Colors.GOLD, true, true);
        TextMob hLab = label("height = ", 440, -110, 54, Colors.WHITE, true, true);
        TextMob hNum = label("0", 730, -110, 54, Colors.GREEN, true, true);
        play(new FadeIn(nLab, d(0.4)), new FadeIn(nNum, d(0.4)), new FadeIn(hLab, d(0.4)), new FadeIn(hNum, d(0.4)));
        mine.add(nLab);
        mine.add(nNum);
        mine.add(hLab);
        mine.add(hNum);
        for (int lv = 0; lv < 4; lv++) {
            List<Animation> an = new ArrayList<>();
            for (El e : all) if (h.lv(e.idx) == lv) h.showAnim(an, e, 0, d(0.6));
            playAll(an);
            nNum.setText(String.valueOf(sizes[lv]));
            hNum.setText(String.valueOf(lv));
            pause(1.1);
        }
        narr("Every level holds twice as many as the one above, so N doubles while the height grows by just 1.", Colors.LIGHT_GRAY);
        pause(2.2);
        LaTeXMob f = latex("\\mathrm{height} = \\lfloor \\log_2 N \\rfloor", 56, 560, 40);
        play(new FadeIn(f, d(0.8)));
        mine.add(f);
        narr("1000 elements need only 10 levels, a million need 20: the height is O(log N).", Colors.GREEN);
        pause(3.4);
        play(new FadeOut(t2, d(0.4)));
        mine.remove(t2);
        retire(h);
        fadeOutAll(d(0.5), nLab, nNum, hLab, hNum, f);
        mine.removeAll(List.of(nLab, nNum, hLab, hNum, f));
        pause(0.3);
    }

    // ── heap order property ──────────────────────────────────────────

    private void order() {
        TextMob t = tag("Heap order property: child < parent", Colors.ORANGE, -330);
        h = tree(new int[]{52, 39, 12, 6, 30, 5, 11, 3, 1, 29});
        pause(0.5);
        narr("Every element is larger than its children. Look at each family:", Colors.LIGHT_GRAY);
        pause(0.9);
        int[] vals = {52, 39, 12, 6, 30, 5, 11, 3, 1, 29};
        for (int p = 1; p <= 5; p++) {
            List<Animation> an = new ArrayList<>();
            h.paintAnim(an, h.a[p], Colors.TEAL, d(0.3));
            StringBuilder sb = new StringBuilder(h.a[p].v + " > ");
            int c1 = 2 * p, c2 = 2 * p + 1;
            h.paintAnim(an, h.a[c1], Colors.ORANGE, d(0.3));
            h.paintEdgeAnim(an, c1, Colors.GREEN, d(0.3));
            sb.append(h.a[c1].v);
            if (h.a[c2] != null) {
                h.paintAnim(an, h.a[c2], Colors.ORANGE, d(0.3));
                h.paintEdgeAnim(an, c2, Colors.GREEN, d(0.3));
                sb.append(" and ").append(h.a[c2].v);
            }
            playAll(an);
            narr(sb.toString(), Colors.GREEN);
            pause(p <= 2 ? 1.2 : 0.7);
            List<Animation> back = new ArrayList<>();
            h.paintAnim(back, h.a[p], NODE_C, d(0.25));
            h.paintAnim(back, h.a[c1], NODE_C, d(0.25));
            if (h.a[c2] != null) h.paintAnim(back, h.a[c2], NODE_C, d(0.25));
            playAll(back);
        }
        List<MObject> ok = drop("every parent > its children", Colors.GREEN, 560, -240, 560);
        narr("The elements are unique here: no two are equal, so each comparison is strict.", Colors.LIGHT_GRAY);
        pause(3.0);
        fadeOutAll(d(0.5), ok);
        mine.removeAll(ok);
        retire(h);

        narr("A MinHeap has the opposite property: every parent is smaller than its children.", Colors.LIGHT_GRAY);
        play(new FadeOut(t, d(0.3)));
        mine.remove(t);
        TextMob t2 = tag("MinHeap: child > parent", Colors.PURPLE, -330);
        h = new HV(-330, -345, 760, 125, 34, 30, 4, false, 0, 0, 0, 0, 0);
        int[] mv = {1, 3, 5, 6, 29, 12, 11, 30, 39, 52};
        List<El> es = new ArrayList<>();
        for (int i = 0; i < mv.length; i++) es.add(h.make(mv[i], i + 1, Colors.PURPLE));
        for (int lv = 0; lv < 4; lv++) {
            List<Animation> an = new ArrayList<>();
            for (El e : es) if (h.lv(e.idx) == lv) h.showAnim(an, e, 0, d(0.5));
            playAll(an);
            pause(0.25);
        }
        mine.addAll(h.parts());
        pause(1.0);
        narr("The smallest element, 1, is at the root; each parent is smaller than its children.", Colors.PURPLE);
        pause(3.2);
        play(new FadeOut(t2, d(0.3)));
        mine.remove(t2);
        retire(h);
        pause(0.3);
    }

    // ── arrays, not pointers ─────────────────────────────────────────

    private void arrays() {
        TextMob t = tag("NOT implemented using pointers!", Colors.RED, -330);
        String[] parts = {"value", "left", "right"};
        List<MObject> node = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            List<MObject> ch = chip(parts[i], 440 + i * 170, -200, 160, 80, i == 0 ? Colors.BLUE : Colors.ORANGE, 30);
            node.addAll(ch);
        }
        List<Animation> an = new ArrayList<>();
        for (MObject m : node) an.add(new FadeIn(m, d(0.6)));
        playAll(an);
        mine.addAll(node);
        TextMob cap = label("a pointer node: a value and two pointers", 560, -120, 28, Colors.LIGHT_GRAY, false, false);
        play(new FadeIn(cap, d(0.5)));
        mine.add(cap);
        pause(1.2);
        LineMob x1 = new LineMob(380, -250, 840, -150, Colors.RED, 8);
        LineMob x2 = new LineMob(380, -150, 840, -250, Colors.RED, 8);
        play(new FadeIn(x1, d(0.4)), new FadeIn(x2, d(0.4)));
        mine.add(x1);
        mine.add(x2);
        narr("A heap does not need left and right pointers at all.", Colors.RED);
        pause(2.6);
        List<MObject> gone = new ArrayList<>(node);
        gone.add(cap);
        gone.add(x1);
        gone.add(x2);
        fadeOutAll(d(0.6), gone);
        mine.removeAll(gone);
        play(new FadeOut(t, d(0.3)));
        mine.remove(t);
        TextMob t2 = tag("Implemented using arrays", Colors.TEAL, -330);

        h = new HV(-390, -345, 720, 125, 34, 30, 4, true, -820, 190, 72, 64, 13);
        h.cellsOn = false;
        h.build(new int[]{52, 39, 12, 6, 30, 5, 11, 3, 1, 29}, 0.2);
        h.showFrame(d(0.4));
        mine.addAll(h.parts());
        pause(0.6);
        narr("Write the elements level by level, left to right: the array is the whole heap.", Colors.LIGHT_GRAY);
        h.flatten(0.18, d(0.5));
        pause(1.0);
        List<MObject> ok = drop("just an array of numbers", Colors.TEAL, 560, -200, 540);
        narr("The shape of the tree is hidden in the indexes: the next slide shows how.", Colors.TEAL);
        pause(3.4);
        play(new FadeOut(t2, d(0.3)));
        mine.remove(t2);
    }
}
