package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.core.Scene;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Easing;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

/**
 * Standalone clip covering slides 10-12 of the arrays deck: the surprise
 * quiz and the four classwork questions.
 *
 *   Slide 10     the quiz words are taken apart (Tris-kai-deka = 3 and 10),
 *                a lift skips its 13th label and a calendar rings Friday the
 *                13th; the gap bridges into the classwork
 *   Slides 11-12 all four questions are written out one by one, then each
 *                is explained with its own animation: two-pointer merge and
 *                a pointer-rewiring linked list, a histogram built from
 *                falling dots, a product matrix against its output-size
 *                lower bound, and the smallest absent roll number via
 *                in-place placement
 *
 * Slide 13 (8-queens) is its own clip, {@link PDSQueensScene}, which reuses
 * this class's helper kit and {@link #queensSection()}.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSClassworkScene extends Scene {

    // ── pacing / palette ─────────────────────────────────────────────

    private static final double PACE = 1.15;
    private double d(double seconds) { return seconds * PACE; }
    private void pause(double seconds) { hold(seconds * PACE); }

    private static final Color INK = Color.web("#08080D");
    private static final Color GHOST_FILL = Colors.withAlpha(Colors.WHITE, 0.04);
    private static final Color GHOST_STROKE = Colors.withAlpha(Colors.WHITE, 0.16);
    private static final Color HOT_FILL = Colors.withAlpha(Colors.ORANGE, 0.42);
    private static final Color HOT_STROKE = Colors.ORANGE;
    private static final Color FOUND_FILL = Colors.withAlpha(Colors.GREEN, 0.62);
    private static final Color FOUND_STROKE = Colors.GREEN;
    private static final Color IGNORE_FILL = Colors.withAlpha(Colors.GRAY, 0.12);
    private static final Color IGNORE_STROKE = Colors.withAlpha(Colors.GRAY, 0.4);

    // ── small mobjects & animations this clip needs ─────────────────

    /** A straight segment that can be drawn on progressively. */
    private static final class LineMob extends MObject {
        private final double x1, y1, x2, y2;
        private double fraction = 1.0;

        LineMob(double x1, double y1, double x2, double y2, Color color, double width) {
            this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
            this.strokeColor = color;
            this.strokeWidth = width;
            this.opacity = 0;
        }

        void setFraction(double f) { fraction = Math.max(0, Math.min(1, f)); }

        @Override
        protected void draw(GraphicsContext gc) {
            gc.setStroke(strokeColor);
            gc.setLineWidth(strokeWidth);
            gc.setLineCap(StrokeLineCap.ROUND);
            gc.strokeLine(x1, y1, x1 + (x2 - x1) * fraction, y1 + (y2 - y1) * fraction);
        }

        @Override
        public MObject copy() {
            LineMob c = new LineMob(x1, y1, x2, y2, strokeColor, strokeWidth);
            copyBaseProperties(c);
            c.fraction = fraction;
            return c;
        }
    }

    private static final class DrawLine extends Animation {
        DrawLine(LineMob l, double dur) { super(l, dur, Easing.EASE_OUT); }

        @Override public void begin() { ((LineMob) target).setFraction(0); target.setOpacity(1); }
        @Override public void interpolate(double t) { ((LineMob) target).setFraction(t); }
    }

    /** A little crown, drawn as a polygon, standing in for a chess queen. */
    private static final class QueenMob extends MObject {
        private static final double[] PX = {-0.38, 0.38, 0.34, 0.40, 0.20, 0.0, -0.20, -0.40, -0.34};
        private static final double[] PY = {0.40, 0.40, 0.18, -0.20, -0.02, -0.34, -0.02, -0.20, 0.18};
        private final double size;

        QueenMob(double size, Color fill, Color stroke) {
            this.size = size;
            this.fillColor = fill;
            this.strokeColor = stroke;
            this.strokeWidth = 2;
            this.opacity = 0;
        }

        @Override
        protected void draw(GraphicsContext gc) {
            double[] xs = new double[PX.length], ys = new double[PY.length];
            for (int i = 0; i < PX.length; i++) { xs[i] = PX[i] * size; ys[i] = PY[i] * size; }
            gc.setFill(fillColor);
            gc.fillPolygon(xs, ys, xs.length);
            gc.setStroke(strokeColor);
            gc.setLineWidth(strokeWidth);
            gc.strokePolygon(xs, ys, xs.length);
            double r = 0.065 * size;
            double[][] tips = {{0.40, -0.20}, {0.0, -0.34}, {-0.40, -0.20}};
            for (double[] t : tips) gc.fillOval(t[0] * size - r, t[1] * size - r, 2 * r, 2 * r);
        }

        @Override
        public MObject copy() {
            QueenMob c = new QueenMob(size, fillColor, strokeColor);
            copyBaseProperties(c);
            return c;
        }
    }

    /** Moves along a curved path (bulge > 0 arcs "up" relative to the direction of travel). */
    private static final class ArcMove extends Animation {
        private final double ex, ey, bulge;
        private double sx, sy;

        ArcMove(MObject target, double ex, double ey, double bulge, double dur) {
            super(target, dur, Easing.EASE_IN_OUT);
            this.ex = ex; this.ey = ey; this.bulge = bulge;
        }

        @Override
        public void begin() { sx = target.getPosition().x(); sy = target.getPosition().y(); }

        @Override
        public void interpolate(double t) {
            double dx = ex - sx, dy = ey - sy;
            double len = Math.max(1e-6, Math.hypot(dx, dy));
            double arc = Math.sin(Math.PI * t) * bulge;
            target.setPosition(sx + dx * t + (dy / len) * arc, sy + dy * t - (dx / len) * arc);
        }
    }

    /** Grows a rectangle: from its left edge outward, or upward from its bottom edge. */
    private static final class GrowRect extends Animation {
        private final RectMob r;
        private final double ax, ay, w, h;
        private final boolean up;

        GrowRect(RectMob r, double ax, double ay, double w, double h, boolean up, double dur) {
            super(r, dur, Easing.EASE_OUT);
            this.r = r; this.ax = ax; this.ay = ay; this.w = w; this.h = h; this.up = up;
        }

        @Override public void begin() { r.setOpacity(1); interpolate(0); }

        @Override
        public void interpolate(double t) {
            if (!up) {
                double ww = Math.max(0.01, w * t);
                r.setSize(ww, h);
                r.setPosition(ax + ww / 2, ay);
            } else {
                double hh = Math.max(0.01, h * t);
                r.setSize(w, hh);
                r.setPosition(ax, ay - hh / 2);
            }
        }
    }

    /** A fade-in that starts after a delay, so several can stagger inside one play(). */
    private static final class FadeInAt extends Animation {
        private final double delay, span;

        FadeInAt(MObject t, double delay, double dur) {
            super(t, delay + dur, Easing.LINEAR);
            this.delay = delay;
            this.span = dur;
        }

        @Override public void begin() { target.setOpacity(0); }

        @Override
        public void interpolate(double t) {
            double p = Math.max(0, Math.min(1, (t * duration - delay) / span));
            target.setOpacity(Easing.SMOOTH.applyAsDouble(p));
        }
    }

    /** A box with a value in it. */
    private static final class Cell {
        final RectMob box;
        final TextMob text;
        final double w, h;
        Color baseFill, baseStroke;

        Cell(RectMob box, TextMob text, double w, double h) {
            this.box = box; this.text = text; this.w = w; this.h = h;
        }

        double x() { return box.getPosition().x(); }
        double y() { return box.getPosition().y(); }
    }

    /** An arrow plus its letter, moved together along a row of cells. */
    private static final class Ptr {
        final ArrowMob arrow;
        final TextMob lab;
        final double baseX, labY;

        Ptr(ArrowMob arrow, TextMob lab, double baseX, double labY) {
            this.arrow = arrow; this.lab = lab; this.baseX = baseX; this.labY = labY;
        }

        void go(List<Animation> into, double x, double dur) {
            into.add(new MoveTo(arrow, x - baseX, 0, dur).setEasing(Easing.EASE_IN_OUT));
            into.add(new MoveTo(lab, x, labY, dur).setEasing(Easing.EASE_IN_OUT));
        }

        List<MObject> parts() {
            List<MObject> l = new ArrayList<>();
            l.add(arrow);
            l.add(lab);
            return l;
        }
    }

    // ── measuring & building text ────────────────────────────────────

    private static double strokeW(String text, boolean bold, double size) {
        java.awt.Font f = new java.awt.Font("Georgia", bold ? java.awt.Font.BOLD : java.awt.Font.PLAIN, 1)
                .deriveFont(200f);
        GlyphVector gv = f.createGlyphVector(new FontRenderContext(new AffineTransform(), true, true), text);
        return gv.getLogicalBounds().getWidth() * size / 200.0;
    }

    private static double textW(String s, double size, boolean bold) {
        javafx.scene.text.Text t = new javafx.scene.text.Text(s);
        t.setFont(Font.font("SansSerif", bold ? FontWeight.BOLD : FontWeight.NORMAL, size));
        return t.getLayoutBounds().getWidth();
    }

    private StrokeTextMob stroke(String text, double x, double y, double size, Color color, boolean bold) {
        StrokeTextMob t = new StrokeTextMob(text, "Georgia", bold, size)
                .setFillColor(color).setStrokeColor(color);
        t.setPosition(x, y);
        t.setOpacity(0);
        add(t);
        return t;
    }

    private StrokeTextMob strokeLeft(String text, double left, double y, double size, Color color, boolean bold) {
        return stroke(text, left + strokeW(text, bold, size) / 2, y, size, color, bold);
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

    private LaTeXMob latex(String src, double size, double x, double y) {
        LaTeXMob l = new LaTeXMob(src).setSize((float) size).setLatexColor(Colors.WHITE);
        l.setPosition(x, y);
        l.setOpacity(0);
        add(l);
        return l;
    }

    private void fadeOutAll(double dur, List<? extends MObject> objs) {
        List<Animation> anims = new ArrayList<>();
        for (MObject o : objs) if (o.getOpacity() > 0) anims.add(new FadeOut(o, dur));
        playList(anims);
        for (MObject o : objs) remove(o);
    }

    private void fadeOutAll(double dur, MObject... objs) {
        List<MObject> l = new ArrayList<>();
        for (MObject o : objs) l.add(o);
        fadeOutAll(dur, l);
    }

    private void playList(List<Animation> anims) {
        if (!anims.isEmpty()) play(anims.toArray(new Animation[0]));
    }

    private StrokeTextMob title;

    private void setTitle(String text) {
        if (title != null) {
            play(new FadeOut(title, 0.5));
            remove(title);
        }
        title = stroke(text, 0, -500, 50, Colors.WHITE, true);
        play(new Write(title, 2.0));
    }

    // ── cells, pointers, arrows ──────────────────────────────────────

    private Cell cell(String value, double x, double y, double w, double h, double fs, Color accent, boolean visible) {
        RectMob box = new RectMob(w - 6, h - 6).setCornerRadius(6);
        Color fill = Colors.withAlpha(accent, 0.22), stroke = Colors.withAlpha(accent, 0.7);
        box.setFillColor(fill);
        box.setStrokeColor(stroke);
        box.setPosition(x, y);
        box.setOpacity(visible ? 1 : 0);
        add(box);
        TextMob text = new TextMob(value).setFontSize(fs).setFillColor(Colors.WHITE);
        text.setPosition(x, y);
        text.setOpacity(visible ? 1 : 0);
        add(text);
        Cell c = new Cell(box, text, w, h);
        c.baseFill = fill;
        c.baseStroke = stroke;
        return c;
    }

    private void show(List<Animation> into, Cell c, double dur) {
        into.add(new FadeIn(c.box, dur));
        into.add(new FadeIn(c.text, dur));
    }

    private void showAt(List<Animation> into, Cell c, double delay, double dur) {
        into.add(new FadeInAt(c.box, delay, dur));
        into.add(new FadeInAt(c.text, delay, dur));
    }

    private void paint(List<Animation> into, Cell c, Color fill, Color stroke, double dur) {
        into.add(new ColorChange(c.box, fill, dur));
        into.add(new ColorChange(c.box, stroke, dur, ColorChange.Target.STROKE));
    }

    private void travel(List<Animation> into, Cell c, double x, double y, double bulge, double dur) {
        into.add(new ArcMove(c.box, x, y, bulge, dur));
        into.add(new ArcMove(c.text, x, y, bulge, dur));
    }

    private void cells(List<MObject> into, Cell... cs) {
        for (Cell c : cs) { into.add(c.box); into.add(c.text); }
    }

    private static double boundary(double hw, double hh, double ux, double uy) {
        double tx = Math.abs(ux) < 1e-9 ? Double.MAX_VALUE : hw / Math.abs(ux);
        double ty = Math.abs(uy) < 1e-9 ? Double.MAX_VALUE : hh / Math.abs(uy);
        return Math.min(tx, ty);
    }

    /** An arrow from the edge of one cell to the edge of another (hidden until drawn). */
    private ArrowMob edgeArrow(Cell a, Cell b, Color color, double head) {
        double x1 = a.x(), y1 = a.y(), x2 = b.x(), y2 = b.y();
        double len = Math.hypot(x2 - x1, y2 - y1), ux = (x2 - x1) / len, uy = (y2 - y1) / len;
        double ta = boundary(a.w / 2, a.h / 2, ux, uy) + 6, tb = boundary(b.w / 2, b.h / 2, ux, uy) + 6;
        ArrowMob arr = new ArrowMob(x1 + ux * ta, y1 + uy * ta, x2 - ux * tb, y2 - uy * tb);
        arr.setHeadLength(head);
        arr.setStrokeColor(color);
        arr.setStrokeWidth(3);
        arr.setOpacity(0);
        add(arr);
        return arr;
    }

    private Ptr pointer(String name, double x, double yTip, boolean pointsDown, Color color) {
        double y0 = pointsDown ? yTip - 36 : yTip + 36;
        double labY = pointsDown ? yTip - 54 : yTip + 54;
        ArrowMob arr = new ArrowMob(x, y0, x, yTip);
        arr.setHeadLength(11);
        arr.setStrokeColor(color);
        arr.setStrokeWidth(3);
        arr.setOpacity(0);
        add(arr);
        TextMob lab = label(name, x, labY, 28, color, false, true);
        return new Ptr(arr, lab, x, labY);
    }

    private RectMob keywordBand(String text, String keyword, double size, double left, double y, Color accent) {
        int idx = text.indexOf(keyword);
        double pre = strokeW(text.substring(0, idx), false, size);
        double kw = strokeW(text.substring(0, idx + keyword.length()), false, size) - pre;
        RectMob b = new RectMob(kw + 14, size * 1.25).setCornerRadius(7);
        b.setFillColor(Colors.withAlpha(accent, 0.30));
        b.setStrokeColor(Color.TRANSPARENT);
        b.setOpacity(0);
        b.setPosition(left + pre + kw / 2, y);
        add(b);
        return b;
    }

    private Animation bandGrow(RectMob b, double dur) {
        return new GrowRect(b, b.getPosition().x() - b.getWidth() / 2, b.getPosition().y(), b.getWidth(), b.getHeight(), false, dur);
    }

    /** Numbered badge + restated question across the top of an explanation. */
    private List<MObject> header(int n, String text, Color accent) {
        double w = strokeW(text, true, 30);
        double bx = -w / 2 - 46;
        CircleMob badge = new CircleMob(24);
        badge.setFillColor(Colors.withAlpha(accent, 0.35));
        badge.setStrokeColor(accent);
        badge.setStrokeWidth(3);
        badge.setPosition(bx, -430);
        badge.setOpacity(0);
        badge.setScale(0.2);
        add(badge);
        TextMob num = label(String.valueOf(n), bx, -430, 28, accent, false, true);
        StrokeTextMob t = stroke(text, 0, -430, 30, Colors.WHITE, true);
        play(new FadeIn(badge, d(0.4)), new ScaleTo(badge, 1.0, d(0.5)).setEasing(Easing.EASE_OUT),
                new FadeIn(num, d(0.4)), new Write(t, d(1.8)));
        List<MObject> l = new ArrayList<>();
        l.add(badge);
        l.add(num);
        l.add(t);
        return l;
    }

    @Override
    public void construct() {
        quizOpener();
        classworkOverview();
        mergeQuestion();
        histogramQuestion();
        productQuestion();
        absentQuestion();
        fadeOutAll(1.2, title);
        pause(0.5);
    }

    // ═════════════════════════════════════════════════════════════════
    //  Slide 10 — Surprise Quiz
    // ═════════════════════════════════════════════════════════════════

    /** Splits a long word into colored parts with a gloss under each; returns everything created. */
    private List<MObject> wordBreakdown(String[] parts, String[] gloss, Color[] colors, double y, double size) {
        double[] w = new double[parts.length];
        double total = 0;
        for (int i = 0; i < parts.length; i++) { w[i] = textW(parts[i], size, true); total += w[i]; }
        List<MObject> made = new ArrayList<>();
        TextMob[] pt = new TextMob[parts.length];
        TextMob[] gl = new TextMob[parts.length];
        double x = -total / 2;
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < parts.length; i++) {
            double cx = x + w[i] / 2;
            pt[i] = label(parts[i], cx, y, size, Colors.WHITE, false, true);
            gl[i] = label(gloss[i], cx, y + size * 0.95, 30, colors[i], false, true);
            made.add(pt[i]);
            made.add(gl[i]);
            in.add(new FadeIn(pt[i], d(0.7)));
            x += w[i];
        }
        playList(in);
        pause(0.5);
        for (int i = 0; i < parts.length; i++) {
            gl[i].setScale(0.6);
            play(new ColorChange(pt[i], colors[i], d(0.35)), new FadeIn(gl[i], d(0.45)),
                    new ScaleTo(gl[i], 1.0, d(0.45)).setEasing(Easing.EASE_OUT));
            pause(0.25);
        }
        return made;
    }

    private void shake(MObject o, double amp) {
        double x = o.getPosition().x(), y = o.getPosition().y();
        for (int k = 0; k < 4; k++) {
            play(new MoveTo(o, x + (k % 2 == 0 ? amp : -amp), y, 0.06));
        }
        play(new MoveTo(o, x, y, 0.06));
    }

    private void relabel(Cell c, String s) {
        play(new ScaleTo(c.text, 1.4, d(0.12)));
        c.text.setText(s);
        play(new ScaleTo(c.text, 1.0, d(0.16)));
    }

    private void quizOpener() {
        setTitle("Surprise Quiz");
        pause(0.6);

        StrokeTextMob q1 = stroke("What is Triskaidekaphobia?", 0, -405, 34, Colors.WHITE, false);
        play(new Write(q1, d(2.0)));
        pause(0.5);
        StrokeTextMob q2 = stroke("What is Paraskevidekatriaphobia?", 0, -350, 34, Colors.WHITE, false);
        play(new Write(q2, d(2.4)));
        pause(1.0);
        play(new ColorChange(q2, Colors.GRAY, d(0.5)));

        // ── Question 1: the word, taken apart; a lift that skips 13 ──
        List<MObject> a1 = new ArrayList<>(wordBreakdown(new String[]{"Tris", "kai", "deka", "phobia"},
                new String[]{"3", "and", "10", "fear"},
                new Color[]{Colors.BLUE, Colors.GREEN, Colors.GOLD, Colors.RED}, -235, 62));
        TextMob sum = label("3 + 10 = 13   →   fear of 13", 0, -105, 38, Colors.GOLD, false, true);
        sum.setScale(0.7);
        play(new FadeIn(sum, d(0.6)), new ScaleTo(sum, 1.0, d(0.6)).setEasing(Easing.EASE_OUT));
        a1.add(sum);
        pause(0.9);

        double pitch = 132, sx0 = -462, yLab = 25, yFloor = 85;
        Cell[] lab = new Cell[8];
        TextMob[] floorTxt = new TextMob[8];
        List<Animation> in = new ArrayList<>();
        for (int p = 9; p <= 16; p++) {
            int i = p - 9;
            lab[i] = cell(String.valueOf(p), sx0 + pitch * i, yLab, 120, 70, 40, Colors.GREEN, false);
            floorTxt[i] = label(String.valueOf(p), sx0 + pitch * i, yFloor, 22, Colors.GRAY, false, false);
            show(in, lab[i], d(0.6));
            in.add(new FadeIn(floorTxt[i], d(0.6)));
            cells(a1, lab[i]);
            a1.add(floorTxt[i]);
        }
        TextMob tl = label("label", -690, yLab, 22, Colors.LIGHT_GRAY, true, false);
        TextMob tf = label("floor", -690, yFloor, 22, Colors.LIGHT_GRAY, true, false);
        in.add(new FadeIn(tl, d(0.6)));
        in.add(new FadeIn(tf, d(0.6)));
        a1.add(tl);
        a1.add(tf);
        playList(in);
        pause(0.8);

        // floor 13 gets the label 14, and every label after it shifts by one
        List<Animation> warn = new ArrayList<>();
        paint(warn, lab[4], Colors.withAlpha(Colors.RED, 0.38), Colors.RED, d(0.4));
        warn.add(new ColorChange(lab[4].text, Colors.RED, d(0.4)));
        playList(warn);
        shake(lab[4].text, 8);
        TextMob ghost = label("13", lab[4].x(), yLab, 40, Colors.RED, false, false);
        ghost.setOpacity(1);
        lab[4].text.setText("14");
        lab[4].text.setFillColor(Colors.WHITE);
        play(new MoveTo(ghost, lab[4].x(), yLab + 190, d(0.9)).setEasing(Easing.EASE_IN),
                new FadeOut(ghost, d(0.9)), new ScaleTo(lab[4].text, 1.0, 0.1));
        remove(ghost);
        for (int i = 5; i < 8; i++) {
            relabel(lab[i], String.valueOf(9 + i + 1));
            pause(0.05);
        }
        pause(0.5);
        StrokeTextMob ans1 = stroke("Fear of the number 13 — lifts jump from 12 to 14, stalls from 12 to 12A to 14.",
                0, 255, 25, Colors.ORANGE, false);
        play(new Write(ans1, d(3.0)));
        a1.add(ans1);
        pause(2.0);
        fadeOutAll(d(0.7), a1);

        // ── Question 2: Friday + 13, on a calendar ──
        play(new ColorChange(q1, Colors.GRAY, d(0.5)), new ColorChange(q2, Colors.WHITE, d(0.5)));
        List<MObject> a2 = new ArrayList<>(wordBreakdown(new String[]{"Paraskevi", "dekatria", "phobia"},
                new String[]{"Friday", "13", "fear"},
                new Color[]{Colors.BLUE, Colors.GOLD, Colors.RED}, -235, 62));
        pause(0.6);

        double cw = 72, rh = 44;
        String[] dow = {"S", "M", "T", "W", "T", "F", "S"};
        List<Animation> cal = new ArrayList<>();
        Cell[] days = new Cell[31];
        for (int c = 0; c < 7; c++) {
            TextMob h = label(dow[c], (c - 3) * cw, -62, 24, c == 5 ? Colors.GOLD : Colors.LIGHT_GRAY, false, true);
            cal.add(new FadeIn(h, d(0.6)));
            a2.add(h);
        }
        for (int dd = 1; dd <= 30; dd++) {
            int r = (dd - 1) / 7, c = (dd - 1) % 7;
            days[dd] = cell(String.valueOf(dd), (c - 3) * cw, -14 + rh * r, cw, rh, 20, Colors.BLUE, false);
            showAt(cal, days[dd], 0.02 * (r * 7 + c), d(0.5));
            cells(a2, days[dd]);
        }
        playList(cal);
        pause(0.5);

        RectMob fri = new RectMob(cw - 4, rh * 5 + 8).setCornerRadius(8);
        fri.setFillColor(Colors.withAlpha(Colors.GOLD, 0.16));
        fri.setStrokeColor(Colors.withAlpha(Colors.GOLD, 0.7));
        fri.setPosition(2 * cw, -14 + rh * 2);
        fri.setOpacity(0);
        add(fri);
        a2.add(fri);
        play(new FadeIn(fri, d(0.6)));
        pause(0.4);
        List<Animation> hot = new ArrayList<>();
        paint(hot, days[13], Colors.withAlpha(Colors.RED, 0.45), Colors.RED, d(0.4));
        hot.add(new ColorChange(days[13].text, Colors.RED, d(0.4)));
        playList(hot);
        shake(days[13].text, 6);
        pause(0.4);
        StrokeTextMob ans2 = stroke("Fear of Friday the 13th.", 0, 255, 28, Colors.ORANGE, false);
        play(new Write(ans2, d(1.8)));
        a2.add(ans2);
        pause(1.6);
        fadeOutAll(d(0.7), a2);
        fadeOutAll(d(0.6), q1, q2);

        // ── Bridge: labels skip numbers, arrays don't ──
        StrokeTextMob br1 = stroke("Labels can skip a number — an array stores its slots side by side.", 0, -300, 28, Colors.WHITE, false);
        play(new Write(br1, d(2.8)));
        pause(0.4);
        String[] labs = {"10", "11", "12", "14", "15", "16"};
        List<MObject> bridge = new ArrayList<>();
        bridge.add(br1);
        Cell[] bc = new Cell[6];
        TextMob[] expect = new TextMob[6];
        List<Animation> bin = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            bc[i] = cell(labs[i], -330 + 132 * i, -10, 120, 70, 38, Colors.GREEN, false);
            TextMob idx = label(String.valueOf(i), -330 + 132 * i, 58, 22, Colors.GRAY, false, false);
            expect[i] = label("expect " + (10 + i), -330 + 132 * i, -88, 22, Colors.GRAY, false, false);
            show(bin, bc[i], d(0.6));
            bin.add(new FadeIn(idx, d(0.6)));
            bin.add(new FadeIn(expect[i], d(0.6)));
            cells(bridge, bc[i]);
            bridge.add(idx);
            bridge.add(expect[i]);
        }
        playList(bin);
        pause(0.6);
        RectMob scan = new RectMob(120, 76).setCornerRadius(8);
        scan.setFillColor(Color.TRANSPARENT);
        scan.setStrokeColor(Colors.WHITE);
        scan.setStrokeWidth(3.5);
        scan.setPosition(-330, -10);
        scan.setOpacity(0);
        add(scan);
        bridge.add(scan);
        play(new FadeIn(scan, d(0.4)));
        for (int i = 0; i < 6; i++) {
            if (i > 0) play(new MoveTo(scan, -330 + 132 * i, -10, d(0.4)).setEasing(Easing.EASE_IN_OUT));
            boolean ok = Integer.parseInt(labs[i]) == 10 + i;
            List<Animation> a = new ArrayList<>();
            if (ok) {
                paint(a, bc[i], FOUND_FILL, FOUND_STROKE, d(0.3));
                a.add(new ColorChange(expect[i], Colors.GREEN, d(0.3)));
            } else {
                paint(a, bc[i], Colors.withAlpha(Colors.RED, 0.4), Colors.RED, d(0.3));
                a.add(new ColorChange(expect[i], Colors.RED, d(0.3)));
                a.add(new ColorChange(scan, Colors.RED, d(0.3), ColorChange.Target.STROKE));
            }
            playList(a);
            pause(ok ? 0.2 : 0.8);
            if (!ok) break;
        }
        StrokeTextMob br2 = stroke("13 is absent — but spotting the gap took a scan. Can we do better?", 0, 190, 28, Colors.GOLD, false);
        play(new Write(br2, d(2.8)));
        bridge.add(br2);
        pause(2.0);
        fadeOutAll(d(0.8), bridge);
    }

    // ═════════════════════════════════════════════════════════════════
    //  Slides 11-12 — the classwork questions, written out one by one
    // ═════════════════════════════════════════════════════════════════

    /** One numbered question: badge pops, sentence is written, keyword highlighted, sub-bullets fade in. */
    private List<MObject> card(int n, Color accent, String main, String keyword, double y, String[] subs) {
        List<MObject> made = new ArrayList<>();
        double left = -770;
        CircleMob badge = new CircleMob(25);
        badge.setFillColor(Colors.withAlpha(accent, 0.35));
        badge.setStrokeColor(accent);
        badge.setStrokeWidth(3);
        badge.setPosition(-830, y);
        badge.setOpacity(0);
        badge.setScale(0.2);
        add(badge);
        CircleMob ripple = new CircleMob(25);
        ripple.setFillColor(Color.TRANSPARENT);
        ripple.setStrokeColor(accent);
        ripple.setStrokeWidth(3);
        ripple.setPosition(-830, y);
        ripple.setOpacity(0);
        add(ripple);
        TextMob num = label(String.valueOf(n), -830, y, 30, accent, false, true);
        play(new FadeIn(badge, d(0.4)), new ScaleTo(badge, 1.0, d(0.5)).setEasing(Easing.EASE_OUT), new FadeIn(num, d(0.4)));
        ripple.setOpacity(1);
        StrokeTextMob t = strokeLeft(main, left, y, 32, Colors.WHITE, false);
        play(new Write(t, d(2.6)), new ScaleTo(ripple, 2.6, d(0.9)).setEasing(Easing.EASE_OUT), new FadeOut(ripple, d(0.9)));
        remove(ripple);
        RectMob band = keywordBand(main, keyword, 32, left, y, accent);
        play(bandGrow(band, d(0.7)));
        made.add(badge);
        made.add(num);
        made.add(t);
        made.add(band);
        pause(0.4);

        List<Animation> in = new ArrayList<>();
        for (int k = 0; k < subs.length; k++) {
            TextMob s = label(subs[k], left + 40, y + 52 + 46 * k, 26, Colors.LIGHT_GRAY, true, false);
            in.add(new FadeInAt(s, 0.6 * k, d(0.7)));
            made.add(s);
        }
        playList(in);
        pause(0.5);
        return made;
    }

    private List<MObject> icon(int which, double cx, double cy, Color accent) {
        List<MObject> made = new ArrayList<>();
        List<Animation> in = new ArrayList<>();
        switch (which) {
            case 1 -> { // two sorted rows zip into one
                RectMob[] sq = new RectMob[6];
                for (int i = 0; i < 6; i++) {
                    boolean a = i < 3;
                    sq[i] = new RectMob(24, 24).setCornerRadius(4);
                    sq[i].setFillColor(Colors.withAlpha(a ? Colors.BLUE : Colors.PINK, 0.5));
                    sq[i].setStrokeColor(a ? Colors.BLUE : Colors.PINK);
                    sq[i].setPosition(cx - 40 + 40 * (i % 3), cy + (a ? -22 : 22));
                    sq[i].setOpacity(0);
                    add(sq[i]);
                    made.add(sq[i]);
                    in.add(new FadeIn(sq[i], d(0.5)));
                }
                playList(in);
                pause(0.3);
                List<Animation> zip = new ArrayList<>();
                for (int i = 0; i < 6; i++) {
                    int m = (i % 3) * 2 + (i < 3 ? 0 : 1);
                    zip.add(new MoveTo(sq[i], cx - 100 + 40 * m, cy, d(1.0)).setEasing(Easing.EASE_IN_OUT));
                }
                playList(zip);
            }
            case 2 -> { // bars growing
                double[] hs = {30, 62, 96, 70, 40};
                List<Animation> grow = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    RectMob b = new RectMob(24, 10).setCornerRadius(3);
                    Color c = Colors.interpolate(Colors.BLUE, Colors.ORANGE, i / 4.0);
                    b.setFillColor(Colors.withAlpha(c, 0.55));
                    b.setStrokeColor(c);
                    b.setPosition(cx - 64 + 32 * i, cy + 40);
                    b.setOpacity(0);
                    add(b);
                    made.add(b);
                    grow.add(new GrowRect(b, cx - 64 + 32 * i, cy + 48, 24, hs[i], true, d(0.9)));
                }
                playList(grow);
            }
            case 3 -> { // outer-product grid appearing diagonally
                for (int r = 0; r < 3; r++)
                    for (int c = 0; c < 4; c++) {
                        boolean edge = false;
                        RectMob b = new RectMob(24, 24).setCornerRadius(4);
                        b.setFillColor(Colors.withAlpha(Colors.TEAL, 0.4));
                        b.setStrokeColor(Colors.TEAL);
                        b.setPosition(cx - 48 + 32 * c, cy - 32 + 32 * r);
                        b.setOpacity(0);
                        add(b);
                        made.add(b);
                        in.add(new FadeInAt(b, 0.12 * (r + c), d(0.5)));
                    }
                playList(in);
            }
            default -> { // a row with one slot missing
                for (int i = 0; i < 5; i++) {
                    boolean gap = i == 2;
                    RectMob b = new RectMob(24, 24).setCornerRadius(4);
                    b.setFillColor(gap ? Color.TRANSPARENT : Colors.withAlpha(accent, 0.45));
                    b.setStrokeColor(gap ? Colors.RED : accent);
                    b.setPosition(cx - 64 + 32 * i, cy);
                    b.setOpacity(0);
                    add(b);
                    made.add(b);
                    in.add(new FadeInAt(b, 0.1 * i, d(0.5)));
                }
                TextMob q = label("?", cx, cy, 24, Colors.RED, false, true);
                made.add(q);
                in.add(new FadeInAt(q, 0.6, d(0.5)));
                playList(in);
            }
        }
        return made;
    }

    private void classworkOverview() {
        setTitle("Arrays: Classwork");
        pause(0.8);

        // ── Slide 11: three questions, one at a time ──
        List<MObject> page = new ArrayList<>();
        page.addAll(card(1, Colors.TEAL, "Merge two sorted arrays", "Merge", -330,
                new String[]{"–  In a third array", "–  In situ (also check with linked lists)"}));
        page.addAll(icon(1, 790, -310, Colors.TEAL));
        pause(0.6);
        page.addAll(card(2, Colors.ORANGE, "For a given data, create a histogram", "histogram", -110,
                new String[]{"–  Numbers of students in [0..10), [10, 20), ..., [90, 100]."}));
        page.addAll(icon(2, 790, -140, Colors.ORANGE));
        pause(0.6);
        page.addAll(card(3, Colors.BLUE, "Given two arrays of sizes N1 and N2, find a product matrix (P[i][j] = A[i] * B[j]).",
                "product", 110,
                new String[]{"–  Can this be done in O(N1 + N2) time?", "–  or O(N1 log N2)?"}));
        page.addAll(icon(3, 790, 150, Colors.BLUE));
        pause(2.2);
        fadeOutAll(d(0.8), page);
        pause(0.3);

        // ── Slide 12: the fourth question, with its three examples ──
        List<MObject> page2 = new ArrayList<>();
        String q4 = "Given an unsorted array of roll numbers, find the smallest CS18 roll number absent today.";
        page2.addAll(card(4, Colors.GOLD, q4, "CS18 roll number absent", -330, new String[0]));
        pause(0.4);

        String[][] ex = {
            {"2", "3", "7", "6", "8", "CH…", "10", "15"},
            {"2", "3", "EE…", "6", "8", "1", "CH…", "15"},
            {"1", "1", "EE…", "EE…", "EE…"},
        };
        String[] out = {"1", "4", "2"};
        for (int e = 0; e < 3; e++) {
            double y = -205 + 85 * e;
            List<Animation> row = new ArrayList<>();
            for (int k = 0; k < ex[e].length; k++) {
                boolean num = Character.isDigit(ex[e][k].charAt(0));
                Cell c = cell(ex[e][k], -730 + 70 * k, y, 66, 52, num ? 22 : 17, num ? Colors.GREEN : Colors.PINK, false);
                showAt(row, c, 0.09 * k, d(0.5));
                cells(page2, c);
            }
            double endX = -730 + 70 * (ex[e].length - 1) + 33;
            TextMob outs = label("outputs", endX + 24, y, 26, Colors.LIGHT_GRAY, true, false);
            double ow = textW("outputs", 26, false);
            TextMob val = label(out[e], endX + 24 + ow + 30, y, 40, Colors.GOLD, false, true);
            val.setScale(0.5);
            row.add(new FadeInAt(outs, 0.09 * ex[e].length + 0.2, d(0.5)));
            page2.add(outs);
            page2.add(val);
            playList(row);
            play(new FadeIn(val, d(0.4)), new ScaleTo(val, 1.0, d(0.5)).setEasing(Easing.EASE_OUT));
            pause(0.5);
        }

        String last = "Can this be done in linear time and constant additional space?";
        double lastLeft = -strokeW(last, false, 32) / 2;
        StrokeTextMob lt = strokeLeft(last, lastLeft, 100, 32, Colors.WHITE, false);
        play(new Write(lt, d(2.8)));
        RectMob b1 = keywordBand(last, "linear", 32, lastLeft, 100, Colors.GOLD);
        RectMob b2 = keywordBand(last, "constant", 32, lastLeft, 100, Colors.TEAL);
        play(bandGrow(b1, d(0.6)));
        play(bandGrow(b2, d(0.6)));
        page2.add(lt);
        page2.add(b1);
        page2.add(b2);
        pause(2.6);
        fadeOutAll(d(0.8), page2);
        pause(0.3);
    }

    // ═════════════════════════════════════════════════════════════════
    //  Question 1 — merge two sorted arrays
    // ═════════════════════════════════════════════════════════════════

    private void mergeQuestion() {
        List<MObject> hdr = header(1, "Merge two sorted arrays", Colors.TEAL);
        StrokeTextMob sub = stroke("In a third array", 0, -372, 24, Colors.LIGHT_GRAY, false);
        play(new Write(sub, d(1.4)));

        int[] A = {2, 6, 11, 19, 30}, B = {4, 5, 9, 17};
        double pitch = 96, x0 = -384, yA = -240, yB = -100, yC = 90;
        List<MObject> part = new ArrayList<>();
        part.add(sub);
        Cell[] a = new Cell[A.length], b = new Cell[B.length];
        RectMob[] slot = new RectMob[A.length + B.length];
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < A.length; i++) {
            a[i] = cell(String.valueOf(A[i]), x0 + pitch * i, yA, 90, 58, 26, Colors.BLUE, false);
            show(in, a[i], d(0.6));
            cells(part, a[i]);
        }
        for (int j = 0; j < B.length; j++) {
            b[j] = cell(String.valueOf(B[j]), x0 + pitch * j, yB, 90, 58, 26, Colors.PINK, false);
            show(in, b[j], d(0.6));
            cells(part, b[j]);
        }
        for (int k = 0; k < slot.length; k++) {
            slot[k] = new RectMob(84, 52).setCornerRadius(6);
            slot[k].setFillColor(GHOST_FILL);
            slot[k].setStrokeColor(GHOST_STROKE);
            slot[k].setPosition(x0 + pitch * k, yC);
            slot[k].setOpacity(0);
            add(slot[k]);
            in.add(new FadeIn(slot[k], d(0.6)));
            part.add(slot[k]);
        }
        String[] rn = {"A", "B", "C"};
        Color[] rc = {Colors.BLUE, Colors.PINK, Colors.GOLD};
        double[] ry = {yA, yB, yC};
        for (int r = 0; r < 3; r++) {
            TextMob t = label(rn[r], x0 - 92, ry[r], 34, rc[r], false, true);
            in.add(new FadeIn(t, d(0.6)));
            part.add(t);
        }
        Ptr pI = pointer("i", x0, yA - 30, true, Colors.BLUE);
        Ptr pJ = pointer("j", x0, yB - 30, true, Colors.PINK);
        in.add(new FadeIn(pI.arrow, d(0.6)));
        in.add(new FadeIn(pI.lab, d(0.6)));
        in.add(new FadeIn(pJ.arrow, d(0.6)));
        in.add(new FadeIn(pJ.lab, d(0.6)));
        part.addAll(pI.parts());
        part.addAll(pJ.parts());
        TextMob tA = label("", 170, -240, 32, Colors.BLUE, true, true);
        TextMob tB = label("", 170, -192, 32, Colors.PINK, true, true);
        TextMob tRes = label("", 170, -128, 32, Colors.WHITE, true, true);
        part.add(tA);
        part.add(tB);
        part.add(tRes);
        playList(in);
        pause(0.8);
        play(new FadeIn(tA, d(0.4)), new FadeIn(tB, d(0.4)), new FadeIn(tRes, d(0.4)));

        int i = 0, j = 0;
        for (int k = 0; k < A.length + B.length; k++) {
            double sp = k < 3 ? 1.0 : (k < 6 ? 0.7 : 0.55);
            boolean aOk = i < A.length, bOk = j < B.length;
            boolean takeA = aOk && (!bOk || A[i] <= B[j]);
            tA.setText(aOk ? "A[i] = " + A[i] : "A is empty");
            tB.setText(bOk ? "B[j] = " + B[j] : "B is empty");
            if (aOk && bOk) {
                tRes.setText(A[i] + (takeA ? "  <  " : "  >  ") + B[j] + "   →   take " + (takeA ? "A[i]" : "B[j]"));
            } else {
                tRes.setText("copy the rest of " + (aOk ? "A" : "B"));
            }
            tRes.setFillColor(takeA ? Colors.BLUE : Colors.PINK);
            List<Animation> hi = new ArrayList<>();
            if (aOk) paint(hi, a[i], HOT_FILL, HOT_STROKE, d(0.3 * sp));
            if (bOk) paint(hi, b[j], HOT_FILL, HOT_STROKE, d(0.3 * sp));
            playList(hi);
            pause(0.45 * sp);

            Cell src = takeA ? a[i] : b[j];
            Cell other = takeA ? (bOk ? b[j] : null) : (aOk ? a[i] : null);
            int v = takeA ? A[i] : B[j];
            Cell tok = cell(String.valueOf(v), src.x(), src.y(), 90, 58, 26, takeA ? Colors.BLUE : Colors.PINK, true);
            cells(part, tok);
            List<Animation> fl = new ArrayList<>();
            travel(fl, tok, x0 + pitch * k, yC, takeA ? -60 : 60, d(0.75 * sp));
            paint(fl, src, GHOST_FILL, GHOST_STROKE, d(0.4 * sp));
            fl.add(new ColorChange(src.text, Colors.GRAY, d(0.4 * sp)));
            if (other != null) paint(fl, other, other.baseFill, other.baseStroke, d(0.4 * sp));
            boolean aDone = false, bDone = false;
            if (takeA) { i++; aDone = i == A.length; } else { j++; bDone = j == B.length; }
            Ptr moved = takeA ? pI : pJ;
            if (takeA ? aDone : bDone) {
                for (MObject m : moved.parts()) fl.add(new FadeOut(m, d(0.4 * sp)));
            } else {
                moved.go(fl, x0 + pitch * (takeA ? i : j), d(0.5 * sp));
            }
            playList(fl);
        }
        pause(0.6);
        StrokeTextMob s1 = stroke("Every element is copied once: N1 + N2 steps, and a third array of N1 + N2 cells.",
                0, 215, 24, Colors.ORANGE, false);
        play(new Write(s1, d(3.0)));
        LaTeXMob lt = latex("\\mathbf{time:\\;O(N_1+N_2)\\qquad extra\\;space:\\;O(N_1+N_2)}", 32, 0, 290);
        play(new Write(lt, d(2.0)));
        part.add(s1);
        part.add(lt);
        pause(2.2);
        fadeOutAll(d(0.8), part);

        // ── In situ: arrays shift, linked lists just rewire ──
        StrokeTextMob sub2 = stroke("In situ — no third array", 0, -372, 24, Colors.LIGHT_GRAY, false);
        play(new Write(sub2, d(1.4)));
        part.clear();
        part.add(sub2);

        // arrays: inserting 4 after 2 shifts a whole block
        int[] mem = {2, 6, 11, 19, 30, 4, 5, 9, 17};
        Cell[] mc = new Cell[mem.length];
        List<Animation> min = new ArrayList<>();
        double yM = -140;
        for (int k = 0; k < mem.length; k++) {
            mc[k] = cell(String.valueOf(mem[k]), x0 + pitch * k, yM, 90, 58, 26, k < 5 ? Colors.BLUE : Colors.PINK, false);
            show(min, mc[k], d(0.6));
            cells(part, mc[k]);
        }
        StrokeTextMob s2 = stroke("Arrays: to place 4 after 2, everything in between has to shift right.", 0, -30, 24, Colors.LIGHT_GRAY, false);
        TextMob shifts = label("shifts: 0", 0, 45, 38, Colors.GOLD, false, true);
        part.add(s2);
        part.add(shifts);
        playList(min);
        play(new Write(s2, d(2.6)));
        play(new FadeIn(shifts, d(0.4)));
        pause(0.5);
        List<Animation> lift = new ArrayList<>();
        travel(lift, mc[5], x0 + pitch * 5, yM - 105, 0, d(0.5));
        playList(lift);
        int moves = 0;
        for (int k = 4; k >= 1; k--) {
            List<Animation> sh = new ArrayList<>();
            travel(sh, mc[k], x0 + pitch * (k + 1), yM, 0, d(0.32));
            playList(sh);
            moves++;
            shifts.setText("shifts: " + moves);
        }
        List<Animation> drop = new ArrayList<>();
        travel(drop, mc[5], x0 + pitch * 1, yM, 0, d(0.6));
        playList(drop);
        pause(0.5);
        StrokeTextMob s3 = stroke("…and every later insertion shifts again — up to O(N1 · N2) moves overall.", 0, 125, 24, Colors.ORANGE, false);
        play(new Write(s3, d(2.8)));
        part.add(s3);
        pause(1.4);

        // a better in-place algorithm exists: leave the room a moment to think about it
        String bq = "Can we do better?";
        StrokeTextMob better = stroke(bq, 0, 215, 38, Colors.GOLD, false);
        play(new Write(better, d(1.8)));
        RectMob bBand = keywordBand(bq, "better", 38, -strokeW(bq, false, 38) / 2, 215, Colors.GOLD);
        play(bandGrow(bBand, d(0.7)));
        pause(0.5);
        StrokeTextMob think = stroke("Think of it", 0, 282, 32, Colors.WHITE, false);
        play(new Write(think, d(1.4)));
        part.add(better);
        part.add(bBand);
        part.add(think);
        pause(3.4);
        part.remove(sub2);
        fadeOutAll(d(0.8), part);
        part.clear();
        part.add(sub2);

        // linked lists: nodes stay put, only next pointers are rewired
        double nx0 = -380, px = 190, yX = -170, yY = 40;
        Cell[] node = new Cell[9];
        List<Animation> lin = new ArrayList<>();
        for (int k = 0; k < 5; k++) {
            node[k] = cell(String.valueOf(A[k]), nx0 + px * k, yX, 100, 62, 28, Colors.BLUE, false);
            show(lin, node[k], d(0.6));
            cells(part, node[k]);
        }
        for (int k = 0; k < 4; k++) {
            node[5 + k] = cell(String.valueOf(B[k]), nx0 + px * k, yY, 100, 62, 28, Colors.PINK, false);
            show(lin, node[5 + k], d(0.6));
            cells(part, node[5 + k]);
        }
        TextMob lA = label("A", nx0 - 130, yX, 34, Colors.BLUE, false, true);
        TextMob lB = label("B", nx0 - 130, yY, 34, Colors.PINK, false, true);
        lin.add(new FadeIn(lA, d(0.6)));
        lin.add(new FadeIn(lB, d(0.6)));
        part.add(lA);
        part.add(lB);
        playList(lin);
        ArrowMob[] out = new ArrowMob[9];
        int[] outTo = new int[9];
        java.util.Arrays.fill(outTo, -1);
        List<Animation> draw = new ArrayList<>();
        for (int k = 0; k < 4; k++) {
            out[k] = edgeArrow(node[k], node[k + 1], Colors.BLUE, 12);
            outTo[k] = k + 1;
            draw.add(new DrawArrow(out[k], d(0.9)));
        }
        for (int k = 0; k < 3; k++) {
            out[5 + k] = edgeArrow(node[5 + k], node[6 + k], Colors.PINK, 12);
            outTo[5 + k] = 6 + k;
            draw.add(new DrawArrow(out[5 + k], d(0.9)));
        }
        playList(draw);
        for (ArrowMob ar : out) if (ar != null) part.add(ar);
        StrokeTextMob s4 = stroke("Linked lists: nodes stay where they are — only the next pointers change.", 0, 180, 24, Colors.ORANGE, false);
        play(new Write(s4, d(2.8)));
        part.add(s4);
        pause(0.5);

        Ptr pp = pointer("p", nx0, yX - 36, true, Colors.BLUE);
        Ptr pq = pointer("q", nx0, yY + 36, false, Colors.PINK);
        play(new FadeIn(pp.arrow, d(0.4)), new FadeIn(pp.lab, d(0.4)), new FadeIn(pq.arrow, d(0.4)), new FadeIn(pq.lab, d(0.4)));
        part.addAll(pp.parts());
        part.addAll(pq.parts());
        TextMob head = label("head", node[0].x() - 46, node[0].y() + 50, 22, Colors.GREEN, false, true);
        part.add(head);

        int p = 0, q = 0, last = -1;
        for (int k = 0; k < 9; k++) {
            double sp = k < 3 ? 1.0 : (k < 6 ? 0.7 : 0.55);
            boolean pOk = p < A.length, qOk = q < B.length;
            boolean takeA = pOk && (!qOk || A[p] <= B[q]);
            int cur = takeA ? p : 5 + q;
            List<Animation> hi = new ArrayList<>();
            if (pOk) paint(hi, node[p], HOT_FILL, HOT_STROKE, d(0.3 * sp));
            if (qOk) paint(hi, node[5 + q], HOT_FILL, HOT_STROKE, d(0.3 * sp));
            playList(hi);
            pause(0.4 * sp);

            List<Animation> link = new ArrayList<>();
            List<MObject> dead = new ArrayList<>();
            if (last == -1) {
                link.add(new FadeIn(head, d(0.4 * sp)));
            } else if (outTo[last] == cur) {
                link.add(new ColorChange(out[last], Colors.GREEN, d(0.5 * sp), ColorChange.Target.STROKE));
            } else {
                if (out[last] != null) {
                    link.add(new FadeOut(out[last], d(0.5 * sp)));
                    dead.add(out[last]);
                }
                ArrowMob na = edgeArrow(node[last], node[cur], Colors.GREEN, 12);
                out[last] = na;
                outTo[last] = cur;
                part.add(na);
                link.add(new DrawArrow(na, d(0.8 * sp)));
            }
            for (int c = 0; c < 9; c++) {
                if (c == p && pOk || c == 5 + q && qOk) paint(link, node[c], node[c].baseFill, node[c].baseStroke, d(0.4 * sp));
            }
            last = cur;
            if (takeA) p++; else q++;
            if (takeA) {
                if (p < A.length) pp.go(link, nx0 + px * p, d(0.5 * sp));
                else for (MObject m : pp.parts()) link.add(new FadeOut(m, d(0.4 * sp)));
            } else {
                if (q < B.length) pq.go(link, nx0 + px * q, d(0.5 * sp));
                else for (MObject m : pq.parts()) link.add(new FadeOut(m, d(0.4 * sp)));
            }
            playList(link);
            for (MObject m : dead) { remove(m); part.remove(m); }
        }
        pause(1.0);

        // gather the chain into one row
        int[] chain = {0, 5, 6, 1, 7, 2, 8, 3, 4};
        List<MObject> arrowsNow = new ArrayList<>();
        for (ArrowMob ar : out) if (ar != null && ar.getOpacity() > 0) arrowsNow.add(ar);
        List<Animation> pack = new ArrayList<>();
        for (MObject m : arrowsNow) pack.add(new FadeOut(m, d(0.5)));
        pack.add(new FadeOut(s4, d(0.5)));
        pack.add(new FadeOut(lA, d(0.5)));
        pack.add(new FadeOut(lB, d(0.5)));
        playList(pack);
        for (MObject m : arrowsNow) { remove(m); part.remove(m); }
        for (MObject m : new MObject[]{s4, lA, lB}) { remove(m); part.remove(m); }
        double rowX0 = -640, rowY = -50;
        List<Animation> gather = new ArrayList<>();
        for (int m = 0; m < 9; m++) {
            Cell nd = node[chain[m]];
            travel(gather, nd, rowX0 + 160 * m, rowY, chain[m] < 5 ? -40 : 40, d(1.3));
        }
        gather.add(new MoveTo(head, rowX0 - 46, rowY + 50, d(1.3)).setEasing(Easing.EASE_IN_OUT));
        playList(gather);
        List<Animation> rewire = new ArrayList<>();
        for (int m = 0; m < 8; m++) {
            ArrowMob na = edgeArrow(node[chain[m]], node[chain[m + 1]], Colors.GREEN, 12);
            part.add(na);
            rewire.add(new DrawArrow(na, d(0.8)));
        }
        playList(rewire);
        pause(0.5);
        StrokeTextMob s5 = stroke("Only next pointers changed — nothing was copied.", 0, 150, 26, Colors.ORANGE, false);
        play(new Write(s5, d(2.2)));
        LaTeXMob lt2 = latex("\\mathbf{time:\\;O(N_1+N_2)\\qquad extra\\;space:\\;O(1)}", 32, 0, 235);
        play(new Write(lt2, d(2.0)));
        part.add(s5);
        part.add(lt2);
        pause(2.6);
        fadeOutAll(d(0.9), part);
        fadeOutAll(d(0.6), hdr);
    }

    // ═════════════════════════════════════════════════════════════════
    //  Question 2 — histogram
    // ═════════════════════════════════════════════════════════════════

    private static Color binColor(int b) {
        Color[] stops = {Colors.BLUE, Colors.TEAL, Colors.GREEN, Colors.GOLD, Colors.ORANGE, Colors.RED};
        double t = b / 9.0 * (stops.length - 1);
        int i = Math.min((int) t, stops.length - 2);
        return Colors.interpolate(stops[i], stops[i + 1], t - i);
    }

    private void histogramQuestion() {
        List<MObject> all = new ArrayList<>(header(2, "Create a histogram", Colors.ORANGE));
        StrokeTextMob sub = stroke("Numbers of students in [0..10), [10, 20), ..., [90, 100]", 0, -382, 22, Colors.LIGHT_GRAY, false);
        play(new Write(sub, d(2.4)));
        all.add(sub);

        int[] data = {42, 67, 85, 91, 58, 73, 66, 8, 49, 100, 77, 62, 18, 88, 35, 71, 54, 96, 27, 79, 83, 68, 45, 57, 74, 92, 61, 33};
        double baseY = 300;
        List<Animation> in = new ArrayList<>();
        LineMob axis = new LineMob(-760, baseY, 760, baseY, Colors.withAlpha(Colors.WHITE, 0.55), 3);
        add(axis);
        in.add(new FadeIn(axis, d(0.6)));
        all.add(axis);
        double[] bx = new double[10];
        for (int b = 0; b < 10; b++) {
            bx[b] = -675 + 150 * b;
            String t = b < 9 ? "[" + (10 * b) + "," + (10 * b + 10) + ")" : "[90,100]";
            TextMob bl = label(t, bx[b], baseY + 32, 21, Colors.LIGHT_GRAY, false, false);
            in.add(new FadeIn(bl, d(0.6)));
            all.add(bl);
        }
        CircleMob[] dot = new CircleMob[data.length];
        TextMob[] dt = new TextMob[data.length];
        double[] dx = new double[data.length], dy = new double[data.length];
        for (int k = 0; k < data.length; k++) {
            dx[k] = -390 + 60 * (k % 14);
            dy[k] = -318 + 58 * (k / 14);
            dot[k] = new CircleMob(21);
            dot[k].setFillColor(Colors.withAlpha(Colors.WHITE, 0.12));
            dot[k].setStrokeColor(Colors.withAlpha(Colors.WHITE, 0.6));
            dot[k].setStrokeWidth(2);
            dot[k].setPosition(dx[k], dy[k]);
            dot[k].setOpacity(0);
            add(dot[k]);
            dt[k] = label(String.valueOf(data[k]), dx[k], dy[k], 17, Colors.WHITE, false, false);
            in.add(new FadeInAt(dot[k], 0.03 * k, d(0.5)));
            in.add(new FadeInAt(dt[k], 0.03 * k, d(0.5)));
            all.add(dot[k]);
            all.add(dt[k]);
        }
        playList(in);
        pause(0.6);

        LaTeXMob formula = latex("\\mathbf{bin = \\lfloor mark / 10 \\rfloor}", 36, 0, -205);
        play(new Write(formula, d(1.8)));
        all.add(formula);
        TextMob rd = label("", 0, -140, 36, Colors.WHITE, false, true);
        all.add(rd);
        RectMob scan = new RectMob(138, 336).setCornerRadius(8);
        scan.setFillColor(Colors.withAlpha(Colors.WHITE, 0.06));
        scan.setStrokeColor(Colors.withAlpha(Colors.WHITE, 0.25));
        scan.setPosition(bx[0], 170);
        scan.setOpacity(0);
        add(scan);
        all.add(scan);
        CircleMob ring = new CircleMob(28);
        ring.setFillColor(Color.TRANSPARENT);
        ring.setStrokeColor(Colors.WHITE);
        ring.setStrokeWidth(3.5);
        ring.setOpacity(0);
        add(ring);
        all.add(ring);
        pause(0.5);

        int[] cnt = new int[10];
        boolean first = true;
        for (int k = 0; k < data.length; k++) {
            int v = data[k];
            int bn = Math.min(v / 10, 9);
            double sp = k < 3 ? 1.0 : (k < 9 ? 0.55 : (k == 9 ? 1.0 : 0.36));
            ring.setPosition(dx[k], dy[k]);
            if (v == 100) {
                rd.setText("100 / 10 = 10   →   [90, 100] is closed, so bin 9");
                rd.setFillColor(Colors.ORANGE);
            } else {
                rd.setText(v + " / 10 = " + (v / 10) + "   →   bin " + bn);
                rd.setFillColor(Colors.WHITE);
            }
            List<Animation> look = new ArrayList<>();
            look.add(new FadeIn(ring, d(0.25 * sp)));
            if (first) {
                look.add(new FadeIn(rd, d(0.4)));
                look.add(new FadeIn(scan, d(0.4)));
                first = false;
            }
            look.add(new MoveTo(scan, bx[bn], 170, d(0.35 * sp)).setEasing(Easing.EASE_IN_OUT));
            playList(look);
            pause(v == 100 ? 1.8 : 0.3 * sp);

            double slotY = baseY - 24 - 44 * cnt[bn];
            Color bc = binColor(bn);
            List<Animation> fl = new ArrayList<>();
            fl.add(new ArcMove(dot[k], bx[bn], slotY, 70, d(0.75 * sp)));
            fl.add(new ArcMove(dt[k], bx[bn], slotY, 70, d(0.75 * sp)));
            fl.add(new ColorChange(dot[k], Colors.withAlpha(bc, 0.55), d(0.5 * sp)));
            fl.add(new ColorChange(dot[k], bc, d(0.5 * sp), ColorChange.Target.STROKE));
            fl.add(new FadeOut(ring, d(0.3 * sp)));
            playList(fl);
            cnt[bn]++;
        }
        pause(0.5);
        play(new FadeOut(scan, d(0.5)), new FadeOut(rd, d(0.5)), new FadeOut(formula, d(0.5)));

        // dots turn into bars
        List<Animation> bars = new ArrayList<>();
        for (int b = 0; b < 10; b++) {
            Color bc = binColor(b);
            double topY = cnt[b] > 0 ? baseY - 24 - 44 * (cnt[b] - 1) - 46 : baseY - 34;
            TextMob cl = label(String.valueOf(cnt[b]), bx[b], topY, 32, bc, false, true);
            bars.add(new FadeInAt(cl, 0.5, d(0.6)));
            all.add(cl);
            if (cnt[b] > 0) {
                RectMob bar = new RectMob(124, 10).setCornerRadius(4);
                bar.setFillColor(Colors.withAlpha(bc, 0.26));
                bar.setStrokeColor(Colors.withAlpha(bc, 0.9));
                bar.setStrokeWidth(2.5);
                bar.setPosition(bx[b], baseY - 5);
                bar.setOpacity(0);
                add(bar);
                all.add(bar);
                bars.add(new GrowRect(bar, bx[b], baseY, 124, 44 * cnt[b], true, d(1.0)));
            }
        }
        playList(bars);
        pause(0.8);
        StrokeTextMob s1 = stroke("One pass: each mark goes straight to its bin.", 0, 392, 26, Colors.ORANGE, false);
        play(new Write(s1, d(2.0)));
        LaTeXMob lt = latex("\\mathbf{time:\\;O(N)\\qquad extra\\;space:\\;O(1)\\;\\;(just\\;10\\;counters)}", 30, 0, 450);
        play(new Write(lt, d(2.0)));
        all.add(s1);
        all.add(lt);
        pause(2.8);
        fadeOutAll(d(0.9), all);
    }

    // ═════════════════════════════════════════════════════════════════
    //  Question 3 — product matrix
    // ═════════════════════════════════════════════════════════════════

    private void productQuestion() {
        List<MObject> all = new ArrayList<>(header(3, "Product matrix: P[i][j] = A[i] × B[j]", Colors.BLUE));
        StrokeTextMob sub = stroke("A has N1 = 4 numbers, B has N2 = 5 numbers", 0, -378, 22, Colors.LIGHT_GRAY, false);
        play(new Write(sub, d(2.0)));
        all.add(sub);

        int[] A = {2, 3, 5, 7}, B = {1, 4, 6, 8, 9};
        double pw = 102, ph = 66, mx0 = -404, my0 = -150;
        Cell[] ac = new Cell[4], bc = new Cell[5];
        Cell[][] pc = new Cell[4][5];
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ac[i] = cell(String.valueOf(A[i]), mx0 - pw, my0 + ph * i, 96, 60, 26, Colors.BLUE, false);
            show(in, ac[i], d(0.6));
            cells(all, ac[i]);
        }
        for (int j = 0; j < 5; j++) {
            bc[j] = cell(String.valueOf(B[j]), mx0 + pw * j, my0 - ph - 6, 96, 60, 26, Colors.PINK, false);
            show(in, bc[j], d(0.6));
            cells(all, bc[j]);
        }
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 5; j++) {
                RectMob slot = new RectMob(90, 54).setCornerRadius(6);
                slot.setFillColor(GHOST_FILL);
                slot.setStrokeColor(GHOST_STROKE);
                slot.setPosition(mx0 + pw * j, my0 + ph * i);
                slot.setOpacity(0);
                add(slot);
                in.add(new FadeIn(slot, d(0.6)));
                all.add(slot);
                pc[i][j] = cell(String.valueOf(A[i] * B[j]), mx0 + pw * j, my0 + ph * i, 96, 60, 26, Colors.TEAL, false);
                cells(all, pc[i][j]);
            }
        TextMob corner = label("×", mx0 - pw, my0 - ph - 6, 36, Colors.LIGHT_GRAY, false, true);
        in.add(new FadeIn(corner, d(0.6)));
        all.add(corner);
        playList(in);
        pause(0.7);

        // crosshair bands + counters
        double rowW = (mx0 + pw * 4 + 51) - (mx0 - pw - 51);
        RectMob rowBand = new RectMob(rowW, ph - 4).setCornerRadius(8);
        rowBand.setFillColor(Colors.withAlpha(Colors.GOLD, 0.13));
        rowBand.setStrokeColor(Colors.withAlpha(Colors.GOLD, 0.45));
        rowBand.setPosition((mx0 - pw - 51 + mx0 + pw * 4 + 51) / 2, my0);
        rowBand.setOpacity(0);
        add(rowBand);
        double colTop = my0 - ph - 6 - 33, colBot = my0 + ph * 3 + 33;
        RectMob colBand = new RectMob(pw - 6, colBot - colTop).setCornerRadius(8);
        colBand.setFillColor(Colors.withAlpha(Colors.GOLD, 0.13));
        colBand.setStrokeColor(Colors.withAlpha(Colors.GOLD, 0.45));
        colBand.setPosition(mx0, (colTop + colBot) / 2);
        colBand.setOpacity(0);
        add(colBand);
        all.add(rowBand);
        all.add(colBand);

        TextMob tExpr = label("", 190, -150, 30, Colors.WHITE, true, true);
        TextMob tCount = label("writes: 0", 190, -50, 56, Colors.GOLD, true, true);
        double barL = 190, barW = 460;
        RectMob barBg = new RectMob(barW, 22).setCornerRadius(5);
        barBg.setFillColor(GHOST_FILL);
        barBg.setStrokeColor(GHOST_STROKE);
        barBg.setPosition(barL + barW / 2, 40);
        barBg.setOpacity(0);
        add(barBg);
        RectMob barFill = new RectMob(0.01, 22).setCornerRadius(5);
        barFill.setFillColor(Colors.withAlpha(Colors.TEAL, 0.8));
        barFill.setStrokeColor(Color.TRANSPARENT);
        barFill.setPosition(barL, 40);
        barFill.setOpacity(0);
        add(barFill);
        double markX = barL + barW * 9 / 20.0;
        LineMob mark = new LineMob(markX, 14, markX, 66, Colors.WHITE, 3);
        add(mark);
        TextMob markLab = label("N1 + N2 = 9", markX, 90, 22, Colors.LIGHT_GRAY, false, false);
        TextMob endLab = label("N1 × N2 = 20", barL + barW, 90, 22, Colors.LIGHT_GRAY, false, false);
        all.add(tExpr);
        all.add(tCount);
        all.add(barBg);
        all.add(barFill);
        all.add(mark);
        all.add(markLab);
        all.add(endLab);
        play(new FadeIn(rowBand, d(0.4)), new FadeIn(colBand, d(0.4)), new FadeIn(tExpr, d(0.4)),
                new FadeIn(tCount, d(0.4)), new FadeIn(barBg, d(0.4)), new FadeIn(barFill, d(0.4)),
                new DrawLine(mark, d(0.6)), new FadeIn(markLab, d(0.4)), new FadeIn(endLab, d(0.4)));

        int writes = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                double sp = i == 0 ? 1.0 : (i == 1 ? 0.3 : 0.2);
                tExpr.setText("P[" + i + "][" + j + "] = A[" + i + "] × B[" + j + "] = " + A[i] + " × " + B[j] + " = " + (A[i] * B[j]));
                writes++;
                tCount.setText("writes: " + writes);
                double w = barW * writes / 20.0;
                barFill.setSize(w, 22);
                barFill.setPosition(barL + w / 2, 40);
                List<Animation> st = new ArrayList<>();
                st.add(new MoveTo(rowBand, rowBand.getPosition().x(), my0 + ph * i, d(0.3 * sp)).setEasing(Easing.EASE_IN_OUT));
                st.add(new MoveTo(colBand, mx0 + pw * j, colBand.getPosition().y(), d(0.3 * sp)).setEasing(Easing.EASE_IN_OUT));
                pc[i][j].box.setScale(1.35);
                pc[i][j].text.setScale(1.35);
                show(st, pc[i][j], d(0.35 * sp));
                st.add(new ScaleTo(pc[i][j].box, 1.0, d(0.35 * sp)).setEasing(Easing.EASE_OUT));
                st.add(new ScaleTo(pc[i][j].text, 1.0, d(0.35 * sp)).setEasing(Easing.EASE_OUT));
                if (writes == 10) {
                    st.add(new ColorChange(barFill, Colors.withAlpha(Colors.RED, 0.85), d(0.3)));
                    st.add(new ColorChange(tCount, Colors.RED, d(0.3)));
                }
                playList(st);
                if (i == 0) pause(0.25);
            }
        }
        pause(0.8);

        StrokeTextMob s1 = stroke("Just writing the output takes N1 × N2 = 20 steps.", 0, 225, 26, Colors.ORANGE, false);
        play(new Write(s1, d(2.2)));
        StrokeTextMob s2 = stroke("So O(N1 + N2) is impossible — and so is O(N1 log N2).", 0, 285, 26, Colors.WHITE, false);
        play(new Write(s2, d(2.4)));
        LaTeXMob omega = latex("\\mathbf{writes\\;\\geq\\;N_1 \\cdot N_2}", 40, 0, 360);
        play(new Write(omega, d(1.4)));
        pause(2.6);

        // the way out: never build P at all
        List<MObject> gone = new ArrayList<>();
        gone.add(s1);
        gone.add(s2);
        gone.add(omega);
        gone.add(tExpr);
        gone.add(tCount);
        gone.add(barBg);
        gone.add(barFill);
        gone.add(mark);
        gone.add(markLab);
        gone.add(endLab);
        List<Animation> out = new ArrayList<>();
        for (MObject m : gone) out.add(new FadeOut(m, d(0.6)));
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 5; j++) {
                out.add(new FadeOut(pc[i][j].box, d(0.6)));
                out.add(new FadeOut(pc[i][j].text, d(0.6)));
            }
        playList(out);
        for (MObject m : gone) remove(m);
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 5; j++) { remove(pc[i][j].box); remove(pc[i][j].text); }
        all.removeAll(gone);

        StrokeTextMob s3 = stroke("…unless we never build P: keep A and B, and compute any entry on demand.", 0, 225, 26, Colors.ORANGE, false);
        play(new Write(s3, d(2.8)));
        all.add(s3);
        TextMob tQ = label("P[2][3] = A[2] × B[3] = 5 × 8 = 40", 190, -150, 30, Colors.WHITE, true, true);
        all.add(tQ);
        play(new MoveTo(rowBand, rowBand.getPosition().x(), my0 + ph * 2, d(0.7)).setEasing(Easing.EASE_IN_OUT),
                new MoveTo(colBand, mx0 + pw * 3, colBand.getPosition().y(), d(0.7)).setEasing(Easing.EASE_IN_OUT),
                new FadeIn(tQ, d(0.7)));
        pc[2][3].box.setScale(1.35);
        pc[2][3].text.setScale(1.35);
        List<Animation> pop = new ArrayList<>();
        show(pop, pc[2][3], d(0.4));
        pop.add(new ScaleTo(pc[2][3].box, 1.0, d(0.4)).setEasing(Easing.EASE_OUT));
        pop.add(new ScaleTo(pc[2][3].text, 1.0, d(0.4)).setEasing(Easing.EASE_OUT));
        playList(pop);
        all.add(pc[2][3].box);
        all.add(pc[2][3].text);
        pause(1.2);
        StrokeTextMob s4 = stroke("Store only A and B, and every P[i][j] costs O(1).", 0, 285, 26, Colors.WHITE, false);
        play(new Write(s4, d(2.2)));
        LaTeXMob lt = latex("\\mathbf{store:\\;O(N_1+N_2)\\qquad each\\;entry:\\;O(1)}", 32, 0, 360);
        play(new Write(lt, d(2.0)));
        all.add(s4);
        all.add(lt);
        pause(2.8);
        fadeOutAll(d(0.9), all);
    }

    // ═════════════════════════════════════════════════════════════════
    //  Question 4 — smallest absent CS18 roll number
    // ═════════════════════════════════════════════════════════════════

    private static boolean isNum(String s) { return Character.isDigit(s.charAt(0)); }

    private void absentQuestion() {
        List<MObject> all = new ArrayList<>(header(4, "Smallest CS18 roll number absent today", Colors.GOLD));
        StrokeTextMob sub = stroke("Like the lift that skipped 13: which roll number is missing?", 0, -382, 22, Colors.LIGHT_GRAY, false);
        play(new Write(sub, d(2.4)));
        all.add(sub);

        StrokeTextMob rule = stroke("Idea: roll number v belongs in slot v.", 0, 235, 30, Colors.GOLD, false);
        play(new Write(rule, d(2.2)));
        all.add(rule);
        pause(0.8);

        String[][] ex = {
            {"2", "3", "7", "6", "8", "CH…", "10", "15"},
            {"2", "3", "EE…", "6", "8", "1", "CH…", "15"},
            {"1", "1", "EE…", "EE…", "EE…"},
        };
        double[] speeds = {1.0, 0.7, 0.5};
        for (int e = 0; e < 3; e++) runAbsent(ex[e], speeds[e]);

        fadeOutAll(d(0.6), rule);
        StrokeTextMob f1 = stroke("Yes — linear time and constant extra space.", 0, -120, 32, Colors.GOLD, false);
        play(new Write(f1, d(2.2)));
        StrokeTextMob f2 = stroke("The answer is always between 1 and N + 1, so only the slots 1 .. N matter.", 0, -30, 24, Colors.WHITE, false);
        play(new Write(f2, d(2.8)));
        StrokeTextMob f3 = stroke("Each swap puts one roll number in its final slot: at most N swaps, then one scan.", 0, 40, 24, Colors.WHITE, false);
        play(new Write(f3, d(3.0)));
        LaTeXMob lt = latex("\\mathbf{time:\\;O(N)\\qquad extra\\;space:\\;O(1)}", 36, 0, 150);
        play(new Write(lt, d(2.0)));
        all.add(f1);
        all.add(f2);
        all.add(f3);
        all.add(lt);
        pause(3.0);
        fadeOutAll(d(0.9), all);
    }

    private void runAbsent(String[] items, double sp) {
        int n = items.length;
        double pitch = 112, y = -140;
        double xs0 = -(n - 1) * pitch / 2;
        Cell[] cl = new Cell[n];
        String[] arr = items.clone();
        List<MObject> mine = new ArrayList<>();
        List<Animation> in = new ArrayList<>();
        TextMob[] slotLab = new TextMob[n];
        for (int k = 0; k < n; k++) {
            boolean num = isNum(items[k]);
            cl[k] = cell(items[k], xs0 + pitch * k, y, 104, 66, num ? 28 : 22, num ? Colors.GREEN : Colors.PINK, false);
            slotLab[k] = label("slot " + (k + 1), xs0 + pitch * k, y + 56, 20, Colors.GRAY, false, false);
            showAt(in, cl[k], 0.07 * k, d(0.5));
            in.add(new FadeInAt(slotLab[k], 0.07 * k, d(0.5)));
            cells(mine, cl[k]);
            mine.add(slotLab[k]);
        }
        TextMob l1 = label("", 0, 40, 32, Colors.WHITE, false, true);
        TextMob l2 = label("", 0, 92, 24, Colors.LIGHT_GRAY, false, false);
        TextMob ans = label("", 0, 160, 60, Colors.GOLD, false, true);
        mine.add(l1);
        mine.add(l2);
        mine.add(ans);
        Ptr ptr = pointer("i", cl[0].x(), y - 40, true, Colors.ORANGE);
        mine.addAll(ptr.parts());
        RectMob tgt = new RectMob(110, 72).setCornerRadius(8);
        tgt.setFillColor(Color.TRANSPARENT);
        tgt.setStrokeColor(Colors.WHITE);
        tgt.setStrokeWidth(3.5);
        tgt.setOpacity(0);
        add(tgt);
        mine.add(tgt);
        playList(in);
        pause(0.6);
        play(new FadeIn(ptr.arrow, d(0.4)), new FadeIn(ptr.lab, d(0.4)), new FadeIn(l1, d(0.4)), new FadeIn(l2, d(0.4)));

        for (int i = 0; i < n; i++) {
            List<Animation> mv = new ArrayList<>();
            ptr.go(mv, cl[i].x(), d(0.4 * sp));
            playList(mv);
            while (true) {
                String s = arr[i];
                List<Animation> a = new ArrayList<>();
                paint(a, cl[i], HOT_FILL, HOT_STROKE, d(0.25 * sp));
                if (!isNum(s)) {
                    l1.setText(s + " is not a CS18 roll number");
                    l2.setText("skip it");
                    playList(a);
                    pause(0.7 * sp);
                    List<Animation> ig = new ArrayList<>();
                    paint(ig, cl[i], IGNORE_FILL, IGNORE_STROKE, d(0.3 * sp));
                    playList(ig);
                    break;
                }
                int v = Integer.parseInt(s);
                if (v < 1 || v > n) {
                    l1.setText(v + " is outside 1 .. " + n);
                    l2.setText("it can't be the answer — skip it");
                    playList(a);
                    pause(0.8 * sp);
                    List<Animation> ig = new ArrayList<>();
                    paint(ig, cl[i], IGNORE_FILL, IGNORE_STROKE, d(0.3 * sp));
                    ig.add(new ColorChange(cl[i].text, Colors.GRAY, d(0.3 * sp)));
                    playList(ig);
                    break;
                }
                if (v == i + 1) {
                    l1.setText(v + " is already in slot " + v);
                    l2.setText("");
                    playList(a);
                    pause(0.4 * sp);
                    List<Animation> ok = new ArrayList<>();
                    paint(ok, cl[i], FOUND_FILL, FOUND_STROKE, d(0.3 * sp));
                    playList(ok);
                    break;
                }
                int t = v - 1;
                if (isNum(arr[t]) && Integer.parseInt(arr[t]) == v) {
                    l1.setText(v + " is a duplicate");
                    l2.setText("slot " + v + " already holds it — skip");
                    playList(a);
                    pause(0.8 * sp);
                    List<Animation> ig = new ArrayList<>();
                    paint(ig, cl[i], IGNORE_FILL, IGNORE_STROKE, d(0.3 * sp));
                    ig.add(new ColorChange(cl[i].text, Colors.GRAY, d(0.3 * sp)));
                    playList(ig);
                    break;
                }
                l1.setText("a[i] = " + v + " belongs in slot " + v);
                l2.setText("slot " + v + " currently holds " + arr[t] + "   →   swap");
                tgt.setPosition(cl[t].x(), y);
                a.add(new FadeIn(tgt, d(0.3 * sp)));
                playList(a);
                pause(0.5 * sp);

                Cell ci = cl[i], ct = cl[t];
                double xi = ci.x(), xt = ct.x();
                List<Animation> sw = new ArrayList<>();
                travel(sw, ci, xt, y, 78, d(0.85 * sp));
                travel(sw, ct, xi, y, 78, d(0.85 * sp));
                paint(sw, ci, FOUND_FILL, FOUND_STROKE, d(0.6 * sp));
                paint(sw, ct, ct.baseFill, ct.baseStroke, d(0.6 * sp));
                sw.add(new FadeOut(tgt, d(0.5 * sp)));
                playList(sw);
                cl[i] = ct;
                cl[t] = ci;
                String tmp = arr[i];
                arr[i] = arr[t];
                arr[t] = tmp;
                pause(0.2 * sp);
            }
            pause(0.2 * sp);
        }

        // the scan: first slot whose number is wrong
        l1.setText("Scan the slots: the first wrong one is the answer");
        l2.setText("");
        RectMob sc = new RectMob(110, 72).setCornerRadius(8);
        sc.setFillColor(Color.TRANSPARENT);
        sc.setStrokeColor(Colors.WHITE);
        sc.setStrokeWidth(3.5);
        sc.setPosition(cl[0].x(), y);
        sc.setOpacity(0);
        add(sc);
        mine.add(sc);
        play(new FadeOut(ptr.arrow, d(0.3)), new FadeOut(ptr.lab, d(0.3)), new FadeIn(sc, d(0.3)));
        int answer = n + 1;
        for (int k = 0; k < n; k++) {
            if (k > 0) play(new MoveTo(sc, cl[k].x(), y, d(0.32 * sp)).setEasing(Easing.EASE_IN_OUT));
            boolean right = isNum(arr[k]) && Integer.parseInt(arr[k]) == k + 1;
            if (!right) {
                List<Animation> bad = new ArrayList<>();
                bad.add(new ColorChange(sc, Colors.RED, d(0.3), ColorChange.Target.STROKE));
                bad.add(new ColorChange(slotLab[k], Colors.RED, d(0.3)));
                paint(bad, cl[k], Colors.withAlpha(Colors.RED, 0.35), Colors.RED, d(0.3));
                playList(bad);
                answer = k + 1;
                break;
            }
        }
        l1.setText("slot " + answer + " does not hold " + answer);
        ans.setText("smallest absent: " + answer);
        ans.setScale(0.6);
        play(new FadeIn(ans, d(0.5)), new ScaleTo(ans, 1.0, d(0.5)).setEasing(Easing.EASE_OUT));
        pause(1.8);
        fadeOutAll(d(0.7), mine);
    }

    // ═════════════════════════════════════════════════════════════════
    //  Slide 13 — 8-Queens
    // ═════════════════════════════════════════════════════════════════

    /** A square board of translucent squares (hidden until faded in). */
    private final class Board {
        final int n;
        final double cs, cx, cy;
        final RectMob[][] sq;
        final List<MObject> parts = new ArrayList<>();

        Board(int n, double cs, double cx, double cy, boolean coords) {
            this.n = n; this.cs = cs; this.cx = cx; this.cy = cy;
            sq = new RectMob[n][n];
            for (int r = 0; r < n; r++)
                for (int c = 0; c < n; c++) {
                    RectMob s = new RectMob(cs - 3, cs - 3).setCornerRadius(4);
                    s.setFillColor(baseFill(r, c));
                    s.setStrokeColor(Colors.withAlpha(Colors.WHITE, 0.2));
                    s.setStrokeWidth(1.5);
                    s.setPosition(x(c), y(r));
                    s.setOpacity(0);
                    add(s);
                    sq[r][c] = s;
                    parts.add(s);
                }
            if (coords) {
                for (int c = 0; c < n; c++)
                    parts.add(label(String.valueOf((char) ('a' + c)), x(c), y(0) - cs / 2 - 20, 20, Colors.GRAY, false, false));
                for (int r = 0; r < n; r++)
                    parts.add(label(String.valueOf(n - r), x(0) - cs / 2 - 24, y(r), 20, Colors.GRAY, false, false));
            }
        }

        double x(int c) { return cx + (c - (n - 1) / 2.0) * cs; }
        double y(int r) { return cy + (r - (n - 1) / 2.0) * cs; }
        Color baseFill(int r, int c) { return Colors.withAlpha(Colors.WHITE, (r + c) % 2 == 0 ? 0.13 : 0.04); }
    }

    // row (from the top) of the queen standing in each column of the slide's solution: a1 b7 c4 d6 e8 f2 g5 h3
    private static final int[] QROW = {7, 1, 4, 2, 0, 6, 3, 5};

    private StrokeTextMob ban;

    private StrokeTextMob banner(String text, Color color, double y, double writeSecs) {
        if (ban != null) fadeOutAll(d(0.4), ban);
        ban = strokeLeft(text, -880, y, 24, color, false);
        play(new Write(ban, d(writeSecs)));
        return ban;
    }

    private QueenMob queen(Board b, int r, int c, Color fill) {
        QueenMob q = new QueenMob(b.cs * 0.8, fill, INK);
        q.setPosition(b.x(c), b.y(r));
        add(q);
        return q;
    }

    private void drop(QueenMob q, double sp) {
        double x = q.getPosition().x(), y = q.getPosition().y();
        q.setPosition(x, y - 70);
        q.setScale(1.35);
        play(new FadeIn(q, d(0.25 * sp)), new MoveTo(q, x, y, d(0.4 * sp)).setEasing(Easing.EASE_OUT),
                new ScaleTo(q, 1.0, d(0.4 * sp)).setEasing(Easing.EASE_OUT));
    }

    private List<LineMob> beams(Board b, int r, int c, Color col, double alpha, double width) {
        List<LineMob> lines = new ArrayList<>();
        int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};
        for (int[] dd : dirs) {
            int rr = r, cc = c, steps = 0;
            while (rr + dd[0] >= 0 && rr + dd[0] < b.n && cc + dd[1] >= 0 && cc + dd[1] < b.n) {
                rr += dd[0];
                cc += dd[1];
                steps++;
            }
            if (steps == 0) continue;
            LineMob l = new LineMob(b.x(c), b.y(r), b.x(cc), b.y(rr), Colors.withAlpha(col, alpha), width);
            add(l);
            lines.add(l);
        }
        return lines;
    }

    private static boolean attacks(int r1, int c1, int r2, int c2) {
        return r1 == r2 || c1 == c2 || Math.abs(r1 - r2) == Math.abs(c1 - c2);
    }

    private void tintAttacked(List<Animation> into, Board b, List<int[]> qs) {
        for (int r = 0; r < b.n; r++)
            for (int c = 0; c < b.n; c++) {
                boolean att = false;
                for (int[] q : qs) if (!(q[0] == r && q[1] == c) && attacks(q[0], q[1], r, c)) att = true;
                into.add(new ColorChange(b.sq[r][c], att ? Colors.withAlpha(Colors.RED, 0.25) : b.baseFill(r, c), d(0.5)));
            }
    }

    /** A red cross drawn over one square. */
    private List<LineMob> cross(Board b, int r, int c, double sp) {
        double x = b.x(c), y = b.y(r), h = b.cs * 0.26;
        LineMob l1 = new LineMob(x - h, y - h, x + h, y + h, Colors.RED, 6);
        LineMob l2 = new LineMob(x - h, y + h, x + h, y - h, Colors.RED, 6);
        add(l1);
        add(l2);
        play(new DrawLine(l1, d(0.12 * sp)), new DrawLine(l2, d(0.12 * sp)));
        List<LineMob> l = new ArrayList<>();
        l.add(l1);
        l.add(l2);
        return l;
    }

    /** Row-by-row backtracking, animated: place, reject, take back. */
    private boolean dfs(Board b, int r, int[] col, QueenMob[] qm, double sp) {
        if (r == b.n) return true;
        for (int c = 0; c < b.n; c++) {
            boolean safe = true;
            for (int rr = 0; rr < r; rr++)
                if (col[rr] == c || Math.abs(col[rr] - c) == r - rr) { safe = false; break; }
            if (!safe) {
                List<LineMob> x = cross(b, r, c, sp);
                pause(0.1 * sp);
                List<Animation> f = new ArrayList<>();
                for (LineMob l : x) f.add(new FadeOut(l, d(0.14 * sp)));
                playList(f);
                for (LineMob l : x) remove(l);
                continue;
            }
            col[r] = c;
            qm[r] = queen(b, r, c, Colors.GOLD);
            drop(qm[r], sp * 0.8);
            pause(0.08 * sp);
            if (dfs(b, r + 1, col, qm, sp)) return true;
            play(new FadeOut(qm[r], d(0.2 * sp)), new ScaleTo(qm[r], 0.5, d(0.2 * sp)));
            remove(qm[r]);
        }
        return false;
    }

    void queensSection() {
        setTitle("8-Queens Problem");
        pause(0.4);
        List<MObject> all = new ArrayList<>();
        Board bd = new Board(8, 78, 560, 20, true);
        all.addAll(bd.parts);

        // heading is up; question and sub-questions fade in with a small latency
        String[] qLines = {"Given a chess-board,", "can you place 8 queens", "in non-attacking positions?",
                "(no two queens in the same row", "or same column or same diagonal)"};
        double[] qY = {-330, -272, -214, -150, -100};
        double[] qS = {32, 32, 32, 26, 26};
        Color[] qC = {Colors.WHITE, Colors.WHITE, Colors.WHITE, Colors.LIGHT_GRAY, Colors.LIGHT_GRAY};
        List<Animation> intro = new ArrayList<>();
        double delay = 0.2;
        for (MObject p : bd.parts) intro.add(new FadeInAt(p, 0.2, d(1.0)));
        for (int k = 0; k < qLines.length; k++) {
            TextMob t = label(qLines[k], -880, qY[k], qS[k], qC[k], true, false);
            t.setFontFamily("Georgia");
            intro.add(new FadeInAt(t, delay, d(0.8)));
            all.add(t);
            delay += 0.55;
        }
        delay += 0.5;
        TextMob s1 = label("•  Does a solution exist for 2x2, 3x3, 4x4?", -880, 0, 28, Colors.GOLD, true, false);
        s1.setFontFamily("Georgia");
        intro.add(new FadeInAt(s1, delay, d(0.8)));
        delay += 0.6;
        TextMob s2 = label("•  Have you seen similar constraints somewhere?", -880, 70, 28, Colors.GOLD, true, false);
        s2.setFontFamily("Georgia");
        intro.add(new FadeInAt(s2, delay, d(0.8)));
        all.add(s1);
        all.add(s2);
        playList(intro);
        pause(1.4);

        // ── Answer 1: can 8 queens be placed? the rules, then a solution ──
        banner("A queen attacks along its row, column and both diagonals.", Colors.LIGHT_GRAY, 215, 2.6);
        QueenMob q0 = queen(bd, QROW[0], 0, Colors.GOLD);
        drop(q0, 1.0);
        List<LineMob> bm = beams(bd, QROW[0], 0, Colors.RED, 0.6, 5);
        List<Animation> a = new ArrayList<>();
        for (LineMob l : bm) a.add(new DrawLine(l, d(0.8)));
        List<int[]> qs = new ArrayList<>();
        qs.add(new int[]{QROW[0], 0});
        tintAttacked(a, bd, qs);
        playList(a);
        pause(0.9);

        int[][] tries = {{6, 1}, {3, 0}, {7, 3}};   // b2, a5, d1
        String[] why = {"No — same diagonal.", "No — same column.", "No — same row."};
        for (int t = 0; t < tries.length; t++) {
            QueenMob g = queen(bd, tries[t][0], tries[t][1], Colors.withAlpha(Colors.RED, 0.9));
            drop(g, 0.8);
            List<LineMob> x = cross(bd, tries[t][0], tries[t][1], 1.0);
            banner(why[t], Colors.RED, 275, 1.0);
            pause(0.8);
            List<Animation> f = new ArrayList<>();
            f.add(new FadeOut(g, d(0.3)));
            for (LineMob l : x) f.add(new FadeOut(l, d(0.3)));
            playList(f);
            remove(g);
            for (LineMob l : x) remove(l);
        }
        fadeOutAll(d(0.3), ban);
        ban = null;
        QueenMob q1 = queen(bd, QROW[1], 1, Colors.GOLD);
        drop(q1, 1.0);
        List<LineMob> bm2 = beams(bd, QROW[1], 1, Colors.RED, 0.6, 5);
        qs.add(new int[]{QROW[1], 1});
        List<Animation> a2 = new ArrayList<>();
        for (LineMob l : bm2) a2.add(new DrawLine(l, d(0.8)));
        tintAttacked(a2, bd, qs);
        playList(a2);
        banner("Each new queen goes on a square no earlier queen can reach.", Colors.GREEN, 215, 2.6);
        pause(1.2);

        List<Animation> clear = new ArrayList<>();
        for (LineMob l : bm) clear.add(new FadeOut(l, d(0.5)));
        for (LineMob l : bm2) clear.add(new FadeOut(l, d(0.5)));
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) clear.add(new ColorChange(bd.sq[r][c], bd.baseFill(r, c), d(0.5)));
        playList(clear);
        for (LineMob l : bm) remove(l);
        for (LineMob l : bm2) remove(l);

        QueenMob[] queens = new QueenMob[8];
        queens[0] = q0;
        queens[1] = q1;
        for (int c = 2; c < 8; c++) {
            queens[c] = queen(bd, QROW[c], c, Colors.GOLD);
            drop(queens[c], 0.7);
        }
        pause(0.5);
        List<LineMob> allBeams = new ArrayList<>();
        List<Animation> ab = new ArrayList<>();
        for (int c = 0; c < 8; c++)
            for (LineMob l : beams(bd, QROW[c], c, Colors.WHITE, 0.2, 3)) {
                allBeams.add(l);
                ab.add(new DrawLine(l, d(1.0)));
            }
        playList(ab);
        banner("No queen stands on another queen's line — 8 queens fit! (there are 92 solutions)", Colors.GOLD, 215, 3.2);
        pause(1.6);
        List<Animation> ab2 = new ArrayList<>();
        for (LineMob l : allBeams) ab2.add(new FadeOut(l, d(0.6)));
        playList(ab2);
        for (LineMob l : allBeams) remove(l);

        // the same solution, as an array
        double ay = 410;
        Cell[] qa = new Cell[8];
        List<Animation> arr = new ArrayList<>();
        TextMob qName = label("Q =", bd.x(0) - 100, ay, 32, Colors.GOLD, false, true);
        arr.add(new FadeInAt(qName, 0, d(0.5)));
        all.add(qName);
        for (int c = 0; c < 8; c++) {
            qa[c] = cell(String.valueOf(8 - QROW[c]), bd.x(c), ay, 72, 56, 30, Colors.GOLD, false);
            showAt(arr, qa[c], 0.25 * c, d(0.5));
            cells(all, qa[c]);
        }
        playList(arr);
        banner("A solution is just an array: Q[column] = row.", Colors.GOLD, 215, 2.4);
        StrokeTextMob n1 = strokeLeft("All different — so no two queens share a row.", -880, 275, 24, Colors.WHITE, false);
        play(new Write(n1, d(2.4)));
        StrokeTextMob n2 = strokeLeft("Diagonals: the gap in rows must differ from the gap in columns.", -880, 330, 24, Colors.WHITE, false);
        play(new Write(n2, d(2.8)));
        LaTeXMob dl = latex("\\mathbf{|Q[i]-Q[j]| \\neq |i-j|}", 36, -420, 405);
        play(new Write(dl, d(1.8)));
        all.add(n1);
        all.add(n2);
        all.add(dl);
        for (QueenMob q : queens) all.add(q);
        pause(2.6);
        fadeOutAll(d(0.6), n1, n2, dl);
        all.remove(n1);
        all.remove(n2);
        all.remove(dl);

        // ── Answer 2: does a solution exist for 2x2, 3x3, 4x4? ──
        List<MObject> gone = new ArrayList<>(bd.parts);
        for (QueenMob q : queens) gone.add(q);
        gone.add(qName);
        for (Cell c : qa) cells(gone, c);
        play(new ColorChange(s2, Colors.GRAY, d(0.5)));
        fadeOutAll(d(0.8), gone);
        all.removeAll(gone);
        banner("Let's try the small boards — row by row, taking back any dead end.", Colors.LIGHT_GRAY, 215, 3.0);

        double[] sizes = {2, 3, 4};
        double[] bxs = {190, 450, 760};
        double[] sps = {1.0, 0.8, 0.7};
        Board[] small = new Board[3];
        List<Animation> sin = new ArrayList<>();
        for (int k = 0; k < 3; k++) {
            int n = (int) sizes[k];
            small[k] = new Board(n, 66, bxs[k], 20, false);
            for (MObject p : small[k].parts) { sin.add(new FadeIn(p, d(0.6))); all.add(p); }
            TextMob lab = label(n + "x" + n, bxs[k], 20 - n * 33 - 40, 32, Colors.WHITE, false, true);
            sin.add(new FadeIn(lab, d(0.6)));
            all.add(lab);
        }
        playList(sin);
        pause(0.6);
        boolean[] ok = new boolean[3];
        for (int k = 0; k < 3; k++) {
            Board b = small[k];
            int[] col = new int[b.n];
            QueenMob[] qm = new QueenMob[b.n];
            ok[k] = dfs(b, 0, col, qm, sps[k]);
            double ry = 20 + b.n * 33 + 44;
            if (ok[k]) {
                List<Animation> win = new ArrayList<>();
                for (int r = 0; r < b.n; r++) {
                    win.add(new ColorChange(qm[r], Colors.withAlpha(Colors.GREEN, 0.95), d(0.4)));
                    all.add(qm[r]);
                }
                TextMob res = label("a solution!", bxs[k], ry, 30, Colors.GREEN, false, true);
                win.add(new FadeIn(res, d(0.5)));
                all.add(res);
                playList(win);
            } else {
                TextMob res = label("no solution", bxs[k], ry, 30, Colors.RED, false, true);
                List<Animation> lose = new ArrayList<>();
                for (int r = 0; r < b.n; r++)
                    for (int c = 0; c < b.n; c++)
                        lose.add(new ColorChange(b.sq[r][c], Colors.withAlpha(Colors.RED, 0.16), d(0.5)));
                lose.add(new FadeIn(res, d(0.5)));
                all.add(res);
                playList(lose);
            }
            pause(0.9);
        }
        banner("No for 2x2 and 3x3, yes for 4x4 — and for every board from 4x4 up.", Colors.GOLD, 215, 3.2);
        pause(2.2);

        // ── Answer 3: similar constraints — Sudoku ──
        play(new ColorChange(s2, Colors.GOLD, d(0.5)), new ColorChange(s1, Colors.GRAY, d(0.5)));
        List<MObject> gone2 = new ArrayList<>();
        for (Board b : small) gone2.addAll(b.parts);
        for (MObject m : all) {
            if (m instanceof QueenMob || (m instanceof TextMob t && (t.getText().equals("a solution!") || t.getText().equals("no solution")
                    || t.getText().matches("\\dx\\d")))) gone2.add(m);
        }
        fadeOutAll(d(0.8), gone2);
        all.removeAll(gone2);
        banner("Sudoku! One of each digit in every row, every column and every 3x3 box.", Colors.LIGHT_GRAY, 215, 3.4);

        String[] giv = {"53..7....", "6..195...", ".98....6.", "8...6...3", "4..8.3..1", "7...2...6", ".6....28.", "...419..5", "....8..79"};
        double sc = 58, sx = 560, sy = 20;
        List<Animation> sud = new ArrayList<>();
        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++) {
                double x = sx + (c - 4) * sc, y = sy + (r - 4) * sc;
                RectMob s = new RectMob(sc - 3, sc - 3).setCornerRadius(3);
                boolean dark = ((r / 3) + (c / 3)) % 2 == 0;
                s.setFillColor(Colors.withAlpha(Colors.WHITE, dark ? 0.10 : 0.035));
                s.setStrokeColor(Colors.withAlpha(Colors.WHITE, 0.18));
                s.setStrokeWidth(1.2);
                s.setPosition(x, y);
                s.setOpacity(0);
                add(s);
                sud.add(new FadeInAt(s, 0.02 * (r + c), d(0.5)));
                all.add(s);
                char ch = giv[r].charAt(c);
                if (ch != '.') {
                    TextMob dg = label(String.valueOf(ch), x, y, 28, Colors.WHITE, false, true);
                    sud.add(new FadeInAt(dg, 0.02 * (r + c) + 0.25, d(0.5)));
                    all.add(dg);
                }
            }
        for (int k = 0; k <= 3; k++) {
            double lx = sx + (3 * k - 4.5) * sc, ly = sy + (3 * k - 4.5) * sc;
            LineMob v = new LineMob(lx, sy - 4.5 * sc, lx, sy + 4.5 * sc, Colors.withAlpha(Colors.WHITE, 0.65), 4);
            LineMob h = new LineMob(sx - 4.5 * sc, ly, sx + 4.5 * sc, ly, Colors.withAlpha(Colors.WHITE, 0.65), 4);
            add(v);
            add(h);
            sud.add(new FadeInAt(v, 0.4, d(0.8)));
            sud.add(new FadeInAt(h, 0.4, d(0.8)));
            all.add(v);
            all.add(h);
        }
        playList(sud);
        pause(0.8);

        RectMob rowB = new RectMob(9 * sc, sc - 2).setCornerRadius(6);
        rowB.setFillColor(Colors.withAlpha(Colors.GOLD, 0.2));
        rowB.setStrokeColor(Colors.GOLD);
        rowB.setStrokeWidth(2.5);
        rowB.setPosition(sx, sy);
        rowB.setOpacity(0);
        add(rowB);
        RectMob colB = new RectMob(sc - 2, 9 * sc).setCornerRadius(6);
        colB.setFillColor(Colors.withAlpha(Colors.TEAL, 0.2));
        colB.setStrokeColor(Colors.TEAL);
        colB.setStrokeWidth(2.5);
        colB.setPosition(sx, sy);
        colB.setOpacity(0);
        add(colB);
        RectMob boxB = new RectMob(3 * sc - 2, 3 * sc - 2).setCornerRadius(6);
        boxB.setFillColor(Colors.withAlpha(Colors.PINK, 0.2));
        boxB.setStrokeColor(Colors.PINK);
        boxB.setStrokeWidth(2.5);
        boxB.setPosition(sx, sy);
        boxB.setOpacity(0);
        add(boxB);
        all.add(rowB);
        all.add(colB);
        all.add(boxB);
        play(new FadeIn(rowB, d(0.6)));
        pause(1.0);
        play(new FadeOut(rowB, d(0.4)), new FadeIn(colB, d(0.6)));
        pause(1.0);
        play(new FadeOut(colB, d(0.4)), new FadeIn(boxB, d(0.6)));
        pause(1.0);
        play(new FadeOut(boxB, d(0.4)));

        StrokeTextMob c1 = strokeLeft("Queens: no two on a row, column or diagonal.", -880, 275, 24, Colors.WHITE, false);
        play(new Write(c1, d(2.4)));
        StrokeTextMob c2 = strokeLeft("Sudoku: no repeat in a row, column or box.", -880, 330, 24, Colors.WHITE, false);
        play(new Write(c2, d(2.4)));
        all.add(c1);
        all.add(c2);
        pause(1.0);
        StrokeTextMob fin = stroke("Place  ·  check the constraints  ·  backtrack", -420, 440, 34, Colors.WHITE, true);
        play(new Write(fin, d(2.6)));
        all.add(fin);
        pause(3.0);

        all.add(title);
        if (ban != null) all.add(ban);
        fadeOutAll(1.5, all);
        pause(0.5);
    }
}
