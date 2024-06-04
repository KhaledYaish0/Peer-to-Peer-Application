package network_part2;

// Importing necessary libraries for GUI, networking, I/O, and logging.
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionEvent;
import java.awt.Dimension;
import javax.swing.JOptionPane;


// TCPServer class that extends JFrame for GUI components.
public class TCPServer extends javax.swing.JFrame {

    // Declaration of variables for server socket, client management, and UI components.
    ServerSocket serversocket;
    // A map to keep track of clients connected to the server.
    HashMap Clients = new HashMap();
    // A unique identifier for each client.
    String id;
    // Thread to accept clients.
    AcceptClient ac;
    // Thread to read messages from clients.
    ReadMsg rm;
    // Model for managing the list of online clients in the GUI.
    DefaultListModel dlm;
    // To store the last login time for each client.
    HashMap<String, LocalDateTime> lastLoginTimes = new HashMap<>();

    // Constructor to initialize GUI components and some server functionalities.
    public TCPServer() {
        initComponents();
        dlm = new DefaultListModel();
        onlineList.setModel(dlm);
    }

    // initComponents method is responsible for setting up the GUI components.
    private void initComponents() {
        // GUI component declarations and initializations.
        jLabel1 = new javax.swing.JLabel();
        port = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        a = new javax.swing.JTextArea();
        status = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        onlineList = new javax.swing.JList<>();
        jButton1 = new javax.swing.JButton();
        
        jLabelUsername = new javax.swing.JLabel();
        jLabelPassword = new javax.swing.JLabel();
        jTextFieldUsername = new javax.swing.JTextField();
        jTextFieldUsername.setPreferredSize(new Dimension(250, 20));
        jTextFieldPassword = new javax.swing.JTextField();
        jTextFieldPassword.setPreferredSize(new Dimension(150, 20));
        jButtonAddUser = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Port:");
        port.setText("8000");
        port.addActionListener(evt -> portActionPerformed(evt));
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"wi-fi :192.168.1.112", "Ethernet: 169.254.49.56", "loopback Pseudo-Interface 1: 127.0.0.1"}));
        jComboBox1.addActionListener(evt -> jComboBox1ActionPerformed(evt));

        a.setColumns(20);
        a.setRows(5);
        jScrollPane1.setViewportView(a);

        jLabel2.setText("Server Status:");

        onlineList.addListSelectionListener(this::onlineListValueChanged);
        jScrollPane3.setViewportView(onlineList);

        jButton1.setText("Start Listening");
        jButton1.addActionListener(this::jButton1ActionPerformed);
        
        jLabelUsername.setText("Username:");
        jLabelPassword.setText("Password:");
        jButtonAddUser.setText("Add User");
        jButtonAddUser.addActionListener(evt -> addUserActionPerformed(evt));


        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1)
                    .addComponent(jLabelUsername, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(port)
                    .addComponent(jTextFieldUsername))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelPassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldPassword)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jButtonAddUser))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addComponent(status))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane3))
    );

    // Vertical layout - stack components vertically
    layout.setVerticalGroup(
        layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jButton1)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(port, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane1)
            .addComponent(jScrollPane3)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel2)
                .addComponent(status))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabelUsername)
                .addComponent(jTextFieldUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabelPassword)
                .addComponent(jTextFieldPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButtonAddUser))
    );

        pack();
    }
    
private void addUserActionPerformed(ActionEvent evt) {
    String username = jTextFieldUsername.getText().trim().toLowerCase();
    String password = jTextFieldPassword.getText().trim();
    if (!username.isEmpty() && !password.isEmpty()) {
        // Check if the username already exists
        if (!usernameExists(username)) {
            try (FileWriter fw = new FileWriter("credential.txt", true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.println(username + "," + password);
                JOptionPane.showMessageDialog(this, "User added successfully: " + username, "Success", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("User added: " + username);
            } catch (IOException e) {
                System.out.println("An error occurred while adding a new user.");
                e.printStackTrace();
            }
        } else {
            // Show dialog that username already exists
            JOptionPane.showMessageDialog(this, "This username already exists. Please choose a different username.", "Username Exists", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        // Optionally, handle the case where either username or password is empty
        JOptionPane.showMessageDialog(this, "Username and password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private boolean usernameExists(String username) {
    try (BufferedReader reader = new BufferedReader(new FileReader("credential.txt"))) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.split(",")[0].equals(username)) {
                return true; // Username exists
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return false; // Username does not exist
}

    // Event listeners for GUI components like text fields and buttons.
    private void portActionPerformed(ActionEvent evt) {
    }

    private void jComboBox1ActionPerformed(ActionEvent evt) {
    }

    private void onlineListValueChanged(ListSelectionEvent evt) {
    }

    // Action listener for the "Start Listening" button.
    private void jButton1ActionPerformed(ActionEvent evt) {
        try {
            // Attempt to create a ServerSocket and start listening on the specified port.
            // Also, initializes and starts the AcceptClient thread to accept incoming client connections.
            serversocket = new ServerSocket(Integer.parseInt(port.getText()));
            status.setText("server started at port: " + port.getText());
            //Creates a new thread to accept client connections.
            ac = new AcceptClient();
            ac.start();
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // AcceptClient class extends Thread, designed to handle incoming client connections.
    public class AcceptClient extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    // Server accepts a client connection.
                    Socket s = serversocket.accept();
                    // Reads a message from the connected client.
                    String in = new DataInputStream(s.getInputStream()).readUTF();
                    // Logout logic: if message contains "logout:", remove client from list.
                    if (in.contains("logout:")) {
                        in = in.substring(7);
                        Clients.remove(in);
                        a.append(in + " logged out. \n");
                        new PrepareClientList().start();
                        // Registration logic: if client is not already registered, add them to the client list.
                    } else {
                        id = in;
                        String[] array = in.split("-");
                        if (Clients.containsKey(array[0]) && Clients.containsKey(array[1])) {
                            DataOutputStream d_out = new DataOutputStream(s.getOutputStream());
                            d_out.writeUTF("you are Already Registered!");
                        } else {
                            Clients.put(in, s);
                            lastLoginTimes.put(in, LocalDateTime.now()); ///////qais new time line
                            a.append(in + " Joined !\n");

                            rm = new ReadMsg(s, in);
                            rm.start();
                            if (!in.isEmpty()) {
                                new PrepareClientList().start();
                            }
                        }
                    }
                    // Logic to handle client logout or registration.
                } catch (Exception e) {
                    status.setText(e.getMessage());
                }
            }
        }
    }

    // ReadMsg class extends Thread, intended to handle reading messages from clients.
    class ReadMsg extends Thread {

        Socket sock;
        String id;

        private ReadMsg(Socket s, String in) {
            this.id = in;
            this.sock = s;
        }
    }

    // PrepareClientList class extends Thread, updates the client list GUI component.
    public class PrepareClientList extends Thread {

        public PrepareClientList() {
        }

        // Logic to update client list in the GUI.
        @Override
        public void run() {
            try {
                dlm.clear();

                String ids = "";
                Set k = Clients.keySet();
                Iterator itr = k.iterator();
                while (itr.hasNext()) {
                    String key = (String) itr.next();
                    ids += key + ",";
                }
                if (ids.length() != 0) {
                    ids = ids.substring(0, ids.length() - 1);
                }
                itr = k.iterator();
                while (itr.hasNext()) {
                    String key = (String) itr.next();
                    try {
                        new DataOutputStream(((Socket) Clients.get(key)).getOutputStream()).writeUTF(ids);
                    } catch (IOException ex) {
                        Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                StringTokenizer st = new StringTokenizer(ids, ",");
                while (st.hasMoreTokens()) {
                    String u = st.nextToken();
                    dlm.addElement(u);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // main method to run the TCP Server application.
    public static void main(String args[]) {
        try {
            File myObj = new File("credential.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        java.awt.EventQueue.invokeLater(() -> new TCPServer().setVisible(true));
    }

    // Declaration of GUI components.
    private javax.swing.JTextArea a;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonAddUser;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelUsername;
    private javax.swing.JLabel jLabelPassword;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList<String> onlineList;
    private javax.swing.JTextField port;
    private javax.swing.JTextField status;
    private javax.swing.JTextField jTextFieldUsername;
    private javax.swing.JTextField jTextFieldPassword;
}