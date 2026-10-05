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
 * Standalone clip for slides 5-6 of the lists deck: the List ADT built on an array.
 *
 *   Slide 5  the class and an array of five elements in eight slots; the design decisions come one
 *            line at a time, each with a small picture: the fixed size, a size counter versus a
 *            sentinel, an overflow, an underflow, the printing order and duplicates
 *   Slide 6  each operation runs on the array while a step counter counts what it costs: insert
 *            writes at the counter (1 step), find scans (N), remove shifts the rest (N), print
 *            walks the elements (N) and size just reads the counter (1)
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListArrayScene extends PDSListClipBase {

    private static final double CELL_Y = -285, CX = 380, PITCH = 100;

    private StrokeTextMob head;
    private final List<Tok> toks = new ArrayList<>();
    private TextMob stepsLab;
    private int steps;

    private double slotX(int i) { return CX + (i - 3.5) * PITCH; }

    /** One element standing in an array slot. */
    private final class Tok {
        final RectMob box;
        final TextMob text;
        int slot;

        Tok(String v, int slot) {
            this.slot = slot;
            box = panel(slotX(slot), CELL_Y, 94, 86, Colors.TEAL, 0.3);
            text = label(v, slotX(slot), CELL_Y, 40, Colors.WHITE, false, true);
        }

        List<MObject> parts() {
            List<MObject> l = new ArrayList<>();
            l.add(box);
            l.add(text);
            return l;
        }

        void toSlot(List<Animation> into, int s, double dur) {
            into.add(new MoveTo(box, slotX(s), CELL_Y, dur).setEasing(Easing.EASE_IN_OUT));
            into.add(new MoveTo(text, slotX(s), CELL_Y, dur).setEasing(Easing.EASE_IN_OUT));
            slot = s;
        }

        void color(List<Animation> into, Color c, double dur) {
            into.add(new ColorChange(box, Colors.withAlpha(c, 0.3), dur));
            into.add(new ColorChange(box, Colors.withAlpha(c, 0.85), dur, ColorChange.Target.STROKE));
        }
    }

    private void tick() {
        steps++;
        stepsLab.setText(String.valueOf(steps));
    }

    private void resetSteps() {
        steps = 0;
        stepsLab.setText("0");
    }

    @Override
    public void construct() {
        head = writeHeading("List using Array");
        pause(0.4);

        CodeBox code = new CodeBox(new String[]{
                "class List {",
                "public:",
                "  List();",
                "  void insert(Element e);",
                "  bool find(Element e);",
                "  void remove(Element e);",
                "  void print();",
                "  int size();",
                "};"}, -880, -330, 32, 56);
        code.typeIn(3.6);

        // the array: five elements in eight slots
        RectMob[] cell = new RectMob[8];
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            cell[i] = panel(slotX(i), CELL_Y, 98, 90, Colors.WHITE, 0.04);
            cell[i].setStrokeColor(Colors.withAlpha(Colors.WHITE, 0.2));
            in.add(new FadeInAt(cell[i], 0.06 * i, d(0.5)));
        }
        int[] vals = {4, 2, 7, 2, 9};
        for (int i = 0; i < 5; i++) {
            Tok t = new Tok(String.valueOf(vals[i]), i);
            toks.add(t);
            for (MObject m : t.parts()) in.add(new FadeInAt(m, 0.4 + 0.12 * i, d(0.5)));
        }
        playAll(in);
        pause(0.5);

        List<MObject> everything = new ArrayList<>(code.parts());
        everything.add(head);
        for (RectMob c : cell) everything.add(c);
        for (Tok t : toks) everything.addAll(t.parts());

        List<List<MObject>> bullets = new ArrayList<>();
        List<MObject> header = designDecisions(cell, bullets);
        everything.addAll(header);

        // slide 6: the same code and array, now with the cost of each operation
        StrokeTextMob h2 = stroke("With certain design decisions:", CX, -125, 38, Colors.MAROON, true);
        swipeAway(bullets);
        play(new FadeOut(header.get(0), d(0.5)));
        remove(header.get(0));
        play(new Write(h2, d(2.0)));
        everything.add(h2);
        pause(0.4);
        costs(code, cell, everything);

        fadeOutAll(d(1.2), everything);
        pause(0.4);
    }

    // ── slide 5: the design decisions ────────────────────────────────

    /** The decision lines, one at a time, each with a small picture. Returns the header (first) and leftovers. */
    private List<MObject> designDecisions(RectMob[] cell, List<List<MObject>> bullets) {
        List<MObject> keep = new ArrayList<>();
        StrokeTextMob hdr = stroke("Design decisions", -250 + strokeW("Design decisions", true, 38) / 2, -125, 38, Colors.MAROON, true);
        play(new Write(hdr, d(1.8)));
        keep.add(hdr);

        String[][] lines = {
                {"Size of the array?"},
                {"Maintain size separately or use a", "sentinel?"},
                {"On overflow: error or realloc?"},
                {"On underflow: error message or exit or", "silent?"},
                {"Printing order?"},
                {"Duplicates allowed?"},
                {"For duplicates, what does remove do?"},
                {"..."}};
        double y = -60;
        List<MObject> viz = new ArrayList<>();
        for (int k = 0; k < lines.length; k++) {
            List<MObject> group = new ArrayList<>();
            double ly = y;
            CircleMob dot = new CircleMob(8);
            dot.setFillColor(Colors.ORANGE);
            dot.setStrokeColor(Color.TRANSPARENT);
            dot.setPosition(-275, y);
            dot.setOpacity(0);
            add(dot);
            group.add(dot);
            List<Animation> start = new ArrayList<>();
            start.add(new FadeIn(dot, d(0.4)));
            for (int j = 0; j < lines[k].length; j++) {
                StrokeTextMob t = strokeLeft(lines[k][j], -250, y, 34, Colors.WHITE);
                start.add(new Write(t, d(Math.max(1.1, lines[k][j].length() * 0.05))));
                group.add(t);
                y += j + 1 < lines[k].length ? 44 : 0;
            }
            playAll(start);
            bullets.add(group);
            pause(0.2);

            // the picture for this decision
            int vizFrom = viz.size();
            switch (k) {
                case 0 -> {
                    Link br = new Link(new double[]{slotX(0) - 50, slotX(0) - 50, slotX(7) + 50, slotX(7) + 50},
                            new double[]{CELL_Y - 58, CELL_Y - 72, CELL_Y - 72, CELL_Y - 58}, Colors.GOLD, 3.2, false);
                    add(br);
                    TextMob lab = label("N = 8 slots, fixed", CX, CELL_Y - 98, 32, Colors.GOLD, false, true);
                    play(new DrawLink(br, d(0.8)), new FadeIn(lab, d(0.8)));
                    pause(1.2);
                    play(new FadeOut(br, d(0.5)), new FadeOut(lab, d(0.5)));
                    remove(br);
                    remove(lab);
                }
                case 1 -> {
                    List<MObject> sizeChip = chip("size = 5", 868, CELL_Y, 150, 56, Colors.GOLD, 26);
                    List<Animation> a = new ArrayList<>();
                    fade(a, sizeChip, d(0.6));
                    playAll(a);
                    pause(1.0);
                    List<Animation> sw = new ArrayList<>();
                    for (MObject m : sizeChip) sw.add(new FadeOut(m, d(0.4)));
                    playAll(sw);
                    TextMob sent = label("-1", slotX(5), CELL_Y, 34, Colors.RED, false, true);
                    TextMob sentLab = label("sentinel", slotX(5), CELL_Y + 66, 22, Colors.RED, false, true);
                    play(new FadeIn(sent, d(0.5)), new FadeIn(sentLab, d(0.5)));
                    pause(1.2);
                    play(new FadeOut(sent, d(0.4)), new FadeOut(sentLab, d(0.4)));
                    remove(sent);
                    remove(sentLab);
                    List<Animation> back = new ArrayList<>();
                    for (MObject m : sizeChip) back.add(new FadeIn(m, d(0.5)));
                    playAll(back);
                    viz.addAll(sizeChip);
                    keep.addAll(sizeChip);
                }
                case 2 -> {
                    List<Animation> a = new ArrayList<>();
                    for (int i = 0; i < 8; i++) {
                        a.add(new ColorChange(cell[i], Colors.withAlpha(Colors.ORANGE, 0.18), d(0.3)));
                        a.add(new ColorChange(cell[i], Colors.withAlpha(Colors.ORANGE, 0.9), d(0.3), ColorChange.Target.STROKE));
                    }
                    TextMob full = label("array full!", CX, CELL_Y - 98, 32, Colors.ORANGE, false, true);
                    a.add(new FadeIn(full, d(0.4)));
                    playAll(a);
                    List<MObject> c1 = chip("error", 400, ly, 150, 50, Colors.RED, 28);
                    List<MObject> c2 = chip("realloc", 580, ly, 170, 50, Colors.GREEN, 28);
                    TextMob note = label("bigger array + copy the old one", 620, ly + 52, 26, Colors.LIGHT_GRAY, false, false);
                    List<Animation> b = new ArrayList<>();
                    fade(b, c1, d(0.5));
                    fade(b, c2, d(0.5));
                    b.add(new FadeInAt(note, 0.5, d(0.5)));
                    playAll(b);
                    pause(1.4);
                    List<Animation> r = new ArrayList<>();
                    for (int i = 0; i < 8; i++) {
                        r.add(new ColorChange(cell[i], Colors.withAlpha(Colors.WHITE, 0.04), d(0.4)));
                        r.add(new ColorChange(cell[i], Colors.withAlpha(Colors.WHITE, 0.2), d(0.4), ColorChange.Target.STROKE));
                    }
                    r.add(new FadeOut(full, d(0.4)));
                    playAll(r);
                    remove(full);
                    viz.addAll(c1);
                    viz.addAll(c2);
                    viz.add(note);
                }
                case 3 -> {
                    List<MObject> c1 = chip("error message", 570, ly, 240, 50, Colors.RED, 26);
                    List<MObject> c2 = chip("exit", 740, ly, 100, 50, Colors.ORANGE, 26);
                    List<MObject> c3 = chip("silent", 870, ly, 130, 50, Colors.GRAY, 26);
                    TextMob empty = label("remove from an empty list", 710, ly + 52, 26, Colors.LIGHT_GRAY, false, false);
                    List<Animation> a = new ArrayList<>();
                    fade(a, c1, d(0.5));
                    fade(a, c2, d(0.7));
                    fade(a, c3, d(0.9));
                    a.add(new FadeIn(empty, d(0.8)));
                    playAll(a);
                    pause(1.6);
                    viz.addAll(c1);
                    viz.addAll(c2);
                    viz.addAll(c3);
                    viz.add(empty);
                }
                case 4 -> {
                    TextMob p1 = mono("4 2 7 2 9", 330, ly, 32, Colors.TEAL);
                    TextMob orT = label("or", 510, ly, 28, Colors.GRAY, false, false);
                    TextMob p2 = mono("9 2 7 2 4", 680, ly, 32, Colors.PINK);
                    play(new FadeIn(p1, d(0.5)), new FadeInAt(orT, 0.3, d(0.4)), new FadeInAt(p2, 0.6, d(0.5)));
                    pause(1.2);
                    viz.add(p1);
                    viz.add(orT);
                    viz.add(p2);
                }
                case 5 -> {
                    Tok a = toks.get(1), b = toks.get(3);
                    List<Animation> an = new ArrayList<>();
                    a.color(an, Colors.ORANGE, d(0.4));
                    b.color(an, Colors.ORANGE, d(0.4));
                    TextMob two = label("two 2s", 350, ly, 30, Colors.ORANGE, false, true);
                    an.add(new FadeIn(two, d(0.5)));
                    playAll(an);
                    pause(1.2);
                    viz.add(two);
                }
                case 6 -> {
                    List<MObject> c1 = chip("only the first 2", 580, ly, 290, 50, Colors.TEAL, 26);
                    List<MObject> c2 = chip("every 2", 810, ly, 160, 50, Colors.PINK, 26);
                    List<Animation> a = new ArrayList<>();
                    fade(a, c1, d(0.5));
                    fade(a, c2, d(0.7));
                    playAll(a);
                    pause(1.2);
                    List<Animation> back = new ArrayList<>();
                    toks.get(1).color(back, Colors.TEAL, d(0.4));
                    toks.get(3).color(back, Colors.TEAL, d(0.4));
                    playAll(back);
                    viz.addAll(c1);
                    viz.addAll(c2);
                }
                default -> pause(0.6);
            }
            // this decision's picture goes away before the next one comes (the size chip stays)
            List<MObject> done = new ArrayList<>();
            for (int v = vizFrom; v < viz.size(); v++) if (!keep.contains(viz.get(v))) done.add(viz.get(v));
            if (!done.isEmpty() && k < lines.length - 1) {
                fadeOutAll(d(0.5), done);
                viz.removeAll(done);
            }
            y += 62;
        }
        pause(1.0);
        // everything except the size counter fades with the slide-5 text
        List<MObject> gone = new ArrayList<>();
        for (MObject m : viz) if (!keep.contains(m)) gone.add(m);
        fadeOutAll(d(0.6), gone);
        // keep: header (index 0) and the size chip
        return keep;
    }

    // ── slide 6: what each operation costs ───────────────────────────

    private void costs(CodeBox code, RectMob[] cell, List<MObject> everything) {
        stepsLab = label("0", CX + 90, 230, 130, Colors.WHITE, false, true);
        TextMob stepsCap = label("steps", CX - 130, 240, 46, Colors.GRAY, false, true);
        play(new FadeIn(stepsLab, d(0.5)), new FadeIn(stepsCap, d(0.5)));
        everything.add(stepsLab);
        everything.add(stepsCap);

        // the size counter from slide 5 is still next to the array: reuse it
        TextMob sizeText = null;
        for (MObject m : everything) {
            if (m instanceof TextMob && ((TextMob) m).getText().equals("size = 5")) sizeText = (TextMob) m;
        }

        play(new FadeIn(code.hl, d(0.4)));
        String[] cost = {"O(1)", "O(N)", "O(N)", "O(N)", "O(1)"};
        List<List<MObject>> chips = new ArrayList<>();
        for (int k = 0; k < 5; k++) {
            List<MObject> ch = chip(cost[k], -200, code.lineY(3 + k), 150, 48, k == 0 || k == 4 ? Colors.GREEN : Colors.ORANGE, 26);
            chips.add(ch);
            everything.addAll(ch);
        }

        // insert(5): write at slot `size`
        code.setLine(3);
        StrokeTextMob c1 = stroke("insert(5): write at the slot size points to", CX, -55, 38, Colors.WHITE, false);
        resetSteps();
        play(new Write(c1, d(2.4)));
        Ptr sp = pointer("size", slotX(5), CELL_Y + 46, false, Colors.GOLD);
        play(new FadeIn(sp.arrow, d(0.5)), new FadeIn(sp.lab, d(0.5)));
        Tok t5 = new Tok("5", 5);
        toks.add(t5);
        List<Animation> ins = new ArrayList<>();
        for (MObject m : t5.parts()) ins.add(new DropIn(m, 90, 0, d(0.8)));
        playAll(ins);
        List<Animation> g = new ArrayList<>();
        t5.color(g, Colors.GREEN, d(0.3));
        playAll(g);
        tick();
        sizeText.setText("size = 6");
        List<Animation> sh = new ArrayList<>();
        sp.go(sh, slotX(6), d(0.5));
        playAll(sh);
        play(new FadeIn(chips.get(0).get(0), d(0.5)), new FadeIn(chips.get(0).get(1), d(0.5)));
        pause(1.2);
        List<Animation> back = new ArrayList<>();
        t5.color(back, Colors.TEAL, d(0.3));
        playAll(back);
        play(new FadeOut(c1, d(0.4)));
        remove(c1);
        everything.addAll(sp.parts());
        everything.addAll(t5.parts());

        // find(9): scan from the left
        code.setLine(4);
        play(code.moveHl(4, d(0.5)));
        StrokeTextMob c2 = stroke("find(9): look at the elements one by one", CX, -55, 38, Colors.WHITE, false);
        resetSteps();
        play(new Write(c2, d(2.0)));
        Ptr ip = pointer("i", slotX(0), CELL_Y - 46, true, Colors.ORANGE);
        play(new FadeIn(ip.arrow, d(0.4)), new FadeIn(ip.lab, d(0.4)));
        for (int i = 0; i <= 4; i++) {
            Tok t = toks.get(i);
            List<Animation> mv = new ArrayList<>();
            if (i > 0) ip.go(mv, slotX(i), d(0.45));
            t.color(mv, i == 4 ? Colors.GREEN : Colors.ORANGE, d(0.35));
            playAll(mv);
            tick();
            pause(0.18);
            if (i < 4) {
                List<Animation> off = new ArrayList<>();
                t.color(off, Colors.TEAL, d(0.25));
                playAll(off);
            }
        }
        play(new FadeIn(chips.get(1).get(0), d(0.5)), new FadeIn(chips.get(1).get(1), d(0.5)));
        TextMob nwords = label("up to N elements to look at", CX, 80, 40, Colors.ORANGE, false, true);
        play(new FadeIn(nwords, d(0.5)));
        pause(1.2);
        List<Animation> offAll = new ArrayList<>();
        toks.get(4).color(offAll, Colors.TEAL, d(0.3));
        offAll.add(new FadeOut(ip.arrow, d(0.4)));
        offAll.add(new FadeOut(ip.lab, d(0.4)));
        offAll.add(new FadeOut(nwords, d(0.4)));
        offAll.add(new FadeOut(c2, d(0.4)));
        playAll(offAll);
        remove(c2);
        remove(nwords);

        // remove(2): delete every 2, shifting the rest left each time
        play(code.moveHl(5, d(0.5)));
        StrokeTextMob c3 = stroke("remove(2): delete it, then shift the rest to fill the gap", CX, -55, 38, Colors.WHITE, false);
        resetSteps();
        play(new Write(c3, d(2.6)));
        Ptr rp = pointer("i", slotX(0), CELL_Y - 46, true, Colors.ORANGE);
        play(new FadeIn(rp.arrow, d(0.4)), new FadeIn(rp.lab, d(0.4)));
        int i = 0;
        while (i < toks.size()) {
            Tok t = toks.get(i);
            List<Animation> mv = new ArrayList<>();
            rp.go(mv, slotX(i), d(0.4));
            playAll(mv);
            tick();
            if (t.text.getText().equals("2")) {
                List<Animation> kill = new ArrayList<>();
                t.color(kill, Colors.RED, d(0.3));
                playAll(kill);
                List<Animation> pop = new ArrayList<>();
                pop.add(new FadeOut(t.box, d(0.4)));
                pop.add(new FadeOut(t.text, d(0.4)));
                playAll(pop);
                remove(t.box);
                remove(t.text);
                toks.remove(i);
                List<Animation> shift = new ArrayList<>();
                for (int j = i; j < toks.size(); j++) toks.get(j).toSlot(shift, j, d(0.5));
                sp.go(shift, slotX(toks.size()), d(0.5));
                playAll(shift);
                for (int j = i; j < toks.size(); j++) tick();
                // the array got shorter: size follows
                sizeText.setText("size = " + toks.size());
                play(new ScaleTo(sizeText, 1.3, d(0.2)));
                play(new ScaleTo(sizeText, 1.0, d(0.25)));
            } else {
                i++;
            }
        }
        sizeText.setText("size = " + toks.size());
        play(new FadeIn(chips.get(2).get(0), d(0.5)), new FadeIn(chips.get(2).get(1), d(0.5)));
        TextMob shifted = label("shifting costs up to N moves", CX, 80, 40, Colors.ORANGE, false, true);
        play(new FadeIn(shifted, d(0.5)));
        pause(1.2);
        play(new FadeOut(rp.arrow, d(0.4)), new FadeOut(rp.lab, d(0.4)), new FadeOut(shifted, d(0.4)), new FadeOut(c3, d(0.4)));
        remove(c3);
        remove(shifted);

        // print(): walk through everything
        play(code.moveHl(6, d(0.5)));
        StrokeTextMob c4 = stroke("print(): visit every element", CX, -55, 38, Colors.WHITE, false);
        resetSteps();
        play(new Write(c4, d(1.8)));
        TextMob out = mono("Output:", CX - 250, 60, 46, Colors.GOLD);
        out.setAlignment(javafx.scene.text.TextAlignment.LEFT);
        play(new FadeIn(out, d(0.4)));
        Ptr pp = pointer("i", slotX(0), CELL_Y - 46, true, Colors.ORANGE);
        play(new FadeIn(pp.arrow, d(0.4)), new FadeIn(pp.lab, d(0.4)));
        StringBuilder sbOut = new StringBuilder("Output:");
        for (int k = 0; k < toks.size(); k++) {
            List<Animation> mv = new ArrayList<>();
            if (k > 0) pp.go(mv, slotX(k), d(0.4));
            toks.get(k).color(mv, Colors.ORANGE, d(0.3));
            playAll(mv);
            tick();
            sbOut.append(' ').append(toks.get(k).text.getText());
            out.setText(sbOut.toString());
            pause(0.15);
            List<Animation> off = new ArrayList<>();
            toks.get(k).color(off, Colors.TEAL, d(0.25));
            playAll(off);
        }
        play(new FadeIn(chips.get(3).get(0), d(0.5)), new FadeIn(chips.get(3).get(1), d(0.5)));
        pause(1.0);
        play(new FadeOut(pp.arrow, d(0.4)), new FadeOut(pp.lab, d(0.4)), new FadeOut(c4, d(0.4)), new FadeOut(out, d(0.4)));
        remove(c4);
        remove(out);

        // size(): the counter already knows
        play(code.moveHl(7, d(0.5)));
        StrokeTextMob c5 = stroke("size(): just read the counter", CX, -55, 38, Colors.WHITE, false);
        resetSteps();
        play(new Write(c5, d(1.8)));
        play(new ScaleTo(sizeText, 1.35, d(0.3)));
        tick();
        play(new ScaleTo(sizeText, 1.0, d(0.35)));
        play(new FadeIn(chips.get(4).get(0), d(0.5)), new FadeIn(chips.get(4).get(1), d(0.5)));
        pause(1.2);
        play(new FadeOut(c5, d(0.4)));
        remove(c5);

        StrokeTextMob sum1 = stroke("insert and size: O(1).", CX, 40, 46, Colors.GREEN, false);
        StrokeTextMob sum2 = stroke("find, remove and print: O(N).", CX, 110, 46, Colors.ORANGE, false);
        play(new Write(sum1, d(1.8)));
        play(new Write(sum2, d(2.2)));
        everything.add(sum1);
        everything.add(sum2);
        pause(2.6);
    }
}
