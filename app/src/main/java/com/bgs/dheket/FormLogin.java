package com.bgs.dheket;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;


public class FormLogin extends AppCompatActivity {
    CallbackManager callbackManager;
    Button share,details,logout_button;
    ShareDialog shareDialog;
    LoginButton login;
    ProfilePictureView profile;
    Dialog details_dialog;
    TextView details_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_form_login);

        callbackManager = CallbackManager.Factory.create();
        login = (LoginButton)findViewById(R.id.login_button);
        profile = (ProfilePictureView)findViewById(R.id.picture);
        shareDialog = new ShareDialog(this);
        share = (Button)findViewById(R.id.share);
        details = (Button)findViewById(R.id.details);
        logout_button = (Button)findViewById(R.id.logout_button);
        login.setReadPermissions("public_profile email");
        share.setVisibility(View.INVISIBLE);
        details.setVisibility(View.INVISIBLE);
        details_dialog = new Dialog(this);
        details_dialog.setContentView(R.layout.dialog_details);
        details_dialog.setTitle("Details");
        details_txt = (TextView)details_dialog.findViewById(R.id.details);
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                details_dialog.show();
            }
        });


        if(AccessToken.getCurrentAccessToken() != null){
            RequestData();
            share.setVisibility(View.VISIBLE);
            details.setVisibility(View.VISIBLE);
            logout_button.setVisibility(View.VISIBLE);
            login.setVisibility(View.INVISIBLE);
        } else {
            logout_button.setVisibility(View.INVISIBLE);
            login.setVisibility(View.VISIBLE);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AccessToken.getCurrentAccessToken() != null) {

                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareLinkContent content = new ShareLinkContent.Builder().build();
                shareDialog.show(content);

            }
        });


        login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                if (AccessToken.getCurrentAccessToken() != null) {
                    RequestData();
                    logout_button.setVisibility(View.VISIBLE);
                    login.setVisibility(View.INVISIBLE);
                    share.setVisibility(View.VISIBLE);
                    details.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Login Canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
            }
        });

        logout_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                logout_user();
            }
        });

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
                        logout_button.setVisibility(View.INVISIBLE);
                        login.setVisibility(View.VISIBLE);
                        share.setVisibility(View.INVISIBLE);
                        details.setVisibility(View.INVISIBLE);
                        profile.setProfileId(null);
                    }
                })
                .setNegativeButton(cancel, null);
        builder.create().show();
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
                        profile.setProfileId(json.getString("id"));
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
