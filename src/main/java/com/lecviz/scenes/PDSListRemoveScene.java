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
 * Standalone clip for slide 15 of the lists deck: List remove.
 *
 * remove(2), remove(5) and remove(4) run on the list 4 2 7 2 9 5 with the slide's code beside it
 * and its pointers drawn on the list: ptr walks, previous trails one node behind, toberemoved marks
 * the node about to be deleted. The three calls show the three situations: a value in the middle
 * (twice), the last node, and the head, where head itself has to move.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListRemoveScene extends PDSListClipBase {

    private static final double ROW_Y = -255, CX = 350, PITCH = 165, RX = 340;
    private static final int L_SPECIAL = 1, L_PREV = 4, L_FOR = 5, L_IF = 6, L_TBR = 7, L_IFPREV = 8, L_PREVNEXT = 9,
            L_HEAD = 10, L_PTRNEXT = 11, L_DELETE = 12, L_REMOVED = 13, L_ELSE = 14, L_PREVPTR = 15, L_PTRNEXT2 = 16;

    private StrokeTextMob head;
    private CodeBox code;
    private Row row;
    private final List<Integer> live = new ArrayList<>();
    private Link headArrow;
    private TextMob headLab;
    private TextMob nullMark;
    private List<MObject> removedChip;
    private final List<MObject> everything = new ArrayList<>();
    private boolean firstCall = true;

    @Override
    public void construct() {
        head = writeHeading("List remove");
        pause(0.4);
        everything.add(head);

        // the list
        row = new Row(new String[]{"4", "2", "7", "2", "9", "5"}, CX, ROW_Y, PITCH, NODE);
        LNode n0 = row.nodes.get(0);
        headArrow = arrow(n0.leftX() - 120, n0.y, n0.leftX() - 3, n0.y, Colors.GOLD, 3.6);
        headLab = label("head", n0.leftX() - 70, n0.y - 36, 30, Colors.GOLD, false, true);
        play(new DrawLink(headArrow, d(0.6)), new FadeIn(headLab, d(0.6)));
        row.build(d(0.3), d(0.6));
        everything.addAll(row.parts());
        everything.add(headArrow);
        everything.add(headLab);
        for (int i = 0; i < 6; i++) live.add(i);

        // the slide's three calls and its note
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "remove(2)"));
        s.add(ln(0, "remove(5)"));
        s.add(ln(0, "remove(4)"));
        List<List<MObject>> bullets = writeSlide(s, -120);
        StrokeTextMob n1 = strokeLeft("We want to remove all", -880, 150, 36, Colors.LIGHT_GRAY);
        StrokeTextMob n2 = strokeLeft("occurrences of the value.", -880, 200, 36, Colors.LIGHT_GRAY);
        play(new Write(n1, d(1.8)));
        play(new Write(n2, d(1.8)));
        pause(1.6);
        swipeAway(bullets);
        play(new FadeOut(n1, d(0.5)), new FadeOut(n2, d(0.5)));
        remove(n1);
        remove(n2);

        code = new CodeBox(new String[]{
                "Special case:",
                "  if (head == NULL) return false;",
                "",
                "General case:",
                "  Node *previous = NULL;",
                "  for (Node *ptr = head; ptr;) {",
                "    if (ptr->val == val) {",
                "      Node *toberemoved = ptr;",
                "      if (previous) {",
                "        previous->next = ptr->next;",
                "      } else head = ptr->next;",
                "      ptr = ptr->next;",
                "      delete toberemoved;",
                "      removed = true;",
                "    } else {",
                "      previous = ptr;",
                "      ptr = ptr->next;",
                "    }",
                "  }"}, -880, -380, 26, 38);
        code.typeIn(6.0);
        everything.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.4)));
        pause(0.4);

        removeAll(2, 1.0);
        removeAll(5, 0.75);
        removeAll(4, 0.75);

        StrokeTextMob cost = stroke("remove walks the list: O(N).", RX + 120, 200, 40, Colors.ORANGE, false);
        play(new Write(cost, d(2.0)));
        everything.add(cost);
        pause(2.4);
        fadeOutAll(d(1.2), everything);
        unsay();
        pause(0.4);
    }

    // ── one remove(val) ──────────────────────────────────────────────

    private void line(int i, double dur) { play(code.moveHl(i, dur)); }

    private Ptr lowPtr(String name, LNode n, Color c) {
        Ptr p = below(name, n, c);
        p.lab.setFontSize(22);
        return p;
    }

    private void paintNode(LNode n, Color c, double dur, List<Animation> into) { n.paint(into, c, dur); }

    private void removeAll(int val, double sp) {
        StrokeTextMob call = stroke("remove(" + val + ")", RX + 120, -90, 64, Colors.WHITE, true);
        play(new Write(call, d(1.2)));
        everything.add(call);
        TextMob valLab = null;

        if (firstCall) {
            line(L_SPECIAL, d(0.4));
            sayAt("head is not NULL: carry on", Colors.LIGHT_GRAY, RX + 120, 40, 34);
            pause(0.9 * sp);
        }
        line(L_PREV, d(0.4 * sp));

        // previous = NULL parked left of the list
        LNode first = row.nodes.get(live.get(0));
        double parkX = first.leftX() - 70;
        Ptr prev = pointer("previous", parkX, ROW_Y + 44, false, Colors.TEAL);
        prev.lab.setFontSize(22);
        TextMob nullTag = mono("NULL", parkX, ROW_Y + 128, 22, Colors.GRAY);
        List<Animation> pin = new ArrayList<>();
        pin.add(new FadeIn(prev.arrow, d(0.4 * sp)));
        pin.add(new FadeIn(prev.lab, d(0.4 * sp)));
        pin.add(new FadeIn(nullTag, d(0.4 * sp)));
        playAll(pin);
        sayAt("previous = NULL: nothing is before head", Colors.LIGHT_GRAY, RX + 120, 40, 34);
        pause(0.7 * sp);

        line(L_FOR, d(0.4 * sp));
        Ptr ptr = above("ptr", first, Colors.ORANGE);
        play(new FadeIn(ptr.arrow, d(0.4 * sp)), new FadeIn(ptr.lab, d(0.4 * sp)));
        sayAt("ptr = head", Colors.LIGHT_GRAY, RX + 120, 40, 34);
        pause(0.6 * sp);

        int p = -1;
        int i = 0;
        while (i < live.size()) {
            int idx = live.get(i);
            LNode n = row.nodes.get(idx);
            int q = i + 1 < live.size() ? live.get(i + 1) : -1;
            // ptr is at n
            List<Animation> at = new ArrayList<>();
            ptr.go(at, n.x, d(0.5 * sp));
            paintNode(n, Colors.ORANGE, d(0.3 * sp), at);
            at.add(code.moveHl(L_IF, d(0.3 * sp)));
            playAll(at);
            boolean hit = Integer.parseInt(n.value) == val;
            sayAt(n.value + " == " + val + " ?   " + (hit ? "yes" : "no"), hit ? Colors.GREEN : Colors.LIGHT_GRAY, RX + 120, 40, 36);
            pause(0.5 * sp);

            if (hit) {
                // toberemoved = ptr
                List<Animation> pa = new ArrayList<>();
                paintNode(n, DOOMED, d(0.3 * sp), pa);
                playAll(pa);
                Ptr tbr = lowPtr("toberemoved", n, Colors.RED);
                line(L_TBR, d(0.3 * sp));
                play(new FadeIn(tbr.arrow, d(0.35 * sp)), new FadeIn(tbr.lab, d(0.35 * sp)));
                sayAt("toberemoved = ptr: remember this node", Colors.LIGHT_GRAY, RX + 120, 40, 34);
                pause(0.6 * sp);
                line(L_IFPREV, d(0.3 * sp));
                pause(0.3 * sp);
                if (p >= 0) {
                    line(L_PREVNEXT, d(0.35 * sp));
                    sayAt("previous->next = ptr->next: skip over it", Colors.GREEN, RX + 120, 40, 34);
                    List<Animation> by = new ArrayList<>();
                    LNode pn = row.nodes.get(p);
                    if (q >= 0) {
                        double[][] r = nextRoute(pn, row.nodes.get(q));
                        by.add(new LinkTo(row.links.get(p), r[0], r[1], d(0.8 * sp)));
                    } else {
                        by.add(new EraseLink(row.links.get(p), d(0.6 * sp)));
                        pn.makeNull(by, d(0.5 * sp));
                    }
                    playAll(by);
                } else {
                    line(L_HEAD, d(0.35 * sp));
                    sayAt("no previous: head = ptr->next", Colors.GREEN, RX + 120, 40, 34);
                    LNode nx = row.nodes.get(q);
                    List<Animation> hd = new ArrayList<>();
                    hd.add(new LinkTo(headArrow, new double[]{headArrow.xs()[0], nx.leftX() - 3},
                            new double[]{ROW_Y, ROW_Y}, d(0.9 * sp)));
                    playAll(hd);
                }
                pause(0.4 * sp);
                // ptr = ptr->next
                line(L_PTRNEXT, d(0.3 * sp));
                List<Animation> adv = new ArrayList<>();
                if (q >= 0) ptr.go(adv, row.nodes.get(q).x, d(0.5 * sp));
                else ptr.go(adv, nullX(), d(0.6 * sp));
                if (q < 0) adv.add(new FadeIn(nullMark(), d(0.4 * sp)));
                playAll(adv);
                // delete toberemoved
                line(L_DELETE, d(0.3 * sp));
                List<Animation> del = new ArrayList<>();
                n.fadeOut(del, d(0.8 * sp));
                if (idx < row.links.size()) del.add(new EraseLink(row.links.get(idx), d(0.6 * sp)));
                del.add(new FadeOut(tbr.arrow, d(0.5 * sp)));
                del.add(new FadeOut(tbr.lab, d(0.5 * sp)));
                playAll(del);
                sayAt("delete toberemoved: the node is freed", Colors.ORANGE, RX + 120, 40, 34);
                line(L_REMOVED, d(0.3 * sp));
                showRemoved();
                pause(0.5 * sp);
                live.remove(i);
            } else {
                line(L_ELSE, d(0.3 * sp));
                sayAt("not equal: move previous and ptr on", Colors.LIGHT_GRAY, RX + 120, 40, 34);
                List<Animation> adv = new ArrayList<>();
                paintNode(n, NODE, d(0.3 * sp), adv);
                playAll(adv);
                line(L_PREVPTR, d(0.3 * sp));
                List<Animation> pv = new ArrayList<>();
                prev.go(pv, n.x, d(0.45 * sp));
                if (p < 0) pv.add(new FadeOut(nullTag, d(0.3 * sp)));
                playAll(pv);
                p = idx;
                line(L_PTRNEXT2, d(0.3 * sp));
                if (q >= 0) {
                    List<Animation> go = new ArrayList<>();
                    ptr.go(go, row.nodes.get(q).x, d(0.45 * sp));
                    playAll(go);
                }
                i++;
            }
        }

        // ptr is NULL: the loop ends
        List<Animation> end = new ArrayList<>();
        if (ptr.arrow.getOpacity() > 0 && !nullShown()) {
            ptr.go(end, nullX(), d(0.6 * sp));
            end.add(new FadeIn(nullMark(), d(0.4 * sp)));
        }
        playAll(end);
        sayAt("ptr is NULL: the loop ends", Colors.GREEN, RX + 120, 40, 34);
        line(L_FOR, d(0.3 * sp));
        pause(1.2 * sp);
        List<Animation> clean = new ArrayList<>();
        for (int k : live) paintNode(row.nodes.get(k), NODE, d(0.3), clean);
        clean.add(new FadeOut(ptr.arrow, d(0.5)));
        clean.add(new FadeOut(ptr.lab, d(0.5)));
        clean.add(new FadeOut(prev.arrow, d(0.5)));
        clean.add(new FadeOut(prev.lab, d(0.5)));
        if (nullTag.getOpacity() > 0) clean.add(new FadeOut(nullTag, d(0.5)));
        clean.add(new FadeOut(call, d(0.5)));
        if (nullMark != null && nullMark.getOpacity() > 0) clean.add(new FadeOut(nullMark, d(0.5)));
        playAll(clean);
        nullMark = null;
        everything.addAll(ptr.parts());
        everything.addAll(prev.parts());
        everything.add(nullTag);
        firstCall = false;
        pause(0.5);
    }

    private double nullX() { return row.nodes.get(5).x + 20; }

    private boolean nullShown() { return nullMark != null && nullMark.getOpacity() > 0; }

    private TextMob nullMark() {
        if (nullMark == null) {
            nullMark = mono("NULL", nullX(), ROW_Y, 28, Colors.GRAY);
            everything.add(nullMark);
        }
        return nullMark;
    }

    private void showRemoved() {
        if (removedChip == null) {
            removedChip = chip("removed = true", RX + 120, 330, 380, 70, Colors.GREEN, 36);
            everything.addAll(removedChip);
            List<Animation> a = new ArrayList<>();
            fade(a, removedChip, d(0.5));
            playAll(a);
        } else {
            play(new ScaleTo(removedChip.get(1), 1.25, d(0.2)));
            play(new ScaleTo(removedChip.get(1), 1.0, d(0.25)));
        }
    }
}
