package gui.addDialogs;

import Entity.notificationProviders.SmsProviderEntity;
import gui.TableForm;
import util.DBUtil;

import javax.swing.*;
import java.awt.event.*;

public class AddSmsProviderDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;
    private JTextField textField2;
    private JPasswordField passwordField1;
    private JTextField textField3;
    private JTextField textField4;
    private JCheckBox checkBox;
    private TableForm parentForm;
    private SmsProviderEntity smsProvider;

    public AddSmsProviderDialog(TableForm parentForm, SmsProviderEntity smsProvider) {
        this.parentForm = parentForm;
        setContentPane(contentPane);
        setTitle("Добавление SMS-провайдера");
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

        if (smsProvider != null) {
            this.smsProvider = smsProvider;
            textField1.setText(smsProvider.getName());
            textField2.setText(smsProvider.getLogin());
            textField3.setText(smsProvider.getSignature());
            textField4.setText(smsProvider.getRequest());
            passwordField1.setText(smsProvider.getPassword());
            checkBox.setSelected(smsProvider.getActive() == 1);
        } else this.smsProvider = null;
    }

    private void onOK() {
        if (textField1.getText() == null || textField1.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Введите имя", "Неверные данные", JOptionPane.INFORMATION_MESSAGE);
        } else if (textField2.getText() == null || textField2.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Введите логин", "Неверные данные", JOptionPane.INFORMATION_MESSAGE);
        } else if (passwordField1.getText() == null || passwordField1.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Введите пароль", "Неверные данные", JOptionPane.INFORMATION_MESSAGE);
        } else if (textField4.getText() == null || textField4.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Введите шаблон GET-запроса", "Неверные данные", JOptionPane.INFORMATION_MESSAGE);
        } else {
            boolean isNew = false;

            if (smsProvider == null) {
                isNew = true;
                smsProvider = new SmsProviderEntity();
            }

            smsProvider.setName(textField1.getText());
            smsProvider.setLogin(textField2.getText());
            smsProvider.setPassword(passwordField1.getText());
            smsProvider.setSignature(textField3.getText());
            smsProvider.setRequest(textField4.getText());
            smsProvider.setActive(checkBox.isSelected() ? (byte) 1 : (byte) 0);
            DBUtil.writeEntity(smsProvider, isNew);

            if (isNew)
                parentForm.addRow(new Object[]{smsProvider.getName(), smsProvider.getLogin(), smsProvider.getPassword().replaceAll(".", "*"), smsProvider.getRequest(), smsProvider.getActive() == 1 ? true : false});

            dispose();
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
//        AddSmsProviderDialog dialog = new AddSmsProviderDialog();
//        dialog.pack();
//        dialog.setVisible(true);
//        System.exit(0);
    }
}
