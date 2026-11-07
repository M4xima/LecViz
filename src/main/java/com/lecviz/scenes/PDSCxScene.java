package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * Clips for the complexity deck (2-complexity.pdf, 27 slides), one clip per sub-topic, named by slide numbers:
 *
 *   1to3    title card; algorithms; algorithm efficiency
 *   4       the misconceptions about running time
 *   5to7    examples: counted operations of loops and recursion, constant and linear time
 *   8to9    the definition of big O, drawn: T(N), c f(N) and the point n0 (n0 is computed)
 *   10to11  the examples with their classes; solving the recurrence of fun(n)
 *   12to13  the family of symbols; which bounds hold for T(n) = n (checked numerically)
 *   14      complexity arithmetic: the classwork answered with counted loops
 *   15to16   typical complexities, their growth curves, the homework and the limit test
 *   17      facets of efficiency
 *   18to19   maximum subsequence sum and the exhaustive algorithm, run on an array
 *   20to22   the tight count of algorithm 1, checked against the formula
 *   23      algorithm 2
 *   24      algorithm 3
 *   25to26   binary search and its recurrence
 *   27      the exercises and the closing card
 *
 * Counts, thresholds and ratios are computed by running the code or the numbers, not typed in.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSCxScene extends PDSGraphClipBase {

    private final String part;

    public PDSCxScene(String part) { this.part = part; }

    @Override
    public void construct() {
        switch (part) {
            case "1to3": c1to3(); break;
            case "4": c4(); break;
            case "5to7": c5to7(); break;
            case "8to9": c8to9(); break;
            case "10to11": c10to11(); break;
            case "12to13": c12to13(); break;
            case "14": c14(); break;
            case "15to16": c15to16(); break;
            case "17": c17(); break;
            case "18to19": c18to19(); break;
            case "20to22": c20to22(); break;
            case "23": c23(); break;
            case "24": c24(); break;
            case "25to26": c25to26(); break;
            default: c27(); break;
        }
        unsay();
        fadeOutAll(d(1.2), mine);
        if (head != null) fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    // ── helpers ──────────────────────────────────────────────────────

    private static final Color[] PAL = {Colors.BLUE, Colors.GOLD, Colors.PINK, Colors.GREEN, Colors.PURPLE, Colors.ORANGE, Colors.TEAL, Colors.RED};

    /** Axes with two labels; curves are drawn with curve(). */
    private final class Plot {
        final double x0, y0, w, h, xmax, ymax;
        double lastX, lastY;

        Plot(double x0, double y0, double w, double h, double xmax, double ymax, String xl, String yl) {
            this.x0 = x0;
            this.y0 = y0;
            this.w = w;
            this.h = h;
            this.xmax = xmax;
            this.ymax = ymax;
            Link ax = new Link(new double[]{x0, x0 + w + 16}, new double[]{y0, y0}, Colors.LIGHT_GRAY, 3, true);
            Link ay = new Link(new double[]{x0, x0}, new double[]{y0, y0 - h - 16}, Colors.LIGHT_GRAY, 3, true);
            add(ax);
            add(ay);
            mine.add(ax);
            mine.add(ay);
            List<Animation> a = new ArrayList<>();
            a.add(new DrawLinkAt(ax, 0, d(0.6)));
            a.add(new DrawLinkAt(ay, 0, d(0.6)));
            TextMob tx = label(xl, x0 + w - 10, y0 + 34, 24, Colors.LIGHT_GRAY, false, false);
            TextMob ty = label(yl, x0 + 60, y0 - h - 24, 24, Colors.LIGHT_GRAY, false, false);
            mine.add(tx);
            mine.add(ty);
            a.add(new FadeIn(tx, d(0.6)));
            a.add(new FadeIn(ty, d(0.6)));
            playAll(a);
        }

        double px(double x) { return x0 + x / xmax * w; }

        double py(double y) { return y0 - Math.min(y, ymax) / ymax * h; }

        /** Draws f from..to as a polyline, stopping where it leaves the picture. */
        Link curve(DoubleUnaryOperator f, double from, double to, Color c, double lw) {
            List<double[]> pts = new ArrayList<>();
            int steps = 160;
            double px0 = 0, py0 = 0;
            boolean have = false;
            for (int i = 0; i <= steps; i++) {
                double x = from + (to - from) * i / steps, y = f.applyAsDouble(x);
                if (y > ymax) {
                    if (have) {
                        double t = (ymax - py0) / (y - py0);
                        pts.add(new double[]{px0 + (x - px0) * t, ymax});
                    }
                    break;
                }
                pts.add(new double[]{x, y});
                px0 = x;
                py0 = y;
                have = true;
            }
            double[] xs = new double[pts.size()], ys = new double[pts.size()];
            for (int i = 0; i < xs.length; i++) {
                xs[i] = px(pts.get(i)[0]);
                ys[i] = py(pts.get(i)[1]);
            }
            lastX = xs[xs.length - 1];
            lastY = ys[ys.length - 1];
            Link l = new Link(xs, ys, c, lw, false);
            add(l);
            mine.add(l);
            playAll(java.util.Collections.singletonList(new DrawLinkAt(l, 0, d(1.0))));
            return l;
        }

        TextMob endLabel(String s, Color c) {
            TextMob t = label(s, lastX + 8, lastY, 26, c, true, true);
            mine.add(t);
            play(new FadeIn(t, d(0.4)));
            return t;
        }
    }

    private static double lg(double x) { return Math.log(x) / Math.log(2); }

    /** Counts up a number tag, quickly. */
    private void countUp(TextMob t, String prefix, int total) {
        int steps = Math.min(total, 16);
        for (int i = 1; i <= steps; i++) {
            int v = (int) Math.round(total * (double) i / steps);
            t.setText(prefix + v);
            pause(0.12);
        }
    }

    private static int calls(int n, boolean le1) {
        if (le1 ? n <= 1 : n == 0) return 1;
        return 1 + calls(n / 3, le1);
    }

    private TextMob row(String s, double x, double y, double size, Color c, boolean left) {
        TextMob t = label(s, x, y, size, c, left, true);
        mine.add(t);
        play(new FadeIn(t, d(0.35)));
        return t;
    }

    // ── 1-3 ──────────────────────────────────────────────────────────

    private void c1to3() {
        StrokeTextMob title = stroke("Complexity", 0, -420, 104, Colors.WHITE, true);
        mine.add(title);
        play(new Write(title, d(2.0)));
        Plot p = new Plot(-330, 80, 660, 330, 20, 100, "N", "time");
        p.curve(x -> x, 0, 20, Colors.BLUE, 5);
        p.endLabel("N", Colors.BLUE);
        p.curve(x -> x <= 1 ? 0 : x * lg(x), 0, 20, Colors.GOLD, 5);
        p.endLabel("N log N", Colors.GOLD);
        p.curve(x -> x * x / 4.0, 0, 20, Colors.PINK, 5);
        p.endLabel("N\u00b2", Colors.PINK);
        TextMob who = label("Rupesh Nasre.", 0, 230, 46, Colors.WHITE, false, false);
        TextMob mail = label("rupesh@cse.iitm.ac.in", 0, 288, 34, Colors.BLUE, false, false);
        TextMob when = label("August 2021", 0, 410, 30, Colors.GRAY, false, false);
        mine.add(who);
        mine.add(mail);
        mine.add(when);
        List<Animation> a = new ArrayList<>();
        a.add(new FadeIn(who, d(0.8)));
        a.add(new FadeInAt(mail, 0.3, d(0.8)));
        a.add(new FadeInAt(when, 0.6, d(0.8)));
        playAll(a);
        pause(2.4);
        clearMine();

        head = writeHeading("Algorithms");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "For the same problem, there could be multiple"));
        s.add(ln(3, "algorithms."));
        s.add(ln(0, "An algorithm is a clearly specified sequence of"));
        s.add(ln(3, "simple instructions that solve a given problem."));
        s.add(ln(1, "An algorithm, by definition, terminates."));
        s.add(ln(1, "Otherwise, the sequence of instructions constitutes a"));
        s.add(ln(2, "procedure."));
        s.add(ln(0, "The algorithm should be so clear to you that you"));
        s.add(ln(3, "should be able to make a machine understand it."));
        s.add(ln(1, "This is called programming."));
        keep(writeSlide(s, -380));
        pause(1.0);
        // one problem, several algorithms
        int m = mark();
        List<MObject> prob = chip("sort 5 numbers", 380, -60, 220, 60, Colors.BLUE, 26);
        List<MObject> a1 = chip("bubble sort", 700, -220, 200, 56, Colors.GOLD, 26);
        List<MObject> a2 = chip("insertion sort", 700, -60, 200, 56, Colors.PINK, 26);
        List<MObject> a3 = chip("merge sort", 700, 100, 200, 56, Colors.GREEN, 26);
        List<MObject> sorted = chip("sorted", 880, -60, 150, 56, Colors.TEAL, 26);
        mine.addAll(prob);
        mine.addAll(a1);
        mine.addAll(a2);
        mine.addAll(a3);
        mine.addAll(sorted);
        List<Animation> in = new ArrayList<>();
        fade(in, prob, d(0.5));
        playAll(in);
        in = new ArrayList<>();
        fade(in, a1, d(0.5));
        fade(in, a2, d(0.5));
        fade(in, a3, d(0.5));
        playAll(in);
        Link l1 = arrow(490, -78, 598, -200, Colors.LIGHT_GRAY, 3);
        Link l2 = arrow(490, -60, 598, -60, Colors.LIGHT_GRAY, 3);
        Link l3 = arrow(490, -42, 598, 82, Colors.LIGHT_GRAY, 3);
        mine.add(l1);
        mine.add(l2);
        mine.add(l3);
        playAll(java.util.Arrays.asList(new DrawLinkAt(l1, 0, d(0.5)), new DrawLinkAt(l2, 0, d(0.5)), new DrawLinkAt(l3, 0, d(0.5))));
        in = new ArrayList<>();
        fade(in, sorted, d(0.5));
        playAll(in);
        say("One problem, several algorithms: all give the same answer, but not all are equally good.", Colors.LIGHT_GRAY);
        pause(3.0);
        dropSince(m, d(0.7));
        unsay();
        newHeading("Algorithm Efficiency");
        clearMine();
        List<Spec> t = new ArrayList<>();
        t.add(ln(0, "For the same problem, there could be multiple"));
        t.add(ln(3, "algorithms."));
        t.add(ln(0, "We prefer the ones that run fast."));
        t.add(ln(1, "I don't want an algorithm that takes a year to sort!"));
        t.add(ln(1, "By the way, there are computations that run for months!"));
        t.add(ln(1, "Operating systems on servers may run for years."));
        t.add(ln(0, "We would like to compare algorithms based on"));
        t.add(ln(3, "their speeds."));
        t.add(ln(1, "Mathematical model to capture algorithm efficiency."));
        keep(writeSlide(t, -380));
        CodeBox cb = code(new String[]{"[rupesh@aqua ~]$ uptime", " 17:15:41 up 228 days, 3:50, 155 users,", " load average: 0.21, 0.22, 0.26"}, 40, 150, 24, 40, 2.4);
        say("A server can stay up for 228 days: speed is about how the running time grows, not about one run.", Colors.LIGHT_GRAY);
        pause(3.4);
    }

    // ── 4 ────────────────────────────────────────────────────────────

    private void c4() {
        head = writeHeading("Misconceptions");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Program P1 takes 10 seconds, P2 takes 20"));
        s.add(ln(3, "seconds, so I would choose P1."));
        s.add(ln(1, "Execution time is input-dependent."));
        s.add(ln(1, "Execution time is hardware-dependent."));
        s.add(ln(1, "Execution time is machine-load dependent."));
        s.add(ln(1, "Execution time is run-dependent too!"));
        s.add(ln(1, "Other factors play a role; for instance:"));
        s.add(ln(2, "whether the program is running in hostel or in DCF"));
        s.add(ln(2, "Or whether in Chennai or Kashmir"));
        s.add(ln(2, "Or whether in May or December!"));
        keep(writeSlide(s, -380));
        pause(1.0);
        code(new String[]{"$ time ls >/dev/null", "real 0m0.002s", "user 0m0.000s", "sys  0m0.001s"}, 150, -330, 26, 44, 2.0);
        pause(0.5);
        code(new String[]{"// on another machine", "$ time ls >/dev/null", "real 0m0.005s", "user 0m0.000s", "sys  0m0.004s"}, 150, -60, 26, 44, 2.4);
        say("The same command on two machines: 0.002 s and 0.005 s. Seconds say little about the algorithm itself.", Colors.GOLD);
        pause(3.6);
    }

    // ── 5-7 ──────────────────────────────────────────────────────────

    private void snippet(String[] lines, String what, int total, String note) {
        int m = mark();
        CodeBox cb = code(lines, -900, -280, 30, 54, 2.2);
        TextMob cnt = tag(what + "0", 300, -200, 40, Colors.GOLD, true);
        pause(0.4);
        countUp(cnt, what, total);
        pause(0.4);
        tag(note, 300, -120, 30, Colors.WHITE, true);
        pause(2.6);
        dropSince(m, d(0.7));
    }

    private void c5to7() {
        head = writeHeading("Examples");
        pause(0.5);
        say("Count what the machine really does.", Colors.LIGHT_GRAY);
        snippet(new String[]{"a = a + b;", "b = a - b;", "a = a - b;"}, "instructions executed: ", 3,
                "Irrespective of a and b: three instructions.");
        int n = 6;
        snippet(new String[]{"for (i = 0; i < N; ++i)", "    a[i] = 0;"}, "executions of a[i] = 0: ", n,
                "With N = " + n + ": " + n + ". Proportional to N.");
        int nn = 4, mm = 3;
        snippet(new String[]{"for (i = 0; i < N; ++i)", "    for (j = 0; j < M; ++j)", "        mat[i][j] = i + j;"}, "executions of the body: ", nn * mm,
                "With N = " + nn + ", M = " + mm + ": " + nn * mm + ". Proportional to N*M.");
        int c81 = calls(81, false);
        snippet(new String[]{"int fun(int n) {", "    return (n == 0 ? 1 : 4 * fun(n / 3));", "}"}, "calls of fun: ", c81,
                "fun(81): 81, 27, 9, 3, 1, 0: " + c81 + " calls");
        unsay();
        // constant time
        clearMine();
        say("Constant-time pieces: the work does not change when N grows.", Colors.LIGHT_GRAY);
        String[] code6 = {"a = a + b; b = a - b; a = a - b;", "a[i] = 0;", "if (x > 0) y = x + 1; else z = x + 1;", "for (i = 0; i < 1000; ++i) a[i] = 0;"};
        int[] c10 = {3, 1, 2, 1000}, c1000 = {3, 1, 2, 1000};
        row("code", -880, -330, 28, Colors.GRAY, true);
        row("N = 10", 330, -330, 28, Colors.GRAY, true);
        row("N = 1000", 560, -330, 28, Colors.GRAY, true);
        for (int i = 0; i < code6.length; i++) {
            row(code6[i], -880, -270 + i * 56, 26, Colors.WHITE, true);
            row(String.valueOf(c10[i]), 330, -270 + i * 56, 26, Colors.GOLD, true);
            row(String.valueOf(c1000[i]), 560, -270 + i * 56, 26, Colors.GOLD, true);
        }
        pause(1.0);
        row("All O(1)", 720, -150, 40, Colors.GREEN, true);
        say("All equally efficient: they perform constant-time operations. We write O(1).", Colors.GREEN);
        pause(3.2);
        clearMine();
        // linear time
        say("Linear-time pieces: the work grows in step with n.", Colors.LIGHT_GRAY);
        int[] ns = {10, 1000};
        String[] code7 = {"a[0] = 0; a[1] = 0; ... a[n-5] = 0;", "fact(n): n * fact(n - 1) until n = 0", "for (i = 0; i < 2*n; ++i) a[i] = 0;"};
        row("code", -880, -330, 28, Colors.GRAY, true);
        row("n = 10", 380, -330, 28, Colors.GRAY, true);
        row("n = 1000", 600, -330, 28, Colors.GRAY, true);
        for (int i = 0; i < 3; i++) {
            row(code7[i], -880, -270 + i * 56, 26, Colors.WHITE, true);
            for (int k = 0; k < 2; k++) {
                int v = i == 0 ? ns[k] - 4 : i == 1 ? ns[k] + 1 : 2 * ns[k];
                row(String.valueOf(v), 380 + k * 220, -270 + i * 56, 26, Colors.GOLD, true);
            }
        }
        pause(1.0);
        row("All O(n)", 720, -150, 40, Colors.GREEN, true);
        say("All equally efficient: linear in n. We write O(n).", Colors.GREEN);
        pause(3.2);
    }

    // ── 8-9 ──────────────────────────────────────────────────────────

    private void c8to9() {
        head = writeHeading("Definition");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "T(N) = O(1) if T(N) \u2264 c when N \u2265 n0, for some"));
        s.add(ln(3, "positive c and n0."));
        s.add(ln(0, "T(N) = O(N) if T(N) \u2264 cN when N \u2265 n0, for some"));
        s.add(ln(3, "positive c and n0."));
        s.add(ln(0, "In general,"));
        s.add(ln(3, "T(N) = O(f(N)) if there exist positive constants"));
        s.add(ln(3, "c and n0 such that T(N) \u2264 cf(N) when N \u2265 n0."));
        s.add(ln(0, "Complexity captures the rate of growth of a"));
        s.add(ln(3, "function."));
        List<List<MObject>> slide = writeSlide(s, -380);
        pause(1.6);
        swipeAway(slide);
        pause(0.4);
        newHeading("Big O");
        show("T(N)=O(f(N))\\iff \\exists\\,c,n_0>0:\\ T(N)\\le c\\,f(N)\\ \\ \\forall N\\ge n_0", 40, 0, -350);
        Plot p = new Plot(-420, 270, 840, 480, 12, 720, "N", "time");
        DoubleUnaryOperator t = x -> 3 * x * x + 10 * x + 5;
        double c = 5;
        DoubleUnaryOperator cf = x -> c * x * x;
        p.curve(cf, 0, 12, Colors.GOLD, 5);
        p.endLabel("c\u00b7f(N)", Colors.GOLD);
        p.curve(t, 0, 12, Colors.BLUE, 5);
        p.endLabel("T(N)", Colors.BLUE);
        say("Here T(N) = 3N\u00b2 + 10N + 5 and f(N) = N\u00b2. With c = 5, where does T(N) stay below c\u00b7f(N)?", Colors.LIGHT_GRAY);
        pause(2.0);
        int n0 = -1;
        for (int n = 0; n <= 100000; n++) {
            boolean ok = true;
            for (int k = n; k <= n + 200; k++) if (t.applyAsDouble(k) > cf.applyAsDouble(k)) {
                ok = false;
                break;
            }
            if (ok) {
                n0 = n;
                break;
            }
        }
        double vx = p.px(n0);
        Link v = new Link(new double[]{vx, vx}, new double[]{p.y0, p.y0 - p.h}, Colors.GREEN, 3, false);
        add(v);
        mine.add(v);
        playAll(java.util.Collections.singletonList(new DrawLinkAt(v, 0, d(0.8))));
        TextMob nl = row("n0 = " + n0, vx, p.y0 + 40, 30, Colors.GREEN, false);
        say("From n0 = " + n0 + " on, T(N) \u2264 5N\u00b2 forever: T(N) = O(N\u00b2). Before n0 it does not matter.", Colors.GREEN);
        pause(2.8);
        say("The complexity is upper-bounded by c\u00b7f(N): big O describes the worst case.", Colors.GOLD);
        pause(3.2);
    }

    // ── 10-11 ────────────────────────────────────────────────────────

    private void c10to11() {
        head = writeHeading("Examples");
        pause(0.5);
        String[] code = {"a = a + b; b = a - b; a = a - b;", "for (i = 0; i < N; ++i) a[i] = 0;",
                "for (i...N) for (j...M) mat[i][j] = i + j;", "int fun(int n) { return (n <= 1 ? 1 : 4 * fun(n / 3)); }"};
        String[] cls = {"O(1)", "O(N)", "O(N*M)", "?"};
        String[] why = {"three instructions, whatever a and b are", "proportional to N", "proportional to N*M", ""};
        for (int i = 0; i < 4; i++) {
            row(code[i], -900, -330 + i * 100, 26, Colors.WHITE, true);
            row(cls[i], 400, -330 + i * 100, 36, i == 3 ? Colors.RED : Colors.GREEN, true);
            if (!why[i].isEmpty()) row(why[i], -900, -295 + i * 100, 22, Colors.GRAY, true);
        }
        say("The first three are easy to read off. What about the recursive one?", Colors.LIGHT_GRAY);
        pause(2.4);
        clearMine();
        newHeading("Solving for Time Complexity");
        say("Each call does constant work c1 and calls fun on n/3.", Colors.LIGHT_GRAY);
        String[] steps = {"T(n)=c_1+T(n/3)\\qquad T(1)=1", "=c_1+\\left[c_1+T(n/9)\\right]", "=2c_1+T(n/3^2)", "=3c_1+T(n/3^3)", "=k\\,c_1+T(n/3^k)"};
        for (int i = 0; i < steps.length; i++) {
            show(steps[i], 40, i == 0 ? -300 : -200, -320 + i * 70);
            pause(0.5);
        }
        show("\\text{If } n=3^k:\\ T(n)=c_1\\log_3 n+T(1)=O(\\log_3 n)", 40, 120, 60);
        pause(1.4);
        int ks = 7;
        String[][] cells = new String[2][ks];
        for (int k = 0; k < ks; k++) {
            int nv = (int) Math.pow(3, k);
            cells[0][k] = String.valueOf(nv);
            cells[1][k] = String.valueOf(calls(nv, true));
        }
        Grid g = new Grid(cells, -380, 190, 110, 54, Colors.BLUE, 26);
        g.headers(null, new String[]{"n", "calls"}, 22, Colors.GRAY);
        List<Animation> a = new ArrayList<>();
        g.show(a, d(0.7));
        playAll(a);
        say("Counted by running it: n = 3^k needs k + 1 calls, so the work is about log\u2083 n.", Colors.GOLD);
        pause(3.8);
    }

    // ── 12-13 ────────────────────────────────────────────────────────

    private void c12to13() {
        head = writeHeading("Types of Complexities");
        pause(0.5);
        String[][] tab = {{"Symbol", "Name", "Bound", "Equation"},
                {"O(...)", "Big O", "Upper", "T(n) \u2264 c f(n)"},
                {"\u03a9(...)", "Big Omega", "Lower", "T(n) \u2265 c f(n)"},
                {"\u0398(...)", "Theta", "Upper and Lower", "c1 f(n) \u2264 T(n) \u2264 c2 f(n)"},
                {"o(...)", "Little O", "Strictly Upper", "T(n) < c f(n)"},
                {"\u03c9(...)", "Little Omega", "Strictly Lower", "T(n) > c f(n)"}};
        Grid g = new Grid(tab, -760, -320, 380, 64, Colors.BLUE, 26);
        List<Animation> a = new ArrayList<>();
        g.show(a, d(0.8));
        playAll(a);
        a = new ArrayList<>();
        g.lightRow(a, 0, Colors.GOLD, d(0.5));
        playAll(a);
        say("Five ways to compare T(n) with f(n): from an upper bound to a strictly lower one.", Colors.LIGHT_GRAY);
        pause(3.4);
        clearMine();
        newHeading("Notes");
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "\u0398 means O and \u03a9. It is a stronger guarantee on"));
        s.add(ln(3, "the complexity."));
        s.add(ln(0, "If T(n) is O(n), then T(n) is also O(n\u00b2), also"));
        s.add(ln(3, "O(n log n), also O(n\u00b3), O(n\u00b9\u2070\u2070), O(2\u207f); but it is not"));
        s.add(ln(3, "O(log n) or O(1)."));
        s.add(ln(0, "Big O is also called Big Oh."));
        s.add(ln(0, "T(n) = T(n/2) = T(1000n) = T(n log 2) = T(2 log n)"));
        s.add(ln(0, "Log2(x), that is, log to the base 2 is sometimes"));
        s.add(ln(3, "written as lg(x)."));
        s.add(ln(0, "If T(n) = O(f(n)) then f(n) = \u03a9(T(n))."));
        keep(writeSlide(s, -380));
        pause(1.2);
        // which bounds hold for T(n) = n: the ratio T(n)/f(n) must stay bounded
        String[] names = {"log n", "n", "n log n", "n\u00b2", "n\u00b3", "2\u207f"};
        DoubleUnaryOperator[] fs = {x -> lg(x), x -> x, x -> x * lg(x), x -> x * x, x -> x * x * x, x -> Math.pow(2, x)};
        tag("T(n) = n", 700, -330, 34, Colors.BLUE, false);
        for (int i = 0; i < 6; i++) {
            double n1 = 100, n2 = i == 5 ? 1000 : 100000;
            double r1 = n1 / fs[i].applyAsDouble(n1), r2 = n2 / fs[i].applyAsDouble(n2);
            boolean ok = r2 <= r1 * 1.0001;
            List<MObject> chip = chip((ok ? "\u2714 " : "\u2718 ") + "O(" + names[i] + ")", 700, -250 + i * 70, 260, 54, ok ? Colors.GREEN : Colors.RED, 26);
            mine.addAll(chip);
            List<Animation> in = new ArrayList<>();
            fade(in, chip, d(0.4));
            playAll(in);
        }
        say("Checked by computing T(n)/f(n) for large n: it stays bounded for n and above, but grows for log n.", Colors.GOLD);
        pause(4.0);
    }

    // ── 14 ───────────────────────────────────────────────────────────

    private void c14() {
        head = writeHeading("Complexity Arithmetic");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "If T1(n) = O(f(n)) and T2(n) = O(g(n)), then"));
        s.add(ln(1, "T1(n) + T2(n) = max(O(f(n)), O(g(n)))"));
        s.add(ln(1, "T1(n) * T2(n) = O(f(n) * g(n))"));
        s.add(ln(0, "Classwork:").kw("Classwork", Colors.RED));
        s.add(ln(1, "Write a C code that requires the use of T1(n) + T2(n)."));
        s.add(ln(1, "Write a C code that requires the use of T1(n) * T2(n)."));
        keep(writeSlide(s, -380));
        pause(1.4);
        int n = 5, m = 4;
        // sum: two loops one after the other
        tag("T1 + T2: one loop after the other", 560, -400, 30, Colors.GOLD, false);
        code(new String[]{"for (i = 0; i < N; ++i)          // T1(n) = n", "    a[i] = 0;", "for (i = 0; i < N; ++i)          // T2(n) = n*n", "    for (j = 0; j < N; ++j)", "        b[i][j] = 0;"}, 100, -330, 22, 38, 2.4);
        TextMob c1 = tag("", 540, -90, 30, Colors.WHITE, false);
        c1.setText("N = " + n + ": " + n + " + " + n * n + " = " + (n + n * n) + " steps");
        say("Run one after the other, the costs add; for large n the bigger one decides: max(n, n\u00b2) = n\u00b2.", Colors.LIGHT_GRAY);
        pause(3.4);
        clearMine();
        // product: nested loops
        keep(new ArrayList<List<MObject>>());
        tag("T1 * T2: one loop inside the other", 560, -400, 30, Colors.GOLD, false);
        code(new String[]{"for (i = 0; i < N; ++i)          // n times", "    for (j = 0; j < M; ++j)      // m times each", "        c[i][j] = 0;"}, 100, -330, 22, 38, 2.0);
        TextMob c2 = tag("", 540, -170, 30, Colors.WHITE, false);
        c2.setText("N = " + n + ", M = " + m + ": " + n + " * " + m + " = " + n * m + " steps");
        say("Nested, the costs multiply: O(n) * O(m) = O(n*m).", Colors.GREEN);
        pause(3.6);
    }

    // ── 15-16 ────────────────────────────────────────────────────────

    private void c15to16() {
        head = writeHeading("Typical Complexities");
        pause(0.5);
        String[][] tab = {{"Function", "Name"}, {"c", "Constant"}, {"Log N", "Logarithmic"}, {"Log\u00b2 N", "Log-squared"}, {"N", "Linear"},
                {"N log N", "Superlinear"}, {"N\u00b2", "Quadratic"}, {"N\u00b3", "Cubic"}, {"2\u1d3a", "Exponential"}};
        Grid g = new Grid(tab, -900, -330, 190, 52, Colors.BLUE, 26);
        List<Animation> a = new ArrayList<>();
        g.show(a, d(0.7));
        playAll(a);
        Plot p = new Plot(120, 280, 640, 540, 16, 300, "N", "time");
        String[] nm = {"c", "log N", "log\u00b2 N", "N", "N log N", "N\u00b2", "N\u00b3", "2^N"};
        DoubleUnaryOperator[] fs = {x -> 10, x -> x < 1 ? 0 : 10 * lg(x), x -> x < 1 ? 0 : 5 * lg(x) * lg(x), x -> 10 * x / 2.0,
                x -> x < 1 ? 0 : x * lg(x) * 2.5, x -> x * x * 1.2, x -> x * x * x / 12.0, x -> Math.pow(2, x) / 220.0};
        for (int i = 0; i < 8; i++) {
            p.curve(fs[i], 0, 16, PAL[i], 4);
            p.endLabel(nm[i], PAL[i]);
        }
        say("From the slowest-growing to the fastest: constant, logarithmic, linear, N log N, quadratic, cubic, exponential.", Colors.LIGHT_GRAY);
        pause(3.4);
        clearMine();
        // the homework: n log n against n^1.5
        newHeading("Homework: n log n or n^1.5?");
        String[][] hw = new String[4][5];
        double[] ns = {10, 1e3, 1e5, 1e7, 1e9};
        hw[0][0] = "n";
        hw[1][0] = "n log n";
        hw[2][0] = "n^1.5";
        hw[3][0] = "ratio";
        String[] hdr = new String[5];
        for (int k = 0; k < 5; k++) {
            double nv = ns[k];
            hw[0][k] = k == 0 ? "10" : String.format("10^%d", (int) Math.round(Math.log10(nv)));
            double a1 = nv * lg(nv), a2 = Math.pow(nv, 1.5);
            hw[1][k] = String.format("%.3g", a1);
            hw[2][k] = String.format("%.3g", a2);
            hw[3][k] = String.format("%.4f", a1 / a2);
        }
        Grid hg = new Grid(hw, -420, -300, 220, 60, Colors.BLUE, 26);
        hg.headers(null, new String[]{"n", "n log n", "n^1.5", "ratio"}, 20, Colors.GRAY);
        a = new ArrayList<>();
        hg.show(a, d(0.8));
        playAll(a);
        a = new ArrayList<>();
        hg.lightRow(a, 3, Colors.GOLD, d(0.5));
        playAll(a);
        say("The ratio (n log n) / n^1.5 = log n / \u221an keeps shrinking toward 0: n^1.5 grows faster.", Colors.GOLD);
        pause(3.6);
        clearMine();
        newHeading("Complexity Comparison");
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Given two complexity functions f(n) and g(n),"));
        s.add(ln(3, "we can determine relative growth rates using"));
        s.add(ln(3, "lim f(n) / g(n), using L'Hospital's rule."));
        s.add(ln(0, "Four possible values:"));
        s.add(ln(1, "The limit is zero, implies f(n) = o(g(n))."));
        s.add(ln(1, "The limit is c \u2260 0, implies f(n) = \u0398(g(n))."));
        s.add(ln(1, "The limit is \u221e, implies g(n) = o(f(n))."));
        s.add(ln(1, "The limit oscillates, implies there is no relation."));
        keep(writeSlide(s, -380));
        pause(1.2);
        String[] fd = {"f = n, g = n\u00b2", "f = 3n + 1, g = n", "f = n\u00b2, g = n", "f = n(2 + sin n), g = n"};
        double[] pts = {10, 100, 1000, 10000};
        for (int i = 0; i < 4; i++) {
            StringBuilder sb = new StringBuilder();
            for (int k = 0; k < pts.length; k++) {
                double nv = pts[k], f, gg;
                if (i == 0) { f = nv; gg = nv * nv; }
                else if (i == 1) { f = 3 * nv + 1; gg = nv; }
                else if (i == 2) { f = nv * nv; gg = nv; }
                else { nv = 1000 + k; f = nv * (2 + Math.sin(nv)); gg = nv; }
                sb.append(String.format("%.3g", f / gg)).append(k < pts.length - 1 ? "   " : "");
            }
            row(fd[i], 60, -250 + i * 80, 26, Colors.WHITE, true);
            row("f/g: " + sb, 60, -215 + i * 80, 24, Colors.GOLD, true);
        }
        say("Try the four cases numerically: 0, a constant, growing without bound, and swinging up and down.", Colors.GOLD);
        pause(4.0);
    }

    // ── 17 ───────────────────────────────────────────────────────────

    private void c17() {
        head = writeHeading("Facets of Efficiency");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "An algorithm or its implementation may have"));
        s.add(ln(3, "various facets towards efficiency."));
        s.add(ln(1, "Time complexity (which we usually focus on)"));
        s.add(ln(1, "Space complexity (considered in memory-critical"));
        s.add(ln(2, "systems such as embedded devices)"));
        s.add(ln(1, "Energy complexity (e.g., your smartphones)"));
        s.add(ln(1, "Security level (e.g., program with less versus more"));
        s.add(ln(2, "usage of pointers)"));
        s.add(ln(1, "I/O complexity"));
        s.add(ln(1, "..."));
        keep(writeSlide(s, -380));
        pause(1.2);
        String[] facets = {"time", "space", "energy", "security", "I/O"};
        for (int i = 0; i < facets.length; i++) {
            List<MObject> c = chip(facets[i], 640, -280 + i * 90, 240, 64, PAL[i], 30);
            mine.addAll(c);
            List<Animation> in = new ArrayList<>();
            fade(in, c, d(0.5));
            playAll(in);
        }
        say("Time is the facet we study in this course; the others matter in other settings.", Colors.GOLD);
        pause(3.6);
    }

    // ── 18-19 ────────────────────────────────────────────────────────

    private static final int[] MSS = {4, -3, 5, -2, -1, 2, 6, -2};

    private void c18to19() {
        head = writeHeading("Max. Subsequence Sum");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Problem Statement").kw("Problem", Colors.GOLD));
        s.add(ln(1, "Given an array of (positive, negative, zero)"));
        s.add(ln(2, "integer values, find the largest subsequence"));
        s.add(ln(2, "sum."));
        s.add(ln(1, "A subsequence is a consecutive set of"));
        s.add(ln(2, "elements. If empty, its sum is zero."));
        keep(writeSlide(s, -380));
        int n = MSS.length;
        String[][] cells = new String[1][n];
        for (int i = 0; i < n; i++) cells[0][i] = String.valueOf(MSS[i]);
        Grid g = new Grid(cells, 80, -330, 100, 70, Colors.BLUE, 34);
        List<Animation> a = new ArrayList<>();
        g.show(a, d(0.7));
        playAll(a);
        pause(1.6);
        clearMine();
        newHeading("MSS: Algorithm 1");
        g = new Grid(cells, 80, -330, 100, 70, Colors.BLUE, 34);
        a = new ArrayList<>();
        g.show(a, d(0.6));
        playAll(a);
        code(new String[]{"For each possible subsequence", "  Compute sum", "  If sum > current maxsum", "    current maxsum = sum", "Return current maxsum"}, -930, -300, 26, 46, 2.4);
        TextMob cnt = tag("subsequences checked: 0 of " + n * (n + 1) / 2, 280, -120, 30, Colors.WHITE, true);
        TextMob adds = tag("additions so far: 0", 280, -70, 30, Colors.WHITE, true);
        TextMob best = tag("maxsum = 0", 280, -20, 34, Colors.GOLD, true);
        say("Try every subsequence i..j, add up its elements, and keep the largest sum.", Colors.LIGHT_GRAY);
        int checked = 0, additions = 0, maxsum = 0, bi = -1, bj = -1;
        for (int i = 0; i < n; i++) for (int j = i; j < n; j++) {
            int sum = 0;
            for (int k = i; k <= j; k++) {
                sum += MSS[k];
                additions++;
            }
            checked++;
            boolean upd = sum > maxsum;
            if (upd) {
                maxsum = sum;
                bi = i;
                bj = j;
            }
            if (checked <= 6 || upd) {
                a = new ArrayList<>();
                g.plain(a, Colors.BLUE, d(0.15));
                for (int k = i; k <= j; k++) g.light(a, 0, k, upd ? Colors.GREEN : Colors.GOLD, d(0.25));
                playAll(a);
                pause(upd ? 0.7 : 0.35);
            }
            cnt.setText("subsequences checked: " + checked + " of " + n * (n + 1) / 2);
            adds.setText("additions so far: " + additions);
            best.setText("maxsum = " + maxsum + (bi >= 0 ? "   (elements " + bi + " to " + bj + ")" : ""));
        }
        a = new ArrayList<>();
        g.plain(a, Colors.BLUE, d(0.3));
        for (int k = bi; k <= bj; k++) g.light(a, 0, k, Colors.GREEN, d(0.5));
        playAll(a);
        say("Done: " + checked + " subsequences, " + additions + " additions. The best sum is " + maxsum + ".", Colors.GREEN);
        pause(1.6);
        tag("How many subsequences? N(N+1)/2 = " + checked, 280, 40, 28, Colors.GRAY, true);
        tag("What does each cost? Up to N additions.", 280, 85, 28, Colors.GRAY, true);
        say("About N\u00b2/2 subsequences, each costing up to N additions: Algorithm 1 takes O(N\u00b3).", Colors.GOLD);
        pause(4.0);
    }

    // ── 20-22 ────────────────────────────────────────────────────────

    private void c20to22() {
        head = writeHeading("MSS: Algorithm 1");
        pause(0.5);
        say("Did we perform a tight mathematical analysis? Count the additions exactly.", Colors.LIGHT_GRAY);
        show("\\sum_{i=0}^{N-1}\\sum_{j=i}^{N-1}\\sum_{k=i}^{j}O(1)", 40, 0, -360);
        pause(0.8);
        show("=\\sum_{i=0}^{N-1}\\sum_{j=i}^{N-1}(j-i+1)", 40, 0, -215);
        pause(0.8);
        show("=\\sum_{i=0}^{N-1}\\frac{(N-i)(N-i+1)}{2}", 40, 0, -60);
        pause(0.8);
        show("=\\frac{N^3+3N^2+2N}{6}=O(N^3)", 44, 0, 90);
        pause(1.2);
        int ns = 8;
        String[][] cells = new String[3][ns];
        for (int n = 1; n <= ns; n++) {
            int cnt = 0;
            for (int i = 0; i < n; i++) for (int j = i; j < n; j++) for (int k = i; k <= j; k++) cnt++;
            int formula = (n * n * n + 3 * n * n + 2 * n) / 6;
            if (cnt != formula) throw new IllegalStateException("formula mismatch at N=" + n);
            cells[0][n - 1] = String.valueOf(n);
            cells[1][n - 1] = String.valueOf(cnt);
            cells[2][n - 1] = String.valueOf(formula);
        }
        Grid g = new Grid(cells, -460, 215, 115, 54, Colors.BLUE, 26);
        g.headers(null, new String[]{"N", "counted", "formula"}, 22, Colors.GRAY);
        List<Animation> a = new ArrayList<>();
        g.show(a, d(0.8));
        playAll(a);
        a = new ArrayList<>();
        g.lightRow(a, 1, Colors.GREEN, d(0.4));
        g.lightRow(a, 2, Colors.GREEN, d(0.4));
        playAll(a);
        say("Counting the additions for N = 1 to " + ns + " matches the formula every time: the analysis is tight.", Colors.GREEN);
        pause(3.0);
        say("The analysis is tight. Is the algorithm tight? Not at all: the next two are faster.", Colors.GOLD);
        pause(3.6);
    }

    // ── 23 ───────────────────────────────────────────────────────────

    private void c23() {
        head = writeHeading("MSS: Algorithm 2");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Observation:"));
        s.add(ln(0, "sum of A[i..j] = A[j] + sum of A[i..j-1]"));
        keep(writeSlide(s, -380));
        code(new String[]{"For each starting position i", "  For each ending position j", "    Incrementally compute sum", "    If sum > maxsum", "        maxsum = sum", "Return maxsum"}, -930, -200, 26, 46, 2.6);
        int n = MSS.length;
        String[][] cells = new String[1][n];
        for (int i = 0; i < n; i++) cells[0][i] = String.valueOf(MSS[i]);
        Grid g = new Grid(cells, 80, -330, 100, 70, Colors.BLUE, 34);
        List<Animation> a = new ArrayList<>();
        g.show(a, d(0.7));
        playAll(a);
        TextMob adds = tag("additions so far: 0", 280, -150, 32, Colors.WHITE, true);
        TextMob best = tag("maxsum = 0", 280, -90, 36, Colors.GOLD, true);
        say("Keep the running sum while j moves right: each new element costs one addition.", Colors.LIGHT_GRAY);
        int additions = 0, maxsum = 0;
        for (int i = 0; i < n; i++) {
            int sum = 0;
            for (int j = i; j < n; j++) {
                sum += MSS[j];
                additions++;
                if (sum > maxsum) maxsum = sum;
                if (i < 2) {
                    a = new ArrayList<>();
                    g.plain(a, Colors.BLUE, d(0.1));
                    for (int k = i; k <= j; k++) g.light(a, 0, k, Colors.GOLD, d(0.2));
                    playAll(a);
                    pause(0.2);
                }
                adds.setText("additions so far: " + additions);
                best.setText("maxsum = " + maxsum);
            }
        }
        a = new ArrayList<>();
        g.plain(a, Colors.BLUE, d(0.3));
        playAll(a);
        say(additions + " additions for N = " + n + " instead of 120: N(N+1)/2 of them, so O(N\u00b2).", Colors.GREEN);
        pause(1.4);
        tag("N(N+1)/2 = " + n * (n + 1) / 2 + " for N = " + n, 280, -30, 28, Colors.GRAY, true);
        say("What is the complexity of this algorithm? Two nested loops, O(N\u00b2).", Colors.GOLD);
        pause(3.6);
    }

    // ── 24 ───────────────────────────────────────────────────────────

    private void c24() {
        head = writeHeading("MSS: Algorithm 3");
        pause(0.5);
        int[] arr = {1, 2, -8, 4, -3, 5, -2, -1, 2, 6, -2};
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Observation: Discard fruitless subsequences early."));
        s.add(ln(1, "e.g., in {1, 2, -8, 4, -3, 5, -2, -1, 2, 6, -2},"));
        s.add(ln(2, "we need not consider {-2} or {-2, -1} or even"));
        s.add(ln(2, "{-2, -1, 2} or {1, 2, -8, 4}."));
        List<List<MObject>> slide = writeSlide(s, -380);
        pause(1.8);
        swipeAway(slide);
        pause(0.4);
        code(new String[]{"For each position", "    Add next element to sum", "    If sum > maxsum", "        maxsum = sum", "    Else if sum is negative", "        sum = 0"}, -930, -300, 26, 46, 2.6);
        int n = arr.length;
        String[][] cells = new String[1][n];
        for (int i = 0; i < n; i++) cells[0][i] = String.valueOf(arr[i]);
        Grid g = new Grid(cells, 20, -330, 82, 66, Colors.BLUE, 30);
        List<Animation> a = new ArrayList<>();
        g.show(a, d(0.7));
        playAll(a);
        TextMob sumT = tag("sum = 0", 260, -170, 36, Colors.WHITE, true);
        TextMob maxT = tag("maxsum = 0", 260, -110, 36, Colors.GOLD, true);
        say("One pass: add each element; when the sum goes negative, throw the past away and start again.", Colors.LIGHT_GRAY);
        int sum = 0, maxsum = 0, start = 0;
        for (int i = 0; i < n; i++) {
            sum += arr[i];
            boolean newMax = sum > maxsum;
            if (newMax) maxsum = sum;
            a = new ArrayList<>();
            g.light(a, 0, i, Colors.GOLD, d(0.25));
            playAll(a);
            sumT.setText("sum = " + sum);
            maxT.setText("maxsum = " + maxsum);
            if (!newMax && sum < 0) {
                a = new ArrayList<>();
                for (int k = start; k <= i; k++) g.light(a, 0, k, Colors.RED, d(0.3));
                playAll(a);
                pause(0.4);
                sum = 0;
                start = i + 1;
                sumT.setText("sum = 0   (negative: discard)");
            } else if (newMax) {
                a = new ArrayList<>();
                for (int k = start; k <= i; k++) g.light(a, 0, k, Colors.GREEN, d(0.25));
                playAll(a);
            }
            pause(0.5);
        }
        say("maxsum = " + maxsum + " after one pass: O(N) time. Are you kidding? It really works: only fruitless prefixes were dropped.", Colors.GREEN);
        pause(4.0);
    }

    // ── 25-26 ────────────────────────────────────────────────────────

    private void c25to26() {
        head = writeHeading("Binary Search");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Go to page number 44."));
        s.add(ln(0, "Searching in an array takes linear time O(N)."));
        s.add(ln(0, "If the array is sorted already, we can do better."));
        s.add(ln(0, "We can cut the search space by half at every"));
        s.add(ln(3, "step."));
        s.add(ln(0, "Classwork: Write the code for binary search.").kw("Classwork", Colors.RED));
        s.add(ln(1, "Source: bsearch.cpp"));
        List<List<MObject>> slide = writeSlide(s, -380);
        pause(1.4);
        swipeAway(slide);
        pause(0.4);
        int[] arr = {5, 7, 8, 20, 23, 42, 43, 44, 78, 81};
        int key = 44;
        int n = arr.length;
        String[][] cells = new String[1][n];
        for (int i = 0; i < n; i++) cells[0][i] = String.valueOf(arr[i]);
        Grid g = new Grid(cells, -490, -280, 98, 76, Colors.BLUE, 32);
        g.headers(numbered(n), null, 22, Colors.GRAY);
        List<Animation> a = new ArrayList<>();
        g.show(a, d(0.8));
        playAll(a);
        say("Find " + key + " in the sorted array: look at the middle and discard the half that cannot hold it.", Colors.LIGHT_GRAY);
        int lo = 0, hi = n - 1, steps = 0;
        TextMob st = tag("steps: 0", 0, -120, 34, Colors.WHITE, false);
        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            steps++;
            a = new ArrayList<>();
            g.plain(a, Colors.BLUE, d(0.2));
            for (int k = 0; k < n; k++) if (k < lo || k > hi) g.light(a, 0, k, Colors.GRAY, d(0.2));
            g.light(a, 0, mid, Colors.GOLD, d(0.3));
            playAll(a);
            st.setText("steps: " + steps + "    lo = " + lo + ", hi = " + hi + ", mid = " + mid);
            pause(1.0);
            if (arr[mid] == key) {
                a = new ArrayList<>();
                g.light(a, 0, mid, Colors.GREEN, d(0.4));
                playAll(a);
                say("Found " + key + " at index " + mid + " after " + steps + " looks, not " + (mid + 1) + ".", Colors.GREEN);
                break;
            } else if (arr[mid] < key) {
                say(arr[mid] + " < " + key + ": keep only the right half.", Colors.LIGHT_GRAY);
                lo = mid + 1;
            } else {
                say(arr[mid] + " > " + key + ": keep only the left half.", Colors.LIGHT_GRAY);
                hi = mid - 1;
            }
            pause(0.8);
        }
        pause(2.0);
        clearMine();
        newHeading("Binary Search");
        List<Spec> t = new ArrayList<>();
        t.add(ln(0, "Constant amount of time required to"));
        t.add(ln(1, "Find the mid element."));
        t.add(ln(1, "Check if it is the element to be searched."));
        t.add(ln(1, "Decide whether to go to the left or the right."));
        t.add(ln(1, "Cut the search space by half."));
        t.add(ln(0, "T(N) = T(N/2) + O(1)"));
        t.add(ln(1, "Thus, T(N) is O(log N)."));
        keep(writeSlide(t, -380));
        pause(1.0);
        long[] sizes = {10, 1000, 1000000, 1000000000L};
        String[][] tab = new String[2][sizes.length];
        for (int i = 0; i < sizes.length; i++) {
            long size = sizes[i];
            int worst = 0;
            long left = size;
            while (left > 0) {
                worst++;
                left /= 2;
            }
            tab[0][i] = String.valueOf(size);
            tab[1][i] = String.valueOf(worst);
        }
        Grid tg = new Grid(tab, 60, -260, 200, 60, Colors.BLUE, 26);
        tg.headers(null, new String[]{"N", "looks"}, 22, Colors.GRAY);
        a = new ArrayList<>();
        tg.show(a, d(0.8));
        playAll(a);
        say("Counted by halving: even a billion elements need only " + tab[1][3] + " looks. T(N) = T(N/2) + O(1) is O(log N).", Colors.GOLD);
        pause(4.0);
    }

    private static String[] numbered(int n) {
        String[] r = new String[n];
        for (int i = 0; i < n; i++) r[i] = String.valueOf(i);
        return r;
    }

    // ── 27 ───────────────────────────────────────────────────────────

    private void c27() {
        head = writeHeading("Exercises");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Solve exercises at the end of Chapter 2 of"));
        s.add(ln(3, "Weiss's book."));
        keep(writeSlide(s, -300));
        pause(2.4);
        clearMine();
        fadeOutAll(d(0.9), head);
        head = null;
        StrokeTextMob by = stroke("This video was made by", 0, -170, 38, Colors.LIGHT_GRAY, false);
        play(new Write(by, d(2.0)));
        mine.add(by);
        pause(0.2);
        double size = 104, gap = 34;
        double w1 = strokeW("Karthik", true, size), w2 = strokeW("&", true, size), w3 = strokeW("Tejaswi", true, size);
        double left = -(w1 + w2 + w3 + 2 * gap) / 2;
        StrokeTextMob n1 = stroke("Karthik", left + w1 / 2, -30, size, Colors.TEAL, true);
        StrokeTextMob amp = stroke("&", left + w1 + gap + w2 / 2, -30, size, Colors.WHITE, true);
        StrokeTextMob n2 = stroke("Tejaswi", left + w1 + w2 + 2 * gap + w3 / 2, -30, size, Colors.GOLD, true);
        play(new Write(n1, d(1.8)));
        play(new Write(amp, d(0.7)));
        play(new Write(n2, d(1.8)));
        mine.add(n1);
        mine.add(amp);
        mine.add(n2);
        Plot p = new Plot(-200, 360, 400, 150, 20, 100, "N", "");
        p.curve(x -> x, 0, 20, Colors.BLUE, 4);
        p.curve(x -> x <= 1 ? 0 : x * lg(x), 0, 20, Colors.GOLD, 4);
        p.curve(x -> x * x / 4.0, 0, 20, Colors.PINK, 4);
        pause(3.2);
    }
}
