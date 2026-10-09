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
 * Standalone clip for slide 17 of the trees deck: story so far.
 *
 *   - the slide's points one at a time
 *   - the summary as a diagram of two cards: general trees (any number of children: employees, files) and
 *     special trees (fixed or bounded number of children, children may be missing: expressions, boolean flows),
 *     each with a small drawn picture
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeStoryScene extends PDSTreeClipBase {

    private StrokeTextMob head;

    @Override
    public void construct() {
        head = writeHeading("Story so far...");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "General trees").kw("General trees", Colors.MAROON));
        s.add(ln(1, "arbitrary number of children"));
        s.add(ln(1, "Resembles several situations such as employees,"));
        s.add(ln(2, "files, ..."));
        s.add(ln(0, "Special trees").kw("Special trees", Colors.MAROON));
        s.add(ln(1, "Fixed / bounded number of children"));
        s.add(ln(1, "Resembles situations such as expressions, boolean"));
        s.add(ln(2, "flows, ..."));
        s.add(ln(1, "All the children may not be present."));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(1.4);
        swipeAway(text);
        pause(0.4);
        cards();
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void cards() {
        List<MObject> mine = new ArrayList<>();
        // left: general trees
        RectMob c1 = panel(-470, -20, 820, 760, Colors.BLUE, 0.07);
        TextMob n1 = label("General trees", -470, -340, 46, Colors.BLUE, false, true);
        TextMob s1 = label("any number of children", -470, -285, 28, Colors.LIGHT_GRAY, false, false);
        play(new FadeIn(c1, d(0.6)), new FadeIn(n1, d(0.6)), new FadeIn(s1, d(0.6)));
        mine.add(c1);
        mine.add(n1);
        mine.add(s1);
        GT g1 = tree(idTree(), -470, -200, 110, 20, 20, 22, true);
        g1.build(0.3);
        mine.addAll(g1.parts());
        String[] ex1 = {"employees", "files and folders"};
        for (int i = 0; i < 2; i++) {
            List<MObject> ch = chip(ex1[i], -470 - 190 + i * 380, 265, 340, 60, Colors.BLUE, 28);
            List<Animation> a = new ArrayList<>();
            for (MObject m : ch) a.add(new DropIn(m, 40, 0, d(0.6)));
            playAll(a);
            mine.addAll(ch);
        }
        pause(0.6);

        // right: special trees
        RectMob c2 = panel(470, -20, 820, 760, Colors.GREEN, 0.07);
        TextMob n2 = label("Special trees", 470, -340, 46, Colors.GREEN, false, true);
        TextMob s2 = label("fixed or bounded number of children", 470, -285, 28, Colors.LIGHT_GRAY, false, false);
        play(new FadeIn(c2, d(0.6)), new FadeIn(n2, d(0.6)), new FadeIn(s2, d(0.6)));
        mine.add(c2);
        mine.add(n2);
        mine.add(s2);
        // at most two children: some are missing (dashed ghosts)
        GN full = g("+", g("a"), g("*", g("b"), g("c")));
        GT g2 = new GT(full, 470, -200, 120, 130, 30, 30, false, n -> n.leaf() ? Colors.BLUE : Colors.GOLD);
        g2.build(0.3);
        mine.addAll(g2.parts());
        // the missing child under the left-hand leaves
        List<MObject> ghosts = new ArrayList<>();
        List<Animation> ga = new ArrayList<>();
        for (GN leaf : new GN[]{at(full, 0), at(full, 1, 0), at(full, 1, 1)}) {
            for (int side = -1; side <= 1; side += 2) {
                CircleMob gh = new CircleMob(18);
                gh.setFillColor(Color.TRANSPARENT);
                gh.setStrokeColor(Colors.withAlpha(Colors.WHITE, 0.3));
                gh.setStrokeWidth(2);
                gh.setPosition(leaf.x + side * 46, leaf.y + 70);
                gh.setOpacity(0);
                add(gh);
                ga.add(new FadeIn(gh, d(0.7)));
                ghosts.add(gh);
            }
        }
        TextMob gl = label("children may be missing", 470, 190, 28, Colors.LIGHT_GRAY, false, false);
        ga.add(new FadeIn(gl, d(0.7)));
        playAll(ga);
        mine.addAll(ghosts);
        mine.add(gl);
        String[] ex2 = {"expressions", "boolean flows"};
        for (int i = 0; i < 2; i++) {
            List<MObject> ch = chip(ex2[i], 470 - 190 + i * 380, 265, 340, 60, Colors.GREEN, 28);
            List<Animation> a = new ArrayList<>();
            for (MObject m : ch) a.add(new DropIn(m, 40, 0, d(0.6)));
            playAll(a);
            mine.addAll(ch);
        }
        pause(0.6);
        StrokeTextMob nx = stroke("Next: special trees with a fixed number of children.", 0, 430, 38, Colors.GOLD, false);
        play(new Write(nx, d(3.0)));
        mine.add(nx);
        pause(3.2);
        fadeOutAll(d(1.2), mine);
    }
}
