package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Easing;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Shared kit for the linked-list clips, on top of the sorting kit (pen-stroke slide text, swipe-off,
 * code boxes, pointers, callouts):
 *
 *   - LNode: a node drawn the textbook way (value cell + next cell with a dot where the pointer
 *     starts, a slash when it is NULL; doubly linked nodes add a prev cell), that moves, recolors
 *     and fades as one object
 *   - Link: an arrow that follows a polyline (straight, or routed around for circular lists),
 *     drawn on progressively and re-routable while nodes move
 *   - C++ syntax colors for the list code, and a one-line narration helper
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public abstract class PDSListClipBase extends PDSSortClipBase {

    // ── C++ list code colors ─────────────────────────────────────────

    private static final Pattern CODE = Pattern.compile(
            "\\b(class|struct|public|void|bool|int|char|for|if|else|while|do|return|new|delete|true|false|NULL|sizeof|"
                    + "Node|List|head|tail|ptr|previous|newptr|toberemoved|current|insert|find|remove|print|size|"
                    + "free|malloc|printf|Element|Polynomial|initialize|add|Stack|Queue|push|pop|search|isEmpty|front|back|peek|enqueue|dequeue)\\b");
    private static final Set<String> KW = Set.of("class", "struct", "public", "void", "bool", "int", "char", "for",
            "if", "else", "while", "do", "return", "new", "delete", "true", "false", "NULL", "sizeof");
    private static final Set<String> VAR = Set.of("head", "tail", "ptr", "previous", "newptr", "toberemoved", "current");

    @Override protected Pattern codeTokens() { return CODE; }
    @Override protected Set<String> codeKeywords() { return KW; }
    @Override protected Set<String> codeVariables() { return VAR; }

    // ── palette ──────────────────────────────────────────────────────

    protected static final Color NODE = Colors.BLUE;
    protected static final Color NEXT_LINK = Colors.LIGHT_GRAY;
    protected static final Color PREV_LINK = Colors.PINK;
    protected static final Color NEW_NODE = Colors.GREEN;
    protected static final Color DOOMED = Colors.RED;
    protected static final Color CURRENT = Colors.ORANGE;

    // ── arrows along a polyline ──────────────────────────────────────

    protected static final class Link extends MObject {
        private double[] xs, ys;
        private double fraction = 1.0;
        private final boolean head;

        Link(double[] xs, double[] ys, Color color, double width, boolean head) {
            this.xs = xs.clone();
            this.ys = ys.clone();
            this.head = head;
            this.strokeColor = color;
            this.strokeWidth = width;
            this.opacity = 0;
        }

        void setFraction(double f) { fraction = Math.max(0, Math.min(1, f)); }
        double[] xs() { return xs; }
        double[] ys() { return ys; }

        void setPoints(double[] nx, double[] ny) {
            xs = nx.clone();
            ys = ny.clone();
        }

        @Override
        protected void draw(GraphicsContext gc) {
            int n = xs.length;
            double total = 0;
            for (int i = 1; i < n; i++) total += Math.hypot(xs[i] - xs[i - 1], ys[i] - ys[i - 1]);
            if (total < 1e-6) return;
            double budget = total * fraction;
            gc.setStroke(strokeColor);
            gc.setFill(strokeColor);
            gc.setLineWidth(strokeWidth);
            gc.setLineCap(StrokeLineCap.ROUND);
            gc.setLineJoin(StrokeLineJoin.ROUND);
            gc.beginPath();
            gc.moveTo(xs[0], ys[0]);
            double ex = xs[0], ey = ys[0], ang = 0;
            for (int i = 1; i < n && budget > 0; i++) {
                double seg = Math.hypot(xs[i] - xs[i - 1], ys[i] - ys[i - 1]);
                if (seg < 1e-9) continue;
                double take = Math.min(seg, budget);
                ex = xs[i - 1] + (xs[i] - xs[i - 1]) * take / seg;
                ey = ys[i - 1] + (ys[i] - ys[i - 1]) * take / seg;
                ang = Math.atan2(ys[i] - ys[i - 1], xs[i] - xs[i - 1]);
                gc.lineTo(ex, ey);
                budget -= take;
            }
            gc.stroke();
            if (head && fraction > 0.25) {
                double hl = 15, ha = Math.toRadians(26);
                gc.fillPolygon(new double[]{ex, ex - hl * Math.cos(ang - ha), ex - hl * Math.cos(ang + ha)},
                        new double[]{ey, ey - hl * Math.sin(ang - ha), ey - hl * Math.sin(ang + ha)}, 3);
            }
        }

        @Override
        public MObject copy() {
            Link c = new Link(xs, ys, strokeColor, strokeWidth, head);
            copyBaseProperties(c);
            c.fraction = fraction;
            return c;
        }
    }

    /** Draws a link on from its start to its end. */
    protected static final class DrawLink extends Animation {
        DrawLink(Link l, double dur) { super(l, dur, Easing.EASE_OUT); }

        @Override public void begin() { ((Link) target).setFraction(0); target.setOpacity(1); }
        @Override public void interpolate(double t) { ((Link) target).setFraction(t); }
    }

    /** Un-draws a link back into its start, then hides it. */
    protected static final class EraseLink extends Animation {
        EraseLink(Link l, double dur) { super(l, dur, Easing.EASE_IN_OUT); }

        @Override public void begin() { ((Link) target).setFraction(1); }

        @Override
        public void interpolate(double t) {
            ((Link) target).setFraction(1 - t);
            if (t >= 1) target.setOpacity(0);
        }
    }

    /** Re-routes a link to new points (same number of points) while nodes move. */
    protected static final class LinkTo extends Animation {
        private final double[] tx, ty;
        private double[] sx, sy;

        LinkTo(Link l, double[] tx, double[] ty, double dur) {
            super(l, dur, Easing.EASE_IN_OUT);
            this.tx = tx.clone();
            this.ty = ty.clone();
        }

        @Override
        public void begin() {
            sx = ((Link) target).xs().clone();
            sy = ((Link) target).ys().clone();
        }

        @Override
        public void interpolate(double t) {
            double[] x = new double[tx.length], y = new double[ty.length];
            for (int i = 0; i < x.length; i++) {
                x[i] = sx[i] + (tx[i] - sx[i]) * t;
                y[i] = sy[i] + (ty[i] - sy[i]) * t;
            }
            ((Link) target).setPoints(x, y);
        }
    }

    // ── nodes ────────────────────────────────────────────────────────

    protected static final double NODE_H = 76;

    /** A list node: value cell plus pointer cell(s). Everything is created hidden. */
    protected final class LNode {
        final boolean dbl;
        final double w;
        final RectMob box, div1, div2;
        final TextMob text;
        final CircleMob nextDot, prevDot;
        final RectMob nextSlash, prevSlash;
        final List<MObject> all = new ArrayList<>();
        final List<double[]> off = new ArrayList<>();
        double x, y;
        boolean nextNull, prevNull;
        String value;

        LNode(String value, double x, double y, Color c, boolean dbl) {
            this.value = value;
            this.dbl = dbl;
            this.x = x;
            this.y = y;
            this.w = dbl ? 176 : 128;
            this.nextNull = false;
            this.prevNull = false;

            box = new RectMob(w, NODE_H).setCornerRadius(12);
            box.setFillColor(Colors.withAlpha(c, 0.22));
            box.setStrokeColor(Colors.withAlpha(c, 0.95));
            box.setStrokeWidth(2.8);
            reg(box, 0, 0);

            double cellL = dbl ? 44 : 44;
            div1 = new RectMob(3, NODE_H - 22).setCornerRadius(1.5);
            div1.setFillColor(Colors.withAlpha(c, 0.8));
            div1.setStrokeColor(Color.TRANSPARENT);
            reg(div1, w / 2 - cellL, 0);

            double valX = dbl ? 0 : -(cellL / 2);
            text = label(value, x + valX, y, 34, Colors.WHITE, false, true);
            reg(text, valX, 0);

            nextDot = new CircleMob(6.5);
            nextDot.setFillColor(c);
            nextDot.setStrokeColor(Color.TRANSPARENT);
            reg(nextDot, w / 2 - cellL / 2, dbl ? -14 : 0);

            nextSlash = new RectMob(34, 3.5).setCornerRadius(1.7);
            nextSlash.setFillColor(Colors.withAlpha(Colors.LIGHT_GRAY, 0.95));
            nextSlash.setStrokeColor(Color.TRANSPARENT);
            nextSlash.setRotation(-0.95);
            reg(nextSlash, w / 2 - cellL / 2, 0);

            if (dbl) {
                div2 = new RectMob(3, NODE_H - 22).setCornerRadius(1.5);
                div2.setFillColor(Colors.withAlpha(c, 0.8));
                div2.setStrokeColor(Color.TRANSPARENT);
                reg(div2, -(w / 2 - cellL), 0);
                prevDot = new CircleMob(6.5);
                prevDot.setFillColor(c);
                prevDot.setStrokeColor(Color.TRANSPARENT);
                reg(prevDot, -(w / 2 - cellL / 2), 14);
                prevSlash = new RectMob(34, 3.5).setCornerRadius(1.7);
                prevSlash.setFillColor(Colors.withAlpha(Colors.LIGHT_GRAY, 0.95));
                prevSlash.setStrokeColor(Color.TRANSPARENT);
                prevSlash.setRotation(-0.95);
                reg(prevSlash, -(w / 2 - cellL / 2), 0);
            } else {
                div2 = null;
                prevDot = null;
                prevSlash = null;
            }
        }

        private void reg(MObject m, double dx, double dy) {
            if (m != text) {
                m.setPosition(x + dx, y + dy);
                m.setOpacity(0);
                add(m);
            } else {
                m.setPosition(x + dx, y + dy);
            }
            all.add(m);
            off.add(new double[]{dx, dy});
        }

        /** The parts that should be visible right now (a NULL cell shows a slash instead of a dot). */
        List<MObject> shown() {
            List<MObject> l = new ArrayList<>();
            for (MObject m : all) {
                if (m == nextDot && nextNull) continue;
                if (m == nextSlash && !nextNull) continue;
                if (m == prevDot && prevNull) continue;
                if (m == prevSlash && !prevNull) continue;
                l.add(m);
            }
            return l;
        }

        /** NULL in the next cell: slash instead of dot. */
        LNode nullNext() {
            nextNull = true;
            return this;
        }

        LNode nullPrev() {
            prevNull = true;
            return this;
        }

        double leftX() { return x - w / 2; }
        double rightX() { return x + w / 2; }
        double nextX() { return x + w / 2 - 22; }
        double nextY() { return y + (dbl ? -14 : 0); }
        double prevX() { return x - w / 2 + 22; }
        double prevY() { return y + 14; }
        double top() { return y - NODE_H / 2; }
        double bottom() { return y + NODE_H / 2; }

        void fadeIn(List<Animation> into, double delay, double dur) {
            for (MObject m : shown()) into.add(new FadeInAt(m, delay, dur));
        }

        void fadeOut(List<Animation> into, double dur) {
            for (MObject m : shown()) if (m.getOpacity() > 0) into.add(new FadeOut(m, dur));
        }

        /** Every part slides (with an optional arc) so the node ends up centered on (nx, ny). */
        void moveTo(List<Animation> into, double nx, double ny, double bulge, double dur) {
            for (int i = 0; i < all.size(); i++) {
                double[] o = off.get(i);
                if (bulge == 0) into.add(new MoveTo(all.get(i), nx + o[0], ny + o[1], dur).setEasing(Easing.EASE_IN_OUT));
                else into.add(new ArcMove(all.get(i), nx + o[0], ny + o[1], bulge, dur));
            }
            x = nx;
            y = ny;
        }

        /** Recolor the node (box and pointer dots). */
        void paint(List<Animation> into, Color c, double dur) {
            into.add(new ColorChange(box, Colors.withAlpha(c, 0.3), dur));
            into.add(new ColorChange(box, Colors.withAlpha(c, 0.95), dur, ColorChange.Target.STROKE));
            into.add(new ColorChange(nextDot, c, dur));
            into.add(new ColorChange(div1, Colors.withAlpha(c, 0.8), dur));
            if (dbl) {
                into.add(new ColorChange(prevDot, c, dur));
                into.add(new ColorChange(div2, Colors.withAlpha(c, 0.8), dur));
            }
        }

        void paintDefault(List<Animation> into, double dur) { paint(into, NODE, dur); }

        /** The slash fades away and the dot fades in (the cell now holds a real pointer). */
        void unNull(List<Animation> into, double dur) {
            nextNull = false;
            into.add(new FadeOut(nextSlash, dur));
            into.add(new FadeIn(nextDot, dur));
        }

        void makeNull(List<Animation> into, double dur) {
            nextNull = true;
            into.add(new FadeOut(nextDot, dur));
            into.add(new FadeIn(nextSlash, dur));
        }

        /** Doubly linked nodes: the prev cell becomes NULL / holds a pointer again. */
        void makePrevNull(List<Animation> into, double dur) {
            prevNull = true;
            into.add(new FadeOut(prevDot, dur));
            into.add(new FadeIn(prevSlash, dur));
        }

        void unPrevNull(List<Animation> into, double dur) {
            prevNull = false;
            into.add(new FadeOut(prevSlash, dur));
            into.add(new FadeIn(prevDot, dur));
        }
    }

    protected LNode node(String value, double x, double y, Color c) { return new LNode(value, x, y, c, false); }

    protected LNode dnode(String value, double x, double y, Color c) { return new LNode(value, x, y, c, true); }

    // ── links between nodes ──────────────────────────────────────────

    protected static double[][] pts(double... xy) {
        double[] x = new double[xy.length / 2], y = new double[xy.length / 2];
        for (int i = 0; i < x.length; i++) {
            x[i] = xy[2 * i];
            y[i] = xy[2 * i + 1];
        }
        return new double[][]{x, y};
    }

    /** Straight route of a.next -> b (from a's pointer dot to b's left edge). */
    protected double[][] nextRoute(LNode a, LNode b) {
        return pts(a.nextX(), a.nextY(), b.leftX() - 3, a.dbl ? a.nextY() : b.y);
    }

    /** Straight route of b.prev -> a (from b's prev dot to a's right edge). */
    protected double[][] prevRoute(LNode b, LNode a) {
        return pts(b.prevX(), b.prevY(), a.rightX() + 3, b.prevY());
    }

    protected Link nextLink(LNode a, LNode b) {
        double[][] p = nextRoute(a, b);
        Link l = new Link(p[0], p[1], NEXT_LINK, 3.2, true);
        add(l);
        return l;
    }

    protected Link prevLink(LNode b, LNode a) {
        double[][] p = prevRoute(b, a);
        Link l = new Link(p[0], p[1], PREV_LINK, 3.2, true);
        add(l);
        return l;
    }

    /** A free-standing arrow between two points. */
    protected Link arrow(double x1, double y1, double x2, double y2, Color c, double w) {
        Link l = new Link(new double[]{x1, x2}, new double[]{y1, y2}, c, w, true);
        add(l);
        return l;
    }

    // ── a row of nodes ───────────────────────────────────────────────

    /** Singly linked row of nodes with links; the head pointer is not included. */
    protected final class Row {
        final List<LNode> nodes = new ArrayList<>();
        final List<Link> links = new ArrayList<>();
        final double pitch;

        Row(String[] vals, double cx, double y, double pitch, Color c) {
            this.pitch = pitch;
            for (int i = 0; i < vals.length; i++) {
                LNode n = node(vals[i], cx + (i - (vals.length - 1) / 2.0) * pitch, y, c);
                if (i == vals.length - 1) n.nullNext();
                nodes.add(n);
            }
            for (int i = 0; i + 1 < nodes.size(); i++) links.add(nextLink(nodes.get(i), nodes.get(i + 1)));
        }

        double slotX(int i, int count) { return nodes.get(0).x + i * pitch; }

        /** Nodes appear left to right, each followed by the arrow that leads to the next one. */
        void build(double stagger, double dur) {
            List<Animation> a = new ArrayList<>();
            for (int i = 0; i < nodes.size(); i++) {
                nodes.get(i).fadeIn(a, stagger * i, dur);
                if (i < links.size()) a.add(new DrawLinkAt(links.get(i), stagger * i + dur * 0.5, dur));
            }
            playAll(a);
        }

        List<MObject> parts() {
            List<MObject> l = new ArrayList<>();
            for (LNode n : nodes) l.addAll(n.all);
            l.addAll(links);
            return l;
        }
    }

    /** Draws a link on after a delay, inside a play() that also does other things. */
    protected static final class DrawLinkAt extends Animation {
        private final double delay, span;

        DrawLinkAt(Link l, double delay, double dur) {
            super(l, delay + dur, Easing.LINEAR);
            this.delay = delay;
            this.span = dur;
        }

        @Override
        public void begin() {
            ((Link) target).setFraction(0);
            target.setOpacity(1);
        }

        @Override
        public void interpolate(double t) {
            double p = Math.max(0, Math.min(1, (t * duration - delay) / span));
            ((Link) target).setFraction(Easing.EASE_OUT.applyAsDouble(p));
        }
    }


    // ── array-like cells and a step counter ──────────────────────────

    /** A boxed value: an array cell or a token that moves around. */
    protected final class Cell {
        final RectMob box;
        final TextMob text;
        double x, y;

        Cell(String v, double x, double y, double w, double h, Color c, double size) {
            this.x = x;
            this.y = y;
            box = panel(x, y, w, h, c, 0.3);
            text = label(v, x, y, size, Colors.WHITE, false, true);
        }

        List<MObject> parts() {
            List<MObject> l = new ArrayList<>();
            l.add(box);
            l.add(text);
            return l;
        }

        void moveTo(List<Animation> into, double nx, double ny, double bulge, double dur) {
            if (bulge == 0) {
                into.add(new MoveTo(box, nx, ny, dur).setEasing(Easing.EASE_IN_OUT));
                into.add(new MoveTo(text, nx, ny, dur).setEasing(Easing.EASE_IN_OUT));
            } else {
                into.add(new ArcMove(box, nx, ny, bulge, dur));
                into.add(new ArcMove(text, nx, ny, bulge, dur));
            }
            x = nx;
            y = ny;
        }

        void color(List<Animation> into, Color c, double dur) {
            into.add(new ColorChange(box, Colors.withAlpha(c, 0.3), dur));
            into.add(new ColorChange(box, Colors.withAlpha(c, 0.85), dur, ColorChange.Target.STROKE));
        }

        void fadeIn(List<Animation> into, double delay, double dur) {
            into.add(new FadeInAt(box, delay, dur));
            into.add(new FadeInAt(text, delay, dur));
        }

        void fadeOut(List<Animation> into, double dur) {
            into.add(new FadeOut(box, dur));
            into.add(new FadeOut(text, dur));
        }
    }

    /** A big step counter: "steps" and a number that ticks up while an operation runs. */
    protected final class Steps {
        final TextMob num, cap;
        int n;

        Steps(double cx, double cy, double size) {
            num = label("0", cx + size * 0.55, cy, size, Colors.WHITE, false, true);
            cap = label("steps", cx - size * 0.75, cy + size * 0.12, size * 0.36, Colors.GRAY, false, true);
        }

        void tick() {
            n++;
            num.setText(String.valueOf(n));
        }

        void reset() {
            n = 0;
            num.setText("0");
        }

        List<MObject> parts() {
            List<MObject> l = new ArrayList<>();
            l.add(num);
            l.add(cap);
            return l;
        }
    }


    /** U-turn route for a reversed pointer: from node a's next dot, under the row, up into node b's bottom. */
    protected double[][] uRoute(LNode a, LNode b) {
        return pts(a.nextX(), a.nextY(), a.nextX(), a.y + 78, b.x + 30, a.y + 78, b.x + 30, b.bottom() + 4);
    }

    /** Reversed pointers alternate: odd indexes go under the row, even indexes over it (so they never overlap). */
    protected double[][] uRouteAlt(LNode a, LNode b, int index) {
        if (index % 2 == 1) return uRoute(a, b);
        return pts(a.nextX(), a.nextY(), a.nextX(), a.y - 135, b.x + 30, a.y - 135, b.x + 30, b.top() - 4);
    }

    /** A call stack drawn as frames that drop in (a call) and fade out with a return value (a return). */
    protected final class CallStack {
        final double x, topY, w, h, gap;
        final List<List<MObject>> frames = new ArrayList<>();
        final List<MObject> all = new ArrayList<>();
        TextMob title;

        CallStack(double x, double topY, double w, double h) {
            this.x = x;
            this.topY = topY;
            this.w = w;
            this.h = h;
            this.gap = 12;
            title = label("call stack", x, topY - 50, 30, Colors.GRAY, false, true);
            all.add(title);
        }

        void showTitle() { play(new FadeIn(title, d(0.5))); }

        List<MObject> push(String text, Color c) {
            double y = topY + frames.size() * (h + gap);
            List<MObject> f = chip(text, x, y, w, h, c, 26);
            List<Animation> a = new ArrayList<>();
            for (MObject m : f) a.add(new DropIn(m, 50, 0, d(0.6)));
            playAll(a);
            frames.add(f);
            all.addAll(f);
            return f;
        }

        /** The top frame returns: its value shows beside it, then the frame goes away. */
        void pop(String ret, Color c) {
            List<MObject> f = frames.remove(frames.size() - 1);
            double y = f.get(0).getPosition().y();
            TextMob r = null;
            if (ret != null) {
                r = label(ret, x + w / 2 + 20, y, 28, c, true, true);
                all.add(r);
                play(new FadeIn(r, d(0.4)));
                pause(0.4);
            }
            List<Animation> a = new ArrayList<>();
            for (MObject m : f) a.add(new FadeOut(m, d(0.5)));
            if (r != null) a.add(new FadeOut(r, d(0.5)));
            playAll(a);
        }

        List<MObject> all() { return all; }
    }


    // ── text helpers ─────────────────────────────────────────────────

    protected LaTeXMob latex(String src, double size, double x, double y) {
        LaTeXMob l = new LaTeXMob(src).setSize((float) size).setLatexColor(Colors.WHITE);
        l.setPosition(x, y);
        l.setOpacity(0);
        add(l);
        return l;
    }

    protected TextMob monoLeft(String text, double x, double y, double size, Color c) {
        TextMob t = label(text, x, y, size, c, true, false);
        t.setFontFamily(MONO);
        return t;
    }

    // ── a vertical stack with a moving "top" arrow ───────────────────

    /** An open-topped container whose items drop in (push) and lift out (pop); a gold arrow follows the top. */
    protected final class VStack {
        final double cx, baseY, w, h, gap = 10;
        final List<Cell> items = new ArrayList<>();
        final Link box, topArrow;
        final TextMob topLab;
        final double arrowY0, labX;

        VStack(double cx, double baseY, double w, double h, int cap, String topName) { this(cx, baseY, w, h, cap, topName, 1); }

        /** side = 1 puts the "top" arrow to the right of the stack, -1 to the left. */
        VStack(double cx, double baseY, double w, double h, int cap, String topName, int side) {
            this.cx = cx;
            this.baseY = baseY;
            this.w = w;
            this.h = h;
            double top = baseY - cap * (h + gap) - 10;
            box = new Link(new double[]{cx - w / 2 - 14, cx - w / 2 - 14, cx + w / 2 + 14, cx + w / 2 + 14},
                    new double[]{top, baseY + 12, baseY + 12, top}, Colors.withAlpha(Colors.WHITE, 0.6), 4, false);
            add(box);
            arrowY0 = slotY(0);
            topArrow = new Link(new double[]{cx + side * (w / 2 + 120), cx + side * (w / 2 + 28)}, new double[]{arrowY0, arrowY0}, Colors.GOLD, 3.6, true);
            add(topArrow);
            labX = cx + side * (w / 2 + 180);
            topLab = label(topName, labX, arrowY0, 30, Colors.GOLD, false, true);
        }

        double slotY(int i) { return baseY - h / 2 - i * (h + gap); }

        List<MObject> parts() {
            List<MObject> l = new ArrayList<>();
            l.add(box);
            l.add(topArrow);
            l.add(topLab);
            for (Cell c : items) l.addAll(c.parts());
            return l;
        }

        void showBox() { play(new DrawLink(box, d(0.8))); }

        void showTop() {
            List<Animation> a = new ArrayList<>();
            moveTop(a, 0.01);
            a.add(new FadeIn(topArrow, d(0.5)));
            a.add(new FadeIn(topLab, d(0.5)));
            playAll(a);
        }

        void moveTop(List<Animation> into, double dur) {
            double y = slotY(items.size() - 1);
            into.add(new MoveTo(topArrow, 0, y - arrowY0, dur).setEasing(Easing.EASE_IN_OUT));
            into.add(new MoveTo(topLab, labX, y, dur).setEasing(Easing.EASE_IN_OUT));
        }

        Cell push(String v, Color c) {
            Cell cell = new Cell(v, cx, slotY(items.size()), w, h, c, Math.min(34, h * 0.56));
            items.add(cell);
            List<Animation> a = new ArrayList<>();
            a.add(new DropIn(cell.box, 130, 0, d(0.6)));
            a.add(new DropIn(cell.text, 130, 0, d(0.6)));
            moveTop(a, d(0.6));
            playAll(a);
            return cell;
        }

        /** The top item leaves to (toX, toY) and stays visible there. */
        Cell popTo(double toX, double toY) {
            Cell cell = items.remove(items.size() - 1);
            List<Animation> a = new ArrayList<>();
            cell.moveTo(a, toX, toY, 70, d(0.8));
            moveTop(a, d(0.8));
            playAll(a);
            return cell;
        }

        /** The top item lifts out and fades away. */
        Cell popAway() {
            Cell cell = items.remove(items.size() - 1);
            List<Animation> a = new ArrayList<>();
            cell.moveTo(a, cx, cell.y - 130, 0, d(0.7));
            cell.fadeOut(a, d(0.7));
            moveTop(a, d(0.7));
            playAll(a);
            return cell;
        }

        Cell top() { return items.get(items.size() - 1); }
    }

    // ── expression trees ─────────────────────────────────────────────

    /** A tree node: a circle with a label (an operator or an operand). */
    protected final class TNode {
        final CircleMob ring;
        final TextMob text;
        final String label;
        final double x, y, r;
        TNode left, right;

        TNode(String label, double x, double y, double r, Color c) {
            this.label = label;
            this.x = x;
            this.y = y;
            this.r = r;
            ring = new CircleMob(r);
            ring.setFillColor(Colors.withAlpha(c, 0.28));
            ring.setStrokeColor(Colors.withAlpha(c, 0.95));
            ring.setStrokeWidth(3);
            ring.setPosition(x, y);
            ring.setOpacity(0);
            add(ring);
            text = label(label, x, y, r * 1.0, Colors.WHITE, false, true);
        }

        List<MObject> parts() {
            List<MObject> l = new ArrayList<>();
            l.add(ring);
            l.add(text);
            return l;
        }

        void fadeIn(List<Animation> into, double delay, double dur) {
            into.add(new FadeInAt(ring, delay, dur));
            into.add(new FadeInAt(text, delay, dur));
        }

        void paint(List<Animation> into, Color c, double dur) {
            into.add(new ColorChange(ring, Colors.withAlpha(c, 0.42), dur));
            into.add(new ColorChange(ring, Colors.withAlpha(c, 0.95), dur, ColorChange.Target.STROKE));
        }
    }

    /** A line from the lower edge of a parent circle to the upper edge of a child circle. */
    protected Link treeEdge(TNode p, TNode c) {
        double dx = c.x - p.x, dy = c.y - p.y, len = Math.hypot(dx, dy);
        double ux = dx / len, uy = dy / len;
        Link l = new Link(new double[]{p.x + ux * p.r, c.x - ux * c.r}, new double[]{p.y + uy * p.r, c.y - uy * c.r},
                Colors.withAlpha(Colors.LIGHT_GRAY, 0.8), 3, false);
        add(l);
        return l;
    }

    /** One token cell in a row. */
    protected Cell tokCell(String t, double x, double y, double w, double h, Color c, double size) {
        return new Cell(t, x, y, w, h, c, size);
    }

    // ── pointers & narration ─────────────────────────────────────────

    /** A pointer label above a node (arrow pointing down at it). */
    protected Ptr above(String name, LNode n, Color c) { return pointer(name, n.x, n.top() - 6, true, c); }

    /** A pointer label below a node (arrow pointing up at it). */
    protected Ptr below(String name, LNode n, Color c) { return pointer(name, n.x, n.bottom() + 6, false, c); }

    private TextMob said;

    /** One narration line at a fixed place; the previous line fades out as the new one fades in. */
    protected void say(String text, Color c, double y, double size) { sayAt(text, c, 0, y, size); }

    protected void sayAt(String text, Color c, double x, double y, double size) {
        TextMob old = said;
        TextMob t = label(text, x, y, size, c, false, false);
        List<Animation> an = new ArrayList<>();
        if (old != null) an.add(new FadeOut(old, d(0.25)));
        an.add(new FadeIn(t, d(0.35)));
        playAll(an);
        if (old != null) remove(old);
        said = t;
    }

    protected void unsay() {
        if (said != null) {
            play(new FadeOut(said, d(0.3)));
            remove(said);
            said = null;
        }
    }

    /** A small caption chip: colored translucent rounded box with a line of text, hidden until faded in. */
    protected List<MObject> chip(String text, double cx, double cy, double w, double h, Color c, double size) {
        RectMob r = new RectMob(w, h).setCornerRadius(h / 2.4);
        r.setFillColor(Colors.withAlpha(c, 0.22));
        r.setStrokeColor(Colors.withAlpha(c, 0.9));
        r.setStrokeWidth(2.2);
        r.setPosition(cx, cy);
        r.setOpacity(0);
        add(r);
        TextMob t = label(text, cx, cy, size, Colors.WHITE, false, true);
        List<MObject> l = new ArrayList<>();
        l.add(r);
        l.add(t);
        return l;
    }

    /** A translucent rounded panel, hidden until faded in. */
    protected RectMob panel(double cx, double cy, double w, double h, Color c, double fill) {
        RectMob r = new RectMob(w, h).setCornerRadius(14);
        r.setFillColor(Colors.withAlpha(c, fill));
        r.setStrokeColor(Colors.withAlpha(c, 0.85));
        r.setStrokeWidth(2.4);
        r.setPosition(cx, cy);
        r.setOpacity(0);
        add(r);
        return r;
    }

    /** Monospaced label (code-like text). */
    protected TextMob mono(String text, double x, double y, double size, Color c) {
        TextMob t = label(text, x, y, size, c, false, false);
        t.setFontFamily(MONO);
        return t;
    }

    protected void fade(List<Animation> into, List<MObject> objs, double dur) {
        for (MObject m : objs) into.add(new FadeIn(m, dur));
    }
}
