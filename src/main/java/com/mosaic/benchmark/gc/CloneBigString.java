package com.mosaic.benchmark.gc;

import com.mosaic.benchmark.Benchmark;
import com.mosaic.benchmark.BenchmarkSizing;

/**
 *
 */
public class CloneBigString extends Benchmark {

    private String bigString;

    public CloneBigString() {
        super(
            "clones", // units
            BenchmarkSizing.InMemory,
            "How fast is cloning and discarding big strings?", // question
            "" // description
        );
    }

    @Override
    public void setUp() {
        StringBuilder buf = new StringBuilder();

        for ( int i=0; i<2048; i++ ) {
            buf.append('a');
        }

        bigString = buf.toString();
    }

    @Override
    public void tearDown() {
        bigString = null;
    }

    @Override
    protected long runSingleIteration() {
        String c = new String( bigString );

        return c.length();
    }

}
