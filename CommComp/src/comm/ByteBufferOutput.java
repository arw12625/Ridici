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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * A class providing methods for reading from a buffer as well as an 
 * OutputStream view of the buffer for writing. The buffer can only be read 
 * through this class, and the buffer can be only written through the
 * OutputStream view.
 * 
 * @author Andrew_2
 */
public class ByteBufferOutput {
    
    /**
     * The underlying buffer containing the data
     */
    private final ByteBuffer buffer;
    /**
     * The read-only InputStream view of the buffer
     */
    private ByteBufferOutputStream stream;
    /**
     * Callback when data is available to be read
     */
    private ByteBufferOutputCallback callback;

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    /**
     * Construct a buffer with default buffer size
     */
    public ByteBufferOutput() {
        this(DEFAULT_BUFFER_SIZE);
    }
    
    /**
     * Construct buffer with specified buffer size
     * 
     * @param bufferSize the size of the buffer to be constructed
     */
    public ByteBufferOutput(int bufferSize) {
        buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
        buffer.limit(0);
        stream = new ByteBufferOutputStream();
    }
    
    /**
     * Set the callback for when data is available to be read
     * 
     * @param callback the callback for when data is available to be read
     */
    public void setCallback(ByteBufferOutputCallback callback) {
        this.callback = callback;
    }
    
    /**
     * Write a single byte to the buffer
     * 
     * @param b the byte to be written
     * @return whether the write was successful
     */
    synchronized private boolean write(int b) {
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
    synchronized private boolean write(byte[] data) {
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
                    System.err.println("Uh oh. ByteBufferOutput Overflow.");
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
     * Return the number of available bytes for reading in the buffer
     * 
     * @return the number of available bytes
     */
    synchronized public int available() {
        return buffer.remaining();
    }
    
    /**
     * Read a single byte from the buffer 
     * 
     * @return the byte read
     */
    synchronized public int read() {
        return buffer.get();
    }
    
    /**
     * Read an array of bytes from the byte
     * Must be accessed through the InputStream view for external reading
     * 
     * @param arr the array to read the buffer data into
     * @return the number of bytes read
     */
    synchronized public int read(byte[] arr) {
        int num = Math.min(arr.length, buffer.remaining());
        buffer.get(arr, 0, num);
        return num;
    }
    
    /**
     * Notify the callback that data is available for reading
     */
    synchronized public void flush() {
        if(callback != null) {
            callback.available();
        }
    }
    
    /**
     * Get the OutputStream view of the buffer
     * @return the OutputStream view of the buffer
     */
    public OutputStream getOutputStream() {
        return stream;
    }
    
    /**
     * The OutputStream view of the buffer
     * Only provides write functionality
     */
    private class ByteBufferOutputStream extends OutputStream {

        private ByteBufferOutputStream() {

        }

        @Override
        public void write(int b) {
            ByteBufferOutput.this.write(b);
        }
        
        @Override
        public void write(byte[] data) {
            ByteBufferOutput.this.write(data);
        }
        
        @Override
        public void flush() {
            ByteBufferOutputStream.this.flush();
        }

    }
    
    /**
     * A callback for when buffer data is available to be read
     */
    public static interface ByteBufferOutputCallback {
        
        /**
         * Callback to indicate that buffer data is available to be read
         */
        public void available();
        
    }

}
