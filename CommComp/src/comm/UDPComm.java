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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * An implementation of Comm using the UDP protocol over the web. Sends UDP to a
 * specified address and port, and listens on all local ports for UDP from that
 * address
 *
 * @author Andrew_2
 */
public class UDPComm implements Comm {

    private int port;
    private InetAddress address;
    private DatagramSocket socket;

    private ByteBufferInput bin;
    private ByteBufferOutput bout;

    private boolean connected;

    private DefaultThread readThread;
    private DefaultThread writeThread;

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
    public UDPComm() {
        this(null, 0);
    }

    /**
     * Construct UDPComm with the specified address and port
     *
     * @param address address to send UDP to
     * @param port port to send UDP to
     */
    public UDPComm(InetAddress address, int port) {

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
            socket.setSoTimeout(1000);
        } catch (SocketException ex) {
            System.err.println("Could not instantiate DatagramSocket");
        }

        bin = new ByteBufferInput();
        bout = new ByteBufferOutput();
        bout.setCallback(() -> {
            writeUDP();
        });
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
     * Get the InputStream of data from all local UDP ports
     *
     * @return the InputStream of data from all local UDP ports
     */
    @Override
    public InputStream getInputStream() {
        return bin.getInputStream();
    }

    /**
     * Get the OutputStream of data to the UDP target
     *
     * @return the OutputStream of data to the UDP target
     */
    @Override
    public OutputStream getOutputStream() {
        return bout.getOutputStream();
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
        writeThread = new DefaultThread(() -> {
            writeUDP();
        });
        writeThread.start();
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
        writeThread.stop();
        socket.disconnect();
        return false;
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
            byte[] tmp = new byte[receivePacket.getLength()];
            System.arraycopy(receivePacket.getData(), receivePacket.getOffset(),
                    tmp, 0, receivePacket.getLength());

            bin.write(tmp);
            //bin.write(receivePacket.getData(), receivePacket.getOffset(),receivePacket.getLength());
        }
    }

    /**
     * Internal function for writing data from stream into UDP packet
     */
    private void writeUDP() {
        if (bout.available() > 0) {
            byte[] arr = new byte[packetBufferSize];
            int len = bout.read(arr);
            DatagramPacket sendPacket = new DatagramPacket(arr, len, address, port);
            try {
                socket.send(sendPacket);
            } catch (IOException ex) {
                System.err.println("Could not send to UDP");
            }
        }
    }

    /**
     * A utility class for creating a generic thread from a repeatable task
     * with a fixed loop time and functionality for stopping.
     */
    private class DefaultThread implements Runnable {

        private int loopTime;
        private boolean running;
        private Thread thread;
        private Runnable reptask;

        public static final int defaultLoopTime = 20;

        private DefaultThread(Runnable reptask) {
            this.loopTime = defaultLoopTime;
            this.reptask = reptask;
        }

        public void start() {
            running = true;
            thread = new Thread(this);
            thread.start();
        }

        public void stop() {
            running = false;
        }

        @Override
        public void run() {

            long last = System.currentTimeMillis();
            while (running) {
                long time = System.currentTimeMillis();
                if (time - last > loopTime) {

                    reptask.run();

                    last = time;
                }
                Thread.yield();
            }
        }

    }

}
