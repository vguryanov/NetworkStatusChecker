package Entity.notificationProviders;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.*;

import Entity.HostEntity;
import Entity.UserEntity;
import loggers.MainLogger;

import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Created by User2 on 14.12.2017.
 */
@Entity
@Table(name = "email_provider", schema = "nscdb")
public class EmailProviderEntity extends NotificationProvider {
    private String name;
    private String login;
    private String pass;
    private String smtpServer;
    private String smtpPort;
    private String replyTo;
    private String subject;
    private byte active;
    private int id;
    private String signature;

    @Basic
    @Column(name = "name", nullable = false, length = 20)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "login", nullable = false, length = 45)
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Basic
    @Column(name = "pass", nullable = false, length = 45)
    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Basic
    @Column(name = "smtp_server", nullable = false, length = 45)
    public String getSmtpServer() {
        return smtpServer;
    }

    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    @Basic
    @Column(name = "smtp_port", nullable = false, length = 5)
    public String getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(String smtpPort) {
        this.smtpPort = smtpPort;
    }

    @Basic
    @Column(name = "reply_to", nullable = false, length = 45)
    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    @Basic
    @Column(name = "subject", nullable = true, length = 45)
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Basic
    @Column(name = "active", nullable = false)
    public byte getActive() {
        return active;
    }

    public void setActive(byte active) {
        this.active = active;
    }

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "signature", nullable = false, length = 45)
    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmailProviderEntity that = (EmailProviderEntity) o;

        if (active != that.active) return false;
        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (login != null ? !login.equals(that.login) : that.login != null) return false;
        if (pass != null ? !pass.equals(that.pass) : that.pass != null) return false;
        if (smtpServer != null ? !smtpServer.equals(that.smtpServer) : that.smtpServer != null) return false;
        if (smtpPort != null ? !smtpPort.equals(that.smtpPort) : that.smtpPort != null) return false;
        if (replyTo != null ? !replyTo.equals(that.replyTo) : that.replyTo != null) return false;
        if (subject != null ? !subject.equals(that.subject) : that.subject != null) return false;
        if (signature != null ? !signature.equals(that.signature) : that.signature != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (pass != null ? pass.hashCode() : 0);
        result = 31 * result + (smtpServer != null ? smtpServer.hashCode() : 0);
        result = 31 * result + (smtpPort != null ? smtpPort.hashCode() : 0);
        result = 31 * result + (replyTo != null ? replyTo.hashCode() : 0);
        result = 31 * result + (subject != null ? subject.hashCode() : 0);
        result = 31 * result + (int) active;
        result = 31 * result + id;
        result = 31 * result + (signature != null ? signature.hashCode() : 0);
        return result;
    }

    @Override
    public boolean sendNotification(UserEntity user, HostEntity host) {
        if (user.getEmail() != null && NotificationProvider.checkValid(DataType.EMAIL, user.getEmail())) {
            MainLogger.log(Level.FINE, "SSLEmail Start");
//            System.out.println("SSLEmail Start");
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpServer); //SMTP Host
            props.put("mail.smtp.socketFactory.port", smtpPort); //SSL Port
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
            props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
            props.put("mail.smtp.port", smtpPort); //SMTP Port

            Authenticator auth = new Authenticator() {
                //override the getPasswordAuthentication method
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(login, pass);
                }
            };

            Session session = Session.getDefaultInstance(props, auth);
            MainLogger.log(Level.FINE, "Session created");
//            System.out.println("Session created");

            try {
                MimeMessage msg = new MimeMessage(session);

                msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
                msg.addHeader("format", "flowed");
                msg.addHeader("Content-Transfer-Encoding", "8bit");

                msg.setFrom(new InternetAddress(signature));
                msg.setReplyTo(InternetAddress.parse(replyTo, false));
                msg.setSubject((subject == null ? "" : subject + " ") +
                        host.getName() + " " + (host.getIsOnline() == 1 ? "онлайн" : "оффлайн"), "UTF-8");
                msg.setText(host.getName() + " is " + (host.getIsOnline() == 1 ? "онлайн" : "оффлайн"), "UTF-8");
                msg.setSentDate(new Date());
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail(), false));

                MainLogger.log(Level.FINE, "Message is ready");
//                System.out.println("Message is ready");
                Transport.send(msg);

                MainLogger.log(Level.FINE, "EMail Sent Successfully");
//                System.out.println("EMail Sent Successfully");
                return true;
            } catch (Exception e) {
                MainLogger.log(Level.SEVERE, e.toString());
                e.printStackTrace();
            }
        } else {
            MainLogger.log(Level.WARNING, "Wrong email for user " + user.getName());
        }

        return false;
    }
}
