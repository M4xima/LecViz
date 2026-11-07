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
 * Clips for slides 14-19 of the graph deck: traversals.
 *
 *   14  the strategies, BFS and DFS side by side on a small tree
 *   15  BFS from 5 on the example graph with its queue, the BFS edges, the BFS tree, the cost
 *   16  BFS as code, run line by line; the version without a queue
 *   17  DFS from 1: the call stack, the DFS edges, the DFS tree
 *   18  DFS as code, run line by line, with its call stack
 *   19  the directory listing: a depth-first walk that prints the current path
 *
 * Every order, edge set, level and tree is computed by running the algorithm on the graph model.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSGraphTravScene extends PDSGraphClipBase {

    private final String part;

    public PDSGraphTravScene(String part) { this.part = part; }

    private static final String[] CODE_BFS = {
            "void bfs(int src) {",
            "    vector<bool> seen(V, false);",
            "    queue<int> q;",
            "    q.push(src); seen[src] = true;",
            "    while (!q.empty()) {",
            "        int u = q.front(); q.pop();",
            "        visit(u);",
            "        for (int v : adj[u])",
            "            if (!seen[v]) { seen[v] = true; q.push(v); }",
            "    }",
            "}"};

    private static final String[] CODE_DFS = {
            "void dfs(int u) {",
            "    seen[u] = true;",
            "    visit(u);",
            "    for (int v : adj[u])",
            "        if (!seen[v])",
            "            dfs(v);",
            "}"};

    @Override
    public void construct() {
        switch (part) {
            case "14": s14(); break;
            case "15": s15(); break;
            case "16": s16(); break;
            case "17": s17(); break;
            case "18": s18(); break;
            default: s19(); break;
        }
        unsay();
        fadeOutAll(d(1.2), mine);
        if (head != null) fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    // ── the algorithms on the model ──────────────────────────────────

    private static List<Integer> bfsOrder(G g, int s) {
        List<Integer> out = new ArrayList<>();
        boolean[] seen = new boolean[g.n];
        java.util.ArrayDeque<Integer> q = new java.util.ArrayDeque<>();
        q.add(s);
        seen[s] = true;
        while (!q.isEmpty()) {
            int u = q.poll();
            out.add(u);
            for (int w : g.nbrs(u)) if (!seen[w]) {
                seen[w] = true;
                q.add(w);
            }
        }
        return out;
    }

    private static void dfsInto(G g, int u, boolean[] seen, List<Integer> out) {
        seen[u] = true;
        out.add(u);
        for (int w : g.nbrs(u)) if (!seen[w]) dfsInto(g, w, seen, out);
    }

    private static List<Integer> dfsOrder(G g, int s) {
        List<Integer> out = new ArrayList<>();
        dfsInto(g, s, new boolean[g.n], out);
        return out;
    }

    // ── a tree from a parent array ───────────────────────────────────

    private static void layoutTree(int u, List<List<Integer>> ch, double[] px, int[] depth, int[] leaf) {
        if (ch.get(u).isEmpty()) {
            px[u] = leaf[0]++;
            return;
        }
        for (int c : ch.get(u)) {
            depth[c] = depth[u] + 1;
            layoutTree(c, ch, px, depth, leaf);
        }
        px[u] = (px[ch.get(u).get(0)] + px[ch.get(u).get(ch.get(u).size() - 1)]) / 2;
    }

    /** Draws the tree given by par[] (par[root] = -1) with the root on top. */
    private GV treeView(int[] par, int root, int n, double cx, double top, double xGap, double yGap, double r, double fs) {
        List<List<Integer>> ch = new ArrayList<>();
        for (int i = 0; i < n; i++) ch.add(new ArrayList<>());
        for (int i = 0; i < n; i++) if (par[i] >= 0) ch.get(par[i]).add(i);
        double[] px = new double[n];
        int[] depth = new int[n];
        layoutTree(root, ch, px, depth, new int[]{0});
        double mn = Double.MAX_VALUE, mx = -Double.MAX_VALUE;
        for (int i = 0; i < n; i++) if (i == root || par[i] >= 0) {
            mn = Math.min(mn, px[i]);
            mx = Math.max(mx, px[i]);
        }
        G tg = new G(n, true);
        for (int i = 0; i < n; i++) if (par[i] >= 0) tg.add(par[i], i);
        double[][] xy = new double[n][2];
        for (int i = 0; i < n; i++) {
            xy[i][0] = cx + (px[i] - (mn + mx) / 2) * xGap;
            xy[i][1] = top + depth[i] * yGap;
        }
        return new GV(tg, xy, r, fs);
    }

    private static String edgesOf(int[] par) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < par.length; i++) if (par[i] >= 0) sb.append(sb.length() > 0 ? ", " : "").append(par[i]).append("-").append(i);
        return sb.toString();
    }

    private void dimNonTree(GV v, G g, int[] par) {
        for (int k = 0; k < g.e.size(); k++) {
            int[] ed = g.e.get(k);
            boolean tree = par[ed[1]] == ed[0] || par[ed[0]] == ed[1];
            if (!tree) v.dimEdge(k, 0.2);
        }
    }

    // ── animated BFS and DFS ─────────────────────────────────────────

    private int[][] bfsAnim(GV v, G g, int s, Seq q, Seq order) {
        int n = g.n;
        int[] par = new int[n], dist = new int[n];
        java.util.Arrays.fill(par, -1);
        boolean[] seen = new boolean[n];
        java.util.ArrayDeque<Integer> dq = new java.util.ArrayDeque<>();
        seen[s] = true;
        dq.add(s);
        v.light(s, QUEUED_C);
        q.push(String.valueOf(s), QUEUED_C);
        v.note(s, "d=0", Colors.WHITE, 0, -(v.r + 16), 24);
        pause(0.5);
        while (!dq.isEmpty()) {
            int u = dq.poll();
            q.popFront();
            v.light(u, CURRENT_C);
            order.push(String.valueOf(u), DONE_C);
            pause(0.35);
            for (int w : g.nbrs(u)) {
                if (seen[w]) continue;
                seen[w] = true;
                par[w] = u;
                dist[w] = dist[u] + 1;
                dq.add(w);
                v.trace(u, w, Colors.GOLD);
                v.light(w, QUEUED_C);
                q.push(String.valueOf(w), QUEUED_C);
                v.note(w, "d=" + dist[w], Colors.WHITE, 0, -(v.r + 16), 24);
            }
            v.light(u, DONE_C);
            pause(0.25);
        }
        return new int[][]{par, dist};
    }

    private void dfsAnim(GV v, G g, int u, boolean[] seen, int[] par, Seq stack, Seq order) {
        seen[u] = true;
        v.light(u, CURRENT_C);
        stack.push("dfs(" + u + ")", QUEUED_C);
        order.push(String.valueOf(u), DONE_C);
        pause(0.45);
        for (int w : g.nbrs(u)) {
            if (!seen[w]) {
                par[w] = u;
                v.trace(u, w, Colors.GOLD);
                dfsAnim(v, g, w, seen, par, stack, order);
                v.light(u, CURRENT_C);
                pause(0.2);
            } else if (par[u] != w) {
                say("Node " + w + " is already visited: the edge (" + u + ", " + w + ") is not used.", Colors.ORANGE);
                pause(1.2);
            }
        }
        stack.popBack();
        v.light(u, DONE_C);
        pause(0.25);
    }

    // ── 14 ───────────────────────────────────────────────────────────

    private static double[][] treeXY(double cx) {
        return new double[][]{{cx, -250}, {cx - 90, -130}, {cx + 90, -130}, {cx - 135, -10}, {cx - 45, -10}, {cx + 45, -10}, {cx + 135, -10}};
    }

    private void s14() {
        head = writeHeading("Traversals");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Repeated application of findNeighbors()"));
        s.add(ln(0, "Strategies:"));
        s.add(ln(1, "Level-by-level: breadth-first search (BFS)").kw("breadth-first", Colors.BLUE));
        s.add(ln(1, "Explore a neighbor completely before moving to the"));
        s.add(ln(2, "other: depth-first search (DFS)").kw("depth-first", Colors.PINK));
        s.add(ln(1, "Cost based exploration: A* (used in AI / games)"));
        s.add(ln(1, "..."));
        keep(writeSlide(s, -380));
        pause(1.0);
        G tg = new G(7, false);
        tg.add(0, 1).add(0, 2).add(1, 3).add(1, 4).add(2, 5).add(2, 6);
        GV va = new GV(tg, treeXY(330), 26, 22);
        va.build(0.5);
        GV vb = new GV(tg, treeXY(760), 26, 22);
        vb.build(0.5);
        tag("BFS", 330, -330, 30, Colors.BLUE, false);
        tag("DFS", 760, -330, 30, Colors.PINK, false);
        List<Integer> bfs = bfsOrder(tg, 0), dfs = dfsOrder(tg, 0);
        say("Both keep asking for neighbours. BFS finishes a level before it goes down; DFS goes deep first.", Colors.LIGHT_GRAY);
        pause(1.0);
        for (int i = 0; i < 7; i++) {
            List<Animation> a = new ArrayList<>();
            va.nodeColor(a, bfs.get(i), Colors.BLUE, d(0.3));
            va.noteIn(a, bfs.get(i), String.valueOf(i + 1), Colors.WHITE, 0, -40, 24);
            vb.nodeColor(a, dfs.get(i), Colors.PINK, d(0.3));
            vb.noteIn(a, dfs.get(i), String.valueOf(i + 1), Colors.WHITE, 0, -40, 24);
            playAll(a);
            pause(0.55);
        }
        pause(1.0);
        say("The numbers show the order of visits: BFS " + join(bfs, " ") + ", DFS " + join(dfs, " ") + ".", Colors.GOLD);
        pause(3.2);
    }

    // ── 15 ───────────────────────────────────────────────────────────

    private void s15() {
        head = writeHeading("BFS");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Start from a given vertex"));
        s.add(ln(1, "Then visit each neighbor of the vertex (without exploring"));
        s.add(ln(2, "the neighbor's neighbors)"));
        s.add(ln(1, "Then visit the first neighbor's neighbors"));
        s.add(ln(1, "Followed by second neighbor's neighbors"));
        s.add(ln(1, "And so on."));
        s.add(ln(0, "Classwork").kw("Classwork", Colors.RED));
        s.add(ln(1, "Show the order of BFS traversal from 5 on the example."));
        s.add(ln(1, "What edges are present in the BFS?"));
        s.add(ln(1, "What data structure does it form?"));
        s.add(ln(1, "What is the complexity of BFS?"));
        List<List<MObject>> slide = writeSlide(s, -380);
        pause(1.4);
        swipeAway(slide);
        pause(0.4);

        G g = exampleGraph();
        GV v = new GV(g, exampleXY(-400, -60, 220, 240), 42, 34);
        v.build(0.5);
        tag("queue", 60, -390, 28, Colors.GRAY, true);
        tag("visit order", 60, -250, 28, Colors.GRAY, true);
        Seq q = new Seq(250, -330, 95, 0, 80, 54, QUEUED_C, 28);
        Seq order = new Seq(250, -190, 95, 0, 80, 54, DONE_C, 28);
        int src = 5;
        say("BFS from " + src + ": its neighbours first, then theirs. The queue keeps the order; d is the distance from " + src + ".", Colors.LIGHT_GRAY);
        pause(1.0);
        int[][] r = bfsAnim(v, g, src, q, order);
        int[] par = r[0];
        List<Integer> ord = bfsOrder(g, src);
        pause(0.8);
        tag("Order from " + src + ": " + join(ord, ", "), -920, 200, 30, Colors.GOLD, true);
        say("The order of the traversal from " + src + ": " + join(ord, ", ") + ".", Colors.GOLD);
        pause(1.8);
        dimNonTree(v, g, par);
        tag("BFS edges: " + edgesOf(par), -920, 255, 30, Colors.GREEN, true);
        say("Only the edges that first reached a node are in the BFS; the rest are skipped.", Colors.GREEN);
        pause(2.2);
        GV tv = treeView(par, src, g.n, 600, 130, 150, 100, 30, 26);
        tag("the BFS tree", 600, 70, 26, Colors.GRAY, false);
        tv.build(0.7);
        tag("It forms a tree rooted at " + src + ": the depth of a node is its distance from " + src + ".", -920, 310, 28, Colors.GREEN, true);
        say("No cycles, every node once: BFS builds a tree, level by level.", Colors.GREEN);
        pause(2.6);
        tag("Each node is queued once and each list read once: O(|V| + |E|).", -920, 365, 28, Colors.GOLD, true);
        say("Every node enters the queue once and every adjacency list is read once: O(|V| + |E|).", Colors.GOLD);
        pause(3.4);
    }

    // ── 16 ───────────────────────────────────────────────────────────

    private void s16() {
        head = writeHeading("BFS Implementation");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Classwork: Let's implement BFS.").kw("Classwork", Colors.RED));
        s.add(ln(1, "Source: bfs.cpp"));
        s.add(ln(1, "Implement BFS without a queue. What is the complexity?"));
        s.add(ln(0, "Applications of BFS:"));
        s.add(ln(1, "Finding connectivity"));
        s.add(ln(1, "Finding cycles"));
        s.add(ln(1, "Finding strongly connected components (forward-"));
        s.add(ln(2, "backward method)"));
        s.add(ln(1, "Checking bipartiteness"));
        s.add(ln(1, "..."));
        List<List<MObject>> slide = writeSlide(s, -380);
        pause(1.6);
        swipeAway(slide);
        pause(0.4);

        G g = exampleGraph();
        int n = g.n;
        CodeBox cb = code(CODE_BFS, -930, -300, 24, 44, 3.0);
        GV v = new GV(g, exampleXY(560, -130, 200, 220), 38, 32);
        v.build(0.5);
        tag("queue q", 160, 60, 26, Colors.GRAY, true);
        tag("visit order", 160, 170, 26, Colors.GRAY, true);
        Seq q = new Seq(400, 110, 90, 0, 76, 50, QUEUED_C, 26);
        Seq order = new Seq(400, 220, 90, 0, 76, 50, DONE_C, 26);
        int src = 5;
        say("The code, run on the example with src = " + src + ".", Colors.LIGHT_GRAY);
        boolean[] seen = new boolean[n];
        java.util.ArrayDeque<Integer> dq = new java.util.ArrayDeque<>();
        play(cb.moveHl(1, d(0.3)));
        pause(0.3);
        play(cb.moveHl(2, d(0.3)));
        pause(0.3);
        play(cb.moveHl(3, d(0.3)));
        seen[src] = true;
        dq.add(src);
        v.light(src, QUEUED_C);
        q.push(String.valueOf(src), QUEUED_C);
        Color[] col = new Color[n];
        java.util.Arrays.fill(col, NODE_C);
        col[src] = QUEUED_C;
        while (!dq.isEmpty()) {
            play(cb.moveHl(4, d(0.25)));
            int u = dq.poll();
            play(cb.moveHl(5, d(0.25)));
            q.popFront();
            v.light(u, CURRENT_C);
            play(cb.moveHl(6, d(0.25)));
            order.push(String.valueOf(u), DONE_C);
            for (int w : g.nbrs(u)) {
                play(cb.moveHl(7, d(0.2)));
                play(cb.moveHl(8, d(0.25)));
                if (!seen[w]) {
                    seen[w] = true;
                    dq.add(w);
                    v.trace(u, w, Colors.GOLD);
                    v.light(w, QUEUED_C);
                    col[w] = QUEUED_C;
                    q.push(String.valueOf(w), QUEUED_C);
                } else {
                    v.light(w, Colors.RED);
                    pause(0.2);
                    v.light(w, col[w]);
                }
            }
            v.light(u, DONE_C);
            col[u] = DONE_C;
            pause(0.2);
        }
        play(cb.moveHl(4, d(0.3)));
        say("The queue is empty: every node reachable from " + src + " was visited once. Cost: O(|V| + |E|).", Colors.GREEN);
        pause(2.6);
        say("Without a queue: keep a level number per node and, level by level, scan all |V| nodes for those on that level.", Colors.GOLD);
        pause(4.2);
        say("That is O(|V|) work per level: O(|V| x levels + |E|), which is O(|V|^2) on a long path.", Colors.GOLD);
        pause(4.0);
    }

    // ── 17 ───────────────────────────────────────────────────────────

    private void s17() {
        head = writeHeading("DFS");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Start from a given vertex"));
        s.add(ln(1, "Then visit the first neighbor of the vertex"));
        s.add(ln(1, "Then visit the first neighbor's neighbors, without exploring"));
        s.add(ln(2, "the second neighbor. Go deep."));
        s.add(ln(1, "Followed by second neighbor's neighbors. Go deep."));
        s.add(ln(1, "And so on."));
        s.add(ln(0, "Classwork").kw("Classwork", Colors.RED));
        s.add(ln(1, "Show the order of DFS traversal from node 1."));
        s.add(ln(1, "What edges are present in the DFS?"));
        s.add(ln(1, "What data structure does it form?"));
        s.add(ln(1, "What is the complexity of DFS?"));
        List<List<MObject>> slide = writeSlide(s, -380);
        pause(1.4);
        swipeAway(slide);
        pause(0.4);

        G g = exampleGraph();
        GV v = new GV(g, exampleXY(-400, -60, 220, 240), 42, 34);
        v.build(0.5);
        tag("visit order", 60, -390, 28, Colors.GRAY, true);
        tag("call stack", 330, 330, 26, Colors.GRAY, false);
        Seq order = new Seq(250, -330, 95, 0, 80, 54, DONE_C, 28);
        Seq stack = new Seq(330, 255, 0, -56, 150, 46, QUEUED_C, 26);
        int src = 1;
        say("DFS from " + src + ": take the first unvisited neighbour and go deep; the call stack remembers the way back.", Colors.LIGHT_GRAY);
        pause(1.0);
        int[] par = new int[g.n];
        java.util.Arrays.fill(par, -1);
        dfsAnim(v, g, src, new boolean[g.n], par, stack, order);
        List<Integer> ord = dfsOrder(g, src);
        pause(0.8);
        tag("Order from " + src + ": " + join(ord, ", "), -920, 200, 30, Colors.GOLD, true);
        say("The order of the traversal from " + src + ": " + join(ord, ", ") + ".", Colors.GOLD);
        pause(1.8);
        dimNonTree(v, g, par);
        tag("DFS edges: " + edgesOf(par), -920, 255, 30, Colors.GREEN, true);
        say("The DFS uses these edges only; the edge (1, 5) was skipped.", Colors.GREEN);
        pause(2.2);
        GV tv = treeView(par, src, g.n, 700, 40, 130, 80, 28, 24);
        tag("the DFS tree", 700, -20, 26, Colors.GRAY, false);
        tv.build(0.7);
        tag("It forms a tree rooted at " + src + ": a deep, thin one.", -920, 310, 28, Colors.GREEN, true);
        say("Again a tree: no cycles, every node once, but this one is deep instead of wide.", Colors.GREEN);
        pause(2.6);
        tag("Every node once, every list once: O(|V| + |E|).", -920, 365, 28, Colors.GOLD, true);
        say("Every node is visited once and every adjacency list read once: O(|V| + |E|).", Colors.GOLD);
        pause(3.4);
    }

    // ── 18 ───────────────────────────────────────────────────────────

    private void dfsCode(GV v, G g, int u, boolean[] seen, Seq stack, Seq order, CodeBox cb) {
        play(cb.moveHl(0, d(0.25)));
        stack.push("dfs(" + u + ")", QUEUED_C);
        play(cb.moveHl(1, d(0.25)));
        seen[u] = true;
        v.light(u, CURRENT_C);
        play(cb.moveHl(2, d(0.25)));
        order.push(String.valueOf(u), DONE_C);
        for (int w : g.nbrs(u)) {
            play(cb.moveHl(3, d(0.2)));
            play(cb.moveHl(4, d(0.25)));
            if (!seen[w]) {
                play(cb.moveHl(5, d(0.25)));
                v.trace(u, w, Colors.GOLD);
                dfsCode(v, g, w, seen, stack, order, cb);
                v.light(u, CURRENT_C);
            }
        }
        stack.popBack();
        v.light(u, DONE_C);
        pause(0.15);
    }

    private void s18() {
        head = writeHeading("DFS Implementation");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Classwork: Let's implement DFS.").kw("Classwork", Colors.RED));
        s.add(ln(1, "Source: dfs.cpp"));
        s.add(ln(0, "Applications of DFS:"));
        s.add(ln(1, "Finding connectivity"));
        s.add(ln(1, "Finding cycles"));
        s.add(ln(1, "Finding strongly connected components (forward-"));
        s.add(ln(2, "backward method)"));
        s.add(ln(1, "Checking bipartiteness"));
        s.add(ln(1, "..."));
        List<List<MObject>> slide = writeSlide(s, -380);
        pause(1.6);
        swipeAway(slide);
        pause(0.4);

        G g = exampleGraph();
        CodeBox cb = code(CODE_DFS, -930, -300, 26, 48, 2.6);
        GV v = new GV(g, exampleXY(560, -130, 200, 220), 38, 32);
        v.build(0.5);
        tag("call stack", 160, 100, 26, Colors.GRAY, true);
        tag("visit order", 160, 360, 26, Colors.GRAY, true);
        Seq stack = new Seq(420, 150, 0, 0, 140, 46, QUEUED_C, 26);
        // the stack grows upwards from y = 330; items of Seq go along dy
        Seq st = new Seq(420, 310, 0, -52, 140, 44, QUEUED_C, 24);
        Seq order = new Seq(400, 395, 90, 0, 76, 46, DONE_C, 26);
        mine.remove(stack);
        int src = 1;
        say("The code, run on the example from node " + src + ".", Colors.LIGHT_GRAY);
        dfsCode(v, g, src, new boolean[g.n], st, order, cb);
        say("The recursion is the stack: a call is pushed on entering a node and popped on leaving it.", Colors.GREEN);
        pause(3.0);
        say("Replace the recursion by an explicit stack and the code becomes the same as BFS with a stack instead of a queue.", Colors.GOLD);
        pause(4.4);
    }

    // ── 19 ───────────────────────────────────────────────────────────

    private static String pathOf(GN n) {
        java.util.ArrayDeque<String> names = new java.util.ArrayDeque<>();
        for (GN x = n; x != null && x.parent != null; x = x.parent) names.addFirst(x.name);
        return "/" + String.join("/", names);
    }

    private void s19() {
        head = writeHeading("Directory Listing");
        pause(0.5);
        GN root = dirTree();
        GT t = new GT(root, 0, -370, 100, 16, 22, 22, true,
                n -> n.parent == null ? Colors.GOLD : n.kids.isEmpty() ? Colors.GRAY : Colors.BLUE);
        t.build(0.35);
        mine.addAll(t.parts());
        pause(0.8);
        say("A directory tree is a graph: directories are nodes and 'contains' is a directed edge.", Colors.LIGHT_GRAY);
        pause(2.2);
        TextMob path = label("/", 0, 330, 36, Colors.WHITE, false, true);
        mine.add(path);
        TextMob cnt = label("", 0, 385, 26, Colors.GRAY, false, false);
        mine.add(cnt);
        play(new FadeIn(path, d(0.4)), new FadeIn(cnt, d(0.4)));
        say("Visit it depth first: go down a directory completely before the next one at the same level.", Colors.LIGHT_GRAY);
        GN prev = null;
        int k = 0;
        for (GN n : t.nodes) {
            if (prev != null) {
                List<Animation> a = new ArrayList<>();
                t.paint(a, prev, Colors.GREEN, d(0.15));
                t.paintEdge(a, prev, Colors.GREEN, d(0.15));
                playAll(a);
            }
            lit(t, n, Colors.ORANGE, d(0.2));
            path.setText(n.parent == null ? "/" : pathOf(n));
            cnt.setText("visited " + (++k) + " of " + t.nodes.size());
            pause(0.12);
            prev = n;
        }
        List<Animation> a = new ArrayList<>();
        t.paint(a, prev, Colors.GREEN, d(0.2));
        t.paintEdge(a, prev, Colors.GREEN, d(0.2));
        playAll(a);
        say("The listing prints exactly this order: a depth-first traversal of the tree of directories.", Colors.GOLD);
        pause(3.4);
    }
}
