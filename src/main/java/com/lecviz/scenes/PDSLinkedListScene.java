package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Scene;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Vec2;

/**
 * PDS Lecture: Linked Lists — comprehensive coverage.
 * Covers node structure, creation, traversal, insertion (beginning/end/middle),
 * deletion, reversal, doubly linked list, circular linked list, and comparison.
 */
public class PDSLinkedListScene extends Scene {

    @Override
    public void construct() {

        // ================================================================
        // SECTION 1: Title Card
        // ================================================================
        TextMob title = new TextMob("Linked Lists")
                .setFontSize(72).setBold().setFillColor(Colors.TEAL);
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
        // SECTION 2: Array Limitations
        // ================================================================
        TextMob limHeading = new TextMob("Why Do We Need Linked Lists?")
                .setFontSize(44).setBold().setFillColor(Colors.TEAL);
        limHeading.setPosition(0, -420);
        play(new Write(limHeading, 0.8));

        TextMob limTitle = new TextMob("Limitations of Arrays:")
                .setFontSize(28).setFillColor(Colors.RED);
        limTitle.setPosition(0, -350);
        play(new FadeIn(limTitle, 0.5));

        // Show array with fixed size
        ArrayMob fixedArr = new ArrayMob("5", "12", "7", "9", "3");
        fixedArr.setLabel("int arr[5] — fixed size at compile time");
        fixedArr.setPosition(0, -220);
        play(new FadeIn(fixedArr, 0.7));

        String[] limitations = {
            "1. Fixed size — must know size at compile time (or waste memory)",
            "2. Insertion in middle: O(n) — must shift ALL subsequent elements",
            "3. Deletion in middle: O(n) — must shift elements to fill gap",
            "4. Cannot grow dynamically (realloc is expensive)"
        };

        TextMob[] limMobs = new TextMob[limitations.length];
        for (int i = 0; i < limitations.length; i++) {
            limMobs[i] = new TextMob(limitations[i])
                    .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
            limMobs[i].setPosition(0, -100 + i * 50);
            play(new Write(limMobs[i], 0.8));
            hold(0.3);
        }

        // Animate insertion shifting
        TextMob shiftNote = new TextMob("Inserting 99 at index 2 requires shifting 3 elements!")
                .setFontSize(22).setFillColor(Colors.ORANGE);
        shiftNote.setPosition(0, 120);
        play(new FadeIn(shiftNote, 0.5));
        fixedArr.highlight(2);
        fixedArr.highlight(3);
        fixedArr.highlight(4);
        hold(2.0);

        play(new FadeOut(fixedArr, 0.3), new FadeOut(limHeading, 0.3),
             new FadeOut(limTitle, 0.3), new FadeOut(shiftNote, 0.3));
        for (TextMob m : limMobs) play(new FadeOut(m, 0.1));

        // ================================================================
        // SECTION 3: Node Structure
        // ================================================================
        TextMob nodeHeading = new TextMob("The Node Structure")
                .setFontSize(44).setBold().setFillColor(Colors.TEAL);
        nodeHeading.setPosition(0, -420);
        play(new Write(nodeHeading, 0.8));

        // Visual representation of a single node
        RectMob nodeBox = new RectMob(200, 80);
        nodeBox.setFillColor(Colors.withAlpha(Colors.TEAL, 0.15));
        nodeBox.setStrokeColor(Colors.TEAL);
        nodeBox.setPosition(-100, -250);

        TextMob dataLabel = new TextMob("data")
                .setFontSize(22).setFillColor(Colors.WHITE);
        dataLabel.setPosition(-150, -250);

        TextMob nextLabel = new TextMob("next")
                .setFontSize(22).setFillColor(Colors.ORANGE);
        nextLabel.setPosition(-50, -250);

        play(new FadeIn(nodeBox, 0.5));
        play(new FadeIn(dataLabel, 0.3), new FadeIn(nextLabel, 0.3));

        TextMob nodeDesc = new TextMob("Each node contains: data + pointer to next node")
                .setFontSize(24).setFillColor(Colors.YELLOW);
        nodeDesc.setPosition(0, -160);
        play(new Write(nodeDesc, 1.0));

        CodeBlock structCode = new CodeBlock(
            "struct Node {\n" +
            "    int data;           // the value\n" +
            "    struct Node* next;  // pointer to next node\n" +
            "};\n" +
            "\n" +
            "// Allocate a new node\n" +
            "struct Node* newNode(int val) {\n" +
            "    struct Node* node = \n" +
            "        (struct Node*)malloc(sizeof(struct Node));\n" +
            "    node->data = val;\n" +
            "    node->next = NULL;\n" +
            "    return node;\n" +
            "}", 18
        );
        structCode.setPosition(0, 50);
        play(new RevealCode(structCode, 3.5));

        structCode.highlightLine(1);
        structCode.highlightLine(2);
        hold(1.5);
        structCode.clearHighlights();
        structCode.highlightLine(9);
        structCode.highlightLine(10);
        hold(1.5);
        structCode.clearHighlights();

        hold(1.5);
        play(new FadeOut(structCode, 0.3), new FadeOut(nodeHeading, 0.3),
             new FadeOut(nodeBox, 0.3), new FadeOut(dataLabel, 0.3),
             new FadeOut(nextLabel, 0.3), new FadeOut(nodeDesc, 0.3));

        // ================================================================
        // SECTION 4: Creating a Linked List Step by Step
        // ================================================================
        TextMob createHeading = new TextMob("Building a Linked List")
                .setFontSize(44).setBold().setFillColor(Colors.TEAL);
        createHeading.setPosition(0, -420);
        play(new Write(createHeading, 0.8));

        CodeBlock createCode = new CodeBlock(
            "struct Node* head = newNode(10);\n" +
            "head->next = newNode(25);\n" +
            "head->next->next = newNode(3);\n" +
            "head->next->next->next = newNode(42);", 20
        );
        createCode.setPosition(0, -280);
        play(new RevealCode(createCode, 2.0));

        // Build visually step by step
        LinkedListMob buildList = new LinkedListMob();
        buildList.setPosition(0, -80);
        add(buildList);

        TextMob buildStatus = new TextMob("")
                .setFontSize(22).setFillColor(Colors.GREEN);
        buildStatus.setPosition(0, 30);
        add(buildStatus);

        // Step 1: head = newNode(10)
        createCode.highlightLine(0);
        buildList.addNode("10");
        buildStatus.setText("Step 1: Create node with data=10, head points to it");
        hold(1.5);

        // Step 2: head->next = newNode(25)
        createCode.clearHighlights();
        createCode.highlightLine(1);
        buildList.addNode("25");
        buildStatus.setText("Step 2: Create node 25, link from node 10");
        hold(1.5);

        // Step 3: head->next->next = newNode(3)
        createCode.clearHighlights();
        createCode.highlightLine(2);
        buildList.addNode("3");
        buildStatus.setText("Step 3: Create node 3, link from node 25");
        hold(1.5);

        // Step 4: head->next->next->next = newNode(42)
        createCode.clearHighlights();
        createCode.highlightLine(3);
        buildList.addNode("42");
        buildStatus.setText("Step 4: Create node 42, link from node 3. Last node's next = NULL");
        hold(2.0);
        createCode.clearHighlights();

        play(new FadeOut(createCode, 0.3), new FadeOut(buildList, 0.3),
             new FadeOut(createHeading, 0.3), new FadeOut(buildStatus, 0.3));

        // ================================================================
        // SECTION 5: Traversal
        // ================================================================
        TextMob travHeading = new TextMob("Linked List Traversal")
                .setFontSize(44).setBold().setFillColor(Colors.TEAL);
        travHeading.setPosition(0, -420);
        play(new Write(travHeading, 0.8));

        CodeBlock travCode = new CodeBlock(
            "void printList(struct Node* head) {\n" +
            "    struct Node* curr = head;\n" +
            "    while (curr != NULL) {\n" +
            "        printf(\"%d -> \", curr->data);\n" +
            "        curr = curr->next;\n" +
            "    }\n" +
            "    printf(\"NULL\\n\");\n" +
            "}", 18
        );
        travCode.setPosition(0, -250);
        play(new RevealCode(travCode, 2.5));

        LinkedListMob travList = new LinkedListMob("10", "25", "3", "42");
        travList.setPosition(0, -30);
        play(new FadeIn(travList, 0.8));

        TextMob output = new TextMob("Output: ")
                .setFontSize(24).setFillColor(Colors.YELLOW).setFontFamily("Monospace");
        output.setPosition(0, 80);
        add(output);

        // Animate traversal
        String outputStr = "Output: ";
        String[] travData = {"10", "25", "3", "42"};
        for (int i = 0; i < 4; i++) {
            travList.highlightNode(i);
            travCode.clearHighlights();
            travCode.highlightLine(3);
            outputStr += travData[i] + " -> ";
            output.setText(outputStr);
            hold(0.8);
            travCode.clearHighlights();
            travCode.highlightLine(4);
            travList.unhighlightNode(i);
            hold(0.3);
        }
        outputStr += "NULL";
        output.setText(outputStr);
        travCode.clearHighlights();
        travCode.highlightLine(6);

        TextMob travComplexity = new TextMob("Traversal: O(n) — must visit each node sequentially")
                .setFontSize(22).setFillColor(Colors.TEAL);
        travComplexity.setPosition(0, 140);
        play(new FadeIn(travComplexity, 0.5));
        hold(2.0);

        play(new FadeOut(travCode, 0.3), new FadeOut(travList, 0.3),
             new FadeOut(travHeading, 0.3), new FadeOut(output, 0.3),
             new FadeOut(travComplexity, 0.3));

        // ================================================================
        // SECTION 6: Insertion at Beginning
        // ================================================================
        TextMob insBeginHeading = new TextMob("Insertion at Beginning")
                .setFontSize(44).setBold().setFillColor(Colors.TEAL);
        insBeginHeading.setPosition(0, -420);
        play(new Write(insBeginHeading, 0.8));

        CodeBlock insBeginCode = new CodeBlock(
            "void insertAtBeginning(struct Node** head, int val) {\n" +
            "    struct Node* node = newNode(val);\n" +
            "    node->next = *head;   // point to old head\n" +
            "    *head = node;          // update head\n" +
            "}", 18
        );
        insBeginCode.setPosition(0, -270);
        play(new RevealCode(insBeginCode, 2.0));

        LinkedListMob insBeginList = new LinkedListMob("10", "25", "3");
        insBeginList.setPosition(0, -60);
        play(new FadeIn(insBeginList, 0.7));

        TextMob insBeginStep = new TextMob("Insert 99 at the beginning")
                .setFontSize(24).setFillColor(Colors.GREEN);
        insBeginStep.setPosition(0, 50);
        play(new FadeIn(insBeginStep, 0.4));
        hold(1.0);

        // Step 1: create new node
        insBeginCode.highlightLine(1);
        insBeginStep.setText("Step 1: Create new node with data = 99");
        hold(1.0);

        // Step 2: new node -> old head
        insBeginCode.clearHighlights();
        insBeginCode.highlightLine(2);
        insBeginStep.setText("Step 2: new->next = head (point to old first node)");
        hold(1.0);

        // Step 3: update head
        insBeginCode.clearHighlights();
        insBeginCode.highlightLine(3);
        insBeginStep.setText("Step 3: head = new node");
        insBeginList.insertNode(0, "99");
        insBeginList.highlightNode(0);
        hold(1.5);
        insBeginList.unhighlightNode(0);

        TextMob insBeginTime = new TextMob("Time: O(1) — no traversal needed!")
                .setFontSize(22).setFillColor(Colors.GOLD);
        insBeginTime.setPosition(0, 120);
        play(new FadeIn(insBeginTime, 0.5));
        hold(2.0);

        play(new FadeOut(insBeginCode, 0.3), new FadeOut(insBeginList, 0.3),
             new FadeOut(insBeginHeading, 0.3), new FadeOut(insBeginStep, 0.3),
             new FadeOut(insBeginTime, 0.3));

        // ================================================================
        // SECTION 7: Insertion at End
        // ================================================================
        TextMob insEndHeading = new TextMob("Insertion at End")
                .setFontSize(44).setBold().setFillColor(Colors.TEAL);
        insEndHeading.setPosition(0, -420);
        play(new Write(insEndHeading, 0.8));

        CodeBlock insEndCode = new CodeBlock(
            "void insertAtEnd(struct Node** head, int val) {\n" +
            "    struct Node* node = newNode(val);\n" +
            "    if (*head == NULL) {\n" +
            "        *head = node; return;\n" +
            "    }\n" +
            "    struct Node* curr = *head;\n" +
            "    while (curr->next != NULL)\n" +
            "        curr = curr->next;  // go to last\n" +
            "    curr->next = node;       // link new node\n" +
            "}", 18
        );
        insEndCode.setPosition(0, -240);
        play(new RevealCode(insEndCode, 2.5));

        LinkedListMob insEndList = new LinkedListMob("10", "25", "3");
        insEndList.setPosition(0, -10);
        play(new FadeIn(insEndList, 0.7));

        TextMob insEndStep = new TextMob("Insert 77 at the end")
                .setFontSize(24).setFillColor(Colors.GREEN);
        insEndStep.setPosition(0, 90);
        play(new FadeIn(insEndStep, 0.4));
        hold(0.8);

        // Traverse to find last node
        insEndCode.highlightLine(6);
        insEndCode.highlightLine(7);
        for (int i = 0; i < 3; i++) {
            insEndList.highlightNode(i);
            insEndStep.setText("Traversing... curr at node " + (i == 0 ? "10" : i == 1 ? "25" : "3"));
            hold(0.6);
            insEndList.unhighlightNode(i);
        }

        // Link new node
        insEndCode.clearHighlights();
        insEndCode.highlightLine(8);
        insEndStep.setText("Found last node (3). Link new node 77.");
        insEndList.addNode("77");
        insEndList.highlightNode(3);
        hold(1.5);
        insEndList.unhighlightNode(3);

        TextMob insEndTime = new TextMob("Time: O(n) — must traverse to find the last node")
                .setFontSize(22).setFillColor(Colors.GOLD);
        insEndTime.setPosition(0, 150);
        play(new FadeIn(insEndTime, 0.5));
        hold(2.0);

        play(new FadeOut(insEndCode, 0.3), new FadeOut(insEndList, 0.3),
             new FadeOut(insEndHeading, 0.3), new FadeOut(insEndStep, 0.3),
             new FadeOut(insEndTime, 0.3));

        // ================================================================
        // SECTION 8: Insertion at Position
        // ================================================================
        TextMob insPosHeading = new TextMob("Insertion at a Given Position")
                .setFontSize(44).setBold().setFillColor(Colors.TEAL);
        insPosHeading.setPosition(0, -420);
        play(new Write(insPosHeading, 0.8));

        CodeBlock insPosCode = new CodeBlock(
            "void insertAt(struct Node* head, int pos, int val) {\n" +
            "    struct Node* node = newNode(val);\n" +
            "    struct Node* curr = head;\n" +
            "    for (int i = 0; i < pos - 1; i++)\n" +
            "        curr = curr->next;  // find predecessor\n" +
            "    node->next = curr->next; // new -> successor\n" +
            "    curr->next = node;       // pred -> new\n" +
            "}", 18
        );
        insPosCode.setPosition(0, -250);
        play(new RevealCode(insPosCode, 2.5));

        LinkedListMob insPosList = new LinkedListMob("10", "25", "3", "42");
        insPosList.setPosition(0, -20);
        play(new FadeIn(insPosList, 0.7));

        TextMob insPosStep = new TextMob("Insert 99 at position 2")
                .setFontSize(24).setFillColor(Colors.GREEN);
        insPosStep.setPosition(0, 80);
        play(new FadeIn(insPosStep, 0.4));
        hold(0.8);

        // Find predecessor (index 1)
        insPosList.highlightNode(0);
        insPosStep.setText("Traverse to predecessor at position 1...");
        hold(0.8);
        insPosList.unhighlightNode(0);
        insPosList.highlightNode(1);
        insPosStep.setText("Found predecessor: node 25");
        hold(1.0);

        // Rewire
        insPosCode.highlightLine(5);
        insPosStep.setText("new->next = curr->next (99 -> 3)");
        hold(1.0);
        insPosCode.clearHighlights();
        insPosCode.highlightLine(6);
        insPosStep.setText("curr->next = new (25 -> 99)");
        insPosList.insertNode(2, "99");
        insPosList.highlightNode(2);
        hold(1.5);
        insPosList.unhighlightNode(1);
        insPosList.unhighlightNode(2);

        TextMob insPosTime = new TextMob("Time: O(n) to find position + O(1) to rewire")
                .setFontSize(22).setFillColor(Colors.GOLD);
        insPosTime.setPosition(0, 150);
        play(new FadeIn(insPosTime, 0.5));
        hold(2.0);

        play(new FadeOut(insPosCode, 0.3), new FadeOut(insPosList, 0.3),
             new FadeOut(insPosHeading, 0.3), new FadeOut(insPosStep, 0.3),
             new FadeOut(insPosTime, 0.3));

        // ================================================================
        // SECTION 9: Deletion
        // ================================================================
        TextMob delHeading = new TextMob("Deleting a Node")
                .setFontSize(44).setBold().setFillColor(Colors.TEAL);
        delHeading.setPosition(0, -420);
        play(new Write(delHeading, 0.8));

        CodeBlock delCode = new CodeBlock(
            "void deleteNode(struct Node** head, int key) {\n" +
            "    struct Node* curr = *head;\n" +
            "    struct Node* prev = NULL;\n" +
            "    // If head holds the key\n" +
            "    if (curr != NULL && curr->data == key) {\n" +
            "        *head = curr->next;\n" +
            "        free(curr); return;\n" +
            "    }\n" +
            "    while (curr != NULL && curr->data != key) {\n" +
            "        prev = curr;\n" +
            "        curr = curr->next;\n" +
            "    }\n" +
            "    if (curr == NULL) return; // not found\n" +
            "    prev->next = curr->next;  // bypass node\n" +
            "    free(curr);               // free memory\n" +
            "}", 16
        );
        delCode.setPosition(-350, -200);
        play(new RevealCode(delCode, 3.5));

        LinkedListMob delList = new LinkedListMob("10", "25", "3", "42");
        delList.setPosition(350, -100);
        play(new FadeIn(delList, 0.7));

        TextMob delStep = new TextMob("Delete node with value 25")
                .setFontSize(24).setFillColor(Colors.RED);
        delStep.setPosition(350, 10);
        play(new FadeIn(delStep, 0.4));
        hold(0.8);

        // Find node 25
        delList.highlightNode(0);
        delStep.setText("curr = head (10 != 25), move on");
        hold(0.8);
        delList.unhighlightNode(0);
        delList.highlightNode(1);
        delStep.setText("curr = 25, FOUND! prev = node 10");
        hold(1.0);

        // Rewire and delete
        delCode.highlightLine(13);
        delStep.setText("prev->next = curr->next (10 -> 3)");
        hold(1.0);
        delCode.clearHighlights();
        delCode.highlightLine(14);
        delStep.setText("free(curr) — release node 25's memory");
        delList.removeNode(1);
        hold(1.5);

        TextMob delNote = new TextMob("Always free() deleted nodes to avoid memory leaks!")
                .setFontSize(22).setFillColor(Colors.ORANGE);
        delNote.setPosition(350, 80);
        play(new FadeIn(delNote, 0.5));
        hold(2.0);

        play(new FadeOut(delCode, 0.3), new FadeOut(delList, 0.3),
             new FadeOut(delHeading, 0.3), new FadeOut(delStep, 0.3),
             new FadeOut(delNote, 0.3));

        // ================================================================
        // SECTION 10: Reversing a Linked List
        // ================================================================
        TextMob revHeading = new TextMob("Reversing a Linked List")
                .setFontSize(44).setBold().setFillColor(Colors.TEAL);
        revHeading.setPosition(0, -420);
        play(new Write(revHeading, 0.8));

        TextMob revAlgo = new TextMob("Three-pointer technique: prev, curr, next")
                .setFontSize(24).setFillColor(Colors.GOLD);
        revAlgo.setPosition(0, -360);
        play(new Write(revAlgo, 1.0));

        CodeBlock revCode = new CodeBlock(
            "struct Node* reverse(struct Node* head) {\n" +
            "    struct Node* prev = NULL;\n" +
            "    struct Node* curr = head;\n" +
            "    struct Node* next = NULL;\n" +
            "    while (curr != NULL) {\n" +
            "        next = curr->next;  // save next\n" +
            "        curr->next = prev;  // reverse link\n" +
            "        prev = curr;        // advance prev\n" +
            "        curr = next;        // advance curr\n" +
            "    }\n" +
            "    return prev; // new head\n" +
            "}", 17
        );
        revCode.setPosition(-350, -200);
        play(new RevealCode(revCode, 3.0));

        // Show reversal visually
        LinkedListMob revList = new LinkedListMob("A", "B", "C", "D");
        revList.setPosition(350, -200);
        play(new FadeIn(revList, 0.7));

        TextMob revStep = new TextMob("prev=NULL, curr=A")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        revStep.setPosition(350, -80);
        add(revStep);

        // Simulate reversal with status updates
        String[] revSteps = {
            "next=B, A->next=NULL, prev=A, curr=B",
            "next=C, B->next=A, prev=B, curr=C",
            "next=D, C->next=B, prev=C, curr=D",
            "next=NULL, D->next=C, prev=D, curr=NULL"
        };
        String[] revNodeNames = {"A", "B", "C", "D"};

        for (int i = 0; i < 4; i++) {
            revList.highlightNode(i);
            revCode.clearHighlights();
            revCode.highlightLine(5);
            revCode.highlightLine(6);
            revCode.highlightLine(7);
            revCode.highlightLine(8);
            revStep.setText(revSteps[i]);
            hold(1.5);
            revList.unhighlightNode(i);
        }

        // Show reversed list
        revStep.setText("Done! New head = D, list is now D -> C -> B -> A -> NULL");
        revStep.setFillColor(Colors.GREEN);
        play(new FadeOut(revList, 0.3));

        LinkedListMob reversedList = new LinkedListMob("D", "C", "B", "A");
        reversedList.setPosition(350, -200);
        play(new FadeIn(reversedList, 0.8));

        TextMob revTime = new TextMob("Time: O(n), Space: O(1)")
                .setFontSize(22).setFillColor(Colors.TEAL);
        revTime.setPosition(350, 0);
        play(new FadeIn(revTime, 0.5));
        hold(2.5);

        play(new FadeOut(revCode, 0.3), new FadeOut(reversedList, 0.3),
             new FadeOut(revHeading, 0.3), new FadeOut(revAlgo, 0.3),
             new FadeOut(revStep, 0.3), new FadeOut(revTime, 0.3));

        // ================================================================
        // SECTION 11: Doubly Linked List
        // ================================================================
        TextMob dllHeading = new TextMob("Doubly Linked List")
                .setFontSize(44).setBold().setFillColor(Colors.TEAL);
        dllHeading.setPosition(0, -420);
        play(new Write(dllHeading, 0.8));

        TextMob dllDesc = new TextMob("Each node has TWO pointers: prev and next")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        dllDesc.setPosition(0, -360);
        play(new Write(dllDesc, 1.0));

        CodeBlock dllCode = new CodeBlock(
            "struct DNode {\n" +
            "    int data;\n" +
            "    struct DNode* prev;  // backward link\n" +
            "    struct DNode* next;  // forward link\n" +
            "};\n" +
            "\n" +
            "void insertFront(struct DNode** head, int val) {\n" +
            "    struct DNode* node = createDNode(val);\n" +
            "    node->next = *head;\n" +
            "    if (*head != NULL)\n" +
            "        (*head)->prev = node;\n" +
            "    *head = node;\n" +
            "}", 17
        );
        dllCode.setPosition(-300, -200);
        play(new RevealCode(dllCode, 3.0));

        // Visual: show double arrows between nodes
        TextMob dllVisual = new TextMob("NULL <-> [10] <-> [25] <-> [3] <-> NULL")
                .setFontSize(28).setFillColor(Colors.ORANGE).setFontFamily("Monospace");
        dllVisual.setPosition(300, -100);
        play(new FadeIn(dllVisual, 0.8));

        TextMob dllAdv = new TextMob("Advantage: can traverse in BOTH directions, O(1) deletion given node pointer")
                .setFontSize(20).setFillColor(Colors.GREEN);
        dllAdv.setPosition(0, 30);
        play(new Write(dllAdv, 1.0));

        TextMob dllDisadv = new TextMob("Disadvantage: extra memory for prev pointer, more complex code")
                .setFontSize(20).setFillColor(Colors.RED);
        dllDisadv.setPosition(0, 80);
        play(new Write(dllDisadv, 1.0));
        hold(2.5);

        play(new FadeOut(dllCode, 0.3), new FadeOut(dllVisual, 0.3),
             new FadeOut(dllHeading, 0.3), new FadeOut(dllDesc, 0.3),
             new FadeOut(dllAdv, 0.3), new FadeOut(dllDisadv, 0.3));

        // ================================================================
        // SECTION 12: Circular Linked List
        // ================================================================
        TextMob circHeading = new TextMob("Circular Linked List")
                .setFontSize(44).setBold().setFillColor(Colors.TEAL);
        circHeading.setPosition(0, -420);
        play(new Write(circHeading, 0.8));

        TextMob circDesc = new TextMob("Last node's next points back to the head — forming a circle")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        circDesc.setPosition(0, -360);
        play(new Write(circDesc, 1.0));

        // Show visual with nodes in a circle-like layout
        // Represent as: [10] -> [25] -> [3] -> [42] --+
        //                 ^                            |
        //                 +----------------------------+
        TextMob circVisual1 = new TextMob("[10] -> [25] -> [3] -> [42]")
                .setFontSize(28).setFillColor(Colors.WHITE).setFontFamily("Monospace");
        circVisual1.setPosition(0, -200);
        play(new FadeIn(circVisual1, 0.6));

        // Arrow from 42 back to 10
        ArrowMob circArrow = new ArrowMob(200, -200, -200, -200);
        circArrow.setStrokeColor(Colors.ORANGE);
        play(new DrawArrow(circArrow, 0.8));

        TextMob circLoop = new TextMob("Last node (42) -> first node (10), not NULL!")
                .setFontSize(22).setFillColor(Colors.ORANGE);
        circLoop.setPosition(0, -120);
        play(new FadeIn(circLoop, 0.5));

        TextMob circUse = new TextMob("Use cases: round-robin scheduling, circular buffers, multiplayer games")
                .setFontSize(20).setFillColor(Colors.TEAL);
        circUse.setPosition(0, -50);
        play(new Write(circUse, 1.2));

        TextMob circWarn = new TextMob("Caution: traversal must check for cycle — or it loops forever!")
                .setFontSize(22).setFillColor(Colors.RED);
        circWarn.setPosition(0, 10);
        play(new Write(circWarn, 1.0));
        hold(2.5);

        play(new FadeOut(circHeading, 0.3), new FadeOut(circDesc, 0.3),
             new FadeOut(circVisual1, 0.3), new FadeOut(circArrow, 0.3),
             new FadeOut(circLoop, 0.3), new FadeOut(circUse, 0.3),
             new FadeOut(circWarn, 0.3));

        // ================================================================
        // SECTION 13: Array vs Linked List Comparison
        // ================================================================
        TextMob compHeading = new TextMob("Array vs Linked List")
                .setFontSize(48).setBold().setFillColor(Colors.BLUE);
        compHeading.setPosition(0, -380);
        play(new Write(compHeading, 0.8));

        String[][] compRows = {
            {"Feature",            "Array",     "Linked List"},
            {"Access by index",    "O(1)",      "O(n)"},
            {"Insert at front",    "O(n)",      "O(1)"},
            {"Insert at end",      "O(1)*",     "O(n) or O(1)**"},
            {"Delete",             "O(n)",      "O(1)***"},
            {"Memory usage",       "Compact",   "Extra (pointers)"},
            {"Memory allocation",  "Static",    "Dynamic"},
            {"Cache performance",  "Excellent", "Poor"},
        };

        double cy = -280;
        TextMob[] compMobs = new TextMob[compRows.length];
        for (int i = 0; i < compRows.length; i++) {
            String line = String.format("%-20s %-14s %-18s",
                    compRows[i][0], compRows[i][1], compRows[i][2]);
            compMobs[i] = new TextMob(line)
                    .setFontSize(i == 0 ? 22 : 20)
                    .setFillColor(i == 0 ? Colors.GOLD : Colors.WHITE)
                    .setFontFamily("Monospace");
            compMobs[i].setPosition(0, cy + i * 45);
            play(new Write(compMobs[i], 0.5));
            hold(0.2);
        }

        TextMob footnotes = new TextMob("* amortized  ** with tail pointer  *** given pointer to node")
                .setFontSize(16).setFillColor(Colors.GRAY);
        footnotes.setPosition(0, cy + compRows.length * 45 + 15);
        play(new FadeIn(footnotes, 0.4));
        hold(3.0);

        play(new FadeOut(compHeading, 0.3), new FadeOut(footnotes, 0.3));
        for (TextMob m : compMobs) play(new FadeOut(m, 0.1));

        // ================================================================
        // SECTION 14: Summary
        // ================================================================
        TextMob sumTitle = new TextMob("Summary: Linked Lists")
                .setFontSize(52).setBold().setFillColor(Colors.TEAL);
        sumTitle.setPosition(0, -350);
        play(new FadeIn(sumTitle, 0.6));

        String[] sumPoints = {
            "Linked lists use dynamic memory with nodes connected by pointers",
            "Node = data + next pointer (doubly: also prev pointer)",
            "Insertion/deletion at known position: O(1) rewiring",
            "No random access — must traverse from head: O(n)",
            "Reverse with three pointers: prev, curr, next in O(n)",
            "Circular list: last node points to head",
            "Choose array for fast access, linked list for dynamic insert/delete"
        };

        TextMob[] sumMobs = new TextMob[sumPoints.length];
        for (int i = 0; i < sumPoints.length; i++) {
            sumMobs[i] = new TextMob("  " + (i + 1) + ". " + sumPoints[i])
                    .setFontSize(22).setFillColor(Colors.WHITE);
            sumMobs[i].setPosition(0, -240 + i * 55);
            play(new Write(sumMobs[i], 0.8));
            hold(0.3);
        }
        hold(3.0);

        play(new FadeOut(sumTitle, 1.0));
        for (TextMob m : sumMobs) play(new FadeOut(m, 0.5));
        hold(1.0);
    }
}
