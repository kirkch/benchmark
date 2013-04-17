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
public class BufferedFileStreamArrayBatchRead extends Benchmark {

    private int fileSizeBytes;
    private int bufSize;
    private byte[] buf;
    private File             file;
    private RandomAccessFile raFile;

    public BufferedFileStreamArrayBatchRead( int cacheSize ) {
        super(
            "bytes", // units
            BenchmarkSizing.LocalDisk,
            "How fast is reading from BufferedInputStream(InputFileStream) in batches?", // question
            "Loops sequentially over every byte received from the stream, one byte at a time, summing its value so that the read is not optimised out" // description
        );

        this.bufSize = cacheSize;

        this.fileSizeBytes = getBenchmarkSizing().numOpsPerTest;
    }

// 128    809716.60
// 258    709219.86
// 512    878734.62
//1024    889679.72
//4068    699300.70
//8128    672494.96
//16384   911577.03   bytes/ms

    @Override
    public void setUp() throws Throwable {
        super.setUp();

        file = File.createTempFile( getClass().getSimpleName(), ".dat" );
        raFile = new RandomAccessFile( file, "rw" );

        raFile.setLength( fileSizeBytes );

        buf = new byte[bufSize];
    }

    @Override
    public void tearDown() {
        file.delete();
        file   = null;
        raFile = null;
        buf    = null;
    }

    @Override
    protected long runSingleIteration() throws IOException {
        InputStream in = new BufferedInputStream( new FileInputStream(file) );

        long sum = 0;

        int b;
        do {
            b = in.read(buf);

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
