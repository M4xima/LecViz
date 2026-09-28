package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.MObject;
import com.lecviz.core.Scene;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import com.lecviz.utils.Easing;
import com.lecviz.utils.Vec2;
import com.lecviz.utils.Vec3;

import java.util.HashMap;
import java.util.Map;

/**
 * PDS Arrays — 3Blue1Brown-style visual reasoning pass.
 *
 * This is a deliberately narrower cut than PDSArraySceneV2: instead of
 * animating every bullet on Prof. Rupesh Nasre's slides, it picks the ideas
 * with the strongest visual payoff and builds real motion around them —
 * a puzzle-first open, a memory strip the array formula is derived from,
 * a matrix that physically unrolls into 1D memory two different ways,
 * a linear-vs-binary search race, and a bubble sort that actually swaps
 * cells in space instead of teleporting numbers.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSArraySceneV3 extends Scene {

    // ─── Shared helpers ─────────────────────────────────────────
    private void fadeOutAll(double dur, MObject... objs) {
        FadeOut[] anims = new FadeOut[objs.length];
        for (int i = 0; i < objs.length; i++) anims[i] = new FadeOut(objs[i], dur);
        play(anims);
        for (MObject o : objs) remove(o);
    }

    private TextMob sectionTitle(String text) {
        return new TextMob(text)
                .setFontSize(48).setBold().setFillColor(Colors.BLUE)
                .setPosition(0, -430);
    }

    private RectMob[] memoryRow(int n, double cw, double y) {
        RectMob[] cells = new RectMob[n];
        for (int i = 0; i < n; i++) {
            double x = -((n - 1) * cw) / 2.0 + i * cw;
            RectMob c = new RectMob(cw - 8, 50).setCornerRadius(4);
            c.setStrokeColor(Colors.withAlpha(Colors.BLUE, 0.45));
            c.setFillColor(Colors.withAlpha(Colors.BLUE, 0.07));
            c.setPosition(x, y);
            c.setOpacity(0);
            cells[i] = c;
        }
        return cells;
    }

    @Override
    public void construct() {
        buildOpeningHook();
        buildMemoryAndAddresses();
        buildRowColumnMajor();
        build3DRowColumnMajor();
        buildSearchRace();
        buildBubbleSortReal();
        buildClosing();
    }

    // ╔══════════════════════════════════════════════════════════╗
    // ║  1. COLD OPEN — pose the puzzle before the title card    ║
    // ╚══════════════════════════════════════════════════════════╝
    private void buildOpeningHook() {
        RectMob[] cellsA = memoryRow(10, 64, 0);
        for (RectMob c : cellsA) { add(c); play(new FadeIn(c, 0.06)); }
        hold(0.6);

        TextMob q1 = new TextMob("The program needs arr[9].")
                .setFontSize(30).setFillColor(Colors.WHITE);
        q1.setPosition(0, -180);
        play(new Write(q1, 1.5));
        hold(0.7);

        TextMob q2 = new TextMob("Surely it has to check arr[0], arr[1], arr[2] ... first?")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        q2.setPosition(0, -130);
        play(new Write(q2, 1.8));
        hold(1.0);

        CircleMob dot = new CircleMob(8);
        dot.setFillColor(Colors.GOLD);
        dot.setPosition(cellsA[0].getPosition().x(), 55);
        add(dot);
        play(new FadeIn(dot, 0.3));
        for (int i = 0; i < 9; i++) {
            play(new MoveTo(dot, cellsA[i].getPosition().x(), 55, 0.16),
                 new ColorChange(cellsA[i], Colors.withAlpha(Colors.GRAY, 0.35), 0.16));
        }
        hold(0.9);

        fadeOutAll(0.7, q1, q2, dot);
        for (RectMob c : cellsA) { play(new FadeOut(c, 0.3)); remove(c); }
        hold(0.5);

        RectMob[] cellsB = memoryRow(10, 64, 0);
        for (RectMob c : cellsB) { add(c); play(new FadeIn(c, 0.06)); }

        TextMob answer = new TextMob("No. One formula. One jump.")
                .setFontSize(30).setBold().setFillColor(Colors.GOLD);
        answer.setPosition(0, -150);
        play(new Write(answer, 1.2));
        hold(0.6);

        CircleMob jumpDot = new CircleMob(9);
        jumpDot.setFillColor(Colors.GOLD);
        jumpDot.setPosition(cellsB[0].getPosition().x(), 55);
        jumpDot.setOpacity(0);
        add(jumpDot);
        play(new FadeIn(jumpDot, 0.25));
        play(new MoveTo(jumpDot, cellsB[9].getPosition().x(), 55, 0.7).setEasing(Easing.EASE_OUT));
        play(new ColorChange(cellsB[9], Colors.GOLD, 0.3));
        hold(0.6);

        // Push in on the landing cell — the first "camera as punctuation" beat
        cameraTo(cellsB[9].getPosition().x(), 55, 2.2, 0.8);
        hold(1.4);
        resetCamera(0.9);
        hold(0.5);

        fadeOutAll(0.9, answer, jumpDot);
        for (RectMob c : cellsB) { play(new FadeOut(c, 0.3)); remove(c); }
        hold(0.5);

        // Title card
        TextMob title = new TextMob("Arrays").setFontSize(90).setBold().setFillColor(Colors.BLUE);
        title.setPosition(0, -30);
        title.setScale(0.3);
        play(new FadeIn(title, 1.3), new ScaleTo(title, 1.0, 1.6));
        hold(1.2);

        TextMob subtitle = new TextMob("base + index × size — the formula behind every jump")
                .setFontSize(24).setFillColor(Colors.LIGHT_GRAY);
        subtitle.setPosition(0, 50);
        play(new Write(subtitle, 2.0));
        hold(1.4);

        TextMob credit = new TextMob("PDS  ·  Prof. Rupesh Nasre  ·  IIT Madras")
                .setFontSize(18).setFillColor(Colors.GRAY);
        credit.setPosition(0, 95);
        play(new FadeIn(credit, 1.2));
        hold(2.0);

        fadeOutAll(1.2, title, subtitle, credit);
        hold(0.6);
    }

    // ╔══════════════════════════════════════════════════════════╗
    // ║  2. MEMORY & ADDRESSES — derive the formula visually     ║
    // ╚══════════════════════════════════════════════════════════╝
    private void buildMemoryAndAddresses() {
        TextMob heading = sectionTitle("Where Does arr[i] Actually Live?");
        play(new Write(heading, 1.6));
        hold(0.9);

        int total = 16, arrStart = 3, arrLen = 6;
        double cw = 58;
        RectMob[] mem = new RectMob[total];
        TextMob[] addr = new TextMob[total];
        for (int i = 0; i < total; i++) {
            double x = -((total - 1) * cw) / 2.0 + i * cw;
            RectMob c = new RectMob(cw - 6, 46).setCornerRadius(3);
            c.setStrokeColor(Colors.withAlpha(Colors.GRAY, 0.5));
            c.setFillColor(Colors.withAlpha(Colors.GRAY, 0.05));
            c.setPosition(x, -40);
            c.setOpacity(0);
            mem[i] = c;
            add(c);

            TextMob a = new TextMob(String.valueOf(1000 + i * 4))
                    .setFontSize(13).setFillColor(Colors.DARK_GRAY);
            a.setPosition(x, -40 + 23 + 18);
            a.setOpacity(0);
            addr[i] = a;
            add(a);
        }
        for (int i = 0; i < total; i++) play(new FadeIn(mem[i], 0.05), new FadeIn(addr[i], 0.05));
        hold(0.9);

        TextMob memLabel = new TextMob("Computer memory: one long numbered row of boxes")
                .setFontSize(20).setFillColor(Colors.LIGHT_GRAY);
        memLabel.setPosition(0, -140);
        play(new Write(memLabel, 1.8));
        hold(1.6);
        play(new FadeOut(memLabel, 0.6));
        remove(memLabel);

        for (int i = arrStart; i < arrStart + arrLen; i++) {
            play(new ColorChange(mem[i], Colors.BLUE, 0.2));
            mem[i].setFillColor(Colors.withAlpha(Colors.BLUE, 0.15));
        }
        hold(0.6);

        RectMob bracket = new RectMob(arrLen * cw - 4, 4);
        bracket.setFillColor(Colors.BLUE);
        bracket.setPosition(mem[arrStart].getPosition().x() + (arrLen - 1) * cw / 2.0, -40 - 23 - 12);
        bracket.setOpacity(0);
        add(bracket);
        play(new FadeIn(bracket, 0.6));

        TextMob arrLabelT = new TextMob("int arr[6]  —  contiguous: no gaps, no jumps between cells")
                .setFontSize(20).setFillColor(Colors.BLUE);
        arrLabelT.setPosition(0, -140);
        play(new Write(arrLabelT, 1.6));
        hold(1.6);
        play(new FadeOut(arrLabelT, 0.6));
        remove(arrLabelT);

        // Push in on the array slice before deriving the formula on it
        double arrCenterX = mem[arrStart].getPosition().x() + (arrLen - 1) * cw / 2.0;
        cameraTo(arrCenterX, -40, 1.8, 1.2);
        hold(0.8);

        int targetOff = 4;
        TextMob baseLbl = new TextMob("base").setFontSize(16).setFillColor(Colors.GREEN);
        baseLbl.setPosition(mem[arrStart].getPosition().x(), -40 - 23 - 28);
        play(new Write(baseLbl, 0.7), new ColorChange(mem[arrStart], Colors.GREEN, 0.4));
        hold(0.7);

        TextMob targetLbl = new TextMob("i = 4").setFontSize(16).setFillColor(Colors.GOLD);
        targetLbl.setPosition(mem[arrStart + targetOff].getPosition().x(), -40 - 23 - 28);
        play(new Write(targetLbl, 0.7), new ColorChange(mem[arrStart + targetOff], Colors.GOLD, 0.4));
        hold(0.8);

        resetCamera(1.2);
        hold(0.5);

        LaTeXMob formula = new LaTeXMob("\\text{addr}(arr[i]) = base + i \\times size")
                .setSize(30).setLatexColor(Colors.WHITE);
        formula.setPosition(0, -170);
        play(new Write(formula, 2.2));
        hold(1.6);

        LaTeXMob computed = new LaTeXMob("= base + 4 \\times 4 = base + 16")
                .setSize(28).setLatexColor(Colors.GOLD);
        computed.setPosition(0, -100);
        play(new Write(computed, 1.8));
        hold(2.0);

        TextMob keyInsight = new TextMob("No searching. No walking. Pure arithmetic — O(1).")
                .setFontSize(24).setBold().setFillColor(Colors.GREEN);
        keyInsight.setPosition(0, 200);
        play(new Write(keyInsight, 2.0));
        hold(2.4);

        fadeOutAll(0.9, heading, bracket, baseLbl, targetLbl, formula, computed, keyInsight);
        fadeOutAll(0.7, mem);
        fadeOutAll(0.7, addr);
        hold(0.5);
    }

    // ╔══════════════════════════════════════════════════════════╗
    // ║  3. ROW-MAJOR vs COLUMN-MAJOR (2D) — the matrix unrolls   ║
    // ╚══════════════════════════════════════════════════════════╝
    private void buildRowColumnMajor() {
        TextMob heading = sectionTitle("Row-Major vs Column-Major");
        play(new Write(heading, 1.6));
        hold(0.9);

        int rows = 3, cols = 3;
        double gcw = 92;
        double gx0 = -gcw;
        double gy0 = -230;

        RectMob[] gridCell = new RectMob[rows * cols];
        TextMob[] gridVal = new TextMob[rows * cols];
        int val = 1;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int k = r * cols + c;
                double x = gx0 + c * gcw;
                double y = gy0 + r * gcw;
                RectMob cell = new RectMob(gcw - 8, gcw - 8).setCornerRadius(6);
                cell.setStrokeColor(Colors.BLUE);
                cell.setFillColor(Colors.withAlpha(Colors.BLUE, 0.12));
                cell.setPosition(x, y);
                cell.setOpacity(0);
                gridCell[k] = cell;
                add(cell);

                TextMob t = new TextMob(String.valueOf(val++))
                        .setFontSize(28).setFillColor(Colors.WHITE);
                t.setPosition(x, y);
                t.setOpacity(0);
                gridVal[k] = t;
                add(t);
            }
        }
        for (int k = 0; k < rows * cols; k++) play(new FadeIn(gridCell[k], 0.1), new FadeIn(gridVal[k], 0.1));
        hold(1.0);

        TextMob matLabel = new TextMob("A 3×3 matrix — but memory is only ever 1D")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        matLabel.setPosition(0, -350);
        play(new Write(matLabel, 1.6));
        hold(1.5);
        play(new FadeOut(matLabel, 0.6));
        remove(matLabel);

        double scw = 68;
        double stripY = 260;
        RectMob[] slot = new RectMob[9];
        TextMob[] slotIdx = new TextMob[9];
        for (int i = 0; i < 9; i++) {
            double x = -((9 - 1) * scw) / 2.0 + i * scw;
            RectMob s = new RectMob(scw - 6, 50).setCornerRadius(3);
            s.setStrokeColor(Colors.withAlpha(Colors.GRAY, 0.5));
            s.setFillColor(Colors.withAlpha(Colors.GRAY, 0.04));
            s.setPosition(x, stripY);
            s.setOpacity(0);
            slot[i] = s;
            add(s);

            TextMob si = new TextMob(String.valueOf(i))
                    .setFontSize(13).setFillColor(Colors.DARK_GRAY);
            si.setPosition(x, stripY + 25 + 16);
            si.setOpacity(0);
            slotIdx[i] = si;
            add(si);
        }
        for (int i = 0; i < 9; i++) play(new FadeIn(slot[i], 0.06), new FadeIn(slotIdx[i], 0.06));
        hold(0.6);

        TextMob rowLabel = new TextMob("Row-major: walk each row left→right, then drop to the next row")
                .setFontSize(20).setFillColor(Colors.TEAL);
        rowLabel.setPosition(0, -350);
        play(new Write(rowLabel, 1.8));
        hold(1.0);

        TextMob[] landedRow = new TextMob[9];
        int slotI = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int k = r * cols + c;
                TextMob ghost = new TextMob(gridVal[k].getText())
                        .setFontSize(22).setFillColor(Colors.TEAL);
                ghost.setPosition(gridCell[k].getPosition());
                add(ghost);

                TextMob landed = new TextMob(gridVal[k].getText())
                        .setFontSize(22).setFillColor(Colors.TEAL);
                landed.setPosition(slot[slotI].getPosition());
                add(landed);
                landedRow[slotI] = landed;

                play(new ColorChange(slot[slotI], Colors.withAlpha(Colors.TEAL, 0.22), 0.18));
                play(new Transform(ghost, landed, 0.45));
                remove(ghost);
                slotI++;
            }
        }
        hold(0.8);

        LaTeXMob rowFormula = new LaTeXMob("\\text{addr}(a[r][c]) = base + (r \\times cols + c) \\times size")
                .setSize(24).setLatexColor(Colors.TEAL);
        rowFormula.setPosition(0, 350);
        play(new Write(rowFormula, 2.0));
        hold(2.0);

        fadeOutAll(0.7, rowLabel, rowFormula);
        fadeOutAll(0.6, landedRow);
        for (RectMob s : slot) play(new ColorChange(s, Colors.withAlpha(Colors.GRAY, 0.04), 0.15));
        hold(0.4);

        TextMob colLabel = new TextMob("Column-major: walk each column top→bottom, then move right")
                .setFontSize(20).setFillColor(Colors.PURPLE);
        colLabel.setPosition(0, -350);
        play(new Write(colLabel, 1.8));
        hold(1.0);

        TextMob[] landedCol = new TextMob[9];
        int slotJ = 0;
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                int k = r * cols + c;
                TextMob ghost = new TextMob(gridVal[k].getText())
                        .setFontSize(22).setFillColor(Colors.PURPLE);
                ghost.setPosition(gridCell[k].getPosition());
                add(ghost);

                TextMob landed = new TextMob(gridVal[k].getText())
                        .setFontSize(22).setFillColor(Colors.PURPLE);
                landed.setPosition(slot[slotJ].getPosition());
                add(landed);
                landedCol[slotJ] = landed;

                play(new ColorChange(slot[slotJ], Colors.withAlpha(Colors.PURPLE, 0.22), 0.18));
                play(new Transform(ghost, landed, 0.45));
                remove(ghost);
                slotJ++;
            }
        }
        hold(0.8);

        LaTeXMob colFormula = new LaTeXMob("\\text{addr}(a[r][c]) = base + (c \\times rows + r) \\times size")
                .setSize(24).setLatexColor(Colors.PURPLE);
        colFormula.setPosition(0, 350);
        play(new Write(colFormula, 2.0));
        hold(1.8);

        TextMob closing = new TextMob("Same matrix. Same data. Two entirely different memory orders.")
                .setFontSize(22).setBold().setFillColor(Colors.GOLD);
        closing.setPosition(0, 410);
        play(new Write(closing, 2.0));
        hold(2.2);

        fadeOutAll(0.9, heading, colLabel, colFormula, closing);
        fadeOutAll(0.6, gridCell);
        fadeOutAll(0.6, gridVal);
        fadeOutAll(0.6, slot);
        fadeOutAll(0.6, slotIdx);
        fadeOutAll(0.6, landedCol);
        hold(0.5);
    }

    // ╔══════════════════════════════════════════════════════════╗
    // ║  4. ROW-MAJOR vs COLUMN-MAJOR (3D) — a real cuboid unrolls ║
    // ╚══════════════════════════════════════════════════════════╝
    private void build3DRowColumnMajor() {
        TextMob heading = sectionTitle("Row-Major vs Column-Major — In 3D");
        play(new Write(heading, 1.6));
        hold(1.0);

        TextMob teaser = new TextMob("A matrix was 2D. What about a cube of numbers?")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        teaser.setPosition(0, -360);
        play(new Write(teaser, 1.8));
        hold(1.4);
        play(new FadeOut(teaser, 0.7));
        remove(teaser);

        int R = 3, C = 3, D = 2, N = R * C * D;
        double unit = 110;
        CubeGrid3D cube = new CubeGrid3D(R, C, D, unit);
        cube.setPosition(0, -110);
        cube.setBaseColor(Colors.BLUE);
        cube.setLabelFontSize(22);
        // Never start perfectly flat — at rotation (0,0) the two depth
        // layers' front faces land exactly on top of each other, and with
        // translucent faces that double-exposes their numbers. A small
        // resting tilt keeps every layer visibly offset from frame one.
        cube.setRotation(0.22, 0.14);
        int val = 1;
        for (int r = 0; r < R; r++)
            for (int c = 0; c < C; c++)
                for (int d = 0; d < D; d++)
                    cube.setValue(r, c, d, String.valueOf(val++));
        cube.setScale(0.2);
        add(cube);
        play(new FadeIn(cube, 1.2), new ScaleTo(cube, 1.0, 1.5).setEasing(Easing.EASE_OUT));
        hold(1.2);

        // Reveal the third dimension by orbiting the camera around it
        play(new RotateCube3D(cube, 0.65, 0.38, 2.6));
        hold(1.6);

        TextMob dims = new TextMob("rows (r) × cols (c) × depth (d) — three independent indices")
                .setFontSize(20).setFillColor(Colors.LIGHT_GRAY);
        dims.setPosition(0, 340);
        play(new Write(dims, 1.8));
        hold(1.8);

        // A slow orbit to really sell that this is a genuine 3D object
        play(new RotateCube3D(cube, -0.35, 0.5, 2.8));
        hold(0.8);
        play(new RotateCube3D(cube, 0.55, 0.3, 2.4));
        hold(1.2);

        // Highlight one index's "prefix": every complete layer above it,
        // plus everything earlier within its own layer — i.e. every cube
        // that sits before it in row-major memory order.
        int hr = 1, hc = 2, hd = 1;
        int targetIdx = (hr * C + hc) * D + hd;
        for (int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                for (int d = 0; d < D; d++) {
                    int idx = (r * C + c) * D + d;
                    if (idx < targetIdx) cube.setCubeColor(r, c, d, Colors.ORANGE);
                }
            }
        }
        cube.setCubeColor(hr, hc, hd, Colors.GOLD);

        TextMob idxCallout = new TextMob("cube[" + hr + "][" + hc + "][" + hd + "] = " + cube.getValue(hr, hc, hd))
                .setFontSize(24).setBold().setFillColor(Colors.GOLD);
        idxCallout.setPosition(0, -380);
        play(new Write(idxCallout, 1.3));
        hold(0.7);

        TextMob prefixNote = new TextMob(targetIdx + " cubes already sit in memory before it — watch them light up as we turn")
                .setFontSize(19).setFillColor(Colors.ORANGE);
        prefixNote.setPosition(0, 400);
        play(new Write(prefixNote, 1.7));
        hold(0.6);

        // A deliberate sweep, not a random spin: orbit far enough each way
        // that every highlighted cube — the layers above and the earlier
        // cubes within its own layer — gets its moment facing the camera.
        double sweepStart = cube.getRotY();
        play(new RotateCube3D(cube, sweepStart - 1.3, 0.5, 2.2));
        hold(0.9);
        play(new RotateCube3D(cube, sweepStart + 1.3, 0.4, 3.4));
        hold(1.1);
        play(new RotateCube3D(cube, sweepStart + 0.4, 0.38, 1.8));
        hold(0.9);

        play(new FadeOut(idxCallout, 0.7), new FadeOut(prefixNote, 0.7));
        remove(idxCallout);
        remove(prefixNote);
        for (int r = 0; r < R; r++)
            for (int c = 0; c < C; c++)
                for (int d = 0; d < D; d++)
                    cube.clearCubeColor(r, c, d);
        hold(0.5);

        fadeOutAll(0.7, dims);

        // ── Row-major: unroll the whole cuboid into one 1D memory strip ──
        TextMob rowLabel = new TextMob("Row-major: r slowest, d fastest — watch it unroll")
                .setFontSize(20).setFillColor(Colors.TEAL);
        rowLabel.setPosition(0, -360);
        play(new Write(rowLabel, 1.8));
        hold(1.0);

        double scw = 100;
        double stripY = 300;
        RectMob[] slot = new RectMob[N];
        for (int i = 0; i < N; i++) {
            double x = -((N - 1) * scw) / 2.0 + i * scw;
            RectMob s = new RectMob(scw - 10, 60).setCornerRadius(4);
            s.setStrokeColor(Colors.withAlpha(Colors.GRAY, 0.4));
            s.setFillColor(javafx.scene.paint.Color.TRANSPARENT);
            s.setPosition(x, stripY);
            s.setOpacity(0);
            slot[i] = s;
            add(s);
        }
        for (RectMob s : slot) play(new FadeIn(s, 0.03));
        hold(0.6);

        Map<CubeGrid3D.Idx, Vec3> rowTargets = new HashMap<>();
        int slotI = 0;
        for (int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                for (int d = 0; d < D; d++) {
                    double slotX = slot[slotI].getPosition().x();
                    double slotY = slot[slotI].getPosition().y();
                    Vec3 targetLocal = new Vec3(slotX - cube.getPosition().x(), slotY - cube.getPosition().y(), 0);
                    rowTargets.put(new CubeGrid3D.Idx(r, c, d), targetLocal.sub(cube.cubeCenter(r, c, d)));
                    slotI++;
                }
            }
        }
        play(new RotateCube3D(cube, 0, 0, 2.2), new FlattenCube3D(cube, rowTargets, 2.2));
        hold(1.2);

        LaTeXMob rowFormula = new LaTeXMob("\\text{addr} = base + (r \\times C \\times D + c \\times D + d) \\times size")
                .setSize(24).setLatexColor(Colors.TEAL);
        rowFormula.setPosition(0, 400);
        play(new Write(rowFormula, 2.0));
        hold(2.0);

        LaTeXMob rowHorner = new LaTeXMob("= base + \\big((r \\times C + c) \\times D + d\\big) \\times size")
                .setSize(22).setLatexColor(Colors.TEAL);
        rowHorner.setPosition(0, 445);
        play(new Write(rowHorner, 1.8));
        hold(2.0);

        fadeOutAll(0.7, rowLabel, rowFormula, rowHorner);
        hold(0.3);

        // ── Column-major: pull the cube back together, unroll differently ──
        Map<CubeGrid3D.Idx, Vec3> zeroTargets = new HashMap<>();
        for (int r = 0; r < R; r++)
            for (int c = 0; c < C; c++)
                for (int d = 0; d < D; d++)
                    zeroTargets.put(new CubeGrid3D.Idx(r, c, d), Vec3.ZERO);
        play(new RotateCube3D(cube, 0.5, 0.35, 1.8), new FlattenCube3D(cube, zeroTargets, 1.8));
        hold(1.2);

        TextMob colLabel = new TextMob("Column-major: r fastest, d slowest — a different unrolling")
                .setFontSize(20).setFillColor(Colors.PURPLE);
        colLabel.setPosition(0, -360);
        play(new Write(colLabel, 1.8));
        hold(1.0);

        Map<CubeGrid3D.Idx, Vec3> colTargets = new HashMap<>();
        int slotJ = 0;
        for (int d = 0; d < D; d++) {
            for (int c = 0; c < C; c++) {
                for (int r = 0; r < R; r++) {
                    double slotX = slot[slotJ].getPosition().x();
                    double slotY = slot[slotJ].getPosition().y();
                    Vec3 targetLocal = new Vec3(slotX - cube.getPosition().x(), slotY - cube.getPosition().y(), 0);
                    colTargets.put(new CubeGrid3D.Idx(r, c, d), targetLocal.sub(cube.cubeCenter(r, c, d)));
                    slotJ++;
                }
            }
        }
        play(new RotateCube3D(cube, 0, 0, 2.2), new FlattenCube3D(cube, colTargets, 2.2));
        hold(1.2);

        LaTeXMob colFormula = new LaTeXMob("\\text{addr} = base + (d \\times C \\times R + c \\times R + r) \\times size")
                .setSize(24).setLatexColor(Colors.PURPLE);
        colFormula.setPosition(0, 400);
        play(new Write(colFormula, 2.0));
        hold(2.0);

        TextMob closingNote = new TextMob("Same cube. Same 18 numbers. Two completely different memory orders.")
                .setFontSize(22).setBold().setFillColor(Colors.GOLD);
        closingNote.setPosition(0, 450);
        play(new Write(closingNote, 1.8));
        hold(2.2);

        fadeOutAll(0.8, heading, colLabel, colFormula, closingNote);
        fadeOutAll(0.6, cube);
        fadeOutAll(0.6, slot);
        hold(0.5);

        // ── Bridge to n dimensions: the labels literally become their shapes ──
        TextMob d1 = new TextMob("1D").setFontSize(32).setBold().setFillColor(Colors.LIGHT_BLUE);
        d1.setPosition(-320, -140);
        play(new Write(d1, 0.9));
        hold(0.5);

        RectMob line = new RectMob(150, 24).setCornerRadius(5);
        line.setFillColor(Colors.withAlpha(Colors.LIGHT_BLUE, 0.5));
        line.setStrokeColor(Colors.LIGHT_BLUE);
        line.setPosition(-320, -140);
        add(line);
        play(new Transform(d1, line, 0.9));
        remove(d1);
        hold(0.6);

        TextMob d2 = new TextMob("2D").setFontSize(32).setBold().setFillColor(Colors.TEAL);
        d2.setPosition(0, -140);
        play(new Write(d2, 0.9));
        hold(0.5);

        RectMob square = new RectMob(110, 95).setCornerRadius(7);
        square.setFillColor(Colors.withAlpha(Colors.TEAL, 0.35));
        square.setStrokeColor(Colors.TEAL);
        square.setPosition(0, -140);
        add(square);
        play(new Transform(d2, square, 0.9));
        remove(d2);
        hold(0.6);

        TextMob d3 = new TextMob("3D").setFontSize(32).setBold().setFillColor(Colors.GOLD);
        d3.setPosition(320, -140);
        play(new Write(d3, 0.9));
        hold(0.5);

        CubeGrid3D miniCube = new CubeGrid3D(2, 2, 2, 42);
        miniCube.setBaseColor(Colors.GOLD);
        miniCube.setShowLabels(false);
        miniCube.setPosition(320, -140);
        miniCube.setRotation(0.5, 0.35);
        add(miniCube);
        play(new Transform(d3, miniCube, 1.0));
        remove(d3);
        hold(0.5);
        play(new RotateCube3D(miniCube, 1.7, 0.5, 1.8));
        hold(0.8);

        TextMob genLabel = new TextMob("The pattern generalizes to any number of dimensions:")
                .setFontSize(22).setFillColor(Colors.LIGHT_GRAY);
        genLabel.setPosition(0, 60);
        play(new Write(genLabel, 1.8));
        hold(1.4);

        LaTeXMob genFormula = new LaTeXMob(
                "\\text{addr}(i_0,\\dots,i_{k-1}) = base + \\Big(\\big((i_0 n_1 + i_1) n_2 + i_2\\big) \\cdots + i_{k-1}\\Big) \\times size")
                .setSize(24).setLatexColor(Colors.WHITE);
        genFormula.setPosition(0, 135);
        play(new Write(genFormula, 2.4));
        hold(2.4);

        TextMob genNote = new TextMob("Horner's rule — one nested multiply per extra dimension, same idea for any k.")
                .setFontSize(20).setFillColor(Colors.GREEN);
        genNote.setPosition(0, 200);
        play(new Write(genNote, 2.0));
        hold(2.4);

        fadeOutAll(1.0, line, square, miniCube, genLabel, genFormula, genNote);
        hold(0.5);
    }

    // ╔══════════════════════════════════════════════════════════╗
    // ║  4. SEARCH RACE — linear vs binary, same target           ║
    // ╚══════════════════════════════════════════════════════════╝
    private void buildSearchRace() {
        TextMob heading = sectionTitle("Search: Linear vs Binary");
        play(new Write(heading, 1.5));
        hold(0.8);

        int[] vals = {2, 5, 9, 12, 17, 23, 31, 38, 45, 52, 60, 71};
        int target = 71;

        ArrayMob linArr = new ArrayMob(vals.clone());
        linArr.setCellSize(62, 46);
        linArr.setLabel("Linear search");
        linArr.setPosition(0, -190);

        ArrayMob binArr = new ArrayMob(vals.clone());
        binArr.setCellSize(62, 46);
        binArr.setLabel("Binary search");
        binArr.setPosition(0, 80);

        play(new FadeIn(linArr, 1.0), new FadeIn(binArr, 1.0));
        hold(0.8);

        TextMob goal = new TextMob("Find 71 — who gets there in fewer comparisons?")
                .setFontSize(22).setFillColor(Colors.GOLD);
        goal.setPosition(0, -340);
        play(new Write(goal, 1.7));
        hold(1.0);

        TextMob linCount = new TextMob("comparisons: 0").setFontSize(18).setFillColor(Colors.LIGHT_GRAY);
        linCount.setPosition(520, -190);
        TextMob binCount = new TextMob("comparisons: 0").setFontSize(18).setFillColor(Colors.LIGHT_GRAY);
        binCount.setPosition(520, 80);
        add(linCount);
        add(binCount);
        play(new FadeIn(linCount, 0.5), new FadeIn(binCount, 0.5));
        hold(0.6);

        // Linear search — check every cell in order
        int linSteps = 0;
        for (int i = 0; i < vals.length; i++) {
            linArr.setPointer(i, "i");
            linArr.setCellColor(i, Colors.withAlpha(Colors.ORANGE, 0.35));
            linSteps++;
            linCount.setText("comparisons: " + linSteps);
            hold(0.24);
            if (vals[i] == target) {
                linArr.setCellColor(i, Colors.withAlpha(Colors.GREEN, 0.4));
                break;
            }
            linArr.clearCellColorAt(i);
        }
        linArr.hidePointer();
        hold(1.0);

        // Binary search — shrink the window each step
        int lo = 0, hi = vals.length - 1, binSteps = 0, foundIdx = -1;
        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            for (int k = lo; k <= hi; k++) binArr.setCellColor(k, Colors.withAlpha(Colors.BLUE, 0.18));
            binArr.setPointer(mid, "mid");
            binArr.setCellColor(mid, Colors.withAlpha(Colors.ORANGE, 0.4));
            binSteps++;
            binCount.setText("comparisons: " + binSteps);

            // Push in on the shrinking window so it reads as genuinely narrowing
            Vec2 winCenter = binArr.getPosition().add(
                    binArr.getCellCenter(lo).lerp(binArr.getCellCenter(hi), 0.5));
            double zoom = 1.0 + 0.9 * (1.0 - (hi - lo + 1) / (double) vals.length);
            cameraTo(winCenter.x(), winCenter.y(), zoom, 0.5);
            hold(0.85);

            if (vals[mid] == target) {
                binArr.setCellColor(mid, Colors.withAlpha(Colors.GREEN, 0.4));
                foundIdx = mid;
                break;
            } else if (vals[mid] < target) {
                for (int k = lo; k <= mid; k++) binArr.clearCellColorAt(k);
                lo = mid + 1;
            } else {
                for (int k = mid; k <= hi; k++) binArr.clearCellColorAt(k);
                hi = mid - 1;
            }
        }
        binArr.hidePointer();
        hold(0.8);

        if (foundIdx >= 0) {
            Vec2 focus = binArr.getPosition().add(binArr.getCellCenter(foundIdx));
            cameraTo(focus.x(), focus.y(), 2.2, 0.9);
            hold(1.6);
            resetCamera(1.0);
        }
        hold(0.4);

        TextMob verdict = new TextMob("linear: " + linSteps + " comparisons   vs   binary: " + binSteps
                + " comparisons — binary wins by a mile")
                .setFontSize(22).setBold().setFillColor(Colors.GOLD);
        verdict.setPosition(0, 340);
        play(new Write(verdict, 2.2));
        hold(2.4);

        fadeOutAll(1.0, heading, linArr, binArr, goal, linCount, binCount, verdict);
        hold(0.6);
    }

    // ╔══════════════════════════════════════════════════════════╗
    // ║  5. BUBBLE SORT — real positional swaps, not teleports    ║
    // ╚══════════════════════════════════════════════════════════╝
    private void buildBubbleSortReal() {
        TextMob heading = sectionTitle("Bubble Sort — Watch The Swaps");
        play(new Write(heading, 1.5));
        hold(0.8);

        int[] vals = {8, 3, 9, 1, 6, 2};
        ArrayMob arr = new ArrayMob(vals.clone());
        arr.setCellSize(90, 60);
        arr.setPosition(0, -40);
        play(new FadeIn(arr, 1.0));
        hold(0.9);

        TextMob passLbl = new TextMob("Pass 1").setFontSize(20).setFillColor(Colors.LIGHT_GRAY);
        passLbl.setPosition(0, -220);
        add(passLbl);

        TextMob swapsLbl = new TextMob("swaps: 0").setFontSize(18).setFillColor(Colors.GRAY);
        swapsLbl.setPosition(0, 180);
        add(swapsLbl);
        play(new FadeIn(passLbl, 0.5), new FadeIn(swapsLbl, 0.5));
        hold(0.4);

        int n = vals.length;
        int swapCount = 0;
        int[] work = vals.clone();
        for (int p = 0; p < n - 1; p++) {
            passLbl.setText("Pass " + (p + 1));
            boolean swappedAny = false;
            for (int i = 0; i < n - 1 - p; i++) {
                arr.setCellColor(i, Colors.withAlpha(Colors.BLUE, 0.3));
                arr.setCellColor(i + 1, Colors.withAlpha(Colors.BLUE, 0.3));
                hold(0.4);
                if (work[i] > work[i + 1]) {
                    // Zoom onto the pair actually swapping — the camera sells the motion
                    Vec2 pairMid = arr.getPosition().add(
                            arr.getCellCenter(i).lerp(arr.getCellCenter(i + 1), 0.5));
                    cameraTo(pairMid.x(), pairMid.y(), 1.6, 0.3);
                    play(new SwapCells(arr, i, i + 1, 0.6));
                    int tmp = work[i];
                    work[i] = work[i + 1];
                    work[i + 1] = tmp;
                    swapCount++;
                    swapsLbl.setText("swaps: " + swapCount);
                    swappedAny = true;
                    hold(0.15);
                    resetCamera(0.3);
                }
                arr.clearCellColorAt(i);
                arr.clearCellColorAt(i + 1);
            }
            arr.setCellColor(n - 1 - p, Colors.withAlpha(Colors.GREEN, 0.35));
            hold(0.3);
            if (!swappedAny) break;
        }
        arr.setCellColor(0, Colors.withAlpha(Colors.GREEN, 0.35));
        hold(1.4);

        TextMob done = new TextMob("Sorted — the largest unsorted value bubbles to the end, every pass")
                .setFontSize(22).setFillColor(Colors.GREEN);
        done.setPosition(0, 280);
        play(new Write(done, 2.0));
        hold(2.4);

        fadeOutAll(1.1, heading, arr, passLbl, swapsLbl, done);
        hold(0.6);
    }

    // ╔══════════════════════════════════════════════════════════╗
    // ║  6. CLOSING — return to the opening puzzle                ║
    // ╚══════════════════════════════════════════════════════════╝
    private void buildClosing() {
        TextMob heading = sectionTitle("The Big Idea");
        play(new Write(heading, 1.6));
        hold(0.9);

        LaTeXMob f = new LaTeXMob("\\text{addr}(arr[i]) = base + i \\times size")
                .setSize(34).setLatexColor(Colors.GOLD);
        f.setPosition(0, -60);
        play(new Write(f, 2.0));
        hold(1.6);

        // One last slow push-in on the formula that anchored the whole video
        cameraTo(0, -60, 1.5, 1.0);
        hold(1.2);
        resetCamera(1.0);
        hold(0.4);

        TextMob line = new TextMob("Contiguous memory + one multiplication = instant access.")
                .setFontSize(24).setFillColor(Colors.WHITE);
        line.setPosition(0, 40);
        play(new Write(line, 1.9));
        hold(1.4);

        TextMob line2 = new TextMob("Row/column order, search, sorting — all built on this one fact.")
                .setFontSize(20).setFillColor(Colors.LIGHT_GRAY);
        line2.setPosition(0, 90);
        play(new Write(line2, 2.0));
        hold(2.6);

        fadeOutAll(1.4, heading, f, line, line2);

        TextMob thanks = new TextMob("LecViz").setFontSize(40).setBold().setFillColor(Colors.BLUE);
        thanks.setPosition(0, -20);
        thanks.setScale(0.5);
        play(new FadeIn(thanks, 1.3), new ScaleTo(thanks, 1.0, 1.5));
        hold(0.4);
        TextMob thanks2 = new TextMob("CS5013 · Karthik & Tejaswi")
                .setFontSize(20).setFillColor(Colors.GRAY);
        thanks2.setPosition(0, 40);
        play(new Write(thanks2, 1.5));
        hold(2.8);

        fadeOutAll(1.5, thanks, thanks2);
    }
}
