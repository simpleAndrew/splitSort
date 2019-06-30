package me.challenge.automationhero;

import me.challenge.automationhero.map.SimpleStreamSorter;
import me.challenge.automationhero.map.SizeBasedInputSlicer;
import me.challenge.automationhero.map.StreamSorter;
import me.challenge.automationhero.reduce.SortedMergingReducer;
import me.challenge.automationhero.reduce.StreamReducer;
import me.challenge.automationhero.utils.Logging;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SplitSort implements Logging {

    private final int sliceSizeLimitBytes = 5 * 1024 * 1024;

    private StreamSorter<Integer> sorter = new SimpleStreamSorter<>(Integer::compareTo);
    private StreamReducer merger = new SortedMergingReducer(Integer::compareTo);
    private Path tempDirectory = initTempDirectory();

    public void sortFile(Path inputPath, Path outputPath) throws IOException {
        InputStream inputStream = Files.newInputStream(inputPath);
        SizeBasedInputSlicer streamSlicer = new SizeBasedInputSlicer(inputStream, sliceSizeLimitBytes);

        List<File> sortedFiles = StreamSupport.stream(streamSlicer.spliterator(), false)
                .map(sorter::readSorted)
                .map(this::storeOnFilesystem)
                .collect(Collectors.toList());

        log("Prepare input streams of sorted files");

        List<InputStream> sortedStreams = sortedFiles.stream()
                .map(SplitSort::openFileInputStream)
                .collect(Collectors.toList());

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


    private Path initTempDirectory() {
        try {
            Path tempDirectory = Files.createTempDirectory("map-reduce-sorting");
            tempDirectory.toFile().deleteOnExit();
            return tempDirectory;
        } catch (IOException e) {
            log("Can't create temp directory. Shutting down");
            System.exit(1);
            return null;
        }
    }

    private File storeOnFilesystem(List<Integer> results) {
        try {
            Path shard = Files.createTempFile(tempDirectory, "map-reduce", "shard");
            log("Saving slice of data in temp file: " + shard.toString());
            try (DataOutputStream dataOut =
                         new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(shard)))
            ) {
                results.stream().forEachOrdered(i -> writeIntUnsafe(dataOut, i));
                log("Saving finished to file: " + shard.toString());
                results.clear();
            }
            return shard.toFile();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp file", e);
        }
    }

    private void writeIntUnsafe(DataOutputStream dataOutputStream, int value) {
        try {
            dataOutputStream.writeInt(value);
        } catch (IOException e) {
            throw new RuntimeException("Can not write " + value + " to data stream", e);
        }
    }
}
