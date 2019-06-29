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

public class Starter {


    public static void main(String[] args) throws IOException {

        String inputPathStr = args[0];
        String outputPathStr = args[1];

        log("Read from:" + inputPathStr + "; write the sorted output to:" + outputPathStr);

        Path inputPath = Paths.get(inputPathStr);
        InputStream inputStream = Files.newInputStream(inputPath);

        Starter starter = new Starter();

        List<File> sortedFiles = starter.slicer.splitOnStreams(inputStream)
                .map(inStream -> {
                    try (BufferedReader reader = openWithBufferedReader(inStream)) {
                        Stream<Integer> lines = reader.lines().map(Integer::parseInt);
                        return starter.sorter.readSorted(lines);
                    } catch (IOException e) {
                        log("Failed to close an input stream", e);
                        throw new RuntimeException("Failed to sort input:" + inputPath);
                    }
                })
                .map(starter::storeOnFilesystem)
                .collect(Collectors.toList());

        List<BufferedReader> readers = sortedFiles.stream()
                .map(Starter::openWithBufferedReader)
                .collect(Collectors.toList());

        List<Stream<Integer>> sortedStreams = readers.stream()
                .map(reader -> reader.lines().map(Integer::parseInt))
                .collect(Collectors.toList());

        try (OutputStream out = Files.newOutputStream(Paths.get(outputPathStr))) {
            starter.merger.mergeStreams(sortedStreams, out);
        }

        for (BufferedReader reader : readers) {
            try {

                reader.close();

            } catch (IOException e) {
                throw new RuntimeException("Can not close shard readers", e);
            }
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

    private InputSlicer slicer = new SlicerStub();
    private StreamSorter<Integer> sorter = new SorterStub<>();
    private SortedMerger<Integer> merger = new MergerStub<>();

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

                if (writer.checkError()) throw new RuntimeException("Failed to write data on file system");
            }
            return shard.toFile();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp file", e);
        }
    }
}


interface InputSlicer {

    Stream<InputStream> splitOnStreams(InputStream input);
}

class SlicerStub implements InputSlicer {
    @Override
    public Stream<InputStream> splitOnStreams(InputStream input) {
        return Stream.empty();
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

interface SortedMerger<D extends Comparable<D>> {

    void mergeStreams(List<Stream<D>> streamsToMerge, OutputStream outputTarget);
}

class MergerStub<D extends Comparable<D>> implements SortedMerger<D> {
    @Override
    public void mergeStreams(List<Stream<D>> streamsToMerge, OutputStream outputTarget) {

    }
}
