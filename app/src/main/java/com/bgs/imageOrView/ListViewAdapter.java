package com.bgs.imageOrView;

/**
 * Created by ade on 01-Feb-16.
 */
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bgs.dheket.R;
import com.bgs.dheket.SearchAllCategoryActivity;
import com.bgs.dheket.SearchViewActivity;


public class ListViewAdapter extends BaseAdapter {

    // Declare Variables
    Context context;
    LayoutInflater inflater;
    ArrayList<HashMap<String, String>> data;

    HashMap<String, String> resultp = new HashMap<String, String>();

    public ListViewAdapter(Context context,
                           ArrayList<HashMap<String, String>> arraylist) {
        this.context = context;
        data = arraylist;

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, final View convertView, ViewGroup parent) {
        // Declare Variables
        TextView id_category; //rank
        TextView category_name; //country
        TextView category_id; //population


        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View itemView = inflater.inflate(R.layout.searchview, parent, false);
        // Get the position
        resultp = data.get(position);

        // Locate the TextViews in listview_item.xml
        id_category = (TextView) itemView.findViewById(R.id.id_category); //rank
        category_name = (TextView) itemView.findViewById(R.id.category_name); //country
        category_id = (TextView) itemView.findViewById(R.id.category_id); //population

        // Capture position and set results to the TextViews
        id_category.setText(resultp.get(SearchAllCategoryActivity.id_category)); //RANK
        category_name.setText(resultp.get(SearchAllCategoryActivity.category_name)); //COUNTRY
        category_id.setText(resultp.get(SearchAllCategoryActivity.category_id)); //POPULATION
        // Capture ListView item click
        itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get the position
                resultp = data.get(position);
                Intent intent = new Intent(context, SearchViewActivity.class);
                // Pass all data id_category
                intent.putExtra("", resultp.get(SearchAllCategoryActivity.id_category)); //RANK
                // Pass all data category_name
                intent.putExtra("category_name", resultp.get(SearchAllCategoryActivity.category_name)); //COUNTRY
                // Pass all data category_id
                intent.putExtra("", resultp.get(SearchAllCategoryActivity.category_id)); //POPULATION
                // Start SingleItemView Class
                context.startActivity(intent);
                ((Activity)context).finish();
                //context.finish();
            }
        });
        return itemView;
    }

}
