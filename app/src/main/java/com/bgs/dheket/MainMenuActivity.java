package com.bgs.dheket;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bgs.common.ConfigInternetAndGPS;
import com.bgs.common.Dialogs;
import com.bgs.common.ProfilePictureView_viaFB;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by SND on 20/01/2016.
 */

public class MainMenuActivity extends AppCompatActivity implements LocationListener{

    LinearLayout buble_cat1,buble_cat2,buble_cat3,buble_cat4,buble_cat5;
    Button btn_buble_cat1,btn_buble_cat2,btn_buble_cat3,btn_buble_cat4,btn_buble_cat5,btn_search;
    TextView txt_tot_cat1,txt_tot_cat2,txt_tot_cat3,txt_tot_cat4,txt_tot_cat5;
    TextView txt_promo_cat1,txt_promo_cat2,txt_promo_cat3,txt_promo_cat4,txt_promo_cat5;
    ImageView imVi_usrPro;
    ProfilePictureView_viaFB view_usrPro;
    CallbackManager callbackManager;
    Dialog details_dialog;
    TextView details_txt,textView_usrNm;
    ConfigInternetAndGPS checkInternetGPS;

    private JSONObject jObject;
    private String jsonResult ="";
    int[] promo=new int[5], lokasi =new int[5], id_kategori=new int[5];
    String[] nama_katagori=new String[5];
    double radius = 0.0;
    double latitude, longitude;
    String url = "http://dheket.esy.es/getLocationPromo.php";

    boolean tambah = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        FacebookSdk.sdkInitialize(getApplicationContext());

        checkInternetGPS = new ConfigInternetAndGPS(getApplicationContext());

        buble_cat1 = (LinearLayout)findViewById(R.id.buble_cat1);
        buble_cat2 = (LinearLayout)findViewById(R.id.buble_cat2);
        buble_cat3 = (LinearLayout)findViewById(R.id.buble_cat3);
        buble_cat4 = (LinearLayout)findViewById(R.id.buble_cat4);
        buble_cat5 = (LinearLayout)findViewById(R.id.buble_cat5);
        btn_buble_cat1 = (Button)findViewById(R.id.btn_buble_cat1);
        btn_buble_cat2 = (Button)findViewById(R.id.btn_buble_cat2);
        btn_buble_cat3 = (Button)findViewById(R.id.btn_buble_cat3);
        btn_buble_cat4 = (Button)findViewById(R.id.btn_buble_cat4);
        btn_buble_cat5 = (Button)findViewById(R.id.btn_buble_cat5);
        btn_search = (Button)findViewById(R.id.btn_search);
        imVi_usrPro = (ImageView)findViewById(R.id.imageView_userProfile);
        txt_promo_cat1 = (TextView)findViewById(R.id.textView_promo_cat1);
        txt_promo_cat2 = (TextView)findViewById(R.id.textView_promo_cat2);
        txt_promo_cat3 = (TextView)findViewById(R.id.textView_promo_cat3);
        txt_promo_cat4 = (TextView)findViewById(R.id.textView_promo_cat4);
        txt_promo_cat5 = (TextView)findViewById(R.id.textView_promo_cat5);
        txt_tot_cat1 = (TextView)findViewById(R.id.textView_total_cat1);
        txt_tot_cat2 = (TextView)findViewById(R.id.textView_total_cat2);
        txt_tot_cat3 = (TextView)findViewById(R.id.textView_total_cat3);
        txt_tot_cat4 = (TextView)findViewById(R.id.textView_total_cat4);
        txt_tot_cat5 = (TextView)findViewById(R.id.textView_total_cat5);
        view_usrPro = (ProfilePictureView_viaFB)findViewById(R.id.view_userProfile);
        details_dialog = new Dialog(this);
        details_dialog.setContentView(R.layout.dialog_details);
        details_dialog.setTitle("Details");
        details_txt = (TextView)details_dialog.findViewById(R.id.details);
        textView_usrNm = (TextView)findViewById(R.id.textView_usrNm);

        LocationManager myLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = myLocationManager.getBestProvider(criteria, true);
        Location location = myLocationManager.getLastKnownLocation(provider);
        if (location != null) {
            onLocationChanged(location);
        }
        myLocationManager.requestLocationUpdates(provider, 20000, 0, this);


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

        if(AccessToken.getCurrentAccessToken() != null){
            RequestDataFromFB();
        }

        view_usrPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                details_dialog.show();
            }
        });

        btn_buble_cat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_buble_cat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_buble_cat3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_buble_cat4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_buble_cat5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_search.setAnimation(animButtonPress);
                btn_search.setText("Internet "+checkInternetGPS.isConnectingToInternet()+" | GPS "+checkInternetGPS.isGPSActived());
                if (tambah==true)tambah=false;
                else tambah=true;
            }
        });
    }

    public void RequestDataFromFB(){
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object,GraphResponse response) {

                JSONObject json = response.getJSONObject();
                try {
                    if(json != null){
                        String text = "<b>Name :</b> "+json.getString("name")+"<br><br><b>Email :</b> "+json.getString("email")+"<br><br><b>Profile link :</b> "+json.getString("link");
                        details_txt.setText(Html.fromHtml(text));
                        view_usrPro.setProfileId(json.getString("id"));
                        view_usrPro.setCropped(true);
                        textView_usrNm.setText(json.getString("name"));
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

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
                        Intent logout_user_fb = new Intent(MainMenuActivity.this, FormLoginActivity.class);
                        startActivity(logout_user_fb);
                        finish();
                    }
                })
                .setNegativeButton(cancel, null);
        builder.create().show();
    }

    public void resizeBuble(Button button, int lokasi){
        if (lokasi<=5)lokasi=100;
        else if (lokasi>5 && lokasi<11) lokasi=lokasi*20;
        else if (lokasi>10)lokasi=200;
        ViewGroup.LayoutParams params = button.getLayoutParams();
        params.width=lokasi;
        params.height=lokasi;
        button.setLayoutParams(params);
    }

    /**
     * Method untuk Mengirimkan data ke server
     */
    public String getRequest(String Url){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String sreq="";
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(Url);
        try{
            HttpResponse response = client.execute(request);
            sreq =setRequest(response);

        }catch(Exception ex){
            Toast.makeText(this,"Gagal "+sreq, Toast.LENGTH_SHORT).show();
            Log.e("GAGAL",""+ex);
        }
        return sreq;
    }

    /**
     * Method untuk Menerima data dari server
     */
    public static String setRequest(HttpResponse response){
        String result = "";
        try{
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null){
                str.append(line + "\n");
            }
            in.close();
            result = str.toString();
        }catch(Exception ex){
            result = "Error";
        }

        return result;
    }

    public void getDataCategory(double rad,double lat, double lng) {
        Log.e("DHEKET ", url+"?rad="+rad+"&lat="+lat+"&lng="+lng);
        try {
            jsonResult = getRequest(url+"?rad="+rad+"&lat="+lat+"&lng="+lng);
            jObject = new JSONObject(jsonResult);
            JSONArray menuItemArray = jObject.getJSONArray("dheket_totLocation");
            for (int i = 0; i < menuItemArray.length(); i++) {
                id_kategori[i] = menuItemArray.getJSONObject(i).getInt("id_category");
                nama_katagori[i] = menuItemArray.getJSONObject(i).getString("category_name").toString();
                lokasi[i] = menuItemArray.getJSONObject(i).getInt("total_location");
                promo[i] = menuItemArray.getJSONObject(i).getInt("total_promo");
            }

            Log.e("cek","SUKSES"+nama_katagori.length+"|");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("cek","Error");
        }
    }

    public void updateData(){
        txt_tot_cat1.setText(""+lokasi[0]);
        String cat1 = "-";
        if (nama_katagori[0]!=null) cat1=nama_katagori[0];
        btn_buble_cat1.setText(""+cat1);
        txt_promo_cat1.setText("Promo: "+promo[0]);
        resizeBuble(btn_buble_cat1, lokasi[0]);

        txt_tot_cat2.setText("" + lokasi[1]);
        String cat2 = "-";
        if (nama_katagori[1]!=null) cat2=nama_katagori[1];
        btn_buble_cat2.setText(""+cat2);
        txt_promo_cat2.setText("Promo: "+promo[1]);
        resizeBuble(btn_buble_cat2, lokasi[1]);

        txt_tot_cat3.setText("" + lokasi[2]);
        String cat3 = "-";
        if (nama_katagori[2]!=null) cat3=nama_katagori[2];
        btn_buble_cat3.setText(""+cat3);
        txt_promo_cat3.setText("Promo: "+promo[2]);
        resizeBuble(btn_buble_cat3, lokasi[2]);

        txt_tot_cat4.setText("" + lokasi[3]);
        String cat4 = "-";
        if (nama_katagori[3]!=null) cat4=nama_katagori[3];
        btn_buble_cat4.setText(""+cat4);
        txt_promo_cat4.setText("Promo: "+promo[3]);
        resizeBuble(btn_buble_cat4, lokasi[3]);

        txt_tot_cat5.setText("" + lokasi[4]);
        String cat5 = "-";
        if (nama_katagori[4]!=null) cat5=nama_katagori[4];
        btn_buble_cat5.setText(""+cat5);
        txt_promo_cat5.setText("Promo: "+promo[4]);
        resizeBuble(btn_buble_cat5,lokasi[4]);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Toast.makeText(getApplicationContext(),"lat "+latitude+" | lgt "+longitude, Toast.LENGTH_LONG).show();
        getDataCategory(radius, latitude, longitude);
        if (tambah==true)radius=radius+0.4;
        else radius=radius-0.4;
        updateData();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        String message = "GPS enabled";
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        String message = "GPS disabled";
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
