package com.policestrategies.calm_stop.citizen.beacon_detection;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.policestrategies.calm_stop.R;

import java.util.List;

/**
 * Adapter for recyclerview used in {@link BeaconDetectionActivity}
 * @author Talal Abou Haiba
 */
class BeaconDetectionAdapter extends RecyclerView.Adapter<BeaconDetectionAdapter.BeaconView> {

    private List<BeaconObject> mBeaconObjects;
    private OnItemClickListener mItemClickListener;

    BeaconDetectionAdapter(List<BeaconObject> beaconObjects, OnItemClickListener listener) {
        mBeaconObjects = beaconObjects;
        mItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        if (mBeaconObjects == null) {
            return 0;
        }
        return mBeaconObjects.size();
    }

    @Override
    public BeaconView onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_view_officer, viewGroup, false);
        return new BeaconView(itemView);
    }

    @Override
    public void onBindViewHolder(BeaconView beaconViewHolder, int i) {
        BeaconObject current = mBeaconObjects.get(i);
        if (current != null) {
            beaconViewHolder.setBeaconView(current, mItemClickListener);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    static class BeaconView extends RecyclerView.ViewHolder {

        private CardView mCardView;
        private TextView mOfficerName;
        private TextView mDepartmentNumber;
        private TextView mBadgeNumber;
        private TextView mOfficerDistance;
        private ImageView mOfficerPhoto;

        BeaconView(View view) {
            super(view);
            mCardView = (CardView) view.findViewById(R.id.officer_information_card_view);
            mOfficerName = ((TextView) view.findViewById(R.id.text_card_view_officer_name));
            mDepartmentNumber = ((TextView) view.findViewById(R.id.text_card_view_officer_department_number));
            mBadgeNumber = ((TextView) view.findViewById(R.id.text_card_view_officer_badge_number));
            mOfficerDistance = ((TextView) view.findViewById(R.id.text_cardview_officer_range));
            mOfficerPhoto = ((ImageView) view.findViewById(R.id.officer_photo));
        }

        void setBeaconView(final BeaconObject beacon, final OnItemClickListener listener) {
            String officerName = "Officer " + beacon.getOfficerName();
            String departmentNumber = "Department #" + beacon.getDepartmentNumber();
            String badgeNumber = "Badge #" + beacon.getBadgeNumber();
            String officerDistance = beacon.getDistance();

            mOfficerName.setText(officerName);
            mDepartmentNumber.setText(departmentNumber);
            mBadgeNumber.setText(badgeNumber);
            mOfficerDistance.setText(officerDistance);
            mOfficerPhoto.setImageResource(R.mipmap.ic_launcher);
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(beacon);
                }
            });
        }

    }

    interface OnItemClickListener {
        void onItemClick(BeaconObject item);
    }

} // end class BeaconDetectionAdapter
