package com.vtion.karvyinsights;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.vtion.karvyinsights.Utility.ComSharedPref;
import com.vtion.karvyinsights.Utility.Utility;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

import semusi.activitysdk.ContextSdk;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private String msgBody="";
    private String refNo="",otp="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Karvy Insights");

        Button btnTermsNCondtns = findViewById(R.id.btn_tnc);
        CheckBox checkBox = findViewById(R.id.checkbox_tnc);
        btnTermsNCondtns.setTag(new String("Default"));

        if(ComSharedPref.loadBooleanSavedPreferences(ComSharedPref.tncCheckedStatus,MainActivity.this)) {
            btnTermsNCondtns.setText("DONE");
            btnTermsNCondtns.setTag(new String("Selected"));

            checkBox.setChecked(true);
            checkBox.setEnabled(false);
        }
        btnTermsNCondtns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox tcBox = findViewById(R.id.checkbox_tnc);
                tcBox.setSelected(true);


                Button btnTermsNCondtns = findViewById(R.id.btn_tnc);
                if(btnTermsNCondtns.getTag().equals("Selected")) {
//                    Utility.hideIcon(MainActivity.this);
                    finish();
                } else if (btnTermsNCondtns.getTag().equals("Default")) {
                    Intent intent = new Intent(MainActivity.this, TermsNConditionsActivity.class);
                    startActivityForResult(intent,100);
                }
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    LinkedHashMap<String,Object> smsDetailsMap = new LinkedHashMap<>();
                    //smsDetailsMap.put("REF", refNo);
                    smsDetailsMap.put("OTP", otp);
//                    smsDetailsMap.put("Name", ComSharedPref.loadStringSavedPreferences("sp_sender_name",MainActivity.this));
//                    smsDetailsMap.put("Msg", msgBody);
//                    smsDetailsMap.put("Time",ComSharedPref.loadStringSavedPreferences("sp_msg_date",MainActivity.this));
                    ContextSdk.tagEventObj("Registration",smsDetailsMap,MainActivity.this);
                    //Utility.hideIcon(MainActivity.this);
                    CheckBox checkBox = findViewById(R.id.checkbox_tnc);
                    checkBox.setEnabled(false);

                    Button btnTermsNCondtns = findViewById(R.id.btn_tnc);
                    btnTermsNCondtns.setText("DONE");
                    btnTermsNCondtns.setTag(new String("Selected"));
                    ComSharedPref.saveBooleanPreferences(ComSharedPref.tncCheckedStatus,true, MainActivity.this);
                }
            }
        });

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    10);
        } else {
            msgBody = Utility.readSMS(MainActivity.this);
//            System.out.println("DevTest: OTP is : "+msgBody.substring(msgBody.indexOf("REF#") + 4, msgBody.indexOf("REF#") + 10));
//            System.out.println("DevTest: OTP is : "+msgBody.substring(msgBody.indexOf("OTP is ") + 7, msgBody.indexOf("OTP is") + 11));
            if(msgBody.length()>0 && msgBody!=null) {
//                refNo = msgBody.substring(msgBody.indexOf("REF#") + 4, msgBody.indexOf("REF#") + 10);
                otp = msgBody.substring(msgBody.indexOf("OTP is ") + 7, msgBody.indexOf("OTP is") + 13);
            }
//            ContextSdk.setCustomVariable("REF",refNo,MainActivity.this);
            ContextSdk.setCustomVariable("OTP",otp,MainActivity.this);
        }

        showNotifDialog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    msgBody = Utility.readSMS(MainActivity.this);
                    if(msgBody.length()>0 && msgBody!=null){
//                        refNo = msgBody.substring(msgBody.indexOf("REF#") + 4, msgBody.indexOf("REF#") + 10);
                        otp = msgBody.substring(msgBody.indexOf("OTP is ") + 7, msgBody.indexOf("OTP is") + 13);
                    }

//                    ContextSdk.setCustomVariable("REF",refNo,MainActivity.this);
                    ContextSdk.setCustomVariable("OTP",otp,MainActivity.this);

                } else {

                }
                return;
            }
        }
    }



    @Override
    protected void onResume() {
        super.onResume();

        if (!isNotifDlgVisible) {
            showNotifDialog();

            HashMap<String, Object> map = new HashMap<>();
            map.put("state", false);
            ContextSdk.tagEventObj("NotificationState", map, MainActivity.this);
        }
        else if (Utility.hasNotificationAccess(getApplicationContext())) {
            if (notifDialog != null && notifDialog.isShowing()) {
                isNotifDlgVisible = false;
                notifDialog.cancel();
                notifDialog = null;
            }

            HashMap<String, Object> map = new HashMap<>();
            map.put("state", true);
            ContextSdk.tagEventObj("NotificationState", map, MainActivity.this);
        }
    }

    private boolean isNotifDlgVisible = true;
    private AlertDialog notifDialog = null;
    private Timer notifTimer = null;

    private void showNotifDialog() {
        if (!Utility.hasNotificationAccess(getApplicationContext())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Notification Access");
            builder.setMessage("Please Enable Notification Access");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Utility.openNotificationAccess(MainActivity.this);
                }
            });
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    isNotifDlgVisible = false;
                }
            });
            builder.setCancelable(false);

            notifDialog = builder.create();
            notifDialog.show();
            isNotifDlgVisible = true;

            // Check for enabled state of notification permission
            notifTimer = new Timer();
            notifTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (Utility.hasNotificationAccess(getApplicationContext())) {
                        // Push back self on screen
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);

                        if(notifTimer!=null ){
                            notifTimer.cancel();
                            notifTimer.purge();
                            notifTimer = null;
                        }
                    }
                }
            }, 1000, 1000);
        } else if (notifDialog != null && notifDialog.isShowing()) {
            isNotifDlgVisible = false;
            notifDialog.cancel();
            notifDialog = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 100) {
            if(resultCode == RESULT_OK) {
                CheckBox checkBox = findViewById(R.id.checkbox_tnc);
                checkBox.setChecked(true);
                checkBox.setEnabled(false);

                Button btnTermsNCondtns = findViewById(R.id.btn_tnc);
                /*if(btnTermsNCondtns.getTag().equals("Selected")) {
                    Utility.hideIcon(MainActivity.this);
                }*/
                btnTermsNCondtns.setText("DONE");
                btnTermsNCondtns.setTag(new String("Selected"));

                ComSharedPref.saveBooleanPreferences(ComSharedPref.tncCheckedStatus,true, MainActivity.this);
                //Utility.hideIcon(MainActivity.this);
            }
        }
    }
}
