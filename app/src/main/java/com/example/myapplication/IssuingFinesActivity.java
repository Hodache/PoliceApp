package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dataclasses.Driver;
import com.github.cliftonlabs.json_simple.JsonException;
import com.http.HttpHelper;

import java.io.IOException;

public class IssuingFinesActivity extends AppCompatActivity {
    private EditText fineSizeInput = null;
    private EditText descriptionInput = null;
    private Button issuingButton = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issuing_fines);
        setTitle("Выписать штраф");

        fineSizeInput = findViewById(R.id.fineSizeInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        issuingButton = findViewById(R.id.issueFineButton);

        issuingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description = descriptionInput.getText().toString();
                int fineSize = Integer.parseInt(fineSizeInput.getText().toString());
                sendFine(description, fineSize);

                Intent intent = new Intent(getApplicationContext(), DriverSearchActivity.class);
                startActivity(intent);
            }
        });
    }

    private void sendFine(String description, int fineSize){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!HttpHelper.issueFine(Driver.getLicense(), description, fineSize)) {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_occured), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException | JsonException e) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_occured), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}
