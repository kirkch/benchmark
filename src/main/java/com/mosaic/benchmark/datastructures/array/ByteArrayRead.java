package com.mosaic.benchmark.datastructures.array;

import com.mosaic.benchmark.Benchmark;
import com.mosaic.benchmark.BenchmarkSizing;

/**
 *
 */
public class ByteArrayRead extends Benchmark {

    private int arraySize;
    private byte[] array;

    public ByteArrayRead() {
        super(
            "array reads", // units
            BenchmarkSizing.InMemory,
            "How fast is reading from a byte array?", // question
            "Loops sequentially over every element in a byte array, summing its value so that the read is not optimised out" // description
        );

        this.arraySize = getBenchmarkSizing().numOpsPerTest;
    }

    @Override
    public void setUp() {
        array = new byte[arraySize];
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
