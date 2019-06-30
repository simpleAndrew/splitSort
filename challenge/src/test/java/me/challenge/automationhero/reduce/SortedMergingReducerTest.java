package me.challenge.automationhero.reduce;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class SortedMergingReducerTest {

    @Test
    public void comparatorShouldPushNoneToTop() {

        SortedMergingReducer sortedMergingReducer = new SortedMergingReducer(Integer::compareTo);
        Comparator<Optional<Integer>> noneGoesLastComparator = sortedMergingReducer::noneGoesLastComparator;

        assertTrue(noneGoesLastComparator.compare(Optional.empty(), Optional.of(1)) > 0);
    }

    @Test
    public void comparatorShouldHigherIntToTop() {

        SortedMergingReducer sortedMergingReducer = new SortedMergingReducer(Integer::compareTo);
        Comparator<Optional<Integer>> noneGoesLastComparator = sortedMergingReducer::noneGoesLastComparator;

        assertTrue(noneGoesLastComparator.compare(Optional.of(2), Optional.of(1)) > 0);
    }

    @Test
    public void shouldMergeListsKeepingThemSorted() throws IOException {
        SortedMergingReducer sortedMergingReducer = new SortedMergingReducer(Integer::compareTo);

        List<InputStream> input = Arrays.asList(
                createInputStream(0, 1, 2),
                createInputStream(-10, 1, 20)
        );

        String expected = IntStream.of(-10, 0, 1, 1, 2, 20)
                .mapToObj(i -> i + "\n")
                .collect(Collectors.joining());

        ByteOutputStream byteOutputStream = new ByteOutputStream();
        sortedMergingReducer.mergeStreams(input, byteOutputStream);

        assertEquals(expected, byteOutputStream.toString());
    }

    private static InputStream createInputStream(int... numbers) throws IOException {
        ByteOutputStream bytesOut = new ByteOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(bytesOut);
        for (int number : numbers) {
            dataOutputStream.writeInt(number);
        }
        dataOutputStream.close();
        return bytesOut.newInputStream();
    }
}