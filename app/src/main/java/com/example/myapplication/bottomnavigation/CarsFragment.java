package com.example.myapplication.bottomnavigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.dataclasses.Car;
import com.dataclasses.Driver;
import com.example.myapplication.R;
import com.github.cliftonlabs.json_simple.JsonException;
import com.http.HttpHelper;

import java.io.IOException;
import java.util.ArrayList;

public class CarsFragment extends Fragment {
    private ArrayList<Car> carsOwned = new ArrayList<>();
    private ArrayList<CardView> cardViews = new ArrayList<>();

    private LinearLayout linear = null;

    public CarsFragment(){
    }

    public static Fragment newInstance() {
        return new CarsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.cars_fragment, container, false);

        linear = (LinearLayout) RootView.findViewById(R.id.linear);

        return RootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        removeCars();
        showCars();
    }

    private View showCarCard(Car car, ViewGroup root) {
        View cardBody = getLayoutInflater().inflate(R.layout.driver_car_card, null);

        TextView finesText = cardBody.findViewById(R.id.finesCountText);
        finesText.setText(String.valueOf(car.getFinesCount()));

        TextView modelText = cardBody.findViewById(R.id.modelText);
        modelText.setText(car.getModel());

        TextView plateText = cardBody.findViewById(R.id.plateText);
        plateText.setText(car.getPlateNumber());

        TextView colorText = cardBody.findViewById(R.id.colorText);
        colorText.setText(car.getColor());

        TextView insuranceText = cardBody.findViewById(R.id.insuranceText);
        insuranceText.setText(car.getInsurance());

        root.addView(cardBody);

        return cardBody;
    }

    private void showCars () {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    carsOwned = HttpHelper.getCarsList(Driver.getLicense());
                } catch (JsonException | IOException e) {
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (Car car: carsOwned) {
                            CardView card = (CardView) showCarCard(car, linear);
                            if (carsOwned.indexOf(car) % 2 == 0)
                                card.setBackgroundColor(getActivity().getColor(R.color.teal_200));

                            cardViews.add(card);
                        }
                    }
                });
            }
        });

        thread.start();
    }

    private void removeCars () {
        for (CardView card: cardViews) {
            linear.removeView(card);
        }
    }
}
