package me.challenge.automationhero.reduce;

import me.challenge.automationhero.utils.Logging;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class SortedMergingReducer implements StreamReducer, Logging {

    private static final int DEFAULT_MEM_LIMIT = 1024 * 1024;

    private final Comparator<Integer> comparator;
    private final Comparator<StreamPicker> pickerComparator;
    private final int memoryLimit;

    public SortedMergingReducer(Comparator<Integer> comparator) {
        this(comparator, DEFAULT_MEM_LIMIT);
    }

    public SortedMergingReducer(Comparator<Integer> comparator, int memoryLimit) {
        this.comparator = comparator;
        this.pickerComparator = Comparator.comparing(StreamPicker::pick, this::noneGoesLastComparator);
        this.memoryLimit = memoryLimit;
    }

    @Override
    public void mergeStreams(List<InputStream> streamsToMerge, OutputStream outputTarget) {
        int perStreamMemory = memoryLimit / streamsToMerge.size();

        log("Allocating " + perStreamMemory + " per sorted data stream");

        List<StreamPicker> pickers = new ArrayList<>();
        streamsToMerge.forEach(stream -> pickers.add(new StreamPicker(stream, perStreamMemory)));

        log("StreamPicker created. Proceeding to merge");

        StreamPicker min = Collections.min(pickers, pickerComparator);
        while (min.pick().isPresent()) {
            min.read().ifPresent(val -> unsafePrintln(outputTarget, val));
            min = Collections.min(pickers, pickerComparator);
        }

        log("Merge finished. Flushing the data");

        try {
            outputTarget.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to flush output");
        }
    }

    int noneGoesLastComparator(Optional<Integer> o1, Optional<Integer> o2) {
        if (o1.isPresent() && o2.isPresent())
            return comparator.compare(o1.get(), o2.get());
        else if (o1.equals(o2))
            return 0;
        else return o1.isPresent() ? -1 : 1;
    }

    private static void unsafePrintln(OutputStream outputStream, int value) {
        try {
            outputStream.write(String.format("%d\n", value).getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write " + value + " into output", e);
        }
    }
}
