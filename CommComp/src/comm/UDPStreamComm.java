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

import java.net.InetAddress;

/**
 * An implementation of a StreamComm stream communication channel using UDP
 * 
 * Note this is accomplished by using a StreamOverBlockComm over a UDPBlockComm
 * 
 * @author Andrew_2
 */
public class UDPStreamComm extends StreamOverBlockComm {

    private UDPStreamComm(UDPBlockComm comm) {
        super(comm);
    }
    
    /**
     * Create a StreamComm using UDP over the given address and port
     * 
     * @param address The internet address to use with UDP
     * @param port The port to use with UDP
     * @return 
     */
    public static UDPStreamComm createUDPStreamComm(InetAddress address, int port) {
        UDPBlockComm udpBlockComm = new UDPBlockComm(address, port);
        UDPStreamComm udpStreamComm = new UDPStreamComm(udpBlockComm);
        return udpStreamComm;
    }

}
