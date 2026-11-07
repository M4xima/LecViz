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
 * Clips for the intro and logistics deck (0-intro+logistics.pdf, 12 slides), named by slide numbers:
 *
 *   1      the title card, with small pictures of the structures of the course
 *   2to3   where the course sits among the others; the cab example (nearest cab and shortest route, computed)
 *   4to5   more examples (address suggestions as the user types, filtered by prefix); the misconceptions
 *   6to8   what a data structure is (algorithms and structures drawn as a bipartite graph), the standard
 *          structures, the learning outcomes
 *   9to12  logistics (the timetable slots drawn), evaluation, how to get the most from the course, closing card
 *
 * The one-line corrections of the misconceptions are added, and said so on the screen.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSIntroCourseScene extends PDSGraphClipBase {

    private final String part;

    public PDSIntroCourseScene(String part) { this.part = part; }

    @Override
    public void construct() {
        switch (part) {
            case "1": s1(); break;
            case "2to3": s2to3(); break;
            case "4to5": s4to5(); break;
            case "6to8": s6to8(); break;
            default: s9to12(); break;
        }
        unsay();
        fadeOutAll(d(1.2), mine);
        if (head != null) fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    // ── helpers ──────────────────────────────────────────────────────

    private static String[] blanks(int n) {
        String[] b = new String[n];
        java.util.Arrays.fill(b, "");
        return b;
    }

    private void fadeIn(List<MObject> parts, double dur) {
        List<Animation> in = new ArrayList<>();
        fade(in, parts, dur);
        playAll(in);
    }

    private List<MObject> chipIn(String text, double cx, double cy, double w, double h, Color c, double size) {
        List<MObject> ch = chip(text, cx, cy, w, h, c, size);
        mine.addAll(ch);
        fadeIn(ch, d(0.45));
        return ch;
    }

    private void cross(double x, double y) {
        List<LineMob> m = new ArrayList<>();
        m.add(new LineMob(x - 14, y - 14, x + 14, y + 14, Colors.RED, 6));
        m.add(new LineMob(x - 14, y + 14, x + 14, y - 14, Colors.RED, 6));
        for (LineMob l : m) {
            l.setOpacity(0);
            add(l);
            mine.add(l);
        }
        for (LineMob l : m) play(new DrawLine(l, d(0.2)));
    }

    // ── 1 ────────────────────────────────────────────────────────────

    private void s1() {
        StrokeTextMob title = stroke("Programming and Data Structures", 0, -410, 78, Colors.WHITE, true);
        mine.add(title);
        play(new Write(title, d(2.6)));
        TextMob code = label("CS2700", 0, -320, 56, Colors.GOLD, false, true);
        mine.add(code);
        play(new FadeIn(code, d(0.8)));
        // one small picture of each structure of the course
        String[][] arr = {{"3", "1", "4", "1", "5"}};
        Grid g = new Grid(arr, -740, -170, 56, 50, Colors.BLUE, 24);
        List<Animation> a = new ArrayList<>();
        g.show(a, d(0.6));
        playAll(a);
        double lx = -330;
        List<List<MObject>> chips = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            List<MObject> c = chip(String.valueOf(2 + 3 * i), lx + i * 100, -145, 70, 46, Colors.ORANGE, 24);
            mine.addAll(c);
            chips.add(c);
        }
        a = new ArrayList<>();
        for (List<MObject> c : chips) fade(a, c, d(0.5));
        playAll(a);
        for (int i = 0; i < 2; i++) {
            Link l = arrow(lx + i * 100 + 38, -145, lx + (i + 1) * 100 - 38, -145, Colors.LIGHT_GRAY, 3);
            mine.add(l);
            play(new DrawLinkAt(l, 0, d(0.3)));
        }
        G tr = new G(5, true);
        tr.add(0, 1).add(0, 2).add(1, 3).add(1, 4);
        GV tv = new GV(tr, new double[][]{{110, -215}, {50, -150}, {170, -150}, {10, -85}, {90, -85}}, 15, 1, blanks(5), null, null);
        tv.build(0.4);
        GV gv = new GV(exampleGraph(), exampleXY(480, -150, 110, 90), 15, 1, blanks(6), null, null);
        gv.build(0.5);
        TextMob who = label("Rupesh Nasre.", 0, 40, 46, Colors.WHITE, false, false);
        TextMob mail = label("rupesh@cse.iitm.ac.in", 0, 95, 32, Colors.BLUE, false, false);
        TextMob tas = label("TAs: Abhishek, Kaushal, Lokasis, Shan, Solarica, Akshay, Keshav", 0, 175, 28, Colors.LIGHT_GRAY, false, false);
        TextMob web = label("Course webpage: ~rupesh/teaching/pds/aug21", 0, 235, 28, Colors.LIGHT_GRAY, false, false);
        TextMob moodle = label("Moodle: https://courses.iitm.ac.in/course/view.php?id=399", 0, 290, 26, Colors.GRAY, false, false);
        TextMob when = label("August 2021", 0, 400, 32, Colors.GRAY, false, false);
        for (TextMob t : new TextMob[]{who, mail, tas, web, moodle, when}) mine.add(t);
        a = new ArrayList<>();
        a.add(new FadeIn(who, d(0.8)));
        a.add(new FadeInAt(mail, 0.2, d(0.8)));
        a.add(new FadeInAt(tas, 0.4, d(0.8)));
        a.add(new FadeInAt(web, 0.6, d(0.8)));
        a.add(new FadeInAt(moodle, 0.8, d(0.8)));
        a.add(new FadeInAt(when, 1.0, d(0.8)));
        playAll(a);
        say("An array, a list, a tree and a graph: the structures this course is about.", Colors.LIGHT_GRAY);
        pause(3.6);
    }

    // ── 2-3 ──────────────────────────────────────────────────────────

    private void s2to3() {
        head = writeHeading("Placement in Computer Science");
        pause(0.5);
        String[] courses = {"CS1100: Coding", "CS1200: Proofs, Counting", "CS2200: Computation Theory", "CS2300: Overview of Digital World",
                "CS2600: Hardware", "CS2700: Efficient Implementation", "CS2800: Algorithms", "CS3100: Ways of Programming",
                "CS3300: Translation (Programmer and Machine)", "CS3500: Resource Management (User and Machine)"};
        List<Spec> s = new ArrayList<>();
        for (String c : courses) {
            Spec sp = ln(0, c);
            if (c.startsWith("CS2700")) sp.kw("CS2700", Colors.GOLD);
            s.add(sp);
        }
        keep(writeSlide(s, -380));
        pause(1.0);
        G g = new G(7, true);
        String[] nm = {"CS1100", "CS1200", "CS2700", "CS2800", "CS3100", "CS3300", "CS3500"};
        g.add(0, 2).add(1, 2).add(2, 3).add(3, 2).add(2, 4).add(2, 5).add(2, 6);
        GV v = new GV(g, new double[][]{{380, -250}, {380, -40}, {640, -145}, {640, 60}, {900, -270}, {900, -145}, {900, -20}},
                46, 19, nm, new double[]{0, 0, 16, 16, 0, 0, 0}, null);
        v.build(0.8);
        v.light(2, Colors.GOLD);
        say("CS2700 builds on the earlier courses, feeds the later ones, and goes hand in hand with Algorithms.", Colors.LIGHT_GRAY);
        pause(3.6);
        clearMine();
        newHeading("Relevance, with an example");
        String[][] qs = {{"DM", "How many ways can you go from IIT to Central?"}, {"LMC", "Can you compute blah using a smartphone?"},
                {"CO/CA", "How to build a smartphone hardware?"}, {"PDS", "Keep track of cabs; query the nearest ones efficiently"},
                {"Algo", "How to compute the shortest path from IITM to Central?"}, {"OS", "How to give a higher priority to an incoming call?"},
                {"Compilers", "Translating an app or OS to machine code"}, {"Networks", "How does a phone call work?"},
                {"DBMS", "How to store and retrieve world-map data for the user?"}};
        for (int i = 0; i < qs.length; i++) {
            boolean hot = qs[i][0].equals("PDS") || qs[i][0].equals("Algo");
            row(qs[i][0] + ":", -920, -330 + i * 56, 26, hot ? Colors.GOLD : Colors.BLUE, true);
            row(qs[i][1], -770, -330 + i * 56, 24, hot ? Colors.WHITE : Colors.LIGHT_GRAY, true);
        }
        pause(1.0);
        // the cab example on a small road map
        G rd = new G(8, false);
        rd.add(0, 1, 4).add(0, 2, 3).add(1, 3, 5).add(2, 3, 6).add(2, 4, 7).add(3, 5, 4).add(3, 6, 5).add(4, 6, 3).add(5, 7, 6).add(6, 7, 2);
        String[] rn = {"IIT", "A", "B", "C", "D", "E", "F", "Central"};
        GV map = new GV(rd, new double[][]{{220, -200}, {380, -310}, {390, -110}, {540, -230}, {540, -20}, {700, -320}, {720, -110}, {900, -200}},
                28, 16, rn, null, null);
        map.build(0.8);
        int[] cabs = {1, 4, 5};
        for (int c : cabs) mineChip(c, map);
        // Dijkstra from IIT
        int n = rd.n;
        int[] dist = new int[n], prev = new int[n];
        java.util.Arrays.fill(dist, Integer.MAX_VALUE);
        java.util.Arrays.fill(prev, -1);
        boolean[] done = new boolean[n];
        dist[0] = 0;
        for (int it = 0; it < n; it++) {
            int u = -1;
            for (int i = 0; i < n; i++) if (!done[i] && dist[i] != Integer.MAX_VALUE && (u < 0 || dist[i] < dist[u])) u = i;
            if (u < 0) break;
            done[u] = true;
            for (int w : rd.nbrs(u)) if (dist[u] + rd.weight(u, w) < dist[w]) {
                dist[w] = dist[u] + rd.weight(u, w);
                prev[w] = u;
            }
        }
        say("PDS: which cab is nearest to the passenger at IIT? Keep the map and the cabs so this is quick to ask.", Colors.LIGHT_GRAY);
        List<Animation> a = new ArrayList<>();
        for (int i = 0; i < n; i++) map.noteIn(a, i, String.valueOf(dist[i]), Colors.WHITE, 0, -(map.r + 12), 20);
        playAll(a);
        pause(1.0);
        int best = cabs[0];
        for (int c : cabs) if (dist[c] < dist[best]) best = c;
        map.light(best, Colors.GREEN);
        row("nearest cab: at " + rn[best] + ", " + dist[best] + " minutes away", 560, 170, 30, Colors.GREEN, false);
        pause(2.6);
        say("Algo: the shortest route from IIT to Central, found on the same map.", Colors.LIGHT_GRAY);
        List<Integer> path = new ArrayList<>();
        for (int x = 7; x != -1; x = prev[x]) path.add(0, x);
        map.light(0, Colors.GOLD);
        for (int i = 1; i < path.size(); i++) {
            map.trace(path.get(i - 1), path.get(i), Colors.GOLD);
            map.light(path.get(i), Colors.GOLD);
        }
        StringBuilder ps = new StringBuilder();
        for (int i = 0; i < path.size(); i++) ps.append(i > 0 ? " → " : "").append(rn[path.get(i)]);
        row("route: " + ps + "  (" + dist[7] + " minutes)", 560, 225, 28, Colors.GOLD, false);
        pause(2.6);
        row("Algorithms and data structures go hand in hand.", 560, 290, 26, Colors.WHITE, false);
        say("Do not decouple these subjects, especially Algorithms and Data Structures: they go hand in hand.", Colors.GOLD);
        pause(4.0);
    }

    private TextMob row(String s, double x, double y, double size, Color c, boolean left) {
        TextMob t = label(s, x, y, size, c, left, true);
        mine.add(t);
        play(new FadeIn(t, d(0.35)));
        return t;
    }

    private void mineChip(int node, GV map) {
        List<MObject> c = chip("cab", map.x[node], map.y[node] + map.r + 26, 62, 32, Colors.ORANGE, 18);
        mine.addAll(c);
        fadeIn(c, d(0.4));
    }

    // ── 4-5 ──────────────────────────────────────────────────────────

    private void s4to5() {
        head = writeHeading("More Examples");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Track cabs; query the nearest ones efficiently"));
        s.add(ln(0, "Store addresses; suggest as the user types"));
        s.add(ln(0, "Categorize cabs: Micro, Mini, Prime, Sharing"));
        s.add(ln(0, "Quickly find multiple sharers nearby"));
        s.add(ln(0, "When a cab moves, redraw only the diff"));
        s.add(ln(0, "Store past rides to estimate a journey's cost"));
        s.add(ln(0, "Data structures matter more when there is more data,"));
        s.add(ln(3, "and more types of data."));
        keep(writeSlide(s, -380));
        pause(1.2);
        String[] places = {"Adambakkam", "Adyar", "Anna Nagar", "Ambattur", "Besant Nagar", "Central", "Guindy", "IIT Madras"};
        row("destination:", 120, -330, 30, Colors.GRAY, true);
        TextMob typed = row("", 330, -330, 34, Colors.WHITE, true);
        String word = "Ada";
        List<MObject> shown = new ArrayList<>();
        TextMob mt = row("", 560, -330, 26, Colors.GOLD, true);
        say("Address suggestions: after each letter, only the addresses with that prefix are shown.", Colors.LIGHT_GRAY);
        for (int len = 1; len <= word.length(); len++) {
            String pre = word.substring(0, len);
            typed.setText(pre + "|");
            fadeOutAll(d(0.3), shown);
            mine.removeAll(shown);
            shown.clear();
            int k = 0;
            for (String p : places) if (p.toLowerCase().startsWith(pre.toLowerCase())) {
                List<MObject> c = chip(p, 330, -240 + k * 66, 280, 52, Colors.BLUE, 26);
                shown.addAll(c);
                mine.addAll(c);
                fadeIn(c, d(0.35));
                k++;
            }
            mt.setText(k + " match" + (k == 1 ? "" : "es"));
            pause(1.4);
        }
        say("A tree with one letter per level finds everything under a prefix without scanning every address.", Colors.GOLD);
        pause(3.8);
        clearMine();

        newHeading("Misconceptions");
        String[] claims = {"Data structures get created when we use struct.", "Data structures need pointers.",
                "An algorithm must use a specific data structure.", "C++ has more data structures than C.",
                "There are in total seven data structures.", "Union-Find is the best data structure."};
        String[] fix = {"A struct only groups fields; the organisation is the structure.", "An array is a data structure with no pointers.",
                "Different structures can give the same computation.", "The structures can be built in C as well.",
                "There is no fixed count; new ones keep being invented.", "No structure is best for every problem."};
        List<Spec> t = new ArrayList<>();
        for (String c : claims) t.add(ln(0, c));
        List<List<MObject>> g = writeSlide(t, -330);
        keep(g);
        pause(1.0);
        say("All six are misconceptions. (The one-line corrections are added.)", Colors.RED);
        for (int i = 0; i < claims.length; i++) {
            double y = g.get(i).get(g.get(i).size() - 1).getPosition().y();
            cross(-925, y);
            TextMob r = label(fix[i], 940, y, 24, Colors.LIGHT_GRAY, false, false);
            r.setAlignment(javafx.scene.text.TextAlignment.RIGHT);
            r.setPosition(940, y);
            mine.add(r);
            play(new FadeIn(r, d(0.5)));
            pause(0.5);
        }
        pause(3.2);
    }

    // ── 6-8 ──────────────────────────────────────────────────────────

    private void s6to8() {
        head = writeHeading("What is it?");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Data Structures is about organizing data such"));
        s.add(ln(3, "that its storage and retrieval improve the"));
        s.add(ln(3, "efficiency of the algorithms using it."));
        s.add(ln(0, "A data structure may be used by multiple"));
        s.add(ln(3, "algorithms."));
        s.add(ln(0, "An algorithm may use multiple data structures"));
        s.add(ln(3, "simultaneously."));
        s.add(ln(0, "An algorithm may use different data structures"));
        s.add(ln(3, "and achieve the same computation."));
        keep(writeSlide(s, -380));
        pause(1.0);
        G g = new G(6, false);
        g.add(0, 3).add(0, 5).add(1, 4).add(1, 5).add(2, 4).add(2, 5);
        String[] nm = {"BFS", "Dijkstra", "Heapsort", "queue", "heap", "array"};
        GV v = new GV(g, new double[][]{{330, -250}, {330, -70}, {330, 110}, {800, -250}, {800, -70}, {800, 110}}, 56, 20, nm, null, null);
        v.build(0.8);
        for (int i = 0; i < 3; i++) v.light(i, Colors.BLUE);
        for (int i = 3; i < 6; i++) v.light(i, Colors.PINK);
        tag("algorithms", 330, -340, 26, Colors.BLUE, false);
        tag("data structures", 800, -340, 26, Colors.PINK, false);
        say("Algorithms on the left, data structures on the right; a line means 'uses'.", Colors.LIGHT_GRAY);
        pause(1.6);
        // computed from the picture: who is used by many, who uses many
        int most = 3;
        for (int i = 3; i < 6; i++) if (g.deg(i) > g.deg(most)) most = i;
        say("The " + nm[most] + " is used by " + g.deg(most) + " algorithms: one structure, many algorithms.", Colors.GOLD);
        for (int w : g.nbrs(most)) v.trace(most, w, Colors.GOLD);
        pause(2.6);
        say("Dijkstra uses a heap and an array together: one algorithm, several structures. It also works with only an array.", Colors.GOLD);
        v.trace(1, 4, Colors.GREEN);
        v.trace(1, 5, Colors.GREEN);
        pause(3.2);
        clearMine();

        newHeading("Core and Standard");
        List<Spec> t = new ArrayList<>();
        t.add(ln(0, "Array"));
        t.add(ln(0, "Linked List"));
        t.add(ln(1, "Stack"));
        t.add(ln(1, "Queue"));
        t.add(ln(0, "Tree"));
        t.add(ln(1, "Binary Tree"));
        t.add(ln(1, "Binary Search Tree"));
        t.add(ln(1, "Heap"));
        t.add(ln(1, "..."));
        t.add(ln(0, "Hash Table"));
        t.add(ln(0, "Graph"));
        keep(writeSlide(t, -380));
        pause(1.0);
        String[] names = {"Array", "Linked List", "Tree", "Hash Table", "Graph"};
        double[] xs = {260, 520, 780, 390, 650};
        double[] ys = {-230, -230, -230, 10, 10};
        for (int i = 0; i < 5; i++) {
            double cx = xs[i], cy = ys[i];
            if (i == 0) {
                Grid ar = new Grid(new String[][]{{"3", "1", "4"}}, cx - 93, cy - 29, 62, 58, Colors.BLUE, 26);
                List<Animation> a = new ArrayList<>();
                ar.show(a, d(0.5));
                playAll(a);
            } else if (i == 1) {
                for (int k = 0; k < 3; k++) {
                    List<MObject> c = chip(String.valueOf(k + 1), cx - 76 + k * 76, cy, 62, 54, Colors.ORANGE, 24);
                    mine.addAll(c);
                    fadeIn(c, d(0.3));
                }
                for (int k = 0; k < 2; k++) {
                    Link l = arrow(cx - 76 + k * 76 + 32, cy, cx - 76 + (k + 1) * 76 - 32, cy, Colors.LIGHT_GRAY, 3);
                    mine.add(l);
                    play(new DrawLinkAt(l, 0, d(0.25)));
                }
            } else if (i == 2) {
                G tr = new G(3, true);
                tr.add(0, 1).add(0, 2);
                new GV(tr, new double[][]{{cx, cy - 45}, {cx - 56, cy + 30}, {cx + 56, cy + 30}}, 18, 1, blanks(3), null, null).build(0.4);
            } else if (i == 3) {
                Grid hs = new Grid(new String[][]{{"", "7", "", "2"}}, cx - 104, cy - 26, 52, 52, Colors.PURPLE, 22);
                List<Animation> a = new ArrayList<>();
                hs.show(a, d(0.5));
                playAll(a);
            } else {
                new GV(exampleGraph(), exampleXY(cx, cy, 90, 80), 16, 1, blanks(6), null, null).build(0.4);
            }
            row(names[i], cx, cy + 75, 26, Colors.LIGHT_GRAY, false);
        }
        say("These are the structures of the course: one clip per topic follows in the order of the slides.", Colors.GOLD);
        pause(3.4);
        clearMine();

        newHeading("Learning Outcomes");
        List<Spec> u = new ArrayList<>();
        u.add(ln(0, "Choose efficient data structures and apply"));
        u.add(ln(3, "them to solve problems."));
        u.add(ln(0, "Design correct programs to solve problems."));
        u.add(ln(0, "Analyze the efficiency of programs based on time"));
        u.add(ln(3, "complexity."));
        u.add(ln(0, "Prove the correctness of a program using loop"));
        u.add(ln(3, "invariants, pre-conditions and post-conditions in"));
        u.add(ln(3, "programs."));
        keep(writeSlide(u, -330));
        String[] verbs = {"choose", "design", "analyze", "prove"};
        Color[] vc = {Colors.BLUE, Colors.GREEN, Colors.GOLD, Colors.PINK};
        for (int i = 0; i < 4; i++) chipIn(verbs[i], 640, -280 + i * 90, 240, 60, vc[i], 30);
        pause(3.6);
    }

    // ── 9-12 ─────────────────────────────────────────────────────────

    private void s9to12() {
        head = writeHeading("Logistics");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Theory: Join here."));
        s.add(ln(1, "B slot (Mon 9, Tue 8, Wed 12, Fri 11)"));
        s.add(ln(1, "Hackerrank submissions"));
        s.add(ln(0, "Lab: DCF"));
        s.add(ln(1, "R slot (Wed 14)"));
        s.add(ln(0, "Prerequisite: CS1200 Discrete Mathematics"));
        s.add(ln(0, "It is your responsibility to get subscribed to"));
        s.add(ln(3, "moodle (id=399)."));
        s.add(ln(1, "Self-enrolment is enabled."));
        keep(writeSlide(s, -380));
        pause(1.0);
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri"};
        String[] hours = {"8", "9", "10", "11", "12", "13", "14"};
        int[][] bslot = {{0, 9}, {1, 8}, {2, 12}, {4, 11}};
        String[][] cells = new String[5][7];
        Grid tt = new Grid(cells, 250, -260, 90, 60, Colors.GRAY, 26);
        tt.headers(hours, days, 24, Colors.LIGHT_GRAY);
        List<Animation> a = new ArrayList<>();
        tt.show(a, d(0.8));
        playAll(a);
        say("The week: the B slot gives four theory hours, and the R slot is the lab.", Colors.LIGHT_GRAY);
        for (int[] b : bslot) {
            int c = b[1] - 8;
            tt.put(new ArrayList<>(), b[0], c, "B", 0);
            tt.txt[b[0]][c].setOpacity(1);
            a = new ArrayList<>();
            tt.light(a, b[0], c, Colors.BLUE, d(0.4));
            playAll(a);
            pause(0.3);
        }
        tt.txt[2][6].setText("R");
        tt.txt[2][6].setOpacity(1);
        a = new ArrayList<>();
        tt.light(a, 2, 6, Colors.ORANGE, d(0.4));
        playAll(a);
        pause(2.8);
        clearMine();

        newHeading("Evaluation");
        List<Spec> t = new ArrayList<>();
        t.add(ln(0, "Assignments"));
        t.add(ln(1, "Problems on Moodle / Hackerrank"));
        t.add(ln(1, "Mostly C (a little bit of C++)"));
        t.add(ln(1, "Plagiarism checker will be used"));
        t.add(ln(0, "Q1, Q2, Q3"));
        t.add(ln(1, "20 marks each, 1 hour"));
        t.add(ln(1, "On Moodle"));
        t.add(ln(1, "Dates differ by a couple of days from the institute"));
        t.add(ln(2, "calendar (check course webpage)."));
        t.add(ln(1, "You have this week to suggest changes."));
        keep(writeSlide(t, -380));
        pause(1.0);
        for (int i = 0; i < 3; i++) chipIn("Q" + (i + 1) + ":  20 marks  ·  1 hour", 640, -240 + i * 90, 380, 64, PAL3[i], 28);
        say("Three quizzes of 20 marks each: " + 3 * 20 + " marks in all, besides the assignments.", Colors.GOLD);
        pause(3.4);
        clearMine();

        newHeading("To get the MOST out of this course");
        List<Spec> u = new ArrayList<>();
        u.add(ln(0, "Keep hands away from mouse, keyboard, and"));
        u.add(ln(3, "whatsapp."));
        u.add(ln(0, "Solve questions during classwork."));
        u.add(ln(1, "Keep a copy with you. Take notes."));
        u.add(ln(0, "Ask questions (others also haven't understood)."));
        u.add(ln(1, "Do not let a few dominate the discussion."));
        keep(writeSlide(u, -330));
        pause(3.0);
        clearMine();
        fadeOutAll(d(0.9), head);
        head = null;
        madeBy();
    }

    private static final Color[] PAL3 = {Colors.BLUE, Colors.GOLD, Colors.PINK};

    private void madeBy() {
        StrokeTextMob by = stroke("This video was made by", 0, -170, 38, Colors.LIGHT_GRAY, false);
        play(new Write(by, d(2.0)));
        mine.add(by);
        pause(0.2);
        double size = 104, gap = 34;
        double w1 = strokeW("Karthik", true, size), w2 = strokeW("&", true, size), w3 = strokeW("Tejaswi", true, size);
        double left = -(w1 + w2 + w3 + 2 * gap) / 2;
        StrokeTextMob n1 = stroke("Karthik", left + w1 / 2, -30, size, Colors.TEAL, true);
        StrokeTextMob amp = stroke("&", left + w1 + gap + w2 / 2, -30, size, Colors.WHITE, true);
        StrokeTextMob n2 = stroke("Tejaswi", left + w1 + w2 + 2 * gap + w3 / 2, -30, size, Colors.GOLD, true);
        play(new Write(n1, d(1.8)));
        play(new Write(amp, d(0.7)));
        play(new Write(n2, d(1.8)));
        mine.add(n1);
        mine.add(amp);
        mine.add(n2);
        TextMob who = label("Programming and Data Structures · CS5013 · LecViz", 0, 330, 30, Colors.GRAY, false, false);
        mine.add(who);
        play(new FadeIn(who, d(0.8)));
        pause(3.4);
    }
}
