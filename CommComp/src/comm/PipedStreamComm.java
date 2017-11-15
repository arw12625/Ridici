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
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * A StreamComm stream communication channel whose InputStream and OutputStream
 * is piped to another instance of PipedStreamComm.
 * 
 * PipedStreamComm is used to create a pair of linked stream communication channels
 * within the machine. This can be used to communicate between applications on
 * the host machine or emulate a physical device's communication channel.
 * 
 * @author Andrew_2
 */
public class PipedStreamComm implements StreamComm {

    private PipedStreamComm target;
    private PipedInputStream input;
    private PipedOutputStream output;
    private boolean connected;
    
    /**
     * Default Constructor
     */
    public PipedStreamComm() {
        input = new PipedInputStream();
        output = new PipedOutputStream();
    }
    
    /**
     * Set the target PipedStreamComm to pipe input and output streams to
     * 
     * Note that this method pipes output from the target to this instance and
     * output from this instance to the target. Hence it should only be called
     * once for pair of PipedStreamComm instances.
     * 
     * @param target the target to be set
     */
    public void setTarget(PipedStreamComm target) {
        try {
            this.setTargetNoRecurse(target);
            target.setTargetNoRecurse(this);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Set the PipedStreamComm target of only this instance.
     * 
     * Does not set the target of the target to this. See {@link #setTarget(PipedStreamComm)}
     * 
     * @param target the target to be set
     */
    private void setTargetNoRecurse(PipedStreamComm target) throws IOException {
        connected = connected && target != null;
        this.target = target;
        if(target != null) {
            input.connect(target.output);
        }
    }
    
    @Override
    public InputStream getInputStream() {
        return input;
    }

    @Override
    public OutputStream getOutputStream() {
        return output;
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
