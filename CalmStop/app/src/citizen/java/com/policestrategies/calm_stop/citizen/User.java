package com.policestrategies.calm_stop.citizen;

public class User {
    String email, password, licenseNum, ethnicity,
            firstname, lastname, phone, address, gender, language, DateOfBirth;

    public User(String email, String password, String licenseNum, String firstname,
                String lastname, String phone, String address, String gender, String language, String DateOfBirth) {
        this.email = email;
        this.password = password;
        this.licenseNum = licenseNum;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.address = address;
        this.gender = gender;
        this.language = language;
        this.DateOfBirth = DateOfBirth;
    }

    public User(final String email, final String password) {
        this.email = email;
        this.password = password;
    }
}
