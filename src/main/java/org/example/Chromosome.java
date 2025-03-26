package org.example;

import java.util.Random;

public class Chromosome {
    private int id;
    private double value;
    private String value_bin;

    private double fitness;
    private double selection_probability;

    Chromosome(int id, double x, String value_bin, double fitness, double selection_p) {
        this.id = id;
        this.value = x;
        this.value_bin = value_bin;
        this.fitness = fitness;
        this.selection_probability = selection_p;
    }

    Chromosome(Chromosome other) {
        this.id = other.id;
        this.value = other.value;
        this.value_bin = other.value_bin;
        this.fitness = other.fitness;
        this.selection_probability = other.selection_probability;
    }

    @Override
    public String toString() {
        String temp = ((this.value > 0) ? " " : "-");
        String formatString = "%3s. %-20s x= " + temp + "%-12.8f f= %.8f%n";

        String result = String.format(
                formatString,
                id + 1,
                this.value_bin,
                Math.abs(this.value),
                this.fitness
        );

        return result;
    }

    public void mutate() {
        Random rd = new Random();
        StringBuilder sb = new StringBuilder(this.value_bin);

        int idx = rd.nextInt(0, sb.length());

        if (sb.charAt(idx) == '0') {
            sb.setCharAt(idx, '1');
        } else {
            sb.setCharAt(idx, '0');
        }

        this.setValueBin(sb.toString());
    }
//    public void mutate() {
//        Random rd = new Random();
//        StringBuilder sb = new StringBuilder(this.value_bin);
//
//        int idx = rd.nextInt(sb.length());
//        if (sb.charAt(idx) == '0') {
//            sb.setCharAt(idx, '1');
//        } else {
//            sb.setCharAt(idx, '0');
//        }
//
//        if (rd.nextDouble() < 0.1) {
//            int secondIdx = rd.nextInt(sb.length());
//
//            if (rd.nextDouble() < 0.7) {
//                secondIdx = rd.nextInt(sb.length() / 2);
//            }
//
//            if (sb.charAt(secondIdx) == '0') {
//                sb.setCharAt(secondIdx, '1');
//            } else {
//                sb.setCharAt(secondIdx, '0');
//            }
//        }
//
//        this.value_bin = sb.toString();
//        this.value = Codificator.from(this.value_bin);
//    }

    public void mutate(double mutation_p) {
        Random rd = new Random();
        StringBuilder sb = new StringBuilder(this.value_bin);

        for(int j = 0; j < sb.length(); j++) {
            double u = rd.nextDouble();

            if(u < mutation_p){
                if (sb.charAt(j) == '0') {
                    sb.setCharAt(j, '1');
                } else {
                    sb.setCharAt(j, '0');
                }
            }
        }
    }

    // Getters and Setters
    public int getId() {return this.id;}
    public void setId(int id) {
        if(id < 0) {id = 0;}
        this.id = id;
    }

    public double getValue() {return this.value;}
    public void setValue(double value) {
        this.value = value;
        this.value_bin = Codificator.to(value);
        this.fitness = 0;
        this.selection_probability = 0;
    }

    public String getValueBin() {return this.value_bin;}
    public void setValueBin(String bin) {
        this.value_bin = bin;
        this.value = Codificator.from(bin);
        this.fitness = 0;
        this.selection_probability = 0;
    }

    public double getFitness() {return this.fitness;}
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getSelectionP() {return this.selection_probability;}
    public void setSelectionP(double prob) {
        if(prob < 0.0 || prob > 1.0) {return;}

        this.selection_probability = prob;
    }
}
