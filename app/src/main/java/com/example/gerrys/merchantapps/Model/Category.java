package com.example.gerrys.merchantapps.Model;

/**
 * Created by Cj_2 on 2017-11-04.
 */

public class Category {
    private String Name;
    private String Image;
    private String Password;
    private String Origin;
    private String Phone;

    public Category(){

    }

    public Category(String name, String image,String password,String origin) {
        Name = name;
        Image = image;
        Password = password;
        Origin = origin;
    }


    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
