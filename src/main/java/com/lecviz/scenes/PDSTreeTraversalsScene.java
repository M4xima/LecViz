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
 * Standalone clip for slide 13 of the trees deck: traversals.
 *
 *   - the slide's points one line at a time
 *   - preorder on the tree A..N: a gold marker walks the tree, every node is written the moment it is
 *     reached, the walk speeds up as it goes; then the same walk taking the children right to left, to
 *     show that "any order" is allowed
 *   - postorder: the same walk, but a node is written only when the walk leaves it
 *   - depth-first vs breadth-first: D's children are finished before its sibling E (depth first), then the
 *     level-order sweep row by row
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeTraversalsScene extends PDSTreeClipBase {

    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();
    private GT t;
    private GN root;
    private OutStrip os;
    private final List<MObject> tags = new ArrayList<>();
    private int step;

    @Override
    public void construct() {
        head = writeHeading("Traversals");
        pause(0.5);
        slideText();
        root = idTree();
        t = new GT(root, 0, -330, 120, 56, 24, 26, true, null);
        t.build(0.3);
        mine.addAll(t.parts());
        pause(0.5);
        preorder();
        postorder();
        levelOrder();
        unsay();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void slideText() {
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Preorder"));
        s.add(ln(1, "Process each node before processing its children.").kw("before", Colors.GREEN));
        s.add(ln(1, "Children can be processed in any order."));
        s.add(ln(0, "Postorder"));
        s.add(ln(1, "Process each node after processing its children.").kw("after", Colors.ORANGE));
        s.add(ln(1, "Children can be processed in any order."));
        s.add(ln(0, "Preorder and postorder are examples of Depth-First Traversal.").kw("Depth-First Traversal", Colors.BLUE));
        s.add(ln(1, "Children of a node are processed before processing its siblings."));
        s.add(ln(1, "The other way is called Breadth-First or Level-Order Traversal.").kw("Breadth-First", Colors.TEAL).kw("Level-Order", Colors.TEAL));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(1.4);
        swipeAway(text);
        pause(0.4);
    }

    private void say(String s, Color c) { sayAt(s, c, 0, 395, 36); }

    private double gap() { return Math.max(0.12, 0.75 - 0.055 * step); }

    private void number(GN n, boolean right) {
        step++;
        os.add(n.name);
        TextMob tg = t.tag(n, String.valueOf(step), Colors.GOLD, 28, 34, -30);
        play(new FadeIn(tg, d(0.3)));
        tags.add(tg);
    }

    private void enter(GN n, boolean pre) {
        List<Animation> a = new ArrayList<>();
        t.paint(a, n, VISIT_C, d(0.3));
        if (n.edge != null) t.paintEdge(a, n, VISIT_C, d(0.3));
        playAll(a);
        if (pre) number(n, false);
        pause(gap());
    }

    private void leave(GN n, boolean pre, boolean reverseKids) {
        List<Animation> a = new ArrayList<>();
        t.paint(a, n, DONE_C, d(0.3));
        if (n.edge != null) t.paintEdge(a, n, DONE_C, d(0.3));
        playAll(a);
        if (!pre) number(n, false);
        pause(gap() * 0.6);
    }

    private void walk(GN n, boolean pre, boolean reverse) {
        enter(n, pre);
        List<GN> kids = new ArrayList<>(n.kids);
        if (reverse) java.util.Collections.reverse(kids);
        for (GN c : kids) walk(c, pre, reverse);
        leave(n, pre, reverse);
    }

    private void reset(double dur) {
        List<Animation> a = new ArrayList<>();
        for (GN n : t.nodes) {
            t.paintDefault(a, n, dur);
            t.paintEdge(a, n, Colors.withAlpha(Colors.LIGHT_GRAY, 0.8), dur);
        }
        for (MObject m : tags) a.add(new FadeOut(m, dur));
        playAll(a);
        for (MObject m : tags) remove(m);
        tags.clear();
        step = 0;
    }

    private void preorder() {
        os = new OutStrip("preorder:", -900, 250, 56, Colors.GREEN);
        os.showTitle();
        mine.addAll(os.all());
        say("Preorder: write a node first, then walk into its children.", Colors.LIGHT_GRAY);
        pause(0.8);
        step = 0;
        walk(root, true, false);
        mine.addAll(os.all());
        say("A B C D H I E F J M N K L G: each node comes before everything below it.", Colors.GREEN);
        pause(2.4);
        reset(d(0.8));
        os.clear();
        say("Children in any order: take them right to left and the output changes.", Colors.ORANGE);
        pause(1.0);
        step = 0;
        walk(root, true, true);
        mine.addAll(os.all());
        say("A G F L K J N M E D I H C B: a different order, still a preorder.", Colors.ORANGE);
        pause(2.6);
        reset(d(0.8));
        os.clear();
    }

    private void postorder() {
        mine.removeAll(os.all());
        fadeOutAll(d(0.5), os.title);
        os = new OutStrip("postorder:", -900, 250, 56, Colors.ORANGE);
        os.showTitle();
        mine.addAll(os.all());
        say("Postorder: write a node only after all its children are done.", Colors.LIGHT_GRAY);
        pause(0.8);
        step = 0;
        walk(root, false, false);
        mine.addAll(os.all());
        say("B C H I D E M N J K L F G A: the root comes last.", Colors.ORANGE);
        pause(2.6);
        reset(d(0.8));
        os.clear();
    }

    private void levelOrder() {
        // depth first: D's children before D's sibling E
        say("Depth-first: D's children H and I are finished before its sibling E is touched.", Colors.LIGHT_GRAY);
        GN d0 = at(root, 2), h = at(root, 2, 0), i = at(root, 2, 1), e = at(root, 3);
        List<Animation> a = new ArrayList<>();
        t.paint(a, d0, VISIT_C, d(0.4));
        t.paint(a, h, Colors.TEAL, d(0.4));
        t.paint(a, i, Colors.TEAL, d(0.4));
        playAll(a);
        pause(0.8);
        List<Animation> b = new ArrayList<>();
        t.paint(b, e, Colors.RED, d(0.4));
        playAll(b);
        TextMob later = label("later", e.x, e.y + 48, 26, Colors.RED, false, true);
        play(new FadeIn(later, d(0.4)));
        pause(2.2);
        List<Animation> c = new ArrayList<>();
        c.add(new FadeOut(later, d(0.4)));
        for (GN n : new GN[]{d0, h, i, e}) t.paintDefault(c, n, d(0.5));
        playAll(c);
        remove(later);

        // breadth first, level by level
        fadeOutAll(d(0.4), os.title);
        os = new OutStrip("level order:", -900, 250, 56, Colors.TEAL);
        os.showTitle();
        mine.addAll(os.all());
        say("Breadth-first: finish a whole level before going to the next.", Colors.TEAL);
        pause(0.8);
        int maxD = 3;
        step = 0;
        for (int dp = 0; dp <= maxD; dp++) {
            RectMob band = outline(0, -330 + 120 * dp, 1500, 62, Colors.TEAL);
            play(new FadeIn(band, d(0.5)));
            for (GN n : t.nodes) {
                if (n.depth != dp) continue;
                t.paintNow(n, VISIT_C, d(0.25));
                number(n, false);
                t.paintNow(n, DONE_C, d(0.25));
                pause(0.12);
            }
            play(new FadeOut(band, d(0.4)));
            remove(band);
        }
        mine.addAll(os.all());
        say("A B C D E F G H I J K L M N: row by row, top to bottom.", Colors.TEAL);
        pause(3.0);
        mine.addAll(tags);
    }
}
