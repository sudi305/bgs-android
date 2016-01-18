package com.bgs.dheket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

/**
 * Created by SND on 18/01/2016.
 */
public class SplashScreenActivity extends Activity{
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FacebookSdk.sdkInitialize(getApplicationContext());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AccessToken.getCurrentAccessToken() != null) {
                    Intent loginWithFb = new Intent(SplashScreenActivity.this, MainMenuActivity.class);
                    startActivity(loginWithFb);
                    finish();
                }else {
                    Intent i = new Intent(SplashScreenActivity.this, FormLoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }
}
