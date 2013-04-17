package com.mosaic.benchmark;

/**
 *
 */
public abstract class Benchmark {

    private String          units;
    private BenchmarkSizing sizing;
    private String          question;
    private String          description;

    public Benchmark( String units, BenchmarkSizing sizing, String question, String description ) {
        this.units       = units;
        this.sizing      = sizing;
        this.question    = question;
        this.description = description;
    }


    public void setUp() throws Throwable {

    }

    public void tearDown() throws Throwable {

    }

    public final BenchmarkResult invoke() throws Throwable {
        int targetCount = sizing.numIterationsOfTest;
        long sum = 0;
        for ( int i=0; i<targetCount; i++ ) {
            sum += runSingleIteration();
        }

        return new BenchmarkResult( ((long) targetCount)*sizing.numOpsPerTest, units, sum );
    }


    protected abstract long runSingleIteration() throws Throwable;




    public String getName() {
        return getClass().getSimpleName();
    }

    public String getUnits() {
        return units;
    }

    public String getQuestion() {
        return question;
    }

    public String getDescription() {
        return description;
    }

    public BenchmarkSizing getBenchmarkSizing() {
        return sizing;
    }
}
