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
 * Standalone clip for slides 7-8 of the lists deck: the List ADT on a linked list, and arrays
 * versus linked lists.
 *
 *   Slide 7  the same operations run on a linked list while a big step counter counts the work:
 *            insert walks to the last node (N) unless a tail pointer jumps straight there (1), find
 *            and print walk the links (N), remove finds the node and changes one pointer, size reads
 *            a counter (1); then the slide's question: if the costs are the same, why linked lists?
 *   Slide 8  the four rows of the comparison, array on the left and list on the right: reallocation
 *            copies every element but a list only adds a link, removal and insertion shift elements
 *            but a list re-points pointers, concatenation copies N + M but a list joins with one link
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListLinkedScene extends PDSListClipBase {

    private static final double CX = 380, ROW_Y = -285, PITCH = 175;

    private StrokeTextMob head;
    private StrokeTextMob cap;

    @Override
    public void construct() {
        linkedList();
        arraysVsLists();
    }

    // ── helpers ──────────────────────────────────────────────────────

    private void caption(String text, Color c) {
        StrokeTextMob n = stroke(text, CX, -95, 36, c, false);
        if (cap != null) {
            play(new FadeOut(cap, d(0.3)));
            remove(cap);
        }
        play(new Write(n, d(Math.max(1.4, text.length() * 0.05))));
        cap = n;
    }

    private void dropCaption() {
        if (cap != null) {
            play(new FadeOut(cap, d(0.3)));
            remove(cap);
            cap = null;
        }
    }

    private void show(List<MObject> parts) {
        List<Animation> a = new ArrayList<>();
        fade(a, parts, d(0.5));
        playAll(a);
    }

    private void paintNow(LNode n, Color c) {
        List<Animation> a = new ArrayList<>();
        n.paint(a, c, 0.01);
        playAll(a);
    }

    private void paintFade(LNode n, Color c, double dur) {
        List<Animation> a = new ArrayList<>();
        n.paint(a, c, dur);
        playAll(a);
    }

    // ── slide 7 ──────────────────────────────────────────────────────

    private void linkedList() {
        head = writeHeading("List using Linked List");
        pause(0.4);
        CodeBox code = new CodeBox(new String[]{
                "class List {",
                "public:",
                "  List();",
                "  void insert(Element e);",
                "  bool find(Element e);",
                "  void remove(Element e);",
                "  void print();",
                "  int size();",
                "};"}, -880, -330, 32, 56);
        code.typeIn(3.6);

        Row row = new Row(new String[]{"4", "2", "7", "2", "9", "5"}, CX, ROW_Y, PITCH, NODE);
        row.nodes.get(4).nullNext();
        LNode n0 = row.nodes.get(0);
        Link headArrow = arrow(n0.leftX() - 120, n0.y, n0.leftX() - 3, n0.y, Colors.GOLD, 3.5);
        TextMob headLab = label("head", n0.leftX() - 70, n0.y - 36, 30, Colors.GOLD, false, true);
        play(new DrawLink(headArrow, d(0.6)), new FadeIn(headLab, d(0.6)));
        List<Animation> build = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            row.nodes.get(i).fadeIn(build, d(0.35) * i, d(0.6));
            if (i < 4) build.add(new DrawLinkAt(row.links.get(i), d(0.35) * i + d(0.3), d(0.5)));
        }
        playAll(build);
        pause(0.5);

        List<MObject> everything = new ArrayList<>(code.parts());
        everything.add(head);
        everything.addAll(row.parts());
        everything.add(headArrow);
        everything.add(headLab);

        Steps st = new Steps(CX, 235, 130);
        play(new FadeIn(st.num, d(0.5)), new FadeIn(st.cap, d(0.5)));
        everything.addAll(st.parts());
        play(new FadeIn(code.hl, d(0.4)));

        String[] cost = {"O(N)", "O(N)", "O(N)", "O(N)", "O(1)"};
        List<List<MObject>> chips = new ArrayList<>();
        for (int k = 0; k < 5; k++) {
            List<MObject> ch = chip(cost[k], -200, code.lineY(3 + k), 150, 48, k == 4 ? Colors.GREEN : Colors.ORANGE, 26);
            chips.add(ch);
            everything.addAll(ch);
        }

        // insert(5) without a tail pointer
        code.setLine(3);
        caption("insert(5) without a tail pointer: walk to the last node", Colors.WHITE);
        st.reset();
        Ptr ptr = above("ptr", n0, Colors.ORANGE);
        play(new FadeIn(ptr.arrow, d(0.4)), new FadeIn(ptr.lab, d(0.4)));
        everything.addAll(ptr.parts());
        for (int i = 0; i < 5; i++) {
            LNode n = row.nodes.get(i);
            List<Animation> mv = new ArrayList<>();
            if (i > 0) ptr.go(mv, n.x, d(0.45));
            n.paint(mv, Colors.ORANGE, d(0.35));
            playAll(mv);
            st.tick();
            pause(0.12);
            if (i < 4) paintFade(n, NODE, 0.25);
        }
        LNode n4 = row.nodes.get(4), n5 = row.nodes.get(5);
        paintNow(n5, NEW_NODE);
        List<Animation> add1 = new ArrayList<>();
        n5.fadeIn(add1, 0, d(0.6));
        n4.unNull(add1, d(0.5));
        add1.add(new DrawLinkAt(row.links.get(4), d(0.2), d(0.6)));
        playAll(add1);
        st.tick();
        play(new FadeIn(chips.get(0).get(0), d(0.5)), new FadeIn(chips.get(0).get(1), d(0.5)));
        pause(1.0);
        paintFade(n4, NODE, 0.3);

        // ... and with a tail pointer
        caption("with a tail pointer we jump straight to the end", Colors.GREEN);
        List<Animation> undo = new ArrayList<>();
        n5.fadeOut(undo, d(0.5));
        undo.add(new EraseLink(row.links.get(4), d(0.5)));
        undo.add(new FadeOut(ptr.arrow, d(0.4)));
        undo.add(new FadeOut(ptr.lab, d(0.4)));
        playAll(undo);
        List<Animation> nn = new ArrayList<>();
        n4.makeNull(nn, d(0.4));
        playAll(nn);
        st.reset();
        Ptr tail = below("tail", n4, Colors.PINK);
        play(new FadeIn(tail.arrow, d(0.5)), new FadeIn(tail.lab, d(0.5)));
        everything.addAll(tail.parts());
        pause(0.5);
        List<Animation> add2 = new ArrayList<>();
        n5.fadeIn(add2, 0, d(0.6));
        n4.unNull(add2, d(0.5));
        add2.add(new DrawLinkAt(row.links.get(4), d(0.2), d(0.6)));
        tail.go(add2, n5.x, d(0.7));
        playAll(add2);
        st.tick();
        List<MObject> chipTail = chip("O(1) with tail", -10, code.lineY(3), 210, 48, Colors.GREEN, 24);
        show(chipTail);
        everything.addAll(chipTail);
        paintFade(n5, NODE, 0.4);
        pause(1.4);

        // find(9)
        play(code.moveHl(4, d(0.5)));
        caption("find(9): follow the links one by one", Colors.WHITE);
        st.reset();
        Ptr fp = above("ptr", n0, Colors.ORANGE);
        play(new FadeIn(fp.arrow, d(0.4)), new FadeIn(fp.lab, d(0.4)));
        everything.addAll(fp.parts());
        for (int i = 0; i <= 4; i++) {
            LNode n = row.nodes.get(i);
            List<Animation> mv = new ArrayList<>();
            if (i > 0) fp.go(mv, n.x, d(0.45));
            n.paint(mv, i == 4 ? Colors.GREEN : Colors.ORANGE, d(0.35));
            playAll(mv);
            st.tick();
            pause(0.12);
            if (i < 4) paintFade(n, NODE, 0.25);
        }
        show(chips.get(1));
        pause(1.2);
        List<Animation> clean = new ArrayList<>();
        row.nodes.get(4).paint(clean, NODE, d(0.3));
        clean.add(new FadeOut(fp.arrow, d(0.4)));
        clean.add(new FadeOut(fp.lab, d(0.4)));
        playAll(clean);

        // remove(2): find it, then change one pointer
        play(code.moveHl(5, d(0.5)));
        caption("remove(2): find it, then change one pointer", Colors.WHITE);
        st.reset();
        Ptr rp = above("ptr", n0, Colors.ORANGE);
        play(new FadeIn(rp.arrow, d(0.4)), new FadeIn(rp.lab, d(0.4)));
        everything.addAll(rp.parts());
        st.tick();
        List<Animation> mv1 = new ArrayList<>();
        rp.go(mv1, row.nodes.get(1).x, d(0.5));
        playAll(mv1);
        st.tick();
        paintFade(row.nodes.get(1), DOOMED, d(0.35));
        Ptr prev = below("previous", n0, Colors.TEAL);
        play(new FadeIn(prev.arrow, d(0.4)), new FadeIn(prev.lab, d(0.4)));
        everything.addAll(prev.parts());
        pause(0.4);
        LNode n1 = row.nodes.get(1), n2 = row.nodes.get(2);
        double[][] reroute = nextRoute(n0, n2);
        List<Animation> byp = new ArrayList<>();
        byp.add(new LinkTo(row.links.get(0), reroute[0], reroute[1], d(0.8)));
        byp.add(new EraseLink(row.links.get(1), d(0.6)));
        n1.fadeOut(byp, d(0.8));
        byp.add(new FadeOut(rp.arrow, d(0.5)));
        byp.add(new FadeOut(rp.lab, d(0.5)));
        playAll(byp);
        st.tick();
        show(chips.get(2));
        play(new FadeOut(prev.arrow, d(0.4)), new FadeOut(prev.lab, d(0.4)));
        pause(1.2);

        // print(): visit every node still in the list
        play(code.moveHl(6, d(0.5)));
        caption("print(): visit every node", Colors.WHITE);
        st.reset();
        TextMob out = mono("Output:", CX - 250, 50, 46, Colors.GOLD);
        out.setAlignment(javafx.scene.text.TextAlignment.LEFT);
        play(new FadeIn(out, d(0.4)));
        everything.add(out);
        int[] live = {0, 2, 3, 4, 5};
        Ptr pp = above("ptr", n0, Colors.ORANGE);
        play(new FadeIn(pp.arrow, d(0.4)), new FadeIn(pp.lab, d(0.4)));
        everything.addAll(pp.parts());
        StringBuilder sb = new StringBuilder("Output:");
        for (int k = 0; k < live.length; k++) {
            LNode n = row.nodes.get(live[k]);
            List<Animation> mv = new ArrayList<>();
            if (k > 0) pp.go(mv, n.x, d(0.45));
            n.paint(mv, Colors.ORANGE, d(0.3));
            playAll(mv);
            st.tick();
            sb.append(' ').append(n.value);
            out.setText(sb.toString());
            pause(0.12);
            paintFade(n, NODE, 0.25);
        }
        show(chips.get(3));
        play(new FadeOut(pp.arrow, d(0.4)), new FadeOut(pp.lab, d(0.4)), new FadeOut(out, d(0.4)));
        pause(0.8);

        // size(): the list keeps a counter
        play(code.moveHl(7, d(0.5)));
        caption("size(): the list keeps a counter", Colors.WHITE);
        st.reset();
        List<MObject> sizeChip = chip("size = 5", CX, 50, 300, 84, Colors.GOLD, 44);
        show(sizeChip);
        everything.addAll(sizeChip);
        play(new ScaleTo(sizeChip.get(1), 1.3, d(0.3)));
        st.tick();
        play(new ScaleTo(sizeChip.get(1), 1.0, d(0.35)));
        show(chips.get(4));
        pause(1.4);

        // the question
        dropCaption();
        play(new FadeOut(st.num, d(0.5)), new FadeOut(st.cap, d(0.5)), new FadeOut(sizeChip.get(0), d(0.5)),
                new FadeOut(sizeChip.get(1), d(0.5)));
        StrokeTextMob q1 = stroke("If the complexities of array-based versus", CX, 30, 42, Colors.WHITE, false);
        StrokeTextMob q2 = stroke("linked-list-based implementations are the same,", CX, 95, 42, Colors.WHITE, false);
        StrokeTextMob q3 = stroke("why use linked lists?", CX, 190, 56, Colors.ORANGE, true);
        play(new Write(q1, d(2.4)));
        play(new Write(q2, d(2.8)));
        play(new Write(q3, d(2.0)));
        everything.add(q1);
        everything.add(q2);
        everything.add(q3);
        pause(3.2);
        fadeOutAll(d(1.2), everything);
        pause(0.4);
    }

    // ── slide 8 ──────────────────────────────────────────────────────

    private List<StrokeTextMob> colText(String[] lines, double left, double y0, Color dot) {
        List<StrokeTextMob> made = new ArrayList<>();
        CircleMob d0 = new CircleMob(8);
        d0.setFillColor(dot);
        d0.setStrokeColor(Color.TRANSPARENT);
        d0.setPosition(left - 24, y0);
        d0.setOpacity(0);
        add(d0);
        dots.add(d0);
        play(new FadeIn(d0, d(0.3)));
        for (int i = 0; i < lines.length; i++) {
            StrokeTextMob t = strokeLeft(lines[i], left, y0 + 50 * i, 36, Colors.WHITE);
            play(new Write(t, d(Math.max(1.2, lines[i].length() * 0.05))));
            made.add(t);
        }
        return made;
    }

    private final List<CircleMob> dots = new ArrayList<>();

    private void arraysVsLists() {
        head = writeHeading("Arrays versus Linked Lists");
        pause(0.4);
        List<MObject> frame = new ArrayList<>();
        frame.add(head);
        TextMob hA = label("Array", -460, -385, 46, Colors.TEAL, false, true);
        TextMob hL = label("Linked List", 460, -385, 46, Colors.BLUE, false, true);
        Link div = new Link(new double[]{0, 0}, new double[]{-420, 500}, Colors.withAlpha(Colors.WHITE, 0.25), 3, false);
        add(div);
        play(new FadeIn(hA, d(0.6)), new FadeIn(hL, d(0.6)), new DrawLink(div, d(0.9)));
        frame.add(hA);
        frame.add(hL);
        frame.add(div);
        pause(0.4);

        row1(frame);
        row2(frame);
        row3(frame);
        row4(frame);
        fadeOutAll(d(1.2), frame);
        pause(0.4);
    }

    /** Fades one row's texts and pictures away. */
    private void clearRow(List<MObject> stuff) {
        for (CircleMob c : dots) stuff.add(c);
        dots.clear();
        fadeOutAll(d(0.8), stuff);
        pause(0.2);
    }

    private static final double CELL_W = 76, CELL_H = 70, CELL_P = 84;

    private double aSlot(int i, int total) { return -460 + (i - (total - 1) / 2.0) * CELL_P; }

    private Cell cell(String v, double x, double y, Color c) { return new Cell(v, x, y, CELL_W, CELL_H, c, 34); }

    // row 1: reallocation versus one link
    private void row1(List<MObject> frame) {
        List<MObject> stuff = new ArrayList<>();
        List<StrokeTextMob> tl = colText(new String[]{"Need to copy the existing array", "on reallocation."}, -860, -310, Colors.GOLD);
        List<StrokeTextMob> tr = colText(new String[]{"Only a link needs to be", "established (O(1))."}, 60, -310, Colors.GOLD);
        stuff.addAll(tl);
        stuff.addAll(tr);

        // left: a full array of 4, a bigger one, and the copy
        Cell[] a = new Cell[4];
        int[] vals = {4, 2, 7, 9};
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            a[i] = cell(String.valueOf(vals[i]), aSlot(i, 8), -110, Colors.TEAL);
            a[i].fadeIn(in, 0.1 * i, d(0.5));
            stuff.addAll(a[i].parts());
        }
        TextMob full = label("full: no room for another", -460, -175, 28, Colors.RED, false, true);
        in.add(new FadeInAt(full, 0.6, d(0.5)));
        stuff.add(full);
        // right: a list of four
        Row rr = new Row(new String[]{"4", "2", "7", "9", "5"}, 460, 10, PITCH, NODE);
        rr.nodes.get(3).nullNext();
        for (int i = 0; i < 4; i++) {
            rr.nodes.get(i).fadeIn(in, 0.1 * i, d(0.5));
            if (i < 3) in.add(new DrawLinkAt(rr.links.get(i), 0.1 * i + 0.3, d(0.5)));
        }
        stuff.addAll(rr.parts());
        playAll(in);
        pause(0.8);

        // new bigger array
        RectMob[] ghost = new RectMob[8];
        List<Animation> g = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            ghost[i] = panel(aSlot(i, 8), 110, CELL_W, CELL_H, Colors.WHITE, 0.05);
            ghost[i].setStrokeColor(Colors.withAlpha(Colors.WHITE, 0.25));
            g.add(new FadeInAt(ghost[i], 0.05 * i, d(0.4)));
            stuff.add(ghost[i]);
        }
        TextMob bigger = label("a new, bigger array", -460, 170, 28, Colors.GOLD, false, true);
        g.add(new FadeInAt(bigger, 0.3, d(0.5)));
        stuff.add(bigger);
        playAll(g);
        List<Animation> cp = new ArrayList<>();
        Cell[] c = new Cell[4];
        for (int i = 0; i < 4; i++) {
            c[i] = cell(String.valueOf(vals[i]), aSlot(i, 8), -110, Colors.TEAL);
            for (MObject m : c[i].parts()) m.setOpacity(1);
            c[i].moveTo(cp, aSlot(i, 8), 110, 0, d(0.9));
            stuff.addAll(c[i].parts());
        }
        // staggered start by delaying through separate plays is costly: move them together
        for (Cell x : a) {
            List<Animation> fo = new ArrayList<>();
            x.fadeOut(fo, d(0.9));
            cp.addAll(fo);
        }
        playAll(cp);
        Cell five = cell("5", aSlot(4, 8), 110, Colors.GREEN);
        for (MObject m : five.parts()) m.setOpacity(0);
        List<Animation> dropFive = new ArrayList<>();
        for (MObject m : five.parts()) dropFive.add(new DropIn(m, 90, 0, d(0.7)));
        playAll(dropFive);
        stuff.addAll(five.parts());
        StrokeTextMob cl = stroke("copy every element: O(N)", -460, 250, 34, Colors.ORANGE, false);
        play(new Write(cl, d(2.0)));
        stuff.add(cl);

        // right: just one more link
        LNode nn = rr.nodes.get(4);
        paintNow(nn, NEW_NODE);
        List<Animation> add = new ArrayList<>();
        nn.fadeIn(add, 0, d(0.7));
        rr.nodes.get(3).unNull(add, d(0.5));
        add.add(new DrawLinkAt(rr.links.get(3), d(0.3), d(0.7)));
        playAll(add);
        StrokeTextMob cr = stroke("just one new link: O(1)", 460, 250, 34, Colors.GREEN, false);
        play(new Write(cr, d(2.0)));
        stuff.add(cr);
        pause(2.4);
        clearRow(stuff);
    }

    // row 2: removal
    private void row2(List<MObject> frame) {
        List<MObject> stuff = new ArrayList<>();
        stuff.addAll(colText(new String[]{"Removal of ith element needs", "element-shifting from i+1 to end."}, -860, -310, Colors.BLUE));
        stuff.addAll(colText(new String[]{"Removal of an element using", "pointers can be done in O(1)."}, 60, -310, Colors.BLUE));

        int[] vals = {4, 2, 7, 9, 5};
        Cell[] a = new Cell[5];
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            a[i] = cell(String.valueOf(vals[i]), aSlot(i, 5), 20, Colors.TEAL);
            a[i].fadeIn(in, 0.1 * i, d(0.5));
            stuff.addAll(a[i].parts());
        }
        Row rr = new Row(new String[]{"4", "2", "7", "9", "5"}, 460, 20, PITCH, NODE);
        for (int i = 0; i < 5; i++) {
            rr.nodes.get(i).fadeIn(in, 0.1 * i, d(0.5));
            if (i < 4) in.add(new DrawLinkAt(rr.links.get(i), 0.1 * i + 0.3, d(0.5)));
        }
        stuff.addAll(rr.parts());
        playAll(in);
        pause(0.8);

        // remove the 2
        List<Animation> kill = new ArrayList<>();
        a[1].color(kill, Colors.RED, d(0.4));
        rr.nodes.get(1).paint(kill, DOOMED, d(0.4));
        playAll(kill);
        pause(0.2);
        List<Animation> go = new ArrayList<>();
        a[1].fadeOut(go, d(0.6));
        for (int i = 2; i < 5; i++) a[i].moveTo(go, aSlot(i - 1, 5), 20, 0, d(0.9));
        double[][] rer = nextRoute(rr.nodes.get(0), rr.nodes.get(2));
        go.add(new LinkTo(rr.links.get(0), rer[0], rer[1], d(0.9)));
        go.add(new EraseLink(rr.links.get(1), d(0.6)));
        rr.nodes.get(1).fadeOut(go, d(0.8));
        playAll(go);
        StrokeTextMob cl = stroke("shift everything after it: O(N)", -460, 200, 34, Colors.ORANGE, false);
        StrokeTextMob cr = stroke("re-point one pointer: O(1)", 460, 200, 34, Colors.GREEN, false);
        play(new Write(cl, d(2.2)));
        play(new Write(cr, d(1.9)));
        stuff.add(cl);
        stuff.add(cr);
        pause(2.4);
        clearRow(stuff);
    }

    // row 3: insertion
    private void row3(List<MObject> frame) {
        List<MObject> stuff = new ArrayList<>();
        stuff.addAll(colText(new String[]{"Same with insertion."}, -860, -310, Colors.TEAL));
        stuff.addAll(colText(new String[]{"Same with insertion."}, 60, -310, Colors.TEAL));

        int[] vals = {4, 2, 7, 9};
        Cell[] a = new Cell[4];
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            RectMob gh = panel(aSlot(i, 5), 70, CELL_W, CELL_H, Colors.WHITE, 0.05);
            gh.setStrokeColor(Colors.withAlpha(Colors.WHITE, 0.25));
            in.add(new FadeInAt(gh, 0.05 * i, d(0.4)));
            stuff.add(gh);
        }
        for (int i = 0; i < 4; i++) {
            a[i] = cell(String.valueOf(vals[i]), aSlot(i, 5), 70, Colors.TEAL);
            a[i].fadeIn(in, 0.1 * i + 0.3, d(0.5));
            stuff.addAll(a[i].parts());
        }
        Row rr = new Row(new String[]{"4", "2", "7", "9"}, 460, 120, PITCH, NODE);
        for (int i = 0; i < 4; i++) {
            rr.nodes.get(i).fadeIn(in, 0.1 * i + 0.3, d(0.5));
            if (i < 3) in.add(new DrawLinkAt(rr.links.get(i), 0.1 * i + 0.6, d(0.5)));
        }
        stuff.addAll(rr.parts());
        playAll(in);
        pause(0.8);

        // insert 6 after the 4
        List<Animation> sh = new ArrayList<>();
        for (int i = 3; i >= 1; i--) a[i].moveTo(sh, aSlot(i + 1, 5), 70, 0, d(0.9));
        playAll(sh);
        Cell six = cell("6", aSlot(1, 5), 70, Colors.GREEN);
        for (MObject m : six.parts()) m.setOpacity(0);
        List<Animation> drop = new ArrayList<>();
        for (MObject m : six.parts()) drop.add(new DropIn(m, 90, 0, d(0.8)));
        playAll(drop);
        stuff.addAll(six.parts());
        StrokeTextMob cl = stroke("shift to make room: O(N)", -460, 240, 34, Colors.ORANGE, false);
        play(new Write(cl, d(2.0)));
        stuff.add(cl);

        LNode n0 = rr.nodes.get(0), n1 = rr.nodes.get(1);
        LNode nw = node("6", (n0.x + n1.x) / 2, -60, NEW_NODE);
        double[][] r1 = nextRoute(n0, nw);
        double[][] r2 = nextRoute(nw, n1);
        Link l2 = new Link(r2[0], r2[1], NEXT_LINK, 3.2, true);
        add(l2);
        List<Animation> ap = new ArrayList<>();
        nw.fadeIn(ap, 0, d(0.7));
        ap.add(new LinkTo(rr.links.get(0), r1[0], r1[1], d(0.9)));
        ap.add(new DrawLinkAt(l2, d(0.5), d(0.7)));
        playAll(ap);
        stuff.addAll(nw.all);
        stuff.add(l2);
        StrokeTextMob cr = stroke("re-point two pointers: O(1)", 460, 240, 34, Colors.GREEN, false);
        play(new Write(cr, d(2.0)));
        stuff.add(cr);
        pause(2.4);
        clearRow(stuff);
    }

    // row 4: concatenation
    private void row4(List<MObject> frame) {
        List<MObject> stuff = new ArrayList<>();
        stuff.addAll(colText(new String[]{"Array concatenation is linear time."}, -860, -310, Colors.ORANGE));
        stuff.addAll(colText(new String[]{"List concatenation is O(1)."}, 60, -310, Colors.ORANGE));

        Cell[] a = new Cell[6];
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            double x = i < 3 ? -700 + i * CELL_P : -330 + (i - 3) * CELL_P;
            a[i] = cell(String.valueOf(i + 1), x, -90, i < 3 ? Colors.TEAL : Colors.PINK);
            a[i].fadeIn(in, 0.08 * i, d(0.5));
            stuff.addAll(a[i].parts());
        }
        TextMob la = label("A", -616, -165, 30, Colors.TEAL, false, true);
        TextMob lb = label("B", -246, -165, 30, Colors.PINK, false, true);
        in.add(new FadeIn(la, d(0.5)));
        in.add(new FadeIn(lb, d(0.5)));
        stuff.add(la);
        stuff.add(lb);

        // right: two lists, A above B
        Row ra = new Row(new String[]{"1", "2", "3"}, 330, -90, PITCH, Colors.TEAL);
        Row rb = new Row(new String[]{"4", "5", "6"}, 330, 110, PITCH, Colors.PINK);
        for (int i = 0; i < 3; i++) {
            ra.nodes.get(i).fadeIn(in, 0.08 * i, d(0.5));
            rb.nodes.get(i).fadeIn(in, 0.08 * i, d(0.5));
            if (i < 2) {
                in.add(new DrawLinkAt(ra.links.get(i), 0.08 * i + 0.3, d(0.5)));
                in.add(new DrawLinkAt(rb.links.get(i), 0.08 * i + 0.3, d(0.5)));
            }
        }
        stuff.addAll(ra.parts());
        stuff.addAll(rb.parts());
        playAll(in);
        pause(0.8);

        // left: copy both into a new array of six
        RectMob[] ghost = new RectMob[6];
        List<Animation> g = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            ghost[i] = panel(-640 + i * CELL_P, 130, CELL_W, CELL_H, Colors.WHITE, 0.05);
            ghost[i].setStrokeColor(Colors.withAlpha(Colors.WHITE, 0.25));
            g.add(new FadeInAt(ghost[i], 0.05 * i, d(0.4)));
            stuff.add(ghost[i]);
        }
        playAll(g);
        List<Animation> cp = new ArrayList<>();
        Cell[] c = new Cell[6];
        for (int i = 0; i < 6; i++) {
            c[i] = cell(String.valueOf(i + 1), a[i].x, -90, i < 3 ? Colors.TEAL : Colors.PINK);
            for (MObject m : c[i].parts()) m.setOpacity(1);
            c[i].moveTo(cp, -640 + i * CELL_P, 130, 0, d(1.1));
            stuff.addAll(c[i].parts());
            a[i].fadeOut(cp, d(1.1));
        }
        playAll(cp);
        StrokeTextMob cl = stroke("copy N + M elements: O(N)", -460, 240, 34, Colors.ORANGE, false);
        play(new Write(cl, d(2.0)));
        stuff.add(cl);

        // right: one pointer joins the lists
        LNode a2 = ra.nodes.get(2), b0 = rb.nodes.get(0);
        List<Animation> un = new ArrayList<>();
        a2.unNull(un, d(0.4));
        playAll(un);
        double ax = a2.nextX();
        Link join = new Link(new double[]{ax, ax + 60, ax + 60, b0.x, b0.x},
                new double[]{a2.y, a2.y, 10, 10, b0.top() - 4}, NEXT_LINK, 3.4, true);
        add(join);
        play(new DrawLink(join, d(1.4)));
        stuff.add(join);
        StrokeTextMob cr = stroke("one pointer joins them: O(1)", 460, 240, 34, Colors.GREEN, false);
        play(new Write(cr, d(2.0)));
        stuff.add(cr);
        pause(2.6);
        clearRow(stuff);
    }
}
