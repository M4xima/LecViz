package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.core.Scene;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Easing;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

/**
 * Standalone clip covering slides 7-9 of the arrays deck: searching a
 * matrix that is sorted along both rows and columns.
 *
 * Same visual language as the array-search clip: the matrix fades in
 * in translucent green (its base color for the rest of the clip). Each
 * approach follows its slide step by step on a shared layout — the grid
 * slides right and the explanation plays out in a panel on the left:
 *
 *   Approach 1  quadrants Q1-Q4, the two cells (i,0) and (0,j), four cases
 *   Approach 2  the corner points x, y, z and the two quadrant rules
 *   Approach 3  the three-line elimination logic, then a full walk from the
 *               top-right corner with the matching rule called out at
 *               every step, ending on O(M + N)
 *
 * Closes on the three complexities side by side.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSSortedMatrixSearchScene extends Scene {

    private static final int[][] MATRIX = {
        {3, 5, 9, 20, 39},
        {4, 6, 11, 21, 40},
        {7, 10, 12, 23, 45},
        {8, 13, 22, 27, 46},
        {19, 29, 41, 43, 49},
        {24, 30, 44, 50, 52},
        {25, 31, 47, 51, 55},
        {28, 33, 48, 53, 61},
        {32, 42, 54, 56, 66},
        {35, 57, 60, 62, 69},
    };
    private static final int ROWS = 10, COLS = 5;
    private static final int TARGET = 44; // row 5, col 2 on the slide

    private static final double CELL_W = 76, CELL_H = 44;
    private static final double MATRIX_TOP = -170;
    private static final double CAPTION_Y = -400;

    // Same green palette as the array-search clip, for a consistent feel.
    private static final Color BASE_FILL = Colors.withAlpha(Colors.GREEN, 0.22);
    private static final Color BASE_STROKE = Colors.withAlpha(Colors.GREEN, 0.55);
    private static final Color CURRENT_FILL = Colors.withAlpha(Colors.ORANGE, 0.42);
    private static final Color CURRENT_STROKE = Colors.ORANGE;
    private static final Color ELIMINATED_FILL = Colors.withAlpha(Colors.GREEN, 0.06);
    private static final Color ELIMINATED_STROKE = Colors.withAlpha(Colors.GREEN, 0.22);
    private static final Color FOUND_FILL = Colors.withAlpha(Colors.GREEN, 0.62);
    private static final Color FOUND_STROKE = Colors.GREEN;

    // The explanatory sections run at a relaxed pace.
    private static final double PACE = 1.3;
    private double d(double seconds) { return seconds * PACE; }
    private void pause(double seconds) { hold(seconds * PACE); }

    private double colX(int c) { return -((COLS - 1) * CELL_W) / 2.0 + c * CELL_W; }
    private double rowY(int r) { return MATRIX_TOP + r * CELL_H + CELL_H / 2.0; }

    private void fadeOutAll(double dur, MObject... objs) {
        FadeOut[] anims = new FadeOut[objs.length];
        for (int i = 0; i < objs.length; i++) anims[i] = new FadeOut(objs[i], dur);
        play(anims);
        for (MObject o : objs) remove(o);
    }

    private StrokeTextMob caption(String text, double y, double size, Color color) {
        StrokeTextMob t = new StrokeTextMob(text, "Georgia", false, size)
                .setFillColor(color).setStrokeColor(color);
        t.setPosition(0, y);
        add(t);
        return t;
    }

    private StrokeTextMob bigStatement(String text) {
        StrokeTextMob t = new StrokeTextMob(text, "Georgia", true, 32)
                .setFillColor(Colors.WHITE).setStrokeColor(Colors.WHITE);
        t.setPosition(0, 0);
        add(t);
        return t;
    }

    private RectMob[][] box;
    private TextMob[][] val;
    private Color[][] pin; // cells that keep their own fill through quadrant tints

    @Override
    public void construct() {
        StrokeTextMob title = new StrokeTextMob("Search in a Sorted Matrix", "Georgia", true, 50)
                .setFillColor(Colors.WHITE).setStrokeColor(Colors.WHITE);
        title.setPosition(0, -500);
        add(title);
        play(new Write(title, 2.6));
        hold(1.0);

        // ── The matrix simply fades in, in its translucent green (the
        //    base color it keeps for the rest of the clip). ──
        box = new RectMob[ROWS][COLS];
        val = new TextMob[ROWS][COLS];
        pin = new Color[ROWS][COLS];
        List<Animation> settle = new ArrayList<>();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                RectMob b = new RectMob(CELL_W - 6, CELL_H - 6).setCornerRadius(5);
                b.setFillColor(BASE_FILL);
                b.setStrokeColor(BASE_STROKE);
                b.setPosition(colX(c), rowY(r));
                b.setOpacity(0);
                box[r][c] = b;
                add(b);
                settle.add(new FadeIn(b, 0.6));

                TextMob t = new TextMob(String.valueOf(MATRIX[r][c])).setFontSize(18).setFillColor(Colors.WHITE);
                t.setPosition(colX(c), rowY(r));
                t.setOpacity(0);
                val[r][c] = t;
                add(t);
                settle.add(new FadeIn(t, 0.6));
            }
        }
        play(settle.toArray(new Animation[0]));
        hold(0.6);

        // Slide 7: "Focus on 44." — only here; the approaches that follow
        // use their own keys, so 44 goes back to plain green afterwards.
        int tr = 5, tc = 2; // TARGET's position
        box[tr][tc].setFillColor(Colors.withAlpha(Colors.GOLD, 0.4));
        box[tr][tc].setStrokeColor(Colors.GOLD);
        StrokeTextMob focusCap = caption("Focus on " + TARGET + " — where do smaller and larger values live?",
                CAPTION_Y, 22, Colors.GOLD);
        play(new Write(focusCap, 2.0));
        hold(1.6);
        play(new FadeOut(focusCap, 0.6),
                new ColorChange(box[tr][tc], BASE_FILL, 0.6),
                new ColorChange(box[tr][tc], BASE_STROKE, 0.6, ColorChange.Target.STROKE));
        remove(focusCap);
        hold(0.6);

        approachOne();
        approachTwo();
        approachThree();

        // ── Closing: the three complexities, side by side ──
        LaTeXMob complexity = new LaTeXMob(
                "\\mathbf{O(\\min(M,N)^2) \\;\\to\\; O(\\min(M,N)^{1.54}) \\;\\to\\; O(M+N)}")
                .setSize(28).setLatexColor(Colors.WHITE);
        complexity.setPosition(0, 400);
        add(complexity);
        play(new Write(complexity, 2.4));
        hold(1.6);

        StrokeTextMob big = bigStatement("Smarter elimination beats brute-force quadrants");
        big.setPosition(0, 470);
        play(new Write(big, 2.2));
        hold(2.2);

        List<MObject> finale = new ArrayList<>();
        finale.add(title);
        finale.add(complexity);
        finale.add(big);
        for (int rr = 0; rr < ROWS; rr++)
            for (int cc = 0; cc < COLS; cc++) {
                finale.add(box[rr][cc]);
                finale.add(val[rr][cc]);
            }
        fadeOutAll(1.5, finale.toArray(new MObject[0]));
        hold(0.5);
    }

    // ── Shared layout pieces ─────────────────────────────────────────

    // [i, j] — the bottom-right corner of Q1. Rows 0..i | i+1.. and
    // columns 0..j | j+1.. split the grid into the four quadrants.
    private static final int PIVOT_R = 4, PIVOT_C = 2;
    // Q1 top-left, Q2 top-right, Q3 bottom-right, Q4 bottom-left.
    private static final Color[] QUAD_COLOR = {Colors.BLUE, Colors.PURPLE, Colors.TEAL, Colors.GOLD};
    private static final double GRID_SHIFT = 330;       // how far the grid slides right
    private static final double PANEL_LEFT = -820;      // left edge of the text panel
    private static final double PANEL_CENTER = -420;    // center of the text panel
    private static final Color INK = Color.web("#08080D");

    private RectMob hLine, vLine;
    private TextMob[] qLab;
    private final List<MObject> decor = new ArrayList<>(); // rides along when the grid shifts

    private static int quadrantOf(int r, int c) {
        return (r <= PIVOT_R) ? (c <= PIVOT_C ? 0 : 1) : (c <= PIVOT_C ? 3 : 2);
    }

    /** Candidate quadrants tinted in their own colors; every other quadrant dims out. */
    private void tintQuadrants(boolean[] keep) {
        List<Animation> anims = new ArrayList<>();
        double dur = d(0.6);
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++) {
                int q = quadrantOf(r, c);
                Color fill = pin[r][c] != null ? pin[r][c]
                        : keep[q] ? Colors.withAlpha(QUAD_COLOR[q], 0.32) : ELIMINATED_FILL;
                Color stroke = keep[q] ? QUAD_COLOR[q] : ELIMINATED_STROKE;
                anims.add(new ColorChange(box[r][c], fill, dur));
                anims.add(new ColorChange(box[r][c], stroke, dur, ColorChange.Target.STROKE));
            }
        play(anims.toArray(new Animation[0]));
    }

    private void resetQuadrants() {
        List<Animation> anims = new ArrayList<>();
        double dur = d(0.5);
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++) {
                anims.add(new ColorChange(box[r][c], pin[r][c] != null ? pin[r][c] : BASE_FILL, dur));
                anims.add(new ColorChange(box[r][c], BASE_STROKE, dur, ColorChange.Target.STROKE));
            }
        play(anims.toArray(new Animation[0]));
    }

    private void shiftMatrix(double dx, double dur, List<MObject> extras) {
        List<Animation> anims = new ArrayList<>();
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++) {
                anims.add(new MoveTo(box[r][c], box[r][c].getPosition().x() + dx, box[r][c].getPosition().y(), dur)
                        .setEasing(Easing.EASE_IN_OUT));
                anims.add(new MoveTo(val[r][c], val[r][c].getPosition().x() + dx, val[r][c].getPosition().y(), dur)
                        .setEasing(Easing.EASE_IN_OUT));
            }
        for (MObject m : extras)
            anims.add(new MoveTo(m, m.getPosition().x() + dx, m.getPosition().y(), dur)
                    .setEasing(Easing.EASE_IN_OUT));
        play(anims.toArray(new Animation[0]));
    }

    private RectMob ring(int r, int c, Color color, double shiftX) {
        RectMob rm = new RectMob(CELL_W - 2, CELL_H - 2).setCornerRadius(5);
        rm.setFillColor(Color.TRANSPARENT);
        rm.setStrokeColor(color);
        rm.setStrokeWidth(3.5);
        rm.setPosition(colX(c) + shiftX, rowY(r));
        rm.setOpacity(0);
        add(rm);
        return rm;
    }

    /** A translucent highlight band (hidden until animated in). */
    private RectMob band(double cx, double cy, double w, double h, Color color) {
        RectMob b = new RectMob(w, h).setCornerRadius(5);
        b.setFillColor(Colors.withAlpha(color, 0.4));
        b.setStrokeColor(color);
        b.setStrokeWidth(2);
        b.setPosition(cx, cy);
        b.setOpacity(0);
        add(b);
        return b;
    }

    private TextMob label(String text, double x, double y, double size, Color color, boolean left) {
        TextMob t = new TextMob(text).setFontSize(size).setFillColor(color);
        if (left) t.setAlignment(TextAlignment.LEFT);
        t.setPosition(x, y);
        t.setOpacity(0);
        add(t);
        return t;
    }

    private StrokeTextMob captionAt(String text, double x, double y, double size, Color color) {
        StrokeTextMob t = new StrokeTextMob(text, "Georgia", false, size)
                .setFillColor(color).setStrokeColor(color);
        t.setPosition(x, y);
        add(t);
        return t;
    }

    /** Briefly swells the given rings — a "compare against this" cue. */
    private void pulse(RectMob... rings) {
        Animation[] up = new Animation[rings.length];
        Animation[] down = new Animation[rings.length];
        for (int i = 0; i < rings.length; i++) {
            up[i] = new ScaleTo(rings[i], 1.22, d(0.22));
            down[i] = new ScaleTo(rings[i], 1.0, d(0.25));
        }
        play(up);
        play(down);
    }

    /** The grid's dividing lines (between rows i|i+1 and columns j|j+1) and the Q1-Q4 tags. */
    private void buildDividers() {
        double midY = MATRIX_TOP + (PIVOT_R + 1) * CELL_H;
        double midX = colX(PIVOT_C) + CELL_W / 2.0;
        hLine = new RectMob(COLS * CELL_W + 40, 2.5);
        hLine.setFillColor(Colors.withAlpha(Colors.WHITE, 0.75));
        hLine.setStrokeColor(Color.TRANSPARENT);
        hLine.setPosition(0, midY);
        vLine = new RectMob(2.5, ROWS * CELL_H + 40);
        vLine.setFillColor(Colors.withAlpha(Colors.WHITE, 0.75));
        vLine.setStrokeColor(Color.TRANSPARENT);
        vLine.setPosition(midX, MATRIX_TOP + ROWS * CELL_H / 2.0);
        hLine.setOpacity(0);
        vLine.setOpacity(0);
        add(hLine);
        add(vLine);
        decor.add(hLine);
        decor.add(vLine);

        double leftEdge = colX(0) - CELL_W / 2.0, rightEdge = colX(COLS - 1) + CELL_W / 2.0;
        double bottomEdge = MATRIX_TOP + ROWS * CELL_H;
        qLab = new TextMob[]{
            label("Q1", leftEdge - 34, MATRIX_TOP - 16, 26, QUAD_COLOR[0], false),
            label("Q2", rightEdge + 34, MATRIX_TOP - 16, 26, QUAD_COLOR[1], false),
            label("Q3", rightEdge + 34, bottomEdge + 18, 26, QUAD_COLOR[2], false),
            label("Q4", leftEdge - 34, bottomEdge + 18, 26, QUAD_COLOR[3], false),
        };
        for (TextMob q : qLab) { q.setBold(); decor.add(q); }
    }

    private void fadeInQuadrantTags() {
        play(new FadeIn(qLab[0], d(0.4)), new FadeIn(qLab[1], d(0.4)),
                new FadeIn(qLab[2], d(0.4)), new FadeIn(qLab[3], d(0.4)));
    }

    // ── Approach 1, step by step as on slide 8 ───────────────────────

    private void approachOne() {
        StrokeTextMob a1cap = caption("Approach 1: Divide and Conquer", CAPTION_Y, 22, Colors.LIGHT_GRAY);
        play(new Write(a1cap, d(1.8)));
        pause(0.8);

        // 1) Divide the grid: columns 0-2 | 3-4 and rows 0-4 | 5-9.
        buildDividers();
        play(new FadeIn(hLine, d(0.5)), new FadeIn(vLine, d(0.5)));
        tintQuadrants(new boolean[]{true, true, true, true});
        fadeInQuadrantTags();
        pause(1.6);
        resetQuadrants();

        // 2) Highlight the two cells every case is decided by: (i, 0) and (0, j).
        double leftEdge = colX(0) - CELL_W / 2.0;
        RectMob ringRow = ring(PIVOT_R, 0, Colors.PINK, 0);
        RectMob ringCol = ring(0, PIVOT_C, Colors.PINK, 0);
        decor.add(ringRow);
        decor.add(ringCol);
        TextMob lblRow = label("(i, 0)", leftEdge - 46, rowY(PIVOT_R), 20, Colors.PINK, false);
        TextMob lblCol = label("(0, j)", colX(PIVOT_C), MATRIX_TOP - 24, 20, Colors.PINK, false);
        decor.add(lblRow);
        decor.add(lblCol);
        Color pinkFill = Colors.withAlpha(Colors.PINK, 0.5);
        pin[PIVOT_R][0] = pinkFill;
        pin[0][PIVOT_C] = pinkFill;
        play(new FadeIn(ringRow, d(0.5)), new FadeIn(ringCol, d(0.5)), new FadeIn(lblRow, d(0.5)), new FadeIn(lblCol, d(0.5)),
                new ColorChange(box[PIVOT_R][0], pinkFill, d(0.5)),
                new ColorChange(box[0][PIVOT_C], pinkFill, d(0.5)));
        pause(1.6);

        // 3) Slide the grid right; the cases play out on the left.
        shiftMatrix(GRID_SHIFT, d(1.2), new ArrayList<>(decor));
        pause(0.4);

        TextMob header = label("Compare the key with the two pink cells:", PANEL_LEFT, -340, 22, Colors.LIGHT_GRAY, true);
        play(new FadeIn(header, d(0.5)));

        String[] rules = {
            "< (i, 0)  and  < (0, j)   →   Q1",
            "< (i, 0)  and  > (0, j)   →   Q1, Q2",
            "> (i, 0)  and  < (0, j)   →   Q1, Q4",
            "> (i, 0)  and  > (0, j)   →   Q1, Q2, Q3, Q4",
        };
        boolean[][] keep = {
            {true, false, false, false},
            {true, true, false, false},
            {true, false, false, true},
            {true, true, true, true},
        };
        // One sample element per quadrant that can hold a key satisfying the case.
        int[][][] samples = {
            {{0, 1}},                          // 5
            {{2, 2}},                          // 12
            {},                                // nothing fits: 19 > 9
            {{3, 2}, {3, 3}, {6, 3}, {6, 1}},  // 22, 27, 51, 31
        };
        String[] sampleNote = {
            "e.g. key = 5     (5 < 19 and 5 < 9)",
            "e.g. key = 12     (12 < 19 and 12 > 9) — Q2 can't be ruled out yet",
            "No key fits here: this case needs (i,0) < (0,j), but 19 > 9",
            "e.g. one from every quadrant: 22, 27, 51, 31 — all > 19 and > 9",
        };

        TextMob[] ruleTxt = new TextMob[4];
        for (int k = 0; k < 4; k++) {
            ruleTxt[k] = label(rules[k], PANEL_LEFT, -270 + 65 * k, 26, Colors.WHITE, true);
            ruleTxt[k].setBold();
            List<Animation> in = new ArrayList<>();
            in.add(new FadeIn(ruleTxt[k], d(0.5)));
            if (k > 0) in.add(new ColorChange(ruleTxt[k - 1], Colors.GRAY, d(0.4)));
            play(in.toArray(new Animation[0]));

            tintQuadrants(keep[k]);

            List<MObject> temp = new ArrayList<>();
            List<Animation> showTemp = new ArrayList<>();
            for (int[] s : samples[k]) {
                RectMob rg = ring(s[0], s[1], Colors.WHITE, GRID_SHIFT);
                temp.add(rg);
                showTemp.add(new FadeIn(rg, d(0.4)));
            }
            TextMob note = label(sampleNote[k], PANEL_LEFT, -5, 21, samples[k].length == 0 ? Colors.ORANGE : Colors.GOLD, true);
            temp.add(note);
            showTemp.add(new FadeIn(note, d(0.5)));
            play(showTemp.toArray(new Animation[0]));
            pause(2.6);

            fadeOutAll(d(0.4), temp.toArray(new MObject[0]));
            resetQuadrants();
            pause(0.3);
        }
        pause(0.3);

        // 4) The recurrence and the sentences from the slide.
        LaTeXMob formula = new LaTeXMob("\\mathbf{T(M,N) = 4T(M/2,N/2)+c = O(\\min(M,N)^2)}")
                .setSize(28).setLatexColor(Colors.WHITE);
        formula.setPosition(PANEL_CENTER, 105);
        add(formula);
        play(new Write(formula, d(2.0)));
        pause(1.0);

        StrokeTextMob s1 = captionAt("This complexity is same as that for the linear search.", PANEL_CENTER, 178, 22, Colors.ORANGE);
        play(new Write(s1, d(2.2)));
        pause(1.2);
        StrokeTextMob s2 = captionAt("To improve complexity, we need to reduce at least one quadrant.", PANEL_CENTER, 238, 22, Colors.LIGHT_GRAY);
        play(new Write(s2, d(2.4)));
        pause(1.4);

        // The note: Q1 is always below [i, j]... but values below [i, j] aren't always in Q1.
        StrokeTextMob s3a = captionAt("Note: A number in Q1 is always smaller than [i,j].", PANEL_CENTER, 302, 22, Colors.LIGHT_GRAY);
        play(new Write(s3a, d(2.2)));
        RectMob pivotRing = ring(PIVOT_R, PIVOT_C, Colors.YELLOW, GRID_SHIFT);
        TextMob pivotNote = label("[i, j] = " + MATRIX[PIVOT_R][PIVOT_C] + "  (bottom-right corner of Q1)",
                PANEL_LEFT, -5, 21, Colors.YELLOW, true);
        tintQuadrants(new boolean[]{true, false, false, false});
        play(new FadeIn(pivotRing, d(0.4)), new FadeIn(pivotNote, d(0.5)));
        pause(1.8);

        StrokeTextMob s3b = captionAt("But a number smaller than [i,j] need not be in Q1.", PANEL_CENTER, 346, 22, Colors.LIGHT_GRAY);
        play(new Write(s3b, d(2.2)));
        RectMob outA = ring(0, 3, Colors.WHITE, GRID_SHIFT);   // 20 — in Q2
        RectMob outB = ring(5, 0, Colors.WHITE, GRID_SHIFT);   // 24 — in Q4
        TextMob outNote = label("20 and 24 are both < 41 — yet neither is in Q1", PANEL_LEFT, 30, 21, Colors.GOLD, true);
        play(new FadeIn(outA, d(0.4)), new FadeIn(outB, d(0.4)), new FadeIn(outNote, d(0.5)));
        pause(2.6);

        // Clear Approach 1: the panel, the markers, then slide the grid home.
        List<MObject> panel = new ArrayList<>();
        panel.add(a1cap);
        panel.add(header);
        for (TextMob t : ruleTxt) panel.add(t);
        panel.add(formula);
        panel.add(s1);
        panel.add(s2);
        panel.add(s3a);
        panel.add(s3b);
        panel.add(pivotRing);
        panel.add(pivotNote);
        panel.add(outA);
        panel.add(outB);
        panel.add(outNote);
        panel.addAll(decor);
        fadeOutAll(d(0.7), panel.toArray(new MObject[0]));
        decor.clear();
        pin[PIVOT_R][0] = null;
        pin[0][PIVOT_C] = null;
        resetQuadrants();
        shiftMatrix(-GRID_SHIFT, d(1.1), new ArrayList<>());
        pause(0.3);
    }

    // ── Approach 2: the corner points x, y, z (slide 9) ──────────────

    private void approachTwo() {
        StrokeTextMob cap = caption("Approach 2: Divide and Conquer", CAPTION_Y, 22, Colors.LIGHT_GRAY);
        play(new Write(cap, d(1.8)));
        pause(0.8);

        buildDividers();
        play(new FadeIn(hLine, d(0.5)), new FadeIn(vLine, d(0.5)));
        fadeInQuadrantTags();
        pause(0.8);

        shiftMatrix(GRID_SHIFT, d(1.2), new ArrayList<>(decor));
        pause(0.4);

        TextMob header = label("Use the corner points of Q1, Q2, Q3, Q4 to decide the quadrant:",
                PANEL_LEFT, -345, 22, Colors.LIGHT_GRAY, true);
        play(new FadeIn(header, d(0.5)));
        pause(0.4);

        // The three corner points, one by one: x = [i, j], y = [i, N-1], z = [M-1, j].
        int[][] pts = {{PIVOT_R, PIVOT_C}, {PIVOT_R, COLS - 1}, {ROWS - 1, PIVOT_C}};
        String[] names = {"x", "y", "z"};
        String[] defs = {"[i, j]", "[i, N-1]", "[M-1, j]"};
        Color pointFill = Colors.withAlpha(Colors.YELLOW, 0.45);
        RectMob[] pointRing = new RectMob[3];
        List<MObject> panel = new ArrayList<>();
        panel.add(cap);
        panel.add(header);
        for (int k = 0; k < 3; k++) {
            int pr = pts[k][0], pc = pts[k][1];
            pointRing[k] = ring(pr, pc, Colors.YELLOW, GRID_SHIFT);
            pointRing[k].setScale(1.7);

            double bx = colX(pc) + GRID_SHIFT + 31, by = rowY(pr) - 16;
            CircleMob badge = new CircleMob(11);
            badge.setFillColor(Colors.YELLOW);
            badge.setStrokeColor(Color.TRANSPARENT);
            badge.setPosition(bx, by);
            badge.setOpacity(0);
            add(badge);
            TextMob letter = label(names[k], bx, by, 15, INK, false);
            letter.setBold();

            TextMob legend = label(names[k] + "  =  " + defs[k] + "  =  " + MATRIX[pr][pc],
                    PANEL_LEFT, -285 + 40 * k, 24, Colors.YELLOW, true);
            legend.setBold();

            pin[pr][pc] = pointFill;
            decor.add(pointRing[k]);
            decor.add(badge);
            decor.add(letter);
            panel.add(legend);

            Animation drop = new ScaleTo(pointRing[k], 1.0, d(0.55)).setEasing(Easing.EASE_OUT);
            play(new FadeIn(pointRing[k], d(0.55)), drop,
                    new FadeIn(badge, d(0.55)), new FadeIn(letter, d(0.55)),
                    new FadeIn(legend, d(0.55)),
                    new ColorChange(box[pr][pc], pointFill, d(0.55)));
            pause(0.7);
        }
        pause(0.8);

        // The two rules, one by one, each with sample keys from the grid.
        TextMob rule1 = label("> y   and   > z   →   Q3", PANEL_LEFT, -135, 26, Colors.WHITE, true);
        rule1.setBold();
        TextMob rule2 = label("Else   →   Q1, Q2, Q4", PANEL_LEFT, -70, 26, Colors.WHITE, true);
        rule2.setBold();
        panel.add(rule1);
        panel.add(rule2);

        // Rule 1: a key bigger than both y and z can only be in Q3.
        play(new FadeIn(rule1, d(0.6)));
        tintQuadrants(new boolean[]{false, false, true, false});
        RectMob s66 = ring(8, 4, Colors.WHITE, GRID_SHIFT);               // 66, in Q3
        TextMob n66 = label("e.g. key = 66 in Q3:   66 > y (" + MATRIX[PIVOT_R][COLS - 1] + ")  and  66 > z ("
                + MATRIX[ROWS - 1][PIVOT_C] + ")", PANEL_LEFT, -5, 21, Colors.GOLD, true);
        play(new FadeIn(s66, d(0.4)), new FadeIn(n66, d(0.5)));
        pulse(pointRing[1], pointRing[2]);
        pause(2.2);
        fadeOutAll(d(0.4), s66, n66);
        pause(0.3);

        // Rule 2: anything else could still be in Q1, Q2 or Q4 — one key from each.
        play(new FadeIn(rule2, d(0.6)), new ColorChange(rule1, Colors.GRAY, d(0.4)));
        tintQuadrants(new boolean[]{true, true, false, true});
        int[][] elseKeys = {{2, 2}, {1, 4}, {7, 0}};                      // 12 in Q1, 40 in Q2, 28 in Q4
        String[] elseNotes = {
            "e.g. key = 12 in Q1:   12 < y (49), so it is not > both",
            "e.g. key = 40 in Q2:   40 < y (49), so it is not > both",
            "e.g. key = 28 in Q4:   28 < y (49)  and  28 < z (60)",
        };
        for (int k = 0; k < 3; k++) {
            RectMob rg = ring(elseKeys[k][0], elseKeys[k][1], Colors.WHITE, GRID_SHIFT);
            TextMob note = label(elseNotes[k], PANEL_LEFT, -5, 21, Colors.GOLD, true);
            play(new FadeIn(rg, d(0.4)), new FadeIn(note, d(0.5)));
            pulse(pointRing[1], pointRing[2]);
            pause(1.7);
            fadeOutAll(d(0.4), rg, note);
            pause(0.2);
        }
        resetQuadrants();
        pause(0.3);

        // The recurrence from the slide.
        LaTeXMob formula = new LaTeXMob("\\mathbf{T(M,N) = 3T(M/2,N/2)+c = O(\\min(M,N))^{1.54}}")
                .setSize(28).setLatexColor(Colors.WHITE);
        formula.setPosition(PANEL_CENTER, 105);
        add(formula);
        play(new Write(formula, d(2.2)));
        pause(1.0);
        StrokeTextMob s1 = captionAt("At most three quadrants are searched: four sub-problems become three.",
                PANEL_CENTER, 178, 22, Colors.ORANGE);
        play(new Write(s1, d(2.4)));
        pause(2.0);

        panel.add(formula);
        panel.add(s1);
        panel.addAll(decor);
        fadeOutAll(d(0.7), panel.toArray(new MObject[0]));
        decor.clear();
        for (int[] p : pts) pin[p[0]][p[1]] = null;
        resetQuadrants();
        shiftMatrix(-GRID_SHIFT, d(1.1), new ArrayList<>());
        pause(0.3);
    }

    // ── Approach 3: elimination from the top-right corner (slide 9) ──

    /** Grows a band from one edge of a rectangle across it: a wipe that shows what gets eliminated. */
    private static final class Wipe extends Animation {
        private final RectMob band;
        private final double left, top, right, bottom;
        private final boolean leftward; // true: grows from the right edge to the left; false: from the top edge down

        Wipe(RectMob band, double left, double top, double right, double bottom, boolean leftward, double dur) {
            super(band, dur, Easing.EASE_OUT);
            this.band = band;
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.leftward = leftward;
        }

        @Override
        public void begin() {
            band.setOpacity(1);
            interpolate(0);
        }

        @Override
        public void interpolate(double t) {
            if (leftward) {
                double w = Math.max(0.01, (right - left) * t);
                band.setSize(w, bottom - top);
                band.setPosition(right - w / 2, (top + bottom) / 2);
            } else {
                double h = Math.max(0.01, (bottom - top) * t);
                band.setSize(right - left, h);
                band.setPosition((left + right) / 2, top + h / 2);
            }
        }
    }

    private void recolor(List<Animation> into, int r, int c, Color fill, Color stroke, double dur) {
        into.add(new ColorChange(box[r][c], fill, dur));
        into.add(new ColorChange(box[r][c], stroke, dur, ColorChange.Target.STROKE));
    }

    private ArrowMob trailArrow(int pr, int pc, int r, int c) {
        double x1 = colX(pc) + GRID_SHIFT, y1 = rowY(pr), x2 = colX(c) + GRID_SHIFT, y2 = rowY(r);
        double len = Math.hypot(x2 - x1, y2 - y1), ux = (x2 - x1) / len, uy = (y2 - y1) / len, shrink = 12;
        ArrowMob a = new ArrowMob(x1 + ux * shrink, y1 + uy * shrink, x2 - ux * shrink, y2 - uy * shrink);
        a.setHeadLength(9);
        a.setStrokeColor(Colors.ORANGE);
        a.setStrokeWidth(2.5);
        return a;
    }

    private void approachThree() {
        StrokeTextMob cap = caption("Approach 3: Elimination", CAPTION_Y, 22, Colors.LIGHT_GRAY);
        play(new Write(cap, d(1.6)));
        pause(0.8);

        shiftMatrix(GRID_SHIFT, d(1.2), new ArrayList<>());
        pause(0.3);

        // Consider e: [0, N-1] — the top-right corner.
        int eR = 0, eC = COLS - 1;
        TextMob header = label("Consider e : [0, N-1]   (the top-right corner)", PANEL_LEFT, -345, 22, Colors.LIGHT_GRAY, true);
        RectMob eRing = ring(eR, eC, Colors.ORANGE, GRID_SHIFT);
        eRing.setScale(1.7);
        TextMob eLab = label("e", colX(eC) + GRID_SHIFT, MATRIX_TOP - 22, 26, Colors.ORANGE, false);
        eLab.setBold();
        Animation eDrop = new ScaleTo(eRing, 1.0, d(0.55)).setEasing(Easing.EASE_OUT);
        play(new FadeIn(header, d(0.5)), new FadeIn(eRing, d(0.55)), eDrop, new FadeIn(eLab, d(0.55)),
                new ColorChange(box[eR][eC], CURRENT_FILL, d(0.55)),
                new ColorChange(box[eR][eC], CURRENT_STROKE, d(0.55), ColorChange.Target.STROKE));
        pause(1.0);

        // The logic, one line at a time — each previewed on the grid.
        String[] logic = {
            "If  key == e   →   found the element",
            "If  key < e   →   eliminate that column",
            "If  key > e   →   eliminate that row",
        };
        Color[] ruleColor = {Colors.GREEN, Colors.BLUE, Colors.PINK};
        double[] ruleY = {-280, -215, -150};
        TextMob[] logicTxt = new TextMob[3];
        double gridMidY = MATRIX_TOP + ROWS * CELL_H / 2.0;
        for (int k = 0; k < 3; k++) {
            logicTxt[k] = label(logic[k], PANEL_LEFT, ruleY[k], 26, ruleColor[k], true);
            logicTxt[k].setBold();
            play(new FadeIn(logicTxt[k], d(0.6)));

            if (k == 0) {
                play(new ColorChange(eRing, Colors.GREEN, d(0.3), ColorChange.Target.STROKE));
                pause(0.7);
                play(new ColorChange(eRing, Colors.ORANGE, d(0.3), ColorChange.Target.STROKE));
            } else {
                RectMob demo = (k == 1)
                        ? band(colX(eC) + GRID_SHIFT, gridMidY, CELL_W - 6, ROWS * CELL_H - 6, ruleColor[k])
                        : band(GRID_SHIFT, rowY(0), COLS * CELL_W - 6, CELL_H - 6, ruleColor[k]);
                play(new FadeIn(demo, d(0.4)));
                pause(0.8);
                play(new FadeOut(demo, d(0.4)));
                remove(demo);
            }
            pause(0.4);
        }
        pause(0.4);

        StrokeTextMob keyCap = captionAt("Let's search for key = " + TARGET, PANEL_CENTER, -70, 26, Colors.GOLD);
        play(new Write(keyCap, d(1.6)));
        pause(0.9);

        // The walk: compare, name the matching rule, eliminate, move on.
        RectMob selector = new RectMob(7, 38).setCornerRadius(3);
        selector.setPosition(PANEL_LEFT - 20, ruleY[0]);
        selector.setOpacity(0);
        add(selector);
        boolean selectorShown = false;

        boolean[][] elim = new boolean[ROWS][COLS];
        List<MObject> trail = new ArrayList<>();
        TextMob stepA = null, stepB = null;
        int step = 0, r = eR, c = eC, prevR = -1, prevC = -1;

        while (r < ROWS && c >= 0) {
            step++;
            double sp = step <= 3 ? 1.0 : 0.72;   // the first steps teach; later ones just run
            int v = MATRIX[r][c];
            int rule = (v == TARGET) ? 0 : (TARGET < v ? 1 : 2);
            Color rc = ruleColor[rule];

            // 1) The e marker walks onto this cell, leaving a trail arrow behind.
            List<Animation> go = new ArrayList<>();
            if (step > 1) {
                go.add(new MoveTo(eRing, colX(c) + GRID_SHIFT, rowY(r), d(0.65 * sp)).setEasing(Easing.EASE_IN_OUT));
                go.add(new ColorChange(eRing, Colors.ORANGE, d(0.65 * sp), ColorChange.Target.STROKE));
                ArrowMob arrow = trailArrow(prevR, prevC, r, c);
                trail.add(arrow);
                go.add(new DrawArrow(arrow, d(0.65 * sp)));
                go.add(new FadeOut(stepA, d(0.4)));
                go.add(new FadeOut(stepB, d(0.4)));
                if (step == 2) go.add(new FadeOut(eLab, d(0.4)));
            }
            recolor(go, r, c, CURRENT_FILL, CURRENT_STROKE, d(0.4 * sp));
            play(go.toArray(new Animation[0]));
            if (step > 1) remove(stepA, stepB);
            if (step == 2) remove(eLab);

            // 2) "Step k: key = 44 vs e = 39" — every rule goes quiet while we look.
            stepA = label(String.format("Step %d:   key = %d   vs   e = %d", step, TARGET, v),
                    PANEL_LEFT, 5, 26, Colors.WHITE, true);
            stepA.setBold();
            List<Animation> look = new ArrayList<>();
            look.add(new FadeIn(stepA, d(0.5 * sp)));
            for (TextMob t : logicTxt) look.add(new ColorChange(t, Colors.GRAY, d(0.4 * sp)));
            play(look.toArray(new Animation[0]));
            pause(0.55 * sp);

            // 3) Name the case: the matching rule lights up, the selector slides to it.
            String decision = switch (rule) {
                case 0 -> TARGET + " == " + v + "   →   key == e   →   found the element!";
                case 1 -> TARGET + " < " + v + "   →   key < e   →   eliminate column " + c;
                default -> TARGET + " > " + v + "   →   key > e   →   eliminate row " + r;
            };
            stepB = label(decision, PANEL_LEFT, 60, 26, rc, true);
            stepB.setBold();
            List<Animation> name = new ArrayList<>();
            name.add(new FadeIn(stepB, d(0.5 * sp)));
            name.add(new ColorChange(logicTxt[rule], rc, d(0.4 * sp)));
            name.add(new ColorChange(eRing, rc, d(0.4 * sp), ColorChange.Target.STROKE));
            if (!selectorShown) {
                selector.setPosition(PANEL_LEFT - 20, ruleY[rule]);
                selector.setFillColor(rc);
                name.add(new FadeIn(selector, d(0.4 * sp)));
                selectorShown = true;
            } else {
                name.add(new MoveTo(selector, PANEL_LEFT - 20, ruleY[rule], d(0.45 * sp)).setEasing(Easing.EASE_IN_OUT));
                name.add(new ColorChange(selector, rc, d(0.45 * sp)));
            }
            play(name.toArray(new Animation[0]));
            pause(0.6 * sp);

            if (rule == 0) {
                // Found: lock in green, swell the number, send out two ripples.
                List<Animation> win = new ArrayList<>();
                recolor(win, r, c, FOUND_FILL, FOUND_STROKE, d(0.5));
                play(win.toArray(new Animation[0]));
                play(new ScaleTo(val[r][c], 1.6, 0.25), new ScaleTo(eRing, 1.15, 0.25));
                play(new ScaleTo(val[r][c], 1.0, 0.3), new ScaleTo(eRing, 1.0, 0.3));
                for (int b = 0; b < 2; b++) {
                    RectMob ripple = ring(r, c, Colors.GREEN, GRID_SHIFT);
                    ripple.setOpacity(1);
                    play(new ScaleTo(ripple, 2.6, 0.9).setEasing(Easing.EASE_OUT), new FadeOut(ripple, 0.9));
                    remove(ripple);
                }
                pause(1.0);
                break;
            }

            // 4) Eliminate: a band sweeps across what dies, then those cells dim.
            double bw = CELL_W - 6, bh = CELL_H - 6;
            RectMob sweep;
            Animation wipe;
            if (rule == 1) {
                double x = colX(c) + GRID_SHIFT;
                sweep = band(x, rowY(r), bw, bh, rc);
                wipe = new Wipe(sweep, x - bw / 2, rowY(r) - bh / 2, x + bw / 2, MATRIX_TOP + ROWS * CELL_H - 3,
                        false, d(0.75 * sp));
            } else {
                double y = rowY(r);
                sweep = band(GRID_SHIFT, y, bw, bh, rc);
                wipe = new Wipe(sweep, colX(0) + GRID_SHIFT - bw / 2, y - bh / 2, colX(c) + GRID_SHIFT + bw / 2, y + bh / 2,
                        true, d(0.75 * sp));
            }
            play(wipe);

            List<Animation> dim = new ArrayList<>();
            if (rule == 1) {
                for (int rr = 0; rr < ROWS; rr++)
                    if (!elim[rr][c]) { elim[rr][c] = true; recolor(dim, rr, c, ELIMINATED_FILL, ELIMINATED_STROKE, d(0.5 * sp)); }
            } else {
                for (int cc = 0; cc < COLS; cc++)
                    if (!elim[r][cc]) { elim[r][cc] = true; recolor(dim, r, cc, ELIMINATED_FILL, ELIMINATED_STROKE, d(0.5 * sp)); }
            }
            dim.add(new FadeOut(sweep, d(0.5 * sp)));
            play(dim.toArray(new Animation[0]));
            remove(sweep);
            pause(0.25 * sp);

            prevR = r;
            prevC = c;
            if (rule == 1) c--; else r++;
        }

        // The cost: each step removes a row or a column.
        play(new FadeOut(stepA, d(0.5)), new FadeOut(stepB, d(0.5)), new FadeOut(selector, d(0.5)));
        remove(stepA, stepB, selector);
        StrokeTextMob s1 = captionAt("Found " + TARGET + " in " + step + " steps — every step removes a row or a column,",
                PANEL_CENTER, 30, 22, Colors.ORANGE);
        play(new Write(s1, d(2.4)));
        pause(0.6);
        StrokeTextMob s2 = captionAt("so the walk takes at most M + N steps.", PANEL_CENTER, 90, 22, Colors.LIGHT_GRAY);
        play(new Write(s2, d(1.8)));
        pause(0.5);
        LaTeXMob bigO = new LaTeXMob("\\mathbf{O(M + N)}").setSize(44).setLatexColor(Colors.WHITE);
        bigO.setPosition(PANEL_CENTER, 190);
        add(bigO);
        play(new Write(bigO, d(1.6)));
        pause(2.2);

        List<MObject> panel = new ArrayList<>();
        panel.add(cap);
        panel.add(header);
        panel.add(keyCap);
        panel.add(s1);
        panel.add(s2);
        panel.add(bigO);
        panel.add(eRing);
        for (TextMob t : logicTxt) panel.add(t);
        panel.addAll(trail);
        fadeOutAll(d(0.7), panel.toArray(new MObject[0]));
        resetQuadrants();
        shiftMatrix(-GRID_SHIFT, d(1.1), new ArrayList<>());
        pause(0.3);
    }
}
