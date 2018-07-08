package util;

import Entity.HostEntity;
import Entity.SettingEntity;
import Entity.UserEntity;
import Entity.notificationProviders.EmailProviderEntity;
import Entity.notificationProviders.NotificationProvider;
import Entity.notificationProviders.SmsProviderEntity;
import loggers.MainLogger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by User2 on 30.11.2017.
 */
public class DBUtil {
    private static final SessionFactory ourSessionFactory;
    private static final Session session;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();
            ourSessionFactory = configuration.buildSessionFactory();
            session = ourSessionFactory.openSession();
        } catch (Throwable ex) {
            MainLogger.log(Level.WARNING, ex.toString());
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getSession() throws HibernateException {
        return session;
    }

    public static List<UserEntity> getUsers() {
        return session.createQuery("from UserEntity").list();
    }

    public static List<HostEntity> getHosts() {
        return session.createQuery("from HostEntity").list();
    }

    public static List<NotificationProvider> getNotificationProviders() {
        List<NotificationProvider> result = new LinkedList<NotificationProvider>();
        result.addAll(getSmsProviders());
        result.addAll(getEmailProviders());

        return result;
    }

    public static List<SmsProviderEntity> getSmsProviders() {
        List<SmsProviderEntity> result = session.createQuery("from SmsProviderEntity ").list();
        return result;
    }

    public static List<EmailProviderEntity> getEmailProviders() {
        List<EmailProviderEntity> result = session.createQuery("from EmailProviderEntity ").list();
        return result;
    }

    public static void writeEntity(Object object, boolean evict) {
        session.beginTransaction();
        session.save(object);
        session.getTransaction().commit();
        if (evict) evictEntity(object);
    }

    public static void evictEntity(Object object) {
        getSession().evict(object);
    }

    public static <T> T getEntityById(Class<T> clazz, Serializable id) {
        return (T) getSession().get(clazz, id);
    }

    public static <T> T getEntityByNameField(Class<T> clazz, String name) {
        Criteria criteria = session.createCriteria(clazz);
        Object result = criteria.add(Restrictions.eq("name", name)).uniqueResult();

        return (T) result;
    }

    public static String getSetting(String settingName) {
        SettingEntity setting = null;

        try {
            setting = session.get(SettingEntity.class, settingName);
        } catch (Exception e) {
            MainLogger.log(Level.SEVERE, e.toString());
        } finally {
            return setting.getValue();
        }
    }
}
