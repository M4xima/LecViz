package com.lecviz.animations;

import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Vec2;
import javafx.scene.paint.Color;

/**
 * Morphs one mobject into another: crossfades opacity while both objects
 * travel/scale toward the target's end state. The target object must already
 * be added to the scene (and typically positioned at its final resting spot)
 * before this plays; the source is left fully transparent when finished.
 */
public class Transform extends Animation {

    private final MObject into;

    private Vec2 startPos;
    private Vec2 endPos;
    private double startScale;
    private double endScale;
    private Color startFill;
    private Color endFill;
    private Color startStroke;
    private Color endStroke;
    private double startOpacity;
    private double endOpacity;

    public Transform(MObject from, MObject into, double duration) {
        super(from, duration);
        this.into = into;
    }

    @Override
    public void begin() {
        startPos = target.getPosition();
        endPos = into.getPosition();
        startScale = target.getScale();
        endScale = into.getScale();
        startFill = target.getFillColor();
        endFill = into.getFillColor();
        startStroke = target.getStrokeColor();
        endStroke = into.getStrokeColor();
        startOpacity = target.getOpacity();
        endOpacity = into.getOpacity() > 0 ? into.getOpacity() : 1.0;

        into.setPosition(startPos);
        into.setScale(startScale);
        into.setOpacity(0);
    }

    @Override
    public void interpolate(double t) {
        Vec2 p = startPos.lerp(endPos, t);
        double s = startScale + (endScale - startScale) * t;

        target.setPosition(p);
        target.setScale(s);
        target.setOpacity(startOpacity * (1 - t));
        target.setFillColor(Colors.interpolate(startFill, endFill, t));
        target.setStrokeColor(Colors.interpolate(startStroke, endStroke, t));

        into.setPosition(p);
        into.setScale(s);
        into.setOpacity(endOpacity * t);
    }

    @Override
    public void finish() {
        interpolate(1.0);
        target.setOpacity(0);
        into.setPosition(endPos);
        into.setScale(endScale);
        into.setOpacity(endOpacity);
    }
}
