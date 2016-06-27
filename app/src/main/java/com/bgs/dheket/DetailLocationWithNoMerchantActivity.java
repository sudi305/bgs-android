package com.bgs.dheket;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bgs.common.Constants;
import com.bgs.common.DialogUtils;
import com.bgs.common.ExtraParamConstants;
import com.bgs.common.Utility;
import com.bgs.imageOrView.ViewPagerAdapter;
import com.bgs.model.Category;
import com.bgs.model.Lokasi;
import com.bgs.networkAndSensor.HttpGetOrPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.internal.Util;

/**
 * Created by SND on 28/03/2016.
 */
public class DetailLocationWithNoMerchantActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    android.support.v7.app.ActionBar actionBar;

    String url = "",responseServer="";
    private JSONObject JsonObject, jsonobject;

    //store location detail sent via intent
    private Lokasi lokasi;
    private Location currentBestLocation;
    //double radius, latitude, longitude;
    //int cat_id, id_loc;
    //String email, kategori, icon;

    Utility formatNumber = new Utility();

    //private Category category;

    TextView textView_namaloc, textView_alamatloc, textView_distanceloc, textView_descriptionloc,
             textView_simpledescloc, textView_pricepromo, textView_gotoloc;

    private ViewPager intro_images;
    private LinearLayout pager_indicator;
    private RelativeLayout topLayout;
    private LinearLayout gotoLayout;
    private int dotsCount;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ImageView[] dots;
    private ViewPagerAdapter mAdapter;

    int[] arraylist_foto = new int[] {R.drawable.default_placeholder};
    String[] icon_cat;
    int[] id_cat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_loc_no_merchant);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.d_ic_back));
        //actionBar.setHomeAsUpIndicator(R.drawable.logo);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Detail Location");
//        actionBar.setSubtitle(Html.fromHtml("<font color='#FFBF00'>Location in Radius " + formatter.format(radius) + " Km</font>"));

        lokasi = getIntent().getParcelableExtra("lokasi");
        currentBestLocation = getIntent().getParcelableExtra("currentBestLocation");

        if (" ".equalsIgnoreCase(lokasi.getCategory().getName())){
            //icon_cat = lokasi.getCategory().getIcon();
            //id_cat = paket.getIntArray("id_cat");
        } else {
            //icon = paket.getString("icon");
        }

        url = String.format(getResources().getString(R.string.link_getSingleLocationById));

        topLayout = (RelativeLayout)findViewById(R.id.top_layout);
        gotoLayout = (LinearLayout)findViewById(R.id.linearLayout_dl_nm_loc_goto);
        textView_namaloc = (TextView)findViewById(R.id.textView_dl_nm_loc_name);
        textView_alamatloc = (TextView)findViewById(R.id.textView_dl_nm_loc_address);
        textView_distanceloc = (TextView)findViewById(R.id.textView_dl_nm_loc_distance);
        textView_descriptionloc = (TextView)findViewById(R.id.textView_dl_nm_loc_description);
        textView_simpledescloc = (TextView)findViewById(R.id.textView_dl_nm_loc_simple_description);
        textView_pricepromo = (TextView)findViewById(R.id.textView_dl_nm_loc_pricepromo);
        textView_gotoloc = (TextView)findViewById(R.id.textView_dl_nm_loc_goto);
        intro_images = (ViewPager) findViewById(R.id.viewPager_dl_nm_loc_logo);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);

        getDetailLocation();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        getMenuInflater().inflate(R.menu.menu_detail_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            back_to_previous_screen();
            return super.onOptionsItemSelected(item);
        }

        if (item.getItemId() == R.id.select_map){
            toMapScreen();
            return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        back_to_previous_screen();
    }

    public void toMapScreen(){
        MapViewSingleActivity.startFromLocationNoMerchant(getApplicationContext(), lokasi, currentBestLocation);
        /*
        //paket.putInt("location_id", Integer.parseInt(arraylist.get(0).get("loc_id")));
        */
        finish();
    }

    public void back_to_previous_screen(){
        Intent intent = new Intent(this, MapViewWithListActivity.class);


        Category category = lokasi.getCategory();
        if (" ".equalsIgnoreCase(category.getName())){
            intent = new Intent(this,MapViewActivity.class);
            //paket.putString("email",category.getEmail());
            //paket.putStringArray("icon", icon_cat);
            //paket.putIntArray("id_cat",id_cat);
        } else {
            //paket.putString("icon", category.getIcon());
        }

        intent.putExtra(ExtraParamConstants.CATEGORY, category);
        intent.putExtra(ExtraParamConstants.CURRNET_BEST_LOCATION, currentBestLocation);
        startActivity(intent);
        finish();
    }

    public void getDetailLocation() {
        CallWebPageTask task = new CallWebPageTask(this);
        double latitude =0, longitude = 0;
        if ( currentBestLocation != null) {
            latitude = currentBestLocation.getLatitude();
            longitude = currentBestLocation.getLongitude();
        }
        String urls = url + "/" + latitude + "/" + longitude + "/" + lokasi.getId();
        Log.d(Constants.TAG, "Get Detail Lokasi url => " + urls);
        task.execute(new String[]{urls});
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));

        if (position + 1 == dotsCount) {
            /*btnNext.setVisibility(View.GONE);
            btnFinish.setVisibility(View.VISIBLE);*/
        } else {
            /*btnNext.setVisibility(View.VISIBLE);
            btnFinish.setVisibility(View.GONE);*/
        }
        currentPage = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class CallWebPageTask extends AsyncTask<String, Void, String> {
        protected Context context;
        private Dialog dialog;
        private Lokasi lokasiDetail;

        public CallWebPageTask(Context context) {
            this.context = context;
            this.dialog = DialogUtils.LoadingSpinner(context);
        }

        @Override
        protected void onPreExecute() {
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpGetOrPost httpGetOrPost = new HttpGetOrPost();
            response = httpGetOrPost.getRequest(urls[0]);
            try {
                HashMap<String, String> map = new HashMap<String,String>();
                JSONObject joResponse = new JSONObject(response);
                JSONArray joArrayLokasi = joResponse.getJSONArray("dheket_singleLoc");
                //jika banyak data cuma diambil 1
                final JSONObject data =  joArrayLokasi.getJSONObject(0);
                Log.d(Constants.TAG, "Lokasi Detail ->" + data.toString());
                lokasiDetail = new Lokasi();
                lokasiDetail.setId(Integer.parseInt(data.getString("id_location")));
                lokasiDetail.setName(data.getString("location_name"));
                lokasiDetail.setAddress(data.getString("location_address"));
                lokasiDetail.setLatitude(Double.parseDouble(data.getString("latitude")));
                lokasiDetail.setLongitude(Double.parseDouble(data.getString("longitude")));

                //ambil categori dari lokasi yang dikirim vai intent
                Category category = lokasi.getCategory();
                lokasiDetail.setCategory(category);

                lokasiDetail.setPhone(data.getString("phone"));
                lokasiDetail.setIsPromo( Integer.parseInt(data.getString("isPromo")));
                //map.put("loc_photo", jsonobject.getString("photo"));
                if ( !data.isNull("id_location_here"))
                    lokasiDetail.setIdLocationHere(data.getString("id_location_here"));

                lokasiDetail.setDescription(data.getString("description"));
                lokasiDetail.setLocationTag(data.getString("location_tag"));
                lokasiDetail.setDistance(Double.parseDouble(data.getString("distance")));
            } catch (JSONException e) {
                Log.e(Constants.TAG, e.getMessage(), e);
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            updateData(lokasiDetail);

            if ( this.dialog.isShowing())
                this.dialog.dismiss();
        }
    }

    public void updateData(Lokasi lokasiDetail) {
        Log.d(Constants.TAG, "lokasiDetail => " + lokasiDetail);
        if (lokasiDetail != null){
            setReference();

            textView_namaloc.setText(lokasiDetail.getName());
            actionBar.setTitle(textView_namaloc.getText());
            textView_alamatloc.setText("@"+lokasiDetail.getAddress());
            Log.d(Constants.TAG, "D1 => " + lokasiDetail.getDistance());
            Log.d(Constants.TAG, "D2 => " + Utility.changeFormatNumber(lokasiDetail.getDistance()));
            double distance = lokasiDetail.getDistance();
            textView_distanceloc.setText(Utility.andjustDistanceUnit(distance));

            textView_descriptionloc.setText(lokasiDetail.getDescription());
            //textView_simpledescloc = (TextView)findViewById(R.id.textView_dl_nm_loc_simple_description);
            //textView_pricepromo = (TextView)findViewById(R.id.textView_dl_nm_loc_pricepromo);
            textView_gotoloc.setText("GO TO " + lokasiDetail.getName().toUpperCase()+"  ");
        }

    }

    public void setReference() {
        mAdapter = new ViewPagerAdapter(getApplicationContext(), arraylist_foto);
        Log.d(Constants.TAG, "foto => "+arraylist_foto.toString());
        intro_images.setAdapter(mAdapter);
        intro_images.setCurrentItem(0);
        intro_images.setOnPageChangeListener(this);
        intro_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        setUiPageViewController();

        NUM_PAGES = arraylist_foto.length;

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                intro_images.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);
    }

    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }
}
