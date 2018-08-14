package com.vtion.kantarradio;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.vtion.Utility.ComSharedPref;
import com.vtion.Utility.Utility;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import semusi.activitysdk.ContextSdk;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private String msgBody = "";
    private String refNo = "", otp = "";
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setTitle("Kantar Metrics");

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        /*Button btnTermsNCondtns = findViewById(R.id.btn_tnc);
        btnTermsNCondtns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox tcBox = findViewById(R.id.checkbox_tnc);
                tcBox.setSelected(true);

                Intent intent = new Intent(MainActivity.this, TermsNConditionsActivity.class);
                startActivityForResult(intent,100);
            }
        });
        btnTermsNCondtns.setTag(new Integer(100));*/

//        CheckBox checkBox = findViewById(R.id.checkbox_tnc);
//        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked) {
//                    LinkedHashMap<String,Object> smsDetailsMap = new LinkedHashMap<>();
//                    smsDetailsMap.put("REF", refNo);
//                    smsDetailsMap.put("OTP", otp);
//                    smsDetailsMap.put("Name", ComSharedPref.loadStringSavedPreferences("sp_sender_name",MainActivity.this));
//                    smsDetailsMap.put("Msg", msgBody);
//                    smsDetailsMap.put("Time",ComSharedPref.loadStringSavedPreferences("sp_msg_date",MainActivity.this));
//                    ContextSdk.tagEventObj("Registration",smsDetailsMap,MainActivity.this);
//                    Utility.hideIcon(MainActivity.this);
//                }
//            }
//        });

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

        // Check for registration info sent
        try {
            boolean isSent = ComSharedPref.loadBooleanSavedPreferences("register_sent", getApplicationContext());
            if (!isSent) {
                // Gather installer info
                String installRef = ContextSdk.getReferrer(getApplicationContext());
                System.out.println("vtion Ref Info : " + installRef);
                if (installRef != null && installRef.length() > 0) {
                    // Send registration event with info
                    HashMap<String, Object> map = new HashMap<>();
                    String[] arr = installRef.split("&");
                    if (arr != null && arr.length > 1) {
                        for (String val : arr) {
                            String[] sub = val.split("=");
                            map.put(sub[0], sub[1]);
                        }
                        if (map != null && map.size() > 0) {
                            ContextSdk.tagEventObj("Registration", map, getApplicationContext());
                            ComSharedPref.saveBooleanPreferences("register_sent", true, getApplicationContext());
                        }
                    }
                }
            }
        } catch (Exception e) {
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

        if (!isNotifDlgVisible) {
            showNotifDialog();

            HashMap<String, Object> map = new HashMap<>();
            map.put("state", false);
            ContextSdk.tagEventObj("NotificationState", map, MainActivity.this);
        } else if (Utility.hasNotificationAccess(getApplicationContext())) {
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

                        notifTimer.cancel();
                        notifTimer.purge();
                        notifTimer = null;
                    }
                }
            }, 1000, 1000);
        } else if (notifDialog != null && notifDialog.isShowing()) {
            isNotifDlgVisible = false;
            notifDialog.cancel();
            notifDialog = null;
        }
    }

    /*private class RegisterTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;
        private  String responseJson = "",dataStr = "", messageStr="";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpRequestHandler httpRequestHandler = new HttpRequestHandler();

            JSONObject postDataJson = new JSONObject();
            try {
                postDataJson.put("age",10);
                postDataJson.put("gender","Male");
                postDataJson.put("profession","AD");
                postDataJson.put("code","+91");
                postDataJson.put("mno","9999999999");
                postDataJson.put("did",123112232);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            responseJson = httpRequestHandler.requestPost(UrlConstants.registerUrl, postDataJson.toString());

            Log.d(TAG,"Response json is : "+responseJson);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(progressDialog!= null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            if(responseJson != null) {
                try {
                    JSONObject rootJsonObject = new JSONObject(responseJson);
                    //messageStr = rootJsonObject.getString("msg");
                    //dataStr = rootJsonObject.getString("data");
                    Log.d(TAG,"Data fetched is : "+dataStr);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get response from server",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }

        }
    }*/

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 100) {
            if(resultCode == RESULT_OK) {
                CheckBox checkBox = findViewById(R.id.checkbox_tnc);
                checkBox.setChecked(true);

//                Utility.hideIcon(MainActivity.this);
            }
        }
    }*/
}
