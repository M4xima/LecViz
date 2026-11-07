# LecViz: 3Blue1Brown-style lecture videos, in pure Java

LecViz turns lecture slides into short animated videos in the style of
[3Blue1Brown](https://www.3blue1brown.com/) (Grant Sanderson's Manim). It is written in Java
(JavaFX canvas + JavaCV/FFmpeg) and was built for Prof. Rupesh Nasre's **Programming and Data Structures (PDS)**
course at IIT Madras (CS5013 project, by Karthik CS23B018 and Tejaswi CS23B023).

So far it has produced **all eight decks of the course**, one clip per sub-topic, every slide covered:

| Deck | Slides | Clips | Length | Folder | Key prefix |
|---|---|---|---|---|---|
| Intro and logistics (`lectures/0-intro+logistics.pdf`) | 1–12 | 5 | 6 min 45 s | `output/intro/` | `intro_` |
| Complexity (`lectures/2-complexity.pdf`) | 1–27 | 15 | 12 min 30 s | `output/complexity/` | `cx_` |
| Arrays (`lectures/3-arrays.pdf`) | 1–31 | 19 | 44 min 52 s | `output/arrays/` | `array_` |
| Lists, stacks and queues (`lectures/4-lists.pdf`) | 1–48 | 26 | 54 min 16 s | `output/lists/` | `list_` |
| Trees (`lectures/5-trees.pdf`) | all | 34 | 52 min 36 s | `output/trees/` | `tree_` |
| Dictionary: BST, AVL, splay, hash tables (`lectures/6-dictionary.pdf`) | 1–90 | 72 | 77 min 38 s | `output/dictionary/` | `dict_` |
| Priority queues (`lectures/7-priorityqueues.pdf`) | all | 15 | not re-measured here | `output/pq/` | `pq_` |
| Graphs (`lectures/8-graphs.pdf`) | 1–30 | 24 | 27 min 19 s | `output/graphs/` | `graph_` |

(`output/pixel/` holds three extra clips with the `pixel_` prefix.)

Everything is 1920×1080 at 60 fps. Text is written in the pen-stroke style (the outline is traced, then filled from
left to right), code is typed in and walked through with a highlight bar, and every algorithm runs on a live picture
(array cells, pointers, stacks, queues, trees).

---

## Watching the videos

The finished clips are in the repository, named by the printed slide numbers (`array_16to17.mp4` is slides 16–17,
`list_21.mp4` is slide 21; the decks skip a slide number here and there).

To watch a whole deck as one video, join the clips (no re-encoding, a few seconds, nothing extra to install):

```bash
./combine.sh arrays          # Windows: combine.bat arrays
./combine.sh lists           # any folder under output/ works: trees, dictionary, graphs, complexity, intro, pq
```

This writes `output/combined/<folder>_full.mp4` (for arrays and lists about 0.8 GB and 1.2 GB; the later decks are
of the same order). These files are over GitHub's 100 MB file limit, so `output/combined/` is git-ignored and is not
stored in the repository. The full arrays video is also attached to the GitHub release `arrays-full-v1` (visible to people who have
access to this repository).

### Arrays clips (`output/arrays/`)

| Clip | Slides | Topic |
|---|---|---|
| `array_1to3` | 1–3 | Title, properties, array expressions |
| `array_3to4` | 3–4 | Row-major and column-major storage |
| `array_5` | 5 | Linear and binary search |
| `array_6` | 6 | Matrices, knight's tour |
| `array_7to9` | 7–9 | Searching a sorted matrix (three approaches) |
| `array_10to12` | 10–12 | Classwork: quiz, merge, histogram, product, absent roll number |
| `array_13` | 13 | Classwork: N-queens |
| `array_14to15` | 14–15 | Sorting algorithms at a glance |
| `array_16to17` | 16–17 | Bubble sort |
| `array_18to19` | 18–19 | Insertion sort |
| `array_20to21` | 20–21 | Shell sort |
| `array_22` to `array_25` | 22–25 | Selection, heap, quick and merge sort |
| `array_27` to `array_29` | 27–29 | Bucket, counting and radix sort |
| `array_30to31` | 30–31 | Summary, DSAP usage, end card |

### Lists clips (`output/lists/`)

| Clip | Slides | Topic |
|---|---|---|
| `list_1to4` | 1–4 | ADTs, the List ADT, other ADTs |
| `list_5to6`, `list_7to8` | 5–8 | A list on an array, on a linked list, arrays vs lists |
| `list_10` | 10 | List insert |
| `list_13to14`, `list_15`, `list_16` | 13–16 | Print, find, remove, pitfalls |
| `list_17to18` | 17–18 | Doubly linked and circular lists |
| `list_19to20` | 19–20 | Polynomial ADT |
| `list_21`, `list_22` | 21–22 | List reversal, recursion |
| `list_23to24`, `list_25` | 23–25 | Stack ADT, list vs stack, stack implementation |
| `list_26to27` | 26–27 | Balanced parentheses |
| `list_29to30` to `list_37` | 29–37 | Expressions, prefix/postfix, evaluation, infix to postfix |
| `list_38to39` | 38–39 | Queue, Queue ADT |
| `list_40to41`, `list_42` | 40–42 | Call center and its simulation |
| `list_43to44`, `list_45to46` | 43–46 | Queue on an array, wrap-around, empty vs full |
| `list_47`, `list_48` | 47–48 | Practice problems with solutions, outcomes, end card |

### Dictionary clips (`output/dictionary/`, slides 1–90)

| Clips | Slides | Topic |
|---|---|---|
| `dict_1to2` … `dict_24b` | 1–24 | Binary search trees: search, insert, orders, remove (three cases), complexity, exercises |
| `dict_25to26` … `dict_48` | 25–48 | AVL trees: balance factors, the four rotation cases, insertion and deletion run for real |
| `dict_49` … `dict_66` | 49–66 | Splay trees: move to root, zig / zig-zig / zig-zag, amortized analysis |
| `dict_67` … `dict_90` | 67–90 | Hash tables: hash functions, collisions, chaining, linear / quadratic / double probing, outcomes, end card |

### Graph clips (`output/graphs/`, slides 1–30)

| Clips | Slides | Topic |
|---|---|---|
| `graph_1to2`, `graph_3`, `graph_4`, `graph_5to6`, `graph_7to8` | 1–8 | Title, what a graph is, nomenclature, relations, the 1822 puzzle |
| `graph_9` … `graph_13` | 9–13 | Adjacency matrix and list, edge list and CSR, the classwork, primitive and high-level operations |
| `graph_14` … `graph_19` | 14–19 | Traversals, BFS and DFS (queue, call stack, the BFS and DFS trees), their code, directory listing |
| `graph_20` … `graph_30` | 20–30 | Topological sort (Kahn), Euler tours, Hamiltonian cycle, practice problems, outcomes, end card |

### Complexity clips (`output/complexity/`, slides 1–27)

`cx_1to3` (title, algorithms, efficiency), `cx_4` (misconceptions), `cx_5to7` (counted operations), `cx_8to9` (big O
drawn), `cx_10to11` (examples, the recurrence), `cx_12to13` (the family of symbols), `cx_14` (complexity arithmetic),
`cx_15to16` (typical complexities, limit test), `cx_17` (facets), `cx_18to19`, `cx_20to22`, `cx_23`, `cx_24` (the
maximum subsequence sum, three algorithms), `cx_25to26` (binary search), `cx_27` (exercises, end card).

### Intro clips (`output/intro/`, slides 1–12)

`intro_1` (title), `intro_2to3` (placement, the cab example), `intro_4to5` (more examples, misconceptions),
`intro_6to8` (what it is, the standard structures, outcomes), `intro_9to12` (logistics, evaluation, how to get the
most, end card).

### What the clips add to, or correct in, the slides

- Everything on a table, order or count comes from running the algorithm on a small model, so a few of the deck's
  own figures are corrected on the screen with a grey note: the graph slides leave out the edge 4–5 in the lists and
  4–0 in the edge list; the splay "adversary" slide says access 2 costs N−2 links, the run shows 4.
- One-line reasons and corrections were added to the pitfall and misconception slides (marked "added"), the BST
  slide 4 `remove()` signature is fixed, and the unanswered slides 23–24 of the dictionary deck are answered.
- The lyrics on slide 7 of the graph deck are not reproduced (the clip describes them).

Slides 9, 11, 12 and 28 of the lists deck have no clip (a file-name-only slide, a missing number, a quiz slide, and a
file-name-only slide).

---

## Repository layout

```
lecviz/
├── lectures/                 The course slides (PDF)
├── output/
│   ├── arrays/ lists/ trees/ pq/ dictionary/ graphs/ complexity/ intro/ pixel/
│   │                         Finished clips (tracked in git), one folder per deck
│   └── combined/             Joined full videos (git-ignored)
├── docs/new-slides-prompt.md A prompt + code guide for making clips from new slides
├── combine.sh, combine.bat   Join the clips of one folder into one video
├── pom.xml                   Maven build
└── src/main/java/com/lecviz/
    ├── core/                 Scene (play/hold/camera, 60 fps loop), Animation, MObject, VideoRenderer
    ├── mobjects/             StrokeTextMob (pen-stroke text), TextMob, RectMob, CircleMob, ArrowMob, LaTeXMob, ...
    ├── animations/           Write, FadeIn, FadeOut, MoveTo, ScaleTo, ColorChange, ...
    ├── utils/                Colors, Easing, Vec2, Vec3
    ├── tools/CombineClips    The joiner behind combine.sh
    ├── scenes/
    │   ├── PDSSortClipBase   Shared kit: slide text, tables, code boxes, pointers, bars
    │   ├── PDSListClipBase   Kit for nodes, links, stacks, queues, call stacks, trees (extends the above)
    │   ├── PDSTreeClipBase   General trees (GN nodes, GT drawing)             <- PDSListClipBase
    │   ├── PDSDictClipBase   Binary search trees (insert, find, remove)       <- PDSTreeClipBase
    │   ├── PDSAvlClipBase    DT engine: rotations, AVL rebalancing, splaying  <- PDSDictClipBase
    │   ├── PDSHashClipBase   Hash cells, probing, chained buckets, histograms <- PDSAvlClipBase
    │   ├── PDSGraphClipBase  Graph model G, graph picture GV, Grid, Seq       <- PDSHashClipBase
    │   └── PDS*Scene         One scene class per group of clips (the part is the constructor argument)
    ├── parser/, generator/   An early attempt to build videos straight from slides (not used for the clips)
    └── LecVizApp.java        The scene registry (SCENES map) and the render entry point
```

The older prototypes (`PDSArrayScene`, `PDSLinkedListScene`, `PDSStackScene`, ... and their keys `arrays`,
`linkedlist`, `stack`, `tree`) are the first long-video versions and are kept for reference only.

---

## Prerequisites and build

| Tool | Version | Purpose |
|---|---|---|
| Java JDK | 17+ | Language runtime |
| Maven | 3.8+ | Build and dependencies (JavaFX 21, JavaCV 1.5.9, JLaTeXMath, ... are Maven dependencies) |

JavaCV brings its own FFmpeg, so nothing else needs installing. The first build downloads about 500 MB.

```bash
# macOS:           brew install openjdk@17 maven
# Ubuntu/Debian:   sudo apt install openjdk-17-jdk maven
# Windows:         JDK 17 from https://adoptium.net and Maven from https://maven.apache.org
mvn clean compile
```

**macOS note.** Video encoding uses the Apple hardware encoder `h264_videotoolbox` (set in
`core/VideoRenderer.java`), and the scenes use macOS fonts (Georgia, Menlo, Apple Symbols). On Linux or Windows,
change the codec name in `VideoRenderer.java` and substitute fonts that exist on that system.

---

## Rendering a clip

Every clip is one scene class registered under a key in `LecVizApp.java`. The key is the output file name.

```bash
mvn -q exec:java -Dexec.mainClass=com.lecviz.LecVizApp -Dexec.args="list_21"
```

The key prefix decides the folder: `array_` goes to `output/arrays/`, `list_` to `output/lists/`, `pixel_` to
`output/pixel/`, `tree_` to `output/trees/`, `pq_` to `output/pq/`, `dict_` to `output/dictionary/`, `graph_` to
`output/graphs/`, `cx_` to `output/complexity/` and `intro_` to `output/intro/` (anything else goes to `output/`).
`mvn exec:java` does not compile: run `mvn -q compile` after every edit and before rendering. Rendering runs at about real time, so a 3-minute clip takes about 3 minutes.
Do not compile while a render is running.

Each clip is kept under about 95 MiB (GitHub rejects files over 100 MiB); a clip that would be bigger is split into
two clips rather than compressed.

---

## Making clips for a new deck

1. Put the slide PDF in `lectures/`.
2. Open the repo in Claude Code (a strong model such as Claude Sonnet 5.5 or better works best) and paste the prompt
   from [docs/new-slides-prompt.md](docs/new-slides-prompt.md) together with the PDF name. The file holds all the
   style and workflow rules settled on the arrays and lists decks, which parts of the code are needed, and the
   mistakes to avoid. Expect two to three feedback rounds per clip.
3. Or write a scene by hand: extend `PDSSortClipBase` (or `PDSListClipBase` for nodes, pointers, stacks and queues),
   register it in `LecVizApp.java`, and render it.

```java
public class MyScene extends PDSSortClipBase {
    @Override
    public void construct() {
        StrokeTextMob head = writeHeading("My topic");          // pen-stroke heading
        List<Spec> s = new ArrayList<>();
        s.add(ln(0, "First point of the slide"));
        s.add(ln(1, "A sub-point").kw("sub-point", Colors.TEAL)); // highlighter on a key word
        List<List<MObject>> text = writeSlide(s, -330);          // written line by line
        pause(1.2);
        swipeAway(text);                                         // swipes off to the right
        // ... pictures, code boxes (CodeBox), pointers, narration ...
        fadeOutAll(d(1.0), head);
    }
}
```

```java
SCENES.put("list_99", MyScene::new);   // in LecVizApp.java
```

### What the kits give a scene

- **`PDSSortClipBase`**: timing (`d`, `pause`), pen-stroke text (`stroke`, `strokeLeft`, `writeHeading`, `dropHeading`,
  `writeSlide`, `swipeAway`), plain labels, tables (`dropTable`), code boxes with a moving highlight (`CodeBox`),
  callouts, pointers, and bar charts (`Bars`).
- **The later kits** build on it: `PDSTreeClipBase` (general trees), `PDSDictClipBase` (binary search trees),
  `PDSAvlClipBase` (a dynamic tree `DT` that really inserts, deletes, rotates and splays), `PDSHashClipBase` (table
  cells with real open-addressing probes, chained buckets, histograms) and `PDSGraphClipBase` (a graph model `G`, its
  picture `GV` with lit nodes and traced edges, boxed tables `Grid`, queue/stack chips `Seq`).
- **`PDSListClipBase`**: list nodes with pointer cells (`LNode`), arrows that re-route while nodes move (`Link`),
  rows, boxed values (`Cell`), a step counter (`Steps`), stacks with a moving top (`VStack`), call stacks
  (`CallStack`), expression trees (`TNode`), chips, panels and one-line narration (`sayAt`).
- **Coordinates:** the origin is the centre of the 1920×1080 canvas, +X to the right and +Y down
  (from (-960, -540) at the top-left to (960, 540) at the bottom-right).

The basic engine calls are `play(animation...)`, `hold(seconds)`, `add(object)`, `remove(object)`, the animations
`FadeIn`, `FadeOut`, `Write`, `MoveTo`, `ScaleTo`, `ColorChange` (each accepts `.setEasing(Easing.EASE_IN_OUT)`), and
the object setters `setPosition`, `setOpacity`, `setScale`, `setFillColor`, `setStrokeColor`.

---

## Credits and licence

Slides: Prof. Rupesh Nasre (IIT Madras), course page
<https://cse.iitm.ac.in/~rupesh/teaching/pds/aug21/>. The animation style follows 3Blue1Brown's videos; this project
is a Java implementation of that look, not Manim itself. Videos and code: Karthik (CS23B018) and Tejaswi (CS23B023),
built with Claude Code.

Course project: CS5013 Programming with AI, IIT Madras.
