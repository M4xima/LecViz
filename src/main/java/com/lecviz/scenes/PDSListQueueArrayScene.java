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
 * Standalone clip for slides 43-44 of the lists deck: queue implementation with an array, and wrap-around.
 *
 *   Slide 43  a queue in ten array cells with front (below) and back (above): insert 3, remove, then
 *             remove three more and insert 1, 2, 3, 4 -- the fourth insert would have to go past the last cell
 *             although the first cells are free again
 *   Slide 44  wrap-around: that insert goes to cell 0 (a curved arrow carries back around), five removes and
 *             an insert, a remove that carries front around to cell 0, and two more removes until front has
 *             passed back: the queue is empty
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListQueueArrayScene extends PDSListClipBase {

    private static final int N = 10;
    private static final double Y = 20, PITCH = 150;

    private StrokeTextMob head;
    private StrokeTextMob cap;
    private Cell[] cell;
    private Ptr front, back;
    private int f, b;
    private final List<MObject> stage = new ArrayList<>();
    private TextMob srcLab;
    private List<MObject> recall;

    private double slotX(int i) { return -675 + i * PITCH; }

    @Override
    public void construct() {
        head = writeHeading("Queue Implementation");
        stage.add(head);
        pause(0.4);
        setup();
        part43();
        part44();
        dropCaption();
        fadeOutAll(d(1.2), stage);
        pause(0.4);
    }

    private void caption(String text, Color c) {
        StrokeTextMob n = stroke(text, 0, -330, 40, c, false);
        if (cap != null) {
            play(new FadeOut(cap, d(0.3)));
            remove(cap);
        }
        play(new Write(n, d(Math.max(1.2, text.length() * 0.05))));
        cap = n;
    }

    private void dropCaption() {
        if (cap != null) {
            play(new FadeOut(cap, d(0.3)));
            remove(cap);
            cap = null;
        }
    }

    private void note(String text, Color c) { sayAt(text, c, 0, 330, 36); }

    // ── the array ────────────────────────────────────────────────────

    private void setup() {
        cell = new Cell[N];
        String[] vals = {"4", "2", "7", "2", "9", "5", null, null, null, null};
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            cell[i] = new Cell(vals[i] == null ? "" : vals[i], slotX(i), Y, 134, 100, Colors.TEAL, 44);
            if (vals[i] == null) {
                cell[i].box.setFillColor(Colors.withAlpha(Colors.WHITE, 0.04));
                cell[i].box.setStrokeColor(Colors.withAlpha(Colors.WHITE, 0.25));
            }
            cell[i].fadeIn(in, 0.07 * i, d(0.5));
            TextMob idx = label(String.valueOf(i), slotX(i), Y + 70, 24, Colors.GRAY, false, false);
            in.add(new FadeInAt(idx, 0.07 * i, d(0.4)));
            stage.addAll(cell[i].parts());
            stage.add(idx);
        }
        playAll(in);
        f = 0;
        b = 5;
        front = pointer("front", slotX(f), Y + 100, false, Colors.GOLD);
        back = pointer("back", slotX(b), Y - 58, true, Colors.PINK);
        play(new FadeIn(front.arrow, d(0.5)), new FadeIn(front.lab, d(0.5)), new FadeIn(back.arrow, d(0.5)), new FadeIn(back.lab, d(0.5)));
        stage.addAll(front.parts());
        stage.addAll(back.parts());
    }

    private void insert(String v) {
        b++;
        List<Animation> a = new ArrayList<>();
        back.go(a, slotX(b), d(0.6));
        playAll(a);
        if (b < N) {
            List<Animation> g = new ArrayList<>();
            cell[b].text.setText(v);
            cell[b].text.setFillColor(Colors.WHITE);
            g.add(new FadeIn(cell[b].text, d(0.4)));
            cell[b].color(g, Colors.GREEN, d(0.4));
            playAll(g);
            List<Animation> s = new ArrayList<>();
            cell[b].color(s, Colors.TEAL, d(0.4));
            playAll(s);
        }
    }

    private void kill(int i) {
        List<Animation> a = new ArrayList<>();
        a.add(new ColorChange(cell[i].text, Colors.withAlpha(Colors.WHITE, 0.2), d(0.4)));
        a.add(new ColorChange(cell[i].box, Colors.withAlpha(Colors.WHITE, 0.04), d(0.4)));
        a.add(new ColorChange(cell[i].box, Colors.withAlpha(Colors.WHITE, 0.25), d(0.4), ColorChange.Target.STROKE));
        playAll(a);
    }

    private void remove() {
        kill(f);
        f++;
        List<Animation> a = new ArrayList<>();
        front.go(a, slotX(f), d(0.6));
        playAll(a);
    }

    // ── slide 43 ─────────────────────────────────────────────────────

    private void part43() {
        caption("This time, we will use arrays.", Colors.WHITE);
        RectMob star = panel(690, -400, 330, 90, Colors.ORANGE, 0.16);
        TextMob s1 = label("Recall", 690, -418, 28, Colors.WHITE, false, true);
        TextMob s2 = label("circular list", 690, -382, 28, Colors.WHITE, false, true);
        play(new FadeIn(star, d(0.6)), new FadeIn(s1, d(0.6)), new FadeIn(s2, d(0.6)));
        stage.add(star);
        stage.add(s1);
        stage.add(s2);
        recall = List.of(star, s1, s2);
        srcLab = mono("qimpl.c", 780, -270, 30, Colors.MAROON);
        TextMob src = srcLab;
        play(new FadeIn(src, d(0.5)));
        stage.add(src);
        note("front: the first element     back: the last element", Colors.LIGHT_GRAY);
        pause(2.0);

        caption("Insert 3", Colors.WHITE);
        insert("3");
        pause(1.0);

        caption("Remove", Colors.WHITE);
        remove();
        pause(1.0);

        caption("Remove, Remove, Remove, Insert 1, 2, 3, 4", Colors.WHITE);
        for (int k = 0; k < 3; k++) {
            remove();
            pause(0.2);
        }
        pause(0.4);
        for (int k = 1; k <= 4; k++) {
            insert(String.valueOf(k));
            pause(0.2);
        }
        note("the 4 has no cell: back ran off the end, yet cells 0 to 3 are free", Colors.RED);
        pause(3.2);
    }

    // ── slide 44 ─────────────────────────────────────────────────────

    /** back (or front) leaves the right end and re-enters at the left end, along a curved arrow. */
    private void wrap(Ptr p, boolean above, int toIndex) {
        double y1 = above ? Y - 180 : Y + 205;
        double y0 = above ? Y - 150 : Y + 175;
        double xFrom = slotX(N);
        Link curve = new Link(new double[]{xFrom, xFrom, slotX(toIndex), slotX(toIndex)},
                new double[]{y0, y1, y1, y0}, Colors.ORANGE, 3.6, true);
        add(curve);
        stage.add(curve);
        play(new DrawLink(curve, d(1.2)));
        List<Animation> out = new ArrayList<>();
        out.add(new FadeOut(p.arrow, d(0.3)));
        out.add(new FadeOut(p.lab, d(0.3)));
        playAll(out);
        List<Animation> jump = new ArrayList<>();
        p.go(jump, slotX(toIndex), 0.01);
        playAll(jump);
        List<Animation> in = new ArrayList<>();
        in.add(new FadeIn(p.arrow, d(0.4)));
        in.add(new FadeIn(p.lab, d(0.4)));
        playAll(in);
        play(new FadeOut(curve, d(0.8)));
    }

    private void part44() {
        List<Animation> swap = new ArrayList<>();
        swap.add(new FadeOut(head, d(0.5)));
        for (MObject m : recall) swap.add(new FadeOut(m, d(0.5)));
        swap.add(new FadeOut(srcLab, d(0.5)));
        playAll(swap);
        StrokeTextMob h2 = stroke("Wrap-around", 0, -470, 54, Colors.WHITE, true);
        play(new Write(h2, d(1.2)));
        stage.add(h2);
        caption("Insert 4", Colors.WHITE);
        // back wraps from past the end to cell 0
        wrap(back, true, 0);
        b = 0;
        List<Animation> g = new ArrayList<>();
        cell[0].text.setText("4");
        cell[0].text.setFillColor(Colors.WHITE);
        g.add(new FadeIn(cell[0].text, d(0.4)));
        cell[0].color(g, Colors.GREEN, d(0.4));
        playAll(g);
        List<Animation> s = new ArrayList<>();
        cell[0].color(s, Colors.TEAL, d(0.4));
        playAll(s);
        note("index 10 becomes 0: the array is used as a circle", Colors.GREEN);
        pause(2.4);

        caption("Remove five elements, Insert 2", Colors.WHITE);
        for (int k = 0; k < 5; k++) {
            remove();
            pause(0.15);
        }
        insert("2");
        note("front moved on five cells, and the 2 went to cell 1", Colors.GREEN);
        pause(2.0);

        caption("Remove", Colors.WHITE);
        // removes the 3 in cell 9: front moves past the end and wraps to cell 0
        kill(f);
        f++;
        List<Animation> a = new ArrayList<>();
        front.go(a, slotX(f), d(0.6));
        playAll(a);
        wrap(front, false, 0);
        f = 0;
        note("front also wraps: after the last cell comes cell 0", Colors.GREEN);
        pause(2.0);

        caption("Remove", Colors.WHITE);
        remove();
        note("front moves on to cell 1", Colors.LIGHT_GRAY);
        pause(1.4);

        caption("Remove", Colors.WHITE);
        remove();
        note("front has passed back: the queue is empty", Colors.ORANGE);
        TextMob src2 = mono("qimpl2.c", 780, -270, 30, Colors.MAROON);
        play(new FadeIn(src2, d(0.5)));
        stage.add(src2);
        pause(3.2);
    }
}
