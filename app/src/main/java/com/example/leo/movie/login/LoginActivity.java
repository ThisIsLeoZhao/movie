package com.example.leo.movie.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.leo.movie.R;
import com.example.leo.movie.model.generated.LoginResult;
import com.example.leo.movie.transport.UserClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

            UserClient.obtain().login(username, password).enqueue(new Callback<LoginResult>() {
                @Override
                public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                    if (response.isSuccessful()) {
                        if (response.body().auth) {
                            LoginUtils.login(username, response.body().token, getApplicationContext());
                            finish();

                            Log.i(TAG, response.body().token);
                        } else {
                            Toast.makeText(LoginActivity.this, response.body().message, Toast.LENGTH_LONG).show();
                            Log.e(TAG, response.body().message);
                        }
                    } else {
                        try {
                            Toast.makeText(LoginActivity.this, response.errorBody().string(), Toast.LENGTH_LONG).show();
                            Log.e(TAG, response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginResult> call, Throwable t) {
                    Log.e(TAG, t.getMessage());
                }
            });
        });
    }
}
