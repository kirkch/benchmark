package com.mosaic.benchmark;

/**
 *
 */
public abstract class Benchmark {

    private String units;
    private long   opsPerCall;
    private String question;
    private String description;

    public Benchmark( String units, long opsPerCall, String question, String description ) {
        this.units       = units;
        this.opsPerCall  = opsPerCall;
        this.question    = question;
        this.description = description;
    }


    public void setUp() {

    }

    public void tearDown() {

    }

    public final BenchmarkResult invoke( int targetCount ) {
        long sum = 0;
        for ( int i=0; i<targetCount; i++ ) {
            sum += invoke();
        }

        return new BenchmarkResult( ((long)targetCount)*opsPerCall, units, sum );
    }


    protected abstract long invoke();




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

}
