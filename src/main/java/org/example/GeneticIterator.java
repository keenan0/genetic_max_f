package org.example;

import org.example.crossover.OneCrossoverOperator;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;

public class GeneticIterator {
    private int iteration_id;
    private static int n_iterations = 0;
    private static Random rd = new Random();

    private int n_chromosomes;
    private ArrayList<Chromosome> chromosomes = new ArrayList<>();
    private ArrayList<Double> selection_probability_intervals = new ArrayList<>();

    private double fitness_sum;

    // 2nd Deg Equation
    private double a, b, c;

    private int n_mutations;

    private double crossover_probability;
    private double mutation_probability;
    private boolean elitism = true;

    {
        this.iteration_id = n_iterations++;
    }

    GeneticIterator(double a, double b, double c, double crossover_p, double mutation_p, int n_chromosomes) {
        this.a = a;
        this.b = b;
        this.c = c;

        this.crossover_probability = crossover_p;
        this.mutation_probability = mutation_p;
        this.n_chromosomes = n_chromosomes;
    }

    GeneticIterator(GeneticIterator other) {
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

    public static void reset() {
        n_iterations = 1;
    }

    public double getFitnessSum() {
        return fitness_sum;
    }
    public int getId() {return this.iteration_id;}

    private ArrayList<Chromosome> getCrossoverChromosomes() {
        boolean initial = this.iteration_id == 2;
        ArrayList<Chromosome> cross_chromosomes = new ArrayList<>();

        for(int i = 0; i < this.n_chromosomes; i++) {
            double u = rd.nextDouble();

            if(initial) {
                String format = "%" + Utils.n_digits(this.n_chromosomes) + "s. %-"+ Codificator.getHashedBits()+"s u=%.8f ";
                GeneticHandler.pw.printf(format,
                        i + 1,
                        this.chromosomes.get(i).getValueBin(),
                        u
                );
            }

            if(u < this.crossover_probability) {
                cross_chromosomes.add(this.chromosomes.get(i));

                if(initial) GeneticHandler.pw.println(" < " + this.crossover_probability);
            } else {
                if(initial) GeneticHandler.pw.println();
            }
        }

        return cross_chromosomes;
    }

    public void mutate() {
        boolean initial = this.iteration_id == 2;

        this.n_mutations = 0;
        if(initial) GeneticHandler.pw.println("  * Mutation Phase:");
        for(int i = 0; i < n_chromosomes; i++) {
            double u = rd.nextDouble();

            if(u < this.mutation_probability) {
                if(initial) {
                    GeneticHandler.pw.printf(
                            "%d | ",
                            i + 1
                    );
                }

                this.n_mutations++;
                this.chromosomes.get(i).mutate();
            }
        }
        if(initial) GeneticHandler.pw.println();
    }

    public void crossover() {
        boolean initial = this.iteration_id == 2;
        ArrayList<Chromosome> cross_chromosomes = this.getCrossoverChromosomes();

        if(initial) GeneticHandler.pw.println();

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
                String format = "  * Crossover between: chromosome %" + Utils.n_digits(this.n_chromosomes) + "s and chromosome %" + Utils.n_digits(this.n_chromosomes) + "s\n";
                GeneticHandler.pw.printf(format, ch1.getId() + 1, ch2.getId() + 1);
            }

            OneCrossoverOperator op = new OneCrossoverOperator();
            if(initial) {
                GeneticHandler.pw.printf(
                        "%s %s | at index ",
                        ch1.getValueBin(),
                        ch2.getValueBin()
                );
            }

            int[] indices = op.crossover(ch1, ch2);

            if(initial) {
                GeneticHandler.pw.printf(
                        "%d\n",
                        indices[0]
                );

                GeneticHandler.pw.printf(
                        "Result: %s %s\n",
                        ch1.getValueBin(),
                        ch2.getValueBin()
                );
            }
        }

        if(initial) GeneticHandler.pw.println();
    }

    public int getMutations() {
        return this.n_mutations;
    }

    private Chromosome getBestChromosome() {
        if(this.chromosomes.size() < 1) return null;

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

        if(initial) GeneticHandler.pw.println("Selection Phase:");

        // Elitism to keep the best chromosome always
        int k = 0;
        if(elitism) {
            Chromosome best_chromosome = getBestChromosome();
            next.chromosomes.set(0, best_chromosome);
            k = 1;
        }

        for(int i = k; i < this.n_chromosomes; i++) {
            double u = rd.nextDouble();
            int idx = Utils.binarySearchIntervals(
                    u,
                    this.selection_probability_intervals,
                    this.n_chromosomes
            );

            if(initial) {
                String formatString = "u= %.8f choosing chromosome %d\n";
                GeneticHandler.pw.printf(
                        formatString,
                        u,
                        idx + 1
                );
            }

            Chromosome chosen_chromosome = new Chromosome(this.chromosomes.get(idx));
            chosen_chromosome.setId(i);
            next.chromosomes.set(i, chosen_chromosome);
        }

        if(initial) GeneticHandler.pw.println();

        next.crossover();
        next.fillFitness();

        next.mutate();
        if(initial) next.print_initial();

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

    public double f(double x) {
        return (this.a * x * x + this.b * x + this.c);
    }

    public ArrayList<Point2D.Double> getPoints() {
        ArrayList<Point2D.Double> points = new ArrayList<>();

        for(int i = 0; i < this.n_chromosomes; ++i) {
            Point2D.Double p = new Point2D.Double(
                    this.chromosomes.get(i).getValue(),
                    this.chromosomes.get(i).getFitness()
            );

            points.add(p);
        }

        return points;
    }

    public void fillChromosomes() {
        for(int i = 0; i < n_chromosomes; ++i) {
            this.chromosomes.add(new Chromosome(0, 0, "0", 0, 0));
        }

        for(int i = 0; i < n_chromosomes; i++) {
            double chromosome_x = rd.nextDouble(Codificator.getLowerBound(), Codificator.getUpperBound());

            Chromosome c = new Chromosome(
                    i,
                    chromosome_x,
                    Codificator.to(chromosome_x),
                    0.0,
                    0.0);

            this.chromosomes.set(i, c);
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
        GeneticHandler.pw.println("  * Current population:");

        for(int i = 0; i < n_chromosomes; i++) {
            GeneticHandler.pw.print(this.chromosomes.get(i));
        }

        GeneticHandler.pw.println();
    }

    public void print_selection_probabilities() {
        GeneticHandler.pw.println("  * Selection probabilities:");

        for(int i = 0; i < n_chromosomes; i++) {
            String formatString = "chromosome %-" + Utils.n_digits(Codificator.getHashedBits()) + "s prob=%9.6f\n";

            GeneticHandler.pw.printf(formatString, i + 1, this.chromosomes.get(i).getSelectionP());
        }

        GeneticHandler.pw.println();
    }

    public void print_selection_intervals() {
        GeneticHandler.pw.println("  * Selection intervals:");
        for(int i = 0; i < n_chromosomes; i++) {
            String format = "%" + Utils.n_digits(Codificator.getHashedBits()) + "s. [%.8f : %.8f]\n";

            GeneticHandler.pw.printf(format, i + 1, this.selection_probability_intervals.get(i), this.selection_probability_intervals.get(i + 1));
        }

        GeneticHandler.pw.println();
    }
}