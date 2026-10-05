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
 * Standalone clip for slides 13-14 of the lists deck: List print and List find.
 *
 * Both are walks along the list. For each, the slide's plain-language steps (in maroon) are shown
 * next to the C++ loop, and a band marks which step the highlighted line of code is performing:
 *
 *   print()   ptr starts at head, prints each value (the output builds up), moves on, and stops
 *             when ptr becomes NULL
 *   find(9)   ptr walks and compares each value with 9; found at the fifth node; then find(8)
 *             runs off the end of the list: "Element not present"
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListTraverseScene extends PDSListClipBase {

    private static final double ROW_Y = -300, PITCH = 180;

    private StrokeTextMob head;

    @Override
    public void construct() {
        print();
        find();
    }

    private void paintFade(LNode n, Color c, double dur) {
        List<Animation> a = new ArrayList<>();
        n.paint(a, c, dur);
        playAll(a);
    }

    /** The list on top: head, six nodes and a NULL marker after the last one. Returns all objects. */
    private Row topRow(List<MObject> everything) {
        Row row = new Row(new String[]{"4", "2", "7", "2", "9", "5"}, 0, ROW_Y, PITCH, NODE);
        LNode n0 = row.nodes.get(0);
        Link headArrow = arrow(n0.leftX() - 130, n0.y, n0.leftX() - 3, n0.y, Colors.GOLD, 3.6);
        TextMob headLab = label("head", n0.leftX() - 78, n0.y - 36, 32, Colors.GOLD, false, true);
        play(new DrawLink(headArrow, d(0.6)), new FadeIn(headLab, d(0.6)));
        row.build(d(0.3), d(0.6));
        everything.addAll(row.parts());
        everything.add(headArrow);
        everything.add(headLab);
        return row;
    }

    /** A band behind the plain-language step that the highlighted code line performs. */
    private RectMob stepBand(double y) {
        RectMob b = panel(-420, y, 920, 56, Colors.GOLD, 0.22);
        b.setStrokeColor(Colors.withAlpha(Colors.GOLD, 0.85));
        return b;
    }

    // ── slide 13 ─────────────────────────────────────────────────────

    private void print() {
        head = writeHeading("List print");
        pause(0.4);
        List<MObject> everything = new ArrayList<>();
        everything.add(head);
        Row row = topRow(everything);

        StrokeTextMob call = strokeLeft("print()", -880, -225, 54, Colors.WHITE);
        play(new Write(call, d(1.2)));
        everything.add(call);

        // the plain-language steps, then the loop
        String[] pseudo = {"For each element in the list", "    Print the element"};
        StrokeTextMob p0 = strokeLeft(pseudo[0], -880, -140, 40, Colors.MAROON);
        StrokeTextMob p1 = strokeLeft(pseudo[1], -880, -78, 40, Colors.MAROON);
        RectMob band = stepBand(-140);
        everything.add(band);
        play(new Write(p0, d(2.0)));
        play(new Write(p1, d(1.8)));
        everything.add(p0);
        everything.add(p1);
        CodeBox code = new CodeBox(new String[]{
                "for (Node *ptr = head; ptr; ptr = ptr->next)",
                "  printf(\"%c \", ptr->val);",
                "printf(\"\\n\");"}, -880, 60, 36, 62);
        code.typeIn(3.0);
        everything.addAll(code.parts());
        pause(0.5);
        play(new FadeIn(code.hl, d(0.4)), new FadeIn(band, d(0.4)));

        TextMob out = mono("Output:", 230, 125, 58, Colors.GOLD);
        out.setAlignment(javafx.scene.text.TextAlignment.LEFT);
        play(new FadeIn(out, d(0.5)));
        everything.add(out);

        // ptr walks; each visit prints, then ptr = ptr->next
        code.setLine(0);
        sayAt("ptr starts at head", Colors.LIGHT_GRAY, 560, -200, 34);
        Ptr ptr = above("ptr", row.nodes.get(0), Colors.ORANGE);
        play(new FadeIn(ptr.arrow, d(0.5)), new FadeIn(ptr.lab, d(0.5)));
        everything.addAll(ptr.parts());
        pause(0.7);
        StringBuilder sb = new StringBuilder("Output:");
        for (int i = 0; i < 6; i++) {
            LNode n = row.nodes.get(i);
            List<Animation> mv = new ArrayList<>();
            if (i > 0) {
                ptr.go(mv, n.x, d(0.5));
                mv.add(code.moveHl(0, d(0.3)));
                mv.add(new MoveTo(band, -420, -140, d(0.3)));
            }
            n.paint(mv, Colors.ORANGE, d(0.35));
            playAll(mv);
            sayAt("ptr is not NULL: visit this node", Colors.LIGHT_GRAY, 560, -200, 34);
            pause(0.3);
            List<Animation> pr = new ArrayList<>();
            pr.add(code.moveHl(1, d(0.35)));
            pr.add(new MoveTo(band, -420, -78, d(0.35)));
            playAll(pr);
            sb.append(' ').append(n.value);
            out.setText(sb.toString());
            play(new ScaleTo(out, 1.08, d(0.15)));
            play(new ScaleTo(out, 1.0, d(0.2)));
            pause(0.2);
            paintFade(n, NODE, 0.25);
        }

        // ptr = ptr->next once more: NULL, so the loop ends
        List<Animation> end = new ArrayList<>();
        LNode last = row.nodes.get(5);
        double nullX = last.rightX() + 90;
        TextMob nullLab = mono("NULL", nullX, ROW_Y, 30, Colors.GRAY);
        end.add(new FadeIn(nullLab, d(0.5)));
        end.add(code.moveHl(0, d(0.35)));
        end.add(new MoveTo(band, -420, -140, d(0.35)));
        ptr.go(end, nullX, d(0.7));
        playAll(end);
        everything.add(nullLab);
        sayAt("ptr is NULL: the loop ends", Colors.GREEN, 560, -200, 34);
        pause(1.0);
        play(code.moveHl(2, d(0.5)));
        play(new FadeOut(band, d(0.4)));
        sayAt("printf(\"\\n\") finishes the line", Colors.LIGHT_GRAY, 560, -200, 34);
        pause(1.8);
        StrokeTextMob cost = stroke("One visit per node: O(N).", 560, 330, 44, Colors.ORANGE, false);
        play(new Write(cost, d(1.8)));
        everything.add(cost);
        pause(2.2);
        fadeOutAll(d(1.2), everything);
        unsay();
        pause(0.4);
    }

    // ── slide 14 ─────────────────────────────────────────────────────

    private void find() {
        head = writeHeading("List find");
        pause(0.4);
        List<MObject> everything = new ArrayList<>();
        everything.add(head);
        Row row = topRow(everything);
        LNode lastNode = row.nodes.get(5);
        double nullX = lastNode.rightX() + 90;

        StrokeTextMob call = strokeLeft("find(9)", -880, -235, 54, Colors.WHITE);
        play(new Write(call, d(1.2)));
        everything.add(call);

        String[] pseudo = {"For each element in the list",
                "    If the element is same as that to be searched",
                "        Found the element",
                "Element not present"};
        List<StrokeTextMob> ps = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            StrokeTextMob p = strokeLeft(pseudo[i], -880, -160 + 54 * i, 36, Colors.MAROON);
            ps.add(p);
        }
        RectMob band = panel(-380, -160, 1000, 52, Colors.GOLD, 0.22);
        band.setStrokeColor(Colors.withAlpha(Colors.GOLD, 0.85));
        everything.add(band);
        for (StrokeTextMob p : ps) {
            play(new Write(p, d(Math.max(1.4, 1.2))));
            everything.add(p);
        }
        CodeBox code = new CodeBox(new String[]{
                "for (Node *ptr = head; ptr; ptr = ptr->next)",
                "  if (ptr->val == val) return true;",
                "return false;"}, -880, 120, 36, 62);
        code.typeIn(3.0);
        everything.addAll(code.parts());
        pause(0.5);
        play(new FadeIn(code.hl, d(0.4)), new FadeIn(band, d(0.4)));

        // find(9): compare node by node
        runFind(row, code, band, everything, 9, nullX);

        // find(8): not in the list: ptr reaches NULL
        play(new FadeOut(call, d(0.4)));
        StrokeTextMob call2 = strokeLeft("find(8)", -880, -235, 54, Colors.WHITE);
        play(new Write(call2, d(1.0)));
        everything.add(call2);
        runFind(row, code, band, everything, 8, nullX);

        StrokeTextMob cost = stroke("In the worst case every node is looked at: O(N).", 400, 400, 40, Colors.ORANGE, false);
        play(new Write(cost, d(3.0)));
        everything.add(cost);
        pause(2.6);
        fadeOutAll(d(1.2), everything);
        unsay();
        pause(0.4);
    }

    private void runFind(Row row, CodeBox code, RectMob band, List<MObject> everything, int target, double nullX) {
        code.setLine(0);
        play(new MoveTo(band, -380, -160, d(0.2)));
        play(code.moveHl(0, d(0.2)));
        sayAt("looking for " + target, Colors.LIGHT_GRAY, 560, -200, 34);
        Ptr ptr = above("ptr", row.nodes.get(0), Colors.ORANGE);
        play(new FadeIn(ptr.arrow, d(0.5)), new FadeIn(ptr.lab, d(0.5)));
        TextMob result = null;
        boolean found = false;
        for (int i = 0; i < 6 && !found; i++) {
            LNode n = row.nodes.get(i);
            List<Animation> mv = new ArrayList<>();
            if (i > 0) {
                ptr.go(mv, n.x, d(0.5));
                mv.add(code.moveHl(0, d(0.3)));
                mv.add(new MoveTo(band, -380, -160, d(0.3)));
            }
            n.paint(mv, Colors.ORANGE, d(0.35));
            playAll(mv);
            List<Animation> cmp = new ArrayList<>();
            cmp.add(code.moveHl(1, d(0.35)));
            cmp.add(new MoveTo(band, -380, -106, d(0.35)));
            playAll(cmp);
            boolean hit = Integer.parseInt(n.value) == target;
            sayAt(n.value + " == " + target + " ?   " + (hit ? "yes" : "no"), hit ? Colors.GREEN : Colors.LIGHT_GRAY, 560, -200, 38);
            pause(0.5);
            if (hit) {
                found = true;
                List<Animation> g = new ArrayList<>();
                n.paint(g, Colors.GREEN, d(0.4));
                g.add(new MoveTo(band, -380, -52, d(0.4)));
                playAll(g);
            } else {
                paintFade(n, NODE, 0.25);
            }
        }
        if (found) {
            result = mono("return true", 470, 190, 64, Colors.GREEN);
        } else {
            // ptr = ptr->next: NULL, the loop ends
            TextMob nl = mono("NULL", nullX, ROW_Y, 30, Colors.GRAY);
            List<Animation> end = new ArrayList<>();
            end.add(new FadeIn(nl, d(0.4)));
            ptr.go(end, nullX, d(0.7));
            end.add(code.moveHl(0, d(0.35)));
            end.add(new MoveTo(band, -380, -160, d(0.35)));
            playAll(end);
            sayAt("ptr is NULL: nothing matched", Colors.ORANGE, 560, -200, 38);
            pause(0.8);
            List<Animation> nf = new ArrayList<>();
            nf.add(code.moveHl(2, d(0.4)));
            nf.add(new MoveTo(band, -380, 2, d(0.4)));
            playAll(nf);
            result = mono("return false", 470, 190, 64, Colors.RED);
            everything.add(nl);
        }
        result.setOpacity(0);
        play(new FadeIn(result, d(0.5)));
        everything.add(result);
        pause(1.6);
        List<Animation> clear = new ArrayList<>();
        clear.add(new FadeOut(result, d(0.5)));
        clear.add(new FadeOut(ptr.arrow, d(0.5)));
        clear.add(new FadeOut(ptr.lab, d(0.5)));
        for (LNode n : row.nodes) n.paint(clear, NODE, d(0.4));
        playAll(clear);
        everything.addAll(ptr.parts());
    }
}
