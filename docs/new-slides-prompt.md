# LecViz: prompt and code guide for making clips from new slides

This file has three parts:

1. **The prompt**: paste it (with the slide PDF) into Claude Code, opened in the repo folder.
2. **Which parts of the code are needed** (and which are not).
3. **How to run, check and combine** the clips yourself.

The prompt below holds every style and workflow rule that was settled while making the 19 array clips
(Prof. Nasre's `3-arrays.pdf`, 44 min 53 s in total).

---

## Part 1: The prompt (copy everything between the lines)

---

You are working in the **LecViz** repository (pure Java; JavaFX Canvas + JavaCV/FFmpeg; Maven). It makes
3Blue1Brown-style animated lecture videos from Prof. Rupesh Nasre's PDS slides (CS5013, IIT Madras).
The slides are PDFs in `lectures/`. Read them with a PDF reader. Your task: **turn the slide PDF I name into
a set of short, polished clips, one per sub-topic, and cover every slide.**

### 1. Workflow (follow in this order)

1. **Read every slide of the PDF first**, text and images. Use the *printed* slide numbers. Note any numbering
   gaps (the arrays deck has no slide 26), and any unnumbered closing slide.
2. **Propose a plan before building anything.** Divide the deck into clips by sub-topic (one algorithm or one
   idea per clip; a classwork block gets its own clip). For each clip give: slide range, file name, how the
   clip opens, what it shows, and how each part is animated. Do not just animate what is on the slide: add
   an interesting visual idea that explains it. Wait for my OK on the split.
3. **Build one clip at a time.** One scene class + one mp4 per clip. The scene key is the output file name,
   `<topic>_<slides>`: `array_5`, `array_16to17`, `array_10to12`. Register each in the `SCENES` map in
   `LecVizApp.java`. Reuse the shared kit `PDSSortClipBase` (see part 2); do not copy its helpers.
4. **Render, check, fix, then deliver.** After each render, extract frames into contact sheets and look at
   them for overlaps, clipped or off-screen text, shapes touching labels, flicker, and leftovers from earlier
   steps. Fix problems and re-render. Only then give me the video with its duration and size.
5. **Cover all the slides.** Every printed slide must appear in some clip, including the title slide and the
   summary slide. If a slide has no clip yet, say so and make one.
6. **Join the clips** with `./combine.sh <topic>` into `output/combined/<topic>_full.mp4`.

### 2. Content rules

- **All the content of each slide must appear**: every line, example, table, code listing and complexity
  bound, using the slide's own wording. Add visuals on top of it; never drop slide content.
- **Slide text comes one line at a time** in the pen-stroke style (section 3), then the whole text swipes
  away to the right and fades. Visuals start after that.
- **Code slides**: type the code in line by line, then walk through it with a moving highlight bar and
  live values (index pointers like `i`, `j`, `key`, counters, array cells changing). End with the slide's
  complexity.
- **If the slide's code and its table or example disagree**, do not hide it: show the correction on screen
  (strike through the old line, write the right one) before running the code.
- **Questions and classwork**: the questions appear one by one, slowly, then are answered one by one with an
  animated explanation. Where the slide hints at a better solution, add a short "Can we do better?" and
  "Think of it" beat after showing the cost of the naive one.
- **Algorithm walkthroughs** (searching, eliminating, partitioning) use the layout I called "insanely good":
  the data grid slides to the right and a panel on the left shows the rules one at a time (each rule in its
  own color, each previewed on the grid), then a worked example in which every step states which rule fired
  (e.g. "44 < 45 → key < e → eliminate column 4"). A selector bar slides to the rule that fired, a colored band
  wipes over what is eliminated, eliminated cells dim, an arrow trail records the path, early steps are slow
  and later steps faster, and the match ends with a ripple. **Highlight only the element actually being used
  as the key.** Do not highlight other cells.
- **`low`/`mid`/`high` and similar pointers are indexes, not values.** Show the index numbers dropping from
  their cells to a "low = …" tag.
- **Tables** (e.g. sorting algorithms at a glance): the heading slides in from above and each row drops in
  from above one by one, slowly.
- **Summary / overview slides**: turn the list into a diagram of cards with small drawn icons.
- **Use random-looking example numbers** (12, 7, 31, 4 …), not 1, 2, 3 …, unless the slide gives its own.
  To show that values are arbitrary, fade in the empty boxes first, flicker each cell through a few fast
  random values for about a second, then lock to the real value.
- **Closing clip**: the last clip ends with a short card, "This video was made by Karthik & Tejaswi"
  (replace with the right names for a new team). Do not include the unnumbered closing course-title slide.

### 3. Text style

- **Pen-stroke reveal** (the 3Blue1Brown "Day 1" handwriting look): each glyph's outline is traced first,
  then the fill comes in from left to right. Use `StrokeTextMob` + the `Write` animation for **titles,
  headings and full-sentence captions**.
- **Plain `TextMob` (with a fade) for short labels and numbers**: row tags, cell values, index digits,
  counters, single words. Do **not** use pen-stroke on those.
- `StrokeTextMob` cannot change its text after creation. If text must change, make a new one.
- **Formulas** use `LaTeXMob` (not pen-stroke). Make hard-to-read formulas **bold**. Greek glyphs are not on
  the classpath: avoid `\Omega` and similar; `\neq`, `\geq`, `\lfloor` work.
- Captions are short ("Row-major storage"), never overlap the heading, and sit in a consistent place.
  Georgia for text, Menlo for code (macOS fonts).
- Typical sizes: headings 54 pt (the kit default), captions 26–34 pt, code 24–28 pt, labels 20–34 pt.

### 4. Look and pacing

- Dark near-black background (`#08080D` with a soft radial gradient `#12141F` centre), set in `Scene.java`.
  Flat translucent colored blocks with an outline of the same color, not heavy 2D boxes.
- Colors stay consistent across a clip and across clips: a color, once set, persists (for example the array's
  translucent green stays to the end). The array fades in plainly first and only then takes its color with a
  smooth fade (no pixelated or noisy effects).
- Use the camera as an effect: slides move like a frame sliding (screen moves right/down with a slight zoom
  while the old heading slides off the other way). Zoom is welcome; do not overuse it.
- **Pacing is relaxed.** The kit's `PACE = 1.15` multiplier slows everything slightly. Early steps of a
  traversal are slow and later steps faster. When I say "too fast", slow it further.
- Endings: at the end of a section fade **everything out at once**, smoothly. No stray title popping back in.
- Always keep each mp4 comfortably **under about 95 MiB** (about 16–24 MB per minute at this quality).
  If a clip would be bigger, **split it into two clips; never re-encode it smaller.** I want full quality.

### 5. Technical rules

- Java 17+, JavaFX 21, JavaCV 1.5.9, Maven. 1920×1080 at 60 fps, coordinates centered on the screen
  (+X right, +Y down, from (-960,-540) to (960,540)).
- Render one clip: `mvn -q exec:java -Dexec.mainClass=com.lecviz.LecVizApp -Dexec.args="<scene-key>"`.
  Output goes to `output/<scene-key>.mp4`; then move it into `output/<topic>/` (e.g. `output/lists/`).
  If you hit `NoClassDefFoundError`, run `mvn clean compile` first. **Never compile while a render is
  running.**
- Encoding uses `h264_videotoolbox` (Apple hardware encoder, High profile, ~20 Mbps) in
  `core/VideoRenderer.java`. That is **Mac-only**; on Linux/Windows change the codec name there.
- Fonts such as Georgia, Menlo and Apple Symbols are macOS fonts; on other systems substitute available
  ones and check glyphs (for example the knight symbol) render.
- There may be no system `ffmpeg`. `tools/CombineClips.java` shows how to get the ffmpeg bundled with JavaCV
  (`Loader.load(org.bytedeco.ffmpeg.ffmpeg.class)`), which you can also use for frame extraction.
- Do not commit anything unless I ask. When I do, commit with `git add` on the relevant files only; mp4
  clips are tracked under `output/<topic>/`, but `output/combined/` is git-ignored (the full video is far
  over GitHub's 100 MiB limit).

### 6. How I give you feedback

I usually answer with small corrections ("slower", "too fast at the start", "this overlaps the heading",
"highlight only the key"). Apply them to the clip, re-render, re-check and send it again. Keep the rest of
the clip unchanged.

**Now:** read `lectures/<PDF NAME>.pdf` and propose the plan (section 1, step 2).

---

## Part 2: Which parts of the code are needed

Everything is in this repository. For a new deck you need the engine, the shared kit, one registry file and
the helper scripts. You can copy the whole repo, or only the rows marked **needed**.

| Path | Needed? | What it is |
|---|---|---|
| `pom.xml` | **needed** | Maven build (Java 17, JavaFX 21, JavaCV 1.5.9, JLaTeXMath) |
| `src/main/java/com/lecviz/core/` | **needed** | `Scene` (play/hold/add/remove, camera, 60 fps loop), `Animation`, `MObject`, `VideoRenderer` (MP4 encoding) |
| `src/main/java/com/lecviz/utils/` | **needed** | `Colors`, `Easing`, `Vec2`, `Vec3` |
| `src/main/java/com/lecviz/mobjects/` | **needed** | Drawable objects: `StrokeTextMob` (pen-stroke text), `TextMob`, `RectMob`, `CircleMob`, `ArrowMob`, `LaTeXMob`; also `ArrayMob`, `LinkedListMob`, `TreeMob`, `StackMob`, `CodeBlock`, `CubeGrid3D` (handy for lists, trees and stacks) |
| `src/main/java/com/lecviz/animations/` | **needed** | `Write`, `FadeIn`, `FadeOut`, `MoveTo`, `ScaleTo`, `ColorChange`, `DrawArrow`, `Transform`, `RevealCode`, and the 3D cube ones |
| `src/main/java/com/lecviz/scenes/PDSSortClipBase.java` | **needed (the key file)** | The shared style kit all current clips extend. See below |
| `src/main/java/com/lecviz/LecVizApp.java` | **needed** | The scene registry (`SCENES` map) and render entry point; add each new clip here |
| `combine.sh`, `combine.bat`, `src/main/java/com/lecviz/tools/CombineClips.java` | **needed** | Joins all clips in `output/<topic>/` into one video, no re-encoding |
| `lectures/*.pdf` | **needed** | The slides (`4-lists.pdf`, `5-trees.pdf`, … are already there) |
| `docs/new-slides-prompt.md` | this file | The prompt |
| Reference scenes in `src/main/java/com/lecviz/scenes/` | **good examples to read** | `PDSBubbleSortScene` / `PDSInsertionSortScene` (code walkthrough), `PDSSortedMatrixSearchScene` (rule panel + worked example), `PDSClassworkScene` (questions revealed one by one, then answered), `PDSSortingScene` (heading drop-in + table, self-contained), `PDSWrapUpScene` (summary card diagram, end card), `PDSArrayIntroScene` (title card, memory-cell pictures) |
| Older scenes: `PDSArrayScene`, `PDSArraySceneV2`, `PDSArraySceneV3`, `PDSIntroScene`, `PDSComplexityScene`, `PDSLinkedListScene`, `PDSStackScene`, `PDSTreeScene`, `PDSDictionaryScene`, `PDSPriorityQueueScene`, `PDSGraphScene` | not needed | Early prototypes in the old long-video style. Not part of the current clip workflow |
| `parser/`, `generator/` (PPTX auto-builder) | not needed | An early attempt at generating a video straight from slides. Its output was not the quality wanted, so the clips are hand-built scenes |
| `output/`, `target/` | not needed | Finished videos and build output |

**What `PDSSortClipBase` gives a new scene** (so the AI should call these, not rewrite them):

- Timing: `d(sec)` / `pause(sec)` (both apply the 1.15 pace), `playAll(list)`, `fadeOutAll(dur, objects…)`.
- Text: `stroke(...)` / `strokeLeft(...)` (pen-stroke), `label(...)` (plain), `writeHeading`, `dropHeading`
  (slides in from above), `writeSlide(List<Spec>, startY)` with `ln(level, text)` (levels: 0 bullet,
  1 sub-bullet, 2 sub-continuation, 3 bullet continuation) plus `.kw(word, color)` highlighter sweep,
  `.tail(text)` and `.gap(g)`, and `swipeAway(groups)`.
- Tables: `dropTable(header, rows, colWidths, tints, headY)`.
- Code: `CodeBox` (`typeIn`, `moveHl`, `setLine`, `shift`, `strike`, `rewrite`) with syntax coloring.
- Annotations: `callout(...)`, `pointer(name, x, yTip, pointsDown, color)`.
- Bars/cells: `Bars` / `SBar` (move, swap, lift, place, paint).
- Animations: `SwipeOut`, `DropIn`, `FadeInAt`, `TypeAt`, `GrowRight`, `ArcMove`, `ResizeX`,
  `LineMob` + `DrawLine`.

---

## Part 3: Running it yourself

Prerequisites: JDK 17 or newer and Maven. On macOS, run from the repo folder.

```bash
# render one clip (key = the name registered in LecVizApp.java)
mvn -q exec:java -Dexec.mainClass=com.lecviz.LecVizApp -Dexec.args="array_16to17"

# put finished clips for a topic in output/<topic>/ , then join them in order
./combine.sh arrays        # Windows: combine.bat arrays
# result: output/combined/arrays_full.mp4
```

Rendering runs at about real time (a 3-minute clip takes about 3 minutes). A set the size of the arrays deck
took roughly 10 hours of working time including feedback rounds; a first draft of a clip is usually quick and
polish takes 2–3 rounds.
