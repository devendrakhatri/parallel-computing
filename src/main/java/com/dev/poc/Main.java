package com.dev.poc;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorSpecies;

import java.util.Arrays;
import java.util.Random;

public class Main {
    private final static VectorSpecies<Double> SPECIES = DoubleVector.SPECIES_PREFERRED;

    public static void main(String[] args) {
        System.out.println("Hello world!");
        int length = SPECIES.length();
        System.out.println("length = " + length);

        double[] arr1 = new double[512];
        Arrays.fill( arr1, 25.0 );

        double[] arr2 = new double[512];
        Arrays.fill( arr2, 15.0 );

        long startTime = System.currentTimeMillis();
        double[] resultVectorApi = addUsingVectorApi(SPECIES, arr1, arr2);
        long vectorApiTime = System.currentTimeMillis() - startTime;
        System.out.println("vectorApiTime = " + vectorApiTime);
        System.out.println("resultVectorApi.length() = " + resultVectorApi.length);

        startTime = System.currentTimeMillis();
        double[] resultStandardApi = addStandard(arr1, arr2);
        long stdApiTime = System.currentTimeMillis() - startTime;
        System.out.println("stdApiTime = " + stdApiTime);
        System.out.println("sum via vector api = " + resultVectorApi[new Random().nextInt(arr1.length)]);
        System.out.println("sum via standard api = " + resultStandardApi[new Random().nextInt(arr1.length)]);
    }

    private static double[] addUsingVectorApi(VectorSpecies<Double> SPECIES, double[] arr1, double[] arr2) {

        var result = new double[arr1.length];
        int upperBound = SPECIES.loopBound(arr1.length);

        var i = 0;
        for(; i < upperBound; i+= SPECIES.length()) {
            DoubleVector vector1 = DoubleVector.fromArray(SPECIES, arr1, i);
            DoubleVector vector2 = DoubleVector.fromArray(SPECIES, arr2, i);

            DoubleVector fractionalResult = vector1.add(vector2);
            fractionalResult.intoArray(result, i);
        }

        for(; i < arr1.length; i++) {
            result[i] = arr1[i] + arr1[i];
        }

        return result;
    }

    private static double[] addUsingVectorApiWithMask(VectorSpecies<Double> SPECIES, double[] arr1, double[] arr2) {

        var result = new double[arr1.length];

        for(int i = 0; i < arr1.length; i+= SPECIES.length()) {

            var mask = SPECIES.indexInRange(i, arr1.length);

            DoubleVector vector1 = DoubleVector.fromArray(SPECIES, arr1, i, mask);
            DoubleVector vector2 = DoubleVector.fromArray(SPECIES, arr2, i, mask);

            DoubleVector fractionalResult = vector1.add(vector2);
            fractionalResult.intoArray(result, i);
        }

        return result;
    }

    private static double[] addStandard(double[] arr1, double[] arr2) {
        double[] result = new double[arr1.length];
        for (int i = 0; i < arr1.length; i++) {
            result[i] = arr1[i] + arr2[i];
        }
        return result;
    }
}