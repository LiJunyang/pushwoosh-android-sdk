package com.example.emarsysmobile;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.pm.ApplicationInfo;
import android.provider.Settings.Secure;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;

import java.util.TimeZone;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Emarsys Mobile Engage SDK for Android
 *
 * Public methods
 *
 * app_launch       --
 * event            --
 * message_open     --
 * acceptPush       --
 * contact_update   --
 * logout           --
 * getDeviceName    --
 *
 * Created by dgalante on 05/24/16.
 */
public class EmarsysMobile extends AsyncTask<String, Void, String> {

private URL endpoint;
private JSONObject postdata;
public String AppID;
public String android_id;
public String App_token;

Context EmarsysContext;


    public EmarsysMobile(Context context) {

        EmarsysContext = context;

        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            App_token = bundle.getString("auth_token");
        } catch (NullPointerException e) {
            Log.d("Emarsys", "Failed to load App_token from AndroidManifest, NullPointer: " + e.getMessage());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            AppID = bundle.getString("app_id");
        } catch (NullPointerException e) {
            Log.d("Emarsys", "Failed to load AppID from AndroidManifest, NullPointer: " + e.getMessage());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

        try {
            endpoint = new URL("https://push.eservice.emarsys.net/api/sdk/events/app_launch");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.d("Emarsys", "in constructor() [AppID: " + AppID + "AppToken: "+ App_token +"]");

    }


    public EmarsysMobile app_launch(Context context){

        try {
            endpoint = new URL("https://push.eservice.emarsys.net/api/sdk/events/app_launch");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        postdata = new JSONObject();

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        DateFormat date = new SimpleDateFormat("Z");
        String localTime = date.format(currentLocalTime);


        PackageInfo pInfo = null;

        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String version = pInfo.versionName;

        postdata = new JSONObject();

        List<Pair<String, String>> params = new ArrayList<>();
        try {
            postdata.put("application_id", AppID);
            postdata.put("hardware_id", android_id);
            postdata.put("platform", "android");
            postdata.put("language", Locale.getDefault().getLanguage());
            postdata.put("timezone", localTime);
            postdata.put("device_model", this.getDeviceName().toString());
            postdata.put("application_version", version);
            postdata.put("os_version", Build.VERSION.RELEASE.toString());

            // postdata.put("ems_sdk", "0.0.1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Emarsys", "in app_launch()");
        this.execute();

        return null;
    }

    public EmarsysMobile event(String event, JSONObject data){

        try {
            endpoint = new URL("https://push.eservice.emarsys.net/api/sdk/events/custom/"+ event);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        postdata = new JSONObject();

        List<Pair<String, String>> params = new ArrayList<>();
        try {
            postdata.put("application_id",AppID);
            postdata.put("hardware_id", android_id);

            if (data != null)
            {
                postdata.put("attributes", data);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Emarsys", "in checkpoint()");
        this.execute();

        return null;
    }

    public EmarsysMobile message_open(String msg_id){

        try {
            endpoint = new URL("https://push.eservice.emarsys.net/api/sdk/events/message_open");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        postdata = new JSONObject();

        List<Pair<String, String>> params = new ArrayList<>();
        try {
            postdata.put("application_id",AppID);
            postdata.put("hardware_id", android_id);

            if (msg_id != "null") {
                postdata.put("sid", msg_id);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Emarsys", "in open()");
        this.execute();

        return null;
    }

    public EmarsysMobile acceptPush(Context context, String pushToken){


        try {
            endpoint = new URL("https://push.eservice.emarsys.net/api/sdk/events/push_accepted");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        postdata = new JSONObject();

        List<Pair<String, String>> params = new ArrayList<>();
        try {
            postdata.put("application_id",AppID);
            postdata.put("hardware_id", android_id);
            postdata.put("push_token", pushToken);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Emarsys", "acceptPush()");
        this.execute();

        return null;
    }

    public EmarsysMobile contact_update(int mergefield, JSONObject contactdata ){
        try {
            endpoint = new URL("https://push.eservice.emarsys.net/api/sdk/events/contact_update");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        postdata = new JSONObject();
        List<Pair<String, String>> params = new ArrayList<>();
        try {
            postdata.put("application_id",AppID);
            postdata.put("hardware_id", android_id);
            postdata.put("contact_fields", contactdata);
            postdata.put("merge_by_field", mergefield);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Emarsys", "in contact_update()");
        this.execute();

        return null;
    }

    public EmarsysMobile logout(){
        try {
            endpoint = new URL("https://push.eservice.emarsys.net/api/sdk/events/logout");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d("Emarsys", "in logout()");
        this.execute();
        return null;
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    protected String doInBackground(String... urls) {
        try {
            postData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void postData() throws IOException {

        String hash = AppID + ":" + App_token;

        String basicAuth = "Basic " + Base64.encodeToString(hash.getBytes(), Base64.NO_WRAP);

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) endpoint.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        conn.setRequestProperty("Authorization", basicAuth );
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setDoOutput(true);

        OutputStream os = null;
        try {
            os = conn.getOutputStream();
            Log.d("Emarsys", "getOutputStream");
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter( new OutputStreamWriter(os, "UTF-8"));
            writer.write(postdata.toString());

            writer.flush();
            writer.close();
            int responseCode = conn.getResponseCode();
            Log.d("Emarsys", "Endpoint [" + endpoint + "]" );
            Log.d("Emarsys", "Response [" + responseCode + "] [" + conn.getResponseMessage() + "]" + ":" + postdata.toString()  );
           // Toast.makeText(this.EmarsysContext ,  postdata.toString(), Toast.LENGTH_SHORT).show();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {

            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            Log.d("Emarsys", "Sent!");
            Log.d("Emarsys", "Result: " + in.toString());

            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return;

    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

}
