package com.lecviz.animations;

import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.utils.Colors;
import javafx.scene.paint.Color;

/**
 * Smoothly transitions an object's fill or stroke color.
 */
public class ColorChange extends Animation {

    public enum Target { FILL, STROKE }

    private final Color endColor;
    private Color startColor;
    private final Target colorTarget;

    public ColorChange(MObject target, Color endColor, double duration, Target ct) {
        super(target, duration);
        this.endColor = endColor;
        this.startColor = null;
        this.colorTarget = ct;
    }

    public ColorChange(MObject target, Color endColor, double duration) {
        this(target, endColor, duration, Target.FILL);
    }

    // 4-arg constructor: explicit start and end colors
    public ColorChange(MObject target, Color startColor, Color endColor, double duration) {
        super(target, duration);
        this.startColor = startColor;
        this.endColor = endColor;
        this.colorTarget = Target.FILL;
    }

    @Override
    public void begin() {
        if (startColor == null) {
            startColor = (colorTarget == Target.FILL)
                    ? target.getFillColor()
                    : target.getStrokeColor();
        }
        if (colorTarget == Target.FILL) target.setFillColor(startColor);
        else target.setStrokeColor(startColor);
    }

    @Override
    public void interpolate(double t) {
        Color c = Colors.interpolate(startColor, endColor, t);
        if (colorTarget == Target.FILL) target.setFillColor(c);
        else target.setStrokeColor(c);
    }
}
