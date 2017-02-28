package com.policestrategies.calm_stop.citizen;

/**
 * Created by jting on 2/27/2017.
 */

public class Citizen {
    String email, license_number,
            first_name, last_name, phone_number, address, gender, language, date_of_birth;

    public Citizen(String email, String password, String licenseNum, String firstname,
                   String lastname, String phone, String address, String gender, String language,
                   String dateofbirth) {
        this.email = email;
        this.license_number = licenseNum;
        this.first_name = firstname;
        this.last_name = lastname;
        this.phone_number = phone;
        this.address = address;
        this.gender = gender;
        this.language = language;
        this.date_of_birth = dateofbirth;
    }

    public Citizen(final String email, final String password) {
        this.email = email;
    }
}
