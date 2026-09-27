package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Scene;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Vec2;

/**
 * PDS Lecture on Graphs — representations, BFS, DFS, applications.
 * Comprehensive coverage matching Rupesh sir's PDS slides.
 */
public class PDSGraphScene extends Scene {

    @Override
    public void construct() {

        // ===== SECTION 1: Title Card =====
        TextMob title = new TextMob("Graphs")
                .setFontSize(64).setBold().setFillColor(Colors.ORANGE);
        title.setPosition(0, -100);
        TextMob subtitle = new TextMob("Representations, Traversals & Applications")
                .setFontSize(28).setFillColor(Colors.LIGHT_GRAY);
        subtitle.setPosition(0, -20);
        TextMob credit = new TextMob("Prof. Rupesh Nasre — IIT Madras")
                .setFontSize(22).setFillColor(Colors.GRAY);
        credit.setPosition(0, 40);

        play(new FadeIn(title, 1.0));
        play(new Write(subtitle, 1.0));
        play(new FadeIn(credit, 0.5));
        hold(2.0);
        play(new FadeOut(title, 0.5), new FadeOut(subtitle, 0.5), new FadeOut(credit, 0.5));

        // ===== SECTION 2: What is a Graph? =====
        TextMob heading = new TextMob("What is a Graph?")
                .setFontSize(48).setBold().setFillColor(Colors.ORANGE);
        heading.setPosition(0, -420);
        play(new FadeIn(heading, 0.5));

        TextMob defn = new TextMob("G = (V, E)  where V = vertices, E = edges")
                .setFontSize(28).setFillColor(Colors.LIGHT_GRAY);
        defn.setPosition(0, -350);
        play(new Write(defn, 1.2));
        hold(1.0);

        // Draw a simple graph: 5 nodes with edges
        // Node positions (relative)
        double[][] nodePos = {
            {-200, -150}, {200, -150}, {-300, 50}, {0, 100}, {300, 50}
        };
        String[] nodeLabels = {"0", "1", "2", "3", "4"};
        CircleMob[] nodes = new CircleMob[5];
        TextMob[] labels = new TextMob[5];

        for (int i = 0; i < 5; i++) {
            nodes[i] = new CircleMob(25);
            nodes[i].setFillColor(Colors.withAlpha(Colors.ORANGE, 0.3));
            nodes[i].setStrokeColor(Colors.ORANGE);
            nodes[i].setPosition(nodePos[i][0], nodePos[i][1]);
            labels[i] = new TextMob(nodeLabels[i]).setFontSize(22).setFillColor(Colors.WHITE);
            labels[i].setPosition(nodePos[i][0], nodePos[i][1]);
            play(new FadeIn(nodes[i], 0.3), new FadeIn(labels[i], 0.3));
        }

        // Edges: 0-1, 0-2, 0-3, 1-4, 3-4, 2-3
        int[][] edges = {{0,1},{0,2},{0,3},{1,4},{3,4},{2,3}};
        ArrowMob[] edgeMobs = new ArrowMob[edges.length];
        for (int i = 0; i < edges.length; i++) {
            int u = edges[i][0], v = edges[i][1];
            edgeMobs[i] = new ArrowMob(nodePos[u][0], nodePos[u][1], nodePos[v][0], nodePos[v][1]);
            edgeMobs[i].setStrokeColor(Colors.LIGHT_GRAY);
            play(new DrawArrow(edgeMobs[i], 0.4));
        }

        TextMob info = new TextMob("|V| = 5,  |E| = 6")
                .setFontSize(24).setFillColor(Colors.TEAL);
        info.setPosition(0, 250);
        play(new FadeIn(info, 0.5));
        hold(2.0);

        // Fade out graph
        for (int i = 0; i < 5; i++) {
            play(new FadeOut(nodes[i], 0.2), new FadeOut(labels[i], 0.2));
        }
        for (ArrowMob e : edgeMobs) play(new FadeOut(e, 0.2));
        play(new FadeOut(heading, 0.3), new FadeOut(defn, 0.3), new FadeOut(info, 0.3));

        // ===== SECTION 3: Directed vs Undirected =====
        heading = new TextMob("Directed vs Undirected Graphs")
                .setFontSize(44).setBold().setFillColor(Colors.ORANGE);
        heading.setPosition(0, -420);
        play(new FadeIn(heading, 0.5));

        // Undirected side
        TextMob undLabel = new TextMob("Undirected").setFontSize(28).setBold().setFillColor(Colors.BLUE);
        undLabel.setPosition(-400, -320);
        play(new FadeIn(undLabel, 0.3));

        CircleMob uA = new CircleMob(22); uA.setFillColor(Colors.withAlpha(Colors.BLUE, 0.3)); uA.setStrokeColor(Colors.BLUE); uA.setPosition(-500, -200);
        CircleMob uB = new CircleMob(22); uB.setFillColor(Colors.withAlpha(Colors.BLUE, 0.3)); uB.setStrokeColor(Colors.BLUE); uB.setPosition(-300, -200);
        CircleMob uC = new CircleMob(22); uC.setFillColor(Colors.withAlpha(Colors.BLUE, 0.3)); uC.setStrokeColor(Colors.BLUE); uC.setPosition(-400, -50);
        TextMob tA = new TextMob("A").setFontSize(18).setFillColor(Colors.WHITE); tA.setPosition(-500, -200);
        TextMob tB = new TextMob("B").setFontSize(18).setFillColor(Colors.WHITE); tB.setPosition(-300, -200);
        TextMob tC = new TextMob("C").setFontSize(18).setFillColor(Colors.WHITE); tC.setPosition(-400, -50);

        play(new FadeIn(uA, 0.3), new FadeIn(uB, 0.3), new FadeIn(uC, 0.3));
        play(new FadeIn(tA, 0.2), new FadeIn(tB, 0.2), new FadeIn(tC, 0.2));

        ArrowMob ue1 = new ArrowMob(-500, -200, -300, -200); ue1.setStrokeColor(Colors.LIGHT_GRAY);
        ArrowMob ue2 = new ArrowMob(-500, -200, -400, -50); ue2.setStrokeColor(Colors.LIGHT_GRAY);
        ArrowMob ue3 = new ArrowMob(-300, -200, -400, -50); ue3.setStrokeColor(Colors.LIGHT_GRAY);
        play(new DrawArrow(ue1, 0.3), new DrawArrow(ue2, 0.3), new DrawArrow(ue3, 0.3));

        TextMob undNote = new TextMob("Edge (u,v) = Edge (v,u)")
                .setFontSize(20).setFillColor(Colors.LIGHT_GRAY);
        undNote.setPosition(-400, 50);
        play(new Write(undNote, 0.8));

        // Directed side
        TextMob dirLabel = new TextMob("Directed (Digraph)").setFontSize(28).setBold().setFillColor(Colors.RED);
        dirLabel.setPosition(400, -320);
        play(new FadeIn(dirLabel, 0.3));

        CircleMob dA = new CircleMob(22); dA.setFillColor(Colors.withAlpha(Colors.RED, 0.3)); dA.setStrokeColor(Colors.RED); dA.setPosition(300, -200);
        CircleMob dB = new CircleMob(22); dB.setFillColor(Colors.withAlpha(Colors.RED, 0.3)); dB.setStrokeColor(Colors.RED); dB.setPosition(500, -200);
        CircleMob dC = new CircleMob(22); dC.setFillColor(Colors.withAlpha(Colors.RED, 0.3)); dC.setStrokeColor(Colors.RED); dC.setPosition(400, -50);
        TextMob dtA = new TextMob("A").setFontSize(18).setFillColor(Colors.WHITE); dtA.setPosition(300, -200);
        TextMob dtB = new TextMob("B").setFontSize(18).setFillColor(Colors.WHITE); dtB.setPosition(500, -200);
        TextMob dtC = new TextMob("C").setFontSize(18).setFillColor(Colors.WHITE); dtC.setPosition(400, -50);

        play(new FadeIn(dA, 0.3), new FadeIn(dB, 0.3), new FadeIn(dC, 0.3));
        play(new FadeIn(dtA, 0.2), new FadeIn(dtB, 0.2), new FadeIn(dtC, 0.2));

        ArrowMob de1 = new ArrowMob(300, -200, 500, -200); de1.setStrokeColor(Colors.RED);
        ArrowMob de2 = new ArrowMob(300, -200, 400, -50); de2.setStrokeColor(Colors.RED);
        ArrowMob de3 = new ArrowMob(400, -50, 500, -200); de3.setStrokeColor(Colors.RED);
        play(new DrawArrow(de1, 0.3), new DrawArrow(de2, 0.3), new DrawArrow(de3, 0.3));

        TextMob dirNote = new TextMob("Edge (u,v) != Edge (v,u)")
                .setFontSize(20).setFillColor(Colors.LIGHT_GRAY);
        dirNote.setPosition(400, 50);
        play(new Write(dirNote, 0.8));
        hold(3.0);

        // Fade all
        play(new FadeOut(heading, 0.3), new FadeOut(undLabel, 0.2), new FadeOut(dirLabel, 0.2));
        play(new FadeOut(uA, 0.2), new FadeOut(uB, 0.2), new FadeOut(uC, 0.2));
        play(new FadeOut(tA, 0.2), new FadeOut(tB, 0.2), new FadeOut(tC, 0.2));
        play(new FadeOut(ue1, 0.2), new FadeOut(ue2, 0.2), new FadeOut(ue3, 0.2));
        play(new FadeOut(dA, 0.2), new FadeOut(dB, 0.2), new FadeOut(dC, 0.2));
        play(new FadeOut(dtA, 0.2), new FadeOut(dtB, 0.2), new FadeOut(dtC, 0.2));
        play(new FadeOut(de1, 0.2), new FadeOut(de2, 0.2), new FadeOut(de3, 0.2));
        play(new FadeOut(undNote, 0.2), new FadeOut(dirNote, 0.2));

        // ===== SECTION 4: Graph Terminology =====
        heading = new TextMob("Graph Terminology")
                .setFontSize(44).setBold().setFillColor(Colors.ORANGE);
        heading.setPosition(0, -420);
        play(new FadeIn(heading, 0.5));

        String[] terms = {
            "Degree: number of edges incident to a vertex",
            "Path: sequence of vertices connected by edges",
            "Cycle: path that starts and ends at the same vertex",
            "Connected: path exists between every pair of vertices",
            "DAG: Directed Acyclic Graph (no cycles)",
            "Weighted Graph: edges have associated costs/weights"
        };

        TextMob[] termMobs = new TextMob[terms.length];
        for (int i = 0; i < terms.length; i++) {
            termMobs[i] = new TextMob(terms[i])
                    .setFontSize(24).setFillColor(Colors.WHITE);
            termMobs[i].setPosition(0, -280 + i * 60);
            play(new Write(termMobs[i], 0.8));
            hold(0.5);
        }
        hold(2.0);

        play(new FadeOut(heading, 0.3));
        for (TextMob t : termMobs) play(new FadeOut(t, 0.2));

        // ===== SECTION 5: Adjacency Matrix =====
        heading = new TextMob("Representation 1: Adjacency Matrix")
                .setFontSize(42).setBold().setFillColor(Colors.ORANGE);
        heading.setPosition(0, -420);
        play(new FadeIn(heading, 0.5));

        TextMob matDesc = new TextMob("2D array: matrix[i][j] = 1 if edge (i,j) exists, 0 otherwise")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        matDesc.setPosition(0, -360);
        play(new Write(matDesc, 1.2));

        // Draw a small graph on the left
        // 4-node graph: 0-1, 0-2, 1-3, 2-3
        double gx = -400, gy = -100;
        double[][] gPos = {{gx-80, gy-80}, {gx+80, gy-80}, {gx-80, gy+80}, {gx+80, gy+80}};
        CircleMob[] gn = new CircleMob[4];
        TextMob[] gl = new TextMob[4];
        for (int i = 0; i < 4; i++) {
            gn[i] = new CircleMob(22);
            gn[i].setFillColor(Colors.withAlpha(Colors.TEAL, 0.3));
            gn[i].setStrokeColor(Colors.TEAL);
            gn[i].setPosition(gPos[i][0], gPos[i][1]);
            gl[i] = new TextMob(String.valueOf(i)).setFontSize(20).setFillColor(Colors.WHITE);
            gl[i].setPosition(gPos[i][0], gPos[i][1]);
            play(new FadeIn(gn[i], 0.2), new FadeIn(gl[i], 0.2));
        }
        int[][] gEdges = {{0,1},{0,2},{1,3},{2,3}};
        ArrowMob[] ge = new ArrowMob[4];
        for (int i = 0; i < 4; i++) {
            int u = gEdges[i][0], v = gEdges[i][1];
            ge[i] = new ArrowMob(gPos[u][0], gPos[u][1], gPos[v][0], gPos[v][1]);
            ge[i].setStrokeColor(Colors.LIGHT_GRAY);
            play(new DrawArrow(ge[i], 0.3));
        }

        // Draw adjacency matrix on the right
        TextMob matTitle = new TextMob("Adjacency Matrix:")
                .setFontSize(22).setBold().setFillColor(Colors.GOLD);
        matTitle.setPosition(200, -220);
        play(new FadeIn(matTitle, 0.3));

        int[][] adjMatrix = {{0,1,1,0},{1,0,0,1},{1,0,0,1},{0,1,1,0}};
        double mx = 100, my = -140;
        // Header row
        TextMob[] hdr = new TextMob[5];
        hdr[0] = new TextMob("").setFontSize(20).setFillColor(Colors.GOLD); hdr[0].setPosition(mx, my);
        for (int i = 0; i < 4; i++) {
            hdr[i+1] = new TextMob(String.valueOf(i)).setFontSize(20).setFillColor(Colors.GOLD);
            hdr[i+1].setPosition(mx + 60 + i * 60, my);
            play(new FadeIn(hdr[i+1], 0.1));
        }

        for (int r = 0; r < 4; r++) {
            TextMob rowLabel = new TextMob(String.valueOf(r)).setFontSize(20).setFillColor(Colors.GOLD);
            rowLabel.setPosition(mx, my + 40 + r * 40);
            play(new FadeIn(rowLabel, 0.1));

            for (int c = 0; c < 4; c++) {
                TextMob cell = new TextMob(String.valueOf(adjMatrix[r][c]))
                        .setFontSize(22)
                        .setFillColor(adjMatrix[r][c] == 1 ? Colors.GREEN : Colors.GRAY);
                cell.setPosition(mx + 60 + c * 60, my + 40 + r * 40);
                play(new FadeIn(cell, 0.1));
            }
            hold(0.3);
        }

        TextMob matSpace = new TextMob("Space: O(V^2)  |  Edge lookup: O(1)")
                .setFontSize(22).setFillColor(Colors.TEAL);
        matSpace.setPosition(0, 200);
        play(new FadeIn(matSpace, 0.5));
        hold(3.0);

        // Fade section
        play(new FadeOut(heading, 0.3), new FadeOut(matDesc, 0.2), new FadeOut(matTitle, 0.2), new FadeOut(matSpace, 0.2));
        for (int i = 0; i < 4; i++) { play(new FadeOut(gn[i], 0.1), new FadeOut(gl[i], 0.1)); }
        for (ArrowMob a : ge) play(new FadeOut(a, 0.1));
        // Note: matrix text mobs will just be left off-screen/overwritten
        clear();

        // ===== SECTION 6: Adjacency List =====
        heading = new TextMob("Representation 2: Adjacency List")
                .setFontSize(42).setBold().setFillColor(Colors.ORANGE);
        heading.setPosition(0, -420);
        play(new FadeIn(heading, 0.5));

        TextMob alDesc = new TextMob("Array of linked lists — each vertex stores its neighbors")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        alDesc.setPosition(0, -360);
        play(new Write(alDesc, 1.0));

        // Show adjacency list for same graph
        // 0 -> [1, 2]
        // 1 -> [0, 3]
        // 2 -> [0, 3]
        // 3 -> [1, 2]
        String[][] adjList = {{"1","2"}, {"0","3"}, {"0","3"}, {"1","2"}};
        for (int i = 0; i < 4; i++) {
            double ly = -220 + i * 80;

            // Vertex box
            RectMob vBox = new RectMob(50, 40);
            vBox.setFillColor(Colors.withAlpha(Colors.ORANGE, 0.3));
            vBox.setStrokeColor(Colors.ORANGE);
            vBox.setPosition(-400, ly);
            TextMob vLabel = new TextMob(String.valueOf(i)).setFontSize(22).setFillColor(Colors.WHITE);
            vLabel.setPosition(-400, ly);
            play(new FadeIn(vBox, 0.2), new FadeIn(vLabel, 0.2));

            // Arrow from box
            ArrowMob toList = new ArrowMob(-375, ly, -300, ly);
            toList.setStrokeColor(Colors.LIGHT_GRAY);
            play(new DrawArrow(toList, 0.2));

            // Linked list of neighbors
            LinkedListMob neighbors = new LinkedListMob(adjList[i]);
            neighbors.setPosition(0, ly);
            play(new FadeIn(neighbors, 0.4));
            hold(0.3);
        }

        TextMob alSpace = new TextMob("Space: O(V + E)  |  Better for sparse graphs")
                .setFontSize(22).setFillColor(Colors.TEAL);
        alSpace.setPosition(0, 200);
        play(new FadeIn(alSpace, 0.5));
        hold(3.0);
        clear();

        // ===== SECTION 7: Matrix vs List Comparison =====
        heading = new TextMob("Adjacency Matrix vs Adjacency List")
                .setFontSize(40).setBold().setFillColor(Colors.GOLD);
        heading.setPosition(0, -380);
        play(new FadeIn(heading, 0.5));

        String[][] compRows = {
            {"Property",       "Adj. Matrix",  "Adj. List"},
            {"Space",          "O(V^2)",       "O(V + E)"},
            {"Edge lookup",    "O(1)",         "O(degree)"},
            {"All neighbors",  "O(V)",         "O(degree)"},
            {"Add edge",       "O(1)",         "O(1)"},
            {"Dense graph",    "Better",       "Wasteful"},
            {"Sparse graph",   "Wasteful",     "Better"},
        };

        TextMob[] compMobs = new TextMob[compRows.length];
        for (int i = 0; i < compRows.length; i++) {
            String line = String.format("%-16s %-14s %-14s", compRows[i][0], compRows[i][1], compRows[i][2]);
            compMobs[i] = new TextMob(line).setFontSize(i == 0 ? 24 : 22)
                    .setFillColor(i == 0 ? Colors.GOLD : Colors.WHITE);
            compMobs[i].setFontFamily("Monospace");
            compMobs[i].setPosition(0, -250 + i * 55);
            play(new Write(compMobs[i], 0.5));
            hold(0.3);
        }
        hold(3.0);
        clear();

        // ===== SECTION 8: BFS (Breadth-First Search) =====
        heading = new TextMob("Breadth-First Search (BFS)")
                .setFontSize(48).setBold().setFillColor(Colors.BLUE);
        heading.setPosition(0, -420);
        play(new FadeIn(heading, 0.5));

        TextMob bfsDesc = new TextMob("Explore level by level using a Queue")
                .setFontSize(26).setFillColor(Colors.LIGHT_GRAY);
        bfsDesc.setPosition(0, -360);
        play(new Write(bfsDesc, 1.0));

        // Build a graph: 6 nodes
        //    0
        //   / \
        //  1   2
        // / \   \
        // 3  4   5
        double[][] bfsPos = {
            {0, -230}, {-200, -130}, {200, -130},
            {-300, -10}, {-100, -10}, {300, -10}
        };
        int[][] bfsEdges = {{0,1},{0,2},{1,3},{1,4},{2,5}};

        CircleMob[] bfsNodes = new CircleMob[6];
        TextMob[] bfsLabels = new TextMob[6];
        for (int i = 0; i < 6; i++) {
            bfsNodes[i] = new CircleMob(25);
            bfsNodes[i].setFillColor(Colors.withAlpha(Colors.BLUE, 0.15));
            bfsNodes[i].setStrokeColor(Colors.BLUE);
            bfsNodes[i].setPosition(bfsPos[i][0], bfsPos[i][1]);
            bfsLabels[i] = new TextMob(String.valueOf(i)).setFontSize(20).setFillColor(Colors.WHITE);
            bfsLabels[i].setPosition(bfsPos[i][0], bfsPos[i][1]);
            play(new FadeIn(bfsNodes[i], 0.2), new FadeIn(bfsLabels[i], 0.2));
        }

        ArrowMob[] bfsEdgeMobs = new ArrowMob[bfsEdges.length];
        for (int i = 0; i < bfsEdges.length; i++) {
            int u = bfsEdges[i][0], v = bfsEdges[i][1];
            bfsEdgeMobs[i] = new ArrowMob(bfsPos[u][0], bfsPos[u][1], bfsPos[v][0], bfsPos[v][1]);
            bfsEdgeMobs[i].setStrokeColor(Colors.DARK_GRAY);
            play(new DrawArrow(bfsEdgeMobs[i], 0.2));
        }

        // Queue display
        TextMob queueLabel = new TextMob("Queue: ").setFontSize(22).setBold().setFillColor(Colors.GOLD);
        queueLabel.setPosition(-400, 120);
        play(new FadeIn(queueLabel, 0.3));

        TextMob queueContent = new TextMob("").setFontSize(22).setFillColor(Colors.WHITE);
        queueContent.setPosition(-100, 120);
        add(queueContent);

        TextMob visitOrder = new TextMob("Visit order: ").setFontSize(22).setBold().setFillColor(Colors.GREEN);
        visitOrder.setPosition(-400, 170);
        play(new FadeIn(visitOrder, 0.3));

        TextMob visited = new TextMob("").setFontSize(22).setFillColor(Colors.WHITE);
        visited.setPosition(-100, 170);
        add(visited);

        // BFS animation: start from 0
        // Queue: [0], visit 0
        int[] bfsOrder = {0, 1, 2, 3, 4, 5};
        String[] queueStates = {"[0]", "[1, 2]", "[2, 3, 4]", "[3, 4, 5]", "[4, 5]", "[5]"};
        String[] visitStates = {"0", "0, 1", "0, 1, 2", "0, 1, 2, 3", "0, 1, 2, 3, 4", "0, 1, 2, 3, 4, 5"};

        for (int step = 0; step < 6; step++) {
            int node = bfsOrder[step];
            // Highlight current node
            bfsNodes[node].setFillColor(Colors.withAlpha(Colors.GOLD, 0.5));
            bfsNodes[node].setStrokeColor(Colors.GOLD);
            queueContent.setText(queueStates[step]);
            visited.setText(visitStates[step]);
            hold(1.2);

            // Mark as visited
            bfsNodes[node].setFillColor(Colors.withAlpha(Colors.GREEN, 0.4));
            bfsNodes[node].setStrokeColor(Colors.GREEN);
            hold(0.5);
        }

        TextMob bfsTime = new TextMob("Time: O(V + E)  |  Space: O(V)")
                .setFontSize(22).setFillColor(Colors.TEAL);
        bfsTime.setPosition(0, 240);
        play(new FadeIn(bfsTime, 0.5));
        hold(2.0);
        clear();

        // ===== SECTION 9: BFS Code =====
        heading = new TextMob("BFS Implementation in C")
                .setFontSize(40).setBold().setFillColor(Colors.BLUE);
        heading.setPosition(0, -440);
        play(new FadeIn(heading, 0.5));

        CodeBlock bfsCode = new CodeBlock(
            "void BFS(int adj[][MAX], int V, int src) {\n" +
            "    int visited[MAX] = {0};\n" +
            "    int queue[MAX], front = 0, rear = 0;\n" +
            "    \n" +
            "    visited[src] = 1;\n" +
            "    queue[rear++] = src;\n" +
            "    \n" +
            "    while (front < rear) {\n" +
            "        int u = queue[front++];  // dequeue\n" +
            "        printf(\"%d \", u);\n" +
            "        \n" +
            "        for (int v = 0; v < V; v++) {\n" +
            "            if (adj[u][v] && !visited[v]) {\n" +
            "                visited[v] = 1;\n" +
            "                queue[rear++] = v;  // enqueue\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}", 18
        );
        bfsCode.setPosition(0, 0);
        play(new RevealCode(bfsCode, 4.0));
        hold(1.0);

        // Highlight key parts
        bfsCode.highlightLine(4); bfsCode.highlightLine(5);
        TextMob note1 = new TextMob("Mark source as visited, add to queue")
                .setFontSize(20).setFillColor(Colors.YELLOW);
        note1.setPosition(0, 340);
        play(new FadeIn(note1, 0.3));
        hold(1.5);
        bfsCode.clearHighlights();
        play(new FadeOut(note1, 0.2));

        bfsCode.highlightLine(11); bfsCode.highlightLine(12); bfsCode.highlightLine(13); bfsCode.highlightLine(14);
        TextMob note2 = new TextMob("Visit all unvisited neighbors")
                .setFontSize(20).setFillColor(Colors.YELLOW);
        note2.setPosition(0, 340);
        play(new FadeIn(note2, 0.3));
        hold(2.0);
        bfsCode.clearHighlights();
        play(new FadeOut(note2, 0.2));
        clear();

        // ===== SECTION 10: DFS (Depth-First Search) =====
        heading = new TextMob("Depth-First Search (DFS)")
                .setFontSize(48).setBold().setFillColor(Colors.PURPLE);
        heading.setPosition(0, -420);
        play(new FadeIn(heading, 0.5));

        TextMob dfsDesc = new TextMob("Go as deep as possible, then backtrack — uses Stack (or recursion)")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        dfsDesc.setPosition(0, -360);
        play(new Write(dfsDesc, 1.2));

        // Reuse same graph topology
        CircleMob[] dfsNodes = new CircleMob[6];
        TextMob[] dfsLabels = new TextMob[6];
        for (int i = 0; i < 6; i++) {
            dfsNodes[i] = new CircleMob(25);
            dfsNodes[i].setFillColor(Colors.withAlpha(Colors.PURPLE, 0.15));
            dfsNodes[i].setStrokeColor(Colors.PURPLE);
            dfsNodes[i].setPosition(bfsPos[i][0], bfsPos[i][1]);
            dfsLabels[i] = new TextMob(String.valueOf(i)).setFontSize(20).setFillColor(Colors.WHITE);
            dfsLabels[i].setPosition(bfsPos[i][0], bfsPos[i][1]);
            play(new FadeIn(dfsNodes[i], 0.2), new FadeIn(dfsLabels[i], 0.2));
        }

        ArrowMob[] dfsEdgeMobs = new ArrowMob[bfsEdges.length];
        for (int i = 0; i < bfsEdges.length; i++) {
            int u = bfsEdges[i][0], v = bfsEdges[i][1];
            dfsEdgeMobs[i] = new ArrowMob(bfsPos[u][0], bfsPos[u][1], bfsPos[v][0], bfsPos[v][1]);
            dfsEdgeMobs[i].setStrokeColor(Colors.DARK_GRAY);
            play(new DrawArrow(dfsEdgeMobs[i], 0.2));
        }

        // Stack display
        TextMob stackLabel = new TextMob("Stack: ").setFontSize(22).setBold().setFillColor(Colors.GOLD);
        stackLabel.setPosition(-400, 120);
        play(new FadeIn(stackLabel, 0.3));

        TextMob stackContent = new TextMob("").setFontSize(22).setFillColor(Colors.WHITE);
        stackContent.setPosition(-100, 120);
        add(stackContent);

        TextMob dfsVisitLabel = new TextMob("Visit order: ").setFontSize(22).setBold().setFillColor(Colors.GREEN);
        dfsVisitLabel.setPosition(-400, 170);
        play(new FadeIn(dfsVisitLabel, 0.3));

        TextMob dfsVisited = new TextMob("").setFontSize(22).setFillColor(Colors.WHITE);
        dfsVisited.setPosition(-100, 170);
        add(dfsVisited);

        // DFS animation: 0 -> 1 -> 3 -> (backtrack) -> 4 -> (backtrack) -> 2 -> 5
        int[] dfsOrder = {0, 1, 3, 4, 2, 5};
        String[] stackStates = {"[0]", "[0, 1]", "[0, 1, 3]", "[0, 1, 4]", "[0, 2]", "[0, 2, 5]"};
        String[] dfsVisitStates = {"0", "0, 1", "0, 1, 3", "0, 1, 3, 4", "0, 1, 3, 4, 2", "0, 1, 3, 4, 2, 5"};

        for (int step = 0; step < 6; step++) {
            int node = dfsOrder[step];
            dfsNodes[node].setFillColor(Colors.withAlpha(Colors.GOLD, 0.5));
            dfsNodes[node].setStrokeColor(Colors.GOLD);
            stackContent.setText(stackStates[step]);
            dfsVisited.setText(dfsVisitStates[step]);
            hold(1.2);

            dfsNodes[node].setFillColor(Colors.withAlpha(Colors.GREEN, 0.4));
            dfsNodes[node].setStrokeColor(Colors.GREEN);
            hold(0.5);
        }

        TextMob dfsTime = new TextMob("Time: O(V + E)  |  Uses recursion stack")
                .setFontSize(22).setFillColor(Colors.TEAL);
        dfsTime.setPosition(0, 240);
        play(new FadeIn(dfsTime, 0.5));
        hold(2.0);
        clear();

        // ===== SECTION 11: DFS Code =====
        heading = new TextMob("DFS Implementation — Recursive")
                .setFontSize(40).setBold().setFillColor(Colors.PURPLE);
        heading.setPosition(0, -440);
        play(new FadeIn(heading, 0.5));

        CodeBlock dfsCode = new CodeBlock(
            "int visited[MAX] = {0};\n" +
            "\n" +
            "void DFS(int adj[][MAX], int V, int u) {\n" +
            "    visited[u] = 1;\n" +
            "    printf(\"%d \", u);\n" +
            "    \n" +
            "    for (int v = 0; v < V; v++) {\n" +
            "        if (adj[u][v] && !visited[v]) {\n" +
            "            DFS(adj, V, v);  // recurse\n" +
            "        }\n" +
            "    }\n" +
            "}", 20
        );
        dfsCode.setPosition(0, -50);
        play(new RevealCode(dfsCode, 3.0));
        hold(1.0);

        dfsCode.highlightLine(3); dfsCode.highlightLine(4);
        TextMob dNote1 = new TextMob("Mark visited and process the vertex")
                .setFontSize(20).setFillColor(Colors.YELLOW);
        dNote1.setPosition(0, 250);
        play(new FadeIn(dNote1, 0.3));
        hold(1.5);
        dfsCode.clearHighlights();
        play(new FadeOut(dNote1, 0.2));

        dfsCode.highlightLine(7); dfsCode.highlightLine(8);
        TextMob dNote2 = new TextMob("Recursively visit unvisited neighbors — goes DEEP first")
                .setFontSize(20).setFillColor(Colors.YELLOW);
        dNote2.setPosition(0, 250);
        play(new FadeIn(dNote2, 0.3));
        hold(2.0);
        dfsCode.clearHighlights();
        clear();

        // ===== SECTION 12: BFS vs DFS Comparison =====
        heading = new TextMob("BFS vs DFS")
                .setFontSize(48).setBold().setFillColor(Colors.GOLD);
        heading.setPosition(0, -380);
        play(new FadeIn(heading, 0.5));

        String[][] bvdRows = {
            {"Property",        "BFS",              "DFS"},
            {"Data Structure",  "Queue",            "Stack/Recursion"},
            {"Order",           "Level by level",   "Depth first"},
            {"Shortest Path?",  "Yes (unweighted)",  "No"},
            {"Space",           "O(V)",             "O(V)"},
            {"Time",            "O(V + E)",         "O(V + E)"},
            {"Use case",        "Shortest path",    "Cycle detection"},
        };

        TextMob[] bvdMobs = new TextMob[bvdRows.length];
        for (int i = 0; i < bvdRows.length; i++) {
            String line = String.format("%-16s %-20s %-20s", bvdRows[i][0], bvdRows[i][1], bvdRows[i][2]);
            bvdMobs[i] = new TextMob(line).setFontSize(i == 0 ? 24 : 22)
                    .setFillColor(i == 0 ? Colors.GOLD : Colors.WHITE);
            bvdMobs[i].setFontFamily("Monospace");
            bvdMobs[i].setPosition(0, -260 + i * 55);
            play(new Write(bvdMobs[i], 0.5));
            hold(0.3);
        }
        hold(3.0);
        clear();

        // ===== SECTION 13: Applications of Graphs =====
        heading = new TextMob("Applications of Graphs")
                .setFontSize(44).setBold().setFillColor(Colors.ORANGE);
        heading.setPosition(0, -380);
        play(new FadeIn(heading, 0.5));

        String[] apps = {
            "Social networks — users are vertices, friendships are edges",
            "Google Maps — cities as vertices, roads as weighted edges",
            "Web crawling — pages as vertices, hyperlinks as edges",
            "Compiler — dependency graphs for build order",
            "Networks — routing, shortest path (Dijkstra, Bellman-Ford)",
            "Scheduling — topological sort on DAGs",
            "Circuit design — VLSI layout, connectivity"
        };

        TextMob[] appMobs = new TextMob[apps.length];
        for (int i = 0; i < apps.length; i++) {
            appMobs[i] = new TextMob(apps[i])
                    .setFontSize(22).setFillColor(Colors.WHITE);
            appMobs[i].setPosition(0, -250 + i * 55);
            play(new Write(appMobs[i], 0.8));
            hold(0.4);
        }
        hold(3.0);
        clear();

        // ===== SECTION 14: Topological Sort =====
        heading = new TextMob("Topological Sort (DAG)")
                .setFontSize(44).setBold().setFillColor(Colors.ORANGE);
        heading.setPosition(0, -420);
        play(new FadeIn(heading, 0.5));

        TextMob tsDesc = new TextMob("Linear ordering of vertices such that for every edge (u,v), u comes before v")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        tsDesc.setPosition(0, -360);
        play(new Write(tsDesc, 1.2));

        TextMob tsEx = new TextMob("Example: Course prerequisites")
                .setFontSize(24).setFillColor(Colors.TEAL);
        tsEx.setPosition(0, -300);
        play(new FadeIn(tsEx, 0.3));

        // Show DAG: Math -> Physics -> QM, Math -> CS -> AI, CS -> ML
        String[] courseNames = {"Math", "Physics", "CS", "QM", "AI", "ML"};
        double[][] cPos = {{-300, -180}, {-100, -180}, {100, -180}, {-100, -40}, {100, -40}, {300, -40}};

        RectMob[] courseBoxes = new RectMob[6];
        TextMob[] courseLabels = new TextMob[6];
        for (int i = 0; i < 6; i++) {
            courseBoxes[i] = new RectMob(90, 40);
            courseBoxes[i].setCornerRadius(8);
            courseBoxes[i].setFillColor(Colors.withAlpha(Colors.ORANGE, 0.2));
            courseBoxes[i].setStrokeColor(Colors.ORANGE);
            courseBoxes[i].setPosition(cPos[i][0], cPos[i][1]);
            courseLabels[i] = new TextMob(courseNames[i]).setFontSize(18).setFillColor(Colors.WHITE);
            courseLabels[i].setPosition(cPos[i][0], cPos[i][1]);
            play(new FadeIn(courseBoxes[i], 0.2), new FadeIn(courseLabels[i], 0.2));
        }

        // Edges: Math->Physics, Math->CS, Physics->QM, CS->AI, CS->ML
        int[][] tsEdges = {{0,1},{0,2},{1,3},{2,4},{2,5}};
        for (int[] e : tsEdges) {
            ArrowMob a = new ArrowMob(cPos[e[0]][0], cPos[e[0]][1], cPos[e[1]][0], cPos[e[1]][1]);
            a.setStrokeColor(Colors.LIGHT_GRAY);
            play(new DrawArrow(a, 0.3));
        }

        TextMob tsResult = new TextMob("Topological order: Math -> Physics -> CS -> QM -> AI -> ML")
                .setFontSize(22).setFillColor(Colors.GREEN);
        tsResult.setPosition(0, 80);
        play(new Write(tsResult, 1.2));

        TextMob tsAlgo = new TextMob("Algorithm: DFS-based or Kahn's (BFS with in-degree)")
                .setFontSize(20).setFillColor(Colors.TEAL);
        tsAlgo.setPosition(0, 140);
        play(new FadeIn(tsAlgo, 0.5));
        hold(3.0);
        clear();

        // ===== SECTION 15: Summary =====
        TextMob sumTitle = new TextMob("Summary")
                .setFontSize(52).setBold().setFillColor(Colors.ORANGE);
        sumTitle.setPosition(0, -300);
        play(new FadeIn(sumTitle, 0.5));

        String[] sumPoints = {
            "Graphs model relationships: G = (V, E)",
            "Adjacency Matrix: O(V^2) space, O(1) edge lookup",
            "Adjacency List: O(V+E) space, better for sparse graphs",
            "BFS: level-order traversal using queue — shortest paths",
            "DFS: depth-first using stack/recursion — cycle detection",
            "Topological sort: ordering for DAGs (prerequisites)",
            "Time complexity for BFS/DFS: O(V + E)"
        };

        TextMob[] sumMobs = new TextMob[sumPoints.length];
        for (int i = 0; i < sumPoints.length; i++) {
            sumMobs[i] = new TextMob(sumPoints[i])
                    .setFontSize(24).setFillColor(Colors.WHITE);
            sumMobs[i].setPosition(0, -180 + i * 55);
            play(new Write(sumMobs[i], 0.8));
            hold(0.4);
        }
        hold(3.0);

        play(new FadeOut(sumTitle, 1.0));
        for (TextMob s : sumMobs) play(new FadeOut(s, 0.5));
        hold(1.0);
    }
}
