package com.trivia;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;

public class GoldenMaster {

    public String getGameResult(long seed) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream);
        System.setOut(printStream);

        GameRunner.play(new Random(seed));

        return byteArrayOutputStream.toString();
    }

    public String getGoldenMaster(long seed) {
        try {
            return FileUtils.readFileToString(new File("goldenMasterData/" + seed + ".txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void generateGoldenMaster() {
        for (int seed = 0; seed < 1000; seed++) {
            try {
                FileUtils.writeStringToFile(new File("goldenMasterData/" + seed + ".txt"), getGameResult(seed));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
