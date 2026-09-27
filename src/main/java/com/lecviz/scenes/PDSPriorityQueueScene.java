package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Scene;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Vec2;

/**
 * PDS Lecture: Priority Queues / Heaps.
 * Covers heap concept, array representation, insert (bubble up),
 * extract-min (bubble down), build-heap, heapsort, and applications.
 */
public class PDSPriorityQueueScene extends Scene {

    @Override
    public void construct() {

        // ===== SECTION 1: Title Card =====
        TextMob title = new TextMob("Priority Queues & Heaps")
                .setFontSize(56)
                .setBold()
                .setFillColor(Colors.GREEN);
        title.setPosition(0, -120);

        TextMob subtitle = new TextMob("Programming and Data Structures")
                .setFontSize(28)
                .setFillColor(Colors.LIGHT_GRAY);
        subtitle.setPosition(0, -40);

        TextMob credit = new TextMob("Prof. Rupesh Nasre — IIT Madras")
                .setFontSize(22)
                .setFillColor(Colors.GRAY);
        credit.setPosition(0, 20);

        play(new FadeIn(title, 1.0));
        play(new Write(subtitle, 0.8));
        play(new FadeIn(credit, 0.5));
        hold(2.0);

        play(new FadeOut(title, 0.5),
             new FadeOut(subtitle, 0.5),
             new FadeOut(credit, 0.5));

        // ===== SECTION 2: Priority Queue ADT =====
        TextMob heading = new TextMob("Priority Queue ADT")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.GREEN);
        heading.setPosition(0, -400);

        TextMob defn = new TextMob("A collection where each element has a priority — highest priority served first")
                .setFontSize(24)
                .setFillColor(Colors.LIGHT_GRAY);
        defn.setPosition(0, -340);

        play(new Write(heading, 0.8));
        play(new Write(defn, 1.0));
        hold(0.5);

        // Operations
        RectMob pqBox = new RectMob(500, 220);
        pqBox.setCornerRadius(12);
        pqBox.setFillColor(Colors.withAlpha(Colors.GREEN, 0.1));
        pqBox.setStrokeColor(Colors.GREEN);
        pqBox.setPosition(0, -170);

        TextMob pqTitle = new TextMob("Core Operations")
                .setFontSize(26).setBold().setFillColor(Colors.GREEN);
        pqTitle.setPosition(0, -260);

        TextMob pqOp1 = new TextMob("insert(x, priority)    — add element with priority")
                .setFontSize(20).setFillColor(Colors.WHITE);
        pqOp1.setPosition(0, -210);

        TextMob pqOp2 = new TextMob("extractMin()           — remove & return minimum priority element")
                .setFontSize(20).setFillColor(Colors.WHITE);
        pqOp2.setPosition(0, -170);

        TextMob pqOp3 = new TextMob("getMin()               — peek at minimum without removing")
                .setFontSize(20).setFillColor(Colors.WHITE);
        pqOp3.setPosition(0, -130);

        TextMob pqOp4 = new TextMob("decreaseKey(x, newP)   — lower an element's priority")
                .setFontSize(20).setFillColor(Colors.WHITE);
        pqOp4.setPosition(0, -90);

        play(new FadeIn(pqBox, 0.4), new FadeIn(pqTitle, 0.3));
        play(new Write(pqOp1, 0.6));
        play(new Write(pqOp2, 0.6));
        play(new Write(pqOp3, 0.6));
        play(new Write(pqOp4, 0.6));
        hold(1.0);

        // Real world analogy
        TextMob analogy = new TextMob("Like a hospital ER: most critical patients treated first, not first-come")
                .setFontSize(22).setFillColor(Colors.TEAL);
        analogy.setPosition(0, 10);
        play(new Write(analogy, 1.0));
        hold(2.0);

        play(new FadeOut(heading, 0.3), new FadeOut(defn, 0.3),
             new FadeOut(pqBox, 0.3), new FadeOut(pqTitle, 0.3),
             new FadeOut(pqOp1, 0.3), new FadeOut(pqOp2, 0.3),
             new FadeOut(pqOp3, 0.3), new FadeOut(pqOp4, 0.3),
             new FadeOut(analogy, 0.3));

        // ===== SECTION 3: Why Not a Sorted Array? =====
        TextMob whyHeading = new TextMob("Why Not Just Use a Sorted Array?")
                .setFontSize(44)
                .setBold()
                .setFillColor(Colors.RED);
        whyHeading.setPosition(0, -400);
        play(new Write(whyHeading, 0.8));

        // Sorted array
        ArrayMob sortedArr = new ArrayMob("2", "5", "8", "12", "17", "23");
        sortedArr.setLabel("sorted array");
        sortedArr.setPosition(0, -260);
        play(new FadeIn(sortedArr, 0.5));

        TextMob extractGood = new TextMob("extractMin(): just remove first element -> O(1)")
                .setFontSize(22).setFillColor(Colors.GREEN);
        extractGood.setPosition(0, -170);
        play(new Write(extractGood, 0.8));
        sortedArr.highlight(0);
        sortedArr.setCellColor(0, Colors.withAlpha(Colors.GREEN, 0.3));
        hold(0.8);
        sortedArr.clearHighlights();
        sortedArr.clearCellColors();

        TextMob insertBad = new TextMob("insert(10): must shift elements to maintain sorted order -> O(n)!")
                .setFontSize(22).setFillColor(Colors.RED);
        insertBad.setPosition(0, -110);
        play(new Write(insertBad, 0.8));

        // Show shifting animation
        sortedArr.highlight(3);
        sortedArr.highlight(4);
        sortedArr.highlight(5);
        hold(0.5);

        TextMob shiftNote = new TextMob("Every insert needs to find position AND shift elements right")
                .setFontSize(20).setFillColor(Colors.ORANGE);
        shiftNote.setPosition(0, -50);
        play(new Write(shiftNote, 0.8));
        hold(1.0);

        // Comparison table
        RectMob compBox = new RectMob(600, 130);
        compBox.setCornerRadius(10);
        compBox.setFillColor(Colors.withAlpha(Colors.PURPLE, 0.1));
        compBox.setStrokeColor(Colors.PURPLE);
        compBox.setPosition(0, 80);

        TextMob compTitle = new TextMob("Sorted Array vs Binary Heap")
                .setFontSize(22).setBold().setFillColor(Colors.PURPLE);
        compTitle.setPosition(0, 30);

        TextMob compRow1 = new TextMob("           Insert    ExtractMin")
                .setFontSize(20).setFillColor(Colors.GRAY);
        compRow1.setPosition(0, 65);

        TextMob compRow2 = new TextMob("Sorted:    O(n)      O(1)")
                .setFontSize(20).setFillColor(Colors.RED);
        compRow2.setPosition(0, 95);

        TextMob compRow3 = new TextMob("Heap:      O(log n)  O(log n)    <- both fast!")
                .setFontSize(20).setFillColor(Colors.GREEN);
        compRow3.setPosition(0, 125);

        play(new FadeIn(compBox, 0.3), new FadeIn(compTitle, 0.3));
        play(new Write(compRow1, 0.5));
        play(new Write(compRow2, 0.5));
        play(new Write(compRow3, 0.5));
        hold(2.5);

        play(new FadeOut(whyHeading, 0.3), new FadeOut(sortedArr, 0.3),
             new FadeOut(extractGood, 0.3), new FadeOut(insertBad, 0.3), new FadeOut(shiftNote, 0.3),
             new FadeOut(compBox, 0.3), new FadeOut(compTitle, 0.3),
             new FadeOut(compRow1, 0.3), new FadeOut(compRow2, 0.3), new FadeOut(compRow3, 0.3));

        // ===== SECTION 4: Binary Heap Concept =====
        TextMob heapHeading = new TextMob("Binary Heap")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.BLUE);
        heapHeading.setPosition(0, -420);
        play(new Write(heapHeading, 0.8));

        TextMob heapDef = new TextMob("A complete binary tree stored as an array with the heap property")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        heapDef.setPosition(0, -360);
        play(new Write(heapDef, 1.0));
        hold(0.5);

        // Show tree AND array side by side
        //       3
        //      / \
        //     5   8
        //    / \  /
        //   9  6 12
        TreeMob heapTree = new TreeMob("3", "5", "8", "9", "6", "12");
        heapTree.setPosition(-350, -130);

        TextMob treeLabel = new TextMob("Tree View")
                .setFontSize(22).setBold().setFillColor(Colors.BLUE);
        treeLabel.setPosition(-350, -280);

        play(new FadeIn(treeLabel, 0.3), new FadeIn(heapTree, 1.0));
        hold(0.5);

        ArrayMob heapArr = new ArrayMob("3", "5", "8", "9", "6", "12");
        heapArr.setLabel("Array View");
        heapArr.setPosition(350, -130);

        play(new FadeIn(heapArr, 0.6));

        // Show correspondence
        TextMob correspond = new TextMob("Same data — tree is the logical view, array is the storage")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        correspond.setPosition(0, 60);
        play(new Write(correspond, 1.0));

        ArrowMob corrArrow = new ArrowMob(-100, -130, 150, -130);
        corrArrow.setStrokeColor(Colors.GOLD);
        play(new DrawArrow(corrArrow, 0.5));
        hold(2.0);

        play(new FadeOut(heapHeading, 0.3), new FadeOut(heapDef, 0.3),
             new FadeOut(heapTree, 0.3), new FadeOut(treeLabel, 0.3),
             new FadeOut(heapArr, 0.3), new FadeOut(correspond, 0.3), new FadeOut(corrArrow, 0.3));

        // ===== SECTION 5: Min-Heap Property =====
        TextMob propHeading = new TextMob("Min-Heap Property")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.TEAL);
        propHeading.setPosition(0, -420);
        play(new Write(propHeading, 0.8));

        TextMob propDef = new TextMob("Every parent is less than or equal to its children: A[parent] ≤ A[child]")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        propDef.setPosition(0, -360);
        play(new Write(propDef, 1.0));
        hold(0.5);

        // Valid min-heap
        TreeMob validHeap = new TreeMob("2", "4", "5", "7", "6", "8");
        validHeap.setPosition(-350, -130);

        TextMob validLabel = new TextMob("Valid Min-Heap")
                .setFontSize(22).setBold().setFillColor(Colors.GREEN);
        validLabel.setPosition(-350, -280);

        play(new FadeIn(validLabel, 0.3), new FadeIn(validHeap, 1.0));

        // Highlight parent-child relationship
        validHeap.setNodeColor(0, Colors.withAlpha(Colors.GREEN, 0.4));
        TextMob validNote = new TextMob("2 ≤ 4 and 2 ≤ 5 (root is minimum)")
                .setFontSize(18).setFillColor(Colors.GREEN);
        validNote.setPosition(-350, 50);
        play(new FadeIn(validNote, 0.3));
        hold(1.0);

        // Invalid heap
        TreeMob invalidHeap = new TreeMob("2", "1", "5", "7", "6", "8");
        invalidHeap.setPosition(350, -130);

        TextMob invalidLabel = new TextMob("NOT a Valid Min-Heap")
                .setFontSize(22).setBold().setFillColor(Colors.RED);
        invalidLabel.setPosition(350, -280);

        play(new FadeIn(invalidLabel, 0.3), new FadeIn(invalidHeap, 1.0));

        invalidHeap.setNodeColor(0, Colors.withAlpha(Colors.RED, 0.4));
        invalidHeap.setNodeColor(1, Colors.withAlpha(Colors.RED, 0.4));
        TextMob invalidNote = new TextMob("2 > 1! Parent must be ≤ child")
                .setFontSize(18).setFillColor(Colors.RED);
        invalidNote.setPosition(350, 50);
        play(new FadeIn(invalidNote, 0.3));
        hold(2.0);

        play(new FadeOut(propHeading, 0.3), new FadeOut(propDef, 0.3),
             new FadeOut(validHeap, 0.3), new FadeOut(validLabel, 0.3), new FadeOut(validNote, 0.3),
             new FadeOut(invalidHeap, 0.3), new FadeOut(invalidLabel, 0.3), new FadeOut(invalidNote, 0.3));

        // ===== SECTION 6: Array Representation =====
        TextMob arrRepHeading = new TextMob("Array Representation")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.PURPLE);
        arrRepHeading.setPosition(0, -420);
        play(new Write(arrRepHeading, 0.8));

        TextMob arrRepDesc = new TextMob("For node at index i (0-based):")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        arrRepDesc.setPosition(0, -360);
        play(new Write(arrRepDesc, 0.8));

        // Formulas
        RectMob formulaBox = new RectMob(500, 180);
        formulaBox.setCornerRadius(12);
        formulaBox.setFillColor(Colors.withAlpha(Colors.PURPLE, 0.1));
        formulaBox.setStrokeColor(Colors.PURPLE);
        formulaBox.setPosition(-300, -220);

        TextMob f1 = new TextMob("Parent:       (i - 1) / 2")
                .setFontSize(24).setFillColor(Colors.TEAL);
        f1.setPosition(-300, -280);

        TextMob f2 = new TextMob("Left Child:   2*i + 1")
                .setFontSize(24).setFillColor(Colors.GREEN);
        f2.setPosition(-300, -230);

        TextMob f3 = new TextMob("Right Child:  2*i + 2")
                .setFontSize(24).setFillColor(Colors.ORANGE);
        f3.setPosition(-300, -180);

        play(new FadeIn(formulaBox, 0.4));
        play(new Write(f1, 0.6));
        play(new Write(f2, 0.6));
        play(new Write(f3, 0.6));
        hold(0.5);

        // Show example
        // Tree: [3, 5, 8, 9, 6, 12]
        ArrayMob idxArr = new ArrayMob("3", "5", "8", "9", "6", "12");
        idxArr.setLabel("heap[]");
        idxArr.setPosition(300, -260);
        play(new FadeIn(idxArr, 0.5));

        // Highlight node at index 1 (value 5)
        idxArr.highlight(1);

        TextMob idxExample = new TextMob("Node i=1 (value 5):")
                .setFontSize(22).setBold().setFillColor(Colors.YELLOW);
        idxExample.setPosition(300, -180);
        play(new FadeIn(idxExample, 0.3));

        TextMob parentEx = new TextMob("Parent: (1-1)/2 = 0 -> value 3")
                .setFontSize(20).setFillColor(Colors.TEAL);
        parentEx.setPosition(300, -140);
        play(new Write(parentEx, 0.5));
        idxArr.setCellColor(0, Colors.withAlpha(Colors.TEAL, 0.3));
        hold(0.4);

        TextMob leftEx = new TextMob("Left:   2*1+1 = 3 -> value 9")
                .setFontSize(20).setFillColor(Colors.GREEN);
        leftEx.setPosition(300, -100);
        play(new Write(leftEx, 0.5));
        idxArr.setCellColor(3, Colors.withAlpha(Colors.GREEN, 0.3));
        hold(0.4);

        TextMob rightEx = new TextMob("Right:  2*1+2 = 4 -> value 6")
                .setFontSize(20).setFillColor(Colors.ORANGE);
        rightEx.setPosition(300, -60);
        play(new Write(rightEx, 0.5));
        idxArr.setCellColor(4, Colors.withAlpha(Colors.ORANGE, 0.3));
        hold(1.5);

        TextMob noPointers = new TextMob("No pointers needed! Navigating the tree is just arithmetic.")
                .setFontSize(22).setBold().setFillColor(Colors.GOLD);
        noPointers.setPosition(0, 40);
        play(new Write(noPointers, 1.0));
        hold(2.0);

        play(new FadeOut(arrRepHeading, 0.3), new FadeOut(arrRepDesc, 0.3),
             new FadeOut(formulaBox, 0.3), new FadeOut(f1, 0.3), new FadeOut(f2, 0.3), new FadeOut(f3, 0.3),
             new FadeOut(idxArr, 0.3), new FadeOut(idxExample, 0.3),
             new FadeOut(parentEx, 0.3), new FadeOut(leftEx, 0.3), new FadeOut(rightEx, 0.3),
             new FadeOut(noPointers, 0.3));

        // ===== SECTION 7: Insert — Bubble Up =====
        TextMob insertHeading = new TextMob("Insert: Bubble Up (Heapify-Up)")
                .setFontSize(44)
                .setBold()
                .setFillColor(Colors.GOLD);
        insertHeading.setPosition(0, -420);
        play(new Write(insertHeading, 0.8));

        TextMob insertDesc = new TextMob("Add new element at end, then swap upward until heap property restored")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        insertDesc.setPosition(0, -370);
        play(new Write(insertDesc, 1.0));
        hold(0.5);

        // Start with heap [3, 5, 8, 9, 6, 12]
        // Insert 1
        TreeMob insertTree = new TreeMob("3", "5", "8", "9", "6", "12");
        insertTree.setPosition(-300, -130);
        TextMob insertTreeLabel = new TextMob("Tree View")
                .setFontSize(20).setBold().setFillColor(Colors.BLUE);
        insertTreeLabel.setPosition(-300, -290);
        play(new FadeIn(insertTreeLabel, 0.3), new FadeIn(insertTree, 0.8));

        ArrayMob insertArr = new ArrayMob("3", "5", "8", "9", "6", "12");
        insertArr.setLabel("Array");
        insertArr.setPosition(350, -280);
        play(new FadeIn(insertArr, 0.5));

        TextMob insertStep = new TextMob("Step 1: Add 1 at the end (index 6)")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        insertStep.setPosition(0, 120);
        play(new Write(insertStep, 0.8));

        // After adding 1 at index 6 (right child of node 2 which is 8)
        // Tree becomes [3, 5, 8, 9, 6, 12, 1]
        TreeMob insertTree2 = new TreeMob("3", "5", "8", "9", "6", "12", "1");
        insertTree2.setPosition(-300, -130);
        play(new FadeOut(insertTree, 0.3));
        play(new FadeIn(insertTree2, 0.5));

        insertTree2.setNodeColor(6, Colors.withAlpha(Colors.GOLD, 0.5));
        hold(0.8);

        // Bubble up: compare 1 with parent 8 (index 2)
        insertStep.setText("Step 2: Compare 1 with parent 8. 1 < 8 -> swap!");
        insertTree2.highlightNode(6);
        insertTree2.highlightNode(2);
        hold(0.8);

        // After swap: [3, 5, 1, 9, 6, 12, 8]
        TreeMob insertTree3 = new TreeMob("3", "5", "1", "9", "6", "12", "8");
        insertTree3.setPosition(-300, -130);
        play(new FadeOut(insertTree2, 0.3));
        play(new FadeIn(insertTree3, 0.4));
        insertTree3.setNodeColor(2, Colors.withAlpha(Colors.GOLD, 0.5));
        hold(0.5);

        // Bubble up: compare 1 with parent 3 (index 0)
        insertStep.setText("Step 3: Compare 1 with parent 3. 1 < 3 -> swap!");
        insertTree3.highlightNode(2);
        insertTree3.highlightNode(0);
        hold(0.8);

        // After swap: [1, 5, 3, 9, 6, 12, 8]
        TreeMob insertTree4 = new TreeMob("1", "5", "3", "9", "6", "12", "8");
        insertTree4.setPosition(-300, -130);
        play(new FadeOut(insertTree3, 0.3));
        play(new FadeIn(insertTree4, 0.4));
        insertTree4.setNodeColor(0, Colors.withAlpha(Colors.GREEN, 0.5));

        insertStep.setText("Done! 1 is now the root — min-heap property restored");
        play(new ColorChange(insertStep, Colors.YELLOW, Colors.GREEN, 0.3));

        // Update array display
        ArrayMob insertArr2 = new ArrayMob("1", "5", "3", "9", "6", "12", "8");
        insertArr2.setLabel("Array (after insert)");
        insertArr2.setPosition(350, -200);
        play(new FadeOut(insertArr, 0.3));
        play(new FadeIn(insertArr2, 0.4));
        insertArr2.setCellColor(0, Colors.withAlpha(Colors.GREEN, 0.3));
        hold(2.0);

        play(new FadeOut(insertHeading, 0.3), new FadeOut(insertDesc, 0.3),
             new FadeOut(insertTree4, 0.3), new FadeOut(insertTreeLabel, 0.3),
             new FadeOut(insertArr2, 0.3), new FadeOut(insertStep, 0.3));

        // ===== SECTION 8: Extract-Min — Bubble Down =====
        TextMob extractHeading = new TextMob("Extract-Min: Bubble Down (Heapify-Down)")
                .setFontSize(40)
                .setBold()
                .setFillColor(Colors.RED);
        extractHeading.setPosition(0, -420);
        play(new Write(extractHeading, 0.8));

        TextMob extractDesc = new TextMob("Remove root (min), replace with last element, then bubble down")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        extractDesc.setPosition(0, -370);
        play(new Write(extractDesc, 1.0));
        hold(0.5);

        // Start: [1, 5, 3, 9, 6, 12, 8]
        TreeMob extTree1 = new TreeMob("1", "5", "3", "9", "6", "12", "8");
        extTree1.setPosition(-300, -130);

        TextMob extTreeLabel = new TextMob("Min-Heap")
                .setFontSize(20).setBold().setFillColor(Colors.BLUE);
        extTreeLabel.setPosition(-300, -290);

        play(new FadeIn(extTreeLabel, 0.3), new FadeIn(extTree1, 0.8));

        TextMob extStep = new TextMob("Step 1: Remove root (1) — this is the minimum")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        extStep.setPosition(0, 120);
        play(new Write(extStep, 0.8));

        extTree1.setNodeColor(0, Colors.withAlpha(Colors.RED, 0.5));
        hold(0.8);

        // Replace root with last element (8)
        extStep.setText("Step 2: Move last element (8) to root position");
        // [8, 5, 3, 9, 6, 12]
        TreeMob extTree2 = new TreeMob("8", "5", "3", "9", "6", "12");
        extTree2.setPosition(-300, -130);
        play(new FadeOut(extTree1, 0.3));
        play(new FadeIn(extTree2, 0.4));
        extTree2.setNodeColor(0, Colors.withAlpha(Colors.ORANGE, 0.5));
        hold(0.8);

        // Bubble down: 8 vs children 5,3 — swap with smaller (3)
        extStep.setText("Step 3: 8 > min(5, 3) = 3 -> swap 8 and 3");
        extTree2.highlightNode(0);
        extTree2.highlightNode(2);
        hold(0.8);

        // [3, 5, 8, 9, 6, 12]
        TreeMob extTree3 = new TreeMob("3", "5", "8", "9", "6", "12");
        extTree3.setPosition(-300, -130);
        play(new FadeOut(extTree2, 0.3));
        play(new FadeIn(extTree3, 0.4));
        extTree3.setNodeColor(2, Colors.withAlpha(Colors.ORANGE, 0.5));

        // 8 is now at index 2, children are 12 (index 5) — 8 < 12, done
        extStep.setText("Step 4: 8 < child 12 -> stop! Heap property restored");
        play(new ColorChange(extStep, Colors.YELLOW, Colors.GREEN, 0.3));
        extTree3.setNodeColor(2, Colors.withAlpha(Colors.GREEN, 0.4));

        // Show result
        TextMob extracted = new TextMob("Extracted min = 1")
                .setFontSize(26).setBold().setFillColor(Colors.GOLD);
        extracted.setPosition(350, -200);

        ArrayMob extArr = new ArrayMob("3", "5", "8", "9", "6", "12");
        extArr.setLabel("Result array");
        extArr.setPosition(350, -120);

        play(new FadeIn(extracted, 0.4), new FadeIn(extArr, 0.4));
        hold(2.0);

        play(new FadeOut(extractHeading, 0.3), new FadeOut(extractDesc, 0.3),
             new FadeOut(extTree3, 0.3), new FadeOut(extTreeLabel, 0.3),
             new FadeOut(extStep, 0.3), new FadeOut(extracted, 0.3), new FadeOut(extArr, 0.3));

        // ===== SECTION 9: Build Heap =====
        TextMob buildHeading = new TextMob("Build Heap: Bottom-Up")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.PURPLE);
        buildHeading.setPosition(0, -420);
        play(new Write(buildHeading, 0.8));

        TextMob buildDesc = new TextMob("Given an unsorted array, make it a valid heap")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        buildDesc.setPosition(0, -360);
        play(new Write(buildDesc, 1.0));
        hold(0.3);

        // Start with unsorted: [9, 5, 6, 2, 3]
        ArrayMob buildArr = new ArrayMob("9", "5", "6", "2", "3");
        buildArr.setLabel("Unsorted input");
        buildArr.setPosition(0, -260);
        play(new FadeIn(buildArr, 0.5));

        TextMob buildNote = new TextMob("Strategy: call heapify-down on each non-leaf, from bottom to top")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        buildNote.setPosition(0, -180);
        play(new Write(buildNote, 1.0));
        hold(0.5);

        // Show tree
        TreeMob buildTree = new TreeMob("9", "5", "6", "2", "3");
        buildTree.setPosition(0, -20);
        play(new FadeIn(buildTree, 0.8));

        // Last non-leaf = index 1 (value 5, children 2,3)
        TextMob buildStep = new TextMob("Heapify node 1: 5 > min(2,3) = 2 -> swap 5 and 2")
                .setFontSize(20).setFillColor(Colors.TEAL);
        buildStep.setPosition(0, 160);
        play(new Write(buildStep, 0.8));
        buildTree.highlightNode(1);
        buildTree.highlightNode(3);
        hold(0.8);

        // [9, 2, 6, 5, 3]
        TreeMob buildTree2 = new TreeMob("9", "2", "6", "5", "3");
        buildTree2.setPosition(0, -20);
        play(new FadeOut(buildTree, 0.3));
        play(new FadeIn(buildTree2, 0.4));
        hold(0.5);

        // Heapify node 0: 9 > min(2,6) = 2 -> swap 9 and 2
        buildStep.setText("Heapify node 0: 9 > min(2,6) = 2 -> swap 9 and 2");
        buildTree2.highlightNode(0);
        buildTree2.highlightNode(1);
        hold(0.8);

        // [2, 9, 6, 5, 3] — but 9 at index 1 still violates: 9 > min(5,3)
        TreeMob buildTree3 = new TreeMob("2", "9", "6", "5", "3");
        buildTree3.setPosition(0, -20);
        play(new FadeOut(buildTree2, 0.3));
        play(new FadeIn(buildTree3, 0.4));

        buildStep.setText("Continue: 9 > min(5,3) = 3 -> swap 9 and 3");
        buildTree3.highlightNode(1);
        buildTree3.highlightNode(4);
        hold(0.8);

        // [2, 3, 6, 5, 9]
        TreeMob buildTree4 = new TreeMob("2", "3", "6", "5", "9");
        buildTree4.setPosition(0, -20);
        play(new FadeOut(buildTree3, 0.3));
        play(new FadeIn(buildTree4, 0.4));
        buildTree4.setNodeColor(0, Colors.withAlpha(Colors.GREEN, 0.4));

        buildStep.setText("Done! Valid min-heap built.");
        play(new ColorChange(buildStep, Colors.TEAL, Colors.GREEN, 0.3));
        hold(1.0);

        TextMob buildComplexity = new TextMob("Surprising: Build-Heap is O(n), NOT O(n log n)!")
                .setFontSize(24).setBold().setFillColor(Colors.GOLD);
        buildComplexity.setPosition(0, 220);
        play(new Write(buildComplexity, 1.0));

        TextMob buildWhy = new TextMob("Most nodes are near the bottom and do very little work")
                .setFontSize(20).setFillColor(Colors.GRAY);
        buildWhy.setPosition(0, 260);
        play(new Write(buildWhy, 0.8));
        hold(2.5);

        play(new FadeOut(buildHeading, 0.3), new FadeOut(buildDesc, 0.3),
             new FadeOut(buildArr, 0.3), new FadeOut(buildNote, 0.3),
             new FadeOut(buildTree4, 0.3), new FadeOut(buildStep, 0.3),
             new FadeOut(buildComplexity, 0.3), new FadeOut(buildWhy, 0.3));

        // ===== SECTION 10: Heap Sort =====
        TextMob hsHeading = new TextMob("Heap Sort")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.ORANGE);
        hsHeading.setPosition(0, -420);
        play(new Write(hsHeading, 0.8));

        TextMob hsDesc = new TextMob("Build a min-heap, then extract min repeatedly -> sorted output")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        hsDesc.setPosition(0, -360);
        play(new Write(hsDesc, 1.0));
        hold(0.5);

        // Start with heap [2, 3, 6, 5, 9]
        ArrayMob hsHeap = new ArrayMob("2", "3", "6", "5", "9");
        hsHeap.setLabel("Min-Heap");
        hsHeap.setPosition(-300, -250);
        play(new FadeIn(hsHeap, 0.5));

        ArrayMob hsSorted = new ArrayMob(" ", " ", " ", " ", " ");
        hsSorted.setLabel("Sorted output");
        hsSorted.setPosition(300, -250);
        play(new FadeIn(hsSorted, 0.5));

        // Extract min steps
        String[][] hsSteps = {
            {"2", "Extract 2 (min)"},
            {"3", "Extract 3"},
            {"5", "Extract 5"},
            {"6", "Extract 6"},
            {"9", "Extract 9"}
        };

        for (int i = 0; i < hsSteps.length; i++) {
            TextMob hsStep = new TextMob(hsSteps[i][1])
                    .setFontSize(20).setFillColor(Colors.WHITE);
            hsStep.setPosition(0, -160);
            play(new FadeIn(hsStep, 0.2));

            hsHeap.highlight(0);
            hsHeap.setCellColor(0, Colors.withAlpha(Colors.GREEN, 0.3));
            hold(0.3);

            hsSorted.setValue(i, hsSteps[i][0]);
            hsSorted.setCellColor(i, Colors.withAlpha(Colors.TEAL, 0.3));
            hold(0.3);

            hsHeap.clearHighlights();
            hsHeap.clearCellColors();
            hsSorted.clearCellColors();
            play(new FadeOut(hsStep, 0.15));

            // Simulate shrinking heap (simplified)
            if (i < hsSteps.length - 1) {
                hsHeap.setValue(0, " ");
            }
        }

        TextMob hsResult = new TextMob("Result: [2, 3, 5, 6, 9] — Sorted! Time: O(n log n)")
                .setFontSize(24).setBold().setFillColor(Colors.GREEN);
        hsResult.setPosition(0, -80);
        play(new Write(hsResult, 1.0));
        hold(2.0);

        play(new FadeOut(hsHeading, 0.3), new FadeOut(hsDesc, 0.3),
             new FadeOut(hsHeap, 0.3), new FadeOut(hsSorted, 0.3), new FadeOut(hsResult, 0.3));

        // ===== SECTION 11: Code — Insert & Extract in C =====
        TextMob codeHeading = new TextMob("Heap Operations in C")
                .setFontSize(40)
                .setBold()
                .setFillColor(Colors.BLUE);
        codeHeading.setPosition(0, -440);
        play(new FadeIn(codeHeading, 0.5));

        // Insert code
        CodeBlock insertCode = new CodeBlock(
            "void insert(int heap[], int *n, int val) {\n" +
            "    heap[*n] = val;       // add at end\n" +
            "    int i = *n;\n" +
            "    (*n)++;\n" +
            "    // Bubble up\n" +
            "    while (i > 0) {\n" +
            "        int p = (i - 1) / 2;\n" +
            "        if (heap[p] > heap[i]) {\n" +
            "            swap(&heap[p], &heap[i]);\n" +
            "            i = p;\n" +
            "        } else break;\n" +
            "    }\n" +
            "}", 16
        );
        insertCode.setPosition(-350, -140);
        play(new RevealCode(insertCode, 3.0));
        hold(1.0);

        insertCode.highlightLine(5);
        insertCode.highlightLine(6);
        insertCode.highlightLine(7);
        insertCode.highlightLine(8);
        TextMob bubbleUpNote = new TextMob("Bubble up: keep swapping with parent while smaller")
                .setFontSize(18).setFillColor(Colors.YELLOW);
        bubbleUpNote.setPosition(-350, 150);
        play(new FadeIn(bubbleUpNote, 0.3));
        hold(1.5);
        insertCode.clearHighlights();

        // ExtractMin code
        CodeBlock extractCode = new CodeBlock(
            "int extractMin(int heap[], int *n) {\n" +
            "    int min = heap[0];\n" +
            "    heap[0] = heap[--(*n)];  // last to root\n" +
            "    // Bubble down (heapify)\n" +
            "    int i = 0;\n" +
            "    while (2*i+1 < *n) {\n" +
            "        int c = 2*i+1; // left child\n" +
            "        if (c+1 < *n && heap[c+1] < heap[c])\n" +
            "            c++;       // pick smaller child\n" +
            "        if (heap[i] > heap[c]) {\n" +
            "            swap(&heap[i], &heap[c]);\n" +
            "            i = c;\n" +
            "        } else break;\n" +
            "    }\n" +
            "    return min;\n" +
            "}", 16
        );
        extractCode.setPosition(350, -140);
        play(new RevealCode(extractCode, 3.5));
        hold(2.0);

        play(new FadeOut(codeHeading, 0.3), new FadeOut(insertCode, 0.3),
             new FadeOut(bubbleUpNote, 0.3), new FadeOut(extractCode, 0.3));

        // ===== SECTION 12: Time Complexities =====
        TextMob tcHeading = new TextMob("Time Complexities")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.TEAL);
        tcHeading.setPosition(0, -400);
        play(new Write(tcHeading, 0.8));

        String[][] complexities = {
            {"Insert",       "O(log n)", "Bubble up at most height levels"},
            {"Extract-Min",  "O(log n)", "Bubble down at most height levels"},
            {"Get-Min",      "O(1)",     "Just read heap[0]"},
            {"Build Heap",   "O(n)",     "Bottom-up heapify — amortized"},
            {"Heap Sort",    "O(n log n)","Build + n extractions"},
            {"Decrease-Key", "O(log n)", "Bubble up after lowering value"}
        };
        javafx.scene.paint.Color[] tcColors = {
            Colors.GREEN, Colors.ORANGE, Colors.BLUE, Colors.GOLD, Colors.RED, Colors.PURPLE
        };

        for (int i = 0; i < complexities.length; i++) {
            double yPos = -290 + i * 65;

            TextMob opName = new TextMob(complexities[i][0])
                    .setFontSize(22).setBold().setFillColor(tcColors[i]);
            opName.setPosition(-400, yPos);

            TextMob opTime = new TextMob(complexities[i][1])
                    .setFontSize(22).setBold().setFillColor(Colors.WHITE);
            opTime.setPosition(-100, yPos);

            TextMob opWhy = new TextMob(complexities[i][2])
                    .setFontSize(18).setFillColor(Colors.GRAY);
            opWhy.setPosition(250, yPos);

            play(new FadeIn(opName, 0.2), new FadeIn(opTime, 0.2), new FadeIn(opWhy, 0.2));
            hold(0.3);
        }

        TextMob treeHeight = new TextMob("Height of complete binary tree = ⌊log₂ n⌋")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        treeHeight.setPosition(0, 130);
        play(new Write(treeHeight, 0.8));
        hold(3.0);

        play(new FadeOut(tcHeading, 0.3), new FadeOut(treeHeight, 0.3));
        hold(0.3);

        // ===== SECTION 13: Applications =====
        TextMob appHeading = new TextMob("Applications of Heaps")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.GOLD);
        appHeading.setPosition(0, -400);
        play(new Write(appHeading, 0.8));

        String[][] apps = {
            {"Dijkstra's Algorithm",    "Find shortest paths — extract nearest unvisited node"},
            {"Task Scheduling",         "OS scheduler picks highest-priority process"},
            {"Median Maintenance",      "Two heaps (max + min) to track running median"},
            {"K-th Largest Element",    "Min-heap of size k — root is answer"},
            {"Huffman Encoding",        "Build optimal prefix codes — merge two smallest"}
        };
        javafx.scene.paint.Color[] appColors = {Colors.TEAL, Colors.BLUE, Colors.PURPLE, Colors.ORANGE, Colors.GREEN};

        for (int i = 0; i < apps.length; i++) {
            double yPos = -280 + i * 75;

            RectMob appBox = new RectMob(800, 50);
            appBox.setCornerRadius(8);
            appBox.setFillColor(Colors.withAlpha(appColors[i], 0.1));
            appBox.setStrokeColor(appColors[i]);
            appBox.setPosition(0, yPos);

            TextMob appTitle = new TextMob(apps[i][0])
                    .setFontSize(22).setBold().setFillColor(appColors[i]);
            appTitle.setPosition(-350, yPos);

            TextMob appDesc2 = new TextMob(apps[i][1])
                    .setFontSize(18).setFillColor(Colors.WHITE);
            appDesc2.setPosition(150, yPos);

            play(new FadeIn(appBox, 0.2), new FadeIn(appTitle, 0.2), new FadeIn(appDesc2, 0.2));
            hold(0.4);
        }
        hold(2.5);

        play(new FadeOut(appHeading, 0.3));
        hold(0.3);

        // ===== SECTION 14: Summary =====
        TextMob sumTitle = new TextMob("Summary")
                .setFontSize(52)
                .setBold()
                .setFillColor(Colors.GREEN);
        sumTitle.setPosition(0, -300);
        play(new FadeIn(sumTitle, 0.5));

        String[] sumPoints = {
            "Priority Queue: insert + extractMin as core operations",
            "Binary Heap: complete binary tree stored compactly in an array",
            "Min-Heap Property: parent ≤ children — root is always minimum",
            "Insert (bubble up) and ExtractMin (bubble down) both O(log n)",
            "Build-Heap is O(n) using bottom-up heapify",
            "Heap Sort: O(n log n) in-place sorting algorithm",
            "Key enabler for Dijkstra, scheduling, and streaming algorithms"
        };

        for (int i = 0; i < sumPoints.length; i++) {
            TextMob pt = new TextMob("• " + sumPoints[i])
                    .setFontSize(22).setFillColor(Colors.WHITE);
            pt.setPosition(0, -200 + i * 55);
            play(new Write(pt, 0.8));
            hold(0.3);
        }
        hold(3.0);

        play(new FadeOut(sumTitle, 1.0));
    }
}
