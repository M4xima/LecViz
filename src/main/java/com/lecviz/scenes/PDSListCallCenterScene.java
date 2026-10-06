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
 * Standalone clip for slides 40-41 of the lists deck: the call center and its data structures.
 *
 *   Slide 40  the points one at a time; then the story: users call, two operators answer (and show busy),
 *             the next users have to wait in a line, an operator becomes free, and the user who has waited
 *             longest is answered: a queue
 *   Slide 41  the five data structures one at a time; then a snapshot of the call center drawn with them:
 *             the user record (id, call time), the operator record (id), the queue of waiting users, the
 *             list of busy operators and the queue of free operators
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListCallCenterScene extends PDSListClipBase {

    private StrokeTextMob head;
    private StrokeTextMob cap;

    @Override
    public void construct() {
        story();
        structures();
    }

    private void caption(String text, Color c, double y) {
        StrokeTextMob n = stroke(text, 0, y, 38, c, false);
        if (cap != null) {
            play(new FadeOut(cap, d(0.3)));
            remove(cap);
        }
        play(new Write(n, d(Math.max(1.4, text.length() * 0.05))));
        cap = n;
    }

    private void dropCaption() {
        if (cap != null) {
            play(new FadeOut(cap, d(0.3)));
            remove(cap);
            cap = null;
        }
    }

    // ── slide 40 ─────────────────────────────────────────────────────

    private void story() {
        head = writeHeading("Call Center");
        pause(0.4);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Multiple users call a call-center."));
        s.add(ln(0, "Multiple operators answer the call."));
        s.add(ln(0, "Each call takes an unknown amount of time."));
        s.add(ln(0, "When all the operators are busy"));
        s.add(ln(1, "Calling users need to wait."));
        s.add(ln(0, "When an operator becomes available"));
        s.add(ln(1, "Which waiting user is answered?"));
        s.add(ln(0, "Can we use Queue ADT to implement this?"));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(1.2);
        swipeAway(text);
        pause(0.4);

        List<MObject> mine = new ArrayList<>();
        mine.add(head);
        // the two operators
        double[] oy = {-190, 30};
        RectMob[] card = new RectMob[2];
        TextMob[] status = new TextMob[2];
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            card[i] = panel(560, oy[i], 400, 160, Colors.TEAL, 0.12);
            TextMob nm = label("Operator " + (i + 1), 560, oy[i] - 48, 36, Colors.WHITE, false, true);
            status[i] = label("free", 560, oy[i] + 4, 34, Colors.GREEN, false, true);
            in.add(new FadeInAt(card[i], 0.2 * i, d(0.6)));
            in.add(new FadeInAt(nm, 0.2 * i, d(0.6)));
            in.add(new FadeInAt(status[i], 0.2 * i, d(0.6)));
            mine.add(card[i]);
            mine.add(nm);
            mine.add(status[i]);
        }
        playAll(in);
        caption("Multiple users call a call-center; multiple operators answer.", Colors.WHITE, -385);

        Cell[] user = new Cell[5];
        Color[] uc = {Colors.BLUE, Colors.PINK, Colors.GOLD, Colors.PURPLE, Colors.ORANGE};
        for (int i = 0; i < 5; i++) user[i] = tokCell("U" + (i + 1), -780, -60, 110, 64, uc[i], 34);
        // U1 and U2 are answered at once
        for (int i = 0; i < 2; i++) {
            List<Animation> a = new ArrayList<>();
            user[i].fadeIn(a, 0, d(0.5));
            TextMob ring = label("ring ring", -780, -120, 26, Colors.GOLD, false, true);
            a.add(new FadeIn(ring, d(0.5)));
            playAll(a);
            pause(0.4);
            List<Animation> mv = new ArrayList<>();
            user[i].moveTo(mv, 300, oy[i], 60, d(1.0));
            mv.add(new FadeOut(ring, d(0.5)));
            playAll(mv);
            status[i].setText("busy");
            status[i].setFillColor(Colors.RED);
            play(new ColorChange(card[i], Colors.withAlpha(Colors.RED, 0.14), d(0.4)));
            mine.addAll(user[i].parts());
            pause(0.3);
        }
        TextMob unk1 = label("call time: ?", 560, oy[0] + 50, 26, Colors.GRAY, false, false);
        TextMob unk2 = label("call time: ?", 560, oy[1] + 50, 26, Colors.GRAY, false, false);
        play(new FadeIn(unk1, d(0.5)), new FadeIn(unk2, d(0.5)));
        mine.add(unk1);
        mine.add(unk2);
        caption("Each call takes an unknown amount of time.", Colors.WHITE, -385);
        pause(1.4);

        // every operator is busy: the next users have to wait
        caption("All the operators are busy: calling users need to wait.", Colors.ORANGE, -385);
        TextMob wl = label("waiting line", -380, 150, 30, Colors.GRAY, false, true);
        play(new FadeIn(wl, d(0.5)));
        mine.add(wl);
        Cell[] wait = new Cell[3];
        for (int k = 0; k < 3; k++) {
            Cell u = user[k + 2];
            List<Animation> a = new ArrayList<>();
            u.fadeIn(a, 0, d(0.5));
            playAll(a);
            pause(0.2);
            List<Animation> mv = new ArrayList<>();
            u.moveTo(mv, -640 + k * 150, 250, 50, d(0.9));
            playAll(mv);
            mine.addAll(u.parts());
            wait[k] = u;
        }
        pause(1.2);

        // O1 becomes free: who is answered?
        caption("An operator becomes available: which waiting user is answered?", Colors.WHITE, -385);
        List<Animation> fr = new ArrayList<>();
        user[0].fadeOut(fr, d(0.6));
        playAll(fr);
        status[0].setText("free");
        status[0].setFillColor(Colors.GREEN);
        play(new ColorChange(card[0], Colors.withAlpha(Colors.TEAL, 0.12), d(0.4)));
        TextMob q1 = label("?", -520, 100, 80, Colors.ORANGE, false, true);
        play(new FadeIn(q1, d(0.5)));
        mine.add(q1);
        for (int k = 0; k < 3; k++) {
            List<Animation> hi = new ArrayList<>();
            wait[k].color(hi, Colors.ORANGE, d(0.3));
            playAll(hi);
            List<Animation> lo = new ArrayList<>();
            wait[k].color(lo, uc[k + 2], d(0.3));
            playAll(lo);
        }
        play(new FadeOut(q1, d(0.4)));
        caption("The one who has waited longest: first come, first served.", Colors.GREEN, -385);
        List<Animation> pick = new ArrayList<>();
        wait[0].color(pick, Colors.GREEN, d(0.4));
        playAll(pick);
        List<Animation> go = new ArrayList<>();
        wait[0].moveTo(go, 300, oy[0], 60, d(1.0));
        wait[1].moveTo(go, -640, 250, 0, d(0.8));
        wait[2].moveTo(go, -490, 250, 0, d(0.8));
        playAll(go);
        status[0].setText("busy");
        status[0].setFillColor(Colors.RED);
        play(new ColorChange(card[0], Colors.withAlpha(Colors.RED, 0.14), d(0.4)));
        pause(1.0);
        List<MObject> ans = chip("Can we use the Queue ADT?  Yes: a queue of waiting users.", 0, 400, 1000, 70, Colors.GREEN, 36);
        List<Animation> aa = new ArrayList<>();
        fade(aa, ans, d(0.7));
        playAll(aa);
        mine.addAll(ans);
        pause(3.0);
        dropCaption();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(0.6), head);
        pause(0.4);
    }

    // ── slide 41 ─────────────────────────────────────────────────────

    private void structures() {
        head = writeHeading("Call Center: Data Structures");
        pause(0.4);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "User (id, call time)"));
        s.add(ln(0, "Operator (id)"));
        s.add(ln(0, "Queue of waiting users"));
        s.add(ln(0, "List of busy operators"));
        s.add(ln(0, "Queue of free operators"));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(1.2);
        swipeAway(text);
        pause(0.4);

        List<MObject> mine = new ArrayList<>();
        mine.add(head);
        // the two records
        RectMob ur = panel(-380, -360, 600, 110, Colors.BLUE, 0.1);
        TextMob un = label("User", -600, -360, 36, Colors.BLUE, false, true);
        Cell uid = tokCell("id", -420, -360, 110, 60, Colors.BLUE, 30);
        Cell ut = tokCell("call time", -270, -360, 170, 60, Colors.BLUE, 30);
        RectMob or = panel(330, -360, 440, 110, Colors.PINK, 0.1);
        TextMob on = label("Operator", 170, -360, 36, Colors.PINK, false, true);
        Cell oid = tokCell("id", 400, -360, 110, 60, Colors.PINK, 30);
        List<Animation> r = new ArrayList<>();
        for (MObject m : new MObject[]{ur, un, or, on}) r.add(new FadeIn(m, d(0.6)));
        uid.fadeIn(r, 0.3, d(0.5));
        ut.fadeIn(r, 0.4, d(0.5));
        oid.fadeIn(r, 0.5, d(0.5));
        playAll(r);
        mine.addAll(List.of(ur, un, or, on));
        mine.addAll(uid.parts());
        mine.addAll(ut.parts());
        mine.addAll(oid.parts());
        pause(0.6);

        // the three containers, a snapshot of the call center
        String[] names = {"Queue of waiting users", "List of busy operators", "Queue of free operators"};
        Color[] cols = {Colors.TEAL, Colors.GOLD, Colors.GREEN};
        double[] by = {-170, 20, 210};
        for (int b = 0; b < 3; b++) {
            RectMob band = panel(60, by[b], 1700, 140, cols[b], 0.07);
            TextMob nm = label(names[b], -830, by[b] - 88, 28, cols[b], true, true);
            play(new FadeIn(band, d(0.5)), new FadeIn(nm, d(0.5)));
            mine.add(band);
            mine.add(nm);
            if (b == 0) {
                Cell u1 = tokCell("U4 · 1", -240, by[b], 150, 70, Colors.BLUE, 30);
                Cell u2 = tokCell("U5 · 4", -60, by[b], 150, 70, Colors.BLUE, 30);
                Link arr = arrow(-30 + 60, by[b], 130, by[b], Colors.LIGHT_GRAY, 3);
                TextMob front = label("front", -240, by[b] - 55, 24, Colors.GRAY, false, true);
                TextMob back = label("back", -60, by[b] - 55, 24, Colors.GRAY, false, true);
                List<Animation> a = new ArrayList<>();
                u1.fadeIn(a, 0, d(0.6));
                u2.fadeIn(a, 0.3, d(0.6));
                a.add(new FadeInAt(front, 0.5, d(0.4)));
                a.add(new FadeInAt(back, 0.5, d(0.4)));
                playAll(a);
                mine.addAll(u1.parts());
                mine.addAll(u2.parts());
                mine.add(front);
                mine.add(back);
            } else if (b == 1) {
                String[][] pairs = {{"O1", "U1", "3 left"}, {"O2", "U2", "2 left"}};
                for (int k = 0; k < 2; k++) {
                    double x = -330 + k * 480;
                    Cell o = tokCell(pairs[k][0], x, by[b], 90, 70, Colors.PINK, 30);
                    Cell u = tokCell(pairs[k][1], x + 190, by[b], 90, 70, Colors.BLUE, 30);
                    Link l = arrow(x + 50, by[b], x + 140, by[b], Colors.LIGHT_GRAY, 3);
                    TextMob rem = label(pairs[k][2], x + 330, by[b], 28, Colors.ORANGE, false, true);
                    List<Animation> a = new ArrayList<>();
                    o.fadeIn(a, 0, d(0.6));
                    u.fadeIn(a, 0.2, d(0.6));
                    a.add(new DrawLinkAt(l, 0.2, d(0.5)));
                    a.add(new FadeInAt(rem, 0.4, d(0.5)));
                    playAll(a);
                    mine.addAll(o.parts());
                    mine.addAll(u.parts());
                    mine.add(l);
                    mine.add(rem);
                }
            } else {
                Cell o = tokCell("O3", -240, by[b], 90, 70, Colors.PINK, 30);
                List<Animation> a = new ArrayList<>();
                o.fadeIn(a, 0, d(0.6));
                playAll(a);
                mine.addAll(o.parts());
            }
            pause(0.5);
        }
        StrokeTextMob c1 = stroke("Two kinds of records, three containers: all a call center needs.", 0, 400, 38, Colors.WHITE, false);
        play(new Write(c1, d(3.2)));
        mine.add(c1);
        pause(3.0);
        fadeOutAll(d(1.2), mine);
        pause(0.4);
    }
}
