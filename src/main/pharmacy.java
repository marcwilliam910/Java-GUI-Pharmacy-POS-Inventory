/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.RowFilter;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author asus
 */
public class pharmacy extends javax.swing.JFrame {

    DefaultTableModel model1, model2;
    Color defaultColor, clickedColor;
    Font fontHover, fontDefault;
    //for calendar

    public pharmacy() {
        initComponents();
        connect();
        table(selectCommand);
        fontHover = new Font("Segoe UI Black", Font.BOLD, 35);
        fontDefault = new Font("Segoe UI Black", Font.BOLD, 24);
        defaultColor = new Color(0, 0, 0);
        clickedColor = new Color(82, 23, 179);
        barChart();
        buy.setEnabled(false);
        updateBtn.setEnabled(false);
        addNewMedBtn.setEnabled(false);

        //for receipt
        home.setOpaque(true);
        home.setBackground(clickedColor);
        pos.setBackground(defaultColor);
        sales.setBackground(defaultColor);
        expenses.setBackground(defaultColor);
        inventory.setBackground(defaultColor);
    }
       
    //CONNECTOR SA XAMPP MYSQL    
    String url = "jdbc:mysql://localhost:3306/pharma";
    String username = "root";
    String password = "";
    String selectCommand = "SELECT * FROM medicine";

    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    public void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url, username, password);
            System.out.println("Database Connected!");

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Can't Connect!");
        }
    }

    private void table(String command) {

        try {
            int i;
            pst = con.prepareStatement(command);
            rs = pst.executeQuery();

            ResultSetMetaData rss = rs.getMetaData();
            i = rss.getColumnCount();

            //for pos tab
            DefaultTableModel df1 = (DefaultTableModel) medsTable.getModel();
            //for inventory tab
            DefaultTableModel df2 = (DefaultTableModel) medsTable1.getModel();

            df1.setRowCount(0);
            df2.setRowCount(0);
            while (rs.next()) {
                Vector v2 = new Vector();
                for (int a = 1; a <= i; a++) {
                    v2.add(rs.getString("id"));
                    v2.add(rs.getString("meds_name"));
                    v2.add(rs.getString("price"));
                    v2.add(rs.getString("stock"));
                }
                df1.addRow(v2);
                df2.addRow(v2);
            }

            //for table sa pos
            JTableHeader header = medsTable.getTableHeader();
            header.setForeground(Color.blue);
            Font font = new Font("Cambria", Font.BOLD, 20);
            header.setFont(font);

            //medsTable.setBackground(new Color(69,109,176));
            medsTable.setShowGrid(true);
            medsTable.setGridColor(Color.BLACK);
            medsTable.setFont(new Font("Calibri", Font.PLAIN, 18));

            TableColumnModel tableColumn = medsTable.getColumnModel();
            TableColumn column = tableColumn.getColumn(1);
            column.setPreferredWidth(150);

            //for table sa inventory
            JTableHeader header1 = medsTable1.getTableHeader();
            Font font1 = new Font("Cambria", Font.BOLD, 20);
            header1.setFont(font1);
            header1.setForeground(Color.blue);
            //medsTable.setBackground(new Color(69,109,176));
            medsTable1.setShowGrid(true);
            medsTable1.setGridColor(Color.BLACK);
            medsTable1.setFont(new Font("Calibri", Font.PLAIN, 18));

            TableColumnModel tableColumn1 = medsTable1.getColumnModel();
            TableColumn column1 = tableColumn1.getColumn(1);
            column1.setPreferredWidth(150);

            //for table sa order
            JTableHeader header2 = orderTable.getTableHeader();
            Font font2 = new Font("Cambria", Font.BOLD, 20);
            header2.setFont(font2);
            header2.setForeground(Color.red);
            //medsTable.setBackground(new Color(69,109,176));

            orderTable.setFont(new Font("Calibri", Font.PLAIN, 18));

            TableColumnModel tableColumn2 = orderTable.getColumnModel();
            TableColumn column2 = tableColumn2.getColumn(0);
            column2.setPreferredWidth(140);

        } catch (SQLException | NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "No Database Found!");
        }

    }

    public void search(String med) {
        //for pos search
        model1 = (DefaultTableModel) medsTable.getModel();
        //for inventory search
        model2 = (DefaultTableModel) medsTable1.getModel();

        TableRowSorter<DefaultTableModel> trs1 = new TableRowSorter<>(model1);
        TableRowSorter<DefaultTableModel> trs2 = new TableRowSorter<>(model2);

        medsTable.setRowSorter(trs1);
        medsTable1.setRowSorter(trs2);

        trs1.setRowFilter(RowFilter.regexFilter("(?i)" + med));
        trs2.setRowFilter(RowFilter.regexFilter("(?i)" + med));
    }

    //for chart
    public void barChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.setValue(125000, "January", "Jan");
        dataset.setValue(94543, "February", "Feb");
        dataset.setValue(100255, "March", "Mar");
        dataset.setValue(80233, "April", "Apr");
        dataset.setValue(185000, "May", "May");
        dataset.setValue(199999, "May", "May");
        dataset.setValue(90123, "June", "June");
        dataset.setValue(79320, "July", "July");
        dataset.setValue(100219, "August", "Aug");
        dataset.setValue(123878, "September", "Sept");
        dataset.setValue(99807, "October", "Oct");
        dataset.setValue(89067, "November", "Nov");
        dataset.setValue(150000, "December", "Dec");

        JFreeChart chart = ChartFactory.createBarChart3D("", "Months", "Income", dataset, PlotOrientation.VERTICAL, true, true, false);
        chart.setBackgroundPaint(new Color(96, 247, 190));

        CategoryPlot plot = chart.getCategoryPlot();

        Font labelFont = new Font("Arial", Font.BOLD, 12);
        Font horizontalFont = new Font("Arial", Font.PLAIN, 10);
        Font verticalFont = new Font("Arial", Font.PLAIN, 10);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelFont(labelFont);
        domainAxis.setTickLabelFont(verticalFont);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLabelFont(labelFont);
        rangeAxis.setTickLabelFont(horizontalFont);

        plot.setRangeGridlinePaint(Color.black);
        ChartPanel chartPanel = new ChartPanel(chart);

        barChartPanel.removeAll();
        barChartPanel.add(chartPanel, BorderLayout.CENTER);
        barChartPanel.validate();

    }

    //for receipt
    int total = 0;
    int qty;

    public void showInOrderTable() {
        try {
            qty = Integer.parseInt(JOptionPane.showInputDialog("Quantity"));

            int selectrow = medsTable.getSelectedRow();
            String itemName = medsTable.getValueAt(selectrow, 1).toString();
            int price = Integer.parseInt(medsTable.getValueAt(selectrow, 2).toString());
            int quantity = qty;
            price *= quantity;

            total += price;

            DefaultTableModel df = (DefaultTableModel) orderTable.getModel();
            Vector v = new Vector();

            v.add(itemName);
            v.add(quantity);
            v.add(price);

            df.addRow(v);
            totalTxt.setText(String.valueOf(total));

        } catch (NumberFormatException | NullPointerException e) {
            JOptionPane.showMessageDialog(null, "Please Enter Valid Amount!");
        }

    }

    public void buy() {
        for (int row = 0; row < orderTable.getRowCount(); row++) {
            try {
                int quantity = (int) orderTable.getValueAt(row, 1);
                String name = (String) orderTable.getValueAt(row, 0);
                pst = con.prepareStatement("UPDATE medicine SET stock = (stock - ?) WHERE meds_name = ?");
                pst.setInt(1, quantity);
                pst.setString(2, name);
                pst.executeUpdate();

            } catch (SQLException ex) {
                Logger.getLogger(pharmacy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        showReceipt.setSelected(false);
        totalTxt.setText("");
        payment.setText("");
        change.setText("");
        search.setText("");
        total = 0;

        JOptionPane.showMessageDialog(null, "Done!");
        table(selectCommand);
        buy.setEnabled(false);
    }

    public void soldMedsToDB() {
        try {
            //LocalDateTime currentDateTime = LocalDateTime.of(2023, 6, 15, 9, 30 , 0);
            //June 15, 2023 9:30AM  hh:mm:ss a - AM/PM indicator
            LocalDateTime currentDateTime = LocalDateTime.now();
            String formattedDateTime = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String user = new login().user;
            for (int i = 0; i < orderTable.getRowCount(); i++) {
                String name = (String) orderTable.getValueAt(i, 0);
                int quan = (int) orderTable.getValueAt(i, 1);
                int price = (int) orderTable.getValueAt(i, 2);

                pst = con.prepareStatement("INSERT INTO sold_medicine(product_name, quantity, total_price, date_sold, seller) VALUES(?,?,?,?,?)");
                pst.setString(1, name);
                pst.setInt(2, quan);
                pst.setInt(3, price);
                pst.setString(4, formattedDateTime);
                pst.setString(5, user);
                pst.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(pharmacy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        kGradientPanel2 = new com.k33ptoo.components.KGradientPanel();
        home = new javax.swing.JPanel();
        homeLbl = new javax.swing.JLabel();
        pos = new javax.swing.JPanel();
        posLbl = new javax.swing.JLabel();
        sales = new javax.swing.JPanel();
        salesLbl = new javax.swing.JLabel();
        expenses = new javax.swing.JPanel();
        expLbl = new javax.swing.JLabel();
        inventory = new javax.swing.JPanel();
        invLbl = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        logout = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        homeTab = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        dashboardOrderPanel = new com.k33ptoo.components.KGradientPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        dashboardOrderLbl = new javax.swing.JLabel();
        dashboardSalesPanel = new com.k33ptoo.components.KGradientPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        dashboardSalesLbl = new javax.swing.JLabel();
        dashboardIncomePanel = new com.k33ptoo.components.KGradientPanel();
        jLabel31 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        dashboardIncomeLbl = new javax.swing.JLabel();
        posTab = new javax.swing.JPanel();
        buy = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        medsTable = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        search = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        orderTable = new javax.swing.JTable();
        totalTxt = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        payment = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        change = new javax.swing.JTextField();
        showReceipt = new javax.swing.JCheckBox();
        salesTab = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        calendarPanel1 = new com.github.lgooddatepicker.components.CalendarPanel();
        jLabel32 = new javax.swing.JLabel();
        barChartPanel = new javax.swing.JPanel();
        expensesTab = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        inventoryTab = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        kGradientPanel5 = new com.k33ptoo.components.KGradientPanel();
        jLabel6 = new javax.swing.JLabel();
        currentName = new javax.swing.JTextField();
        currentStock = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        currentPrice = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        addStock = new javax.swing.JTextField();
        updatePrice = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        updateBtn = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        newName = new javax.swing.JTextField();
        newStock = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        newPrice = new javax.swing.JTextField();
        addNewMedBtn = new javax.swing.JButton();
        jLabel30 = new javax.swing.JLabel();
        clear = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        medsTable1 = new javax.swing.JTable();
        searchInventory = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        home.setOpaque(false);
        home.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                homeMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                homeMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                homeMouseExited(evt);
            }
        });

        homeLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        homeLbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/home.png"))); // NOI18N
        homeLbl.setText(" Dashboard");
        homeLbl.setBackground(new java.awt.Color(255, 102, 102));
        homeLbl.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        homeLbl.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout homeLayout = new javax.swing.GroupLayout(home);
        home.setLayout(homeLayout);
        homeLayout.setHorizontalGroup(
            homeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(homeLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        homeLayout.setVerticalGroup(
            homeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(homeLbl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
        );

        pos.setOpaque(false);
        pos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                posMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                posMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                posMouseExited(evt);
            }
        });

        posLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        posLbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/pos.png"))); // NOI18N
        posLbl.setText(" Point of Sale");
        posLbl.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        posLbl.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout posLayout = new javax.swing.GroupLayout(pos);
        pos.setLayout(posLayout);
        posLayout.setHorizontalGroup(
            posLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(posLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
        );
        posLayout.setVerticalGroup(
            posLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(posLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
        );

        sales.setOpaque(false);
        sales.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                salesMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                salesMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                salesMouseExited(evt);
            }
        });

        salesLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        salesLbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/sales.png"))); // NOI18N
        salesLbl.setText("Sales");
        salesLbl.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        salesLbl.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout salesLayout = new javax.swing.GroupLayout(sales);
        sales.setLayout(salesLayout);
        salesLayout.setHorizontalGroup(
            salesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(salesLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        salesLayout.setVerticalGroup(
            salesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(salesLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
        );

        expenses.setOpaque(false);
        expenses.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                expensesMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                expensesMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                expensesMouseExited(evt);
            }
        });

        expLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        expLbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/exp.png"))); // NOI18N
        expLbl.setText("  Expenses");
        expLbl.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        expLbl.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout expensesLayout = new javax.swing.GroupLayout(expenses);
        expenses.setLayout(expensesLayout);
        expensesLayout.setHorizontalGroup(
            expensesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(expLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        expensesLayout.setVerticalGroup(
            expensesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(expLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
        );

        inventory.setOpaque(false);
        inventory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                inventoryMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                inventoryMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                inventoryMouseExited(evt);
            }
        });

        invLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        invLbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/inventory.png"))); // NOI18N
        invLbl.setText(" Inventory");
        invLbl.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        invLbl.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout inventoryLayout = new javax.swing.GroupLayout(inventory);
        inventory.setLayout(inventoryLayout);
        inventoryLayout.setHorizontalGroup(
            inventoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(invLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        inventoryLayout.setVerticalGroup(
            inventoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(invLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
        );

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/logo (2).png"))); // NOI18N

        jLabel8.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));

        logout.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/log_out.png"))); // NOI18N
        logout.setText("Log out");
        logout.setFont(new java.awt.Font("Segoe UI Light", 3, 14)); // NOI18N
        logout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutMouseExited(evt);
            }
        });

        javax.swing.GroupLayout kGradientPanel2Layout = new javax.swing.GroupLayout(kGradientPanel2);
        kGradientPanel2.setLayout(kGradientPanel2Layout);
        kGradientPanel2Layout.setHorizontalGroup(
            kGradientPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kGradientPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(kGradientPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(kGradientPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(inventory, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(expenses, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(sales, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(home, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(24, 24, 24))
            .addGroup(kGradientPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(kGradientPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(logout))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        kGradientPanel2Layout.setVerticalGroup(
            kGradientPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel2Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(home, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(pos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(sales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(expenses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(inventory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addComponent(logout)
                .addContainerGap())
        );

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("X");
        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel19.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel19MouseClicked(evt);
            }
        });

        dashboardOrderPanel.setkEndColor(new java.awt.Color(73, 254, 73));
        dashboardOrderPanel.setkGradientFocus(300);
        dashboardOrderPanel.setkStartColor(new java.awt.Color(95, 95, 249));
        dashboardOrderPanel.setPreferredSize(new java.awt.Dimension(220, 100));
        dashboardOrderPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dashboardOrderPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dashboardOrderPanelMouseExited(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Today's Orders");
        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N

        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/dashboard_order.png"))); // NOI18N

        dashboardOrderLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dashboardOrderLbl.setText("25");
        dashboardOrderLbl.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N

        javax.swing.GroupLayout dashboardOrderPanelLayout = new javax.swing.GroupLayout(dashboardOrderPanel);
        dashboardOrderPanel.setLayout(dashboardOrderPanelLayout);
        dashboardOrderPanelLayout.setHorizontalGroup(
            dashboardOrderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashboardOrderPanelLayout.createSequentialGroup()
                .addGroup(dashboardOrderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dashboardOrderPanelLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(dashboardOrderPanelLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(dashboardOrderLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jLabel33)
                .addContainerGap(12, Short.MAX_VALUE))
        );
        dashboardOrderPanelLayout.setVerticalGroup(
            dashboardOrderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dashboardOrderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dashboardOrderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(dashboardOrderPanelLayout.createSequentialGroup()
                        .addComponent(dashboardOrderLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        dashboardSalesPanel.setkEndColor(new java.awt.Color(73, 254, 73));
        dashboardSalesPanel.setkGradientFocus(300);
        dashboardSalesPanel.setkStartColor(new java.awt.Color(95, 95, 249));
        dashboardSalesPanel.setPreferredSize(new java.awt.Dimension(220, 100));
        dashboardSalesPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dashboardSalesPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dashboardSalesPanelMouseExited(evt);
            }
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Today's Sales");
        jLabel3.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N

        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/dashboard_sale.png"))); // NOI18N

        dashboardSalesLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dashboardSalesLbl.setText("1500");
        dashboardSalesLbl.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        dashboardSalesLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dashboardSalesLblMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dashboardSalesLblMouseExited(evt);
            }
        });

        javax.swing.GroupLayout dashboardSalesPanelLayout = new javax.swing.GroupLayout(dashboardSalesPanel);
        dashboardSalesPanel.setLayout(dashboardSalesPanelLayout);
        dashboardSalesPanelLayout.setHorizontalGroup(
            dashboardSalesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashboardSalesPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(dashboardSalesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                    .addComponent(dashboardSalesLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jLabel34)
                .addGap(24, 24, 24))
        );
        dashboardSalesPanelLayout.setVerticalGroup(
            dashboardSalesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dashboardSalesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dashboardSalesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(dashboardSalesPanelLayout.createSequentialGroup()
                        .addGap(0, 9, Short.MAX_VALUE)
                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(dashboardSalesPanelLayout.createSequentialGroup()
                        .addComponent(dashboardSalesLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(8, 8, 8)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        dashboardIncomePanel.setkEndColor(new java.awt.Color(73, 254, 73));
        dashboardIncomePanel.setkGradientFocus(300);
        dashboardIncomePanel.setkStartColor(new java.awt.Color(95, 95, 249));
        dashboardIncomePanel.setPreferredSize(new java.awt.Dimension(220, 100));
        dashboardIncomePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dashboardIncomePanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dashboardIncomePanelMouseExited(evt);
            }
        });

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("Income this Month");
        jLabel31.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N

        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/dashboard_income.png"))); // NOI18N

        dashboardIncomeLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dashboardIncomeLbl.setText("50000");
        dashboardIncomeLbl.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        dashboardIncomeLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dashboardIncomeLblMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dashboardIncomeLblMouseExited(evt);
            }
        });

        javax.swing.GroupLayout dashboardIncomePanelLayout = new javax.swing.GroupLayout(dashboardIncomePanel);
        dashboardIncomePanel.setLayout(dashboardIncomePanelLayout);
        dashboardIncomePanelLayout.setHorizontalGroup(
            dashboardIncomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashboardIncomePanelLayout.createSequentialGroup()
                .addGroup(dashboardIncomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dashboardIncomeLbl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(dashboardIncomePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel36)
                .addGap(22, 22, 22))
        );
        dashboardIncomePanelLayout.setVerticalGroup(
            dashboardIncomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashboardIncomePanelLayout.createSequentialGroup()
                .addGroup(dashboardIncomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(dashboardIncomePanelLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabel36))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, dashboardIncomePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(dashboardIncomeLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout homeTabLayout = new javax.swing.GroupLayout(homeTab);
        homeTab.setLayout(homeTabLayout);
        homeTabLayout.setHorizontalGroup(
            homeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homeTabLayout.createSequentialGroup()
                .addContainerGap(115, Short.MAX_VALUE)
                .addComponent(dashboardOrderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(dashboardSalesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(dashboardIncomePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(116, 116, 116))
        );
        homeTabLayout.setVerticalGroup(
            homeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, homeTabLayout.createSequentialGroup()
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(homeTabLayout.createSequentialGroup()
                .addContainerGap(42, Short.MAX_VALUE)
                .addGroup(homeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dashboardOrderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dashboardSalesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dashboardIncomePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(560, 560, 560))
        );

        jTabbedPane1.addTab("tab1", homeTab);

        buy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/buy.png"))); // NOI18N
        buy.setText(" Buy");
        buy.setBackground(new java.awt.Color(255, 255, 255));
        buy.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        buy.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        buy.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                buyMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                buyMouseExited(evt);
            }
        });
        buy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buyActionPerformed(evt);
            }
        });

        medsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Medicine Name", "Price", "Stock"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        medsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                medsTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(medsTable);
        if (medsTable.getColumnModel().getColumnCount() > 0) {
            medsTable.getColumnModel().getColumn(1).setResizable(false);
            medsTable.getColumnModel().getColumn(2).setResizable(false);
            medsTable.getColumnModel().getColumn(3).setResizable(false);
        }

        jLabel11.setText("Search Meds");
        jLabel11.setBackground(new java.awt.Color(0, 0, 0));
        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        search.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        search.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        search.setOpaque(false);
        search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchActionPerformed(evt);
            }
        });
        search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                searchKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchKeyReleased(evt);
            }
        });

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("X");
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Order");
        jLabel12.setBackground(new java.awt.Color(0, 0, 0));
        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        orderTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Medicine Name", "Quantity", "Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        orderTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                orderTableMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(orderTable);
        if (orderTable.getColumnModel().getColumnCount() > 0) {
            orderTable.getColumnModel().getColumn(0).setResizable(false);
            orderTable.getColumnModel().getColumn(1).setResizable(false);
            orderTable.getColumnModel().getColumn(2).setResizable(false);
        }

        totalTxt.setEditable(false);
        totalTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        totalTxt.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        totalTxt.setForeground(new java.awt.Color(255, 0, 51));

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Total");
        jLabel15.setBackground(new java.awt.Color(0, 0, 0));
        jLabel15.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Payment");
        jLabel16.setBackground(new java.awt.Color(0, 0, 0));
        jLabel16.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N

        payment.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        payment.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        payment.setForeground(new java.awt.Color(0, 51, 255));
        payment.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                paymentKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                paymentKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                paymentKeyTyped(evt);
            }
        });

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("Change");
        jLabel17.setBackground(new java.awt.Color(0, 0, 0));
        jLabel17.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N

        change.setEditable(false);
        change.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        change.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        change.setForeground(new java.awt.Color(255, 0, 0));
        change.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeActionPerformed(evt);
            }
        });

        showReceipt.setText("Show Receipt");
        showReceipt.setBackground(new java.awt.Color(255, 255, 255));
        showReceipt.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        showReceipt.setForeground(new java.awt.Color(0, 0, 255));
        showReceipt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showReceiptActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout posTabLayout = new javax.swing.GroupLayout(posTab);
        posTab.setLayout(posTabLayout);
        posTabLayout.setHorizontalGroup(
            posTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(posTabLayout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(288, 288, 288)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, posTabLayout.createSequentialGroup()
                .addContainerGap(35, Short.MAX_VALUE)
                .addGroup(posTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, posTabLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(116, 116, 116))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, posTabLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(posTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(posTabLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(posTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(posTabLayout.createSequentialGroup()
                                        .addGroup(posTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(posTabLayout.createSequentialGroup()
                                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(payment, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(posTabLayout.createSequentialGroup()
                                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(change, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(posTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(posTabLayout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(totalTxt))
                                            .addGroup(posTabLayout.createSequentialGroup()
                                                .addGap(37, 37, 37)
                                                .addComponent(showReceipt))))))
                            .addGroup(posTabLayout.createSequentialGroup()
                                .addGap(46, 46, 46)
                                .addComponent(buy, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(132, 132, 132))))
        );
        posTabLayout.setVerticalGroup(
            posTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(posTabLayout.createSequentialGroup()
                .addGroup(posTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(posTabLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addGroup(posTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(totalTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(payment, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(posTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(change, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(showReceipt))
                        .addGap(18, 18, 18)
                        .addComponent(buy, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(posTabLayout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(posTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 493, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(87, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab2", posTab);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("X");
        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });

        calendarPanel1.setBackground(new java.awt.Color(255, 255, 255));
        calendarPanel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel32.setFont(new java.awt.Font("Segoe UI Semibold", 3, 18)); // NOI18N
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText("Your Monthly Income Chart");

        barChartPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout salesTabLayout = new javax.swing.GroupLayout(salesTab);
        salesTab.setLayout(salesTabLayout);
        salesTabLayout.setHorizontalGroup(
            salesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(salesTabLayout.createSequentialGroup()
                .addGap(157, 157, 157)
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(115, 115, 115))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, salesTabLayout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addComponent(barChartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 469, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 75, Short.MAX_VALUE)
                .addComponent(calendarPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(193, 193, 193))
        );
        salesTabLayout.setVerticalGroup(
            salesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(salesTabLayout.createSequentialGroup()
                .addGroup(salesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(salesTabLayout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(calendarPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(salesTabLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel32)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(barChartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(363, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab3", salesTab);

        jLabel4.setText("4");
        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("X");
        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel9MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout expensesTabLayout = new javax.swing.GroupLayout(expensesTab);
        expensesTab.setLayout(expensesTabLayout);
        expensesTabLayout.setHorizontalGroup(
            expensesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(expensesTabLayout.createSequentialGroup()
                .addContainerGap(553, Short.MAX_VALUE)
                .addGroup(expensesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, expensesTabLayout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(323, 323, 323))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, expensesTabLayout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(116, 116, 116))))
        );
        expensesTabLayout.setVerticalGroup(
            expensesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(expensesTabLayout.createSequentialGroup()
                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                .addGap(161, 161, 161)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(413, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab4", expensesTab);

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("X");
        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel10MouseClicked(evt);
            }
        });

        kGradientPanel5.setkBorderRadius(30);
        kGradientPanel5.setkEndColor(new java.awt.Color(73, 254, 73));
        kGradientPanel5.setkGradientFocus(300);
        kGradientPanel5.setkStartColor(new java.awt.Color(95, 95, 249));

        jLabel6.setText("Name :");
        jLabel6.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N

        currentName.setEditable(false);
        currentName.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        currentStock.setEditable(false);
        currentStock.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel20.setText("Current Stock :");
        jLabel20.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N

        jLabel21.setText("Current Price :");
        jLabel21.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N

        currentPrice.setEditable(false);
        currentPrice.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel22.setText("Add Stock : ");
        jLabel22.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 0, 0));

        addStock.setEditable(false);
        addStock.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        addStock.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                addStockKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                addStockKeyTyped(evt);
            }
        });

        updatePrice.setEditable(false);
        updatePrice.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        updatePrice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                updatePriceKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                updatePriceKeyTyped(evt);
            }
        });

        jLabel23.setText("Update Price(optional) :");
        jLabel23.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 0, 0));

        updateBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/add_med.png"))); // NOI18N
        updateBtn.setText("Update");
        updateBtn.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        updateBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                updateBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                updateBtnMouseExited(evt);
            }
        });
        updateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateBtnActionPerformed(evt);
            }
        });

        jLabel24.setText("Update Stock/Price");
        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N

        jLabel26.setText("Name :");
        jLabel26.setFont(new java.awt.Font("Times New Roman", 1, 20)); // NOI18N

        jLabel25.setText("Add New Medicine");
        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N

        newName.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        newName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                newNameKeyReleased(evt);
            }
        });

        newStock.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        newStock.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                newStockKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                newStockKeyTyped(evt);
            }
        });

        jLabel27.setText("Stock :");
        jLabel27.setFont(new java.awt.Font("Times New Roman", 1, 20)); // NOI18N

        jLabel28.setText("Price :");
        jLabel28.setFont(new java.awt.Font("Times New Roman", 1, 20)); // NOI18N

        newPrice.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        newPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newPriceActionPerformed(evt);
            }
        });
        newPrice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                newPriceKeyReleased(evt);
            }
        });

        addNewMedBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/add_med.png"))); // NOI18N
        addNewMedBtn.setText("Add");
        addNewMedBtn.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        addNewMedBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addNewMedBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addNewMedBtnMouseExited(evt);
            }
        });
        addNewMedBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewMedBtnActionPerformed(evt);
            }
        });

        jLabel30.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));

        clear.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        clear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/clear_text.png"))); // NOI18N
        clear.setText("Clear all");
        clear.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        clear.setForeground(new java.awt.Color(255, 255, 255));
        clear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clearMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                clearMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                clearMouseExited(evt);
            }
        });

        javax.swing.GroupLayout kGradientPanel5Layout = new javax.swing.GroupLayout(kGradientPanel5);
        kGradientPanel5.setLayout(kGradientPanel5Layout);
        kGradientPanel5Layout.setHorizontalGroup(
            kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel5Layout.createSequentialGroup()
                .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, kGradientPanel5Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(kGradientPanel5Layout.createSequentialGroup()
                                .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(kGradientPanel5Layout.createSequentialGroup()
                                        .addComponent(jLabel21)
                                        .addGap(23, 23, 23))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kGradientPanel5Layout.createSequentialGroup()
                                        .addComponent(jLabel20)
                                        .addGap(18, 18, 18)))
                                .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(currentStock)
                                    .addComponent(currentPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(kGradientPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(83, 83, 83)
                                .addComponent(currentName, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(kGradientPanel5Layout.createSequentialGroup()
                                    .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kGradientPanel5Layout.createSequentialGroup()
                                            .addComponent(jLabel22)
                                            .addGap(54, 54, 54)
                                            .addComponent(addStock, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(kGradientPanel5Layout.createSequentialGroup()
                                            .addComponent(jLabel23)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(updatePrice, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGap(0, 0, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kGradientPanel5Layout.createSequentialGroup()
                                    .addGap(22, 22, 22)
                                    .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(kGradientPanel5Layout.createSequentialGroup()
                                            .addGap(69, 69, 69)
                                            .addComponent(updateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(kGradientPanel5Layout.createSequentialGroup()
                                            .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(jLabel28)
                                                .addComponent(jLabel26)
                                                .addComponent(jLabel27))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(newName, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                    .addComponent(newPrice, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                                                    .addComponent(newStock, javax.swing.GroupLayout.Alignment.LEADING)))))))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, kGradientPanel5Layout.createSequentialGroup()
                        .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, kGradientPanel5Layout.createSequentialGroup()
                                .addGap(78, 78, 78)
                                .addComponent(jLabel24))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, kGradientPanel5Layout.createSequentialGroup()
                                .addGap(83, 83, 83)
                                .addComponent(jLabel25)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(kGradientPanel5Layout.createSequentialGroup()
                        .addGap(0, 23, Short.MAX_VALUE)
                        .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(kGradientPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(clear, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addNewMedBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(11, Short.MAX_VALUE))
        );
        kGradientPanel5Layout.setVerticalGroup(
            kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel5Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel24)
                .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kGradientPanel5Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jLabel6))
                    .addGroup(kGradientPanel5Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(currentName, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5)
                .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(currentStock, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(currentPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kGradientPanel5Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel22))
                    .addGroup(kGradientPanel5Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(addStock, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updatePrice, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addGap(18, 18, 18)
                .addComponent(updateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17)
                .addComponent(jLabel25)
                .addGap(18, 18, 18)
                .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(newName, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28))
                .addGap(10, 10, 10)
                .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newStock, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kGradientPanel5Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(addNewMedBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(kGradientPanel5Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(clear, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(27, 27, 27))
        );

        medsTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Medicine Name", "Price", "Stock"
            }
        ));
        medsTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                medsTable1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(medsTable1);

        searchInventory.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        searchInventory.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        searchInventory.setForeground(new java.awt.Color(255, 0, 0));
        searchInventory.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                searchInventoryKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchInventoryKeyReleased(evt);
            }
        });

        jLabel29.setText("Search Meds");
        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        javax.swing.GroupLayout inventoryTabLayout = new javax.swing.GroupLayout(inventoryTab);
        inventoryTab.setLayout(inventoryTabLayout);
        inventoryTabLayout.setHorizontalGroup(
            inventoryTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inventoryTabLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(inventoryTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(inventoryTabLayout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addGap(18, 18, 18)
                        .addComponent(searchInventory, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 481, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(kGradientPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(117, 117, 117))
        );
        inventoryTabLayout.setVerticalGroup(
            inventoryTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, inventoryTabLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(inventoryTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchInventory, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 518, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(96, 96, 96))
            .addGroup(inventoryTabLayout.createSequentialGroup()
                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                .addGap(670, 670, 670))
            .addGroup(inventoryTabLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(kGradientPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 592, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab5", inventoryTab);

        jPanel1.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(-30, -32, 1020, 730));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(kGradientPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 873, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 626, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void homeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeMouseClicked

        home.setOpaque(true);
        pos.setOpaque(false);
        sales.setOpaque(false);
        expenses.setOpaque(false);
        inventory.setOpaque(false);

        home.setBackground(clickedColor);
        pos.setBackground(defaultColor);
        sales.setBackground(defaultColor);
        expenses.setBackground(defaultColor);
        inventory.setBackground(defaultColor);

        jTabbedPane1.setSelectedIndex(0);
    }//GEN-LAST:event_homeMouseClicked

    private void homeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeMouseEntered
        homeLbl.setForeground(Color.black);
    }//GEN-LAST:event_homeMouseEntered

    private void homeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeMouseExited
        homeLbl.setForeground(Color.white);
    }//GEN-LAST:event_homeMouseExited

    private void posMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_posMouseClicked
        home.setOpaque(false);
        pos.setOpaque(true);
        sales.setOpaque(false);
        expenses.setOpaque(false);
        inventory.setOpaque(false);

        home.setBackground(defaultColor);
        pos.setBackground(clickedColor);
        sales.setBackground(defaultColor);
        expenses.setBackground(defaultColor);
        inventory.setBackground(defaultColor);

        jTabbedPane1.setSelectedIndex(1);
    }//GEN-LAST:event_posMouseClicked

    private void posMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_posMouseEntered
        posLbl.setForeground(Color.black);
    }//GEN-LAST:event_posMouseEntered

    private void posMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_posMouseExited
        posLbl.setForeground(Color.white);
    }//GEN-LAST:event_posMouseExited

    private void salesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_salesMouseClicked
        home.setOpaque(false);
        pos.setOpaque(false);
        sales.setOpaque(true);
        expenses.setOpaque(false);
        inventory.setOpaque(false);

        home.setBackground(defaultColor);
        pos.setBackground(defaultColor);
        sales.setBackground(clickedColor);
        expenses.setBackground(defaultColor);
        inventory.setBackground(defaultColor);

        jTabbedPane1.setSelectedIndex(2);
    }//GEN-LAST:event_salesMouseClicked

    private void salesMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_salesMouseEntered

        salesLbl.setForeground(Color.black);
    }//GEN-LAST:event_salesMouseEntered

    private void salesMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_salesMouseExited

        salesLbl.setForeground(Color.white);
    }//GEN-LAST:event_salesMouseExited

    private void expensesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_expensesMouseClicked
        home.setOpaque(false);
        pos.setOpaque(false);
        sales.setOpaque(false);
        expenses.setOpaque(true);
        inventory.setOpaque(false);

        home.setBackground(defaultColor);
        pos.setBackground(defaultColor);
        sales.setBackground(defaultColor);
        expenses.setBackground(clickedColor);
        inventory.setBackground(defaultColor);

        jTabbedPane1.setSelectedIndex(3);
    }//GEN-LAST:event_expensesMouseClicked

    private void expensesMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_expensesMouseEntered

        expLbl.setForeground(Color.black);
    }//GEN-LAST:event_expensesMouseEntered

    private void expensesMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_expensesMouseExited

        expLbl.setForeground(Color.white);
    }//GEN-LAST:event_expensesMouseExited

    private void inventoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inventoryMouseClicked
        home.setOpaque(false);
        pos.setOpaque(false);
        sales.setOpaque(false);
        expenses.setOpaque(false);
        inventory.setOpaque(true);

        home.setBackground(defaultColor);
        pos.setBackground(defaultColor);
        sales.setBackground(defaultColor);
        expenses.setBackground(defaultColor);
        inventory.setBackground(clickedColor);

        jTabbedPane1.setSelectedIndex(4);
    }//GEN-LAST:event_inventoryMouseClicked

    private void inventoryMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inventoryMouseEntered

        invLbl.setForeground(Color.black);
    }//GEN-LAST:event_inventoryMouseEntered

    private void inventoryMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inventoryMouseExited

        invLbl.setForeground(Color.white);
    }//GEN-LAST:event_inventoryMouseExited

    private void buyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buyActionPerformed
        int isSure = JOptionPane.showConfirmDialog(null, "Are you sure you want to buy?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (isSure == JOptionPane.YES_OPTION) {
            try {
                float pay = Float.parseFloat(payment.getText());
                float tot = Float.parseFloat(totalTxt.getText());
                float ch = Float.parseFloat(change.getText());

                if (pay < tot) {
                    JOptionPane.showMessageDialog(this, "Not Enough Payment");
                } else if (showReceipt.isSelected()) {
                    buy();
                    showReceipt r = new showReceipt();
                    DefaultTableModel order = (DefaultTableModel) orderTable.getModel();
                    r.setVisible(true);
                    r.showToReceipt(order, pay, ch);
                    soldMedsToDB();
                    order.setRowCount(0);
                    
                } else {
                    buy();
                    soldMedsToDB();
                    DefaultTableModel order = (DefaultTableModel) orderTable.getModel();
                    order.setRowCount(0);                  
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Enter Valid Amount");
            }
        }
    }//GEN-LAST:event_buyActionPerformed

    private void medsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_medsTableMouseClicked
        int selectrow = medsTable.getSelectedRow();
        int stock = Integer.parseInt(medsTable.getValueAt(selectrow, 3).toString());

        if (stock <= 0) {
            JOptionPane.showMessageDialog(this, "No Stock!");
        } else {
            showInOrderTable();
        }
    }//GEN-LAST:event_medsTableMouseClicked

    private void searchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchKeyPressed

    private void searchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchKeyReleased
        search(search.getText());
    }//GEN-LAST:event_searchKeyReleased

    private void searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchActionPerformed

    private void buyMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buyMouseEntered
        buy.setBackground(Color.red);
        buy.setForeground(Color.white);
    }//GEN-LAST:event_buyMouseEntered

    private void buyMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buyMouseExited
        buy.setBackground(Color.white);
        buy.setForeground(Color.black);
    }//GEN-LAST:event_buyMouseExited

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel2MouseClicked

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel7MouseClicked

    private void jLabel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel9MouseClicked

    private void jLabel19MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel19MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel19MouseClicked

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel10MouseClicked

    private void medsTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_medsTable1MouseClicked
        String[] options = {"Add Stock", "Remove to list"};
        int choice = JOptionPane.showOptionDialog(null, "What do you want to do?", "Question", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == JOptionPane.YES_OPTION) {
            int selectrow = medsTable1.getSelectedRow();
            currentName.setText(medsTable1.getValueAt(selectrow, 1).toString());
            currentStock.setText(medsTable1.getValueAt(selectrow, 3).toString());
            currentPrice.setText(medsTable1.getValueAt(selectrow, 2).toString());

            addStock.setEditable(true);
            updatePrice.setEditable(true);
        } else if (choice == JOptionPane.NO_OPTION) {
            int isSure = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this to database?");
            if (isSure == JOptionPane.YES_OPTION) {
                try {
                    int selectedRow = medsTable1.getSelectedRow();

                    //get the name para madelete sa database
                    String name = medsTable1.getValueAt(selectedRow, 1).toString();

                    pst = con.prepareStatement("DELETE FROM medicine WHERE meds_name = ?");
                    pst.setString(1, name);
                    pst.executeUpdate();

                    table(selectCommand);
                    currentName.setText("");
                    currentPrice.setText("");
                    currentStock.setText("");
                    searchInventory.setText("");
                    JOptionPane.showMessageDialog(null, "Deleting Success");

                } catch (SQLException ex) {
                    Logger.getLogger(pharmacy.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {

            }

        }
    }//GEN-LAST:event_medsTable1MouseClicked

    private void searchInventoryKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchInventoryKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchInventoryKeyPressed

    private void searchInventoryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchInventoryKeyReleased
        search(searchInventory.getText());
    }//GEN-LAST:event_searchInventoryKeyReleased

    private void newPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newPriceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_newPriceActionPerformed

    private void updateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateBtnActionPerformed
        try {
            int isSure = JOptionPane.showConfirmDialog(null, "Are you sure?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (isSure == JOptionPane.YES_OPTION) {
                try {
                    //para makuha yung price sa table
                    int selectrow = medsTable1.getSelectedRow();
                    int updatePrice;

                    String medsName = currentName.getText();
                    int updateStock;

                    //para kapag stock lang mag dadagdag hindi sa price
                    if (addStock.getText().isEmpty()) {
                        updateStock = 0;
                    } else {
                        updateStock = Integer.parseInt(addStock.getText());
                    }

                    //para naman pag price lang uupdate pero yung stock hindi
                    if (this.updatePrice.getText().isEmpty()) {
                        updatePrice = Integer.parseInt(medsTable1.getValueAt(selectrow, 2).toString());
                    } else {
                        updatePrice = Integer.parseInt(this.updatePrice.getText());
                    }

                    pst = con.prepareStatement("UPDATE medicine SET stock = (stock + ?), price = ? WHERE meds_name = ?");
                    pst.setInt(1, updateStock);
                    pst.setInt(2, updatePrice);
                    pst.setString(3, medsName);
                    pst.executeUpdate();

                    searchInventory.setText("");
                    currentName.setText("");
                    currentPrice.setText("");
                    currentStock.setText("");
                    addStock.setText("");
                    this.updatePrice.setText("");

                    JOptionPane.showMessageDialog(null, "Stock and Price Updated!");
                    table(selectCommand);
                    updateBtn.setEnabled(false);

                    //para disabled yung mga textfield
                    if (currentName.getText().isEmpty()) {
                        addStock.setEditable(false);
                        this.updatePrice.setEditable(false);
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(pharmacy.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {

            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter valid number!");
        }
    }//GEN-LAST:event_updateBtnActionPerformed

    private void updateBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_updateBtnMouseEntered
        updateBtn.setBackground(Color.red);
        updateBtn.setForeground(Color.white);
    }//GEN-LAST:event_updateBtnMouseEntered

    private void updateBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_updateBtnMouseExited
        updateBtn.setBackground(Color.white);
        updateBtn.setForeground(Color.black);
    }//GEN-LAST:event_updateBtnMouseExited

    private void addNewMedBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addNewMedBtnMouseEntered
        addNewMedBtn.setBackground(Color.red);
        addNewMedBtn.setForeground(Color.white);
    }//GEN-LAST:event_addNewMedBtnMouseEntered

    private void addNewMedBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addNewMedBtnMouseExited
        addNewMedBtn.setBackground(Color.white);
        addNewMedBtn.setForeground(Color.black);
    }//GEN-LAST:event_addNewMedBtnMouseExited

    private void addStockKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_addStockKeyReleased
        if (addStock.getText().isEmpty() || currentName.getText().isEmpty()) {
            updateBtn.setEnabled(false);
        } else {
            updateBtn.setEnabled(true);
        }
    }//GEN-LAST:event_addStockKeyReleased

    private void addNewMedBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewMedBtnActionPerformed
        try {
            int isSure = JOptionPane.showConfirmDialog(null, "Are you sure?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (isSure == JOptionPane.YES_OPTION) {
                try {
                    String medsName = newName.getText();
                    int medsStock = Integer.parseInt(newStock.getText());
                    int medPrice = Integer.parseInt(newPrice.getText());

                    pst = con.prepareStatement("INSERT INTO medicine(meds_name,stock,price) VALUES(?,?,?)");
                    pst.setString(1, medsName);
                    pst.setInt(2, medsStock);
                    pst.setInt(3, medPrice);
                    pst.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Added Successfully!");
                    table(selectCommand);

                    newName.setText("");
                    newPrice.setText("");
                    newStock.setText("");

                    addNewMedBtn.setEnabled(false);
                } catch (SQLException ex) {
                    Logger.getLogger(pharmacy.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {

            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter valid value!");
        }
    }//GEN-LAST:event_addNewMedBtnActionPerformed

    private void newStockKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_newStockKeyReleased
        if (newStock.getText().isEmpty() || newName.getText().isEmpty() || newPrice.getText().isEmpty()) {
            addNewMedBtn.setEnabled(false);
        } else {
            addNewMedBtn.setEnabled(true);
        }
    }//GEN-LAST:event_newStockKeyReleased

    private void newStockKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_newStockKeyTyped
        //addNewMedBtn.setEnabled(false);
    }//GEN-LAST:event_newStockKeyTyped

    private void addStockKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_addStockKeyTyped
        if (addStock.getText().isEmpty()) {
            updateBtn.setEnabled(false);
        }
    }//GEN-LAST:event_addStockKeyTyped

    private void updatePriceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_updatePriceKeyReleased
        if (updatePrice.getText().isEmpty() || currentName.getText().isEmpty()) {
            updateBtn.setEnabled(false);
        } else {
            updateBtn.setEnabled(true);
        }
    }//GEN-LAST:event_updatePriceKeyReleased

    private void updatePriceKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_updatePriceKeyTyped
        if (updatePrice.getText().isEmpty()) {
            updateBtn.setEnabled(false);
        }
    }//GEN-LAST:event_updatePriceKeyTyped

    private void dashboardOrderPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardOrderPanelMouseEntered
        dashboardOrderLbl.setFont(fontHover);
        dashboardOrderLbl.setForeground(Color.red);
    }//GEN-LAST:event_dashboardOrderPanelMouseEntered

    private void dashboardOrderPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardOrderPanelMouseExited
        dashboardOrderLbl.setFont(fontDefault);
        dashboardOrderLbl.setForeground(Color.black);
    }//GEN-LAST:event_dashboardOrderPanelMouseExited

    private void dashboardSalesLblMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardSalesLblMouseEntered

    }//GEN-LAST:event_dashboardSalesLblMouseEntered

    private void dashboardSalesLblMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardSalesLblMouseExited

    }//GEN-LAST:event_dashboardSalesLblMouseExited

    private void dashboardIncomeLblMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardIncomeLblMouseEntered

    }//GEN-LAST:event_dashboardIncomeLblMouseEntered

    private void dashboardIncomeLblMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardIncomeLblMouseExited

    }//GEN-LAST:event_dashboardIncomeLblMouseExited

    private void dashboardSalesPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardSalesPanelMouseEntered
        dashboardSalesLbl.setFont(new Font("Segoe UI Black", Font.BOLD, 30));
        dashboardSalesLbl.setForeground(Color.red);
    }//GEN-LAST:event_dashboardSalesPanelMouseEntered

    private void dashboardSalesPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardSalesPanelMouseExited
        dashboardSalesLbl.setFont(fontDefault);
        dashboardSalesLbl.setForeground(Color.black);
    }//GEN-LAST:event_dashboardSalesPanelMouseExited

    private void dashboardIncomePanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardIncomePanelMouseEntered
        dashboardIncomeLbl.setFont(new Font("Segoe UI Black", Font.BOLD, 30));
        dashboardIncomeLbl.setForeground(Color.red);
    }//GEN-LAST:event_dashboardIncomePanelMouseEntered

    private void dashboardIncomePanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardIncomePanelMouseExited
        dashboardIncomeLbl.setFont(fontDefault);
        dashboardIncomeLbl.setForeground(Color.black);
    }//GEN-LAST:event_dashboardIncomePanelMouseExited

    private void paymentKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_paymentKeyPressed

    }//GEN-LAST:event_paymentKeyPressed

    private void paymentKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_paymentKeyReleased
        try {
            float totalPrice = Float.parseFloat(totalTxt.getText());
            float pay = Float.parseFloat(payment.getText());
            float totalChange = pay - totalPrice;
            change.setText(Float.toString(totalChange));
            buy.setEnabled(true);
        } catch (Exception e) {
        }
    }//GEN-LAST:event_paymentKeyReleased

    private void paymentKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_paymentKeyTyped
        if (payment.getText().isEmpty()) {
            change.setText("");
            buy.setEnabled(false);
        }
    }//GEN-LAST:event_paymentKeyTyped

    private void changeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_changeActionPerformed

    private void showReceiptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showReceiptActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_showReceiptActionPerformed

    private void orderTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_orderTableMouseClicked
        int selectedRow = orderTable.getSelectedRow();
        int price = Integer.parseInt(orderTable.getValueAt(selectedRow, 2).toString());
        total -= price;
        String displayTotal = String.valueOf(total);
        totalTxt.setText(displayTotal);

        //to remove the clicked row
        DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
        model.removeRow(selectedRow);
    }//GEN-LAST:event_orderTableMouseClicked

    private void newNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_newNameKeyReleased
        if (newStock.getText().isEmpty() || newName.getText().isEmpty() || newPrice.getText().isEmpty()) {
            addNewMedBtn.setEnabled(false);
        } else {
            addNewMedBtn.setEnabled(true);
        }
    }//GEN-LAST:event_newNameKeyReleased

    private void newPriceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_newPriceKeyReleased
        if (newStock.getText().isEmpty() || newName.getText().isEmpty() || newPrice.getText().isEmpty()) {
            addNewMedBtn.setEnabled(false);
        } else {
            addNewMedBtn.setEnabled(true);
        }
    }//GEN-LAST:event_newPriceKeyReleased

    private void clearMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearMouseClicked
        newName.setText("");
        newPrice.setText("");
        newStock.setText("");
        addNewMedBtn.setEnabled(false);
    }//GEN-LAST:event_clearMouseClicked

    private void clearMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearMouseEntered
        clear.setForeground(Color.black);
    }//GEN-LAST:event_clearMouseEntered

    private void clearMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearMouseExited
        clear.setForeground(Color.white);
    }//GEN-LAST:event_clearMouseExited

    private void logoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseClicked
        int log_out = JOptionPane.showConfirmDialog(null, "Do you want to log out?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (log_out == JOptionPane.YES_OPTION) {
            dispose();
            new login().setVisible(true);
        } else {

        }
    }//GEN-LAST:event_logoutMouseClicked

    private void logoutMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseEntered
        logout.setForeground(Color.white);
    }//GEN-LAST:event_logoutMouseEntered

    private void logoutMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseExited
        logout.setForeground(Color.black);
    }//GEN-LAST:event_logoutMouseExited

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
            java.util.logging.Logger.getLogger(pharmacy.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(pharmacy.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(pharmacy.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(pharmacy.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new pharmacy().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addNewMedBtn;
    private javax.swing.JTextField addStock;
    private javax.swing.JPanel barChartPanel;
    private javax.swing.JButton buy;
    private com.github.lgooddatepicker.components.CalendarPanel calendarPanel1;
    private javax.swing.JTextField change;
    private javax.swing.JLabel clear;
    private javax.swing.JTextField currentName;
    private javax.swing.JTextField currentPrice;
    private javax.swing.JTextField currentStock;
    private javax.swing.JLabel dashboardIncomeLbl;
    private com.k33ptoo.components.KGradientPanel dashboardIncomePanel;
    private javax.swing.JLabel dashboardOrderLbl;
    private com.k33ptoo.components.KGradientPanel dashboardOrderPanel;
    private javax.swing.JLabel dashboardSalesLbl;
    private com.k33ptoo.components.KGradientPanel dashboardSalesPanel;
    private javax.swing.JLabel expLbl;
    private javax.swing.JPanel expenses;
    private javax.swing.JPanel expensesTab;
    private javax.swing.JPanel home;
    private javax.swing.JLabel homeLbl;
    private javax.swing.JPanel homeTab;
    private javax.swing.JLabel invLbl;
    private javax.swing.JPanel inventory;
    private javax.swing.JPanel inventoryTab;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private com.k33ptoo.components.KGradientPanel kGradientPanel2;
    private com.k33ptoo.components.KGradientPanel kGradientPanel5;
    private javax.swing.JLabel logout;
    private javax.swing.JTable medsTable;
    private javax.swing.JTable medsTable1;
    private javax.swing.JTextField newName;
    private javax.swing.JTextField newPrice;
    private javax.swing.JTextField newStock;
    private javax.swing.JTable orderTable;
    private javax.swing.JTextField payment;
    private javax.swing.JPanel pos;
    private javax.swing.JLabel posLbl;
    private javax.swing.JPanel posTab;
    private javax.swing.JPanel sales;
    private javax.swing.JLabel salesLbl;
    private javax.swing.JPanel salesTab;
    private javax.swing.JTextField search;
    private javax.swing.JTextField searchInventory;
    private javax.swing.JCheckBox showReceipt;
    private javax.swing.JTextField totalTxt;
    private javax.swing.JButton updateBtn;
    private javax.swing.JTextField updatePrice;
    // End of variables declaration//GEN-END:variables
class RoundedPanel extends JPanel {

        private Color backgroundColor;
        private int cornerRadius = 15;

        public RoundedPanel(LayoutManager layout, int radius) {
            super(layout);
            cornerRadius = radius;
        }

        public RoundedPanel(LayoutManager layout, int radius, Color bgColor) {
            super(layout);
            cornerRadius = radius;
            backgroundColor = bgColor;
        }

        public RoundedPanel(int radius) {
            super();
            cornerRadius = radius;

        }

        public RoundedPanel(int radius, Color bgColor) {
            super();
            cornerRadius = radius;
            backgroundColor = bgColor;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension arcs = new Dimension(cornerRadius, cornerRadius);
            int width = getWidth();
            int height = getHeight();
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            //Draws the rounded panel with borders.
            if (backgroundColor != null) {
                graphics.setColor(backgroundColor);
            } else {
                graphics.setColor(getBackground());
            }
            graphics.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height); //paint background
            graphics.setColor(getForeground());
//            graphics.drawRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height); //paint border
//             
        }
    }

}
