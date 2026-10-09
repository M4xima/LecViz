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
 * Standalone clip for slide 35 of the trees deck: prefix codes.
 *
 *   - the slide's points one line at a time
 *   - "easy to decode": code 3 (00, 10, 11, 010, 011) is checked pair by pair: no code is the beginning of another
 *   - "faster transmission of frequent data": the average number of bits per letter for the five letters of the
 *     previous slide, 3 bits with a fixed-length code and about 2.34 with code 3 (a small example; the slide says that
 *     in practice the improvement is close to 40-50%)
 *   - "We will study Huffman's algorithm during Heaps": a card that points ahead
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreePrefixScene extends PDSTreeClipBase {

    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();

    @Override
    public void construct() {
        head = writeHeading("Prefix Codes");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Such codes were invented by Huffman.").kw("Huffman", Colors.GOLD));
        s.add(ln(1, "as a term paper at MIT during his PhD."));
        s.add(ln(1, "had the habit of keeping poisonous snakes as pets."));
        s.add(ln(0, "Prefix codes are easy to decode."));
        s.add(ln(1, "No ambiguous decoding possible."));
        s.add(ln(0, "Faster transmission of frequent data."));
        s.add(ln(1, "In practice, close to 40-50% improvement").kw("40-50%", Colors.GREEN));
        s.add(ln(0, "We will study Huffman's algorithm during Heaps."));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(1.6);
        swipeAway(text);
        pause(0.4);
        easy();
        faster();
        ahead();
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void narr(String t, Color c) { sayAt(t, c, 0, 450, 34); }

    // ── easy to decode ───────────────────────────────────────────────

    private void easy() {
        mine.addAll(solutionHeader(1, "Easy to decode", -400));
        String[] codes = {"00", "10", "11", "010", "011"};
        String[] ch = {"e", "t", "a", "o", "i"};
        List<RectMob> boxes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            RectMob b = panel(-600 + 300 * i, -230, 250, 90, Colors.BLUE, 0.2);
            TextMob c = mono(codes[i], -600 + 300 * i + 20, -230, 44, Colors.WHITE);
            TextMob l = label(ch[i], -600 + 300 * i - 80, -230, 44, Colors.GOLD, false, true);
            List<Animation> a = new ArrayList<>();
            a.add(new FadeInAt(b, 0.1 * i, d(0.5)));
            a.add(new FadeInAt(c, 0.1 * i, d(0.5)));
            a.add(new FadeInAt(l, 0.1 * i, d(0.5)));
            playAll(a);
            boxes.add(b);
            mine.add(b);
            mine.add(c);
            mine.add(l);
        }
        narr("Is any code the beginning of another? Check each code against all the others.", Colors.LIGHT_GRAY);
        pause(1.2);
        int found = 0;
        List<MObject> marks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<Animation> a = new ArrayList<>();
            a.add(new ColorChange(boxes.get(i), Colors.withAlpha(Colors.GOLD, 0.35), d(0.3)));
            playAll(a);
            for (int j = 0; j < 5; j++) {
                if (i == j) continue;
                if (codes[j].startsWith(codes[i])) found++;
            }
            TextMob ok = label("no", -600 + 300 * i, -150, 30, Colors.GREEN, false, true);
            play(new FadeIn(ok, d(0.4)));
            marks.add(ok);
            mine.add(ok);
            pause(0.45);
            List<Animation> b = new ArrayList<>();
            b.add(new ColorChange(boxes.get(i), Colors.withAlpha(Colors.BLUE, 0.2), d(0.3)));
            playAll(b);
        }
        narr("None starts another: the moment a code is complete, the letter is known. No ambiguity.", Colors.GREEN);
        pause(3.2);
        unsay();
        fadeOutAll(d(0.9), mine);
        mine.clear();
        pause(0.3);
    }

    // ── faster for frequent data ─────────────────────────────────────

    private void faster() {
        mine.addAll(solutionHeader(2, "Faster transmission of frequent data", -400));
        narr("Average bits per letter for e, t, a, o, i (weighted by how often each letter occurs).", Colors.LIGHT_GRAY);
        double[] f = {12.02, 9.10, 8.12, 7.68, 7.31};
        int[] len = {2, 2, 2, 3, 3};
        double sum = 0, tot = 0;
        for (int i = 0; i < 5; i++) {
            sum += f[i] * len[i];
            tot += f[i];
        }
        double avg = sum / tot;
        double base = 180, unit = 90;
        Link axis = new Link(new double[]{-560, 560}, new double[]{base, base}, Colors.withAlpha(Colors.WHITE, 0.6), 3, false);
        add(axis);
        play(new DrawLink(axis, d(0.6)));
        mine.add(axis);
        double[] vals = {3.0, avg};
        String[] names = {"fixed length: 3 bits", "code 3"};
        Color[] cols = {Colors.GRAY, Colors.GREEN};
        double[] xs = {-250, 250};
        List<Animation> grow = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            RectMob bar = new RectMob(190, 1).setCornerRadius(8);
            bar.setFillColor(Colors.withAlpha(cols[i], 0.5));
            bar.setStrokeColor(cols[i]);
            bar.setStrokeWidth(2.6);
            bar.setPosition(xs[i], base - 0.5);
            bar.setOpacity(0);
            add(bar);
            grow.add(new GrowAt(bar, base, vals[i] * unit, d(1.2)));
            TextMob nm = label(names[i], xs[i], base + 36, 28, Colors.WHITE, false, true);
            TextMob v = label(String.format("%.2f", vals[i]), xs[i], base - vals[i] * unit - 28, 36, cols[i], false, true);
            grow.add(new FadeInAt(nm, 0.2, d(0.5)));
            grow.add(new FadeInAt(v, 1.0, d(0.5)));
            mine.add(bar);
            mine.add(nm);
            mine.add(v);
        }
        playAll(grow);
        pause(1.0);
        TextMob sv = label(String.format("%.0f%% fewer bits", (1 - avg / 3.0) * 100), 0, -230, 46, Colors.GOLD, false, true);
        play(new FadeIn(sv, d(0.7)));
        mine.add(sv);
        pause(1.6);
        narr("A small example. With a full alphabet the slide says to expect close to 40-50% in practice.", Colors.GREEN);
        pause(3.4);
        unsay();
        fadeOutAll(d(0.9), mine);
        mine.clear();
        pause(0.3);
    }

    private static final class GrowAt extends Animation {
        private final RectMob r;
        private final double baseY, h;

        GrowAt(RectMob r, double baseY, double h, double dur) {
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

    // ── what comes next ──────────────────────────────────────────────

    private void ahead() {
        RectMob c1 = panel(-380, 0, 560, 300, Colors.BLUE, 0.1);
        TextMob t1 = label("Huffman's algorithm", -380, -60, 42, Colors.BLUE, false, true);
        TextMob s1 = label("builds a good prefix code", -380, 10, 30, Colors.LIGHT_GRAY, false, false);
        RectMob c2 = panel(380, 0, 560, 300, Colors.ORANGE, 0.1);
        TextMob t2 = label("Heaps", 380, -60, 42, Colors.ORANGE, false, true);
        TextMob s2 = label("the data structure it needs", 380, 10, 30, Colors.LIGHT_GRAY, false, false);
        Link ar = arrow(-90, 0, 90, 0, Colors.GOLD, 5);
        play(new FadeIn(c1, d(0.7)), new FadeIn(t1, d(0.7)), new FadeIn(s1, d(0.7)));
        play(new DrawLink(ar, d(0.7)));
        play(new FadeIn(c2, d(0.7)), new FadeIn(t2, d(0.7)), new FadeIn(s2, d(0.7)));
        mine.addAll(List.of(c1, t1, s1, c2, t2, s2, ar));
        StrokeTextMob w = stroke("We will study Huffman's algorithm during Heaps.", 0, 250, 40, Colors.GOLD, false);
        play(new Write(w, d(3.0)));
        mine.add(w);
        pause(3.4);
        fadeOutAll(d(1.2), mine);
        mine.clear();
    }
}
