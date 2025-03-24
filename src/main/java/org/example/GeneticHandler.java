package org.example;

import java.util.ArrayList;

public class GeneticHandler {
    private ArrayList<GeneticIterator> iterations;
    private int n_iterations;

    GeneticHandler(GeneticIterator it, int n_iterations) {
        this.n_iterations = n_iterations;
        this.iterations = new ArrayList<>();
        this.iterations.add(it);

        GeneticIterator first = this.iterations.get(0);

        // Setup the initial generation
        first.fillChromosomes();
        first.fillFitness();
        first.fillSelectionProbabilities();
        first.fillSelectionIntervals();

        // Printing the initial generation
        //first.print_initial();
        //first.print_selection_probabilities();
        //first.print_selection_intervals();
    }

    public void evolve() {
        for(int i = 1; i < n_iterations; ++i) {
            GeneticIterator it = this.iterations.get(i - 1).getNext();

            this.iterations.add(it);

            System.out.printf(
                    "f=%12.8f mean=%12.8f sum=%12.8f\n",
                    it.maxFitness(),
                    it.meanFitness(),
                    it.getFitnessSum()
            );
        }

        System.out.println("Finished evolving.");
    }
}
