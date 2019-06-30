package me.challenge.automationhero.map;

import me.challenge.automationhero.utils.Logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class SizeBasedInputSlicer implements Iterator<Stream<Integer>>, Iterable<Stream<Integer>>, Logging {

    private final long sliceSizeLimitBytes;

    private final BufferedReader buff;

    public SizeBasedInputSlicer(InputStream originalInput, long sliceSizeLimitBytes) {
        this.sliceSizeLimitBytes = sliceSizeLimitBytes;
        this.buff = new BufferedReader(new InputStreamReader(originalInput),  10);
    }

    @Override
    public Iterator<Stream<Integer>> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        try {
            return buff.ready();
        } catch (IOException e) {
            throw new RuntimeException("Failed to continue reading output", e);
        }
    }

    @Override
    public Stream<Integer> next() {
        log("Generating next list");
        List<Integer> result = new LinkedList<>();
        try {
            while (listToByteSize(result) < sliceSizeLimitBytes) {
                String nextInt = buff.readLine();
                if (nextInt == null) {
                    break;
                }
                result.add(Integer.parseInt(nextInt));
            }
            log("Finished list generation");
            return result.stream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private long listToByteSize(List<Integer> list) {
        int i = list.size() * 4;
        return i;
    }
}
