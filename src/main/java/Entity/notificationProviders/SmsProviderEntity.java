package Entity.notificationProviders;

import javax.persistence.*;

import Entity.HostEntity;
import Entity.UserEntity;
import loggers.MainLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;

/**
 * Created by User2 on 24.11.2017.
 */
@Entity
@Table(name = "sms_provider", schema = "nscdb")
public class SmsProviderEntity extends NotificationProvider {
    private String name;
    private String login;
    private String password;
    private String request;
    private String signature;
    private byte active;
    private int id;

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
    @Column(name = "password", nullable = false, length = 45)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "request", nullable = false, length = 150)
    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    @Basic
    @Column(name = "signature", nullable = true, length = 45)
    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Basic
    @Column(name = "active", nullable = false)
    public byte getActive() {
        return active;
    }

    public void setActive(byte active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SmsProviderEntity that = (SmsProviderEntity) o;

        if (active != that.active) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (login != null ? !login.equals(that.login) : that.login != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (request != null ? !request.equals(that.request) : that.request != null) return false;
        if (signature != null ? !signature.equals(that.signature) : that.signature != null) return false;

        return true;
    }

    public boolean sendNotification(UserEntity user, HostEntity host) {
        boolean sendSuccess = false;

        if (user.getPhone() != null && NotificationProvider.checkValid(DataType.PHONE, user.getPhone())) {
            try {
                HttpURLConnection con = (HttpURLConnection) new URL(String.format(request, login, password, user.getPhone(),
                        host.getName() + " " + (host.getIsOnline() == 1 ? "онлайн" : "оффлайн"))).openConnection();

                con.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                if (response.toString().startsWith("OK")) {
                    sendSuccess = true;
                }

                MainLogger.log(sendSuccess ? Level.FINE : Level.WARNING, response.toString());
                return sendSuccess;
            } catch (IOException e) {
                MainLogger.log(Level.WARNING, e.toString());
                e.printStackTrace();
            }
        } else {
            MainLogger.log(Level.WARNING, "Wrong phone for user " + user.getName());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (request != null ? request.hashCode() : 0);
        result = 31 * result + (signature != null ? signature.hashCode() : 0);
        result = 31 * result + (int) active;
        return result;
    }

    @Override
    public String toString() {
        return name;
    }

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
