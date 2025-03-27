package org.example.crossover;

import org.example.Chromosome;

public interface CrossoverOperator {
    /*
        This function can implement crossovers at one or two chosen points.

        @params first chromosome to crossover
        @params second chromosome to crossover
        @return an array of the indices at which the crossover happened
    */
    int[] crossover(Chromosome first, Chromosome second);
}
