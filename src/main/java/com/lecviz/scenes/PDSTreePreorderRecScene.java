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
 * Standalone clip for the recursive half of slide 14 of the trees deck: preorder, recursively.
 *
 *   - the slide's recursive code types in, with a highlight bar that follows the line being executed
 *   - the tree A (B (E F) C D (G)) on the right: the visited node lights up, is written into the output
 *     strip the moment its print line runs, and turns green once all its children are done
 *   - a call stack on the left grows one frame per node going down and shrinks on the way back: its height
 *     is the depth of the node
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreePreorderRecScene extends PDSTreeClipBase {

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
        head = writeHeading("Preorder: Recursive");
        pause(0.5);
        code = new CodeBox(new String[]{
                "void Tree::preorder(PtrToNode rr) {",
                "    if (rr) {",
                "        rr->print();",
                "        for (auto child: rr->children)",
                "            preorder(child);",
                "    }",
                "}",
                "void Tree::preorder() {",
                "    preorder(root);",
                "}"}, -930, -330, 24, 36);
        code.typeIn(6.0);
        mine.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.3)));
        TextMob src = label("Source: 4.cpp, 6.cpp", 760, -470, 26, Colors.GRAY, false, false);
        play(new FadeIn(src, d(0.6)));
        mine.add(src);

        root = smallTree();
        t = new GT(root, 520, -300, 140, 110, 30, 30, false, null);
        t.build(0.3);
        mine.addAll(t.parts());
        cs = new CallStack(-620, 112, 560, 54);
        cs.showTitle();
        mine.addAll(cs.all());
        os = new OutStrip("output:", -900, 384, 56, Colors.GREEN);
        os.showTitle();
        mine.addAll(os.all());
        pause(0.6);

        say("preorder() starts at the root.", Colors.LIGHT_GRAY);
        hl(7);
        push("preorder()");
        hl(8);
        pause(0.5);
        call(root);
        pause(0.6);
        hl(9);
        cs.pop(null, null);
        mine.addAll(cs.all());
        mine.addAll(os.all());
        say("A B E F C D G: a node, then its children, one after the other.", Colors.GREEN);
        pause(3.0);
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
        push("preorder(" + n.name + ")");
        hl(0);
        hl(1);
        List<Animation> a = new ArrayList<>();
        t.paint(a, n, VISIT_C, d(0.3));
        if (n.edge != null) t.paintEdge(a, n, VISIT_C, d(0.3));
        playAll(a);
        hl(2);
        os.add(n.name);
        mine.addAll(os.all());
        if (calls == 1) say("The node is written first, then every child is walked in turn.", Colors.LIGHT_GRAY);
        pause(calls < 4 ? 0.5 : 0.2);
        for (GN c : n.kids) {
            hl(3);
            hl(4);
            call(c);
        }
        hl(5);
        List<Animation> b = new ArrayList<>();
        t.paint(b, n, DONE_C, d(0.3));
        if (n.edge != null) t.paintEdge(b, n, DONE_C, d(0.3));
        playAll(b);
        hl(6);
        cs.pop(null, null);
    }
}
