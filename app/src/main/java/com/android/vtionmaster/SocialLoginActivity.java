package com.android.vtionmaster;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.Utility.ComSharedPref;
import com.android.Utility.HttpRequestHandler;
import com.android.Utility.UrlConstants;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

public class SocialLoginActivity extends AppCompatActivity/* implements View.OnClickListener*/ {

    private CallbackManager callbackManager;
    private AccessToken accessToken;
    private LoginButton fbLoginButton;
    private boolean isFBRequestSuccessful;
    private static final String EMAIL = "email";
    private static final String GENDER = "user_gender";
    private static final String BIRTHDAY = "user_birthday";
    private static final String LOCATION = "user_location";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_login);

        callbackManager = CallbackManager.Factory.create();
        fbLoginButton = findViewById(R.id.fblogin_button);
        fbLoginButton.setReadPermissions(Arrays.asList(EMAIL));
        fbLoginButton.setReadPermissions(Arrays.asList(GENDER));
        fbLoginButton.setReadPermissions(Arrays.asList(BIRTHDAY));
        fbLoginButton.setReadPermissions(Arrays.asList(LOCATION));

//        fbLoginButton.setOnClickListener(this);

        accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("Inside onSuccess - ");
                fbLoginButton.setVisibility(View.GONE);
                ComSharedPref.saveBooleanPreferences(ComSharedPref.isUserSignedIn,
                        true, SocialLoginActivity.this);
                isFBRequestSuccessful = true;

            }

            @Override
            public void onCancel() {
                System.out.println("Inside onCancel");
                Toast.makeText(SocialLoginActivity.this, "Request cancelled", Toast.LENGTH_LONG).show();
                isFBRequestSuccessful = false;
            }

            @Override
            public void onError(FacebookException error) {
                System.out.println("Inside onError - " + error.getLocalizedMessage());
                Toast.makeText(SocialLoginActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                isFBRequestSuccessful = false;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Inside onActivityResult");

        /*Profile profile = Profile.getCurrentProfile()
        System.out.println("Inside onActivityResult2, name is - "+profile.getFirstName());*/

        if (isFBRequestSuccessful) {
            accessToken = AccessToken.getCurrentAccessToken();
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                final JSONObject object,
                                GraphResponse response) {
                            // Application code
                            final JSONObject jsonObject = response.getJSONObject();
                            try {
                                String name = jsonObject.getString("name");
                                String gender = jsonObject.optString("gender");
                                String birthday = jsonObject.optString("birthday");
                                System.out.println("Raw json : " + jsonObject.toString());

                                JSONObject locationObj = jsonObject.getJSONObject("location");
                                String locationId = locationObj.optString("id");
                                String locationName = locationObj.optString("name");


                                System.out.println("Gender is : " + gender +
                                        " Birthday : " + birthday + " Location : " + locationName);

                                int userAge = getAge(birthday);

                                ComSharedPref.saveStringPreferences(ComSharedPref.sharedPrefsGender,
                                        gender, SocialLoginActivity.this);
                                ComSharedPref.saveStringPreferences(ComSharedPref.userName,
                                        name, SocialLoginActivity.this);
                                ComSharedPref.saveStringPreferences(ComSharedPref.userBirthday,
                                        birthday, SocialLoginActivity.this);
                                ComSharedPref.saveStringPreferences(ComSharedPref.sharedPrefsAge,
                                        "" + userAge, SocialLoginActivity.this);
                                ComSharedPref.saveStringPreferences(ComSharedPref.userLocation,
                                        locationName, SocialLoginActivity.this);

                                Intent mainLauncher = new Intent(SocialLoginActivity.this, MainActivity.class);
                                startActivity(mainLauncher);
                                finish();

                            } catch (JSONException e) {
                                e.printStackTrace();
                                LoginManager.getInstance().logOut();
                            } catch (NullPointerException e) {
                                System.out.println("Unable to fetch data from Facebook");
                                LoginManager.getInstance().logOut();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "name,gender,birthday,location");
            request.setParameters(parameters);
            request.executeAsync();
        }

    }

    private int getAge(String age) {
        String[] ageComponents = age.split("/");
        int month = Integer.parseInt(ageComponents[0]);
        int date = Integer.parseInt(ageComponents[1]);
        int year = Integer.parseInt(ageComponents[2]);

        System.out.println("Age components " + month + " " + date + " " + year);

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, date);

        int userAge = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            userAge--;
        }

        return userAge;
    }

    /*@Override
    public void onClick(View v) {
        LoginManager.getInstance().logInWithReadPermissions(this,Arrays.asList("public_profile"));

    }*/

    private class SocialRegisterTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;
        private String responseJson = "", messageStr = "",source="";

        /*
        * URL : http://api.vtion.in/Registerusersource
        Verb : POST
        content-type: application/json
        request : {"did":"f340fbc2-b825-4dce-9685-47e9c3f3b0c1",
        "uname":"Saurabh","uage":"18","up":"Salary","city":"Gorakhpur",
        "mno":"7827188996","appId":"24236742378","ug":"Male","ubd":"12/02/1994",
        "ue":"saurabh.singh@semusi.com","uls":"Facebook"}
        response : {"response":true,"message":"User Registered Successfully"}
        * */
        public SocialRegisterTask(String source){
            this.source = source;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SocialLoginActivity.this);
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
                int age = Integer.parseInt(ComSharedPref.loadStringSavedPreferences(ComSharedPref.sharedPrefsAge, SocialLoginActivity.this));

                postDataJson.put("did", 1111);
                postDataJson.put("uname", ComSharedPref.loadStringSavedPreferences(ComSharedPref.userName,
                        SocialLoginActivity.this));
                postDataJson.put("uage", age);
                if(source.equalsIgnoreCase("facebook")){
                    postDataJson.put("up","null");
                }
                postDataJson.put("city",ComSharedPref.loadStringSavedPreferences(ComSharedPref.userLocation,
                        SocialLoginActivity.this));
                postDataJson.put("ug", ComSharedPref.loadStringSavedPreferences(ComSharedPref.sharedPrefsGender,
                        SocialLoginActivity.this));
                postDataJson.put("ubd",ComSharedPref.loadStringSavedPreferences(ComSharedPref.userBirthday,
                        SocialLoginActivity.this));
                postDataJson.put("ue","");
                postDataJson.put("uls",source);
//                postDataJson.put("profession",ComSharedPref.loadStringSavedPreferences(ComSharedPref.sharedPrefsProfession,UpdateDetailsActivity.this));
//                postDataJson.put("code","+91");
//                postDataJson.put("mno", ComSharedPref.loadStringSavedPreferences(ComSharedPref.sharedPrefsMobile, UpdateDetailsActivity.this));
                postDataJson.put("appId", "843TQ-278T-8TYUE-6BF37-5GIP2");
                /*if (isRegistered.equals("true")) {
                    postDataJson.put("stream-type", "update");
                } else if (isRegistered.equals("")) {
                    postDataJson.put("stream-type", "register");
                }*/
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
            header.put("Content-Type", "application/json");

            //responseJson = httpRequestHandler.requestPost(UrlConstants.registerUrl, postDataJson.toString());
            try {
                responseJson = httpRequestHandler.sendPost(UrlConstants.socialRegisterUrl, postDataJson.toString(), header, false);
            } catch (Exception e) {
                System.out.println("Exception caught : " + e);
            }

            System.out.println("SocialRegisterTask : url is - " + UrlConstants.socialRegisterUrl + " , json is - " + postDataJson.toString());
            Log.d("SocialRegisterTask", "Response json is : " + responseJson);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            String age = ComSharedPref.loadStringSavedPreferences(ComSharedPref.sharedPrefsAge, SocialLoginActivity.this);
            String gender = ComSharedPref.loadStringSavedPreferences(ComSharedPref.sharedPrefsGender, SocialLoginActivity.this);
            String profession = ComSharedPref.loadStringSavedPreferences(ComSharedPref.sharedPrefsProfession, SocialLoginActivity.this);
            String mobile = ComSharedPref.loadStringSavedPreferences(ComSharedPref.sharedPrefsMobile, SocialLoginActivity.this);

            if (responseJson != null) {
                try {
                    JSONObject rootJsonObject = new JSONObject(responseJson);
                    messageStr = rootJsonObject.getString("message");
                    String responseStr = rootJsonObject.optString("response");
                    /*ComSharedPref.saveStringPreferences(ComSharedPref.sharedPrefsUserId, userId, SocialLoginActivity.this);
                    Log.d("RegisterTask", "Data fetched is : " + isExistStr);*/

                    if(responseStr.equalsIgnoreCase("true")){
                        Intent mainLauncher = new Intent(SocialLoginActivity.this, MainActivity.class);
                        startActivity(mainLauncher);
                        finish();
                    } else if (responseStr.equalsIgnoreCase("false")) {

                    }
                    /*if (messageStr.equals("success")) {

                        if (streamType.equals("update")) {
                            Toast.makeText(getApplicationContext(),
                                    "User details updated",
                                    Toast.LENGTH_LONG)
                                    .show();

                            HashMap<String, String> map = new HashMap<>();
                            map.put("mobile", mobile);
                            map.put("age", age);
                            map.put("gender", gender);
                            map.put("profession", profession);
                            //map.put("pin","432232");

                            ContextSdk.tagEvent("Profile", map, SocialLoginActivity.this.getApplicationContext());
                            ContextSdk.setCustomVariable("LoginPin", "432232", SocialLoginActivity.this);

                        } else if (streamType.equals("register")) {
                        *//*Toast.makeText(getApplicationContext(),
                                "Device successfully registered",
                                Toast.LENGTH_LONG)
                                .show();*//*
                            HashMap<String, String> map = new HashMap<>();
                            map.put("mobile", mobile);
                            map.put("age", age);
                            map.put("gender", gender);
                            map.put("profession", profession);
                            map.put("pin", "443239");

                            HashMap<String, String> map1 = new HashMap<>();
                            map1.put("pin", "443239");
                            ContextSdk.tagEvent("PinLogin", map1, UpdateDetailsActivity.this.getApplicationContext());

                            ContextSdk.tagEvent("Register", map, UpdateDetailsActivity.this.getApplicationContext());
                            ContextSdk.setCustomVariable("LoginPin", "432232", UpdateDetailsActivity.this);
                        }

                        if (isRegistered.equals("true")) {
                            finish();
                        } else if (isRegistered.equals("")) {
                            Intent intentOtp = new Intent(UpdateDetailsActivity.this, OTPVerificationScreen.class);
                            startActivityForResult(intentOtp, 10);
                            finish();
                        }
                    }*/

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("SocialRegisterTask", "Couldn't get json from server.");
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
