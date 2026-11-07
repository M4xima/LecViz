package com.lecviz.scenes;

import com.lecviz.animations.*;
import com.lecviz.core.Animation;
import com.lecviz.core.MObject;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Clips for slides 67-90 of the dictionary deck: hash tables. One class, one clip per part (the constructor takes
 * the slide range): 67 the idea, 68to70 hash functions, 71to72 distribution and a better function, 73 the ADT,
 * 74 pitfalls, 75 collisions, 76to77 chaining, 78 load factor, 79 issues with chaining, 80 open addressing,
 * 81 linear probing, 82to84 its operations as code, 85 primary clustering, 86 quadratic probing, 87 the guarantee,
 * 88 secondary clustering, 89 double hashing, 90 learning outcomes and the closing card.
 *
 * Every table is filled by a real program: hash values, probes, collisions, chain lengths and counts are computed,
 * not drawn by hand.
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class PDSHashScene extends PDSHashClipBase {

    private final String part;
    private final List<MObject> mine = new ArrayList<>();
    private StrokeTextMob head;

    public PDSHashScene(String part) { this.part = part; }

    @Override
    public void construct() {
        switch (part) {
            case "67": s67(); break;
            case "68to70": s68to70(); break;
            case "71to72": s71to72(); break;
            case "73": s73(); break;
            case "74": s74(); break;
            case "75": s75(); break;
            case "76to77": s76to77(); break;
            case "78": s78(); break;
            case "79": s79(); break;
            case "80": s80(); break;
            case "81": s81(); break;
            case "82to84": s82to84(); break;
            case "85": s85(); break;
            case "86": s86(); break;
            case "87": s87(); break;
            case "88": s88(); break;
            case "89": s89(); break;
            default: s90(); break;
        }
        unsay();
        fadeOutAll(d(1.2), mine);
        if (head != null) fadeOutAll(d(1.0), head);
        pause(0.3);
    }

    // ── helpers ──────────────────────────────────────────────────────

    private void say(String s, Color c) { sayAt(s, c, 0, 440, 34); }

    private void keep(List<List<MObject>> g) { for (List<MObject> x : g) mine.addAll(x); }

    private void textThenSwipe(List<Spec> s, double startY, double hold) {
        List<List<MObject>> t = writeSlide(s, startY);
        pause(hold);
        swipeAway(t);
        pause(0.4);
    }

    private void clearMine() {
        unsay();
        fadeOutAll(d(1.0), mine);
        mine.clear();
        pause(0.3);
    }

    private void newHeading(String s) {
        if (head != null) fadeOutAll(d(0.8), head);
        head = dropHeading(s);
        pause(0.4);
    }

    private TextMob tag(String s, double x, double y, double size, Color c, boolean left) {
        TextMob t = label(s, x, y, size, c, left, true);
        mine.add(t);
        play(new FadeIn(t, d(0.5)));
        return t;
    }

    private LaTeXMob show(String src, double size, double x, double y) {
        LaTeXMob f = latex(src, size, x, y);
        mine.add(f);
        play(new FadeIn(f, d(0.8)));
        return f;
    }

    private HT table(int n, double cx, double cy, double w, double h, double gap) {
        HT t = new HT(n, cx, cy, w, h, gap);
        mine.addAll(t.parts());
        return t;
    }

    private void showTable(HT t) {
        List<Animation> a = new ArrayList<>();
        t.show(a, d(0.6));
        playAll(a);
    }

    private CodeBox code(String[] lines, double left, double top, double size, double pitch, double typeSecs) {
        CodeBox c = new CodeBox(lines, left, top, size, pitch);
        c.typeIn(typeSecs);
        mine.addAll(c.parts());
        play(new FadeIn(c.hl, d(0.3)));
        return c;
    }

    private int hashName(String s, int H) {
        int v = 0;
        for (char ch : s.toCharArray()) v += ch;
        return v % H;
    }

    private static final String[] NAMES = {"Akshat", "Aditi", "Anil", "Arjun", "Amit", "Anita", "Krishna", "Monisha",
            "Naveen", "Pavan", "Karthik", "Tejaswi", "Ananya", "Aryan"};

    // ── 67 ───────────────────────────────────────────────────────────

    private void s67() {
        head = writeHeading("Hash Tables");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Hashtable is also a dictionary."));
        s.add(ln(1, "Dictionaries map keys to values."));
        s.add(ln(1, "Hashtables also do the same in a specific way."));
        s.add(ln(0, "Hashing is indexing elements in a (typically)").kw("indexing", Colors.BLUE));
        s.add(ln(3, "fixed length array."));
        s.add(ln(0, "Element's pattern (name, bit-pattern, ...) is"));
        s.add(ln(3, "used for computing the index."));
        textThenSwipe(s, -330, 1.8);

        String[] sorted = {"Akshat", "Krishna", "Monisha", "Naveen", "Pavan"};
        String[] hashed = {"Pavan", "Monisha", "Akshat", "Naveen", "Krishna"};
        HT a = table(5, 150, -190, 200, 84, 14);
        HT b = table(5, 150, 120, 200, 84, 14);
        for (int i = 0; i < 5; i++) a.set(i, sorted[i], Colors.BLUE);
        tag("Sorted Array", -780, -190, 34, Colors.MAROON, true);
        tag("Hash Table", -780, 120, 34, Colors.MAROON, true);
        showTable(a);
        showTable(b);
        say("A sorted array keeps the names in order; to find one you search.", Colors.LIGHT_GRAY);
        pause(2.0);
        say("A hash table computes where a name goes from the name itself.", Colors.GOLD);
        pause(1.0);
        for (int k = 0; k < 5; k++) {
            int from = -1;
            for (int i = 0; i < 5; i++) if (sorted[i].equals(hashed[k])) from = i;
            List<MObject> chip = chip(hashed[k], a.x(from), -190, 180, 56, Colors.BLUE, 30);
            List<Animation> in = new ArrayList<>();
            fade(in, chip, d(0.3));
            playAll(in);
            List<Animation> mv = new ArrayList<>();
            for (MObject m : chip) mv.add(new MoveTo(m, b.x(k), 120, d(1.0)).setEasing(com.lecviz.utils.Easing.EASE_IN_OUT));
            playAll(mv);
            b.put(k, hashed[k], Colors.BLUE, d(0.3));
            for (MObject m : chip) remove(m);
            pause(0.2);
        }
        say("The index comes from the key: no search through the array.", Colors.GREEN);
        pause(3.2);
    }

    // ── 68-70 ────────────────────────────────────────────────────────

    private void s68to70() {
        head = writeHeading("Hash Functions");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Identity function: f(x) = x").kw("Identity function", Colors.RED));
        s.add(ln(1, "simple"));
        s.add(ln(1, "needs space equal to the maximum value of x"));
        s.add(ln(1, "may leave large set of holes"));
        s.add(ln(1, "applicable only to integral keys"));
        s.add(ln(0, "General function").kw("General function", Colors.RED));
        s.add(ln(1, "may use space judiciously"));
        s.add(ln(1, "array size not dependent on the element values"));
        s.add(ln(1, "may result in collisions").kw("collisions", Colors.RED));
        s.add(ln(1, "Applicable to arbitrary types"));
        textThenSwipe(s, -400, 1.8);

        // identity: one cell per possible value
        HT big = table(41, 0, -250, 38, 60, 6);
        for (int i = 0; i < 41; i++) {
            big.box[i].setOpacity(0);
        }
        List<Animation> a = new ArrayList<>();
        for (int i = 0; i < 41; i++) a.add(new FadeInAt(big.box[i], 0.012 * i, d(0.3)));
        playAll(a);
        say("The identity function f(x) = x: to store 2, 9 and 40 the array needs 41 cells.", Colors.LIGHT_GRAY);
        int[] keys = {2, 9, 40};
        for (int k : keys) {
            big.put(k, String.valueOf(k), Colors.BLUE, d(0.4));
            big.val[k].setFontSize(20);
        }
        pause(1.4);
        List<MObject> holes = chip("38 of the 41 cells are holes", 0, -120, 600, 60, Colors.RED, 32);
        mine.addAll(holes);
        List<Animation> ha = new ArrayList<>();
        fade(ha, holes, d(0.6));
        playAll(ha);
        say("The space is as large as the largest key, whatever the number of keys.", Colors.RED);
        pause(3.0);
        clearMine();

        // a general function
        CodeBox cb = code(new String[]{
                "int hashfun(char *key, int H) {",
                "    int val = 0;",
                "    while (*key) val += *key++;",
                "    return val % H;",
                "}"}, -930, -330, 26, 46, 3.0);
        HT t = table(7, 450, -60, 88, 84, 10);
        showTable(t);
        say("A general function: add up the characters, then % H. Here H = 7.", Colors.LIGHT_GRAY);
        pause(1.6);
        for (String key : new String[]{"cat", "act"}) {
            int sum = 0;
            StringBuilder sb = new StringBuilder();
            for (char c : key.toCharArray()) {
                sb.append(sb.length() == 0 ? "" : " + ").append((int) c);
                sum += c;
            }
            play(cb.moveHl(2, d(0.4)));
            TextMob calc = tag("hashfun(\"" + key + "\", 7):  " + sb + " = " + sum, 450, -300, 30, Colors.WHITE, false);
            pause(0.8);
            play(cb.moveHl(3, d(0.4)));
            TextMob res = tag(sum + " % 7 = " + (sum % 7), 450, -250, 34, Colors.GOLD, false);
            int pos = sum % 7;
            List<MObject> chip = chip(key, t.x(pos), -190, 110, 54, Colors.BLUE, 30);
            List<Animation> in = new ArrayList<>();
            fade(in, chip, d(0.4));
            playAll(in);
            if (t.cont[pos] == null) {
                List<Animation> dn = new ArrayList<>();
                for (MObject m : chip) dn.add(new MoveTo(m, t.x(pos), -60, d(0.6)));
                playAll(dn);
                t.put(pos, key, Colors.BLUE, d(0.3));
                for (MObject m : chip) remove(m);
                say("\"" + key + "\" goes to cell " + pos + ".", Colors.GREEN);
            } else {
                t.flash(pos, Colors.RED);
                say("\"act\" has the same letters, so the same sum and the same cell: a collision.", Colors.RED);
                pause(1.4);
                t.unflash(pos);
                fadeOutAll(d(0.5), chip);
            }
            pause(1.6);
            fadeOutAll(d(0.4), calc, res);
        }
        pause(1.0);
        clearMine();

        // slide 69
        newHeading("Hash Functions");
        List<Spec> v = new ArrayList<>();
        v.add(ln(0, "Various hash functions"));
        v.add(ln(1, "Integral value % H").tail("(H is the hash table size)").gap(80));
        v.add(ln(1, "Sum of all the characters / bytes"));
        v.add(ln(1, "ExOR of odd-indexed bits"));
        v.add(ln(1, "Integral value / M").tail("(similar values in nearby buckets)").gap(80));
        v.add(ln(1, "..."));
        v.add(ln(0, "Several hash functions are implemented at"));
        v.add(ln(3, "Arash Partow's webpage."));
        v.add(ln(1, "Bitwise (! | & ^ << >>)"));
        v.add(ln(1, "Mathematical (+ *)"));
        v.add(ln(1, "Lookup (prime numbers, magic numbers)"));
        textThenSwipe(v, -420, 1.8);

        // slide 70
        newHeading("Hash Functions");
        List<Spec> w = new ArrayList<>();
        w.add(ln(0, "Application needs to choose the right functions."));
        w.add(ln(1, "using last two bits of a pointer's address"));
        w.add(ln(1, "using % 10 for values with least count of 10"));
        w.add(ln(1, "using % 123456789 for student's roll-number"));
        w.add(ln(1, "using first character of a name to choose the bucket"));
        w.add(ln(1, "..."));
        w.add(ln(0, "Index values must distribute across the array."));
        w.add(ln(1, "reduces number of conflicts"));
        w.add(ln(1, "improves overall execution time"));
        w.add(ln(0, "But, hash functions should also be fast."));
        textThenSwipe(w, -400, 1.8);

        int H = 7;
        int[] first = new int[H], sum = new int[H];
        for (String n : NAMES) {
            first[n.charAt(0) % H]++;
            sum[hashName(n, H)]++;
        }
        say("The same 14 names in 7 buckets, with two hash functions.", Colors.LIGHT_GRAY);
        mine.addAll(bars(first, -440, 200, 38, 50, Colors.RED, "first letter % 7"));
        mine.addAll(bars(sum, 440, 200, 38, 50, Colors.GREEN, "sum of letters % 7"));
        pause(2.4);
        int maxF = 0, maxS = 0;
        for (int i = 0; i < H; i++) {
            maxF = Math.max(maxF, first[i]);
            maxS = Math.max(maxS, sum[i]);
        }
        say("Fullest bucket: " + maxF + " names with the first letter, " + maxS + " with the sum. An even spread means fewer conflicts.", Colors.GOLD);
        pause(3.6);
    }

    // ── 71-72 ────────────────────────────────────────────────────────

    private void s71to72() {
        head = writeHeading("Index Distribution");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "For equitable distribution, H is often a prime."));
        s.add(ln(0, "Consider the following hash function:"));
        s.add(ln(1, "return (key[0] + 27 * key[1] + 729 * key[2]) % H;"));
        s.add(ln(0, "Assumes at least two characters in the key"));
        s.add(ln(0, "Works well for several strings"));
        s.add(ln(0, "Skewed by first three characters").kw("Skewed", Colors.RED));
        s.add(ln(0, "Skewed if characters are not random").kw("Skewed", Colors.RED));
        keep(writeSlide(s, -380));
        pause(1.0);
        int[] skew = {9, 7, 6, 5, 4, 4, 3, 2, 2, 2, 1, 1};
        mine.addAll(bars(skew, 0, 340, 17, 52, Colors.RED, "keys per bucket: a few buckets get most of the keys (illustration)"));
        say("If the characters are not random, the first three characters pile the keys into a few buckets.", Colors.RED);
        pause(3.6);
        clearMine();

        newHeading("A Better Hash Function");
        List<Spec> t = new ArrayList<>();
        t.add(ln(0, "Uses all the characters"));
        t.add(ln(0, "Distributes better"));
        t.add(ln(0, "Exploits Horner's rule for faster computation"));
        t.add(ln(0, "Is no longer O(1), but proportional to key length."));
        keep(writeSlide(t, -380));
        CodeBox cb = code(new String[]{
                "int hashfun(char *key, int H) {",
                "    int val = 0;",
                "    while (*key) val = (val << 5) + *key++;",
                "    return val % H;",
                "}"}, -930, 20, 26, 46, 3.0);
        pause(0.8);
        say("Take \"abc\" with H = 11. Each character is folded in: val = val * 32 + character.", Colors.LIGHT_GRAY);
        int val = 0, step = 0;
        String key = "abc";
        List<TextMob> rows = new ArrayList<>();
        for (char c : key.toCharArray()) {
            play(cb.moveHl(2, d(0.4)));
            int before = val;
            val = (val << 5) + c;
            TextMob r = tag("val = (" + before + " << 5) + " + (int) c + " = " + val, 330, 40 + step * 60, 32, Colors.WHITE, false);
            rows.add(r);
            step++;
            pause(1.0);
        }
        play(cb.moveHl(3, d(0.4)));
        tag(val + " % 11 = " + (val % 11), 330, 40 + step * 60 + 20, 38, Colors.GOLD, false);
        say("All three characters take part, and each one changes the result: a better spread.", Colors.GREEN);
        pause(3.6);
    }

    // ── 73 ───────────────────────────────────────────────────────────

    private void s73() {
        head = writeHeading("Hash Table ADT");
        pause(0.5);
        CodeBox cb = code(new String[]{
                "struct Hashtable {",
                "    void insert(KeyType key, ElementType value);",
                "    void remove(KeyType key);",
                "    ElementType find(KeyType key);",
                "};"}, -880, -330, 32, 60, 3.5);
        pause(0.6);
        String[] what = {"add a key (with its value)", "take a key out", "look a key up"};
        for (int i = 0; i < 3; i++) {
            play(cb.moveHl(i + 1, d(0.4)));
            TextMob t = tag(what[i], 330, -270 + i * 60, 32, Colors.GOLD, true);
            pause(0.6);
        }
        TextMob n1 = label("In some applications, value may not be required.", -880, 120, 32, Colors.LIGHT_GRAY, true, false);
        mine.add(n1);
        play(new FadeIn(n1, d(0.7)));
        pause(0.8);
        TextMob n2 = label("If you get a deja vu feeling by looking at the ADT, don't worry; that's natural.", -880, 190, 28, Colors.GRAY, true, false);
        mine.add(n2);
        play(new FadeIn(n2, d(0.7)));
        pause(1.0);
        say("The same three operations as the BST: only the way they are carried out changes.", Colors.GREEN);
        pause(3.4);
    }

    // ── 74 ───────────────────────────────────────────────────────────

    private void s74() {
        head = writeHeading("Pitfalls");
        pause(0.5);
        String[] claims = {"Prime numbers provide equitable distribution.", "Data distribution does not matter for hashing.",
                "Hashing is O(1).", "There exists a hash function which works well across all applications.",
                "It is always possible to increase the hash table size to reduce collisions.",
                "Hash function must contain modulus operator."};
        String[] reasons = {"Primes help, but do not guarantee it.", "It does: skewed keys fill a few buckets.",
                "Only on average: collisions and long keys cost more.", "No single function suits every kind of key.",
                "A bigger table costs memory and may not be possible.", "Any function that gives 0 .. H-1 will do."};
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, claims[0]));
        s.add(ln(0, claims[1]));
        s.add(ln(0, claims[2]));
        s.add(ln(0, "There exists a hash function which works well"));
        s.add(ln(3, "across all applications."));
        s.add(ln(0, "It is always possible to increase the hash table"));
        s.add(ln(3, "size to reduce collisions."));
        s.add(ln(0, claims[5]));
        List<List<MObject>> g = writeSlide(s, -380);
        keep(g);
        pause(1.2);
        say("All six are pitfalls: none of them is true. (The one-line reasons are added.)", Colors.RED);
        double[] ys = new double[6];
        int[] idx = {0, 1, 2, 3, 5, 7};
        for (int i = 0; i < 6; i++) ys[i] = g.get(idx[i]).get(g.get(idx[i]).size() - 1).getPosition().y();
        for (int i = 0; i < 6; i++) {
            List<LineMob> m = new ArrayList<>();
            m.add(new LineMob(-905, ys[i] - 12, -875, ys[i] + 12, Colors.RED, 6));
            m.add(new LineMob(-905, ys[i] + 12, -875, ys[i] - 12, Colors.RED, 6));
            for (LineMob l : m) {
                l.setOpacity(0);
                add(l);
                mine.add(l);
            }
            for (LineMob l : m) play(new DrawLine(l, d(0.2)));
            TextMob r = label(reasons[i], 900, ys[i], 28, Colors.LIGHT_GRAY, false, false);
            r.setAlignment(javafx.scene.text.TextAlignment.RIGHT);
            r.setPosition(920, ys[i]);
            mine.add(r);
            play(new FadeIn(r, d(0.5)));
            pause(0.5);
        }
        pause(3.0);
    }

    // ── 75 ───────────────────────────────────────────────────────────

    private void s75() {
        head = writeHeading("Collisions");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Without collisions, key need not be stored.").kw("not", Colors.RED));
        s.add(ln(0, "A good distribution reduces collisions."));
        s.add(ln(0, "Multiple ways to handle collision:"));
        s.add(ln(1, "Chaining"));
        s.add(ln(1, "Open Addressing (linear probing, quadratic probing,"));
        s.add(ln(2, "double hashing)"));
        s.add(ln(1, "Rehashing"));
        textThenSwipe(s, -330, 1.8);

        HT t = table(7, 0, -80, 100, 84, 14);
        showTable(t);
        say("Two keys, 12 and 19, both hash to 12 % 7 = 5 = 19 % 7: they collide.", Colors.LIGHT_GRAY);
        pause(1.6);
        probeInsert(t, 12, 5, i -> i, Colors.BLUE, 440, 1.0);
        pause(0.6);
        // the second key finds the cell taken
        List<MObject> chip = chip("19", t.x(5), -80 - 42 - 90, 100, 54, Colors.PINK, 30);
        List<Animation> in = new ArrayList<>();
        fade(in, chip, d(0.5));
        playAll(in);
        t.flash(5, Colors.RED);
        say("Cell 5 already holds 12: where does 19 go?", Colors.RED);
        pause(2.0);
        t.unflash(5);
        fadeOutAll(d(0.4), chip);
        List<MObject> ch = new ArrayList<>();
        String[] names = {"Chaining", "Open addressing", "Rehashing"};
        Color[] cs = {Colors.TEAL, Colors.ORANGE, Colors.PURPLE};
        List<Animation> ca = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            List<MObject> c = chip(names[i], (i - 1) * 420, 230, 380, 66, cs[i], 32);
            ch.addAll(c);
            mine.addAll(c);
            for (MObject m : c) ca.add(new FadeInAt(m, 0.3 * i, d(0.6)));
        }
        playAll(ca);
        say("The next slides show how each way finds another place for the colliding key.", Colors.GOLD);
        pause(3.2);
    }

    // ── 76-77 ────────────────────────────────────────────────────────

    private void s76to77() {
        head = writeHeading("Chaining");
        pause(0.5);
        TextMob src = label("Source: hash.cpp", 760, -470, 26, Colors.GRAY, false, false);
        play(new FadeIn(src, d(0.6)));
        mine.add(src);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Allows arbitrary number of elements"));
        s.add(ln(0, "Can be used to cluster elements"));
        s.add(ln(0, "Can use List ADT"));
        s.add(ln(0, "List can actually be replaced by other data"));
        s.add(ln(3, "structures such as trees, or even hash tables!"));
        textThenSwipe(s, -330, 1.8);

        CH c = new CH(7, -700, -300, 62);
        mine.addAll(c.parts());
        List<Animation> a = new ArrayList<>();
        c.show(a, d(0.6));
        playAll(a);
        say("H = 7 buckets, hash(x) = x % 7. Each bucket holds a list. Insert 4, 10, 33, 0, 2, 12, 19.", Colors.LIGHT_GRAY);
        pause(2.0);
        int[] keys = {4, 10, 33, 0, 2, 12, 19};
        Color[] cols = {Colors.BLUE, Colors.TEAL, Colors.PINK, Colors.GOLD, Colors.PURPLE, Colors.ORANGE, Colors.GREEN};
        for (int i = 0; i < keys.length; i++) {
            int b = keys[i] % 7;
            say(keys[i] + " % 7 = " + b + ": add " + keys[i] + " to the list of bucket " + b + ".", Colors.LIGHT_GRAY);
            c.lightBucket(b, Colors.GOLD);
            c.add(keys[i], b, cols[i]);
            pause(0.4);
        }
        say("Seven keys, seven buckets: bucket 5 has a chain of three, some buckets are empty.", Colors.GREEN);
        pause(2.6);

        // slide 77: operations
        List<MObject> panel = new ArrayList<>();
        String[] ops = {"Insert: hash, then list insert. May have to apply find.", "Remove: hash, then list remove.", "Find: hash, then list search."};
        for (int i = 0; i < 3; i++) {
            TextMob t = label(ops[i], 40, -310 + i * 54, 30, Colors.WHITE, true, false);
            mine.add(t);
            play(new FadeIn(t, d(0.6)));
        }
        pause(1.0);
        say("find(12): 12 % 7 = 5, so search bucket 5's list: 33, then 12.", Colors.LIGHT_GRAY);
        c.lightBucket(5, Colors.GOLD);
        pause(0.5);
        c.lightNode(5, 0, Colors.GOLD);
        pause(0.5);
        c.lightNode(5, 1, Colors.GREEN);
        pause(1.4);
        say("find(26): 26 % 7 = 5: search 33, 12, 19: not there. Only one list was searched.", Colors.LIGHT_GRAY);
        c.lightBucket(5, Colors.GOLD);
        pause(0.4);
        for (int k = 0; k < 3; k++) {
            c.lightNode(5, k, Colors.GOLD);
            pause(0.4);
        }
        c.lightNode(5, 2, Colors.RED);
        pause(1.6);
        say("insert(26): the same walk finds it absent, then the new node joins the end of the list.", Colors.GREEN);
        c.add(26, 5, Colors.PINK);
        pause(2.6);
        say("remove(33): hash, then take 33 out of bucket 5's list.", Colors.RED);
        c.lightNode(5, 0, Colors.RED);
        pause(1.6);
        c.remove(5, 0);
        say("Bucket 5's list is now 12, 19, 26: the head points straight to 12.", Colors.GREEN);
        pause(2.4);
    }

    // ── 78 ───────────────────────────────────────────────────────────

    private void s78() {
        head = writeHeading("Load Factor");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Load factor indicates fullness of a"));
        s.add(ln(3, "hashtable."));
        s.add(ln(0, "LF = number of elements inserted / table size").kw("LF", Colors.GOLD));
        s.add(ln(1, "In our example, LF = 7/7"));
        s.add(ln(0, "In case of chaining, load factor is the"));
        s.add(ln(3, "average chain-length."));
        s.add(ln(1, "Useful for calculating the average"));
        s.add(ln(2, "hashtable search complexity."));
        s.add(ln(0, "Table size should be chosen to have LF"));
        s.add(ln(3, "close to 1."));
        s.add(ln(1, "But LF alone is not the precise measure."));
        textThenSwipe(s, -400, 2.0);

        int[] keys = {4, 10, 33, 0, 2, 12, 19};
        int[] lens = new int[7];
        for (int k : keys) lens[k % 7]++;
        mine.addAll(bars(lens, 0, 140, 60, 70, Colors.ORANGE, "keys per bucket, H = 7, hash(x) = x % 7"));
        say("7 keys in 7 buckets: LF = 7 / 7 = 1, so the average chain has length 1 ...", Colors.GOLD);
        show("LF=\\frac{7}{7}=1", 54, 0, -300);
        pause(3.0);
        int max = 0;
        for (int l : lens) max = Math.max(max, l);
        say("... but the buckets differ: the longest chain has " + max + " keys. LF alone is not the precise measure.", Colors.RED);
        pause(3.6);
    }

    // ── 79 ───────────────────────────────────────────────────────────

    private void s79() {
        head = writeHeading("Issues with Chaining");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Has low cache efficiency").kw("low cache efficiency", Colors.RED));
        s.add(ln(0, "Uses pointers (makes code complicated)"));
        s.add(ln(0, "Need to maintain two data structures"));
        keep(writeSlide(s, -330));
        pause(0.8);

        // memory strip: an array is contiguous, a chain is scattered
        TextMob l1 = tag("array: neighbouring cells", -880, -60, 30, Colors.GREEN, true);
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            RectMob c = new RectMob(86, 60).setCornerRadius(8);
            c.setFillColor(Colors.withAlpha(Colors.GREEN, 0.3));
            c.setStrokeColor(Colors.GREEN);
            c.setStrokeWidth(2.4);
            c.setPosition(-180 + i * 92, -60);
            c.setOpacity(0);
            add(c);
            mine.add(c);
            in.add(new FadeInAt(c, 0.08 * i, d(0.4)));
        }
        playAll(in);
        TextMob l2 = tag("chain: nodes anywhere in memory", -880, 110, 30, Colors.RED, true);
        double[] xs = {-170, 330, 40, 500, -90, 250, 620, 150};
        double[] ys = {120, 160, 90, 110, 175, 70, 150, 190};
        List<Animation> in2 = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            RectMob c = new RectMob(86, 60).setCornerRadius(8);
            c.setFillColor(Colors.withAlpha(Colors.RED, 0.3));
            c.setStrokeColor(Colors.RED);
            c.setStrokeWidth(2.4);
            c.setPosition(xs[i], ys[i]);
            c.setOpacity(0);
            add(c);
            mine.add(c);
            in2.add(new FadeInAt(c, 0.12 * i, d(0.4)));
        }
        for (int i = 0; i < 5; i++) {
            Link l = arrow(xs[i] + 44, ys[i], xs[i + 1] - 46, ys[i + 1], Colors.LIGHT_GRAY, 3);
            mine.add(l);
            in2.add(new DrawLinkAt(l, 0.12 * i + 0.3, d(0.4)));
        }
        playAll(in2);
        say("An array is read from nearby memory, which the cache likes; a chain jumps from pointer to pointer.", Colors.LIGHT_GRAY);
        pause(3.0);
        StrokeTextMob q = stroke("Can we maintain elements in the array itself?", 0, 330, 44, Colors.GOLD, false);
        play(new Write(q, d(2.6)));
        mine.add(q);
        pause(3.0);
    }

    // ── 80 ───────────────────────────────────────────────────────────

    private void s80() {
        head = writeHeading("Open Addressing");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Has only the hashtable array."));
        s.add(ln(0, "On collision, try some other position in the"));
        s.add(ln(3, "hashtable array."));
        s.add(ln(0, "Formally, positions (hash(X) + F(i)) % H are"));
        s.add(ln(3, "tried in succession."));
        s.add(ln(1, "F(0) = 0"));
        s.add(ln(0, "Function F is the collision resolution strategy."));
        s.add(ln(1, "F(i) = i is linear probing"));
        s.add(ln(1, "F(i) = i^2 is quadratic probing"));
        s.add(ln(1, "F(i) = i * hash2(i) is double hashing"));
        textThenSwipe(s, -400, 1.8);

        show("\\bigl(hash(X)+F(i)\\bigr)\\,\\%\\,H,\\qquad i=0,1,2,\\dots", 56, 0, -250);
        pause(1.0);
        String[] names = {"linear probing", "quadratic probing", "double hashing"};
        String[] fs = {"F(i) = i", "F(i) = i^2", "F(i) = i * hash2"};
        Color[] cs = {Colors.TEAL, Colors.ORANGE, Colors.PURPLE};
        List<Animation> in = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            List<MObject> c = chip(names[i], (i - 1) * 520, -60, 440, 70, cs[i], 34);
            TextMob f = label(fs[i], (i - 1) * 520, 40, 36, cs[i], false, true);
            mine.addAll(c);
            mine.add(f);
            for (MObject m : c) in.add(new FadeInAt(m, 0.4 * i, d(0.6)));
            in.add(new FadeInAt(f, 0.4 * i + 0.3, d(0.6)));
        }
        playAll(in);
        say("Probe 0 is the hash position itself; probe i adds F(i). Three choices for F.", Colors.GOLD);
        pause(3.6);
    }

    // ── 81 ───────────────────────────────────────────────────────────

    private void s81() {
        head = writeHeading("Linear Probing");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "(hash(X) + F(i)) % H and F(i) = i"));
        s.add(ln(0, "One may have a different linear function of i."));
        s.add(ln(1, "Identity function is the most common."));
        s.add(ln(0, "Let H = 7, hash(X) = X % 7"));
        s.add(ln(0, "Elements: 4, 12, 70, 11, 20"));
        s.add(ln(0, "Elements: 20, 11, 4, 12, 70"));
        s.add(ln(0, "Elements: 11, 20, 12, 70, 4"));
        s.add(ln(1, "Check number of elements colliding"));
        s.add(ln(1, "Check total number of collisions"));
        textThenSwipe(s, -400, 1.8);

        int[][] orders = {{4, 12, 70, 11, 20}, {20, 11, 4, 12, 70}, {11, 20, 12, 70, 4}};
        Color[] oc = {Colors.BLUE, Colors.TEAL, Colors.PINK};
        int[] totals = new int[3], colliding = new int[3];
        double[] ys = {-250, -20, 210};
        HT[] ts = new HT[3];
        for (int r = 0; r < 3; r++) {
            ts[r] = table(7, 330, ys[r], 96, 80, 12);
            StringBuilder sb = new StringBuilder("Elements: ");
            for (int i = 0; i < 5; i++) sb.append(i == 0 ? "" : ", ").append(orders[r][i]);
            tag(sb.toString(), -900, ys[r], 30, oc[r], true);
            showTable(ts[r]);
        }
        say("H = 7, hash(X) = X % 7. Insert each list of five elements, one by one, into its own table.", Colors.LIGHT_GRAY);
        pause(1.6);
        for (int r = 0; r < 3; r++) {
            for (int k : orders[r]) {
                int c = probeInsert(ts[r], k, k % 7, i -> i, oc[r], 900, 0.55);
                totals[r] += c;
                if (c > 0) colliding[r]++;
            }
            say("Order " + (r + 1) + " done: " + colliding[r] + " element" + (colliding[r] == 1 ? "" : "s") + " collided, " + totals[r] + " collisions in all.", Colors.GOLD);
            TextMob res = tag(colliding[r] + " colliding, " + totals[r] + " collisions", -900, ys[r] + 56, 26, Colors.GOLD, true);
            pause(1.4);
        }
        say("The final table depends on the order, and so do the elements that collide.", Colors.GREEN);
        pause(2.0);
        TextMob cw = tag("Classwork: Write codes for insert, remove and find.", -900, 400, 32, Colors.RED, true);
        pause(3.0);
    }

    // ── 82-84 ────────────────────────────────────────────────────────

    private static final String[] INSERT_CODE = {
            "int index = hash(e);",
            "for (int ii = 0; ii < H; ++ii) {",
            "    if (arr[index] == emptycell) {",
            "        arr[index] = e;",
            "        return true;",
            "    }",
            "    index = (index + 1) % H;",
            "}",
            "std::cerr << \"Hashtable is full.\" << std::endl;",
            "return false;"};

    private static final String[] REMOVE_CODE = {
            "int index = hash(e);",
            "for (int ii = 0; ii < H; ++ii) {",
            "    if (arr[index] == e) {",
            "        arr[index] = deletedcell;",
            "        return true;",
            "    } else if (arr[index] == emptycell) {",
            "        std::cout << e << \" not present.\\n\";",
            "        return false;",
            "    }",
            "    index = (index + 1) % H;",
            "}",
            "std::cerr << e << \" not present.\\n\";",
            "return false;"};

    private static final String[] FIND_CODE = {
            "int index = hash(e);",
            "for (int ii = 0; ii < H; ++ii) {",
            "    if (arr[index] == e) {",
            "        std::cout << e << \" is found\" << std::endl;",
            "        return true;",
            "    } else if (arr[index] == emptycell) {",
            "        std::cout << e << \" not present.\\n\";",
            "        return false;",
            "    }",
            "    index = (index + 1) % H;",
            "}",
            "std::cerr << e << \" not present.\\n\";",
            "return false;"};

    private void s82to84() {
        head = writeHeading("Linear Probing: Operations");
        pause(0.5);
        HT t = table(7, 560, -60, 82, 80, 10);
        int[] start = {70, 20, -1, -1, 4, 12, 11};
        for (int i = 0; i < 7; i++) if (start[i] >= 0) t.set(i, String.valueOf(start[i]), Colors.BLUE);
        showTable(t);
        TextMob info = tag("H = 7, hash(e) = e % 7", 560, -310, 30, Colors.LIGHT_GRAY, false);
        Ptr ptr = pointer("index", t.x(0), t.cy - 40 - 8, true, Colors.ORANGE);
        mine.addAll(ptr.parts());

        operation(t, ptr, "insert", INSERT_CODE, 18);
        operation(t, ptr, "remove", REMOVE_CODE, 11);
        operation(t, ptr, "find", FIND_CODE, 20);
        operation(t, ptr, "find", FIND_CODE, 25);
        say("Each operation walks the cells one by one: in the worst case all H of them, O(H).", Colors.GOLD);
        List<MObject> oh = chip("O(H)", 560, 200, 160, 64, Colors.ORANGE, 38);
        List<Animation> in = new ArrayList<>();
        fade(in, oh, d(0.6));
        playAll(in);
        mine.addAll(oh);
        pause(3.0);
    }

    /** Runs one operation: the code box beside the table, a highlight bar on the line, an index pointer on the cell. */
    private void operation(HT t, Ptr ptr, String op, String[] lines, int e) {
        CodeBox cb = code(lines, -930, -330, 22, 38, 3.0);
        TextMob title = tag(op + "(" + e + ")", 560, -245, 40, Colors.GOLD, false);
        int h = e % 7;
        List<Animation> a = new ArrayList<>();
        ptr.go(a, t.x(h), d(0.5));
        if (ptr.arrow.getOpacity() < 0.5) {
            a.add(new FadeIn(ptr.arrow, d(0.5)));
            a.add(new FadeIn(ptr.lab, d(0.5)));
        }
        playAll(a);
        play(cb.moveHl(0, d(0.3)));
        say("hash(" + e + ") = " + e + " % 7 = " + h + ": start at cell " + h + ".", Colors.LIGHT_GRAY);
        pause(0.8);
        int index = h;
        boolean isInsert = op.equals("insert"), isRemove = op.equals("remove");
        for (int ii = 0; ii < 7; ii++) {
            play(cb.moveHl(1, d(0.3)));
            String c = t.cont[index];
            if (isInsert) {
                play(cb.moveHl(2, d(0.3)));
                t.flash(index, Colors.GOLD);
                pause(0.4);
                if (c == null) {
                    play(cb.moveHl(3, d(0.3)));
                    t.put(index, String.valueOf(e), Colors.PINK, d(0.4));
                    play(cb.moveHl(4, d(0.3)));
                    say("Cell " + index + " is empty: insert " + e + " there (return true).", Colors.GREEN);
                    pause(1.4);
                    break;
                }
                t.flash(index, Colors.RED);
                pause(0.3);
                t.unflash(index);
                play(cb.moveHl(6, d(0.3)));
            } else {
                play(cb.moveHl(2, d(0.3)));
                t.flash(index, Colors.GOLD);
                pause(0.4);
                if (c != null && c.equals(String.valueOf(e))) {
                    play(cb.moveHl(3, d(0.3)));
                    if (isRemove) {
                        t.put(index, "D", Colors.GRAY, d(0.4));
                        say("Found " + e + " at cell " + index + ": mark it deleted (deletedcell), do not empty it.", Colors.GREEN);
                    } else {
                        say(e + " is found at cell " + index + ".", Colors.GREEN);
                    }
                    play(cb.moveHl(4, d(0.3)));
                    pause(1.6);
                    break;
                }
                play(cb.moveHl(5, d(0.3)));
                if (c == null) {
                    play(cb.moveHl(6, d(0.3)));
                    say("Cell " + index + " is empty: " + e + " cannot be further along. Not present.", Colors.RED);
                    play(cb.moveHl(7, d(0.3)));
                    pause(1.6);
                    break;
                }
                t.flash(index, Colors.RED);
                pause(0.3);
                t.unflash(index);
                if (c.equals("D")) say("Cell " + index + " is deleted, not empty: keep looking.", Colors.ORANGE);
                else say("Cell " + index + " holds " + c + ": not " + e + ". Next cell.", Colors.LIGHT_GRAY);
                play(cb.moveHl(9, d(0.3)));
            }
            t.unflash(index);
            index = (index + 1) % 7;
            List<Animation> mv = new ArrayList<>();
            ptr.go(mv, t.x(index), d(0.4));
            playAll(mv);
        }
        t.unflash(index);
        fadeOutAll(d(0.7), cb.parts());
        fadeOutAll(d(0.4), title);
        pause(0.4);
    }

    // ── 85 ───────────────────────────────────────────────────────────

    private void s85() {
        head = writeHeading("Primary Clustering");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Due to linear probing, groups of elements"));
        s.add(ln(3, "may get formed in the array."));
        s.add(ln(0, "This phenomenon is called clustering.").kw("clustering", Colors.BLUE));
        s.add(ln(0, "Clustering increases the number of"));
        s.add(ln(3, "collisions."));
        s.add(ln(1, "in turn, the execution time."));
        s.add(ln(0, "Intuitively, this works against uniform"));
        s.add(ln(3, "distribution."));
        s.add(ln(0, "Deletions are useful in such a scenario."));
        s.add(ln(0, "Alternatively, probing should leave holes."));
        textThenSwipe(s, -400, 2.0);

        HT t = table(13, 0, -80, 100, 84, 14);
        showTable(t);
        say("H = 13, hash(X) = X % 13. Keys 3, 16, 29, 42 all hash to 3.", Colors.LIGHT_GRAY);
        pause(1.8);
        int[] keys = {3, 16, 29, 42};
        int total = 0;
        for (int k : keys) total += probeInsert(t, k, k % 13, i -> i, Colors.BLUE, 440, 0.6);
        List<MObject> br = chip("a cluster of 4 cells", 0, 120, 420, 60, Colors.RED, 30);
        br.get(0).setPosition((t.x(3) + t.x(6)) / 2, 120);
        br.get(1).setPosition((t.x(3) + t.x(6)) / 2, 120);
        List<Animation> ba = new ArrayList<>();
        fade(ba, br, d(0.6));
        playAll(ba);
        mine.addAll(br);
        say("Each collision lengthened the run: " + total + " collisions to build a cluster of 4.", Colors.RED);
        pause(2.6);
        say("Now any key hashing to 3, 4, 5 or 6 must walk to the end of the cluster, and then joins it.", Colors.GOLD);
        pause(2.4);
        int c = probeInsert(t, 5, 5, i -> i, Colors.PINK, 440, 0.8);
        say("Key 5 hashed to cell 5, inside the cluster: " + c + " collisions, and the cluster grew to 5 cells.", Colors.RED);
        pause(3.4);
    }

    // ── 86 ───────────────────────────────────────────────────────────

    private void s86() {
        head = writeHeading("Quadratic Probing");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "(hash(X) + F(i)) % H and F(i) = i^2"));
        s.add(ln(0, "Distributes the indices better"));
        s.add(ln(1, "Needs H to be prime"));
        s.add(ln(1, "Practically needs load factor to be at max. 0.5"));
        s.add(ln(0, "Avoids primary clustering issue"));
        s.add(ln(1, "Leaves holes"));
        s.add(ln(0, "Not guaranteed to cover all the indices"));
        textThenSwipe(s, -380, 1.8);

        HT t = table(11, 0, -80, 100, 84, 12);
        showTable(t);
        say("H = 11 (a prime), hash(X) = X % 11. Keys 22, 33, 44 all hash to 0.", Colors.LIGHT_GRAY);
        pause(1.6);
        int[] keys = {22, 33, 44};
        for (int k : keys) probeInsert(t, k, 0, i -> i * i, Colors.BLUE, 440, 0.7);
        say("Offsets 0, 1, 4: the keys sit in cells 0, 1 and 4: holes are left in between. Linear probing would give 0, 1, 2.", Colors.GOLD);
        pause(3.6);
        fadeOutAll(d(0.9), mine);
        mine.clear();

        // not every index is reached
        HT u = table(7, 0, -120, 100, 84, 14);
        showTable(u);
        say("H = 7: the offsets i^2 % 7 for i = 0, 1, 2 ... are 0, 1, 4, 2, 2, 4, 1.", Colors.LIGHT_GRAY);
        pause(2.0);
        boolean[] seen = new boolean[7];
        for (int i = 0; i < 7; i++) {
            int pos = (i * i) % 7;
            if (!seen[pos]) {
                seen[pos] = true;
                u.flash(pos, Colors.GOLD);
                pause(0.5);
            }
        }
        int reached = 0;
        for (boolean b : seen) if (b) reached++;
        say("Only " + reached + " of the 7 cells are ever probed: quadratic probing need not cover all the indices.", Colors.RED);
        pause(3.6);
    }

    // ── 87 ───────────────────────────────────────────────────────────

    private void s87() {
        head = writeHeading("Quadratic Probing Guarantee");
        pause(0.5);
        StrokeTextMob t1 = stroke("Theorem: If table size is prime, then using", 0, -350, 38, Colors.GOLD, false);
        StrokeTextMob t2 = stroke("quadratic probing, a new element can always be", 0, -296, 38, Colors.GOLD, false);
        StrokeTextMob t3 = stroke("inserted if the table is at least half empty.", 0, -242, 38, Colors.GOLD, false);
        play(new Write(t1, d(2.2)));
        play(new Write(t2, d(2.2)));
        play(new Write(t3, d(2.2)));
        mine.add(t1);
        mine.add(t2);
        mine.add(t3);
        pause(1.4);

        // the picture: H = 11, the first H/2 probes are distinct
        HT t = table(11, 0, -60, 100, 76, 12);
        for (int i = 0; i < 11; i++) t.box[i].setOpacity(0);
        showTable(t);
        say("Try it: H = 11 and hash(X) = 0. The first H/2 probes, i = 1 ... 5, land on i^2 % 11 = 1, 4, 9, 5, 3.", Colors.LIGHT_GRAY);
        pause(1.6);
        for (int i = 1; i <= 5; i++) {
            int pos = (i * i) % 11;
            t.flash(pos, Colors.GOLD);
            TextMob n = label("i=" + i, t.x(pos), t.cy - 76, 24, Colors.GOLD, false, true);
            mine.add(n);
            play(new FadeIn(n, d(0.4)));
            pause(0.5);
        }
        pause(1.0);
        say("Five different cells. If the table is at least half empty, at most 5 of 11 are full: one of those five is empty.", Colors.GREEN);
        pause(3.4);
        clearMine();

        // the proof by contradiction
        newHeading("Quadratic Probing Guarantee");
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Proof: when tried for indices hash(X) + i^2, we get at"));
        s.add(ln(3, "least H/2 different indices. At least one is empty."));
        s.add(ln(0, "Take the first H/2 probes (0 < i, j) and suppose two of them"));
        s.add(ln(3, "give the same index, with i != j."));
        keep(writeSlide(s, -350));
        pause(0.8);
        show("hash(X)+i^2\\equiv hash(X)+j^2\\pmod H", 40, 0, -80);
        pause(0.8);
        show("i^2\\equiv j^2\\pmod H", 40, 0, -10);
        pause(0.8);
        show("(i-j)(i+j)\\equiv 0\\pmod H", 40, 0, 60);
        pause(1.0);
        say("H is prime, so H divides (i - j) or (i + j).", Colors.LIGHT_GRAY);
        pause(2.2);
        TextMob r1 = tag("(i - j) = 0 is not possible, since i != j.", 0, 150, 32, Colors.RED, false);
        pause(1.6);
        TextMob r2 = tag("(i + j) = 0 is not possible, since 0 < i, j < H/2.", 0, 205, 32, Colors.RED, false);
        pause(1.8);
        say("So the first H/2 locations are distinct, and the pigeon-hole principle allows the insertion.", Colors.GREEN);
        pause(3.6);
    }

    // ── 88 ───────────────────────────────────────────────────────────

    private void s88() {
        head = writeHeading("Secondary Clustering");
        pause(0.5);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "Does quadratic probing avoid primary clustering?"));
        s.add(ln(0, "Does it lead to any issue?"));
        s.add(ln(1, "Two elements mapping to the same initial index"));
        s.add(ln(2, "continue to conflict."));
        s.add(ln(1, "This is called secondary clustering").kw("secondary clustering", Colors.BLUE));
        s.add(ln(0, "Can be resolved using double hashing"));
        textThenSwipe(s, -380, 1.8);

        HT t = table(11, 0, 20, 100, 84, 12);
        showTable(t);
        say("H = 11. Keys 22 and 33 both hash to 0, so both follow the same probes: 0, 1, 4, 9, 5 ...", Colors.LIGHT_GRAY);
        pause(1.8);
        probeInsert(t, 22, 0, i -> i * i, Colors.BLUE, 440, 0.7);
        pause(0.6);
        say("Now 33: its probe sequence is exactly the same as 22's, so it must go through every cell 22 passed.", Colors.GOLD);
        int c = probeInsert(t, 33, 0, i -> i * i, Colors.PINK, 440, 0.7);
        pause(1.0);
        for (int k : new int[]{44, 55}) c += probeInsert(t, k, 0, i -> i * i, Colors.TEAL, 440, 0.5);
        say("The keys with the same initial index keep conflicting, one after the other: secondary clustering.", Colors.RED);
        pause(3.6);
    }

    // ── 89 ───────────────────────────────────────────────────────────

    private void s89() {
        head = writeHeading("Double Hashing");
        pause(0.5);
        TextMob src = label("Source: hash-open.h and .cpp", 700, -470, 26, Colors.GRAY, false, false);
        play(new FadeIn(src, d(0.6)));
        mine.add(src);
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "(hash(X) + F(i)) % H and F(i) = i * hash2(X)"));
        s.add(ln(0, "Distributes elements further"));
        s.add(ln(0, "Avoids primary as well as secondary clustering").kw("primary", Colors.BLUE).kw("secondary", Colors.BLUE));
        s.add(ln(0, "hash2 must not evaluate to zero").kw("must not", Colors.RED));
        s.add(ln(0, "H being prime becomes more important from"));
        s.add(ln(3, "linear to quadratic, and from quadratic to double."));
        s.add(ln(0, "Needs another hash function"));
        textThenSwipe(s, -380, 1.8);

        HT t = table(11, 0, 20, 100, 84, 12);
        showTable(t);
        TextMob f = tag("hash(X) = X % 11,   hash2(X) = 7 - (X % 7)", 0, -250, 34, Colors.WHITE, false);
        say("The step size depends on the key. Keys 22, 33, 44 all hash to 0, but hash2 gives 6, 2 and 5.", Colors.LIGHT_GRAY);
        pause(2.4);
        int[] keys = {22, 33, 44};
        Color[] cols = {Colors.BLUE, Colors.PINK, Colors.TEAL};
        for (int i = 0; i < 3; i++) {
            int k = keys[i];
            int h2 = 7 - (k % 7);
            say("Key " + k + ": hash2 = " + h2 + ", so the offsets are 0, " + h2 + ", " + 2 * h2 + " ...", Colors.GOLD);
            pause(1.0);
            probeInsert(t, k, k % 11, j -> j * h2, cols[i], 440, 0.7);
        }
        say("Three different probe sequences: no primary clustering and no secondary clustering.", Colors.GREEN);
        pause(3.6);
    }

    // ── 90 ───────────────────────────────────────────────────────────

    private void s90() {
        head = writeHeading("Learning Outcomes");
        pause(0.5);
        String[] titles = {"BST", "AVL", "Splay Trees", "Hash Tables"};
        String[][] subs = {{"add, remove, find", "Implementation, complexity"}, {"add, remove, find"}, {"splaying"},
                {"Chaining", "Linear probing", "Quadratic probing", "Double hashing"}};
        Color[] cs = {Colors.RED, Colors.RED, Colors.RED, Colors.RED};
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "BST: add, remove, find").kw("BST", Colors.RED));
        s.add(ln(1, "Implementation, complexity"));
        s.add(ln(0, "AVL: add, remove, find").kw("AVL", Colors.RED));
        s.add(ln(0, "Splay Trees: splaying").kw("Splay Trees", Colors.RED));
        s.add(ln(0, "Hash Tables").kw("Hash Tables", Colors.RED));
        s.add(ln(1, "Chaining"));
        s.add(ln(1, "Linear probing"));
        s.add(ln(1, "Quadratic probing"));
        s.add(ln(1, "Double hashing"));
        List<List<MObject>> text = writeSlide(s, -330);
        keep(text);
        TextMob hw = tag("Homework: Exercises in Chapter 5.", 0, 400, 36, Colors.GOLD, false);
        pause(1.0);
        // a small picture for each topic
        GT t1 = mini(bstOf(4, 2, 6, 1, 3, 5, 7), 520, -330, 12, 8, 46);
        t1.build(0.1);
        mine.addAll(t1.parts());
        GT t2 = mini(bstOf(4, 2, 6, 1, 3, 5, 7), 720, -170, 12, 8, 46);
        t2.build(0.1);
        mine.addAll(t2.parts());
        HT h = table(7, 560, 140, 62, 52, 6);
        int[] ks = {70, 20, -1, -1, 4, 12, 11};
        for (int i = 0; i < 7; i++) if (ks[i] >= 0) h.set(i, String.valueOf(ks[i]), Colors.BLUE);
        for (int i = 0; i < 7; i++) h.val[i].setFontSize(24);
        showTable(h);
        pause(3.4);
        clearMine();
        fadeOutAll(d(0.9), head);
        head = null;
        madeBy();
    }

    private void madeBy() {
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
        HT t = table(7, 0, 150, 70, 60, 10);
        int[] ks = {70, 20, -1, -1, 4, 12, 11};
        for (int i = 0; i < 7; i++) if (ks[i] >= 0) t.set(i, String.valueOf(ks[i]), Colors.BLUE);
        showTable(t);
        TextMob course = label("CS5013  ·  LecViz", 0, 360, 30, Colors.GRAY, false, false);
        play(new FadeIn(course, d(0.8)));
        mine.add(course);
        pause(3.2);
    }
}
