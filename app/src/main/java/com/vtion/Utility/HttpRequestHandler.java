package com.vtion.Utility;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class HttpRequestHandler {
    private static final String TAG = HttpRequestHandler.class.getSimpleName();

    public HttpRequestHandler() {
    }

    public String requestGet(String reqUrl) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public String requestPost(String reqUrl, String postData) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(postData.getBytes());
            os.flush();
            os.close();
        }catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (ProtocolException e) {
                Log.e(TAG, "ProtocolException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        return response;
    }

    // HTTP POST request
    public String sendPost(String BaseUrl, String PostData,
                           HashMap<String, String> headers, boolean useSSL) throws Exception {

        BufferedReader in = null;
        HttpURLConnection con = null;
        StringBuffer sb = new StringBuffer("");
        String serverRes = "";

        try {
            URL obj = new URL(BaseUrl);

            /*SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(null, null, new java.security.SecureRandom());*/

            con = (HttpURLConnection) obj.openConnection();
            //con.setSSLSocketFactory(sc.getSocketFactory());

            //add reuqest type
            con.setRequestMethod("POST");

            //add reuqest headers
            if (headers != null && headers.size() > 0) {
                Set<String> keys = headers.keySet();
                if (keys != null && keys.size() > 0) {
                    Iterator<String> iterator = keys.iterator();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        con.setRequestProperty(key, headers.get(key));
                    }
                }
            }

            // Send post request
            con.setDoOutput(true);

            OutputStream os = con.getOutputStream();
            DataOutputStream wr = new DataOutputStream(os);
            wr.writeBytes(PostData);
            wr.flush();
            wr.close();
            os.close();

            int statusCode = con.getResponseCode();
            System.out.println("Status code is : "+statusCode);

            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            String NL = System.getProperty("line.separator");

            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine + NL);
            }

            serverRes = sb.toString();
            if (serverRes.contains("\n")) {
                serverRes.replaceAll("\n", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return serverRes;
    }

}
