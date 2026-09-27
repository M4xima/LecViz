package com.lecviz.animations;

import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.mobjects.LaTeXMob;
import com.lecviz.mobjects.TextMob;
import com.lecviz.utils.Easing;

/**
 * Typewriter-style text reveal animation.
 * Works with both TextMob and LaTeXMob.
 */
public class Write extends Animation {

    public Write(MObject target, double duration) {
        super(target, duration, Easing.LINEAR);
    }

    @Override
    public void begin() {
        if (target instanceof TextMob t) {
            t.setVisibleFraction(0);
            t.setOpacity(1);
        } else if (target instanceof LaTeXMob l) {
            l.setVisibleFraction(0);
            l.setOpacity(1);
        }
    }

    @Override
    public void interpolate(double t) {
        if (target instanceof TextMob text) {
            text.setVisibleFraction(t);
        } else if (target instanceof LaTeXMob latex) {
            latex.setVisibleFraction(t);
        }
    }
}
