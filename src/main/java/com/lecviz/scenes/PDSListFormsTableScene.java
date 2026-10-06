package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Standalone clip for slides 31-32 of the lists deck: prefix, postfix and non-ambiguity.
 *
 *   Slide 31  the table of five parenthesized infix expressions drops in row by row with the prefix and
 *             postfix columns still empty
 *   Slide 32  each row is read off: its prefix form and postfix form type into the empty cells one after
 *             the other; then the slide's three conclusions one line at a time
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSListFormsTableScene extends PDSListClipBase {

    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("Prefix, Postfix and Non-ambiguity");
        pause(0.4);

        String[] infix = {"(1 + 2) * (3 – 4)", "1 + (2 * (3 – 4))", "(1 + (2 * 3)) – 4", "((1 + 2) * 3) – 4", "1 + ((2 * 3) - 4)"};
        String[] prefix = {"* + 1 2 – 3 4", "+ 1 * 2 – 3 4", "- + 1 * 2 3 4", "- * + 1 2 3 4", "+ 1 - * 2 3 4"};
        String[] postfix = {"1 2 + 3 4 - *", "1 2 3 4 - * +", "1 2 3 * + 4 -", "1 2 + 3 * 4 -", "1 2 3 * 4 - +"};
        String[][] blank = new String[5][3];
        Color[][] tint = new Color[5][3];
        for (int r = 0; r < 5; r++) {
            blank[r][0] = infix[r];
            blank[r][1] = "";
            blank[r][2] = "";
            tint[r][1] = Colors.GOLD;
            tint[r][2] = Colors.PINK;
        }
        List<MObject> table = dropTable(new String[]{"Infix", "Prefix", "Postfix"}, blank,
                new double[]{640, 520, 520}, tint, -330);
        List<MObject> mine = new ArrayList<>();
        mine.add(head);
        mine.addAll(table);
        pause(0.6);
        StrokeTextMob ask = stroke("Fill in the prefix and postfix form of each row.", 0, 110, 40, Colors.ORANGE, false);
        play(new Write(ask, d(2.8)));
        mine.add(ask);
        pause(0.8);

        // each row is read off: the cells type in, and the infix cell lights up while they do
        for (int r = 0; r < 5; r++) {
            RectMob infixBox = (RectMob) table.get(2 * ((r + 1) * 3));
            TextMob preT = (TextMob) table.get(2 * ((r + 1) * 3 + 1) + 1);
            TextMob postT = (TextMob) table.get(2 * ((r + 1) * 3 + 2) + 1);
            play(new ColorChange(infixBox, Colors.withAlpha(Colors.ORANGE, 0.5), d(0.3)));
            typeable(preT, prefix[r]);
            typeable(postT, postfix[r]);
            play(new TypeAt(preT, 0, d(1.0)), new TypeAt(postT, 0.5, d(1.0)));
            pause(0.5);
            play(new ColorChange(infixBox, Colors.withAlpha(Colors.WHITE, 0.07), d(0.3)));
        }
        pause(0.8);
        play(new FadeOut(ask, d(0.5)));
        remove(ask);
        mine.remove(ask);

        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "No parentheses in prefix and postfix forms."));
        s.add(ln(0, "Infix is ambiguous; prefix and postfix are not."));
        s.add(ln(0, "Unique prefix and postfix forms for different orders of operator evaluation."));
        List<List<MObject>> text = writeSlide(s, 130);
        for (List<MObject> g : text) mine.addAll(g);
        pause(3.2);
        fadeOutAll(d(1.2), mine);
        pause(0.4);
    }

    /** Sets the cell's text so it can type in without drifting: left-aligned, placed so the full text is centred. */
    private void typeable(TextMob t, String text) {
        double w = measure(text, "SansSerif", t.getFontSize(), true);
        double cx = t.getPosition().x();
        t.setText(text);
        t.setAlignment(javafx.scene.text.TextAlignment.LEFT);
        t.setPosition(cx - w / 2, t.getPosition().y());
    }
}
