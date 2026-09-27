package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Scene;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Vec2;

/**
 * PDS Lecture: Stacks and Queues — comprehensive coverage.
 * Covers stack ADT, array implementation, overflow/underflow,
 * balanced parentheses, postfix evaluation, function call stack,
 * queue ADT, circular queue, and comparison.
 */
public class PDSStackScene extends Scene {

    @Override
    public void construct() {

        // ================================================================
        // SECTION 1: Title Card
        // ================================================================
        TextMob title = new TextMob("Stacks & Queues")
                .setFontSize(72).setBold().setFillColor(Colors.PURPLE);
        title.setPosition(0, -120);

        TextMob subtitle = new TextMob("Programming and Data Structures")
                .setFontSize(30).setFillColor(Colors.LIGHT_GRAY);
        subtitle.setPosition(0, -30);

        TextMob credit = new TextMob("Prof. Rupesh Nasre — IIT Madras")
                .setFontSize(24).setFillColor(Colors.GRAY);
        credit.setPosition(0, 30);

        play(new FadeIn(title, 1.2));
        play(new Write(subtitle, 1.0));
        play(new FadeIn(credit, 0.6));
        hold(2.5);

        play(new FadeOut(title, 0.5), new FadeOut(subtitle, 0.5), new FadeOut(credit, 0.5));

        // ================================================================
        // SECTION 2: Stack ADT — LIFO Concept
        // ================================================================
        TextMob sHeading = new TextMob("Stack: Last In, First Out (LIFO)")
                .setFontSize(44).setBold().setFillColor(Colors.PURPLE);
        sHeading.setPosition(0, -420);
        play(new Write(sHeading, 0.8));

        TextMob analogy = new TextMob("Think of a stack of plates: you can only add/remove from the TOP")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        analogy.setPosition(0, -360);
        play(new Write(analogy, 1.2));

        // Visual plate stack
        StackMob plateStack = new StackMob();
        plateStack.setLabel("Stack of Plates");
        plateStack.setPosition(-300, 0);
        add(plateStack);

        TextMob plateNote = new TextMob("")
                .setFontSize(24).setFillColor(Colors.GREEN);
        plateNote.setPosition(-300, 280);
        add(plateNote);

        // Stack the plates
        String[] plates = {"Plate A", "Plate B", "Plate C", "Plate D"};
        for (String p : plates) {
            plateStack.push(p);
            plateNote.setText("Place " + p + " on top");
            hold(0.8);
        }
        hold(0.5);

        // Remove from top
        plateNote.setFillColor(Colors.RED);
        for (int i = 0; i < 2; i++) {
            String removed = plateStack.peek();
            plateStack.setHighlightTop(1);
            plateNote.setText("Remove " + removed + " from top");
            hold(0.6);
            plateStack.pop();
            plateStack.setHighlightTop(0);
            hold(0.4);
        }

        TextMob lifoVisual = new TextMob("LIFO: Last plate placed = First plate removed")
                .setFontSize(24).setFillColor(Colors.GOLD);
        lifoVisual.setPosition(200, -200);
        play(new Write(lifoVisual, 1.0));

        TextMob ops = new TextMob("Core operations: push(item), pop(), peek(), isEmpty()")
                .setFontSize(22).setFillColor(Colors.TEAL);
        ops.setPosition(200, -130);
        play(new Write(ops, 1.0));
        hold(2.0);

        play(new FadeOut(plateStack, 0.3), new FadeOut(sHeading, 0.3),
             new FadeOut(analogy, 0.3), new FadeOut(plateNote, 0.3),
             new FadeOut(lifoVisual, 0.3), new FadeOut(ops, 0.3));

        // ================================================================
        // SECTION 3: Stack Operations with Array Visualization
        // ================================================================
        TextMob opsHeading = new TextMob("Stack Operations — Step by Step")
                .setFontSize(44).setBold().setFillColor(Colors.PURPLE);
        opsHeading.setPosition(0, -420);
        play(new Write(opsHeading, 0.8));

        // Array-based view and stack view side by side
        ArrayMob stackArr = new ArrayMob("_", "_", "_", "_", "_");
        stackArr.setLabel("Array implementation (capacity=5)");
        stackArr.setPosition(0, -260);
        play(new FadeIn(stackArr, 0.7));

        StackMob stackViz = new StackMob();
        stackViz.setLabel("Stack view");
        stackViz.setPosition(-400, 0);
        add(stackViz);

        TextMob topVar = new TextMob("top = -1 (empty)")
                .setFontSize(24).setFillColor(Colors.ORANGE);
        topVar.setPosition(0, -170);
        add(topVar);

        TextMob opAction = new TextMob("")
                .setFontSize(24).setFillColor(Colors.YELLOW);
        opAction.setPosition(300, 0);
        add(opAction);

        // Push operations
        String[] pushVals = {"10", "25", "3", "42"};
        int topIdx = -1;
        for (String val : pushVals) {
            topIdx++;
            opAction.setText("push(" + val + ")");
            opAction.setFillColor(Colors.GREEN);
            stackArr.setValue(topIdx, val);
            stackArr.highlight(topIdx);
            stackArr.setPointer(topIdx, "top");
            stackViz.push(val);
            stackViz.setHighlightTop(1);
            topVar.setText("top = " + topIdx);
            hold(1.0);
            stackArr.unhighlight(topIdx);
            stackViz.setHighlightTop(0);
        }
        hold(0.5);

        // Peek
        opAction.setText("peek() = " + pushVals[topIdx]);
        opAction.setFillColor(Colors.TEAL);
        stackArr.highlight(topIdx);
        stackViz.setHighlightTop(1);
        hold(1.2);
        stackArr.unhighlight(topIdx);
        stackViz.setHighlightTop(0);

        // Pop operations
        for (int i = 0; i < 2; i++) {
            String popped = stackViz.peek();
            opAction.setText("pop() -> " + popped);
            opAction.setFillColor(Colors.RED);
            stackArr.highlight(topIdx);
            stackViz.setHighlightTop(1);
            hold(0.6);
            stackArr.setValue(topIdx, "_");
            stackArr.unhighlight(topIdx);
            stackViz.pop();
            stackViz.setHighlightTop(0);
            topIdx--;
            stackArr.setPointer(topIdx, "top");
            topVar.setText("top = " + topIdx);
            hold(0.6);
        }
        hold(1.0);

        play(new FadeOut(stackArr, 0.3), new FadeOut(stackViz, 0.3),
             new FadeOut(opsHeading, 0.3), new FadeOut(topVar, 0.3),
             new FadeOut(opAction, 0.3));

        // ================================================================
        // SECTION 4: Array-Based Stack Implementation
        // ================================================================
        TextMob implHeading = new TextMob("Array-Based Stack in C")
                .setFontSize(44).setBold().setFillColor(Colors.PURPLE);
        implHeading.setPosition(0, -420);
        play(new Write(implHeading, 0.8));

        CodeBlock stackCode = new CodeBlock(
            "#define MAX 100\n" +
            "int stack[MAX];\n" +
            "int top = -1;\n" +
            "\n" +
            "int isEmpty() { return top == -1; }\n" +
            "int isFull()  { return top == MAX - 1; }\n" +
            "\n" +
            "void push(int val) {\n" +
            "    if (isFull()) {\n" +
            "        printf(\"Stack Overflow!\\n\");\n" +
            "        return;\n" +
            "    }\n" +
            "    stack[++top] = val;\n" +
            "}\n" +
            "\n" +
            "int pop() {\n" +
            "    if (isEmpty()) {\n" +
            "        printf(\"Stack Underflow!\\n\");\n" +
            "        return -1;\n" +
            "    }\n" +
            "    return stack[top--];\n" +
            "}\n" +
            "\n" +
            "int peek() {\n" +
            "    if (isEmpty()) return -1;\n" +
            "    return stack[top];\n" +
            "}", 15
        );
        stackCode.setPosition(0, 0);
        play(new RevealCode(stackCode, 5.0));

        // Highlight key parts
        stackCode.highlightLine(12);
        TextMob pushNote = new TextMob("push: increment top, then store value")
                .setFontSize(20).setFillColor(Colors.TEAL);
        pushNote.setPosition(0, 380);
        play(new FadeIn(pushNote, 0.3));
        hold(1.5);
        stackCode.clearHighlights();
        play(new FadeOut(pushNote, 0.2));

        stackCode.highlightLine(20);
        TextMob popNote = new TextMob("pop: return value at top, then decrement top")
                .setFontSize(20).setFillColor(Colors.TEAL);
        popNote.setPosition(0, 380);
        play(new FadeIn(popNote, 0.3));
        hold(1.5);
        stackCode.clearHighlights();

        play(new FadeOut(stackCode, 0.3), new FadeOut(implHeading, 0.3), new FadeOut(popNote, 0.3));

        // ================================================================
        // SECTION 5: Stack Overflow and Underflow
        // ================================================================
        TextMob overHeading = new TextMob("Stack Overflow & Underflow")
                .setFontSize(44).setBold().setFillColor(Colors.RED);
        overHeading.setPosition(0, -420);
        play(new Write(overHeading, 0.8));

        // Overflow: full stack
        StackMob fullStack = new StackMob("A", "B", "C", "D", "E");
        fullStack.setLabel("Full Stack (capacity=5)");
        fullStack.setPosition(-350, -50);
        add(fullStack);

        TextMob overDesc = new TextMob("OVERFLOW: Pushing to a full stack!")
                .setFontSize(26).setFillColor(Colors.RED);
        overDesc.setPosition(-350, 250);
        play(new FadeIn(overDesc, 0.5));

        TextMob overExplain = new TextMob("top == MAX-1, no room for new element")
                .setFontSize(20).setFillColor(Colors.ORANGE);
        overExplain.setPosition(-350, 300);
        play(new FadeIn(overExplain, 0.4));

        // Underflow: empty stack
        StackMob emptyStack = new StackMob();
        emptyStack.setLabel("Empty Stack");
        emptyStack.setPosition(350, -50);
        add(emptyStack);

        TextMob underDesc = new TextMob("UNDERFLOW: Popping from an empty stack!")
                .setFontSize(26).setFillColor(Colors.RED);
        underDesc.setPosition(350, 250);
        play(new FadeIn(underDesc, 0.5));

        TextMob underExplain = new TextMob("top == -1, nothing to remove")
                .setFontSize(20).setFillColor(Colors.ORANGE);
        underExplain.setPosition(350, 300);
        play(new FadeIn(underExplain, 0.4));

        TextMob alwaysCheck = new TextMob("Always check isFull() before push and isEmpty() before pop!")
                .setFontSize(24).setFillColor(Colors.GOLD);
        alwaysCheck.setPosition(0, 380);
        play(new Write(alwaysCheck, 1.0));
        hold(2.5);

        play(new FadeOut(fullStack, 0.3), new FadeOut(emptyStack, 0.3),
             new FadeOut(overHeading, 0.3), new FadeOut(overDesc, 0.3),
             new FadeOut(underDesc, 0.3), new FadeOut(overExplain, 0.3),
             new FadeOut(underExplain, 0.3), new FadeOut(alwaysCheck, 0.3));

        // ================================================================
        // SECTION 6: Application — Balanced Parentheses
        // ================================================================
        TextMob parenHeading = new TextMob("Application: Balanced Parentheses")
                .setFontSize(44).setBold().setFillColor(Colors.PURPLE);
        parenHeading.setPosition(0, -420);
        play(new Write(parenHeading, 0.8));

        TextMob parenAlgo = new TextMob("Push opening brackets, pop and match for closing brackets")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        parenAlgo.setPosition(0, -360);
        play(new Write(parenAlgo, 1.0));

        // Input string: (({[]}))
        TextMob inputStr = new TextMob("Input: ( ( { [ ] } ) )")
                .setFontSize(32).setFillColor(Colors.WHITE).setFontFamily("Monospace");
        inputStr.setPosition(0, -280);
        play(new FadeIn(inputStr, 0.5));

        StackMob parenStack = new StackMob();
        parenStack.setLabel("Stack");
        parenStack.setPosition(-350, 20);
        add(parenStack);

        TextMob parenStep = new TextMob("")
                .setFontSize(24).setFillColor(Colors.YELLOW);
        parenStep.setPosition(200, -100);
        add(parenStep);

        TextMob parenResult = new TextMob("")
                .setFontSize(24).setFillColor(Colors.GREEN);
        parenResult.setPosition(200, -40);
        add(parenResult);

        // Step through (({[]}))
        // char 0: ( -> push
        parenStep.setText("Read '(' -> opening bracket, push");
        parenStack.push("(");
        parenStack.setHighlightTop(1);
        hold(0.8);
        parenStack.setHighlightTop(0);

        // char 1: ( -> push
        parenStep.setText("Read '(' -> opening bracket, push");
        parenStack.push("(");
        parenStack.setHighlightTop(1);
        hold(0.8);
        parenStack.setHighlightTop(0);

        // char 2: { -> push
        parenStep.setText("Read '{' -> opening bracket, push");
        parenStack.push("{");
        parenStack.setHighlightTop(1);
        hold(0.8);
        parenStack.setHighlightTop(0);

        // char 3: [ -> push
        parenStep.setText("Read '[' -> opening bracket, push");
        parenStack.push("[");
        parenStack.setHighlightTop(1);
        hold(0.8);
        parenStack.setHighlightTop(0);

        // char 4: ] -> pop and check
        parenStep.setText("Read ']' -> closing bracket, pop top");
        parenStack.setHighlightTop(1);
        hold(0.5);
        parenResult.setText("Pop '[' matches ']' -> OK");
        parenStack.pop();
        parenStack.setHighlightTop(0);
        hold(0.8);

        // char 5: } -> pop and check
        parenStep.setText("Read '}' -> closing bracket, pop top");
        parenStack.setHighlightTop(1);
        hold(0.5);
        parenResult.setText("Pop '{' matches '}' -> OK");
        parenStack.pop();
        parenStack.setHighlightTop(0);
        hold(0.8);

        // char 6: ) -> pop and check
        parenStep.setText("Read ')' -> closing bracket, pop top");
        parenStack.setHighlightTop(1);
        hold(0.5);
        parenResult.setText("Pop '(' matches ')' -> OK");
        parenStack.pop();
        parenStack.setHighlightTop(0);
        hold(0.8);

        // char 7: ) -> pop and check
        parenStep.setText("Read ')' -> closing bracket, pop top");
        parenStack.setHighlightTop(1);
        hold(0.5);
        parenResult.setText("Pop '(' matches ')' -> OK");
        parenStack.pop();
        parenStack.setHighlightTop(0);
        hold(0.8);

        // Final check
        parenStep.setText("End of input. Stack is empty.");
        parenResult.setText("BALANCED! All brackets matched correctly.");
        parenResult.setFillColor(Colors.GREEN);
        hold(2.5);

        play(new FadeOut(parenStack, 0.3), new FadeOut(parenHeading, 0.3),
             new FadeOut(parenAlgo, 0.3), new FadeOut(inputStr, 0.3),
             new FadeOut(parenStep, 0.3), new FadeOut(parenResult, 0.3));

        // ================================================================
        // SECTION 7: Application — Postfix Expression Evaluation
        // ================================================================
        TextMob postHeading = new TextMob("Application: Postfix Evaluation")
                .setFontSize(44).setBold().setFillColor(Colors.PURPLE);
        postHeading.setPosition(0, -420);
        play(new Write(postHeading, 0.8));

        TextMob postExplain = new TextMob("Postfix (Reverse Polish): operators come AFTER operands")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        postExplain.setPosition(0, -360);
        play(new Write(postExplain, 1.0));

        TextMob postExpr = new TextMob("Expression:  2  3  *  5  +    (= (2*3)+5 = 11)")
                .setFontSize(28).setFillColor(Colors.WHITE).setFontFamily("Monospace");
        postExpr.setPosition(0, -290);
        play(new FadeIn(postExpr, 0.5));

        StackMob postStack = new StackMob();
        postStack.setLabel("Stack");
        postStack.setPosition(-350, 20);
        add(postStack);

        TextMob postStep = new TextMob("")
                .setFontSize(24).setFillColor(Colors.YELLOW);
        postStep.setPosition(200, -100);
        add(postStep);

        TextMob postCalc = new TextMob("")
                .setFontSize(24).setFillColor(Colors.GREEN);
        postCalc.setPosition(200, -30);
        add(postCalc);

        // Token: 2
        postStep.setText("Read '2' -> operand, push 2");
        postStack.push("2");
        postStack.setHighlightTop(1);
        hold(1.0);
        postStack.setHighlightTop(0);

        // Token: 3
        postStep.setText("Read '3' -> operand, push 3");
        postStack.push("3");
        postStack.setHighlightTop(1);
        hold(1.0);
        postStack.setHighlightTop(0);

        // Token: *
        postStep.setText("Read '*' -> operator, pop 3 and 2");
        postStack.setHighlightTop(2);
        hold(0.6);
        postStack.pop(); // 3
        postStack.pop(); // 2
        postCalc.setText("Compute: 2 * 3 = 6, push 6");
        postStack.push("6");
        postStack.setHighlightTop(1);
        hold(1.2);
        postStack.setHighlightTop(0);

        // Token: 5
        postStep.setText("Read '5' -> operand, push 5");
        postStack.push("5");
        postStack.setHighlightTop(1);
        hold(1.0);
        postStack.setHighlightTop(0);

        // Token: +
        postStep.setText("Read '+' -> operator, pop 5 and 6");
        postStack.setHighlightTop(2);
        hold(0.6);
        postStack.pop(); // 5
        postStack.pop(); // 6
        postCalc.setText("Compute: 6 + 5 = 11, push 11");
        postStack.push("11");
        postStack.setHighlightTop(1);
        hold(1.2);
        postStack.setHighlightTop(0);

        // Result
        postStep.setText("End of expression. Pop result.");
        postCalc.setText("Result = 11");
        postCalc.setFillColor(Colors.GOLD);
        hold(2.5);

        play(new FadeOut(postStack, 0.3), new FadeOut(postHeading, 0.3),
             new FadeOut(postExplain, 0.3), new FadeOut(postExpr, 0.3),
             new FadeOut(postStep, 0.3), new FadeOut(postCalc, 0.3));

        // ================================================================
        // SECTION 8: Application — Function Call Stack
        // ================================================================
        TextMob callHeading = new TextMob("Application: Function Call Stack")
                .setFontSize(44).setBold().setFillColor(Colors.PURPLE);
        callHeading.setPosition(0, -420);
        play(new Write(callHeading, 0.8));

        TextMob callDesc = new TextMob("Each function call pushes a frame; return pops it")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        callDesc.setPosition(0, -360);
        play(new Write(callDesc, 1.0));

        CodeBlock fibCode = new CodeBlock(
            "int fib(int n) {\n" +
            "    if (n <= 1) return n;\n" +
            "    return fib(n-1) + fib(n-2);\n" +
            "}", 20
        );
        fibCode.setPosition(-350, -220);
        play(new RevealCode(fibCode, 1.5));

        TextMob fibCall = new TextMob("Call: fib(4)")
                .setFontSize(28).setFillColor(Colors.GOLD);
        fibCall.setPosition(-350, -100);
        play(new FadeIn(fibCall, 0.4));

        StackMob callStack = new StackMob();
        callStack.setLabel("Call Stack");
        callStack.setPosition(300, 0);
        add(callStack);

        TextMob callNote = new TextMob("")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        callNote.setPosition(300, 280);
        add(callNote);

        // Simulate fib(4) call stack (simplified key calls)
        callStack.push("fib(4)");
        callNote.setText("Push fib(4)");
        hold(0.8);

        callStack.push("fib(3)");
        callNote.setText("fib(4) calls fib(3)");
        hold(0.8);

        callStack.push("fib(2)");
        callNote.setText("fib(3) calls fib(2)");
        hold(0.8);

        callStack.push("fib(1)");
        callNote.setText("fib(2) calls fib(1)");
        hold(0.6);
        callNote.setText("fib(1) returns 1");
        callNote.setFillColor(Colors.GREEN);
        callStack.setHighlightTop(1);
        hold(0.6);
        callStack.pop();
        callStack.setHighlightTop(0);

        callStack.push("fib(0)");
        callNote.setText("fib(2) calls fib(0)");
        callNote.setFillColor(Colors.YELLOW);
        hold(0.6);
        callNote.setText("fib(0) returns 0");
        callNote.setFillColor(Colors.GREEN);
        callStack.setHighlightTop(1);
        hold(0.6);
        callStack.pop();
        callStack.setHighlightTop(0);

        // fib(2) returns 1
        callNote.setText("fib(2) = 1+0 = 1, returns");
        callStack.setHighlightTop(1);
        hold(0.8);
        callStack.pop();
        callStack.setHighlightTop(0);

        // continue fib(3) calls fib(1)
        callStack.push("fib(1)");
        callNote.setText("fib(3) calls fib(1)");
        callNote.setFillColor(Colors.YELLOW);
        hold(0.6);
        callNote.setText("fib(1) returns 1");
        callNote.setFillColor(Colors.GREEN);
        callStack.setHighlightTop(1);
        hold(0.6);
        callStack.pop();
        callStack.setHighlightTop(0);

        // fib(3) returns 2
        callNote.setText("fib(3) = 1+1 = 2, returns");
        callStack.setHighlightTop(1);
        hold(0.8);
        callStack.pop();
        callStack.setHighlightTop(0);

        // fib(4) calls fib(2)
        callStack.push("fib(2)");
        callNote.setText("fib(4) calls fib(2)");
        callNote.setFillColor(Colors.YELLOW);
        hold(0.6);
        callNote.setText("fib(2) returns 1");
        callNote.setFillColor(Colors.GREEN);
        callStack.setHighlightTop(1);
        hold(0.6);
        callStack.pop();
        callStack.setHighlightTop(0);

        // fib(4) returns 3
        callNote.setText("fib(4) = 2+1 = 3, returns");
        callStack.setHighlightTop(1);
        hold(0.8);
        callStack.pop();
        callStack.setHighlightTop(0);

        callNote.setText("Result: fib(4) = 3. Stack is empty again.");
        callNote.setFillColor(Colors.GOLD);
        hold(2.0);

        TextMob stackOverflowNote = new TextMob("Too-deep recursion -> Stack Overflow! (e.g., fib(100000))")
                .setFontSize(22).setFillColor(Colors.RED);
        stackOverflowNote.setPosition(0, 400);
        play(new FadeIn(stackOverflowNote, 0.5));
        hold(2.0);

        play(new FadeOut(callStack, 0.3), new FadeOut(callHeading, 0.3),
             new FadeOut(callDesc, 0.3), new FadeOut(fibCode, 0.3),
             new FadeOut(fibCall, 0.3), new FadeOut(callNote, 0.3),
             new FadeOut(stackOverflowNote, 0.3));

        // ================================================================
        // SECTION 9: Queue ADT — FIFO Concept
        // ================================================================
        TextMob qHeading = new TextMob("Queue: First In, First Out (FIFO)")
                .setFontSize(44).setBold().setFillColor(Colors.ORANGE);
        qHeading.setPosition(0, -420);
        play(new Write(qHeading, 0.8));

        TextMob qAnalogy = new TextMob("Like a line at a ticket counter: first person in line is served first")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        qAnalogy.setPosition(0, -360);
        play(new Write(qAnalogy, 1.2));

        TextMob qOps = new TextMob("Operations: enqueue(item) — add to rear  |  dequeue() — remove from front")
                .setFontSize(22).setFillColor(Colors.TEAL);
        qOps.setPosition(0, -300);
        play(new Write(qOps, 1.2));
        hold(1.0);

        // Visualize queue as horizontal array
        ArrayMob qArr = new ArrayMob("_", "_", "_", "_", "_");
        qArr.setLabel("Queue (capacity=5)");
        qArr.setPosition(0, -180);
        play(new FadeIn(qArr, 0.7));

        TextMob qFR = new TextMob("front = -1, rear = -1")
                .setFontSize(22).setFillColor(Colors.ORANGE);
        qFR.setPosition(0, -100);
        add(qFR);

        TextMob qAction = new TextMob("")
                .setFontSize(24).setFillColor(Colors.GREEN);
        qAction.setPosition(0, -30);
        add(qAction);

        // Enqueue operations
        String[] enqVals = {"A", "B", "C", "D"};
        int front = 0, rear = -1;
        for (String val : enqVals) {
            rear++;
            qAction.setText("enqueue('" + val + "')");
            qAction.setFillColor(Colors.GREEN);
            qArr.setValue(rear, val);
            qArr.setCellColor(rear, Colors.withAlpha(Colors.GREEN, 0.2));
            qFR.setText("front = " + front + ", rear = " + rear);
            hold(0.8);
        }
        hold(0.5);

        // Dequeue operations
        for (int i = 0; i < 2; i++) {
            String val = enqVals[front];
            qAction.setText("dequeue() -> '" + val + "'");
            qAction.setFillColor(Colors.RED);
            qArr.highlight(front);
            hold(0.5);
            qArr.setValue(front, "_");
            qArr.setCellColor(front, Colors.withAlpha(Colors.DARK_GRAY, 0.2));
            qArr.unhighlight(front);
            front++;
            qFR.setText("front = " + front + ", rear = " + rear);
            hold(0.6);
        }

        TextMob qProblem = new TextMob("Problem: front moves forward, wasting space at the beginning!")
                .setFontSize(22).setFillColor(Colors.RED);
        qProblem.setPosition(0, 50);
        play(new Write(qProblem, 1.0));
        hold(2.0);

        play(new FadeOut(qArr, 0.3), new FadeOut(qHeading, 0.3), new FadeOut(qAnalogy, 0.3),
             new FadeOut(qOps, 0.3), new FadeOut(qFR, 0.3), new FadeOut(qAction, 0.3),
             new FadeOut(qProblem, 0.3));

        // ================================================================
        // SECTION 10: Circular Queue
        // ================================================================
        TextMob cqHeading = new TextMob("Circular Queue")
                .setFontSize(44).setBold().setFillColor(Colors.ORANGE);
        cqHeading.setPosition(0, -420);
        play(new Write(cqHeading, 0.8));

        TextMob cqDesc = new TextMob("Solution: wrap around using modular arithmetic!")
                .setFontSize(24).setFillColor(Colors.GREEN);
        cqDesc.setPosition(0, -360);
        play(new Write(cqDesc, 1.0));

        TextMob cqFormula = new TextMob("rear = (rear + 1) % capacity    |    front = (front + 1) % capacity")
                .setFontSize(22).setFillColor(Colors.GOLD).setFontFamily("Monospace");
        cqFormula.setPosition(0, -300);
        play(new FadeIn(cqFormula, 0.5));

        // Visualize circular queue
        ArrayMob cqArr = new ArrayMob("_", "_", "_", "_", "_");
        cqArr.setLabel("Circular Queue (cap=5)");
        cqArr.setPosition(0, -180);
        play(new FadeIn(cqArr, 0.7));

        TextMob cqFR = new TextMob("")
                .setFontSize(22).setFillColor(Colors.ORANGE);
        cqFR.setPosition(0, -100);
        add(cqFR);

        TextMob cqStep = new TextMob("")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        cqStep.setPosition(0, -40);
        add(cqStep);

        // Enqueue A,B,C,D,E -> full
        int cqFront = 0, cqRear = -1, cqCount = 0, cqCap = 5;
        String[] cqEnq = {"A", "B", "C", "D", "E"};
        for (String v : cqEnq) {
            cqRear = (cqRear + 1) % cqCap;
            cqArr.setValue(cqRear, v);
            cqArr.setCellColor(cqRear, Colors.withAlpha(Colors.TEAL, 0.2));
            cqCount++;
            cqFR.setText("front=" + cqFront + " rear=" + cqRear + " count=" + cqCount);
            cqStep.setText("enqueue('" + v + "') at index " + cqRear);
            hold(0.6);
        }

        cqStep.setText("Queue is FULL (count == capacity)");
        cqStep.setFillColor(Colors.RED);
        hold(1.0);

        // Dequeue 2 items
        for (int i = 0; i < 2; i++) {
            String v = cqEnq[cqFront];
            cqArr.setValue(cqFront, "_");
            cqArr.setCellColor(cqFront, Colors.withAlpha(Colors.DARK_GRAY, 0.15));
            cqFront = (cqFront + 1) % cqCap;
            cqCount--;
            cqFR.setText("front=" + cqFront + " rear=" + cqRear + " count=" + cqCount);
            cqStep.setText("dequeue() -> '" + v + "', front moves to " + cqFront);
            cqStep.setFillColor(Colors.YELLOW);
            hold(0.8);
        }

        // Enqueue F -> wraps around to index 0!
        cqRear = (cqRear + 1) % cqCap; // wraps to 0
        cqArr.setValue(cqRear, "F");
        cqArr.setCellColor(cqRear, Colors.withAlpha(Colors.GREEN, 0.3));
        cqCount++;
        cqFR.setText("front=" + cqFront + " rear=" + cqRear + " count=" + cqCount);
        cqStep.setText("enqueue('F') at index " + cqRear + " -> WRAPPED AROUND!");
        cqStep.setFillColor(Colors.GREEN);
        hold(2.0);

        play(new FadeOut(cqArr, 0.3), new FadeOut(cqHeading, 0.3), new FadeOut(cqDesc, 0.3),
             new FadeOut(cqFormula, 0.3), new FadeOut(cqFR, 0.3), new FadeOut(cqStep, 0.3));

        // ================================================================
        // SECTION 11: Queue Implementation Code
        // ================================================================
        TextMob qCodeHeading = new TextMob("Queue Implementation in C")
                .setFontSize(44).setBold().setFillColor(Colors.ORANGE);
        qCodeHeading.setPosition(0, -420);
        play(new Write(qCodeHeading, 0.8));

        CodeBlock qImplCode = new CodeBlock(
            "#define MAX 100\n" +
            "int queue[MAX];\n" +
            "int front = 0, rear = -1, count = 0;\n" +
            "\n" +
            "void enqueue(int val) {\n" +
            "    if (count == MAX) { printf(\"Full!\\n\"); return; }\n" +
            "    rear = (rear + 1) % MAX;\n" +
            "    queue[rear] = val;\n" +
            "    count++;\n" +
            "}\n" +
            "\n" +
            "int dequeue() {\n" +
            "    if (count == 0) { printf(\"Empty!\\n\"); return -1; }\n" +
            "    int val = queue[front];\n" +
            "    front = (front + 1) % MAX;\n" +
            "    count--;\n" +
            "    return val;\n" +
            "}", 17
        );
        qImplCode.setPosition(0, -20);
        play(new RevealCode(qImplCode, 4.0));

        qImplCode.highlightLine(6);
        TextMob wrapNote = new TextMob("Key: modular arithmetic (% MAX) enables wrap-around")
                .setFontSize(22).setFillColor(Colors.GOLD);
        wrapNote.setPosition(0, 350);
        play(new FadeIn(wrapNote, 0.5));
        hold(2.0);

        qImplCode.clearHighlights();
        qImplCode.highlightLine(14);
        hold(1.5);

        play(new FadeOut(qImplCode, 0.3), new FadeOut(qCodeHeading, 0.3), new FadeOut(wrapNote, 0.3));

        // ================================================================
        // SECTION 12: BFS Uses Queue (Brief)
        // ================================================================
        TextMob bfsHeading = new TextMob("Queue Application: BFS")
                .setFontSize(44).setBold().setFillColor(Colors.ORANGE);
        bfsHeading.setPosition(0, -420);
        play(new Write(bfsHeading, 0.8));

        TextMob bfsDesc = new TextMob("Breadth-First Search explores a graph level by level using a queue")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        bfsDesc.setPosition(0, -360);
        play(new Write(bfsDesc, 1.2));

        // Show tree with BFS order
        TreeMob bfsTree = new TreeMob("1", "2", "3", "4", "5", "6", "7");
        bfsTree.setPosition(0, -100);
        play(new FadeIn(bfsTree, 1.0));

        TextMob bfsOrder = new TextMob("BFS visit order: 1, 2, 3, 4, 5, 6, 7 (level by level)")
                .setFontSize(24).setFillColor(Colors.GOLD);
        bfsOrder.setPosition(0, 150);
        play(new FadeIn(bfsOrder, 0.5));

        // Animate BFS
        int[] bfsNodes = {0, 1, 2, 3, 4, 5, 6};
        for (int nodeIdx : bfsNodes) {
            bfsTree.highlightNode(nodeIdx);
            hold(0.6);
            bfsTree.setNodeColor(nodeIdx, Colors.withAlpha(Colors.GOLD, 0.4));
            bfsTree.clearHighlight();
        }
        hold(2.0);

        play(new FadeOut(bfsTree, 0.3), new FadeOut(bfsHeading, 0.3),
             new FadeOut(bfsDesc, 0.3), new FadeOut(bfsOrder, 0.3));

        // ================================================================
        // SECTION 13: Stack vs Queue Comparison
        // ================================================================
        TextMob vsHeading = new TextMob("Stack vs Queue")
                .setFontSize(48).setBold().setFillColor(Colors.BLUE);
        vsHeading.setPosition(0, -380);
        play(new Write(vsHeading, 0.8));

        String[][] vsRows = {
            {"Feature",          "Stack",               "Queue"},
            {"Order",            "LIFO",                "FIFO"},
            {"Insert",           "push (top)",          "enqueue (rear)"},
            {"Remove",           "pop (top)",           "dequeue (front)"},
            {"Peek",             "top element",         "front element"},
            {"Use case",         "Undo, DFS, parsing",  "BFS, scheduling"},
            {"Complexity",       "O(1) all ops",        "O(1) all ops"},
        };

        double vsY = -280;
        TextMob[] vsMobs = new TextMob[vsRows.length];
        for (int i = 0; i < vsRows.length; i++) {
            String line = String.format("%-18s %-22s %-22s",
                    vsRows[i][0], vsRows[i][1], vsRows[i][2]);
            vsMobs[i] = new TextMob(line)
                    .setFontSize(i == 0 ? 22 : 20)
                    .setFillColor(i == 0 ? Colors.GOLD : Colors.WHITE)
                    .setFontFamily("Monospace");
            vsMobs[i].setPosition(0, vsY + i * 45);
            play(new Write(vsMobs[i], 0.5));
            hold(0.2);
        }
        hold(3.0);

        play(new FadeOut(vsHeading, 0.3));
        for (TextMob m : vsMobs) play(new FadeOut(m, 0.1));

        // ================================================================
        // SECTION 14: Summary
        // ================================================================
        TextMob sumTitle = new TextMob("Summary: Stacks & Queues")
                .setFontSize(52).setBold().setFillColor(Colors.PURPLE);
        sumTitle.setPosition(0, -380);
        play(new FadeIn(sumTitle, 0.6));

        String[] sumPoints = {
            "Stack = LIFO: push/pop at top, O(1) operations",
            "Array-based stack uses top pointer, check overflow/underflow",
            "Stack apps: parenthesis matching, postfix eval, call stack",
            "Queue = FIFO: enqueue at rear, dequeue at front",
            "Circular queue uses modular arithmetic to wrap around",
            "Queue apps: BFS, scheduling, buffering",
            "Both are fundamental ADTs used throughout CS"
        };

        TextMob[] sumMobs = new TextMob[sumPoints.length];
        for (int i = 0; i < sumPoints.length; i++) {
            sumMobs[i] = new TextMob("  " + (i + 1) + ". " + sumPoints[i])
                    .setFontSize(22).setFillColor(Colors.WHITE);
            sumMobs[i].setPosition(0, -270 + i * 55);
            play(new Write(sumMobs[i], 0.8));
            hold(0.3);
        }
        hold(3.0);

        play(new FadeOut(sumTitle, 1.0));
        for (TextMob m : sumMobs) play(new FadeOut(m, 0.5));
        hold(1.0);
    }
}
