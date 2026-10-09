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
 * Standalone clip for slide 34 of the trees deck: shorter codes.
 *
 *   - the table (character, frequency, three codes) drops in row by row, and each code is drawn as a tree
 *   - code 1 (e=0, t=1, a=00, o=01, i=10): encoding "eat" gives 0001, but 0001 can be read as five different words:
 *     the letters sit on interior nodes, so one code is the start of another
 *   - codes 2 and 3 keep every letter on a leaf: no code is the beginning of another (prefix codes), and 001110 can
 *     be decoded by walking tree 3 and starting again at the root after every leaf: e, a, t
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeShorterScene extends PDSTreeClipBase {

    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();
    private final GT[] ts = new GT[3];
    private final GN[] roots = new GN[3];
    private TextMob encCode;

    @Override
    public void construct() {
        head = writeHeading("Shorter Codes");
        pause(0.5);
        String[][] rows = {{"e", "12.02", "0", "0", "00"}, {"t", "9.10", "1", "10", "10"}, {"a", "8.12", "00", "110", "11"},
                {"o", "7.68", "01", "1110", "010"}, {"i", "7.31", "10", "1111", "011"}};
        Color[][] tint = new Color[5][5];
        mine.addAll(dropTable(new String[]{"Character", "Frequency", "Code", "Code 2", "Code 3"}, rows,
                new double[]{200, 220, 190, 210, 210}, tint, -400));
        pause(1.2);
        trees();
        encoding();
        decodingTough();
        prefix();
        decode3();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private static final String[][] PATHS = {{"0", "1", "00", "01", "10"}, {"0", "10", "110", "1110", "1111"}, {"00", "10", "11", "010", "011"}};
    private static final String[] CH = {"e", "t", "a", "o", "i"};

    private void trees() {
        double[] cx = {-600, -30, 590};
        String[] names = {"Code", "Code 2", "Code 3"};
        for (int k = 0; k < 3; k++) {
            roots[k] = fromPaths(PATHS[k], CH);
            ts[k] = new GT(roots[k], cx[k], 70, 64, 16, 22, 26, true,
                    n -> n.parent == null ? Colors.MAROON : n.name.isEmpty() ? Colors.GRAY : Colors.BLUE, true);
            TextMob nm = label(names[k], cx[k], 10, 30, Colors.GOLD, false, true);
            play(new FadeIn(nm, d(0.5)));
            mine.add(nm);
            ts[k].build(0.25);
            mine.addAll(ts[k].parts());
            List<Animation> a = new ArrayList<>();
            for (GN n : ts[k].nodes) {
                if (n.parent == null) continue;
                TextMob et = ts[k].edgeTag(n, n.slot == 0 ? "0" : "1", n.slot == 0 ? Colors.TEAL : Colors.ORANGE, 22);
                a.add(new FadeIn(et, d(0.5)));
                mine.add(et);
            }
            playAll(a);
            pause(0.5);
        }
    }

    private void narr(String s, Color c) { sayAt(s, c, 0, 470, 34); }

    private GN find(GN root, String name) {
        List<GN> l = new ArrayList<>();
        preorderInto(root, l);
        for (GN n : l) if (n.name.equals(name)) return n;
        return null;
    }

    private void encoding() {
        narr("Encoding is easy: eat = 0 + 00 + 1 = 0001.", Colors.LIGHT_GRAY);
        String[] part = {"e", "a", "t"}, bits = {"0", "00", "1"};
        TextMob eat = label("eat", -600, 405, 40, Colors.WHITE, false, true);
        play(new FadeIn(eat, d(0.5)));
        mine.add(eat);
        StringBuilder sb = new StringBuilder();
        TextMob code = mono("", -380, 405, 46, Colors.GOLD);
        encCode = code;
        mine.add(code);
        for (int i = 0; i < 3; i++) {
            GN n = find(roots[0], part[i]);
            ts[0].paintNow(n, Colors.GOLD, d(0.4));
            sb.append(bits[i]);
            code.setText(sb.toString());
            code.setOpacity(1);
            pause(0.7);
            ts[0].paintNow(n, Colors.BLUE, d(0.3));
        }
        pause(1.6);
        play(new FadeOut(eat, d(0.4)));
    }

    private void decodingTough() {
        narr("Decoding is tough: 0001 = ?  Several words fit.", Colors.ORANGE);
        String[] readings = {"e e e t", "e e o", "e a t", "a e t", "a o"};
        List<MObject> chips = new ArrayList<>();
        for (int i = 0; i < readings.length; i++) {
            List<MObject> ch = chip(readings[i], -150 + 130 * i, 405, 118, 56, Colors.ORANGE, 28);
            List<Animation> a = new ArrayList<>();
            for (MObject m : ch) a.add(new DropIn(m, 30, 0, d(0.5)));
            playAll(a);
            mine.addAll(ch);
            chips.addAll(ch);
            pause(0.5);
        }
        pause(1.0);
        narr("This happens because interior nodes also represent data: e and t sit above other letters.", Colors.RED);
        List<Animation> a = new ArrayList<>();
        ts[0].paint(a, find(roots[0], "e"), Colors.RED, d(0.6));
        ts[0].paint(a, find(roots[0], "t"), Colors.RED, d(0.6));
        playAll(a);
        pause(2.6);
        narr("We need data only at the leaves.", Colors.GREEN);
        List<Animation> b = new ArrayList<>();
        ts[0].paint(b, find(roots[0], "e"), Colors.BLUE, d(0.6));
        ts[0].paint(b, find(roots[0], "t"), Colors.BLUE, d(0.6));
        for (MObject m : chips) b.add(new FadeOut(m, d(0.6)));
        playAll(b);
        pause(1.0);
    }

    private void prefix() {
        List<Animation> a = new ArrayList<>();
        for (int k = 1; k < 3; k++) {
            for (GN n : ts[k].nodes) if (!n.name.isEmpty()) ts[k].paint(a, n, Colors.GREEN, d(0.7));
        }
        playAll(a);
        narr("In codes 2 and 3 every letter is on a leaf: no code is the beginning of another.", Colors.GREEN);
        TextMob pc = label("Called prefix codes.", 590, 405, 34, Colors.GOLD, false, true);
        play(new FadeIn(pc, d(0.7)));
        mine.add(pc);
        pause(3.0);
    }

    private void decode3() {
        if (encCode != null) play(new FadeOut(encCode, d(0.4)));
        narr("We can decode 001110 easily now: walk tree 3, and start again at the root after each leaf.", Colors.LIGHT_GRAY);
        String bits = "001110";
        double cw = measure("MMMMMMMMMM", MONO, 44, false) / 10.0 * 1.2, left = -520;
        List<TextMob> bs = new ArrayList<>();
        for (int i = 0; i < bits.length(); i++) {
            TextMob b = mono(String.valueOf(bits.charAt(i)), left + i * cw, 405, 44, Colors.WHITE);
            play(new FadeIn(b, d(0.15)));
            bs.add(b);
            mine.add(b);
        }
        TextMob out = label("", left + 6 * cw + 120, 405, 46, Colors.GREEN, true, true);
        mine.add(out);
        StringBuilder sb = new StringBuilder();
        GN cur = roots[2];
        for (int i = 0; i < bits.length(); i++) {
            int slot = bits.charAt(i) == '0' ? 0 : 1;
            GN nx = null;
            for (GN k : cur.kids) if (k.slot == slot) nx = k;
            bs.get(i).setFillColor(Colors.GOLD);
            List<Animation> a = new ArrayList<>();
            ts[2].paintEdge(a, nx, Colors.GOLD, d(0.4));
            ts[2].paint(a, nx, Colors.GOLD, d(0.4));
            playAll(a);
            pause(0.4);
            cur = nx;
            if (cur.leaf()) {
                sb.append(cur.name);
                out.setText("= " + sb);
                out.setOpacity(1);
                pause(0.8);
                List<Animation> r = new ArrayList<>();
                for (GN n : ts[2].nodes) {
                    ts[2].paint(r, n, n.parent == null ? Colors.MAROON : n.name.isEmpty() ? Colors.GRAY : Colors.GREEN, d(0.4));
                    ts[2].paintEdge(r, n, Colors.withAlpha(Colors.LIGHT_GRAY, 0.8), d(0.4));
                }
                playAll(r);
                cur = roots[2];
            }
        }
        pause(2.0);
        narr("00 = e, 11 = a, 10 = t: \"eat\", and there was never a choice to make.", Colors.GREEN);
        pause(3.4);
        unsay();
    }
}
