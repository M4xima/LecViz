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
 * Slides 18-19 of the arrays deck (insertion sort) in the pixel-slime style with sound, 16:9 like the other clips.
 * The video is cut into three parts so each file stays under the size limit; they play one after the other:
 *
 *   part 1  "pixel_18"   slide 18: the text one line at a time, then the slide's question ("O(n log n)? But are we
 *                        doing more work?") answered on slimes: binary search finds the place in two probes, but
 *                        making room still means shifting every bigger slime
 *   part 2  "pixel_19a"  slide 19: the code exactly as on the slide, with its three callouts, then run on a hand of
 *                        eight slimes: the key is picked up and leaves a dashed hole, bigger slimes slide into it,
 *                        the key drops in and the sorted bar grows; ii, jj and key, each comparison, and the shift
 *                        and comparison counters are on screen; the sound follows every step
 *   part 3  "pixel_19b"  the best case (sorted input) and the worst case (reverse sorted) run on the same code,
 *                        then the slide's closing points next to a table of comparisons and shifts
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSInsertionPixelScene extends PDSPixelBase {

    private static final String[] CODE = {
            "for (ii = 1 ; ii < N; ++ii) {",
            "    int key = arr[ii];",
            "    int jj = ii - 1;",
            "",
            "    while (jj >= 0 && key < arr[jj]) {",
            "        arr[jj + 1] = arr[jj];",
            "        --jj;",
            "    }",
            "    arr[jj + 1] = key;",
            "}"};
    private static final int L_FOR = 0, L_KEY = 1, L_J = 2, L_WHILE = 4, L_SHIFT = 5, L_DEC = 6, L_PUT = 8;

    private static final int[] MIXED = {7, 4, 2, 1, 8, 3, 6, 5};
    private static final int[] SORTED = {1, 2, 3, 4, 5, 6, 7, 8};
    private static final int[] REVERSE = {8, 7, 6, 5, 4, 3, 2, 1};

    private final int part;

    public PDSInsertionPixelScene(int part) { this.part = part; }

    // ── state of the current run ─────────────────────────────────────
    private List<MObject> title;
    private PixelSlime[] at, original;
    private Brackets brackets;
    private Pill iChip, jChip, keyChip, cmpChip, shiftChip, cmpPill;
    private Tag iTag, jTag;
    private Ghost hole;
    private RectMob sortedBar;
    private TextMob sortedLab;
    private int comparisons = 0, shifts = 0;
    private boolean iVisible = false;

    @Override
    public void construct() {
        setUseGradientBackground(false);
        setBackgroundColor(BG);
        title = makeTitle("INSERTION SORT");
        popIn(title);
        pause(0.3);
        switch (part) {
            case 1 -> part18();
            case 2 -> part19a();
            default -> part19b();
        }
        fadeOutAll(d(1.2), title);
        pause(0.4);
    }

    // ═════════════════════════════════════════════════════════════════
    //  part 1: slide 18
    // ═════════════════════════════════════════════════════════════════

    private void part18() {
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Consider ith element and insert it at its place w.r.t."));
        s.add(ln(3, "the first i elements."));
        s.add(ln(1, "Resembles insertion of a playing card."));
        s.add(ln(0, "Invariant: Keep the first i elements sorted.").kw("Invariant:", Colors.BLUE));
        s.add(ln(0, "Note: Insertion is in a sorted array.").kw("Note:", Colors.GOLD));
        s.add(ln(0, "Complexity: O(n log n)?"));
        s.add(ln(1, "Yes, binary search is O(log n)."));
        s.add(ln(2, "But are we doing more work?"));
        s.add(ln(1, "Best case, Worst case?"));
        s.add(ln(0, "Classwork: Write the code.").kw("Classwork", Colors.ORANGE));
        List<List<MObject>> groups = writeSlide(s, -345);
        pause(0.9);
        sfx("whoosh_down");
        swipeAway(groups);
        pause(0.5);
        binaryTeaser();
    }

    private void binaryTeaser() {
        viewX = 0;
        pitch = 230;
        ground = 40;
        handN = 5;
        int[] vals = {2, 4, 5, 6, 1};
        int n = vals.length;
        List<MObject> mine = new ArrayList<>();

        brackets = new Brackets(pitch * 2.5 + 10, 82);
        brackets.setPosition(0, ground + 8);
        add(brackets);
        mine.add(brackets);
        PixelSlime[] sl = new PixelSlime[n];
        for (int i = 0; i < n; i++) {
            sl[i] = slime(vals[i], slotX(i));
            mine.add(sl[i]);
        }
        StrokeTextMob intro = say("The first i elements are already sorted, so binary search can find the place.", 0, -370, 30, Colors.LIGHT_GRAY, 3.6);
        mine.add(intro);
        List<Animation> drop = new ArrayList<>();
        drop.add(new FadeIn(brackets, 0.4));
        for (int i = 0; i < n; i++) {
            drop.add(new LandIn(sl[i], 0.08 * i, 170, 0.8));
            drop.add(new FadeInAt(sl[i], 0.08 * i, 0.18));
            sfxAt(0.08 * i + 0.48, "plop", 2 + vals[i], pan(slotX(i)));
        }
        playAll(drop);

        // the sorted part gets its green bar
        RectMob bar = new RectMob(4 * pitch - 18, 11).setCornerRadius(5);
        bar.setFillColor(Colors.withAlpha(GREEN, 0.55));
        bar.setStrokeColor(GREEN);
        bar.setStrokeWidth(2);
        bar.setPosition(slotX(0) - pitch / 2 + 9 + (4 * pitch - 18) / 2, barY());
        bar.setOpacity(0);
        add(bar);
        mine.add(bar);
        sfx("good", 7, 0);
        play(new FadeIn(bar, 0.5));
        pause(0.5);

        // the new element steps out of the row
        PixelSlime key = sl[4];
        key.setMood(PixelSlime.Mood.OOF);
        key.setLabelColor(YELLOW);
        sfx("whoosh_up");
        sfxAt(0.05, "boing", 3, pan(slotX(4)));
        play(new Hop(key, 0, slotX(4), 130, 0, 0.6));

        TextMob ver = label("", 0, -292, 38, Colors.WHITE, false, true);
        ver.setFontFamily("Menlo");
        mine.add(ver);
        RectMob ring = new RectMob(168, 150).setCornerRadius(20);
        ring.setFillColor(Colors.withAlpha(ORANGE, 0.1));
        ring.setStrokeColor(ORANGE);
        ring.setStrokeWidth(4);
        ring.setPosition(slotX(1), ground + 4);
        ring.setOpacity(0);
        add(ring);
        mine.add(ring);
        TextMob midLab = label("mid", slotX(1), ground + 112, 30, ORANGE, false, true);
        mine.add(midLab);
        ver.setText("1 < 4   →   look left");
        sfx("tick", 9, pan(slotX(1)));
        play(new FadeIn(ring, 0.4), new FadeIn(midLab, 0.4), new FadeIn(ver, 0.4));
        pause(1.5);
        ver.setText("1 < 2   →   look left");
        sfx("tick", 7, pan(slotX(0)));
        play(new MoveTo(ring, slotX(0), ground + 4, 0.55).setEasing(Easing.EASE_IN_OUT),
                new MoveTo(midLab, slotX(0), ground + 112, 0.55).setEasing(Easing.EASE_IN_OUT));
        pause(1.5);
        ver.setText("place found: slot 0   (2 probes)");
        ver.setFillColor(GREEN);
        sfx("good", 9, 0);
        pause(1.3);
        play(new FadeOut(ring, 0.4), new FadeOut(midLab, 0.4));

        // ...but the room still has to be made
        TextMob cnt = label("shifts: 0", 0, 245, 38, YELLOW, false, true);
        mine.add(cnt);
        StrokeTextMob more = say("...but making room still means shifting every bigger slime.", 0, 308, 30, Colors.WHITE, 2.8);
        mine.add(more);
        play(new FadeIn(cnt, 0.4));
        int cntShifts = 0;
        for (int k = 3; k >= 0; k--) {
            sfx("slide", 1, pan(slotX(k)));
            play(new Hop(sl[k], 0, slotX(k + 1), 0, 46, 0.45));
            cntShifts++;
            cnt.setText("shifts: " + cntShifts);
            play(new Pulse(cnt, 0.16, 0.18));
        }
        sfxAt(0.4, "thud", 0, pan(slotX(0)));
        play(new Hop(key, 0, slotX(0), 0, 40, 0.6));
        key.setMood(PixelSlime.Mood.JOY);
        key.setLabelColor(Color.WHITE);
        sfx("good", 6, pan(slotX(0)));
        sfx("sparkle", 0, pan(slotX(0)));
        Confetti spark = new Confetti(18, 280, 0.5, 700, 6, 9, 17, RAINBOW[0], Color.WHITE, YELLOW);
        spark.setPosition(slotX(0), ground - 10);
        add(spark);
        play(new Burst(spark), new Wobble(key, 0.5));
        remove(spark);
        key.setRotation(0);
        key.setMood(PixelSlime.Mood.NORMAL);
        play(new ResizeX(bar, 5 * pitch - 18, slotX(0) - pitch / 2 + 9 + (5 * pitch - 18) / 2, 0.4));
        pause(0.5);
        StrokeTextMob concl = say("Finding the place is O(log n), but the shifting is still up to O(i): we do more work.", 0, 370, 29, ORANGE, 3.6);
        mine.add(concl);
        pause(0.4);
        StrokeTextMob ask = say("So what are the best and the worst cases?", 0, 432, 32, Colors.WHITE, 2.2);
        mine.add(ask);
        pause(2.2);
        sfx("whoosh_down");
        fadeOutAll(d(0.9), mine);
        pause(0.3);
    }

    // ═════════════════════════════════════════════════════════════════
    //  part 2: slide 19, the code and a mixed hand
    // ═════════════════════════════════════════════════════════════════

    private void part19a() {
        // the listing as on the slide, with its three callouts
        panel = new CodePanel(CODE, -380, -405, 860, 29, 46);
        List<Animation> in = new ArrayList<>();
        panel.typeIn(in, 4.2);
        for (int k = 0; k < 40; k++) sfxAt(0.12 + 0.1 * k, "type", 0, -0.3);
        sfx("whoosh_up");
        playAll(in);
        List<MObject> stage = new ArrayList<>();
        List<Animation> co = new ArrayList<>();
        callout(co, stage, "ith element", null, 580, panel.lineY(1), 300, 62, panel.lineEndX(1), panel.lineY(1));
        sfx("pop", 9, 0.4);
        playAll(co);
        pause(0.8);
        co = new ArrayList<>();
        callout(co, stage, "Shift elements", "0 + 1 + 2 + ... n-1", 580, panel.lineY(5) + 14, 340, 100, panel.lineEndX(5), panel.lineY(5));
        sfx("pop", 10, 0.4);
        playAll(co);
        pause(0.8);
        co = new ArrayList<>();
        callout(co, stage, "At its place", null, 580, panel.lineY(8), 300, 62, panel.lineEndX(8), panel.lineY(8));
        sfx("pop", 11, 0.4);
        playAll(co);
        pause(2.0);

        // the panel moves left, the hand drops in on the right
        sfx("whoosh_up");
        List<Animation> mv = new ArrayList<>();
        panel.slide(mv, -120, 410, 0.9);
        for (MObject m : stage) mv.add(new FadeOut(m, 0.5));
        playAll(mv);
        for (MObject m : stage) remove(m);

        beginRun(MIXED);
        List<MObject> lesson = note("Like a playing card: take the next slime and insert it into the sorted hand.", Colors.LIGHT_GRAY, 3.4);
        pause(0.6);
        drop(lesson);
        double[] sp = {1.0, 0.7, 0.5, 0.4, 0.3, 0.3, 0.3};
        for (int ii = 1; ii < handN; ii++) insertStep(ii, sp[ii - 1]);
        finishRun(true);
        List<MObject> inv = note("Invariant: the first i elements stay sorted, so after n - 1 insertions all of them are.", GREEN, 3.4);
        pause(2.2);
        drop(inv);
        endRun(true);
        play(new FadeOut(panel.card, 0.6), new FadeOut(panel.bar, 0.6), new FadeOut(panel.tick, 0.6));
        List<Animation> off = new ArrayList<>();
        for (TextMob r : panel.runs) off.add(new FadeOut(r, 0.6));
        playAll(off);
    }

    // ═════════════════════════════════════════════════════════════════
    //  part 3: best case, worst case, the table
    // ═════════════════════════════════════════════════════════════════

    private void part19b() {
        panel = new CodePanel(CODE, -500, 5, 860, 29, 46);
        List<Animation> in = new ArrayList<>();
        panel.appear(in);
        sfx("whoosh_up");
        playAll(in);
        pause(0.3);

        // best case: already sorted
        beginRun(SORTED);
        List<MObject> c1 = note("Best case: already sorted. The while loop stops at once, O(1) work per element.", GREEN, 3.4);
        for (int ii = 1; ii < handN; ii++) insertStep(ii, 0.16);
        finishRun(false);
        pause(1.2);
        drop(c1);
        int[] best = {comparisons, shifts};
        endRun(false);

        // worst case: reverse sorted
        beginRun(REVERSE);
        List<MObject> c2 = note("Worst case: reverse sorted. Slime ii shifts ii places.", RED, 3.0);
        for (int ii = 1; ii < handN; ii++) insertStep(ii, ii == 1 ? 0.3 : 0.14);
        finishRun(false);
        drop(c2);
        List<MObject> c3 = note("0 + 1 + 2 + ... + (n-1) = " + shifts + " shifts for n = 8, so O(n²).", ORANGE, 3.0);
        pause(2.0);
        int[] worst = {comparisons, shifts};
        drop(c3);
        endRun(false);

        // the panel leaves; the slide's closing points and the counts
        List<Animation> off = new ArrayList<>();
        off.add(new FadeOut(panel.card, 0.6));
        off.add(new FadeOut(panel.bar, 0.6));
        off.add(new FadeOut(panel.tick, 0.6));
        for (TextMob r : panel.runs) off.add(new FadeOut(r, 0.6));
        playAll(off);
        pause(0.3);

        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Best case: Sorted:").tail("while loop is O(1)").kw("Best", GREEN));
        s.add(ln(0, "Worst case: Reverse sorted:").tail("O(n²)").kw("Worst", RED));
        List<List<MObject>> groups = writeSlide(s, -330);
        pause(0.5);
        int[] mixed = count(MIXED);
        String[][] rows = {
                {"Sorted (best case)", String.valueOf(best[0]), String.valueOf(best[1])},
                {"Mixed", String.valueOf(mixed[0]), String.valueOf(mixed[1])},
                {"Reverse sorted (worst case)", String.valueOf(worst[0]), String.valueOf(worst[1])}};
        Color[][] tint = {{null, Colors.TEAL, Colors.BLUE}, {null, Colors.TEAL, Colors.GOLD}, {null, Colors.TEAL, Colors.RED}};
        for (int r = 0; r < 4; r++) sfxAt(d(0.7) * r + 0.2, "pop", 4 + r, 0);
        List<MObject> table = dropTable(new String[]{"Input (8 slimes)", "Comparisons", "Shifts"}, rows,
                new double[]{520, 300, 260}, tint, -150);
        StrokeTextMob formula = say("Worst case shifts: 0 + 1 + 2 + ... + (n-1) = " + worst[1] + " for n = 8, and O(n²) in general.", 0, 180, 30, YELLOW, 3.4);
        sfx("tada");
        pause(3.2);
        List<MObject> all = new ArrayList<>(table);
        all.add(formula);
        for (List<MObject> g : groups) all.addAll(g);
        sfx("whoosh_down");
        fadeOutAll(1.2, all);
    }

    /** Comparisons and shifts that the code makes on the data. */
    private static int[] count(int[] data) {
        int[] a = data.clone();
        int comps = 0, sh = 0;
        for (int ii = 1; ii < a.length; ii++) {
            int key = a[ii], jj = ii - 1;
            while (true) {
                if (jj < 0) break;
                comps++;
                if (!(key < a[jj])) break;
                a[jj + 1] = a[jj];
                sh++;
                jj--;
            }
            a[jj + 1] = key;
        }
        return new int[]{comps, sh};
    }

    // ═════════════════════════════════════════════════════════════════
    //  running the code on a hand of slimes
    // ═════════════════════════════════════════════════════════════════

    /** A caption in the right column under the counters, wrapped into lines of at most ~46 characters. */
    private List<MObject> note(String text, Color c, double secs) {
        List<String> lines = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        for (String w : text.split(" ")) {
            if (cur.length() + w.length() + 1 > 46 && cur.length() > 0) {
                lines.add(cur.toString());
                cur.setLength(0);
            }
            if (cur.length() > 0) cur.append(' ');
            cur.append(w);
        }
        lines.add(cur.toString());
        List<MObject> made = new ArrayList<>();
        for (int k = 0; k < lines.size(); k++) {
            made.add(say(lines.get(k), RIGHT_CX, 335 + 46 * k, 29, c, Math.max(1.0, secs * lines.get(k).length() / text.length())));
        }
        return made;
    }

    private void drop(List<MObject> l) {
        List<Animation> a = new ArrayList<>();
        for (MObject m : l) a.add(new FadeOut(m, 0.5));
        playAll(a);
        for (MObject m : l) remove(m);
    }

    private void beginRun(int[] data) {
        viewX = 0;
        pitch = 190;
        ground = -195;
        handN = data.length;
        comparisons = 0;
        shifts = 0;
        iVisible = false;
        at = new PixelSlime[handN];
        original = new PixelSlime[handN];
        brackets = new Brackets(pitch * handN / 2 + 24, 82);
        brackets.setPosition(viewX, ground + 6);
        add(brackets);
        hole = new Ghost(PS * 17.5, PS * 13.3);
        hole.setPosition(slotX(0), ground + 6);
        add(hole);
        sortedBar = new RectMob(pitch - 16, 11).setCornerRadius(5);
        sortedBar.setFillColor(Colors.withAlpha(GREEN, 0.55));
        sortedBar.setStrokeColor(GREEN);
        sortedBar.setStrokeWidth(2);
        sortedBar.setPosition(slotX(0), barY());
        sortedBar.setOpacity(0);
        add(sortedBar);
        sortedLab = label("sorted", slotX(0), barY() + 26, 22, GREEN, false, true);
        iTag = new Tag("ii", ORANGE);
        jTag = new Tag("jj", CYAN);
        iTag.setPosition(slotX(1), markY());
        jTag.setPosition(slotX(0), markY());
        add(iTag);
        add(jTag);
        iChip = new Pill(RIGHT_CX - 250, varY(), 235, 58, ORANGE, "ii = –", 30);
        jChip = new Pill(RIGHT_CX, varY(), 235, 58, CYAN, "jj = –", 30);
        keyChip = new Pill(RIGHT_CX + 250, varY(), 235, 58, YELLOW, "key = –", 30);
        cmpChip = new Pill(RIGHT_CX - 155, cntY(), 300, 50, GRAY, "comparisons 0", 26);
        shiftChip = new Pill(RIGHT_CX + 155, cntY(), 300, 50, GRAY, "shifts 0", 26);
        cmpPill = new Pill(RIGHT_CX, pillY(), 500, 66, Colors.GRAY, "", 34);

        for (int k = 0; k < handN; k++) {
            at[k] = slime(data[k], slotX(k));
            original[k] = at[k];
        }
        List<Animation> drop = new ArrayList<>();
        drop.add(new FadeIn(brackets, 0.4));
        for (int k = 0; k < handN; k++) {
            drop.add(new LandIn(at[k], 0.07 * k, 120, 0.8));
            drop.add(new FadeInAt(at[k], 0.07 * k, 0.18));
            sfxAt(0.07 * k + 0.48, "plop", 2 + data[k], pan(slotX(k)));
        }
        for (Pill p : new Pill[]{iChip, jChip, keyChip, cmpChip, shiftChip}) p.fadeIn(drop, 0.5);
        drop.add(new FadeIn(sortedBar, 0.5));
        drop.add(new FadeIn(sortedLab, 0.5));
        playAll(drop);
        List<Animation> hl = new ArrayList<>();
        hl.add(new FadeIn(panel.bar, 0.3));
        hl.add(new FadeIn(panel.tick, 0.3));
        playAll(hl);
        pause(0.3);
    }

    /** One pass: take arr[ii] and insert it into the sorted part. */
    private void insertStep(int ii, double sp) {
        PixelSlime key = at[ii];
        int keyVal = key.getValue();

        // for (ii = 1; ii < N; ++ii)
        List<Animation> a = bar(L_FOR, sp, "ii = " + ii, ORANGE);
        iChip.set("ii = " + ii);
        jChip.set("jj = –");
        iChip.bumpUp(a, 0.2);
        if (!iVisible) {
            a.add(new FadeIn(iTag, 0.2));
            iVisible = true;
        }
        a.add(new MoveTo(iTag, slotX(ii), markY(), q(0.35, sp, 0.14)));
        sfx("tick", 11, pan(slotX(ii)));
        playAll(a);
        hold(q(0.1, sp, 0.03));

        // int key = arr[ii]: pick it up
        List<Animation> up = bar(L_KEY, sp, "key = " + keyVal, YELLOW);
        keyChip.set("key = " + keyVal);
        key.setMood(PixelSlime.Mood.OOF);
        key.setLabelColor(YELLOW);
        hole.setPosition(slotX(ii), ground + 6);
        sfx("whoosh_up");
        sfxAt(0.04, "boing", 2 + keyVal, pan(slotX(ii)));
        up.add(new Hop(key, 0, slotX(ii), 112, 0, q(0.5, sp, 0.26)));
        up.add(new FadeIn(hole, 0.22));
        keyChip.bumpUp(up, 0.2);
        playAll(up);
        hold(q(0.12, sp, 0.03));

        // int jj = ii - 1
        int jj = ii - 1;
        List<Animation> js = bar(L_J, sp, "jj = " + jj, CYAN);
        jChip.set("jj = " + jj);
        jTag.setPosition(slotX(jj), markY());
        js.add(new FadeIn(jTag, q(0.22, sp, 0.1)));
        jChip.bumpUp(js, 0.2);
        sfx("tick", 13, pan(slotX(jj)));
        playAll(js);
        hold(q(0.14, sp, 0.03));

        // while (jj >= 0 && key < arr[jj])
        while (true) {
            if (jj < 0) {
                List<Animation> stop = bar(L_WHILE, sp, "jj < 0", RED);
                cmpPill.set("jj < 0   ✗  stop");
                cmpPill.tint(RED);
                sfx("bad");
                if (cmpPill.box.getOpacity() < 0.99) cmpPill.fadeIn(stop, 0.1);
                playAll(stop);
                hold(q(0.5, sp, 0.16));
                break;
            }
            int vj = at[jj].getValue();
            List<Animation> cmp = bar(L_WHILE, sp, keyVal + " < " + vj + " ?", Color.web("#cfd3d8"));
            comparisons++;
            cmpChip.set("comparisons " + comparisons);
            cmpPill.set(keyVal + " < " + vj + "  ?");
            cmpPill.tint(Colors.GRAY);
            sfx("tick", 2 + vj, pan(slotX(jj)));
            if (cmpPill.box.getOpacity() < 0.99) cmpPill.fadeIn(cmp, 0.1);
            cmpChip.bumpUp(cmp, 0.22);
            cmp.add(new Pulse(at[jj], 0.26, 0.14));
            playAll(cmp);
            hold(q(0.2, sp, 0.04));
            boolean shift = keyVal < vj;
            cmpPill.set(keyVal + " < " + vj + (shift ? "  ✓  shift" : "  ✗  stop"));
            cmpPill.tint(shift ? ORANGE : GREEN);
            panel.note.setText(shift ? "True" : "False");
            panel.note.setFillColor(shift ? ORANGE : GREEN);
            sfx(shift ? "pop" : "good", shift ? 4 : 2 + keyVal, pan(slotX(jj)));
            hold(q(0.4, sp, 0.1));
            if (!shift) break;

            // arr[jj + 1] = arr[jj]: the neighbour slides into the hole
            List<Animation> sl = bar(L_SHIFT, sp, "arr[" + (jj + 1) + "] = " + vj, ORANGE);
            shifts++;
            shiftChip.set("shifts " + shifts);
            sfx("slide", 1, pan(slotX(jj)));
            double hop = q(0.5, sp, 0.24);
            sl.add(new Hop(at[jj], 0, slotX(jj + 1), 0, 46, hop));
            sl.add(new MoveTo(hole, slotX(jj), ground + 6, hop));
            shiftChip.bumpUp(sl, 0.22);
            playAll(sl);
            at[jj + 1] = at[jj];

            // --jj
            List<Animation> dj = bar(L_DEC, sp, "jj = " + (jj - 1), CYAN);
            jj--;
            jChip.set("jj = " + jj);
            if (jj >= 0) dj.add(new MoveTo(jTag, slotX(jj), markY(), q(0.3, sp, 0.14)));
            else dj.add(new FadeOut(jTag, 0.18));
            jChip.bumpUp(dj, 0.2);
            playAll(dj);
            hold(q(0.05, sp, 0.02));
        }

        // arr[jj + 1] = key: it drops into the hole
        List<Animation> down = bar(L_PUT, sp, "arr[" + (jj + 1) + "] = " + keyVal, YELLOW);
        at[jj + 1] = key;
        double drop = q(0.5, sp, 0.26);
        down.add(new Hop(key, 0, slotX(jj + 1), 0, 38, drop));
        down.add(new FadeOut(cmpPill.box, 0.2));
        down.add(new FadeOut(cmpPill.txt, 0.2));
        down.add(new FadeOut(hole, 0.24));
        if (jj >= 0) down.add(new FadeOut(jTag, 0.2));
        sfxAt(drop * 0.93, "thud", 0, pan(slotX(jj + 1)));
        playAll(down);
        key.setMood(PixelSlime.Mood.JOY);
        key.setLabelColor(Color.WHITE);
        sfx("good", 2 + ii, pan(slotX(jj + 1)));
        sfx("sparkle", 0, pan(slotX(jj + 1)));
        Confetti spark = new Confetti(16, 260, 0.45, 700, 6, 9, 31L * ii, RAINBOW[(keyVal - 1) % 8], Color.WHITE, YELLOW);
        spark.setPosition(slotX(jj + 1), ground - 10);
        add(spark);
        List<Animation> done = new ArrayList<>();
        done.add(new Burst(spark));
        double w = pitch * (ii + 1) - 14;
        double cxBar = slotX(0) - pitch / 2 + 7 + w / 2;
        done.add(new ResizeX(sortedBar, w, cxBar, 0.34));
        done.add(new MoveTo(sortedLab, cxBar, barY() + 26, 0.34).setEasing(Easing.EASE_IN_OUT));
        done.add(new Wobble(key, 0.42));
        playAll(done);
        remove(spark);
        key.setRotation(0);
        key.setMood(PixelSlime.Mood.NORMAL);
        keyChip.set("key = –");
        panel.note.setOpacity(0);
        hold(q(0.12, sp, 0.02));
    }

    /** The whole row is sorted: the slimes celebrate. */
    private void finishRun(boolean big) {
        List<Animation> clear = new ArrayList<>();
        clear.add(new FadeOut(iTag, 0.25));
        playAll(clear);
        iChip.set("ii = –");
        jChip.set("jj = –");
        List<Animation> wave = new ArrayList<>();
        for (int k = 0; k < handN; k++) {
            at[k].setMood(PixelSlime.Mood.JOY);
            wave.add(new Hop(at[k], 0.085 * k, slotX(k), 0, big ? 80 : 60, 0.5));
            sfxAt(0.085 * k, "arp", 2 + k, pan(slotX(k)));
        }
        Confetti party = new Confetti(big ? 120 : 70, 900, 1.7, 1500, 7, 13, 77, RAINBOW);
        party.setPosition(viewX, ground - 40);
        add(party);
        sfxAt(0.8, "tada", 0, 0);
        wave.add(new Burst(party));
        double w = pitch * handN - 14;
        wave.add(new ResizeX(sortedBar, w, viewX, 0.3));
        wave.add(new MoveTo(sortedLab, viewX, barY() + 26, 0.3).setEasing(Easing.EASE_IN_OUT));
        cmpChip.bumpUp(wave, 0.7);
        shiftChip.bumpUp(wave, 0.7);
        playAll(wave);
        remove(party);
        hold(0.5);
    }

    /** Takes the hand away (back to the unsorted order, or just fading out). */
    private void endRun(boolean shuffleBack) {
        List<Animation> an = new ArrayList<>();
        an.add(new FadeOut(sortedBar, 0.5));
        an.add(new FadeOut(sortedLab, 0.5));
        an.add(new FadeOut(brackets, 0.6));
        for (Pill p : new Pill[]{iChip, jChip, keyChip, cmpChip, shiftChip}) p.fadeOut(an, 0.6);
        if (shuffleBack) sfx("swirl");
        for (int k = 0; k < handN; k++) {
            an.add(new FadeOut(at[k], 0.6));
            if (shuffleBack) an.add(new Hop(at[k], 0.03 * k, at[k].getPosition().x(), 0, 120 + 10 * k, 0.7));
        }
        playAll(an);
        for (int k = 0; k < handN; k++) remove(at[k]);
        remove(brackets);
        remove(sortedBar);
        remove(sortedLab);
        remove(hole);
        remove(iTag);
        remove(jTag);
        for (Pill p : new Pill[]{iChip, jChip, keyChip, cmpChip, shiftChip, cmpPill}) {
            remove(p.box);
            remove(p.txt);
        }
        panel.note.setOpacity(0);
        pause(0.2);
    }
}
