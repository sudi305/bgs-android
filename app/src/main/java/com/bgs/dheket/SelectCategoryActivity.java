package com.bgs.dheket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bgs.hashTag.HashTag;
import com.bgs.hashTag.TagsView;
import com.bgs.imageOrView.RoundImage;
import com.bgs.networkAndSensor.HttpGetOrPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SND on 31/03/2016.
 */
public class SelectCategoryActivity extends AppCompatActivity {

    android.support.v7.app.ActionBar actionBar;

    RoundImage roundImage;

    String url = "";
    private JSONObject JsonObject, jsonobject;
    ArrayList<HashMap<String, String>> arraylist;
    String email;

    LinearLayout linearLayout;
    EditText editText_search;
    TagsView category;
    Bundle paket;

    ArrayAdapter<String> adapter;
    private MyAdapter mMyAdapter;
    private List<String> filteredList = new ArrayList<>();
    private String[] newDataAfterRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);
        actionBar = getSupportActionBar();

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeAsUpIndicator(R.drawable.logo);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Select Category");
//        actionBar.setSubtitle(Html.fromHtml("<font color='#FFBF00'>Location in Radius " + formatter.format(radius) + " Km</font>"));

        url = String.format(getResources().getString(R.string.link_getAllCategory));

        paket = getIntent().getExtras();
        email = paket.getString("email");

        editText_search = (EditText)findViewById(R.id.editText_search_select_category);
        editText_search.addTextChangedListener(textWatcher);
        category = (TagsView)findViewById(R.id.tagsView_select_category);

        getDataCategory();
    }

    final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            arraylist.contains(s);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        public MyAdapter() {
            //Collections.addAll(filteredList, data);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(View.inflate(parent.getContext(), R.layout.item_my, null));
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.textView.setText(filteredList.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = holder.textView.getText().toString();
                    Toast.makeText(getApplicationContext(), email, Toast.LENGTH_SHORT).show();
                    //Uri imgUrl = Math.random() > .7d ? null : Uri.parse("https://robohash.org/" + Math.abs(email.hashCode()));
                    Uri imgUrl = null;
                    HashTag hashTag = new HashTag("", "", email, imgUrl);
                    category.addTags(email, imgUrl, hashTag);
                    String itemLabel = filteredList.get(position);
                    filteredList.remove(position);
                    filteredList.remove(email);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, filteredList.size());

                    //Toast.makeText(getApplicationContext(),"Added : " + itemLabel+" to Hashtag",Toast.LENGTH_SHORT).show();
                    newDataAfterRemove = new String[filteredList.size()];
                    for (int i = 0; i < filteredList.size(); i++) {
                        newDataAfterRemove[i] = filteredList.get(i).toString();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return filteredList.size();
        }

        public void filterItems(CharSequence text) {
            filteredList.clear();
            if (TextUtils.isEmpty(text)) {
                Collections.addAll(filteredList, newDataAfterRemove);
            } else {
                for (String s : newDataAfterRemove) {
                    if (s.toLowerCase().contains(text)) {
                        filteredList.add(s);
                    }
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return Math.abs(filteredList.get(position).hashCode());
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView textView;
        public final ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text_view_recyclerView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView_recyclerView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_category_or_tag, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            back_to_previous_screen();
            return super.onOptionsItemSelected(item);
        }

        if (item.getItemId() == R.id.select_done) {
            //save
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
        Intent intent = new Intent(getApplicationContext(),SettingCategoryBubbleActivity.class);
        Bundle paket = new Bundle();
        paket.putString("email",email);
        intent.putExtras(paket);
        startActivity(intent);
        finish();
    }

    public void getDataCategory() {
        CallWebPageTask task = new CallWebPageTask();
        task.applicationContext = getApplicationContext();
        String urls = url;
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
                menuItemArray = JsonObject.getJSONArray("dheket_allCat");
                for (int i = 0; i < menuItemArray.length(); i++) {
                    map = new HashMap<String, String>();
                    jsonobject = menuItemArray.getJSONObject(i);

                    map.put("id_category", jsonobject.getString("id_category"));
                    map.put("category_name", jsonobject.getString("category_name"));
                    // Set the JSON Objects into the array
                    arraylist.add(map);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            updateData();
        }
    }

    public void updateData(){
        for (int i = 0; i < arraylist.size(); i++) {
            Log.e("arraylist", "ke-" + i + " = " + arraylist.get(i));
            String email = arraylist.get(i).get("category_name").toString();
            //Uri imgUrl = Math.random() > .7d ? null : Uri.parse("https://robohash.org/" + Math.abs(email.hashCode()));
            Uri imgUrl = null;
            HashTag hashTag = new HashTag("", arraylist.get(i).get("id_category"), email, imgUrl);
            category.addTags(email, imgUrl, hashTag);
        }
    }

}

