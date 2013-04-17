package com.mosaic.benchmark.datastructures.buffers;

import com.mosaic.benchmark.Benchmark;
import com.mosaic.benchmark.BenchmarkSizing;

import java.nio.ByteBuffer;

/**
 *
 */
public class ByteBufferWrite extends Benchmark {

    private int bufferSize;
    private ByteBuffer buf;

    public ByteBufferWrite() {
        super(
            "buffer writes", // units
            BenchmarkSizing.InMemory,
            "How fast is writing to a normal nio byte buffer?", // question
            "Loops sequentially over every element in a byte buffer, writing in a constant value" // description
        );

        this.bufferSize = getBenchmarkSizing().numOpsPerTest;
    }

    @Override
    public void setUp() {
        buf = ByteBuffer.allocate( bufferSize );
    }

    @Override
    public void tearDown() {
        for ( int i=0; i<bufferSize; i++ ) {
            if ( buf.get(i) != 42 ) {
                throw new IllegalStateException( "array writes were optimised out, invalidating the test" );
            }
        }

        buf = null;
    }

    @Override
    protected long runSingleIteration() {
        for ( int i=0; i< bufferSize; i++ ) {
            buf.put( i, (byte) 42 );
        }

        return 0;
    }

}
