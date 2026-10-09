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
 * Standalone clip for slides 18-19 of the trees deck: K-ary trees.
 *
 *   Slide 18  the slide's four structs: the two general-tree versions from before, then "for a fixed K" an
 *             array of K child pointers, and "when K == 2" a left and a right pointer
 *   Slide 19  a ternary tree and a binary tree: every node has exactly K child slots, and the slots that are not
 *             used are NULL (drawn as gray hollow circles), so a missing child costs nothing but a NULL pointer
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeKaryScene extends PDSTreeClipBase {

    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();

    @Override
    public void construct() {
        head = writeHeading("K-ary Trees");
        pause(0.5);
        structs();
        pictures();
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    // ── slide 18: the four structs ───────────────────────────────────

    private void structs() {
        CodeBox a = new CodeBox(new String[]{
                "typedef struct TreeNode *PtrToNode;",
                "",
                "struct TreeNode {",
                "    int data;",
                "    PtrToNode firstChild;",
                "    PtrToNode nextSibling;",
                "};"}, -930, -340, 22, 32);
        CodeBox b = new CodeBox(new String[]{
                "#include <vector>",
                "typedef struct TreeNode *PtrToNode;",
                "",
                "struct TreeNode {",
                "    int data;",
                "    std::vector<PtrToNode> children;",
                "};"}, 10, -340, 22, 32);
        a.typeIn(3.0);
        mine.addAll(a.parts());
        b.typeIn(3.0);
        mine.addAll(b.parts());
        TextMob la = label("any number of children (C)", -640, -398, 26, Colors.GRAY, false, false);
        TextMob lb = label("any number of children (C++)", 330, -398, 26, Colors.GRAY, false, false);
        play(new FadeIn(la, d(0.5)), new FadeIn(lb, d(0.5)));
        mine.add(la);
        mine.add(lb);
        sayAt("So far: a node whose number of children is not known in advance.", Colors.LIGHT_GRAY, 0, 440, 34);
        pause(1.8);

        TextMob l1 = label("For a fixed K", -640, -96, 30, Colors.ORANGE, false, true);
        TextMob l2 = label("When K == 2", 330, -96, 30, Colors.GREEN, false, true);
        play(new FadeIn(l1, d(0.6)), new FadeIn(l2, d(0.6)));
        mine.add(l1);
        mine.add(l2);
        CodeBox c = new CodeBox(new String[]{
                "typedef struct TreeNode *PtrToNode;",
                "",
                "struct TreeNode {",
                "    int data;",
                "    PtrToNode children[K];",
                "};"}, -930, -50, 22, 32);
        CodeBox d2 = new CodeBox(new String[]{
                "typedef struct TreeNode *PtrToNode;",
                "",
                "struct TreeNode {",
                "    int data;",
                "    PtrToNode left;",
                "    PtrToNode right;",
                "};"}, 10, -50, 22, 32);
        c.typeIn(3.0);
        mine.addAll(c.parts());
        play(c.moveHl(4, d(0.4)));
        sayAt("If every node has at most K children, an array of K child pointers is enough.", Colors.ORANGE, 0, 440, 34);
        pause(1.6);
        d2.typeIn(3.0);
        mine.addAll(d2.parts());
        play(d2.moveHl(4, d(0.4)));
        sayAt("For K = 2 the two slots get names: left and right.", Colors.GREEN, 0, 440, 34);
        pause(2.6);
        unsay();
        fadeOutAll(d(1.0), mine);
        mine.clear();
        pause(0.3);
    }

    // ── slide 19: a ternary and a binary tree ────────────────────────

    /** Gray hollow circles where a node's unused child slots (NULL pointers) are. */
    private List<MObject> nullSlots(GT t, int k) {
        List<MObject> made = new ArrayList<>();
        for (GN n : t.nodes) {
            int used = n.kids.size();
            for (int s = 0; s < k; s++) {
                boolean taken = false;
                if (k == 2) taken = (s == 0 && leftOf(n) != null) || (s == 1 && rightOf(n) != null);
                else taken = s < used;
                if (taken) continue;
                double x = n.x + (s - (k - 1) / 2.0) * 30;
                CircleMob c = new CircleMob(7);
                c.setFillColor(Color.TRANSPARENT);
                c.setStrokeColor(Colors.withAlpha(Colors.LIGHT_GRAY, 0.75));
                c.setStrokeWidth(2);
                c.setPosition(x, n.y + 46);
                c.setOpacity(0);
                add(c);
                made.add(c);
            }
        }
        return made;
    }

    private void pictures() {
        // the ternary tree: root, three children, nine grandchildren of which some are missing
        GN ter = g("", g("", g(""), g(""), g("")), g("", g(""), g("")), g("", g("")));
        GT t3 = tree(ter, -430, -250, 130, 62, 20, 1, true);
        t3.build(0.35);
        mine.addAll(t3.parts());
        TextMob n3 = label("Ternary: K = 3", -430, -340, 34, Colors.ORANGE, false, true);
        play(new FadeIn(n3, d(0.6)));
        mine.add(n3);
        // the binary tree: some children are missing
        GN bi = bin("", bin("", bin(""), bin("")), bin("", null, bin("")));
        GT t2 = new GT(bi, 430, -250, 130, 90, 20, 1, true, null, true);
        t2.build(0.35);
        mine.addAll(t2.parts());
        TextMob n2 = label("Binary: K = 2", 430, -340, 34, Colors.GREEN, false, true);
        play(new FadeIn(n2, d(0.6)));
        mine.add(n2);
        pause(0.6);

        sayAt("Every node has room for K children. The slots that are not used hold NULL.", Colors.LIGHT_GRAY, 0, 400, 34);
        List<MObject> z3 = nullSlots(t3, 3), z2 = nullSlots(t2, 2);
        List<Animation> a = new ArrayList<>();
        for (MObject m : z3) a.add(new FadeIn(m, d(0.8)));
        for (MObject m : z2) a.add(new FadeIn(m, d(0.8)));
        playAll(a);
        mine.addAll(z3);
        mine.addAll(z2);
        pause(1.2);
        TextMob c3 = mono("PtrToNode children[3];", -430, 250, 28, Colors.WHITE);
        TextMob c2 = mono("PtrToNode left, right;", 430, 250, 28, Colors.WHITE);
        play(new FadeIn(c3, d(0.6)), new FadeIn(c2, d(0.6)));
        mine.add(c3);
        mine.add(c2);
        pause(0.8);
        TextMob k3 = label(z3.size() + " NULL pointers", -430, 320, 30, Colors.LIGHT_GRAY, false, true);
        TextMob k2 = label(z2.size() + " NULL pointers", 430, 320, 30, Colors.LIGHT_GRAY, false, true);
        play(new FadeIn(k3, d(0.6)), new FadeIn(k2, d(0.6)));
        mine.add(k3);
        mine.add(k2);
        pause(3.0);
        unsay();
        fadeOutAll(d(1.2), mine);
        mine.clear();
        pause(0.3);
    }
}
