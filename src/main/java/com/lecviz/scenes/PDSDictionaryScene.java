package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Scene;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Vec2;

/**
 * PDS Lecture: Dictionaries / Hash Tables.
 * Covers hash functions, collisions, chaining, open addressing,
 * load factor, code examples, and complexity analysis.
 */
public class PDSDictionaryScene extends Scene {

    @Override
    public void construct() {

        // ===== SECTION 1: Title Card =====
        TextMob title = new TextMob("Dictionaries & Hash Tables")
                .setFontSize(56)
                .setBold()
                .setFillColor(Colors.GOLD);
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

        // ===== SECTION 2: Dictionary ADT =====
        TextMob heading = new TextMob("Dictionary ADT")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.GOLD);
        heading.setPosition(0, -400);

        TextMob defn = new TextMob("A collection of key-value pairs with fast lookup by key")
                .setFontSize(24)
                .setFillColor(Colors.LIGHT_GRAY);
        defn.setPosition(0, -340);

        play(new Write(heading, 0.8));
        play(new Write(defn, 1.0));
        hold(0.5);

        // Show key-value pairs visually
        TextMob kvTitle = new TextMob("Key -> Value")
                .setFontSize(26).setBold().setFillColor(Colors.YELLOW);
        kvTitle.setPosition(-400, -250);
        play(new FadeIn(kvTitle, 0.3));

        String[][] pairs = {
            {"\"name\"", "\"Rupesh\""},
            {"\"age\"", "45"},
            {"\"dept\"", "\"CSE\""},
            {"\"id\"", "1042"}
        };

        TextMob[] keyMobs = new TextMob[pairs.length];
        TextMob[] valMobs = new TextMob[pairs.length];
        ArrowMob[] kvArrows = new ArrowMob[pairs.length];

        for (int i = 0; i < pairs.length; i++) {
            double yPos = -190 + i * 50;
            keyMobs[i] = new TextMob(pairs[i][0])
                    .setFontSize(22).setFillColor(Colors.TEAL);
            keyMobs[i].setPosition(-470, yPos);

            kvArrows[i] = new ArrowMob(-390, yPos, -340, yPos);
            kvArrows[i].setStrokeColor(Colors.LIGHT_GRAY);

            valMobs[i] = new TextMob(pairs[i][1])
                    .setFontSize(22).setFillColor(Colors.WHITE);
            valMobs[i].setPosition(-290, yPos);

            play(new FadeIn(keyMobs[i], 0.2), new DrawArrow(kvArrows[i], 0.2), new FadeIn(valMobs[i], 0.2));
            hold(0.2);
        }
        hold(0.5);

        // Operations
        RectMob opsBox = new RectMob(380, 200);
        opsBox.setCornerRadius(10);
        opsBox.setFillColor(Colors.withAlpha(Colors.GOLD, 0.12));
        opsBox.setStrokeColor(Colors.GOLD);
        opsBox.setPosition(300, -160);

        TextMob opsTitle = new TextMob("Operations")
                .setFontSize(24).setBold().setFillColor(Colors.GOLD);
        opsTitle.setPosition(300, -240);

        TextMob op1 = new TextMob("insert(key, value)")
                .setFontSize(20).setFillColor(Colors.WHITE);
        op1.setPosition(300, -190);

        TextMob op2 = new TextMob("search(key) -> value")
                .setFontSize(20).setFillColor(Colors.WHITE);
        op2.setPosition(300, -150);

        TextMob op3 = new TextMob("delete(key)")
                .setFontSize(20).setFillColor(Colors.WHITE);
        op3.setPosition(300, -110);

        TextMob opsGoal = new TextMob("Goal: All three in O(1) average!")
                .setFontSize(20).setBold().setFillColor(Colors.GREEN);
        opsGoal.setPosition(300, -60);

        play(new FadeIn(opsBox, 0.4), new FadeIn(opsTitle, 0.3));
        play(new Write(op1, 0.5));
        play(new Write(op2, 0.5));
        play(new Write(op3, 0.5));
        play(new Write(opsGoal, 0.6));
        hold(2.0);

        play(new FadeOut(heading, 0.3), new FadeOut(defn, 0.3), new FadeOut(kvTitle, 0.3),
             new FadeOut(opsBox, 0.3), new FadeOut(opsTitle, 0.3),
             new FadeOut(op1, 0.3), new FadeOut(op2, 0.3), new FadeOut(op3, 0.3), new FadeOut(opsGoal, 0.3));
        for (int i = 0; i < pairs.length; i++) {
            play(new FadeOut(keyMobs[i], 0.2), new FadeOut(kvArrows[i], 0.2), new FadeOut(valMobs[i], 0.2));
        }

        // ===== SECTION 3: Direct Addressing =====
        TextMob daHeading = new TextMob("Direct Addressing")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.BLUE);
        daHeading.setPosition(0, -400);
        play(new Write(daHeading, 0.8));

        TextMob daDesc = new TextMob("If keys are small integers 0..m-1, use key as array index directly")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        daDesc.setPosition(0, -340);
        play(new Write(daDesc, 1.0));
        hold(0.5);

        // Show direct address table
        ArrayMob daTable = new ArrayMob(" ", "cat", " ", "dog", " ", " ", " ", "fox");
        daTable.setLabel("T[0..7]");
        daTable.setPosition(0, -200);

        play(new FadeIn(daTable, 0.6));

        TextMob daEx = new TextMob("T[1] = \"cat\", T[3] = \"dog\", T[7] = \"fox\"")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        daEx.setPosition(0, -110);
        play(new Write(daEx, 0.8));
        hold(0.5);

        daTable.highlight(1);
        daTable.highlight(3);
        daTable.highlight(7);
        hold(1.0);

        TextMob daProblem = new TextMob("Problem: What if keys are huge? (e.g., phone numbers, strings)")
                .setFontSize(22).setFillColor(Colors.RED);
        daProblem.setPosition(0, -50);
        play(new Write(daProblem, 1.0));

        TextMob daProblem2 = new TextMob("Array of size 10¹ is impractical -> need a hash function!")
                .setFontSize(22).setFillColor(Colors.ORANGE);
        daProblem2.setPosition(0, 0);
        play(new Write(daProblem2, 1.0));
        hold(2.0);

        play(new FadeOut(daHeading, 0.3), new FadeOut(daDesc, 0.3),
             new FadeOut(daTable, 0.3), new FadeOut(daEx, 0.3),
             new FadeOut(daProblem, 0.3), new FadeOut(daProblem2, 0.3));

        // ===== SECTION 4: Hash Function Concept =====
        TextMob hashHeading = new TextMob("Hash Function")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.PURPLE);
        hashHeading.setPosition(0, -400);
        play(new Write(hashHeading, 0.8));

        TextMob hashDef = new TextMob("Maps a large key space to a small index range: h(key) = key % m")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        hashDef.setPosition(0, -340);
        play(new Write(hashDef, 1.0));
        hold(0.5);

        // Visual: large key space -> small table
        RectMob bigSpace = new RectMob(250, 300);
        bigSpace.setCornerRadius(10);
        bigSpace.setFillColor(Colors.withAlpha(Colors.RED, 0.1));
        bigSpace.setStrokeColor(Colors.RED);
        bigSpace.setPosition(-400, -120);

        TextMob bigLabel = new TextMob("Key Space")
                .setFontSize(20).setBold().setFillColor(Colors.RED);
        bigLabel.setPosition(-400, -260);

        TextMob bigRange = new TextMob("0 .. 999,999")
                .setFontSize(18).setFillColor(Colors.GRAY);
        bigRange.setPosition(-400, -230);

        play(new FadeIn(bigSpace, 0.4), new FadeIn(bigLabel, 0.3), new FadeIn(bigRange, 0.3));

        // Hash function box
        RectMob hashBox = new RectMob(180, 60);
        hashBox.setCornerRadius(8);
        hashBox.setFillColor(Colors.withAlpha(Colors.PURPLE, 0.3));
        hashBox.setStrokeColor(Colors.PURPLE);
        hashBox.setPosition(0, -120);

        TextMob hashFunc = new TextMob("h(k) = k % 7")
                .setFontSize(22).setBold().setFillColor(Colors.PURPLE);
        hashFunc.setPosition(0, -120);

        play(new FadeIn(hashBox, 0.4), new FadeIn(hashFunc, 0.3));

        // Small table
        ArrayMob smallTable = new ArrayMob(" ", " ", " ", " ", " ", " ", " ");
        smallTable.setLabel("Table[0..6]");
        smallTable.setPosition(400, -120);

        play(new FadeIn(smallTable, 0.5));

        // Arrows from big to small through hash
        ArrowMob toHash = new ArrowMob(-275, -120, -90, -120);
        toHash.setStrokeColor(Colors.LIGHT_GRAY);
        ArrowMob fromHash = new ArrowMob(90, -120, 260, -120);
        fromHash.setStrokeColor(Colors.LIGHT_GRAY);
        play(new DrawArrow(toHash, 0.4), new DrawArrow(fromHash, 0.4));
        hold(0.5);

        // Show key mappings
        TextMob mapTitle = new TextMob("Examples: h(k) = k % 7")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        mapTitle.setPosition(-400, 80);
        play(new FadeIn(mapTitle, 0.3));

        int[] keys = {14, 22, 3, 35, 9};
        String[] keyStrs = {"14", "22", "3", "35", "9"};
        int m = 7;
        for (int i = 0; i < keys.length; i++) {
            int idx = keys[i] % m;
            TextMob keyShow = new TextMob("h(" + keyStrs[i] + ") = " + keyStrs[i] + " % 7 = " + idx)
                    .setFontSize(18).setFillColor(Colors.WHITE);
            keyShow.setPosition(-400, 120 + i * 35);
            play(new Write(keyShow, 0.5));

            smallTable.setValue(idx, keyStrs[i]);
            smallTable.setCellColor(idx, Colors.withAlpha(Colors.TEAL, 0.3));
            hold(0.4);
            smallTable.clearCellColors();
        }
        hold(2.0);

        // Transition — fade everything
        play(new FadeOut(hashHeading, 0.3), new FadeOut(hashDef, 0.3),
             new FadeOut(bigSpace, 0.3), new FadeOut(bigLabel, 0.3), new FadeOut(bigRange, 0.3),
             new FadeOut(hashBox, 0.3), new FadeOut(hashFunc, 0.3),
             new FadeOut(smallTable, 0.3),
             new FadeOut(toHash, 0.3), new FadeOut(fromHash, 0.3),
             new FadeOut(mapTitle, 0.3));
        hold(0.3);

        // ===== SECTION 5: Hash Table Visual =====
        TextMob htHeading = new TextMob("Hash Table: Inserting Values")
                .setFontSize(44)
                .setBold()
                .setFillColor(Colors.TEAL);
        htHeading.setPosition(0, -420);
        play(new Write(htHeading, 0.8));

        TextMob htFormula = new TextMob("h(key) = key % 7")
                .setFontSize(24).setFillColor(Colors.YELLOW);
        htFormula.setPosition(0, -370);
        play(new FadeIn(htFormula, 0.3));

        ArrayMob hashTable = new ArrayMob(" ", " ", " ", " ", " ", " ", " ");
        hashTable.setLabel("hash_table[7]");
        hashTable.setPosition(0, -250);
        play(new FadeIn(hashTable, 0.5));

        // Insert values step by step
        int[] insertKeys = {10, 22, 31, 4, 15, 28, 17};
        for (int i = 0; i < insertKeys.length; i++) {
            int idx = insertKeys[i] % 7;
            TextMob insertNote = new TextMob("Insert " + insertKeys[i] + ": h(" + insertKeys[i] + ") = " + insertKeys[i] + " % 7 = " + idx)
                    .setFontSize(22).setFillColor(Colors.WHITE);
            insertNote.setPosition(0, -170);

            play(new FadeIn(insertNote, 0.2));
            hashTable.highlight(idx);
            hold(0.4);

            // Check if collision (idx 3 gets hit twice: 10%7=3 and 31%7=3)
            hashTable.setValue(idx, String.valueOf(insertKeys[i]));
            hashTable.setCellColor(idx, Colors.withAlpha(Colors.GREEN, 0.3));
            hold(0.3);
            hashTable.clearHighlights();
            hashTable.clearCellColors();
            play(new FadeOut(insertNote, 0.15));
        }

        // Now show collision
        TextMob collisionAlert = new TextMob("Wait — key 10 mapped to index 3, but 31 also maps to 3!")
                .setFontSize(22).setBold().setFillColor(Colors.RED);
        collisionAlert.setPosition(0, -130);
        play(new Write(collisionAlert, 1.0));
        hashTable.setCellColor(3, Colors.withAlpha(Colors.RED, 0.4));
        hold(2.0);

        play(new FadeOut(htHeading, 0.3), new FadeOut(htFormula, 0.3),
             new FadeOut(hashTable, 0.3), new FadeOut(collisionAlert, 0.3));

        // ===== SECTION 6: Collision Problem =====
        TextMob collHeading = new TextMob("The Collision Problem")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.RED);
        collHeading.setPosition(0, -400);
        play(new Write(collHeading, 0.8));

        TextMob collDesc = new TextMob("Two different keys hash to the same index — this is inevitable!")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        collDesc.setPosition(0, -340);
        play(new Write(collDesc, 1.0));
        hold(0.5);

        // Pigeonhole principle
        TextMob pigeonhole = new TextMob("Pigeonhole Principle: If n keys > m slots, at least one slot gets 2+ keys")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        pigeonhole.setPosition(0, -270);
        play(new Write(pigeonhole, 1.0));
        hold(0.5);

        // Show collision visually
        ArrayMob collTable = new ArrayMob(" ", " ", " ", "?", " ", " ", " ");
        collTable.setLabel("table[7]");
        collTable.setPosition(0, -150);
        play(new FadeIn(collTable, 0.5));

        TextMob key1 = new TextMob("key=10")
                .setFontSize(22).setFillColor(Colors.GREEN);
        key1.setPosition(-200, -50);

        TextMob key2 = new TextMob("key=31")
                .setFontSize(22).setFillColor(Colors.ORANGE);
        key2.setPosition(200, -50);

        ArrowMob collArrow1 = new ArrowMob(-150, -50, -30, -120);
        collArrow1.setStrokeColor(Colors.GREEN);
        ArrowMob collArrow2 = new ArrowMob(150, -50, 30, -120);
        collArrow2.setStrokeColor(Colors.ORANGE);

        play(new FadeIn(key1, 0.3), new FadeIn(key2, 0.3));
        play(new DrawArrow(collArrow1, 0.4), new DrawArrow(collArrow2, 0.4));

        collTable.setCellColor(3, Colors.withAlpha(Colors.RED, 0.4));
        TextMob bothMap = new TextMob("Both map to index 3!")
                .setFontSize(24).setBold().setFillColor(Colors.RED);
        bothMap.setPosition(0, 20);
        play(new Write(bothMap, 0.6));
        hold(1.0);

        TextMob solutions = new TextMob("Solutions: (1) Chaining  (2) Open Addressing")
                .setFontSize(24).setFillColor(Colors.TEAL);
        solutions.setPosition(0, 80);
        play(new Write(solutions, 0.8));
        hold(2.0);

        play(new FadeOut(collHeading, 0.3), new FadeOut(collDesc, 0.3), new FadeOut(pigeonhole, 0.3),
             new FadeOut(collTable, 0.3), new FadeOut(key1, 0.3), new FadeOut(key2, 0.3),
             new FadeOut(collArrow1, 0.3), new FadeOut(collArrow2, 0.3),
             new FadeOut(bothMap, 0.3), new FadeOut(solutions, 0.3));

        // ===== SECTION 7: Chaining (Separate Chaining) =====
        TextMob chainHeading = new TextMob("Collision Resolution: Chaining")
                .setFontSize(44)
                .setBold()
                .setFillColor(Colors.GREEN);
        chainHeading.setPosition(0, -420);
        play(new Write(chainHeading, 0.8));

        TextMob chainDesc = new TextMob("Each bucket stores a linked list of all entries that hash there")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        chainDesc.setPosition(0, -360);
        play(new Write(chainDesc, 1.0));
        hold(0.5);

        // Show array of buckets with linked lists
        // Bucket labels
        for (int i = 0; i < 7; i++) {
            double yPos = -270 + i * 65;
            RectMob bucket = new RectMob(60, 40);
            bucket.setCornerRadius(4);
            bucket.setFillColor(Colors.withAlpha(Colors.BLUE, 0.2));
            bucket.setStrokeColor(Colors.BLUE);
            bucket.setPosition(-500, yPos);

            TextMob idxLabel = new TextMob("[" + i + "]")
                    .setFontSize(18).setFillColor(Colors.BLUE);
            idxLabel.setPosition(-500, yPos);

            play(new FadeIn(bucket, 0.1), new FadeIn(idxLabel, 0.1));
        }

        // Add chains at certain indices
        // Index 0: 14 -> 21
        LinkedListMob chain0 = new LinkedListMob("14", "21");
        chain0.setPosition(-250, -270);
        play(new FadeIn(chain0, 0.4));

        ArrowMob cArrow0 = new ArrowMob(-470, -270, -350, -270);
        cArrow0.setStrokeColor(Colors.LIGHT_GRAY);
        play(new DrawArrow(cArrow0, 0.2));

        // Index 1: 22
        LinkedListMob chain1 = new LinkedListMob("22");
        chain1.setPosition(-300, -205);
        play(new FadeIn(chain1, 0.3));

        ArrowMob cArrow1 = new ArrowMob(-470, -205, -370, -205);
        cArrow1.setStrokeColor(Colors.LIGHT_GRAY);
        play(new DrawArrow(cArrow1, 0.2));

        // Index 3: 10 -> 31 -> 45
        LinkedListMob chain3 = new LinkedListMob("10", "31", "45");
        chain3.setPosition(-150, -75);
        play(new FadeIn(chain3, 0.5));

        ArrowMob cArrow3 = new ArrowMob(-470, -75, -300, -75);
        cArrow3.setStrokeColor(Colors.LIGHT_GRAY);
        play(new DrawArrow(cArrow3, 0.2));

        // Highlight the chain at index 3
        TextMob chainNote = new TextMob("Bucket 3: three keys collided here -> linked list of length 3")
                .setFontSize(20).setFillColor(Colors.YELLOW);
        chainNote.setPosition(0, 250);
        play(new Write(chainNote, 0.8));

        chain3.highlightNode(0);
        chain3.highlightNode(1);
        chain3.highlightNode(2);
        hold(1.5);

        TextMob searchChain = new TextMob("Search key=31: go to bucket 3, traverse list -> found at 2nd node")
                .setFontSize(20).setFillColor(Colors.TEAL);
        searchChain.setPosition(0, 300);
        play(new Write(searchChain, 1.0));
        hold(2.0);

        play(new FadeOut(chainHeading, 0.3), new FadeOut(chainDesc, 0.3),
             new FadeOut(chain0, 0.3), new FadeOut(chain1, 0.3), new FadeOut(chain3, 0.3),
             new FadeOut(cArrow0, 0.3), new FadeOut(cArrow1, 0.3), new FadeOut(cArrow3, 0.3),
             new FadeOut(chainNote, 0.3), new FadeOut(searchChain, 0.3));
        hold(0.3);

        // ===== SECTION 8: Open Addressing — Linear Probing =====
        TextMob openHeading = new TextMob("Open Addressing: Linear Probing")
                .setFontSize(44)
                .setBold()
                .setFillColor(Colors.ORANGE);
        openHeading.setPosition(0, -420);
        play(new Write(openHeading, 0.8));

        TextMob openDesc = new TextMob("On collision, probe the next slot: h(k,i) = (h(k) + i) % m")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        openDesc.setPosition(0, -360);
        play(new Write(openDesc, 1.0));
        hold(0.5);

        ArrayMob probeTable = new ArrayMob(" ", " ", " ", " ", " ", " ", " ");
        probeTable.setLabel("table[7], h(k) = k % 7");
        probeTable.setPosition(0, -230);
        play(new FadeIn(probeTable, 0.5));

        // Insert 10 -> index 3
        TextMob probeStep = new TextMob("Insert 10: h(10) = 3 -> slot 3 empty, place here")
                .setFontSize(20).setFillColor(Colors.WHITE);
        probeStep.setPosition(0, -140);
        play(new FadeIn(probeStep, 0.2));
        probeTable.setValue(3, "10");
        probeTable.setCellColor(3, Colors.withAlpha(Colors.GREEN, 0.3));
        hold(0.8);
        probeTable.clearCellColors();

        // Insert 22 -> index 1
        probeStep.setText("Insert 22: h(22) = 1 -> slot 1 empty, place here");
        probeTable.setValue(1, "22");
        probeTable.setCellColor(1, Colors.withAlpha(Colors.GREEN, 0.3));
        hold(0.8);
        probeTable.clearCellColors();

        // Insert 31 -> index 3, collision! probe 4
        probeStep.setText("Insert 31: h(31) = 3 -> COLLISION! Try slot 4...");
        play(new ColorChange(probeStep, Colors.WHITE, Colors.RED, 0.3));
        probeTable.setCellColor(3, Colors.withAlpha(Colors.RED, 0.4));
        hold(0.8);

        probeStep.setText("Insert 31: slot 4 empty -> place here");
        play(new ColorChange(probeStep, Colors.RED, Colors.GREEN, 0.3));
        probeTable.clearCellColors();
        probeTable.setValue(4, "31");
        probeTable.setCellColor(4, Colors.withAlpha(Colors.GREEN, 0.3));
        hold(0.8);
        probeTable.clearCellColors();

        // Insert 24 -> index 3, collision! try 4 (full), try 5
        probeStep.setText("Insert 24: h(24) = 3 -> collision! slot 4 full! Try slot 5...");
        play(new ColorChange(probeStep, Colors.GREEN, Colors.RED, 0.3));
        probeTable.setCellColor(3, Colors.withAlpha(Colors.RED, 0.4));
        probeTable.setCellColor(4, Colors.withAlpha(Colors.RED, 0.4));
        hold(0.8);

        probeStep.setText("Insert 24: slot 5 empty -> place here");
        play(new ColorChange(probeStep, Colors.RED, Colors.GREEN, 0.3));
        probeTable.clearCellColors();
        probeTable.setValue(5, "24");
        probeTable.setCellColor(5, Colors.withAlpha(Colors.GREEN, 0.3));
        hold(0.8);
        probeTable.clearCellColors();

        TextMob clusterWarning = new TextMob("Warning: Clustering — consecutive filled slots slow things down")
                .setFontSize(20).setFillColor(Colors.RED);
        clusterWarning.setPosition(0, -80);
        probeTable.setCellColor(3, Colors.withAlpha(Colors.ORANGE, 0.3));
        probeTable.setCellColor(4, Colors.withAlpha(Colors.ORANGE, 0.3));
        probeTable.setCellColor(5, Colors.withAlpha(Colors.ORANGE, 0.3));
        play(new Write(clusterWarning, 0.8));
        hold(2.0);

        play(new FadeOut(openHeading, 0.3), new FadeOut(openDesc, 0.3),
             new FadeOut(probeTable, 0.3), new FadeOut(probeStep, 0.3), new FadeOut(clusterWarning, 0.3));

        // ===== SECTION 9: Load Factor =====
        TextMob lfHeading = new TextMob("Load Factor")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.TEAL);
        lfHeading.setPosition(0, -400);
        play(new Write(lfHeading, 0.8));

        TextMob lfFormula = new TextMob("α = n / m    (n = number of elements, m = table size)")
                .setFontSize(28).setFillColor(Colors.YELLOW);
        lfFormula.setPosition(0, -320);
        play(new Write(lfFormula, 1.0));
        hold(0.5);

        // Low load factor
        RectMob lowBox = new RectMob(380, 100);
        lowBox.setCornerRadius(10);
        lowBox.setFillColor(Colors.withAlpha(Colors.GREEN, 0.12));
        lowBox.setStrokeColor(Colors.GREEN);
        lowBox.setPosition(-300, -200);

        TextMob lowTitle = new TextMob("α < 0.7 (good)")
                .setFontSize(22).setBold().setFillColor(Colors.GREEN);
        lowTitle.setPosition(-300, -235);

        TextMob lowDetail = new TextMob("Few collisions, fast lookups\nPlenty of empty slots")
                .setFontSize(18).setFillColor(Colors.WHITE);
        lowDetail.setPosition(-300, -185);

        play(new FadeIn(lowBox, 0.4), new FadeIn(lowTitle, 0.3));
        play(new Write(lowDetail, 0.6));

        // High load factor
        RectMob highBox = new RectMob(380, 100);
        highBox.setCornerRadius(10);
        highBox.setFillColor(Colors.withAlpha(Colors.RED, 0.12));
        highBox.setStrokeColor(Colors.RED);
        highBox.setPosition(300, -200);

        TextMob highTitle = new TextMob("α > 0.7 (bad)")
                .setFontSize(22).setBold().setFillColor(Colors.RED);
        highTitle.setPosition(300, -235);

        TextMob highDetail = new TextMob("Many collisions, degraded O(n)\nTime to resize (rehash)!")
                .setFontSize(18).setFillColor(Colors.WHITE);
        highDetail.setPosition(300, -185);

        play(new FadeIn(highBox, 0.4), new FadeIn(highTitle, 0.3));
        play(new Write(highDetail, 0.6));
        hold(1.0);

        TextMob resize = new TextMob("Resize: double the table, rehash all entries -> amortized O(1) insert")
                .setFontSize(22).setFillColor(Colors.GOLD);
        resize.setPosition(0, -90);
        play(new Write(resize, 1.0));
        hold(2.0);

        play(new FadeOut(lfHeading, 0.3), new FadeOut(lfFormula, 0.3),
             new FadeOut(lowBox, 0.3), new FadeOut(lowTitle, 0.3), new FadeOut(lowDetail, 0.3),
             new FadeOut(highBox, 0.3), new FadeOut(highTitle, 0.3), new FadeOut(highDetail, 0.3),
             new FadeOut(resize, 0.3));

        // ===== SECTION 10: Hash Function Properties =====
        TextMob propHeading = new TextMob("Good Hash Function Properties")
                .setFontSize(44)
                .setBold()
                .setFillColor(Colors.PURPLE);
        propHeading.setPosition(0, -400);
        play(new Write(propHeading, 0.8));

        String[] properties = {
            "1. Deterministic: same key always gives same hash",
            "2. Uniform distribution: keys spread evenly across buckets",
            "3. Fast to compute: O(1) per hash",
            "4. Minimizes collisions for expected input"
        };
        javafx.scene.paint.Color[] propColors = {Colors.TEAL, Colors.GREEN, Colors.GOLD, Colors.ORANGE};

        for (int i = 0; i < properties.length; i++) {
            TextMob prop = new TextMob(properties[i])
                    .setFontSize(22).setFillColor(propColors[i]);
            prop.setPosition(0, -280 + i * 65);
            play(new Write(prop, 0.8));
            hold(0.3);
        }

        TextMob commonHash = new TextMob("Common: h(k) = k % m  (choose m as prime for better distribution)")
                .setFontSize(22).setFillColor(Colors.YELLOW);
        commonHash.setPosition(0, -10);
        play(new Write(commonHash, 0.8));
        hold(2.0);

        play(new FadeOut(propHeading, 0.3), new FadeOut(commonHash, 0.3));
        hold(0.3);

        // ===== SECTION 11: Code — Hash Table with Chaining =====
        TextMob codeHeading = new TextMob("Hash Table with Chaining in C")
                .setFontSize(40)
                .setBold()
                .setFillColor(Colors.GREEN);
        codeHeading.setPosition(0, -430);
        play(new FadeIn(codeHeading, 0.5));

        CodeBlock htCode = new CodeBlock(
            "#define TABLE_SIZE 7\n" +
            "\n" +
            "struct Node {\n" +
            "    int key;\n" +
            "    struct Node *next;\n" +
            "};\n" +
            "\n" +
            "struct Node *table[TABLE_SIZE];\n" +
            "\n" +
            "int hash(int key) {\n" +
            "    return key % TABLE_SIZE;\n" +
            "}\n" +
            "\n" +
            "void insert(int key) {\n" +
            "    int idx = hash(key);\n" +
            "    struct Node *n = malloc(sizeof(*n));\n" +
            "    n->key = key;\n" +
            "    n->next = table[idx];  // prepend\n" +
            "    table[idx] = n;\n" +
            "}", 16
        );
        htCode.setPosition(-250, -80);
        play(new RevealCode(htCode, 4.0));
        hold(1.0);

        htCode.highlightLine(14);
        htCode.highlightLine(15);
        TextMob insertNote = new TextMob("Insert at head of list -> O(1)")
                .setFontSize(20).setFillColor(Colors.GREEN);
        insertNote.setPosition(400, -50);
        play(new FadeIn(insertNote, 0.3));
        hold(1.5);
        htCode.clearHighlights();

        // Search function
        CodeBlock searchCode = new CodeBlock(
            "struct Node* search(int key) {\n" +
            "    int idx = hash(key);\n" +
            "    struct Node *cur = table[idx];\n" +
            "    while (cur != NULL) {\n" +
            "        if (cur->key == key)\n" +
            "            return cur;\n" +
            "        cur = cur->next;\n" +
            "    }\n" +
            "    return NULL;\n" +
            "}", 16
        );
        searchCode.setPosition(400, 200);
        play(new RevealCode(searchCode, 2.5));
        hold(2.0);

        play(new FadeOut(codeHeading, 0.3), new FadeOut(htCode, 0.3),
             new FadeOut(insertNote, 0.3), new FadeOut(searchCode, 0.3));

        // ===== SECTION 12: Time Complexity =====
        TextMob tcHeading = new TextMob("Time Complexity")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.BLUE);
        tcHeading.setPosition(0, -400);
        play(new Write(tcHeading, 0.8));

        // Average case
        RectMob avgBox = new RectMob(600, 100);
        avgBox.setCornerRadius(10);
        avgBox.setFillColor(Colors.withAlpha(Colors.GREEN, 0.12));
        avgBox.setStrokeColor(Colors.GREEN);
        avgBox.setPosition(0, -260);

        TextMob avgTitle2 = new TextMob("Average Case: O(1)")
                .setFontSize(28).setBold().setFillColor(Colors.GREEN);
        avgTitle2.setPosition(0, -290);

        TextMob avgDetail2 = new TextMob("With good hash function and low load factor, chains are short")
                .setFontSize(20).setFillColor(Colors.WHITE);
        avgDetail2.setPosition(0, -240);

        play(new FadeIn(avgBox, 0.4), new FadeIn(avgTitle2, 0.3));
        play(new Write(avgDetail2, 0.8));
        hold(0.8);

        // Worst case
        RectMob worstBox = new RectMob(600, 100);
        worstBox.setCornerRadius(10);
        worstBox.setFillColor(Colors.withAlpha(Colors.RED, 0.12));
        worstBox.setStrokeColor(Colors.RED);
        worstBox.setPosition(0, -120);

        TextMob worstTitle2 = new TextMob("Worst Case: O(n)")
                .setFontSize(28).setBold().setFillColor(Colors.RED);
        worstTitle2.setPosition(0, -150);

        TextMob worstDetail2 = new TextMob("All n keys hash to same bucket -> single linked list of length n")
                .setFontSize(20).setFillColor(Colors.WHITE);
        worstDetail2.setPosition(0, -100);

        play(new FadeIn(worstBox, 0.4), new FadeIn(worstTitle2, 0.3));
        play(new Write(worstDetail2, 0.8));
        hold(1.0);

        // Visual: worst case — all in one bucket
        LinkedListMob worstChain = new LinkedListMob("a", "b", "c", "d", "e");
        worstChain.setPosition(0, 20);

        TextMob worstVis = new TextMob("Worst case: everything in bucket 0")
                .setFontSize(20).setFillColor(Colors.ORANGE);
        worstVis.setPosition(0, 80);

        play(new FadeIn(worstChain, 0.5), new FadeIn(worstVis, 0.3));
        hold(2.0);

        play(new FadeOut(tcHeading, 0.3),
             new FadeOut(avgBox, 0.3), new FadeOut(avgTitle2, 0.3), new FadeOut(avgDetail2, 0.3),
             new FadeOut(worstBox, 0.3), new FadeOut(worstTitle2, 0.3), new FadeOut(worstDetail2, 0.3),
             new FadeOut(worstChain, 0.3), new FadeOut(worstVis, 0.3));

        // ===== SECTION 13: Real-World Uses =====
        TextMob rwHeading = new TextMob("Real-World Applications")
                .setFontSize(48)
                .setBold()
                .setFillColor(Colors.GOLD);
        rwHeading.setPosition(0, -400);
        play(new Write(rwHeading, 0.8));

        String[][] uses = {
            {"Compilers", "Symbol table: variable name -> type, scope, address"},
            {"Databases", "Index structures for fast record lookup by key"},
            {"Caching", "Web caches, DNS lookup, memoization tables"},
            {"Networking", "Routing tables, MAC address tables"},
            {"Languages", "Python dict, Java HashMap, C++ unordered_map"}
        };
        javafx.scene.paint.Color[] useColors = {Colors.TEAL, Colors.BLUE, Colors.GREEN, Colors.PURPLE, Colors.ORANGE};

        for (int i = 0; i < uses.length; i++) {
            double yPos = -280 + i * 75;

            TextMob useTitle = new TextMob(uses[i][0])
                    .setFontSize(24).setBold().setFillColor(useColors[i]);
            useTitle.setPosition(-400, yPos);

            TextMob useDesc = new TextMob(uses[i][1])
                    .setFontSize(20).setFillColor(Colors.WHITE);
            useDesc.setPosition(100, yPos);

            play(new FadeIn(useTitle, 0.2), new Write(useDesc, 0.6));
            hold(0.3);
        }
        hold(2.5);

        play(new FadeOut(rwHeading, 0.3));
        hold(0.3);

        // ===== SECTION 14: Summary =====
        TextMob sumTitle = new TextMob("Summary")
                .setFontSize(52)
                .setBold()
                .setFillColor(Colors.GOLD);
        sumTitle.setPosition(0, -300);
        play(new FadeIn(sumTitle, 0.5));

        String[] sumPoints = {
            "Hash tables map keys to indices via a hash function",
            "Collisions are inevitable — handle with chaining or probing",
            "Average case O(1) for insert, search, delete",
            "Load factor α controls performance — resize when α > 0.7",
            "Choose hash function for uniform distribution",
            "Foundation of dictionaries in every modern language"
        };

        for (int i = 0; i < sumPoints.length; i++) {
            TextMob pt = new TextMob("• " + sumPoints[i])
                    .setFontSize(23).setFillColor(Colors.WHITE);
            pt.setPosition(0, -200 + i * 55);
            play(new Write(pt, 0.8));
            hold(0.3);
        }
        hold(3.0);

        play(new FadeOut(sumTitle, 1.0));
    }
}
