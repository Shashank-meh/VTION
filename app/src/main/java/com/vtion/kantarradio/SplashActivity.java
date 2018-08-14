package com.vtion.kantarradio;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.vtion.Utility.HttpRequestHandler;
import com.vtion.Utility.UrlConstants;
import com.vtion.Utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //new CheckDateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

                finish();
            }
        }, 2500);
    }

    /*private class CheckDateTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;
        private String responseJson = "", resultStr = "", messageStr = "";
        private boolean isUserExists;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SplashActivity.this);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpRequestHandler httpRequestHandler = new HttpRequestHandler();

            HashMap<String, String> header = new HashMap<>();
            header.put("Content-Type", "application/x-www-form-urlencoded");

            String post = "appId=843TQ-278T-8TYUE-6BF37-5GIP2";
            //responseJson = httpRequestHandler.requestPost(UrlConstants.registerUrl, postDataJson.toString());
            try {
                responseJson = httpRequestHandler.sendPost(UrlConstants.checkDateTimeUrl, post, header, false);
            } catch (Exception e) {
                System.out.println("Exception caught : " + e);
            }

            System.out.println("CheckDateTask : url is - " + UrlConstants.checkDateTimeUrl + " , json is - " + post);
            Log.d("CheckDateTask", "Response json is : " + responseJson);

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
                    *//*
                     * {"result":true,"startDate":1528442859694,"endDate":1529452800}
                     * *//*
                    JSONObject root = new JSONObject(responseJson);
                    String startDate = root.optString("startDate");
                    String endDate = root.optString("endDate");
                    String message = root.optString("message");

                    long currentTime = System.currentTimeMillis()/1000;
                    System.out.println("Current time : "+currentTime+" End time - "+endDate);
                    if(currentTime >= Long.parseLong(endDate)) {
                        showExitDialog(message);
                    } else {
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);

                                finish();
                            }
                        }, 2500);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("CheckDateTask", "Couldn't get response from server.");
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

    public void showExitDialog(String message){
        AlertDialog builder = new AlertDialog.Builder(SplashActivity.this).create();
        builder.setTitle("Alert");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setButton(DialogInterface.BUTTON_POSITIVE, "Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();

    }*/
}
