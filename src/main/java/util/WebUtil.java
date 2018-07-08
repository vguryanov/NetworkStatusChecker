package util;

import loggers.MainLogger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;

/**
 * Created by User2 on 08.01.2018.
 */
public class WebUtil {
    public static boolean pageContainsClassElements(String url, String className) {
        Document doc;
        Elements elements = null;
        if (!url.startsWith("http")) url = "http://" + url;

        try {
            doc = Jsoup.connect(url).get();
            elements = doc.getElementsByClass(className);
        } catch (IOException e) {
            MainLogger.log(Level.WARNING, e.toString());
        }

//        System.out.println(elements.size());
        return elements != null && elements.size() > 0;
    }

    public static boolean ping(String address) {
        boolean online = false;

        try {
            online = InetAddress.getByName(address).isReachable(4000);
        } catch (IOException e) {
            MainLogger.log(Level.SEVERE, e.toString());
        } finally {
            return online;
        }
    }

    public static boolean isLocalConnectionOk() {
        boolean net_card = false;
        boolean google = false;
        boolean yandex = false;
        boolean result;

        try {
            net_card = InetAddress.getByName("127.0.0.1").isReachable(4000);
            if (net_card) {
                google = InetAddress.getByName("www.google.com").isReachable(4000);
                yandex = InetAddress.getByName("www.ya.ru").isReachable(4000);
            }
        } catch (IOException e) {
            MainLogger.log(Level.WARNING, e.toString());
        } finally {
            result = net_card && (google || yandex);
            MainLogger.log(Level.INFO, "Local Internet connection is " + (result ? "ok" : "lost"));
            return result;
        }
    }
}
