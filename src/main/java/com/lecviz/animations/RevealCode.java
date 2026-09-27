package com.lecviz.animations;

import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.mobjects.CodeBlock;
import com.lecviz.utils.Easing;

/**
 * Reveals code line by line.
 */
public class RevealCode extends Animation {

    private final int totalLines;

    public RevealCode(MObject target, double duration) {
        super(target, duration, Easing.LINEAR);
        if (target instanceof CodeBlock cb) {
            this.totalLines = cb.getTotalLines();
        } else {
            this.totalLines = 1;
        }
    }

    @Override
    public void begin() {
        if (target instanceof CodeBlock cb) {
            cb.setVisibleLines(0);
            cb.setOpacity(1);
        }
    }

    @Override
    public void interpolate(double t) {
        if (target instanceof CodeBlock cb) {
            cb.setVisibleLines((int) Math.ceil(t * totalLines));
        }
    }
}
