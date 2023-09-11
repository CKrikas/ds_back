package org.ck.ds.entities;

public class UserLoginRequest {
    private int amka;
    private String password;

    public int getAmka() {
        return amka;
    }

    public void setAmka(int amka) {
        this.amka = amka;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
