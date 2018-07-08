package Entity;

import loggers.MainLogger;
import util.WebUtil;

import javax.persistence.*;
import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;

/**
 * Created by User2 on 22.11.2017.
 */
@Entity
@Table(name = "host", schema = "nscdb")
public class HostEntity {
    private String name;
    private String ipAddress;
    private byte isOnline;
    private byte isActive;
    private int id;
    private byte isWebpage;
    private String containedWebclass;

    @Basic
    @Column(name = "name", nullable = false, length = 30)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "ip_address", nullable = false, length = 45)
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Basic
    @Column(name = "is_online", nullable = false)
    public byte getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(byte isOnline) {
        this.isOnline = isOnline;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HostEntity that = (HostEntity) o;

        if (isOnline != that.isOnline) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (ipAddress != null ? !ipAddress.equals(that.ipAddress) : that.ipAddress != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (ipAddress != null ? ipAddress.hashCode() : 0);
        result = 31 * result + (int) isOnline;
        return result;
    }

    @Override
    public String toString() {
        return name + " " + ipAddress + " " + (isOnline == 1 ? "online" : "offline");
    }

    public boolean checkStatus() {
        if (getIsWebpage() == 1) return WebUtil.pageContainsClassElements(getIpAddress(), getContainedWebclass());
        else return WebUtil.ping(getIpAddress());
    }

    @Basic
    @Column(name = "is_active", nullable = false)
    public byte getIsActive() {
        return isActive;
    }

    public void setIsActive(byte isActive) {
        this.isActive = isActive;
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
    @Column(name = "is_webpage", nullable = false)
    public byte getIsWebpage() {
        return isWebpage;
    }

    public void setIsWebpage(byte isWebpage) {
        this.isWebpage = isWebpage;
    }

    @Basic
    @Column(name = "contained_webclass", length = 45)
    public String getContainedWebclass() {
        return containedWebclass;
    }

    public void setContainedWebclass(String containedWebclass) {
        this.containedWebclass = containedWebclass;
    }
}
