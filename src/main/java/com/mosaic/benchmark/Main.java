package com.mosaic.benchmark;

import com.mosaic.benchmark.io.FileInputStreamArrayBatchRead;

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
//        new ByteArrayRead(numItterations),    // 2.46
//        new ByteBufferRead(numItterations),     // 1.9
//        new DirectByteBufferRead(numItterations),     // 1.55
//
//        new ByteArrayWrite(numItterations),   // 12.3
//        new ByteBufferWrite(numItterations),   // 13.5
//        new DirectByteBufferWrite(numItterations)   // 1.5

//        new FileInputStreamReadPerByte(),
//        new RandomAccessFileReadPerByte()
//        new BufferedFileStreamPerByte()
        new FileInputStreamArrayBatchRead(128),
        new FileInputStreamArrayBatchRead(258),
        new FileInputStreamArrayBatchRead(512),
        new FileInputStreamArrayBatchRead(1024),
        new FileInputStreamArrayBatchRead(4068),
        new FileInputStreamArrayBatchRead(8128),
        new FileInputStreamArrayBatchRead(16384)
    );



    private static MeasureJitter measureJitterThread = new MeasureJitter();

    public static void main( String[] args ) throws InterruptedException {
        measureJitterThread.start();

        Thread.sleep(1000);
        for ( int i=0; i<3; i++ ) {
            measureJitterThread.printMaxJitterMillis();

            measureJitterThread.reset();
            Thread.sleep(200);
        }

        for ( Benchmark b : benchmarks ) {
            try {
                runBenchmarkBatch( b );
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private static void runBenchmarkBatch( Benchmark b ) throws Throwable {
        b.setUp();

        System.gc();

        System.out.println( "warm up" );

        runBenchmark( b, RunMode.FirstRun );
        runBenchmark( b, RunMode.WramUp );

        System.out.println( "starting benchmarks" );


        runBenchmark( b, RunMode.NormalRun );
        runBenchmark( b, RunMode.NormalRun );
        runBenchmark( b, RunMode.NormalRun );
        runBenchmark( b, RunMode.LastRun );

        b.tearDown();
    }

    private static void runBenchmark( Benchmark b, RunMode runMode ) {
        measureJitterThread.reset();

        long startNanos = System.nanoTime();

        try {

            BenchmarkResult result = b.invoke();

            long durationNanos = System.nanoTime() - startNanos;
            double durationMillis = durationNanos / 1000000.0;
            double maxJitterMillis = measureJitterThread.getMaxJitterMillis();

            long numOps = result.getNumOps();
            double rateMS = ((double)numOps) / durationNanos * 1000000;
            double rateNS = ((double)numOps) / durationNanos;


            String prefix = runMode.isWarmUp ? "[WARMUP] " : "";
            prefix = "["+b.getName()+"]: ";

//            System.out.println( prefix + rateNS + " " + result.getUnits() + "/ns  [maxJitter="+maxJitterMillis+"ms totalTestRun="+durationMillis+"ms]" );
            if ( rateNS < 10.0 ) {
                System.out.println( String.format("%s%.2f %s/ms  [maxJitter=%.3fms totalTestRun=%.3fms]", prefix,rateMS,result.getUnits(),maxJitterMillis,durationMillis) );
            } else {
                System.out.println( String.format("%s%.2f %s/ns  [maxJitter=%.3fms totalTestRun=%.3fms]", prefix,rateNS,result.getUnits(),maxJitterMillis,durationMillis) );
            }

        } catch ( Throwable ex ) {
            ex.printStackTrace();
        }
    }

    private static enum RunMode {
        FirstRun(true), WramUp(true), NormalRun(false), LastRun(false);

        public final boolean isWarmUp;

        private RunMode( boolean isWarmUp ) {
            this.isWarmUp = isWarmUp;
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
