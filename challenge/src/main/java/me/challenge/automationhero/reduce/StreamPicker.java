package me.challenge.automationhero.reduce;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

class StreamPicker {

    private DataInputStream buffered;
    private Optional<Integer> cursor;

    public StreamPicker(InputStream input, int bufferSize) {
        this.buffered = new DataInputStream(new BufferedInputStream(input, bufferSize));
    }

    Optional<Integer> pick() {
        try {
            if (cursor == null) {
                cursor = isEmpty() ? Optional.empty() : Optional.of(buffered.readInt());
            }
            return cursor;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from input stream", e);
        }
    }

    Optional<Integer> read() {
        Optional<Integer> returnVal = pick();
        this.cursor = null;
        return returnVal;
    }

    private boolean isEmpty() throws IOException {
        return buffered.available() <= 0;
    }
}
