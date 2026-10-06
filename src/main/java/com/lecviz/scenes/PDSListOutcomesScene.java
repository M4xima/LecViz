package com.lecviz.scenes;

/**
 * Standalone clip for slide 48 of the lists deck, the end of the lists lecture: the learning outcomes (List,
 * Stack and Queue with the applications seen in this lecture), then the "made by Karthik & Tejaswi" card.
 * The scenes themselves live in PDSListPracticeScene.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListOutcomesScene extends PDSListPracticeScene {

    @Override
    public void construct() {
        outcomes();
        madeBy();
    }
}
