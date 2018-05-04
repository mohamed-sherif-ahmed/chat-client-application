package main;

import java.util.StringTokenizer;

public class User {
    private String id;
    private String name;
    private String ip;
    private String rx;
    private String tx;
    private String status;

    public User(String id, String name, String ip, String rx, String tx, String status) {
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.rx = rx;
        this.tx = tx;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRx() {
        return rx;
    }

    public void setRx(String rx) {
        this.rx = rx;
    }

    public String getTx() {
        return tx;
    }

    public void setTx(String tx) {
        this.tx = tx;
    }
}
