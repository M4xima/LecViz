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
 * Standalone clip for the iterative half of slide 14 of the trees deck: preorder with an explicit stack.
 *
 *   - the slide's iterative code types in, a highlight bar follows the line being executed
 *   - the tree A (B (E F) C D (G)) on the right and a stack drawn between the code and the tree: push the
 *     root; then pop a node, write it, push its children
 *   - because a stack gives back the last thing pushed, the children come out right to left (A D G C B F E):
 *     still a valid preorder, since the slide allows any order of the children
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreePreorderIterScene extends PDSTreeClipBase {

    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();
    private CodeBox code;
    private GT t;
    private GN root;
    private OutStrip os;
    private VStack vs;
    private final java.util.Map<String, Color> col = new java.util.HashMap<>();

    @Override
    public void construct() {
        head = writeHeading("Preorder: Iterative");
        pause(0.5);
        code = new CodeBox(new String[]{
                "void Tree::preorder() {",
                "    std::stack<PtrToNode> stack;",
                "    stack.push(root);",
                "    while (!stack.empty()) {",
                "        PtrToNode rr = stack.top();",
                "        stack.pop();",
                "        if (rr) {",
                "            rr->print();",
                "            for (auto child: rr->children)",
                "                stack.push(child);",
                "        }",
                "    }",
                "}"}, -930, -330, 24, 34);
        code.typeIn(6.5);
        mine.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.3)));
        TextMob src = label("Source: 4.cpp, 6.cpp", 760, -470, 26, Colors.GRAY, false, false);
        play(new FadeIn(src, d(0.6)));
        mine.add(src);

        root = smallTree();
        t = new GT(root, 560, -300, 140, 110, 30, 30, false, null);
        t.build(0.3);
        mine.addAll(t.parts());
        vs = new VStack(60, 330, 200, 50, 5, "top", 1);
        vs.showBox();
        vs.showTop();
        mine.addAll(vs.parts());
        os = new OutStrip("output:", -900, 250, 56, Colors.GREEN);
        os.showTitle();
        mine.addAll(os.all());
        Color[] cs = {Colors.MAROON, Colors.BLUE, Colors.TEAL, Colors.GOLD, Colors.PINK, Colors.PURPLE, Colors.GREEN};
        List<GN> all = new ArrayList<>();
        preorderInto(root, all);
        for (int i = 0; i < all.size(); i++) col.put(all.get(i).name, cs[i % cs.length]);
        pause(0.6);

        say("An explicit stack replaces the call stack.", Colors.LIGHT_GRAY);
        hl(1);
        pause(0.6);
        hl(2);
        pushNode(root);
        pause(0.5);
        int round = 0;
        while (!vs.items.isEmpty()) {
            round++;
            hl(3);
            hl(4);
            GN rr = nodeOf(vs.top());
            List<Animation> a = new ArrayList<>();
            t.paint(a, rr, VISIT_C, d(0.3));
            playAll(a);
            hl(5);
            vs.popAway();
            hl(6);
            hl(7);
            os.add(rr.name);
            mine.addAll(os.all());
            pause(round < 4 ? 0.5 : 0.2);
            for (GN c : rr.kids) {
                hl(8);
                hl(9);
                pushNode(c);
            }
            List<Animation> b = new ArrayList<>();
            t.paint(b, rr, DONE_C, d(0.3));
            playAll(b);
            if (round == 2) say("The last child pushed is the first one popped: children come out right to left.", Colors.ORANGE);
        }
        hl(3);
        say("The stack is empty: A D G C B F E. The slide allows the children in any order.", Colors.GREEN);
        pause(3.2);
        unsay();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private GN nodeOf(Cell c) {
        List<GN> all = new ArrayList<>();
        preorderInto(root, all);
        for (GN n : all) if (n.name.equals(c.text.getText())) return n;
        return null;
    }

    private void pushNode(GN n) {
        Cell c = vs.push(n.name, col.get(n.name));
        mine.addAll(vs.parts());
    }

    private void say(String s, Color c) { sayAt(s, c, 0, 425, 34); }

    private void hl(int line) { play(code.moveHl(line, d(0.2))); }
}
