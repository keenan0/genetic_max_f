package org.example;

import java.util.ArrayList;

public class Utils {
    public static int n_digits(int number) {
        int digits = 0;

        while(number > 0) {
            number /= 10;
            digits++;
        }

        return digits;
    }

    public static int binarySearchIntervals(double p, ArrayList<Double> arr, int n_chromosomes) {
        int left = 0, right = n_chromosomes - 1;

        while (left <= right) {
            int mid = (right - left) / 2 + left;

            if(p <= arr.get(mid)) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        return (left > 0) ? left - 1 : 0;
    }
}
