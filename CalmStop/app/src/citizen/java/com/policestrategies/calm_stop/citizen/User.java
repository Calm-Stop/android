package com.policestrategies.calm_stop.citizen;

public class User {
    String email, password;
    String licenseNum, name, phone, address, gender;
    int age;

    public User(final String name, final int age, final String email, final String password) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.password = password;
    }

    public User(final String email, final String password) {
        this.email = email;
        this.password = password;
        this.name = "";
        this.age = -1;
    }
}
