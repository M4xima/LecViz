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
 * Standalone clip for slide 42 of the lists deck: the call center simulation.
 *
 * The slide's points one at a time; then the simulation runs time unit by time unit with two operators and
 * four users (U1 calls at t = 1 for 5 units, U2 at 2 for 2, U3 at 3 for 2, U4 at 4 for 1). In every time unit the
 * actions of the slide happen in order and are named on screen: the call time of engaged users reduces, a busy
 * operator becomes free, a waiting user is given a free operator, a new user arrives and is either assigned to a
 * free operator or has to wait; a time unit in which none of that happens says "nothing happens". The waiting
 * queue, the list of busy operators and the queue of free operators are drawn and change as the actions occur.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListSimScene extends PDSListClipBase {

    private static final double[] BUSY_Y = {-185, -85};
    private static final double FREE_Y = 100, WAIT_Y = -180;

    private StrokeTextMob head;
    private final List<MObject> stage = new ArrayList<>();

    private final class Op {
        final int id;
        Cell chip;
        Cell user;
        TextMob rem;
        int remaining;
        boolean busy;

        Op(int id) { this.id = id; }
    }

    private Op[] ops;
    private final List<Op> freeQ = new ArrayList<>();
    private final List<Cell> waitQ = new ArrayList<>();
    private final List<Integer> waitDur = new ArrayList<>();
    private TextMob timeLab;

    @Override
    public void construct() {
        head = writeHeading("Call Center: Simulation");
        pause(0.4);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Simulation is often based on time."));
        s.add(ln(0, "At each time unit, various actions occur."));
        s.add(ln(1, "A new user arrives."));
        s.add(ln(1, "A free operator needs to be assigned to a user."));
        s.add(ln(1, "No operator is free, so the user needs to wait."));
        s.add(ln(1, "A busy operator becomes free."));
        s.add(ln(1, "Nothing happens, call time of engaged users"));
        s.add(ln(2, "reduces."));
        s.add(ln(0, "Simulation ties these actions together logically."));
        s.add(ln(3, "Source: callcenter.cpp").kw("callcenter.cpp", Colors.MAROON));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(1.4);
        swipeAway(text);
        pause(0.4);
        stage.add(head);
        simulate();
        fadeOutAll(d(1.2), stage);
        pause(0.4);
    }

    // ── layout helpers ───────────────────────────────────────────────

    private double freeX(int k) { return 250 + k * 140; }

    private double waitX(int k) { return -650 + k * 190; }

    private void narrate(String text, Color c) { sayAt(text, c, 0, 300, 38); }

    private void pop(MObject m) {
        play(new ScaleTo(m, 1.3, d(0.18)));
        play(new ScaleTo(m, 1.0, d(0.22)));
    }

    private void setup() {
        timeLab = label("t = 0", -720, -390, 72, Colors.WHITE, false, true);
        TextMob l1 = label("Queue of waiting users", -420, -265, 30, Colors.TEAL, false, true);
        TextMob l2 = label("Busy operators (list)", 480, -265, 30, Colors.GOLD, false, true);
        TextMob l3 = label("Free operators (queue)", 480, 25, 30, Colors.GREEN, false, true);
        TextMob l4 = label("new user", -750, -85, 26, Colors.GRAY, false, true);
        RectMob b1 = panel(-420, WAIT_Y + 4, 720, 120, Colors.TEAL, 0.07);
        RectMob b2 = panel(480, -135, 640, 230, Colors.GOLD, 0.07);
        RectMob b3 = panel(480, FREE_Y, 640, 120, Colors.GREEN, 0.07);
        List<Animation> a = new ArrayList<>();
        for (MObject m : new MObject[]{timeLab, l1, l2, l3, l4, b1, b2, b3}) a.add(new FadeIn(m, d(0.6)));
        ops = new Op[]{new Op(1), new Op(2)};
        for (int i = 0; i < 2; i++) {
            ops[i].chip = tokCell("O" + (i + 1), freeX(i), FREE_Y, 110, 76, Colors.PINK, 36);
            ops[i].chip.fadeIn(a, 0.3 + 0.1 * i, d(0.6));
            freeQ.add(ops[i]);
            stage.addAll(ops[i].chip.parts());
        }
        playAll(a);
        stage.addAll(List.of(timeLab, l1, l2, l3, l4, b1, b2, b3));
    }

    private void rearrangeFree() {
        List<Animation> a = new ArrayList<>();
        for (int k = 0; k < freeQ.size(); k++) freeQ.get(k).chip.moveTo(a, freeX(k), FREE_Y, 0, d(0.5));
        playAll(a);
    }

    private void rearrangeWait() {
        List<Animation> a = new ArrayList<>();
        for (int k = 0; k < waitQ.size(); k++) waitQ.get(k).moveTo(a, waitX(k), WAIT_Y, 0, d(0.5));
        playAll(a);
    }

    private void assign(Op o, Cell user, int dur, boolean fromWait) {
        freeQ.remove(o);
        List<Animation> a = new ArrayList<>();
        o.chip.moveTo(a, 250, BUSY_Y[o.id - 1], 40, d(0.9));
        user.moveTo(a, 410, BUSY_Y[o.id - 1], 60, d(0.9));
        user.text.setText(user.text.getText().split(" ")[0]);
        o.user = user;
        o.remaining = dur;
        o.busy = true;
        o.rem = label(dur + " left", 590, BUSY_Y[o.id - 1], 36, Colors.ORANGE, false, true);
        a.add(new FadeInAt(o.rem, 0.5, d(0.5)));
        stage.add(o.rem);
        playAll(a);
        if (fromWait) {
            waitQ.remove(user);
            waitDur.remove(0);
            rearrangeWait();
        }
        rearrangeFree();
    }

    private void release(Op o) {
        List<Animation> a = new ArrayList<>();
        o.user.fadeOut(a, d(0.6));
        a.add(new FadeOut(o.rem, d(0.6)));
        o.chip.moveTo(a, freeX(freeQ.size()), FREE_Y, 40, d(0.9));
        playAll(a);
        o.busy = false;
        o.user = null;
        freeQ.add(o);
    }

    private void simulate() {
        setup();
        int[][] arrivals = {{1, 1, 5}, {2, 2, 2}, {3, 3, 2}, {4, 4, 1}};    // {time, user id, call time}
        Color[] uc = {Colors.BLUE, Colors.PINK, Colors.GOLD, Colors.PURPLE};
        pause(0.8);
        for (int t = 1; t <= 7; t++) {
            timeLab.setText("t = " + t);
            pop(timeLab);
            boolean did = false;

            // the call time of engaged users reduces; a busy operator whose call is over becomes free
            List<Op> done = new ArrayList<>();
            boolean anyBusy = false;
            for (Op o : ops) if (o.busy) anyBusy = true;
            if (anyBusy) {
                for (Op o : ops) {
                    if (!o.busy) continue;
                    o.remaining--;
                    o.rem.setText(o.remaining + " left");
                    pop(o.rem);
                    if (o.remaining == 0) done.add(o);
                }
                pause(0.9);
            }
            for (Op o : done) {
                narrate("A busy operator becomes free: O" + o.id, Colors.ORANGE);
                release(o);
                did = true;
                pause(1.0);
            }

            // waiting users are given free operators
            while (!waitQ.isEmpty() && !freeQ.isEmpty()) {
                Op o = freeQ.get(0);
                Cell u = waitQ.get(0);
                narrate("A free operator is assigned to the user waiting longest: O" + o.id + " takes " + u.text.getText().split(" ")[0], Colors.GREEN);
                assign(o, u, waitDur.get(0), true);
                did = true;
                pause(1.0);
            }

            // a new user arrives
            for (int[] ar : arrivals) {
                if (ar[0] != t) continue;
                Cell u = tokCell("U" + ar[1] + " · " + ar[2], -750, -20, 170, 76, uc[ar[1] - 1], 32);
                narrate("A new user arrives: U" + ar[1] + ", call time " + ar[2], Colors.BLUE);
                List<Animation> a = new ArrayList<>();
                u.fadeIn(a, 0, d(0.6));
                playAll(a);
                stage.addAll(u.parts());
                pause(1.0);
                if (!freeQ.isEmpty()) {
                    Op o = freeQ.get(0);
                    narrate("A free operator needs to be assigned to the user: O" + o.id, Colors.GREEN);
                    assign(o, u, ar[2], false);
                } else {
                    narrate("No operator is free, so the user needs to wait", Colors.ORANGE);
                    List<Animation> mv = new ArrayList<>();
                    u.moveTo(mv, waitX(waitQ.size()), WAIT_Y, 50, d(0.9));
                    playAll(mv);
                    waitQ.add(u);
                    waitDur.add(ar[2]);
                }
                did = true;
                pause(1.0);
            }
            if (!did) {
                narrate("Nothing happens, call time of engaged users reduces", Colors.LIGHT_GRAY);
                pause(1.8);
            }
            pause(0.8);
        }
        narrate("Every call is over: all the operators are free again", Colors.GREEN);
        pause(1.6);
        StrokeTextMob c = stroke("Simulation ties these actions together logically.", 0, 400, 40, Colors.WHITE, false);
        play(new Write(c, d(2.8)));
        stage.add(c);
        pause(3.0);
        unsay();
    }
}
