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
 * Clips for slides 20-30 of the graph deck: topological sort, Euler tours, the Hamiltonian cycle, practice.
 *
 *   20     collapsing a cycle, then Kahn's algorithm on a course DAG (in-degrees, queue, order)
 *   21     which drawings can be traced without lifting the pen: degrees counted by the program
 *   22     Koenigsberg as a graph with seven bridges and four odd nodes
 *   23to26  finding an Euler tour by splicing cycles; every cycle is checked against the graph
 *   27     the Hamiltonian cycle found by backtracking, with its dead ends
 *   28     the practice questions, each with a small worked demonstration
 *   29     some more graph problems with small demonstrations
 *   30     learning outcomes and the closing card
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSGraphAlgoScene extends PDSGraphClipBase {

    private final String part;

    public PDSGraphAlgoScene(String part) { this.part = part; }

    @Override
    public void construct() {
        switch (part) {
            case "20": s20(); break;
            case "21": s21(); break;
            case "22": s22(); break;
            case "23to26": s23to26(); break;
            case "27": s27(); break;
            case "28": s28(); break;
            case "29": s29(); break;
            default: s30(); break;
        }
        unsay();
        fadeOutAll(d(1.2), mine);
        if (head != null) fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    // ── helpers ──────────────────────────────────────────────────────

    private static final Color[] PALETTE = {Colors.BLUE, Colors.PINK, Colors.GREEN, Colors.GOLD, Colors.PURPLE, Colors.ORANGE};

    private static double[][] unit(double... v) {
        double[][] r = new double[v.length / 2][2];
        for (int i = 0; i < r.length; i++) {
            r[i][0] = v[2 * i];
            r[i][1] = v[2 * i + 1];
        }
        return r;
    }

    private static double[][] scale(double[][] u, double cx, double cy, double w, double h) {
        double[][] r = new double[u.length][2];
        for (int i = 0; i < u.length; i++) {
            r[i][0] = cx + u[i][0] * w / 2;
            r[i][1] = cy + u[i][1] * h / 2;
        }
        return r;
    }

    private static String[] numbered(int n, int from) {
        String[] r = new String[n];
        for (int i = 0; i < n; i++) r[i] = String.valueOf(i + from);
        return r;
    }

    private static String[] blanks(int n) {
        String[] b = new String[n];
        java.util.Arrays.fill(b, "");
        return b;
    }

    private List<TextMob> lines = new ArrayList<>();

    private TextMob line(String s, double y, double size, Color c) {
        TextMob t = tag(s, -920, y, size, c, true);
        lines.add(t);
        return t;
    }

    private void clearLines() {
        fadeOutAll(d(0.5), new ArrayList<MObject>(lines));
        mine.removeAll(lines);
        lines.clear();
    }

    // ── 20 ───────────────────────────────────────────────────────────

    private void s20() {
        head = writeHeading("Topological Sort");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Given an arbitrary, directed graph,"));
        s.add(ln(3, "collapse cycles."));
        s.add(ln(2, "This leads to a DAG (directed acyclic graph)."));
        s.add(ln(0, "Each DAG exhibits a topological ordering."));
        s.add(ln(1, "All ancestors of a node precede the node."));
        s.add(ln(0, "This is what we did in the last assignment."));
        s.add(ln(0, "Applications:"));
        s.add(ln(1, "OS task dependences"));
        s.add(ln(1, "Course prerequisites"));
        s.add(ln(1, "Job scheduling (in companies, check gantt charts)"));
        s.add(ln(1, "..."));
        keep(writeSlide(s, -380));
        tag("Source: topo.cpp", 560, -400, 26, Colors.GRAY, false);
        tag("Uses Kahn's algorithm.", 560, -350, 30, Colors.GOLD, false);
        pause(1.2);

        // collapsing a cycle
        int m = mark();
        G cy = new G(4, true);
        cy.add(0, 1).add(1, 2).add(2, 0).add(2, 3);
        GV c1 = new GV(cy, new double[][]{{300, -240}, {470, -240}, {385, -120}, {640, -180}}, 36, 28, new String[]{"A", "B", "C", "D"}, null, null);
        c1.build(0.6);
        say("A cycle can never have a first node, so a cycle is first collapsed into one node.", Colors.LIGHT_GRAY);
        pause(0.8);
        c1.trace(0, 1, Colors.GOLD);
        c1.trace(1, 2, Colors.GOLD);
        c1.trace(2, 0, Colors.GOLD);
        pause(1.2);
        dropSince(m, d(0.7));
        G cd = new G(2, true);
        cd.add(0, 1);
        GV c2 = new GV(cd, new double[][]{{385, -180}, {640, -180}}, 46, 24, new String[]{"ABC", "D"}, null, null);
        c2.build(0.6);
        say("What remains is a DAG: the arrows never lead back to a node.", Colors.GREEN);
        pause(2.4);
        dropSince(m, d(0.7));
        unsay();

        // Kahn on a course graph
        String[] names = {"CS1", "Math", "DS", "Algo", "OS", "ML", "Proj"};
        G dg = new G(7, true);
        dg.add(0, 2).add(0, 4).add(1, 3).add(2, 3).add(3, 5).add(4, 6).add(5, 6);
        double[][] xy = {{260, -150}, {260, 60}, {400, -215}, {410, -40}, {570, 10}, {720, -130}, {860, -50}};
        xy[3] = new double[]{400, -60};
        xy[4] = new double[]{570, 10};
        GV v = new GV(dg, xy, 34, 20, names, null, null);
        v.build(0.7);
        int[] indeg = dg.indeg();
        TextMob[] dn = new TextMob[7];
        List<Animation> a = new ArrayList<>();
        for (int i = 0; i < 7; i++) dn[i] = v.noteIn(a, i, String.valueOf(indeg[i]), indeg[i] == 0 ? Colors.GREEN : Colors.RED, 0, -(v.r + 14), 22);
        playAll(a);
        say("The number on each node is its in-degree: how many prerequisites are still unfinished.", Colors.LIGHT_GRAY);
        pause(1.6);
        tag("ready (in-degree 0)", 150, 170, 24, Colors.GRAY, true);
        tag("order so far", 150, 290, 24, Colors.GRAY, true);
        Seq q = new Seq(430, 170, 85, 0, 76, 48, QUEUED_C, 22);
        Seq order = new Seq(430, 290, 78, 0, 70, 48, DONE_C, 22);
        java.util.ArrayDeque<Integer> dq = new java.util.ArrayDeque<>();
        for (int i = 0; i < 7; i++) if (indeg[i] == 0) {
            dq.add(i);
            q.push(names[i], QUEUED_C);
            v.light(i, QUEUED_C);
        }
        say("Kahn's algorithm: take a node with in-degree 0, output it, and remove its outgoing edges.", Colors.LIGHT_GRAY);
        pause(1.2);
        List<Integer> out = new ArrayList<>();
        while (!dq.isEmpty()) {
            int u = dq.poll();
            out.add(u);
            q.popFront();
            v.light(u, CURRENT_C);
            order.push(names[u], DONE_C);
            pause(0.3);
            for (int w : dg.nbrs(u)) {
                v.dimEdge(dg.edgeIndex(u, w), 0.15);
                indeg[w]--;
                dn[w].setText(String.valueOf(indeg[w]));
                if (indeg[w] == 0) {
                    dn[w].setFillColor(Colors.GREEN);
                    dq.add(w);
                    q.push(names[w], QUEUED_C);
                    v.light(w, QUEUED_C);
                }
            }
            v.light(u, DONE_C);
            pause(0.35);
        }
        StringBuilder ord = new StringBuilder();
        for (int i = 0; i < out.size(); i++) ord.append(i > 0 ? ", " : "").append(names[out.get(i)]);
        say("A topological order: " + ord + ". Every course comes after its prerequisites.", Colors.GOLD);
        pause(3.8);
    }

    // ── 21 ───────────────────────────────────────────────────────────

    private void s21() {
        head = writeHeading("Euler Tour");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Draw the following diagrams in your copies"));
        s.add(ln(3, "without retracing and without lifting the pen."));
        s.add(ln(1, "Can you come back to the original point?"));
        s.add(ln(0, "Once we enter a vertex via one edge, we need"));
        s.add(ln(3, "to leave the vertex via another edge."));
        s.add(ln(1, "Hence, each node degree must be even.").kw("even", Colors.GOLD));
        keep(writeSlide(s, -380));
        pause(1.2);
        double[][] sq = unit(-0.7, -0.35, 0.7, -0.35, 0.7, 0.35, -0.7, 0.35, 0, -1, 0, 1);
        G ga = new G(4, false);
        ga.add(0, 1).add(1, 2).add(2, 3).add(3, 0).add(0, 2).add(1, 3);
        G gb = new G(5, false);
        gb.add(0, 1).add(1, 2).add(2, 3).add(3, 0).add(0, 2).add(1, 3).add(4, 0).add(4, 1);
        G gc = new G(6, false);
        gc.add(0, 1).add(1, 2).add(2, 3).add(3, 0).add(0, 2).add(1, 3).add(4, 0).add(4, 1).add(5, 2).add(5, 3);
        G[] gs = {ga, gb, gc};
        double[] cxs = {235, 520, 805};
        GV[] vs = new GV[3];
        for (int k = 0; k < 3; k++) {
            double[][] xy = scale(java.util.Arrays.copyOf(sq, gs[k].n), cxs[k], -110, 240, 260);
            vs[k] = new GV(gs[k], xy, 16, 1, blanks(gs[k].n), null, null);
            vs[k].build(0.5);
        }
        say("Three drawings. Can each be traced in one go, and can the pen come back to where it started?", Colors.LIGHT_GRAY);
        pause(1.6);
        say("Count the edges at every node, its degree.", Colors.LIGHT_GRAY);
        for (int k = 0; k < 3; k++) {
            List<Animation> a = new ArrayList<>();
            int odd = 0;
            for (int i = 0; i < gs[k].n; i++) {
                int dg = gs[k].deg(i);
                if (dg % 2 == 1) odd++;
                vs[k].noteIn(a, i, String.valueOf(dg), dg % 2 == 0 ? Colors.GREEN : Colors.RED, 0, -28, 26);
                vs[k].nodeColor(a, i, dg % 2 == 0 ? Colors.GREEN : Colors.RED, d(0.4));
            }
            playAll(a);
            String verdict = odd == 0 ? "all even: a tour" : odd == 2 ? "2 odd: a path, not a tour" : odd + " odd: no tour";
            tag(verdict, cxs[k], 100, 26, odd == 0 ? Colors.GREEN : Colors.RED, false);
            pause(1.2);
        }
        say("Only the last drawing has every degree even: there the pen can return to its start.", Colors.GOLD);
        pause(3.4);
    }

    // ── 22 ───────────────────────────────────────────────────────────

    private void s22() {
        head = writeHeading("Euler Tour");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "It is proven that even degree is both necessary"));
        s.add(ln(3, "and sufficient criterion for Euler tour."));
        s.add(ln(1, "Also called Eulerian circuit"));
        s.add(ln(1, "Considered to be start of Graph Theory"));
        s.add(ln(0, "The city of Königsberg in Prussia"));
        s.add(ln(3, "(now Kaliningrad, Russia)"));
        s.add(ln(1, "The problem: a walk crossing each of the seven"));
        s.add(ln(2, "bridges exactly once."));
        s.add(ln(1, "In 1736, Euler proved that it was not possible."));
        s.add(ln(1, "Currently, there are five bridges (an Euler path"));
        s.add(ln(2, "is possible, not a tour)."));
        keep(writeSlide(s, -380));
        pause(1.2);
        G g = new G(4, false);
        g.add(0, 1).add(0, 1).add(0, 2).add(0, 2).add(0, 3).add(1, 3).add(2, 3);
        GV v = new GV(g, new double[][]{{480, -110}, {680, -310}, {680, 90}, {880, -110}}, 38, 30,
                new String[]{"A", "B", "C", "D"}, new double[]{-24, 24, 24, -24, 0, 0, 0}, null);
        v.build(0.8);
        say("Euler turned the map into a graph: four land masses are nodes and the seven bridges are edges.", Colors.LIGHT_GRAY);
        pause(2.0);
        List<Animation> a = new ArrayList<>();
        int odd = 0;
        for (int i = 0; i < g.n; i++) {
            int dg = g.e.stream().mapToInt(x -> 0).sum();
            dg = 0;
            for (int[] ed : g.e) {
                if (ed[0] == i) dg++;
                if (ed[1] == i) dg++;
            }
            if (dg % 2 == 1) odd++;
            v.noteIn(a, i, String.valueOf(dg), dg % 2 == 0 ? Colors.GREEN : Colors.RED, 0, -(v.r + 16), 28);
            v.nodeColor(a, i, dg % 2 == 0 ? Colors.GREEN : Colors.RED, d(0.4));
        }
        playAll(a);
        tag(odd + " nodes have an odd degree", 680, 200, 30, Colors.RED, false);
        say("Every node has an odd number of bridges, so no walk can cross each bridge exactly once and return.", Colors.GOLD);
        pause(3.8);
    }

    // ── 23-26 ────────────────────────────────────────────────────────

    private static int[][] EULER_EDGES = {{1, 3}, {1, 4}, {2, 3}, {2, 8}, {3, 4}, {3, 6}, {3, 7}, {3, 9}, {4, 5}, {4, 7}, {4, 10},
            {4, 11}, {5, 10}, {6, 9}, {7, 9}, {7, 10}, {8, 9}, {9, 10}, {9, 12}, {10, 11}, {10, 12}};

    private static String pathText(List<Integer> p) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < p.size(); i++) sb.append(i > 0 ? " " : "").append(p.get(i));
        return sb.toString();
    }

    private void s23to26() {
        head = writeHeading("Finding Euler Tour");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Find a cycle, remove it. Splice the path."));
        s.add(ln(0, "Continue with the vertex having unvisited edge"));
        s.add(ln(3, "in the path."));
        s.add(ln(1, "The remaining graph must be Eulerian."));
        List<List<MObject>> slide = writeSlide(s, -300);
        pause(1.6);
        swipeAway(slide);
        pause(0.4);

        // the graph: nodes are numbered 1..12 as on the slide, the model uses 0..11
        G g = new G(12, false);
        for (int[] e : EULER_EDGES) g.add(e[0] - 1, e[1] - 1);
        double[][] u = unit(0, -1, -0.95, -0.5, -0.2, -0.5, 0.25, -0.5, 0.95, -0.5, -0.5, -0.15, 0.0, -0.2, -0.95, 0.35,
                -0.2, 0.35, 0.25, 0.35, 0.95, 0.35, 0.02, 0.95);
        GV v = new GV(g, scale(u, 0, -120, 1000, 520), 21, 19, numbered(12, 1), null, null);
        v.build(0.8);
        List<Animation> a = new ArrayList<>();
        boolean allEven = true;
        for (int i = 0; i < 12; i++) {
            allEven &= g.deg(i) % 2 == 0;
            v.noteIn(a, i, String.valueOf(g.deg(i)), Colors.GRAY, 0, -30, 18);
        }
        playAll(a);
        say("Every node has an even degree (the small numbers): an Euler tour exists. Start at node 5.", Colors.LIGHT_GRAY);
        if (!allEven) throw new IllegalStateException("the Euler graph must have even degrees");
        pause(2.2);

        int[][] cycles = {{5, 4, 10, 5}, {4, 1, 3, 7, 4, 11, 10, 7, 9, 3, 4}, {3, 2, 8, 9, 6, 3}, {9, 12, 10, 9}};
        int[] expectFirst = {4, 3, 9, -1};
        Color[] cc = {Colors.BLUE, Colors.RED, Colors.GREEN, Colors.PURPLE};
        boolean[] used = new boolean[g.e.size()];
        List<Integer> path = new ArrayList<>();
        for (int step = 0; step < 4; step++) {
            int[] c = cycles[step];
            if (c[0] != c[c.length - 1]) throw new IllegalStateException("a cycle must end where it starts");
            say(step == 0 ? "Find a cycle through 5: here 5 4 10 5." : "Find a cycle that starts at " + c[0] + " and uses only unused edges.", Colors.LIGHT_GRAY);
            line("Cycle: " + pathText(toList(c)), 215, 30, cc[step]);
            for (int i = 0; i + 1 < c.length; i++) {
                int e = g.edgeIndex(c[i] - 1, c[i + 1] - 1);
                if (e < 0 || used[e]) throw new IllegalStateException("edge " + c[i] + "-" + c[i + 1] + " missing or used twice");
                used[e] = true;
                v.light(c[i] - 1, cc[step]);
                v.trace(c[i] - 1, c[i + 1] - 1, cc[step]);
            }
            v.light(c[c.length - 1] - 1, cc[step]);
            pause(0.6);
            // splice the cycle into the path at its first vertex
            if (path.isEmpty()) {
                for (int x : c) path.add(x);
            } else {
                int at = path.indexOf(c[0]);
                List<Integer> np = new ArrayList<>(path.subList(0, at));
                for (int x : c) np.add(x);
                np.addAll(path.subList(at + 1, path.size()));
                path = np;
            }
            line("Path: " + pathText(path), 265, 30, Colors.WHITE);
            int first = -1;
            for (int x : path) {
                boolean has = false;
                for (int e = 0; e < g.e.size(); e++) if (!used[e] && (g.e.get(e)[0] == x - 1 || g.e.get(e)[1] == x - 1)) has = true;
                if (has) {
                    first = x;
                    break;
                }
            }
            if (first != expectFirst[step]) throw new IllegalStateException("expected " + expectFirst[step] + " but found " + first);
            if (first > 0) {
                line("First vertex with unvisited edges = " + first, 315, 30, Colors.GOLD);
                say("Continue from " + first + ": it still has unvisited edges.", Colors.GOLD);
            } else {
                line("This is the Euler tour. Runs in O(|E| + |V|) time.", 315, 30, Colors.GOLD);
                say("No unvisited edge is left: all " + g.e.size() + " edges are in the path, an Euler tour in O(|E| + |V|) time.", Colors.GREEN);
            }
            pause(step == 3 ? 4.0 : 2.4);
            if (step < 3) clearLines();
        }
    }

    private static List<Integer> toList(int[] a) {
        List<Integer> l = new ArrayList<>();
        for (int x : a) l.add(x);
        return l;
    }

    // ── 27 ───────────────────────────────────────────────────────────

    private void search(G g, int u, boolean[] in, List<Integer> cur, List<String> ev, boolean[] found) {
        if (found[0]) return;
        if (cur.size() == g.n) {
            if (g.has(u, cur.get(0))) {
                ev.add("close");
                found[0] = true;
            }
            return;
        }
        for (int w : g.nbrs(u)) {
            if (in[w]) continue;
            in[w] = true;
            cur.add(w);
            ev.add("go " + w);
            search(g, w, in, cur, ev, found);
            if (found[0]) return;
            cur.remove(cur.size() - 1);
            in[w] = false;
            ev.add("back " + w);
        }
    }

    private void s27() {
        head = writeHeading("Hamiltonian Cycle");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Find a tour such that each node is visited"));
        s.add(ln(3, "exactly once, and all the nodes are covered."));
        s.add(ln(1, "Have we seen such a problem before?"));
        s.add(ln(1, "Unlike Euler tour, this problem is hard on general").kw("hard", Colors.RED));
        s.add(ln(2, "graphs."));
        keep(writeSlide(s, -380));
        pause(1.2);
        G g = new G(6, false);
        g.add(0, 1).add(0, 2).add(1, 2).add(1, 3).add(2, 4).add(3, 4).add(3, 5).add(4, 5);
        GV v = new GV(g, new double[][]{{330, -60}, {500, -220}, {500, 100}, {700, -220}, {700, 100}, {870, -60}}, 34, 28);
        v.build(0.7);
        say("Try to build a tour from node 0 that visits every node once and returns to 0. Wrong turns are undone.", Colors.LIGHT_GRAY);
        pause(1.2);
        TextMob pathTxt = tag("path: 0", 600, 220, 32, Colors.WHITE, false);
        List<String> ev = new ArrayList<>();
        boolean[] in = new boolean[g.n];
        in[0] = true;
        List<Integer> cur = new ArrayList<>();
        cur.add(0);
        boolean[] found = new boolean[1];
        search(g, 0, in, cur, ev, found);
        if (!found[0]) throw new IllegalStateException("the graph must have a Hamiltonian cycle");
        v.light(0, Colors.GOLD);
        java.util.ArrayDeque<Link> traces = new java.util.ArrayDeque<>();
        List<Integer> walk = new ArrayList<>();
        walk.add(0);
        for (String e : ev) {
            if (e.startsWith("go")) {
                int w = Integer.parseInt(e.substring(3));
                int from = walk.get(walk.size() - 1);
                traces.push(v.trace(from, w, Colors.ORANGE));
                v.light(w, Colors.ORANGE);
                walk.add(w);
            } else if (e.startsWith("back")) {
                int w = Integer.parseInt(e.substring(5));
                v.untrace(traces.pop());
                v.light(w, NODE_C);
                walk.remove(walk.size() - 1);
                say("Dead end at " + w + ": go back and try another neighbour.", Colors.RED);
            } else {
                traces.push(v.trace(walk.get(walk.size() - 1), 0, Colors.GREEN));
                for (int x : walk) v.light(x, Colors.GREEN);
                say("Every node is visited once and the last one is next to 0: a Hamiltonian cycle.", Colors.GREEN);
            }
            pathTxt.setText("path: " + join(walk, " → "));
            pause(0.55);
        }
        pause(1.6);
        say("Seen before: the travelling salesman asks for the cheapest such tour. No fast method is known for general graphs.", Colors.GOLD);
        pause(4.4);
    }

    // ── 28 ───────────────────────────────────────────────────────────

    private void s28() {
        head = writeHeading("Practice Questions");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Given an unweighted graph and two vertices u, v,"));
        s.add(ln(3, "there could be multiple paths between u and v."));
        s.add(ln(2, "What is the shortest distance between them?"));
        s.add(ln(0, "Given a weighted graph with weight values 1 or 2,"));
        s.add(ln(3, "find the shortest distance between two vertices."));
        s.add(ln(0, "Find if a node is a part of a 5-clique."));
        s.add(ln(0, "Find if a node is a part of a 5-cycle."));
        s.add(ln(0, "Find an edge, deleting which partitions the graph"));
        s.add(ln(3, "into two disconnected subgraphs, if it exists."));
        s.add(ln(0, "If A is an adjacency matrix, what does A^k mean?"));
        List<List<MObject>> slide = writeSlide(s, -380);
        pause(2.0);
        swipeAway(slide);
        pause(0.4);
        say("A hint for each, worked on the example graph.", Colors.LIGHT_GRAY);
        pause(1.0);
        unsay();

        G g = exampleGraph();
        // Q1: shortest distance by BFS
        int m = mark();
        tag("Shortest distance between 5 and 2", 0, -380, 34, Colors.GOLD, false);
        GV v1 = new GV(g, exampleXY(0, -50, 340, 280), 44, 36);
        v1.build(0.6);
        int[] par = new int[6];
        java.util.Arrays.fill(par, -1);
        int[] dist = new int[6];
        java.util.Arrays.fill(dist, -1);
        java.util.ArrayDeque<Integer> q = new java.util.ArrayDeque<>();
        dist[5] = 0;
        q.add(5);
        while (!q.isEmpty()) {
            int x = q.poll();
            for (int y : g.nbrs(x)) if (dist[y] < 0) {
                dist[y] = dist[x] + 1;
                par[y] = x;
                q.add(y);
            }
        }
        List<Integer> sp = new ArrayList<>();
        for (int x = 2; x != -1; x = par[x]) sp.add(0, x);
        v1.light(5, Colors.GOLD);
        for (int i = 1; i < sp.size(); i++) {
            v1.trace(sp.get(i - 1), sp.get(i), Colors.GOLD);
            v1.light(sp.get(i), Colors.GOLD);
        }
        tag("BFS from 5 reaches 2 on level " + dist[2] + ": " + join(sp, " → "), 0, 290, 30, Colors.GOLD, false);
        say("Idea: BFS visits nodes in order of distance, so the level of v is the shortest distance.", Colors.LIGHT_GRAY);
        pause(3.4);
        dropSince(m, d(0.7));

        // Q2: weights 1 or 2
        m = mark();
        tag("Weights 1 or 2", 0, -380, 34, Colors.GOLD, false);
        G w = new G(3, false);
        w.add(0, 1, 2).add(1, 2, 1).add(0, 2, 2);
        GV v2 = new GV(w, new double[][]{{-380, -100}, {-120, -100}, {-250, 90}}, 40, 30);
        v2.build(0.6);
        say("Idea: replace every weight-2 edge by two weight-1 edges through a new node, then run plain BFS.", Colors.LIGHT_GRAY);
        pause(1.8);
        G w2 = new G(5, false);
        w2.add(0, 3).add(3, 1).add(1, 2).add(0, 4).add(4, 2);
        GV v3 = new GV(w2, new double[][]{{240, -100}, {600, -100}, {420, 90}, {420, -100}, {330, 0}}, 40, 30,
                new String[]{"0", "1", "2", "n", "n"}, null, null);
        v3.light(0, NODE_C);
        v3.build(0.6);
        v3.light(3, Colors.GRAY);
        v3.light(4, Colors.GRAY);
        int[] d2 = new int[5];
        java.util.Arrays.fill(d2, -1);
        d2[0] = 0;
        q.clear();
        q.add(0);
        while (!q.isEmpty()) {
            int x = q.poll();
            for (int y : w2.nbrs(x)) if (d2[y] < 0) {
                d2[y] = d2[x] + 1;
                q.add(y);
            }
        }
        tag("BFS distance from 0 to 2 = " + d2[2], 420, 250, 30, Colors.GOLD, false);
        pause(3.4);
        dropSince(m, d(0.7));

        // Q3 + Q4: 5-clique and 5-cycle
        m = mark();
        tag("Is a node in a 5-clique or a 5-cycle?", 0, -380, 34, Colors.GOLD, false);
        GV v4 = new GV(g, exampleXY(0, -50, 340, 280), 44, 36);
        v4.build(0.6);
        int maxDeg = 0;
        for (int i = 0; i < 6; i++) maxDeg = Math.max(maxDeg, g.deg(i));
        say("A 5-clique needs at least 4 neighbours per node: the largest degree here is " + maxDeg + ", so there is none.", Colors.LIGHT_GRAY);
        pause(3.0);
        // depth-limited search for a cycle of length 5 through node 0
        List<Integer> cyc = new ArrayList<>();
        cyc.add(0);
        boolean ok = cycle5(g, 0, 0, cyc);
        say("A 5-cycle: look for a path of 5 nodes that returns to the start (depth-limited DFS).", Colors.LIGHT_GRAY);
        pause(1.0);
        if (ok) {
            for (int i = 0; i < cyc.size(); i++) {
                int a = cyc.get(i), b = cyc.get((i + 1) % cyc.size());
                v4.light(a, Colors.GOLD);
                v4.trace(a, b, Colors.GOLD);
            }
            tag("5-cycle through node 0: " + join(cyc, " → ") + " → 0", 0, 290, 30, Colors.GOLD, false);
        }
        pause(3.4);
        dropSince(m, d(0.7));

        // Q5: a bridge
        m = mark();
        tag("An edge whose deletion disconnects the graph", 0, -380, 34, Colors.GOLD, false);
        GV v5 = new GV(g, exampleXY(0, -50, 340, 280), 44, 36);
        v5.build(0.6);
        say("Idea: delete each edge in turn and test connectivity (or use one DFS with low-link values).", Colors.LIGHT_GRAY);
        pause(1.6);
        for (int k = 0; k < g.e.size(); k++) {
            int[] ed = g.e.get(k);
            boolean disc = !connectedWithout(g, k);
            Link l = v5.trace(ed[0], ed[1], disc ? Colors.RED : Colors.GREEN);
            if (!disc) {
                pause(0.35);
                v5.untrace(l);
            } else {
                tag("deleting (" + ed[0] + ", " + ed[1] + ") leaves node " + (ed[0] == 2 ? ed[0] : ed[1]) + " alone", 0, 290, 30, Colors.RED, false);
            }
        }
        say("Deleting (2, 4) cuts node 2 off: it is such an edge. Every other edge lies on a cycle.", Colors.GOLD);
        pause(3.6);
        dropSince(m, d(0.7));

        // Q6: A^k
        m = mark();
        tag("What does A^2 mean?", 0, -380, 34, Colors.GOLD, false);
        int n = g.n;
        int[][] A = new int[n][n];
        for (int[] ed : g.e) {
            A[ed[0]][ed[1]] = 1;
            A[ed[1]][ed[0]] = 1;
        }
        int[][] A2 = new int[n][n];
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) for (int k = 0; k < n; k++) A2[i][j] += A[i][k] * A[k][j];
        String[][] cells = new String[n][n];
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) cells[i][j] = String.valueOf(A2[i][j]);
        Grid gr = new Grid(cells, -170, -250, 68, 58, Colors.BLUE, 30);
        gr.headers(numbered(n, 0), numbered(n, 0), 26, Colors.GRAY);
        List<Animation> a = new ArrayList<>();
        gr.show(a, d(0.7));
        playAll(a);
        say("(A²)[i][j] counts the walks of length 2 from i to j; A^k counts the walks of length k.", Colors.LIGHT_GRAY);
        pause(2.4);
        a = new ArrayList<>();
        for (int i = 0; i < n; i++) gr.light(a, i, i, Colors.GOLD, d(0.4));
        playAll(a);
        say("The diagonal counts the walks that go out and come back: the degrees " + degrees(g) + ".", Colors.GOLD);
        pause(3.6);
    }

    private static String degrees(G g) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < g.n; i++) sb.append(i > 0 ? ", " : "").append(g.deg(i));
        return sb.toString();
    }

    private boolean cycle5(G g, int start, int u, List<Integer> cur) {
        if (cur.size() == 5) return g.has(u, start);
        for (int w : g.nbrs(u)) {
            if (cur.contains(w)) continue;
            cur.add(w);
            if (cycle5(g, start, w, cur)) return true;
            cur.remove(cur.size() - 1);
        }
        return false;
    }

    private boolean connectedWithout(G g, int skip) {
        boolean[] seen = new boolean[g.n];
        java.util.ArrayDeque<Integer> st = new java.util.ArrayDeque<>();
        st.push(0);
        seen[0] = true;
        int cnt = 1;
        while (!st.isEmpty()) {
            int x = st.pop();
            for (int k = 0; k < g.e.size(); k++) {
                if (k == skip) continue;
                int[] ed = g.e.get(k);
                int y = ed[0] == x ? ed[1] : ed[1] == x ? ed[0] : -1;
                if (y >= 0 && !seen[y]) {
                    seen[y] = true;
                    cnt++;
                    st.push(y);
                }
            }
        }
        return cnt == g.n;
    }

    // ── 29 ───────────────────────────────────────────────────────────

    private void s29() {
        head = writeHeading("Some Graph Problems");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Given a node, find another node having the same"));
        s.add(ln(3, "neighborhood, if one exists."));
        s.add(ln(1, "Approximate similarity"));
        s.add(ln(1, "Bipartite graphs (document-terms, course preferences)"));
        s.add(ln(0, "Find if a graph is uniform or skewed."));
        s.add(ln(1, "Road networks, social networks"));
        s.add(ln(0, "Simulate nslookup."));
        s.add(ln(1, "Network of routers; store tables."));
        s.add(ln(0, "Graph coloring"));
        List<List<MObject>> slide = writeSlide(s, -380);
        pause(2.0);
        swipeAway(slide);
        pause(0.4);

        // same neighbourhood in a bipartite graph
        int m = mark();
        tag("Documents and the terms they contain", 0, -380, 34, Colors.GOLD, false);
        String[] nm = {"D1", "D2", "D3", "t1", "t2", "t3"};
        G b = new G(6, false);
        b.add(0, 3).add(0, 4).add(1, 3).add(1, 4).add(2, 4).add(2, 5);
        GV v = new GV(b, new double[][]{{-250, -250}, {-250, -60}, {-250, 130}, {250, -250}, {250, -60}, {250, 130}}, 44, 30, nm, null, null);
        v.build(0.7);
        say("Which node has the same neighbourhood as D1? Compare the neighbour sets.", Colors.LIGHT_GRAY);
        pause(1.4);
        int twin = -1;
        for (int j = 1; j < 3; j++) if (b.nbrs(0).equals(b.nbrs(j))) twin = j;
        v.light(0, Colors.GOLD);
        if (twin >= 0) {
            v.light(twin, Colors.GOLD);
            for (int w : b.nbrs(0)) {
                v.trace(0, w, Colors.GOLD);
                v.trace(twin, w, Colors.GOLD);
            }
            tag("D1 and D" + (twin + 1) + " both contain t1 and t2: the same neighbourhood", 0, 290, 30, Colors.GOLD, false);
        }
        pause(2.4);
        say("A node that shares most (not all) of its neighbours is an approximate match; Jaccard overlap measures it.", Colors.LIGHT_GRAY);
        double jac = 0;
        java.util.Set<Integer> s0 = new java.util.HashSet<>(b.nbrs(0)), s2 = new java.util.HashSet<>(b.nbrs(2));
        java.util.Set<Integer> inter = new java.util.HashSet<>(s0);
        inter.retainAll(s2);
        java.util.Set<Integer> uni = new java.util.HashSet<>(s0);
        uni.addAll(s2);
        jac = inter.size() / (double) uni.size();
        tag(String.format("D1 and D3 share %d of %d terms: overlap %.2f", inter.size(), uni.size(), jac), 0, 340, 28, Colors.GRAY, false);
        pause(3.4);
        dropSince(m, d(0.7));

        // uniform or skewed: the degree histogram
        m = mark();
        tag("Uniform or skewed?", 0, -380, 34, Colors.GOLD, false);
        G ring = new G(8, false);
        for (int i = 0; i < 8; i++) ring.add(i, (i + 1) % 8).add(i, (i + 2) % 8);
        G star = new G(8, false);
        for (int i = 1; i < 8; i++) star.add(0, i);
        star.add(1, 2).add(3, 4);
        GV r1 = new GV(ring, ringXY(-420, -150, 130), 22, 1, blanks(8), null, null);
        r1.build(0.6);
        GV r2 = new GV(star, ringXYStar(420, -150, 130), 22, 1, blanks(8), null, null);
        r2.build(0.6);
        tag("road-like: every degree the same", -420, 20, 26, Colors.LIGHT_GRAY, false);
        tag("social-like: one hub, many small", 420, 20, 26, Colors.LIGHT_GRAY, false);
        int[] h1 = histogram(ring), h2 = histogram(star);
        mine.addAll(bars(h1, -420, 270, 20, 40, Colors.BLUE, "number of nodes of each degree"));
        mine.addAll(bars(h2, 420, 270, 20, 40, Colors.PINK, "number of nodes of each degree"));
        say("Count how many nodes have each degree: one tall bar is uniform, a long thin tail is skewed.", Colors.GOLD);
        pause(4.0);
        dropSince(m, d(0.7));

        // nslookup as a routing table
        m = mark();
        tag("A network of routers: R1's table", 0, -380, 34, Colors.GOLD, false);
        G net = new G(6, false);
        net.add(0, 1).add(0, 2).add(1, 3).add(2, 3).add(3, 4).add(3, 5);
        String[] rn = {"R1", "R2", "R3", "R4", "R5", "R6"};
        GV v6 = new GV(net, new double[][]{{-380, -130}, {-200, -230}, {-200, -30}, {20, -130}, {220, -230}, {220, -30}}, 38, 26, rn, null, null);
        v6.build(0.7);
        int[] par = new int[6];
        java.util.Arrays.fill(par, -1);
        int[] dist = new int[6];
        java.util.Arrays.fill(dist, -1);
        java.util.ArrayDeque<Integer> q = new java.util.ArrayDeque<>();
        dist[0] = 0;
        q.add(0);
        while (!q.isEmpty()) {
            int x = q.poll();
            for (int y : net.nbrs(x)) if (dist[y] < 0) {
                dist[y] = dist[x] + 1;
                par[y] = x;
                q.add(y);
            }
        }
        String[][] tab = new String[6][2];
        tab[0][0] = "R1";
        tab[0][1] = "-";
        for (int t = 1; t < 6; t++) {
            int hop = t;
            while (par[hop] != 0) hop = par[hop];
            tab[t][0] = rn[t];
            tab[t][1] = rn[hop];
        }
        Grid gt = new Grid(tab, 450, -230, 120, 46, Colors.BLUE, 24);
        gt.headers(new String[]{"to", "send via"}, null, 22, Colors.GRAY);
        List<Animation> a = new ArrayList<>();
        gt.show(a, d(0.7));
        playAll(a);
        say("Each router keeps a table: for every destination, the neighbour to forward to, found by BFS from the router.", Colors.LIGHT_GRAY);
        pause(3.6);
        dropSince(m, d(0.7));

        // colouring
        m = mark();
        tag("Graph colouring", 0, -380, 34, Colors.GOLD, false);
        G g = exampleGraph();
        GV v7 = new GV(g, exampleXY(0, -50, 340, 280), 44, 36);
        v7.build(0.6);
        int[] color = new int[6];
        java.util.Arrays.fill(color, -1);
        for (int i = 0; i < 6; i++) {
            boolean[] taken = new boolean[6];
            for (int w : g.nbrs(i)) if (color[w] >= 0) taken[color[w]] = true;
            int c = 0;
            while (taken[c]) c++;
            color[i] = c;
            v7.light(i, PALETTE[c]);
            pause(0.5);
        }
        int used = 0;
        for (int c : color) used = Math.max(used, c + 1);
        say("Give each node the first colour none of its neighbours has: adjacent nodes never share one. Here " + used + " colours.", Colors.GOLD);
        pause(3.8);
    }

    private static double[][] ringXY(double cx, double cy, double r) {
        double[][] xy = new double[8][2];
        for (int i = 0; i < 8; i++) {
            xy[i][0] = cx + r * Math.cos(2 * Math.PI * i / 8 - Math.PI / 2);
            xy[i][1] = cy + r * Math.sin(2 * Math.PI * i / 8 - Math.PI / 2);
        }
        return xy;
    }

    private static double[][] ringXYStar(double cx, double cy, double r) {
        double[][] xy = new double[8][2];
        xy[0] = new double[]{cx, cy};
        for (int i = 1; i < 8; i++) {
            xy[i][0] = cx + r * Math.cos(2 * Math.PI * (i - 1) / 7 - Math.PI / 2);
            xy[i][1] = cy + r * Math.sin(2 * Math.PI * (i - 1) / 7 - Math.PI / 2);
        }
        return xy;
    }

    private static int[] histogram(G g) {
        int max = 0;
        for (int i = 0; i < g.n; i++) max = Math.max(max, g.deg(i));
        int[] h = new int[max + 1];
        for (int i = 0; i < g.n; i++) h[g.deg(i)]++;
        return h;
    }

    // ── 30 ───────────────────────────────────────────────────────────

    private void s30() {
        head = writeHeading("Learning Outcomes");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Represent graphs in memory").kw("Represent", Colors.RED));
        s.add(ln(0, "Traverse graphs").kw("Traverse", Colors.RED));
        s.add(ln(0, "Solve simple graph problems").kw("Solve", Colors.RED));
        keep(writeSlide(s, -300));
        pause(1.0);
        // a small picture for each outcome
        G g = exampleGraph();
        GV v = new GV(g, exampleXY(620, -240, 150, 110), 22, 18);
        v.build(0.4);
        String[][] cells = new String[6][6];
        for (int u = 0; u < 6; u++) for (int w : g.nbrs(u)) cells[u][w] = "1";
        Grid mx = new Grid(cells, 480, -110, 38, 30, Colors.BLUE, 18);
        List<Animation> a = new ArrayList<>();
        mx.show(a, d(0.6));
        playAll(a);
        pause(1.0);
        v.light(5, QUEUED_C);
        for (int w : g.nbrs(5)) {
            v.trace(5, w, Colors.GOLD);
            v.light(w, QUEUED_C);
        }
        pause(1.6);
        clearMine();
        fadeOutAll(d(0.9), head);
        head = null;
        madeBy();
    }

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
        G g = exampleGraph();
        GV v = new GV(g, exampleXY(0, 210, 120, 90), 20, 16);
        v.build(0.5);
        TextMob who = label("CS5013 · LecViz", 0, 400, 30, Colors.GRAY, false, false);
        mine.add(who);
        play(new FadeIn(who, d(0.8)));
        pause(3.4);
    }
}
