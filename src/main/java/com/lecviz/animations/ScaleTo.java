package com.lecviz.animations;

import com.lecviz.core.Animation;
import com.lecviz.core.MObject;

public class ScaleTo extends Animation {

    private double startScale;
    private final double endScale;

    public ScaleTo(MObject target, double endScale, double duration) {
        super(target, duration);
        this.endScale = endScale;
    }

    @Override
    public void begin() {
        startScale = target.getScale();
    }

    @Override
    public void interpolate(double t) {
        target.setScale(startScale + (endScale - startScale) * t);
    }
}
