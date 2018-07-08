package loggers;

import gui.MainForm;
import util.DBUtil;

import javax.swing.*;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by User2 on 02.12.2017.
 */
public class MainLogger {
    private final static Logger logger;
    private final static FileHandler filehandler;
    private static MainForm form;

    static {
        String logRootPath = DBUtil.getSetting("log_directory");
        if (logRootPath == null || logRootPath.equals("")) {
            throw new IllegalArgumentException();
        }

        File logDirectory = new File(logRootPath + System.getProperty("user.name"));
        if (!logDirectory.exists()) logDirectory.mkdirs();

        logger = Logger.getLogger(MainLogger.class.getSimpleName());
        FileHandler tempFileHandler = null;

        try {
            tempFileHandler = new FileHandler(logDirectory.getAbsolutePath() + "\\" + logger.getName() + ".log",
                    100 * 300, 10, true);
        } catch (Exception e) {
            MainLogger.log(Level.WARNING, e.toString());
            e.printStackTrace();
        } finally {
            filehandler = tempFileHandler;
        }

        try {
            logger.addHandler(filehandler);
            logger.setLevel(Level.ALL);
            SimpleFormatter formatter = new SimpleFormatter();
            filehandler.setFormatter(formatter);
        } catch (Exception e) {
            MainLogger.log(Level.WARNING, e.toString());
            e.printStackTrace();
        }
    }

    public static void log(Level level, String message) {
        logger.log(level, message);
        System.out.println(message);
        form.print(message);
    }

    public static void setForm(MainForm form) {
        MainLogger.form = form;
    }
}
