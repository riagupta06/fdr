import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewFd extends JFrame implements ActionListener {

    private final String dummyValue = "-select-";
    private final String allValue = "ALL";
    private final Font font = new Font("Times New Roman", Font.BOLD, 18);
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    private final String cols[] = {"Created On","FDR No","Period","Rate %","Amount","Mt. Date","Mt. Value","Opening bal",
            "Int-Q1","TDS-Q1","Int-Q2","TDS-Q2","Int-Q3","TDS-Q3","Int-Q4","TDS-Q4","Total Int","Total TDS", "Closing bal", "Remarks"};
    private final Statement statement;

    JLabel memberNameLabel = new JLabel("Member");
    JLabel bankNameLabel = new JLabel("Bank");
    JComboBox membersList;
    JComboBox banksList;
    JLabel financialYearLabel = new JLabel("Financial Year");
    JComboBox financialYearsList;
    JLabel tdsLabel = new JLabel("TDS %");
    JTextField tds = new JTextField(15);

    JButton viewFdButton = new JButton("View FDs");
    JButton clearButton = new JButton("CLEAR");
    JButton saveTdsButton = new JButton("SAVE TDS");
    JButton carryForwardButton = new JButton("SAVE TDS AND CARRY FORWARD");

    java.util.List<String> members = new ArrayList<>();
    java.util.List<String> banks = new ArrayList<>();
    java.util.List<String> financialYears = new ArrayList<>();
    JScrollPane sp;

    JLabel tdsValueLabel = new JLabel();
    JLabel customerIdLabel = new JLabel();

    public ViewFd(Statement statement) {
        this.statement = statement;
        members.add(dummyValue);
        banks.add(dummyValue);
        autoPopulateMembers(members);
        autoPopulateBanks(banks);
        autoPopulateYears(financialYears);
        viewPage();
    }

    private void autoPopulateYears(List<String> financialYears) {
        financialYears.add(dummyValue);
        financialYears.add("2016-2017");
        financialYears.add("2017-2018");
        financialYears.add("2018-2019");
        financialYears.add("2019-2020");
        financialYears.add("2020-2021");
        financialYears.add("2021-2022");
        financialYears.add("2022-2023");
        financialYears.add("2023-2024");
        financialYears.add("2024-2025");
        financialYears.add("2025-2026");
        financialYears.add("2026-2027");
        financialYears.add("2027-2028");
        financialYears.add("2028-2029");
        financialYears.add("2029-2030");
        financialYears.add("2030-2031");
        financialYears.add("2031-2032");
    }

    private void viewPage() {
        getContentPane();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("FD RECORD");
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(null);

        memberNameLabel.setBounds(90, 70, 200, 25);
        memberNameLabel.setFont(font);
        add(memberNameLabel);
        membersList = new JComboBox(members.toArray());
        membersList.setBounds(340, 70, 200, 25);
        add(membersList);

        bankNameLabel.setBounds(90, 95, 200, 25);
        bankNameLabel.setFont(font);
        add(bankNameLabel);
        banksList = new JComboBox(banks.toArray());
        banksList.setBounds(340, 95, 200, 25);
        add(banksList);

        financialYearLabel.setBounds(900, 70, 150, 25);
        financialYearLabel.setFont(font);
        add(financialYearLabel);
        financialYearsList = new JComboBox(financialYears.toArray());
        financialYearsList.setBounds(1050, 70, 150, 25);
        add(financialYearsList);

        tdsLabel.setBounds(900, 95, 150, 25);
        tdsLabel.setFont(font);
        add(tdsLabel);
        tds.setBounds(1050, 95, 150, 25);
        add(tds);

        viewFdButton.setBounds(540, 100, 100, 25);
        add(viewFdButton);
        viewFdButton.addActionListener(this);

        clearButton.setBounds(540, 125, 100, 25);
        add(clearButton);
        clearButton.addActionListener(this);

        tdsValueLabel.setBounds(1200, 100, 400, 25);
        add(tdsValueLabel);
        customerIdLabel.setBounds(0, 0, 400, 25);
        add(customerIdLabel);

        carryForwardButton.setBounds(1200, 125, 200, 25);
        add(carryForwardButton);
        carryForwardButton.addActionListener(this);

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == viewFdButton) {
            validateInput();
            getAndSetCustomerId();
            getAndSetSavedTdsValue();
            String tdsValue = tds.getText();
            if(tds.getText() == null || tds.getText().isEmpty()) {
                int reply = JOptionPane.showConfirmDialog(null, "Do you want to see fds with 0 tds??");
                if (reply == JOptionPane.YES_OPTION) {
                    tdsValue = "0";
                    tds.setText("0");
                } else if (reply == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            try {
                createFDsTable(tdsValue);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (event.getSource() == clearButton) {
            clearDetails();
        } else if(event.getSource() == carryForwardButton) {
            validateInput();
            saveTds();
            try {
                carryForwardFds();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void getAndSetCustomerId() {
        String bank = (String) banksList.getItemAt(banksList.getSelectedIndex());
        String member = (String) membersList.getItemAt(membersList.getSelectedIndex());
        if(!allValue.equals(bank)) {
            String getSql = "SELECT * FROM CustId WHERE Member = '%s' AND Bank = '%s'";
            try {
                String getSqlValue = String.format(getSql, member, bank);
                ResultSet resultSet = statement.executeQuery(getSqlValue);
                if(resultSet.next()) {
                    String value = resultSet.getString("CustomerId");
                    customerIdLabel.setText("Customer Id " + value);
                }
            } catch (SQLException throwables) {
                JOptionPane.showMessageDialog(null, "Error fetching customer id" + throwables.toString(), "ERROR", 2);
            }
        }
    }

    private void getAndSetSavedTdsValue() {
        String bank = (String) banksList.getItemAt(banksList.getSelectedIndex());
        String member = (String) membersList.getItemAt(membersList.getSelectedIndex());
        String financialYearValue = (String) financialYearsList.getItemAt(financialYearsList.getSelectedIndex());
        if(!allValue.equals(bank)) {
            String getSql = "SELECT * FROM TDS WHERE Member = '%s' AND Bank = '%s' AND FinancialYear = '%s'";
            try {
                String getSqlValue = String.format(getSql, member, bank, financialYearValue);
                ResultSet resultSet = statement.executeQuery(getSqlValue);
                if(resultSet.next()) {
                    String value = resultSet.getString("Tds");
                    //tdsValueLabel.setText("Saved tds value is " + value);
                    tds.setText(value);
                }
            } catch (SQLException throwables) {
                JOptionPane.showMessageDialog(null, "Error fetching saved tds value" + throwables.toString(), "ERROR", 2);
            }
        }
    }

    private void carryForwardFds() throws ParseException {
        List<FD> fds = getRequiredFds();
        String tdsValue = tds.getText();
        if(tds.getText() == null || tds.getText().isEmpty()) {
            int reply = JOptionPane.showConfirmDialog(null, "Do you want to see fds with 0 tds??");
            if (reply == JOptionPane.YES_OPTION) {
                tdsValue = "0";
                tds.setText("0");
            } else if (reply == JOptionPane.NO_OPTION) {
                return;
            }
        }
        int reply = JOptionPane.showConfirmDialog(null, "Do you want to confirm and save??");
        if (reply == JOptionPane.YES_OPTION) {
            int size = fds.size();
            for(int i=0; i<size; i++) {
                FD fd = fds.get(i);
                fd.setTdsValue(tdsValue);
                fd.getQ1Interest();
                fd.getQ2Interest();
                fd.getQ3Interest();
                fd.getQ4Interest();
                String financialYear = fd.getFinancialYear();
                String financialYear1 = financialYear.substring(0, financialYear.indexOf("-"));
                String financialYear2 = financialYear.substring(financialYear.indexOf("-") + 1);
                financialYear = (Integer.parseInt(financialYear1) + 1) + "-" + (Integer.parseInt(financialYear2) + 1);
                String getSql = "SELECT * FROM YearlyFd WHERE Fd = '%s' AND financialYear = '%s'";
                String insertSql = "INSERT INTO YearlyFd (Fd, financialYear, opening) VALUES('%s','%s','%s')";
                String updateSql = "UPDATE YearlyFd SET opening = '%s' WHERE Fd = '%s' AND financialYear = '%s'";
                String getSqlValue = String.format(getSql, fd.getFdNo(), financialYear);
                String insertSqlValue = String.format(insertSql, fd.getFdNo(), financialYear, fd.getClosing());
                String updateSqlValue = String.format(updateSql, fd.getClosing(), fd.getFdNo(), financialYear);
                String updateClosingOld = "UPDATE YearlyFd "
                        + "set closing = '" + fd.getClosing() + "' "
                        + "where Fd = '" + fd.getFdNo() + "' and financialYear = '" + fd.getFinancialYear() + "'";
                try {
                    ResultSet resultSet = statement.executeQuery(getSqlValue);
                    if (resultSet.next()) {
                        statement.executeUpdate(updateSqlValue);
                    } else {
                        statement.execute(insertSqlValue);
                    }
                    statement.executeUpdate(updateClosingOld);
                } catch (SQLException throwable) {
                    JOptionPane.showMessageDialog(null, "Error updating records" + throwable.toString(), "ERROR", JOptionPane.WARNING_MESSAGE);
                }
            }
        } else if (reply == JOptionPane.NO_OPTION) {
            return;
        }
    }

    private void saveTds() {
        String bank = (String) banksList.getItemAt(banksList.getSelectedIndex());
        String member = (String) membersList.getItemAt(membersList.getSelectedIndex());
        String financialYearValue = (String) financialYearsList.getItemAt(financialYearsList.getSelectedIndex());
        String tdsValue = tds.getText();
        if(!allValue.equals(bank)) {
            if(tdsValue == null || tdsValue.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Enter TDS value!!!", "Note", JOptionPane.WARNING_MESSAGE);
                tds.requestFocus();
                return;
            }
            String getSql = "SELECT * FROM TDS WHERE Member = '%s' AND Bank = '%s' AND FinancialYear = '%s'";
            String insertSql = "INSERT INTO TDS (Member, Bank, FinancialYear, Tds) VALUES('%s','%s','%s','%s')";
            String updateSql = "UPDATE TDS SET Tds = '%s' WHERE Member = '%s' AND Bank = '%s' AND FinancialYear = '%s'";
            int reply = JOptionPane.showConfirmDialog(null, "Do you want to save TDS for given entry??");
            if (reply == JOptionPane.YES_OPTION) {
                try {
                    String getSqlValue = String.format(getSql, member, bank, financialYearValue);
                    ResultSet resultSet = statement.executeQuery(getSqlValue);
                    if(resultSet.next()) {
                        String updateSqlValue = String.format(updateSql, tdsValue, member, bank, financialYearValue);
                        statement.executeUpdate(updateSqlValue);
                        JOptionPane.showMessageDialog(null, "Tds updated successfully!!!", "Note", 2);
                    } else {
                        String insertSqlValue = String.format(insertSql, member, bank, financialYearValue, tdsValue);
                        statement.execute(insertSqlValue);
                        JOptionPane.showMessageDialog(null, "Record has been added!!!", "Note", 2);
                    }
                } catch (SQLException throwables) {
                    JOptionPane.showMessageDialog(null, "Error saving record" + throwables.toString(), "ERROR", 2);
                }
            } else if (reply == JOptionPane.NO_OPTION) {
                return;
            }
        }
    }

    private void saveDetails() throws ParseException {
        List<FD> fds = getRequiredFds();
        int size = fds.size();
        for(int i=0; i<size; i++) {
            FD fd = fds.get(i);
            fd.getQ1Interest();
            fd.getQ2Interest();
            fd.getQ3Interest();
            fd.getQ4Interest();
            String financialYear = fd.getFinancialYear();
            String financialYear1 = financialYear.substring(0, financialYear.indexOf("-"));
            String financialYear2 = financialYear.substring(financialYear.indexOf("-")+1);
            financialYear = (Integer.parseInt(financialYear1)+1) + "-" + (Integer.parseInt(financialYear2)+1);
            String sql = "Insert into Fd"
                    + "(Member, Bank, Fd, CreatedOn, MaturityOn, ROI, Principal, MaturityAmt, Period, financialYear, opening)"
                    + "values('"
                    + fd.getMember() + "','" + fd.getBank() + "','" + fd.getFdNo() + "','" + fd.getCreatedOn() + "','"
                    + fd.getMaturityDate() + "','" + fd.getRoi() + "','" + fd.getPrincipal() + "','"
                    + fd.getMaturityVal() + "','" + fd.getPeriod() + "','" + financialYear + "','" + fd.getClosing()
                    + "')";
            String update = "UPDATE Fd "
                    + "set closing = '" + fd.getClosing() + "', tdsPercentage = '" + fd.getTdsValue() + "' "
                    + "where Fd = '" + fd.getFdNo() +"' and financialYear = '" + fd.getFinancialYear() + "'";
            int reply = JOptionPane.showConfirmDialog(null, "Do you want to confirm and save??");
            if (reply == JOptionPane.YES_OPTION) {
                try {
                    statement.execute(sql);
                    statement.executeUpdate(update);
                    JOptionPane.showMessageDialog(null, "Records updated!!!", "Note", JOptionPane.WARNING_MESSAGE);
                } catch (SQLException throwable) {
                    JOptionPane.showMessageDialog(null, "Error updating records" + throwable.toString(), "ERROR", JOptionPane.WARNING_MESSAGE);
                }
            } else if (reply == JOptionPane.NO_OPTION) {
                return;
            }
        }
    }

    private void createFDsTable(String tdsValue) throws ParseException {
        List<FD> fds = getRequiredFds();
        int size = fds.size();
        String data[][] = new String[size+1][];
        String array[] = new String[cols.length];
        array[8]=array[9]=array[10]=array[11]=array[12]=array[13]=array[14]=array[15]=array[16]=array[17]=array[18]="0";
        for(int i=0; i<size; i++) {
            FD fd = fds.get(i);
            fd.setTdsValue(tdsValue);
            String arr[] = new String[cols.length];
            arr[0] = fd.getCreatedOn();
            arr[1] = fd.getFdNo();
            arr[2] = fd.getPeriod();
            arr[3] = fd.getRoi();
            arr[4] = fd.getPrincipal();
            arr[5] = fd.getMaturityDate();
            arr[6] = fd.getMaturityVal();
            arr[7] = fd.getOpening();
            try {
                arr[8] = fd.getQ1Interest();
                array[8] = String.format("%.0f", Double.parseDouble(array[8]) + Double.parseDouble(arr[8]));
                arr[9] = fd.getQ1Tds();
                array[9] = String.format("%.0f", Double.parseDouble(array[9]) + Double.parseDouble(arr[9]));
                arr[10] = fd.getQ2Interest();
                array[10] = String.format("%.0f", Double.parseDouble(array[10]) + Double.parseDouble(arr[10]));
                arr[11] = fd.getQ2Tds();
                array[11] = String.format("%.0f", Double.parseDouble(array[11]) + Double.parseDouble(arr[11]));
                arr[12] = fd.getQ3Interest();
                array[12] = String.format("%.0f", Double.parseDouble(array[12]) + Double.parseDouble(arr[12]));
                arr[13] = fd.getQ3Tds();
                array[13] = String.format("%.0f", Double.parseDouble(array[13]) + Double.parseDouble(arr[13]));
                arr[14] = fd.getQ4Interest();
                array[14] = String.format("%.0f", Double.parseDouble(array[14]) + Double.parseDouble(arr[14]));
                arr[15] = fd.getQ4Tds();
                array[15] = String.format("%.0f", Double.parseDouble(array[15]) + Double.parseDouble(arr[15]));
                arr[16] = fd.getTotalInterest();
                array[16] = String.format("%.0f", Double.parseDouble(array[16]) + Double.parseDouble(arr[16]));
                arr[17] = fd.getTotalTds();
                array[17] = String.format("%.0f", Double.parseDouble(array[17]) + Double.parseDouble(arr[17]));
                arr[18] = fd.getClosing();
                array[18] = String.format("%.2f", Double.parseDouble(array[18]) + Double.parseDouble(arr[18]));
                arr[19] = "";
            } catch(ParseException e) {
                e.printStackTrace();
            }
            data[i] = arr;
            if(i==size-1) {
                data[i+1] = array;
            }
        }
        JTable jTable = new JTable(data, cols) {
            public boolean editCellAt(int row, int column, java.util.EventObject e) {
                return false;
            }
        };
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTable.setColumnSelectionAllowed(false);
        jTable.setRowSelectionAllowed(true);
        jTable.setDefaultRenderer(Object.class, new LastRowBold());
        setJTableColumnsWidth(jTable, screenSize.width-20, 6,10,3,3,6,6,6,6,3,3,3,3,3,3,3,3,4,4,6,16);

        sp = new JScrollPane(jTable);
        sp.setBounds(0,150, screenSize.width-20, screenSize.height-250);

        add(sp);
        setVisible(true);
        System.out.println(fds);
    }

    private void clearDetails() {
        membersList.setSelectedItem(membersList.getItemAt(0));
        banksList.setSelectedItem(banksList.getItemAt(0));
        financialYearsList.setSelectedItem(financialYearsList.getItemAt(0));
        tds.setText("");
        tdsValueLabel.setText("");
        customerIdLabel.setText("");
        if(sp != null) {
            sp = new JScrollPane();
            sp.setBounds(0,150, screenSize.width-20, screenSize.height-250);
            add(sp);
            setVisible(true);
        }
    }

    private void autoPopulateBanks(List<String> banks) {
        try {
            String sql = "SELECT * FROM Banks";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                banks.add(rs.getString("Bank"));
            }
            //banks.add(allValue);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error in populating banks!!!", "Note", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void autoPopulateMembers(List<String> members) {
        try {
            String sql = "SELECT * FROM Members";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                members.add(rs.getString("Member"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error in populating members!!!", "Note", JOptionPane.WARNING_MESSAGE);
        }
    }

    //gives list of fds for selected financial year
    private List<FD> getRequiredFds() throws ParseException {
        String bank = (String) banksList.getItemAt(banksList.getSelectedIndex());
        String member = (String) membersList.getItemAt(membersList.getSelectedIndex());
        String financialYearValue = (String) financialYearsList.getItemAt(financialYearsList.getSelectedIndex());
        String tdsValue = tds.getText();
        try {
             String getSql = "SELECT * FROM Fd f INNER JOIN YearlyFd y ON f.Fd = y.Fd WHERE f.Member='%s' AND f.Bank='%s' AND y.financialYear = '%s'";
             String getSqlValue = String.format(getSql, member, bank, financialYearValue);
            ResultSet rs = statement.executeQuery(getSqlValue);
            List<FD> fds = new ArrayList<>();
            while (rs.next()) {
                FD fd = new FD();
                fd.setMember(rs.getString("Member"));
                fd.setBank(rs.getString("Bank"));
                fd.setFdNo(rs.getString("Fd"));
                fd.setCreatedOn(rs.getString("CreatedOn"));
                fd.setMaturityDate(rs.getString("MaturityOn"));
                fd.setRoi(rs.getString("ROI"));
                fd.setPrincipal(rs.getString("Principal"));
                fd.setFinancialYear(rs.getString("financialYear"));
                fd.setTdsValue(tdsValue);
                fd.setOpening(rs.getString("opening"));
                fd.setMaturityVal(rs.getString("MaturityAmt"));
                fd.setPeriod(rs.getString("Period"));
                String createdOn = rs.getString("CreatedOn");
                String maturityDate = rs.getString("MaturityOn");
                if(toBeIncluded(createdOn, maturityDate, financialYearValue)) {
                    if(financialYearValue.equals(fd.getFinancialYear())) {
                        fds.add(fd);
                    }
                }
            }
            return fds;
        } catch (SQLException throwable) {
            JOptionPane.showMessageDialog(null, "Error fetching records" + throwable.toString(), "ERROR", JOptionPane.WARNING_MESSAGE);
        }
        return null;
    }

    private boolean toBeIncluded(String createdOn, String maturityDate, String financialYearValue) throws ParseException {
        Date start = sdf.parse(createdOn);
        Date end = sdf.parse(maturityDate);
        String financialYear1 = financialYearValue.substring(0, financialYearValue.indexOf("-"));
        String financialYear2 = financialYearValue.substring(financialYearValue.indexOf("-")+1);
        String financialStart = "01-04-" + financialYear1;
        String financialEnd  = "31-03-" + financialYear2;
        Date fStart = sdf.parse(financialStart);
        Date fEnd = sdf.parse(financialEnd);
        if(end.compareTo(fStart) < 0 || fEnd.compareTo(start) < 0) { //end<fStart or fEnd<start
            return false;
        }
        return true;
    }

    private void validateInput() {
        String bank = (String) banksList.getItemAt(banksList.getSelectedIndex());
        String member = (String) membersList.getItemAt(membersList.getSelectedIndex());
        String financialYearValue = (String) financialYearsList.getItemAt(financialYearsList.getSelectedIndex());

        if (bank == null || "".equals(bank) || dummyValue.equals(bank)) {
            JOptionPane.showMessageDialog(null, "Invalid bank name!!!", "Note", JOptionPane.WARNING_MESSAGE);
            banksList.requestFocus();
            return;
        }
        if (member == null || "".equals(member) || dummyValue.equals(member)) {
            JOptionPane.showMessageDialog(null, "Invalid member name!!!", "Note", JOptionPane.WARNING_MESSAGE);
            membersList.requestFocus();
            return;
        }
        if (financialYearValue == null || "".equals(financialYearValue) || dummyValue.equals(financialYearValue)) {
            JOptionPane.showMessageDialog(null, "Invalid financial year!!!", "Note", JOptionPane.WARNING_MESSAGE);
            financialYearsList.requestFocus();
            return;
        }
        /*String tdsValue = tds.getText();
        if (tdsValue == null || "".equals(tdsValue) || dummyValue.equals(tdsValue)) {
            JOptionPane.showMessageDialog(null, "Invalid tds value!!!", "Note", JOptionPane.WARNING_MESSAGE);
            tds.requestFocus();
            return;
        }*/
    }

    public static void setJTableColumnsWidth(JTable table, int tablePreferredWidth,
                                             double... percentages) {
        double total = 0;
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            total += percentages[i];
        }
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth((int)
                    (tablePreferredWidth * (percentages[i] / total)));
        }
    }
}
