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
 * Standalone clip: how a 2D array A[4][4] actually sits in memory — first
 * row-major, then, continuing straight on in the same video, column-major.
 *
 * Row-major half: title -> grid (values flicker then settle) -> row colors
 * -> unroll into a horizontal strip -> formula + big statement, both
 * centered. That pair then slides off to the right while fading, at the
 * same time the strip regroups back into a 4-row grid shifted left; each
 * row's label reappears beside it with its color pouring in top to bottom.
 *
 * Column-major half: same grid, cells shrink and cascade column-by-column
 * into a vertical strip on the right (so the row colors land interleaved
 * instead of in blocks) -> formula + big statement, centered -> everything
 * fades out together at the very end.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSArrayExpressionsScene extends Scene {

    private static final int SIZE = 4;
    private static final int N = SIZE * SIZE;
    private static final Color[] ROW_COLOR = { Colors.BLUE, Colors.GREEN, Colors.PURPLE, Colors.GOLD };
    private static final String[] ROW_NAME = { "Row 0", "Row 1", "Row 2", "Row 3" };
    private static final int[] RANDOM_VALUES = { 42, 17, 88, 3, 56, 91, 24, 67, 9, 73, 38, 15, 82, 6, 50, 29 };

    // A single reusable caption line for the row-major half.
    private static final double CAPTION_Y = 180;

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

    /** The big, centered, stroke-drawn statement each half closes on. */
    private StrokeTextMob bigStatement(String text) {
        StrokeTextMob t = new StrokeTextMob(text, "Georgia", true, 36)
                .setFillColor(Colors.WHITE).setStrokeColor(Colors.WHITE);
        t.setPosition(0, 30);
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

        double cell = 100;

        RectMob[][] box = new RectMob[SIZE][SIZE];
        TextMob[][] val = new TextMob[SIZE][SIZE];
        buildGrid(box, val, cell, 0);

        StrokeTextMob gridCaption = caption("In C, C++, Java, we use row-major storage", CAPTION_Y, 22, Colors.LIGHT_GRAY);
        play(new Write(gridCaption, 1.8));
        hold(1.3);
        play(new FadeOut(gridCaption, 0.6));
        remove(gridCaption);

        colorRows(box);

        // ── "Row-major storage" ──
        StrokeTextMob unfoldCaption = caption("Row-major storage", CAPTION_Y, 26, Colors.LIGHT_GRAY);
        play(new Write(unfoldCaption, 1.4));
        hold(0.8);

        double stripY = 320;
        double stripX0 = -((N - 1) * cell) / 2.0;

        RectMob[] slot = horizontalSlots(cell, stripY, stripX0);

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
                double targetX = stripX0 + slotIdx * cell;
                slide[k++] = new MoveTo(box[r][c], targetX, stripY, 1.0).setEasing(Easing.EASE_IN_OUT);
                slide[k++] = new MoveTo(val[r][c], targetX, stripY, 1.0).setEasing(Easing.EASE_IN_OUT);
            }
            play(slide);

            FadeOut[] slotFade = new FadeOut[SIZE];
            for (int c = 0; c < SIZE; c++) slotFade[c] = new FadeOut(slot[r * SIZE + c], 0.2);
            play(slotFade);
            for (int c = 0; c < SIZE; c++) remove(slot[r * SIZE + c]);

            hold(0.35);
        }
        hold(0.6);

        play(new FadeOut(unfoldCaption, 0.6));
        remove(unfoldCaption);

        RectMob[] idxBox = new RectMob[N];
        TextMob[] idxText = new TextMob[N];
        for (int i = 0; i < N; i++) {
            double x = stripX0 + i * cell;
            double y = stripY + 68;
            idxBox[i] = outlineIndexBox(x, y, 38, 32);
            idxText[i] = indexLabel(x, y, i);
            play(new FadeIn(idxBox[i], 0.1), new FadeIn(idxText[i], 0.1));
        }
        hold(0.9);

        // ── Formula and big statement, centered, formula above ──
        LaTeXMob rowFormula = new LaTeXMob("\\mathbf{\\text{addr}(A[r][c]) = base + (r \\times 4 + c) \\times size}")
                .setSize(36).setLatexColor(Colors.WHITE);
        rowFormula.setPosition(0, -60);
        add(rowFormula);
        play(new Write(rowFormula, 2.0));
        hold(1.2);

        StrokeTextMob rowBig = bigStatement("All elements of a row are stored together");
        play(new Write(rowBig, 2.4));
        hold(2.2);

        // ── Transition: the formula + statement slide off to the right
        //    while fading, at the same moment the strip regroups back
        //    into a 4-row grid shifted left to make room for the column
        //    that's coming. Everything moves together, nothing is cut. ──
        double gridCenterX = -350;
        double gx0 = gridCenterX - (SIZE - 1) * cell / 2.0;
        double gy0 = -60 - (SIZE - 1) * cell / 2.0;

        java.util.List<Animation> transition = new java.util.ArrayList<>();
        transition.add(new MoveTo(rowFormula, 1500, rowFormula.getPosition().y(), 1.3).setEasing(Easing.EASE_IN));
        transition.add(new FadeOut(rowFormula, 1.3));
        transition.add(new MoveTo(rowBig, 1500, rowBig.getPosition().y(), 1.3).setEasing(Easing.EASE_IN));
        transition.add(new FadeOut(rowBig, 1.3));
        for (int i = 0; i < N; i++) {
            transition.add(new FadeOut(idxBox[i], 0.9));
            transition.add(new FadeOut(idxText[i], 0.9));
        }
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                double x = gx0 + c * cell;
                double y = gy0 + r * cell;
                transition.add(new MoveTo(box[r][c], x, y, 1.3).setEasing(Easing.EASE_IN_OUT));
                transition.add(new MoveTo(val[r][c], x, y, 1.3).setEasing(Easing.EASE_IN_OUT));
            }
        }
        play(transition.toArray(new Animation[0]));
        remove(rowFormula);
        remove(rowBig);
        for (int i = 0; i < N; i++) { remove(idxBox[i]); remove(idxText[i]); }
        hold(0.4);

        // ── Row labels reappear beside each row, colors set directly ──
        double labelX = gridCenterX + (SIZE * cell) / 2.0 + 70;
        java.util.List<Animation> relabel = new java.util.ArrayList<>();
        StrokeTextMob[] rowLbl2 = new StrokeTextMob[SIZE];
        for (int r = 0; r < SIZE; r++) {
            double rowY = gy0 + r * cell;
            rowLbl2[r] = new StrokeTextMob(ROW_NAME[r], "Georgia", false, 24)
                    .setFillColor(ROW_COLOR[r]).setStrokeColor(ROW_COLOR[r]);
            rowLbl2[r].setPosition(labelX, rowY);
            add(rowLbl2[r]);
            relabel.add(new Write(rowLbl2[r], 1.0));

            for (int c = 0; c < SIZE; c++) {
                relabel.add(new ColorChange(box[r][c], Colors.withAlpha(ROW_COLOR[r], 0.30), 1.0));
            }
        }
        play(relabel.toArray(new Animation[0]));
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                box[r][c].setStrokeColor(Colors.withAlpha(ROW_COLOR[r], 0.8));
        hold(0.9);
        fadeOutAll(0.6, rowLbl2);

        // ── Column-major half ──
        double colCaptionY = -300;
        StrokeTextMob colCaption = caption("In Fortran, we use column-major storage.", colCaptionY, 22, Colors.LIGHT_GRAY);
        play(new Write(colCaption, 1.8));
        hold(1.3);
        play(new FadeOut(colCaption, 0.6));
        remove(colCaption);

        StrokeTextMob colUnfold = caption("Column-major storage", colCaptionY, 26, Colors.LIGHT_GRAY);
        play(new Write(colUnfold, 1.4));
        hold(0.8);

        double columnX = 560;
        double colCellSize = 44, colSpacing = 42;
        double colScale = colCellSize / (cell - 10);
        double colCenterY = 100;
        double colY0 = colCenterY - (N - 1) * colSpacing / 2.0;

        RectMob[] colSlot = new RectMob[N];
        for (int i = 0; i < N; i++) {
            double targetY = colY0 + i * colSpacing;
            RectMob s = new RectMob(colCellSize, colCellSize).setCornerRadius(5);
            s.setFillColor(Color.TRANSPARENT);
            s.setStrokeColor(Colors.withAlpha(Colors.LIGHT_GRAY, 0.55));
            s.setPosition(columnX, targetY + 60);
            s.setOpacity(0);
            colSlot[i] = s;
            add(s);
        }
        Animation[] slotIn = new Animation[N * 2];
        int sli = 0;
        for (int i = 0; i < N; i++) {
            double targetY = colY0 + i * colSpacing;
            slotIn[sli++] = new FadeIn(colSlot[i], 0.4);
            slotIn[sli++] = new MoveTo(colSlot[i], columnX, targetY, 0.5).setEasing(Easing.EASE_OUT);
        }
        play(slotIn);
        hold(0.7);

        // Cascade column by column: each wave takes one cell from every
        // row, shrinking to fit the square slots, so the row colors land
        // interleaved instead of in blocks.
        for (int c = 0; c < SIZE; c++) {
            Animation[] lift = new Animation[SIZE * 2];
            int k = 0;
            for (int r = 0; r < SIZE; r++) {
                lift[k++] = new MoveTo(box[r][c], box[r][c].getPosition().x() + 14, box[r][c].getPosition().y(), 0.18)
                        .setEasing(Easing.EASE_OUT);
                lift[k++] = new MoveTo(val[r][c], val[r][c].getPosition().x() + 14, val[r][c].getPosition().y(), 0.18)
                        .setEasing(Easing.EASE_OUT);
            }
            play(lift);

            Animation[] slide = new Animation[SIZE * 4];
            k = 0;
            for (int r = 0; r < SIZE; r++) {
                int slotIdx = c * SIZE + r;
                double targetY = colY0 + slotIdx * colSpacing;
                slide[k++] = new MoveTo(box[r][c], columnX, targetY, 1.0).setEasing(Easing.EASE_IN_OUT);
                slide[k++] = new MoveTo(val[r][c], columnX, targetY, 1.0).setEasing(Easing.EASE_IN_OUT);
                slide[k++] = new ScaleTo(box[r][c], colScale, 1.0);
                slide[k++] = new ScaleTo(val[r][c], colScale, 1.0);
            }
            play(slide);

            FadeOut[] slotFade = new FadeOut[SIZE];
            for (int r = 0; r < SIZE; r++) slotFade[r] = new FadeOut(colSlot[c * SIZE + r], 0.2);
            play(slotFade);
            for (int r = 0; r < SIZE; r++) remove(colSlot[c * SIZE + r]);

            hold(0.35);
        }
        hold(0.6);

        play(new FadeOut(colUnfold, 0.6));
        remove(colUnfold);

        // ── The view pans right and zooms in very slightly to center on
        //    the column, as if the frame itself were sliding right; the
        //    title exits as though left behind by that motion. ──
        cameraToWith(columnX, colCenterY, 1.1, 1.4, new FadeOut(title, 1.4));
        remove(title);
        hold(0.3);

        RectMob[] colIdxBox = new RectMob[N];
        TextMob[] colIdxText = new TextMob[N];
        double idxX = columnX + colCellSize / 2.0 + 10 + 19;
        for (int i = 0; i < N; i++) {
            double y = colY0 + i * colSpacing;
            colIdxBox[i] = outlineIndexBox(idxX, y, 38, colCellSize - 8);
            colIdxText[i] = indexLabel(idxX, y, i);
            play(new FadeIn(colIdxBox[i], 0.1), new FadeIn(colIdxText[i], 0.1));
        }
        hold(0.9);

        // ── Formula and big statement, centered on the new (panned) view,
        //    sitting in the open space below the column so neither
        //    overlaps it ──
        LaTeXMob colFormula = new LaTeXMob("\\mathbf{\\text{addr}(A[r][c]) = base + (c \\times 4 + r) \\times size}")
                .setSize(36).setLatexColor(Colors.WHITE);
        colFormula.setPosition(columnX, 470);
        add(colFormula);
        play(new Write(colFormula, 2.0));
        hold(1.2);

        StrokeTextMob colBig = bigStatement("Each column is stored together");
        colBig.setPosition(columnX, 545);
        play(new Write(colBig, 2.4));
        hold(2.2);

        // Everything dissolves together in one smooth motion.
        MObject[] finale = new MObject[N * 4 + 3];
        int fi = 0;
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++) {
                finale[fi++] = box[r][c];
                finale[fi++] = val[r][c];
            }
        for (int i = 0; i < N; i++) {
            finale[fi++] = colIdxBox[i];
            finale[fi++] = colIdxText[i];
        }
        finale[fi++] = title;
        finale[fi++] = colFormula;
        finale[fi++] = colBig;
        fadeOutAll(1.6, finale);
        hold(0.5);
    }

    private void buildGrid(RectMob[][] box, TextMob[][] val, double cell, double centerX) {
        double gx0 = centerX - (SIZE - 1) * cell / 2.0;
        double gy0 = -80 - (SIZE - 1) * cell / 2.0;

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

        Animation[] boxFade = new Animation[N];
        int bi = 0;
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                boxFade[bi++] = new FadeIn(box[r][c], 0.5);
        play(boxFade);
        hold(0.5);

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
    }

    private void colorRows(RectMob[][] box) {
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
    }

    private RectMob[] horizontalSlots(double cell, double stripY, double stripX0) {
        RectMob[] slot = new RectMob[N];
        for (int i = 0; i < N; i++) {
            double targetX = stripX0 + i * cell;
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
        return slot;
    }

    private RectMob outlineIndexBox(double x, double y, double w, double h) {
        RectMob ib = new RectMob(w, h).setCornerRadius(5);
        ib.setFillColor(Color.TRANSPARENT);
        ib.setStrokeColor(Colors.withAlpha(Colors.GRAY, 0.6));
        ib.setPosition(x, y);
        ib.setOpacity(0);
        add(ib);
        return ib;
    }

    private TextMob indexLabel(double x, double y, int i) {
        TextMob it = new TextMob(String.valueOf(i)).setFontSize(16).setFillColor(Colors.GRAY);
        it.setPosition(x, y);
        it.setOpacity(0);
        add(it);
        return it;
    }
}
