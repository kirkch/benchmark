package com.mosaic.benchmark.io;

import com.mosaic.benchmark.Benchmark;
import com.mosaic.benchmark.BenchmarkSizing;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 */
public class RandomAccessFileArrayBatchRead extends Benchmark {

    private int fileSizeBytes;
    private int bufSize;
    private byte[] buf;
    private File             file;
    private RandomAccessFile raFile;

    public RandomAccessFileArrayBatchRead( int cacheSize ) {
        super(
            "bytes", // units
            BenchmarkSizing.LocalDisk,
            "How fast is reading from RandomAccessFile fetching into a byte array?", // question
            "Loops sequentially over every byte received from the file, one byte at a time, summing its value so that the read is not optimised out" // description
        );

        this.bufSize = cacheSize;

        this.fileSizeBytes = getBenchmarkSizing().numOpsPerTest;
    }

//  128   120670.93
//  258   221434.90
//  512   358680.06
// 1024   546448.09
// 4068   887311.45
// 8128   912408.76 bytes/ms
//16384   786782.06

    @Override
    public void setUp() throws Throwable {
        super.setUp();

        file = File.createTempFile( getClass().getSimpleName(), ".dat" );
        raFile = new RandomAccessFile( file, "rw" );

        raFile.setLength( fileSizeBytes );

        buf = new byte[bufSize];
    }

    @Override
    public void tearDown() throws IOException {
        raFile.close();
        file.delete();
        file   = null;
        raFile = null;
        buf    = null;
    }

    @Override
    protected long runSingleIteration() throws IOException {
        raFile.seek(0);

        long sum = 0;

        int b;
        do {
            b = raFile.read(buf);

            for ( int i=0; i<bufSize; i++ ) {
                sum += buf[i];
            }
        } while ( b >= 0 );

        return sum;
    }

    @Override
    public String getName() {
        return super.getName() + " " + bufSize;
    }
}
