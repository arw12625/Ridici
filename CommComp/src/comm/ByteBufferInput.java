/*
 * The MIT License
 *
 * Copyright 2017 Andrew_2.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package comm;

import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * A class providing methods for writing to a buffer as well as an 
 * InputStream view of the buffer for reading. The buffer can only be written
 * through this class, and the buffer can be only read through the
 * InputStream view.
 * 
 * @author Andrew_2
 */
public class ByteBufferInput {

    /**
     * The underlying buffer containing the data
     */
    private final ByteBuffer buffer;
    /**
     * The read-only InputStream view of the buffer
     */
    private ByteBufferInputStream stream;

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    /**
     * Creates a buffer with the default size
     */
    public ByteBufferInput() {
        this(DEFAULT_BUFFER_SIZE);
    }
    
    /**
     * Creates a buffer with specified buffer size
     * 
     * @param bufferSize the size of the buffer to be created
     */
    public ByteBufferInput(int bufferSize) {
        buffer = ByteBuffer.allocate(bufferSize);
        buffer.limit(0);
        stream = new ByteBufferInputStream();
    }
    
    /**
     * Write a single byte to the buffer
     * 
     * @param b the byte to be written
     * @return whether the write was successful
     */
    synchronized public boolean write(int b) {
        //silly, I know
        byte[] data = {(byte)b};
        return write(data);
    }

    /**
     * Write an array of bytes to the buffer
     * 
     * @param data the bytes to be written
     * @return whether the write was successful
     */
    synchronized public boolean write(byte[] data) {
        boolean success = true;
        if (data.length > buffer.capacity()) {
            System.err.println("Data length exceeded buffer capacity in ByteBufferInput");
            buffer.clear();
            //put the last section of data that will fit into the buffer
            buffer.put(data, data.length - buffer.capacity(), buffer.capacity());
            success = false;
        } else {
            if (buffer.capacity() - buffer.limit() < data.length) {
                //write will exceed buffer capacity so shift data if possible
                if (buffer.capacity() - buffer.limit() + buffer.position() < data.length) {
                    System.err.println("Uh oh. ByteBufferInput Overflow.");
                    buffer.position(-buffer.capacity() + data.length + buffer.limit());
                    success = false;
                }
                buffer.compact();
                buffer.flip();
            }
            buffer.mark();
            buffer.position(buffer.limit());
            buffer.limit(buffer.limit() + data.length);
            buffer.put(data);
            buffer.reset();
        }
        return success;
    }
    
    /**
     * Read a single byte from the buffer
     * Must be accessed through the InputStream view for external reading
     * 
     * @return the byte read
     */
    synchronized private int read() {
        return buffer.get();
    }
    
    /**
     * Read an array of bytes from the byte
     * Must be accessed through the InputStream view for external reading
     * 
     * @param arr the array to read the buffer data into
     * @return the number of bytes read
     */
    synchronized private int read(byte[] arr) {
        int num = Math.min(arr.length, buffer.remaining());
        buffer.get(arr, 0, num);
        return num;
    }
    
    /**
     * Returns the number of bytes in the buffer available to be read
     * 
     * @return the number of available bytes
     */
    synchronized private int available() {
        return buffer.remaining();
    }

    /**
     * Get the InputStream view of the buffer
     * 
     * @return the InputStream view of the buffer
     */
    public InputStream getInputStream() {
        return stream;
    }

    /**
     * The InputStream view of the buffer
     * Only provides read functionality
     */
    private class ByteBufferInputStream extends InputStream {

        private ByteBufferInputStream() {            
        }

        @Override
        public int read() {
            return ByteBufferInput.this.read();
        }
        
        @Override
        public int read(byte[] arr) {
            return ByteBufferInput.this.read(arr);
        }

        @Override
        public int available() {
            return ByteBufferInput.this.available();
        }

    }
}
