package me.challenge.automationhero;

import me.challenge.automationhero.utils.Logging;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class IntegerRandomnessGenerator implements Logging {

    private static int MAX_FILE_SIZE = 100 * 1024 * 1024;
    private static int LOGGING_GAUGE_LIMIT = MAX_FILE_SIZE / 20;

    public static void main(String[] args) throws IOException {

        String destinationStr = args[0];

        new IntegerRandomnessGenerator().generateFile(destinationStr, MAX_FILE_SIZE);

    }

    private void generateFile(String destinationStr, int maxSize) throws IOException {
        log("Generating ints to " + destinationStr + "; size=" + maxSize);

        Random random = new Random();


        int loggingGauge = 0;
        try (OutputStream stream = new BufferedOutputStream(new FileOutputStream(destinationStr, false))) {

            for (String i = toNewLineString(random);
                 maxSize - i.getBytes().length > 0;
                 maxSize -= i.getBytes().length, i = toNewLineString(random)) {

                stream.write(i.getBytes());

                loggingGauge += i.getBytes().length;
                if (loggingGauge > LOGGING_GAUGE_LIMIT) {
                    log("Generated another " + LOGGING_GAUGE_LIMIT + "; current value: " + i);
                    loggingGauge = 0;
                }

            }

            log("Finished file generation");
        }

        log("File saved");
    }

    private static String toNewLineString(Random r) {
        return String.format("%d\n", r.nextInt());
    }

    private static String toNewLineString(int v) {
        return String.format("%d\n", v);
    }
}
