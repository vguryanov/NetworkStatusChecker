package gui.addDialogs;

import Entity.HostEntity;
import Entity.notificationProviders.NotificationProvider;
import gui.TableForm;
import util.DBUtil;

import javax.swing.*;
import java.awt.event.*;

public class AddHostDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;
    private JTextField textField2;
    private JCheckBox checkBox;
    private JTextField textField3;
    private JCheckBox isWebPageCheckBox;
    private TableForm parentForm;
    private HostEntity host;

    public AddHostDialog(TableForm parentForm, HostEntity host) {
        this.parentForm = parentForm;
        setContentPane(contentPane);
        setTitle("Добавление хоста");
        setModal(true);
        setLocationRelativeTo(this.getParent());
        setSize(300, 200);
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

        isWebPageCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textField3.setEnabled(isWebPageCheckBox.isSelected());
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

        if (host != null) {
            this.host = host;
            textField1.setText(host.getName());
            textField2.setText(host.getIpAddress());
            checkBox.setSelected(host.getIsActive() == 1);
            isWebPageCheckBox.setSelected(host.getIsWebpage() == 1);
            textField3.setText(host.getContainedWebclass());
        } else this.host = null;

        textField3.setEnabled(isWebPageCheckBox.isSelected());
    }

    private void onOK() {
        if (!NotificationProvider.checkValid(NotificationProvider.DataType.IPORDOMAIN, textField2.getText())) {
            JOptionPane.showMessageDialog(this, "Укажите верный адрес", "Неверные данные", JOptionPane.INFORMATION_MESSAGE);
        } else if (textField1.getText() == null || textField1.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Введите имя", "Неверные данные", JOptionPane.INFORMATION_MESSAGE);
        } else if (isWebPageCheckBox.isSelected() == true && (textField3.getText() == null || textField3.getText().equals(""))) {
            JOptionPane.showMessageDialog(this, "Введите имя содержащегося на странице веб-класса",
                    "Неверные данные", JOptionPane.INFORMATION_MESSAGE);
        } else {
            boolean isNew = false;

            if (host == null) {
                isNew = true;
                host = new HostEntity();
            }

            host.setName(textField1.getText());
            host.setIpAddress(textField2.getText());
            host.setIsActive(checkBox.isSelected() ? (byte) 1 : (byte) 0);
            host.setIsWebpage(isWebPageCheckBox.isSelected() ? (byte) 1 : (byte) 0);
            if (textField3.getText() != null) host.setContainedWebclass(textField3.getText());
            DBUtil.writeEntity(host, isNew);

            if (isNew)
                parentForm.addRow(new Object[]{host.getName(), host.getIpAddress(), host.getIsWebpage() == 1, host.getIsActive() == 1});

            dispose();
        }
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        AddHostDialog dialog = new AddHostDialog(null, null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
