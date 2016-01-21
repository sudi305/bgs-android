package com.bgs.dheket;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bgs.common.ProfilePictureView_viaFB;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SND on 20/01/2016.
 */

public class MainMenuActivity extends AppCompatActivity {
    LinearLayout buble_rest,buble_salon,buble_atm,buble_apotek,buble_mart;
    Button btn_buble_rest,btn_buble_salon,btn_buble_atm,btn_buble_apotek,btn_buble_mart,btn_search;
    TextView txt_tot_rest,txt_tot_salon,txt_tot_atm,txt_tot_apotek,txt_tot_mart;
    TextView txt_promo_rest,txt_promo_salon,txt_promo_atm,txt_promo_apotek,txt_promo_mart;
    ImageView imVi_usrPro;
    ProfilePictureView_viaFB view_usrPro;
    CallbackManager callbackManager;
    Dialog details_dialog;
    TextView details_txt,textView_usrNm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        FacebookSdk.sdkInitialize(getApplicationContext());

        buble_apotek = (LinearLayout)findViewById(R.id.buble_apotek);
        buble_salon = (LinearLayout)findViewById(R.id.buble_salon);
        buble_rest = (LinearLayout)findViewById(R.id.buble_rest);
        buble_mart = (LinearLayout)findViewById(R.id.buble_mart);
        buble_atm = (LinearLayout)findViewById(R.id.buble_atm);
        btn_buble_apotek = (Button)findViewById(R.id.btn_buble_apotek);
        btn_buble_salon = (Button)findViewById(R.id.btn_buble_salon);
        btn_buble_rest = (Button)findViewById(R.id.btn_buble_rest);
        btn_buble_mart = (Button)findViewById(R.id.btn_buble_mart);
        btn_buble_atm = (Button)findViewById(R.id.btn_buble_atm);
        btn_search = (Button)findViewById(R.id.btn_search);
        imVi_usrPro = (ImageView)findViewById(R.id.imageView_userProfile);
        txt_promo_apotek = (TextView)findViewById(R.id.textView_promo_apotek);
        txt_promo_atm = (TextView)findViewById(R.id.textView_promo_atm);
        txt_promo_mart = (TextView)findViewById(R.id.textView_promo_mart);
        txt_promo_rest = (TextView)findViewById(R.id.textView_promo_rest);
        txt_promo_salon = (TextView)findViewById(R.id.textView_promo_salon);
        txt_tot_apotek = (TextView)findViewById(R.id.textView_total_apotek);
        txt_tot_atm = (TextView)findViewById(R.id.textView_total_atm);
        txt_tot_mart = (TextView)findViewById(R.id.textView_total_mart);
        txt_tot_rest = (TextView)findViewById(R.id.textView_total_rest);
        txt_tot_salon = (TextView)findViewById(R.id.textView_total_salon);
        txt_tot_salon = (TextView)findViewById(R.id.textView_total_salon);
        view_usrPro = (ProfilePictureView_viaFB)findViewById(R.id.view_userProfile);
        details_dialog = new Dialog(this);
        details_dialog.setContentView(R.layout.dialog_details);
        details_dialog.setTitle("Details");
        details_txt = (TextView)details_dialog.findViewById(R.id.details);
        textView_usrNm = (TextView)findViewById(R.id.textView_usrNm);

        final Animation animTranslate = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        final Animation animScale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        final Animation animButtonPress = AnimationUtils.loadAnimation(this, R.anim.anim_scale_button_press);

        imVi_usrPro.setAnimation(animScale);
        buble_apotek.setAnimation(animTranslate);
        buble_salon.setAnimation(animTranslate);
        buble_rest.setAnimation(animTranslate);
        buble_mart.setAnimation(animTranslate);
        buble_atm.setAnimation(animTranslate);

        if(AccessToken.getCurrentAccessToken() != null){
            RequestData();
        }

        view_usrPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                details_dialog.show();
            }
        });

        btn_buble_apotek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_buble_apotek.setAnimation(animAlpha);
            }
        });

        btn_buble_salon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_buble_salon.setAnimation(animAlpha);
            }
        });

        btn_buble_rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_buble_rest.setAnimation(animAlpha);
            }
        });

        btn_buble_mart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_buble_mart.setAnimation(animAlpha);
            }
        });

        btn_buble_atm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_buble_atm.setAnimation(animAlpha);
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_search.setAnimation(animButtonPress);
            }
        });
    }

    public void RequestData(){
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
}
