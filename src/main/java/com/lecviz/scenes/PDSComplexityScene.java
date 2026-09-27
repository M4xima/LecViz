package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Scene;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Vec2;

/**
 * PDS Lecture: Complexity Analysis.
 * Covers Big-O, Big-Omega, Big-Theta, common complexities,
 * loop analysis, recursion analysis, and space complexity.
 */
public class PDSComplexityScene extends Scene {

    @Override
    public void construct() {

        // ===== SECTION 1: Title Card =====
        TextMob title = new TextMob("Complexity Analysis")
                .setFontSize(58)
                .setBold()
                .setFillColor(Colors.TEAL);
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

        // ===== SECTION 2: Why Analyze Algorithms? =====
        TextMob heading = new TextMob("Why Analyze Algorithms?")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.TEAL);
        heading.setPosition(0, -400);

        TextMob desc = new TextMob("Two programs can solve the same problem — but one may be far faster")
                .setFontSize(24)
                .setFillColor(Colors.LIGHT_GRAY);
        desc.setPosition(0, -340);

        play(new Write(heading, 0.8));
        play(new Write(desc, 1.0));
        hold(0.8);

        // Show two sorting approaches
        RectMob approach1 = new RectMob(380, 120);
        approach1.setCornerRadius(10);
        approach1.setFillColor(Colors.withAlpha(Colors.RED, 0.15));
        approach1.setStrokeColor(Colors.RED);
        approach1.setPosition(-300, -200);

        TextMob a1Title = new TextMob("Approach A: Bubble Sort")
                .setFontSize(22).setBold().setFillColor(Colors.RED);
        a1Title.setPosition(-300, -240);

        TextMob a1Detail = new TextMob("~n² comparisons\nn=10,000 -> 100,000,000 ops")
                .setFontSize(18).setFillColor(Colors.WHITE);
        a1Detail.setPosition(-300, -190);

        RectMob approach2 = new RectMob(380, 120);
        approach2.setCornerRadius(10);
        approach2.setFillColor(Colors.withAlpha(Colors.GREEN, 0.15));
        approach2.setStrokeColor(Colors.GREEN);
        approach2.setPosition(300, -200);

        TextMob a2Title = new TextMob("Approach B: Merge Sort")
                .setFontSize(22).setBold().setFillColor(Colors.GREEN);
        a2Title.setPosition(300, -240);

        TextMob a2Detail = new TextMob("~n log n comparisons\nn=10,000 -> 130,000 ops")
                .setFontSize(18).setFillColor(Colors.WHITE);
        a2Detail.setPosition(300, -190);

        play(new FadeIn(approach1, 0.4), new FadeIn(a1Title, 0.3), new FadeIn(a1Detail, 0.4));
        play(new FadeIn(approach2, 0.4), new FadeIn(a2Title, 0.3), new FadeIn(a2Detail, 0.4));
        hold(1.0);

        TextMob speedup = new TextMob("770x faster! That is why analysis matters.")
                .setFontSize(28).setBold().setFillColor(Colors.GOLD);
        speedup.setPosition(0, -80);
        play(new Write(speedup, 1.0));
        hold(2.0);

        play(new FadeOut(heading, 0.3), new FadeOut(desc, 0.3),
             new FadeOut(approach1, 0.3), new FadeOut(a1Title, 0.3), new FadeOut(a1Detail, 0.3),
             new FadeOut(approach2, 0.3), new FadeOut(a2Title, 0.3), new FadeOut(a2Detail, 0.3),
             new FadeOut(speedup, 0.3));

        // ===== SECTION 3: Counting Operations =====
        TextMob countHeading = new TextMob("Counting Operations")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.BLUE);
        countHeading.setPosition(0, -400);
        play(new Write(countHeading, 0.8));

        CodeBlock countCode = new CodeBlock(
            "int sum = 0;          // 1 assignment\n" +
            "for (int i = 0;       // 1 assignment\n" +
            "     i < n;           // n+1 comparisons\n" +
            "     i++) {           // n increments\n" +
            "    sum += arr[i];    // n additions\n" +
            "}                     \n" +
            "return sum;           // 1 return", 20
        );
        countCode.setPosition(-200, -150);
        play(new RevealCode(countCode, 3.0));
        hold(1.0);

        TextMob totalOps = new TextMob("Total: 1 + 1 + (n+1) + n + n + 1 = 3n + 4")
                .setFontSize(24).setFillColor(Colors.YELLOW);
        totalOps.setPosition(0, 100);
        play(new Write(totalOps, 1.0));
        hold(1.0);

        TextMob simplify = new TextMob("As n grows, constants don't matter -> O(n)")
                .setFontSize(26).setBold().setFillColor(Colors.GREEN);
        simplify.setPosition(0, 170);
        play(new Write(simplify, 1.0));
        hold(2.0);

        play(new FadeOut(countHeading, 0.3), new FadeOut(countCode, 0.3),
             new FadeOut(totalOps, 0.3), new FadeOut(simplify, 0.3));

        // ===== SECTION 4: Big-O Notation =====
        TextMob bigOHeading = new TextMob("Big-O Notation")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.PURPLE);
        bigOHeading.setPosition(0, -400);
        play(new Write(bigOHeading, 0.8));

        // Formal definition
        TextMob formalDef = new TextMob("f(n) = O(g(n)) if there exist constants c > 0 and n₀")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        formalDef.setPosition(0, -330);

        TextMob formalDef2 = new TextMob("such that f(n) ≤ c · g(n) for all n ≥ n₀")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        formalDef2.setPosition(0, -295);

        play(new Write(formalDef, 1.0));
        play(new Write(formalDef2, 1.0));
        hold(1.0);

        // Intuitive meaning
        RectMob intuitionBox = new RectMob(700, 80);
        intuitionBox.setCornerRadius(12);
        intuitionBox.setFillColor(Colors.withAlpha(Colors.PURPLE, 0.15));
        intuitionBox.setStrokeColor(Colors.PURPLE);
        intuitionBox.setPosition(0, -200);

        TextMob intuition = new TextMob("Intuition: O(g(n)) = upper bound on growth rate as n -> infinity")
                .setFontSize(24).setFillColor(Colors.YELLOW);
        intuition.setPosition(0, -200);

        play(new FadeIn(intuitionBox, 0.4));
        play(new Write(intuition, 1.0));
        hold(1.0);

        // Example
        TextMob example = new TextMob("Example: f(n) = 5n² + 3n + 7")
                .setFontSize(24).setFillColor(Colors.WHITE);
        example.setPosition(0, -120);
        play(new Write(example, 0.8));

        TextMob exStep1 = new TextMob("5n² + 3n + 7 ≤ 5n² + 3n² + 7n² = 15n²  (for n ≥ 1)")
                .setFontSize(22).setFillColor(Colors.TEAL);
        exStep1.setPosition(0, -70);
        play(new Write(exStep1, 1.0));
        hold(0.5);

        TextMob exResult = new TextMob("So f(n) = O(n²)  with c=15, n₀=1")
                .setFontSize(26).setBold().setFillColor(Colors.GREEN);
        exResult.setPosition(0, -20);
        play(new Write(exResult, 0.8));
        hold(2.0);

        play(new FadeOut(bigOHeading, 0.3), new FadeOut(formalDef, 0.3), new FadeOut(formalDef2, 0.3),
             new FadeOut(intuitionBox, 0.3), new FadeOut(intuition, 0.3),
             new FadeOut(example, 0.3), new FadeOut(exStep1, 0.3), new FadeOut(exResult, 0.3));

        // ===== SECTION 5: Common Complexities =====
        TextMob commonHeading = new TextMob("Common Complexity Classes")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.GOLD);
        commonHeading.setPosition(0, -420);
        play(new Write(commonHeading, 0.8));
        hold(0.3);

        // Show each complexity with n=1024 example
        String[] names =    {"O(1)",    "O(log n)", "O(n)",  "O(n log n)", "O(n²)",      "O(2ⁿ)"};
        String[] examples = {"1 op",    "10 ops",   "1K ops","10K ops",    "1M ops",       "≈1.8×10³⁰⁸"};
        String[] descs =    {"Constant","Logarithmic","Linear","Linearithmic","Quadratic","Exponential"};
        javafx.scene.paint.Color[] cColors = {
            Colors.GREEN, Colors.TEAL, Colors.BLUE, Colors.GOLD, Colors.ORANGE, Colors.RED
        };

        // Visualize as bars using RectMob (proportional heights)
        int[] barHeights = {10, 30, 60, 100, 180, 300};

        for (int i = 0; i < names.length; i++) {
            double xPos = -500 + i * 190;

            RectMob bar = new RectMob(100, barHeights[i]);
            bar.setCornerRadius(4);
            bar.setFillColor(Colors.withAlpha(cColors[i], 0.5));
            bar.setStrokeColor(cColors[i]);
            bar.setPosition(xPos, 100 - barHeights[i] / 2.0);

            TextMob nameLabel = new TextMob(names[i])
                    .setFontSize(20).setBold().setFillColor(cColors[i]);
            nameLabel.setPosition(xPos, 100 + barHeights[i] / 2.0 + 25);

            TextMob descLabel = new TextMob(descs[i])
                    .setFontSize(14).setFillColor(Colors.GRAY);
            descLabel.setPosition(xPos, 100 + barHeights[i] / 2.0 + 50);

            TextMob valLabel = new TextMob(examples[i])
                    .setFontSize(16).setFillColor(Colors.WHITE);
            valLabel.setPosition(xPos, 100 - barHeights[i] / 2.0 - 15);

            play(new FadeIn(bar, 0.3), new FadeIn(nameLabel, 0.2),
                 new FadeIn(descLabel, 0.2), new FadeIn(valLabel, 0.2));
            hold(0.3);
        }

        TextMob nVal = new TextMob("(values shown for n = 1024)")
                .setFontSize(18).setFillColor(Colors.GRAY);
        nVal.setPosition(0, 330);
        play(new FadeIn(nVal, 0.3));
        hold(3.0);

        // Fade all bars (using a batch approach)
        play(new FadeOut(commonHeading, 0.3), new FadeOut(nVal, 0.3));
        // We need a clean transition — hold and proceed
        hold(0.5);

        // ===== SECTION 6: Drop Constants & Lower-Order Terms =====
        TextMob dropHeading = new TextMob("Dropping Constants & Lower-Order Terms")
                .setFontSize(44)
                .setBold()
                .setFillColor(Colors.ORANGE);
        dropHeading.setPosition(0, -400);
        play(new Write(dropHeading, 0.8));

        TextMob dropRule = new TextMob("Big-O cares about the dominant term as n -> infinity")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        dropRule.setPosition(0, -340);
        play(new Write(dropRule, 1.0));
        hold(0.5);

        // Example 1
        TextMob ex1 = new TextMob("3n² + 5n + 2")
                .setFontSize(36).setFillColor(Colors.WHITE);
        ex1.setPosition(-300, -230);
        play(new FadeIn(ex1, 0.4));

        ArrowMob exArrow1 = new ArrowMob(-120, -230, 50, -230);
        exArrow1.setStrokeColor(Colors.GOLD);
        play(new DrawArrow(exArrow1, 0.4));

        TextMob ex1Result = new TextMob("O(n²)")
                .setFontSize(36).setBold().setFillColor(Colors.GREEN);
        ex1Result.setPosition(200, -230);
        play(new FadeIn(ex1Result, 0.4));

        TextMob ex1Why = new TextMob("drop 5n, drop 2, drop constant 3")
                .setFontSize(18).setFillColor(Colors.GRAY);
        ex1Why.setPosition(0, -180);
        play(new Write(ex1Why, 0.6));
        hold(1.0);

        // Example 2
        TextMob ex2 = new TextMob("100n + 999")
                .setFontSize(36).setFillColor(Colors.WHITE);
        ex2.setPosition(-300, -100);
        play(new FadeIn(ex2, 0.4));

        ArrowMob exArrow2 = new ArrowMob(-120, -100, 50, -100);
        exArrow2.setStrokeColor(Colors.GOLD);
        play(new DrawArrow(exArrow2, 0.4));

        TextMob ex2Result = new TextMob("O(n)")
                .setFontSize(36).setBold().setFillColor(Colors.GREEN);
        ex2Result.setPosition(200, -100);
        play(new FadeIn(ex2Result, 0.4));
        hold(1.0);

        // Example 3
        TextMob ex3 = new TextMob("2ⁿ + n³")
                .setFontSize(36).setFillColor(Colors.WHITE);
        ex3.setPosition(-300, 20);
        play(new FadeIn(ex3, 0.4));

        ArrowMob exArrow3 = new ArrowMob(-120, 20, 50, 20);
        exArrow3.setStrokeColor(Colors.GOLD);
        play(new DrawArrow(exArrow3, 0.4));

        TextMob ex3Result = new TextMob("O(2ⁿ)")
                .setFontSize(36).setBold().setFillColor(Colors.RED);
        ex3Result.setPosition(200, 20);
        play(new FadeIn(ex3Result, 0.4));

        TextMob ex3Why = new TextMob("exponential dominates polynomial")
                .setFontSize(18).setFillColor(Colors.GRAY);
        ex3Why.setPosition(0, 70);
        play(new Write(ex3Why, 0.6));
        hold(2.0);

        play(new FadeOut(dropHeading, 0.3), new FadeOut(dropRule, 0.3),
             new FadeOut(ex1, 0.3), new FadeOut(exArrow1, 0.3), new FadeOut(ex1Result, 0.3), new FadeOut(ex1Why, 0.3),
             new FadeOut(ex2, 0.3), new FadeOut(exArrow2, 0.3), new FadeOut(ex2Result, 0.3),
             new FadeOut(ex3, 0.3), new FadeOut(exArrow3, 0.3), new FadeOut(ex3Result, 0.3), new FadeOut(ex3Why, 0.3));

        // ===== SECTION 7: Best / Worst / Average Case =====
        TextMob caseHeading = new TextMob("Best / Worst / Average Case")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.GREEN);
        caseHeading.setPosition(0, -400);
        play(new Write(caseHeading, 0.8));

        TextMob caseSub = new TextMob("Example: Linear Search for key in array of n elements")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        caseSub.setPosition(0, -340);
        play(new Write(caseSub, 1.0));
        hold(0.5);

        ArrayMob caseArr = new ArrayMob("7", "3", "9", "1", "5", "8", "2", "6");
        caseArr.setLabel("arr");
        caseArr.setPosition(0, -220);
        play(new FadeIn(caseArr, 0.5));
        hold(0.5);

        // Best case
        TextMob bestLabel = new TextMob("Best Case: key = 7 (first element)")
                .setFontSize(22).setFillColor(Colors.GREEN);
        bestLabel.setPosition(0, -130);
        play(new Write(bestLabel, 0.8));
        caseArr.highlight(0);
        caseArr.setCellColor(0, Colors.withAlpha(Colors.GREEN, 0.3));
        hold(0.8);

        TextMob bestResult = new TextMob("1 comparison -> O(1)")
                .setFontSize(22).setBold().setFillColor(Colors.GREEN);
        bestResult.setPosition(0, -90);
        play(new FadeIn(bestResult, 0.3));
        hold(1.0);
        caseArr.clearHighlights();
        caseArr.clearCellColors();

        // Worst case
        TextMob worstLabel = new TextMob("Worst Case: key = 6 (last element) or not found")
                .setFontSize(22).setFillColor(Colors.RED);
        worstLabel.setPosition(0, -30);
        play(new Write(worstLabel, 0.8));

        for (int i = 0; i < 8; i++) {
            caseArr.highlight(i);
            hold(0.15);
            caseArr.unhighlight(i);
        }
        caseArr.highlight(7);
        caseArr.setCellColor(7, Colors.withAlpha(Colors.RED, 0.3));

        TextMob worstResult = new TextMob("n comparisons -> O(n)")
                .setFontSize(22).setBold().setFillColor(Colors.RED);
        worstResult.setPosition(0, 10);
        play(new FadeIn(worstResult, 0.3));
        hold(1.0);
        caseArr.clearHighlights();
        caseArr.clearCellColors();

        // Average case
        TextMob avgLabel = new TextMob("Average Case: key equally likely at any position")
                .setFontSize(22).setFillColor(Colors.GOLD);
        avgLabel.setPosition(0, 70);
        play(new Write(avgLabel, 0.8));

        caseArr.highlight(3);
        caseArr.highlight(4);

        TextMob avgResult = new TextMob("n/2 comparisons on average -> O(n)")
                .setFontSize(22).setBold().setFillColor(Colors.GOLD);
        avgResult.setPosition(0, 110);
        play(new FadeIn(avgResult, 0.3));
        hold(2.0);

        play(new FadeOut(caseHeading, 0.3), new FadeOut(caseSub, 0.3), new FadeOut(caseArr, 0.3),
             new FadeOut(bestLabel, 0.3), new FadeOut(bestResult, 0.3),
             new FadeOut(worstLabel, 0.3), new FadeOut(worstResult, 0.3),
             new FadeOut(avgLabel, 0.3), new FadeOut(avgResult, 0.3));

        // ===== SECTION 8: Big-Omega and Big-Theta =====
        TextMob omegaHeading = new TextMob("Ω and Θ Notation")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.PURPLE);
        omegaHeading.setPosition(0, -400);
        play(new Write(omegaHeading, 0.8));

        // Big-Omega box
        RectMob omegaBox = new RectMob(750, 100);
        omegaBox.setCornerRadius(10);
        omegaBox.setFillColor(Colors.withAlpha(Colors.ORANGE, 0.12));
        omegaBox.setStrokeColor(Colors.ORANGE);
        omegaBox.setPosition(0, -270);

        TextMob omegaTitle = new TextMob("Ω(g(n)) — Lower Bound")
                .setFontSize(26).setBold().setFillColor(Colors.ORANGE);
        omegaTitle.setPosition(0, -300);

        TextMob omegaDef = new TextMob("f(n) ≥ c · g(n) for all n ≥ n₀  |  \"At least this fast\"")
                .setFontSize(20).setFillColor(Colors.WHITE);
        omegaDef.setPosition(0, -250);

        play(new FadeIn(omegaBox, 0.4), new FadeIn(omegaTitle, 0.3));
        play(new Write(omegaDef, 0.8));
        hold(0.8);

        // Big-Theta box
        RectMob thetaBox = new RectMob(750, 100);
        thetaBox.setCornerRadius(10);
        thetaBox.setFillColor(Colors.withAlpha(Colors.TEAL, 0.12));
        thetaBox.setStrokeColor(Colors.TEAL);
        thetaBox.setPosition(0, -130);

        TextMob thetaTitle = new TextMob("Θ(g(n)) — Tight Bound")
                .setFontSize(26).setBold().setFillColor(Colors.TEAL);
        thetaTitle.setPosition(0, -160);

        TextMob thetaDef = new TextMob("c₁ · g(n) ≤ f(n) ≤ c₂ · g(n)  |  \"Exactly this growth rate\"")
                .setFontSize(20).setFillColor(Colors.WHITE);
        thetaDef.setPosition(0, -110);

        play(new FadeIn(thetaBox, 0.4), new FadeIn(thetaTitle, 0.3));
        play(new Write(thetaDef, 0.8));
        hold(1.0);

        // Summary diagram
        TextMob summary = new TextMob("O = upper bound (worst guarantee)  |  Ω = lower bound  |  Θ = tight")
                .setFontSize(22).setFillColor(Colors.GOLD);
        summary.setPosition(0, -20);
        play(new Write(summary, 1.2));
        hold(2.0);

        play(new FadeOut(omegaHeading, 0.3),
             new FadeOut(omegaBox, 0.3), new FadeOut(omegaTitle, 0.3), new FadeOut(omegaDef, 0.3),
             new FadeOut(thetaBox, 0.3), new FadeOut(thetaTitle, 0.3), new FadeOut(thetaDef, 0.3),
             new FadeOut(summary, 0.3));

        // ===== SECTION 9: Analyzing Loops =====
        TextMob loopHeading = new TextMob("Analyzing Loops")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.BLUE);
        loopHeading.setPosition(0, -420);
        play(new Write(loopHeading, 0.8));

        // Single loop
        TextMob singleLabel = new TextMob("Single Loop")
                .setFontSize(28).setBold().setFillColor(Colors.TEAL);
        singleLabel.setPosition(-400, -340);
        play(new FadeIn(singleLabel, 0.3));

        CodeBlock singleLoop = new CodeBlock(
            "for (int i = 0; i < n; i++) {\n" +
            "    // O(1) work\n" +
            "}", 20
        );
        singleLoop.setPosition(-400, -250);
        play(new RevealCode(singleLoop, 1.0));

        TextMob singleResult = new TextMob("-> O(n)")
                .setFontSize(24).setBold().setFillColor(Colors.GREEN);
        singleResult.setPosition(-400, -170);
        play(new FadeIn(singleResult, 0.3));
        hold(1.0);

        // Nested loops
        TextMob nestedLabel = new TextMob("Nested Loops")
                .setFontSize(28).setBold().setFillColor(Colors.ORANGE);
        nestedLabel.setPosition(300, -340);
        play(new FadeIn(nestedLabel, 0.3));

        CodeBlock nestedLoop = new CodeBlock(
            "for (int i = 0; i < n; i++) {\n" +
            "    for (int j = 0; j < n; j++) {\n" +
            "        // O(1) work\n" +
            "    }\n" +
            "}", 20
        );
        nestedLoop.setPosition(300, -240);
        play(new RevealCode(nestedLoop, 1.5));

        TextMob nestedResult = new TextMob("-> O(n²)")
                .setFontSize(24).setBold().setFillColor(Colors.RED);
        nestedResult.setPosition(300, -120);
        play(new FadeIn(nestedResult, 0.3));
        hold(1.0);

        // Logarithmic loop
        TextMob logLabel = new TextMob("Halving Loop")
                .setFontSize(28).setBold().setFillColor(Colors.PURPLE);
        logLabel.setPosition(-400, -50);
        play(new FadeIn(logLabel, 0.3));

        CodeBlock logLoop = new CodeBlock(
            "int i = n;\n" +
            "while (i > 1) {\n" +
            "    i = i / 2;  // halves each time\n" +
            "}", 20
        );
        logLoop.setPosition(-400, 30);
        play(new RevealCode(logLoop, 1.0));

        TextMob logResult = new TextMob("-> O(log n)")
                .setFontSize(24).setBold().setFillColor(Colors.GREEN);
        logResult.setPosition(-400, 130);
        play(new FadeIn(logResult, 0.3));

        TextMob logExplain = new TextMob("n -> n/2 -> n/4 -> ... -> 1  (log₂n steps)")
                .setFontSize(18).setFillColor(Colors.GRAY);
        logExplain.setPosition(-400, 170);
        play(new Write(logExplain, 0.8));
        hold(2.0);

        play(new FadeOut(loopHeading, 0.3),
             new FadeOut(singleLabel, 0.3), new FadeOut(singleLoop, 0.3), new FadeOut(singleResult, 0.3),
             new FadeOut(nestedLabel, 0.3), new FadeOut(nestedLoop, 0.3), new FadeOut(nestedResult, 0.3),
             new FadeOut(logLabel, 0.3), new FadeOut(logLoop, 0.3), new FadeOut(logResult, 0.3), new FadeOut(logExplain, 0.3));

        // ===== SECTION 10: Analyzing Recursion =====
        TextMob recHeading = new TextMob("Analyzing Recursive Algorithms")
                .setFontSize(44)
                .setBold()
                .setFillColor(Colors.RED);
        recHeading.setPosition(0, -420);
        play(new Write(recHeading, 0.8));

        TextMob recSub = new TextMob("Example: Fibonacci — fib(n) = fib(n-1) + fib(n-2)")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        recSub.setPosition(0, -360);
        play(new Write(recSub, 1.0));
        hold(0.5);

        // Show recursion tree for fib(5)
        //           fib(5)
        //         /        \
        //     fib(4)      fib(3)
        //     /    \       /    \
        //  fib(3) fib(2) fib(2) fib(1)
        TreeMob fibTree = new TreeMob("f(5)", "f(4)", "f(3)", "f(3)", "f(2)", "f(2)", "f(1)");
        fibTree.setPosition(0, -120);
        play(new FadeIn(fibTree, 1.5));
        hold(1.0);

        // Highlight duplicate computations
        TextMob dupNote = new TextMob("Notice: fib(3) computed TWICE, fib(2) computed THREE times!")
                .setFontSize(22).setFillColor(Colors.RED);
        dupNote.setPosition(0, 100);
        play(new Write(dupNote, 1.0));

        fibTree.setNodeColor(2, Colors.withAlpha(Colors.RED, 0.5));
        fibTree.setNodeColor(3, Colors.withAlpha(Colors.RED, 0.5));
        hold(1.5);

        TextMob fibComplexity = new TextMob("Time: O(2ⁿ) — exponential! Each level doubles the work.")
                .setFontSize(24).setBold().setFillColor(Colors.ORANGE);
        fibComplexity.setPosition(0, 160);
        play(new Write(fibComplexity, 1.0));
        hold(1.0);

        TextMob fibFix = new TextMob("Fix: Dynamic Programming (memoization) -> O(n)")
                .setFontSize(22).setFillColor(Colors.GREEN);
        fibFix.setPosition(0, 210);
        play(new Write(fibFix, 0.8));
        hold(2.0);

        play(new FadeOut(recHeading, 0.3), new FadeOut(recSub, 0.3),
             new FadeOut(fibTree, 0.3), new FadeOut(dupNote, 0.3),
             new FadeOut(fibComplexity, 0.3), new FadeOut(fibFix, 0.3));

        // ===== SECTION 11: Space Complexity =====
        TextMob spaceHeading = new TextMob("Space Complexity")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.TEAL);
        spaceHeading.setPosition(0, -400);
        play(new Write(spaceHeading, 0.8));

        TextMob spaceDef = new TextMob("Extra memory used by the algorithm (beyond the input)")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        spaceDef.setPosition(0, -340);
        play(new Write(spaceDef, 1.0));
        hold(0.5);

        // In-place example
        RectMob inPlaceBox = new RectMob(420, 130);
        inPlaceBox.setCornerRadius(10);
        inPlaceBox.setFillColor(Colors.withAlpha(Colors.GREEN, 0.12));
        inPlaceBox.setStrokeColor(Colors.GREEN);
        inPlaceBox.setPosition(-300, -210);

        TextMob inPlaceTitle = new TextMob("Bubble Sort: O(1) space")
                .setFontSize(22).setBold().setFillColor(Colors.GREEN);
        inPlaceTitle.setPosition(-300, -255);

        TextMob inPlaceDetail = new TextMob("Sorts in-place — only needs\na single temp variable for swap")
                .setFontSize(18).setFillColor(Colors.WHITE);
        inPlaceDetail.setPosition(-300, -200);

        play(new FadeIn(inPlaceBox, 0.4), new FadeIn(inPlaceTitle, 0.3));
        play(new Write(inPlaceDetail, 0.8));
        hold(0.8);

        // Extra space example
        RectMob extraBox = new RectMob(420, 130);
        extraBox.setCornerRadius(10);
        extraBox.setFillColor(Colors.withAlpha(Colors.ORANGE, 0.12));
        extraBox.setStrokeColor(Colors.ORANGE);
        extraBox.setPosition(300, -210);

        TextMob extraTitle = new TextMob("Merge Sort: O(n) space")
                .setFontSize(22).setBold().setFillColor(Colors.ORANGE);
        extraTitle.setPosition(300, -255);

        TextMob extraDetail = new TextMob("Needs auxiliary arrays to merge\nhalves — proportional to n")
                .setFontSize(18).setFillColor(Colors.WHITE);
        extraDetail.setPosition(300, -200);

        play(new FadeIn(extraBox, 0.4), new FadeIn(extraTitle, 0.3));
        play(new Write(extraDetail, 0.8));
        hold(1.0);

        // Stack space
        TextMob stackSpace = new TextMob("Recursion also uses stack space: depth of recursion tree")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        stackSpace.setPosition(0, -90);
        play(new Write(stackSpace, 1.0));
        hold(2.0);

        play(new FadeOut(spaceHeading, 0.3), new FadeOut(spaceDef, 0.3),
             new FadeOut(inPlaceBox, 0.3), new FadeOut(inPlaceTitle, 0.3), new FadeOut(inPlaceDetail, 0.3),
             new FadeOut(extraBox, 0.3), new FadeOut(extraTitle, 0.3), new FadeOut(extraDetail, 0.3),
             new FadeOut(stackSpace, 0.3));

        // ===== SECTION 12: Comparison Table =====
        TextMob tableHeading = new TextMob("Complexity Comparison at a Glance")
                .setFontSize(44)
                .setBold()
                .setFillColor(Colors.GOLD);
        tableHeading.setPosition(0, -420);
        play(new Write(tableHeading, 0.8));
        hold(0.3);

        // Show ranking with ArrayMob style
        String[] ranked = {"O(1)", "O(log n)", "O(n)", "O(n log n)", "O(n²)", "O(2ⁿ)"};
        javafx.scene.paint.Color[] rankColors = {
            Colors.GREEN, Colors.TEAL, Colors.BLUE, Colors.GOLD, Colors.ORANGE, Colors.RED
        };
        String[] rankLabels = {"Best", "", "", "", "", "Worst"};
        String[] rankExamples = {"Hash lookup", "Binary search", "Linear scan", "Merge sort", "Bubble sort", "Subset enum"};

        for (int i = 0; i < ranked.length; i++) {
            double yPos = -300 + i * 80;

            TextMob rankName = new TextMob(ranked[i])
                    .setFontSize(28).setBold().setFillColor(rankColors[i]);
            rankName.setPosition(-300, yPos);

            TextMob rankEx = new TextMob(rankExamples[i])
                    .setFontSize(20).setFillColor(Colors.LIGHT_GRAY);
            rankEx.setPosition(100, yPos);

            // Connecting bar
            RectMob rankBar = new RectMob(30 + i * 80, 25);
            rankBar.setCornerRadius(4);
            rankBar.setFillColor(Colors.withAlpha(rankColors[i], 0.3));
            rankBar.setPosition(450, yPos);

            play(new FadeIn(rankName, 0.2), new FadeIn(rankEx, 0.2), new FadeIn(rankBar, 0.2));
            hold(0.2);
        }

        // Arrow from best to worst
        ArrowMob rankArrow = new ArrowMob(-500, -300, -500, 100);
        rankArrow.setStrokeColor(Colors.LIGHT_GRAY);
        play(new DrawArrow(rankArrow, 0.5));

        TextMob bestTag = new TextMob("FAST")
                .setFontSize(18).setBold().setFillColor(Colors.GREEN);
        bestTag.setPosition(-500, -330);
        TextMob worstTag = new TextMob("SLOW")
                .setFontSize(18).setBold().setFillColor(Colors.RED);
        worstTag.setPosition(-500, 130);
        play(new FadeIn(bestTag, 0.3), new FadeIn(worstTag, 0.3));
        hold(3.0);

        play(new FadeOut(tableHeading, 0.3), new FadeOut(rankArrow, 0.3),
             new FadeOut(bestTag, 0.3), new FadeOut(worstTag, 0.3));
        hold(0.3);

        // ===== SECTION 13: Summary =====
        TextMob sumTitle2 = new TextMob("Summary")
                .setFontSize(52)
                .setBold()
                .setFillColor(Colors.TEAL);
        sumTitle2.setPosition(0, -300);
        play(new FadeIn(sumTitle2, 0.5));

        String[] sumPoints = {
            "Big-O gives the upper bound on growth rate",
            "Focus on dominant term — drop constants and lower-order terms",
            "Analyze loops: single O(n), nested O(n²), halving O(log n)",
            "Recursion: draw the call tree, count total work",
            "Space complexity counts extra memory beyond input",
            "O(1) < O(log n) < O(n) < O(n log n) < O(n²) < O(2ⁿ)"
        };

        for (int i = 0; i < sumPoints.length; i++) {
            TextMob pt = new TextMob("• " + sumPoints[i])
                    .setFontSize(23).setFillColor(Colors.WHITE);
            pt.setPosition(0, -200 + i * 55);
            play(new Write(pt, 0.8));
            hold(0.3);
        }
        hold(3.0);

        play(new FadeOut(sumTitle2, 1.0));
    }
}
