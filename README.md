# LecViz: 3Blue1Brown-style lecture videos, in pure Java

LecViz turns lecture slides into short animated videos in the style of
[3Blue1Brown](https://www.3blue1brown.com/) (Grant Sanderson's Manim). It is written in Java
(JavaFX canvas + JavaCV/FFmpeg) and was built for Prof. Rupesh Nasre's **Programming and Data Structures (PDS)**
course at IIT Madras (CS5013 project, by Karthik CS23B018 and Tejaswi CS23B023).

So far it has produced **two complete decks**, one clip per sub-topic, every slide covered:

| Deck | Slides | Clips | Length | Folder |
|---|---|---|---|---|
| Arrays (`lectures/3-arrays.pdf`) | 1–31 | 19 | 44 min 53 s | `output/arrays/` |
| Lists, stacks and queues (`lectures/4-lists.pdf`) | 1–48 | 26 | about 54 min | `output/lists/` |

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
./combine.sh lists
```

This writes `output/combined/arrays_full.mp4` and `output/combined/lists_full.mp4`. These files are about 0.8 GB and
1.2 GB, which is over GitHub's 100 MB file limit, so `output/combined/` is git-ignored and is not stored in the
repository. The full arrays video is also attached to the GitHub release `arrays-full-v1` (visible to people who have
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

Slides 9, 11, 12 and 28 of the lists deck have no clip (a file-name-only slide, a missing number, a quiz slide, and a
file-name-only slide).

---

## Repository layout

```
lecviz/
├── lectures/                 The course slides (PDF)
├── output/
│   ├── arrays/  lists/       Finished clips (tracked in git)
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
    │   └── PDS*Scene         One scene class per clip
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

Keys starting with `array_` are written to `output/arrays/` and keys starting with `list_` to `output/lists/`
(anything else goes to `output/`). Rendering runs at about real time, so a 3-minute clip takes about 3 minutes.
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
