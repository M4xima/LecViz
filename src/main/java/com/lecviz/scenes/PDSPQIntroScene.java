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
 * Standalone clip for slides 1-2 of the priority-queue deck: the title card and the applications.
 *
 *   Slide 1  "Priority Queues" is written over a line of waiting items that reorders itself so that the largest
 *            priority is first; then the author line, the e-mail and the date of the slide
 *   Slide 2  the six applications in the pen-stroke style, then the orange note of the slide; then the note is shown
 *            at work: a plain queue serves in arrival order, a priority queue serves the ambulance first
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSPQIntroScene extends PDSTreeClipBase {

    private final List<MObject> mine = new ArrayList<>();

    @Override
    public void construct() {
        titleCard();
        applications();
    }

    // ── slide 1 ──────────────────────────────────────────────────────

    private List<MObject> prChip(String text, double x, double y, double w, Color c) {
        return chip(text, x, y, w, 72, c, 32);
    }

    private void titleCard() {
        List<MObject> all = new ArrayList<>();
        StrokeTextMob title = stroke("Priority Queues", 0, -330, 130, Colors.WHITE, true);
        play(new Write(title, d(2.6)));
        all.add(title);
        pause(0.3);

        int[] pr = {3, 1, 5, 2, 4};
        double[] xs = {-440, -220, 0, 220, 440};
        List<List<MObject>> chips = new ArrayList<>();
        List<Animation> an = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<MObject> c = prChip(String.valueOf(pr[i]), xs[i], -110, 150, Colors.BLUE);
            chips.add(c);
            for (MObject m : c) an.add(new FadeInAt(m, 0.25 * i, d(0.5)));
            all.addAll(c);
        }
        playAll(an);
        pause(0.5);
        TextMob cap1 = label("they arrive like this", 0, -10, 28, Colors.GRAY, false, false);
        play(new FadeIn(cap1, d(0.5)));
        pause(1.2);
        // the largest priority comes to the front
        int[] order = {2, 4, 0, 3, 1};   // by priority 5 4 3 2 1
        List<Animation> mv = new ArrayList<>();
        for (int pos = 0; pos < 5; pos++) {
            List<MObject> c = chips.get(order[pos]);
            for (MObject m : c) mv.add(new ArcMove(m, xs[pos], -110, pos % 2 == 0 ? 45 : -45, d(1.3)));
        }
        List<Animation> col = new ArrayList<>();
        playAll(mv);
        TextMob cap2 = label("the largest priority is served first", 0, -10, 28, Colors.GOLD, false, true);
        play(new FadeOut(cap1, d(0.4)), new FadeIn(cap2, d(0.6)));
        List<MObject> front = chips.get(2);
        List<Animation> hl = new ArrayList<>();
        hl.add(new ColorChange((RectMob) front.get(0), Colors.withAlpha(Colors.GOLD, 0.4), d(0.5)));
        hl.add(new ColorChange((RectMob) front.get(0), Colors.GOLD, d(0.5), ColorChange.Target.STROKE));
        playAll(hl);
        all.add(cap1);
        all.add(cap2);
        pause(1.0);

        StrokeTextMob name = stroke("Rupesh Nasre.", 0, 150, 56, Colors.WHITE, false);
        play(new Write(name, d(2.0)));
        TextMob mail = label("rupesh@iitm.ac.in", 0, 220, 30, Colors.GRAY, false, false);
        TextMob date = label("July 2019", 0, 300, 30, Colors.GRAY, false, false);
        play(new FadeIn(mail, d(0.8)), new FadeIn(date, d(0.8)));
        all.add(name);
        all.add(mail);
        all.add(date);
        pause(2.4);
        fadeOutAll(1.2, all);
        pause(0.3);
    }

    // ── slide 2 ──────────────────────────────────────────────────────

    private void applications() {
        StrokeTextMob head = writeHeading("Applications");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Ambulance in traffic").kw("Ambulance", Colors.RED));
        s.add(ln(0, "Customer buying one chocolate amid others"));
        s.add(ln(3, "buying loads of grocery"));
        s.add(ln(0, "Processes in OS").kw("Processes", Colors.BLUE));
        s.add(ln(0, "Physically disabled person in lift / bus / aeroplane"));
        s.add(ln(0, "JEE admissions"));
        s.add(ln(0, "..."));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(0.6);
        RectMob note = panel(0, 270, 1100, 150, Colors.ORANGE, 0.22);
        TextMob n1 = label("If all priorities are the same, a queue suffices.", 0, 235, 36, Colors.WHITE, false, false);
        TextMob n2 = label("When priorities differ, we need a priority queue.", 0, 295, 36, Colors.WHITE, false, true);
        play(new FadeIn(note, d(0.6)), new FadeIn(n1, d(0.9)));
        play(new FadeIn(n2, d(0.9)));
        List<MObject> noteGroup = new ArrayList<>();
        noteGroup.add(note);
        noteGroup.add(n1);
        noteGroup.add(n2);
        text.add(noteGroup);
        pause(2.2);
        swipeAway(text);
        pause(0.4);

        // a queue and a priority queue at work
        double[] xs = {-500, -250, 0, 250, 500};
        TextMob qa = label("all priorities the same: a queue", -880, -330, 34, Colors.TEAL, true, true);
        play(new FadeIn(qa, d(0.5)));
        mine.add(qa);
        String[] pa = {"A", "B", "C", "D", "E"};
        List<List<MObject>> row1 = new ArrayList<>();
        List<Animation> an = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<MObject> c = prChip(pa[i], xs[i], -240, 150, Colors.TEAL);
            row1.add(c);
            for (MObject m : c) an.add(new FadeInAt(m, 0.25 * i, d(0.5)));
            mine.addAll(c);
        }
        playAll(an);
        pause(0.6);
        say("A queue serves in the order of arrival: first come, first served.", Colors.LIGHT_GRAY, 440, 32);
        List<Integer> alive1 = new ArrayList<>();
        for (int i = 0; i < 5; i++) alive1.add(i);
        for (int k = 0; k < 2; k++) {
            List<MObject> first = row1.get(alive1.remove(0));
            List<Animation> out = new ArrayList<>();
            for (MObject m : first) {
                out.add(new MoveTo(m, 850, -240, d(0.9)).setEasing(Easing.EASE_IN));
                out.add(new FadeOut(m, d(0.9)));
            }
            playAll(out);
            List<Animation> close = new ArrayList<>();
            for (int j = 0; j < alive1.size(); j++)
                for (MObject m : row1.get(alive1.get(j))) close.add(new MoveTo(m, xs[j], -240, d(0.7)).setEasing(Easing.EASE_IN_OUT));
            playAll(close);
            pause(0.5);
        }
        pause(0.8);

        TextMob qb = label("priorities differ: a priority queue", -880, -90, 34, Colors.ORANGE, true, true);
        play(new FadeIn(qb, d(0.5)));
        mine.add(qb);
        String[] names = {"chocolate", "groceries", "ambulance", "process", "lift"};
        int[] pri = {3, 1, 9, 5, 7};
        List<List<MObject>> row2 = new ArrayList<>();
        List<Animation> an2 = new ArrayList<>();
        double[] xs2 = {-500, -250, 0, 250, 500};
        for (int i = 0; i < 5; i++) {
            List<MObject> c = chip(names[i] + " " + pri[i], xs2[i], 10, 230, 72, Colors.BLUE, 28);
            row2.add(c);
            for (MObject m : c) an2.add(new FadeInAt(m, 0.25 * i, d(0.5)));
            mine.addAll(c);
        }
        playAll(an2);
        pause(0.6);
        say("A priority queue serves the largest priority first, whenever it arrived.", Colors.GOLD, 440, 32);
        pause(1.2);
        TextMob served = label("served:", -880, 200, 34, Colors.GOLD, true, true);
        play(new FadeIn(served, d(0.4)));
        mine.add(served);
        List<Integer> alive = new ArrayList<>();
        for (int i = 0; i < 5; i++) alive.add(i);
        double sx = -720;
        for (int k = 0; k < 3; k++) {
            int best = alive.get(0);
            for (int i : alive) if (pri[i] > pri[best]) best = i;
            List<MObject> c = row2.get(best);
            List<Animation> hl = new ArrayList<>();
            hl.add(new ColorChange((RectMob) c.get(0), Colors.withAlpha(Colors.GOLD, 0.4), d(0.4)));
            hl.add(new ColorChange((RectMob) c.get(0), Colors.GOLD, d(0.4), ColorChange.Target.STROKE));
            playAll(hl);
            pause(0.5);
            double tx = sx + 125 + k * 270;
            List<Animation> go = new ArrayList<>();
            for (MObject m : c) go.add(new MoveTo(m, tx, 200, d(0.9)).setEasing(Easing.EASE_IN_OUT));
            playAll(go);
            alive.remove(Integer.valueOf(best));
            // the others close the gap
            List<Animation> close = new ArrayList<>();
            for (int j = 0; j < alive.size(); j++)
                for (MObject m : row2.get(alive.get(j))) close.add(new MoveTo(m, xs2[j], 10, d(0.7)).setEasing(Easing.EASE_IN_OUT));
            playAll(close);
            pause(0.6);
        }
        say("Ambulance (9), then the lift (7), then the process (5): the order of priority, not of arrival.", Colors.GREEN, 440, 32);
        pause(3.6);
        unsay();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }
}
