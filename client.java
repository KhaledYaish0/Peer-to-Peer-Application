package network_part2;

// Importing necessary Java libraries for GUI components, networking, I/O operations, and more.
import java.awt.*;
import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Client class that extends JFrame to create a GUI application.
public class client extends javax.swing.JFrame {

    // Variables for data input stream and threads for networking.
    DataInputStream din;
    // Custom thread for handling network tasks.
    mythread net;
    // Another custom thread for sending data.
    sendThread net1;
    // Model for managing a list in the GUI.
    DefaultListModel dlm;
    // Variable to keep track of the selected user in the GUI.
    String selectedUser;
    // Array of port numbers for peers.
    static int[] peers = {1111, 2222, 3333, 4444};
    // List to store names of peers.
    static ArrayList<String> names = new ArrayList<>();
    // List to store port numbers of peers.
    static ArrayList<Integer> ports = new ArrayList<>();
    // Map to associate port numbers with names.
    static Map<Integer, String> map = new HashMap<>();
    // List to be used for unspecified purposes (not initialized or used in the provided code).
    List<String> l;
    // An array to possibly keep track of something related to peers, not used in the provided snippet.
    static int[] pp = {0, 0, 0};
    // Variable to store the port number for this client.
    int myPort;
    // Variable to store the name of this client.
    String myName;
    // Global index for cycling through colors
    private static int colorIndex = 0; // Global index for cycling through colors
    private static final Map<String, Color> userColorMap = new HashMap<>();


    // Constructor for the client class.
    public client() {
        initComponents();
        dlm = new DefaultListModel();
        UL.setModel(dlm);

        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                // For each network interface, get a list of its IP addresses.
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        // Check if the IP address belongs to certain subnets (assumed to be usable server IPs).
                        if (sAddr.contains("192.168.1") || sAddr.contains("172.17.") || sAddr.contains("172.26.")) {//possible ip addresses
                            // Set text fields in the GUI to show these IP addresses.
                            lip.setText(sAddr);
                            dip.setText(sAddr);
                            serverIp.setText(sAddr);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Attempt to start a DatagramSocket for peer-to-peer communication.
        try {
            DatagramSocket p2p = new DatagramSocket(myPort);
            net = new mythread(p2p);
            net.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {
        name = new JTextField();
        st = new JTextField();
        JLabel jLabel10 = new JLabel();
        JScrollPane jScrollPane3 = new JScrollPane();
        UL = new JList<>();
        JLabel jLabel3 = new JLabel();
        JLabel jLabel4 = new JLabel();
        serverIp = new JTextField();
        serverPort = new JTextField();
        JLabel jLabel5 = new JLabel();
        JLabel jLabel6 = new JLabel();
        JLabel jLabel7 = new JLabel();
        JLabel jLabel8 = new JLabel();
        JLabel jLabel1 = new JLabel();
        JScrollPane jScrollPane1 = new JScrollPane();
        //A1 = new javax.swing.JTextArea();
        textPane = new JTextPane();
        msg = new JTextField();
        JLabel jLabel2 = new JLabel();
        lp = new JTextField();
        JLabel jLabel9 = new JLabel();
        dip = new JTextField();
        lip = new JTextField();
        dp = new JTextField();
        JButton login = new JButton();
        JButton send = new JButton();
        JButton jButton2 = new JButton();
        JButton text = new JButton();
        JComboBox<String> jComboBox1 = new JComboBox<>();
        JLabel jLabel11 = new JLabel();
        password = new JTextField();
        JButton all = new JButton();
        lastLoginTimeLabel = new JLabel("Last Login: Not Available");
        lastLoginTimeLabel.setFont(new java.awt.Font("Tahoma", 0, 14));

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        name.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        name.addActionListener(this::nameActionPerformed);

        jLabel10.setFont(new java.awt.Font("Thoma", Font.BOLD, 14)); // NOI18N
        jLabel10.setText("Online Users");

        UL.addListSelectionListener(this::ULValueChanged);
        jScrollPane3.setViewportView(UL);

        jLabel3.setText("TCP Server IP :");

        jLabel4.setText("TCP Server Port :");

        serverIp.setText("  ");
        serverIp.addActionListener(this::serveripActionPerformed);

        serverPort.setText("8000");

        jLabel5.setText("Available Interfaces");

        jLabel6.setText(" Local IP:");

        jLabel7.setText(" Local Port:");

        jLabel8.setText("Remote IP :");

        jLabel1.setFont(new java.awt.Font("Thoma", Font.PLAIN, 14)); // NOI18N
        jLabel1.setText("Username:");

        textPane.setEditable(false);
        textPane.setPreferredSize(new Dimension(200, 100));
        jScrollPane1.setViewportView(textPane);

        msg.setFont(new java.awt.Font("Thoma", Font.PLAIN, 14)); // NOI18N
        msg.setForeground(new Color(153, 153, 153));
        msg.setText("enter text here");
        msg.setCursor(new Cursor(java.awt.Cursor.TEXT_CURSOR));
        msg.setMargin(new Insets(-50, 2, 0, 0));
        msg.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                msgFocusGained(evt);
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                msgFocusLost(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Thoma", Font.PLAIN, 14)); // NOI18N
        jLabel2.setText("Status:");

        lp.addActionListener(this::lpActionPerformed);

        jLabel9.setText("Remote Port ");

        lip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lipActionPerformed(evt);
            }
        });

        dp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dpActionPerformed(evt);
            }
        });

        login.setLabel("Login");
        login.setMaximumSize(new java.awt.Dimension(75, 35));
        login.setMinimumSize(new java.awt.Dimension(75, 35));
        login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginActionPerformed(evt);
            }
        });

        send.setFont(new java.awt.Font("Thoma", Font.PLAIN, 18)); // NOI18N
        send.setText("Send");
        send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendActionPerformed(evt);
            }
        });

        jButton2.setLabel("Logout");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        text.setLabel("test Button");
        text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"wi-fi :192.168.1.9", "Ethernet: 172.17.0.1", "loopback Pseudo-Interface 1: fe80::b5da:5dc7:27aa:7afe%11"}));
        jComboBox1.addActionListener(this::jComboBox1ActionPerformed);

        jLabel11.setFont(new java.awt.Font("Thoma", Font.PLAIN, 14)); // NOI18N
        jLabel11.setText("Password:");

        all.setText("Send to all");
        all.addActionListener(this::allActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(msg, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(3, 3, 3)
                                                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(3, 3, 3)
                                                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(10, 10, 10)
                                                                .addComponent(lip, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(3, 3, 3)
                                                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(lp, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(3, 3, 3)
                                                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(4, 4, 4)
                                                                .addComponent(dip, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(22, 22, 22)
                                                                .addComponent(send)
                                                                .addGap(26, 26, 26)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                        .addComponent(text, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                                                                        .addComponent(all, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(73, 73, 73)
                                                                .addComponent(dp, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(lastLoginTimeLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(st, javax.swing.GroupLayout.PREFERRED_SIZE, 461, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(password, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                                                        .addComponent(name))
                                                .addGap(30, 30, 30)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(login, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(26, 26, 26)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(39, 39, 39)
                                                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(serverIp, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                                                        .addComponent(serverPort))
                                                .addGap(66, 66, 66)
                                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(63, 63, 63))))
        );
        
        
        
        
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(15, 15, 15)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(name, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(13, 13, 13)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(serverIp, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                                .addGap(8, 8, 8)
                                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(56, 56, 56)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGap(1, 1, 1)
                                                                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addComponent(serverPort, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(5, 5, 5))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addComponent(login, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jLabel11)
                                                                        .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(msg, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGap(6, 6, 6)
                                                                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addComponent(lip, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(12, 12, 12)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(lp, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(11, 11, 11)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGap(2, 2, 2)
                                                                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addComponent(dip, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(3, 3, 3)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGap(3, 3, 3)
                                                                                .addComponent(dp, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                .addGap(18, 18, 18)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addComponent(all)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                .addComponent(text))
                                                                        .addComponent(send, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lastLoginTimeLabel))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(st, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nameActionPerformed

    private void ULValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_ULValueChanged
        // TODO add your handling code here:

        if (UL.getSelectedIndices().length == 1) {
            selectedUser = UL.getSelectedValue();
            l = UL.getSelectedValuesList();
            String[] array = selectedUser.split("-");
            dip.setText(array[1]);
            dp.setText(array[2]);
        } else {
            l = UL.getSelectedValuesList();
        }

    }//GEN-LAST:event_ULValueChanged

    private void msgFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_msgFocusGained
        // TODO add your handling code here:
        if (msg.getText().equals("enter text here")) {
            msg.setText("");
            msg.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_msgFocusGained

    private void msgFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_msgFocusLost
        // TODO add your handling code here:
        if (msg.getText().equals("")) {
            msg.setText("enter text here");
            msg.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_msgFocusLost

    private void lpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lpActionPerformed

    private void lipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lipActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lipActionPerformed

    private void dpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dpActionPerformed
    
    
    
    // ---------------------------------------------------------- //
    
    // list of colors to be used for different users.
    private static final Color[] userColors = {
    Color.BLUE, Color.MAGENTA, Color.ORANGE, Color.DARK_GRAY, Color.CYAN, Color.PINK, Color.YELLOW, Color.GREEN, Color.RED
    };

    private static Color getColorForUser(String username) {
         // Check if the user already has a color assigned
    if (!userColorMap.containsKey(username)) {
        // Assign the next color from the array
        Color color = userColors[colorIndex % userColors.length];
        userColorMap.put(username, color);
        colorIndex++; // Move to the next color for the next new user
    }
    return userColorMap.get(username);
    }

    // ---------------------------------------------------------- //

    
    
    private void loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginActionPerformed

        try {
            // Open and read from the "credential.txt" file.
            File myObj = new File("credential.txt");
            Scanner myReader = new Scanner(myObj);

            // Reading each line from the file and splitting it by "," to separate username and password.
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] nameAndpass = data.split(",");
                String name = nameAndpass[0].toLowerCase();
                String password = nameAndpass[1].toLowerCase();
                // Storing each username and password pair in a map for later verification.
                mapOfNamePass.put(name, password);
            }
            // Closing the file reader.
            myReader.close();
            
            //Traversing the map to print out all username and password pairs for debugging purposes.
            Set set = mapOfNamePass.entrySet();//Converting to Set so that we can traverse
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                //Converting to Map.Entry so that we can get key and value separately
                Map.Entry entry = (Map.Entry) itr.next();
                System.out.println(entry.getKey() + ".." + entry.getValue());
            }

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        ///////////
        System.out.println("reached444");
        try {
            // Flag to check if the user is registered.
            boolean register = false;
            System.out.println("reached333");
            //Re-using the set to traverse the map again for login validation.
            Set set = mapOfNamePass.entrySet();//Converting to Set so that we can traverse

            //Iterating through the set to find a match for the username and password entered by the user.
            for (Object o : set) {
                System.out.println("reached122");
                Map.Entry entry = (Map.Entry) o;
                System.out.println((entry.getKey().toString().trim().compareTo(name.getText().toString().trim())) + " " + (entry.getValue().toString().trim().compareTo(password.getText().trim())));
                //Comparing the input username and password with each entry in the map.
                if ((entry.getKey().toString().trim().equalsIgnoreCase(name.getText().trim()))
                        && (entry.getValue().toString().trim().equalsIgnoreCase(password.getText().trim()))) {
                    register = true; //If a match is found, set register to true
                    JOptionPane.showMessageDialog(this, "logged in successfully\n");
                    break; //Exit the loop as we have found a valid user
                }
            }

            //If no valid user was found, display an error message
            if (!register) {
                JOptionPane.showMessageDialog(this, "invalid login information, either user name or password\n");
            } else {

                String id = name.getText() + "-" + lip.getText() + "-" + lp.getText();
                Socket s = new Socket(serverIp.getText().trim(), Integer.parseInt(serverPort.getText()));
                DataInputStream din = new DataInputStream(s.getInputStream());
                DataOutputStream data_out = new DataOutputStream(s.getOutputStream());
                data_out.writeUTF(id);
                String in = new DataInputStream(s.getInputStream()).readUTF();
                if (in.equals("you are Already Registered!")) {
                    JOptionPane.showMessageDialog(this, "You've  Already Registered....!!\n");
                } else {
                    
                    DateTimeFormatter dtfs = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    String currentTime = dtfs.format(LocalDateTime.now());
                    lastLoginTimeLabel.setText(currentTime);

                    
                    // boolean flag=false;
                    String port = lp.getText();
                    myPort = Integer.parseInt(port);

                    myName = name.getText();
             
                    st.setText("welcome " + name.getText() + " on port: " + lp.getText() + " ip: " + lip.getText());
                    new Read(s, id).start();

                    try {
                        DatagramSocket p2p = new DatagramSocket(myPort);
                        net = new mythread(p2p);
                        net.start();
                    } catch (Exception e) {
                        Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, e);
                    }

                }
            }
        } catch (Exception ex) {

            ex.printStackTrace();

        }

    }//GEN-LAST:event_loginActionPerformed

    private void sendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendActionPerformed
        try {
            DatagramSocket clientSocket = new DatagramSocket();

            String dst_ip = dip.getText();     //destination ip
            InetAddress IPAddress = InetAddress.getByName(dst_ip);
            net1 = new sendThread(clientSocket);
            net1.start();

        } catch (Exception ex) {
            Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_sendActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            // TODO add your handling code here:
            String id = name.getText() + "-" + lip.getText() + "-" + lp.getText();
            Socket s = new Socket(serverIp.getText().trim(), Integer.parseInt(serverPort.getText()));
            DataOutputStream data_out = new DataOutputStream(s.getOutputStream());
            data_out.writeUTF("logout:" + id);
            this.dispose();
        } catch (IOException ex) {
            Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textActionPerformed
        // TODO add your handling code here:
        boolean flag = false;
        String port = lp.getText();
        myPort = Integer.parseInt(port);
        for (int i = 0; i < 4; i++) {
            if (myPort == peers[i]) {
                flag = true;
                break;
            }
        }
        if (flag) {
            myName = name.getText();
            map.put(myPort, myName);
            ports.add(Integer.parseInt(lp.getText()));
            names.add(name.getText());
            st.setText("welcome " + myName + " on port: " + myPort);
            int k = 0;
            for (int i = 0; i < 4; i++) {
                if (peers[i] != myPort) {

                    pp[k] = peers[i];
                    k++;
                }
            }

            name.setText(myName);
            String p = String.valueOf(myPort);
            lp.setText(p);
            try {
                DatagramSocket p2p = new DatagramSocket(myPort);
                net = new mythread(p2p);
                net.start();
            } catch (Exception e) {
                //Logger.getLogger(network.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "please enter a new port because this port not in the system", port, HEIGHT);
        }
    }//GEN-LAST:event_textActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void serveripActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serveripActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_serveripActionPerformed

    private void allActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allActionPerformed
        // TODO add your handling code here:
        try {
            DatagramSocket clientSocket = new DatagramSocket();

            net1 = new sendThread(clientSocket);
            net1.start();

        } catch (Exception ex) {
            Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_allActionPerformed

    static Map<String, String> mapOfNamePass = new HashMap<>();

    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(() -> new client().setVisible(true));

    }

    public class sendThread extends Thread {

        DatagramSocket rp1;
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        public sendThread(DatagramSocket ds) {
            rp1 = ds;
        }

        @Override
        public void run() {
            try {
                byte[] sendData = new byte[1024];

                String mess = msg.getText() + ' ';
                msg.setText("");
                int ReceiverPort = 0;
                int flag = 1;
                if (!dp.getText().equals("")) {
                    if (l.size() == 1) {
                        // qais---------------------------------------------------------- //
                        Style style1 = textPane.addStyle("UserStyle", null);
                        StyleConstants.setForeground(style1, getColorForUser(myName));
                        // qais---------------------------------------------------------- //
                        // Append the message to the JTextPane
                        StyledDocument doc = textPane.getStyledDocument();
                        //////Style style = textPane.addStyle("RedStyle", null);
                        //////StyleConstants.setForeground(style, Color.RED);
                        String textToAppend = "Me: " + mess + "from " + myPort + " " + timestamp + "\n";
                        try {
                            // qais---------------------------------------------------------- added style1 instead of style //
                            doc.insertString(doc.getLength(), textToAppend, style1);
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }

                        ReceiverPort = Integer.parseInt(dp.getText());
                    } else {
                        for (int i = 0; i < l.size(); i++) {
                            String str = l.get(i);
                            String array[] = str.split("-");
                            InetAddress ip = InetAddress.getByName(array[1]);
                            mess = mess + "-" + myName + "-" + flag + "-" + myPort + ' ';

                            sendData = mess.getBytes();
                            DatagramPacket sendPacket
                                    = new DatagramPacket(sendData, sendData.length, ip, Integer.parseInt(array[2]));
                            rp1.send(sendPacket);
                            flag = 3;

                        }

                    }
                } else {
                    // Append the message to the JTextPane
                    StyledDocument doc = textPane.getStyledDocument();
                    // qais---------------------------------------------------------- //
                    Style style1 = textPane.addStyle("UserStyle", null);
                    StyleConstants.setForeground(style1, getColorForUser(myName));
                    // qais---------------------------------------------------------- //
                    /////////Style style = textPane.addStyle("RedStyle", null);
                    /////////StyleConstants.setForeground(style, Color.RED);
                    String textToAppend = "Me to all: " + mess + "from " + myPort + " " + timestamp + "\n";
                    
                    try {
                        doc.insertString(doc.getLength(), textToAppend, style1);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                    flag = 2;
                }

                mess = mess + "-" + myName + "-" + flag + "-" + myPort + ' ';
                String dst_ip = dip.getText();     //destination ip
                InetAddress IPAddress = InetAddress.getByName(dst_ip);

                sendData = mess.getBytes();
                if (flag == 1) {
                    DatagramPacket sendPacket
                            = new DatagramPacket(sendData, sendData.length, IPAddress, ReceiverPort);
                    rp1.send(sendPacket);
                    st.setText("Send to: IP:" + dip.getText() + " Port:" + ReceiverPort);
                } else if (flag == 2) {
                    for (int i = 0; i < dlm.getSize(); i++) {
                        String ii = (String) dlm.get(i);
                        String[] array = ii.split("-");
                        InetAddress ip = InetAddress.getByName(array[1]);
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, Integer.parseInt(array[2]));
                        rp1.send(sendPacket);
                    }
                    st.setText("Send to all");
                }

            } catch (Exception a) {
                //System.out.println(a.getMessage());
            }

        }
    }

    //receiving data
    private class mythread extends Thread {

        DatagramSocket RP;
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        mythread(DatagramSocket d) {
            RP = d;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    RP.receive(receivePacket);
                    String sentence = null;
                    sentence = new String(receivePacket.getData());

                    String rip = receivePacket.getAddress().toString();

                    //get name and message
                    String[] recived = sentence.split("-");
                    String senderName = recived[1]; //////////////// qais
                    String rport = recived[3];
                    if (recived[2].trim().equals("2")) {
                        // Append the message to the JTextPane
                        StyledDocument doc = textPane.getStyledDocument();
                        // qais---------------------------------------------------------- //
                        Style style = textPane.addStyle("UserStyle", null);
                        StyleConstants.setForeground(style, getColorForUser(senderName));
                        // qais---------------------------------------------------------- //
                        /////Style style = textPane.addStyle("GreenStyle", null);
                        /////StyleConstants.setForeground(style, Color.GREEN);

                        String textToAppend = recived[1] + " :" + recived[0] + " from " + rport + timestamp + "\n";
                        try {
                            doc.insertString(doc.getLength(), textToAppend, style);
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                        st.setText("Received from:IP= " + rip.trim() + " ,Port=" + rport + " to all.");

                    } else {
                        // Append the message to the JTextPane
                        StyledDocument doc = textPane.getStyledDocument();
                        // qais---------------------------------------------------------- //
                        Style style = textPane.addStyle("UserStyle", null);
                        StyleConstants.setForeground(style, getColorForUser(senderName));
                        // qais---------------------------------------------------------- //
                        //////Style style = textPane.addStyle("GreenStyle", null);
                        //////StyleConstants.setForeground(style, Color.GREEN);

                        String textToAppend = recived[1] + " :" + recived[0] + "from" + rport + "\n";
                        try {
                            doc.insertString(doc.getLength(), textToAppend, style);
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                        st.setText("Received from:IP=" + rip + " ,Port=" + rport);

                    }
                }
                //To change body of generated methods, choose Tools | Templates.
            } catch (Exception ex) {
                //Logger.getLogger(c1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    class Read extends Thread {

        Socket s;
        String id;

        private Read(Socket s, String id) {
            this.id = id;
            this.s = s;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    din = new DataInputStream(s.getInputStream());
                    String m = din.readUTF();
                    System.out.println(m);
                    dlm.clear();
                    String array[] = m.split(",");
                    for (int i = 0; i < array.length; i++) {

                        if (!this.id.equals(array[i])) {
                            dlm.addElement(array[i]);
                            UL.setModel(dlm);
                        }
                    }
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }
    //private static javax.swing.JTextArea A1;
    JTextPane textPane;
    private JList<String> UL;
    private JTextField dip;
    private JTextField dp;
    private JTextField lip;
    private JTextField lp;
    private JTextField msg;
    private JTextField name;
    private JTextField password;
    private JTextField serverIp;
    private JTextField serverPort;
    private JTextField st;
    private JLabel lastLoginTimeLabel;

}
