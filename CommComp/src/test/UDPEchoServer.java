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
package test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * A simple implementation of a UDP echo server running on localhost.
 * 
 * @author Andrew_2
 */
public class UDPEchoServer implements Runnable {

    private boolean running;
    private int port;
    private Thread thread;

    public static void main(String[] args) {
        (new UDPEchoServer(1234)).start();
    }
    
    /**
     * Construct a local UDP echo server listening on a specified local port 
     * 
     * @param port the port to listen for UDP packets on
     */
    public UDPEchoServer(int port) {
        this.port = port;
    }

    /**
     * Starts the UDP echo server
     * Spawns a new thread to listen for and respond to UDP packets
     */
    public void start() {
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    /**
     * Stop the UDP server
     * Ends the thread the server is running on
     */
    public void stop() {
        running = false;

    }

    @Override
    public void run() {

        try {
            DatagramSocket socket = new DatagramSocket(port);

            byte[] buffer = new byte[1024];

            while (running) {

                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(receivePacket);

                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                byte[] rec = receivePacket.getData();
                int offset = receivePacket.getOffset();
                int len = receivePacket.getLength();

                String recString = new String(rec, offset, len);

                DatagramPacket sendPacket = new DatagramPacket(rec, offset, len, IPAddress, port);
                socket.send(sendPacket);

                Thread.yield();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
