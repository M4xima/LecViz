package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.core.Scene;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Easing;
import javafx.scene.paint.Color;

/**
 * Standalone clip: how a 2D array A[4][4] actually sits in memory.
 * Opens with the 3Blue1Brown handwritten-stroke title reveal (StrokeTextMob
 * — used for sentence-level titles/captions only; short labels and numbers
 * stay plain TextMob), builds a translucent 4x4 grid whose values flicker
 * through random decoys before settling, color-codes each row, shows the
 * empty destination slots sliding into place together, then unrolls the
 * rows — in their own colors — into a single row-major memory strip with
 * indices.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSArrayExpressionsScene extends Scene {

    private static final int SIZE = 4;
    private static final Color[] ROW_COLOR = { Colors.BLUE, Colors.GREEN, Colors.PURPLE, Colors.GOLD };
    private static final String[] ROW_NAME = { "Row 0", "Row 1", "Row 2", "Row 3" };
    private static final int[] RANDOM_VALUES = { 42, 17, 88, 3, 56, 91, 24, 67, 9, 73, 38, 15, 82, 6, 50, 29 };

    // A single reusable caption line, far enough from the title and the
    // grid that nothing here ever overlaps it.
    private static final double CAPTION_Y = 180;

    private void fadeOutAll(double dur, MObject... objs) {
        FadeOut[] anims = new FadeOut[objs.length];
        for (int i = 0; i < objs.length; i++) anims[i] = new FadeOut(objs[i], dur);
        play(anims);
        for (MObject o : objs) remove(o);
    }

    private StrokeTextMob caption(String text, double size, Color color) {
        StrokeTextMob t = new StrokeTextMob(text, "Georgia", false, size)
                .setFillColor(color).setStrokeColor(color);
        t.setPosition(0, CAPTION_Y);
        add(t);
        return t;
    }

    @Override
    public void construct() {
        // ── Title: the real handwritten-stroke reveal, not a wipe ──
        StrokeTextMob title = new StrokeTextMob("Let's take the array A[4][4]", "Georgia", true, 54)
                .setFillColor(Colors.WHITE).setStrokeColor(Colors.WHITE);
        title.setPosition(0, -380);
        add(title);
        play(new Write(title, 2.8));
        hold(1.4);

        // ── A translucent 4x4 grid of arbitrary (not sequential) values ──
        double cell = 100;
        double gx0 = -(SIZE - 1) * cell / 2.0;
        double gy0 = -80 - (SIZE - 1) * cell / 2.0;

        RectMob[][] box = new RectMob[SIZE][SIZE];
        TextMob[][] val = new TextMob[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                double x = gx0 + c * cell;
                double y = gy0 + r * cell;
                RectMob b = new RectMob(cell - 10, cell - 10).setCornerRadius(8);
                b.setFillColor(Colors.withAlpha(Colors.LIGHT_GRAY, 0.08));
                b.setStrokeColor(Colors.withAlpha(Colors.LIGHT_GRAY, 0.4));
                b.setPosition(x, y);
                b.setOpacity(0);
                box[r][c] = b;
                add(b);

                TextMob t = new TextMob("").setFontSize(26).setFillColor(Colors.WHITE);
                t.setPosition(x, y);
                val[r][c] = t;
                add(t);
            }
        }

        // Boxes first, all together
        Animation[] boxFade = new Animation[SIZE * SIZE];
        int bi = 0;
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                boxFade[bi++] = new FadeIn(box[r][c], 0.5);
        play(boxFade);
        hold(0.5);

        // Then the values: flicker through random decoys, all cells at
        // once, before every cell locks onto its real value together —
        // the classic "randomizing" reveal.
        java.util.Random rng = new java.util.Random(7);
        int flickerSteps = 10;
        for (int s = 0; s < flickerSteps; s++) {
            for (int r = 0; r < SIZE; r++)
                for (int c = 0; c < SIZE; c++)
                    val[r][c].setText(String.valueOf(1 + rng.nextInt(99)));
            hold(0.06);
        }
        int vi = 0;
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                val[r][c].setText(String.valueOf(RANDOM_VALUES[vi++]));
        hold(0.8);

        StrokeTextMob gridCaption = caption("A 4×4 grid — but memory is only ever 1D", 22, Colors.LIGHT_GRAY);
        play(new Write(gridCaption, 1.8));
        hold(1.3);
        play(new FadeOut(gridCaption, 0.6));
        remove(gridCaption);

        // ── Color each row so the eye can track it through the unroll ──
        for (int r = 0; r < SIZE; r++) {
            TextMob rowLbl = new TextMob(ROW_NAME[r]).setFontSize(24).setBold().setFillColor(ROW_COLOR[r]);
            rowLbl.setPosition(0, CAPTION_Y);
            add(rowLbl);
            play(new FadeIn(rowLbl, 0.3));

            ColorChange[] fillCC = new ColorChange[SIZE];
            for (int c = 0; c < SIZE; c++) {
                fillCC[c] = new ColorChange(box[r][c], Colors.withAlpha(ROW_COLOR[r], 0.30), 0.6);
            }
            play(fillCC);
            for (int c = 0; c < SIZE; c++) box[r][c].setStrokeColor(Colors.withAlpha(ROW_COLOR[r], 0.8));
            hold(0.6);
            play(new FadeOut(rowLbl, 0.3));
            remove(rowLbl);
        }
        hold(0.4);

        // ── "Row-major storage" ──
        StrokeTextMob unfoldCaption = caption("Row-major storage", 26, Colors.LIGHT_GRAY);
        play(new Write(unfoldCaption, 1.4));
        hold(0.8);

        // ── The 16 destination slots arrive first: outline only, fading
        //    in as they slide up from below into the exact spot each
        //    colored cell is about to land on. ──
        double scw = cell;
        double stripY = 320;
        int N = SIZE * SIZE;
        double stripX0 = -((N - 1) * scw) / 2.0;

        RectMob[] slot = new RectMob[N];
        for (int i = 0; i < N; i++) {
            double targetX = stripX0 + i * scw;
            RectMob s = new RectMob(cell - 10, cell - 10).setCornerRadius(8);
            s.setFillColor(Color.TRANSPARENT);
            s.setStrokeColor(Colors.withAlpha(Colors.LIGHT_GRAY, 0.55));
            s.setPosition(targetX, stripY + 90);
            s.setOpacity(0);
            slot[i] = s;
            add(s);
        }
        Animation[] slotIn = new Animation[N * 2];
        int sli = 0;
        for (int i = 0; i < N; i++) {
            slotIn[sli++] = new FadeIn(slot[i], 0.4);
            slotIn[sli++] = new MoveTo(slot[i], slot[i].getPosition().x(), stripY, 0.5).setEasing(Easing.EASE_OUT);
        }
        play(slotIn);
        hold(0.7);

        // ── Cascade the colored rows down into those slots, row by row ──
        for (int r = 0; r < SIZE; r++) {
            Animation[] lift = new Animation[SIZE * 2];
            int k = 0;
            for (int c = 0; c < SIZE; c++) {
                lift[k++] = new MoveTo(box[r][c], box[r][c].getPosition().x(), box[r][c].getPosition().y() - 18, 0.18)
                        .setEasing(Easing.EASE_OUT);
                lift[k++] = new MoveTo(val[r][c], val[r][c].getPosition().x(), val[r][c].getPosition().y() - 18, 0.18)
                        .setEasing(Easing.EASE_OUT);
            }
            play(lift);

            Animation[] slide = new Animation[SIZE * 2];
            k = 0;
            for (int c = 0; c < SIZE; c++) {
                int slotIdx = r * SIZE + c;
                double targetX = stripX0 + slotIdx * scw;
                slide[k++] = new MoveTo(box[r][c], targetX, stripY, 1.0).setEasing(Easing.EASE_IN_OUT);
                slide[k++] = new MoveTo(val[r][c], targetX, stripY, 1.0).setEasing(Easing.EASE_IN_OUT);
            }
            play(slide);

            // The now-covered placeholder outlines disappear as each piece clicks in.
            FadeOut[] slotFade = new FadeOut[SIZE];
            for (int c = 0; c < SIZE; c++) slotFade[c] = new FadeOut(slot[r * SIZE + c], 0.2);
            play(slotFade);
            for (int c = 0; c < SIZE; c++) remove(slot[r * SIZE + c]);

            hold(0.35);
        }
        hold(0.6);

        play(new FadeOut(unfoldCaption, 0.6));
        remove(unfoldCaption);

        // ── Index labels underneath, each in its own small outline block ──
        RectMob[] idxBox = new RectMob[N];
        TextMob[] idxText = new TextMob[N];
        for (int i = 0; i < N; i++) {
            double x = stripX0 + i * scw;
            double y = stripY + 68;

            RectMob ib = new RectMob(38, 32).setCornerRadius(5);
            ib.setFillColor(Color.TRANSPARENT);
            ib.setStrokeColor(Colors.withAlpha(Colors.GRAY, 0.6));
            ib.setPosition(x, y);
            ib.setOpacity(0);
            idxBox[i] = ib;
            add(ib);

            TextMob it = new TextMob(String.valueOf(i)).setFontSize(16).setFillColor(Colors.GRAY);
            it.setPosition(x, y);
            it.setOpacity(0);
            idxText[i] = it;
            add(it);

            play(new FadeIn(ib, 0.1), new FadeIn(it, 0.1));
        }
        hold(0.9);

        LaTeXMob formula = new LaTeXMob("\\text{addr}(A[r][c]) = base + (r \\times 4 + c) \\times size")
                .setSize(26).setLatexColor(Colors.WHITE);
        formula.setPosition(0, 460);
        play(new Write(formula, 2.0));
        hold(2.0);

        StrokeTextMob closing = caption("All elements of a row are stored together", 20, Colors.LIGHT_GRAY);
        play(new Write(closing, 2.2));
        hold(2.4);

        MObject[] allCells = new MObject[N * 2];
        int ci = 0;
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++) {
                allCells[ci++] = box[r][c];
                allCells[ci++] = val[r][c];
            }
        MObject[] allIdx = new MObject[N * 2];
        for (int i = 0; i < N; i++) {
            allIdx[i * 2] = idxBox[i];
            allIdx[i * 2 + 1] = idxText[i];
        }
        fadeOutAll(0.9, allCells);
        fadeOutAll(0.9, allIdx);
        fadeOutAll(0.9, title, formula, closing);
        hold(0.5);
    }
}
