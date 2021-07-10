package com.kpolak.reader;

import com.kpolak.hierarchy.RootNode;
import com.kpolak.model.Dicom;
import com.kpolak.model.Patient;
import com.kpolak.model.Series;
import com.kpolak.model.Study;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReader;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReaderSpi;
import org.dcm4che3.io.DicomInputStream;

import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class DicomReader {
    //    private final ImageReader imageReader = ImageIO.getImageReadersByFormatName("DICOM").next();
    private final ImageReader imageReader = new DicomImageReader(new DicomImageReaderSpi());
    private RootNode rootNode;

    public DicomReader() {
        rootNode = new RootNode();
    }

    public RootNode getRootNode() {
        return rootNode;
    }

    public BufferedImage readImageFromDicomInputStream(File file, int frame) throws IOException {
        try (DicomInputStream dis = new DicomInputStream(file)) {
            imageReader.setInput(dis);
            return imageReader.read(frame - 1, readParam());
        }
    }

    public void readDicomFilesInDirectory(String path) {
        File dir = new File(path);
        File[] files = dir.listFiles((dir1, name) -> name.endsWith(".dcm"));

        for (File dicomFile : files) {
            readDicomFromFile(dicomFile.getPath());
//            if (dicom.getInstanceNumber() == -1) {
//                System.out.println("File without instanceNumber found. File is skipped");
//            } else {
//                rootNode.add(dicom);
//            }
        }
        System.out.println("break");
    }

    public Dicom readDicomFromFile(String path) {
        Attributes attributes;
        try (DicomInputStream dis = new DicomInputStream(new File(path))) {
            dis.setIncludeBulkData(DicomInputStream.IncludeBulkData.NO);
            attributes = dis.readDataset();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        printTags(attributes);
        Patient patient = readPatientData(attributes);
        Study study = readStudyData(attributes);
        Series series = readSeriesData(attributes);
        Map<Integer, BufferedImage> frames;
        int numberOfFrames = attributes.getInt(Tag.NumberOfFrames, 1);
        int width = attributes.getInt(Tag.Rows, 0);
        int height = attributes.getInt(Tag.Columns, 0);
        int instanceNumber = attributes.getInt(Tag.InstanceNumber, -1);
        if (numberOfFrames == 1) {
            frames = readSingleFrameDicom(path, instanceNumber);
        } else {
            frames = readMultiframeDicomFrames(path, numberOfFrames);
        }

        Dicom dicom = Dicom.builder()
                .patient(patient)
                .study(study)
                .series(series)
                .frames(frames)
                .instanceNumber(instanceNumber)
                .width(width)
                .height(height)
                .build();

        if (dicom.getInstanceNumber() == -1) {
            System.out.println("File without instanceNumber found. File is skipped");
        } else {
            rootNode.add(dicom);
        }

        return rootNode.findDicom(patient, study, series);
    }

    private Map<Integer, BufferedImage> readMultiframeDicomFrames(String path, int numberOfFrames) {
        Map<Integer, BufferedImage> frames = new TreeMap<>();
        try (DicomInputStream dis = new DicomInputStream(new File(path))) {
            imageReader.setInput(dis);
            for (int i = 0; i < numberOfFrames; i++) {
                frames.put(i + 1, imageReader.read(i, readParam()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return frames;
    }

    private Map<Integer, BufferedImage> readSingleFrameDicom(String path, int instanceNumber) {
        Map<Integer, BufferedImage> frames = new TreeMap<>();
        try (DicomInputStream dis = new DicomInputStream(new File(path))) {
            imageReader.setInput(dis);
            frames.put(instanceNumber, imageReader.read(0, readParam()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return frames;
    }


    private Patient readPatientData(Attributes attributes) {
        return Patient.builder()
                .patientName(attributes.getString(Tag.PatientName))
                .patientID(attributes.getString(Tag.PatientID))
                .patientAge(attributes.getString(Tag.PatientAge))
                .patientSex(attributes.getString(Tag.PatientSex))
                .patientBirthDate(attributes.getString(Tag.PatientBirthDate))
                .patientComments(attributes.getString(Tag.PatientComments))
                .build();
    }

    private Study readStudyData(Attributes attributes) {
        return Study.builder()
                .studyID(attributes.getString(Tag.StudyID))
                .studyInstanceUID(attributes.getString(Tag.StudyInstanceUID))
                .studyDate(attributes.getString(Tag.StudyDate))
                .studyTime(attributes.getString(Tag.StudyTime))
                .build();
    }

    private Series readSeriesData(Attributes attributes) {
        return Series.builder()
                .seriesNumber(attributes.getString(Tag.SeriesNumber))
                .seriesInstanceUID(attributes.getString(Tag.SeriesInstanceUID))
                .seriesDate(attributes.getString(Tag.SeriesDate))
                .seriesTime(attributes.getString(Tag.SeriesTime))
                .seriesDescription(attributes.getString(Tag.SeriesDescription))
                .build();
    }

    private ImageReadParam readParam() {
        return imageReader.getDefaultReadParam();
    }

    public void printTags(Attributes attributes) {
        Arrays.stream(attributes.tags())
                .forEach(tag -> System.out.println("Tag " + getFieldNameByValue(tag) + "   " + tag + " =  " + attributes.getString(tag)));
    }

    private String getFieldNameByValue(int value) {
        Class<? extends Object> c = Tag.class;
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.getType() == int.class) {
                    if (field.getInt(null) == value) {
                        return field.getName();
                    }
                } else if (field.getType() == long.class) {
                    if (field.getLong(null) == (long) value) {
                        return field.getName();
                    }
                }

            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return "N/A";
    }
}
