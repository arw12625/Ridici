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

import comm.Comm;
import comm.ConnectCallback;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * A graphic interface for a communication channel. Provdies functionality
 * for configuring, creating, connecting to, and disconnecting from a
 * communication channel.
 * 
 * Consists of a combo box for selection communication type, a connect/
 * disconnect button, and a panel containing the CommOptionPanel for the 
 * type of communication selected.
 * 
 * @author Andrew_2
 */
public class CommPanel extends JPanel {

    private Comm comm;

    private boolean connected;
    private final JButton connectButton;
    private final JComboBox<CommType> commTypes;
    
    private final ConnectCallback ccb;
    
    private final Timer connectUpdate;

    /**
     * All compatible communication types
     */
    private enum CommType {

        SERIAL("Serial"),
        WIFI_TCP("TCP"),
        WIFI_UDP("UDP");

        final String displayName;

        CommType(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private final JPanel copWrapper;
    private CommOptionPanel cop;

    /**
     * Construct the comm panel and register the callback for connection status
     * 
     * @param ccb the callback for a change in connection status
     */
    public CommPanel(ConnectCallback ccb) {
        super();
        this.ccb = ccb;
        
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 5, 0, 5);

        c.gridx = 0;
        commTypes = new JComboBox();
        commTypes.setModel(new DefaultComboBoxModel(CommType.values()));
        commTypes.setSelectedItem(CommType.SERIAL);
        commTypes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                useCommType();
            }
        });
        this.add(commTypes, c);

        c.gridx++;
        connectButton = new JButton("Connect");
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (connected) {
                    disconnect();
                } else {
                    connect();
                }
            }
        });
        this.add(connectButton, c);

        c.gridx++;
        copWrapper = new JPanel();
        this.add(copWrapper, c);

        useCommType();

        connectUpdate = new Timer(100, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(comm != null) {
                    if(connected && !comm.isConnected()) {
                        disconnect();
                    }
                }
            }
        });
        connectUpdate.start();
    }

    /**
     * Attempt to disconnect the comm channel.
     * Changes internal connection status to disconnected and re-enables
     * comm channel configuration. Notifies the connection callback
     * 
     * @return if the disconnect was successful
     */
    private boolean disconnect() {
        boolean status = comm.disconnect();
        ccb.onDisconnect(comm);
        comm = null;
        connectButton.setText("Connect");
        commTypes.setEnabled(true);
        cop.setEnabled(true);
        connected = false;
        return status;
    }

    /**
     * Create a comm channel using the configuration and attempt to connect
     * 
     * If the connection is successful, then this changes the internal 
     * connection status to connected, disables comm channel configuration, and
     * notifies the connection callback.
     * 
     * @return if the connect was successful
     */
    private boolean connect() {
        comm = cop.createComm();
        comm.connect();
        if (comm.isConnected()) {
            ccb.onConnect(comm);
            connectButton.setText("Disconnect");
            commTypes.setEnabled(false);
            cop.setEnabled(false);
            connected = true;
        }
        return connected;
    }

    /**
     * Select the type of communication channel to use.
     * Adds the corresponding comm option panel and revalidates
     */
    private void useCommType() {
        if (cop != null) {
            copWrapper.remove(cop);
        }

        CommType type = (CommType) commTypes.getSelectedItem();
        switch (type) {
            case SERIAL: {
                cop = new SerialOptionPanel();
                break;
            }
            case WIFI_UDP: {
                cop = new UDPOptionPanel();
                break;
            }
        }

        copWrapper.add(cop);
        revalidate();
    }
    
    /**
     * Get the created Comm channel.
     * @return the comm channel if connected, null otherwise
     */
    public Comm getComm() {
        return comm;
    }
    
}
