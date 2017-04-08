package com.policestrategies.calm_stop;

import android.util.Patterns;

/**
 * Contains verification methods for all fields (officer & citizen) used in the signup process.
 * @author Talal Abou Haiba
 */
public class RegexChecks {

    public static boolean validEmail(String email) {
        return (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public static boolean validPassword(String password) {
        return (!(password.length() < 6) && password.matches("((.*\\d.*)(.*[A-Za-z].*))|((.*[A-Za-z].*)(.*\\d.*))"));
    }

    public static boolean validLicense(String license) {
        return (license.matches("^[A-Z]([0-9]{8})"));
    }

    public static boolean validAddress(String address) {
        return (!address.isEmpty());
    }

    public static boolean validZip(String zip) { return (zip.matches("^\\d{5}")); }

    public static boolean validPhone(String phone) {
        return (phone.matches("^(1|(1(-|\\s)))?(\\d{10}|" + "\\(?\\d{3}\\)?(-|\\s)?\\d{3}(-|\\s)?\\d{4})\\s?"));
    }


    public static boolean validFirstName(String firstname) {
        return (!firstname.isEmpty());
    }

    public static boolean validLastName(String lastname) {
        return (!lastname.isEmpty());
    }

    public static boolean validDepartment(String department) {
        return (!department.isEmpty());
    }

    public static boolean validBadge(String badge) {
        return (!badge.isEmpty());
    }

    public static boolean validDateOfBirth(String dateofbirth) {
        return !dateofbirth.isEmpty();
    }

} // end class RegexChecks
