package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class GeneticHandler {
    private ArrayList<GeneticIterator> iterations = new ArrayList<>();
    private int n_iterations;
    public static PrintWriter pw;
    private int current_iteration = 0;

    static {
        try {
            pw = new PrintWriter("C:\\Users\\tumbr\\IdeaProjects\\aoc\\output.txt");
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    GeneticHandler(GeneticIterator it, int n_iterations) {
        this.n_iterations = n_iterations;
        this.iterations.add(it);

        GeneticIterator first = this.iterations.get(0);

        // Setup the initial generation
        first.fillChromosomes();
        first.fillFitness();
        first.fillSelectionProbabilities();
        first.fillSelectionIntervals();

        // Printing the initial generation
        first.print_initial();
        first.print_selection_probabilities();
        first.print_selection_intervals();
    }

    public void reset() {
        GeneticIterator.reset();

        GeneticIterator first = this.iterations.get(0);
        first.fillChromosomes();
        first.fillFitness();
        first.fillSelectionProbabilities();
        first.fillSelectionIntervals();

        for(int i = 1; i < n_iterations; ++i) {
            GeneticIterator it = this.iterations.get(i - 1).getNext();
            this.iterations.set(i, it);
        }
    }

    public GeneticIterator getCurrentIterator() {
        return this.iterations.get(current_iteration);
    }

    public boolean hasNext() {
        return this.current_iteration < this.n_iterations - 1;
    }

    public boolean hasPrevious() {
        return this.current_iteration > 0;
    }

    public void nextIterator() {
        if(this.current_iteration < this.n_iterations - 1) {
            this.current_iteration++;
        }
    }

    public void previousIterator() {
        if(this.current_iteration > 0) {
            this.current_iteration--;
        }
    }

    public void evolve() {
        GeneticIterator it = this.iterations.get(0);
        System.out.printf(
                "id=%3s   f=%12.8f mean=%12.8f sum=%12.8f mutations=%d\n",
                it.getId(),
                it.maxFitness(),
                it.meanFitness(),
                it.getFitnessSum(),
                it.getMutations()
        );

        for(int i = 1; i < n_iterations; ++i) {
            it = this.iterations.get(i - 1).getNext();

            this.iterations.add(it);

            System.out.printf(
                    "id=%3s   f=%12.8f mean=%12.8f sum=%12.8f mutations=%d\n",
                    it.getId(),
                    it.maxFitness(),
                    it.meanFitness(),
                    it.getFitnessSum(),
                    it.getMutations()
            );
        }

        System.out.println("Finished evolving.");

        double init = this.iterations.get(0).maxFitness();
        double fin = this.iterations.get(this.n_iterations - 1).maxFitness();
        String growth = "/!\\ The population evolved from %f to %f with a growth of %f.\n";
        System.out.printf(
                growth,
                init,
                fin,
                fin - init
        );

        pw.flush();
    }

    public GeneticIterator getIterator(int index) {
        if(0 <= index && index < this.n_iterations)
            return this.iterations.get(index);
        return null;
    }
}
