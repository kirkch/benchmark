package com.mosaic.benchmark.datastructures.buffers;

import com.mosaic.benchmark.Benchmark;
import com.mosaic.benchmark.BenchmarkSizing;

import java.nio.CharBuffer;

/**
 *
 */
public class CharBufferToCharArrayCopyThenRead extends Benchmark {

    private int bufferSize;
    private CharBuffer buf;
    private char[]     array;

    public CharBufferToCharArrayCopyThenRead() {
        super(
                "reads", // units
                BenchmarkSizing.InMemory,
                "How fast is copy chars out of a CharBuffer and then access them array directly?", // question
                "Trying to figure out the optimal way to process chars incrementally" // description
        );

        this.bufferSize = getBenchmarkSizing().numOpsPerTest;
    }

    @Override
    public void setUp() {
        buf = CharBuffer.allocate( bufferSize );
        array = new char[bufferSize];
    }

    @Override
    public void tearDown() {
        buf = null;
        array = null;
    }

    @Override
    protected long runSingleIteration() {
        buf.get(array, 0, bufferSize);

        long sum = 0;

        for ( int i=0; i< bufferSize; i++ ) {
            sum += array[i];
        }

        buf.position(0);

        return sum;
    }

}
