package gui;

import Entity.HostEntity;
import Entity.UserEntity;
import Entity.notificationProviders.EmailProviderEntity;
import Entity.notificationProviders.SmsProviderEntity;
import gui.addDialogs.AddEmailProviderDialog;
import gui.addDialogs.AddHostDialog;
import gui.addDialogs.AddSmsProviderDialog;
import gui.addDialogs.AddUserDialog;
import loggers.MainLogger;
import org.apache.commons.lang3.ArrayUtils;
import util.DBUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class TableForm {
    private TableType tableType;
    private static Map<TableType, String[]> typeColumns = new HashMap<TableType, String[]>();
    private static Map<TableType, Class> classesForTabletypes = new HashMap<TableType, Class>();
    private MyTableModel model;
    private JDialog frame;

    static {
        classesForTabletypes.put(TableType.USERS, UserEntity.class);
        classesForTabletypes.put(TableType.HOSTS, HostEntity.class);
        classesForTabletypes.put(TableType.SMSPROVIDERS, SmsProviderEntity.class);
        classesForTabletypes.put(TableType.EMAILPROVIDERS, EmailProviderEntity.class);

        typeColumns.put(TableType.USERS, new String[]{"Имя", "Телефон", "e-mail", "Активен"});
        typeColumns.put(TableType.HOSTS, new String[]{"Название", "Адрес", "Веб-страница", "Активен"});
        typeColumns.put(TableType.SMSPROVIDERS, new String[]{"Имя", "Логин", "Пароль", "Шаблон GET-запроса", "Активен"});
        typeColumns.put(TableType.EMAILPROVIDERS, new String[]{"Имя", "Логин", "Пароль", "SMTP-сервер", "SMTP-port (SSL)", "Активен"});
    }

    public static enum TableType {
        USERS, HOSTS, SMSPROVIDERS, EMAILPROVIDERS
    }

    public static void main(String[] args) {
        TableForm form = new TableForm(TableType.HOSTS);
    }

    public TableForm(TableType type) {
        tableType = type;
        startUI();
    }

    private void startUI() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    MainLogger.log(Level.SEVERE, ex.toString());
                    ex.printStackTrace();
                }

                frame = new JDialog();
                JTable table = new JTable(loadModel(frame));

                table.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent mouseEvent) {
                        JTable table = (JTable) mouseEvent.getSource();
                        Point point = mouseEvent.getPoint();
                        int row = table.rowAtPoint(point);
//                        int column = table.columnAtPoint(point);

                        if (mouseEvent.getClickCount() == 2) {
                            showDialog(DBUtil.getEntityByNameField(classesForTabletypes.get(tableType), (String) table.getModel().getValueAt(row, 0)));
                            updateModel(table);
                        }
                    }
                });

                frame.setTitle(getTypeTitle());
                frame.setModal(true);
                frame.add(new JScrollPane(table));
                setToolbar(frame);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    private void setToolbar(JDialog frame) {
        JToolBar toolbar = new JToolBar();
        toolbar.setRollover(true);

        JButton button = new JButton("Добавить");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showDialog(null);
            }
        });
        toolbar.add(button);

        Container contentPane = frame.getContentPane();
        contentPane.add(toolbar, BorderLayout.NORTH);
    }

    private String getTypeTitle() {
        switch (tableType) {
            case USERS:
                return "Пользователи";

            case HOSTS:
                return "Хосты";

            case SMSPROVIDERS:
                return "SMS-провайдеры";

            case EMAILPROVIDERS:
                return "Email-провайдеры";

            default:
                throw new IllegalArgumentException();
        }
    }

    private void showDialog(Object object) {
        switch (tableType) {
            case USERS:
                AddUserDialog addUserDialog = new AddUserDialog(this, (UserEntity) object);
                addUserDialog.pack();
                addUserDialog.setVisible(true);
                break;

            case HOSTS:
                AddHostDialog addHostDialog = new AddHostDialog(this, (HostEntity) object);
                addHostDialog.pack();
                addHostDialog.setVisible(true);
                break;

            case SMSPROVIDERS:
                AddSmsProviderDialog addSmsProviderDialog = new AddSmsProviderDialog(this, (SmsProviderEntity) object);
                addSmsProviderDialog.pack();
                addSmsProviderDialog.setVisible(true);
                break;

            case EMAILPROVIDERS:
                AddEmailProviderDialog addEmailProviderDialog = new AddEmailProviderDialog(this, (EmailProviderEntity) object);
                addEmailProviderDialog.pack();
                addEmailProviderDialog.setVisible(true);
                break;
        }
    }

    private MyTableModel loadModel(Component parent) {
        model = new MyTableModel(tableType);

        switch (tableType) {
            case USERS:
                for (UserEntity user : DBUtil.getUsers()) {
//                    ids.put(model.getRowCount() + 1, user.getId());
                    model.addRow(new Object[]{user.getName(), user.getPhone(), user.getEmail(), user.getIsActive() == 1});
                }
                break;
            case HOSTS:
                for (HostEntity host : DBUtil.getHosts()) {
//                    ids.put(model.getRowCount() + 1, host.getId());
                    model.addRow(new Object[]{host.getName(), host.getIpAddress(), host.getIsWebpage() == 1, host.getIsActive() == 1,});
                }
                break;
            case SMSPROVIDERS:
                for (SmsProviderEntity smsProvider : DBUtil.getSmsProviders()) {
//                    ids.put(model.getRowCount() + 1, smsProvider.getId());
                    model.addRow(new Object[]{smsProvider.getName(), smsProvider.getLogin(), smsProvider.getPassword().replaceAll(".", "*"),
                            smsProvider.getRequest(), smsProvider.getActive() == 1});
                }
                break;
            case EMAILPROVIDERS:
                for (EmailProviderEntity emailProvider : DBUtil.getEmailProviders()) {
//                    ids.put(model.getRowCount() + 1, emailProvider.getId());
                    model.addRow(new Object[]{emailProvider.getName(), emailProvider.getLogin(), emailProvider.getPass().replaceAll(".", "*"),
                            emailProvider.getSmtpServer(), emailProvider.getSmtpPort(), emailProvider.getActive() == 1});
                }
                break;
        }

        return model;
    }

    private void updateModel(JTable table) {
        ((DefaultTableModel) table.getModel()).setRowCount(0);
        table.setModel(loadModel(frame));
    }

    public void addRow(Object[] objects) {
        model.addRow(objects);
    }

    public class MyTableModel extends DefaultTableModel {
        private int checkboxColumnIndex;

        public MyTableModel(TableType tableType) {
            super(TableForm.typeColumns.get(tableType), 0);
            checkboxColumnIndex = typeColumns.get(tableType).length - 1;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (tableType == TableType.HOSTS && columnIndex == typeColumns.get(tableType).length - 2)
                return Boolean.class;
            return columnIndex == checkboxColumnIndex ? Boolean.class : String.class;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

//        @Override
//        public void setValueAt(Object aValue, int row, int column) {
//            System.out.println(aValue);
//            Vector rowData = (Vector) getDataVector().get(row);
//            rowData.set(column, aValue);
//            fireTableCellUpdated(row, column);
//
//            switch (tableType) {
//                case USERS:
//                    UserEntity user = DBUtil.getEntityById(UserEntity.class, ids.get(rowData.get(0)));
//                    user.setName((String) rowData.get(1));
//                    user.setPhone((String) rowData.get(2));
//                    user.setEmail((String) rowData.get(3));
//                    user.setIsActive((Boolean) (rowData.get(4)) ? (byte) 1 : (byte) 0);
//                    DBUtil.writeEntity(user, false);
//                    break;
//                case HOSTS:
//                    if (!NotificationProvider.checkValid(NotificationProvider.DataType.IPORDOMAIN, (String) rowData.get(2))) {
//                        JOptionPane.showMessageDialog(parent, "Укажите верный IPv4 адрес", "Неверные данные", JOptionPane.INFORMATION_MESSAGE);
//                    } else if (rowData.get(1) == null || (rowData.get(1)).equals("")) {
//                        JOptionPane.showMessageDialog(parent, "Введите имя", "Неверные данные", JOptionPane.INFORMATION_MESSAGE);
//                    } else {
//                        HostEntity host = DBUtil.getEntityById(HostEntity.class, ids.get(rowData.get(0)));
//                        host.setName((String) rowData.get(1));
//                        host.setIpAddress((String) rowData.get(2));
//                        host.setIsActive((Boolean) (rowData.get(3)) ? (byte) 1 : (byte) 0);
//                        DBUtil.writeEntity(host, false);
//                    }
//                    break;
//                case SMSPROVIDERS:
//                    SmsProviderEntity smsProvider = DBUtil.getEntityById(SmsProviderEntity.class, ids.get(rowData.get(0)));
//                    smsProvider.setName((String) rowData.get(1));
//                    smsProvider.setLogin((String) rowData.get(2));
//                    smsProvider.setPassword((String) rowData.get(3));
//                    smsProvider.setRequest((String) rowData.get(4));
//                    smsProvider.setActive((Boolean) (rowData.get(5)) ? (byte) 1 : (byte) 0);
//                    DBUtil.writeEntity(smsProvider, false);
//                    rowData.set(3, ((String) rowData.get(3)).replaceAll(".", "*"));
//                    break;
//                case EMAILPROVIDERS:
//                    EmailProviderEntity emailProvider = DBUtil.getEntityById(EmailProviderEntity.class, ids.get(rowData.get(0)));
//                    emailProvider.setName((String) rowData.get(1));
//                    emailProvider.setLogin((String) rowData.get(2));
//                    emailProvider.setPass((String) rowData.get(3));
//                    emailProvider.setSignature((String) rowData.get(2));
//                    emailProvider.setReplyTo((String) rowData.get(2));
//                    emailProvider.setSmtpServer((String) rowData.get(4));
//                    emailProvider.setSmtpPort((String) rowData.get(5));
//                    emailProvider.setActive((Boolean) (rowData.get(6)) ? (byte) 1 : (byte) 0);
//                    DBUtil.writeEntity(emailProvider, false);
//                    rowData.set(3, ((String) rowData.get(3)).replaceAll(".", "*"));
//                    break;
//            }
//        }
    }
}