package com.mosaic.benchmark;

/**
 *
 */
public class BenchmarkResult {
    private long   numOps;
    private String units;
    private long   calculatedResult;

    public BenchmarkResult( long numOps, String units, long calculatedResult ) {
        this.numOps           = numOps;
        this.units            = units;
        this.calculatedResult = calculatedResult;
    }

    public long getNumOps() {
        return numOps;
    }

    public String getUnits() {
        return units;
    }

    public long getCalculatedResult() {
        return calculatedResult;
    }
}
