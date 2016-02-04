package com.bgs.dheket;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import com.bgs.common.Utility;
import com.bgs.networkAndSensor.Compass;
import com.bgs.networkAndSensor.ConfigInternetAndGPS;
import com.bgs.networkAndSensor.HttpGetOrPost;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOptions;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import org.json.JSONObject;

/**
 * Created by SND on 04/02/2016.
 */
public class SingleMapLocationActivity extends AppCompatActivity {

    ConfigInternetAndGPS checkInternetGPS;
    HttpGetOrPost httpGetOrPost;

    LocationManager myLocationManager;
    Criteria criteria;
    String provider;
    Location location;

    private JSONObject jObject;
    private String jsonResult ="";
    double radius = 0.0;
    double latitude, longitude;
    String url = "http://dheket.esy.es/getLocationPromo.php";
    android.support.v7.app.ActionBar actionBar;

    Utility formatNumber = new Utility();
    Compass mCompass;

    MapView mMapView;
    MapOptions options;

    int selectId,selectPromo,cat_id;
    String selectNameLoc, selectAddress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_single_map_location);
        actionBar = getSupportActionBar();

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeAsUpIndicator(R.drawable.logo);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Dheket");
//        actionBar.setSubtitle(Html.fromHtml("<font color='#FFBF00'>Location in Radius " + formatter.format(radius) + " Km</font>"));
        actionBar.setSubtitle(Html.fromHtml("<font color='#FFBF00'>Location in Radius " + formatNumber.changeFormatNumber(radius) + " Km</font>"));

        FacebookSdk.sdkInitialize(getApplicationContext());
        options = new MapOptions(MapOptions.MapType.STREETS, -6.246760,106.762618, 10);

        mMapView = new MapView(SingleMapLocationActivity.this, options);
        setContentView(mMapView);

        //mMapView = (MapView) findViewById(R.id.single_map);
        //mapoptions.center="34.056215, -117.195668"
        mMapView.centerAndZoom(34.056215, -117.195668,10);
        mMapView.setAllowRotationByPinch(true);
        mMapView.setMapOptions(null);
        // Set the MapView to allow the user to rotate the map when as part of a pinch gesture.

        // Enabled wrap around map.
        mMapView.enableWrapAround(true);

        // Create the Compass custom view, and add it onto the MapView.
        mCompass = new Compass(this, null, mMapView);
        mMapView.addView(mCompass);

        GraphicsLayer graphicsLayer = new GraphicsLayer(mMapView.getSpatialReference(), new Envelope(-180, -90, 180, 90));
        mMapView.addLayer(graphicsLayer);
        Point p = new Point(-9664114.480484284,3962469.970217699); //Birmingham
        mMapView.setResolution(mMapView.getMinResolution());
        //add marker
        Drawable d = getResources().getDrawable(R.drawable.logo_back);
        PictureMarkerSymbol sym = new PictureMarkerSymbol(d);
        Graphic g = new Graphic(p, sym);
        graphicsLayer.addGraphic(g);

        GraphicsLayer graphicsLayer1 = new GraphicsLayer();
        mMapView.addLayer(graphicsLayer1);
        Point point = new Point(1.3799775, 103.84877200000005);
        graphicsLayer1.addGraphic(new Graphic(point, new SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE)));

        // Set a single tap listener on the MapView.
        mMapView.setOnSingleTapListener(new OnSingleTapListener() {

            public void onSingleTap(float x, float y) {

                // When a single tap gesture is received, reset the map to its default rotation angle,
                // where North is shown at the top of the device.
                mMapView.setRotationAngle(0);

                // Also reset the compass angle.
                mCompass.setRotationAngle(0);
            }
        });

    }

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
            Intent intent = new Intent(SingleMapLocationActivity.this,DetailLocationActivity.class);
            Bundle paket = new Bundle();

            paket.putInt("id_loc", selectId);
            paket.putString("loc_name", selectNameLoc);
            paket.putInt("loc_promo", selectPromo);
            paket.putString("loc_address", selectAddress);
            paket.putInt("cat_id", cat_id);
            paket.putDouble("latitude", latitude);
            paket.putDouble("longitude", longitude);
            paket.putDouble("radius", radius);

            intent.putExtras(paket);
            startActivity(intent);
            finish();
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
                        Intent logout_user_fb = new Intent(SingleMapLocationActivity.this, FormLoginActivity.class);
                        startActivity(logout_user_fb);
                        finish();
                    }
                })
                .setNegativeButton(cancel, null);
        builder.create().show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Call MapView.pause to suspend map rendering while the activity is paused
        mMapView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Call MapView.unpause to resume map rendering when the activity returns to the foreground.
        mMapView.unpause();
    }

}
