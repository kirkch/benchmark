package com.mosaic.benchmark.datastructures.buffers;

import com.mosaic.benchmark.Benchmark;
import com.mosaic.benchmark.BenchmarkSizing;

import java.nio.CharBuffer;

/**
 *
 */
public class CharBufferRead extends Benchmark {

    private int bufferSize;
    private CharBuffer buf;

    public CharBufferRead() {
        super(
                "buffer reads", // units
                BenchmarkSizing.InMemory,
                "How fast is reading from a normal nio char buffer?", // question
                "Loops sequentially over every element in a char buffer, summing its value so that the read is not optimised out" // description
        );

        this.bufferSize = getBenchmarkSizing().numOpsPerTest;
    }

    @Override
    public void setUp() {
        buf = CharBuffer.allocate( bufferSize );
    }

    @Override
    public void tearDown() {
        buf = null;
    }

    @Override
    protected long runSingleIteration() {
        long sum = 0;

        for ( int i=0; i< bufferSize; i++ ) {
            sum += buf.get( i );
        }

        return sum;
    }

}
