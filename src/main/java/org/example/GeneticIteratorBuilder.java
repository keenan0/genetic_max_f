package org.example;

public class GeneticIteratorBuilder {
    double lowerBound;
    double upperBound;
    double precision;
    Integer hashedBits = null;
    Double bucketLength = null;

    GeneticIteratorBuilder() {}

    public GeneticIteratorBuilder setLowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
        return this;
    }

    public GeneticIteratorBuilder setUpperBound(double upperBound) {
        this.upperBound = upperBound;
        return this;
    }

    public GeneticIteratorBuilder setPrecision(double precision) {
        this.precision = precision;
        return this;
    }

    public GeneticIteratorBuilder setHashedBits(int hashedBits) {
        this.hashedBits = hashedBits;
        return this;
    }

    public GeneticIteratorBuilder setBucketLength(double bucketLength) {
        this.bucketLength = bucketLength;
        return this;
    }

    private static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    private void computeValues() {
        if(this.hashedBits == null) {
            double diff = (upperBound - lowerBound);
            double mult = diff * Math.pow(10, precision);
            double lg = log2(mult);

            this.hashedBits = (int) Math.ceil(lg);
        }

        if(this.bucketLength == null) {
            double diff = (upperBound - lowerBound);
            double denominator = Math.pow(2, hashedBits);

            this.bucketLength = diff / denominator;
        }
    }

    public Codificator build() {
        this.computeValues();
        return new Codificator(this);
    }
};
