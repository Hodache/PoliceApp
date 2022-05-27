package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.cliftonlabs.json_simple.JsonException;
import com.http.HttpHelper;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    private EditText loginInput = null;
    private EditText passwordInput = null;
    private Button loginButton = null;
    private TextView authStatus = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.login_title);

        loginInput = (EditText) this.findViewById(R.id.loginInput);
        passwordInput = (EditText) this.findViewById(R.id.passwordInput);
        loginButton = (Button) this.findViewById(R.id.loginButton);
        authStatus = (TextView) this.findViewById(R.id.authStatus);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authStatus.setText(R.string.auth_loggining);

                String login = loginInput.getText().toString();
                String password = passwordInput.getText().toString();

                Thread thread = new Thread(new Runnable() {
                    Boolean status = null;

                    @Override
                    public void run() {
                        try {
                            status = HttpHelper.logIn(login, password);
                        } catch (JsonException | IOException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!status) {
                                    authStatus.setText(R.string.auth_failed);
                                    return;
                                }

                                Intent intent = new Intent(getApplicationContext(), DriverSearchActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                });

                thread.start();
            }
        });
    }
}
