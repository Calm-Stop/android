package com.policestrategies.calm_stop.citizen.stop;

import com.google.firebase.database.DataSnapshot;

/**
 * Container for officer stop information.
 * @author Talal Abou Haiba
 */

class OfficerInfo {

    private String mFullName;
    private String mDepartmentNumber;
    private String mBadgeNumber;

    private String mPhotoUrl;

    OfficerInfo() { }

    void setOfficerProfile(DataSnapshot profileSnapshot) {
        String firstName = profileSnapshot.child("first_name").getValue().toString();
        String lastName = profileSnapshot.child("last_name").getValue().toString();

        mFullName = firstName + " " + lastName;
        mDepartmentNumber = profileSnapshot.child("department").getValue().toString();
        mBadgeNumber = profileSnapshot.child("badge").getValue().toString();
        mPhotoUrl = profileSnapshot.child("photo").getValue().toString();
    }

    String getFullName() {
        return mFullName;
    }

    String getDepartmentNumber() {
        return mDepartmentNumber;
    }

    String getBadgeNumber() {
        return mBadgeNumber;
    }

    String getPhotoUrl() {
        return mPhotoUrl;
    }

} // end class OfficerInfo
