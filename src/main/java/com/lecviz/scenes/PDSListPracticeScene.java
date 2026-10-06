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
 * Standalone clip for slide 47 of the lists deck, in the style of the arrays classwork: the four practice
 * problems appear one at a time (numbered badge, pen-stroke question, highlighted key words, a small picture),
 * then each of the first three gets a worked solution:
 *
 *   1  a stack from two queues: push enqueues into the empty queue, moves the other queue behind it and swaps
 *      the two names, so the front of q1 is always the top; pop is one dequeue
 *   2  a queue from two stacks: enqueue pushes on "in"; dequeue pops from "out", first pouring "in" into "out"
 *      (which reverses the order) when "out" is empty
 *   3  three stacks in one array without wasted space: a shared free list of slots, per-slot "next" links and
 *      one top per stack, so any stack can use any free slot
 *
 * The fourth problem (the exercises at the end of Chapter 3) is a pointer to the textbook, so it has no solution.
 * Slide 48 (outcomes and the "made by" card) is in PDSListOutcomesScene, which extends this class.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListPracticeScene extends PDSListClipBase {

    private StrokeTextMob head;

    @Override
    public void construct() {
        practice();
    }

    // ── slide 47: questions first, then a solution for each ──────────

    private StrokeTextMob ptitle;

    void practice() {
        ptitle = stroke("Lists: Practice problems", 0, -500, 50, Colors.WHITE, true);
        play(new Write(ptitle, d(2.0)));
        pause(0.6);
        overview();
        stackFromQueues();
        queueFromStacks();
        threeStacks();
        fadeOutAll(d(1.0), ptitle);
        pause(0.4);
    }

    private static Color valColor(String v) {
        return switch (v) {
            case "1" -> Colors.TEAL;
            case "2" -> Colors.BLUE;
            case "3" -> Colors.PINK;
            case "4" -> Colors.GOLD;
            default -> Colors.ORANGE;
        };
    }

    private static String nstr(int n) { return n < 0 ? "–" : String.valueOf(n); }

    private void ghost(Cell c) {
        c.box.setFillColor(Colors.withAlpha(Colors.WHITE, 0.04));
        c.box.setStrokeColor(Colors.withAlpha(Colors.WHITE, 0.25));
    }

    private void unGhost(List<Animation> into, Cell c, double dur) {
        into.add(new ColorChange(c.box, Colors.withAlpha(Colors.WHITE, 0.04), dur));
        into.add(new ColorChange(c.box, Colors.withAlpha(Colors.WHITE, 0.25), dur, ColorChange.Target.STROKE));
    }

    private void popText(MObject m) {
        play(new ScaleTo(m, 1.25, d(0.2)));
        play(new ScaleTo(m, 1.0, d(0.25)));
    }

    private RectMob keywordBand(String text, String keyword, double size, double left, double y, Color accent) {
        int idx = text.indexOf(keyword);
        double pre = strokeW(text.substring(0, idx), false, size);
        double kw = strokeW(text.substring(0, idx + keyword.length()), false, size) - pre;
        RectMob b = new RectMob(kw + 14, size * 1.25).setCornerRadius(7);
        b.setFillColor(Colors.withAlpha(accent, 0.30));
        b.setStrokeColor(Color.TRANSPARENT);
        b.setOpacity(0);
        b.setPosition(left + pre + kw / 2, y);
        add(b);
        return b;
    }

    private Animation bandGrow(RectMob b, double dur) {
        return new GrowRight(b, b.getPosition().x() - b.getWidth() / 2, b.getPosition().y(), b.getWidth(), b.getHeight(), dur);
    }

    /** One question: a numbered badge with a ripple, the question written in pen-stroke, a highlighter on its key words. */
    private List<MObject> card(int n, Color accent, String main, String keyword, double y, String[] subs) {
        List<MObject> made = new ArrayList<>();
        double left = -770;
        CircleMob badge = new CircleMob(25);
        badge.setFillColor(Colors.withAlpha(accent, 0.35));
        badge.setStrokeColor(accent);
        badge.setStrokeWidth(3);
        badge.setPosition(-830, y);
        badge.setOpacity(0);
        badge.setScale(0.2);
        add(badge);
        CircleMob ripple = new CircleMob(25);
        ripple.setFillColor(Color.TRANSPARENT);
        ripple.setStrokeColor(accent);
        ripple.setStrokeWidth(3);
        ripple.setPosition(-830, y);
        ripple.setOpacity(0);
        add(ripple);
        TextMob num = label(String.valueOf(n), -830, y, 30, accent, false, true);
        play(new FadeIn(badge, d(0.4)), new ScaleTo(badge, 1.0, d(0.5)).setEasing(com.lecviz.utils.Easing.EASE_OUT), new FadeIn(num, d(0.4)));
        ripple.setOpacity(1);
        StrokeTextMob t = strokeLeft(main, left, y, 32, Colors.WHITE);
        play(new Write(t, d(2.6)), new ScaleTo(ripple, 2.6, d(0.9)).setEasing(com.lecviz.utils.Easing.EASE_OUT), new FadeOut(ripple, d(0.9)));
        remove(ripple);
        RectMob band = keywordBand(main, keyword, 32, left, y, accent);
        play(bandGrow(band, d(0.7)));
        made.add(badge);
        made.add(num);
        made.add(t);
        made.add(band);
        pause(0.4);
        List<Animation> in = new ArrayList<>();
        for (int k = 0; k < subs.length; k++) {
            TextMob sub = label(subs[k], left + 40, y + 52 + 46 * k, 26, Colors.LIGHT_GRAY, true, false);
            in.add(new FadeInAt(sub, 0.6 * k, d(0.7)));
            made.add(sub);
        }
        playAll(in);
        pause(0.5);
        return made;
    }

    private RectMob sq(double x, double y, double size, Color c, List<MObject> made) {
        RectMob b = new RectMob(size, size).setCornerRadius(5);
        b.setFillColor(Colors.withAlpha(c, 0.45));
        b.setStrokeColor(c);
        b.setPosition(x, y);
        b.setOpacity(0);
        add(b);
        made.add(b);
        return b;
    }

    /** A small picture at the right edge of a question card. */
    private List<MObject> icon(int which, double cx, double cy, Color accent) {
        List<MObject> made = new ArrayList<>();
        List<Animation> in = new ArrayList<>();
        switch (which) {
            case 1 -> {
                for (int r = 0; r < 2; r++)
                    for (int i = 0; i < 4; i++)
                        in.add(new FadeInAt(sq(cx - 60 + 40 * i, cy + (r == 0 ? -24 : 24), 28, r == 0 ? Colors.TEAL : Colors.PINK, made), 0.08 * i + 0.2 * r, d(0.5)));
            }
            case 2 -> {
                for (int s = 0; s < 2; s++)
                    for (int i = 0; i < (s == 0 ? 3 : 2); i++)
                        in.add(new FadeInAt(sq(cx - 36 + 72 * s, cy + 34 - 32 * i, 28, Colors.PINK, made), 0.1 * i + 0.2 * s, d(0.5)));
            }
            case 3 -> {
                Color[] cc = {Colors.TEAL, Colors.PINK, Colors.GOLD};
                for (int i = 0; i < 9; i++)
                    in.add(new FadeInAt(sq(cx - 112 + 28 * i, cy, 24, cc[i % 3], made), 0.07 * i, d(0.5)));
            }
            default -> {
                RectMob page = panel(cx, cy, 70, 84, accent, 0.2);
                made.add(page);
                in.add(new FadeIn(page, d(0.6)));
                for (int i = 0; i < 3; i++) {
                    RectMob ln = new RectMob(40, 5).setCornerRadius(2);
                    ln.setFillColor(Colors.withAlpha(accent, 0.8));
                    ln.setStrokeColor(Color.TRANSPARENT);
                    ln.setPosition(cx, cy - 20 + 20 * i);
                    ln.setOpacity(0);
                    add(ln);
                    made.add(ln);
                    in.add(new FadeInAt(ln, 0.2 + 0.15 * i, d(0.5)));
                }
            }
        }
        playAll(in);
        return made;
    }

    /** The numbered header of one solution, as in the arrays classwork. */
    private List<MObject> header(int n, String text, Color accent) {
        double w = strokeW(text, true, 30);
        double bx = -w / 2 - 46;
        CircleMob badge = new CircleMob(24);
        badge.setFillColor(Colors.withAlpha(accent, 0.35));
        badge.setStrokeColor(accent);
        badge.setStrokeWidth(3);
        badge.setPosition(bx, -430);
        badge.setOpacity(0);
        badge.setScale(0.2);
        add(badge);
        TextMob num = label(String.valueOf(n), bx, -430, 28, accent, false, true);
        StrokeTextMob t = stroke(text, 0, -430, 30, Colors.WHITE, true);
        play(new FadeIn(badge, d(0.4)), new ScaleTo(badge, 1.0, d(0.5)).setEasing(com.lecviz.utils.Easing.EASE_OUT),
                new FadeIn(num, d(0.4)), new Write(t, d(1.8)));
        List<MObject> l = new ArrayList<>();
        l.add(badge);
        l.add(num);
        l.add(t);
        return l;
    }

    // all four questions, one at a time
    private void overview() {
        List<MObject> page = new ArrayList<>();
        page.addAll(card(1, Colors.TEAL, "Implement a stack using two queues.", "stack", -330,
                new String[]{"–  push/pop should be implemented using enqueue / dequeue."}));
        page.addAll(icon(1, 790, -330, Colors.TEAL));
        pause(0.6);
        page.addAll(card(2, Colors.PINK, "Implement a queue using two stacks.", "queue", -130, new String[0]));
        page.addAll(icon(2, 790, -130, Colors.PINK));
        pause(0.6);
        page.addAll(card(3, Colors.GOLD, "Implement three stacks using an array (without space wastage).", "three stacks", 50, new String[0]));
        page.addAll(icon(3, 790, 50, Colors.GOLD));
        pause(0.6);
        page.addAll(card(4, Colors.ORANGE, "Solve problems at the end of Chapter 3.", "Chapter 3", 230, new String[0]));
        page.addAll(icon(4, 790, 230, Colors.ORANGE));
        pause(2.4);
        fadeOutAll(d(0.8), page);
        pause(0.3);
    }

    // ── question 1: a stack from two queues ──────────────────────────

    private static double qx(int i) { return -300 + 112 * i; }

    private void stackFromQueues() {
        List<MObject> mine = new ArrayList<>(header(1, "Implement a stack using two queues", Colors.TEAL));
        CodeBox code = new CodeBox(new String[]{
                "push(x):",
                "  enqueue(q2, x)",
                "  while q1 is not empty:",
                "    enqueue(q2, dequeue(q1))",
                "  swap q1 and q2",
                "pop():",
                "  return dequeue(q1)"}, -930, -330, 24, 44);
        code.typeIn(4.0);
        mine.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.4)));

        double[] rowY = {-250, -50};
        List<Animation> in = new ArrayList<>();
        for (int r = 0; r < 2; r++) {
            TextMob nm = label("q" + (r + 1), -400, rowY[r], 34, r == 0 ? Colors.TEAL : Colors.PINK, false, true);
            in.add(new FadeIn(nm, d(0.5)));
            mine.add(nm);
            for (int i = 0; i < 6; i++) {
                RectMob g = panel(qx(i), rowY[r], 100, 70, Colors.WHITE, 0.04);
                g.setStrokeColor(Colors.withAlpha(Colors.WHITE, 0.25));
                in.add(new FadeInAt(g, 0.05 * i, d(0.4)));
                mine.add(g);
            }
        }
        TextMob fr = label("front", qx(0), rowY[0] - 58, 22, Colors.GRAY, false, true);
        TextMob sl = label("the stack the user sees", 700, 40, 28, Colors.GOLD, false, true);
        in.add(new FadeIn(fr, d(0.5)));
        in.add(new FadeIn(sl, d(0.5)));
        mine.add(fr);
        mine.add(sl);
        VStack st = new VStack(700, 340, 150, 52, 4, "top", -1);
        st.showBox();
        playAll(in);
        st.showTop();
        mine.addAll(st.parts());

        List<Cell> q1 = new ArrayList<>(), q2 = new ArrayList<>();
        for (String v : new String[]{"1", "2", "3"}) {
            Color c = valColor(v);
            List<MObject> call = chip("push(" + v + ")", 450, -250, 220, 56, c, 30);
            List<Animation> ca = new ArrayList<>();
            fade(ca, call, d(0.4));
            playAll(ca);
            mine.addAll(call);
            play(code.moveHl(0, d(0.3)));
            play(code.moveHl(1, d(0.4)));
            sayAt("enqueue the new element into the empty q2", Colors.LIGHT_GRAY, -100, 110, 28);
            Cell nc = tokCell(v, qx(0), rowY[1], 100, 70, c, 34);
            List<Animation> na = new ArrayList<>();
            nc.fadeIn(na, 0, d(0.5));
            playAll(na);
            mine.addAll(nc.parts());
            q2.add(nc);
            pause(0.5);
            if (!q1.isEmpty()) sayAt("move q1 behind it, one element at a time", Colors.LIGHT_GRAY, -100, 110, 28);
            while (!q1.isEmpty()) {
                play(code.moveHl(2, d(0.3)));
                play(code.moveHl(3, d(0.3)));
                Cell f = q1.remove(0);
                List<Animation> a = new ArrayList<>();
                f.moveTo(a, qx(q2.size()), rowY[1], 40, d(0.8));
                for (int i = 0; i < q1.size(); i++) q1.get(i).moveTo(a, qx(i), rowY[0], 0, d(0.8));
                playAll(a);
                q2.add(f);
            }
            play(code.moveHl(2, d(0.3)));
            sayAt("q1 is empty: swap the names q1 and q2", Colors.GREEN, -100, 110, 28);
            play(code.moveHl(4, d(0.4)));
            List<Animation> sw = new ArrayList<>();
            for (int i = 0; i < q2.size(); i++) q2.get(i).moveTo(sw, qx(i), rowY[0], 0, d(0.9));
            playAll(sw);
            q1.addAll(q2);
            q2.clear();
            Cell sc = st.push(v, c);
            mine.addAll(sc.parts());
            List<Animation> gone = new ArrayList<>();
            for (MObject m : call) gone.add(new FadeOut(m, d(0.4)));
            playAll(gone);
            pause(0.5);
        }
        for (int k = 0; k < 2; k++) {
            List<MObject> call = chip("pop()", 450, -250, 220, 56, Colors.PINK, 30);
            List<Animation> ca = new ArrayList<>();
            fade(ca, call, d(0.4));
            playAll(ca);
            mine.addAll(call);
            play(code.moveHl(5, d(0.3)));
            play(code.moveHl(6, d(0.4)));
            sayAt("dequeue(q1): the front of q1 is the top of the stack", Colors.LIGHT_GRAY, -100, 110, 28);
            Cell f = q1.remove(0);
            String v = f.text.getText();
            List<Animation> a = new ArrayList<>();
            f.moveTo(a, f.x, f.y - 90, 0, d(0.7));
            f.fadeOut(a, d(0.7));
            for (int i = 0; i < q1.size(); i++) q1.get(i).moveTo(a, qx(i), rowY[0], 0, d(0.8));
            playAll(a);
            List<MObject> ret = chip("returns " + v, 450, -170, 240, 56, Colors.GREEN, 28);
            List<Animation> ra = new ArrayList<>();
            fade(ra, ret, d(0.4));
            playAll(ra);
            mine.addAll(ret);
            Cell gone = st.popAway();
            mine.addAll(gone.parts());
            pause(0.7);
            List<Animation> off = new ArrayList<>();
            for (MObject m : call) off.add(new FadeOut(m, d(0.4)));
            for (MObject m : ret) off.add(new FadeOut(m, d(0.4)));
            playAll(off);
        }
        unsay();
        StrokeTextMob cl = stroke("push moves every element once: O(N).  pop is a single dequeue: O(1).", -100, 190, 34, Colors.ORANGE, false);
        play(new Write(cl, d(3.4)));
        mine.add(cl);
        pause(2.6);
        fadeOutAll(d(1.0), mine);
        pause(0.3);
    }

    // ── question 2: a queue from two stacks ──────────────────────────

    private static double ux(int i) { return -150 + 110 * i; }

    private void queueFromStacks() {
        List<MObject> mine = new ArrayList<>(header(2, "Implement a queue using two stacks", Colors.PINK));
        CodeBox code = new CodeBox(new String[]{
                "enqueue(x):",
                "  in.push(x)",
                "dequeue():",
                "  if out is empty:",
                "    while in is not empty:",
                "      out.push(in.pop())",
                "  return out.pop()"}, -930, -330, 24, 44);
        code.typeIn(4.0);
        mine.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.4)));

        VStack inS = new VStack(-130, 340, 130, 52, 4, "top", -1);
        VStack outS = new VStack(300, 340, 130, 52, 4, "top", 1);
        inS.showBox();
        outS.showBox();
        TextMob li = label("in", -130, 392, 34, Colors.TEAL, false, true);
        TextMob lo = label("out", 300, 392, 34, Colors.PINK, false, true);
        TextMob lq = label("the queue the user sees (front on the left)", 150, -345, 26, Colors.GOLD, false, true);
        play(new FadeIn(li, d(0.5)), new FadeIn(lo, d(0.5)), new FadeIn(lq, d(0.5)));
        inS.showTop();
        outS.showTop();
        mine.addAll(inS.parts());
        mine.addAll(outS.parts());
        mine.add(li);
        mine.add(lo);
        mine.add(lq);

        List<Cell> uq = new ArrayList<>();
        String[] ops = {"e1", "e2", "e3", "d", "d", "e4", "d", "d"};
        for (String op : ops) {
            if (op.startsWith("e")) {
                String v = op.substring(1);
                Color c = valColor(v);
                List<MObject> call = chip("enqueue(" + v + ")", 700, -250, 260, 56, c, 28);
                List<Animation> ca = new ArrayList<>();
                fade(ca, call, d(0.4));
                playAll(ca);
                mine.addAll(call);
                play(code.moveHl(0, d(0.3)));
                play(code.moveHl(1, d(0.4)));
                sayAt("enqueue is just a push onto the in-stack", Colors.LIGHT_GRAY, 100, 40, 28);
                Cell sc = inS.push(v, c);
                mine.addAll(sc.parts());
                Cell qc = tokCell(v, ux(uq.size()), -270, 90, 64, c, 32);
                List<Animation> qa = new ArrayList<>();
                qc.fadeIn(qa, 0, d(0.5));
                playAll(qa);
                mine.addAll(qc.parts());
                uq.add(qc);
                pause(0.5);
                List<Animation> gone = new ArrayList<>();
                for (MObject m : call) gone.add(new FadeOut(m, d(0.4)));
                playAll(gone);
            } else {
                List<MObject> call = chip("dequeue()", 700, -250, 260, 56, Colors.PINK, 28);
                List<Animation> ca = new ArrayList<>();
                fade(ca, call, d(0.4));
                playAll(ca);
                mine.addAll(call);
                play(code.moveHl(2, d(0.3)));
                play(code.moveHl(3, d(0.4)));
                if (outS.items.isEmpty()) {
                    sayAt("out is empty: pour everything from in into out", Colors.ORANGE, 100, 40, 28);
                    while (!inS.items.isEmpty()) {
                        play(code.moveHl(4, d(0.3)));
                        play(code.moveHl(5, d(0.3)));
                        Cell mv = inS.popAway();
                        String t = mv.text.getText();
                        Cell nc = outS.push(t, valColor(t));
                        mine.addAll(nc.parts());
                        pause(0.15);
                    }
                    play(code.moveHl(4, d(0.3)));
                    sayAt("the order is reversed: the oldest element is now on top", Colors.GREEN, 100, 40, 28);
                    pause(0.8);
                } else {
                    sayAt("out is not empty: its top is already the oldest element", Colors.GREEN, 100, 40, 28);
                    pause(0.6);
                }
                play(code.moveHl(6, d(0.4)));
                Cell r = outS.popAway();
                mine.addAll(r.parts());
                String v = r.text.getText();
                Cell uf = uq.remove(0);
                List<Animation> a = new ArrayList<>();
                uf.moveTo(a, uf.x, uf.y - 90, 0, d(0.7));
                uf.fadeOut(a, d(0.7));
                for (int i = 0; i < uq.size(); i++) uq.get(i).moveTo(a, ux(i), -270, 0, d(0.8));
                playAll(a);
                List<MObject> ret = chip("returns " + v, 700, -170, 260, 56, Colors.GREEN, 28);
                List<Animation> ra = new ArrayList<>();
                fade(ra, ret, d(0.4));
                playAll(ra);
                mine.addAll(ret);
                pause(0.8);
                List<Animation> off = new ArrayList<>();
                for (MObject m : call) off.add(new FadeOut(m, d(0.4)));
                for (MObject m : ret) off.add(new FadeOut(m, d(0.4)));
                playAll(off);
            }
        }
        unsay();
        StrokeTextMob c1 = stroke("Each element moves at most once from in to out:", 100, -170, 34, Colors.ORANGE, false);
        StrokeTextMob c2 = stroke("enqueue is O(1), and dequeue is O(1) on average.", 100, -115, 34, Colors.ORANGE, false);
        play(new Write(c1, d(2.6)));
        play(new Write(c2, d(2.6)));
        mine.add(c1);
        mine.add(c2);
        pause(2.6);
        fadeOutAll(d(1.0), mine);
        pause(0.3);
    }

    // ── question 3: three stacks in one array ────────────────────────

    private void threeStacks() {
        List<MObject> mine = new ArrayList<>(header(3, "Implement three stacks using an array (without space wastage)", Colors.GOLD));
        final int N = 9;
        int[] next = {1, 2, 3, 4, 5, 6, 7, 8, -1};
        int[] top = {-1, -1, -1};
        int[] free = {0};
        Color[] sc = {Colors.TEAL, Colors.PINK, Colors.GOLD};
        Cell[] vc = new Cell[N], nc = new Cell[N];
        List<Animation> in = new ArrayList<>();
        TextMob la = label("array", -740, -250, 28, Colors.GRAY, false, true);
        TextMob ln = label("next", -740, -110, 28, Colors.GRAY, false, true);
        in.add(new FadeIn(la, d(0.5)));
        in.add(new FadeIn(ln, d(0.5)));
        mine.add(la);
        mine.add(ln);
        for (int i = 0; i < N; i++) {
            double x = -560 + 140 * i;
            vc[i] = tokCell("", x, -250, 120, 90, Colors.WHITE, 40);
            ghost(vc[i]);
            nc[i] = tokCell(nstr(next[i]), x, -110, 100, 52, Colors.GRAY, 28);
            ghost(nc[i]);
            nc[i].text.setFillColor(Colors.LIGHT_GRAY);
            vc[i].fadeIn(in, 0.05 * i, d(0.5));
            nc[i].fadeIn(in, 0.05 * i + 0.2, d(0.5));
            TextMob idx = label(String.valueOf(i), x, -182, 22, Colors.GRAY, false, false);
            in.add(new FadeInAt(idx, 0.05 * i, d(0.4)));
            mine.addAll(vc[i].parts());
            mine.addAll(nc[i].parts());
            mine.add(idx);
        }
        List<MObject> freeChip = chip("free = 0", -600, 30, 250, 56, Colors.LIGHT_GRAY, 28);
        List<List<MObject>> topChip = new ArrayList<>();
        double[] tx = {-250, 100, 450};
        for (int k = 0; k < 3; k++) {
            List<MObject> ch = chip("top" + (k + 1) + " = –", tx[k], 30, 250, 56, sc[k], 28);
            topChip.add(ch);
            fade(in, ch, d(0.6));
            mine.addAll(ch);
        }
        fade(in, freeChip, d(0.6));
        mine.addAll(freeChip);
        playAll(in);
        TextMob freeT = (TextMob) freeChip.get(1);
        StrokeTextMob idea = stroke("One free list of slots, shared by all three stacks.", 0, 250, 34, Colors.WHITE, false);
        play(new Write(idea, d(2.8)));
        mine.add(idea);
        pause(1.4);
        play(new FadeOut(idea, d(0.5)));
        remove(idea);
        mine.remove(idea);

        Object[][] ops = {{"push", 0, "A"}, {"push", 1, "B"}, {"push", 0, "C"}, {"push", 2, "D"}, {"push", 0, "E"}, {"pop", 0, ""}, {"push", 2, "F"}};
        for (Object[] op : ops) {
            boolean isPush = op[0].equals("push");
            int k = (Integer) op[1];
            String v = (String) op[2];
            List<MObject> call = chip(isPush ? "push(stack " + (k + 1) + ", " + v + ")" : "pop(stack " + (k + 1) + ")", 0, 140, 420, 60, sc[k], 30);
            List<Animation> ca = new ArrayList<>();
            fade(ca, call, d(0.4));
            playAll(ca);
            mine.addAll(call);
            if (isPush) {
                int i = free[0];
                sayAt("take the first free slot: slot " + i, Colors.LIGHT_GRAY, 0, 250, 30);
                List<Animation> hi = new ArrayList<>();
                nc[i].color(hi, Colors.ORANGE, d(0.3));
                playAll(hi);
                popText(freeT);
                pause(0.3);
                free[0] = next[i];
                freeT.setText("free = " + nstr(free[0]));
                popText(freeT);
                sayAt(v + " goes into slot " + i + " and remembers the old top", Colors.LIGHT_GRAY, 0, 250, 30);
                vc[i].text.setText(v);
                vc[i].text.setFillColor(Colors.WHITE);
                List<Animation> st = new ArrayList<>();
                st.add(new FadeIn(vc[i].text, d(0.4)));
                vc[i].color(st, sc[k], d(0.4));
                playAll(st);
                next[i] = top[k];
                nc[i].text.setText(nstr(next[i]));
                nc[i].text.setFillColor(Colors.WHITE);
                List<Animation> nx = new ArrayList<>();
                nc[i].color(nx, sc[k], d(0.4));
                playAll(nx);
                popText(nc[i].text);
                top[k] = i;
                ((TextMob) topChip.get(k).get(1)).setText("top" + (k + 1) + " = " + i);
                popText(topChip.get(k).get(1));
            } else {
                int i = top[k];
                String got = vc[i].text.getText();
                sayAt("top" + (k + 1) + " = " + i + ": slot " + i + " holds the top element", Colors.LIGHT_GRAY, 0, 250, 30);
                List<Animation> hi = new ArrayList<>();
                vc[i].color(hi, Colors.RED, d(0.3));
                playAll(hi);
                pause(0.4);
                top[k] = next[i];
                ((TextMob) topChip.get(k).get(1)).setText("top" + (k + 1) + " = " + nstr(top[k]));
                popText(topChip.get(k).get(1));
                sayAt("its next is the new top; the slot goes back to the free list", Colors.LIGHT_GRAY, 0, 250, 30);
                List<Animation> clr = new ArrayList<>();
                clr.add(new FadeOut(vc[i].text, d(0.5)));
                unGhost(clr, vc[i], d(0.5));
                playAll(clr);
                vc[i].text.setText("");
                next[i] = free[0];
                nc[i].text.setText(nstr(next[i]));
                nc[i].text.setFillColor(Colors.LIGHT_GRAY);
                List<Animation> nx = new ArrayList<>();
                unGhost(nx, nc[i], d(0.4));
                playAll(nx);
                popText(nc[i].text);
                free[0] = i;
                freeT.setText("free = " + i);
                popText(freeT);
                List<MObject> ret = chip("returns " + got, 0, 208, 300, 56, Colors.GREEN, 28);
                List<Animation> ra = new ArrayList<>();
                fade(ra, ret, d(0.4));
                playAll(ra);
                mine.addAll(ret);
                pause(0.8);
                List<Animation> off = new ArrayList<>();
                for (MObject m : ret) off.add(new FadeOut(m, d(0.4)));
                playAll(off);
            }
            pause(0.5);
            List<Animation> gone = new ArrayList<>();
            for (MObject m : call) gone.add(new FadeOut(m, d(0.4)));
            playAll(gone);
        }
        unsay();
        StrokeTextMob c1 = stroke("Slot 4 held E of stack 1, and now holds F of stack 3:", 0, 270, 34, Colors.GREEN, false);
        StrokeTextMob c2 = stroke("any stack can use any free slot, so no space is wasted.", 0, 330, 34, Colors.GREEN, false);
        play(new Write(c1, d(2.8)));
        play(new Write(c2, d(2.8)));
        mine.add(c1);
        mine.add(c2);
        pause(3.0);
        fadeOutAll(d(1.2), mine);
        pause(0.4);
    }

    // ── slide 48 ─────────────────────────────────────────────────────

    void outcomes() {
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

    void madeBy() {
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
