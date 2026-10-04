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
 * Standalone clip for slides 1-3 of the arrays deck: the title card, the properties of an array,
 * and array expressions.
 *
 *   Slide 1  "Arrays" is written over a row of memory cells that appear side by side, then the
 *            author, address and date
 *   Slide 2  the properties come one line at a time in the pen-stroke style and swipe off; each
 *            group then gets a picture: an array aggregating and gaining dimensions, contiguous
 *            storage with O(1) address arithmetic, the type deciding how far a[i + 1] is from
 *            a[i], and fixed / allocated / growing storage (the vector reallocating and copying)
 *   Slide 3  the slide itself — the code and its error, the D-dimensional matrix that the
 *            hardware sees as one dimension, the address formula and Horner's rule — followed by
 *            a worked example that counts the multiplications each form needs
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSArrayIntroScene extends PDSSortClipBase {

    private StrokeTextMob head;

    @Override
    public void construct() {
        titleCard();
        properties();
        expressions();
    }

    // ── helpers ──────────────────────────────────────────────────────

    private RectMob cellBox(double x, double y, double w, double h, Color c, double fill) {
        RectMob r = new RectMob(w, h).setCornerRadius(8);
        r.setFillColor(Colors.withAlpha(c, fill));
        r.setStrokeColor(Colors.withAlpha(c, 0.85));
        r.setStrokeWidth(2.5);
        r.setPosition(x, y);
        r.setOpacity(0);
        add(r);
        return r;
    }

    private LaTeXMob latex(String src, double size, double x, double y) {
        LaTeXMob l = new LaTeXMob(src).setSize((float) size).setLatexColor(Colors.WHITE);
        l.setPosition(x, y);
        l.setOpacity(0);
        add(l);
        return l;
    }

    private TextMob mono(String text, double x, double y, double size, Color c) {
        TextMob t = label(text, x, y, size, c, false, true);
        t.setFontFamily("Menlo");
        return t;
    }

    private StrokeTextMob caption(String text, Color c, double secs) {
        StrokeTextMob t = stroke(text, 0, -345, 30, c, false);
        play(new Write(t, d(secs)));
        return t;
    }

    // ── slide 1 ──────────────────────────────────────────────────────

    private void titleCard() {
        List<MObject> mine = new ArrayList<>();
        StrokeTextMob title = stroke("Arrays", 0, -230, 140, Colors.WHITE, true);
        play(new Write(title, d(2.6)));
        mine.add(title);
        pause(0.3);

        // memory cells appear side by side, one after another
        int[] v = {12, 7, 31, 4, 19, 8, 25, 16, 3, 22, 9, 14};
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < v.length; i++) {
            double x = (i - 5.5) * 118;
            RectMob b = cellBox(x, -40, 108, 80, Colors.TEAL, 0.25);
            TextMob t = label(String.valueOf(v[i]), x, -40, 34, Colors.WHITE, false, true);
            TextMob ix = label(String.valueOf(i), x, 20, 20, Colors.GRAY, false, false);
            in.add(new FadeInAt(b, 0.14 * i, d(0.5)));
            in.add(new FadeInAt(t, 0.14 * i, d(0.5)));
            in.add(new FadeInAt(ix, 0.14 * i, d(0.5)));
            mine.add(b);
            mine.add(t);
            mine.add(ix);
        }
        playAll(in);
        pause(0.4);
        StrokeTextMob name = stroke("Rupesh Nasre.", 0, 150, 56, Colors.WHITE, false);
        play(new Write(name, d(2.0)));
        StrokeTextMob mail = stroke("rupesh@cse.iitm.ac.in", 0, 220, 34, Colors.LIGHT_GRAY, false);
        play(new Write(mail, d(2.2)));
        TextMob date = label("August 2021", 0, 420, 30, Colors.GRAY, false, false);
        play(new FadeIn(date, d(0.8)));
        mine.add(name);
        mine.add(mail);
        mine.add(date);
        pause(2.6);
        fadeOutAll(1.2, mine);
        pause(0.3);
    }

    // ── slide 2 ──────────────────────────────────────────────────────

    private void properties() {
        head = writeHeading("Properties");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Simplest data structure"));
        s.add(ln(1, "Acts as aggregate over primitives or other aggregates"));
        s.add(ln(1, "May have multiple dimensions"));
        s.add(ln(0, "Contiguous storage"));
        s.add(ln(0, "Random access in O(1)"));
        s.add(ln(0, "Languages such as C use type system to index"));
        s.add(ln(3, "appropriately"));
        s.add(ln(1, "e.g., a[i] and a[i + 1] refer to locations based on type"));
        s.add(ln(0, "Storage space:"));
        s.add(ln(1, "Fixed for arrays"));
        s.add(ln(1, "Dynamically allocatable but fixed on stack and heap"));
        s.add(ln(1, "Variable for vectors (internally, reallocation and copying)"));
        List<List<MObject>> text = writeSlide(s, -385);
        pause(1.2);
        swipeAway(text);
        pause(0.4);

        aggregateAndDimensions();
        contiguousRandomAccess();
        typeDecidesStride();
        storageSpace();
        fadeOutAll(d(0.8), head);
        pause(0.3);
    }

    private void aggregateAndDimensions() {
        List<MObject> mine = new ArrayList<>();
        mine.add(caption("The simplest data structure: an aggregate over primitives or other aggregates.", Colors.WHITE, 4.0));
        int[] v = {7, 3, 9, 1, 8, 2, 5, 4, 6, 0, 3, 5};
        RectMob[] b = new RectMob[12];
        TextMob[] t = new TextMob[12];
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            double x = (i - 5.5) * 100;
            b[i] = cellBox(x, -60, 90, 70, Colors.TEAL, 0.25);
            t[i] = label(String.valueOf(v[i]), x, -60, 30, Colors.WHITE, false, true);
            in.add(new FadeInAt(b[i], 0.07 * i, d(0.5)));
            in.add(new FadeInAt(t[i], 0.07 * i, d(0.5)));
            mine.add(b[i]);
            mine.add(t[i]);
        }
        TextMob code1 = mono("int a[12];", 0, -170, 32, Colors.TEAL);
        in.add(new FadeIn(code1, d(0.6)));
        mine.add(code1);
        playAll(in);
        pause(1.0);

        // the same twelve cells, folded into rows: a second dimension
        play(new FadeOut(mine.get(0), d(0.4)));
        StrokeTextMob c2 = stroke("It may have multiple dimensions.", 0, -345, 30, Colors.WHITE, false);
        play(new Write(c2, d(1.8)));
        mine.add(c2);
        code1.setText("int a[3][4];");
        List<Animation> fold = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            int r = i / 4, c = i % 4;
            double x = (c - 1.5) * 106, y = -150 + 86 * r;
            fold.add(new ArcMove(b[i], x, y, -60, d(1.2)));
            fold.add(new ArcMove(t[i], x, y, -60, d(1.2)));
        }
        fold.add(new MoveTo(code1, 0, -250, d(1.2)).setEasing(Easing.EASE_IN_OUT));
        playAll(fold);
        pause(2.2);
        fadeOutAll(d(0.8), mine);
        pause(0.2);
    }

    private void contiguousRandomAccess() {
        List<MObject> mine = new ArrayList<>();
        mine.add(caption("Contiguous storage: the elements sit side by side, 4 bytes apart for an int.", Colors.WHITE, 4.0));
        int[] v = {7, 3, 9, 1, 8, 2, 5, 4};
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            double x = (i - 3.5) * 140;
            RectMob b = cellBox(x, -40, 128, 86, Colors.TEAL, 0.25);
            TextMob t = label(String.valueOf(v[i]), x, -40, 34, Colors.WHITE, false, true);
            TextMob ix = label("a[" + i + "]", x, -110, 24, Colors.LIGHT_GRAY, false, true);
            TextMob ad = label(String.valueOf(1000 + 4 * i), x, 30, 24, Colors.GOLD, false, true);
            ad.setFontFamily("Menlo");
            in.add(new FadeInAt(b, 0.12 * i, d(0.5)));
            in.add(new FadeInAt(t, 0.12 * i, d(0.5)));
            in.add(new FadeInAt(ix, 0.12 * i, d(0.5)));
            in.add(new FadeInAt(ad, 0.12 * i + 0.3, d(0.5)));
            mine.add(b);
            mine.add(t);
            mine.add(ix);
            mine.add(ad);
        }
        TextMob adLab = label("address", -640, 30, 24, Colors.GOLD, false, true);
        in.add(new FadeIn(adLab, d(0.6)));
        mine.add(adLab);
        playAll(in);
        pause(1.0);

        // random access: jump straight to a[5] — one multiplication, one addition
        play(new FadeOut(mine.get(0), d(0.4)));
        StrokeTextMob c2 = stroke("Random access in O(1): no walking through the elements.", 0, -345, 30, Colors.WHITE, false);
        play(new Write(c2, d(2.6)));
        mine.add(c2);
        RectMob ring = new RectMob(138, 96).setCornerRadius(12);
        ring.setFillColor(Color.TRANSPARENT);
        ring.setStrokeColor(Colors.ORANGE);
        ring.setStrokeWidth(5);
        ring.setPosition(-3.5 * 140, -40);
        ring.setOpacity(0);
        add(ring);
        mine.add(ring);
        TextMob calc = mono("", 0, 150, 34, Colors.ORANGE);
        mine.add(calc);
        play(new FadeIn(ring, d(0.4)), new FadeIn(calc, d(0.4)));
        int[] queries = {5, 2, 7};
        for (int q : queries) {
            calc.setText("a[" + q + "]  =  1000 + " + q + " × 4  =  " + (1000 + 4 * q));
            play(new ArcMove(ring, (q - 3.5) * 140, -40, 70, d(0.9)));
            pause(1.0);
        }
        StrokeTextMob c3 = stroke("One multiplication and one addition, whichever index we ask for.", 0, 240, 28, Colors.ORANGE, false);
        play(new Write(c3, d(3.0)));
        mine.add(c3);
        pause(2.2);
        fadeOutAll(d(0.8), mine);
        pause(0.2);
    }

    private void typeDecidesStride() {
        List<MObject> mine = new ArrayList<>();
        mine.add(caption("C uses the type system to index: a[i] and a[i + 1] sit sizeof(type) bytes apart.", Colors.WHITE, 4.4));
        String[] names = {"char c[]", "int a[]", "double d[]"};
        int[] bytes = {1, 4, 8};
        double[] ys = {-190, -20, 150};
        String[] gap = {"+ 1 byte", "+ 4 bytes", "+ 8 bytes"};
        Color[] cols = {Colors.PINK, Colors.TEAL, Colors.GOLD};
        for (int r = 0; r < 3; r++) {
            double w = 48 * bytes[r];
            double x0 = -400;
            List<Animation> row = new ArrayList<>();
            TextMob nm = mono(names[r], -600, ys[r], 30, cols[r]);
            row.add(new FadeIn(nm, d(0.5)));
            mine.add(nm);
            String[] tag = {"[i]", "[i+1]", "…"};
            for (int k = 0; k < 3; k++) {
                double x = x0 + k * w + w / 2;
                RectMob b = cellBox(x, ys[r], w - 4, 84, cols[r], k < 2 ? 0.3 : 0.12);
                TextMob t = label(tag[k], x, ys[r] + 62, bytes[r] == 1 ? 17 : 24, Colors.WHITE, false, true);
                row.add(new FadeInAt(b, 0.15 * k, d(0.5)));
                row.add(new FadeInAt(t, 0.15 * k, d(0.5)));
                mine.add(b);
                mine.add(t);
            }
            TextMob g = label(gap[r], x0 + w, ys[r] - 68, 26, cols[r], false, true);
            row.add(new FadeInAt(g, 0.6, d(0.6)));
            mine.add(g);
            playAll(row);
            pause(0.6);
        }
        StrokeTextMob c = stroke("a[i + 1] is not \"the next byte\": the compiler scales the index by the element size.", 0, 310, 28, Colors.ORANGE, false);
        play(new Write(c, d(3.4)));
        mine.add(c);
        pause(2.4);
        fadeOutAll(d(0.8), mine);
        pause(0.2);
    }

    private void storageSpace() {
        List<MObject> mine = new ArrayList<>();
        mine.add(caption("Storage space: fixed for arrays, fixed once allocated, variable only for vectors.", Colors.WHITE, 4.0));
        double[] px = {-560, 0, 560};
        String[] title = {"fixed for arrays", "allocated, then fixed", "variable for vectors"};
        String[] code = {"int a[5];   (stack)", "malloc(5 * 4)   (heap)", "vector<int> v;"};
        Color[] cols = {Colors.TEAL, Colors.BLUE, Colors.GOLD};
        for (int p = 0; p < 3; p++) {
            TextMob tt = label(title[p], px[p], -250, 30, cols[p], false, true);
            TextMob cc = mono(code[p], px[p], -200, 24, Colors.LIGHT_GRAY);
            RectMob frame = new RectMob(480, 400).setCornerRadius(16);
            frame.setFillColor(GHOST_FILL);
            frame.setStrokeColor(Colors.withAlpha(cols[p], 0.6));
            frame.setStrokeWidth(2.5);
            frame.setPosition(px[p], 60);
            frame.setOpacity(0);
            add(frame);
            play(new FadeIn(tt, d(0.5)), new FadeIn(cc, d(0.5)), new FadeIn(frame, d(0.5)));
            mine.add(tt);
            mine.add(cc);
            mine.add(frame);
        }
        // panels 1 and 2: five cells, and the sixth has nowhere to go
        String[] never = {"the size is fixed at compile time", "the block never grows"};
        for (int p = 0; p < 2; p++) {
            List<Animation> in = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                double x = px[p] + (i - 2.5) * 70;
                RectMob b = cellBox(x, -50, 64, 64, p == 0 ? Colors.TEAL : Colors.BLUE, 0.3);
                TextMob t = label(String.valueOf(new int[]{4, 8, 1, 6, 3}[i]), x, -50, 26, Colors.WHITE, false, true);
                in.add(new FadeInAt(b, 0.1 * i, d(0.5)));
                in.add(new FadeInAt(t, 0.1 * i, d(0.5)));
                mine.add(b);
                mine.add(t);
            }
            playAll(in);
        }
        pause(0.6);
        for (int p = 0; p < 2; p++) {
            double x = px[p] + 2.5 * 70;
            RectMob ghost = cellBox(x, -50, 64, 64, Colors.RED, 0.08);
            ghost.setStrokeColor(Colors.withAlpha(Colors.RED, 0.7));
            LineMob x1 = new LineMob(x - 16, -66, x + 16, -34, Colors.RED, 5);
            LineMob x2 = new LineMob(x - 16, -34, x + 16, -66, Colors.RED, 5);
            add(x1);
            add(x2);
            TextMob no = label("no sixth cell", px[p], 40, 26, Colors.RED, false, true);
            TextMob nv = label(never[p], px[p], 130, 22, Colors.LIGHT_GRAY, false, false);
            play(new FadeIn(ghost, d(0.4)), new DrawLine(x1, d(0.4)), new DrawLine(x2, d(0.4)),
                    new FadeIn(no, d(0.5)), new FadeIn(nv, d(0.5)));
            mine.add(ghost);
            mine.add(x1);
            mine.add(x2);
            mine.add(no);
            mine.add(nv);
        }
        pause(0.8);

        // panel 3: the vector is full, so it allocates a bigger block, copies, and frees the old one
        double vx = px[2];
        List<Animation> in = new ArrayList<>();
        RectMob[] oc = new RectMob[4];
        TextMob[] ot = new TextMob[4];
        for (int i = 0; i < 4; i++) {
            double x = vx + (i - 1.5) * 82;
            oc[i] = cellBox(x, -100, 74, 62, Colors.GOLD, 0.3);
            ot[i] = label(String.valueOf(new int[]{4, 8, 1, 6}[i]), x, -100, 28, Colors.WHITE, false, true);
            in.add(new FadeInAt(oc[i], 0.1 * i, d(0.5)));
            in.add(new FadeInAt(ot[i], 0.1 * i, d(0.5)));
            mine.add(oc[i]);
            mine.add(ot[i]);
        }
        playAll(in);
        TextMob push = mono("v.push_back(9);   // full!", vx, -40, 22, Colors.ORANGE);
        play(new FadeIn(push, d(0.5)));
        mine.add(push);
        pause(0.8);
        RectMob[] nc = new RectMob[8];
        List<Animation> alloc = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            double x = vx + (i - 3.5) * 54;
            nc[i] = cellBox(x, 40, 50, 56, Colors.GREEN, 0.15);
            alloc.add(new FadeInAt(nc[i], 0.06 * i, d(0.4)));
            mine.add(nc[i]);
        }
        TextMob newLab = label("new block: room for 8", vx, 100, 22, Colors.GREEN, false, true);
        alloc.add(new FadeIn(newLab, d(0.6)));
        mine.add(newLab);
        playAll(alloc);
        List<Animation> copy = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            double x = vx + (i - 3.5) * 54;
            RectMob cp = cellBox(oc[i].getPosition().x(), oc[i].getPosition().y(), 50, 56, Colors.GOLD, 0.35);
            cp.setOpacity(1);
            TextMob ct = label(String.valueOf(new int[]{4, 8, 1, 6}[i]), oc[i].getPosition().x(), oc[i].getPosition().y(), 24, Colors.WHITE, false, true);
            ct.setOpacity(1);
            copy.add(new ArcMove(cp, x, 40, 40, d(1.0)));
            copy.add(new ArcMove(ct, x, 40, 40, d(1.0)));
            mine.add(cp);
            mine.add(ct);
        }
        playAll(copy);
        TextMob cpLab = label("copy every element", vx, 150, 22, Colors.GOLD, false, true);
        play(new FadeIn(cpLab, d(0.5)));
        mine.add(cpLab);
        pause(0.4);
        List<Animation> freeOld = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            freeOld.add(new FadeOut(oc[i], d(0.6)));
            freeOld.add(new FadeOut(ot[i], d(0.6)));
        }
        freeOld.add(new FadeOut(push, d(0.6)));
        playAll(freeOld);
        RectMob nine = cellBox(vx + (4 - 3.5) * 54, 40, 50, 56, Colors.PINK, 0.4);
        TextMob nineT = label("9", vx + (4 - 3.5) * 54, 40, 24, Colors.WHITE, false, true);
        TextMob freed = label("old block freed, 9 added", vx, -70, 22, Colors.ORANGE, false, true);
        play(new FadeIn(nine, d(0.5)), new FadeIn(nineT, d(0.5)), new FadeIn(freed, d(0.5)));
        mine.add(nine);
        mine.add(nineT);
        mine.add(freed);
        StrokeTextMob c = stroke("Reallocation and copying: the vector's size is variable, at a cost.", 0, 330, 28, Colors.ORANGE, false);
        play(new Write(c, d(3.2)));
        mine.add(c);
        pause(2.6);
        fadeOutAll(d(0.9), mine);
        pause(0.2);
    }

    // ── slide 3 ──────────────────────────────────────────────────────

    private void expressions() {
        head = writeHeading("Array Expressions");
        pause(0.5);
        List<MObject> stage = new ArrayList<>();

        CodeBox box = new CodeBox(new String[]{
            "void fun(int a[ ][ ]) {",
            "   a[0][0] = 20;",
            "}",
            "void main() {",
            "   int a[5][10];",
            "   fun(a);",
            "   printf(\"%d\\n\", a[0][0]);",
            "}"}, -880, -350, 26, 40);
        box.typeIn(3.6);
        StrokeTextMob err = strokeLeft("ERROR: type of formal parameter 1 is incomplete", -880, 0, 26, Colors.RED);
        play(new Write(err, d(2.8)));
        stage.add(err);
        pause(0.6);

        // what the compiler needs: the row width
        StrokeTextMob why1 = strokeLeft("a[i][j] lives at  base + (i × width + j) × 4 —", -880, 55, 26, Colors.LIGHT_GRAY);
        play(new Write(why1, d(2.8)));
        StrokeTextMob why2 = strokeLeft("but  int a[ ][ ]  hides the width. The fix:  int a[ ][10].", -880, 100, 26, Colors.GREEN);
        play(new Write(why2, d(3.0)));
        stage.add(why1);
        stage.add(why2);

        // the paragraph and the picture: a 2-D view of what the hardware sees in one dimension
        String[] para = {"We view an array to be a D-", "dimensional matrix. However, for", "the hardware, it is simply single", "dimensional."};
        List<StrokeTextMob> pl = new ArrayList<>();
        for (int k = 0; k < para.length; k++) {
            StrokeTextMob p = strokeLeft(para[k], -330, -350 + 46 * k, 28, Colors.WHITE);
            play(new Write(p, d(2.0)));
            pl.add(p);
            stage.add(p);
        }
        Color[] rowCol = {Colors.LIGHT_GRAY, Colors.BLUE, Colors.GREEN, Colors.MAROON};
        RectMob[] mc = new RectMob[12];
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            int r = i / 3, c = i % 3;
            double x = 470 + (c - 1) * 72, y = -310 + 60 * r;
            mc[i] = cellBox(x, y, 66, 54, rowCol[r], 0.45);
            in.add(new FadeInAt(mc[i], 0.05 * i, d(0.5)));
            stage.add(mc[i]);
        }
        playAll(in);
        pause(0.5);
        List<Animation> flat = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            double x = 800, y = -340 + 58 * i;
            flat.add(new ArcMove(mc[i], x, y, -40, d(1.4)));
            flat.add(new ColorChange(mc[i], Colors.withAlpha(rowCol[i / 3], 0.6), d(1.4)));
        }
        playAll(flat);
        pause(1.0);

        // the declaration, the address, Horner's rule
        StrokeTextMob decl = strokeLeft("For declaration  int a[w4][w3][w2][w1]:", -880, 175, 34, Colors.WHITE);
        play(new Write(decl, d(2.6)));
        StrokeTextMob q1 = strokeLeft("What is the address of  a[i][j][k][l] ?", -840, 235, 32, Colors.WHITE);
        play(new Write(q1, d(2.4)));
        LaTeXMob f1 = latex("\\mathbf{(i \\cdot w_3 \\cdot w_2 \\cdot w_1 + j \\cdot w_2 \\cdot w_1 + k \\cdot w_1 + l) \\times 4}", 32, -300, 292);
        play(new Write(f1, d(2.8)));
        StrokeTextMob q2 = strokeLeft("How to optimize the computation?", -840, 360, 32, Colors.WHITE);
        play(new Write(q2, d(2.2)));
        StrokeTextMob q3 = strokeLeft("Use Horner's rule:", -800, 420, 30, Colors.BLUE);
        play(new Write(q3, d(1.8)));
        LaTeXMob f2 = latex("\\mathbf{(((i \\cdot w_3 + j) \\cdot w_2 + k) \\cdot w_1 + l) \\times 4}", 32, 20, 420);
        play(new Write(f2, d(2.6)));
        stage.add(decl);
        stage.add(q1);
        stage.add(f1);
        stage.add(q2);
        stage.add(q3);
        stage.add(f2);
        pause(2.6);

        // the slide is complete: it leaves, and a worked example counts the multiplications
        List<MObject> all = new ArrayList<>(stage);
        all.addAll(box.parts());
        fadeOutAll(d(0.9), all);
        pause(0.3);
        workedExample();
        fadeOutAll(1.5, head);
        pause(0.5);
    }

    private void workedExample() {
        List<MObject> mine = new ArrayList<>();
        TextMob decl = mono("int a[3][2][2][3];   a[1][1][0][2] = ?", 0, -360, 34, Colors.GOLD);
        play(new FadeIn(decl, d(0.6)));
        mine.add(decl);
        TextMob h1 = label("plain formula", -440, -285, 32, Colors.TEAL, false, true);
        TextMob h2 = label("Horner's rule", 440, -285, 32, Colors.BLUE, false, true);
        play(new FadeIn(h1, d(0.5)), new FadeIn(h2, d(0.5)));
        mine.add(h1);
        mine.add(h2);
        String[] plain = {
            "(i·w3·w2·w1 + j·w2·w1 + k·w1 + l) × 4",
            "(1·2·2·3 + 1·2·3 + 0·3 + 2) × 4",
            "(12 + 6 + 0 + 2) × 4",
            "20 × 4 = 80"};
        String[] horner = {
            "(((i·w3 + j)·w2 + k)·w1 + l) × 4",
            "(((1·2 + 1)·2 + 0)·3 + 2) × 4",
            "((3·2 + 0)·3 + 2) × 4",
            "(6·3 + 2) × 4 = 80"};
        TextMob[] pl = new TextMob[4], hl = new TextMob[4];
        for (int k = 0; k < 4; k++) {
            pl[k] = mono(plain[k], -440, -200 + 75 * k, 28, Colors.WHITE);
            hl[k] = mono(horner[k], 440, -200 + 75 * k, 28, Colors.WHITE);
            mine.add(pl[k]);
            mine.add(hl[k]);
        }
        for (int k = 0; k < 4; k++) {
            play(new FadeIn(pl[k], d(0.6)), new FadeIn(hl[k], d(0.6)));
            if (k > 0) {
                play(new ColorChange(pl[k - 1], Colors.GRAY, d(0.4)), new ColorChange(hl[k - 1], Colors.GRAY, d(0.4)));
            }
            pause(0.9);
        }
        TextMob m1 = label("6 multiplications", -440, 110, 34, Colors.RED, false, true);
        TextMob m2 = label("3 multiplications", 440, 110, 34, Colors.GREEN, false, true);
        m1.setScale(0.6);
        m2.setScale(0.6);
        play(new FadeIn(m1, d(0.6)), new ScaleTo(m1, 1.0, d(0.7)).setEasing(Easing.EASE_OUT));
        play(new FadeIn(m2, d(0.6)), new ScaleTo(m2, 1.0, d(0.7)).setEasing(Easing.EASE_OUT));
        mine.add(m1);
        mine.add(m2);
        pause(1.0);

        // where the element is: slot 20 of the 36 cells in memory, byte 80
        List<Animation> in = new ArrayList<>();
        RectMob[] cell = new RectMob[36];
        for (int i = 0; i < 36; i++) {
            double x = (i - 17.5) * 50;
            cell[i] = cellBox(x, 270, 46, 52, Colors.TEAL, i == 20 ? 0.6 : 0.2);
            in.add(new FadeInAt(cell[i], 0.02 * i, d(0.4)));
            mine.add(cell[i]);
        }
        TextMob mem = label("the 36 ints in memory, one after another", 0, 205, 26, Colors.LIGHT_GRAY, false, true);
        in.add(new FadeIn(mem, d(0.6)));
        mine.add(mem);
        playAll(in);
        RectMob ring = new RectMob(54, 62).setCornerRadius(8);
        ring.setFillColor(Color.TRANSPARENT);
        ring.setStrokeColor(Colors.GOLD);
        ring.setStrokeWidth(5);
        ring.setPosition((20 - 17.5) * 50, 270);
        ring.setOpacity(0);
        add(ring);
        TextMob at = label("slot 20  →  byte 80", (20 - 17.5) * 50, 345, 30, Colors.GOLD, false, true);
        play(new FadeIn(ring, d(0.5)), new FadeIn(at, d(0.5)));
        mine.add(ring);
        mine.add(at);
        StrokeTextMob c = stroke("Same address either way — Horner's rule just needs half the multiplications.", 0, 430, 28, Colors.ORANGE, false);
        play(new Write(c, d(3.4)));
        mine.add(c);
        pause(3.0);
        fadeOutAll(d(0.9), mine);
        pause(0.2);
    }
}
