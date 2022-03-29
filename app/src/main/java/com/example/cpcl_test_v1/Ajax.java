package com.example.cpcl_test_v1;


import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bluebook1 on 10/5/2017.
 */

public class Ajax extends AsyncTask<String, Void, String> {
    public interface MyCustomObjectListener {
        public void onsuccess(String data);
        public void onerror();
    }
    public MyCustomObjectListener listener;
    public void setCustomObjectListener(MyCustomObjectListener listener) {
        this.listener = listener;
    }
    HashMap<String,String> theval= new HashMap<String,String>();
    @Override
    protected String doInBackground(String... params) {
        try {
            return downloadContent(params[0]);
        }
        catch (IOException e) {
            return "nowifi";
        }
    }
    @Override
    protected void onPostExecute(String result) {
        if(result.equalsIgnoreCase("nowifi")){
            listener.onerror();
            this.cancel(true);
        }
        else{
            listener.onsuccess(result);
            this.cancel(true);
        }
    }
    public void adddata(String thekey, String thevalue){
        theval.put(thekey,thevalue);
    }

    private String downloadContent(String myurl) throws IOException{
        InputStream is = null;
        JSONObject jsobject = new JSONObject();
        try {
            jsobject.put("test","testing");
        } catch (JSONException e){
            e.printStackTrace();
        }
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(60000 /* milliseconds */);
            conn.setConnectTimeout(60000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.connect();

            if (theval != null){
                OutputStream os = conn.getOutputStream();
                OutputStreamWriter osWriter = new OutputStreamWriter(os, "UTF-8");
                BufferedWriter writer = new BufferedWriter(osWriter);
                writer.write(getPostData(theval));

                writer.flush();
                writer.close();
                os.close();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                return sb.toString();
            }
            return null;
        }
        finally{
            if (is != null){
                is.close();
            }
        }
    }

    public String getPostData(HashMap<String, String> values){
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : values.entrySet()){
            if (first)
                first = false;
            else
                builder.append("&");
            try {
                builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                builder.append("=");
                builder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            catch (UnsupportedEncodingException e){}
        }
        return builder.toString();
    }
//    public String convertInputStreamToString(InputStream stream, int length) throws IOException, UnsupportedEncodingException {
//        Reader reader = null;
//        reader = new InputStreamReader(stream, "UTF-8");
//        String buffer="";
//        //char[] buffer = new char[length];
//        reader.read(new char[]);
//        return new String(buffer);
//    }
}
