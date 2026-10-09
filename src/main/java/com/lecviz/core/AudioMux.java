package com.lecviz.core;

import org.bytedeco.javacpp.Loader;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

/**
 * Adds the synthesized sound track to a finished silent video: the events logged by the scene are rendered by
 * {@link Sfx}, written as a WAV file and muxed into the mp4 with the ffmpeg bundled with JavaCV (the video stream
 * is copied, only the audio is encoded, as AAC).
 *
 * CS5013 Project: LecViz — Karthik (CS23B018) & Tejaswi (CS23B023)
 */
public final class AudioMux {

    private AudioMux() {}

    public static void mux(String videoPath, List<Sfx.Event> events, double seconds) throws Exception {
        float[][] pcm = Sfx.render(events, seconds);
        File wav = File.createTempFile("lecviz-sfx", ".wav");
        File out = File.createTempFile("lecviz-mux", ".mp4");
        try {
            writeWav(wav, pcm);
            String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
            ProcessBuilder pb = new ProcessBuilder(ffmpeg, "-v", "error", "-y",
                    "-i", videoPath, "-i", wav.getAbsolutePath(),
                    "-map", "0:v:0", "-map", "1:a:0", "-c:v", "copy", "-c:a", "aac", "-b:a", "192k",
                    "-shortest", "-movflags", "+faststart", "-f", "mp4", out.getAbsolutePath());
            String libDir = new File(ffmpeg).getParent();
            Map<String, String> env = pb.environment();
            for (String key : new String[]{"DYLD_LIBRARY_PATH", "LD_LIBRARY_PATH"}) {
                String old = env.get(key);
                env.put(key, old == null || old.isEmpty() ? libDir : libDir + File.pathSeparator + old);
            }
            pb.inheritIO();
            int code = pb.start().waitFor();
            if (code != 0) throw new IOException("ffmpeg failed to add the sound (exit " + code + ")");
            Files.move(out.toPath(), new File(videoPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.printf("[AudioMux] Added %d sound events (%.1f s) to %s%n", events.size(), seconds, videoPath);
        } finally {
            wav.delete();
            out.delete();
        }
    }

    private static void writeWav(File f, float[][] pcm) throws IOException {
        int n = pcm[0].length;
        ByteBuffer data = ByteBuffer.allocate(n * 4).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < n; i++) {
            data.putShort((short) Math.round(Math.max(-1, Math.min(1, pcm[0][i])) * 32767));
            data.putShort((short) Math.round(Math.max(-1, Math.min(1, pcm[1][i])) * 32767));
        }
        ByteBuffer hdr = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN);
        hdr.put("RIFF".getBytes()).putInt(36 + n * 4).put("WAVE".getBytes());
        hdr.put("fmt ".getBytes()).putInt(16).putShort((short) 1).putShort((short) 2)
                .putInt(Sfx.SR).putInt(Sfx.SR * 4).putShort((short) 4).putShort((short) 16);
        hdr.put("data".getBytes()).putInt(n * 4);
        try (OutputStream os = Files.newOutputStream(f.toPath())) {
            os.write(hdr.array());
            os.write(data.array());
        }
    }
}
