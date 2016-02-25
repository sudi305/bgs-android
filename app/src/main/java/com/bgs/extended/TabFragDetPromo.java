package com.bgs.extended;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bgs.dheket.R;
import com.bgs.networkAndSensor.HttpGetOrPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by SND on 01/02/2016.
 */
public class TabFragDetPromo extends Fragment implements LocationListener {
    private JSONObject jObject;
    private String jsonResult ="";
    View rootView;

    int cat_id,loc_id;
    double radius, latitude, longitude;
    String urls = "http://dheket.esy.es/getLocationByCategory.php";
    String parameters;
    NumberFormat formatter = new DecimalFormat("#0.000");

    LocationManager myLocationManager;
    Criteria criteria;
    String provider;
    Location location;

    CallWebPageTask task;
    boolean isFirst=true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab_frag_detail_promo, container, false);

/*        cat_id = getArguments().getInt("loc_id");
        radius = getArguments().getDouble("radius");
        latitude = getArguments().getDouble("latitude");
        longitude = getArguments().getDouble("longitude");*/
        loc_id = getArguments().getInt("loc_id");

        getServiceFromGPS();

        setHasOptionsMenu(true);
        return rootView;
    }

    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater ) {
        inflater.inflate(R.menu.menu_detail_promo, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.goto_share:
                shareIt();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void shareIt() {
        //sharing implementation here
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Dheket");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Nearby location https://dheket.esy.es/ ");
        startActivity(Intent.createChooser(sharingIntent, "Share Promo on This Location Via"));
    }

    public void getDataFromServer() {
        Log.e("Sukses bro", "" + parameters);
        task = new CallWebPageTask();
        task.applicationContext = rootView.getContext();
        parameters = urls + "?rad=" + radius + "&lat=" + latitude + "&lng=" + longitude + "&cat=" + cat_id;
        //Log.e("Sukses", parameters);
        task.execute(new String[]{parameters});
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.e("ok 3", "sip " + isFirst);
        if (isFirst == false) {
            getDataFromServer();
        }
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

    private class CallWebPageTask extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;
        protected Context applicationContext;

        @Override
        protected void onPreExecute() {
            if (isFirst == true) {
                this.dialog = ProgressDialog.show(applicationContext, "Retrieving Data", "Please Wait...", true);
            }
        }

        @Override
        protected String doInBackground(String... url) {
            String response = "";
            HttpGetOrPost httpGetOrPost = new HttpGetOrPost();
            response = httpGetOrPost.getRequest(url[0]);
            try {
                //simpan data dari web ke dalam array
                JSONArray menuItemArray = null;
                jObject = new JSONObject(response);
                menuItemArray = jObject.getJSONArray("dheket_locByCat");
                Log.e("cek 1", "" + menuItemArray);
                for (int i = 0; i < menuItemArray.length(); i++) {
                    //String id_loc, String loc_name, String loc_address, int loc_promo, double loc_distance, int loc_pic
                    //listViewItems.add(new ItemObjectCustomList(menuItemArray.getJSONObject(i).getInt("id_category")),menuItemArray.getJSONObject(i).getString("location_name").toString(),menuItemArray.getJSONObject(i).getString("location_address").toString(),menuItemArray.getJSONObject(i).getInt("isPromo"),menuItemArray.getJSONObject(i).getDouble("distance"),menuItemArray.getJSONObject(i).getString("photo").toString()));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("cek 2", "error" + e);
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (isFirst) {
                this.dialog.cancel();
                isFirst = false;
                updateList();
            } else {

            }
        }
    }

    public void getServiceFromGPS() {
        myLocationManager = (LocationManager) rootView.getContext().getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        provider = myLocationManager.getBestProvider(criteria, true);
        location = myLocationManager.getLastKnownLocation(provider);
        if (isFirst) {
            //getDataFromServer();
            Log.e("ok 1", "sip " + isFirst);
        } else {
            if (location != null) {
                //onLocationChanged(location);
                Log.e("ok 2", "sip " + isFirst);
            }
        }
        myLocationManager.requestLocationUpdates(provider, 20000, 0, this);
    }

    public boolean isDataUpdate() {
        boolean isUpdate = false;
        return isUpdate;
    }

    public void updateList() {

    }
}
