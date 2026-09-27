package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Scene;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Vec2;

/**
 * PDS Lecture: Arrays in C — comprehensive coverage.
 * Covers declaration, memory layout, pointer arithmetic, 2D arrays,
 * linear search, binary search, bubble sort, selection sort,
 * insertion sort, complexity comparison, and common pitfalls.
 */
public class PDSArrayScene extends Scene {

    @Override
    public void construct() {

        // ================================================================
        // SECTION 1: Title Card
        // ================================================================
        TextMob title = new TextMob("Arrays in C")
                .setFontSize(72).setBold().setFillColor(Colors.BLUE);
        title.setPosition(0, -120);

        TextMob subtitle = new TextMob("Programming and Data Structures")
                .setFontSize(30).setFillColor(Colors.LIGHT_GRAY);
        subtitle.setPosition(0, -30);

        TextMob credit = new TextMob("Prof. Rupesh Nasre — IIT Madras")
                .setFontSize(24).setFillColor(Colors.GRAY);
        credit.setPosition(0, 30);

        TextMob courseCode = new TextMob("CS1100 / CS2100")
                .setFontSize(20).setFillColor(Colors.DARK_GRAY);
        courseCode.setPosition(0, 75);

        play(new FadeIn(title, 1.2));
        play(new Write(subtitle, 1.0));
        play(new FadeIn(credit, 0.6));
        play(new FadeIn(courseCode, 0.4));
        hold(2.5);

        play(new FadeOut(title, 0.5), new FadeOut(subtitle, 0.5),
             new FadeOut(credit, 0.5), new FadeOut(courseCode, 0.5));

        // ================================================================
        // SECTION 2: What is an Array?
        // ================================================================
        TextMob heading = new TextMob("What is an Array?")
                .setFontSize(48).setBold().setFillColor(Colors.BLUE);
        heading.setPosition(0, -420);
        play(new Write(heading, 0.8));

        TextMob def1 = new TextMob("A collection of elements of the SAME TYPE stored in CONTIGUOUS memory")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        def1.setPosition(0, -350);
        play(new Write(def1, 1.2));
        hold(1.0);

        // Show array with memory addresses
        ArrayMob arr = new ArrayMob("10", "25", "3", "42", "17", "8");
        arr.setLabel("int arr[6]");
        arr.setPosition(0, -180);
        play(new FadeIn(arr, 1.0));
        hold(0.8);

        // Show memory addresses below the array
        TextMob addrLabel = new TextMob("Memory Addresses (each int = 4 bytes):")
                .setFontSize(20).setFillColor(Colors.TEAL);
        addrLabel.setPosition(0, -80);
        play(new FadeIn(addrLabel, 0.5));

        String[] addrs = {"0x1000", "0x1004", "0x1008", "0x100C", "0x1010", "0x1014"};
        TextMob[] addrMobs = new TextMob[6];
        for (int i = 0; i < 6; i++) {
            addrMobs[i] = new TextMob(addrs[i])
                    .setFontSize(16).setFillColor(Colors.TEAL).setFontFamily("Monospace");
            addrMobs[i].setPosition(-245 + i * 70 + 35, -40);
            play(new FadeIn(addrMobs[i], 0.2));
        }
        hold(1.5);

        // Highlight contiguous nature
        TextMob contiguous = new TextMob("Contiguous = no gaps between elements in memory")
                .setFontSize(22).setFillColor(Colors.GOLD);
        contiguous.setPosition(0, 20);
        play(new Write(contiguous, 1.0));
        hold(2.0);

        // Clean up section 2
        play(new FadeOut(arr, 0.3), new FadeOut(heading, 0.3), new FadeOut(def1, 0.3),
             new FadeOut(addrLabel, 0.3), new FadeOut(contiguous, 0.3));
        for (TextMob a : addrMobs) play(new FadeOut(a, 0.1));

        // ================================================================
        // SECTION 3: Declaration and Initialization
        // ================================================================
        TextMob declHeading = new TextMob("Declaration & Initialization")
                .setFontSize(48).setBold().setFillColor(Colors.BLUE);
        declHeading.setPosition(0, -420);
        play(new Write(declHeading, 0.8));

        CodeBlock declCode = new CodeBlock(
            "// Method 1: Declare with size\n" +
            "int arr[5];\n" +
            "\n" +
            "// Method 2: Initialize at declaration\n" +
            "int arr[] = {1, 2, 3, 4, 5};\n" +
            "\n" +
            "// Method 3: Partial initialization\n" +
            "int arr[5] = {1, 2};  // rest are 0\n" +
            "\n" +
            "// Method 4: All zeros\n" +
            "int arr[5] = {0};", 20
        );
        declCode.setPosition(-250, -150);
        play(new RevealCode(declCode, 4.0));
        hold(1.0);

        // Show what partial init looks like
        ArrayMob partialArr = new ArrayMob("1", "2", "0", "0", "0");
        partialArr.setLabel("int arr[5] = {1, 2};");
        partialArr.setPosition(400, -150);
        play(new FadeIn(partialArr, 0.8));
        partialArr.setCellColor(0, Colors.withAlpha(Colors.GREEN, 0.3));
        partialArr.setCellColor(1, Colors.withAlpha(Colors.GREEN, 0.3));
        partialArr.setCellColor(2, Colors.withAlpha(Colors.GRAY, 0.2));
        partialArr.setCellColor(3, Colors.withAlpha(Colors.GRAY, 0.2));
        partialArr.setCellColor(4, Colors.withAlpha(Colors.GRAY, 0.2));

        TextMob partialNote = new TextMob("Green = explicitly set, Gray = auto-initialized to 0")
                .setFontSize(18).setFillColor(Colors.LIGHT_GRAY);
        partialNote.setPosition(400, -60);
        play(new FadeIn(partialNote, 0.5));
        hold(2.5);

        play(new FadeOut(declCode, 0.3), new FadeOut(partialArr, 0.3),
             new FadeOut(declHeading, 0.3), new FadeOut(partialNote, 0.3));

        // ================================================================
        // SECTION 4: Accessing Elements — Pointer Arithmetic
        // ================================================================
        TextMob accessHeading = new TextMob("Accessing Elements: Pointer Arithmetic")
                .setFontSize(44).setBold().setFillColor(Colors.BLUE);
        accessHeading.setPosition(0, -420);
        play(new Write(accessHeading, 0.8));

        TextMob formula = new TextMob("Address of arr[i] = base_address + i * sizeof(type)")
                .setFontSize(26).setFillColor(Colors.GOLD);
        formula.setPosition(0, -350);
        play(new Write(formula, 1.2));
        hold(1.0);

        ArrayMob accessArr = new ArrayMob("10", "25", "3", "42", "17");
        accessArr.setLabel("int arr[5]    base = 0x1000");
        accessArr.setPosition(0, -200);
        play(new FadeIn(accessArr, 0.8));

        // Step through each access
        TextMob accessCalc = new TextMob("")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        accessCalc.setPosition(0, -80);
        add(accessCalc);

        String[] accessExamples = {
            "arr[0] = *(0x1000 + 0*4) = *(0x1000) = 10",
            "arr[1] = *(0x1000 + 1*4) = *(0x1004) = 25",
            "arr[2] = *(0x1000 + 2*4) = *(0x1008) = 3",
            "arr[3] = *(0x1000 + 3*4) = *(0x100C) = 42",
            "arr[4] = *(0x1000 + 4*4) = *(0x1010) = 17"
        };
        for (int i = 0; i < 5; i++) {
            accessArr.clearHighlights();
            accessArr.highlight(i);
            accessArr.setPointer(i, "i=" + i);
            accessCalc.setText(accessExamples[i]);
            hold(1.5);
        }
        accessArr.clearHighlights();
        accessArr.hidePointer();

        TextMob o1note = new TextMob("Random access in O(1) — this is the key advantage of arrays!")
                .setFontSize(24).setFillColor(Colors.GREEN);
        o1note.setPosition(0, -10);
        play(new Write(o1note, 1.0));
        hold(2.0);

        play(new FadeOut(accessArr, 0.3), new FadeOut(accessHeading, 0.3),
             new FadeOut(formula, 0.3), new FadeOut(accessCalc, 0.3),
             new FadeOut(o1note, 0.3));

        // ================================================================
        // SECTION 5: Array as Function Argument
        // ================================================================
        TextMob funcHeading = new TextMob("Passing Arrays to Functions")
                .setFontSize(44).setBold().setFillColor(Colors.BLUE);
        funcHeading.setPosition(0, -420);
        play(new Write(funcHeading, 0.8));

        TextMob decayNote = new TextMob("Arrays decay to pointers when passed to functions!")
                .setFontSize(24).setFillColor(Colors.RED);
        decayNote.setPosition(0, -350);
        play(new Write(decayNote, 1.0));
        hold(0.8);

        CodeBlock funcCode = new CodeBlock(
            "// These are EQUIVALENT:\n" +
            "void printArray(int arr[], int n);\n" +
            "void printArray(int *arr, int n);\n" +
            "\n" +
            "// arr is a POINTER, not a copy!\n" +
            "void modify(int arr[], int n) {\n" +
            "    arr[0] = 999; // changes original!\n" +
            "}\n" +
            "\n" +
            "int main() {\n" +
            "    int a[] = {1, 2, 3};\n" +
            "    modify(a, 3);\n" +
            "    // a[0] is now 999\n" +
            "}", 18
        );
        funcCode.setPosition(0, -50);
        play(new RevealCode(funcCode, 4.0));

        funcCode.highlightLine(1);
        funcCode.highlightLine(2);
        hold(1.5);
        funcCode.clearHighlights();

        funcCode.highlightLine(6);
        TextMob warnNote = new TextMob("Modifying arr inside the function modifies the original array!")
                .setFontSize(22).setFillColor(Colors.ORANGE);
        warnNote.setPosition(0, 300);
        play(new FadeIn(warnNote, 0.5));
        hold(2.5);

        play(new FadeOut(funcCode, 0.3), new FadeOut(funcHeading, 0.3),
             new FadeOut(decayNote, 0.3), new FadeOut(warnNote, 0.3));

        // ================================================================
        // SECTION 6: 2D Arrays — Row-Major Layout
        // ================================================================
        TextMob twoDHeading = new TextMob("2D Arrays: Row-Major Layout")
                .setFontSize(44).setBold().setFillColor(Colors.BLUE);
        twoDHeading.setPosition(0, -420);
        play(new Write(twoDHeading, 0.8));

        CodeBlock twoDCode = new CodeBlock(
            "int mat[2][3] = {\n" +
            "    {1, 2, 3},\n" +
            "    {4, 5, 6}\n" +
            "};", 22
        );
        twoDCode.setPosition(-400, -280);
        play(new RevealCode(twoDCode, 1.5));

        // Show as grid (matrix)
        TextMob gridLabel = new TextMob("Logical View (Matrix):")
                .setFontSize(22).setFillColor(Colors.TEAL);
        gridLabel.setPosition(200, -310);
        play(new FadeIn(gridLabel, 0.3));

        // Row 0
        ArrayMob row0 = new ArrayMob("1", "2", "3");
        row0.setLabel("Row 0");
        row0.setPosition(200, -230);
        row0.setShowIndices(false);
        play(new FadeIn(row0, 0.5));

        // Row 1
        ArrayMob row1 = new ArrayMob("4", "5", "6");
        row1.setLabel("Row 1");
        row1.setPosition(200, -130);
        row1.setShowIndices(false);
        play(new FadeIn(row1, 0.5));
        hold(1.5);

        // Show linearized memory layout
        TextMob memLabel = new TextMob("Physical View (Memory — Row-Major Order):")
                .setFontSize(22).setFillColor(Colors.GOLD);
        memLabel.setPosition(0, -20);
        play(new FadeIn(memLabel, 0.3));

        ArrayMob linearArr = new ArrayMob("1", "2", "3", "4", "5", "6");
        linearArr.setLabel("Contiguous in memory");
        linearArr.setPosition(0, 60);
        play(new FadeIn(linearArr, 0.8));

        // Color code rows
        linearArr.setCellColor(0, Colors.withAlpha(Colors.TEAL, 0.3));
        linearArr.setCellColor(1, Colors.withAlpha(Colors.TEAL, 0.3));
        linearArr.setCellColor(2, Colors.withAlpha(Colors.TEAL, 0.3));
        linearArr.setCellColor(3, Colors.withAlpha(Colors.ORANGE, 0.3));
        linearArr.setCellColor(4, Colors.withAlpha(Colors.ORANGE, 0.3));
        linearArr.setCellColor(5, Colors.withAlpha(Colors.ORANGE, 0.3));

        TextMob rowColors = new TextMob("Teal = Row 0,  Orange = Row 1")
                .setFontSize(18).setFillColor(Colors.LIGHT_GRAY);
        rowColors.setPosition(0, 140);
        play(new FadeIn(rowColors, 0.4));

        TextMob addressFormula = new TextMob("Address of mat[i][j] = base + (i * COLS + j) * sizeof(int)")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        addressFormula.setPosition(0, 200);
        play(new Write(addressFormula, 1.2));
        hold(3.0);

        play(new FadeOut(twoDHeading, 0.3), new FadeOut(twoDCode, 0.3),
             new FadeOut(gridLabel, 0.3), new FadeOut(row0, 0.3), new FadeOut(row1, 0.3),
             new FadeOut(memLabel, 0.3), new FadeOut(linearArr, 0.3),
             new FadeOut(rowColors, 0.3), new FadeOut(addressFormula, 0.3));

        // ================================================================
        // SECTION 7: Linear Search
        // ================================================================
        TextMob lsHeading = new TextMob("Linear Search")
                .setFontSize(48).setBold().setFillColor(Colors.GOLD);
        lsHeading.setPosition(0, -420);
        play(new Write(lsHeading, 0.8));

        TextMob lsDesc = new TextMob("Check each element one by one until target is found (or array ends)")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        lsDesc.setPosition(0, -360);
        play(new Write(lsDesc, 1.0));

        ArrayMob lsArr = new ArrayMob("14", "7", "23", "9", "42", "3", "18");
        lsArr.setLabel("Unsorted array");
        lsArr.setPosition(0, -220);
        play(new FadeIn(lsArr, 0.7));

        TextMob target = new TextMob("Target: 42")
                .setFontSize(28).setFillColor(Colors.RED);
        target.setPosition(0, -130);
        play(new FadeIn(target, 0.5));

        TextMob lsStatus = new TextMob("")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        lsStatus.setPosition(0, -60);
        add(lsStatus);

        int[] lsData = {14, 7, 23, 9, 42, 3, 18};
        int searchTarget = 42;
        for (int i = 0; i < lsData.length; i++) {
            lsArr.clearHighlights();
            lsArr.highlight(i);
            lsArr.setPointer(i, "i=" + i);
            if (lsData[i] == searchTarget) {
                lsArr.setCellColor(i, Colors.withAlpha(Colors.GREEN, 0.4));
                lsStatus.setText("arr[" + i + "] = " + lsData[i] + " == 42  FOUND!");
                lsStatus.setFillColor(Colors.GREEN);
                hold(2.0);
                break;
            } else {
                lsArr.setCellColor(i, Colors.withAlpha(Colors.RED, 0.2));
                lsStatus.setText("arr[" + i + "] = " + lsData[i] + " != 42  keep searching...");
                hold(0.8);
            }
        }

        TextMob lsComplexity = new TextMob("Time: O(n) worst/average  |  O(1) best  |  Works on unsorted arrays")
                .setFontSize(22).setFillColor(Colors.TEAL);
        lsComplexity.setPosition(0, 10);
        play(new FadeIn(lsComplexity, 0.5));
        hold(2.0);

        play(new FadeOut(lsArr, 0.3), new FadeOut(lsHeading, 0.3), new FadeOut(lsDesc, 0.3),
             new FadeOut(target, 0.3), new FadeOut(lsStatus, 0.3), new FadeOut(lsComplexity, 0.3));

        // ================================================================
        // SECTION 8: Binary Search
        // ================================================================
        TextMob bsHeading = new TextMob("Binary Search")
                .setFontSize(48).setBold().setFillColor(Colors.GOLD);
        bsHeading.setPosition(0, -420);
        play(new Write(bsHeading, 0.8));

        TextMob bsReq = new TextMob("Prerequisite: Array MUST be sorted!")
                .setFontSize(24).setFillColor(Colors.RED);
        bsReq.setPosition(0, -360);
        play(new Write(bsReq, 0.8));

        ArrayMob bsArr = new ArrayMob("3", "7", "11", "18", "25", "34", "42", "56", "71");
        bsArr.setLabel("Sorted array");
        bsArr.setCellSize(65, 50);
        bsArr.setPosition(0, -230);
        play(new FadeIn(bsArr, 0.8));

        TextMob bsTarget = new TextMob("Target: 34")
                .setFontSize(28).setFillColor(Colors.RED);
        bsTarget.setPosition(0, -140);
        play(new FadeIn(bsTarget, 0.5));

        TextMob bsStep = new TextMob("")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        bsStep.setPosition(0, -70);
        add(bsStep);

        // Binary search for 34 in {3,7,11,18,25,34,42,56,71}
        int low = 0, high = 8, bsTargetVal = 34;
        int[] bsData = {3, 7, 11, 18, 25, 34, 42, 56, 71};

        // Pass 1: low=0 high=8 mid=4 -> 25 < 34 -> go right
        int mid = (low + high) / 2; // 4
        bsArr.clearHighlights();
        bsArr.clearCellColors();
        bsArr.setCellColor(low, Colors.withAlpha(Colors.TEAL, 0.3));
        bsArr.setCellColor(high, Colors.withAlpha(Colors.TEAL, 0.3));
        bsArr.setCellColor(mid, Colors.withAlpha(Colors.ORANGE, 0.4));
        bsStep.setText("low=0, high=8, mid=4 -> arr[4]=25 < 34 -> search RIGHT half");
        hold(2.0);

        // Pass 2: low=5 high=8 mid=6 -> 42 > 34 -> go left
        low = 5;
        mid = (low + high) / 2; // 6
        bsArr.clearCellColors();
        // Gray out eliminated elements
        for (int i = 0; i < 5; i++) bsArr.setCellColor(i, Colors.withAlpha(Colors.DARK_GRAY, 0.3));
        bsArr.setCellColor(low, Colors.withAlpha(Colors.TEAL, 0.3));
        bsArr.setCellColor(high, Colors.withAlpha(Colors.TEAL, 0.3));
        bsArr.setCellColor(mid, Colors.withAlpha(Colors.ORANGE, 0.4));
        bsStep.setText("low=5, high=8, mid=6 -> arr[6]=42 > 34 -> search LEFT half");
        hold(2.0);

        // Pass 3: low=5 high=5 mid=5 -> 34 == 34 -> FOUND
        high = 5;
        mid = 5;
        bsArr.clearCellColors();
        for (int i = 0; i < 5; i++) bsArr.setCellColor(i, Colors.withAlpha(Colors.DARK_GRAY, 0.3));
        for (int i = 6; i <= 8; i++) bsArr.setCellColor(i, Colors.withAlpha(Colors.DARK_GRAY, 0.3));
        bsArr.setCellColor(mid, Colors.withAlpha(Colors.GREEN, 0.5));
        bsStep.setText("low=5, high=5, mid=5 -> arr[5]=34 == 34 -> FOUND in 3 steps!");
        bsStep.setFillColor(Colors.GREEN);
        hold(2.5);

        TextMob bsComplexity = new TextMob("Time: O(log n) — halves the search space each step!")
                .setFontSize(24).setFillColor(Colors.TEAL);
        bsComplexity.setPosition(0, 10);
        play(new FadeIn(bsComplexity, 0.5));

        TextMob bsCompare = new TextMob("9 elements: Linear = up to 9 checks, Binary = at most 4 checks")
                .setFontSize(20).setFillColor(Colors.LIGHT_GRAY);
        bsCompare.setPosition(0, 60);
        play(new FadeIn(bsCompare, 0.5));
        hold(2.5);

        play(new FadeOut(bsArr, 0.3), new FadeOut(bsHeading, 0.3), new FadeOut(bsReq, 0.3),
             new FadeOut(bsTarget, 0.3), new FadeOut(bsStep, 0.3),
             new FadeOut(bsComplexity, 0.3), new FadeOut(bsCompare, 0.3));

        // ================================================================
        // SECTION 9: Bubble Sort — Full Sort
        // ================================================================
        TextMob bubbleHeading = new TextMob("Bubble Sort")
                .setFontSize(52).setBold().setFillColor(Colors.GOLD);
        bubbleHeading.setPosition(0, -420);
        play(new Write(bubbleHeading, 0.6));

        TextMob bubbleDesc = new TextMob("Repeatedly swap adjacent elements if out of order")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        bubbleDesc.setPosition(0, -360);
        play(new Write(bubbleDesc, 1.0));

        int[] bubbleData = {42, 17, 3, 25, 8};
        String[] bubbleVals = new String[bubbleData.length];
        for (int i = 0; i < bubbleData.length; i++) bubbleVals[i] = String.valueOf(bubbleData[i]);

        ArrayMob bubbleArr = new ArrayMob(bubbleVals);
        bubbleArr.setLabel("Pass 1");
        bubbleArr.setPosition(0, -230);
        play(new FadeIn(bubbleArr, 0.5));

        TextMob swapNote = new TextMob("")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        swapNote.setPosition(0, -130);
        add(swapNote);

        CodeBlock bubbleCode = new CodeBlock(
            "void bubbleSort(int a[], int n) {\n" +
            "  for (int i = 0; i < n-1; i++)\n" +
            "    for (int j = 0; j < n-i-1; j++)\n" +
            "      if (a[j] > a[j+1])\n" +
            "        swap(&a[j], &a[j+1]);\n" +
            "}", 18
        );
        bubbleCode.setPosition(0, 50);
        play(new RevealCode(bubbleCode, 2.0));
        hold(0.5);

        int bn = bubbleData.length;
        for (int pass = 0; pass < bn - 1; pass++) {
            bubbleArr.setLabel("Pass " + (pass + 1));
            for (int j = 0; j < bn - pass - 1; j++) {
                bubbleArr.clearHighlights();
                bubbleArr.clearCellColors();
                // Gray out already-sorted region
                for (int k = bn - pass; k < bn; k++)
                    bubbleArr.setCellColor(k, Colors.withAlpha(Colors.GREEN, 0.25));
                bubbleArr.highlight(j);
                bubbleArr.highlight(j + 1);
                bubbleCode.clearHighlights();
                bubbleCode.highlightLine(3);

                if (bubbleData[j] > bubbleData[j + 1]) {
                    swapNote.setText(bubbleData[j] + " > " + bubbleData[j + 1] + " -> SWAP");
                    swapNote.setFillColor(Colors.RED);
                    hold(0.5);
                    int tmp = bubbleData[j];
                    bubbleData[j] = bubbleData[j + 1];
                    bubbleData[j + 1] = tmp;
                    bubbleArr.swap(j, j + 1);
                    bubbleCode.clearHighlights();
                    bubbleCode.highlightLine(4);
                    hold(0.4);
                } else {
                    swapNote.setText(bubbleData[j] + " <= " + bubbleData[j + 1] + " -> no swap");
                    swapNote.setFillColor(Colors.GREEN);
                    hold(0.4);
                }
            }
            // Mark the sorted element
            bubbleArr.clearHighlights();
            hold(0.3);
        }

        // Final sorted state
        bubbleArr.clearCellColors();
        for (int i = 0; i < bn; i++)
            bubbleArr.setCellColor(i, Colors.withAlpha(Colors.GREEN, 0.3));
        bubbleArr.setLabel("Sorted!");
        swapNote.setText("Bubble Sort complete");
        swapNote.setFillColor(Colors.GREEN);
        hold(2.0);

        play(new FadeOut(bubbleArr, 0.3), new FadeOut(bubbleHeading, 0.3), new FadeOut(bubbleDesc, 0.3),
             new FadeOut(swapNote, 0.3), new FadeOut(bubbleCode, 0.3));

        // ================================================================
        // SECTION 10: Selection Sort
        // ================================================================
        TextMob selHeading = new TextMob("Selection Sort")
                .setFontSize(52).setBold().setFillColor(Colors.GOLD);
        selHeading.setPosition(0, -420);
        play(new Write(selHeading, 0.6));

        TextMob selDesc = new TextMob("Find the minimum element, swap it to the front")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        selDesc.setPosition(0, -360);
        play(new Write(selDesc, 1.0));

        int[] selData = {29, 10, 14, 37, 13};
        String[] selVals = new String[selData.length];
        for (int i = 0; i < selData.length; i++) selVals[i] = String.valueOf(selData[i]);

        ArrayMob selArr = new ArrayMob(selVals);
        selArr.setLabel("Selection Sort");
        selArr.setPosition(0, -220);
        play(new FadeIn(selArr, 0.5));

        TextMob selStatus = new TextMob("")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        selStatus.setPosition(0, -110);
        add(selStatus);

        int sn = selData.length;
        for (int i = 0; i < sn - 1; i++) {
            int minIdx = i;
            selArr.clearHighlights();
            selArr.clearCellColors();
            // Mark already sorted
            for (int k = 0; k < i; k++)
                selArr.setCellColor(k, Colors.withAlpha(Colors.GREEN, 0.25));

            selArr.highlight(i);
            selArr.setPointer(i, "min");
            selStatus.setText("Pass " + (i + 1) + ": finding minimum from index " + i);
            hold(0.6);

            for (int j = i + 1; j < sn; j++) {
                selArr.highlight(j);
                hold(0.3);
                if (selData[j] < selData[minIdx]) {
                    minIdx = j;
                    selArr.setPointer(j, "min");
                    selStatus.setText("New minimum found: " + selData[j] + " at index " + j);
                    hold(0.4);
                }
                selArr.unhighlight(j);
            }

            if (minIdx != i) {
                selStatus.setText("Swap arr[" + i + "]=" + selData[i] + " with arr[" + minIdx + "]=" + selData[minIdx]);
                selStatus.setFillColor(Colors.ORANGE);
                hold(0.5);
                int tmp = selData[i]; selData[i] = selData[minIdx]; selData[minIdx] = tmp;
                selArr.swap(i, minIdx);
                hold(0.5);
                selStatus.setFillColor(Colors.YELLOW);
            }
        }

        selArr.clearHighlights();
        selArr.clearCellColors();
        selArr.hidePointer();
        for (int i = 0; i < sn; i++)
            selArr.setCellColor(i, Colors.withAlpha(Colors.GREEN, 0.3));
        selArr.setLabel("Sorted!");
        selStatus.setText("Selection Sort complete — O(n^2) always");
        selStatus.setFillColor(Colors.GREEN);
        hold(2.0);

        play(new FadeOut(selArr, 0.3), new FadeOut(selHeading, 0.3),
             new FadeOut(selDesc, 0.3), new FadeOut(selStatus, 0.3));

        // ================================================================
        // SECTION 11: Insertion Sort
        // ================================================================
        TextMob insHeading = new TextMob("Insertion Sort")
                .setFontSize(52).setBold().setFillColor(Colors.GOLD);
        insHeading.setPosition(0, -420);
        play(new Write(insHeading, 0.6));

        TextMob insDesc = new TextMob("Pick element, shift sorted portion right, insert in correct position")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        insDesc.setPosition(0, -360);
        play(new Write(insDesc, 1.2));

        int[] insData = {34, 8, 64, 51, 32, 21};
        String[] insVals = new String[insData.length];
        for (int i = 0; i < insData.length; i++) insVals[i] = String.valueOf(insData[i]);

        ArrayMob insArr = new ArrayMob(insVals);
        insArr.setLabel("Insertion Sort");
        insArr.setPosition(0, -220);
        play(new FadeIn(insArr, 0.5));

        TextMob insStatus = new TextMob("")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        insStatus.setPosition(0, -110);
        add(insStatus);

        int in = insData.length;
        // Element 0 is already "sorted"
        insArr.setCellColor(0, Colors.withAlpha(Colors.TEAL, 0.2));

        for (int i = 1; i < in; i++) {
            int key = insData[i];
            insArr.clearHighlights();
            insArr.setPointer(i, "key=" + key);
            insStatus.setText("Pick key = " + key + " (index " + i + "), insert into sorted portion");
            hold(0.8);

            int j = i - 1;
            while (j >= 0 && insData[j] > key) {
                insData[j + 1] = insData[j];
                insArr.setValue(j + 1, String.valueOf(insData[j]));
                insArr.highlight(j);
                insStatus.setText("Shift " + insData[j] + " right");
                hold(0.4);
                insArr.unhighlight(j);
                j--;
            }
            insData[j + 1] = key;
            insArr.setValue(j + 1, String.valueOf(key));
            insArr.setCellColor(j + 1, Colors.withAlpha(Colors.GREEN, 0.3));
            insStatus.setText("Insert " + key + " at index " + (j + 1));
            hold(0.5);

            // Mark sorted portion
            insArr.clearCellColors();
            for (int k = 0; k <= i; k++)
                insArr.setCellColor(k, Colors.withAlpha(Colors.TEAL, 0.2));
        }

        insArr.clearHighlights();
        insArr.clearCellColors();
        insArr.hidePointer();
        for (int i = 0; i < in; i++)
            insArr.setCellColor(i, Colors.withAlpha(Colors.GREEN, 0.3));
        insArr.setLabel("Sorted!");
        insStatus.setText("Insertion Sort complete — O(n^2) worst, O(n) best (already sorted)");
        insStatus.setFillColor(Colors.GREEN);
        hold(2.0);

        play(new FadeOut(insArr, 0.3), new FadeOut(insHeading, 0.3),
             new FadeOut(insDesc, 0.3), new FadeOut(insStatus, 0.3));

        // ================================================================
        // SECTION 12: Sorting Algorithm Comparison
        // ================================================================
        TextMob compHeading = new TextMob("Sorting Algorithm Comparison")
                .setFontSize(44).setBold().setFillColor(Colors.BLUE);
        compHeading.setPosition(0, -380);
        play(new Write(compHeading, 0.8));

        String[][] compRows = {
            {"Algorithm",      "Best",     "Average",  "Worst",    "Space",  "Stable?"},
            {"Bubble Sort",    "O(n)",     "O(n^2)",   "O(n^2)",   "O(1)",   "Yes"},
            {"Selection Sort", "O(n^2)",   "O(n^2)",   "O(n^2)",   "O(1)",   "No"},
            {"Insertion Sort", "O(n)",     "O(n^2)",   "O(n^2)",   "O(1)",   "Yes"},
            {"Merge Sort",     "O(nlogn)", "O(nlogn)", "O(nlogn)", "O(n)",   "Yes"},
            {"Quick Sort",     "O(nlogn)", "O(nlogn)", "O(n^2)",   "O(logn)","No"},
        };

        double compY = -280;
        TextMob[] compMobs = new TextMob[compRows.length];
        for (int i = 0; i < compRows.length; i++) {
            String line = String.format("%-16s %-10s %-10s %-10s %-8s %-6s",
                    compRows[i][0], compRows[i][1], compRows[i][2],
                    compRows[i][3], compRows[i][4], compRows[i][5]);
            compMobs[i] = new TextMob(line)
                    .setFontSize(i == 0 ? 22 : 20)
                    .setFillColor(i == 0 ? Colors.GOLD : Colors.WHITE)
                    .setFontFamily("Monospace");
            compMobs[i].setPosition(0, compY + i * 45);
            play(new Write(compMobs[i], 0.5));
            hold(0.2);
        }

        TextMob compNote = new TextMob("For PDS: master Bubble, Selection, and Insertion. Know Merge & Quick exist.")
                .setFontSize(20).setFillColor(Colors.TEAL);
        compNote.setPosition(0, compY + compRows.length * 45 + 20);
        play(new FadeIn(compNote, 0.5));
        hold(3.0);

        play(new FadeOut(compHeading, 0.3), new FadeOut(compNote, 0.3));
        for (TextMob m : compMobs) play(new FadeOut(m, 0.1));

        // ================================================================
        // SECTION 13: Common Pitfalls
        // ================================================================
        TextMob pitfallHeading = new TextMob("Common Pitfalls")
                .setFontSize(48).setBold().setFillColor(Colors.RED);
        pitfallHeading.setPosition(0, -380);
        play(new Write(pitfallHeading, 0.8));

        // Pitfall 1: Out of bounds
        CodeBlock oobCode = new CodeBlock(
            "int arr[5] = {1, 2, 3, 4, 5};\n" +
            "printf(\"%d\", arr[5]); // UNDEFINED!\n" +
            "// Valid indices: 0 to 4\n" +
            "// arr[5] accesses memory PAST the array", 20
        );
        oobCode.setPosition(-300, -220);
        play(new RevealCode(oobCode, 2.0));
        oobCode.highlightLine(1);

        TextMob oobWarn = new TextMob("Array index out of bounds — C does NOT check bounds!")
                .setFontSize(24).setFillColor(Colors.RED);
        oobWarn.setPosition(0, -60);
        play(new Write(oobWarn, 1.0));
        hold(2.0);

        play(new FadeOut(oobCode, 0.3), new FadeOut(oobWarn, 0.3));

        // Pitfall 2: Uninitialized
        CodeBlock uninitCode = new CodeBlock(
            "int arr[5];  // NOT initialized!\n" +
            "// arr contains GARBAGE values\n" +
            "// Always initialize: int arr[5] = {0};", 20
        );
        uninitCode.setPosition(-300, -220);
        play(new RevealCode(uninitCode, 1.5));

        ArrayMob garbageArr = new ArrayMob("???", "???", "???", "???", "???");
        garbageArr.setLabel("Uninitialized — garbage values!");
        garbageArr.setPosition(300, -220);
        for (int i = 0; i < 5; i++)
            garbageArr.setCellColor(i, Colors.withAlpha(Colors.RED, 0.2));
        play(new FadeIn(garbageArr, 0.6));
        hold(2.5);

        play(new FadeOut(uninitCode, 0.3), new FadeOut(garbageArr, 0.3),
             new FadeOut(pitfallHeading, 0.3));

        // ================================================================
        // SECTION 14: Summary
        // ================================================================
        TextMob sumTitle = new TextMob("Summary: Arrays in C")
                .setFontSize(52).setBold().setFillColor(Colors.BLUE);
        sumTitle.setPosition(0, -350);
        play(new FadeIn(sumTitle, 0.6));

        String[] points = {
            "Arrays store same-type elements in contiguous memory",
            "O(1) random access via pointer arithmetic: base + i*sizeof(type)",
            "Arrays decay to pointers when passed to functions",
            "2D arrays use row-major layout in memory",
            "Linear Search: O(n) — works on unsorted arrays",
            "Binary Search: O(log n) — requires sorted array",
            "Bubble, Selection, Insertion Sort: O(n^2) — simple but slow",
            "Always watch for: out-of-bounds access, uninitialized arrays"
        };

        TextMob[] sumMobs = new TextMob[points.length];
        for (int i = 0; i < points.length; i++) {
            sumMobs[i] = new TextMob("  " + (i + 1) + ". " + points[i])
                    .setFontSize(22).setFillColor(Colors.WHITE);
            sumMobs[i].setPosition(0, -240 + i * 55);
            play(new Write(sumMobs[i], 0.8));
            hold(0.3);
        }
        hold(3.0);

        // Final fade out
        play(new FadeOut(sumTitle, 1.0));
        for (TextMob m : sumMobs) play(new FadeOut(m, 0.5));
        hold(1.0);
    }
}
