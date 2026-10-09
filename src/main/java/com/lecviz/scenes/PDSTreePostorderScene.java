package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Standalone clip for slide 16 of the trees deck: postorder.
 *
 *   - the slide's recursive code, a highlight bar on the line being executed; a call stack on the left; the
 *     tree A (B (E F) C D (G)) on the right: a node is written only after all its children have returned
 *   - the slide's iterative box says "Try it out offline": shown as a blank box, followed by a hint that
 *     is not on the slide (reverse a "node first, children right to left" walk)
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreePostorderScene extends PDSTreeClipBase {

    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();
    private CodeBox code;
    private GT t;
    private GN root;
    private CallStack cs;
    private OutStrip os;
    private final Color[] fc = {Colors.TEAL, Colors.BLUE, Colors.PINK, Colors.GOLD, Colors.PURPLE};
    private int calls;

    @Override
    public void construct() {
        head = writeHeading("Postorder");
        pause(0.5);
        code = new CodeBox(new String[]{
                "void Tree::postorder(PtrToNode rr) {",
                "    if (rr) {",
                "        for (auto child: rr->children)",
                "            postorder(child);",
                "        rr->print();",
                "    }",
                "}",
                "void Tree::postorder() {",
                "    postorder(root);",
                "}"}, -930, -330, 24, 36);
        code.typeIn(6.0);
        mine.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.3)));
        TextMob src = label("Source: 5.cpp", 800, -470, 26, Colors.GRAY, false, false);
        play(new FadeIn(src, d(0.6)));
        mine.add(src);
        root = smallTree();
        t = new GT(root, 520, -300, 140, 110, 30, 30, false, null);
        t.build(0.3);
        mine.addAll(t.parts());
        cs = new CallStack(-620, 112, 560, 54);
        cs.showTitle();
        mine.addAll(cs.all());
        os = new OutStrip("output:", -900, 384, 56, Colors.ORANGE);
        os.showTitle();
        mine.addAll(os.all());
        pause(0.6);
        say("The children come first; the node is written last.", Colors.LIGHT_GRAY);
        hl(7);
        push("postorder()");
        hl(8);
        pause(0.5);
        call(root);
        pause(0.6);
        hl(9);
        cs.pop(null, null);
        mine.addAll(os.all());
        say("E F B C G D A: every node comes after everything below it, the root is last.", Colors.ORANGE);
        pause(3.0);
        iterative();
        unsay();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void say(String s, Color c) { sayAt(s, c, 0, 446, 34); }

    private void hl(int line) { play(code.moveHl(line, d(calls < 4 ? 0.3 : 0.18))); }

    private void push(String text) {
        cs.push(text, fc[Math.min(cs.frames.size(), fc.length - 1)]);
        mine.addAll(cs.all());
    }

    private void call(GN n) {
        calls++;
        push("postorder(" + n.name + ")");
        hl(0);
        hl(1);
        List<Animation> a = new ArrayList<>();
        t.paint(a, n, VISIT_C, d(0.3));
        if (n.edge != null) t.paintEdge(a, n, VISIT_C, d(0.3));
        playAll(a);
        for (GN c : n.kids) {
            hl(2);
            hl(3);
            call(c);
        }
        hl(4);
        os.add(n.name);
        mine.addAll(os.all());
        if (calls == 3) say("A node is written only after all its children are done.", Colors.LIGHT_GRAY);
        List<Animation> b = new ArrayList<>();
        t.paint(b, n, DONE_C, d(0.3));
        if (n.edge != null) t.paintEdge(b, n, DONE_C, d(0.3));
        playAll(b);
        pause(calls < 4 ? 0.4 : 0.15);
        hl(5);
        hl(6);
        cs.pop(null, null);
    }

    // ── the slide's "try it out offline" ─────────────────────────────

    private void iterative() {
        unsay();
        List<Animation> out = new ArrayList<>();
        for (MObject m : code.parts()) out.add(new FadeOut(m, d(0.8)));
        for (MObject m : cs.all()) if (m.getOpacity() > 0) out.add(new FadeOut(m, d(0.8)));
        for (MObject m : os.all()) out.add(new FadeOut(m, d(0.8)));
        List<Animation> back = new ArrayList<>();
        for (GN n : t.nodes) {
            t.paintDefault(back, n, d(0.8));
            t.paintEdge(back, n, Colors.withAlpha(Colors.LIGHT_GRAY, 0.8), d(0.8));
        }
        playAll(out);
        playAll(back);
        pause(0.3);
        TextMob h = label("Iterative", -520, -290, 40, Colors.ORANGE, false, true);
        RectMob box = panel(-520, -120, 600, 300, Colors.GRAY, 0.08);
        TextMob tr = label("Try it out offline.", -520, -120, 36, Colors.LIGHT_GRAY, false, false);
        play(new FadeIn(h, d(0.6)), new FadeIn(box, d(0.6)), new FadeIn(tr, d(0.6)));
        mine.add(h);
        mine.add(box);
        mine.add(tr);
        pause(2.4);

        // a hint that is not on the slide
        StrokeTextMob hint = stroke("Hint (not on the slide): reverse a walk that goes node first, children right to left.", 0, 195, 34, Colors.GOLD, false);
        play(new Write(hint, d(4.0)));
        mine.add(hint);
        String[] pre = {"A", "D", "G", "C", "B", "F", "E"};
        OutStrip s1 = new OutStrip("node first, right to left:", -900, 290, 56, Colors.TEAL);
        s1.showTitle();
        mine.addAll(s1.all());
        for (String s : pre) {
            s1.add(s);
            pause(0.1);
        }
        mine.addAll(s1.all());
        pause(0.8);
        OutStrip s2 = new OutStrip("reversed:", -900, 380, 56, Colors.ORANGE);
        s2.showTitle();
        mine.addAll(s2.all());
        List<String> rev = new ArrayList<>(List.of(pre));
        Collections.reverse(rev);
        for (String s : rev) {
            s2.add(s);
            pause(0.18);
        }
        mine.addAll(s2.all());
        pause(0.6);
        sayAt("E F B C G D A: exactly the postorder.", Colors.GREEN, 0, 460, 34);
        pause(3.0);
    }
}
