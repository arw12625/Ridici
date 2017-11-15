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
package ui;

import comm.StreamComm;
import comm.UDPStreamComm;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;

/**
 * A graphic interface for the configuration of a UDP communication channel.
 * 
 * Consists of fields for the target IP address and the target port
 * 
 * @author Andrew_2
 */
public class UDPOptionPanel extends CommOptionPanel {


    private JTextField targetIPField;
    private JTextField targetPortField;
    
    private String defaultIP = "192.168.4.1";
    private int defaultPort = 1234;

    /**
     * Constructs the UDP graphic configuration with default field entries
     */
    public UDPOptionPanel() {
        super();
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 5, 0, 5);

        targetIPField = new JTextField();
        targetIPField.setColumns(15); 
        targetIPField.setText(defaultIP);
        this.add(targetIPField, c);

        c.gridx = 1;
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        decimalFormat.setGroupingUsed(false);
        targetPortField = new JFormattedTextField(decimalFormat);
        targetPortField.setColumns(15); 
        targetPortField.setText("" + defaultPort);
        this.add(targetPortField, c);
    }

    /**
     * Create a UDP communication channel from the entered fields
     * 
     * @return the created communication channel
     */
    @Override
    public StreamComm createComm() {
        InetAddress address = null;
        try {
             address = InetAddress.getByName(targetIPField.getText());
        } catch (UnknownHostException ex) {
            System.err.println("Unkown UDP Host " + targetIPField.getText());
        }
        int port = Integer.parseInt(targetPortField.getText());
        
        UDPStreamComm comm = UDPStreamComm.createUDPStreamComm(address, port);
        return comm;
    }

    /**
     * Set whether the configuration is enabled.
     * The option panel should be disabled when the communication 
     * channel is open and enabled when it is closed.
     * 
     * @param ena whether to enable or disable the option panel
     */
    @Override
    public void setEnabled(boolean ena) {
        super.setEnabled(ena);
        targetIPField.setEditable(ena);
        targetPortField.setEditable(ena);
    }

    
}
