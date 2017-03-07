package com.policestrategies.calm_stop.citizen.beacon_detection;

/**
 * Encapsulates a given officer.
 * @author Talal Abou Haiba
 */
class BeaconObject {

    private String mOfficerName;
    private String mDepartmentNumber;

    private String mOfficerUid;
    private String mInstanceId;

    BeaconObject(String officerName, String departmentNumber, String officerUid, String instance) {
        mOfficerName = officerName;
        mDepartmentNumber = departmentNumber;
        mOfficerUid = officerUid;
        mInstanceId = instance;
    }

    String getOfficerName() {
        return mOfficerName;
    }

    String getDepartmentNumber() {
        return mDepartmentNumber;
    }

    String getOfficerUid() {
        return mOfficerUid;
    }

    String getBeaconInstanceId() {
        return mInstanceId;
    }

} // end class BeaconObject
