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
package message;

import coding.COBSReader;
import coding.COBSWriter;
import comm.StreamComm;

/**
 * An implementation of a Messenger using a StreamComm stream communication channel
 * with data encoded and decoded using the consistent overhead byte stuffing (COBS)
 * protocol
 * 
 * @author Andrew_2
 */
public class COBSMessenger extends BlockToStreamMessenger {

    private COBSReader reader;
    private COBSWriter writer;
    
    public static final int maxMessageLen = 254;
    public static final int readerBufferSize = 1024;
    
    private COBSMessenger(COBSReader reader, COBSWriter writer, StreamComm streamComm) {
        super(writer, writer, reader, reader, streamComm);
        this.reader = reader;
        this.writer = writer;
    }
    
    /**
     * Create a COBSMessenger over the given StreamComm
     * 
     * @param streamComm the StreamComm to be used by the created COBSMessenger
     * @return 
     */
    public static COBSMessenger createCOBSMessenger(StreamComm streamComm) {
        
        COBSReader reader = new COBSReader(readerBufferSize, maxMessageLen);
        COBSWriter writer = new COBSWriter(maxMessageLen);
        
        return new COBSMessenger(reader, writer, streamComm);
        
    }
    
}
