package me.challenge.automationhero.map;

import me.challenge.automationhero.map.SizeBasedInputSlicer;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.reducing;
import static org.junit.Assert.*;

public class SizeBasedInputSlicerTest {

    @Test
    public void shouldHaveNextIfHasValuesIfOriginalStreamNotEmpty() {
        SizeBasedInputSlicer slicer = buildSlicer();
        assertTrue("Should return true when values are present", slicer.hasNext());
    }

    @Test
    public void shouldHaveNextWhenUnderlyingInputNotCompletelyRead() {
        SizeBasedInputSlicer slicer = buildSlicer();
        slicer.next();
        assertTrue("Should return true when values are still present", slicer.hasNext());
    }

    @Test
    public void shouldNotHaveNextWhenUnderlyingInputIsEmpty() {
        SizeBasedInputSlicer slicer = buildSlicer(Stream.empty(), 1L);
        assertFalse("Should return false when source empty", slicer.hasNext());
    }

    @Test
    public void shouldReturnListCloseToGivenLimit() {
        SizeBasedInputSlicer slicer = buildSlicer(0, 1, 1L);

        List<Integer> next = slicer.next().collect(Collectors.toList());

        assertEquals("Should return have only 1 element", Collections.singletonList(0), next);
    }

    @Test
    public void shouldSplitTheInputBasedOnSizeProvided() {
        SizeBasedInputSlicer slicer = buildSlicer(0, 2, 1L);

        int counter = 0;
        while (slicer.hasNext()) {
            slicer.next();
            counter++;
        }

        assertEquals("Should split input on 2 streams", 2, counter);
    }

    @Test(expected = NumberFormatException.class)
    public void shouldThrowNumberFormatEWhenDataNotInteger() {
        SizeBasedInputSlicer slicer = buildSlicer(Stream.of("Not_integer"), 1L);

        slicer.next();
    }

    private SizeBasedInputSlicer buildSlicer() {
        return buildSlicer(0, 10);
    }

    private SizeBasedInputSlicer buildSlicer(int from, int to) {
        return buildSlicer(from, to, 1);
    }

    private SizeBasedInputSlicer buildSlicer(int from, int to, long sizeLimit) {
        return buildSlicer(IntStream.range(from, to).boxed(), sizeLimit);
    }

    private <T> SizeBasedInputSlicer buildSlicer(Stream<T> input, Long sizeLimit) {
        String text = input.map(Object::toString).collect(joining("\n"));
        return new SizeBasedInputSlicer(new ByteArrayInputStream(text.getBytes()), sizeLimit);
    }
}