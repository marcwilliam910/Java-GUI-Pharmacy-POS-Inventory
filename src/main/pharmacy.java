package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class pharmacy extends javax.swing.JFrame {

    DefaultTableModel model1, model2;
    Color defaultColor, clickedColor;
    Font fontHover, fontDefault;

    public pharmacy() {
        initComponents();
        connect();
        table(selectCommand);
        outOfStock();
        tableDesign();
        dashboardOptionDesign();
        monthlyChart();
        buttonEnabled();
        dailyChart();
    }

    //CONNECTOR SA XAMPP MYSQL    
    private final String url = "jdbc:mysql://localhost:3306/pharma";
    private final String username = "root";
    private final String password = "";
    private final String selectCommand = "SELECT * FROM medicine";

    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    //for receipt
    protected double total = 0;
    protected int qty;

    //para sa discount sa generics medicine
    double genericsPrice = 0;
    double discountAmount = 0;

    //para sa dashboard count
    private int orderCount = 0;
    private int saleCount = 0;

    private void connect() {
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
            pst = con.prepareStatement(command);
            rs = pst.executeQuery();

            //for pos tab
            DefaultTableModel df1 = (DefaultTableModel) medsTable.getModel();
            //for inventory tab
            DefaultTableModel df2 = (DefaultTableModel) medsTable1.getModel();

            df1.setRowCount(0);
            df2.setRowCount(0);
            while (rs.next()) {
                Object[] data = {
                    rs.getString("meds_name"),
                    rs.getString("price"),
                    rs.getString("stock"),
                    rs.getString("formulation")
                };
                df1.addRow(data);
                df2.addRow(data);
            }
        } catch (SQLException | NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "No Database Found!");
        }

    }

    //design lang to ng mga table
    private void tableDesign() {
        //for table sa pos
        JTableHeader header = medsTable.getTableHeader();
        header.setForeground(Color.blue);
        Font font = new Font("Cambria", Font.BOLD, 18);
        header.setFont(font);

        medsTable.setShowGrid(true);
        medsTable.setGridColor(Color.BLACK);
        Font insideFont = new Font("Calibri", Font.PLAIN, 17);
        medsTable.setFont(insideFont);

        TableColumnModel tableColumn = medsTable.getColumnModel();
        TableColumn name = tableColumn.getColumn(0);
        TableColumn formulation = tableColumn.getColumn(3);

        name.setPreferredWidth(150);
        formulation.setPreferredWidth(120);

        //for table sa inventory
        JTableHeader header1 = medsTable1.getTableHeader();
        header1.setFont(font);
        header1.setForeground(Color.blue);
        medsTable1.setShowGrid(true);
        medsTable1.setGridColor(Color.BLACK);
        medsTable1.setFont(insideFont);

        TableColumnModel tableColumn1 = medsTable1.getColumnModel();
        TableColumn name1 = tableColumn1.getColumn(0);
        TableColumn formulation1 = tableColumn1.getColumn(3);
        name1.setPreferredWidth(150);
        formulation1.setPreferredWidth(120);
        //for table sa order
        JTableHeader header2 = orderTable.getTableHeader();

        header2.setFont(font);
        header2.setForeground(Color.red);

        orderTable.setFont(insideFont);

        TableColumnModel tableColumn2 = orderTable.getColumnModel();
        TableColumn column2 = tableColumn2.getColumn(0);
        column2.setPreferredWidth(140);

        //for table sa purchase history
        JTableHeader header3 = purcharseHistoryTable.getTableHeader();
        header3.setForeground(Color.blue);
        header3.setFont(font);

        TableColumnModel tableColumn3 = purcharseHistoryTable.getColumnModel();
        TableColumn column3 = tableColumn3.getColumn(3);
        column3.setPreferredWidth(100);

        //for out of stock table
        JTableHeader header4 = outOfStockTable.getTableHeader();
        header4.setForeground(Color.red);
        header4.setFont(font);
        TableColumnModel tableColumn4 = outOfStockTable.getColumnModel();
        TableColumn formulation2 = tableColumn4.getColumn(2);
        formulation2.setPreferredWidth(130);

        TableColumnModel tableColumn5 = salesTable.getColumnModel();
        TableColumn date = tableColumn5.getColumn(0);
        TableColumn day = tableColumn5.getColumn(1);
        TableColumn sale = tableColumn5.getColumn(2);
        date.setPreferredWidth(100);
        day.setPreferredWidth(80);
        sale.setPreferredWidth(60);
    }

    private void search(String med) {
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

    //for monthly chart
    private void monthlyChart() {
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
        chart.setBackgroundPaint(new Color(240, 240, 240));

        CategoryPlot plot = chart.getCategoryPlot();

        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Font horizontalFont = new Font("Arial", Font.PLAIN, 14);
        Font verticalFont = new Font("Arial", Font.PLAIN, 14);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelFont(labelFont);
        domainAxis.setTickLabelFont(verticalFont);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLabelFont(labelFont);
        rangeAxis.setTickLabelFont(horizontalFont);

        plot.setRangeGridlinePaint(Color.black);
        ChartPanel chartPanel = new ChartPanel(chart);

        barChartPanelMonthly.removeAll();
        barChartPanelMonthly.add(chartPanel, BorderLayout.CENTER);
        barChartPanelMonthly.validate();

    }

    //for daily chart
    boolean firstClicked = true;

    private void todaySaleToDB() {
        try {
            //to get the todays date: ex June 17, 2023
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            LocalDate today = LocalDate.now();
            String formattedDate = today.format(dateFormatter);

            //for today day: ex Friday
            DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE");
            String formattedDay = today.format(dayFormatter);

            //pang where condition, todays date: ex 2023-06-20
            LocalDateTime now = LocalDateTime.now();
            String todayDateTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            int todaySales = 0;

            pst = con.prepareStatement("SELECT SUM(total_price) FROM sold_medicine WHERE DATE(date_sold) = ?");
            pst.setString(1, todayDateTime);
            rs = pst.executeQuery();

            if (rs.next()) {
                todaySales = rs.getInt(1);
            }

            String dateCondition = null;
            pst = con.prepareStatement("SELECT date FROM daily_sales ORDER BY date DESC LIMIT 1");
            rs = pst.executeQuery();
            if (rs.next()) {
                dateCondition = rs.getString(1);
            }

            if (firstClicked && !formattedDate.equals(dateCondition)) {
                pst = con.prepareStatement("INSERT INTO daily_sales(date, day, total_sale) VALUES(?,?,?)");
                pst.setString(1, formattedDate);
                pst.setString(2, formattedDay);
                pst.setInt(3, todaySales);
                pst.executeUpdate();
                firstClicked = false;
            } else {
                pst = con.prepareStatement("UPDATE daily_sales SET total_sale = ? WHERE date = ?");
                pst.setInt(1, todaySales);
                pst.setString(2, formattedDate);
                pst.executeUpdate();
            }

        } catch (SQLException | NullPointerException ex) {
            String errorMessage = ex.getMessage();
            System.out.println("Error occurred todaySalesToDB: " + errorMessage);
        }

    }

    private void dailyChart() {
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            DefaultTableModel model = (DefaultTableModel) salesTable.getModel();
            String date;
            String day;
            double totalSale;

            //subquery para last 7 days lang ang kukunin nya
            pst = con.prepareStatement("SELECT date, day, total_sale FROM daily_sales WHERE ID > (SELECT MAX(ID) - 7 FROM daily_sales)");
            rs = pst.executeQuery();

            while (rs.next()) {
                date = rs.getString("date");
                day = rs.getString("day");
                totalSale = rs.getDouble("total_sale");

                dataset.addValue(totalSale, "Income", day);
                model.addRow(new Object[]{date, day, totalSale});

            }

            JFreeChart chart = ChartFactory.createLineChart("", "Day", "Income", dataset, PlotOrientation.VERTICAL, true, true, false);
            chart.setBackgroundPaint(new Color(240, 240, 240));

            CategoryPlot plot = chart.getCategoryPlot();

            Font labelFont = new Font("Arial", Font.BOLD, 16);
            Font tickFont = new Font("Arial", Font.PLAIN, 14);

            plot.getDomainAxis().setLabelFont(labelFont);
            plot.getDomainAxis().setTickLabelFont(tickFont);

            plot.getRangeAxis().setLabelFont(labelFont);
            plot.getRangeAxis().setTickLabelFont(tickFont);

            plot.setRangeGridlinePaint(Color.BLACK);

            ChartPanel chartPanel = new ChartPanel(chart);
            barChartPanelDaily.setLayout(new BorderLayout());
            barChartPanelDaily.add(chartPanel, BorderLayout.CENTER);

        } catch (SQLException | NullPointerException ex) {
            String errorMessage = ex.getMessage();
            System.out.println("Error occurred dailyChart: " + errorMessage);
        }
    }

    private void showInOrderTable() {
        try {
            qty = Integer.parseInt(JOptionPane.showInputDialog("Quantity"));

            discount.setSelected(false);
            int selectrow = medsTable.getSelectedRow();
            String itemName = medsTable.getValueAt(selectrow, 0).toString();
            int price = Integer.parseInt(medsTable.getValueAt(selectrow, 1).toString());
            int quantity = qty;
            String formulation = medsTable.getValueAt(selectrow, 3).toString();
            price *= quantity;

            total += price;
            if (formulation.equals("Generic")) {
                genericsPrice += price;
            }

            DefaultTableModel df = (DefaultTableModel) orderTable.getModel();
            //store sa array of object
            Object[] order = {itemName, quantity, price, formulation};
            df.addRow(order);

            totalTxt.setText(String.valueOf(total));

        } catch (NumberFormatException | NullPointerException e) {
            JOptionPane.showMessageDialog(null, "Please Enter Valid Amount!");
        }

    }

    //cclear o ibabalik lang nito sa dati lahat pagka buy ng item
    private void clearWhenBuy() {
        discountAmount = 0;
        genericsPrice = 0;
        totalTxt.setText("");
        payment.setText("");
        change.setText("");
        search.setText("");
        discount.setSelected(false);
        buy.setEnabled(false);
        JOptionPane.showMessageDialog(null, "Buying Done!");
    }

    private void buy() {
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
        //since na buy na, balik na sa 0 
        total = 0;

        table(selectCommand);
        outOfStock();
    }

    private void soldMedsToDB() {
        try {
            //LocalDateTime currentDateTime = LocalDateTime.of(2023, 6, 15, 9, 30 , 0);
            //June 15, 2023 9:30AM  hh:mm:ss a - AM/PM indicator
            LocalDateTime currentDateTime = LocalDateTime.now();
            String formattedDateTime = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            //kinuha yung last user id sa login_hisoty sa db
            int userID = 1;
            pst = con.prepareStatement("SELECT userID FROM login_history");
            rs = pst.executeQuery();
            if (rs.last()) {
                userID = rs.getInt(1);
            }
            //gumamit ng StringBuilder para makuha lahat ng item at i-concatenate silang lahat
            StringBuilder itemsBuilder = new StringBuilder();
            for (int i = 0; i < orderTable.getRowCount(); i++) {
                itemsBuilder.append((String) orderTable.getValueAt(i, 0));
                if (i != orderTable.getRowCount() - 1) {
                    itemsBuilder.append(", ");
                }
            }
            String items = itemsBuilder.toString();

            double price = Double.parseDouble(totalTxt.getText());
            int roundedPrice = (int) Math.round(price);

            pst = con.prepareStatement("INSERT INTO sold_medicine(items, total_price, date_sold, login_id) VALUES(?,?,?,?)");
            pst.setString(1, items);
            pst.setInt(2, roundedPrice);
            pst.setString(3, formattedDateTime);
            pst.setInt(4, userID);
            pst.executeUpdate();

        } catch (SQLException | NullPointerException ex) {
            Logger.getLogger(pharmacy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //need ibahin design kasi hindi naaapply ang discount
    private void purchaseHistory() {
        try {
            Date dd = new Date();
            SimpleDateFormat timef = new SimpleDateFormat("hh:mm a");
            DefaultTableModel model = (DefaultTableModel) purcharseHistoryTable.getModel();
            String user = null;
            String items = null;
            int price = 0;
            //kinuha yung last username sa login_hisoty sa db
            pst = con.prepareStatement("SELECT username FROM login_history");
            rs = pst.executeQuery();
            if (rs.last()) {
                user = rs.getString(1);
            }

            //para makuha yung order na item
            pst = con.prepareStatement("SELECT items, total_price FROM sold_medicine");
            rs = pst.executeQuery();
            if (rs.last()) {
                items = rs.getString(1);
                price = rs.getInt(2);
            }
            
            String time = timef.format(dd);

            //store sa array of objects
            Object[] purchase = {items, price, time, user};
            model.addRow(purchase);

        } catch (SQLException | NullPointerException ex) {
            Logger.getLogger(pharmacy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void outOfStock() {
        try {
            pst = con.prepareStatement("SELECT meds_name, stock, formulation FROM medicine");
            rs = pst.executeQuery();

            DefaultTableModel df = (DefaultTableModel) outOfStockTable.getModel();
            df.setRowCount(0);

            while (rs.next()) {
                int stock = Integer.parseInt(rs.getString("stock"));
                if (stock < 10) {
                    Object[] rowData = {
                        rs.getString("meds_name"),
                        rs.getString("stock"),
                        rs.getString("formulation")
                    };
                    df.addRow(rowData);
                }
            }
        } catch (SQLException | NullPointerException ex) {
            System.out.println("Error out of stock");
        }
    }

    private void calculateDiscount() {
        try {
            if (discount.isSelected()) {
                int pay = Integer.parseInt(this.payment.getText());
                discountAmount = genericsPrice * 0.20;
                double totalWithDiscount = Double.parseDouble(totalTxt.getText()) - discountAmount;
                int roundedTotal = (int) Math.round(totalWithDiscount);
                totalTxt.setText(String.valueOf(roundedTotal));

                //para makuha ang value ng payment at isubract sa total at idisplay sa change
                int roundedChange = (int) Math.round(pay - totalWithDiscount);
                change.setText(String.valueOf(roundedChange));
            } else {
                totalTxt.setText(String.valueOf(total));

                //para lumabas ang sukli real time 
                int pay = Integer.parseInt(this.payment.getText());

                int roundedChange = (int) Math.round(pay - total);
                change.setText(String.valueOf(roundedChange));
            }
        } catch (NumberFormatException ex) {

        }
    }

    private void buttonEnabled() {
        buy.setEnabled(false);
        updateBtn.setEnabled(false);
        addNewMedBtn.setEnabled(false);
    }

    private void dashboardOptionDesign() {
        fontHover = new Font("Segoe UI Black", Font.BOLD, 35);
        fontDefault = new Font("Segoe UI Black", Font.BOLD, 24);
        defaultColor = new Color(0, 0, 0);
        clickedColor = new Color(82, 23, 179);

        home.setOpaque(true);
        home.setBackground(clickedColor);
        pos.setBackground(defaultColor);
        sales.setBackground(defaultColor);
        expenses.setBackground(defaultColor);
        inventory.setBackground(defaultColor);
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
        supplier = new javax.swing.JPanel();
        supplierLbl = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        logout = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        homeTab = new javax.swing.JPanel();
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
        calendarPanel1 = new com.github.lgooddatepicker.components.CalendarPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        purcharseHistoryTable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        outOfStockTable = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        posTab = new javax.swing.JPanel();
        buy = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        medsTable = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        search = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        orderTable = new javax.swing.JTable();
        totalTxt = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        payment = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        change = new javax.swing.JTextField();
        discount = new javax.swing.JCheckBox();
        salesTab = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        dailyChart = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        prevDaily = new javax.swing.JLabel();
        nextDaily = new javax.swing.JLabel();
        barChartPanelDaily = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        salesTable = new javax.swing.JTable();
        weeklyChart = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        nextWeekly = new javax.swing.JLabel();
        prevWeekly = new javax.swing.JLabel();
        barChartPanelWeekly = new javax.swing.JPanel();
        monthlyChart = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        prevMonth = new javax.swing.JLabel();
        nextMonth = new javax.swing.JLabel();
        barChartPanelMonthly = new javax.swing.JPanel();
        expensesTab = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        inventoryTab = new javax.swing.JPanel();
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
        formulationCombo = new javax.swing.JComboBox<>();
        jLabel38 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        medsTable1 = new javax.swing.JTable();
        searchInventory = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        supplierTab = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();

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
            .addComponent(posLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        supplier.setOpaque(false);
        supplier.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                supplierMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                supplierMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                supplierMouseExited(evt);
            }
        });

        supplierLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        supplierLbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/supplier.png"))); // NOI18N
        supplierLbl.setText("  Supplier");
        supplierLbl.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        supplierLbl.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout supplierLayout = new javax.swing.GroupLayout(supplier);
        supplier.setLayout(supplierLayout);
        supplierLayout.setHorizontalGroup(
            supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(supplierLayout.createSequentialGroup()
                .addComponent(supplierLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                .addContainerGap())
        );
        supplierLayout.setVerticalGroup(
            supplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(supplierLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
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
            .addGroup(kGradientPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(kGradientPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(logout)
                    .addGroup(kGradientPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kGradientPanel2Layout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(kGradientPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(kGradientPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(inventory, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(expenses, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(sales, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(pos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(home, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(supplier, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(11, 11, 11))
                        .addComponent(jLabel5)))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        kGradientPanel2Layout.setVerticalGroup(
            kGradientPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel2Layout.createSequentialGroup()
                .addGap(22, 22, 22)
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
                .addGap(10, 10, 10)
                .addComponent(supplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addComponent(logout)
                .addContainerGap())
        );

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dashboardOrderPanel.setkEndColor(new java.awt.Color(73, 254, 73));
        dashboardOrderPanel.setkGradientFocus(300);
        dashboardOrderPanel.setkStartColor(new java.awt.Color(95, 95, 249));
        dashboardOrderPanel.setPreferredSize(new java.awt.Dimension(230, 100));
        dashboardOrderPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dashboardOrderPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dashboardOrderPanelMouseExited(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Order's Count");
        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N

        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/dashboard_order.png"))); // NOI18N

        dashboardOrderLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dashboardOrderLbl.setText("0");
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
                .addContainerGap(22, Short.MAX_VALUE))
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
        dashboardSalesPanel.setPreferredSize(new java.awt.Dimension(230, 100));
        dashboardSalesPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dashboardSalesPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dashboardSalesPanelMouseExited(evt);
            }
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Sales Count");
        jLabel3.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N

        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/dashboard_sale.png"))); // NOI18N

        dashboardSalesLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dashboardSalesLbl.setText("0");
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
                .addContainerGap(13, Short.MAX_VALUE)
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
        dashboardIncomePanel.setPreferredSize(new java.awt.Dimension(230, 100));
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
        dashboardIncomeLbl.setText("0");
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

        calendarPanel1.setBackground(new java.awt.Color(255, 255, 255));
        calendarPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        calendarPanel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        purcharseHistoryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item", "Price", "Time Sold", "Seller"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        purcharseHistoryTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(purcharseHistoryTable);
        if (purcharseHistoryTable.getColumnModel().getColumnCount() > 0) {
            purcharseHistoryTable.getColumnModel().getColumn(0).setResizable(false);
            purcharseHistoryTable.getColumnModel().getColumn(1).setResizable(false);
            purcharseHistoryTable.getColumnModel().getColumn(2).setResizable(false);
            purcharseHistoryTable.getColumnModel().getColumn(3).setResizable(false);
        }

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Purchase History");
        jLabel2.setFont(new java.awt.Font("Segoe UI", 3, 30)); // NOI18N

        outOfStockTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Stock", "Formulation"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(outOfStockTable);
        if (outOfStockTable.getColumnModel().getColumnCount() > 0) {
            outOfStockTable.getColumnModel().getColumn(0).setResizable(false);
            outOfStockTable.getColumnModel().getColumn(1).setResizable(false);
            outOfStockTable.getColumnModel().getColumn(2).setResizable(false);
        }

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Out of Stock");
        jLabel7.setFont(new java.awt.Font("Tahoma", 2, 18)); // NOI18N

        javax.swing.GroupLayout homeTabLayout = new javax.swing.GroupLayout(homeTab);
        homeTab.setLayout(homeTabLayout);
        homeTabLayout.setHorizontalGroup(
            homeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homeTabLayout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addGroup(homeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 516, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(homeTabLayout.createSequentialGroup()
                        .addGap(142, 142, 142)
                        .addComponent(jLabel2)))
                .addGap(10, 10, 10)
                .addGroup(homeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(calendarPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(157, 157, 157))
            .addGroup(homeTabLayout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addComponent(dashboardOrderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(dashboardSalesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(dashboardIncomePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        homeTabLayout.setVerticalGroup(
            homeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homeTabLayout.createSequentialGroup()
                .addContainerGap(42, Short.MAX_VALUE)
                .addGroup(homeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dashboardOrderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dashboardSalesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dashboardIncomePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(homeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(homeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(homeTabLayout.createSequentialGroup()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(calendarPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)))
                .addContainerGap(83, Short.MAX_VALUE))
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
                "Medicine Name", "Price", "Stock", "Formulation"
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
            medsTable.getColumnModel().getColumn(0).setResizable(false);
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

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Order");
        jLabel12.setBackground(new java.awt.Color(0, 0, 0));
        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        orderTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Medicine Name", "Quantity", "Price", "Formulation"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
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
            orderTable.getColumnModel().getColumn(3).setResizable(false);
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

        discount.setText("20% Discount");
        discount.setBackground(new java.awt.Color(255, 255, 255));
        discount.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        discount.setForeground(new java.awt.Color(0, 0, 255));
        discount.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                discountItemStateChanged(evt);
            }
        });
        discount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discountActionPerformed(evt);
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
                                        .addGap(53, 53, 53)
                                        .addComponent(discount))))))
                    .addGroup(posTabLayout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(buy, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(132, 132, 132))
        );
        posTabLayout.setVerticalGroup(
            posTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(posTabLayout.createSequentialGroup()
                .addGroup(posTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(posTabLayout.createSequentialGroup()
                        .addGap(47, 47, 47)
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
                            .addComponent(discount))
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

        jTabbedPane2.setOpaque(true);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText(" Daily Sales Chart");
        jLabel35.setFont(new java.awt.Font("Segoe UI Semibold", 3, 36)); // NOI18N

        prevDaily.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        prevDaily.setText("←");
        prevDaily.setFont(new java.awt.Font("Segoe UI Black", 1, 60)); // NOI18N
        prevDaily.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                prevDailyMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                prevDailyMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                prevDailyMouseExited(evt);
            }
        });

        nextDaily.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nextDaily.setText("→");
        nextDaily.setFont(new java.awt.Font("Segoe UI Black", 1, 60)); // NOI18N
        nextDaily.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nextDailyMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                nextDailyMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                nextDailyMouseExited(evt);
            }
        });

        barChartPanelDaily.setLayout(new java.awt.BorderLayout());

        salesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "Day", "Sales"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        salesTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(salesTable);
        if (salesTable.getColumnModel().getColumnCount() > 0) {
            salesTable.getColumnModel().getColumn(0).setResizable(false);
            salesTable.getColumnModel().getColumn(1).setResizable(false);
            salesTable.getColumnModel().getColumn(2).setResizable(false);
        }

        javax.swing.GroupLayout dailyChartLayout = new javax.swing.GroupLayout(dailyChart);
        dailyChart.setLayout(dailyChartLayout);
        dailyChartLayout.setHorizontalGroup(
            dailyChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dailyChartLayout.createSequentialGroup()
                .addContainerGap(172, Short.MAX_VALUE)
                .addComponent(prevDaily)
                .addGap(50, 50, 50)
                .addComponent(jLabel35)
                .addGap(62, 62, 62)
                .addComponent(nextDaily)
                .addGap(162, 162, 162))
            .addGroup(dailyChartLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(barChartPanelDaily, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        dailyChartLayout.setVerticalGroup(
            dailyChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dailyChartLayout.createSequentialGroup()
                .addGroup(dailyChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dailyChartLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(dailyChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(prevDaily, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(dailyChartLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(nextDaily, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(dailyChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dailyChartLayout.createSequentialGroup()
                        .addComponent(barChartPanelDaily, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(26, 26, 26))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dailyChartLayout.createSequentialGroup()
                        .addGap(0, 79, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(125, 125, 125))))
        );

        jTabbedPane2.addTab("Daily ", new javax.swing.ImageIcon(getClass().getResource("/icons/daily.png")), dailyChart); // NOI18N

        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setText(" Weekly Income Chart");
        jLabel37.setFont(new java.awt.Font("Segoe UI Semibold", 3, 36)); // NOI18N

        nextWeekly.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nextWeekly.setText("→");
        nextWeekly.setFont(new java.awt.Font("Segoe UI Black", 1, 60)); // NOI18N
        nextWeekly.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nextWeeklyMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                nextWeeklyMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                nextWeeklyMouseExited(evt);
            }
        });

        prevWeekly.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        prevWeekly.setText("←");
        prevWeekly.setFont(new java.awt.Font("Segoe UI Black", 1, 60)); // NOI18N
        prevWeekly.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                prevWeeklyMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                prevWeeklyMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                prevWeeklyMouseExited(evt);
            }
        });

        barChartPanelWeekly.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout weeklyChartLayout = new javax.swing.GroupLayout(weeklyChart);
        weeklyChart.setLayout(weeklyChartLayout);
        weeklyChartLayout.setHorizontalGroup(
            weeklyChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(weeklyChartLayout.createSequentialGroup()
                .addGroup(weeklyChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(weeklyChartLayout.createSequentialGroup()
                        .addGap(156, 156, 156)
                        .addComponent(prevWeekly)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel37)
                        .addGap(27, 27, 27)
                        .addComponent(nextWeekly))
                    .addGroup(weeklyChartLayout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(barChartPanelWeekly, javax.swing.GroupLayout.PREFERRED_SIZE, 784, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(48, Short.MAX_VALUE))
        );
        weeklyChartLayout.setVerticalGroup(
            weeklyChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(weeklyChartLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(weeklyChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, weeklyChartLayout.createSequentialGroup()
                        .addComponent(nextWeekly, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13))
                    .addGroup(weeklyChartLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(prevWeekly, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addComponent(barChartPanelWeekly, javax.swing.GroupLayout.PREFERRED_SIZE, 473, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        jTabbedPane2.addTab("Weekly", new javax.swing.ImageIcon(getClass().getResource("/icons/weekly.png")), weeklyChart); // NOI18N

        monthlyChart.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText(" Monthly Sales Chart");
        jLabel32.setFont(new java.awt.Font("Segoe UI Semibold", 3, 36)); // NOI18N

        prevMonth.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        prevMonth.setText("←");
        prevMonth.setFont(new java.awt.Font("Segoe UI Black", 1, 60)); // NOI18N
        prevMonth.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                prevMonthMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                prevMonthMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                prevMonthMouseExited(evt);
            }
        });

        nextMonth.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nextMonth.setText("→");
        nextMonth.setFont(new java.awt.Font("Segoe UI Black", 1, 60)); // NOI18N
        nextMonth.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nextMonthMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                nextMonthMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                nextMonthMouseExited(evt);
            }
        });

        barChartPanelMonthly.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout monthlyChartLayout = new javax.swing.GroupLayout(monthlyChart);
        monthlyChart.setLayout(monthlyChartLayout);
        monthlyChartLayout.setHorizontalGroup(
            monthlyChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(monthlyChartLayout.createSequentialGroup()
                .addGroup(monthlyChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(monthlyChartLayout.createSequentialGroup()
                        .addGap(154, 154, 154)
                        .addComponent(prevMonth)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel32)
                        .addGap(18, 18, 18)
                        .addComponent(nextMonth))
                    .addGroup(monthlyChartLayout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(barChartPanelMonthly, javax.swing.GroupLayout.PREFERRED_SIZE, 784, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(49, Short.MAX_VALUE))
        );
        monthlyChartLayout.setVerticalGroup(
            monthlyChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(monthlyChartLayout.createSequentialGroup()
                .addGroup(monthlyChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(monthlyChartLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(monthlyChartLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(nextMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(monthlyChartLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(prevMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addComponent(barChartPanelMonthly, javax.swing.GroupLayout.PREFERRED_SIZE, 473, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );

        jTabbedPane2.addTab("Monthly", new javax.swing.ImageIcon(getClass().getResource("/icons/monthly.png")), monthlyChart); // NOI18N

        javax.swing.GroupLayout salesTabLayout = new javax.swing.GroupLayout(salesTab);
        salesTab.setLayout(salesTabLayout);
        salesTabLayout.setHorizontalGroup(
            salesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(salesTabLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 884, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(111, Short.MAX_VALUE))
        );
        salesTabLayout.setVerticalGroup(
            salesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(salesTabLayout.createSequentialGroup()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 641, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 61, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab3", salesTab);

        jLabel4.setText("4");
        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N

        javax.swing.GroupLayout expensesTabLayout = new javax.swing.GroupLayout(expensesTab);
        expensesTab.setLayout(expensesTabLayout);
        expensesTabLayout.setHorizontalGroup(
            expensesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(expensesTabLayout.createSequentialGroup()
                .addContainerGap(553, Short.MAX_VALUE)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(323, 323, 323))
        );
        expensesTabLayout.setVerticalGroup(
            expensesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(expensesTabLayout.createSequentialGroup()
                .addGap(196, 196, 196)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(413, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab4", expensesTab);

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

        formulationCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Branded", "Generic" }));
        formulationCombo.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel38.setText("Formulation :");
        jLabel38.setFont(new java.awt.Font("Times New Roman", 1, 20)); // NOI18N

        javax.swing.GroupLayout kGradientPanel5Layout = new javax.swing.GroupLayout(kGradientPanel5);
        kGradientPanel5.setLayout(kGradientPanel5Layout);
        kGradientPanel5Layout.setHorizontalGroup(
            kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel5Layout.createSequentialGroup()
                .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(kGradientPanel5Layout.createSequentialGroup()
                        .addGap(0, 23, Short.MAX_VALUE)
                        .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, kGradientPanel5Layout.createSequentialGroup()
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
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kGradientPanel5Layout.createSequentialGroup()
                                            .addComponent(jLabel22)
                                            .addGap(54, 54, 54)
                                            .addComponent(addStock, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(kGradientPanel5Layout.createSequentialGroup()
                                            .addComponent(jLabel23)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(updatePrice, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, kGradientPanel5Layout.createSequentialGroup()
                                .addGap(78, 78, 78)
                                .addComponent(jLabel24)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, kGradientPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(clear, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addNewMedBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kGradientPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(updateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(100, 100, 100))
            .addGroup(kGradientPanel5Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(kGradientPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel38)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(formulationCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(kGradientPanel5Layout.createSequentialGroup()
                            .addComponent(jLabel28)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(newPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel27)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(newStock, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(kGradientPanel5Layout.createSequentialGroup()
                            .addComponent(jLabel26)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel25)
                                .addComponent(newName)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel25)
                .addGap(18, 18, 18)
                .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(newName, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28)
                    .addComponent(newStock, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(formulationCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38))
                .addGap(17, 17, 17)
                .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(addNewMedBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clear, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(201, 201, 201))
        );

        medsTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Medicine Name", "Price", "Stock", "Formulation"
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
                .addGap(50, 50, 50)
                .addGroup(inventoryTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(inventoryTabLayout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addGap(18, 18, 18)
                        .addComponent(searchInventory, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 481, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(kGradientPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(130, Short.MAX_VALUE))
        );
        inventoryTabLayout.setVerticalGroup(
            inventoryTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inventoryTabLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(inventoryTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(inventoryTabLayout.createSequentialGroup()
                        .addGroup(inventoryTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(searchInventory, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 518, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(96, 96, 96))
                    .addGroup(inventoryTabLayout.createSequentialGroup()
                        .addComponent(kGradientPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 611, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jTabbedPane1.addTab("tab5", inventoryTab);

        jLabel9.setText("6");
        jLabel9.setFont(new java.awt.Font("Tahoma", 3, 36)); // NOI18N

        javax.swing.GroupLayout supplierTabLayout = new javax.swing.GroupLayout(supplierTab);
        supplierTab.setLayout(supplierTabLayout);
        supplierTabLayout.setHorizontalGroup(
            supplierTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(supplierTabLayout.createSequentialGroup()
                .addGap(439, 439, 439)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(495, Short.MAX_VALUE))
        );
        supplierTabLayout.setVerticalGroup(
            supplierTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(supplierTabLayout.createSequentialGroup()
                .addGap(195, 195, 195)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(451, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab6", supplierTab);

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
        supplier.setOpaque(false);

        home.setBackground(clickedColor);
        pos.setBackground(defaultColor);
        sales.setBackground(defaultColor);
        expenses.setBackground(defaultColor);
        inventory.setBackground(defaultColor);
        supplier.setBackground(defaultColor);

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
        supplier.setOpaque(false);

        home.setBackground(defaultColor);
        pos.setBackground(clickedColor);
        sales.setBackground(defaultColor);
        expenses.setBackground(defaultColor);
        inventory.setBackground(defaultColor);
        supplier.setBackground(defaultColor);

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
        supplier.setOpaque(false);

        home.setBackground(defaultColor);
        pos.setBackground(defaultColor);
        sales.setBackground(clickedColor);
        expenses.setBackground(defaultColor);
        inventory.setBackground(defaultColor);
        supplier.setBackground(defaultColor);

        jTabbedPane1.setSelectedIndex(2);

        //need iset sa 0 ang table para ang madisplay lang ay ang laman ng table sa database, pumapatong lang kasi pag hindi
        DefaultTableModel model = (DefaultTableModel) salesTable.getModel();
        model.setRowCount(0);
        todaySaleToDB();
        dailyChart();
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
        supplier.setOpaque(false);

        home.setBackground(defaultColor);
        pos.setBackground(defaultColor);
        sales.setBackground(defaultColor);
        expenses.setBackground(clickedColor);
        inventory.setBackground(defaultColor);
        supplier.setBackground(defaultColor);

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
        supplier.setOpaque(false);

        home.setBackground(defaultColor);
        pos.setBackground(defaultColor);
        sales.setBackground(defaultColor);
        expenses.setBackground(defaultColor);
        inventory.setBackground(clickedColor);
        supplier.setBackground(defaultColor);

        jTabbedPane1.setSelectedIndex(4);
    }//GEN-LAST:event_inventoryMouseClicked

    private void inventoryMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inventoryMouseEntered

        invLbl.setForeground(Color.black);
    }//GEN-LAST:event_inventoryMouseEntered

    private void inventoryMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inventoryMouseExited

        invLbl.setForeground(Color.white);
    }//GEN-LAST:event_inventoryMouseExited

    private void buyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buyActionPerformed
        try {
            float pay = Float.parseFloat(payment.getText());
            float tot = Float.parseFloat(totalTxt.getText());
            float ch = Float.parseFloat(change.getText());

            if (pay < tot) {
                JOptionPane.showMessageDialog(this, "Not Enough Payment");
            } else {
                String[] options = {"Yes, let me see", "No, I don't", "Cancel Order"};
                int choice = JOptionPane.showOptionDialog(null, "Do you want to see the receipt?", "Question", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                switch (choice) {
                    case JOptionPane.YES_OPTION:
                        orderCount++;
                        saleCount += tot;
                        dashboardOrderLbl.setText(String.valueOf(orderCount));
                        dashboardSalesLbl.setText(String.valueOf(saleCount));

                        buy();
                        showReceipt r = new showReceipt();
                        soldMedsToDB();
                        purchaseHistory();
                        clearWhenBuy();
                        DefaultTableModel order = (DefaultTableModel) orderTable.getModel();
                        r.setVisible(true);
                        r.showToReceipt(order, pay, ch, discountAmount);
                        order.setRowCount(0);
                        break;

                    case JOptionPane.NO_OPTION:
                        orderCount++;
                        saleCount += tot;
                        dashboardOrderLbl.setText(String.valueOf(orderCount));
                        dashboardSalesLbl.setText(String.valueOf(saleCount));

                        buy();
                        soldMedsToDB();
                        purchaseHistory();
                        DefaultTableModel order1 = (DefaultTableModel) orderTable.getModel();
                        order1.setRowCount(0);
                        clearWhenBuy();
                        break;

                    default:

                }
            }
        } catch (NumberFormatException e) {
            //JOptionPane.showMessageDialog(this, "Enter Valid Amount");
            System.out.println("buy action error - NumberFormatException: " + e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("buy action error - NullPointerException: " + e.getMessage());
            e.printStackTrace();
        }


    }//GEN-LAST:event_buyActionPerformed

    private void medsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_medsTableMouseClicked
        int selectrow = medsTable.getSelectedRow();
        int stock = Integer.parseInt(medsTable.getValueAt(selectrow, 2).toString());

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

    private void medsTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_medsTable1MouseClicked
        String[] options = {"Add Stock", "Remove to list"};
        int choice = JOptionPane.showOptionDialog(null, "What do you want to do?", "Question", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == JOptionPane.YES_OPTION) {
            int selectrow = medsTable1.getSelectedRow();
            currentName.setText(medsTable1.getValueAt(selectrow, 0).toString());
            currentPrice.setText(medsTable1.getValueAt(selectrow, 1).toString());
            currentStock.setText(medsTable1.getValueAt(selectrow, 2).toString());

            addStock.setEditable(true);
            updatePrice.setEditable(true);
        } else if (choice == JOptionPane.NO_OPTION) {
            int isSure = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this to database?");
            if (isSure == JOptionPane.YES_OPTION) {
                try {
                    int selectedRow = medsTable1.getSelectedRow();

                    //get the name para madelete sa database
                    String name = medsTable1.getValueAt(selectedRow, 0).toString();

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
                        updatePrice = Integer.parseInt(medsTable1.getValueAt(selectrow, 1).toString());
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

                    table(selectCommand);
                    outOfStock();
                    JOptionPane.showMessageDialog(null, "Stock and Price Updated!");
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
                    String formulation = formulationCombo.getSelectedItem().toString();

                    pst = con.prepareStatement("INSERT INTO medicine(meds_name,stock,price,formulation) VALUES(?,?,?,?)");
                    pst.setString(1, medsName);
                    pst.setInt(2, medsStock);
                    pst.setInt(3, medPrice);
                    pst.setString(4, formulation);
                    pst.executeUpdate();

                    table(selectCommand);
                    outOfStock();
                    JOptionPane.showMessageDialog(this, "Added Successfully!");

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
            //limit decimal to 2
            String changeDecimal = String.format("%.2f", totalChange);

            change.setText(changeDecimal);
            buy.setEnabled(true);
        } catch (Exception e) {
        }
    }//GEN-LAST:event_paymentKeyReleased

    private void paymentKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_paymentKeyTyped
        if (payment.getText().matches("[a-zA-Z]+")) {
            discount.setEnabled(false);
        } else if (payment.getText().isEmpty()) {
            change.setText("");
            buy.setEnabled(false);
        } else {
            discount.setEnabled(true);
        }
    }//GEN-LAST:event_paymentKeyTyped

    private void changeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_changeActionPerformed

    private void orderTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_orderTableMouseClicked
        discount.setSelected(false);
        int selectedRow = orderTable.getSelectedRow();
        int price = Integer.parseInt(orderTable.getValueAt(selectedRow, 2).toString());
        String formulation = orderTable.getValueAt(selectedRow, 3).toString();
        total -= price;

        if (formulation.equals("Generic")) {
            genericsPrice -= price;
        }

        totalTxt.setText(String.valueOf(total));

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
        currentName.setText("");
        currentPrice.setText("");
        currentStock.setText("");
        addStock.setText("");
        updatePrice.setText("");
        searchInventory.requestFocus();
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

    private void prevDailyMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevDailyMouseEntered
        prevDaily.setForeground(Color.red);
    }//GEN-LAST:event_prevDailyMouseEntered

    private void prevDailyMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevDailyMouseExited
        prevDaily.setForeground(Color.black);
    }//GEN-LAST:event_prevDailyMouseExited

    private void nextDailyMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextDailyMouseEntered
        nextDaily.setForeground(Color.red);
    }//GEN-LAST:event_nextDailyMouseEntered

    private void nextDailyMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextDailyMouseExited
        nextDaily.setForeground(Color.black);
    }//GEN-LAST:event_nextDailyMouseExited

    private void prevDailyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevDailyMouseClicked
        jTabbedPane2.setSelectedIndex(2);
    }//GEN-LAST:event_prevDailyMouseClicked

    private void nextMonthMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextMonthMouseEntered
        nextMonth.setForeground(Color.red);
    }//GEN-LAST:event_nextMonthMouseEntered

    private void nextMonthMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextMonthMouseExited
        nextMonth.setForeground(Color.black);
    }//GEN-LAST:event_nextMonthMouseExited

    private void nextWeeklyMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextWeeklyMouseEntered
        nextWeekly.setForeground(Color.red);
    }//GEN-LAST:event_nextWeeklyMouseEntered

    private void nextWeeklyMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextWeeklyMouseExited
        nextWeekly.setForeground(Color.black);
    }//GEN-LAST:event_nextWeeklyMouseExited

    private void prevWeeklyMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevWeeklyMouseEntered
        prevWeekly.setForeground(Color.red);
    }//GEN-LAST:event_prevWeeklyMouseEntered

    private void nextDailyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextDailyMouseClicked
        jTabbedPane2.setSelectedIndex(1);
    }//GEN-LAST:event_nextDailyMouseClicked

    private void prevWeeklyMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevWeeklyMouseExited
        prevWeekly.setForeground(Color.black);
    }//GEN-LAST:event_prevWeeklyMouseExited

    private void prevWeeklyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevWeeklyMouseClicked
        jTabbedPane2.setSelectedIndex(0);
    }//GEN-LAST:event_prevWeeklyMouseClicked

    private void nextWeeklyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextWeeklyMouseClicked
        jTabbedPane2.setSelectedIndex(2);
    }//GEN-LAST:event_nextWeeklyMouseClicked

    private void prevMonthMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevMonthMouseEntered
        prevMonth.setForeground(Color.red);
    }//GEN-LAST:event_prevMonthMouseEntered

    private void prevMonthMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevMonthMouseExited
        prevMonth.setForeground(Color.black);
    }//GEN-LAST:event_prevMonthMouseExited

    private void prevMonthMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevMonthMouseClicked
        jTabbedPane2.setSelectedIndex(1);
    }//GEN-LAST:event_prevMonthMouseClicked

    private void nextMonthMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextMonthMouseClicked
        jTabbedPane2.setSelectedIndex(0);
    }//GEN-LAST:event_nextMonthMouseClicked

    private void supplierMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_supplierMouseClicked
        home.setOpaque(false);
        pos.setOpaque(false);
        sales.setOpaque(false);
        expenses.setOpaque(false);
        inventory.setOpaque(false);
        supplier.setOpaque(true);

        home.setBackground(defaultColor);
        pos.setBackground(defaultColor);
        sales.setBackground(defaultColor);
        expenses.setBackground(defaultColor);
        inventory.setBackground(defaultColor);
        supplier.setBackground(clickedColor);

        jTabbedPane1.setSelectedIndex(5);
    }//GEN-LAST:event_supplierMouseClicked

    private void supplierMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_supplierMouseEntered
        supplierLbl.setForeground(Color.black);
    }//GEN-LAST:event_supplierMouseEntered

    private void supplierMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_supplierMouseExited
        supplierLbl.setForeground(Color.white);
    }//GEN-LAST:event_supplierMouseExited

    private void discountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_discountActionPerformed

    private void discountItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_discountItemStateChanged
        calculateDiscount();
    }//GEN-LAST:event_discountItemStateChanged

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
    private javax.swing.JPanel barChartPanelDaily;
    private javax.swing.JPanel barChartPanelMonthly;
    private javax.swing.JPanel barChartPanelWeekly;
    private javax.swing.JButton buy;
    private com.github.lgooddatepicker.components.CalendarPanel calendarPanel1;
    private javax.swing.JTextField change;
    private javax.swing.JLabel clear;
    private javax.swing.JTextField currentName;
    private javax.swing.JTextField currentPrice;
    private javax.swing.JTextField currentStock;
    private javax.swing.JPanel dailyChart;
    private javax.swing.JLabel dashboardIncomeLbl;
    private com.k33ptoo.components.KGradientPanel dashboardIncomePanel;
    private javax.swing.JLabel dashboardOrderLbl;
    private com.k33ptoo.components.KGradientPanel dashboardOrderPanel;
    private javax.swing.JLabel dashboardSalesLbl;
    private com.k33ptoo.components.KGradientPanel dashboardSalesPanel;
    private javax.swing.JCheckBox discount;
    private javax.swing.JLabel expLbl;
    private javax.swing.JPanel expenses;
    private javax.swing.JPanel expensesTab;
    private javax.swing.JComboBox<String> formulationCombo;
    private javax.swing.JPanel home;
    private javax.swing.JLabel homeLbl;
    private javax.swing.JPanel homeTab;
    private javax.swing.JLabel invLbl;
    private javax.swing.JPanel inventory;
    private javax.swing.JPanel inventoryTab;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
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
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private com.k33ptoo.components.KGradientPanel kGradientPanel2;
    private com.k33ptoo.components.KGradientPanel kGradientPanel5;
    private javax.swing.JLabel logout;
    private javax.swing.JTable medsTable;
    private javax.swing.JTable medsTable1;
    private javax.swing.JPanel monthlyChart;
    private javax.swing.JTextField newName;
    private javax.swing.JTextField newPrice;
    private javax.swing.JTextField newStock;
    private javax.swing.JLabel nextDaily;
    private javax.swing.JLabel nextMonth;
    private javax.swing.JLabel nextWeekly;
    private javax.swing.JTable orderTable;
    private javax.swing.JTable outOfStockTable;
    private javax.swing.JTextField payment;
    private javax.swing.JPanel pos;
    private javax.swing.JLabel posLbl;
    private javax.swing.JPanel posTab;
    private javax.swing.JLabel prevDaily;
    private javax.swing.JLabel prevMonth;
    private javax.swing.JLabel prevWeekly;
    private javax.swing.JTable purcharseHistoryTable;
    private javax.swing.JPanel sales;
    private javax.swing.JLabel salesLbl;
    private javax.swing.JPanel salesTab;
    private javax.swing.JTable salesTable;
    private javax.swing.JTextField search;
    private javax.swing.JTextField searchInventory;
    private javax.swing.JPanel supplier;
    private javax.swing.JLabel supplierLbl;
    private javax.swing.JPanel supplierTab;
    private javax.swing.JTextField totalTxt;
    private javax.swing.JButton updateBtn;
    private javax.swing.JTextField updatePrice;
    private javax.swing.JPanel weeklyChart;
    // End of variables declaration//GEN-END:variables

}
