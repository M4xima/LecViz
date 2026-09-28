package com.lecviz.core;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.image.BufferedImage;

/**
 * Captures canvas frames and encodes them into an MP4 video file.
 * Uses JavaCV (FFmpeg bindings) for encoding.
 */
public class VideoRenderer {

    private final int width;
    private final int height;
    private final int fps;
    private final String outputPath;

    private FFmpegFrameRecorder recorder;
    private Java2DFrameConverter converter;
    private int frameCount = 0;

    public VideoRenderer(int width, int height, int fps, String outputPath) {
        this.width = width;
        this.height = height;
        this.fps = fps;
        this.outputPath = outputPath;
    }

    public void start() throws Exception {
        recorder = new FFmpegFrameRecorder(outputPath, width, height);
        // The bundled libopenh264 software encoder is baseline-profile only
        // (no B-frames) and visibly soft on flat colors/text edges. This
        // Mac has Apple's hardware H.264 encoder, which does real High
        // profile at much better quality — and it's faster too.
        recorder.setVideoCodecName("h264_videotoolbox");
        recorder.setFormat("mp4");
        recorder.setFrameRate(fps);
        recorder.setPixelFormat(org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_YUV420P);
        recorder.setVideoOption("profile", "high");
        recorder.setVideoBitrate(20_000_000); // ~20 Mbps — generous for crisp flat color + text at 1080p60
        recorder.start();

        converter = new Java2DFrameConverter();
        System.out.println("[VideoRenderer] Started recording to " + outputPath);
    }

    /**
     * Capture one frame from the JavaFX canvas.
     */
    public void captureFrame(Canvas canvas) throws Exception {
        WritableImage fxImage = new WritableImage(width, height);
        canvas.snapshot(null, fxImage);

        BufferedImage bImg = SwingFXUtils.fromFXImage(fxImage, null);
        // Ensure TYPE_3BYTE_BGR for FFmpeg
        BufferedImage bgrImg = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        bgrImg.getGraphics().drawImage(bImg, 0, 0, null);

        Frame frame = converter.convert(bgrImg);
        recorder.record(frame);
        frameCount++;
    }

    /**
     * Alternative: capture from a BufferedImage directly (for headless rendering).
     */
    public void captureFrame(BufferedImage bImg) throws Exception {
        BufferedImage bgrImg = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        bgrImg.getGraphics().drawImage(bImg, 0, 0, null);
        Frame frame = converter.convert(bgrImg);
        recorder.record(frame);
        frameCount++;
    }

    public void stop() throws Exception {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            System.out.printf("[VideoRenderer] Finished. %d frames written to %s%n",
                    frameCount, outputPath);
        }
    }

    public int getFrameCount() { return frameCount; }
    public int getFps() { return fps; }
    public String getOutputPath() { return outputPath; }
}
