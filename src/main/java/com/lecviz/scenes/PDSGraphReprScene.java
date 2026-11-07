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
 * Clips for slides 9-13 of the graph deck: how a graph sits in memory and what can be asked of it.
 *
 *   9   adjacency matrix and adjacency list, both filled from the model of the example graph
 *   10  edge list (COO) and compressed sparse row (CSR)
 *   11  the classwork: a weighted directed graph in all four representations
 *   12  the primitive operations, their cost on a matrix and on a list
 *   13  high-level operations built from them: path, connectivity, cycle, bipartite check, triangles
 *
 * Every table is computed from the graph. The deck's own figures leave some entries out (the edge 4-5 in the
 * lists, 4-0 in the edge list): the clips store each undirected edge from both ends and say so on screen.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSGraphReprScene extends PDSGraphClipBase {

    private final String part;

    public PDSGraphReprScene(String part) { this.part = part; }

    @Override
    public void construct() {
        switch (part) {
            case "9": s9(); break;
            case "10": s10(); break;
            case "11": s11(); break;
            case "12": s12(); break;
            default: s13(); break;
        }
        unsay();
        fadeOutAll(d(1.2), mine);
        if (head != null) fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    // ── helpers ──────────────────────────────────────────────────────

    private static final Color[] PALETTE = {Colors.BLUE, Colors.PINK, Colors.GREEN, Colors.GOLD, Colors.PURPLE, Colors.ORANGE};

    private static String[] range(int n) {
        String[] r = new String[n];
        for (int i = 0; i < n; i++) r[i] = String.valueOf(i);
        return r;
    }

    private void run(List<Animation> a) { playAll(a); }

    private void lightCell(Grid g, int r, int c, Color col, double dur) {
        List<Animation> a = new ArrayList<>();
        g.light(a, r, c, col, dur);
        playAll(a);
    }

    // ── 9 ────────────────────────────────────────────────────────────

    private void s9() {
        head = writeHeading("Graph Representation");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "1. Adjacency matrix"));
        s.add(ln(1, "|V| x |V| matrix"));
        s.add(ln(1, "Each entry [i, j] denotes if edge (i,j) is"));
        s.add(ln(2, "present in G"));
        s.add(ln(1, "Useful for dense graph").kw("dense", Colors.RED));
        s.add(ln(1, "Finding neighbors is O(|V|)"));
        s.add(ln(0, "2. Adjacency list"));
        s.add(ln(1, "|V| + |E| size"));
        s.add(ln(1, "Each vertex i has a list of its neighbors"));
        s.add(ln(1, "Useful for sparse graphs").kw("sparse", Colors.RED));
        s.add(ln(1, "Finding neighbors is O(max. degree)"));
        keep(writeSlide(s, -380));
        pause(1.0);

        G g = exampleGraph();
        int n = g.n;
        GV v = new GV(g, exampleXY(620, -300, 140, 120), 30, 26);
        v.build(0.5);
        say("The graph to store in memory: six nodes and six undirected edges.", Colors.LIGHT_GRAY);
        pause(1.4);

        // the adjacency matrix
        int mm = mark();
        String[] hd = range(n);
        Grid mx = new Grid(new String[n][n], 422, -170, 66, 52, Colors.BLUE, 30);
        mx.headers(hd, hd, 26, Colors.GRAY);
        List<Animation> a = new ArrayList<>();
        mx.show(a, d(0.6));
        run(a);
        say("Row i has a 1 in column j when there is an edge from i to j.", Colors.LIGHT_GRAY);
        for (int u = 0; u < n; u++) {
            v.light(u, Colors.GOLD);
            a = new ArrayList<>();
            for (int w : g.nbrs(u)) {
                mx.put(a, u, w, "1", d(0.4));
                mx.light(a, u, w, Colors.GOLD, d(0.3));
            }
            run(a);
            pause(0.2);
            v.light(u, NODE_C);
        }
        a = new ArrayList<>();
        mx.plain(a, Colors.BLUE, d(0.4));
        run(a);
        say("An undirected edge is stored twice, as (i,j) and (j,i): the matrix is symmetric.", Colors.GOLD);
        tag("The slide's figure leaves out (4,5); it is added here.", 620, 205, 24, Colors.GRAY, false);
        pause(2.4);
        say("findNeighbors(4): scan the whole row, all |V| = 6 cells.", Colors.LIGHT_GRAY);
        v.light(4, Colors.ORANGE);
        for (int j = 0; j < n; j++) lightCell(mx, 4, j, g.has(4, j) ? Colors.GREEN : Colors.ORANGE, d(0.25));
        tag("6 cells checked, 3 neighbours: " + join(g.nbrs(4), ", "), 620, 250, 28, Colors.GREEN, false);
        pause(2.0);
        v.resetColors();
        dropSince(mm, d(0.8));

        // the adjacency list
        mm = mark();
        CH ch = new CH(n, 430, -170, 58);
        a = new ArrayList<>();
        ch.show(a, d(0.6));
        run(a);
        say("The list: each vertex keeps just its neighbours.", Colors.LIGHT_GRAY);
        for (int u = 0; u < n; u++) {
            v.light(u, Colors.GOLD);
            for (int w : g.nbrs(u)) ch.add(w, u, Colors.BLUE);
            v.light(u, NODE_C);
        }
        mine.addAll(ch.parts());
        tag("The slide's lists leave out 5 in 4's list; it is added here.", 620, 250, 22, Colors.GRAY, false);
        pause(1.0);
        say("findNeighbors(4): walk 4's list only: as many steps as 4 has neighbours.", Colors.LIGHT_GRAY);
        ch.lightBucket(4, Colors.ORANGE);
        v.light(4, Colors.ORANGE);
        for (int k = 0; k < ch.nodes.get(4).size(); k++) {
            ch.lightNode(4, k, Colors.GREEN);
            pause(0.3);
        }
        pause(1.2);
        int pairs = g.pairs().size();
        tag("matrix: |V|² = " + n * n + " cells", 620, 300, 26, Colors.GOLD, false);
        tag("list: |V| + 2|E| = " + n + " + " + pairs + " = " + (n + pairs) + " entries", 620, 335, 26, Colors.GOLD, false);
        say("A matrix suits dense graphs, where most cells are 1; a list suits sparse graphs.", Colors.GOLD);
        pause(3.4);
    }

    // ── 10 ───────────────────────────────────────────────────────────

    private void s10() {
        head = writeHeading("Graph Representation");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "3. Edge list / Coordinate list (COO)"));
        s.add(ln(1, "|E| pairs"));
        s.add(ln(1, "Useful for edge-based algorithms"));
        s.add(ln(1, "Typically sorted on vertex id"));
        s.add(ln(0, "4. Compressed sparse row (CSR)"));
        s.add(ln(1, "Concatenated adjacency lists"));
        s.add(ln(1, "Useful for sparse graphs").kw("sparse", Colors.RED));
        s.add(ln(1, "Useful for data transfer"));
        keep(writeSlide(s, -380));
        pause(1.0);

        G g = exampleGraph();
        int n = g.n;
        List<int[]> pr = g.pairs();
        int m = pr.size();
        // COO
        Grid coo = new Grid(new String[2][m], 300, -330, 52, 54, Colors.BLUE, 26);
        coo.headers(null, new String[]{"u", "v"}, 24, Colors.GRAY);
        List<Animation> a = new ArrayList<>();
        coo.show(a, d(0.6));
        run(a);
        say("Edge list: one (u, v) pair per edge, sorted by u. Each undirected edge gives two pairs.", Colors.LIGHT_GRAY);
        for (int k = 0; k < m; k++) {
            a = new ArrayList<>();
            coo.put(a, 0, k, String.valueOf(pr.get(k)[0]), d(0.25));
            coo.put(a, 1, k, String.valueOf(pr.get(k)[1]), d(0.25));
            coo.light(a, 0, k, Colors.GOLD, d(0.2));
            coo.light(a, 1, k, Colors.GOLD, d(0.2));
            run(a);
        }
        a = new ArrayList<>();
        coo.plain(a, Colors.BLUE, d(0.4));
        run(a);
        tag(m + " pairs for " + g.e.size() + " undirected edges", 612, -190, 28, Colors.GOLD, false);
        tag("The slide lists 10 pairs; (4,0) and (4,5) are added here.", 612, -152, 22, Colors.GRAY, false);
        pause(2.2);

        // CSR
        int[] ptr = new int[n + 1];
        for (int u = 0; u < n; u++) ptr[u + 1] = ptr[u] + g.deg(u);
        List<Integer> col = new ArrayList<>();
        for (int u = 0; u < n; u++) col.addAll(g.nbrs(u));
        String[] ph = new String[n + 1];
        for (int i = 0; i < n; i++) ph[i] = String.valueOf(i);
        ph[n] = "end";
        Grid pg = new Grid(new String[1][n + 1], 300, -20, 52, 54, Colors.ORANGE, 26);
        pg.headers(ph, new String[]{"ptr"}, 20, Colors.GRAY);
        Grid cg = new Grid(new String[1][m], 300, 170, 52, 54, Colors.ORANGE, 26);
        cg.headers(null, new String[]{"col"}, 24, Colors.GRAY);
        a = new ArrayList<>();
        pg.show(a, d(0.6));
        cg.show(a, d(0.6));
        run(a);
        say("CSR: write all the adjacency lists one after another in col; ptr[i] says where list i starts.", Colors.LIGHT_GRAY);
        for (int i = 0; i <= n; i++) {
            a = new ArrayList<>();
            pg.put(a, 0, i, String.valueOf(ptr[i]), d(0.3));
            if (i < n) pg.light(a, 0, i, PALETTE[i % PALETTE.length], d(0.3));
            run(a);
            if (i < n) {
                a = new ArrayList<>();
                for (int k = ptr[i]; k < ptr[i + 1]; k++) {
                    cg.put(a, 0, k, String.valueOf(col.get(k)), d(0.3));
                    cg.light(a, 0, k, PALETTE[i % PALETTE.length], d(0.3));
                }
                Link l = arrow(pg.cx(i), pg.cy(0) + 28, cg.cx(ptr[i]), cg.cy(0) - 28, PALETTE[i % PALETTE.length], 2.5);
                mine.add(l);
                a.add(new DrawLinkAt(l, 0, d(0.4)));
                run(a);
            }
        }
        pause(1.0);
        int u = 4;
        say("Neighbours of " + u + ": col[ptr[" + u + "] .. ptr[" + (u + 1) + "] - 1] = col[" + ptr[u] + " .. " + (ptr[u + 1] - 1) + "] = " + join(g.nbrs(u), ", ") + ".", Colors.GOLD);
        a = new ArrayList<>();
        pg.light(a, 0, u, Colors.GREEN, d(0.4));
        pg.light(a, 0, u + 1, Colors.GREEN, d(0.4));
        for (int k = ptr[u]; k < ptr[u + 1]; k++) cg.light(a, 0, k, Colors.GREEN, d(0.4));
        run(a);
        tag("two plain arrays: easy to copy as a block", 612, 290, 26, Colors.GRAY, false);
        pause(3.4);
    }

    // ── 11 ───────────────────────────────────────────────────────────

    private void s11() {
        head = writeHeading("Classwork");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Represent the graph using"));
        s.add(ln(1, "Adjacency matrix"));
        s.add(ln(1, "Adjacency list"));
        s.add(ln(1, "COO format"));
        s.add(ln(1, "CSR format"));
        keep(writeSlide(s, -380));
        pause(1.0);

        G g = new G(6, true);
        g.add(1, 0, 2).add(2, 0, 5).add(0, 3, 4).add(0, 4, -3).add(1, 5, 4).add(5, 4, 9);
        int n = g.n;
        double[][] xy = {{360, -350}, {580, -350}, {800, -350}, {360, -235}, {580, -235}, {800, -235}};
        GV v = new GV(g, xy, 32, 28, null, new double[]{0, -42, 0, 0, 0, 0}, new double[]{20, 20, -26, 20, -22, 20});
        v.build(0.7);
        say("Directed and weighted: an edge goes from one node to another and has a weight.", Colors.LIGHT_GRAY);
        pause(1.8);

        // adjacency matrix
        int mm = mark();
        String[] hd = range(n);
        Grid mx = new Grid(new String[n][n], 422, -140, 66, 50, Colors.BLUE, 28);
        mx.headers(hd, hd, 26, Colors.GRAY);
        List<Animation> a = new ArrayList<>();
        mx.show(a, d(0.6));
        run(a);
        say("Matrix: row = from, column = to, the entry is the weight; blank means no edge.", Colors.LIGHT_GRAY);
        for (int k = 0; k < g.e.size(); k++) {
            int[] ed = g.e.get(k);
            a = new ArrayList<>();
            mx.put(a, ed[0], ed[1], String.valueOf(g.wt.get(k)), d(0.4));
            mx.light(a, ed[0], ed[1], Colors.GOLD, d(0.3));
            run(a);
            pause(0.3);
        }
        a = new ArrayList<>();
        mx.plain(a, Colors.BLUE, d(0.4));
        run(a);
        tag("directed: the matrix is not symmetric", 620, 190, 28, Colors.GOLD, false);
        pause(2.6);
        dropSince(mm, d(0.8));

        // adjacency list
        mm = mark();
        CH ch = new CH(n, 430, -140, 54);
        a = new ArrayList<>();
        ch.show(a, d(0.6));
        run(a);
        say("List: for every node, its out-neighbours, each with the edge weight (neighbour:weight).", Colors.LIGHT_GRAY);
        for (int u = 0; u < n; u++) for (int w : g.nbrs(u)) ch.add(w + ":" + g.weight(u, w), u, Colors.BLUE);
        mine.addAll(ch.parts());
        pause(3.0);
        dropSince(mm, d(0.8));

        // COO
        mm = mark();
        List<int[]> pr = g.pairs();
        int m = pr.size();
        Grid coo = new Grid(new String[3][m], 420, -140, 62, 54, Colors.BLUE, 28);
        coo.headers(null, new String[]{"u", "v", "w"}, 24, Colors.GRAY);
        a = new ArrayList<>();
        coo.show(a, d(0.6));
        run(a);
        say("COO: one (from, to, weight) triple per edge, sorted by the first number.", Colors.LIGHT_GRAY);
        for (int k = 0; k < m; k++) {
            a = new ArrayList<>();
            coo.put(a, 0, k, String.valueOf(pr.get(k)[0]), d(0.25));
            coo.put(a, 1, k, String.valueOf(pr.get(k)[1]), d(0.25));
            coo.put(a, 2, k, String.valueOf(g.weight(pr.get(k)[0], pr.get(k)[1])), d(0.25));
            for (int r = 0; r < 3; r++) coo.light(a, r, k, Colors.GOLD, d(0.2));
            run(a);
        }
        a = new ArrayList<>();
        coo.plain(a, Colors.BLUE, d(0.4));
        run(a);
        pause(2.6);
        dropSince(mm, d(0.8));

        // CSR
        mm = mark();
        int[] ptr = new int[n + 1];
        for (int u = 0; u < n; u++) ptr[u + 1] = ptr[u] + g.deg(u);
        List<Integer> col = new ArrayList<>(), val = new ArrayList<>();
        for (int u = 0; u < n; u++) for (int w : g.nbrs(u)) {
            col.add(w);
            val.add(g.weight(u, w));
        }
        String[] ph = new String[n + 1];
        for (int i = 0; i < n; i++) ph[i] = String.valueOf(i);
        ph[n] = "end";
        Grid pg = new Grid(new String[1][n + 1], 420, -90, 58, 50, Colors.ORANGE, 26);
        pg.headers(ph, new String[]{"ptr"}, 20, Colors.GRAY);
        Grid cg = new Grid(new String[2][m], 420, 70, 58, 50, Colors.ORANGE, 26);
        cg.headers(null, new String[]{"col", "val"}, 22, Colors.GRAY);
        a = new ArrayList<>();
        pg.show(a, d(0.6));
        cg.show(a, d(0.6));
        run(a);
        say("CSR: the lists end to end in col, their weights in val, and ptr marks where each list starts.", Colors.LIGHT_GRAY);
        for (int i = 0; i <= n; i++) {
            a = new ArrayList<>();
            pg.put(a, 0, i, String.valueOf(ptr[i]), d(0.3));
            if (i < n) pg.light(a, 0, i, PALETTE[i % PALETTE.length], d(0.3));
            run(a);
            if (i < n && ptr[i + 1] > ptr[i]) {
                a = new ArrayList<>();
                for (int k = ptr[i]; k < ptr[i + 1]; k++) {
                    cg.put(a, 0, k, String.valueOf(col.get(k)), d(0.3));
                    cg.put(a, 1, k, String.valueOf(val.get(k)), d(0.3));
                    cg.light(a, 0, k, PALETTE[i % PALETTE.length], d(0.3));
                    cg.light(a, 1, k, PALETTE[i % PALETTE.length], d(0.3));
                }
                Link l = arrow(pg.cx(i), pg.cy(0) + 26, cg.cx(ptr[i]), cg.cy(0) - 26, PALETTE[i % PALETTE.length], 2.5);
                mine.add(l);
                a.add(new DrawLinkAt(l, 0, d(0.4)));
                run(a);
            }
        }
        tag("3 and 4 have no outgoing edge: ptr repeats", 620, 250, 26, Colors.GOLD, false);
        pause(3.4);
    }

    // ── 12 ───────────────────────────────────────────────────────────

    private void s12() {
        head = writeHeading("Primitive Operations");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "isEdge(e) or hasNeighbor(u, v)"));
        s.add(ln(0, "getDegree(u)"));
        s.add(ln(0, "findNeighbors(u)"));
        s.add(ln(1, "Read-only").kw("Read-only", Colors.GREEN));
        s.add(ln(0, "updateAttribute(e, attr)"));
        s.add(ln(0, "updateAttribute(u, attr)"));
        s.add(ln(1, "Read-Write").kw("Read-Write", Colors.GOLD));
        s.add(ln(0, "addVertex(u)"));
        s.add(ln(0, "addEdge(u, v)"));
        s.add(ln(1, "Dynamic graphs").kw("Dynamic graphs", Colors.RED));
        List<List<MObject>> slide = writeSlide(s, -380);
        pause(1.2);
        swipeAway(slide);
        pause(0.4);

        G g = exampleGraph();
        int n = g.n;
        int mm = mark();
        TextMob t1 = tag("adjacency matrix", 232, -380, 28, Colors.BLUE, false);
        TextMob t2 = tag("adjacency list", 700, -380, 28, Colors.ORANGE, false);
        String[] hd = range(n);
        Grid mx = new Grid(new String[n][n], 100, -300, 44, 44, Colors.BLUE, 24);
        mx.headers(hd, hd, 20, Colors.GRAY);
        for (int u = 0; u < n; u++) for (int w : g.nbrs(u)) mx.txt[u][w].setText("1");
        CH ch = new CH(n, 520, -300, 44);
        List<Animation> a = new ArrayList<>();
        mx.show(a, d(0.6));
        ch.show(a, d(0.6));
        run(a);
        for (int u = 0; u < n; u++) for (int w : g.nbrs(u)) ch.add(w, u, Colors.ORANGE);
        mine.addAll(ch.parts());

        // isEdge(4, 2)
        say("isEdge(4, 2): the matrix looks at one cell; the list walks 4's neighbours until it finds 2.", Colors.LIGHT_GRAY);
        lightCell(mx, 4, 2, Colors.GREEN, d(0.4));
        tag("1 check", 232, -10, 26, Colors.GREEN, false);
        ch.lightBucket(4, Colors.ORANGE);
        int checks = 0;
        for (int k = 0; k < ch.nodes.get(4).size(); k++) {
            checks++;
            boolean hit = g.nbrs(4).get(k) == 2;
            ch.lightNode(4, k, hit ? Colors.GREEN : Colors.RED);
            if (hit) break;
        }
        tag(checks + " checks", 700, -10, 26, Colors.GREEN, false);
        pause(2.0);
        // findNeighbors(4)
        say("findNeighbors(4): the matrix scans the whole row; the list just reads 4's list.", Colors.LIGHT_GRAY);
        for (int j = 0; j < n; j++) lightCell(mx, 4, j, g.has(4, j) ? Colors.GOLD : Colors.RED, d(0.2));
        tag("6 checks", 232, 40, 26, Colors.GOLD, false);
        for (int k = 0; k < ch.nodes.get(4).size(); k++) ch.lightNode(4, k, Colors.GOLD);
        tag(ch.nodes.get(4).size() + " checks", 700, 40, 26, Colors.GOLD, false);
        pause(2.6);
        dropSince(mm, d(0.8));

        // the cost table
        mm = mark();
        String[][] cost = {
                {"operation", "matrix", "list"},
                {"isEdge(u, v)", "O(1)", "O(deg u)"},
                {"getDegree(u)", "O(|V|)", "O(1)*"},
                {"findNeighbors(u)", "O(|V|)", "O(deg u)"},
                {"addEdge(u, v)", "O(1)", "O(1)*"},
                {"addVertex(u)", "O(|V|²)", "O(1)"}};
        Grid cg = new Grid(cost, 280, -300, 220, 52, Colors.BLUE, 26);
        a = new ArrayList<>();
        cg.show(a, d(0.7));
        run(a);
        a = new ArrayList<>();
        cg.lightRow(a, 0, Colors.GOLD, d(0.4));
        run(a);
        say("The cost of each operation follows from how the graph is stored.", Colors.LIGHT_GRAY);
        pause(1.6);
        tag("* if the length of each list is kept up to date", 280, 40, 24, Colors.GRAY, true);
        say("Dynamic graphs: a list of lists grows easily; a matrix must be re-allocated; CSR would need shifting.", Colors.GOLD);
        pause(3.6);
        tag("Classwork: implement the read-only operations in C/C++ for each representation.", -920, 120, 26, Colors.RED, true);
        tag("Classwork: how would you implement dynamic graphs with them?", -920, 170, 26, Colors.RED, true);
        pause(3.0);
    }

    // ── 13 ───────────────────────────────────────────────────────────

    private void s13() {
        head = writeHeading("High-level Operations");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Use primitives to:"));
        s.add(ln(1, "Find if a directed path exists from u to v"));
        s.add(ln(1, "Find if all the vertices of an undirected graph are"));
        s.add(ln(2, "connected"));
        s.add(ln(1, "Find if (u, v, w) form a cycle"));
        s.add(ln(1, "Check if a graph is bipartite given two partitions of"));
        s.add(ln(2, "vertices P1 and P2"));
        s.add(ln(1, "Count all the triangles in the graph"));
        keep(writeSlide(s, -380));
        pause(1.2);

        // a directed path
        int m = mark();
        G d1 = new G(5, true);
        d1.add(0, 1).add(1, 2).add(2, 4).add(0, 3).add(3, 1);
        GV v1 = new GV(d1, new double[][]{{300, -120}, {500, -200}, {700, -120}, {500, 20}, {900, -120}}, 36, 28);
        v1.build(0.6);
        int src = 0, dst = 4;
        int[] par = new int[5];
        java.util.Arrays.fill(par, -1);
        boolean[] seen = new boolean[5];
        java.util.ArrayDeque<Integer> q = new java.util.ArrayDeque<>();
        q.add(src);
        seen[src] = true;
        while (!q.isEmpty()) {
            int x = q.poll();
            for (int y : d1.nbrs(x)) if (!seen[y]) {
                seen[y] = true;
                par[y] = x;
                q.add(y);
            }
        }
        say("A directed path from " + src + " to " + dst + "? Keep asking for neighbours until " + dst + " is reached.", Colors.LIGHT_GRAY);
        List<Integer> path = new ArrayList<>();
        for (int x = dst; x != -1; x = par[x]) path.add(0, x);
        v1.light(src, Colors.GOLD);
        for (int i = 1; i < path.size(); i++) {
            v1.trace(path.get(i - 1), path.get(i), Colors.GOLD);
            v1.light(path.get(i), Colors.GOLD);
        }
        tag("path: " + join(path, " → "), 600, 150, 30, Colors.GOLD, false);
        pause(2.4);
        dropSince(m, d(0.7));

        // connectivity
        m = mark();
        G c1 = new G(7, false);
        c1.add(0, 1).add(1, 2).add(3, 4).add(4, 5).add(5, 6);
        double[][] cxy = {{300, -150}, {440, -60}, {580, -150}, {680, 20}, {760, -100}, {820, 20}, {890, -150}};
        GV v2 = new GV(c1, cxy, 34, 26);
        v2.build(0.6);
        int[] comp = new int[7];
        java.util.Arrays.fill(comp, -1);
        int cnt = 0;
        for (int st = 0; st < 7; st++) {
            if (comp[st] >= 0) continue;
            java.util.ArrayDeque<Integer> stk = new java.util.ArrayDeque<>();
            stk.push(st);
            comp[st] = cnt;
            while (!stk.isEmpty()) {
                int x = stk.pop();
                for (int y : c1.nbrs(x)) if (comp[y] < 0) {
                    comp[y] = cnt;
                    stk.push(y);
                }
            }
            cnt++;
        }
        say("Are all vertices connected? Explore from node 0: some nodes are never reached.", Colors.LIGHT_GRAY);
        for (int i = 0; i < 7; i++) v2.light(i, PALETTE[comp[i] % PALETTE.length]);
        tag(cnt + " groups: not connected", 620, 150, 30, Colors.RED, false);
        pause(2.4);
        G c2 = new G(7, false);
        c2.add(0, 1).add(1, 2).add(3, 4).add(4, 5).add(5, 6).add(2, 3);
        GV v3 = new GV(c2, cxy, 34, 26);
        v3.build(0.1);
        pause(0.5);
        say("With one more edge, 2 to 3, exploring from 0 reaches everything: connected.", Colors.GREEN);
        for (int i = 0; i < 7; i++) v3.light(i, Colors.GREEN);
        tag("1 group: connected", 620, 200, 30, Colors.GREEN, false);
        pause(2.4);
        dropSince(m, d(0.7));

        // a cycle through (u, v, w)
        m = mark();
        G y1 = new G(4, false);
        y1.add(0, 1).add(1, 2).add(0, 2).add(2, 3);
        GV v4 = new GV(y1, new double[][]{{450, -120}, {750, -120}, {600, 40}, {600, 190}}, 36, 28);
        v4.build(0.6);
        int a0 = 0, a1 = 1, a2 = 2;
        say("Do (" + a0 + ", " + a1 + ", " + a2 + ") form a cycle? Ask isEdge three times.", Colors.LIGHT_GRAY);
        boolean ok = true;
        int[][] chk = {{a0, a1}, {a1, a2}, {a2, a0}};
        int row = 0;
        for (int[] c : chk) {
            boolean e = y1.has(c[0], c[1]);
            ok &= e;
            v4.trace(c[0], c[1], e ? Colors.GREEN : Colors.RED);
            tag("isEdge(" + c[0] + ", " + c[1] + ") = " + e, 600, 270 + 36 * row, 26, e ? Colors.GREEN : Colors.RED, false);
            row++;
        }
        tag(ok ? "all three edges exist: a cycle" : "not a cycle", 600, -215, 30, ok ? Colors.GREEN : Colors.RED, false);
        pause(2.4);
        dropSince(m, d(0.7));

        // bipartite check with the two partitions
        m = mark();
        G e1 = exampleGraph();
        GV v5 = new GV(e1, exampleXY(600, -40, 220, 230), 36, 28);
        v5.build(0.6);
        int[] part = {0, 0, 0, 1, 1, 1};
        say("Bipartite with P1 = {0, 1, 2} and P2 = {3, 4, 5}? Every edge must go from P1 to P2.", Colors.LIGHT_GRAY);
        for (int i = 0; i < 6; i++) v5.light(i, part[i] == 0 ? Colors.BLUE : Colors.PINK);
        pause(1.0);
        int bad = 0;
        for (int[] ed : e1.e) {
            boolean good = part[ed[0]] != part[ed[1]];
            v5.trace(ed[0], ed[1], good ? Colors.GREEN : Colors.RED);
            if (!good) bad++;
        }
        tag(bad == 0 ? "bipartite" : "(4,5) joins two nodes of P2: not valid", 600, 150, 28, bad == 0 ? Colors.GREEN : Colors.RED, false);
        pause(2.6);
        dropSince(m, d(0.7));

        // triangles
        m = mark();
        G t1 = new G(5, false);
        t1.add(0, 1).add(0, 2).add(1, 2).add(1, 3).add(2, 3).add(3, 4);
        GV v6 = new GV(t1, new double[][]{{460, -100}, {600, -190}, {600, 20}, {740, -100}, {900, -100}}, 36, 28);
        v6.build(0.6);
        say("Count the triangles: for every edge, look for a node adjacent to both ends.", Colors.LIGHT_GRAY);
        int tri = 0;
        for (int p = 0; p < 5; p++) for (int q2 = p + 1; q2 < 5; q2++) for (int r = q2 + 1; r < 5; r++) {
            if (t1.has(p, q2) && t1.has(q2, r) && t1.has(p, r)) {
                tri++;
                Color c = PALETTE[tri % PALETTE.length];
                v6.light(p, c);
                v6.light(q2, c);
                v6.light(r, c);
                v6.trace(p, q2, c);
                v6.trace(q2, r, c);
                v6.trace(p, r, c);
                pause(0.8);
            }
        }
        tag(tri + " triangles", 620, 150, 30, Colors.GOLD, false);
        pause(2.6);
    }
}
