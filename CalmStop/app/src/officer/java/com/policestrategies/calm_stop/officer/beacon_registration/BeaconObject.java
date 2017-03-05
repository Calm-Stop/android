package com.policestrategies.calm_stop.officer.beacon_registration;

/**
 * Encapsulates a scanned beacon.
 * @author Talal Abou Haiba
 */
class BeaconObject {

    private String mNamespaceId;
    private String mInstanceId;
    private String mDistance;

    BeaconObject(String namespaceId, String instanceId, String distance) {
        mNamespaceId = namespaceId;
        mInstanceId = instanceId;
        mDistance = distance;
    }

    String getNamespace() {
        return mNamespaceId;
    }

    String getInstance() {
        return mInstanceId;
    }

    String getDistance() {
        return mDistance;
    }

} // end class BeaconObject
