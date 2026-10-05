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
 * Standalone clip for slide 22 of the lists deck: recursive methods.
 *
 *   - the slide's lines one at a time
 *   - "natural to model": a list is a node followed by a smaller list, shown as nested braces
 *   - print recursively: the call stack grows down the list, prints on the way down, and unwinds
 *     (one stack frame per node: "sometimes inefficient")
 *   - "how to print in reverse?": swap two lines, and the printing happens on the way back up
 *   - find recursively: the stack grows until the value is found, then true returns up the stack
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListRecursionScene extends PDSListClipBase {

    private static final double CX = 480, ROW_Y = -255, PITCH = 165;

    private StrokeTextMob head;

    @Override
    public void construct() {
        head = writeHeading("Recursive Methods");
        pause(0.4);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Sometimes natural to model."));
        s.add(ln(0, "Sometimes inefficient to implement."));
        s.add(ln(0, "Classwork: find an element recursively.").kw("Classwork", Colors.RED));
        s.add(ln(0, "Classwork: print a list recursively.").kw("Classwork", Colors.RED));
        s.add(ln(1, "How to print in reverse?"));
        s.add(ln(1, "sll.cpp").kw("sll.cpp", Colors.MAROON));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(1.2);
        swipeAway(text);
        pause(0.4);

        natural();
        printList();
        findElement();
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void paint(LNode n, Color c, double dur) {
        List<Animation> a = new ArrayList<>();
        n.paint(a, c, dur);
        playAll(a);
    }

    // ── natural to model ─────────────────────────────────────────────

    private void natural() {
        List<MObject> mine = new ArrayList<>();
        Row row = new Row(new String[]{"4", "2", "7", "9"}, 0, -230, 240, NODE);
        LNode n0 = row.nodes.get(0);
        Link ha = arrow(n0.leftX() - 130, n0.y, n0.leftX() - 3, n0.y, Colors.GOLD, 3.6);
        TextMob hl = label("head", n0.leftX() - 78, n0.y - 36, 30, Colors.GOLD, false, true);
        play(new DrawLink(ha, d(0.6)), new FadeIn(hl, d(0.6)));
        row.build(d(0.3), d(0.6));
        mine.addAll(row.parts());
        mine.add(ha);
        mine.add(hl);
        StrokeTextMob t1 = stroke("A list is a node followed by a smaller list.", 0, 190, 44, Colors.WHITE, false);
        play(new Write(t1, d(2.8)));
        mine.add(t1);
        pause(0.6);

        // nested braces: node + the rest of the list, again and again
        double right = row.nodes.get(3).rightX() + 10;
        for (int i = 0; i < 4; i++) {
            LNode n = row.nodes.get(i);
            paint(n, Colors.ORANGE, d(0.4));
            double y = -150 + 62 * i;
            if (i < 3) {
                double left = row.nodes.get(i + 1).leftX() - 10;
                Link br = new Link(new double[]{left, left, right, right}, new double[]{y - 14, y, y, y - 14}, Colors.TEAL, 3.4, false);
                add(br);
                TextMob lab = label("the rest is a list again", (left + right) / 2, y + 30, 28, Colors.TEAL, false, true);
                play(new DrawLink(br, d(0.7)), new FadeIn(lab, d(0.7)));
                mine.add(br);
                mine.add(lab);
            } else {
                TextMob lab = label("and the rest is empty: NULL", n.x, y + 8, 28, Colors.GREEN, false, true);
                play(new FadeIn(lab, d(0.7)));
                mine.add(lab);
            }
            pause(0.5);
            paint(n, NODE, d(0.3));
        }
        StrokeTextMob t2 = stroke("So a function on a list can call itself on the rest.", 0, 300, 42, Colors.GREEN, false);
        play(new Write(t2, d(3.0)));
        mine.add(t2);
        pause(2.6);
        fadeOutAll(d(0.9), mine);
        pause(0.3);
    }

    // ── print, forwards and backwards ────────────────────────────────

    private CodeBox code;
    private Row row;
    private CallStack stack;
    private Ptr p;
    private TextMob out;
    private final List<MObject> stage = new ArrayList<>();
    private final String[] vals = {"4", "2", "7", "9"};
    private final Color[] fc = {Colors.TEAL, Colors.BLUE, Colors.PINK, Colors.GOLD, Colors.GRAY};

    /** The code on the left, the list on top right, the call stack under it. */
    private void setup(String[] src, int topLines) {
        stage.clear();
        code = new CodeBox(src, -880, -330, 28, 46);
        code.typeIn(3.4);
        stage.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.4)));
        row = new Row(vals, CX, ROW_Y, PITCH, NODE);
        LNode n0 = row.nodes.get(0);
        Link ha = arrow(n0.leftX() - 110, n0.y, n0.leftX() - 3, n0.y, Colors.GOLD, 3.6);
        TextMob hl = label("head", n0.leftX() - 62, n0.y - 36, 30, Colors.GOLD, false, true);
        play(new DrawLink(ha, d(0.6)), new FadeIn(hl, d(0.6)));
        row.build(d(0.3), d(0.6));
        stage.addAll(row.parts());
        stage.add(ha);
        stage.add(hl);
        stack = new CallStack(620, -30, 300, 54);
        stack.showTitle();
        stage.addAll(stack.all());
        p = above("p", n0, Colors.ORANGE);
        play(new FadeIn(p.arrow, d(0.4)), new FadeIn(p.lab, d(0.4)));
        stage.addAll(p.parts());
        out = mono("Output:", -860, 190, 54, Colors.GOLD);
        out.setAlignment(javafx.scene.text.TextAlignment.LEFT);
        play(new FadeIn(out, d(0.4)));
        stage.add(out);
    }

    private void goTo(int k, int line) {
        List<Animation> mv = new ArrayList<>();
        mv.add(code.moveHl(line, d(0.3)));
        if (k < 4) p.go(mv, row.nodes.get(k).x, d(0.5));
        playAll(mv);
    }

    private void printList() {
        setup(new String[]{
                "void print(Node *p) {",
                "  if (p == NULL) return;",
                "  printf(\"%d \", p->val);",
                "  print(p->next);",
                "}"}, 5);
        StrokeTextMob cw = stroke("Classwork: print a list recursively.", 0, -410, 44, Colors.ORANGE, false);
        play(new Write(cw, d(2.4)));
        stage.add(cw);
        StringBuilder sb = new StringBuilder("Output:");
        // down: print this node, then print the rest
        for (int k = 0; k < 4; k++) {
            goTo(k, 0);
            stack.push("print(p = " + vals[k] + ")", fc[k]);
            stage.addAll(stack.all());
            goTo(k, 2);
            sb.append(' ').append(vals[k]);
            out.setText(sb.toString());
            List<Animation> hi = new ArrayList<>();
            row.nodes.get(k).paint(hi, Colors.ORANGE, d(0.3));
            playAll(hi);
            if (k < 2) sayAt("print this node, then print(p->next)", Colors.LIGHT_GRAY, -400, 340, 34);
            goTo(k, 3);
            List<Animation> back = new ArrayList<>();
            row.nodes.get(k).paint(back, NODE, d(0.3));
            playAll(back);
        }
        // the base case
        List<Animation> toNull = new ArrayList<>();
        p.go(toNull, row.nodes.get(3).rightX() + 80, d(0.5));
        toNull.add(code.moveHl(0, d(0.3)));
        playAll(toNull);
        stack.push("print(p = NULL)", fc[4]);
        stage.addAll(stack.all());
        goTo(4, 1);
        sayAt("p is NULL: return. The list is done.", Colors.GREEN, -400, 340, 34);
        pause(0.9);
        stack.pop("return", Colors.GREEN);
        for (int k = 3; k >= 0; k--) {
            pause(0.2);
            stack.pop("return", Colors.GREEN);
        }
        pause(0.6);
        StrokeTextMob cap = stroke("One stack frame per node: O(N) extra memory.", -400, 430, 38, Colors.ORANGE, false);
        play(new Write(cap, d(2.6)));
        stage.add(cap);
        pause(2.2);

        // how to print in reverse? swap two lines
        play(new FadeOut(cap, d(0.4)));
        remove(cap);
        out.setText("Output:");
        sb.setLength(0);
        sb.append("Output:");
        StrokeTextMob q = stroke("How to print in reverse? Swap two lines.", 0, -410, 44, Colors.GREEN, false);
        play(new FadeOut(cw, d(0.4)));
        play(new Write(q, d(2.4)));
        stage.add(q);
        List<Animation> sw = new ArrayList<>();
        stage.addAll(code.rewrite(sw, 2, "  print(p->next);", d(0.7)));
        stage.addAll(code.rewrite(sw, 3, "  printf(\"%d \", p->val);", d(0.7)));
        playAll(sw);
        sayAt("now the printing happens after the call returns", Colors.LIGHT_GRAY, -400, 340, 34);
        pause(1.0);
        List<Animation> reset = new ArrayList<>();
        p.go(reset, row.nodes.get(0).x, d(0.4));
        playAll(reset);
        for (int k = 0; k < 4; k++) {
            goTo(k, 0);
            stack.push("print(p = " + vals[k] + ")", fc[k]);
            stage.addAll(stack.all());
            goTo(k, 2);
        }
        List<Animation> toNull2 = new ArrayList<>();
        p.go(toNull2, row.nodes.get(3).rightX() + 80, d(0.5));
        toNull2.add(code.moveHl(0, d(0.3)));
        playAll(toNull2);
        stack.push("print(p = NULL)", fc[4]);
        stage.addAll(stack.all());
        goTo(4, 1);
        pause(0.5);
        stack.pop("return", Colors.GREEN);
        // up: print each node as its call returns
        for (int k = 3; k >= 0; k--) {
            List<Animation> mv = new ArrayList<>();
            p.go(mv, row.nodes.get(k).x, d(0.5));
            mv.add(code.moveHl(3, d(0.3)));
            row.nodes.get(k).paint(mv, Colors.ORANGE, d(0.3));
            playAll(mv);
            sb.append(' ').append(vals[k]);
            out.setText(sb.toString());
            pause(0.4);
            stack.pop("return", Colors.GREEN);
            List<Animation> calm = new ArrayList<>();
            row.nodes.get(k).paint(calm, NODE, d(0.3));
            playAll(calm);
        }
        sayAt("9 7 2 4: the list printed backwards, with no extra list", Colors.GREEN, -400, 340, 34);
        pause(2.6);
        fadeOutAll(d(1.0), stage);
        unsay();
        pause(0.3);
    }

    // ── find recursively ─────────────────────────────────────────────

    private void findElement() {
        setup(new String[]{
                "bool find(Node *p, int val) {",
                "  if (p == NULL) return false;",
                "  if (p->val == val) return true;",
                "  return find(p->next, val);",
                "}"}, 5);
        out.setText("find(7) ?");
        StrokeTextMob cw = stroke("Classwork: find an element recursively.", 0, -410, 44, Colors.ORANGE, false);
        play(new Write(cw, d(2.6)));
        stage.add(cw);
        int target = 7;
        int found = -1;
        for (int k = 0; k < 4 && found < 0; k++) {
            goTo(k, 0);
            stack.push("find(p = " + vals[k] + ")", fc[k]);
            stage.addAll(stack.all());
            goTo(k, 1);
            sayAt("p is not NULL", Colors.LIGHT_GRAY, -400, 340, 34);
            pause(0.3);
            goTo(k, 2);
            List<Animation> hi = new ArrayList<>();
            boolean hit = Integer.parseInt(vals[k]) == target;
            row.nodes.get(k).paint(hi, hit ? Colors.GREEN : Colors.ORANGE, d(0.3));
            playAll(hi);
            sayAt(vals[k] + " == " + target + " ?   " + (hit ? "yes: return true" : "no"), hit ? Colors.GREEN : Colors.LIGHT_GRAY, -400, 340, 36);
            pause(0.5);
            if (hit) {
                found = k;
            } else {
                goTo(k, 3);
                List<Animation> back = new ArrayList<>();
                row.nodes.get(k).paint(back, NODE, d(0.3));
                playAll(back);
            }
        }
        // true returns up the whole stack
        pause(0.4);
        for (int k = found; k >= 0; k--) {
            stack.pop("true", Colors.GREEN);
            pause(0.15);
        }
        out.setText("find(7) = true");
        pause(0.8);
        StrokeTextMob cap = stroke("Found: true is handed back, frame by frame, to the caller.", -400, 430, 36, Colors.GREEN, false);
        play(new Write(cap, d(2.8)));
        stage.add(cap);
        pause(2.6);
        fadeOutAll(d(1.2), stage);
        unsay();
        pause(0.4);
    }
}
