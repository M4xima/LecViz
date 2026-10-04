package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Easing;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Standalone clip for slide 6 of the arrays deck: matrices and the knight's tour.
 *
 *   - the slide text comes one line at a time in the pen-stroke style, then swipes off
 *   - "typically 2D arrays, sometimes an array of arrays": one contiguous block next to an
 *     int *arr[N] whose rows sit anywhere and can differ in length
 *   - "sorted left-to-right and top-to-bottom, can we apply binary search?": the middle element
 *     only rules out one quadrant, so no half can be thrown away (the next slides' problem)
 *   - the knight's tour: the L-shaped moves, the edge that cannot be wrapped around, and a
 *     complete tour from a corner (always stepping to the square with the fewest onward moves)
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSMatricesScene extends PDSSortClipBase {

    // a knight's tour from the bottom-left corner, as (row, column) with row 0 at the top
    private static final int[][] TOUR = {
        {7,0},{5,1},{3,0},{1,1},{0,3},{1,5},{0,7},{2,6},{0,5},{1,7},{3,6},{5,7},{7,6},{6,4},{7,2},{6,0},
        {4,1},{2,0},{0,1},{1,3},{3,2},{4,0},{6,1},{7,3},{5,2},{7,1},{5,0},{6,2},{7,4},{5,3},{6,5},{7,7},
        {5,6},{3,7},{1,6},{0,4},{1,2},{0,0},{2,1},{0,2},{1,0},{3,1},{2,3},{4,4},{2,5},{0,6},{2,7},{4,6},
        {6,7},{7,5},{6,3},{4,2},{3,4},{2,2},{1,4},{3,3},{4,5},{2,4},{4,3},{5,5},{4,7},{3,5},{5,4},{6,6}};

    private StrokeTextMob head;

    @Override
    public void construct() {
        head = writeHeading("Matrices");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Typically 2D arrays"));
        s.add(ln(1, "Sometimes array of arrays (int *arr[N])"));
        s.add(ln(0, "If a matrix is sorted left-to-right and top-to-"));
        s.add(ln(3, "bottom, can we apply binary search?"));
        s.add(ln(0, "Knight's tour").kw("Knight's tour", Colors.BLUE));
        s.add(ln(1, "Start from a corner."));
        s.add(ln(1, "Visit all 64 squares without"));
        s.add(ln(2, "visiting a square twice."));
        s.add(ln(1, "The only moves allowed are"));
        s.add(ln(2, "2.5 places."));
        s.add(ln(1, "Cannot wrap-around the board."));
        List<List<MObject>> text = writeSlide(s, -365);
        pause(1.2);
        swipeAway(text);
        pause(0.4);

        arrayOfArrays();
        sortedMatrixQuestion();
        knightsTour();
        fadeOutAll(1.5, head);
        pause(0.5);
    }

    private RectMob box(double x, double y, double w, double h, Color c, double fill) {
        RectMob r = new RectMob(w, h).setCornerRadius(6);
        r.setFillColor(Colors.withAlpha(c, fill));
        r.setStrokeColor(Colors.withAlpha(c, 0.8));
        r.setStrokeWidth(2.5);
        r.setPosition(x, y);
        r.setOpacity(0);
        add(r);
        return r;
    }

    // ── "Typically 2D arrays — sometimes an array of arrays" ─────────

    private void arrayOfArrays() {
        List<MObject> mine = new ArrayList<>();
        List<Animation> in = new ArrayList<>();

        TextMob h1 = label("int a[3][4];", -380, -320, 34, Colors.TEAL, false, true);
        h1.setFontFamily("Menlo");
        TextMob h2 = label("int *arr[3];", 380, -320, 34, Colors.GOLD, false, true);
        h2.setFontFamily("Menlo");
        in.add(new FadeIn(h1, d(0.6)));
        in.add(new FadeIn(h2, d(0.6)));
        mine.add(h1);
        mine.add(h2);
        playAll(in);
        in.clear();

        // left: one contiguous block
        double[] vals = {7, 3, 9, 1, 8, 2, 5, 4, 6, 0, 3, 5};
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 4; c++) {
                double x = -380 + (c - 1.5) * 76, y = -190 + 66 * r;
                RectMob b = box(x, y, 70, 58, Colors.TEAL, 0.25);
                TextMob t = label(String.valueOf((int) vals[r * 4 + c]), x, y, 28, Colors.WHITE, false, true);
                in.add(new FadeInAt(b, 0.5 * r + 0.08 * c, d(0.5)));
                in.add(new FadeInAt(t, 0.5 * r + 0.08 * c, d(0.5)));
                mine.add(b);
                mine.add(t);
            }
        playAll(in);
        in.clear();
        StrokeTextMob l1 = stroke("one contiguous block: row after row", -380, 12, 26, Colors.TEAL, false);
        play(new Write(l1, d(2.2)));
        mine.add(l1);
        pause(0.5);

        // right: three pointers, each to a row that lives somewhere else (and can be any length)
        double[] ptrY = {-215, -150, -85};
        int[] lens = {4, 2, 5};
        double[][] rowAt = {{440, -250}, {520, -120}, {400, 10}};
        List<Animation> arrows = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            RectMob p = box(190, ptrY[i], 60, 54, Colors.GOLD, 0.3);
            TextMob pl = label("arr[" + i + "]", 120, ptrY[i], 22, Colors.LIGHT_GRAY, false, false);
            in.add(new FadeInAt(p, 0.3 * i, d(0.5)));
            in.add(new FadeInAt(pl, 0.3 * i, d(0.5)));
            mine.add(p);
            mine.add(pl);
            for (int c = 0; c < lens[i]; c++) {
                double x = rowAt[i][0] + c * 66, y = rowAt[i][1];
                RectMob b = box(x, y, 60, 54, Colors.GOLD, 0.18);
                TextMob t = label(String.valueOf((int) vals[(i * 3 + c) % vals.length]), x, y, 26, Colors.WHITE, false, true);
                in.add(new FadeInAt(b, 0.8 + 0.4 * i + 0.07 * c, d(0.5)));
                in.add(new FadeInAt(t, 0.8 + 0.4 * i + 0.07 * c, d(0.5)));
                mine.add(b);
                mine.add(t);
            }
            ArrowMob a = new ArrowMob(220, ptrY[i], rowAt[i][0] - 38, rowAt[i][1]);
            a.setStrokeColor(Colors.GOLD);
            a.setStrokeWidth(3);
            a.setHeadLength(11);
            a.setOpacity(0);
            add(a);
            arrows.add(new DrawArrow(a, d(0.8)));
            mine.add(a);
        }
        playAll(in);
        playAll(arrows);
        StrokeTextMob l2 = stroke("rows can sit anywhere — even with different lengths", 380, 110, 26, Colors.GOLD, false);
        play(new Write(l2, d(2.6)));
        mine.add(l2);
        pause(2.6);
        fadeOutAll(d(0.9), mine);
        pause(0.3);
    }

    // ── "sorted left-to-right and top-to-bottom: binary search?" ─────

    private void sortedMatrixQuestion() {
        List<MObject> mine = new ArrayList<>();
        StrokeTextMob q = stroke("Sorted left-to-right and top-to-bottom: can we apply binary search?", 0, -330, 32, Colors.WHITE, false);
        play(new Write(q, d(3.4)));
        mine.add(q);
        int[][] m = {{1, 2, 4, 7}, {3, 5, 8, 11}, {6, 9, 12, 14}, {10, 13, 15, 16}};
        RectMob[][] box = new RectMob[4][4];
        List<Animation> in = new ArrayList<>();
        for (int r = 0; r < 4; r++)
            for (int c = 0; c < 4; c++) {
                double x = (c - 1.5) * 110, y = -170 + 90 * r;
                box[r][c] = box(x, y, 100, 80, Colors.GREEN, 0.22);
                TextMob t = label(String.valueOf(m[r][c]), x, y, 30, Colors.WHITE, false, true);
                in.add(new FadeInAt(box[r][c], 0.05 * (r + c), d(0.5)));
                in.add(new FadeInAt(t, 0.05 * (r + c), d(0.5)));
                mine.add(box[r][c]);
                mine.add(t);
            }
        playAll(in);
        pause(0.6);

        // key = 10: compare with the middle element, 5
        TextMob keyLab = label("key = 10", 560, -170, 36, Colors.GOLD, false, true);
        play(new FadeIn(keyLab, d(0.5)));
        mine.add(keyLab);
        RectMob ring = new RectMob(108, 88).setCornerRadius(10);
        ring.setFillColor(Color.TRANSPARENT);
        ring.setStrokeColor(Colors.ORANGE);
        ring.setStrokeWidth(5);
        ring.setPosition((1 - 1.5) * 110, -170 + 90);
        ring.setOpacity(0);
        add(ring);
        mine.add(ring);
        TextMob cmp = label("10 > 5", 560, -110, 36, Colors.ORANGE, false, true);
        play(new FadeIn(ring, d(0.5)), new FadeIn(cmp, d(0.5)));
        mine.add(cmp);
        pause(1.0);

        // the top-left quadrant is ruled out; the other three are not
        List<Animation> shade = new ArrayList<>();
        for (int r = 0; r < 4; r++)
            for (int c = 0; c < 4; c++) {
                boolean out = r <= 1 && c <= 1;
                shade.add(new ColorChange(box[r][c], out ? Colors.withAlpha(Colors.RED, 0.28) : Colors.withAlpha(Colors.ORANGE, 0.3), d(0.7)));
                shade.add(new ColorChange(box[r][c], out ? Colors.RED : Colors.ORANGE, d(0.7), ColorChange.Target.STROKE));
            }
        playAll(shade);
        StrokeTextMob c1 = stroke("Only the top-left quadrant is ruled out — three quadrants remain, not a half.", 0, 240, 28, Colors.ORANGE, false);
        play(new Write(c1, d(3.4)));
        StrokeTextMob c2 = stroke("Binary search has no obvious half to drop. That is the next slides' problem.", 0, 300, 26, Colors.LIGHT_GRAY, false);
        play(new Write(c2, d(3.2)));
        mine.add(c1);
        mine.add(c2);
        pause(2.6);
        fadeOutAll(d(0.9), mine);
        pause(0.3);
    }

    // ── the knight's tour ────────────────────────────────────────────

    private static final double CS = 68, BX = 0, BY = 30;

    private double sx(int c) { return BX + (c - 3.5) * CS; }
    private double sy(int r) { return BY + (r - 3.5) * CS; }

    private void knightsTour() {
        List<MObject> mine = new ArrayList<>();
        List<Animation> in = new ArrayList<>();
        RectMob[][] sq = new RectMob[8][8];
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                RectMob b = new RectMob(CS - 3, CS - 3).setCornerRadius(4);
                b.setFillColor(Colors.withAlpha(Colors.WHITE, (r + c) % 2 == 0 ? 0.14 : 0.04));
                b.setStrokeColor(Colors.withAlpha(Colors.WHITE, 0.2));
                b.setStrokeWidth(1.5);
                b.setPosition(sx(c), sy(r));
                b.setOpacity(0);
                add(b);
                sq[r][c] = b;
                in.add(new FadeInAt(b, 0.012 * (r * 8 + c), d(0.4)));
                mine.add(b);
            }
        StrokeTextMob cap = stroke("Knight's tour", 0, -330, 38, Colors.BLUE, false);
        in.add(new Write(cap, d(1.6)));
        mine.add(cap);
        playAll(in);
        pause(0.4);

        TextMob knight = label("♞", sx(3), sy(4), 60, Colors.GOLD, false, true);
        knight.setFontFamily("Apple Symbols");
        mine.add(knight);
        play(new FadeIn(knight, d(0.6)));

        // 1) the moves: an L — two squares one way, one square across ("2.5 places")
        int[][] mv = {{-2, -1}, {-2, 1}, {-1, 2}, {1, 2}, {2, 1}, {2, -1}, {1, -2}, {-1, -2}};
        List<Animation> ls = new ArrayList<>();
        List<MObject> temp = new ArrayList<>();
        for (int[] m : mv) {
            double x0 = sx(3), y0 = sy(4);
            boolean longVertical = Math.abs(m[0]) == 2;
            double mx = longVertical ? x0 : x0 + m[1] * CS, my = longVertical ? y0 + m[0] * CS : y0;
            double tx = x0 + m[1] * CS, ty = y0 + m[0] * CS;
            LineMob l1 = new LineMob(x0, y0, mx, my, Colors.withAlpha(Colors.GOLD, 0.9), 4);
            LineMob l2 = new LineMob(mx, my, tx, ty, Colors.withAlpha(Colors.GOLD, 0.9), 4);
            add(l1);
            add(l2);
            CircleMob dot = new CircleMob(11);
            dot.setFillColor(Colors.GOLD);
            dot.setStrokeColor(Color.TRANSPARENT);
            dot.setPosition(tx, ty);
            dot.setOpacity(0);
            add(dot);
            ls.add(new DrawLine(l1, d(0.7)));
            ls.add(new FadeInAt(l2, 0.5, d(0.4)));
            ls.add(new FadeInAt(dot, 0.7, d(0.4)));
            temp.add(l1);
            temp.add(l2);
            temp.add(dot);
        }
        playAll(ls);
        StrokeTextMob c1 = stroke("The only moves allowed: an L — two squares one way, one square across (2.5 places).", 0, 380, 26, Colors.GOLD, false);
        play(new Write(c1, d(3.4)));
        temp.add(c1);
        pause(1.8);
        fadeOutAll(d(0.6), temp);

        // 2) a corner: most of the moves would leave the board — no wrap-around
        List<Animation> go = new ArrayList<>();
        go.add(new ArcMove(knight, sx(0), sy(7), 0, d(0.9)));
        playAll(go);
        List<MObject> temp2 = new ArrayList<>();
        List<Animation> off = new ArrayList<>();
        for (int[] m : mv) {
            int r = 7 + m[0], c = 0 + m[1];
            boolean on = r >= 0 && r < 8 && c >= 0 && c < 8;
            double tx = sx(c), ty = sy(r);
            CircleMob dot = new CircleMob(11);
            dot.setFillColor(on ? Colors.GOLD : Colors.withAlpha(Colors.RED, 0.25));
            dot.setStrokeColor(on ? Color.TRANSPARENT : Colors.RED);
            dot.setStrokeWidth(2.5);
            dot.setPosition(tx, ty);
            dot.setOpacity(0);
            add(dot);
            off.add(new FadeIn(dot, d(0.6)));
            temp2.add(dot);
            if (!on) {
                LineMob x1 = new LineMob(tx - 9, ty - 9, tx + 9, ty + 9, Colors.RED, 4);
                LineMob x2 = new LineMob(tx - 9, ty + 9, tx + 9, ty - 9, Colors.RED, 4);
                add(x1);
                add(x2);
                off.add(new DrawLine(x1, d(0.5)));
                off.add(new DrawLine(x2, d(0.5)));
                temp2.add(x1);
                temp2.add(x2);
            }
        }
        playAll(off);
        StrokeTextMob c2 = stroke("From a corner only 2 moves stay on the board: it cannot wrap around.", 0, 480, 26, Colors.RED, false);
        play(new Write(c2, d(3.0)));
        temp2.add(c2);
        pause(1.8);
        fadeOutAll(d(0.6), temp2);

        // 3) the tour: all 64 squares, none twice
        StrokeTextMob c3 = stroke("Start from a corner and visit all 64 squares without visiting one twice.", 0, 380, 26, Colors.GREEN, false);
        play(new Write(c3, d(3.4)));
        mine.add(c3);
        List<MObject> trail = new ArrayList<>();
        TextMob num0 = label("1", sx(0), sy(7), 22, Colors.WHITE, false, true);
        play(new FadeIn(num0, d(0.3)), new ColorChange(sq[7][0], Colors.withAlpha(Colors.GREEN, 0.4), d(0.3)));
        trail.add(num0);
        for (int k = 1; k < 64; k++) {
            double s = k < 8 ? 1.0 : 0.3;
            int pr = TOUR[k - 1][0], pc = TOUR[k - 1][1], r = TOUR[k][0], c = TOUR[k][1];
            LineMob seg = new LineMob(sx(pc), sy(pr), sx(c), sy(r), Colors.withAlpha(Colors.GREEN, 0.8), 3);
            add(seg);
            TextMob num = label(String.valueOf(k + 1), sx(c), sy(r), 22, Colors.WHITE, false, true);
            List<Animation> step = new ArrayList<>();
            step.add(new ArcMove(knight, sx(c), sy(r) - 6, 0, d(0.55 * s)));
            step.add(new DrawLine(seg, d(0.5 * s)));
            step.add(new ColorChange(sq[r][c], Colors.withAlpha(Colors.GREEN, 0.4), d(0.5 * s)));
            step.add(new FadeInAt(num, 0.25 * s, d(0.3 * s)));
            playAll(step);
            trail.add(seg);
            trail.add(num);
            mine.addAll(trail.subList(trail.size() - 2, trail.size()));
            pause(0.04 * s);
        }
        mine.add(num0);
        pause(1.0);
        StrokeTextMob c4 = stroke("One rule finds it: always step to the square with the fewest onward moves.", 0, 440, 26, Colors.ORANGE, false);
        play(new Write(c4, d(3.4)));
        mine.add(c4);
        pause(3.0);
        fadeOutAll(d(1.0), mine);
        pause(0.3);
    }
}
