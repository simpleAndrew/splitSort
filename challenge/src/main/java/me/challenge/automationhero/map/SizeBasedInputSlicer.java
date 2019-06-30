package me.challenge.automationhero.map;

import me.challenge.automationhero.utils.Logging;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SizeBasedInputSlicer implements Iterator<List<Integer>>, Iterable<List<Integer>>, Logging {

    private final long sliceSizeLimitBytes;

    private final BufferedReader buff;

    public SizeBasedInputSlicer(InputStream originalInput, long sliceSizeLimitBytes) {
        this.sliceSizeLimitBytes = sliceSizeLimitBytes;
        this.buff = new BufferedReader(new InputStreamReader(new BufferedInputStream(originalInput, 1024 * 10)));
    }

    @Override
    public Iterator<List<Integer>> iterator() {
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
    public List<Integer> next() {
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
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private long listToByteSize(List<Integer> list) {
        return list.size() * 4;
    }
}
