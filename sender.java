import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.time.Duration;
import java.time.LocalDateTime;

public class sender implements ActionListener // class sender is created and adds a ActionListener
{
    static JButton cnct, reset, time, sendNew, miss_pkt, d_miss_pkt, width; // creating JButtons for all
    static JLabel[] pkt; // declaring pkt as JLabel
    static JList<String> jl1; // jl1 as JList for displaying in the JArea
    static DefaultListModel<String> dlmx; // this dlmx is used to add the strings in the JArea
    static JScrollBar vertical; // Activating Scroll Bar to move in the vertical
    static Timer DocTimer; // the delay time required for filling the JPane in the right side
    Socket S; // S is the object created for the class Socket
    static Timer timer;
    static JLabel timeDsply; // creating timeDisplay as JLabel
    LocalDateTime startTime;
    Duration duration = Duration.ofSeconds(12);
    static int base = 0; // base represents the first number packet of the window
    static int nextSeqNo = 0; // nextseqNo is the next packet that need to be sent
    static JLabel seqNoLabel, baseLabel;
    static DataInputStream dis; // declaring dis as static DataInputStream variable
    static DataOutputStream dos; // declaring dos as static DataOutputStream variable
    static int channel_length = 0;

    sender() // constructor declaration
    {
        JFrame jf = new JFrame("Sender in GBN"); // creating JFrame and title of the Frame
        jf.setSize(1360, 370); // setting Size of the JFrame
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // if we click on close button it should close

        // creating butons
        cnct = new JButton("Connect"); // creating connect Button
        cnct.addActionListener(this); // If we click on that Connect button what action need to be taken
        sendNew = new JButton("Send New");
        sendNew.addActionListener(this);
        sendNew.setEnabled(false); // making SendNew not to enable for usage
        miss_pkt = new JButton("Miss Packet");
        miss_pkt.addActionListener(this);
        miss_pkt.setEnabled(false); // don't Enable miss_pkt button
        d_miss_pkt = new JButton("Don't Miss Packet"); // creating Don't Miss Packet and adding ActionListener
        d_miss_pkt.addActionListener(this);
        d_miss_pkt.setEnabled(false);
        reset = new JButton("Reset"); // declaring Reset and adding actionListener
        reset.addActionListener(this);
        reset.setEnabled(false);
        width = new JButton("Band Width");
        width.addActionListener(this);
        width.setEnabled(false);
        time = new JButton("Start Timer"); // declaring Start Timer and not making it to enable
        time.setEnabled(false);

        // creating packets
        pkt = new JLabel[12]; // creating 12 packets as JLabels
        for (int i = 0; i < 12; i++) // for loop passing all 12 packets
        {
            pkt[i] = new JLabel(" " + i + " "); // printing 1,2,3...on the packets
            pkt[i].setForeground(Color.black); // setting Foreground color to black
            pkt[i].setOpaque(true);
            pkt[i].setBackground(Color.white); // setting background color as White initially for all packets
        }
        for (int i = 0; i < 4; i++) // as the window size=4 First 4 packets making as red as they are ready for
                                    // transmission
        {
            pkt[i].setForeground(Color.red);
        }

        // initialising display for time,and creating pane for it
        timeDsply = new JLabel(" -- -- -- ");
        timeDsply.setBackground(Color.cyan);
        JLabel impPermanentInfo = new JLabel("Window Size=4");
        JPanel TDsplyPanel = new JPanel(); // TDsplyPanel refers to TimeDisplayPanel which is declared as JPanel
        TDsplyPanel.add(impPermanentInfo); // adding the heading Window Size into the TimeDisplayPanel
        TDsplyPanel.add(Box.createHorizontalStrut(120)); // created an invisible fixed width of length 120
        TDsplyPanel.add(time); // adding time to timeDisplayPanel
        TDsplyPanel.add(timeDsply);
        TDsplyPanel.add(Box.createHorizontalStrut(10));

        seqNoLabel = new JLabel("next Sequence no:" + nextSeqNo); // creating JLabel for seqNoLabel
        baseLabel = new JLabel("base :" + base); // creating jLabel for BaseLabel
        TDsplyPanel.add(Box.createHorizontalStrut(30)); // creating invisible horizontal width of length 30
        TDsplyPanel.add(seqNoLabel);
        TDsplyPanel.add(Box.createHorizontalStrut(10)); // creating invisible horizontal width of length 10
        TDsplyPanel.add(baseLabel);
        TDsplyPanel.add(Box.createHorizontalStrut(6));
        baseLabel.setOpaque(true);
        seqNoLabel.setOpaque(true);
        TDsplyPanel.setBackground(Color.red);

        // creating pane,to display summary
        dlmx = new DefaultListModel<String>(); // dlmx is like a Array_List which adds string type into the Area Display
        jl1 = new JList<String>(dlmx); // creating List
        jl1.setVisibleRowCount(30); // Maximum No.of letters going to be filled in a line
        jl1.setLayoutOrientation(JList.VERTICAL);
        jl1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // if u click on a list it will just select only one
                                                                   // List_String at a time
        jl1.setBackground(Color.orange);
        JScrollPane scrollArea = new JScrollPane(jl1);
        scrollArea.setSize(1000, 300);
        vertical = scrollArea.getVerticalScrollBar();
        DocTimer = new Timer(500, new ActionListener() // delay for strings noting into JArea Box is 500msec
        {
            public void actionPerformed(ActionEvent e1) {
                vertical.validate();
                vertical.setValue(vertical.getMaximum());
            }
        });
        DocTimer.setRepeats(false);
        dlmx.addListDataListener(new ListDataListener() {

            public void intervalAdded(ListDataEvent e98) // creating method intervalRemoved
            {
                if (DocTimer.isRunning()) {
                    DocTimer.restart(); // if DocTimer is running means restart from start
                } else {
                    DocTimer.start(); // else start the timer
                }
            }
        });

        // creating pane,to display buttons
        JPanel BtnPane = new JPanel();
        BtnPane.setLayout(new BoxLayout(BtnPane, BoxLayout.X_AXIS));
        BtnPane.add(cnct);
        BtnPane.add(Box.createHorizontalStrut(120));
        BtnPane.add(sendNew);
        BtnPane.add(Box.createHorizontalStrut(10));
        BtnPane.add(miss_pkt);
        BtnPane.add(d_miss_pkt);
        BtnPane.add(Box.createHorizontalStrut(120));
        BtnPane.add(reset);
        BtnPane.add(Box.createHorizontalStrut(10));
        BtnPane.add(width);
        BtnPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        BtnPane.setBackground(Color.green);

        // creating pane,to display packets
        JPanel packetPane = new JPanel();
        packetPane.setBorder(BorderFactory.createEmptyBorder(80, 15, 80, 15));
        for (int i = 0; i < 12; i++) {
            packetPane.add(pkt[i]);
            packetPane.add(Box.createHorizontalStrut(5));
        }

        // functioning timer
        time.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e1) {
                if (timer.isRunning()) {
                    timer.stop(); // if Timer is running means stop the timer first
                    startTime = null; // make the startTime to 0 or null the value
                    time.setText("Start the Timer"); // enable it to "start the timer"
                } else {
                    startTime = LocalDateTime.now(); // if timer is not running means just start timer according
                    timer.start(); // to the LOcalDateTime
                    time.setText("Stop the Timer"); // set the text as Stop the timer as soon as it starts to work
                }
            }
        });
        timer = new Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent e3) {
                LocalDateTime now = LocalDateTime.now(); // obtains the LocalDate and Time according to present Zone
                Duration runningTime = Duration.between(startTime, now);
                Duration timeLeft = duration.minus(runningTime);
                if (timeLeft.isNegative() || timeLeft.isZero()) {
                    timeLeft = Duration.ZERO;
                    time.doClick();
                    GBN();
                }
                timeDsply.setText(String.format("00h 00m %02ds", timeLeft.toSeconds()));
            }
        });

        // adding all panes,to main frame
        jf.add(scrollArea, BorderLayout.LINE_START);
        jf.add(BtnPane, BorderLayout.PAGE_END); // adding ButtonPane to Page End
        jf.add(packetPane, BorderLayout.CENTER); // adding Packets to center of the page
        jf.add(TDsplyPanel, BorderLayout.PAGE_START); // adding TimeDisplayPanel to Page Start
        jf.setVisible(true); // everything on the JFrame should be visible
    }// constructor end

    public static void GBN() // creating a method called GBN
    {
        dlmx.addElement("TIMEOUT for packet no: " + base); // adding time out sentence in JList
        dlmx.addElement("Go-Back-N packets: " + base + "-" + (nextSeqNo - 1));

        sendNew.setVisible(false); // sendNew button is disabled
        miss_pkt.setEnabled(true); // making miss_pkt and d_miss_pkt enable for use
        d_miss_pkt.setEnabled(true);
        miss_pkt.setText("Miss Packets[" + base + " -" + (nextSeqNo - 1) + " ]");
        d_miss_pkt.setText("don't Miss Packets[" + base + " -" + (nextSeqNo - 1) + " ]");
        miss_pkt.setVisible(false);
        miss_pkt.setVisible(true);
        d_miss_pkt.setVisible(false);
        d_miss_pkt.setVisible(true);
        channel_length = channel_length + (nextSeqNo - base);
    }

    public static void implementing_GBN(boolean pktsMissed) // method implementing_GBN is created with ptsMissed as
                                                            // parameter
    {
        time.doClick();
        dlmx.addElement("Restarting Timer for packet no:" + base); // adding sentence into JList
        if (pktsMissed == false) {
            try // if packets are not missed means in case of exception (try will execute) or
                // else catch block executes
            {
                for (int i = base; i <= nextSeqNo - 1; i++) {
                    dos.writeInt(2); // writes an Integer to underlying output stream as 4bytes
                    dos.writeInt(i);
                }
                dlmx.addElement("packet no: " + base + "-" + (nextSeqNo - 1) + "had been sent");
            } catch (Exception e9) {
            }
        } else // if packet is missed means it will display that packet is lost in the network
        {
            dlmx.addElement("packet no: " + base + "-" + (nextSeqNo - 1) + " got missed in network");
        }
        miss_pkt.setVisible(false);
        d_miss_pkt.setVisible(false);
        miss_pkt.setText("Miss Packet");
        d_miss_pkt.setText("Don't Miss Packet");
        d_miss_pkt.setVisible(true);
        miss_pkt.setVisible(true);

    }

    public static void sendNewPressed(boolean pktMissed) {
        if (nextSeqNo < base + 4) {
            if (pktMissed == false) // if packet is not missed in the network means
            {
                try {
                    dos.writeInt(2);
                    dos.writeInt(nextSeqNo);
                    dlmx.addElement("packet no: " + (nextSeqNo) + "had been sent"); // shows that the numbered packet is
                                                                                    // sent

                } catch (Exception e9) {
                    dlmx.addElement("error sending packet no" + (nextSeqNo) + " ."); // any exception means error in
                                                                                     // sending message is displayed
                }
            } else {
                dlmx.addElement("packet no " + (nextSeqNo) + " got missed in network");

            }

            if (base == nextSeqNo) // if base value is equal to nextSeqNo means enable the timer
            {
                if (!time.isEnabled()) {
                    time.setEnabled(true);
                }
                time.doClick();
                dlmx.addElement("timer started for packet no: " + (nextSeqNo) + " .");

            }
            pkt[nextSeqNo].setVisible(false); // initially make next_pkt as not visible
            pkt[nextSeqNo].setBackground(Color.cyan); // change the Backgroundcolour to cyan
            pkt[nextSeqNo].setVisible(true); // after changing colour make it visible
            nextSeqNo++; // increment the nextseqNo
            channel_length++;
            seqNoLabel.setText("next Sequence no:" + nextSeqNo);
            if (nextSeqNo == base + 4) {
                sendNew.setEnabled(false);
            }
        } else {
            dlmx.addElement("sending request REJECTED-exceeding window size(4)");
        }
    }// func sendNewPressed end

    public void Application_Reset() // when an Rest button is clicked..this function is called
    {
        base = 0; // as the operation should starting from the starting make all base,nextseqNo=0
        nextSeqNo = 0;
        baseLabel.setText("base :" + base);
        channel_length = 0;
        seqNoLabel.setText("next Sequence no:" + nextSeqNo);
        dlmx.clear(); // clear the JPane
        if (timer.isRunning()) {
            time.doClick();
        }
        time.setEnabled(false);
        dlmx.addElement("tcp handshaking successful -8575 ");
        miss_pkt.setVisible(false);
        d_miss_pkt.setVisible(false);
        miss_pkt.setText("Miss Packet");
        d_miss_pkt.setText("Don't Miss Packet");
        miss_pkt.setEnabled(false);
        d_miss_pkt.setEnabled(false);
        miss_pkt.setVisible(true);
        d_miss_pkt.setVisible(true);
        timeDsply.setText("-- -- --");
        sendNew.setEnabled(true);
        sendNew.setVisible(true);
        for (int i = 0; i < 12; i++) // make all packets Background Color as white and ForeGround Color as black
        {
            pkt[i].setVisible(false);
            pkt[i].setForeground(Color.black);
            pkt[i].setBackground(Color.white);
            pkt[i].setVisible(true);
        }
        for (int z = 0; z < 4; z++) {
            pkt[z].setVisible(false);
            pkt[z].setForeground(Color.red); // as the size of window is 4 ...make first 4 packets as red Color
            pkt[z].setVisible(true);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cnct) // if the user click the button connect means this action is to be done
        {
            if (e.getActionCommand().equals("Connect")) {
                try {
                    S = new Socket("localhost", 8575); // giving Socket number and name of packet
                    dis = new DataInputStream(S.getInputStream()); // declaring dos and dis
                    dos = new DataOutputStream(S.getOutputStream());
                    dlmx.addElement("tcp handshaking successful -8575");
                    cnct.setText("Close Connection");
                    sendNew.setEnabled(true); // enabling sendNew and reset Buttons for usage
                    reset.setEnabled(true);
                } // try end
                catch (Exception ee) {
                    dlmx.addElement("tcp handshaking failed..");
                }
            } else // if action_command is not equal to "create a connection"
            {
                try {
                    dos.writeInt(1); // copy dos value as 1
                } catch (Exception eee) {
                    dlmx.addElement("error while closing connection.");
                }
                sendNew.setEnabled(false);
                reset.setEnabled(false);
                if (timer.isRunning()) {
                    time.doClick();
                }
                time.setEnabled(false);
                miss_pkt.setEnabled(false);
                d_miss_pkt.setEnabled(false);
                dlmx.addElement("Closing Socket...");
                cnct.setText("rerun,server & client code");
                cnct.setEnabled(false);
            }
        } else if (e.getSource() == sendNew) // if user clicks sendNew button means
        {
            sendNew.setVisible(false); // sendNew Button setVisible as false
            miss_pkt.setEnabled(true); // miss_pkt and d_miss_pkt need to be enabled
            d_miss_pkt.setEnabled(true);
        } else if (e.getSource() == miss_pkt) // if user clicks miss_pkt button
        {

            if (miss_pkt.getText().equals("Miss Packet")) {
                sendNewPressed(true); // call the function sendNewPressed and send parameter as pktsKilled=true
            } else {
                implementing_GBN(true); // call the function implementing_GBN also
            }
            sendNew.setVisible(true);
            miss_pkt.setEnabled(false);
            d_miss_pkt.setEnabled(false);
            width.setEnabled(true);
        } else if (e.getSource() == d_miss_pkt) // if d_miss_pkt button is clicked means
        {
            if (d_miss_pkt.getText().equals("Don't Miss Packet")) {
                sendNewPressed(false);
            } else {
                implementing_GBN(false);
            }
            width.setEnabled(true);
            sendNew.setVisible(true);
            miss_pkt.setEnabled(false);
            d_miss_pkt.setEnabled(false);
        } else if (e.getSource() == reset) {
            try {
                dos.writeInt(3);
            } catch (Exception e3) {
            }
            Application_Reset(); // call the applicationReset function
        } else if (e.getSource() == width) {
            dlmx.addElement("band width=" + channel_length);
        }

    }// func actionPerformed end

    public static void updateBase(int old_base, int new_base) // creating a method called updateBase
    {

        for (int i = new_base; (i < new_base + 4) && (i < 12); i++) {
            pkt[i].setForeground(Color.red); // the packet which satisfies the logic make it as red color
        }
        for (int i = old_base; i <= new_base - 1; i++) {
            pkt[i].setVisible(false); // packets from the old base to new base-1 make background yellow
            pkt[i].setBackground(Color.yellow);
            pkt[i].setForeground(Color.black);
            pkt[i].setVisible(true);
            channel_length = channel_length - 1;
        }
        if (!sendNew.isEnabled() && (nextSeqNo - new_base < 4)) {
            sendNew.setEnabled(true); // if sendNew button is enabled and nextSeqNo-new_base <4 enable sendNew
        }
        baseLabel.setText("base :" + new_base);
    }

    public static void main(String[] args) throws Exception {
        new sender();
        // receving ack
        int i = 0;
        while (true) {
            try {
                i = dis.readInt();
                if (miss_pkt.getText().equals("Miss Packet")) {
                    dlmx.addElement("acknowledgement" + i + " received.");
                    updateBase(base, i + 1); // call the method updateBase with parameters base,i+1
                    base = i + 1; // make the base equal to incremented value of i

                    if (base == nextSeqNo) {
                        time.doClick();
                        time.setEnabled(false);
                        dlmx.addElement("Stoping Timer");/* stop timmer */
                    } else {
                        time.doClick();
                        time.doClick();
                        dlmx.addElement("Restarting Timer");
                    }
                } else {
                    dlmx.addElement("ack: " + i + " discarded, as GBN procedure is in progress");
                }

            } catch (Exception e) {
            }

        }
    }
}
