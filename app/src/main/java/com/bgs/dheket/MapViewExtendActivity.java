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
import com.bgs.common.Utility;
import com.bgs.model.Category;
import com.bgs.model.Lokasi;
import com.bgs.networkAndSensor.Compass;
import com.bgs.networkAndSensor.HttpGetOrPost;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.MultiPoint;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;
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
public class MapViewExtendActivity extends AppCompatActivity {

    final static double ZOOM_BY = 15;

    MapView mMapView = null;
    SpatialReference mMapSr = null;
    GraphicsLayer mResultsLayer = null;
    ImageButton imageButton_gps_center;
    PictureMarkerSymbol mcat;
    PictureMarkerSymbol[] madd;
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

    private JSONObject jObject;
    private String jsonResult = "";
    //double radius = 0.0;
    //double latitude, longitude;
    String urls = "";
    String parameters;//, email, icon, category;
    //int cat_id, looping = 0;
    ViewGroup placeLayout;

    ArrayList<HashMap<String, String>> arraylist;

    boolean isFirst = true, maxView = true, minView = true;
    CallWebPageTask task;
    Bundle paket;

    private Category category;
    private Location currentBestLocation;
    private static final String ACTION_CALL_FROM_LOC_MAPWITHLIST = "com.bgs.dheket.map.action.CALL_FROM_MAPWITHLIST";

    public static void startFromMapWithList(Context context, Category category, Location location) {
        startMapActivity(context, ACTION_CALL_FROM_LOC_MAPWITHLIST, category, location);
    }

    private static void startMapActivity(Context context, String action, Category category, Location location) {
        Intent intent = new Intent(context, MapViewExtendActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(action);
        if ( category != null )
            intent.putExtra(ExtraParamConstants.CATEGORY, category);

        if ( location != null )
            intent.putExtra(ExtraParamConstants.CURRENT_BEST_LOCATION, location);

        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view_extend_list);
        Log.d(Constants.TAG, "MapViewExtendActivity=>onCreate");
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.d_ic_back));
        //actionBar.setHomeAsUpIndicator(R.drawable.logo);
        actionBar.setHomeButtonEnabled(true);

        //Retrieve the map and initial extent from XML layout
        mMapView = (MapView) findViewById(R.id.map_view_extend);
        mMapView.setOnStatusChangedListener(statusChangedListener);
        mMapView.setOnSingleTapListener(mapTapCallback);
        //mMapView.setOnLongPressListener(mapLongPress);

        category = (Category) getIntent().getParcelableExtra(ExtraParamConstants.CATEGORY);
        currentBestLocation = (Location) getIntent().getParcelableExtra(ExtraParamConstants.CURRENT_BEST_LOCATION);


        urls = String.format(getResources().getString(R.string.link_getLocationAndMerchByCategory));//"http://dheket.esy.es/getLocationByCategory.php"

        actionBar.setTitle(category.getName());
//        actionBar.setSubtitle(Html.fromHtml("<font color='#FFBF00'>Location in Radius " + formatter.format(radius) + " Km</font>"));
        actionBar.setSubtitle(Html.fromHtml("<font color='#ff9800' size='10'>Radius " + formatNumber.changeFormatNumber(category.getRadius()) + " Km</font>"));

        textView_id_loc = (TextView) findViewById(R.id.textView_map_extend_id);
        textView_loc_name = (TextView) findViewById(R.id.textView_map_extend_nama_lokasi);
        textView_loc_address = (TextView) findViewById(R.id.textView_map_extend_lokasinya);
        textView_loc_distance = (TextView) findViewById(R.id.textView_map_extend_jarak);
        btn_toMainMenu = (Button)findViewById(R.id.button_map_extend_tolist);
        imageButton_gps_center = (ImageButton)findViewById(R.id.imageButton_map_extend_center);
        placeLayout = (ViewGroup) findViewById(R.id.placeLayout_extend);
        placeLayout.setVisibility(View.GONE);

        String icon = category.getIcon();
        if (icon != null && !icon.isEmpty() && !(icon.equalsIgnoreCase("null") || icon.equalsIgnoreCase(""))){
            mcat= new PictureMarkerSymbol();
            mcat.setUrl(icon);
        } else {
            mcat= new PictureMarkerSymbol(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_blue));
            Log.e("icon kosong","["+icon+"]");
        }

        placeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent goToScreen = new Intent(MapViewExtendActivity.this, DetailLocationWithNoMerchantActivity.class);

                Bundle paket = new Bundle();
                paket.putInt("location_id", Integer.parseInt(textView_id_loc.getText().toString()));
                paket.putInt("cat_id", category.getId());
                paket.putString("kategori", category.getName());
                paket.putDouble("radius", category.getRadius());
                paket.putDouble("latitude", currentBestLocation.getLatitude());
                paket.putDouble("longitude", currentBestLocation.getLongitude());
                paket.putString("icon", category.getIcon());
                goToScreen.putExtras(paket);
                //startActivity(goToScreen);
                //finish();
            }
        });

        btn_toMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toListMap();
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
        //mAdd = new PictureMarkerSymbol(rootView.getContext().getApplicationContext(), ContextCompat.getDrawable(rootView.getContext().getApplicationContext(), R.drawable.pin_add));

        setupLocator();
        setupLocationListener();

        //first init
        getDataFromServer();
    }

    public void getDataFromServer() {
        task = new CallWebPageTask(this);
        double latitude = 0, longitude = 0;
        if ( currentBestLocation != null ) {
            latitude = currentBestLocation.getLatitude();
            longitude = currentBestLocation.getLongitude();
        }
        parameters = urls +"/"+ category.getRadius() + "/" + latitude + "/" + longitude + "/" + category.getId();
        Log.e("OK Connecting Sukses", "" + parameters);
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
                Log.e("atrribut", "" + gr.getAttributes());

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

                boolean locationChanged = false;

                // Zooms to the current location when first GPS fix arrives.
                @Override
                public void onLocationChanged(Location loc) {
                    if (!locationChanged) {
                        locationChanged = true;
                        Log.d(Constants.TAG, "sukses location -> lat " + loc.getLatitude() + " | lng " + loc.getLongitude() + " | point " + getAsPoint(loc));

                        if ( currentBestLocation != null ) {
                            if (GpsUtils.isBetterLocation(loc, currentBestLocation)) {
                                currentBestLocation = loc;
                            }
                        } else { currentBestLocation = loc; }

                        locationTouch = currentBestLocation;

                        getDataFromServer();
                        /*Toast.makeText(getApplicationContext(), "location change " + (looping++), Toast.LENGTH_SHORT).show();*/
                        mLDM.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);
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

    /**
     * Zoom to location using a specific size of extent.
     *
     * @param loc the location to center the MapView at
     */
    private void zoomToLocation(Location loc) {
        Point mapPoint = getAsPoint(loc);
        Unit mapUnit = mMapSr.getUnit();
        double zoomFactor = Unit.convertUnits(ZOOM_BY,
                Unit.create(LinearUnit.Code.MILE_US), mapUnit);
        Envelope zoomExtent = new Envelope(mapPoint, zoomFactor, zoomFactor);
        mMapView.setExtent(zoomExtent);
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
            toListMap();
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
        toListMap();
    }

    private void toListMap(){
        Log.d(Constants.TAG, "BACK TO => MapViewWithListActivity FROM => MapViewExtendActivity");
        Intent toListMap = new Intent(this, MapViewWithListActivity.class);
        toListMap.putExtra(ExtraParamConstants.CATEGORY, category);
        toListMap.putExtra(ExtraParamConstants.CURRENT_BEST_LOCATION, currentBestLocation);
        /*
        Bundle paket = new Bundle();
        paket.putInt("cat_id", cat_id);
        paket.putString("kategori", category);
        paket.putDouble("radius", radius);
        paket.putDouble("latitude", latitude);
        paket.putDouble("longitude", longitude);
        paket.putString("icon", icon);
        toListMap.putExtras(paket);
        startActivity(toListMap);
        */
        finish();
    }

    private void clearCurrentResults() {
        if (mResultsLayer != null) {
            mResultsLayer.removeAll();
        }
        textView_id_loc.setText("");
        textView_loc_name.setText("");
        textView_loc_address.setText("");
        textView_loc_distance.setText("");
    }


    private Point getAsPoint(Location loc) {
        Point wgsPoint = new Point(loc.getLongitude(), loc.getLatitude());
        return (Point) GeometryEngine.project(wgsPoint, SpatialReference.create(4326),
                mMapSr);
    }

    public Point onSingleTaps(float x, float y) {
        Point pnt = (Point) GeometryEngine.project(mMapView.toMapPoint(x, y), mMapView.getSpatialReference(), SpatialReference.create(4326));
        return pnt;
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
        Log.d(Constants.TAG, this.getLocalClassName() + "=>onPause");
        Log.d(Constants.TAG, this.getLocalClassName() + String.format("=>lat=%s, long=%s", currentBestLocation.getLatitude(), currentBestLocation.getLongitude()));
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
        protected Context context;
        private Dialog dialog;
        private Graphic[] graphics;
        MultiPoint fullExtent = new MultiPoint();

        public CallWebPageTask(Context context) {
            dialog = DialogUtils.LoadingSpinner(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected String doInBackground(String... url) {
            String response = "";
            HttpGetOrPost httpGetOrPost = new HttpGetOrPost();
            response = httpGetOrPost.getRequest(url[0]);
            JSONArray locationArray = null;
            JSONObject joResponse;
            JSONObject data;
            try {
                joResponse = new JSONObject(response);
                //get locations
                if ( !joResponse.isNull("locations")) {
                    locationArray = joResponse.getJSONArray("locations");
                    Log.d(Constants.TAG, "Data lokasi dari server -> " + locationArray.length());
                    Lokasi lokasi = null;
                    Map<String, Object> attr = null;
                    int totalLocation = locationArray.length();
                    //store graphic obj
                    graphics = new Graphic[totalLocation];
                    for (int i = 0; i < totalLocation; i++) {
                        try {

                            data = locationArray.getJSONObject(i);
                            //Log.d(Constants.TAG, "MapViewExtendActivity=>data => " + data.toString());
                            lokasi = new Lokasi();
                            lokasi.setId(Integer.parseInt(data.getString("id_location")));
                            lokasi.setName(data.getString("location_name"));
                            lokasi.setAddress(data.getString("location_address"));
                            lokasi.setLatitude(Double.parseDouble(data.getString("latitude")));
                            lokasi.setLongitude(Double.parseDouble(data.getString("longitude")));
                            lokasi.setDistance(Double.parseDouble(data.getString("distance")));


                            Location locationPin = Constants.DEMO_LOCATION;
                            locationPin.setLatitude(lokasi.getLatitude());
                            locationPin.setLongitude(lokasi.getLongitude());
                            Point point = getAsPoint(locationPin);
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
                }

            } catch (JSONException e) {
                Log.e(Constants.TAG, e.getMessage(), e);
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            updateData(graphics, fullExtent);
            if ( dialog.isShowing()) dialog.dismiss();
        }
    }

    public void updateData(Graphic[] graphics, MultiPoint fullExtent) {
        if (graphics != null) {
            clearCurrentResults();
            //redraw layer
            mResultsLayer.addGraphics(graphics);

            mMapView.setExtent(fullExtent, 100);
            if (graphics.length < 2) {
                if ((mLDM != null) && (mLDM.getLocation() != null)) {
                    Log.d(Constants.TAG, String.format("lat=%s, long=%s", currentBestLocation.getLatitude(), currentBestLocation.getLongitude()));

                    // Keep current scale and go to current location, if there is one.
                    zoomToLocation(currentBestLocation);
                    mLDM.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);
                }
            }
            placeLayout.setVisibility(View.GONE);
        }
    }
}
