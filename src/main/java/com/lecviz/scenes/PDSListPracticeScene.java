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
 * Standalone clip for slides 47-48 of the lists deck, the end of the lists lecture: practice problems and
 * learning outcomes, then the "made by" card.
 *
 *   Slide 47  the four problems one at a time; then each problem as a set-up picture with a question mark
 *             (a stack from two queues, a queue from two stacks, three stacks in one array): the problems
 *             are for the students, so they are posed and not solved
 *   Slide 48  the three outcomes one at a time; then List, Stack and Queue with the applications seen in this
 *             lecture, all implemented with pointers or arrays
 *   End card  "This video was made by Karthik & Tejaswi"
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListPracticeScene extends PDSListClipBase {

    private StrokeTextMob head;

    @Override
    public void construct() {
        practice();
        outcomes();
        madeBy();
    }

    // ── slide 47 ─────────────────────────────────────────────────────

    private void practice() {
        head = writeHeading("Practice problems");
        pause(0.4);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Implement a stack using two queues."));
        s.add(ln(1, "push/pop should be implemented using enqueue /"));
        s.add(ln(2, "dequeue."));
        s.add(ln(0, "Implement a queue using two stacks."));
        s.add(ln(0, "Implement three stacks using an array (without"));
        s.add(ln(3, "space wastage)."));
        s.add(ln(0, "Solve problems at the end of Chapter 3."));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(1.2);
        swipeAway(text);
        pause(0.4);

        List<MObject> mine = new ArrayList<>();
        mine.add(head);
        double[] cx = {-640, 0, 640};
        String[] title = {"A stack from two queues", "A queue from two stacks", "Three stacks in one array"};
        Color[] col = {Colors.TEAL, Colors.PINK, Colors.GOLD};
        for (int i = 0; i < 3; i++) {
            RectMob card = panel(cx[i], 0, 580, 600, col[i], 0.06);
            TextMob t = label(title[i], cx[i], -250, 32, col[i], false, true);
            play(new FadeIn(card, d(0.6)), new FadeIn(t, d(0.6)));
            mine.add(card);
            mine.add(t);
            if (i == 0) twoQueues(cx[i], mine);
            else if (i == 1) twoStacks(cx[i], mine);
            else threeStacks(cx[i], mine);
            pause(0.8);
        }
        List<MObject> ch3 = chip("Solve problems at the end of Chapter 3.", 0, 400, 900, 70, Colors.ORANGE, 36);
        List<Animation> a = new ArrayList<>();
        fade(a, ch3, d(0.8));
        playAll(a);
        mine.addAll(ch3);
        pause(3.0);
        fadeOutAll(d(1.2), mine);
        pause(0.4);
    }

    private void twoQueues(double cx, List<MObject> mine) {
        TextMob l1 = label("queue 1", cx, -170, 26, Colors.GRAY, false, true);
        TextMob l2 = label("queue 2", cx, 20, 26, Colors.GRAY, false, true);
        List<Animation> a = new ArrayList<>();
        a.add(new FadeIn(l1, d(0.5)));
        a.add(new FadeIn(l2, d(0.5)));
        for (int k = 0; k < 3; k++) {
            Cell c = tokCell(String.valueOf(k + 1), cx - 120 + k * 120, -100, 100, 70, Colors.TEAL, 34);
            c.fadeIn(a, 0.15 * k, d(0.5));
            mine.addAll(c.parts());
            RectMob g = panel(cx - 120 + k * 120, 90, 100, 70, Colors.WHITE, 0.04);
            g.setStrokeColor(Colors.withAlpha(Colors.WHITE, 0.25));
            a.add(new FadeInAt(g, 0.15 * k, d(0.5)));
            mine.add(g);
        }
        playAll(a);
        mine.add(l1);
        mine.add(l2);
        TextMob q = label("push, pop ?", cx, 205, 44, Colors.ORANGE, false, true);
        TextMob r = label("only enqueue and dequeue allowed", cx, 255, 24, Colors.LIGHT_GRAY, false, false);
        play(new FadeIn(q, d(0.6)), new FadeIn(r, d(0.6)));
        mine.add(q);
        mine.add(r);
    }

    private void twoStacks(double cx, List<MObject> mine) {
        VStack s1 = new VStack(cx - 120, 150, 100, 46, 4, "");
        VStack s2 = new VStack(cx + 120, 150, 100, 46, 4, "");
        s1.showBox();
        s2.showBox();
        String[] v = {"A", "B", "C"};
        for (String x : v) {
            Cell c = s1.push(x, Colors.PINK);
            mine.addAll(c.parts());
        }
        mine.add(s1.box);
        mine.add(s2.box);
        TextMob l1 = label("stack 1", cx - 120, 185, 26, Colors.GRAY, false, true);
        TextMob l2 = label("stack 2", cx + 120, 185, 26, Colors.GRAY, false, true);
        TextMob q = label("enqueue, dequeue ?", cx, 232, 40, Colors.ORANGE, false, true);
        TextMob r = label("only push and pop allowed", cx, 276, 24, Colors.LIGHT_GRAY, false, false);
        play(new FadeIn(l1, d(0.5)), new FadeIn(l2, d(0.5)), new FadeIn(q, d(0.6)), new FadeIn(r, d(0.6)));
        mine.addAll(List.of(l1, l2, q, r));
    }

    private void threeStacks(double cx, List<MObject> mine) {
        Color[] c = {Colors.TEAL, Colors.BLUE, Colors.PINK};
        int[] used = {2, 1, 3};
        List<Animation> a = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            int stk = i / 4, pos = i % 4;
            boolean on = pos < used[stk];
            Cell cell = tokCell(on ? String.valueOf(pos + 1) : "", cx + (i - 5.5) * 46, -90, 42, 48, c[stk], 22);
            if (!on) {
                cell.box.setFillColor(Colors.withAlpha(Colors.WHITE, 0.04));
                cell.box.setStrokeColor(Colors.withAlpha(Colors.RED, 0.45));
            }
            cell.fadeIn(a, 0.04 * i, d(0.4));
            mine.addAll(cell.parts());
        }
        TextMob l = label("one array, split into three fixed parts", cx, -170, 24, Colors.GRAY, false, true);
        a.add(new FadeIn(l, d(0.5)));
        playAll(a);
        mine.add(l);
        TextMob w = label("the empty cells are wasted", cx, -10, 28, Colors.RED, false, true);
        TextMob q = label("how can the three share it?", cx, 100, 36, Colors.ORANGE, false, true);
        TextMob r = label("without space wastage", cx, 150, 24, Colors.LIGHT_GRAY, false, false);
        play(new FadeIn(w, d(0.6)), new FadeIn(q, d(0.7)), new FadeIn(r, d(0.7)));
        mine.addAll(List.of(w, q, r));
    }

    // ── slide 48 ─────────────────────────────────────────────────────

    private void outcomes() {
        head = writeHeading("Learning Outcomes");
        pause(0.4);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Use List, Stack, Queue ADTs in applications."));
        s.add(ln(0, "Implement these ADTs using C/C++ with"));
        s.add(ln(3, "pointers or arrays."));
        s.add(ln(0, "Study various applications using these data"));
        s.add(ln(3, "structures."));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(1.2);
        swipeAway(text);
        pause(0.4);

        List<MObject> mine = new ArrayList<>();
        mine.add(head);
        double[] cx = {-600, 0, 600};
        String[] name = {"List", "Stack", "Queue"};
        Color[] col = {Colors.BLUE, Colors.TEAL, Colors.PINK};
        String[][] apps = {{"polynomials", "list reversal"}, {"balanced parentheses", "postfix evaluation", "infix to postfix"}, {"call center simulation"}};
        for (int i = 0; i < 3; i++) {
            RectMob card = panel(cx[i], 0, 520, 600, col[i], 0.07);
            TextMob nm = label(name[i], cx[i], -250, 44, col[i], false, true);
            List<Animation> a = new ArrayList<>();
            a.add(new FadeIn(card, d(0.6)));
            a.add(new FadeIn(nm, d(0.6)));
            // a small picture of the structure
            if (i == 0) {
                for (int k = 0; k < 3; k++) {
                    LNode n = node(String.valueOf(new int[]{4, 2, 7}[k]), cx[i] - 150 + k * 150, -120, col[i]);
                    if (k == 2) n.nullNext();
                    n.fadeIn(a, 0.15 * k, d(0.6));
                    mine.addAll(n.all);
                    if (k < 2) {
                        Link l = arrow(cx[i] - 150 + k * 150 + 22, -120, cx[i] - 150 + (k + 1) * 150 - 66, -120, Colors.LIGHT_GRAY, 3);
                        a.add(new DrawLinkAt(l, 0.2 + 0.15 * k, d(0.5)));
                        mine.add(l);
                    }
                }
            } else if (i == 1) {
                for (int k = 0; k < 3; k++) {
                    Cell c = tokCell(new String[]{"A", "B", "C"}[k], cx[i], -60 - k * 62, 220, 52, col[i], 30);
                    c.fadeIn(a, 0.15 * k, d(0.5));
                    mine.addAll(c.parts());
                }
            } else {
                for (int k = 0; k < 4; k++) {
                    Cell c = tokCell(String.valueOf(k + 1), cx[i] - 180 + k * 120, -120, 100, 64, col[i], 30);
                    c.fadeIn(a, 0.15 * k, d(0.5));
                    mine.addAll(c.parts());
                }
            }
            playAll(a);
            mine.add(card);
            mine.add(nm);
            // the applications seen in this lecture
            for (int k = 0; k < apps[i].length; k++) {
                List<MObject> ch = chip(apps[i][k], cx[i], 40 + 70 * k, 440, 56, col[i], 26);
                List<Animation> ca = new ArrayList<>();
                for (MObject m : ch) ca.add(new DropIn(m, 40, 0.15 * k, d(0.6)));
                playAll(ca);
                mine.addAll(ch);
            }
            pause(0.5);
        }
        List<MObject> impl = chip("implemented in C/C++ with pointers or arrays", 0, 400, 880, 70, Colors.ORANGE, 36);
        List<Animation> ia = new ArrayList<>();
        fade(ia, impl, d(0.8));
        playAll(ia);
        mine.addAll(impl);
        pause(3.2);
        fadeOutAll(d(1.2), mine);
        pause(0.4);
    }

    // ── the end card ─────────────────────────────────────────────────

    private void madeBy() {
        List<MObject> mine = new ArrayList<>();
        StrokeTextMob by = stroke("This video was made by", 0, -120, 38, Colors.LIGHT_GRAY, false);
        play(new Write(by, d(2.0)));
        mine.add(by);
        pause(0.2);
        double size = 104, gap = 34;
        double w1 = strokeW("Karthik", true, size), w2 = strokeW("&", true, size), w3 = strokeW("Tejaswi", true, size);
        double left = -(w1 + w2 + w3 + 2 * gap) / 2;
        StrokeTextMob n1 = stroke("Karthik", left + w1 / 2, 20, size, Colors.TEAL, true);
        StrokeTextMob amp = stroke("&", left + w1 + gap + w2 / 2, 20, size, Colors.WHITE, true);
        StrokeTextMob n2 = stroke("Tejaswi", left + w1 + w2 + 2 * gap + w3 / 2, 20, size, Colors.GOLD, true);
        play(new Write(n1, d(1.8)));
        play(new Write(amp, d(0.7)));
        play(new Write(n2, d(1.8)));
        mine.add(n1);
        mine.add(amp);
        mine.add(n2);
        // a short linked list underneath: the topic of this lecture
        Row row = new Row(new String[]{"4", "2", "7", "9"}, 0, 190, 190, NODE);
        row.nodes.get(3).nullNext();
        row.build(d(0.3), d(0.6));
        mine.addAll(row.parts());
        TextMob course = label("CS5013  ·  LecViz", 0, 320, 30, Colors.GRAY, false, false);
        play(new FadeIn(course, d(0.8)));
        mine.add(course);
        pause(3.2);
        fadeOutAll(d(1.4), mine);
        pause(0.4);
    }
}
