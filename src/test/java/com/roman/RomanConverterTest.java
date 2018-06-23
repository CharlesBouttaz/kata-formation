package com.roman;

import org.junit.Test;

import static com.roman.RomanConverter.convert;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class RomanConverterTest {

//    I             1
//    V             5
//    X             10
//    L             50
//    C             100
//    D             500
//    M             1000
//    


    @Test
    public void acceptanceTest() {
        assertThat(convert(1989), is("MCMLXXXIX"));
    }

    @Test
    public void shouldConvertArabicToNumeral() {
        assertThat(convert(1), is("I"));
        assertThat(convert(2), is("II"));
        assertThat(convert(3), is("III")); // Duplication -> Refactor for loop
        assertThat(convert(5), is("V")); // Return 5
        assertThat(convert(7), is("VII")); // Introduce substraction
        assertThat(convert(10), is("X")); // Return 10 -> duplication -> extract Enum
        assertThat(convert(30), is("XXX")); // Transform if to while + transform for to new Enum value
        assertThat(convert(4), is("IV")); // Treat as a single character exception
    }
}