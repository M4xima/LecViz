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
 * Standalone clip for slides 1-4 of the lists deck: the title card, what an ADT is, the List ADT
 * and its question about complexity, and other ADTs.
 *
 *   Slide 1  "Lists" is written over a list that builds node by node, then the author line
 *   Slide 2  the points come one line at a time; then a picture: a program calling the interface
 *            of a List ADT, an implementation hidden behind a curtain, and the same calls working
 *            when the implementation is swapped from an array to a linked list
 *   Slide 3  the List class types in, the complexity question appears, and every operation tries
 *            on a few guesses before settling on "O(?)": the answer depends on the implementation
 *   Slide 4  the other ADTs as cards whose operations (the interface) are clicked: a fan regulator
 *            knob, an integer, a student record
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListIntroScene extends PDSListClipBase {

    private StrokeTextMob head;

    @Override
    public void construct() {
        titleCard();
        adt();
        listAsAdt();
        otherAdts();
    }

    // ── helpers ──────────────────────────────────────────────────────

    /** A chip pressed: it lights up and settles back. */
    private void click(List<MObject> chip, Color c) {
        RectMob r = (RectMob) chip.get(0);
        play(new ColorChange(r, Colors.withAlpha(c, 0.8), d(0.15)));
        play(new ColorChange(r, Colors.withAlpha(c, 0.22), d(0.35)));
    }

    private Link bracket(double x, double y1, double y2, Color c) {
        Link l = new Link(new double[]{x - 22, x, x, x - 22}, new double[]{y1, y1, y2, y2}, c, 3.2, false);
        add(l);
        return l;
    }

    // ── slide 1 ──────────────────────────────────────────────────────

    private void titleCard() {
        List<MObject> mine = new ArrayList<>();
        StrokeTextMob title = stroke("Lists", 0, -310, 150, Colors.WHITE, true);
        play(new Write(title, d(2.4)));
        mine.add(title);
        pause(0.3);

        Row row = new Row(new String[]{"4", "2", "7", "2", "9"}, 70, -70, 200, NODE);
        LNode first = row.nodes.get(0);
        Link headArrow = arrow(first.leftX() - 150, first.y, first.leftX() - 3, first.y, Colors.GOLD, 3.5);
        TextMob headLab = label("head", first.leftX() - 92, first.y - 34, 30, Colors.GOLD, false, true);
        play(new DrawLink(headArrow, d(0.6)), new FadeIn(headLab, d(0.6)));
        row.build(d(0.4), d(0.6));
        mine.addAll(row.parts());
        mine.add(headArrow);
        mine.add(headLab);
        pause(0.4);

        StrokeTextMob name = stroke("Rupesh Nasre.", 0, 130, 56, Colors.WHITE, false);
        play(new Write(name, d(2.0)));
        StrokeTextMob mail = stroke("rupesh@cse.iitm.ac.in", 0, 200, 34, Colors.LIGHT_GRAY, false);
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

    private void adt() {
        head = writeHeading("ADT");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Abstract Data Type"));
        s.add(ln(0, "Defines the interface of the functionality").kw("interface", Colors.BLUE));
        s.add(ln(3, "provided by the data structure."));
        s.add(ln(0, "Hides implementation details."));
        s.add(ln(1, "Defines what and hides how.").kw("what", Colors.TEAL).kw("how", Colors.ORANGE));
        s.add(ln(0, "Makes software modular."));
        s.add(ln(0, "Allows easy change of implementation."));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(1.2);
        swipeAway(text);
        pause(0.4);

        adtPicture();
        fadeOutAll(d(0.8), head);
        pause(0.3);
    }

    private void adtPicture() {
        List<MObject> mine = new ArrayList<>();

        // the program that uses the list
        RectMob client = panel(-720, -10, 340, 250, Colors.TEAL, 0.14);
        TextMob clientLab = label("your program", -720, -165, 26, Colors.TEAL, false, true);
        TextMob c1 = mono("list.insert(5);", -720, -55, 27, Colors.WHITE);
        TextMob c2 = mono("list.find(9);", -720, 10, 27, Colors.WHITE);
        TextMob c3 = mono("list.print();", -720, 75, 27, Colors.WHITE);
        RectMob callBand = panel(-720, -55, 316, 46, Colors.GOLD, 0.3);
        callBand.setStrokeColor(Colors.withAlpha(Colors.GOLD, 0.9));
        play(new FadeIn(client, d(0.6)), new FadeIn(clientLab, d(0.6)), new FadeInAt(c1, 0.3, d(0.5)),
                new FadeInAt(c2, 0.6, d(0.5)), new FadeInAt(c3, 0.9, d(0.5)));
        mine.add(client);
        mine.add(clientLab);
        mine.add(c1);
        mine.add(c2);
        mine.add(c3);
        mine.add(callBand);
        pause(0.4);

        // the List ADT: an interface on the left, the implementation on the right
        RectMob adtBox = panel(210, 30, 1340, 470, Colors.BLUE, 0.06);
        TextMob adtLab = label("List ADT", 210, -230, 30, Colors.BLUE, false, true);
        RectMob ifacePanel = panel(-315, 40, 260, 400, Colors.GOLD, 0.08);
        TextMob ifaceLab = label("interface: what", -315, -135, 24, Colors.GOLD, false, true);
        play(new FadeIn(adtBox, d(0.6)), new FadeIn(adtLab, d(0.6)), new FadeIn(ifacePanel, d(0.7)),
                new FadeIn(ifaceLab, d(0.7)));
        mine.add(adtBox);
        mine.add(adtLab);
        mine.add(ifacePanel);
        mine.add(ifaceLab);

        String[] ops = {"insert(e)", "find(e)", "remove(e)", "print()", "size()"};
        List<List<MObject>> chips = new ArrayList<>();
        List<Animation> drop = new ArrayList<>();
        for (int k = 0; k < ops.length; k++) {
            List<MObject> ch = chip(ops[k], -315, -75 + 62 * k, 216, 50, Colors.GOLD, 26);
            chips.add(ch);
            for (MObject m : ch) {
                drop.add(new DropIn(m, 60, d(0.45) * k, d(0.7)));
                mine.add(m);
            }
        }
        playAll(drop);
        Link call = arrow(-545, -10, -448, -10, Colors.GOLD, 3.4);
        TextMob callLab = label("calls", -497, -36, 22, Colors.GOLD, false, true);
        play(new DrawLink(call, d(0.6)), new FadeIn(callLab, d(0.6)));
        mine.add(call);
        mine.add(callLab);

        RectMob implPanel = panel(350, 40, 1010, 400, Colors.PURPLE, 0.07);
        TextMob implLab = label("implementation: how", 350, -135, 24, Colors.PURPLE, false, true);
        RectMob curtain = panel(350, 60, 960, 330, Colors.GRAY, 0.9);
        curtain.setFillColor(Colors.withAlpha(INK, 0.97));
        TextMob q = label("?", 350, 20, 110, Colors.GRAY, false, true);
        TextMob hid = label("hidden from the program", 350, 120, 28, Colors.GRAY, false, false);
        play(new FadeIn(implPanel, d(0.7)), new FadeIn(implLab, d(0.7)), new FadeIn(curtain, d(0.8)),
                new FadeIn(q, d(0.8)), new FadeIn(hid, d(0.8)));
        mine.add(implPanel);
        mine.add(implLab);
        mine.add(curtain);
        mine.add(q);
        mine.add(hid);

        StrokeTextMob cap = stroke("Defines what, hides how.", 0, 335, 40, Colors.WHITE, false);
        play(new Write(cap, d(2.0)));
        pause(0.6);

        // the program calls insert(5): it never sees what happens behind the curtain
        play(new FadeIn(callBand, d(0.4)));
        click(chips.get(0), Colors.GOLD);
        play(new ScaleTo(q, 1.3, d(0.3)));
        play(new ScaleTo(q, 1.0, d(0.4)));
        pause(0.5);

        // the curtain lifts: an array
        StrokeTextMob cap2 = stroke("Makes software modular.", 0, 335, 40, Colors.WHITE, false);
        play(new FadeOut(cap, d(0.4)));
        remove(cap);
        play(new Write(cap2, d(1.8)));
        play(new FadeOut(curtain, d(0.9)), new FadeOut(q, d(0.9)), new FadeOut(hid, d(0.9)));
        TextMob arrLab = label("an array", 350, -100, 28, Colors.PURPLE, false, true);
        List<MObject> cells = new ArrayList<>();
        int[] vals = {4, 2, 7, 2, 9};
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            double x = 350 + (i - 3.5) * 100;
            RectMob cb = panel(x, 60, 88, 80, Colors.TEAL, i < 5 ? 0.28 : 0.08);
            in.add(new FadeInAt(cb, 0.08 * i, d(0.5)));
            cells.add(cb);
            if (i < 5) {
                TextMob t = label(String.valueOf(vals[i]), x, 60, 34, Colors.WHITE, false, true);
                in.add(new FadeInAt(t, 0.08 * i, d(0.5)));
                cells.add(t);
            }
        }
        in.add(new FadeIn(arrLab, d(0.6)));
        playAll(in);
        cells.add(arrLab);
        mine.addAll(cells);
        pause(0.6);
        click(chips.get(0), Colors.GOLD);
        // the 6th cell (index 5) gets the value
        RectMob cell5 = null;
        for (MObject m : cells) {
            if (m instanceof RectMob && Math.abs(m.getPosition().x() - (350 + 1.5 * 100)) < 1) cell5 = (RectMob) m;
        }
        TextMob five = label("5", 350 + 1.5 * 100, 60, 34, Colors.WHITE, false, true);
        play(new ColorChange(cell5, Colors.withAlpha(NEW_NODE, 0.45), d(0.4)), new FadeIn(five, d(0.4)));
        mine.add(five);
        pause(1.0);

        // swap the implementation: a linked list, the program is unchanged
        StrokeTextMob cap3 = stroke("Allows easy change of implementation.", 0, 335, 40, Colors.WHITE, false);
        play(new FadeOut(cap2, d(0.4)));
        remove(cap2);
        play(new Write(cap3, d(2.2)));
        List<Animation> out = new ArrayList<>();
        for (MObject m : cells) out.add(new FadeOut(m, d(0.7)));
        out.add(new FadeOut(five, d(0.7)));
        playAll(out);
        for (MObject m : cells) remove(m);
        remove(five);
        mine.removeAll(cells);
        mine.remove(five);

        TextMob llLab = label("a linked list", 350, -100, 28, Colors.PURPLE, false, true);
        Row row = new Row(new String[]{"4", "2", "7", "2", "9", "5"}, 350, 60, 172, NODE);
        row.nodes.get(4).nullNext();
        List<Animation> build = new ArrayList<>();
        build.add(new FadeIn(llLab, d(0.6)));
        for (int i = 0; i < 5; i++) {
            row.nodes.get(i).fadeIn(build, d(0.3) * i, d(0.6));
            if (i < 4) build.add(new DrawLinkAt(row.links.get(i), d(0.3) * i + d(0.3), d(0.5)));
        }
        playAll(build);
        mine.add(llLab);
        mine.addAll(row.parts());
        Ptr hp = above("head", row.nodes.get(0), Colors.GOLD);
        play(new FadeIn(hp.arrow, d(0.5)), new FadeIn(hp.lab, d(0.5)));
        mine.addAll(hp.parts());
        pause(0.6);

        // the very same call
        play(new FadeOut(callBand, d(0.2)));
        play(new FadeIn(callBand, d(0.4)));
        click(chips.get(0), Colors.GOLD);
        LNode last = row.nodes.get(5);
        LNode prev = row.nodes.get(4);
        List<Animation> ins = new ArrayList<>();
        last.paint(ins, NEW_NODE, 0.01);
        playAll(ins);
        List<Animation> show = new ArrayList<>();
        last.fadeIn(show, 0, d(0.6));
        prev.unNull(show, d(0.5));
        show.add(new DrawLinkAt(row.links.get(4), d(0.2), d(0.6)));
        playAll(show);
        TextMob same = label("same code, new implementation", -720, 165, 26, Colors.GREEN, false, true);
        play(new FadeIn(same, d(0.6)));
        mine.add(same);
        pause(2.4);

        mine.add(cap3);
        fadeOutAll(d(1.0), mine);
        pause(0.3);
    }

    // ── slide 3 ──────────────────────────────────────────────────────

    private void listAsAdt() {
        head = writeHeading("List as an ADT");
        pause(0.4);
        CodeBox code = new CodeBox(new String[]{
                "class List {",
                "public:",
                "  List();",
                "  void insert(Element e);",
                "  bool find(Element e);",
                "  void remove(Element e);",
                "  void print();",
                "  int size();",
                "};"}, -880, -320, 36, 62);
        code.typeIn(4.5);
        pause(0.5);

        // one "O(?)" chip per operation, each trying a few guesses first
        String[] guess = {"O(1)", "O(N)", "O(log N)", "O(N²)", "O(N log N)"};
        List<List<MObject>> chips = new ArrayList<>();
        List<Animation> pop = new ArrayList<>();
        for (int k = 0; k < 5; k++) {
            List<MObject> ch = chip("", -150, code.lineY(3 + k), 150, 48, Colors.GOLD, 26);
            chips.add(ch);
            for (MObject m : ch) pop.add(new FadeInAt(m, 0.15 * k, d(0.5)));
        }
        playAll(pop);
        List<MObject> made = new ArrayList<>();
        Link br = bracket(-40, code.lineY(3) - 30, code.lineY(7) + 30, Colors.GOLD);
        RectMob bubble = panel(470, code.lineY(5), 780, 220, Colors.GOLD, 0.15);
        StrokeTextMob b1 = stroke("What are the complexities", 470, code.lineY(5) - 36, 46, Colors.WHITE, false);
        StrokeTextMob b2 = stroke("of these operations?", 470, code.lineY(5) + 30, 46, Colors.WHITE, false);
        Link lead = arrow(80, code.lineY(5), -34, code.lineY(5), Colors.GOLD, 3.4);
        play(new DrawLink(br, d(0.8)), new FadeIn(bubble, d(0.7)));
        play(new Write(b1, d(1.9)));
        play(new Write(b2, d(1.6)));
        play(new DrawLink(lead, d(0.5)));
        made.add(br);
        made.add(bubble);
        made.add(b1);
        made.add(b2);
        made.add(lead);
        pause(0.5);

        for (int step = 0; step < 14; step++) {
            for (int k = 0; k < 5; k++) ((TextMob) chips.get(k).get(1)).setText(guess[(step * 2 + k * 3) % 5]);
            pause(0.07 * 1.6);
        }
        for (int k = 0; k < 5; k++) ((TextMob) chips.get(k).get(1)).setText("O(?)");
        pause(1.0);

        StrokeTextMob cap = stroke("It depends on how we implement it: an array, or a linked list?", 470, code.lineY(5) + 160, 32,
                Colors.ORANGE, false);
        play(new Write(cap, d(3.4)));
        made.add(cap);
        pause(2.6);

        List<MObject> all = new ArrayList<>(code.parts());
        for (List<MObject> ch : chips) all.addAll(ch);
        all.addAll(made);
        all.add(head);
        fadeOutAll(d(1.0), all);
        pause(0.3);
    }

    // ── slide 4 ──────────────────────────────────────────────────────

    private void otherAdts() {
        head = writeHeading("Other ADTs");
        pause(0.4);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Fan regulator"));
        s.add(ln(1, "IncSpeed, decSpeed, getSpeed, getCompanyName"));
        s.add(ln(0, "Integer"));
        s.add(ln(1, "size, isSigned, getValue, setValue, add, sub"));
        s.add(ln(0, "Student"));
        s.add(ln(1, "getRollNo, getHostel, getFavGame, setHostel,"));
        s.add(ln(2, "getSlots, setCGPA"));
        List<List<MObject>> text = writeSlide(s, -330);
        pause(1.2);
        swipeAway(text);
        pause(0.4);

        List<MObject> mine = new ArrayList<>();
        double[] cx = {-610, 0, 610};
        String[] title = {"Fan regulator", "Integer", "Student"};
        Color[] col = {Colors.TEAL, Colors.GOLD, Colors.PINK};
        String[][] ops = {
                {"IncSpeed", "decSpeed", "getSpeed", "getCompanyName"},
                {"size", "isSigned", "getValue", "setValue", "add", "sub"},
                {"getRollNo", "getHostel", "getFavGame", "setHostel", "getSlots", "setCGPA"}};
        List<List<List<MObject>>> chips = new ArrayList<>();
        List<Animation> in = new ArrayList<>();
        for (int c = 0; c < 3; c++) {
            RectMob card = panel(cx[c], 30, 540, 700, col[c], 0.07);
            TextMob t = label(title[c], cx[c], -285, 36, col[c], false, true);
            in.add(new FadeInAt(card, 0.3 * c, d(0.7)));
            in.add(new FadeInAt(t, 0.3 * c, d(0.7)));
            mine.add(card);
            mine.add(t);
            List<List<MObject>> row = new ArrayList<>();
            for (int k = 0; k < ops[c].length; k++) {
                List<MObject> ch = chip(ops[c][k], cx[c], -10 + 58 * k, 430, 46, col[c], 27);
                row.add(ch);
                for (MObject m : ch) {
                    in.add(new DropIn(m, 50, 0.3 * c + 0.35 + 0.12 * k, d(0.7)));
                    mine.add(m);
                }
            }
            chips.add(row);
        }
        playAll(in);

        // 1) fan regulator: a knob whose pointer turns
        double kx = cx[0], ky = -170;
        CircleMob knob = new CircleMob(62);
        knob.setFillColor(Colors.withAlpha(Colors.TEAL, 0.14));
        knob.setStrokeColor(Colors.withAlpha(Colors.TEAL, 0.9));
        knob.setStrokeWidth(3);
        knob.setPosition(kx, ky);
        knob.setOpacity(0);
        add(knob);
        mine.add(knob);
        List<Animation> tick = new ArrayList<>();
        tick.add(new FadeIn(knob, d(0.6)));
        for (int i = 0; i < 6; i++) {
            double a = Math.toRadians(-210 + 48 * i);
            CircleMob dot = new CircleMob(4);
            dot.setFillColor(Colors.TEAL);
            dot.setStrokeColor(Color.TRANSPARENT);
            dot.setPosition(kx + 84 * Math.cos(a), ky + 84 * Math.sin(a));
            dot.setOpacity(0);
            add(dot);
            tick.add(new FadeInAt(dot, 0.05 * i, d(0.4)));
            mine.add(dot);
        }
        int speed = 2;
        double[][] tip = tipFor(kx, ky, speed);
        Link needle = new Link(tip[0], tip[1], Colors.WHITE, 5, false);
        add(needle);
        tick.add(new FadeIn(needle, d(0.5)));
        mine.add(needle);
        TextMob speedLab = label("speed 2", kx, ky + 100, 24, Colors.TEAL, false, true);
        tick.add(new FadeIn(speedLab, d(0.5)));
        mine.add(speedLab);

        // 2) integer: a value on a small display
        RectMob disp = panel(cx[1], -170, 220, 110, Colors.GOLD, 0.12);
        TextMob num = label("42", cx[1], -170, 70, Colors.WHITE, false, true);
        tick.add(new FadeIn(disp, d(0.6)));
        tick.add(new FadeIn(num, d(0.6)));
        mine.add(disp);
        mine.add(num);

        // 3) student: a small ID card
        RectMob id = panel(cx[2], -170, 330, 130, Colors.PINK, 0.12);
        CircleMob photo = new CircleMob(30);
        photo.setFillColor(Colors.withAlpha(Colors.PINK, 0.4));
        photo.setStrokeColor(Colors.PINK);
        photo.setStrokeWidth(2.5);
        photo.setPosition(cx[2] - 112, -170);
        photo.setOpacity(0);
        add(photo);
        TextMob roll = mono("CS23B042", cx[2] + 36, -205, 22, Colors.WHITE);
        TextMob hostel = mono("Ganga", cx[2] + 36, -170, 22, Colors.WHITE);
        TextMob cgpa = mono("CGPA 8.5", cx[2] + 36, -135, 22, Colors.WHITE);
        tick.add(new FadeIn(id, d(0.6)));
        tick.add(new FadeIn(photo, d(0.6)));
        tick.add(new FadeIn(roll, d(0.6)));
        tick.add(new FadeIn(hostel, d(0.6)));
        tick.add(new FadeIn(cgpa, d(0.6)));
        mine.add(id);
        mine.add(photo);
        mine.add(roll);
        mine.add(hostel);
        mine.add(cgpa);
        playAll(tick);
        pause(0.6);

        // the operations are clicked: only the interface is used, never the inside
        click(chips.get(0).get(0), Colors.TEAL);
        speed = turn(needle, kx, ky, speed, 3, speedLab);
        click(chips.get(0).get(0), Colors.TEAL);
        speed = turn(needle, kx, ky, speed, 4, speedLab);
        click(chips.get(0).get(1), Colors.TEAL);
        speed = turn(needle, kx, ky, speed, 3, speedLab);
        click(chips.get(0).get(2), Colors.TEAL);
        play(new ScaleTo(speedLab, 1.35, d(0.25)));
        play(new ScaleTo(speedLab, 1.0, d(0.3)));
        pause(0.3);

        click(chips.get(1).get(4), Colors.GOLD);
        num.setText("47");
        play(new ScaleTo(num, 1.25, d(0.2)));
        play(new ScaleTo(num, 1.0, d(0.25)));
        click(chips.get(1).get(5), Colors.GOLD);
        num.setText("45");
        play(new ScaleTo(num, 1.25, d(0.2)));
        play(new ScaleTo(num, 1.0, d(0.25)));
        pause(0.3);

        click(chips.get(2).get(1), Colors.PINK);
        play(new ScaleTo(hostel, 1.25, d(0.25)));
        play(new ScaleTo(hostel, 1.0, d(0.3)));
        click(chips.get(2).get(3), Colors.PINK);
        hostel.setText("Narmada");
        play(new ScaleTo(hostel, 1.25, d(0.25)));
        play(new ScaleTo(hostel, 1.0, d(0.3)));
        click(chips.get(2).get(5), Colors.PINK);
        cgpa.setText("CGPA 9.1");
        play(new ScaleTo(cgpa, 1.25, d(0.25)));
        play(new ScaleTo(cgpa, 1.0, d(0.3)));
        pause(0.5);

        StrokeTextMob cap = stroke("Each ADT is just a set of operations: the interface.", 0, 420, 38, Colors.WHITE, false);
        play(new Write(cap, d(3.0)));
        mine.add(cap);
        pause(2.6);
        mine.add(head);
        fadeOutAll(d(1.2), mine);
        pause(0.4);
    }

    /** The needle's end points for a knob setting 0..5. */
    private double[][] tipFor(double kx, double ky, int speed) {
        double a = Math.toRadians(-210 + 48 * speed);
        return pts(kx, ky, kx + 54 * Math.cos(a), ky + 54 * Math.sin(a));
    }

    private int turn(Link needle, double kx, double ky, int from, int to, TextMob lab) {
        double[][] t = tipFor(kx, ky, to);
        play(new LinkTo(needle, t[0], t[1], d(0.5)));
        lab.setText("speed " + to);
        return to;
    }
}
