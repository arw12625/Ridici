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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * An implementation of a BlockComm block communication channel over UDP
 * 
 * @author Andrew_2
 */
public class UDPBlockComm implements BlockComm {

    private int port;
    private InetAddress address;
    private DatagramSocket socket;

    private boolean connected;

    private BlockReceivedCallback blockReceivedCallback;
    private DefaultThread readThread;

    private int packetBufferSize;

    /**
     * The default address for UDP which is the loopback address for localhost
     */
    public static final byte[] defaultAddress = {127, 0, 0, 1};
    public static final int defaultPort = 1234;
    public static final int defaultPacketBufferSize = 1024;

    /**
     * Construct UDPComm with default parameters
     */
    public UDPBlockComm() {
        this(null, 0);
    }

    /**
     * Construct UDPComm with the specified address and port
     *
     * @param address address to send UDP to
     * @param port port to send UDP to
     */
    public UDPBlockComm(InetAddress address, int port) {

        if (address == null) {
            try {
                address = InetAddress.getLocalHost();
            } catch (UnknownHostException ex) {
                System.err.println("Could not resolve local host?!?!");
            }
        }
        if (port == 0) {
            port = defaultPort;
        }

        this.address = address;
        this.port = port;
        this.packetBufferSize = defaultPacketBufferSize;

        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(10000);
        } catch (SocketException ex) {
            System.err.println("Could not instantiate DatagramSocket");
        }

    }

    /**
     * Set the address and port to send UDP to
     *
     * @param address address to send UDP to
     * @param port port to send UDP to
     */
    public void setAddress(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    /**
     * Connect the UDPComm. This starts threads reading and writing UDP from the
     * internal buffers No actual connection is made as UDP is a connectionless
     * protocol
     *
     * @return whether the connection was successful
     */
    @Override
    public boolean connect() {

        //mostly symbolic as UDP is connectionless
        socket.connect(address, port);

        connected = true;
        readThread = new DefaultThread(() -> {
            readUDP();
        });
        readThread.start();

        return true;
    }

    /**
     * Disconnect the UDPComm Stops the threads reading and writing UDP
     *
     * @return whether the disconnection was successful
     */
    @Override
    public boolean disconnect() {
        readThread.stop();
        socket.disconnect();
        return true;
    }

    /**
     * Returns whether the UDP is connected
     *
     * @return whether the UDP is connected
     */
    @Override
    public boolean isConnected() {
        return connected;
    }

    /**
     * Internal function for reading from UDP and writing data to the stream
     */
    private void readUDP() {
        boolean invalid = false;
        byte[] rec = new byte[packetBufferSize];
        DatagramPacket receivePacket = new DatagramPacket(rec, rec.length);
        
        /*
         If no packet is received by the timeout, a SocketTimeoutException 
         is thrown and then caught allowing the function to return. This is
         necessary as otherwise socket will always be locked and cause the 
         program to hang when trying to close the socket
         */
        try {
            socket.receive(receivePacket);
        } catch (SocketTimeoutException ex) {
            //udp timeout invalidates packet
            invalid = true;
        } catch (IOException ex) {
            System.err.println("Could not read from UDP");
        }

        if (receivePacket.getLength() != 0 && !invalid) {
            ByteBuffer buf = ByteBuffer.wrap(rec);
            buf.position(receivePacket.getOffset());
            buf.limit(receivePacket.getLength() + buf.position());
            blockReceivedCallback.onBlockReceived(this, buf.slice());

        }
    }

    /**
     * Write a block of data as a single datagram packet to the UDP socket
     * 
     * @param block The block to be written
     */
    @Override
    public void writeBlock(ByteBuffer block) {
        if (!isConnected()) {
            System.err.println("Cannot write to UDP when disconnected");
        } else if(!block.hasRemaining()) {
            System.err.println("Cannot write empty block to UDP");
        } else {
            
            byte[] blockArray = new byte[block.remaining()];
            block.get(blockArray);
            DatagramPacket sendPacket = new DatagramPacket(blockArray, blockArray.length, address, port);
            try {
                socket.send(sendPacket);
            } catch (IOException ex) {
                System.err.println("Could not send to UDP");
            }
        }
    }

    /**
     * Set the callback used when a block of data is received through UDP
     * 
     * @param callback the callback used
     */
    @Override
    public void setBlockReceivedCallback(BlockReceivedCallback callback) {
        this.blockReceivedCallback = callback;
    }


}
