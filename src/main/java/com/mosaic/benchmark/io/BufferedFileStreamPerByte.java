package com.mosaic.benchmark.io;

import com.mosaic.benchmark.Benchmark;
import com.mosaic.benchmark.BenchmarkSizing;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 *
 */
public class BufferedFileStreamPerByte extends Benchmark {

    private int fileSizeBytes;
    private File             file;
    private RandomAccessFile raFile;

// 136072.94 bytes/ms

    public BufferedFileStreamPerByte() {
        super(
            "bytes", // units
            BenchmarkSizing.LocalDisk,
            "How fast is reading from BufferedInputStream(InputFileStream) one byte at a time?", // question
            "Loops sequentially over every byte received from the stream, one byte at a time, summing its value so that the read is not optimised out" // description
        );

        this.fileSizeBytes = getBenchmarkSizing().numOpsPerTest;
    }

    @Override
    public void setUp() throws Throwable {
        super.setUp();

        file = File.createTempFile( getClass().getSimpleName(), ".dat" );
        raFile = new RandomAccessFile( file, "rw" );

        raFile.setLength( fileSizeBytes );
    }

    @Override
    public void tearDown() {
        file.delete();
        file   = null;
        raFile = null;
    }

    @Override
    protected long runSingleIteration() throws IOException {
        InputStream in = new BufferedInputStream( new FileInputStream(file) );

        long sum = 0;

        int b;
        do {
            b = in.read();

            sum += b;
        } while ( b >= 0 );

        return sum;
    }

}
