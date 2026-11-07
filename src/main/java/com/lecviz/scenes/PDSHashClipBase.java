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
import java.util.function.IntUnaryOperator;

/**
 * Kit for the hash-table clips, on top of the list kit:
 *
 *   - HT: a row of table cells with their indexes. probeInsert really inserts a key by open addressing (linear,
 *     quadratic or double hashing): the key moves over the cells it probes, a taken cell flashes red (a collision),
 *     the first empty cell takes the key; it returns the number of collisions.
 *   - CH: the buckets of a chained table, each with a chain of key nodes that grows at its end
 *   - bars: a small bar chart of how many keys fell into each bucket
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public abstract class PDSHashClipBase extends PDSAvlClipBase {

    protected static final Color CELL_C = Colors.BLUE;

    // ── a row of cells ───────────────────────────────────────────────

    protected final class HT {
        final int n;
        final double cx, cy, w, h, pitch;
        final RectMob[] box;
        final TextMob[] val;
        final TextMob[] idx;
        final String[] cont;
        final Color[] col;

        HT(int n, double cx, double cy, double w, double h, double gap) {
            this.n = n;
            this.cx = cx;
            this.cy = cy;
            this.w = w;
            this.h = h;
            this.pitch = w + gap;
            box = new RectMob[n];
            val = new TextMob[n];
            idx = new TextMob[n];
            cont = new String[n];
            col = new Color[n];
            for (int i = 0; i < n; i++) {
                RectMob b = new RectMob(w, h).setCornerRadius(10);
                b.setFillColor(Colors.withAlpha(Colors.WHITE, 0.07));
                b.setStrokeColor(Colors.withAlpha(Colors.GRAY, 0.8));
                b.setStrokeWidth(2.6);
                b.setPosition(x(i), cy);
                b.setOpacity(0);
                add(b);
                box[i] = b;
                val[i] = label("", x(i), cy, Math.min(40, w * 0.42), Colors.WHITE, false, true);
                idx[i] = label(String.valueOf(i), x(i), cy + h / 2 + 26, 26, Colors.GRAY, false, false);
            }
        }

        double x(int i) { return cx + (i - (n - 1) / 2.0) * pitch; }

        List<MObject> parts() {
            List<MObject> l = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                l.add(box[i]);
                l.add(val[i]);
                l.add(idx[i]);
            }
            return l;
        }

        void show(List<Animation> a, double dur) {
            for (int i = 0; i < n; i++) {
                a.add(new FadeIn(box[i], dur));
                a.add(new FadeIn(idx[i], dur));
                if (cont[i] != null) a.add(new FadeIn(val[i], dur));
            }
        }

        private void rest(int i) {
            box[i].setFillColor(col[i] == null ? Colors.withAlpha(Colors.WHITE, 0.07) : Colors.withAlpha(col[i], 0.38));
            box[i].setStrokeColor(col[i] == null ? Colors.withAlpha(Colors.GRAY, 0.8) : Colors.withAlpha(col[i], 0.95));
        }

        /** Sets a cell at once (before it is shown). */
        void set(int i, String s, Color c) {
            cont[i] = s;
            col[i] = c;
            val[i].setText(s == null ? "" : s);
            rest(i);
        }

        /** The cell takes this content, fading in. */
        void put(int i, String s, Color c, double dur) {
            cont[i] = s;
            col[i] = c;
            val[i].setText(s);
            val[i].setOpacity(0);
            List<Animation> a = new ArrayList<>();
            a.add(new FadeIn(val[i], dur));
            a.add(new ColorChange(box[i], Colors.withAlpha(c, 0.38), dur));
            a.add(new ColorChange(box[i], Colors.withAlpha(c, 0.95), dur, ColorChange.Target.STROKE));
            playAll(a);
        }

        /** Empties a cell with a fade. */
        void clear(int i, double dur) {
            cont[i] = null;
            col[i] = null;
            List<Animation> a = new ArrayList<>();
            a.add(new FadeOut(val[i], dur));
            a.add(new ColorChange(box[i], Colors.withAlpha(Colors.WHITE, 0.07), dur));
            a.add(new ColorChange(box[i], Colors.withAlpha(Colors.GRAY, 0.8), dur, ColorChange.Target.STROKE));
            playAll(a);
        }

        void flash(int i, Color c) {
            List<Animation> a = new ArrayList<>();
            a.add(new ColorChange(box[i], Colors.withAlpha(c, 0.55), d(0.25)));
            a.add(new ColorChange(box[i], c, d(0.25), ColorChange.Target.STROKE));
            playAll(a);
        }

        void unflash(int i) {
            List<Animation> a = new ArrayList<>();
            a.add(new ColorChange(box[i], col[i] == null ? Colors.withAlpha(Colors.WHITE, 0.07) : Colors.withAlpha(col[i], 0.38), d(0.25)));
            a.add(new ColorChange(box[i], col[i] == null ? Colors.withAlpha(Colors.GRAY, 0.8) : Colors.withAlpha(col[i], 0.95), d(0.25), ColorChange.Target.STROKE));
            playAll(a);
        }
    }

    // ── open addressing ──────────────────────────────────────────────

    /**
     * Inserts key into t by probing the positions (h + off(i)) % n for i = 0, 1, 2 ... A taken cell is a collision.
     * @param narrate where to write the narration (null for none)
     * @return the number of collisions
     */
    protected int probeInsert(HT t, int key, int h, IntUnaryOperator off, Color keyColor, double sayY, double pace) {
        double ky = t.cy - t.h / 2 - 90;
        List<MObject> chip = chip(String.valueOf(key), t.x(h), ky, Math.max(70, t.w), 54, keyColor, 30);
        List<Animation> in = new ArrayList<>();
        fade(in, chip, d(0.4));
        playAll(in);
        int coll = 0;
        for (int i = 0; i < t.n; i++) {
            int pos = (h + off.applyAsInt(i)) % t.n;
            List<Animation> mv = new ArrayList<>();
            for (MObject m : chip) mv.add(new MoveTo(m, t.x(pos), ky, d(0.4 * pace + 0.1)).setEasing(Easing.EASE_IN_OUT));
            playAll(mv);
            t.flash(pos, Colors.GOLD);
            if (sayY < 900) {
                String msg = i == 0
                        ? key + " % " + t.n + " = " + h + ": look at cell " + pos + "."
                        : "(" + h + " + " + off.applyAsInt(i) + ") % " + t.n + " = " + pos + ": look at cell " + pos + ".";
                sayAt(msg, Colors.LIGHT_GRAY, 0, sayY, 34);
            }
            pause(0.35 * pace);
            if (t.cont[pos] == null) {
                if (sayY < 900) sayAt("Cell " + pos + " is empty: " + key + " goes here.", Colors.GREEN, 0, sayY, 34);
                List<Animation> dn = new ArrayList<>();
                for (MObject m : chip) dn.add(new MoveTo(m, t.x(pos), t.cy, d(0.4 * pace + 0.1)));
                playAll(dn);
                t.put(pos, String.valueOf(key), keyColor, d(0.3));
                List<Animation> out = new ArrayList<>();
                for (MObject m : chip) out.add(new FadeOut(m, d(0.2)));
                playAll(out);
                for (MObject m : chip) remove(m);
                pause(0.25 * pace);
                return coll;
            }
            coll++;
            t.flash(pos, Colors.RED);
            if (sayY < 900) sayAt("Cell " + pos + " is taken by " + t.cont[pos] + ": a collision. Probe again.", Colors.RED, 0, sayY, 34);
            pause(0.4 * pace);
            t.unflash(pos);
        }
        for (MObject m : chip) remove(m);
        return coll;
    }

    // ── chaining ─────────────────────────────────────────────────────

    protected final class CH {
        final int n;
        final double x0, top, pitch;
        final RectMob[] cell;
        final TextMob[] idx;
        final List<List<List<MObject>>> nodes = new ArrayList<>();
        final List<List<Link>> links = new ArrayList<>();
        final List<MObject> all = new ArrayList<>();

        CH(int n, double x0, double top, double pitch) {
            this.n = n;
            this.x0 = x0;
            this.top = top;
            this.pitch = pitch;
            cell = new RectMob[n];
            idx = new TextMob[n];
            for (int i = 0; i < n; i++) {
                RectMob c = new RectMob(70, pitch - 8).setCornerRadius(6);
                c.setFillColor(Colors.withAlpha(Colors.ORANGE, 0.3));
                c.setStrokeColor(Colors.withAlpha(Colors.ORANGE, 0.9));
                c.setStrokeWidth(2.4);
                c.setPosition(x0, y(i));
                c.setOpacity(0);
                PDSHashClipBase.this.add(c);
                cell[i] = c;
                idx[i] = label(String.valueOf(i), x0 - 62, y(i), 26, Colors.GRAY, false, false);
                nodes.add(new ArrayList<>());
                links.add(new ArrayList<>());
                all.add(c);
                all.add(idx[i]);
            }
        }

        double y(int i) { return top + i * pitch; }

        double nx(int k) { return x0 + 130 + k * 120; }

        void show(List<Animation> a, double dur) {
            for (int i = 0; i < n; i++) {
                a.add(new FadeIn(cell[i], dur));
                a.add(new FadeIn(idx[i], dur));
            }
        }

        /** Appends key to the end of bucket b's chain. */
        void add(int key, int b, Color c) { add(String.valueOf(key), b, c); }

        /** Appends a node with the given text (such as "3:4" for neighbour 3 with weight 4). */
        void add(String text, int b, Color c) {
            int k = nodes.get(b).size();
            List<MObject> chip = chip(text, nx(k), y(b), 84, Math.min(54, pitch - 12), c, 28);
            double fromX = k == 0 ? x0 + 38 : nx(k - 1) + 42, toX = nx(k) - 44;
            Link l = arrow(fromX, y(b), toX, y(b), Colors.LIGHT_GRAY, 3);
            List<Animation> a = new ArrayList<>();
            for (MObject m : chip) a.add(new DropIn(m, 50, 0, d(0.5)));
            a.add(new DrawLinkAt(l, 0.1, d(0.4)));
            playAll(a);
            nodes.get(b).add(chip);
            links.get(b).add(l);
            all.addAll(chip);
            all.add(l);
        }

        /**
         * Takes node k out of bucket b's chain: it fades away, the arrow that led into it now leads to the next node,
         * and the nodes behind slide one place forward to close the gap.
         */
        void remove(int b, int k) {
            List<List<MObject>> row = nodes.get(b);
            List<Link> lk = links.get(b);
            int last = row.size() - 1;
            List<Animation> a = new ArrayList<>();
            for (MObject m : row.get(k)) a.add(new FadeOut(m, d(0.6)));
            // the arrow into the removed node stays (it will lead to the next node); the arrow out of it goes
            int gone = k < last ? k + 1 : k;
            a.add(new FadeOut(lk.get(gone), d(0.6)));
            playAll(a);
            pause(0.2);
            List<Animation> slide = new ArrayList<>();
            for (int j = k + 1; j <= last; j++) {
                for (MObject m : row.get(j)) slide.add(new MoveTo(m, nx(j - 1), y(b), d(0.7)).setEasing(Easing.EASE_IN_OUT));
                if (j >= k + 2) {
                    Link l = lk.get(j);
                    double fromX = nx(j - 2) + 42, toX = nx(j - 1) - 44;
                    slide.add(new LinkTo(l, new double[]{fromX, toX}, new double[]{y(b), y(b)}, d(0.7)));
                }
            }
            if (!slide.isEmpty()) playAll(slide);
            row.remove(k);
            lk.remove(gone);
        }

        void lightNode(int b, int k, Color c) {
            List<Animation> a = new ArrayList<>();
            a.add(new ColorChange((MObject) nodes.get(b).get(k).get(0), Colors.withAlpha(c, 0.5), d(0.3)));
            a.add(new ColorChange((MObject) nodes.get(b).get(k).get(0), c, d(0.3), ColorChange.Target.STROKE));
            playAll(a);
        }

        void lightBucket(int b, Color c) {
            List<Animation> a = new ArrayList<>();
            a.add(new ColorChange(cell[b], Colors.withAlpha(c, 0.55), d(0.3)));
            a.add(new ColorChange(cell[b], c, d(0.3), ColorChange.Target.STROKE));
            playAll(a);
        }

        List<MObject> parts() { return all; }
    }

    // ── bars ─────────────────────────────────────────────────────────

    /** A small bar chart: one bar per bucket, bottom at baseY, hidden until grown. Returns the parts. */
    protected List<MObject> bars(int[] counts, double cx, double baseY, double unit, double barW, Color c, String title) {
        List<MObject> made = new ArrayList<>();
        List<Animation> a = new ArrayList<>();
        double gap = barW * 0.35;
        double total = counts.length * barW + (counts.length - 1) * gap;
        for (int i = 0; i < counts.length; i++) {
            double x = cx - total / 2 + i * (barW + gap) + barW / 2;
            double hgt = Math.max(2, counts[i] * unit);
            RectMob r = new RectMob(barW, hgt).setCornerRadius(4);
            r.setFillColor(Colors.withAlpha(c, 0.45));
            r.setStrokeColor(Colors.withAlpha(c, 0.95));
            r.setStrokeWidth(2.2);
            r.setPosition(x, baseY - hgt / 2);
            r.setOpacity(0);
            add(r);
            made.add(r);
            TextMob n = label(String.valueOf(counts[i]), x, baseY - hgt - 20, 24, Colors.WHITE, false, true);
            TextMob ix = label(String.valueOf(i), x, baseY + 22, 22, Colors.GRAY, false, false);
            made.add(n);
            made.add(ix);
            a.add(new FadeInAt(r, 0.08 * i, d(0.5)));
            a.add(new FadeInAt(n, 0.08 * i + 0.2, d(0.4)));
            a.add(new FadeInAt(ix, 0.08 * i, d(0.4)));
        }
        TextMob t = label(title, cx, baseY + 64, 28, Colors.LIGHT_GRAY, false, true);
        made.add(t);
        a.add(new FadeIn(t, d(0.6)));
        playAll(a);
        return made;
    }
}
