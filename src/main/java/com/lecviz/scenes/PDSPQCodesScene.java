package com.lecviz.scenes;

import com.lecviz.mobjects.StrokeTextMob;

/**
 * Standalone clip for slide 14 of the priority-queue deck: the first slide of "Application: Huffman Coding". The
 * slide is the one the trees deck has as slide 34 (the table of five letters with three codes, and the three code
 * trees, ending with the prefix codes), so the clip is that clip under this deck's heading.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSPQCodesScene extends PDSTreeShorterScene {

    @Override
    protected StrokeTextMob writeHeading(String text) {
        return super.writeHeading("Shorter Codes".equals(text) ? "Application: Huffman Coding" : text);
    }
}
