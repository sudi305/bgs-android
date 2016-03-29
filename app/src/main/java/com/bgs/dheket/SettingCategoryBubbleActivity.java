package com.bgs.dheket;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bgs.imageOrView.ListViewAdapter;
import com.bgs.imageOrView.ListViewAdapterSettingBubble;
import com.bgs.imageOrView.RoundImage;
import com.bgs.networkAndSensor.HttpGetOrPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SND on 28/03/2016.
 */
public class SettingCategoryBubbleActivity extends AppCompatActivity {

    android.support.v7.app.ActionBar actionBar;

    RoundImage roundImage;

    String url = "";
    private JSONObject JsonObject, jsonobject;
    ArrayList<HashMap<String, String>> arraylist;
    int radius;
    String email;

    ListViewAdapterSettingBubble adapter;
    ListView listView;
    LinearLayout linearLayout;
    public static ViewGroup viewGroup;
    SeekBar seekBar_rad, seekBar_back;

    Bundle paket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_bubble);
        actionBar = getSupportActionBar();

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeAsUpIndicator(R.drawable.logo);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Configuration");
//        actionBar.setSubtitle(Html.fromHtml("<font color='#FFBF00'>Location in Radius " + formatter.format(radius) + " Km</font>"));

        url = String.format(getResources().getString(R.string.link_getUserConfig));

        listView = (ListView)findViewById(R.id.listView_setbub_cat);

        paket = getIntent().getExtras();
        email = paket.getString("email");

        getDataConfig();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            back_to_previous_screen();
            return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        back_to_previous_screen();
    }

    public void back_to_previous_screen(){
        Intent intent = new Intent(getApplicationContext(),MainMenuActivity.class);
        startActivity(intent);
        finish();
    }

    public void getDataConfig() {
        CallWebPageTask task = new CallWebPageTask();
        task.applicationContext = getApplicationContext();
        String urls = url+"/"+email;
        Log.e("Sukses", urls);
        task.execute(new String[]{urls});
    }

    private class CallWebPageTask extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;
        protected Context applicationContext;

        @Override
        protected void onPreExecute() {
            //this.dialog = ProgressDialog.show(applicationContext, "Login Process", "Please Wait...", true);
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
                menuItemArray = JsonObject.getJSONArray("user_config");
                for (int i = 0; i < menuItemArray.length(); i++) {
                    map = new HashMap<String, String>();
                    jsonobject = menuItemArray.getJSONObject(i);

                    map.put("id_category", jsonobject.getString("id_category"));
                    map.put("category_name", jsonobject.getString("category_name"));
                    map.put("id_profile_tag", jsonobject.getString("id_profile_tag"));
                    map.put("detail_tag", jsonobject.getString("detail_tag"));
                    map.put("icon", jsonobject.getString("icon"));
                    // Set the JSON Objects into the array
                    arraylist.add(map);
                }
                map = new HashMap<String, String>();
                map.put("email", JsonObject.getString("email"));
                radius = Integer.parseInt(JsonObject.getString("rad").toString())/1000;
                map.put("radius", ""+radius);
                map.put("id_profile", JsonObject.getString("id_profile"));
                arraylist.add(arraylist.size(), map);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            updateData();
            initFormSettingRadius();
        }
    }

    public void updateData(){
        adapter = new ListViewAdapterSettingBubble(SettingCategoryBubbleActivity.this, arraylist);
        listView.setAdapter(adapter);
        for (int i = 0; i < arraylist.size(); i++) {
            Log.e("arraylist", "ke-" + i + " = " + arraylist.get(i));
        }
    }

    public void initFormSettingRadius(){
        linearLayout = (LinearLayout)findViewById(R.id.linearLayout_setbub_dialog);
        viewGroup = (ViewGroup)findViewById(R.id.relativeLayout_setbub_formdialog);
        seekBar_back = (SeekBar)findViewById(R.id.seekBar_setbub_back);
        seekBar_rad = (SeekBar)findViewById(R.id.seekBar_setbub_rad);

        seekBar_back.setEnabled(false);

        seekBar_rad.setMax(9);
        seekBar_rad.setProgress(radius - 1);
        seekBar_back.setMax(9);
        seekBar_back.setProgress(radius - 1);
        String text = "";
        text=""+(seekBar_rad.getProgress()+1)+" Km";
        seekBar_back.setThumb(writeOnDrawable(R.drawable.transparant, text));

        /*bar.setThumb(getApplicationContext().getResources().getDrawable(
                R.drawable.menu_info));*/
        seekBar_rad.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                //textView.setVisibility(View.INVISIBLE);
                //saving to db
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
                Rect thumbRect = seekBar_rad.getThumb().getBounds();
                String texts = "";
                //Toast.makeText(getApplicationContext(),"position "+thumbRect.centerX(),Toast.LENGTH_SHORT).show();
                texts=""+(seekBar_rad.getProgress()+1)+" Km";
                seekBar_back.setProgress(progress);
                seekBar_back.setThumb(writeOnDrawable(R.drawable.transparant, texts));
            }
        });
        viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewGroup.setVisibility(View.GONE);
            }
        });
        linearLayout.setOnClickListener(new View.OnClickListener() {
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
}
