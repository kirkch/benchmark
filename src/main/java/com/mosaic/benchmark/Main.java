package com.mosaic.benchmark;

import com.mosaic.benchmark.datastructures.array.ByteArrayRead;
import com.mosaic.benchmark.datastructures.array.ByteArrayWrite;
import com.mosaic.benchmark.datastructures.array.CharArrayRead;
import com.mosaic.benchmark.datastructures.array.CharArrayToCharArrayCopyThenRead;
import com.mosaic.benchmark.datastructures.buffers.*;
import com.mosaic.benchmark.io.FileInputStreamArrayBatchRead;
import com.mosaic.benchmark.io.FileInputStreamByteBufferRead;

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
                                 // super + freezer
    private static List<Benchmark> benchmarks = Arrays.<Benchmark>asList(
//        new ByteArrayRead(),    // 3.4 /ns
//        new ByteBufferRead(),     // 2.9 /ns
//        new CharArrayRead(),     // 3.4 /ns
//        new CharBufferRead(),     // 3.4 /ns
        new CharBufferToCharArrayCopyThenRead(),    // 2.5ns
        new CharArrayToCharArrayCopyThenRead()      // 2.5ns
//        new DirectByteBufferRead(numItterations),     // 1.55
//
//        new ByteArrayWrite(),         // 12.3   34.5
//        new ByteBufferWrite(),        // 13.5   18.5
//        new DirectByteBufferWrite()   // 1.5     2.3

//        new FileInputStreamArrayBatchRead(4068),
//        new FileInputStreamArrayBatchRead(4068),
//        new FileInputStreamByteBufferRead(4068),
//        new FileInputStreamByteBufferRead(4068)

//        new FileInputStreamReadPerByte(),
//        new RandomAccessFileReadPerByte()
//        new BufferedFileStreamPerByte()
//        new FileInputStreamArrayBatchRead(128),
//        new FileInputStreamArrayBatchRead(258),
//        new FileInputStreamArrayBatchRead(512),
//        new FileInputStreamArrayBatchRead(1024),
//        new FileInputStreamArrayBatchRead(4068)
//        new FileInputStreamArrayBatchRead(8128),
//        new FileInputStreamArrayBatchRead(16384)
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

//Thread.sleep(60000);
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


            String isWarmUpStr = runMode.isWarmUp ? "WARMUP " : "";
            String prefix = "["+isWarmUpStr + b.getName()+"]: ";

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
