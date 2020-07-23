/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TCPServer;

import Form.Login;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Chen-Yang
 */
public class TCPServer extends javax.swing.JFrame {

    ArrayList clientOutputStreams;
    ArrayList<String> users;

    public TCPServer() {
        initComponents();
        Thread starter = new Thread(new ServerStart());
        starter.start();

    }

    public class ClientHandler implements Runnable {

        BufferedReader br;
        Socket s;
        PrintWriter client;

        public ClientHandler(Socket clientSocket, PrintWriter user) {
            client = user;
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

            try {
                while ((message = br.readLine()) != null) {
                    txtMess.append("Received: " + message + "\n");
                    data = message.split(":");

                    if (data[2].equals(connect)) {
                        tellEveryone((data[0] + ":" + data[1] + ":" + chat));
                        //tellUser((data[0] + ":" + data[1] + ":" + chat),data[0]);
                        userAdd(data[0]);
                    } else if (data[2].equals(disconnect)) {
                        tellEveryone((data[0] + ":has disconnected." + ":" + chat));
                        userRemove(data[0]);
                    } else if (data[2].equals(chat)) {
                        tellEveryone(message);
                        //tellUser(message,data[0]);
                    } else {
                        txtMess.append("No Conditions were met. \n");
                    }
                }
            } catch (Exception ex) {
                txtMess.append("Lost a connection. \n");
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
    
    public void tellUser(String message,String userName) {
        Iterator it = clientOutputStreams.iterator();
        
        while (it.hasNext()) {
            try {
                String[] arrTemp;
                PrintWriter writer = (PrintWriter) it.next();
                if(message.contains(",")) {
                    String temp = message;
                    arrTemp = temp.split(",");
                    if(userName.equals(arrTemp[1])) {
                        writer.println(arrTemp[0]);
                        txtMess.append("Sending: " + arrTemp[0] + "\n");
                        writer.flush();
                        txtMess.setCaretPosition(txtMess.getDocument().getLength());
                    }
                }
//                writer.println(message);
//                txtMess.append("Sending: " + message + "\n");
//                writer.flush();
//                txtMess.setCaretPosition(txtMess.getDocument().getLength());

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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        txtMess.setColumns(20);
        txtMess.setFont(new java.awt.Font("Courier New", 0, 14)); // NOI18N
        txtMess.setRows(5);
        jScrollPane1.setViewportView(txtMess);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(99, 99, 99)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
                .addContainerGap())
        );

        setSize(new java.awt.Dimension(581, 495));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtMess;
    // End of variables declaration//GEN-END:variables
}
