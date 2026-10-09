package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Easing;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Shared kit for the pixel-art clips (16:9, 1920 x 1080, with sound): a flat dark background, a yellow Impact title,
 * pixel "slime" characters that carry the numbers ({@link PixelSlime}), their moves (hop with squash and stretch, fall
 * and land, wobble, confetti, pulse), the small marker pills and tags, and a syntax-highlighted code panel with a yellow
 * bar on the line being run. Sound is logged through {@code sfx(...)} (see {@link com.lecviz.core.Sfx}).
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public abstract class PDSPixelBase extends PDSSortClipBase {

    // ── palette ──────────────────────────────────────────────────────
    protected static final Color BG = Color.web("#17181b");
    protected static final Color YELLOW = Color.web("#ffe600");
    protected static final Color CYAN = Color.web("#4dd8e6");
    protected static final Color ORANGE = Color.web("#ffa24a");
    protected static final Color GREEN = Color.web("#6fdc8c");
    protected static final Color RED = Color.web("#ff6b6b");
    protected static final Color GRAY = Color.web("#8d939c");
    protected static final Color[] RAINBOW = {
            Color.web("#ff5d73"), Color.web("#ff9f4a"), Color.web("#ffd84d"), Color.web("#6fdc6f"),
            Color.web("#3fd9c6"), Color.web("#5aa2ff"), Color.web("#9d7bff"), Color.web("#d36bff")};
    private static final int[] FACE_OF_VALUE = {0, 3, 2, 5, 1, 7, 6, 0, 4};   // by value 1..8

    // ── layout ───────────────────────────────────────────────────────
    protected static final double TITLE_Y = -482, PS = 9.0, RIGHT_CX = 470;
    protected double viewX = 0, pitch = 190, ground = -195;
    protected int handN = 8;

    protected double slotX(int k) { return viewX + (k - (handN - 1) / 2.0) * pitch; }

    protected static double pan(double x) { return Math.max(-0.8, Math.min(0.8, x / 960.0)); }

    protected double barY() { return ground + 6 * PS + 24; }
    protected double markY() { return ground + 152; }
    protected double pillY() { return 70; }
    protected double varY() { return 165; }
    protected double cntY() { return 245; }

    @Override protected int videoBitrate() { return 7_000_000; }

    // ── timing helpers ───────────────────────────────────────────────

    /** base * sp, but never below a floor that shrinks (to 35%) as sp gets small. */
    protected static double q(double base, double sp, double min) {
        return Math.max(min * Math.min(1, Math.max(0.35, sp / 0.3)), base * sp);
    }

    // ── small animations & mobjects ──────────────────────────────────

    protected abstract static class Timed extends Animation {
        final double delay, span;

        Timed(MObject t, double delay, double dur) {
            super(t, delay + dur, Easing.LINEAR);
            this.delay = delay;
            this.span = dur;
        }

        double p(double t) { return Math.max(0, Math.min(1, (t * duration - delay) / span)); }
    }

    /** A jump from the current place to (x, lift) along an arc, with squash and stretch. */
    protected static final class Hop extends Timed {
        final PixelSlime s;
        final double x1, l1, h;
        double x0, l0;

        Hop(PixelSlime s, double delay, double x1, double l1, double h, double dur) {
            super(s, delay, dur);
            this.s = s;
            this.x1 = x1;
            this.l1 = l1;
            this.h = h;
        }

        @Override
        public void begin() {
            x0 = s.getPosition().x();
            l0 = s.getLift();
        }

        @Override
        public void interpolate(double t) {
            double u = p(t);
            double e = u < 0.5 ? 4 * u * u * u : 1 - Math.pow(-2 * u + 2, 3) / 2;
            s.place(x0 + (x1 - x0) * e, l0 + (l1 - l0) * e + h * Math.sin(Math.PI * u));
            double st = 0.15 * Math.sin(Math.PI * Math.min(1, u * 1.15)) * (h > 0 || Math.abs(l1 - l0) > 1 ? 1 : 0);
            double sy = 1 + st, sx = 1 - st * 0.55;
            if (u > 0.86 && u < 1) {
                double q = (u - 0.86) / 0.14;
                sy = 1 - 0.24 * Math.sin(Math.PI * q);
                sx = 1 + 0.2 * Math.sin(Math.PI * q);
            }
            if (u <= 0 || u >= 1) { sx = 1; sy = 1; }
            s.setSquash(sx, sy);
        }
    }

    /** Falls in from above, lands with a squash and bounces once. */
    protected static final class LandIn extends Timed {
        final PixelSlime s;
        final double from;

        LandIn(PixelSlime s, double delay, double from, double dur) {
            super(s, delay, dur);
            this.s = s;
            this.from = from;
        }

        @Override
        public void begin() { s.place(s.getPosition().x(), from); }

        @Override
        public void interpolate(double t) {
            double u = p(t);
            double lift;
            double sx = 1, sy = 1;
            if (u < 0.6) {
                double q = u / 0.6;
                lift = from * (1 - q * q);
                sy = 1 + 0.12 * q;
                sx = 1 - 0.06 * q;
            } else if (u < 0.78) {
                double q = (u - 0.6) / 0.18;
                lift = 0;
                sy = 1 - 0.26 * Math.sin(Math.PI * q);
                sx = 1 + 0.22 * Math.sin(Math.PI * q);
            } else {
                double q = (u - 0.78) / 0.22;
                lift = 20 * Math.sin(Math.PI * q);
                sy = 1 + 0.06 * Math.sin(Math.PI * q);
            }
            s.place(s.getPosition().x(), lift);
            s.setSquash(sx, sy);
        }
    }

    /** A small side-to-side wobble that dies away. */
    protected static final class Wobble extends Animation {
        final PixelSlime s;

        Wobble(PixelSlime s, double dur) {
            super(s, dur, Easing.LINEAR);
            this.s = s;
        }

        @Override
        public void interpolate(double t) { s.setRotation(0.16 * Math.sin(2 * Math.PI * 3 * t) * (1 - t)); }
    }

    /** Drives a confetti burst. */
    protected static final class Burst extends Animation {
        final Confetti c;

        Burst(Confetti c) {
            super(c, c.getLife(), Easing.LINEAR);
            this.c = c;
        }

        @Override
        public void begin() { c.setOpacity(1); c.setTime(0); }

        @Override
        public void interpolate(double t) { c.setTime(t * c.getLife()); }
    }

    /** A quick swell and settle (scale 1 -> 1 + amount -> 1) in one animation. */
    protected static final class Pulse extends Animation {
        final double amount;

        Pulse(MObject t, double dur, double amount) {
            super(t, dur, Easing.LINEAR);
            this.amount = amount;
        }

        @Override
        public void interpolate(double t) { target.setScale(1 + amount * Math.sin(Math.PI * t)); }
    }

    /** A pair of thin brackets around the array. */
    protected static final class Brackets extends MObject {
        final double halfW, halfH;

        Brackets(double halfW, double halfH) {
            this.halfW = halfW;
            this.halfH = halfH;
            this.opacity = 0;
        }

        @Override
        protected void draw(GraphicsContext gc) {
            gc.setStroke(Color.web("#9aa0a8"));
            gc.setLineWidth(5);
            gc.strokePolyline(new double[]{-halfW + 18, -halfW, -halfW, -halfW + 18}, new double[]{-halfH, -halfH, halfH, halfH}, 4);
            gc.strokePolyline(new double[]{halfW - 18, halfW, halfW, halfW - 18}, new double[]{-halfH, -halfH, halfH, halfH}, 4);
        }

        @Override
        public MObject copy() { Brackets b = new Brackets(halfW, halfH); copyBaseProperties(b); return b; }
    }

    /** A dashed outline: the empty place the key will drop back into. */
    protected static final class Ghost extends MObject {
        final double w, h;

        Ghost(double w, double h) {
            this.w = w;
            this.h = h;
            this.opacity = 0;
        }

        @Override
        protected void draw(GraphicsContext gc) {
            gc.setStroke(YELLOW);
            gc.setLineWidth(4);
            gc.setLineDashes(11, 9);
            gc.strokeRoundRect(-w / 2, -h / 2, w, h, 24, 24);
            gc.setLineDashes((double[]) null);
        }

        @Override
        public MObject copy() { Ghost g = new Ghost(w, h); copyBaseProperties(g); return g; }
    }

    /** A small pill with a letter and a pointer, for ii and jj. */
    protected static final class Tag extends MObject {
        final String text;
        final Color color;

        Tag(String text, Color color) {
            this.text = text;
            this.color = color;
            this.opacity = 0;
        }

        @Override
        protected void draw(GraphicsContext gc) {
            gc.setFill(color.deriveColor(0, 1, 1, 0.92));
            gc.fillPolygon(new double[]{-11, 11, 0}, new double[]{-17, -17, -31}, 3);
            gc.fillRoundRect(-32, -17, 64, 40, 20, 20);
            gc.setFill(BG);
            gc.setFont(Font.font("Helvetica Neue", FontWeight.BOLD, 27));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(javafx.geometry.VPos.CENTER);
            gc.fillText(text, 0, 4);
        }

        @Override
        public MObject copy() { Tag t = new Tag(text, color); copyBaseProperties(t); return t; }
    }

    /** A rounded chip with changing text. */
    protected final class Pill {
        final RectMob box;
        final TextMob txt;

        Pill(double x, double y, double w, double h, Color accent, String text, double size) {
            box = new RectMob(w, h).setCornerRadius(h / 2.3);
            box.setFillColor(Colors.withAlpha(accent, 0.15));
            box.setStrokeColor(Colors.withAlpha(accent, 0.85));
            box.setStrokeWidth(2.5);
            box.setPosition(x, y);
            box.setOpacity(0);
            add(box);
            txt = label(text, x, y + 1, size, Colors.WHITE, false, true);
            txt.setFontFamily("Helvetica Neue");
        }

        void set(String s) { txt.setText(s); }

        void tint(Color accent) {
            box.setFillColor(Colors.withAlpha(accent, 0.18));
            box.setStrokeColor(Colors.withAlpha(accent, 0.9));
        }

        List<MObject> parts() {
            List<MObject> l = new ArrayList<>();
            l.add(box);
            l.add(txt);
            return l;
        }

        void fadeIn(List<Animation> into, double dur) {
            into.add(new FadeIn(box, dur));
            into.add(new FadeIn(txt, dur));
        }

        void fadeOut(List<Animation> into, double dur) {
            into.add(new FadeOut(box, dur));
            into.add(new FadeOut(txt, dur));
        }

        void bumpUp(List<Animation> into, double dur) {
            into.add(new Pulse(box, dur, 0.1));
            into.add(new Pulse(txt, dur, 0.1));
        }
    }

    // ── slimes ───────────────────────────────────────────────────────

    /** A slime for value 1..8, standing on the current ground, hidden until faded in. */
    protected PixelSlime slime(int v, double x) {
        PixelSlime s = new PixelSlime(v, FACE_OF_VALUE[((v - 1) % 8) + 1], RAINBOW[(v - 1) % 8], PS);
        s.setGround(ground);
        s.place(x, 0);
        s.setOpacity(0);
        add(s);
        return s;
    }

    // ── the title ────────────────────────────────────────────────────

    /** A yellow Impact title with a drop shadow; returns its two layers. */
    protected List<MObject> makeTitle(String text) {
        List<MObject> l = new ArrayList<>();
        for (int pass = 0; pass < 2; pass++) {
            double off = pass == 0 ? 5 : 0;
            TextMob t = label(text, off, TITLE_Y + off, 84, pass == 0 ? Color.rgb(0, 0, 0, 0.6) : YELLOW, false, false);
            t.setFontFamily("Impact");
            t.setScale(0.7);
            l.add(t);
        }
        return l;
    }

    protected void popIn(List<MObject> objs) {
        sfx("intro");
        List<Animation> a = new ArrayList<>();
        for (MObject m : objs) {
            a.add(new FadeIn(m, 0.35));
            a.add(new ScaleTo(m, 1.0, 0.45).setEasing(Easing.EASE_OUT));
        }
        playAll(a);
    }

    // ── text with sound ──────────────────────────────────────────────

    private int bullets = 0;

    @Override
    protected void lineStart(int level, double secs) {
        if (level == 0) sfx("pop", 5 + (bullets++ % 5), -0.3);
        for (double t = 0.12; t < secs - 0.05; t += 0.075) sfxAt(t, "type", 0, 0);
    }

    /** A pen-stroke sentence with typing clicks. */
    protected StrokeTextMob say(String text, double x, double y, double size, Color c, double secs) {
        StrokeTextMob t = stroke(text, x, y, size, c, false);
        double dd = d(secs);
        for (double k = 0.1; k < dd - 0.05; k += 0.075) sfxAt(k, "type", 0, 0);
        play(new Write(t, dd));
        return t;
    }

    // ── the code panel ───────────────────────────────────────────────

    private static final Pattern TOK = Pattern.compile("([A-Za-z_][A-Za-z_0-9]*)|(\\d+)|(\\s+)|(.)");
    private static final java.util.Set<String> KEYWORDS = java.util.Set.of("for", "int", "while", "if", "else", "return", "void");
    private static final java.util.Set<String> VARIABLES = java.util.Set.of("ii", "jj", "key", "arr", "N");
    private static final Color C_KEY = Color.web("#c792ea"), C_NUM = Color.web("#ffb86c"),
            C_VAR = Color.web("#ff7a93"), C_OP = Color.web("#dfe3e8");

    protected CodePanel panel;

    /** A code card with C-style syntax colors and a yellow bar that follows the line being run. */
    protected final class CodePanel {
        final String[] src;
        final RectMob card, bar, tick;
        final List<TextMob> runs = new ArrayList<>();
        final List<Integer> runLine = new ArrayList<>();
        final List<Integer> runStart = new ArrayList<>();
        final List<Integer> runLen = new ArrayList<>();
        final TextMob note;
        final double w, size, pitch, charW;
        double cx, top;

        CodePanel(String[] src, double cx, double top, double w, double size, double pitch) {
            this.src = src;
            this.cx = cx;
            this.top = top;
            this.w = w;
            this.size = size;
            this.pitch = pitch;
            double h = 34 + src.length * pitch + 16;
            card = new RectMob(w, h).setCornerRadius(28);
            card.setFillColor(Color.web("#212327"));
            card.setStrokeColor(Color.web("#33363b"));
            card.setStrokeWidth(2);
            card.setPosition(cx, top + h / 2);
            card.setOpacity(0);
            add(card);
            bar = new RectMob(w - 16, pitch - 4).setCornerRadius(10);
            bar.setFillColor(Colors.withAlpha(YELLOW, 0.17));
            bar.setStrokeColor(Color.TRANSPARENT);
            bar.setPosition(cx, lineY(0));
            bar.setOpacity(0);
            add(bar);
            tick = new RectMob(7, pitch - 8).setCornerRadius(3);
            tick.setFillColor(YELLOW);
            tick.setStrokeColor(Color.TRANSPARENT);
            tick.setPosition(cx - w / 2 + 12, lineY(0));
            tick.setOpacity(0);
            add(tick);
            charW = measure("MMMMMMMMMM", "Menlo", size, false) / 10.0;
            for (int i = 0; i < src.length; i++) build(i);
            note = label("", cx + w / 2 - 30, lineY(0), 24, YELLOW, false, true);
            note.setFontFamily("Menlo");
            note.setAlignment(TextAlignment.RIGHT);
        }

        double left() { return cx - w / 2 + 40; }

        double lineY(int i) { return top + 34 + pitch / 2.0 + pitch * i; }

        double lineEndX(int i) { return left() + src[i].stripTrailing().length() * charW + 12; }

        private void build(int line) {
            Matcher m = TOK.matcher(src[line]);
            int col = 0;
            while (m.find()) {
                String t = m.group();
                if (m.group(3) != null) {
                    col += t.length();
                    continue;
                }
                Color c;
                if (m.group(1) != null) c = KEYWORDS.contains(t) ? C_KEY : VARIABLES.contains(t) ? C_VAR : C_OP;
                else if (m.group(2) != null) c = C_NUM;
                else c = C_OP;
                TextMob r = new TextMob(t).setFontSize(size).setFillColor(c);
                r.setFontFamily("Menlo");
                r.setAlignment(TextAlignment.LEFT);
                r.setPosition(left() + col * charW, lineY(line));
                r.setOpacity(0);
                add(r);
                runs.add(r);
                runLine.add(line);
                runStart.add(col);
                runLen.add(t.length());
                col += t.length();
            }
        }

        /** The card fades in and the code types out line by line over {@code secs}. */
        void typeIn(List<Animation> into, double secs) {
            into.add(new FadeIn(card, 0.5));
            double per = secs / src.length;
            for (int r = 0; r < runs.size(); r++) {
                int li = runLine.get(r);
                double lineLen = Math.max(1, src[li].stripTrailing().length());
                double start = li * per + (runStart.get(r) / lineLen) * per * 0.95;
                double dur = Math.max(0.05, (runLen.get(r) / lineLen) * per * 0.95);
                into.add(new TypeAt(runs.get(r), start, dur));
            }
        }

        void appear(List<Animation> into) {
            into.add(new FadeIn(card, 0.5));
            for (int i = 0; i < runs.size(); i++) into.add(new FadeInAt(runs.get(i), 0.2 + 0.01 * i, 0.4));
        }

        List<MObject> parts() {
            List<MObject> l = new ArrayList<>(runs);
            l.add(card);
            l.add(bar);
            l.add(tick);
            l.add(note);
            return l;
        }

        /** Slides the whole panel by (dx, dy). */
        void slide(List<Animation> into, double dx, double dy, double dur) {
            for (MObject m : parts()) into.add(new MoveTo(m, m.getPosition().x() + dx, m.getPosition().y() + dy, dur).setEasing(Easing.EASE_IN_OUT));
            cx += dx;
            top += dy;
        }
    }

    /** The animations that move the highlight bar to a line (and show its note), to play together with the action itself. */
    protected List<Animation> bar(int line, double sp, String note, Color noteColor) {
        double dur = q(0.2, sp, 0.07);
        double y = panel.lineY(line);
        panel.note.setOpacity(0);
        List<Animation> l = new ArrayList<>();
        l.add(new MoveTo(panel.bar, panel.cx, y, dur));
        l.add(new MoveTo(panel.tick, panel.cx - panel.w / 2 + 12, y, dur));
        if (note != null) {
            panel.note.setText(note);
            panel.note.setFillColor(noteColor);
            panel.note.setPosition(panel.cx + panel.w / 2 - 30, y);
            l.add(new FadeInAt(panel.note, dur * 0.5, 0.1));
        }
        return l;
    }

    protected void highlight(int line, double dur, String note, Color noteColor) {
        List<Animation> l = bar(line, 1, note, noteColor);
        play(l.toArray(new Animation[0]));
    }
}
