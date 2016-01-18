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

/**
 * Created by SND on 18/01/2016.
 */

public class FormLoginActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    LoginButton login;
    Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_form_login);

        callbackManager = CallbackManager.Factory.create();
        login = (LoginButton)findViewById(R.id.login_button);
        login.setReadPermissions("public_profile email");
        signup = (Button)findViewById(R.id.signup_button);

//        if(AccessToken.getCurrentAccessToken() != null){
//            RequestData();
//            login.setVisibility(View.INVISIBLE);
//        } else {
//            login.setVisibility(View.VISIBLE);
//        }

        login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    Intent loginWithFb = new Intent(FormLoginActivity.this,MainMenuActivity.class);
                    startActivity(loginWithFb);
                    finish();
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

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Sign Up With Email", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
