package com.shamim.frremoteattendence.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.shamim.frremoteattendence.LocationService.LocationService;
import com.shamim.frremoteattendence.R;
import com.shamim.frremoteattendence.activity.Fragment_Changer_Activity;
import com.shamim.frremoteattendence.utils.ImageToBitmap;

public class Location_Not_Match extends Fragment {


    public Location_Not_Match() {
        // Required empty public constructor
    }

    Button cameraBack;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_location__not__match, container, false);
        LocationService.removeLocationUpdates();
        cameraBack=v.findViewById(R.id.cameraBackBtn);

        cameraBack.setOnClickListener(view ->
        {
            Intent backCameraIntent=new Intent(getActivity(), Fragment_Changer_Activity.class);
            startActivity(backCameraIntent);
        });
        return  v;
    }
}