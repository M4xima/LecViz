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
 * Clips for slides 18-22 of the dictionary deck: the average height of a BST built from random insertions.
 * One class, one clip per slide (the constructor takes the slide number):
 *
 *   18  the height of a node is its level; H(N) is the sum of the levels, worked out on a 7-node tree; a new root over
 *       two subtrees pushes each of their N-1 nodes one level down: H(N) = H(i) + H(N-i-1) + N-1
 *   19  every split (i, N-i-1) is equally likely, and the two columns of splits hold the same values: that is the
 *       factor 2 in H(N) = N-1 + 2/N times the sum of H(j)
 *   20  the algebra of slide 20: multiply by N, write the same line for N-1, subtract: the sums cancel
 *   21  divide by N(N+1) and add the lines for N, N-1, ..., 2: the terms in the middle cancel in pairs
 *   22  the sum of 1/i is O(log N), so H(N) = O(N log N): O(log N) per node on average, O(N) in the worst case
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSDictAvgScene extends PDSDictClipBase {

    private final int part;
    private final List<MObject> mine = new ArrayList<>();

    public PDSDictAvgScene(int part) { this.part = part; }

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("Average Case Analysis");
        pause(0.5);
        switch (part) {
            case 18: slide18(); break;
            case 19: slide19(); break;
            case 20: slide20(); break;
            case 21: slide21(); break;
            default: slide22(); break;
        }
        unsay();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void say(String s, Color c) { sayAt(s, c, 0, 440, 34); }

    private LaTeXMob show(String src, double size, double x, double y) {
        LaTeXMob f = latex(src, size, x, y);
        mine.add(f);
        play(new FadeIn(f, d(0.8)));
        return f;
    }

    private StrokeTextMob note(String s, double x, double y, double size, Color c) {
        StrokeTextMob n = stroke(s, x, y, size, c, false);
        play(new Write(n, d(Math.max(1.0, s.length() * 0.055))));
        mine.add(n);
        return n;
    }

    private void keep(List<List<MObject>> groups) {
        for (List<MObject> g : groups) mine.addAll(g);
    }

    // ── slide 18 ─────────────────────────────────────────────────────

    private void slide18() {
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Let's calculate the average height of the BST when"));
        s.add(ln(3, "elements are inserted in random order."));
        s.add(ln(0, "Let h(n1) denote height of a node n1."));
        s.add(ln(0, "Let H(N) denote the sum of the heights of all the"));
        s.add(ln(3, "nodes in the N-node BST."));
        keep(writeSlide(s, -380));
        pause(1.0);

        // the height of a node is its level; H(N) is the sum
        GN root = bstOf(4, 2, 6, 1, 3, 5, 7);
        GT t = bst(root, -430, -20, 90, 28, 30);
        t.build(0.25);
        mine.addAll(t.parts());
        say("The height of a node is its level: the number of edges from the root.", Colors.LIGHT_GRAY);
        List<TextMob> tags = new ArrayList<>();
        int[] total = {0};
        List<Animation> ta = new ArrayList<>();
        for (GN n : t.nodes) {
            TextMob tag = t.tag(n, String.valueOf(n.depth), Colors.GOLD, 30, 44, -34);
            tags.add(tag);
            mine.add(tag);
            total[0] += n.depth;
        }
        for (int dep = 0; dep <= 2; dep++) {
            List<Animation> a = new ArrayList<>();
            for (int i = 0; i < t.nodes.size(); i++) if (t.nodes.get(i).depth == dep) a.add(new FadeIn(tags.get(i), d(0.5)));
            playAll(a);
            pause(0.7);
        }
        LaTeXMob fa = show("H(N)=\\sum_{i=0}^{N-1} h(i)", 44, 330, -10);
        pause(0.8);
        LaTeXMob fb = show("H(7)=0+1+1+2+2+2+2=10", 40, 330, 100);
        pause(2.6);
        unsay();
        List<MObject> gone = new ArrayList<>(t.parts());
        gone.addAll(tags);
        gone.add(fa);
        gone.add(fb);
        fadeOutAll(d(0.9), gone);
        mine.removeAll(gone);
        pause(0.3);

        // a new root over two subtrees
        say("Build a tree from a root, a left subtree of i nodes and a right subtree of N - i - 1 nodes.", Colors.LIGHT_GRAY);
        CircleMob rc = new CircleMob(34);
        rc.setFillColor(Colors.withAlpha(Colors.MAROON, 0.42));
        rc.setStrokeColor(Colors.MAROON);
        rc.setStrokeWidth(3);
        rc.setPosition(0, -10);
        rc.setOpacity(0);
        add(rc);
        TextMob rt = label("N", 0, -10, 34, Colors.WHITE, false, true);
        RectMob lcl = cloud(-330, 170, "i nodes");
        RectMob rcl = cloud(330, 170, "N - i - 1 nodes");
        Link e1 = arrow(-24, 22, -250, 100, Colors.withAlpha(Colors.LIGHT_GRAY, 0.8), 3);
        Link e2 = arrow(24, 22, 250, 100, Colors.withAlpha(Colors.LIGHT_GRAY, 0.8), 3);
        mine.add(rc);
        mine.add(rt);
        mine.add(lcl);
        mine.add(rcl);
        mine.add(e1);
        mine.add(e2);
        List<Animation> in = new ArrayList<>();
        in.add(new FadeIn(rc, d(0.6)));
        in.add(new FadeIn(rt, d(0.6)));
        in.add(new FadeIn(lcl, d(0.7)));
        in.add(new FadeIn(rcl, d(0.7)));
        in.add(new DrawLinkAt(e1, 0.3, d(0.6)));
        in.add(new DrawLinkAt(e2, 0.3, d(0.6)));
        for (MObject m : cloudLabels) in.add(new FadeIn(m, d(0.7)));
        mine.addAll(cloudLabels);
        playAll(in);
        pause(1.6);
        say("The new root sits on top: every node below it goes down one level.", Colors.GOLD);
        TextMob p1 = label("+1 for each node", -330, 260, 32, Colors.GOLD, false, true);
        TextMob p2 = label("+1 for each node", 330, 260, 32, Colors.GOLD, false, true);
        mine.add(p1);
        mine.add(p2);
        List<Animation> pl = new ArrayList<>();
        pl.add(new FadeIn(p1, d(0.6)));
        pl.add(new FadeIn(p2, d(0.6)));
        playAll(pl);
        pause(1.6);
        show("H(N)=H(i)+H(N-i-1)+N-1", 46, 0, 340);
        say("There are N - 1 nodes below the root, so N - 1 is added. Hence H(N) = H(i) + H(N - i - 1) + N - 1.", Colors.GREEN);
        pause(3.4);
    }

    private final List<MObject> cloudLabels = new ArrayList<>();

    private RectMob cloud(double x, double y, String text) {
        RectMob r = new RectMob(400, 150).setCornerRadius(70);
        r.setFillColor(Colors.withAlpha(Colors.BLUE, 0.14));
        r.setStrokeColor(Colors.withAlpha(Colors.BLUE, 0.8));
        r.setStrokeWidth(2.6);
        r.setPosition(x, y);
        r.setOpacity(0);
        add(r);
        TextMob t = label(text, x, y, 36, Colors.WHITE, false, true);
        cloudLabels.add(t);
        return r;
    }

    // ── slide 19 ─────────────────────────────────────────────────────

    private void slide19() {
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "H(N) = H(i) + H(N - i - 1) + N - 1"));
        s.add(ln(0, "If the trees are random, each subtree height is equally likely.").kw("equally likely", Colors.BLUE));
        s.add(ln(0, "Thus, average value of H(i) and H(N - i - 1) is"));
        s.add(ln(3, "the average of H(j) over j = 0 ... N - 1."));
        s.add(ln(0, "So H(N) = N - 1 + 2/N times the sum of those H(j)."));
        keep(writeSlide(s, -380));
        pause(1.0);

        say("With 7 nodes the root can split the rest in 7 ways, all equally likely.", Colors.LIGHT_GRAY);
        int n = 7;
        List<Animation> in = new ArrayList<>();
        TextMob hl = label("left subtree i", -330, -40, 30, Colors.BLUE, false, true);
        TextMob hr = label("right subtree N - i - 1", 330, -40, 30, Colors.ORANGE, false, true);
        mine.add(hl);
        mine.add(hr);
        in.add(new FadeIn(hl, d(0.6)));
        in.add(new FadeIn(hr, d(0.6)));
        List<List<MObject>> left = new ArrayList<>(), right = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double y = 6 + i * 44;
            List<MObject> a = chip("H(" + i + ")", -330, y, 130, 38, Colors.BLUE, 24);
            List<MObject> b = chip("H(" + (n - 1 - i) + ")", 330, y, 130, 38, Colors.ORANGE, 24);
            TextMob lab = label("i = " + i, 0, y, 26, Colors.GRAY, false, false);
            left.add(a);
            right.add(b);
            mine.addAll(a);
            mine.addAll(b);
            mine.add(lab);
            for (MObject m : a) in.add(new FadeInAt(m, 0.12 * i, d(0.4)));
            for (MObject m : b) in.add(new FadeInAt(m, 0.12 * i, d(0.4)));
            in.add(new FadeInAt(lab, 0.12 * i, d(0.4)));
        }
        playAll(in);
        pause(1.4);
        say("Averaging over all the splits gives (1/N) times the sum of H(j) for j = 0 ... N - 1 ...", Colors.LIGHT_GRAY);
        show("\\frac{1}{N}\\sum_{j=0}^{N-1} H(j)", 40, 0, 345);
        pause(2.0);
        say("... and the right column holds exactly the same values as the left one, in reverse order.", Colors.GOLD);
        List<Animation> pulse = new ArrayList<>();
        for (List<MObject> c : left) pulse.add(new ColorChange(c.get(0), Colors.withAlpha(Colors.GOLD, 0.4), d(0.5)));
        for (List<MObject> c : right) pulse.add(new ColorChange(c.get(0), Colors.withAlpha(Colors.GOLD, 0.4), d(0.5)));
        playAll(pulse);
        pause(2.2);
        say("So the two subtrees together contribute twice that average.", Colors.GREEN);
        pause(1.0);
        for (MObject m : new ArrayList<>(mine)) {
            if (m instanceof LaTeXMob) {
                play(new FadeOut(m, d(0.5)));
                mine.remove(m);
                remove(m);
            }
        }
        show("H(N)=N-1+\\frac{2}{N}\\sum_{j=0}^{N-1} H(j)", 42, 0, 345);
        pause(3.4);
    }

    // ── slide 20 ─────────────────────────────────────────────────────

    private void slide20() {
        show("H(N)=N-1+\\frac{2}{N}\\sum_{j=0}^{N-1} H(j)", 38, 0, -385);
        pause(0.8);
        show("N\\,H(N)=N(N-1)+2\\sum_{j=0}^{N-1} H(j)\\qquad\\qquad (1)", 38, 0, -272);
        pause(1.0);
        note("Replacing N by N - 1", 0, -192, 32, Colors.RED);
        pause(0.4);
        show("(N-1)\\,H(N-1)=(N-1)(N-2)+2\\sum_{j=0}^{N-2} H(j)\\qquad\\qquad (2)", 38, 0, -103);
        pause(1.2);
        note("(1) - (2) gives", 0, -10, 32, Colors.RED);
        say("The two sums are the same, except that (1) has one more term: H(N - 1).", Colors.GOLD);
        pause(1.8);
        show("N\\,H(N)-(N-1)\\,H(N-1)=2N-2+2H(N-1)", 38, 0, 70);
        pause(1.6);
        note("Rearranging and ignoring 2", 0, 150, 32, Colors.RED);
        pause(0.4);
        LaTeXMob end = show("\\mathbf{N\\,H(N)=(N+1)\\,H(N-1)+2N}", 48, 0, 248);
        pause(0.6);
        say("One term of the average is gone: H(N) now depends only on H(N - 1).", Colors.GREEN);
        pause(3.4);
    }

    // ── slide 21 ─────────────────────────────────────────────────────

    private void slide21() {
        show("N\\,H(N)=(N+1)\\,H(N-1)+2N", 42, 0, -385);
        pause(0.8);
        note("Dividing by N(N+1)", 0, -305, 32, Colors.RED);
        pause(0.4);
        show("\\frac{H(N)}{N+1}=\\frac{H(N-1)}{N}+\\frac{2}{N+1}\\qquad\\qquad (3)", 44, 0, -215);
        pause(0.8);
        show("\\frac{H(N-1)}{N}=\\frac{H(N-2)}{N-1}+\\frac{2}{N}\\qquad\\qquad (4)", 44, 0, -97);
        pause(0.8);
        show("\\vdots", 44, 0, -22);
        show("\\frac{H(2)}{3}=\\frac{H(1)}{2}+\\frac{2}{3}\\qquad\\qquad (5)", 44, 0, 58);
        pause(1.0);
        note("Adding (3), (4), ..., (5)", 0, 145, 32, Colors.RED);
        say("The term H(N - 1) / N on the right of (3) appears on the left of (4): they cancel, and so on all the way down.", Colors.GOLD);
        pause(2.4);
        show("\\frac{H(N)}{N+1}=\\frac{H(1)}{2}+2\\sum_{i=3}^{N+1}\\frac{1}{i}", 50, 0, 250);
        pause(0.6);
        say("Only H(N) / (N + 1) on the left and H(1) / 2 on the right survive, plus all the 2 / i terms.", Colors.GREEN);
        pause(3.4);
    }

    // ── slide 22 ─────────────────────────────────────────────────────

    private void slide22() {
        show("\\frac{H(N)}{N+1}=\\frac{H(1)}{2}+2\\sum_{i=3}^{N+1}\\frac{1}{i}", 42, 0, -378);
        pause(0.8);
        say("The sum of 1/i for i up to N grows like log N.", Colors.LIGHT_GRAY);
        show("\\frac{H(N)}{N+1}=O(\\log N)", 42, 0, -258);
        pause(1.0);
        note("Thus, H(N) = O(N log N)", 0, -155, 42, Colors.WHITE);
        pause(1.0);
        List<Spec> s = new ArrayList<>();
        s.add(ln(1, "This indicates that sum of the heights of the nodes is"));
        s.add(ln(2, "O(N log N) in an N-node random BST."));
        s.add(ln(1, "Thus, average height of each node (in random BST) is O(log N).").kw("O(log N)", Colors.GREEN));
        s.add(ln(1, "The worst case is still O(N).").kw("O(N)", Colors.RED));
        keep(writeSlide(s, -75));
        say("Sum of heights divided by N nodes: O(log N) per node on average.", Colors.GREEN);
        pause(1.2);
        List<MObject> a = chip("random insertions: O(log N)", -330, 250, 560, 66, Colors.GREEN, 34);
        List<MObject> b = chip("worst case: O(N)", 330, 250, 480, 66, Colors.RED, 34);
        List<Animation> in = new ArrayList<>();
        fade(in, a, d(0.7));
        fade(in, b, d(0.7));
        playAll(in);
        mine.addAll(a);
        mine.addAll(b);
        pause(3.4);
    }
}
