import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AddCustomerId  extends JFrame implements ActionListener {

    private final int col = 20;
    private final String dummyValue = "-select-";
    private final Font font = new Font("Times New Roman", Font.BOLD, 18);
    private final Statement statement;

    JTextField customerId = new JTextField(col);
    JLabel customerIdLabel = new JLabel("CustId");

    JLabel memberNameLabel = new JLabel("Member");
    JLabel bankNameLabel = new JLabel("Bank");
    JComboBox membersList;
    JComboBox banksList;

    JButton clearButton = new JButton("CLEAR");
    JButton viewFdButton = new JButton("VIEW");
    JButton deleteFdButton = new JButton("DELETE");
    JButton saveButton = new JButton("SAVE");

    java.util.List<String> members = new ArrayList<>();
    java.util.List<String> banks = new ArrayList<>();

    JFrame jFrame;

    public AddCustomerId(Statement statement) {
        jFrame = this;
        this.statement = statement;
        members.add(dummyValue);
        banks.add(dummyValue);
        autoPopulateMembers(members);
        autoPopulateBanks(banks);
        viewPage();
    }

    private void autoPopulateBanks(java.util.List<String> banks) {
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

    private void viewPage() {
        Container pane = getContentPane();
        setSize(730, 720);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("CUSTOMER ID RECORD");
        setResizable(true);

        memberNameLabel.setFont(font);
        membersList = new JComboBox(members.toArray());
        bankNameLabel.setFont(font);
        banksList = new JComboBox(banks.toArray());
        customerIdLabel.setFont(font);

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
        c.gridy = 1;
        c.ipadx = 25;
        c.insets = new Insets(10,0,0,0);
        panel.add(bankNameLabel, c);
        c.gridx = 2;
        c.gridy = 1;
        c.ipadx = 25;
        c.insets = new Insets(10,0,0,0);
        panel.add(banksList, c);
        c.gridx = 0;
        c.gridy = 2;
        c.ipadx = 25;
        panel.add(customerIdLabel, c);
        c.gridx = 2;
        c.gridy = 2;
        c.ipadx = 25;
        panel.add(customerId, c);
        pane.add(panel, BorderLayout.NORTH);

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
            saveDetails();
        }
    }

    private void viewDetails() {
        String bank = (String) banksList.getItemAt(banksList.getSelectedIndex());
        String member = (String) membersList.getItemAt(membersList.getSelectedIndex());
        if (bank == null || "".equals(bank) || dummyValue.equals(bank)) {
            JOptionPane.showMessageDialog(null, "Invalid bank name!!!", "Note", JOptionPane.WARNING_MESSAGE);
            banksList.requestFocus();
            return;
        } else if (member == null || "".equals(member) || dummyValue.equals(member)) {
            JOptionPane.showMessageDialog(null, "Invalid member name!!!", "Note", JOptionPane.WARNING_MESSAGE);
            membersList.requestFocus();
            return;
        }
        String getSql = "SELECT * FROM CustId WHERE Member = '%s' AND Bank = '%s'";
        String getSqlValue = String.format(getSql, member, bank);
        try {
            ResultSet resultSet = statement.executeQuery(getSqlValue);
            if (resultSet.next()) {
                customerId.setText(resultSet.getString("CustomerId"));
            } else {
                JOptionPane.showMessageDialog(null, "No record found!!!", "Note", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException throwable) {
            JOptionPane.showMessageDialog(null, "Error fetching records" + throwable.toString(), "ERROR", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteDetails() {
        String bank = (String) banksList.getItemAt(banksList.getSelectedIndex());
        String member = (String) membersList.getItemAt(membersList.getSelectedIndex());
        if (bank == null || "".equals(bank) || dummyValue.equals(bank)) {
            JOptionPane.showMessageDialog(null, "Invalid bank name!!!", "Note", JOptionPane.WARNING_MESSAGE);
            banksList.requestFocus();
            return;
        } else if (member == null || "".equals(member) || dummyValue.equals(member)) {
            JOptionPane.showMessageDialog(null, "Invalid member name!!!", "Note", JOptionPane.WARNING_MESSAGE);
            membersList.requestFocus();
            return;
        }
        String deleteSql = "DELETE FROM CustId WHERE Member = '%s' AND Bank = '%s'";
        String deleteSqlValue = String.format(deleteSql, member, bank);
        try {
            statement.executeUpdate(deleteSqlValue);
            customerId.setText("");
        } catch (SQLException throwable) {
            JOptionPane.showMessageDialog(null, "Error fetching records" + throwable.toString(), "ERROR", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearDetails() {
        membersList.setSelectedItem(membersList.getItemAt(0));
        banksList.setSelectedItem(banksList.getItemAt(0));
        customerId.setText("");
    }

    private void saveDetails() {
        String bank = (String) banksList.getItemAt(banksList.getSelectedIndex());
        String member = (String) membersList.getItemAt(membersList.getSelectedIndex());
        String customerIdVal = customerId.getText();
        if (bank == null || "".equals(bank) || dummyValue.equals(bank)) {
            JOptionPane.showMessageDialog(null, "Invalid bank name!!!", "Note", JOptionPane.WARNING_MESSAGE);
            banksList.requestFocus();
            return;
        } else if (member == null || "".equals(member) || dummyValue.equals(member)) {
            JOptionPane.showMessageDialog(null, "Invalid member name!!!", "Note", JOptionPane.WARNING_MESSAGE);
            membersList.requestFocus();
            return;
        } else if (customerIdVal == null || "".equals(customerIdVal)) {
            JOptionPane.showMessageDialog(null, "Invalid customer Id!!!", "Note", JOptionPane.WARNING_MESSAGE);
            customerId.requestFocus();
            return;
        }
        String saveSql = "INSERT INTO CustId (Member, Bank, CustomerId) values ('%s','%s','%s')";
        String getSql = "SELECT * FROM CustId WHERE Member = '%s' AND Bank = '%s'";
        String updateSql = "UPDATE CustId SET CustomerId = '%s' WHERE Member = '%s' AND Bank = '%s'";
        String saveSqlValue = String.format(saveSql, member, bank, customerIdVal);
        String getSqlValue = String.format(getSql, member, bank);
        String updateSqlValue = String.format(updateSql, customerIdVal, member, bank);
        try {
            ResultSet resultSet = statement.executeQuery(getSqlValue);
            if (resultSet.next()) {
                statement.executeUpdate(updateSqlValue);
            } else {
                statement.execute(saveSqlValue);
            }
            JOptionPane.showMessageDialog(null, "Records updated!!!", "Note", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException throwable) {
            JOptionPane.showMessageDialog(null, "Error updating records" + throwable.toString(), "ERROR", JOptionPane.WARNING_MESSAGE);
        }
    }
}
