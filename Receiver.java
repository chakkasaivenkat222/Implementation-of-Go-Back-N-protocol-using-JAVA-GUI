import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.net.*;

public class receiver // class receiver is created
{
    static JLabel[] pkt; // packets are represented as Label options in GUI
    static JList<String> jlx; // JList is created
    static JButton acknow, d_acknow; // 2 buttons are created 1 for the receiver to acknowledge the packet and other
                                     // to donot ACK
    static DataInputStream dis; // variable dis is used for data_input_stream
    static DataOutputStream dos; // variable dos is used for data_output_stream
    static DefaultListModel<String> dlmx; // this is created for adding comments on the right of the screen
    static JScrollBar vertical; // to control the vertical scroll bar for JScrollPane
    static Timer Time; // to auto scroll the last added entry in the JList we need to wait for 500msec
                       // after
                       // adding the dlm element so we use docTimer

    static int expectedSeqNo = 0; // next packet to be received

    receiver() // constructor receiver is declared
    {
        JFrame jf = new JFrame("Receiver in GO BACK-N"); // JFrame is created with the Title Receiver in GBN
        jf.setSize(1400, 400); // setting the size of the Frame window

        // creating packets
        pkt = new JLabel[12]; // packets in the form of JLabels
        for (int i = 0; i < 12; i++) {
            pkt[i] = new JLabel(" " + i + " "); // naming the packets as 1,2,3....11
            pkt[i].setOpaque(true);
            pkt[i].setBackground(Color.WHITE); // initially setting the background colour to white for all pkts
        }
        pkt[0].setForeground(Color.red);

        // creating buttons
        acknow = new JButton("send ACK"); // creating the buttons
        acknow.setEnabled(false);
        acknow.addActionListener(new ActionListener() // if that button is clicked what action have to be done
        {
            public void actionPerformed(ActionEvent e5) {
                try {
                    dos.writeInt(expectedSeqNo - 1);
                    dlmx.addElement("ACK for " + (expectedSeqNo - 1) + "had been sent" + "\n"); // adding sentence to
                                                                                                // the comment box
                } catch (Exception ew) {
                    dlmx.addElement("error in sending ACK for : " + (expectedSeqNo - 1));
                }
            }
        });
        d_acknow = new JButton("Miss the ACK");
        d_acknow.setEnabled(false);
        d_acknow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e9) {
                dlmx.addElement("ACK for :" + (expectedSeqNo - 1) + "had been killed" + "\n"); // adding the sentence to
                                                                                               // comment box(right
                                                                                               // side)
            }
        });

        // creating a panel to display the comment summary
        dlmx = new DefaultListModel<String>(); // declaring dlmx variable to print comments in comment box
        jlx = new JList<String>(dlmx);
        jlx.setLayoutOrientation(JList.VERTICAL); // setting layout movement as vertical to move in vertival direction
        jlx.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // if we select a particular sentence in the panel
                                                                   // ,only 1 list is going to be selected
        jlx.setVisibleRowCount(30); // no. of characters in the row that is visible
        JScrollPane scrollArea = new JScrollPane(jlx);
        scrollArea.setSize(1000, 600);
        vertical = scrollArea.getVerticalScrollBar();
        Time = new Timer(500, new ActionListener() // delay in displaying in the Pane of comment box
        {
            public void actionPerformed(ActionEvent e19) {
                vertical.validate(); // validation is,updating to "Maximum"
                vertical.setValue(vertical.getMaximum());
            }
        });
        Time.setRepeats(false);
        dlmx.addListDataListener(new ListDataListener() {
            public void change_of_contents(ListDataEvent e99) {
            }

            public void Removed_Interval(ListDataEvent e) {
            }

            public void Added_Interval(ListDataEvent e98) {
                if (Time.isRunning()) {
                    Time.restart();
                } else {
                    Time.start();
                }
            }
        });
        // creating pane to display the packets
        JPanel pktPane = new JPanel();
        pktPane.setLayout(new FlowLayout()); // adding layout to display the packets
        pktPane.setBorder(BorderFactory.createEmptyBorder(80, 15, 80, 15)); // setting the place of the Frame
        for (int i = 0; i < 12; i++) {
            pktPane.add(pkt[i]);
            pktPane.add(Box.createHorizontalStrut(5)); // giving width of each Packet
        }

        // creating pane,to display buttons
        JPanel BtnPane = new JPanel(); // creating a JPanel to display all the buttons
        BtnPane.setLayout(new BoxLayout(BtnPane, BoxLayout.X_AXIS)); // setting layout to x-axis
        BtnPane.add(acknow); // adding acknow button to Panel
        BtnPane.add(Box.createHorizontalStrut(120)); // where acknow button is to be placed
        BtnPane.add(d_acknow); // adding d_acknow button to the Panel
        BtnPane.setBorder(BorderFactory.createEmptyBorder(5, 360, 5, 120));

        // creating pane,for heading panel
        JPanel head_Pane = new JPanel();
        JLabel heading = new JLabel("Send Window Size=1"); // the heading is set as window size of the receiver is 1
        head_Pane.setLayout(new FlowLayout()); // setting the layout
        head_Pane.add(heading); // adding the heading into the head_pane to display the Window Size

        // adding all panes,to main frame
        jf.add(scrollArea, BorderLayout.LINE_END); // adding the scrollArea
        jf.add(BtnPane, BorderLayout.PAGE_END); // adding buttonPane
        jf.add(pktPane, BorderLayout.CENTER); // adding pktPane
        jf.add(head_Pane, BorderLayout.PAGE_START); // adding head_Pane to thr JFrame
        jf.setVisible(true);
    }

    // overall GUI is designed.....now moving on to the logic part
    public static void After_Receiving_Packet() {
        int p;
        try {
            p = dis.readInt(); // reads the next 4 input bytes and returns value
            if (p == expectedSeqNo) // if returned value is equal to expected Sequence number then go into the loop
            {
                expectedSeqNo++; // increment the expected seq no
                dlmx.addElement("packet no: " + p + " received"); // add the statement in the JPane command
                pkt[expectedSeqNo - 1].setForeground(Color.black); // set foreground color -black to the preceeding
                                                                   // packet
                pkt[expectedSeqNo - 1].setBackground(Color.GREEN); // set Background color as green for packets which
                                                                   // are received
                if (expectedSeqNo < 12) // after incrementing expectedseqNo also if it is less than 12 go into loop
                {
                    pkt[expectedSeqNo].setForeground(Color.red); // set the foreground color as red for the next packet
                                                                 // which is just now turned to green
                }
            } else // if p value is not equal to expectedseqNo
            {
                dlmx.addElement("packet no: " + p + " received,DISCARDED"); // this packet may be lost in the network
            }
            acknow.setEnabled(true); // after this process only again these buttons are enabled for furthur usage
            d_acknow.setEnabled(true);
        } catch (Exception ew) // if there is any exception raised in the try block can be solved catch block
        {
        }
    }

    public static void Application_Reset() {
        dlmx.clear();
        dlmx.addElement("Listening to the port 8575" + "\n"); // this statement is printed in the Pane window
        dlmx.addElement("TCP connection estabilished successfully" + "\n");
        acknow.setEnabled(false); // these buttons are disabled
        d_acknow.setEnabled(false);
        expectedSeqNo = 0; // as process is reset it starts from the beginning ...so expectedseqNo=0
        for (int i = 0; i < 12; i++) {
            pkt[i].setVisible(false);
            pkt[i].setBackground(Color.WHITE); // initially all are black with numbers and white as background
            pkt[i].setForeground(Color.black);
            pkt[i].setVisible(true);
        }
        pkt[0].setVisible(false);
        pkt[0].setForeground(Color.red); // for first packet alone make foreground color as red
        pkt[0].setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        new receiver();
        ServerSocket SS1 = new ServerSocket(8575); // for the class ServerSocket we are declaring the SS1 object
        dlmx.addElement("Listening at port 8575");
        Socket S = SS1.accept(); // this Socket implements the Clients Socket and for this Socket class we are
                                 // declaring S as object
        dlmx.addElement("TCP connection estabilished!!");
        dis = new DataInputStream(S.getInputStream()); // initializing dis as getInputStream
        dos = new DataOutputStream(S.getOutputStream()); // initializing dos as getOutput Stream
        int q;
        while (true) {
            q = dis.readInt(); // returns the next 4 bytes of input Stream as an integer
            switch (q) {
                case 1: // if q value is equal to 1 means close the socket connection
                {
                    S.close();
                    SS1.close();
                    dlmx.addElement("Closing Socket...");
                }
                case 2: // if q=2 means call method After_Receiving_Packet
                {
                    After_Receiving_Packet();
                    break;
                }
                case 3: {
                    Application_Reset(); // if q=3 reset the application
                    break;
                }
                case 4: {
                    break;
                }
            }
        }
    }
}
