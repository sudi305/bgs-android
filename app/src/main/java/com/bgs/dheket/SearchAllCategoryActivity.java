package com.bgs.dheket;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.bgs.imageOrView.CustomListView;
import com.bgs.imageOrView.ListViewAdapter;
import com.bgs.networkAndSensor.HttpGetOrPost;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SND on 01/02/2016.
 */
public class SearchAllCategoryActivity extends AppCompatActivity {

    // Declare Variables
    JSONObject jsonobject;
    JSONArray jsonarray;
    private JSONObject jObject;
    ListView listview;
    ListViewAdapter adapter;
    ProgressDialog mProgressDialog;
    ArrayList<HashMap<String, String>> arraylist;
    public static String id_category = ""; //rank
    public static String category_name = "category_name"; //country
    public static String category_id = ""; //population

    boolean tambah = true;  // tombol back
    android.support.v7.app.ActionBar actionBar;  // tombol back
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from listview_main.xml
        setContentView(R.layout.activity_search_all_category); //listview_main

        actionBar = getSupportActionBar(); // tombol back

        actionBar.setDisplayShowHomeEnabled(true); // tombol back
        actionBar.setDisplayHomeAsUpEnabled(true); // tombol back
        actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.d_ic_back));
        //actionBar.setHomeAsUpIndicator(R.drawable.logo);
        actionBar.setHomeButtonEnabled(true); // tombol back
        actionBar.setTitle("Dheket"); // tombol back
//        actionBar.setSubtitle(Html.fromHtml("<font color='#FFBF00'>Location in Radius " + formatter.format(radius) + " Km</font>"));
        // Execute DownloadJSON AsyncTask
        getDataCategory();
    }

    public void getDataCategory() {
        CallWebPageTask task = new CallWebPageTask();
        task.applicationContext = getApplicationContext();
        String urls = String.format(getResources().getString(R.string.link_getAllCategory));
        Log.e("Sukses", urls);
        task.execute(new String[]{urls});
    }

    // DownloadJSON AsyncTask

    private class CallWebPageTask extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;
        protected Context applicationContext;

        @Override
        protected void onPreExecute() {
            //this.dialog = ProgressDialog.show(applicationContext, "Login Process", "Please Wait...", true);
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpGetOrPost httpGetOrPost = new HttpGetOrPost();
            response = httpGetOrPost.getRequest(urls[0]);
            arraylist = new ArrayList<HashMap<String, String>>();
            try {
                JSONArray menuItemArray = null;
                jObject = new JSONObject(response);
                menuItemArray = jObject.getJSONArray("dheket_allCat");
                for (int i = 0; i < menuItemArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    jsonobject = menuItemArray.getJSONObject(i);
                    // Retrive JSON Objects
                    map.put("id_category", jsonobject.getString("id_category")); // rank
                    map.put("category_name", jsonobject.getString("category_name")); //country
                    // Set the JSON Objects into the array
                    arraylist.add(map);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            //this.dialog.cancel();
            listview = (CustomListView) findViewById(R.id.listview);

            // Pass the results into ListViewAdapter.java
            adapter = new ListViewAdapter(SearchAllCategoryActivity.this, arraylist);
            // Set the adapter ListView
            listview.setAdapter(adapter);
            //listview.setScrollContainer(false);
        }
    }

    ///
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            back_to_previous_screen();
            return super.onOptionsItemSelected(item);
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logout_user();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logout_user(){
        String logout = getResources().getString(com.facebook.R.string.com_facebook_loginview_log_out_action);
        String cancel = getResources().getString(com.facebook.R.string.com_facebook_loginview_cancel_action);
        String message= "Are you sure?";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton(logout, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        LoginManager.getInstance().logOut();
                        Intent logout_user_fb = new Intent(SearchAllCategoryActivity.this, FormLoginActivity.class);
                        startActivity(logout_user_fb);
                        finish();
                    }
                })
                .setNegativeButton(cancel, null);
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        back_to_previous_screen();
    }

    public void back_to_previous_screen(){
        Intent intent = new Intent(SearchAllCategoryActivity.this,MainMenuActivity.class);
        startActivity(intent);
        finish();
    }
}
