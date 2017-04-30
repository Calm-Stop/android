package com.policestrategies.calm_stop.officer.stop;

import com.google.firebase.database.DataSnapshot;

/**
 * Container for citizen stop information.
 * @author Talal Abou Haiba
 */

class CitizenInfo {

    private long mNumberOfStops;
    private long mNumberOfAlerts;
    private long mNumberOfWarnings;
    private long mNumberOfThreats;
    private long mNumberOfCitations;
    private long mNumberOfIntoxicated;
    private long mNumberOfArrests;
    private long mNumberOfWeapons;

    CitizenInfo() {}

    void setCitizenInfo(DataSnapshot infoSnapshot) {
        mNumberOfStops = ((long) infoSnapshot.child("stops").getValue());
        mNumberOfAlerts = ((long) infoSnapshot.child("alerts").getValue());
        mNumberOfWarnings = ((long) infoSnapshot.child("warnings").getValue());
        mNumberOfThreats = ((long) infoSnapshot.child("threats").getValue());
        mNumberOfCitations = ((long) infoSnapshot.child("citations").getValue());
        mNumberOfIntoxicated = ((long) infoSnapshot.child("intoxicated").getValue());
        mNumberOfArrests = ((long) infoSnapshot.child("arrests").getValue());
        mNumberOfWeapons = ((long) infoSnapshot.child("weapons").getValue());
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
