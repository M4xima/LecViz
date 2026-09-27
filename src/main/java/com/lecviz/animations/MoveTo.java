package com.lecviz.animations;

import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.utils.Vec2;

public class MoveTo extends Animation {

    private Vec2 startPos;
    private final Vec2 endPos;

    public MoveTo(MObject target, Vec2 endPos, double duration) {
        super(target, duration);
        this.endPos = endPos;
    }

    public MoveTo(MObject target, double x, double y, double duration) {
        this(target, new Vec2(x, y), duration);
    }

    @Override
    public void begin() {
        startPos = target.getPosition();
    }

    @Override
    public void interpolate(double t) {
        target.setPosition(startPos.lerp(endPos, t));
    }
}
