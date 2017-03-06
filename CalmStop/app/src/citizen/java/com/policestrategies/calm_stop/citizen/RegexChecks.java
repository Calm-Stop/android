package com.policestrategies.calm_stop.citizen;

import android.util.Patterns;

/**
 * Created by jting on 3/6/2017.
 */
//(8 fns)validEmail, validPassword, validLicense, validAddress, validPhone, validFirstname, validLastname, validDateOfBirth
public class RegexChecks {
    //EMAIL REGEX
    public boolean validEmail(String email) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }
        return true;
    }

    //PASSWORD REGEX
    //Password must be >= 6 characters long and contain at least 1 letter and 1 number
    public boolean validPassword(String password) {
        if (password.length() < 6 || !password.matches("((.*\\d.*)(.*\\w.*))|((.*\\w.*)(.*\\d.*))")) {
            return false;
        }
        return true;
    }

    //LICENSE REGEX
    public boolean validLicense(String license) {
        if(!license.matches("^\\w([0-9]{8})")) {
            return false;
        }
        return true;
    }

    //ADDRESS REGEX
    public boolean validAddress(String address) {
        if (address.isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean validPhone(String phone) {
        if(!phone.matches("^1?(\\d{10}|" +
                "\\(?\\d{3}\\)?(-|\\s)?\\d{3}(-|\\s)?\\d{4})\\s?")) {
            return false;
        }
        return true;
    }

    public boolean validFirstName(String firstname) {
        if (firstname.isEmpty()){
            return false;
        }
        return true;
    }

    public boolean validLastName(String lastname) {
        if (lastname.isEmpty()){
            return false;
        }
        return true;
    }

    public boolean validDateOfBirth(String dateofbirth) {
        if (!dateofbirth.matches("^\\d{1,2}(-?|(,\\s)?|/?)\\d{1,2}(-?|(,\\s)?|/?)\\d{4}")) {
            return false;
        }
        return true;
    }
}

