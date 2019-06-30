package me.challenge.automationhero;

import me.challenge.automationhero.utils.Logging;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Starter implements Logging {


    public static void main(String[] args) {

        String inputPathStr = args[0];
        String outputPathStr = args[1];
        int expectedMemoryLimit = 100 * 1024 * 1024;
        try {
            Path tempDirectory = Files.createTempDirectory("map-reduce-sorting");
            tempDirectory.toFile().deleteOnExit();
            new SplitSort(tempDirectory, expectedMemoryLimit)
                    .sortFile(Paths.get(inputPathStr), Paths.get(outputPathStr));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        System.exit(0);
    }

}