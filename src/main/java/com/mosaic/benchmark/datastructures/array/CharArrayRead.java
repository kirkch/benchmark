package com.mosaic.benchmark.datastructures.array;

import com.mosaic.benchmark.Benchmark;
import com.mosaic.benchmark.BenchmarkSizing;

/**
 *
 */
public class CharArrayRead extends Benchmark {

    private int arraySize;
    private char[] array;

    public CharArrayRead() {
        super(
                "array reads", // units
                BenchmarkSizing.InMemory,
                "How fast is reading from a char array?", // question
                "Loops sequentially over every element in a char array, summing its value so that the read is not optimised out" // description
        );

        this.arraySize = getBenchmarkSizing().numOpsPerTest;
    }

    @Override
    public void setUp() {
        array = new char[arraySize];
    }

    @Override
    public void tearDown() {
        array = null;
    }

    @Override
    protected long runSingleIteration() {
        long sum = 0;

        for ( int j=0; j<arraySize; j++ ) {
            sum += array[j];
        }

        return sum;
    }

}
