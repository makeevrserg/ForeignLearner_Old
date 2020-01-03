package com.example.learnjapanese.Adapters;

import com.example.learnjapanese.WordElement;

import java.util.ArrayList;

public class UserData {
    public String name;
    public String lastName;

    public String year;
    public String month;
    public String day;

    public String eMail;

    public String sex;
    public String uId;

   // public ArrayList<WordElement> wordElements;

    public UserData() {
        this.name = "";
        this.lastName = "";
        this.year = "";
        this.month = "";
        this.day = "";
        this.eMail = "";
        this.sex = "";
        this.uId="";
    }
    public UserData(String name, String lastName, String year, String month, String day, String eMail, String sex) {
        this.name = name;
        this.lastName = lastName;
        this.year = year;
        this.month = month;
        this.day = day;
        this.eMail = eMail;
        this.sex = sex;
    }
}
