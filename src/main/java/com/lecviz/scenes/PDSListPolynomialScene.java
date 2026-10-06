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
 * Standalone clip for slides 19-20 of the lists deck: the Polynomial ADT.
 *
 *   Slide 19  the formula, the example and the member functions on the left, the implementation
 *             options and questions on the right, written line by line; then the pictures: the
 *             polynomial as an array of coefficients (term by term into its slot), why an array hurts
 *             for 2x^1000 - x (1001 slots, two used), a linked list of only the existing terms, and the
 *             design decisions for lists shown by adding two sparse polynomials (merge by power)
 *   Slide 20  the class and the two classwork functions; initialize and add are written out and add
 *             runs on two coefficient arrays, slot by slot
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListPolynomialScene extends PDSListClipBase {

    private StrokeTextMob head;

    @Override
    public void construct() {
        slide19();
        slide20();
    }

    // ── helpers ──────────────────────────────────────────────────────



    private StrokeTextMob line(String text, double left, double y, double size, Color c, boolean bold, List<List<MObject>> groups) {
        StrokeTextMob t = bold ? stroke(text, left + strokeW(text, true, size) / 2, y, size, c, true) : strokeLeft(text, left, y, size, c);
        play(new Write(t, d(Math.max(1.0, text.length() * 0.05))));
        List<MObject> g = new ArrayList<>();
        g.add(t);
        groups.add(g);
        return t;
    }

    private void dash(double x, double y, List<List<MObject>> groups) {
        RectMob dsh = new RectMob(22, 4).setCornerRadius(2);
        dsh.setFillColor(Colors.GRAY);
        dsh.setStrokeColor(Color.TRANSPARENT);
        dsh.setPosition(x, y);
        dsh.setOpacity(0);
        add(dsh);
        play(new FadeIn(dsh, d(0.3)));
        List<MObject> g = new ArrayList<>();
        g.add(dsh);
        groups.add(g);
    }

    private void dot(double x, double y, Color c, List<List<MObject>> groups) {
        CircleMob dt = new CircleMob(8);
        dt.setFillColor(c);
        dt.setStrokeColor(Color.TRANSPARENT);
        dt.setPosition(x, y);
        dt.setOpacity(0);
        add(dt);
        play(new FadeIn(dt, d(0.3)));
        List<MObject> g = new ArrayList<>();
        g.add(dt);
        groups.add(g);
    }

    private void pop(MObject m) {
        play(new ScaleTo(m, 1.18, d(0.2)));
        play(new ScaleTo(m, 1.0, d(0.25)));
    }

    // ── slide 19 ─────────────────────────────────────────────────────

    private void slide19() {
        head = writeHeading("Polynomial ADT");
        pause(0.4);
        List<List<MObject>> groups = new ArrayList<>();

        // left column
        dot(-905, -330, Colors.GOLD, groups);
        LaTeXMob f = latex("\\mathbf{F(X) = \\sum_{i=0}^{N} A_i X^i}", 56, -520, -330);
        play(new Write(f, d(2.6)));
        groups.add(new ArrayList<>(List.of(f)));
        dot(-905, -220, Colors.BLUE, groups);
        line("Example:", -880, -220, 40, Colors.WHITE, false, groups);
        LaTeXMob ex = latex("\\mathbf{x^4 - 4x^3 + 7x - 6}", 48, -430, -220);
        play(new Write(ex, d(2.2)));
        groups.add(new ArrayList<>(List.of(ex)));
        dot(-905, -130, Colors.TEAL, groups);
        line("Member functions", -880, -130, 42, Colors.MAROON, true, groups);
        String[] mf = {"Initialize", "Set a coefficient (for a power)", "Add polynomials", "Multiply polynomials", "..."};
        for (int i = 0; i < mf.length; i++) {
            dash(-835, -65 + 55 * i, groups);
            line(mf[i], -800, -65 + 55 * i, 34, Colors.LIGHT_GRAY, false, groups);
        }

        // right column
        dot(25, -330, Colors.ORANGE, groups);
        line("Implementation", 60, -330, 42, Colors.MAROON, true, groups);
        dash(75, -265, groups);
        line("Could be using arrays", 110, -265, 34, Colors.LIGHT_GRAY, false, groups);
        dash(75, -210, groups);
        line("Could be using linked lists", 110, -210, 34, Colors.LIGHT_GRAY, false, groups);
        dot(25, -135, Colors.GOLD, groups);
        line("Classwork: Create a struct / class", 60, -135, 36, Colors.RED, false, groups);
        line("to implement polynomials.", 60, -85, 36, Colors.WHITE, false, groups);
        dot(25, -10, Colors.BLUE, groups);
        line("Are there disadvantages of", 60, -10, 36, Colors.WHITE, false, groups);
        line("using arrays?", 60, 40, 36, Colors.WHITE, false, groups);
        dash(75, 100, groups);
        LaTeXMob big = latex("\\mathbf{2x^{1000} - x}", 44, 245, 100);
        play(new Write(big, d(1.8)));
        groups.add(new ArrayList<>(List.of(big)));
        dash(75, 165, groups);
        line("What are the design decisions", 110, 165, 34, Colors.LIGHT_GRAY, false, groups);
        line("for using lists?", 110, 215, 34, Colors.LIGHT_GRAY, false, groups);
        pause(1.6);
        swipeAway(groups);
        pause(0.4);

        arrayOfCoefficients();
        bigPower();
        listOfTerms();
        mergeTerms();
        fadeOutAll(d(0.9), head);
        pause(0.3);
    }

    // a polynomial as an array of coefficients
    private void arrayOfCoefficients() {
        List<MObject> mine = new ArrayList<>();
        String[] terms = {"x^4", "-4x^3", "+7x", "-6"};
        int[] slot = {4, 3, 1, 0};
        String[] coef = {"1", "-4", "7", "-6"};
        LaTeXMob[] t = new LaTeXMob[4];
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            t[i] = latex("\\mathbf{" + terms[i] + "}", 60, -420 + 280 * i, -300);
            in.add(new FadeInAt(t[i], 0.25 * i, d(0.6)));
            mine.add(t[i]);
        }
        playAll(in);
        double y = 10;
        Cell[] cell = new Cell[5];
        List<Animation> cin = new ArrayList<>();
        for (int k = 0; k < 5; k++) {
            cell[k] = new Cell("", (k - 2) * 190, y, 160, 110, Colors.TEAL, 52);
            cell[k].box.setFillColor(Colors.withAlpha(Colors.WHITE, 0.05));
            cell[k].box.setStrokeColor(Colors.withAlpha(Colors.WHITE, 0.3));
            cell[k].fadeIn(cin, 0.08 * k, d(0.5));
            TextMob idx = mono("coeff[" + k + "]", (k - 2) * 190, y + 85, 28, Colors.GRAY);
            LaTeXMob pw = latex("x^{" + k + "}", 36, (k - 2) * 190, y - 85);
            cin.add(new FadeInAt(idx, 0.08 * k, d(0.5)));
            cin.add(new FadeInAt(pw, 0.08 * k, d(0.5)));
            mine.addAll(cell[k].parts());
            mine.add(idx);
            mine.add(pw);
        }
        playAll(cin);
        pause(0.6);

        for (int i = 0; i < 4; i++) {
            pop(t[i]);
            int k = slot[i];
            cell[k].text.setText(coef[i]);
            List<Animation> g = new ArrayList<>();
            g.add(new FadeIn(cell[k].text, d(0.5)));
            cell[k].color(g, Colors.GREEN, d(0.5));
            playAll(g);
            pause(0.5);
        }
        // the missing x^2 term
        cell[2].text.setText("0");
        List<Animation> z = new ArrayList<>();
        z.add(new FadeIn(cell[2].text, d(0.5)));
        cell[2].color(z, Colors.ORANGE, d(0.5));
        playAll(z);
        TextMob none = label("no x² term: the coefficient is 0", 0, 170, 32, Colors.ORANGE, false, true);
        play(new FadeIn(none, d(0.6)));
        mine.add(none);
        pause(1.2);
        StrokeTextMob cap = stroke("coeff[i] holds the coefficient of x to the power i.", 0, 290, 42, Colors.WHITE, false);
        play(new Write(cap, d(2.8)));
        mine.add(cap);
        pause(2.4);
        fadeOutAll(d(0.9), mine);
        pause(0.3);
    }

    // the problem with arrays: 2x^1000 - x
    private void bigPower() {
        List<MObject> mine = new ArrayList<>();
        LaTeXMob p = latex("\\mathbf{2x^{1000} - x}", 64, 0, -300);
        play(new FadeIn(p, d(0.6)));
        mine.add(p);
        List<Animation> in = new ArrayList<>();
        String[] vals = {"0", "-1", "0", "0", "", "2"};
        double[] xs = {-720, -540, -360, -180, 0, 560};
        for (int i = 0; i < 6; i++) {
            if (i == 4) {
                TextMob dots = label("· · ·", 190, -60, 70, Colors.GRAY, false, true);
                in.add(new FadeInAt(dots, 0.5, d(0.5)));
                mine.add(dots);
                continue;
            }
            Cell c = new Cell(vals[i], xs[i], -60, 150, 100, i == 1 || i == 5 ? Colors.GREEN : Colors.TEAL, 46);
            c.fadeIn(in, 0.12 * i, d(0.5));
            mine.addAll(c.parts());
            TextMob idx = mono("coeff[" + (i == 5 ? "1000" : String.valueOf(i)) + "]", xs[i], 30, 26, Colors.GRAY);
            in.add(new FadeInAt(idx, 0.12 * i, d(0.5)));
            mine.add(idx);
        }
        playAll(in);
        pause(0.8);
        List<MObject> chip1 = chip("1001 slots for just 2 terms!", 0, 150, 900, 90, Colors.RED, 46);
        List<Animation> c1 = new ArrayList<>();
        fade(c1, chip1, d(0.7));
        playAll(c1);
        mine.addAll(chip1);
        StrokeTextMob cap = stroke("Almost every slot holds a 0, and the array must still be that big.", 0, 290, 38, Colors.LIGHT_GRAY, false);
        play(new Write(cap, d(3.2)));
        mine.add(cap);
        pause(2.6);
        fadeOutAll(d(0.9), mine);
        pause(0.3);
    }

    // a list of only the terms that exist
    private void listOfTerms() {
        List<MObject> mine = new ArrayList<>();
        LaTeXMob p = latex("\\mathbf{2x^{1000} - x}", 64, 0, -300);
        play(new FadeIn(p, d(0.6)));
        mine.add(p);
        Row row = new Row(new String[]{"-1", "2"}, 90, -110, 300, NODE);
        LNode n0 = row.nodes.get(0);
        Link ha = arrow(n0.leftX() - 130, n0.y, n0.leftX() - 3, n0.y, Colors.GOLD, 3.6);
        TextMob hl = label("head", n0.leftX() - 78, n0.y - 36, 30, Colors.GOLD, false, true);
        play(new DrawLink(ha, d(0.6)), new FadeIn(hl, d(0.6)));
        row.build(d(0.5), d(0.7));
        mine.addAll(row.parts());
        mine.add(ha);
        mine.add(hl);
        TextMob p1 = mono("power 1", n0.x, n0.y + 75, 28, Colors.GOLD);
        TextMob p2 = mono("power 1000", row.nodes.get(1).x, n0.y + 75, 28, Colors.GOLD);
        play(new FadeIn(p1, d(0.6)), new FadeInAt(p2, 0.3, d(0.6)));
        mine.add(p1);
        mine.add(p2);
        StrokeTextMob cap = stroke("A list stores only the terms that exist: two nodes.", 0, 100, 42, Colors.GREEN, false);
        play(new Write(cap, d(2.8)));
        mine.add(cap);
        pause(1.4);

        // the design decisions
        StrokeTextMob q = stroke("Design decisions for the list:", 0, 190, 38, Colors.WHITE, true);
        play(new Write(q, d(1.8)));
        mine.add(q);
        String[] dd = {"keep the terms sorted by power", "add the coefficients of equal powers", "drop a term whose coefficient is 0"};
        Color[] cc = {Colors.TEAL, Colors.GOLD, Colors.PINK};
        for (int i = 0; i < 3; i++) {
            List<MObject> ch = chip(dd[i], 0, 260 + 70 * i, 820, 56, cc[i], 30);
            List<Animation> a = new ArrayList<>();
            for (MObject m : ch) a.add(new DropIn(m, 50, 0, d(0.7)));
            playAll(a);
            mine.addAll(ch);
            pause(0.5);
        }
        pause(1.6);
        fadeOutAll(d(0.9), mine);
        pause(0.3);
    }

    // adding two sparse polynomials by walking both lists in power order
    private void mergeTerms() {
        List<MObject> mine = new ArrayList<>();
        StrokeTextMob t1 = stroke("Adding: walk both lists in order of power.", 0, -380, 40, Colors.WHITE, false);
        play(new Write(t1, d(2.4)));
        mine.add(t1);
        LaTeXMob la = latex("\\mathbf{(2x^{1000} - x)}", 44, -700, -240);
        LaTeXMob lb = latex("\\mathbf{(x^{1000} + 3x^2)}", 44, -700, -50);
        play(new FadeIn(la, d(0.6)), new FadeInAt(lb, 0.3, d(0.6)));
        mine.add(la);
        mine.add(lb);
        Row ra = new Row(new String[]{"-1", "2"}, -90, -240, 280, NODE);
        Row rb = new Row(new String[]{"3", "1"}, -90, -50, 280, NODE);
        Row rr = new Row(new String[]{"-1", "3", "3"}, 40, 170, 280, NODE);
        String[] pa = {"power 1", "power 1000"};
        String[] pb = {"power 2", "power 1000"};
        String[] pr = {"power 1", "power 2", "power 1000"};
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            ra.nodes.get(i).fadeIn(in, 0.2 * i, d(0.6));
            rb.nodes.get(i).fadeIn(in, 0.2 * i + 0.3, d(0.6));
            TextMob ta = mono(pa[i], ra.nodes.get(i).x, -240 + 70, 24, Colors.GOLD);
            TextMob tb = mono(pb[i], rb.nodes.get(i).x, -50 + 70, 24, Colors.GOLD);
            in.add(new FadeInAt(ta, 0.2 * i, d(0.6)));
            in.add(new FadeInAt(tb, 0.2 * i + 0.3, d(0.6)));
            mine.add(ta);
            mine.add(tb);
        }
        in.add(new DrawLinkAt(ra.links.get(0), 0.4, d(0.6)));
        in.add(new DrawLinkAt(rb.links.get(0), 0.7, d(0.6)));
        playAll(in);
        mine.addAll(ra.parts());
        mine.addAll(rb.parts());
        TextMob[] trs = new TextMob[3];
        for (int i = 0; i < 3; i++) {
            trs[i] = mono(pr[i], rr.nodes.get(i).x, 170 + 70, 24, Colors.GOLD);
            mine.add(trs[i]);
        }
        for (int i = 0; i < 3; i++) {
            List<Animation> pa0 = new ArrayList<>();
            rr.nodes.get(i).paint(pa0, NEW_NODE, 0.01);
            playAll(pa0);
        }
        mine.addAll(rr.parts());
        TextMob sumLab = label("sum", -700, 170, 40, Colors.GREEN, false, true);
        play(new FadeIn(sumLab, d(0.6)));
        mine.add(sumLab);

        // step 1: power 1 < power 2 -> take A's term
        compare(ra.nodes.get(0), rb.nodes.get(0), "power 1 < power 2: take  -x", mine);
        List<Animation> s1 = new ArrayList<>();
        rr.nodes.get(0).fadeIn(s1, 0, d(0.7));
        s1.add(new FadeIn(trs[0], d(0.7)));
        playAll(s1);
        pause(0.6);
        uncompare(ra.nodes.get(0), rb.nodes.get(0));
        // step 2: power 1000 > power 2 -> take B's term
        compare(ra.nodes.get(1), rb.nodes.get(0), "power 2 < power 1000: take  3x²", mine);
        List<Animation> s2 = new ArrayList<>();
        rr.nodes.get(1).fadeIn(s2, 0, d(0.7));
        s2.add(new FadeIn(trs[1], d(0.7)));
        s2.add(new DrawLinkAt(rr.links.get(0), 0.2, d(0.6)));
        playAll(s2);
        pause(0.6);
        uncompare(ra.nodes.get(1), rb.nodes.get(0));
        // step 3: equal powers -> add the coefficients
        compare(ra.nodes.get(1), rb.nodes.get(1), "equal powers: 2 + 1 = 3", mine);
        List<Animation> s3 = new ArrayList<>();
        rr.nodes.get(2).fadeIn(s3, 0, d(0.7));
        s3.add(new FadeIn(trs[2], d(0.7)));
        s3.add(new DrawLinkAt(rr.links.get(1), 0.2, d(0.6)));
        playAll(s3);
        pause(0.8);
        uncompare(ra.nodes.get(1), rb.nodes.get(1));
        LaTeXMob res = latex("\\mathbf{3x^{1000} + 3x^2 - x}", 48, 40, 340);
        play(new Write(res, d(2.2)));
        mine.add(res);
        pause(2.6);
        fadeOutAll(d(1.0), mine);
        unsay();
        pause(0.3);
    }

    private void compare(LNode a, LNode b, String msg, List<MObject> mine) {
        List<Animation> an = new ArrayList<>();
        a.paint(an, Colors.ORANGE, d(0.3));
        b.paint(an, Colors.ORANGE, d(0.3));
        playAll(an);
        sayAt(msg, Colors.ORANGE, 380, -380 + 70, 36);
        pause(0.9);
    }

    private void uncompare(LNode a, LNode b) {
        List<Animation> an = new ArrayList<>();
        a.paint(an, NODE, d(0.3));
        b.paint(an, NODE, d(0.3));
        playAll(an);
    }

    // ── slide 20 ─────────────────────────────────────────────────────

    private void slide20() {
        head = writeHeading("Polynomial ADT");
        pause(0.4);
        List<MObject> everything = new ArrayList<>();
        everything.add(head);
        CodeBox code = new CodeBox(new String[]{
                "class Polynomial {",
                "  int coeff[MaxDegree + 1];",
                "};",
                "void Polynomial::initialize(int coeff[ ]) {",
                "  // Classwork: implement this.",
                "",
                "}",
                "void Polynomial::add(Polynomial p2, Polynomial psum) {",
                "  // Classwork: implement this.",
                "",
                "}"}, -880, -380, 30, 50);
        code.typeIn(5.0);
        everything.addAll(code.parts());
        play(new FadeIn(code.hl, d(0.4)));
        pause(0.8);

        double rx = 560;
        // classwork 1: initialize
        play(code.moveHl(4, d(0.5)));
        List<Animation> fo = new ArrayList<>();
        everything.addAll(code.rewrite(fo, 4, "  for (int i = 0; i <= MaxDegree; i++)", d(0.6)));
        everything.addAll(code.rewrite(fo, 5, "    this->coeff[i] = coeff[i];", d(0.6)));
        playAll(fo);
        sayAt("the parameter hides the member: use this->coeff", Colors.LIGHT_GRAY, rx, -400, 28);
        pause(2.4);

        // classwork 2: add
        play(code.moveHl(8, d(0.5)));
        List<Animation> fo2 = new ArrayList<>();
        everything.addAll(code.rewrite(fo2, 8, "  for (int i = 0; i <= MaxDegree; i++)", d(0.6)));
        everything.addAll(code.rewrite(fo2, 9, "    psum.coeff[i] = coeff[i] + p2.coeff[i];", d(0.6)));
        playAll(fo2);
        sayAt("add: slot by slot", Colors.LIGHT_GRAY, rx, -400, 30);
        pause(1.0);

        // run add on two coefficient arrays (MaxDegree = 4)
        int[] a = {-6, 7, 0, -4, 1};
        int[] b = {1, 0, 2, 0, 3};
        double y0 = -230, dy = 125, w = 110, gx = 290, gp = 130;
        String[] names = {"this", "p2", "psum"};
        Color[] cols = {Colors.TEAL, Colors.PINK, Colors.GREEN};
        Cell[][] g = new Cell[3][5];
        List<Animation> in = new ArrayList<>();
        for (int r = 0; r < 3; r++) {
            TextMob nm = mono(names[r], 215, y0 + dy * r, 26, cols[r]);
            in.add(new FadeIn(nm, d(0.6)));
            everything.add(nm);
            for (int k = 0; k < 5; k++) {
                String v = r == 0 ? String.valueOf(a[k]) : (r == 1 ? String.valueOf(b[k]) : "");
                g[r][k] = new Cell(v, gx + 50 + k * gp, y0 + dy * r, w, 84, cols[r], 38);
                if (r == 2) g[r][k].box.setFillColor(Colors.withAlpha(Colors.WHITE, 0.05));
                g[r][k].fadeIn(in, 0.05 * k + 0.1 * r, d(0.5));
                everything.addAll(g[r][k].parts());
            }
        }
        for (int k = 0; k < 5; k++) {
            LaTeXMob pw = latex("x^{" + k + "}", 32, gx + 50 + k * gp, y0 - 75);
            in.add(new FadeInAt(pw, 0.05 * k, d(0.5)));
            everything.add(pw);
        }
        playAll(in);
        pause(0.6);
        for (int k = 0; k < 5; k++) {
            List<Animation> hi = new ArrayList<>();
            g[0][k].color(hi, Colors.ORANGE, d(0.3));
            g[1][k].color(hi, Colors.ORANGE, d(0.3));
            playAll(hi);
            int sum = a[k] + b[k];
            g[2][k].text.setText(String.valueOf(sum));
            List<Animation> res = new ArrayList<>();
            res.add(new FadeIn(g[2][k].text, d(0.4)));
            g[2][k].color(res, Colors.GREEN, d(0.4));
            playAll(res);
            sayAt(a[k] + " + " + b[k] + " = " + sum, Colors.ORANGE, rx, 180, 44);
            pause(0.45);
            List<Animation> back = new ArrayList<>();
            g[0][k].color(back, Colors.TEAL, d(0.3));
            g[1][k].color(back, Colors.PINK, d(0.3));
            playAll(back);
        }
        LaTeXMob sumPoly = latex("\\mathbf{4x^4 - 4x^3 + 2x^2 + 7x - 5}", 44, rx, 270);
        play(new Write(sumPoly, d(2.0)));
        everything.add(sumPoly);
        StrokeTextMob tip1 = stroke("In C++, take psum by reference", rx, 360, 32, Colors.ORANGE, false);
        StrokeTextMob tip2 = stroke("so the sum reaches the caller.", rx, 405, 32, Colors.ORANGE, false);
        play(new Write(tip1, d(2.0)));
        play(new Write(tip2, d(2.0)));
        everything.add(tip1);
        everything.add(tip2);
        pause(2.6);
        fadeOutAll(d(1.2), everything);
        unsay();
        pause(0.4);
    }
}
