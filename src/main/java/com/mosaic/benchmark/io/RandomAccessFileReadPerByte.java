package com.mosaic.benchmark.io;

import com.mosaic.benchmark.Benchmark;
import com.mosaic.benchmark.BenchmarkSizing;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 */
public class RandomAccessFileReadPerByte extends Benchmark {

    private int fileSizeBytes;
    private File             file;
    private RandomAccessFile raFile;

    public RandomAccessFileReadPerByte() {
        super(
            "bytes", // units
            BenchmarkSizing.LocalDisk,
            "How fast is reading from RandomAccessFile one byte at a time?", // question
            "Loops sequentially over every byte received from the file, one byte at a time, summing its value so that the read is not optimised out" // description
        );

        this.fileSizeBytes = getBenchmarkSizing().numOpsPerTest;
    }

//1171.73 bytes/ms

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
    protected long runSingleIteration() throws IOException {
        long sum = 0;

        raFile.seek(0);
        int b;
        do {
            b = raFile.read();

            sum += b;
        } while ( b >= 0 );

        return sum;
    }

}
