package com.bgs.dheket;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bgs.common.Constants;
import com.bgs.common.DialogUtils;
import com.bgs.common.ExtraParamConstants;
import com.bgs.common.GpsUtils;
import com.bgs.common.MapUtils;
import com.bgs.common.Utility;
import com.bgs.model.Category;
import com.bgs.model.UserApp;
import com.bgs.networkAndSensor.Compass;
import com.bgs.networkAndSensor.HttpGetOrPost;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.MultiPoint;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.Symbol;
import com.esri.core.tasks.geocode.Locator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SND on 01/05/2016.
 */
public class MapViewActivity extends AppCompatActivity {

    final static double ZOOM_BY = 15;

    MapView mMapView = null;
    SpatialReference mMapSr = null;
    GraphicsLayer mResultsLayer = null;
    ImageButton imageButton_gps_center;
    PictureMarkerSymbol mcat;
    PictureMarkerSymbol[] madd;
    //int[] id_cat;
    //String[] icon_cat;
    Menu menu;
    Button btn_toMainMenu;
    // Views to show selected search result information.
    TextView textView_id_loc, textView_loc_name, textView_loc_address, textView_loc_distance;

    android.support.v7.app.ActionBar actionBar;

    Locator mLocator;
    Location locationTouch;
    //Location location;
    ArrayList<String> mFindOutFields = new ArrayList<>();

    LocationDisplayManager mLDM;

    Compass mCompass;
    Utility formatNumber = new Utility();

    //private JSONObject jObject;
    //private String jsonResult = "";
    //double radius = 0.0;
    //double latitude, longitude;
    String urls = "";
    String parameters;//, email, icon;
    //int cat_id;
    int looping = 0;
    ViewGroup placeLayout;

    /*String[] loc_name, loc_address, loc_pic;
    int[] loc_promo, id_loc;
    double[] loc_distance, loc_lat, loc_lng;*/

    boolean isFirst = true, maxView = true, minView = true;
    CallWebPageTask task;
    //Bundle paket;

    //store location detail sent via intent
    private Category[] categories;
    private Location currentBestLocation;

    private static final String ACTION_CALL_FROM_MAINMENU = "com.bgs.dheket.map.action.CALL_FROM_MAINMENU";

    public static void startFromMainMenu(Context context, Category[] categories) {
        startMapActivity(context, ACTION_CALL_FROM_MAINMENU, categories);
    }

    private static void startMapActivity(Context context, String action, Category[] categories) {
        Intent intent = new Intent(context, MapViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(action);
        if ( categories != null )
            intent.putExtra(ExtraParamConstants.CATEGORIES, categories);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.d_ic_back));
        //actionBar.setHomeAsUpIndicator(R.drawable.logo);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Dheket");
//        actionBar.setSubtitle(Html.fromHtml("<font color='#FFBF00'>Location in Radius " + formatter.format(radius) + " Km</font>"));

        //Retrieve the map and initial extent from XML layout
        mMapView = (MapView) findViewById(R.id.map_view);
        mMapView.setOnStatusChangedListener(statusChangedListener);
        mMapView.setOnSingleTapListener(mapTapCallback);
        //mMapView.setOnLongPressListener(mapLongPress);

        Parcelable[] parcelables = getIntent().getParcelableArrayExtra(ExtraParamConstants.CATEGORIES);
        categories = new Category[parcelables.length];
        System.arraycopy(parcelables, 0, categories, 0, parcelables.length);

        madd = new PictureMarkerSymbol[categories.length];
        PictureMarkerSymbol a = null;
        for (int i = 0; i < categories.length; i++) {
            Log.d(Constants.TAG, categories[i].getName());
            a = new PictureMarkerSymbol();
            a.setUrl(categories[i].getIcon());
            madd[i]=a;
        }

        double radius = categories[0].getRadius();
        actionBar.setSubtitle(Html.fromHtml("<font color='#FFBF00'>Location in Radius " + formatNumber.changeFormatNumber(radius) + " Km</font>"));

        urls = String.format(getResources().getString(R.string.link_getLocUserConfig));//"http://dheket.esy.es/getLocationByCategory.php"

        textView_id_loc = (TextView) findViewById(R.id.textView_map_id);
        textView_loc_name = (TextView) findViewById(R.id.textView_map_nama_lokasi);
        textView_loc_address = (TextView) findViewById(R.id.textView_map_lokasinya);
        textView_loc_distance = (TextView) findViewById(R.id.textView_map_jarak);
        btn_toMainMenu = (Button)findViewById(R.id.button_map_tomainmenu);
        imageButton_gps_center = (ImageButton)findViewById(R.id.imageButton_map_center);
        placeLayout = (ViewGroup) findViewById(R.id.placeLayout_single);
        placeLayout.setVisibility(View.GONE);

        placeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToScreen = new Intent(getApplicationContext(), DetailLocationWithNoMerchantActivity.class);
                startActivity(goToScreen);
                finish();
            }
        });

        btn_toMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMainMenu();
            }
        });

        imageButton_gps_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMapView.isLoaded()) {
                    // If LocationDisplayManager has a fix, pan to that location. If no
                    // fix yet, this will happen when the first fix arrives, due to
                    // callback set up previously.
                    if ((mLDM != null) && (mLDM.getLocation() != null)) {
                        // Keep current scale and go to current location, if there is one.
                        mLDM.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);
                    }
                }
            }
        });
        mResultsLayer = new GraphicsLayer();
        mResultsLayer.setSelectionColorWidth(6);
        mMapView.addLayer(mResultsLayer);
        mMapView.setAllowRotationByPinch(true);

        // Enabled wrap around map.
        mMapView.enableWrapAround(true);

        // Create the Compass custom view, and add it onto the MapView.
        mCompass = new Compass(getApplicationContext(), null, mMapView);
        mMapView.addView(mCompass);

        mcat = new PictureMarkerSymbol(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_orange));
        //mAdd = new PictureMarkerSymbol(rootView.getContext().getApplicationContext(), ContextCompat.getDrawable(rootView.getContext().getApplicationContext(), R.drawable.pin_add));

        //setup dan ini data dipindah ke resume
    }

    public void getDataFromServer(boolean showProgress) {
        task = new CallWebPageTask(this, showProgress);
        UserApp user = ((App)getApplication()).getUserApp();
        double latitude = 0,longitude = 0;
        if ( currentBestLocation != null ) {
            latitude = currentBestLocation.getLatitude();
            longitude = currentBestLocation.getLongitude();
        }
        parameters = urls + (user.getEmail()) + "/" + latitude + "/" + longitude;
        Log.e(Constants.TAG, "OK Connecting Sukses -> " + parameters);
        //Log.e("Sukses", parameters);
        task.execute(new String[]{parameters});
    }

    /**
     * When the map is tapped, select the graphic at that location.
     */
    final OnSingleTapListener mapTapCallback = new OnSingleTapListener() {
        @Override
        public void onSingleTap(float x, float y) {
            // Find out if we tapped on a Graphic

            //Toast.makeText(rootView.getContext().getApplicationContext(),"this location at x= "+x+" and y= "+y+" | point on SingleTaps"+onSingleTaps(x,y)+"",Toast.LENGTH_SHORT).show();
            int[] graphicIDs = mResultsLayer.getGraphicIDs(x, y, 25);
            if (graphicIDs != null && graphicIDs.length > 0) {
                placeLayout.setVisibility(View.VISIBLE);
                // If there is more than one graphic, only select the first found.
                if (graphicIDs.length > 1) {
                    int id = graphicIDs[0];
                    graphicIDs = new int[]{id};
                }

                // Only deselect the last graphic if user has tapped a new one. App
                // remains showing the last selected nearby service information,
                // as that is the main focus of the app.
                mResultsLayer.clearSelection();

                // Select the graphic
                mResultsLayer.setSelectedGraphics(graphicIDs, true);

                // Use the graphic attributes to update the information views.
                Graphic gr = mResultsLayer.getGraphic(graphicIDs[0]);
                Log.e(Constants.TAG, "atrribut -> " + gr.getAttributes());

                if (gr.getAttributes().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "No Attribute for this location!", Toast.LENGTH_SHORT).show();
                } else {
                    updateContent(gr.getAttributes());
                }
            } else {
                placeLayout.setVisibility(View.GONE);
                mMapView.setRotationAngle(0);
                // Also reset the compass angle.
                mCompass.setRotationAngle(0);
            }
        }
    };

    /**
     * When map is ready, set up the LocationDisplayManager.
     */
    final OnStatusChangedListener statusChangedListener = new OnStatusChangedListener() {

        private static final long serialVersionUID = 1L;

        @Override
        public void onStatusChanged(Object source, STATUS status) {
            if (source == mMapView && status == STATUS.INITIALIZED) {
                mMapSr = mMapView.getSpatialReference();
                if (mLDM == null) {
                    setupLocationListener();
                }
            }
        }
    };

    private void setupLocator() {
        // Parameterless constructor - uses the Esri world geocoding service.
        mLocator = Locator.createOnlineLocator();

        // Set up the outFields parameter for the search.
        mFindOutFields.add("id_loc");
        mFindOutFields.add("loc_name");
        mFindOutFields.add("loc_address");
        mFindOutFields.add("loc_distance");
    }

    private void setupLocationListener() {

        if ((mMapView != null) && (mMapView.isLoaded())) {
            mLDM = mMapView.getLocationDisplayManager();
            mLDM.setLocationListener(new LocationListener() {
                // Zooms to the current location when first GPS fix arrives.
                @Override
                public void onLocationChanged(Location location) {
                    boolean locationChanged = false;

                    Log.e(Constants.TAG_GPS, "sukses location -> lat " + location.getLatitude() + " | lng " + location.getLongitude() + " | point " + MapUtils.getAsPoint(mMapSr, location));

                    if ( currentBestLocation != null ) {
                        boolean betterLocation = GpsUtils.isBetterLocation(location, currentBestLocation);
                        Log.d(Constants.TAG_GPS, "betterLocation = " + betterLocation);
                        if (betterLocation) {
                            currentBestLocation = location;
                            locationChanged = true;
                        }
                    } else {
                        currentBestLocation = location;
                        locationChanged = true;
                    }

                    locationTouch = currentBestLocation;

                    // After zooming, turn on the Location pan mode to show the location
                    // symbol. This will disable as soon as you interact with the map.
                    /*
                    if (!isFirst) {
                        latitude = loc.getLatitude();
                        longitude = loc.getLongitude();
                    }
                    */
                    if (locationChanged ) {
                        boolean loader = isFirst ? true : false;
                        getDataFromServer(loader);
                        mLDM.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);
                        isFirst = false;
                    }
                }


                @Override
                public void onProviderDisabled(String arg0) {
                }

                @Override
                public void onProviderEnabled(String arg0) {
                }

                @Override
                public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

                }
            });

            mLDM.start();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main_slider, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            toMainMenu();
            return super.onOptionsItemSelected(item);
        }

        if (item.getItemId() == R.id.goto_setting) {
            /*if (formRadius.is)*/
            return super.onOptionsItemSelected(item);
        }

        if (item.getItemId() == R.id.goto_search) {
            return super.onOptionsItemSelected(item);
        }

        /*//noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logout_user();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        toMainMenu();
    }

    public void toMainMenu(){
        Intent toMainMenu = new Intent(getApplicationContext(), MainMenuActivity.class);
        startActivity(toMainMenu);
        finish();
    }

    //map di update dan tidak clear
    private void clearCurrentResults() {

        if (mResultsLayer != null) {
            mResultsLayer.removeAll();
        }

        textView_id_loc.setText("");
        textView_loc_name.setText("");
        textView_loc_address.setText("");
        textView_loc_distance.setText("");
    }


    public void updateContent(Map<String, Object> attributes) {
        // This is called from UI thread (MapTap listener)
        String title = attributes.get("loc_name").toString();
        textView_loc_name.setText(title);

        String address = attributes.get("loc_address").toString();
        textView_loc_address.setText(address);

        String id_loc = attributes.get("id_loc").toString();
        textView_id_loc.setText(id_loc);

        String distance = attributes.get("loc_distance").toString();
        double meters = Double.parseDouble(distance);
        textView_loc_distance.setText("" + formatNumber.changeFormatNumber(meters) + " Km");
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.pause();
        if (mLDM != null) {
            mLDM.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.unpause();
        if (mLDM != null) {
            mLDM.resume();
        }
        Log.d(Constants.TAG_GPS, "ON RESUME currentBestLocation => " + (currentBestLocation != null ? String.format("lat=%s, long=%s", currentBestLocation.getLatitude(), currentBestLocation.getLongitude()) : null));
        setupLocator();
        setupLocationListener();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mLDM != null) {
            mLDM.stop();
        }
    }

    private class CallWebPageTask extends AsyncTask<String, Void, String> {
        private boolean showProgress = false;
        private Dialog dialog;
        protected Context context;
        ArrayList<HashMap<String, String>> arraylist;

        public CallWebPageTask(Context context, boolean showProgress) {
            this.context = context;
            this.showProgress = showProgress;
            this.dialog = DialogUtils.LoadingSpinner(context);
        }
        @Override
        protected void onPreExecute() {
            if ( showProgress) this.dialog.show();

        }

        @Override
        protected String doInBackground(String... url) {
            HttpGetOrPost httpGetOrPost = new HttpGetOrPost();
            String response = httpGetOrPost.getRequest(url[0]);
            try {
                //simpan data dari web ke dalam array
                JSONObject joResponse = new JSONObject(response);
                JSONArray jaTag = joResponse.getJSONArray("tag_cat");
                arraylist = new ArrayList<HashMap<String, String>>();
                HashMap<String, String> map;
                Log.e(Constants.TAG, "Data dari server -> " + jaTag.length());

                for (int i = 0; i < jaTag.length(); i++) {
                    //Log.d(Constants.TAG, "Data User Lokasi -> " + jaTag.getJSONObject(i));
                    map = new HashMap<String, String>();
                    map.put("id_loc",jaTag.getJSONObject(i).getString("id_location"));
                    map.put("loc_name",jaTag.getJSONObject(i).getString("location_name"));
                    map.put("loc_address",jaTag.getJSONObject(i).getString("location_address"));
                    map.put("loc_lat",jaTag.getJSONObject(i).getString("latitude"));
                    map.put("loc_lng",jaTag.getJSONObject(i).getString("longitude"));
                    map.put("cat_id",jaTag.getJSONObject(i).getString("category_id"));
                    map.put("loc_icon", jaTag.getJSONObject(i).getString("icon"));
                    map.put("loc_distance",jaTag.getJSONObject(i).getString("distance"));
                    arraylist.add(map);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(Constants.TAG, "cek 2 -> error" + e);
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            updateData(arraylist);
            if ( this.dialog.isShowing()) this.dialog.dismiss();
        }
    }

    public void updateData(ArrayList<HashMap<String, String>> arraylist) {
        MultiPoint fullExtent = new MultiPoint();
        Symbol symbol = null;
        //-6.21267000, 106.61778566
        Map<String, Object> attr = new HashMap<String, Object>();
        if (arraylist != null) {
            clearCurrentResults();

            HashMap<String, String> data = null;
            for (int i = 0; i < arraylist.size() ; i++) {
                data = arraylist.get(i);
                //skip null icon
                if ( "null".equalsIgnoreCase(data.get("loc_icon"))) continue;

                Location locationPin = GpsUtils.DUMMY_LOCATION;

                locationPin.setLatitude(Double.parseDouble(data.get("loc_lat")));
                locationPin.setLongitude(Double.parseDouble(data.get("loc_lng")));

                Point point = MapUtils.getAsPoint(mMapSr, locationPin);

                //Log.d(Constants.TAG, "map data => " + new JSONObject(data).toString());

                attr.put("id_loc", data.get("id_loc"));
                attr.put("loc_name", data.get("loc_name"));
                attr.put("loc_address", data.get("loc_address"));
                //Log.d(Constants.TAG, "map data => " + data.get("loc_icon"));

                attr.put("loc_icon", data.get("loc_icon"));
                attr.put("loc_distance", data.get("loc_distance"));


                for (int j = 0; j < categories.length ; j++) {
                    //Log.d(Constants.TAG, String.format("CAT1=>%s == CAT2=>%s", Integer.parseInt(arraylist.get(i).get("cat_id").toString()), categories[j].getId()));
                    if (Integer.parseInt(arraylist.get(i).get("cat_id").toString())==categories[j].getId()){
                        symbol = madd[j];
                    }
                }
                if (symbol==null){
                    symbol = mcat;
                }

                mResultsLayer.addGraphic(new Graphic(point, symbol, attr));
                fullExtent.add(point);
            }
            /*for (int i = 0; i < id_loc.length; i++) {
                Location locationPin = location;
                locationPin.setLatitude(loc_lat[i]);
                locationPin.setLongitude(loc_lng[i]);
                Point point = getAsPoint(locationPin);
                attr.put("id_loc", id_loc[i]);
                attr.put("loc_name", loc_name[i]);
                attr.put("loc_address", loc_address[i]);
                attr.put("loc_distance", loc_distance[i]);
                attr.put("loc_icon", loc_pic[i]);


                symbol = mcat;

                mResultsLayer.addGraphic(new Graphic(point, symbol, attr));
                fullExtent.add(point);
            }*/
            mMapView.setExtent(fullExtent, 100);
            if (arraylist.size() < 2) {
                if ((mLDM != null) && (mLDM.getLocation() != null)) {
                    // Keep current scale and go to current location, if there is one.
                    MapUtils.zoomToLocation(mMapView, mMapSr,currentBestLocation, ZOOM_BY);
                    mLDM.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);
                }
            }
            placeLayout.setVisibility(View.GONE);
        }
    }
}
