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

import util.BlockInput;
import util.BlockOutput;
import util.StreamInput;
import util.StreamOutput;
import comm.StreamComm;
import java.nio.ByteBuffer;

/**
 * An implementation of a Messenger using a StreamComm block communication channel.
 * 
 * Messages are written as blocks to the provided encoder whose output is 
 * written as a stream to the StreamComm. Messages are received through 
 * stream from the StreamComm and decoded to blocks by the provided decoder.
 * 
 * @author Andrew_2
 */
public class BlockToStreamMessenger implements Messenger {

    private BlockInput encoderInput;
    private StreamOutput encoderOutput;
    private StreamInput decoderInput;
    private BlockOutput decoderOuput;

    private StreamComm streamComm;
    
    private BlockReceivedToDecoderCallback blockCallback;

    /**
     * Construct a BlockToStreamMessenger with the given encoder, decoder, and StreamComm
     * 
     * Note most typically, the input and output of the encoder will be one object,
     * and the input and output of the decoder will be one object as well.
     * 
     * @param encoderInput The input of the encoder
     * @param encoderOutput The output of the encoder
     * @param decoderInput The input of the decoder
     * @param decoderOuput The output of the decoder
     * @param streamComm The StreamComm to communicate over
     */
    public BlockToStreamMessenger(BlockInput encoderInput, StreamOutput encoderOutput,
            StreamInput decoderInput, BlockOutput decoderOuput,
            StreamComm streamComm) {
        this.encoderInput = encoderInput;
        this.encoderOutput = encoderOutput;
        this.decoderInput = decoderInput;
        this.decoderOuput = decoderOuput;
        this.streamComm = streamComm;
        
        encoderOutput.setOutputStream(streamComm.getOutputStream());
        decoderInput.setInputStream(streamComm.getInputStream());
        
        this.blockCallback = new BlockReceivedToDecoderCallback();
        decoderOuput.setBlockAvailableCallback(blockCallback);
    }

    @Override
    public boolean sendMessage(ByteBuffer msg) {
        return encoderInput.writeBlock(msg);
    }

    @Override
    public void setMessageReceivedCallback(MessageReceivedCallback msgCallback) {
        this.blockCallback.setMessageReceivedCallback(msgCallback);
    }

    @Override
    public boolean connect() {
        if(streamComm != null) {
            return streamComm.connect();
        } else {
            return false;
        }
    }

    @Override
    public boolean disconnect() {
        if(streamComm != null) {
            return streamComm.disconnect();
        } else {
            return false;
        }
    }

    @Override
    public boolean isConnected() {
        if(streamComm != null) {
            return streamComm.isConnected();
        } else {
            return false;
        }
    }
    
    private class BlockReceivedToDecoderCallback implements BlockOutput.BlockOutputCallback {

        MessageReceivedCallback msgCallback;
        
        public void setMessageReceivedCallback(MessageReceivedCallback msgCallback) {
            this.msgCallback = msgCallback;
        }

        @Override
        public void onBlockOutput(ByteBuffer block) {
            if(msgCallback != null) {
                msgCallback.onMessageReceived(BlockToStreamMessenger.this, block);
            } else {
                System.err.println("Cannot receive message. Callback is null");
            }
        }
        
    }

}
