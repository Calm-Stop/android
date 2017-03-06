package com.policestrategies.calm_stop.citizen;

import android.util.Patterns;

/**
 * Created by jting on 3/6/2017.
 */

public class RegexChecks {
    //EMAIL REGEX
    private boolean validEmail(String email) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }
        return true;
    }

    //PASSWORD REGEX
    //Password must be >= 6 characters long and contain at least 1 letter and 1 number
    private boolean validPassword(String password) {
        if (password.isEmpty() || password.length() < 6 || !password.matches("((.*\\d.*)(.*\\w.*))|((.*\\w.*)(.*\\d.*))")) {
            return false;
        }
        return true;
    }


/*
    private boolean validateInput(String email, String password, String licensenum, String firstname,
                                  String lastname, String phone, String address, String gender, String language, String dateofbirth) {

        //FIRST NAME REGEX
        if(firstname.matches(".*\\d.*") || firstname.matches(".*\\s.*")) {
            mFirstNameField.setError("Please enter a valid first name.");
            mFirstNameField.requestFocus();
            return false;
        } else if (firstname.isEmpty()) {
            mFirstNameField.setError("This field was left empty.");
            mFirstNameField.requestFocus();
            return false;
        } else {
            mFirstNameField.setError(null);
        }

        //LAST NAME REGEX
        if(lastname.matches(".*\\d.*") || lastname.matches(".*\\s.*")) {
            mLastNameField.setError("Please enter a valid last name.");
            mLastNameField.requestFocus();
            return false;
        } else if (lastname.isEmpty()) {
            mLastNameField.setError("This field was left empty.");
            mLastNameField.requestFocus();
            return false;
        } else {
            mLastNameField.setError(null);
        }


        //EMAIL REGEX
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailField.setError("Enter a valid email address.");
            mEmailField.requestFocus();
            return false;
        } else {
            mEmailField.setError(null);
        }

        //PASSWORD REGEX
        if (password.isEmpty() || password.length() < 6) {
            mPasswordField.setError("Please enter a valid password" +
                    " containing at least 6 characters.");
            mPasswordField.requestFocus();
            return false;
        } else {
            mPasswordField.setError(null);
        }

        //DRIVER'S LICENSE REGEX (CALIFORNIA FORMAT)
        if(!licensenum.matches("^\\w([0-9]{8})")) {
            mLicenseNum.setError("Enter a letter followed by eight numbers\nExample: A12345678");
            mLicenseNum.requestFocus();
            return false;
        } else {
            mLicenseNum.setError(null);
        }

        //PHONE REGEX
        if(!phone.matches("^1?(\\d{10}|" +
                "\\(?\\d{3}\\)?(-|\\s)?\\d{3}(-|\\s)?\\d{4})\\s?")) {
            mPhone.setError("Invalid Phone Number.");
            mPhone.requestFocus();
            return false;
        } else {
            mPhone.setError(null);
        }
        //address gender language DOB
        //ADDRESS REGEX
        if (address.isEmpty()){
            mAddress.setError("This field was left empty.");
            mAddress.requestFocus();
            return false;
        } else {
            mAddress.setError(null);
        }

        //DATE OF BIRTH REGEX; replace with spinner for month, day, and year.
        if (dateofbirth.isEmpty()) {
            mDateOfBirth.setError("This field was left empty");
            mDateOfBirth.requestFocus();
            return false;
        } else if (!dateofbirth.matches("^\\d{1,2}(-?|(,\\s)?|/?)\\d{1,2}(-?|(,\\s)?|/?)\\d{4}")){
            mDateOfBirth.setError("DD-MM-YYYY");
            mDateOfBirth.requestFocus();
            return false;
        } else {
            mDateOfBirth.setError(null);
        }

        return true;


    }
*/
}

