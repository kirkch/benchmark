package com.mosaic.benchmark;

/**
 *
 */
public enum BenchmarkSizing {
    InMemory(100000,10000),
    LocalDisk(100000,10);
//    RemoteNetwork(100000,10000)
//    LocalNetwork(100000,10000),

    public final int numOpsPerTest;
    public final int numIterationsOfTest;

    private BenchmarkSizing( int numOpsPerTest, int numIterationsOfTest ) {

        this.numOpsPerTest = numOpsPerTest;
        this.numIterationsOfTest = numIterationsOfTest;
    }
}
