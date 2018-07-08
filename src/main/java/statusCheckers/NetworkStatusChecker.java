package statusCheckers;

import Entity.HostEntity;
import Entity.notificationProviders.NotificationProvider;
import loggers.MainLogger;
import util.DBUtil;
import util.WebUtil;

import java.util.List;
import java.util.logging.Level;

/**
 * Created by User2 on 30.11.2017.
 */
public class NetworkStatusChecker implements Runnable {
    private static int timeout;
    private static NetworkStatusChecker networkStatusChecker;

    private NetworkStatusChecker() {

    }

    public static NetworkStatusChecker getInstance() {
        if (networkStatusChecker == null) networkStatusChecker = new NetworkStatusChecker();
        return networkStatusChecker;
    }

    static {
        timeout = Integer.parseInt(DBUtil.getSetting("checker_timeout"));
        if (timeout <= 0) {
            MainLogger.log(Level.SEVERE, "checker_timeout load error");
            throw new IllegalArgumentException();
        }
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (WebUtil.isLocalConnectionOk()) {
                for (HostEntity host : DBUtil.getHosts()) {
                    if (host.getIsActive() == 1) {
                        if (host.checkStatus()) {
                            MainLogger.log(Level.INFO, host.getName() + " online");
                            if (host.getIsOnline() == 0) {
                                host.setIsOnline((byte) 1);
                                DBUtil.writeEntity(host, false);
                                NotificationProvider.notifyAllUsers(host);
                            }
                        } else {
                            MainLogger.log(Level.INFO, host.getName() + " offline");
                            if (host.getIsOnline() == 1) {
                                host.setIsOnline((byte) 0);
                                DBUtil.writeEntity(host, false);
                                NotificationProvider.notifyAllUsers(host);
                            }
                        }
                    }
                }
            }

            try {
                Thread.currentThread().sleep(1000 * timeout);
            } catch (InterruptedException e) {
                MainLogger.log(Level.SEVERE, e.toString());
            }
        }
    }
}
