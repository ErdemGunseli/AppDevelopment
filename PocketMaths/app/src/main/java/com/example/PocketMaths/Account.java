package com.example.PocketMaths;

import java.util.ArrayList;
import java.util.Locale;

public class Account {
    // A class to hold the account details.

    private int id;

    private String parentName, studentName, email, password, pin;

    private String accountType;

    public static final String Member = "Member";
    public static final String Guest = "Guest";


    public Account(int id, String parentName, String studentName, String email, String password, String pin) {
        this.id = id;
        this.parentName = parentName.toUpperCase(Locale.ROOT);
        this.studentName = studentName.toUpperCase(Locale.ROOT);
        this.email = email.toLowerCase(Locale.ROOT);
        this.password = password;
        this.pin = pin;
        this.accountType = Member;
    }

    public Account(){
        this.accountType = Guest;
        this.id = -1;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }



    public boolean checkPin(String pin){
        if (pin.equals(this.pin)){
            return true;
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
