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
import comm.SerialComm;
import static comm.SerialComm.defaultBaudrate;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;

/**
 * A graphic interface for the configuration of a serial communication channel.
 * 
 * Consists of fields for the local serial port name and the desired baud rate
 * 
 * @author Andrew_2
 */
public class SerialOptionPanel extends CommOptionPanel {

    private JTextField portNameField;
    private JTextField baudRateField;

    /**
     * Create serial option panel with the default field entries.
     */
    public SerialOptionPanel() {
        super();
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 5, 0, 5);

        portNameField = new JTextField(SerialComm.defaultPortName, 10);
        this.add(portNameField, c);

        c.gridx = 1;
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        decimalFormat.setGroupingUsed(false);
        baudRateField = new JFormattedTextField(decimalFormat);
        baudRateField.setColumns(15); //whatever size you wish to set
        baudRateField.setText("" + defaultBaudrate);
        this.add(baudRateField, c);
    }

    /**
     * Create the serial communication channel from the graphic configuration.
     * 
     * @return the created serial communication channel
     */
    @Override
    public StreamComm createComm() {
        SerialComm sc = new SerialComm(portNameField.getText(), Integer.parseInt(baudRateField.getText()));
        return sc;
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
        portNameField.setEditable(ena);
        baudRateField.setEditable(ena);
    }

}
