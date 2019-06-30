package me.challenge.automationhero;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Starter {


    public static void main(String[] args) throws IOException {

        String inputPathStr = args[0];
        String outputPathStr = args[1];

        log("Read from:" + inputPathStr + "; write the sorted output to:" + outputPathStr);

        Path inputPath = Paths.get(inputPathStr);
        InputStream inputStream = Files.newInputStream(inputPath);

        Starter starter = new Starter();

        List<File> sortedFiles = StreamSupport.stream(new SizeBasedInputSlicer(inputStream, 12).spliterator(), false)
                .map(starter.sorter::readSorted)
                .map(starter::storeOnFilesystem)
                .collect(Collectors.toList());

        List<InputStream> sortedStreams = sortedFiles.stream()
                .map(Starter::openFileInputStream)
                .collect(Collectors.toList());

        try (OutputStream out = Files.newOutputStream(Paths.get(outputPathStr))) {
            starter.merger.mergeStreams(sortedStreams, out);
        }

        for (InputStream input : sortedStreams) {
            try {
                input.close();
            } catch (IOException e) {
                throw new RuntimeException("Can not close shard readers", e);
            }
        }
    }

    private static FileInputStream openFileInputStream(File fileToRead) {
        try {
            return new FileInputStream(fileToRead);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static BufferedReader openWithBufferedReader(File fileToRead) {
        try {
            return new BufferedReader(new FileReader(fileToRead));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static BufferedReader openWithBufferedReader(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream));
    }

    private static void log(String message, Throwable e) {
        String finalMsg = message + "; exception:" + Arrays.toString(e.getStackTrace());
        log(finalMsg);
    }

    private static void log(String message) {
        System.out.println(message);
    }

    private StreamSorter<Integer> sorter = new SorterStub<>();
    private SortedMerger merger = new MergerStub();

    private Path tempDirectory = initTempDirectory();

    private Path initTempDirectory() {
        try {
            return Files.createTempDirectory("map-reduce-sorting");
        } catch (IOException e) {
            log("Can't create temp directory. Shutting down");
            System.exit(1);
            return null;
        }
    }

    private File storeOnFilesystem(List<Integer> results) {
        try {
            Path shard = Files.createTempFile(tempDirectory, "map-reduce", "shard");
            try (PrintWriter writer = new PrintWriter(new BufferedOutputStream(Files.newOutputStream(shard)))) {
                results.stream().forEachOrdered(writer::println);
                writer.flush();
                if (writer.checkError()) throw new RuntimeException("Failed to write data on file system");
            }
            return shard.toFile();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp file", e);
        }
    }
}


interface StreamSorter<D extends Comparable<D>> {

    List<D> readSorted(Stream<D> input);

}

class SorterStub<D extends Comparable<D>> implements StreamSorter<D> {
    @Override
    public List<D> readSorted(Stream<D> input) {
        return new LinkedList<>();
    }
}

interface SortedMerger {

    void mergeStreams(List<InputStream> streamsToMerge, OutputStream outputTarget);
}

class MergerStub implements SortedMerger {
    @Override
    public void mergeStreams(List<InputStream> streamsToMerge, OutputStream outputTarget) {
    }
}
