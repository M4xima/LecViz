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
 * Standalone clip for slide 10 of the lists deck: List insert. (Slide 9, the linked list
 * implementation / sll.cpp, is written below as implementation() but is not played for now.)
 *
 *   Slide 9   "Source: sll.cpp", then what is in it: the Node (a value and a pointer to the next
 *             node) and the List (just a head pointer), each field lit up on the picture
 *   Slide 10  insert(5) step by step: the setup (a node is allocated, its value set, its next set
 *             to NULL), the end case (an empty list: head becomes the new node) shown on an empty
 *             list, and the regular case: ptr walks until ptr->next is NULL, then the new node is
 *             linked in; the code highlight follows every step
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListInsertScene extends PDSListClipBase {

    private static final double ROW_Y = -300, PITCH = 180;

    private StrokeTextMob head;

    @Override
    public void construct() {
        // slide 9 (the sll.cpp / Node picture) is left out of the video for now:
        // implementation();
        insert();
    }

    private void flash(List<MObject> chip, Color c) {
        RectMob r = (RectMob) chip.get(0);
        play(new ColorChange(r, Colors.withAlpha(c, 0.8), d(0.15)));
        play(new ColorChange(r, Colors.withAlpha(c, 0.22), d(0.35)));
    }

    // ── slide 9 ──────────────────────────────────────────────────────

    private void implementation() {
        head = writeHeading("Linked List Implementation");
        pause(0.4);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Source: sll.cpp").kw("sll.cpp", Colors.MAROON));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(1.0);

        // what is in the file: the Node and the List
        CodeBox code = new CodeBox(new String[]{
                "struct Node {",
                "  int val;",
                "  Node *next;",
                "};",
                "class List {",
                "  Node *head;",
                "  // insert, find, ...",
                "};"}, -880, -190, 32, 56);
        TextMob file = mono("sll.cpp", -700, -250, 34, Colors.MAROON);
        file.setFontFamily("Menlo");
        play(new FadeIn(file, d(0.6)));
        code.typeIn(4.0);

        // the node picture: first node alone, with its two fields named
        Row row = new Row(new String[]{"4", "2", "7", "2"}, 230, 20, 205, NODE);
        LNode n0 = row.nodes.get(0);
        play(new FadeIn(n0.box, d(0.6)));
        List<Animation> f0 = new ArrayList<>();
        for (MObject m : n0.shown()) if (m != n0.box) f0.add(new FadeIn(m, d(0.6)));
        playAll(f0);
        pause(0.4);

        List<MObject> made = new ArrayList<>();
        play(new FadeIn(code.hl, d(0.4)));
        code.setLine(1);
        Link vArrow = arrow(n0.x - 22, n0.y + 120, n0.x - 22, n0.bottom() + 6, Colors.GOLD, 3.4);
        TextMob vLab = mono("int val", n0.x - 22, n0.y + 148, 28, Colors.GOLD);
        play(new DrawLink(vArrow, d(0.6)), new FadeIn(vLab, d(0.6)));
        made.add(vArrow);
        made.add(vLab);
        pause(0.8);
        play(code.moveHl(2, d(0.5)));
        Link nArrow = arrow(n0.nextX(), n0.y + 120, n0.nextX(), n0.bottom() + 6, Colors.GOLD, 3.4);
        TextMob nLab = mono("Node *next", n0.nextX() + 112, n0.y + 148, 28, Colors.GOLD);
        play(new DrawLink(nArrow, d(0.6)), new FadeIn(nLab, d(0.6)));
        made.add(nArrow);
        made.add(nLab);
        pause(1.2);

        // more nodes link up behind it; the list keeps only a head pointer
        play(code.moveHl(5, d(0.5)));
        List<Animation> rest = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            row.nodes.get(i).fadeIn(rest, d(0.3) * (i - 1), d(0.6));
        }
        for (int i = 0; i < 3; i++) rest.add(new DrawLinkAt(row.links.get(i), d(0.3) * i, d(0.6)));
        playAll(rest);
        Link headArrow = arrow(n0.leftX() - 130, n0.y, n0.leftX() - 3, n0.y, Colors.GOLD, 3.6);
        TextMob headLab = label("head", n0.leftX() - 78, n0.y - 36, 32, Colors.GOLD, false, true);
        play(new DrawLink(headArrow, d(0.6)), new FadeIn(headLab, d(0.6)));
        made.add(headArrow);
        made.add(headLab);
        StrokeTextMob cap = stroke("A node holds a value and a pointer to the next node.", 230, 250, 36, Colors.WHITE, false);
        StrokeTextMob cap2 = stroke("The list only remembers where it starts: head.", 230, 315, 36, Colors.GREEN, false);
        play(new Write(cap, d(3.0)));
        play(new Write(cap2, d(2.8)));
        made.add(cap);
        made.add(cap2);
        pause(2.6);

        swipeAway(text);
        List<MObject> all = new ArrayList<>(code.parts());
        all.add(file);
        all.addAll(row.parts());
        all.addAll(made);
        all.add(head);
        fadeOutAll(d(1.0), all);
        pause(0.4);
    }

    // ── slide 10 ─────────────────────────────────────────────────────

    private void insert() {
        head = writeHeading("List insert");
        pause(0.4);
        List<MObject> everything = new ArrayList<>();
        everything.add(head);

        // the list (six slots; the last one is where 5 will go)
        Row row = new Row(new String[]{"4", "2", "7", "2", "9", "5"}, 0, ROW_Y, PITCH, NODE);
        row.nodes.get(4).nullNext();
        LNode n0 = row.nodes.get(0);
        Link headArrow = arrow(n0.leftX() - 130, n0.y, n0.leftX() - 3, n0.y, Colors.GOLD, 3.6);
        TextMob headLab = label("head", n0.leftX() - 78, n0.y - 36, 32, Colors.GOLD, false, true);
        play(new DrawLink(headArrow, d(0.6)), new FadeIn(headLab, d(0.6)));
        List<Animation> build = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            row.nodes.get(i).fadeIn(build, d(0.3) * i, d(0.6));
            if (i < 4) build.add(new DrawLinkAt(row.links.get(i), d(0.3) * i + d(0.3), d(0.5)));
        }
        playAll(build);
        everything.addAll(row.parts());
        everything.add(headArrow);
        everything.add(headLab);

        StrokeTextMob call = strokeLeft("insert(5)", -880, -190, 46, Colors.WHITE);
        play(new Write(call, d(1.4)));
        everything.add(call);

        CodeBox code = new CodeBox(new String[]{
                "Setup node:",
                "  Node *newptr = new Node();",
                "  newptr->val = 5;",
                "  newptr->next = NULL;",
                "",
                "End case:",
                "  if (head == NULL) head = newptr;",
                "",
                "Regular case:",
                "  for (Node *ptr = head; ptr->next; ptr = ptr->next)",
                "    ;",
                "  ptr->next = newptr;"}, -880, -120, 30, 46);
        code.typeIn(5.0);
        everything.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.4)));

        // ── setup node ──
        double vy = 160;
        RectMob var = panel(440, vy, 180, 66, Colors.GOLD, 0.2);
        TextMob varLab = mono("newptr", 440, vy, 30, Colors.WHITE);
        LNode nw = node("?", 760, vy, NEW_NODE);
        double[] ax = {var.getPosition().x() + 90, nw.leftX() - 3};
        Link varArrow = new Link(ax, new double[]{vy, vy}, Colors.GOLD, 3.4, true);
        add(varArrow);
        code.setLine(1);
        play(code.moveHl(1, d(0.1)));
        List<Animation> a = new ArrayList<>();
        a.add(new FadeIn(var, d(0.6)));
        a.add(new FadeIn(varLab, d(0.6)));
        nw.fadeIn(a, 0.3, d(0.7));
        a.add(new DrawLinkAt(varArrow, 0.6, d(0.6)));
        playAll(a);
        sayAt("new Node(): a fresh node is allocated", Colors.LIGHT_GRAY, 440, -185, 30);
        everything.add(var);
        everything.add(varLab);
        everything.addAll(nw.all);
        everything.add(varArrow);
        pause(1.2);

        play(code.moveHl(2, d(0.5)));
        nw.text.setText("5");
        play(new ScaleTo(nw.text, 1.5, d(0.25)));
        play(new ScaleTo(nw.text, 1.0, d(0.3)));
        sayAt("its value is 5", Colors.LIGHT_GRAY, 440, -185, 30);
        pause(0.8);

        play(code.moveHl(3, d(0.5)));
        nw.nextNull = true;
        List<Animation> nl = new ArrayList<>();
        nl.add(new FadeOut(nw.nextDot, d(0.4)));
        nl.add(new FadeIn(nw.nextSlash, d(0.4)));
        playAll(nl);
        sayAt("and it points nowhere yet: next = NULL", Colors.LIGHT_GRAY, 440, -185, 30);
        pause(1.4);

        // ── end case: an empty list ──
        play(code.moveHl(6, d(0.5)));
        sayAt("End case: the list is empty", Colors.ORANGE, 440, -185, 30);
        List<Animation> clear = new ArrayList<>();
        for (int i = 0; i < 5; i++) row.nodes.get(i).fadeOut(clear, d(0.6));
        for (int i = 0; i < 4; i++) clear.add(new FadeOut(row.links.get(i), d(0.6)));
        playAll(clear);
        RectMob nullBox = panel(n0.x, ROW_Y, 128, NODE_H, Colors.GRAY, 0.12);
        TextMob nullLab = mono("NULL", n0.x, ROW_Y, 32, Colors.GRAY);
        play(new FadeIn(nullBox, d(0.5)), new FadeIn(nullLab, d(0.5)));
        pause(1.0);
        sayAt("head == NULL: so head = newptr", Colors.ORANGE, 440, -185, 30);
        List<Animation> take = new ArrayList<>();
        nw.moveTo(take, n0.x, ROW_Y, -120, d(1.1));
        take.add(new FadeOut(var, d(0.6)));
        take.add(new FadeOut(varLab, d(0.6)));
        take.add(new FadeOut(varArrow, d(0.6)));
        take.add(new FadeOut(nullBox, d(0.8)));
        take.add(new FadeOut(nullLab, d(0.8)));
        playAll(take);
        pause(1.4);

        // put things back for the regular case
        List<Animation> back = new ArrayList<>();
        nw.moveTo(back, 760, vy, 120, d(1.0));
        playAll(back);
        List<Animation> restore = new ArrayList<>();
        for (int i = 0; i < 5; i++) row.nodes.get(i).fadeIn(restore, 0, d(0.7));
        for (int i = 0; i < 4; i++) restore.add(new FadeIn(row.links.get(i), d(0.7)));
        restore.add(new FadeIn(var, d(0.6)));
        restore.add(new FadeIn(varLab, d(0.6)));
        playAll(restore);
        Link varArrow2 = new Link(new double[]{var.getPosition().x() + 90, nw.leftX() - 3}, new double[]{vy, vy}, Colors.GOLD, 3.4, true);
        add(varArrow2);
        play(new DrawLink(varArrow2, d(0.5)));
        everything.add(varArrow2);
        pause(0.6);

        // ── regular case ──
        play(code.moveHl(9, d(0.6)));
        sayAt("Regular case: walk to the last node", Colors.ORANGE, 440, -185, 30);
        Ptr ptr = above("ptr", n0, Colors.ORANGE);
        play(new FadeIn(ptr.arrow, d(0.5)), new FadeIn(ptr.lab, d(0.5)));
        everything.addAll(ptr.parts());
        for (int i = 0; i < 5; i++) {
            LNode n = row.nodes.get(i);
            List<Animation> mv = new ArrayList<>();
            if (i > 0) ptr.go(mv, n.x, d(0.5));
            n.paint(mv, Colors.ORANGE, d(0.35));
            playAll(mv);
            sayAt(i < 4 ? "ptr->next is not NULL: move on" : "ptr->next is NULL: this is the last node",
                    i < 4 ? Colors.LIGHT_GRAY : Colors.GREEN, 440, -185, 30);
            pause(0.55);
            if (i < 4) paintFade(n, NODE, 0.25);
        }
        pause(0.5);

        play(code.moveHl(11, d(0.6)));
        LNode last = row.nodes.get(4), slot = row.nodes.get(5);
        // the new node slides into the empty slot and ptr->next points to it
        List<Animation> link = new ArrayList<>();
        nw.moveTo(link, slot.x, ROW_Y, -80, d(1.2));
        last.unNull(link, d(0.5));
        playAll(link);
        List<Animation> lk = new ArrayList<>();
        lk.add(new DrawLinkAt(row.links.get(4), 0, d(0.7)));
        lk.add(new FadeOut(var, d(0.6)));
        lk.add(new FadeOut(varLab, d(0.6)));
        lk.add(new FadeOut(varArrow2, d(0.6)));
        playAll(lk);
        sayAt("ptr->next = newptr: the new node is linked in", Colors.GREEN, 440, -185, 30);
        List<Animation> calm = new ArrayList<>();
        last.paint(calm, NODE, d(0.4));
        calm.add(new FadeOut(ptr.arrow, d(0.5)));
        calm.add(new FadeOut(ptr.lab, d(0.5)));
        playAll(calm);
        pause(1.6);

        StrokeTextMob cost = stroke("Walking to the end costs O(N).", 520, 290, 40, Colors.ORANGE, false);
        StrokeTextMob cost2 = stroke("With a tail pointer it would be O(1).", 520, 355, 40, Colors.GREEN, false);
        play(new Write(cost, d(2.2)));
        play(new Write(cost2, d(2.6)));
        everything.add(cost);
        everything.add(cost2);
        pause(2.8);
        fadeOutAll(d(1.2), everything);
        unsay();
        pause(0.4);
    }

    private void paintFade(LNode n, Color c, double dur) {
        List<Animation> a = new ArrayList<>();
        n.paint(a, c, dur);
        playAll(a);
    }
}
