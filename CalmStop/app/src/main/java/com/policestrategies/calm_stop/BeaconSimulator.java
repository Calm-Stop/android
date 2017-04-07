package com.policestrategies.calm_stop;

import java.util.ArrayList;
import java.util.List;
import org.altbeacon.beacon.Beacon;

/**
 * Simulates beacon detection. Only used for debugging purposes.
 * @author Talal Abou Haiba
 */
public class BeaconSimulator implements org.altbeacon.beacon.simulator.BeaconSimulator {
    private List<Beacon> mBeaconList;

    public static boolean USE_SIMULATED_BEACONS = true;

    /**
     *  Creates empty beacons ArrayList.
     */
    public BeaconSimulator(){
        mBeaconList = new ArrayList<>();
    }

    /**
     * Required getter method that is called regularly by the Android Beacon Library.
     * Any beacons returned by this method will appear within your test environment immediately.
     */
    public List<Beacon> getBeacons(){
        return mBeaconList;
    }

    /**
     * Creates simulated beacons all at once.
     */
    public void createBasicSimulatedBeacons() {
        if (USE_SIMULATED_BEACONS) {
            Beacon beacon1 = new Beacon.Builder().setId1("DF7E1C79-43E9-44FF-886F-1D1F7DA6997A")
                    .setId2("0071").setId3("1").setRssi(-55).setTxPower(-55).setServiceUuid(0xfeaa)
                    .setBeaconTypeCode(0x00).build();
            Beacon beacon2 = new Beacon.Builder().setId1("DF7E1C79-43E9-44FF-886F-1D1F7DA6997A")
                    .setId2("0072").setId3("2").setRssi(-55).setTxPower(-55).setServiceUuid(0xfeaa)
                    .setBeaconTypeCode(0x00).build();
            Beacon beacon3 = new Beacon.Builder().setId1("DF7E1C79-43E9-44FF-886F-1D1F7DA6997A")
                    .setId2("0073").setId3("3").setRssi(-55).setTxPower(-55).setServiceUuid(0xfeaa)
                    .setBeaconTypeCode(0x00).build();
            Beacon beacon4 = new Beacon.Builder().setId1("DF7E1C79-43E9-44FF-886F-1D1F7DA6997A")
                    .setId2("0074").setId3("4").setRssi(-55).setTxPower(-55).setServiceUuid(0xfeaa)
                    .setBeaconTypeCode(0x00).build();
            mBeaconList.add(beacon1);
            mBeaconList.add(beacon2);
            mBeaconList.add(beacon3);
            mBeaconList.add(beacon4);
        }
    }

}
