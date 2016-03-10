package com.bgs.dheket;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bgs.hashTag.HashTag;
import com.bgs.hashTag.TagsView;
import com.bgs.networkAndSensor.HttpGetOrPost;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SND on 23/02/2016.
 */
public class SettingCategoryBubleActivity extends AppCompatActivity {
    android.support.v7.app.ActionBar actionBar;

    private int GET_INDEX_OF_HASHTAG_POSITION = 0;

    EditText editText_cat1, editText_cat2, editText_cat3, editText_cat4, editText_cat5, editText_radius;
    TagsView editText_subcat1, editText_subcat2, editText_subcat3, editText_subcat4, editText_subcat5;
    Button button_save;
    ImageView imageView_set_radius,imageView_expand_rad,imageView_expand_cat1,imageView_expand_cat2,imageView_expand_cat3,
            imageView_expand_cat4,imageView_expand_cat5;
    TextView textView_sb_ct1,textView_sb_ct2,textView_sb_ct3,textView_sb_ct4,textView_sb_ct5,textView_warning,
            textView_sb_cc1,textView_sb_cc2,textView_sb_cc3,textView_sb_cc4,textView_sb_cc5,
            textView_hashtag_noresult1,textView_hashtag_noresult2,textView_hashtag_noresult3,textView_hashtag_noresult4,
            textView_hashtag_noresult5;
    ViewGroup title_rad,title_cat1,title_cat2,title_cat3,title_cat4,title_cat5,con_rad,con_cat1,con_cat2,con_cat3,
            con_cat4,con_cat5,list_hashtag1,list_hashtag2,list_hashtag3,list_hashtag4,list_hashtag5,
            ll_sbt_ht1,ll_sbt_ht2,ll_sbt_ht3,ll_sbt_ht4,ll_sbt_ht5;
    RadioGroup rgCat1,rgCat2,rgCat3,rgCat4,rgCat5;
    RadioButton rbCat_1,rbCat_2,rbCat_3,rbCat_4,rbCat_5,rbBrand_1,rbBrand_2,rbBrand_3,rbBrand_4,rbBrand_5;

    String url = "http://dheket.esy.es/getAllCategory.php";
    private JSONObject jObject;
    String []nama_katagori;
    int []id_kategori;
    int []id_kategori_select_user = {9999,9999,9999,9999,9999};
    /*int []id_subCat;
    int []temp_id_cat = new int[1];*/
    ArrayAdapter<String> adapter;
    int lastPosition;
    double radius;

    private RecyclerView mRecyclerView,mRecyclerView2,mRecyclerView3,mRecyclerView4,mRecyclerView5;
    private MyAdapter mMyAdapter,mMyAdapter2,mMyAdapter3,mMyAdapter4,mMyAdapter5;

    boolean []showHideContent = {true,false,false,false,false,false};
    boolean []hasValid = {true,true,true,true,true,true,true,true,true,true,true,true};
    int []tipeCategory = {2,1,2,1,2};

    String [] dataHashtagName,dataHashtagId,
            dataHashtagName2,dataHashtagId2,
            dataHashtagName3,dataHashtagId3,
            dataHashtagName4,dataHashtagId4,
            dataHashtagName5,dataHashtagId5;

    private String[] newDataAfterRemove,newDataAfterRemove2,newDataAfterRemove3,
            newDataAfterRemove4,newDataAfterRemove5;
    private List<String> filteredList = new ArrayList<>(),filteredList2 = new ArrayList<>(),
            filteredList3 = new ArrayList<>(),filteredList4 = new ArrayList<>(),
            filteredList5 = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_category_buble_new);
        actionBar = getSupportActionBar();

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeAsUpIndicator(R.drawable.logo);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Dheket");
        //actionBar.setSubtitle(Html.fromHtml("<font color='#FFBF00'>Location in Radius " + formatter.format(radius) + " Km</font>"));
        actionBar.setSubtitle(Html.fromHtml("<font color='#ff9800'>Setting category bubble</font>"));

        title_rad = (ViewGroup)findViewById(R.id.ll_sbt_rad);
        title_rad.setOnClickListener(title0);
        title_cat1 = (ViewGroup)findViewById(R.id.ll_sbt_cat1);
        title_cat1.setOnClickListener(title1);
        title_cat2 = (ViewGroup)findViewById(R.id.ll_sbt_cat2);
        title_cat2.setOnClickListener(title2);
        title_cat3 = (ViewGroup)findViewById(R.id.ll_sbt_cat3);
        title_cat3.setOnClickListener(title3);
        title_cat4 = (ViewGroup)findViewById(R.id.ll_sbt_cat4);
        title_cat4.setOnClickListener(title4);
        title_cat5 = (ViewGroup)findViewById(R.id.ll_sbt_cat5);
        title_cat5.setOnClickListener(title5);
        con_rad = (ViewGroup)findViewById(R.id.linearLayout_sb_rad);
        con_cat1 = (ViewGroup)findViewById(R.id.linearLayout_sb_cat1);
        con_cat2 = (ViewGroup)findViewById(R.id.linearLayout_sb_cat2);
        con_cat3 = (ViewGroup)findViewById(R.id.linearLayout_sb_cat3);
        con_cat4 = (ViewGroup)findViewById(R.id.linearLayout_sb_cat4);
        con_cat5 = (ViewGroup)findViewById(R.id.linearLayout_sb_cat5);
        list_hashtag1 = (ViewGroup)findViewById(R.id.linearLayout_sb_listhashtag1);
        list_hashtag2 = (ViewGroup)findViewById(R.id.linearLayout_sb_listhashtag2);
        list_hashtag3 = (ViewGroup)findViewById(R.id.linearLayout_sb_listhashtag3);
        list_hashtag4 = (ViewGroup)findViewById(R.id.linearLayout_sb_listhashtag4);
        list_hashtag5 = (ViewGroup)findViewById(R.id.linearLayout_sb_listhashtag5);
        ll_sbt_ht1 = (ViewGroup)findViewById(R.id.ll_sbt_ht1);
        ll_sbt_ht2 = (ViewGroup)findViewById(R.id.ll_sbt_ht2);
        ll_sbt_ht3 = (ViewGroup)findViewById(R.id.ll_sbt_ht3);
        ll_sbt_ht4 = (ViewGroup)findViewById(R.id.ll_sbt_ht4);
        ll_sbt_ht5 = (ViewGroup)findViewById(R.id.ll_sbt_ht5);

        imageView_expand_rad = (ImageView)findViewById(R.id.imageView_sbt_rad);
        imageView_expand_cat1 = (ImageView)findViewById(R.id.imageView_sbt_cat1);
        imageView_expand_cat2 = (ImageView)findViewById(R.id.imageView_sbt_cat2);
        imageView_expand_cat3 = (ImageView)findViewById(R.id.imageView_sbt_cat3);
        imageView_expand_cat4 = (ImageView)findViewById(R.id.imageView_sbt_cat4);
        imageView_expand_cat5 = (ImageView)findViewById(R.id.imageView_sbt_cat5);
        
        editText_cat1 = (EditText)findViewById(R.id.editText_sb_cat1);
        editText_cat1.setOnClickListener(cat1);

        editText_cat2 = (EditText)findViewById(R.id.editText_sb_cat2);
        editText_cat2.setOnClickListener(cat2);

        editText_cat3 = (EditText)findViewById(R.id.editText_sb_cat3);
        editText_cat3.setOnClickListener(cat3);

        editText_cat4 = (EditText)findViewById(R.id.editText_sb_cat4);
        editText_cat4.setOnClickListener(cat4);

        editText_cat5 = (EditText)findViewById(R.id.editText_sb_cat5);
        editText_cat5.setOnClickListener(cat5);

        editText_radius = (EditText)findViewById(R.id.editText_sb_radius);
        editText_radius.addTextChangedListener(textWatcher);

        editText_subcat1 = (TagsView)findViewById(R.id.view_sb_tag1);
        editText_subcat1.setAllowEdit(false);
        editText_subcat1.findViewById(R.id.hashtag_view).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) showHideListHashtag(1, true);
                else showHideListHashtag(1, false);
                GET_INDEX_OF_HASHTAG_POSITION=1;
            }
        });
        editText_subcat1.findViewById(R.id.editText_hashtagEditText).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) showHideListHashtag(1, true);
                else showHideListHashtag(1, false);
                GET_INDEX_OF_HASHTAG_POSITION=1;
            }
        });

        editText_subcat1.setTagsListener(new TagsView.TagsListener() {
            @Override
            public void onTagsAdded(TagsView.Tags tags) {

            }

            @Override
            public void onTagsDeleted(TagsView.Tags tags) {

            }

            @Override
            public void onTextChanged(CharSequence text) {
                String teks = text.toString().toLowerCase();
                if (newDataAfterRemove != null){
                    mMyAdapter.filterItems(teks);
                    if (mMyAdapter.getItemCount() < 1) textView_hashtag_noresult1.setVisibility(View.VISIBLE);
                    else textView_hashtag_noresult1.setVisibility(View.GONE);
                    Log.e("disini awal", "removeTagListSize" + editText_subcat1.removeTagsList.size());
                    if (editText_subcat1.removeTagsList.size() != 0) {
                        editText_subcat1.removeTagsList.get(0);
                        filteredList.add(editText_subcat1.removeTagsList.get(0).getHashTag().getHashTagName());
                        newDataAfterRemove = new String[filteredList.size()];
                        for (int i = 0; i < filteredList.size(); i++) {
                            newDataAfterRemove[i] = filteredList.get(i).toString();
                        }
                        editText_subcat1.removeTagsList.clear();
                        Log.e("disini akhir", "removeTagListSize" + editText_subcat1.removeTagsList.size());
                    }
                }
            }
        });

        editText_subcat2 = (TagsView)findViewById(R.id.view_sb_tag2);
        editText_subcat2.findViewById(R.id.editText_hashtagEditText).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) showHideListHashtag(2, true);
                else showHideListHashtag(2, false);
            }
        });
        editText_subcat3 = (TagsView)findViewById(R.id.view_sb_tag3);
        editText_subcat3.findViewById(R.id.editText_hashtagEditText).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) showHideListHashtag(3, true);
                else showHideListHashtag(3, false);
            }
        });
        editText_subcat4 = (TagsView)findViewById(R.id.view_sb_tag4);
        editText_subcat4.findViewById(R.id.editText_hashtagEditText).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) showHideListHashtag(4, true);
                else showHideListHashtag(4, false);
            }
        });
        editText_subcat5 = (TagsView)findViewById(R.id.view_sb_tag5);
        editText_subcat5.findViewById(R.id.editText_hashtagEditText).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) showHideListHashtag(5,true);
                else showHideListHashtag(5,false);
            }
        });

        imageView_set_radius = (ImageView)findViewById(R.id.imageView_set_radius);
        final Animation rotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
        imageView_set_radius.setAnimation(rotate);
        //button_save = (Button)findViewById(R.id.btn_set_save);

        textView_sb_ct1 = (TextView)findViewById(R.id.textView_sb_ch1);
        textView_sb_ct2 = (TextView)findViewById(R.id.textView_sb_ch2);
        textView_sb_ct3 = (TextView)findViewById(R.id.textView_sb_ch3);
        textView_sb_ct4 = (TextView)findViewById(R.id.textView_sb_ch4);
        textView_sb_ct5 = (TextView)findViewById(R.id.textView_sb_ch5);
        textView_sb_cc1 = (TextView)findViewById(R.id.textView_sb_cc1);
        textView_sb_cc2 = (TextView)findViewById(R.id.textView_sb_cc2);
        textView_sb_cc3 = (TextView)findViewById(R.id.textView_sb_cc3);
        textView_sb_cc4 = (TextView)findViewById(R.id.textView_sb_cc4);
        textView_sb_cc5 = (TextView)findViewById(R.id.textView_sb_cc5);
        textView_warning = (TextView)findViewById(R.id.textView_sb_warning);
        textView_hashtag_noresult1 = (TextView)findViewById(R.id.textView_hashtag_noresult1);
        textView_hashtag_noresult2 = (TextView)findViewById(R.id.textView_hashtag_noresult2);
        textView_hashtag_noresult3 = (TextView)findViewById(R.id.textView_hashtag_noresult3);
        textView_hashtag_noresult4 = (TextView)findViewById(R.id.textView_hashtag_noresult4);
        textView_hashtag_noresult5 = (TextView)findViewById(R.id.textView_hashtag_noresult5);

        rgCat1 = (RadioGroup)findViewById(R.id.radioGrup_cat1);
        rgCat1.setOnCheckedChangeListener(rg1);
        rgCat2 = (RadioGroup)findViewById(R.id.radioGrup_cat2);
        rgCat2.setOnCheckedChangeListener(rg2);
        rgCat3 = (RadioGroup)findViewById(R.id.radioGrup_cat3);
        rgCat3.setOnCheckedChangeListener(rg3);
        rgCat4 = (RadioGroup)findViewById(R.id.radioGrup_cat4);
        rgCat4.setOnCheckedChangeListener(rg4);
        rgCat5 = (RadioGroup)findViewById(R.id.radioGrup_cat5);
        rgCat5.setOnCheckedChangeListener(rg5);

        initData();
    }

    final View.OnClickListener cat1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDialogCategory(1);
        }
    };

    final View.OnClickListener cat2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDialogCategory(2);
        }
    };

    final View.OnClickListener cat3 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDialogCategory(3);
        }
    };

    final View.OnClickListener cat4 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDialogCategory(4);
        }
    };

    final View.OnClickListener cat5 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDialogCategory(5);
        }
    };

    final View.OnClickListener title0 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showHideContent(0);
        }
    };

    final View.OnClickListener title1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showHideContent(1);
        }
    };

    final View.OnClickListener title2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showHideContent(2);
        }
    };

    final View.OnClickListener title3 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showHideContent(3);
        }
    };

    final View.OnClickListener title4 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showHideContent(4);
        }
    };

    final View.OnClickListener title5 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showHideContent(5);
        }
    };

    final RadioGroup.OnCheckedChangeListener rg1 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            rbCat_1 = (RadioButton)findViewById(checkedId);
            settingRadioButton(rbCat_1, editText_cat1, 0);
        }
    };

    final RadioGroup.OnCheckedChangeListener rg2 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            rbCat_2 = (RadioButton)findViewById(checkedId);
            settingRadioButton(rbCat_2,editText_cat2,1);
        }
    };

    final RadioGroup.OnCheckedChangeListener rg3 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            rbCat_3 = (RadioButton)findViewById(checkedId);
            settingRadioButton(rbCat_3,editText_cat3,2);
        }
    };

    final RadioGroup.OnCheckedChangeListener rg4 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            rbCat_4 = (RadioButton)findViewById(checkedId);
            settingRadioButton(rbCat_4,editText_cat4,3);
        }
    };

    final RadioGroup.OnCheckedChangeListener rg5 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            rbCat_5 = (RadioButton)findViewById(checkedId);
            settingRadioButton(rbCat_5,editText_cat5,4);
        }
    };

    final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!editText_radius.getText().toString().isEmpty()){
                if (Double.parseDouble(editText_radius.getText().toString())<1){
                    editText_radius.setText(""+1);
                } else if (Double.parseDouble(editText_radius.getText().toString())>10){
                    editText_radius.setText(""+10);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    final TagsView.TagsListener tagListener = new TagsView.TagsListener(){
        @Override
        public void onTagsAdded(TagsView.Tags tags) {

        }

        @Override
        public void onTagsDeleted(TagsView.Tags tags) {

        }

        @Override
        public void onTextChanged(CharSequence text) {
            String teks = text.toString().toLowerCase();
            if (newDataAfterRemove != null){
                mMyAdapter.filterItems(teks);
                if (mMyAdapter.getItemCount() < 1) textView_hashtag_noresult1.setVisibility(View.VISIBLE);
                else textView_hashtag_noresult1.setVisibility(View.GONE);
                Log.e("disini awal", "removeTagListSize" + editText_subcat1.removeTagsList.size());
                if (editText_subcat1.removeTagsList.size() != 0) {
                    editText_subcat1.removeTagsList.get(0);
                    filteredList.add(editText_subcat1.removeTagsList.get(0).getHashTag().getHashTagName());
                    newDataAfterRemove = new String[filteredList.size()];
                    for (int i = 0; i < filteredList.size(); i++) {
                        newDataAfterRemove[i] = filteredList.get(i).toString();
                    }
                    editText_subcat1.removeTagsList.clear();
                    Log.e("disini akhir", "removeTagListSize" + editText_subcat1.removeTagsList.size());
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            dialogConfirmation("Saving Confirmation", "Do you want to save this setting?", 2);
            return super.onOptionsItemSelected(item);
        }

        if (id == R.id.action_logout) {
            logout_user();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initData(){
        settingRadioGrup();
        //getDataHashTag();
        getDataCategory();

        initFormHashtag();
    }

    public void initDataHashtag(){
        setVarToGetDataHashtag(0, dataHashtagName, editText_cat1);
        setVarToGetDataHashtag(1, dataHashtagName2, editText_cat2);
        setVarToGetDataHashtag(2, dataHashtagName3, editText_cat3);
        setVarToGetDataHashtag(3, dataHashtagName4, editText_cat4);
        setVarToGetDataHashtag(4, dataHashtagName5, editText_cat5);
    }

    public void initFormHashtag(){
        showHideFormHashTag(editText_cat1, ll_sbt_ht1);
        showHideFormHashTag(editText_cat2, ll_sbt_ht2);
        showHideFormHashTag(editText_cat3, ll_sbt_ht3);
        showHideFormHashTag(editText_cat4, ll_sbt_ht4);
        showHideFormHashTag(editText_cat5, ll_sbt_ht5);
    }

    public void showHideFormHashTag(EditText editTextcat,ViewGroup ll_sbt_ht){
        if (editTextcat.getText().toString().isEmpty())
            ll_sbt_ht.setVisibility(View.GONE);
        else
            ll_sbt_ht.setVisibility(View.VISIBLE);
    }

    public void showHideContent(int pos){
        if (showHideContent[pos]){
            switch (pos){
                case 0:
                    con_rad.setVisibility(View.GONE);
                    showHideContent[0]=false;
                    imageView_expand_rad.setImageResource(R.drawable.ic_expand_more_24dp);
                    break;
                case 1:
                    con_cat1.setVisibility(View.GONE);
                    showHideContent[1]=false;
                    imageView_expand_cat1.setImageResource(R.drawable.ic_expand_more_24dp);
                    break;
                case 2:
                    con_cat2.setVisibility(View.GONE);
                    showHideContent[2]=false;
                    imageView_expand_cat2.setImageResource(R.drawable.ic_expand_more_24dp);
                    break;
                case 3:
                    con_cat3.setVisibility(View.GONE);
                    showHideContent[3]=false;
                    imageView_expand_cat3.setImageResource(R.drawable.ic_expand_more_24dp);
                    break;
                case 4:
                    con_cat4.setVisibility(View.GONE);
                    showHideContent[4]=false;
                    imageView_expand_cat4.setImageResource(R.drawable.ic_expand_more_24dp);
                    break;
                case 5:
                    con_cat5.setVisibility(View.GONE);
                    showHideContent[5]=false;
                    imageView_expand_cat5.setImageResource(R.drawable.ic_expand_more_24dp);
                    break;
            }
        } else {
            switch (pos) {
                case 0:
                    con_rad.setVisibility(View.VISIBLE);
                    showHideContent[0] = true;
                    imageView_expand_rad.setImageResource(R.drawable.ic_expand_less_24dp);
                    break;
                case 1:
                    con_cat1.setVisibility(View.VISIBLE);
                    showHideContent[1] = true;
                    imageView_expand_cat1.setImageResource(R.drawable.ic_expand_less_24dp);
                    break;
                case 2:
                    con_cat2.setVisibility(View.VISIBLE);
                    showHideContent[2] = true;
                    imageView_expand_cat2.setImageResource(R.drawable.ic_expand_less_24dp);
                    break;
                case 3:
                    con_cat3.setVisibility(View.VISIBLE);
                    showHideContent[3] = true;
                    imageView_expand_cat3.setImageResource(R.drawable.ic_expand_less_24dp);
                    break;
                case 4:
                    con_cat4.setVisibility(View.VISIBLE);
                    showHideContent[4] = true;
                    imageView_expand_cat4.setImageResource(R.drawable.ic_expand_less_24dp);
                    break;
                case 5:
                    con_cat5.setVisibility(View.VISIBLE);
                    showHideContent[5] = true;
                    imageView_expand_cat5.setImageResource(R.drawable.ic_expand_less_24dp);
                    break;
            }
        }
    }

    public void showHideListHashtag(int pos,boolean show){
        switch (pos) {
            case 1:
                if (show)list_hashtag1.setVisibility(View.VISIBLE);
                else list_hashtag1.setVisibility(View.GONE);
                //setVarToGetDataHashtag(0,dataHashtagName,editText_cat1);//jangan pakai yang ini karena akan ambil data terus
                /*String typeHashtag = "";
                String catOrBrandNow = editText_cat1.getText().toString();
                if (tipeCategory[0]==1) {
                    for (int i = 0; i < nama_katagori.length; i++) {
                        if (catOrBrandNow.equalsIgnoreCase(nama_katagori[i])) {
                            typeHashtag = "" + id_kategori[i];
                        }
                    }
                    if (dataHashtagName==null || !typeHashtag.equalsIgnoreCase(""+id_kategori_select_user[0]))getDataHashTag(typeHashtag);
                } else {
                    if (dataHashtagName==null || tipeCategory[0]==2)getDataHashTag(typeHashtag);
                }*/
                break;
            case 2:
                if (show)list_hashtag2.setVisibility(View.VISIBLE);
                else list_hashtag2.setVisibility(View.GONE);
                setVarToGetDataHashtag(1,dataHashtagName2,editText_cat2);
                break;
            case 3:
                if (show)list_hashtag3.setVisibility(View.VISIBLE);
                else list_hashtag3.setVisibility(View.GONE);
                setVarToGetDataHashtag(2,dataHashtagName3,editText_cat3);
                break;
            case 4:
                if (show)list_hashtag4.setVisibility(View.VISIBLE);
                else list_hashtag4.setVisibility(View.GONE);
                setVarToGetDataHashtag(3,dataHashtagName4,editText_cat4);
                break;
            case 5:
                if (show)list_hashtag5.setVisibility(View.VISIBLE);
                else list_hashtag5.setVisibility(View.GONE);
                setVarToGetDataHashtag(4,dataHashtagName5,editText_cat5);
                break;
        }
    }

    public void setVarToGetDataHashtag(int indexTypeCategory,String[] data_HashtagName,EditText edittext){
        String typeHashtag = "";
        String catOrBrandNow = edittext.getText().toString();

        if (tipeCategory[indexTypeCategory]==1) {
            for (int i = 0; i < nama_katagori.length; i++) {
                if (catOrBrandNow.equalsIgnoreCase(nama_katagori[i])) {
                    typeHashtag = "" + id_kategori[i];
                }
            }
            if (data_HashtagName==null || !typeHashtag.equalsIgnoreCase(""+id_kategori_select_user[indexTypeCategory]))getDataHashTag(typeHashtag);
        } else {
            if (data_HashtagName==null || tipeCategory[indexTypeCategory]==2)getDataHashTag(typeHashtag);
        }
    }

    public void settingRadioButton(RadioButton radioButton, EditText editText, int pos){
        if (radioButton.getText().toString().equalsIgnoreCase("brand")){
            editText.setHint("Brand");
            tipeCategory[pos]=2;
        } else {
            editText.setHint("Category");
            tipeCategory[pos]=1;
        }
    }
    
    public void settingRadioGrup(){
        rbCat_1 = (RadioButton)findViewById(R.id.radioButton_cat1);
        rbBrand_1 = (RadioButton)findViewById(R.id.radioButton_brand1);
        rbCat_2 = (RadioButton)findViewById(R.id.radioButton_cat2);
        rbBrand_2 = (RadioButton)findViewById(R.id.radioButton_brand2);
        rbCat_3 = (RadioButton)findViewById(R.id.radioButton_cat3);
        rbBrand_3 = (RadioButton)findViewById(R.id.radioButton_brand3);
        rbCat_4 = (RadioButton)findViewById(R.id.radioButton_cat4);
        rbBrand_4 = (RadioButton)findViewById(R.id.radioButton_brand4);
        rbCat_5 = (RadioButton)findViewById(R.id.radioButton_cat5);
        rbBrand_5 = (RadioButton)findViewById(R.id.radioButton_brand5);
        int posRg=0;

        for (int i = 0; i < tipeCategory.length ; i++) {
            posRg=i+1;
            switch (posRg){
                case 1:
                    settingRadioGrupContent(rbCat_1,rbBrand_1,editText_cat1,tipeCategory[i]);
                    break;
                case 2:
                    settingRadioGrupContent(rbCat_2,rbBrand_2,editText_cat2,tipeCategory[i]);
                    break;
                case 3:
                    settingRadioGrupContent(rbCat_3,rbBrand_3,editText_cat3,tipeCategory[i]);
                    break;
                case 4:
                    settingRadioGrupContent(rbCat_4,rbBrand_4,editText_cat4,tipeCategory[i]);
                    break;
                case 5:
                    settingRadioGrupContent(rbCat_5,rbBrand_5,editText_cat5,tipeCategory[i]);
                    break;
            }
        }
    }

    public void settingRadioGrupContent(RadioButton rb1,RadioButton rb2,EditText et,int type){
        if (type==1){
            rb1.setChecked(true);
            rb2.setChecked(false);
            et.setHint("Category");
        } else if (type==2){
            rb1.setChecked(false);
            rb2.setChecked(true);
            et.setHint("Brand");
        }
    }

    public void getDataCategory() {
        CallWebPageTask task = new CallWebPageTask();
        task.applicationContext = SettingCategoryBubleActivity.this;
        String urls =url;
        Log.e("Sukses", urls);
        task.execute(new String[]{urls});
    }

    public void getDataHashTag(String typeHashtag) {
        CallWebPageTaskHashtag task = new CallWebPageTaskHashtag();
        task.applicationContext = SettingCategoryBubleActivity.this;
        //url = url+"?typeHashtag"+typeHashtag;
        String urls =url;
        Log.e("Sukses", urls);
        task.execute(new String[]{urls});
    }

    @Override
    public void onBackPressed() {
        dialogConfirmation("Saving Confirmation", "Do you want to save this setting?", 2);
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
                        Intent logout_user_fb = new Intent(SettingCategoryBubleActivity.this, FormLoginActivity.class);
                        startActivity(logout_user_fb);
                        finish();
                    }
                })
                .setNegativeButton(cancel, null);
        builder.create().show();
    }

    public void back_to_previous_screen(){
        Intent intent = new Intent(SettingCategoryBubleActivity.this,MainMenuActivity.class);
        startActivity(intent);
        finish();
    }



    /**
     * Class CallWebPageTask untuk implementasi class AscyncTask
     */
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
            try {
                //simpan data dari web ke dalam array
                JSONArray menuItemArray = null;
                jObject = new JSONObject(response);
                menuItemArray = jObject.getJSONArray("dheket_allCat");
                id_kategori = new int[menuItemArray.length()];
                nama_katagori = new String[menuItemArray.length()];

                for (int i = 0; i < menuItemArray.length(); i++) {
                    id_kategori[i] = menuItemArray.getJSONObject(i).getInt("id_category");
                    nama_katagori[i] = menuItemArray.getJSONObject(i).getString("category_name").toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            //this.dialog.cancel();
            Log.e("get data complete"," 1 ");
            if (nama_katagori==null){
                Log.e("get data complete"," 2 ");
                getDataCategory();
            }
            else {
                initDataHashtag();
                initFormHashtag();
            }
        }
    }

    public void checkCategoryIsUsed(int pos,String text){
        boolean sameCategory = false;
        int index = 9999, samePosAt=0;
        for (int i = 0; i < nama_katagori.length; i++) {
            if (text.equalsIgnoreCase(nama_katagori[i])) {
                index = id_kategori[i];
                i = nama_katagori.length;
            }
        }
        for (int j = 0; j < id_kategori_select_user.length; j++) {
            if ((pos-1)!=j) {
                if (index == id_kategori_select_user[j]) {
                    sameCategory = true;
                    samePosAt = j + 1;
                }
            }
        }
        if (sameCategory) {
            dialogConfirmation("Warning", "This Category has used by Bubble " +samePosAt+"! Please choose other!",1);
        } else {
            id_kategori_select_user[pos - 1] = index;
            if (pos == 1) {
                editText_cat1.setText(text);
            } else if (pos == 2) {
                editText_cat2.setText(text);
            } else if (pos == 3) {
                editText_cat3.setText(text);
            } else if (pos == 4) {
                editText_cat4.setText(text);
            } else {
                editText_cat5.setText(text);
            }
        }
    }

    public void showDialogCategory(final int pos){
        LayoutInflater mInflater = LayoutInflater.from(this);
        View v = mInflater.inflate(R.layout.dialog_select_category, null);

        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(v);
        dialog.setCancelable(true);

        ListView lv;
        // ArrayList for Listview
        ArrayList<HashMap<String, String>> categoryList;

        final ListView listView_tag;
        final Button btn_cancel, btn_ok;
        final ImageButton btn_refresh;
        final TextView textView_tags, textView_titleDialog;
        final EditText editText_inputSearch;
        final ViewGroup buttonView, selectTagView, frameLayout_listcat, linearLayout_btn_ref_liscat;
        final ImageButton imageButton_clear;

        lastPosition = pos;
        listView_tag = (ListView)v.findViewById(R.id.listView_dialog_select_tag);
        btn_cancel = (Button)v.findViewById(R.id.button_dialog_tag_cancel);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)btn_cancel.getLayoutParams();
        //ViewGroup.LayoutParams params = btn_cancel.getLayoutParams();
        final float scale = getResources().getDisplayMetrics().density;
        //params.width=((int)(120 * scale + 0.5f));
        params.setMargins((int) (100 * scale + 0.5f), (int) (5 * scale + 0.5f), (int) (100 * scale + 0.5f), (int) (5 * scale + 0.5f));
        btn_cancel.setLayoutParams(params);

        btn_ok = (Button)v.findViewById(R.id.button_dialog_tag_ok);
        btn_ok.setVisibility(View.GONE);
        btn_refresh = (ImageButton)v.findViewById(R.id.imageButton_ref_listcat);
        imageButton_clear = (ImageButton)v.findViewById(R.id.imageButton_dialog_clear);
        imageButton_clear.setVisibility(View.GONE);
        textView_tags = (TextView)v.findViewById(R.id.textView_dialog_show_select_tag);
        textView_titleDialog = (TextView)v.findViewById(R.id.textView_dialog_tag);
        editText_inputSearch = (EditText)v.findViewById(R.id.editText_dialog_select_search);
        /*buttonView = (ViewGroup)v.findViewById(R.id.button_dialog_tag_cancel_ok);
        buttonView.setVisibility(View.GONE);*/
        selectTagView = (ViewGroup)v.findViewById(R.id.text_dialog_item_tag);
        selectTagView.setVisibility(View.GONE);
        frameLayout_listcat = (ViewGroup)v.findViewById(R.id.frameLayout_listCat);
        linearLayout_btn_ref_liscat = (ViewGroup)v.findViewById(R.id.linearLayout_btn_ref_listcat);

        textView_titleDialog.setText("Category");

        //adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice, nama_katagori);
        if (nama_katagori!=null){
            adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.textView_set_categorys, nama_katagori);
            listView_tag.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView_tag.setAdapter(adapter);
        } else {
            frameLayout_listcat.setVisibility(View.GONE);
            linearLayout_btn_ref_liscat.setVisibility(View.VISIBLE);
        }

        /**
         * Enabling Search Filter
         * */
        editText_inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                if (nama_katagori!=null)SettingCategoryBubleActivity.this.adapter.getFilter().filter(cs);

                if (editText_inputSearch.getText().length() > 0) {
                    imageButton_clear.setVisibility(View.VISIBLE);
                } else {
                    imageButton_clear.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        listView_tag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView txt = (TextView) view.findViewById(R.id.textView_set_categorys);
                Log.e("item select", "parent:" + parent + "\nview:" + view + "\nposisi:" + position + "\nid:" + id + "\ntxt:" + txt.getText());
                checkCategoryIsUsed(pos, txt.getText().toString());
                dialog.dismiss();
            }
        });

        imageButton_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_inputSearch.setText("");
                imageButton_clear.setVisibility(View.GONE);
            }
        });

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataCategory();
                dialog.dismiss();
                showDialogCategory(pos);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private class CallWebPageTaskHashtag extends AsyncTask<String, Void, String> {

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
            try {
                //simpan data dari web ke dalam array
                JSONArray menuItemArray = null;
                jObject = new JSONObject(response);
                menuItemArray = jObject.getJSONArray("dheket_allCat");
                dataHashtagId = new String[menuItemArray.length()];
                dataHashtagName = new String[menuItemArray.length()];

                for (int i = 0; i < menuItemArray.length(); i++) {
                    dataHashtagId[i] = menuItemArray.getJSONObject(i).getString("id_category");
                    dataHashtagName[i] = menuItemArray.getJSONObject(i).getString("category_name").toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            //this.dialog.cancel();
            if (dataHashtagName==null)setVarToGetDataHashtag(0,dataHashtagName,editText_cat1);
            else{
                mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_hashtag_listag1);
                //mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                mRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
                mMyAdapter = new MyAdapter();
                newDataAfterRemove = dataHashtagName;
                Collections.addAll(filteredList, dataHashtagName);
                mRecyclerView.setAdapter(mMyAdapter);

            }
        }
    }

    public void getDataHashtag(){

    }

    public void setRecycleViewAttribute(MyAdapter mMyAdapterX, String[] newDataAfterRemoveX,String[]
                                        dataHashtagNameX,List<String> filteredListX,RecyclerView mRecyclerViewX){

    }

    public void showDialogSubCategory(final int pos){
        LayoutInflater mInflater = LayoutInflater.from(this);
        View v = mInflater.inflate(R.layout.dialog_select_hashtag, null);

        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(v);
        dialog.setCancelable(true);

        RecyclerView mRecyclerView;
        // ArrayList for Listview

        final ListView listView_tag;
        final Button btn_cancel, btn_ok;
        final TextView textView_tags;
        final EditText editText_inputSearch;

        lastPosition = pos;
        btn_cancel = (Button)v.findViewById(R.id.button_ditag_cancel);
        btn_ok = (Button)v.findViewById(R.id.button_ditag_ok);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();
    }

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
                    //Uri imgUrl = Math.random() > .7d ? null : Uri.parse("https://robohash.org/" + Math.abs(email.hashCode()));
                    Uri imgUrl = null;
                    HashTag hashTag = new HashTag("", "", email, imgUrl);
                    editText_subcat1.addTags(email, imgUrl, hashTag);
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

    public void dialogConfirmation(String title,String message, final int typeOk){
        LayoutInflater mInflater = LayoutInflater.from(this);
        View v = mInflater.inflate(R.layout.dialog_confirmation, null);

        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(v);
        dialog.setCancelable(true);

        final Button btn_cancel, btn_ok;
        final TextView textView_message,textView_title;

        btn_cancel = (Button)v.findViewById(R.id.button_dico_cancel);
        btn_ok = (Button)v.findViewById(R.id.button_dico_ok);
        textView_message = (TextView)v.findViewById(R.id.textView_dico_message);
        textView_title = (TextView)v.findViewById(R.id.textView_dico_title);
        textView_message.setText(message);
        textView_title.setText(title);

        if (typeOk==2){
            btn_cancel.setText("No");
            btn_ok.setText("Yes");
        }

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (typeOk==2)back_to_previous_screen();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeOk == 1) {
                    showDialogCategory(lastPosition);
                } else if (typeOk == 2) {
                    if (editText_radius.getText().toString().isEmpty()) {
                        radius = 1;
                    } else {
                        radius = Double.parseDouble(editText_radius.getText().toString());
                    }
                    if (checkDataValid()){
                        textView_warning.setVisibility(View.GONE);
                        back_to_previous_screen();
                    } {
                        textView_warning.setVisibility(View.VISIBLE);
                    }
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public boolean checkDataValid(){
        boolean valid = true;
        if (editText_radius.getText().toString().isEmpty())editText_radius.setText(""+1);

        if (editText_cat1.getText().toString().isEmpty()){
            textView_sb_cc1.setTextColor(Color.RED);
            valid = false;
            hasValid[1] = false;
        } else {
            textView_sb_cc1.setTextColor(Color.parseColor("#8C8C8C"));
            hasValid[1] = true;
        }

        if (editText_subcat1.getListData().size()<5){
            textView_sb_ct1.setTextColor(Color.RED);
            valid = false;
            hasValid[1] = false;
        } else {
            textView_sb_ct1.setTextColor(Color.parseColor("#8C8C8C"));
            hasValid[1] = true;
        }

        if (editText_cat2.getText().toString().isEmpty()){
            textView_sb_cc2.setTextColor(Color.RED);
            valid = false;
            hasValid[2] = false;
        } else {
            textView_sb_cc2.setTextColor(Color.parseColor("#8C8C8C"));
            hasValid[2] = true;
        }

        if (editText_subcat2.getListData().size()<5){
            textView_sb_ct2.setTextColor(Color.RED);
            valid = false;
            hasValid[2] = false;
        } else {
            textView_sb_ct2.setTextColor(Color.parseColor("#8C8C8C"));
            hasValid[2] = true;
        }

        if (editText_cat3.getText().toString().isEmpty()){
            textView_sb_cc3.setTextColor(Color.RED);
            valid = false;
            hasValid[3] = false;
        } else {
            textView_sb_cc3.setTextColor(Color.parseColor("#8C8C8C"));
            hasValid[3] = true;
        }

        if (editText_subcat3.getListData().size()<5){
            textView_sb_ct3.setTextColor(Color.RED);
            valid = false;
            hasValid[3] = false;
        } else {
            textView_sb_ct3.setTextColor(Color.parseColor("#8C8C8C"));
            hasValid[3] = true;
        }

        if (editText_cat4.getText().toString().isEmpty()){
            textView_sb_cc4.setTextColor(Color.RED);
            valid = false;
            hasValid[4] = false;
        } else {
            textView_sb_cc4.setTextColor(Color.parseColor("#8C8C8C"));
            hasValid[4] = true;
        }

        if (editText_subcat4.getListData().size()<5){
            textView_sb_ct4.setTextColor(Color.RED);
            valid = false;
            hasValid[4] = false;
        } else {
            textView_sb_ct4.setTextColor(Color.parseColor("#8C8C8C"));
            hasValid[4] = true;
        }

        if (editText_cat5.getText().toString().isEmpty()){
            textView_sb_cc5.setTextColor(Color.RED);
            valid = false;
            hasValid[5] = false;
        } else {
            textView_sb_cc5.setTextColor(Color.parseColor("#8C8C8C"));
            hasValid[5] = true;
        }

        if (editText_subcat5.getListData().size()<5){
            textView_sb_ct5.setTextColor(Color.RED);
            valid = false;
            hasValid[5] = false;
        } else {
            textView_sb_ct5.setTextColor(Color.parseColor("#8C8C8C"));
            hasValid[5] = true;
        }

        for (int i = 0; i < hasValid.length ; i++) {
            if (!hasValid[i]){
                showHideContent[i]=false;
                showHideContent(i);
            } else {
                showHideContent[i]=true;
                showHideContent(i);
            }
        }

        return valid;
    }
}
