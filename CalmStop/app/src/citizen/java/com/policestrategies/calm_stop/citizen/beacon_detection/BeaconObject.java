package com.policestrategies.calm_stop.citizen.beacon_detection;

/**
 * Encapsulates a given officer.
 * @author Talal Abou Haiba
 */
class BeaconObject {

    private String mOfficerName;
    private String mDepartmentNumber;

    BeaconObject(String officerName, String departmentNumber) {
        mOfficerName = officerName;
        mDepartmentNumber = departmentNumber;
    }

    String getOfficerName() {
        return mOfficerName;
    }

    String getDepartmentNumber() {
        return mDepartmentNumber;
    }

} // end class BeaconObject
