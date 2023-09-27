package com.shamim.frremoteattendence.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shamim.frremoteattendence.LocationService.LocationService;
import com.shamim.frremoteattendence.R;
import com.shamim.frremoteattendence.utils.ImageToBitmap;

public class Location_Not_Match extends Fragment {


    public Location_Not_Match() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_location__not__match, container, false);
        LocationService.removeLocationUpdates();

        return  v;
    }
}