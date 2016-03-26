package com.bgs.dheket;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.login.LoginManager;

/**
 * Created by ade on 01-Feb-16.
 */
public class SearchViewActivity extends AppCompatActivity {
    // Declare Variables
    String id_category; //rank
    String category_name; //country
    String category_id; //population
    String position;

    boolean tambah = true;  // tombol back
    android.support.v7.app.ActionBar actionBar;  // tombol back
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from singleitemview.xml
        setContentView(R.layout.searchview);

        actionBar = getSupportActionBar(); // tombol back

        actionBar.setDisplayShowHomeEnabled(true); // tombol back
        actionBar.setDisplayHomeAsUpEnabled(true); // tombol back
        //actionBar.setHomeAsUpIndicator(R.drawable.logo);
        actionBar.setHomeButtonEnabled(true); // tombol back
        actionBar.setTitle("Dheket"); // tombol back

        Intent i = getIntent();
        // Get the result of rank
        id_category = i.getStringExtra(""); //rank
        // Get the result of country
        category_name = i.getStringExtra("category_name"); //country
        // Get the result of population
        category_id = i.getStringExtra(""); //population

        // Locate the TextViews in singleitemview.xml view yang ke 2
        TextView txtid_category = (TextView) findViewById(R.id.id_category); //rank
        TextView txtcategory_name = (TextView) findViewById(R.id.category_name); //country
        TextView txtcategory_id = (TextView) findViewById(R.id.category_id); //population


        // Set results to the TextViews yang ke 2
        txtid_category.setText(id_category); //rank
        txtcategory_name.setText(category_name); //country
        txtcategory_id.setText(category_id); //population

    }

    ///
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
            back_to_previous_screen();
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
                        Intent logout_user_fb = new Intent(SearchViewActivity.this, FormLoginActivity.class);
                        startActivity(logout_user_fb);
                        finish();
                    }
                })
                .setNegativeButton(cancel, null);
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        back_to_previous_screen();
    }

    public void back_to_previous_screen(){
        Intent intent = new Intent(SearchViewActivity.this,SearchAllCategoryActivity.class);
        startActivity(intent);
        finish();
    }

}


