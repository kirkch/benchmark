package com.mosaic.benchmark.datastructures.array;

import com.mosaic.benchmark.Benchmark;
import com.mosaic.benchmark.BenchmarkSizing;

/**
 *
 */
public class ByteArrayWrite extends Benchmark {

    private int arraySize;
    private byte[] array;

    public ByteArrayWrite() {
        super(
            "array writes", // units
            BenchmarkSizing.InMemory,
            "How fast is writing to a byte array?", // question
            "Loops sequentially over every element in a byte array, writing a constant to each position as it goes" // description
        );

        this.arraySize = getBenchmarkSizing().numOpsPerTest;
    }

    @Override
    public void setUp() {
        array = new byte[arraySize];
    }

    @Override
    public void tearDown() {
        for ( int j=0; j<arraySize; j++ ) {
            if ( array[j] != 42 ) {
                throw new IllegalStateException( "array writes were optimised out, invalidating the test" );
            }
        }

        array = null;
    }

    @Override
    protected long runSingleIteration() {
        long sum = 0;

        for ( int j=0; j<arraySize; j++ ) {
            array[j] = 42;
        }

        return sum;
    }

}
