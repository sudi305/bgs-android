package com.bgs.dheket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by SND on 31/03/2016.
 */
public class SelectCategoryActivity extends AppCompatActivity {

    android.support.v7.app.ActionBar actionBar;

    String url = "",responseServer="";
    private JSONObject JsonObject, jsonobject;
    ArrayList<HashMap<String, String>> catarraylist;
    private ArrayList<String> categorys = new ArrayList<>();
    private String[] categoryReady;
    String email,category_name,new_category_name,id_category,id_profile_tag,detail_tag,new_id_category;
    String selectCategorys;
    ArrayList<String>categoryUser;
    Bundle paket;

    LinearLayout ll_sc_search, ll_sc_result;
    EditText editText_search;
    LayoutInflater mInflater;
    private TagFlowLayout tfl_search, tfl_result;
    private EditText editText;

    private TagAdapter<String> mAdapter ;

    ArrayAdapter<String> adapter;
    private List<String> filteredList = new ArrayList<>();
    private String[] newDataAfterRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.d_ic_back));
        //actionBar.setHomeAsUpIndicator(R.drawable.logo);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Select Category");
//        actionBar.setSubtitle(Html.fromHtml("<font color='#FFBF00'>Location in Radius " + formatter.format(radius) + " Km</font>"));

        url = String.format(getResources().getString(R.string.link_getAllCategory));

        paket = getIntent().getExtras();
        email = paket.getString("email");
        id_category = paket.getString("id_category");
        id_profile_tag = paket.getString("id_profile_tag");
        detail_tag = paket.getString("detail_tag");
        category_name = paket.getString("category_name");
        categoryUser = paket.getStringArrayList("data_category");
        Log.e("sizex "+categoryUser.size(),""+categoryUser.toString());

        for (int i = 0; i <categoryUser.size() ; i++) {
            if (categoryUser.get(i).equalsIgnoreCase(category_name))categoryUser.remove(i);
        }
        Log.e("size",""+categoryUser.size());

        editText_search = (EditText)findViewById(R.id.editText_search_select_category);
        editText_search.addTextChangedListener(textWatcher);

        ll_sc_result = (LinearLayout)findViewById(R.id.ll_sc_result_cat);
        ll_sc_search = (LinearLayout)findViewById(R.id.ll_sc_search);

        tfl_result = (TagFlowLayout)findViewById(R.id.tfl_sc_result_cat);
        tfl_search = (TagFlowLayout)findViewById(R.id.tfl_sc_search);

        mInflater = LayoutInflater.from(getApplicationContext());

        getDataCategory();
    }

    final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (catarraylist.size()!=0) {
                filterItems(s);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

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
            new_id_category = id_category;
            gotoSelectHashtag();
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
            catarraylist = new ArrayList<HashMap<String, String>>();
            try {
                HashMap<String, String> map = new HashMap<String,String>();
                JSONArray menuItemArray = null;
                JsonObject = new JSONObject(response);
                menuItemArray = JsonObject.getJSONArray("dheket_allCat");
                int index = 0;
                boolean notEqual = true;
                for (int i = 0; i < menuItemArray.length(); i++) {
                    map = new HashMap<String, String>();
                    jsonobject = menuItemArray.getJSONObject(i);

                    map.put("id_category", jsonobject.getString("id_category"));
                    map.put("category_name", jsonobject.getString("category_name"));
                    for (int j = 0; j < categoryUser.size(); j++){
                        if (jsonobject.getString("category_name").equalsIgnoreCase(categoryUser.get(j).toString())){
                            notEqual = false;
                            Log.e("sama",""+jsonobject.getString("category_name").equalsIgnoreCase(categoryUser.get(j).toString()));
                        }
                    }
                    if (notEqual==true){
                        categorys.add(index,jsonobject.getString("category_name"));
                        index++;
                    } else {
                        notEqual=true;
                    }
                    // Set the JSON Objects into the array
                    catarraylist.add(map);
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
        /*for (int i = 0; i < catarraylist.size(); i++) {
            Log.e("arraylist", "ke-" + i + " = " + catarraylist.get(i));
            String email = catarraylist.get(i).get("category_name").toString();
            //Uri imgUrl = Math.random() > .7d ? null : Uri.parse("https://robohash.org/" + Math.abs(email.hashCode()));
        }*/
        if (catarraylist.size()!=0) {
            categoryReady = new String[categorys.size()];
            for (int i = 0; i < categoryReady.length; i++) {
                categoryReady[i] = categorys.get(i).toString();
            }
            newDataAfterRemove = categoryReady;
            Collections.addAll(filteredList, categoryReady);
            initResultCat();
        }
    }

    public void initResultCat(){
        //mFlowLayout.setMaxSelectCount(3);
        tfl_result.setMaxSelectCount(1);
        tfl_result.setAdapter(mAdapter = new TagAdapter<String>(categorys) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) mInflater.inflate(R.layout.flow_tv_without_icon,
                        tfl_result, false);
                tv.setText(s);
                return tv;
            }
        });

        tfl_result.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                //Toast.makeText(getApplicationContext(), catarraylist.get(position).get("category_name"), Toast.LENGTH_SHORT).show();
                //view.setVisibility(View.GONE);
                new_category_name = catarraylist.get(position).get("category_name");
                for (int i = 0; i < catarraylist.size() ; i++) {
                    if (categorys.get(position).equalsIgnoreCase(catarraylist.get(i).get("category_name"))){
                        new_id_category = catarraylist.get(i).get("id_category");
                        new_category_name = catarraylist.get(i).get("category_name");
                    }
                }

                AsyncTAddingDataToServer asyncTAddingDataToServer = new AsyncTAddingDataToServer();
                asyncTAddingDataToServer.execute();

                return true;
            }
        });


        tfl_result.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                //setTitle("choose:" + selectPosSet.toString());
            }
        });

        for (int i = 0; i < categoryReady.length ; i++) {
            if (categoryReady[i].equalsIgnoreCase(category_name)){
                //mAdapter.setSingleSelected(i,categorys[i]);
                //Log.e("selected",""+mAdapter.setSingleSelected(i,categorys[i])+" | "+categorys[i]);
                mAdapter.setSingleSelected(i);
            }
        }
    }

    public void filterItems(CharSequence text) {
        int countNotFound = 0;
        filteredList.clear();
        Log.e("masuk", "" + text);
        if (TextUtils.isEmpty(text)) {
            Collections.addAll(filteredList, newDataAfterRemove);
            ll_sc_result.setVisibility(View.VISIBLE);
            ll_sc_search.setVisibility(View.GONE);
            changeViewSearch();
            Log.e("kosong","iya");
        } else {
            ll_sc_result.setVisibility(View.GONE);
            ll_sc_search.setVisibility(View.VISIBLE);
            for (String s : newDataAfterRemove) {
                if (s.toLowerCase().contains(text.toString().toLowerCase())) {
                    filteredList.add(s);
                    //Log.e("cari dan ketemu", ""+filteredList.add(s));
                    changeViewSearch();
                }
                else {
                    countNotFound++;
                }
                //Log.e("cari", ""+filteredList.add(s));
            }
            if (countNotFound==newDataAfterRemove.length){
                filteredList.clear();
                changeViewSearch();
            }
        }
        //notifyDataSetChanged();
    }

    public void changeViewSearch(){
        final int[] searchItem = {0};
        final String[] cari = {""};
        tfl_search.setMaxSelectCount(1);
        tfl_search.setAdapter(new TagAdapter<String>(filteredList)
        {

            @Override
            public View getView(FlowLayout parent, int position, String s)
            {
                TextView tv = (TextView) mInflater.inflate(R.layout.flow_tv_without_icon,
                        tfl_search, false);
                tv.setText(s);
                return tv;
            }
        });

        tfl_search.setOnTagClickListener(new TagFlowLayout.OnTagClickListener()
        {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent)
            {
                //Toast.makeText(getApplicationContext(), filteredList.get(position), Toast.LENGTH_SHORT).show();
                //view.setVisibility(View.GONE);
                //Log.e("yang terpilih", "" + searchItem[0] + "|" + cari[0]);
                for (int i = 0; i < categoryReady.length; i++) {
                    if (categoryReady[i].equalsIgnoreCase(filteredList.get(position))){
                        //mAdapter.setSingleSelected(i,categorys[i]);
                        //Log.e("selected",""+mAdapter.setSingleSelected(i,categorys[i])+" | "+categorys[i]);
                        mAdapter.setSingleSelected(i);
                        Log.e("mAdapter", "" + mAdapter.getItem(i).toString());
                        ll_sc_search.setVisibility(View.GONE);
                        ll_sc_result.setVisibility(View.VISIBLE);
                        for (int j = 0; j < catarraylist.size() ; j++) {
                            if (mAdapter.getItem(i).toString().equalsIgnoreCase(catarraylist.get(j).get("category_name"))){
                                new_id_category = catarraylist.get(j).get("id_category");
                                new_category_name = catarraylist.get(j).get("category_name");
                            }
                        }

                        AsyncTAddingDataToServer asyncTAddingDataToServer = new AsyncTAddingDataToServer();
                        asyncTAddingDataToServer.execute();
                        editText_search.setText("");
                    }
                }
                mAdapter.notifyDataChanged();
                return true;
            }
        });


        tfl_search.setOnSelectListener(new TagFlowLayout.OnSelectListener()
        {
            @Override
            public void onSelected(Set<Integer> selectPosSet)
            {
                //searchItem = Integer.parseInt(selectPosSet.toString().replace("[","").replace("]",""));
                //setTitle("choose:" + selectPosSet.toString());
            }
        });
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
            String url = String.format(getResources().getString(R.string.link_updateCategoryUserByEmail));
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            Log.e("link", url + " | " + id_category + " | " + new_id_category + " | " + email);
            try {
                JSONObject jsonobj = new JSONObject();
                jsonobj.put("email", email);
                jsonobj.put("cat_old", Integer.parseInt(id_category));
                jsonobj.put("cat_new",Integer.parseInt(new_id_category));
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
                Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                responseServer="";
                gotoSelectHashtag();
            } else {
                if (responseServer.equalsIgnoreCase("") || responseServer.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Ops, Error! Please Try Again!",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void gotoSelectHashtag(){
        Intent tonext = new Intent(getApplicationContext(), SelectHashtagActivity.class);
        Bundle paket = new Bundle();
        paket.putString("email", email);
        paket.putString("id_profile_tag", id_profile_tag);
        if (Integer.parseInt(id_category)==Integer.parseInt(new_id_category)){
            paket.putString("detail_tag",detail_tag);
            paket.putString("id_category",id_category);
            paket.putString("category_name", category_name);
        } else {
            paket.putString("id_category",new_id_category);
            paket.putString("category_name", new_category_name);
            paket.putString("detail_tag","");
            categoryUser.remove(category_name);
            categoryUser.add(new_category_name);
        }
        paket.putStringArrayList("data_category",categoryUser);
        tonext.putExtras(paket);
        startActivity(tonext);
        finish();
    }
}

