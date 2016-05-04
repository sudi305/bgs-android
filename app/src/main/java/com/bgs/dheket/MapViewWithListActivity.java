package com.bgs.dheket;

import android.app.ProgressDialog;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bgs.common.Utility;
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
    Location location;
    ArrayList<String> mFindOutFields = new ArrayList<>();

    LocationDisplayManager mLDM;

    Utility formatNumber = new Utility();

    private JSONObject jObject;
    private String jsonResult = "";
    double radius = 0.0;
    double latitude, longitude;
    String urls = "";
    String parameters, email, icon,category;
    int cat_id;
    LinearLayout linearLayout_contentlist;

    ArrayList<HashMap<String, String>> arraylist;
    /*String[] loc_name, loc_address, loc_pic;
    int[] loc_promo, id_loc;
    double[] loc_distance, loc_lat, loc_lng;*/

    boolean isFirst = true, maxView = true, minView = true;
    CallWebPageTask task;
    Bundle paket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_map_loc);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeAsUpIndicator(R.drawable.logo);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Dheket");
//        actionBar.setSubtitle(Html.fromHtml("<font color='#FFBF00'>Location in Radius " + formatter.format(radius) + " Km</font>"));
        actionBar.setSubtitle(Html.fromHtml("<font color='#ff9800' size='10'>Radius " + formatNumber.changeFormatNumber(radius) + " Km</font>"));

        //Retrieve the map and initial extent from XML layout
        mMapView = (MapView) findViewById(R.id.map_single);
        mMapView.setOnStatusChangedListener(statusChangedListener);
        mMapView.setOnSingleTapListener(mapTapCallback);
        //mMapView.setOnLongPressListener(mapLongPress);

        paket = getIntent().getExtras();
        latitude = paket.getDouble("latitude");
        cat_id = paket.getInt("cat_id");
        longitude = paket.getDouble("longitude");
        email = paket.getString("email");
        icon = paket.getString("icon");
        category = paket.getString("kategori");
        radius = paket.getDouble("radius");

        urls = String.format(getResources().getString(R.string.link_getLocationByCategory));//"http://dheket.esy.es/getLocationByCategory.php"

        mResultsLayer = new GraphicsLayer();
        mResultsLayer.setSelectionColorWidth(6);
        mMapView.addLayer(mResultsLayer);
        mMapView.setAllowRotationByPinch(true);

        // Enabled wrap around map.
        mMapView.enableWrapAround(true);
        if (!icon.isEmpty() && (!icon.equalsIgnoreCase("null") || !icon.equalsIgnoreCase(""))){
            mcat= new PictureMarkerSymbol();
            mcat.setUrl(icon);
        } else {
            mcat= new PictureMarkerSymbol(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin_blue));
            Log.e("icon kosong","["+icon+"]");
        }
        //mAdd = new PictureMarkerSymbol(rootView.getContext().getApplicationContext(), ContextCompat.getDrawable(rootView.getContext().getApplicationContext(), R.drawable.pin_add));
        linearLayout_contentlist = (LinearLayout)findViewById(R.id.linearLayout_result_lm);
        setupLocator();
        setupLocationListener();
    }

    public void getDataFromServer() {
        task = new CallWebPageTask();
        task.applicationContext = getApplicationContext();
        /*getlocationbycategoryid/{rad}/{center_lat}/{center_lng}/{cat}*/
        parameters = urls +"/"+ radius + "/" + latitude + "/" + longitude + "/" +cat_id;
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
            Intent gotoMapExtend = new Intent(getApplicationContext(),MapViewExtendActivity.class);
            Bundle paket = new Bundle();
            paket.putInt("cat_id", cat_id);
            paket.putString("kategori", category);
            paket.putDouble("radius", radius);
            paket.putDouble("latitude", latitude);
            paket.putDouble("longitude", longitude);
            paket.putString("icon", icon);
            gotoMapExtend.putExtras(paket);
            startActivity(gotoMapExtend);
            finish();
            /*//Toast.makeText(rootView.getContext().getApplicationContext(),"this location at x= "+x+" and y= "+y+" | point on SingleTaps"+onSingleTaps(x,y)+"",Toast.LENGTH_SHORT).show();
            int[] graphicIDs = mResultsLayer.getGraphicIDs(x, y, 25);
            if (graphicIDs != null && graphicIDs.length > 0) {
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
                mMapView.setRotationAngle(0);
                // Also reset the compass angle.
            }*/
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
                        Log.e("sukses location ", "lat " + loc.getLatitude() + " | lng " + loc.getLongitude() + " | point " + getAsPoint(loc));
                        location = loc;
                        locationTouch = location;
                        // After zooming, turn on the Location pan mode to show the location
                        // symbol. This will disable as soon as you interact with the map.
                        if (!isFirst) {
                            latitude = loc.getLatitude();
                            longitude = loc.getLongitude();
                        }

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
        Intent toMainMenu = new Intent(getApplicationContext(),MainMenuActivity.class);
        startActivity(toMainMenu);
        finish();
    }

    private void clearCurrentResults() {
        if (mResultsLayer != null) {
            mResultsLayer.removeAll();
        }
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

        private ProgressDialog dialog;
        protected Context applicationContext;

        @Override
        protected void onPreExecute() {
            /*if (isFirst==true){
                this.dialog = ProgressDialog.show(applicationContext, "Retrieving Data", "Please Wait...", true);
            }*/
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
                arraylist = new ArrayList<HashMap<String, String>>();
                Log.e("Data dari server", "" + menuItemArray.length());
                for (int i = 0; i < menuItemArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("id_loc",menuItemArray.getJSONObject(i).getString("id_location"));
                    map.put("loc_name",menuItemArray.getJSONObject(i).getString("location_name"));
                    map.put("loc_address",menuItemArray.getJSONObject(i).getString("location_address"));
                    map.put("loc_lat",menuItemArray.getJSONObject(i).getString("latitude"));
                    map.put("loc_lng",menuItemArray.getJSONObject(i).getString("longitude"));
                    map.put("cat_id",menuItemArray.getJSONObject(i).getString("category_id"));
                    map.put("loc_distance",""+Double.parseDouble(formatNumber.changeFormatNumber(menuItemArray.getJSONObject(i).getDouble("distance"))));
                    arraylist.add(map);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("cek 2", "error" + e);
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            updateData();
        }
    }

    public void updateData() {
        MultiPoint fullExtent = new MultiPoint();
        Symbol symbol = null;
        //-6.21267000, 106.61778566
        Map<String, Object> attr = new HashMap<String, Object>();
        if (arraylist != null) {
            mResultsLayer.removeAll();
            clearCurrentResults();
            for (int i = 0; i < arraylist.size() ; i++) {
                Location locationPin = location;
                locationPin.setLatitude(Double.parseDouble(arraylist.get(i).get("loc_lat").toString()));
                locationPin.setLongitude(Double.parseDouble(arraylist.get(i).get("loc_lng").toString()));
                Point point = getAsPoint(locationPin);
                attr.put("id_loc", arraylist.get(i).get("id_loc").toString());
                attr.put("loc_name", arraylist.get(i).get("loc_name").toString());
                attr.put("loc_address", arraylist.get(i).get("loc_address").toString());
                attr.put("loc_distance", arraylist.get(i).get("loc_distance").toString());

                symbol = mcat;

                mResultsLayer.addGraphic(new Graphic(point, symbol, attr));
                fullExtent.add(point);

                LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                View ll = inflater.inflate(R.layout.item_listmod, null);
                TextView id = (TextView)ll.findViewById(R.id.textView_il_id);
                id.setText(arraylist.get(i).get("id_loc").toString());
                TextView nama = (TextView)ll.findViewById(R.id.textView_il_nama);
                nama.setText(arraylist.get(i).get("loc_name").toString());
                TextView alamat = (TextView)ll.findViewById(R.id.textView_il_alamat);
                alamat.setText(arraylist.get(i).get("loc_address").toString());
                TextView jarak = (TextView)ll.findViewById(R.id.textView_il_jarak);
                jarak.setText(arraylist.get(i).get("loc_distance").toString()+" Km");
                ImageView foto = (ImageView)ll.findViewById(R.id.imageView_il_foto);
                linearLayout_contentlist.addView(ll);
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
                    zoomToLocation(location);
                    mLDM.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);
                }
            }
        }
    }
}
