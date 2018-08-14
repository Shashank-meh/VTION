//package com.vtion.kantarradio;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.vtion.Utility.ComSharedPref;
//import com.vtion.Utility.HttpRequestHandler;
//import com.vtion.Utility.UrlConstants;
//import com.vtion.Utility.Utility;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//
//import semusi.activitysdk.ContextSdk;
//
//public class PhoneValidationActivity extends AppCompatActivity {
//
//    private EditText phoneInput;
//    private CheckBox tncBox;
//    private Button submit;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_phone_validation);
//
//        phoneInput = findViewById(R.id.phone_input);
//        tncBox = findViewById(R.id.checkbox_tnc);
//        submit = findViewById(R.id.btn_submit);
//
//        submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String phoneStr = phoneInput.getText().toString().trim();
//                if(phoneStr.length() < 10) {
//                    phoneInput.setError("Requires 10-digit phone number");
//                } else {
//                    phoneInput.setError(null);
//                    if(tncBox.isChecked()) {
//                        if(Utility.isNetworkAvailable(PhoneValidationActivity.this)){
//                            new SubmitPhoneTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                        } else {
//                            Toast.makeText(PhoneValidationActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(PhoneValidationActivity.this, "Please accept the terms and conditions to continue", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });
//
//        tncBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(PhoneValidationActivity.this, TermsNConditionsActivity.class);
//                startActivityForResult(intent,100);
//            }
//        });
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode == 100) {
//            if(resultCode == RESULT_OK) {
//                tncBox.setChecked(true);
//                tncBox.setEnabled(false);
//
//                //Utility.hideIcon(MainActivity.this);
//            }
//        }
//    }
//
//    private class SubmitPhoneTask extends AsyncTask<Void, Void, Void> {
//        private ProgressDialog progressDialog;
//        private String responseJson = "", resultStr = "", messageStr = "";
//        private boolean isUserExists;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressDialog = new ProgressDialog(PhoneValidationActivity.this);
//            progressDialog.setMessage("Please Wait...");
//            progressDialog.setCancelable(false);
//            progressDialog.show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            HttpRequestHandler httpRequestHandler = new HttpRequestHandler();
//
//            HashMap<String, String> header = new HashMap<>();
//            header.put("Content-Type", "application/json");
//
//            JSONObject postData = new JSONObject();
//            try {
//                postData.put("age","30");
//                postData.put("gender","Male");
//                postData.put("profession", "Test");
//                postData.put("code","+91");
//                postData.put("mno",phoneInput.getText().toString().trim());
//                postData.put("did",1111);
//                postData.put("appId","8345Q-278T-8TYUE-6BF37-5G123");
//                postData.put("stream-type","register");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            //responseJson = httpRequestHandler.requestPost(UrlConstants.registerUrl, postDataJson.toString());
//            try {
//                responseJson = httpRequestHandler.sendPost(UrlConstants.submitPhoneTask, postData.toString(), header, false);
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("Exception caught : " + e);
//            }
//
//            System.out.println("SubmitPhoneTask : url is - " + UrlConstants.submitPhoneTask + " , json is - " + postData.toString());
//            Log.d("SubmitPhoneTask", "Response json is : " + responseJson);
//
//            return null;
//        }
//
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            if (progressDialog != null && progressDialog.isShowing()) {
//                progressDialog.dismiss();
//            }
//
//            if (responseJson != null) {
//                try {
//                    JSONObject rootJsonObject = new JSONObject(responseJson);
//                    String messageStr = rootJsonObject.getString("msg");
//                    boolean isExistStr = rootJsonObject.getBoolean("isExists");
//                    String userId = rootJsonObject.getString("userid");
//                    String streamType = rootJsonObject.getString("stream-type");
//
//                    if(isExistStr == false) {
//                        LinkedHashMap<String,Object> smsDetailsMap = new LinkedHashMap<>();
//                        smsDetailsMap.put("Phone", phoneInput.getText().toString().trim());
//                        ContextSdk.tagEventObj("Registration",smsDetailsMap,PhoneValidationActivity.this);
//
//                        Toast.makeText(PhoneValidationActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                        startActivity(intent);
//
//                        finish();
//                    } else if(isExistStr == true){
//                        Toast.makeText(PhoneValidationActivity.this, "This phone number is already registered", Toast.LENGTH_SHORT).show();
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                Log.e("SubmitPhoneTask", "Couldn't get response from server.");
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(),
//                                "Couldn't get response from server",
//                                Toast.LENGTH_LONG)
//                                .show();
//                    }
//                });
//            }
//
//        }
//    }
//}
