package com.roman;

public class RomanConverter {
    public static String convert(int decimal) {
        StringBuilder roman = new StringBuilder();
        for (RomanToDecimal romanToDecimal : RomanToDecimal.values()) {
            while (decimal >= romanToDecimal.decimal) {
                roman.append(romanToDecimal.roman);
                decimal -= romanToDecimal.decimal;
            }
        }
        return roman.toString();
    }

    enum RomanToDecimal {
        THOUSAND("M", 1000),
        NINE_HUNDREAD("CM", 900),
        FIVE_HUNDREAD("D", 500),
        FOUR_HUNDREAD("CD", 400),
        HUNDRED("C", 100),
        NINETY("XC", 90),
        FIFTY("L", 50),
        FOURTY("XL", 40),
        TEN("X", 10),
        NINE("IX", 9),
        FIVE("V", 5),
        FOUR("IV", 4),
        ONE("I", 1)
        ;

        final int decimal;
        final String roman;

        RomanToDecimal(String roman, int decimal) {
            this.decimal = decimal;
            this.roman = roman;
        }
    }
}
