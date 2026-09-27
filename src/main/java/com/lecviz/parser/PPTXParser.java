package com.lecviz.parser;

import org.apache.poi.xslf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses PowerPoint (.pptx) slides using Apache POI.
 * Extracts structured content: title, bullet points, code, and data-structure hints.
 *
 * Usage:
 *   PPTXParser parser = new PPTXParser();
 *   List<ParsedSlide> slides = parser.parse(new File("lecture.pptx"));
 */
public class PPTXParser {

    /**
     * Represents one parsed slide.
     */
    public static class ParsedSlide {
        public int slideNumber;
        public String title = "";
        public String subtitle = "";
        public List<String> bullets = new ArrayList<>();
        public List<String> codeBlocks = new ArrayList<>();
        public String bodyText = "";
        public String notes = "";
        public List<String> imageDescriptions = new ArrayList<>();

        // Detected content type
        public SlideType type = SlideType.CONTENT;

        public enum SlideType {
            TITLE_SLIDE,     // First slide / section title
            CONTENT,         // Normal content with bullets
            CODE_SLIDE,      // Primarily code
            DIAGRAM,         // Has images/diagrams
            MIXED            // Code + text
        }

        @Override
        public String toString() {
            return String.format("Slide %d [%s]: %s (%d bullets, %d code blocks)",
                    slideNumber, type, title, bullets.size(), codeBlocks.size());
        }
    }

    // Patterns to detect code
    private static final Pattern CODE_PATTERN = Pattern.compile(
        "(int |void |char |float |double |struct |#include|printf|scanf|malloc|free|return |for\\s*\\(|while\\s*\\(|if\\s*\\(|\\{|\\}|->|\\*\\w|\\w+\\s*\\()",
        Pattern.CASE_INSENSITIVE
    );

    // Patterns to detect data structure keywords
    private static final Pattern DS_PATTERN = Pattern.compile(
        "(array|linked list|stack|queue|tree|binary tree|BST|heap|graph|hash|dictionary|priority queue|node|pointer|traversal|sort|search|BFS|DFS|insert|delete)",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * Parse a PPTX file into a list of structured slides.
     */
    public List<ParsedSlide> parse(File pptxFile) throws Exception {
        List<ParsedSlide> result = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(pptxFile);
             XMLSlideShow pptx = new XMLSlideShow(fis)) {

            List<XSLFSlide> slides = pptx.getSlides();
            System.out.printf("[PPTXParser] Found %d slides in %s%n", slides.size(), pptxFile.getName());

            for (int i = 0; i < slides.size(); i++) {
                XSLFSlide slide = slides.get(i);
                ParsedSlide parsed = parseSlide(slide, i + 1);
                result.add(parsed);
            }
        }

        // Post-process: detect slide types
        for (int i = 0; i < result.size(); i++) {
            classifySlide(result.get(i), i == 0);
        }

        return result;
    }

    private ParsedSlide parseSlide(XSLFSlide slide, int number) {
        ParsedSlide parsed = new ParsedSlide();
        parsed.slideNumber = number;

        boolean titleFound = false;

        for (XSLFShape shape : slide.getShapes()) {
            if (shape instanceof XSLFTextShape textShape) {
                String text = textShape.getText().trim();
                if (text.isEmpty()) continue;

                // Check if this is a title placeholder
                if (isTitle(textShape) && !titleFound) {
                    parsed.title = text;
                    titleFound = true;
                    continue;
                }

                // Check if this is a subtitle
                if (isSubtitle(textShape) && parsed.subtitle.isEmpty()) {
                    parsed.subtitle = text;
                    continue;
                }

                // Extract bullets from paragraphs
                for (XSLFTextParagraph para : textShape.getTextParagraphs()) {
                    String paraText = para.getText().trim();
                    if (paraText.isEmpty()) continue;

                    if (looksLikeCode(paraText)) {
                        parsed.codeBlocks.add(paraText);
                    } else if (para.getIndentLevel() > 0 || para.isBullet()) {
                        parsed.bullets.add(paraText);
                    } else {
                        // Could be a bullet without indent or body text
                        if (paraText.length() < 150 && !paraText.equals(parsed.title)) {
                            parsed.bullets.add(paraText);
                        } else {
                            parsed.bodyText += paraText + "\n";
                        }
                    }
                }
            } else if (shape instanceof XSLFPictureShape) {
                parsed.imageDescriptions.add("Image on slide " + number);
            } else if (shape instanceof XSLFTable table) {
                // Extract table data
                StringBuilder tableText = new StringBuilder();
                for (int r = 0; r < table.getNumberOfRows(); r++) {
                    XSLFTableRow row = table.getRows().get(r);
                    for (int c = 0; c < row.getCells().size(); c++) {
                        tableText.append(row.getCells().get(c).getText()).append("\t");
                    }
                    tableText.append("\n");
                }
                parsed.bullets.add(tableText.toString().trim());
            }
        }

        // If no title found, use first bullet or body text
        if (parsed.title.isEmpty() && !parsed.bullets.isEmpty()) {
            parsed.title = parsed.bullets.remove(0);
        }

        // Extract speaker notes
        XSLFNotes notes = slide.getNotes();
        if (notes != null) {
            StringBuilder noteText = new StringBuilder();
            for (XSLFShape shape : notes.getShapes()) {
                if (shape instanceof XSLFTextShape ts) {
                    String t = ts.getText().trim();
                    if (!t.isEmpty() && !t.equals(String.valueOf(number))) {
                        noteText.append(t).append(" ");
                    }
                }
            }
            parsed.notes = noteText.toString().trim();
        }

        return parsed;
    }

    private boolean isTitle(XSLFTextShape shape) {
        // Check placeholder type
        if (shape.getTextType() != null) {
            String type = shape.getTextType().toString();
            if (type.contains("TITLE") || type.contains("CTR_TITLE")) {
                return true;
            }
        }
        // Heuristic: large font, short text at top
        try {
            for (XSLFTextParagraph p : shape.getTextParagraphs()) {
                for (XSLFTextRun run : p.getTextRuns()) {
                    if (run.getFontSize() != null && run.getFontSize() >= 28) {
                        return true;
                    }
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    private boolean isSubtitle(XSLFTextShape shape) {
        if (shape.getTextType() != null) {
            return shape.getTextType().toString().contains("SUBTITLE") ||
                   shape.getTextType().toString().contains("BODY");
        }
        return false;
    }

    private boolean looksLikeCode(String text) {
        if (text.length() < 5) return false;
        Matcher m = CODE_PATTERN.matcher(text);
        int matches = 0;
        while (m.find()) matches++;
        // If more than 2 code-like patterns, it's likely code
        return matches >= 2 || text.contains(";") && text.contains("{");
    }

    private void classifySlide(ParsedSlide slide, boolean isFirst) {
        if (isFirst || (slide.bullets.isEmpty() && slide.codeBlocks.isEmpty()
                        && slide.subtitle.isEmpty() && slide.bodyText.isBlank())) {
            slide.type = ParsedSlide.SlideType.TITLE_SLIDE;
        } else if (!slide.codeBlocks.isEmpty() && slide.bullets.isEmpty()) {
            slide.type = ParsedSlide.SlideType.CODE_SLIDE;
        } else if (!slide.codeBlocks.isEmpty()) {
            slide.type = ParsedSlide.SlideType.MIXED;
        } else if (!slide.imageDescriptions.isEmpty() && slide.bullets.size() <= 2) {
            slide.type = ParsedSlide.SlideType.DIAGRAM;
        } else {
            slide.type = ParsedSlide.SlideType.CONTENT;
        }
    }

    /**
     * Detect which PDS data structures are mentioned in a slide.
     */
    public static List<String> detectDataStructures(ParsedSlide slide) {
        List<String> found = new ArrayList<>();
        String allText = (slide.title + " " + slide.bodyText + " " +
                String.join(" ", slide.bullets) + " " +
                String.join(" ", slide.codeBlocks)).toLowerCase();

        String[][] dsKeywords = {
            {"array", "arrays"},
            {"linked list", "linkedlist", "node->next"},
            {"stack", "push", "pop", "lifo"},
            {"queue", "enqueue", "dequeue", "fifo"},
            {"tree", "binary tree", "bst", "inorder", "preorder", "postorder"},
            {"heap", "min-heap", "max-heap", "priority queue", "heapify"},
            {"graph", "bfs", "dfs", "adjacency", "vertex", "edge"},
            {"hash", "dictionary", "hash table", "hashing", "collision"},
            {"sort", "bubble sort", "insertion sort", "merge sort", "quick sort", "selection sort"},
            {"search", "binary search", "linear search"}
        };
        String[] dsNames = {"Array", "LinkedList", "Stack", "Queue", "Tree", "Heap", "Graph", "Hash", "Sort", "Search"};

        for (int i = 0; i < dsKeywords.length; i++) {
            for (String kw : dsKeywords[i]) {
                if (allText.contains(kw)) {
                    if (!found.contains(dsNames[i])) {
                        found.add(dsNames[i]);
                    }
                    break;
                }
            }
        }
        return found;
    }
}
