package com.bgs.dheket;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bgs.common.Constants;
import com.bgs.common.DialogUtils;
import com.bgs.common.ExtraParamConstants;
import com.bgs.common.GpsUtils;
import com.bgs.common.MapUtils;
import com.bgs.common.Utility;
import com.bgs.model.Category;
import com.bgs.model.Lokasi;
import com.bgs.model.Merchant;
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
public class MapViewWithListActivity extends AppCompatActivity {

    final static double ZOOM_BY = 15;

    MapView mMapView = null;
    SpatialReference mMapSr = null;
    GraphicsLayer mResultsLayer = null;

    PictureMarkerSymbol mcat;
    int[] id_cat;
    String[] icon_cat;
    Menu menu;
    // Views to show selected search result information.

    android.support.v7.app.ActionBar actionBar;

    Locator mLocator;
    Location locationTouch;
    ArrayList<String> mFindOutFields = new ArrayList<>();

    LocationDisplayManager mLDM;

    //Utility formatNumber = new Utility();

    private String jsonResult = "";

    //store data katagory yg dikirim via intent
    private Category category;
    private Location currentBestLocation;

    String urls = "";
    String parameters;
    LinearLayout linearLayout_contentlist;
    private ScrollView contentView;

    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    //FrameLayout progressBarHolder;

    /*String[] loc_name, loc_address, loc_pic;
    int[] loc_promo, id_loc;
    double[] loc_distance, loc_lat, loc_lng;*/

    boolean isFirst = true, maxView = true, minView = true;
    CallWebPageTask task;

    //Bundle paket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_map_loc);

        Log.d(Constants.TAG, "MapViewWithListActivity -> onCreate");
        //progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        //contentView = (ScrollView) findViewById(R.id.scrollView9);
        //showProgresBar();

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.d_ic_back));
        //actionBar.setHomeAsUpIndicator(R.drawable.logo);
        actionBar.setHomeButtonEnabled(true);

        //Retrieve the map and initial extent from XML layout
        mMapView = (MapView) findViewById(R.id.map_with_list);
        mMapView.setOnStatusChangedListener(statusChangedListener);
        mMapView.setOnSingleTapListener(mapTapCallback);
        //mMapView.setOnLongPressListener(mapLongPress);

        //get category from bundle
        category = (Category) getIntent().getParcelableExtra(ExtraParamConstants.CATEGORY);
        currentBestLocation = GpsUtils.DEMO_LOCATION;

        actionBar.setTitle(category.getName());
//        actionBar.setSubtitle(Html.fromHtml("<font color='#FFBF00'>Location in Radius " + formatter.format(radius) + " Km</font>"));
        actionBar.setSubtitle(Html.fromHtml("<font color='#ff9800' size='10'>Radius " + Utility.changeFormatNumber(category.getRadius()) + " Km</font>"));

        urls = String.format(getResources().getString(R.string.link_getLocationAndMerchByCategory));//"http://dheket.esy.es/getLocationByCategory.php"

        mResultsLayer = new GraphicsLayer();
        mResultsLayer.setSelectionColorWidth(6);
        mMapView.addLayer(mResultsLayer);
        mMapView.setAllowRotationByPinch(true);

        // Enabled wrap around map.
        mMapView.enableWrapAround(true);
        String icon = category.getIcon();
        Log.d(Constants.TAG, "icon -> ["+icon+"]");
        Log.d(Constants.TAG, String.format("%s - %s - %s", icon != null, !icon.isEmpty(), !(icon.equalsIgnoreCase("null") || icon.equalsIgnoreCase(""))));
        if (icon != null && !icon.isEmpty() && !(icon.equalsIgnoreCase("null") || icon.equalsIgnoreCase(""))){
            mcat= new PictureMarkerSymbol();
            mcat.setUrl(icon);
        } else {
            mcat= new PictureMarkerSymbol(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_blue));
        }
        Log.d(Constants.TAG, "mcat -> ["+mcat.getUrl()+"]");

        //mAdd = new PictureMarkerSymbol(rootView.getContext().getApplicationContext(), ContextCompat.getDrawable(rootView.getContext().getApplicationContext(), R.drawable.pin_add));
        linearLayout_contentlist = (LinearLayout)findViewById(R.id.linearLayout_result_lm);
        setupLocator();
        setupLocationListener();

        //first fill data
        getDataFromServer();
    }


    public void getDataFromServer() {
        task = new CallWebPageTask(this);
        /*getlocationbycategoryid/{rad}/{center_lat}/{center_lng}/{cat}*/
        double latitude =0, longitude = 0;
        if ( currentBestLocation != null) {
            latitude = currentBestLocation.getLatitude();
            longitude = currentBestLocation.getLongitude();
        }
        parameters = urls +"/"+ category.getRadius() + "/" + latitude  + "/" + longitude  + "/" + category.getId();
        Log.d(Constants.TAG, "OK Connecting Sukses -> " + parameters);
        //Log.d("Sukses", parameters);
        task.execute(new String[]{parameters});
    }


    /**
     * When the map is tapped, select the graphic at that location.
     */
    private OnSingleTapListener mapTapCallback = new OnSingleTapListener() {
        @Override
        public void onSingleTap(float x, float y) {
            // Find out if we tapped on a Graphic
            MapViewExtendActivity.startFromMapWithList(getApplicationContext(), category);
            //stop location manager
            //onStop();
            //hack mode
            //clearCurrentResults();
            //finish();
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
        Log.d(Constants.TAG, mMapView + " -> isLoaded " + (mMapView != null ? mMapView.isLoaded() : false) );
        if ((mMapView != null) && (mMapView.isLoaded())) {
            mLDM = mMapView.getLocationDisplayManager();
            mLDM.setLocationListener(new LocationListener() {
                // Zooms to the current location when first GPS fix arrives.
                @Override
                public void onLocationChanged(Location loc) {
                    boolean locationChanged = false;
                    Log.d(Constants.TAG, "sukses location -> lat " + loc.getLatitude() + " | lng " + loc.getLongitude() + " | point " + MapUtils.getAsPoint(mMapSr, loc));

                    if ( currentBestLocation != null ) {
                        if (GpsUtils.isBetterLocation(loc, currentBestLocation)) {
                            currentBestLocation = loc;
                        } else {
                            locationChanged = true;
                        }

                    } else { currentBestLocation = loc; }

                    locationTouch = currentBestLocation;

                    // After zooming, turn on the Location pan mode to show the location
                    // symbol. This will disable as soon as you interact with the map.
                    if ( !isFirst && locationChanged ) {
                        getDataFromServer();
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
        menu.getItem(0).setVisible(false);
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
        Intent toMainMenu = new Intent(this, MainMenuActivity.class);
        startActivity(toMainMenu);
        finish();
    }

    public void clearCurrentResults() {
        /*
        if (mResultsLayer != null && clearMap) {
            mResultsLayer.removeAll();
        }
        */
        linearLayout_contentlist.removeAllViews();
    }




    public void updateContent(Map<String, Object> attributes) {
        // This is called from UI thread (MapTap listener)
        String title = attributes.get("loc_name").toString();
        //textView_loc_name.setText(title);

        String address = attributes.get("loc_address").toString();
        //textView_loc_address.setText(address);

        String id_loc = attributes.get("id_loc").toString();
        //textView_id_loc.setText(id_loc);

        String distance = attributes.get("loc_distance").toString();
        double meters = Double.parseDouble(distance);
        //textView_loc_distance.setText("" + formatNumber.changeFormatNumber(meters) + " Km");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(Constants.TAG, this.getLocalClassName() + "=>onPause");
        mMapView.pause();
        if (mLDM != null) {
            mLDM.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(Constants.TAG, this.getLocalClassName() + "=>onResume");
        mMapView.unpause();
        if (mLDM != null) {
            mLDM.resume();
        }
        setupLocator();
        setupLocationListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(Constants.TAG, this.getLocalClassName() + "=>onStop");
        if (mLDM != null) {
            mLDM.stop();
        }
    }

    private class CallWebPageTask extends AsyncTask<String, Void, String> {
        private Context context;
        private ArrayList<Lokasi> locationList;
        private Dialog dialog;
        //private Graphic[] graphics;
        Map<String, ArrayList> graphicMap;
        MultiPoint fullExtent = new MultiPoint();

        public CallWebPageTask(Context context) {
            this.context = context;
            this.dialog = DialogUtils.LoadingSpinner(context);
        }

        @Override
        protected void onPreExecute() {
            //this.dialog.setMessage("Loading");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... url) {
            String response = "";
            HttpGetOrPost httpGetOrPost = new HttpGetOrPost();
            response = httpGetOrPost.getRequest(url[0]);
            Map<String, Merchant> merchantMap = new HashMap<String, Merchant>();
            locationList = new ArrayList<Lokasi>();
            JSONArray locationArray = null;
            JSONArray merchantArray = null;
            JSONObject joResponse;
            JSONObject data;

            try {
                joResponse = new JSONObject(response);
                String merchId = "";
                //get merchant
                if ( !joResponse.isNull("merchants") ) {
                    merchantArray = joResponse.getJSONArray("merchants");
                    Log.d(Constants.TAG, "Data merchant dari server -> " + merchantArray.length());
                    Merchant merchant = null;
                    for (int i = 0; i < merchantArray.length(); i++) {
                        try {
                            data = merchantArray.getJSONObject(i);
                            merchId = data.getString("id_merchant");
                            //merchant_name, username,email,gender, birthday":null,"phone":null,"address":null,"facebook_photo":}
                            merchant = new Merchant();
                            merchant.setId(Integer.parseInt(merchId));
                            merchant.setName(data.getString("merchant_name"));
                            merchant.setUserName(data.getString("username"));
                            merchant.setEmail(data.getString("email"));
                            merchant.setGender(data.getString("gender"));
                            merchant.setBirthDay(data.getString("birthday"));
                            merchant.setPhone(data.getString("phone"));
                            merchant.setAddress(data.getString("address"));
                            merchant.setFacebookPhoto(data.getString("facebook_photo"));
                            merchantMap.put(merchId, merchant);
                        } catch (JSONException e) {
                            Log.e(Constants.TAG, e.getMessage(), e);
                        }
                    }
                }

                //get locations
                if ( !joResponse.isNull("locations")) {
                    locationArray = joResponse.getJSONArray("locations");
                    Log.d(Constants.TAG, "Data lokasi dari server -> " + locationArray.length());
                    Lokasi lokasi = null;
                    Map<String, Object> attr = null;
                    int totalLocation = locationArray.length();

                    //store graphic obj
                    final Graphic[] graphics = new Graphic[totalLocation];
                    for (int i = 0; i < totalLocation; i++) {
                        try {

                            data = locationArray.getJSONObject(i);
                            //Log.d(Constants.TAG, "MapViewWithListActivity=>data => " + data.toString());
                            merchId = data.getString("merchant_id");
                            /*
                            int id, String name, String address, double latitude, double longitude,
                            String phone, int isPromo, int idLocationHere, String description, String locationTag, double distance
                             */
                            lokasi = new Lokasi();
                            lokasi.setId(Integer.parseInt(data.getString("id_location")));
                            lokasi.setName(data.getString("location_name"));
                            lokasi.setAddress(data.getString("location_address"));
                            lokasi.setLatitude(Double.parseDouble(data.getString("latitude")));
                            lokasi.setLongitude(Double.parseDouble(data.getString("longitude")));
                            lokasi.setPhone("");
                            lokasi.setIsPromo(0);
                            lokasi.setIdLocationHere("");
                            lokasi.setDescription("");
                            lokasi.setLocationTag("");
                            lokasi.setDistance(Double.parseDouble(data.getString("distance")));
                            lokasi.setCategory(category);
                            lokasi.setMerchant(merchantMap.get(merchId));
                            locationList.add(lokasi);

                            Location locationPin = GpsUtils.DEMO_LOCATION;
                            locationPin.setLatitude(lokasi.getLatitude());
                            locationPin.setLongitude(lokasi.getLongitude());
                            Point point = MapUtils.getAsPoint(mMapSr, locationPin);
                            fullExtent.add(point);

                            attr = new HashMap<String, Object>();
                            attr.put("id_loc", lokasi.getId());
                            attr.put("loc_name", lokasi.getName());
                            attr.put("loc_address", lokasi.getAddress());
                            attr.put("loc_distance", lokasi.getDistance());


                            graphics[i] = new Graphic(point, mcat, attr);

                        } catch (JSONException e) {
                            Log.e(Constants.TAG, e.getMessage(), e);
                        }
                    }
                    graphicMap = MapUtils.analyzeGraphics(mResultsLayer, graphics);
                }

            } catch (JSONException e) {
                Log.e(Constants.TAG, e.getMessage(), e);
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            updateData(locationList, graphicMap, fullExtent);
            if (dialog.isShowing()) dialog.dismiss();
        }
    }

    public void updateData(ArrayList<Lokasi> locationList, Map<String, ArrayList> graphicMap, MultiPoint fullExtent) {
        Log.d(Constants.TAG, "call updateData()");
        if (locationList != null) {
            //clear data
            clearCurrentResults();
            MapUtils.updateMap(mResultsLayer, graphicMap);
            for (final Lokasi lokasi : locationList) {

                LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                final View viewItem = inflater.inflate(R.layout.item_listmod, null);
                final TextView id = (TextView)viewItem.findViewById(R.id.textView_il_id);

                id.setText(String.valueOf(lokasi.getId()));
                final TextView nama = (TextView)viewItem.findViewById(R.id.textView_il_nama);
                nama.setText(lokasi.getName());
                TextView alamat = (TextView)viewItem.findViewById(R.id.textView_il_alamat);
                alamat.setText(lokasi.getAddress());
                TextView jarak = (TextView)viewItem.findViewById(R.id.textView_il_jarak);

                jarak.setText(Utility.andjustDistanceUnit(lokasi.getDistance()));
                ImageView foto = (ImageView)viewItem.findViewById(R.id.imageView_il_foto);
                viewItem.setTag(lokasi);
                linearLayout_contentlist.addView(viewItem);

                viewItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(Constants.TAG, "call listItem->setOnClickListener()");
                        //Toast.makeText(ll.getContext().getApplicationContext(),nama.getText().toString(),Toast.LENGTH_SHORT).show();

                        Intent goToScreen = null;
                        Lokasi _lokasi =  (Lokasi)v.getTag();
                        if ( _lokasi.getMerchant() != null ) {
                            Log.d(Constants.TAG, "call listItem->setOnClickListener()=>merchant=" + _lokasi.getMerchant().getName() );
                            goToScreen = new Intent(MapViewWithListActivity.this, DetailLocationWithMerchantActivity.class);
                        } else {
                            goToScreen = new Intent(MapViewWithListActivity.this, DetailLocationWithNoMerchantActivity.class);
                        }
                        if ( goToScreen != null ) {
                            goToScreen.putExtra(ExtraParamConstants.LOKASI_DETAIL, _lokasi);
                            startActivity(goToScreen);
                            finish();
                        }
                    }
                });
            }

            mMapView.setExtent(fullExtent, 100);
            if (locationList.size() < 2) {
                if ((mLDM != null) && (mLDM.getLocation() != null)) {
                    // Keep current scale and go to current location, if there is one.
                    MapUtils.zoomToLocation(mMapView, mMapSr, currentBestLocation, ZOOM_BY);
                    mLDM.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);
                }
            }
        }
    }


}
