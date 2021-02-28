import javax.swing.*;
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
import java.util.concurrent.TimeUnit;

public class AddFd extends JFrame implements ActionListener {

    private final int col = 20;
    private final String dummyValue = "-select-";
    private final Font font = new Font("Times New Roman", Font.BOLD, 18);
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    private final Statement statement;

    JLabel memberNameLabel = new JLabel("Member");
    JLabel bankNameLabel = new JLabel("Bank");
    JTextField fdName = new JTextField(col);
    JLabel fdNameLabel = new JLabel("FD No");
    JTextField openedOn = new JTextField(col);
    JLabel createdOnLabel = new JLabel("Created On");
    JButton createdButton = new JButton("..");
    JTextField maturityDate = new JTextField(col);
    JLabel maturityDateLabel = new JLabel("Maturity Date");
    JButton maturityButton = new JButton("..");
    JTextField rateOfInterest = new JTextField(col);
    JLabel rateOfInterestLabel = new JLabel("Rate Of Interest");
    JTextField principal = new JTextField(col);
    JLabel principalLabel = new JLabel("Principal Amount");
    JTextField maturityAmt = new JTextField(col);
    JLabel maturityAmtLabel = new JLabel("Maturity Amount");
    JTextField period = new JTextField(col);
    JLabel periodLabel = new JLabel("Period");
    JComboBox membersList;
    JComboBox banksList;
    JLabel maturityAmountLabel = new JLabel();
    JButton clearButton = new JButton("CLEAR");
    JButton viewFdButton = new JButton("VIEW");
    JButton deleteFdButton = new JButton("DELETE");
    JButton saveButton = new JButton("SAVE");

    java.util.List<String> members = new ArrayList<>();
    java.util.List<String> banks = new ArrayList<>();

    JFrame jFrame;

    public AddFd(Statement statement) {
        jFrame = this;
        this.statement = statement;
        members.add(dummyValue);
        banks.add(dummyValue);
        autoPopulateMembers(members);
        autoPopulateBanks(banks);
        viewPage();
    }

    private void viewPage() {
        Container pane = getContentPane();
        setSize(730, 720);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("FD RECORD");
        setResizable(true);
        //setLayout(null);

        memberNameLabel.setBounds(90, 70, 300, 25);
        memberNameLabel.setFont(font);
        add(memberNameLabel);
        membersList = new JComboBox(members.toArray());
        membersList.setBounds(340, 70, 300, 25);
        add(membersList);

        bankNameLabel.setBounds(90, 95, 300, 25);
        bankNameLabel.setFont(font);
        add(bankNameLabel);
        banksList = new JComboBox(banks.toArray());
        banksList.setBounds(340, 95, 300, 25);
        add(banksList);

        fdNameLabel.setBounds(90, 120, 300, 25);
        fdNameLabel.setFont(font);
        add(fdNameLabel);
        fdName.setBounds(340, 120, 300, 25);
        add(fdName);

        createdOnLabel.setBounds(90, 145, 300, 25);
        createdOnLabel.setFont(font);
        add(createdOnLabel);
        openedOn.setBounds(340, 145, 300, 25);
        add(openedOn);
        createdButton.setBounds(640, 145, 25, 25);
        add(createdButton);

        maturityDateLabel.setBounds(90, 170, 500, 25);
        maturityDateLabel.setFont(font);
        add(maturityDateLabel);
        maturityDate.setBounds(340, 170, 300, 25);
        add(maturityDate);
        maturityButton.setBounds(640, 170, 25, 25);
        add(maturityButton);

        createdButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                openedOn.setText(new DatePicker(jFrame).setPickedDate());
            }
        });
        maturityButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                maturityDate.setText(new DatePicker(jFrame).setPickedDate());
            }
        });

        rateOfInterestLabel.setBounds(90, 195, 300, 25);
        rateOfInterestLabel.setFont(font);
        add(rateOfInterestLabel);
        rateOfInterest.setBounds(340, 195, 300, 25);
        add(rateOfInterest);

        principalLabel.setBounds(90, 220, 300, 25);
        principalLabel.setFont(font);
        add(principalLabel);
        principal.setBounds(340, 220, 300, 25);
        add(principal);

        maturityAmtLabel.setBounds(90, 245, 300, 25);
        maturityAmtLabel.setFont(font);
        add(maturityAmtLabel);
        maturityAmt.setBounds(340, 245, 300, 25);
        add(maturityAmt);

        periodLabel.setBounds(90, 270, 300, 25);
        periodLabel.setFont(font);
        add(periodLabel);
        period.setBounds(340, 270, 300, 25);
        add(period);

        maturityAmountLabel.setBounds(340, 300, 600, 25);
        add(maturityAmountLabel);

        clearButton.addActionListener(this);
        viewFdButton.addActionListener(this);
        deleteFdButton.addActionListener(this);
        saveButton.addActionListener(this);

        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());
        buttons.add(viewFdButton);
        buttons.add(deleteFdButton);
        buttons.add(saveButton);
        buttons.add(clearButton);
        pane.add(buttons, BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == clearButton) {
            clearDetails();
        } else if (event.getSource() == viewFdButton) {
            viewDetails();
        } else if (event.getSource() == deleteFdButton) {
            deleteDetails();
        } else if (event.getSource() == saveButton) {
            addFD();
        }
    }

    private void deleteDetails() {
        String bank = (String) banksList.getItemAt(banksList.getSelectedIndex());
        String member = (String) membersList.getItemAt(membersList.getSelectedIndex());
        String fdNo = fdName.getText();
        String createdOn = openedOn.getText();
        if (bank == null || "".equals(bank) || dummyValue.equals(bank)) {
            JOptionPane.showMessageDialog(null, "Invalid bank name!!!", "Note", JOptionPane.WARNING_MESSAGE);
            banksList.requestFocus();
            return;
        } else if (member == null || "".equals(member) || dummyValue.equals(member)) {
            JOptionPane.showMessageDialog(null, "Invalid member name!!!", "Note", JOptionPane.WARNING_MESSAGE);
            membersList.requestFocus();
            return;
        }
        if (fdNo == null || "".equals(fdNo)) {
            JOptionPane.showMessageDialog(null, "Invalid fd no!!!", "Note", JOptionPane.WARNING_MESSAGE);
            fdName.requestFocus();
            return;
        }
        try {
            Date firstDate = sdf.parse(createdOn);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Invalid format of created date!!!", "Note", JOptionPane.WARNING_MESSAGE);
            openedOn.requestFocus();
            return;
        }
        try {
            String deleteFdSql = "DELETE FROM Fd WHERE Member = '%s' AND Bank = '%s' AND Fd = '%s' AND CreatedOn = '%s'";
            String deleteFdSqlValue = String.format(deleteFdSql, member, bank, fdNo, createdOn);
            statement.executeUpdate(deleteFdSqlValue);
            String deleteYearlyFdSql = "DELETE FROM YearlyFd WHERE Fd = '%s' AND CreatedOn = '%s'";
            String deleteYearlyFdSqlValue = String.format(deleteYearlyFdSql, fdNo, createdOn);
            statement.executeUpdate(deleteYearlyFdSqlValue);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private void viewDetails() {
        String bank = (String) banksList.getItemAt(banksList.getSelectedIndex());
        String member = (String) membersList.getItemAt(membersList.getSelectedIndex());
        String fdNo = fdName.getText();
        String createdOn = openedOn.getText();
        if (bank == null || "".equals(bank) || dummyValue.equals(bank)) {
            JOptionPane.showMessageDialog(null, "Invalid bank name!!!", "Note", JOptionPane.WARNING_MESSAGE);
            banksList.requestFocus();
            return;
        } else if (member == null || "".equals(member) || dummyValue.equals(member)) {
            JOptionPane.showMessageDialog(null, "Invalid member name!!!", "Note", JOptionPane.WARNING_MESSAGE);
            membersList.requestFocus();
            return;
        }
        if (fdNo == null || "".equals(fdNo)) {
            JOptionPane.showMessageDialog(null, "Invalid fd no!!!", "Note", JOptionPane.WARNING_MESSAGE);
            fdName.requestFocus();
            return;
        }
        try {
            Date firstDate = sdf.parse(createdOn);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Invalid format of created date!!!", "Note", JOptionPane.WARNING_MESSAGE);
            openedOn.requestFocus();
            return;
        }
        String sql = "SELECT * FROM Fd WHERE Member = '%s' AND Bank = '%s' AND Fd = '%s' AND CreatedOn = '%s'";
        String getSqlValue = String.format(sql, member, bank, fdNo, createdOn);
        try {
            ResultSet resultSet = statement.executeQuery(getSqlValue);
            while(resultSet.next()) {
                maturityDate.setText(resultSet.getString("MaturityOn"));
                rateOfInterest.setText(resultSet.getString("ROI"));
                principal.setText(resultSet.getString("Principal"));
                maturityAmt.setText(resultSet.getString("MaturityAmt"));
                period.setText(resultSet.getString("Period"));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearDetails() {
        membersList.setSelectedItem(membersList.getItemAt(0));
        banksList.setSelectedItem(banksList.getItemAt(0));
        fdName.setText("");
        openedOn.setText("");
        maturityDate.setText("");
        rateOfInterest.setText("");
        principal.setText("");
        maturityAmt.setText("");
        period.setText("");
        maturityAmountLabel.setText("");
    }

    private void autoPopulateBanks(List<String> banks) {
        try {
            String sql = "SELECT * FROM Banks";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                banks.add(rs.getString("Bank"));
            }
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

    private void addFD() {
        String bank = (String) banksList.getItemAt(banksList.getSelectedIndex());
        String member = (String) membersList.getItemAt(membersList.getSelectedIndex());
        String fdNo = fdName.getText();
        String createdOn = openedOn.getText();
        String maturityDat = maturityDate.getText();
        String roi = rateOfInterest.getText();
        String principalAmt = principal.getText();
        String maturityAmtVal = maturityAmt.getText();
        String periodVal = period.getText();
        if (bank == null || "".equals(bank) || dummyValue.equals(bank)) {
            JOptionPane.showMessageDialog(null, "Invalid bank name!!!", "Note", JOptionPane.WARNING_MESSAGE);
            banksList.requestFocus();
            return;
        } else if (member == null || "".equals(member) || dummyValue.equals(member)) {
            JOptionPane.showMessageDialog(null, "Invalid member name!!!", "Note", JOptionPane.WARNING_MESSAGE);
            membersList.requestFocus();
            return;
        }
        if (fdNo == null || "".equals(fdNo)) {
            JOptionPane.showMessageDialog(null, "Invalid fd no!!!", "Note", JOptionPane.WARNING_MESSAGE);
            fdName.requestFocus();
            return;
        }
        try {
            Date firstDate = sdf.parse(createdOn);
            Date secondDate = sdf.parse(maturityDat);
            /*long diffInMilli = new Date().getTime() - firstDate.getTime();
            if(diffInMilli < 0.0) {
                JOptionPane.showMessageDialog(null, "FD creation date cannot be more than current time!!!", "Note", JOptionPane.WARNING_MESSAGE);
                return;
            }*/
            long diffInMillis = secondDate.getTime() - firstDate.getTime();
            if(diffInMillis < 0.0) {
                JOptionPane.showMessageDialog(null, "Maturity date cannot be less than FD creation date!!!", "Note", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Invalid format of created date/maturity date!!!", "Note", JOptionPane.WARNING_MESSAGE);
            openedOn.requestFocus();
            return;
        }
        if (roi == null || "".equals(roi)) {
            JOptionPane.showMessageDialog(null, "Invalid roi!!!", "Note", JOptionPane.WARNING_MESSAGE);
            rateOfInterest.requestFocus();
            return;
        }
        if (principalAmt == null || "".equals(principalAmt)) {
            JOptionPane.showMessageDialog(null, "Invalid principal amount!!!", "Note", JOptionPane.WARNING_MESSAGE);
            principal.requestFocus();
            return;
        }
        if (maturityAmtVal == null || "".equals(maturityAmtVal)) {
            JOptionPane.showMessageDialog(null, "Invalid maturity amount!!!", "Note", JOptionPane.WARNING_MESSAGE);
            maturityAmt.requestFocus();
            return;
        }
        if (periodVal == null || "".equals(periodVal)) {
            JOptionPane.showMessageDialog(null, "Invalid period amount!!!", "Note", JOptionPane.WARNING_MESSAGE);
            period.requestFocus();
            return;
        } else {
            try {
                displayMaturityAmt();
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(null, "Error calculating maturity amount" + e.toString(), "ERROR", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Date openingDate = null;
            try {
                openingDate = sdf.parse(createdOn);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String financialYear;
            if(openingDate.getMonth() >= 3) { //April or more
                financialYear = (openingDate.getYear()+1900) + "-" + (openingDate.getYear()+1900+1);
            } else {
                financialYear = (openingDate.getYear()+1900-1) + "-" + (openingDate.getYear()+1900);
            }
            String sql = "Insert into Fd"
                    + "(Member, Bank, Fd, CreatedOn, MaturityOn, ROI, Principal, MaturityAmt, Period) "
                    + "values('"
                    + member + "','" + bank + "','" + fdNo + "','" + createdOn + "','"
                    + maturityDat + "','" + roi + "','" + principalAmt + "','"
                    + maturityAmtVal + "','" + periodVal
                    + "')";
            String yearlyFdSql = "Insert into YearlyFd "
                    + "(Fd, CreatedOn, financialYear, opening) "
                    + "values('"
                    + fdNo + "','" + createdOn + "','" + financialYear + "','" + principalAmt
                    + "')";
            int reply = JOptionPane.showConfirmDialog(null, "Do you want to add this record??");
            if (reply == JOptionPane.YES_OPTION) {
                try {
                    statement.execute(sql);
                    statement.execute(yearlyFdSql);
                    JOptionPane.showMessageDialog(null, "Record has been added!!!", "Note", JOptionPane.WARNING_MESSAGE);
                } catch (SQLException throwable) {
                    JOptionPane.showMessageDialog(null, "Error saving record" + throwable.toString(), "ERROR", JOptionPane.WARNING_MESSAGE);
                }
            }
            if (reply == JOptionPane.NO_OPTION) {
                return;
            }
        }
    }

    private void displayMaturityAmt() throws ParseException {
        Double maturity = calculateMaturityAmt();
        maturityAmountLabel.setText(String.format("Maturity Amount is Rs. %.2f", maturity));
    }

    private Double calculateMaturityAmt() throws ParseException {
        String createdOn = openedOn.getText();
        String maturityDat = maturityDate.getText();
        String roi = rateOfInterest.getText();
        String principalAmt = principal.getText();

        Double roiD = Double.parseDouble(roi);
        Double principalAmtD = Double.parseDouble(principalAmt);

        Date firstDate = sdf.parse(createdOn);
        Date secondDate = sdf.parse(maturityDat);

        long diffInMillis = secondDate.getTime() - firstDate.getTime();
        long days = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        days = days + 1;

        Double maturity = principalAmtD * Math.pow((1 + (roiD) / (4 * 100)), 4 * days / 365d);

        return maturity;
    }
}
