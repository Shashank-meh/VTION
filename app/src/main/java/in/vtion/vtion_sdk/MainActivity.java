package in.vtion.vtion_sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import semusi.activitysdk.ContextSdk;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button notificationBtn = findViewById(R.id.notificationBtn);
        if (Build.VERSION.SDK_INT >= 19) {
            notificationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                    } catch (Exception e) {
                    }
                }
            });
        } else {
            notificationBtn.setVisibility(View.GONE);
        }

        Button accessibilityBtn = findViewById(R.id.accessibilityBtn);
        {
            accessibilityBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivityForResult(intent, 0);
                }
            });
        }

        Button playBtn = findViewById(R.id.playBtn);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContextSdk.openPlayServiceUpdate(MainActivity.this.getApplicationContext());
            }
        });
    }
}
