package com.mosaic.benchmark.datastructures.array;

import com.mosaic.benchmark.Benchmark;

/**
 *
 */
public class UncontendedByteArrayWrite extends Benchmark {

    private int arraySize;
    private byte[] array;

    public UncontendedByteArrayWrite( int arraySize ) {
        super(
            "array writes", // units
            arraySize, // number of ops per call to invoke()
            "How fast is writing to a byte array?", // question
            "Loops sequentially over every element in a byte array, writing a constant to each position as it goes" // description
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
        for ( int j=0; j<arraySize; j++ ) {
            if ( array[j] != 42 ) {
                throw new IllegalStateException( "array writes were optimised out, invalidating the test" );
            }
        }

        array = null;
    }

    @Override
    protected long invoke() {
        long sum = 0;

        for ( int j=0; j<arraySize; j++ ) {
            array[j] = 42;
        }

        return sum;
    }

}
