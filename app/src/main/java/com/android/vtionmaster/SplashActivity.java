package com.android.vtionmaster;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.Utility.ComSharedPref;
import com.android.Utility.HttpRequestHandler;
import com.android.Utility.UrlConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import semusi.activitysdk.Api;
import semusi.activitysdk.ContextSdk;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2500);
        /*boolean isUserSignedIn = ComSharedPref.loadBooleanSavedPreferences(ComSharedPref.isUserSignedIn,
                SplashActivity.this);
        if(isUserSignedIn) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                    finish();
                }
            }, 2500);
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), SocialLoginActivity.class);
                    startActivity(intent);

                    finish();
                }
            }, 2500);
        }*/
    }

}
