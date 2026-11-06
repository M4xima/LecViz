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
 * Standalone clip for slides 5-6 of the priority-queue deck: the MaxHeap example and the MaxHeap ADT.
 *
 *   Slide 5  the heap 52 (39 12) (6 30 5 11) (3 1 29) as a tree and as its array. A sweep reads the tree level by
 *            level, left to right, and the cells appear: that is the array. The index rules (parent at i / 2,
 *            children at 2i and 2i + 1) are shown on three examples; "30 and 5 cannot be ordered" is shown by their
 *            missing path; the fixed array length is drawn as a wall; the invalid heap of the slide (15 below 12)
 *            ends the slide
 *   Slide 6  the MaxHeap class is typed in and the highlight bar goes over its six operations, naming the two (insert
 *            and deleteMax) that the next clips work out
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSPQHeapExampleScene extends PDSHeapClipBase {

    private static final Color REL_C = Colors.TEAL;
    private final List<MObject> mine = new ArrayList<>();
    private StrokeTextMob head;
    private HV h;

    @Override
    public void construct() {
        exampleSlide();
        adtSlide();
    }

    // ── slide 5 ──────────────────────────────────────────────────────

    private void flash(List<El> es, Color c, double dur) {
        List<Animation> an = new ArrayList<>();
        for (El e : es) h.paintAnim(an, e, c, dur);
        playAll(an);
    }

    private void reset(List<El> es) { flash(es, NODE_C, d(0.3)); }

    private List<El> els(int... idx) {
        List<El> l = new ArrayList<>();
        for (int i : idx) l.add(h.a[i]);
        return l;
    }

    private void exampleSlide() {
        head = writeHeading("MaxHeap Example");
        pause(0.5);
        h = new HV(-390, -345, 720, 125, 34, 30, 4, true, -820, 190, 72, 64, 13);
        h.cellsOn = false;
        h.build(new int[]{52, 39, 12, 6, 30, 5, 11, 3, 1, 29}, 0.25);
        mine.addAll(h.parts());
        pause(0.4);
        List<MObject> ok = chip("✓  a heap", -390, -420, 220, 56, Colors.GREEN, 30);
        List<Animation> an = new ArrayList<>();
        for (MObject m : ok) an.add(new DropIn(m, 30, 0, d(0.5)));
        playAll(an);
        mine.addAll(ok);

        // stores elements level-wise, left-to-right
        h.showFrame(d(0.5));
        narr("Stores elements level-wise, left-to-right.", Colors.LIGHT_GRAY);
        pause(0.6);
        for (int i = 1; i <= 10; i++) {
            h.paint(h.a[i], CMP_C, d(0.18));
            pause(0.08);
            h.paint(h.a[i], NODE_C, d(0.18));
        }
        h.flatten(0.15, d(0.5));
        pause(0.6);
        narr("Read the tree level by level, left to right: that is the array. Index 0 stays unused.", Colors.GREEN);
        pause(2.2);

        // the index rules
        RectMob panel = panel(610, -190, 620, 280, Colors.TEAL, 0.08);
        LaTeXMob f1 = latex("\\mathrm{parent}(i) = \\lfloor i / 2 \\rfloor", 48, 610, -270);
        LaTeXMob f2 = latex("\\mathrm{children}(i) = 2i \\ \\mathrm{and} \\ 2i + 1", 48, 610, -170);
        TextMob f3 = label("for the element at index i", 610, -85, 26, Colors.GRAY, false, false);
        play(new FadeIn(panel, d(0.5)), new FadeIn(f1, d(0.7)), new FadeIn(f2, d(0.7)), new FadeIn(f3, d(0.5)));
        mine.add(panel);
        mine.add(f1);
        mine.add(f2);
        mine.add(f3);
        pause(1.0);

        flash(els(5), NEW_C, d(0.35));
        narr("Element 30 is at index 5. Its parent is at 5 / 2 = 2 (floor): that is 39.", Colors.PINK);
        flash(els(2), REL_C, d(0.35));
        pause(2.2);
        reset(els(5, 2));
        flash(els(2), NEW_C, d(0.35));
        narr("Element 39 is at index 2. Its children are at 2 * 2 = 4 and 2 * 2 + 1 = 5: that is 6 and 30.", Colors.PINK);
        flash(els(4, 5), REL_C, d(0.35));
        pause(2.2);
        reset(els(2, 4, 5));
        flash(els(3), NEW_C, d(0.35));
        narr("Element 12 is at index 3. Its children are at 6 and 7: that is 5 and 11. Its parent is at 3 / 2 = 1: the root.", Colors.PINK);
        flash(els(6, 7, 1), REL_C, d(0.35));
        pause(2.6);
        reset(els(3, 6, 7, 1));
        pause(0.4);

        // ordering only along a path
        narr("Elements can be ordered only if one is reachable from the other.", Colors.LIGHT_GRAY);
        pause(1.2);
        List<Animation> pa = new ArrayList<>();
        h.paintAnim(pa, h.a[1], REL_C, d(0.3));
        h.paintAnim(pa, h.a[2], REL_C, d(0.3));
        h.paintAnim(pa, h.a[5], REL_C, d(0.3));
        h.paintEdgeAnim(pa, 2, Colors.GREEN, d(0.3));
        h.paintEdgeAnim(pa, 5, Colors.GREEN, d(0.3));
        playAll(pa);
        narr("52 and 30: there is a path down from 52 to 30, so we know 52 > 30.", Colors.GREEN);
        pause(2.4);
        List<Animation> pb = new ArrayList<>();
        h.paintAnim(pb, h.a[1], NODE_C, d(0.3));
        h.paintAnim(pb, h.a[2], NODE_C, d(0.3));
        h.paintEdgeAnim(pb, 2, Colors.withAlpha(Colors.LIGHT_GRAY, 0.8), d(0.3));
        h.paintEdgeAnim(pb, 5, Colors.withAlpha(Colors.LIGHT_GRAY, 0.8), d(0.3));
        h.paintAnim(pb, h.a[6], Colors.RED, d(0.3));
        h.paintAnim(pb, h.a[5], Colors.RED, d(0.3));
        playAll(pb);
        TextMob q = label("?", (h.px(5) + h.px(6)) / 2, h.py(5) - 8, 40, Colors.RED, false, true);
        play(new FadeIn(q, d(0.4)));
        mine.add(q);
        narr("30 and 5: neither is above the other, so they cannot be ordered.", Colors.RED);
        pause(2.8);
        play(new FadeOut(q, d(0.3)));
        remove(q);
        mine.remove(q);
        reset(els(5, 6));
        pause(0.3);

        // fixed length
        RectMob wall = new RectMob(8, 120).setCornerRadius(4);
        wall.setFillColor(Colors.withAlpha(Colors.RED, 0.8));
        wall.setStrokeColor(Color.TRANSPARENT);
        wall.setPosition(h.ax0 + 13 * h.cw + 4, h.ay);
        wall.setOpacity(0);
        add(wall);
        mine.add(wall);
        play(new FadeIn(wall, d(0.5)));
        narr("Need to depend upon a fixed array length: the heap cannot grow past the end of its array.", Colors.ORANGE);
        pause(3.0);
        play(new FadeOut(wall, d(0.4)));

        // the invalid heap
        narr("And a tree with 15 below 12 is not a heap:", Colors.LIGHT_GRAY);
        pause(1.0);
        h.setValue(h.a[6], 15);
        List<Animation> bad = new ArrayList<>();
        h.paintAnim(bad, h.a[3], Colors.RED, d(0.4));
        h.paintAnim(bad, h.a[6], Colors.RED, d(0.4));
        h.paintEdgeAnim(bad, 6, Colors.RED, d(0.4));
        playAll(bad);
        List<Animation> sw = new ArrayList<>();
        sw.add(new FadeOut(ok.get(0), d(0.4)));
        sw.add(new FadeOut(ok.get(1), d(0.4)));
        playAll(sw);
        List<MObject> no = chip("✗  not a heap", -390, -420, 260, 56, Colors.RED, 30);
        List<Animation> dn = new ArrayList<>();
        for (MObject m : no) dn.add(new DropIn(m, 30, 0, d(0.5)));
        playAll(dn);
        mine.addAll(no);
        narr("The child 15 is larger than its parent 12: the heap order property is broken.", Colors.RED);
        pause(3.6);
        unsay();
        fadeOutAll(d(1.2), mine);
        mine.clear();
        fadeOutAll(d(1.0), head);
        pause(0.4);
    }

    // ── slide 6 ──────────────────────────────────────────────────────

    private void adtSlide() {
        head = writeHeading("MaxHeap ADT");
        pause(0.5);
        CodeBox code = new CodeBox(new String[]{
                "class MaxHeap {",
                "public:",
                "    insert(e);",
                "    deleteMax();",
                "",
                "    increaseKey(e);",
                "    decreaseKey(e);",
                "    remove(e);",
                "    buildHeap();",
                "};"}, -880, -330, 34, 54);
        code.typeIn(5.0);
        mine.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.3)));
        pause(0.6);
        String[][] notes = {
                {"2", "insert: add an element"},
                {"3", "deleteMax: remove the maximum"},
                {"5", "increaseKey: make a key larger"},
                {"6", "decreaseKey: make a key smaller"},
                {"7", "remove: take out any element"},
                {"8", "buildHeap: turn N elements into a heap"}};
        double ny = -190;
        for (String[] nt : notes) {
            int line = Integer.parseInt(nt[0]);
            play(code.moveHl(line, d(0.35)));
            boolean core = line <= 3;
            TextMob t = label(nt[1], -330, ny, 32, core ? Colors.GOLD : Colors.LIGHT_GRAY, true, core);
            play(new FadeIn(t, d(0.5)));
            mine.add(t);
            ny += 70;
            pause(core ? 1.4 : 0.9);
        }
        play(code.moveHl(2, d(0.35)));
        List<MObject> chip = chip("insert and deleteMax are the two core operations", 130, 330, 1000, 70, Colors.GOLD, 32);
        List<Animation> an = new ArrayList<>();
        for (MObject m : chip) an.add(new DropIn(m, 40, 0, d(0.6)));
        playAll(an);
        mine.addAll(chip);
        pause(3.0);
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }
}
