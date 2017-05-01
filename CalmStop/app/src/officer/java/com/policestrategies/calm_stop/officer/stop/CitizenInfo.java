package com.policestrategies.calm_stop.officer.stop;

import com.google.firebase.database.DataSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Container for citizen stop information.
 * @author Talal Abou Haiba
 */

class CitizenInfo {

    private String mFullName;
    private String mPreferredLanguage;
    private String mAgeAndGender;

    private String mPhotoUrl;

    private long mNumberOfStops;
    private long mNumberOfAlerts;
    private long mNumberOfWarnings;
    private long mNumberOfThreats;
    private long mNumberOfCitations;
    private long mNumberOfIntoxicated;
    private long mNumberOfArrests;
    private long mNumberOfWeapons;

    CitizenInfo() {}

    void setCitizenInfo(DataSnapshot citizenSnapshot) {
        loadCitizenProfile(citizenSnapshot.child("profile"));
        loadCitizenInfo(citizenSnapshot.child("profile").child("info"));
        //loadCitizenInfo(citizenSnapshot.child("info")); TODO: NO PROFILE #45
    }

    private void loadCitizenProfile(DataSnapshot profileSnapshot) {
        String firstName = profileSnapshot.child("first_name").getValue().toString();
        String lastName = profileSnapshot.child("last_name").getValue().toString();
        String dob = profileSnapshot.child("dob").getValue().toString();
        long language = ((long) profileSnapshot.child("language").getValue());
        long gender = ((long) profileSnapshot.child("gender").getValue());

        mPhotoUrl = profileSnapshot.child("photo").getValue().toString();
        mFullName = firstName + " " + lastName;
        mPreferredLanguage = "English";
        mAgeAndGender = calculateAgeAndGender(dob, gender);
    }

    private void loadCitizenInfo(DataSnapshot infoSnapshot) {
        mNumberOfStops = ((long) infoSnapshot.child("stops").getValue());
        mNumberOfAlerts = ((long) infoSnapshot.child("alerts").getValue());
        mNumberOfWarnings = ((long) infoSnapshot.child("warnings").getValue());
        mNumberOfThreats = ((long) infoSnapshot.child("threats").getValue());
        mNumberOfCitations = ((long) infoSnapshot.child("citations").getValue());
        mNumberOfIntoxicated = ((long) infoSnapshot.child("intoxicated").getValue());
        mNumberOfArrests = ((long) infoSnapshot.child("arrests").getValue());
        mNumberOfWeapons = ((long) infoSnapshot.child("weapons").getValue());
    }

    private String calculateAgeAndGender(String dob, long gender) {

        int age = 0;

        try {
            Date dateOfBirth = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH).parse(dob);
            Calendar dobCalendar = new GregorianCalendar();
            dobCalendar.setTime(dateOfBirth);

            Calendar currentCalendar = new GregorianCalendar();
            currentCalendar.setTimeInMillis(System.currentTimeMillis());

            age = currentCalendar.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);

        } catch (ParseException e) {

        }
        return "Male - " + age;
    }

    String getFullName() {
        return mFullName;
    }

    String getPreferredLanguage() {
        return mPreferredLanguage;
    }

    String getAgeAndGender() {
        return mAgeAndGender;
    }

    String getPhotoUrl() {
        return mPhotoUrl;
    }

    int getNumberOfStops() {
        return (int) mNumberOfStops;
    }

    int getNumberOfAlerts() {
        return (int) mNumberOfAlerts;
    }

    int getNumberOfWarnings() {
        return (int) mNumberOfWarnings;
    }

    int getNumberOfThreats() {
        return (int) mNumberOfThreats;
    }

    int getNumberOfCitations() {
        return (int) mNumberOfCitations;
    }

    int getNumberOfIntoxicated() {
        return (int) mNumberOfIntoxicated;
    }

    int getNumberOfArrests() {
        return (int) mNumberOfArrests;
    }

    int getNumberOfWeapons() {
        return (int) mNumberOfWeapons;
    }

} // end class CitizenInfo
