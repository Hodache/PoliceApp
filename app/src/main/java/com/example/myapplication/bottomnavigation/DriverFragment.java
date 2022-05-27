package com.example.myapplication.bottomnavigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dataclasses.Driver;
import com.example.myapplication.R;

public class DriverFragment extends Fragment {
    TextView driverNameTV;

    public DriverFragment(){
    }

    public static Fragment newInstance() {
        Fragment fragment = new DriverFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.driver_fragment, container, false);

        driverNameTV = RootView.findViewById(R.id.driverNameTV);

        if (Driver.getLicense() != null){
            String driverName = Driver.getFirstName() + " " + Driver.getLastName();
            driverNameTV.setText(driverName);
        }

        return RootView;
    }
}
