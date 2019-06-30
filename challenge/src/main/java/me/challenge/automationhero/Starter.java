package me.challenge.automationhero;

import me.challenge.automationhero.utils.Logging;

import java.nio.file.Paths;

public class Starter implements Logging {


    public static void main(String[] args) {

        String inputPathStr = args[0];
        String outputPathStr = args[1];

        try {
            new SplitSort().sortFile(Paths.get(inputPathStr), Paths.get(outputPathStr));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        System.exit(0);
    }

}


