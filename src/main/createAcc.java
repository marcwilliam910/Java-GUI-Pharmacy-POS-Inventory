package main;

import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.border.Border;

public class createAcc extends javax.swing.JFrame {

    public createAcc() {
        initComponents();
        connect();
        password.setBackground(new Color(0, 0, 0, 0));
        showpassCB.setBackground(new Color(0, 0, 0, 0));
        username.setBackground(new Color(0, 0, 0, 0));
        jTextField1.setBackground(new Color(0, 0, 0, 0));
        removeCorner();
        create_acc.setText("<html><u>Already have an account?</u></html>");
    }

    //CONNECTOR SA XAMPP MYSQL    
    String url = "jdbc:mysql://localhost:3306/pharma";
    String sqlusername = "root";
    String sqlpassword = "";

    //para iconnect yung mysql sa gui
    Connection con;
    //ginagamit para mag deliver ng command sa database
    PreparedStatement pst;
    //ginagamit para kumuha ng data sa database
    ResultSet rs;

    private void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url, sqlusername, sqlpassword);

        } catch (ClassNotFoundException | SQLException e) {

        }
    }

    private void removeCorner() {
        this.setContentPane(bg);
        this.setBackground(new Color(0, 0, 0, 0));
    }

    private void createClicked(){
            try {
            String user = username.getText();
            String pass = password.getText();
            if (user.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill out Username");
                create.requestFocus();
            } else if (pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill out Password");
                create.requestFocus();
            } else if (user.equals("Username") || pass.equals("password")) {
                JOptionPane.showMessageDialog(this, "Please fill out Username and Password First");
                create.requestFocus();

            } else {
                String[] position = {"Owner", "Employee"};
                int defaultOption = 1;
                int choice = JOptionPane.showOptionDialog(null, "Select your position", "Position", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, position, position[defaultOption]);

                int isSure = JOptionPane.showConfirmDialog(null, "Create Account?", "Confirmation", JOptionPane.YES_NO_OPTION);
                
                if (isSure == JOptionPane.NO_OPTION || isSure == JOptionPane.CANCEL_OPTION) {
                   
                } else if(choice == JOptionPane.YES_OPTION) {
                    pst = con.prepareStatement("INSERT INTO login(username,password,position) VALUES(?,?, ?)");
                    pst.setString(1, user);
                    pst.setString(2, pass);
                    pst.setString(3, "Owner");
                    pst.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Account Added!");
                    dispose();
                    new login().setVisible(true);
                }
                else{
                    pst = con.prepareStatement("INSERT INTO login(username,password,position) VALUES(?,?, ?)");
                    pst.setString(1, user);
                    pst.setString(2, pass);
                    pst.setString(3, "Employee");
                    pst.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Account Added!");
                    dispose();
                    new login().setVisible(true);
                }

            }
        } catch (HeadlessException | SQLException e) {
        }

    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg = new com.k33ptoo.components.KGradientPanel();
        jLabel4 = new javax.swing.JLabel();
        password = new javax.swing.JPasswordField();
        username = new javax.swing.JTextField();
        showpassCB = new javax.swing.JCheckBox();
        create = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        create_acc = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        bg.setkBorderRadius(50);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel4.setText("X");
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel4MouseClicked(evt);
            }
        });

        password.setText("password");
        password.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(204, 204, 204)));
        password.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                passwordFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                passwordFocusLost(evt);
            }
        });
        password.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                passwordKeyPressed(evt);
            }
        });

        username.setText("Username");
        username.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(204, 204, 204)));
        username.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                usernameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                usernameFocusLost(evt);
            }
        });
        username.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernameActionPerformed(evt);
            }
        });
        username.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                usernameKeyPressed(evt);
            }
        });

        showpassCB.setBackground(new java.awt.Color(255, 255, 255));
        showpassCB.setText("Show Password");
        showpassCB.setBorder(null);
        showpassCB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                showpassCBMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                showpassCBMouseExited(evt);
            }
        });
        showpassCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showpassCBActionPerformed(evt);
            }
        });
        showpassCB.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                showpassCBKeyPressed(evt);
            }
        });

        create.setBackground(new java.awt.Color(255, 255, 255));
        create.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        create.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        create.setText("Create ");
        create.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        create.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                createMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                createMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                createMouseExited(evt);
            }
        });

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/username_new.png"))); // NOI18N

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/password_new.png"))); // NOI18N

        jTextField1.setEditable(false);
        jTextField1.setFont(new java.awt.Font("Showcard Gothic", 2, 24)); // NOI18N
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField1.setText("Create Account");
        jTextField1.setBorder(null);

        create_acc.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        create_acc.setText("jLabel6");
        create_acc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                create_accMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                create_accMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                create_accMouseExited(evt);
            }
        });

        javax.swing.GroupLayout bgLayout = new javax.swing.GroupLayout(bg);
        bg.setLayout(bgLayout);
        bgLayout.setHorizontalGroup(
            bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bgLayout.createSequentialGroup()
                .addContainerGap(42, Short.MAX_VALUE)
                .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bgLayout.createSequentialGroup()
                            .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(showpassCB)
                                .addGroup(bgLayout.createSequentialGroup()
                                    .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(username)
                                        .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGap(2, 2, 2))
                        .addComponent(create, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(bgLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(create_acc, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))
                    .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bgLayout.createSequentialGroup()
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(40, 40, 40))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bgLayout.createSequentialGroup()
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addContainerGap()))))
        );
        bgLayout.setVerticalGroup(
            bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bgLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(5, 5, 5)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bgLayout.createSequentialGroup()
                        .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(bgLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26)
                .addComponent(showpassCB)
                .addGap(30, 30, 30)
                .addComponent(create, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(create_acc)
                .addGap(36, 36, 36))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel4MouseClicked

    private void passwordFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passwordFocusGained
        if (password.getText().equals("password")) {
            password.setText("");
        }
    }//GEN-LAST:event_passwordFocusGained

    private void passwordFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passwordFocusLost
        if (password.getText().equals("")) {
            password.setText("password");
        }
    }//GEN-LAST:event_passwordFocusLost

    private void usernameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_usernameFocusGained
        if (username.getText().equals("Username")) {
            username.setText("");
        }
    }//GEN-LAST:event_usernameFocusGained

    private void usernameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_usernameFocusLost
        if (username.getText().equals("")) {
            username.setText("Username");
        }
    }//GEN-LAST:event_usernameFocusLost

    private void usernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usernameActionPerformed

    private void showpassCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showpassCBActionPerformed
        if (password.getText().equals("password")) {

        } else if (showpassCB.isSelected()) {
            password.setEchoChar((char) 0);
        } else {
            password.setEchoChar(('*'));
        }
    }//GEN-LAST:event_showpassCBActionPerformed

    private void createMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_createMouseClicked
        createClicked();
    }//GEN-LAST:event_createMouseClicked

    private void createMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_createMouseEntered

        Border border = BorderFactory.createLineBorder(Color.white);
        create.setBorder(border);
    }//GEN-LAST:event_createMouseEntered

    private void createMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_createMouseExited
        Border border = BorderFactory.createLineBorder(Color.black);
        create.setBorder(border);
    }//GEN-LAST:event_createMouseExited

    private void create_accMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_create_accMouseClicked
        dispose();
        new login().setVisible(true);
    }//GEN-LAST:event_create_accMouseClicked

    private void create_accMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_create_accMouseEntered
        create_acc.setForeground(Color.white);
    }//GEN-LAST:event_create_accMouseEntered

    private void create_accMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_create_accMouseExited
        create_acc.setForeground(Color.black);
    }//GEN-LAST:event_create_accMouseExited

    private void showpassCBMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showpassCBMouseEntered
        showpassCB.setForeground(Color.white);
    }//GEN-LAST:event_showpassCBMouseEntered

    private void showpassCBMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showpassCBMouseExited
        showpassCB.setForeground(Color.BLACK);
    }//GEN-LAST:event_showpassCBMouseExited

    private void usernameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_usernameKeyPressed
         if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            createClicked();
        }
    }//GEN-LAST:event_usernameKeyPressed

    private void passwordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_passwordKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            createClicked();
        }
    }//GEN-LAST:event_passwordKeyPressed

    private void showpassCBKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_showpassCBKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            createClicked();
        }
    }//GEN-LAST:event_showpassCBKeyPressed

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
            java.util.logging.Logger.getLogger(createAcc.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(createAcc.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(createAcc.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(createAcc.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new createAcc().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.k33ptoo.components.KGradientPanel bg;
    private javax.swing.JLabel create;
    private javax.swing.JLabel create_acc;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JPasswordField password;
    private javax.swing.JCheckBox showpassCB;
    private javax.swing.JTextField username;
    // End of variables declaration//GEN-END:variables
}
