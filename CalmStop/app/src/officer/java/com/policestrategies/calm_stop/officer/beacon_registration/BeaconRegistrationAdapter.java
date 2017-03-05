package com.policestrategies.calm_stop.officer.beacon_registration;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.policestrategies.calm_stop.R;

import java.util.List;

/**
 * Adapter for recyclerview used in {@link BeaconRegistrationActivity}
 * @author Talal Abou Haiba
 */
class BeaconRegistrationAdapter extends RecyclerView.Adapter<BeaconRegistrationAdapter.BeaconView> {

    private List<BeaconObject> mBeaconObjects;
    private OnItemClickListener mItemClickListener;

    BeaconRegistrationAdapter(List<BeaconObject> beaconObjects, OnItemClickListener listener) {
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
                .inflate(R.layout.card_view_beacon, viewGroup, false);
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
        private TextView mNamespace;
        private TextView mInstance;
        private TextView mDistance;

        BeaconView(View view) {
            super(view);
            mCardView = (CardView) view.findViewById(R.id.beacon_card_view);
            mNamespace = ((TextView) view.findViewById(R.id.text_cardview_namespace_id));
            mInstance = ((TextView) view.findViewById(R.id.text_cardview_instance_id));
            mDistance = ((TextView) view.findViewById(R.id.text_cardview_beacon_range));
        }

        void setBeaconView(final BeaconObject beacon, final OnItemClickListener listener) {
            mNamespace.setText(beacon.getNamespace());
            mInstance.setText(beacon.getInstance());
            mDistance.setText(beacon.getDistance());
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

} // end class BeaconRegistrationAdapter
