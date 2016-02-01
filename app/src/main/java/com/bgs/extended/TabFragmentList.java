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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bgs.dheket.DetailLocationActivity;
import com.bgs.dheket.ListAndMapAllLocActivity;
import com.bgs.dheket.R;
import com.bgs.imageOrView.CustomAdapter;
import com.bgs.model.ItemObjectCustomList;
import com.bgs.networkAndSensor.HttpGetOrPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SND on 27/01/2016.
 */
public class TabFragmentList extends Fragment implements LocationListener {
    private ListView customListView;
    List<ItemObjectCustomList> listViewItems;
    private JSONObject jObject;
    private String jsonResult ="";
    View rootView;
    Button btn_update;

    boolean isFirst = true;
    boolean setUpdate = false;

    String[] loc_name, loc_address,loc_pic;
    int[] loc_promo,id_loc,temp_loc_promo,temp_id_loc;
    double[] loc_distance, loc_lat, loc_lng, temp_loc_distance;
    CallWebPageTask task;

    int cat_id;
    double radius,latitude,longitude;
    String urls ="http://dheket.esy.es/getLocationByCategory.php";
    String parameters;
    NumberFormat formatter = new DecimalFormat("#0.000");

    LocationManager myLocationManager;
    Criteria criteria;
    String provider;
    Location location;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab_fragment_list, container, false);

        cat_id = getArguments().getInt("cat_id");
        radius = getArguments().getDouble("radius");
        latitude = getArguments().getDouble("latitude");
        longitude = getArguments().getDouble("longitude");

        btn_update = (Button)rootView.findViewById(R.id.btn_update_list);
        customListView = (ListView)rootView.findViewById(R.id.listView);
        listViewItems = new ArrayList<ItemObjectCustomList>();

        customListView.setAdapter(new CustomAdapter(rootView.getContext(), listViewItems));
        customListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // make Toast when click
                int selectId = id_loc[position];
                int selectPromo = loc_promo[position];
                String selectNameLoc = loc_name[position];
                String selectAddress = loc_address[position];

                Intent i = new Intent(rootView.getContext(), DetailLocationActivity.class);
                Bundle paket = new Bundle();

                paket.putInt("id_loc", selectId);
                paket.putString("loc_name", selectNameLoc);
                paket.putInt("loc_promo", selectPromo);
                paket.putString("loc_address", selectAddress);
                paket.putInt("cat_id", cat_id);
                paket.putDouble("latitude",latitude);
                paket.putDouble("longitude",longitude);
                paket.putDouble("radius", radius);

                Toast.makeText(rootView.getContext(), "Id Loc " + selectId, Toast.LENGTH_LONG).show();
                i.putExtras(paket);
                startActivity(i);
                getActivity().finish();
                //rootView.getContext().
            }
        });
        getServiceFromGPS();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listViewItems.clear();
                updateList();
                btn_update.setVisibility(View.GONE);
            }
        });

        return rootView;
    }

    public void getDataFromServer(){
        Log.e("Sukses bro", ""+parameters);
        task = new CallWebPageTask();
        task.applicationContext = rootView.getContext();
        parameters = urls+"?rad="+radius+"&lat="+latitude+"&lng="+longitude + "&cat=" + cat_id;
        //Log.e("Sukses", parameters);
        task.execute(new String[]{parameters});
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.e("ok 3","sip "+isFirst);
        if (isFirst==false){
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
            if (isFirst==true){
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
                id_loc = new int[menuItemArray.length()];
                loc_name = new String[menuItemArray.length()];
                loc_address = new String[menuItemArray.length()];
                loc_promo = new int[menuItemArray.length()];
                loc_distance = new double[menuItemArray.length()];
                loc_pic = new String[menuItemArray.length()];
                loc_lat = new double[menuItemArray.length()];
                loc_lng = new double[menuItemArray.length()];
                Log.e("cek 1",""+menuItemArray);
                for (int i = 0; i < menuItemArray.length(); i++) {
                    //String id_loc, String loc_name, String loc_address, int loc_promo, double loc_distance, int loc_pic
                    //listViewItems.add(new ItemObjectCustomList(menuItemArray.getJSONObject(i).getInt("id_category")),menuItemArray.getJSONObject(i).getString("location_name").toString(),menuItemArray.getJSONObject(i).getString("location_address").toString(),menuItemArray.getJSONObject(i).getInt("isPromo"),menuItemArray.getJSONObject(i).getDouble("distance"),menuItemArray.getJSONObject(i).getString("photo").toString()));
                    id_loc[i]=menuItemArray.getJSONObject(i).getInt("id_location");
                    loc_name[i]=menuItemArray.getJSONObject(i).getString("location_name").toString();
                    loc_address[i]=menuItemArray.getJSONObject(i).getString("location_address").toString();
                    loc_promo[i]=menuItemArray.getJSONObject(i).getInt("isPromo");
                    loc_distance[i]= Double.parseDouble(formatter.format(menuItemArray.getJSONObject(i).getDouble("distance")));
                    loc_pic[i]=menuItemArray.getJSONObject(i).getString("photo").toString();
                    loc_lat[i]=menuItemArray.getJSONObject(i).getDouble("latitude");
                    loc_lng[i]=menuItemArray.getJSONObject(i).getDouble("longitude");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("cek 2", "error" + e);
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (isFirst){
                this.dialog.cancel();
                isFirst=false;
                updateList();
            } else {
                if (isDataUpdate())btn_update.setVisibility(View.VISIBLE);
            }
        }
    }

    public void getServiceFromGPS(){
        myLocationManager = (LocationManager)rootView.getContext().getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        provider = myLocationManager.getBestProvider(criteria, true);
        location = myLocationManager.getLastKnownLocation(provider);
        if (isFirst){
            getDataFromServer();
            Log.e("ok 1","sip "+isFirst);
        } else {
            if (location != null) {
                onLocationChanged(location);
                Log.e("ok 2", "sip " + isFirst);
            }
        }
        myLocationManager.requestLocationUpdates(provider, 20000, 0, this);
    }

    public boolean isDataUpdate(){
        boolean isUpdate = false;
        if (temp_id_loc.length!=id_loc.length){
            isUpdate = true;
        } else {
            for (int i = 0; i < id_loc.length; i++) {
                if (temp_id_loc[i]!=id_loc[i] || temp_loc_distance[i]!=loc_distance[i] || temp_loc_promo[i]!=loc_promo[i]){
                    isUpdate = true;
                    i=id_loc.length;
                }
            }
        }
        return isUpdate;
    }

    public void updateList(){
        for (int i = 0; i <id_loc.length ; i++) {
            if (i==0){
                temp_id_loc = new int[id_loc.length];
                temp_loc_distance = new double[id_loc.length];
                temp_loc_promo = new int[id_loc.length];
            }
            temp_loc_promo[i] = loc_promo[i];
            temp_loc_distance[i] = loc_distance[i];
            temp_id_loc[i] = id_loc[i];
            listViewItems.add(new ItemObjectCustomList(id_loc[i],loc_name[i],loc_address[i],loc_promo[i],loc_distance[i],loc_pic[i]));
        }
        customListView.setAdapter(new CustomAdapter(rootView.getContext(), listViewItems));
    }
}
