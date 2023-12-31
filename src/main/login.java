
package main;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.border.Border;


public class login extends javax.swing.JFrame {

   
    protected int userID;
    protected String user = "";

    public login() {
        initComponents();
        design();
        connect();
        combobox();
        removeCorner();
    }

    //CONNECTOR SA XAMPP MYSQL    
    private final String url = "jdbc:mysql://localhost:3306/pharma";
    private final String sqlusername = "root";
    private final String sqlpassword = "";

    //para iconnect yung mysql sa gui
    Connection con;
    //ginagamit para mag deliver ng command sa database
    PreparedStatement pst;
    //ginagamit para kumuha ng data sa database
    ResultSet rs;

    private void design(){
        password.setBackground(new Color(0, 0, 0, 0));
        showpassCB.setBackground(new Color(0, 0, 0, 0));
        create_acc.setText("<html><u>Don't have an account?</u></html>");
    }
    
    private void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url, sqlusername, sqlpassword);

        } catch (ClassNotFoundException | SQLException e) {

        }
    }

    private void combobox() {
        try {
            pst = con.prepareStatement("SELECT username FROM login");
            rs = pst.executeQuery();
            while (rs.next()) {
                usernameCombo.addItem(rs.getString("username"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(login.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(this, "No Database Found!");
        }

    }

    private void getUserId() {
        try {
            user = usernameCombo.getSelectedItem().toString();
            pst = con.prepareStatement("SELECT ID FROM login WHERE username = ?");
            pst.setString(1, user);
            rs = pst.executeQuery();

            if (rs.next()) {
                userID = Integer.parseInt(rs.getString("ID"));
            }
        } catch (SQLException | NullPointerException ex) {

        }
    }

    private void removeCorner() {
        this.setContentPane(bg);
        this.setBackground(new Color(0, 0, 0, 0));
    }

    private void loginHistory(){
        try {
            pst = con.prepareStatement("INSERT INTO login_history(userID, username) VALUES(?,?)");
            pst.setInt(1, userID);
            pst.setString(2, user);
            pst.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void loginClicked(){
        try {
            String username = usernameCombo.getSelectedItem().toString();
            String pass = password.getText();

            pst = con.prepareStatement("SELECT username, password FROM login WHERE username = ? and password = ?");
            pst.setString(1, username);
            pst.setString(2, pass);
            rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login Success!");
                getUserId();
                loginHistory();
                this.dispose();
                //since static sya, no need to instantiate
                loading.startLoadingProcess();

            } else if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill out Username");
                login.requestFocus();
            } else if (pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill out Password");
                login.requestFocus();
            } else if (username.equals("Username") && pass.equals("password")) {
                JOptionPane.showMessageDialog(this, "Please fill out Username and Password First");
                login.requestFocus();
            } else {
                JOptionPane.showMessageDialog(this, "Wrong Username or Password!", "Message", JOptionPane.ERROR_MESSAGE);
                login.requestFocus();
                password.setText("");
                showpassCB.setSelected(false);

            }

        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "No Database Found!");
        } catch (SQLException ex) {
            Logger.getLogger(login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg = new com.k33ptoo.components.KGradientPanel();
        jLabel1 = new javax.swing.JLabel();
        password = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        login = new javax.swing.JLabel();
        showpassCB = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        usernameCombo = new javax.swing.JComboBox<>();
        create_acc = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        bg.setkBorderRadius(80);
        bg.setkEndColor(new java.awt.Color(255, 0, 255));
        bg.setkStartColor(new java.awt.Color(0, 0, 255));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/logo (2).png"))); // NOI18N

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

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/username_new.png"))); // NOI18N

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/password_new.png"))); // NOI18N

        login.setBackground(new java.awt.Color(255, 255, 255));
        login.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        login.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        login.setText("Login");
        login.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        login.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                loginMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginMouseExited(evt);
            }
        });

        showpassCB.setText("Show Password");
        showpassCB.setBackground(new java.awt.Color(255, 255, 255));
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

        jLabel4.setText("X");
        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel4MouseClicked(evt);
            }
        });

        jLabel5.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 2, 0, 0, new java.awt.Color(204, 204, 204)));

        usernameCombo.setBackground(new java.awt.Color(204, 204, 204));
        usernameCombo.setBorder(null);
        usernameCombo.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        usernameCombo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                usernameComboKeyPressed(evt);
            }
        });

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
            bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(bgLayout.createSequentialGroup()
                .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bgLayout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(showpassCB)
                            .addGroup(bgLayout.createSequentialGroup()
                                .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(password, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                                    .addComponent(usernameCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(login, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(bgLayout.createSequentialGroup()
                        .addGap(88, 88, 88)
                        .addComponent(create_acc, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bgLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(9, 9, 9)))
                .addGap(18, 18, 18))
        );
        bgLayout.setVerticalGroup(
            bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bgLayout.createSequentialGroup()
                .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bgLayout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(usernameCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(showpassCB)
                        .addGap(18, 18, 18)
                        .addComponent(login, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(create_acc))
                    .addGroup(bgLayout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(bgLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel1)))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(bg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
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

    private void showpassCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showpassCBActionPerformed
        if (password.getText().equals("password")) {

        } else if (showpassCB.isSelected()) {
            password.setEchoChar((char) 0);
        } else {
            password.setEchoChar(('*'));
        }
    }//GEN-LAST:event_showpassCBActionPerformed

    private void loginMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginMouseExited
        Border border = BorderFactory.createLineBorder(Color.WHITE);
        login.setBorder(border);
    }//GEN-LAST:event_loginMouseExited

    private void loginMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginMouseEntered

        Border border = BorderFactory.createLineBorder(Color.BLACK);
        login.setBorder(border);
    }//GEN-LAST:event_loginMouseEntered

    private void loginMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginMouseClicked
        loginClicked();
    }//GEN-LAST:event_loginMouseClicked

    private void passwordFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passwordFocusLost
        if (password.getText().equals("")) {
            password.setText("password");
        }
    }//GEN-LAST:event_passwordFocusLost

    private void passwordFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passwordFocusGained
        if (password.getText().equals("password")) {
            password.setText("");
        }
    }//GEN-LAST:event_passwordFocusGained

    private void create_accMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_create_accMouseEntered
        create_acc.setForeground(Color.white);
    }//GEN-LAST:event_create_accMouseEntered

    private void create_accMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_create_accMouseExited
        create_acc.setForeground(Color.black);
    }//GEN-LAST:event_create_accMouseExited

    private void create_accMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_create_accMouseClicked
        dispose();
        new createAcc().setVisible(true);
    }//GEN-LAST:event_create_accMouseClicked

    private void showpassCBMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showpassCBMouseEntered
        showpassCB.setForeground(Color.WHITE);
    }//GEN-LAST:event_showpassCBMouseEntered

    private void showpassCBMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showpassCBMouseExited
        showpassCB.setForeground(Color.black);
    }//GEN-LAST:event_showpassCBMouseExited

    private void usernameComboKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_usernameComboKeyPressed
         if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            loginClicked();
        }
    }//GEN-LAST:event_usernameComboKeyPressed

    private void passwordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_passwordKeyPressed
         if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            loginClicked();
        }
    }//GEN-LAST:event_passwordKeyPressed

    private void showpassCBKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_showpassCBKeyPressed
         if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            loginClicked();
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
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.k33ptoo.components.KGradientPanel bg;
    private javax.swing.JLabel create_acc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel login;
    private javax.swing.JPasswordField password;
    private javax.swing.JCheckBox showpassCB;
    private javax.swing.JComboBox<String> usernameCombo;
    // End of variables declaration//GEN-END:variables

}
