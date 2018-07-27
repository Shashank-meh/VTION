package com.android.vtionmaster;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
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

public class UpdateDetailsActivity extends AppCompatActivity {

    private Spinner codeSpinner, genderSpinner;
    private EditText etMobile, etAge, etProfession;
    private TextView tncText;
    private CheckBox tnc_checkbox;
    private Button submitBtn;
    private boolean isAction;
    private String isRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_details);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        isAction = getIntent().getBooleanExtra("ActionBar",false);
        isRegistered = getIntent().getStringExtra("isRegistered");
        System.out.println("Value of isRegistered : "+isRegistered);

        etMobile = findViewById(R.id.et_mobile);
        etAge = findViewById(R.id.et_age);
        etProfession = findViewById(R.id.et_profession);

        tnc_checkbox = findViewById(R.id.tnc_checkbox);
        tnc_checkbox.setChecked(true);

        tncText = findViewById(R.id.tnc_text);
        tncText.setClickable(true);
        tncText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTNC = new Intent(UpdateDetailsActivity.this, TermsNConditionsActivity.class);
                startActivityForResult(intentTNC,111);
            }
        });

        codeSpinner = findViewById(R.id.spinner_code);
        genderSpinner = findViewById(R.id.spinner_gender);

        submitBtn = findViewById(R.id.btn_submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hit post request and switch to otp verification screen
                String mNo = etMobile.getText().toString();
                String age = etAge.getText().toString();
                String pofession = etProfession.getText().toString();
                if((mNo.length() > 0 && mNo != null)
                        && (age.length() > 0 && age != null)
                        && (pofession.length() > 0 && pofession != null)) {

                    if(tnc_checkbox.isChecked()) {
                        ComSharedPref.saveStringPreferences(ComSharedPref.sharedPrefsMobile,
                                etMobile.getText().toString(),
                                UpdateDetailsActivity.this);
                        ComSharedPref.saveStringPreferences(ComSharedPref.sharedPrefsAge,
                                etAge.getText().toString(),
                                UpdateDetailsActivity.this);
                        ComSharedPref.saveStringPreferences(ComSharedPref.sharedPrefsGender,
                                genderSpinner.getSelectedItem().toString(),
                                UpdateDetailsActivity.this);
                        ComSharedPref.saveStringPreferences(ComSharedPref.sharedPrefsProfession,
                                etProfession.getText().toString(),
                                UpdateDetailsActivity.this);

                        if(Utility.isNetworkAvailable(UpdateDetailsActivity.this)) {
                            new RegisterTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Internet not available",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Please agree to Terms & Conditions",
                                Toast.LENGTH_LONG)
                                .show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Fields cannot be left blank",
                            Toast.LENGTH_LONG)
                            .show();
                }

            }
        });

        /*tnc_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                *//*if(isChecked) {
                    LinkedHashMap<String,Object> smsDetailsMap = new LinkedHashMap<>();
                    *//**//*smsDetailsMap.put("REF", refNo);
                    smsDetailsMap.put("OTP", otp);
                    smsDetailsMap.put("Name", ComSharedPref.loadStringSavedPreferences("sp_sender_name",MainActivity.this));
                    smsDetailsMap.put("Msg", msgBody);
                    smsDetailsMap.put("Time",ComSharedPref.loadStringSavedPreferences("sp_msg_date",MainActivity.this));*//**//*

                    ContextSdk.tagEventObj("Registration",smsDetailsMap,UpdateDetailsActivity.this);
                    //Utility.hideIcon(MainActivity.this);
                }*//*
            }
        });*/
        //adapter for code spinner
        String[] codeArray = {"+91"};
        ArrayAdapter<String> codeSpinnerArrayAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item,
                        codeArray);
        codeSpinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        codeSpinner.setAdapter(codeSpinnerArrayAdapter);

        //adapter for gender spinner
        String[] genderArray = {"Male", "Female"};
        ArrayAdapter<String> genderSpinnerArrayAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item,
                        genderArray);
        genderSpinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderSpinnerArrayAdapter);

        if(isAction) {
            submitBtn.setText("Update");
            etMobile.setText(ComSharedPref.loadStringSavedPreferences(ComSharedPref.sharedPrefsMobile,
                    UpdateDetailsActivity.this));
            etAge.setText(ComSharedPref.loadStringSavedPreferences(ComSharedPref.sharedPrefsAge,
                    UpdateDetailsActivity.this));
            etProfession.setText(ComSharedPref.loadStringSavedPreferences(ComSharedPref.sharedPrefsProfession,
                    UpdateDetailsActivity.this));
            String gender = ComSharedPref.loadStringSavedPreferences(ComSharedPref.sharedPrefsGender,
                    UpdateDetailsActivity.this);
            if(gender.equals("Male")) {
                genderSpinner.setSelection(0);
            } else if (gender.equals("Female")) {
                genderSpinner.setSelection(1);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(isAction){
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 111) {
            if(resultCode == RESULT_OK) {
                tnc_checkbox.setChecked(true);
            }
        } /*else if (requestCode == 10) {

        }*/
    }

    private class RegisterTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;
        private  String responseJson = "",isExistStr = "", messageStr="";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UpdateDetailsActivity.this);
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
                int age  = Integer.parseInt(ComSharedPref.loadStringSavedPreferences(ComSharedPref.sharedPrefsAge,UpdateDetailsActivity.this));

                postDataJson.put("age",age);
                postDataJson.put("gender",ComSharedPref.loadStringSavedPreferences(ComSharedPref.sharedPrefsGender,UpdateDetailsActivity.this));
                postDataJson.put("profession",ComSharedPref.loadStringSavedPreferences(ComSharedPref.sharedPrefsProfession,UpdateDetailsActivity.this));
                postDataJson.put("code","+91");
                postDataJson.put("mno",ComSharedPref.loadStringSavedPreferences(ComSharedPref.sharedPrefsMobile,UpdateDetailsActivity.this));
                postDataJson.put("did",1111);
                postDataJson.put("appId","843TQ-278T-8TYUE-6BF37-5GIP2");
                if(isRegistered.equals("true")) {
                    postDataJson.put("stream-type","update");
                } else if (isRegistered.equals("")) {
                    postDataJson.put("stream-type","register");
                }
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

            System.out.println("RegisterTask : url is - "+UrlConstants.registerUrl+ " , json is - "+postDataJson.toString());
            Log.d("RegisterTask","Response json is : "+responseJson);

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
                    ComSharedPref.saveStringPreferences(ComSharedPref.sharedPrefsUserId,userId,UpdateDetailsActivity.this);
                    Log.d("RegisterTask","Data fetched is : "+isExistStr);
                    if(streamType.equals("update")) {
                        Toast.makeText(getApplicationContext(),
                                "User details updated",
                                Toast.LENGTH_LONG)
                                .show();
                    } /*else if (streamType.equals("register")){
                        Toast.makeText(getApplicationContext(),
                                "Device successfully registered",
                                Toast.LENGTH_LONG)
                                .show();
                    }*/

                    if(messageStr.equals("success")) {
                        if(isRegistered.equals("true")){
                            finish();
                        } else if (isRegistered.equals("")) {
                            Intent intentOtp = new Intent(UpdateDetailsActivity.this, OTPVerificationScreen.class);
                            startActivityForResult(intentOtp,10);
                            finish();
                        }
                    }

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
