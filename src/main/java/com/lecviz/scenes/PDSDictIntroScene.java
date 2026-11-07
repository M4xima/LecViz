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
 * Standalone clip for slides 1-2 of the dictionary deck: the title card and "Dictionary Instances".
 *
 *   - the title is written in pen-stroke above a small search tree that grows level by level
 *   - the slide's three lines come one at a time and swipe away
 *   - what a dictionary is (keys mapped to values), then the two instances side by side: a search tree
 *     (smaller keys to the left) and a hash table (keys dropping into cells)
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSDictIntroScene extends PDSDictClipBase {

    @Override
    public void construct() {
        titleCard();
        instances();
    }

    // ── slide 1 ──────────────────────────────────────────────────────

    private void titleCard() {
        StrokeTextMob title = stroke("Dictionaries", 0, -390, 104, Colors.WHITE, true);
        play(new Write(title, d(2.4)));
        GT tr = bst(bstOf(6, 2, 8, 1, 4, 3, 5), 0, -260, 100, 28, 28);
        tr.build(0.3);
        TextMob who = label("Rupesh Nasre.", 0, 250, 46, Colors.WHITE, false, false);
        TextMob mail = label("rupesh@iitm.ac.in", 0, 308, 34, Colors.BLUE, false, false);
        TextMob when = label("July 2019", 0, 420, 30, Colors.GRAY, false, false);
        List<Animation> a = new ArrayList<>();
        a.add(new FadeIn(who, d(0.8)));
        a.add(new FadeInAt(mail, 0.3, d(0.8)));
        a.add(new FadeInAt(when, 0.6, d(0.8)));
        playAll(a);
        pause(2.2);
        List<MObject> all = new ArrayList<>(tr.parts());
        all.add(title);
        all.add(who);
        all.add(mail);
        all.add(when);
        fadeOutAll(d(1.0), all);
        pause(0.3);
    }

    // ── slide 2 ──────────────────────────────────────────────────────

    private void instances() {
        StrokeTextMob head = dropHeading("Dictionary Instances");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Binary Search Trees").kw("Search", Colors.BLUE));
        s.add(ln(1, "Balanced BST").kw("Balanced", Colors.TEAL));
        s.add(ln(0, "Hash Tables").kw("Hash", Colors.GOLD));
        List<List<MObject>> text = writeSlide(s, -300);
        pause(1.2);
        swipeAway(text);
        pause(0.4);

        // what a dictionary is
        StrokeTextMob cap = stroke("A dictionary maps keys to values.", 0, -380, 44, Colors.WHITE, false);
        play(new Write(cap, d(2.2)));
        String[] keys = {"cat", "dog", "owl"};
        String[] vals = {"4", "9", "2"};
        List<MObject> mine = new ArrayList<>();
        mine.add(cap);
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            double y = -280 + i * 84;
            List<MObject> k = chip(keys[i], -190, y, 190, 58, Colors.BLUE, 30);
            List<MObject> v = chip(vals[i], 190, y, 120, 58, Colors.GOLD, 30);
            Link l = arrow(-80, y, 118, y, Colors.LIGHT_GRAY, 3);
            fade(in, k, d(0.5));
            fade(in, v, d(0.5));
            in.add(new DrawLinkAt(l, 0.15, d(0.6)));
            mine.addAll(k);
            mine.addAll(v);
            mine.add(l);
        }
        playAll(in);
        pause(1.4);
        fadeOutAll(d(0.8), mine);

        // the two instances
        StrokeTextMob cap2 = stroke("Two ways to build one.", 0, -380, 44, Colors.WHITE, false);
        play(new Write(cap2, d(1.8)));
        RectMob left = panel(-445, 20, 800, 640, Colors.BLUE, 0.07);
        RectMob right = panel(445, 20, 800, 640, Colors.GOLD, 0.07);
        TextMob lt = label("Binary Search Trees", -445, -250, 42, Colors.BLUE, false, true);
        TextMob rt = label("Hash Tables", 445, -250, 42, Colors.GOLD, false, true);
        List<Animation> a = new ArrayList<>();
        a.add(new FadeIn(left, d(0.6)));
        a.add(new FadeIn(right, d(0.6)));
        a.add(new FadeIn(lt, d(0.6)));
        a.add(new FadeIn(rt, d(0.6)));
        playAll(a);

        GT mini = bst(bstOf(6, 2, 8, 1, 4, 3, 5), -445, -150, 84, 24, 26);
        mini.build(0.2);
        TextMob ls = label("keys kept in order: smaller to the left", -445, 150, 30, Colors.LIGHT_GRAY, false, false);
        List<MObject> bal = chip("Balanced BST", -445, 235, 330, 60, Colors.TEAL, 32);
        List<Animation> b = new ArrayList<>();
        b.add(new FadeIn(ls, d(0.7)));
        fade(b, bal, d(0.7));
        playAll(b);
        pause(0.5);

        // the hash table side: three keys drop into cells
        List<RectMob> cells = new ArrayList<>();
        List<TextMob> idx = new ArrayList<>();
        List<Animation> c = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            double x = 445 + (i - 3) * 100;
            RectMob cell = new RectMob(86, 86).setCornerRadius(10);
            cell.setFillColor(Colors.withAlpha(Colors.GOLD, 0.16));
            cell.setStrokeColor(Colors.withAlpha(Colors.GOLD, 0.9));
            cell.setStrokeWidth(2.4);
            cell.setPosition(x, 40);
            cell.setOpacity(0);
            add(cell);
            TextMob n = label(String.valueOf(i), x, 112, 24, Colors.GRAY, false, false);
            cells.add(cell);
            idx.add(n);
            c.add(new FadeIn(cell, d(0.5)));
            c.add(new FadeIn(n, d(0.5)));
        }
        playAll(c);
        int[] slot = {2, 5, 0};
        List<List<MObject>> flying = new ArrayList<>();
        List<Animation> fly = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            double x0 = 445 + (i - 1) * 190;
            List<MObject> k = chip(keys[i], x0, -130, 120, 50, Colors.BLUE, 26);
            flying.add(k);
            fade(fly, k, d(0.5));
        }
        playAll(fly);
        pause(0.3);
        List<Animation> drop = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            double x = 445 + (slot[i] - 3) * 100;
            for (MObject m : flying.get(i)) drop.add(new MoveTo(m, x, 40, d(0.9)).setEasing(com.lecviz.utils.Easing.EASE_IN_OUT));
            drop.add(new ScaleTo(flying.get(i).get(0), 0.68, d(0.9)));
            drop.add(new ScaleTo(flying.get(i).get(1), 0.68, d(0.9)));
        }
        playAll(drop);
        TextMob hs = label("index = hash(key)", 445, 210, 32, Colors.LIGHT_GRAY, false, false);
        play(new FadeIn(hs, d(0.7)));
        TextMob hs2 = label("keys scattered over a fixed array", 445, 262, 28, Colors.GRAY, false, false);
        play(new FadeIn(hs2, d(0.7)));
        pause(2.6);

        List<MObject> all = new ArrayList<>(mini.parts());
        all.add(cap2);
        all.add(left);
        all.add(right);
        all.add(lt);
        all.add(rt);
        all.add(ls);
        all.addAll(bal);
        all.addAll(cells);
        all.addAll(idx);
        for (List<MObject> f : flying) all.addAll(f);
        all.add(hs);
        all.add(hs2);
        all.add(head);
        fadeOutAll(d(1.2), all);
        pause(0.3);
    }
}
