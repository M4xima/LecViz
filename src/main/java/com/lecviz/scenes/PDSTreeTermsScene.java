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
 * Standalone clip for slide 7 of the trees deck: more nomenclature.
 *
 *   - the slide's list one line at a time
 *   - sibling on a tree (same parent), then the slide's question on a star tree: with N nodes the most
 *     siblings a node can have is N - 2 (the parent and the node itself are not siblings)
 *   - grandparent / grandchild, ancestor / descendant, path and its length (edges walked),
 *     depth (distance from the root, row by row) and height (distance down to the deepest leaf)
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeTermsScene extends PDSTreeClipBase {

    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();
    private GT t;
    private GN rootN;
    private TextMob termLab;

    @Override
    public void construct() {
        head = writeHeading("More Nomenclature");
        pause(0.5);
        slideText();
        t = new GT(rootN = g("A", g("B", g("E", g("I")), g("F")), g("C"), g("D", g("G"), g("H"))),
                0, -300, 140, 100, 30, 32, false, null);
        t.build(0.3);
        mine.addAll(t.parts());
        pause(0.4);
        sibling();
        starTree();
        grand();
        ancestors();
        pathLength();
        depthHeight();
        unsay();
        fadeOutAll(d(1.0), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void slideText() {
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Sibling"));
        s.add(ln(1, "What is the maximum number of siblings a node"));
        s.add(ln(2, "may have in an N node tree?"));
        s.add(ln(0, "Grandparent, grandchild"));
        s.add(ln(0, "Ancestor, descendant"));
        s.add(ln(0, "Path, length"));
        s.add(ln(0, "Height, depth"));
        List<List<MObject>> text = writeSlide(s, -300);
        pause(1.4);
        swipeAway(text);
        pause(0.4);
    }

    // ── helpers ──────────────────────────────────────────────────────

    private void term(String s) {
        TextMob old = termLab;
        TextMob n = label(s, 0, 300, 48, Colors.GOLD, false, true);
        List<Animation> a = new ArrayList<>();
        if (old != null) a.add(new FadeOut(old, d(0.3)));
        a.add(new FadeIn(n, d(0.5)));
        playAll(a);
        if (old != null) remove(old);
        termLab = n;
    }

    private void lit(List<GN> ns, Color c, double dur) {
        List<Animation> a = new ArrayList<>();
        for (GN n : ns) t.paint(a, n, c, dur);
        playAll(a);
    }

    private void unlit(List<GN> ns, double dur) {
        List<Animation> a = new ArrayList<>();
        for (GN n : ns) t.paintDefault(a, n, dur);
        playAll(a);
    }

    private void say(String s) { sayAt(s, Colors.LIGHT_GRAY, 0, 385, 36); }

    private GN n(int... path) { return at(rootN, path); }

    // ── sibling ──────────────────────────────────────────────────────

    private void sibling() {
        term("Sibling");
        say("Siblings share the same parent.");
        List<GN> bcd = List.of(n(0), n(1), n(2));
        lit(bcd, Colors.GOLD, d(0.6));
        TextMob l = label("children of A", n(1).x, n(1).y + 62, 28, Colors.GOLD, false, true);
        play(new FadeIn(l, d(0.5)));
        pause(1.4);
        play(new FadeOut(l, d(0.4)));
        remove(l);
        unlit(bcd, d(0.5));
        List<GN> gh = List.of(n(2, 0), n(2, 1));
        lit(gh, Colors.TEAL, d(0.6));
        say("G and H are siblings too: the children of D.");
        pause(1.6);
        unlit(gh, d(0.5));
    }

    private void starTree() {
        // the slide's question, on a star
        fadeOutAll(d(0.7), mine);
        mine.clear();
        t = null;
        say("What is the maximum number of siblings in an N node tree?");
        GT star = tree(g("", g(""), g(""), g(""), g(""), g(""), g("")), 0, -250, 160, 56, 26, 1, false);
        star.build(0.3);
        mine.addAll(star.parts());
        TextMob nl = label("N = 7", 400, -250, 44, Colors.WHITE, false, true);
        play(new FadeIn(nl, d(0.5)));
        mine.add(nl);
        pause(0.8);
        GN rt = star.root;
        List<Animation> a = new ArrayList<>();
        star.paint(a, rt, ROOT_C, d(0.5));
        GN me = rt.kids.get(2);
        star.paint(a, me, Colors.WHITE, d(0.5));
        playAll(a);
        TextMob ml = label("this node", me.x, me.y + 62, 26, Colors.WHITE, false, true);
        play(new FadeIn(ml, d(0.5)));
        mine.add(ml);
        say("Put every other node under the root. Count one node's siblings.");
        pause(1.0);
        int k = 0;
        TextMob cnt = label("0", 400, -150, 84, Colors.GOLD, false, true);
        TextMob cc = label("siblings", 400, -80, 30, Colors.GRAY, false, true);
        play(new FadeIn(cnt, d(0.4)), new FadeIn(cc, d(0.4)));
        mine.add(cnt);
        mine.add(cc);
        for (GN o : rt.kids) {
            if (o == me) continue;
            List<Animation> b = new ArrayList<>();
            star.paint(b, o, Colors.GOLD, d(0.4));
            playAll(b);
            k++;
            cnt.setText(String.valueOf(k));
            pause(0.4);
        }
        pause(0.5);
        say("The root and the node itself are not siblings: N - 2.");
        LaTeXMob f = latex("N - 2 = 7 - 2 = 5", 58, 330, 40);
        play(new FadeIn(f, d(0.8)));
        mine.add(f);
        pause(2.8);
        fadeOutAll(d(0.8), mine);
        mine.clear();
        // back to the tree for the other terms
        t = new GT(rootN = g("A", g("B", g("E", g("I")), g("F")), g("C"), g("D", g("G"), g("H"))),
                0, -300, 140, 100, 30, 32, false, null);
        t.build(0.2);
        mine.addAll(t.parts());
        pause(0.4);
    }

    // ── grandparent, ancestor, path, depth, height ───────────────────

    private void grand() {
        term("Grandparent, grandchild");
        say("A is the parent of B, and B is the parent of E: A is E's grandparent.");
        GN a = rootN, e = n(0, 0);
        lit(List.of(a, n(0), e), Colors.GOLD, d(0.6));
        TextMob l1 = label("grandparent", a.x - 160, a.y, 28, Colors.GOLD, false, true);
        TextMob l2 = label("grandchild", e.x - 150, e.y, 28, Colors.GOLD, false, true);
        play(new FadeIn(l1, d(0.5)), new FadeIn(l2, d(0.5)));
        pause(2.4);
        play(new FadeOut(l1, d(0.4)), new FadeOut(l2, d(0.4)));
        remove(l1);
        remove(l2);
        unlit(List.of(a, n(0), e), d(0.5));
    }

    private void ancestors() {
        term("Ancestor, descendant");
        say("Everyone on the way up from I to the root is an ancestor of I.");
        GN i = n(0, 0, 0);
        List<GN> anc = List.of(n(0, 0), n(0), rootN);
        lit(List.of(i), Colors.WHITE, d(0.4));
        for (GN x : anc) {
            lit(List.of(x), Colors.GOLD, d(0.5));
            pause(0.2);
        }
        pause(2.0);
        unlit(anc, d(0.5));
        unlit(List.of(i), d(0.4));
        GN b = n(0);
        List<GN> desc = List.of(n(0, 0), n(0, 1), n(0, 0, 0));
        lit(List.of(b), Colors.WHITE, d(0.4));
        lit(desc, Colors.TEAL, d(0.6));
        say("Everything below B is a descendant of B.");
        pause(2.2);
        unlit(desc, d(0.5));
        unlit(List.of(b), d(0.4));
    }

    private void pathLength() {
        term("Path, length");
        say("A path is a walk along edges; its length is the number of edges.");
        List<GN> path = List.of(rootN, n(0), n(0, 0), n(0, 0, 0));
        lit(List.of(rootN), Colors.GOLD, d(0.4));
        TextMob cnt = label("length 0", 440, -150, 52, Colors.GOLD, false, true);
        play(new FadeIn(cnt, d(0.4)));
        mine.add(cnt);
        for (int k = 1; k < path.size(); k++) {
            List<Animation> a = new ArrayList<>();
            t.paintEdge(a, path.get(k), Colors.GOLD, d(0.5));
            t.paint(a, path.get(k), Colors.GOLD, d(0.5));
            playAll(a);
            cnt.setText("length " + k);
            pause(0.4);
        }
        say("A - B - E - I is a path of length 3.");
        pause(2.6);
        List<Animation> b = new ArrayList<>();
        for (GN x : path) {
            t.paintDefault(b, x, d(0.5));
            t.paintEdge(b, x, Colors.withAlpha(Colors.LIGHT_GRAY, 0.8), d(0.5));
        }
        b.add(new FadeOut(cnt, d(0.5)));
        playAll(b);
    }

    private void depthHeight() {
        term("Depth, height");
        say("Depth: how far a node is from the root.");
        List<MObject> tags = new ArrayList<>();
        int maxD = 3;
        for (int dp = 0; dp <= maxD; dp++) {
            TextMob tg = label("depth " + dp, 470, -300 + 140 * dp, 32, Colors.TEAL, false, true);
            List<Animation> a = new ArrayList<>();
            a.add(new FadeIn(tg, d(0.5)));
            for (GN x : t.nodes) if (x.depth == dp) t.paint(a, x, Colors.TEAL, d(0.5));
            playAll(a);
            tags.add(tg);
            pause(0.5);
            List<Animation> b = new ArrayList<>();
            for (GN x : t.nodes) if (x.depth == dp) t.paintDefault(b, x, d(0.5));
            playAll(b);
        }
        mine.addAll(tags);
        pause(0.6);
        say("Height: how far down the deepest leaf below a node is.");
        List<GN> order = new ArrayList<>();
        postorderInto(rootN, order);
        List<MObject> hs = new ArrayList<>();
        // one level at a time, bottom up
        for (int dp = maxD; dp >= 0; dp--) {
            List<Animation> a = new ArrayList<>();
            for (GN x : t.nodes) {
                if (x.depth != dp) continue;
                TextMob h = label("h = " + height(x), x.x + x.hw + 42, x.y, 24, Colors.ORANGE, false, true);
                a.add(new FadeIn(h, d(0.5)));
                hs.add(h);
            }
            playAll(a);
            pause(0.5);
        }
        mine.addAll(hs);
        pause(0.8);
        say("The height of the whole tree is the height of its root: 3.");
        List<Animation> a = new ArrayList<>();
        t.paint(a, rootN, Colors.ORANGE, d(0.5));
        playAll(a);
        pause(0.5);
        TextMob note = label("(counted in edges: a leaf has height 0, the root has depth 0)", 0, 440, 26, Colors.GRAY, false, false);
        play(new FadeIn(note, d(0.6)));
        mine.add(note);
        mine.add(termLab);
        pause(2.8);
    }
}
