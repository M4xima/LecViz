package com.lecviz.animations;

import com.lecviz.core.Animation;
import com.lecviz.mobjects.CubeGrid3D;
import com.lecviz.utils.Vec3;

import java.util.HashMap;
import java.util.Map;

/**
 * Eases every listed cube's local-space offset from where it is now toward
 * a target offset — e.g. pulling a 3D cuboid apart into a straight line.
 * Play alongside a RotateCube3D back to (0, 0) and the cubes visually
 * "unroll" into a flat memory strip.
 */
public class FlattenCube3D extends Animation {

    private final CubeGrid3D grid;
    private final Map<CubeGrid3D.Idx, Vec3> targets;
    private Map<CubeGrid3D.Idx, Vec3> starts;

    public FlattenCube3D(CubeGrid3D grid, Map<CubeGrid3D.Idx, Vec3> targets, double duration) {
        super(grid, duration);
        this.grid = grid;
        this.targets = targets;
    }

    @Override
    public void begin() {
        starts = new HashMap<>();
        for (CubeGrid3D.Idx idx : targets.keySet()) {
            starts.put(idx, grid.getOffset(idx));
        }
    }

    @Override
    public void interpolate(double t) {
        for (Map.Entry<CubeGrid3D.Idx, Vec3> e : targets.entrySet()) {
            Vec3 start = starts.get(e.getKey());
            grid.setOffset(e.getKey(), start.lerp(e.getValue(), t));
        }
    }
}
