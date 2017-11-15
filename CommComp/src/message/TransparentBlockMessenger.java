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

import comm.BlockComm;
import java.nio.ByteBuffer;

/**
 * An implementation of a Messenger using a BlockComm block communication channel.
 * 
 * Data blocks are written to and from the communication channel transparently,
 * that is with no modification or coding
 * 
 * @author Andrew_2
 */
public class TransparentBlockMessenger implements Messenger {

    private BlockComm comm;
    
    /**
     * Construct the TransparentBlockMessenger using the given BlockComm
     * @param comm the BlockComm the messenger will communicate over
     */
    public TransparentBlockMessenger(BlockComm comm) {
        this.comm = comm;
    }
    
    @Override
    public boolean sendMessage(ByteBuffer msg) {
        comm.writeBlock(msg);
        return true;
    }

    @Override
    public void setMessageReceivedCallback(MessageReceivedCallback callback) {
        comm.setBlockReceivedCallback(new BlockComm.BlockReceivedCallback() {
            @Override
            public void onBlockReceived(BlockComm comm, ByteBuffer block) {
                callback.onMessageReceived(TransparentBlockMessenger.this, block);
            }
        });
    }

    @Override
    public boolean connect() {
        return comm.connect();
    }

    @Override
    public boolean disconnect() {
        return comm.disconnect();
    }

    @Override
    public boolean isConnected() {
        return comm.isConnected();
    }
    
}
