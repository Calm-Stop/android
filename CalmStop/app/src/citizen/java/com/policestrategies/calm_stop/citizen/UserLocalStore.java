package com.policestrategies.calm_stop.citizen;

import android.content.Context;
import android.content.SharedPreferences;

public class UserLocalStore {

    SharedPreferences userLocalDatabase;
    public static final String SP_Name = "userDetails";


// Not sure what this does; context?

    public UserLocalStore(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_Name, 0);
    }

// Stores user data in the local database
    public void StoreUserData(User user) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();

        spEditor.putString("email", user.email);
        spEditor.putString("password", user.password);
        spEditor.putString("licenseNum", user.licenseNum);
        spEditor.putString("gender", user.gender);
        spEditor.putString("ethnicity", user.ethnicity);
        spEditor.putString("phone", user.phone);

        spEditor.commit();

    }

// retrieves User user that is currently logged in (assumed data already stored in SP DB)
    public User getLoggedInUser() {
        String email = userLocalDatabase.getString("email", "");
        String password = userLocalDatabase.getString("password", "");
        String name = userLocalDatabase.getString("name", "Citizen") == "" ? "Citizen" : userLocalDatabase.getString("name", "Citizen");
        String licenseNum = userLocalDatabase.getString("licenseNum", "");
        String gender = userLocalDatabase.getString("gender", "");
        String phone = userLocalDatabase.getString("phone", "");
        int age = userLocalDatabase.getInt("age", -1);

        User user = new User(email, password);
        return user;
    }

// sets loggedin to be true or false into the SP database.
    public void setUserLoggedIn(boolean loggedin) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("loggedIn", loggedin);
        spEditor.commit();
    }

// clear all SP data
    public void clearUserData(){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }
}
