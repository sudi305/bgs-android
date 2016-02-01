package com.bgs.dheket;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bgs.common.Utility;
import com.bgs.networkAndSensor.HttpGetOrPost;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SND on 01/02/2016.
 */
public class SearchAllCategoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    android.support.v7.app.ActionBar actionBar;
    Bundle paket;
    //NumberFormat formatter = new DecimalFormat("#0.000");
    Utility formatNumber = new Utility();
    HttpGetOrPost httpGetOrPost;

    private JSONObject jObject;
    private String jsonResult ="";
    int[] id_kategori;
    String[] nama_katagori,kategori_id;
    double radius = 0.0;
    double latitude, longitude;
    String url = "http://dheket.esy.es/getAllCategory.php";

    ListView listView_allCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_map_all_location);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeAsUpIndicator(R.drawable.logo);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Dheket");

        paket = getIntent().getExtras();
        /*actionBar.setSubtitle(Html.fromHtml("<font color='#FFBF00'>Category " + paket.getString("kategori") + " in Radius "
                + formatter.format(paket.getDouble("radius")) + " Km</font>"));*/
        actionBar.setSubtitle(Html.fromHtml("<font color='#FFBF00'>All Category</font>"));

        listView_allCat = (ListView)findViewById(R.id.listView_allcat);
        //listView_allCat.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, nama_katagori));
        getDataFromServer();
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

        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(SearchAllCategoryActivity.this,MainMenuActivity.class);
            startActivity(intent);
            finish();
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
                        Intent logout_user_fb = new Intent(SearchAllCategoryActivity.this, FormLoginActivity.class);
                        startActivity(logout_user_fb);
                        finish();
                    }
                })
                .setNegativeButton(cancel, null);
        builder.create().show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final int selectId = id_kategori[position];
        final String selectNameCat = nama_katagori[position];
        final String selectCatId = kategori_id[position];
        Toast.makeText(getApplicationContext(), "Id Cat : " + selectId + " | Cat Name : " + selectNameCat, Toast.LENGTH_LONG);
        /*Intent i = new Intent(getApplicationContext(), SPBUDetail.class);
        i.putExtra("id_cat", selectId);
        i.putExtra("name_cat", selectNameCat);
        i.putExtra("cat_id", selectCatId);
        startActivity(i);*/
    }

    public void getDataFromServer(){
        CallWebPageTask task = new CallWebPageTask();
        task.applicationContext = SearchAllCategoryActivity.this;
        Log.e("Sukses", url);
        task.execute(new String[]{url});
    }

    /**
     * Class CallWebPageTask untuk implementasi class AscyncTask
     */
    private class CallWebPageTask extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;
        protected Context applicationContext;

        @Override
        protected void onPreExecute() {
            this.dialog = ProgressDialog.show(applicationContext, "Getting Data From Server", "Please Wait...", true);
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            httpGetOrPost = new HttpGetOrPost();
            response = httpGetOrPost.getRequest(urls[0]);
            try {
                //simpan data dari web ke dalam array
                JSONArray menuItemArray = null;
                jObject = new JSONObject(response);
                menuItemArray = jObject.getJSONArray("dheket_allCat");
                id_kategori = new int[menuItemArray.length()];
                nama_katagori = new String[menuItemArray.length()];
                kategori_id = new String[menuItemArray.length()];
                for (int i = 0; i < menuItemArray.length(); i++) {
                    id_kategori[i] = menuItemArray.getJSONObject(i).getInt("id_category");
                    nama_katagori[i] = menuItemArray.getJSONObject(i).getString("category_name").toString();
                    kategori_id[i] = menuItemArray.getJSONObject(i).getString("category_id").toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            this.dialog.cancel();
            updateData();
        }
    }

    public void updateData(){

    }
}
