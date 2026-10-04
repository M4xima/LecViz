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
        String outFile = "output/" + name + ".mp4";
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
