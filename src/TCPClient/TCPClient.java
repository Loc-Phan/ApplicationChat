/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TCPClient;

import Account.Account;
import Form.Login;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;

/**
 *
 * @author Chen-Yang
 */
public class TCPClient extends javax.swing.JFrame {
    int chatGr = 0;
    String username, address = "localhost";
    ArrayList<String> users = new ArrayList();
    DefaultListModel listModel=null;
    int port = 3451;
    Boolean isConnected = false;

    Socket sock;
    BufferedReader reader;
    PrintWriter writer;

    public void ListenThread() {
        
        Thread IncomingReader = new Thread(new IncomingReader());
        IncomingReader.start();
    }

    public void userAdd(String data) {
        users.add(data);
    }

    public void userRemove(String data) {
        txtMess.append(data + " is now offline.\n");
    }

    //--------------------------//
    public void writeUsers() {
        String[] tempList = new String[(users.size())];
        users.toArray(tempList);
    }

    //--------------------------//
    public void sendDisconnect() {
        String bye = (username + ": :Disconnect");
        try {
            writer.println(bye);
            writer.flush();
        } catch (Exception e) {
            txtMess.append("Không thể gửi tin nhắn khi ngắt kết nối.\n");
        }
    }

    //--------------------------//
    public void Disconnect() {
        try {
            sock.close();
        } catch (Exception ex) {
            txtMess.append("Không thể ngắt kết nối. \n");
        }
        isConnected = false;
    }

    /**
     * Creates new form TCPClient
     */
    String userName;
    public TCPClient(Account acc) {
        initComponents();
        username = acc.getUserName();
        userName = acc.getUserName();
        listModel = new DefaultListModel();

            try 
            {
                sock = new Socket(address, port);
                InputStreamReader streamreader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(streamreader);
                //System.out.println(reader);
                writer = new PrintWriter(sock.getOutputStream());
                writer.println(username + ":has connected.:Connect");
                writer.flush(); 
                isConnected = true; 
            } 
            catch (Exception ex) 
            {
                txtMess.append("Không thể kết nối! Vui lòng thử lại. \n");
            }
            
            ListenThread();
            
    }
    
    public TCPClient() {
        
        initComponents();

    }

    public class IncomingReader implements Runnable {

        @Override
        public void run() {
            String[] data;
            String stream, done = "Done", connect = "Connect", disconnect = "Disconnect", chat = "Chat";
            
            String[] mess;
            //System.out.println(reader);
            try {
                while ((stream = reader.readLine()) != null) {
                    //System.out.println(stream);
                    data = stream.split(":");
                    //System.out.println(data[2]);
                    if (data[2].equals(chat)) {
                        System.out.println("chatGr: "+chatGr);
                        if (chatGr == 1) {
                            
                            txtMess.append(data[0] + ": " + data[1] + "\n");
                        } 
                        else if(chatGr==0){
                            if (data[1].contains(",")) {
                                String tempMess = data[1];
                                mess = tempMess.split(",");

                                if (userName.equals(mess[1])) {
                                    txtMess.append(data[0] + " đã gửi tới tôi: " + mess[0] + "\n");
                                } else if (userName.equals(data[0])) {
                                    txtMess.append("Tôi đã gửi tới " + data[0] + ": " + mess[0] + "\n");
                                }
                            }
                        }
                        txtMess.setCaretPosition(txtMess.getDocument().getLength());
                    } else if (data[2].equals(connect)) {
                        
                        txtMess.removeAll();
                        userAdd(data[0]);
                    } else if (data[2].equals(disconnect)) {
                        userRemove(data[0]);
                    } else if (data[2].equals(done)) {
                        txtMess.setText("");
                        writeUsers();
                        if (chatGr == 0) {
                            listModel.clear();
                            for (int i = 0; i < users.size(); i++) {
                                if (!users.get(i).equals(userName)) {
                                    listModel.addElement(users.get(i).toString());
                                }
                            }
                            list.setModel(listModel);
                        }
                        users.clear();
                    }
                }

            } catch (Exception ex) {
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        list = new javax.swing.JList<>();
        btnLog = new javax.swing.JButton();
        btnGroup = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        user = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        txtChat = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        btnSend = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtMess = new javax.swing.JTextArea();

        jButton1.setIcon(new javax.swing.ImageIcon("C:\\Users\\ASUS-PC\\Downloads\\icons8-file-48.png")); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        list.setBackground(new java.awt.Color(240, 240, 240));
        jScrollPane1.setViewportView(list);

        btnLog.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnLog.setText("Log Out");
        btnLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogActionPerformed(evt);
            }
        });

        btnGroup.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnGroup.setText("Group");
        btnGroup.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnGroupMousePressed(evt);
            }
        });
        btnGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGroupActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon("C:\\Users\\ASUS-PC\\Downloads\\10154750161582994861-128.png")); // NOI18N

        user.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        user.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        user.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                userAncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(user, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(user, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnGroup, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnLog, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLog, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        txtChat.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jButton2.setIcon(new javax.swing.ImageIcon("C:\\Users\\ASUS-PC\\Downloads\\icons8-file-48.png")); // NOI18N

        btnSend.setForeground(new java.awt.Color(204, 255, 255));
        btnSend.setIcon(new javax.swing.ImageIcon("C:\\Users\\ASUS-PC\\Downloads\\icons8-email-send-48.png")); // NOI18N
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon("C:\\Users\\ASUS-PC\\Downloads\\sticker (3).png")); // NOI18N

        txtMess.setColumns(20);
        txtMess.setRows(5);
        jScrollPane2.setViewportView(txtMess);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtChat, javax.swing.GroupLayout.PREFERRED_SIZE, 543, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSend, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 62, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSend, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                    .addComponent(txtChat, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogActionPerformed
        sendDisconnect();
        Disconnect();
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLogActionPerformed

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        String nothing = "";
        String name=(String)list.getSelectedValue();
        if ((txtChat.getText()).equals(nothing)) {
            txtChat.setText("");
            txtChat.requestFocus();
        } else {
            try {
                if(chatGr==0) {
                    writer.println(username + ":" + txtChat.getText()+ "," + name + ":" + "Chat");
                }
                else if(chatGr==1){
                    writer.println(username + ":" + txtChat.getText() + ":" + "Chat");
                }
                writer.flush(); // flushes the buffer
            } catch (Exception ex) {
                txtMess.append("Không thể gửi tin nhắn. \n");
            }
            txtChat.setText("");
            txtChat.requestFocus();
        }

        txtChat.setText("");
        txtChat.requestFocus();
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSendActionPerformed

    private void btnGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGroupActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGroupActionPerformed

    private void btnGroupMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGroupMousePressed
        chatGr = 1;
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGroupMousePressed

    private void userAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_userAncestorAdded
        user.setText(username);
        // TODO add your handling code here:
    }//GEN-LAST:event_userAncestorAdded

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
            java.util.logging.Logger.getLogger(TCPClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TCPClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TCPClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TCPClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TCPClient().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGroup;
    private javax.swing.JButton btnLog;
    private javax.swing.JButton btnSend;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList<String> list;
    private javax.swing.JTextField txtChat;
    private javax.swing.JTextArea txtMess;
    private javax.swing.JLabel user;
    // End of variables declaration//GEN-END:variables
}
