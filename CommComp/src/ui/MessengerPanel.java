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

import comm.BlockComm;
import comm.BlockOverStreamComm;
import comm.Comm;
import comm.StreamComm;
import comm.StreamOverBlockComm;
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
import message.COBSMessenger;
import message.Messenger;
import message.TransparentBlockMessenger;

/**
 * A graphical interface for the configuration, creation, and connection of a 
 * Messenger
 *
 * @author Andrew_2
 */
public class MessengerPanel extends JPanel {
    
    private Messenger messenger;
    private Comm comm;
    private boolean connected;
    
    private final ConnectUICallback ccb;
    private final Timer connectUpdate;
    
    private final JButton connectButton;
    private final JComboBox<CommType> commTypes;
    
    private JPanel copWrapper;
    private CommOptionPanel cop;
    
    private JComboBox<MessengerType> messengerTypes;
    /**
     * Supported communication types
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
    
    /**
     * Supported Messenger types
     */
    private enum MessengerType {

        TRANSPARENT_BLOCK("Transparent Block"),
        TRANSPARENT_STREAM("Transparent Stream"),
        COBS("COBS");

        final String displayName;

        MessengerType(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
    /**
     * Construct the MessengerPanel and register the callback for connection status
     * 
     * @param ccb the callback for a change in connection status
     */
    public MessengerPanel(ConnectUICallback ccb) {
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
        //update UI to reflect comm type
        useCommType();

        //Add Messenger configuration combo box
        c.gridx = 0;
        c.gridy++;
        c.gridy++;
        messengerTypes = new JComboBox();
        messengerTypes.setModel(new DefaultComboBoxModel(MessengerType.values()));
        messengerTypes.setSelectedItem(MessengerType.TRANSPARENT_BLOCK);
        this.add(messengerTypes, c);

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
     * Attempt to disconnect the Messenger.
     * Changes internal connection status to disconnected and re-enables
     * Messenger configuration. Notifies the connection callback
     * 
     * @return if the disconnect was successful
     */
    private boolean disconnect() {
        boolean status = messenger.disconnect();
        ccb.onUIDisconnect(messenger);
        comm = null;
        connectButton.setText("Connect");
        commTypes.setEnabled(true);
        cop.setEnabled(true);
        messengerTypes.setEnabled(true);
        connected = false;
        return status;
    }

    /**
     * Create a Messenger using the configuration and attempt to connect
     * 
     * If the connection is successful, then this changes the internal 
     * connection status to connected, disables Messenger configuration, and
     * notifies the connection callback.
     * 
     * @return if the connect was successful
     */
    private boolean connect() {
        //Construct base communication channel. May have to wrap to agree
        //with the messenger type
        Comm baseComm = cop.createComm();
        switch((MessengerType)messengerTypes.getSelectedItem()) {
            case TRANSPARENT_BLOCK : {
                BlockComm blockComm;
                if(baseComm instanceof BlockComm) {
                    blockComm = (BlockComm)baseComm;
                } else if(baseComm instanceof StreamComm) {
                    blockComm = new BlockOverStreamComm((StreamComm)baseComm);
                } else {
                    System.err.println("Communication type not supported");
                    return false;
                }
                messenger = new TransparentBlockMessenger(blockComm);
                break;
            }
            case COBS : {
                StreamComm streamComm;
                if(baseComm instanceof StreamComm) {
                    streamComm = (StreamComm)baseComm;
                } else if(baseComm instanceof BlockComm) {
                    streamComm = new StreamOverBlockComm((BlockComm)baseComm);
                } else {
                    System.err.println("Communication type not supported");
                    return false;
                }
                messenger = COBSMessenger.createCOBSMessenger(streamComm);
                break;
            }
        }
        
        if (messenger.connect()) {
            ccb.onUIConnect(messenger);
            connectButton.setText("Disconnect");
            commTypes.setEnabled(false);
            cop.setEnabled(false);
            messengerTypes.setEnabled(false);
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
    * A callback for connections and disconnections of a Messenger
    */
   public interface ConnectUICallback {

       /**
        * Callback for when the Messenger is connected
        * @param m the connected Messenger
        */
       public void onUIConnect(Messenger m);

       /**
        * Callback for when the Messenger is disconnected
        * @param m the disconnected Messenger
        */
       public void onUIDisconnect(Messenger m);

   }

    
}
