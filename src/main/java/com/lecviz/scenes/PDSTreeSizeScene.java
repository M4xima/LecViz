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
 * Standalone clip for slide 15 of the trees deck: the full size of every directory.
 *
 *   - the directory tree, every file or folder counts as 1 (this is how the slide's numbers come out:
 *     "third 2" is third itself plus its readme)
 *   - the rule size(v) = 1 + the sum of the sizes of v's children
 *   - a postorder walk: children before the parent, because a directory's size needs the sizes below it;
 *     each node gets its size as a red badge the moment the walk leaves it, with the sum written out
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeSizeScene extends PDSTreeClipBase {

    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();
    private GT t;
    private GN root;
    private final Map<GN, Integer> size = new HashMap<>();

    @Override
    public void construct() {
        head = writeHeading("Find full size of each directory");
        pause(0.5);
        root = dirTree();
        t = new GT(root, 0, -370, 100, 16, 22, 22, true,
                n -> n.parent == null ? Colors.GOLD : PDSTreeDirScene.DIRS.contains(n.name) ? Colors.BLUE : Colors.GRAY);
        t.build(0.3);
        mine.addAll(t.parts());
        pause(0.6);
        say("Count every file and folder as 1. A folder's size includes everything below it.", Colors.LIGHT_GRAY);
        pause(2.2);
        LaTeXMob f = latex("\\mathrm{size}(v) = 1 + \\sum_{c} \\mathrm{size}(c)", 52, 0, 318);
        play(new FadeIn(f, d(0.8)));
        mine.add(f);
        pause(1.6);
        say("A folder needs its children's sizes first: children before the parent, a postorder walk.", Colors.ORANGE);
        pause(2.4);
        play(new FadeOut(f, d(0.6)));

        List<GN> post = new ArrayList<>();
        postorderInto(root, post);
        int k = 0;
        for (GN n : post) {
            int sum = 1;
            StringBuilder eq = new StringBuilder(n.name + " = 1");
            for (GN c : n.kids) {
                sum += size.get(c);
                eq.append(" + ").append(size.get(c));
            }
            size.put(n, sum);
            if (!n.leaf()) eq.append(" = ").append(sum);
            double slow = k < 6 ? 0.6 : (k < 14 ? 0.3 : 0.18);
            List<Animation> a = new ArrayList<>();
            t.paint(a, n, VISIT_C, d(0.25));
            playAll(a);
            equation(eq.toString());
            badge2(n, sum);
            pause(slow);
            List<Animation> b = new ArrayList<>();
            t.paint(b, n, DONE_C, d(0.25));
            playAll(b);
            k++;
        }
        pause(0.6);
        say("The root says 29: every file and folder is counted once. These are the numbers on the slide.", Colors.GREEN);
        List<Animation> hi = new ArrayList<>();
        t.paint(hi, root, Colors.GOLD, d(0.5));
        playAll(hi);
        pause(3.4);
        fadeOutAll(d(1.2), mine);
        unsay();
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private TextMob eqLab;

    private void equation(String s) {
        TextMob old = eqLab;
        TextMob e = label(s, 0, 345, 34, Colors.WHITE, false, true);
        List<Animation> a = new ArrayList<>();
        a.add(new FadeIn(e, d(0.2)));
        if (old != null) a.add(new FadeOut(old, d(0.2)));
        playAll(a);
        if (old != null) remove(old);
        eqLab = e;
        mine.add(e);
    }

    private void badge2(GN n, int v) {
        List<MObject> ch = chip(String.valueOf(v), n.x + n.hw - 4, n.y - 30, v > 9 ? 44 : 34, 30, Colors.RED, 22);
        List<Animation> a = new ArrayList<>();
        for (MObject m : ch) a.add(new FadeIn(m, d(0.3)));
        playAll(a);
        mine.addAll(ch);
    }

    private void say(String s, Color c) { sayAt(s, c, 0, 420, 34); }
}
