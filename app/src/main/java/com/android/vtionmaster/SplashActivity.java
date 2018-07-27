package com.android.vtionmaster;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.Utility.ComSharedPref;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        logo = findViewById(R.id.logo);

        final boolean isSecondLaunch = ComSharedPref.loadBooleanSavedPreferences(ComSharedPref.isFirstLaunch,SplashActivity.this);
        final boolean isUserSignedIn = ComSharedPref.loadBooleanSavedPreferences(ComSharedPref.isUserSignedIn,
                SplashActivity.this);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(!isSecondLaunch){
                    Intent intent = new Intent(getApplicationContext(), IntroSliderActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    /*if(isUserSignedIn){
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), PinValidationActivity.class);
                        startActivity(intent);
                        finish();
                    }*/
                }
            }
        }, 2500);

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animation = ObjectAnimator.ofFloat(logo, "translationY", -450f);
                animation.setDuration(1000);
                animation.start();
                animation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        super.onAnimationRepeat(animation);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                    }

                    @Override
                    public void onAnimationPause(Animator animation) {
                        super.onAnimationPause(animation);
                    }

                    @Override
                    public void onAnimationResume(Animator animation) {
                        super.onAnimationResume(animation);
                    }
                });
            }
        },2000);*/
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
