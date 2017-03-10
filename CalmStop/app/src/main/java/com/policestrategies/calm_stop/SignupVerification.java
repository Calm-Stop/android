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
        return (!(password.length() < 6) && password.matches("((.*\\d.*)(.*[A-Za-z].*))|((.*[A-Za-z].*)(.*\\d.*))"));
    }

    public static boolean validLicense(String license) {
        return (license.matches("^[A-Z]([0-9]{8})"));
    }

    public static boolean validAddress(String address) {
        return (!address.isEmpty());
    }

    public static boolean validPhone(String phone) {
        return (phone.matches("^(1|(1(-|\\s)))?(\\d{10}|" + "\\(?\\d{3}\\)?(-|\\s)?\\d{3}(-|\\s)?\\d{4})\\s?"));
    }


    public static boolean validFirstName(String firstname) {
        return (!firstname.isEmpty());
    }

    public static boolean validLastName(String lastname) {
        return (!lastname.isEmpty());
    }

    public static boolean validDateOfBirth(String dateofbirth) {
        if (dateofbirth.matches("^\\d{1,2}-\\d{1,2}-\\d{4}")) {
            int i,j,k,l;
            String S_day="", S_month="", S_year="";
            int I_day, I_month, I_year;
            String dummy = dateofbirth.substring(0,1);
            for (i = 0, j = 1; !dummy.equalsIgnoreCase("-");) {
                S_day = dateofbirth.substring(0,j);
                i++; j++;
                dummy = dateofbirth.substring(i,j);
            }
            //i,j = (1,2)|(2,3)
            k = j;
            i=j+1;
            dummy = dateofbirth.substring(j,i);
            while (!dummy.equalsIgnoreCase("-")) {
                S_month = dateofbirth.substring(k,i);
                i++; j++;
                dummy = dateofbirth.substring(j,i);
            }
            k=i;
            j=i+1;
            dummy = dateofbirth.substring(i,j);
            while (!dummy.equalsIgnoreCase("-")) {
                S_year = dateofbirth.substring(k,j);
                i++; j++;
                dummy = dateofbirth.substring(i,j);
            }
            I_day = Integer.parseInt(S_day);
            I_month = Integer.parseInt(S_month);
            I_year = Integer.parseInt(S_year);
            if (I_day > 31 || I_month > 12 || I_year < 1900 || I_year > 2010) return false;
        }
        return true;
    }

} // end class SignupVerification
