package com.policestrategies.calm_stop.citizen;

/**
 * Created by jting on 2/27/2017.
 */

public class Citizen {
    String email, password, licensenum,
                firstname, lastname, phone, address, gender, language, dateofbirth;

    public Citizen(String email, String password, String licenseNum, String firstname,
                   String lastname, String phone, String address, String gender, String language,
                   String dateofbirth) {
        this.email = email;
        this.password = password;
        this.licensenum = licenseNum;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.address = address;
        this.gender = gender;
        this.language = language;
        this.dateofbirth = dateofbirth;
    }

    public Citizen(final String email, final String password) {
        this.email = email;
        this.password = password;
    }
}
