package org.example;

public class Codificator {
    private final double lowerBound;
    private final double upperBound;

    private final double precision;

    private final int hashedBits;
    private final double bucketLength;

    Codificator(GeneticIteratorBuilder b) {
        this.lowerBound = b.lowerBound;
        this.upperBound = b.upperBound;
        this.precision = b.precision;
        this.hashedBits = b.hashedBits;
        this.bucketLength = b.bucketLength;
    }

    private int binToDec(String bin) {
        int power = 1, dec = 0;

        for(int i = bin.length() - 1; i >= 0 && i >= (bin.length() - hashedBits); --i) {
            if(bin.charAt(i) == '1')
                dec += power;

            power *= 2;
        }

        return dec;
    }

    public double from(String bin) {
        int dec = binToDec(bin);
        double num = bucketLength * dec + lowerBound;

        return num;
    }

    private String decToBin(int val) {
        StringBuilder sb = new StringBuilder();

        if(val >= Math.pow(2, hashedBits)) {
            for(int i = 0; i < hashedBits; ++i)
                sb.append('1');
            return sb.toString();
        }

        while (val > 0) {
            int remainder = (int) (val % 2);
            sb.insert(0, remainder);
            val /= 2;
        }

        while (sb.length() < hashedBits) {
            sb.insert(0, '0');
        }

        return sb.toString();
    }

    public String to(double val) {
        double transformed = (val - lowerBound) / bucketLength;
        String bin = decToBin((int)Math.round(transformed));

        return bin;
    }

    public double getLowerBound() {return this.lowerBound;}
    public double getUpperBound() {return this.upperBound;}
    public double getPrecision() {return this.precision;}
    public int getHashedBits() {return this.hashedBits;}
}