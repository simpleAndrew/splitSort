package me.challenge.automationhero;

import org.junit.Test;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class SimpleComparatorTest {

    @Test
    public void shouldSortAsc() {

        SimpleStreamSorter<Integer> integerSimpleComparator = new SimpleStreamSorter<>(Integer::compareTo);

        List<Integer> sortedIntegers = integerSimpleComparator.readSorted(Stream.of(10, 5, 0));

        assertEquals("numbers should be sorted in asc", Arrays.asList(0, 5, 10), sortedIntegers);
    }

}