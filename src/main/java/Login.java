import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.Statement;

public class Login extends JFrame implements ActionListener {

    Statement statement;

    JTextField userID;
    JTextField pass;

    JLabel userIDLabel;
    JLabel passLabel;

    JButton loginButton;

    Font fnt;

    public Login(Statement statement) {
        this.statement = statement;
        viewPage();
    }

    private void viewPage() {
        getContentPane();
        setSize(920, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("LOGIN FORM");
        setResizable(false);

        userID = new JTextField(15);
        pass = new JPasswordField(15);

        loginButton = new JButton("SUBMIT");
        userIDLabel = new JLabel("User ID :");
        passLabel = new JLabel("Password :");

        setLayout(null);

        fnt = new Font("Times New Roman", Font.BOLD, 18);

        userIDLabel.setBounds(340, 200, 100, 25);
        userIDLabel.setFont(fnt);
        add(userIDLabel);
        userID.setBounds(440, 200, 140, 25);
        add(userID);

        passLabel.setBounds(340, 260, 100, 25);
        passLabel.setFont(fnt);
        add(passLabel);
        pass.setBounds(440, 260, 140, 25);
        add(pass);

        loginButton.setBounds(400, 310, 100, 25);
        add(loginButton);
        loginButton.addActionListener(this);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == loginButton) {
            if ((userID.getText()) == null || (pass.getText()) == null) {
                JOptionPane.showMessageDialog(this, "Fill both the user name and password");
            } else {
                String sql;
                try {
                    sql = "SELECT * FROM LoginData WHERE userID = '" + userID.getText() + "' AND password ='" + pass.getText() + "'";
                    ResultSet rs = statement.executeQuery(sql);

                    String userIdValue = "";
                    String passValue = "";

                    if (rs.next()) {
                        userIdValue = rs.getString("userID");
                        passValue = rs.getString("password");
                    } else {
                        JOptionPane.showMessageDialog(this, "Login failed. This may be due to incorrect username or password");
                    }

                    if (((userID.getText()).equals(userIdValue)) && (pass.getText().equals(passValue))) {
                        sql = "Update LoginData set Status = 'O' ";
                        statement.executeUpdate(sql);

                        sql = "Update LoginData set Status = 'L' where userID = '" + userID.getText() + "' AND password ='" + pass.getText() + "'";
                        statement.executeUpdate(sql);

                        this.dispose();
                        MainPage p2 = new MainPage(statement);
                        p2.setVisible(true);
                        p2.getContentPane();
                    }
                } catch (Exception es) {
                    JOptionPane.showMessageDialog(this, "1 " + es);
                }
            }
        }
    }
}
