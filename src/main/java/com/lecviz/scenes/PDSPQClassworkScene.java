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
 * Standalone clip for slides 9-10 of the priority-queue deck: the deleteMax classwork.
 *
 *   - the question: deleteMax on the heap 39 (30 12) (6 29 5 11) (3 1)
 *   - slide 9, the two wrong answers: (1) the hole is percolated down and then simply left there, so the array has
 *     an empty cell in the middle and the tree is no longer complete; (2) the smaller child is moved up and the last
 *     element lands under it, so 29 ends up below 6 and the heap order is broken
 *   - slide 10, the rule written out, and the right answer: percolate the hole down along the larger children and
 *     finally put the last element in it; narrated step by step, the same tree as the slide
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSPQClassworkScene extends PDSHeapClipBase {

    private static final int[] DATA = {39, 30, 12, 6, 29, 5, 11, 3, 1};
    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();
    private HV h;
    private final List<MObject> extras = new ArrayList<>();

    @Override
    public void construct() {
        head = writeHeading("Classwork: deleteMax");
        pause(0.5);
        List<MObject> q = new ArrayList<>(question(1, "deleteMax on this heap.", -250, 46, new String[]{"deleteMax"}, Colors.GOLD));
        pause(0.8);
        q.add(subLine("Remove the largest element and keep the tree a heap.", -170, 32));
        pause(2.0);
        fadeOutAll(d(1.0), q);
        pause(0.3);

        h = fresh();
        mine.addAll(h.parts());
        pause(0.8);

        attemptOne();
        h = fresh();
        attemptTwo();
        h = fresh();
        rightWay();

        unsay();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private HV fresh() {
        if (h != null) {
            List<MObject> gone = new ArrayList<>(h.parts());
            gone.addAll(extras);
            fadeOutAll(d(0.8), gone);
            mine.removeAll(gone);
            extras.clear();
        }
        HV v = new HV(-390, -345, 720, 125, 34, 30, 4, true, -820, 190, 72, 64, 13);
        v.build(DATA, 0.2);
        v.showFrame(d(0.4));
        mine.addAll(v.parts());
        pause(0.5);
        return v;
    }

    private List<MObject> verdict(String text, Color c, double y) {
        List<MObject> ch = chip(text, 610, y, 620, 80, c, 30);
        List<Animation> an = new ArrayList<>();
        for (MObject m : ch) an.add(new DropIn(m, 40, 0, d(0.6)));
        playAll(an);
        mine.addAll(ch);
        extras.addAll(ch);
        return ch;
    }

    private TextMob title(String text, Color c) {
        TextMob t = label(text, 330, -300, 34, c, true, true);
        play(new FadeIn(t, d(0.5)));
        mine.add(t);
        extras.add(t);
        return t;
    }

    // ── slide 9, first answer ────────────────────────────────────────

    private void attemptOne() {
        TextMob t = title("Answer 1 (slide 9)", Colors.ORANGE);
        narr("Remove 39 and keep moving the larger child up, then stop where the hole has no children.", Colors.LIGHT_GRAY);
        pause(1.0);
        El root = h.a[1];
        Hole hole = h.hole(1);
        mine.addAll(hole.parts());
        List<Animation> an = new ArrayList<>();
        h.hideAnim(an, root, false, d(0.6));
        an.add(new FadeIn(hole.shape, d(0.6)));
        hole.cell.fadeIn(an, 0, d(0.6));
        playAll(an);
        h.a[1] = null;
        pause(0.7);
        int[][] steps = {{2, 1}, {5, 2}};   // child index -> hole index it climbs into
        for (int[] st : steps) {
            int c = st[0], i = st[1];
            El ce = h.a[c];
            narr(ce.v + " is the larger child: it moves up.", Colors.ORANGE);
            List<Animation> mv = new ArrayList<>();
            h.moveAnim(mv, ce, i, 0, d(0.65));
            mv.add(new MoveTo(hole.shape, h.px(c), h.py(c), d(0.65)).setEasing(Easing.EASE_IN_OUT));
            hole.cell.moveTo(mv, h.ax(c), h.ay, 0, d(0.65));
            playAll(mv);
            h.a[i] = ce;
            h.a[c] = null;
            pause(0.6);
        }
        narr("The hole is at index 5 and has no children, so we stop and just remove it.", Colors.ORANGE);
        List<Animation> out = new ArrayList<>();
        out.add(new FadeOut(hole.shape, d(0.7)));
        hole.cell.fadeOut(out, d(0.7));
        playAll(out);
        // the empty place stays visible
        CircleMob gap = h.ghost(5);
        gap.setStrokeColor(Colors.withAlpha(Colors.RED, 0.95));
        RectMob gapCell = panel(h.ax(5), h.ay, h.cw - 8, h.ch, Colors.RED, 0.05);
        mine.add(gap);
        mine.add(gapCell);
        play(new FadeIn(gap, d(0.5)), new FadeIn(gapCell, d(0.5)));
        pause(0.8);
        narr("Index 5 is empty, but 3 and 1 still hang below it: the tree is no longer complete.", Colors.RED);
        verdict("✗  not a complete tree", Colors.RED, -190);
        pause(3.0);
        play(new FadeOut(t, d(0.3)));
    }

    // ── slide 9, second answer ───────────────────────────────────────

    private void attemptTwo() {
        TextMob t = title("Answer 2 (slide 9)", Colors.ORANGE);
        narr("Remove 39, move 30 up, but then move the smaller child, 6, up and put the last element, 1, there.", Colors.LIGHT_GRAY);
        pause(1.2);
        El root = h.a[1];
        Hole hole = h.hole(1);
        mine.addAll(hole.parts());
        List<Animation> an = new ArrayList<>();
        h.hideAnim(an, root, false, d(0.6));
        an.add(new FadeIn(hole.shape, d(0.6)));
        hole.cell.fadeIn(an, 0, d(0.6));
        playAll(an);
        h.a[1] = null;
        pause(0.6);
        El last = h.a[9];
        h.paint(last, NEW_C, d(0.4));
        int[][] steps = {{2, 1}, {4, 2}};
        for (int[] st : steps) {
            int c = st[0], i = st[1];
            El ce = h.a[c];
            narr(ce.v + " moves up" + (c == 4 ? " (the smaller child: that is the mistake)." : "."), c == 4 ? Colors.RED : Colors.ORANGE);
            List<Animation> mv = new ArrayList<>();
            h.moveAnim(mv, ce, i, 0, d(0.65));
            mv.add(new MoveTo(hole.shape, h.px(c), h.py(c), d(0.65)).setEasing(Easing.EASE_IN_OUT));
            hole.cell.moveTo(mv, h.ax(c), h.ay, 0, d(0.65));
            playAll(mv);
            h.a[i] = ce;
            h.a[c] = null;
            pause(0.6);
        }
        narr("The last element, 1, fills the hole at index 4.", Colors.PINK);
        List<Animation> fin = new ArrayList<>();
        h.moveAnim(fin, last, 4, 0, d(0.8));
        fin.add(new FadeOut(hole.shape, d(0.8)));
        hole.cell.fadeOut(fin, d(0.8));
        fin.add(new EraseLink(h.edge[9], d(0.6)));
        playAll(fin);
        h.a[9] = null;
        h.a[4] = last;
        pause(0.6);
        List<Animation> bad = new ArrayList<>();
        h.paintAnim(bad, h.a[2], Colors.RED, d(0.4));
        h.paintAnim(bad, h.a[5], Colors.RED, d(0.4));
        h.paintEdgeAnim(bad, 5, Colors.RED, d(0.4));
        playAll(bad);
        narr("29 is below 6, but 29 > 6: a child is larger than its parent. The heap order is broken.", Colors.RED);
        verdict("✗  heap order broken", Colors.RED, -190);
        pause(3.0);
        play(new FadeOut(t, d(0.3)));
    }

    // ── slide 10: the rule and the right answer ──────────────────────

    private void rightWay() {
        TextMob t = title("The rule (slide 10)", Colors.GREEN);
        RectMob panel = panel(610, -140, 620, 230, Colors.GREEN, 0.08);
        TextMob r1 = label("1  remove the root", 330, -220, 30, Colors.GOLD, true, true);
        TextMob r2 = label("2  percolate the hole down", 330, -165, 30, Colors.GOLD, true, true);
        TextMob r3 = label("3  finally replace it with", 330, -110, 30, Colors.PINK, true, true);
        TextMob r4 = label("    the last element", 330, -65, 30, Colors.PINK, true, true);
        play(new FadeIn(panel, d(0.5)), new FadeIn(r1, d(0.5)), new FadeIn(r2, d(0.7)), new FadeIn(r3, d(0.9)), new FadeIn(r4, d(0.9)));
        mine.add(panel);
        mine.add(r1);
        mine.add(r2);
        mine.add(r3);
        mine.add(r4);
        extras.add(panel);
        extras.add(r1);
        extras.add(r2);
        extras.add(r3);
        extras.add(r4);
        pause(1.6);
        heapDeleteMax(h, 0.9, true);
        pause(1.0);
        narr("The tree is complete and every parent is larger than its children.", Colors.GREEN);
        verdict("✓  a valid heap", Colors.GREEN, 130);
        pause(3.2);
    }
}
