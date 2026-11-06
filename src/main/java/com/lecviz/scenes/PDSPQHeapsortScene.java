package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.MObject;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;

import java.util.ArrayList;
import java.util.List;

/**
 * Standalone clip for slide 17 of the priority-queue deck, the last slide: heapsort. The slide is the one the arrays
 * deck has as slide 23, so the clip is that clip (the slide line by line, a heap built and N deleteMax moved into a
 * second array, then the same sort in place with hide_back), followed by the "made by Karthik &amp; Tejaswi" card that
 * ends the deck.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSPQHeapsortScene extends PDSHeapSortScene {

    @Override
    public void construct() {
        super.construct();
        madeBy();
    }

    private void madeBy() {
        List<MObject> mine = new ArrayList<>();
        StrokeTextMob by = stroke("This video was made by", 0, -170, 38, Colors.LIGHT_GRAY, false);
        play(new Write(by, d(2.0)));
        mine.add(by);
        pause(0.2);
        double size = 104, gap = 34;
        double w1 = strokeW("Karthik", true, size), w2 = strokeW("&", true, size), w3 = strokeW("Tejaswi", true, size);
        double left = -(w1 + w2 + w3 + 2 * gap) / 2;
        StrokeTextMob n1 = stroke("Karthik", left + w1 / 2, -30, size, Colors.TEAL, true);
        StrokeTextMob amp = stroke("&", left + w1 + gap + w2 / 2, -30, size, Colors.WHITE, true);
        StrokeTextMob n2 = stroke("Tejaswi", left + w1 + w2 + 2 * gap + w3 / 2, -30, size, Colors.GOLD, true);
        play(new Write(n1, d(1.8)));
        play(new Write(amp, d(0.7)));
        play(new Write(n2, d(1.8)));
        mine.add(n1);
        mine.add(amp);
        mine.add(n2);
        TextMob course = label("CS5013  ·  LecViz  ·  Priority Queues", 0, 200, 30, Colors.GRAY, false, false);
        play(new FadeIn(course, d(0.8)));
        mine.add(course);
        pause(3.2);
        fadeOutAll(d(1.4), mine);
        pause(0.4);
    }
}
