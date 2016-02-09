package com.bgs.extended;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bgs.common.Utility;
import com.bgs.dheket.DetailLocationActivity;
import com.bgs.dheket.R;
import com.bgs.networkAndSensor.Compass;
import com.bgs.networkAndSensor.HttpGetOrPost;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnLongPressListener;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.MultiPoint;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.Symbol;
import com.esri.core.tasks.geocode.Locator;
import com.esri.core.tasks.geocode.LocatorFindParameters;
import com.esri.core.tasks.geocode.LocatorGeocodeResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by SND on 27/01/2016.
 */
public class TabFragmentMap extends Fragment {
    View rootView;

    private static final String TAG = TabFragmentMap.class.getSimpleName();

    // Define types of search.
    private enum SearchType {
        BAR,
        PIZZA,
        COFFEE,
    }
    SearchType mCurrentSearchType;
    final static double ZOOM_BY = 15;
    LinearUnit mMilesUnit = new LinearUnit(LinearUnit.Code.MILE_STATUTE);

    MapView mMapView = null;
    SpatialReference mMapSr = null;
    GraphicsLayer mResultsLayer = null;
    ImageView imageView_pic, imageView_pic_back;
    ImageButton imageButton_minimaze,imageButton_maximaze,imageButton_gps_center;
    PictureMarkerSymbol mCat1, mCat2, mCat3, mCat4, mCat5, mAdd;
    // Views to show selected search result information.
    TextView textView_id_loc,textView_loc_name,textView_loc_address,textView_loc_promo,textView_loc_idstance;

    android.support.v7.app.ActionBar actionBar;

    Locator mLocator;
    Location locationTouch;
    Location location;
    ArrayList<String> mFindOutFields = new ArrayList<>();

    LocationDisplayManager mLDM;

    Compass mCompass;
    Utility formatNumber = new Utility();

    private JSONObject jObject;
    private String jsonResult ="";
    double radius = 0.0;
    double latitude, longitude;
    String urls ="http://dheket.esy.es/getLocationByCategory.php";
    String parameters,kategori;
    int cat_id;
    ViewGroup placeLayout;

    String[] loc_name, loc_address,loc_pic;
    int[] loc_promo,id_loc,temp_loc_promo,temp_id_loc;
    double[] loc_distance, loc_lat, loc_lng, temp_loc_distance;
    boolean isFirst = true,maxView = true, minView = true,showDetail = true;
    CallWebPageTask task;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab_fragment_map,container, false);
        //Retrieve the map and initial extent from XML layout
        mMapView = (MapView)rootView.findViewById(R.id.map);
        mMapView.setOnStatusChangedListener(statusChangedListener);
        mMapView.setOnSingleTapListener(mapTapCallback);
        //mMapView.setOnLongPressListener(mapLongPress);

        cat_id = getArguments().getInt("cat_id");
        radius = getArguments().getDouble("radius");
        latitude = getArguments().getDouble("latitude");
        longitude = getArguments().getDouble("longitude");
        kategori = getArguments().getString("kategori");

        imageView_pic = (ImageView)rootView.findViewById(R.id.imageView_tfm_pic);
        imageView_pic_back = (ImageView)rootView.findViewById(R.id.imageView_tfm_pic_back);
        imageButton_minimaze = (ImageButton)rootView.findViewById(R.id.imageButton_minimaze);
        imageButton_maximaze = (ImageButton)rootView.findViewById(R.id.imageButton_maximize);
        imageButton_gps_center = (ImageButton)rootView.findViewById(R.id.imageButton_gps_center);
        textView_id_loc = (TextView)rootView.findViewById(R.id.textView_tmf_id_loc);
        textView_loc_name = (TextView)rootView.findViewById(R.id.textView_tfm_loc_name);
        textView_loc_address = (TextView)rootView.findViewById(R.id.textView_tfm_loc_address);
        textView_loc_promo = (TextView)rootView.findViewById(R.id.textView_tfm_promo);
        textView_loc_idstance = (TextView)rootView.findViewById(R.id.textView_tfm_dist);
        placeLayout = (ViewGroup)rootView.findViewById(R.id.detail_loc);
        placeLayout.setVisibility(View.GONE);

        placeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(rootView.getContext(), DetailLocationActivity.class);
                Bundle paket = new Bundle();
                paket.putInt("id_loc", Integer.parseInt(textView_id_loc.getText().toString()));
                paket.putString("loc_name", textView_loc_name.getText().toString());
                int promo;
                if (textView_loc_promo.getText().toString().equalsIgnoreCase("promo")) promo = 1;
                else promo = 0;
                paket.putInt("loc_promo", promo);
                paket.putString("loc_address", textView_loc_address.getText().toString());
                paket.putInt("cat_id", cat_id);
                paket.putDouble("latitude", latitude);
                paket.putDouble("longitude", longitude);
                paket.putDouble("radius", radius);
                paket.putString("kategori",kategori);
                //Toast.makeText(rootView.getContext(), "Id Loc " + selectId, Toast.LENGTH_LONG).show();
                i.putExtras(paket);
                startActivity(i);
                getActivity().finish();
            }
        });

        imageButton_minimaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeLayout.setVisibility(View.GONE);
                minView = false;
                maxView = true;
                imageButton_maximaze.setVisibility(View.VISIBLE);
                imageButton_minimaze.setVisibility(View.GONE);
            }
        });

        imageButton_maximaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeLayout.setVisibility(View.VISIBLE);
                minView = true;
                maxView = false;
                imageButton_maximaze.setVisibility(View.GONE);
                imageButton_minimaze.setVisibility(View.VISIBLE);
            }
        });

        mResultsLayer = new GraphicsLayer();
        mResultsLayer.setSelectionColorWidth(6);
        mMapView.addLayer(mResultsLayer);
        mMapView.setAllowRotationByPinch(true);

        // Enabled wrap around map.
        mMapView.enableWrapAround(true);

        // Create the Compass custom view, and add it onto the MapView.
        mCompass = new Compass(rootView.getContext(), null, mMapView);
        mMapView.addView(mCompass);

        mCat1 = new PictureMarkerSymbol(rootView.getContext().getApplicationContext(), ContextCompat.getDrawable(rootView.getContext().getApplicationContext(), R.drawable.pin_red));
        mCat2 = new PictureMarkerSymbol(rootView.getContext().getApplicationContext(), ContextCompat.getDrawable(rootView.getContext().getApplicationContext(), R.drawable.pin_green));
        mCat3 = new PictureMarkerSymbol(rootView.getContext().getApplicationContext(), ContextCompat.getDrawable(rootView.getContext().getApplicationContext(), R.drawable.pin_blue));
        mCat4 = new PictureMarkerSymbol(rootView.getContext().getApplicationContext(), ContextCompat.getDrawable(rootView.getContext().getApplicationContext(), R.drawable.pin_orange));
        mCat5 = new PictureMarkerSymbol(rootView.getContext().getApplicationContext(), ContextCompat.getDrawable(rootView.getContext().getApplicationContext(), R.drawable.pin_yellow));
        mAdd = new PictureMarkerSymbol(rootView.getContext().getApplicationContext(), ContextCompat.getDrawable(rootView.getContext().getApplicationContext(), R.drawable.pin_add));

        setupLocator();
        setupLocationListener();
        setHasOptionsMenu(true);

        return rootView;
    }

    public void getDataFromServer() {
        task = new CallWebPageTask();
        task.applicationContext = rootView.getContext();
        parameters = urls + "?rad=" + radius + "&lat=" + latitude + "&lng=" + longitude + "&cat=" + cat_id;
        Log.e("OK Connecting Sukses", "" + parameters);
        //Log.e("Sukses", parameters);
        task.execute(new String[]{parameters});
    }

    /**
     * When the map is long tapped, select the graphic at that location.
     */
    final OnLongPressListener mapLongPress = new OnLongPressListener() {
        @Override
        public boolean onLongPress(float v, float v1) {
            //Toast.makeText(rootView.getContext().getApplicationContext(), "this location at x= " + v + " and y= " + v1 + " | point " + onSingleTaps(v, v1) + "", Toast.LENGTH_SHORT).show();
            mResultsLayer.removeAll();
            Point point = onSingleTaps(v, v1);
            Location location = locationTouch;
            locationTouch.setLatitude(point.getY());
            locationTouch.setLongitude(point.getX());
            point = getAsPoint(locationTouch);
            Symbol symbol = mAdd;

            mResultsLayer.addGraphic(new Graphic(point, symbol));
            mMapView.setExtent(point, 100);
            placeLayout.setVisibility(View.GONE);
            return false;
        }
    };

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
                if (graphicIDs.length > 1){
                    int id = graphicIDs[0];
                    graphicIDs = new int[] { id };
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

                if (minView){
                    imageButton_minimaze.setVisibility(View.VISIBLE);
                    imageButton_maximaze.setVisibility(View.GONE);
                    placeLayout.setVisibility(View.VISIBLE);
                } else {
                    imageButton_minimaze.setVisibility(View.GONE);
                    imageButton_maximaze.setVisibility(View.VISIBLE);
                    placeLayout.setVisibility(View.GONE);
                }

                if (gr.getAttributes().isEmpty()) {
                    Toast.makeText(rootView.getContext().getApplicationContext(),"No Attribute for this location!",Toast.LENGTH_SHORT).show();
                }else {
                    updateContent(gr.getAttributes());
                }
            } else {
                placeLayout.setVisibility(View.GONE);
                imageButton_minimaze.setVisibility(View.GONE);
                imageButton_maximaze.setVisibility(View.GONE);
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

    /**
     * When user touches phone number, send this to the dialler using an intent.
     */
    /*final View.OnTouchListener callTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            String num = mPhoneTextView.getText().toString();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + num));
            startActivity(intent);
            return true;
        }
    };*/

    private void setupLocator() {
        // Parameterless constructor - uses the Esri world geocoding service.
        mLocator = Locator.createOnlineLocator();

        // Set up the outFields parameter for the search.
        mFindOutFields.add("id_loc");
        mFindOutFields.add("loc_name");
        mFindOutFields.add("loc_address");
        mFindOutFields.add("loc_promo");
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
//                        location.setLatitude(-6.246760);
//                        location.setLongitude(106.762618);
//                        Log.e("sukses location lat lng", "lat " + location.getLatitude() + " | lng " + location.getLongitude() + " | point " + getAsPoint(location));
                        // After zooming, turn on the Location pan mode to show the location
                        // symbol. This will disable as soon as you interact with the map.
                        if (!isFirst){
                            latitude = loc.getLatitude();
                            longitude = loc.getLongitude();
                        }

                        getDataFromServer();
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
     * @param loc  the location to center the MapView at
     */
    private void zoomToLocation(Location loc) {
        Point mapPoint = getAsPoint(loc);
        Unit mapUnit = mMapSr.getUnit();
        double zoomFactor = Unit.convertUnits(ZOOM_BY,
                Unit.create(LinearUnit.Code.MILE_US), mapUnit);
        Envelope zoomExtent = new Envelope(mapPoint, zoomFactor, zoomFactor);
        mMapView.setExtent(zoomExtent);
    }

    /**
     * Performs a find using the Locator, for a specific type of business.
     *
     * @param searchFor A string containing the type of business to search for.
     */
    private void doFindNearbyAsync(String searchFor) {
        final CallbackListener<List<LocatorGeocodeResult>> findCallback = new
                CallbackListener<List<LocatorGeocodeResult>>() {

                    @Override
                    public void onError(Throwable e) {
                        setProgressOnUIThread(false);

                        // Log the error
                        Log.e(TAG, "No Results Found");
                        Log.e(TAG, e.getMessage());
                        // Indicate to user we cannot show results in this area.
                        showToastOnUiThread("Error searching for results");
                    }

                    @Override
                    public void onCallback(List<LocatorGeocodeResult> results) {

                        // remove any previous graphics
                        mResultsLayer.removeAll();

                        // Use a Multipoint as a simple way to set total extent.
                        MultiPoint fullExtent = new MultiPoint();

                        if (results.size() > 0) {
                            // Set specific symbols and selection color for each type of search.
                            Symbol symbol = null;
                            if (mCurrentSearchType == SearchType.BAR) {
                                mResultsLayer.setSelectionColor(getResources().getColor(
                                        R.color.beer_selection));
                                symbol = mCat1;//mBarMapIcon;
                            } else if (mCurrentSearchType == SearchType.PIZZA) {
                                mResultsLayer.setSelectionColor(getResources().getColor(
                                        R.color.pizza_selection));
                                symbol = mCat2;//mPizzaMapIcon;
                            } else if (mCurrentSearchType == SearchType.COFFEE) {
                                mResultsLayer.setSelectionColor(getResources().getColor(
                                        R.color.coffee_selection));
                                symbol = mCat3;//mCoffeeMapIcon;
                            }

                            // For each result, create a Graphic, using result attributes as
                            // graphic attributes.
                            for (LocatorGeocodeResult result : results) {
                                Point resultPoint = result.getLocation();
                                HashMap<String, Object> attrMap = new
                                        HashMap<String, Object>(result.getAttributes());
                                mResultsLayer.addGraphic(new Graphic(resultPoint, symbol, attrMap));
                                Log.e("map point",""+resultPoint);
                                fullExtent.add(resultPoint);
                            }
                            // Zoom to the full extent
                            mMapView.setExtent(fullExtent, 100);
                        }
                        // Update the UI with the result information.
                        setResultCount(results.size(), mCurrentSearchType);
                    }
                };

        try {
            setProgressOnUIThread(true);

            // Get the current map extent.
            Envelope currExt = new Envelope();
            mMapView.getExtent().queryEnvelope(currExt);

            // Set up locator parameters based on the extent, search type, and the
            // outfields set previously.
            LocatorFindParameters fParams = new LocatorFindParameters(searchFor);
            fParams.setSearchExtent(currExt, mMapSr);
            fParams.setOutSR(mMapSr);
            fParams.setOutFields(mFindOutFields);

            // If LocationDisplayManger has a current location, set this to increase
            // priority and return a distance value in the results.
            if ((mLDM != null) && (mLDM.getLocation() != null)) {
                Point currentPoint = getAsPoint(mLDM.getLocation());
                fParams.setLocation(currentPoint, mMapSr);
            }

            // Call find, passing in the callback above.
            mLocator.find(fParams, findCallback);
        } catch (Exception e) {
            // Update UI and report any errors.
            setProgressOnUIThread(false);

            // Log the error
            Log.e(TAG, "No Results Found");
            Log.e(TAG, e.getMessage());

            // Indicate to user we cannot show results in this area.
            showToastOnUiThread("Error searching for results");
        }
    }

    private static String getRating() {
        // Randomized ratings could be replaced by a ratings service from a third
        // party.
        Random r = new Random();
        return String.valueOf(1 + (r.nextFloat() * 4));
    }

    /**
     * Update user interface with result set information. Ensure this can be
     * called from either background or UI thread by performing any actions on
     * Views within a runnable on the UI thread.
     *
     * @param resultCount  number of results in the result set
     * @param searchType  type of business searched for
     */
    private void setResultCount(int resultCount, SearchType searchType) {
        String searchTypeMessage = "";

        switch (searchType) {
            case COFFEE:
                searchTypeMessage = getResources().getString(R.string.results_coffee);
                break;
            case PIZZA:
                searchTypeMessage = getResources().getString(R.string.results_pizza);
                break;
            case BAR:
                searchTypeMessage = getResources().getString(R.string.results_bar);
                break;
        }

        final String message = String.format("Found %d %s", resultCount,
                searchTypeMessage);

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                /*mProgress.setIndeterminate(false);
                mTitleTextView.setText(message);
                mAddressTextView.setText("");
                mPhoneTextView.setText("");
                mPhoneImageView.setImageDrawable(null);
                mDistanceTextView.setText("");
                mRatingBar.setVisibility(View.GONE);*/
            }
        });
    }

    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater ) {
        inflater.inflate(R.menu.menu_layout, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.bar:
                if (mMapView.isLoaded()) {
                    clearCurrentResults();
                    mCurrentSearchType = SearchType.BAR;
                    try {
                        doFindNearbyAsync(getResources().getString(R.string.bar_query));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                return true;

            case R.id.pizza:
                if (mMapView.isLoaded()) {
                    mCurrentSearchType = SearchType.PIZZA;
                    try {
                        doFindNearbyAsync(getResources().getString(R.string.pizza_query));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return true;

            case R.id.coffee:
                if (mMapView.isLoaded()) {
                    mCurrentSearchType = SearchType.COFFEE;
                    try {
                        doFindNearbyAsync(getResources().getString(R.string.coffee_query));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                return true;
*/
            case R.id.locate:
                if (mMapView.isLoaded()) {
                    // If LocationDisplayManager has a fix, pan to that location. If no
                    // fix yet, this will happen when the first fix arrives, due to
                    // callback set up previously.
                    if ((mLDM != null) && (mLDM.getLocation() != null)) {
                        // Keep current scale and go to current location, if there is one.
                        mLDM.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);
                    }
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void clearCurrentResults() {
        if (mResultsLayer != null) {
            mResultsLayer.removeAll();
        }
        textView_id_loc.setText("");
        textView_loc_name.setText("");
        textView_loc_address.setText("");
        imageView_pic.setImageDrawable(null);
        textView_loc_promo.setText("");
        textView_loc_idstance.setText("");
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
        textView_loc_idstance.setText(""+formatNumber.changeFormatNumber(meters)+" Km");

        int promo = (int) attributes.get("loc_promo");
        if (promo == 1) textView_loc_promo.setText("Promo");
        else textView_loc_promo.setText("-");


/*        String phone = attributes.get(getResources().getString(
                R.string.result_phone)).toString();
        mPhoneTextView.setText(phone);
        mPhoneImageView.setImageDrawable(ContextCompat.getDrawable(rootView.getContext().getApplicationContext(), R.drawable.ic_action_call));

        Float rating = Float.parseFloat(getRating());
        mRatingBar.setRating(rating);
        mRatingBar.setVisibility(View.VISIBLE);*/
    }

    public void showToastOnUiThread(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                //Toast.makeText(rootView.getContext().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setProgressOnUIThread(final boolean isIndeterminate) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                //mProgress.setIndeterminate(isIndeterminate);
            }
        });
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
                id_loc = new int[menuItemArray.length()];
                loc_name = new String[menuItemArray.length()];
                loc_address = new String[menuItemArray.length()];
                loc_promo = new int[menuItemArray.length()];
                loc_distance = new double[menuItemArray.length()];
                loc_pic = new String[menuItemArray.length()];
                loc_lat = new double[menuItemArray.length()];
                loc_lng = new double[menuItemArray.length()];
                Log.e("Data dari server",""+menuItemArray);
                for (int i = 0; i < menuItemArray.length(); i++) {
                    id_loc[i]=menuItemArray.getJSONObject(i).getInt("id_location");
                    loc_name[i]=menuItemArray.getJSONObject(i).getString("location_name").toString();
                    loc_address[i]=menuItemArray.getJSONObject(i).getString("location_address").toString();
                    loc_promo[i]=menuItemArray.getJSONObject(i).getInt("isPromo");
                    loc_distance[i]= Double.parseDouble(formatNumber.changeFormatNumber(menuItemArray.getJSONObject(i).getDouble("distance")));
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
            updateData();
            /*if (isFirst){
                this.dialog.cancel();
                isFirst=false;
                updateData();
            } else {
                updateData();
            }*/
        }
    }

    public void updateData(){
        MultiPoint fullExtent = new MultiPoint();
        mResultsLayer.removeAll();
        Symbol symbol = null;
        //-6.21267000, 106.61778566
        Map<String, Object> attr = new HashMap<String, Object>();
        for (int i = 0; i <id_loc.length ; i++) {
            Location locationPin = location;
            locationPin.setLatitude(loc_lat[i]);
            locationPin.setLongitude(loc_lng[i]);
            Point point = getAsPoint(locationPin);
            attr.put("id_loc", id_loc[i]);
            attr.put("loc_name", loc_name[i]);
            attr.put("loc_address", loc_address[i]);
            attr.put("loc_promo", loc_promo[i]);
            attr.put("loc_distance", loc_distance[i]);
            attr.put("loc_pic", loc_pic[i]);
            attr.put("loc_lat", loc_lat[i]);
            attr.put("loc_lng", loc_lng[i]);

            Log.e("Ok sip", "this location at lat= " + loc_lat[i] + " and lng= " + loc_lng[i] + " | point on getAsPoint" + point + "");
            if (cat_id==1) symbol = mCat1;
            else if (cat_id==2) symbol = mCat2;
            else if (cat_id==3) symbol = mCat3;
            else if (cat_id==4) symbol = mCat4;
            else symbol = mCat5;

            mResultsLayer.addGraphic(new Graphic(point, symbol,attr));
            fullExtent.add(point);
        }
        mMapView.setExtent(fullExtent, 100);
        if (id_loc.length<2){
            if ((mLDM != null) && (mLDM.getLocation() != null)) {
                // Keep current scale and go to current location, if there is one.
                zoomToLocation(location);
                mLDM.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);
            }
        }
        placeLayout.setVisibility(View.GONE);
        imageButton_maximaze.setVisibility(View.GONE);
        imageButton_minimaze.setVisibility(View.GONE);
    }
}
