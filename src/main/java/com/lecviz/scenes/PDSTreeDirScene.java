package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Standalone clip for slide 11 of the trees deck: directory listing. (Slide 12, "Switch to code: 2.cpp and
 * 3.cpp", only names source files and has no clip.)
 *
 *   - the directory tree of the lecture builds level by level (directories blue, files gray)
 *   - the yellow note asks which Linux command lists a directory in a tree-like format
 *   - the answer, typed in a terminal: "tree", whose output (drawn from the same data) has the
 *     same shape, and ends with the number of directories and files
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSTreeDirScene extends PDSTreeClipBase {

    static final Set<String> DIRS = Set.of("/", "home", "somesh", "saurabh", "jk", "acad", "intern", "ibm", "first",
            "second", "third", "cs1100", "spw", "bintree", "searchtree");

    private StrokeTextMob head;
    private final List<MObject> mine = new ArrayList<>();

    @Override
    public void construct() {
        head = writeHeading("Directory Listing");
        pause(0.5);
        GN root = dirTree();
        GT t = new GT(root, 0, -370, 100, 16, 22, 22, true,
                n -> n.parent == null ? Colors.GOLD : DIRS.contains(n.name) ? Colors.BLUE : Colors.GRAY);
        t.build(0.35);
        mine.addAll(t.parts());
        pause(0.8);

        // the sticky note
        RectMob note = new RectMob(640, 190).setCornerRadius(10);
        note.setFillColor(Colors.withAlpha(Colors.GOLD, 0.92));
        note.setStrokeColor(Colors.withAlpha(Colors.GOLD, 1.0));
        note.setStrokeWidth(2);
        note.setPosition(-540, -330);
        note.setOpacity(0);
        add(note);
        TextMob n1 = label("There is a Linux command to list", -540, -385, 28, INK, false, false);
        TextMob n2 = label("a directory in a tree-like format.", -540, -345, 28, INK, false, false);
        TextMob n3 = label("Any guesses for the command?", -540, -290, 30, INK, false, true);
        play(new FadeIn(note, d(0.7)), new FadeIn(n1, d(0.7)), new FadeIn(n2, d(0.7)), new FadeIn(n3, d(0.7)));
        mine.add(note);
        mine.add(n1);
        mine.add(n2);
        mine.add(n3);
        pause(3.0);

        // the tree makes way for the terminal
        List<Animation> out = new ArrayList<>();
        for (MObject m : mine) out.add(new FadeOut(m, d(0.9)));
        playAll(out);
        for (MObject m : mine) remove(m);
        mine.clear();
        pause(0.2);
        terminal(root);
        fadeOutAll(d(1.2), mine);
        fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    private void lines(GN n, String prefix, List<String> out) {
        for (int i = 0; i < n.kids.size(); i++) {
            GN c = n.kids.get(i);
            boolean last = i == n.kids.size() - 1;
            out.add(prefix + (last ? "└── " : "├── ") + c.name);
            lines(c, prefix + (last ? "    " : "│   "), out);
        }
    }

    private void terminal(GN root) {
        List<String> ls = new ArrayList<>();
        ls.add("/");
        lines(root, "", ls);
        int dirs = 0, files = 0;
        List<GN> all = new ArrayList<>();
        preorderInto(root, all);
        for (GN n : all) {
            if (n == root) continue;
            if (DIRS.contains(n.name)) dirs++;
            else files++;
        }
        ls.add("");
        ls.add(dirs + " directories, " + files + " files");

        double top = -360, pitch = 31;
        int perPanel = 17;
        RectMob p1 = panel(-470, -95, 860, 620, Colors.GRAY, 0.12);
        RectMob p2 = panel(470, -95, 860, 620, Colors.GRAY, 0.12);
        p1.setFillColor(Colors.withAlpha(Color.web("#1B0F26"), 0.95));
        p2.setFillColor(Colors.withAlpha(Color.web("#1B0F26"), 0.95));
        play(new FadeIn(p1, d(0.6)), new FadeIn(p2, d(0.6)));
        mine.add(p1);
        mine.add(p2);
        TextMob prompt = mono("$ ", -860, top, 26, Colors.GREEN);
        prompt.setAlignment(TextAlignment.LEFT);
        TextMob cmd = mono("tree", -830, top, 26, Colors.WHITE);
        cmd.setAlignment(TextAlignment.LEFT);
        play(new FadeIn(prompt, d(0.4)));
        play(new TypeAt(cmd, 0, d(0.9)));
        mine.add(prompt);
        mine.add(cmd);
        pause(0.4);
        List<Animation> an = new ArrayList<>();
        for (int i = 0; i < ls.size(); i++) {
            int panelIdx = i < perPanel ? 0 : 1;
            int row = i < perPanel ? i + 1 : i - perPanel;
            double x = panelIdx == 0 ? -860 : 40;
            Color c = i == ls.size() - 1 ? Colors.GOLD : DIRS.contains(ls.get(i).replaceAll("^[│ ├└─]*", "")) || i == 0 ? Colors.LIGHT_BLUE : Colors.WHITE;
            TextMob t = mono(ls.get(i), x, top + row * pitch, 26, c);
            t.setAlignment(TextAlignment.LEFT);
            an.add(new FadeInAt(t, 0.11 * i, d(0.25)));
            mine.add(t);
        }
        playAll(an);
        pause(1.0);
        StrokeTextMob cap = stroke("tree draws a directory as a tree: the same shape as our picture.", 0, 390, 38, Colors.GREEN, false);
        play(new Write(cap, d(3.2)));
        mine.add(cap);
        pause(3.0);
    }
}
