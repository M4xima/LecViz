package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Standalone clip for slide 21 of the lists deck: list reversal.
 *
 *   - the slide's points one at a time, then the goal: the same nodes with every arrow flipped and
 *     head at the other end, so a traversal from head gives the opposite order
 *   - classwork 1, the iterative reversal with three pointers: previous, current and next move along
 *     the list while each node's next pointer is turned around (drawn as a U-turn under the row), one
 *     line of code at a time; at the end head = previous
 *   - classwork 2, the recursive reversal: a call stack grows down the list, the last node is the base
 *     case, and the links are turned around on the way back, one return at a time
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListReverseScene extends PDSListClipBase {

    private static final double CX = 350, ROW_Y = -255, PITCH = 165;

    private StrokeTextMob head;
    private CodeBox code;

    @Override
    public void construct() {
        head = writeHeading("List Reversal");
        pause(0.4);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Given a list (SLL, DLL, CSLL, CDLL), reverse it."));
        s.add(ln(0, "The traversal from head should result in the opposite"));
        s.add(ln(3, "order."));
        s.add(ln(0, "Typically need three pointers: previous, current and")
                .kw("previous", Colors.TEAL).kw("current", Colors.ORANGE));
        s.add(ln(3, "next.").kw("next", Colors.PINK));
        s.add(ln(0, "Classwork: Write a list reversal for SLL (sll.cpp).").kw("Classwork", Colors.RED));
        s.add(ln(0, "Classwork: Write a recursive list reversal.").kw("Classwork", Colors.RED));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(1.2);
        swipeAway(text);
        pause(0.4);

        goal();
        iterative();
        recursive();
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void line(int i, double dur) { play(code.moveHl(i, dur)); }

    private Cell cellAt(String v, double x, double y, Color c) { return new Cell(v, x, y, 110, 76, c, 36); }

    // ── the goal: the same nodes, every arrow flipped ────────────────

    private void goal() {
        List<MObject> mine = new ArrayList<>();
        String[] vals = {"4", "2", "7", "2", "9", "5"};
        double[] x = new double[6];
        Cell[] a = new Cell[6], b = new Cell[6];
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            x[i] = (i - 2.5) * 180;
            a[i] = cellAt(vals[i], x[i], -250, Colors.BLUE);
            b[i] = cellAt(vals[i], x[i], -60, Colors.BLUE);
            a[i].fadeIn(in, 0.12 * i, d(0.5));
            b[i].fadeIn(in, 0.12 * i + 0.4, d(0.5));
            mine.addAll(a[i].parts());
            mine.addAll(b[i].parts());
        }
        for (int i = 0; i < 5; i++) {
            Link r = arrow(x[i] + 58, -250, x[i + 1] - 58, -250, NEXT_LINK, 3.2);
            Link l = arrow(x[i + 1] - 58, -60, x[i] + 58, -60, PREV_LINK, 3.2);
            in.add(new DrawLinkAt(r, 0.12 * i + 0.3, d(0.5)));
            in.add(new DrawLinkAt(l, 0.12 * i + 0.7, d(0.5)));
            mine.add(r);
            mine.add(l);
        }
        Link h1 = arrow(x[0] - 190, -250, x[0] - 58, -250, Colors.GOLD, 3.6);
        TextMob h1l = label("head", x[0] - 125, -285, 30, Colors.GOLD, false, true);
        Link h2 = arrow(x[5] + 190, -60, x[5] + 58, -60, Colors.GOLD, 3.6);
        TextMob h2l = label("head", x[5] + 125, -95, 30, Colors.GOLD, false, true);
        in.add(new DrawLinkAt(h1, 0.3, d(0.6)));
        in.add(new FadeInAt(h1l, 0.3, d(0.6)));
        in.add(new DrawLinkAt(h2, 1.0, d(0.6)));
        in.add(new FadeInAt(h2l, 1.0, d(0.6)));
        mine.add(h1);
        mine.add(h1l);
        mine.add(h2);
        mine.add(h2l);
        playAll(in);
        pause(0.6);
        TextMob t1 = mono("from head:  4 2 7 2 9 5", 0, 90, 44, Colors.TEAL);
        TextMob t2 = mono("from head:  5 9 2 7 2 4", 0, 190, 44, Colors.PINK);
        play(new FadeIn(t1, d(0.6)));
        play(new FadeIn(t2, d(0.6)));
        mine.add(t1);
        mine.add(t2);
        StrokeTextMob cap = stroke("Reversed: the traversal from head gives the opposite order.", 0, 320, 40, Colors.WHITE, false);
        play(new Write(cap, d(3.0)));
        mine.add(cap);
        pause(2.6);
        fadeOutAll(d(0.9), mine);
        pause(0.3);
    }

    // ── classwork 1: three pointers ──────────────────────────────────

    private void iterative() {
        List<MObject> everything = new ArrayList<>();
        Row row = new Row(new String[]{"4", "2", "7", "2", "9", "5"}, CX, ROW_Y, PITCH, NODE);
        LNode n0 = row.nodes.get(0), n5 = row.nodes.get(5);
        Link headArrow = arrow(n0.leftX() - 120, n0.y, n0.leftX() - 3, n0.y, Colors.GOLD, 3.6);
        TextMob headLab = label("head", n0.leftX() - 70, n0.y - 36, 30, Colors.GOLD, false, true);
        play(new DrawLink(headArrow, d(0.6)), new FadeIn(headLab, d(0.6)));
        row.build(d(0.3), d(0.6));
        everything.addAll(row.parts());
        everything.add(headArrow);
        everything.add(headLab);

        StrokeTextMob cw = stroke("Classwork: reverse a singly linked list.", CX + 100, -110, 42, Colors.ORANGE, false);
        play(new Write(cw, d(2.4)));
        everything.add(cw);
        code = new CodeBox(new String[]{
                "Node *previous = NULL;",
                "Node *current = head;",
                "Node *next;",
                "while (current) {",
                "  next = current->next;",
                "  current->next = previous;",
                "  previous = current;",
                "  current = next;",
                "}",
                "head = previous;"}, -880, -380, 26, 40);
        code.typeIn(4.0);
        everything.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.4)));
        pause(0.5);

        double nullX = n5.rightX() + 60;
        TextMob nullLab = mono("NULL", nullX, ROW_Y, 26, Colors.GRAY);
        play(new FadeIn(nullLab, d(0.5)));
        everything.add(nullLab);
        double parkX = -310;
        Ptr prev = pointer("previous", parkX, ROW_Y - 44, true, Colors.TEAL);
        prev.lab.setFontSize(22);
        TextMob nullTag = mono("NULL", parkX, ROW_Y - 8, 22, Colors.GRAY);
        line(0, d(0.1));
        play(new FadeIn(prev.arrow, d(0.4)), new FadeIn(prev.lab, d(0.4)), new FadeIn(nullTag, d(0.4)));
        sayAt("previous = NULL", Colors.LIGHT_GRAY, CX + 100, -35, 36);
        pause(0.6);
        line(1, d(0.4));
        Ptr cur = pointer("current", n0.x - 35, n0.top() - 6, true, Colors.ORANGE);
        cur.lab.setFontSize(22);
        play(new FadeIn(cur.arrow, d(0.4)), new FadeIn(cur.lab, d(0.4)));
        sayAt("current = head", Colors.LIGHT_GRAY, CX + 100, -35, 36);
        pause(0.6);
        line(2, d(0.4));
        Ptr nxt = pointer("next", row.nodes.get(1).x - 35, n0.top() - 6, true, Colors.PINK);
        nxt.lab.setFontSize(22);
        everything.addAll(prev.parts());
        everything.addAll(cur.parts());
        everything.addAll(nxt.parts());
        everything.add(nullTag);
        pause(0.4);

        List<Link> flips = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            double sp = i == 0 ? 1.0 : (i == 1 ? 0.65 : 0.4);
            LNode n = row.nodes.get(i);
            line(3, d(0.3 * sp));
            if (i < 2) sayAt("current is not NULL: keep going", Colors.LIGHT_GRAY, CX + 100, -35, 36);
            pause(0.35 * sp);

            // next = current->next
            line(4, d(0.3 * sp));
            List<Animation> a1 = new ArrayList<>();
            double nxX = i < 5 ? row.nodes.get(i + 1).x : nullX;
            if (i == 0) {
                a1.add(new FadeIn(nxt.arrow, d(0.4 * sp)));
                a1.add(new FadeIn(nxt.lab, d(0.4 * sp)));
            } else {
                nxt.go(a1, nxX - 35, d(0.5 * sp));
            }
            playAll(a1);
            if (i < 2) sayAt("next remembers the rest of the list", Colors.PINK, CX + 100, -35, 36);
            pause(0.4 * sp);

            // current->next = previous: the arrow turns around
            line(5, d(0.3 * sp));
            List<Animation> a2 = new ArrayList<>();
            if (i < 5) a2.add(new EraseLink(row.links.get(i), d(0.5 * sp)));
            if (i == 0) {
                n.makeNull(a2, d(0.5 * sp));
            } else {
                if (n.nextNull) n.unNull(a2, d(0.4 * sp));
                double[][] r = uRouteAlt(n, row.nodes.get(i - 1), i);
                Link u = new Link(r[0], r[1], PREV_LINK, 3.4, true);
                add(u);
                a2.add(new DrawLinkAt(u, 0.1, d(0.8 * sp)));
                flips.add(u);
                everything.add(u);
            }
            playAll(a2);
            if (i < 2) sayAt("current->next = previous: this arrow turns around", Colors.GREEN, CX + 100, -35, 36);
            pause(0.4 * sp);

            // previous = current
            line(6, d(0.3 * sp));
            List<Animation> a3 = new ArrayList<>();
            prev.go(a3, n.x - 35, d(0.5 * sp));
            if (i == 0) a3.add(new FadeOut(nullTag, d(0.3 * sp)));
            playAll(a3);

            // current = next
            line(7, d(0.3 * sp));
            List<Animation> a4 = new ArrayList<>();
            cur.go(a4, nxX - 35, d(0.5 * sp));
            playAll(a4);
            pause(0.2 * sp);
        }
        line(3, d(0.3));
        sayAt("current is NULL: the loop ends", Colors.GREEN, CX + 100, -35, 36);
        pause(1.0);

        // head = previous
        line(9, d(0.5));
        play(new FadeOut(prev.arrow, d(0.5)), new FadeOut(prev.lab, d(0.5)));
        Ptr newHead = above("head", n5, Colors.GOLD);
        List<Animation> hd = new ArrayList<>();
        hd.add(new FadeOut(headArrow, d(0.6)));
        hd.add(new FadeOut(headLab, d(0.6)));
        hd.add(new FadeIn(newHead.arrow, d(0.7)));
        hd.add(new FadeIn(newHead.lab, d(0.7)));
        hd.add(new FadeOut(nxt.arrow, d(0.5)));
        hd.add(new FadeOut(nxt.lab, d(0.5)));
        hd.add(new FadeOut(cur.arrow, d(0.5)));
        hd.add(new FadeOut(cur.lab, d(0.5)));
        hd.add(new FadeOut(nullLab, d(0.5)));
        playAll(hd);
        everything.addAll(newHead.parts());
        sayAt("head = previous: the old last node is now the first", Colors.GREEN, CX + 100, -35, 36);
        pause(1.0);
        TextMob outT = mono("from head:  5 9 2 7 2 4", CX + 100, 80, 50, Colors.GOLD);
        play(new FadeIn(outT, d(0.7)));
        everything.add(outT);
        StrokeTextMob cost = stroke("One pass over the list: O(N) time, three pointers.", CX + 100, 200, 38, Colors.ORANGE, false);
        play(new Write(cost, d(2.8)));
        everything.add(cost);
        pause(2.6);
        fadeOutAll(d(1.2), everything);
        unsay();
        pause(0.4);
    }

    // ── classwork 2: the recursive reversal ──────────────────────────

    private void recursive() {
        List<MObject> everything = new ArrayList<>();
        StrokeTextMob cw = stroke("Classwork: a recursive list reversal.", 0, -405, 44, Colors.ORANGE, false);
        play(new Write(cw, d(2.4)));
        everything.add(cw);
        code = new CodeBox(new String[]{
                "Node *reverse(Node *p) {",
                "  if (p == NULL || p->next == NULL) return p;",
                "  Node *rest = reverse(p->next);",
                "  p->next->next = p;",
                "  p->next = NULL;",
                "  return rest;",
                "}"}, -880, -330, 26, 42);
        code.typeIn(3.6);
        everything.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.4)));

        Row row = new Row(new String[]{"4", "2", "7", "9"}, 480, -255, PITCH, NODE);
        LNode n0 = row.nodes.get(0);
        Link headArrow = arrow(n0.leftX() - 110, n0.y, n0.leftX() - 3, n0.y, Colors.GOLD, 3.6);
        TextMob headLab = label("head", n0.leftX() - 62, n0.y - 36, 30, Colors.GOLD, false, true);
        play(new DrawLink(headArrow, d(0.6)), new FadeIn(headLab, d(0.6)));
        row.build(d(0.3), d(0.6));
        everything.addAll(row.parts());
        everything.add(headArrow);
        everything.add(headLab);

        CallStack stack = new CallStack(560, -20, 300, 56);
        stack.showTitle();
        everything.addAll(stack.all());
        Ptr p = pointer("p", n0.x - 35, n0.top() - 6, true, Colors.ORANGE);
        play(new FadeIn(p.arrow, d(0.4)), new FadeIn(p.lab, d(0.4)));
        everything.addAll(p.parts());
        pause(0.6);

        String[] vals = {"4", "2", "7", "9"};
        Color[] fc = {Colors.TEAL, Colors.BLUE, Colors.PINK, Colors.GOLD};
        // down: each call reverses the rest of the list first
        for (int k = 0; k < 4; k++) {
            List<Animation> mv = new ArrayList<>();
            if (k > 0) p.go(mv, row.nodes.get(k).x - 35, d(0.5));
            mv.add(code.moveHl(k < 3 ? 2 : 1, d(0.3)));
            playAll(mv);
            stack.push("reverse(p = " + vals[k] + ")", fc[k]);
            everything.addAll(stack.all());
            if (k < 3) {
                sayAt("rest = reverse(p->next): first reverse the rest", Colors.LIGHT_GRAY, 480, 400, 32);
                pause(0.6);
            }
        }
        line(1, d(0.3));
        sayAt("p->next is NULL: base case, return p", Colors.GREEN, 480, 400, 32);
        pause(0.9);
        stack.pop("returns 9", Colors.GREEN);
        pause(0.4);

        // up: turn each link around on the way back
        for (int k = 2; k >= 0; k--) {
            LNode n = row.nodes.get(k), b = row.nodes.get(k + 1);
            List<Animation> mv = new ArrayList<>();
            p.go(mv, n.x - 35, d(0.5));
            mv.add(code.moveHl(3, d(0.4)));
            playAll(mv);
            sayAt("p->next->next = p: the next node now points back", Colors.GREEN, 480, 400, 32);
            List<Animation> fl = new ArrayList<>();
            if (b.nextNull) b.unNull(fl, d(0.4));
            double[][] r = uRouteAlt(b, n, k + 1);
            Link u = new Link(r[0], r[1], PREV_LINK, 3.4, true);
            add(u);
            fl.add(new DrawLinkAt(u, 0.1, d(0.8)));
            playAll(fl);
            everything.add(u);
            pause(0.5);
            line(4, d(0.4));
            sayAt("p->next = NULL: cut the old forward link", Colors.LIGHT_GRAY, 480, 400, 32);
            List<Animation> cut = new ArrayList<>();
            cut.add(new EraseLink(row.links.get(k), d(0.6)));
            n.makeNull(cut, d(0.5));
            playAll(cut);
            pause(0.4);
            line(5, d(0.4));
            stack.pop("returns 9", Colors.GREEN);
            pause(0.3);
        }

        // head = reverse(head)
        Ptr nh = above("head", row.nodes.get(3), Colors.GOLD);
        List<Animation> hd = new ArrayList<>();
        hd.add(new FadeOut(headArrow, d(0.6)));
        hd.add(new FadeOut(headLab, d(0.6)));
        hd.add(new FadeIn(nh.arrow, d(0.7)));
        hd.add(new FadeIn(nh.lab, d(0.7)));
        hd.add(new FadeOut(p.arrow, d(0.5)));
        hd.add(new FadeOut(p.lab, d(0.5)));
        playAll(hd);
        everything.addAll(nh.parts());
        sayAt("head = reverse(head): 9 7 2 4", Colors.GREEN, 480, 400, 36);
        pause(1.2);
        StrokeTextMob note = stroke("Recursion uses one stack frame per node.", 480, 470, 34, Colors.ORANGE, false);
        play(new Write(note, d(2.4)));
        everything.add(note);
        pause(2.6);
        fadeOutAll(d(1.2), everything);
        unsay();
        pause(0.4);
    }
}
