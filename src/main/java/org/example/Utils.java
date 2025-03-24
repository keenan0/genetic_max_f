package org.example;

public class Utils {
    public static int n_digits(int number) {
        int digits = 0;

        while(number > 0) {
            number /= 10;
            digits++;
        }

        return digits;
    }
}
