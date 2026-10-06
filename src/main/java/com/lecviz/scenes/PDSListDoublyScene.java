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
 * Standalone clip for slides 17-18 of the lists deck: doubly linked lists and circular doubly
 * linked lists.
 *
 *   Slide 17  the points one at a time; the node with its two pointers (next and previous) beside
 *             the struct; a doubly linked list with head and tail; and the classwork, remove(Node *p):
 *             the middle node, the head and the tail each unlinked using only the node's own pointers
 *   Slide 18  the points one at a time; the ring: last.next points to the first and first.previous to
 *             the last, so no tail pointer is needed; a circular singly linked list; and the classwork,
 *             printing a CDLL with a do-while that stops when ptr is back at head
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListDoublyScene extends PDSListClipBase {

    private StrokeTextMob head;

    @Override
    public void construct() {
        dllSlide();
        cdllSlide();
    }

    // ── a doubly linked row ──────────────────────────────────────────

    private final class Dll {
        final List<LNode> n = new ArrayList<>();
        final List<Link> nx = new ArrayList<>();   // nx[i]: node i -> node i+1
        final List<Link> pv = new ArrayList<>();   // pv[i]: node i+1 -> node i

        Dll(String[] vals, double cx, double y, double pitch, boolean circular) {
            for (int i = 0; i < vals.length; i++) {
                LNode node = dnode(vals[i], cx + (i - (vals.length - 1) / 2.0) * pitch, y, NODE);
                if (!circular && i == 0) node.nullPrev();
                if (!circular && i == vals.length - 1) node.nullNext();
                n.add(node);
            }
            for (int i = 0; i + 1 < vals.length; i++) {
                nx.add(nextLink(n.get(i), n.get(i + 1)));
                pv.add(prevLink(n.get(i + 1), n.get(i)));
            }
        }

        void build(List<Animation> into, double stagger, double dur) {
            for (int i = 0; i < n.size(); i++) {
                n.get(i).fadeIn(into, stagger * i, dur);
                if (i < nx.size()) {
                    into.add(new DrawLinkAt(nx.get(i), stagger * i + dur * 0.5, dur));
                    into.add(new DrawLinkAt(pv.get(i), stagger * i + dur * 0.5, dur));
                }
            }
        }

        List<MObject> parts() {
            List<MObject> l = new ArrayList<>();
            for (LNode x : n) l.addAll(x.all);
            l.addAll(nx);
            l.addAll(pv);
            return l;
        }
    }


    private void flashNode(LNode n, Color c) {
        List<Animation> a = new ArrayList<>();
        n.paint(a, c, d(0.3));
        playAll(a);
    }

    private void restore(LNode n) {
        List<Animation> a = new ArrayList<>();
        n.paint(a, NODE, d(0.3));
        playAll(a);
    }

    // ── slide 17 ─────────────────────────────────────────────────────

    private void dllSlide() {
        head = writeHeading("Doubly Linked List");
        pause(0.4);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Links in both the directions."));
        s.add(ln(0, "Node structure contains two pointers: next and").kw("next", Colors.TEAL));
        s.add(ln(3, "previous.").kw("previous", Colors.PINK));
        s.add(ln(0, "Deletion now becomes simpler."));
        s.add(ln(0, "Two pointers: head and tail maintain list ends.").kw("head", Colors.GOLD).kw("tail", Colors.PINK));
        s.add(ln(0, "Classwork: Write a function to remove a node.").kw("Classwork", Colors.RED));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(1.2);
        swipeAway(text);
        pause(0.4);

        nodeStructure();
        removeNode();
        fadeOutAll(d(0.9), head);
        pause(0.3);
    }

    private void nodeStructure() {
        List<MObject> mine = new ArrayList<>();
        CodeBox code = new CodeBox(new String[]{
                "struct Node {",
                "  int val;",
                "  Node *next;",
                "  Node *previous;",
                "};"}, -880, -250, 34, 58);
        code.typeIn(3.2);
        mine.addAll(code.parts());
        LNode nd = dnode("7", 330, -80, NODE);
        List<Animation> in = new ArrayList<>();
        nd.fadeIn(in, 0, d(0.7));
        playAll(in);
        mine.addAll(nd.all);
        play(new FadeIn(code.hl, d(0.4)));

        // each field of the struct, pointed at on the node
        double ly = 130;
        code.setLine(1);
        play(code.moveHl(1, d(0.1)));
        Link a1 = arrow(nd.x, ly - 30, nd.x, nd.bottom() + 8, Colors.GOLD, 3.4);
        TextMob l1 = mono("val", nd.x, ly, 32, Colors.GOLD);
        play(new DrawLink(a1, d(0.6)), new FadeIn(l1, d(0.6)));
        mine.add(a1);
        mine.add(l1);
        pause(0.9);
        play(code.moveHl(2, d(0.5)));
        Link a2 = arrow(nd.x + 180, ly - 30, nd.nextX() + 10, nd.nextY() + 18, Colors.TEAL, 3.4);
        TextMob l2 = mono("next", nd.x + 180, ly, 32, Colors.TEAL);
        play(new DrawLink(a2, d(0.6)), new FadeIn(l2, d(0.6)));
        mine.add(a2);
        mine.add(l2);
        pause(0.9);
        play(code.moveHl(3, d(0.5)));
        Link a3 = arrow(nd.x - 190, ly - 30, nd.prevX() - 10, nd.prevY() + 18, Colors.PINK, 3.4);
        TextMob l3 = mono("previous", nd.x - 190, ly, 32, Colors.PINK);
        play(new DrawLink(a3, d(0.6)), new FadeIn(l3, d(0.6)));
        mine.add(a3);
        mine.add(l3);
        pause(1.4);

        StrokeTextMob cap = stroke("Every node knows both its neighbours.", 330, 300, 42, Colors.WHITE, false);
        play(new Write(cap, d(2.2)));
        mine.add(cap);
        pause(2.2);
        fadeOutAll(d(0.9), mine);
        pause(0.3);
    }

    private void removeNode() {
        List<MObject> everything = new ArrayList<>();
        Dll dll = new Dll(new String[]{"4", "2", "7", "2", "9", "5"}, 0, -250, 230, false);
        LNode first = dll.n.get(0), last = dll.n.get(5);
        Link headArrow = arrow(first.leftX() - 150, first.y, first.leftX() - 3, first.y, Colors.GOLD, 3.6);
        TextMob headLab = label("head", first.leftX() - 85, first.y - 36, 30, Colors.GOLD, false, true);
        Link tailArrow = arrow(last.rightX() + 150, last.y, last.rightX() + 3, last.y, Colors.PINK, 3.6);
        TextMob tailLab = label("tail", last.rightX() + 85, last.y - 36, 30, Colors.PINK, false, true);
        List<Animation> in = new ArrayList<>();
        dll.build(in, d(0.3), d(0.6));
        playAll(in);
        play(new DrawLink(headArrow, d(0.6)), new FadeIn(headLab, d(0.6)), new DrawLink(tailArrow, d(0.6)),
                new FadeIn(tailLab, d(0.6)));
        everything.addAll(dll.parts());
        everything.add(headArrow);
        everything.add(headLab);
        everything.add(tailArrow);
        everything.add(tailLab);
        StrokeTextMob c1 = stroke("Links in both directions, with head and tail at the ends.", 0, -110, 40, Colors.WHITE, false);
        play(new Write(c1, d(3.0)));
        pause(1.8);
        play(new FadeOut(c1, d(0.5)));
        remove(c1);

        // the classwork
        StrokeTextMob cw = stroke("Classwork: write a function to remove a node.", 0, -105, 44, Colors.ORANGE, false);
        play(new Write(cw, d(2.8)));
        everything.add(cw);
        CodeBox code = new CodeBox(new String[]{
                "void remove(Node *p) {",
                "  if (p->previous) p->previous->next = p->next;",
                "  else head = p->next;",
                "  if (p->next) p->next->previous = p->previous;",
                "  else tail = p->previous;",
                "  delete p;",
                "}"}, -880, -20, 30, 52);
        code.typeIn(4.0);
        everything.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.4)));
        pause(0.4);

        double nx = 520, ny = 40;

        // remove the third node (7): it sits between two others
        LNode k = dll.n.get(2), a = dll.n.get(1), b = dll.n.get(3);
        Ptr p = below("p", k, Colors.ORANGE);
        play(new FadeIn(p.arrow, d(0.5)), new FadeIn(p.lab, d(0.5)));
        everything.addAll(p.parts());
        flashNode(k, Colors.RED);
        sayAt("remove(p) for the node holding 7", Colors.LIGHT_GRAY, nx, ny, 34);
        pause(0.8);
        code.setLine(1);
        play(code.moveHl(1, d(0.4)));
        flashNode(a, Colors.PINK);
        sayAt("the node before p now skips over p", Colors.LIGHT_GRAY, nx, ny, 34);
        double[][] r1 = nextRoute(a, b);
        play(new LinkTo(dll.nx.get(1), r1[0], r1[1], d(0.9)));
        pause(0.5);
        restore(a);
        play(code.moveHl(3, d(0.4)));
        flashNode(b, Colors.PINK);
        sayAt("the node after p now points back past p", Colors.LIGHT_GRAY, nx, ny, 34);
        double[][] r2 = prevRoute(b, a);
        play(new LinkTo(dll.pv.get(2), r2[0], r2[1], d(0.9)));
        pause(0.5);
        restore(b);
        play(code.moveHl(5, d(0.4)));
        List<Animation> del = new ArrayList<>();
        k.fadeOut(del, d(0.8));
        del.add(new EraseLink(dll.nx.get(2), d(0.6)));
        del.add(new EraseLink(dll.pv.get(1), d(0.6)));
        del.add(new FadeOut(p.arrow, d(0.5)));
        del.add(new FadeOut(p.lab, d(0.5)));
        playAll(del);
        sayAt("delete p: gone, with no scanning for neighbours", Colors.GREEN, nx, ny, 34);
        pause(1.8);

        // remove the head
        LNode h = dll.n.get(0), h1 = dll.n.get(1);
        Ptr p2 = below("p", h, Colors.ORANGE);
        play(new FadeIn(p2.arrow, d(0.4)), new FadeIn(p2.lab, d(0.4)));
        everything.addAll(p2.parts());
        flashNode(h, Colors.RED);
        sayAt("now remove the head", Colors.LIGHT_GRAY, nx, ny, 34);
        play(code.moveHl(1, d(0.4)));
        sayAt("p->previous is NULL: nothing to fix before it", Colors.LIGHT_GRAY, nx, ny, 34);
        pause(0.8);
        play(code.moveHl(2, d(0.4)));
        sayAt("so head = p->next", Colors.GREEN, nx, ny, 34);
        play(new LinkTo(headArrow, new double[]{headArrow.xs()[0], h1.leftX() - 3}, new double[]{first.y, first.y}, d(0.9)));
        pause(0.5);
        play(code.moveHl(3, d(0.4)));
        List<Animation> pn = new ArrayList<>();
        pn.add(new EraseLink(dll.pv.get(0), d(0.6)));
        h1.makePrevNull(pn, d(0.5));
        playAll(pn);
        play(code.moveHl(5, d(0.4)));
        List<Animation> d2 = new ArrayList<>();
        h.fadeOut(d2, d(0.8));
        d2.add(new EraseLink(dll.nx.get(0), d(0.6)));
        d2.add(new FadeOut(p2.arrow, d(0.5)));
        d2.add(new FadeOut(p2.lab, d(0.5)));
        playAll(d2);
        pause(1.4);

        // remove the tail
        LNode t = dll.n.get(5), t4 = dll.n.get(4);
        Ptr p3 = below("p", t, Colors.ORANGE);
        play(new FadeIn(p3.arrow, d(0.4)), new FadeIn(p3.lab, d(0.4)));
        everything.addAll(p3.parts());
        flashNode(t, Colors.RED);
        sayAt("and the tail", Colors.LIGHT_GRAY, nx, ny, 34);
        play(code.moveHl(1, d(0.4)));
        List<Animation> pt = new ArrayList<>();
        pt.add(new EraseLink(dll.nx.get(4), d(0.6)));
        t4.makeNull(pt, d(0.5));
        playAll(pt);
        play(code.moveHl(3, d(0.4)));
        sayAt("p->next is NULL: nothing after it", Colors.LIGHT_GRAY, nx, ny, 34);
        pause(0.8);
        play(code.moveHl(4, d(0.4)));
        sayAt("so tail = p->previous", Colors.GREEN, nx, ny, 34);
        play(new LinkTo(tailArrow, new double[]{tailArrow.xs()[0], t4.rightX() + 3}, new double[]{last.y, last.y}, d(0.9)));
        pause(0.5);
        play(code.moveHl(5, d(0.4)));
        List<Animation> d3 = new ArrayList<>();
        t.fadeOut(d3, d(0.8));
        d3.add(new EraseLink(dll.pv.get(4), d(0.6)));
        d3.add(new FadeOut(p3.arrow, d(0.5)));
        d3.add(new FadeOut(p3.lab, d(0.5)));
        playAll(d3);
        sayAt("one function handles middle, head and tail: O(1)", Colors.GREEN, nx, ny, 34);
        pause(2.4);
        fadeOutAll(d(1.0), everything);
        unsay();
        pause(0.3);
    }

    // ── slide 18 ─────────────────────────────────────────────────────

    private void cdllSlide() {
        head = writeHeading("Circular Doubly Linked List");
        pause(0.4);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Last element points to the first, and first"));
        s.add(ln(3, "element's previous is the last node."));
        s.add(ln(0, "Node structure continues to contain two"));
        s.add(ln(3, "pointers: next and previous."));
        s.add(ln(0, "Tail pointer is not required.").kw("not", Colors.BLUE));
        s.add(ln(0, "A singly linked list can also be circular."));
        s.add(ln(0, "Classwork: Write a function to print all the node").kw("Classwork", Colors.RED));
        s.add(ln(3, "values in a CDLL."));
        List<List<MObject>> text = writeSlide(s, -350);
        pause(1.2);
        swipeAway(text);
        pause(0.4);

        List<MObject> everything = new ArrayList<>();
        everything.add(head);
        double ry = -230;
        Dll cd = new Dll(new String[]{"4", "2", "7", "2", "9", "5"}, 0, ry, 230, true);
        LNode n0 = cd.n.get(0), n5 = cd.n.get(5);
        Ptr hp = above("head", n0, Colors.GOLD);
        List<Animation> in = new ArrayList<>();
        cd.build(in, d(0.3), d(0.6));
        playAll(in);
        play(new FadeIn(hp.arrow, d(0.5)), new FadeIn(hp.lab, d(0.5)));
        everything.addAll(cd.parts());
        everything.addAll(hp.parts());
        pause(0.6);

        // last.next -> first
        Link wn = new Link(
                new double[]{n5.nextX(), n5.rightX() + 50, n5.rightX() + 50, n0.leftX() - 50, n0.leftX() - 50, n0.leftX() - 3},
                new double[]{n5.nextY(), n5.nextY(), ry - 170, ry - 170, n0.nextY(), n0.nextY()},
                NEXT_LINK, 3.4, true);
        add(wn);
        StrokeTextMob c1 = stroke("The last element's next points to the first.", 0, 20, 42, Colors.WHITE, false);
        play(new Write(c1, d(2.4)));
        play(new DrawLink(wn, d(1.8)));
        everything.add(wn);
        pause(1.2);
        // first.previous -> last
        Link wp = new Link(
                new double[]{n0.prevX(), n0.leftX() - 90, n0.leftX() - 90, n5.rightX() + 90, n5.rightX() + 90, n5.rightX() + 3},
                new double[]{n0.prevY(), n0.prevY(), ry + 150, ry + 150, n5.prevY(), n5.prevY()},
                PREV_LINK, 3.4, true);
        add(wp);
        StrokeTextMob c2 = stroke("And the first element's previous is the last node.", 0, 85, 42, Colors.WHITE, false);
        play(new Write(c2, d(2.6)));
        play(new DrawLink(wp, d(1.8)));
        everything.add(wp);
        pause(1.2);
        StrokeTextMob c3 = stroke("No tail pointer needed: head->previous is the last node.", 0, 150, 42, Colors.GREEN, false);
        play(new Write(c3, d(3.0)));
        pause(2.4);
        fadeOutAll(d(0.7), List.of(c1, c2, c3));

        // a circular singly linked list
        List<Animation> out = new ArrayList<>();
        for (MObject m : cd.parts()) if (m.getOpacity() > 0) out.add(new FadeOut(m, d(0.8)));
        out.add(new FadeOut(wn, d(0.8)));
        out.add(new FadeOut(wp, d(0.8)));
        out.add(new FadeOut(hp.arrow, d(0.8)));
        out.add(new FadeOut(hp.lab, d(0.8)));
        playAll(out);
        Row cs = new Row(new String[]{"4", "2", "7", "2"}, 0, ry, 220, NODE);
        LNode s0 = cs.nodes.get(0), s3 = cs.nodes.get(3);
        Ptr sh = above("head", s0, Colors.GOLD);
        Link sw = new Link(
                new double[]{s3.nextX(), s3.rightX() + 60, s3.rightX() + 60, s0.leftX() - 60, s0.leftX() - 60, s0.leftX() - 3},
                new double[]{s3.y, s3.y, ry + 130, ry + 130, s0.y, s0.y}, NEXT_LINK, 3.4, true);
        add(sw);
        s3.nextNull = false;
        cs.build(d(0.3), d(0.6));
        play(new FadeIn(sh.arrow, d(0.5)), new FadeIn(sh.lab, d(0.5)));
        play(new DrawLink(sw, d(1.6)));
        everything.addAll(cs.parts());
        everything.addAll(sh.parts());
        everything.add(sw);
        StrokeTextMob c4 = stroke("A singly linked list can be circular too.", 0, 40, 44, Colors.WHITE, false);
        play(new Write(c4, d(2.4)));
        pause(2.6);
        List<Animation> out2 = new ArrayList<>();
        for (MObject m : cs.parts()) if (m.getOpacity() > 0) out2.add(new FadeOut(m, d(0.8)));
        out2.add(new FadeOut(sw, d(0.8)));
        out2.add(new FadeOut(sh.arrow, d(0.8)));
        out2.add(new FadeOut(sh.lab, d(0.8)));
        out2.add(new FadeOut(c4, d(0.8)));
        playAll(out2);

        // the classwork: print a CDLL
        List<Animation> back = new ArrayList<>();
        for (LNode x : cd.n) for (MObject m : x.shown()) back.add(new FadeIn(m, d(0.8)));
        for (Link l : cd.nx) back.add(new FadeIn(l, d(0.8)));
        for (Link l : cd.pv) back.add(new FadeIn(l, d(0.8)));
        back.add(new FadeIn(wn, d(0.8)));
        back.add(new FadeIn(wp, d(0.8)));
        back.add(new FadeIn(hp.arrow, d(0.8)));
        back.add(new FadeIn(hp.lab, d(0.8)));
        playAll(back);
        StrokeTextMob cw = stroke("Classwork: print all the node values in a CDLL.", 0, -10, 44, Colors.ORANGE, false);
        play(new Write(cw, d(2.8)));
        everything.add(cw);
        CodeBox code = new CodeBox(new String[]{
                "if (head == NULL) return;",
                "Node *ptr = head;",
                "do {",
                "  printf(\"%d \", ptr->val);",
                "  ptr = ptr->next;",
                "} while (ptr != head);"}, -880, 80, 32, 56);
        code.typeIn(3.6);
        everything.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.4)));
        TextMob outT = mono("Output:", 0, 200, 56, Colors.GOLD);
        outT.setAlignment(javafx.scene.text.TextAlignment.LEFT);
        play(new FadeIn(outT, d(0.5)));
        everything.add(outT);
        pause(0.4);

        code.setLine(0);
        play(code.moveHl(0, d(0.1)));
        sayAt("head is not NULL: carry on", Colors.LIGHT_GRAY, 500, 90, 34);
        pause(0.8);
        play(code.moveHl(1, d(0.4)));
        Ptr ptr = below("ptr", n0, Colors.ORANGE);
        play(new FadeIn(ptr.arrow, d(0.5)), new FadeIn(ptr.lab, d(0.5)));
        everything.addAll(ptr.parts());
        sayAt("ptr = head", Colors.LIGHT_GRAY, 500, 90, 34);
        pause(0.7);
        StringBuilder sb = new StringBuilder("Output:");
        for (int i = 0; i < 6; i++) {
            LNode n = cd.n.get(i);
            List<Animation> mv = new ArrayList<>();
            if (i > 0) ptr.go(mv, n.x, d(0.5));
            n.paint(mv, Colors.ORANGE, d(0.3));
            mv.add(code.moveHl(3, d(0.35)));
            playAll(mv);
            sb.append(' ').append(n.value);
            outT.setText(sb.toString());
            sayAt("print " + n.value, Colors.LIGHT_GRAY, 500, 90, 34);
            pause(0.35);
            play(code.moveHl(4, d(0.35)));
            restore(n);
            if (i < 5) {
                sayAt("ptr = ptr->next", Colors.LIGHT_GRAY, 500, 90, 34);
            } else {
                sayAt("ptr = ptr->next goes round to the head", Colors.ORANGE, 500, 90, 34);
                List<Animation> wrap = new ArrayList<>();
                ptr.go(wrap, n0.x, d(1.2));
                wrap.add(new ColorChange(wn, Colors.GOLD, d(0.5), ColorChange.Target.STROKE));
                playAll(wrap);
            }
            pause(0.3);
        }
        play(code.moveHl(5, d(0.4)));
        sayAt("ptr == head again: the loop stops (there is no NULL in a circle)", Colors.GREEN, 400, 90, 32);
        pause(2.8);
        fadeOutAll(d(1.2), everything);
        unsay();
        pause(0.4);
    }
}
