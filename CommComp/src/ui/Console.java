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

import util.BlockInput;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import message.Messenger;
import ui.MessengerPanel.ConnectUICallback;

/**
 * A graphic console window for sending and receiving messages from a
 * communication channel. The console also provides a CommPanel for configuring
 * and creating a communication channel.
 *
 * @author Andy
 */
public class Console extends JFrame implements ConnectUICallback {

    private String title;

    private JTextArea outputText;

    private JTextField inputLine;
    private StringModifier modifier;

    private MessengerPanel mp;

    private Messenger messenger;

    private ByteBuffer writerBuffer;
    private WriterWrapper writerWrapper;

    private PrintReaderCallback printCallback;

    public static final int maxMessageLen = 254;

    /**
     * Construct a console window with the given title
     *
     * @param title the title of the console window
     */
    public Console(String title) {

        super();

        this.title = title;

        setTitle(title);
        Container con = getContentPane();
        con.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel(title);
        con.add(titleLabel, c);

        Font codeFont = new Font("Courier New", Font.PLAIN, 16);

        c.gridy = 1;
        c.gridwidth = 3;
        outputText = new JTextArea(16, 64);
        outputText.setFont(codeFont);
        outputText.setEditable(false);
        //Output window automatically scrolls if at the bottom, otherwise is stationary
        JScrollPane outputScroll = new JScrollPane(outputText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        new SmartScroller(outputScroll);
        con.add(outputScroll, c);

        c.gridy = 2;
        inputLine = new JTextField(66);
        inputLine.setFont(codeFont);
        inputLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = inputLine.getText();
                inputLine.setText("");
                writeUserInput(text);
            }
        });
        inputLine.setEnabled(false);
        con.add(inputLine, c);

        c.gridy = 3;
        c.gridwidth = 1;
        mp = new MessengerPanel(this);
        con.add(mp, c);

        c.gridy = 4;

        pack();
        setSize(800, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        writerWrapper = new WriterWrapper();
        printCallback = new PrintReaderCallback();
        /*printCallback.setAuxReader(new Messenger.MessageReceivedCallback() {
            @Override
            public void onMessageReceived(Messenger messenger, ByteBuffer msg) {

                byte[] rec = new byte[msg.remaining()];
                msg.get(rec);
                int x1 = ((rec[0] << 8) & 0xFF00) | ((rec[1] & 0xFF));
                int y1 = ((rec[2] << 8) & 0xFF00) | ((rec[3] & 0xFF));
                int s1 = ((rec[4] << 8) & 0xFF00) | ((rec[5] & 0xFF));
                int x2 = ((rec[6] << 8) & 0xFF00) | ((rec[7] & 0xFF));
                int y2 = ((rec[8] << 8) & 0xFF00) | ((rec[9] & 0xFF));
                int s2 = ((rec[10] << 8) & 0xFF00) | ((rec[11] & 0xFF));
                println("x1: " + x1 + ", y1: " + y1 + ", s1: " + s1);
                println("x2: " + x2 + ", y2: " + y2 + ", s2: " + s2);

            }
        });*/

        MultiModifier mmodifier = new MultiModifier();
        mmodifier.addModifier(new HexEscapeModifier());
        mmodifier.addModifier(new ProtocolModifier());
        modifier = mmodifier;

    }

    /**
     * Internal callback for when data is read from the communication channel.
     * Outputs the data to the output text
     *
     * @param b the data read
     */
    private void onRead(ByteBuffer b) {
        byte[] data = new byte[b.remaining()];
        b.get(data);
        String s = new String(data);
        println(s);
    }

    /**
     * Print a line of text to the output text of the console
     *
     * @param line the line to print
     */
    private void println(String line) {
        outputText.append(line + "\n");
    }

    /**
     * Write user input to the communication channel
     *
     * @param text the user input string
     */
    private void writeUserInput(String text) {
        String modText = modifier.modify(text);

        writerBuffer.clear();
        writerBuffer.put(modText.getBytes());
        writerBuffer.flip();
        try {
            messenger.sendMessage(writerBuffer);

            outputText.append("Line sent: ");
            outputText.append(text);
            outputText.append("\n");
            inputLine.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            outputText.append("Line could not be sent: ");
            outputText.append(text);
            outputText.append("\n");
        }
        writerBuffer.rewind();
    }

    /**
     * Get the writer to communication channel being used by the console. Allows
     * for external classes to write to the communication channel in addition to
     * the output from the console.
     *
     * @return the writer to the communication channel
     */
    public BlockInput getWriter() {
        return writerWrapper;
    }

    /**
     * Set a reader to read incoming data from the communication channel. Used
     * in parallel with output to the console.
     *
     * @param brc the reader for the incoming comm data
     */
    public void setReader(Messenger.MessageReceivedCallback brc) {
        printCallback.setAuxReader(brc);
    }

    /**
     * Callback for when the MessengerPanel creates and connects to a Messenger
     * successfully. Constructs a reader and writer on the channel and
     * enables user input to be sent. Starts a thread to read data from the
     * channel.
     *
     * @param messenger the communication channel created
     */
    @Override
    public void onUIConnect(Messenger messenger) {

        this.messenger = messenger;
        messenger.setMessageReceivedCallback(printCallback);

        writerBuffer = ByteBuffer.allocate(maxMessageLen);
        writerWrapper.setMessenger(messenger);

        inputLine.setEnabled(true);
    }

    /**
     * Callback for when the MessengerPanel disconnects the Messenger
     * Disables user input. Stops the reader thread created in onConnect
     *
     * @param messenger
     */
    @Override
    public void onUIDisconnect(Messenger messenger) {
        this.messenger = null;
        inputLine.setEnabled(false);
    }

    /**
     * A callback for reading data from the communication channel and printing
     * the data to the console output and to another auxillary callback if it
     * exists
     */
    private class PrintReaderCallback implements Messenger.MessageReceivedCallback {

        private Messenger.MessageReceivedCallback aux;

        /**
         * Set an auxiliary reader callback
         *
         * @param aux the auxiliary reader callback
         */
        private void setAuxReader(Messenger.MessageReceivedCallback aux) {
            this.aux = aux;
        }

        /**
         * The callback function. Prints data to console and to auxiliary reader
         * if it exists
         *
         * @param messenger The messenger that is relaying the message
         * @param msg The message received
         *
         */
        @Override
        public void onMessageReceived(Messenger messenger, ByteBuffer msg) {
            ByteBuffer copy = msg.duplicate();
            Console.this.onRead(msg);
            if (aux != null) {
                aux.onMessageReceived(messenger, copy);
            }
        }

    }

    /**
     * A wrapper of the consoles writer. Allows external classes to write to the
     * communication channel writer of the console.
     */
    private class WriterWrapper implements BlockInput {

        private Messenger messenger;

        /**
         * Set the messenger
         *
         * @param messenger the writer to be used
         */
        public void setMessenger(Messenger messenger) {
            this.messenger = messenger;
        }

        /**
         * Write data to the console's communication channel writer
         *
         * @param b the data to write
         * @return whether the write was successful
         * @throws IOException
         */
        @Override
        public boolean writeBlock(ByteBuffer block) {

            if (messenger != null) {
                messenger.sendMessage(block);
                return true;
            } else {
                //System.err.println("Cannot write block to null writer");
                return false;
            }
        }

    }
}
