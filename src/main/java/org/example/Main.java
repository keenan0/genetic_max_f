package org.example;

import java.io.*;
import java.util.*;

class GeneticIterator {
    private int iteration_id;
    private static int n_iterations = 0;
    private static Random rd = new Random();
    private Codificator codificator;

    private int n_chromosomes;
    private ArrayList<Chromosome> chromosomes = new ArrayList<>();
    private ArrayList<Double> selection_probability_intervals = new ArrayList<>();

    private double fitness_sum;

    // 2nd Deg Equation
    private double a, b, c;

    private double crossover_probability;
    private double mutation_probability;

    {
        this.iteration_id = ++n_iterations;
    }

    GeneticIterator(Codificator cd, double a, double b, double c, double crossover_p, double mutation_p, int n_chromosomes) {
        this.codificator = cd;

        this.a = a;
        this.b = b;
        this.c = c;

        this.crossover_probability = crossover_p;
        this.mutation_probability = mutation_p;
        this.n_chromosomes = n_chromosomes;
    }

    GeneticIterator(GeneticIterator other) {
        this.codificator = other.codificator;
        this.a = other.a;
        this.b = other.b;
        this.c = other.c;

        this.crossover_probability = other.crossover_probability;
        this.mutation_probability = other.mutation_probability;
        this.n_chromosomes = other.n_chromosomes;

        this.chromosomes = new ArrayList<>();
        for (Chromosome ch : other.chromosomes) {
            this.chromosomes.add(new Chromosome(ch));
        }

        this.selection_probability_intervals = new ArrayList<>();
        this.fitness_sum = 0;
    }

    public double getFitnessSum() {
        return fitness_sum;
    }

    private ArrayList<Chromosome> getCrossoverChromosomes() {
        boolean initial = this.iteration_id == 2;
        ArrayList<Chromosome> cross_chromosomes = new ArrayList<>();

        for(int i = 0; i < this.n_chromosomes; i++) {
            double u = rd.nextDouble();

            if(initial) {
                String format = "%" + Utils.n_digits(this.n_chromosomes) + "s. %-"+this.codificator.getHashedBits()+"s u=%.8f ";
                System.out.printf(format,
                        i + 1,
                        this.chromosomes.get(i).getValueBin(),
                        u
                );
            }

            if(u < this.crossover_probability) {
                cross_chromosomes.add(this.chromosomes.get(i));

                if(initial) System.out.println(" < " + this.crossover_probability);
            } else {
                if(initial) System.out.println();
            }
        }

        return cross_chromosomes;
    }

    public void mutate() {
        boolean initial = this.iteration_id == 2;

        if(initial) System.out.println("Mutation Phase:");
        for(int i = 0; i < n_chromosomes; i++) {
            double u = rd.nextDouble();

            if(u < this.mutation_probability) {
                if(initial) {
                    System.out.printf(
                            "%d | ",
                            i + 1
                    );
                }

                this.chromosomes.get(i).mutate();
            }
        }
        if(initial) System.out.println();
    }

    public void crossover() {
        boolean initial = this.iteration_id == 2;
        ArrayList<Chromosome> cross_chromosomes = this.getCrossoverChromosomes();

        if(initial) System.out.println();

        while(cross_chromosomes.size() > 1) {
            int idx_ch1 = rd.nextInt(cross_chromosomes.size());
            int idx_ch2 = rd.nextInt(cross_chromosomes.size());

            while(idx_ch1 == idx_ch2) {
                idx_ch2 = rd.nextInt(cross_chromosomes.size());
            }

            Chromosome ch1 = cross_chromosomes.get(idx_ch1);
            Collections.swap(cross_chromosomes, idx_ch1, cross_chromosomes.size() - 1);
            cross_chromosomes.remove(cross_chromosomes.size() - 1);

            // Last element was deleted. idx_ch2 could have been initially list.size() - 1 = list.size() after deletion
            if(idx_ch2 == cross_chromosomes.size()) {
                idx_ch2 = idx_ch1;
            }

            Chromosome ch2 = cross_chromosomes.get(idx_ch2);
            Collections.swap(cross_chromosomes, idx_ch2, cross_chromosomes.size() - 1);
            cross_chromosomes.remove(cross_chromosomes.size() - 1);

            if(initial) {
                String format = "Crossover between: chromosome %" + Utils.n_digits(this.n_chromosomes) + "s and chromosome %" + Utils.n_digits(this.n_chromosomes) + "s\n";
                System.out.printf(format, ch1.getId() + 1, ch2.getId() + 1);
            }

            OneCrossoverOperator op = new OneCrossoverOperator();
            if(initial) {
                System.out.printf(
                        "%s %s | at index ",
                        ch1.getValueBin(),
                        ch2.getValueBin()
                );
            }

            int[] indices = op.crossover(ch1, ch2);

            // Move this functionality to other class
            ch1.setValue(this.codificator.from(ch1.getValueBin()));
            ch2.setValue(this.codificator.from(ch2.getValueBin()));

            if(initial) {
                System.out.printf(
                        "%d\n",
                        indices[0]
                );

                System.out.printf(
                    "Result: %s %s\n",
                    ch1.getValueBin(),
                    ch2.getValueBin()
                );
            }
        }

        if(initial) System.out.println();
    }

    private int binarySearchIntervals(double p) {
        int left = 0, right = n_chromosomes - 1;

        while (left <= right) {
            int mid = (right - left) / 2 + left;

            if(p <= this.selection_probability_intervals.get(mid)) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        return (left > 0) ? left - 1 : 0;
    }

    private Chromosome getBestChromosome() {
        Chromosome ans = this.chromosomes.get(0);

        for(int i = 0; i < n_chromosomes; ++i) {
            if(this.chromosomes.get(i).getFitness() > ans.getFitness()) {
                ans = this.chromosomes.get(i);
            }
        }

        ans = new Chromosome(ans);
        ans.setId(0);
        return ans;
    }

    public GeneticIterator getNext() {
        boolean initial = this.iteration_id == 1;
        GeneticIterator next = new GeneticIterator(this);

        if(initial) System.out.println("Selection Phase:");

        // Elitism to keep the best chromosome always
        boolean elitism = true;

        int k = 0;
        if(elitism) {
            Chromosome best_chromosome = getBestChromosome();
            next.chromosomes.set(0, best_chromosome);
            k = 1;
        }

        for(int i = k; i < this.n_chromosomes; i++) {
            double u = rd.nextDouble();
            int idx = binarySearchIntervals(u);

            if(initial) {
                String formatString = "u= %.8f choosing chromosome %d\n";
                System.out.printf(
                        formatString,
                        u,
                        idx + 1
                );
            }

            Chromosome chosen_chromosome = new Chromosome(this.chromosomes.get(idx));
            chosen_chromosome.setId(i);
            next.chromosomes.set(i, chosen_chromosome);
        }

        if(initial) System.out.println();

        next.crossover();
        next.fillFitness();

        next.mutate();
        next.fillFitness();
        next.fillSelectionProbabilities();
        next.fillSelectionIntervals();

        return next;
    }

    public void add(Chromosome c) {
        this.chromosomes.add(c);
    }

    public double maxFitness() {
        double maxF = Double.NEGATIVE_INFINITY;
        for(int i = 0; i < n_chromosomes; ++i) {
            maxF = Math.max(maxF, this.chromosomes.get(i).getFitness());
        }
        return maxF;
    }

    public double meanFitness() {
        return (this.fitness_sum / this.n_chromosomes);
    }

    private double f(double x) {
        return (this.a * x * x + this.b * x + this.c);
    }

    public void fillChromosomes() {
        for(int i = 0; i < n_chromosomes; i++) {
            double chromosome_x = rd.nextDouble(this.codificator.getLowerBound(), this.codificator.getUpperBound());

            Chromosome c = new Chromosome(
                    i,
                    chromosome_x,
                    this.codificator.to(chromosome_x),
                    0.0,
                    0.0);

            this.chromosomes.add(c);
        }
    }

    public void fillFitness() {
        this.fitness_sum = 0;
        for(int i = 0; i < n_chromosomes; i++) {
            double fitness = this.f(this.chromosomes.get(i).getValue());
            this.fitness_sum += fitness;

            this.chromosomes.get(i).setFitness(fitness);
        }
    }

    public void fillSelectionProbabilities() {
        if(this.fitness_sum == 0) {return;}

        for(int i = 0; i < n_chromosomes; i++) {
            double selection_p = this.chromosomes.get(i).getFitness() / this.fitness_sum;

            this.chromosomes.get(i).setSelectionP(selection_p);
        }
    }

    public void fillSelectionIntervals() {
        if(this.fitness_sum == 0) {return;}

        double prob_sum = 0.0f;

        this.selection_probability_intervals.add(prob_sum);
        for(int i = 0; i < n_chromosomes; i++) {
            prob_sum += this.chromosomes.get(i).getSelectionP();

            this.selection_probability_intervals.add(prob_sum);
        }
    }

    public void print_initial() {
        System.out.println("Initial population:");

        for(int i = 0; i < n_chromosomes; i++) {
            System.out.print(this.chromosomes.get(i));
        }

        System.out.println();
    }

    public void print_selection_probabilities() {
        System.out.println("Selection probabilities:");

        for(int i = 0; i < n_chromosomes; i++) {
            String formatString = "chromosome %-" + Utils.n_digits(this.codificator.getHashedBits()) + "s prob=%9.6f\n";

            System.out.printf(formatString, i + 1, this.chromosomes.get(i).getSelectionP());
        }

        System.out.println();
    }

    public void print_selection_intervals() {
        System.out.println("Selection intervals:");
        for(int i = 0; i < n_chromosomes; i++) {
            String format = "%" + Utils.n_digits(this.codificator.getHashedBits()) + "s. [%.8f : %.8f]\n";

            System.out.printf(format, i + 1, this.selection_probability_intervals.get(i), this.selection_probability_intervals.get(i + 1));
        }

        System.out.println();
    }

    public void print() {
        String fp = "C:\\Users\\tumbr\\IdeaProjects\\aoc\\src\\main\\java\\org\\example\\out.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fp))) {
            for(int i = 0; i < n_chromosomes; i++) {
                writer.write(this.chromosomes.get(i).toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

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

            GeneticIterator it = new GeneticIterator(cd, a, b, c, p_crossover, p_mutation, n_chromosomes);
            GeneticHandler handler = new GeneticHandler(it, n_iterations);

            handler.evolve();
        } catch(FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}