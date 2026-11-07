package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Animation;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Shared kit for the dictionary clips (binary search trees, AVL, splay, hash tables), on top of the tree kit:
 *
 *   - a binary search tree model on the tree kit's GN nodes: insert, find, search path, min / max, splice
 *     (child takes the place of its parent), so a clip can build the final tree and replay how it grew
 *   - bst(): a tree drawn with in-order layout (a smaller value always hangs to the left), one color
 *   - lit / plain: light a node and the edge into it, and put it back
 *   - bstRelayout: after a clip changed the model (a removal), the remaining nodes glide to their new places
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public abstract class PDSDictClipBase extends PDSTreeClipBase {

    protected static final Color BST_C = Colors.TEAL;      // an ordinary node
    protected static final Color NEW_C = Colors.PINK;      // a node that has just been inserted
    protected static final Color GONE_C = Colors.RED;      // a node about to be removed
    protected static final Color FOUND_C = Colors.GREEN;   // the node a search ends on
    protected static final Color EDGE_C = Colors.withAlpha(Colors.LIGHT_GRAY, 0.8);

    // ── binary search tree model ─────────────────────────────────────

    protected static int val(GN n) { return Integer.parseInt(n.name); }

    protected static GN child(GN p, int slot) {
        for (GN k : p.kids) if (k.slot == slot) return k;
        return null;
    }

    /** Puts c under p on the given side (0 = left, 1 = right), replacing whatever was there. c may be null. */
    protected static void setChild(GN p, int slot, GN c) {
        GN old = child(p, slot);
        if (old != null) p.kids.remove(old);
        if (c != null) {
            c.parent = p;
            c.slot = slot;
            p.kids.add(c);
            p.kids.sort(Comparator.comparingInt(k -> k.slot));
        }
    }

    protected static GN bstInsert(GN root, int v) {
        GN nn = new GN(String.valueOf(v));
        if (root == null) return nn;
        GN p = root;
        while (true) {
            int slot = v < val(p) ? 0 : 1;
            GN c = child(p, slot);
            if (c == null) {
                setChild(p, slot, nn);
                break;
            }
            p = c;
        }
        return root;
    }

    /** The BST made by inserting the values in this order. */
    protected static GN bstOf(int... order) {
        GN root = null;
        for (int v : order) root = bstInsert(root, v);
        return root;
    }

    protected static GN bstFind(GN root, int v) {
        GN n = root;
        while (n != null && val(n) != v) n = child(n, v < val(n) ? 0 : 1);
        return n;
    }

    /** The nodes a search for v compares against, root first (ends on the node itself, or on the last node before NULL). */
    protected static List<GN> bstPath(GN root, int v) {
        List<GN> path = new ArrayList<>();
        GN n = root;
        while (n != null) {
            path.add(n);
            if (val(n) == v) break;
            n = child(n, v < val(n) ? 0 : 1);
        }
        return path;
    }

    protected static GN bstMin(GN n) {
        while (child(n, 0) != null) n = child(n, 0);
        return n;
    }

    protected static GN bstMax(GN n) {
        while (child(n, 1) != null) n = child(n, 1);
        return n;
    }

    /** Takes a node with at most one child out of the model: its child (if any) takes its place. Returns that child. */
    protected static GN splice(GN n) {
        GN c = n.kids.isEmpty() ? null : n.kids.get(0);
        GN p = n.parent;
        int slot = n.slot;
        p.kids.remove(n);
        if (c != null) setChild(p, slot, c);
        n.parent = null;
        return c;
    }

    // ── drawing ──────────────────────────────────────────────────────

    /** A BST drawn by in-order position: all nodes one color, every smaller value hanging to the left. */
    protected GT bst(GN root, double cx, double topY, double levelGap, double r, double fs) {
        return new GT(root, cx, topY, levelGap, 34, r, fs, false, n -> BST_C, true);
    }

    /** Lights a node and the edge that leads into it. */
    protected void lit(GT t, GN n, Color c, double dur) {
        List<Animation> a = new ArrayList<>();
        t.paint(a, n, c, dur);
        t.paintEdge(a, n, c, dur);
        playAll(a);
    }

    /** Puts a node and its edge back to the plain look. */
    protected void plain(GT t, List<Animation> a, GN n, double dur) {
        t.paint(a, n, BST_C, dur);
        t.paintEdge(a, n, EDGE_C, dur);
    }

    protected void plainAll(GT t, double dur) {
        List<Animation> a = new ArrayList<>();
        for (GN n : t.nodes) plain(t, a, n, dur);
        playAll(a);
    }

    private double bstCursor;

    private void bstPlace(GN n, double pitch, int depth) {
        n.depth = depth;
        GN l = leftOf(n), r = rightOf(n);
        if (l != null) bstPlace(l, pitch, depth + 1);
        n.x = bstCursor;
        bstCursor += pitch;
        if (r != null) bstPlace(r, pitch, depth + 1);
    }

    /** After the model changed (nodes removed or moved up), lays what is left out again and glides every node there. */
    protected void bstRelayout(GT t, List<Animation> a, double dur) {
        List<GN> alive = new ArrayList<>();
        preorderInto(t.root, alive);
        t.nodes.retainAll(alive);
        bstCursor = 0;
        bstPlace(t.root, 2 * t.r + 34, 0);
        double mn = Double.MAX_VALUE, mx = -Double.MAX_VALUE;
        for (GN n : t.nodes) {
            mn = Math.min(mn, n.x - n.hw);
            mx = Math.max(mx, n.x + n.hw);
        }
        double dx = t.cx - (mn + mx) / 2;
        double[][] xy = new double[t.nodes.size()][2];
        for (int i = 0; i < t.nodes.size(); i++) {
            GN n = t.nodes.get(i);
            xy[i][0] = n.x + dx;
            xy[i][1] = t.topY + n.depth * t.levelGap;
        }
        t.relocate(a, xy, dur);
    }

    /** Fades a node out together with the edge into it. */
    protected void vanish(GT t, List<Animation> a, GN n, double dur) {
        t.hideNode(a, n, dur);
    }

    // ── changing a tree that is already on screen ────────────────────

    /** A node with at most one child is cut out: it fades, its child moves up, the tree glides to its new shape. */
    protected void cutOut(GT t, GN n) {
        List<Animation> a = new ArrayList<>();
        t.hideNode(a, n, d(0.6));
        splice(n);
        playAll(a);
        List<Animation> b = new ArrayList<>();
        bstRelayout(t, b, d(0.9));
        for (GN m : t.nodes) plain(t, b, m, d(0.5));
        playAll(b);
    }

    /** A copy of y's value flies to node x, and x takes that value (x's own value fades out first). */
    protected void copyInto(GT t, GN y, GN x) {
        TextMob fly = label(y.name, y.x, y.y, t.fs, Colors.GOLD, false, true);
        play(new FadeOut(x.text, d(0.3)), new FadeIn(fly, d(0.3)));
        play(new MoveTo(fly, x.x, x.y, d(1.0)).setEasing(com.lecviz.utils.Easing.EASE_IN_OUT));
        x.name = y.name;
        x.text.setText(y.name);
        x.text.setOpacity(1);
        remove(fly);
        lit(t, x, FOUND_C, d(0.4));
        t.pop(x);
    }

    /** Hangs a new, hidden leaf under parent (slot 0 = left, 1 = right); settle() brings it in. */
    protected GN addLeaf(GT t, GN parent, int slot, String name) {
        GN n = new GN(name);
        setChild(parent, slot, n);
        n.depth = parent.depth + 1;
        n.hh = t.r;
        n.hw = t.r;
        n.color = BST_C;
        n.x = parent.x;
        n.y = parent.y + 4;   // not exactly on the parent: the edge direction would be 0 / 0
        CircleMob s = new CircleMob(t.r);
        s.setFillColor(Colors.withAlpha(NEW_C, 0.42));
        s.setStrokeColor(Colors.withAlpha(NEW_C, 0.98));
        s.setStrokeWidth(3);
        s.setPosition(parent.x, parent.y);
        s.setOpacity(0);
        add(s);
        n.shape = s;
        n.text = label(name, parent.x, parent.y, t.fs, Colors.WHITE, false, true);
        double[][] q = t.route(parent, n);
        n.edge = new Link(q[0], q[1], NEW_C, 3, false);
        add(n.edge);
        t.nodes.add(n);
        return n;
    }

    /** The new leaf glides out of its parent while the tree makes room, then its edge is drawn. */
    protected void settle(GT t, GN n) {
        List<Animation> a = new ArrayList<>();
        a.add(new FadeIn(n.shape, d(0.7)));
        a.add(new FadeIn(n.text, d(0.7)));
        bstRelayout(t, a, d(0.9));
        playAll(a);
        play(new DrawLinkAt(n.edge, 0, d(0.6)));
        t.pop(n);
        List<Animation> b = new ArrayList<>();
        for (GN m : t.nodes) plain(t, b, m, d(0.5));
        playAll(b);
    }

    /** A named pointer (small arrow plus label) that sits above a node and glides from node to node. */
    protected final class NodePtr {
        final ArrowMob arrow;
        final TextMob lab;
        final double bx, by;

        NodePtr(String name, GN n, Color c) {
            bx = n.x;
            by = n.y - n.hh - 8;
            arrow = new ArrowMob(bx, by - 38, bx, by);
            arrow.setHeadLength(11);
            arrow.setStrokeColor(c);
            arrow.setStrokeWidth(3);
            arrow.setOpacity(0);
            add(arrow);
            lab = label(name, bx, by - 60, 28, c, false, true);
        }

        void show(List<Animation> into, double dur) {
            into.add(new FadeIn(arrow, dur));
            into.add(new FadeIn(lab, dur));
        }

        void go(List<Animation> into, GN n, double dur) {
            double tx = n.x, ty = n.y - n.hh - 8;
            into.add(new MoveTo(arrow, tx - bx, ty - by, dur).setEasing(com.lecviz.utils.Easing.EASE_IN_OUT));
            into.add(new MoveTo(lab, tx, ty - 60, dur).setEasing(com.lecviz.utils.Easing.EASE_IN_OUT));
        }

        List<com.lecviz.core.MObject> parts() {
            List<com.lecviz.core.MObject> l = new ArrayList<>();
            l.add(arrow);
            l.add(lab);
            return l;
        }
    }
}
