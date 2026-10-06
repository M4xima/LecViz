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
 * Standalone clip for slides 45-46 of the lists deck: queue conditions, and empty versus full.
 *
 *   Slide 45  the conditions one line at a time; then the two conventions run side by side on the same
 *             operations (insert 7, insert 3, remove, remove): front and back as in the previous slide
 *             (back = last element, empty when front > back, initially front = 0 and back = -1) and as in
 *             qimpl.c (back = where the next element goes, front = 0 and back = 0 at the start, empty when
 *             they meet); the classwork asks for the "full" condition
 *   Slide 46  a queue in ten cells: empty, then filled to the brim: front and back sit in exactly the same
 *             places in both states. The two solutions follow: leave one cell unused (N-1 elements) or track
 *             the size separately (qimpl.c); the classwork answer is shown with them
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListQueueFullScene extends PDSListClipBase {

    private StrokeTextMob head;
    private StrokeTextMob cap;
    private final List<MObject> stage = new ArrayList<>();

    @Override
    public void construct() {
        conditions();
        emptyVersusFull();
    }

    private void caption(String text, Color c, double y) {
        StrokeTextMob n = stroke(text, 0, y, 40, c, false);
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

    // ── a small array with front and back ────────────────────────────

    private final class Arr {
        final Cell[] cell;
        final double cx, y, pitch;
        final int n;
        Ptr front, back;
        int f, b;

        Arr(int n, double cx, double y, double pitch, double w, Color c) {
            this.n = n;
            this.cx = cx;
            this.y = y;
            this.pitch = pitch;
            cell = new Cell[n];
            List<Animation> in = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                cell[i] = new Cell("", x(i), y, w, w * 0.9, c, Math.min(40, w * 0.42));
                cell[i].box.setFillColor(Colors.withAlpha(Colors.WHITE, 0.04));
                cell[i].box.setStrokeColor(Colors.withAlpha(Colors.WHITE, 0.25));
                cell[i].fadeIn(in, 0.05 * i, d(0.4));
                TextMob idx = label(String.valueOf(i), x(i), y + w * 0.45 + 22, 22, Colors.GRAY, false, false);
                in.add(new FadeInAt(idx, 0.05 * i, d(0.3)));
                stage.addAll(cell[i].parts());
                stage.add(idx);
            }
            playAll(in);
        }

        double x(int i) { return cx + (i - (n - 1) / 2.0) * pitch; }

        void pointers(int f0, int b0) {
            f = f0;
            b = b0;
            front = pointer("front", x(f), y + 100, false, Colors.GOLD);
            back = pointer("back", x(b), y - 60, true, Colors.PINK);
            play(new FadeIn(front.arrow, d(0.5)), new FadeIn(front.lab, d(0.5)), new FadeIn(back.arrow, d(0.5)), new FadeIn(back.lab, d(0.5)));
            stage.addAll(front.parts());
            stage.addAll(back.parts());
        }

        void put(int i, String v, Color c, double sp) {
            cell[i].text.setText(v);
            cell[i].text.setFillColor(Colors.WHITE);
            List<Animation> a = new ArrayList<>();
            a.add(new FadeIn(cell[i].text, d(0.3 * sp)));
            cell[i].color(a, Colors.GREEN, d(0.3 * sp));
            playAll(a);
            List<Animation> s = new ArrayList<>();
            cell[i].color(s, c, d(0.3 * sp));
            playAll(s);
        }

        void kill(int i, double sp) {
            List<Animation> a = new ArrayList<>();
            a.add(new ColorChange(cell[i].text, Colors.withAlpha(Colors.WHITE, 0.2), d(0.3 * sp)));
            a.add(new ColorChange(cell[i].box, Colors.withAlpha(Colors.WHITE, 0.04), d(0.3 * sp)));
            a.add(new ColorChange(cell[i].box, Colors.withAlpha(Colors.WHITE, 0.25), d(0.3 * sp), ColorChange.Target.STROKE));
            playAll(a);
        }

        void moveBack(int to, double sp) {
            b = to;
            List<Animation> a = new ArrayList<>();
            back.go(a, x(b), d(0.5 * sp));
            playAll(a);
        }

        void moveFront(int to, double sp) {
            f = to;
            List<Animation> a = new ArrayList<>();
            front.go(a, x(f), d(0.5 * sp));
            playAll(a);
        }

        /** The pointer leaves one end and comes back at the other (no curve, just a quick re-entry). */
        void jump(Ptr p, double toX) {
            List<Animation> out = new ArrayList<>();
            out.add(new FadeOut(p.arrow, d(0.2)));
            out.add(new FadeOut(p.lab, d(0.2)));
            playAll(out);
            List<Animation> mv = new ArrayList<>();
            p.go(mv, toX, 0.01);
            playAll(mv);
            List<Animation> in = new ArrayList<>();
            in.add(new FadeIn(p.arrow, d(0.2)));
            in.add(new FadeIn(p.lab, d(0.2)));
            playAll(in);
        }
    }

    // ── slide 45 ─────────────────────────────────────────────────────

    private void conditions() {
        head = writeHeading("Queue Conditions");
        pause(0.4);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Queue is empty:"));
        s.add(ln(1, "when front > back (in previous slide)"));
        s.add(ln(1, "That is also initialization: front = 0, back = -1"));
        s.add(ln(1, "Our implementation qimpl.c uses front = 0, back = 0").kw("qimpl.c", Colors.MAROON));
        s.add(ln(0, "Whichever you use, follow invariants:"));
        s.add(ln(1, "qimpl.c: front points to the first element in the queue.").kw("qimpl.c", Colors.MAROON));
        s.add(ln(2, "back points to the place where next element should be inserted."));
        s.add(ln(1, "Previous slide: front points to the first element in the queue."));
        s.add(ln(2, "back points to the last element in the queue."));
        s.add(ln(0, "Classwork: Write conditions for when queue is full.").kw("Classwork", Colors.RED));
        List<List<MObject>> text = writeSlide(s, -340);
        pause(1.6);
        swipeAway(text);
        pause(0.4);
        stage.add(head);

        TextMob la = label("previous slide", -690, -170, 30, Colors.TEAL, false, true);
        TextMob na = label("back = last element", -690, -128, 24, Colors.LIGHT_GRAY, false, false);
        TextMob lb = label("qimpl.c", -690, 190, 30, Colors.PINK, false, true);
        TextMob nb = label("back = next free cell", -690, 232, 24, Colors.LIGHT_GRAY, false, false);
        play(new FadeIn(la, d(0.5)), new FadeIn(na, d(0.5)), new FadeIn(lb, d(0.5)), new FadeIn(nb, d(0.5)));
        stage.addAll(List.of(la, na, lb, nb));
        Arr A = new Arr(6, 100, -170, 116, 96, Colors.TEAL);
        Arr B = new Arr(6, 100, 190, 116, 96, Colors.PINK);
        // initialization
        caption("Initialization: the queue is empty", Colors.WHITE, -345);
        A.pointers(0, -1);
        B.pointers(0, 0);
        List<MObject> ea = chip("empty:  front > back", 720, -170, 400, 58, Colors.TEAL, 28);
        List<MObject> eb = chip("empty:  front == back", 720, 190, 400, 58, Colors.PINK, 28);
        List<Animation> ca = new ArrayList<>();
        fade(ca, ea, d(0.6));
        fade(ca, eb, d(0.6));
        playAll(ca);
        stage.addAll(ea);
        stage.addAll(eb);
        pause(1.8);

        caption("Insert 7", Colors.WHITE, -345);
        A.moveBack(0, 1.0);
        A.put(0, "7", Colors.TEAL, 1.0);
        B.put(0, "7", Colors.PINK, 1.0);
        B.moveBack(1, 1.0);
        pause(0.8);
        caption("Insert 3", Colors.WHITE, -345);
        A.moveBack(1, 1.0);
        A.put(1, "3", Colors.TEAL, 1.0);
        B.put(1, "3", Colors.PINK, 1.0);
        B.moveBack(2, 1.0);
        pause(0.8);
        caption("Remove", Colors.WHITE, -345);
        A.kill(0, 1.0);
        A.moveFront(1, 1.0);
        B.kill(0, 1.0);
        B.moveFront(1, 1.0);
        pause(0.8);
        caption("Remove: the queue is empty again", Colors.WHITE, -345);
        A.kill(1, 1.0);
        A.moveFront(2, 1.0);
        B.kill(1, 1.0);
        B.moveFront(2, 1.0);
        TextMob ra = label("front 2 > back 1", 720, -105, 28, Colors.TEAL, false, true);
        TextMob rb = label("front 2 == back 2", 720, 255, 28, Colors.PINK, false, true);
        play(new FadeIn(ra, d(0.5)), new FadeIn(rb, d(0.5)));
        stage.add(ra);
        stage.add(rb);
        pause(2.0);
        StrokeTextMob cw = stroke("Classwork: write the conditions for when the queue is full.", 0, 420, 40, Colors.RED, false);
        play(new Write(cw, d(3.0)));
        stage.add(cw);
        pause(2.6);
        dropCaption();
        fadeOutAll(d(1.2), stage);
        stage.clear();
        pause(0.3);
    }

    // ── slide 46 ─────────────────────────────────────────────────────

    private void emptyVersusFull() {
        head = writeHeading("Empty versus Full");
        pause(0.4);
        stage.add(head);
        Arr A = new Arr(10, 0, 0, 150, 130, Colors.TEAL);
        caption("Empty queue", Colors.WHITE, -300);
        A.pointers(4, 3);
        TextMob n1 = label("back is just before front", 0, 290, 36, Colors.LIGHT_GRAY, false, true);
        play(new FadeIn(n1, d(0.6)));
        stage.add(n1);
        pause(2.2);
        play(new FadeOut(n1, d(0.4)));

        caption("Full queue", Colors.WHITE, -300);
        // fill: 0 goes into cell 4 (the front), and the values run round the array
        int cellIdx = 4;
        for (int v = 0; v < 10; v++) {
            int c = (4 + v) % 10;
            int nb = c;
            if (v > 0 && c == 0) A.jump(A.back, A.x(0));
            else A.moveBack(nb, 0.5);
            A.b = nb;
            A.put(c, String.valueOf(v), Colors.TEAL, 0.5);
            pause(0.05);
            cellIdx = c;
        }
        TextMob n2 = label("full: back is again just before front", 0, 290, 36, Colors.ORANGE, false, true);
        play(new FadeIn(n2, d(0.6)));
        stage.add(n2);
        pause(1.2);
        StrokeTextMob amb = stroke("The same pointers for an empty and a full queue: ambiguous.", 0, 360, 40, Colors.RED, false);
        play(new Write(amb, d(3.0)));
        stage.add(amb);
        pause(2.8);
        List<Animation> clr = new ArrayList<>();
        clr.add(new FadeOut(n2, d(0.5)));
        clr.add(new FadeOut(amb, d(0.5)));
        playAll(clr);

        // solution 1: leave one cell unused
        caption("Possible solutions: leave one space unused (N-1 elements)", Colors.GREEN, -300);
        A.cell[3].text.setText("");
        List<Animation> gone = new ArrayList<>();
        A.kill(3, 0.8);
        A.moveBack(2, 0.8);
        List<MObject> full = chip("full:  (back + 2) % N == front", -330, 290, 560, 62, Colors.GREEN, 30);
        List<MObject> empty = chip("empty:  (back + 1) % N == front", 330, 290, 560, 62, Colors.TEAL, 30);
        List<Animation> ca = new ArrayList<>();
        fade(ca, full, d(0.7));
        fade(ca, empty, d(0.7));
        playAll(ca);
        stage.addAll(full);
        stage.addAll(empty);
        pause(3.0);
        List<Animation> off = new ArrayList<>();
        for (MObject m : full) off.add(new FadeOut(m, d(0.5)));
        for (MObject m : empty) off.add(new FadeOut(m, d(0.5)));
        playAll(off);

        // solution 2: track the size
        caption("Or track size separately (used in qimpl.c)", Colors.GREEN, -300);
        A.put(3, "9", Colors.TEAL, 0.8);
        A.moveBack(3, 0.8);
        List<MObject> sz = chip("size = 10", 0, 290, 300, 70, Colors.GOLD, 40);
        List<Animation> sa = new ArrayList<>();
        fade(sa, sz, d(0.6));
        playAll(sa);
        stage.addAll(sz);
        TextMob szText = (TextMob) sz.get(1);
        pause(1.0);
        // empty it again: the pointers end exactly where they were, but size says 0
        for (int k = 0; k < 10; k++) {
            int c = (4 + k) % 10;
            A.kill(c, 0.45);
            int nf = c + 1;
            if (nf == 10) A.jump(A.front, A.x(0));
            else A.moveFront(nf, 0.45);
            szText.setText("size = " + (9 - k));
            pause(0.05);
        }
        TextMob n3 = label("same pointers, but size tells them apart: 0 means empty, N means full", 0, 380, 32, Colors.GREEN, false, true);
        play(new FadeIn(n3, d(0.6)));
        stage.add(n3);
        pause(2.4);

        // the classwork answer
        List<MObject> ans = chip("Classwork: full  =  size == N   (or, with one space unused,  (back + 2) % N == front)", 0, 440, 1500, 70, Colors.RED, 30);
        List<Animation> aa = new ArrayList<>();
        fade(aa, ans, d(0.8));
        playAll(aa);
        stage.addAll(ans);
        pause(3.4);
        dropCaption();
        fadeOutAll(d(1.2), stage);
        stage.clear();
        pause(0.4);
    }
}
