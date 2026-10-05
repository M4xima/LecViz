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
 * Standalone clip for slide 16 of the lists deck: pitfalls.
 *
 * The slide's lines are written one at a time (code with its comment in small print), then each
 * pitfall is shown happening:
 *   1  ptr = head->next on an empty list: the pointer follows address 0 and the program crashes
 *   2  a pointer to a local variable: the function returns, its frame disappears, and the pointer
 *      dangles
 *   3  malloc(sizeof(Node*)) asks for a pointer's worth of bytes, but a Node needs more
 *   4  the wrong deleteList: free(ptr) and then ptr->next reads freed memory; the fix saves next first
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListPitfallsScene extends PDSListClipBase {

    private StrokeTextMob head;

    @Override
    public void construct() {
        head = writeHeading("Pitfalls");
        pause(0.5);

        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "ptr = head->next;").tail("// segfault. Check if head is NULL.").gap(60));
        s.add(ln(0, "Node *ptr = &node1; return;").tail("// local variable node1.").gap(40));
        s.add(ln(0, "ptr = malloc(sizeof(Node*));").tail("// insufficient memory.").gap(40));
        s.add(ln(3, "Wrong deleteList program").gap(12));
        s.add(ln(3, "for (ptr = head; ptr; ptr = ptr->next)"));
        s.add(ln(3, "    free(ptr);").tail("// invalid memory on free.").gap(130));
        s.add(ln(3, "                // may work but wrong."));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(1.6);
        swipeAway(text);
        pause(0.4);

        nullDeref();
        danglingLocal();
        smallMalloc();
        wrongDelete();

        fadeOutAll(d(1.0), head);
        pause(0.4);
    }

    // ── helpers ──────────────────────────────────────────────────────

    private TextMob monoLeft(String text, double x, double y, double size, Color c) {
        TextMob t = label(text, x, y, size, c, true, false);
        t.setFontFamily("Menlo");
        return t;
    }

    /** The stage's code line and comment at the top left. Returns the objects. */
    private List<MObject> stageTitle(String num, String code, String comment) {
        List<MObject> made = new ArrayList<>();
        StrokeTextMob t = strokeLeft(num + "   " + code, -880, -340, 46, Colors.WHITE);
        TextMob c = monoLeft(comment, -880, -280, 28, Colors.MAROON);
        play(new Write(t, d(2.0)));
        play(new FadeIn(c, d(0.6)));
        made.add(t);
        made.add(c);
        return made;
    }

    private void shake(MObject m, double x, double y) {
        for (int k = 0; k < 8; k++) {
            play(new MoveTo(m, x + (k % 2 == 0 ? 16 : -16), y, 0.05));
        }
        play(new MoveTo(m, x, y, 0.05));
    }

    // ── 1. head is NULL ──────────────────────────────────────────────

    private void nullDeref() {
        List<MObject> st = stageTitle("1.", "ptr = head->next;", "// segfault. Check if head is NULL.");

        RectMob headBox = panel(-330, -20, 230, 96, Colors.GOLD, 0.15);
        TextMob headName = label("head", -330, -88, 32, Colors.GOLD, false, true);
        TextMob headVal = mono("NULL", -330, -20, 36, Colors.GRAY);
        RectMob zero = panel(300, -20, 380, 130, Colors.RED, 0.14);
        TextMob zeroLab = mono("address 0", 300, -40, 36, Colors.WHITE);
        TextMob zeroSub = label("not ours: off limits", 300, 15, 26, Colors.LIGHT_GRAY, false, false);
        List<Animation> in = new ArrayList<>();
        for (MObject m : new MObject[]{headBox, headName, headVal}) in.add(new FadeIn(m, d(0.6)));
        for (MObject m : new MObject[]{zero, zeroLab, zeroSub}) in.add(new FadeInAt(m, 0.4, d(0.6)));
        playAll(in);
        st.addAll(List.of(headBox, headName, headVal, zero, zeroLab, zeroSub));
        pause(0.6);

        Link follow = arrow(-205, -20, 100, -20, Colors.ORANGE, 4);
        TextMob how = mono("head->next", -60, -75, 32, Colors.ORANGE);
        play(new DrawLink(follow, d(1.0)), new FadeIn(how, d(0.6)));
        st.add(follow);
        st.add(how);
        pause(0.3);
        play(new ColorChange(zero, Colors.withAlpha(Colors.RED, 0.6), d(0.2)));
        StrokeTextMob boom = stroke("Segmentation fault!", 0, 180, 78, Colors.RED, true);
        play(new Write(boom, d(1.0)));
        shake(boom, 0, 180);
        st.add(boom);
        pause(1.0);

        List<MObject> fix = chip("if (head != NULL)  ptr = head->next;", 0, 320, 900, 74, Colors.GREEN, 36);
        List<Animation> a = new ArrayList<>();
        fade(a, fix, d(0.7));
        playAll(a);
        st.addAll(fix);
        TextMob chk = label("check before you follow the pointer", 0, 400, 30, Colors.GREEN, false, true);
        play(new FadeIn(chk, d(0.6)));
        st.add(chk);
        pause(2.4);
        fadeOutAll(d(0.9), st);
        pause(0.3);
    }

    // ── 2. pointer to a local variable ───────────────────────────────

    private void danglingLocal() {
        List<MObject> st = stageTitle("2.", "Node *ptr = &node1; return;", "// local variable node1.");

        RectMob frame = panel(-300, 40, 520, 380, Colors.BLUE, 0.08);
        TextMob frameLab = label("the function's stack frame", -300, -130, 28, Colors.BLUE, false, true);
        LNode node1 = node("5", -300, 30, Colors.BLUE);
        node1.nullNext();
        TextMob n1Lab = mono("node1", -300, 110, 30, Colors.WHITE);
        RectMob ptrBox = panel(330, 30, 190, 80, Colors.GOLD, 0.18);
        TextMob ptrLab = mono("ptr", 330, 30, 34, Colors.WHITE);
        Link pLink = arrow(235, 30, node1.rightX() + 6, 30, Colors.GOLD, 4);
        List<Animation> in = new ArrayList<>();
        in.add(new FadeIn(frame, d(0.7)));
        in.add(new FadeIn(frameLab, d(0.7)));
        node1.fadeIn(in, 0.3, d(0.7));
        in.add(new FadeInAt(n1Lab, 0.5, d(0.6)));
        in.add(new FadeInAt(ptrBox, 0.7, d(0.6)));
        in.add(new FadeInAt(ptrLab, 0.7, d(0.6)));
        playAll(in);
        play(new DrawLink(pLink, d(0.8)));
        st.addAll(List.of(frame, frameLab, n1Lab, ptrBox, ptrLab, pLink));
        st.addAll(node1.all);
        pause(1.0);

        // return: the frame goes away
        List<MObject> ret = chip("return;", 0, -150, 200, 62, Colors.ORANGE, 34);
        List<Animation> r = new ArrayList<>();
        fade(r, ret, d(0.5));
        playAll(r);
        st.addAll(ret);
        pause(0.5);
        List<Animation> gone = new ArrayList<>();
        gone.add(new FadeOut(frame, d(1.2)));
        gone.add(new FadeOut(frameLab, d(1.0)));
        gone.add(new FadeOut(n1Lab, d(1.0)));
        node1.fadeOut(gone, d(1.2));
        gone.add(new ColorChange(pLink, Colors.RED, d(0.6), ColorChange.Target.STROKE));
        gone.add(new ColorChange(ptrBox, Colors.withAlpha(Colors.RED, 0.25), d(0.8)));
        playAll(gone);
        TextMob q = label("?", -300, 30, 110, Colors.RED, false, true);
        play(new FadeIn(q, d(0.6)));
        st.add(q);
        StrokeTextMob dang = stroke("ptr now points to memory that no longer exists.", 0, 250, 40, Colors.RED, false);
        play(new Write(dang, d(2.6)));
        st.add(dang);
        StrokeTextMob fix = stroke("Allocate the node with new / malloc: it outlives the function.", 0, 320, 36, Colors.GREEN, false);
        play(new Write(fix, d(3.0)));
        st.add(fix);
        pause(2.6);
        fadeOutAll(d(0.9), st);
        pause(0.3);
    }

    // ── 3. malloc(sizeof(Node*)) ─────────────────────────────────────

    private void smallMalloc() {
        List<MObject> st = stageTitle("3.", "ptr = malloc(sizeof(Node*));", "// insufficient memory.");

        Cell[] bytes = new Cell[16];
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            Color c = i < 4 ? Colors.TEAL : (i < 8 ? Colors.GRAY : Colors.GOLD);
            bytes[i] = new Cell("", -420 + i * 56, 40, 50, 60, c, 20);
            bytes[i].fadeIn(in, 0.04 * i, d(0.4));
            st.addAll(bytes[i].parts());
        }
        TextMob lVal = label("val: 4 bytes", -364, 105, 26, Colors.TEAL, false, true);
        TextMob lPad = label("padding", -140, 105, 26, Colors.GRAY, false, true);
        TextMob lNext = label("next: 8 bytes", 168, 105, 26, Colors.GOLD, false, true);
        in.add(new FadeInAt(lVal, 0.7, d(0.5)));
        in.add(new FadeInAt(lPad, 0.8, d(0.5)));
        in.add(new FadeInAt(lNext, 0.9, d(0.5)));
        playAll(in);
        st.addAll(List.of(lVal, lPad, lNext));
        Link need = new Link(new double[]{-446, -446, 446, 446}, new double[]{-22, -38, -38, -22}, Colors.WHITE, 3.2, false);
        add(need);
        TextMob needLab = label("a Node needs 16 bytes", 0, -75, 36, Colors.WHITE, false, true);
        play(new DrawLink(need, d(0.9)), new FadeIn(needLab, d(0.9)));
        st.add(need);
        st.add(needLab);
        pause(1.2);

        // malloc hands out only sizeof(Node*) = 8 bytes
        Link got = new Link(new double[]{-446, -446, 2, 2}, new double[]{178, 194, 194, 178}, Colors.GREEN, 3.4, false);
        add(got);
        TextMob gotLab = label("malloc(sizeof(Node*)) gives only 8 bytes", -222, 235, 30, Colors.GREEN, false, true);
        List<Animation> g = new ArrayList<>();
        g.add(new DrawLink(got, d(0.9)));
        g.add(new FadeIn(gotLab, d(0.9)));
        for (int i = 0; i < 8; i++) bytes[i].color(g, Colors.GREEN, d(0.6));
        playAll(g);
        st.add(got);
        st.add(gotLab);
        pause(0.9);
        List<Animation> bad = new ArrayList<>();
        for (int i = 8; i < 16; i++) bytes[i].color(bad, Colors.RED, d(0.6));
        playAll(bad);
        StrokeTextMob lack = stroke("The last 8 bytes are not ours: insufficient memory.", 0, 320, 40, Colors.RED, false);
        play(new Write(lack, d(2.8)));
        st.add(lack);
        StrokeTextMob fix = stroke("sizeof(Node*) is a pointer's size. Use sizeof(Node) or new Node().", 0, 390, 34, Colors.GREEN, false);
        play(new Write(fix, d(3.2)));
        st.add(fix);
        pause(2.6);
        fadeOutAll(d(0.9), st);
        pause(0.3);
    }

    // ── 4. the wrong deleteList ──────────────────────────────────────

    private void wrongDelete() {
        List<MObject> st = new ArrayList<>();
        StrokeTextMob title = strokeLeft("4.   Wrong deleteList program", -880, -340, 46, Colors.WHITE);
        play(new Write(title, d(2.0)));
        st.add(title);

        CodeBox wrong = new CodeBox(new String[]{
                "for (ptr = head; ptr; ptr = ptr->next)",
                "  free(ptr);"}, -880, -250, 30, 52);
        wrong.typeIn(2.4);
        st.addAll(wrong.parts());
        TextMob c1 = monoLeft("// invalid memory on free.", -880, -122, 26, Colors.MAROON);
        TextMob c2 = monoLeft("// may work but wrong.", -880, -84, 26, Colors.MAROON);
        play(new FadeIn(c1, d(0.5)), new FadeInAt(c2, 0.4, d(0.5)));
        st.add(c1);
        st.add(c2);
        play(new FadeIn(wrong.hl, d(0.4)));

        Row row = new Row(new String[]{"4", "2", "7"}, 420, -230, 200, NODE);
        LNode n0 = row.nodes.get(0), n1 = row.nodes.get(1);
        Link headArrow = arrow(n0.leftX() - 110, n0.y, n0.leftX() - 3, n0.y, Colors.GOLD, 3.6);
        TextMob headLab = label("head", n0.leftX() - 62, n0.y - 36, 30, Colors.GOLD, false, true);
        play(new DrawLink(headArrow, d(0.6)), new FadeIn(headLab, d(0.6)));
        row.build(d(0.3), d(0.6));
        st.addAll(row.parts());
        st.add(headArrow);
        st.add(headLab);
        Ptr ptr = above("ptr", n0, Colors.ORANGE);
        play(new FadeIn(ptr.arrow, d(0.5)), new FadeIn(ptr.lab, d(0.5)));
        st.addAll(ptr.parts());
        pause(0.6);

        // free(ptr)
        wrong.setLine(1);
        play(wrong.moveHl(1, d(0.4)));
        List<Animation> fr = new ArrayList<>();
        n0.paint(fr, Colors.RED, d(0.5));
        playAll(fr);
        TextMob freed = label("freed", n0.x, n0.y + 70, 28, Colors.RED, false, true);
        play(new FadeIn(freed, d(0.4)));
        st.add(freed);
        pause(0.7);

        // ptr = ptr->next reads from the freed node
        play(wrong.moveHl(0, d(0.4)));
        TextMob bad = label("ptr->next reads freed memory!", n0.x + 150, n0.y + 130, 30, Colors.RED, false, true);
        Link rd = arrow(n0.nextX(), n0.y + 20, n0.nextX() + 60, n0.y + 105, Colors.RED, 3.4);
        play(new DrawLink(rd, d(0.6)), new FadeIn(bad, d(0.6)));
        st.add(bad);
        st.add(rd);
        pause(1.8);
        List<Animation> clear = new ArrayList<>();
        clear.add(new FadeOut(bad, d(0.5)));
        clear.add(new FadeOut(rd, d(0.5)));
        clear.add(new FadeOut(freed, d(0.5)));
        clear.add(new FadeOut(ptr.arrow, d(0.5)));
        clear.add(new FadeOut(ptr.lab, d(0.5)));
        playAll(clear);

        // the fix: remember next before freeing
        List<Animation> back = new ArrayList<>();
        n0.paint(back, NODE, d(0.6));
        playAll(back);
        StrokeTextMob fixT = stroke("The fix: remember next before freeing.", 440, 40, 40, Colors.GREEN, false);
        play(new Write(fixT, d(2.4)));
        st.add(fixT);
        CodeBox right = new CodeBox(new String[]{
                "for (ptr = head; ptr; ptr = next) {",
                "  next = ptr->next;",
                "  free(ptr);",
                "}"}, -880, 100, 30, 52);
        right.card.setFillColor(Colors.withAlpha(Colors.GREEN, 0.12));
        right.card.setStrokeColor(Colors.withAlpha(Colors.GREEN, 0.6));
        right.typeIn(3.0);
        st.addAll(right.parts());
        play(new FadeIn(right.hl, d(0.4)));

        Ptr p2 = above("ptr", n0, Colors.ORANGE);
        Ptr nx = below("next", n1, Colors.TEAL);
        List<Animation> sh = new ArrayList<>();
        sh.add(new FadeIn(p2.arrow, d(0.4)));
        sh.add(new FadeIn(p2.lab, d(0.4)));
        sh.add(new FadeIn(nx.arrow, d(0.4)));
        sh.add(new FadeIn(nx.lab, d(0.4)));
        playAll(sh);
        st.addAll(p2.parts());
        st.addAll(nx.parts());
        right.setLine(1);
        play(right.moveHl(1, d(0.3)));
        sayAt("next = ptr->next: saved before freeing", Colors.LIGHT_GRAY, 440, 200, 32);
        pause(0.9);
        for (int i = 0; i < 3; i++) {
            LNode n = row.nodes.get(i);
            play(right.moveHl(2, d(0.35)));
            List<Animation> f = new ArrayList<>();
            n.paint(f, Colors.RED, d(0.4));
            playAll(f);
            List<Animation> gone = new ArrayList<>();
            n.fadeOut(gone, d(0.7));
            if (i < 2) gone.add(new EraseLink(row.links.get(i), d(0.5)));
            playAll(gone);
            if (i < 2) {
                play(right.moveHl(0, d(0.3)));
                List<Animation> adv = new ArrayList<>();
                p2.go(adv, row.nodes.get(i + 1).x, d(0.5));
                if (i < 1) nx.go(adv, row.nodes.get(i + 2).x, d(0.5));
                else adv.add(new FadeOut(nx.arrow, d(0.4)));
                if (i >= 1) adv.add(new FadeOut(nx.lab, d(0.4)));
                playAll(adv);
                play(right.moveHl(1, d(0.3)));
                pause(0.3);
            }
        }
        List<Animation> tidy = new ArrayList<>();
        tidy.add(new FadeOut(headArrow, d(0.6)));
        tidy.add(new FadeOut(headLab, d(0.6)));
        tidy.add(new FadeOut(p2.arrow, d(0.6)));
        tidy.add(new FadeOut(p2.lab, d(0.6)));
        playAll(tidy);
        sayAt("every node is freed exactly once", Colors.GREEN, 440, 200, 34);
        pause(2.4);
        fadeOutAll(d(1.0), st);
        unsay();
        pause(0.3);
    }
}
