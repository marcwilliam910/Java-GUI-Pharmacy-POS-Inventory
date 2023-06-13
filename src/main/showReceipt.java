/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author asus
 */
public class showReceipt extends javax.swing.JFrame {

    /**
     * Creates new form showReceipt
     */
    public showReceipt() {
        initComponents();
        showReceiptDesign();
        this.setContentPane(jPanel1);
        this.setBackground(new Color(0,0,0,0));
       
    }
    public void showReceiptDesign(){
        receipt.setText("\t           JLMV Pharmacy\n");
        receipt.setText(receipt.getText() + "\t         Palapat, Hagonoy\n");
        receipt.setText(receipt.getText() + "\t       Bulacan, Philippines\n");
        receipt.setText(receipt.getText() + "\t            09653410782\n");
        receipt.setText(receipt.getText() + "    ---------------------------------------------------------------------------\n");
        receipt.setText(receipt.getText() + "            Item                            Qty.                               Price\n");
        receipt.setText(receipt.getText() + "    ---------------------------------------------------------------------------\n");
        
    }

    
    
     public void showToReceipt(TableModel model, float payment, float change){
         DefaultTableModel order = (DefaultTableModel) model;
         float total = 0;
         int discount = 0;
         float cash = payment;
         float balance = change;
         
         for(int i = 0; i < order.getRowCount(); i++){
             
            String itemName = order.getValueAt(i, 0).toString();
            String quantity = order.getValueAt(i, 1).toString();
            String price = order.getValueAt(i, 2).toString();
            total +=Float.parseFloat(price);
            
            receipt.setText(receipt.getText() + "        " + itemName + "	                  " + quantity + "	                           " + price + "\n");
         }
        receipt.setText(receipt.getText() + "    ---------------------------------------------------------------------------\n");
        receipt.setText(receipt.getText() + "       Subtotal :                                                             "+ total +"\n");
        receipt.setText(receipt.getText() + "       Discount :                                                             " + discount + "\n");
        receipt.setText(receipt.getText() + "    ---------------------------------------------------------------------------\n");
        receipt.setText(receipt.getText() + "      Grand Total :                                                       "+ total +"\n");
        receipt.setText(receipt.getText() + "      Cash :                                                                  "+ cash +"\n");
        receipt.setText(receipt.getText() + "      Balance :                                                              "+ balance +"\n");
        receipt.setText(receipt.getText() + "    ===========================================\n");

        Date dd = new Date();
        SimpleDateFormat datef = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat timef = new SimpleDateFormat("hh:mm a");
        
        String date = datef.format(dd);
        String time = timef.format(dd);
        
        receipt.setText(receipt.getText() + "     Date : " + date + "                               Time : " + time + "\n");
        receipt.setText(receipt.getText() + "   ************************************************************\n");
        receipt.setText(receipt.getText() + "                                Thank you! Come again       \n");
        receipt.setText(receipt.getText() + "   ************************************************************\n");
     }
    
     
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        receipt = new javax.swing.JTextArea();
        kButton1 = new com.k33ptoo.components.KButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        receipt.setEditable(false);
        receipt.setColumns(20);
        receipt.setRows(5);
        jScrollPane3.setViewportView(receipt);

        kButton1.setForeground(new java.awt.Color(51, 51, 51));
        kButton1.setText("Exit");
        kButton1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        kButton1.setkHoverForeGround(new java.awt.Color(0, 0, 0));
        kButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(kButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(96, 96, 96))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 489, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(kButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void kButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kButton1ActionPerformed
       this.dispose();
    }//GEN-LAST:event_kButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(showReceipt.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(showReceipt.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(showReceipt.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(showReceipt.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new showReceipt().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane3;
    private com.k33ptoo.components.KButton kButton1;
    private javax.swing.JTextArea receipt;
    // End of variables declaration//GEN-END:variables
}
