package com.lecviz.tools;

import org.bytedeco.javacpp.Loader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Joins every clip in output/<folder>/ into one video, in slide order, without re-encoding
 * (so quality is identical and it takes seconds): output/combined/<folder>_full.mp4.
 *
 * Uses the ffmpeg that ships with JavaCV, so nothing needs to be installed beyond the
 * project's own Maven dependencies.
 *
 *   mvn -q compile exec:java -Dexec.mainClass=com.lecviz.tools.CombineClips -Dexec.args="arrays"
 *   (or ./combine.sh arrays, combine.bat arrays)
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public class CombineClips {

    public static void main(String[] args) throws Exception {
        String folder = args.length > 0 ? args[0] : "arrays";
        File in = new File("output", folder);
        File[] found = in.listFiles((d, n) -> n.toLowerCase().endsWith(".mp4"));
        if (found == null || found.length == 0) {
            System.err.println("No .mp4 clips found in " + in.getPath());
            System.exit(1);
        }
        List<File> clips = new ArrayList<>(Arrays.asList(found));
        clips.sort((a, b) -> compareNatural(a.getName(), b.getName()));

        File outDir = new File("output", "combined");
        outDir.mkdirs();
        File out = new File(outDir, folder + "_full.mp4");

        Path list = Files.createTempFile("lecviz-concat", ".txt");
        StringBuilder sb = new StringBuilder();
        for (File c : clips) {
            sb.append("file '").append(c.getAbsolutePath().replace("'", "'\\''")).append("'\n");
            System.out.println("  + " + c.getName());
        }
        Files.write(list, sb.toString().getBytes(StandardCharsets.UTF_8));

        String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
        ProcessBuilder pb = new ProcessBuilder(ffmpeg, "-v", "error", "-y",
                "-f", "concat", "-safe", "0", "-i", list.toString(),
                "-c", "copy", "-movflags", "+faststart", out.getAbsolutePath());
        // the bundled ffmpeg finds its libraries next to itself
        String libDir = new File(ffmpeg).getParent();
        Map<String, String> env = pb.environment();
        for (String key : new String[]{"DYLD_LIBRARY_PATH", "LD_LIBRARY_PATH"}) {
            String old = env.get(key);
            env.put(key, old == null || old.isEmpty() ? libDir : libDir + File.pathSeparator + old);
        }
        pb.inheritIO();
        int code = pb.start().waitFor();
        Files.deleteIfExists(list);
        if (code != 0) {
            System.err.println("ffmpeg failed (exit " + code + ")");
            System.exit(code);
        }
        System.out.println("Wrote " + out.getPath() + "  (" + clips.size() + " clips, "
                + out.length() / (1024 * 1024) + " MB)");
    }

    /** array_2 < array_10, array_1to3 < array_3to4 < array_5 < array_10to12: compare the numbers inside the names. */
    static int compareNatural(String a, String b) {
        int i = 0, j = 0;
        while (i < a.length() && j < b.length()) {
            char x = a.charAt(i), y = b.charAt(j);
            if (Character.isDigit(x) && Character.isDigit(y)) {
                int si = i, sj = j;
                while (i < a.length() && Character.isDigit(a.charAt(i))) i++;
                while (j < b.length() && Character.isDigit(b.charAt(j))) j++;
                long na = Long.parseLong(a.substring(si, i)), nb = Long.parseLong(b.substring(sj, j));
                if (na != nb) return Long.compare(na, nb);
            } else {
                if (x != y) return Character.compare(x, y);
                i++;
                j++;
            }
        }
        return Integer.compare(a.length() - i, b.length() - j);
    }
}
