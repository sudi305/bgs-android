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
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bgs.common.Dialogs;
import com.bgs.common.Utility;
import com.bgs.dheket.DetailLocationActivity;
import com.bgs.dheket.R;
import com.bgs.dheket.SingleMapLocationActivity;
import com.bgs.imageOrView.CustomAdapter;
import com.bgs.model.ItemObjectCustomList;
import com.bgs.networkAndSensor.HttpGetOrPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by SND on 01/02/2016.
 */
public class TabFragDetLoc  extends Fragment implements LocationListener {
    private JSONObject jObject;
    private String jsonResult ="";
    String temp_loc_name, temp_loc_address,temp_loc_pic;
    int temp_loc_promo,temp_id_loc,temp_loc_cat_id;
    double temp_loc_distance, temp_loc_lat, temp_loc_lng;

    View rootView;
    TextView textView_detLoc,textView_descLoc;
    ImageButton imageButton_share, imageButton_map;

    int cat_id,loc_id;
    double radius, latitude, longitude;
    String urls = "http://dheket.esy.es/getSingleLocationById.php";
    String parameters, kategori;

    LocationManager myLocationManager;
    Criteria criteria;
    String provider;
    Location location;

    CallWebPageTask task;
    Utility formatNumber = new Utility();
    boolean isFirst=true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab_frag_detail_loc, container, false);

        final Animation animButtonPress = AnimationUtils.loadAnimation(rootView.getContext(), R.anim.anim_scale_button_press);

        loc_id = getArguments().getInt("loc_id");
        cat_id = getArguments().getInt("cat_id");
        radius = getArguments().getDouble("radius");
        latitude = getArguments().getDouble("latitude");
        longitude = getArguments().getDouble("longitude");
        kategori = getArguments().getString("kategori");

//        Log.e("data yang dikirim","loc_id "+loc_id+" | cat_id "+cat_id+" | radius "+radius+" | lat "+latitude+" | lng "+longitude+" | kat "+kategori);

        imageButton_share = (ImageButton)rootView.findViewById(R.id.imageButton_share);
        imageButton_map = (ImageButton)rootView.findViewById(R.id.imageButton_maps);
        getServiceFromGPS();

        imageButton_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButton_share.setAnimation(animButtonPress);
                shareIt();
            }
        });

        imageButton_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButton_map.setAnimation(animButtonPress);
                gotoMap();
            }
        });

        setHasOptionsMenu(true);
        return rootView;
    }

    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater ) {
        inflater.inflate(R.menu.menu_detail_loc, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.goto_map:
                gotoMap();
                return true;
            case R.id.goto_share:
                shareIt();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void getDataFromServer() {
        Log.e("Sukses bro", "" + parameters);
        task = new CallWebPageTask();
        task.applicationContext = rootView.getContext();
        parameters = urls + "?lat=" + latitude + "&lng=" + longitude + "&loc_id=" + loc_id;
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
                menuItemArray = jObject.getJSONArray("dheket_singleLoc");
                Log.e("cek 1", "" + menuItemArray);
                for (int i = 0; i < menuItemArray.length(); i++) {
                    //String id_loc, String loc_name, String loc_address, int loc_promo, double loc_distance, int loc_pic
                    //listViewItems.add(new ItemObjectCustomList(menuItemArray.getJSONObject(i).getInt("id_category")),menuItemArray.getJSONObject(i).getString("location_name").toString(),menuItemArray.getJSONObject(i).getString("location_address").toString(),menuItemArray.getJSONObject(i).getInt("isPromo"),menuItemArray.getJSONObject(i).getDouble("distance"),menuItemArray.getJSONObject(i).getString("photo").toString()));
                    temp_id_loc=menuItemArray.getJSONObject(i).getInt("id_location");
                    temp_loc_name=menuItemArray.getJSONObject(i).getString("location_name").toString();
                    temp_loc_address=menuItemArray.getJSONObject(i).getString("location_address").toString();
                    temp_loc_promo=menuItemArray.getJSONObject(i).getInt("isPromo");
                    temp_loc_distance= Double.parseDouble(formatNumber.changeFormatNumber(menuItemArray.getJSONObject(i).getDouble("distance")));
                    temp_loc_pic=menuItemArray.getJSONObject(i).getString("photo").toString();
                    temp_loc_lat=menuItemArray.getJSONObject(i).getDouble("latitude");
                    temp_loc_lng=menuItemArray.getJSONObject(i).getDouble("longitude");
                    temp_loc_cat_id=menuItemArray.getJSONObject(i).getInt("category_id");
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

    public void shareIt() {
        //sharing implementation here
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Dheket");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Nearby location https://dheket.esy.es/ ");
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void gotoMap() {
        Intent map = new Intent(rootView.getContext().getApplicationContext(), SingleMapLocationActivity.class);
        Bundle paket = new Bundle();
        paket.putInt("loc_id",loc_id);
        paket.putInt("cat_id",cat_id);
        paket.putDouble("radius",radius);
        paket.putDouble("latitude",latitude);
        paket.putDouble("longitude",longitude);
        paket.putString("kategori",kategori);
        map.putExtras(paket);
        startActivity(map);
        getActivity().finish();
    }
}