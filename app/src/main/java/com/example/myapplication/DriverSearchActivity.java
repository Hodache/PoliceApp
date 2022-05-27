package com.example.myapplication;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dataclasses.Driver;
import com.example.myapplication.bottomnavigation.CarsFragment;
import com.example.myapplication.bottomnavigation.DriverFragment;
import com.example.myapplication.bottomnavigation.FinesFragment;
import com.github.cliftonlabs.json_simple.JsonException;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.http.HttpHelper;

import java.io.IOException;

public class DriverSearchActivity extends AppCompatActivity {
    private Button searchButton = null;
    private EditText searchInput = null;

    private NavigationBarView.OnItemSelectedListener OnNavigationItemSelectedListener
            = new BottomNavigationView.OnItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_driver:
                    loadFragment(DriverFragment.newInstance());
                    return true;
                case R.id.nav_cars:
                    loadFragment(CarsFragment.newInstance());
                    return true;
                case R.id.nav_fines:
                    loadFragment(FinesFragment.newInstance());
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_content, fragment);
        ft.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        setTitle(getString(R.string.drivers_data));

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnItemSelectedListener(OnNavigationItemSelectedListener);

        if (Driver.getLicense() != null) {
            setTitle(getString(R.string.drivers_data) + Driver.getLicense());
        }

        navigation.setSelectedItemId(R.id.nav_driver);

        searchButton = findViewById(R.id.searchButton);
        searchInput = findViewById(R.id.searchInput);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread requestThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HttpHelper.changeDriverByLicense(searchInput.getText().toString());
                        } catch (JsonException | IOException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (Driver.getLicense() != null) {
                                    navigation.setSelectedItemId(R.id.nav_driver);
                                    setTitle("Данные водителя " + Driver.getLicense());
                                }
                            }
                        });
                    }
                });
                requestThread.start();
            }
        });
    }

    public void test() {

        Thread connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {


                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                    }
                });
            }
        });
        connectionThread.start();

    }
}
