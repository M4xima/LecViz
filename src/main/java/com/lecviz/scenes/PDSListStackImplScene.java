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
 * Standalone clip for slide 25 of the lists deck: Stack Implementation.
 *
 *   - the design decisions one line at a time, and the slide's picture: the call stack of a running
 *     program (main, three printRecursive calls, Node::print, printf) growing and shrinking at the top
 *   - then every design decision on its own picture: array versus linked list, traversal, size, peek,
 *     who checks isEmpty, and whether the top points at the last element or at the next free entry
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListStackImplScene extends PDSListClipBase {

    private StrokeTextMob head;
    private final List<MObject> stage = new ArrayList<>();
    private StrokeTextMob title;

    @Override
    public void construct() {
        head = writeHeading("Stack Implementation");
        pause(0.4);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Design decisions"));
        s.add(ln(1, "Array versus Linked List"));
        s.add(ln(1, "Allow traversing through the stack?"));
        s.add(ln(1, "Allow querying stack size?"));
        s.add(ln(1, "Allow peeking at the stack top?"));
        s.add(ln(1, "IsEmpty is user's responsibility or"));
        s.add(ln(2, "library implementation's?"));
        s.add(ln(1, "Stack Top points to the last"));
        s.add(ln(2, "element, or the entry next to that?"));
        s.add(ln(3, "Source: stack.cpp").kw("stack.cpp", Colors.MAROON));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(0.6);

        callStack();
        swipeAway(text);
        pause(0.4);

        arrayOrList();
        traversal();
        sizeQuery();
        peek();
        emptyCheck();
        topPointer();
        fadeOutAll(d(1.0), head);
        pause(0.4);
    }

    private void keep(List<MObject> parts) { stage.addAll(parts); }

    private void clearStage() {
        fadeOutAll(d(0.8), stage);
        stage.clear();
        pause(0.2);
    }

    private void question(String text) {
        title = stroke(text, 0, -345, 44, Colors.WHITE, false);
        play(new Write(title, d(Math.max(1.6, text.length() * 0.05))));
        stage.add(title);
    }

    // ── the slide's picture: the program's call stack ────────────────

    private void callStack() {
        VStack st = new VStack(400, 440, 400, 64, 7, "Stack top");
        st.showBox();
        st.showTop();
        List<MObject> mine = new ArrayList<>();
        String[] names = {"main", "List::printRecursive", "List::printRecursive", "List::printRecursive", "Node::print", "printf"};
        Color[] cols = {Colors.GRAY, Colors.PINK, Colors.PINK, Colors.PINK, Colors.TEAL, Colors.GOLD};
        for (int i = 0; i < names.length; i++) {
            Cell c = st.push(names[i], cols[i]);
            c.text.setFontSize(26);
            mine.addAll(c.parts());
        }
        TextMob lab = label("every function call is pushed, every return is popped", 400, -140, 26, Colors.LIGHT_GRAY, false, true);
        play(new FadeIn(lab, d(0.6)));
        mine.add(lab);
        pause(1.4);
        for (int i = 0; i < 5; i++) {
            st.popAway();
            pause(0.15);
        }
        pause(0.8);
        List<MObject> all = new ArrayList<>(mine);
        all.add(st.box);
        all.add(st.topArrow);
        all.add(st.topLab);
        fadeOutAll(d(0.8), all);
    }

    // ── array versus linked list ─────────────────────────────────────

    private Cell[] arrayRow(double cx, double y, String[] vals, double w, double pitch, Color c) {
        Cell[] cells = new Cell[vals.length];
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < vals.length; i++) {
            cells[i] = new Cell(vals[i] == null ? "" : vals[i], cx + (i - (vals.length - 1) / 2.0) * pitch, y, w, w * 0.9, c, 34);
            if (vals[i] == null) {
                cells[i].box.setFillColor(Colors.withAlpha(Colors.WHITE, 0.04));
                cells[i].box.setStrokeColor(Colors.withAlpha(Colors.WHITE, 0.25));
            }
            cells[i].fadeIn(in, 0.06 * i, d(0.5));
            stage.addAll(cells[i].parts());
        }
        playAll(in);
        return cells;
    }

    private void fillCell(Cell c, String v, Color col, List<Animation> into) {
        c.text.setText(v);
        into.add(new FadeIn(c.text, d(0.4)));
        c.color(into, col, d(0.4));
    }

    private void arrayOrList() {
        question("Array versus Linked List");
        TextMob la = label("Array", -440, -250, 36, Colors.TEAL, false, true);
        TextMob ll = label("Linked list", 440, -250, 36, Colors.BLUE, false, true);
        play(new FadeIn(la, d(0.5)), new FadeIn(ll, d(0.5)));
        stage.add(la);
        stage.add(ll);

        Cell[] arr = arrayRow(-440, -90, new String[]{"7", "3", "9", null, null, null}, 84, 94, Colors.TEAL);
        Ptr topA = pointer("top", arr[2].x, arr[2].y + 52, false, Colors.GOLD);
        play(new FadeIn(topA.arrow, d(0.5)), new FadeIn(topA.lab, d(0.5)));
        keep(topA.parts());

        Row row = new Row(new String[]{"9", "3", "7"}, 520, -90, 170, NODE);
        row.nodes.get(2).nullNext();
        LNode first = row.nodes.get(0);
        Ptr topL = above("top", first, Colors.GOLD);
        row.build(d(0.3), d(0.6));
        play(new FadeIn(topL.arrow, d(0.5)), new FadeIn(topL.lab, d(0.5)));
        keep(row.parts());
        keep(topL.parts());
        pause(0.8);

        // push(5) on both
        List<MObject> call = chip("push(5)", 0, 40, 240, 58, Colors.GREEN, 30);
        List<Animation> ca = new ArrayList<>();
        fade(ca, call, d(0.4));
        playAll(ca);
        stage.addAll(call);
        List<Animation> a = new ArrayList<>();
        fillCell(arr[3], "5", Colors.GREEN, a);
        topA.go(a, arr[3].x, d(0.6));
        playAll(a);
        LNode nn = node("5", first.x - 190, first.y, NEW_NODE);
        Link lk = nextLink(nn, first);
        List<Animation> b = new ArrayList<>();
        nn.fadeIn(b, 0, d(0.7));
        b.add(new DrawLinkAt(lk, 0.3, d(0.6)));
        topL.go(b, nn.x, d(0.7));
        playAll(b);
        keep(nn.all);
        stage.add(lk);
        pause(0.8);

        List<MObject> c1 = chip("fixed size: can run out of room", -440, 130, 560, 60, Colors.ORANGE, 28);
        List<MObject> c2 = chip("grows with every push, one pointer per item", 420, 130, 640, 60, Colors.BLUE, 28);
        List<Animation> cc = new ArrayList<>();
        fade(cc, c1, d(0.6));
        fade(cc, c2, d(0.6));
        playAll(cc);
        stage.addAll(c1);
        stage.addAll(c2);
        StrokeTextMob t = stroke("Both work: the stack operations stay the same.", 0, 300, 40, Colors.GREEN, false);
        play(new Write(t, d(2.6)));
        stage.add(t);
        pause(2.4);
        clearStage();
    }

    // ── traversal ────────────────────────────────────────────────────

    private void traversal() {
        question("Allow traversing through the stack?");
        VStack st = new VStack(-300, 380, 340, 62, 5, "top");
        st.showBox();
        st.showTop();
        String[] v = {"A", "B", "C", "D"};
        Color[] cl = {Colors.TEAL, Colors.BLUE, Colors.PINK, Colors.GOLD};
        for (int i = 0; i < 4; i++) {
            Cell c = st.push(v[i], cl[i]);
            keep(c.parts());
        }
        stage.add(st.box);
        stage.add(st.topArrow);
        stage.add(st.topLab);
        List<MObject> yes = chip("yes: a walker scans every item", 400, -180, 640, 62, Colors.ORANGE, 28);
        List<Animation> y = new ArrayList<>();
        fade(y, yes, d(0.6));
        playAll(y);
        stage.addAll(yes);
        pause(0.4);
        // the walker goes from the top down to the bottom
        for (int i = 3; i >= 0; i--) {
            Cell c = st.items.get(i);
            List<Animation> hi = new ArrayList<>();
            c.color(hi, Colors.ORANGE, d(0.25));
            playAll(hi);
            pause(0.1);
            List<Animation> back = new ArrayList<>();
            c.color(back, cl[i], d(0.25));
            playAll(back);
        }
        List<MObject> no = chip("no: only the top is reachable", 400, -80, 640, 62, Colors.GREEN, 28);
        List<Animation> n = new ArrayList<>();
        fade(n, no, d(0.6));
        playAll(n);
        stage.addAll(no);
        StrokeTextMob t1 = strokeLeft("Allowing the walk means items other than the top", 130, 80, 32, Colors.LIGHT_GRAY);
        StrokeTextMob t2 = strokeLeft("can be read: it is no longer a one-ended structure.", 130, 140, 32, Colors.LIGHT_GRAY);
        play(new Write(t1, d(2.4)));
        play(new Write(t2, d(2.4)));
        stage.add(t1);
        stage.add(t2);
        pause(2.6);
        clearStage();
    }

    // ── size ─────────────────────────────────────────────────────────

    private void sizeQuery() {
        question("Allow querying stack size?");
        VStack st = new VStack(-300, 380, 340, 62, 5, "top");
        st.showBox();
        st.showTop();
        Cell a = st.push("7", Colors.TEAL);
        Cell b = st.push("3", Colors.BLUE);
        Cell c = st.push("9", Colors.PINK);
        keep(a.parts());
        keep(b.parts());
        keep(c.parts());
        stage.add(st.box);
        stage.add(st.topArrow);
        stage.add(st.topLab);
        List<MObject> sz = chip("size() = 3", 400, -120, 380, 84, Colors.GOLD, 44);
        List<Animation> an = new ArrayList<>();
        fade(an, sz, d(0.6));
        playAll(an);
        stage.addAll(sz);
        TextMob szText = (TextMob) sz.get(1);
        pause(0.6);
        // push and pop keep the count
        Cell d1 = st.push("5", Colors.GREEN);
        stage.addAll(d1.parts());
        szText.setText("size() = 4");
        play(new ScaleTo(szText, 1.25, d(0.2)));
        play(new ScaleTo(szText, 1.0, d(0.25)));
        pause(0.5);
        Cell gone = st.popAway();
        szText.setText("size() = 3");
        play(new ScaleTo(szText, 1.25, d(0.2)));
        play(new ScaleTo(szText, 1.0, d(0.25)));
        stage.addAll(gone.parts());
        StrokeTextMob t1 = strokeLeft("Needs a counter that every push and pop updates,", 130, 60, 32, Colors.LIGHT_GRAY);
        StrokeTextMob t2 = strokeLeft("or the stack has to be counted item by item.", 130, 120, 32, Colors.LIGHT_GRAY);
        play(new Write(t1, d(2.6)));
        play(new Write(t2, d(2.4)));
        stage.add(t1);
        stage.add(t2);
        pause(2.6);
        clearStage();
    }

    // ── peek ─────────────────────────────────────────────────────────

    private void peek() {
        question("Allow peeking at the stack top?");
        VStack st = new VStack(-300, 380, 340, 62, 5, "top");
        st.showBox();
        st.showTop();
        Cell a = st.push("7", Colors.TEAL);
        Cell b = st.push("3", Colors.BLUE);
        Cell c = st.push("9", Colors.PINK);
        keep(a.parts());
        keep(b.parts());
        keep(c.parts());
        stage.add(st.box);
        stage.add(st.topArrow);
        stage.add(st.topLab);
        pause(0.4);
        List<MObject> pk = chip("peek()  returns 9, the stack is unchanged", 420, -140, 760, 64, Colors.TEAL, 28);
        List<Animation> an = new ArrayList<>();
        fade(an, pk, d(0.6));
        playAll(an);
        stage.addAll(pk);
        for (int k = 0; k < 2; k++) {
            List<Animation> f = new ArrayList<>();
            c.color(f, Colors.WHITE, d(0.25));
            playAll(f);
            List<Animation> g = new ArrayList<>();
            c.color(g, Colors.PINK, d(0.3));
            playAll(g);
        }
        pause(0.8);
        List<MObject> pp = chip("pop()  returns 9 and removes it", 420, -30, 760, 64, Colors.PINK, 28);
        List<Animation> an2 = new ArrayList<>();
        fade(an2, pp, d(0.6));
        playAll(an2);
        stage.addAll(pp);
        Cell out = st.popTo(560, 130);
        stage.addAll(out.parts());
        StrokeTextMob t1 = strokeLeft("Without peek, looking at the top means popping it", 130, 250, 32, Colors.LIGHT_GRAY);
        StrokeTextMob t2 = strokeLeft("and pushing it back.", 130, 310, 32, Colors.LIGHT_GRAY);
        play(new Write(t1, d(2.6)));
        play(new Write(t2, d(1.4)));
        stage.add(t1);
        stage.add(t2);
        pause(2.6);
        clearStage();
    }

    // ── who checks isEmpty ───────────────────────────────────────────

    private void emptyCheck() {
        question("IsEmpty: the user's responsibility or the library's?");
        VStack st = new VStack(-300, 380, 340, 62, 5, "top");
        st.showBox();
        st.showTop();
        Cell a = st.push("7", Colors.TEAL);
        keep(a.parts());
        stage.add(st.box);
        stage.add(st.topArrow);
        stage.add(st.topLab);
        List<MObject> user = chip("user:  if (!s.isEmpty()) s.pop();", 420, -170, 760, 64, Colors.BLUE, 28);
        List<MObject> lib = chip("library:  pop() checks by itself", 420, -70, 760, 64, Colors.TEAL, 28);
        List<Animation> an = new ArrayList<>();
        fade(an, user, d(0.6));
        fade(an, lib, d(0.8));
        playAll(an);
        stage.addAll(user);
        stage.addAll(lib);
        pause(0.8);
        // pop twice: the second pop finds nothing
        Cell p1 = st.popAway();
        stage.addAll(p1.parts());
        pause(0.5);
        List<MObject> bad = chip("pop() on an empty stack: underflow!", 420, 60, 760, 70, Colors.RED, 32);
        List<Animation> b = new ArrayList<>();
        fade(b, bad, d(0.5));
        playAll(b);
        stage.addAll(bad);
        StrokeTextMob t1 = strokeLeft("Someone has to check before popping: either the caller", 130, 200, 32, Colors.LIGHT_GRAY);
        StrokeTextMob t2 = strokeLeft("tests isEmpty first, or the stack reports the error.", 130, 260, 32, Colors.LIGHT_GRAY);
        play(new Write(t1, d(3.0)));
        play(new Write(t2, d(2.8)));
        stage.add(t1);
        stage.add(t2);
        pause(2.6);
        clearStage();
    }

    // ── where the top points ─────────────────────────────────────────

    private void topPointer() {
        question("Stack Top points to the last element, or the entry next to that?");
        double cx = 120;
        TextMob l1 = label("top = last element", -640, -190, 30, Colors.TEAL, false, true);
        TextMob n1 = label("push: top++, then write", -640, -145, 24, Colors.LIGHT_GRAY, false, false);
        TextMob l2 = label("top = next free entry", -640, 130, 30, Colors.PINK, false, true);
        TextMob n2 = label("push: write, then top++", -640, 175, 24, Colors.LIGHT_GRAY, false, false);
        play(new FadeIn(l1, d(0.5)), new FadeIn(l2, d(0.5)), new FadeIn(n1, d(0.5)), new FadeIn(n2, d(0.5)));
        stage.add(l1);
        stage.add(l2);
        stage.add(n1);
        stage.add(n2);
        String[] vals = {"7", "3", "9", null, null, null};
        Cell[] rowA = arrayRow(cx, -190, vals, 90, 104, Colors.TEAL);
        Cell[] rowB = arrayRow(cx, 130, vals, 90, 104, Colors.PINK);
        for (int i = 0; i < 6; i++) {
            TextMob ia = label(String.valueOf(i), rowA[i].x, rowA[i].y - 62, 24, Colors.GRAY, false, false);
            TextMob ib = label(String.valueOf(i), rowB[i].x, rowB[i].y - 62, 24, Colors.GRAY, false, false);
            play(new FadeInAt(ia, 0, d(0.3)), new FadeInAt(ib, 0, d(0.3)));
            stage.add(ia);
            stage.add(ib);
        }
        Ptr ta = pointer("top", rowA[2].x, rowA[2].y + 56, false, Colors.GOLD);
        Ptr tb = pointer("top", rowB[3].x, rowB[3].y + 56, false, Colors.GOLD);
        play(new FadeIn(ta.arrow, d(0.5)), new FadeIn(ta.lab, d(0.5)), new FadeIn(tb.arrow, d(0.5)), new FadeIn(tb.lab, d(0.5)));
        keep(ta.parts());
        keep(tb.parts());
        pause(1.0);

        // push(5) on both
        List<MObject> call = chip("push(5)", -640, -30, 220, 58, Colors.GREEN, 30);
        List<Animation> ca = new ArrayList<>();
        fade(ca, call, d(0.4));
        playAll(ca);
        stage.addAll(call);
        List<Animation> a = new ArrayList<>();
        ta.go(a, rowA[3].x, d(0.6));
        playAll(a);
        List<Animation> a2 = new ArrayList<>();
        fillCell(rowA[3], "5", Colors.GREEN, a2);
        playAll(a2);
        pause(0.6);
        List<Animation> b = new ArrayList<>();
        fillCell(rowB[3], "5", Colors.GREEN, b);
        playAll(b);
        List<Animation> b2 = new ArrayList<>();
        tb.go(b2, rowB[4].x, d(0.6));
        playAll(b2);
        pause(0.8);
        StrokeTextMob t = stroke("Either works, as long as push, pop and isEmpty all use the same rule.", 0, 340, 38, Colors.GREEN, false);
        play(new Write(t, d(3.4)));
        stage.add(t);
        pause(3.0);
        clearStage();
    }
}
