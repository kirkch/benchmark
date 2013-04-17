package com.mosaic.benchmark.io;

import com.mosaic.benchmark.Benchmark;
import com.mosaic.benchmark.BenchmarkSizing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 */
public class FileInputStreamArrayBatchRead extends Benchmark {

    private int fileSizeBytes;
    private int bufSize;
    private byte[] buf;
    private File             file;
    private RandomAccessFile raFile;

    public FileInputStreamArrayBatchRead( int cacheSize ) {
        super(
            "bytes", // units
            BenchmarkSizing.LocalDisk,
            "How fast is reading from InputFileStream fetching into a byte array?", // question
            "Loops sequentially over every byte received from the stream, one byte at a time, summing its value so that the read is not optimised out" // description
        );

        this.bufSize = cacheSize;

        this.fileSizeBytes = getBenchmarkSizing().numOpsPerTest;
    }
//  128    121817.52
//  258    223813.79
//  512    383877.16
// 1024    607164.54
// 4068    933706.82
// 8128   1067235.86 bytes/ms
//16384   1022494.89


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
        FileInputStream in = new FileInputStream( file );

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
