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

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

/**
 * Standalone clip covering slides 14-15 of the arrays deck: the sorting
 * overview and the "Sorting Algorithms at a Glance" table.
 *
 *   Slide 14  every line of the slide is written out in the pen-stroke style
 *             (outline first, then the fill running left to right), with its
 *             bullet popping in; when the slide is complete, all of its text
 *             swipes off to the right in a cascade while fading away
 *   Slide 15  the heading fades in sliding down from above, then the header
 *             row and each table row follow the same way, slowly, one by one
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSSortingScene extends Scene {

    private static final double PACE = 1.15;
    private double d(double seconds) { return seconds * PACE; }
    private void pause(double seconds) { hold(seconds * PACE); }

    // ── animations this clip needs ───────────────────────────────────

    /** Slides an object off to the right while it fades, after an optional delay. */
    private static final class SwipeOut extends Animation {
        private final double dx, delay, span;
        private double sx, sy;

        SwipeOut(MObject t, double dx, double delay, double dur) {
            super(t, delay + dur, Easing.LINEAR);
            this.dx = dx;
            this.delay = delay;
            this.span = dur;
        }

        @Override
        public void begin() {
            sx = target.getPosition().x();
            sy = target.getPosition().y();
        }

        @Override
        public void interpolate(double t) {
            double p = Math.max(0, Math.min(1, (t * duration - delay) / span));
            target.setPosition(sx + dx * Easing.EASE_IN.applyAsDouble(p), sy);
            target.setOpacity(1 - Math.pow(p, 1.4));
        }
    }

    /** Fades an object in while it slides down into place from above, after an optional delay. */
    private static final class DropIn extends Animation {
        private final double fromDy, delay, span;
        private double ex, ey;

        DropIn(MObject t, double fromDy, double delay, double dur) {
            super(t, delay + dur, Easing.LINEAR);
            this.fromDy = fromDy;
            this.delay = delay;
            this.span = dur;
        }

        @Override
        public void begin() {
            ex = target.getPosition().x();
            ey = target.getPosition().y();
            target.setOpacity(0);
            target.setPosition(ex, ey - fromDy);
        }

        @Override
        public void interpolate(double t) {
            double p = Math.max(0, Math.min(1, (t * duration - delay) / span));
            target.setPosition(ex, ey - fromDy * (1 - Easing.EASE_OUT.applyAsDouble(p)));
            target.setOpacity(Easing.SMOOTH.applyAsDouble(p));
        }
    }

    /** Grows a rectangle from its left edge — the highlighter sweep. */
    private static final class GrowRight extends Animation {
        private final RectMob r;
        private final double left, cy, w, h;

        GrowRight(RectMob r, double left, double cy, double w, double h, double dur) {
            super(r, dur, Easing.EASE_OUT);
            this.r = r;
            this.left = left;
            this.cy = cy;
            this.w = w;
            this.h = h;
        }

        @Override public void begin() { r.setOpacity(1); interpolate(0); }

        @Override
        public void interpolate(double t) {
            double ww = Math.max(0.01, w * t);
            r.setSize(ww, h);
            r.setPosition(left + ww / 2, cy);
        }
    }

    // ── text helpers ─────────────────────────────────────────────────

    private static double strokeW(String text, boolean bold, double size) {
        java.awt.Font f = new java.awt.Font("Georgia", bold ? java.awt.Font.BOLD : java.awt.Font.PLAIN, 1)
                .deriveFont(200f);
        GlyphVector gv = f.createGlyphVector(new FontRenderContext(new AffineTransform(), true, true), text);
        return gv.getLogicalBounds().getWidth() * size / 200.0;
    }

    private StrokeTextMob stroke(String text, double x, double y, double size, Color color, boolean bold) {
        StrokeTextMob t = new StrokeTextMob(text, "Georgia", bold, size)
                .setFillColor(color).setStrokeColor(color);
        t.setPosition(x, y);
        t.setOpacity(0);
        add(t);
        return t;
    }

    private StrokeTextMob strokeLeft(String text, double left, double y, double size, Color color) {
        return stroke(text, left + strokeW(text, false, size) / 2, y, size, color, false);
    }

    private TextMob label(String text, double x, double y, double size, Color color, boolean left, boolean bold) {
        TextMob t = new TextMob(text).setFontSize(size).setFillColor(color);
        if (left) t.setAlignment(TextAlignment.LEFT);
        if (bold) t.setBold();
        t.setPosition(x, y);
        t.setOpacity(0);
        add(t);
        return t;
    }

    private void playAll(List<Animation> anims) {
        if (!anims.isEmpty()) play(anims.toArray(new Animation[0]));
    }

    private void fadeOutAll(double dur, List<? extends MObject> objs) {
        List<Animation> anims = new ArrayList<>();
        for (MObject o : objs) if (o.getOpacity() > 0) anims.add(new FadeOut(o, dur));
        playAll(anims);
        for (MObject o : objs) remove(o);
    }

    // ── the scene ────────────────────────────────────────────────────

    @Override
    public void construct() {
        List<List<MObject>> slide14 = sortingSlide();
        pause(0.4);
        swipeAway(slide14);
        pause(0.5);
        glanceTable();
    }

    private static final class Line {
        final String text;
        final boolean main;
        final String keyword; // highlighted once the line is written, or null

        Line(String text, boolean main, String keyword) {
            this.text = text;
            this.main = main;
            this.keyword = keyword;
        }
    }

    // line groups of slide 14, in reading order
    private static final Line[] LINES = {
        new Line("A fundamental operation", true, null),
        new Line("Elements need to be stored in increasing order.", true, null),
        new Line("Some methods would work with duplicates.", false, null),
        new Line("Algorithms that maintain relative order of duplicates from input to output are called stable.", false, "stable"),
        new Line("Comparison-based methods", true, null),
        new Line("Insertion, Shell, Selection, Quick, Merge", false, null),
        new Line("Other methods", true, null),
        new Line("Radix, Bucket, Counting", false, null),
    };
    private static final double[] LINE_Y = {-290, -210, -140, -80, 10, 80, 170, 240};
    private static final Color[] BULLET = {Colors.GOLD, Colors.BLUE, Colors.TEAL, Colors.ORANGE};

    /** Writes slide 14 line by line; returns what is on screen grouped per line (bullet, text, highlight), top to bottom. */
    private List<List<MObject>> sortingSlide() {
        List<List<MObject>> shown = new ArrayList<>();
        StrokeTextMob head = stroke("Sorting", 0, -470, 54, Colors.WHITE, true);
        play(new Write(head, d(1.8)));
        List<MObject> headGroup = new ArrayList<>();
        headGroup.add(head);
        shown.add(headGroup);
        pause(0.5);

        int mainIdx = -1;
        for (int k = 0; k < LINES.length; k++) {
            Line ln = LINES[k];
            double y = LINE_Y[k];
            double size = ln.main ? 40 : 31;
            Color textColor = ln.main ? Colors.WHITE : Colors.LIGHT_GRAY;
            double left = ln.main ? -800 : -715;
            if (ln.main) mainIdx++;

            // bullet: a dot for each main point, a short dash for each sub-point
            MObject mark;
            if (ln.main) {
                CircleMob dot = new CircleMob(9);
                dot.setFillColor(BULLET[mainIdx % BULLET.length]);
                dot.setStrokeColor(Color.TRANSPARENT);
                dot.setPosition(-835, y);
                dot.setOpacity(0);
                dot.setScale(0.2);
                add(dot);
                mark = dot;
            } else {
                RectMob dash = new RectMob(24, 4).setCornerRadius(2);
                dash.setFillColor(Colors.GRAY);
                dash.setStrokeColor(Color.TRANSPARENT);
                dash.setPosition(-755, y);
                dash.setOpacity(0);
                add(dash);
                mark = dash;
            }

            StrokeTextMob t = strokeLeft(ln.text, left, y, size, textColor);
            double secs = Math.max(1.3, ln.text.length() * (ln.main ? 0.06 : 0.045));
            play(new FadeIn(mark, d(0.35)), new ScaleTo(mark, 1.0, d(0.45)).setEasing(Easing.EASE_OUT),
                    new Write(t, d(secs)));
            List<MObject> group = new ArrayList<>();
            group.add(mark);
            group.add(t);
            shown.add(group);

            if (ln.keyword != null) {
                int idx = ln.text.indexOf(ln.keyword);
                double pre = strokeW(ln.text.substring(0, idx), false, size);
                double kw = strokeW(ln.text.substring(0, idx + ln.keyword.length()), false, size) - pre;
                RectMob band = new RectMob(kw + 14, size * 1.3).setCornerRadius(8);
                band.setFillColor(Colors.withAlpha(Colors.ORANGE, 0.34));
                band.setStrokeColor(Color.TRANSPARENT);
                band.setOpacity(0);
                band.setPosition(left + pre + kw / 2, y);
                add(band);
                play(new GrowRight(band, left + pre - 7, y, kw + 14, size * 1.3, d(0.7)));
                group.add(band);
            }
            pause(ln.main ? 0.45 : 0.3);
        }
        return shown;
    }

    /**
     * Everything on slide 14 swipes off to the right in a top-to-bottom cascade, fading as it
     * goes. A line's bullet, text and highlight leave together so nothing slides into its neighbor.
     */
    private void swipeAway(List<List<MObject>> shown) {
        List<Animation> out = new ArrayList<>();
        for (int i = 0; i < shown.size(); i++)
            for (MObject m : shown.get(i))
                out.add(new SwipeOut(m, 900, 0.1 * i * PACE, d(1.0)));
        playAll(out);
        for (List<MObject> group : shown)
            for (MObject m : group) remove(m);
    }

    // ── slide 15 ─────────────────────────────────────────────────────

    private static final String[][] ROWS = {
        {"Bubble", "O(n²)", "O(n²)"},
        {"Insertion", "O(n²)", "O(n²)"},
        {"Shell", "O(n²)", "Depends on increment sequence"},
        {"Selection", "O(n²)", "O(n²)"},
        {"Heap", "O(n log n)", "O(n log n)"},
        {"Quick", "O(n²)", "O(n log n) depending on partitioning"},
        {"Merge", "O(n log n)", "O(n log n)"},
        {"Bucket", "O(n α log α)", "Depends on α"},
    };

    /** Cell tint by complexity: slow quadratic red, n log n green, "depends" gold, bucket blue. */
    private static Color tint(String cell) {
        if (cell.startsWith("Depends")) return Colors.GOLD;
        if (cell.contains("α")) return Colors.BLUE;
        if (cell.contains("log")) return Colors.GREEN;
        return Colors.RED;
    }

    private void glanceTable() {
        StrokeTextMob head = stroke("Sorting Algorithms at a Glance", 0, -470, 54, Colors.WHITE, true);
        List<Animation> anims = new ArrayList<>();
        anims.add(new DropIn(head, 110, 0, d(1.2)));
        List<MObject> all = new ArrayList<>();
        all.add(head);

        double[] colW = {300, 400, 660};
        double gap = 6;
        double total = colW[0] + colW[1] + colW[2] + 2 * gap;
        double[] cx = new double[3];
        double x = -total / 2;
        for (int c = 0; c < 3; c++) {
            cx[c] = x + colW[c] / 2;
            x += colW[c] + gap;
        }
        double headH = 84, rowH = 72, step = 76;
        double headY = -330;
        double firstRowY = headY + headH / 2 + 5 + rowH / 2;

        String[] headers = {"Algorithm", "Worst case complexity", "Average case complexity"};
        double t0 = d(1.3);
        double rowGap = d(0.85);
        double dur = d(1.15);
        for (int c = 0; c < 3; c++) {
            RectMob box = new RectMob(colW[c], headH).setCornerRadius(8);
            box.setFillColor(Colors.withAlpha(Colors.BLUE, 0.38));
            box.setStrokeColor(Colors.BLUE);
            box.setPosition(cx[c], headY);
            box.setOpacity(0);
            add(box);
            TextMob txt = (c == 0)
                    ? label(headers[c], cx[c] - colW[c] / 2 + 26, headY, 30, Colors.WHITE, true, true)
                    : label(headers[c], cx[c], headY, 30, Colors.WHITE, false, true);
            anims.add(new DropIn(box, 80, t0, dur));
            anims.add(new DropIn(txt, 80, t0, dur));
            all.add(box);
            all.add(txt);
        }
        for (int r = 0; r < ROWS.length; r++) {
            double y = firstRowY + step * r;
            double start = t0 + rowGap * (r + 1);
            for (int c = 0; c < 3; c++) {
                String cell = ROWS[r][c];
                Color base = c == 0 ? Colors.WHITE : tint(cell);
                RectMob box = new RectMob(colW[c], rowH).setCornerRadius(8);
                box.setFillColor(Colors.withAlpha(base, c == 0 ? 0.07 : 0.17));
                box.setStrokeColor(Colors.withAlpha(base, c == 0 ? 0.35 : 0.6));
                box.setPosition(cx[c], y);
                box.setOpacity(0);
                add(box);
                TextMob txt = (c == 0)
                        ? label(cell, cx[c] - colW[c] / 2 + 26, y, 30, Colors.WHITE, true, false)
                        : label(cell, cx[c], y, 30, Colors.WHITE, false, false);
                anims.add(new DropIn(box, 80, start, dur));
                anims.add(new DropIn(txt, 80, start, dur));
                all.add(box);
                all.add(txt);
            }
        }
        playAll(anims);
        pause(3.5);
        fadeOutAll(1.5, all);
        pause(0.5);
    }
}
