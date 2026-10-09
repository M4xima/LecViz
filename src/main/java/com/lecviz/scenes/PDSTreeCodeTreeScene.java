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
 * Standalone clip for slide 31 of the trees deck: how a code is related to a tree.
 *
 *   - the 5-bit code drawn as a binary tree (0 = left edge, 1 = right edge), cut to the characters the slide shows:
 *     space, a, b, c, d and z, the unused string 11011, and "..." for the rest
 *   - the slide's seven points, each with its own animation: a binary tree; (almost) complete; height 5 = code
 *     length; a unique path for every character; encoding walks from the character's node up to the root and
 *     gets the code reversed; decoding walks down from the root; the interior nodes hold no character
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeCodeTreeScene extends PDSTreeClipBase {

    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();
    private GT t;
    private GN root;
    private final List<TextMob> edgeTags = new ArrayList<>();
    private final List<MObject> caption = new ArrayList<>();

    @Override
    public void construct() {
        head = writeHeading("How is a code related to a tree?");
        pause(0.5);
        String[] paths = {"00000", "00001", "00010", "00011", "00100", "11010", "11011"};
        String[] labels = {"space", "a", "b", "c", "d", "z", ""};
        root = fromPaths(paths, labels);
        t = new GT(root, -170, -300, 90, 10, 20, 24, false,
                n -> n.parent == null ? Colors.MAROON : n.leaf() ? Colors.BLUE : Colors.GRAY, true);
        t.build(0.35);
        mine.addAll(t.parts());
        for (GN n : t.nodes) {
            if (n.parent == null) continue;
            TextMob et = t.edgeTag(n, n.slot == 0 ? "0" : "1", n.slot == 0 ? Colors.TEAL : Colors.ORANGE, 24);
            edgeTags.add(et);
        }
        List<Animation> ea = new ArrayList<>();
        for (TextMob e : edgeTags) ea.add(new FadeIn(e, d(0.8)));
        playAll(ea);
        mine.addAll(edgeTags);
        GN dots1 = byPath("00100"), dots2 = byPath("11010");
        TextMob el = label("...", (dots1.x + dots2.x) / 2, dots1.y, 40, Colors.LIGHT_GRAY, false, true);
        play(new FadeIn(el, d(0.6)));
        mine.add(el);
        pause(0.8);

        // 1
        say("It is a binary tree.");
        pulse(List.of(root, byPath("0"), byPath("1")), Colors.GOLD);
        // 2
        say("Tree is (almost) complete.");
        GN un = byPath("11011");
        TextMob ul = label("unused", un.x, un.y + 52, 24, Colors.GRAY, false, true);
        play(new FadeIn(ul, d(0.6)));
        mine.add(ul);
        pause(2.2);
        // 3
        say("Has height of 5, equal to the code length.");
        double bx = t.nodes.stream().mapToDouble(n -> n.x + n.hw).max().orElse(800) + 50;
        Link br = bracketRight(bx, -300, -300 + 5 * 90, Colors.GOLD);
        TextMob bl = label("5 edges = 5 bits", bx + 30, -300 + 2.5 * 90, 28, Colors.GOLD, true, true);
        play(new DrawLink(br, d(0.8)), new FadeIn(bl, d(0.8)));
        mine.add(br);
        mine.add(bl);
        pause(2.4);
        // 4
        say("Each character has a unique code, because each tree node has a unique path from the root.");
        for (String p : new String[]{"00010", "00011"}) {
            GN leaf = byPath(p);
            List<GN> path = pathTo(leaf);
            colorPath(path, Colors.GOLD);
            TextMob w = label(leaf.name + " = " + p, 720, -240, 44, Colors.GOLD, false, true);
            play(new FadeIn(w, d(0.5)));
            pause(1.2);
            play(new FadeOut(w, d(0.4)));
            remove(w);
            colorPath(path, null);
        }
        pause(0.3);
        // 5
        sayTwo("Encoding: Given a character, traverse back from its node towards the root,", "and we get the reverse of its code.");
        GN c = byPath("00011");
        List<GN> up = pathTo(c);
        java.util.Collections.reverse(up);
        TextMob rev = label("", 720, -200, 56, Colors.ORANGE, false, true);
        TextMob rcap = label("going up from c:", 720, -270, 28, Colors.LIGHT_GRAY, false, true);
        play(new FadeIn(rcap, d(0.5)));
        mine.add(rev);
        mine.add(rcap);
        StringBuilder sb = new StringBuilder();
        t.paintNow(c, Colors.GOLD, d(0.4));
        for (int i = 0; i + 1 < up.size(); i++) {
            GN n = up.get(i);
            sb.append(n.slot == 0 ? '0' : '1');
            rev.setText(sb.toString());
            rev.setOpacity(1);
            List<Animation> a = new ArrayList<>();
            t.paintEdge(a, n, Colors.GOLD, d(0.4));
            t.paint(a, up.get(i + 1), Colors.GOLD, d(0.4));
            playAll(a);
            pause(0.4);
        }
        TextMob fin = label("reversed: " + new StringBuilder(sb).reverse(), 720, -120, 44, Colors.GREEN, false, true);
        play(new FadeIn(fin, d(0.6)));
        mine.add(fin);
        pause(2.6);
        colorPath(pathTo(c), null);
        play(new FadeOut(rev, d(0.4)), new FadeOut(rcap, d(0.4)), new FadeOut(fin, d(0.4)));
        // 6
        sayTwo("Decoding: Given a code, traverse the tree from the root,", "and the node we reach is the corresponding character.");
        String code = "00100";
        TextMob cd = label("code: " + code, 720, -240, 40, Colors.WHITE, false, true);
        play(new FadeIn(cd, d(0.5)));
        mine.add(cd);
        GN cur = root;
        for (char ch : code.toCharArray()) {
            GN nx = null;
            for (GN k : cur.kids) if (k.slot == (ch == '0' ? 0 : 1)) nx = k;
            List<Animation> a = new ArrayList<>();
            t.paintEdge(a, nx, Colors.GOLD, d(0.4));
            t.paint(a, nx, Colors.GOLD, d(0.4));
            playAll(a);
            cur = nx;
            pause(0.5);
        }
        TextMob got = label("we reach: " + cur.name, 720, -170, 44, Colors.GREEN, false, true);
        play(new FadeIn(got, d(0.6)));
        mine.add(got);
        pause(2.6);
        colorPath(pathTo(cur), null);
        play(new FadeOut(cd, d(0.4)), new FadeOut(got, d(0.4)));
        // 7
        say("None of the interior nodes represents a character.");
        List<GN> inner = new ArrayList<>();
        for (GN n : t.nodes) if (!n.leaf() && n != root) inner.add(n);
        pulse(inner, Colors.RED);
        pause(2.0);
        unsay();
        fadeOutAll(d(1.2), caption);
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private GN byPath(String p) {
        GN n = root;
        for (char c : p.toCharArray()) {
            for (GN k : n.kids) if (k.slot == (c == '0' ? 0 : 1)) { n = k; break; }
        }
        return n;
    }

    private List<GN> pathTo(GN n) {
        List<GN> l = new ArrayList<>();
        for (GN x = n; x != null; x = x.parent) l.add(0, x);
        return l;
    }

    private void colorPath(List<GN> path, Color c) {
        List<Animation> a = new ArrayList<>();
        for (GN n : path) {
            if (c == null) {
                t.paint(a, n, n.parent == null ? Colors.MAROON : n.leaf() ? Colors.BLUE : Colors.GRAY, d(0.5));
                t.paintEdge(a, n, Colors.withAlpha(Colors.LIGHT_GRAY, 0.8), d(0.5));
            } else {
                t.paint(a, n, c, d(0.5));
                t.paintEdge(a, n, c, d(0.5));
            }
        }
        playAll(a);
    }

    private void pulse(List<GN> ns, Color c) {
        List<Animation> a = new ArrayList<>();
        for (GN n : ns) t.paint(a, n, c, d(0.5));
        playAll(a);
        pause(1.6);
        List<Animation> b = new ArrayList<>();
        for (GN n : ns) t.paint(b, n, n.parent == null ? Colors.MAROON : n.leaf() ? Colors.BLUE : Colors.GRAY, d(0.5));
        playAll(b);
    }

    private void say(String s) {
        fadeOutAll(d(0.3), caption);
        caption.clear();
        StrokeTextMob t1 = stroke(s, 0, 420, 34, Colors.WHITE, false);
        play(new Write(t1, d(Math.max(1.4, s.length() * 0.05))));
        caption.add(t1);
    }

    private void sayTwo(String a, String b) {
        fadeOutAll(d(0.3), caption);
        caption.clear();
        StrokeTextMob t1 = stroke(a, 0, 395, 34, Colors.WHITE, false);
        play(new Write(t1, d(Math.max(1.4, a.length() * 0.05))));
        StrokeTextMob t2 = stroke(b, 0, 445, 34, Colors.WHITE, false);
        play(new Write(t2, d(Math.max(1.2, b.length() * 0.05))));
        caption.add(t1);
        caption.add(t2);
    }
}
