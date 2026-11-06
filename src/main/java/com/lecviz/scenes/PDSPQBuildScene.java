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
 * Standalone clip for the second half of slide 12 of the priority-queue deck: build.
 *
 *   - the slide's lines about build in the pen-stroke style: N inserts give O(N log N), a tighter analysis gives O(N)
 *   - the buildHeap of 15 numbers in any order, done by percolating down from the last parent (index N / 2) to the
 *     root; the swaps are counted
 *   - the tighter analysis: every node is tagged with its height (leaves 0, root 3), the heights add up to 11 for 15
 *     nodes, and the slide's derivation S = sum of 2^i (h - i) = (2^(h+1) - 1) - (h + 1) is written line by line,
 *     ending with: h = log N, so building takes O(N)
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSPQBuildScene extends PDSHeapClipBase {

    private final List<MObject> mine = new ArrayList<>();

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("Other Operations: build");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "build").kw("build", Colors.TEAL));
        s.add(ln(1, "N elements with max-height as log N leads to the complexity of O(N log N)").kw("O(N log N)", Colors.RED));
        s.add(ln(1, "A tighter analysis reflects this is O(N).").kw("O(N)", Colors.GREEN));
        List<List<MObject>> text = writeSlide(s, -250);
        pause(1.8);
        swipeAway(text);
        pause(0.4);

        HV h = new HV(-390, -345, 720, 125, 34, 30, 4, true, -820, 190, 62, 60, 16);
        h.build(new int[]{4, 17, 9, 25, 14, 31, 6, 38, 22, 47, 11, 19, 2, 8, 28}, 0.25);
        h.showFrame(d(0.5));
        mine.addAll(h.parts());
        pause(0.5);

        cmpNum = label("0", 450, 265, 84, Colors.WHITE, false, true);
        moveNum = label("0", 760, 265, 84, Colors.WHITE, false, true);
        TextMob c1 = label("comparisons", 450, 335, 28, Colors.GRAY, false, true);
        TextMob c2 = label("swaps", 760, 335, 28, Colors.GRAY, false, true);
        TextMob t1 = label("buildHeap: N numbers in any order", 600, -300, 30, Colors.TEAL, false, true);
        TextMob t2 = label("percolate down from index N / 2 to the root", 600, -245, 26, Colors.LIGHT_GRAY, false, false);
        play(new FadeIn(cmpNum, d(0.4)), new FadeIn(moveNum, d(0.4)), new FadeIn(c1, d(0.4)), new FadeIn(c2, d(0.4)),
                new FadeIn(t1, d(0.5)), new FadeIn(t2, d(0.5)));
        mine.add(cmpNum);
        mine.add(moveNum);
        mine.add(c1);
        mine.add(c2);
        mine.add(t1);
        mine.add(t2);
        pause(0.6);
        narr("15 numbers in no particular order. The leaves are heaps already, so start at the last parent, index 7.", Colors.LIGHT_GRAY);
        pause(2.0);
        buildHeap(h, 0.5, true);
        h.paint(h.a[1], OK_C, d(0.4));
        narr("A heap in " + moves() + " swaps for 15 elements: fewer swaps than N.", Colors.GREEN);
        pause(2.6);
        List<Animation> back = new ArrayList<>();
        h.paintAnim(back, h.a[1], NODE_C, d(0.4));
        playAll(back);

        analysis(h, t1, t2);

        unsay();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void analysis(HV h, TextMob t1, TextMob t2) {
        narr("Why O(N)? N inserts cost up to log N each, but here a node only moves as far as its height.", Colors.LIGHT_GRAY);
        List<Animation> out = new ArrayList<>();
        out.add(new FadeOut(t1, d(0.5)));
        out.add(new FadeOut(t2, d(0.5)));
        playAll(out);
        // the height of every node
        List<TextMob> tags = new ArrayList<>();
        List<Animation> an = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            int ht = 3 - (int) h.lv(i);
            TextMob t = label(String.valueOf(ht), h.px(i) + 36, h.py(i) - 30, 26, Colors.GOLD, false, true);
            an.add(new FadeIn(t, d(0.6)));
            tags.add(t);
            mine.add(t);
        }
        playAll(an);
        pause(1.6);
        narr("Gold: the height of each node. 1 node of height 3, 2 of height 2, 4 of height 1, 8 leaves of height 0.", Colors.GOLD);
        pause(2.6);
        TextMob sum = label("S = 1×3 + 2×2 + 4×1 + 8×0 = 11", 600, -300, 30, Colors.GOLD, false, true);
        play(new FadeIn(sum, d(0.7)));
        mine.add(sum);
        pause(1.2);
        narr("The sum S of all heights is only 11, less than N = 15. In general:", Colors.GOLD);
        pause(2.0);
        String[] eq = {
                "S = \\sum_{i=0}^{h} 2^{i}\\,(h-i)",
                "S = h + 2(h-1) + 4(h-2) + \\cdots + 2^{h-1}",
                "2S = 2h + 4(h-1) + 8(h-2) + \\cdots + 2^{h}",
                "2S - S = -h + 2 + 4 + 8 + \\cdots + 2^{h}",
                "S = (2^{h+1} - 1) - (h + 1)"};
        String[] why = {
                "2^i nodes sit at height h - i",
                "write out the terms",
                "multiply by 2",
                "subtract: most terms cancel",
                "a geometric sum"};
        double y = -225;
        for (int k = 0; k < eq.length; k++) {
            LaTeXMob f = latex(eq[k], 36, 600, y);
            play(new FadeIn(f, d(0.8)));
            mine.add(f);
            narr(why[k], Colors.LIGHT_GRAY);
            pause(1.5);
            y += 78;
        }
        List<MObject> ch = chip("h = log N, so S = O(N)", 600, 175, 520, 72, Colors.GREEN, 36);
        List<Animation> dn = new ArrayList<>();
        for (MObject m : ch) dn.add(new DropIn(m, 40, 0, d(0.6)));
        playAll(dn);
        mine.addAll(ch);
        narr("The total work is at most the sum of the heights, so building a heap takes O(N), not O(N log N).", Colors.GREEN);
        pause(4.0);
    }
}
