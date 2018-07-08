package gui.addDialogs;

import Entity.UserEntity;
import Entity.notificationProviders.NotificationProvider;
import gui.TableForm;
import util.DBUtil;

import javax.swing.*;
import java.awt.event.*;

public class AddUserDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JCheckBox checkBox;
    private TableForm parentForm;
    private UserEntity user;

    public AddUserDialog(TableForm parentForm, UserEntity user) {
        this.parentForm = parentForm;
        setContentPane(contentPane);
        setTitle("Добавление пользователя");
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

        if (user != null) {
            this.user = user;
            textField1.setText(user.getName());
            textField2.setText(user.getPhone());
            textField3.setText(user.getEmail());
            checkBox.setSelected(user.getIsActive() == 1);
        } else this.user = null;
    }

    private void onOK() {
        if (textField1.getText() == null || textField1.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Введите имя пользователя", "Неверные данные", JOptionPane.INFORMATION_MESSAGE);
        } else if (!NotificationProvider.checkValid(NotificationProvider.DataType.EMAIL, textField3.getText()) &&
                !NotificationProvider.checkValid(NotificationProvider.DataType.PHONE, textField2.getText())) {
            JOptionPane.showMessageDialog(this, "Укажите верный номер телефона или почтовый адрес", "Неверные данные", JOptionPane.INFORMATION_MESSAGE);
        } else {
            boolean isNew = false;

            if (user == null) {
                isNew = true;
                user = new UserEntity();
            }

            user.setName(textField1.getText());
            if (NotificationProvider.checkValid(NotificationProvider.DataType.PHONE, textField2.getText()))
                user.setPhone(textField2.getText());
            if (NotificationProvider.checkValid(NotificationProvider.DataType.EMAIL, textField3.getText()))
                user.setEmail(textField3.getText());
            user.setIsActive(checkBox.isSelected() ? (byte) 1 : (byte) 0);
            DBUtil.writeEntity(user, isNew);

            if (isNew)
                parentForm.addRow(new Object[]{user.getName(), user.getPhone(), user.getEmail(), user.getIsActive() == 1});

            dispose();
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
//        AddUserDialog dialog = new AddUserDialog();
//        dialog.pack();
//        dialog.setVisible(true);
//        System.exit(0);
    }
}
