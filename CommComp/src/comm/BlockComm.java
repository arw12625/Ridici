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

import java.nio.ByteBuffer;

/**
 * An abstraction of a communication channel that transfers data in blocks.
 * A block of data is represented as a ByteBuffer
 * 
 * @author Andrew_2
 */
public interface BlockComm extends Comm {
    
    /**
     * Set the callback for when a block of data is received through the 
     * communication channel.
     * 
     * @param callback The callback for when a block of data is received
     */
    public void setBlockReceivedCallback(BlockReceivedCallback callback);
    
    /**
     * Write a block of data to the communication channel
     * 
     * @param block The block of data to be written
     */
    public void writeBlock(ByteBuffer block);
    
    /**
     * The callback to be used when blocks are received by the communication channel
     */
    public interface BlockReceivedCallback {
        
        /**
         * Called when the specified block communication channel receives a block
         * @param comm the block communication channel that received the block
         * @param block the block received
         */
        public void onBlockReceived(BlockComm comm, ByteBuffer block);
    
    }
    
}
