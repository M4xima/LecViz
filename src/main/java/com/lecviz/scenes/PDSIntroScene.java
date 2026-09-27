package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Scene;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Vec2;

/**
 * PDS Lecture: Introduction to Programming and Data Structures.
 * Covers data structures, algorithms, ADTs, efficiency, memory model,
 * pointers, dynamic allocation, and course roadmap.
 */
public class PDSIntroScene extends Scene {

    @Override
    public void construct() {

        // ===== SECTION 1: Title Card =====
        TextMob title = new TextMob("Programming and Data Structures")
                .setFontSize(56)
                .setBold()
                .setFillColor(Colors.BLUE);
        title.setPosition(0, -150);

        TextMob subtitle = new TextMob("CS2100 — IIT Madras")
                .setFontSize(30)
                .setFillColor(Colors.LIGHT_GRAY);
        subtitle.setPosition(0, -70);

        TextMob credit = new TextMob("Prof. Rupesh Nasre")
                .setFontSize(24)
                .setFillColor(Colors.GRAY);
        credit.setPosition(0, -20);

        TextMob tagline = new TextMob("Organizing data. Designing algorithms. Writing efficient C programs.")
                .setFontSize(22)
                .setFillColor(Colors.TEAL);
        tagline.setPosition(0, 60);

        play(new FadeIn(title, 1.2));
        play(new Write(subtitle, 0.8));
        play(new FadeIn(credit, 0.6));
        hold(1.0);
        play(new Write(tagline, 1.5));
        hold(2.5);

        play(new FadeOut(title, 0.5),
             new FadeOut(subtitle, 0.5),
             new FadeOut(credit, 0.5),
             new FadeOut(tagline, 0.5));

        // ===== SECTION 2: What is a Data Structure? =====
        TextMob heading = new TextMob("What is a Data Structure?")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.BLUE);
        heading.setPosition(0, -400);

        TextMob defn = new TextMob("A way of organizing and storing data for efficient access and modification")
                .setFontSize(24)
                .setFillColor(Colors.LIGHT_GRAY);
        defn.setPosition(0, -340);

        play(new Write(heading, 0.8));
        play(new Write(defn, 1.2));
        hold(1.0);

        // Show Array icon
        TextMob arrLabel = new TextMob("Array")
                .setFontSize(22)
                .setFillColor(Colors.YELLOW);
        arrLabel.setPosition(-500, -190);
        ArrayMob arrIcon = new ArrayMob("3", "7", "1", "9");
        arrIcon.setPosition(-500, -240);
        arrIcon.setCellSize(50, 40);

        play(new FadeIn(arrLabel, 0.4), new FadeIn(arrIcon, 0.6));
        hold(0.5);

        // Show Linked List icon
        TextMob llLabel = new TextMob("Linked List")
                .setFontSize(22)
                .setFillColor(Colors.YELLOW);
        llLabel.setPosition(0, -190);
        LinkedListMob llIcon = new LinkedListMob("A", "B", "C");
        llIcon.setPosition(0, -240);

        play(new FadeIn(llLabel, 0.4), new FadeIn(llIcon, 0.6));
        hold(0.5);

        // Show Tree icon
        TextMob treeLabel = new TextMob("Tree")
                .setFontSize(22)
                .setFillColor(Colors.YELLOW);
        treeLabel.setPosition(500, -190);
        TreeMob treeIcon = new TreeMob("10", "5", "15");
        treeIcon.setPosition(500, -240);

        play(new FadeIn(treeLabel, 0.4), new FadeIn(treeIcon, 0.6));
        hold(1.5);

        // Everyday analogy
        TextMob analogy = new TextMob("Think: bookshelf (array), chain of paper clips (list), family tree (tree)")
                .setFontSize(22)
                .setFillColor(Colors.TEAL);
        analogy.setPosition(0, -100);
        play(new Write(analogy, 1.5));
        hold(2.0);

        play(new FadeOut(heading, 0.3), new FadeOut(defn, 0.3),
             new FadeOut(arrLabel, 0.3), new FadeOut(arrIcon, 0.3),
             new FadeOut(llLabel, 0.3), new FadeOut(llIcon, 0.3),
             new FadeOut(treeLabel, 0.3), new FadeOut(treeIcon, 0.3),
             new FadeOut(analogy, 0.3));

        // ===== SECTION 3: What is an Algorithm? =====
        TextMob algoHeading = new TextMob("What is an Algorithm?")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.GREEN);
        algoHeading.setPosition(0, -400);

        TextMob algoDef = new TextMob("A step-by-step procedure to solve a problem in finite time")
                .setFontSize(24)
                .setFillColor(Colors.LIGHT_GRAY);
        algoDef.setPosition(0, -340);

        play(new Write(algoHeading, 0.8));
        play(new Write(algoDef, 1.0));
        hold(1.0);

        // Recipe analogy — show steps
        TextMob recipeTitle = new TextMob("Recipe: Find the largest element in an array")
                .setFontSize(26)
                .setBold()
                .setFillColor(Colors.GOLD);
        recipeTitle.setPosition(0, -260);
        play(new FadeIn(recipeTitle, 0.5));

        TextMob step1 = new TextMob("1. Set max = first element")
                .setFontSize(22).setFillColor(Colors.WHITE);
        step1.setPosition(-200, -200);

        TextMob step2 = new TextMob("2. For each remaining element:")
                .setFontSize(22).setFillColor(Colors.WHITE);
        step2.setPosition(-200, -160);

        TextMob step3 = new TextMob("     If element > max, update max")
                .setFontSize(22).setFillColor(Colors.WHITE);
        step3.setPosition(-200, -120);

        TextMob step4 = new TextMob("3. Return max")
                .setFontSize(22).setFillColor(Colors.WHITE);
        step4.setPosition(-200, -80);

        play(new Write(step1, 0.8));
        hold(0.3);
        play(new Write(step2, 0.8));
        play(new Write(step3, 0.8));
        hold(0.3);
        play(new Write(step4, 0.6));
        hold(1.0);

        // Show algorithm running on array
        ArrayMob algoArr = new ArrayMob("4", "9", "2", "7", "5");
        algoArr.setLabel("arr");
        algoArr.setPosition(0, 50);

        TextMob maxTracker = new TextMob("max = 4")
                .setFontSize(28)
                .setFillColor(Colors.ORANGE);
        maxTracker.setPosition(0, 160);

        play(new FadeIn(algoArr, 0.5));
        play(new FadeIn(maxTracker, 0.3));

        int[] algoData = {4, 9, 2, 7, 5};
        int currentMax = algoData[0];
        algoArr.highlight(0);
        algoArr.setCellColor(0, Colors.withAlpha(Colors.GREEN, 0.3));
        hold(0.8);

        for (int i = 1; i < algoData.length; i++) {
            algoArr.unhighlight(i - 1);
            algoArr.highlight(i);
            hold(0.5);
            if (algoData[i] > currentMax) {
                currentMax = algoData[i];
                algoArr.setCellColor(i, Colors.withAlpha(Colors.GREEN, 0.3));
                maxTracker.setText("max = " + currentMax);
            }
            hold(0.5);
        }
        algoArr.clearHighlights();
        maxTracker.setText("max = 9  (found!)");
        play(new ColorChange(maxTracker, Colors.ORANGE, Colors.GREEN, 0.5));
        hold(1.5);

        play(new FadeOut(algoHeading, 0.3), new FadeOut(algoDef, 0.3),
             new FadeOut(recipeTitle, 0.3),
             new FadeOut(step1, 0.3), new FadeOut(step2, 0.3),
             new FadeOut(step3, 0.3), new FadeOut(step4, 0.3),
             new FadeOut(algoArr, 0.3), new FadeOut(maxTracker, 0.3));

        // ===== SECTION 4: Abstract Data Type (ADT) =====
        TextMob adtHeading = new TextMob("Abstract Data Type (ADT)")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.PURPLE);
        adtHeading.setPosition(0, -400);

        TextMob adtDef = new TextMob("Defines WHAT operations are supported, not HOW they are implemented")
                .setFontSize(24)
                .setFillColor(Colors.LIGHT_GRAY);
        adtDef.setPosition(0, -340);

        play(new Write(adtHeading, 0.8));
        play(new Write(adtDef, 1.0));
        hold(1.0);

        // Stack ADT interface box
        RectMob adtBox = new RectMob(340, 220);
        adtBox.setCornerRadius(12);
        adtBox.setFillColor(Colors.withAlpha(Colors.PURPLE, 0.15));
        adtBox.setStrokeColor(Colors.PURPLE);
        adtBox.setPosition(-350, -160);

        TextMob adtTitle2 = new TextMob("Stack ADT (Interface)")
                .setFontSize(24).setBold().setFillColor(Colors.PURPLE);
        adtTitle2.setPosition(-350, -250);

        TextMob op1 = new TextMob("push(x)  — add to top")
                .setFontSize(20).setFillColor(Colors.WHITE);
        op1.setPosition(-350, -200);

        TextMob op2 = new TextMob("pop()    — remove top")
                .setFontSize(20).setFillColor(Colors.WHITE);
        op2.setPosition(-350, -165);

        TextMob op3 = new TextMob("peek()   — view top")
                .setFontSize(20).setFillColor(Colors.WHITE);
        op3.setPosition(-350, -130);

        TextMob op4 = new TextMob("isEmpty() — check empty")
                .setFontSize(20).setFillColor(Colors.WHITE);
        op4.setPosition(-350, -95);

        play(new FadeIn(adtBox, 0.5));
        play(new FadeIn(adtTitle2, 0.3));
        play(new Write(op1, 0.5));
        play(new Write(op2, 0.5));
        play(new Write(op3, 0.5));
        play(new Write(op4, 0.5));
        hold(1.0);

        // Implementation 1: Array-based
        RectMob implBox1 = new RectMob(260, 150);
        implBox1.setCornerRadius(10);
        implBox1.setFillColor(Colors.withAlpha(Colors.TEAL, 0.15));
        implBox1.setStrokeColor(Colors.TEAL);
        implBox1.setPosition(200, -200);

        TextMob implTitle1 = new TextMob("Array Implementation")
                .setFontSize(20).setBold().setFillColor(Colors.TEAL);
        implTitle1.setPosition(200, -260);

        ArrayMob stackArr = new ArrayMob("5", "8", "3", " ", " ");
        stackArr.setPosition(200, -200);
        stackArr.setCellSize(40, 35);

        TextMob topPtr = new TextMob("top = 2")
                .setFontSize(18).setFillColor(Colors.YELLOW);
        topPtr.setPosition(200, -150);

        play(new FadeIn(implBox1, 0.4), new FadeIn(implTitle1, 0.3));
        play(new FadeIn(stackArr, 0.4), new FadeIn(topPtr, 0.3));
        hold(0.8);

        // Implementation 2: Linked List
        RectMob implBox2 = new RectMob(260, 150);
        implBox2.setCornerRadius(10);
        implBox2.setFillColor(Colors.withAlpha(Colors.ORANGE, 0.15));
        implBox2.setStrokeColor(Colors.ORANGE);
        implBox2.setPosition(200, -20);

        TextMob implTitle2 = new TextMob("Linked List Implementation")
                .setFontSize(20).setBold().setFillColor(Colors.ORANGE);
        implTitle2.setPosition(200, -80);

        LinkedListMob stackLL = new LinkedListMob("3", "8", "5");
        stackLL.setPosition(200, -20);

        TextMob headPtr = new TextMob("head (top)")
                .setFontSize(18).setFillColor(Colors.YELLOW);
        headPtr.setPosition(200, 30);

        play(new FadeIn(implBox2, 0.4), new FadeIn(implTitle2, 0.3));
        play(new FadeIn(stackLL, 0.4), new FadeIn(headPtr, 0.3));
        hold(1.0);

        // Arrow from ADT to implementations
        ArrowMob arrow1 = new ArrowMob(-180, -180, 70, -200);
        arrow1.setStrokeColor(Colors.LIGHT_GRAY);
        ArrowMob arrow2 = new ArrowMob(-180, -140, 70, -30);
        arrow2.setStrokeColor(Colors.LIGHT_GRAY);

        TextMob implNote = new TextMob("Same interface, different implementations!")
                .setFontSize(22).setFillColor(Colors.GOLD);
        implNote.setPosition(0, 100);

        play(new DrawArrow(arrow1, 0.5), new DrawArrow(arrow2, 0.5));
        play(new Write(implNote, 1.0));
        hold(2.0);

        play(new FadeOut(adtHeading, 0.3), new FadeOut(adtDef, 0.3),
             new FadeOut(adtBox, 0.3), new FadeOut(adtTitle2, 0.3),
             new FadeOut(op1, 0.3), new FadeOut(op2, 0.3),
             new FadeOut(op3, 0.3), new FadeOut(op4, 0.3),
             new FadeOut(implBox1, 0.3), new FadeOut(implTitle1, 0.3),
             new FadeOut(stackArr, 0.3), new FadeOut(topPtr, 0.3),
             new FadeOut(implBox2, 0.3), new FadeOut(implTitle2, 0.3),
             new FadeOut(stackLL, 0.3), new FadeOut(headPtr, 0.3),
             new FadeOut(arrow1, 0.3), new FadeOut(arrow2, 0.3),
             new FadeOut(implNote, 0.3));

        // ===== SECTION 5: Why Efficiency Matters =====
        TextMob effHeading = new TextMob("Why Efficiency Matters")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.RED);
        effHeading.setPosition(0, -400);
        play(new Write(effHeading, 0.8));

        TextMob effDesc = new TextMob("Linear Search O(n) vs Binary Search O(log n)")
                .setFontSize(26)
                .setFillColor(Colors.LIGHT_GRAY);
        effDesc.setPosition(0, -340);
        play(new Write(effDesc, 1.0));
        hold(0.5);

        // Sorted array for demonstration
        ArrayMob searchArr = new ArrayMob("2", "5", "8", "12", "17", "23", "31", "45");
        searchArr.setLabel("sorted array");
        searchArr.setPosition(0, -200);
        play(new FadeIn(searchArr, 0.5));

        // Linear search for 23
        TextMob linearLabel = new TextMob("Linear Search for 23: check every element")
                .setFontSize(22).setFillColor(Colors.ORANGE);
        linearLabel.setPosition(0, -110);
        play(new FadeIn(linearLabel, 0.3));

        for (int i = 0; i < 6; i++) {
            searchArr.highlight(i);
            hold(0.3);
            searchArr.unhighlight(i);
        }
        // found at index 5
        searchArr.highlight(5);
        searchArr.setCellColor(5, Colors.withAlpha(Colors.GREEN, 0.3));

        TextMob linearCount = new TextMob("6 comparisons")
                .setFontSize(22).setFillColor(Colors.RED);
        linearCount.setPosition(0, -60);
        play(new FadeIn(linearCount, 0.3));
        hold(1.0);
        searchArr.clearHighlights();
        searchArr.clearCellColors();

        play(new FadeOut(linearLabel, 0.3), new FadeOut(linearCount, 0.3));

        // Binary search for 23
        TextMob binLabel = new TextMob("Binary Search for 23: halve each time")
                .setFontSize(22).setFillColor(Colors.TEAL);
        binLabel.setPosition(0, -110);
        play(new FadeIn(binLabel, 0.3));

        // mid = 3 (value 12), 23 > 12 -> go right
        searchArr.highlight(3);
        TextMob binStep = new TextMob("mid=12, 23>12 -> go right")
                .setFontSize(20).setFillColor(Colors.YELLOW);
        binStep.setPosition(0, -60);
        play(new FadeIn(binStep, 0.3));
        hold(0.8);
        searchArr.unhighlight(3);

        // mid = 5 (value 23)
        searchArr.highlight(5);
        searchArr.setCellColor(5, Colors.withAlpha(Colors.GREEN, 0.3));
        binStep.setText("mid=23, Found! Only 2 comparisons");
        hold(1.0);

        TextMob effSummary = new TextMob("For n=1,000,000: Linear=1M steps, Binary=20 steps!")
                .setFontSize(24).setBold().setFillColor(Colors.GREEN);
        effSummary.setPosition(0, 30);
        play(new Write(effSummary, 1.2));
        hold(2.0);

        play(new FadeOut(effHeading, 0.3), new FadeOut(effDesc, 0.3),
             new FadeOut(searchArr, 0.3), new FadeOut(binLabel, 0.3),
             new FadeOut(binStep, 0.3), new FadeOut(effSummary, 0.3));

        // ===== SECTION 6: Memory Model in C =====
        TextMob memHeading = new TextMob("Memory Model in C")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.TEAL);
        memHeading.setPosition(0, -400);
        play(new Write(memHeading, 0.8));

        TextMob memDesc = new TextMob("A C program's memory is divided into distinct regions")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        memDesc.setPosition(0, -340);
        play(new Write(memDesc, 1.0));
        hold(0.5);

        // Stack region
        RectMob stackRegion = new RectMob(280, 120);
        stackRegion.setCornerRadius(8);
        stackRegion.setFillColor(Colors.withAlpha(Colors.BLUE, 0.2));
        stackRegion.setStrokeColor(Colors.BLUE);
        stackRegion.setPosition(-300, -220);

        TextMob stackLabel2 = new TextMob("Stack")
                .setFontSize(24).setBold().setFillColor(Colors.BLUE);
        stackLabel2.setPosition(-300, -260);

        TextMob stackInfo = new TextMob("Local variables, function calls\nGrows downward, auto-managed")
                .setFontSize(18).setFillColor(Colors.WHITE);
        stackInfo.setPosition(-300, -200);

        play(new FadeIn(stackRegion, 0.5), new FadeIn(stackLabel2, 0.3));
        play(new Write(stackInfo, 0.8));
        hold(0.5);

        // Heap region
        RectMob heapRegion = new RectMob(280, 120);
        heapRegion.setCornerRadius(8);
        heapRegion.setFillColor(Colors.withAlpha(Colors.GREEN, 0.2));
        heapRegion.setStrokeColor(Colors.GREEN);
        heapRegion.setPosition(-300, -60);

        TextMob heapLabel = new TextMob("Heap")
                .setFontSize(24).setBold().setFillColor(Colors.GREEN);
        heapLabel.setPosition(-300, -100);

        TextMob heapInfo = new TextMob("Dynamic memory (malloc/free)\nGrows upward, programmer-managed")
                .setFontSize(18).setFillColor(Colors.WHITE);
        heapInfo.setPosition(-300, -40);

        play(new FadeIn(heapRegion, 0.5), new FadeIn(heapLabel, 0.3));
        play(new Write(heapInfo, 0.8));
        hold(0.5);

        // Data/BSS region
        RectMob dataRegion = new RectMob(280, 90);
        dataRegion.setCornerRadius(8);
        dataRegion.setFillColor(Colors.withAlpha(Colors.GOLD, 0.2));
        dataRegion.setStrokeColor(Colors.GOLD);
        dataRegion.setPosition(-300, 90);

        TextMob dataLabel = new TextMob("Data / BSS")
                .setFontSize(24).setBold().setFillColor(Colors.GOLD);
        dataLabel.setPosition(-300, 60);

        TextMob dataInfo = new TextMob("Global & static variables")
                .setFontSize(18).setFillColor(Colors.WHITE);
        dataInfo.setPosition(-300, 100);

        play(new FadeIn(dataRegion, 0.5), new FadeIn(dataLabel, 0.3));
        play(new Write(dataInfo, 0.6));
        hold(0.5);

        // Code/Text region
        RectMob codeRegion = new RectMob(280, 90);
        codeRegion.setCornerRadius(8);
        codeRegion.setFillColor(Colors.withAlpha(Colors.PURPLE, 0.2));
        codeRegion.setStrokeColor(Colors.PURPLE);
        codeRegion.setPosition(-300, 210);

        TextMob codeLabel = new TextMob("Code (Text)")
                .setFontSize(24).setBold().setFillColor(Colors.PURPLE);
        codeLabel.setPosition(-300, 180);

        TextMob codeInfo = new TextMob("Compiled machine instructions")
                .setFontSize(18).setFillColor(Colors.WHITE);
        codeInfo.setPosition(-300, 220);

        play(new FadeIn(codeRegion, 0.5), new FadeIn(codeLabel, 0.3));
        play(new Write(codeInfo, 0.6));
        hold(0.5);

        // Address direction arrows
        ArrowMob highAddr = new ArrowMob(-100, -280, -100, 260);
        highAddr.setStrokeColor(Colors.LIGHT_GRAY);
        TextMob addrLabel2 = new TextMob("High Address -> Low Address")
                .setFontSize(16).setFillColor(Colors.GRAY);
        addrLabel2.setPosition(-100, 290);
        play(new DrawArrow(highAddr, 0.8));
        play(new FadeIn(addrLabel2, 0.3));
        hold(2.0);

        // Example code showing stack vs heap
        CodeBlock memCode = new CodeBlock(
            "int main() {\n" +
            "    int x = 10;       // stack\n" +
            "    int arr[5];       // stack\n" +
            "    int *p = malloc(sizeof(int)*5);\n" +
            "                      // p on stack,\n" +
            "                      // data on heap\n" +
            "    free(p);\n" +
            "    return 0;\n" +
            "}", 18
        );
        memCode.setPosition(350, -80);
        play(new RevealCode(memCode, 2.5));
        hold(2.0);

        play(new FadeOut(memHeading, 0.3), new FadeOut(memDesc, 0.3),
             new FadeOut(stackRegion, 0.3), new FadeOut(stackLabel2, 0.3), new FadeOut(stackInfo, 0.3),
             new FadeOut(heapRegion, 0.3), new FadeOut(heapLabel, 0.3), new FadeOut(heapInfo, 0.3),
             new FadeOut(dataRegion, 0.3), new FadeOut(dataLabel, 0.3), new FadeOut(dataInfo, 0.3),
             new FadeOut(codeRegion, 0.3), new FadeOut(codeLabel, 0.3), new FadeOut(codeInfo, 0.3),
             new FadeOut(highAddr, 0.3), new FadeOut(addrLabel2, 0.3),
             new FadeOut(memCode, 0.3));

        // ===== SECTION 7: Pointers Basics =====
        TextMob ptrHeading = new TextMob("Pointers in C")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.ORANGE);
        ptrHeading.setPosition(0, -400);
        play(new Write(ptrHeading, 0.8));

        TextMob ptrDef = new TextMob("A pointer stores the memory address of another variable")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        ptrDef.setPosition(0, -340);
        play(new Write(ptrDef, 1.0));
        hold(0.5);

        // Variable box
        RectMob varBox = new RectMob(160, 80);
        varBox.setCornerRadius(8);
        varBox.setFillColor(Colors.withAlpha(Colors.BLUE, 0.2));
        varBox.setStrokeColor(Colors.BLUE);
        varBox.setPosition(-400, -180);

        TextMob varName = new TextMob("x")
                .setFontSize(28).setBold().setFillColor(Colors.BLUE);
        varName.setPosition(-400, -210);

        TextMob varVal = new TextMob("42")
                .setFontSize(32).setFillColor(Colors.WHITE);
        varVal.setPosition(-400, -170);

        TextMob varAddr = new TextMob("addr: 0x1000")
                .setFontSize(16).setFillColor(Colors.GRAY);
        varAddr.setPosition(-400, -135);

        play(new FadeIn(varBox, 0.4), new FadeIn(varName, 0.3),
             new FadeIn(varVal, 0.3), new FadeIn(varAddr, 0.3));
        hold(0.5);

        // Pointer box
        RectMob ptrBox = new RectMob(160, 80);
        ptrBox.setCornerRadius(8);
        ptrBox.setFillColor(Colors.withAlpha(Colors.ORANGE, 0.2));
        ptrBox.setStrokeColor(Colors.ORANGE);
        ptrBox.setPosition(0, -180);

        TextMob ptrName = new TextMob("int *p = &x")
                .setFontSize(22).setBold().setFillColor(Colors.ORANGE);
        ptrName.setPosition(0, -215);

        TextMob ptrVal = new TextMob("0x1000")
                .setFontSize(28).setFillColor(Colors.YELLOW);
        ptrVal.setPosition(0, -170);

        TextMob ptrAddr2 = new TextMob("addr: 0x2000")
                .setFontSize(16).setFillColor(Colors.GRAY);
        ptrAddr2.setPosition(0, -135);

        play(new FadeIn(ptrBox, 0.4), new FadeIn(ptrName, 0.3),
             new FadeIn(ptrVal, 0.3), new FadeIn(ptrAddr2, 0.3));
        hold(0.5);

        // Arrow from pointer to variable
        ArrowMob ptrArrow = new ArrowMob(0, -180, -320, -180);
        ptrArrow.setStrokeColor(Colors.GOLD);
        play(new DrawArrow(ptrArrow, 0.6));

        TextMob deref = new TextMob("*p gives 42  (dereference)")
                .setFontSize(24).setFillColor(Colors.GREEN);
        deref.setPosition(0, -80);
        play(new Write(deref, 0.8));
        hold(1.0);

        // Dereferenced value box
        RectMob derefBox = new RectMob(160, 80);
        derefBox.setCornerRadius(8);
        derefBox.setFillColor(Colors.withAlpha(Colors.GREEN, 0.2));
        derefBox.setStrokeColor(Colors.GREEN);
        derefBox.setPosition(400, -180);

        TextMob derefLabel = new TextMob("*p")
                .setFontSize(28).setBold().setFillColor(Colors.GREEN);
        derefLabel.setPosition(400, -210);

        TextMob derefVal = new TextMob("42")
                .setFontSize(32).setFillColor(Colors.WHITE);
        derefVal.setPosition(400, -170);

        ArrowMob derefArrow = new ArrowMob(80, -180, 320, -180);
        derefArrow.setStrokeColor(Colors.GREEN);

        play(new FadeIn(derefBox, 0.4), new FadeIn(derefLabel, 0.3),
             new FadeIn(derefVal, 0.3), new DrawArrow(derefArrow, 0.5));
        hold(2.0);

        play(new FadeOut(ptrHeading, 0.3), new FadeOut(ptrDef, 0.3),
             new FadeOut(varBox, 0.3), new FadeOut(varName, 0.3),
             new FadeOut(varVal, 0.3), new FadeOut(varAddr, 0.3),
             new FadeOut(ptrBox, 0.3), new FadeOut(ptrName, 0.3),
             new FadeOut(ptrVal, 0.3), new FadeOut(ptrAddr2, 0.3),
             new FadeOut(ptrArrow, 0.3), new FadeOut(deref, 0.3),
             new FadeOut(derefBox, 0.3), new FadeOut(derefLabel, 0.3),
             new FadeOut(derefVal, 0.3), new FadeOut(derefArrow, 0.3));

        // ===== SECTION 8: Dynamic Memory Allocation =====
        TextMob dynHeading = new TextMob("Dynamic Memory Allocation")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.GOLD);
        dynHeading.setPosition(0, -400);
        play(new Write(dynHeading, 0.8));

        TextMob dynDesc = new TextMob("Allocate memory at runtime using malloc, and release with free")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        dynDesc.setPosition(0, -340);
        play(new Write(dynDesc, 1.0));
        hold(0.5);

        CodeBlock dynCode = new CodeBlock(
            "#include <stdlib.h>\n" +
            "\n" +
            "int main() {\n" +
            "    // Allocate array of 5 ints on heap\n" +
            "    int *arr = (int *)malloc(5 * sizeof(int));\n" +
            "\n" +
            "    if (arr == NULL) {\n" +
            "        printf(\"Allocation failed!\\n\");\n" +
            "        return 1;\n" +
            "    }\n" +
            "\n" +
            "    // Use the array\n" +
            "    for (int i = 0; i < 5; i++)\n" +
            "        arr[i] = i * 10;\n" +
            "\n" +
            "    // MUST free when done\n" +
            "    free(arr);\n" +
            "    return 0;\n" +
            "}", 18
        );
        dynCode.setPosition(-250, -50);
        play(new RevealCode(dynCode, 3.5));
        hold(1.0);

        // Highlight malloc
        dynCode.highlightLine(4);
        TextMob mallocNote = new TextMob("malloc returns a void* — cast to desired type")
                .setFontSize(20).setFillColor(Colors.YELLOW);
        mallocNote.setPosition(350, -150);
        play(new FadeIn(mallocNote, 0.3));
        hold(1.2);
        dynCode.clearHighlights();

        // Highlight free
        dynCode.highlightLine(16);
        TextMob freeNote = new TextMob("free() releases memory — forgetting causes memory leaks!")
                .setFontSize(20).setFillColor(Colors.RED);
        freeNote.setPosition(350, -100);
        play(new FadeIn(freeNote, 0.3));
        hold(1.5);
        dynCode.clearHighlights();

        // Highlight NULL check
        dynCode.highlightLine(6);
        TextMob nullNote = new TextMob("Always check for NULL — allocation can fail")
                .setFontSize(20).setFillColor(Colors.ORANGE);
        nullNote.setPosition(350, -50);
        play(new FadeIn(nullNote, 0.3));
        hold(1.5);
        dynCode.clearHighlights();

        play(new FadeOut(dynHeading, 0.3), new FadeOut(dynDesc, 0.3),
             new FadeOut(dynCode, 0.3), new FadeOut(mallocNote, 0.3),
             new FadeOut(freeNote, 0.3), new FadeOut(nullNote, 0.3));

        // ===== SECTION 9: Course Roadmap =====
        TextMob roadHeading = new TextMob("Course Roadmap")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.BLUE);
        roadHeading.setPosition(0, -420);
        play(new Write(roadHeading, 0.8));
        hold(0.3);

        String[] topics = {
            "1. Introduction & C Basics",
            "2. Arrays & Sorting",
            "3. Linked Lists",
            "4. Stacks & Queues",
            "5. Trees & BST",
            "6. Hashing / Dictionaries",
            "7. Heaps & Priority Queues",
            "8. Graphs & Algorithms"
        };
        javafx.scene.paint.Color[] topicColors = {
            Colors.TEAL, Colors.GREEN, Colors.ORANGE, Colors.PURPLE,
            Colors.GOLD, Colors.RED, Colors.PINK, Colors.LIGHT_BLUE
        };

        TextMob[] topicMobs = new TextMob[topics.length];
        RectMob[] topicBoxes = new RectMob[topics.length];
        for (int i = 0; i < topics.length; i++) {
            int col = i % 2;
            int row = i / 2;
            double xPos = (col == 0) ? -300 : 300;
            double yPos = -300 + row * 100;

            topicBoxes[i] = new RectMob(450, 60);
            topicBoxes[i].setCornerRadius(10);
            topicBoxes[i].setFillColor(Colors.withAlpha(topicColors[i], 0.15));
            topicBoxes[i].setStrokeColor(topicColors[i]);
            topicBoxes[i].setPosition(xPos, yPos);

            topicMobs[i] = new TextMob(topics[i])
                    .setFontSize(22)
                    .setFillColor(topicColors[i]);
            topicMobs[i].setPosition(xPos, yPos);

            play(new FadeIn(topicBoxes[i], 0.3), new FadeIn(topicMobs[i], 0.3));
            hold(0.3);
        }

        // Draw connecting arrows between sequential topics
        ArrowMob roadArrow1 = new ArrowMob(-75, -300, 75, -300);
        roadArrow1.setStrokeColor(Colors.LIGHT_GRAY);
        ArrowMob roadArrow2 = new ArrowMob(525, -300, 525, -200);
        roadArrow2.setStrokeColor(Colors.LIGHT_GRAY);
        play(new DrawArrow(roadArrow1, 0.3), new DrawArrow(roadArrow2, 0.3));
        hold(2.0);

        // Fade out roadmap
        play(new FadeOut(roadHeading, 0.3),
             new FadeOut(roadArrow1, 0.3), new FadeOut(roadArrow2, 0.3));
        for (int i = 0; i < topics.length; i++) {
            play(new FadeOut(topicBoxes[i], 0.2), new FadeOut(topicMobs[i], 0.2));
        }

        // ===== SECTION 10: Summary =====
        TextMob sumTitle = new TextMob("Key Takeaways")
                .setFontSize(52)
                .setBold()
                .setFillColor(Colors.BLUE);
        sumTitle.setPosition(0, -300);

        play(new FadeIn(sumTitle, 0.5));

        String[] takeaways = {
            "Data Structures organize data for efficient operations",
            "Algorithms are step-by-step procedures with measurable cost",
            "ADTs separate interface (WHAT) from implementation (HOW)",
            "Choosing the right structure can mean 1M steps vs 20 steps",
            "C gives direct memory control: stack (auto) vs heap (manual)",
            "Pointers are addresses — foundation of dynamic data structures",
            "Always free() what you malloc() — avoid memory leaks"
        };

        TextMob[] sumPoints = new TextMob[takeaways.length];
        for (int i = 0; i < takeaways.length; i++) {
            sumPoints[i] = new TextMob("• " + takeaways[i])
                    .setFontSize(23)
                    .setFillColor(Colors.WHITE);
            sumPoints[i].setPosition(0, -200 + i * 55);

            play(new Write(sumPoints[i], 0.8));
            hold(0.3);
        }
        hold(3.0);

        // Final fade out
        play(new FadeOut(sumTitle, 1.0));
        for (TextMob pt : sumPoints) {
            play(new FadeOut(pt, 0.8));
        }
    }
}
