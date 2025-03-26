package org.example;

public class Codificator {
    private static double lowerBound;
    private static double upperBound;

    private static double precision;

    private static int hashedBits;
    private static double bucketLength;

    Codificator(GeneticIteratorBuilder b) {
        this.lowerBound = b.lowerBound;
        this.upperBound = b.upperBound;
        this.precision = b.precision;
        this.hashedBits = b.hashedBits;
        this.bucketLength = b.bucketLength;
    }

    private static int binToDec(String bin) {
        int power = 1, dec = 0;

        for(int i = bin.length() - 1; i >= 0 && i >= (bin.length() - hashedBits); --i) {
            if(bin.charAt(i) == '1')
                dec += power;

            power *= 2;
        }

        return dec;
    }

    public static double from(String bin) {
        int dec = binToDec(bin);
        double num = bucketLength * dec + lowerBound;

        return num;
    }

    private static String decToBin(int val) {
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

    public static String to(double val) {
        double transformed = (val - lowerBound) / bucketLength;
        String bin = decToBin((int)Math.round(transformed));

        return bin;
    }

    public static double getLowerBound() {return lowerBound;}
    public static double getUpperBound() {return upperBound;}
    public static double getPrecision() {return precision;}
    public static int getHashedBits() {return hashedBits;}
}