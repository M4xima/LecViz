package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Kit for the clips whose trees change shape by rotations (AVL trees and splay trees), on top of the dictionary kit:
 *
 *   - rotRight / rotLeft on the tree model, hgt / bal (height, height(left) - height(right))
 *   - DT: a "dynamic tree" drawn on the tree kit, with a hidden super-root so that the real root may change.
 *     It really performs insertions, deletions and rebalancing, and animates every step: the walk down, the
 *     balance numbers on the way back up, the imbalance, the case, the rotation (all nodes glide to their new
 *     places). A clip only has to say which values to insert or delete.
 *   - TriMob and Fig: a figure made of nodes and subtree triangles (X, Y, Z, A, B ...) that can morph between two
 *     layouts, for the general rotation pictures
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public abstract class PDSAvlClipBase extends PDSDictClipBase {

    // ── model ────────────────────────────────────────────────────────

    /** Height in edges: an empty tree is -1, a single node 0. */
    protected static int hgt(GN n) { return n == null ? -1 : 1 + Math.max(hgt(child(n, 0)), hgt(child(n, 1))); }

    /** height(left) - height(right). */
    protected static int bal(GN n) { return hgt(child(n, 0)) - hgt(child(n, 1)); }

    /** Right rotation at x: its left child moves up. Returns the new top. x's parent may be the hidden super-root. */
    protected static GN rotRight(GN x) {
        GN y = child(x, 0), p = x.parent;
        int slot = x.slot;
        GN yr = child(y, 1);
        // first detach everything that moves, then link it again (a node's side must not change while it is still listed)
        x.kids.remove(y);
        if (yr != null) y.kids.remove(yr);
        p.kids.remove(x);
        setChild(x, 0, yr);
        setChild(y, 1, x);
        setChild(p, slot, y);
        return y;
    }

    /** Left rotation at x: its right child moves up. Returns the new top. */
    protected static GN rotLeft(GN x) {
        GN y = child(x, 1), p = x.parent;
        int slot = x.slot;
        GN yl = child(y, 0);
        x.kids.remove(y);
        if (yl != null) y.kids.remove(yl);
        p.kids.remove(x);
        setChild(x, 1, yl);
        setChild(y, 0, x);
        setChild(p, slot, y);
        return y;
    }

    protected static String bfText(int b) { return b > 0 ? "+" + b : String.valueOf(b); }

    /** True when every node's balance is -1, 0 or +1. */
    protected static boolean isAvl(GN n) {
        if (n == null) return true;
        return Math.abs(bal(n)) <= 1 && isAvl(child(n, 0)) && isAvl(child(n, 1));
    }

    /** The AVL tree made by inserting these values in order, worked out at once (no animation). */
    protected static GN avlOf(int... vals) {
        GN dummy = bin("", null, null);
        for (int v : vals) {
            GN nn = new GN(String.valueOf(v));
            GN n = child(dummy, 1), last = dummy;
            int slot = 1;
            while (n != null) {
                last = n;
                slot = v < val(n) ? 0 : 1;
                n = child(n, slot);
            }
            setChild(last, slot, nn);
            for (GN a = nn.parent; a != null && a != dummy; a = a.parent) {
                int b = bal(a);
                if (Math.abs(b) > 1) {
                    boolean lh = b > 0;
                    GN y = child(a, lh ? 0 : 1);
                    int by = bal(y);
                    boolean dbl = lh ? by < 0 : by > 0;
                    if (!dbl) {
                        if (lh) rotRight(a); else rotLeft(a);
                    } else if (lh) {
                        rotLeft(y);
                        rotRight(a);
                    } else {
                        rotRight(y);
                        rotLeft(a);
                    }
                    break;
                }
            }
        }
        GN root = child(dummy, 1);
        dummy.kids.clear();
        if (root != null) {
            root.parent = null;
            root.slot = -1;
        }
        return root;
    }

    /** A small tree for galleries: blank circles by default, in-order layout. */
    protected GT mini(GN root, double cx, double topY, double r, double slotGap, double levelGap) {
        return new GT(root, cx, topY, levelGap, slotGap, r, 24, false, n -> BST_C, true);
    }

    /** A green tick or a red cross made of two strokes, hidden until DrawLine'd. */
    protected List<LineMob> mark(boolean ok, double x, double y) {
        List<LineMob> l = new ArrayList<>();
        if (ok) {
            l.add(new LineMob(x - 15, y + 2, x - 5, y + 14, Colors.GREEN, 6));
            l.add(new LineMob(x - 5, y + 14, x + 17, y - 15, Colors.GREEN, 6));
        } else {
            l.add(new LineMob(x - 13, y - 13, x + 13, y + 13, Colors.RED, 6));
            l.add(new LineMob(x - 13, y + 13, x + 13, y - 13, Colors.RED, 6));
        }
        for (LineMob m : l) {
            m.setOpacity(0);
            add(m);
        }
        return l;
    }

    /** Draws a mark on, stroke after stroke. */
    protected void drawMark(List<LineMob> m) {
        for (LineMob l : m) play(new DrawLine(l, d(0.22)));
    }

    // ── a subtree triangle ───────────────────────────────────────────

    protected static final class TriMob extends MObject {
        private final double w, h;

        TriMob(double w, double h, Color c) {
            this.w = w;
            this.h = h;
            this.fillColor = Colors.withAlpha(c, 0.32);
            this.strokeColor = Colors.withAlpha(c, 0.95);
            this.strokeWidth = 2.6;
            this.opacity = 0;
        }

        @Override
        protected void draw(GraphicsContext gc) {
            double[] xs = {0, -w / 2, w / 2}, ys = {-h / 2, h / 2, h / 2};
            gc.setFill(fillColor);
            gc.fillPolygon(xs, ys, 3);
            gc.setStroke(strokeColor);
            gc.setLineWidth(strokeWidth);
            gc.strokePolygon(xs, ys, 3);
        }

        @Override
        public MObject copy() {
            TriMob c = new TriMob(w, h, Colors.BLUE);
            copyBaseProperties(c);
            return c;
        }
    }

    // ── a figure of nodes and triangles that morphs between two layouts ──

    protected final class Fig {
        final List<MObject> shapes = new ArrayList<>();
        final List<TextMob> texts = new ArrayList<>();
        final List<Double> rad = new ArrayList<>();      // circle radius, or 0 for a triangle
        final List<Double> hgtOf = new ArrayList<>();    // triangle height, or 0
        final List<Link> edges = new ArrayList<>();
        final List<int[]> ends = new ArrayList<>();
        final double fs;

        Fig(double fs) { this.fs = fs; }

        int node(String label, Color c, double r) {
            CircleMob s = new CircleMob(r);
            s.setFillColor(Colors.withAlpha(c, 0.32));
            s.setStrokeColor(Colors.withAlpha(c, 0.95));
            s.setStrokeWidth(3);
            s.setOpacity(0);
            add(s);
            shapes.add(s);
            texts.add(label(label, 0, 0, fs, Colors.WHITE, false, true));
            rad.add(r);
            hgtOf.add(0.0);
            return shapes.size() - 1;
        }

        int tri(String label, Color c, double w, double h) {
            TriMob s = new TriMob(w, h, c);
            add(s);
            shapes.add(s);
            texts.add(label(label, 0, 0, fs, Colors.WHITE, false, true));
            rad.add(0.0);
            hgtOf.add(h);
            return shapes.size() - 1;
        }

        /** The edges now join these (parent, child) pairs, in the same order; call before morph(). */
        void reconnect(int[][] pairs) {
            for (int e = 0; e < pairs.length; e++) ends.set(e, pairs[e]);
        }

        void edge(int parent, int child) {
            Link l = new Link(new double[]{0, 0}, new double[]{0, 1}, Colors.withAlpha(Colors.LIGHT_GRAY, 0.85), 3, false);
            add(l);
            edges.add(l);
            ends.add(new int[]{parent, child});
        }

        private double[][] route(int e, double[][] xy) {
            int p = ends.get(e)[0], c = ends.get(e)[1];
            double px = xy[p][0], py = xy[p][1], cx = xy[c][0], cy = xy[c][1];
            double ty = rad.get(c) > 0 ? cy - rad.get(c) : cy - hgtOf.get(c) / 2;
            double dx = cx - px, dy = ty - py, len = Math.max(1e-6, Math.hypot(dx, dy));
            double sx = px + dx / len * rad.get(p), sy = py + dy / len * rad.get(p);
            return pts(sx, sy, cx, ty);
        }

        private double textDy(int i) { return rad.get(i) > 0 ? 0 : hgtOf.get(i) * 0.18; }

        /** Puts every part at once (no animation). */
        void place(double[][] xy) {
            for (int i = 0; i < shapes.size(); i++) {
                shapes.get(i).setPosition(xy[i][0], xy[i][1]);
                texts.get(i).setPosition(xy[i][0], xy[i][1] + textDy(i));
            }
            for (int e = 0; e < edges.size(); e++) {
                double[][] q = route(e, xy);
                edges.get(e).setPoints(q[0], q[1]);
            }
        }

        void show(List<Animation> a, double dur) {
            for (MObject m : shapes) a.add(new FadeIn(m, dur));
            for (MObject m : texts) a.add(new FadeIn(m, dur));
            for (Link l : edges) a.add(new DrawLinkAt(l, 0, dur));
        }

        /** Every part glides to its place in the new layout; the edges follow. */
        void morph(List<Animation> a, double[][] xy, double dur) {
            for (int i = 0; i < shapes.size(); i++) {
                a.add(new MoveTo(shapes.get(i), xy[i][0], xy[i][1], dur).setEasing(com.lecviz.utils.Easing.EASE_IN_OUT));
                a.add(new MoveTo(texts.get(i), xy[i][0], xy[i][1] + textDy(i), dur).setEasing(com.lecviz.utils.Easing.EASE_IN_OUT));
            }
            for (int e = 0; e < edges.size(); e++) {
                double[][] q = route(e, xy);
                a.add(new LinkTo(edges.get(e), q[0], q[1], dur));
            }
        }

        void paint(List<Animation> a, int i, Color c, double dur) {
            a.add(new ColorChange(shapes.get(i), Colors.withAlpha(c, 0.45), dur));
            a.add(new ColorChange(shapes.get(i), Colors.withAlpha(c, 0.98), dur, ColorChange.Target.STROKE));
        }

        List<MObject> all() {
            List<MObject> l = new ArrayList<>(shapes);
            l.addAll(texts);
            l.addAll(edges);
            return l;
        }
    }

    // ── the dynamic tree: really inserts, deletes and rebalances, and shows it ──

    protected final class DT {
        final GT t;
        final GN dummy;
        boolean talk = true;
        double sayY = 440;
        double slow = 1.0;            // multiplies every pause
        private double cursor;
        private final List<TextMob> tags = new ArrayList<>();
        /** Called with 0 = remove, 1 = update balances, 2 = imbalance found, 3 = tallest children, 4 = rotate. */
        java.util.function.IntConsumer onStage;

        private void stage(int i) { if (onStage != null) onStage.accept(i); }

        DT(GN realRoot, double cx, double topY, double levelGap, double r, double fs) {
            dummy = bin("", null, realRoot);
            t = new GT(dummy, cx, topY, levelGap, 34, r, fs, false, n -> BST_C, true);
            snap();
        }

        GN root() { return child(dummy, 1); }

        void say(String s, Color c) { if (talk) sayAt(s, c, 0, sayY, 34); }

        void rest(double sec) { pause(sec * slow); }

        private void syncNodes() {
            List<GN> alive = new ArrayList<>();
            preorderInto(dummy, alive);
            t.nodes.retainAll(alive);
        }

        private void place(GN n, int depth) {
            n.depth = depth;
            GN l = child(n, 0), r = child(n, 1);
            if (l != null) place(l, depth + 1);
            n.x = cursor;
            cursor += 2 * t.r + 34;
            if (r != null) place(r, depth + 1);
        }

        private double[][] layout() {
            GN rt = root();
            cursor = 0;
            double dx = t.cx;
            if (rt != null) {
                place(rt, 0);
                List<GN> real = new ArrayList<>();
                preorderInto(rt, real);
                double mn = Double.MAX_VALUE, mx = -Double.MAX_VALUE;
                for (GN n : real) {
                    mn = Math.min(mn, n.x - n.hw);
                    mx = Math.max(mx, n.x + n.hw);
                }
                dx = t.cx - (mn + mx) / 2;
            }
            double[][] xy = new double[t.nodes.size()][2];
            for (int i = 0; i < t.nodes.size(); i++) {
                GN n = t.nodes.get(i);
                if (n == dummy) {
                    xy[i][0] = rt != null ? rt.x + dx : t.cx;
                    xy[i][1] = t.topY - t.levelGap;
                } else {
                    xy[i][0] = n.x + dx;
                    xy[i][1] = t.topY + n.depth * t.levelGap;
                }
            }
            return xy;
        }

        /** Puts every node where the layout wants it, with no animation. */
        void snap() {
            syncNodes();
            double[][] xy = layout();
            for (int i = 0; i < t.nodes.size(); i++) {
                GN n = t.nodes.get(i);
                n.x = xy[i][0];
                n.y = xy[i][1];
                n.shape.setPosition(n.x, n.y);
                n.text.setPosition(n.x, n.y);
            }
            for (GN n : t.nodes) {
                if (n == dummy || n.edge == null) continue;
                double[][] q = t.route(n.parent, n);
                n.edge.setPoints(q[0], q[1]);
            }
        }

        /** Every node glides to the place the layout gives it now; edges follow, and edges to or from the root appear or go. */
        void relayout(List<Animation> a, double dur) {
            syncNodes();
            double[][] xy = layout();
            t.relocate(a, xy, dur);
            for (GN n : t.nodes) {
                if (n == dummy || n.edge == null) continue;
                boolean vis = n.shape.getOpacity() > 0.01, edgeVis = n.edge.getOpacity() > 0.01, want = n.parent != dummy;
                if (vis && want && !edgeVis) a.add(new DrawLinkAt(n.edge, 0, dur));
                if (vis && !want && edgeVis) a.add(new FadeOut(n.edge, dur * 0.6));
            }
        }

        /** The starting tree appears level by level (the hidden super-root and its edge are never shown). */
        void build(double perLevelPause) {
            int h = Math.max(0, hgt(root()));
            for (int dep = 0; dep <= h; dep++) {
                List<Animation> a = new ArrayList<>();
                for (GN n : t.nodes) {
                    if (n == dummy || n.depth != dep) continue;
                    a.add(new FadeInAt(n.shape, 0, d(0.5)));
                    a.add(new FadeInAt(n.text, 0, d(0.5)));
                    if (n.parent != dummy) a.add(new DrawLinkAt(n.edge, 0, d(0.5)));
                }
                playAll(a);
                pause(perLevelPause);
            }
        }

        List<MObject> parts() {
            List<MObject> l = t.parts();
            l.remove(dummy.shape);
            l.remove(dummy.text);
            return l;
        }

        GN find(int v) { return bstFind(root(), v); }

        void plainAll() { PDSAvlClipBase.this.plainAll(t, d(0.4)); }

        /** A new leaf glides out of its parent while the tree makes room; its edge is drawn. */
        void reveal(GN n) {
            List<Animation> a = new ArrayList<>();
            a.add(new FadeIn(n.shape, d(0.7)));
            a.add(new FadeIn(n.text, d(0.7)));
            relayout(a, d(0.9));
            playAll(a);
            if (n.parent != dummy) play(new DrawLinkAt(n.edge, 0, d(0.6)));
            t.pop(n);
        }

        void clearTags() {
            if (tags.isEmpty()) return;
            List<Animation> a = new ArrayList<>();
            for (TextMob g : tags) a.add(new FadeOut(g, d(0.4)));
            playAll(a);
            for (TextMob g : tags) remove(g);
            tags.clear();
        }

        // ── insertion ────────────────────────────────────────────────

        /** Inserts v as a leaf, walking down from the root; returns the new node. No rebalancing. */
        GN insertLeaf(int v) {
            GN n = root(), last = dummy;
            int slot = 1;
            if (n == null) say("The first value becomes the root.", Colors.LIGHT_GRAY);
            while (n != null) {
                lit(t, n, VISIT_C, d(0.3));
                boolean left = v < val(n);
                say(v + (left ? " < " : " > ") + n.name + ": go " + (left ? "left." : "right."), Colors.LIGHT_GRAY);
                rest(0.6);
                last = n;
                slot = left ? 0 : 1;
                n = child(n, slot);
            }
            GN nn = addLeaf(t, last, slot, String.valueOf(v));
            if (last != dummy) say("An empty place: " + v + " hangs here, as a leaf.", Colors.GREEN);
            rest(0.3);
            reveal(nn);
            return nn;
        }

        /** Insert with AVL rebalancing. Returns true when a rotation was needed. */
        boolean insert(int v) {
            GN nn = insertLeaf(v);
            boolean rot = rebalanceUp(nn.parent, true);
            plainAll();
            return rot;
        }

        // ── deletion ─────────────────────────────────────────────────

        /** Deletes v as in a BST (two children: copy the smallest value of the right subtree) and rebalances upward. */
        boolean delete(int v) {
            GN n = find(v);
            if (n == null) {
                say(v + " is not in the tree.", Colors.RED);
                rest(1.0);
                return false;
            }
            say("delete(" + v + "): search for it.", Colors.LIGHT_GRAY);
            List<GN> path = bstPath(root(), v);
            for (int i = 0; i < path.size(); i++) {
                lit(t, path.get(i), i == path.size() - 1 ? GONE_C : VISIT_C, d(0.3));
                rest(0.35);
            }
            stage(0);
            GN par;
            GN l = child(n, 0), r = child(n, 1);
            if (l != null && r != null) {
                say(v + " has two children: copy the smallest value of its right subtree into it.", Colors.ORANGE);
                rest(1.0);
                GN y = r;
                lit(t, y, VISIT_C, d(0.3));
                rest(0.3);
                while (child(y, 0) != null) {
                    y = child(y, 0);
                    lit(t, y, VISIT_C, d(0.3));
                    rest(0.3);
                }
                lit(t, y, FOUND_C, d(0.3));
                t.pop(y);
                copyInto(t, y, n);
                say("Now remove the original " + y.name + ".", Colors.GOLD);
                lit(t, y, GONE_C, d(0.3));
                rest(0.6);
                par = y.parent;
                removeLeafOrOneChild(y);
            } else {
                say(v + (l == null && r == null ? " is a leaf: cut it off." : " has one child: it moves up."), Colors.LIGHT_GRAY);
                rest(0.7);
                par = n.parent;
                removeLeafOrOneChild(n);
            }
            boolean rot = rebalanceUp(par, false);
            plainAll();
            return rot;
        }

        private void removeLeafOrOneChild(GN n) {
            List<Animation> a = new ArrayList<>();
            t.hideNode(a, n, d(0.6));
            splice(n);
            playAll(a);
            List<Animation> b = new ArrayList<>();
            relayout(b, d(0.9));
            playAll(b);
        }

        // ── rebalancing ──────────────────────────────────────────────

        /**
         * Climbs from {@code from} to the root, writing each node's balance. At the first node whose balance is
         * +-2 it shows the case and rotates. After an insertion one rotation is enough (stopAfterFirst);
         * after a deletion the climb goes on to the root.
         */
        boolean rebalanceUp(GN from, boolean stopAfterFirst) {
            boolean rotated = false;
            GN a = from;
            if (a != null && a != dummy) {
                stage(1);
                say("Walk back up and write each node's balance: height(left) - height(right).", Colors.LIGHT_GRAY);
            }
            while (a != null && a != dummy) {
                int b = bal(a);
                boolean bad = Math.abs(b) > 1;
                TextMob tag = t.tag(a, bfText(b), bad ? Colors.RED : Colors.GOLD, 32, 48, -36);
                tags.add(tag);
                List<Animation> an = new ArrayList<>();
                an.add(new FadeIn(tag, d(0.35)));
                t.paint(an, a, bad ? GONE_C : VISIT_C, d(0.35));
                playAll(an);
                rest(bad ? 0.9 : 0.5);
                if (bad) {
                    stage(2);
                    GN top = rebalanceAt(a);
                    rotated = true;
                    if (stopAfterFirst) break;
                    a = top.parent;
                    continue;
                }
                a = a.parent;
            }
            clearTags();
            return rotated;
        }

        /** Climbs from {@code from} writing balances; stops, tags still showing, at the first node with balance +-2. */
        GN findImbalance(GN from) {
            GN a = from;
            say("Walk back up and write each node's balance: height(left) - height(right).", Colors.LIGHT_GRAY);
            while (a != null && a != dummy) {
                int b = bal(a);
                boolean bad = Math.abs(b) > 1;
                TextMob tag = t.tag(a, bfText(b), bad ? Colors.RED : Colors.GOLD, 32, 48, -36);
                tags.add(tag);
                List<Animation> an = new ArrayList<>();
                an.add(new FadeIn(tag, d(0.35)));
                t.paint(an, a, bad ? GONE_C : VISIT_C, d(0.35));
                playAll(an);
                rest(bad ? 0.9 : 0.55);
                if (bad) return a;
                a = a.parent;
            }
            return null;
        }

        /** x has balance +-2: name the case, rotate (once or twice). Returns the new top of the subtree. */
        GN rebalanceAt(GN x) {
            boolean leftHeavy = bal(x) > 0;
            GN y = child(x, leftHeavy ? 0 : 1);
            int by = bal(y);
            boolean dbl = leftHeavy ? by < 0 : by > 0;
            GN z = dbl ? child(y, leftHeavy ? 1 : 0) : child(y, leftHeavy ? 0 : 1);
            int caseNo = leftHeavy ? (dbl ? 2 : 1) : (dbl ? 3 : 4);
            String kind = leftHeavy ? (dbl ? "left-right" : "left-left") : (dbl ? "right-left" : "right-right");
            say("Imbalance at " + x.name + ": " + kind + " (case " + caseNo + "), so a " + (dbl ? "double" : "single") + " rotation.", Colors.ORANGE);
            List<Animation> a = new ArrayList<>();
            t.paint(a, x, GONE_C, d(0.4));
            t.paint(a, y, VISIT_C, d(0.4));
            t.paint(a, z, NEW_C, d(0.4));
            playAll(a);
            stage(3);
            rest(1.3);
            clearTags();
            stage(4);
            GN top;
            if (!dbl) {
                say("Rotate " + (leftHeavy ? "right" : "left") + " at " + x.name + ".", Colors.LIGHT_GRAY);
                top = rotate(x, leftHeavy);
            } else {
                say("First rotate " + (leftHeavy ? "left" : "right") + " at " + y.name + " ...", Colors.LIGHT_GRAY);
                rotate(y, !leftHeavy);
                rest(0.5);
                say("... then rotate " + (leftHeavy ? "right" : "left") + " at " + x.name + ".", Colors.LIGHT_GRAY);
                top = rotate(x, leftHeavy);
            }
            rest(0.6);
            say("Balanced again.", Colors.GREEN);
            plainAll();
            rest(0.5);
            return top;
        }

        // ── splaying ─────────────────────────────────────────────────

        /** Walks down from the root to the node holding v, lighting the path. Returns the node, or null. */
        GN walkTo(int v) {
            GN n = root();
            while (n != null) {
                lit(t, n, VISIT_C, d(0.3));
                if (val(n) == v) break;
                boolean left = v < val(n);
                say(v + (left ? " < " : " > ") + n.name + ": go " + (left ? "left." : "right."), Colors.LIGHT_GRAY);
                rest(0.5);
                n = child(n, left ? 0 : 1);
            }
            if (n == null) {
                say(v + " is not in the tree.", Colors.RED);
                return null;
            }
            lit(t, n, FOUND_C, d(0.3));
            t.pop(n);
            say("Found " + v + ".", Colors.GREEN);
            rest(0.6);
            return n;
        }

        /** Splays x to the root: zig (parent is the root), zig-zig or zig-zag (parent and grandparent). */
        void splay(GN x) {
            while (x.parent != dummy) {
                GN p = x.parent, g = p.parent;
                boolean xl = x.slot == 0;
                if (g == dummy) {
                    say(x.name + "'s parent is the root: rotate " + x.name + " and the root (zig).", Colors.ORANGE);
                    List<Animation> a = new ArrayList<>();
                    t.paint(a, x, FOUND_C, d(0.4));
                    t.paint(a, p, GONE_C, d(0.4));
                    playAll(a);
                    rest(1.0);
                    rotate(p, xl);
                } else {
                    boolean pl = p.slot == 0;
                    List<Animation> a = new ArrayList<>();
                    t.paint(a, x, FOUND_C, d(0.4));
                    t.paint(a, p, VISIT_C, d(0.4));
                    t.paint(a, g, GONE_C, d(0.4));
                    playAll(a);
                    if (xl == pl) {
                        say(x.name + " and its parent are both " + (xl ? "left" : "right") + " children: zig-zig. Rotate the grandparent, then the parent.", Colors.ORANGE);
                        rest(1.2);
                        rotate(g, pl);
                        rest(0.4);
                        rotate(p, xl);
                    } else {
                        say(x.name + " and its parent are on different sides: zig-zag. Rotate the parent, then the grandparent.", Colors.ORANGE);
                        rest(1.2);
                        rotate(p, !xl);
                        rest(0.4);
                        rotate(g, xl);
                    }
                }
                plainAll();
                rest(0.5);
            }
        }

        /** Search v and splay it to the root. */
        void access(int v) {
            GN n = walkTo(v);
            if (n != null) splay(n);
        }

        /** Moves x up to the root by single rotations only: x goes above its parent, again and again. */
        void moveUp(GN x) {
            while (x.parent != dummy) {
                GN p = x.parent;
                boolean xl = x.slot == 0;
                say("Rotate " + (xl ? "right" : "left") + " at " + p.name + ": " + x.name + " goes above " + p.name + ".", Colors.LIGHT_GRAY);
                List<Animation> a = new ArrayList<>();
                t.paint(a, x, FOUND_C, d(0.3));
                t.paint(a, p, GONE_C, d(0.3));
                playAll(a);
                rest(0.5);
                rotate(p, xl);
                plainAll();
                rest(0.4);
            }
        }

        /** One rotation: the model changes, then every node glides to its new place. */
        GN rotate(GN pivot, boolean right) {
            GN top = right ? rotRight(pivot) : rotLeft(pivot);
            List<Animation> a = new ArrayList<>();
            relayout(a, d(1.1));
            playAll(a);
            return top;
        }
    }
}
