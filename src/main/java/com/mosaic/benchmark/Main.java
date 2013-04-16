package com.mosaic.benchmark;

import com.mosaic.benchmark.datastructures.array.UncontendedByteArrayWrite;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Run all benchmarks.
 *
 * -XX:BiasedLockingStartupDelay=0 (removes jitter 4s from startup when biased locking would usually be turned on by the Oracle JVM)
 * -XX:+PrintGCApplicationStoppedTime (negligible cost)
 * -XX:+PrintCompilation (very slight cost)
 */
public class Main {

    private static List<Benchmark> benchmarks = Arrays.<Benchmark>asList(
    //        new UncontendedByteArrayRead(100000),
    //        new UncontendedByteArrayRW(10000),
        new UncontendedByteArrayWrite(100000)
    );

    private static MeasureJitter measureJitterThread = new MeasureJitter();

    public static void main( String[] args ) throws InterruptedException {

        measureJitterThread.start();

        Thread.sleep(2000);
        measureJitterThread.printMaxJitterMillis();

        measureJitterThread.reset();
        Thread.sleep(200);
        measureJitterThread.printMaxJitterMillis();

        measureJitterThread.reset();
        Thread.sleep(200);
        measureJitterThread.printMaxJitterMillis();

        for ( Benchmark b : benchmarks ) {
            runBenchmarkBatch( b );
        }

    }

    private static void runBenchmarkBatch( Benchmark b ) {
        b.setUp();

        System.gc();

        System.out.println( "warm up" );

        runBenchmark( b, true );
        runBenchmark( b, true);

        System.out.println( "starting benchmarks" );


        runBenchmark( b, false );
        runBenchmark( b, false );
        runBenchmark( b, false );
        runBenchmark( b, false );

        b.tearDown();
    }

    private static void runBenchmark( Benchmark b, boolean isWarmup ) {
        measureJitterThread.reset();

        long startNanos = System.nanoTime();

        try {


            BenchmarkResult result = b.invoke( 10000 );

            long durationNanos = System.nanoTime() - startNanos;
            double durationMillis = durationNanos / 1000000.0;
            double maxJitterMillis = measureJitterThread.getMaxJitterMillis();

            long numOps = result.getNumOps();
            double rateMS = ((double)numOps) / durationNanos * 1000000;
            double rateNS = ((double)numOps) / durationNanos;


            String prefix = isWarmup ? "[WARMUP] " : "";

//            System.out.println( prefix + rateNS + " " + result.getUnits() + "/ns  [maxJitter="+maxJitterMillis+"ms totalTestRun="+durationMillis+"ms]" );
            System.out.println( String.format("%s%.2f %s/ns  [maxJitter=%.3fms totalTestRun=%.3fms]", prefix,rateNS,result.getUnits(),maxJitterMillis,durationMillis) );

        } catch ( Throwable ex ) {
            ex.printStackTrace();
        }
    }

    private static class MeasureJitter extends Thread {
        private AtomicLong maxJitterWitnessedNS = new AtomicLong(0);

        public MeasureJitter() {
            setDaemon( true );
        }

        public void reset() {
            maxJitterWitnessedNS.set( 0 );
        }

        public double getMaxJitterMillis() {
            return maxJitterWitnessedNS.get()/1000000.0;
        }

        public void printMaxJitterMillis() {
            System.out.println( "getMaxJitterMillis() = " + getMaxJitterMillis() );
        }

        @Override
        public void run() {
            super.run();

            long preSleepNS = System.nanoTime();
            while( true ) {
                try {
                    Thread.sleep( 1 );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                long wakeupNS = System.nanoTime();
                long jitterNS = wakeupNS - (preSleepNS+1000000);

                long max = Math.max( maxJitterWitnessedNS.get(), jitterNS );
                maxJitterWitnessedNS.lazySet( max );

                preSleepNS = wakeupNS;
            }
        }
    }
}
