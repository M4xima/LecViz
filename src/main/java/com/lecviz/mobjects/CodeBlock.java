package com.lecviz.mobjects;

import com.lecviz.core.MObject;
import com.lecviz.utils.Colors;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Syntax-highlighted code block with C-style keyword highlighting.
 * Supports line-by-line reveal and line highlighting.
 */
public class CodeBlock extends MObject {

    private final String[] lines;
    private final double fontSize;
    private final double lineHeight;
    private final double paddingX = 30;
    private final double paddingY = 20;
    private double blockWidth;
    private double blockHeight;
    private int visibleLines;              // for line-by-line reveal
    private Set<Integer> highlightedLines = new HashSet<>();
    private Color highlightColor = Colors.withAlpha(Colors.YELLOW, 0.15);

    // C/Java keywords for syntax highlighting
    private static final Set<String> KEYWORDS = Set.of(
        "int", "float", "double", "char", "void", "long", "short", "unsigned",
        "signed", "struct", "enum", "union", "typedef", "const", "static",
        "extern", "volatile", "register", "auto", "inline",
        "if", "else", "for", "while", "do", "switch", "case", "default",
        "break", "continue", "return", "goto", "sizeof",
        "malloc", "calloc", "realloc", "free", "printf", "scanf",
        "NULL", "true", "false",
        // Java extras
        "public", "private", "protected", "class", "interface", "extends",
        "implements", "new", "this", "super", "final", "abstract",
        "import", "package", "try", "catch", "throw", "throws", "finally",
        "boolean", "byte", "String", "System"
    );

    private static final Set<String> TYPES = Set.of(
        "int", "float", "double", "char", "void", "long", "short",
        "boolean", "byte", "String", "FILE"
    );

    public CodeBlock(String code) {
        this(code, 22);
    }

    public CodeBlock(String code, double fontSize) {
        this.lines = code.split("\n");
        this.fontSize = fontSize;
        this.lineHeight = fontSize * 1.5;
        this.visibleLines = lines.length;
        this.fillColor = Colors.CODE_BG;

        // Calculate block dimensions
        double maxLineWidth = 0;
        for (String line : lines) {
            maxLineWidth = Math.max(maxLineWidth, line.length() * fontSize * 0.6);
        }
        this.blockWidth = maxLineWidth + 2 * paddingX + 50; // +50 for line numbers
        this.blockHeight = lines.length * lineHeight + 2 * paddingY;
    }

    public CodeBlock setVisibleLines(int n) {
        this.visibleLines = Math.max(0, Math.min(n, lines.length));
        return this;
    }
    public int getVisibleLines() { return visibleLines; }
    public int getTotalLines() { return lines.length; }

    public CodeBlock highlightLine(int lineIndex) {
        highlightedLines.add(lineIndex);
        return this;
    }
    public CodeBlock clearHighlights() {
        highlightedLines.clear();
        return this;
    }
    public CodeBlock setHighlightedLines(Set<Integer> lines) {
        this.highlightedLines = new HashSet<>(lines);
        return this;
    }

    @Override
    protected void draw(GraphicsContext gc) {
        double startX = -blockWidth / 2;
        double startY = -blockHeight / 2;

        // Draw background with rounded rect
        gc.setFill(fillColor);
        gc.fillRoundRect(startX, startY, blockWidth, blockHeight, 12, 12);

        // Draw border
        gc.setStroke(Colors.DARK_GRAY);
        gc.setLineWidth(1);
        gc.strokeRoundRect(startX, startY, blockWidth, blockHeight, 12, 12);

        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);
        Font codeFont = Font.font("Monospace", fontSize);
        gc.setFont(codeFont);

        double lineNumWidth = 40;

        for (int i = 0; i < visibleLines && i < lines.length; i++) {
            double y = startY + paddingY + i * lineHeight + lineHeight / 2;

            // Line highlight background
            if (highlightedLines.contains(i)) {
                gc.setFill(highlightColor);
                gc.fillRect(startX, startY + paddingY + i * lineHeight, blockWidth, lineHeight);
            }

            // Line number
            gc.setFill(Colors.GRAY);
            gc.fillText(String.format("%3d", i + 1), startX + 10, y);

            // Syntax-highlighted code
            drawHighlightedLine(gc, lines[i], startX + paddingX + lineNumWidth, y);
        }
    }

    private void drawHighlightedLine(GraphicsContext gc, String line, double x, double y) {
        double charWidth = fontSize * 0.6;

        // Simple tokenizer
        int pos = 0;
        while (pos < line.length()) {
            char ch = line.charAt(pos);

            if (ch == '/' && pos + 1 < line.length() && line.charAt(pos + 1) == '/') {
                // Comment — rest of line
                gc.setFill(Colors.CODE_COMMENT);
                gc.fillText(line.substring(pos), x + pos * charWidth, y);
                break;
            } else if (ch == '"' || ch == '\'') {
                // String literal
                int end = line.indexOf(ch, pos + 1);
                if (end == -1) end = line.length() - 1;
                String token = line.substring(pos, end + 1);
                gc.setFill(Colors.CODE_STRING);
                gc.fillText(token, x + pos * charWidth, y);
                pos = end + 1;
            } else if (Character.isDigit(ch)) {
                // Number
                int start = pos;
                while (pos < line.length() && (Character.isDigit(line.charAt(pos)) || line.charAt(pos) == '.'))
                    pos++;
                gc.setFill(Colors.CODE_NUMBER);
                gc.fillText(line.substring(start, pos), x + start * charWidth, y);
            } else if (Character.isLetterOrDigit(ch) || ch == '_') {
                // Identifier or keyword
                int start = pos;
                while (pos < line.length() && (Character.isLetterOrDigit(line.charAt(pos)) || line.charAt(pos) == '_'))
                    pos++;
                String word = line.substring(start, pos);
                if (KEYWORDS.contains(word)) {
                    gc.setFill(TYPES.contains(word) ? Colors.CODE_TYPE : Colors.CODE_KEYWORD);
                } else if (pos < line.length() && line.charAt(pos) == '(') {
                    gc.setFill(Colors.CODE_FUNCTION);
                } else {
                    gc.setFill(Colors.CODE_DEFAULT);
                }
                gc.fillText(word, x + start * charWidth, y);
            } else {
                // Operator / punctuation
                gc.setFill(Colors.CODE_DEFAULT);
                gc.fillText(String.valueOf(ch), x + pos * charWidth, y);
                pos++;
            }
        }
    }

    @Override
    public MObject copy() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) sb.append("\n");
            sb.append(lines[i]);
        }
        CodeBlock c = new CodeBlock(sb.toString(), fontSize);
        copyBaseProperties(c);
        c.visibleLines = this.visibleLines;
        c.highlightedLines = new HashSet<>(this.highlightedLines);
        return c;
    }
}
