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
 * Standalone clip for slide 8 of the trees deck: the exercises, in the classwork style.
 *
 *   - all seven exercises first, one at a time (badge, pen-stroke text, highlighted key words)
 *   - then a solution section for each, with pseudo-code on the left, its highlight bar following the line
 *     being executed, and an employee tree on the right that does what the line says:
 *       1 list the subordinates of a node (direct and indirect in two colors)
 *       2 the same, with only a name (search first, then list)
 *       3 distance between two nodes (climb to the common ancestor)
 *       4 diameter (the two tallest branches through every node)
 *       5 infix to postfix with a tree (build it, walk it children-first)
 *       6 mirror a tree (reverse the children at every node)
 *       7 is there a directed path from p to q
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeExercisesScene extends PDSTreeClipBase {

    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();
    private GT et;
    private GN er;
    private CodeBox code;
    private OutStrip out;

    @Override
    public void construct() {
        head = writeHeading("Exercises");
        pause(0.5);
        questions();
        subordinates();
        byName();
        distance();
        diameter();
        postfix();
        mirror();
        directedPath();
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    // ── the exercises, one at a time ─────────────────────────────────

    private void questions() {
        List<MObject> q = new ArrayList<>();
        double y = -350, step = 100;
        q.addAll(question(1, "Given (a pointer to) a node in an employee tree,", y, 38, new String[]{"node"}, Colors.BLUE));
        q.add(subLine("list all its direct and indirect subordinates.", y + 46, 30));
        pause(0.7);
        q.addAll(question(2, "Same as above with the name of the employee given.", y + step, 38, new String[]{"name"}, Colors.TEAL));
        pause(0.7);
        q.addAll(question(3, "Find distance between two nodes.", y + 2 * step, 38, new String[]{"distance"}, Colors.GOLD));
        pause(0.7);
        q.addAll(question(4, "Find tree diameter (max. distance).", y + 3 * step, 38, new String[]{"diameter"}, Colors.ORANGE));
        pause(0.7);
        q.addAll(question(5, "Convert infix to postfix (using a tree).", y + 4 * step, 38, new String[]{"postfix"}, Colors.GREEN));
        pause(0.7);
        q.addAll(question(6, "Mirror a tree.", y + 5 * step, 38, new String[]{"Mirror"}, Colors.PINK));
        pause(0.7);
        q.addAll(question(7, "Find if there is a directed path from p to q.", y + 6 * step, 38, new String[]{"directed path"}, Colors.PURPLE));
        pause(2.4);
        fadeOutAll(d(1.0), q);
        pause(0.4);
    }

    // ── shared stage ─────────────────────────────────────────────────

    private GN emp() {
        return g("Asha", g("Bala", g("Esha"), g("Farid")), g("Chitra", g("Gita")),
                g("Dev", g("Hari", g("Kiran")), g("Indu"), g("Jay")));
    }

    private void begin(int num, String title, String[] src, boolean arrows) {
        mine.addAll(solutionHeader(num, title, -400));
        code = new CodeBox(src, -920, -310, 26, 40);
        code.typeIn(1.4 + 0.45 * src.length);
        mine.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.3)));
        er = emp();
        et = new GT(er, 410, -300, 125, 22, 28, 26, arrows, null);
        et.buildAll(d(0.9));
        mine.addAll(et.parts());
        pause(0.4);
    }

    private void end() {
        unsay();
        fadeOutAll(d(0.9), mine);
        mine.clear();
        pause(0.3);
    }

    private void hl(int line) { play(code.moveHl(line, d(0.28))); }

    private void narr(String s, Color c) { sayAt(s, c, 0, 410, 34); }

    private void paint(GN n, Color c) { et.paintNow(n, c, d(0.35)); }

    private TextMob mark(GN n, String s, Color c) {
        TextMob t = label(s, n.x, n.y - n.hh - 24, 26, c, false, true);
        play(new FadeIn(t, d(0.35)));
        mine.add(t);
        return t;
    }

    // ── 1: subordinates of a node ────────────────────────────────────

    private void listFrom(GN p, boolean direct) {
        for (GN c : p.kids) {
            hl(1);
            paint(c, direct ? Colors.GOLD : Colors.TEAL);
            hl(2);
            out.add(c.name, direct ? Colors.GOLD : Colors.TEAL);
            hl(3);
            listFrom(c, false);
            et.paintNow(c, c.leaf() ? LEAF_C : INNER_C, d(0.3));
        }
    }

    private void subordinates() {
        begin(1, "Subordinates of a node", new String[]{
                "void list(Node *p) {",
                "  for (c : p->children) {",
                "    print(c);",
                "    list(c);",
                "  }",
                "}"}, true);
        out = new OutStrip("printed:", -900, 250, 56, Colors.GOLD);
        out.showTitle();
        mine.addAll(out.all());
        GN dev = at(er, 2);
        mark(dev, "p", Colors.WHITE);
        paint(dev, Colors.WHITE);
        hl(0);
        narr("Start at p. Every child is a direct subordinate; going down further gives the indirect ones.", Colors.LIGHT_GRAY);
        pause(1.0);
        listFrom(dev, true);
        mine.addAll(out.all());
        TextMob l1 = label("direct", -760, 350, 28, Colors.GOLD, false, true);
        TextMob l2 = label("indirect", -600, 350, 28, Colors.TEAL, false, true);
        play(new FadeIn(l1, d(0.5)), new FadeIn(l2, d(0.5)));
        mine.add(l1);
        mine.add(l2);
        narr("Hari, Indu and Jay report to Dev directly; Kiran reports to Hari.", Colors.GREEN);
        pause(2.6);
        end();
    }

    // ── 2: the name is given ─────────────────────────────────────────

    private boolean found;

    private boolean search(GN p, String name, Steps st) {
        hl(1);
        st.tick();
        paint(p, Colors.ORANGE);
        pause(0.2);
        if (p.name.equals(name)) {
            paint(p, Colors.GREEN);
            return true;
        }
        for (GN c : p.kids) {
            hl(3);
            if (search(c, name, st)) {
                hl(5);
                paint(p, Colors.GOLD);
                return true;
            }
        }
        hl(7);
        paint(p, Colors.GRAY);
        return false;
    }

    private void byName() {
        begin(2, "The name is given", new String[]{
                "Node *find(Node *p, name) {",
                "  if (p->name == name)",
                "    return p;",
                "  for (c : p->children) {",
                "    r = find(c, name);",
                "    if (r) return r;",
                "  }",
                "  return NULL;",
                "}"}, true);
        // line numbers used below: 1 test, 3 loop, 4 recurse, 5 propagate
        List<MObject> nm = chip("name = Dev", -640, 235, 300, 56, Colors.TEAL, 30);
        play(new FadeIn(nm.get(0), d(0.5)), new FadeIn(nm.get(1), d(0.5)));
        mine.addAll(nm);
        Steps st = new Steps(-700, 330, 70);
        play(new FadeIn(st.num, d(0.4)), new FadeIn(st.cap, d(0.4)));
        mine.addAll(st.parts());
        narr("We only have the name, so we must search the tree first.", Colors.LIGHT_GRAY);
        pause(1.0);
        searchRun(er, "Dev", st);
        pause(0.6);
        narr("Found Dev after 7 nodes. In the worst case the search looks at all N nodes: O(N).", Colors.ORANGE);
        pause(2.4);
        List<Animation> reset = new ArrayList<>();
        for (GN n : et.nodes) et.paintDefault(reset, n, d(0.5));
        playAll(reset);
        narr("Now we hold a pointer to Dev: exercise 1 does the rest.", Colors.GREEN);
        fadeOutAll(d(0.5), nm);
        fadeOutAll(d(0.5), st.parts());
        out = new OutStrip("printed:", -900, 250, 56, Colors.GOLD);
        out.showTitle();
        GN dev = at(er, 2);
        paint(dev, Colors.WHITE);
        pause(0.6);
        // quick reuse of exercise 1's walk
        listQuick(dev, true);
        mine.addAll(out.all());
        pause(2.4);
        end();
    }

    private void searchRun(GN root, String name, Steps st) {
        search(root, name, st);
    }

    private void listQuick(GN p, boolean direct) {
        for (GN c : p.kids) {
            paint(c, direct ? Colors.GOLD : Colors.TEAL);
            out.add(c.name, direct ? Colors.GOLD : Colors.TEAL);
            listQuick(c, false);
        }
    }

    // ── 3: distance between two nodes ────────────────────────────────

    private void distance() {
        begin(3, "Distance between two nodes", new String[]{
                "int dist(Node *u, Node *v) {",
                "  w = lowest common ancestor",
                "  return depth(u) + depth(v)",
                "         - 2 * depth(w);",
                "}"}, false);
        GN u = at(er, 0, 1), v = at(er, 2, 0, 0);
        paint(u, Colors.WHITE);
        paint(v, Colors.WHITE);
        mark(u, "u", Colors.WHITE);
        mark(v, "v", Colors.WHITE);
        hl(0);
        narr("The path between two nodes goes up to a common ancestor, then down.", Colors.LIGHT_GRAY);
        pause(1.0);
        hl(1);
        List<GN> upU = new ArrayList<>(), upV = new ArrayList<>();
        for (GN n = u; n != null; n = n.parent) upU.add(n);
        for (GN n = v; n != null; n = n.parent) upV.add(n);
        GN w = null;
        for (GN n : upU) if (upV.contains(n)) { w = n; break; }
        for (int i = 1; i < upU.size(); i++) {
            List<Animation> a = new ArrayList<>();
            et.paintEdge(a, upU.get(i - 1), Colors.GOLD, d(0.4));
            et.paint(a, upU.get(i), upU.get(i) == w ? Colors.GREEN : Colors.GOLD, d(0.4));
            playAll(a);
        }
        for (int i = 1; i < upV.size(); i++) {
            List<Animation> a = new ArrayList<>();
            et.paintEdge(a, upV.get(i - 1), Colors.PINK, d(0.4));
            if (upV.get(i) != w) et.paint(a, upV.get(i), Colors.PINK, d(0.4));
            playAll(a);
        }
        mark(w, "w", Colors.GREEN);
        narr("Climbing from both nodes the two trails meet at Asha: the lowest common ancestor.", Colors.GREEN);
        pause(1.6);
        hl(2);
        TextMob du = label("depth 2", u.x, u.y + 56, 24, Colors.GOLD, false, true);
        TextMob dv = label("depth 3", v.x, v.y + 56, 24, Colors.PINK, false, true);
        TextMob dw = label("depth 0", w.x + 120, w.y, 24, Colors.GREEN, false, true);
        play(new FadeIn(du, d(0.5)), new FadeIn(dv, d(0.5)), new FadeIn(dw, d(0.5)));
        mine.add(du);
        mine.add(dv);
        mine.add(dw);
        pause(0.6);
        hl(3);
        LaTeXMob f = latex("2 + 3 - 2 \\cdot 0 = 5", 56, -560, 235);
        play(new FadeIn(f, d(0.8)));
        mine.add(f);
        narr("Walk the path to check: five edges.", Colors.LIGHT_GRAY);
        pause(1.0);
        List<GN> path = new ArrayList<>();
        for (GN n : upU) { path.add(n); if (n == w) break; }
        List<GN> down = new ArrayList<>();
        for (GN n : upV) { if (n == w) break; down.add(0, n); }
        path.addAll(down);
        TextMob cnt = label("length 0", -560, 330, 40, Colors.GOLD, false, true);
        play(new FadeIn(cnt, d(0.4)));
        mine.add(cnt);
        for (int i = 1; i < path.size(); i++) {
            et.paintNow(path.get(i), Colors.WHITE, d(0.3));
            cnt.setText("length " + i);
            pause(0.35);
        }
        pause(2.2);
        end();
    }

    // ── 4: diameter ──────────────────────────────────────────────────

    private void diameter() {
        begin(4, "Tree diameter", new String[]{
                "// for every node p:",
                "//   a, b = the two tallest",
                "//          branches below p",
                "//   through(p) = a + b",
                "// diameter = max through(p)"}, false);
        narr("The longest path bends at one node, using its two tallest branches: the gold number is a + b.", Colors.LIGHT_GRAY);
        hl(0);
        pause(1.2);
        List<GN> order = new ArrayList<>();
        postorderInto(er, order);
        int best = -1;
        GN bestN = null;
        for (GN p : order) {
            if (p.leaf()) continue;
            hl(1);
            List<Integer> hs = new ArrayList<>();
            for (GN c : p.kids) hs.add(1 + height(c));
            Collections.sort(hs, Collections.reverseOrder());
            int a = hs.get(0), b = hs.size() > 1 ? hs.get(1) : 0;
            paint(p, Colors.ORANGE);
            hl(3);
            TextMob tg = label(String.valueOf(a + b), p.x + p.hw + 26, p.y, 34, Colors.GOLD, false, true);
            play(new FadeIn(tg, d(0.4)));
            mine.add(tg);
            if (a + b > best) {
                best = a + b;
                bestN = p;
            }
            et.paintNow(p, INNER_C, d(0.3));
            pause(0.2);
        }
        hl(4);
        narr("The largest value is 5, at Asha: that is the diameter.", Colors.GREEN);
        List<GN> path = new ArrayList<>();
        for (GN n : new GN[]{at(er, 2, 0, 0), at(er, 2, 0), at(er, 2), er, at(er, 0), at(er, 0, 0)}) path.add(n);
        for (int i = 0; i < path.size(); i++) {
            List<Animation> a = new ArrayList<>();
            et.paint(a, path.get(i), Colors.GREEN, d(0.4));
            if (i > 0) et.paintEdge(a, path.get(i - 1).depth > path.get(i).depth ? path.get(i - 1) : path.get(i), Colors.GREEN, d(0.4));
            playAll(a);
        }
        List<MObject> ans = chip("diameter = 5", -560, 235, 300, 60, Colors.GREEN, 32);
        play(new FadeIn(ans.get(0), d(0.5)), new FadeIn(ans.get(1), d(0.5)));
        mine.addAll(ans);
        pause(2.8);
        end();
    }

    // ── 5: infix to postfix through a tree ───────────────────────────

    private void postfix() {
        mine.addAll(solutionHeader(5, "Infix to postfix, using a tree", -400));
        code = new CodeBox(new String[]{
                "1. build the expression tree",
                "2. walk it children first,",
                "   then the node (postorder)"}, -920, -310, 26, 40);
        code.typeIn(3.0);
        mine.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.3)));
        TextMob inf = mono("infix:  a + b * c", -900, -140, 34, Colors.WHITE);
        inf.setAlignment(javafx.scene.text.TextAlignment.LEFT);
        play(new FadeIn(inf, d(0.6)));
        mine.add(inf);
        GN root = g("+", g("a"), g("*", g("b"), g("c")));
        GT t = new GT(root, 410, -260, 140, 150, 36, 34, false, n -> n.leaf() ? Colors.BLUE : Colors.GOLD);
        hl(0);
        narr("* binds tighter than +, so b * c sits deeper in the tree.", Colors.LIGHT_GRAY);
        t.build(0.4);
        mine.addAll(t.parts());
        pause(1.4);
        hl(1);
        OutStrip os = new OutStrip("postfix:", -900, 150, 60, Colors.GREEN);
        os.showTitle();
        mine.addAll(os.all());
        List<GN> post = new ArrayList<>();
        postorderInto(root, post);
        for (GN n : post) {
            hl(2);
            t.paintNow(n, Colors.WHITE, d(0.4));
            os.add(n.name);
            t.paintNow(n, Colors.GREEN, d(0.3));
        }
        mine.addAll(os.all());
        narr("a b c * +: the same expression with no parentheses needed.", Colors.GREEN);
        pause(3.0);
        end();
    }

    // ── 6: mirror ────────────────────────────────────────────────────

    private void mirrorRec(GN p) {
        hl(0);
        paint(p, Colors.GOLD);
        if (p.kids.size() > 1) {
            hl(1);
            Collections.reverse(p.kids);
            List<Animation> a = new ArrayList<>();
            et.relayout(a, d(1.1));
            playAll(a);
        }
        hl(2);
        pause(0.15);
        for (GN c : new ArrayList<>(p.kids)) {
            hl(3);
            mirrorRec(c);
        }
        et.paintNow(p, p == er ? ROOT_C : p.leaf() ? LEAF_C : INNER_C, d(0.3));
    }

    private void mirror() {
        begin(6, "Mirror a tree", new String[]{
                "void mirror(Node *p) {",
                "  reverse(p->children);",
                "  for (c : p->children)",
                "    mirror(c);",
                "}"}, false);
        narr("At every node, reverse the order of its children, then do the same below.", Colors.LIGHT_GRAY);
        pause(1.0);
        mirrorRec(er);
        narr("Every level is flipped left to right: the mirror image.", Colors.GREEN);
        pause(2.8);
        end();
    }

    // ── 7: a directed path from p to q ───────────────────────────────

    private boolean pathRec(GN p, GN q) {
        hl(1);
        paint(p, p == q ? Colors.GREEN : Colors.ORANGE);
        pause(0.2);
        if (p == q) return true;
        for (GN c : p.kids) {
            hl(3);
            if (pathRec(c, q)) {
                hl(4);
                paint(p, Colors.GREEN);
                return true;
            }
        }
        hl(5);
        paint(p, Colors.RED);
        pause(0.2);
        return false;
    }

    private void directedPath() {
        begin(7, "A directed path from p to q", new String[]{
                "bool path(Node *p, Node *q) {",
                "  if (p == q) return true;",
                "  for (c : p->children)",
                "    if (path(c, q))",
                "      return true;",
                "  return false;",
                "}"}, true);
        // the hint line index: 1 test, 3 recursive call, 4 and 5 results
        GN p = at(er, 2), q = at(er, 2, 0, 0);
        mark(p, "p", Colors.WHITE);
        mark(q, "q", Colors.WHITE);
        narr("Edges point down, so we can only walk down from p. Is q below p?", Colors.LIGHT_GRAY);
        hl(0);
        pause(1.2);
        boolean r = pathRec(p, q);
        TextMob ok = label(r ? "true: Dev - Hari - Kiran" : "false", -560, 300, 40, r ? Colors.GREEN : Colors.RED, false, true);
        play(new FadeIn(ok, d(0.5)));
        mine.add(ok);
        pause(2.0);
        // a second try that fails
        List<Animation> reset = new ArrayList<>();
        for (GN n : et.nodes) et.paintDefault(reset, n, d(0.5));
        reset.add(new FadeOut(ok, d(0.5)));
        playAll(reset);
        narr("Now p = Bala and q = Indu.", Colors.LIGHT_GRAY);
        GN p2 = at(er, 0), q2 = at(er, 2, 1);
        TextMob m1 = mark(p2, "p", Colors.WHITE);
        TextMob m2 = mark(q2, "q", Colors.WHITE);
        pause(0.8);
        boolean r2 = pathRec(p2, q2);
        TextMob no = label(r2 ? "true" : "false: Indu is not below Bala", -560, 300, 40, r2 ? Colors.GREEN : Colors.RED, false, true);
        play(new FadeIn(no, d(0.5)));
        mine.add(no);
        pause(1.0);
        narr("Searching Bala's subtree never meets Indu, so there is no directed path.", Colors.ORANGE);
        pause(2.8);
        end();
    }
}
