# LecViz — 3Blue1Brown-Style Lecture Video Generator (Java)

A Java framework for generating animated educational videos from lecture content,
inspired by Grant Sanderson's [3Blue1Brown](https://www.3blue1brown.com/) / Manim.

Built for Prof. Rupesh Nasre's **Programming and Data Structures (PDS)** course
at IIT Madras (CS5013 project).

---

## Architecture

```
lecviz/
├── core/
│   ├── MObject.java        — Base class for all visual objects (like Manim's Mobject)
│   ├── Animation.java       — Base animation with easing and duration
│   ├── Scene.java           — Orchestrator: add objects, play animations, render frames
│   └── VideoRenderer.java   — Captures frames → MP4 via JavaCV/FFmpeg
├── mobjects/
│   ├── TextMob.java         — Text with font control, partial reveal
│   ├── CodeBlock.java       — Syntax-highlighted code (C/Java keywords)
│   ├── ArrayMob.java        — Array visualization with pointers, highlights
│   ├── LinkedListMob.java   — Linked list with nodes and arrows
│   ├── TreeMob.java         — Binary tree (BST, heap)
│   ├── StackMob.java        — Visual stack with push/pop
│   ├── RectMob.java         — Rectangle
│   ├── CircleMob.java       — Circle
│   └── ArrowMob.java        — Line with arrowhead
├── animations/
│   ├── FadeIn / FadeOut      — Opacity transitions
│   ├── Write.java            — Typewriter text reveal
│   ├── MoveTo.java           — Smooth position transition
│   ├── ScaleTo.java          — Scale transition
│   ├── DrawArrow.java        — Arrow grows from start to end
│   ├── RevealCode.java       — Line-by-line code reveal
│   └── ColorChange.java      — Smooth color transition
├── parser/
│   └── SlideParser.java      — Parse PDS HTML slides via Jsoup
├── scenes/
│   ├── PDSArrayScene.java    — Arrays + Bubble Sort animation
│   ├── PDSLinkedListScene.java — Linked lists + insertion
│   ├── PDSStackScene.java    — Stack push/pop operations
│   └── PDSTreeScene.java     — BST search animation
├── utils/
│   ├── Colors.java           — 3B1B dark-theme palette
│   ├── Easing.java           — Smooth/elastic/bounce easing functions
│   └── Vec2.java             — 2D vector math
└── LecVizApp.java            — Main entry point
```

## Prerequisites

| Tool          | Version  | Purpose                                |
|---------------|----------|----------------------------------------|
| **Java JDK**  | 17+      | Language runtime                       |
| **Maven**     | 3.8+     | Build & dependency management          |
| **FFmpeg**    | 5.0+     | Video encoding (pulled in via JavaCV)  |
| **JavaFX**    | 21       | 2D rendering engine (Maven dependency) |
| **JavaCV**    | 1.5.9    | Java bindings for FFmpeg               |
| **Apache POI**| 5.2.5    | Reading PPTX slide files               |
| **Jsoup**     | 1.17.2   | Parsing HTML lecture pages             |

All Java dependencies are managed by Maven — just have JDK 17+ and Maven installed.

### macOS (Homebrew)
```bash
brew install openjdk@17 maven
```

### Ubuntu/Debian
```bash
sudo apt install openjdk-17-jdk maven
```

### Windows
Install JDK 17 from https://adoptium.net and Maven from https://maven.apache.org.

## Build

```bash
cd lecviz
mvn clean compile
```

First build downloads ~500MB of dependencies (JavaCV includes native FFmpeg binaries).

## Usage

### Render a single scene
```bash
mvn exec:java -Dexec.mainClass="com.lecviz.LecVizApp" -Dexec.args="arrays"
```

### Render all PDS scenes
```bash
mvn exec:java -Dexec.mainClass="com.lecviz.LecVizApp"
```

### Available scenes
| Argument     | Topic                        |
|-------------|------------------------------|
| `arrays`    | Arrays & Bubble Sort         |
| `linkedlist`| Linked Lists & Pointers      |
| `stack`     | Stack Push/Pop Operations    |
| `tree`      | Binary Search Trees          |

Output goes to `output/<scene>.mp4`.

### Watching all the clips as one video

The finished clips are in `output/arrays/` (one per slide range). The full lecture is too big for GitHub
(about 800 MB, and GitHub rejects files over 100 MB), so it is not stored in the repo. After cloning or pulling,
build it locally in a few seconds, with no re-encoding and nothing extra to install:

```bash
./combine.sh arrays          # Windows: combine.bat arrays
```

This writes `output/combined/arrays_full.mp4` (clips joined in slide order). The same command works for any other
folder under `output/`, for example `./combine.sh lists` once that folder has clips. The `output/combined/` folder
is git-ignored.

## Creating New Scenes

1. Create a class extending `Scene`:

```java
package com.lecviz.scenes;

import com.lecviz.core.Scene;
import com.lecviz.animations.*;
import com.lecviz.mobjects.*;
import com.lecviz.utils.Colors;

public class MyScene extends Scene {
    @Override
    public void construct() {
        // Title
        TextMob title = new TextMob("My Topic")
            .setFontSize(52).setBold().setFillColor(Colors.BLUE);
        play(new FadeIn(title, 1.0));
        hold(2.0);

        // Code example
        CodeBlock code = new CodeBlock("int x = 42;", 24);
        code.setPosition(0, 100);
        play(new RevealCode(code, 2.0));
        hold(2.0);

        // Array visualization
        ArrayMob arr = new ArrayMob("1", "2", "3", "4", "5");
        arr.setPosition(0, 250);
        play(new FadeIn(arr, 1.0));
        arr.highlight(2);
        hold(1.0);
    }
}
```

2. Register it in `LecVizApp.java`:
```java
SCENES.put("mytopic", MyScene::new);
```

3. Run it:
```bash
mvn exec:java -Dexec.mainClass="com.lecviz.LecVizApp" -Dexec.args="mytopic"
```

## Key APIs

### Scene methods (like Manim)
- `play(animation)` — Play an animation, blocking until done
- `play(anim1, anim2, ...)` — Play animations simultaneously
- `hold(seconds)` — Hold current frame
- `add(object)` / `remove(object)` — Manage scene objects

### MObject (all visual objects)
- `.setPosition(x, y)` — Center-based coordinates
- `.setOpacity(0..1)` / `.setScale(s)` / `.setRotation(radians)`
- `.setFillColor(c)` / `.setStrokeColor(c)`
- `.shift(dx, dy)` — Relative move

### Animations
- `new FadeIn(obj, duration)`
- `new FadeOut(obj, duration)`
- `new Write(textMob, duration)` — Typewriter reveal
- `new RevealCode(codeBlock, duration)` — Line-by-line
- `new MoveTo(obj, x, y, duration)`
- `new ScaleTo(obj, scale, duration)`
- `new DrawArrow(arrow, duration)`
- `new ColorChange(obj, color, duration)`

All animations accept `.setEasing(Easing.EASE_IN_OUT)` etc.

### Coordinate System
- Origin (0,0) = center of 1920×1080 canvas
- X: left=-960, right=+960
- Y: up=-540, down=+540

## Parsing PDS Slides

The `SlideParser` can scrape Prof. Rupesh's course page:

```java
SlideParser parser = new SlideParser();
List<String> links = parser.fetchLectureLinks(
    "https://cse.iitm.ac.in/~rupesh/teaching/pds/aug21/"
);

// Or parse local HTML files
List<SlideContent> slides = parser.parseHtmlSlides(new File("lecture5.html"));
```

For PDF slides, add Apache PDFBox and extract text/images
before feeding them into your scene.

## License

Course project — CS5013 Programming with AI, IIT Madras.
