import java.util.*;

import java.net.*;
import java.io.*;

class User {    // 储存用户信息
    private String name;
    private String md5;
    private String pass;
    private String stat;
    private Socket sock;
    private PrintWriter pw;

    public User(String name) {
        setName(name);
        setStat("offline");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) { this.pass=pass; }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) { this.stat=stat; }

    public String getMD5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5=md5;
    }

    public Socket getSock(){
        return sock;
    }

    public void setSock(Socket sock){
        this.sock=sock;
    }

    public PrintWriter getPw(){
        return pw;
    }

    public void setPw(PrintWriter pw){
        this.pw=pw;
    }
}



