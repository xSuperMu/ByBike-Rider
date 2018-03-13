package com.muhammadelsayed.bybike_rider.Fragments;


import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.muhammadelsayed.bybike_rider.AccountActivities.EditAccount;
import com.muhammadelsayed.bybike_rider.AccountActivities.RiderProfile;
import com.muhammadelsayed.bybike_rider.R;




import static com.muhammadelsayed.bybike_rider.R.drawable.muhammad;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#accountFragmentInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    // the fragment initialization parameters
    private static final String ARG_TITLE = "Account Fragment";
    private String mTitle;
    private TextView mTvAccountFragment;
    private ConstraintLayout mClRiderProfile;
    private ConstraintLayout mClEditRiderProfile;
    private CircularImageView mUserImage;

    private ConstraintLayout.OnClickListener mOnClEditRiderProfile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getContext(), EditAccount.class));
        }
    };
    private ConstraintLayout.OnClickListener mOnClRiderProfile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getContext(), RiderProfile.class));
        }
    };

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title The title of fragment RatingFragment.
     * @return A new instance of fragment AccountFragment.
     */
    public static AccountFragment accountFragmentInstance(String title) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        mClEditRiderProfile = rootView.findViewById(R.id.cl_edit_rider_profile);
        mClRiderProfile = rootView.findViewById(R.id.cl_rider_profile);
        mUserImage = rootView.findViewById(R.id.profile_image);
        mUserImage.setImageResource(R.drawable.trump);
        mClRiderProfile.setOnClickListener(mOnClRiderProfile);
        mClEditRiderProfile.setOnClickListener(mOnClEditRiderProfile);


        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
