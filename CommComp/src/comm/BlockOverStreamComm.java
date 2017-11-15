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

import util.DefaultThread;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * An implementation of a BlockComm block communication channel wrapping 
 * a provided StreamComm communication channel
 * 
 * Allows a StreamComm to be used as a BlockComm.
 * Spawns a thread to read data from the stream to write into blocks
 *
 * @author Andrew_2
 */
public class BlockOverStreamComm implements BlockComm {

    private StreamComm streamComm;
    private DefaultThread readThread;
    private BlockReceivedCallback readCallback;
    
    public static final int INPUT_BUFFER_SIZE = 256;
    
    /**
     * Construct the BlockComm by wrapping the given StreamComm
     * 
     * @param streamComm the StreamComm to be wrapped
     */
    public BlockOverStreamComm(StreamComm streamComm) {
        this.streamComm = streamComm;
        readThread = new DefaultThread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream in = streamComm.getInputStream();
                    if(in.available() > 0) {
                        byte inBuf[] = new byte[INPUT_BUFFER_SIZE];
                        int numRead = in.read(inBuf);
                        readCallback.onBlockReceived(BlockOverStreamComm.this, ByteBuffer.wrap(inBuf, 0, numRead));
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        
    }
    
    @Override
    public void setBlockReceivedCallback(BlockReceivedCallback callback) {
        this.readCallback = callback;
    }

    @Override
    public void writeBlock(ByteBuffer block) {
        byte blockArray[] = new byte[block.remaining()];
        block.get(blockArray);
        try {
            streamComm.getOutputStream().write(blockArray);
            streamComm.getOutputStream().flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean connect() {
        boolean success = streamComm.connect();
        readThread.start();
        return success;
    }

    @Override
    public boolean disconnect() {
        boolean success = streamComm.disconnect();
        readThread.stop();
        return success;
    }

    @Override
    public boolean isConnected() {
        return streamComm.isConnected();
    }
    
}
