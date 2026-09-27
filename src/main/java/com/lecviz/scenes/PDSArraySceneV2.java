package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.MObject;
import com.lecviz.core.Scene;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Vec2;
import javafx.scene.paint.Color;

/**
 * PDS Arrays Lecture — High-Quality 3Blue1Brown Style
 * Based on Prof. Rupesh Nasre's lecture slides (3-arrays.pdf)
 * IIT Madras, August 2021
 *
 * Covers: Properties, Array Expressions, Row/Column Major,
 * Search (Linear, Binary, Sorted Matrix), Sorting Algorithms
 * (Bubble, Insertion, Shell, Selection, Quick, Merge, Counting, Radix),
 * and Summary.
 *
 * CS5013 Project: LecViz
 * Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSArraySceneV2 extends Scene {

    // ─── Shared helper: fade-out a group of objects ────────────
    private void fadeOutAll(double dur, MObject... objs) {
        FadeOut[] anims = new FadeOut[objs.length];
        for (int i = 0; i < objs.length; i++) anims[i] = new FadeOut(objs[i], dur);
        play(anims);
        for (MObject o : objs) remove(o);
    }

    // ─── Shared helper: create a section heading ───────────────
    private TextMob sectionTitle(String text) {
        return new TextMob(text)
                .setFontSize(52).setBold().setFillColor(Colors.BLUE)
                .setPosition(0, -440);
    }

    @Override
    public void construct() {
        buildTitleCard();
        buildProperties();
        buildArrayExpressions();
        buildRowColumnMajor();
        buildSearch();
        buildSortedMatrixSearch();
        buildSortingOverview();
        buildBubbleSort();
        buildInsertionSort();
        buildQuicksort();
        buildMergeSort();
        buildCountingSort();
        buildSummary();
    }


    // ╔══════════════════════════════════════════════════════════╗
    // ║  SECTION 1: TITLE CARD                                  ║
    // ╚══════════════════════════════════════════════════════════╝
    private void buildTitleCard() {

        RectMob[] decorCells = new RectMob[12];
        for (int i = 0; i < 12; i++) {
            decorCells[i] = new RectMob(60, 40);
            decorCells[i].setFillColor(Colors.withAlpha(Colors.BLUE, 0.08 + i * 0.02));
            decorCells[i].setStrokeColor(Colors.withAlpha(Colors.BLUE, 0.3));
            decorCells[i].setPosition(-600 + i * 100, 200);
            decorCells[i].setOpacity(0);
        }
        for (RectMob r : decorCells) add(r);

        for (int i = 0; i < 12; i++) {
            play(new FadeIn(decorCells[i], 0.25));
        }
        hold(1.0);

        TextMob title = new TextMob("Arrays")
                .setFontSize(96).setBold().setFillColor(Colors.BLUE);
        title.setPosition(0, -140);
        title.setScale(0.3);
        play(new FadeIn(title, 1.2), new ScaleTo(title, 1.0, 1.5));
        hold(1.5);

        TextMob subtitle = new TextMob("Programming and Data Structures")
                .setFontSize(32).setFillColor(Colors.LIGHT_GRAY);
        subtitle.setPosition(0, -50);
        play(new Write(subtitle, 2.0));
        hold(0.5);

        TextMob credit = new TextMob("Prof. Rupesh Nasre")
                .setFontSize(28).setFillColor(Colors.GOLD);
        credit.setPosition(0, 20);
        play(new Write(credit, 1.5));
        hold(0.3);

        TextMob institute = new TextMob("IIT Madras")
                .setFontSize(22).setFillColor(Colors.GRAY);
        institute.setPosition(0, 65);
        play(new FadeIn(institute, 1.0));

        RectMob line = new RectMob(400, 2);
        line.setFillColor(Colors.BLUE);
        line.setPosition(0, 105);
        play(new FadeIn(line, 0.8));

        TextMob lecviz = new TextMob("Animated with LecViz")
                .setFontSize(16).setFillColor(Colors.DARK_GRAY);
        lecviz.setPosition(0, 130);
        play(new FadeIn(lecviz, 0.8));

        hold(5.0);

        fadeOutAll(1.0, title, subtitle, credit, institute, line, lecviz);
        for (RectMob r : decorCells) { play(new FadeOut(r, 0.3)); remove(r); }
        hold(1.0);
    }


    // ╔══════════════════════════════════════════════════════════╗
    // ║  SECTION 2: PROPERTIES                                  ║
    // ╚══════════════════════════════════════════════════════════╝
    private void buildProperties() {

        TextMob heading = sectionTitle("Properties of Arrays");
        play(new Write(heading, 1.5));
        hold(1.0);

        TextMob prop1 = new TextMob("Simplest data structure")
                .setFontSize(30).setFillColor(Colors.WHITE);
        prop1.setPosition(-400, -340);
        play(new Write(prop1, 1.5));
        hold(0.5);

        TextMob sub1a = new TextMob("Acts as aggregate over primitives or other aggregates")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        sub1a.setPosition(-350, -300);
        play(new Write(sub1a, 1.2));
        hold(0.3);

        TextMob sub1b = new TextMob("May have multiple dimensions")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        sub1b.setPosition(-350, -265);
        play(new Write(sub1b, 1.0));
        hold(1.5);

        TextMob prop2 = new TextMob("Contiguous storage")
                .setFontSize(30).setBold().setFillColor(Colors.GREEN);
        prop2.setPosition(-400, -200);
        play(new Write(prop2, 1.2));
        hold(0.5);

        ArrayMob mem = new ArrayMob("10", "20", "30", "40", "50");
        mem.setCellSize(80, 50);
        mem.setLabel("int arr[5]");
        mem.setPosition(0, -100);
        play(new FadeIn(mem, 1.2));
        hold(0.5);

        for (int i = 0; i < 5; i++) {
            mem.setCellColor(i, Colors.withAlpha(Colors.GREEN, 0.3));
            hold(0.6);
        }
        hold(1.5);

        TextMob prop3 = new TextMob("Random access in O(1)")
                .setFontSize(30).setBold().setFillColor(Colors.GOLD);
        prop3.setPosition(-400, 0);
        play(new Write(prop3, 1.2));
        hold(0.5);

        LaTeXMob o1formula = new LaTeXMob("\\text{addr}(a[i]) = \\text{base} + i \\times \\text{sizeof(type)}")
                .setSize(32).setLatexColor(Colors.YELLOW);
        o1formula.setPosition(100, 60);
        play(new Write(o1formula, 2.0));
        hold(1.5);

        mem.setPointer(3, "i=3");
        mem.highlight(3);
        hold(2.0);
        mem.clearHighlights();
        mem.setPointer(0, "i=0");
        mem.highlight(0);
        hold(1.5);
        mem.clearHighlights();
        mem.setPointer(4, "i=4");
        mem.highlight(4);
        hold(1.5);
        mem.clearHighlights();
        mem.hidePointer();
        hold(0.5);

        TextMob prop4 = new TextMob("Storage space:")
                .setFontSize(30).setFillColor(Colors.WHITE);
        prop4.setPosition(-400, 140);
        play(new Write(prop4, 1.0));

        TextMob sub4a = new TextMob("Fixed for arrays  |  Dynamically allocatable on heap  |  Variable for vectors")
                .setFontSize(20).setFillColor(Colors.LIGHT_GRAY);
        sub4a.setPosition(0, 185);
        play(new Write(sub4a, 2.0));

        hold(5.0);

        fadeOutAll(0.8, heading, prop1, sub1a, sub1b, prop2, mem, prop3, o1formula, prop4, sub4a);
        hold(1.0);
    }


    // ╔══════════════════════════════════════════════════════════╗
    // ║  SECTION 3: ARRAY EXPRESSIONS                           ║
    // ╚══════════════════════════════════════════════════════════╝
    private void buildArrayExpressions() {

        TextMob heading = sectionTitle("Array Expressions");
        play(new Write(heading, 1.5));
        hold(0.5);

        TextMob explain = new TextMob("We view an array as a D-dimensional matrix.")
                .setFontSize(26).setFillColor(Colors.LIGHT_GRAY);
        explain.setPosition(0, -370);
        play(new Write(explain, 1.5));
        hold(0.5);

        TextMob explain2 = new TextMob("However, for the hardware, it is simply single dimensional.")
                .setFontSize(26).setFillColor(Colors.GOLD);
        explain2.setPosition(0, -330);
        play(new Write(explain2, 1.5));
        hold(1.5);

        CodeBlock errorCode = new CodeBlock(
            "void fun(int a[ ][ ]) {\n" +
            "    a[0][0] = 20;\n" +
            "}\n" +
            "void main() {\n" +
            "    int a[5][10];\n" +
            "    fun(a);\n" +
            "    printf(\"%d\\n\", a[0][0]);\n" +
            "}", 20
        );
        errorCode.setPosition(-350, -130);
        play(new RevealCode(errorCode, 5.0));
        hold(1.0);

        TextMob errorMsg = new TextMob("ERROR: type of formal parameter 1 is incomplete")
                .setFontSize(20).setBold().setFillColor(Colors.RED);
        errorMsg.setPosition(-350, 50);
        play(new Write(errorMsg, 1.5));
        hold(3.0);

        TextMob hornTitle = new TextMob("For int a[w4][w3][w2][w1]:")
                .setFontSize(24).setFillColor(Colors.WHITE);
        hornTitle.setPosition(300, -250);
        play(new Write(hornTitle, 1.2));
        hold(0.5);

        TextMob addrQ = new TextMob("Address of a[i][j][k][l] ?")
                .setFontSize(22).setFillColor(Colors.TEAL);
        addrQ.setPosition(300, -210);
        play(new Write(addrQ, 1.0));
        hold(0.5);

        LaTeXMob naiveFormula = new LaTeXMob(
            "(i \\cdot w3 \\cdot w2 \\cdot w1 + j \\cdot w2 \\cdot w1 + k \\cdot w1 + l) \\times 4"
        ).setSize(22).setLatexColor(Colors.LIGHT_GRAY);
        naiveFormula.setPosition(300, -160);
        play(new Write(naiveFormula, 2.0));
        hold(2.0);

        TextMob horner = new TextMob("Horner's Rule (optimized):")
                .setFontSize(22).setBold().setFillColor(Colors.GOLD);
        horner.setPosition(300, -110);
        play(new Write(horner, 1.0));
        hold(0.5);

        LaTeXMob hornerFormula = new LaTeXMob(
            "(((i \\cdot w3 + j) \\cdot w2 + k) \\cdot w1 + l) \\times 4"
        ).setSize(24).setLatexColor(Colors.YELLOW);
        hornerFormula.setPosition(300, -60);
        play(new Write(hornerFormula, 2.0));
        hold(0.5);

        TextMob fewer = new TextMob("Fewer multiplications!")
                .setFontSize(20).setFillColor(Colors.GREEN);
        fewer.setPosition(300, -15);
        play(new FadeIn(fewer, 1.0));

        hold(5.0);

        fadeOutAll(0.8, heading, explain, explain2, errorCode, errorMsg,
                   hornTitle, addrQ, naiveFormula, horner, hornerFormula, fewer);
        hold(1.0);
    }


    // ╔══════════════════════════════════════════════════════════╗
    // ║  SECTION 4: ROW-MAJOR vs COLUMN-MAJOR                  ║
    // ╚══════════════════════════════════════════════════════════╝
    private void buildRowColumnMajor() {

        TextMob heading = sectionTitle("Row-Major vs Column-Major Storage");
        play(new Write(heading, 1.5));
        hold(0.5);

        TextMob rowTitle = new TextMob("Row-Major (C, C++, Java)")
                .setFontSize(26).setBold().setFillColor(Colors.BLUE);
        rowTitle.setPosition(-420, -350);
        play(new Write(rowTitle, 1.2));
        hold(0.3);

        TextMob rowDesc = new TextMob("All elements of a row are stored together")
                .setFontSize(20).setFillColor(Colors.LIGHT_GRAY);
        rowDesc.setPosition(-420, -310);
        play(new Write(rowDesc, 1.2));
        hold(0.5);

        Color[] rowColors = {
            Colors.withAlpha(Colors.BLUE, 0.3),
            Colors.withAlpha(Colors.GREEN, 0.3),
            Colors.withAlpha(Colors.RED, 0.3)
        };
        String[][] matData = {
            {"1", "2", "3", "4"},
            {"5", "6", "7", "8"},
            {"9", "10", "11", "12"}
        };

        ArrayMob[] rowArrays = new ArrayMob[3];
        for (int r = 0; r < 3; r++) {
            rowArrays[r] = new ArrayMob(matData[r]);
            rowArrays[r].setCellSize(55, 40);
            rowArrays[r].setShowIndices(false);
            rowArrays[r].setPosition(-420, -230 + r * 50);
            for (int c = 0; c < 4; c++) {
                rowArrays[r].setCellColor(c, rowColors[r]);
            }
            play(new FadeIn(rowArrays[r], 0.8));
        }
        hold(1.0);

        ArrayMob rowFlat = new ArrayMob(
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"
        );
        rowFlat.setCellSize(50, 35);
        rowFlat.setShowIndices(false);
        rowFlat.setLabel("Memory layout:");
        rowFlat.setPosition(-420, -50);
        for (int i = 0; i < 12; i++) {
            rowFlat.setCellColor(i, rowColors[i / 4]);
        }
        play(new FadeIn(rowFlat, 1.5));
        hold(2.0);

        TextMob colTitle = new TextMob("Column-Major (Fortran)")
                .setFontSize(26).setBold().setFillColor(Colors.TEAL);
        colTitle.setPosition(420, -350);
        play(new Write(colTitle, 1.2));
        hold(0.3);

        TextMob colDesc = new TextMob("Each column is stored together")
                .setFontSize(20).setFillColor(Colors.LIGHT_GRAY);
        colDesc.setPosition(420, -310);
        play(new Write(colDesc, 1.2));
        hold(0.5);

        ArrayMob[] colArrays = new ArrayMob[3];
        for (int r = 0; r < 3; r++) {
            colArrays[r] = new ArrayMob(matData[r]);
            colArrays[r].setCellSize(55, 40);
            colArrays[r].setShowIndices(false);
            colArrays[r].setPosition(420, -230 + r * 50);
            for (int c = 0; c < 4; c++) {
                Color[] colColors = {
                    Colors.withAlpha(Colors.BLUE, 0.3),
                    Colors.withAlpha(Colors.GREEN, 0.3),
                    Colors.withAlpha(Colors.RED, 0.3),
                    Colors.withAlpha(Colors.GOLD, 0.3)
                };
                colArrays[r].setCellColor(c, colColors[c]);
            }
            play(new FadeIn(colArrays[r], 0.8));
        }
        hold(1.0);

        ArrayMob colFlat = new ArrayMob(
            "1", "5", "9", "2", "6", "10", "3", "7", "11", "4", "8", "12"
        );
        colFlat.setCellSize(50, 35);
        colFlat.setShowIndices(false);
        colFlat.setLabel("Memory layout:");
        colFlat.setPosition(420, -50);
        Color[] colFlatColors = {
            Colors.withAlpha(Colors.BLUE, 0.3),
            Colors.withAlpha(Colors.GREEN, 0.3),
            Colors.withAlpha(Colors.RED, 0.3),
            Colors.withAlpha(Colors.GOLD, 0.3)
        };
        for (int i = 0; i < 12; i++) {
            colFlat.setCellColor(i, colFlatColors[i / 3]);
        }
        play(new FadeIn(colFlat, 1.5));

        RectMob divider = new RectMob(2, 400);
        divider.setFillColor(Colors.DARK_GRAY);
        divider.setPosition(0, -180);
        play(new FadeIn(divider, 0.6));

        hold(6.0);

        fadeOutAll(0.8, heading, rowTitle, rowDesc, colTitle, colDesc, rowFlat, colFlat, divider);
        for (ArrayMob a : rowArrays) { play(new FadeOut(a, 0.4)); remove(a); }
        for (ArrayMob a : colArrays) { play(new FadeOut(a, 0.4)); remove(a); }
        hold(1.0);
    }


    // ╔══════════════════════════════════════════════════════════╗
    // ║  SECTION 5: SEARCH                                      ║
    // ╚══════════════════════════════════════════════════════════╝
    private void buildSearch() {

        TextMob heading = sectionTitle("Search");
        play(new Write(heading, 1.5));
        hold(0.5);

        // ─── Linear Search ───
        TextMob linearTitle = new TextMob("Linear Search: O(N)")
                .setFontSize(32).setFillColor(Colors.RED);
        linearTitle.setPosition(-500, -350);
        play(new Write(linearTitle, 1.2));
        hold(0.5);

        ArrayMob searchArr = new ArrayMob("3", "7", "2", "9", "5", "1", "8", "4", "6");
        searchArr.setCellSize(65, 45);
        searchArr.setLabel("Searching for value 5:");
        searchArr.setPosition(0, -250);
        play(new FadeIn(searchArr, 1.0));
        hold(0.5);

        TextMob searchStatus = new TextMob("")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        searchStatus.setPosition(0, -160);
        add(searchStatus);

        for (int i = 0; i < 9; i++) {
            searchArr.clearHighlights();
            searchArr.setPointer(i, "i=" + i);
            searchArr.highlight(i);
            String val = searchArr.getValue(i);
            if (val.equals("5")) {
                searchArr.setCellColor(i, Colors.withAlpha(Colors.GREEN, 0.4));
                searchStatus.setText("Found 5 at index " + i + "!");
                searchStatus.setFillColor(Colors.GREEN);
                hold(3.0);
                break;
            } else {
                searchArr.setCellColor(i, Colors.withAlpha(Colors.RED, 0.2));
                searchStatus.setText(val + " != 5, continue...");
                hold(1.0);
            }
        }
        searchArr.hidePointer();
        hold(1.5);

        // ─── Binary Search ───
        fadeOutAll(0.6, linearTitle, searchArr, searchStatus);
        hold(0.5);

        TextMob binaryTitle = new TextMob("Binary Search: O(log N)")
                .setFontSize(32).setBold().setFillColor(Colors.GREEN);
        binaryTitle.setPosition(-500, -350);
        play(new Write(binaryTitle, 1.2));
        hold(0.5);

        LaTeXMob recurrence = new LaTeXMob("T(N) = T(N/2) + c")
                .setSize(28).setLatexColor(Colors.TEAL);
        recurrence.setPosition(-500, -300);
        play(new Write(recurrence, 1.2));
        hold(1.0);

        ArrayMob bsArr = new ArrayMob("1", "2", "5", "8", "12", "19", "25", "31", "40", "50");
        bsArr.setCellSize(60, 45);
        bsArr.setLabel("Sorted array — searching for 19:");
        bsArr.setPosition(0, -200);
        play(new FadeIn(bsArr, 1.0));
        hold(0.5);

        TextMob bsStatus = new TextMob("")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        bsStatus.setPosition(0, -110);
        add(bsStatus);

        // Step 1
        bsArr.setPointer(4, "mid");
        bsArr.highlight(4);
        bsStatus.setText("mid=4, arr[4]=12 < 19 → search right half");
        for (int i = 0; i <= 3; i++) bsArr.setCellColor(i, Colors.withAlpha(Colors.DARK_GRAY, 0.4));
        hold(3.0);

        bsArr.clearHighlights();
        // Step 2
        bsArr.setPointer(7, "mid");
        bsArr.highlight(7);
        bsStatus.setText("mid=7, arr[7]=31 > 19 → search left half");
        for (int i = 8; i <= 9; i++) bsArr.setCellColor(i, Colors.withAlpha(Colors.DARK_GRAY, 0.4));
        hold(3.0);

        bsArr.clearHighlights();
        // Step 3
        bsArr.setPointer(5, "mid");
        bsArr.highlight(5);
        bsArr.setCellColor(5, Colors.withAlpha(Colors.GREEN, 0.4));
        bsStatus.setText("mid=5, arr[5]=19 == 19 → FOUND!");
        bsStatus.setFillColor(Colors.GREEN);
        hold(4.0);

        bsArr.hidePointer();
        bsArr.clearHighlights();
        fadeOutAll(0.6, bsArr, bsStatus);
        hold(0.5);

        CodeBlock bsCode = new CodeBlock(
            "int bsearch(int a[], int N, int val) {\n" +
            "    int low = 0, high = N - 1;\n" +
            "    while (low <= high) {\n" +
            "        int mid = (low + high) / 2;\n" +
            "        if (a[mid] == val) return 1;\n" +
            "        if (a[mid] > val) high = mid - 1;\n" +
            "        else low = mid + 1;\n" +
            "    }\n" +
            "    return 0;\n" +
            "}", 22
        );
        bsCode.setPosition(0, -100);
        play(new RevealCode(bsCode, 6.0));
        hold(2.0);

        TextMob ternary = new TextMob("How about Ternary Search?")
                .setFontSize(24).setFillColor(Colors.ORANGE);
        ternary.setPosition(0, 120);
        play(new Write(ternary, 1.2));
        hold(0.5);

        TextMob ternaryAns = new TextMob("Also O(log N), but with larger constant — binary is more efficient!")
                .setFontSize(20).setFillColor(Colors.LIGHT_GRAY);
        ternaryAns.setPosition(0, 160);
        play(new Write(ternaryAns, 1.5));

        hold(5.0);

        fadeOutAll(0.8, heading, binaryTitle, recurrence, bsCode, ternary, ternaryAns);
        hold(1.0);
    }


    // ╔══════════════════════════════════════════════════════════╗
    // ║  SECTION 6: SEARCH IN A SORTED MATRIX                  ║
    // ╚══════════════════════════════════════════════════════════╝
    private void buildSortedMatrixSearch() {

        TextMob heading = sectionTitle("Search in a Sorted Matrix");
        play(new Write(heading, 1.5));
        hold(0.5);

        TextMob desc = new TextMob("Matrix sorted left-to-right AND top-to-bottom. Search for key = 44")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        desc.setPosition(0, -380);
        play(new Write(desc, 1.5));
        hold(1.0);

        int[][] matrix = {
            {3, 5, 9, 20, 39},
            {4, 6, 11, 21, 40},
            {7, 10, 12, 23, 45},
            {8, 13, 22, 27, 46},
            {19, 29, 41, 43, 49}
        };

        double cellW = 70, cellH = 50;
        double matX = -350, matY = -280;

        RectMob[][] cells = new RectMob[5][5];
        TextMob[][] cellTexts = new TextMob[5][5];

        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                cells[r][c] = new RectMob(cellW - 4, cellH - 4);
                cells[r][c].setFillColor(Colors.withAlpha(Colors.BLUE, 0.08));
                cells[r][c].setStrokeColor(Colors.withAlpha(Colors.BLUE, 0.4));
                cells[r][c].setPosition(matX + c * cellW, matY + r * cellH);
                add(cells[r][c]);

                cellTexts[r][c] = new TextMob(String.valueOf(matrix[r][c]))
                        .setFontSize(20).setFillColor(Colors.WHITE);
                cellTexts[r][c].setPosition(matX + c * cellW, matY + r * cellH);
                add(cellTexts[r][c]);
            }
        }
        hold(1.5);

        TextMob approach = new TextMob("Approach: Elimination — O(M + N)")
                .setFontSize(28).setBold().setFillColor(Colors.GOLD);
        approach.setPosition(300, -280);
        play(new Write(approach, 1.2));
        hold(0.5);

        TextMob stepDesc = new TextMob("Start from top-right corner")
                .setFontSize(20).setFillColor(Colors.LIGHT_GRAY);
        stepDesc.setPosition(300, -240);
        play(new Write(stepDesc, 1.0));

        TextMob rule1 = new TextMob("If key < element → eliminate that column")
                .setFontSize(20).setFillColor(Colors.TEAL);
        rule1.setPosition(300, -200);
        play(new Write(rule1, 1.0));

        TextMob rule2 = new TextMob("If key > element → eliminate that row")
                .setFontSize(20).setFillColor(Colors.ORANGE);
        rule2.setPosition(300, -165);
        play(new Write(rule2, 1.0));
        hold(1.5);

        TextMob statusText = new TextMob("")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        statusText.setPosition(300, -100);
        add(statusText);

        int row = 0, col = 4;
        int key = 44;

        while (row < 5 && col >= 0) {
            cells[row][col].setFillColor(Colors.withAlpha(Colors.YELLOW, 0.4));
            cellTexts[row][col].setFillColor(Colors.YELLOW);

            int val = matrix[row][col];
            if (val == key) {
                cells[row][col].setFillColor(Colors.withAlpha(Colors.GREEN, 0.5));
                cellTexts[row][col].setFillColor(Colors.GREEN);
                statusText.setText(val + " == " + key + " → FOUND!");
                statusText.setFillColor(Colors.GREEN);
                hold(4.0);
                break;
            } else if (key < val) {
                statusText.setText(key + " < " + val + " → eliminate column " + col);
                for (int r = row; r < 5; r++) {
                    cells[r][col].setFillColor(Colors.withAlpha(Colors.DARK_GRAY, 0.3));
                    cellTexts[r][col].setFillColor(Colors.GRAY);
                }
                hold(2.0);
                col--;
            } else {
                statusText.setText(key + " > " + val + " → eliminate row " + row);
                for (int c = 0; c <= col; c++) {
                    cells[row][c].setFillColor(Colors.withAlpha(Colors.DARK_GRAY, 0.3));
                    cellTexts[row][c].setFillColor(Colors.GRAY);
                }
                hold(2.0);
                row++;
            }
        }

        if (row >= 5 || col < 0) {
            statusText.setText(key + " not found in matrix");
            statusText.setFillColor(Colors.RED);
            hold(3.0);
        }

        hold(3.0);

        fadeOutAll(0.6, heading, desc, approach, stepDesc, rule1, rule2, statusText);
        for (int r = 0; r < 5; r++) for (int c = 0; c < 5; c++) {
            remove(cells[r][c]);
            remove(cellTexts[r][c]);
        }
        hold(1.0);
    }


    // ╔══════════════════════════════════════════════════════════╗
    // ║  SECTION 7: SORTING OVERVIEW                            ║
    // ╚══════════════════════════════════════════════════════════╝
    private void buildSortingOverview() {

        TextMob heading = sectionTitle("Sorting");
        play(new Write(heading, 1.5));
        hold(0.5);

        TextMob fundamental = new TextMob("A fundamental operation")
                .setFontSize(30).setFillColor(Colors.WHITE);
        fundamental.setPosition(0, -370);
        play(new Write(fundamental, 1.2));
        hold(0.5);

        TextMob stable = new TextMob("Stable sorting: maintains relative order of equal elements")
                .setFontSize(22).setFillColor(Colors.TEAL);
        stable.setPosition(0, -320);
        play(new Write(stable, 1.5));
        hold(1.0);

        TextMob compTitle = new TextMob("Comparison-based:")
                .setFontSize(26).setBold().setFillColor(Colors.BLUE);
        compTitle.setPosition(-400, -260);
        play(new Write(compTitle, 1.0));

        String[] compMethods = {"Bubble", "Insertion", "Shell", "Selection", "Quick", "Merge", "Heap"};
        for (int i = 0; i < compMethods.length; i++) {
            TextMob m = new TextMob(compMethods[i])
                    .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
            m.setPosition(-380 + i * 120, -220);
            play(new FadeIn(m, 0.3));
        }
        hold(1.0);

        TextMob otherTitle = new TextMob("Non-comparison:")
                .setFontSize(26).setBold().setFillColor(Colors.GREEN);
        otherTitle.setPosition(-400, -170);
        play(new Write(otherTitle, 1.0));

        String[] otherMethods = {"Radix", "Bucket", "Counting"};
        for (int i = 0; i < otherMethods.length; i++) {
            TextMob m = new TextMob(otherMethods[i])
                    .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
            m.setPosition(-380 + i * 120, -130);
            play(new FadeIn(m, 0.3));
        }

        hold(2.0);

        TextMob tableTitle = new TextMob("Sorting Algorithms at a Glance")
                .setFontSize(28).setBold().setFillColor(Colors.GOLD);
        tableTitle.setPosition(0, -70);
        play(new Write(tableTitle, 1.2));
        hold(0.5);

        String[][] tableData = {
            {"Algorithm", "Worst Case", "Average Case"},
            {"Bubble",    "O(n^2)",     "O(n^2)"},
            {"Insertion",  "O(n^2)",     "O(n^2)"},
            {"Selection", "O(n^2)",     "O(n^2)"},
            {"Heap",      "O(n log n)", "O(n log n)"},
            {"Quick",     "O(n^2)",     "O(n log n)"},
            {"Merge",     "O(n log n)", "O(n log n)"},
        };

        double tableX = -350, tableY = -20;
        double colWidths[] = {200, 200, 200};
        double rowH = 38;

        for (int r = 0; r < tableData.length; r++) {
            for (int c = 0; c < 3; c++) {
                Color textColor;
                if (r == 0) textColor = Colors.BLUE;
                else if (tableData[r][1].contains("log")) textColor = Colors.GREEN;
                else textColor = Colors.withAlpha(Colors.WHITE, 0.9);

                double fontSize = (r == 0) ? 22 : 20;
                TextMob cell = new TextMob(tableData[r][c])
                        .setFontSize(fontSize).setFillColor(textColor);
                if (r == 0) cell.setBold();
                cell.setPosition(tableX + c * colWidths[c] + colWidths[c] / 2, tableY + r * rowH);
                play(new FadeIn(cell, 0.15));
            }
            if (r == 0) {
                RectMob sep = new RectMob(580, 1);
                sep.setFillColor(Colors.DARK_GRAY);
                sep.setPosition(tableX + 290, tableY + rowH / 2 + 5);
                add(sep);
            }
            hold(0.5);
        }

        hold(6.0);

        clear();
        hold(1.0);
    }


    // ╔══════════════════════════════════════════════════════════╗
    // ║  SECTION 8: BUBBLE SORT                                 ║
    // ╚══════════════════════════════════════════════════════════╝
    private void buildBubbleSort() {

        TextMob heading = sectionTitle("Bubble Sort");
        heading.setFillColor(Colors.ORANGE);
        play(new Write(heading, 1.5));
        hold(0.5);

        TextMob desc = new TextMob("Compare adjacent values and swap if needed")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        desc.setPosition(0, -380);
        play(new Write(desc, 1.5));
        hold(0.3);

        TextMob invariant = new TextMob("Invariant: After i-th pass, i largest elements are in final position")
                .setFontSize(20).setFillColor(Colors.TEAL);
        invariant.setPosition(0, -340);
        play(new Write(invariant, 1.5));
        hold(1.0);

        String[] vals = {"64", "34", "25", "12", "22", "11", "90"};
        ArrayMob arr = new ArrayMob(vals);
        arr.setCellSize(80, 55);
        arr.setLabel("Bubble Sort Animation:");
        arr.setPosition(0, -200);
        play(new FadeIn(arr, 1.0));
        hold(1.0);

        TextMob passLabel = new TextMob("")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        passLabel.setPosition(0, -110);
        add(passLabel);

        int n = vals.length;
        for (int pass = 0; pass < 3; pass++) {
            passLabel.setText("Pass " + (pass + 1));
            hold(0.5);
            for (int j = 0; j < n - 1 - pass; j++) {
                arr.clearHighlights();
                arr.highlight(j);
                arr.highlight(j + 1);
                arr.setCellColor(j, Colors.withAlpha(Colors.YELLOW, 0.3));
                arr.setCellColor(j + 1, Colors.withAlpha(Colors.YELLOW, 0.3));

                int a = Integer.parseInt(arr.getValue(j));
                int b = Integer.parseInt(arr.getValue(j + 1));
                if (a > b) {
                    hold(0.6);
                    arr.swap(j, j + 1);
                    arr.setCellColor(j, Colors.withAlpha(Colors.GREEN, 0.3));
                    arr.setCellColor(j + 1, Colors.withAlpha(Colors.RED, 0.2));
                    hold(0.5);
                } else {
                    hold(0.4);
                }
                arr.setCellColor(j, Colors.withAlpha(Colors.BLUE, 0.1));
                arr.setCellColor(j + 1, Colors.withAlpha(Colors.BLUE, 0.1));
            }
            arr.setCellColor(n - 1 - pass, Colors.withAlpha(Colors.GREEN, 0.35));
            hold(1.0);
        }

        for (int pass = 3; pass < n - 1; pass++) {
            for (int j = 0; j < n - 1 - pass; j++) {
                int a = Integer.parseInt(arr.getValue(j));
                int b = Integer.parseInt(arr.getValue(j + 1));
                if (a > b) arr.swap(j, j + 1);
            }
            arr.setCellColor(n - 1 - pass, Colors.withAlpha(Colors.GREEN, 0.35));
        }
        arr.setCellColor(0, Colors.withAlpha(Colors.GREEN, 0.35));
        passLabel.setText("Sorted!");
        passLabel.setFillColor(Colors.GREEN);
        hold(2.0);

        LaTeXMob complexity = new LaTeXMob("O(n^2)")
                .setSize(36).setLatexColor(Colors.RED);
        complexity.setPosition(-300, -30);
        play(new Write(complexity, 1.0));
        hold(0.5);

        TextMob best = new TextMob("Best case (sorted): O(n) with early termination")
                .setFontSize(20).setFillColor(Colors.GREEN);
        best.setPosition(150, -30);
        play(new Write(best, 1.2));
        hold(1.0);

        CodeBlock bubbleCode = new CodeBlock(
            "for (ii = 0; ii < N - 1; ++ii)\n" +
            "    for (jj = 0; jj < N - ii - 1; ++jj)\n" +
            "        if (arr[jj] > arr[jj + 1])\n" +
            "            swap(jj, jj + 1);", 20
        );
        bubbleCode.setPosition(0, 100);
        play(new RevealCode(bubbleCode, 4.0));

        hold(5.0);

        fadeOutAll(0.8, heading, desc, invariant, arr, passLabel, complexity, best, bubbleCode);
        hold(1.0);
    }


    // ╔══════════════════════════════════════════════════════════╗
    // ║  SECTION 9: INSERTION SORT                              ║
    // ╚══════════════════════════════════════════════════════════╝
    private void buildInsertionSort() {

        TextMob heading = sectionTitle("Insertion Sort");
        heading.setFillColor(Colors.ORANGE);
        play(new Write(heading, 1.5));
        hold(0.5);

        TextMob desc = new TextMob("Take the i-th element, insert it at its correct position among first i elements")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        desc.setPosition(0, -380);
        play(new Write(desc, 1.5));
        hold(0.3);

        TextMob metaphor = new TextMob("Like sorting playing cards in your hand")
                .setFontSize(20).setFillColor(Colors.TEAL);
        metaphor.setPosition(0, -345);
        play(new Write(metaphor, 1.0));
        hold(0.3);

        TextMob inv = new TextMob("Invariant: First i elements are always sorted")
                .setFontSize(20).setBold().setFillColor(Colors.GOLD);
        inv.setPosition(0, -310);
        play(new Write(inv, 1.0));
        hold(1.0);

        String[] vals = {"12", "11", "13", "5", "6"};
        ArrayMob arr = new ArrayMob(vals);
        arr.setCellSize(90, 55);
        arr.setLabel("Insertion Sort Animation:");
        arr.setPosition(0, -200);
        play(new FadeIn(arr, 1.0));

        arr.setCellColor(0, Colors.withAlpha(Colors.GREEN, 0.25));
        hold(1.0);

        TextMob statusText = new TextMob("")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        statusText.setPosition(0, -110);
        add(statusText);

        int n = vals.length;

        for (int i = 1; i < n; i++) {
            String keyStr = arr.getValue(i);
            int key = Integer.parseInt(keyStr);

            arr.setCellColor(i, Colors.withAlpha(Colors.YELLOW, 0.4));
            arr.setPointer(i, "key=" + key);
            statusText.setText("Insert " + key + " into sorted portion [0.." + (i - 1) + "]");
            hold(1.5);

            int j = i - 1;
            while (j >= 0 && Integer.parseInt(arr.getValue(j)) > key) {
                arr.setValue(j + 1, arr.getValue(j));
                arr.setCellColor(j + 1, Colors.withAlpha(Colors.ORANGE, 0.3));
                arr.setCellColor(j, Colors.withAlpha(Colors.PURPLE, 0.3));
                hold(0.8);
                arr.setCellColor(j + 1, Colors.withAlpha(Colors.GREEN, 0.25));
                j--;
            }
            arr.setValue(j + 1, keyStr);
            arr.setCellColor(j + 1, Colors.withAlpha(Colors.GREEN, 0.5));
            arr.hidePointer();
            statusText.setText(key + " placed at index " + (j + 1));
            hold(1.0);

            for (int k = 0; k <= i; k++) {
                arr.setCellColor(k, Colors.withAlpha(Colors.GREEN, 0.25));
            }
            hold(0.8);
        }

        statusText.setText("Sorted!");
        statusText.setFillColor(Colors.GREEN);
        hold(2.0);

        TextMob bestCase = new TextMob("Best: O(n) sorted  |  Worst: O(n^2) reverse sorted")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        bestCase.setPosition(0, -60);
        play(new Write(bestCase, 1.5));
        hold(1.0);

        CodeBlock insertCode = new CodeBlock(
            "for (ii = 1; ii < N; ++ii) {\n" +
            "    int key = arr[ii];\n" +
            "    int jj = ii - 1;\n" +
            "    while (jj >= 0 && key < arr[jj]) {\n" +
            "        arr[jj + 1] = arr[jj];\n" +
            "        --jj;\n" +
            "    }\n" +
            "    arr[jj + 1] = key;\n" +
            "}", 20
        );
        insertCode.setPosition(0, 100);
        play(new RevealCode(insertCode, 5.0));

        hold(5.0);

        fadeOutAll(0.8, heading, desc, metaphor, inv, arr, statusText, bestCase, insertCode);
        hold(1.0);
    }


    // ╔══════════════════════════════════════════════════════════╗
    // ║  SECTION 10: QUICKSORT                                  ║
    // ╚══════════════════════════════════════════════════════════╝
    private void buildQuicksort() {

        TextMob heading = sectionTitle("Quicksort");
        heading.setFillColor(Colors.ORANGE);
        play(new Write(heading, 1.5));
        hold(0.5);

        TextMob step1 = new TextMob("1. Choose a pivot element")
                .setFontSize(24).setFillColor(Colors.WHITE);
        step1.setPosition(-500, -370);
        play(new Write(step1, 1.0));
        hold(0.3);

        TextMob step2 = new TextMob("2. Partition: smaller elements left, larger elements right")
                .setFontSize(24).setFillColor(Colors.WHITE);
        step2.setPosition(-500, -330);
        play(new Write(step2, 1.2));
        hold(0.3);

        TextMob step3 = new TextMob("3. Recursively sort left and right halves")
                .setFontSize(24).setFillColor(Colors.WHITE);
        step3.setPosition(-500, -290);
        play(new Write(step3, 1.0));
        hold(0.3);

        TextMob pivotNote = new TextMob("Partitioning crucially decides the complexity!")
                .setFontSize(20).setBold().setFillColor(Colors.RED);
        pivotNote.setPosition(-500, -250);
        play(new Write(pivotNote, 1.2));
        hold(1.5);

        String[] vals = {"38", "27", "43", "3", "9", "82", "10"};
        ArrayMob arr = new ArrayMob(vals);
        arr.setCellSize(80, 55);
        arr.setLabel("Quicksort — Pivot = 38:");
        arr.setPosition(0, -140);
        play(new FadeIn(arr, 1.0));

        arr.setCellColor(0, Colors.withAlpha(Colors.GOLD, 0.4));
        arr.setPointer(0, "pivot");
        hold(1.5);

        TextMob partStatus = new TextMob("")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        partStatus.setPosition(0, -50);
        add(partStatus);

        partStatus.setText("Partitioning...");
        hold(1.5);

        arr.setValues("27", "3", "9", "10", "38", "82", "43");
        arr.hidePointer();
        for (int i = 0; i < 4; i++) arr.setCellColor(i, Colors.withAlpha(Colors.TEAL, 0.3));
        arr.setCellColor(4, Colors.withAlpha(Colors.GOLD, 0.5));
        for (int i = 5; i < 7; i++) arr.setCellColor(i, Colors.withAlpha(Colors.RED, 0.2));

        partStatus.setText("Pivot 38 is at its final position! Left < 38 < Right");
        partStatus.setFillColor(Colors.GREEN);
        hold(4.0);

        fadeOutAll(0.6, arr, partStatus, step1, step2, step3, pivotNote);
        hold(0.5);

        CodeBlock qsCode = new CodeBlock(
            "void quick(int start, int end) {\n" +
            "    if (start < end) {\n" +
            "        int iipivot = partition(start, end);\n" +
            "        quick(start, iipivot - 1);\n" +
            "        quick(iipivot + 1, end);\n" +
            "    }\n" +
            "}", 22
        );
        qsCode.setPosition(0, -120);
        play(new RevealCode(qsCode, 4.0));
        hold(1.5);

        LaTeXMob avgCase = new LaTeXMob("\\text{Average: } O(n \\log n)")
                .setSize(28).setLatexColor(Colors.GREEN);
        avgCase.setPosition(-250, 60);
        play(new Write(avgCase, 1.0));
        hold(0.5);

        LaTeXMob worstCase = new LaTeXMob("\\text{Worst: } O(n^2)")
                .setSize(28).setLatexColor(Colors.RED);
        worstCase.setPosition(250, 60);
        play(new Write(worstCase, 1.0));
        hold(0.5);

        TextMob worstNote = new TextMob("Worst case when pivot is always min/max (already sorted input)")
                .setFontSize(18).setFillColor(Colors.LIGHT_GRAY);
        worstNote.setPosition(0, 110);
        play(new Write(worstNote, 1.2));

        hold(5.0);

        fadeOutAll(0.8, heading, qsCode, avgCase, worstCase, worstNote);
        hold(1.0);
    }


    // ╔══════════════════════════════════════════════════════════╗
    // ║  SECTION 11: MERGE SORT                                 ║
    // ╚══════════════════════════════════════════════════════════╝
    private void buildMergeSort() {

        TextMob heading = sectionTitle("Merge Sort");
        heading.setFillColor(Colors.ORANGE);
        play(new Write(heading, 1.5));
        hold(0.5);

        TextMob desc1 = new TextMob("Divide-and-Conquer approach")
                .setFontSize(26).setFillColor(Colors.WHITE);
        desc1.setPosition(0, -380);
        play(new Write(desc1, 1.0));
        hold(0.5);

        TextMob s1 = new TextMob("1. Divide the array into two halves")
                .setFontSize(22).setFillColor(Colors.TEAL);
        s1.setPosition(-400, -340);
        play(new Write(s1, 1.0));
        hold(0.3);

        TextMob s2 = new TextMob("2. Sort each half recursively")
                .setFontSize(22).setFillColor(Colors.GREEN);
        s2.setPosition(-400, -305);
        play(new Write(s2, 1.0));
        hold(0.3);

        TextMob s3 = new TextMob("3. Merge the two sorted sequences")
                .setFontSize(22).setFillColor(Colors.GOLD);
        s3.setPosition(-400, -270);
        play(new Write(s3, 1.0));
        hold(1.5);

        ArrayMob level0 = new ArrayMob("38", "27", "43", "3", "9", "82", "10");
        level0.setCellSize(65, 40);
        level0.setShowIndices(false);
        level0.setPosition(0, -180);
        play(new FadeIn(level0, 1.0));
        hold(1.5);

        ArrayMob left1 = new ArrayMob("38", "27", "43", "3");
        left1.setCellSize(55, 35);
        left1.setShowIndices(false);
        left1.setPosition(-200, -110);
        left1.setStrokeColor(Colors.TEAL);

        ArrayMob right1 = new ArrayMob("9", "82", "10");
        right1.setCellSize(55, 35);
        right1.setShowIndices(false);
        right1.setPosition(200, -110);
        right1.setStrokeColor(Colors.ORANGE);

        ArrowMob arrowL = new ArrowMob(-50, 10, -180, 35);
        arrowL.setStrokeColor(Colors.TEAL);
        arrowL.setPosition(0, -180);
        ArrowMob arrowR = new ArrowMob(50, 10, 180, 35);
        arrowR.setStrokeColor(Colors.ORANGE);
        arrowR.setPosition(0, -180);

        play(new DrawArrow(arrowL, 1.0), new DrawArrow(arrowR, 1.0));
        play(new FadeIn(left1, 1.0), new FadeIn(right1, 1.0));
        hold(1.5);

        ArrayMob ll = new ArrayMob("38", "27");
        ll.setCellSize(45, 30); ll.setShowIndices(false); ll.setPosition(-320, -40);
        ArrayMob lr = new ArrayMob("43", "3");
        lr.setCellSize(45, 30); lr.setShowIndices(false); lr.setPosition(-100, -40);
        ArrayMob rl = new ArrayMob("9", "82");
        rl.setCellSize(45, 30); rl.setShowIndices(false); rl.setPosition(130, -40);
        ArrayMob rr = new ArrayMob("10");
        rr.setCellSize(45, 30); rr.setShowIndices(false); rr.setPosition(300, -40);

        play(new FadeIn(ll, 0.8), new FadeIn(lr, 0.8),
             new FadeIn(rl, 0.8), new FadeIn(rr, 0.8));
        hold(1.5);

        TextMob mergeLabel = new TextMob("Merge phase:")
                .setFontSize(24).setBold().setFillColor(Colors.GOLD);
        mergeLabel.setPosition(0, 20);
        play(new Write(mergeLabel, 1.0));
        hold(0.5);

        ArrayMob sorted_ll = new ArrayMob("27", "38");
        sorted_ll.setCellSize(45, 30); sorted_ll.setShowIndices(false);
        sorted_ll.setPosition(-320, 70);
        for (int i = 0; i < 2; i++) sorted_ll.setCellColor(i, Colors.withAlpha(Colors.GREEN, 0.25));

        ArrayMob sorted_lr = new ArrayMob("3", "43");
        sorted_lr.setCellSize(45, 30); sorted_lr.setShowIndices(false);
        sorted_lr.setPosition(-100, 70);
        for (int i = 0; i < 2; i++) sorted_lr.setCellColor(i, Colors.withAlpha(Colors.GREEN, 0.25));

        ArrayMob sorted_rl = new ArrayMob("9", "82");
        sorted_rl.setCellSize(45, 30); sorted_rl.setShowIndices(false);
        sorted_rl.setPosition(130, 70);
        for (int i = 0; i < 2; i++) sorted_rl.setCellColor(i, Colors.withAlpha(Colors.GREEN, 0.25));

        ArrayMob sorted_rr = new ArrayMob("10");
        sorted_rr.setCellSize(45, 30); sorted_rr.setShowIndices(false);
        sorted_rr.setPosition(300, 70);
        sorted_rr.setCellColor(0, Colors.withAlpha(Colors.GREEN, 0.25));

        play(new FadeIn(sorted_ll, 0.8), new FadeIn(sorted_lr, 0.8),
             new FadeIn(sorted_rl, 0.8), new FadeIn(sorted_rr, 0.8));
        hold(1.5);

        ArrayMob sorted_left = new ArrayMob("3", "27", "38", "43");
        sorted_left.setCellSize(50, 35); sorted_left.setShowIndices(false);
        sorted_left.setPosition(-200, 140);
        for (int i = 0; i < 4; i++) sorted_left.setCellColor(i, Colors.withAlpha(Colors.TEAL, 0.3));

        ArrayMob sorted_right = new ArrayMob("9", "10", "82");
        sorted_right.setCellSize(50, 35); sorted_right.setShowIndices(false);
        sorted_right.setPosition(200, 140);
        for (int i = 0; i < 3; i++) sorted_right.setCellColor(i, Colors.withAlpha(Colors.ORANGE, 0.3));

        play(new FadeIn(sorted_left, 1.0), new FadeIn(sorted_right, 1.0));
        hold(1.5);

        ArrayMob finalArr = new ArrayMob("3", "9", "10", "27", "38", "43", "82");
        finalArr.setCellSize(65, 40); finalArr.setShowIndices(false);
        finalArr.setPosition(0, 220);
        for (int i = 0; i < 7; i++) finalArr.setCellColor(i, Colors.withAlpha(Colors.GREEN, 0.4));
        play(new FadeIn(finalArr, 1.0));

        TextMob sortedLabel = new TextMob("Sorted!")
                .setFontSize(24).setBold().setFillColor(Colors.GREEN);
        sortedLabel.setPosition(0, 270);
        play(new Write(sortedLabel, 0.8));
        hold(1.0);

        LaTeXMob mergeComplexity = new LaTeXMob("O(n \\log n) \\text{ — always}")
                .setSize(28).setLatexColor(Colors.GREEN);
        mergeComplexity.setPosition(0, 320);
        play(new Write(mergeComplexity, 1.0));
        hold(0.5);

        TextMob note = new TextMob("Not efficient in practice due to array copying")
                .setFontSize(18).setFillColor(Colors.GRAY);
        note.setPosition(0, 360);
        play(new FadeIn(note, 0.8));

        hold(5.0);

        clear();
        hold(1.0);
    }


    // ╔══════════════════════════════════════════════════════════╗
    // ║  SECTION 12: COUNTING SORT                              ║
    // ╚══════════════════════════════════════════════════════════╝
    private void buildCountingSort() {

        TextMob heading = sectionTitle("Counting Sort");
        heading.setFillColor(Colors.ORANGE);
        play(new Write(heading, 1.5));
        hold(0.5);

        TextMob s1 = new TextMob("1. Bucketize elements")
                .setFontSize(24).setFillColor(Colors.TEAL);
        s1.setPosition(-400, -370);
        play(new Write(s1, 0.8));
        hold(0.3);

        TextMob s2 = new TextMob("2. Count elements in each bucket")
                .setFontSize(24).setFillColor(Colors.GREEN);
        s2.setPosition(-400, -335);
        play(new Write(s2, 0.8));
        hold(0.3);

        TextMob s3 = new TextMob("3. Compute prefix sums")
                .setFontSize(24).setFillColor(Colors.GOLD);
        s3.setPosition(-400, -300);
        play(new Write(s3, 0.8));
        hold(0.3);

        TextMob s4 = new TextMob("4. Place elements in output array")
                .setFontSize(24).setFillColor(Colors.ORANGE);
        s4.setPosition(-400, -265);
        play(new Write(s4, 0.8));
        hold(1.5);

        ArrayMob original = new ArrayMob("4", "1", "4", "9", "11", "7", "8", "1", "3", "4");
        original.setCellSize(55, 40);
        original.setShowIndices(false);
        original.setLabel("Original array:");
        original.setPosition(0, -180);
        play(new FadeIn(original, 1.0));
        hold(1.5);

        TextMob countLabel = new TextMob("Count each value:")
                .setFontSize(20).setFillColor(Colors.TEAL);
        countLabel.setPosition(0, -120);
        play(new Write(countLabel, 0.8));
        hold(0.5);

        ArrayMob countArr = new ArrayMob("2", "0", "1", "3", "0", "0", "1", "1", "1", "0", "1");
        countArr.setCellSize(45, 35);
        countArr.setLabel("Counts [1..11]:");
        countArr.setPosition(0, -60);
        int[] counts = {2, 0, 1, 3, 0, 0, 1, 1, 1, 0, 1};
        for (int i = 0; i < 11; i++) {
            if (counts[i] > 0) {
                countArr.setCellColor(i, Colors.withAlpha(Colors.TEAL, 0.3));
            }
        }
        play(new FadeIn(countArr, 1.2));
        hold(2.0);

        TextMob prefixLabel = new TextMob("Prefix sum (starting indices):")
                .setFontSize(20).setFillColor(Colors.GOLD);
        prefixLabel.setPosition(0, 10);
        play(new Write(prefixLabel, 0.8));
        hold(0.5);

        ArrayMob prefixArr = new ArrayMob("0", "2", "2", "3", "6", "6", "6", "7", "8", "9", "9");
        prefixArr.setCellSize(45, 35);
        prefixArr.setLabel("Starting index:");
        prefixArr.setPosition(0, 70);
        play(new FadeIn(prefixArr, 1.2));
        hold(2.0);

        TextMob outputLabel = new TextMob("Output (sorted):")
                .setFontSize(20).setFillColor(Colors.GREEN);
        outputLabel.setPosition(0, 130);
        play(new Write(outputLabel, 0.8));
        hold(0.5);

        ArrayMob outputArr = new ArrayMob("1", "1", "3", "4", "4", "4", "7", "8", "9", "11");
        outputArr.setCellSize(55, 40);
        outputArr.setShowIndices(false);
        outputArr.setPosition(0, 190);
        for (int i = 0; i < 10; i++) {
            outputArr.setCellColor(i, Colors.withAlpha(Colors.GREEN, 0.3));
        }
        play(new FadeIn(outputArr, 1.2));
        hold(2.0);

        TextMob radixNote = new TextMob("Radix Sort: Generalization — sort digit by digit (LSD first)")
                .setFontSize(20).setFillColor(Colors.PURPLE);
        radixNote.setPosition(0, 260);
        play(new Write(radixNote, 1.5));
        hold(0.5);

        LaTeXMob radixComplexity = new LaTeXMob("O(P \\times (N + B)) \\text{  where P=passes, N=elements, B=buckets}")
                .setSize(22).setLatexColor(Colors.PURPLE);
        radixComplexity.setPosition(0, 310);
        play(new Write(radixComplexity, 1.5));

        hold(5.0);

        clear();
        hold(1.0);
    }


    // ╔══════════════════════════════════════════════════════════╗
    // ║  SECTION 13: SUMMARY                                    ║
    // ╚══════════════════════════════════════════════════════════╝
    private void buildSummary() {

        TextMob heading = sectionTitle("Summary");
        play(new Write(heading, 1.5));
        hold(1.0);

        double startY = -340;
        double spacing = 80;

        ArrayMob arrVis = new ArrayMob("", "", "", "", "", "", "");
        arrVis.setCellSize(40, 25);
        arrVis.setShowIndices(false);
        arrVis.setPosition(-500, startY);
        for (int i = 0; i < 7; i++) arrVis.setCellColor(i, Colors.withAlpha(Colors.TEAL, 0.3));
        play(new FadeIn(arrVis, 0.6));

        TextMob arrLabel = new TextMob("Array — contiguous, O(1) random access, fixed size")
                .setFontSize(22).setFillColor(Colors.WHITE);
        arrLabel.setPosition(100, startY);
        play(new Write(arrLabel, 1.2));
        hold(0.5);

        TextMob llVis = new TextMob("[■]→[■]→[■]→[■]→null")
                .setFontSize(18).setFillColor(Colors.BLUE).setFontFamily("Monospace");
        llVis.setPosition(-500, startY + spacing);
        play(new FadeIn(llVis, 0.6));

        TextMob llLabel = new TextMob("Linked List — dynamic size, O(n) access, O(1) insert at head")
                .setFontSize(22).setFillColor(Colors.WHITE);
        llLabel.setPosition(100, startY + spacing);
        play(new Write(llLabel, 1.2));
        hold(0.5);

        TextMob stackVis = new TextMob("[TOP]  LIFO")
                .setFontSize(18).setFillColor(Colors.GREEN).setFontFamily("Monospace");
        stackVis.setPosition(-500, startY + 2 * spacing);
        play(new FadeIn(stackVis, 0.6));

        TextMob stackLabel = new TextMob("Stack — Last In, First Out (push/pop at top)")
                .setFontSize(22).setFillColor(Colors.WHITE);
        stackLabel.setPosition(100, startY + 2 * spacing);
        play(new Write(stackLabel, 1.0));
        hold(0.5);

        TextMob queueVis = new TextMob("FIFO ← [■][■][■] ←")
                .setFontSize(18).setFillColor(Colors.ORANGE).setFontFamily("Monospace");
        queueVis.setPosition(-500, startY + 3 * spacing);
        play(new FadeIn(queueVis, 0.6));

        TextMob queueLabel = new TextMob("Queue — First In, First Out (enqueue/dequeue)")
                .setFontSize(22).setFillColor(Colors.WHITE);
        queueLabel.setPosition(100, startY + 3 * spacing);
        play(new Write(queueLabel, 1.0));
        hold(0.5);

        TextMob treeVis = new TextMob("     (o)\n    / \\\n  (o) (o)")
                .setFontSize(14).setFillColor(Colors.GOLD).setFontFamily("Monospace");
        treeVis.setPosition(-500, startY + 4 * spacing);
        play(new FadeIn(treeVis, 0.6));

        TextMob treeLabel = new TextMob("Tree — hierarchical, BST gives O(log n) search")
                .setFontSize(22).setFillColor(Colors.WHITE);
        treeLabel.setPosition(100, startY + 4 * spacing);
        play(new Write(treeLabel, 1.0));
        hold(0.5);

        TextMob hashVis = new TextMob("[0]→  [1]→■  [2]→■→■")
                .setFontSize(16).setFillColor(Colors.PURPLE).setFontFamily("Monospace");
        hashVis.setPosition(-500, startY + 5 * spacing);
        play(new FadeIn(hashVis, 0.6));

        TextMob hashLabel = new TextMob("Hash Table — O(1) average lookup via hashing")
                .setFontSize(22).setFillColor(Colors.WHITE);
        hashLabel.setPosition(100, startY + 5 * spacing);
        play(new Write(hashLabel, 1.0));
        hold(0.5);

        TextMob graphVis = new TextMob("(o)─(o)─(o)\n |     |\n(o)─(o)")
                .setFontSize(14).setFillColor(Colors.RED).setFontFamily("Monospace");
        graphVis.setPosition(-500, startY + 6 * spacing);
        play(new FadeIn(graphVis, 0.6));

        TextMob graphLabel = new TextMob("Graph — vertices + edges, models relationships")
                .setFontSize(22).setFillColor(Colors.WHITE);
        graphLabel.setPosition(100, startY + 6 * spacing);
        play(new Write(graphLabel, 1.0));

        hold(2.0);

        TextMob takeaway = new TextMob("Properties of the problem dictate both the algorithm and the data structure.")
                .setFontSize(26).setBold().setFillColor(Colors.GOLD);
        takeaway.setPosition(0, startY + 7 * spacing + 30);
        play(new Write(takeaway, 2.0));

        hold(5.0);

        // ─── Final outro ───
        clear();
        hold(0.5);

        TextMob thanks = new TextMob("Arrays")
                .setFontSize(72).setBold().setFillColor(Colors.BLUE);
        thanks.setPosition(0, -80);
        play(new FadeIn(thanks, 2.0));
        hold(0.5);

        TextMob by = new TextMob("Prof. Rupesh Nasre — IIT Madras")
                .setFontSize(28).setFillColor(Colors.GOLD);
        by.setPosition(0, 0);
        play(new Write(by, 1.5));
        hold(0.3);

        TextMob animated = new TextMob("Animated with LecViz — CS5013 Project")
                .setFontSize(20).setFillColor(Colors.GRAY);
        animated.setPosition(0, 50);
        play(new FadeIn(animated, 1.0));

        TextMob team = new TextMob("Karthik (CS23B018) & Tejaswi (CS23B023)")
                .setFontSize(18).setFillColor(Colors.DARK_GRAY);
        team.setPosition(0, 85);
        play(new FadeIn(team, 1.0));

        hold(6.0);

        fadeOutAll(1.5, thanks, by, animated, team);
        hold(2.0);
    }
}
