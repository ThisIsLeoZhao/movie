package com.example.leo.movie.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import com.example.leo.movie.IResponseCallback;
import com.example.leo.movie.R;
import com.example.leo.movie.network.LoginResponse;
import com.example.leo.movie.network.Requester;
import com.example.leo.movie.network.ResponseHandler;
import com.example.leo.movie.network.URLBuilder;

/**
 * Created by Leo on 14/01/2018.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        findViewById(R.id.loginButton).setOnClickListener(view -> {
            final String username = ((EditText) findViewById(R.id.username)).getText().toString();
            final String password = ((EditText) findViewById(R.id.password)).getText().toString();

            Requester.post(URLBuilder.loginURL(),
                    "username=" + username + "&password=" + password,
                    new ResponseHandler<>(LoginResponse.class, new IResponseCallback<LoginResponse>() {
                @Override
                public void success(LoginResponse response) {
                    if (response.auth) {
                        Log.i(TAG, response.token);
                    } else {
                        Log.e(TAG, response.message);
                    }
                }

                @Override
                public void fail(String reason) {
                    Log.e(TAG, reason);
                }
            }));
        });
    }


}
