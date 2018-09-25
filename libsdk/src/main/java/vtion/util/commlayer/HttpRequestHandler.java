package vtion.util.commlayer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import vtion.context.utility.Utility;

public class HttpRequestHandler {

    private int statusCode = 0;

    public int getStatusCode() {
        return statusCode;
    }

    // HTTP POST request
    public String sendPost(String BaseUrl, String PostData, HashMap<String, String> headers) {
        String serverRes = "";
        try {
            if (BaseUrl.startsWith("https"))
                serverRes = sendHttpsPost(BaseUrl, PostData, headers);
            else
                serverRes = sendHttpPost(BaseUrl, PostData, headers);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serverRes;
    }

    private String sendHttpsPost(String BaseUrl, String PostData, HashMap<String, String> headers) {

        long requestTime = System.nanoTime();

        BufferedReader in = null;
        HttpsURLConnection con = null;
        InputStream inStream = null;
        StringBuffer sb = new StringBuffer("");
        String serverRes = "";

        try {
            Utility.loginfo("RTime : " + requestTime + " , Vtion PostRequest : " + BaseUrl + " , PostData : " + PostData);

            URL obj = new URL(BaseUrl);

            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(null, null, new java.security.SecureRandom());

            con = (HttpsURLConnection) obj.openConnection();
            con.setSSLSocketFactory(sc.getSocketFactory());

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

            statusCode = con.getResponseCode();

            inStream = con.getInputStream();
            in = new BufferedReader(new InputStreamReader(inStream));
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
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (Exception e) {
                }
            }
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception e) {
                }
            }
        }

        Utility.loginfo("RTime : " + requestTime + " , Vtion PostResponse : StatusCode: " + statusCode + " , Res : " + serverRes);
        return serverRes;
    }

    private String sendHttpPost(String BaseUrl, String PostData, HashMap<String, String> headers) {

        long requestTime = System.nanoTime();

        BufferedReader in = null;
        HttpURLConnection con = null;
        InputStream inStream = null;
        StringBuffer sb = new StringBuffer("");
        String serverRes = "";

        try {
            Utility.loginfo("RTime : " + requestTime + " , Vtion PostRequest : " + BaseUrl + " , PostData : " + PostData);

            URL obj = new URL(BaseUrl);
            con = (HttpURLConnection) obj.openConnection();

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

            statusCode = con.getResponseCode();

            inStream = con.getInputStream();
            in = new BufferedReader(new InputStreamReader(inStream));
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
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (Exception e) {
                }
            }
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception e) {
                }
            }
        }

        Utility.loginfo("RTime : " + requestTime + " , Vtion PostResponse : StatusCode: " + statusCode + " , Res : " + serverRes);
        return serverRes;
    }

    // HTTP GET request
    public String sendGet(String BaseUrl, String GetData, HashMap<String, String> headers) {
        String serverRes = "";

        try {
            if (BaseUrl.startsWith("https"))
                serverRes = getHttpsReq(BaseUrl, GetData, headers);
            else
                serverRes = getHttpReq(BaseUrl, GetData, headers);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serverRes;
    }

    private String getHttpsReq(String BaseUrl, String GetData, HashMap<String, String> headers) {

        long requestTime = System.nanoTime();

        InputStream inStream = null;
        BufferedReader in = null;
        HttpsURLConnection con = null;
        StringBuffer sb = new StringBuffer("");
        String serverRes = "";

        try {
            Utility.loginfo("RTime : " + requestTime + " , Vtion Https GetRequest : " + BaseUrl + " , Data : " + GetData);

            URL obj = new URL(BaseUrl + GetData);

            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(null, null, new java.security.SecureRandom());

            con = (HttpsURLConnection) obj.openConnection();
            con.setSSLSocketFactory(sc.getSocketFactory());

            //add reuqest type
            con.setRequestMethod("GET");

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

            statusCode = con.getResponseCode();

            inStream = con.getInputStream();
            in = new BufferedReader(new InputStreamReader(inStream));
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
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (Exception e) {
                }
            }
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception e) {
                }
            }
        }

        Utility.loginfo("RTime : " + requestTime + " , Vtion Https GetResponse : StatusCode: " + statusCode + " , Res : " + serverRes);
        return serverRes;
    }

    private String getHttpReq(String BaseUrl, String GetData, HashMap<String, String> headers) {

        long requestTime = System.nanoTime();

        InputStream inStream = null;
        BufferedReader in = null;
        HttpURLConnection con = null;
        StringBuffer sb = new StringBuffer("");
        String serverRes = "";

        try {
            Utility.loginfo("RTime : " + requestTime + " , Vtion Http GetRequest : " + BaseUrl + " , Data : " + GetData);

            URL obj = new URL(BaseUrl + GetData);
            con = (HttpURLConnection) obj.openConnection();

            //add reuqest type
            con.setRequestMethod("GET");

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

            statusCode = con.getResponseCode();

            inStream = con.getInputStream();
            in = new BufferedReader(new InputStreamReader(inStream));
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
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (Exception e) {
                }
            }
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception e) {
                }
            }
        }

        Utility.loginfo("RTime : " + requestTime + " , Vtion Http GetResponse : StatusCode: " + statusCode + " , Res : " + serverRes);
        return serverRes;
    }
}
