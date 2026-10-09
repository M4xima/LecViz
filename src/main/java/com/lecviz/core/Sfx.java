package com.lecviz.core;

import java.util.List;
import java.util.Random;

/**
 * A tiny sound-effect synthesizer: scenes log named events with a time stamp ({@link Scene#sfx}), and this class
 * turns the log into a stereo track (48 kHz). Everything is generated from sine / triangle waves and filtered
 * noise, so there are no audio files to ship. Pitched effects take a note index on a major pentatonic scale, so a
 * run of related events (comparing 1, 2, 3 ...) sounds like a tune and never clashes.
 *
 * Effects: tick, pop, plop, good (bell), bad (bonk), boing, thud, slide, sparkle, whoosh_up, whoosh_down,
 * arp (marimba note), tada, intro, swirl, type.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public final class Sfx {

    /** One logged sound: when, which, a parameter (usually a note index) and a stereo position (-1 left .. 1 right). */
    public record Event(double t, String name, double a, double pan) {}

    public static final int SR = 48000;
    private static final double[] PENTA = {0, 2, 4, 7, 9};

    private Sfx() {}

    /** Frequency of note {@code idx} on a major pentatonic scale; idx 0 is C4, idx 5 is C5. */
    public static double freq(double idx) {
        int i = (int) Math.round(idx);
        int oct = Math.floorDiv(i, 5), deg = Math.floorMod(i, 5);
        return 261.63 * Math.pow(2, (PENTA[deg] + 12 * oct) / 12.0);
    }

    /** Renders the events into [left, right] sample arrays, normalized with a soft limiter. */
    public static float[][] render(List<Event> events, double seconds) {
        int n = (int) ((seconds + 1.5) * SR);
        double[] left = new double[n], right = new double[n];
        Random rnd = new Random(11);
        for (Event e : events) {
            double[] s = voice(e, rnd);
            if (s == null) continue;
            int start = (int) Math.round(e.t() * SR);
            double ang = (Math.max(-1, Math.min(1, e.pan())) + 1) * Math.PI / 4;
            double gl = Math.cos(ang), gr = Math.sin(ang);
            for (int i = 0; i < s.length && start + i < n; i++) {
                if (start + i < 0) continue;
                left[start + i] += s[i] * gl;
                right[start + i] += s[i] * gr;
            }
        }
        double peak = 1e-9;
        for (int i = 0; i < n; i++) {
            left[i] = Math.tanh(left[i] * 0.9);
            right[i] = Math.tanh(right[i] * 0.9);
            peak = Math.max(peak, Math.max(Math.abs(left[i]), Math.abs(right[i])));
        }
        double g = 0.88 / peak;
        float[] l = new float[n], r = new float[n];
        for (int i = 0; i < n; i++) {
            l[i] = (float) (left[i] * g);
            r[i] = (float) (right[i] * g);
        }
        return new float[][]{l, r};
    }

    // ── voices ───────────────────────────────────────────────────────

    private static double[] voice(Event e, Random rnd) {
        double f = freq(e.a());
        return switch (e.name()) {
            case "tick" -> tick(f);
            case "pop" -> pop(f);
            case "plop" -> plop(f);
            case "good" -> bell(f, 0.9);
            case "bad" -> bad();
            case "boing" -> boing(f);
            case "thud" -> thud(1.0);
            case "slide" -> slide(e.a());
            case "sparkle" -> sparkle(rnd);
            case "whoosh_up" -> whoosh(rnd, 350, 2600, 0.26, 0.55);
            case "whoosh_down" -> whoosh(rnd, 2600, 320, 0.34, 0.5);
            case "arp" -> marimba(f);
            case "tada" -> tada(rnd);
            case "intro" -> intro(rnd);
            case "swirl" -> swirl(rnd);
            case "type" -> typeClick(rnd);
            default -> null;
        };
    }

    private static double env(double t, double attack, double tau) {
        return (1 - Math.exp(-t / attack)) * Math.exp(-t / tau);
    }

    private static double tri(double phase) {
        return 2 / Math.PI * Math.asin(Math.sin(phase));
    }

    /** A short soft blip used for comparisons. */
    private static double[] tick(double f) {
        int n = (int) (0.14 * SR);
        double[] s = new double[n];
        for (int i = 0; i < n; i++) {
            double t = i / (double) SR;
            s[i] = (tri(2 * Math.PI * f * t) * 0.55 + Math.sin(2 * Math.PI * f * 2 * t) * 0.18) * env(t, 0.002, 0.04) * 0.55;
        }
        return s;
    }

    /** A rounded "pop": a sine that drops a little in pitch. */
    private static double[] pop(double f) {
        int n = (int) (0.16 * SR);
        double[] s = new double[n];
        double ph = 0;
        for (int i = 0; i < n; i++) {
            double t = i / (double) SR;
            ph += 2 * Math.PI * f * (1 + 0.6 * Math.exp(-t / 0.025)) / SR;
            s[i] = Math.sin(ph) * env(t, 0.001, 0.05) * 0.7;
        }
        return s;
    }

    /** A character landing: a pop with a low body. */
    private static double[] plop(double f) {
        int n = (int) (0.22 * SR);
        double[] s = new double[n];
        double ph = 0, ph2 = 0;
        for (int i = 0; i < n; i++) {
            double t = i / (double) SR;
            ph += 2 * Math.PI * f * (1 + 0.9 * Math.exp(-t / 0.03)) / SR;
            ph2 += 2 * Math.PI * (f / 2) * (1 + 0.4 * Math.exp(-t / 0.05)) / SR;
            s[i] = (Math.sin(ph) * 0.6 + Math.sin(ph2) * 0.35) * env(t, 0.001, 0.07) * 0.8;
        }
        return s;
    }

    /** A bright bell (inharmonic partials, long decay). */
    private static double[] bell(double f, double len) {
        int n = (int) (len * SR);
        double[] s = new double[n];
        double[] mult = {1, 2.76, 5.40, 8.93};
        double[] amp = {1.0, 0.42, 0.2, 0.08};
        double[] tau = {0.38, 0.2, 0.1, 0.05};
        for (int i = 0; i < n; i++) {
            double t = i / (double) SR;
            double v = 0;
            for (int k = 0; k < mult.length; k++) v += Math.sin(2 * Math.PI * f * mult[k] * t) * amp[k] * Math.exp(-t / tau[k]);
            s[i] = v * (1 - Math.exp(-t / 0.001)) * 0.32;
        }
        return s;
    }

    /** A soft low bonk. */
    private static double[] bad() {
        int n = (int) (0.2 * SR);
        double[] s = new double[n];
        double ph = 0;
        for (int i = 0; i < n; i++) {
            double t = i / (double) SR;
            ph += 2 * Math.PI * (230 - 95 * Math.min(1, t / 0.12)) / SR;
            s[i] = (Math.sin(ph) * 0.7 + tri(ph * 0.5) * 0.25) * env(t, 0.001, 0.075) * 0.7;
        }
        return s;
    }

    /** A springy rise (a character jumping up). */
    private static double[] boing(double f) {
        int n = (int) (0.34 * SR);
        double[] s = new double[n];
        double ph = 0;
        for (int i = 0; i < n; i++) {
            double t = i / (double) SR;
            double fr = f * (0.8 + 0.9 * Math.min(1, t / 0.2)) * (1 + 0.02 * Math.sin(2 * Math.PI * 22 * t));
            ph += 2 * Math.PI * fr / SR;
            s[i] = Math.sin(ph) * env(t, 0.004, 0.11) * 0.55;
        }
        return s;
    }

    /** A landing thud with a click. */
    private static double[] thud(double gain) {
        int n = (int) (0.26 * SR);
        double[] s = new double[n];
        double ph = 0;
        Random r = new Random(5);
        double lp = 0;
        for (int i = 0; i < n; i++) {
            double t = i / (double) SR;
            ph += 2 * Math.PI * (52 + 110 * Math.exp(-t / 0.035)) / SR;
            lp += (r.nextGaussian() - lp) * 0.25;
            s[i] = (Math.sin(ph) * env(t, 0.001, 0.085) * 0.95 + lp * Math.exp(-t / 0.006) * 0.35) * gain;
        }
        return s;
    }

    /** A quick gliding slip (a character sliding one slot over). */
    private static double[] slide(double dir) {
        int n = (int) (0.17 * SR);
        double[] s = new double[n];
        double ph = 0;
        for (int i = 0; i < n; i++) {
            double t = i / (double) SR;
            double u = t / 0.15;
            double fr = dir >= 0 ? 340 + 260 * u : 600 - 260 * u;
            ph += 2 * Math.PI * fr / SR;
            double w = Math.sin(Math.PI * Math.min(1, u));
            s[i] = (Math.sin(ph) * 0.5 + tri(ph * 1.5) * 0.12) * w * 0.38;
        }
        return s;
    }

    /** A handful of tiny high chimes. */
    private static double[] sparkle(Random rnd) {
        int n = (int) (0.34 * SR);
        double[] s = new double[n];
        for (int k = 0; k < 4; k++) {
            double f = 1800 + rnd.nextDouble() * 2400;
            int start = (int) (k * 0.035 * SR);
            for (int i = 0; start + i < n; i++) {
                double t = i / (double) SR;
                s[start + i] += Math.sin(2 * Math.PI * f * t) * env(t, 0.001, 0.06) * 0.2;
            }
        }
        return s;
    }

    /** Filtered noise sweeping between two centre frequencies. */
    private static double[] whoosh(Random rnd, double f0, double f1, double len, double gain) {
        int n = (int) (len * SR);
        double[] s = new double[n];
        double low = 0, band = 0;
        for (int i = 0; i < n; i++) {
            double u = i / (double) n;
            double fc = f0 * Math.pow(f1 / f0, u);
            double ff = 2 * Math.sin(Math.PI * fc / SR);
            double in = rnd.nextGaussian() * 0.5;
            low += ff * band;
            double high = in - low - 0.35 * band;
            band += ff * high;
            s[i] = band * Math.pow(Math.sin(Math.PI * u), 1.6) * gain;
        }
        return s;
    }

    /** A marimba-like note for the finale run. */
    private static double[] marimba(double f) {
        int n = (int) (0.55 * SR);
        double[] s = new double[n];
        for (int i = 0; i < n; i++) {
            double t = i / (double) SR;
            s[i] = (Math.sin(2 * Math.PI * f * t) * Math.exp(-t / 0.22) + Math.sin(2 * Math.PI * f * 4 * t) * 0.35 * Math.exp(-t / 0.05)
                    + Math.sin(2 * Math.PI * f * 10 * t) * 0.12 * Math.exp(-t / 0.02)) * (1 - Math.exp(-t / 0.001)) * 0.5;
        }
        return s;
    }

    /** The "done" flourish: a bright major chord with sparkle on top. */
    private static double[] tada(Random rnd) {
        int n = (int) (1.8 * SR);
        double[] s = new double[n];
        double[] fs = {523.25, 659.25, 783.99, 1046.5};
        for (double f : fs) {
            double[] b = bell(f, 1.8);
            for (int i = 0; i < n && i < b.length; i++) s[i] += b[i] * 0.9;
        }
        double[] sp = sparkle(rnd);
        for (int i = 0; i < sp.length; i++) s[i + (int) (0.05 * SR)] += sp[i] * 1.2;
        return s;
    }

    /** The opening: a rising whoosh landing on a pop. */
    private static double[] intro(Random rnd) {
        int n = (int) (0.7 * SR);
        double[] s = new double[n];
        double[] w = whoosh(rnd, 200, 2400, 0.32, 0.6);
        for (int i = 0; i < w.length; i++) s[i] += w[i];
        double[] p = pop(523.25);
        int off = (int) (0.30 * SR);
        for (int i = 0; i < p.length && off + i < n; i++) s[off + i] += p[i] * 0.9;
        double[] b = bell(1046.5, 0.5);
        for (int i = 0; i < b.length && off + i < n; i++) s[off + i] += b[i] * 0.5;
        return s;
    }

    /** Everything swirling back: a falling whoosh with a few falling notes. */
    private static double[] swirl(Random rnd) {
        int n = (int) (0.9 * SR);
        double[] s = new double[n];
        double[] w = whoosh(rnd, 2800, 260, 0.7, 0.6);
        for (int i = 0; i < w.length; i++) s[i] += w[i];
        double[] notes = {freq(9), freq(7), freq(4), freq(2)};
        for (int k = 0; k < notes.length; k++) {
            double[] m = marimba(notes[k]);
            int off = (int) (k * 0.11 * SR);
            for (int i = 0; i < m.length && off + i < n; i++) s[off + i] += m[i] * 0.45;
        }
        return s;
    }

    /** One typewriter click. */
    private static double[] typeClick(Random rnd) {
        int n = (int) (0.05 * SR);
        double[] s = new double[n];
        double lp = 0;
        for (int i = 0; i < n; i++) {
            double t = i / (double) SR;
            lp += (rnd.nextGaussian() - lp) * 0.6;
            s[i] = (lp * 0.25 + Math.sin(2 * Math.PI * 2400 * t) * 0.18) * Math.exp(-t / 0.008);
        }
        return s;
    }
}
