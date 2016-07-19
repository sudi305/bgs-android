package com.bgs.dheket;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bgs.common.Constants;
import com.bgs.common.DialogUtils;
import com.bgs.domain.chat.model.UserType;
import com.bgs.model.UserApp;
import com.bgs.networkAndSensor.HttpGetOrPost;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by SND on 18/01/2016.
 */

public class FormLoginActivity extends AppCompatActivity implements LocationListener {
    CallbackManager callbackManager;
    LoginButton loginBtn;
    Button signup;
    TextView loading, login_fb, check_email;
    //android.support.v7.app.ActionBar actionBar;

    String url = "";
    String urlCreateAccount = "";
    String temp_email = "",email = "",username="",password="",facebook_id="",responseServer="";
    double latitude=0, longitude=0;

    LocationManager myLocationManager;
    Criteria criteria;
    String provider;
    Location location;

    private JSONObject jObject;
    JSONArray menuItemArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.bgs.dheket", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d(Constants.TAG, "KeyHash:" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_form_login);

        /*actionBar = getSupportActionBar();

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeAsUpIndicator(R.drawable.logo);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Login to Dheket");*/

        getServiceFromGPS();

        callbackManager = CallbackManager.Factory.create();
        loginBtn = (LoginButton)findViewById(R.id.login_button);
        loginBtn.setReadPermissions("public_profile email");
        signup = (Button)findViewById(R.id.signup_button);
        loading = (TextView)findViewById(R.id.textView_formLogin_loading);
        login_fb = (TextView)findViewById(R.id.textView_login_fb);
        check_email = (TextView)findViewById(R.id.textView_checkemail);
        url = String.format(getResources().getString(R.string.link_cekExistingUser));
        urlCreateAccount = String.format(getResources().getString(R.string.link_addUserCustomerByEmail));

//        if(AccessToken.getCurrentAccessToken() != null){
//            RequestData();
//            login.setVisibility(View.INVISIBLE);
//        } else {
//            login.setVisibility(View.VISIBLE);
//        }
        login_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginBtn.performClick();
            }
        });
        loginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                loading.setVisibility(View.VISIBLE);
                loginBtn.setVisibility(View.GONE);
                signup.setVisibility(View.GONE);
                Log.d(Constants.TAG, "Success => 1");
                RequestDataFromFB();
                Log.d(Constants.TAG, "Success => 1a");
                login_fb.setVisibility(View.GONE);
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Login Canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e(Constants.TAG, exception.getMessage(), exception);
                Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Sign Up With Email", Toast.LENGTH_SHORT).show();
            }
        });

        check_email.addTextChangedListener(textWatcher);
    }

    final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Log.e("belum","belum");
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.e("belum","saat");
            /*if (check_email.getText().equals(temp_email) && !temp_email.isEmpty()){
                //checkExistingUser(check_email.getText().toString(),latitude,longitude);
                Log.e("belum","ok");
                Toast.makeText(getApplicationContext(),check_email.getText().toString(),Toast.LENGTH_SHORT).show();
            }*/
        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.e("belum","sudah");
            checkExistingUser(check_email.getText().toString(), latitude, longitude);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            finish();
            return super.onOptionsItemSelected(item);
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            //logout_user();
            Toast.makeText(getApplicationContext(),"About this app",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void RequestDataFromFB() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                JSONObject json = response.getJSONObject();
                try {
                    if (json != null) {
                        Log.d(Constants.TAG, "json => "+json.toString());

                        String id = json.getString("id");
                        String name = json.getString("name");
                        //String gender = json.getString("gender");
                        String email = json.getString("email");
                        String profilePicUrl = "";


                        String link = json.getString("link");

                        String text = String.format("<b>Name :</b> %s<br><br><b>Email :</b>%s<br><br><b>Profile link :</b>%s ",name,email,link);

                        temp_email = email;
                        username = ""+name.replace(" ","");
                        username = ""+username.replace("'","");
                        password = "123456";
                        facebook_id = id;
                        Log.d(Constants.TAG, "Success => 2");
                        //checkExistingUser(email, latitude, longitude);
                        Log.d(Constants.TAG, "Success => 2a");

                        if (json.has("picture")) {
                            profilePicUrl = json.getJSONObject("picture").getJSONObject("data").getString("url");
                        }

                        if (email.equalsIgnoreCase("")||email.isEmpty()){
                            temp_email = "user"+facebook_id+"@dheket.co.id";
                        }
                        check_email.setText(temp_email);

                        //add by supri 2016/6/16
                        UserApp userApp = App.getInstance().getUserApp();
                        if ( userApp == null) userApp = new UserApp();
                        userApp.setName(name);
                        userApp.setEmail(email);
                        userApp.setId(id);
                        userApp.setPicture(profilePicUrl);
                        userApp.setType(UserType.USER);
                        App.getInstance().updateUserApp(userApp);


                    }

                } catch (JSONException e) {
                    Log.e(Constants.TAG, e.getMessage(), e);
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture");
        request.setParameters(parameters);
        Log.d(Constants.TAG, "Success => 3");
        request.executeAsync();
        /*if (email.equalsIgnoreCase("")||email.isEmpty()){
            temp_email = "user"+facebook_id+"@dheket.co.id";
            createUserAccountCustomer();
        } else {
            checkExistingUser(email, latitude, longitude);
        }
        Log.d(Constants.TAG, "Success => 3a");*/
    }

    public void checkExistingUser(String email, double latitude, double longitude) {
        CallWebPageTaskCheckEmail task = new CallWebPageTaskCheckEmail(this);
        task.applicationContext = getApplicationContext();
        //String urls = url + "/" + email + "/" + latitude + "/" + longitude;
        String urls = url + "/" + email;
        Log.d(Constants.TAG, "Sukses =>" + urls);
        task.execute(new String[]{urls});
    }

    public void getServiceFromGPS() {
        myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        provider = myLocationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = myLocationManager.getLastKnownLocation(provider);
        if (location != null) {
            onLocationChanged(location);
        }
        myLocationManager.requestLocationUpdates(provider, 20000, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class CallWebPageTaskCheckEmail extends AsyncTask<String, Void, String> {
        private Context context;
        private Dialog dialog;
        protected Context applicationContext;

        public CallWebPageTaskCheckEmail(Context context) {
            this.context = context;
            dialog = DialogUtils.LoadingSpinner(context);
        }

        @Override
        protected void onPreExecute() {
            //dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpGetOrPost httpGetOrPost = new HttpGetOrPost();
            response = httpGetOrPost.getRequest(urls[0]);
            try {
                //simpan data dari web ke dalam array
                jObject = new JSONObject(response);
                //menuItemArray = jObject.getJSONArray("tag_cat");
                //email = jObject.getString("email");
                menuItemArray = jObject.getJSONArray("result_user");
                if (menuItemArray.length()!=0) {
                    for (int i = 0; i < menuItemArray.length(); i++) {
                        email = menuItemArray.getJSONObject(i).getString("email").toString();
                    }
                }else{
                    email = "kosong";
                }
            } catch (JSONException e) {
                Log.e(Constants.TAG, e.getMessage(), e);
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            //this.dialog.cancel();
            Log.d(Constants.TAG, "Success => 4");
            //if (email.equalsIgnoreCase("guest@dheket.co.id") || email.equalsIgnoreCase("") || email.equalsIgnoreCase(null)) {
            if (email.equalsIgnoreCase("kosong")) {
                Log.d(Constants.TAG, "Success => 5");
                createUserAccountCustomer();
                Log.d(Constants.TAG, "Success => 5a");
            } else {
                if (AccessToken.getCurrentAccessToken() != null) {
                    Intent loginWithFb = new Intent(FormLoginActivity.this, MainMenuActivity.class);
                    Log.d(Constants.TAG, "Success => 6");
                    startActivity(loginWithFb);
                    finish();
                }
            }
            if ( dialog.isShowing()) dialog.dismiss();
        }
    }

    public void createUserAccountCustomer() {
        AsyncTAddingDataToServer asyncT = new AsyncTAddingDataToServer(this);
        asyncT.execute();
    }

    public static class InputStreamToStringExample {

        public static void main(String[] args) throws IOException {

            // intilize an InputStream
            InputStream is = new ByteArrayInputStream("file content is process".getBytes());

            String result = getStringFromInputStream(is);

            System.out.println(result);
            System.out.println("Done");

        }

        // convert InputStream to String
        private static String getStringFromInputStream(InputStream is) {

            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {

                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return sb.toString();
        }
    }

    /* Inner class to get response */
    class AsyncTAddingDataToServer extends AsyncTask<Void, Void, Void> {
        private Context context;
        private Dialog dialog;
        public AsyncTAddingDataToServer(Context context) {
            this.context = context;
            dialog = DialogUtils.LoadingSpinner(context);
        }

        @Override
        protected void onPreExecute() {
            //dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String url = urlCreateAccount;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {
                JSONObject jsonobj = new JSONObject();
                jsonobj.put("email", temp_email);
                jsonobj.put("username", username);
                jsonobj.put("password", password);
                jsonobj.put("facebook_id", facebook_id);
                Log.d(Constants.TAG, "mainToPost => " + jsonobj.toString());
                httppost.setEntity(new StringEntity(jsonobj.toString())); //json without header {"a"="a","b"=1}
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                InputStream inputStream = response.getEntity().getContent();
                InputStreamToStringExample str = new InputStreamToStringExample();
                responseServer = str.getStringFromInputStream(inputStream);
                Log.d(Constants.TAG, "response ----- " + responseServer.toString() + "|");
                Log.d(Constants.TAG, "response ----- " + responseServer.toString().equalsIgnoreCase("{\"success\":1}") + "|");
                Log.d(Constants.TAG, "Success => 7");
            } catch (Exception e) {
                Log.e(Constants.TAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(Constants.TAG, "Success => 8");
            if (responseServer!=null && responseServer.equalsIgnoreCase("{\"success\":1}")) {
                Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_SHORT).show();
                responseServer="";
                Log.d(Constants.TAG, "Success => 8a");
                if (AccessToken.getCurrentAccessToken() != null) {
                    Intent loginWithFb = new Intent(FormLoginActivity.this, MainMenuActivity.class);
                    Log.d(Constants.TAG, "Success => 8b");
                    startActivity(loginWithFb);
                    finish();
                }
                Log.d(Constants.TAG, "Success => 9");
            } else {
                if (responseServer.equalsIgnoreCase("") || responseServer.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Ops, Error! Please Try Again!",Toast.LENGTH_SHORT).show();
                    Log.d(Constants.TAG, "Success => 10");
                }
            }
            if ( dialog.isShowing()) dialog.dismiss();
        }
    }
}
