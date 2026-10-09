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
 * Standalone clip for slide 30 of the trees deck: a fixed-length code.
 *
 *   - the slide's lines one at a time
 *   - 27 characters (space and a-z) need 5 bits: the 32 five-bit strings appear as a table, the first 27 get their
 *     character in the slide's order, the last five (11011 to 11111) stay unused
 *   - the slide's question: 001001100101110000010110101111 is cut into groups of five, each group looked up in the
 *     table, and the letters spell the answer
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeCodingScene extends PDSTreeClipBase {

    private static final String BITS = "001001100101110000010110101111";

    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();

    @Override
    public void construct() {
        head = writeHeading("Coding (a little different)");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "I want to transmit some data."));
        s.add(ln(0, "Data contains a-z and space."));
        s.add(ln(0, "For these 27 characters, I need 5 bits.").kw("5 bits", Colors.GOLD));
        s.add(ln(1, "For N characters, I need log2(N) bits."));
        s.add(ln(0, "Encoding pattern:"));
        s.add(ln(1, "space = 00000, a = 00001, b = 00010, ..., z = 11010"));
        s.add(ln(0, "Decoding:"));
        s.add(ln(1, "Each 5-bit string represents a unique character"));
        s.add(ln(2, "(except the last five strings: 11011 to 11111)."));
        s.add(ln(1, "What is " + BITS + "?").kw(BITS, Colors.RED));
        List<List<MObject>> text = writeSlide(s, -340);
        pause(1.6);
        swipeAway(text);
        pause(0.4);
        table();
        decode();
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private final List<RectMob> cellBox = new ArrayList<>();
    private final List<TextMob> cellChar = new ArrayList<>();

    private static String bits5(int k) {
        String b = Integer.toBinaryString(k);
        return "00000".substring(b.length()) + b;
    }

    private static String charOf(int k) { return k == 0 ? "space" : k <= 26 ? String.valueOf((char) ('a' + k - 1)) : ""; }

    private void table() {
        sayAt("27 characters do not fit in 4 bits (16 strings): 5 bits give 32 strings.", Colors.LIGHT_GRAY, 0, 440, 34);
        List<Animation> a = new ArrayList<>();
        for (int k = 0; k < 32; k++) {
            int row = k / 8, col = k % 8;
            double x = -752 + 215 * col, y = -330 + 100 * row;
            boolean used = k <= 26;
            RectMob box = panel(x, y, 200, 86, used ? Colors.BLUE : Colors.GRAY, used ? 0.2 : 0.06);
            TextMob code = mono(bits5(k), x, y - 20, 26, used ? Colors.WHITE : Colors.GRAY);
            TextMob ch = label(charOf(k), x, y + 20, 34, used ? Colors.GOLD : Colors.GRAY, false, true);
            a.add(new FadeInAt(box, 0.04 * k, d(0.4)));
            a.add(new FadeInAt(code, 0.04 * k, d(0.4)));
            if (used) a.add(new FadeInAt(ch, 0.04 * k + 0.5, d(0.4)));
            cellBox.add(box);
            cellChar.add(ch);
            mine.add(box);
            mine.add(code);
            mine.add(ch);
        }
        playAll(a);
        pause(1.0);
        sayAt("Space is 00000, a is 00001, b is 00010 ... z is 11010. The last five strings stand for nothing.", Colors.GOLD, 0, 440, 34);
        TextMob un = label("unused", 640, 60, 28, Colors.GRAY, false, true);
        play(new FadeIn(un, d(0.6)));
        mine.add(un);
        pause(2.6);
    }

    private void decode() {
        sayAt("Now decode the slide's string: cut it into groups of five bits.", Colors.LIGHT_GRAY, 0, 440, 34);
        double size = 44, cw = measure("MMMMMMMMMM", MONO, size, false) / 10.0 * 1.1;
        double left = -BITS.length() * cw / 2;
        List<TextMob> bits = new ArrayList<>();
        List<Animation> ba = new ArrayList<>();
        for (int i = 0; i < BITS.length(); i++) {
            Color c = (i / 5) % 2 == 0 ? Colors.WHITE : Colors.ORANGE;
            TextMob b = mono(String.valueOf(BITS.charAt(i)), left + (i + 0.5) * cw, 150, size, c);
            ba.add(new FadeInAt(b, 0.03 * i, d(0.3)));
            bits.add(b);
            mine.add(b);
        }
        playAll(ba);
        pause(1.0);
        RectMob sel = new RectMob(cw * 5 + 8, 62).setCornerRadius(10);
        sel.setFillColor(Colors.withAlpha(Colors.GOLD, 0.2));
        sel.setStrokeColor(Colors.GOLD);
        sel.setStrokeWidth(2.6);
        sel.setPosition(left + 2.5 * cw, 150);
        sel.setOpacity(0);
        add(sel);
        RectMob cur = new RectMob(204, 90).setCornerRadius(14);
        cur.setFillColor(Colors.withAlpha(Colors.GOLD, 0.2));
        cur.setStrokeColor(Colors.GOLD);
        cur.setStrokeWidth(3.6);
        cur.setOpacity(0);
        add(cur);
        mine.add(sel);
        mine.add(cur);
        TextMob out = label("", 0, 270, 70, Colors.GREEN, false, true);
        mine.add(out);
        StringBuilder sb = new StringBuilder();
        for (int g = 0; g < 6; g++) {
            String grp = BITS.substring(5 * g, 5 * g + 5);
            int k = Integer.parseInt(grp, 2);
            List<Animation> mv = new ArrayList<>();
            mv.add(new MoveTo(sel, left + (5 * g + 2.5) * cw, 150, d(0.5)));
            mv.add(new FadeIn(sel, d(0.4)));
            playAll(mv);
            double cx = -752 + 215 * (k % 8), cy = -330 + 100 * (k / 8);
            cur.setPosition(cx, cy);
            play(new FadeIn(cur, d(0.5)));
            pause(0.5);
            sb.append(charOf(k));
            out.setText(sb.toString());
            play(new FadeIn(out, d(0.01)));
            out.setOpacity(1);
            pause(g < 3 ? 0.8 : 0.4);
            play(new FadeOut(cur, d(0.3)));
            sel.setOpacity(1);
        }
        play(new FadeOut(sel, d(0.4)));
        sayAt("The slide's string spells \"" + sb + "\".", Colors.GREEN, 0, 440, 36);
        pause(3.4);
        unsay();
    }
}
