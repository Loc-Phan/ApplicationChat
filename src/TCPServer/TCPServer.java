/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TCPServer;

import Account.Account;
import Account.SaveAccount;
import Form.Login;
import TCPClient.TCPClient;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Chen-Yang
 */
public class TCPServer extends javax.swing.JFrame {

    ArrayList clientOutputStreams;
    ArrayList<String> users;
    public static int isConn = 0;
    public static List<Account> ds = null;
    //BufferedReader br;

    public TCPServer() {
        initComponents();
        Thread starter = new Thread(new ServerStart());
        starter.start();
        isConn=0;
    }

    public class ClientHandler implements Runnable {

        BufferedReader br;
        Socket s;
        PrintWriter client;

        public ClientHandler(Socket clientSocket, PrintWriter user){
            client = user;
            //System.out.println("client: "+client);
            try {
                s = clientSocket;
                InputStreamReader isReader = new InputStreamReader(s.getInputStream());
                br = new BufferedReader(isReader);

            } catch (Exception ex) {
                txtMess.append("Unexpected error... \n");
            }

        }

        @Override
        public void run() {
            String message, connect = "Connect", disconnect = "Disconnect", chat = "Chat";
            String[] data;
            isConn = 0; //khởi động lại thì chưa có account nào
            Account acc = new Account();
            try {

                while ((message = br.readLine()) != null) {
                    txtMess.append("Received: " + message + "\n");
                    data = message.split(":");

                    acc.setUserName(data[0]);
                    acc.setPassWord(data[1]);

                    //System.out.println(acc.getPassWord());
                    if (data[2].equals("Connect")) {
                        try {
                            
                            ds = SaveAccount.ListAccount("Account.xml");
                            for (int i = 0; i < ds.size(); i++) {
                                if (acc.getUserName().equals(ds.get(i).getUserName()) && !acc.getPassWord().equals(ds.get(i).getPassWord())) {
                                    isConn = 2;
                                    OutputStream os = s.getOutputStream();
                                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                                    bw.write("2-");
                                    bw.flush();
                                }

                                if (acc.getUserName().equals(ds.get(i).getUserName()) && acc.getPassWord().equals(ds.get(i).getPassWord())) {
                                    //tellEveryone((data[0] + ":" + data[1] + ":" + chat));
                                    isConn = 1;
                                    OutputStream os = s.getOutputStream();
                                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                                    bw.write("1-");
                                    bw.flush();

                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            txtMess.append("Lỗi đăng nhập... \n");
                        }
                        if (isConn == 0) {
                            OutputStream os = s.getOutputStream();
                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                            bw.write("0-");
                            bw.flush();
                        }

                    }
                    else if (data[2].equals("Register")) {
                        int tontai = 0;
                        //System.out.println("Do duoc register");
                        try {
                            ds = SaveAccount.ListAccount("Account.xml");
                            for (int i = 0; i < ds.size(); i++) {
                                //System.out.println("Do duoc for");
                                if (acc.getUserName().equals(ds.get(i).getUserName())) {
                                    tontai = 1;
                                    OutputStream os = s.getOutputStream();
                                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                                    bw.write("1-");
                                    bw.flush();
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            
                        }
                        if (tontai == 0) {
                            acc.setFirstName("None");
                            acc.setLastName("None");
                            File f = new File("Account.xml");
                            if(f.exists()) {
                                SaveAccount.addAccount(acc, "Account.xml"); 
                            }
                            else {
                                SaveAccount.createFileXML(acc, "Account.xml");
                            }
                            OutputStream os = s.getOutputStream();
                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                            bw.write("0-");
                            bw.flush();
                        }
                    }

                    if (data[2].equals(connect)) {
                        tellEveryone((data[0] + ":" + data[1] + ":" + chat));
                        // đúng thì thêm user vào
                        if(isConn==1) {
                            userAdd(data[0]); 
                        }
                    } else if (data[2].equals(disconnect)) {
                        tellEveryone((data[0] + ":has disconnected." + ":" + chat));
                        userRemove(data[0]);
                    } else if (data[2].equals(chat)) {
                        tellEveryone(message);
                        //tellUser(message,data[0]);
                    } else if (data[2].equals("Register")) {
                        tellEveryone((data[0] + ":" + data[1] + ":" + "Register"));
                        //tellUser(message,data[0]);
                    } 
                    else{
                        //txtMess.append("No Conditions were met. \n");
                    }

                }
            } catch (Exception ex) {
                txtMess.append(acc.getUserName() + " đã mất kết nối. \n");
                ex.printStackTrace();
                clientOutputStreams.remove(client);
            }
        }
    }

    public void userAdd(String data) {
        String message, add = ": :Connect", done = "Server: :Done", name = data;
        //txtMess.append("Before " + name + " added. \n");
        users.add(name);
        //txtMess.append("After " + name + " added. \n");
        String[] tempList = new String[(users.size())];
        users.toArray(tempList);

        for (String token : tempList) {
            message = (token + add);
            tellEveryone(message);
        }
        tellEveryone(done);
    }

    public void userRemove(String data) {
        String message, add = ": :Connect", done = "Server: :Done", name = data;
        users.remove(name);
        String[] tempList = new String[(users.size())];
        users.toArray(tempList);

        for (String token : tempList) {
            message = (token + add);
            tellEveryone(message);
        }
        tellEveryone(done);
    }

    public void tellEveryone(String message) {
        Iterator it = clientOutputStreams.iterator();

        while (it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                txtMess.append("Sending: " + message + "\n");
                writer.flush();
                txtMess.setCaretPosition(txtMess.getDocument().getLength());

            } catch (Exception ex) {
                txtMess.append("Error telling everyone. \n");
            }
        }
    }

    public void tellUser(String message, String userName) {
        Iterator it = clientOutputStreams.iterator();

        while (it.hasNext()) {
            try {
                String[] arrTemp;
                PrintWriter writer = (PrintWriter) it.next();
                if (message.contains(",")) {
                    String temp = message;
                    arrTemp = temp.split(",");
                    if (userName.equals(arrTemp[1])) {
                        writer.println(arrTemp[0]);
                        txtMess.append("Sending: " + arrTemp[0] + "\n");
                        writer.flush();
                        txtMess.setCaretPosition(txtMess.getDocument().getLength());
                    }
                }

            } catch (Exception ex) {
                txtMess.append("Error telling everyone. \n");
            }
        }
    }

    /**
     * Creates new form TCPServer
     */
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txtMess = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        lblIP = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Server");
        setBackground(new java.awt.Color(204, 204, 204));

        txtMess.setColumns(20);
        txtMess.setFont(new java.awt.Font("Courier New", 0, 14)); // NOI18N
        txtMess.setRows(5);
        jScrollPane1.setViewportView(txtMess);

        jButton1.setIcon(new javax.swing.ImageIcon("C:\\Users\\ASUS-PC\\Downloads\\database.png")); // NOI18N
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("IP Address: ");

        lblIP.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblIP.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                lblIPAncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblIP, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblIP, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        setSize(new java.awt.Dimension(581, 495));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void lblIPAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_lblIPAncestorAdded
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        String ipAddress = addr.getHostAddress();
        lblIP.setText(ipAddress);
        // TODO add your handling code here:
    }//GEN-LAST:event_lblIPAncestorAdded

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TCPServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TCPServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TCPServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TCPServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TCPServer().setVisible(true);
//                try {
//                    new TCPServer().setVisible(true);
//                } catch (ParserConfigurationException ex) {
//                    Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (IOException ex) {
//                    Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (SAXException ex) {
//                    Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }
        });
    }

    public class ServerStart implements Runnable {

        @Override
        public void run() {
            clientOutputStreams = new ArrayList();
            users = new ArrayList();

            try {
                ServerSocket serverSocket = new ServerSocket(3451);

                while (true) {
                    //đúng user mới đồng ý cho vào 

                    Socket cliSock = serverSocket.accept();
                    PrintWriter writer = new PrintWriter(cliSock.getOutputStream());
                    clientOutputStreams.add(writer);

                    Thread listener = new Thread(new ClientHandler(cliSock, writer));

                    listener.start();

                    //txtMess.append("Got a connection. \n");
                }
            } catch (Exception ex) {
                txtMess.append("Error making a connection. \n");
            }
        }
    }




    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblIP;
    private javax.swing.JTextArea txtMess;
    // End of variables declaration//GEN-END:variables
}
