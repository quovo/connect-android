package com.quovo.quovoconnect;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class UserTokenAsyncTask extends AsyncTask<String, String, String> {
    private final static String URL="https://api.quovo.com/v2/iframe_token";
    private static MainActivity.UserTokenListener lUserTokenListener;

    public UserTokenAsyncTask(MainActivity.UserTokenListener listener ){
        lUserTokenListener=listener;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        // Create the urlConnection
        URL url = null;
        try {
            url = new URL(URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer "+params[0]);
            urlConnection.setRequestMethod("POST");
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("user", params[1]);
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write(jsonParam.toString());
            writer.flush();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 201) {
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertInputStreamToString(inputStream);
                Log.d("Response","statusCode="+statusCode+"\n"+response);

                return response;
            }else{
                Log.e("Bad Request",""+statusCode);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line).append('\n');
        }
        return total.toString();
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if(response!=null&&!response.isEmpty()) {
            lUserTokenListener.onUserToken(getUserToken(response));
        }else{
            lUserTokenListener.onError();
        }

    }

    private String getUserToken(String response){
          String userToken=null;
        try {
            JSONObject jsonObject=new JSONObject(response);
            JSONObject iframe_token =jsonObject.getJSONObject("iframe_token");
            userToken=iframe_token.getString("token");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userToken;
    }


}
