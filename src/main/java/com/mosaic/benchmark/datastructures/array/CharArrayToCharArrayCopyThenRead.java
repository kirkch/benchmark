package com.mosaic.benchmark.datastructures.array;

import com.mosaic.benchmark.Benchmark;
import com.mosaic.benchmark.BenchmarkSizing;

/**
 * conclusion:  direct buffer access was averaged 3.5 reads/ns; copy buffer to
 * array then access averaged 2.5 reads/ns
 */
public class CharArrayToCharArrayCopyThenRead extends Benchmark {

    private int arraySize;
    private char[] array1;
    private char[] array2;

    public CharArrayToCharArrayCopyThenRead() {
        super(
                "reads", // units
                BenchmarkSizing.InMemory,
                "How fast is reading from a char array after a copy?", // question
                "Copies an array before reading it" // description
        );

        this.arraySize = getBenchmarkSizing().numOpsPerTest;
    }

    @Override
    public void setUp() {
        array1 = new char[arraySize];
        array2 = new char[arraySize];
    }

    @Override
    public void tearDown() {
        array1 = null;
        array2 = null;
    }

    @Override
    protected long runSingleIteration() {
        System.arraycopy( array1, 0, array2, 0, arraySize );

        long sum = 0;

        for ( int j=0; j<arraySize; j++ ) {
            sum += array2[j];
        }

        return sum;
    }

}
