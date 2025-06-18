package system.view;

import javax.swing.*;
import java.awt.*;
import system.components.*;
import system.controllers.RegisterController;

public class RegisterForm extends JFrame {
    private RoundedTextField usernameField, emailField;
    private RoundedPasswordField passField, confirmPassField;
    private RegisterController controller;

    public RegisterForm() {
        controller = new RegisterController(this);

        setTitle("Đăng ký tài khoản");
        setSize(450, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/img/bg_register.jpg"));
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setBounds(0, 0, 450, 500);
        getLayeredPane().add(backgroundLabel, new Integer(Integer.MIN_VALUE));

        JPanel content = (JPanel) getContentPane();
        content.setOpaque(false);
        content.setLayout(null);
        setIconImage(new ImageIcon(getClass().getResource("/img/logo.png")).getImage());

        JLabel title = new JLabel("Tạo tài khoản mới");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBounds(110, 30, 300, 30);
        add(title);

        JLabel nameLabel = new JLabel("Username:");
        nameLabel.setBounds(50, 80, 100, 25);
        add(nameLabel);

        usernameField = new RoundedTextField(30);
        usernameField.setPlaceholder("Đây sẽ là tên đăng nhập");
        usernameField.setBounds(50, 110, 330, 35);
        add(usernameField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 150, 100, 25);
        add(emailLabel);

        emailField = new RoundedTextField(30);
        emailField.setPlaceholder("Đây sẽ là để bạn khôi phục mật khẩu");
        emailField.setBounds(50, 180, 330, 35);
        add(emailField);

        JLabel passLabel = new JLabel("Mật khẩu:");
        passLabel.setBounds(50, 220, 100, 25);
        add(passLabel);

        passField = new RoundedPasswordField(30);
        passField.setPlaceholder("Điền mật khẩu nè");
        passField.setBounds(50, 250, 330, 35);
        add(passField);

        JLabel confirmLabel = new JLabel("Xác nhận mật khẩu:");
        confirmLabel.setBounds(50, 290, 150, 25);
        add(confirmLabel);

        confirmPassField = new RoundedPasswordField(30);
        confirmPassField.setPlaceholder("Điền xác nhận điiii");
        confirmPassField.setBounds(50, 320, 330, 35);
        add(confirmPassField);

        RoundedButton registerBtn = new RoundedButton("Đăng ký");
        registerBtn.setBounds(125, 380, 180, 40);
        add(registerBtn);

        confirmPassField.addActionListener(e -> {
            registerBtn.setPressedTemporarily();
            Timer timer = new Timer(500, evt -> registerBtn.doClick());
            timer.setRepeats(false);
            timer.start();
        });

        registerBtn.addActionListener(e -> controller.register(
            usernameField.getText().trim(),
            emailField.getText().trim(),
            new String(passField.getPassword()),
            new String(confirmPassField.getPassword())
        ));

        setVisible(true);
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    public void closeForm() {
        dispose();
    }
}
