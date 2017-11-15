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

import comm.UDPStreamComm;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Creates a UDP echo server, sends input from System.in to the server
 * through UDP, and prints UDP responses from the server to System.out
 * 
 * @author Andrew_2
 */
public class UDPTest {

    public static void main(String[] args) throws UnknownHostException {

        int port = 1234;

        UDPEchoServer echo = new UDPEchoServer(port);
        echo.start();

        UDPStreamComm comm = UDPStreamComm.createUDPStreamComm(InetAddress.getByName("192.168.4.1"), port);
        comm.connect();

        InputStream in = comm.getInputStream();
        OutputStream out = comm.getOutputStream();

        BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));

        Thread writeThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (true) {
                        String line = userIn.readLine() + "\n";
                        out.write(line.getBytes());
                        Thread.yield();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Thread readThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (true) {
                        if (in.available() > 0) {
                            System.out.print("ECHO ");
                            while (in.available() > 0) {
                                System.out.write(in.read());
                            }
                        }
                        Thread.yield();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        writeThread.start();
        readThread.start();

    }

}
