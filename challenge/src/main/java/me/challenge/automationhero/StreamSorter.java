package me.challenge.automationhero;

import java.util.List;
import java.util.stream.Stream;

interface StreamSorter<D> {

    List<D> readSorted(Stream<D> input);

}
