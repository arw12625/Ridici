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
 * A BlockComm block communication channel whose input and ouput is piped to
 * another instance of PipedBlockComm.
 * 
 * PipedBlockComm is used to create a pair of linked block communication channels
 * within the machine. This can be used to communicate between applications on
 * the host machine or emulate a physical device's communication channel.
 * 
 * @author Andrew_2
 */
public class PipedBlockComm implements BlockComm {

    private PipedBlockComm target;
    private BlockReceivedCallback callback;
    private boolean connected;
    
    /**
     * Set the target PipedBlockComm to pipe input and output to
     * 
     * Note that this method pipes output from the target to this instance and
     * output from this instance to the target. Hence it should only be called
     * once for pair of PipedBlockComm instances.
     * 
     * @param target the target to be set
     */
    public void setTarget(PipedBlockComm target) {
        this.setTargetNoRecurse(target);
        target.setTargetNoRecurse(this);
    }
    
    /**
     * Set the PiepedBlockComm target of only this instance.
     * 
     * Does not set the target of the target to this. See {@link #setTarget(PipedBlockComm)}
     * 
     * @param target the target to be set
     */
    private void setTargetNoRecurse(PipedBlockComm target)  {
        this.target = target;
        connected = connected && target != null;
    }
    
    @Override
    public void setBlockReceivedCallback(BlockReceivedCallback callback) {
        this.callback = callback;
    }

    @Override
    public void writeBlock(ByteBuffer block) {
        if(connected) {
            target.writeBlockFromTarget(block);
        }
    }
    
    /**
     * Writes block from source PipedBlockComm to this PipedBlockComm's callback
     * @param block 
     */
    private void writeBlockFromTarget(ByteBuffer block) {
        if(callback != null) {
            callback.onBlockReceived(this, block);
        }
    }

    @Override
    public boolean connect() {
        connected = target != null;
        return connected;
    }

    @Override
    public boolean disconnect() {
        connected = false;
        return true;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }
}
