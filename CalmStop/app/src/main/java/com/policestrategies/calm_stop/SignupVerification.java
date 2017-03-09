package com.policestrategies.calm_stop;

import android.util.Patterns;

/**
 * Contains verification methods for all fields (officer & citizen) used in the signup process.
 * @author Talal Abou Haiba
 */
public class SignupVerification {

    public static boolean validEmail(String email) {
        return (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public static boolean validPassword(String password) {
        System.out.println("Password length: " + password.length());
        System.out.println("Match: " + password.matches("((.*\\d.*)(.*\\w.*))|((.*\\w.*)(.*\\d.*))"));
        return (!(password.length() < 6) && password.matches("((.*\\d.*)(.*\\w.*))|((.*\\w.*)(.*\\d.*))"));
    }

    public static boolean validLicense(String license) {
        return (license.matches("^\\w([0-9]{8})"));
    }

    public static boolean validAddress(String address) {
        return (!address.isEmpty());
    }

    public static boolean validPhone(String phone) {
        return (phone.matches("^1?(\\d{10}|" + "\\(?\\d{3}\\)?(-|\\s)?\\d{3}(-|\\s)?\\d{4})\\s?"));
    }

    public static boolean validFirstName(String firstname) {
        return (!firstname.isEmpty());
    }

    public static boolean validLastName(String lastname) {
        return (!lastname.isEmpty());
    }

    public static boolean validDateOfBirth(String dateofbirth) {
        return (dateofbirth.matches("^\\d{1,2}(-?|(,\\s)?|/?)\\d{1,2}(-?|(,\\s)?|/?)\\d{4}"));
    }

} // end class SignupVerification
