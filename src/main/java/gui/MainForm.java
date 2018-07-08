package gui;

import gui.addDialogs.AddHostDialog;
import gui.addDialogs.AddUserDialog;
import loggers.MainLogger;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;


/**
 * Created by User2 on 03.12.2017.
 */
public class MainForm extends JFrame {
    private JPanel panel1;
    private JTextArea textArea1;
    private JButton usersButton;
    private JButton hostsButton;
    private JButton smsButton;
    private JButton emailButton;
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public MainForm() {
        setSystemLookAndFeel();
        setTitle("Network Status Checker");
        setContentPane(panel1);

        usersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TableForm tableForm = new TableForm(TableForm.TableType.USERS);
            }
        });

        hostsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TableForm tableForm = new TableForm(TableForm.TableType.HOSTS);
            }
        });

        smsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TableForm tableForm = new TableForm(TableForm.TableType.SMSPROVIDERS);
            }
        });

        emailButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TableForm tableForm = new TableForm(TableForm.TableType.EMAILPROVIDERS);
            }
        });

        setVisible(true);
        setSize(500, 400);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        DefaultCaret caret = (DefaultCaret) textArea1.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        MainLogger.setForm(this);
    }

    public static void main(String[] args) {
        JFrame frame = new MainForm();
    }

    private void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            MainLogger.log(Level.WARNING, e.toString());
            e.printStackTrace();
        } catch (InstantiationException e) {
            MainLogger.log(Level.WARNING, e.toString());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            MainLogger.log(Level.WARNING, e.toString());
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            MainLogger.log(Level.WARNING, e.toString());
            e.printStackTrace();
        }
    }

    public void print(String message) {
        textArea1.append(dateFormat.format(new Date()) + ": " + message + "\n");
    }
}
