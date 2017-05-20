package com.wakarkhan.deliverydrop.model;

import java.io.Serializable;

/**
 * Created by wakar on 17/5/17.
 */

public class User implements Serializable {

    private int id;
    private String first_name;
    private String email;
    private String password;
    private String last_name;
    private String phone;
    private String username;
    private String address;
    private int[] orders;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setOrders(int[] orders) {
        this.orders = orders;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getEmail() {
        return email;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getAddress() {
        return address;
    }

    public int[] getOrders() {
        return orders;
    }

}
