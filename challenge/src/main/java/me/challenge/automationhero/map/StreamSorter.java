package me.challenge.automationhero.map;

import java.util.List;
import java.util.stream.Stream;

public interface StreamSorter<D> {

    List<D> readSorted(Stream<D> input);

}
