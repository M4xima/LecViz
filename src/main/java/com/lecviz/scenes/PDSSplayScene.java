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
 * Clips for slides 49-66 of the dictionary deck: splay trees. One class, one clip per part (the constructor takes
 * the slide range): 49 and 50 the idea, 51 and 52 moving a node up with single rotations (and why that is not
 * enough), 53to54 zig, zig-zag and zig-zig drawn in general, 55 the example find(k1), 56to59 the adversary,
 * 60 the need for amortized analysis, 61to62 the potential function, 63to64 the zig-zag analysis, 65 zig-zig,
 * 66 the total.
 *
 * The tree clips run a real tree (DT in PDSAvlClipBase): the search, the zig / zig-zig / zig-zag steps and every
 * rotation are worked out by the program and animated.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSSplayScene extends PDSAvlClipBase {

    private final String part;
    private final List<MObject> mine = new ArrayList<>();
    private StrokeTextMob head;

    public PDSSplayScene(String part) { this.part = part; }

    @Override
    public void construct() {
        switch (part) {
            case "49": s49(); break;
            case "50": s50(); break;
            case "51": s51(); break;
            case "52": s52(); break;
            case "53to54": s53to54(); break;
            case "55": s55(); break;
            case "56to59": s56to59(); break;
            case "60": s60(); break;
            case "61to62": s61to62(); break;
            case "63to64": s63to64(); break;
            case "65": s65(); break;
            default: s66(); break;
        }
        unsay();
        fadeOutAll(d(1.2), mine);
        if (head != null) fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    // ── helpers ──────────────────────────────────────────────────────

    private void say(String s, Color c) { sayAt(s, c, 0, 440, 34); }

    private void keep(List<List<MObject>> g) { for (List<MObject> x : g) mine.addAll(x); }

    private void textThenSwipe(List<Spec> s, double startY, double hold) {
        List<List<MObject>> t = writeSlide(s, startY);
        pause(hold);
        swipeAway(t);
        pause(0.4);
    }

    private void clearMine() {
        unsay();
        fadeOutAll(d(1.0), mine);
        mine.clear();
        pause(0.3);
    }

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

    /** The tree of slides 51-55: k5 (k4 (k3 (k2 (A, k1 (B, C)), D), E), F). */
    private GN pathTree() {
        return bin("k5", bin("k4", bin("k3", bin("k2", bin("A"), bin("k1", bin("B"), bin("C"))), bin("D")), bin("E")), bin("F"));
    }

    private GN named(GN root, String name) {
        if (root == null) return null;
        if (root.name.equals(name)) return root;
        for (GN k : root.kids) {
            GN f = named(k, name);
            if (f != null) return f;
        }
        return null;
    }

    private void tint(DT dt, GN root, Color keys, Color subtrees) {
        List<GN> all = new ArrayList<>();
        preorderInto(root, all);
        List<Animation> a = new ArrayList<>();
        for (GN n : all) dt.t.paint(a, n, "ABCDEF".contains(n.name) && n.name.length() == 1 ? subtrees : keys, 0.01);
        playAll(a);
    }

    // ── 49, 50 ───────────────────────────────────────────────────────

    private void s49() {
        head = writeHeading("Splay Trees");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "BSTs provide O(N) search."));
        s.add(ln(0, "AVLs provide O(log N) search."));
        s.add(ln(1, "Too strict about height-balancing."));
        s.add(ln(1, "Can be expensive in practice."));
        s.add(ln(0, "Can we construct a lightweight mechanism?"));
        s.add(ln(1, "Splay Trees provide O(log N) amortized search.").kw("amortized", Colors.RED));
        s.add(ln(1, "Splaying is widening of the road at junctions to"));
        s.add(ln(2, "improve visibility. In this case, we improve visibility"));
        s.add(ln(2, "of accessed nodes."));
        s.add(ln(1, "The BST need not be height-balanced.").kw("not", Colors.BLUE));
        textThenSwipe(s, -400, 1.8);

        String[][] rows = {{"BST", "search O(N)", "RED"}, {"AVL", "search O(log N), always: strict, can be costly", "GOLD"},
                {"Splay tree", "search O(log N) amortized: lightweight, no balance rule", "GREEN"}};
        Color[] cols = {Colors.RED, Colors.GOLD, Colors.GREEN};
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            List<MObject> a = chip(rows[i][0], -560, -250 + i * 130, 260, 70, cols[i], 34);
            TextMob b = label(rows[i][1], -380, -250 + i * 130, 36, Colors.WHITE, true, false);
            mine.addAll(a);
            mine.add(b);
            for (MObject m : a) in.add(new FadeInAt(m, 0.5 * i, d(0.6)));
            in.add(new FadeInAt(b, 0.5 * i + 0.2, d(0.6)));
        }
        playAll(in);
        pause(1.2);
        say("A splay tree repairs itself a little on every access, instead of enforcing a rule at every update.", Colors.LIGHT_GRAY);
        pause(2.6);
        say("Splaying widens the road where it is used: the nodes we access become easier to reach.", Colors.GOLD);
        pause(3.4);
    }

    private void s50() {
        head = writeHeading("Splay Trees");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Worst-case time to find an element is still O(N)."));
        s.add(ln(1, "Amortization: Over a set of M operations, the").kw("Amortization", Colors.RED));
        s.add(ln(2, "execution time is O(M log N)."));
        s.add(ln(0, "This means, a deep node cannot retain its"));
        s.add(ln(3, "position after access."));
        s.add(ln(1, "Else, an adversary can come up with a sequence of").kw("adversary", Colors.RED));
        s.add(ln(2, "M accesses requiring O(M N) time."));
        s.add(ln(0, "On an access, the element is pushed to the root.").kw("root", Colors.BLUE));
        s.add(ln(1, "This uses a series of rotations."));
        s.add(ln(1, "In the process, some other nodes also change their"));
        s.add(ln(2, "heights (resulting in some balancing)."));
        textThenSwipe(s, -420, 1.8);

        DT dt = new DT(bstOf(6, 3, 8, 2, 4, 7, 9, 1, 5), 0, -260, 120, 30, 32);
        dt.build(0.25);
        mine.addAll(dt.parts());
        dt.slow = 0.8;
        dt.say("The deepest node is 1. Access it: it is pushed to the root by rotations.", Colors.LIGHT_GRAY);
        pause(2.0);
        dt.access(1);
        mine.addAll(dt.parts());
        dt.say("1 is now at the root, and the other nodes on the path moved up and across as well.", Colors.GREEN);
        pause(3.2);
    }

    // ── 51, 52 ───────────────────────────────────────────────────────

    private void s51() {
        head = writeHeading("Moving k1 up on search(k1)");
        pause(0.5);
        GN root = pathTree();
        DT dt = new DT(root, 0, -290, 100, 24, 28);
        dt.build(0.2);
        mine.addAll(dt.parts());
        GN k1 = named(root, "k1");
        dt.t.paint(new ArrayList<>(), k1, Colors.ORANGE, 0.01);
        dt.say("search(k1) finds k1 deep in the tree. Move it up with single rotations.", Colors.LIGHT_GRAY);
        pause(2.2);
        dt.slow = 0.8;
        dt.moveUp(k1);
        mine.addAll(dt.parts());
        List<MObject> seq = chip("L  R  R  R", 0, 340, 300, 60, Colors.TEAL, 38);
        List<Animation> in = new ArrayList<>();
        fade(in, seq, d(0.7));
        playAll(in);
        mine.addAll(seq);
        dt.say("One left rotation, then three right rotations: k1 is the root.", Colors.GREEN);
        pause(3.2);
    }

    private void s52() {
        head = writeHeading("Moving k1 up on search(k1)");
        pause(0.5);
        GN after = bin("k1", bin("k2", bin("A"), bin("B")), bin("k5", bin("k4", bin("k3", bin("C"), bin("D")), bin("E")), bin("F")));
        DT dt = new DT(after, 420, -250, 95, 22, 26);
        dt.build(0.2);
        mine.addAll(dt.parts());
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "What did this move achieve?"));
        s.add(ln(1, "Next access to k1 is fast."));
        s.add(ln(1, "Next access to k2 is also faster."));
        s.add(ln(1, "Next accesses to k3, k4, k5 are slower."));
        s.add(ln(1, "Overall tree height has reduced."));
        keep(writeSlide(s, -300));
        pause(2.4);
        clearMine();

        List<Spec> t = new ArrayList<>();
        t.add(ln(0, "Let's play adversarial:").kw("adversarial", Colors.RED));
        t.add(ln(1, "Construct a left-skewed BST with insertion order N..1."));
        t.add(ln(1, "Access 1 (traverses N-1 links). Pushes 1 to root."));
        t.add(ln(1, "Access 2 (traverses N-2 links). Pushes 2 to root."));
        t.add(ln(1, "Access 3 (traverses N-3 links). Pushes 3 to root."));
        t.add(ln(1, "..."));
        t.add(ln(1, "Access N (traverses 1 link). Pushes N to root."));
        t.add(ln(1, "We get the original left-skewed BST. Repeat."));
        textThenSwipe(t, -330, 2.0);

        DT d2 = new DT(bstOf(5, 4, 3, 2, 1), 0, -300, 95, 28, 30);
        d2.build(0.2);
        mine.addAll(d2.parts());
        d2.talk = false;
        d2.slow = 0.5;
        TextMob info = label("", 0, 400, 36, Colors.GOLD, false, true);
        mine.add(info);
        int n = 5;
        for (int v = 1; v <= n; v++) {
            GN x = d2.find(v);
            int links = 0;
            for (GN c = x; c.parent != d2.dummy; c = c.parent) links++;
            info.setText("access " + v + ": " + links + " link" + (links == 1 ? "" : "s") + " traversed");
            info.setOpacity(1);
            lit(d2.t, x, FOUND_C, d(0.3));
            pause(0.6);
            d2.moveUp(x);
            mine.addAll(d2.parts());
            pause(0.5);
        }
        d2.talk = true;
        d2.say("Pushing up by single rotations keeps the tree lopsided: every access is expensive, N^2 in all.", Colors.RED);
        pause(3.6);
    }

    // ── 53-54 ────────────────────────────────────────────────────────

    private void s53to54() {
        head = writeHeading("Splaying");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Splaying on node X"));
        s.add(ln(1, "X must be a non-root; else no splaying is required."));
        s.add(ln(0, "If X's parent is the root, rotate X and root.").kw("rotate X and root", Colors.TEAL));
        s.add(ln(0, "Otherwise, X has a parent P and grandparent G."));
        s.add(ln(1, "Again, four cases."));
        s.add(ln(1, "Transform each of the cases as on the next slide."));
        textThenSwipe(s, -330, 1.8);

        // zig
        GN zig = bin("P", bin("X", bin("A"), bin("B")), bin("C"));
        DT dz = new DT(zig, 0, -250, 130, 30, 32);
        dz.build(0.2);
        mine.addAll(dz.parts());
        tint(dz, zig, Colors.ORANGE, Colors.PURPLE);
        dz.say("X's parent P is the root: one rotation brings X to the top (zig).", Colors.LIGHT_GRAY);
        pause(2.0);
        List<Animation> a = new ArrayList<>();
        dz.t.paint(a, named(zig, "X"), Colors.GOLD, d(0.4));
        playAll(a);
        pause(0.6);
        dz.rotate(zig, true);
        mine.addAll(dz.parts());
        dz.say("zig: X is the root now.", Colors.GREEN);
        pause(2.2);
        clearMine();

        // zig-zag
        GN zz = bin("G", bin("P", bin("A"), bin("X", bin("B"), bin("C"))), bin("D"));
        DT d1 = new DT(zz, 0, -280, 120, 28, 30);
        d1.build(0.2);
        mine.addAll(d1.parts());
        tint(d1, zz, Colors.ORANGE, Colors.PURPLE);
        GN x = named(zz, "X"), p = named(zz, "P"), g = named(zz, "G");
        List<Animation> b = new ArrayList<>();
        d1.t.paint(b, x, Colors.GOLD, d(0.4));
        d1.t.paint(b, p, Colors.BLUE, d(0.4));
        playAll(b);
        d1.say("X is the right child of P, and P is the left child of G: zig-zag. The same as the AVL case LR.", Colors.LIGHT_GRAY);
        pause(2.6);
        d1.rotate(p, false);
        d1.say("Rotate left at P ...", Colors.LIGHT_GRAY);
        pause(0.5);
        d1.rotate(g, true);
        mine.addAll(d1.parts());
        d1.say("... then right at G: X is the top, P and G are its children.", Colors.GREEN);
        pause(3.0);
        clearMine();

        // zig-zig
        GN zg = bin("G", bin("P", bin("X", bin("A"), bin("B")), bin("C")), bin("D"));
        DT d3 = new DT(zg, 0, -280, 120, 28, 30);
        d3.build(0.2);
        mine.addAll(d3.parts());
        tint(d3, zg, Colors.ORANGE, Colors.PURPLE);
        GN x3 = named(zg, "X"), p3 = named(zg, "P"), g3 = named(zg, "G");
        List<Animation> c = new ArrayList<>();
        d3.t.paint(c, x3, Colors.GOLD, d(0.4));
        d3.t.paint(c, p3, Colors.BLUE, d(0.4));
        playAll(c);
        d3.say("X, P and G all lean the same way: zig-zig. It is two right rotations: G first, then P.", Colors.LIGHT_GRAY);
        pause(2.6);
        d3.rotate(g3, true);
        d3.say("Rotate right at G (not at P: that would be the single-rotation way) ...", Colors.LIGHT_GRAY);
        pause(0.5);
        d3.rotate(p3, true);
        mine.addAll(d3.parts());
        d3.say("... then right at P: X is the top, with P and G below it on the same side.", Colors.GREEN);
        pause(3.0);
    }

    // ── 55 ───────────────────────────────────────────────────────────

    private void s55() {
        head = writeHeading("Splaying Example: find(k1)");
        pause(0.5);
        GN root = pathTree();
        DT dt = new DT(root, 0, -300, 80, 24, 28);
        dt.build(0.2);
        mine.addAll(dt.parts());
        GN k1 = named(root, "k1");
        dt.slow = 0.8;
        dt.say("find(k1): k1 is found, now splay it to the root.", Colors.LIGHT_GRAY);
        lit(dt.t, k1, FOUND_C, d(0.4));
        pause(1.8);
        dt.splay(k1);
        mine.addAll(dt.parts());
        dt.say("A zig-zag, then a zig-zig: k1 is the root, and the deep path k5 ... k2 is about half as long.", Colors.GREEN);
        pause(3.4);
    }

    // ── 56-59 ────────────────────────────────────────────────────────

    private void s56to59() {
        head = writeHeading("Splaying on Adversary");
        pause(0.5);
        String[] lines = {"Let's play adversarial:", "Construct a left-skewed BST with insertion order N..1.",
                "Access 1 (traverses N-1 links). Pushes 1 to root.", "Access 2 (traverses N-2 links). Pushes 2 to root.",
                "Access 3 (traverses N-3 links). Pushes 3 to root.", "...", "Access N (traverses 1 link). Pushes N to root.",
                "We get the original left-skewed BST. Repeat."};
        for (int i = 0; i < lines.length; i++) {
            StrokeTextMob l = strokeLeft(lines[i], -920, -330 + i * 62, 26, i == 0 ? Colors.RED : Colors.WHITE);
            play(new Write(l, d(1.0)));
            mine.add(l);
        }
        TextMob big = label("N^2", -520, 230, 56, Colors.RED, false, true);
        mine.add(big);
        play(new FadeIn(big, d(0.6)));
        DT dt = new DT(bstOf(7, 6, 5, 4, 3, 2, 1), 440, -290, 100, 28, 30);
        dt.build(0.2);
        mine.addAll(dt.parts());
        dt.slow = 0.7;
        dt.access(1);
        mine.addAll(dt.parts());
        dt.say("Notice that no nodes are as deep as before.", Colors.GOLD);
        pause(2.8);
        dt.access(2);
        mine.addAll(dt.parts());
        dt.say("After find(2) the tree is even more balanced: splaying reshapes the whole path.", Colors.GREEN);
        pause(3.4);
    }

    // ── 60 ───────────────────────────────────────────────────────────

    private void s60() {
        head = writeHeading("Splaying Analysis");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Many of you noted that"));
        s.add(ln(1, "Left-skewed may not be the worst-case sequence."));
        s.add(ln(1, "Height of a node may be above log(N) for every"));
        s.add(ln(2, "step in splaying."));
        s.add(ln(0, "How can we guarantee O(M log N) complexity"));
        s.add(ln(3, "for M operations?"));
        s.add(ln(0, "Need to depend upon amortized analysis.").kw("amortized", Colors.RED));
        s.add(ln(1, "Will use potential function.").kw("potential function", Colors.GOLD));
        keep(writeSlide(s, -330));
        pause(1.0);
        List<MObject> a = chip("one access may cost O(N)", -420, 230, 520, 66, Colors.RED, 32);
        List<MObject> b = chip("M accesses together: O(M log N)", 380, 230, 640, 66, Colors.GREEN, 32);
        List<Animation> in = new ArrayList<>();
        fade(in, a, d(0.7));
        in.addAll(new ArrayList<>());
        for (MObject m : b) in.add(new FadeInAt(m, 0.6, d(0.7)));
        playAll(in);
        mine.addAll(a);
        mine.addAll(b);
        say("Some accesses are expensive, but they pay for making later accesses cheap: amortized O(log N).", Colors.GOLD);
        pause(3.4);
    }

    // ── 61-62 ────────────────────────────────────────────────────────

    private void s61to62() {
        head = writeHeading("Potential Function");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "We intelligently guess a potential, and"));
        s.add(ln(1, "Show that it is maintained across operations."));
        s.add(ln(0, "In case of splaying, since we want an amortized"));
        s.add(ln(3, "bound of O(log N) per operation, the potential"));
        s.add(ln(3, "function is naturally O(log N)."));
        s.add(ln(1, "This means, potential of the data structure (splay"));
        s.add(ln(2, "tree here) increases by at max. O(log N)."));
        s.add(ln(0, "A good potential function:"));
        s.add(ln(1, "Clearly, node height doesn't help."));
        s.add(ln(1, "Thus, sum of the node heights also does not help."));
        s.add(ln(1, "We will use sum of log(S_i) where S_i is the subtree"));
        s.add(ln(2, "size rooted at node i."));
        textThenSwipe(s, -420, 2.0);

        List<Spec> t = new ArrayList<>();
        t.add(ln(0, "PF(i) = sum of R(i), where R(i) = log(S_i) for node i."));
        t.add(ln(1, "R(i) is often called the rank of node i."));
        t.add(ln(1, "Root has a rank of log(N)."));
        t.add(ln(1, "We need to show that PF is bounded for zig-zig and zig-zag rotations."));
        t.add(ln(1, "Important: rotation may change heights of many nodes,").kw("Important", Colors.RED));
        t.add(ln(2, "but only X, P, G may change their ranks."));
        keep(writeSlide(t, -330));
        show("PF=\\sum_i R(i),\\qquad R(i)=\\log(S_i)", 44, 0, 70);
        pause(1.6);

        // only X, P and G change their ranks
        GN zz = bin("G", bin("P", bin("A"), bin("X", bin("B"), bin("C"))), bin("D"));
        DT d1 = new DT(zz, 0, 160, 76, 24, 26);
        mine.addAll(d1.parts());
        List<Animation> in = new ArrayList<>();
        d1.build(0.0);
        tint(d1, zz, Colors.ORANGE, Colors.PURPLE);
        say("A, B, C and D are whole subtrees: their sizes, and so their ranks, do not change.", Colors.LIGHT_GRAY);
        pause(2.0);
        d1.talk = false;
        d1.rotate(named(zz, "P"), false);
        d1.rotate(named(zz, "G"), true);
        mine.addAll(d1.parts());
        say("Only X, P and G sit differently: only their ranks can change.", Colors.GREEN);
        pause(3.2);
    }

    // ── 63-64 ────────────────────────────────────────────────────────

    private void s63to64() {
        head = writeHeading("Zig-zag Step's Analysis");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Let R_i be initial rank and R_f be the rank after"));
        s.add(ln(3, "each step of splaying."));
        s.add(ln(0, "Cost of only zig-zag = 2"));
        s.add(ln(0, "Potential change ="));
        keep(writeSlide(s, -380));
        show("R_f(X)+R_f(P)+R_f(G)-\\left(R_i(X)+R_i(P)+R_i(G)\\right)", 40, 0, -150);
        pause(1.0);
        List<Spec> t = new ArrayList<>();
        t.add(ln(0, "Now, S_f(X) = S_i(G), so R_f(X) = R_i(G)."));
        t.add(ln(0, "Also, S_i(P) >= S_i(X). Thus, R_i(P) >= R_i(X)."));
        List<List<MObject>> tt = writeSlide(t, -60);
        keep(tt);
        say("After the step X holds the whole subtree that G held before: the same size, the same rank.", Colors.LIGHT_GRAY);
        pause(3.0);
        clearMine();

        show("AT_{zig\\text{-}zag}\\le 2+R_f(P)+R_f(G)-2R_i(X)\\qquad (1)", 42, 0, -330);
        pause(0.8);
        show("S_f(P)+S_f(G)\\le S_f(X)", 42, 0, -230);
        pause(0.8);
        show("\\log S_f(P)+\\log S_f(G)\\le 2\\log S_f(X)-2", 42, 0, -130);
        pause(0.8);
        show("R_f(P)+R_f(G)\\le 2R_f(X)-2", 42, 0, -30);
        pause(1.0);
        note("Substituting in (1)", 0, 60, 34, Colors.RED);
        pause(0.4);
        LaTeXMob end = show("AT_{zig\\text{-}zag}\\le 2R_f(X)-2R_i(X)\\le 3R_f(X)-3R_i(X)", 46, 0, 170);
        say("The two ranks R_f(P) and R_f(G) are squeezed: they cannot both be big, because their subtrees share S_f(X).", Colors.GOLD);
        pause(3.6);
    }

    // ── 65, 66 ───────────────────────────────────────────────────────

    private void s65() {
        head = writeHeading("Zig-zig Step's Analysis");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Similar to zig-zag, left as a homework.").kw("homework", Colors.RED));
        s.add(ln(1, "R_f(X) = R_i(G)"));
        s.add(ln(1, "R_f(X) >= R_f(P)"));
        s.add(ln(1, "R_i(X) <= R_i(P)"));
        s.add(ln(1, "S_i(X) + S_f(G) <= S_f(X)"));
        keep(writeSlide(s, -380));
        GN zg = bin("G", bin("P", bin("X", bin("A"), bin("B")), bin("C")), bin("D"));
        DT dt = new DT(zg, 0, -60, 100, 26, 28);
        dt.build(0.2);
        mine.addAll(dt.parts());
        tint(dt, zg, Colors.ORANGE, Colors.PURPLE);
        say("Why these hold: X ends up at the top of what G was the top of, so R_f(X) = R_i(G), and so on.", Colors.LIGHT_GRAY);
        pause(2.6);
        dt.talk = false;
        GN gNode = named(zg, "G"), pNode = named(zg, "P");
        dt.rotate(gNode, true);
        dt.rotate(pNode, true);
        mine.addAll(dt.parts());
        say("The same steps as for zig-zag give the same kind of bound: 3 R_f(X) - 3 R_i(X).", Colors.GREEN);
        pause(3.4);
    }

    private void s66() {
        head = writeHeading("Splaying Analysis");
        pause(0.5);
        show("AT_{zig\\text{-}zag}\\le 3R_f(X)-3R_i(X)", 44, 0, -340);
        List<Spec> s = new ArrayList<>();
        s.add(ln(1, "This is for one step of splaying."));
        s.add(ln(1, "For the next step, R_f(X) would be the initial rank."));
        s.add(ln(0, "Summing up across steps:"));
        s.add(ln(1, "Total cost <= 3 R_final(X) - 3 R_initial(X) + 1 (last 1 for zig)"));
        s.add(ln(1, "But finally, X becomes the root."));
        s.add(ln(1, "Total cost <= 3 R(root) - 3 R_initial(X) + 1"));
        s.add(ln(1, "Total cost <= O(log N) (root has a rank of log N)").kw("O(log N)", Colors.GREEN));
        s.add(ln(0, "Thus, splaying is amortized O(log N).").kw("amortized O(log N)", Colors.GREEN));
        keep(writeSlide(s, -250));
        pause(1.0);
        show("\\sum_{\\text{steps}}\\left(3R_{next}(X)-3R_{prev}(X)\\right)=3R_{final}(X)-3R_{initial}(X)", 38, 0, 305);
        say("The terms cancel in pairs from step to step, like a telescope: only the first and the last rank remain.", Colors.GOLD);
        pause(3.8);
    }
}
