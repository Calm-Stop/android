package com.policestrategies.calm_stop.citizen.beacon_detection;

/**
 * Encapsulates a given officer.
 * @author Talal Abou Haiba
 */
class BeaconObject {

    private String mOfficerName;
    private String mDepartmentNumber;
    private String mBadgeNumber;
    private String mOfficerPhotoPath;

    private String mOfficerUid;
    private String mInstanceId;
    private String mBeaconDistance;

    BeaconObject(String name, String department, String uid, String instance, String badge,
                 String distance, String photoPath) {
        mOfficerName = name;
        mDepartmentNumber = department;
        mOfficerUid = uid;
        mInstanceId = instance;
        mBadgeNumber = badge;
        mBeaconDistance = distance;
        mOfficerPhotoPath = photoPath;
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

    String getBadgeNumber() {
        return mBadgeNumber;
    }

    String getDistance() {
        return mBeaconDistance;
    }

    String getPhotoPath() {
        return mOfficerPhotoPath;
    }

} // end class BeaconObject
