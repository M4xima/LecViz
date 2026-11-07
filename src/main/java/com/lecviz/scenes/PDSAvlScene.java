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
 * Clips for slides 25-48 of the dictionary deck: AVL trees. One class, one clip per part (the constructor takes
 * the slide range): 25to26 intro and what does not work, 27 the AVL property, 28 and 29 the two classworks,
 * 30 an insertion that breaks the property, 31 the four cases, 32to33 and 35 the single rotations drawn in general,
 * 34 the first example, 36, 37to39, 40, 41, 42 the long insertion example, 43 its classwork, 44 deletion in AVL,
 * 45to47 the deletion example, 48 its classwork.
 *
 * The example clips run a real AVL tree (see DT in PDSAvlClipBase): every insertion, deletion and rotation is
 * worked out by the program and animated, so the pictures are always correct.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSAvlScene extends PDSAvlClipBase {

    private final String part;
    private final List<MObject> mine = new ArrayList<>();
    private StrokeTextMob head;

    public PDSAvlScene(String part) { this.part = part; }

    @Override
    public void construct() {
        switch (part) {
            case "25to26": s25to26(); break;
            case "27": s27(); break;
            case "28": s28(); break;
            case "29": s29(); break;
            case "30": s30(); break;
            case "31": s31(); break;
            case "32to33": s32to33(); break;
            case "34": s34(); break;
            case "35": s35(); break;
            case "36": s36(); break;
            case "37to39": s37to39(); break;
            case "40": s40(); break;
            case "41": s41(); break;
            case "42": s42(); break;
            case "43": s43(); break;
            case "44": s44(); break;
            case "45to47": s45to47(); break;
            default: s48(); break;
        }
        unsay();
        fadeOutAll(d(1.2), mine);
        if (head != null) fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    // ── helpers ──────────────────────────────────────────────────────

    private void say(String s, Color c) { sayAt(s, c, 0, 440, 34); }

    private void keep(List<List<MObject>> groups) { for (List<MObject> g : groups) mine.addAll(g); }

    private void textThenSwipe(List<Spec> s, double startY, double hold) {
        List<List<MObject>> t = writeSlide(s, startY);
        pause(hold);
        swipeAway(t);
        pause(0.4);
    }

    private StrokeTextMob cap(String s, double y, double size, Color c) {
        StrokeTextMob t = stroke(s, 0, y, size, c, false);
        play(new Write(t, d(Math.max(1.4, s.length() * 0.055))));
        mine.add(t);
        return t;
    }

    private void clearMine() {
        unsay();
        fadeOutAll(d(1.0), mine);
        mine.clear();
        pause(0.3);
    }

    private void newHeading(String s) {
        if (head != null) fadeOutAll(d(0.8), head);
        head = dropHeading(s);
        pause(0.4);
    }

    private GN leaf() { return bin(""); }

    private GN one(boolean left) { return left ? bin("", leaf(), null) : bin("", null, leaf()); }

    private GN two() { return bin("", leaf(), leaf()); }

    private GN perfect(int k) { return k == 0 ? leaf() : bin("", perfect(k - 1), perfect(k - 1)); }

    private GN fib(int h) { return h == 0 ? leaf() : h == 1 ? bin("", leaf(), null) : bin("", fib(h - 1), fib(h - 2)); }

    /** Shows balance numbers on every node of a tree, red where the balance is +-2 or worse. */
    private void balances(GT t, GN root, List<MObject> into) {
        List<GN> all = new ArrayList<>();
        preorderInto(root, all);
        List<Animation> a = new ArrayList<>();
        for (GN n : all) {
            int b = bal(n);
            TextMob tag = t.tag(n, bfText(b), Math.abs(b) > 1 ? Colors.RED : Colors.GOLD, 26, 30, -26);
            into.add(tag);
            a.add(new FadeIn(tag, d(0.6)));
            if (Math.abs(b) > 1) t.paint(a, n, GONE_C, d(0.5));
        }
        playAll(a);
    }

    // ── 25-26 ────────────────────────────────────────────────────────

    private void s25to26() {
        head = writeHeading("AVL Trees");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Normal BSTs may have height O(N)."));
        s.add(ln(0, "As long as BST property is satisfied, the BST"));
        s.add(ln(3, "can be restructured to maintain O(log N) height.").kw("O(log N)", Colors.GREEN));
        s.add(ln(0, "Invented by two researchers Georgy Adelson-").kw("two", Colors.RED));
        s.add(ln(3, "Velsky and Evgenii Landis from Russia in 1962."));
        s.add(ln(0, "Often called height-balanced trees or self-").kw("height-balanced", Colors.TEAL));
        s.add(ln(3, "balancing BSTs."));
        textThenSwipe(s, -330, 1.6);

        // the same keys, two shapes
        DT dt = new DT(bstOf(1, 2, 3, 4, 5, 6, 7), 0, -280, 100, 30, 32);
        dt.build(0.15);
        mine.addAll(dt.parts());
        dt.say("Insert 1, 2, 3 ... 7 in order and the BST is a chain: height 6, searches cost O(N).", Colors.RED);
        pause(2.6);
        GN[] nd = new GN[8];
        for (int v = 1; v <= 7; v++) nd[v] = dt.find(v);
        for (int v = 1; v <= 7; v++) nd[v].kids.clear();
        setChild(dt.dummy, 1, nd[4]);
        setChild(nd[4], 0, nd[2]);
        setChild(nd[4], 1, nd[6]);
        setChild(nd[2], 0, nd[1]);
        setChild(nd[2], 1, nd[3]);
        setChild(nd[6], 0, nd[5]);
        setChild(nd[6], 1, nd[7]);
        List<Animation> a = new ArrayList<>();
        dt.relayout(a, d(2.0));
        playAll(a);
        dt.say("The same keys in the same left-to-right order, as long as the BST property holds: height 2.", Colors.GREEN);
        pause(3.4);
        clearMine();

        // slide 26
        newHeading("What Doesn't Work");
        List<Spec> w = new ArrayList<>();
        w.add(ln(0, "Ensure that at the root, the left and the right subtrees"));
        w.add(ln(3, "have the same heights."));
        w.add(ln(1, "Doesn't guarantee height balance."));
        w.add(ln(0, "Ensure the above at every node in the BST."));
        w.add(ln(1, "Allows only a few BSTs (number of nodes 2^K - 1)"));
        textThenSwipe(w, -300, 1.6);

        GN root = bin("", bin("", bin("", leaf(), null), null), bin("", null, bin("", null, leaf())));
        GT t = mini(root, 0, -290, 24, 24, 100);
        t.build(0.2);
        mine.addAll(t.parts());
        List<GN> pre = new ArrayList<>();
        preorderInto(root, pre);
        GN lk = pre.get(1), rk = null;
        for (GN n : pre) if (n.parent == root && n.slot == 1) rk = n;
        TextMob hl = t.tag(lk, "height 2", Colors.GOLD, 30, -110, -6);
        TextMob hr = t.tag(rk, "height 2", Colors.GOLD, 30, 110, -6);
        mine.add(hl);
        mine.add(hr);
        List<Animation> in = new ArrayList<>();
        in.add(new FadeIn(hl, d(0.6)));
        in.add(new FadeIn(hr, d(0.6)));
        playAll(in);
        say("Equal heights at the root ...", Colors.GREEN);
        pause(2.0);
        say("... yet each side is a stick: the tree is far from balanced.", Colors.RED);
        pause(3.0);
        unsay();
        fadeOutAll(d(0.9), mine);
        mine.clear();

        // the same rule at every node allows only perfect trees
        double[] cx = {-700, -520, -240, 330};
        int[] sizes = {1, 3, 7, 15};
        for (int k = 0; k < 4; k++) {
            GT p = mini(perfect(k), cx[k], -300, 10, 10, 50);
            p.build(0.0);
            mine.addAll(p.parts());
            TextMob n = label(String.valueOf(sizes[k]) + (k == 0 ? " node" : " nodes"), cx[k], -300 + k * 50 + 60, 30, Colors.WHITE, false, true);
            mine.add(n);
            play(new FadeIn(n, d(0.4)));
        }
        say("If every node must have equal subtrees, only the perfect trees remain: 2^K - 1 nodes.", Colors.LIGHT_GRAY);
        pause(1.6);
        List<List<MObject>> chips = new ArrayList<>();
        List<Animation> ca = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            boolean ok = i == 1 || i == 3 || i == 7 || i == 15;
            List<MObject> c = chip(String.valueOf(i), (i - 8) * 64, 270, 56, 50, ok ? Colors.GREEN : Colors.GRAY, 26);
            chips.add(c);
            mine.addAll(c);
            for (MObject m : c) ca.add(new FadeInAt(m, 0.04 * i, d(0.4)));
        }
        playAll(ca);
        pause(0.8);
        say("A tree with 2, 4, 5, 6 ... nodes cannot be built this way: far too restrictive.", Colors.RED);
        pause(3.2);
    }

    // ── 27 ───────────────────────────────────────────────────────────

    private void s27() {
        head = writeHeading("AVL Property");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "At every node, the height-difference must not exceed 1.").kw("height-difference", Colors.BLUE));
        textThenSwipe(s, -300, 1.6);

        GN[] two = {bin("", leaf(), null), bin("", null, leaf()), two()};
        GN[] good = {bin("", two(), two()), bin("", two(), leaf()), bin("", one(true), one(false)), bin("", leaf(), one(true))};
        GN[] bad = {bin("", two(), null), bin("", null, one(true)), bin("", one(false), null), bin("", null, two())};

        double topY = -250;
        List<MObject> all = new ArrayList<>();
        // row 1: zero, one and two levels
        String[] titles = {"Zero level", "One level", "Two levels"};
        double[] tx = {-720, -540, -140};
        for (int i = 0; i < 3; i++) {
            TextMob t = label(titles[i], tx[i], -325, 30, Colors.MAROON, false, true);
            mine.add(t);
            play(new FadeIn(t, d(0.4)));
        }
        TextMob empty = label("(empty)", -720, -240, 26, Colors.GRAY, false, false);
        mine.add(empty);
        GT one1 = mini(leaf(), -540, topY, 16, 8, 56);
        mine.addAll(one1.parts());
        List<Animation> r1 = new ArrayList<>();
        r1.add(new FadeIn(empty, d(0.5)));
        one1.showAnim(r1, 0, d(0.5));
        List<GT> row1 = new ArrayList<>();
        double[] x2 = {-300, -140, 20};
        for (int i = 0; i < 3; i++) {
            GT g = mini(two[i], x2[i], topY, 16, 8, 56);
            row1.add(g);
            mine.addAll(g.parts());
            g.showAnim(r1, 0.1 * i, d(0.5));
        }
        playAll(r1);
        double[] mx = {-720, -540, -300, -140, 20};
        for (double x : mx) {
            List<LineMob> m = mark(true, x, -150);
            mine.addAll(m);
            drawMark(m);
        }
        pause(0.6);

        // row 2: three levels
        TextMob t3 = label("Three levels", -760, -85, 30, Colors.MAROON, false, true);
        mine.add(t3);
        play(new FadeIn(t3, d(0.4)));
        List<GN> eight = new ArrayList<>();
        for (GN g : good) eight.add(g);
        for (GN g : bad) eight.add(g);
        List<GT> row2 = new ArrayList<>();
        List<Animation> r2 = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            GT g = mini(eight.get(i), -777 + i * 222, -30, 13, 4, 52);
            row2.add(g);
            mine.addAll(g.parts());
            g.showAnim(r2, 0.08 * i, d(0.5));
        }
        playAll(r2);
        say("Check every node: the heights of its two subtrees differ by at most 1.", Colors.LIGHT_GRAY);
        pause(1.2);
        for (int i = 0; i < 8; i++) {
            boolean ok = isAvl(eight.get(i));
            List<LineMob> m = mark(ok, -777 + i * 222, 160);
            mine.addAll(m);
            drawMark(m);
            pause(0.25);
        }
        TextMob dots = label("...", 900, 40, 40, Colors.WHITE, false, true);
        mine.add(dots);
        play(new FadeIn(dots, d(0.5)));
        say("A node with one child and a node with two children are fine; a node with a single long branch is not.", Colors.GOLD);
        pause(3.6);
    }

    // ── 28 ───────────────────────────────────────────────────────────

    private void s28() {
        head = writeHeading("AVL Property");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "At every node, the height-difference must not exceed 1."));
        s.add(ln(0, "Classwork: Which of these have AVL structure?").kw("Classwork", Colors.RED));
        keep(writeSlide(s, -380));
        pause(0.6);

        GN t1 = bin("", bin("", leaf(), bin("", leaf(), null)), bin("", leaf(), null));
        GN t2 = bin("", bin("", leaf(), two()), leaf());
        GN t3 = bin("", bin("", bin("", leaf(), null), two()), bin("", leaf(), null));
        GN[] roots = {t1, t2, t3};
        double[] cx = {-600, 0, 600};
        List<GT> ts = new ArrayList<>();
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            GT g = mini(roots[i], cx[i], -200, 28, 12, 100);
            ts.add(g);
            mine.addAll(g.parts());
            g.showAnim(in, 0.15 * i, d(0.6));
        }
        playAll(in);
        say("Write the balance, height(left) - height(right), at every node.", Colors.LIGHT_GRAY);
        pause(1.4);
        for (int i = 0; i < 3; i++) balances(ts.get(i), roots[i], mine);
        pause(1.2);
        say("In the middle tree the root has balance +2: its left side is two levels deeper.", Colors.RED);
        pause(1.4);
        for (int i = 0; i < 3; i++) {
            List<LineMob> m = mark(isAvl(roots[i]), cx[i], 250);
            mine.addAll(m);
            drawMark(m);
            pause(0.3);
        }
        say("The first and the third trees have AVL structure; the second does not.", Colors.GREEN);
        pause(3.4);
    }

    // ── 29 ───────────────────────────────────────────────────────────

    private void s29() {
        head = writeHeading("AVL Property");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Classwork: Find an AVL tree having four").kw("Classwork", Colors.RED));
        s.add(ln(3, "levels and fewest number of nodes."));
        s.add(ln(0, "Classwork: Find the maximal AVL tree having four levels").kw("Classwork", Colors.RED));
        s.add(ln(3, "(such that addition of any edge makes it a"));
        s.add(ln(3, "height-imbalanced BST)."));
        textThenSwipe(s, -330, 1.8);

        // the fewest nodes for each height
        double[] cx = {-690, -520, -300, 130};
        int[] counts = {1, 2, 4, 7};
        List<GN> trees = new ArrayList<>();
        List<GT> gts = new ArrayList<>();
        List<Animation> in = new ArrayList<>();
        for (int h = 0; h < 4; h++) {
            GN g = fib(h);
            trees.add(g);
            GT t = mini(g, cx[h], -310, 16, 20, 80);
            gts.add(t);
            mine.addAll(t.parts());
            t.showAnim(in, 0.25 * h, d(0.6));
            TextMob lab = label("S(" + h + ") = " + counts[h], cx[h], -310 + h * 80 + 60, 32, Colors.WHITE, false, true);
            mine.add(lab);
            in.add(new FadeInAt(lab, 0.25 * h + 0.3, d(0.5)));
        }
        playAll(in);
        say("The fewest nodes an AVL tree of height h can have: S(h). One node, then a root with one child ...", Colors.LIGHT_GRAY);
        pause(2.4);
        GN t3 = trees.get(3);
        GN left = child(t3, 0), right = child(t3, 1);
        RectMob b1 = subBox(left, Colors.ORANGE, 16, 26, 26);
        RectMob b2 = subBox(right, Colors.TEAL, 16, 26, 26);
        mine.add(b1);
        mine.add(b2);
        List<Animation> bx = new ArrayList<>();
        bx.add(new FadeIn(b1, d(0.6)));
        bx.add(new FadeIn(b2, d(0.6)));
        playAll(bx);
        say("Smallest tree of height h: a root, a smallest tree of height h-1 on one side and h-2 on the other.", Colors.GOLD);
        pause(2.6);
        LaTeXMob f = latex("S(h)=S(h-1)+S(h-2)+1", 50, 0, 300);
        mine.add(f);
        play(new FadeIn(f, d(0.8)));
        pause(1.6);
        say("S(3) = 4 + 2 + 1 = 7: four levels are enough for just 7 nodes. (Next: 12, 20, 33 ...)", Colors.GREEN);
        pause(3.2);
        unsay();
        fadeOutAll(d(0.9), mine);
        mine.clear();

        // the maximal tree
        GT p = mini(perfect(3), 0, -300, 22, 18, 100);
        p.build(0.25);
        mine.addAll(p.parts());
        say("The other extreme: most nodes in four levels. (Assumption: 'maximal' means the most nodes.)", Colors.LIGHT_GRAY);
        pause(2.2);
        TextMob n15 = label("15 nodes = 2^4 - 1", 0, 300, 44, Colors.GREEN, false, true);
        mine.add(n15);
        play(new FadeIn(n15, d(0.7)));
        say("Every level is full. Adding any edge would start a fifth level.", Colors.GOLD);
        pause(3.4);
    }

    // ── 30 ───────────────────────────────────────────────────────────

    private void s30() {
        head = writeHeading("Insertion may violate AVL property");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Originally, the BST is height-balanced."));
        s.add(ln(0, "Insert(6) violates AVL property").kw("violates", Colors.RED));
        s.add(ln(1, "at node 8"));
        s.add(ln(0, "This is handled using rotations.").kw("rotations", Colors.ORANGE));
        s.add(ln(1, "Exploits BST property which allows multiple"));
        s.add(ln(2, "structures for the same set of keys."));
        s.add(ln(0, "Observation: Only nodes along the path from"));
        s.add(ln(3, "root to the new node have their subtrees altered."));
        s.add(ln(1, "Only these nodes may be checked for imbalance."));
        s.add(ln(1, "This means O(log N) rotations may be required."));
        s.add(ln(1, "We will show that only 2 rotations are sufficient.").kw("only 2", Colors.GREEN));
        textThenSwipe(s, -370, 1.8);

        GN root = bin("5", bin("2", bin("1"), bin("4", bin("3"), null)), bin("8", bin("7"), null));
        DT dt = new DT(root, 0, -270, 120, 30, 32);
        dt.build(0.25);
        mine.addAll(dt.parts());
        dt.say("This tree is height-balanced. Insert 6.", Colors.LIGHT_GRAY);
        pause(1.6);
        GN nn = dt.insertLeaf(6);
        pause(0.4);
        GN bad = dt.findImbalance(nn.parent);
        pause(1.2);
        dt.say("Insert(6) violates the AVL property at node 8: the left side is two levels deeper.", Colors.RED);
        pause(3.4);
        dt.clearTags();
    }

    // ── 31 ───────────────────────────────────────────────────────────

    private void s31() {
        head = writeHeading("AVL insert");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Four cases:"));
        s.add(ln(1, "1. insert into left subtree of left child"));
        s.add(ln(1, "2. Insert into right subtree of left child"));
        s.add(ln(1, "3. Insert into left subtree of right child"));
        s.add(ln(1, "4. Insert into right subtree of right child"));
        List<List<MObject>> text = writeSlide(s, -380);
        keep(text);
        String[] kinds = {"single rotation", "double rotation", "double rotation", "single rotation"};
        Color[] kc = {Colors.TEAL, Colors.ORANGE, Colors.ORANGE, Colors.TEAL};
        List<Animation> ch = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            List<MObject> c = chip(kinds[i], 420, -320 + 60 * i, 300, 44, kc[i], 26);
            mine.addAll(c);
            for (MObject m : c) ch.add(new FadeInAt(m, 0.25 * i, d(0.5)));
        }
        playAll(ch);
        pause(0.8);

        double[] cx = {-660, -220, 220, 660};
        for (int k = 0; k < 4; k++) {
            boolean ls = k < 2;
            Fig f = new Fig(22);
            int k2 = f.node("k2", Colors.ORANGE, 24), k1 = f.node("k1", Colors.BLUE, 24);
            int hi = k == 0 ? 0 : k == 1 ? 1 : k == 2 ? 1 : 2;   // which of X, Y, Z got the new node
            int[] tr = new int[3];
            String[] nm = {"X", "Y", "Z"};
            for (int i = 0; i < 3; i++) tr[i] = f.tri(nm[i], i == hi ? Colors.PINK : Colors.BLUE, 64, i == hi ? 100 : 70);
            double[][] xy = new double[5][2];
            double l1 = 120, l2 = 205;
            xy[k2] = new double[]{cx[k], 30};
            if (ls) {
                xy[k1] = new double[]{cx[k] - 90, l1};
                xy[tr[2]] = new double[]{cx[k] + 90, l1 - 24 + 35};
                xy[tr[0]] = new double[]{cx[k] - 160, l2 - 24 + (hi == 0 ? 50 : 35)};
                xy[tr[1]] = new double[]{cx[k] - 20, l2 - 24 + (hi == 1 ? 50 : 35)};
                f.edge(k2, k1);
                f.edge(k2, tr[2]);
                f.edge(k1, tr[0]);
                f.edge(k1, tr[1]);
            } else {
                xy[tr[0]] = new double[]{cx[k] - 90, l1 - 24 + 35};
                xy[k1] = new double[]{cx[k] + 90, l1};
                xy[tr[1]] = new double[]{cx[k] + 20, l2 - 24 + (hi == 1 ? 50 : 35)};
                xy[tr[2]] = new double[]{cx[k] + 160, l2 - 24 + (hi == 2 ? 50 : 35)};
                f.edge(k2, tr[0]);
                f.edge(k2, k1);
                f.edge(k1, tr[1]);
                f.edge(k1, tr[2]);
            }
            f.place(xy);
            mine.addAll(f.all());
            List<Animation> sh = new ArrayList<>();
            f.show(sh, d(0.7));
            TextMob num = label("case " + (k + 1), cx[k], 300, 30, Colors.GOLD, false, true);
            mine.add(num);
            sh.add(new FadeIn(num, d(0.7)));
            playAll(sh);
            pause(0.5);
        }
        say("The pink triangle is the subtree that just received the new node.", Colors.PINK);
        pause(3.2);
    }

    // ── 32-33 and 35: the general single rotations ───────────────────

    /** The general picture of a single rotation. Returns the figure, placed at its "before" layout, hidden. */
    private Fig rotFig(boolean caseOne) {
        Fig f = new Fig(24);
        int k2 = f.node("k2", Colors.ORANGE, 26), k1 = f.node("k1", Colors.BLUE, 26);
        int x = f.tri("X", caseOne ? Colors.PINK : Colors.BLUE, 80, caseOne ? 140 : 100);
        int y = f.tri("Y", Colors.BLUE, 80, 100);
        int z = f.tri("Z", caseOne ? Colors.BLUE : Colors.PINK, 80, caseOne ? 100 : 140);
        double L0 = -170, L1 = -70, L2 = 40;
        double[][] before = new double[5][2];
        before[k2] = new double[]{0, L0};
        if (caseOne) {
            before[k1] = new double[]{-150, L1};
            before[x] = new double[]{-250, L2 - 26 + 70};
            before[y] = new double[]{-50, L2 - 26 + 50};
            before[z] = new double[]{150, L1 - 26 + 50};
            f.edge(k2, k1);
            f.edge(k2, z);
            f.edge(k1, x);
            f.edge(k1, y);
        } else {
            before[x] = new double[]{-150, L1 - 26 + 50};
            before[k1] = new double[]{150, L1};
            before[y] = new double[]{50, L2 - 26 + 50};
            before[z] = new double[]{250, L2 - 26 + 70};
            f.edge(k2, x);
            f.edge(k2, k1);
            f.edge(k1, y);
            f.edge(k1, z);
        }
        f.place(before);
        return f;
    }

    private double[][] rotAfter(Fig f, boolean caseOne) {
        double L0 = -170, L1 = -70, L2 = 40;
        double[][] a = new double[5][2];
        if (caseOne) {
            a[0] = new double[]{150, L1};                  // k2
            a[1] = new double[]{0, L0};                    // k1
            a[2] = new double[]{-150, L1 - 26 + 70};       // X
            a[3] = new double[]{50, L2 - 26 + 50};         // Y
            a[4] = new double[]{250, L2 - 26 + 50};        // Z
            f.reconnect(new int[][]{{1, 0}, {0, 4}, {1, 2}, {0, 3}});
        } else {
            a[0] = new double[]{-150, L1};                 // k2
            a[1] = new double[]{0, L0};                    // k1
            a[2] = new double[]{-250, L2 - 26 + 50};       // X
            a[3] = new double[]{-50, L2 - 26 + 50};        // Y
            a[4] = new double[]{150, L1 - 26 + 70};        // Z
            f.reconnect(new int[][]{{0, 2}, {1, 0}, {0, 3}, {1, 4}});
        }
        return a;
    }

    private void s32to33() {
        head = writeHeading("AVL insert: Case 1");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Insert into left subtree of left child"));
        s.add(ln(1, "Before insertion into X, AVL property was satisfied."));
        s.add(ln(1, "Let k2 be the first node upward with imbalance."));
        s.add(ln(1, "Height(k2->left) - Height(k2->right) > 1"));
        s.add(ln(1, "k1 continues to be height-balanced."));
        s.add(ln(1, "Can Y and Z be at the same level?").kw("Y and Z", Colors.BLUE));
        textThenSwipe(s, -330, 1.6);

        Fig f = rotFig(true);
        mine.addAll(f.all());
        List<Animation> in = new ArrayList<>();
        f.show(in, d(0.8));
        playAll(in);
        List<LineMob> bad = mark(false, -400, -130);
        mine.addAll(bad);
        drawMark(bad);
        say("X, the pink subtree, just grew: k2's left side is now two levels deeper than its right.", Colors.PINK);
        pause(2.4);
        say("Y and Z cannot be at the same level as X: if they were, k1 would be unbalanced before.", Colors.LIGHT_GRAY);
        pause(2.4);

        // the in-order strip stays the same
        String[] seq = {"X", "k1", "Y", "k2", "Z"};
        List<List<MObject>> strip = new ArrayList<>();
        List<Animation> sa = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Color c = i % 2 == 0 ? Colors.BLUE : Colors.ORANGE;
            List<MObject> ch = chip(seq[i], (i - 2) * 100, 300, 84, 50, c, 28);
            strip.add(ch);
            mine.addAll(ch);
            for (MObject m : ch) sa.add(new FadeInAt(m, 0.1 * i, d(0.4)));
        }
        playAll(sa);
        say("Read left to right the order is X k1 Y k2 Z. A rotation must keep it.", Colors.GOLD);
        pause(2.2);
        List<MObject> r = chip("right rotation at k2", 400, -130, 380, 56, Colors.TEAL, 30);
        List<Animation> ra = new ArrayList<>();
        fade(ra, r, d(0.6));
        playAll(ra);
        mine.addAll(r);
        pause(0.6);
        List<Animation> mv = new ArrayList<>();
        f.morph(mv, rotAfter(f, true), d(2.0));
        playAll(mv);
        List<LineMob> ok = mark(true, -400, -130);
        mine.addAll(ok);
        for (MObject m : bad) play(new FadeOut(m, d(0.4)));
        drawMark(ok);
        say("New order: X k1 Y k2 Z: unchanged. X moves up one level, Y stays, Z moves down one level.", Colors.GREEN);
        pause(3.0);
        unsay();
        fadeOutAll(d(0.9), mine);
        mine.clear();

        List<Spec> t = new ArrayList<>();
        t.add(ln(0, "Insert into left subtree of left child"));
        t.add(ln(1, "Original order: X k1 Y k2 Z"));
        t.add(ln(1, "New order: X k1 Y k2 Z"));
        t.add(ln(1, "X moves up one level, Y stays at the same level,"));
        t.add(ln(2, "and Z moves down one level."));
        t.add(ln(1, "k1 and k2 satisfy AVL property."));
        t.add(ln(3, "In fact, they have subtrees with exactly the same height."));
        textThenSwipe(t, -330, 1.8);
    }

    private void s35() {
        head = writeHeading("AVL insert: Case 4");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Scenario is symmetric to Case 1."));
        s.add(ln(0, "Case 1 had a right rotation."));
        s.add(ln(0, "Case 4 needs a left rotation.").kw("left rotation", Colors.TEAL));
        textThenSwipe(s, -330, 1.6);

        Fig f = rotFig(false);
        mine.addAll(f.all());
        List<Animation> in = new ArrayList<>();
        f.show(in, d(0.8));
        playAll(in);
        List<LineMob> bad = mark(false, -400, -130);
        mine.addAll(bad);
        drawMark(bad);
        say("Z, the pink subtree on the right of the right child, just grew.", Colors.PINK);
        pause(2.4);
        List<MObject> r = chip("left rotation at k2", -400, -30, 380, 56, Colors.TEAL, 30);
        List<Animation> ra = new ArrayList<>();
        fade(ra, r, d(0.6));
        playAll(ra);
        mine.addAll(r);
        pause(0.6);
        List<Animation> mv = new ArrayList<>();
        f.morph(mv, rotAfter(f, false), d(2.0));
        playAll(mv);
        List<LineMob> ok = mark(true, 400, -130);
        mine.addAll(ok);
        drawMark(ok);
        say("Mirror image of Case 1: Z moves up one level, Y stays, X moves down one level.", Colors.GREEN);
        pause(3.0);
        unsay();
        fadeOutAll(d(0.9), mine);
        mine.clear();

        // a small example: 1, 2, 3
        DT dt = new DT(null, 0, -250, 120, 30, 32);
        mine.addAll(dt.parts());
        dt.say("An example: insert 1, 2, 3 into an empty AVL tree.", Colors.LIGHT_GRAY);
        pause(1.4);
        dt.insert(1);
        dt.insert(2);
        dt.insert(3);
        mine.addAll(dt.parts());
        pause(2.6);
    }

    // ── 34, 36 ... 43: runs of the dynamic tree ──────────────────────

    private void s34() {
        head = writeHeading("AVL insert: Case 1");
        pause(0.5);
        GN root = bin("5", bin("2", bin("1"), bin("4", bin("3"), null)), bin("8", bin("7"), null));
        DT dt = new DT(root, 0, -270, 120, 30, 32);
        dt.build(0.25);
        mine.addAll(dt.parts());
        dt.say("The tree from before. Insert 6, which goes into the left subtree of the left child of 8.", Colors.LIGHT_GRAY);
        pause(2.0);
        dt.insert(6);
        mine.addAll(dt.parts());
        TextMob note = label("k1 == 7, k2 == 8, X is the subtree rooted at 6, Y is empty, Z is empty.", 0, 360, 34, Colors.GOLD, false, true);
        mine.add(note);
        play(new FadeIn(note, d(0.8)));
        pause(3.6);
    }

    private void exampleText() {
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Start with an empty AVL tree."));
        s.add(ln(0, "Insert 3, 2, 1, 4, 5, 6, 7."));
        s.add(ln(0, "Then insert 16, 15, 14, 13, 12, 11, 10, 8, 9."));
        textThenSwipe(s, -300, 1.6);
    }

    private void s36() {
        head = writeHeading("Example");
        pause(0.5);
        exampleText();
        DT dt = new DT(null, 0, -250, 135, 36, 38);
        mine.addAll(dt.parts());
        dt.slow = 0.8;
        int[] vals = {3, 2, 1, 4, 5, 6, 7};
        for (int v : vals) {
            dt.insert(v);
            mine.addAll(dt.parts());
        }
        dt.say("Four rotations, and the tree is perfectly balanced.", Colors.GREEN);
        pause(3.0);
    }

    private void s37to39() {
        head = writeHeading("Example");
        pause(0.5);
        cap("Then insert 16, 15, 14, 13, 12, 11, 10, 8, 9.", -395, 38, Colors.WHITE);
        DT dt = new DT(avlOf(3, 2, 1, 4, 5, 6, 7), 0, -290, 110, 28, 30);
        dt.build(0.25);
        mine.addAll(dt.parts());
        dt.slow = 0.8;
        dt.insert(16);
        mine.addAll(dt.parts());
        dt.say("Now 15: it goes to the left of 16, below the right child of 7.", Colors.LIGHT_GRAY);
        pause(1.4);
        dt.insert(15);
        mine.addAll(dt.parts());
        pause(1.0);
        dt.say("Neither case 1 (left-left) nor case 4 (right-right): it is case 3 (right-left): a right-left double rotation.", Colors.ORANGE);
        pause(3.4);
        unsay();
        fadeOutAll(d(1.0), mine);
        mine.clear();

        // the general picture of the right-left double rotation
        Fig f = new Fig(24);
        int k1 = f.node("k1", Colors.BLUE, 26), k2 = f.node("k2", Colors.ORANGE, 26), k3 = f.node("k3", Colors.TEAL, 26);
        int a = f.tri("A", Colors.BLUE, 76, 90), b = f.tri("B", Colors.PINK, 76, 90), c = f.tri("C", Colors.PINK, 76, 90), dd = f.tri("D", Colors.BLUE, 76, 90);
        double L0 = -230, L1 = -130, L2 = -30, L3 = 70;
        double[][] before = new double[7][2];
        before[k1] = new double[]{0, L0};
        before[a] = new double[]{-200, L1 - 26 + 45};
        before[k3] = new double[]{200, L1};
        before[k2] = new double[]{100, L2};
        before[dd] = new double[]{300, L2 - 26 + 45};
        before[b] = new double[]{30, L3 - 26 + 45};
        before[c] = new double[]{170, L3 - 26 + 45};
        f.edge(k1, a);
        f.edge(k1, k3);
        f.edge(k3, k2);
        f.edge(k3, dd);
        f.edge(k2, b);
        f.edge(k2, c);
        f.place(before);
        mine.addAll(f.all());
        List<Animation> in = new ArrayList<>();
        f.show(in, d(0.8));
        playAll(in);
        say("In general: k3 is the right child of k1, and the new node went into k3's left subtree, B or C.", Colors.LIGHT_GRAY);
        pause(2.6);
        double[][] mid = new double[7][2];
        mid[k1] = new double[]{0, L0};
        mid[a] = new double[]{-200, L1 - 26 + 45};
        mid[k2] = new double[]{150, L1};
        mid[b] = new double[]{50, L2 - 26 + 45};
        mid[k3] = new double[]{260, L2};
        mid[c] = new double[]{200, L3 - 26 + 45};
        mid[dd] = new double[]{340, L3 - 26 + 45};
        double[][] after = new double[7][2];
        after[k2] = new double[]{0, L0};
        after[k1] = new double[]{-150, L1};
        after[k3] = new double[]{150, L1};
        after[a] = new double[]{-250, L2 - 26 + 45};
        after[b] = new double[]{-50, L2 - 26 + 45};
        after[c] = new double[]{50, L2 - 26 + 45};
        after[dd] = new double[]{250, L2 - 26 + 45};
        say("First a right rotation at k3 ...", Colors.LIGHT_GRAY);
        f.reconnect(new int[][]{{k1, a}, {k1, k2}, {k2, k3}, {k3, dd}, {k2, b}, {k3, c}});
        List<Animation> m1 = new ArrayList<>();
        f.morph(m1, mid, d(1.8));
        playAll(m1);
        pause(0.8);
        say("... then a left rotation at k1: k2 comes to the top.", Colors.LIGHT_GRAY);
        f.reconnect(new int[][]{{k1, a}, {k2, k1}, {k2, k3}, {k3, dd}, {k1, b}, {k3, c}});
        List<Animation> m2 = new ArrayList<>();
        f.morph(m2, after, d(1.8));
        playAll(m2);
        pause(0.8);
        TextMob seq = label("BST sequence: A k1 B k2 C k3 D", 0, 230, 38, Colors.MAROON, false, true);
        mine.add(seq);
        play(new FadeIn(seq, d(0.7)));
        say("RL: the order A k1 B k2 C k3 D is the same before and after, and the tree is balanced.", Colors.GREEN);
        pause(3.4);
    }

    private void insertRun(int[] prefix, int[] vals, String intro, double slow) {
        head = writeHeading("Example");
        pause(0.5);
        cap(intro, -395, 36, Colors.WHITE);
        DT dt = new DT(avlOf(prefix), 0, -290, 105, 26, 28);
        dt.build(0.25);
        mine.addAll(dt.parts());
        dt.slow = slow;
        for (int v : vals) {
            dt.insert(v);
            mine.addAll(dt.parts());
        }
        dt.say("Balanced: every node has a balance of -1, 0 or +1.", Colors.GREEN);
        pause(3.0);
    }

    private void s40() {
        insertRun(new int[]{3, 2, 1, 4, 5, 6, 7, 16, 15}, new int[]{14, 13}, "Insert 14, then 13.", 0.7);
    }

    private void s41() {
        insertRun(new int[]{3, 2, 1, 4, 5, 6, 7, 16, 15, 14, 13}, new int[]{12, 11, 10}, "Insert 12, 11, then 10.", 0.7);
    }

    private void s42() {
        insertRun(new int[]{3, 2, 1, 4, 5, 6, 7, 16, 15, 14, 13, 12, 11, 10}, new int[]{8, 9}, "Insert 8, then 9.", 0.7);
    }

    private void s43() {
        head = writeHeading("Classwork");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "In an empty AVL tree,"));
        s.add(ln(3, "insert 4, 10, 1, 2, 5, 7, 3, 6, 8, 9.").kw("insert", Colors.GOLD));
        textThenSwipe(s, -300, 1.6);
        DT dt = new DT(null, 0, -290, 105, 26, 28);
        mine.addAll(dt.parts());
        dt.slow = 0.6;
        int[] vals = {4, 10, 1, 2, 5, 7, 3, 6, 8, 9};
        for (int v : vals) {
            dt.insert(v);
            mine.addAll(dt.parts());
        }
        dt.say("The answer: an AVL tree with 4 at the root. Every node is balanced.", Colors.GREEN);
        pause(3.4);
    }

    // ── 44 ───────────────────────────────────────────────────────────

    private void s44() {
        head = writeHeading("Deletion in AVL");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Can be implemented lazily (marking).").kw("lazily", Colors.TEAL));
        s.add(ln(1, "Does not maintain AVL property."));
        s.add(ln(0, "Inverse of insertion, so need to think backwards."));
        s.add(ln(0, "But rotations would help us rebalance."));
        s.add(ln(1, "The four rotations can be called as primitives.").kw("primitives", Colors.RED));
        s.add(ln(1, "Deletion at internal node starts similar to as in BST."));
        s.add(ln(2, "Gets converted to deletion at the leaf."));
        s.add(ln(0, "Need to find taller of the two subtrees (left or right)."));
        s.add(ln(1, "We can then rotate that subtree to rebalance."));
        s.add(ln(0, "Unlike insertion, deletion may need to be repeated").kw("repeated", Colors.RED));
        s.add(ln(3, "for all the ancestors."));
        textThenSwipe(s, -390, 1.8);

        // lazy deletion: marked nodes stay, the live tree may be unbalanced
        DT dt = new DT(avlOf(4, 2, 6, 1, 3, 5, 7), 0, -270, 120, 30, 32);
        dt.build(0.25);
        mine.addAll(dt.parts());
        dt.say("Lazy deletion only marks a node. The tree keeps its shape.", Colors.LIGHT_GRAY);
        pause(2.0);
        int[] gone = {1, 2, 3};
        List<Animation> a = new ArrayList<>();
        for (int v : gone) {
            GN n = dt.find(v);
            dt.t.paint(a, n, Colors.GRAY, d(0.8));
            a.add(new FadeOut(n.text, d(0.8)));
        }
        playAll(a);
        dt.say("Delete 1, 2 and 3 by marking them: the picture still looks perfectly balanced ...", Colors.GOLD);
        pause(2.6);
        dt.say("... but the live nodes are 4, 5, 6, 7: nothing on the left of 4, three nodes below on the right.", Colors.RED);
        pause(3.4);
    }

    // ── 45-47, 48 ────────────────────────────────────────────────────

    private GN deletionTree() {
        return bin("50",
                bin("25", bin("10", bin("5", bin("1"), null), bin("15")), bin("30", bin("27"), null)),
                bin("75", bin("60", bin("55"), null), bin("80")));
    }

    private void s45to47() {
        head = writeHeading("Deletion Example");
        pause(0.5);
        DT dt = new DT(deletionTree(), 400, -320, 100, 26, 28);
        dt.sayY = 440;
        dt.build(0.2);
        mine.addAll(dt.parts());
        TextMob title = label("Delete 80", -690, -310, 40, Colors.MAROON, false, true);
        mine.add(title);
        play(new FadeIn(title, d(0.6)));
        String[] steps = {
                "Remove the node as in BST.",
                "Update heights of ancestors upward along the path.",
                "Find the first (deepest / lowest) imbalanced node x.",
                "Find x's tallest child y. Find y's tallest child z.",
                "Rotate x, y, z."};
        List<StrokeTextMob> lines = new ArrayList<>();
        for (int i = 0; i < steps.length; i++) {
            StrokeTextMob l = strokeLeft(steps[i], -900, -230 + i * 72, 28, Colors.WHITE);
            play(new Write(l, d(1.2)));
            lines.add(l);
            mine.add(l);
        }
        RectMob band = panel(-545, -230, 740, 54, Colors.GOLD, 0.2);
        mine.add(band);
        play(new FadeIn(band, d(0.4)));
        dt.onStage = i -> play(new MoveTo(band, -545, -230 + i * 72, d(0.4)).setEasing(com.lecviz.utils.Easing.EASE_IN_OUT));
        pause(0.8);
        dt.delete(80);
        mine.addAll(dt.parts());
        dt.say("Done: after two rotations the tree is balanced again, at 60 and at 50.", Colors.GREEN);
        pause(3.4);
    }

    private void s48() {
        head = writeHeading("Deletion Example");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Classwork: Delete 75, 55, 60, 50, 27.").kw("Classwork", Colors.RED));
        keep(writeSlide(s, -400));
        TextMob src = label("Source: avl.cpp", 760, -470, 26, Colors.GRAY, false, false);
        play(new FadeIn(src, d(0.6)));
        mine.add(src);
        GN start = bin("25", bin("10", bin("5", bin("1"), null), bin("15")),
                bin("50", bin("30", bin("27"), null), bin("60", bin("55"), bin("75"))));
        DT dt = new DT(start, 0, -300, 100, 26, 28);
        dt.build(0.2);
        mine.addAll(dt.parts());
        dt.slow = 0.6;
        pause(0.6);
        int[] vals = {75, 55, 60, 50, 27};
        for (int v : vals) {
            dt.delete(v);
            mine.addAll(dt.parts());
            pause(0.8);
        }
        dt.say("All five deleted, and after every deletion the tree is balanced.", Colors.GREEN);
        pause(3.4);
    }
}
