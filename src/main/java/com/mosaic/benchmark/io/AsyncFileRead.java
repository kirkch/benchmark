package com.mosaic.benchmark.io;

import com.mosaic.benchmark.Benchmark;
import com.mosaic.benchmark.BenchmarkSizing;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 */
public class AsyncFileRead extends Benchmark {

    private int fileSizeBytes;
    private int bufSize;
    private int maxConcurrentReaders;
    private File             file;
    private RandomAccessFile raFile;
    private Semaphore        concurrentReadSemaphore;

    public AsyncFileRead( int cacheSize, int maxConcurrentReaders ) {
        super(
            "bytes", // units
            BenchmarkSizing.LocalDisk,
            "How fast is reading from RandomAccessFile fetching into a byte array?", // question
            "Loops sequentially over every byte received from the file, one byte at a time, summing its value so that the read is not optimised out" // description
        );

        this.bufSize = cacheSize;
        this.maxConcurrentReaders = maxConcurrentReaders;

        this.fileSizeBytes = getBenchmarkSizing().numOpsPerTest;

        this.concurrentReadSemaphore = new Semaphore(maxConcurrentReaders);
    }

// conclusion: slow  (1.7 and 1.8 (ea 2013))
//   upon investigation the JRE has implemented the interface for async reads but not carried it through down to the metel.
//   instead it wraps streams and uses a thread pool. Thus adding the thread over heads without improving the scaling.

//  128   7840.37  (and high jitter)           19100.74       19251.13
//  256   14644.50                             27455.18       37352.46     (compared to 221434.90)
//  512                                                                    (358680.06)
// 1024                                                                    (546448.09)
// 4068                                                                    (887311.45)
// 8128   125,596.58                           199560.97      332,005.31   (912,408.76) bytes/ms
//16384                                                                    (786782.06)

    @Override
    public void setUp() throws Throwable {
        super.setUp();

        file = File.createTempFile( getClass().getSimpleName(), ".dat" );
        raFile = new RandomAccessFile( file, "rw" );

        raFile.setLength( fileSizeBytes );
    }

    @Override
    public void tearDown() throws IOException {
        raFile.close();
        file.delete();
        file   = null;
        raFile = null;
    }

    @Override
    protected long runSingleIteration() throws IOException, InterruptedException {
        Path p = Paths.get( file.getAbsolutePath() );
        AsynchronousFileChannel ch = AsynchronousFileChannel.open( p );

        final AtomicLong totalSum = new AtomicLong(0);

        int b = 0;
        do {
            concurrentReadSemaphore.acquire();

            final ByteBuffer buf = ByteBuffer.allocate(bufSize);

            ch.read( buf, b, null, new CompletionHandler<Integer,Object>() {
                @Override
                public void completed( Integer result, Object attachment ) {
                    concurrentReadSemaphore.release();

                    long localSum     = 0;
                    int  numBytesRead = buf.position();

                    for ( int i=0; i<numBytesRead; i++ ) {
                        localSum += buf.get(i);
                    }

                    totalSum.addAndGet( localSum );
                }

                @Override
                public void failed( Throwable exc, Object attachment ) {
                    concurrentReadSemaphore.release();

                    exc.printStackTrace();
                }
            } );

            b += bufSize;
        } while ( b < fileSizeBytes );

        return totalSum.get();
    }

    @Override
    public String getName() {
        return super.getName() + " " + bufSize + " " + maxConcurrentReaders;
    }
}
