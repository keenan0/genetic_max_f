package org.example;

import java.util.Random;

public class OneCrossoverOperator implements CrossoverOperator {
    private Random rd = new Random();

    @Override
    public int[] crossover(Chromosome first, Chromosome second) {
        StringBuilder sba = new StringBuilder(first.getValueBin());
        StringBuilder sbb = new StringBuilder(second.getValueBin());

        int idx = rd.nextInt(sba.length());
        for(int k = 0; k < idx; ++k) {
            char temp = sba.charAt(k);
            sba.setCharAt(k, sbb.charAt(k));
            sbb.setCharAt(k, temp);
        }

        first.setValueBin(sba.toString());
        second.setValueBin(sbb.toString());

        return new int[]{idx};
    }
}
