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
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * A class that reads an InputStream of data encoded with consistent overhead
 * byte stuffing(COBS), decodes the data, and passes the result to a callback for 
 * parsing.
 * 
 * @author Andrew_2
 */
public class COBSReader {

    private InputStream in;
    private byte[] rawBuffer;
    private ByteBuffer buffer, stuffed, unstuffed;
    private int bufferSize, unstuffedLength;
    private boolean validMessage = true;

    /**
     * the callback for when a message is unstuffed
     */
    private BufferReaderCallback parser;

    /**
     * Construct a reader with the appropriate parameters
     * @param in the InputStream to read from
     * @param bufferSize the size of the internal buffer
     * @param unstuffedLength the max size of an unstuffed message
     * @param parser the callback for unstuffed messages
     */
    public COBSReader(InputStream in, int bufferSize, int unstuffedLength, BufferReaderCallback parser) {

        this.in = in;
        this.bufferSize = bufferSize;
        this.unstuffedLength = unstuffedLength;
        this.parser = parser;
        rawBuffer = new byte[bufferSize];
        buffer = ByteBuffer.wrap(rawBuffer);
        stuffed = ByteBuffer.allocate(unstuffedLength + 2);
        unstuffed = ByteBuffer.allocate(unstuffedLength);
    }

    /**
     * Read data from the InputStream, decode, and pass to callback
     * @throws IOException 
     */
    public void read() throws IOException {
        //add timeout later

        if (in.available() > 0) {
            int numRead = in.read(rawBuffer);
            buffer.clear();
            buffer.limit(numRead);
            while (buffer.hasRemaining()) {
                byte current = buffer.get();
                //System.out.println(0xFF & current);

                //System.out.println(current);
                if (stuffed.hasRemaining()) {
                    stuffed.put(current);
                } else {
                    System.err.println("Message length exceeded in reader.");
                    validMessage = false;
                }

                if (current == 0) {
                    if (validMessage) {
                        stuffed.flip();
                        if (!unstuffBytes(stuffed, unstuffed)) {
                            System.err.println("Invalid Message. Message could not be unstuffed");
                        } else {
                            //Utility.printBytes(unstuffed);
                            
                            parser.readBuffer(unstuffed);
                        }
                    }
                    validMessage = true;
                    stuffed.clear();
                    unstuffed.clear();

                }

            }
        }

    }

    /**
     * Unstuff or decode a message encoded with consistent overhead byte stuffing
     * 
     * @param source the buffer containing the stuffed message
     * @param dest the buffer to write the unstuffed message to
     * 
     * @return whether the decoding was successful
     */
    public static boolean unstuffBytes(ByteBuffer source, ByteBuffer dest) {
        int length = source.remaining();

        if (dest.remaining() + 2 < length) {
            System.err.println("Source length greater than dest length.");
            return false;
        }
        if (256 < length) {
            System.err.println("Source length greater than 256 not suppported");
            return false;
        }
        if (2 >= length) {
            System.err.println("Empty source");
            return false;
        }

        try {
            int index = 0;
            while (index < length - 1) {
                int stop = (source.get() & 0xFF) - 1 + index;
                while (index++ < stop) {
                    dest.put(source.get());
                }
                if (index + 1 != length) {
                    dest.put((byte) 0);
                }
            }
        } catch (java.nio.BufferUnderflowException e) {
            e.printStackTrace();
        }

        dest.flip();

        return true;
    }

}
