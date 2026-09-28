package com.lecviz.animations;

import com.lecviz.core.Animation;
import com.lecviz.mobjects.ArrayMob;
import com.lecviz.utils.Vec2;

/**
 * Visually swaps two cells of an ArrayMob by arcing them past each other
 * (one dips down, one arcs up), then commits the underlying value swap.
 * This is what makes sorting animations read as real motion instead of
 * numbers teleporting in place.
 */
public class SwapCells extends Animation {

    private final ArrayMob arr;
    private final int i;
    private final int j;
    private final double arcHeight;

    public SwapCells(ArrayMob arr, int i, int j, double duration) {
        this(arr, i, j, duration, 45);
    }

    public SwapCells(ArrayMob arr, int i, int j, double duration, double arcHeight) {
        super(arr, duration);
        this.arr = arr;
        this.i = i;
        this.j = j;
        this.arcHeight = arcHeight;
    }

    @Override
    public void begin() {
        arr.setCellColor(i, com.lecviz.utils.Colors.withAlpha(com.lecviz.utils.Colors.ORANGE, 0.35));
        arr.setCellColor(j, com.lecviz.utils.Colors.withAlpha(com.lecviz.utils.Colors.ORANGE, 0.35));
    }

    @Override
    public void interpolate(double t) {
        double cellW = arr.getCellWidth();
        double dx = (j - i) * cellW;
        double arc = Math.sin(Math.PI * t) * arcHeight;

        arr.setCellOffset(i, new Vec2(dx * t, -arc));
        arr.setCellOffset(j, new Vec2(-dx * t, arc));
    }

    @Override
    public void finish() {
        arr.clearCellOffset(i);
        arr.clearCellOffset(j);
        arr.swap(i, j);
        arr.clearCellColorAt(i);
        arr.clearCellColorAt(j);
    }
}
