package com.lecviz.scenes;

/**
 * Standalone clip for slide 13 (the 8-queens problem): heading, question and
 * sub-questions fade in with a small latency, then get answered one by one — a
 * solution on the 8x8 board, backtracking on 2x2 / 3x3 / 4x4, and Sudoku as the
 * look-alike constraint puzzle. The scene itself lives in
 * {@link PDSClassworkScene#queensSection()}; this class just runs it alone so the
 * slide-13 video can be rendered (and stored) separately from slides 10-12.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSQueensScene extends PDSClassworkScene {

    @Override
    public void construct() {
        queensSection();
    }
}
