package org.example;

public class Courier {

    public String login;
    public String password;
    public String firstName;


    public Courier(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }

    public Courier(String login, String password){
        this.login = login;
        this.password = password;
    }
}
