package me.challenge.automationhero.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public interface Logging {

    default void log(String message, Throwable e) {
        String finalMsg = message + "; exception:" + Arrays.toString(e.getStackTrace());
        log(finalMsg);
    }

    default void log(String message) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);
        System.out.printf("%s -- %s\n", time, message);
    }
}
