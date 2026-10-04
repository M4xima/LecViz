package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.core.Scene;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Easing;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Standalone clip: linear vs binary search on a sorted array.
 *
 * Opens with the array fading in plainly, then it fades to a translucent
 * green that stays for the rest of the clip as the array's base color,
 * the same way a color, once set, persists in the row/column-major clip.
 *
 * Linear search plays out as a real camera move that starts slow right
 * after the zoom-in, accelerates over its first two steps, then holds a
 * brisk constant pace the rest of the way. Binary search drops the index
 * itself out of the array at each bound — low/mid/high are pointers, not
 * values — landing as "low = 0", "mid = 5", "high = 11" below the array.
 * Closes on a Big-O comparison between the two.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSSearchScene extends Scene {

    private static final int[] VALUES = {2, 5, 9, 12, 17, 23, 31, 38, 45, 52, 60, 71};
    private static final int N = VALUES.length;
    private static final int TARGET = 45; // index 8 — a long linear scan, a short binary one

    private static final double CELL_W = 100, CELL_H = 60;
    private static final double ARRAY_Y = -60;
    private static final double CAPTION_Y = -280;

    // The array's base color is translucent green, permanently, once the
    // opening sweep sets it — everything else (current/checked/found) is
    // a temporary highlight on top of that.
    private static final Color BASE_FILL = Colors.withAlpha(Colors.GREEN, 0.26);
    private static final Color BASE_STROKE = Colors.withAlpha(Colors.GREEN, 0.6);
    private static final Color CURRENT_FILL = Colors.withAlpha(Colors.ORANGE, 0.42);
    private static final Color CURRENT_STROKE = Colors.ORANGE;
    private static final Color CHECKED_FILL = Colors.withAlpha(Colors.GREEN, 0.09);
    private static final Color CHECKED_STROKE = Colors.withAlpha(Colors.GREEN, 0.3);
    private static final Color FOUND_FILL = Colors.withAlpha(Colors.GREEN, 0.6);
    private static final Color FOUND_STROKE = Colors.GREEN;

    private double cellX(int i) {
        return -(N - 1) * CELL_W / 2.0 + i * CELL_W;
    }

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
        StrokeTextMob t = new StrokeTextMob(text, "Georgia", true, 34)
                .setFillColor(Colors.WHITE).setStrokeColor(Colors.WHITE);
        t.setPosition(0, 0);
        add(t);
        return t;
    }

    @Override
    public void construct() {
        StrokeTextMob title = new StrokeTextMob("Let's search this array", "Georgia", true, 54)
                .setFillColor(Colors.WHITE).setStrokeColor(Colors.WHITE);
        title.setPosition(0, -400);
        add(title);
        play(new Write(title, 2.6));
        hold(1.2);

        // ── The array fades in first, plainly; once it is fully visible
        //    it fades to green, and that becomes its color for the rest
        //    of the clip. ──
        RectMob[] box = new RectMob[N];
        TextMob[] val = new TextMob[N];
        for (int i = 0; i < N; i++) {
            RectMob b = new RectMob(CELL_W - 10, CELL_H - 10).setCornerRadius(8);
            b.setFillColor(Colors.withAlpha(Colors.LIGHT_GRAY, 0.08));
            b.setStrokeColor(Colors.withAlpha(Colors.LIGHT_GRAY, 0.45));
            b.setPosition(cellX(i), ARRAY_Y);
            b.setOpacity(0);
            box[i] = b;
            add(b);

            TextMob t = new TextMob(String.valueOf(VALUES[i])).setFontSize(24).setFillColor(Colors.WHITE);
            t.setPosition(cellX(i), ARRAY_Y);
            t.setOpacity(0);
            val[i] = t;
            add(t);
        }
        Animation[] settle = new Animation[N * 2];
        int si = 0;
        for (int i = 0; i < N; i++) {
            settle[si++] = new FadeIn(box[i], 0.7);
            settle[si++] = new FadeIn(val[i], 0.7);
        }
        play(settle);
        hold(0.5);

        // the array settles into its green base color with a plain fade
        Animation[] tint = new Animation[N * 2];
        for (int i = 0; i < N; i++) {
            tint[2 * i] = new ColorChange(box[i], BASE_FILL, 0.9);
            tint[2 * i + 1] = new ColorChange(box[i], BASE_STROKE, 0.9, ColorChange.Target.STROKE);
        }
        play(tint);
        hold(0.3);

        TextMob[] idxLabels = new TextMob[N];
        for (int i = 0; i < N; i++) {
            TextMob idx = new TextMob(String.valueOf(i)).setFontSize(14).setFillColor(Colors.DARK_GRAY);
            idx.setPosition(cellX(i), ARRAY_Y + CELL_H / 2.0 + 20);
            idx.setOpacity(0);
            add(idx);
            idxLabels[i] = idx;
            play(new FadeIn(idx, 0.05));
        }
        hold(0.6);

        StrokeTextMob findCaption = caption("Find " + TARGET, CAPTION_Y, 26, Colors.GOLD);
        play(new Write(findCaption, 1.3));
        hold(0.9);
        play(new FadeOut(findCaption, 0.5));
        remove(findCaption);

        // ── Linear search: the camera itself does the traversing.
        //    Slow right after the zoom-in, speeding up over the first
        //    two steps, then a constant brisk pace the rest of the way. ──
        StrokeTextMob linCaption = caption("Linear search: check every element, one by one", CAPTION_Y, 22, Colors.LIGHT_GRAY);
        play(new Write(linCaption, 1.8));
        hold(1.0);
        fadeOutAll(0.5, linCaption);

        double zoom = 2.1;
        ArrowMob pointer = new ArrowMob(0, -46, 0, -14);
        pointer.setStrokeColor(Colors.ORANGE);
        pointer.setPosition(cellX(0), ARRAY_Y);
        pointer.setOpacity(0);
        add(pointer);

        cameraTo(cellX(0), ARRAY_Y, zoom, 1.1);
        play(new FadeIn(pointer, 0.3));

        int linSteps = 0;
        for (int i = 0; i < N; i++) {
            linSteps++;
            double moveDur = i == 0 ? 0.3 : i == 1 ? 0.22 : 0.16;
            double holdDur = i == 0 ? 0.12 : i == 1 ? 0.08 : 0.05;

            box[i].setFillColor(CURRENT_FILL);
            box[i].setStrokeColor(CURRENT_STROKE);
            play(new MoveTo(pointer, cellX(i), ARRAY_Y, moveDur).setEasing(Easing.EASE_IN_OUT));
            cameraTo(cellX(i), ARRAY_Y, zoom, moveDur);
            hold(holdDur);
            if (VALUES[i] == TARGET) {
                box[i].setFillColor(FOUND_FILL);
                box[i].setStrokeColor(FOUND_STROKE);
                break;
            }
            box[i].setFillColor(CHECKED_FILL);
            box[i].setStrokeColor(CHECKED_STROKE);
        }
        hold(0.8);

        // Back to the normal view before showing the result — keeps the
        // caption from landing cropped at whatever zoom/pan we ended on.
        play(new FadeOut(pointer, 0.5));
        remove(pointer);
        resetCamera(1.1);

        StrokeTextMob foundLin = caption("Found — but it took " + linSteps + " comparisons", ARRAY_Y + 130, 22, Colors.GOLD);
        play(new Write(foundLin, 1.6));
        hold(1.4);
        fadeOutAll(0.6, foundLin);

        Animation[] resetColors = new Animation[N];
        for (int i = 0; i < N; i++) {
            resetColors[i] = new ColorChange(box[i], BASE_FILL, 0.6);
        }
        play(resetColors);
        for (int i = 0; i < N; i++) box[i].setStrokeColor(BASE_STROKE);
        hold(0.4);

        // ── Binary search: the index at each bound drops out of the
        //    array to sit next to its "low ="/"mid ="/"high =" tag. ──
        StrokeTextMob binCaption = caption("Binary search: cut the search space in half, every time", CAPTION_Y, 22, Colors.LIGHT_GRAY);
        play(new Write(binCaption, 1.9));
        hold(1.0);

        double dropY = ARRAY_Y + 170;
        double lowX = -300, midX = 0, highX = 300;

        TextMob lowPrefix = staticLabel("low =", lowX, dropY, Colors.BLUE);
        TextMob midPrefix = staticLabel("mid =", midX, dropY, Colors.GOLD);
        TextMob highPrefix = staticLabel("high =", highX, dropY, Colors.PURPLE);
        play(new FadeIn(lowPrefix, 0.3), new FadeIn(midPrefix, 0.3), new FadeIn(highPrefix, 0.3));

        int lo = 0, hi = N - 1, binSteps = 0, foundIdx = -1;
        TextMob lowVal = dropValue(null, lo, lowX + 62, dropY, Colors.BLUE);
        TextMob highVal = dropValue(null, hi, highX + 66, dropY, Colors.PURPLE);
        TextMob midVal = null;
        hold(0.6);

        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            binSteps++;
            midVal = dropValue(midVal, mid, midX + 62, dropY, Colors.GOLD);
            box[mid].setFillColor(CURRENT_FILL);
            box[mid].setStrokeColor(CURRENT_STROKE);
            hold(0.8);

            if (VALUES[mid] == TARGET) {
                box[mid].setFillColor(FOUND_FILL);
                box[mid].setStrokeColor(FOUND_STROKE);
                foundIdx = mid;
                break;
            } else if (VALUES[mid] < TARGET) {
                for (int k = lo; k <= mid; k++) {
                    box[k].setFillColor(CHECKED_FILL);
                    box[k].setStrokeColor(CHECKED_STROKE);
                }
                lo = mid + 1;
                lowVal = dropValue(lowVal, lo, lowX + 62, dropY, Colors.BLUE);
            } else {
                for (int k = mid; k <= hi; k++) {
                    box[k].setFillColor(CHECKED_FILL);
                    box[k].setStrokeColor(CHECKED_STROKE);
                }
                hi = mid - 1;
                highVal = dropValue(highVal, hi, highX + 66, dropY, Colors.PURPLE);
            }
            hold(0.4);
        }
        hold(0.6);

        List<MObject> binLabels = new ArrayList<>();
        binLabels.add(lowPrefix);
        binLabels.add(midPrefix);
        binLabels.add(highPrefix);
        if (lowVal != null) binLabels.add(lowVal);
        if (midVal != null) binLabels.add(midVal);
        if (highVal != null) binLabels.add(highVal);
        fadeOutAll(0.6, binLabels.toArray(new MObject[0]));

        StrokeTextMob foundBin = caption("Found in just " + binSteps + " comparisons", ARRAY_Y + 130, 22, Colors.GOLD);
        play(new Write(foundBin, 1.6));
        hold(1.6);
        fadeOutAll(0.6, foundBin, binCaption);

        // ── Time complexity, side by side ──
        LaTeXMob complexity = new LaTeXMob(
                "\\mathbf{\\text{Linear: } O(n) \\quad\\quad \\text{Binary: } O(\\log n)}")
                .setSize(34).setLatexColor(Colors.WHITE);
        complexity.setPosition(0, 190);
        add(complexity);
        play(new Write(complexity, 2.2));
        hold(1.6);

        StrokeTextMob big = bigStatement("Halving beats checking one by one");
        big.setPosition(0, 270);
        play(new Write(big, 2.2));
        hold(2.2);

        List<MObject> finale = new ArrayList<>();
        finale.add(title);
        finale.add(complexity);
        finale.add(big);
        for (int i = 0; i < N; i++) { finale.add(box[i]); finale.add(val[i]); finale.add(idxLabels[i]); }
        fadeOutAll(1.5, finale.toArray(new MObject[0]));
        hold(0.5);
    }

    /** A fixed "low ="/"mid ="/"high =" tag that never moves. */
    private TextMob staticLabel(String text, double x, double y, Color color) {
        TextMob t = new TextMob(text).setFontSize(22).setBold().setFillColor(color);
        t.setPosition(x, y);
        t.setOpacity(0);
        add(t);
        return t;
    }

    /**
     * Drops the index number itself (low/mid/high are pointers, not
     * values) from just under its cell down to sit beside its tag —
     * replacing whatever index was shown there before for that bound.
     */
    private TextMob dropValue(TextMob previous, int index, double targetX, double targetY, Color color) {
        if (previous != null) {
            play(new FadeOut(previous, 0.15));
            remove(previous);
        }
        TextMob v = new TextMob(String.valueOf(index)).setFontSize(22).setBold().setFillColor(color);
        v.setPosition(cellX(index), ARRAY_Y + CELL_H / 2.0 + 20);
        add(v);
        play(new MoveTo(v, targetX, targetY, 0.6).setEasing(Easing.EASE_IN));
        return v;
    }
}
