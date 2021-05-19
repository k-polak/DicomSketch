package com.kpolak.reader;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.util.SafeClose;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;


public class Dicom2Jpeg {

    public interface ReadImage {
        BufferedImage apply(File src) throws IOException;
    }

    private ReadImage readImage;
    private String suffix;
    private int frame = 1;
    private int windowIndex;
    private int voiLUTIndex;
    private boolean preferWindow = true;
    private float windowCenter;
    private float windowWidth;
    private boolean autoWindowing = true;
    private Attributes prState;
    private final ImageReader imageReader = ImageIO.getImageReadersByFormatName("DICOM").next();
    private ImageWriter imageWriter;
    private ImageWriteParam imageWriteParam;
    private int overlayActivationMask = 0xffff;
    private int overlayGrayscaleValue = 0xffff;
    private int overlayRGBValue = 0xffffff;
    //private ICCProfile.Option iccProfile = ICCProfile.Option.none;

    public void initImageWriter(String formatName, String suffix,
                                String clazz, String compressionType, Number quality) {
        this.suffix = suffix != null ? suffix : formatName.toLowerCase();
        Iterator<ImageWriter> imageWriters =
                ImageIO.getImageWritersByFormatName(formatName);
        if (!imageWriters.hasNext()) {
            throw new IllegalArgumentException("removed rb 54");
        }
        Iterable<ImageWriter> iterable = () -> imageWriters;
        imageWriter = StreamSupport.stream(iterable.spliterator(), false)
                .filter(matchClassName(clazz))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("removed rb"));
        imageWriteParam = imageWriter.getDefaultWriteParam();
        if (compressionType != null || quality != null) {
            imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            if (compressionType != null)
                imageWriteParam.setCompressionType(compressionType);
            if (quality != null)
                imageWriteParam.setCompressionQuality(quality.floatValue());
        }
    }


    private static Predicate<Object> matchClassName(String clazz) {
        Predicate<String> predicate = clazz.endsWith("*")
                ? startsWith(clazz.substring(0, clazz.length() - 1))
                : clazz::equals;
        return w -> predicate.test(w.getClass().getName());
    }

    private static Predicate<String> startsWith(String prefix) {
        return s -> s.startsWith(prefix);
    }

    public final void setFrame(int frame) {
        this.frame = frame;
    }

    public final void setWindowCenter(float windowCenter) {
        this.windowCenter = windowCenter;
    }

    public final void setWindowWidth(float windowWidth) {
        this.windowWidth = windowWidth;
    }

    public final void setWindowIndex(int windowIndex) {
        this.windowIndex = windowIndex;
    }

    public final void setVOILUTIndex(int voiLUTIndex) {
        this.voiLUTIndex = voiLUTIndex;
    }

    public final void setPreferWindow(boolean preferWindow) {
        this.preferWindow = preferWindow;
    }

    public final void setAutoWindowing(boolean autoWindowing) {
        this.autoWindowing = autoWindowing;
    }

    public final void setPresentationState(Attributes prState) {
        this.prState = prState;
    }

    public void setOverlayActivationMask(int overlayActivationMask) {
        this.overlayActivationMask = overlayActivationMask;
    }

    public void setOverlayGrayscaleValue(int overlayGrayscaleValue) {
        this.overlayGrayscaleValue = overlayGrayscaleValue;
    }

    public void setOverlayRGBValue(int overlayRGBValue) {
        this.overlayRGBValue = overlayRGBValue;
    }

    public final void setReadImage(ReadImage readImage) {
        this.readImage = readImage;
    }

    private BufferedImage readImageFromImageInputStream(File file) throws IOException {
        try (ImageInputStream iis = new FileImageInputStream(file)) {
            imageReader.setInput(iis);
            return imageReader.read(frame - 1, readParam());
        }
    }

    public BufferedImage readImageFromDicomInputStream(File file) throws IOException {
        try (DicomInputStream dis = new DicomInputStream(file)) {
            imageReader.setInput(dis);
            return imageReader.read(frame - 1, readParam());
        }
    }

    public BufferedImage readImageFromDicomInputStream(File file, int frame) throws IOException {
        try (DicomInputStream dis = new DicomInputStream(file)) {
            imageReader.setInput(dis);
            return imageReader.read(frame -1, readParam());
        }
    }

    private ImageReadParam readParam() {
        DicomImageReadParam param =
                (DicomImageReadParam) imageReader.getDefaultReadParam();
        param.setWindowCenter(windowCenter);
        param.setWindowWidth(windowWidth);
        param.setAutoWindowing(autoWindowing);
        param.setWindowIndex(windowIndex);
        param.setVOILUTIndex(voiLUTIndex);
        param.setPreferWindow(preferWindow);
        param.setPresentationState(prState);
        param.setOverlayActivationMask(overlayActivationMask);
        param.setOverlayGrayscaleValue(overlayGrayscaleValue);
        //param.setOverlayRGBValue(overlayRGBValue);
        return param;
    }

    private void writeImage(File dest, BufferedImage bi) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(dest, "rw")) {
            raf.setLength(0);
            imageWriter.setOutput(new FileImageOutputStream(raf));
            imageWriter.write(null, new IIOImage(bi, null, null), imageWriteParam);
        }
    }


    private String suffix(File src) {
        return src.getName() + '.' + suffix;
    }

    private static Attributes loadDicomObject(File f) throws IOException {
        if (f == null)
            return null;
        DicomInputStream dis = new DicomInputStream(f);
        try {
            return dis.readDataset(-1, -1);
        } finally {
            SafeClose.close(dis);
        }
    }
}
