package com.mosaic.benchmark.datastructures.buffers;

import com.mosaic.benchmark.Benchmark;
import com.mosaic.benchmark.BenchmarkSizing;

import java.nio.ByteBuffer;

/**
 *
 */
public class DirectByteBufferRead extends Benchmark {

    private int bufferSize;
    private ByteBuffer buf;

    public DirectByteBufferRead() {
        super(
            "direct buffer reads", // units
            BenchmarkSizing.InMemory,
            "How fast is reading from a direct nio byte buffer?", // question
            "Loops sequentially over every element in a byte buffer, summing its value so that the read is not optimised out" // description
        );

        this.bufferSize = getBenchmarkSizing().numOpsPerTest;
    }

    @Override
    public void setUp() {
        buf = ByteBuffer.allocateDirect( bufferSize );
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
