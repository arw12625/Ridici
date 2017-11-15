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
package coding;

import util.BlockInput;
import util.StreamOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * A class that encodes messages as a block with consistent overhead 
 * byte stuffing(COBS) and writes the encoded data to an OutputStream
 *
 * @author Andrew_2
 */
public class COBSWriter implements BlockInput, StreamOutput {

    private OutputStream out;
    private byte[] rawStuffed;
    private ByteBuffer stuffed;

    /**
     * Constructs a writer with the appropriate parameters
     *
     * @param unstuffedMessageLength the maximum unstuffed message length
     */
    public COBSWriter(int unstuffedMessageLength) {
        rawStuffed = new byte[unstuffedMessageLength + 2];
        stuffed = ByteBuffer.wrap(rawStuffed);
    }

    /**
     * Set the OutputStream where stuffed data is output
     * 
     * @param out the OutputStream to write stuffed data to
     */
    @Override
    public void setOutputStream(OutputStream out) {
        this.out = out;
    }

    /**
     * Write a message to the writer
     *
     * @param unstuffed the message to be written
     * @return whether the write was successful or not
     */
    @Override
    public synchronized boolean writeBlock(ByteBuffer unstuffed) {
        boolean success = stuffBytes(unstuffed, stuffed);
        if (success) {
            try {
                out.write(rawStuffed, 0, stuffed.remaining());
                out.flush();
            } catch (IOException ex) {
                success = false;
                ex.printStackTrace();
            }
        }
        return success;
    }

    /**
     * Stuff or enocde a message with COBS
     *
     * @param source the buffer containing the message to be encoded
     * @param dest the buffer to write the result to
     * @return whether the encoding was successful
     */
    public static boolean stuffBytes(ByteBuffer source, ByteBuffer dest) {
        dest.clear();
        int length = source.remaining();
        if (length > dest.remaining()) {
            System.err.println("Source length greater than dest length");
            return false;
        }
        if (254 < length) {
            System.err.println("Source length greater than 254 not suppported");
            return false;
        }
        if (length == 0) {
            System.err.println("Cannot stuff Empty Source");
            return false;
        }

        dest.put((byte) 0);
        int index = 0;
        int codeIndex = 0;
        byte current;
        while (index++ < length) {
            current = source.get();
            if (current == 0) {
                dest.put(codeIndex, (byte) (index - codeIndex));
                dest.put((byte) 0);
                codeIndex = index;
            } else {
                dest.put(current);
            }
        }
        dest.put(codeIndex, (byte) (index - codeIndex));
        dest.put((byte) 0);

        dest.flip();

        return true;

    }

}
