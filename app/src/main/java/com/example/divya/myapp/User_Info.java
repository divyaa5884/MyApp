package com.example.divya.myapp;

/**
 * Created by divya on 24/2/18.
 */

class User_Info {
    String name,username,eid,mob,pwd;
    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEid() {
        return eid;
    }

    public String getMob() {
        return mob;
    }

    public String getPwd() {
        return pwd;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public void setMob(String mob) {
        this.mob = mob;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String toString() {
        return "User_Info [name=" + name + ", username=" + username + ", eid=" + eid + ", mob=" + mob +"]";
    }
}
