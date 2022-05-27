package com.example.myapplication.bottomnavigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.dataclasses.Car;
import com.dataclasses.Driver;
import com.dataclasses.Fine;
import com.example.myapplication.DriverSearchActivity;
import com.example.myapplication.IssuingFinesActivity;
import com.example.myapplication.R;
import com.github.cliftonlabs.json_simple.JsonException;
import com.http.HttpHelper;

import java.io.IOException;
import java.util.ArrayList;

public class FinesFragment extends Fragment {
    private ArrayList<Fine> fines = new ArrayList<>();
    private ArrayList<CardView> cardViews = new ArrayList<>();

    private LinearLayout linear = null;
    private Button issueFineButton = null;

    public FinesFragment(){
    }

    public static Fragment newInstance() {
        return new FinesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.fines_fragment, container, false);

        linear = (LinearLayout) RootView.findViewById(R.id.finesLayout);
        issueFineButton = (Button) RootView.findViewById(R.id.fineButton);

        issueFineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), IssuingFinesActivity.class);
                startActivity(intent);
            }
        });

        if (Driver.getLicense() == null) {
            issueFineButton.setClickable(false);
            issueFineButton.setVisibility(View.GONE);
        }

        return RootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        removeFines();
        showFines();
    }

    private View showFineCard(Fine fine, ViewGroup root) {
        View cardBody = getLayoutInflater().inflate(R.layout.driver_fine_card, null);

        TextView finesSizeText = cardBody.findViewById(R.id.fineSizeText);
        finesSizeText.setText(String.valueOf(fine.getSize()));

        TextView dateText = cardBody.findViewById(R.id.dateText);
        dateText.setText(fine.getDate());

        TextView descriptionText = cardBody.findViewById(R.id.descriptionText);
        descriptionText.setText(fine.getDescription());

        root.addView(cardBody);

        return cardBody;
    }

    private void showFines () {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    fines = HttpHelper.getFinesList(Driver.getLicense());
                } catch (JsonException | IOException e) {
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (Fine fine : fines) {
                            CardView card = (CardView) showFineCard(fine, linear);
                            if (fines.indexOf(fine) % 2 == 0)
                                card.setBackgroundColor(getActivity().getColor(R.color.teal_200));

                            cardViews.add(card);
                        }
                    }
                });
            }
        });

        thread.start();
    }

    private void removeFines () {
        for (CardView card: cardViews) {
            linear.removeView(card);
        }
    }
}
