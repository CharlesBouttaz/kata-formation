package com.trivia;

import org.junit.Test;

import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class GameTest {

    @Test
    public void resultShouldBeTheSameAsGoldenMaster() {
        GoldenMaster goldenMaster = new GoldenMaster();
//        goldenMaster.generateGoldenMaster();

        for (int seed = 0; seed < 1000; seed++) {
            String actual = goldenMaster.getGameResult(seed);
            String expected = goldenMaster.getGoldenMaster(seed);
            assertThat(expected, is(actual));
        }
    }
}