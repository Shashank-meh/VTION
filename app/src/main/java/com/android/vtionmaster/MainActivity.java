package com.android.vtionmaster;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.Utility.ComSharedPref;
import com.android.Utility.Utility;
import com.facebook.login.LoginManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import semusi.activitysdk.ContextSdk;

public class MainActivity extends AppCompatActivity {

    /*private String TAG = MainActivity.class.getSimpleName();
    private String msgBody="";
    private String refNo="",otp="";*/
    private boolean isRegistered;
    String isRegisteredStr="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setTitle("Vtion Master");

        Button btnTermsNCondtns = findViewById(R.id.btn_register);

        btnTermsNCondtns.setTag(new Integer(100));
        isRegisteredStr = ComSharedPref.loadStringSavedPreferences(ComSharedPref.isUserRegistered,MainActivity.this);

        if(isRegisteredStr.equals("true")){
            btnTermsNCondtns.setText("Registered");
            btnTermsNCondtns.setEnabled(false);
        }

        btnTermsNCondtns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*CheckBox tcBox = findViewById(R.id.checkbox_tnc);
                tcBox.setSelected(true);*/

                Intent intent = new Intent(MainActivity.this, UpdateDetailsActivity.class);
                intent.putExtra("ActionBar",false);
                intent.putExtra("isRegistered",isRegisteredStr);
                startActivityForResult(intent,10);
            }
        });

        /*CheckBox checkBox = findViewById(R.id.checkbox_tnc);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    LinkedHashMap<String,Object> smsDetailsMap = new LinkedHashMap<>();
                    smsDetailsMap.put("REF", refNo);
                    smsDetailsMap.put("OTP", otp);
                    smsDetailsMap.put("Name", ComSharedPref.loadStringSavedPreferences("sp_sender_name",MainActivity.this));
                    smsDetailsMap.put("Msg", msgBody);
                    smsDetailsMap.put("Time",ComSharedPref.loadStringSavedPreferences("sp_msg_date",MainActivity.this));
                    ContextSdk.tagEventObj("Registration",smsDetailsMap,MainActivity.this);
                    Utility.hideIcon(MainActivity.this);
                }
            }
        });*/

        /*if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    10);
        } else {
            msgBody = Utility.readSMS(MainActivity.this);
//            System.out.println("DevTest: OTP is : "+msgBody.substring(msgBody.indexOf("REF#") + 4, msgBody.indexOf("REF#") + 10));
//            System.out.println("DevTest: OTP is : "+msgBody.substring(msgBody.indexOf("OTP is ") + 7, msgBody.indexOf("OTP is") + 11));
            if(msgBody.length()>0 && msgBody!=null) {
                refNo = msgBody.substring(msgBody.indexOf("REF#") + 4, msgBody.indexOf("REF#") + 10);
                otp = msgBody.substring(msgBody.indexOf("OTP is ") + 7, msgBody.indexOf("OTP is") + 11);
            }
            ContextSdk.setCustomVariable("REF",refNo,MainActivity.this);
            ContextSdk.setCustomVariable("OTP",otp,MainActivity.this);
        }*/

        showNotifDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean isResultGen = ComSharedPref.loadBooleanSavedPreferences(ComSharedPref.sharedPrefsIsResultGenerated,MainActivity.this);
        Button btnTermsNCondtns = findViewById(R.id.btn_register);
        if (isResultGen) {
            btnTermsNCondtns.setText("Registered");
            btnTermsNCondtns.setEnabled(false);
            isRegistered = true;
            isRegisteredStr = ComSharedPref.loadStringSavedPreferences(ComSharedPref.isUserRegistered,MainActivity.this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public void showLogoutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Alert");
        builder.setCancelable(false);
        builder.setMessage("Are you sure you want to logout ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ComSharedPref.saveBooleanPreferences(ComSharedPref.isUserSignedIn,
                        false,MainActivity.this);
                Intent intent = new Intent(getApplicationContext(), PinValidationActivity.class);
                startActivity(intent);

                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);

        notifDialog = builder.create();
        notifDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Intent intent = new Intent(MainActivity.this, SettingsScreen.class);
                startActivity(intent);
                return true;

            case R.id.action_profile:
                Intent intentPro = new Intent(MainActivity.this, UpdateDetailsActivity.class);
                intentPro.putExtra("ActionBar",true);
                intentPro.putExtra("isRegistered",isRegisteredStr);
                startActivity(intentPro);
                return true;

            case R.id.action_logout:
//                showLogoutDialog();
                LoginManager.getInstance().logOut();
                ComSharedPref.saveBooleanPreferences(ComSharedPref.isUserSignedIn,
                        false,MainActivity.this);
                Intent loginIntent = new Intent(getApplicationContext(), PinValidationActivity.class);
                startActivity(loginIntent);

                finish();

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }



    /*@Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    msgBody = Utility.readSMS(MainActivity.this);
                    if(msgBody.length()>0 && msgBody!=null){
                        refNo = msgBody.substring(msgBody.indexOf("REF#") + 4, msgBody.indexOf("REF#") + 10);
                        otp = msgBody.substring(msgBody.indexOf("OTP is ") + 7, msgBody.indexOf("OTP is") + 11);
                    }

                    ContextSdk.setCustomVariable("REF",refNo,MainActivity.this);
                    ContextSdk.setCustomVariable("OTP",otp,MainActivity.this);

                } else {

                }
                return;
            }
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        /*String isRegistered = ComSharedPref.loadStringSavedPreferences(ComSharedPref.isUserRegistered,MainActivity.this);
        if(isRegistered.equals("true")){
            Button btnTermsNCondtns = findViewById(R.id.btn_register);
            btnTermsNCondtns.setText("Registered");
            btnTermsNCondtns.setEnabled(false);
        }*/

        if (!isNotifDlgVisible) {
            showNotifDialog();

            HashMap<String, String> map = new HashMap<>();
            map.put("state", "false");
            ContextSdk.tagEvent("NotificationState", map, MainActivity.this);
        }
        else if (Utility.hasNotificationAccess(getApplicationContext())) {
            if (notifDialog != null && notifDialog.isShowing()) {
                isNotifDlgVisible = false;
                notifDialog.cancel();
                notifDialog = null;
            }

            HashMap<String, String> map = new HashMap<>();
            map.put("state", "true");
            ContextSdk.tagEvent("NotificationState", map, MainActivity.this);
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

                        if(notifTimer!=null) {
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
}
