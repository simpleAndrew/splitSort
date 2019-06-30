package me.challenge.automationhero.reduce;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.junit.Test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class StreamPickerTest {

    @Test
    public void shouldPickFirstInt() throws IOException {
        InputStream in = buildStream(0, 1);

        StreamPicker streamPicker = new StreamPicker(in, 100);

        assertEquals(Optional.of(0), streamPicker.pick());
    }

    @Test
    public void shouldReturnSameValueWithSubsequentPicks() throws IOException {
        InputStream in = buildStream(0, 1);

        StreamPicker streamPicker = new StreamPicker(in, 100);
        streamPicker.pick();

        assertEquals(Optional.of(0), streamPicker.pick());
    }

    @Test
    public void shouldPickNexNextValueAfterRead() throws IOException {
        InputStream in = buildStream(0, 2);

        StreamPicker streamPicker = new StreamPicker(in, 100);
        streamPicker.read();

        assertEquals(Optional.of(1), streamPicker.pick());
    }

    @Test
    public void shouldReturnNoneWhenInIsEmpty() throws IOException {
        InputStream in = buildStream(0, 1);

        StreamPicker streamPicker = new StreamPicker(in, 100);
        streamPicker.read();

        assertEquals(Optional.empty(), streamPicker.pick());
    }

    @Test
    public void shouldReadPickedValue() throws IOException {
        InputStream in = buildStream(0, 1);

        StreamPicker streamPicker = new StreamPicker(in, 100);
        Optional<Integer> pickedValue = streamPicker.pick();
        Optional<Integer> readValue = streamPicker.read();

        assertEquals(pickedValue, readValue);
    }

    @Test
    public void shouldReturnReadNoneOnFinishedInput() throws IOException {
        InputStream in = buildStream(0, 1);

        StreamPicker streamPicker = new StreamPicker(in, 100);

        streamPicker.read();

        assertEquals(Optional.empty(), streamPicker.read());
    }

    @Test
    public void shouldReadAllValuesWithReads() throws IOException {

        InputStream in = buildStream(0, 10);
        List<Integer> expected = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        StreamPicker streamPicker = new StreamPicker(in, 100);

        List<Integer> actual = new ArrayList<>();
        while (streamPicker.pick().isPresent()) {
            actual.add(streamPicker.read().orElse(-1));
        }

        assertEquals(expected, actual);
    }

    private static InputStream buildStream(int from, int to) {
        ByteOutputStream bytesOut = new ByteOutputStream();
        DataOutputStream result = new DataOutputStream(bytesOut);
        IntStream.range(from, to).forEachOrdered(i -> writeSafe(result, i));
        return bytesOut.newInputStream();
    }

    private static void writeSafe(DataOutputStream dataOut, int value) {
        try {
            dataOut.writeInt(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}