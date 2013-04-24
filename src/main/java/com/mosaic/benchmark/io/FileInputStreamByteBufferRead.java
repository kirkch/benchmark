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
public class FileInputStreamByteBufferRead extends Benchmark {

    private int              fileSizeBytes;
    private int              bufSize;
    private ByteBuffer       buf;
    private File             file;
    private RandomAccessFile raFile;

    public FileInputStreamByteBufferRead( int cacheSize ) {
        super(
            "bytes", // units
            BenchmarkSizing.LocalDisk,
            "How fast is reading from InputFileStream fetching into a byte buffer?", // question
            "Loops sequentially over every byte received from the stream, one byte at a time, summing its value so that the read is not optimised out" // description
        );

        this.bufSize = cacheSize;

        this.fileSizeBytes = getBenchmarkSizing().numOpsPerTest;
    }
//  128    118287.20
//  258    168548.79
//  512    311720.70
// 1024    536193.03
// 4068    897666.07       (<--- when disk cache is full ... 173792.14 bytes/ms when its not)  or is it 352733.69 :)
// 8128   1030927.84 bytes/ms
//16384   1094091.90

    @Override
    public void setUp() throws Throwable {
        super.setUp();

        file = File.createTempFile( getClass().getSimpleName(), ".dat" );
        raFile = new RandomAccessFile( file, "rw" );

        raFile.setLength( fileSizeBytes );

        buf = ByteBuffer.allocate(bufSize);
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
