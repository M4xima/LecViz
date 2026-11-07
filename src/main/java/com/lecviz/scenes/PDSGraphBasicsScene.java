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
 * Clips for slides 1-8 of the graph deck: the title card, "Graphs are Everywhere!", what a graph is, the
 * nomenclature, relationships and their link to discrete maths, and the "own grandfather" puzzle.
 *
 *   1to2  title card; four example graphs (social, program flow, roads, a big interaction network)
 *   3     the definition: V and E read off the picture, lists/trees as special cases
 *   4     names mapped to integers, edge weights, directed edges
 *   5to6  relations that form graphs; symmetric, transitive, equivalence, partial order as graph shapes
 *   7to8  the song (described, its lyrics are not reproduced) and the 1822 puzzle as a cycle of relations
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSGraphBasicsScene extends PDSGraphClipBase {

    private final String part;

    public PDSGraphBasicsScene(String part) { this.part = part; }

    @Override
    public void construct() {
        switch (part) {
            case "1to2": s1to2(); break;
            case "3": s3(); break;
            case "4": s4(); break;
            case "5to6": s5to6(); break;
            default: s7to8(); break;
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

    /** A graph drawn inside a panel; unit coordinates run from -1 to 1 in both directions. */
    private GV panel(G g, double cx, double cy, double w, double h, double[][] unit, double r, double fs, boolean labels) {
        double[][] xy = new double[unit.length][2];
        for (int i = 0; i < unit.length; i++) {
            xy[i][0] = cx + unit[i][0] * w / 2;
            xy[i][1] = cy + unit[i][1] * h / 2;
        }
        return new GV(g, xy, r, fs, labels ? null : blanks(g.n), null, null);
    }

    private static double[][] u(double... v) {
        double[][] r = new double[v.length / 2][2];
        for (int i = 0; i < r.length; i++) {
            r[i][0] = v[2 * i];
            r[i][1] = v[2 * i + 1];
        }
        return r;
    }

    // ── 1-2 ──────────────────────────────────────────────────────────

    private void s1to2() {
        StrokeTextMob title = stroke("Graphs", 0, -420, 104, Colors.WHITE, true);
        mine.add(title);
        play(new Write(title, d(2.0)));
        G g = exampleGraph();
        GV v = new GV(g, exampleXY(0, -110, 250, 230), 40, 34);
        v.build(0.5);
        TextMob who = label("Rupesh Nasre.", 0, 250, 46, Colors.WHITE, false, false);
        TextMob mail = label("rupesh@iitm.ac.in", 0, 308, 34, Colors.BLUE, false, false);
        TextMob when = label("July 2019", 0, 420, 30, Colors.GRAY, false, false);
        mine.add(who);
        mine.add(mail);
        mine.add(when);
        List<Animation> a = new ArrayList<>();
        a.add(new FadeIn(who, d(0.8)));
        a.add(new FadeInAt(mail, 0.3, d(0.8)));
        a.add(new FadeInAt(when, 0.6, d(0.8)));
        playAll(a);
        pause(2.2);
        clearMine();

        head = writeHeading("Graphs are Everywhere!");
        pause(0.5);
        double w = 700, h = 250;
        // a social network
        G soc = new G(7, false);
        soc.add(0, 1).add(0, 2).add(1, 3).add(2, 3).add(2, 4).add(3, 5).add(4, 5).add(5, 6).add(1, 6);
        panel(soc, -430, -250, w, h, u(-0.8, -0.3, -0.3, -0.8, -0.45, 0.3, 0.1, -0.2, -0.1, 0.8, 0.6, 0.5, 0.85, -0.45), 22, 20, false).build(0.6);
        tag("a social network", -430, -92, 28, Colors.LIGHT_GRAY, false);
        say("Friends and followers are nodes joined by edges.", Colors.LIGHT_GRAY);
        pause(1.0);
        // program flow
        G flow = new G(7, true);
        flow.add(0, 1).add(0, 2).add(1, 3).add(2, 3).add(3, 4).add(3, 5).add(4, 6).add(5, 6);
        panel(flow, 430, -250, w, h, u(0, -0.9, -0.5, -0.4, 0.5, -0.4, 0, 0.05, -0.5, 0.5, 0.5, 0.5, 0, 0.9), 16, 14, false).build(0.6);
        tag("the flow of a program", 430, -92, 28, Colors.LIGHT_GRAY, false);
        say("A compiler turns a program into a graph of its basic blocks.", Colors.LIGHT_GRAY);
        pause(1.0);
        // roads
        G road = new G(12, false);
        road.add(0, 1).add(1, 2).add(2, 3).add(3, 4).add(0, 5).add(1, 6).add(2, 6).add(2, 7).add(4, 7).add(4, 8).add(5, 6)
                .add(6, 7).add(7, 8).add(5, 9).add(6, 10).add(7, 10).add(8, 11).add(9, 10).add(10, 11);
        panel(road, -430, 100, w, h, u(-0.9, -0.6, -0.4, -0.8, 0.1, -0.6, 0.7, -0.85, 0.9, -0.2, -0.75, 0.05, -0.2, -0.1, 0.35, 0.1, 0.8, 0.45, -0.5, 0.75, 0.05, 0.7, 0.55, 0.9), 11, 14, false).build(0.6);
        tag("a road network", -430, 258, 28, Colors.LIGHT_GRAY, false);
        say("Cities are nodes, roads are edges.", Colors.LIGHT_GRAY);
        pause(1.0);
        // a big hairball, generated with a fixed seed
        java.util.Random rnd = new java.util.Random(11);
        int n = 38;
        double[][] pos = new double[n][2];
        for (int i = 0; i < n; i++) {
            double ang = rnd.nextDouble() * 2 * Math.PI, rad = Math.sqrt(rnd.nextDouble()) * 0.95;
            pos[i][0] = rad * Math.cos(ang);
            pos[i][1] = rad * Math.sin(ang) * 0.9;
        }
        G big = new G(n, false);
        int guard = 0;
        while (big.e.size() < 70 && guard++ < 4000) {
            int p = rnd.nextInt(n), q = rnd.nextInt(n);
            if (p == q || big.has(p, q)) continue;
            if (Math.hypot(pos[p][0] - pos[q][0], pos[p][1] - pos[q][1]) < 0.7) big.add(p, q);
        }
        panel(big, 430, 100, w, h, pos, 6, 1, false).build(0.8);
        tag("a network of interacting proteins", 430, 258, 28, Colors.LIGHT_GRAY, false);
        say("The same idea scales to thousands of nodes. All of these are graphs.", Colors.GOLD);
        pause(2.8);
    }

    // ── 3 ────────────────────────────────────────────────────────────

    private void s3() {
        head = writeHeading("Graphs");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Most general data structure").kw("Most general", Colors.GOLD));
        s.add(ln(1, "list, stack, queue, tree, ... are special cases."));
        s.add(ln(0, "Hence, less guarantees, more freedom"));
        s.add(ln(0, "Various types"));
        s.add(ln(1, "(un)weighted, (un)directed, bipartite, (a)cyclic, ..."));
        s.add(ln(0, "A graph G consists of:"));
        s.add(ln(1, "A set of nodes / vertices V"));
        s.add(ln(1, "A set of edges E: V → V"));
        keep(writeSlide(s, -380));
        pause(1.0);

        // special cases first
        int m = mark();
        G chain = new G(4, true);
        chain.add(0, 1).add(1, 2).add(2, 3);
        panel(chain, 590, -280, 520, 100, u(-0.9, 0, -0.3, 0, 0.3, 0, 0.9, 0), 24, 22, false).build(0.5);
        tag("a list", 90, -280, 30, Colors.BLUE, true);
        G tr = new G(5, true);
        tr.add(0, 1).add(0, 2).add(1, 3).add(1, 4);
        panel(tr, 590, -120, 520, 190, u(0, -0.8, -0.4, 0, 0.4, 0, -0.7, 0.8, -0.1, 0.8), 24, 22, false).build(0.5);
        tag("a tree", 90, -120, 30, Colors.TEAL, true);
        G any = exampleGraph();
        panel(any, 590, 90, 520, 200, u(-0.7, -0.7, 0, -0.7, 0.7, -0.7, -0.7, 0.7, 0, 0.7, 0.7, 0.7), 24, 22, true).build(0.5);
        tag("a graph", 90, 90, 30, Colors.GOLD, true);
        say("A list, a stack, a queue and a tree are graphs with extra rules.", Colors.LIGHT_GRAY);
        pause(3.0);
        dropSince(m, d(0.9));
        unsay();

        // the definition
        G g = exampleGraph();
        GV v = new GV(g, exampleXY(560, -170, 190, 220), 40, 34);
        v.build(0.5);
        List<Integer> all = new ArrayList<>();
        for (int i = 0; i < g.n; i++) all.add(i);
        TextMob vs = tag("V = {" + join(all, ", ") + "}", 230, 30, 36, Colors.WHITE, true);
        v.lightAll(all, Colors.GOLD);
        say("The nodes: V.", Colors.GOLD);
        pause(1.2);
        v.resetColors();
        StringBuilder es = new StringBuilder("E = {");
        for (int k = 0; k < g.e.size(); k++) es.append(k > 0 ? ", " : "").append("(").append(g.e.get(k)[0]).append(",").append(g.e.get(k)[1]).append(")");
        es.append("}");
        tag(es.toString(), 230, 100, 34, Colors.WHITE, true);
        for (int[] ed : g.e) v.trace(ed[0], ed[1], Colors.GOLD);
        say("The edges: E, here six of them. An edge is a pair of nodes.", Colors.GOLD);
        pause(3.0);
    }

    // ── 4 ────────────────────────────────────────────────────────────

    private void s4() {
        head = writeHeading("Nomenclature");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Real graph may contain arbitrary data type"));
        s.add(ln(2, "(such as person name in a social network)."));
        s.add(ln(0, "It is often mapped to a unique integer while"));
        s.add(ln(3, "representing the graph in memory."));
        s.add(ln(0, "A vertex may have attributes (such as email id)"));
        s.add(ln(0, "An edge may have attributes (such as road"));
        s.add(ln(3, "length). It may be weighted."));
        s.add(ln(0, "An edge may be directed."));
        s.add(ln(1, "e.g., twitter's follow, guide-mentee relation."));
        keep(writeSlide(s, -380));
        pause(1.0);

        // names become integers
        int m = mark();
        String[] names = {"Asha", "Ben", "Chen", "Dev", "Esha", "Farah"};
        G g = exampleGraph();
        GV v = new GV(g, exampleXY(560, -150, 210, 240), 52, 24, names, null, null);
        v.build(0.6);
        say("In a social network the nodes are people, named by strings.", Colors.LIGHT_GRAY);
        pause(1.6);
        List<Animation> a = new ArrayList<>();
        for (int i = 0; i < g.n; i++) {
            TextMob id = label(String.valueOf(i), v.x[i], v.y[i], 36, Colors.WHITE, false, true);
            mine.add(id);
            a.add(new FadeOut(v.tx[i], d(0.7)));
            a.add(new FadeIn(id, d(0.7)));
        }
        playAll(a);
        StringBuilder map = new StringBuilder();
        for (int i = 0; i < g.n; i++) map.append(names[i]).append(" → ").append(i).append(i < g.n - 1 ? "    " : "");
        tag(map.toString(), 560, 120, 28, Colors.GOLD, false);
        say("Inside the program every name is a unique integer: a number is cheap to store and to compare.", Colors.GOLD);
        pause(3.0);
        dropSince(m, d(0.9));
        unsay();

        // an attribute on a vertex, a weight on an edge, a direction
        G e1 = new G(2, false);
        e1.add(0, 1);
        GV a1 = new GV(e1, new double[][]{{400, -170}, {720, -170}}, 52, 30, new String[]{"A", "B"}, null, null);
        a1.build(0.5);
        tag("email: a@iitm.ac.in", 400, -255, 26, Colors.BLUE, false);
        say("A vertex may carry attributes, such as an email id.", Colors.LIGHT_GRAY);
        pause(2.4);
        G e2 = new G(2, false);
        e2.add(0, 1, 4);
        GV a2 = new GV(e2, new double[][]{{400, 30}, {720, 30}}, 52, 30, new String[]{"A", "B"}, null, null);
        a2.build(0.5);
        tag("road length: 4", 560, 112, 26, Colors.RED, false);
        say("An edge may carry an attribute too: a road length makes it weighted.", Colors.LIGHT_GRAY);
        pause(2.4);
        G e3 = new G(2, true);
        e3.add(0, 1);
        GV a3 = new GV(e3, new double[][]{{400, 230}, {720, 230}}, 52, 30, new String[]{"A", "B"}, null, null);
        a3.build(0.5);
        tag("A follows B", 560, 312, 26, Colors.GOLD, false);
        say("An edge may be directed: A follows B does not mean B follows A.", Colors.GOLD);
        pause(3.0);
    }

    // ── 5-6 ──────────────────────────────────────────────────────────

    private void s5to6() {
        head = writeHeading("Relationship");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Graphs are formed due to relationships"));
        s.add(ln(1, "Guide-mentee relation forms academic geneology"));
        s.add(ln(1, "Molecular interactions form a biological network"));
        s.add(ln(1, "Planetary forces form a galactic network"));
        s.add(ln(1, "Friendship relation forms facebook"));
        s.add(ln(1, "Follow relation forms twitter"));
        s.add(ln(1, "In-the-same-iit relation forms alumni network"));
        s.add(ln(1, "Inherits-from relation forms class hierarchy"));
        s.add(ln(1, "City-connected-to relation forms a road network"));
        s.add(ln(1, "..."));
        keep(writeSlide(s, -380));
        pause(1.0);
        int m = mark();
        double w = 330, h = 220;
        // guide-mentee: a directed tree
        G gm = new G(4, true);
        gm.add(0, 1).add(0, 2).add(0, 3);
        panel(gm, 340, -230, w, h, u(0, -0.8, -0.7, 0.7, 0, 0.7, 0.7, 0.7), 24, 20, false).build(0.5);
        tag("guide → mentee", 340, -100, 26, Colors.LIGHT_GRAY, false);
        // friendship: undirected
        G fr = new G(5, false);
        fr.add(0, 1).add(1, 2).add(2, 3).add(3, 4).add(4, 0).add(0, 2);
        panel(fr, 740, -230, w, h, u(0, -0.85, 0.9, -0.2, 0.55, 0.8, -0.55, 0.8, -0.9, -0.2), 22, 20, false).build(0.5);
        tag("friends", 740, -100, 26, Colors.LIGHT_GRAY, false);
        // follow: directed
        G fo = new G(4, true);
        fo.add(0, 1).add(1, 0).add(2, 1).add(3, 1).add(0, 3);
        panel(fo, 340, 70, w, h, u(-0.8, -0.5, 0.6, -0.5, -0.8, 0.7, 0.6, 0.7), 22, 20, false).build(0.5);
        tag("follows", 340, 200, 26, Colors.LIGHT_GRAY, false);
        // class hierarchy: a tree, edges point to the parent class
        G ch = new G(4, true);
        ch.add(1, 0).add(2, 1).add(3, 1);
        panel(ch, 740, 70, w, h, u(0, -0.8, 0, -0.1, -0.6, 0.7, 0.6, 0.7), 22, 20, false).build(0.5);
        tag("inherits from", 740, 200, 26, Colors.LIGHT_GRAY, false);
        say("Each relation has its own shape: a directed tree, an undirected web, mutual follows, a hierarchy.", Colors.GOLD);
        pause(3.2);
        dropSince(m, d(0.8));
        clearMine();

        newHeading("Connection with Discrete Maths");
        List<Spec> t = new ArrayList<>();
        t.add(ln(0, "A symmetric relation can be modeled using"));
        t.add(ln(3, "undirected graphs."));
        t.add(ln(0, "A transitive relation translates to a path."));
        t.add(ln(0, "An equivalence relation leads to a connected"));
        t.add(ln(3, "component."));
        t.add(ln(1, "Every vertex has a path to every other (directly or"));
        t.add(ln(2, "indirectly) related vertex."));
        t.add(ln(0, "An equivalence relation leads to forest of"));
        t.add(ln(3, "connected components."));
        t.add(ln(0, "A poset can be modeled as a DAG (directed"));
        t.add(ln(3, "acyclic graph)."));
        keep(writeSlide(t, -380));
        pause(1.0);

        // symmetric
        m = mark();
        G sy = new G(2, true);
        sy.add(0, 1).add(1, 0);
        GV s1 = new GV(sy, new double[][]{{360, -150}, {760, -150}}, 46, 30, new String[]{"a", "b"}, new double[]{18, 18}, null);
        s1.build(0.5);
        say("Symmetric: if a is related to b, then b is related to a. Two arrows can be one line.", Colors.LIGHT_GRAY);
        pause(2.0);
        G un = new G(2, false);
        un.add(0, 1);
        GV s2 = new GV(un, new double[][]{{360, 80}, {760, 80}}, 46, 30, new String[]{"a", "b"}, null, null);
        s2.build(0.5);
        pause(2.4);
        dropSince(m, d(0.7));
        // transitive
        m = mark();
        G tr = new G(3, true);
        tr.add(0, 1).add(1, 2);
        GV t1 = new GV(tr, new double[][]{{360, -60}, {560, -60}, {760, -60}}, 46, 30, new String[]{"a", "b", "c"}, null, null);
        t1.build(0.5);
        pause(0.6);
        say("Transitive: a to b and b to c give a to c. Following the edges, a path.", Colors.LIGHT_GRAY);
        t1.trace(0, 1, Colors.GOLD);
        t1.trace(1, 2, Colors.GOLD);
        pause(2.6);
        dropSince(m, d(0.7));
        // equivalence
        m = mark();
        G eq = new G(5, false);
        eq.add(0, 1).add(1, 2).add(0, 2).add(3, 4);
        GV q1 = new GV(eq, new double[][]{{350, -110}, {560, -110}, {455, 70}, {740, -30}, {870, 90}}, 40, 28, null, null, null);
        q1.build(0.5);
        say("Equivalence: every node is related to every related node. Each group is a connected component; the groups form a forest.", Colors.LIGHT_GRAY);
        q1.lightAll(java.util.Arrays.asList(0, 1, 2), Colors.BLUE);
        q1.lightAll(java.util.Arrays.asList(3, 4), Colors.PINK);
        pause(3.2);
        dropSince(m, d(0.7));
        // poset: divisibility
        m = mark();
        G po = new G(5, true);
        po.add(0, 1).add(0, 2).add(1, 3).add(2, 3).add(3, 4);
        GV p1 = new GV(po, new double[][]{{360, 60}, {500, -120}, {500, 190}, {680, 60}, {830, 60}}, 40, 28, new String[]{"1", "2", "3", "6", "12"}, null, null);
        p1.build(0.5);
        say("A partial order such as 'divides' on 1, 2, 3, 6, 12 is a DAG: the arrows never lead back.", Colors.LIGHT_GRAY);
        pause(3.4);
    }

    // ── 7-8 ──────────────────────────────────────────────────────────

    private void s7to8() {
        head = writeHeading("By Willie Nelson");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "The slide shows the lyrics of the song").kw("lyrics", Colors.GOLD));
        s.add(ln(3, "\"I'm My Own Grandpa\"."));
        s.add(ln(0, "A man marries a widow; his father marries her"));
        s.add(ln(3, "grown-up daughter."));
        s.add(ln(0, "From then on every family relation twists"));
        s.add(ln(3, "back on itself, until he is his own grandfather."));
        s.add(ln(0, "(The lyrics are not reproduced here.)"));
        keep(writeSlide(s, -300));
        pause(2.4);
        clearMine();

        newHeading("Ithaca, New York, 1822");
        List<Spec> t = new ArrayList<>();
        t.add(ln(0, "Republican Chronicle of Ithaca, New York on April 24, 1822"));
        t.add(ln(1, "\"There was a widow and her daughter-in-law, and a man"));
        t.add(ln(2, "and his son. The widow married the son, and the daughter"));
        t.add(ln(2, "the old man; the widow was, therefore, mother to her"));
        t.add(ln(2, "husband's father, consequently grandmother to her own"));
        t.add(ln(2, "husband. They had a son, to whom she was great-grandmother;"));
        t.add(ln(2, "now, as the son of a great-grandmother must be either a"));
        t.add(ln(2, "grandfather or great-uncle, this boy was therefore his own"));
        t.add(ln(2, "grandfather.\""));
        t.add(ln(0, "Only graphs can help us model such relationships!").kw("graphs", Colors.GOLD));
        keep(writeSlide(t, -380));
        pause(1.4);
        int m = mark();
        G fam = new G(4, true);
        fam.add(0, 1, "mother of").add(1, 2, "wife of").add(2, 3, "father of").add(3, 0, "husband of");
        GV v = new GV(fam, new double[][]{{400, -150}, {820, -150}, {820, 110}, {400, 110}}, 62, 22,
                new String[]{"Widow", "Daughter", "Old man", "Son"}, null, new double[]{-26, -55, -26, -55});
        v.build(0.8);
        say("Nodes are people, edges are relations. Follow the arrows from the widow and you come back to the widow.", Colors.LIGHT_GRAY);
        pause(1.4);
        v.light(0, Colors.GOLD);
        for (int k = 0; k < 4; k++) {
            int[] ed = fam.e.get(k);
            v.trace(ed[0], ed[1], Colors.GOLD);
            v.light(ed[1], Colors.GOLD);
        }
        say("A cycle of relations: a program can follow such chains and tell what is true. Only graphs help here.", Colors.GOLD);
        pause(3.2);
    }
}
