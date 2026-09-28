package com.lecviz.animations;

import com.lecviz.core.Animation;
import com.lecviz.mobjects.CubeGrid3D;

/** Orbits a CubeGrid3D's camera angles (yaw, pitch) toward a target. */
public class RotateCube3D extends Animation {

    private final CubeGrid3D grid;
    private final double targetRotY, targetRotX;
    private double startRotY, startRotX;

    public RotateCube3D(CubeGrid3D grid, double targetRotY, double targetRotX, double duration) {
        super(grid, duration);
        this.grid = grid;
        this.targetRotY = targetRotY;
        this.targetRotX = targetRotX;
    }

    @Override
    public void begin() {
        startRotY = grid.getRotY();
        startRotX = grid.getRotX();
    }

    @Override
    public void interpolate(double t) {
        grid.setRotation(startRotY + (targetRotY - startRotY) * t, startRotX + (targetRotX - startRotX) * t);
    }
}
