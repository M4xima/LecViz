package com.lecviz;

import com.lecviz.core.Scene;
import com.lecviz.generator.AutoSceneBuilder;
import com.lecviz.parser.PPTXParser;
import com.lecviz.parser.PPTXParser.ParsedSlide;
import com.lecviz.scenes.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * LecViz — 3Blue1Brown-style lecture video generator.
 *
 * NEW: Auto-generate from PPTX slides!
 *
 * Usage:
 *   # Auto-generate from a PowerPoint file:
 *   mvn clean compile exec:java -Dexec.mainClass="com.lecviz.LecVizApp" -Dexec.args="slides lecture.pptx"
 *
 *   # Auto-generate from a folder of PPTX files:
 *   mvn clean compile exec:java -Dexec.mainClass="com.lecviz.LecVizApp" -Dexec.args="slides-dir path/to/slides/"
 *
 *   # Render a specific hardcoded scene:
 *   mvn clean compile exec:java -Dexec.mainClass="com.lecviz.LecVizApp" -Dexec.args="arrays"
 *
 *   # Render all hardcoded scenes:
 *   mvn clean compile exec:java -Dexec.mainClass="com.lecviz.LecVizApp"
 *
 * Available hardcoded scenes:
 *   intro, complexity, arrays, linkedlist, stack, tree, dictionary, priorityq, graph
 */
public class LecVizApp extends Application {

    private static final Map<String, Supplier<Scene>> SCENES = new LinkedHashMap<>();
    static {
        SCENES.put("intro",      PDSIntroScene::new);
        SCENES.put("complexity", PDSComplexityScene::new);
        SCENES.put("arrays",     PDSArrayScene::new);
        SCENES.put("linkedlist", PDSLinkedListScene::new);
        SCENES.put("stack",      PDSStackScene::new);
        SCENES.put("tree",       PDSTreeScene::new);
        SCENES.put("dictionary", PDSDictionaryScene::new);
        SCENES.put("priorityq",  PDSPriorityQueueScene::new);
        SCENES.put("graph",      PDSGraphScene::new);
        SCENES.put("arraysv2",  PDSArraySceneV2::new);
        SCENES.put("arraysv3",  PDSArraySceneV3::new);
        SCENES.put("array_3to4", PDSArrayExpressionsScene::new);
        SCENES.put("array_5", PDSSearchScene::new);
        SCENES.put("array_7to9", PDSSortedMatrixSearchScene::new);
        SCENES.put("array_10to12", PDSClassworkScene::new);
        SCENES.put("array_13", PDSQueensScene::new);
        SCENES.put("array_14to15", PDSSortingScene::new);
        SCENES.put("array_16to17", PDSBubbleSortScene::new);
        SCENES.put("array_18to19", PDSInsertionSortScene::new);
        SCENES.put("array_20to21", PDSShellSortScene::new);
        SCENES.put("array_22", PDSSelectionSortScene::new);
        SCENES.put("array_23", PDSHeapSortScene::new);
        SCENES.put("array_24", PDSQuickSortScene::new);
        SCENES.put("array_25", PDSMergeSortScene::new);
        SCENES.put("array_27", PDSBucketSortScene::new);
        SCENES.put("array_28", PDSCountingSortScene::new);
        SCENES.put("array_29", PDSRadixSortScene::new);
        SCENES.put("array_6", PDSMatricesScene::new);
        SCENES.put("array_30to31", PDSWrapUpScene::new);
        SCENES.put("array_1to3", PDSArrayIntroScene::new);
        SCENES.put("list_1to4", PDSListIntroScene::new);
        SCENES.put("list_5to6", PDSListArrayScene::new);
        SCENES.put("list_7to8", PDSListLinkedScene::new);
        SCENES.put("list_10", PDSListInsertScene::new);
        SCENES.put("list_13to14", PDSListTraverseScene::new);
        SCENES.put("list_15", PDSListRemoveScene::new);
        SCENES.put("list_16", PDSListPitfallsScene::new);
        SCENES.put("list_17to18", PDSListDoublyScene::new);
        SCENES.put("list_19to20", PDSListPolynomialScene::new);
        SCENES.put("list_21", PDSListReverseScene::new);
        SCENES.put("list_22", PDSListRecursionScene::new);
        SCENES.put("list_23to24", PDSListStackScene::new);
        SCENES.put("list_25", PDSListStackImplScene::new);
        SCENES.put("list_26to27", PDSListParenScene::new);
        SCENES.put("list_29to30", PDSListExprScene::new);
        SCENES.put("list_31to32", PDSListFormsTableScene::new);
        SCENES.put("list_33to34", PDSListEvalScene::new);
        SCENES.put("list_35to36", PDSListInfixScene::new);
        SCENES.put("list_37", PDSListRecursiveFormScene::new);
        SCENES.put("list_38to39", PDSListQueueScene::new);
        SCENES.put("list_40to41", PDSListCallCenterScene::new);
        SCENES.put("list_42", PDSListSimScene::new);
        SCENES.put("list_43to44", PDSListQueueArrayScene::new);
        SCENES.put("list_45to46", PDSListQueueFullScene::new);
        SCENES.put("list_47", PDSListPracticeScene::new);
        SCENES.put("pixel_18", () -> new PDSInsertionPixelScene(1));
        SCENES.put("pixel_19a", () -> new PDSInsertionPixelScene(2));
        SCENES.put("pixel_19b", () -> new PDSInsertionPixelScene(3));
        SCENES.put("list_48", PDSListOutcomesScene::new);
        SCENES.put("tree_1to2", PDSTreeIntroScene::new);
        SCENES.put("tree_3", PDSTreeNomenScene::new);
        SCENES.put("tree_4", PDSTreeDefinitionScene::new);
        SCENES.put("tree_5", PDSTreeRolesScene::new);
        SCENES.put("tree_6", PDSTreePropertiesScene::new);
        SCENES.put("tree_7", PDSTreeTermsScene::new);
        SCENES.put("tree_8", PDSTreeExercisesScene::new);
        SCENES.put("tree_9", PDSTreeOutcomesScene::new);
        SCENES.put("tree_10", PDSTreeImplScene::new);
        SCENES.put("tree_11", PDSTreeDirScene::new);
        SCENES.put("tree_13", PDSTreeTraversalsScene::new);
        SCENES.put("tree_14a", PDSTreePreorderRecScene::new);
        SCENES.put("tree_14b", PDSTreePreorderIterScene::new);
        SCENES.put("tree_14c", PDSTreeIndentScene::new);
        SCENES.put("tree_15", PDSTreeSizeScene::new);
        SCENES.put("tree_16", PDSTreePostorderScene::new);
        SCENES.put("tree_17", PDSTreeStoryScene::new);
        SCENES.put("tree_18to19", PDSTreeKaryScene::new);
        SCENES.put("tree_20a", () -> new PDSTreeBinPropsScene(1));
        SCENES.put("tree_20b", () -> new PDSTreeBinPropsScene(2));
        SCENES.put("tree_21", PDSTreeExprScene::new);
        SCENES.put("tree_22", () -> new PDSTreeWalkScene(true));
        SCENES.put("tree_23", () -> new PDSTreeWalkScene(false));
        SCENES.put("tree_24to25", PDSTreeFormsScene::new);
        SCENES.put("tree_26", PDSTreeEvalScene::new);
        SCENES.put("tree_27", PDSTreePostfixTreeScene::new);
        SCENES.put("tree_28", PDSTreeOpsScene::new);
        SCENES.put("tree_29", PDSTreeQuestionsScene::new);
        SCENES.put("tree_30", PDSTreeCodingScene::new);
        SCENES.put("tree_31", PDSTreeCodeTreeScene::new);
        SCENES.put("tree_32to33", PDSTreeFreqScene::new);
        SCENES.put("tree_34", PDSTreeShorterScene::new);
        SCENES.put("tree_35", PDSTreePrefixScene::new);
        SCENES.put("tree_36", PDSTreeEndScene::new);
        SCENES.put("pq_1to2", PDSPQIntroScene::new);
        SCENES.put("pq_3", PDSPQDefinitionScene::new);
        SCENES.put("pq_4", PDSPQHeapScene::new);
        SCENES.put("pq_5to6", PDSPQHeapExampleScene::new);
        SCENES.put("pq_7", PDSPQInsertScene::new);
        SCENES.put("pq_8", PDSPQDeleteScene::new);
        SCENES.put("pq_9to10", PDSPQClassworkScene::new);
        SCENES.put("pq_11", PDSPQDeleteSeriesScene::new);
        SCENES.put("pq_12a", PDSPQKeysScene::new);
        SCENES.put("pq_12b", PDSPQBuildScene::new);
        SCENES.put("pq_13", PDSPQAppsScene::new);
        SCENES.put("pq_14", PDSPQCodesScene::new);
        SCENES.put("pq_15", PDSPQHuffmanIdeaScene::new);
        SCENES.put("pq_16", PDSPQHuffmanScene::new);
        SCENES.put("pq_17", PDSPQHeapsortScene::new);

        // dictionary deck (6-dictionary.pdf)
        SCENES.put("dict_1to2", PDSDictIntroScene::new);
        SCENES.put("dict_3", PDSDictDefinitionScene::new);
        SCENES.put("dict_4", PDSDictAdtScene::new);
        SCENES.put("dict_5", PDSDictSearchScene::new);
        SCENES.put("dict_6", PDSDictFindMinScene::new);
        SCENES.put("dict_7", PDSDictInsertScene::new);
        SCENES.put("dict_8", PDSDictOrdersScene::new);
        SCENES.put("dict_9", PDSDictInsertPropsScene::new);
        SCENES.put("dict_10", PDSDictOrderQuizScene::new);
        SCENES.put("dict_11", PDSDictRemoveScene::new);
        SCENES.put("dict_12", PDSDictRemoveIdeaScene::new);
        SCENES.put("dict_13", PDSDictSmallestScene::new);
        SCENES.put("dict_14", PDSDictStrategyScene::new);
        SCENES.put("dict_15", PDSDictRemoveExScene::new);
        SCENES.put("dict_16", PDSDictClassworkScene::new);
        SCENES.put("dict_17", PDSDictComplexityScene::new);
        SCENES.put("dict_18", () -> new PDSDictAvgScene(18));
        SCENES.put("dict_19", () -> new PDSDictAvgScene(19));
        SCENES.put("dict_20", () -> new PDSDictAvgScene(20));
        SCENES.put("dict_21", () -> new PDSDictAvgScene(21));
        SCENES.put("dict_22", () -> new PDSDictAvgScene(22));
        SCENES.put("dict_23", PDSDictQuestionsScene::new);
        SCENES.put("dict_24a", () -> new PDSDictExercisesScene(1));
        SCENES.put("dict_24b", () -> new PDSDictExercisesScene(2));
        // dictionary deck, AVL trees (slides 25-48)
        SCENES.put("dict_25to26", () -> new PDSAvlScene("25to26"));
        SCENES.put("dict_27", () -> new PDSAvlScene("27"));
        SCENES.put("dict_28", () -> new PDSAvlScene("28"));
        SCENES.put("dict_29", () -> new PDSAvlScene("29"));
        SCENES.put("dict_30", () -> new PDSAvlScene("30"));
        SCENES.put("dict_31", () -> new PDSAvlScene("31"));
        SCENES.put("dict_32to33", () -> new PDSAvlScene("32to33"));
        SCENES.put("dict_34", () -> new PDSAvlScene("34"));
        SCENES.put("dict_35", () -> new PDSAvlScene("35"));
        SCENES.put("dict_36", () -> new PDSAvlScene("36"));
        SCENES.put("dict_37to39", () -> new PDSAvlScene("37to39"));
        SCENES.put("dict_40", () -> new PDSAvlScene("40"));
        SCENES.put("dict_41", () -> new PDSAvlScene("41"));
        SCENES.put("dict_42", () -> new PDSAvlScene("42"));
        SCENES.put("dict_43", () -> new PDSAvlScene("43"));
        SCENES.put("dict_44", () -> new PDSAvlScene("44"));
        SCENES.put("dict_45to47", () -> new PDSAvlScene("45to47"));
        SCENES.put("dict_48", () -> new PDSAvlScene("48"));
        // dictionary deck, splay trees (slides 49-66)
        SCENES.put("dict_49", () -> new PDSSplayScene("49"));
        SCENES.put("dict_50", () -> new PDSSplayScene("50"));
        SCENES.put("dict_51", () -> new PDSSplayScene("51"));
        SCENES.put("dict_52", () -> new PDSSplayScene("52"));
        SCENES.put("dict_53to54", () -> new PDSSplayScene("53to54"));
        SCENES.put("dict_55", () -> new PDSSplayScene("55"));
        SCENES.put("dict_56to59", () -> new PDSSplayScene("56to59"));
        SCENES.put("dict_60", () -> new PDSSplayScene("60"));
        SCENES.put("dict_61to62", () -> new PDSSplayScene("61to62"));
        SCENES.put("dict_63to64", () -> new PDSSplayScene("63to64"));
        SCENES.put("dict_65", () -> new PDSSplayScene("65"));
        SCENES.put("dict_66", () -> new PDSSplayScene("66"));
        // dictionary deck, hash tables (slides 67-90)
        SCENES.put("dict_67", () -> new PDSHashScene("67"));
        SCENES.put("dict_68to70", () -> new PDSHashScene("68to70"));
        SCENES.put("dict_71to72", () -> new PDSHashScene("71to72"));
        SCENES.put("dict_73", () -> new PDSHashScene("73"));
        SCENES.put("dict_74", () -> new PDSHashScene("74"));
        SCENES.put("dict_75", () -> new PDSHashScene("75"));
        SCENES.put("dict_76to77", () -> new PDSHashScene("76to77"));
        SCENES.put("dict_78", () -> new PDSHashScene("78"));
        SCENES.put("dict_79", () -> new PDSHashScene("79"));
        SCENES.put("dict_80", () -> new PDSHashScene("80"));
        SCENES.put("dict_81", () -> new PDSHashScene("81"));
        SCENES.put("dict_82to84", () -> new PDSHashScene("82to84"));
        SCENES.put("dict_85", () -> new PDSHashScene("85"));
        SCENES.put("dict_86", () -> new PDSHashScene("86"));
        SCENES.put("dict_87", () -> new PDSHashScene("87"));
        SCENES.put("dict_88", () -> new PDSHashScene("88"));
        SCENES.put("dict_89", () -> new PDSHashScene("89"));
        SCENES.put("dict_90", () -> new PDSHashScene("90"));

        // graphs deck (8-graphs.pdf): one clip per sub-topic, named by the slide numbers it covers
        for (String p : new String[]{"1to2", "3", "4", "5to6", "7to8"})
            SCENES.put("graph_" + p, () -> new PDSGraphBasicsScene(p));
        for (String p : new String[]{"9", "10", "11", "12", "13"})
            SCENES.put("graph_" + p, () -> new PDSGraphReprScene(p));
        for (String p : new String[]{"14", "15", "16", "17", "18", "19"})
            SCENES.put("graph_" + p, () -> new PDSGraphTravScene(p));
        for (String p : new String[]{"20", "21", "22", "23to26", "27", "28", "29", "30"})
            SCENES.put("graph_" + p, () -> new PDSGraphAlgoScene(p));

        // complexity deck (2-complexity.pdf)
        for (String p : new String[]{"1to3", "4", "5to7", "8to9", "10to11", "12to13", "14", "15to16", "17", "18to19", "20to22", "23", "24", "25to26", "27"})
            SCENES.put("cx_" + p, () -> new PDSCxScene(p));

        // intro and logistics deck (0-intro+logistics.pdf)
        for (String p : new String[]{"1", "2to3", "4to5", "6to8", "9to12"})
            SCENES.put("intro_" + p, () -> new PDSIntroCourseScene(p));
    }

    @Override
    public void start(Stage primaryStage) {
        Platform.setImplicitExit(true);

        String[] args = getParameters().getRaw().toArray(new String[0]);

        try {
            if (args.length >= 2 && args[0].equalsIgnoreCase("slides")) {
                // === AUTO-GENERATE from a single PPTX file ===
                File pptxFile = new File(args[1]);
                if (!pptxFile.exists()) {
                    System.err.println("File not found: " + pptxFile.getAbsolutePath());
                    Platform.exit();
                    return;
                }
                generateFromPptx(pptxFile);

            } else if (args.length >= 2 && args[0].equalsIgnoreCase("slides-dir")) {
                // === AUTO-GENERATE from a directory of PPTX files ===
                File dir = new File(args[1]);
                if (!dir.isDirectory()) {
                    System.err.println("Not a directory: " + dir.getAbsolutePath());
                    Platform.exit();
                    return;
                }
                File[] pptxFiles = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".pptx"));
                if (pptxFiles == null || pptxFiles.length == 0) {
                    System.err.println("No .pptx files found in: " + dir.getAbsolutePath());
                    Platform.exit();
                    return;
                }
                System.out.printf("=== LecViz: Found %d PPTX files ===%n%n", pptxFiles.length);
                for (File f : pptxFiles) {
                    generateFromPptx(f);
                }

            } else if (args.length == 0) {
                // Render all hardcoded scenes
                System.out.println("=== LecViz: Rendering all PDS scenes ===\n");
                for (var entry : SCENES.entrySet()) {
                    renderScene(entry.getKey(), entry.getValue().get());
                }

            } else {
                String sceneName = args[0].toLowerCase();
                if (SCENES.containsKey(sceneName)) {
                    renderScene(sceneName, SCENES.get(sceneName).get());
                } else {
                    System.err.println("Unknown scene: " + sceneName);
                    System.err.println("Available hardcoded: " + String.join(", ", SCENES.keySet()));
                    System.err.println("\nOr use: slides <file.pptx>      — auto-generate from a PPTX");
                    System.err.println("        slides-dir <folder>     — auto-generate from all PPTX in folder");
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        Platform.exit();
    }

    /**
     * Auto-generate a 3Blue1Brown-style video from a PPTX file.
     */
    private void generateFromPptx(File pptxFile) throws Exception {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║  LecViz — Auto-generating from PPTX slides     ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println("  Input:  " + pptxFile.getName());

        // Step 1: Parse the PPTX
        PPTXParser parser = new PPTXParser();
        List<ParsedSlide> slides = parser.parse(pptxFile);

        System.out.println("  Parsed: " + slides.size() + " slides");
        for (ParsedSlide s : slides) {
            System.out.println("    " + s);
        }

        // Step 2: Derive lecture name from filename
        String lectureName = pptxFile.getName()
                .replaceAll("\\.pptx$", "")
                .replaceAll("[_-]", " ")
                .replaceAll("\\d+\\s*", "")  // remove leading numbers
                .trim();
        if (lectureName.isEmpty()) lectureName = "Lecture";

        // If first slide has a title, prefer that
        if (!slides.isEmpty() && !slides.get(0).title.isEmpty()) {
            lectureName = slides.get(0).title;
        }

        // Step 3: Build the animated scene
        AutoSceneBuilder builder = new AutoSceneBuilder(slides, lectureName);
        Scene scene = builder.build();

        // Step 4: Render to MP4
        String outName = pptxFile.getName().replaceAll("\\.pptx$", "");
        String outFile = "output/" + outName + ".mp4";
        System.out.println("  Output: " + outFile);
        System.out.println();

        scene.setOutputPath(outFile);
        scene.render();

        System.out.println("\n  ✓ Video generated: " + outFile);
        System.out.println();
    }

    private void renderScene(String name, Scene scene) {
        // clips named array_* / list_* go straight into output/arrays/ and output/lists/
        String folder = name.startsWith("array_") ? "arrays/" : name.startsWith("list_") ? "lists/" : name.startsWith("pixel_") ? "pixel/" : name.startsWith("tree_") ? "trees/" : name.startsWith("pq_") ? "pq/" : name.startsWith("dict_") ? "dictionary/" : name.startsWith("graph_") ? "graphs/" : name.startsWith("cx_") ? "complexity/" : name.startsWith("intro_") ? "intro/" : "";
        new java.io.File("output/" + folder).mkdirs();
        String outFile = "output/" + folder + name + ".mp4";
        System.out.printf("Rendering scene '%s' → %s%n", name, outFile);
        scene.setOutputPath(outFile);
        scene.render();
        System.out.println();
    }

    public static void main(String[] args) {
        new java.io.File("output").mkdirs();
        launch(args);
    }
}
