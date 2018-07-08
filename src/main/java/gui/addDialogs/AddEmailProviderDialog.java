package gui.addDialogs;

import Entity.notificationProviders.EmailProviderEntity;
import Entity.notificationProviders.NotificationProvider;
import gui.TableForm;
import util.DBUtil;

import javax.swing.*;
import java.awt.event.*;

public class AddEmailProviderDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;
    private JTextField textField2;
    private JPasswordField passwordField1;
    private JTextField textField3;
    private JTextField textField4;
    private JCheckBox checkBox;
    private JTextField textField5;
    private TableForm parentForm;
    private EmailProviderEntity emailProvider;

    public AddEmailProviderDialog(TableForm parentForm, EmailProviderEntity emailProvider) {
        this.parentForm = parentForm;
        setContentPane(contentPane);
        setTitle("Добавление email-провайдера");
        setModal(true);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        if (emailProvider != null) {
            this.emailProvider = emailProvider;
            textField1.setText(emailProvider.getName());
            textField2.setText(emailProvider.getLogin());
            textField3.setText(emailProvider.getSmtpServer());
            textField4.setText(emailProvider.getSmtpPort());
            textField5.setText(emailProvider.getSubject());
            passwordField1.setText(emailProvider.getPass());
            checkBox.setSelected(emailProvider.getActive() == 1);
        } else this.emailProvider = null;
    }

    private void onOK() {
        if (!NotificationProvider.checkValid(NotificationProvider.DataType.EMAIL, textField2.getText())) {
            JOptionPane.showMessageDialog(this, "Укажите верный логин", "Неверные данные", JOptionPane.INFORMATION_MESSAGE);
        } else if (textField1.getText() == null || textField1.getText().equals(""))
            JOptionPane.showMessageDialog(this, "Укажите имя", "Неверные данные", JOptionPane.INFORMATION_MESSAGE);
        else if (passwordField1.getText() == null || passwordField1.getText().equals(""))
            JOptionPane.showMessageDialog(this, "Введите пароль", "Неверные данные", JOptionPane.INFORMATION_MESSAGE);
        else if (textField3.getText() == null || textField3.getText().equals(""))
            JOptionPane.showMessageDialog(this, "Укажите SMTP-сервер", "Неверные данные", JOptionPane.INFORMATION_MESSAGE);
        else if (textField4.getText() == null || textField4.getText().equals(""))
            JOptionPane.showMessageDialog(this, "Укажите SMTP-порт", "Неверные данные", JOptionPane.INFORMATION_MESSAGE);
        else {
            boolean isNew = false;

            if (emailProvider == null) {
                isNew = true;
                emailProvider = new EmailProviderEntity();
            }

            emailProvider.setName(textField1.getText());
            emailProvider.setLogin(textField2.getText());
            emailProvider.setSignature(textField2.getText());
            emailProvider.setReplyTo(textField2.getText());
            emailProvider.setPass(passwordField1.getText());
            emailProvider.setSubject(textField5.getText());
            emailProvider.setSmtpServer(textField3.getText());
            emailProvider.setSmtpPort(textField4.getText());
            emailProvider.setActive(checkBox.isSelected() ? (byte) 1 : (byte) 0);
            DBUtil.writeEntity(emailProvider, isNew);

            if (isNew) parentForm.addRow(new Object[]{emailProvider.getName(), emailProvider.getLogin(),
                    emailProvider.getPass().replaceAll(".", "*"), emailProvider.getSmtpServer(), emailProvider.getSmtpPort(), emailProvider.getActive() == 1 ? true : false});

            dispose();
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
//        AddEmailProviderDialog dialog = new AddEmailProviderDialog();
//        dialog.pack();
//        dialog.setVisible(true);
//        System.exit(0);
    }
}
