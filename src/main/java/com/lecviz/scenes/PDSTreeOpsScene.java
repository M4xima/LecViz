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
 * Standalone clip for slide 28 of the trees deck: operations on trees.
 *
 *   - the slide's points one at a time (insert, remove, search and their sub-questions)
 *   - insert: addChild with a pointer to the parent just links a new node in: constant time
 *   - remove: update the parent's pointer to NULL (O(1)); the node's children go with it, and freeing the
 *     memory of the whole subtree touches every node of it: O(1) or O(N) depending on the answer
 *   - search: a traversal; duplicates are allowed, so every node is looked at: O(N)
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeOpsScene extends PDSTreeClipBase {

    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();
    private GT t;
    private GN root;

    @Override
    public void construct() {
        head = writeHeading("Operations on Trees");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Insert: our addChild would take care of this.").kw("Insert", Colors.MAROON));
        s.add(ln(1, "Given pointers, this is constant time operation."));
        s.add(ln(0, "Remove: Update parent's pointer to NULL (and").kw("Remove", Colors.MAROON));
        s.add(ln(3, "free memory)."));
        s.add(ln(1, "What if the node getting removed has children?"));
        s.add(ln(1, "Based on the above answer, the complexity could"));
        s.add(ln(2, "be O(1) or O(N)"));
        s.add(ln(0, "Search: Our tree traversals can help.").kw("Search", Colors.MAROON));
        s.add(ln(1, "Can a tree contain duplicate values?"));
        s.add(ln(1, "This is O(N), since the whole tree needs to be"));
        s.add(ln(2, "searched in the worst case.").kw("in the worst case", Colors.BLUE));
        List<List<MObject>> text = writeSlide(s, -340);
        pause(1.4);
        swipeAway(text);
        pause(0.4);
        insert();
        remove();
        search();
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private GN numbers() {
        return g("12", g("7", g("4"), g("25")), g("31", g("7"), g("9")), g("19"));
    }

    private void stage(int num, String title) {
        mine.addAll(solutionHeader(num, title, -400));
        root = numbers();
        t = new GT(root, -100, -280, 130, 70, 30, 30, true, null);
        t.build(0.3);
        mine.addAll(t.parts());
    }

    private void narr(String s, Color c) { sayAt(s, c, 0, 440, 34); }

    private void wrap() {
        unsay();
        fadeOutAll(d(0.9), mine);
        mine.clear();
        pause(0.3);
    }

    private void answer(String text, double y, Color c) {
        List<MObject> ans = chip(text, 610, y, 620, 74, c, 40);
        List<Animation> a = new ArrayList<>();
        for (MObject m : ans) a.add(new DropIn(m, 40, 0, d(0.6)));
        playAll(a);
        mine.addAll(ans);
    }

    // ── insert ───────────────────────────────────────────────────────

    private void insert() {
        stage(1, "Insert");
        GN p = at(root, 2);
        List<Animation> a = new ArrayList<>();
        t.paint(a, p, Colors.WHITE, d(0.5));
        playAll(a);
        TextMob pl = label("parent", p.x + 90, p.y - 12, 28, Colors.WHITE, false, true);
        play(new FadeIn(pl, d(0.4)));
        mine.add(pl);
        narr("We hold a pointer to the parent: addChild(parent, 18).", Colors.LIGHT_GRAY);
        pause(1.2);
        // a new node joins under the parent
        CircleMob nn = new CircleMob(30);
        nn.setFillColor(Colors.withAlpha(Colors.GREEN, 0.4));
        nn.setStrokeColor(Colors.GREEN);
        nn.setStrokeWidth(3);
        double nx = p.x + 130, ny = p.y + 130;
        nn.setPosition(nx, ny);
        nn.setOpacity(0);
        add(nn);
        TextMob nt = label("18", nx, ny, 30, Colors.WHITE, false, true);
        Link e = arrow(p.x + 18, p.y + 30, nx - 12, ny - 34, Colors.GREEN, 3.2);
        play(new FadeIn(nn, d(0.6)), new FadeIn(nt, d(0.6)));
        play(new DrawLink(e, d(0.8)));
        mine.add(nn);
        mine.add(nt);
        mine.add(e);
        narr("Link the new node in: a couple of pointer updates, nothing else is touched.", Colors.GREEN);
        pause(1.6);
        TextMob steps = label("steps: 2", 600, -120, 56, Colors.GOLD, false, true);
        play(new FadeIn(steps, d(0.5)));
        mine.add(steps);
        answer("O(1)", 20, Colors.GREEN);
        pause(2.6);
        wrap();
    }

    // ── remove ───────────────────────────────────────────────────────

    private void remove() {
        stage(2, "Remove");
        GN x = at(root, 1);
        narr("Remove 31: update its parent's pointer to NULL.", Colors.LIGHT_GRAY);
        pause(0.8);
        List<Animation> a = new ArrayList<>();
        t.paint(a, x, Colors.RED, d(0.5));
        playAll(a);
        pause(0.5);
        List<Animation> cut = new ArrayList<>();
        cut.add(new FadeOut(x.edge, d(0.8)));
        List<GN> sub = new ArrayList<>();
        preorderInto(x, sub);
        for (GN n : sub) {
            if (n != x) t.paint(cut, n, Colors.GRAY, d(0.8));
            else t.paint(cut, n, Colors.GRAY, d(0.8));
        }
        playAll(cut);
        narr("One pointer changed: O(1). But 31 had children: they are cut off with it.", Colors.ORANGE);
        TextMob q = label("what about its children?", 610, -230, 32, Colors.ORANGE, false, true);
        play(new FadeIn(q, d(0.5)));
        mine.add(q);
        pause(2.0);
        // freeing the memory touches every node of the subtree
        narr("To free the memory we must visit the whole subtree below it.", Colors.LIGHT_GRAY);
        TextMob cnt = label("freed: 0", 600, -120, 56, Colors.GOLD, false, true);
        play(new FadeIn(cnt, d(0.5)));
        mine.add(cnt);
        int k = 0;
        List<GN> post = new ArrayList<>();
        postorderInto(x, post);
        for (GN n : post) {
            List<Animation> f = new ArrayList<>();
            t.hideNode(f, n, d(0.5));
            playAll(f);
            k++;
            cnt.setText("freed: " + k);
            pause(0.3);
        }
        pause(0.5);
        narr("Freeing a leaf costs 1, freeing a big subtree costs its size.", Colors.ORANGE);
        answer("O(1) to unlink", -10, Colors.GREEN);
        List<MObject> a2 = chip("O(N) to free a subtree", 610, 100, 620, 74, Colors.ORANGE, 38);
        List<Animation> aa = new ArrayList<>();
        for (MObject m : a2) aa.add(new DropIn(m, 40, 0, d(0.6)));
        playAll(aa);
        mine.addAll(a2);
        pause(3.0);
        wrap();
    }

    // ── search ───────────────────────────────────────────────────────

    private void search() {
        stage(3, "Search");
        narr("Search for 7. Can a tree contain duplicate values? Nothing forbids it.", Colors.LIGHT_GRAY);
        pause(1.0);
        List<GN> pre = new ArrayList<>();
        preorderInto(root, pre);
        TextMob cnt = label("looked at: 0", 600, -120, 52, Colors.GOLD, false, true);
        TextMob found = label("found: 0", 600, -50, 40, Colors.GREEN, false, true);
        play(new FadeIn(cnt, d(0.5)), new FadeIn(found, d(0.5)));
        mine.add(cnt);
        mine.add(found);
        int k = 0, f = 0;
        for (GN n : pre) {
            List<Animation> a = new ArrayList<>();
            t.paint(a, n, Colors.GOLD, d(0.3));
            playAll(a);
            k++;
            cnt.setText("looked at: " + k);
            boolean hit = n.name.equals("7");
            if (hit) {
                f++;
                found.setText("found: " + f);
                t.paintNow(n, Colors.GREEN, d(0.3));
                pause(0.6);
            } else {
                t.paintNow(n, Colors.GRAY, d(0.3));
            }
            pause(0.3);
        }
        narr("It can occur twice, so we cannot stop at the first 7: the whole tree is searched.", Colors.GREEN);
        answer("O(N) in the worst case", 40, Colors.GREEN);
        pause(3.0);
        wrap();
    }
}
