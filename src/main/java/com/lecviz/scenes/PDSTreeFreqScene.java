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
 * Standalone clip for slides 32-33 of the trees deck: letter frequencies and "make the common case faster".
 *
 *   Slide 32  "What is this plot?": 26 bars grow from the baseline, the percentage of each letter in English text, e
 *             at 12.02 down to z; the slide's question about exploiting the frequencies in a callout. The values
 *             after P are not legible on the slide; they come from the same source (Cornell, cryptography notes)
 *   Slide 33  the five examples of making the common case faster (ice cream in front, fish at a separate counter,
 *             bicycle parking, classes in the hostels), each as a small drawn card, ending with the question about 'e'
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeFreqScene extends PDSTreeClipBase {

    private static final String LETTERS = "ETAOINSRHDLUCMFYWGPBVKXQJZ";
    private static final double[] FREQ = {12.02, 9.10, 8.12, 7.68, 7.31, 6.95, 6.28, 6.02, 5.92, 4.32, 3.98, 2.88, 2.71,
            2.61, 2.30, 2.11, 2.09, 2.03, 1.82, 1.49, 1.11, 0.69, 0.17, 0.11, 0.10, 0.07};

    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();

    /** A bar growing up from its base. */
    private static final class GrowUp extends Animation {
        private final RectMob r;
        private final double baseY, h;

        GrowUp(RectMob r, double baseY, double h, double dur) {
            super(r, dur, Easing.EASE_OUT);
            this.r = r;
            this.baseY = baseY;
            this.h = h;
        }

        @Override public void begin() { r.setOpacity(1); interpolate(0); }

        @Override
        public void interpolate(double t) {
            double hh = Math.max(0.5, h * t);
            r.setSize(r.getWidth(), hh);
            r.setPosition(r.getPosition().x(), baseY - hh / 2);
        }
    }

    @Override
    public void construct() {
        head = writeHeading("What is this plot?");
        pause(0.5);
        plot();
        fadeOutAll(d(1.0), head);
        pause(0.4);
        head = writeHeading("Make the common case faster!");
        pause(0.4);
        common();
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    // ── slide 32 ─────────────────────────────────────────────────────

    private void plot() {
        double base = 300, unit = 30, pitch = 66, bw = 50, left = -(25 * pitch) / 2;
        Link axis = new Link(new double[]{left - 50, -left + 50}, new double[]{base, base}, Colors.withAlpha(Colors.WHITE, 0.7), 3, false);
        add(axis);
        play(new DrawLink(axis, d(0.8)));
        mine.add(axis);
        List<Animation> grow = new ArrayList<>();
        List<Animation> labs = new ArrayList<>();
        for (int i = 0; i < 26; i++) {
            double x = left + i * pitch, h = FREQ[i] * unit;
            Color c = Colors.interpolate(Colors.GOLD, Colors.BLUE, i / 25.0);
            RectMob bar = new RectMob(bw, 1).setCornerRadius(6);
            bar.setFillColor(Colors.withAlpha(c, 0.55));
            bar.setStrokeColor(c);
            bar.setStrokeWidth(2.2);
            bar.setPosition(x, base - 0.5);
            bar.setOpacity(0);
            add(bar);
            grow.add(new GrowUpAt(bar, base, h, 0.12 * i, d(0.7)));
            TextMob l = label(String.valueOf(LETTERS.charAt(i)), x, base + 28, 30, Colors.WHITE, false, true);
            labs.add(new FadeInAt(l, 0.05 * i, d(0.4)));
            mine.add(bar);
            mine.add(l);
            if (i < 5 || i == 25) {
                TextMob v = label(String.format("%.2f", FREQ[i]), x, base - h - 24, 24, Colors.LIGHT_GRAY, false, true);
                grow.add(new FadeInAt(v, 0.12 * i + 0.6, d(0.4)));
                mine.add(v);
            }
        }
        playAll(labs);
        playAll(grow);
        pause(1.0);
        TextMob ans = label("how often each letter appears in English text (percent)", 0, 400, 32, Colors.LIGHT_GRAY, false, false);
        play(new FadeIn(ans, d(0.8)));
        mine.add(ans);
        pause(1.8);
        // the slide's callout
        RectMob box = new RectMob(620, 150).setCornerRadius(18);
        box.setFillColor(Colors.withAlpha(Colors.WHITE, 0.92));
        box.setStrokeColor(Colors.WHITE);
        box.setPosition(380, -250);
        box.setOpacity(0);
        add(box);
        TextMob c1 = label("Can we exploit these frequencies", 380, -280, 32, INK, false, true);
        TextMob c2 = label("to improve data transmission?", 380, -225, 32, INK, false, true);
        play(new FadeIn(box, d(0.7)), new FadeIn(c1, d(0.7)), new FadeIn(c2, d(0.7)));
        mine.add(box);
        mine.add(c1);
        mine.add(c2);
        TextMob src = label("Source: pi.math.cornell.edu/~mec/2003-2004/cryptography/subs/frequencies.html", 0, 470, 22, Colors.GRAY, false, false);
        play(new FadeIn(src, d(0.6)));
        mine.add(src);
        pause(3.4);
        fadeOutAll(d(1.0), mine);
        mine.clear();
    }

    /** A bar that starts growing after a delay. */
    private static final class GrowUpAt extends Animation {
        private final RectMob r;
        private final double baseY, h, delay, span;

        GrowUpAt(RectMob r, double baseY, double h, double delay, double dur) {
            super(r, delay + dur, Easing.LINEAR);
            this.r = r;
            this.baseY = baseY;
            this.h = h;
            this.delay = delay;
            this.span = dur;
        }

        @Override public void begin() { r.setOpacity(1); interpolate(0); }

        @Override
        public void interpolate(double t) {
            double p = Math.max(0, Math.min(1, (t * duration - delay) / span));
            double hh = Math.max(0.5, h * Easing.EASE_OUT.applyAsDouble(p));
            r.setSize(r.getWidth(), hh);
            r.setPosition(r.getPosition().x(), baseY - hh / 2);
        }
    }

    // ── slide 33 ─────────────────────────────────────────────────────

    private void common() {
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "If most people order vanilla ice-cream, keep it in front."));
        s.add(ln(0, "If only a few students buy fish, keep it at a"));
        s.add(ln(3, "separate counter."));
        s.add(ln(0, "If most people coming to the department use"));
        s.add(ln(3, "bicycle, bicycle parking should be prioritized."));
        s.add(ln(0, "If most of the humans in the classroom stay in"));
        s.add(ln(3, "hostels, the classes should be held in hostels!"));
        s.add(ln(0, "If 'e' gets used more often, can we transmit it"));
        s.add(ln(3, "faster?"));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(1.4);
        swipeAway(text);
        pause(0.4);
        double[] cx = {-760, -380, 0, 380, 760};
        String[] cap = {"ice cream", "fish", "bicycles", "classes", "'e'"};
        String[] sub = {"keep it in front", "separate counter", "prioritize parking", "hold them in hostels", "transmit it faster"};
        Color[] col = {Colors.PINK, Colors.TEAL, Colors.GREEN, Colors.GOLD, Colors.ORANGE};
        for (int i = 0; i < 5; i++) {
            RectMob card = panel(cx[i], 0, 350, 520, col[i], 0.08);
            TextMob n = label(cap[i], cx[i], -210, 40, col[i], false, true);
            TextMob su = label(sub[i], cx[i], 190, 28, Colors.LIGHT_GRAY, false, false);
            TextMob common = label(i == 1 ? "rare case" : "common case", cx[i], 130, 28, i == 1 ? Colors.GRAY : col[i], false, true);
            List<MObject> icon = icon(i, cx[i], -30, col[i]);
            List<Animation> a = new ArrayList<>();
            a.add(new FadeIn(card, d(0.6)));
            a.add(new FadeIn(n, d(0.6)));
            for (MObject m : icon) a.add(new FadeInAt(m, 0.3, d(0.6)));
            a.add(new FadeInAt(su, 0.6, d(0.6)));
            a.add(new FadeInAt(common, 0.6, d(0.6)));
            playAll(a);
            mine.add(card);
            mine.add(n);
            mine.add(su);
            mine.add(common);
            mine.addAll(icon);
            pause(0.8);
        }
        StrokeTextMob q = stroke("If 'e' gets used more often, can we transmit it faster?", 0, 400, 40, Colors.GOLD, false);
        play(new Write(q, d(3.2)));
        mine.add(q);
        pause(3.2);
        fadeOutAll(d(1.2), mine);
        mine.clear();
    }

    private Link poly(double[] xs, double[] ys, Color c, double w) {
        Link l = new Link(xs, ys, c, w, false);
        add(l);
        return l;
    }

    private CircleMob ring(double x, double y, double r, Color c, boolean fill) {
        CircleMob k = new CircleMob(r);
        k.setFillColor(fill ? Colors.withAlpha(c, 0.55) : Color.TRANSPARENT);
        k.setStrokeColor(c);
        k.setStrokeWidth(4);
        k.setPosition(x, y);
        k.setOpacity(0);
        add(k);
        return k;
    }

    private List<MObject> icon(int i, double x, double y, Color c) {
        List<MObject> l = new ArrayList<>();
        switch (i) {
            case 0 -> {   // an ice-cream cone with two scoops
                l.add(poly(new double[]{x - 45, x, x + 45, x - 45}, new double[]{y + 10, y + 120, y + 10, y + 10}, Colors.GOLD, 4));
                l.add(ring(x, y - 20, 48, c, true));
                l.add(ring(x, y - 85, 38, Colors.WHITE, true));
            }
            case 1 -> {   // a fish
                l.add(poly(new double[]{x - 70, x - 30, x + 30, x + 65, x + 30, x - 30, x - 70}, new double[]{y + 40, y - 10, y - 20, y + 40, y + 100, y + 90, y + 40}, c, 4));
                l.add(poly(new double[]{x + 65, x + 110, x + 110, x + 65}, new double[]{y + 40, y, y + 80, y + 40}, c, 4));
                l.add(ring(x - 38, y + 30, 7, Colors.WHITE, true));
            }
            case 2 -> {   // a bicycle
                l.add(ring(x - 62, y + 60, 40, c, false));
                l.add(ring(x + 62, y + 60, 40, c, false));
                l.add(poly(new double[]{x - 62, x - 15, x + 30, x + 62}, new double[]{y + 60, y - 10, y - 10, y + 60}, c, 4));
                l.add(poly(new double[]{x - 15, x + 5, x + 30}, new double[]{y - 10, y + 60, y - 10}, c, 4));
                l.add(poly(new double[]{x + 30, x + 40, x + 55}, new double[]{y - 10, y - 30, y - 30}, c, 4));
            }
            case 3 -> {   // a hostel with a roof
                l.add(poly(new double[]{x - 80, x - 80, x + 80, x + 80, x - 80}, new double[]{y + 120, y + 10, y + 10, y + 120, y + 120}, c, 4));
                l.add(poly(new double[]{x - 100, x, x + 100}, new double[]{y + 10, y - 70, y + 10}, c, 4));
                l.add(poly(new double[]{x - 20, x - 20, x + 20, x + 20}, new double[]{y + 120, y + 70, y + 70, y + 120}, c, 4));
            }
            default -> {  // the letter e
                l.add(ring(x, y + 35, 80, c, true));
                TextMob e = label("e", x, y + 35, 110, Colors.WHITE, false, true);
                l.add(e);
            }
        }
        for (MObject m : l) m.setOpacity(0);
        return l;
    }
}
