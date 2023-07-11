
package main;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

public class loading extends javax.swing.JFrame {
   
    public loading() {
        initComponents();
        removeCorner();
    }

     private void removeCorner(){
        this.setContentPane(bg);
        this.setBackground(new Color(0, 0, 0, 0));
    }
    
    protected static void startLoadingProcess() {
        loading l = new loading();
        l.setVisible(true);

        Thread loadingThread = new Thread(() -> {
            try {
                for (int i = 0; i <= 100; i = i + 4) {
                    TimeUnit.MILLISECONDS.sleep(80);
                    l.percent.setText(i + "%");

                    if (i == 12) l.loading.setText("Getting Ready");
                    if (i == 28) l.loading.setText("Loading Modules");
                    if (i == 40) l.loading.setText("Connecting to Database...");
                    if (i == 60) l.loading.setText("Connection Successful!");
                    if (i == 80) l.loading.setText("Launching System...");

                    l.progress.setValue(i);
                    if (i == 100) {
                        TimeUnit.MILLISECONDS.sleep(2000);
                        l.dispose();
                        new pharmacy().setVisible(true);
                        // new login().setVisible(true);
                    }
                }
            } catch (InterruptedException e) {
                // Handle the exception
            }
        });

    loadingThread.start();
}   
     
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg = new com.k33ptoo.components.KGradientPanel();
        icon = new javax.swing.JLabel();
        loading = new javax.swing.JLabel();
        progress = new javax.swing.JProgressBar();
        percent = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        bg.setkBorderRadius(40);
        bg.setkGradientFocus(1300);

        icon.setBackground(new java.awt.Color(102, 255, 255));
        icon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/loading logo (2).png"))); // NOI18N
        icon.setIconTextGap(0);
        icon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                iconMouseClicked(evt);
            }
        });

        loading.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        loading.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        loading.setText("Loading...");

        progress.setBackground(new java.awt.Color(255, 255, 255));
        progress.setFont(new java.awt.Font("Tahoma", 2, 14)); // NOI18N
        progress.setForeground(new java.awt.Color(0, 0, 0));
        progress.setBorderPainted(false);
        progress.setString("Please wait...");
        progress.setStringPainted(true);

        percent.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        percent.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        percent.setText("0%");

        javax.swing.GroupLayout bgLayout = new javax.swing.GroupLayout(bg);
        bg.setLayout(bgLayout);
        bgLayout.setHorizontalGroup(
            bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bgLayout.createSequentialGroup()
                .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bgLayout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(icon, javax.swing.GroupLayout.PREFERRED_SIZE, 844, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(bgLayout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(bgLayout.createSequentialGroup()
                                .addGap(339, 339, 339)
                                .addComponent(percent, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 330, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 748, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bgLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(loading, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(246, 246, 246)))))
                .addGap(0, 63, Short.MAX_VALUE))
        );
        bgLayout.setVerticalGroup(
            bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bgLayout.createSequentialGroup()
                .addContainerGap(45, Short.MAX_VALUE)
                .addComponent(icon, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loading, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(percent, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void iconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_iconMouseClicked
        
    }//GEN-LAST:event_iconMouseClicked

    
    
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
            java.util.logging.Logger.getLogger(loading.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(loading.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(loading.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(loading.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Create and display the form */
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.k33ptoo.components.KGradientPanel bg;
    private javax.swing.JLabel icon;
    private javax.swing.JLabel loading;
    private javax.swing.JLabel percent;
    private javax.swing.JProgressBar progress;
    // End of variables declaration//GEN-END:variables
}
