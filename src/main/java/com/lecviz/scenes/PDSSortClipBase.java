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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Shared kit for the sorting-algorithm clips (bubble sort, insertion sort):
 *
 *   - slide text in the pen-stroke style, line by line, with bullets, highlighter
 *     sweeps on key words and a gray "tail" for the slide's small print
 *   - the swipe-off-to-the-right exit and the drop-in-from-above entrance
 *   - code listings that type in, with syntax colors and a highlight band that
 *     follows the line being executed, plus callout boxes with leader lines
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public abstract class PDSSortClipBase extends Scene {

    // ── pacing / palette ─────────────────────────────────────────────

    protected static final double PACE = 1.15;
    protected double d(double seconds) { return seconds * PACE; }
    protected void pause(double seconds) { hold(seconds * PACE); }

    protected static final Color INK = Color.web("#08080D");
    protected static final Color GHOST_FILL = Colors.withAlpha(Colors.WHITE, 0.04);
    protected static final Color GHOST_STROKE = Colors.withAlpha(Colors.WHITE, 0.16);
    protected static final Color FINAL_FILL = Colors.withAlpha(Colors.GREEN, 0.55);
    protected static final Color FINAL_STROKE = Colors.GREEN;
    protected static final Color KEYWORD = Colors.LIGHT_BLUE;
    private static final Color[] BULLET = {Colors.GOLD, Colors.BLUE, Colors.TEAL, Colors.ORANGE};

    // ── small animations & mobjects ──────────────────────────────────

    /** Slides an object off to the right while it fades, after an optional delay. */
    protected static final class SwipeOut extends Animation {
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
    protected static final class DropIn extends Animation {
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

    /** A fade-in that starts after a delay, so several can stagger inside one play(). */
    protected static final class FadeInAt extends Animation {
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

    /** Types a TextMob out character by character, after an optional delay. */
    protected static final class TypeAt extends Animation {
        private final double delay, span;

        TypeAt(TextMob t, double delay, double dur) {
            super(t, delay + dur, Easing.LINEAR);
            this.delay = delay;
            this.span = dur;
        }

        @Override
        public void begin() {
            ((TextMob) target).setVisibleFraction(0);
            target.setOpacity(1);
        }

        @Override
        public void interpolate(double t) {
            // t == 1 must give exactly 1: rounding in (t * duration - delay) / span can land a hair
            // below it, and TextMob would then drop the last character
            double p = t >= 1.0 ? 1.0 : Math.max(0, Math.min(1, (t * duration - delay) / span));
            ((TextMob) target).setVisibleFraction(p);
        }
    }

    /** Grows a rectangle from its left edge — the highlighter sweep. */
    protected static final class GrowRight extends Animation {
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

    /** Moves along a curved path (bulge > 0 arcs "up" relative to the direction of travel). */
    protected static final class ArcMove extends Animation {
        private final double ex, ey, bulge;
        private double sx, sy;

        ArcMove(MObject target, double ex, double ey, double bulge, double dur) {
            super(target, dur, Easing.EASE_IN_OUT);
            this.ex = ex;
            this.ey = ey;
            this.bulge = bulge;
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

    /** Resizes a rectangle horizontally while moving its center. */
    protected static final class ResizeX extends Animation {
        private final RectMob r;
        private final double tw, tcx;
        private double sw, scx;

        ResizeX(RectMob r, double tw, double tcx, double dur) {
            super(r, dur, Easing.EASE_IN_OUT);
            this.r = r;
            this.tw = tw;
            this.tcx = tcx;
        }

        @Override
        public void begin() {
            sw = r.getWidth();
            scx = r.getPosition().x();
        }

        @Override
        public void interpolate(double t) {
            r.setSize(sw + (tw - sw) * t, r.getHeight());
            r.setPosition(scx + (tcx - scx) * t, r.getPosition().y());
        }
    }

    /** A straight segment that can be drawn on progressively. */
    protected static final class LineMob extends MObject {
        private final double x1, y1, x2, y2;
        private double fraction = 1.0;

        LineMob(double x1, double y1, double x2, double y2, Color color, double width) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
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

    protected static final class DrawLine extends Animation {
        DrawLine(LineMob l, double dur) { super(l, dur, Easing.EASE_OUT); }

        @Override public void begin() { ((LineMob) target).setFraction(0); target.setOpacity(1); }
        @Override public void interpolate(double t) { ((LineMob) target).setFraction(t); }
    }

    // ── measuring & building text ────────────────────────────────────

    protected static double strokeW(String text, boolean bold, double size) {
        java.awt.Font f = new java.awt.Font("Georgia", bold ? java.awt.Font.BOLD : java.awt.Font.PLAIN, 1)
                .deriveFont(200f);
        GlyphVector gv = f.createGlyphVector(new FontRenderContext(new AffineTransform(), true, true), text);
        return gv.getLogicalBounds().getWidth() * size / 200.0;
    }

    protected static double measure(String s, String family, double size, boolean bold) {
        javafx.scene.text.Text t = new javafx.scene.text.Text(s);
        t.setFont(Font.font(family, bold ? FontWeight.BOLD : FontWeight.NORMAL, size));
        return t.getLayoutBounds().getWidth();
    }

    protected StrokeTextMob stroke(String text, double x, double y, double size, Color color, boolean bold) {
        StrokeTextMob t = new StrokeTextMob(text, "Georgia", bold, size)
                .setFillColor(color).setStrokeColor(color);
        t.setPosition(x, y);
        t.setOpacity(0);
        add(t);
        return t;
    }

    protected StrokeTextMob strokeLeft(String text, double left, double y, double size, Color color) {
        return stroke(text, left + strokeW(text, false, size) / 2, y, size, color, false);
    }

    protected TextMob label(String text, double x, double y, double size, Color color, boolean left, boolean bold) {
        TextMob t = new TextMob(text).setFontSize(size).setFillColor(color);
        if (left) t.setAlignment(TextAlignment.LEFT);
        if (bold) t.setBold();
        t.setPosition(x, y);
        t.setOpacity(0);
        add(t);
        return t;
    }

    protected void playAll(List<Animation> anims) {
        if (!anims.isEmpty()) play(anims.toArray(new Animation[0]));
    }

    protected void fadeOutAll(double dur, List<? extends MObject> objs) {
        List<Animation> anims = new ArrayList<>();
        for (MObject o : objs) if (o.getOpacity() > 0) anims.add(new FadeOut(o, dur));
        playAll(anims);
        for (MObject o : objs) remove(o);
    }

    protected void fadeOutAll(double dur, MObject... objs) {
        List<MObject> l = new ArrayList<>();
        for (MObject o : objs) l.add(o);
        fadeOutAll(dur, l);
    }

    // ── slide text, line by line ─────────────────────────────────────

    /**
     * One line of a slide: level 0 = bullet, 1 = sub-bullet, 2 = continuation of a sub-bullet,
     * 3 = continuation of a bullet.
     */
    protected static final class Spec {
        final int level;
        final String text;
        String tail;
        double gap = 12;
        final List<String> kw = new ArrayList<>();
        final List<Color> kwColor = new ArrayList<>();

        Spec(int level, String text) {
            this.level = level;
            this.text = text;
        }

        /** Small gray print the slide sets after the main text. */
        Spec tail(String t) { this.tail = t; return this; }

        /** Space between the main text and its tail. */
        Spec gap(double g) { this.gap = g; return this; }

        /** A word the slide emphasizes: a highlighter sweeps under it once the line is written. */
        Spec kw(String word, Color color) { kw.add(word); kwColor.add(color); return this; }
    }

    protected static Spec ln(int level, String text) { return new Spec(level, text); }

    /** The slide heading, written in the pen-stroke style. */
    protected StrokeTextMob writeHeading(String text) {
        StrokeTextMob h = stroke(text, 0, -470, 54, Colors.WHITE, true);
        play(new Write(h, d(1.6)));
        return h;
    }

    /** The slide heading, fading in while it slides down from above. */
    protected StrokeTextMob dropHeading(String text) {
        StrokeTextMob h = stroke(text, 0, -470, 54, Colors.WHITE, true);
        play(new DropIn(h, 110, 0, d(1.2)));
        return h;
    }

    /**
     * Writes slide text one line at a time — outline first, then the fill running left to
     * right — each with its bullet. Returns the objects grouped per line, top to bottom.
     */
    protected List<List<MObject>> writeSlide(List<Spec> specs, double startY) {
        List<List<MObject>> groups = new ArrayList<>();
        double y = startY;
        int mainIdx = -1;
        for (int k = 0; k < specs.size(); k++) {
            Spec sp = specs.get(k);
            boolean mainSize = sp.level == 0 || sp.level == 3;
            if (k > 0) y += sp.level == 0 ? 78 : (sp.level == 1 ? 60 : (sp.level == 2 ? 48 : 54));
            double size = mainSize ? 40 : 31;
            double left = mainSize ? -800 : -715;
            Color textColor = mainSize ? Colors.WHITE : Colors.LIGHT_GRAY;
            List<MObject> group = new ArrayList<>();
            List<Animation> start = new ArrayList<>();

            if (sp.level == 0) {
                mainIdx++;
                CircleMob dot = new CircleMob(9);
                dot.setFillColor(BULLET[mainIdx % BULLET.length]);
                dot.setStrokeColor(Color.TRANSPARENT);
                dot.setPosition(-835, y);
                dot.setOpacity(0);
                dot.setScale(0.2);
                add(dot);
                start.add(new FadeIn(dot, d(0.35)));
                start.add(new ScaleTo(dot, 1.0, d(0.45)).setEasing(Easing.EASE_OUT));
                group.add(dot);
            } else if (sp.level == 1) {
                RectMob dash = new RectMob(24, 4).setCornerRadius(2);
                dash.setFillColor(Colors.GRAY);
                dash.setStrokeColor(Color.TRANSPARENT);
                dash.setPosition(-755, y);
                dash.setOpacity(0);
                add(dash);
                start.add(new FadeIn(dash, d(0.35)));
                group.add(dash);
            }

            StrokeTextMob t = strokeLeft(sp.text, left, y, size, textColor);
            double secs = Math.max(1.2, sp.text.length() * (mainSize ? 0.06 : 0.045));
            start.add(new Write(t, d(secs)));
            group.add(t);
            playAll(start);

            if (sp.tail != null) {
                double tailSize = size * 0.85;
                StrokeTextMob tt = strokeLeft(sp.tail, left + strokeW(sp.text, false, size) + sp.gap, y, tailSize, Colors.GRAY);
                play(new Write(tt, d(Math.max(0.9, sp.tail.length() * 0.045))));
                group.add(tt);
            }

            for (int w = 0; w < sp.kw.size(); w++) {
                int idx = sp.text.indexOf(sp.kw.get(w));
                double pre = strokeW(sp.text.substring(0, idx), false, size);
                double kwW = strokeW(sp.text.substring(0, idx + sp.kw.get(w).length()), false, size) - pre;
                RectMob band = new RectMob(kwW + 14, size * 1.3).setCornerRadius(8);
                band.setFillColor(Colors.withAlpha(sp.kwColor.get(w), 0.34));
                band.setStrokeColor(Color.TRANSPARENT);
                band.setOpacity(0);
                band.setPosition(left + pre + kwW / 2, y);
                add(band);
                play(new GrowRight(band, left + pre - 7, y, kwW + 14, size * 1.3, d(0.6)));
                group.add(band);
            }
            pause(sp.level == 0 ? 0.45 : 0.3);
            groups.add(group);
        }
        return groups;
    }

    /** Everything on a slide swipes off to the right in a top-to-bottom cascade, fading as it goes. */
    protected void swipeAway(List<List<MObject>> groups) {
        List<Animation> out = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++)
            for (MObject m : groups.get(i))
                if (m.getOpacity() > 0) out.add(new SwipeOut(m, 900, 0.1 * i * PACE, d(1.0)));
        playAll(out);
        for (List<MObject> g : groups)
            for (MObject m : g) remove(m);
    }

    /**
     * A table whose header and rows fade in sliding down from above, one row after another.
     * {@code tint[r][c]} colors body cell (r, c), or null for a neutral cell; column 0 is left-aligned.
     */
    protected List<MObject> dropTable(String[] hdr, String[][] rows, double[] colW, Color[][] tint, double headY) {
        List<MObject> made = new ArrayList<>();
        List<Animation> drop = new ArrayList<>();
        double gap = 6;
        double total = gap * (colW.length - 1);
        for (double w : colW) total += w;
        double[] cx = new double[colW.length];
        double x = -total / 2;
        for (int c = 0; c < colW.length; c++) {
            cx[c] = x + colW[c] / 2;
            x += colW[c] + gap;
        }
        for (int r = 0; r <= rows.length; r++) {
            double y = headY + 68 * r;
            double start = d(0.7) * r;
            for (int c = 0; c < colW.length; c++) {
                boolean header = r == 0;
                Color base = header ? Colors.BLUE : (tint[r - 1][c] == null ? Colors.WHITE : tint[r - 1][c]);
                boolean neutral = !header && tint[r - 1][c] == null;
                RectMob box = new RectMob(colW[c], 62).setCornerRadius(8);
                box.setFillColor(Colors.withAlpha(base, header ? 0.38 : (neutral ? 0.07 : 0.2)));
                box.setStrokeColor(Colors.withAlpha(base, header ? 1.0 : (neutral ? 0.35 : 0.7)));
                box.setPosition(cx[c], y);
                box.setOpacity(0);
                add(box);
                String text = header ? hdr[c] : rows[r - 1][c];
                TextMob t = (c == 0)
                        ? label(text, cx[c] - colW[c] / 2 + 24, y, 28, Colors.WHITE, true, header)
                        : label(text, cx[c], y, 32, Colors.WHITE, false, true);
                drop.add(new DropIn(box, 70, start, d(0.9)));
                drop.add(new DropIn(t, 70, start, d(0.9)));
                made.add(box);
                made.add(t);
            }
        }
        playAll(drop);
        return made;
    }

    // ── code listings ────────────────────────────────────────────────

    private static final Pattern TOKEN = Pattern.compile(
            "\\b(for|if|while|int|void|swap|key|iimin|iipivot|quick|partition|mergeSort|merge|hide_back|deleteMax|printArray)\\b");
    private static final java.util.Set<String> KEYWORDS = java.util.Set.of("for", "if", "while", "int", "void");
    private static final java.util.Set<String> VARIABLES = java.util.Set.of("key", "iimin", "iipivot");

    /** A code listing on a translucent card: types in, syntax-colored, with a highlight band for the executing line. */
    protected final class CodeBox {
        final RectMob card;
        final RectMob hl;
        final List<TextMob> runs = new ArrayList<>();
        final List<Integer> runLine = new ArrayList<>();
        final List<Integer> runStart = new ArrayList<>();
        final List<Integer> runLen = new ArrayList<>();
        final String[] src;
        final double pitch, charW, width, size;
        double left, top;

        CodeBox(String[] src, double left, double top, double size, double pitch) {
            this.src = src;
            this.left = left;
            this.top = top;
            this.pitch = pitch;
            this.size = size;
            this.charW = measure("MMMMMMMMMM", "Menlo", size, false) / 10.0;
            int maxLen = 0;
            for (String s : src) maxLen = Math.max(maxLen, s.length());
            this.width = maxLen * charW + 70;
            double height = src.length * pitch + 30;

            card = new RectMob(width, height).setCornerRadius(14);
            card.setFillColor(Colors.withAlpha(Colors.ORANGE, 0.13));
            card.setStrokeColor(Colors.withAlpha(Colors.ORANGE, 0.55));
            card.setStrokeWidth(2);
            card.setPosition(left + width / 2, top + (src.length - 1) * pitch / 2.0);
            card.setOpacity(0);
            add(card);

            hl = new RectMob(width - 20, pitch - 2).setCornerRadius(8);
            hl.setFillColor(Colors.withAlpha(Colors.GOLD, 0.2));
            hl.setStrokeColor(Colors.withAlpha(Colors.GOLD, 0.75));
            hl.setStrokeWidth(2);
            hl.setPosition(left + width / 2, top);
            hl.setOpacity(0);
            add(hl);

            for (int i = 0; i < src.length; i++) buildLine(i);
        }

        /** Splits one source line into syntax-colored runs. */
        private void buildLine(int i) {
            String line = src[i];
            int pos = 0;
            Matcher m = TOKEN.matcher(line);
            List<int[]> cuts = new ArrayList<>();
            while (m.find()) cuts.add(new int[]{m.start(), m.end()});
            for (int[] c : cuts) {
                if (c[0] > pos) addRun(i, line, pos, c[0], size, Colors.WHITE, false);
                String word = line.substring(c[0], c[1]);
                boolean fn = !KEYWORDS.contains(word) && !VARIABLES.contains(word);
                Color col = fn ? Colors.GOLD : VARIABLES.contains(word) ? Colors.RED : KEYWORD;
                addRun(i, line, c[0], c[1], size, col, fn);
                pos = c[1];
            }
            if (pos < line.length()) addRun(i, line, pos, line.length(), size, Colors.WHITE, false);
        }

        /** A strike-through line across one token of a source line, drawn on later with DrawLine. */
        LineMob strike(int line, String token) {
            int idx = src[line].indexOf(token);
            double x1 = left + 35 + idx * charW - 3, x2 = left + 35 + (idx + token.length()) * charW + 3;
            LineMob l = new LineMob(x1, lineY(line) + 1, x2, lineY(line) + 1, Colors.withAlpha(Colors.LIGHT_GRAY, 0.95), 3);
            add(l);
            return l;
        }

        /** The line's old pieces fade out while the new text fades in over it. Returns the new objects. */
        List<MObject> rewrite(List<Animation> into, int line, String newText, double dur) {
            List<MObject> fresh = new ArrayList<>();
            for (int r = runLine.size() - 1; r >= 0; r--) {
                if (runLine.get(r) == line) {
                    into.add(new FadeOut(runs.get(r), dur));
                    runs.remove(r);
                    runLine.remove(r);
                    runStart.remove(r);
                    runLen.remove(r);
                }
            }
            src[line] = newText;
            int before = runs.size();
            buildLine(line);
            for (int r = before; r < runs.size(); r++) {
                into.add(new FadeIn(runs.get(r), dur));
                fresh.add(runs.get(r));
            }
            return fresh;
        }

        private void addRun(int lineIdx, String line, int from, int to, double size, Color color, boolean bold) {
            String piece = line.substring(from, to);
            if (piece.trim().isEmpty()) return;
            TextMob t = new TextMob(piece).setFontSize(size).setFillColor(color);
            t.setFontFamily("Menlo");
            t.setAlignment(TextAlignment.LEFT);
            if (bold) t.setBold();
            t.setPosition(left + 35 + from * charW, lineY(lineIdx));
            t.setOpacity(0);
            add(t);
            runs.add(t);
            runLine.add(lineIdx);
            runStart.add(from);
            runLen.add(to - from);
        }

        double lineY(int i) { return top + i * pitch; }
        double centerX() { return card.getPosition().x(); }

        /** x where a line's text ends — where a callout's leader line starts. */
        double lineEndX(int i) { return left + 35 + src[i].stripTrailing().length() * charW; }

        List<MObject> parts() {
            List<MObject> l = new ArrayList<>();
            l.add(card);
            l.add(hl);
            l.addAll(runs);
            return l;
        }

        /** The card fades in and the code types out line by line. */
        void typeIn(double secs) {
            int n = src.length;
            double perLine = secs / n;
            List<Animation> an = new ArrayList<>();
            an.add(new FadeIn(card, d(0.5)));
            for (int r = 0; r < runs.size(); r++) {
                int li = runLine.get(r);
                double lineLen = Math.max(1, src[li].stripTrailing().length());
                double start = li * perLine + (runStart.get(r) / lineLen) * perLine * 0.95;
                double dur = Math.max(0.05, (runLen.get(r) / lineLen) * perLine * 0.95);
                an.add(new TypeAt(runs.get(r), d(start), d(dur)));
            }
            playAll(an);
        }

        void setLine(int i) { hl.setPosition(centerX(), lineY(i)); }

        Animation moveHl(int i, double dur) {
            return new MoveTo(hl, centerX(), lineY(i), dur).setEasing(Easing.EASE_IN_OUT);
        }

        void shift(List<Animation> into, double dx, double dy, double dur) {
            for (MObject m : parts())
                into.add(new MoveTo(m, m.getPosition().x() + dx, m.getPosition().y() + dy, dur).setEasing(Easing.EASE_IN_OUT));
            left += dx;
            top += dy;
        }
    }

    /**
     * A callout box with a leader line from (fx, fy). Appears via the returned animations;
     * the created objects are added to {@code made}.
     */
    protected void callout(List<Animation> into, List<MObject> made, String line1, String line2,
                           double cx, double cy, double w, double h, double fx, double fy) {
        RectMob box = new RectMob(w, h).setCornerRadius(16);
        box.setFillColor(Colors.withAlpha(Colors.GREEN, 0.26));
        box.setStrokeColor(Colors.GREEN);
        box.setStrokeWidth(2);
        box.setPosition(cx, cy);
        box.setOpacity(0);
        add(box);
        LineMob leader = new LineMob(fx, fy, cx - w / 2, cy, Colors.withAlpha(Colors.GREEN, 0.9), 3);
        add(leader);
        double ty = line2 == null ? cy : cy - 14;
        TextMob t1 = label(line1, cx, ty, 26, Colors.WHITE, false, true);
        into.add(new DrawLine(leader, d(0.6)));
        into.add(new FadeIn(box, d(0.5)));
        into.add(new FadeIn(t1, d(0.5)));
        made.add(leader);
        made.add(box);
        made.add(t1);
        if (line2 != null) {
            TextMob t2 = label(line2, cx, cy + 20, 21, Colors.LIGHT_GRAY, false, false);
            into.add(new FadeIn(t2, d(0.5)));
            made.add(t2);
        }
    }

    /** Arrow plus letter, moved together along a row. */
    protected static final class Ptr {
        final ArrowMob arrow;
        final TextMob lab;
        final double baseX, labY;

        Ptr(ArrowMob arrow, TextMob lab, double baseX, double labY) {
            this.arrow = arrow;
            this.lab = lab;
            this.baseX = baseX;
            this.labY = labY;
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

    protected Ptr pointer(String name, double x, double yTip, boolean pointsDown, Color color) {
        double y0 = pointsDown ? yTip - 36 : yTip + 36;
        double labY = pointsDown ? yTip - 56 : yTip + 56;
        ArrowMob arr = new ArrowMob(x, y0, x, yTip);
        arr.setHeadLength(11);
        arr.setStrokeColor(color);
        arr.setStrokeWidth(3);
        arr.setOpacity(0);
        add(arr);
        TextMob lab = label(name, x, labY, 28, color, false, true);
        return new Ptr(arr, lab, x, labY);
    }

    // ── bars ─────────────────────────────────────────────────────────

    protected static Color valueColor(int v, int lo, int hi) {
        return Colors.interpolate(Colors.BLUE, Colors.ORANGE, hi <= lo ? 0 : (v - lo) / (double) (hi - lo));
    }

    /** One bar: a translucent rectangle with its value above it. */
    protected final class SBar {
        final RectMob rect;
        final TextMob lbl;
        final int value;
        final double h;

        SBar(int value, double x, double baseY, double w, double h, Color c) {
            this.value = value;
            this.h = h;
            rect = new RectMob(w, h).setCornerRadius(8);
            rect.setFillColor(Colors.withAlpha(c, 0.42));
            rect.setStrokeColor(c);
            rect.setStrokeWidth(2.5);
            rect.setPosition(x, baseY - h / 2);
            rect.setOpacity(0);
            add(rect);
            lbl = label(String.valueOf(value), x, baseY - h - 24, Math.min(34, w * 0.45), Colors.WHITE, false, true);
        }

        double x() { return rect.getPosition().x(); }
        double y() { return rect.getPosition().y(); }
    }

    /** A row of bars standing on a baseline, with helpers to move, swap and color them. */
    protected final class Bars {
        final int n, lo, hi;
        final SBar[] at;
        final double cx, baseY, pitch, bw, unit, minH;
        final List<TextMob> idx = new ArrayList<>();

        Bars(int[] vals, double cx, double baseY, double pitch, double bw, double unit, double minH, boolean showIdx) {
            this.n = vals.length;
            this.cx = cx;
            this.baseY = baseY;
            this.pitch = pitch;
            this.bw = bw;
            this.unit = unit;
            this.minH = minH;
            int mn = Integer.MAX_VALUE, mx = Integer.MIN_VALUE;
            for (int v : vals) { mn = Math.min(mn, v); mx = Math.max(mx, v); }
            lo = mn;
            hi = mx;
            at = new SBar[n];
            for (int i = 0; i < n; i++) {
                at[i] = new SBar(vals[i], slotX(i), baseY, bw, minH + unit * vals[i], valueColor(vals[i], lo, hi));
                if (showIdx) idx.add(label(String.valueOf(i), slotX(i), baseY + 28, 22, Colors.GRAY, false, false));
            }
        }

        double slotX(int i) { return cx + (i - (n - 1) / 2.0) * pitch; }

        List<MObject> parts() {
            List<MObject> l = new ArrayList<>();
            for (SBar b : at) { l.add(b.rect); l.add(b.lbl); }
            l.addAll(idx);
            return l;
        }

        void fadeIn(List<Animation> into, double stagger, double dur) {
            for (int i = 0; i < n; i++) {
                into.add(new FadeInAt(at[i].rect, stagger * i, dur));
                into.add(new FadeInAt(at[i].lbl, stagger * i, dur));
                if (!idx.isEmpty()) into.add(new FadeInAt(idx.get(i), stagger * i, dur));
            }
        }

        void moveX(List<Animation> into, SBar b, double x, double bulge, double dur) {
            into.add(new ArcMove(b.rect, x, b.y(), bulge, dur));
            into.add(new ArcMove(b.lbl, x, b.lbl.getPosition().y(), bulge, dur));
        }

        void moveToSlot(List<Animation> into, SBar b, int slot, double bulge, double dur) {
            moveX(into, b, slotX(slot), bulge, dur);
        }

        /** Moves a bar to a slot, standing on the baseline (any lift is undone). */
        void place(List<Animation> into, SBar b, int slot, double bulge, double dur) {
            double x = slotX(slot);
            into.add(new ArcMove(b.rect, x, baseY - b.h / 2, bulge, dur));
            into.add(new ArcMove(b.lbl, x, baseY - b.h - 24, bulge, dur));
        }

        /** Moves a bar straight up (dy > 0) or down. */
        void lift(List<Animation> into, SBar b, double dy, double dur) {
            into.add(new MoveTo(b.rect, b.x(), b.y() - dy, dur).setEasing(Easing.EASE_IN_OUT));
            into.add(new MoveTo(b.lbl, b.x(), b.lbl.getPosition().y() - dy, dur).setEasing(Easing.EASE_IN_OUT));
        }

        /** Swaps the bars in slots i < j; the left one arcs high over the right one. */
        void swap(List<Animation> into, int i, int j, double dur) {
            SBar a = at[i], b = at[j];
            moveToSlot(into, a, j, 130, dur);
            moveToSlot(into, b, i, -50, dur);
            at[i] = b;
            at[j] = a;
        }

        void paint(List<Animation> into, SBar b, Color c, double dur) {
            into.add(new ColorChange(b.rect, Colors.withAlpha(c, 0.42), dur));
            into.add(new ColorChange(b.rect, c, dur, ColorChange.Target.STROKE));
        }

        void paintFinal(List<Animation> into, SBar b, double dur) {
            into.add(new ColorChange(b.rect, FINAL_FILL, dur));
            into.add(new ColorChange(b.rect, FINAL_STROKE, dur, ColorChange.Target.STROKE));
        }

        void paintOriginal(List<Animation> into, SBar b, double dur) {
            paint(into, b, valueColor(b.value, lo, hi), dur);
        }
    }
}
