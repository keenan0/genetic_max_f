package org.example;

import java.io.*;
import java.util.*;

public class Main {
    public static String file_path = "C:\\Users\\tumbr\\IdeaProjects\\aoc\\src\\main\\java\\org\\example\\data.in";

    private static int n_chromosomes;
    private static double lower_bound, upper_bound, a, b, c;
    private static double precision, p_crossover, p_mutation;
    private static int n_iterations;

    private static void input(Scanner scanner) {
        n_chromosomes = scanner.nextInt();
        lower_bound = scanner.nextDouble();
        upper_bound = scanner.nextDouble();

        a = scanner.nextDouble();
        b = scanner.nextDouble();
        c = scanner.nextDouble();

        precision = scanner.nextDouble();
        p_crossover = scanner.nextDouble();
        p_mutation  = scanner.nextDouble();

        n_iterations = scanner.nextInt();
    }

    public static void main(String[] args) {
        try {
            File file = new File(file_path);
            Scanner scanner = new Scanner(file);

            input(scanner);

            Codificator cd = new GeneticIteratorBuilder()
                    .setLowerBound(lower_bound)
                    .setUpperBound(upper_bound)
                    .setPrecision(precision)
                    .build();

            GeneticIterator it = new GeneticIterator(a, b, c, p_crossover, p_mutation, n_chromosomes);
            GeneticHandler handler = new GeneticHandler(it, n_iterations);

            handler.evolve();
        } catch(FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}