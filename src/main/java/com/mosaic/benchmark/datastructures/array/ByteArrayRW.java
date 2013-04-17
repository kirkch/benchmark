package com.mosaic.benchmark.datastructures.array;

import com.mosaic.benchmark.Benchmark;
import com.mosaic.benchmark.BenchmarkSizing;

/**
 *
 */
public class ByteArrayRW extends Benchmark {

    private int arraySize;
    private byte[] array;

    public ByteArrayRW() {
        super(
            "array rw", // units
            BenchmarkSizing.InMemory,
            "How fast is reading and writing from a byte array?", // question
            "Loops sequentially over every element in a byte array, summing its value and writing back a value calculated from the value read" // description
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
            sum += array[j] + j;
            array[j] = (byte) (sum & 0xFFFF);
        }

        return sum;
    }

}
