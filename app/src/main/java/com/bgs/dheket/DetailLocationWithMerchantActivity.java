package com.bgs.dheket;

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
import com.bgs.common.Utility;
import com.bgs.imageOrView.ViewPagerAdapter;
import com.bgs.model.Category;
import com.bgs.model.Lokasi;
import com.bgs.model.Merchant;
import com.bgs.networkAndSensor.HttpGetOrPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by SND on 28/03/2016.
 */
public class DetailLocationWithMerchantActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    android.support.v7.app.ActionBar actionBar;

    String url = "",responseServer="";
    private JSONObject JsonObject, jsonobject;
    ArrayList<HashMap<String, String>> arraylist;

    //store location detail sent via intent
    private Lokasi lokasi;
    private Location currentBestLocation;
    //double radius, latitude, longitude;
    //int cat_id, id_loc;
    //String email, kategori, icon;

    Utility formatNumber = new Utility();

    //private Category category;

    TextView textView_namaloc, textView_alamatloc, textView_distanceloc, textView_descriptionloc,
            textView_simpledescloc, textView_pricepromo, textView_gochat;

    //private ViewPager intro_images;
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
        setContentView(R.layout.activity_detail_loc_with_merchant);

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
        gotoLayout = (LinearLayout)findViewById(R.id.linearLayout_dl_wm_loc_chat);
        textView_namaloc = (TextView)findViewById(R.id.textView_dl_wm_loc_name);
        textView_alamatloc = (TextView)findViewById(R.id.textView_dl_wm_loc_address);
        textView_distanceloc = (TextView)findViewById(R.id.textView_dl_wm_loc_distance);
        textView_descriptionloc = (TextView)findViewById(R.id.textView_dl_wm_loc_description);
        textView_simpledescloc = (TextView)findViewById(R.id.textView_dl_wm_loc_simple_description);
        textView_pricepromo = (TextView)findViewById(R.id.textView_dl_wm_loc_pricepromo);
        textView_gochat = (TextView)findViewById(R.id.textView_dl_wm_loc_gochat);
        textView_gochat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(Constants.TAG, "GOTO CHAT");

            }
        });
        //intro_images = (ViewPager) findViewById(R.id.viewPager_dl_wm_loc_logo);
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
        Intent gotoMapSingle = new Intent(getApplicationContext(),MapViewSingleActivity.class);

        gotoMapSingle.putExtra("lokasi", lokasi);
        gotoMapSingle.putExtra("currentBestLocation", currentBestLocation);
        /*
        Bundle paket = new Bundle();
        Category category = lokasi.getCategory();
        paket.putInt("cat_id", category.getId());
        paket.putString("kategori", category.getName());
        paket.putDouble("radius", category.getRadius());
        paket.putDouble("latitude", lokasi.getLatitude());
        paket.putDouble("longitude", lokasi.getLongitude());
        paket.putString("icon", category.getIcon());
        paket.putInt("location_id", lokasi.getId());
        //paket.putInt("location_id", Integer.parseInt(arraylist.get(0).get("loc_id")));
        gotoMapSingle.putExtras(paket);
        */
        startActivity(gotoMapSingle);
        finish();
    }

    public void back_to_previous_screen(){
        Intent intent = new Intent(getApplicationContext(),MapViewWithListActivity.class);

        Bundle paket = new Bundle();
        /*
        if (kategori.equalsIgnoreCase(" ")){
            intent = new Intent(getApplicationContext(),MapViewActivity.class);
            paket.putString("email",email);
            paket.putStringArray("icon", icon_cat);
            paket.putIntArray("id_cat",id_cat);
        } else {
            paket.putString("icon", icon);
        }
        */

        Category category = lokasi.getCategory();
        if (" ".equalsIgnoreCase(category.getName())){
            intent = new Intent(getApplicationContext(),MapViewActivity.class);
            //paket.putString("email",category.getEmail());
            paket.putStringArray("icon", icon_cat);
            paket.putIntArray("id_cat",id_cat);
        } else {
            paket.putString("icon", category.getIcon());
        }
        /*
        paket.putInt("cat_id", category.getId());
        paket.putString("kategori", category.getName());
        paket.putDouble("radius", category.getRadius());
        paket.putDouble("latitude", lokasi.getLatitude());
        paket.putDouble("longitude", lokasi.getLongitude());
        intent.putExtras(paket);
        */

        intent.putExtra("category", category);
        intent.putExtra("currentBestLocation", currentBestLocation);
        startActivity(intent);
        finish();
    }

    public void getDetailLocation() {
        CallWebPageTask task = new CallWebPageTask(this);
        Category category = lokasi.getCategory();
        String urls = url + "/" + lokasi.getLatitude() + "/" + lokasi.getLongitude() + "/" + lokasi.getId();
        Log.e(Constants.TAG, "Get Detail Lokasi url => " + urls);
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
        private ProgressDialog dialog;

        public CallWebPageTask(Context context) {
            this.context = context;
            this.dialog = new ProgressDialog(context);
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
            arraylist = new ArrayList<HashMap<String, String>>();
            try {
                HashMap<String, String> map = new HashMap<String,String>();
                JSONArray menuItemArray = null;
                JsonObject = new JSONObject(response);
                menuItemArray = JsonObject.getJSONArray("dheket_singleLoc");
                
                for (int i = 0; i < menuItemArray.length(); i++) {
                    map = new HashMap<String, String>();
                    jsonobject = menuItemArray.getJSONObject(i);
                    Log.e(Constants.TAG, "Lokasi Detail ->" + jsonobject.toString());
                    map.put("loc_id", jsonobject.getString("id_location"));
                    map.put("loc_name", jsonobject.getString("location_name"));
                    map.put("loc_address", jsonobject.getString("location_address"));
                    map.put("loc_latitude", jsonobject.getString("latitude"));
                    map.put("loc_longitude", jsonobject.getString("longitude"));
                    map.put("loc_cat_id", jsonobject.getString("category_id"));
                    map.put("loc_phone", jsonobject.getString("phone"));
                    map.put("loc_ispromo", jsonobject.getString("isPromo"));
                    //map.put("loc_photo", jsonobject.getString("photo"));
                    map.put("loc_here_id", jsonobject.getString("id_location_here"));
                    map.put("loc_description", jsonobject.getString("description"));
                    map.put("loc_tag", jsonobject.getString("location_tag"));
                    map.put("loc_distance", jsonobject.getString("distance"));

                    // Set the JSON Objects into the array
                    arraylist.add(map);
                }
            } catch (JSONException e) {
                Log.e(Constants.TAG, e.getMessage(), e);
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            updateData();
            if ( this.dialog.isShowing())
                this.dialog.dismiss();
        }
    }

    public void updateData() {
        Log.e(Constants.TAG, "size arraylist => " + arraylist.size());
        if (arraylist.size() > 0){
            //setReference();

            textView_namaloc.setText(arraylist.get(0).get("loc_name"));
            actionBar.setTitle(textView_namaloc.getText());
            textView_alamatloc.setText("@"+arraylist.get(0).get("loc_address"));
            double distance = Double.parseDouble(formatNumber.changeFormatNumber(Double.parseDouble(arraylist.get(0).get("loc_distance").toString())));
            int formatDistance = 0;
            if (distance < 1){
                formatDistance = (int) (distance*1000);
                textView_distanceloc.setText(""+formatDistance+" M");
            } else {
                textView_distanceloc.setText(""+distance+" Km");
            }

            textView_descriptionloc.setText(arraylist.get(0).get("loc_description"));
            //textView_simpledescloc = (TextView)findViewById(R.id.textView_dl_nm_loc_simple_description);
            //textView_pricepromo = (TextView)findViewById(R.id.textView_dl_nm_loc_pricepromo);
            Merchant merchant = lokasi.getMerchant();
            String name = merchant != null ? merchant.getUserName() : "";
            textView_gochat.setText("CHAT TO MERCHANT " + name);
        }

    }

    public void setReference() {
        mAdapter = new ViewPagerAdapter(getApplicationContext(), arraylist_foto);
        Log.e(Constants.TAG, "foto => "+arraylist_foto.toString());
        /*
        intro_images.setAdapter(mAdapter);
        intro_images.setCurrentItem(0);
        intro_images.setOnPageChangeListener(this);
        intro_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        */
        setUiPageViewController();

        NUM_PAGES = arraylist_foto.length;

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                //intro_images.setCurrentItem(currentPage++, true);
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
