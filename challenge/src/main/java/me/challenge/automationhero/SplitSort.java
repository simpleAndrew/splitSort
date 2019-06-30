package me.challenge.automationhero;

import me.challenge.automationhero.map.SizeBasedInputSlicer;
import me.challenge.automationhero.reduce.SortedMergingReducer;
import me.challenge.automationhero.reduce.StreamReducer;
import me.challenge.automationhero.utils.Logging;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class SplitSort implements Logging {

    private static final Comparator<Integer> COMPARATOR = Integer::compareTo;

    private final Path tempDirectory;
    private final StreamReducer merger;

    private final int readBufferSize;

    SplitSort(Path tempDirectory, int memoryLimit) {
        this.tempDirectory = tempDirectory;
        readBufferSize = memoryLimit / 20;
        merger = new SortedMergingReducer(COMPARATOR, memoryLimit / 100);
    }

    void sortFile(Path inputPath, Path outputPath) throws IOException {

        log("Copying unsorted ints from " + inputPath.toString() + " to " + outputPath + " sorted");

        InputStream inputStream = Files.newInputStream(inputPath);
        SizeBasedInputSlicer streamSlicer = new SizeBasedInputSlicer(inputStream, readBufferSize);

        List<InputStream> sortedStreams = new LinkedList<>();
        for (List<Integer> numbers : streamSlicer) {
            numbers.sort(COMPARATOR);
            File file = writeToFile(numbers);
            sortedStreams.add(openFileInputStream(file));
        }

        log("Start merging sorted inputs");

        try (OutputStream out = Files.newOutputStream(outputPath)) {
            merger.mergeStreams(sortedStreams, out);
        }

        log("Merge complete");

        for (InputStream input : sortedStreams) {
            try {
                input.close();
            } catch (IOException e) {
                throw new RuntimeException("Can not close shard readers", e);
            }
        }

        log("Input stream closed. Sorting finished");
    }


    private static FileInputStream openFileInputStream(File fileToRead) {
        try {
            return new FileInputStream(fileToRead);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }



    private File writeToFile(List<Integer> results) {
        try {
            Path shard = Files.createTempFile(tempDirectory, "map-reduce", "shard");
            log("Saving slice of data in temp file: " + shard.toString());
            try (DataOutputStream dataOut =
                         new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(shard)))
            ) {
                for (Integer i : results) {
                    dataOut.writeInt(i);
                }
                log("Saving finished to file: " + shard.toString());
                results.clear();
            }
            return shard.toFile();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp file", e);
        }
    }
}
