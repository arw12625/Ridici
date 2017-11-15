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

import comm.BlockComm.BlockReceivedCallback;
import util.ByteBufferOutput;
import util.ByteBufferInput;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * An implementation of a StreamComm stream communication channel wrapping a
 * provided BlockComm communication channel
 *
 * Allows a BlockComm to be used as a StreamComm. Uses ByteBufferInput and
 * ByteBufferOutput to handle conversion from blocks to streams
 *
 * @author Andrew_2
 */
public class StreamOverBlockComm implements StreamComm {

    private BlockComm blockComm;

    private ByteBufferInput bin;
    private ByteBufferOutput bout;

    /**
     * Construct the StreamComm by wrapping the given BlockComm
     *
     * @param comm the BlockComm to be wrapped
     */
    public StreamOverBlockComm(BlockComm comm) {

        this.blockComm = comm;
        bin = new ByteBufferInput();
        bout = new ByteBufferOutput();
        bout.setCallback(new ByteBufferOutput.ByteBufferOutputCallback() {

            @Override
            public void available() {
                readStreamToBlock();
            }
        });
        comm.setBlockReceivedCallback(new BlockReceivedCallback() {
            @Override
            public void onBlockReceived(BlockComm comm, ByteBuffer block) {
                writeBlockToStream(block);
            }
        });
    }

    @Override
    public InputStream getInputStream() {
        return bin.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() {
        return bout.getOutputStream();
    }

    @Override
    public boolean connect() {
        return blockComm.connect();
    }

    @Override
    public boolean disconnect() {
        return blockComm.disconnect();

    }

    @Override
    public boolean isConnected() {
        return blockComm.isConnected();
    }

    /**
     * Internal function for writing input block to stream
     *
     * @param block
     */
    private void writeBlockToStream(ByteBuffer block) {
        byte[] blockArray = new byte[block.remaining()];
        block.get(blockArray);

        bin.write(blockArray);
    }

    /**
     * Internal function for writing data from stream into block
     */
    private void readStreamToBlock() {
        if (bout.available() > 0) {
            byte[] arr = new byte[bout.available()];
            int len = bout.read(arr);
            ByteBuffer block = ByteBuffer.allocate(len);
            block.put(arr, 0, len);
            block.flip();
            blockComm.writeBlock(block);
        }
    }

}
