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
 * Standalone clip for slide 3 of the priority-queue deck: what a priority queue is and the ways to build one.
 *
 *   - the slide's lines in the pen-stroke style (the five structures with their costs come last), then they swipe away
 *   - <key, value> pairs as cards: the key is the priority, the value is the data; findMax picks the card with the
 *     largest key; the values then fade, because sometimes only the key is inserted
 *   - the five structures in a table (insert and findMax costs), then the same costs as bars for N = 1000: the two
 *     columns that are long for the list, the sorted list and the BST, and short for the AVL tree and the heap
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSPQDefinitionScene extends PDSListClipBase {

    private final List<MObject> mine = new ArrayList<>();
    private StrokeTextMob head;

    @Override
    public void construct() {
        head = writeHeading("Priority Queue");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Data structure contains <key, value> pairs.").kw("<key, value>", Colors.BLUE));
        s.add(ln(0, "Key is the priority, value is the associated data.").kw("priority", Colors.GOLD));
        s.add(ln(0, "Sometimes, only <key> is inserted."));
        s.add(ln(0, "At any point, an application should be able to retrieve"));
        s.add(ln(3, "the element with the maximum priority.").kw("maximum priority", Colors.GREEN));
        s.add(ln(0, "Multiple ways:"));
        s.add(ln(1, "Linked list (queue): insert = O(1), findMax = O(N)").kw("Linked list", Colors.ORANGE));
        s.add(ln(1, "Sorted list: insert = O(N), findMax = O(1)").kw("Sorted list", Colors.ORANGE));
        s.add(ln(1, "BST: insert = O(N), findMax = O(1)").kw("BST", Colors.ORANGE));
        s.add(ln(1, "AVL: insert = O(log N), findMax = O(1)").kw("AVL", Colors.ORANGE));
        s.add(ln(1, "Heap: insert = O(log N), findMax = O(1), efficient").kw("Heap", Colors.GOLD));
        List<List<MObject>> text = writeSlide(s, -340);
        pause(1.8);
        swipeAway(text);
        pause(0.4);

        pairs();
        costs();

        unsay();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    // ── <key, value> pairs ───────────────────────────────────────────

    private List<MObject> card(double cx, double cy, int key, String value, Color c) {
        RectMob p = panel(cx, cy, 300, 120, c, 0.16);
        CircleMob badge = new CircleMob(40);
        badge.setFillColor(Colors.withAlpha(Colors.GOLD, 0.35));
        badge.setStrokeColor(Colors.GOLD);
        badge.setStrokeWidth(3);
        badge.setPosition(cx - 95, cy);
        badge.setOpacity(0);
        add(badge);
        TextMob k = label(String.valueOf(key), cx - 95, cy, 44, Colors.WHITE, false, true);
        TextMob v = label(value, cx + 45, cy, 32, Colors.WHITE, false, false);
        List<MObject> l = new ArrayList<>();
        l.add(p);
        l.add(badge);
        l.add(k);
        l.add(v);
        return l;
    }

    private void pairs() {
        int[] keys = {7, 2, 9, 4, 5};
        String[] vals = {"print job", "email", "alarm", "backup", "update"};
        double[] xs = {-660, -330, 0, 330, 660};
        List<List<MObject>> cards = new ArrayList<>();
        List<Animation> an = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<MObject> c = card(xs[i], -120, keys[i], vals[i], Colors.BLUE);
            cards.add(c);
            for (MObject m : c) an.add(new FadeInAt(m, 0.45 * i, d(0.7)));
            mine.addAll(c);
        }
        playAll(an);
        pause(0.5);
        TextMob kl = label("key = priority", xs[0] - 40, -255, 28, Colors.GOLD, false, true);
        TextMob vl = label("value = the data", xs[0] + 200, -215, 28, Colors.LIGHT_GRAY, false, false);
        play(new FadeIn(kl, d(0.5)), new FadeIn(vl, d(0.5)));
        mine.add(kl);
        mine.add(vl);
        say("A priority queue holds <key, value> pairs. The key is the priority, the value is the data.", Colors.LIGHT_GRAY, 440, 32);
        pause(2.6);
        play(new FadeOut(kl, d(0.4)), new FadeOut(vl, d(0.4)));

        // findMax
        say("At any point we must be able to retrieve the element with the maximum priority: findMax.", Colors.GOLD, 440, 32);
        pause(1.0);
        List<Animation> ring = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Color c = i == 2 ? Colors.GREEN : Colors.BLUE;
            ring.add(new ColorChange((RectMob) cards.get(i).get(0), Colors.withAlpha(c, i == 2 ? 0.34 : 0.16), d(0.5)));
            ring.add(new ColorChange((RectMob) cards.get(i).get(0), Colors.withAlpha(c, 0.9), d(0.5), ColorChange.Target.STROKE));
        }
        playAll(ring);
        pause(1.0);
        List<Animation> lift = new ArrayList<>();
        for (MObject m : cards.get(2)) lift.add(new MoveTo(m, m.getPosition().x(), m.getPosition().y() + 230, d(0.8)));
        playAll(lift);
        TextMob res = label("findMax gives  9  (alarm)", 0, 250, 40, Colors.GREEN, false, true);
        play(new FadeIn(res, d(0.6)));
        mine.add(res);
        pause(2.4);
        play(new FadeOut(res, d(0.4)));
        List<Animation> back = new ArrayList<>();
        for (MObject m : cards.get(2)) back.add(new MoveTo(m, m.getPosition().x(), m.getPosition().y() - 230, d(0.8)));
        for (int i = 0; i < 5; i++) {
            back.add(new ColorChange((RectMob) cards.get(i).get(0), Colors.withAlpha(Colors.BLUE, 0.16), d(0.5)));
            back.add(new ColorChange((RectMob) cards.get(i).get(0), Colors.withAlpha(Colors.BLUE, 0.9), d(0.5), ColorChange.Target.STROKE));
        }
        playAll(back);

        // only the key
        say("Sometimes only the <key> is inserted: the priority itself is the data.", Colors.LIGHT_GRAY, 440, 32);
        List<Animation> fadeVals = new ArrayList<>();
        for (List<MObject> c : cards) fadeVals.add(new FadeOut(c.get(3), d(0.8)));
        playAll(fadeVals);
        pause(2.6);
        fadeOutAll(d(0.9), mine);
        mine.clear();
        unsay();
        pause(0.3);
    }

    // ── multiple ways ────────────────────────────────────────────────

    private void costs() {
        String[][] rows = {
                {"Linked list (queue)", "O(1)", "O(N)"},
                {"Sorted list", "O(N)", "O(1)"},
                {"BST", "O(N)", "O(1)"},
                {"AVL", "O(log N)", "O(1)"},
                {"Heap", "O(log N)", "O(1)"}};
        Color bad = Colors.RED, good = Colors.GREEN, ok = Colors.GOLD;
        Color[][] tint = {
                {null, good, bad}, {null, bad, good}, {null, bad, good}, {null, ok, good}, {Colors.GOLD, ok, good}};
        List<MObject> table = dropTable(new String[]{"Multiple ways", "insert", "findMax"}, rows, new double[]{480, 300, 300}, tint, -330);
        mine.addAll(table);
        pause(2.0);
        say("Each way is cheap at one end and costly at the other, except the AVL tree and the heap.", Colors.LIGHT_GRAY, 440, 32);
        pause(3.0);
        List<MObject> hl = outlineRow(4, -330);
        pause(2.0);
        say("The heap is the efficient one: it is also much simpler than an AVL tree, and it needs no pointers.", Colors.GOLD, 440, 32);
        pause(3.0);
        fadeOutAll(d(0.9), mine);
        mine.clear();
        unsay();
        pause(0.3);

        // the same costs as bars, N = 1000
        TextMob nLab = label("steps for ONE operation when N = 1000", 0, -330, 36, Colors.WHITE, false, true);
        play(new FadeIn(nLab, d(0.5)));
        mine.add(nLab);
        String[] names = {"Linked list", "Sorted list", "BST (worst case)", "AVL", "Heap"};
        bars("insert", -260, names, new int[]{1, 1000, 1000, 10, 10});
        bars("findMax", 60, names, new int[]{1000, 1, 1, 1, 1});
        say("log N is tiny: 10 steps instead of 1000. That is why heaps run priority queues.", Colors.GREEN, 440, 32);
        pause(4.0);
    }

    private List<MObject> outlineRow(int row, double headY) {
        List<MObject> l = new ArrayList<>();
        RectMob r = new RectMob(1120, 72).setCornerRadius(12);
        r.setFillColor(Color.TRANSPARENT);
        r.setStrokeColor(Colors.GOLD);
        r.setStrokeWidth(4);
        r.setPosition(0, headY + 68 * (row + 1));
        r.setOpacity(0);
        add(r);
        play(new FadeIn(r, d(0.6)));
        mine.add(r);
        l.add(r);
        return l;
    }

    private void bars(String title, double topY, String[] names, int[] vals) {
        TextMob t = label(title, -880, topY, 34, Colors.GOLD, true, true);
        play(new FadeIn(t, d(0.4)));
        mine.add(t);
        Color[] cols = {Colors.TEAL, Colors.ORANGE, Colors.ORANGE, Colors.BLUE, Colors.GOLD};
        List<Animation> an = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            double y = topY + 50 + 46 * i;
            TextMob n = label(names[i], -880, y, 26, Colors.LIGHT_GRAY, true, false);
            double w = Math.max(8, vals[i] * 0.9);
            RectMob bar = new RectMob(w, 30).setCornerRadius(6);
            bar.setFillColor(Colors.withAlpha(cols[i], 0.45));
            bar.setStrokeColor(cols[i]);
            bar.setStrokeWidth(2.4);
            bar.setPosition(-540 + w / 2, y);
            bar.setOpacity(0);
            add(bar);
            TextMob v = label(String.valueOf(vals[i]), -540 + w + 40, y, 28, Colors.WHITE, false, true);
            an.add(new FadeIn(n, d(0.4)));
            an.add(new GrowRight(bar, -540, y, w, 30, d(0.9)));
            an.add(new FadeInAt(v, 0.6, d(0.4)));
            mine.add(n);
            mine.add(bar);
            mine.add(v);
        }
        playAll(an);
        pause(0.5);
    }
}
