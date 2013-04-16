package com.mosaic.benchmark.datastructures.array;

import com.mosaic.benchmark.Benchmark;

/**
 *
 */
public class UncontendedByteArrayRead extends Benchmark {

    private int arraySize;
    private byte[] array;

    public UncontendedByteArrayRead( int arraySize ) {
        super(
            "array reads", // units
            arraySize, // number of ops per call to invoke()
            "How fast is reading from a byte array?", // question
            "Loops sequentially over every element in a byte array, summing its value so that the read is not optimised out" // description
        );

        this.arraySize = arraySize;
    }

    @Override
    public void setUp() {
        super.setUp();

        array = new byte[arraySize];
    }

    @Override
    public void tearDown() {
        array = null;
    }

    @Override
    protected long invoke() {
        long sum = 0;

        for ( int j=0; j<arraySize; j++ ) {
            sum += array[j];
        }

        return sum;
    }

}
