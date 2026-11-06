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
 * Standalone clip for slide 13 of the priority-queue deck: applications.
 *
 *   - the slide's lines in the pen-stroke style: process scheduling, and the k-th maximum by sorting (O(N log N)) or by
 *     building a heap and k deleteMax (O(N + k log N))
 *   - process scheduling: a heap of waiting processes, a CPU box; the scheduler always takes the highest priority, and a
 *     new process can arrive at any time
 *   - the k-th maximum: the slide's listing is typed and run on ten numbers with k = 3: build, two deleteMax, and the
 *     third deleteMax is the answer; then the two costs side by side
 *   - the slide's question, 75th largest in 100 elements: deleting 74 times is wasteful; it is the 26th smallest, so a
 *     MinHeap needs only 25 deleteMin (shown as two bars)
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSPQAppsScene extends PDSHeapClipBase {

    private final List<MObject> mine = new ArrayList<>();
    private StrokeTextMob head;
    private HV h;

    @Override
    public void construct() {
        head = writeHeading("Applications");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Process scheduling: Next lab assignment").kw("Process scheduling", Colors.BLUE));
        s.add(ln(0, "Finding k-th maximum").kw("k-th maximum", Colors.GOLD));
        s.add(ln(1, "Sort and return k-th element: O(N log N)").kw("O(N log N)", Colors.RED));
        s.add(ln(1, "Build heap and k deleteMax: O(N + k log N)").kw("O(N + k log N)", Colors.GREEN));
        List<List<MObject>> text = writeSlide(s, -300);
        pause(1.8);
        swipeAway(text);
        pause(0.4);

        scheduling();
        kth();
        question();

        unsay();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private List<MObject> drop(String text, Color c, double x, double y, double w) {
        List<MObject> ch = chip(text, x, y, w, 66, c, 32);
        List<Animation> an = new ArrayList<>();
        for (MObject m : ch) an.add(new DropIn(m, 40, 0, d(0.6)));
        playAll(an);
        return ch;
    }

    // ── process scheduling ───────────────────────────────────────────

    private void scheduling() {
        TextMob t1 = label("Process scheduling: waiting processes", 610, -300, 30, Colors.BLUE, false, true);
        play(new FadeIn(t1, d(0.5)));
        mine.add(t1);
        h = new HV(-390, -345, 720, 125, 34, 30, 4, true, -820, 190, 72, 64, 13);
        h.build(new int[]{52, 39, 12, 6, 30, 5, 11}, 0.25);
        h.showFrame(d(0.5));
        mine.addAll(h.parts());
        RectMob cpu = panel(610, -120, 440, 140, Colors.TEAL, 0.12);
        TextMob cl = label("CPU", 440, -165, 28, Colors.TEAL, false, true);
        TextMob run = label("idle", 610, -110, 54, Colors.WHITE, false, true);
        play(new FadeIn(cpu, d(0.5)), new FadeIn(cl, d(0.5)), new FadeIn(run, d(0.5)));
        mine.add(cpu);
        mine.add(cl);
        mine.add(run);
        narr("Each process has a priority. The scheduler always runs the one with the highest priority: deleteMax.", Colors.LIGHT_GRAY);
        pause(2.2);
        int v = heapDeleteMax(h, 0.5, false);
        run.setText("process " + v);
        play(new FadeIn(run, d(0.4)));
        pause(0.9);
        narr("A new process arrives with priority 45: insert.", Colors.PINK);
        play(new FadeOut(run, d(0.3)));
        run.setText("idle");
        heapInsert(h, 45, 0.5, false);
        h.paint(h.a[findKey(45)], NODE_C, d(0.3));
        pause(0.6);
        narr("The next process to run is the new highest priority: deleteMax again.", Colors.LIGHT_GRAY);
        v = heapDeleteMax(h, 0.5, false);
        run.setText("process " + v);
        play(new FadeIn(run, d(0.4)));
        pause(1.0);
        narr("The same idea picks the next lab assignment: always the most urgent one that is waiting.", Colors.GOLD);
        pause(3.0);
        List<MObject> gone = new ArrayList<>(mine);
        fadeOutAll(d(0.9), gone);
        mine.clear();
        unsay();
        pause(0.3);
    }

    private int findKey(int v) {
        for (int i = 1; i <= h.n; i++) if (h.a[i] != null && h.a[i].v == v) return i;
        return 1;
    }

    // ── the k-th maximum ─────────────────────────────────────────────

    private void kth() {
        CodeBox code = new CodeBox(new String[]{
                "h.build(elements, nelements);",
                "",
                "for (int ii = 0; ii < k - 1; ++ii)",
                "    h.deleteMax();",
                "",
                "cout << k << \"'th largest element is \" << h.deleteMax() << endl;"}, -930, -380, 24, 36);
        code.typeIn(4.0);
        mine.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.3)));
        TextMob src = label("Source: heap-kmax.cpp", 640, -380, 28, Colors.GOLD, false, true);
        play(new FadeIn(src, d(0.6)));
        mine.add(src);

        h = new HV(-560, -110, 560, 85, 28, 22, 4, true, -830, 250, 60, 52, 12);
        h.build(new int[]{29, 5, 52, 11, 3, 39, 6, 30, 12, 1}, 0.2);
        h.showFrame(d(0.4));
        mine.addAll(h.parts());
        TextMob kl = label("k = 3", 560, -150, 54, Colors.GOLD, false, true);
        play(new FadeIn(kl, d(0.5)));
        mine.add(kl);
        narr("Ten numbers in any order, and we want the 3rd largest: k = 3.", Colors.LIGHT_GRAY);
        pause(2.0);

        play(code.moveHl(0, d(0.3)));
        narr("build: make a heap out of the array, O(N).", Colors.TEAL);
        buildHeap(h, 0.35, false);
        pause(1.0);

        OutStrip out = new OutStrip("deleted:", 330, -30, 56, Colors.RED);
        out.showTitle();
        mine.addAll(out.all());
        for (int ii = 0; ii < 2; ii++) {
            play(code.moveHl(2, d(0.25)));
            play(code.moveHl(3, d(0.25)));
            narr("ii = " + ii + ": deleteMax, and throw the answer away.", Colors.ORANGE);
            int v = heapDeleteMax(h, 0.45, false);
            out.add(String.valueOf(v), Colors.RED);
            mine.addAll(out.all());
            pause(0.6);
        }
        play(code.moveHl(5, d(0.3)));
        narr("The loop is over. The next deleteMax is the k-th largest.", Colors.GREEN);
        int v = heapDeleteMax(h, 0.45, false);
        out.add(String.valueOf(v), Colors.GREEN);
        mine.addAll(out.all());
        List<MObject> ans = drop("3'th largest element is " + v, Colors.GREEN, 610, 150, 560);
        mine.addAll(ans);
        pause(2.4);

        narr("Sorting would cost O(N log N). Here: O(N) to build plus k deleteMax at O(log N) each.", Colors.GOLD);
        List<MObject> c1 = drop("sort: O(N log N)", Colors.RED, 610, 240, 520);
        List<MObject> c2 = drop("heap: O(N + k log N)", Colors.GREEN, 610, 330, 520);
        mine.addAll(c1);
        mine.addAll(c2);
        pause(4.0);
        fadeOutAll(d(0.9), new ArrayList<>(mine));
        mine.clear();
        unsay();
        pause(0.3);
    }

    // ── the question: 75th largest of 100 ────────────────────────────

    private void question() {
        StrokeTextMob q = stroke("How would you find the 75th largest element in a 100-element array?", 0, -330, 40, Colors.BLUE, false);
        play(new Write(q, d(3.6)));
        mine.add(q);
        pause(1.0);
        narr("Think of it: with a MaxHeap we would do deleteMax 74 times before the 75th.", Colors.LIGHT_GRAY);
        pause(2.0);
        bar("MaxHeap: build + 74 deleteMax", -150, 74, Colors.RED);
        pause(1.2);
        narr("But the 75th largest of 100 is the 26th smallest (100 - 75 + 1 = 26).", Colors.GOLD);
        pause(2.6);
        bar("MinHeap: build + 25 deleteMin", 0, 25, Colors.GREEN);
        pause(1.2);
        narr("Build a MinHeap instead and delete only the 25 smaller ones: the 26th smallest is then the root.", Colors.GREEN);
        pause(3.0);
        List<MObject> ch = drop("use the smaller side: min(k, N - k + 1) deletions", Colors.GOLD, 0, 180, 900);
        mine.addAll(ch);
        pause(4.0);
    }

    private void bar(String name, double y, int n, Color c) {
        TextMob lab = label(name, -880, y - 50, 32, c, true, true);
        double w = n * 16;
        RectMob b = new RectMob(w, 44).setCornerRadius(8);
        b.setFillColor(Colors.withAlpha(c, 0.45));
        b.setStrokeColor(c);
        b.setStrokeWidth(2.6);
        b.setPosition(-880 + w / 2, y + 10);
        b.setOpacity(0);
        add(b);
        TextMob num = label(String.valueOf(n), -880 + w + 50, y + 10, 40, Colors.WHITE, false, true);
        play(new FadeIn(lab, d(0.4)));
        play(new GrowRight(b, -880, y + 10, w, 44, d(1.4)), new FadeInAt(num, 1.0, d(0.5)));
        mine.add(lab);
        mine.add(b);
        mine.add(num);
    }
}
