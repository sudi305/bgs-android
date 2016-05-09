package com.bgs.dheket;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bgs.common.Utility;
import com.bgs.flowLayout.CategoryActivity;
import com.bgs.imageOrView.MySeekBar;
import com.bgs.networkAndSensor.ConfigInternetAndGPS;
import com.bgs.networkAndSensor.HttpGetOrPost;
import com.bgs.imageOrView.ProfilePictureView_viaFB;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SND on 20/01/2016.
 */

public class MainMenuActivity extends AppCompatActivity implements LocationListener, NavigationView.OnNavigationItemSelectedListener  {

    LinearLayout buble_cat1, buble_cat2, buble_cat3, buble_cat4, buble_cat5;
    Button btn_buble_cat1, btn_buble_cat2, btn_buble_cat3, btn_buble_cat4, btn_buble_cat5, btn_search;
    TextView txt_tot_cat1, txt_tot_cat2, txt_tot_cat3, txt_tot_cat4, txt_tot_cat5;
    TextView txt_promo_cat1, txt_promo_cat2, txt_promo_cat3, txt_promo_cat4, txt_promo_cat5, txt_mapView;
    ImageView imVi_usrPro;
    ProfilePictureView_viaFB view_usrPro;
    Menu menu;
    CallbackManager callbackManager;
    //Dialog details_dialog;
    TextView details_txt, textView_usrNm, textView_usrEmail;
    ImageButton imageButton_close;
    ConfigInternetAndGPS checkInternetGPS;
    HttpGetOrPost httpGetOrPost;
    String responseServer="";

    LocationManager myLocationManager;
    Criteria criteria;
    String provider;
    Location location;

    private JSONObject jObject;
    private String jsonResult = "";
    int[] promo = new int[5], lokasi = new int[5], id_kategori = new int[5];
    String[] nama_katagori = new String[5], icon_kategori = new String[5];
    double real_radius = 0.0;
    double radius = 0.0;
    int newRadius = 0;
    double latitude, longitude;
    String url = "";
    String detailUser,email;
    boolean first_check = true;
    android.support.v7.app.ActionBar actionBar;
    float scale;

    Intent goToScreen;
    Utility formatNumber = new Utility();

    private MySeekBar bar, bartext;
    private TextView textViewRad, textViewLoad;
    Button textViewMore;
    ViewGroup layoutRadiusSlider;
    LinearLayout formRadius,formRadiusBackground;
    RelativeLayout rl;
    RelativeLayout.LayoutParams p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeAsUpIndicator(R.drawable.logo);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Dheket");
//        actionBar.setSubtitle(Html.fromHtml("<font color='#FFBF00'>Location in Radius " + formatter.format(radius) + " Km</font>"));
        actionBar.setSubtitle(Html.fromHtml("<font color='#ff9800' size='10'>Radius " + formatNumber.changeFormatNumber(radius) + " Km</font>"));

        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();*/

        /*NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/

        FacebookSdk.sdkInitialize(getApplicationContext());

        //url = String.format(getResources().getString(R.string.link_cekUserLogin));
        url = String.format(getResources().getString(R.string.link_getDataUser));

        checkInternetGPS = new ConfigInternetAndGPS(getApplicationContext());

        scale = getResources().getDisplayMetrics().density;

        buble_cat1 = (LinearLayout) findViewById(R.id.buble_cat1);
        buble_cat2 = (LinearLayout) findViewById(R.id.buble_cat2);
        buble_cat3 = (LinearLayout) findViewById(R.id.buble_cat3);
        buble_cat4 = (LinearLayout) findViewById(R.id.buble_cat4);
        buble_cat5 = (LinearLayout) findViewById(R.id.buble_cat5);
        btn_buble_cat1 = (Button) findViewById(R.id.btn_buble_cat1);
        btn_buble_cat2 = (Button) findViewById(R.id.btn_buble_cat2);
        btn_buble_cat3 = (Button) findViewById(R.id.btn_buble_cat3);
        btn_buble_cat4 = (Button) findViewById(R.id.btn_buble_cat4);
        btn_buble_cat5 = (Button) findViewById(R.id.btn_buble_cat5);
        btn_search = (Button) findViewById(R.id.btn_search);
        imVi_usrPro = (ImageView) findViewById(R.id.imageView_userProfile);
        txt_promo_cat1 = (TextView) findViewById(R.id.textView_promo_cat1);
        txt_promo_cat2 = (TextView) findViewById(R.id.textView_promo_cat2);
        txt_promo_cat3 = (TextView) findViewById(R.id.textView_promo_cat3);
        txt_promo_cat4 = (TextView) findViewById(R.id.textView_promo_cat4);
        txt_promo_cat5 = (TextView) findViewById(R.id.textView_promo_cat5);
        txt_tot_cat1 = (TextView) findViewById(R.id.textView_total_cat1);
        txt_tot_cat2 = (TextView) findViewById(R.id.textView_total_cat2);
        txt_tot_cat3 = (TextView) findViewById(R.id.textView_total_cat3);
        txt_tot_cat4 = (TextView) findViewById(R.id.textView_total_cat4);
        txt_tot_cat5 = (TextView) findViewById(R.id.textView_total_cat5);
        txt_mapView = (TextView) findViewById(R.id.textView_MapView);
        view_usrPro = (ProfilePictureView_viaFB) findViewById(R.id.view_userProfile);
        textView_usrNm = (TextView) findViewById(R.id.textView_usrNm);
        textViewLoad = (TextView)findViewById(R.id.textView_cm_load);

        rl = (RelativeLayout)findViewById(R.id.rl_main_menu_bubble);

        initFormSettingRadius();
        updateData();
        preProcessingGetData();
        getServiceFromGPS();

        final Animation animTranslate = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
        final Animation animScale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        final Animation animButtonPress = AnimationUtils.loadAnimation(this, R.anim.anim_scale_button_press);

        imVi_usrPro.setAnimation(animScale);
        buble_cat1.setAnimation(animTranslate);
        buble_cat2.setAnimation(animTranslate);
        buble_cat3.setAnimation(animTranslate);
        buble_cat4.setAnimation(animTranslate);
        buble_cat5.setAnimation(animTranslate);

//        FragmentManager manager = getSupportFragmentManager(); // or getFragmentManager, depends on which api lvl you are working on but supportFragmentManager will make you dialog work also on devices lower than api lvl 11(3.0 - > Honeycomb)
//        DialogFragment Dialog = Dialogs.newInstance(1);
//        Dialog.show(getFragmentManager(), "tag");

        if (AccessToken.getCurrentAccessToken() != null) {
            Log.e("getdatafrom fb","yes");
            RequestDataFromFB();
        }

        view_usrPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_error_red_24dp));
                Log.e("menu size",""+menu.size());
                menu.getItem(2).setVisible(false);*/
                showDialogDetail();
            }
        });

        btn_buble_cat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Category 1 ",""+id_kategori[0]+ "radius "+real_radius + "icon "+icon_kategori[0] + "lokasi "+lokasi[0]);
                toListAndMapScreen(id_kategori[0], real_radius, nama_katagori[0], icon_kategori[0], lokasi[0]);
            }
        });

        btn_buble_cat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Category 2 ",""+id_kategori[1]+ "radius "+real_radius + "icon "+icon_kategori[1] + "lokasi "+lokasi[1]);
                toListAndMapScreen(id_kategori[1], real_radius, nama_katagori[1],icon_kategori[1],lokasi[1]);
            }
        });

        btn_buble_cat3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Category 3 ",""+id_kategori[2]+ "radius "+real_radius + "icon "+icon_kategori[2] + "lokasi "+lokasi[2]);
                toListAndMapScreen(id_kategori[2], real_radius, nama_katagori[2],icon_kategori[2],lokasi[2]);
            }
        });

        btn_buble_cat4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Category 4 ",""+id_kategori[4]+ "radius "+real_radius + "icon "+icon_kategori[4] + "lokasi "+lokasi[4]);
                toListAndMapScreen(id_kategori[3], real_radius, nama_katagori[3],icon_kategori[3],lokasi[3]);
            }
        });

        btn_buble_cat5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Category 5 ",""+id_kategori[4]+ "radius "+real_radius + "icon "+icon_kategori[4] + "lokasi "+lokasi[4]);
                toListAndMapScreen(id_kategori[4], real_radius, nama_katagori[4],icon_kategori[4],lokasi[4]);
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*btn_search.setAnimation(animButtonPress);
                Intent toSearch = new Intent(getApplicationContext(), SearchAllCategoryActivity.class);
                myLocationManager.removeUpdates(MainMenuActivity.this);
                myLocationManager = null;
                startActivity(toSearch);
                finish();*/
                showDialog(MainMenuActivity.this, v.getLeft() - (v.getWidth() * 2),
                        v.getTop() + (v.getHeight() * 2));
            }
        });

        txt_mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radius!=0) {
                    Intent toMap = new Intent(getApplicationContext(),MapViewActivity.class);
                    Bundle paket = new Bundle();
                    paket.putString("email",email);
                    paket.putDouble("latitude", latitude);
                    paket.putDouble("longitude", longitude);
                    paket.putStringArray("icon", icon_kategori);
                    paket.putIntArray("id_cat",id_kategori);
                    paket.putDouble("radius", radius);
                    toMap.putExtras(paket);
                    startActivity(toMap);
                    finish();
                }
            }
        });
    }

    public void preProcessingGetData(){
        actionBar.setSubtitle(Html.fromHtml("<font color='#FFBF00'>Location in Radius ... </font>"));
        txt_tot_cat1.setText("...");
        btn_buble_cat1.setText("...");
        txt_promo_cat1.setText("Promo: ...");

        txt_tot_cat2.setText("...");
        btn_buble_cat2.setText("...");
        txt_promo_cat2.setText("Promo: ...");

        txt_tot_cat3.setText("...");
        btn_buble_cat3.setText("...");
        txt_promo_cat3.setText("Promo: ...");

        txt_tot_cat4.setText("...");
        btn_buble_cat4.setText("...");
        txt_promo_cat4.setText("Promo: ...");

        txt_tot_cat5.setText("...");
        btn_buble_cat5.setText("...");
        txt_promo_cat5.setText("Promo: ...");
    }

    public void initFormSettingRadius(){
        formRadius = (LinearLayout)findViewById(R.id.layout_main_slider_seekbar);
        formRadiusBackground = (LinearLayout)findViewById(R.id.linearLayout_form_lms_slider);
        ViewGroup.LayoutParams params = formRadius.getLayoutParams();
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;

        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        params.height = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight()
                -actionBarHeight-getStatusBarHeight();
        formRadius.setLayoutParams(params);
        bar = (MySeekBar)findViewById(R.id.seekBar_lms_radius);
        bartext = (MySeekBar)findViewById(R.id.seekBar);
        bartext.setEnabled(false);
        textViewRad = (TextView) findViewById(R.id.textView_lms_rad);
        textViewMore = (Button) findViewById(R.id.button_lms_more);
        //layoutRadiusSlider = (ViewGroup)findViewById(R.id.relativeLayout_seekbar);
        bar.setMax(9);
        bar.setProgress((int) (radius - 1));
        bartext.setMax(9);
        bartext.setProgress((int) (radius - 1));
        textViewRad.setText(String.valueOf(bar.getProgress() + 1) + " Km");
        bartext.setThumb(writeOnDrawable(R.drawable.transparant, String.valueOf(bar.getProgress() + 1) + " Km"));
        p = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        p.addRule(RelativeLayout.ABOVE, bar.getId());
        int xpos = bar.getLayoutParams().width - ((bar.getLayoutParams().width/bar.getMax())*(bar.getMax()-bar.getProgress()));
        p.setMargins(xpos - (textViewRad.getWidth() / (int) (2 * scale + 0.5f)), 0, 0, 0);
        textViewRad.setLayoutParams(p);
        /*bar.setThumb(getApplicationContext().getResources().getDrawable(
                R.drawable.menu_info));*/
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                //textView.setVisibility(View.INVISIBLE);
                AsyncTAddingDataToServer asyncT = new AsyncTAddingDataToServer();
                asyncT.execute();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                Rect thumbRect = bar.getThumb().getBounds();
                //Toast.makeText(getApplicationContext(),"position "+thumbRect.centerX(),Toast.LENGTH_SHORT).show();
                p = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                p.addRule(RelativeLayout.ABOVE, bar.getId());
                p.setMargins((int) (thumbRect.centerX() - (textViewRad.getWidth() / (5 * scale + 0.5f))), 0, 0, 0);
                textViewRad.setLayoutParams(p);
                textViewRad.setText(String.valueOf(progress + 1) + " Km");
                bartext.setProgress(progress);
                bartext.setThumb(writeOnDrawable(R.drawable.transparant, String.valueOf(bar.getProgress() + 1) + " Km"));
                newRadius = bar.getProgress()+1;
                //textView.setVisibility(View.VISIBLE);
                //final Animation animationFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
                //textView.startAnimation(animationFadeOut);
            }
        });
        textViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSetting();
            }
        });
        formRadius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formRadius.setVisibility(View.GONE);
            }
        });
        formRadiusBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public BitmapDrawable writeOnDrawable(int drawableId, String text){
        float scale = getResources().getDisplayMetrics().density;
        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(45 * scale + 0.5f);
        paint.setTextAlign(Paint.Align.CENTER);

        Canvas canvas = new Canvas(bm);
        canvas.drawText(text, bm.getWidth() / 2, (bm.getHeight() / 3), paint);

        return new BitmapDrawable(bm);
    }

    public void showDialogDetail() {
        LayoutInflater mInflater = LayoutInflater.from(this);
        View v = mInflater.inflate(R.layout.dialog_details, null);

        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(v);
        dialog.setCancelable(true);

        details_txt = (TextView) v.findViewById(R.id.details);
        details_txt.setText(Html.fromHtml(detailUser));
        imageButton_close = (ImageButton) v.findViewById(R.id.imageButton_close_dialog);
        imageButton_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void toListAndMapScreen(int cat_id, double radius, String kategori, String icon,int lokasi) {
        if (lokasi!=0){
            //goToScreen = new Intent(getApplicationContext(), ListAndMapAllLocActivity.class);
            goToScreen = new Intent(getApplicationContext(), MapViewWithListActivity.class);
            Bundle paket = new Bundle();
            paket.putInt("cat_id", cat_id);
            paket.putString("kategori", kategori);
            paket.putDouble("radius", radius);
            paket.putDouble("latitude", latitude);
            paket.putDouble("longitude", longitude);
            paket.putString("icon", icon);
            goToScreen.putExtras(paket);
            myLocationManager.removeUpdates(MainMenuActivity.this);
            myLocationManager = null;
            startActivity(goToScreen);
            finish();
        }
    }

    public void toSetting(){
        Intent gotoSetting = new Intent(getApplicationContext(), SettingCategoryBubbleActivity.class);
        Bundle paket = new Bundle();
        paket.putString("email", email);
        gotoSetting.putExtras(paket);
        myLocationManager.removeUpdates(MainMenuActivity.this);
        myLocationManager = null;
        startActivity(gotoSetting);
        finish();
    }

    public void RequestDataFromFB() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                JSONObject json = response.getJSONObject();
                try {
                    if (json != null) {
                        String text = "<b>Name :</b> " + json.getString("name") + "<br><br><b>Email :</b> " + json.getString("email") + "<br><br><b>Profile link :</b> " + json.getString("link");
                        detailUser = text;
                        view_usrPro.setProfileId(json.getString("id"));
                        view_usrPro.setCropped(true);
                        textView_usrNm.setText(json.getString("name"));
                        email = json.getString("email");
                        getDataCategory(email,latitude,longitude);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture");
        request.setParameters(parameters);
        request.executeAsync();
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
            finish();
            return super.onOptionsItemSelected(item);
        }

        if (item.getItemId() == R.id.goto_setting) {
            /*if (formRadius.is)*/
            if (real_radius>0){
                if (formRadius.getVisibility() == View.VISIBLE) {
                    formRadius.setVisibility(View.GONE);
                } else {
                    formRadius.setVisibility(View.VISIBLE);
                }
            }

            return super.onOptionsItemSelected(item);
        }

        if (item.getItemId() == R.id.goto_search) {
            if (real_radius>0){
                Intent toSearch = new Intent(getApplicationContext(), SearchLocationByCategoryActivity.class);
                myLocationManager.removeUpdates(MainMenuActivity.this);
                myLocationManager = null;
                Bundle paket = new Bundle();
                paket.putDouble("radius",radius);
                paket.putDouble("latitude",latitude);
                paket.putDouble("longitude",longitude);
                toSearch.putExtras(paket);
                startActivity(toSearch);
                finish();
            }
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
       /* DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {*/
            super.onBackPressed();
            finish();
        /*}*/
    }

    /*public void warningInfo() {
        String message = "Are you sure?";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton(logout, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        LoginManager.getInstance().logOut();
                        Intent logout_user_fb = new Intent(getApplicationContext(), FormLoginActivity.class);
                        startActivity(logout_user_fb);
                        finish();
                    }
                })
                .setNegativeButton(cancel, null);
        builder.create().show();
    }*/

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void resizeBuble(Button button, int lokasi) {
        if (lokasi <= 5) lokasi = (int) (40 * scale + 0.5f);
        else if (lokasi > 5 && lokasi < 11) lokasi = (int) (((lokasi - 1) * 10) * scale + 0.5f);
        else if (lokasi > 10) lokasi = (int) (90 * scale + 0.5f);
        ViewGroup.LayoutParams params = button.getLayoutParams();
        //Log.e("data params",""+params.height+" | "+params.width);
        int parwid = params.width;
        int parhei = params.height;
        button.setLayoutParams(params);
        if (parwid < lokasi) {
            for (int i = parwid; i <= lokasi; i++) {
                params.width = i;
                params.height = i;
                button.setLayoutParams(params);
            }
        } else if (parwid > lokasi) {
            for (int i = parwid; i >= lokasi; i--) {
                params.width = i;
                params.height = i;
                button.setLayoutParams(params);
            }
        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } /*else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Class CallWebPageTask untuk implementasi class AscyncTask
     */
    private class CallWebPageTask extends AsyncTask<String, Void, String> {

        public CallWebPageTask() {
            applicationContext = MainMenuActivity.this;
            dialog = new ProgressDialog(applicationContext);
        }


        private ProgressDialog dialog;
        protected Context applicationContext;

        @Override
        protected void onPreExecute() {
            this.dialog.setTitle("Requesting Data");
            this.dialog.setMessage("Please Wait...!!!");
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.setCancelable(false);
            //this.dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpGetOrPost httpGetOrPost = new HttpGetOrPost();
            response = httpGetOrPost.getRequest(urls[0]);
            try {
                //simpan data dari web ke dalam array
                JSONArray menuItemArray = null;
                jObject = new JSONObject(response);
                menuItemArray = jObject.getJSONArray("tag_cat");
                real_radius = Double.parseDouble(jObject.getString("rad"));
                email = jObject.getString("email");
                radius = (real_radius);

                for (int i = 0; i < menuItemArray.length(); i++) {
                    id_kategori[i] = menuItemArray.getJSONObject(i).getInt("id_category");
                    nama_katagori[i] = menuItemArray.getJSONObject(i).getString("category_name").toString();
                    lokasi[i] = menuItemArray.getJSONObject(i).getInt("total_location");
                    promo[i] = menuItemArray.getJSONObject(i).getInt("total_promo");
                    icon_kategori[i] = menuItemArray.getJSONObject(i).getString("icon").toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            /*if (dialog.isShowing()) {
                dialog.dismiss();
            }*/
            Log.e("first_check",""+first_check);
            /*if (email.equalsIgnoreCase("guest@dheket.co.id")){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainMenuActivity.this);
                builder.setMessage("This is Guest account! Are you sure to stay with this account?")
                        .setCancelable(true)
                        .setPositiveButton("No, Re-Login", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                LoginManager.getInstance().logOut();
                                myLocationManager.removeUpdates(MainMenuActivity.this);
                                myLocationManager = null;
                                Intent logout_user_fb = new Intent(getApplicationContext(), FormLoginActivity.class);
                                startActivity(logout_user_fb);
                                finish();
                            }
                        })
                        .setNegativeButton("Yes", null);
                builder.create().show();
            } else if (first_check){
                Log.e("");
                url = String.format(getResources().getString(R.string.link_getDataUser));
                getDataCategory(email, latitude, longitude);
                first_check = false;
            }*/
            updateData();
        }
    }

    public void showDialog(Context context, int x, int y){
        // x -->  X-Cordinate
        // y -->  Y-Cordinate
        Dialog dialog  = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.setCanceledOnTouchOutside(true);

        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        wmlp.x = x;
        wmlp.y = y;

        dialog.show();
    }

    public static class InputStreamToStringExample {

        public static void main(String[] args) throws IOException {

            // intilize an InputStream
            InputStream is = new ByteArrayInputStream("file content is process".getBytes());

            String result = getStringFromInputStream(is);

            System.out.println(result);
            System.out.println("Done");

        }

        // convert InputStream to String
        private static String getStringFromInputStream(InputStream is) {

            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {

                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return sb.toString();
        }
    }

    /* Inner class to get response */
    class AsyncTAddingDataToServer extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String url = String.format(getResources().getString(R.string.link_updateRadiusByEmail));
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {
                JSONObject jsonobj = new JSONObject();
                jsonobj.put("email", email);
                jsonobj.put("rad", newRadius);
                Log.e("mainToPost", "mainToPost" + jsonobj.toString());
                httppost.setEntity(new StringEntity(jsonobj.toString())); //json without header {"a"="a","b"=1}
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                InputStream inputStream = response.getEntity().getContent();
                InputStreamToStringExample str = new InputStreamToStringExample();
                responseServer = str.getStringFromInputStream(inputStream);
                Log.e("response", "response ----- " + responseServer.toString() + "|");
                Log.e("response", "response ----- " + responseServer.toString().equalsIgnoreCase("{\"success\":1}") + "|");

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (responseServer!=null && responseServer.equalsIgnoreCase("{\"success\":1}")) {
                Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_SHORT).show();
                responseServer="";
                getDataCategory(email, latitude, longitude);
            } else {
                if (responseServer.equalsIgnoreCase("") || responseServer.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Ops, Error! Please Try Again!",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void getDataCategory(String email, double lat, double lng) {
        CallWebPageTask task = new CallWebPageTask();
        task.applicationContext = getApplicationContext();
        String urls = url + "/" + email + "/" + lat + "/" + lng;
        Log.e("Sukses", urls);
        if (email!=null)task.execute(new String[]{urls});
    }

    public void updateData() {
        actionBar.setSubtitle(Html.fromHtml("<font color='#FFBF00'>Location in Radius " + formatNumber.changeFormatNumber(radius) + " Km</font>"));
        if (radius<1) {
            textViewLoad.setVisibility(View.VISIBLE);
        } else {
            textViewLoad.setVisibility(View.GONE);
        }
        initFormSettingRadius();
        txt_tot_cat1.setText("" + lokasi[0]);
        String cat1 = "-";
        if (nama_katagori[0] != null) cat1 = nama_katagori[0];
        btn_buble_cat1.setText("" + cat1);
        txt_promo_cat1.setText("Promo: " + promo[0]);
        resizeBuble(btn_buble_cat1, lokasi[0]);

        txt_tot_cat2.setText("" + lokasi[1]);
        String cat2 = "-";
        if (nama_katagori[1] != null) cat2 = nama_katagori[1];
        btn_buble_cat2.setText("" + cat2);
        txt_promo_cat2.setText("Promo: " + promo[1]);
        resizeBuble(btn_buble_cat2, lokasi[1]);

        txt_tot_cat3.setText("" + lokasi[2]);
        String cat3 = "-";
        if (nama_katagori[2] != null) cat3 = nama_katagori[2];
        btn_buble_cat3.setText("" + cat3);
        txt_promo_cat3.setText("Promo: " + promo[2]);
        resizeBuble(btn_buble_cat3, lokasi[2]);

        txt_tot_cat4.setText("" + lokasi[3]);
        String cat4 = "-";
        if (nama_katagori[3] != null) cat4 = nama_katagori[3];
        btn_buble_cat4.setText("" + cat4);
        txt_promo_cat4.setText("Promo: " + promo[3]);
        resizeBuble(btn_buble_cat4, lokasi[3]);

        txt_tot_cat5.setText("" + lokasi[4]);
        String cat5 = "-";
        if (nama_katagori[4] != null) cat5 = nama_katagori[4];
        btn_buble_cat5.setText("" + cat5);
        txt_promo_cat5.setText("Promo: " + promo[4]);
        resizeBuble(btn_buble_cat5, lokasi[4]);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        //Toast.makeText(getApplicationContext(),"lat "+latitude+" | lgt "+longitude, Toast.LENGTH_LONG).show();
        getDataCategory(email, latitude, longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        String message = "GPS enabled";
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        String message = "GPS disabled";
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public void getServiceFromGPS() {
        myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        provider = myLocationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = myLocationManager.getLastKnownLocation(provider);
        if (location != null) {
            onLocationChanged(location);
        }
        myLocationManager.requestLocationUpdates(provider, 20000, 0, this);
    }
}
