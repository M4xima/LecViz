package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Scene;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Vec2;

/**
 * PDS Lecture: Trees and Binary Search Trees — comprehensive coverage.
 * Covers tree terminology, binary trees, traversals (in/pre/post),
 * BST property, search, insertion, deletion (3 cases), code,
 * balanced vs unbalanced, AVL concept, and applications.
 */
public class PDSTreeScene extends Scene {

    @Override
    public void construct() {

        // ================================================================
        // SECTION 1: Title Card
        // ================================================================
        TextMob title = new TextMob("Trees & Binary Search Trees")
                .setFontSize(68).setBold().setFillColor(Colors.GREEN);
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
        // SECTION 2: What is a Tree? — Terminology
        // ================================================================
        TextMob termHeading = new TextMob("Tree Terminology")
                .setFontSize(48).setBold().setFillColor(Colors.GREEN);
        termHeading.setPosition(0, -420);
        play(new Write(termHeading, 0.8));

        //       A(0)
        //      / \
        //    B(1)  C(2)
        //   / \      \
        //  D(3) E(4)  F(6)
        TreeMob termTree = new TreeMob("A", "B", "C", "D", "E", null, "F");
        termTree.setPosition(-300, -100);
        play(new FadeIn(termTree, 1.0));

        // Label terminology
        TextMob termRoot = new TextMob("Root: A (no parent)")
                .setFontSize(22).setFillColor(Colors.GOLD);
        termRoot.setPosition(300, -300);
        play(new FadeIn(termRoot, 0.4));
        termTree.highlightNode(0);
        hold(1.0);
        termTree.clearHighlight();

        TextMob termParent = new TextMob("Parent of D,E: B")
                .setFontSize(22).setFillColor(Colors.TEAL);
        termParent.setPosition(300, -250);
        play(new FadeIn(termParent, 0.4));
        termTree.highlightNode(1);
        hold(1.0);
        termTree.clearHighlight();

        TextMob termChild = new TextMob("Children of A: B, C")
                .setFontSize(22).setFillColor(Colors.ORANGE);
        termChild.setPosition(300, -200);
        play(new FadeIn(termChild, 0.4));
        termTree.highlightNode(1);
        hold(0.5);
        termTree.clearHighlight();
        termTree.highlightNode(2);
        hold(0.5);
        termTree.clearHighlight();

        TextMob termLeaf = new TextMob("Leaves: D, E, F (no children)")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        termLeaf.setPosition(300, -150);
        play(new FadeIn(termLeaf, 0.4));
        termTree.highlightNode(3);
        hold(0.4);
        termTree.clearHighlight();
        termTree.highlightNode(4);
        hold(0.4);
        termTree.clearHighlight();
        termTree.highlightNode(6);
        hold(0.4);
        termTree.clearHighlight();

        TextMob termDepth = new TextMob("Depth of E: 2 (edges from root)")
                .setFontSize(22).setFillColor(Colors.LIGHT_BLUE);
        termDepth.setPosition(300, -100);
        play(new FadeIn(termDepth, 0.4));

        TextMob termHeight = new TextMob("Height of tree: 2 (longest root-to-leaf path)")
                .setFontSize(22).setFillColor(Colors.PINK);
        termHeight.setPosition(300, -50);
        play(new FadeIn(termHeight, 0.4));

        TextMob termLevel = new TextMob("Level 0: {A}, Level 1: {B,C}, Level 2: {D,E,F}")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        termLevel.setPosition(300, 0);
        play(new FadeIn(termLevel, 0.4));
        hold(3.0);

        play(new FadeOut(termTree, 0.3), new FadeOut(termHeading, 0.3),
             new FadeOut(termRoot, 0.2), new FadeOut(termParent, 0.2),
             new FadeOut(termChild, 0.2), new FadeOut(termLeaf, 0.2),
             new FadeOut(termDepth, 0.2), new FadeOut(termHeight, 0.2),
             new FadeOut(termLevel, 0.2));

        // ================================================================
        // SECTION 3: Binary Tree
        // ================================================================
        TextMob btHeading = new TextMob("Binary Tree")
                .setFontSize(48).setBold().setFillColor(Colors.GREEN);
        btHeading.setPosition(0, -420);
        play(new Write(btHeading, 0.8));

        TextMob btDef = new TextMob("A tree where each node has AT MOST 2 children (left and right)")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        btDef.setPosition(0, -360);
        play(new Write(btDef, 1.0));

        CodeBlock btStruct = new CodeBlock(
            "struct Node {\n" +
            "    int data;\n" +
            "    struct Node* left;   // left child\n" +
            "    struct Node* right;  // right child\n" +
            "};", 20
        );
        btStruct.setPosition(-350, -230);
        play(new RevealCode(btStruct, 2.0));

        // Show example binary tree
        //       10
        //      /  \
        //    5     15
        //   / \      \
        //  3   7     20
        TreeMob btTree = new TreeMob("10", "5", "15", "3", "7", null, "20");
        btTree.setPosition(300, -150);
        play(new FadeIn(btTree, 1.0));

        TextMob btTypes = new TextMob("Types: Full (every node 0 or 2 children), Complete (all levels full except last)")
                .setFontSize(20).setFillColor(Colors.TEAL);
        btTypes.setPosition(0, 50);
        play(new Write(btTypes, 1.2));

        TextMob btProp = new TextMob("Max nodes at level k = 2^k  |  Max nodes in tree of height h = 2^(h+1) - 1")
                .setFontSize(20).setFillColor(Colors.GOLD);
        btProp.setPosition(0, 100);
        play(new Write(btProp, 1.2));
        hold(2.5);

        play(new FadeOut(btStruct, 0.3), new FadeOut(btTree, 0.3), new FadeOut(btHeading, 0.3),
             new FadeOut(btDef, 0.3), new FadeOut(btTypes, 0.3), new FadeOut(btProp, 0.3));

        // ================================================================
        // SECTION 4: Tree Traversals
        // ================================================================
        TextMob travHeading = new TextMob("Binary Tree Traversals")
                .setFontSize(48).setBold().setFillColor(Colors.GREEN);
        travHeading.setPosition(0, -420);
        play(new Write(travHeading, 0.8));

        //       20(0)
        //      /   \
        //    10(1)  30(2)
        //   / \       \
        //  5(3) 15(4) 40(6)
        TreeMob travTree = new TreeMob("20", "10", "30", "5", "15", null, "40");
        travTree.setPosition(-350, -100);
        play(new FadeIn(travTree, 1.0));

        // --- Inorder: Left, Root, Right ---
        TextMob inLabel = new TextMob("Inorder (Left, Root, Right):")
                .setFontSize(26).setBold().setFillColor(Colors.GOLD);
        inLabel.setPosition(250, -280);
        play(new FadeIn(inLabel, 0.4));

        TextMob inResult = new TextMob("")
                .setFontSize(24).setFillColor(Colors.WHITE).setFontFamily("Monospace");
        inResult.setPosition(250, -230);
        add(inResult);

        // Inorder: 5, 10, 15, 20, 30, 40
        int[] inorderNodes = {3, 1, 4, 0, 2, 6};
        String[] inorderVals = {"5", "10", "15", "20", "30", "40"};
        String inStr = "";
        for (int i = 0; i < inorderNodes.length; i++) {
            travTree.highlightNode(inorderNodes[i]);
            travTree.setNodeColor(inorderNodes[i], Colors.withAlpha(Colors.GOLD, 0.4));
            inStr += (i > 0 ? ", " : "") + inorderVals[i];
            inResult.setText(inStr);
            hold(0.7);
            travTree.clearHighlight();
        }
        hold(1.0);

        // Reset tree colors
        for (int idx : inorderNodes)
            travTree.setNodeColor(idx, Colors.withAlpha(Colors.GREEN, 0.2));

        // --- Preorder: Root, Left, Right ---
        TextMob preLabel = new TextMob("Preorder (Root, Left, Right):")
                .setFontSize(26).setBold().setFillColor(Colors.TEAL);
        preLabel.setPosition(250, -160);
        play(new FadeIn(preLabel, 0.4));

        TextMob preResult = new TextMob("")
                .setFontSize(24).setFillColor(Colors.WHITE).setFontFamily("Monospace");
        preResult.setPosition(250, -110);
        add(preResult);

        // Preorder: 20, 10, 5, 15, 30, 40
        int[] preorderNodes = {0, 1, 3, 4, 2, 6};
        String[] preorderVals = {"20", "10", "5", "15", "30", "40"};
        String preStr = "";
        for (int i = 0; i < preorderNodes.length; i++) {
            travTree.highlightNode(preorderNodes[i]);
            travTree.setNodeColor(preorderNodes[i], Colors.withAlpha(Colors.TEAL, 0.4));
            preStr += (i > 0 ? ", " : "") + preorderVals[i];
            preResult.setText(preStr);
            hold(0.7);
            travTree.clearHighlight();
        }
        hold(1.0);

        // Reset tree colors
        for (int idx : preorderNodes)
            travTree.setNodeColor(idx, Colors.withAlpha(Colors.GREEN, 0.2));

        // --- Postorder: Left, Right, Root ---
        TextMob postLabel = new TextMob("Postorder (Left, Right, Root):")
                .setFontSize(26).setBold().setFillColor(Colors.ORANGE);
        postLabel.setPosition(250, -40);
        play(new FadeIn(postLabel, 0.4));

        TextMob postResult = new TextMob("")
                .setFontSize(24).setFillColor(Colors.WHITE).setFontFamily("Monospace");
        postResult.setPosition(250, 10);
        add(postResult);

        // Postorder: 5, 15, 10, 40, 30, 20
        int[] postorderNodes = {3, 4, 1, 6, 2, 0};
        String[] postorderVals = {"5", "15", "10", "40", "30", "20"};
        String postStr = "";
        for (int i = 0; i < postorderNodes.length; i++) {
            travTree.highlightNode(postorderNodes[i]);
            travTree.setNodeColor(postorderNodes[i], Colors.withAlpha(Colors.ORANGE, 0.4));
            postStr += (i > 0 ? ", " : "") + postorderVals[i];
            postResult.setText(postStr);
            hold(0.7);
            travTree.clearHighlight();
        }

        TextMob travTip = new TextMob("Mnemonic: In=LNR, Pre=NLR, Post=LRN (N=Node, L=Left, R=Right)")
                .setFontSize(20).setFillColor(Colors.YELLOW);
        travTip.setPosition(0, 120);
        play(new FadeIn(travTip, 0.5));
        hold(2.5);

        play(new FadeOut(travTree, 0.3), new FadeOut(travHeading, 0.3),
             new FadeOut(inLabel, 0.2), new FadeOut(inResult, 0.2),
             new FadeOut(preLabel, 0.2), new FadeOut(preResult, 0.2),
             new FadeOut(postLabel, 0.2), new FadeOut(postResult, 0.2),
             new FadeOut(travTip, 0.2));

        // ================================================================
        // SECTION 5: BST Property
        // ================================================================
        TextMob bstHeading = new TextMob("Binary Search Tree (BST)")
                .setFontSize(48).setBold().setFillColor(Colors.GREEN);
        bstHeading.setPosition(0, -420);
        play(new Write(bstHeading, 0.8));

        TextMob bstProp = new TextMob("For every node: left subtree values < node < right subtree values")
                .setFontSize(24).setFillColor(Colors.GOLD);
        bstProp.setPosition(0, -350);
        play(new Write(bstProp, 1.2));

        //       20
        //      /  \
        //    10    30
        //   / \   / \
        //  5  15 25  40
        TreeMob bstTree = new TreeMob("20", "10", "30", "5", "15", "25", "40");
        bstTree.setPosition(0, -100);
        play(new FadeIn(bstTree, 1.2));

        // Color code: left subtree vs right subtree
        TextMob bstLeft = new TextMob("Left subtree of 20: {5, 10, 15} — all < 20")
                .setFontSize(22).setFillColor(Colors.TEAL);
        bstLeft.setPosition(0, 120);
        play(new FadeIn(bstLeft, 0.4));
        bstTree.setNodeColor(1, Colors.withAlpha(Colors.TEAL, 0.4));
        bstTree.setNodeColor(3, Colors.withAlpha(Colors.TEAL, 0.4));
        bstTree.setNodeColor(4, Colors.withAlpha(Colors.TEAL, 0.4));
        hold(1.5);

        TextMob bstRight = new TextMob("Right subtree of 20: {25, 30, 40} — all > 20")
                .setFontSize(22).setFillColor(Colors.ORANGE);
        bstRight.setPosition(0, 170);
        play(new FadeIn(bstRight, 0.4));
        bstTree.setNodeColor(2, Colors.withAlpha(Colors.ORANGE, 0.4));
        bstTree.setNodeColor(5, Colors.withAlpha(Colors.ORANGE, 0.4));
        bstTree.setNodeColor(6, Colors.withAlpha(Colors.ORANGE, 0.4));
        hold(1.5);

        TextMob bstInorder = new TextMob("Key insight: Inorder traversal of BST gives SORTED order: 5,10,15,20,25,30,40")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        bstInorder.setPosition(0, 230);
        play(new Write(bstInorder, 1.2));
        hold(2.5);

        play(new FadeOut(bstTree, 0.3), new FadeOut(bstHeading, 0.3), new FadeOut(bstProp, 0.3),
             new FadeOut(bstLeft, 0.2), new FadeOut(bstRight, 0.2), new FadeOut(bstInorder, 0.2));

        // ================================================================
        // SECTION 6: BST Search
        // ================================================================
        TextMob searchHeading = new TextMob("BST Search")
                .setFontSize(48).setBold().setFillColor(Colors.GREEN);
        searchHeading.setPosition(0, -420);
        play(new Write(searchHeading, 0.8));

        TextMob searchAlgo = new TextMob("Compare with root: go left if smaller, go right if larger")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        searchAlgo.setPosition(0, -360);
        play(new Write(searchAlgo, 1.0));

        TreeMob searchTree = new TreeMob("20", "10", "30", "5", "15", "25", "40");
        searchTree.setPosition(-300, -100);
        play(new FadeIn(searchTree, 1.0));

        TextMob searchTarget = new TextMob("Search for: 25")
                .setFontSize(28).setFillColor(Colors.RED);
        searchTarget.setPosition(300, -300);
        play(new FadeIn(searchTarget, 0.4));

        TextMob searchStep = new TextMob("")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        searchStep.setPosition(300, -200);
        add(searchStep);

        // Step 1: root=20, 25 > 20 -> go right
        searchTree.highlightNode(0);
        searchStep.setText("Compare 25 with 20: 25 > 20 -> go RIGHT");
        hold(1.5);

        // Step 2: node=30, 25 < 30 -> go left
        searchTree.clearHighlight();
        searchTree.setNodeColor(0, Colors.withAlpha(Colors.DARK_GRAY, 0.3));
        searchTree.highlightNode(2);
        searchStep.setText("Compare 25 with 30: 25 < 30 -> go LEFT");
        hold(1.5);

        // Step 3: node=25, 25 == 25 -> FOUND!
        searchTree.clearHighlight();
        searchTree.setNodeColor(2, Colors.withAlpha(Colors.DARK_GRAY, 0.3));
        searchTree.highlightNode(5);
        searchTree.setNodeColor(5, Colors.withAlpha(Colors.GREEN, 0.5));
        searchStep.setText("Compare 25 with 25: FOUND!");
        searchStep.setFillColor(Colors.GREEN);
        hold(1.5);

        TextMob searchTime = new TextMob("Only visited 3 nodes out of 7 — O(log n) average!")
                .setFontSize(22).setFillColor(Colors.GOLD);
        searchTime.setPosition(300, -100);
        play(new FadeIn(searchTime, 0.5));
        hold(2.0);

        play(new FadeOut(searchTree, 0.3), new FadeOut(searchHeading, 0.3),
             new FadeOut(searchAlgo, 0.3), new FadeOut(searchTarget, 0.3),
             new FadeOut(searchStep, 0.3), new FadeOut(searchTime, 0.3));

        // ================================================================
        // SECTION 7: BST Insertion
        // ================================================================
        TextMob insertHeading = new TextMob("BST Insertion")
                .setFontSize(48).setBold().setFillColor(Colors.GREEN);
        insertHeading.setPosition(0, -420);
        play(new Write(insertHeading, 0.8));

        TextMob insertAlgo = new TextMob("Search for the correct position, then insert as a leaf")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        insertAlgo.setPosition(0, -360);
        play(new Write(insertAlgo, 1.0));

        // Start with tree: 20, 10, 30, 5
        // Insert 12: 12 < 20 -> left, 12 > 10 -> right, insert as right child of 10's right
        TreeMob insertTree = new TreeMob("20", "10", "30", "5", null, "25", "40");
        insertTree.setPosition(-300, -100);
        play(new FadeIn(insertTree, 1.0));

        TextMob insertVal = new TextMob("Insert value: 12")
                .setFontSize(28).setFillColor(Colors.RED);
        insertVal.setPosition(300, -300);
        play(new FadeIn(insertVal, 0.4));

        TextMob insertStep = new TextMob("")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        insertStep.setPosition(300, -200);
        add(insertStep);

        // Step 1: 12 < 20 -> go left
        insertTree.highlightNode(0);
        insertStep.setText("12 < 20 -> go LEFT");
        hold(1.2);

        // Step 2: 12 > 10 -> go right
        insertTree.clearHighlight();
        insertTree.highlightNode(1);
        insertStep.setText("12 > 10 -> go RIGHT");
        hold(1.2);

        // Step 3: right child of 10 is NULL -> insert here!
        insertTree.clearHighlight();
        insertStep.setText("Right child of 10 is NULL -> insert 12 here!");
        insertStep.setFillColor(Colors.GREEN);
        insertTree.setNodeValue(4, "12"); // index 4 = right child of node 1
        insertTree.setNodeColor(4, Colors.withAlpha(Colors.GREEN, 0.5));
        insertTree.highlightNode(4);
        hold(2.0);

        TextMob insertNote = new TextMob("New nodes are always inserted as leaves!")
                .setFontSize(22).setFillColor(Colors.GOLD);
        insertNote.setPosition(300, -100);
        play(new FadeIn(insertNote, 0.5));
        hold(2.0);

        play(new FadeOut(insertTree, 0.3), new FadeOut(insertHeading, 0.3),
             new FadeOut(insertAlgo, 0.3), new FadeOut(insertVal, 0.3),
             new FadeOut(insertStep, 0.3), new FadeOut(insertNote, 0.3));

        // ================================================================
        // SECTION 8: BST Deletion — Three Cases
        // ================================================================
        TextMob delHeading = new TextMob("BST Deletion: Three Cases")
                .setFontSize(44).setBold().setFillColor(Colors.GREEN);
        delHeading.setPosition(0, -420);
        play(new Write(delHeading, 0.8));

        // --- Case 1: Delete a leaf ---
        TextMob case1 = new TextMob("Case 1: Node is a LEAF (no children)")
                .setFontSize(28).setBold().setFillColor(Colors.GOLD);
        case1.setPosition(0, -350);
        play(new FadeIn(case1, 0.4));

        TreeMob delTree1 = new TreeMob("20", "10", "30", "5", "15", "25", "40");
        delTree1.setPosition(-300, -120);
        play(new FadeIn(delTree1, 0.8));

        TextMob del1Step = new TextMob("Delete 5 (leaf): simply remove it")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        del1Step.setPosition(300, -200);
        play(new FadeIn(del1Step, 0.4));

        delTree1.highlightNode(3);
        delTree1.setNodeColor(3, Colors.withAlpha(Colors.RED, 0.4));
        hold(1.0);
        delTree1.setNodeValue(3, null);
        del1Step.setText("Node 5 removed. No children to worry about.");
        del1Step.setFillColor(Colors.GREEN);
        hold(1.5);

        play(new FadeOut(delTree1, 0.3), new FadeOut(case1, 0.3), new FadeOut(del1Step, 0.3));

        // --- Case 2: Delete node with ONE child ---
        TextMob case2 = new TextMob("Case 2: Node has ONE child")
                .setFontSize(28).setBold().setFillColor(Colors.GOLD);
        case2.setPosition(0, -350);
        play(new FadeIn(case2, 0.4));

        //       20
        //      /  \
        //    10    30
        //     \      \
        //     15     40
        TreeMob delTree2 = new TreeMob("20", "10", "30", null, "15", null, "40");
        delTree2.setPosition(-300, -120);
        play(new FadeIn(delTree2, 0.8));

        TextMob del2Step = new TextMob("Delete 10 (one child: 15): replace with child")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        del2Step.setPosition(300, -200);
        play(new FadeIn(del2Step, 0.4));

        delTree2.highlightNode(1);
        delTree2.setNodeColor(1, Colors.withAlpha(Colors.RED, 0.4));
        hold(1.0);

        del2Step.setText("Replace node 10 with its child 15");
        delTree2.setNodeValue(1, "15");
        delTree2.setNodeColor(1, Colors.withAlpha(Colors.GREEN, 0.4));
        delTree2.setNodeValue(4, null);
        hold(1.5);

        del2Step.setText("BST property maintained: 15 < 20");
        del2Step.setFillColor(Colors.GREEN);
        hold(1.5);

        play(new FadeOut(delTree2, 0.3), new FadeOut(case2, 0.3), new FadeOut(del2Step, 0.3));

        // --- Case 3: Delete node with TWO children ---
        TextMob case3 = new TextMob("Case 3: Node has TWO children (most complex)")
                .setFontSize(28).setBold().setFillColor(Colors.GOLD);
        case3.setPosition(0, -350);
        play(new FadeIn(case3, 0.4));

        TreeMob delTree3 = new TreeMob("20", "10", "30", "5", "15", "25", "40");
        delTree3.setPosition(-300, -120);
        play(new FadeIn(delTree3, 0.8));

        TextMob del3Step = new TextMob("Delete 20 (root, two children)")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        del3Step.setPosition(300, -220);
        play(new FadeIn(del3Step, 0.4));

        delTree3.highlightNode(0);
        delTree3.setNodeColor(0, Colors.withAlpha(Colors.RED, 0.4));
        hold(1.0);

        del3Step.setText("Find inorder successor: smallest in right subtree");
        hold(1.0);

        // Inorder successor of 20 is 25 (leftmost in right subtree)
        delTree3.highlightNode(5);
        delTree3.setNodeColor(5, Colors.withAlpha(Colors.TEAL, 0.5));
        del3Step.setText("Inorder successor = 25 (leftmost in right subtree)");
        hold(1.5);

        del3Step.setText("Replace 20's value with 25, delete the original 25");
        delTree3.setNodeValue(0, "25");
        delTree3.setNodeColor(0, Colors.withAlpha(Colors.GREEN, 0.4));
        delTree3.setNodeValue(5, null); // remove old 25
        delTree3.clearHighlight();
        hold(2.0);

        del3Step.setText("BST property maintained! Tree is still valid.");
        del3Step.setFillColor(Colors.GREEN);
        hold(2.0);

        play(new FadeOut(delTree3, 0.3), new FadeOut(case3, 0.3),
             new FadeOut(del3Step, 0.3), new FadeOut(delHeading, 0.3));

        // ================================================================
        // SECTION 9: BST Code — Insert and Search
        // ================================================================
        TextMob codeHeading = new TextMob("BST Code: Insert & Search (Recursive)")
                .setFontSize(40).setBold().setFillColor(Colors.GREEN);
        codeHeading.setPosition(0, -440);
        play(new Write(codeHeading, 0.8));

        CodeBlock insertCode = new CodeBlock(
            "struct Node* insert(struct Node* root, int key) {\n" +
            "    if (root == NULL)\n" +
            "        return newNode(key);\n" +
            "    if (key < root->data)\n" +
            "        root->left = insert(root->left, key);\n" +
            "    else if (key > root->data)\n" +
            "        root->right = insert(root->right, key);\n" +
            "    return root;\n" +
            "}", 17
        );
        insertCode.setPosition(-400, -220);
        play(new RevealCode(insertCode, 3.0));

        CodeBlock searchCode = new CodeBlock(
            "struct Node* search(struct Node* root, int key) {\n" +
            "    if (root == NULL || root->data == key)\n" +
            "        return root;\n" +
            "    if (key < root->data)\n" +
            "        return search(root->left, key);\n" +
            "    else\n" +
            "        return search(root->right, key);\n" +
            "}", 17
        );
        searchCode.setPosition(400, -220);
        play(new RevealCode(searchCode, 3.0));

        TextMob codeNote = new TextMob("Both are recursive — base case is NULL (not found / insert here)")
                .setFontSize(22).setFillColor(Colors.TEAL);
        codeNote.setPosition(0, 150);
        play(new FadeIn(codeNote, 0.5));

        // Highlight key lines
        insertCode.highlightLine(1);
        insertCode.highlightLine(2);
        TextMob baseNote = new TextMob("Base case: empty spot found, create new node")
                .setFontSize(20).setFillColor(Colors.YELLOW);
        baseNote.setPosition(0, 210);
        play(new FadeIn(baseNote, 0.4));
        hold(2.0);
        insertCode.clearHighlights();
        play(new FadeOut(baseNote, 0.2));

        insertCode.highlightLine(3);
        insertCode.highlightLine(4);
        insertCode.highlightLine(5);
        insertCode.highlightLine(6);
        TextMob recurNote = new TextMob("Recursive step: go left or right depending on comparison")
                .setFontSize(20).setFillColor(Colors.YELLOW);
        recurNote.setPosition(0, 210);
        play(new FadeIn(recurNote, 0.4));
        hold(2.0);

        play(new FadeOut(insertCode, 0.3), new FadeOut(searchCode, 0.3),
             new FadeOut(codeHeading, 0.3), new FadeOut(codeNote, 0.3),
             new FadeOut(recurNote, 0.3));

        // ================================================================
        // SECTION 10: Balanced vs Unbalanced BST
        // ================================================================
        TextMob balHeading = new TextMob("Balanced vs Unbalanced BST")
                .setFontSize(44).setBold().setFillColor(Colors.GREEN);
        balHeading.setPosition(0, -420);
        play(new Write(balHeading, 0.8));

        // Balanced tree
        TextMob balLabel = new TextMob("Balanced BST")
                .setFontSize(26).setBold().setFillColor(Colors.GREEN);
        balLabel.setPosition(-350, -340);
        play(new FadeIn(balLabel, 0.3));

        TreeMob balTree = new TreeMob("20", "10", "30", "5", "15", "25", "40");
        balTree.setPosition(-350, -100);
        play(new FadeIn(balTree, 0.8));

        TextMob balTime = new TextMob("Height: 2, Search: O(log n)")
                .setFontSize(20).setFillColor(Colors.GREEN);
        balTime.setPosition(-350, 100);
        play(new FadeIn(balTime, 0.4));

        // Degenerate (skewed) tree: 5, 10, 15, 20, 25, 30, 40
        // As array: [5, null, 10, null, null, null, 15] ... this gets unwieldy
        // Instead show it as a linked-list-like structure
        TextMob unbalLabel = new TextMob("Degenerate BST (Skewed)")
                .setFontSize(26).setBold().setFillColor(Colors.RED);
        unbalLabel.setPosition(350, -340);
        play(new FadeIn(unbalLabel, 0.3));

        // Show as linked list
        LinkedListMob skewedList = new LinkedListMob("5", "10", "15", "20");
        skewedList.setHeadLabel("root");
        skewedList.setPosition(350, -150);
        play(new FadeIn(skewedList, 0.8));

        TextMob skewedNote = new TextMob("Inserting sorted data: 5, 10, 15, 20...")
                .setFontSize(20).setFillColor(Colors.ORANGE);
        skewedNote.setPosition(350, -60);
        play(new FadeIn(skewedNote, 0.4));

        TextMob unbalTime = new TextMob("Height: n-1, Search: O(n) -- like a linked list!")
                .setFontSize(20).setFillColor(Colors.RED);
        unbalTime.setPosition(350, 0);
        play(new FadeIn(unbalTime, 0.4));

        TextMob balConclusion = new TextMob("Balanced tree guarantees O(log n) operations. Skewed tree degrades to O(n).")
                .setFontSize(22).setFillColor(Colors.GOLD);
        balConclusion.setPosition(0, 180);
        play(new Write(balConclusion, 1.2));
        hold(3.0);

        play(new FadeOut(balTree, 0.3), new FadeOut(skewedList, 0.3),
             new FadeOut(balHeading, 0.3), new FadeOut(balLabel, 0.3),
             new FadeOut(unbalLabel, 0.3), new FadeOut(balTime, 0.3),
             new FadeOut(unbalTime, 0.3), new FadeOut(skewedNote, 0.3),
             new FadeOut(balConclusion, 0.3));

        // ================================================================
        // SECTION 11: AVL Tree Concept
        // ================================================================
        TextMob avlHeading = new TextMob("AVL Tree: Self-Balancing BST")
                .setFontSize(44).setBold().setFillColor(Colors.GREEN);
        avlHeading.setPosition(0, -420);
        play(new Write(avlHeading, 0.8));

        TextMob avlDef = new TextMob("Balance Factor = height(left) - height(right), must be in {-1, 0, 1}")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        avlDef.setPosition(0, -360);
        play(new Write(avlDef, 1.2));

        // Show unbalanced tree
        TextMob beforeLabel = new TextMob("Before Rotation (unbalanced):")
                .setFontSize(24).setBold().setFillColor(Colors.RED);
        beforeLabel.setPosition(-350, -280);
        play(new FadeIn(beforeLabel, 0.3));

        // Unbalanced: 30 -> 20 -> 10 (left-left case)
        // Array: [30, 20, null, 10]
        TreeMob unbalAVL = new TreeMob("30", "20", null, "10");
        unbalAVL.setPosition(-350, -120);
        play(new FadeIn(unbalAVL, 0.8));

        TextMob bf = new TextMob("BF(30) = 2 -> VIOLATION!")
                .setFontSize(20).setFillColor(Colors.RED);
        bf.setPosition(-350, 50);
        play(new FadeIn(bf, 0.4));
        hold(1.5);

        // Show after rotation
        TextMob afterLabel = new TextMob("After Right Rotation (balanced):")
                .setFontSize(24).setBold().setFillColor(Colors.GREEN);
        afterLabel.setPosition(350, -280);
        play(new FadeIn(afterLabel, 0.3));

        // Balanced: [20, 10, 30]
        TreeMob balAVL = new TreeMob("20", "10", "30");
        balAVL.setPosition(350, -120);
        play(new FadeIn(balAVL, 0.8));

        TextMob bfAfter = new TextMob("BF(20) = 0 -> Balanced!")
                .setFontSize(20).setFillColor(Colors.GREEN);
        bfAfter.setPosition(350, 50);
        play(new FadeIn(bfAfter, 0.4));

        // Arrow between them
        ArrowMob rotArrow = new ArrowMob(-100, -120, 100, -120);
        rotArrow.setStrokeColor(Colors.GOLD);
        play(new DrawArrow(rotArrow, 0.8));

        TextMob rotLabel = new TextMob("Right\nRotation")
                .setFontSize(20).setFillColor(Colors.GOLD);
        rotLabel.setPosition(0, -160);
        play(new FadeIn(rotLabel, 0.3));

        TextMob avlNote = new TextMob("AVL guarantees O(log n) for search, insert, delete by auto-balancing")
                .setFontSize(22).setFillColor(Colors.TEAL);
        avlNote.setPosition(0, 150);
        play(new Write(avlNote, 1.0));
        hold(3.0);

        play(new FadeOut(unbalAVL, 0.3), new FadeOut(balAVL, 0.3),
             new FadeOut(avlHeading, 0.3), new FadeOut(avlDef, 0.3),
             new FadeOut(beforeLabel, 0.2), new FadeOut(afterLabel, 0.2),
             new FadeOut(bf, 0.2), new FadeOut(bfAfter, 0.2),
             new FadeOut(rotArrow, 0.2), new FadeOut(rotLabel, 0.2),
             new FadeOut(avlNote, 0.2));

        // ================================================================
        // SECTION 12: Complexity Comparison
        // ================================================================
        TextMob compHeading = new TextMob("BST Complexity: Balanced vs Unbalanced")
                .setFontSize(40).setBold().setFillColor(Colors.BLUE);
        compHeading.setPosition(0, -380);
        play(new Write(compHeading, 0.8));

        String[][] compRows = {
            {"Operation",    "Balanced BST", "Skewed BST", "AVL Tree"},
            {"Search",       "O(log n)",     "O(n)",       "O(log n)"},
            {"Insert",       "O(log n)",     "O(n)",       "O(log n)"},
            {"Delete",       "O(log n)",     "O(n)",       "O(log n)"},
            {"Min/Max",      "O(log n)",     "O(n)",       "O(log n)"},
            {"Inorder trav", "O(n)",         "O(n)",       "O(n)"},
        };

        double compY = -280;
        TextMob[] compMobs = new TextMob[compRows.length];
        for (int i = 0; i < compRows.length; i++) {
            String line = String.format("%-16s %-16s %-14s %-14s",
                    compRows[i][0], compRows[i][1], compRows[i][2], compRows[i][3]);
            compMobs[i] = new TextMob(line)
                    .setFontSize(i == 0 ? 22 : 20)
                    .setFillColor(i == 0 ? Colors.GOLD : Colors.WHITE)
                    .setFontFamily("Monospace");
            compMobs[i].setPosition(0, compY + i * 45);
            play(new Write(compMobs[i], 0.5));
            hold(0.2);
        }

        TextMob compNote = new TextMob("Always ensure BST stays balanced for guaranteed O(log n) performance")
                .setFontSize(20).setFillColor(Colors.TEAL);
        compNote.setPosition(0, compY + compRows.length * 45 + 15);
        play(new FadeIn(compNote, 0.5));
        hold(3.0);

        play(new FadeOut(compHeading, 0.3), new FadeOut(compNote, 0.3));
        for (TextMob m : compMobs) play(new FadeOut(m, 0.1));

        // ================================================================
        // SECTION 13: Applications
        // ================================================================
        TextMob appHeading = new TextMob("Applications of Trees")
                .setFontSize(48).setBold().setFillColor(Colors.GREEN);
        appHeading.setPosition(0, -380);
        play(new Write(appHeading, 0.8));

        // Expression tree
        TextMob app1Title = new TextMob("1. Expression Trees")
                .setFontSize(28).setBold().setFillColor(Colors.GOLD);
        app1Title.setPosition(-350, -290);
        play(new FadeIn(app1Title, 0.4));

        // Expression: (3 + 5) * 2
        // Tree: [*, +, 2, 3, 5]
        TreeMob exprTree = new TreeMob("*", "+", "2", "3", "5");
        exprTree.setPosition(-350, -100);
        play(new FadeIn(exprTree, 0.8));

        TextMob exprNote = new TextMob("Represents (3 + 5) * 2 = 16")
                .setFontSize(20).setFillColor(Colors.LIGHT_GRAY);
        exprNote.setPosition(-350, 60);
        play(new FadeIn(exprNote, 0.4));

        // Other applications
        TextMob app2 = new TextMob("2. File Systems: directory tree hierarchy")
                .setFontSize(24).setFillColor(Colors.WHITE);
        app2.setPosition(250, -250);
        play(new Write(app2, 0.7));

        TextMob app3 = new TextMob("3. Database indexing: B-trees, B+ trees")
                .setFontSize(24).setFillColor(Colors.WHITE);
        app3.setPosition(250, -190);
        play(new Write(app3, 0.7));

        TextMob app4 = new TextMob("4. Huffman coding: compression trees")
                .setFontSize(24).setFillColor(Colors.WHITE);
        app4.setPosition(250, -130);
        play(new Write(app4, 0.7));

        TextMob app5 = new TextMob("5. Decision trees: AI / ML classification")
                .setFontSize(24).setFillColor(Colors.WHITE);
        app5.setPosition(250, -70);
        play(new Write(app5, 0.7));

        TextMob app6 = new TextMob("6. Syntax trees: compilers parse code into ASTs")
                .setFontSize(24).setFillColor(Colors.WHITE);
        app6.setPosition(250, -10);
        play(new Write(app6, 0.7));
        hold(3.0);

        play(new FadeOut(appHeading, 0.3), new FadeOut(app1Title, 0.3),
             new FadeOut(exprTree, 0.3), new FadeOut(exprNote, 0.2),
             new FadeOut(app2, 0.2), new FadeOut(app3, 0.2),
             new FadeOut(app4, 0.2), new FadeOut(app5, 0.2), new FadeOut(app6, 0.2));

        // ================================================================
        // SECTION 14: Summary
        // ================================================================
        TextMob sumTitle = new TextMob("Summary: Trees & BST")
                .setFontSize(52).setBold().setFillColor(Colors.GREEN);
        sumTitle.setPosition(0, -380);
        play(new FadeIn(sumTitle, 0.6));

        String[] sumPoints = {
            "Tree = hierarchical structure with root, children, leaves",
            "Binary tree: each node has at most 2 children",
            "Traversals: Inorder (LNR), Preorder (NLR), Postorder (LRN)",
            "BST property: left < root < right (inorder = sorted)",
            "Search/Insert: O(log n) average by going left or right",
            "Delete: 3 cases — leaf, one child, two children (successor)",
            "Balanced BST: O(log n) guaranteed; skewed: O(n) worst case",
            "AVL trees auto-balance using rotations"
        };

        TextMob[] sumMobs = new TextMob[sumPoints.length];
        for (int i = 0; i < sumPoints.length; i++) {
            sumMobs[i] = new TextMob("  " + (i + 1) + ". " + sumPoints[i])
                    .setFontSize(21).setFillColor(Colors.WHITE);
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
