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
 * Standalone clip for slides 38-39 of the lists deck: Queue and Queue ADT.
 *
 *   Slide 38  the points one at a time; then a queue as a list with a head and a tail pointer: arrival
 *             numbers under the nodes, enqueue adds at the tail, dequeue removes at the head (the first
 *             one in is the first one out: FIFO, first come first served), and an arbitrary element cannot
 *             be reached
 *   Slide 39  "write down the Queue ADT": the struct version (push = enqueue, pop = dequeue, isEmpty) and the
 *             class version (push, pop, front, back, isEmpty) typed side by side
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListQueueScene extends PDSListClipBase {

    private StrokeTextMob head;
    private StrokeTextMob cap;

    @Override
    public void construct() {
        queue();
        queueAdt();
    }

    private void caption(String text, Color c, double y, double size) {
        StrokeTextMob n = stroke(text, 0, y, size, c, false);
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

    // ── slide 38 ─────────────────────────────────────────────────────

    private void queue() {
        head = writeHeading("Queue");
        pause(0.4);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Special list"));
        s.add(ln(0, "Insertions at one end, deletions at the other"));
        s.add(ln(0, "Tracked using two pointers: head and tail").kw("head", Colors.GOLD).kw("tail", Colors.PINK));
        s.add(ln(0, "FIFO ").tail("(what is FCFS?)").gap(0));
        s.add(ln(0, "Cannot access arbitrary element"));
        s.add(ln(0, "Insert → push / enqueue"));
        s.add(ln(3, "remove → pop / dequeue"));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(1.2);
        swipeAway(text);
        pause(0.4);

        List<MObject> mine = new ArrayList<>();
        mine.add(head);
        Row row = new Row(new String[]{"4", "2", "7", "2", "9", "5"}, -120, -150, 190, NODE);
        row.nodes.get(5).nullNext();
        LNode first = row.nodes.get(0), last = row.nodes.get(5);
        Ptr hp = above("head", first, Colors.GOLD);
        Ptr tp = above("tail", last, Colors.PINK);
        row.build(d(0.35), d(0.6));
        play(new FadeIn(hp.arrow, d(0.5)), new FadeIn(hp.lab, d(0.5)), new FadeIn(tp.arrow, d(0.5)), new FadeIn(tp.lab, d(0.5)));
        mine.addAll(row.parts());
        mine.addAll(hp.parts());
        mine.addAll(tp.parts());
        // arrival order under each node
        List<TextMob> badge = new ArrayList<>();
        List<Animation> ba = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            TextMob b = label("arrived #" + (i + 1), row.nodes.get(i).x, row.nodes.get(i).y + 66, 24, Colors.GRAY, false, true);
            ba.add(new FadeInAt(b, 0.1 * i, d(0.4)));
            badge.add(b);
            mine.add(b);
        }
        playAll(ba);
        caption("Insertions at the tail, deletions at the head.", Colors.WHITE, 260, 40);
        pause(1.0);

        // enqueue(3): a new node joins behind the tail
        List<MObject> call = chip("enqueue(3)", -420, 70, 270, 62, Colors.GREEN, 32);
        List<Animation> ca = new ArrayList<>();
        fade(ca, call, d(0.4));
        playAll(ca);
        mine.addAll(call);
        LNode nn = node("3", last.x + 210, last.y, NEW_NODE);
        nn.nullNext();
        Link lk = nextLink(last, nn);
        List<Animation> en = new ArrayList<>();
        nn.fadeIn(en, 0, d(0.7));
        last.unNull(en, d(0.4));
        en.add(new DrawLinkAt(lk, 0.3, d(0.6)));
        tp.go(en, nn.x, d(0.7));
        playAll(en);
        mine.addAll(nn.all);
        mine.add(lk);
        TextMob b7 = label("arrived #7", nn.x, nn.y + 66, 24, Colors.GRAY, false, true);
        play(new FadeIn(b7, d(0.4)));
        mine.add(b7);
        pause(1.0);
        List<Animation> offCall = new ArrayList<>();
        for (MObject m : call) offCall.add(new FadeOut(m, d(0.4)));
        playAll(offCall);

        // dequeue(): the head leaves first
        for (int k = 0; k < 2; k++) {
            LNode h = row.nodes.get(k);
            List<MObject> dq = chip("dequeue()", -420, 70, 270, 62, Colors.PINK, 32);
            List<Animation> da = new ArrayList<>();
            fade(da, dq, d(0.4));
            playAll(da);
            mine.addAll(dq);
            List<Animation> mark = new ArrayList<>();
            h.paint(mark, DOOMED, d(0.35));
            playAll(mark);
            LNode nextH = row.nodes.get(k + 1);
            List<Animation> go = new ArrayList<>();
            h.fadeOut(go, d(0.7));
            go.add(new EraseLink(row.links.get(k), d(0.6)));
            go.add(new FadeOut(badge.get(k), d(0.6)));
            hp.go(go, nextH.x, d(0.7));
            playAll(go);
            TextMob got = label("returns " + h.value + ": the first one in", 40, 160, 34, Colors.PINK, false, true);
            play(new FadeIn(got, d(0.5)));
            pause(1.0);
            List<Animation> clr = new ArrayList<>();
            clr.add(new FadeOut(got, d(0.4)));
            for (MObject m : dq) clr.add(new FadeOut(m, d(0.4)));
            playAll(clr);
        }
        caption("FIFO: first in, first out.   FCFS: first come, first served.", Colors.GOLD, 300, 40);
        pause(2.4);
        // an arbitrary element is out of reach
        caption("Cannot access an arbitrary element: only the head and the tail.", Colors.ORANGE, 300, 38);
        LNode mid = row.nodes.get(3);
        Link x1 = new Link(new double[]{mid.x - 40, mid.x + 40}, new double[]{mid.y - 36, mid.y + 36}, Colors.RED, 6, false);
        Link x2 = new Link(new double[]{mid.x - 40, mid.x + 40}, new double[]{mid.y + 36, mid.y - 36}, Colors.RED, 6, false);
        add(x1);
        add(x2);
        play(new DrawLink(x1, d(0.4)), new DrawLink(x2, d(0.4)));
        mine.add(x1);
        mine.add(x2);
        pause(2.6);
        dropCaption();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(0.6), head);
        pause(0.4);
    }

    // ── slide 39 ─────────────────────────────────────────────────────

    private void queueAdt() {
        head = writeHeading("Queue ADT");
        pause(0.4);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Classwork: Write down the Queue ADT.").kw("Classwork", Colors.RED));
        List<List<MObject>> text = writeSlide(s, -360);
        List<MObject> mine = new ArrayList<>();
        mine.add(head);
        for (List<MObject> g : text) mine.addAll(g);
        CodeBox st = new CodeBox(new String[]{
                "struct Queue {",
                "  void push(Element);   // enqueue",
                "  Element pop();        // dequeue",
                "  bool isEmpty();",
                "  ...",
                "};"}, -930, -250, 26, 50);
        CodeBox cl = new CodeBox(new String[]{
                "class Queue {",
                "  void push(Element);",
                "  void pop();",
                "  Element front();",
                "  Element back();",
                "  bool isEmpty();",
                "  ...",
                "};"}, -140, -250, 26, 50);
        cl.card.setFillColor(Colors.withAlpha(Colors.BLUE, 0.13));
        cl.card.setStrokeColor(Colors.withAlpha(Colors.BLUE, 0.6));
        st.typeIn(3.2);
        cl.typeIn(3.6);
        mine.addAll(st.parts());
        mine.addAll(cl.parts());
        TextMob src = mono("Source: q.cpp", 560, 160, 26, Colors.MAROON);
        play(new FadeIn(src, d(0.5)));
        mine.add(src);
        play(new FadeIn(st.hl, d(0.3)), new FadeIn(cl.hl, d(0.3)));
        pause(0.6);

        play(st.moveHl(1, d(0.4)));
        List<MObject> a1 = chip("push = enqueue: add at the tail", 640, st.lineY(1), 560, 58, Colors.GREEN, 26);
        play(new FadeIn(a1.get(0), d(0.5)), new FadeIn(a1.get(1), d(0.5)));
        mine.addAll(a1);
        pause(1.0);
        play(st.moveHl(2, d(0.4)));
        List<MObject> a2 = chip("pop = dequeue: remove at the head", 640, st.lineY(2) + 10, 560, 58, Colors.PINK, 26);
        play(new FadeIn(a2.get(0), d(0.5)), new FadeIn(a2.get(1), d(0.5)));
        mine.addAll(a2);
        pause(1.0);
        play(st.moveHl(3, d(0.4)));
        pause(0.8);
        play(cl.moveHl(2, d(0.4)));
        List<MObject> b1 = chip("pop() only removes", 640, -20, 560, 58, Colors.PINK, 26);
        play(new FadeIn(b1.get(0), d(0.5)), new FadeIn(b1.get(1), d(0.5)));
        mine.addAll(b1);
        pause(0.9);
        play(cl.moveHl(3, d(0.4)));
        List<MObject> b2 = chip("front() and back() look at the ends", 640, 50, 560, 58, Colors.TEAL, 26);
        play(new FadeIn(b2.get(0), d(0.5)), new FadeIn(b2.get(1), d(0.5)));
        mine.addAll(b2);
        pause(1.4);
        StrokeTextMob c1 = stroke("Two ways to write the same ADT: either way, only the two ends are used.", 0, 280, 38, Colors.WHITE, false);
        play(new Write(c1, d(3.4)));
        mine.add(c1);
        pause(3.0);
        fadeOutAll(d(1.2), mine);
        pause(0.4);
    }
}
