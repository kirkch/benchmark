package com.mosaic.benchmark.datastructures.array;

import com.mosaic.benchmark.Benchmark;

/**
 *
 */
public class UncontendedByteArrayRW extends Benchmark {

    private int arraySize;
    private byte[] array;

    public UncontendedByteArrayRW( int arraySize ) {
        super(
            "array rw", // units
            arraySize, // number of ops per call to invoke()
            "How fast is reading and writing from a byte array?", // question
            "Loops sequentially over every element in a byte array, summing its value and writing back a value calculated from the value read" // description
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
            sum += array[j] + j;
            array[j] = (byte) (sum & 0xFFFF);
        }

        return sum;
    }

}
