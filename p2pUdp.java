package org.example;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.text.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;


interface myEventListener{
    // send messages to other peer
    void onAction(NewJFrame1 x,DatagramSocket Socket);
    // setting the chat and receive messages from other peer
    void onAction1(NewJFrame1 x,DatagramSocket Socket);
    // delete selected msg
    void onAction2(NewJFrame1 x,DatagramSocket Socket);
    void onAction4(NewJFrame1 newJFrame1, DatagramSocket socket,MouseEvent w);
    // delete all chat for both
    void onAction3(NewJFrame1 x, DatagramSocket Socket);
}
public class p2pUdp implements myEventListener {
    //private static String lineText = null;
    private DatagramSocket socket;
    private ArchiveFrame archiveFrame;
    public p2pUdp(DatagramSocket socket, ArchiveFrame archiveFrame) {
        this.socket = socket;
        this.archiveFrame = archiveFrame;
        
    }

    public void deleteMessage(String message) {
        // Archive the message before deleting
        archiveFrame.archiveMessage(message);}
    public void start(NewJFrame1 x) {
        Font font = new Font("Monospaced", Font.PLAIN, 12);
        x.get_TextPane().setFont(font);
        x.get_TextPane().setEditable(false);
    }
    public void onAction(NewJFrame1 x,DatagramSocket Socket) {
        //check for empty
        boolean checkEmpty=((x.get_username().getText().isEmpty())||(x.get_local_IP().getText().isEmpty())
                ||(x.get_local_port().getText().isEmpty())||(x.get_remote_IP().getText().isEmpty())
                ||((x.get_remote_port()).getText().isEmpty()));
        // if all are filled start the process
        if(!checkEmpty){
            try{
                //get info from gui
                String remoteIP = x.get_remote_IP().getText();
                int remotePort = Integer.parseInt(x.get_remote_port().getText());
                String message= x.get_message().getText();
                message+=","+x.get_username().getText();
                // Add timestamp to the message
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                message = timestamp + " " + message;
                // Append the message to the sender in BLUE
                String textToAppend = "\n"+ timestamp + " Me: " + message.split(",")[0].substring(20);
                x.appendBLUEText(x.get_TextPane(), textToAppend);
                // Create a new pkt with the msg data and send it to the other peer
                byte[] buffer = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(remoteIP), remotePort);
                Socket.send(packet);
            }
            catch(Exception e){

            }
        }
        else{
            JOptionPane.showMessageDialog(null,"Please Make Sure to fill the source & destination IPs and Ports");
        }
    }
    public void onAction1(NewJFrame1 x, DatagramSocket Socket) {
        // check for empty
        boolean checkEmpty = ((x.get_username().getText().isEmpty()) || (x.get_local_IP().getText().isEmpty())
                || (x.get_local_port().getText().isEmpty()));
        // if all are filled start the process
        if (!checkEmpty) {
            String localIP = x.get_local_IP().getText();
            String localPort = x.get_local_port().getText();
    
            try {
                // socket for udp connection
                DatagramSocket socket = new DatagramSocket(Integer.parseInt(localPort), InetAddress.getByName(localIP));
                x.get_status().setText("UDP P2P Chat started on " + localIP + ":" + localPort);
    
                // thread to continuously receive packets
                Thread receiveThread = new Thread(() -> {
                    try {
                        while (true) {
                            // receive pkts
                            byte[] buffer = new byte[1024];
                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                            socket.receive(packet);
                            // process the msg
                            String receivedM = new String(packet.getData(), 0, packet.getLength());
    
                            // Display received IP and port in the status bar
                            String senderIP = packet.getAddress().getHostAddress();
                            int senderPort = packet.getPort();
                            x.get_status().setText("Received from IP: " + senderIP + " Port: " + senderPort);
    
                            // Handle the received message
                            if (receivedM.equals("DELETE ALL")) {
                                x.get_TextPane().setText("");
                                x.appendBLUEText(x.get_TextPane(), "*** Chat Deleted ***");
                            } else if (receivedM.startsWith("DELETE ")) {
                                String messageToDelete = receivedM.substring(7);
                                StyledDocument doc = x.get_TextPane().getStyledDocument();
                                Element root = doc.getDefaultRootElement();
    
                                for (int i = 0; i < root.getElementCount(); i++) {
                                    Element lineElement = root.getElement(i);
                                    int startOffset = lineElement.getStartOffset();
                                    int endOffset = lineElement.getEndOffset() - 1;
    
                                    try {
                                        String lineText = doc.getText(startOffset, endOffset - startOffset);
                                        if (lineText.contains(messageToDelete)) {
                                            doc.remove(startOffset, endOffset - startOffset);
                                            doc.insertString(startOffset, "*** Deleted Message ***", null);// Remove the line
                                            break;
                                        }
                                    } catch (BadLocationException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                String[] MESSAGE = receivedM.split(",");
                                String message = "";
                                for (int i = 0; i < MESSAGE.length - 1; i++) {
                                    message += MESSAGE[i];
                                }
                                String username = MESSAGE[MESSAGE.length - 1];
                                String timestamp = message.substring(0, 19);
                                message = message.substring(20);
    
                                // Append received message in RED
                                String textToAppend = "\n" + timestamp + " " + username + ": " + message;
                                x.appendREDText(x.get_TextPane(), textToAppend);
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error receiving message: " + e.getMessage());
                    }
                });
                // thread for receiving messages
                receiveThread.start();
    
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "Please Make Sure to fill your IP, your port \nand enter a username to instantiate a socket");
        }
    }
    
    int lastClickedLine = -1;
    String lineText;
    StyledDocument doc;
    Element root;
    int startOffset, endOffset;




    public void onAction2(NewJFrame1 x, DatagramSocket socket) {
        try {
            if (lastClickedLine >= 0) {
                doc = x.get_TextPane().getStyledDocument();
                root = doc.getDefaultRootElement();
                startOffset = root.getElement(lastClickedLine).getStartOffset();
                endOffset = root.getElement(lastClickedLine).getEndOffset() - 1; // Adjust end offset
                lineText = doc.getText(startOffset, endOffset - startOffset);
    
                if (lineText.contains("Me")) {
                    // Archive the message before deleting
                    String timestamp = lineText.substring(0, 19);
                    String message = lineText.substring(20);
                    // Ensure this is the correct ArchiveFrame instance
                    archiveFrame.archiveMessage(timestamp + " " + x.get_username().getText() + ": " + message);
    
                    try {
                        doc.remove(startOffset, endOffset - startOffset); // Remove existing line
                        doc.insertString(startOffset, "*** Deleted Message ***", null); // Insert new text
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
    
                    // Delete the message from the other peer's
                    lineText = lineText.replace("Me", x.get_username().getText());
                    byte[] data = ("DELETE " + lineText).getBytes();
                    DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(x.get_remote_IP().getText()), Integer.parseInt(x.get_remote_port().getText()));
                    socket.send(packet);
                } else {
                    JOptionPane.showMessageDialog(null, "You can only delete the message you sent!!");
                }
            } else {
                System.out.println("No message has been selected yet.");
            }
        } catch (BadLocationException | IOException ex) {
            ex.printStackTrace();
        }
    }




    @Override
    public void onAction3(NewJFrame1 x, DatagramSocket Socket){
        try{
            // delete chat for local
            x.get_TextPane().setText(" ");
            // Notify the other peer chat that the all chat will be deleted
            byte[] data = ("DELETE ALL").getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(x.get_remote_IP().getText()), Integer.parseInt(x.get_remote_port().getText()));
            // Send the pkt
            socket.send(packet);
        }
        catch(Exception exp){

        }
    }

    @Override
    public void onAction4(NewJFrame1 x, DatagramSocket socket, MouseEvent e) {
        try {
            int clickedOffset = x.get_TextPane().viewToModel(e.getPoint());
            int lineNumber = x.get_TextPane().getDocument().getDefaultRootElement().getElementIndex(clickedOffset);
            lastClickedLine = lineNumber;

            Element root = x.get_TextPane().getDocument().getDefaultRootElement();
            Element lineElement = root.getElement(lineNumber);
            int startOffset = lineElement.getStartOffset();
            int endOffset = lineElement.getEndOffset() - 1; // Adjust end offset
            lineText = x.get_TextPane().getDocument().getText(startOffset, endOffset - startOffset);
            //System.out.println("Clicked line: " + lineText);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

             public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket();
            ArchiveFrame archiveFrame = new ArchiveFrame(); // Create the archive frame
          // This should be outside of any ArchiveFrame class context
   NewJFrame1 frame = new NewJFrame1(new p2pUdp(socket, archiveFrame), socket);

            frame.pack();
            frame.setVisible(true);
            ((p2pUdp)frame.getEventListener()).start(frame);
        } 
        catch (SocketException e) {
            System.err.println("Error creating socket: " + e.getMessage());
        }
    }
    

}




class ArchiveFrame extends JFrame {
    private JList<String> messageList;
    private DefaultListModel<String> listModel;
    private ConcurrentHashMap<String, Timer> messageTimers;
    private JButton restoreButton;
    private JTextArea textArea;

    public ArchiveFrame() {
        listModel = new DefaultListModel<>();
        messageList = new JList<>(listModel);
        messageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(messageList);
        restoreButton = new JButton("Restore Selected Message");
        restoreButton.addActionListener(e -> restoreSelectedMessage());
        messageTimers = new ConcurrentHashMap<>();
        JPanel panel = new JPanel();

        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(restoreButton, BorderLayout.SOUTH);

        this.add(panel);
        setTitle("Archived Messages");
        pack();

        setSize(300, 400);
        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea)); // Add scrolling
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    public void archiveMessage(String message) {
        listModel.addElement(message);
        scheduleMessageDeletion(message);
    }

    // New method to handle deletion and archiving
    public void deleteAndArchiveMessage(String message) {
        // This method simulates deletion by directly archiving the message.
        archiveMessage(message);
    }

    private void scheduleMessageDeletion(String message) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                removeArchivedMessage(message);
            }
        }, 120000); // 120000 milliseconds = 2 minutes
        messageTimers.put(message, timer);
    }

    private void removeArchivedMessage(String message) {
        listModel.removeElement(message);
        messageTimers.remove(message);
    }

    private void restoreSelectedMessage() {
        String selectedMessage = messageList.getSelectedValue();
        if (selectedMessage == null) {
            JOptionPane.showMessageDialog(this, "No message selected for restoration.");
            return;
        }
        Timer timer = messageTimers.get(selectedMessage);
        if (timer != null) {
            timer.cancel();
            messageTimers.remove(selectedMessage);
        }
        listModel.removeElement(selectedMessage);
    }
}

//gui
class NewJFrame1 extends JFrame {
    private JButton archiveButton;
    private ArchiveFrame archiveFrame;
    private JButton jButton1;
    private JButton jButton2;
    private JButton jButton3;
    private JButton jButton4;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JScrollPane jScrollPane1;
    private JTextPane textPane ;
    private final JTextField jTextField1;
    private final JTextField jTextField2;
    private final JTextField jTextField3;
    private final JTextField jTextField4;
    private final JTextField jTextField5;
    private final JTextField jTextField6;
    private final JTextField jTextField7;
    private final myEventListener listener;

    public NewJFrame1(myEventListener listener , DatagramSocket socket) {
        this.listener=listener;

        archiveFrame = new ArchiveFrame();
        archiveButton = new JButton("Archive");
        archiveButton.addActionListener(e -> {
            archiveFrame.setVisible(true);   });
            getContentPane().add(archiveButton);
            pack();
            
        jScrollPane1 = new JScrollPane();
        jTextField1 = new JTextField();
        jLabel1 = new JLabel();
        jTextField2 = new JTextField();
        jLabel2 = new JLabel();
        jTextField3 = new JTextField();
        jButton2 = new JButton();
        jButton1 = new JButton();
        jButton3 = new JButton();
        jButton4 = new JButton();
        jLabel3 = new JLabel();
        jTextField4 = new JTextField();
        jTextField5 = new JTextField();
        jLabel4 = new JLabel();
        jTextField6 = new JTextField();
        jLabel5 = new JLabel();
        jTextField7 = new JTextField();
        jLabel6 = new JLabel();
        textPane = new JTextPane();
        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        textPane.setPreferredSize(new Dimension(200, 100));
        textPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                listener.onAction4(NewJFrame1.this,socket, e);
            }
        });
        jScrollPane1.setViewportView(textPane);
        jLabel1.setText("username:");

        jTextField2.setForeground(new Color(153, 153, 153));
        jTextField2.setText("enter text here");
        jTextField2.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                jTextField2FocusGained();
            }
        });

        jLabel2.setText("Status:");
        jButton2.setBackground(new Color(204, 204, 204));
        jButton2.setText("test Button");
        jButton2.addActionListener(e -> {
            // Call the event listener method when the button is clicked
            listener.onAction1(NewJFrame1.this, socket);
        });
        jButton1.setBackground(new Color(204, 204, 204));
        jButton1.setText("Send");
        jButton1.addActionListener(e -> {
            // Call the event listener method when the button is clicked
            listener.onAction(NewJFrame1.this, socket);
        });
        jButton3.setBackground(new java.awt.Color(204, 204, 204));
        jButton3.setText("Delete");
        jButton3.setActionCommand("deleteButton");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Call the event listener method when the button is clicked
                listener.onAction2(NewJFrame1.this, socket);
            }
        });
        jButton4.setBackground(new java.awt.Color(204, 204, 204));
        jButton4.setText("Delete All");
        jButton4.setActionCommand("deleteButton");
        jButton4.addActionListener(evt -> listener.onAction3(NewJFrame1.this, socket));

        jLabel3.setText("Remote Port :");

        jLabel4.setText("Remote IP :");

        jLabel5.setText("Local Port :");

        jLabel6.setText("Local IP :");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addGap(2, 2, 2)
                                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jButton3))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(layout.createSequentialGroup()
                                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(layout.createSequentialGroup()
                                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(layout.createSequentialGroup()
                                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jButton2)))
                                .addGap(46, 46, 46))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel1)
                                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(jButton3)
                                                .addComponent(jButton4)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel6)
                                                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel5)
                                                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(25, 25, 25)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel4)
                                                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel3)
                                                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(jButton2)
                                                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel2)
                                                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addContainerGap(29, Short.MAX_VALUE))
        );


    }






    


    public JTextField get_local_IP(){
        return jTextField7;
    }
    public JTextField get_local_port(){
        return jTextField6;
    }
    public JTextField get_remote_IP(){
        return jTextField5;
    }
    public JTextField get_remote_port(){
        return jTextField4;
    }
    public JTextField get_username(){
        return jTextField1;
    }
    public JTextField get_message(){
        return jTextField2;
    }
    public JTextField get_status(){
        return jTextField3;
    }
    public myEventListener getEventListener() {
        return listener;
    }




    


   
    
    public JTextPane get_TextPane() {
        return textPane;
    }
    private void jTextField2FocusGained() {
        if(!(jTextField2.getText().equals(""))){
            jTextField2.setText("");
        }
    }
    public void appendREDText(JTextPane textPane, String text) {
        StyledDocument doc = textPane.getStyledDocument();
        Style style = textPane.addStyle("REDStyle", null);
        StyleConstants.setForeground(style, Color.RED);

        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
   
    public void appendBLUEText(JTextPane textPane, String text) {
        StyledDocument doc = textPane.getStyledDocument();
        Style style = textPane.addStyle("BLUEStyle", null);
        StyleConstants.setForeground(style, Color.BLUE);

        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

}






