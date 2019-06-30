package me.challenge.automationhero.map;

import me.challenge.automationhero.utils.Logging;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleStreamSorter<D> implements StreamSorter<D>, Logging {

    private Comparator<D> comparator;

    public SimpleStreamSorter(Comparator<D> comparator) {
        this.comparator = comparator;
    }

    @Override
    public List<D> readSorted(Stream<D> input) {

        log("Starting sorting of a stream");
        List<D> collect = input.sorted(comparator).collect(Collectors.toList());
//        input.close();
        log("Stream sorted");
        return collect;
    }
}
