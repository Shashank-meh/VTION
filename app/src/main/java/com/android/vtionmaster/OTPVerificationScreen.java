package com.android.vtionmaster;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.Utility.ComSharedPref;
import com.android.Utility.HttpRequestHandler;
import com.android.Utility.UrlConstants;
import com.android.Utility.Utility;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import semusi.activitysdk.ContextSdk;

public class OTPVerificationScreen extends AppCompatActivity {

    String otpStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification_screen);

        final EditText otp = findViewById(R.id.otp_et);

        TextView retryText = findViewById(R.id.tv_retry_url);
        final Button submit = findViewById(R.id.submit_btn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpStr = otp.getText().toString();
                if (otpStr != null && otpStr.length() > 0) {
                    if (Utility.isNetworkAvailable(OTPVerificationScreen.this)) {
                        new VerifyOTPTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Internet not available",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                }
            }
        });

        otp.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submit.performClick();
                    return true;
                }
                return false;
            }
        });

        retryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isNetworkAvailable(OTPVerificationScreen.this)) {
                    new RegisterTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Internet not available",
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        ComSharedPref.saveBooleanPreferences(ComSharedPref.sharedPrefsIsResultGenerated,
                false,OTPVerificationScreen.this);
        //setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    private class VerifyOTPTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;
        private String responseJson = "", isExistStr = "", messageStr = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(OTPVerificationScreen.this);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpRequestHandler httpRequestHandler = new HttpRequestHandler();

            JSONObject postDataJson = new JSONObject();
            try {
                postDataJson.put("uid", ComSharedPref.loadStringSavedPreferences(ComSharedPref.sharedPrefsUserId, OTPVerificationScreen.this));
                postDataJson.put("otp", otpStr);
                postDataJson.put("mno", ComSharedPref.loadStringSavedPreferences(ComSharedPref.sharedPrefsMobile, OTPVerificationScreen.this));
                postDataJson.put("did", 1111);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            HashMap<String, String> header = new HashMap<>();
            header.put("Content-Type", "application/json");

            //responseJson = httpRequestHandler.requestPost(UrlConstants.registerUrl, postDataJson.toString());
            try {
                responseJson = httpRequestHandler.sendPost(UrlConstants.verifyOTPUrl, postDataJson.toString(), header, false);
            } catch (Exception e) {
                System.out.println("Exception caught : " + e);
            }

            System.out.println("VerifyOTPTask : url is - " + UrlConstants.verifyOTPUrl + " , json is - " + postDataJson.toString());
            Log.d("VerifyOTPTask", "Response json is : " + responseJson);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if (responseJson != null) {
                try {
                    JSONObject rootJsonObject = new JSONObject(responseJson);
                    messageStr = rootJsonObject.getString("result");
                    Log.d("VerifyOTPTask", "Data fetched is : " + isExistStr);
                    if (messageStr.equals("true")) {

                        HashMap<String, String> map = new HashMap<>();
                        map.put("otp", otpStr);
                        ContextSdk.tagEvent("OTP", map, OTPVerificationScreen.this.getApplicationContext());

                        Toast.makeText(getApplicationContext(),
                                "Device successfully registered",
                                Toast.LENGTH_LONG)
                                .show();
                        ComSharedPref.saveStringPreferences(ComSharedPref.isUserRegistered,"true",OTPVerificationScreen.this);
                        ComSharedPref.saveBooleanPreferences(ComSharedPref.sharedPrefsIsResultGenerated,
                                true,OTPVerificationScreen.this);
                        //setResult(Activity.RESULT_OK);


                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Please enter correct OTP",
                                Toast.LENGTH_LONG)
                                .show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("VerifyOTPTask", "Couldn't get json from server.");
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
    }

    /*private class ResendOTPTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;
        private String responseJson = "", isExistStr = "", messageStr = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(OTPVerificationScreen.this);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpRequestHandler httpRequestHandler = new HttpRequestHandler();

            JSONObject postDataJson = new JSONObject();
            try {
                postDataJson.put("uid", ComSharedPref.loadStringSavedPreferences(ComSharedPref.sharedPrefsUserId, OTPVerificationScreen.this));
                postDataJson.put("code", "+91");
                postDataJson.put("mno", ComSharedPref.loadStringSavedPreferences(ComSharedPref.sharedPrefsMobile, OTPVerificationScreen.this));
                postDataJson.put("did", 1111);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            HashMap<String, String> header = new HashMap<>();
            header.put("Content-Type", "application/json");

            //responseJson = httpRequestHandler.requestPost(UrlConstants.registerUrl, postDataJson.toString());
            try {
                responseJson = httpRequestHandler.sendPost(UrlConstants.resendOTPUrl, postDataJson.toString(), header, false);
            } catch (Exception e) {
                System.out.println("Exception caught : " + e);
            }

            System.out.println("ResendOTPTask : url is - " + UrlConstants.resendOTPUrl + " , json is - " + postDataJson.toString());
            Log.d("ResendOTPTask", "Response json is : " + responseJson);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if (responseJson != null) {
                try {
                    JSONObject rootJsonObject = new JSONObject(responseJson);
                    messageStr = rootJsonObject.getString("msg");
                    Log.d("ResendOTPTask", "Data fetched is : " + isExistStr);
                    if (messageStr.equals("success")) {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ResendOTPTask", "Couldn't get json from server.");
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

    private class RegisterTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;
        private  String responseJson = "",isExistStr = "", messageStr="";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(OTPVerificationScreen.this);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpRequestHandler httpRequestHandler = new HttpRequestHandler();

            JSONObject postDataJson = new JSONObject();
            try {
                AdvertisingIdClient.Info idInfo = null;

                idInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());

                String advertId = null;
                advertId = idInfo.getId();
                int age  = Integer.parseInt(ComSharedPref.loadStringSavedPreferences(ComSharedPref.sharedPrefsAge,OTPVerificationScreen.this));

                postDataJson.put("age",age);
                postDataJson.put("gender",ComSharedPref.loadStringSavedPreferences(ComSharedPref.sharedPrefsGender,OTPVerificationScreen.this));
                postDataJson.put("profession",ComSharedPref.loadStringSavedPreferences(ComSharedPref.sharedPrefsProfession,OTPVerificationScreen.this));
                postDataJson.put("code","+91");
                postDataJson.put("mno",ComSharedPref.loadStringSavedPreferences(ComSharedPref.sharedPrefsMobile,OTPVerificationScreen.this));
                postDataJson.put("did",1111);
                postDataJson.put("appId","843TQ-278T-8TYUE-6BF37-5GIP2");
                postDataJson.put("stream-type","register");

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            HashMap<String, String> header = new HashMap<>();
            header.put("Content-Type","application/json");

            //responseJson = httpRequestHandler.requestPost(UrlConstants.registerUrl, postDataJson.toString());
            try {
                responseJson = httpRequestHandler.sendPost(UrlConstants.registerUrl, postDataJson.toString(),header,false);
            } catch (Exception e) {
                System.out.println("Exception caught : "+e);
            }

            System.out.println("RegisterTask1 : url is - "+UrlConstants.registerUrl+ " , json is - "+postDataJson.toString());
            Log.d("RegisterTask1","Response json is : "+responseJson);

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
                    messageStr = rootJsonObject.getString("msg");
                    isExistStr = rootJsonObject.getString("isExists");
                    String userId = rootJsonObject.getString("userid");
                    String streamType = rootJsonObject.getString("stream-type");

                    ComSharedPref.saveStringPreferences(ComSharedPref.sharedPrefsUserId,userId,OTPVerificationScreen.this);
                    Log.d("RegisterTask1","Data fetched is : "+isExistStr);

                    /*if(messageStr.equals("success")) {

                    }*/
                    Toast.makeText(getApplicationContext(),
                            "OTP sent successfully",
                            Toast.LENGTH_LONG)
                            .show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("RegisterTask", "Couldn't get json from server.");
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
    }
}
