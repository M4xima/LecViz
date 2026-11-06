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
 * Standalone clip for slide 15 of the priority-queue deck: what a good code looks like (the three observations that
 * give Huffman's algorithm).
 *
 *   - the table of the slide (five letters, their frequencies and the three codes) drops in row by row, then clears
 *   - code 3 as a tree (0 = left edge, 1 = right edge) with the slide's three observations beside it, each shown on the
 *     tree: the depth of a leaf is the length of its code; the more frequent letters sit nearer the root; the common
 *     subcode 01 leads to o and i together, so it stands for their joint frequency 7.68 + 7.31 = 14.99
 *   - "The above observations give us the algorithm": the card that leads to the next clip
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSPQHuffmanIdeaScene extends PDSTreeClipBase {

    private final List<MObject> mine = new ArrayList<>();
    private GT t;
    private GN root, gE, gT, gA, gO, gI, gEio, gAt;

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("Application: Huffman Coding");
        pause(0.5);
        table();
        tree();
        points();
        unsay();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void narr(String s, Color c) { sayAt(s, c, 0, 470, 34); }

    private void table() {
        String[][] rows = {
                {"e", "12.02", "0", "0", "00"},
                {"t", "9.10", "1", "10", "10"},
                {"a", "8.12", "00", "110", "11"},
                {"o", "7.68", "01", "1110", "010"},
                {"i", "7.31", "10", "1111", "011"}};
        Color[][] tint = new Color[5][5];
        for (int r = 0; r < 5; r++) tint[r][4] = Colors.TEAL;
        List<MObject> tb = dropTable(new String[]{"Character", "Frequency", "Code", "Code 2", "Code 3"}, rows,
                new double[]{230, 270, 200, 230, 230}, tint, -330);
        mine.addAll(tb);
        narr("The five letters and three codes of the previous slide. Code 3 is the one we will look at.", Colors.LIGHT_GRAY);
        pause(3.0);
        fadeOutAll(d(1.0), tb);
        mine.removeAll(tb);
        unsay();
        pause(0.3);
    }

    private void tree() {
        gE = bin("e");
        gT = bin("t");
        gA = bin("a");
        gO = bin("o");
        gI = bin("i");
        gEio = bin("", gE, bin("", gO, gI));
        gAt = bin("", gT, gA);
        root = bin("", gEio, gAt);
        t = new GT(root, 560, -230, 120, 70, 34, 30, false,
                n -> n.parent == null ? Colors.MAROON : n.name.isEmpty() ? Colors.GRAY : Colors.BLUE);
        t.build(0.3);
        mine.addAll(t.parts());
        List<Animation> a = new ArrayList<>();
        for (GN n : t.nodes) {
            if (n.parent == null) continue;
            TextMob et = t.edgeTag(n, n.slot == 0 ? "0" : "1", n.slot == 0 ? Colors.TEAL : Colors.ORANGE, 24);
            a.add(new FadeIn(et, d(0.5)));
            mine.add(et);
        }
        playAll(a);
        TextMob nm = label("Code 3", 560, -330, 34, Colors.GOLD, false, true);
        play(new FadeIn(nm, d(0.5)));
        mine.add(nm);
        pause(0.6);
    }

    private void points() {
        // 1. depth = code length
        List<Spec> s1 = new ArrayList<>();
        s1.add(ln(0, "Characters with smaller depths have shorter codes.").kw("shorter codes", Colors.GREEN));
        List<List<MObject>> g1 = writeSlide(s1, -170);
        String[] code = {"00", "010", "011", "10", "11"};
        GN[] leaves = {gE, gO, gI, gT, gA};
        List<Animation> an = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Color c = leaves[i].depth == 2 ? Colors.GREEN : Colors.ORANGE;
            t.paint(an, leaves[i], c, d(0.5));
            TextMob tg = t.tag(leaves[i], code[i], c, 28, 0, 60);
            an.add(new FadeIn(tg, d(0.6)));
            mine.add(tg);
        }
        playAll(an);
        narr("e, t and a are at depth 2 and have 2-bit codes; o and i are at depth 3 and have 3-bit codes.", Colors.GREEN);
        pause(3.2);

        // 2. frequent letters near the root
        List<Spec> s2 = new ArrayList<>();
        s2.add(ln(0, "Characters with higher frequencies should be near"));
        s2.add(ln(3, "the root.").kw("the root", Colors.GOLD));
        List<List<MObject>> g2 = writeSlide(s2, -60);
        String[] fr = {"12.02", "7.68", "7.31", "9.10", "8.12"};
        List<Animation> fa = new ArrayList<>();
        double[] fdx = {-64, -64, 64, -64, 64};
        for (int i = 0; i < 5; i++) {
            TextMob tg = t.tag(leaves[i], fr[i], Colors.LIGHT_GRAY, 24, fdx[i], 4);
            fa.add(new FadeIn(tg, d(0.6)));
            mine.add(tg);
        }
        playAll(fa);
        narr("The frequent letters e, t and a (12.02, 9.10, 8.12) are at depth 2; the rare o and i (7.68, 7.31) are deeper.", Colors.GOLD);
        pause(3.6);

        // 3. a common subcode is a joint frequency
        List<Spec> s3 = new ArrayList<>();
        s3.add(ln(0, "Common subcode for characters indicates joint"));
        s3.add(ln(3, "frequency (e.g., 01 indicates both o and i)").kw("01", Colors.PINK));
        List<List<MObject>> g3 = writeSlide(s3, 80);
        GN io = gEio.kids.get(1);
        RectMob box = subBox(io, Colors.PINK, 40, 52, 22);
        play(new FadeIn(box, d(0.6)));
        mine.add(box);
        TextMob jf = label("01 → o or i:  7.68 + 7.31 = 14.99", 560, 290, 32, Colors.PINK, false, true);
        play(new FadeIn(jf, d(0.7)));
        mine.add(jf);
        narr("Every code that starts with 01 is o or i: the node 01 stands for their joint frequency, 14.99.", Colors.PINK);
        pause(3.8);

        // 4. the algorithm
        List<Spec> s4 = new ArrayList<>();
        s4.add(ln(0, "The above observations give us the algorithm.").kw("algorithm", Colors.TEAL));
        List<List<MObject>> g4 = writeSlide(s4, 220);
        pause(2.6);
        unsay();
        List<List<MObject>> all = new ArrayList<>();
        all.addAll(g1);
        all.addAll(g2);
        all.addAll(g3);
        all.addAll(g4);
        swipeAway(all);
    }
}
