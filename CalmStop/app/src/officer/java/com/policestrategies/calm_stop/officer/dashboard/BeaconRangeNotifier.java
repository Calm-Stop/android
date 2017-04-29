package com.policestrategies.calm_stop.officer.dashboard;


import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

/**
 * @author Talal Abou Haiba
 */
class BeaconRangeNotifier implements RangeNotifier {

    private String mCurrentlyRegisteredBeaconId;
    private DashboardManager mDashboardManager;

    BeaconRangeNotifier(DashboardManager dashboardManager) {
        mDashboardManager = dashboardManager;
    }

    void setCurrentBeaconId(String beaconId) {
        mCurrentlyRegisteredBeaconId = beaconId;
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> collection, final Region region) {

        if (collection.size() == 0) {
            return;
        }

        for (Beacon beacon : collection) {
            if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x00) {
                final String instance = beacon.getId2().toString();
                if (mCurrentlyRegisteredBeaconId != null &&
                        mCurrentlyRegisteredBeaconId.equals(instance)) {
                    mDashboardManager.onSuccessfulBeaconScan(instance, region);
                }
            }
        }

    }

} // end class BeaconRangeNotifier
