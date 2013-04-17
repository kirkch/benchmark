package com.mosaic.benchmark.io;

import com.mosaic.benchmark.Benchmark;
import com.mosaic.benchmark.BenchmarkSizing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 */
public class FileInputStreamDirectByteBufferRead extends Benchmark {

    private int              fileSizeBytes;
    private int              bufSize;
    private ByteBuffer       buf;
    private File             file;
    private RandomAccessFile raFile;

    public FileInputStreamDirectByteBufferRead( int cacheSize ) {
        super(
            "bytes", // units
            BenchmarkSizing.LocalDisk,
            "How fast is reading from InputFileStream fetching into a byte buffer?", // question
            "Loops sequentially over every byte received from the stream, one byte at a time, summing its value so that the read is not optimised out" // description
        );

        this.bufSize = cacheSize;

        this.fileSizeBytes = getBenchmarkSizing().numOpsPerTest;
    }
//  128    130701.87
//  258    230786.98
//  512    355745.29
// 1024    524934.38
// 4068    798084.60
// 8128    904159.13 bytes/ms
//16384    956937.80

    @Override
    public void setUp() throws Throwable {
        super.setUp();

        file = File.createTempFile( getClass().getSimpleName(), ".dat" );
        raFile = new RandomAccessFile( file, "rw" );

        raFile.setLength( fileSizeBytes );

        buf = ByteBuffer.allocateDirect(bufSize);
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
        FileInputStream in      = new FileInputStream( file );
        FileChannel     channel = in.getChannel();

        long sum = 0;
        int b;
        do {
            b = channel.read( buf );

            for ( int i=0; i<b; i++ ) {
                sum += buf.get( i );
            }

            buf.clear();
        } while ( b >= 0 );

        return sum;
    }

    @Override
    public String getName() {
        return super.getName() + " " + bufSize;
    }
}
