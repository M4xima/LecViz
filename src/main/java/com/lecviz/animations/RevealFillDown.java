package com.lecviz.animations;

import com.lecviz.core.Animation;
import com.lecviz.mobjects.RectMob;

/**
 * Grows a RectMob from a thin sliver at its top edge down to full size,
 * staying top-anchored — a vertical "pouring" color reveal, top-left
 * corner to bottom-left corner, instead of an instant color change.
 */
public class RevealFillDown extends Animation {

    private final RectMob rect;
    private final double x, width, height, topY;

    public RevealFillDown(RectMob rect, double x, double width, double height, double topY, double duration) {
        super(rect, duration);
        this.rect = rect;
        this.x = x;
        this.width = width;
        this.height = height;
        this.topY = topY;
    }

    @Override
    public void begin() {
        rect.setSize(width, 0.001);
        rect.setPosition(x, topY);
    }

    @Override
    public void interpolate(double t) {
        double h = Math.max(0.001, height * t);
        rect.setSize(width, h);
        rect.setPosition(x, topY + h / 2.0);
    }
}
