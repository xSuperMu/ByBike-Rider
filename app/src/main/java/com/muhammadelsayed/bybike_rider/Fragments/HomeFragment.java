package com.muhammadelsayed.bybike_rider.Fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.muhammadelsayed.bybike_rider.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#homeFragmentInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {

    // The fragment initialization parameters
    private GoogleMap mMap;
    private MapView mMapView;
    private View mRootView;

    private Boolean mLocationPermissionGranted = false;
    public static String TAG = "HomeFragment";
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 99;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment homeFragmentInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    /**
     * The system calls this method when it's time for the fragment
     * to draw its user interface for the first time.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_home, container, false);

        mMapView = mRootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        // needed to get the map to display immediately
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);


        return mRootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        getLocationPermission();
        initMap();

    }

    private void setMyLocationEnabled(boolean b) {
        // For showing a move to my location button
        if (mLocationPermissionGranted) {
            try {
                mMap.setMyLocationEnabled(b);
            } catch (SecurityException ex) {
                Log.e(TAG, "initMap: SecurityException" + ex.getMessage());
            }
        } else {
            Toast.makeText(getContext(), "You have to accept to enjoy all app's services!", Toast.LENGTH_LONG).show();
        }

    }


    private void initMap() {

        // For dropping a marker at a point on the Map "Mansoura"
        LatLng mansoura = new LatLng(31.037933, 31.381523);
        mMap.addMarker(new MarkerOptions()
                .title("Mansoura")
                .snippet("The is where we start.")
                .position(mansoura));

        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(mansoura).zoom(16).bearing(0).tilt(45).build();

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        if (mMap != null) {
            mMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull Location location) {
                    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);

                    mMap.moveCamera(center);
                    mMap.animateCamera(zoom);
                }
            });
        }

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * gets the required permissions from the user to access his location
     */
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {
                FINE_LOCATION,
                COARSE_LOCATION
        };

        if (ContextCompat.checkSelfPermission(getContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;

                setMyLocationEnabled(true);

            } else {
                requestPermissions(permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            requestPermissions(permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: called !!");
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    // some permission was granted
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            mLocationPermissionGranted = false;

                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionGranted = true;

                    setMyLocationEnabled(true);
                }
        }
    }

}