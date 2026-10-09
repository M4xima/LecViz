package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

/**
 * Standalone clip for the classwork on slide 14 of the trees deck: indent the files by depth, and what is the
 * complexity (indentation time included).
 *
 *   - both questions first, then a solution for each
 *   - 1 the recursive preorder gets a depth parameter; the output panel writes each name pushed right by its depth
 *   - 2 the step counter ticks once for visiting a node and once for every space printed: N + the sum of the
 *     depths; a chain of nodes makes that quadratic
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeIndentScene extends PDSTreeClipBase {

    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();
    private CodeBox code;
    private GT t;
    private GN root;
    private int lineNo;
    private List<TextMob> outLines = new ArrayList<>();
    private Steps st;
    private boolean counting;

    @Override
    public void construct() {
        head = writeHeading("Classwork: Preorder");
        pause(0.5);
        List<MObject> q = new ArrayList<>();
        q.addAll(question(1, "Indent files as per their depth.", -250, 44, new String[]{"depth"}, Colors.BLUE));
        pause(0.9);
        q.addAll(question(2, "What is the code complexity?", -80, 44, new String[]{"complexity"}, Colors.ORANGE));
        q.add(subLine("Note that indentation time also needs to be considered.", -20, 32));
        pause(2.4);
        fadeOutAll(d(1.0), q);
        pause(0.4);
        indent();
        complexity();
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private GN files() {
        return g("home", g("somesh", g("acad", g("1.c"), g("2.c")), g("test.c")), g("saurabh", g("cv.pdf")));
    }

    private void hl(int line) { play(code.moveHl(line, d(0.22))); }

    private void stage(int num, String title) {
        mine.addAll(solutionHeader(num, title, -400));
        code = new CodeBox(new String[]{
                "void Tree::preorder(PtrToNode rr, int depth) {",
                "    if (rr) {",
                "        indent(depth);",
                "        rr->print();",
                "        for (auto child: rr->children)",
                "            preorder(child, depth + 1);",
                "    }",
                "}"}, -930, -310, 24, 36);
        code.typeIn(5.0);
        mine.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.3)));
        root = files();
        t = new GT(root, 520, -300, 125, 36, 26, 26, true, null);
        t.build(0.3);
        mine.addAll(t.parts());
        pause(0.4);
    }

    private void end() {
        unsay();
        fadeOutAll(d(0.9), mine);
        mine.clear();
        outLines.clear();
        pause(0.3);
    }

    /** A small badge on the top-left corner of a node (clear of the arrow that comes in at the top center). */
    private List<MObject> cornerBadge(GN n, String s, Color c, double w) {
        return chip(s, n.x - n.hw - 2 + w / 2 - 12, n.y - 30, w, 30, c, 20);
    }

    private void say(String s, Color c) { sayAt(s, c, 0, 482, 32); }

    // ── 1: indent by depth ───────────────────────────────────────────

    private void walkIndent(GN n, int depth) {
        hl(0);
        hl(1);
        List<Animation> a = new ArrayList<>();
        t.paint(a, n, VISIT_C, d(0.3));
        List<MObject> dt = cornerBadge(n, String.valueOf(depth), Colors.TEAL, 34);
        for (MObject m : dt) a.add(new FadeIn(m, d(0.3)));
        playAll(a);
        mine.addAll(dt);
        hl(2);
        // the indentation: as many steps to the right as the depth
        TextMob line = mono(n.name, -900 + 44 * depth, 100 + 38 * lineNo, 28, Colors.WHITE);
        line.setAlignment(TextAlignment.LEFT);
        lineNo++;
        if (depth > 0) {
            TextMob dots = mono("·".repeat(depth * 2), -900, line.getPosition().y(), 28, Colors.GRAY);
            dots.setAlignment(TextAlignment.LEFT);
            play(new FadeIn(dots, d(0.3)));
            mine.add(dots);
        }
        hl(3);
        play(new FadeIn(line, d(0.35)));
        mine.add(line);
        outLines.add(line);
        pause(0.3);
        for (GN c : n.kids) {
            hl(4);
            hl(5);
            walkIndent(c, depth + 1);
        }
        List<Animation> b = new ArrayList<>();
        t.paint(b, n, DONE_C, d(0.3));
        playAll(b);
    }

    private void indent() {
        stage(1, "Indent files by depth");
        RectMob panel = panel(-545, 245, 760, 380, Colors.GRAY, 0.1);
        panel.setFillColor(Colors.withAlpha(Color.web("#1B0F26"), 0.95));
        TextMob pl = label("output", -545, 35, 24, Colors.GRAY, false, true);
        play(new FadeIn(panel, d(0.5)), new FadeIn(pl, d(0.5)));
        mine.add(panel);
        mine.add(pl);
        TextMob lg = label("teal number = depth of the node", 520, -400, 26, Colors.TEAL, false, false);
        play(new FadeIn(lg, d(0.6)));
        mine.add(lg);
        say("The depth travels down as a parameter: +1 for every call to a child.", Colors.LIGHT_GRAY);
        pause(1.0);
        lineNo = 0;
        walkIndent(root, 0);
        say("Each name is pushed right by its depth: the folders line up like the tree.", Colors.GREEN);
        pause(3.0);
        end();
    }

    // ── 2: the complexity ────────────────────────────────────────────

    private void walkCount(GN n, int depth) {
        hl(0);
        hl(1);
        List<Animation> a = new ArrayList<>();
        t.paint(a, n, VISIT_C, d(0.25));
        playAll(a);
        st.tick();
        hl(2);
        List<MObject> cost = cornerBadge(n, "1+" + depth, Colors.GOLD, 62);
        List<Animation> ca = new ArrayList<>();
        for (MObject m : cost) ca.add(new FadeIn(m, d(0.25)));
        playAll(ca);
        mine.addAll(cost);
        for (int i = 0; i < depth; i++) {
            st.tick();
            pause(0.08);
        }
        hl(3);
        pause(0.15);
        for (GN c : n.kids) {
            hl(4);
            hl(5);
            walkCount(c, depth + 1);
        }
        List<Animation> b = new ArrayList<>();
        t.paint(b, n, DONE_C, d(0.25));
        playAll(b);
    }

    private void complexity() {
        stage(2, "Complexity");
        st = new Steps(-520, 140, 110);
        play(new FadeIn(st.num, d(0.4)), new FadeIn(st.cap, d(0.4)));
        mine.addAll(st.parts());
        TextMob lg = label("gold badge: 1 visit + depth spaces", 520, -400, 26, Colors.GOLD, false, false);
        play(new FadeIn(lg, d(0.6)));
        mine.add(lg);
        say("Count one step per node visited and one per space printed.", Colors.LIGHT_GRAY);
        pause(1.0);
        walkCount(root, 0);
        say("8 nodes + (0+1+2+3+3+2+1+2) = 8 + 14 = 22 steps.", Colors.GREEN);
        pause(2.6);
        List<Animation> down = new ArrayList<>();
        playAll(down);
        LaTeXMob f = latex("N + \\sum_{v} \\mathrm{depth}(v)", 60, -520, 300);
        play(new FadeIn(f, d(0.8)));
        mine.add(f);
        say("Visiting is O(N); the indentation adds the sum of all depths.", Colors.LIGHT_GRAY);
        pause(3.0);
        end();
        chain();
    }

    /** The worst case: a chain, where the depths add up to about N squared over two. */
    private void chain() {
        mine.addAll(solutionHeader(2, "Complexity: the worst case", -400));
        GN c = g("a", g("b", g("c", g("d", g("e", g("f"))))));
        GT ct = new GT(c, -430, -300, 100, 60, 28, 26, true, null);
        ct.build(0.35);
        mine.addAll(ct.parts());
        say("A chain: the node at depth d needs d spaces.", Colors.LIGHT_GRAY);
        pause(0.8);
        List<GN> ns = new ArrayList<>();
        preorderInto(c, ns);
        List<Animation> a = new ArrayList<>();
        int total = 0;
        for (int i = 0; i < ns.size(); i++) {
            GN n = ns.get(i);
            int cost = i + 1;
            total += cost;
            RectMob bar = new RectMob(70 * cost, 40).setCornerRadius(10);
            bar.setFillColor(Colors.withAlpha(Colors.ORANGE, 0.4));
            bar.setStrokeColor(Colors.ORANGE);
            bar.setStrokeWidth(2.5);
            bar.setPosition(-330 + 35 * cost, n.y);
            bar.setOpacity(0);
            add(bar);
            TextMob lab = label(String.valueOf(cost), -330 + 70 * cost + 30, n.y, 28, Colors.WHITE, false, true);
            a.add(new FadeInAt(bar, 0.5 * i, d(0.5)));
            a.add(new FadeInAt(lab, 0.5 * i, d(0.5)));
            mine.add(bar);
            mine.add(lab);
        }
        playAll(a);
        pause(0.6);
        TextMob tot = label("1 + 2 + 3 + 4 + 5 + 6 = " + total, 330, -170, 40, Colors.GOLD, false, true);
        play(new FadeIn(tot, d(0.6)));
        mine.add(tot);
        LaTeXMob f = latex("1 + 2 + \\cdots + N = \\frac{N(N+1)}{2}", 54, 330, -40);
        play(new FadeIn(f, d(0.8)));
        mine.add(f);
        pause(1.2);
        List<MObject> ans = chip("worst case: O(N²)", 330, 150, 520, 74, Colors.RED, 38);
        List<Animation> fa = new ArrayList<>();
        for (MObject m : ans) fa.add(new DropIn(m, 40, 0, d(0.6)));
        playAll(fa);
        mine.addAll(ans);
        say("In general O(N + sum of depths) <= O(N * height): wide, shallow trees stay close to O(N).", Colors.ORANGE);
        pause(3.4);
        end();
    }
}
