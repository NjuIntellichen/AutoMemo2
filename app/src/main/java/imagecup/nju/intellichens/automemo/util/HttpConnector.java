package imagecup.nju.intellichens.automemo.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hanifor on 3/26/2017.
 */
public class HttpConnector {
    public static final String ADDRESS = "http://119.29.153.227:8080/";

    public static Object post(String action, Map<String, String> para2s){
        Log.v("Response Content", "++++++++++Start Post+++++++++");
        Map<String, String> paras;
        if(para2s != null){
            paras = para2s;
        }else{
            paras = new HashMap<String, String>();
            paras.put("tmp", "123456");
        }
        String data = getRequestData(paras, "UTF-8").toString();
        String line = null;
        try {
            URL targetUrl = new URL(ADDRESS + action);
            HttpURLConnection httpConnection = (HttpURLConnection) targetUrl.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setDoOutput(true);
            httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if(User.getSessionId() != null){
                Log.e("Session ID", User.getSessionId());
                httpConnection.setRequestProperty("cookie", User.getSessionId());
            }

            OutputStreamWriter osw = new OutputStreamWriter(httpConnection.getOutputStream());
            osw.write(data);
            osw.flush();

            if(User.getSessionId() == null){
                String cookieval = httpConnection.getHeaderField("set-cookie");
                String sessionid;
                if(cookieval != null) {
                    sessionid = cookieval.substring(0, cookieval.indexOf(";"));
                    User.setSessionId(sessionid);
                }
            }
            BufferedReader br;
            if (httpConnection.getResponseCode() != 200) {
                Log.e("Response Result", "HTTP error code : " + httpConnection.getResponseCode());
                br = new BufferedReader(new InputStreamReader(httpConnection.getErrorStream()));
            }else{
                br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            }
            line = br.readLine();
            Log.e("Return Data", line);

            httpConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("Response Content", "++++++++++Finish Post+++++++++");
        if(line == null){
            return null;
        }else if(line.startsWith("{")){
            try {
                return new JSONObject(line);
            } catch (JSONException e) {
            }
        }else if(line.startsWith("[")){
            try {
                return new JSONArray(line);
            } catch (JSONException e) {
            }
        }
        Log.e("Return Data~~~~~~~", line);
        return null;
    }

    public static Object get(String action, Map<String, String> paras){
        Log.v("Response Content", "++++++++++Start GET+++++++++");
        String data = null;
        if(paras != null){
            data = getRequestData(paras, "UTF-8").toString();
        }else{
            data = "";
        }
        String line = null;
        try {
            URL targetUrl = new URL(ADDRESS + action);
            HttpURLConnection httpConnection = (HttpURLConnection) targetUrl.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);
            httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpConnection.connect();

            BufferedReader br;
            if (httpConnection.getResponseCode() != 200) {
                Log.e("Response Result", "HTTP error code : " + httpConnection.getResponseCode());
                br = new BufferedReader(new InputStreamReader(httpConnection.getErrorStream()));
            }else{
                br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            }
            line = br.readLine();
            Log.e("Return Data", line);

            httpConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("Response Content", "++++++++++Finish GET+++++++++");
        if(line == null){
            return null;
        }else if(line.startsWith("{")){
            try {
                return new JSONObject(line);
            } catch (JSONException e) {
            }
        }else if(line.startsWith("[")){
            try {
                return new JSONArray(line);
            } catch (JSONException e) {
            }
        }
        Log.e("Return Data~~~~~~~", line);
        return null;
    }

    private static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(URLEncoder.encode(entry.getKey(), encode)).append("=").append(URLEncoder.encode(entry.getValue(), encode)).append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }
}

