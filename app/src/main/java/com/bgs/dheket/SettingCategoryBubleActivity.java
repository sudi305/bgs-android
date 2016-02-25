package com.bgs.dheket;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bgs.networkAndSensor.HttpGetOrPost;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SND on 23/02/2016.
 */
public class SettingCategoryBubleActivity extends AppCompatActivity {
    android.support.v7.app.ActionBar actionBar;

    EditText editText_cat1, editText_cat2, editText_cat3, editText_cat4, editText_cat5;
    EditText editText_subcat1, editText_subcat2, editText_subcat3, editText_subcat4, editText_subcat5;
    Button button_save;

    String url = "http://dheket.esy.es/getAllCategory.php";
    private JSONObject jObject;
    String []nama_katagori;
    int []id_kategori;
    int []id_subCat;
    int []temp_id_cat = new int[1];
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_category_buble);
        actionBar = getSupportActionBar();

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeAsUpIndicator(R.drawable.logo);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Dheket");
//        actionBar.setSubtitle(Html.fromHtml("<font color='#FFBF00'>Location in Radius " + formatter.format(radius) + " Km</font>"));
        actionBar.setSubtitle(Html.fromHtml("<font color='#ff9800'>Setting category bubble</font>"));

        editText_cat1 = (EditText)findViewById(R.id.editText_set_cat1);
        editText_cat1.setOnClickListener(tag1);
        editText_cat2 = (EditText)findViewById(R.id.editText_set_cat2);
        editText_cat2.setOnClickListener(tag2);
        editText_cat3 = (EditText)findViewById(R.id.editText_set_cat3);
        editText_cat3.setOnClickListener(tag3);
        editText_cat4 = (EditText)findViewById(R.id.editText_set_cat4);
        editText_cat4.setOnClickListener(tag4);
        editText_cat5 = (EditText)findViewById(R.id.editText_set_cat5);
        editText_cat5.setOnClickListener(tag5);
        editText_subcat1 = (EditText)findViewById(R.id.editText_set_subcat1);
        editText_subcat2 = (EditText)findViewById(R.id.editText_set_subcat2);
        editText_subcat3 = (EditText)findViewById(R.id.editText_set_subcat3);
        editText_subcat4 = (EditText)findViewById(R.id.editText_set_subcat4);
        editText_subcat5 = (EditText)findViewById(R.id.editText_set_subcat5);
        button_save = (Button)findViewById(R.id.btn_set_save);
        
        getDataCategory();
    }

    final View.OnClickListener tag1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDialogCategory(1);
        }
    };

    final View.OnClickListener tag2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDialogCategory(2);
        }
    };

    final View.OnClickListener tag3 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDialogCategory(3);
        }
    };

    final View.OnClickListener tag4 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDialogCategory(4);
        }
    };

    final View.OnClickListener tag5 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDialogCategory(5);
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

    public void getDataCategory() {
        CallWebPageTask task = new CallWebPageTask();
        task.applicationContext = SettingCategoryBubleActivity.this;
        String urls =url;
        Log.e("Sukses", urls);
        task.execute(new String[]{urls});
    }

    @Override
    public void onBackPressed() {
        back_to_previous_screen();
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

        }
    }

    public void showDialogCategory(final int pos){
        LayoutInflater mInflater = LayoutInflater.from(this);
        View v = mInflater.inflate(R.layout.dialog_select_subcategory_or_tag, null);

        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(v);
        dialog.setCancelable(true);

        ListView lv;
        // ArrayList for Listview
        ArrayList<HashMap<String, String>> categoryList;

        final ListView listView_tag;
        final Button btn_cancel, btn_ok;
        final TextView textView_tags, textView_titleDialog;
        final EditText editText_inputSearch;
        final ViewGroup buttonView, selectTagView;
        final ImageButton imageButton_clear;

        listView_tag = (ListView)v.findViewById(R.id.listView_dialog_select_tag);
        btn_cancel = (Button)v.findViewById(R.id.button_dialog_tag_cancel);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)btn_cancel.getLayoutParams();
        //ViewGroup.LayoutParams params = btn_cancel.getLayoutParams();
        final float scale = getResources().getDisplayMetrics().density;
        //params.width=((int)(120 * scale + 0.5f));
        params.setMargins((int)(100 * scale + 0.5f), (int)(5 * scale + 0.5f),(int)(100 * scale + 0.5f), (int)(5 * scale + 0.5f));
        btn_cancel.setLayoutParams(params);

        btn_ok = (Button)v.findViewById(R.id.button_dialog_tag_ok);
        btn_ok.setVisibility(View.GONE);
        imageButton_clear = (ImageButton)v.findViewById(R.id.imageButton_dialog_clear);
        imageButton_clear.setVisibility(View.GONE);
        textView_tags = (TextView)v.findViewById(R.id.textView_dialog_show_select_tag);
        textView_titleDialog = (TextView)v.findViewById(R.id.textView_dialog_tag);
        editText_inputSearch = (EditText)v.findViewById(R.id.editText_dialog_select_search);
        /*buttonView = (ViewGroup)v.findViewById(R.id.button_dialog_tag_cancel_ok);
        buttonView.setVisibility(View.GONE);*/
        selectTagView = (ViewGroup)v.findViewById(R.id.text_dialog_item_tag);
        selectTagView.setVisibility(View.GONE);

        textView_titleDialog.setText("Category");

        //adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice, nama_katagori);
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.textView_set_categorys, nama_katagori);
        listView_tag.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView_tag.setAdapter(adapter);

        /**
         * Enabling Search Filter
         * */
        editText_inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                SettingCategoryBubleActivity.this.adapter.getFilter().filter(cs);
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
                TextView txt = (TextView)view.findViewById(R.id.textView_set_categorys);
                Log.e("item select","parent:"+parent+"\nview:"+view+"\nposisi:"+position+"\nid:"+id+"\ntxt:"+txt.getText());
                if (pos==1){
                    editText_cat1.setText(""+txt.getText());
                } else if (pos==2){
                    editText_cat2.setText(""+txt.getText());
                } else if (pos==3){
                    editText_cat3.setText(""+txt.getText());
                }else if (pos==4){
                    editText_cat4.setText(""+txt.getText());
                } else {
                    editText_cat5.setText(""+txt.getText());
                }
                dialog.dismiss();
            }
        });

        /*if (id_subCat!=null){
            for (int i = 0; i < id_subCat.length ; i++) {
                listView_tag.setItemChecked(id_subCat[i],true);
            }
        }*/

        imageButton_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_inputSearch.setText("");
                imageButton_clear.setVisibility(View.GONE);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        /*btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = listView_tag.getCheckedItemPositions();
                ArrayList<String> selectedItems = new ArrayList<String>();
                String tag = "";
                id_subCat = new int[checked.size()];
                for (int i = 0; i < checked.size(); i++) {
                    // Item position in adapter
                    int position = checked.keyAt(i);
                    // Add sport if it is checked i.e.) == TRUE!
                    if (checked.valueAt(i)) {
                        selectedItems.add(adapter.getItem(position));
                        id_subCat[i] = checked.keyAt(i);
                    }
                }

                String[] outputStrArr = new String[selectedItems.size()];
                for (int i = 0; i < selectedItems.size(); i++) {
                    tag = tag + selectedItems.get(i)+" ";
                }

                //editText_loc_subCat.setText(tag);

                dialog.dismiss();
            }
        });*/

        dialog.show();
    }

    public void showDialogSubCategory(){
        LayoutInflater mInflater = LayoutInflater.from(this);
        View v = mInflater.inflate(R.layout.dialog_select_subcategory_or_tag, null);

        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(v);
        dialog.setCancelable(true);

        ListView lv;
        // ArrayList for Listview
        ArrayList<HashMap<String, String>> categoryList;

        final ListView listView_tag;
        final Button btn_cancel, btn_ok;

        final TextView textView_tags;
        final EditText editText_inputSearch;

        listView_tag = (ListView)v.findViewById(R.id.listView_dialog_select_tag);
        btn_cancel = (Button)v.findViewById(R.id.button_dialog_tag_cancel);
        btn_ok = (Button)v.findViewById(R.id.button_dialog_tag_ok);
        textView_tags = (TextView)v.findViewById(R.id.textView_dialog_show_select_tag);
        editText_inputSearch = (EditText)v.findViewById(R.id.editText_dialog_select_search);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice, nama_katagori);
        //adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.textView_set_categorys, nama_katagori);
        listView_tag.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView_tag.setAdapter(adapter);

        /**
         * Enabling Search Filter
         * */
        editText_inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                SettingCategoryBubleActivity.this.adapter.getFilter().filter(cs);
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

        if (id_subCat!=null){
            for (int i = 0; i < id_subCat.length ; i++) {
                listView_tag.setItemChecked(id_subCat[i],true);
            }
        }

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = listView_tag.getCheckedItemPositions();
                ArrayList<String> selectedItems = new ArrayList<String>();
                String tag = "";
                id_subCat = new int[checked.size()];
                for (int i = 0; i < checked.size(); i++) {
                    // Item position in adapter
                    int position = checked.keyAt(i);
                    // Add sport if it is checked i.e.) == TRUE!
                    if (checked.valueAt(i)) {
                        selectedItems.add(adapter.getItem(position));
                        id_subCat[i] = checked.keyAt(i);
                    }
                }

                String[] outputStrArr = new String[selectedItems.size()];
                for (int i = 0; i < selectedItems.size(); i++) {
                    tag = tag + selectedItems.get(i)+" ";
                }

                //editText_loc_subCat.setText(tag);

                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
