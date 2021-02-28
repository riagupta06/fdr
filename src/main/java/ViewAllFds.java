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

public class ViewAllFds extends JFrame implements ActionListener {

    private final String dummyValue = "-select-";
    private final String allValue = "ALL";
    private final Font font = new Font("Times New Roman", Font.BOLD, 18);
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    private final String cols[] = {"Created On","FDR No","Period","Rate %","Amount","Mt. Date","Mt. Value","Opening bal",
            "Int-Q1","TDS-Q1","Int-Q2","TDS-Q2","Int-Q3","TDS-Q3","Int-Q4","TDS-Q4","Total Int","Total TDS", "Closing bal", "Remarks"};
    private final Statement statement;

    JLabel memberNameLabel = new JLabel("Member");
    JComboBox membersList;
    JLabel financialYearLabel = new JLabel("Financial Year");
    JComboBox financialYearsList;

    JButton viewFdButton = new JButton("View FDs");
    JButton clearButton = new JButton("CLEAR");

    java.util.List<String> members = new ArrayList<>();
    java.util.List<String> financialYears = new ArrayList<>();
    JScrollPane sp;
    JLabel jLabel;

    public ViewAllFds(Statement statement) {
        this.statement = statement;
        members.add(dummyValue);
        autoPopulateMembers(members);
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
        Container pane = getContentPane();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("ALL FD RECORDS");
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        memberNameLabel.setFont(font);
        membersList = new JComboBox(members.toArray());

        financialYearLabel.setFont(font);
        financialYearsList = new JComboBox(financialYears.toArray());

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.ipadx = 25;
        c.insets = new Insets(10,0,0,0);
        panel.add(memberNameLabel, c);
        c.gridx = 2;
        c.gridy = 0;
        c.ipadx = 25;
        c.insets = new Insets(10,0,0,0);
        panel.add(membersList, c);
        c.gridx = 0;
        c.gridy = 2;
        c.ipadx = 25;
        panel.add(financialYearLabel, c);
        c.gridx = 2;
        c.gridy = 2;
        c.ipadx = 25;
        panel.add(financialYearsList, c);
        pane.add(panel, BorderLayout.NORTH);

        viewFdButton.addActionListener(this);
        clearButton.addActionListener(this);

        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());
        buttons.add(viewFdButton);
        buttons.add(clearButton);
        pane.add(buttons, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == viewFdButton) {
            validateInput();
            try {
                createAllFDsTable();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (event.getSource() == clearButton) {
            clearDetails();
        }
    }

    private void createAllFDsTable() throws ParseException {
        String member = (String) membersList.getItemAt(membersList.getSelectedIndex());
        String financialYearValue = (String) financialYearsList.getItemAt(financialYearsList.getSelectedIndex());
        List<String> banks = new ArrayList<>();
        try {
            String getSql = "SELECT distinct Bank FROM Fd f INNER JOIN YearlyFd y ON f.Fd = y.Fd WHERE f.Member='%s' AND y.financialYear = '%s'";
            String getSqlValue = String.format(getSql, member, financialYearValue);
            ResultSet rs = statement.executeQuery(getSqlValue);
            while (rs.next()) {
                banks.add(rs.getString("Bank"));
            }
        } catch (SQLException throwable) {
            JOptionPane.showMessageDialog(null, "Error fetching records" + throwable.toString(), "ERROR", JOptionPane.WARNING_MESSAGE);
        }
        JPanel panel = new JPanel();
        GridLayout gridLayout = new GridLayout(banks.size()*2,1);
        panel.setLayout(gridLayout);
        for(String bank : banks) {
            String tds = getTdsValue(member, bank, financialYearValue);
            if (tds == null) {
                tds = "0";
            }
            jLabel = new JLabel("Bank: " + bank + " [TDS: " + tds + "]");
            sp = createFDsTable(tds, bank);
            panel.add(jLabel);
            panel.add(sp);
        }
        add(panel, BorderLayout.CENTER);
    }

    private String getTdsValue(String member, String bank, String financialYearValue) {
        String getSql = "SELECT * FROM TDS WHERE Member = '%s' AND Bank = '%s' AND FinancialYear = '%s'";
        try {
            String getSqlValue = String.format(getSql, member, bank, financialYearValue);
            ResultSet resultSet = statement.executeQuery(getSqlValue);
            if(resultSet.next()) {
                String value = resultSet.getString("Tds");
                return value;
            }
        } catch (SQLException throwables) {
            JOptionPane.showMessageDialog(null, "Error fetching saved tds value" + throwables.toString(), "ERROR", 2);
        }
        return null;
    }

    private JScrollPane createFDsTable(String tdsValue, String bank) throws ParseException {
        List<FD> fds = getRequiredFds(tdsValue, bank);
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
        setVisible(true);
        System.out.println(fds);
        return sp;
    }

    private void clearDetails() {
        membersList.setSelectedItem(membersList.getItemAt(0));
        financialYearsList.setSelectedItem(financialYearsList.getItemAt(0));
        if(sp != null) {
            sp = new JScrollPane();
            sp.setBounds(0,150, screenSize.width-20, screenSize.height-250);
            add(sp);
            setVisible(true);
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
    private List<FD> getRequiredFds(String tdsValue, String bank) throws ParseException {
        String member = (String) membersList.getItemAt(membersList.getSelectedIndex());
        String financialYearValue = (String) financialYearsList.getItemAt(financialYearsList.getSelectedIndex());
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
        String member = (String) membersList.getItemAt(membersList.getSelectedIndex());
        String financialYearValue = (String) financialYearsList.getItemAt(financialYearsList.getSelectedIndex());
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
