package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.statistics.HistogramDataset;

public class pharmacy extends javax.swing.JFrame {

    //used in meds search
    DefaultTableModel model1, model2;

    //used in dashboard options
    Color defaultColor, clickedColor;
    Font fontHover, fontDefault;

    public pharmacy() {
        initComponents();
        connect();
        table(SELECT_COMMAND);
        outOfStock();
        tableDesign();
        dashboardOptionDesign();
        buttonEnabled();
        dailyTable();
        purchaseHistory();
        dashboardSalesCount();
        monthlyBarChart();
        combobox();
        disableExpensesBtn();
        callExpensesTables();

    }

    //CONNECTOR SA XAMPP MYSQL    
    private final String URL = "jdbc:mysql://localhost:3306/pharma";
    private final String USERNAME = "root";
    private final String PASSWORD = "";

    //sql command to populate the medsTable
    private final String SELECT_COMMAND = "SELECT * FROM medicine ORDER BY meds_name";

    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    //for receipt
    protected double total = 0;
    protected int qty;

    //for the discount on generic medicines.
    //used to sum all the medicine that is generic
    double genericsPrice = 0;
    double discountAmount = 0;

    //connect to mysql database
    private void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Database Connected!");

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Can't Connect!");
        }
    }

    //to populate medicine table
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

    //just tables design (fonts, colors)
    private void tableDesign() {
        Font font = new Font("Cambria", Font.BOLD, 18);
        Font insideFont = new Font("Calibri", Font.PLAIN, 17);

        //for table sa pos
        JTableHeader medsTableHeader = medsTable.getTableHeader();
        medsTableHeader.setForeground(Color.blue);
        medsTableHeader.setFont(font);
        medsTable.setShowGrid(true);
        medsTable.setGridColor(Color.BLACK);
        medsTable.setFont(insideFont);
        DefaultTableCellRenderer medsTableRenderer = (DefaultTableCellRenderer) medsTableHeader.getDefaultRenderer();
        medsTableRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer medsCenterRenderer = new DefaultTableCellRenderer();
        medsCenterRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        medsTable.setDefaultRenderer(Object.class, medsCenterRenderer);

        //for table sa inventory
        JTableHeader medsTable1Header = medsTable1.getTableHeader();
        medsTable1Header.setFont(font);
        medsTable1Header.setForeground(Color.blue);
        medsTable1.setShowGrid(true);
        medsTable1.setGridColor(Color.BLACK);
        medsTable1.setFont(insideFont);
        DefaultTableCellRenderer medsTable1Renderer = (DefaultTableCellRenderer) medsTable1Header.getDefaultRenderer();
        medsTable1Renderer.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer meds1CenterRenderer = new DefaultTableCellRenderer();
        meds1CenterRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        medsTable1.setDefaultRenderer(Object.class, meds1CenterRenderer);

        //for table sa order
        JTableHeader orderTableHeader = orderTable.getTableHeader();
        orderTableHeader.setFont(new Font("Cambria", Font.BOLD, 16));
        orderTableHeader.setForeground(Color.red);
        orderTable.setFont(new Font("Cambria", Font.PLAIN, 14));
        DefaultTableCellRenderer orderRenderer = (DefaultTableCellRenderer) orderTableHeader.getDefaultRenderer();
        orderRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer orderCenterRenderer = new DefaultTableCellRenderer();
        orderCenterRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        orderTable.setDefaultRenderer(Object.class, orderCenterRenderer);

        //for table sa purchase history
        JTableHeader purchaseHistoryHeader = purchaseHistoryTable.getTableHeader();
        purchaseHistoryHeader.setForeground(Color.blue);
        purchaseHistoryHeader.setFont(font);
        DefaultTableCellRenderer purchaseHistoryRenderer = (DefaultTableCellRenderer) purchaseHistoryHeader.getDefaultRenderer();
        purchaseHistoryRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer purchaseCenterRenderer = new DefaultTableCellRenderer();
        purchaseCenterRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        purchaseHistoryTable.setDefaultRenderer(Object.class, purchaseCenterRenderer);

        //for out of stock table
        JTableHeader stockHeader = outOfStockTable.getTableHeader();
        stockHeader.setForeground(Color.red);
        stockHeader.setFont(font);
        DefaultTableCellRenderer stockRenderer = (DefaultTableCellRenderer) stockHeader.getDefaultRenderer();
        stockRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer stockCenterRenderer = new DefaultTableCellRenderer();
        stockCenterRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        outOfStockTable.setDefaultRenderer(Object.class, stockCenterRenderer);

        //for employee salary table
        JTableHeader employeeHeader = employeeHistoryTable.getTableHeader();
        employeeHeader.setForeground(Color.blue);
        employeeHeader.setFont(font);
        DefaultTableCellRenderer employeeSalaryRenderer = (DefaultTableCellRenderer) employeeHeader.getDefaultRenderer();
        employeeSalaryRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer salaryCenterRenderer = new DefaultTableCellRenderer();
        salaryCenterRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        employeeHistoryTable.setDefaultRenderer(Object.class, salaryCenterRenderer);

        //for other expenses table
        JTableHeader otherExpensesHeader = otherExpensesTable.getTableHeader();
        otherExpensesHeader.setForeground(Color.blue);
        otherExpensesHeader.setFont(font);
        DefaultTableCellRenderer otherExpensesRenderer = (DefaultTableCellRenderer) otherExpensesHeader.getDefaultRenderer();
        otherExpensesRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer otherCenterRenderer = new DefaultTableCellRenderer();
        otherCenterRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        otherExpensesTable.setDefaultRenderer(Object.class, otherCenterRenderer);

        //for restock medicine expenses table
        JTableHeader restockExpensesHeader = restockExpensesTbl.getTableHeader();
        restockExpensesHeader.setForeground(Color.blue);
        restockExpensesHeader.setFont(new Font("Cambria", Font.BOLD, 15));
        DefaultTableCellRenderer restockExpensesRenderer = (DefaultTableCellRenderer) restockExpensesHeader.getDefaultRenderer();
        restockExpensesRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer restockCenterRenderer = new DefaultTableCellRenderer();
        restockCenterRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        restockExpensesTbl.setDefaultRenderer(Object.class, restockCenterRenderer);

        //for income table
        JTableHeader incomeHeader = incomeTable.getTableHeader();
        incomeHeader.setForeground(Color.blue);
        incomeHeader.setFont(new Font("Cambria", Font.BOLD, 25));
        DefaultTableCellRenderer incomeRenderer = (DefaultTableCellRenderer) incomeHeader.getDefaultRenderer();
        incomeRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        incomeTable.setFont(new Font("Calibri", Font.PLAIN, 20));
        incomeTable.setRowHeight(30);
        DefaultTableCellRenderer incomeCenterRenderer = new DefaultTableCellRenderer();
        incomeCenterRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        incomeTable.setDefaultRenderer(Object.class, incomeCenterRenderer);

    }

    //search for a medicine in the table.
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

    //FOR DAILY SALES - LINE CHART
    //used in line chart, the SUM of the total_price from the sold_medicine table was used, using today's date condition.
    //and it will be sent into the daily sales table as the total amount sold for today.
    private void todaySalesToDailyDB() {
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

            //para makuha ang date sa last row ng daily_sales
            //convert the date(varchar) column to date datatype
            String dateCondition = null;
            pst = con.prepareStatement("SELECT date FROM daily_sales ORDER BY STR_TO_DATE(date, '%M %d, %Y') DESC LIMIT 1");
            rs = pst.executeQuery();
            if (rs.next()) {
                dateCondition = rs.getString(1);
            }

            //para lang malaman kung ang date today ay katulad ng last row sa daily sales,
            //kasi dapat isang beses lang mag insert, ang sumunod ay i-uupdate nalang ang total_sale
            if (!formattedDate.equals(dateCondition) || dateCondition == null) {
                pst = con.prepareStatement("INSERT INTO daily_sales(date, day, total_sale) VALUES(?,?,?)");
                pst.setString(1, formattedDate);
                pst.setString(2, formattedDay);
                pst.setInt(3, todaySales);
                pst.executeUpdate();

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

    //for daily table and chart
    private void dailyTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) dailySalesTable.getModel();
            String date;
            String day;
            double totalSale;

            //used stack for LIFO in displaying on table
            Stack<Object[]> salesStack = new Stack<>();
            //subquery para last 20 days lang ang kukunin nya, -8 kasi hindi where id >=
            pst = con.prepareStatement("SELECT date, day, total_sale FROM daily_sales WHERE ID > (SELECT MAX(ID) - 21 FROM daily_sales)");
            rs = pst.executeQuery();

            while (rs.next()) {
                date = rs.getString("date");
                day = rs.getString("day");
                totalSale = rs.getDouble("total_sale");

                salesStack.push(new Object[]{date, day, totalSale});
            }

            while (!salesStack.empty()) {
                Object[] saleData = salesStack.pop();
                date = (String) saleData[0];
                day = (String) saleData[1];
                totalSale = (double) saleData[2];

                model.addRow(new Object[]{date, day, totalSale});
            }

        } catch (SQLException | NullPointerException ex) {
            JOptionPane.showMessageDialog(null, "No Database Found!");
        }
    }

    private void dailyLineChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DefaultTableModel model = (DefaultTableModel) dailySalesTable.getModel();

        //display first 6 rows of table in the chart, in descending
        //6 days only because 1 day is rest day
        int rowCount = Math.min(model.getRowCount(), 6);
        dataset.clear();
        for (int row = rowCount - 1; row >= 0; row--) {
            String day = model.getValueAt(row, 1).toString();
            double income = Double.parseDouble(model.getValueAt(row, 2).toString());

            dataset.addValue(income, "Income", day);
        }
        JFreeChart chart = ChartFactory.createLineChart("", "Last 6 Days", "Income", dataset, PlotOrientation.VERTICAL, false, true, false);
        chart.setBackgroundPaint(new Color(240, 240, 240));

        CategoryPlot plot = chart.getCategoryPlot();

        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Font tickFont = new Font("Arial", Font.PLAIN, 14);

        plot.getDomainAxis().setLabelFont(labelFont);
        plot.getDomainAxis().setTickLabelFont(tickFont);

        plot.getRangeAxis().setLabelFont(labelFont);
        plot.getRangeAxis().setTickLabelFont(tickFont);

        plot.setRangeGridlinePaint(Color.BLACK);

        chart.getCategoryPlot().setDataset(dataset); // Set the dataset before creating ChartPanel

        ChartPanel chartPanel = new ChartPanel(chart);
        lineChartPanelDaily.removeAll();
        lineChartPanelDaily.setLayout(new BorderLayout());
        lineChartPanelDaily.add(chartPanel, BorderLayout.CENTER);
        lineChartPanelDaily.validate(); // Ensure proper layout and display

    }

    //FOR WEEKLY SALES - PIE CHART
    //get the total sales in daily sales db if its in week range: ex 1-7, 8-14 ... and same month, using the 2 functions below
    private void dailySalesToWeeklyDB() {
        try {
            String[][] daysAWeek = {
                {"1", "7"},
                {"8", "14"},
                {"15", "21"},
                {"22", "28"},
                {"29", "31"}
            };
            String[] week = {"Week 1", "Week 2", "Week 3", "Week 4", "Week 5"};

            int weekSale = 0;

            LocalDate today = LocalDate.now();
            String todaysDate = today.getMonth() + " " + today.getYear();

            //get the day of the last row of daily sales and use as condition to know what week of the month now
            int day = 0;
            String dailySalesDate;
            String dayString;
            pst = con.prepareStatement("SELECT date FROM daily_sales");
            rs = pst.executeQuery();
            if (rs.last()) {
                dailySalesDate = rs.getString(1);
                dayString = dailySalesDate.substring(dailySalesDate.indexOf(" ") + 1, dailySalesDate.indexOf(",")).trim();
                day = Integer.parseInt(dayString);
            }

            //get the week of last row in weekly sale to know if the system will insert of update
            String weekCondition = null;
            pst = con.prepareStatement("SELECT week FROM weekly_sales");
            rs = pst.executeQuery();
            if (rs.last()) {
                weekCondition = rs.getString(1);
            }

            if (weekCondition == null) {
                weeklySalesIfNull(day, daysAWeek, week, todaysDate, weekSale);
            } else {
                weeklySalesIfNotNull(day, daysAWeek, week, todaysDate, weekSale, weekCondition);
            }
        } catch (SQLException | NullPointerException ex) {
            JOptionPane.showMessageDialog(null, "No Database Found!");
        }
    }

    //check if weekly sales db have row
    private void weeklySalesIfNotNull(int day, String daysAWeek[][], String week[], String todaysDate, int weekSale, String weekCondition) {
        LocalDate today = LocalDate.now();
        if (day <= 7) {
            try {
                String startOfWeek = today.getMonth().toString() + " " + daysAWeek[0][0] + ", " + today.getYear();
                String endOfWeek = today.getMonth().toString() + " " + daysAWeek[0][1] + ", " + today.getYear();

                pst = con.prepareStatement("SELECT SUM(total_sale) FROM daily_sales WHERE STR_TO_DATE(date, '%M %e, %Y') >= STR_TO_DATE(?, '%M %e, %Y') AND STR_TO_DATE(date, '%M %e, %Y') <= STR_TO_DATE(?, '%M %e, %Y');");
                pst.setString(1, startOfWeek);
                pst.setString(2, endOfWeek);
                rs = pst.executeQuery();
                if (rs.next()) {
                    weekSale = rs.getInt(1);
                }

                if (weekCondition.equals("Week 5")) {
                    pst = con.prepareStatement("INSERT INTO weekly_sales(date, week, total_sale) VALUES(?,?,?)");
                    pst.setString(1, todaysDate);
                    pst.setString(2, week[0]);
                    pst.setInt(3, weekSale);
                    pst.executeUpdate();
                } else {
                    pst = con.prepareStatement("UPDATE weekly_sales SET total_sale = ? WHERE date = ? AND week = ?");
                    pst.setInt(1, weekSale);
                    pst.setString(2, todaysDate);
                    pst.setString(3, week[0]);
                    pst.executeUpdate();
                }
            } catch (SQLException ex) {
                Logger.getLogger(pharmacy.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (day <= 14) {
            try {
                String startOfWeek = today.getMonth().toString() + " " + daysAWeek[1][0] + ", " + today.getYear();
                String endOfWeek = today.getMonth().toString() + " " + daysAWeek[1][1] + ", " + today.getYear();

                pst = con.prepareStatement("SELECT SUM(total_sale) FROM daily_sales WHERE STR_TO_DATE(date, '%M %e, %Y') >= STR_TO_DATE(?, '%M %e, %Y') AND STR_TO_DATE(date, '%M %e, %Y') <= STR_TO_DATE(?, '%M %e, %Y');");
                pst.setString(1, startOfWeek);
                pst.setString(2, endOfWeek);
                rs = pst.executeQuery();
                if (rs.next()) {
                    weekSale = rs.getInt(1);
                }

                if (weekCondition.equals("Week 1")) {
                    pst = con.prepareStatement("INSERT INTO weekly_sales(date, week, total_sale) VALUES(?,?,?)");
                    pst.setString(1, todaysDate);
                    pst.setString(2, week[1]);
                    pst.setInt(3, weekSale);
                    pst.executeUpdate();
                } else {
                    pst = con.prepareStatement("UPDATE weekly_sales SET total_sale = ? WHERE date = ? AND week = ?");
                    pst.setInt(1, weekSale);
                    pst.setString(2, todaysDate);
                    pst.setString(3, week[1]);
                    pst.executeUpdate();
                }
            } catch (SQLException ex) {
                Logger.getLogger(pharmacy.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (day <= 21) {
            try {
                String startOfWeek = today.getMonth().toString() + " " + daysAWeek[2][0] + ", " + today.getYear();
                String endOfWeek = today.getMonth().toString() + " " + daysAWeek[2][1] + ", " + today.getYear();

                pst = con.prepareStatement("SELECT SUM(total_sale) FROM daily_sales WHERE STR_TO_DATE(date, '%M %e, %Y') >= STR_TO_DATE(?, '%M %e, %Y') AND STR_TO_DATE(date, '%M %e, %Y') <= STR_TO_DATE(?, '%M %e, %Y');");
                pst.setString(1, startOfWeek);
                pst.setString(2, endOfWeek);
                rs = pst.executeQuery();
                if (rs.next()) {
                    weekSale = rs.getInt(1);
                }

                if (weekCondition.equals("Week 2")) {
                    pst = con.prepareStatement("INSERT INTO weekly_sales(date, week, total_sale) VALUES(?,?,?)");
                    pst.setString(1, todaysDate);
                    pst.setString(2, week[2]);
                    pst.setInt(3, weekSale);
                    pst.executeUpdate();
                } else {
                    pst = con.prepareStatement("UPDATE weekly_sales SET total_sale = ? WHERE date = ? AND week = ?");
                    pst.setInt(1, weekSale);
                    pst.setString(2, todaysDate);
                    pst.setString(3, week[2]);
                    pst.executeUpdate();
                }
            } catch (SQLException ex) {
                Logger.getLogger(pharmacy.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (day <= 28) {
            try {
                String startOfWeek = today.getMonth().toString() + " " + daysAWeek[3][0] + ", " + today.getYear();
                String endOfWeek = today.getMonth().toString() + " " + daysAWeek[3][1] + ", " + today.getYear();

                pst = con.prepareStatement("SELECT SUM(total_sale) FROM daily_sales WHERE STR_TO_DATE(date, '%M %e, %Y') >= STR_TO_DATE(?, '%M %e, %Y') AND STR_TO_DATE(date, '%M %e, %Y') <= STR_TO_DATE(?, '%M %e, %Y');");
                pst.setString(1, startOfWeek);
                pst.setString(2, endOfWeek);
                rs = pst.executeQuery();
                if (rs.next()) {
                    weekSale = rs.getInt(1);
                }

                if (weekCondition.equals("Week 1")) {
                    pst = con.prepareStatement("INSERT INTO weekly_sales(date, week, total_sale) VALUES(?,?,?)");
                    pst.setString(1, todaysDate);
                    pst.setString(2, week[3]);
                    pst.setInt(3, weekSale);
                    pst.executeUpdate();
                } else {
                    pst = con.prepareStatement("UPDATE weekly_sales SET total_sale = ? WHERE date = ? AND week = ?");
                    pst.setInt(1, weekSale);
                    pst.setString(2, todaysDate);
                    pst.setString(3, week[3]);
                    pst.executeUpdate();
                }
            } catch (SQLException ex) {
                Logger.getLogger(pharmacy.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                String startOfWeek = today.getMonth().toString() + " " + daysAWeek[4][0] + ", " + today.getYear();
                String endOfWeek = today.getMonth().toString() + " " + daysAWeek[4][1] + ", " + today.getYear();

                pst = con.prepareStatement("SELECT SUM(total_sale) FROM daily_sales WHERE STR_TO_DATE(date, '%M %e, %Y') >= STR_TO_DATE(?, '%M %e, %Y') AND STR_TO_DATE(date, '%M %e, %Y') <= STR_TO_DATE(?, '%M %e, %Y');");
                pst.setString(1, startOfWeek);
                pst.setString(2, endOfWeek);
                rs = pst.executeQuery();
                if (rs.next()) {
                    weekSale = rs.getInt(1);
                }

                if (weekCondition.equals("Week 4")) {
                    pst = con.prepareStatement("INSERT INTO weekly_sales(date, week, total_sale) VALUES(?,?,?)");
                    pst.setString(1, todaysDate);
                    pst.setString(2, week[4]);
                    pst.setInt(3, weekSale);
                    pst.executeUpdate();
                } else {
                    pst = con.prepareStatement("UPDATE weekly_sales SET total_sale = ? WHERE date = ? AND week = ?");
                    pst.setInt(1, weekSale);
                    pst.setString(2, todaysDate);
                    pst.setString(3, week[4]);
                    pst.executeUpdate();
                }
            } catch (SQLException ex) {
                Logger.getLogger(pharmacy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void weeklySalesIfNull(int day, String daysAWeek[][], String week[], String todaysDate, int weekSale) {
        LocalDate today = LocalDate.now();
        if (day <= 7) {
            try {
                String startOfWeek = today.getMonth().toString() + " " + daysAWeek[0][0] + ", " + today.getYear();
                String endOfWeek = today.getMonth().toString() + " " + daysAWeek[0][1] + ", " + today.getYear();

                pst = con.prepareStatement("SELECT SUM(total_sale) FROM daily_sales WHERE date >= ? AND date <= ?");
                pst.setString(1, startOfWeek);
                pst.setString(2, endOfWeek);
                rs = pst.executeQuery();
                if (rs.next()) {
                    weekSale = rs.getInt(1);
                }
                pst = con.prepareStatement("INSERT INTO weekly_sales(date, week, total_sale) VALUES(?,?,?)");
                pst.setString(1, todaysDate);
                pst.setString(2, week[0]);
                pst.setInt(3, weekSale);
                pst.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(pharmacy.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (day <= 14) {
            try {
                String startOfWeek = today.getMonth().toString() + " " + daysAWeek[1][0] + ", " + today.getYear();
                String endOfWeek = today.getMonth().toString() + " " + daysAWeek[1][1] + ", " + today.getYear();

                pst = con.prepareStatement("SELECT SUM(total_sale) FROM daily_sales WHERE date >= ? AND date <= ?");
                pst.setString(1, startOfWeek);
                pst.setString(2, endOfWeek);
                rs = pst.executeQuery();
                if (rs.next()) {
                    weekSale = rs.getInt(1);
                }
                pst = con.prepareStatement("INSERT INTO weekly_sales(date, week, total_sale) VALUES(?,?,?)");
                pst.setString(1, todaysDate);
                pst.setString(2, week[1]);
                pst.setInt(3, weekSale);
                pst.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(pharmacy.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (day <= 21) {
            try {
                String startOfWeek = today.getMonth().toString() + " " + daysAWeek[2][0] + ", " + today.getYear();
                String endOfWeek = today.getMonth().toString() + " " + daysAWeek[2][1] + ", " + today.getYear();

                pst = con.prepareStatement("SELECT SUM(total_sale) FROM daily_sales WHERE date >= ? AND date <= ?");
                pst.setString(1, startOfWeek);
                pst.setString(2, endOfWeek);
                rs = pst.executeQuery();
                if (rs.next()) {
                    weekSale = rs.getInt(1);
                }
                pst = con.prepareStatement("INSERT INTO weekly_sales(date, week, total_sale) VALUES(?,?,?)");
                pst.setString(1, todaysDate);
                pst.setString(2, week[2]);
                pst.setInt(3, weekSale);
                pst.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(pharmacy.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (day <= 28) {
            try {
                String startOfWeek = today.getMonth().toString() + " " + daysAWeek[3][0] + ", " + today.getYear();
                String endOfWeek = today.getMonth().toString() + " " + daysAWeek[3][1] + ", " + today.getYear();

                pst = con.prepareStatement("SELECT SUM(total_sale) FROM daily_sales WHERE date >= ? AND date <= ?");
                pst.setString(1, startOfWeek);
                pst.setString(2, endOfWeek);
                rs = pst.executeQuery();
                if (rs.next()) {
                    weekSale = rs.getInt(1);
                }
                pst = con.prepareStatement("INSERT INTO weekly_sales(date, week, total_sale) VALUES(?,?,?)");
                pst.setString(1, todaysDate);
                pst.setString(2, week[3]);
                pst.setInt(3, weekSale);
                pst.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(pharmacy.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                String startOfWeek = today.getMonth().toString() + " " + daysAWeek[4][0] + ", " + today.getYear();
                String endOfWeek = today.getMonth().toString() + " " + daysAWeek[4][1] + ", " + today.getYear();

                pst = con.prepareStatement("SELECT SUM(total_sale) FROM daily_sales WHERE date >= ? AND date <= ?");
                pst.setString(1, startOfWeek);
                pst.setString(2, endOfWeek);
                rs = pst.executeQuery();
                if (rs.next()) {
                    weekSale = rs.getInt(1);
                }
                pst = con.prepareStatement("INSERT INTO weekly_sales(date, week, total_sale) VALUES(?,?,?)");
                pst.setString(1, todaysDate);
                pst.setString(2, week[4]);
                pst.setInt(3, weekSale);
                pst.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(pharmacy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //to populate weekly table
    private void weeklyTable() {
        try {
            //to get todays date: ex. June 2023
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
            String todaysDate = today.format(formatter);

            DefaultTableModel model = (DefaultTableModel) weeklySalesTable.getModel();
            String date, week;
            int totalSale;

            pst = con.prepareStatement("SELECT date, week, total_sale FROM weekly_sales WHERE date = ?");
            pst.setString(1, todaysDate);
            rs = pst.executeQuery();
            while (rs.next()) {
                date = rs.getString(1);
                week = rs.getString(2);
                totalSale = rs.getInt(3);

                model.addRow(new Object[]{date, week, totalSale});
            }

        } catch (SQLException | NullPointerException ex) {
            String errorMessage = ex.getMessage();
            System.out.println("Error occurred dailyChart: " + errorMessage);
        }
    }

    //to set the populated weekly table to pie chart
    private void weeklyPieChart() {
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        DefaultTableModel model = (DefaultTableModel) weeklySalesTable.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            String week = model.getValueAt(i, 1).toString();
            int sale = Integer.parseInt(model.getValueAt(i, 2).toString());

            pieDataset.setValue(week, sale);
        }
        //get the date to display
        String date = model.getValueAt(0, 0).toString();
        weeklyIncomeChartLbl.setText(date);

        JFreeChart chart = ChartFactory.createPieChart3D("", pieDataset, false, true, true);
        chart.setBackgroundPaint(new Color(240, 240, 240));
        // Get the plot of the pie chart
        PiePlot3D plot = (PiePlot3D) chart.getPlot();

        // Enable displaying the values as labels on the pie chart
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1}"));

        Font labelFont = new Font("SansSerif", Font.PLAIN, 20);
        plot.setLabelFont(labelFont);

        plot.setDataset(pieDataset);

        ChartPanel chartPanel = new ChartPanel(chart);
        pieChartPanelWeekly.removeAll();
        pieChartPanelWeekly.add(chartPanel);
        pieChartPanelWeekly.validate();

    }

    //FOR MONTHLY SALES - BAR CHART
    //get the total sale in weekly sales db every month and populate it to monthly sales db
    private void weeklySalesToMonthlyDB() {
        try {
            LocalDate currentDate = LocalDate.now();

            // Define the formatter for full month with year
            DateTimeFormatter formatterFull = DateTimeFormatter.ofPattern("MMMM yyyy");

            // Format the current date using the formatters
            String fullMonthWithYear = currentDate.format(formatterFull);

            int totalSale = 0;
            pst = con.prepareStatement("SELECT SUM(total_sale) FROM weekly_sales WHERE date = ?");
            pst.setString(1, fullMonthWithYear);
            rs = pst.executeQuery();
            if (rs.next()) {
                totalSale = rs.getInt(1);
            }

            String dateCondition = null;
            pst = con.prepareStatement("SELECT month_and_year FROM monthly_sales");
            rs = pst.executeQuery();
            if (rs.last()) {
                dateCondition = rs.getString(1);
            }
            //if the date in monthly sales db is empty or not equal to todays month, insert, but if its equal, just update
            if (dateCondition == null || !fullMonthWithYear.equals(dateCondition)) {
                pst = con.prepareStatement("INSERT INTO monthly_sales(month_and_year, total_sale) VALUES(?,?)");
                pst.setString(1, fullMonthWithYear);
                pst.setInt(2, totalSale);
                pst.executeUpdate();
            } else {
                pst = con.prepareStatement("UPDATE monthly_sales SET total_sale = ? WHERE month_and_year = ?");
                pst.setInt(1, totalSale);
                pst.setString(2, fullMonthWithYear);
                pst.executeUpdate();
            }

        } catch (SQLException ex) {
            Logger.getLogger(pharmacy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //get the data on monthly sales db and display as graph, only 12 months will display even if different year
    private void monthlyBarChart() {
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            String date;
            int totalSale;

            pst = con.prepareStatement("SELECT month_and_year, total_sale FROM monthly_sales WHERE ID > (SELECT MAX(ID) - 13 FROM monthly_sales)");
            rs = pst.executeQuery();

            while (rs.next()) {
                date = rs.getString(1);
                totalSale = rs.getInt(2);

                String shortDate = date.substring(0, 3);
                dataset.setValue(totalSale, date, shortDate);
            }

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
        } catch (SQLException | NullPointerException ex) {
            JOptionPane.showMessageDialog(null, "No Database Found!");
        }

    }

    //to display the selected medicines in the order table
    private void showInOrderTable() {
        try {
            int selectrow = medsTable.getSelectedRow();
            int stock = Integer.parseInt(medsTable.getValueAt(selectrow, 2).toString());

            qty = Integer.parseInt(JOptionPane.showInputDialog("Quantity"));

            if (qty > stock) {
                JOptionPane.showMessageDialog(null, "Not Enough Stock!");
            } else {
                removeExistingMeds();
                discount.setSelected(false);
                String itemName = medsTable.getValueAt(selectrow, 0).toString();
                int price = Integer.parseInt(medsTable.getValueAt(selectrow, 1).toString());
                int quantity = qty;
                String formulation = medsTable.getValueAt(selectrow, 3).toString();
                price *= quantity;

                DefaultTableModel df = (DefaultTableModel) orderTable.getModel();
                //store sa array of object
                Object[] order = {itemName, quantity, price, formulation};
                df.addRow(order);

            }

        } catch (NumberFormatException | NullPointerException e) {
            JOptionPane.showMessageDialog(null, "Please Enter Valid Amount!");
        }

    }

    //to display total in order table
    private void setTotal() {
        DefaultTableModel df = (DefaultTableModel) orderTable.getModel();
        total = 0;
        int rowCount = df.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            double quantityValue = Double.parseDouble(df.getValueAt(i, 2).toString());
            total += quantityValue;
        }
        totalTxt.setText(String.valueOf(total));
    }

    //to remove the medicine in order table if the same medicine is picked
    private void removeExistingMeds() {
        int selectrow = medsTable.getSelectedRow();
        String name = medsTable.getValueAt(selectrow, 0).toString();

        DefaultTableModel order = (DefaultTableModel) orderTable.getModel();
        int orderRow = orderTable.getRowCount();

        for (int i = 0; i < orderRow; i++) {
            if (name.equals(orderTable.getValueAt(i, 0))) {
                order.removeRow(i);
                break;
            }
        }
    }

    //will be cleared or returned to an empty state after the item is purchased
    private void clearWhenBuy() {
        genericsPrice = 0;
        totalTxt.setText("");
        payment.setText("");
        change.setText("");
        search.setText("");
        discount.setSelected(false);
        buy.setEnabled(false);
        JOptionPane.showMessageDialog(null, "Buying Done!");
    }

    //to update the stock of medicine if someone buys it
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
        //since the purchase has been made, the total should be returned to 0.
        total = 0;

        table(SELECT_COMMAND);
        outOfStock();
    }

    //to populate the sold_medicine in database, the data is from order table
    private void soldMedsToDB() {
        try {
            //LocalDateTime currentDateTime = LocalDateTime.of(2023, 6, 15, 9, 30 , 0);
            //June 15, 2023 9:30AM  hh:mm:ss a - AM/PM indicator
            LocalDateTime currentDateTime = LocalDateTime.now();
            String formattedDateTime = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            //last user ID was retrieved from the login_history table in the database.
            int userID = 1;
            pst = con.prepareStatement("SELECT userID FROM login_history");
            rs = pst.executeQuery();
            if (rs.last()) {
                userID = rs.getInt(1);
            }
            //used a StringBuilder to retrieve all the items and concatenate them into one string.
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

    //to populate the purchase history on dashboard tab
    private void purchaseHistory() {
        //need to clear the contents of the purchase table first because it will be populated with new data every time this function is called.
        DefaultTableModel model = (DefaultTableModel) purchaseHistoryTable.getModel();
        model.setRowCount(0);
        try {
            LocalDateTime currentDateTime = LocalDateTime.now();
            String formattedDateTime = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("h:mm a");

            //i used a join since they have a relationship.
            pst = con.prepareStatement("SELECT sold_medicine.items, sold_medicine.total_price, sold_medicine.date_sold, login.username FROM sold_medicine JOIN login ON sold_medicine.login_id = login.ID WHERE DATE(sold_medicine.date_sold) = ?");
            pst.setString(1, formattedDateTime);
            rs = pst.executeQuery();
            while (rs.next()) {
                String items = rs.getString(1);
                String totalPrice = rs.getString(2);

                //convert the date_sold with datetime data type to h: mm a format (3:12 PM)
                LocalDateTime dateSold = rs.getTimestamp(3).toLocalDateTime();
                String formattedDate = timeFormat.format(dateSold);

                String seller = rs.getString(4);
                model.addRow(new Object[]{items, totalPrice, formattedDate, seller});
            }

        } catch (SQLException | NullPointerException e) {

        }
    }

    //check if the medicine stock is below 10, if yes show it to the dashboard
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
            JOptionPane.showMessageDialog(null, "No Database Found!");
        }
    }

    //calculate the discount if discount checkbox is selected
    private void calculateDiscount() {
        try {
            //get first the medicine that is "Generic"
            DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
            int rowCount = model.getRowCount();
            genericsPrice = 0.0;

            for (int i = 0; i < rowCount; i++) {
                String formulation = model.getValueAt(i, 3).toString();
                if (formulation.equals("Generic")) {
                    double price = Double.parseDouble(model.getValueAt(i, 2).toString());
                    genericsPrice += price;
                }
            }

            if (discount.isSelected()) {
                int pay = Integer.parseInt(payment.getText());
                discountAmount = genericsPrice * 0.20;

                double totalWithDiscount = Double.parseDouble(totalTxt.getText()) - discountAmount;
                int roundedTotal = (int) Math.round(totalWithDiscount);
                totalTxt.setText(String.valueOf(roundedTotal));

                // To obtain the value of payment, subtract it from the total, and display it as change.
                int roundedChange = (int) Math.round(pay - totalWithDiscount);
                change.setText(String.valueOf(roundedChange));
            } else {
                totalTxt.setText(String.valueOf(total));

                // To show the change real-time if checkbox is not selected
                int pay = Integer.parseInt(payment.getText());
                int roundedChange = (int) Math.round(pay - total);
                change.setText(String.valueOf(roundedChange));
            }
        } catch (NumberFormatException ex) {

        }
    }

    //to disabled button first if the requirements is not met
    private void buttonEnabled() {
        buy.setEnabled(false);
        updateBtn.setEnabled(false);
        addNewMedBtn.setEnabled(false);
        employeeProceed.setEnabled(false);
        otherExpensesProceedBtn.setEnabled(false);
        restockMedsProcessBtn.setEnabled(false);
    }

    //design of the dashboard options when clicked or hover
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

    //To display the number of orders and total sales for today on the dashboard.
    private void dashboardSalesCount() {
        try {
            //The order count was taken from the purchase history, indicating the number of rows there.
            DefaultTableModel purchase = (DefaultTableModel) purchaseHistoryTable.getModel();
            int salesCount = purchase.getRowCount();
            dashboardOrderLbl.setText(String.valueOf(salesCount));

            //added all the price in purchase history
            int totalSales = 0;
            for (int i = 0; i < purchase.getRowCount(); i++) {
                totalSales += Integer.parseInt(purchase.getValueAt(i, 1).toString());
            }
            dashboardSalesLbl.setText(String.valueOf(totalSales));

            //to display the monthly sale on dashboard
            int monthlySales = 0;
            pst = con.prepareStatement("SELECT total_sale FROM monthly_sales");
            rs = pst.executeQuery();
            if (rs.last()) {
                monthlySales = rs.getInt(1);
            }
            dashboardSaleLbl.setText(String.valueOf(monthlySales));

        } catch (SQLException | NullPointerException ex) {
            JOptionPane.showMessageDialog(null, "No Database Found!");
        }

    }

    //to get the username of employee and display to salary expenses
    private void combobox() {
        try {
            pst = con.prepareStatement("SELECT username FROM login WHERE position = 'Employee'");
            rs = pst.executeQuery();
            while (rs.next()) {
                employeeCombo.addItem(rs.getString("username"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(login.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(this, "No Database Found!");
        }

    }

    //to disable the expenses button when user is not the owner
    private void disableExpensesBtn() {
        try {
            String position = "";
            pst = con.prepareStatement("SELECT login.position FROM login JOIN login_history ON login.ID = login_history.userID WHERE login_history.ID = (SELECT MAX(ID) FROM login_history);");
            rs = pst.executeQuery();

            if (rs.first()) {
                position = rs.getString(1);
            }
            if (!position.equals("Owner")) {
                //for salary
                employeeCombo.setEnabled(false);
                employeeSalaryTxt.setEditable(false);
                employeeProceed.setEnabled(false);

                //for other expenses
                otherExpensesAmountTxt.setEditable(false);
                otherExpensesNameTxt.setEditable(false);
                otherExpensesProceedBtn.setEnabled(false);

                //for restock expenses
                restockMedsAmountTxt.setEditable(false);
                restockMedsNameTxt.setEditable(false);
                restockMedsQuanTxt.setEditable(false);
                restockMedsProcessBtn.setEnabled(false);
                restockExpensesTbl.setEnabled(false);

                pinCode.setEnabled(false);
                pinCode.setToolTipText("You need to be the Owner to access this!");
            } else {
                //to show the pin code
                pinCode.setEnabled(true);
                pinCode.setToolTipText("Click to view the pin code");
            }

        } catch (SQLException | NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "Unexpected Error!");
        }
    }

    //to show the data in tables in expenses
    private void callExpensesTables() {
        employeeSalaryHistory();
        otherExpensesTable();
        restockExpensesTable();
    }

    //for income chart
    private void incomeToDB() {
        try {
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
            String currentMonthAndYr = formatter.format(currentDate);

            int restoctRowCount = restockExpensesTbl.getRowCount();
            int employeeRowCount = employeeHistoryTable.getRowCount();
            int otherRowCount = otherExpensesTable.getRowCount();
            float sumOfExpenses = 0;

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");

            //for restock medicine expenses
            for (int i = 0; i < restoctRowCount; i++) {
                //check the row if it is paid
                String paidStatus = restockExpensesTbl.getValueAt(i, 4).toString();

                if (paidStatus.equals("Paid")) {
                    sumOfExpenses += Float.parseFloat(restockExpensesTbl.getValueAt(i, 2).toString());
                }

            }

            //for employee salary expenses
            for (int i = 0; i < employeeRowCount; i++) {
                //convert the date July 9, 2023 to July 2023 only
                String datePaid = employeeHistoryTable.getValueAt(i, 2).toString();

                LocalDate convertDate = LocalDate.parse(datePaid, inputFormatter);
                String formattedDate = outputFormatter.format(convertDate);

                if (currentMonthAndYr.equals(formattedDate)) {
                    sumOfExpenses += Float.parseFloat(employeeHistoryTable.getValueAt(i, 1).toString());
                }
            }

            //for other expenses
            for (int i = 0; i < otherRowCount; i++) {
                //convert the date July 9, 2023 to July 2023 only
                String datePaid = otherExpensesTable.getValueAt(i, 2).toString();

                LocalDate convertDate = LocalDate.parse(datePaid, inputFormatter);
                String formattedDate = outputFormatter.format(convertDate);

                if (currentMonthAndYr.equals(formattedDate)) {
                    sumOfExpenses += Float.parseFloat(otherExpensesTable.getValueAt(i, 1).toString());
                }
            }

            float totalSales = Float.parseFloat(dashboardSaleLbl.getText());
            float totalIncome = totalSales - sumOfExpenses;

            String dateCondition = null;
            pst = con.prepareStatement("SELECT month_and_year FROM monthly_income");
            rs = pst.executeQuery();
            if (rs.last()) {
                dateCondition = rs.getString(1);
            }

            if (dateCondition == null || !dateCondition.equals(currentMonthAndYr)) {
                pst = con.prepareStatement("INSERT INTO monthly_income(month_and_year, income) VALUES(?,?)");
                pst.setString(1, currentMonthAndYr);
                pst.setFloat(2, totalIncome);
                pst.executeUpdate();
            } else {
                pst = con.prepareStatement("UPDATE monthly_income SET income = ? WHERE month_and_year = ?");
                pst.setFloat(1, totalIncome);
                pst.setString(2, currentMonthAndYr);
                pst.executeUpdate();
            }
        } catch (NullPointerException | SQLException ex) {
            Logger.getLogger(pharmacy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //to populate income table
    private void incomeTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) incomeTable.getModel();
            model.setRowCount(0);

            //used stack for LIFO in displaying on table
            Stack<Object[]> incomeStack = new Stack<>();

            pst = con.prepareStatement("SELECT month_and_year, income FROM monthly_income");
            rs = pst.executeQuery();
            while (rs.next()) {
                String date = rs.getString(1);
                float totalIncome = rs.getFloat(2);

                incomeStack.push(new Object[]{date, totalIncome});
            }

            while (!incomeStack.isEmpty()) {
                Object[] rowData = incomeStack.pop();
                model.addRow(rowData);
            }

        } catch (SQLException ex) {
            Logger.getLogger(pharmacy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //to show the income of todays date
    private void showIncome() {
        try {
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
            String currentMonthAndYr = formatter.format(currentDate);

            float totalIncome = Float.parseFloat(incomeTable.getValueAt(0, 1).toString());

            if (totalIncome < 0) {
                incomeLbl.setText("Outstanding balance this " + currentMonthAndYr);
            } else {
                incomeLbl.setText("Income this " + currentMonthAndYr);
            }

            setIncomeLbl.setText("Php " + String.valueOf(totalIncome));
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {

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
        income = new javax.swing.JPanel();
        incomeLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        logout = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        tabOptions = new javax.swing.JTabbedPane();
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
        dashboardSaleLbl = new javax.swing.JLabel();
        calendarPanel1 = new com.github.lgooddatepicker.components.CalendarPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        purchaseHistoryTable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        outOfStockTable = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        pinCode = new javax.swing.JLabel();
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
        dailyWeeklyMonthlyTab = new javax.swing.JTabbedPane();
        dailyChart = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        prevDaily = new javax.swing.JLabel();
        nextDaily = new javax.swing.JLabel();
        lineChartPanelDaily = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        dailySalesTable = new javax.swing.JTable();
        weeklyChart = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        nextWeekly = new javax.swing.JLabel();
        prevWeekly = new javax.swing.JLabel();
        pieChartPanelWeekly = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        weeklySalesTable = new javax.swing.JTable();
        weeklyIncomeChartLbl = new javax.swing.JLabel();
        monthlyChart = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        prevMonth = new javax.swing.JLabel();
        nextMonth = new javax.swing.JLabel();
        barChartPanelMonthly = new javax.swing.JPanel();
        expensesTab = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        noteLbl2 = new javax.swing.JLabel();
        kGradientPanel4 = new com.k33ptoo.components.KGradientPanel();
        jLabel41 = new javax.swing.JLabel();
        restockMedsAmountTxt = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        restockMedsProcessBtn = new javax.swing.JButton();
        restockMedsNameTxt = new javax.swing.JTextField();
        restockMedsQuanTxt = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        restockExpensesTbl = new javax.swing.JTable();
        jLabel44 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        employeeHistoryTable = new javax.swing.JTable();
        kGradientPanel1 = new com.k33ptoo.components.KGradientPanel();
        jLabel13 = new javax.swing.JLabel();
        employeeCombo = new javax.swing.JComboBox<>();
        employeeSalaryTxt = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        employeeProceed = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        noteLbl = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        otherExpensesTable = new javax.swing.JTable();
        jLabel18 = new javax.swing.JLabel();
        kGradientPanel3 = new com.k33ptoo.components.KGradientPanel();
        jLabel19 = new javax.swing.JLabel();
        otherExpensesAmountTxt = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        otherExpensesProceedBtn = new javax.swing.JButton();
        otherExpensesNameTxt = new javax.swing.JTextField();
        noteLbl1 = new javax.swing.JLabel();
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
        incomeTab = new javax.swing.JPanel();
        incomeLbl = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        incomeTable = new javax.swing.JTable();
        kGradientPanel6 = new com.k33ptoo.components.KGradientPanel();
        setIncomeLbl = new javax.swing.JLabel();

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

        income.setOpaque(false);
        income.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                incomeMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                incomeMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                incomeMouseExited(evt);
            }
        });

        incomeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        incomeLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/income.png"))); // NOI18N
        incomeLabel.setText("  Income");
        incomeLabel.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        incomeLabel.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout incomeLayout = new javax.swing.GroupLayout(income);
        income.setLayout(incomeLayout);
        incomeLayout.setHorizontalGroup(
            incomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(incomeLayout.createSequentialGroup()
                .addComponent(incomeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                .addContainerGap())
        );
        incomeLayout.setVerticalGroup(
            incomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(incomeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
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
                                    .addComponent(income, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(11, 11, 11))
                        .addComponent(jLabel5)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(income, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addComponent(logout)
                .addContainerGap())
        );

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dashboardOrderPanel.setkGradientFocus(300);
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

        dashboardSalesPanel.setkGradientFocus(300);
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

        dashboardIncomePanel.setkGradientFocus(300);
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
        jLabel31.setText("Sales this Month");
        jLabel31.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N

        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/dashboard_income.png"))); // NOI18N

        dashboardSaleLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dashboardSaleLbl.setText("0");
        dashboardSaleLbl.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        dashboardSaleLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dashboardSaleLblMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dashboardSaleLblMouseExited(evt);
            }
        });

        javax.swing.GroupLayout dashboardIncomePanelLayout = new javax.swing.GroupLayout(dashboardIncomePanel);
        dashboardIncomePanel.setLayout(dashboardIncomePanelLayout);
        dashboardIncomePanelLayout.setHorizontalGroup(
            dashboardIncomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashboardIncomePanelLayout.createSequentialGroup()
                .addGroup(dashboardIncomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dashboardSaleLbl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addContainerGap()
                .addGroup(dashboardIncomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel36)
                    .addGroup(dashboardIncomePanelLayout.createSequentialGroup()
                        .addComponent(dashboardSaleLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        calendarPanel1.setBackground(new java.awt.Color(255, 255, 255));
        calendarPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        calendarPanel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        purchaseHistoryTable.setModel(new javax.swing.table.DefaultTableModel(
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
        purchaseHistoryTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(purchaseHistoryTable);
        if (purchaseHistoryTable.getColumnModel().getColumnCount() > 0) {
            purchaseHistoryTable.getColumnModel().getColumn(0).setMinWidth(160);
            purchaseHistoryTable.getColumnModel().getColumn(0).setPreferredWidth(160);
            purchaseHistoryTable.getColumnModel().getColumn(1).setResizable(false);
            purchaseHistoryTable.getColumnModel().getColumn(1).setPreferredWidth(30);
            purchaseHistoryTable.getColumnModel().getColumn(2).setResizable(false);
            purchaseHistoryTable.getColumnModel().getColumn(3).setResizable(false);
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
            outOfStockTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        }

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Out of Stock");
        jLabel7.setFont(new java.awt.Font("Tahoma", 2, 18)); // NOI18N

        pinCode.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        pinCode.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pinCode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/pin_code.png"))); // NOI18N
        pinCode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pinCodeMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout homeTabLayout = new javax.swing.GroupLayout(homeTab);
        homeTab.setLayout(homeTabLayout);
        homeTabLayout.setHorizontalGroup(
            homeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homeTabLayout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addGroup(homeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(homeTabLayout.createSequentialGroup()
                        .addComponent(dashboardOrderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(dashboardSalesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(dashboardIncomePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(69, 69, 69)
                        .addComponent(pinCode, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(homeTabLayout.createSequentialGroup()
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
                        .addGap(157, 157, 157))))
        );
        homeTabLayout.setVerticalGroup(
            homeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homeTabLayout.createSequentialGroup()
                .addGroup(homeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(homeTabLayout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addGroup(homeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dashboardOrderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dashboardSalesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dashboardIncomePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(homeTabLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pinCode, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
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

        tabOptions.addTab("tab1", homeTab);

        buy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/buy.png"))); // NOI18N
        buy.setText(" Buy (Enter)");
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
            medsTable.getColumnModel().getColumn(0).setPreferredWidth(150);
            medsTable.getColumnModel().getColumn(1).setResizable(false);
            medsTable.getColumnModel().getColumn(2).setResizable(false);
            medsTable.getColumnModel().getColumn(3).setResizable(false);
            medsTable.getColumnModel().getColumn(3).setPreferredWidth(120);
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
            orderTable.getColumnModel().getColumn(0).setPreferredWidth(140);
            orderTable.getColumnModel().getColumn(1).setResizable(false);
            orderTable.getColumnModel().getColumn(1).setPreferredWidth(100);
            orderTable.getColumnModel().getColumn(2).setResizable(false);
            orderTable.getColumnModel().getColumn(3).setResizable(false);
            orderTable.getColumnModel().getColumn(3).setPreferredWidth(120);
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

        payment.setEditable(false);
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

        discount.setText("20% Discount (D)");
        discount.setBackground(new java.awt.Color(255, 255, 255));
        discount.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        discount.setForeground(new java.awt.Color(0, 0, 255));
        discount.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                discountItemStateChanged(evt);
            }
        });
        discount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                discountKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout posTabLayout = new javax.swing.GroupLayout(posTab);
        posTab.setLayout(posTabLayout);
        posTabLayout.setHorizontalGroup(
            posTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(posTabLayout.createSequentialGroup()
                .addGroup(posTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(posTabLayout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(288, 288, 288)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(posTabLayout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(posTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, posTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
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
                                            .addComponent(discount)))))
                            .addComponent(buy, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(132, Short.MAX_VALUE))
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
                .addContainerGap(89, Short.MAX_VALUE))
        );

        tabOptions.addTab("tab2", posTab);

        dailyWeeklyMonthlyTab.setOpaque(true);
        dailyWeeklyMonthlyTab.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dailyWeeklyMonthlyTabMouseClicked(evt);
            }
        });

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

        lineChartPanelDaily.setLayout(new java.awt.BorderLayout());

        dailySalesTable.setModel(new javax.swing.table.DefaultTableModel(
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
        dailySalesTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(dailySalesTable);
        if (dailySalesTable.getColumnModel().getColumnCount() > 0) {
            dailySalesTable.getColumnModel().getColumn(0).setResizable(false);
            dailySalesTable.getColumnModel().getColumn(0).setPreferredWidth(100);
            dailySalesTable.getColumnModel().getColumn(1).setResizable(false);
            dailySalesTable.getColumnModel().getColumn(1).setPreferredWidth(80);
            dailySalesTable.getColumnModel().getColumn(2).setResizable(false);
            dailySalesTable.getColumnModel().getColumn(2).setPreferredWidth(60);
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
                .addComponent(lineChartPanelDaily, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                        .addComponent(lineChartPanelDaily, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(26, 26, 26))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dailyChartLayout.createSequentialGroup()
                        .addGap(0, 79, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(125, 125, 125))))
        );

        dailyWeeklyMonthlyTab.addTab("Daily ", new javax.swing.ImageIcon(getClass().getResource("/icons/daily.png")), dailyChart); // NOI18N

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

        pieChartPanelWeekly.setLayout(new java.awt.BorderLayout());

        weeklySalesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "Week", "Sales"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        weeklySalesTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(weeklySalesTable);
        if (weeklySalesTable.getColumnModel().getColumnCount() > 0) {
            weeklySalesTable.getColumnModel().getColumn(0).setResizable(false);
            weeklySalesTable.getColumnModel().getColumn(0).setPreferredWidth(100);
            weeklySalesTable.getColumnModel().getColumn(1).setResizable(false);
            weeklySalesTable.getColumnModel().getColumn(1).setPreferredWidth(80);
            weeklySalesTable.getColumnModel().getColumn(2).setResizable(false);
            weeklySalesTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        }

        weeklyIncomeChartLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        weeklyIncomeChartLbl.setText("Month");
        weeklyIncomeChartLbl.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        weeklyIncomeChartLbl.setForeground(new java.awt.Color(255, 0, 51));

        javax.swing.GroupLayout weeklyChartLayout = new javax.swing.GroupLayout(weeklyChart);
        weeklyChart.setLayout(weeklyChartLayout);
        weeklyChartLayout.setHorizontalGroup(
            weeklyChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(weeklyChartLayout.createSequentialGroup()
                .addGap(156, 156, 156)
                .addComponent(prevWeekly)
                .addGap(18, 18, 18)
                .addComponent(jLabel37)
                .addGap(27, 27, 27)
                .addComponent(nextWeekly)
                .addContainerGap(171, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, weeklyChartLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(weeklyChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(weeklyIncomeChartLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 557, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pieChartPanelWeekly, javax.swing.GroupLayout.PREFERRED_SIZE, 592, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
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
                .addGroup(weeklyChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(weeklyChartLayout.createSequentialGroup()
                        .addGap(101, 101, 101)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(weeklyChartLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(weeklyIncomeChartLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pieChartPanelWeekly, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(58, 58, 58))))
        );

        dailyWeeklyMonthlyTab.addTab("Weekly", new javax.swing.ImageIcon(getClass().getResource("/icons/weekly.png")), weeklyChart); // NOI18N

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

        dailyWeeklyMonthlyTab.addTab("Monthly", new javax.swing.ImageIcon(getClass().getResource("/icons/monthly.png")), monthlyChart); // NOI18N

        javax.swing.GroupLayout salesTabLayout = new javax.swing.GroupLayout(salesTab);
        salesTab.setLayout(salesTabLayout);
        salesTabLayout.setHorizontalGroup(
            salesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(salesTabLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(dailyWeeklyMonthlyTab, javax.swing.GroupLayout.PREFERRED_SIZE, 884, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(111, Short.MAX_VALUE))
        );
        salesTabLayout.setVerticalGroup(
            salesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(salesTabLayout.createSequentialGroup()
                .addComponent(dailyWeeklyMonthlyTab, javax.swing.GroupLayout.PREFERRED_SIZE, 641, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 61, Short.MAX_VALUE))
        );

        tabOptions.addTab("tab3", salesTab);

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        noteLbl2.setForeground(new java.awt.Color(255, 51, 51));
        noteLbl2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        noteLbl2.setText("note : you need to be the owner to access this section");
        jPanel3.add(noteLbl2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 80, 320, -1));

        kGradientPanel4.setkBorderRadius(30);

        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel41.setText("Restock Medicine");
        jLabel41.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N

        restockMedsAmountTxt.setEditable(false);
        restockMedsAmountTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        restockMedsAmountTxt.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        restockMedsAmountTxt.setForeground(new java.awt.Color(255, 0, 0));
        restockMedsAmountTxt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                restockMedsAmountTxtKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                restockMedsAmountTxtKeyReleased(evt);
            }
        });

        jLabel42.setText("Name:");
        jLabel42.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        jLabel43.setText("Amount:");
        jLabel43.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        restockMedsProcessBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/proceed.png"))); // NOI18N
        restockMedsProcessBtn.setText("Proceed");
        restockMedsProcessBtn.setBackground(new java.awt.Color(255, 255, 255));
        restockMedsProcessBtn.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        restockMedsProcessBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                restockMedsProcessBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                restockMedsProcessBtnMouseExited(evt);
            }
        });
        restockMedsProcessBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restockMedsProcessBtnActionPerformed(evt);
            }
        });

        restockMedsNameTxt.setEditable(false);
        restockMedsNameTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        restockMedsNameTxt.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        restockMedsNameTxt.setForeground(new java.awt.Color(255, 0, 0));

        restockMedsQuanTxt.setEditable(false);
        restockMedsQuanTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        restockMedsQuanTxt.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        restockMedsQuanTxt.setForeground(new java.awt.Color(255, 0, 0));

        jLabel45.setText("Quantity:");
        jLabel45.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        javax.swing.GroupLayout kGradientPanel4Layout = new javax.swing.GroupLayout(kGradientPanel4);
        kGradientPanel4.setLayout(kGradientPanel4Layout);
        kGradientPanel4Layout.setHorizontalGroup(
            kGradientPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel4Layout.createSequentialGroup()
                .addGroup(kGradientPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kGradientPanel4Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(kGradientPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(restockMedsAmountTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(restockMedsNameTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(restockMedsQuanTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(kGradientPanel4Layout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addComponent(restockMedsProcessBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jLabel41, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        kGradientPanel4Layout.setVerticalGroup(
            kGradientPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel4Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel41)
                .addGap(25, 25, 25)
                .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(restockMedsNameTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(restockMedsQuanTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(restockMedsAmountTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(restockMedsProcessBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37))
        );

        jPanel3.add(kGradientPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, 320, 470));

        restockExpensesTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Medicine", "Quantity", "Amount", "Date Paid", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        restockExpensesTbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                restockExpensesTblMouseClicked(evt);
            }
        });
        jScrollPane10.setViewportView(restockExpensesTbl);
        if (restockExpensesTbl.getColumnModel().getColumnCount() > 0) {
            restockExpensesTbl.getColumnModel().getColumn(0).setResizable(false);
            restockExpensesTbl.getColumnModel().getColumn(0).setPreferredWidth(170);
            restockExpensesTbl.getColumnModel().getColumn(1).setResizable(false);
            restockExpensesTbl.getColumnModel().getColumn(2).setResizable(false);
            restockExpensesTbl.getColumnModel().getColumn(3).setResizable(false);
            restockExpensesTbl.getColumnModel().getColumn(3).setPreferredWidth(130);
            restockExpensesTbl.getColumnModel().getColumn(4).setResizable(false);
        }

        jPanel3.add(jScrollPane10, new org.netbeans.lib.awtextra.AbsoluteConstraints(382, 90, 490, 480));

        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel44.setText("Click to pay");
        jLabel44.setFont(new java.awt.Font("Segoe UI", 3, 36)); // NOI18N
        jPanel3.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 30, 450, -1));

        jTabbedPane1.addTab("Restock Medicine   ", new javax.swing.ImageIcon(getClass().getResource("/icons/restock_medicine.png")), jPanel3); // NOI18N

        employeeHistoryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Employee Name", "Salary", "Date", "Name of Payer"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane8.setViewportView(employeeHistoryTable);
        if (employeeHistoryTable.getColumnModel().getColumnCount() > 0) {
            employeeHistoryTable.getColumnModel().getColumn(0).setResizable(false);
            employeeHistoryTable.getColumnModel().getColumn(0).setPreferredWidth(170);
            employeeHistoryTable.getColumnModel().getColumn(1).setResizable(false);
            employeeHistoryTable.getColumnModel().getColumn(2).setResizable(false);
            employeeHistoryTable.getColumnModel().getColumn(2).setPreferredWidth(130);
            employeeHistoryTable.getColumnModel().getColumn(3).setResizable(false);
            employeeHistoryTable.getColumnModel().getColumn(3).setPreferredWidth(150);
            employeeHistoryTable.getColumnModel().getColumn(3).setHeaderValue("Name of Payer");
        }

        kGradientPanel1.setkBorderRadius(30);

        jLabel13.setText("Employee Salary");
        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N

        employeeSalaryTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        employeeSalaryTxt.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        employeeSalaryTxt.setForeground(new java.awt.Color(255, 0, 0));
        employeeSalaryTxt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                employeeSalaryTxtKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                employeeSalaryTxtKeyReleased(evt);
            }
        });

        jLabel4.setText("Name:");
        jLabel4.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        jLabel14.setText("Amount:");
        jLabel14.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        employeeProceed.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/proceed.png"))); // NOI18N
        employeeProceed.setText("Proceed");
        employeeProceed.setBackground(new java.awt.Color(255, 255, 255));
        employeeProceed.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        employeeProceed.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                employeeProceedMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                employeeProceedMouseExited(evt);
            }
        });
        employeeProceed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                employeeProceedActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout kGradientPanel1Layout = new javax.swing.GroupLayout(kGradientPanel1);
        kGradientPanel1.setLayout(kGradientPanel1Layout);
        kGradientPanel1Layout.setHorizontalGroup(
            kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addGap(71, 71, 71)
                        .addComponent(jLabel13))
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(employeeSalaryTxt)
                            .addComponent(employeeCombo, 0, 242, Short.MAX_VALUE)))
                    .addGroup(kGradientPanel1Layout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addComponent(employeeProceed, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        kGradientPanel1Layout.setVerticalGroup(
            kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel13)
                .addGap(25, 25, 25)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(employeeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(employeeSalaryTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addComponent(employeeProceed, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37))
        );

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Salary History");
        jLabel10.setFont(new java.awt.Font("Segoe UI", 3, 36)); // NOI18N

        noteLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        noteLbl.setText("note : you need to be the owner to access this section");
        noteLbl.setForeground(new java.awt.Color(255, 51, 51));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(kGradientPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(noteLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(26, 26, 26)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
                        .addGap(13, 13, 13))
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 494, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(noteLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(kGradientPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(41, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Employee Expenses   ", new javax.swing.ImageIcon(getClass().getResource("/icons/employee_expenses.png")), jPanel2); // NOI18N

        otherExpensesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Itemized Expenses", "Amount", "Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane9.setViewportView(otherExpensesTable);
        if (otherExpensesTable.getColumnModel().getColumnCount() > 0) {
            otherExpensesTable.getColumnModel().getColumn(0).setResizable(false);
            otherExpensesTable.getColumnModel().getColumn(0).setPreferredWidth(170);
            otherExpensesTable.getColumnModel().getColumn(1).setResizable(false);
            otherExpensesTable.getColumnModel().getColumn(2).setResizable(false);
            otherExpensesTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        }

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Expenses");
        jLabel18.setFont(new java.awt.Font("Segoe UI", 3, 36)); // NOI18N

        kGradientPanel3.setkBorderRadius(30);

        jLabel19.setText("Other Expenses");
        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N

        otherExpensesAmountTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        otherExpensesAmountTxt.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        otherExpensesAmountTxt.setForeground(new java.awt.Color(255, 0, 0));
        otherExpensesAmountTxt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                otherExpensesAmountTxtKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                otherExpensesAmountTxtKeyReleased(evt);
            }
        });

        jLabel39.setText("Name:");
        jLabel39.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        jLabel40.setText("Amount:");
        jLabel40.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        otherExpensesProceedBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/proceed.png"))); // NOI18N
        otherExpensesProceedBtn.setText("Proceed");
        otherExpensesProceedBtn.setBackground(new java.awt.Color(255, 255, 255));
        otherExpensesProceedBtn.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        otherExpensesProceedBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                otherExpensesProceedBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                otherExpensesProceedBtnMouseExited(evt);
            }
        });
        otherExpensesProceedBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                otherExpensesProceedBtnActionPerformed(evt);
            }
        });

        otherExpensesNameTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        otherExpensesNameTxt.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        otherExpensesNameTxt.setForeground(new java.awt.Color(255, 0, 0));
        otherExpensesNameTxt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                otherExpensesNameTxtKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout kGradientPanel3Layout = new javax.swing.GroupLayout(kGradientPanel3);
        kGradientPanel3.setLayout(kGradientPanel3Layout);
        kGradientPanel3Layout.setHorizontalGroup(
            kGradientPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel3Layout.createSequentialGroup()
                .addGroup(kGradientPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kGradientPanel3Layout.createSequentialGroup()
                        .addGap(71, 71, 71)
                        .addComponent(jLabel19))
                    .addGroup(kGradientPanel3Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(kGradientPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(otherExpensesAmountTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(otherExpensesNameTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(kGradientPanel3Layout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addComponent(otherExpensesProceedBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        kGradientPanel3Layout.setVerticalGroup(
            kGradientPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel19)
                .addGap(25, 25, 25)
                .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(otherExpensesNameTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(otherExpensesAmountTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addComponent(otherExpensesProceedBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37))
        );

        noteLbl1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        noteLbl1.setText("note : you need to be the owner to access this section");
        noteLbl1.setForeground(new java.awt.Color(255, 51, 51));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(kGradientPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(noteLbl1, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE))
                .addGap(26, 26, 26)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
                        .addGap(13, 13, 13))
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 494, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(noteLbl1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(kGradientPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(41, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Other   ", new javax.swing.ImageIcon(getClass().getResource("/icons/other_expenses.png")), jPanel4); // NOI18N

        javax.swing.GroupLayout expensesTabLayout = new javax.swing.GroupLayout(expensesTab);
        expensesTab.setLayout(expensesTabLayout);
        expensesTabLayout.setHorizontalGroup(
            expensesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(expensesTabLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 891, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(106, Short.MAX_VALUE))
        );
        expensesTabLayout.setVerticalGroup(
            expensesTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(expensesTabLayout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 644, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 58, Short.MAX_VALUE))
        );

        tabOptions.addTab("tab4", expensesTab);

        kGradientPanel5.setkBorderRadius(30);

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
            public void keyPressed(java.awt.event.KeyEvent evt) {
                addStockKeyPressed(evt);
            }
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
            public void keyPressed(java.awt.event.KeyEvent evt) {
                updatePriceKeyPressed(evt);
            }
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

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("Update Stock/Price");
        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N

        jLabel26.setText("Name :");
        jLabel26.setFont(new java.awt.Font("Times New Roman", 1, 20)); // NOI18N

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
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
            public void keyPressed(java.awt.event.KeyEvent evt) {
                newStockKeyPressed(evt);
            }
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
            public void keyPressed(java.awt.event.KeyEvent evt) {
                newPriceKeyPressed(evt);
            }
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
        formulationCombo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formulationComboKeyPressed(evt);
            }
        });

        jLabel38.setText("Formulation :");
        jLabel38.setFont(new java.awt.Font("Times New Roman", 1, 20)); // NOI18N

        javax.swing.GroupLayout kGradientPanel5Layout = new javax.swing.GroupLayout(kGradientPanel5);
        kGradientPanel5.setLayout(kGradientPanel5Layout);
        kGradientPanel5Layout.setHorizontalGroup(
            kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel5Layout.createSequentialGroup()
                .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(kGradientPanel5Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1))
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
                                    .addComponent(updatePrice, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))))
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
                            .addComponent(newName))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        kGradientPanel5Layout.setVerticalGroup(
            kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel5Layout.createSequentialGroup()
                .addGroup(kGradientPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kGradientPanel5Layout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addComponent(jLabel6))
                    .addGroup(kGradientPanel5Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jLabel24)
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
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        medsTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                medsTable1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(medsTable1);
        if (medsTable1.getColumnModel().getColumnCount() > 0) {
            medsTable1.getColumnModel().getColumn(0).setResizable(false);
            medsTable1.getColumnModel().getColumn(0).setPreferredWidth(150);
            medsTable1.getColumnModel().getColumn(1).setResizable(false);
            medsTable1.getColumnModel().getColumn(2).setResizable(false);
            medsTable1.getColumnModel().getColumn(3).setResizable(false);
            medsTable1.getColumnModel().getColumn(3).setPreferredWidth(120);
        }

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
                .addContainerGap(145, Short.MAX_VALUE))
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

        tabOptions.addTab("tab5", inventoryTab);

        incomeLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        incomeLbl.setText("Income this month");
        incomeLbl.setFont(new java.awt.Font("Tahoma", 3, 36)); // NOI18N

        incomeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "Income"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane11.setViewportView(incomeTable);
        if (incomeTable.getColumnModel().getColumnCount() > 0) {
            incomeTable.getColumnModel().getColumn(0).setResizable(false);
            incomeTable.getColumnModel().getColumn(1).setResizable(false);
        }

        kGradientPanel6.setkBorderRadius(30);
        kGradientPanel6.setkGradientFocus(800);

        setIncomeLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        setIncomeLbl.setText("Income amount");
        setIncomeLbl.setFont(new java.awt.Font("Segoe UI", 2, 48)); // NOI18N
        setIncomeLbl.setForeground(new java.awt.Color(255, 51, 51));
        setIncomeLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setIncomeLblMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setIncomeLblMouseExited(evt);
            }
        });

        javax.swing.GroupLayout kGradientPanel6Layout = new javax.swing.GroupLayout(kGradientPanel6);
        kGradientPanel6.setLayout(kGradientPanel6Layout);
        kGradientPanel6Layout.setHorizontalGroup(
            kGradientPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(setIncomeLbl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE)
        );
        kGradientPanel6Layout.setVerticalGroup(
            kGradientPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(setIncomeLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout incomeTabLayout = new javax.swing.GroupLayout(incomeTab);
        incomeTab.setLayout(incomeTabLayout);
        incomeTabLayout.setHorizontalGroup(
            incomeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(incomeTabLayout.createSequentialGroup()
                .addGroup(incomeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(incomeTabLayout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(incomeLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 872, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(incomeTabLayout.createSequentialGroup()
                        .addGap(124, 124, 124)
                        .addComponent(kGradientPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(incomeTabLayout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 741, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(116, Short.MAX_VALUE))
        );
        incomeTabLayout.setVerticalGroup(
            incomeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(incomeTabLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(incomeLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(kGradientPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(75, Short.MAX_VALUE))
        );

        tabOptions.addTab("tab6", incomeTab);

        jPanel1.add(tabOptions, new org.netbeans.lib.awtextra.AbsoluteConstraints(-30, -32, 1020, 730));

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
        income.setOpaque(false);

        home.setBackground(clickedColor);
        pos.setBackground(defaultColor);
        sales.setBackground(defaultColor);
        expenses.setBackground(defaultColor);
        inventory.setBackground(defaultColor);
        income.setBackground(defaultColor);

        tabOptions.setSelectedIndex(0);

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
        income.setOpaque(false);

        home.setBackground(defaultColor);
        pos.setBackground(clickedColor);
        sales.setBackground(defaultColor);
        expenses.setBackground(defaultColor);
        inventory.setBackground(defaultColor);
        income.setBackground(defaultColor);

        tabOptions.setSelectedIndex(1);
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
        income.setOpaque(false);

        home.setBackground(defaultColor);
        pos.setBackground(defaultColor);
        sales.setBackground(clickedColor);
        expenses.setBackground(defaultColor);
        inventory.setBackground(defaultColor);
        income.setBackground(defaultColor);

        tabOptions.setSelectedIndex(2);

        //table needs to be set to 0 so that only the content from the database will be displayed. it is accumulating additional data when not reset
        DefaultTableModel model = (DefaultTableModel) dailySalesTable.getModel();
        model.setRowCount(0);
        dailyWeeklyMonthlyTab.setSelectedIndex(0);
        todaySalesToDailyDB();
        dailyTable();
        dailyLineChart();
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
        income.setOpaque(false);

        home.setBackground(defaultColor);
        pos.setBackground(defaultColor);
        sales.setBackground(defaultColor);
        expenses.setBackground(clickedColor);
        inventory.setBackground(defaultColor);
        income.setBackground(defaultColor);

        tabOptions.setSelectedIndex(3);
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
        income.setOpaque(false);

        home.setBackground(defaultColor);
        pos.setBackground(defaultColor);
        sales.setBackground(defaultColor);
        expenses.setBackground(defaultColor);
        inventory.setBackground(clickedColor);
        income.setBackground(defaultColor);

        tabOptions.setSelectedIndex(4);
    }//GEN-LAST:event_inventoryMouseClicked

    private void inventoryMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inventoryMouseEntered

        invLbl.setForeground(Color.black);
    }//GEN-LAST:event_inventoryMouseEntered

    private void inventoryMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inventoryMouseExited

        invLbl.setForeground(Color.white);
    }//GEN-LAST:event_inventoryMouseExited

    private void buyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buyActionPerformed
        performBuyAction();
    }//GEN-LAST:event_buyActionPerformed
    //this is the buy button action, i separate it to add key bindings
    private void performBuyAction() {
        try {
            float pay = Float.parseFloat(payment.getText());
            float tot = Float.parseFloat(totalTxt.getText());
            float ch = Float.parseFloat(change.getText());

            int defaultOption = 1;
            if (pay < tot) {
                JOptionPane.showMessageDialog(this, "Not Enough Payment");
            } else {
                String[] options = {"Yes, let me see", "No, I don't", "Cancel Order"};
                int choice = JOptionPane.showOptionDialog(null, "Do you want to see the receipt?", "Question", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[defaultOption]);

                switch (choice) {
                    case JOptionPane.YES_OPTION:
                        buy();
                        showReceipt r = new showReceipt();
                        soldMedsToDB();
                        purchaseHistory();
                        clearWhenBuy();
                        DefaultTableModel order = (DefaultTableModel) orderTable.getModel();
                        r.setVisible(true);
                        r.showToReceipt(order, pay, ch, discountAmount);
                        order.setRowCount(0);
                        //dito lang sya ibabalik sa 0 kasi need pa ipasa value nya sa receipt
                        discountAmount = 0;
                        todaySalesToDailyDB();
                        dailySalesToWeeklyDB();
                        weeklySalesToMonthlyDB();
                        dashboardSalesCount();
                        break;

                    case JOptionPane.NO_OPTION:
                        buy();
                        soldMedsToDB();
                        purchaseHistory();
                        DefaultTableModel order1 = (DefaultTableModel) orderTable.getModel();
                        order1.setRowCount(0);
                        clearWhenBuy();
                        todaySalesToDailyDB();
                        dailySalesToWeeklyDB();
                        weeklySalesToMonthlyDB();
                        dashboardSalesCount();
                        break;

                    default:

                }
            }
            payment.setEditable(false);
        } catch (NumberFormatException | NullPointerException e) {
            JOptionPane.showMessageDialog(this, "Enter Valid Amount");
        }
    }

    private void medsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_medsTableMouseClicked
        discount.setSelected(false);
        payment.setText("");
        change.setText("");
        buy.setEnabled(false);
        payment.setEditable(true);
        int selectrow = medsTable.getSelectedRow();
        int stock = Integer.parseInt(medsTable.getValueAt(selectrow, 2).toString());

        if (stock <= 0) {
            JOptionPane.showMessageDialog(this, "No Stock!");
        } else {
            showInOrderTable();
            setTotal();
            payment.requestFocus();
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
            addStock.requestFocus();
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

                    table(SELECT_COMMAND);
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
        updateAction();
    }//GEN-LAST:event_updateBtnActionPerformed
    //for key bindings again
    private void updateAction() {
        try {
            int isSure = JOptionPane.showConfirmDialog(null, "Are you sure?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (isSure == JOptionPane.YES_OPTION) {
                try {
                    //para makuha yung price sa table
                    int selectrow = medsTable1.getSelectedRow();
                    int priceUpdate;

                    String medsName = currentName.getText();
                    int updateStock;

                    //para kapag stock lang mag dadagdag hindi sa price
                    if (addStock.getText().isEmpty()) {
                        updateStock = 0;
                    } else {
                        updateStock = Integer.parseInt(addStock.getText());
                        restockMedsExpenses(currentName.getText(), Integer.parseInt(addStock.getText()));
                        restockExpensesTable();
                    }

                    //para naman pag price lang uupdate pero yung stock hindi
                    if (this.updatePrice.getText().isEmpty()) {
                        priceUpdate = Integer.parseInt(medsTable1.getValueAt(selectrow, 1).toString());
                    } else {
                        priceUpdate = Integer.parseInt(this.updatePrice.getText());
                    }

                    pst = con.prepareStatement("UPDATE medicine SET stock = (stock + ?), price = ? WHERE meds_name = ?");
                    pst.setInt(1, updateStock);
                    pst.setInt(2, priceUpdate);
                    pst.setString(3, medsName);
                    pst.executeUpdate();

                    searchInventory.setText("");
                    currentName.setText("");
                    currentPrice.setText("");
                    currentStock.setText("");
                    addStock.setText("");
                    this.updatePrice.setText("");

                    table(SELECT_COMMAND);
                    outOfStock();
                    JOptionPane.showMessageDialog(null, "Stock and Price Updated, Check the expenses tab to pay!");
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
    }

    //to populate the restock medicine expenses table
    private void restockMedsExpenses(String name, int quan) {
        try {
            String status = "Unpaid";
            int defaultPrice = 0;

            pst = con.prepareStatement("INSERT INTO restock_expenses_history(meds_name, quantity, price, date, status) VALUES(?,?,?,?,?)");
            pst.setString(1, name);
            pst.setInt(2, quan);
            pst.setFloat(3, defaultPrice);
            pst.setString(4, null);
            pst.setString(5, status);
            pst.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(pharmacy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NumberFormatException | NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "Unexpected Error!");
        }
    }

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
        if (addStock.getText().isEmpty() || currentName.getText().isEmpty() || addStock.getText().matches(".*[a-zA-Z].*")) {
            updateBtn.setEnabled(false);
        } else {
            updateBtn.setEnabled(true);
        }
    }//GEN-LAST:event_addStockKeyReleased

    private void addNewMedBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewMedBtnActionPerformed
        addMedAction();
    }//GEN-LAST:event_addNewMedBtnActionPerformed
    //for key binding
    private void addMedAction() {
        try {
            int isSure = JOptionPane.showConfirmDialog(null, "Are you sure?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (isSure == JOptionPane.YES_OPTION) {
                try {
                    //para iconvert ang 1st letter ng medicine to uppercase
                    String defaultName = newName.getText();
                    String medsName = Character.toUpperCase(defaultName.charAt(0)) + defaultName.substring(1);

                    int medsStock = Integer.parseInt(newStock.getText());
                    int medPrice = Integer.parseInt(newPrice.getText());
                    String formulation = formulationCombo.getSelectedItem().toString();

                    pst = con.prepareStatement("INSERT INTO medicine(meds_name,stock,price,formulation) VALUES(?,?,?,?)");
                    pst.setString(1, medsName);
                    pst.setInt(2, medsStock);
                    pst.setInt(3, medPrice);
                    pst.setString(4, formulation);
                    pst.executeUpdate();

                    table(SELECT_COMMAND);
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
    }
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
        if (updatePrice.getText().isEmpty() || currentName.getText().isEmpty() || updatePrice.getText().matches(".*[a-zA-Z].*")) {
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

    private void dashboardSaleLblMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardSaleLblMouseEntered

    }//GEN-LAST:event_dashboardSaleLblMouseEntered

    private void dashboardSaleLblMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardSaleLblMouseExited

    }//GEN-LAST:event_dashboardSaleLblMouseExited

    private void dashboardSalesPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardSalesPanelMouseEntered
        dashboardSalesLbl.setFont(new Font("Segoe UI Black", Font.BOLD, 30));
        dashboardSalesLbl.setForeground(Color.red);
    }//GEN-LAST:event_dashboardSalesPanelMouseEntered

    private void dashboardSalesPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardSalesPanelMouseExited
        dashboardSalesLbl.setFont(fontDefault);
        dashboardSalesLbl.setForeground(Color.black);
    }//GEN-LAST:event_dashboardSalesPanelMouseExited

    private void dashboardIncomePanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardIncomePanelMouseEntered
        dashboardSaleLbl.setFont(new Font("Segoe UI Black", Font.BOLD, 30));
        dashboardSaleLbl.setForeground(Color.red);
    }//GEN-LAST:event_dashboardIncomePanelMouseEntered

    private void dashboardIncomePanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardIncomePanelMouseExited
        dashboardSaleLbl.setFont(fontDefault);
        dashboardSaleLbl.setForeground(Color.black);
    }//GEN-LAST:event_dashboardIncomePanelMouseExited

    private void paymentKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_paymentKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            performBuyAction();
        }
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
        } catch (NumberFormatException e) {
        }

        //checks if payment has character
        if (payment.getText().matches(".*[a-zA-Z].*") || payment.getText().contains(" ")) {
            discount.setEnabled(false);
            buy.setEnabled(false);
        } else if (payment.getText().isEmpty()) {
            change.setText("");
            buy.setEnabled(false);
        } else {
            discount.setEnabled(true);
        }

    }//GEN-LAST:event_paymentKeyReleased

    private void paymentKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_paymentKeyTyped
        //key binding
        if (evt.getKeyChar() == 'd' || evt.getKeyChar() == 'D') {
            if (discount.isSelected()) {
                discount.setSelected(false);
            } else {
                discount.setSelected(true);
            }
            evt.consume(); // Consume the event to prevent further processing
        } else {
            discount.setSelected(false);
        }
    }//GEN-LAST:event_paymentKeyTyped

    private void orderTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_orderTableMouseClicked
        discount.setSelected(false);
        payment.setText("");
        change.setText("");
        buy.setEnabled(false);
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
        payment.requestFocus();

        //used ternary to enable the payment if table is not empty
        payment.setEditable((model.getRowCount() != 0));

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
        dailyWeeklyMonthlyTab.setSelectedIndex(2);
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
        dailyWeeklyMonthlyTab.setSelectedIndex(1);
    }//GEN-LAST:event_nextDailyMouseClicked

    private void prevWeeklyMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevWeeklyMouseExited
        prevWeekly.setForeground(Color.black);
    }//GEN-LAST:event_prevWeeklyMouseExited

    private void prevWeeklyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevWeeklyMouseClicked
        dailyWeeklyMonthlyTab.setSelectedIndex(0);
    }//GEN-LAST:event_prevWeeklyMouseClicked

    private void nextWeeklyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextWeeklyMouseClicked
        dailyWeeklyMonthlyTab.setSelectedIndex(2);
    }//GEN-LAST:event_nextWeeklyMouseClicked

    private void prevMonthMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevMonthMouseEntered
        prevMonth.setForeground(Color.red);
    }//GEN-LAST:event_prevMonthMouseEntered

    private void prevMonthMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevMonthMouseExited
        prevMonth.setForeground(Color.black);
    }//GEN-LAST:event_prevMonthMouseExited

    private void prevMonthMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prevMonthMouseClicked
        dailyWeeklyMonthlyTab.setSelectedIndex(1);
    }//GEN-LAST:event_prevMonthMouseClicked

    private void nextMonthMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextMonthMouseClicked
        dailyWeeklyMonthlyTab.setSelectedIndex(0);
    }//GEN-LAST:event_nextMonthMouseClicked

    private void incomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_incomeMouseClicked
        home.setOpaque(false);
        pos.setOpaque(false);
        sales.setOpaque(false);
        expenses.setOpaque(false);
        inventory.setOpaque(false);
        income.setOpaque(true);

        home.setBackground(defaultColor);
        pos.setBackground(defaultColor);
        sales.setBackground(defaultColor);
        expenses.setBackground(defaultColor);
        inventory.setBackground(defaultColor);
        income.setBackground(clickedColor);

        dashboardSalesCount();
        incomeToDB();
        incomeTable();
        showIncome();
        tabOptions.setSelectedIndex(5);
    }//GEN-LAST:event_incomeMouseClicked

    private void incomeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_incomeMouseEntered
        incomeLabel.setForeground(Color.black);
    }//GEN-LAST:event_incomeMouseEntered

    private void incomeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_incomeMouseExited
        incomeLabel.setForeground(Color.white);
    }//GEN-LAST:event_incomeMouseExited

    private void discountItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_discountItemStateChanged
        calculateDiscount();
    }//GEN-LAST:event_discountItemStateChanged

    private void dailyWeeklyMonthlyTabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dailyWeeklyMonthlyTabMouseClicked
        DefaultTableModel model = (DefaultTableModel) weeklySalesTable.getModel();
        model.setRowCount(0);

        dailySalesToWeeklyDB();
        weeklyTable();
        weeklyPieChart();

        weeklySalesToMonthlyDB();
        monthlyBarChart();
    }//GEN-LAST:event_dailyWeeklyMonthlyTabMouseClicked

    private void discountKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_discountKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            performBuyAction();
        }
    }//GEN-LAST:event_discountKeyPressed

    private void addStockKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_addStockKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            updateAction();
        }
    }//GEN-LAST:event_addStockKeyPressed

    private void updatePriceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_updatePriceKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            updateAction();
        }
    }//GEN-LAST:event_updatePriceKeyPressed

    private void newStockKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_newStockKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            addMedAction();
        }
    }//GEN-LAST:event_newStockKeyPressed

    private void formulationComboKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formulationComboKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            addMedAction();
        }
    }//GEN-LAST:event_formulationComboKeyPressed

    private void newPriceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_newPriceKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            addMedAction();
        }
    }//GEN-LAST:event_newPriceKeyPressed

    private void employeeProceedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_employeeProceedActionPerformed
        employeeSalary();
    }//GEN-LAST:event_employeeProceedActionPerformed
    //for employee salary
    private void employeeSalary() {
        try {
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            String date = today.format(formatter);

            String employee = employeeCombo.getSelectedItem().toString();
            int salary = Integer.parseInt(employeeSalaryTxt.getText());

            //get the last person who logged in
            String payer = "";
            pst = con.prepareStatement("SELECT username FROM login_history");
            rs = pst.executeQuery();
            if (rs.last()) {
                payer = rs.getString(1);
            }
            //to convert the date of table to month and year only
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            DateTimeFormatter tableDateFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");

            //check if the employee is already paid this month
            boolean isAlreadyPaid = false;
            DefaultTableModel model = (DefaultTableModel) employeeHistoryTable.getModel();
            int rowCount = model.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                LocalDate convertDate = LocalDate.parse(model.getValueAt(i, 2).toString(), inputFormatter);
                String formattedDate = tableDateFormatter.format(convertDate);
                if (model.getValueAt(i, 0).toString().equals(employee) && formattedDate.equals(date)) {
                    isAlreadyPaid = true;
                }
            }
            if (isAlreadyPaid) {
                int response = JOptionPane.showConfirmDialog(null, "This employee has already been paid this month. Do you want to pay again?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    pst = con.prepareStatement("INSERT INTO employee_salary_history(employee_name, salary, date, payer_name) VALUES(?,?,?,?)");
                    pst.setString(1, employee);
                    pst.setInt(2, salary);
                    pst.setString(3, date);
                    pst.setString(4, payer);
                    pst.executeUpdate();

                    employeeSalaryTxt.setText("");
                    employeeProceed.setEnabled(false);
                    employeeSalaryHistory();
                }
            } else {
                int response = JOptionPane.showConfirmDialog(null, "Do you want to proceed?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    pst = con.prepareStatement("INSERT INTO employee_salary_history(employee_name, salary, date, payer_name) VALUES(?,?,?,?)");
                    pst.setString(1, employee);
                    pst.setInt(2, salary);
                    pst.setString(3, date);
                    pst.setString(4, payer);
                    pst.executeUpdate();

                    employeeSalaryTxt.setText("");
                    employeeProceed.setEnabled(false);
                    employeeSalaryHistory();
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } catch (NumberFormatException | NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "Unexpected Error!");
        }
    }

    private void employeeSalaryHistory() {
        try {
            DefaultTableModel model = (DefaultTableModel) employeeHistoryTable.getModel();
            model.setRowCount(0);
            pst = con.prepareStatement("SELECT employee_name, salary, date, payer_name FROM employee_salary_history ORDER BY ID DESC");
            rs = pst.executeQuery();
            while (rs.next()) {
                String employeeName = rs.getString(1);
                int salary = rs.getInt(2);
                String date = rs.getString(3);
                String payerName = rs.getString(4);

                model.addRow(new Object[]{employeeName, salary, date, payerName});
            }

        } catch (SQLException | NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "Unexpected Error!");
        }
    }

    private void employeeSalaryTxtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_employeeSalaryTxtKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            employeeSalary();
        }
    }//GEN-LAST:event_employeeSalaryTxtKeyPressed

    private void employeeSalaryTxtKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_employeeSalaryTxtKeyReleased
        if (employeeSalaryTxt.getText().matches(".*[a-zA-Z].*") || employeeSalaryTxt.getText().isEmpty()) {
            employeeProceed.setEnabled(false);
        } else {
            employeeProceed.setEnabled(true);
        }
    }//GEN-LAST:event_employeeSalaryTxtKeyReleased

    private void employeeProceedMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_employeeProceedMouseEntered
        employeeProceed.setBackground(Color.red);
        employeeProceed.setForeground(Color.white);
    }//GEN-LAST:event_employeeProceedMouseEntered

    private void employeeProceedMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_employeeProceedMouseExited
        employeeProceed.setBackground(Color.white);
        employeeProceed.setForeground(Color.black);
    }//GEN-LAST:event_employeeProceedMouseExited

    private void otherExpensesAmountTxtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_otherExpensesAmountTxtKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            otherExpenses();
        }
    }//GEN-LAST:event_otherExpensesAmountTxtKeyPressed

    private void otherExpensesAmountTxtKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_otherExpensesAmountTxtKeyReleased
        if (otherExpensesAmountTxt.getText().matches(".*[a-zA-Z].*") || otherExpensesAmountTxt.getText().isEmpty()) {
            otherExpensesProceedBtn.setEnabled(false);
        } else {
            otherExpensesProceedBtn.setEnabled(true);
        }
    }//GEN-LAST:event_otherExpensesAmountTxtKeyReleased

    private void otherExpensesProceedBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_otherExpensesProceedBtnMouseEntered
        otherExpensesProceedBtn.setBackground(Color.red);
        otherExpensesProceedBtn.setForeground(Color.white);
    }//GEN-LAST:event_otherExpensesProceedBtnMouseEntered

    private void otherExpensesProceedBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_otherExpensesProceedBtnMouseExited
        otherExpensesProceedBtn.setBackground(Color.white);
        otherExpensesProceedBtn.setForeground(Color.black);
    }//GEN-LAST:event_otherExpensesProceedBtnMouseExited

    private void otherExpensesProceedBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_otherExpensesProceedBtnActionPerformed
        otherExpenses();
    }//GEN-LAST:event_otherExpensesProceedBtnActionPerformed
    //for other expenses. (ex. wifi, water bills, etc)
    private void otherExpenses() {
        try {
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
            String date = today.format(formatter);

            DateTimeFormatter tableDateFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");

            String defaultName = otherExpensesNameTxt.getText();
            String name = Character.toUpperCase(defaultName.charAt(0)) + defaultName.substring(1);
            int amount = Integer.parseInt(otherExpensesAmountTxt.getText());

            boolean isAlreadyPaid = false;
            DefaultTableModel model = (DefaultTableModel) otherExpensesTable.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                LocalDate convertDate = LocalDate.parse(model.getValueAt(i, 2).toString(), inputFormatter);
                String formattedDate = tableDateFormatter.format(convertDate);
                if (model.getValueAt(i, 0).toString().equals(name) && formattedDate.equals(date)) {
                    isAlreadyPaid = true;
                }
            }
            if (isAlreadyPaid) {
                int response = JOptionPane.showConfirmDialog(null, "This item has already been paid this month. Do you want pay again?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    pst = con.prepareStatement("INSERT INTO other_expenses_history(name, amount, date) VALUES(?,?,?)");
                    pst.setString(1, name);
                    pst.setInt(2, amount);
                    pst.setString(3, date);
                    pst.executeUpdate();

                    otherExpensesAmountTxt.setText("");
                    otherExpensesNameTxt.setText("");
                    otherExpensesProceedBtn.setEnabled(false);
                    otherExpensesTable();
                }
            } else {
                int response = JOptionPane.showConfirmDialog(null, "Do you want to proceed?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    pst = con.prepareStatement("INSERT INTO other_expenses_history(name, amount, date) VALUES(?,?,?)");
                    pst.setString(1, name);
                    pst.setInt(2, amount);
                    pst.setString(3, date);
                    pst.executeUpdate();

                    otherExpensesAmountTxt.setText("");
                    otherExpensesNameTxt.setText("");
                    otherExpensesProceedBtn.setEnabled(false);
                    otherExpensesTable();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(pharmacy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NumberFormatException | NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "Unexpected Error!");
        }
    }

    private void otherExpensesTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) otherExpensesTable.getModel();
            model.setRowCount(0);
            pst = con.prepareStatement("SELECT name, amount, date FROM other_expenses_history ORDER BY ID DESC");
            rs = pst.executeQuery();
            while (rs.next()) {
                String name = rs.getString(1);
                int amount = rs.getInt(2);
                String date = rs.getString(3);

                model.addRow(new Object[]{name, amount, date});
            }

        } catch (SQLException | NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "Unexpected Error!");
        }
    }

    private void otherExpensesNameTxtKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_otherExpensesNameTxtKeyReleased
        if (otherExpensesNameTxt.getText().isEmpty()) {
            otherExpensesProceedBtn.setEnabled(false);
        } else {
            otherExpensesProceedBtn.setEnabled(true);
        }

    }//GEN-LAST:event_otherExpensesNameTxtKeyReleased

    private void restockMedsAmountTxtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_restockMedsAmountTxtKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            payRestockExpenses();
        }
    }//GEN-LAST:event_restockMedsAmountTxtKeyPressed

    private void restockMedsAmountTxtKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_restockMedsAmountTxtKeyReleased
        if (restockMedsAmountTxt.getText().matches(".*[a-zA-Z].*") || restockMedsAmountTxt.getText().isEmpty()) {
            restockMedsProcessBtn.setEnabled(false);
        } else {
            restockMedsProcessBtn.setEnabled(true);
        }
    }//GEN-LAST:event_restockMedsAmountTxtKeyReleased

    private void restockMedsProcessBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_restockMedsProcessBtnMouseEntered
        restockMedsProcessBtn.setBackground(Color.red);
        restockMedsProcessBtn.setForeground(Color.white);
    }//GEN-LAST:event_restockMedsProcessBtnMouseEntered

    private void restockMedsProcessBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_restockMedsProcessBtnMouseExited
        restockMedsProcessBtn.setBackground(Color.white);
        restockMedsProcessBtn.setForeground(Color.black);
    }//GEN-LAST:event_restockMedsProcessBtnMouseExited

    private void restockMedsProcessBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restockMedsProcessBtnActionPerformed
        payRestockExpenses();
    }//GEN-LAST:event_restockMedsProcessBtnActionPerformed
    //for restock medicine expenses table
    private void restockExpensesTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) restockExpensesTbl.getModel();
            model.setRowCount(0);
            pst = con.prepareStatement("SELECT meds_name, quantity, price, date, status FROM restock_expenses_history ORDER BY ID DESC");
            rs = pst.executeQuery();
            while (rs.next()) {
                String name = rs.getString(1);
                int quan = rs.getInt(2);
                float price = rs.getFloat(3);
                String date = rs.getString(4);
                String status = rs.getString(5);
                model.addRow(new Object[]{name, quan, price, date, status});
            }

        } catch (SQLException | NullPointerException ex) {
            //JOptionPane.showMessageDialog(this, "Unexpected Error!");
        }
    }

    private void payRestockExpenses() {
        try {
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            String date = today.format(formatter);

            String status = "Paid";
            String name = restockMedsNameTxt.getText();
            float price = Float.parseFloat(restockMedsAmountTxt.getText());

            int response = JOptionPane.showConfirmDialog(null, "Do you want to proceed?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                pst = con.prepareStatement("UPDATE restock_expenses_history SET price = ?, date = ?, status = ? WHERE meds_name = ?");
                pst.setFloat(1, price);
                pst.setString(2, date);
                pst.setString(3, status);
                pst.setString(4, name);
                pst.executeUpdate();

                restockMedsAmountTxt.setText("");
                restockMedsNameTxt.setText("");
                restockMedsProcessBtn.setEnabled(false);
                restockMedsQuanTxt.setText("");
                restockExpensesTable();
            }

        } catch (SQLException ex) {
            Logger.getLogger(pharmacy.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void restockExpensesTblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_restockExpensesTblMouseClicked
        try {
            int selectedRow = restockExpensesTbl.getSelectedRow();
            String status = restockExpensesTbl.getValueAt(selectedRow, 4).toString();

            if (status.equals("Paid")) {
                JOptionPane.showMessageDialog(this, "This item has already been paid!");
            } else {
                String name = restockExpensesTbl.getValueAt(selectedRow, 0).toString();
                String quan = restockExpensesTbl.getValueAt(selectedRow, 1).toString();

                restockMedsNameTxt.setText(name);
                restockMedsQuanTxt.setText(quan);
                restockMedsAmountTxt.setEditable(true);
                restockMedsAmountTxt.requestFocus();
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(this, "You need to be the owner to access this!");
        }
    }//GEN-LAST:event_restockExpensesTblMouseClicked

    private void setIncomeLblMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_setIncomeLblMouseEntered
        setIncomeLbl.setFont(new Font("Segoe UI", Font.CENTER_BASELINE, 55));
    }//GEN-LAST:event_setIncomeLblMouseEntered

    private void setIncomeLblMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_setIncomeLblMouseExited
        setIncomeLbl.setFont(new Font("Segoe UI", Font.ITALIC, 48));
    }//GEN-LAST:event_setIncomeLblMouseExited

    private void pinCodeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pinCodeMouseClicked
        if (pinCode.isEnabled()) {
            JOptionPane.showMessageDialog(null, "Pin Code: 12345 \nYou can use this PIN code when creating an account.");
        }
    }//GEN-LAST:event_pinCodeMouseClicked

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
    private javax.swing.JPanel barChartPanelMonthly;
    private javax.swing.JButton buy;
    private com.github.lgooddatepicker.components.CalendarPanel calendarPanel1;
    private javax.swing.JTextField change;
    private javax.swing.JLabel clear;
    private javax.swing.JTextField currentName;
    private javax.swing.JTextField currentPrice;
    private javax.swing.JTextField currentStock;
    private javax.swing.JPanel dailyChart;
    private javax.swing.JTable dailySalesTable;
    private javax.swing.JTabbedPane dailyWeeklyMonthlyTab;
    private com.k33ptoo.components.KGradientPanel dashboardIncomePanel;
    private javax.swing.JLabel dashboardOrderLbl;
    private com.k33ptoo.components.KGradientPanel dashboardOrderPanel;
    private javax.swing.JLabel dashboardSaleLbl;
    private javax.swing.JLabel dashboardSalesLbl;
    private com.k33ptoo.components.KGradientPanel dashboardSalesPanel;
    private javax.swing.JCheckBox discount;
    private javax.swing.JComboBox<String> employeeCombo;
    private javax.swing.JTable employeeHistoryTable;
    private javax.swing.JButton employeeProceed;
    private javax.swing.JTextField employeeSalaryTxt;
    private javax.swing.JLabel expLbl;
    private javax.swing.JPanel expenses;
    private javax.swing.JPanel expensesTab;
    private javax.swing.JComboBox<String> formulationCombo;
    private javax.swing.JPanel home;
    private javax.swing.JLabel homeLbl;
    private javax.swing.JPanel homeTab;
    private javax.swing.JPanel income;
    private javax.swing.JLabel incomeLabel;
    private javax.swing.JLabel incomeLbl;
    private javax.swing.JPanel incomeTab;
    private javax.swing.JTable incomeTable;
    private javax.swing.JLabel invLbl;
    private javax.swing.JPanel inventory;
    private javax.swing.JPanel inventoryTab;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
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
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private com.k33ptoo.components.KGradientPanel kGradientPanel1;
    private com.k33ptoo.components.KGradientPanel kGradientPanel2;
    private com.k33ptoo.components.KGradientPanel kGradientPanel3;
    private com.k33ptoo.components.KGradientPanel kGradientPanel4;
    private com.k33ptoo.components.KGradientPanel kGradientPanel5;
    private com.k33ptoo.components.KGradientPanel kGradientPanel6;
    private javax.swing.JPanel lineChartPanelDaily;
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
    private javax.swing.JLabel noteLbl;
    private javax.swing.JLabel noteLbl1;
    private javax.swing.JLabel noteLbl2;
    private javax.swing.JTable orderTable;
    private javax.swing.JTextField otherExpensesAmountTxt;
    private javax.swing.JTextField otherExpensesNameTxt;
    private javax.swing.JButton otherExpensesProceedBtn;
    private javax.swing.JTable otherExpensesTable;
    private javax.swing.JTable outOfStockTable;
    private javax.swing.JTextField payment;
    private javax.swing.JPanel pieChartPanelWeekly;
    private javax.swing.JLabel pinCode;
    private javax.swing.JPanel pos;
    private javax.swing.JLabel posLbl;
    private javax.swing.JPanel posTab;
    private javax.swing.JLabel prevDaily;
    private javax.swing.JLabel prevMonth;
    private javax.swing.JLabel prevWeekly;
    private javax.swing.JTable purchaseHistoryTable;
    private javax.swing.JTable restockExpensesTbl;
    private javax.swing.JTextField restockMedsAmountTxt;
    private javax.swing.JTextField restockMedsNameTxt;
    private javax.swing.JButton restockMedsProcessBtn;
    private javax.swing.JTextField restockMedsQuanTxt;
    private javax.swing.JPanel sales;
    private javax.swing.JLabel salesLbl;
    private javax.swing.JPanel salesTab;
    private javax.swing.JTextField search;
    private javax.swing.JTextField searchInventory;
    private javax.swing.JLabel setIncomeLbl;
    private javax.swing.JTabbedPane tabOptions;
    private javax.swing.JTextField totalTxt;
    private javax.swing.JButton updateBtn;
    private javax.swing.JTextField updatePrice;
    private javax.swing.JPanel weeklyChart;
    private javax.swing.JLabel weeklyIncomeChartLbl;
    private javax.swing.JTable weeklySalesTable;
    // End of variables declaration//GEN-END:variables

}
