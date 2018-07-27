package com.android.vtionmaster;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.Utility.ComSharedPref;
import com.android.Utility.HttpRequestHandler;
import com.android.Utility.UrlConstants;
import com.android.Utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import semusi.activitysdk.Api;
import semusi.activitysdk.ContextSdk;

public class PinValidationActivity extends AppCompatActivity {

    String otpStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_validation);
        final EditText otp = findViewById(R.id.pin_et);
        Button submit = findViewById(R.id.submit_pin_btn);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                otpStr = otp.getText().toString();
                ComSharedPref.saveStringPreferences(ComSharedPref.sharedPrefsPin,
                        otpStr, PinValidationActivity.this);
                if (otpStr != null && otpStr.length() > 0) {
                    if (Utility.isNetworkAvailable(PinValidationActivity.this)) {
                        new VerifyPINTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Internet not available",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    private class VerifyPINTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;
        private String responseJson = "", resultStr = "", messageStr = "";
        private boolean isUserExists;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(PinValidationActivity.this);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpRequestHandler httpRequestHandler = new HttpRequestHandler();

            JSONObject postDataJson = new JSONObject();
            JSONObject basicDetailsJson = new JSONObject();
            JSONObject validationDetailsJson = new JSONObject();

            try {
                basicDetailsJson.put("imeiNumber", "123456789012342");
                basicDetailsJson.put("deviceId", Api.getDeviceId(PinValidationActivity.this));
                basicDetailsJson.put("latitude", "12.12");
                basicDetailsJson.put("longitude", "22.12");

                validationDetailsJson.put("apiAlias", "pin Login");
                validationDetailsJson.put("appId", "843TQ-278T-8TYUE-6BF37-5GIP2");
                validationDetailsJson.put("pin", otpStr);
                validationDetailsJson.put("Basic Details", basicDetailsJson);

                postDataJson.put("validation details", validationDetailsJson);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            HashMap<String, String> header = new HashMap<>();
            header.put("Content-Type", "application/json");

            //responseJson = httpRequestHandler.requestPost(UrlConstants.registerUrl, postDataJson.toString());
            try {
                responseJson = httpRequestHandler.sendPost(UrlConstants.deviceActivationURL, postDataJson.toString(), header, false);
            } catch (Exception e) {
                System.out.println("Exception caught : " + e);
            }

            System.out.println("VerifyPINTask : url is - " + UrlConstants.deviceActivationURL + " , json is - " + postDataJson.toString());
            Log.d("VerifyPINTask", "Response json is : " + responseJson);

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
                    resultStr = rootJsonObject.getString("resultStatus");
                    messageStr = rootJsonObject.getString("message");
                    if (resultStr.equals("true")) {
                        ContextSdk.setCustomVariable("Pin", otpStr, PinValidationActivity.this);

                        HashMap<String, String> map = new HashMap<>();
                        map.put("pin", otpStr);
                        ContextSdk.tagEvent("PinLogin", map, PinValidationActivity.this.getApplicationContext());

                        ComSharedPref.saveBooleanPreferences(ComSharedPref.isUserSignedIn,
                                true, PinValidationActivity.this);
                        Intent intent = new Intent(PinValidationActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (resultStr.equals("false")) {
                        Toast.makeText(getApplicationContext(),
                                messageStr,
                                Toast.LENGTH_LONG)
                                .show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("VerifyPINTask", "Couldn't get response from server.");
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
