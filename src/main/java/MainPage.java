import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.Statement;

public class MainPage extends JFrame implements ActionListener {

    Statement statement;
    Font font = new Font("Times New Roman", Font.BOLD, 18);

    JTextField memberName = new JTextField(15);
    JLabel memberNameLabel = new JLabel("Member");
    JButton addMemberButton = new JButton("Add member");

    JTextField bankName = new JTextField(15);
    JLabel bankNameLabel = new JLabel("Bank");
    JButton addBankButton = new JButton("Add bank");;

    JButton addCustomerIdButton = new JButton("ADD CUST ID");
    JButton addFdButton = new JButton("ADD FD");
    JButton viewFdButton = new JButton("VIEW FD");
    JButton viewAllFdsButton = new JButton("VIEW All FDs");

    public MainPage(Statement statement) {
        this.statement = statement;
        viewPage();
    }

    private void viewPage() {
        Container pane = getContentPane();
        this.setSize(820, 700);
        this.setTitle("MAIN PANEL");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(true);

        memberNameLabel.setFont(font);
        bankNameLabel.setFont(font);

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
        panel.add(memberName, c);
        c.gridx = 4;
        c.gridy = 0;
        c.ipadx = 25;
        c.insets = new Insets(10,10,0,0);
        panel.add(addMemberButton, c);
        c.gridx = 0;
        c.gridy = 1;
        c.ipadx = 25;
        c.insets = new Insets(10,0,0,0);
        panel.add(bankNameLabel, c);
        c.gridx = 2;
        c.gridy = 1;
        c.ipadx = 25;
        c.insets = new Insets(10,0,0,0);
        panel.add(bankName, c);
        c.gridx = 4;
        c.gridy = 1;
        c.ipadx = 25;
        c.insets = new Insets(10,10,0,0);
        panel.add(addBankButton, c);
        pane.add(panel, BorderLayout.NORTH);

        addMemberButton.addActionListener(this);
        addBankButton.addActionListener(this);
        addFdButton.addActionListener(this);
        viewFdButton.addActionListener(this);
        viewAllFdsButton.addActionListener(this);
        addCustomerIdButton.addActionListener(this);

        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());
        buttons.add(addFdButton);
        buttons.add(viewFdButton);
        buttons.add(viewAllFdsButton);
        buttons.add(addCustomerIdButton);
        pane.add(buttons, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == addMemberButton) {
            addMember();
        } else if (event.getSource() == addBankButton) {
            addBank();
        } else if (event.getSource() == addFdButton) {
            AddFd addFd = new AddFd(statement);
            addFd.setVisible(true);
            addFd.getContentPane();
        } else if (event.getSource() == viewFdButton) {
            ViewFd viewFd = new ViewFd(statement);
            viewFd.setVisible(true);
            viewFd.getContentPane();
        } else if (event.getSource() == viewAllFdsButton) {
            ViewAllFds viewAllFds = new ViewAllFds(statement);
            viewAllFds.setVisible(true);
            viewAllFds.getContentPane();
        } else if (event.getSource() == addCustomerIdButton) {
            AddCustomerId addCustomerId = new AddCustomerId(statement);
            addCustomerId.setVisible(true);
            addCustomerId.getContentPane();
        }
    }

    private void addBank() {
        String bank = bankName.getText();
        if (bank == null || "".equals(bank)) {
            JOptionPane.showMessageDialog(null, "Invalid bank name!!!", "Note", 2);
            bankName.requestFocus();
            return;

        } else {
            String sql = "Insert into Banks"
                    + "(Bank)"
                    + "values('" + bank + "')";
            int reply = JOptionPane.showConfirmDialog(null, "Do you want to add this record??");
            if (reply == JOptionPane.YES_OPTION) {
                try {
                    statement.execute(sql);
                    JOptionPane.showMessageDialog(null, "Record has been added!!!", "Note", 2);
                    bankName.setText("");
                } catch (SQLException throwables) {
                    JOptionPane.showMessageDialog(null, "Error saving record" + throwables.toString(), "ERROR", 2);
                }
            }
            if (reply == JOptionPane.NO_OPTION) {
                return;
            }
        }
    }

    private void addMember() {
        String member = memberName.getText();
        if (member == null || "".equals(member)) {
            JOptionPane.showMessageDialog(null, "Invalid member name!!!", "Note", 2);
            memberName.requestFocus();
            return;

        } else {
            String sql = "Insert into Members"
                    + "(Member)"
                    + "values('" + member + "')";
            int reply = JOptionPane.showConfirmDialog(null, "Do you want to add this record??");
            if (reply == JOptionPane.YES_OPTION) {
                try {
                    statement.execute(sql);
                    JOptionPane.showMessageDialog(null, "Record has been added!!!", "Note", 2);
                    memberName.setText("");
                } catch (SQLException throwables) {
                    JOptionPane.showMessageDialog(null, "Error saving record" + throwables.toString(), "ERROR", 2);
                }
            }
            if (reply == JOptionPane.NO_OPTION) {
                return;
            }
        }
    }
}
