package com.vtion.kantarradio;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.vtion.Utility.HttpRequestHandler;
import com.vtion.Utility.UrlConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class TermsNConditionsActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_nconditions);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = findViewById(R.id.webview);

        Button btnAccept = findViewById(R.id.btn_accept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK,getIntent());
                finish();
            }
        });

        GetTermsTask object = new GetTermsTask();
        if (Build.VERSION.SDK_INT < 11)
            object.execute();
        else
            object.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class GetTermsTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog = null;
        private  String dataStr = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(TermsNConditionsActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpRequestHandler httpRequestHandler = new HttpRequestHandler();
            String responseJson = httpRequestHandler.requestGet(UrlConstants.tncUrl);
            if(responseJson != null) {
                try {
                    JSONObject rootJsonObject = new JSONObject(responseJson);
                    dataStr = rootJsonObject.optString("data", "");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(progressDialog!= null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            if(dataStr != null) {
                webView.loadData(dataStr, "text/html", "UTF-8");
            } else {
                Toast.makeText(getApplicationContext(),
                        "Couldn't get response from server",
                        Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
}
