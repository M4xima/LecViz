package com.lecviz.generator;

import com.lecviz.animations.*;
import com.lecviz.core.Scene;
import com.lecviz.mobjects.*;
import com.lecviz.parser.PPTXParser;
import com.lecviz.parser.PPTXParser.ParsedSlide;
import com.lecviz.utils.Colors;

import java.util.List;

/**
 * Automatically generates a 3Blue1Brown-style animated scene from parsed PPTX slides.
 *
 * Pipeline:
 *   PPTX file -> PPTXParser -> List<ParsedSlide> -> AutoSceneBuilder -> Scene -> MP4
 *
 * For each slide, it:
 *   1. Creates an animated title card
 *   2. Reveals bullet points one by one with animations
 *   3. Shows code blocks with line-by-line reveal
 *   4. Detects data structure topics and creates interactive visualizations
 *   5. Adds smooth transitions between slides
 */
public class AutoSceneBuilder {

    private final List<ParsedSlide> slides;
    private final String lectureName;

    // Layout constants
    private static final double TITLE_Y = -400;
    private static final double CONTENT_START_Y = -280;
    private static final double BULLET_SPACING = 55;
    private static final double CODE_X = -200;
    private static final double CODE_Y = -100;
    private static final int MAX_BULLETS_PER_SCREEN = 8;

    // 3B1B color cycle for variety
    private static final javafx.scene.paint.Color[] ACCENT_COLORS = {
        Colors.TEAL, Colors.BLUE, Colors.PURPLE, Colors.GREEN,
        Colors.GOLD, Colors.ORANGE, Colors.PINK, Colors.LIGHT_BLUE
    };

    public AutoSceneBuilder(List<ParsedSlide> slides, String lectureName) {
        this.slides = slides;
        this.lectureName = lectureName;
    }

    /**
     * Build a complete Scene from the parsed slides.
     */
    public Scene build() {
        return new GeneratedScene(slides, lectureName);
    }

    /**
     * The generated scene — a Scene subclass that animates all slides.
     */
    private static class GeneratedScene extends Scene {
        private final List<ParsedSlide> slides;
        private final String lectureName;

        GeneratedScene(List<ParsedSlide> slides, String lectureName) {
            this.slides = slides;
            this.lectureName = lectureName;
        }

        @Override
        public void construct() {
            // Intro title card
            buildIntroCard();

            // Process each slide
            for (int i = 0; i < slides.size(); i++) {
                ParsedSlide slide = slides.get(i);
                javafx.scene.paint.Color accent = ACCENT_COLORS[i % ACCENT_COLORS.length];

                switch (slide.type) {
                    case TITLE_SLIDE -> buildTitleSlide(slide, accent);
                    case CODE_SLIDE -> buildCodeSlide(slide, accent);
                    case MIXED -> buildMixedSlide(slide, accent);
                    case DIAGRAM -> buildDiagramSlide(slide, accent);
                    default -> buildContentSlide(slide, accent);
                }

                // Detect and show data structure visualizations
                List<String> dsFound = PPTXParser.detectDataStructures(slide);
                if (!dsFound.isEmpty()) {
                    buildDataStructureViz(slide, dsFound, accent);
                }

                // Transition between slides
                if (i < slides.size() - 1) {
                    hold(0.3);
                }
            }

            // Outro
            buildOutroCard();
        }

        // ===== Intro =====
        private void buildIntroCard() {
            TextMob title = new TextMob(lectureName)
                    .setFontSize(52).setBold().setFillColor(Colors.TEAL);
            title.setPosition(0, -80);

            TextMob subtitle = new TextMob("Programming and Data Structures")
                    .setFontSize(28).setFillColor(Colors.LIGHT_GRAY);
            subtitle.setPosition(0, 0);

            TextMob credit = new TextMob("Prof. Rupesh Nasre — IIT Madras")
                    .setFontSize(22).setFillColor(Colors.GRAY);
            credit.setPosition(0, 60);

            // Decorative line
            RectMob line = new RectMob(400, 3);
            line.setFillColor(Colors.TEAL);
            line.setPosition(0, 30);

            play(new FadeIn(title, 1.2));
            play(new FadeIn(line, 0.5));
            play(new Write(subtitle, 0.8));
            play(new FadeIn(credit, 0.5));
            hold(2.5);

            play(new FadeOut(title, 0.5), new FadeOut(subtitle, 0.5),
                 new FadeOut(credit, 0.5), new FadeOut(line, 0.5));
            hold(0.3);
        }

        // ===== Title Slide =====
        private void buildTitleSlide(ParsedSlide slide, javafx.scene.paint.Color accent) {
            // Section divider style
            RectMob bg = new RectMob(800, 200);
            bg.setCornerRadius(20);
            bg.setFillColor(Colors.withAlpha(accent, 0.1));
            bg.setStrokeColor(Colors.withAlpha(accent, 0.4));
            bg.setPosition(0, -40);

            TextMob title = new TextMob(slide.title)
                    .setFontSize(44).setBold().setFillColor(accent);
            title.setPosition(0, -60);

            play(new FadeIn(bg, 0.5));
            play(new Write(title, 1.0));

            if (!slide.subtitle.isEmpty()) {
                TextMob sub = new TextMob(slide.subtitle)
                        .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
                sub.setPosition(0, 0);
                play(new Write(sub, 0.6));
                hold(2.0);
                play(new FadeOut(sub, 0.3));
            } else {
                hold(2.0);
            }

            play(new FadeOut(title, 0.4), new FadeOut(bg, 0.4));
        }

        // ===== Content Slide (bullets) =====
        private void buildContentSlide(ParsedSlide slide, javafx.scene.paint.Color accent) {
            // Heading
            TextMob heading = new TextMob(slide.title)
                    .setFontSize(40).setBold().setFillColor(accent);
            heading.setPosition(0, TITLE_Y);
            play(new Write(heading, 0.7));

            // Reveal bullets one by one with staggered animation
            TextMob[] bulletMobs = new TextMob[slide.bullets.size()];
            CircleMob[] dots = new CircleMob[slide.bullets.size()];

            int bulletCount = Math.min(slide.bullets.size(), MAX_BULLETS_PER_SCREEN);
            for (int i = 0; i < bulletCount; i++) {
                double y = CONTENT_START_Y + i * BULLET_SPACING;

                // Bullet dot
                dots[i] = new CircleMob(5);
                dots[i].setFillColor(accent);
                dots[i].setPosition(-620, y);

                // Bullet text
                String text = slide.bullets.get(i);
                if (text.length() > 80) text = text.substring(0, 77) + "...";

                bulletMobs[i] = new TextMob(text)
                        .setFontSize(22).setFillColor(Colors.WHITE);
                bulletMobs[i].setPosition(-20, y);
                bulletMobs[i].setAlignment(javafx.scene.text.TextAlignment.LEFT);

                play(new FadeIn(dots[i], 0.15), new Write(bulletMobs[i], 0.5));
                hold(0.4);
            }

            // If there are code blocks in this slide too
            if (!slide.codeBlocks.isEmpty()) {
                buildInlineCode(slide.codeBlocks.get(0), accent);
            }

            hold(1.5);

            // Fade everything out
            play(new FadeOut(heading, 0.3));
            for (int i = 0; i < bulletCount; i++) {
                if (bulletMobs[i] != null) remove(bulletMobs[i]);
                if (dots[i] != null) remove(dots[i]);
            }
            clear();
        }

        // ===== Code Slide =====
        private void buildCodeSlide(ParsedSlide slide, javafx.scene.paint.Color accent) {
            TextMob heading = new TextMob(slide.title)
                    .setFontSize(40).setBold().setFillColor(accent);
            heading.setPosition(0, TITLE_Y);
            play(new Write(heading, 0.7));

            for (String code : slide.codeBlocks) {
                buildInlineCode(code, accent);
            }

            hold(1.5);
            play(new FadeOut(heading, 0.3));
            clear();
        }

        // ===== Mixed Slide (code + bullets) =====
        private void buildMixedSlide(ParsedSlide slide, javafx.scene.paint.Color accent) {
            TextMob heading = new TextMob(slide.title)
                    .setFontSize(40).setBold().setFillColor(accent);
            heading.setPosition(0, TITLE_Y);
            play(new Write(heading, 0.7));

            // Bullets on the left
            int bulletCount = Math.min(slide.bullets.size(), 5);
            for (int i = 0; i < bulletCount; i++) {
                double y = CONTENT_START_Y + i * BULLET_SPACING;
                String text = slide.bullets.get(i);
                if (text.length() > 45) text = text.substring(0, 42) + "...";

                TextMob bullet = new TextMob("• " + text)
                        .setFontSize(20).setFillColor(Colors.WHITE);
                bullet.setPosition(-450, y);
                bullet.setAlignment(javafx.scene.text.TextAlignment.LEFT);

                play(new Write(bullet, 0.4));
                hold(0.2);
            }

            // Code on the right
            if (!slide.codeBlocks.isEmpty()) {
                String code = slide.codeBlocks.get(0);
                CodeBlock codeBlock = new CodeBlock(code, 18);
                codeBlock.setPosition(250, -50);
                play(new RevealCode(codeBlock, Math.min(code.split("\n").length * 0.4, 4.0)));
                hold(1.0);
            }

            hold(1.5);
            play(new FadeOut(heading, 0.3));
            clear();
        }

        // ===== Diagram Slide =====
        private void buildDiagramSlide(ParsedSlide slide, javafx.scene.paint.Color accent) {
            // For now, treat like a title + description since we can't render images
            TextMob heading = new TextMob(slide.title)
                    .setFontSize(40).setBold().setFillColor(accent);
            heading.setPosition(0, TITLE_Y);
            play(new Write(heading, 0.7));

            TextMob note = new TextMob("[Diagram: " + slide.title + "]")
                    .setFontSize(24).setFillColor(Colors.GRAY);
            note.setPosition(0, -200);
            play(new FadeIn(note, 0.5));

            // Show any available text
            if (!slide.bullets.isEmpty()) {
                for (int i = 0; i < Math.min(slide.bullets.size(), 4); i++) {
                    TextMob b = new TextMob(slide.bullets.get(i))
                            .setFontSize(22).setFillColor(Colors.WHITE);
                    b.setPosition(0, -100 + i * 50);
                    play(new Write(b, 0.5));
                }
            }

            hold(2.0);
            play(new FadeOut(heading, 0.3));
            clear();
        }

        // ===== Data Structure Visualization =====
        private void buildDataStructureViz(ParsedSlide slide, List<String> dsTypes,
                                            javafx.scene.paint.Color accent) {
            TextMob vizTitle = new TextMob("Visualization: " + String.join(", ", dsTypes))
                    .setFontSize(32).setBold().setFillColor(Colors.GOLD);
            vizTitle.setPosition(0, TITLE_Y);
            play(new Write(vizTitle, 0.6));

            for (String ds : dsTypes) {
                switch (ds) {
                    case "Array" -> buildArrayViz(accent);
                    case "LinkedList" -> buildLinkedListViz(accent);
                    case "Stack" -> buildStackViz(accent);
                    case "Queue" -> buildQueueViz(accent);
                    case "Tree" -> buildTreeViz(accent);
                    case "Heap" -> buildHeapViz(accent);
                    case "Graph" -> buildGraphViz(accent);
                    case "Hash" -> buildHashViz(accent);
                    case "Sort" -> buildSortViz(accent);
                    case "Search" -> buildSearchViz(accent);
                }
            }

            hold(1.0);
            play(new FadeOut(vizTitle, 0.3));
            clear();
        }

        // --- Array visualization ---
        private void buildArrayViz(javafx.scene.paint.Color accent) {
            ArrayMob arr = new ArrayMob("10", "25", "3", "47", "8", "15", "31");
            arr.setLabel("Example Array");
            arr.setPosition(0, -150);
            play(new FadeIn(arr, 0.6));

            // Highlight elements one by one
            for (int i = 0; i < 7; i++) {
                arr.highlight(i);
                arr.setPointer(i, "i=" + i);
                hold(0.3);
                arr.unhighlight(i);
            }
            arr.hidePointer();
            hold(1.0);
            play(new FadeOut(arr, 0.4));
        }

        // --- LinkedList visualization ---
        private void buildLinkedListViz(javafx.scene.paint.Color accent) {
            LinkedListMob list = new LinkedListMob();
            list.setPosition(-200, -100);
            String[] vals = {"10", "20", "30", "40"};
            for (String v : vals) list.addNode(v);
            play(new FadeIn(list, 0.6));

            // Highlight traversal
            for (int i = 0; i < list.getSize(); i++) {
                list.highlightNode(i);
                hold(0.5);
                list.unhighlightNode(i);
            }

            // Insert demonstration
            TextMob insertMsg = new TextMob("Inserting 25 at index 2")
                    .setFontSize(22).setFillColor(Colors.YELLOW);
            insertMsg.setPosition(0, 100);
            play(new Write(insertMsg, 0.5));
            list.insertNode(2, "25");
            hold(1.5);
            play(new FadeOut(insertMsg, 0.3));
            play(new FadeOut(list, 0.4));
        }

        // --- Stack visualization ---
        private void buildStackViz(javafx.scene.paint.Color accent) {
            StackMob stack = new StackMob();
            stack.setLabel("Stack (LIFO)");
            stack.setPosition(-200, 100);
            play(new FadeIn(stack, 0.4));

            String[] pushVals = {"10", "20", "30", "40"};
            for (String v : pushVals) {
                TextMob pushMsg = new TextMob("push(" + v + ")")
                        .setFontSize(22).setFillColor(Colors.GREEN);
                pushMsg.setPosition(200, -200);
                play(new Write(pushMsg, 0.3));
                stack.push(v);
                hold(0.5);
                remove(pushMsg);
            }

            // Pop
            for (int i = 0; i < 2; i++) {
                String val = stack.peek();
                TextMob popMsg = new TextMob("pop() -> " + val)
                        .setFontSize(22).setFillColor(Colors.RED);
                popMsg.setPosition(200, -200);
                play(new Write(popMsg, 0.3));
                stack.pop();
                hold(0.5);
                remove(popMsg);
            }

            hold(1.0);
            play(new FadeOut(stack, 0.4));
        }

        // --- Queue visualization ---
        private void buildQueueViz(javafx.scene.paint.Color accent) {
            ArrayMob queue = new ArrayMob("A", "B", "C", "D", "E");
            queue.setLabel("Queue (FIFO)");
            queue.setPosition(0, -100);
            play(new FadeIn(queue, 0.5));

            queue.setPointer(0, "front");
            hold(0.5);
            queue.setPointer(4, "rear");
            hold(1.5);
            queue.hidePointer();
            play(new FadeOut(queue, 0.4));
        }

        // --- Tree visualization ---
        private void buildTreeViz(javafx.scene.paint.Color accent) {
            TreeMob tree = new TreeMob("50", "30", "70", "20", "40", "60", "80");
            tree.setPosition(0, -120);
            play(new FadeIn(tree, 0.8));

            // Inorder traversal animation
            TextMob traversalLabel = new TextMob("Inorder Traversal")
                    .setFontSize(24).setBold().setFillColor(Colors.TEAL);
            traversalLabel.setPosition(0, 200);
            play(new Write(traversalLabel, 0.5));

            int[] inorder = {3, 1, 4, 0, 5, 2, 6};
            for (int idx : inorder) {
                tree.highlightNode(idx);
                hold(0.4);
                tree.clearHighlight();
            }

            hold(1.0);
            play(new FadeOut(tree, 0.4), new FadeOut(traversalLabel, 0.3));
        }

        // --- Heap visualization ---
        private void buildHeapViz(javafx.scene.paint.Color accent) {
            TreeMob heap = new TreeMob("1", "3", "5", "9", "6", "8", "12");
            heap.setPosition(0, -120);

            TextMob heapLabel = new TextMob("Min-Heap Property: parent <= children")
                    .setFontSize(22).setFillColor(Colors.GOLD);
            heapLabel.setPosition(0, 200);

            play(new FadeIn(heap, 0.8));
            play(new Write(heapLabel, 0.6));

            // Highlight parent-child relationships
            heap.setNodeColor(0, Colors.withAlpha(Colors.GREEN, 0.5));
            hold(0.5);
            heap.setNodeColor(1, Colors.withAlpha(Colors.TEAL, 0.5));
            heap.setNodeColor(2, Colors.withAlpha(Colors.TEAL, 0.5));
            hold(1.5);

            play(new FadeOut(heap, 0.4), new FadeOut(heapLabel, 0.3));
        }

        // --- Graph visualization ---
        private void buildGraphViz(javafx.scene.paint.Color accent) {
            // Create a simple graph using circles and arrows
            int numNodes = 5;
            String[] labels = {"A", "B", "C", "D", "E"};
            double[] xs = {0, -200, 200, -150, 150};
            double[] ys = {-200, -50, -50, 120, 120};

            CircleMob[] nodes = new CircleMob[numNodes];
            TextMob[] nodeLabels = new TextMob[numNodes];

            for (int i = 0; i < numNodes; i++) {
                nodes[i] = new CircleMob(30);
                nodes[i].setFillColor(Colors.withAlpha(Colors.BLUE, 0.3));
                nodes[i].setStrokeColor(Colors.BLUE);
                nodes[i].setPosition(xs[i], ys[i]);

                nodeLabels[i] = new TextMob(labels[i])
                        .setFontSize(20).setBold().setFillColor(Colors.WHITE);
                nodeLabels[i].setPosition(xs[i], ys[i]);

                play(new FadeIn(nodes[i], 0.2), new FadeIn(nodeLabels[i], 0.2));
            }

            // Draw edges
            int[][] edges = {{0,1}, {0,2}, {1,3}, {2,4}, {1,2}};
            ArrowMob[] edgeMobs = new ArrowMob[edges.length];
            for (int i = 0; i < edges.length; i++) {
                int from = edges[i][0], to = edges[i][1];
                edgeMobs[i] = new ArrowMob(xs[from], ys[from], xs[to], ys[to]);
                edgeMobs[i].setStrokeColor(Colors.GRAY);
                play(new DrawArrow(edgeMobs[i], 0.3));
            }

            // BFS animation
            TextMob bfsLabel = new TextMob("BFS Traversal from A")
                    .setFontSize(24).setBold().setFillColor(Colors.GREEN);
            bfsLabel.setPosition(0, 250);
            play(new Write(bfsLabel, 0.5));

            int[] bfsOrder = {0, 1, 2, 3, 4};
            for (int idx : bfsOrder) {
                nodes[idx].setFillColor(Colors.withAlpha(Colors.GREEN, 0.5));
                hold(0.5);
            }

            hold(1.0);
            play(new FadeOut(bfsLabel, 0.3));
            for (int i = 0; i < numNodes; i++) {
                remove(nodes[i]);
                remove(nodeLabels[i]);
            }
            for (ArrowMob e : edgeMobs) remove(e);
        }

        // --- Hash visualization ---
        private void buildHashViz(javafx.scene.paint.Color accent) {
            ArrayMob hashTable = new ArrayMob("", "", "", "", "", "", "", "");
            hashTable.setLabel("Hash Table (size=8)");
            hashTable.setPosition(0, -100);
            play(new FadeIn(hashTable, 0.5));

            // Insert some values
            int[] keys = {15, 7, 23, 31};
            for (int key : keys) {
                int slot = key % 8;
                TextMob insertMsg = new TextMob("h(" + key + ") = " + key + " % 8 = " + slot)
                        .setFontSize(22).setFillColor(Colors.YELLOW);
                insertMsg.setPosition(0, 50);
                play(new Write(insertMsg, 0.4));
                hashTable.setValue(slot, String.valueOf(key));
                hashTable.setCellColor(slot, Colors.withAlpha(Colors.GREEN, 0.3));
                hold(0.8);
                remove(insertMsg);
                hashTable.clearCellColors();
            }

            hold(1.0);
            play(new FadeOut(hashTable, 0.4));
        }

        // --- Sort visualization ---
        private void buildSortViz(javafx.scene.paint.Color accent) {
            ArrayMob arr = new ArrayMob("64", "25", "12", "22", "11");
            arr.setLabel("Sorting Demo");
            arr.setPosition(0, -100);
            play(new FadeIn(arr, 0.5));

            // Simple selection sort animation
            int[] vals = {64, 25, 12, 22, 11};
            for (int i = 0; i < vals.length - 1; i++) {
                int minIdx = i;
                arr.highlight(i);
                for (int j = i + 1; j < vals.length; j++) {
                    arr.highlight(j);
                    hold(0.2);
                    if (vals[j] < vals[minIdx]) minIdx = j;
                    arr.unhighlight(j);
                }
                if (minIdx != i) {
                    arr.swap(i, minIdx);
                    int tmp = vals[i]; vals[i] = vals[minIdx]; vals[minIdx] = tmp;
                    hold(0.3);
                }
                arr.unhighlight(i);
                arr.setCellColor(i, Colors.withAlpha(Colors.GREEN, 0.3));
            }
            arr.setCellColor(vals.length - 1, Colors.withAlpha(Colors.GREEN, 0.3));

            hold(1.5);
            play(new FadeOut(arr, 0.4));
        }

        // --- Search visualization ---
        private void buildSearchViz(javafx.scene.paint.Color accent) {
            ArrayMob arr = new ArrayMob("5", "12", "18", "25", "31", "42", "56");
            arr.setLabel("Binary Search for 25");
            arr.setPosition(0, -100);
            play(new FadeIn(arr, 0.5));

            // Binary search animation
            int lo = 0, hi = 6, target = 25;
            int[] vals = {5, 12, 18, 25, 31, 42, 56};

            while (lo <= hi) {
                int mid = (lo + hi) / 2;
                arr.setPointer(lo, "lo");
                hold(0.2);
                arr.setPointer(hi, "hi");
                hold(0.2);
                arr.highlight(mid);

                TextMob checkMsg = new TextMob("mid=" + mid + ", arr[mid]=" + vals[mid])
                        .setFontSize(22).setFillColor(Colors.YELLOW);
                checkMsg.setPosition(0, 50);
                play(new Write(checkMsg, 0.3));
                hold(0.5);
                remove(checkMsg);

                if (vals[mid] == target) {
                    arr.setCellColor(mid, Colors.withAlpha(Colors.GREEN, 0.5));
                    TextMob foundMsg = new TextMob("Found 25 at index " + mid + "!")
                            .setFontSize(26).setBold().setFillColor(Colors.GREEN);
                    foundMsg.setPosition(0, 100);
                    play(new Write(foundMsg, 0.5));
                    hold(1.5);
                    remove(foundMsg);
                    break;
                } else if (vals[mid] < target) {
                    lo = mid + 1;
                } else {
                    hi = mid - 1;
                }
                arr.unhighlight(mid);
            }

            arr.hidePointer();
            play(new FadeOut(arr, 0.4));
        }

        // --- Inline code block ---
        private void buildInlineCode(String code, javafx.scene.paint.Color accent) {
            // Truncate very long code
            String[] lines = code.split("\n");
            if (lines.length > 15) {
                StringBuilder truncated = new StringBuilder();
                for (int i = 0; i < 14; i++) truncated.append(lines[i]).append("\n");
                truncated.append("// ... (" + (lines.length - 14) + " more lines)");
                code = truncated.toString();
            }

            CodeBlock codeBlock = new CodeBlock(code, 18);
            codeBlock.setPosition(CODE_X, CODE_Y);
            play(new RevealCode(codeBlock, Math.min(lines.length * 0.3, 4.0)));
            hold(2.0);
            play(new FadeOut(codeBlock, 0.4));
        }

        // ===== Outro =====
        private void buildOutroCard() {
            TextMob thanks = new TextMob("End of Lecture")
                    .setFontSize(48).setBold().setFillColor(Colors.TEAL);
            thanks.setPosition(0, -60);

            TextMob credit = new TextMob("Generated by LecViz — IIT Madras CS5013")
                    .setFontSize(22).setFillColor(Colors.GRAY);
            credit.setPosition(0, 20);

            play(new FadeIn(thanks, 1.0));
            play(new FadeIn(credit, 0.5));
            hold(3.0);
            play(new FadeOut(thanks, 0.5), new FadeOut(credit, 0.5));
        }
    }
}
