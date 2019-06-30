package me.challenge.automationhero;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class SimpleStreamSorter<D> implements StreamSorter<D> {

    private Comparator<D> comparator;

    SimpleStreamSorter(Comparator<D> comparator) {
        this.comparator = comparator;
    }

    @Override
    public List<D> readSorted(Stream<D> input) {
        return input.sorted(comparator).collect(Collectors.toList());
    }
}
