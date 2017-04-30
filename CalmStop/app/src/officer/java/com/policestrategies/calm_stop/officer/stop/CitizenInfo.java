package com.policestrategies.calm_stop.officer.stop;

import com.google.firebase.database.DataSnapshot;

/**
 * Container for citizen stop information.
 * @author Talal Abou Haiba
 */

class CitizenInfo {

    private int mNumberOfStops;
    private int mNumberOfAlerts;
    private int mNumberOfWarnings;
    private int mNumberOfThreats;
    private int mNumberOfCitations;
    private int mNumberOfIntoxicated;
    private int mNumberOfArrests;
    private int mNumberOfWeapons;

    CitizenInfo() {}

    void setCitizenInfo(DataSnapshot infoSnapshot) {
        mNumberOfStops = ((int) infoSnapshot.child("stops").getValue());
        mNumberOfAlerts = ((int) infoSnapshot.child("alerts").getValue());
        mNumberOfWarnings = ((int) infoSnapshot.child("warnings").getValue());
        mNumberOfThreats = ((int) infoSnapshot.child("threats").getValue());
        mNumberOfCitations = ((int) infoSnapshot.child("citations").getValue());
        mNumberOfIntoxicated = ((int) infoSnapshot.child("intoxicated").getValue());
        mNumberOfArrests = ((int) infoSnapshot.child("arrests").getValue());
        mNumberOfWeapons = ((int) infoSnapshot.child("weapons").getValue());
    }

    int getNumberOfStops() {
        return mNumberOfStops;
    }

    int getNumberOfAlerts() {
        return mNumberOfAlerts;
    }

    int getNumberOfWarnings() {
        return mNumberOfWarnings;
    }

    int getNumberOfThreats() {
        return mNumberOfThreats;
    }

    int getNumberOfCitations() {
        return mNumberOfCitations;
    }

    int getNumberOfIntoxicated() {
        return mNumberOfIntoxicated;
    }

    int getNumberOfArrests() {
        return mNumberOfArrests;
    }

    int getNumberOfWeapons() {
        return mNumberOfWeapons;
    }

} // end class CitizenInfo
