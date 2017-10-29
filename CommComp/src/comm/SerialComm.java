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

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashSet;

/**
 * An implementation of Comm utilizing a serial channel through Rxtx
 * Can be used for communication over USB
 * 
 * @author Andrew_2
 */
public class SerialComm implements Comm {

    private int baudrate, databits, stopbits, parity;
    private String portName;
    private SerialPort serialPort;
    private boolean connected;

    private InputStream in;
    private OutputStream out;

    public static final int defaultBaudrate = 9600,
            defaultDatabits = SerialPort.DATABITS_8,
            defaultStopbits = SerialPort.STOPBITS_1,
            defaultParity = SerialPort.PARITY_NONE;

    public static final int portTimeout = 2000;

    public static final String defaultPortName = "COM6";

    /**
     * Construct a SerialComm with the default parameters
     */
    public SerialComm() {
        this(defaultPortName, defaultBaudrate);
    }

    /**
     * Construct a SerialComm with the specified port name and baud rate
     * 
     * @param portName the name of the serial port
     * @param baudrate the desired baud rate
     */
    public SerialComm(String portName, int baudrate) {
        this(portName, baudrate, defaultDatabits, defaultStopbits, defaultParity);
    }

    /**
     * Construct a SerialComm with the specified parameters
     * 
     * @param portName the name of the serial port
     * @param baudrate the desired baud rate
     * @param databits the number of data bits per block
     * @param stopbits the number of stop bits per block
     * @param parity the number of parity bits per block
     */
    public SerialComm(String portName, int baudrate, int databits, int stopbits, int parity) {
        this.portName = portName;
        this.baudrate = baudrate;
        this.databits = databits;
        this.stopbits = stopbits;
        this.parity = parity;
    }

    /**
     * Connect to the serial port
     * 
     * @return whether the connection was successful
     */
    @Override
    public boolean connect() {
        if (connected) {
            disconnect();
        }
        try {
            
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            if (portIdentifier.isCurrentlyOwned()) {
                System.out.println("Error: Port is currently in use");
                return false;
            } else {
                CommPort commPort = portIdentifier.open(this.getClass().getName(), portTimeout);

                if (commPort instanceof SerialPort) {
                    serialPort = (SerialPort) commPort;
                    serialPort.setSerialPortParams(baudrate, databits, stopbits, parity);

                    in = serialPort.getInputStream();
                    out = serialPort.getOutputStream();

                    connected = true;

                } else {
                    System.out.println("Error: Only serial ports are handled by this example.");
                    return false;
                }
            }
        } catch (IOException | UnsupportedCommOperationException |
                PortInUseException | NoSuchPortException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Disconnect from the serial port
     * 
     * @return whether the disconnection was successful
     */
    @Override
    public boolean disconnect() {
        connected = false;
        try {
            serialPort.close();
            in.close();
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Returns if the serial is connected
     * 
     * @return whether the serial comm is connected
     */
    @Override
    public boolean isConnected() {
        return connected;
    }

    /**
     * Get the InputStream of data from serial port
     * 
     * @return the InputStream of data from the serial port
     */
    @Override
    public InputStream getInputStream() {
        return in;
    }
    
    /**
     * Gets the OutputStream of data to the serial port
     * 
     * @return the OutputStream of data to the serial port
     */
    @Override
    public OutputStream getOutputStream() {
        return out;
    }

    /**
     * Set the name of the port to use for serial
     * @param name the name of the desired serial port
     */
    public void setPortName(String name) {
        if (!connected) {
            this.portName = name;
        } else {
            System.out.println("Cannot change settings of port while in use");
        }
    }

    /**
     * Set the baud rate used in serial communication
     * @param baudrate the desired baud rate
     */
    public void setBaudrate(int baudrate) {
        if (!connected) {
            this.baudrate = baudrate;
        } else {
            System.out.println("Cannot change settings of port while in use");
        }
    }

    /**
     * Change settings of the serial communication
     * 
     * @param portName the name of the serial port
     * @param baudrate the desired baud rate
     * @param databits the number of data bits per block
     * @param stopbits the number of stop bits per block
     * @param parity the number of parity bits per block
     */
    public void changeSettings(String portName, int baudrate, int databits, int stopbits, int parity) {
        if (!connected) {
            this.portName = portName;
            this.baudrate = baudrate;
            this.databits = databits;
            this.stopbits = stopbits;
            this.parity = parity;
        } else {
            System.out.println("Cannot change settings of port while in use");
        }
    }

    /**
     * Get all available serial ports
     * 
     * @return A HashSet containing the CommPortIdentifier for all serial ports
     * that are not currently being used.
     */
    public static HashSet<CommPortIdentifier> getAvailableSerialPorts() {
        HashSet<CommPortIdentifier> h = new HashSet<>();
        Enumeration thePorts = CommPortIdentifier.getPortIdentifiers();
        while (thePorts.hasMoreElements()) {
            CommPortIdentifier com = (CommPortIdentifier) thePorts.nextElement();
            switch (com.getPortType()) {
                case CommPortIdentifier.PORT_SERIAL:
                    try {
                        CommPort thePort = com.open("CommUtil", 50);
                        thePort.close();
                        h.add(com);
                    } catch (PortInUseException e) {
                        System.out.println("Port, " + com.getName() + ", is in use.");
                    } catch (Exception e) {
                        System.err.println("Failed to open port " + com.getName());
                        e.printStackTrace();
                    }
            }
        }
        return h;
    }

}
