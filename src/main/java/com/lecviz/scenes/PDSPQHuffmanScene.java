package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Standalone clip for slide 16 of the priority-queue deck: the Huffman coding algorithm.
 *
 *   - the slide's four steps and three notes in the pen-stroke style (with the source note), then they swipe away
 *   - the five frequencies sit in a MinHeap (tree and array) on the left, the five letters as leaves on the right; the
 *     four rounds of the algorithm are run for real: deleteMin twice (the two lowest), the two leaves or subtrees are
 *     joined under a new node holding the sum, and the sum is inserted in the heap (7.31 + 7.68 = 14.99, 8.12 + 9.10 =
 *     17.22, 12.02 + 14.99 = 27.01, 17.22 + 27.01 = 44.23); one frequency, 44.23, remains: the root
 *   - the edges are labelled 0 (left) and 1 (right) and the codes are read off: e 00, t 10, a 11, o 010, i 011, which is
 *     code 3 of the previous slides; the average code length is about 2.34 bits per letter
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSPQHuffmanScene extends PDSHeapClipBase {

    private final List<MObject> mine = new ArrayList<>();
    private HV h;
    private GT t;
    private GN root, gE, gT, gA, gO, gI, gIO, gAT, gEIO;
    private final Map<Integer, GN> byKey = new HashMap<>();
    private StrokeTextMob head;

    @Override
    public void construct() {
        head = writeHeading("Huffman Coding Algorithm");
        pause(0.5);
        slide();
        setup();
        round(1, "i", "o", gIO, 1499, "14.99");
        round(2, "a", "t", gAT, 1722, "17.22");
        round(3, "e", "io", gEIO, 2701, "27.01");
        finalRound();
        codes();
        unsay();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    // ── the slide ────────────────────────────────────────────────────

    private void slide() {
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "(1) Consider the lowest two frequencies.").kw("lowest", Colors.RED));
        s.add(ln(0, "(2) Combine to form a new frequency equal to the sum"));
        s.add(ln(3, "of the two."));
        s.add(ln(0, "(3) Remove the two frequencies, add the summed"));
        s.add(ln(3, "frequency."));
        s.add(ln(0, "(4) Repeat step (1) until a single frequency remains."));
        s.add(ln(1, "The processing leads to a binary tree (called Huffman Tree)."));
        s.add(ln(1, "Finding the lowest frequency can be nicely done using Heaps.").kw("Heaps", Colors.GOLD));
        s.add(ln(1, "Huffman coding is used in compression algorithms (such as gzip and winzip)"));
        s.add(ln(2, "and stream formats (such as jpeg and mp3)."));
        List<List<MObject>> text = writeSlide(s, -340);
        TextMob src = label("Source: heap-huffman.cpp", 600, 300, 28, Colors.GOLD, false, true);
        play(new FadeIn(src, d(0.7)));
        List<MObject> sg = new ArrayList<>();
        sg.add(src);
        text.add(sg);
        pause(2.0);
        swipeAway(text);
        pause(0.4);
    }

    // ── the heap and the leaves ──────────────────────────────────────

    private void setup() {
        gE = bin("e");
        gT = bin("t");
        gA = bin("a");
        gO = bin("o");
        gI = bin("i");
        gIO = bin("14.99", gO, gI);
        gAT = bin("17.22", gT, gA);
        gEIO = bin("27.01", gE, gIO);
        root = bin("44.23", gEIO, gAT);
        t = new GT(root, 430, -300, 130, 50, 34, 26, false, null);
        mine.addAll(t.parts());
        byKey.put(1202, gE);
        byKey.put(910, gT);
        byKey.put(812, gA);
        byKey.put(768, gO);
        byKey.put(731, gI);
        byKey.put(1499, gIO);
        byKey.put(1722, gAT);
        byKey.put(2701, gEIO);
        byKey.put(4423, root);

        TextMob hl = label("MinHeap of the frequencies", -560, -400, 28, Colors.TEAL, false, true);
        TextMob tl = label("Huffman tree", 430, -400, 28, Colors.GOLD, false, true);
        play(new FadeIn(hl, d(0.5)), new FadeIn(tl, d(0.5)));
        mine.add(hl);
        mine.add(tl);

        h = new HV(-560, -330, 440, 110, 36, 22, 3, true, -830, 30, 66, 56, 8);
        h.min = true;
        h.cellsOn = true;
        String[] nm = {"7.31", "7.68", "8.12", "9.10", "12.02"};
        int[] key = {731, 768, 812, 910, 1202};
        List<Animation> an = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            El e = h.makeLabeled(key[i], nm[i], i + 1, NODE_C);
            h.showAnim(an, e, 0.35 * i, d(0.6));
        }
        h.n = 5;
        h.showFrame(d(0.6));
        mine.addAll(h.parts());
        // the five letters as leaves, each with its frequency
        GN[] lv = {gE, gT, gA, gO, gI};
        String[] fq = {"12.02", "9.10", "8.12", "7.68", "7.31"};
        for (int i = 0; i < 5; i++) {
            an.add(new FadeInAt(lv[i].shape, 0.35 * i, d(0.6)));
            an.add(new FadeInAt(lv[i].text, 0.35 * i, d(0.6)));
            TextMob tg = t.tag(lv[i], fq[i], Colors.LIGHT_GRAY, 24, 0, 52);
            an.add(new FadeInAt(tg, 0.35 * i, d(0.6)));
            mine.add(tg);
        }
        playAll(an);
        pause(0.6);
        narr("The five frequencies in a MinHeap, and the five letters as one-node trees.", Colors.LIGHT_GRAY);
        pause(2.4);
    }

    private void join(GN parent, GN c1, GN c2) {
        List<Animation> an = new ArrayList<>();
        an.add(new FadeIn(parent.shape, d(0.6)));
        an.add(new FadeIn(parent.text, d(0.6)));
        an.add(new DrawLinkAt(c1.edge, 0, d(0.7)));
        an.add(new DrawLinkAt(c2.edge, 0, d(0.7)));
        playAll(an);
    }

    private void round(int r, String a, String b, GN parent, int sumKey, String sumText) {
        boolean slow = r == 1;
        double sp = slow ? 0.8 : 0.45;
        narr("Step (1): the lowest two frequencies are in the heap's root: deleteMin twice.", Colors.LIGHT_GRAY);
        pause(slow ? 1.4 : 0.8);
        int k1 = heapDeleteMax(h, sp, slow);
        GN n1 = byKey.get(k1);
        t.paintNow(n1, VISIT_C, d(0.4));
        int k2 = heapDeleteMax(h, sp, slow);
        GN n2 = byKey.get(k2);
        t.paintNow(n2, VISIT_C, d(0.4));
        pause(0.4);
        narr("Step (2): combine them into a new frequency, the sum: " + fmt(k1) + " + " + fmt(k2) + " = " + sumText + ".", Colors.GOLD);
        join(parent, parent.kids.get(0), parent.kids.get(1));
        List<Animation> pa = new ArrayList<>();
        t.paint(pa, n1, DONE_C, d(0.4));
        t.paint(pa, n2, DONE_C, d(0.4));
        t.paint(pa, parent, VISIT_C, d(0.4));
        playAll(pa);
        pause(slow ? 1.6 : 0.9);
        narr("Step (3): add the summed frequency, " + sumText + ", to the heap.", Colors.PINK);
        heapInsert(h, sumKey, sumText, sp, slow);
        pause(slow ? 1.2 : 0.7);
        h.paint(h.a[findIndex(sumKey)], NODE_C, d(0.3));
        narr("Step (4): more than one frequency remains: repeat.", Colors.LIGHT_GRAY);
        pause(slow ? 1.4 : 0.8);
    }

    private static String fmt(int key) { return String.format("%d.%02d", key / 100, key % 100); }

    private int findIndex(int key) {
        for (int i = 1; i <= h.n; i++) if (h.a[i] != null && h.a[i].v == key) return i;
        return 1;
    }

    private void finalRound() {
        narr("Round 4: the two frequencies left are 17.22 and 27.01.", Colors.LIGHT_GRAY);
        pause(1.0);
        int k1 = heapDeleteMax(h, 0.45, false);
        t.paintNow(byKey.get(k1), VISIT_C, d(0.4));
        int k2 = heapDeleteMax(h, 0.45, false);
        t.paintNow(byKey.get(k2), VISIT_C, d(0.4));
        pause(0.3);
        narr("17.22 + 27.01 = 44.23: the last sum.", Colors.GOLD);
        join(root, gEIO, gAT);
        List<Animation> pa = new ArrayList<>();
        t.paint(pa, gEIO, DONE_C, d(0.4));
        t.paint(pa, gAT, DONE_C, d(0.4));
        t.paint(pa, root, VISIT_C, d(0.4));
        playAll(pa);
        pause(1.0);
        heapInsert(h, 4423, "44.23", 0.5, false);
        pause(0.6);
        narr("A single frequency, 44.23, remains: it is the root of the Huffman tree.", Colors.GREEN);
        List<Animation> ra = new ArrayList<>();
        t.paint(ra, root, Colors.MAROON, d(0.6));
        h.paintAnim(ra, h.a[1], OK_C, d(0.6));
        playAll(ra);
        pause(2.8);
    }

    // ── the codes ────────────────────────────────────────────────────

    private void codes() {
        narr("Label the left edges 0 and the right edges 1 and read the code of each letter from the root.", Colors.LIGHT_GRAY);
        List<Animation> a = new ArrayList<>();
        for (GN n : t.nodes) {
            if (n.parent == null) continue;
            TextMob et = t.edgeTag(n, n.slot == 0 ? "0" : "1", n.slot == 0 ? Colors.TEAL : Colors.ORANGE, 24);
            a.add(new FadeIn(et, d(0.6)));
            mine.add(et);
        }
        playAll(a);
        pause(1.6);
        GN[] lv = {gE, gO, gI, gT, gA};
        String[] code = {"00", "010", "011", "10", "11"};
        List<Animation> ca = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            t.paint(ca, lv[i], Colors.GOLD, d(0.5));
            TextMob tg = t.tag(lv[i], code[i], Colors.GOLD, 30, 0, 96);
            ca.add(new FadeIn(tg, d(0.6)));
            mine.add(tg);
        }
        playAll(ca);
        narr("e = 00, t = 10, a = 11, o = 010, i = 011: this is exactly code 3 of the earlier slides.", Colors.GOLD);
        pause(3.2);
        List<MObject> ch = chip("average: 2.34 bits per letter", 430, 330, 640, 70, Colors.GREEN, 34);
        List<Animation> dn = new ArrayList<>();
        for (MObject m : ch) dn.add(new DropIn(m, 40, 0, d(0.6)));
        playAll(dn);
        mine.addAll(ch);
        narr("No prefix code does better for these frequencies, and the heap found the lowest two each time in O(log N).", Colors.GREEN);
        pause(4.0);
    }
}
