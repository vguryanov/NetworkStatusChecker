import gui.MainForm;
import statusCheckers.NetworkStatusChecker;

/**
 * Created by User2 on 20.11.2017.
 */
public class Main {
        public static void main(final String[] args) throws Exception {
        MainForm mainForm = new MainForm();
        mainForm.setVisible(true);

        Thread networkStatusChecker = new Thread(NetworkStatusChecker.getInstance());
        networkStatusChecker.start();
    }
}